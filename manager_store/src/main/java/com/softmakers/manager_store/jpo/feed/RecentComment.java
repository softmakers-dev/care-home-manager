package com.softmakers.manager_store.jpo.feed;

import com.softmakers.manager_store.jpo.UserJpo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "recent_comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecentComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserJpo userJpo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Builder
    public RecentComment(UserJpo userJpo, Post post, Comment comment) {
        this.userJpo = userJpo;
        this.post = post;
        this.comment = comment;
    }
}
