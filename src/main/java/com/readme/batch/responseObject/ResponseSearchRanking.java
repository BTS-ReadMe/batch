package com.readme.batch.responseObject;

import com.readme.batch.projection.ISearch;
import com.readme.batch.service.NovelViewsServiceImpl;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseSearchRanking {
    private String searchDate;
    private List<SearchRankingData> searchRankingData;

    @Getter
    @NoArgsConstructor
    public static class SearchRankingData {
        private String keyword;
        private Integer ranking;
        private Integer changeRanking;

        public SearchRankingData(ISearch iSearch, Integer changeRanking) {
            this.keyword = iSearch.getKeyword();
            this.ranking = iSearch.getRanking();
            this.changeRanking = changeRanking;
        }
    }

    public ResponseSearchRanking(List<SearchRankingData> searchRankingData) {
        this.searchDate = NovelViewsServiceImpl.getUtcToKoreanTime();
        this.searchRankingData = searchRankingData;
    }
}
