package com.softmakers.manager_domain.spec;

import com.softmakers.manager_domain.entity.User;
import com.softmakers.manager_domain.entity.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SearchService {

    public List<SearchDto> searchByText(String text);
    public List<User> getUserAutoComplete(String text);
    public List<User> getRecommendUsers();
    public Page<SearchDto> getTop15RecentSearches();
    public Page<SearchDto> getRecentSearches(int page);
    public void deleteRecentSearch(String entityName, String entityType);
    public void deleteAllRecentSearch();
}
