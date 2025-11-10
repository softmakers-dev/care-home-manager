package com.softmakers.manager_domain.lifecycle;

import com.softmakers.manager_domain.spec.*;

public interface ServiceLifecycle {

    UserService requestUserService();
    RefreshTokenService requestRefreshTokenService();
    RegisterCodeRedisService requestRegisterCodeRedisService();
    PostService requestPostService();
    CommentService requestService();
    RecentCommentService requestRecentCommentService();
    MemberPostService requestMemberPostService();
    ChatService requestChatService();
    SearchService requestSearchService();
}
