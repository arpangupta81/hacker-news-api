package com.hackernews.api.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.google.common.collect.ImmutableList;
import com.hackernews.api.cache.HackerNewsRedisStore;
import com.hackernews.api.client.HackerNewsApiClient;
import com.hackernews.api.model.client.CommentDetails;
import com.hackernews.api.model.client.StoryDetails;
import com.hackernews.api.model.repo.StoryRepository;
import com.hackernews.api.model.ui.CommentDetailsUi;
import com.hackernews.api.model.ui.StoryDetailsUi;
import com.hackernews.api.model.ui.UserDetails;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HackerNewsDetailsServiceTest {

  private static final StoryDetailsUi STORY_DETAILS_UI = StoryDetailsUi.builder()
      .timeOfSubmission(Instant.EPOCH).build();
  private static final String COMMENT_TEXT = "TEXT";
  private static final StoryDetails STORY_DETAILS = StoryDetails.builder()
      .kids(Collections.singletonList(122L))
      .type("STORY")
      .build();
  private static final int STORY_ID = 12;
  private static final CommentDetailsUi COMMENT_DETAILS_UIS = CommentDetailsUi.builder()
      .authorActiveTime(0)
      .commentId(12L)
      .authorId("USER_ID")
      .commentText(COMMENT_TEXT)
      .build();
  private static final String USER_ID = "USER_ID";
  private static final CommentDetails COMMENT_DETAILS_API_121 = CommentDetails.builder()
      .by(USER_ID)
      .text(COMMENT_TEXT)
      .id(121L)
      .build();
  private static final CommentDetailsUi COMMENT_DETAILS_121 = CommentDetailsUi.builder()
      .commentId(121)
      .authorId(USER_ID)
      .commentText(COMMENT_TEXT)
      .build();
  private static final CommentDetailsUi COMMENT_DETAILS_122 = CommentDetailsUi.builder()
      .commentId(122)
      .authorId(USER_ID)
      .commentText(COMMENT_TEXT)
      .build();
  private static final CommentDetails COMMENT_DETAILS = CommentDetails.builder()
      .text(COMMENT_TEXT)
      .id(122L)
      .by(USER_ID)
      .kids(Collections.singletonList(12L))
      .build();
  private static final UserDetails USER_DETAILS = UserDetails.builder()
      .created(Instant.now().getEpochSecond())
      .build();
  @Mock
  private HackerNewsApiClient hackerNewsApiClient;
  @Mock
  private StoryRepository pastStoriesRepository;
  @Mock
  private HackerNewsRedisStore hackerNewsRedisStore;
  private HackerNewsDetailsService hackerNewsDetailsService;

  @Before
  public void setup() {
    hackerNewsDetailsService = new HackerNewsDetailsService(hackerNewsApiClient,
                                                            pastStoriesRepository,
                                                            hackerNewsRedisStore,
                                                            10,
                                                            10);
  }

  @Test
  public void getAllParentCommentIdsToNumberOfChildrenFromRedis() {
    when(hackerNewsRedisStore.fetchTopCommentsFromRedis(STORY_ID))
        .thenReturn(Collections.singletonList(COMMENT_DETAILS_UIS));
    assertThat(hackerNewsDetailsService.getAllParentCommentIdsToNumberOfChildren(STORY_ID))
        .isEqualTo(Collections.singletonList(COMMENT_DETAILS_UIS));
    verify(hackerNewsApiClient, times(0)).storyDetails(STORY_ID);
  }

  @Test
  public void getAllParentCommentIdsToNumberOfChildrenFromApi() {
    when(hackerNewsRedisStore.fetchTopCommentsFromRedis(STORY_ID)).thenReturn(Collections.emptyList());
    when(hackerNewsApiClient.storyDetails(STORY_ID)).thenReturn(STORY_DETAILS);
    when(hackerNewsApiClient.commentDetails(122L)).thenReturn(COMMENT_DETAILS);
    when(hackerNewsApiClient.commentDetails(12L)).thenReturn(CommentDetails.builder().build());
    when(hackerNewsApiClient.userDetails(USER_ID)).thenReturn(USER_DETAILS);
    assertThat(hackerNewsDetailsService.getAllParentCommentIdsToNumberOfChildren(STORY_ID))
        .isEqualTo(Collections.singletonList(COMMENT_DETAILS_122));
    verify(hackerNewsApiClient).storyDetails(STORY_ID);
  }

  @Test
  public void getAllParentCommentIdsSortingLogic() {
    when(hackerNewsRedisStore.fetchTopCommentsFromRedis(124L)).thenReturn(Collections.emptyList());
    when(hackerNewsApiClient.storyDetails(124L)).thenReturn(StoryDetails.builder()
                                                                .kids(ImmutableList.of(122L, 121L))
                                                                .build());
    when(hackerNewsApiClient.commentDetails(122L)).thenReturn(COMMENT_DETAILS);
    when(hackerNewsApiClient.commentDetails(121L)).thenReturn(COMMENT_DETAILS_API_121);
    when(hackerNewsApiClient.commentDetails(12L)).thenReturn(CommentDetails.builder().build());
    when(hackerNewsApiClient.userDetails(USER_ID)).thenReturn(USER_DETAILS);
    List<CommentDetailsUi> expectedOutPutInOrder = new ArrayList<>();
    expectedOutPutInOrder.add(COMMENT_DETAILS_122);
    expectedOutPutInOrder.add(COMMENT_DETAILS_121);
    assertThat(hackerNewsDetailsService.getAllParentCommentIdsToNumberOfChildren(124L))
        .isEqualTo(expectedOutPutInOrder);
  }

  @Test
  public void topStoriesWithSortingLogicFromRedis() {
    when(hackerNewsRedisStore.fetchTopStoriesFromRedis())
        .thenReturn(Collections.singletonList(STORY_DETAILS));
    assertThat(hackerNewsDetailsService.topStoriesWithSortingLogic())
        .isEqualTo(Collections.singletonList(STORY_DETAILS_UI));
  }

  @Test
  public void topStoriesWithSortingLogicFromApi() {
    when(hackerNewsRedisStore.fetchTopStoriesFromRedis()).thenReturn(Collections.emptyList());
    when(hackerNewsApiClient.topStories()).thenReturn(Collections.singletonList(123L));
    when(hackerNewsApiClient.storyDetails(123L)).thenReturn(STORY_DETAILS);
    assertThat(hackerNewsDetailsService.topStoriesWithSortingLogic())
        .isEqualTo(Collections.singletonList(STORY_DETAILS_UI));
  }

  @Test
  public void getPastStoriesServed() {
    when(pastStoriesRepository.findAll()).thenReturn(Collections.singletonList(STORY_DETAILS_UI));
    assertThat(hackerNewsDetailsService.getPastStoriesServed())
        .isSortedAccordingTo(Comparator.comparing(StoryDetailsUi::getScore));
    assertThat(hackerNewsDetailsService.getPastStoriesServed())
        .isEqualTo(Collections.singletonList(STORY_DETAILS_UI));
  }
}