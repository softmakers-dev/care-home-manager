package com.softmakers.manager_service.resource;

import com.softmakers.manager_domain.entity.User;
import com.softmakers.manager_domain.entity.dto.search.SearchDto;
import com.softmakers.manager_domain.spec.SearchService;
import com.softmakers.result.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.softmakers.result.ResultCode.GET_RECOMMEND_MEMBER_SUCCESS;
import static com.softmakers.result.ResultCode.SEARCH_SUCCESS;

@Slf4j
@Validated
@Tag(name = "검색 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/topsearch")
public class SearchController {

    private final SearchService searchService;

    @Operation(
            description = "검색",
            parameters = {
                    @Parameter(
                            name = "text",
                            description = "검색내용", // Replaces the old 'value'
                            required = true,
                            in = ParameterIn.QUERY, // Specify it is a query parameter
                            example = "dlwl"
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SE001 - 검색에 성공하였습니다.\n"),
            @ApiResponse(responseCode = "400", description = "G003 - 유효하지 않은 입력입니다.\n"
                    + "G004 - 입력 타입이 유효하지 않습니다.\n"),
            @ApiResponse(responseCode = "401", description = "M003 - 로그인이 필요한 화면입니다.")
    })
    @GetMapping
    public ResponseEntity<ResultResponse> searchText(@RequestParam("text") String text) {
        final List<SearchDto> searchDtos = searchService.searchByText(text);

        return ResponseEntity.ok(ResultResponse.of(SEARCH_SUCCESS, searchDtos));
    }

    @Operation(description = "팔로잉 멤버 추천")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SE009 - 팔로잉 추천 멤버 조회에 성공하였습니다.\n"),
            @ApiResponse(responseCode = "401", description = "M003 - 로그인이 필요한 화면입니다.")
    })
    @GetMapping("/recommend")
    public ResponseEntity<ResultResponse> getRecommendMembers() {
        final List<User> searchDtos = searchService.getRecommendUsers();
        return ResponseEntity.ok(ResultResponse.of(GET_RECOMMEND_MEMBER_SUCCESS, searchDtos));
    }
}
