#!/bin/bash
set -e

echo "배포 시작"

if [ -z "$DOCKER_CONTAINER_REGISTRY" ] || [ -z "$GITHUB_SHA" ]; then
    echo "환경 변수가 설정되지 않았습니다"
    echo "DOCKER_CONTAINER_REGISTRY: $DOCKER_CONTAINER_REGISTRY"
    echo "GITHUB_SHA: $GITHUB_SHA"
    exit 1
fi

export IMAGE_TAG=${GITHUB_SHA:0:7}
echo "Registry: $DOCKER_CONTAINER_REGISTRY"
echo "Version: $IMAGE_TAG"

echo "기존 컨테이너 중지 및 삭제(docker-compose down)"
docker-compose down || true

echo "이미지 다운로드 중"
docker-compose pull

echo "컨테이너 재시작 중"
docker-compose up -d --force-recreate dobonglife-backend nginx

echo "헬스체크 대기 중"
MAX_ATTEMPTS=30
ATTEMPT=0

while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
    HEALTH_STATUS=$(docker inspect --format='{{.State.Health.Status}}' dobonglife-backend 2>/dev/null || echo "starting")

    if [ "$HEALTH_STATUS" = "healthy" ]; then
        echo "헬스체크 성공"
        break
    fi

    ATTEMPT=$((ATTEMPT + 1))
    echo "   시도 $ATTEMPT/$MAX_ATTEMPTS... (상태: $HEALTH_STATUS)"
    sleep 2
done

if [ $ATTEMPT -eq $MAX_ATTEMPTS ]; then
    echo "배포 실패: 헬스체크 타임아웃"
    echo "최근 로그:"
    docker logs dobonglife-backend --tail 50
    exit 1
fi

echo "배포 성공"
echo "실행 중인 컨테이너"
docker-compose ps --format "table {{.Name}}\t{{.Status}}\t{{.Image}}"

echo "이미지 정리 중"
docker image prune -af --filter "until=24h"

echo "배포 완료"
