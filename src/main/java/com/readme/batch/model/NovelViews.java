package com.readme.batch.model;

import com.readme.batch.utils.BaseTimeEntity;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class NovelViews extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long novelId;
    private String title;
    private String thumbnail;
    private LocalDateTime viewsDate;
    private Long views;

    public NovelViews(Long novelId, String title, String thumbnail, LocalDateTime viewsDate, Long views) {
        this.novelId = novelId;
        this.title = title;
        this.thumbnail = thumbnail;
        this.viewsDate = viewsDate;
        this.views = views;
    }
}
