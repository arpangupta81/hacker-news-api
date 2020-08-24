package com.hackernews.api.cache;

import static com.hackernews.api.TestUtil.OUTPUT_UI;
import static com.hackernews.api.TestUtil.getStory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.google.common.collect.ImmutableList;
import com.hackernews.api.HnApiBackendApplication;
import com.hackernews.api.client.HackerNewsApiClient;
import com.hackernews.api.model.client.CommentDetails;
import java.util.Collections;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {HnApiBackendApplication.class,
    HackerNewsDetailsCache.class})
public class HackerNewsDetailsCacheTest {

  private static final String TOP_STORIES_CACHE = "TOP_STORIES_CACHE";
  private static final String PARENT_COMMENTS_CACHE = "PARENT_COMMENTS_CACHE";
  @MockBean
  private HackerNewsApiClient hackerNewsApiClient;
  @Autowired
  private HackerNewsDetailsCache hackerNewsDetailsCache;
  @Autowired
  private CacheManager cacheManager;

  @Test
  public void topStoriesWhenFetchedFromCache() {
    when(hackerNewsApiClient.topStories()).thenReturn(ImmutableList.of(1L, 2L, 3L));
    when(hackerNewsApiClient.storyDetails(1L)).thenReturn(getStory(1, 10));
    when(hackerNewsApiClient.storyDetails(2L)).thenReturn(getStory(2, 10));
    when(hackerNewsApiClient.storyDetails(3L)).thenReturn(getStory(3, 10));

    hackerNewsDetailsCache.topStoriesFromCache();
    hackerNewsDetailsCache.topStoriesFromCache();
    verify(hackerNewsApiClient, times(1)).topStories();
    hackerNewsDetailsCache.topStoriesFromCache();

    verify(hackerNewsApiClient, times(1)).storyDetails(1L);
    verify(hackerNewsApiClient, times(1)).storyDetails(2L);
    verify(hackerNewsApiClient, times(1)).storyDetails(3L);

  }

  @Test
  public void topStoriesWhenFetchedFromApi() {
    when(hackerNewsApiClient.topStories()).thenReturn(ImmutableList.of(1L, 2L, 3L));
    when(hackerNewsApiClient.storyDetails(1L)).thenReturn(getStory(1, 10));
    when(hackerNewsApiClient.storyDetails(2L)).thenReturn(getStory(2, 10));
    when(hackerNewsApiClient.storyDetails(3L)).thenReturn(getStory(3, 10));

    hackerNewsDetailsCache.topStoriesFromCache();
    clearCache();
    hackerNewsDetailsCache.topStoriesFromCache();
    clearCache();
    verify(hackerNewsApiClient, times(2)).topStories();
    hackerNewsDetailsCache.topStoriesFromCache();

    verify(hackerNewsApiClient, times(3)).storyDetails(1L);
    verify(hackerNewsApiClient, times(3)).storyDetails(2L);
    verify(hackerNewsApiClient, times(3)).storyDetails(3L);
  }

  @After
  public void clearCache() {
    cacheManager.getCache(TOP_STORIES_CACHE).clear();
    cacheManager.getCache(PARENT_COMMENTS_CACHE).clear();
  }

  @Test
  public void topCommentsWhenFetchedFromApi() {
    when(hackerNewsApiClient.commentDetails(123)).thenReturn(CommentDetails.builder().build());
    hackerNewsDetailsCache.getAllParentCommentIdsToNumberOfChildren(Collections.singletonList(123L));
    clearCache();
    hackerNewsDetailsCache.getAllParentCommentIdsToNumberOfChildren(Collections.singletonList(123L));
    clearCache();
    verify(hackerNewsApiClient, times(2)).commentDetails(123);
  }

  @Test
  public void topCommentsWhenFetchedFromCache() {
    when(hackerNewsApiClient.commentDetails(123L)).thenReturn(CommentDetails.builder()
                                                                  .kids(Collections.singletonList(1L))
                                                                  .build());
    when(hackerNewsApiClient.commentDetails(1L)).thenReturn(CommentDetails.builder().build());
    hackerNewsDetailsCache.getAllParentCommentIdsToNumberOfChildren(Collections.singletonList(123L));
    hackerNewsDetailsCache.getAllParentCommentIdsToNumberOfChildren(Collections.singletonList(123L));
    verify(hackerNewsApiClient, times(1)).commentDetails(123);
  }

  @Test
  public void pastResultsServed() {
    hackerNewsDetailsCache.putServedItemsToCache(OUTPUT_UI);
    assertThat(hackerNewsDetailsCache.pastResultsServed())
        .containsExactlyInAnyOrderElementsOf(OUTPUT_UI);
  }
}