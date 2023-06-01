package com.readme.batch.repository;

import com.readme.batch.model.NovelViews;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NovelViewsRepository extends JpaRepository<NovelViews, Long> {
    Boolean existsByNovelIdAndViewsDate(Long novelId, Date viewsDate);
    NovelViews findByNovelIdAndViewsDate(Long novelId, Date viewsDate);
}
