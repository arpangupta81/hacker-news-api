package com.hackernews.api.services;


import com.hackernews.api.cache.HackerNewsDetailsCache;
import com.hackernews.api.client.HackerNewsApiClient;
import com.hackernews.api.model.client.CommentDetails;
import com.hackernews.api.model.client.StoryDetails;
import com.hackernews.api.model.client.UserDetails;
import com.hackernews.api.model.ui.CommentDetailsUi;
import com.hackernews.api.model.ui.StoryDetailsUi;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HackerNewsService {

  private static final String STORY = "story";
  private final HackerNewsDetailsCache hackerNewsDetailsCache;
  private final HackerNewsApiClient hackerNewsApiClient;
  private final int noOfTopStoryResults;
  private final int noOfTopCommentsResults;

  /**
   * The class will be used for the computations required for API.
   *
   * @param hackerNewsDetailsCache Cache.
   * @param hackerNewsApiClient    Client.
   * @param noOfTopStoryResults    The maximum number of Top Story Results required.
   * @param noOfTopCommentsResults The maximum number of Top Comment Results required.
   */
  public HackerNewsService(HackerNewsDetailsCache hackerNewsDetailsCache,
                           HackerNewsApiClient hackerNewsApiClient,
                           @Value("${no-of-top-stories-results:10}") int noOfTopStoryResults,
                           @Value("${no-of-comments-results:10}") int noOfTopCommentsResults) {
    this.hackerNewsApiClient = hackerNewsApiClient;
    this.hackerNewsDetailsCache = hackerNewsDetailsCache;
    this.noOfTopStoryResults = noOfTopStoryResults;
    this.noOfTopCommentsResults = noOfTopCommentsResults;
  }

  /**
   * This method will give the top stories cached within last ten minutes.
   * <p>
   * Top stories will be sorted on the basis of their score with highest score being the first
   * and min being the last.
   * </p>
   *
   * @return List of {@link StoryDetails}
   */
  public List<StoryDetailsUi> topStoriesWithSortingLogic() {
    List<StoryDetailsUi> storyDetails = hackerNewsDetailsCache.topStoriesFromCache()
        .stream()
        .filter(item -> STORY.equalsIgnoreCase(item.getType()))
        .sorted(Comparator.comparing(StoryDetails::getScore).reversed())
        .limit(noOfTopStoryResults)
        .map(StoryDetailsUi::from)
        .collect(Collectors.toList());
    if (!hackerNewsDetailsCache.pastResultsServed().containsAll(storyDetails)) {
      hackerNewsDetailsCache.putServedItemsToCache(storyDetails);
    }
    return storyDetails;
  }

  /**
   * This will give all the stories served to the clients in the past.
   *
   * @return List of {@link StoryDetails}
   */
  public List<StoryDetailsUi> getPastStoriesServed() {
    List<StoryDetailsUi> resultsServedInPast = hackerNewsDetailsCache.pastResultsServed()
        .stream()
        .sorted(Comparator.comparing(StoryDetailsUi::getScore).reversed())
        .collect(Collectors.toList());
    log.info("Length of past stories served: {}", resultsServedInPast.size());
    return resultsServedInPast;
  }

  /**
   * This will sort all the parent comments on the basis of the number of children.
   *
   * @param storyId Story for which top comments are to fetched.
   * @return {@link CommentDetails} list.
   */
  public List<CommentDetailsUi> topCommentsForStory(long storyId) {
    log.info("Fetching top parent comments for story-id: {}", storyId);
    return hackerNewsDetailsCache.getAllParentCommentIdsToNumberOfChildren(storyId)
        .entrySet().stream()
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
}