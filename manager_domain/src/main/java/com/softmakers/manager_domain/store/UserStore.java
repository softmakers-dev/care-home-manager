package com.softmakers.manager_domain.store;

import com.softmakers.manager_domain.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface UserStore {

    public Boolean insertUser( User user );
    public List<User> retrieveUsers();
    public User retrieveUserByEmail( String email );
    public User retrieveUserById( BigDecimal id );
    public boolean savePassword( String oldPassword, String newPassword );
    public User retrieveUserByUsername( String userName );
}
