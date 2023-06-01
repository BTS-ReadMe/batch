package com.readme.batch.service;

import com.readme.batch.projection.INovelViews;
import com.readme.batch.repository.NovelViewsRepository;
import com.readme.batch.responseObject.ResponseRanking;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NovelViewsServiceImpl implements NovelViewsService{
    private final NovelViewsRepository novelViewsRepository;

    @Override
    public List<ResponseRanking> getRanking() {
        LocalDateTime timeWithoutMinutesAndSeconds = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        List<INovelViews> iNovelViewsList = novelViewsRepository.findAllByViewsDateWithRank(timeWithoutMinutesAndSeconds);
        return iNovelViewsList.stream()
            .map(iNovelViews -> new ResponseRanking(iNovelViews.getNovel_Id(), iNovelViews.getTitle(), iNovelViews.getThumbnail(), iNovelViews.getViews_Date(), iNovelViews.getViews(), iNovelViews.getRanking()))
            .collect(Collectors.toList());
    }
}
