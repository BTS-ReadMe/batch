package com.readme.batch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readme.batch.dto.NovelViewDTO;
import com.readme.batch.dto.ViewCountDTO;
import com.readme.batch.requestObject.RequestPlusViewCount;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class NovelCardsViewJobLauncher {

    private Map<Long, ViewCountDTO> novelViewCountMap = new HashMap<>();
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final JobLauncher jobLauncher;
    private final NovelCardsViewsJobService novelCardsViewsJobService;

    @KafkaListener(topics = "plusViewCount", groupId = "batch")
    public void plusViewCount(RequestPlusViewCount requestPlusViewCount) {
        ViewCountDTO viewCountDTO = novelViewCountMap.getOrDefault(requestPlusViewCount.getNovelId(), new ViewCountDTO(
                requestPlusViewCount.getEpisodeId(), 0));
        novelViewCountMap.put(requestPlusViewCount.getNovelId(),
            new ViewCountDTO(requestPlusViewCount.getEpisodeId(), viewCountDTO.getViewCount()
                + requestPlusViewCount.getPlusCnt()));
    }

    @Scheduled(fixedRate = 15000)
    public void listener() {
        Map<Long, ViewCountDTO> currentMessageCountMap = new HashMap<>(novelViewCountMap);
        log.info(currentMessageCountMap.toString());
        novelViewCountMap.clear();

        currentMessageCountMap.forEach((novelId, viewsCountDTO) -> {
            try {
                JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("novelId", novelId)
                    .addLong("episodeId", viewsCountDTO.getEpisodeId())
                    .addLong("views", viewsCountDTO.getViewCount())
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();
                JobExecution jobExecution = jobLauncher.run(
                    novelCardsViewsJobService.NovelCardsViewsJob(), jobParameters);
                log.info("Job Execution: " + jobExecution.getStatus());
                log.info("Job getJobConfigurationName: " + jobExecution.getJobConfigurationName());
                log.info("Job getJobId: " + jobExecution.getJobId());
                log.info("Job getExitStatus: " + jobExecution.getExitStatus());
                log.info("Job getJobInstance: " + jobExecution.getJobInstance());
                log.info("Job getStepExecutions: " + jobExecution.getStepExecutions());
                log.info("Job getLastUpdated: " + jobExecution.getLastUpdated());
                log.info("Job getFailureExceptions: " + jobExecution.getFailureExceptions());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });
    }
}
