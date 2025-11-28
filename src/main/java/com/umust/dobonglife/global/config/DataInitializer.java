//package com.umust.dobonglife.global.config;
//
//import com.umust.dobonglife.domain.auth.model.Provider;
//import com.umust.dobonglife.domain.user.model.User;
//import com.umust.dobonglife.domain.user.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class DataInitializer implements CommandLineRunner {
//
//    private final UserRepository userRepository;
//
//    // 마스터 계정의 권한 목록 (예: ROLE_ADMIN)
//    private static final List<GrantedAuthority> MASTER_AUTHORITIES = List.of(
//            new SimpleGrantedAuthority("ROLE_ADMIN")
//    );
//
//    @Override
//    @Transactional
//    public void run(String... args) throws Exception {
//        if (userRepository.count() == 0) {
//            User masterUser = User.builder()
//                    .name("master")
//                    .provider(Provider.LOCAL)
//                    .build();
//            userRepository.save(masterUser);
//
//            // 2. SecurityContext에 Authentication 정보 설정
//            // 이 과정을 통해 현재 스레드에서는 masterUser가 인증된 것으로 간주됩니다.
//
//            // 2-1. Authentication 객체 생성
//            Authentication authentication = new UsernamePasswordAuthenticationToken(
//                    masterUser.getName(), // Principal (인증된 사용자 ID 또는 이름)
//                    null,                 // Credentials (비밀번호. 초기화 시에는 null)
//                    MASTER_AUTHORITIES    // Authorities (부여할 권한)
//            );
//
//            // 2-2. SecurityContextHolder에 저장
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//            System.out.println("✅ 초기 'master' 사용자 계정 생성 및 SecurityContext에 임시 인증 정보 설정 완료.");
//        }
//
//    }
//}