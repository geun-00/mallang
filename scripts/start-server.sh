#!/bin/bash
set -e

docker stop mallang-server || true
docker rm mallang-server || true
docker pull 200761583844.dkr.ecr.ap-northeast-2.amazonaws.com/mallang-server:latest
docker run -d \
  --name mallang-server \
  -p 8080:8080 \
  --env-file /home/ubuntu/.mallang/.env \
  200761583844.dkr.ecr.ap-northeast-2.amazonaws.com/mallang-server:latest

echo "서버 배포 완료"
