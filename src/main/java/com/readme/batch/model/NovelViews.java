package com.readme.batch.model;

import com.readme.batch.utils.BaseTimeEntity;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(name = "novelViews")
public class NovelViews extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "novelId")
    private Long novelId;
    @Column(name = "viewsDate")
    private LocalDateTime viewsDate;
    @Column(name = "views")
    private Long views;

    public NovelViews(Long novelId, LocalDateTime viewsDate, Long views) {
        this.novelId = novelId;
        this.viewsDate = viewsDate;
        this.views = views;
    }
}
