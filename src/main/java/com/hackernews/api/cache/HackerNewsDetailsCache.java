package com.hackernews.api.cache;

import com.hackernews.api.client.HackerNewsApiClient;
import com.hackernews.api.model.client.StoryDetails;
import com.hackernews.api.model.ui.StoryDetailsUi;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HackerNewsDetailsCache {

  private static final int INITIAL_VALUE = 0;
  private final HackerNewsApiClient hackerNewsApiClient;
  private Set<StoryDetailsUi> alreadyServedStoryDetails;

  public HackerNewsDetailsCache(HackerNewsApiClient hackerNewsApiClient) {
    this.hackerNewsApiClient = hackerNewsApiClient;
    this.alreadyServedStoryDetails = new HashSet<>();
  }

  /**
   * This will fetch all the id's of the stories from top-stories endpoint of the API.
   * Then it makes separate call for each item to fetch the detail for that id.
   *
   * @return List of {@link StoryDetails}
   */
  @Cacheable(value = "TOP_STORIES_CACHE")
  public List<StoryDetails> topStoriesFromCache() {
    log.info("Fetching all top stories from API");
    List<Long> allStoriesIds = hackerNewsApiClient.topStories();
    log.info("Number of top stories from API are : {}", allStoriesIds.size());
    // TODO: 2020-08-25 This can be moved to a Elasticache/Redis with TTL keys expiring 10 mins
    return allStoriesIds.stream().parallel()
        .map(id -> {
          log.debug("Fetching Details for the story with id : {}", id);
          return hackerNewsApiClient.storyDetails(id);
        }).collect(Collectors.toList());
  }

  /**
   * This will fetch all the id's of the stories from top-stories endpoint of the API.
   * Then it makes separate call for each item to fetch the detail for that id.
   *
   * @param storyId Story Id for which comments are to be fetched.
   * @return List of {@link StoryDetails}
   */
  @Cacheable(value = "PARENT_COMMENTS_CACHE")
  public Map<Long, Integer> getAllParentCommentIdsToNumberOfChildren(long storyId) {
    List<Long> kids = hackerNewsApiClient.storyDetails(storyId).getKids();
    log.info("Fetching child comments and their details for parents : {}", kids);
    return kids.stream().parallel()
        .collect(Collectors.toMap(Function.identity(), parentComment -> {
          AtomicInteger counter = new AtomicInteger(INITIAL_VALUE);
          return makeApiCallAndIncreaseCount(parentComment, counter);
        }));
  }

  /**
   * Recursive call to fetch details for evaluation of number of kids.
   *
   * @param commentId commentId
   * @param counter   counter for increasing the value.
   * @return Returns the number of children present for a particular comment.
   */
  private int makeApiCallAndIncreaseCount(long commentId, AtomicInteger counter) {
    List<Long> kids = hackerNewsApiClient.commentDetails(commentId).getKids();
    if (null == kids || kids.isEmpty()) {
      return counter.get();
    }
    for (long kid : kids) {
      counter.incrementAndGet();
      makeApiCallAndIncreaseCount(kid, counter);
    }
    return counter.get();
  }

  /**
   * @return All the results served in the past for top stories.
   */
  public List<StoryDetailsUi> pastResultsServed() {
    log.info("Fetching all past top stories served to the clients.");
    return new ArrayList<>(alreadyServedStoryDetails);
  }

  /**
   * This will put the items served in the past to an imaginary cache.
   *
   * @param servedStoryDetails Served Items.
   */
  public void putServedItemsToCache(List<StoryDetailsUi> servedStoryDetails) {
    log.info("Adding stories with ids : {} to already served items",
             servedStoryDetails.stream().map(StoryDetailsUi::getId).collect(Collectors.toList()));
    // TODO: 2020-08-25 This can be moved to a NoSQL database: Eg. Cassandra
    alreadyServedStoryDetails.addAll(servedStoryDetails);
  }
}