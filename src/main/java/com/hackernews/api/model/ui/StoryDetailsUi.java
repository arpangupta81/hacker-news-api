package com.hackernews.api.model.ui;

import com.hackernews.api.model.client.StoryDetails;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StoryDetailsUi {
  private long id;
  private String author;
  private long score;
  private Instant timeOfSubmission;
  private String storyTitle;
  private String url;

  /**
   * Transformation Function for Ui Model.
   *
   * @param storyDetails {@link StoryDetails}
   * @return Corresponding details to be shown on UI.
   */
  public static StoryDetailsUi from(StoryDetails storyDetails) {
    return StoryDetailsUi.builder()
        .id(storyDetails.getId())
        .author(storyDetails.getBy())
        .score(storyDetails.getScore())
        .storyTitle(storyDetails.getTitle())
        .timeOfSubmission(Instant.ofEpochSecond(storyDetails.getTime()))
        .url(storyDetails.getUrl())
        .build();
  }
}
