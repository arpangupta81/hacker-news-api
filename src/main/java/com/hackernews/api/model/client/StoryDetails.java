package com.hackernews.api.model.client;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoryDetails implements Serializable {
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