package com.softmakers.manager_domain.logic;

import com.softmakers.manager_domain.entity.User;
import com.softmakers.manager_domain.lifecycle.StoreLifecycle;
import com.softmakers.manager_domain.spec.UserService;
import com.softmakers.manager_domain.store.UserStore;

import java.math.BigDecimal;
import java.util.List;

public class UserLogic implements UserService {

    private UserStore userStore;
    private final StoreLifecycle storeLifecycle;

    public UserLogic(StoreLifecycle storeLifecycle) {
        this.storeLifecycle = storeLifecycle;
        this.userStore = this.storeLifecycle.requestUserStore();
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
}
