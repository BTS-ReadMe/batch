package com.readme.batch.service;

import com.readme.batch.model.SearchData;
import com.readme.batch.projection.ISearch;
import com.readme.batch.repository.SearchDataRepository;
import com.readme.batch.responseObject.ResponseSearchRanking;
import com.readme.batch.responseObject.ResponseSearchRanking.SearchRankingData;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService{
    private final SearchDataRepository searchDataRepository;

    @Override
    public ResponseSearchRanking getSearchRanking() {
        LocalDateTime nowWithoutMinutesAndSeconds = LocalDateTime.now().withMinute(0).withSecond(0)
            .withNano(0);
        LocalDateTime oneHourAgo = nowWithoutMinutesAndSeconds.minusHours(1);
        LocalDateTime twoHourAgo = nowWithoutMinutesAndSeconds.minusHours(2);
        List<ISearch> oneAgoList = searchDataRepository.findTop10(oneHourAgo);
        List<ISearch> twoAgoList = searchDataRepository.findTop10(twoHourAgo);
        Map<String, Integer> previousMap = twoAgoList.stream()
            .collect(Collectors.toMap(ISearch::getKeyword, ISearch::getRanking));
        return new ResponseSearchRanking(oneAgoList.stream()
            .map(current -> new SearchRankingData(current, previousMap.getOrDefault(current.getKeyword(), null) != null ?
                previousMap.get(current.getKeyword()) - current.getRanking() : null))
            .collect(Collectors.toList()));
    }
}
