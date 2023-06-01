package com.readme.batch.repository;

import com.readme.batch.model.NovelViews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NovelViewsRepository extends JpaRepository<NovelViews, Long> {

}
