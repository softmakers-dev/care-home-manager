package com.softmakers.manager_service.dto.feed;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.validator.constraints.Length;

@Schema( description = "게시물 댓글 작성 요청 데이터 모델" )
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentUploadRequest {

    @Schema( description = "게시물 PK", example = "1" )
    @NotNull( message = "게시물 PK는 필수입니다." )
    private Long postId;

    @Schema( description = "댓글 부모 PK", example = "0" )
//    @NotNull( message = "부모 댓글 PK는 필수입니다." )
    private Long parentId;

    @Schema( description = "댓글 내용", example = "댓글" )
    @NotBlank( message = "댓글 내용은 필수입니다." )
    @Length( max = 100, message = "최대 100자까지 입력 가능합니다." )
    private String content;
}
