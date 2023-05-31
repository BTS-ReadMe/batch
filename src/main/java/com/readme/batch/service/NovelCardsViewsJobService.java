package com.readme.batch.service;

import com.readme.batch.model.NovelCards;
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
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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
                .<NovelCards, NovelCards> chunk(1) // 앞의 NovelCards는 read에서 읽은 아이템의 타입, 뒤의 NovelCards는 write에게 전달할 아이템의 타입
                .reader(reader(null))
                .processor(processor(null))
                .writer(writer())
                .build();
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Bean
    @StepScope
    public ItemReader<NovelCards> reader(@Value("#{jobParameters['novelId']}") Long novelId) {
        return new ItemReader<NovelCards>() {
            private boolean readFlag = false;

            @Override
            public NovelCards read() throws Exception {
                if (readFlag) {
                    return null;
                }

                Query query = new Query();
                query.addCriteria(Criteria.where("_id").is(String.valueOf(novelId)));
                NovelCards result = mongoTemplate.findOne(query, NovelCards.class, "novel_cards");

                if (result != null) {
                    readFlag = true;
                }

                return result;
            }
        };
    }

    @Bean
    @StepScope
    public ItemProcessor<NovelCards, NovelCards> processor(@Value("#{jobParameters['views']}") Long views) throws Exception{
        try {
            return new ItemProcessor<NovelCards, NovelCards>() {
                @Override
                public NovelCards process(NovelCards novelCards) throws Exception {
                    novelCards.updateViewCount(views);
                    return novelCards;
                }
            };
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    @StepScope
    public MongoItemWriter<NovelCards> writer() throws Exception {
        try {
            return new MongoItemWriterBuilder<NovelCards>()
                .template(mongoTemplate)
                .collection("novel_cards")
                .build();
        } catch(EmptyResultDataAccessException e) {
            throw new RuntimeException(e);
        }

    }

}
