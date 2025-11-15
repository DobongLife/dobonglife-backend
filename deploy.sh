#!/bin/bash
set -e

if [ -z "$DOCKER_CONTAINER_REGISTRY" ]; then
    echo "DOCKER_CONTAINER_REGISTRY가 설정되지 않았습니다"
    exit 1
fi

if [ -z "$GITHUB_SHA" ]; then
    echo "GITHUB_SHA가 없습니다. latest 태그만 사용합니다"
    DEPLOY_TAG="latest"
else
    DEPLOY_TAG="${GITHUB_SHA:0:7}"
fi

echo "Registry: $DOCKER_CONTAINER_REGISTRY"
echo "Deploy Tag: $DEPLOY_TAG"

echo "현재 실행 중인 이미지 백업 중"
CURRENT_IMAGE=$(docker inspect dobonglife-backend --format='{{.Config.Image}}' 2>/dev/null || echo "없음")
echo "이전 이미지: $CURRENT_IMAGE"

echo "새 이미지 다운로드 중"
docker pull "$DOCKER_CONTAINER_REGISTRY/dobonglife-backend:latest"

if [ "$DEPLOY_TAG" != "latest" ]; then
    docker pull "$DOCKER_CONTAINER_REGISTRY/dobonglife-backend:$DEPLOY_TAG" 2>/dev/null || echo "   SHA 태그 이미지 없음"
fi

echo "컨테이너 재시작 중"
docker-compose up -d --no-deps dobonglife-backend

echo "헬스체크 대기 중"
MAX_ATTEMPTS=30
ATTEMPT=0

while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
    if docker ps | grep -q "dobonglife-backend.*Up"; then
        sleep 3
        if docker exec dobonglife-backend curl -sf http://localhost:8080/actuator/health > /dev/null 2>&1; then
            echo "헬스체크 성공"
            break
        fi
    fi

    ATTEMPT=$((ATTEMPT + 1))
    echo "   시도 $ATTEMPT/$MAX_ATTEMPTS..."
    sleep 2
done

if [ $ATTEMPT -eq $MAX_ATTEMPTS ]; then
    echo "배포 실패: 헬스체크 타임아웃"
    echo "최근 로그:"
    docker logs dobonglife-backend --tail 50
    echo "이전 이미지: $CURRENT_IMAGE"
    exit 1
fi

echo "배포 성공"
echo "실행 중인 컨테이너"
docker ps --filter "name=dobonglife-backend" --format "table {{.Names}}\t{{.Status}}\t{{.Image}}"

echo "오래된 이미지 정리 중"

docker image prune -f > /dev/null 2>&1

echo "최신 5개 이미지만 유지"
docker images "$DOCKER_CONTAINER_REGISTRY/dobonglife-backend" \
    --format "{{.ID}} {{.CreatedAt}}" | \
    sort -rk 2 | \
    tail -n +6 | \
    awk '{print $1}' | \
    xargs -r docker rmi -f 2>/dev/null || true

echo "보관 중인 이미지"
docker images "$DOCKER_CONTAINER_REGISTRY/dobonglife-backend" --format "table {{.Repository}}:{{.Tag}}\t{{.Size}}\t{{.CreatedAt}}"