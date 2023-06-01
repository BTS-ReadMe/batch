package com.readme.batch.dto;

import com.readme.batch.requestObject.RequestPlusViewCount;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class NovelViewDTO {
    private String title;
    private String thumbnail;
    private long plusCnt;

    public NovelViewDTO(RequestPlusViewCount requestPlusViewCount) {
        this.title = requestPlusViewCount.getTitle();
        this.thumbnail = requestPlusViewCount.getThumbnail();
        this.plusCnt = requestPlusViewCount.getPlusCnt();
    }
}
