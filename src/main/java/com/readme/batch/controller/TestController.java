package com.readme.batch.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readme.batch.model.NovelCards;
import com.readme.batch.repository.NovelCardsRepository;
import com.readme.batch.requestObject.RequestPlusViewCount;
import com.readme.batch.requestObject.RequestPlusViewString;
import com.readme.batch.service.NovelCardsViewJobLauncher;
import com.readme.batch.service.Producer;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    private final NovelCardsRepository novelCardsRepository;
    private final Producer producer;

    @PostMapping("plus-view")
    public void plusView(@RequestBody RequestPlusViewCount requestPlusViewCount) {
        producer.sendPlusView(requestPlusViewCount);
    }

    @PostMapping("test-plus-view")
    public void testPlusView(@RequestBody RequestPlusViewString requestPlusViewString) throws Exception {
        novelCardsViewJobLauncher.plusViewJob(requestPlusViewString.getPlusViewString());
    }
}
