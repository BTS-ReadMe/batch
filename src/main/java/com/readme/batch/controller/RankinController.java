package com.readme.batch.controller;

import com.readme.batch.commonResponseObject.CommonDataResponse;
import com.readme.batch.model.NovelViews;
import com.readme.batch.repository.NovelViewsRepository;
import com.readme.batch.responseObject.ResponseRanking;
import com.readme.batch.service.NovelViewsService;
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
    @Operation(summary = "랭킹 조회", description = "매 시 정각마다 소설 랭킹 15개를 띄웁니다.", tags = {"랭킹"})
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
}
