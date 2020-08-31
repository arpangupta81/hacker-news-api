package com.hackernews.api.config;

import com.hackernews.api.model.client.StoryDetails;
import com.hackernews.api.model.ui.CommentDetailsUi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  /**
   * Bean for Redis Template for Story Details.
   *
   * @return Redis Template.
   */
  @Bean
  public RedisTemplate<String, StoryDetails> storyDetailsRedisTemplate() {
    RedisTemplate<String, StoryDetails> stringStoryDetailsRedisTemplate = new RedisTemplate<>();
    stringStoryDetailsRedisTemplate.setConnectionFactory(redisConnectionFactory());
    stringStoryDetailsRedisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
    stringStoryDetailsRedisTemplate.setKeySerializer(new StringRedisSerializer());
    stringStoryDetailsRedisTemplate.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
    stringStoryDetailsRedisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    stringStoryDetailsRedisTemplate.afterPropertiesSet();
    return stringStoryDetailsRedisTemplate;
  }

  @Bean
  public LettuceConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory();
  }

  /**
   * Bean for Redis Template for Comment Details.
   *
   * @return Redis Template.
   */
  @Bean
  public RedisTemplate<String, CommentDetailsUi> commentDetailsUiRedisTemplate() {
    RedisTemplate<String, CommentDetailsUi> commentDetailsUiRedisTemplate = new RedisTemplate<>();
    commentDetailsUiRedisTemplate.setConnectionFactory(redisConnectionFactory());
    commentDetailsUiRedisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
    commentDetailsUiRedisTemplate.setKeySerializer(new StringRedisSerializer());
    commentDetailsUiRedisTemplate.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
    commentDetailsUiRedisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    commentDetailsUiRedisTemplate.afterPropertiesSet();
    return commentDetailsUiRedisTemplate;
  }
}
