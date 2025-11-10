package com.softmakers.manager_service.resource;

import com.softmakers.manager_domain.entity.User;
import com.softmakers.manager_domain.entity.feed.PostDto;
import com.softmakers.manager_domain.entity.feed.PostTagDto;
import com.softmakers.manager_domain.lifecycle.ServiceLifecycle;
import com.softmakers.manager_domain.spec.PostService;
import com.softmakers.manager_domain.spec.UserService;
import com.softmakers.manager_service.dto.feed.PostImageTagRequest;
import com.softmakers.manager_service.dto.feed.PostUploadRequest;
import com.softmakers.manager_service.dto.feed.PostUploadResponse;
import com.softmakers.result.ResultResponse;

import com.softmakers.utilities.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import static com.softmakers.result.ResultCode.*;

@Slf4j
@Controller
@RequestMapping(value = "/posts")
public class PostController {
    private PostService postService;
    private final ServiceLifecycle serviceLifecycle;
    private final AuthUtil authUtil;
    private final UserService userService;

    public PostController( ServiceLifecycle serviceLifecycle, AuthUtil authUtil ) {
        this.serviceLifecycle = serviceLifecycle;
        this.postService = this.serviceLifecycle.requestPostService();
        this.authUtil = authUtil;
        this.userService = this.serviceLifecycle.requestUserService();
    }

    @Operation( description = "최근 게시물 10개 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "F005 - 최근 게시물 10개 조회에 성공하였습니다."),
            @ApiResponse(responseCode = "401", description = "F003 - 로그인이 필요한 화면입니다.")
    })
    @GetMapping(value = "/recent")
    public ResponseEntity<ResultResponse> getRecent10Posts() {

        List<PostDto> posts = this.postService.findPosts();
        return ResponseEntity.ok( ResultResponse.of(FIND_POST_SUCCESS, posts) );
    }

    @Operation( description = "게시물 업로드")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "F001 - 게시물 업로드에 성공하였습니다."),
            @ApiResponse(responseCode = "400", description = "G003 - 유효하지 않은 입력입니다.\n"
                    + "G007 - 지원하지 않는 이미지 타입입니다.\n"
                    + "G008 - 변환할 수 없는 파일입니다."),
            @ApiResponse(responseCode = "401", description = "F003 - 로그인이 필요한 화면입니다.")
    })
    @PostMapping
    public ResponseEntity<ResultResponse> uploadPost( @Valid @ModelAttribute PostUploadRequest request ) {

        PostDto postDto = new PostDto();
        try {
            Long userId = authUtil.getLoginUserId();
            User user = this.userService.findUserById( BigDecimal.valueOf( userId ) );
            postDto.setUserId( BigDecimal.valueOf( userId ) );
            postDto.setUserName( user.getUserName() );
        } catch (Exception e) {
            log.info("Exception: authUtil.getLoginUserId");
        }

        postDto.setContent( request.getContent() );
        postDto.setBoardId( 2L );
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        postDto.setCreatedAt( timestamp );
        postDto.setTitle( "Title - TBD" );
        postDto.setCommentOptionFlag( request.isCommentFlag() );
        postDto.setLikeOptionFlag( request.isLikeFlag() );

        List<PostImageTagRequest> postImageTagRequests = request.getPostImageTags();
        List<PostTagDto> postTagDtos = postImageTagRequests.stream()
                .map(req -> new PostTagDto( req.getId(), req.getTagX(),
                        req.getTagY(), req.getUsername() ) )
                .collect( Collectors.toList() );

        Long postId = this.postService.savePost( postDto, request.getPostImages(),
                request.getAltTexts(), postTagDtos );

        return ResponseEntity.ok( ResultResponse.of( CREATE_POST_SUCCESS, new PostUploadResponse( postId ) ) );
    }

    @Operation( description = "게시물 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "F004 - 게시물 조회에 성공하였습니다."),
            @ApiResponse(responseCode = "400", description = "G003 - 유효하지 않은 입력입니다.\n"
                    + "G004 - 입력 타입이 유효하지 않습니다.\n"
                    + "F001 - 존재하지 않는 게시물입니다."),
            @ApiResponse(responseCode = "401", description = "F003 - 로그인이 필요한 화면입니다.")
    })
    @Parameter( name = "postId", description = "게시물 PK, example=1", required = true )
    @GetMapping("/{postId}")
    public ResponseEntity<ResultResponse> getPost( @PathVariable("postId") Long postId ) {
        PostDto postDto = this.postService.findPost( postId );
        return ResponseEntity.ok( ResultResponse.of( FIND_POST_SUCCESS, postDto ) );
    }

    @Operation( description = "게시물 좋아요" )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "F006 - 게시물 좋아요에 성공하였습니다."),
            @ApiResponse(responseCode = "400", description = "G003 - 유효하지 않은 입력입니다.\n"
                    + "G004 - 입력 타입이 유효하지 않습니다.\n"
                    + "F001 - 존재하지 않는 게시물입니다.\n"
                    + "F004 - 해당 게시물에 이미 좋아요를 누른 회원입니다."),
            @ApiResponse(responseCode = "401", description = "F003 - 로그인이 필요한 화면입니다.")
    })
    @PostMapping( "/like")
    public ResponseEntity<ResultResponse> likePost(@RequestParam("postId") Long postId) {
        this.postService.likePost( postId );
        return ResponseEntity.ok( ResultResponse.of( LIKE_POST_SUCCESS ) );
    }

    @Operation( description = "게시물 북마크" )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "F008 - 게시물 북마크에 성공하였습니다."),
            @ApiResponse(responseCode = "400", description = "G003 - 유효하지 않은 입력입니다.\n"
                    + "G004 - 입력 타입이 유효하지 않습니다.\n"
                    + "F001 - 존재하지 않는 게시물입니다.\n"
                    + "F006 - 이미 해당 게시물을 저장하였습니다."),
            @ApiResponse(responseCode = "401", description = "F003 - 로그인이 필요한 화면입니다.")
    })
    @Parameter( name = "postId", description = "게시물 PK, example=1", required = true )
    @PostMapping("/save")
    public ResponseEntity<ResultResponse> bookmarkPost(@RequestParam("postId") Long postId) {
        this.postService.bookmark( postId );

        return ResponseEntity.ok(ResultResponse.of(BOOKMARK_POST_SUCCESS));
    }
}
