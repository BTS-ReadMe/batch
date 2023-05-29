package com.readme.batch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readme.batch.dto.NovelViewDTO;
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

@RequiredArgsConstructor
@Configuration
@Slf4j
public class NovelCardsViewJobLauncher {

    private final JobLauncher jobLauncher;
    private final NovelCardsViewsJobService novelCardsViewsJobService;

//    @KafkaListener(topics = "test_topic", groupId = "foo")
    public void listener(String kafkaMessage) {
        kafkaMessage = "{\"1\":2,\"2\":2,\"3\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8,\"4\":5,\"5\":6,\"6\":7,\"7\":8}";
        Map<String, Object> kafkaMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            kafkaMap = mapper.readValue(kafkaMessage, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info(kafkaMap.toString());
        kafkaMap.forEach((novelId, viewsCount) -> {
            Map<String, JobParameter> jobParameterMap = new HashMap<>();
            log.info("map forEach: " + novelId + viewsCount);
            JobParameters jobParameters = new JobParametersBuilder()
                .addLong("novelId", Long.valueOf(novelId))
                .addLong("views", Long.valueOf(String.valueOf(viewsCount)))
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
            try {
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
