package com.softmakers.manager_domain.logic;

import com.softmakers.manager_domain.entity.User;
import com.softmakers.manager_domain.entity.dto.search.SearchDto;
import com.softmakers.manager_domain.lifecycle.StoreLifecycle;
import com.softmakers.manager_domain.spec.SearchService;
import com.softmakers.manager_domain.store.SearchStore;
import org.springframework.data.domain.Page;

import java.util.List;

public class SearchLogic implements SearchService {

    private final StoreLifecycle storeLifecycle;
    private SearchStore searchStore;

    public SearchLogic(StoreLifecycle storeLifecycle) {
        this.storeLifecycle = storeLifecycle;
        this.searchStore = this.storeLifecycle.requestSearchStore();
    }

    @Override
    public List<SearchDto> searchByText(String text) {
        return this.searchStore.searchByText( text );
    }

    @Override
    public List<User> getUserAutoComplete(String text) {
        return this.searchStore.getUserAutoComplete( text );
    }

    @Override
    public List<User> getRecommendUsers() {
        return this.searchStore.getRecommendUsers();
    }

    @Override
    public Page<SearchDto> getTop15RecentSearches() {
        return this.searchStore.getTop15RecentSearches();
    }

    @Override
    public Page<SearchDto> getRecentSearches(int page) {
        return this.searchStore.getRecentSearches( page );
    }

    @Override
    public void deleteRecentSearch(String entityName, String entityType) {
        this.searchStore.deleteRecentSearch( entityName, entityType );
    }

    @Override
    public void deleteAllRecentSearch() {
        this.searchStore.deleteAllRecentSearch();
    }
}
