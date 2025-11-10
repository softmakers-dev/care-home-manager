package com.softmakers.manager_store.repository.feed.jdbc;

import com.softmakers.manager_domain.entity.feed.PostTagDto;

import java.util.List;

public interface PostTagRepositoryJdbc {

    void savePostTags(List<PostTagDto> postImageTags);
}
