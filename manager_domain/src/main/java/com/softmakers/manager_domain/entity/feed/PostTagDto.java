package com.softmakers.manager_domain.entity.feed;

import com.google.gson.annotations.Expose;
import com.querydsl.core.annotations.QueryProjection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostTagDto {

    @Expose(serialize = false, deserialize = false)
    private Long postImageId;
    private Long id;
    private Double x;
    private Double y;
    private String username;

    @QueryProjection
    public PostTagDto(Long postImageId, Long id, Double x, Double y, String username) {
        this.postImageId = postImageId;
        this.id = id;
        this.x = x;
        this.y = y;
        this.username = username;
    }

    public PostTagDto(Long postImageId, Double x, Double y, String username) {
        this.postImageId = postImageId;
        this.x = x;
        this.y = y;
        this.username = username;
    }

    public PostTagDto(Long postImageId, Long tagId, String username) {
        this.postImageId = postImageId;
        this.id = tagId;
        this.username = username;
    }
}
