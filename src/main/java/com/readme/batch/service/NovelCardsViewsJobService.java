package com.readme.batch.service;

import com.readme.batch.dto.NovelViewDTO;
import com.readme.batch.dto.ViewCountDTO;
import com.readme.batch.model.NovelCards;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@RequiredArgsConstructor
@Configuration
@EnableBatchProcessing
@Slf4j
public class NovelCardsViewsJobService {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final MongoTemplate mongoTemplate;

    @Bean
    public Job NovelCardsViewsJob() throws Exception{
        try {
            return jobBuilderFactory.get("novelCardsViewsJob")
                .incrementer(new RunIdIncrementer())
                .start(novelCardsViewsStep())
                .build();
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException(e);
        }


    }

    @Bean
    @JobScope
    public Step novelCardsViewsStep() throws Exception {
        try {
            return stepBuilderFactory.get("novelCardsViewsStep")
                .<Map.Entry<Long, ViewCountDTO>, NovelCards> chunk(500) // 앞의 NovelCards는 read에서 읽은 아이템의 타입, 뒤의 NovelCards는 write에게 전달할 아이템의 타입
                .reader(reader(null))
                .processor(processor())
                .writer(writer())
                .build();
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Bean
    @StepScope
    public ItemReader<Map.Entry<Long, ViewCountDTO>> reader(@Value("#{jobParameters['novelViewsMapStr']}") String novelViewsMapStr) throws IOException {
        Map<Long, ViewCountDTO> novelViewsMap = NovelCardsViewJobLauncher.deserializeNovelViewsMap(novelViewsMapStr);
        return new IteratorItemReader<>(novelViewsMap.entrySet().iterator());
    }

    @Bean
    @StepScope
    public ItemProcessor<Map.Entry<Long, ViewCountDTO>, NovelCards> processor() {
        return item -> {
            Long novelId = item.getKey();
            Long viewCount = item.getValue().getViewCount();
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(String.valueOf(novelId)));
            NovelCards novelCards = mongoTemplate.findOne(query, NovelCards.class, "novel_cards");
            novelCards.setViews(novelCards.getViews() + viewCount);
            return novelCards;
        };
    }

    @Bean
    @StepScope
    public ItemWriter<NovelCards> writer() throws Exception {
        return new MongoItemWriterBuilder<NovelCards>()
            .template(mongoTemplate)
            .collection("novel_cards")
            .build();
    }
}
