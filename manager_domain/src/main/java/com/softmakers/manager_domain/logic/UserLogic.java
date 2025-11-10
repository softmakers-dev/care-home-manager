package com.softmakers.manager_domain.logic;

import com.softmakers.manager_domain.entity.User;
import com.softmakers.manager_domain.entity.feed.MemberPostDto;
import com.softmakers.manager_domain.lifecycle.StoreLifecycle;
import com.softmakers.manager_domain.spec.MemberPostService;
import com.softmakers.manager_domain.spec.UserService;
import com.softmakers.manager_domain.store.MemberPostStore;
import com.softmakers.manager_domain.store.UserStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public class UserLogic implements UserService, MemberPostService {

    private UserStore userStore;
    private MemberPostStore memberPostStore;
    private final StoreLifecycle storeLifecycle;

    public UserLogic(StoreLifecycle storeLifecycle) {
        this.storeLifecycle = storeLifecycle;
        this.userStore = this.storeLifecycle.requestUserStore();
        this.memberPostStore = this.storeLifecycle.requestMemberPostStore();
    }

    @Override
    public Boolean addUser(User user) {
        Boolean isRegistered = this.userStore.insertUser(user);
        return isRegistered;
    }

    @Override
    public List<User> findUsers() {
        return this.userStore.retrieveUsers();
    }

    @Override
    public User findUserByEmail(String email) {
        return this.userStore.retrieveUserByEmail(email);
    }

    @Override
    public User findUserById(BigDecimal id) {
        return this.userStore.retrieveUserById( id );
    }

    @Override
    public boolean changePassword(String oldPassword, String newPassword) {
        return this.userStore.savePassword( oldPassword, newPassword );
    }

    @Override
    public User findUserByUsername(String userName) {
        return this.userStore.retrieveUserByUsername( userName );
    }

    @Override
    public User findLoginUser() {
        return this.userStore.retrieveLoginUser();
    }

    @Override
    public Page<MemberPostDto> getMemberPostDtoPage(
            Long memberId, String username, Pageable pageable) {
        return this.memberPostStore.getMemberPostDtoPage(memberId, username, pageable);
    }

    @Override
    public Page<MemberPostDto> getMemberPostDtoPage(String username, int size, int page) {
        return this.memberPostStore.getMemberPostDtoPage(username, size, page);
    }

    @Override
    public Page<MemberPostDto> getMemberPostDtoPageWithoutLogin(
            String username, int size, int page) {
        return this.memberPostStore.getMemberPostDtoPageWithoutLogin(username, size, page);
    }
}
