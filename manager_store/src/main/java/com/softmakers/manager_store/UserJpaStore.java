package com.softmakers.manager_store;

import com.softmakers.error.exception.AccountMismatchException;
import com.softmakers.manager_domain.entity.User;
import com.softmakers.manager_domain.store.UserStore;
import com.softmakers.manager_store.jpo.UserJpo;
import com.softmakers.manager_store.repository.UserRepository;
import com.softmakers.utilities.AuthUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.lang.reflect.GenericDeclaration;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class UserJpaStore implements UserStore {

    private final UserRepository userRepository;
    private final AuthUtil authUtil;

    public UserJpaStore(UserRepository userRepository, AuthUtil authUtil) {
        this.userRepository = userRepository;
        this.authUtil = authUtil;
    }

    @Override
    public Boolean insertUser(User user) {
        Boolean isRegistered = false;

        try {
            UserJpo userJpo = new UserJpo(user);
            this.userRepository.save(userJpo);
            isRegistered = true;
        } catch (DataIntegrityViolationException e) {
            log.info("DataIntegrityViolationException", e.getMessage());
            isRegistered = false;
        } catch (IllegalArgumentException e) {
            log.info("IllegalArgumentException", e.getMessage());
            isRegistered = false;
        } catch (Exception e) {
            log.info("Exception", e.getMessage());
            isRegistered = false;
        }

        return isRegistered;
    }

    @Override
    public List<User> retrieveUsers() {
        List<UserJpo> userJpos = this.userRepository.findAll();
        List<User> users = userJpos.stream()
                .map(UserJpo -> UserJpo.toDomain())
                .collect(Collectors.toList());

        return users;
    }

    @Override
    public User retrieveUserByEmail(String email) {
        Optional<UserJpo> userJpoOptional = this.userRepository.findUserByEmail(email);
        if ( userJpoOptional.isPresent() ) {
            UserJpo userJpo = userJpoOptional.get();
            return userJpo.toDomain();
        }

        return null;
    }

    @Override
    public User retrieveUserById( BigDecimal id ) {
        Optional<UserJpo> userJpoOptional = this.userRepository.findById( id );
        if( userJpoOptional.isPresent() ) {
            UserJpo userJpo = userJpoOptional.get();
            return userJpo.toDomain();
        }
        return null;
    }

    @Override
    public boolean savePassword(String oldPassword, String newPassword) {
        Long userId = authUtil.getLoginUserId();
        User user = this.retrieveUserById( BigDecimal.valueOf( userId ) );
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        log.info("user.getPassword(): " + user.getPassword());
        log.info("oldPassword: " + oldPassword);
        log.info("encoder.encode( oldPassword ): " + encoder.encode( oldPassword ));
        if( !encoder.matches( oldPassword, user.getPassword() ) ) {
            throw new AccountMismatchException();
        }

        String encrpytedNewPassword = encoder.encode( newPassword );
        user.setPassword( encrpytedNewPassword );

        boolean isUpdated = false;
        try {
            UserJpo userJpo = new UserJpo( user );
            this.userRepository.save( userJpo );
            isUpdated = true;
        } catch (DataIntegrityViolationException e) {
            log.info("DataIntegrityViolationException", e.getMessage());
            isUpdated = false;
        } catch (IllegalArgumentException e) {
            log.info("IllegalArgumentException", e.getMessage());
            isUpdated = false;
        } catch (Exception e) {
            log.info("Exception", e.getMessage());
            isUpdated = false;
        }
        return isUpdated;
    }

    @Override
    public User retrieveUserByUsername(String userName) {
        Optional<UserJpo> userJpoOptional = this.userRepository.findUserByUserName( userName );
        if ( userJpoOptional.isPresent() ) {
            UserJpo userJpo = userJpoOptional.get();
            return userJpo.toDomain();
        }

        return null;
    }
}
