package com.readme.batch.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchDTO {
    private String keyword;
    private Long count;
    private LocalDateTime searchDate;
    private Integer ranking;
}
