package com.hackernews.api.services;

import static com.hackernews.api.TestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.hackernews.api.cache.HackerNewsDetailsCache;
import com.hackernews.api.client.HackerNewsApiClient;
import com.hackernews.api.model.client.CommentDetails;
import com.hackernews.api.model.client.StoryDetails;
import com.hackernews.api.model.client.UserDetails;
import com.hackernews.api.model.ui.CommentDetailsUi;
import com.hackernews.api.model.ui.StoryDetailsUi;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HackerNewsServiceTest {

  private static final int ONE = 1;
  private static final int TWO = 2;
  private static final int TEN = 10;
  private static final int THREE = 3;
  private static final int TWENTY = 20;
  private static final long ID1 = 1L;
  private static final long ID = 123L;
  private static final String AUTHOR = "author";
  private static final long EPOCH_SECOND = Instant.EPOCH.getEpochSecond();
  @Mock
  private HackerNewsDetailsCache hackerNewsDetailsCache;
  @Mock
  private HackerNewsApiClient hackerNewsApiClient;
  private HackerNewsService hackerNewsService;

  @Before
  public void setUp() {
    hackerNewsService = new HackerNewsService(hackerNewsDetailsCache,
                                              hackerNewsApiClient,
                                              TEN,
                                              TEN);
  }

  @Test
  public void test_topStories_SortingLogic_PastServedAddition() {
    List<StoryDetailsUi> expectedOutput = ImmutableList.of(getStoryDetailsUi(TWO, TWENTY),
                                                           getStoryDetailsUi(ONE, TEN),
                                                           getStoryDetailsUi(THREE, ONE));
    when(hackerNewsDetailsCache.topStoriesFromCache()).thenReturn(INPUT);
    assertThat(hackerNewsService.topStoriesWithSortingLogic())
        .containsExactlyElementsOf(expectedOutput);
    verify(hackerNewsDetailsCache).putServedItemsToCache(expectedOutput);
  }

  @Test
  public void test_topComments_SortingLogic_PastServedAddition() {
    when(hackerNewsApiClient.storyDetails(ID)).thenReturn(StoryDetails.builder()
                                                              .kids(Collections.singletonList(ID1))
                                                              .build());
    when(hackerNewsDetailsCache.getAllParentCommentIdsToNumberOfChildren(Collections
                                                                             .singletonList(ID1)))
        .thenReturn(Collections.singletonMap(ID1, ONE));
    when(hackerNewsApiClient.commentDetails(ID1)).thenReturn(CommentDetails.builder()
                                                                 .by(AUTHOR)
                                                                 .build());
    when(hackerNewsApiClient.userDetails(AUTHOR)).thenReturn(UserDetails.builder()
                                                                 .created(EPOCH_SECOND)
                                                                 .build());
    int years = Period.between(Instant.EPOCH.atZone(ZoneId.systemDefault()).toLocalDate(),
                               LocalDate.now())
        .getYears();
    assertThat(hackerNewsService.topCommentsForStory(ID))
        .containsExactlyElementsOf(Collections.singletonList(CommentDetailsUi.builder()
                                                                 .authorId(AUTHOR)
                                                                 .authorActiveTime(years)
                                                                 .build()));
  }

  @Test
  public void getPastStoriesServed() {
    when(hackerNewsDetailsCache.pastResultsServed()).thenReturn(OUTPUT_UI);
    assertThat(hackerNewsService.getPastStoriesServed())
        .containsExactlyInAnyOrderElementsOf(OUTPUT_UI);
  }
}