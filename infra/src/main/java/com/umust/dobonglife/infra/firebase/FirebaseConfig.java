package com.umust.dobonglife.infra.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseConfig {

    @Value("${firebase.adminsdk.account.path}")
    private String firebaseAccountPath;

    @Value("${firebase.adminsdk.account.enabled:true}")
    private boolean firebaseEnabled;

    @PostConstruct
    public void initialize() {
        if (!firebaseEnabled) {
            log.info("Firebase 초기화 비활성화 (firebase.adminsdk.account.enabled=false)");
            return;
        }

        try {
            Resource resource = firebaseAccountPath.startsWith("/") ?
                    new FileSystemResource(firebaseAccountPath) :
                    new ClassPathResource(firebaseAccountPath);

            log.info("Firebase 키 파일을 로드합니다: {}", resource.getDescription());

            try (InputStream serviceAccount = resource.getInputStream()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                    log.info("Firebase 초기화 성공");
                }
            }
        } catch (IOException e) {
            log.error("Firebase 초기화 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.FIREBASE_INITIALIZATION_FAILED);
        }
    }
}
