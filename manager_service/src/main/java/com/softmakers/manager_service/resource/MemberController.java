package com.softmakers.manager_service.resource;

import com.softmakers.manager_domain.entity.User;
import com.softmakers.manager_domain.entity.dto.MemberProfile;
import com.softmakers.manager_domain.lifecycle.ServiceLifecycle;
import com.softmakers.manager_domain.spec.UserService;
import com.softmakers.result.ResultCode;
import com.softmakers.result.ResultResponse;
import com.softmakers.utilities.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Slf4j
@Tag(name = "Member API")
@RestController
@RequestMapping( "/accounts" )
public class MemberController {

    private UserService userService;
    private final ServiceLifecycle serviceLifecycle;
    private final AuthUtil authUtil;

    public MemberController(ServiceLifecycle serviceLifecycle, AuthUtil authUtil) {
        this.serviceLifecycle = serviceLifecycle;
        this.userService = this.serviceLifecycle.requestUserService();
        this.authUtil = authUtil;
    }

    @Operation( summary = "상단 메뉴 로그인한 유저 프로필 조회" )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", // HTTP status code as a string
                    description = "M016 - 상단 메뉴 프로필을 조회하였습니다."
            ),
            @ApiResponse(
                    responseCode = "401", // HTTP status code as a string
                    description = "M003 - 로그인이 필요한 화면입니다."
            )
    })
    @GetMapping( value = "/profile" )
    public ResponseEntity<ResultResponse> getMemberProfile() {
        Long userId = authUtil.getLoginUserId();
        BigDecimal dbId = BigDecimal.valueOf( userId );

        User user = this.userService.findUserById( dbId );
        MemberProfile memberProfile = new MemberProfile( user );
        memberProfile.setMemberImageUrl("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?q=80&w=987&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");

        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_USERPROFILE_SUCCESS, memberProfile));
    }
}
