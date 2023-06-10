package com.readme.batch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readme.batch.responseObject.ResponseSearch;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchRankingService {
    private Map<String, Long> searchCount = new HashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final JobLauncher jobLauncher;
    private final SearchCountJobService searchCountJobService;
    private final SearchService searchService;

    @KafkaListener(topics = "outputSearch", groupId = "batch")
    public void getSearchCount(ResponseSearch responseSearch) {
        String keyword = responseSearch.getKeyword();
        long count = responseSearch.getCount();
        searchCount.put(keyword, searchCount.getOrDefault(keyword, 0L) + count);
        log.info("map: " + searchCount.toString());
    }

//    @KafkaListener(topics = "inputSearch", groupId = "batch")
    public void saveSearchCount(ConsumerRecord<String, String> record) {
        String keyword = record.value().replaceAll("\"", "");
        searchService.saveSearchData(keyword);
    }

    @Scheduled(fixedRate = 10000)
    public void searchCountJobLauncher() throws Exception {
        Map<String, Long> currentSearchCount = new HashMap<>(searchCount);
        if (!currentSearchCount.isEmpty()) {
            String searchCountDataString = serializeSearchCountMap(searchCount);
            log.info("searchCountDataString : " + searchCountDataString);
            searchCount.clear();
            JobParameters jobParameters = new JobParametersBuilder()
                .addString("searchCount", searchCountDataString)
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
            JobExecution jobExecution = jobLauncher.run(
                searchCountJobService.searchCountJob(), jobParameters);
        }
    }

    public String serializeSearchCountMap(Map<String, Long> searchCountMap)
        throws JsonProcessingException {
        return objectMapper.writeValueAsString(searchCountMap);
    }

    public static Map<String, Long> deserializeSearchCountMap(String searchCountMap)
        throws IOException {
        return objectMapper.readValue(searchCountMap,
            new TypeReference<Map<String, Long>>() {
            });
    }
}
