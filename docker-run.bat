@echo off

echo ===== Docker Compose Down =====
docker compose down

echo ===== Docker Compose Build =====
docker compose build

echo ===== Docker Compose Up =====
docker compose up

pause