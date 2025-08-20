package com.softmakers.manager_store.aws;

import com.softmakers.error.exception.NotSupportedImageTypeException;
import com.softmakers.manager_store.vo.Image;
import com.softmakers.manager_store.vo.ImageType;

import org.springframework.web.multipart.MultipartFile;

import org.apache.commons.io.FilenameUtils;

import java.util.UUID;

import com.google.common.base.Enums;

public class ImageUtil {

    public static Image convertMultipartToImage(MultipartFile file) {

        final String originalName = file.getOriginalFilename();
        final String name = FilenameUtils.getBaseName(originalName);
        final String type = FilenameUtils.getExtension(originalName).toUpperCase();

        if (!Enums.getIfPresent(ImageType.class, type).isPresent()) {
            throw new NotSupportedImageTypeException();
        }

        return Image.builder()
                .imageType(ImageType.valueOf(type))
                .imageName(name)
                .imageUUID(UUID.randomUUID().toString())
                .build();
    }

    public static Image getBaseImage() {
        return Image.builder()
                .imageName("base")
                .imageType(ImageType.PNG)
                .imageUrl("https://instagram-s3-dev.s3.ap-northeast-2.amazonaws.com/member/base-UUID_base.PNG.png")
                .imageUUID("base-UUID")
                .build();
    }
}
