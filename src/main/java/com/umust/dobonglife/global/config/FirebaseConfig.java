package com.umust.dobonglife.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.umust.dobonglife.global.common.exception.BusinessException;
import com.umust.dobonglife.global.common.response.ErrorCode;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;

// @Configuration
public class FirebaseConfig {

//    @Value("${firebase.adminsdk.account.path}")
//    private String firebaseAccountPath;
//
//    @PostConstruct
//    public void initialize() {
//        try (InputStream serviceAccount = new ClassPathResource(firebaseAccountPath).getInputStream()) {
//            FirebaseOptions options = new FirebaseOptions.Builder()
//                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                    .build();
//
//            if (FirebaseApp.getApps().isEmpty()) {
//                FirebaseApp.initializeApp(options);
//            }
//        } catch (IOException e) {
//            throw new BusinessException(ErrorCode.SERVER_ERROR_FIREBASE);
//        }
//    }
}
