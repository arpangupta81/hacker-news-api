package com.hackernews.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@SpringBootApplication
@EnableFeignClients
@EnableCassandraRepositories
public class HnApiBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(HnApiBackendApplication.class, args);
  }
}