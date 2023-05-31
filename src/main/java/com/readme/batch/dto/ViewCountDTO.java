package com.readme.batch.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ViewCountDTO {
    private long episodeId;
    private long viewCount;

    public ViewCountDTO(long episodeId, long viewCount) {
        this.episodeId = episodeId;
        this.viewCount = viewCount;
    }

    public void plusView() {
        this.viewCount += 1;
    }
}
