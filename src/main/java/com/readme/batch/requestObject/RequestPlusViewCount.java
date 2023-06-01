package com.readme.batch.requestObject;

import lombok.Getter;

@Getter
public class RequestPlusViewCount {
    private long novelId;
    private long episodeId;
    private String title;
    private String thumbnail;
    private long plusCnt;
}
