package com.softmakers.manager_service.resource;

import com.google.gson.JsonObject;
import com.softmakers.error.exception.FilterMustRespondException;
import com.softmakers.manager_domain.entity.dto.LoginRequest;
import com.softmakers.manager_domain.entity.dto.redis.RefreshToken;
import com.softmakers.manager_domain.spec.RefreshTokenService;
import com.softmakers.manager_service.dto.RegisterRequest;
import com.softmakers.manager_service.dto.SendConfirmationEmailRequest;
import com.softmakers.manager_service.dto.UpdatePasswordRequest;
import com.softmakers.manager_service.service.EmailCodeService;
import com.softmakers.result.ResultCode;
import com.softmakers.result.ResultResponse;
import com.softmakers.manager_domain.entity.User;
import com.softmakers.manager_domain.lifecycle.ServiceLifecycle;
import com.softmakers.manager_domain.spec.UserService;
import com.softmakers.utilities.AuthUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.softmakers.result.ResultCode.*;

@Slf4j
@Tag(name = "User", description = "멤버 인증 API")
@RestController
@RequestMapping(value = "/")
public class UserResource {

    private UserService userService;
    private RefreshTokenService refreshTokenService;
    private final ServiceLifecycle serviceLifecycle;
    private final AuthUtil authUtil;
    private final EmailCodeService emailCodeService;

    @Value("${server-domain}")
    private String SERVER_DOMAIN;


    public UserResource(AuthUtil authUtil, ServiceLifecycle serviceLifecycle, EmailCodeService emailCodeService) {
        this.authUtil = authUtil;
        this.serviceLifecycle = serviceLifecycle;
        this.emailCodeService = emailCodeService;
        this.userService = this.serviceLifecycle.requestUserService();
        this.refreshTokenService = this.serviceLifecycle.requestRefreshTokenService();
    }

