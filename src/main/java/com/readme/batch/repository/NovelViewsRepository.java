package com.readme.batch.repository;

import com.readme.batch.model.NovelViews;
import com.readme.batch.projection.INovelViews;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NovelViewsRepository extends JpaRepository<NovelViews, Long> {
    Boolean existsByNovelIdAndViewsDate(Long novelId, LocalDateTime viewsDate);
    NovelViews findByNovelIdAndViewsDate(Long novelId, LocalDateTime viewsDate);
    @Query(nativeQuery = true, value = "SELECT *, RANK() OVER (ORDER BY views DESC) ranking  FROM novel_views WHERE views_date = :viewsDate limit 10")
    List<INovelViews> findAllByViewsDateWithRank(@Param("viewsDate") LocalDateTime viewsDate);


}
