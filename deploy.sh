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

echo "이미지 다운로드 중"
docker-compose pull

echo "컨테이너 재시작 중"
docker-compose up -d --no-deps dobonglife-backend

echo "헬스체크 대기 중"
sleep 10

if docker ps | grep -q dobonglife-backend; then
    echo "배포 성공"
    docker ps --filter "name=dobonglife-backend"
else
    echo "배포 실패"
    docker logs dobonglife-backend --tail 50
    exit 1
fi

echo "이미지 정리 중"
docker image prune -af --filter "until=24h"

echo "배포 완료"