package com.readme.batch.responseObject;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseRanking {
    private Long novelId;
    private String title;
    private String thumbnail;
    private String viewsDate;
    private Integer ranking;
    private Integer changeRanking;

    public ResponseRanking(Long novelId, String title, String thumbnail, String viewsDate,
        Integer ranking, Integer changeRanking) {
        this.novelId = novelId;
        this.title = title;
        this.thumbnail = thumbnail;
        this.viewsDate = viewsDate;
        this.ranking = ranking;
        this.changeRanking = changeRanking;
    }
}
