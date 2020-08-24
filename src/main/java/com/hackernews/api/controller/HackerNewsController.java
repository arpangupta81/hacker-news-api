package com.hackernews.api.controller;

import com.hackernews.api.model.ui.CommentDetailsUi;
import com.hackernews.api.model.ui.StoryDetailsUi;
import com.hackernews.api.services.HackerNewsService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HackerNewsController {
  private final HackerNewsService hackerNewsService;

  public HackerNewsController(HackerNewsService hackerNewsService) {
    this.hackerNewsService = hackerNewsService;
  }

  @GetMapping("top-stories")
  public List<StoryDetailsUi> topStoriesInLastTenMinutes() {
    return hackerNewsService.topStoriesWithSortingLogic();
  }

  @GetMapping("comments")
  public List<CommentDetailsUi> topCommentsForStory(@RequestParam long storyId) {
    return hackerNewsService.topCommentsForStory(storyId);
  }

  @GetMapping("past-stories")
  public List<StoryDetailsUi> pastStoriesServed() {
    return hackerNewsService.getPastStoriesServed();
  }
}