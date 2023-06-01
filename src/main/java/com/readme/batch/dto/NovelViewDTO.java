package com.readme.batch.dto;

import com.readme.batch.requestObject.RequestPlusViewCount;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NovelViewDTO {
    private long novelId;
    private long episodeId;
    private long plusCnt;

    public NovelViewDTO(RequestPlusViewCount requestPlusViewCount) {
        this.novelId = requestPlusViewCount.getNovelId();
        this.episodeId = requestPlusViewCount.getEpisodeId();
        this.plusCnt = requestPlusViewCount.getPlusCnt();
    }
}
