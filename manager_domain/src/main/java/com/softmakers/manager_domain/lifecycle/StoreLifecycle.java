package com.softmakers.manager_domain.lifecycle;

import com.softmakers.manager_domain.store.*;

public interface StoreLifecycle {

    UserStore requestUserStore();
    RefreshTokenStore requestRefreshTokenStore();
    RegisterCodeRedisStore requestRegisterCodeRedisStore();
    PostStore requestPostStore();
    CommentStore requestCommentStore();
    RecentCommentStore requestRecentCommentStore();
    MemberPostStore requestMemberPostStore();
    ChatStore requestChatStore();
    SearchStore requestSearchStore();
}
