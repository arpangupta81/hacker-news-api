package com.hackernews.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableCaching
@EnableScheduling
public class HnApiBackendApplication {

  private static final String TOP_STORIES_CACHE = "TOP_STORIES_CACHE";
  private static final String PARENT_COMMENTS_CACHE = "PARENT_COMMENTS_CACHE";

  public static void main(String[] args) {
    SpringApplication.run(HnApiBackendApplication.class, args);
  }

  @Bean
  public CacheManager cacheManager() {
    return new ConcurrentMapCacheManager(TOP_STORIES_CACHE, PARENT_COMMENTS_CACHE);
  }
}
