package com.hackernews.api.config;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@Slf4j
public class CacheConfiguration {
  private static final String TOP_STORIES_CACHE = "TOP_STORIES_CACHE";
  private static final String PARENT_COMMENTS_CACHE = "PARENT_COMMENTS_CACHE";
  private final CacheManager cacheManager;

  public CacheConfiguration(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  /**
   * Scheduling cron for Evicting cache every 10 minutes by default.
   */
  @Scheduled(fixedDelayString = "${spring.cache.expire.delay:600000}")
  public void cacheEvict() {
    log.info("Evicting Hacker-News Top Stories Cache");
    Objects.requireNonNull(cacheManager.getCache(TOP_STORIES_CACHE)).clear();
    Objects.requireNonNull(cacheManager.getCache(PARENT_COMMENTS_CACHE)).clear();
  }
}