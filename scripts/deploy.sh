#!/bin/bash

# AI-Tide 部署脚本

set -e

echo "================================"
echo "  AI-Tide 部署脚本"
echo "================================"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查 Docker
if ! command -v docker &> /dev/null; then
    echo -e "${RED}错误: Docker 未安装${NC}"
    exit 1
fi

# 检查 Docker Compose
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}错误: Docker Compose 未安装${NC}"
    exit 1
fi

# 检查 .env 文件
if [ ! -f ".env" ]; then
    echo -e "${YELLOW}警告: .env 文件不存在，使用 .env.example${NC}"
    if [ -f ".env.example" ]; then
        cp .env.example .env
        echo -e "${YELLOW} .env 已创建，请编辑后重新运行${NC}"
        exit 1
    else
        echo -e "${RED}错误: .env.example 文件不存在${NC}"
        exit 1
    fi
fi

# 显示当前配置
echo -e "${GREEN}当前配置:${NC}"
echo -e "  前端端口: ${APP_PORT:-3000}"
echo -e "  后端端口: ${API_PORT:-8080}"
echo -e "  数据库端口: ${DB_PORT:-3306}"
echo -e "  Redis 端口: ${REDIS_PORT:-6379}"

# 询问是否继续
read -p "是否继续部署? (y/n) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "部署已取消"
    exit 0
fi

# 构建镜像
echo -e "${GREEN}正在构建 Docker 镜像...${NC}"
docker-compose build

# 停止旧容器
echo -e "${GREEN}停止旧容器...${NC}"
docker-compose down

# 启动服务
echo -e "${GREEN}启动服务...${NC}"
docker-compose up -d

# 等待服务启动
echo -e "${GREEN}等待服务启动...${NC}"
sleep 10

# 检查服务状态
echo -e "${GREEN}检查服务状态...${NC}"
docker-compose ps

# 运行健康检查
echo -e "${GREEN}运行健康检查...${NC}"
check_service() {
    local service=$1
    local url=$2
    local max_attempts=30
    local attempt=0

    echo -n "检查 $service... "
    while [ $attempt -lt $max_attempts ]; do
        if curl -s -f "$url" > /dev/null 2>&1; then
            echo -e "${GREEN}✓${NC}"
            return 0
        fi
        attempt=$((attempt + 1))
        echo -n "."
        sleep 2
    done
    echo -e "${RED}✗${NC}"
    return 1
}

# 检查各服务
check_service "Nginx" "http://localhost:${APP_PORT:-3000}/health" || true
check_service "API" "http://localhost:${API_PORT:-8080}/actuator/health" || true
check_service "MySQL" "mysql://localhost:3306" || true

echo ""
echo -e "${GREEN}================================${NC}"
echo -e "${GREEN}  部署完成！${NC}"
echo -e "${GREEN}================================${NC}"
echo ""
echo "访问地址："
echo "  前端: http://localhost:${APP_PORT:-3000}"
echo "  API: http://localhost:${API_PORT:-8080}"
echo "  API 文档: http://localhost:${API_PORT:-8080}/swagger-ui.html"
echo ""
echo "查看日志:"
echo "  docker-compose logs -f"
echo ""
echo "停止服务:"
echo "  docker-compose down"
