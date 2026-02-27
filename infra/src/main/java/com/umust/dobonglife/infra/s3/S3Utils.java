package com.umust.dobonglife.infra.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.umust.dobonglife.global.common.image.ImageUploader;
import com.umust.dobonglife.infra.error.InfraException;
import com.umust.dobonglife.infra.error.InfraErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PreDestroy;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Objects.isNull;

@Component
@Slf4j
@RequiredArgsConstructor
public class S3Utils implements ImageUploader {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;
    private static final String s3FolderName = "images";
    private static final Set<String> FILE_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".webp", ".heic", ".heif");
    private static final ExecutorService UPLOAD_EXECUTOR = Executors.newFixedThreadPool(5);

    @PreDestroy
    void shutdown() {
        UPLOAD_EXECUTOR.shutdown();
    }

    public String uploadImage(MultipartFile multipartFile){
        ObjectMetadata objectMetadata = new ObjectMetadata();

        String fileName = createFileName(multipartFile.getOriginalFilename());

        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        try(InputStream inputStream = multipartFile.getInputStream()){
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, s3FolderName + "/" + fileName,
                    inputStream, objectMetadata);

            amazonS3.putObject(putObjectRequest);
        }catch (IOException e){
            throw new InfraException(InfraErrorCode.IMAGE_UPLOAD_FAILED, e);
        }catch (SdkClientException e){
            throw new InfraException(InfraErrorCode.IMAGE_UPLOAD_FAILED, e);
        }
        return amazonS3.getUrl(bucket,s3FolderName + "/" + fileName).toString();
    }

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName){
        if(isNull(fileName) || fileName.isBlank()){
            throw new InfraException(InfraErrorCode.INVALID_IMAGE);
        }

        int dotIndex = fileName.lastIndexOf(".");
        if(dotIndex == -1){
            throw new InfraException(InfraErrorCode.UNSUPPORTED_IMAGE_FORMAT);
        }

        String extension = fileName.substring(dotIndex).toLowerCase();
        if(!FILE_EXTENSIONS.contains(extension)){
            throw new InfraException(InfraErrorCode.UNSUPPORTED_IMAGE_FORMAT);
        }

        return extension;
    }

    public List<String> uploadImages(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return Collections.emptyList();
        }

        List<MultipartFile> validImages = images.stream()
                .filter(img -> !img.isEmpty())
                .toList();

        if (validImages.isEmpty()) {
            return Collections.emptyList();
        }

        List<CompletableFuture<String>> futures = validImages.stream()
                .map(img -> CompletableFuture.supplyAsync(() -> uploadImage(img), UPLOAD_EXECUTOR))
                .toList();

        return futures.stream()
                .map(future -> {
                    try {
                        return future.join();
                    } catch (java.util.concurrent.CompletionException e) {
                        if (e.getCause() instanceof InfraException be) {
                            throw be;
                        }
                        throw e;
                    }
                })
                .toList();
    }

    public void deleteImage(String imgUrl){
        try{
            URL url = new URL(imgUrl);
            String path = url.getPath();

            String key;
            String bucketPrefix = "/" + bucket + "/";
            if (path.startsWith(bucketPrefix)) {
                key = path.substring(bucketPrefix.length());
            } else if (path.startsWith("/")) {
                key = path.substring(1);
            } else {
                key = path;
            }
            amazonS3.deleteObject(bucket, key);
        }catch (SdkClientException e){
            throw new InfraException(InfraErrorCode.IMAGE_DELETE_FAILED, e);
        } catch (MalformedURLException e) {
            throw new InfraException(InfraErrorCode.IMAGE_NOT_FOUND, e);
        }
    }

    public void deleteImages(List<String> images) {
        if (images == null || images.isEmpty()) {
            return;
        }
        images.stream()
                .map(img -> CompletableFuture.runAsync(() -> deleteImage(img), UPLOAD_EXECUTOR))
                .toList()
                .forEach(CompletableFuture::join);
    }
}
