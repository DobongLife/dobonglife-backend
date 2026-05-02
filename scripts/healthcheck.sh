#!/bin/bash
# ============================================================
# MSA 로컬 헬스체크 스크립트
# 모든 서비스가 정상 기동 중인지 한 번에 확인합니다.
#
# 사용법: ./scripts/healthcheck.sh
# ============================================================

set -euo pipefail

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

TIMEOUT=3

declare -A SERVICES=(
    ["API Gateway"]="http://localhost:8080/actuator/health"
    ["Content"]="http://localhost:8081/actuator/health"
    ["Commerce"]="http://localhost:8082/actuator/health"
    ["User"]="http://localhost:8083/actuator/health"
    ["Auth"]="http://localhost:8084/actuator/health"
    ["Notification"]="http://localhost:8085/actuator/health"
)

declare -A INTERNAL_ENDPOINTS=(
    ["API→Auth"]="http://localhost:8084/internal/auth/oauth/google/verify"
    ["API→User"]="http://localhost:8083/internal/user/1/blocked"
    ["API→Commerce"]="http://localhost:8082/internal/point/balance/1"
    ["API→Content"]="http://localhost:8081/internal/banner/active"
)

echo ""
echo "========================================"
echo "  도봉라이프 MSA 헬스체크"
echo "  $(date '+%Y-%m-%d %H:%M:%S')"
echo "========================================"

# --- 1. 서비스 기동 상태 ---
echo ""
echo "[ 서비스 기동 상태 ]"
echo "----------------------------------------"

TOTAL=0
UP=0

for SERVICE in "API Gateway" "Content" "Commerce" "User" "Auth" "Notification"; do
    URL="${SERVICES[$SERVICE]}"
    TOTAL=$((TOTAL + 1))

    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" --connect-timeout $TIMEOUT "$URL" 2>/dev/null || echo "000")

    if [ "$HTTP_CODE" = "200" ]; then
        printf "  ${GREEN}✅ %-15s${NC} → %s (HTTP %s)\n" "$SERVICE" "$URL" "$HTTP_CODE"
        UP=$((UP + 1))
    elif [ "$HTTP_CODE" = "000" ]; then
        printf "  ${RED}❌ %-15s${NC} → %s (연결 불가)\n" "$SERVICE" "$URL"
    else
        printf "  ${YELLOW}⚠️  %-15s${NC} → %s (HTTP %s)\n" "$SERVICE" "$URL" "$HTTP_CODE"
    fi
done

echo "----------------------------------------"
echo "  결과: ${UP}/${TOTAL} 서비스 정상"

# --- 2. 서비스 간 통신 검증 ---
echo ""
echo "[ 서비스 간 내부 통신 ]"
echo "----------------------------------------"

for ROUTE in "API→Content" "API→Commerce" "API→User" "API→Auth"; do
    URL="${INTERNAL_ENDPOINTS[$ROUTE]}"

    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" --connect-timeout $TIMEOUT "$URL" 2>/dev/null || echo "000")

    if [ "$HTTP_CODE" = "000" ]; then
        printf "  ${RED}❌ %-15s${NC} → 연결 불가\n" "$ROUTE"
    elif [ "$HTTP_CODE" -ge 200 ] && [ "$HTTP_CODE" -lt 500 ]; then
        printf "  ${GREEN}✅ %-15s${NC} → HTTP %s (통신 가능)\n" "$ROUTE" "$HTTP_CODE"
    else
        printf "  ${RED}❌ %-15s${NC} → HTTP %s (서버 오류)\n" "$ROUTE" "$HTTP_CODE"
    fi
done

# --- 3. Redis 연결 ---
echo ""
echo "[ 인프라 ]"
echo "----------------------------------------"

if command -v redis-cli &> /dev/null; then
    REDIS_PONG=$(redis-cli -h localhost -p 6379 ping 2>/dev/null || echo "FAIL")
    if [ "$REDIS_PONG" = "PONG" ]; then
        printf "  ${GREEN}✅ Redis${NC}           → localhost:6379 (PONG)\n"
    else
        printf "  ${RED}❌ Redis${NC}           → localhost:6379 (응답 없음)\n"
    fi
else
    printf "  ${YELLOW}⚠️  Redis${NC}           → redis-cli 미설치 (확인 불가)\n"
fi

echo ""
echo "========================================"

if [ "$UP" -eq "$TOTAL" ]; then
    printf "${GREEN}모든 서비스가 정상 동작 중입니다.${NC}\n"
    exit 0
else
    printf "${YELLOW}일부 서비스가 비정상입니다. 위 결과를 확인하세요.${NC}\n"
    exit 1
fi