    @Operation(description = "회원가입")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "M001 - 회원가입에 성공하였습니다.\n"
                    + "M013 - 이메일 인증을 완료할 수 없습니다."),
            @ApiResponse(responseCode = "400", description = "G003 - 유효하지 않은 입력입니다.\n"
                    + "G004 - 입력 타입이 유효하지 않습니다.\n"
                    + "M002 - 이미 존재하는 사용자 이름입니다.\n"
                    + "M007 - 인증 이메일 전송을 먼저 해야합니다.")
    })
    @PostMapping(value = "/signup", produces = "application/json")
    public ResponseEntity<ResultResponse> saveUser(@RequestBody RegisterRequest registerRequest) {

        Boolean isRegistered = false;
        User userExisting = this.userService.findUserByEmail(registerRequest.getEmail());

        if ( userExisting != null ) {
            log.info("A user with this email already exits: " + userExisting.getEmail());
        } else {
            User user = new User(registerRequest.getEmail(), null,
                    registerRequest.getUserName());

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setPassword(encoder.encode(registerRequest.getPassword()));

            isRegistered = this.userService.addUser(user);
        }

        if (isRegistered) {
            return ResponseEntity.ok(ResultResponse.of(REGISTER_SUCCESS, true));
        } else {
            return ResponseEntity.ok(ResultResponse.of(CONFIRM_EMAIL_FAIL, false));
        }
    }

    @Operation(description = "인증코드 이메일 전송")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "M014 - 인증코드 이메일을 전송하였습니다."),
            @ApiResponse(responseCode = "400", description = "G003 - 유효하지 않은 입력입니다.\n"
                    + "G004 - 입력 타입이 유효하지 않습니다.\n"
                    + "M002 - 이미 존재하는 사용자 이름입니다.")
    })
    @PostMapping(value = "/accounts/email")
    public ResponseEntity<ResultResponse> sendConfirmEmail(
            @Valid @RequestBody SendConfirmationEmailRequest sendConfirmationEmailRequest ) {

        this.emailCodeService.sendRegisterCode( sendConfirmationEmailRequest.getUserName(),
                sendConfirmationEmailRequest.getEmail() );
        return ResponseEntity.ok(ResultResponse.of(SEND_CONFIRM_EMAIL_SUCCESS));
    }

    @Operation(description = "로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "M002 - 로그인에 성공하였습니다."),
            @ApiResponse(responseCode = "400", description = "G003 - 유효하지 않은 입력입니다.\n"
                    + "G004 - 입력 타입이 유효하지 않습니다."),
            @ApiResponse(responseCode = "401", description = "M005 - 계정 정보가 일치하지 않습니다.")
    })
    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        throw new FilterMustRespondException();
    }

    @GetMapping(value = "/findUsers")
    public List<User> requestUsers() {
        log.info("test logged");
        return this.userService.findUsers();
    }

    @Parameter(name = "username", description = "이메일 주소", required = true)
    @GetMapping( value="/accounts/check" )
    public ResponseEntity<ResultResponse> checkUsername(
            @RequestParam( name = "username") String username ) {

        User user = this.userService.findUserByEmail(username);
        if ( user != null ) {
            return ResponseEntity.ok( ResultResponse.of( ResultCode.CHECK_USERNAME_GOOD, true ) );
        } else {
            return ResponseEntity.ok( ResultResponse.of( ResultCode.CHECK_USERNAME_BAD, false ) );
        }
    }

    @Operation( description = "logout")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "M002 - 로그아웃하였습니다."),
            @ApiResponse(responseCode = "400", description = "G003 - 유효하지 않은 입력입니다.\n"
                    + "G004 - 입력 타입이 유효하지 않습니다."),
            @ApiResponse(responseCode = "401", description = "M003 - 로그인이 필요한 화면입니다.")
    })
    @PostMapping( value = "/logout" )
    public ResponseEntity<ResultResponse> logout(
            @CookieValue( value="refreshToken", required = false ) Cookie refreshCookie,
            HttpServletResponse response,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        Optional<RefreshToken> optionalRefreshToken = null;
        if( refreshCookie != null ) {
            optionalRefreshToken = this.refreshTokenService.findRefreshToken(authUtil.getLoginUserId(), refreshCookie.getValue());
            log.info("optionalRefreshToken: " + optionalRefreshToken.isPresent());
            this.refreshTokenService.deleteRefreshTokenByValue(authUtil.getLoginUserId(), refreshCookie.getValue());
        } else {
            String token = authUtil.extractJwt(authorizationHeader);
            optionalRefreshToken = this.refreshTokenService.findRefreshToken(authUtil.getLoginUserId(), token);
            if( optionalRefreshToken.isPresent() ) {
                this.refreshTokenService.deleteRefreshToken(optionalRefreshToken.get());
            }
        }

        final Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        // cookie.setSecure(true); https 미지원
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setDomain(SERVER_DOMAIN);

        response.addCookie(cookie);

        return ResponseEntity.ok(ResultResponse.of(LOGOUT_SUCCESS));
    }

    @Operation( description = "비밀번호 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "M010 - 회원 비밀번호를 변경하였습니다."),
            @ApiResponse(responseCode = "400", description = "G003 - 유효하지 않은 입력입니다.\n" +
                    "G004 - 입력 타입이 유효하지 않습니다.\n" +
                    "M013 - 기존 비밀번호와 동일하게 변경할 수 없습니다."),
            @ApiResponse(responseCode = "401", description = "M003 - 로그인이 필요한 화면입니다.\n"
                    + "M005 - 계정 정보가 일치하지 않습니다.")
    })
    @PutMapping( value = "/accounts/password")
    public ResponseEntity<ResultResponse> updatePassword(
        @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest
        ) {

        this.userService.changePassword( updatePasswordRequest.getOldPassword(),
                updatePasswordRequest.getNewPassword() );
        return ResponseEntity.ok( ResultResponse.of( UPDATE_PASSWORD_SUCCESS ) );
    }

    @GetMapping("/oauth/success")
    public void handleOAuthSuccess(HttpServletResponse response,
                                   HttpServletRequest request,
                                   @RequestParam("accessToken") String accessToken) throws IOException, IOException {

        String frontendUrl = UriComponentsBuilder.fromUriString("http://localhost.com:5176/")
                .queryParam("accessToken", accessToken)
                .build().toUriString();

        response.sendRedirect(frontendUrl);
    }
}
