package com.readme.batch.service;

import com.readme.batch.model.NovelViews;
import com.readme.batch.responseObject.ResponseRanking;
import java.util.List;
import java.util.Map;

public interface NovelViewsService {
    public List<ResponseRanking> getRanking();
}
