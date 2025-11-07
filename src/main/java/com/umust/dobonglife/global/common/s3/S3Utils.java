package com.umust.dobonglife.global.common.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.umust.dobonglife.global.common.exception.BusinessException;
import com.umust.dobonglife.global.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@Slf4j
@RequiredArgsConstructor
public class S3Utils {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;
    private static final String s3FolderName = "images";
    private static final List<String> FILE_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".JPG",
            ".JPEG", ".PNG", ".webp", ".WEBP", ".heic", ".heif", ".HEIF", ".HEIC");

    public String uploadImage(MultipartFile multipartFile){
        ObjectMetadata objectMetadata = new ObjectMetadata();

        String fileName = createFileName(multipartFile.getOriginalFilename());
        log.info("uploadImage - fileName = {}", fileName);
        // 파일명 충돌 방지, 파일 이름 유출 방지

        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        try(InputStream inputStream = multipartFile.getInputStream()){
            // S3에 파일 업로드 요청 생성
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket,s3FolderName + "/" + fileName,
                    inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            amazonS3.putObject(putObjectRequest);
        }catch (IOException e){
            throw new BusinessException(ErrorCode.FAIL_IMG);
        }catch (SdkClientException e){
            throw new BusinessException(ErrorCode.FAIL_IMG);
        }
        return amazonS3.getUrl(bucket,s3FolderName + "/" + fileName).toString();
    }

    private String createFileName(String fileName) {
        log.info("fileName = {}", fileName);
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    } // 무작위로 생성된 UUID와 주어진 파일 이름의 확장자를 결합

    private String getFileExtension(String fileName){
        if(isNull(fileName) || fileName.isBlank()){
            throw new BusinessException(ErrorCode.INVALID_IMG);
        }

        String extension = fileName.substring(fileName.lastIndexOf("."));
        if(!FILE_EXTENSIONS.contains(extension)){
            throw new BusinessException(ErrorCode.INVALID_IMG_FORMAT);
        }

        return extension;

    }

    public List<String> uploadImages(List<MultipartFile> images) {
        List<String> imgUrls = new ArrayList<>();
        for(MultipartFile img : images){
            imgUrls.add(uploadImage(img));
        }
        return imgUrls;
    }

    public void deleteImage(String imgUrl){
        try{
            URL url = new URL(imgUrl);
            String path = url.getPath();

            String key = path;
            if (path.startsWith("/")) {
                key = path.substring(2 + bucket.length()); // 맨 앞 / + bucket 이름 + / 제거
            }
            amazonS3.deleteObject(bucket, key);
        }catch (SdkClientException e){
            throw new BusinessException(ErrorCode.FAIL_IMG);
        } catch (MalformedURLException e) {
            throw new BusinessException(ErrorCode.NOT_FOUND_IMG);
        }
    }
}
