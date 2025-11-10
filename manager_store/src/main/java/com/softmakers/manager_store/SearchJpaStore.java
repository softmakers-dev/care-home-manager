package com.softmakers.manager_store;

import com.softmakers.manager_domain.entity.User;
import com.softmakers.manager_domain.entity.dto.search.RecommendUser;
import com.softmakers.manager_domain.entity.dto.search.SearchDto;
import com.softmakers.manager_domain.entity.dto.search.SearchUser;
import com.softmakers.manager_domain.store.SearchStore;
import com.softmakers.manager_store.jpo.UserJpo;
import com.softmakers.manager_store.jpo.search.Search;
import com.softmakers.manager_store.repository.UserRepository;
import com.softmakers.manager_store.repository.search.SearchRepository;
import com.softmakers.manager_store.repository.search.SearchUserRepository;
import com.softmakers.utilities.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SearchJpaStore implements SearchStore {

    private final SearchRepository searchRepository;
    private final SearchUserRepository searchUserRepository;
    private final AuthUtil authUtil;
    private final UserRepository userRepository;

    @Override
    public List<SearchDto> searchByText(String text) {
        String keyword = text.trim();
        final Long loginId = authUtil.getLoginUserId();
        List<Search> searches;
        if (keyword.charAt(0) == '#') {
            if (keyword.equals("#")) {
                return Collections.emptyList();
            }
            searches = searchRepository.findHashtagsByTextLike(keyword.substring(1));
        } else {
            searches = searchRepository.findAllByTextLike(keyword);
        }
        if( searches.size() > 0 ) {
            log.info("searches.getId: {}", searches.get(0).getId());
        }

        final List<Long> searchIds = searches.stream()
                .map(Search::getId)
                .collect(Collectors.toList());
        if( searchIds.size() > 0 ) {
            log.info("searchIds.getId: {}", searchIds.get(0));
        }

//        searchRepository.checkMatchingHashtag(keyword.substring(1), searches, searchIds);
        searchRepository.checkMatchingUser(keyword, searches, searchIds);

        return setSearchContent(loginId, searches, searchIds);
    }

    @Override
    public List<User> getUserAutoComplete(String text) {
        String keyword = text.trim();
        final List<Long> userIds = searchRepository.findUserIdsByTextLike(keyword);

        searchRepository.checkMatchingUser(keyword, userIds);
        final List<UserJpo> users = userRepository.findAllByUser_idIn(userIds.stream()
                .map(BigDecimal::valueOf)  // Convert each Long to BigDecimal
                .collect(Collectors.toList()));

        return users.stream()
                .map(UserJpo::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getRecommendUsers() {
        final Long loginId = authUtil.getLoginUserId();
        final List<RecommendUser> recommendMembers = searchRepository.findRecommendUsersOrderByPostCounts(
                loginId);

        final List<Long> userIds = recommendMembers.stream()
                .map(RecommendUser::getUserId)
                .map(BigDecimal::longValue)
                .collect(Collectors.toList());

        final Map<BigDecimal, User> userMap = searchRepository.findAllUserByIdIn(userIds);

        return userIds.stream()
                .map(BigDecimal::valueOf)   // 1. Convert Long to BigDecimal
                .map(userMap::get)          // 2. Use the BigDecimal key for lookup
                .filter(java.util.Objects::nonNull) // 3. (Recommended) Filter out nulls for IDs not found
                .collect(Collectors.toList());
    }

    @Override
    public Page<SearchDto> getTop15RecentSearches() {
        final Long loginId = authUtil.getLoginUserId();
//        final Pageable pageable = PageRequest.of(0, FIRST_PAGE_SIZE);
//        final List<Search> searches = recentSearchRepository.findAllByMemberId(loginId, pageable);
//
//        final List<Long> searchIds = searches.stream()
//                .map(Search::getId)
//                .collect(Collectors.toList());
//
//        final List<SearchDto> searchDtos = setSearchContent(loginId, searches, searchIds);
//        final Long total = recentSearchRepository.getRecentSearchCount(loginId);
//        return new PageImpl<>(searchDtos, pageable, total);
        return null;
    }

    @Override
    public Page<SearchDto> getRecentSearches(int page) {
        final Long loginId = authUtil.getLoginUserId();
//        final Pageable pageable = PageRequest.of(page + PAGE_OFFSET, PAGE_SIZE);
//        final List<Search> searches = recentSearchRepository.findAllByMemberId(loginId, pageable);
//
//        final List<Long> searchIds = searches.stream()
//                .map(Search::getId)
//                .collect(Collectors.toList());
//
//        final List<SearchDto> searchDtos = setSearchContent(loginId, searches, searchIds);
//        final Long total = recentSearchRepository.getRecentSearchCount(loginId);
//        return new PageImpl<>(searchDtos, pageable, total);
        return null;
    }

    @Override
    public void deleteRecentSearch(String entityName, String entityType) {
        final Long loginId = authUtil.getLoginUserId();
//        switch (entityType) {
//            case "MEMBER":
//                recentSearchRepository.findRecentSearchByUsername(loginId, entityName)
//                        .ifPresent(recentSearchRepository::delete);
//                break;
//            case "HASHTAG":
//                if (!entityName.startsWith("#")) {
//                    throw new HashtagPrefixMismatchException();
//                }
//                recentSearchRepository.findRecentSearchByHashtagName(loginId, entityName.substring(1))
//                        .ifPresent(recentSearchRepository::delete);
//                break;
//            default:
//                throw new EntityTypeInvalidException();
//        }
    }

    @Override
    public void deleteAllRecentSearch() {
        final Long loginId = authUtil.getLoginUserId();
//        recentSearchRepository.deleteAllByMemberId(loginId);
    }

    private List<SearchDto> setSearchContent(Long loginId, List<Search> searches, List<Long> searchIds) {
        final Map<Long, SearchUser> userMap = searchRepository.findAllSearchUserByIdIn(loginId, searchIds);
//        final Map<Long, SearchHashtagDto> hashtagMap = searchRepository.findAllSearchHashtagDtoByIdIn(searchIds);
        final List<String> searchUsernames = userMap.values().stream().map(s -> s.getUser().getUserName())
                .collect(Collectors.toList());
        if( userMap.size() > 0 ) {
            log.info("userMap.keySet: {}", userMap.keySet());
        }
        // 스토리 주입
//        userMap.forEach(
//                (id, user) -> user.getUser().setHasStory(memberStoryRedisRepository.findById(id).isPresent())
//        );

        // 팔로우 주입
//        final Map<String, List<FollowDto>> followsMap = followRepository.findFollowingMemberFollowMap(loginId,
//                searchUsernames);
//        memberMap.forEach(
//                (id, member) -> member.setFollowingMemberFollow(
//                        followsMap.get(
//                                member.getMember().getUsername()),
//                        MAX_FOLLOWING_MEMBER_FOLLOW_COUNT));

        return searches.stream()
                .map(search -> {
                    switch (search.getDtype()) {
                        case "USER":
                            return userMap.get(search.getId());
//                        case "HASHTAG":
//                            return hashtagMap.get(search.getId());
                        default:
                            return null;
                    }
                })
                .collect(Collectors.toList());
    }
}
