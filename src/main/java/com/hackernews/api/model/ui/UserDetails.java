package com.hackernews.api.model.ui;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetails implements Serializable {
  private String id;
  private String about;
  private long created;
  private long delay;
  private long karma;
  private List<String> submitted;
}