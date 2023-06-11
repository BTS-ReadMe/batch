package com.readme.batch.repository;

import com.readme.batch.model.MainCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MainCategoryRepository extends JpaRepository<MainCategory, Long> {
    List<MainCategory> findAllByOrderById();

    Optional<MainCategory> findByTitle(String genre);
}