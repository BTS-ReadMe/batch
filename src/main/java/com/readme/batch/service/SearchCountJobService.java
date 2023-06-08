package com.readme.batch.service;

import com.readme.batch.model.Search;
import com.readme.batch.repository.SearchRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.EmptyResultDataAccessException;

@RequiredArgsConstructor
@Configuration
@EnableBatchProcessing
@Slf4j
public class SearchCountJobService {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final SearchRepository searchRepository;

    @Bean
    public Job searchCountJob() throws Exception {
        return jobBuilderFactory.get("searchCountJob")
            .start(searchCountStep())
            .build();
    }

    @Bean
    @JobScope
    public Step searchCountStep() throws Exception {
        try {
            return stepBuilderFactory.get("searchCountStep")
                .<Map.Entry<String, Long>, Search>chunk(
                    500) // 앞의 NovelCards는 read에서 읽은 아이템의 타입, 뒤의 NovelCards는 write에게 전달할 아이템의 타입
                .reader(searchCountReader(null))
                .processor(searchCountProcessor())
                .writer(searchCountWriter())
                .build();
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Bean
    @StepScope
    public ItemReader<Entry<String, Long>> searchCountReader(
        @Value("#{jobParameters['searchCount']}") String searchCountMapStr) throws IOException {
        Map<String, Long> searchCountMap = SearchRankingService.deserializeSearchCountMap(
            searchCountMapStr);
        return new IteratorItemReader<>(searchCountMap.entrySet().iterator());
    }

    @Bean
    @StepScope
    public ItemProcessor<Entry<String, Long>, Search> searchCountProcessor() {
        return item -> {
            String keyword = item.getKey();
            keyword = keyword.replaceAll("\"", "");
            log.info(keyword);
            Long count = item.getValue();
            LocalDateTime timeWithoutMinutesAndSeconds = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            Optional<Search> optionalSearch = searchRepository.findByKeywordAndSearchDate(keyword, timeWithoutMinutesAndSeconds);
            Search search = null;
            if (optionalSearch.isPresent()) {
                search = optionalSearch.get();
                search.setCount(search.getCount() + count);
            } else {
                search = new Search(keyword, count, timeWithoutMinutesAndSeconds);
            }
            return search;
        };
    }

    @Bean
    @StepScope
    public ItemWriter<Search> searchCountWriter() throws Exception {
        return new ItemWriter<Search>() {
            @Override
            public void write(List<? extends Search> items) throws Exception {
                searchRepository.saveAll(items);
            }
        };
    }
}
