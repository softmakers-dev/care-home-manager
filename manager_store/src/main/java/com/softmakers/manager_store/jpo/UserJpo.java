package com.softmakers.manager_store.jpo;

import com.softmakers.manager_domain.entity.User;
import com.softmakers.manager_store.vo.Image;
import com.softmakers.manager_store.vo.ImageType;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Slf4j
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

    @Column(name = "user_gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "imageUrl", column = @Column(name = "user_image_url", nullable = true)),
            @AttributeOverride(name = "imageType", column = @Column(name = "user_image_type", nullable = true)),
            @AttributeOverride(name = "imageName", column = @Column(name = "user_image_name", nullable = true)),
            @AttributeOverride(name = "imageUUID", column = @Column(name = "user_image_uuid", nullable = true))
    })
    private Image image;

    public UserJpo(User user) {
        BeanUtils.copyProperties(user, this);
        if( user != null ) {
            this.gender = Gender.valueOf( user.getUserGender() );
            this.image = Image.builder()
                    .imageName( user.getUserImageName() )
                    .imageType( ImageType.valueOf(user.getUserImageType()) )
                    .imageUrl( user.getUserImageUrl() )
                    .imageUUID( user.getUserImageUUID() )
                    .build();
            log.info("image.getImageUUID: {}", image.getImageUUID());
            log.info("image.getImageName: {}", image.getImageName());
        } else {
            // default initialize
            this.gender = Gender.P;
            this.image = Image.builder()
                    .imageName("base")
                    .imageType(ImageType.PNG)
                    .imageUrl("https://instagram-s3-dev.s3.ap-northeast-2.amazonaws.com/member/base-UUID_base.PNG.png")
                    .imageUUID("base-UUID")
                    .build();
        }
    }

    public User toDomain() {
        User user = new User();
        BeanUtils.copyProperties(this, user);

        user.setUserGender( this.gender.toString() );
        if( this.image != null ) {
            user.setUserImageUrl( this.image.getImageUrl() );
            user.setUserImageName( this.image.getImageName() );
            user.setUserImageType( this.image.getImageType().toString() );
            user.setUserImageUUID( this.image.getImageUUID() );
        }

        return user;
    }
}
