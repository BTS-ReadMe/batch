package com.readme.batch.service;

import com.readme.batch.model.Episodes;
import com.readme.batch.model.NovelCards;
import com.readme.batch.model.NovelViews;
import com.readme.batch.repository.EpisodesRepository;
import com.readme.batch.repository.NovelViewsRepository;
import java.io.IOException;
import java.time.LocalDateTime;
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
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.EmptyResultDataAccessException;
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
    private final EpisodesRepository episodesRepository;
    private final NovelViewsRepository novelViewsRepository;

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        return executor;
    }

    @Bean
    public Job novelCardsViewsJob(Flow episodeViewsFlow) throws Exception {
        return jobBuilderFactory.get("ViewsJob")
            .start(doItParallelSteps())
            .build().build();
    }

    @Bean Flow novelCardsViewsFlow() throws Exception {
        return new FlowBuilder<SimpleFlow>("novelCardsViewsFlow")
            .start(novelCardsViewsStep())
            .build();
    }

    @Bean
    public Flow episodeViewsFlow() throws Exception {
        return new FlowBuilder<SimpleFlow>("episodeViewsFlow")
            .start(episodeViewsStep())
            .build();
    }

    @Bean
    public Flow rankingViewsFlow() throws Exception {
        return new FlowBuilder<SimpleFlow>("rankingViewsFlow")
            .start(RankingViewsStep())
            .build();
    }

    @Bean Flow doItParallelSteps() throws Exception {
        return new FlowBuilder<Flow>("doItParallelSteps")
            .split(new SimpleAsyncTaskExecutor())
            .add(novelCardsViewsFlow(), episodeViewsFlow(), rankingViewsFlow()) // 동시에 실행될 flow 들을 넣어줍니다.
            .build();
    }

    @Bean
    @Scope("singleton")
    public Step novelCardsViewsStep() throws Exception {
        try {
            return stepBuilderFactory.get("novelCardsViewsStep")
                .<Map.Entry<Long, Long>, NovelCards>chunk(
                    500) // 앞의 NovelCards는 read에서 읽은 아이템의 타입, 뒤의 NovelCards는 write에게 전달할 아이템의 타입
                .reader(novelCardsReader(null))
                .processor(novelCardsProcessor())
                .writer(novelCardsWriter())
                .taskExecutor(taskExecutor())
                .build();
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Bean
    @Scope("singleton")
    public Step episodeViewsStep() throws Exception {
        try {
            return stepBuilderFactory.get("episodeViewsStep")
                .<Map.Entry<Long, Long>, Episodes>chunk(500)
                .reader(episodeCardsReader(null))
                .processor(episodesProcessor())
                .writer(episodesWriter())
                .taskExecutor(taskExecutor())
                .build();
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    @StepScope
    public ItemReader<Map.Entry<Long, Long>> novelCardsReader(
        @Value("#{jobParameters['novelViewsMapStr']}") String novelViewsMapStr) throws IOException {
        Map<Long, Long> novelViewsMap = NovelCardsViewJobLauncher.deserializeNovelViewsMap(
            novelViewsMapStr);
        return new IteratorItemReader<>(novelViewsMap.entrySet().iterator());
    }

    @Bean
    @StepScope
    public ItemReader<Map.Entry<Long, Long>> episodeCardsReader(
        @Value("#{jobParameters['episodeViewsMapStr']}") String episodeViewsMapStr)
        throws IOException {
        Map<Long, Long> episodeViewsMap = NovelCardsViewJobLauncher.deserializeNovelViewsMap(
            episodeViewsMapStr);
        return new IteratorItemReader<>(episodeViewsMap.entrySet().iterator());
    }

    @Bean
    @StepScope
    public ItemProcessor<Map.Entry<Long, Long>, NovelCards> novelCardsProcessor() {
        return item -> {
            Long novelId = item.getKey();
            Long viewCount = item.getValue();
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(String.valueOf(novelId)));
            NovelCards novelCards = mongoTemplate.findOne(query, NovelCards.class, "novel_cards");
            novelCards.setViews(novelCards.getViews() + viewCount);
            return novelCards;
        };
    }

    @Bean
    @StepScope
    public ItemProcessor<Map.Entry<Long, Long>, Episodes> episodesProcessor() {
        return item -> {
            Long episodeId = item.getKey();
            Long viewCount = item.getValue();
            Episodes episodes = episodesRepository.findById(episodeId).get();
            episodes.setViews(episodes.getViews() + viewCount);
            return episodes;
        };
    }

    @Bean
    @StepScope
    public ItemWriter<NovelCards> novelCardsWriter() throws Exception {
        return new MongoItemWriterBuilder<NovelCards>()
            .template(mongoTemplate)
            .collection("novel_cards")
            .build();
    }

    @Bean
    @StepScope
    public ItemWriter<Episodes> episodesWriter() throws Exception {
        return new ItemWriter<Episodes>() {
            @Override
            public void write(List<? extends Episodes> items) throws Exception {
                episodesRepository.saveAll(items);
            }
        };
    }

    @Bean
    @Scope("singleton")
    public Step RankingViewsStep() throws Exception {
        try {
            return stepBuilderFactory.get("RankingViewsStep")
                .<Map.Entry<Long, Long>, NovelViews>chunk(
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
    public ItemReader<Entry<Long, Long>> novelViewsReader(
        @Value("#{jobParameters['rankingViewsMapStr']}") String novelViewsMapStr) throws IOException {
        Map<Long, Long> novelViewsMap = NovelCardsViewJobLauncher.deserializeNovelViewsMap(
            novelViewsMapStr);
        return new IteratorItemReader<>(novelViewsMap.entrySet().iterator());
    }

    @Bean
    @StepScope
    public ItemProcessor<Entry<Long, Long>, NovelViews> novelViewsProcessor() {
        return item -> {
            Long novelId = item.getKey();
            Long viewCount = item.getValue();
            NovelViews novelViews = null;
            LocalDateTime timeWithoutMinutesAndSeconds = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            if (novelViewsRepository.existsByNovelIdAndViewsDate(novelId, timeWithoutMinutesAndSeconds)) {
                novelViews = novelViewsRepository.findByNovelIdAndViewsDate(novelId, timeWithoutMinutesAndSeconds);
            } else {
                novelViews = new NovelViews(novelId, timeWithoutMinutesAndSeconds, 0L);
            }
            novelViews.setViews(novelViews.getViews() + viewCount);
            return novelViews;
        };
    }

    @Bean
    @StepScope
    public ItemWriter<NovelViews> novelViewsWriter() throws Exception {
        return new ItemWriter<NovelViews>() {
            @Override
            public void write(List<? extends NovelViews> items) throws Exception {
                novelViewsRepository.saveAll(items);
            }
        };
    }
}