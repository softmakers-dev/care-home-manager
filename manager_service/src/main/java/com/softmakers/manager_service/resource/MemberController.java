package com.softmakers.manager_service.resource;

import com.softmakers.manager_domain.entity.User;
import com.softmakers.manager_domain.entity.dto.MemberProfile;
import com.softmakers.manager_domain.entity.feed.MemberPostDto;
import com.softmakers.manager_domain.lifecycle.ServiceLifecycle;
import com.softmakers.manager_domain.spec.MemberPostService;
import com.softmakers.manager_domain.spec.UserService;
import com.softmakers.result.ResultCode;
import com.softmakers.result.ResultResponse;
import com.softmakers.utilities.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Tag(name = "Member API")
@RestController
@RequestMapping( "/accounts" )
public class MemberController {

    private static final int FIRST_PAGE_SIZE_FOR_PROFILE = 15;
    private static final int FIRST_PAGE_SIZE_FOR_POST = 6;
    private static final int PAGE_SIZE_FOR_PROFILE = 3;
    private static final int PAGE_OFFSET_FOR_PROFILE = 4;

    private UserService userService;
    private MemberPostService memberPostService;
    private final ServiceLifecycle serviceLifecycle;
    private final AuthUtil authUtil;

    public MemberController(ServiceLifecycle serviceLifecycle, AuthUtil authUtil) {
        this.serviceLifecycle = serviceLifecycle;
        this.userService = this.serviceLifecycle.requestUserService();
        this.memberPostService = this.serviceLifecycle.requestMemberPostService();
        this.authUtil = authUtil;
    }

    @Operation(summary = "상단 메뉴 로그인한 유저 프로필 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "M016 - 상단 메뉴 프로필을 조회하였습니다."),
            @ApiResponse(responseCode = "401", description = "M003 - 로그인이 필요한 화면입니다.")
    })
    @GetMapping(value = "/profile")
    public ResponseEntity<ResultResponse> getMemberProfile() {
        Long userId = authUtil.getLoginUserId();
        BigDecimal dbId = BigDecimal.valueOf(userId);

        User user = this.userService.findUserById(dbId);
        MemberProfile memberProfile = new MemberProfile(user);
//        memberProfile.setMemberImageUrl("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?q=80&w=987&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");

        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_USERPROFILE_SUCCESS, memberProfile));
    }

    @Operation(summary = "유저 프로필 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "M004 - 회원 프로필을 조회하였습니다."),
            @ApiResponse(responseCode = "400", description = "G003 - 유효하지 않은 입력입니다.\n" +
                    "G004 - 입력 타입이 유효하지 않습니다.\n" +
                    "M001 - 존재 하지 않는 유저입니다."),
            @ApiResponse(responseCode = "401", description = "M003 - 로그인이 필요한 화면입니다.")
    })
    @GetMapping(value = "/{username}")
    public ResponseEntity<ResultResponse> getUserProfile(@PathVariable("username") String username) {
        User user = this.userService.findUserByUsername(username);

        MemberProfile memberProfile = new MemberProfile(user);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_USERPROFILE_SUCCESS, memberProfile));
    }

    @Operation( description = "멤버 게시물 15개 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "MP001 - 회원의 최근 게시물 15개 조회에 성공하였습니다."),
            @ApiResponse(responseCode = "400", description = "G003 - 유효하지 않은 입력입니다.\n"
                    + "G004 - 입력 타입이 유효하지 않습니다.\n"
                    + "M001 - 존재 하지 않는 유저입니다."),
            @ApiResponse(responseCode = "401", description = "F003 - 로그인이 필요한 화면입니다.")
    })
    @Parameters({
            @Parameter(name = "username", description = "유저네임", example = "Kim", required = true),
    })
    @GetMapping("/{username}/posts/recent")
    public ResponseEntity<ResultResponse> getRecent15Posts(@PathVariable("username") String username) {
        final List<MemberPostDto> postList = memberPostService.getMemberPostDtoPage(username,
                FIRST_PAGE_SIZE_FOR_PROFILE, 0).getContent();

        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_RECENT15_MEMBER_POSTS_SUCCESS, postList));
    }

    @Operation( description = "멤버 게시물 6개 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "MP007 - 회원의 최근 게시물 6개 조회에 성공하였습니다."),
            @ApiResponse(responseCode = "400", description = "G003 - 유효하지 않은 입력입니다.\n"
                    + "G004 - 입력 타입이 유효하지 않습니다.\n"
                    + "M001 - 존재 하지 않는 유저입니다."),
            @ApiResponse(responseCode = "401", description = "F003 - 로그인이 필요한 화면입니다.")
    })
    @Parameters({
            @Parameter(name = "username", description = "유저네임", example = "Kim", required = true),
    })
    @GetMapping("/{username}/posts/recent/post")
    public ResponseEntity<ResultResponse> getRecent6Posts(@PathVariable("username") String username) {
        final List<MemberPostDto> postList = memberPostService.getMemberPostDtoPage(username, FIRST_PAGE_SIZE_FOR_POST,
                0).getContent();

        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_RECENT6_MEMBER_POSTS_SUCCESS, postList));
    }
}
