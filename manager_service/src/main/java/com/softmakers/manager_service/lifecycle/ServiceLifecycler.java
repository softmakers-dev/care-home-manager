package com.softmakers.manager_service.lifecycle;

import com.softmakers.manager_domain.lifecycle.ServiceLifecycle;
import com.softmakers.manager_domain.spec.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceLifecycler implements ServiceLifecycle {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final RegisterCodeRedisService registerCodeRedisService;
    private final PostService postService;
    private final CommentService commentService;
    private final RecentCommentService recentCommentService;
    private final MemberPostService memberPostService;
    private final ChatService chatService;
    private final SearchService searchService;

    @Override
    public UserService requestUserService() {
        return this.userService;
    }

    @Override
    public RefreshTokenService requestRefreshTokenService() {
        return this.refreshTokenService;
    }

    @Override
    public RegisterCodeRedisService requestRegisterCodeRedisService() {
        return this.registerCodeRedisService;
    }

    @Override
    public PostService requestPostService() {
        return this.postService;
    }

    @Override
    public CommentService requestService() {
        return this.commentService;
    }

    @Override
    public RecentCommentService requestRecentCommentService() {
        return this.recentCommentService;
    }

    @Override
    public MemberPostService requestMemberPostService() {
        return this.memberPostService;
    }

    @Override
    public ChatService requestChatService() {
        return this.chatService;
    }

    @Override
    public SearchService requestSearchService() {
        return this.searchService;
    }
}
