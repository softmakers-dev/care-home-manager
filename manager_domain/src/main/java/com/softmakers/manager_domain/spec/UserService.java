package com.softmakers.manager_domain.spec;

import com.softmakers.manager_domain.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface UserService {

    public Boolean addUser( User user );
    public List<User> findUsers();
    public User findUserByEmail( String email );
    public User findUserById( BigDecimal id );
    public boolean changePassword( String oldPassword, String newPassword );
    public User findUserByUsername( String userName );
}
