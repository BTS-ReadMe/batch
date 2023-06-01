package com.readme.batch.responseObject;

import com.readme.batch.projection.INovelViews;
import com.readme.batch.service.NovelViewsService;
import com.readme.batch.service.NovelViewsServiceImpl;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseRanking {
    private String viewsDate;
    private List<NovelRankingData> novelRankingData;
    @Getter
    @NoArgsConstructor
    public static class NovelRankingData{
        private Long novelId;
        private String title;
        private String author;
        private String genre;
        private Integer grade;
        private String serializationStatus;
        private String thumbnail;
        private Integer ranking;
        private Integer changeRanking;

        public NovelRankingData(INovelViews iNovelViews, Integer changeRanking) {
            this.novelId = iNovelViews.getId();
            this.title = iNovelViews.getTitle();
            this.author = iNovelViews.getAuthor();
            this.genre = iNovelViews.getGenre();
            this.grade = iNovelViews.getGrade();
            this.serializationStatus = iNovelViews.getSerialization_Status();
            this.thumbnail = iNovelViews.getThumbnail();
            this.ranking = iNovelViews.getRanking();
            this.changeRanking = changeRanking;
        }
    }
    public ResponseRanking(List<NovelRankingData> novelRankingData) {
        this.viewsDate = NovelViewsServiceImpl.getUtcToKoreanTime();
        this.novelRankingData = novelRankingData;
    }

}
