#!/bin/bash

# AI-Tide 备份脚本

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 配置
BACKUP_DIR="${BACKUP_DIR:-./backups}"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
DB_NAME="${DB_NAME:-ai_tide}"

echo "================================"
echo "  AI-Tide 备份脚本"
echo "================================"

# 创建备份目录
mkdir -p "$BACKUP_DIR"

echo -e "${GREEN}开始备份...${NC}"

# 备份数据库
echo -e "${YELLOW}备份数据库...${NC}"
docker-compose exec -T mysql mysqldump \
    -u ai_tide_user \
    -p"${DB_PASSWORD:-ai_tide_password}" \
    --databases "$DB_NAME" \
    --routines \
    --triggers \
    > "$BACKUP_DIR/db_backup_$TIMESTAMP.sql" 2>/dev/null

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ 数据库备份完成${NC}"
else
    echo -e "${RED}✗ 数据库备份失败${NC}"
    exit 1
fi

# 备份上传文件（如果有）
UPLOAD_DIR="${UPLOAD_DIR:-./uploads}"
if [ -d "$UPLOAD_DIR" ] && [ "$(ls -A $UPLOAD_DIR 2>/dev/null)" ]; then
    echo -e "${YELLOW}备份上传文件...${NC}"
    tar -czf "$BACKUP_DIR/uploads_backup_$TIMESTAMP.tar.gz" -C "$(dirname $UPLOAD_DIR)" "$(basename $UPLOAD_DIR)"
    echo -e "${GREEN}✓ 上传文件备份完成${NC}"
fi

# 备份环境变量
echo -e "${YELLOW}备份环境变量...${NC}"
if [ -f ".env" ]; then
    cp .env "$BACKUP_DIR/env_backup_$TIMESTAMP"
    echo -e "${GREEN}✓ 环境变量备份完成${NC}"
fi

# 创建备份清单
echo -e "${YELLOW}创建备份清单...${NC}"
cat > "$BACKUP_DIR/manifest_$TIMESTAMP.txt" << EOF
备份时间: $(date -Iseconds)
备份类型: 完整备份
数据库: db_backup_$TIMESTAMP.sql
EOF

if [ -f "$BACKUP_DIR/uploads_backup_$TIMESTAMP.tar.gz" ]; then
    echo "上传文件: uploads_backup_$TIMESTAMP.tar.gz" >> "$BACKUP_DIR/manifest_$TIMESTAMP.txt"
fi

echo -e "${GREEN}✓ 备份清单创建完成${NC}"

# 清理旧备份（保留最近 10 个）
echo -e "${YELLOW}清理旧备份...${NC}"
cd "$BACKUP_DIR"
ls -t db_backup_*.sql 2>/dev/null | tail -n +11 | xargs -r rm --
ls -t uploads_backup_*.tar.gz 2>/dev/null | tail -n +11 | xargs -r rm --
ls -t env_backup_* 2>/dev/null | tail -n +11 | xargs -r rm --
cd - > /dev/null
echo -e "${GREEN}✓ 旧备份清理完成${NC}"

# 显示备份信息
echo ""
echo -e "${GREEN}================================${NC}"
echo -e "${GREEN}  备份完成！${NC}"
echo -e "${GREEN}================================${NC}"
echo ""
echo "备份文件："
echo "  数据库: $BACKUP_DIR/db_backup_$TIMESTAMP.sql"
if [ -f "$BACKUP_DIR/uploads_backup_$TIMESTAMP.tar.gz" ]; then
    echo "  上传文件: $BACKUP_DIR/uploads_backup_$TIMESTAMP.tar.gz"
fi
echo "  清单: $BACKUP_DIR/manifest_$TIMESTAMP.txt"
echo ""
echo "备份目录: $BACKUP_DIR"
echo ""
echo "恢复数据库:"
echo "  docker-compose exec -T mysql mysql -u ai_tide_user -p ai_tide_password ai_tide < $BACKUP_DIR/db_backup_$TIMESTAMP.sql"

# 可选：压缩备份
read -p "是否压缩备份? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}压缩备份...${NC}"
    tar -czf "$BACKUP_DIR/ai_tide_backup_$TIMESTAMP.tar.gz" \
        db_backup_$TIMESTAMP.sql \
        env_backup_$TIMESTAMP \
        manifest_$TIMESTAMP.txt \
        uploads_backup_$TIMESTAMP.tar.gz 2>/dev/null

    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ 压缩完成${NC}"
        echo "  压缩文件: $BACKUP_DIR/ai_tide_backup_$TIMESTAMP.tar.gz"

        # 删除未压缩文件
        rm db_backup_$TIMESTAMP.sql
        rm env_backup_$TIMESTAMP
        rm manifest_$TIMESTAMP.txt
        rm uploads_backup_$TIMESTAMP.tar.gz 2>/dev/null
    else
        echo -e "${RED}✗ 压缩失败${NC}"
    fi
fi

echo ""
echo "备份完成！"
