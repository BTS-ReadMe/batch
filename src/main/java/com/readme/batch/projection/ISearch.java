package com.readme.batch.projection;

import java.time.LocalDateTime;

public interface ISearch {
    String getKeyword();
    LocalDateTime getSearch_Date();
    Integer getRanking();

}
