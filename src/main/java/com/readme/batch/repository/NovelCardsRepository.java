package com.readme.batch.repository;

import com.readme.batch.model.NovelCards;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface NovelCardsRepository extends MongoRepository<NovelCards, String> {

}
