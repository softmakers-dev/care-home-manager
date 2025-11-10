package com.softmakers.manager_store.repository.search.querydsl;

import com.querydsl.core.Tuple;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.softmakers.manager_domain.entity.QUser;
import com.softmakers.manager_domain.entity.User;
import com.softmakers.manager_domain.entity.dto.search.QRecommendUser;
import com.softmakers.manager_domain.entity.dto.search.QSearchUser;
import com.softmakers.manager_domain.entity.dto.search.RecommendUser;
import com.softmakers.manager_domain.entity.dto.search.SearchUser;
import com.softmakers.manager_store.jpo.QUserJpo;
import com.softmakers.manager_store.jpo.search.QSearchUserJpo;
import com.softmakers.manager_store.jpo.search.Search;
import com.softmakers.manager_store.jpo.search.SearchUserJpo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.softmakers.manager_store.jpo.QUserJpo.userJpo;
import static com.softmakers.manager_store.jpo.feed.QPost.post;
import static com.softmakers.manager_store.jpo.search.QSearch.search;
import static com.softmakers.manager_store.jpo.search.QSearchUserJpo.searchUserJpo;

@Slf4j
@RequiredArgsConstructor
public class SearchRepositoryQuerydslImpl implements SearchRepositoryQuerydsl{

    private static final int SEARCH_SIZE = 50;
    private static final int RECOMMEND_SIZE = 10;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Search> findHashtagsByTextLike(String text) {
        return null;
    }

    @Override
    public List<Search> findAllByTextLike(String text) {
        final String keyword = text + "%";

        return queryFactory
                .select(search)
                .from(search)
                .where(search.id.in(
                                JPAExpressions
                                        .select( searchUserJpo.id )
                                        .from( searchUserJpo )
                                        .innerJoin(searchUserJpo.userJpo, userJpo)
                                        .where( searchUserJpo.userJpo.userName.like(keyword)
                                                .or( searchUserJpo.userJpo.userName.like(keyword))))
                        )
                .orderBy(search.count.desc())
                .limit(SEARCH_SIZE)
                .distinct()
                .fetch();
    }

    @Override
    public List<Long> findUserIdsByTextLike(String text) {
        final String keyword = text + "%";

        return queryFactory
                .select(searchUserJpo.userJpo.user_id.longValue())
                .from(searchUserJpo)
                .where(searchUserJpo.userJpo.userName.like(keyword))
                .orderBy(searchUserJpo.count.desc())
                .limit(SEARCH_SIZE)
                .distinct()
                .fetch();
    }

    @Override
    public List<RecommendUser> findRecommendUsersOrderByPostCounts(Long loginId) {
        return queryFactory
                .select(new QRecommendUser(
                        post.userJpo.user_id,
                        post.count()
                ))
                .from(post)
                .where(post.userJpo.user_id.ne(BigDecimal.valueOf(loginId))
//                        .and(post.userJpo.user_id.notIn(
//                        JPAExpressions.select(follow.followMember.id)
//                                .from(follow)
//                                .where(follow.member.id.eq(loginId))))
                )
                .groupBy(post.userJpo.user_id)
                .orderBy(post.count().desc())
                .distinct()
                .limit(RECOMMEND_SIZE)
                .fetch();
    }

    @Override
    public void checkMatchingUser(String text, List<Search> searches, List<Long> searchIds) {
        final Search matchingSearch = queryFactory
                .select(searchUserJpo._super)
                .from(searchUserJpo)
                .where(searchUserJpo.userJpo.userName.eq(text))
                .fetchOne();

        if (matchingSearch != null && !searchIds.contains(matchingSearch.getId())) {
            searches.add(0, matchingSearch);
            searchIds.add(0, matchingSearch.getId());
            checkSearchSize(searches);
            checkSearchSize(searchIds);
        }
    }

