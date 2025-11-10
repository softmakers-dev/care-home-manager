package com.softmakers.manager_domain.store;

import com.softmakers.manager_domain.entity.User;
import com.softmakers.manager_domain.entity.feed.CommentDto;
import com.softmakers.manager_domain.entity.feed.PostDto;

public interface RecentCommentStore {

    public void deleteAll(PostDto postDto);
    public void updateByUploadingComment( PostDto postDto, User user, CommentDto commentDto,
                                         Long parentId );
}
