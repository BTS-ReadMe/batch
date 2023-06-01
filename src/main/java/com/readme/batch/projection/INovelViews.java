package com.readme.batch.projection;

import java.time.LocalDateTime;

public interface INovelViews {

    Long getNovel_Id();
    String getTitle();
    String getThumbnail();
    LocalDateTime getViews_Date();
    Long getViews();
    Integer getRanking();

}

