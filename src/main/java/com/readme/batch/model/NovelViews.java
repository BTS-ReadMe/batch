package com.readme.batch.model;

import com.readme.batch.utils.BaseTimeEntity;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class NovelViews extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long novelId;
    private String title;
    private String thumbnail;
    private Date viewsDate;
    private Long views;

    public NovelViews(Long novelId, Date viewsDate, Long views) {
        this.novelId = novelId;
        this.viewsDate = viewsDate;
        this.views = views;
    }
}
