package com.readme.batch.model;

import com.readme.batch.utils.BaseTimeEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "search")
public class Search extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "keyword")
    String keyword;
    @Column(name = "count")
    Long count;
    @Column(name = "search_date")
    private LocalDateTime searchDate;

    public Search(String keyword, Long count, LocalDateTime searchDate) {
        this.keyword = keyword;
        this.count = count;
        this.searchDate = searchDate;
    }
}
