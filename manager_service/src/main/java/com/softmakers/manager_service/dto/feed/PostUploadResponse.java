package com.softmakers.manager_service.dto.feed;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema($schema = "게시물 생성 응답 데이터 모델")
@Getter
@AllArgsConstructor
public class PostUploadResponse {
    @Schema(description = "게시물 PK", example = "1")
    private Long postId;
}
