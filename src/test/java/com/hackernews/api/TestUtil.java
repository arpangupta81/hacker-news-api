package com.hackernews.api;

import com.google.common.collect.ImmutableList;
import com.hackernews.api.model.client.StoryDetails;
import com.hackernews.api.model.ui.StoryDetailsUi;
import java.time.Instant;

public class TestUtil {
  public static final ImmutableList<StoryDetails> INPUT = ImmutableList.of(getStory(1, 10),
                                                                           getStory(2, 20),
                                                                           getStory(3, 1));
  public static final ImmutableList<StoryDetailsUi> OUTPUT_UI = ImmutableList
      .of(getStoryDetailsUi(1, 10),
          getStoryDetailsUi(2, 20),
          getStoryDetailsUi(3, 1));

  private TestUtil() {
  }

  public static StoryDetails getStory(int id, int score) {
    return StoryDetails.builder()
        .id(id)
        .score(score)
        .time(Instant.EPOCH.getEpochSecond())
        .type("Story")
        .build();
  }

  public static StoryDetailsUi getStoryDetailsUi(int id, int score) {
    return StoryDetailsUi.builder().id(id).timeOfSubmission(Instant.EPOCH).score(score).build();
  }
}
