package com.readme.batch.service;

import com.readme.batch.dto.NovelViewDTO;
import com.readme.batch.model.NovelViews;
import com.readme.batch.repository.NovelViewsRepository;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@RequiredArgsConstructor
@Configuration
@EnableBatchProcessing
@Slf4j
public class NovelViewsDataJobService {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final NovelViewsRepository novelViewsRepository;

    @Bean
    public Job novelViewsDataJob() throws Exception {
        return jobBuilderFactory.get("novelViewsDataJob")
            .start(novelViewsStep())
            .build();
    }

    @Bean
    @JobScope
    public Step novelViewsStep() throws Exception {
        try {
            return stepBuilderFactory.get("novelViewsStep")
                .<Map.Entry<Long, NovelViewDTO>, NovelViews>chunk(
                    500) // 앞의 NovelCards는 read에서 읽은 아이템의 타입, 뒤의 NovelCards는 write에게 전달할 아이템의 타입
                .reader(novelViewsReader(null))
                .processor(novelViewsProcessor())
                .writer(novelViewsWriter())
                .build();
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Bean
    @StepScope
    public ItemReader<Entry<Long, NovelViewDTO>> novelViewsReader(
        @Value("#{jobParameters['novelViewsData']}") String novelViewsMapStr) throws IOException {
        Map<Long, NovelViewDTO> novelViewsMap = NovelCardsViewJobLauncher.deserializeNovelViewsDataMap(
            novelViewsMapStr);
        return new IteratorItemReader<>(novelViewsMap.entrySet().iterator());
    }

    @Bean
    @StepScope
    public ItemProcessor<Entry<Long, NovelViewDTO>, NovelViews> novelViewsProcessor() {
        return item -> {
            Long novelId = item.getKey();
            NovelViewDTO novelViewDTO = item.getValue();
            NovelViews novelViews = null;
            LocalDateTime timeWithoutMinutesAndSeconds = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            if (novelViewsRepository.existsByNovelIdAndViewsDate(novelId, timeWithoutMinutesAndSeconds)) {
                novelViews = novelViewsRepository.findByNovelIdAndViewsDate(novelId, timeWithoutMinutesAndSeconds);
            } else {
                novelViews = new NovelViews(novelId, novelViewDTO.getTitle(), novelViewDTO.getThumbnail(), timeWithoutMinutesAndSeconds, 0L);
            }
            novelViews.setViews(novelViews.getViews() + novelViewDTO.getPlusCnt());
            return novelViews;
        };
    }

    @Bean
    @StepScope
    public ItemWriter<NovelViews> novelViewsWriter() throws Exception {
        return new ItemWriter<NovelViews>() {
            @Override
            public void write(List<? extends NovelViews> items) throws Exception {
                novelViewsRepository.save(items.get(0));
            }
        };
    }
}
