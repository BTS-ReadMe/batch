package com.readme.batch.controller;

import com.readme.batch.commonResponseObject.CommonDataResponse;
import com.readme.batch.model.NovelViews;
import com.readme.batch.repository.NovelViewsRepository;
import com.readme.batch.responseObject.ResponseRanking;
import com.readme.batch.responseObject.ResponseSearchRanking;
import com.readme.batch.service.NovelViewsService;
import com.readme.batch.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/rankings")
public class RankinController {
    private final NovelViewsService novelViewsService;
    private final SearchService searchService;
    @Operation(summary = "소설 랭킹 조회", description = "매 시 정각마다 소설 랭킹 15개를 띄웁니다.", tags = {"랭킹"})
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
        @ApiResponse(responseCode = "404", description = "NOT FOUND"),
        @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @GetMapping
    public ResponseEntity<CommonDataResponse<ResponseRanking>> getRanking() {
        return ResponseEntity.ok(new CommonDataResponse(novelViewsService.getRanking()));
    }

    @Operation(summary = "실시간 검색어 랭킹 조회", description = "1시간 마다의 실시간 검색어 랭킹 조회", tags = {"랭킹"})
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
        @ApiResponse(responseCode = "404", description = "NOT FOUND"),
        @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @GetMapping("search")
    public ResponseEntity<CommonDataResponse<ResponseSearchRanking>> getSearchRanking() {
        return ResponseEntity.ok(new CommonDataResponse(searchService.getSearchRanking()));
    }
}
