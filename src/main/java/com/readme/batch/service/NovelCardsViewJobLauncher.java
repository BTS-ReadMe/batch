package com.readme.batch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readme.batch.dto.NovelViewDTO;
import com.readme.batch.dto.ViewCountDTO;
import com.readme.batch.requestObject.RequestPlusViewCount;
import java.io.IOException;
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
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "plusViewCount", groupId = "batch")
    public void plusViewCount(RequestPlusViewCount requestPlusViewCount) {
        ViewCountDTO viewCountDTO = novelViewCountMap.getOrDefault(requestPlusViewCount.getNovelId(), new ViewCountDTO(
                requestPlusViewCount.getEpisodeId(), 0));
        novelViewCountMap.put(requestPlusViewCount.getNovelId(),
            new ViewCountDTO(requestPlusViewCount.getEpisodeId(), viewCountDTO.getViewCount()
                + requestPlusViewCount.getPlusCnt()));
    }

    public void plusViewJob(String plusViewString) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
            .addString("novelViewsMapStr", plusViewString)
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
        log.info("종료 시간: " + new Date());

    }

    @Scheduled(fixedRate = 60000)
    public void listener() throws Exception {
        Map<Long, ViewCountDTO> currentMessageCountMap = new HashMap<>(novelViewCountMap);
        if (!currentMessageCountMap.isEmpty()) {
            log.info(currentMessageCountMap.toString());
            log.info("시작 시간: " + new Date());
            String novelViewsMapStr = serializeNovelViewsMap(currentMessageCountMap);
            novelViewCountMap.clear();
            JobParameters jobParameters = new JobParametersBuilder()
                .addString("novelViewsMapStr", novelViewsMapStr)
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
            log.info("종료 시간: " + new Date());
        }

    }

    public String serializeNovelViewsMap(Map<Long, ViewCountDTO> novelViewsMap) throws JsonProcessingException {
        return objectMapper.writeValueAsString(novelViewsMap);
    }

    public static Map<Long, ViewCountDTO> deserializeNovelViewsMap(String novelViewsMapStr) throws IOException {
        return objectMapper.readValue(novelViewsMapStr,
            new TypeReference<Map<Long, ViewCountDTO>>() {
            });
    }
}

