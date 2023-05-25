package com.readme.batch.controller;

import com.readme.batch.model.NovelCards;
import com.readme.batch.service.NovelCardsViewJobLauncher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
@Slf4j
public class TestController {
    private final NovelCardsViewJobLauncher novelCardsViewJobLauncher;
    private final MongoTemplate mongoTemplate;
    @GetMapping
    public void test(@RequestParam String kafka) {
        novelCardsViewJobLauncher.listener(kafka);
    }
}
