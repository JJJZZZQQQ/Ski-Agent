#!/bin/bash
# 滑雪 Agent 后端启动脚本
# 自动加载 .env 环境变量并启动 Spring Boot

set -a
source "$(dirname "$0")/.env"
set +a

echo "🚀 启动 Ski Agent Backend..."
echo "   DB: ${DB_HOST}:${DB_PORT}/${DB_NAME}"
echo "   Model: ${DEEPSEEK_MODEL}"
echo ""

mvn spring-boot:run -pl ski-agent-api