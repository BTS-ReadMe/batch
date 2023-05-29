package com.readme.batch.model;

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "novel_cards")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class NovelCards {

    @Id
    private String novelId;
    @Indexed
    private String title;
    private String description;
    private String author;
    private String authorComment;
    private String genre;
    private Integer grade;
    private String thumbnail;
    private Date startDate;
    private Long views;
    private String serializationStatus;
    private List<Tag> tags;
    private Long scheduleId;
    private Double starRating;
    private Boolean monday;
    private Boolean tuesday;
    private Boolean wednesday;
    private Boolean thursday;
    private Boolean friday;
    private Boolean saturday;
    private Boolean sunday;
    private Long episodeCount;

    @Getter
    public static class Tag {

        private Long id;
        private String name;
    }

    public void updateViewCount(long views) {
        this.views += views;
    }
}
