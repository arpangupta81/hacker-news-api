package com.hackernews.api.services;

import com.hackernews.api.cache.HackerNewsRedisStore;
import com.hackernews.api.client.HackerNewsApiClient;
import com.hackernews.api.model.client.CommentDetails;
import com.hackernews.api.model.client.StoryDetails;
import com.hackernews.api.model.repo.StoryRepository;
import com.hackernews.api.model.ui.CommentDetailsUi;
import com.hackernews.api.model.ui.StoryDetailsUi;
import com.hackernews.api.model.ui.UserDetails;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HackerNewsDetailsService {

  private static final int INITIAL_VALUE = 0;
  private static final String STORY = "story";
  private final HackerNewsApiClient hackerNewsApiClient;
  private final StoryRepository pastStoriesRepository;
  private final HackerNewsRedisStore hackerNewsRedisStore;
  private final int noOfTopCommentsResults;
  private final int noOfTopStoryResults;

  public HackerNewsDetailsService(HackerNewsApiClient hackerNewsApiClient,
                                  StoryRepository pastStoriesRepository,
                                  HackerNewsRedisStore hackerNewsRedisStore,
                                  @Value("${no-of-story-results:10}")
                                      int noOfTopStoryResults,
                                  @Value("${no-of-comments-results:10}")
                                      int noOfTopCommentsResults) {
    this.hackerNewsApiClient = hackerNewsApiClient;
    this.pastStoriesRepository = pastStoriesRepository;
    this.hackerNewsRedisStore = hackerNewsRedisStore;
    this.noOfTopCommentsResults = noOfTopCommentsResults;
    this.noOfTopStoryResults = noOfTopStoryResults;
  }

  /**
   * This will fetch all the id's of the stories from top-stories endpoint of the API.
   * Then it makes separate call for each item to fetch the detail for that id.
   *
   * @param storyId Story Id for which comments are to be fetched.
   * @return List of {@link StoryDetails}
   */
  public List<CommentDetailsUi> getAllParentCommentIdsToNumberOfChildren(long storyId) {
    List<CommentDetailsUi> detailsFromRedis = hackerNewsRedisStore
        .fetchTopCommentsFromRedis(storyId);
    if (!detailsFromRedis.isEmpty()) {
      return detailsFromRedis;
    }
    log.info("Fetching top parent comments for story-id: {}", storyId);
    List<Long> kids = hackerNewsApiClient.storyDetails(storyId).getKids();
    log.info("Fetching child comments and their details for parents : {}", kids);
    Map<Long, Integer> parentToNumberOfChildren = kids.stream().parallel()
        .collect(Collectors.toMap(Function.identity(), parentComment -> {
          AtomicInteger counter = new AtomicInteger(INITIAL_VALUE);
          return makeApiCallAndIncreaseCount(parentComment, counter);
        }));
    List<CommentDetailsUi> commentDetailsUis = fetchDetailsForParents(parentToNumberOfChildren);
    hackerNewsRedisStore.addToTopCommentsInRedis(storyId, commentDetailsUis);
    return commentDetailsUis;
  }

  private List<CommentDetailsUi> fetchDetailsForParents(Map<Long, Integer> parentToNoOfChildren) {
    return parentToNoOfChildren.entrySet().stream()
        .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
        .limit(noOfTopCommentsResults)
        .map(comment -> hackerNewsApiClient.commentDetails(comment.getKey()))
        .map(this::addUserDetailsToComment)
        .collect(Collectors.toList());
  }

  private CommentDetailsUi addUserDetailsToComment(CommentDetails commentDetails) {
    log.info("Fetching user details for user-id: {}", commentDetails.getBy());
    UserDetails userDetails = hackerNewsApiClient.userDetails(commentDetails.getBy());
    return CommentDetailsUi.from(commentDetails, userDetails);
  }

  /**
   * This will fetch all the id's of the stories from top-stories endpoint of the API.
   * Then it makes separate call for each item to fetch the detail for that id.
   *
   * @return List of {@link StoryDetails}
   */
  public List<StoryDetailsUi> topStoriesWithSortingLogic() {
    List<StoryDetails> storyDetailsFromRedis = hackerNewsRedisStore.fetchTopStoriesFromRedis();
    List<StoryDetails> storyDetails = storyDetailsFromRedis.isEmpty()
        ? getStoryDetailsFromApi() : storyDetailsFromRedis;
    log.info("Fetched all Top Story Details.");
    List<StoryDetailsUi> storyDetailsFromApi = storyDetails.stream()
        .sorted(Comparator.comparing(StoryDetails::getScore).reversed())
        .limit(noOfTopStoryResults)
        .map(StoryDetailsUi::from)
        .collect(Collectors.toList());
    if (storyDetailsFromRedis.isEmpty()) {
      log.info("Adding Top Story Details to Redis");
      hackerNewsRedisStore.addToTopStoriesInRedis(storyDetails);
    }
    pastStoriesRepository.saveAll(storyDetailsFromApi);
    return storyDetailsFromApi;
  }

  private List<StoryDetails> getStoryDetailsFromApi() {
    log.info("Fetching all top stories from API");
    List<Long> allStoriesIds = hackerNewsApiClient.topStories();
    log.info("Number of top stories fetched from API are : {}", allStoriesIds.size());
    return allStoriesIds.stream().parallel()
        .map(id -> {
          log.debug("Fetching Details for the story with id : {}", id);
          return hackerNewsApiClient.storyDetails(id);
        })
        .filter(item -> STORY.equalsIgnoreCase(item.getType()))
        .collect(Collectors.toList());
  }

  /**
   * This will give all the stories served to the clients in the past.
   *
   * @return List of {@link StoryDetails}
   */
  public List<StoryDetailsUi> getPastStoriesServed() {
    List<StoryDetailsUi> resultsServedInPast = pastStoriesRepository.findAll()
        .stream()
        .sorted(Comparator.comparing(StoryDetailsUi::getScore).reversed())
        .collect(Collectors.toList());
    log.info("Length of past stories served: {}", resultsServedInPast.size());
    return resultsServedInPast;
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
}