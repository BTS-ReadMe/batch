package com.readme.batch.repository;

import com.readme.batch.model.SearchData;
import com.readme.batch.projection.ISearch;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchDataRepository extends JpaRepository<SearchData, Long> {
    Optional<SearchData> findByKeywordAndSearchDate(String keyword, LocalDateTime searchDate);

    @Query(nativeQuery = true, value = "SELECT search_data.keyword, search_data.search_date, RANK() OVER (ORDER BY search_data.count DESC) ranking FROM search_data WHERE search_data.search_date = :oneHourBefore ORDER BY search_data.count DESC Limit 10"
    )
    List<ISearch> findTop10(@Param("oneHourBefore") LocalDateTime oneHourBefore);
}
