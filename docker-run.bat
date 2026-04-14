@echo off

echo ===== Docker Compose Down =====
docker compose down

echo ===== Docker Compose Build =====
docker compose build --no-cache

echo ===== Docker Compose Up =====
docker compose up

pause