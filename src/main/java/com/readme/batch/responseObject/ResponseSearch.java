package com.readme.batch.responseObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class ResponseSearch {
    private String keyword;
    private long count;

    public ResponseSearch(String keyword, long count) {
        this.keyword = keyword;
        this.count = count;
    }
}
