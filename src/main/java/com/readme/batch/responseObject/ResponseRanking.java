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
    private LocalDateTime viewsDate;
    private Long views;
    private Integer ranking;

    public ResponseRanking(Long novelId, String title, String thumbnail, LocalDateTime viewsDate,
        Long views, Integer ranking) {
        this.novelId = novelId;
        this.title = title;
        this.thumbnail = thumbnail;
        this.viewsDate = viewsDate;
        this.views = views;
        this.ranking = ranking;
    }
}
