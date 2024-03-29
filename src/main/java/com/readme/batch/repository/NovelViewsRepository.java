package com.readme.batch.repository;

import com.readme.batch.model.NovelViews;
import com.readme.batch.projection.INovelViews;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NovelViewsRepository extends JpaRepository<NovelViews, Long> {
    Boolean existsByNovelIdAndViewsDate(Long novelId, LocalDateTime viewsDate);
    NovelViews findByNovelIdAndViewsDate(Long novelId, LocalDateTime viewsDate);
    @Query(nativeQuery = true, value =
        "SELECT novels.id, novels.author, novels.genre, novels.grade, novels.thumbnail, novels.serialization_status, novels.title, novel_views.views_date, RANK() OVER (ORDER BY novel_views.views DESC) ranking " +
            "FROM novels " +
            "INNER JOIN novel_views ON novels.id = novel_views.novel_id " +
            "WHERE novel_views.views_date = :viewsDate " +
            "LIMIT 15")
    List<INovelViews> findAllByViewsDateWithRank(@Param("viewsDate") LocalDateTime viewsDate);


}
