package com.softmakers.manager_domain.logic;

import com.softmakers.manager_domain.entity.feed.PostDto;
import com.softmakers.manager_domain.entity.feed.PostTagDto;
import com.softmakers.manager_domain.lifecycle.StoreLifecycle;
import com.softmakers.manager_domain.spec.PostService;
import com.softmakers.manager_domain.store.PostStore;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class PostLogic implements PostService {
    private PostStore postStore;
    private final StoreLifecycle storeLifecycle;

    public PostLogic(StoreLifecycle storeLifecycle) {
        this.storeLifecycle = storeLifecycle;
        this.postStore = this.storeLifecycle.requestPostStore();
    }

    @Override
    public List<PostDto> findPosts() {
        return this.postStore.getPosts();
    }

    @Override
    public Page<PostDto> findPostDtoPage( int size, int page ) {
        return this.postStore.getPostDtoPage( size, page );
    }

    @Override
    public PostDto findPost(Long postId) {
        return this.postStore.getPost( postId );
    }

    @Override
    public Long savePost( PostDto postDto, List<MultipartFile> multipartFiles,
                             List<String> altTexts, List<PostTagDto> tags ) {
        return this.postStore.addPost( postDto, multipartFiles, altTexts, tags );
    }

    @Override
    public void likePost(Long postId) {
        this.postStore.likePost( postId );
    }

    @Override
    public void bookmark(Long postId) {
        this.postStore.bookmark( postId );
    }
}
