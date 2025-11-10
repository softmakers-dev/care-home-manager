package com.softmakers.manager_store;

import com.softmakers.error.exception.CantUploadReplyException;
import com.softmakers.manager_domain.entity.User;
import com.softmakers.manager_domain.entity.feed.CommentDto;
import com.softmakers.manager_domain.entity.feed.PostDto;
import com.softmakers.manager_domain.store.RecentCommentStore;
import com.softmakers.manager_store.jpo.UserJpo;
import com.softmakers.manager_store.jpo.feed.Comment;
import com.softmakers.manager_store.jpo.feed.Post;
import com.softmakers.manager_store.jpo.feed.RecentComment;
import com.softmakers.manager_store.repository.feed.CommentRepository;
import com.softmakers.manager_store.repository.feed.RecentCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RecentCommentJpaStore implements RecentCommentStore {

    private final RecentCommentRepository recentCommentRepository;
    private final CommentRepository commentRepository;

    @Override
    public void deleteAll(PostDto postDto) {
        Post post = new Post( postDto );
        final List<RecentComment> recentComments = recentCommentRepository.findAllByPost(post);
        recentCommentRepository.deleteAllInBatch( recentComments );
    }

    @Override
    public void updateByUploadingComment( PostDto postDto, User user, CommentDto commentDto,
                                          Long parentId) {

        final Optional<Comment> parent = commentRepository.findById( parentId );
        final boolean isRootComment = parent.isEmpty();

        if( isRootComment ) {
            Post post = new Post(postDto);
            final List<RecentComment> recentComments = recentCommentRepository.findAllByPost(post);
            if (recentComments.size() == 2) {
                final RecentComment recentComment =
                        recentComments.get(0).getId() < recentComments.get(1).getId() ? recentComments.get(0) :
                                recentComments.get(1);
                recentCommentRepository.delete(recentComment);
            }

            UserJpo userJpo = new UserJpo( user );
            Comment comment = new Comment( commentDto );
            recentCommentRepository.save(new RecentComment(userJpo, post, comment));
        } else if ( parent.get().getParent() != null ) {
            throw new CantUploadReplyException();
        }
    }
}
