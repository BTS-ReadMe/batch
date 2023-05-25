package com.readme.batch.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NovelViewDTO {
    private long novelId;
    private long views;

    public NovelViewDTO(long novelId, long views) {
        this.novelId = novelId;
        this.views = views;
    }
}
