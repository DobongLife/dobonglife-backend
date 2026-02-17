#!/bin/bash
# Swagger UI 로컬 실행 스크립트
# MySQL/Redis/Firebase 없이 H2 기반으로 Swagger만 확인 가능

set -e

# JAVA_HOME 설정
export JAVA_HOME=/home/shin/.jdks/jdk-21.0.2
export PATH=$JAVA_HOME/bin:$PATH

echo "=== Java 버전 확인 ==="
java -version

echo ""
echo "=== Gradle 빌드 (테스트 스킵) ==="
./gradlew build -x test

echo ""
echo "=== Spring Boot 실행 (swagger 프로필) ==="
echo "Swagger UI: http://localhost:8080/docs/swagger"
echo ""

SPRING_PROFILES_ACTIVE=swagger java -jar build/libs/dobonglife-0.0.1-SNAPSHOT.jar
