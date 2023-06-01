package com.readme.batch.projection;

import java.time.LocalDateTime;

public interface INovelViews {

    Long getId();
    String getTitle();
    String getThumbnail();
    String getAuthor();
    String getGenre();
    Integer getGrade();
    String getSerialization_Status();
    LocalDateTime getViews_Date();
    Integer getRanking();

}

