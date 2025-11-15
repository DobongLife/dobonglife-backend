#!/bin/bash
set -e

if [ -z "$1" ]; then
    echo "사용법: $0 <이미지태그>"
    echo ""
    echo "사용 가능한 이미지:"
    docker images pyeonk/dobonglife-backend --format "table {{.Tag}}\t{{.CreatedAt}}"
    exit 1
fi

ROLLBACK_TAG=$1
DOCKER_CONTAINER_REGISTRY=${DOCKER_CONTAINER_REGISTRY}

echo "롤백 시작: $ROLLBACK_TAG"

if ! docker images "$DOCKER_CONTAINER_REGISTRY/dobonglife-backend:$ROLLBACK_TAG" --format "{{.ID}}" | grep -q .; then
    echo "이미지를 찾을 수 없습니다: $ROLLBACK_TAG"
    echo "사용 가능한 이미지:"
    docker images "$DOCKER_CONTAINER_REGISTRY/dobonglife-backend" --format "table {{.Tag}}\t{{.CreatedAt}}"
    exit 1
fi

echo "latest 태그를 $ROLLBACK_TAG 로 변경"
docker tag "$DOCKER_CONTAINER_REGISTRY/dobonglife-backend:$ROLLBACK_TAG" \
           "$DOCKER_CONTAINER_REGISTRY/dobonglife-backend:latest"

echo "컨테이너 재시작"
docker-compose up -d --no-deps dobonglife-backend

sleep 10
if docker ps | grep -q "dobonglife-backend.*Up"; then
    echo "롤백 성공!"
    docker ps --filter "name=dobonglife-backend"
else
    echo "롤백 실패"
    docker logs dobonglife-backend --tail 50
    exit 1
fi