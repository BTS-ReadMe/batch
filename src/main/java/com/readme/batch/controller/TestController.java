package com.readme.batch.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readme.batch.model.NovelCards;
import com.readme.batch.repository.NovelCardsRepository;
import com.readme.batch.requestObject.RequestPlusViewCount;
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

    @GetMapping("plus-view")
    public void plusView(@RequestBody RequestPlusViewCount requestPlusViewCount) {
        producer.sendPlusView(requestPlusViewCount);
    }
    @GetMapping("/1")
    public void test1() {
        String kafka = "";
        novelCardsViewJobLauncher.listener();
    }

    @GetMapping("/2")
    public void test2() {
        String kafkaMessage = "{\"1\":2,\"2\":2,\"3\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8}";
        Map<String, Object> kafkaMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            kafkaMap = mapper.readValue(kafkaMessage, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        kafkaMap.forEach((novelId, viewsCount) -> {
            NovelCards novelCards = novelCardsRepository.findById(novelId).get();
            novelCards.setViews(novelCards.getViews()+Long.valueOf(String.valueOf(viewsCount)));
            novelCardsRepository.save(novelCards);
        });
    }
}
