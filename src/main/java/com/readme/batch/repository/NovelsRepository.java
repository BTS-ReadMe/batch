package com.readme.batch.repository;

import com.readme.batch.model.Novels;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NovelsRepository extends JpaRepository<Novels, Long> {

}
