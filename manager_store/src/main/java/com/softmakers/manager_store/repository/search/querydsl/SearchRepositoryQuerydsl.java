package com.softmakers.manager_store.repository.search.querydsl;

import com.softmakers.manager_domain.entity.User;
import com.softmakers.manager_domain.entity.dto.search.RecommendUser;
import com.softmakers.manager_domain.entity.dto.search.SearchUser;
import com.softmakers.manager_store.jpo.search.Search;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface SearchRepositoryQuerydsl {

    List<Search> findHashtagsByTextLike(String text);

    List<Search> findAllByTextLike(String text);

    List<Long> findUserIdsByTextLike(String text);

    List<RecommendUser> findRecommendUsersOrderByPostCounts(Long loginId);

    void checkMatchingUser(String text, List<Search> searches, List<Long> searchIds);

    void checkMatchingUser(String text, List<Long> userIds);

    Map<Long, SearchUser> findAllSearchUserByIdIn(Long loginId, List<Long> ids);

    Map<BigDecimal, User> findAllUserByIdIn(List<Long> userIds);
}
