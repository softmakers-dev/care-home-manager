package com.softmakers.manager_domain.spec;

import com.softmakers.manager_domain.entity.User;
import com.softmakers.manager_domain.entity.feed.CommentDto;
import com.softmakers.manager_domain.entity.feed.PostDto;

public interface RecentCommentService {

    public void deleteAll(PostDto postDto);
    public void updateByUploadingComment( PostDto postDto, User user, CommentDto commentDto,
                                         Long parentId );
}