    @Override
    public void checkMatchingUser(String text, List<Long> userIds) {
        final SearchUserJpo matchingSearch = queryFactory
                .select(searchUserJpo)
                .from(searchUserJpo)
                .where(searchUserJpo.userJpo.userName.eq(text))
                .fetchOne();
        if (matchingSearch != null && !userIds.contains(matchingSearch.getUserJpo().getUser_id())) {
            userIds.add(0, matchingSearch.getUserJpo().getUser_id().longValue());
            checkSearchSize(userIds);
        }
    }

    @Override
    public Map<Long, SearchUser> findAllSearchUserByIdIn(Long loginId, List<Long> searchIds) {
        // 1. Fetch the data as a List of Tuple
        List<Tuple> results = queryFactory
                .select(
                        searchUserJpo.id,
                        searchUserJpo.userJpo.user_id, // Key: The Long ID
                        searchUserJpo._super.dtype,
                        searchUserJpo.userJpo.userName
                )
                .from(searchUserJpo)
                .innerJoin(searchUserJpo.userJpo, userJpo)
                .where(searchUserJpo.id.in(searchIds))
                .fetch(); // Fetch returns List<Tuple>

        // 2. Manually map the List<Tuple> into Map<Long, SearchUser> using Streams
        return results.stream()
                .collect(Collectors.toMap(
                        // Key Mapper: Extract the user_id (Long)
                        tuple -> tuple.get(searchUserJpo.id),
                        // Value Mapper: Construct a new SearchUser DTO
                        tuple -> new SearchUser(
                                tuple.get(searchUserJpo._super.dtype),
                                tuple.get(searchUserJpo.userJpo.user_id),
                                tuple.get(searchUserJpo.userJpo.userName)
                        ),

                        // Merge function (Handling duplicates, only necessary if the query returns duplicates)
                        (existing, replacement) -> existing
                ));
    }

//    public Map<BigDecimal, SearchUser> findAllSearchUserByIdIn(Long loginId, List<Long> searchIds) {
//        // Define the projection (select the user_id and the DTO fields)
//        log.info("loginId: {}", loginId);
//        if( searchIds.size() > 0 ) {
//            log.info("searchIds.get(0): {}", searchIds.get(0));
//        }
//
//        List<SearchUser> searchUsers = queryFactory
//                .select( new QSearchUser(
//                        searchUserJpo._super.dtype,
//                        searchUserJpo.userJpo.user_id, // Key
//                        searchUserJpo.userJpo.userName )
//                )
//                .from(searchUserJpo)
//                .innerJoin(searchUserJpo.userJpo, userJpo)
//                .where(searchUserJpo.id.in(searchIds))
//                .fetch(); // Fetch the results as a List<Tuple>
//
//        // 2. Perform the mapping in Java (as shown below)
//        return searchUsers.stream()
//                .collect(Collectors.toMap(
//                        searchUser -> searchUser.getUser().getUser_id(),  // Key mapping
//                        searchUser -> searchUser  // Value mapping
//                ));
//    }

    @Override
    public Map<BigDecimal, User> findAllUserByIdIn(List<Long> userIds) {
//        return queryFactory
//                .from(userJpo)
//                .where(userJpo.user_id.longValue().in(userIds))
//                .transform(GroupBy.groupBy(userJpo.user_id).as(new QUser(userJpo.user_id,
//                        userJpo.userName,
//                        userJpo.image.imageUrl))
//                );

        // Alternative to transform(GroupBy.groupBy:
        List<BigDecimal> bigDecimalIds = userIds.stream()
                .map(BigDecimal::valueOf)
                .collect(Collectors.toList());

        List<User> users = queryFactory
                .select( new QUser(userJpo.user_id, userJpo.userName, userJpo.image.imageUrl) )
                .from( userJpo )
                .where( userJpo.user_id.in(bigDecimalIds) )
                .fetch();

        return users.stream()
                .collect(Collectors.toMap(
                        User::getUser_id, // Key mapping
                        user -> user      // Value mapping
                ));
    }

    private <T> void checkSearchSize(List<T> list) {
        while (list.size() > SEARCH_SIZE) {
            list.remove(list.size() - 1);
        }
    }
}
