package com.softmakers.manager_store.jpo;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import com.softmakers.manager_domain.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tb_user")
public class UserJpo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private BigDecimal user_id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public UserJpo(User user) {
        BeanUtils.copyProperties(user, this);
    }

    public User toDomain() {
        User user = new User();
        BeanUtils.copyProperties(this, user);
        return user;
    }
}
