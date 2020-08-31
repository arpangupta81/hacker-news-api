package com.hackernews.api.model.client;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDetails {
  private String by;
  private long id;
  private int userActiveTime;
  private long parent;
  private List<Long> kids;
  private String text;
}