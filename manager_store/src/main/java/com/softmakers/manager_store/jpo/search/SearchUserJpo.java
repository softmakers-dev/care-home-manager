package com.softmakers.manager_store.jpo.search;

import com.softmakers.manager_store.jpo.UserJpo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("USER")
@Table(name = "search_users")
public class SearchUserJpo extends Search{

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserJpo userJpo;

    public SearchUserJpo(UserJpo userJpo) {
        super();
        this.userJpo = userJpo;
    }
}
