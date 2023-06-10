package com.readme.batch.service;

import com.readme.batch.model.SearchData;
import com.readme.batch.responseObject.ResponseSearchRanking;

public interface SearchService {
    ResponseSearchRanking getSearchRanking();
    void saveSearchData(String keyword);
}
