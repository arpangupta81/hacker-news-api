package com.hackernews.api.model.client;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserDetails {
  private String id;
  private String about;
  private long created;
  private long delay;
  private long karma;
  private List<String> submitted;
}
