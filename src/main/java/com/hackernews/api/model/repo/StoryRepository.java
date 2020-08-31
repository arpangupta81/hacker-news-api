package com.hackernews.api.model.repo;

import com.hackernews.api.model.ui.StoryDetailsUi;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryRepository extends CassandraRepository<StoryDetailsUi, Long> {
}
