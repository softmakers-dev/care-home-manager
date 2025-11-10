package com.softmakers.manager_service.dto.feed;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostUploadRequest {

    @Schema(description = "게시물 내용", example = "안녕하세요")
    @Size(max = 2200, message = "게시물 내용은 최대 2,200자까지 입력 가능합니다.")
    private String content;

    @Schema(description = "게시물 이미지")
    @Size(min = 1, max = 10, message = "게시물 이미지는 1개 이상, 10개 이하만 추가할 수 있습니다.")
    private List<MultipartFile> postImages = new ArrayList<>();

    @Schema(description = "게시물 이미지 대체 텍스트", example = "image")
    @Size(min = 1, max = 10, message = "게시물 이미지 대체 텍스트는 필수입니다.")
    private List<@NotBlank(message = "게시물 이미지 대체 텍스트는 필수입니다.") String> altTexts;

    @Schema(description = "게시물 이미지 사용자 태그")
    @Valid
    private List<PostImageTagRequest> postImageTags = new ArrayList<>();

    @Schema(description = "댓글 기능 여부", example = "댓글 기능 사용(true) | 미사용(false)", required = true)
    @NotNull(message = "댓글 기능 여부는 필수입니다.")
    private boolean commentFlag;

    @Schema(description = "좋아요 공개 여부", required = true, example = "좋아요 공개(true) | 비공개(false)")
    @NotNull(message = "좋아요 공개 여부는 필수입니다.")
    private boolean likeFlag;
}
