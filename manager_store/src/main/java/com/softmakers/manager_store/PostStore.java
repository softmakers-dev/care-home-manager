package com.softmakers.manager_store;

import com.softmakers.error.exception.EntityAlreadyExistException;
import com.softmakers.error.exception.EntityNotFoundException;
import com.softmakers.manager_domain.entity.feed.*;
import com.softmakers.manager_store.aws.S3Uploader;
import com.softmakers.manager_store.jpo.UserJpo;
import com.softmakers.manager_store.jpo.feed.Bookmark;
import com.softmakers.manager_store.jpo.feed.Post;
import com.softmakers.manager_store.jpo.feed.PostImage;
import com.softmakers.manager_store.jpo.feed.PostLike;
import com.softmakers.manager_store.repository.PostImageRepository;
import com.softmakers.manager_store.repository.PostTagRepository;
import com.softmakers.manager_store.repository.UserRepository;
import com.softmakers.manager_store.repository.feed.BookmarkRepository;
import com.softmakers.manager_store.repository.feed.CommentRepository;
import com.softmakers.manager_store.repository.feed.PostLikeRepository;
import com.softmakers.manager_store.repository.feed.PostRepository;
import com.softmakers.manager_store.vo.Image;
import com.softmakers.utilities.AuthUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.softmakers.error.ErrorCode.BOOKMARK_ALREADY_EXIST;
import static com.softmakers.error.ErrorCode.POST_NOT_FOUND;
import static com.softmakers.utilities.ConstantUtils.BASE_PAGE_NUMBER;
import static java.util.stream.Collectors.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostStore implements com.softmakers.manager_domain.store.PostStore {
    private final PostRepository postRepository;
    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final BookmarkRepository bookmarkRepository;

    @Value("cloud.aws.s3.drive")
    private String USER_S3_DIRNAME = "user";

    private final PostImageRepository postImageRepository;
    private final PostTagRepository postTagRepository;
    private final S3Uploader uploader;

    @Override
    public List<PostDto> getPosts() {
        Long userId = null;
        try {
            userId = authUtil.getLoginUserId();
        } catch( Exception e ) {
            log.info("error at authUtil.getLoginUserId: {} ", e.getMessage());
            userId = 56L;
        }

//        List<Post> posts = this.postRepository.findTop10ByOrderByPostIdDesc();
        final Pageable pageable = PageRequest.of(BASE_PAGE_NUMBER, 10);
        final Page<PostDto> postDtoPage = this.postRepository.findPostDtoPageOfFollowingMembersOrHashtagsByMemberId(
            userId, pageable );

        if( !postDtoPage.isEmpty() ) {
            List<PostDto> postDtos = postDtoPage.getContent().stream()
                    .map(post -> {
                        PostDto dto = new PostDto();
                        dto.setPostId( post.getPostId() );
                        dto.setTitle( post.getTitle() );
                        dto.setContent( post.getContent() );
                        dto.setUserName( post.getUserName() );
                        dto.setImageUrl( post.getImageUrl() );
                        dto.setCreatedAt( post.getCreatedAt() );
                        dto.setLikeOptionFlag( post.isLikeOptionFlag() );
                        dto.setCommentOptionFlag( post.isCommentOptionFlag() );
                        return dto;
                    })
                    .collect( toList() );

            final List<Long> postIds = postDtos.stream()
                    .map(PostDto::getPostId)
                    .collect(toList());
            setRecentComments( BigDecimal.valueOf( userId ), postDtos, postIds );
            setPostImages( postDtos, postIds );
            hidePostLikesCountIfPostLikeFlagIsFalse( BigDecimal.valueOf( userId ), postDtos );

            return postDtos;
        }
        return null;
    }

    @Override
    public Page<PostDto> getPostDtoPage(int size, int page) {
        Long userId = null;
        try {
            userId = authUtil.getLoginUserId();
        } catch( Exception e ) {
            log.info("error at authUtil.getLoginUserId: {} ", e.getMessage());
            userId = 56L;
        }

        final Pageable pageable = PageRequest.of(page, size);
        final Page<PostDto> postDtoPage = this.postRepository.findPostDtoPageOfFollowingMembersOrHashtagsByMemberId(
                userId, pageable );
        Optional<UserJpo> userJpoOptional = userRepository.findById( BigDecimal.valueOf( userId ) );
        setContents( userJpoOptional.get(), postDtoPage.getContent() );

        return postDtoPage;
    }

    private void setContents( UserJpo userJpo, List<PostDto> postDtos ) {
        final List<Long> postIds = postDtos.stream()
                .map(PostDto::getPostId)
                .collect(toList());

        setRecentComments( userJpo.getUser_id(), postDtos, postIds );
        setPostImages( postDtos, postIds );
        hidePostLikesCountIfPostLikeFlagIsFalse( userJpo.getUser_id(), postDtos );
    }

    @Override
    public PostDto getPost(Long postId) {
        Long userId = null;
        try {
            userId = authUtil.getLoginUserId();
        } catch( Exception e ) {
            log.info("error at authUtil.getLoginUserId: {} ", e.getMessage());
            userId = 56L;
        }

        PostDto postDto = this.postRepository.findPostDtoByPostIdAndMemberId( postId, userId )
                .orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND));

        setComments( postDto );
        setPostImages( List.of( postDto ), List.of( postDto.getPostId() ) );
        return postDto;
    }

    @Override
    public Long addPost( PostDto postDto, List<MultipartFile> multipartFiles,
                            List<String> altTexts, List<PostTagDto> tags ) {

        Post post = new Post( postDto );
        try {
            Post savedPost = postRepository.save( post );

            final List<Image> images = multipartFiles.stream()
                    .map( pi -> uploader.uploadImage( pi, USER_S3_DIRNAME ) )
                    .collect( Collectors.toList() );
            this.postImageRepository.savePostImages( images, savedPost.getPostId(), altTexts );

            for ( int i = 0; i < images.size(); i++ ) {
                PostImage postImage = PostImage.builder()
                        .post( savedPost )
                        .image( images.get(i) )
                        .altText( altTexts.get(i) )
                        .build();

                savedPost.getPostImages().add( postImage );
            }

            if ( !tags.isEmpty() ) {
                linkWithTags(tags, savedPost);
            }

            postTagRepository.savePostTags( tags );

            return savedPost.getPostId();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void likePost( Long postId ) {
        Optional<Post> postOptional = postRepository.findById( postId );
        Long userId = null;
        try {
            userId = authUtil.getLoginUserId();
        } catch( Exception e ) {
            log.info("error at authUtil.getLoginUserId: {} ", e.getMessage());
            userId = 56L;
        }
        Optional<UserJpo> userJpoOptional = userRepository.findById( BigDecimal.valueOf( userId ) );

        if( postOptional.isPresent() && userJpoOptional.isPresent() ) {
            postLikeRepository.save( new PostLike( userJpoOptional.get(), postOptional.get() ));
        }
    }

    @Override
    public void bookmark(Long postId) {
        final Optional<Post> postOptional = this.postRepository.findById( postId );
        Long userId = null;
        try {
            userId = authUtil.getLoginUserId();
        } catch( Exception e ) {
            log.info("error at authUtil.getLoginUserId: {} ", e.getMessage());
            userId = 56L;
        }
        Optional<UserJpo> userJpoOptional = this.userRepository.findById( BigDecimal.valueOf( userId ) );

        if( postOptional.isPresent() && userJpoOptional.isPresent() ) {
            if (bookmarkRepository.findByUserAndPost(userJpoOptional.get(),
                    postOptional.get()).isPresent()) {

                throw new EntityAlreadyExistException(BOOKMARK_ALREADY_EXIST);
            }

            bookmarkRepository.save(new Bookmark(userJpoOptional.get(), postOptional.get()));
        }
    }

    private void setComments(PostDto postDto, Long userId) {
        final Pageable pageable = PageRequest.of(BASE_PAGE_NUMBER, 10);
        final List<CommentDto> commentDtos =
                commentRepository.findCommentDtoPageByMemberIdAndPostId( postDto.getPostId(),
                        userId, pageable ).getContent();

        postDto.setRecentComments(commentDtos);
    }

    private void setRecentComments( BigDecimal userId, List<PostDto> postDtos, List<Long> postIds ) {
        final Pageable pageable = PageRequest.of(BASE_PAGE_NUMBER, 10);
        final Map<Long, List<CommentDto>> recentCommentMap =
                commentRepository.findAllRecentCommentDtoByMemberIdAndPostIdIn(
                        userId.longValue(), postIds ).stream()
                        .collect( groupingBy( CommentDto::getPostId ) );

        final List<CommentDto> totalCommentDtos = new ArrayList<>();
        postDtos.forEach(postDto -> {
            if ( recentCommentMap.containsKey( postDto.getPostId() ) ) {
                final List<CommentDto> commentDtos = recentCommentMap.get( postDto.getPostId() );
                totalCommentDtos.addAll( commentDtos );
                postDto.setRecentComments( commentDtos );
                postDto.setPostCommentsCount( commentDtos.size() );
            }
        });
    }

    private void setComments( PostDto postDto ) {
        final Pageable pageable = PageRequest.of(BASE_PAGE_NUMBER, 10);

        final Page<CommentDto> commentDtoPage = commentRepository.findCommentDtoPageWithoutLoginByPostId(postDto.getPostId(),
                pageable);
        final List<CommentDto> commentDtos = commentDtoPage.getContent();
        postDto.setRecentComments(commentDtos);
    }

    private void hidePostLikesCountIfPostLikeFlagIsFalse(BigDecimal userId, PostDto postDto) {
        hidePostLikesCountIfPostLikeFlagIsFalse(userId, List.of(postDto));
    }

    private void hidePostLikesCountIfPostLikeFlagIsFalse( BigDecimal userId, List<PostDto> postDtos ) {
        final Map<Long, PostDto> postDtosToHidePostLikesCountMap = postDtos.stream()
                .filter( postDto -> !userId.equals( postDto.getUserId() ) )
                .collect( toMap(PostDto::getPostId, PostDto -> PostDto) );

        final List<Long> postIds = new ArrayList<>( postDtosToHidePostLikesCountMap.keySet() );
        final List<PostLikeCountDto> postLikeCountDtos = postLikeRepository
                .findAllPostLikeCountDtoOfFollowingsLikedPostByMemberAndPostIdIn( userId.longValue(), postIds );

        postLikeCountDtos.forEach(postLikeCountDto -> postDtosToHidePostLikesCountMap.get(postLikeCountDto.getPostId())
                .setPostLikesCount( postLikeCountDto.getPostLikesCount()) );
    }

    private void linkWithTags( List<PostTagDto> postImageTags, Post post ) {
        final List<Long> postImageIds = postImageRepository.findAllByPost(post).stream()
                .map( PostImage::getId )
                .collect( Collectors.toList() );
        int idx = postImageTags.get(0).getId().intValue();

        for ( PostTagDto postImageTag : postImageTags ) {
            if( idx != postImageTag.getId() )
                idx = postImageTag.getId().intValue();
            postImageTag.setPostImageId( postImageIds.get(idx) );
        }
    }

    private void setPostImages(List<PostDto> postDtos, List<Long> postIds) {
        final List<PostImageDto> postImageDtos = postImageRepository.findAllPostImageDtoByPostIdIn(postIds);
        final List<Long> postImageIds = postImageDtos.stream()
                .map(PostImageDto::getId)
                .collect(toList());

        setPostTags(postImageDtos, postImageIds);

        final Map<Long, List<PostImageDto>> postDtoMap = postImageDtos.stream()
                .collect(groupingBy(PostImageDto::getPostId));
        postDtos.forEach(p -> p.setPostImages(postDtoMap.get(p.getPostId())));
    }

    private void setPostTags(List<PostImageDto> postImageDtos, List<Long> postImageIds) {
        final List<PostTagDto> postTagDtos = postTagRepository.findAllPostTagDto(postImageIds);

        final Map<Long, List<PostTagDto>> postImageDtoMap = postTagDtos.stream()
                .collect(groupingBy(PostTagDto::getPostImageId));
        postImageDtos.forEach(i -> i.setPostTags(postImageDtoMap.get(i.getId())));
    }
}
