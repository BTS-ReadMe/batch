package com.readme.batch.service;

import com.readme.batch.model.NovelViews;
import com.readme.batch.projection.INovelViews;
import com.readme.batch.repository.NovelViewsRepository;
import com.readme.batch.responseObject.ResponseRanking;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class NovelViewsServiceImpl implements NovelViewsService {

    private final NovelViewsRepository novelViewsRepository;

    public List<ResponseRanking> getRanking() {
        LocalDateTime nowWithoutMinutesAndSeconds = LocalDateTime.now().withMinute(0).withSecond(0)
            .withNano(0);
        LocalDateTime oneHourAgo = nowWithoutMinutesAndSeconds.minusHours(1);
        List<INovelViews> currentList = novelViewsRepository.findAllByViewsDateWithRank(
            nowWithoutMinutesAndSeconds);
        List<INovelViews> previousList = novelViewsRepository.findAllByViewsDateWithRank(
            oneHourAgo);

        Map<Long, Integer> previousMap = previousList.stream()
            .collect(Collectors.toMap(INovelViews::getNovel_Id, INovelViews::getRanking));

        return currentList.stream()
            .map(current -> new ResponseRanking(current.getNovel_Id(), current.getTitle(),
                current.getThumbnail(), getUtcToKoreanTime(current.getViews_Date()), current.getRanking(),
                previousMap.getOrDefault(current.getNovel_Id(), null) != null ?
                    previousMap.get(current.getNovel_Id()) - current.getRanking() : null))
            .collect(Collectors.toList());
    }

    public static String getUtcToKoreanTime(LocalDateTime utcTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return formatter.format(utcTime);
    }
}
