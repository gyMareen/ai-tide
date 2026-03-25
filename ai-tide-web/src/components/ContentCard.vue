<template>
  <router-link :to="`/content/${content.id}`" class="content-card">
    <div
      v-if="content.coverImage"
      class="card-image"
      :style="{ backgroundImage: `url(${content.coverImage})` }"
    >
      <div class="image-overlay"></div>
      <span v-if="content.category" class="card-tag">
        {{ content.category.name }}
      </span>
      <el-tag :type="getTypeColor(content.type)" class="card-type-tag">
        {{ getTypeLabel(content.type) }}
      </el-tag>
    </div>

    <div class="card-body">
      <h3 class="card-title">{{ content.title }}</h3>
      <p class="card-desc">{{ content.description }}</p>

      <div class="card-meta">
        <div class="rating-stars">
          <span
            v-for="n in 5"
            :key="n"
            class="star"
            :class="{ filled: n <= Math.round(content.averageRating) }"
          >★</span>
        </div>

        <div class="card-stats">
          <span class="stat">
            <el-icon><View /></el-icon>
            {{ formatNumber(content.viewCount) }}
          </span>
          <span class="stat">
            <el-icon><Star /></el-icon>
            {{ formatNumber(content.likeCount) }}
          </span>
          <span class="stat">
            <el-icon><ChatDotRound /></el-icon>
            {{ formatNumber(content.commentCount) }}
          </span>
        </div>
      </div>
    </div>
  </router-link>
</template>

<script setup lang="ts">
import type { Content } from '@/types/content'
import { View, Star, ChatDotRound } from '@element-plus/icons-vue'

defineProps<{
  content: Content
}>()

function getTypeLabel(type: string) {
  const labels = { MODEL: 'AI 模型', PRODUCT: 'AI 产品', ARTICLE: '技术文章', TUTORIAL: '教程', RESOURCE: '资源' }
  return labels[type as keyof typeof labels] || type
}

function getTypeColor(type: string) {
  const colors = { MODEL: 'danger', PRODUCT: 'success', ARTICLE: 'warning', TUTORIAL: 'info', RESOURCE: 'primary' }
  return colors[type as keyof typeof colors] || 'default'
}

function formatNumber(num: number) {
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num.toString()
}
</script>

<style scoped lang="scss">
@use '@/styles/variables.scss' as *;

.content-card {
  display: flex;
  flex-direction: column;
  background: $bg-secondary;
  border: 1px solid $border-color;
  border-radius: 16px;
  overflow: hidden;
  text-decoration: none;
  transition: all 0.4s ease;

  &:hover {
    border-color: $accent-primary;
    transform: translateY(-4px);
    box-shadow: $shadow-md;
  }
}

.card-image {
  position: relative;
  height: 180px;
  background-size: cover;
  background-position: center;
  background-color: $bg-tertiary;

  .image-overlay {
    position: absolute;
    inset: 0;
    background: linear-gradient(to bottom, transparent, $bg-secondary);
  }

  .card-tag {
    position: absolute;
    top: 12px;
    right: 12px;
    padding: 4px 12px;
    background: rgba($accent-primary, 0.9);
    color: white;
    border-radius: 8px;
    font-size: 12px;
    font-weight: 500;
  }

  .card-type-tag {
    position: absolute;
    top: 12px;
    left: 12px;
    font-size: 12px;
  }
}

.card-body {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-desc {
  font-size: 14px;
  color: $text-secondary;
  margin-bottom: 16px;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.rating-stars {
  display: flex;
  gap: 2px;
  font-size: 14px;
  color: $text-tertiary;

  .star.filled {
    color: #fbbf24;
  }
}

.card-stats {
  display: flex;
  justify-content: space-between;
  color: $text-tertiary;
  font-size: 12px;

  .stat {
    display: flex;
    align-items: center;
    gap: 4px;
  }
}
</style>
