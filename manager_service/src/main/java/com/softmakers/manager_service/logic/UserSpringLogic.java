package com.softmakers.manager_service.logic;

import com.softmakers.manager_domain.lifecycle.StoreLifecycle;
import com.softmakers.manager_domain.logic.UserLogic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class UserSpringLogic extends UserLogic {

    public UserSpringLogic(StoreLifecycle storeLifecycle) {
        super(storeLifecycle);
    }
}
