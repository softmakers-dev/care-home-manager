package com.softmakers.manager_domain.store;

import com.softmakers.manager_domain.entity.feed.PostDto;
import com.softmakers.manager_domain.entity.feed.PostTagDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostStore {
    public List<PostDto> getPosts();
    public Page<PostDto> getPostDtoPage( int size, int page );
    public PostDto getPost( Long postId );
    public Long addPost(PostDto postDto, List<MultipartFile> multipartFiles,
                           List<String> altTexts, List<PostTagDto> postTagDtos );
    public void likePost( Long postId );
    public void bookmark( Long postId );
}
