package com.hackernews.api.client;

import com.hackernews.api.model.client.CommentDetails;
import com.hackernews.api.model.client.StoryDetails;
import com.hackernews.api.model.ui.UserDetails;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client to Connect with Hacker News API.
 */
@FeignClient(value = "hn-api-client", url = "https://hacker-news.firebaseio.com/v0/")
public interface HackerNewsApiClient {

  @GetMapping("item/{id}.json")
  StoryDetails storyDetails(@PathVariable long id);

  @GetMapping("topstories.json")
  List<Long> topStories();

  @GetMapping("item/{id}.json")
  CommentDetails commentDetails(@PathVariable long id);

  @GetMapping("user/{userId}.json")
  UserDetails userDetails(@PathVariable String userId);
}