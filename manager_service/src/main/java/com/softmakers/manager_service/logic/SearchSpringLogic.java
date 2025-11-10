package com.softmakers.manager_service.logic;

import com.softmakers.manager_domain.lifecycle.StoreLifecycle;
import com.softmakers.manager_domain.logic.SearchLogic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class SearchSpringLogic extends SearchLogic {
    public SearchSpringLogic(StoreLifecycle storeLifecycle) {
        super(storeLifecycle);
    }
}
