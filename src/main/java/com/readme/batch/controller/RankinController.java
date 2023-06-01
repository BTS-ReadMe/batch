package com.readme.batch.controller;

import com.readme.batch.model.NovelViews;
import com.readme.batch.repository.NovelViewsRepository;
import com.readme.batch.responseObject.ResponseRanking;
import com.readme.batch.service.NovelViewsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/rankings")
public class RankinController {
    private final NovelViewsService novelViewsService;
    @GetMapping
    public List<ResponseRanking> getRanking() {
        return novelViewsService.getRanking();
    }
}
