package com.readme.batch.repository;

import com.readme.batch.model.Search;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchRepository extends JpaRepository<Search, Long> {
    Optional<Search> findByKeywordAndSearchDate(String keyword, LocalDateTime searchDate);
}
