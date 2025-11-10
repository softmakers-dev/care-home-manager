package com.softmakers.manager_service.resource;

import com.softmakers.manager_domain.entity.User;
import com.softmakers.manager_domain.entity.feed.CommentDto;
import com.softmakers.manager_domain.entity.feed.PostDto;
import com.softmakers.manager_domain.spec.CommentService;
import com.softmakers.manager_domain.spec.PostService;
import com.softmakers.manager_domain.spec.RecentCommentService;
import com.softmakers.manager_domain.spec.UserService;
import com.softmakers.manager_service.dto.feed.CommentUploadRequest;
import com.softmakers.manager_service.dto.feed.CommentUploadResponse;
import com.softmakers.manager_service.lifecycle.ServiceLifecycler;
import com.softmakers.manager_store.jpo.feed.Comment;
import com.softmakers.result.ResultResponse;

import com.softmakers.utilities.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.softmakers.result.ResultCode.*;

@Slf4j
@Tag(name = "Comment API")
@RestController
@RequestMapping( "/comments" )
public class CommentController {

    private CommentService commentService;
    private RecentCommentService recentCommentService;
    private PostService postService;
    private UserService userService;
    private final ServiceLifecycler serviceLifecycler;
    private final AuthUtil authUtil;

    public CommentController(ServiceLifecycler serviceLifecycler, AuthUtil authUtil) {
        this.serviceLifecycler = serviceLifecycler;
        this.authUtil = authUtil;
        this.commentService = this.serviceLifecycler.requestService();
        this.recentCommentService = this.serviceLifecycler.requestRecentCommentService();
        this.postService = this.serviceLifecycler.requestPostService();
        this.userService = this.serviceLifecycler.requestUserService();
    }

    @Operation( description = "댓글 업로드: parentId는 댓글인 경우 0, 답글인 경우 댓글 부모 PK를 입력해 주세요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "F010 - 댓글 업로드에 성공하였습니다."),
            @ApiResponse(responseCode = "400", description = "G003 - 유효하지 않은 입력입니다.\n"
                    + "G005 - request message body가 없거나, 값 타입이 올바르지 않습니다."
                    + "F001 - 존재하지 않는 게시물입니다.\n"
                    + "F012 - 댓글 기능이 해제된 게시물에는 댓글을 작성할 수 없습니다.\n"
                    + "F013 - 최상위 댓글에만 답글을 업로드할 수 있습니다."),
            @ApiResponse(responseCode = "401", description = "F003 - 로그인이 필요한 화면입니다.")
    })
    @PostMapping
    public ResponseEntity<ResultResponse> uploadComment( @Valid @RequestBody CommentUploadRequest request ) {
        CommentDto commentDto = new CommentDto();
        Long userId = 56L;
        try {
            userId = authUtil.getLoginUserId();
            commentDto.setUserId( BigDecimal.valueOf( userId) );
        } catch (Exception e) {
            log.info("Exception: authUtil.getLoginUserId");
            commentDto.setUserId( BigDecimal.valueOf( 56L ) );
        }

        commentDto.setPostId( request.getPostId() );
        commentDto.setContent( request.getContent() );
        LocalDateTime currentDateTime = LocalDateTime.now();
        commentDto.setCreatedAt( currentDateTime );

        CommentDto commentDtoSaved = this.commentService.uploadComment( commentDto, request.getParentId() );
        PostDto postDto = this.postService.findPost( commentDto.getPostId() );
        User user = this.userService.findUserById( BigDecimal.valueOf( userId ) );
        this.recentCommentService.updateByUploadingComment( postDto, user, commentDtoSaved,
                request.getParentId() );

        return ResponseEntity.ok( ResultResponse.of( CREATE_COMMENT_SUCCESS, new CommentUploadResponse(commentDtoSaved) ) );
    }

    @Operation( description = "댓글 페이징 조회: 게시물 조회에서 최근 댓글 10개를 응답하므로, <b>2페이지부터 조회</b>해 주세요.<br>" +
            "조회 중간에 새 댓글이 추가되면, 추가 조회 시 중복 데이터가 발생할 수 있으므로, " +
            "중복 데이터는 걸러서 뷰에 표현해 주시면 됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "F012 - 댓글 목록 페이지 조회에 성공하였습니다."),
            @ApiResponse(responseCode = "400", description = "G003 - 유효하지 않은 입력입니다.\n"),
            @ApiResponse(responseCode = "401", description = "F003 - 로그인이 필요한 화면입니다.")
    })
    @Parameters({
            @Parameter(name = "postId", description = "게시물 PK", example = "1", required = true),
            @Parameter(name = "page", description = "댓글 page", example = "1", required = true)
    })
    @GetMapping(value = "/posts/{postId}")
    public ResponseEntity<ResultResponse> getCommentsByPage(
            @PathVariable(value = "postId") Long postId,
            @RequestParam(value = "page") int page ) {
        Page<CommentDto> commentDtos = this.commentService.getCommentsByPage( postId, page );
        return ResponseEntity.ok( ResultResponse.of( GET_COMMENT_PAGE_SUCCESS, commentDtos ) );
    }

    @Operation( description = "답글 페이징 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "F013 - 답글 목록 페이지 조회에 성공하였습니다."),
            @ApiResponse(responseCode = "400", description = "G003 - 유효하지 않은 입력입니다.\n"),
            @ApiResponse(responseCode = "401", description = "F003 - 로그인이 필요한 화면입니다.")
    })
    @Parameters({
            @Parameter(name = "commentId", description = "부모 댓글 PK", example = "1", required = true),
            @Parameter(name = "page", description = "댓글 page", example = "1", required = true)
    })
    @GetMapping("/{commentId}")
    public ResponseEntity<ResultResponse> getReplyPage(@PathVariable("commentId") Long commentId,
                                                       @RequestParam("page") int page) {
        final Page<CommentDto> response = commentService.getReplyDtoPage(commentId, page);

        return ResponseEntity.ok(ResultResponse.of(GET_REPLY_PAGE_SUCCESS, response));
    }

    @Operation( description = "댓글 좋아요" )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "F015 - 댓글 좋아요에 성공하였습니다."),
            @ApiResponse(responseCode = "400", description = "G003 - 유효하지 않은 입력입니다.\n"
                    + "G004 - 입력 타입이 유효하지 않습니다.\n"
                    + "F008 - 존재하지 않는 댓글입니다.\n"
                    + "F010 - 해당 댓글에 이미 좋아요를 누른 회원입니다."),
            @ApiResponse(responseCode = "401", description = "F003 - 로그인이 필요한 화면입니다.")
    })
    @Parameter(name = "commentId", description = "댓글 PK", example = "1", required = true)
    @PostMapping("/like")
    public ResponseEntity<ResultResponse> likeComment( @RequestParam("commentId") Long commentId ) {
        commentService.likeComment(commentId);

        return ResponseEntity.ok(ResultResponse.of(LIKE_COMMENT_SUCCESS));
    }
}
