package com.softmakers.manager_store;

import com.softmakers.error.exception.EntityAlreadyExistException;
import com.softmakers.error.exception.EntityNotFoundException;
import com.softmakers.manager_domain.entity.feed.CommentDto;
import com.softmakers.manager_domain.store.CommentStore;
import com.softmakers.manager_store.jpo.UserJpo;
import com.softmakers.manager_store.jpo.feed.Comment;
import com.softmakers.manager_store.jpo.feed.CommentLike;
import com.softmakers.manager_store.jpo.feed.Post;
import com.softmakers.manager_store.repository.UserRepository;
import com.softmakers.manager_store.repository.feed.CommentLikeRepository;
import com.softmakers.manager_store.repository.feed.CommentRepository;
import com.softmakers.manager_store.repository.feed.PostRepository;
import com.softmakers.manager_store.repository.feed.RecentCommentRepository;
import com.softmakers.utilities.AuthUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.softmakers.error.ErrorCode.COMMENT_LIKE_ALREADY_EXIST;
import static com.softmakers.error.ErrorCode.COMMENT_NOT_FOUND;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CommentJpaStore implements CommentStore {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthUtil authUtil;
    private final CommentLikeRepository commentLikeRepository;
    private final RecentCommentRepository recentCommentRepository;

    @Override
    public CommentDto saveComment( CommentDto commentDto, Long parentId ) {
        Optional<Post> postOptional = postRepository.findById( commentDto.getPostId() );
        Optional<UserJpo> userJpoOptional = userRepository.findById( commentDto.getUserId() );

        if( postOptional.isPresent() && userJpoOptional.isPresent() ) {
            final Optional<Comment> parent = commentRepository.findById( parentId );
            final boolean isRootComment = parent.isEmpty();
            log.info("isRootComment: {}", isRootComment);
            Comment comment = new Comment(
                    isRootComment ? null : parent.get(),
                    userJpoOptional.get(),
                    postOptional.get(),
                    commentDto.getContent(),
                    commentDto.getCreatedAt() );

            Comment savedComment = commentRepository.save( comment );
            savedComment = commentRepository.findById(savedComment.getId()).orElse(null);

            CommentDto commentDtoNeo = new CommentDto(
                    savedComment.getId(),
                    savedComment.getUser().getUser_id(),
                    savedComment.getUser().getUserName(),
                    savedComment.getUser().getImage().getImageUrl(),
                    savedComment.getContent(),
                    savedComment.getCreatedAt() );
            return commentDtoNeo;
        }

        return null;
    }

    @Override
    public Page<CommentDto> findCommentsByPage(Long postId, int page) {
        Long userId = null;
        try {
            userId = authUtil.getLoginUserId();
        } catch( Exception e ) {
            log.info("error at authUtil.getLoginUserId: {} ", e.getMessage());
            userId = 56L;
        }

        Optional<UserJpo> userJpoOptional = userRepository.findById( BigDecimal.valueOf( userId ) );
        page = ( page == 0 ? 0 : page - 1 );
        final Pageable pageable = PageRequest.of(page, 10);

        if( userJpoOptional.isPresent() ){
            final Page<CommentDto> commentDtoPage = commentRepository.findCommentDtoPageByMemberIdAndPostId( userId, postId, pageable );
            return commentDtoPage;
        }
        return null;
    }

    @Override
    public Page<CommentDto> findReplyDtoPage(Long commentId, int page) {
        Long userId = null;
        try {
            userId = authUtil.getLoginUserId();
        } catch( Exception e ) {
            log.info("error at authUtil.getLoginUserId: {} ", e.getMessage());
            userId = 56L;
        }
        Optional<UserJpo> userJpoOptional = userRepository.findById( BigDecimal.valueOf( userId ) );

        page = ( page == 0 ? 0 : page - 1 );
        final Pageable pageable = PageRequest.of(page, 10);
        final Page<CommentDto> replyDtoPage = commentRepository.findReplyDtoPageByMemberIdAndCommentId(
                userId, commentId,
                pageable);
        final List<CommentDto> commentDtos = replyDtoPage.getContent();

        return replyDtoPage;
    }

    @Override
    public void likeComment(Long commentId) {
        Optional<Comment> commentOptional = commentRepository.findById( commentId );
        Long userId = null;
        try {
            userId = authUtil.getLoginUserId();
        } catch( Exception e ) {
            log.info("error at authUtil.getLoginUserId: {} ", e.getMessage());
            userId = 56L;
        }
        Optional<UserJpo> userJpoOptional = userRepository.findById( BigDecimal.valueOf( userId ) );

        Comment comment = commentRepository.findWithPostAndUserById( commentId )
                .orElseThrow(() -> new EntityNotFoundException(COMMENT_NOT_FOUND));
        if( commentOptional.isPresent() && userJpoOptional.isPresent() ) {
            if( commentLikeRepository.findByUserAndComment( userJpoOptional.get(), comment ).isPresent() ) {
                throw new EntityAlreadyExistException(COMMENT_LIKE_ALREADY_EXIST);
            }

            this.commentLikeRepository.save( new CommentLike( userJpoOptional.get(), comment ));
        }
    }
}
