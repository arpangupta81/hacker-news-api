package com.hackernews.api.model.client;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CommentDetails {
  private String by;
  private long id;
  private int userActiveTime;
  private long parent;
  private List<Long> kids;
  private String text;
}