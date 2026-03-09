package com.umust.dobonglife.domain.auth.application.port;

/**
 * 회원 탈퇴 시 타 도메인 데이터 정리를 위한 포트 인터페이스.
 * 각 도메인 모듈이 마이그레이션되면 실제 구현체로 교체한다.
 */
public interface AccountCleanupPort {

    /**
     * 사업자 회원인지 확인
     */
    boolean isBusiness(Long userId);

    /**
     * 사업자 관련 데이터(장소, 프로모션, 리뷰, 좋아요, 코스 연결 등) 삭제
     */
    void cleanupBusinessData(Long userId);

    /**
     * 사용자 관련 데이터(리뷰, 포인트, 쿠폰) 정리
     */
    void cleanupUserData(Long userId);
}
