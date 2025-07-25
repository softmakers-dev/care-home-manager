package com.softmakers.manager_domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    private BigDecimal user_id;
    private String email;
    private String password;
    private String userName;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public User( String email, String password, String user_name ) {
        this.email = email;
        this.password = password;
        this.userName = user_name;
        this.createdAt = Timestamp.from( Instant.now() );
    }
}
