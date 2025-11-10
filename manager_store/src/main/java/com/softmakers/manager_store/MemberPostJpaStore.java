package com.softmakers.manager_store;

import com.softmakers.manager_domain.entity.feed.MemberPostDto;
import com.softmakers.manager_domain.entity.feed.PostImageDto;
import com.softmakers.manager_domain.store.MemberPostStore;
import com.softmakers.manager_store.jpo.UserJpo;
import com.softmakers.manager_store.repository.PostImageRepository;
import com.softmakers.manager_store.repository.UserRepository;
import com.softmakers.utilities.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberPostJpaStore implements MemberPostStore {
    final private UserRepository userRepository;
    final private PostImageRepository postImageRepository;
    final private AuthUtil authUtil;

    @Override
    public Page<MemberPostDto> getMemberPostDtoPage(
            Long memberId, String username, Pageable pageable) {

        return this.userRepository.findMemberPostDtoPageByLoginMemberIdAndTargetUsername(
                memberId, username, pageable);
    }

    @Override
    public Page<MemberPostDto> getMemberPostDtoPage(String username, int size, int page) {
        Long userId = null;
        try {
            userId = authUtil.getLoginUserId();
        } catch( Exception e ) {
            log.info("error at authUtil.getLoginUserId: {} ", e.getMessage());
            userId = 56L;
        }
        Optional<UserJpo> userJpoOptional = userRepository.findById( BigDecimal.valueOf( userId ) );

        final Pageable pageable = PageRequest.of(page, size);
        final Page<MemberPostDto> posts = getMemberPostDtoPage(
                userJpoOptional.get().getUser_id().longValue(), username, pageable );
        final List<MemberPostDto> content = posts.getContent();

        setMemberPostImageDtos(content);
        setPostLikesCount(userJpoOptional.get(), content);

        return posts;
    }

    @Override
    public Page<MemberPostDto> getMemberPostDtoPageWithoutLogin(
            String username, int size, int page) {
        final Pageable pageable = PageRequest.of(page, size);
        final Page<MemberPostDto> posts = getMemberPostDtoPage(-1L, username, pageable);
        final List<MemberPostDto> content = posts.getContent();

        setMemberPostImageDtos(content);
        setPostLikesCount(null, content);
        return posts;
    }

    private void setPostLikesCount(UserJpo loginMember, List<MemberPostDto> content) {
        content.forEach(post -> {
            if (loginMember != null && !post.getUserId().equals(loginMember.getUser_id())
                    && !post.isLikeOptionFlag()) {
//                final int count = postService.countOfFollowingsFromPostLikes(post.getPostId(), loginMember);
//                post.setPostLikesCount(count);
            } else if (post.isPostLikeFlag()) {
                post.setPostLikesCount(post.getPostLikesCount() + 1);
            }
        });
    }

    private void setMemberPostImageDtos(List<MemberPostDto> memberPostDtos) {
        final List<Long> postIds = memberPostDtos.stream()
                .map(MemberPostDto::getPostId)
                .collect(Collectors.toList());

        final List<PostImageDto> postImageDtos = postImageRepository.findAllPostImageDtoByPostIdIn(postIds);
        log.info("postImageDtos.size: {}", postImageDtos.size());
//        final Map<Long, List<PostImageDto>> postDTOMap = postImageDtos.stream()
//                .collect(Collectors.groupingBy(PostImageDto::getPostId));
        Map<Long, List<PostImageDto>> postDTOMap =
                (postImageDtos != null && !postImageDtos.isEmpty())
                        ? postImageDtos.stream().collect(Collectors.groupingBy(PostImageDto::getPostId))
                        : Collections.emptyMap();
//        memberPostDtos.forEach(p -> p.setPostImage(postDTOMap.get(p.getPostId()).get(0)));
        memberPostDtos.forEach(p -> {
            List<PostImageDto> images = postDTOMap.get(p.getPostId());
            if (images != null && !images.isEmpty()) {
                p.setPostImage(images.get(0));
            }
        });
    }
}
