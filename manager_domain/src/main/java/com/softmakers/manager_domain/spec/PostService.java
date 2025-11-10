package com.softmakers.manager_domain.spec;

import com.softmakers.manager_domain.entity.feed.PostDto;
import com.softmakers.manager_domain.entity.feed.PostTagDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    public List<PostDto> findPosts();
    public Page<PostDto> findPostDtoPage(int size, int page );
    public PostDto findPost( Long postId );
    public Long savePost(PostDto postDto, List<MultipartFile> multipartFiles,
                            List<String> altTexts, List<PostTagDto> postTagDtos );
    public void likePost( Long postId );
    public void bookmark( Long postId );
}
