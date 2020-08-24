package com.hackernews.api.model.client;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StoryDetails {
  private long id;
  private String by;
  private List<Long> kids;
  private long score;
  private long time;
  private String title;
  private String type;
  private String url;
  private long descendants;
}
