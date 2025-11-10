package com.softmakers.manager_store.repository.feed.jdbc;

import com.softmakers.manager_store.vo.Image;

import java.util.List;

public interface PostImageRepositoryJdbc {

    void savePostImages(List<Image> images, Long postId, List<String> altTexts);
}
