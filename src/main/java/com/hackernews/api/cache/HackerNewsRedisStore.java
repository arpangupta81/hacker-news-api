package com.hackernews.api.cache;

import com.hackernews.api.model.client.StoryDetails;
import com.hackernews.api.model.ui.CommentDetailsUi;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class HackerNewsRedisStore {
  private static final String TOP_STORIES = "TOP_STORIES";
  private static final String PARENT_COMMENTS = "PARENT_COMMENTS";
  private final int cachingMinutes;
  private final RedisTemplate<String, CommentDetailsUi> commentDetailsTemplate;
  private final RedisTemplate<String, StoryDetails> storyDetailsTemplate;

  /**
   * Redis storage for Story and Comments.
   *
   * @param storyDetailsTemplate   storyRedisTemplate.
   * @param commentDetailsTemplate commentsRedisTemplate.
   * @param cachingMinutes         cachingMinutes.
   */
  public HackerNewsRedisStore(RedisTemplate<String, StoryDetails> storyDetailsTemplate,
                              RedisTemplate<String, CommentDetailsUi> commentDetailsTemplate,
                              @Value("${caching-minutes:10}") int cachingMinutes) {
    this.commentDetailsTemplate = commentDetailsTemplate;
    this.storyDetailsTemplate = storyDetailsTemplate;
    this.cachingMinutes = cachingMinutes;
  }

  /**
   * Top Stories from Redis.
   *
   * @return List of Story Details fetched from Redis.
   */
  public List<StoryDetails> fetchTopStoriesFromRedis() {
    Set<StoryDetails> topStoriesInCache = storyDetailsTemplate.opsForSet().members(TOP_STORIES);
    if (!CollectionUtils.isEmpty(topStoriesInCache)) {
      log.info("Fetched all top stories from Redis");
      return new ArrayList<>(topStoriesInCache);
    }
    return Collections.emptyList();
  }

  /**
   * This will add data to redis.
   *
   * @param storyDetailsUis Story Details to be cached.
   */
  public void addToTopStoriesInRedis(List<StoryDetails> storyDetailsUis) {
    StoryDetails[] storyDetails = storyDetailsUis.toArray(new StoryDetails[0]);
    storyDetailsTemplate.opsForSet().add(TOP_STORIES, storyDetails);
    storyDetailsTemplate.expire(TOP_STORIES, cachingMinutes, TimeUnit.MINUTES);
  }

  /**
   * Fetches all the top comments for the story.
   *
   * @param storyId storyId of the story.
   * @return List of Comment Details to be displayed on UI.
   */
  public List<CommentDetailsUi> fetchTopCommentsFromRedis(long storyId) {
    String redisKey = String.format("%s_%s", PARENT_COMMENTS, storyId);
    Set<CommentDetailsUi> topStoriesInCache = commentDetailsTemplate.opsForSet().members(redisKey);
    if (!CollectionUtils.isEmpty(topStoriesInCache)) {
      log.info("Fetched all top comment details from Redis for story : {}", storyId);
      return new ArrayList<>(topStoriesInCache);
    }
    return Collections.emptyList();
  }

  /**
   * This will add data to redis.
   *
   * @param commentDetailsUis Comment Details to be cached.
   */
  public void addToTopCommentsInRedis(long storyId, List<CommentDetailsUi> commentDetailsUis) {
    String redisKey = String.format("%s_%s", PARENT_COMMENTS, storyId);
    CommentDetailsUi[] storyDetails = commentDetailsUis.toArray(new CommentDetailsUi[0]);
    commentDetailsTemplate.opsForSet().add(redisKey, storyDetails);
    commentDetailsTemplate.expire(redisKey, cachingMinutes, TimeUnit.MINUTES);
  }
}