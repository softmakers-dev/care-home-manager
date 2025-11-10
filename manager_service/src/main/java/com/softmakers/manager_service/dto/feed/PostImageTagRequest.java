package com.softmakers.manager_service.dto.feed;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostImageTagRequest {

    @Schema(description = "게시물 이미지 순번", example = "1", required = true)
    @NotNull(message = "게시물 이미지 순번은 필수입니다.")
    private Long id;

    @Schema(description = "게시물 이미지 태그 x 좌표", example = "50", required = true, minimum = "0", maximum = "100")
    @NotNull(message = "게시물 이미지 태그 x 좌표는 필수입니다.")
    @Min(value = 0, message = "x 좌표는 0 ~ 100 사이로 입력해주세요.")
    @Max(value = 100, message = "x 좌표는 0 ~ 100 사이로 입력해주세요.")
    private Double tagX;

    @Schema(description = "게시물 이미지 태그 y 좌표", example = "50", required = true, minimum = "0", maximum = "100")
    @NotNull(message = "게시물 이미지 태그 y 좌표는 필수입니다.")
    @Min(value = 0, message = "y 좌표는 0 ~ 100 사이로 입력해주세요.")
    @Max(value = 100, message = "y 좌표는 0 ~ 100 사이로 입력해주세요.")
    private Double tagY;

    @Schema(description = "게시물 이미지 태그 사용자 아이디", example = "dlwlrma", required = true)
    @NotBlank(message = "게시물 이미지 태그 사용자 아이디는 필수입니다.")
    private String username;
}
