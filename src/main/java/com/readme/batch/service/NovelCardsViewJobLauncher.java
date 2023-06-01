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
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class NovelCardsViewJobLauncher {

    private Map<Long, Long> novelViewCount = new HashMap<>();
    private Map<Long, Long> episodeViewCount = new HashMap<>();
    private Map<Long, NovelViewDTO> novelViewsDataCount = new HashMap<>();
    private final JobLauncher jobLauncher;
    private final NovelCardsViewsJobService novelCardsViewsJobService;
    private final NovelViewsDataJobService novelViewsDataJobService;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "plusViewCount", groupId = "batch")
    public void plusViewCount(RequestPlusViewCount requestPlusViewCount) {
        novelViewCount.put(requestPlusViewCount.getNovelId(),
            novelViewCount.getOrDefault(requestPlusViewCount.getNovelId(), 0L)
                + requestPlusViewCount.getPlusCnt());

        episodeViewCount.put(requestPlusViewCount.getEpisodeId(),
            episodeViewCount.getOrDefault(requestPlusViewCount.getEpisodeId(), 0L)
                + requestPlusViewCount.getPlusCnt());

        updateNovelViewsDataCount(requestPlusViewCount);
//        novelViewsDataCount.getOrDefault(requestPlusViewCount.getNovelId(),
//            new NovelViewDTO(requestPlusViewCount));
//        novelViewsDataCount.get(requestPlusViewCount.getNovelId())
//            .setPlusCnt(novelViewsDataCount.get(requestPlusViewCount.getNovelId()).getPlusCnt() +
//                requestPlusViewCount.getPlusCnt());
//        novelViewsDataCount.put(requestPlusViewCount.getNovelId(),
//            novelViewsDataCount.get(requestPlusViewCount.getNovelId()));
    }

    public void plusViewJob(String plusViewString) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
            .addString("novelViewsMapStr", plusViewString)
            .addLong("timestamp", System.currentTimeMillis())
            .toJobParameters();
        JobExecution jobExecution = jobLauncher.run(
            novelCardsViewsJobService.novelCardsViewsJob(null), jobParameters);
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
        Map<Long, Long> currentNovelViews = new HashMap<>(novelViewCount);
        Map<Long, Long> currentEpisodeViews = new HashMap<>(episodeViewCount);
        if (!currentNovelViews.isEmpty()) {
            log.info("시작 시간: " + new Date());
            String novelViewsMapStr = serializeNovelViewsMap(novelViewCount);
            String episodeViewsMapStr = serializeNovelViewsMap(episodeViewCount);
            novelViewCount.clear();
            episodeViewCount.clear();
            JobParameters jobParameters = new JobParametersBuilder()
                .addString("novelViewsMapStr", novelViewsMapStr)
                .addString("episodeViewsMapStr", episodeViewsMapStr)
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
            JobExecution jobExecution = jobLauncher.run(
                novelCardsViewsJobService.novelCardsViewsJob(null), jobParameters);
        }
    }

//    @Scheduled(cron = "0 0 * * * ?")
    @Scheduled(fixedRate = 10000)
    public void rankingJobLauncher() throws Exception {
        Map<Long, NovelViewDTO> currentNovelViewsData = new HashMap<>(novelViewsDataCount);
        if (!currentNovelViewsData.isEmpty()) {
            String novelViewsDataString = serializeNovelViewsDataMap(novelViewsDataCount);
            novelViewsDataCount.clear();
            JobParameters jobParameters = new JobParametersBuilder()
                .addString("novelViewsData", novelViewsDataString)
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
            JobExecution jobExecution = jobLauncher.run(
                novelViewsDataJobService.novelViewsDataJob(), jobParameters);
        }
    }

    public void updateNovelViewsDataCount(RequestPlusViewCount request) {
        long novelId = request.getNovelId();

        if (novelViewsDataCount.containsKey(novelId)) {
            NovelViewDTO existingDTO = novelViewsDataCount.get(novelId);
            existingDTO.setPlusCnt(existingDTO.getPlusCnt() + request.getPlusCnt());
        } else {
            NovelViewDTO newDTO = new NovelViewDTO();
            newDTO.setTitle(request.getTitle());
            newDTO.setThumbnail(request.getThumbnail());
            newDTO.setPlusCnt(request.getPlusCnt());

            novelViewsDataCount.put(novelId, newDTO);
        }
    }

    public String serializeNovelViewsMap(Map<Long, Long> novelViewsMap)
        throws JsonProcessingException {
        return objectMapper.writeValueAsString(novelViewsMap);
    }

    public String serializeNovelViewsDataMap(Map<Long, NovelViewDTO> novelViewsMap)
        throws JsonProcessingException {
        return objectMapper.writeValueAsString(novelViewsMap);
    }

    public static Map<Long, Long> deserializeNovelViewsMap(String novelViewsMapStr)
        throws IOException {
        return objectMapper.readValue(novelViewsMapStr,
            new TypeReference<Map<Long, Long>>() {
            });
    }

    public static Map<Long, NovelViewDTO> deserializeNovelViewsDataMap(String novelViewsMapStr)
        throws IOException {
        return objectMapper.readValue(novelViewsMapStr,
            new TypeReference<Map<Long, NovelViewDTO>>() {
            });
    }
}

