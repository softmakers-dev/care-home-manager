package com.softmakers.manager_store.aws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URL;

import com.softmakers.manager_store.vo.Image;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final S3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public Image uploadImage(MultipartFile multipartFile, String dirName) {
        final Image image = ImageUtil.convertMultipartToImage(multipartFile);
        final String filename = convertToFilename(
                dirName,
                image.getImageUUID(),
                image.getImageName(),
                image.getImageType().toString());

        try {
            final String url = upload(multipartFile, filename);
            image.setUrl(url);
        } catch ( IOException ioE ) {
            log.info("Error for S3 file uploading");
        }
        return image;
    }

    public void deleteImage(String imageUUID,
                            String imageName,
                            String imageType,
                            String dirName) {
        if ( imageUUID.equals("base-UUID") ) {
            return;
        }
        final String filename = convertToFilename(dirName, imageUUID, imageName, imageType);
//        deleteS3(filename);
    }

    private String upload(MultipartFile multipartFile, String filename) throws IOException {
        RequestBody rb = getFileRequestBody( multipartFile );
        final String uploadImageUrl = putS3( rb, filename );
        return uploadImageUrl;
    }

    private PutObjectRequest getPutObjectRequest(String fileName) {
        return PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
//                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();
    }

    private String putS3( RequestBody uploadFile, String fileName ) {
        PutObjectRequest objectRequest = getPutObjectRequest(fileName);
        amazonS3Client.putObject( objectRequest, uploadFile );

        return findUploadKeyUrl( fileName );
    }

    private String convertToFilename(
            String dirName,
            String imageUUID,
            String imageName,
            String imageType) {

        return dirName + "/" + imageUUID + "_" + imageName + "." + imageType;
    }

    private void deleteS3(String filename) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(filename)
                .build();

        amazonS3Client.deleteObject(deleteObjectRequest);
    }

    private RequestBody getFileRequestBody( MultipartFile file ) throws IOException {
        return RequestBody.fromInputStream(file.getInputStream(), file.getSize());
    }

    private String findUploadKeyUrl (String key) {
        S3Utilities s3Utilities = amazonS3Client.utilities();
        GetUrlRequest request = GetUrlRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        URL url = s3Utilities.getUrl(request);
        return url.toString();
    }
}
