<template>
  <div class="content-detail-view">
    <div class="detail-container">
      <!-- 返回按钮 -->
      <el-button
        class="back-btn"
        text
        @click="router.back()"
      >
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>

      <template v-if="isLoading">
        <el-skeleton :rows="10" animated />
      </template>

      <template v-else-if="content">
        <!-- 标题区域 -->
        <div class="content-header">
          <div class="content-meta">
            <el-tag :type="getTypeColor(content.type)">
              {{ getTypeLabel(content.type) }}
            </el-tag>
            <span class="category" v-if="content.category">
              {{ content.category.name }}
            </span>
          </div>

          <h1 class="content-title">{{ content.title }}</h1>

          <p class="content-description">{{ content.description }}</p>

          <div class="content-footer">
            <div class="author-info">
              <el-avatar :src="content.author?.avatar" :size="40">
                {{ content.author?.nickname?.charAt(0) }}
              </el-avatar>
              <div class="author-text">
                <div class="author-name">{{ content.author?.nickname }}</div>
                <div class="publish-time">{{ formatDate(content.publishTime) }}</div>
              </div>
            </div>

            <div class="content-stats">
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

        <!-- 操作按钮 -->
        <div class="content-actions">
          <el-button
            :type="isLiked ? 'primary' : 'default'"
            :icon="isLiked ? StarFilled : Star"
            @click="handleLike"
          >
            {{ isLiked ? '已点赞' : '点赞' }}
          </el-button>

          <el-button
            :type="isFavorited ? 'primary' : 'default'"
            :icon="Collection"
            @click="handleFavorite"
          >
            {{ isFavorited ? '已收藏' : '收藏' }}
          </el-button>

          <el-button :icon="Share" @click="handleShare">
            分享
          </el-button>
        </div>

        <!-- 评分区域 -->
        <div class="rating-section">
          <div class="rating-info">
            <div class="rating-score">{{ content.averageRating.toFixed(1) }}</div>
            <el-rate v-model="userRating" allow-half @change="handleRating" />
            <div class="rating-count">{{ content.ratingCount }} 人评分</div>
          </div>
        </div>

        <!-- 内容正文 -->
        <div class="content-body">
          <div v-html="renderContent(content.content)" class="markdown-content"></div>
        </div>

        <!-- 标签 -->
        <div v-if="content.tags?.length" class="content-tags">
          <el-tag
            v-for="tag in content.tags"
            :key="tag.id"
            class="tag-item"
            @click="router.push(`/search?tag=${tag.id}`)"
          >
            #{{ tag.name }}
          </el-tag>
        </div>

        <!-- 相关链接 -->
        <div v-if="content.relatedLinks?.length" class="related-links">
          <h3 class="section-title">相关链接</h3>
          <a
            v-for="link in content.relatedLinks"
            :key="link.id"
            :href="link.url"
            target="_blank"
            class="link-item"
          >
            <el-icon><Link /></el-icon>
            <div class="link-info">
              <div class="link-title">{{ link.title }}</div>
              <div v-if="link.description" class="link-desc">{{ link.description }}</div>
            </div>
          </a>
        </div>

        <!-- 评论区 -->
        <div class="comments-section">
          <h3 class="section-title">评论 ({{ content.commentCount }})</h3>

          <!-- 评论输入框 -->
          <div class="comment-input" v-if="userStore.isAuthenticated">
            <el-input
              v-model="commentText"
              type="textarea"
              :rows="3"
              placeholder="发表你的评论..."
              maxlength="500"
              show-word-limit
            />
            <el-button
              type="primary"
              :loading="isSubmittingComment"
              :disabled="!commentText.trim()"
              @click="submitComment"
            >
              发表评论
            </el-button>
          </div>

          <div v-else class="comment-login">
            <el-alert type="info" :closable="false">
              <router-link to="/login">登录</router-link> 后发表评论
            </el-alert>
          </div>

          <!-- 评论列表 -->
          <div v-if="comments.length" class="comment-list">
            <div
              v-for="comment in comments"
              :key="comment.id"
              class="comment-item"
            >
              <el-avatar :src="comment.userAvatar" :size="40">
                {{ comment.username?.charAt(0) }}
              </el-avatar>
              <div class="comment-content">
                <div class="comment-header">
                  <span class="comment-author">{{ comment.username }}</span>
                  <span class="comment-time">{{ formatDate(comment.createdAt) }}</span>
                </div>
                <div class="comment-text">{{ comment.content }}</div>
              </div>
            </div>
          </div>
        </div>
      </template>

      <el-empty v-else description="内容不存在或已删除" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useContentStore } from '@/stores/content'
import { useInteractionStore } from '@/stores/interaction'
import { ArrowLeft, View, Star, StarFilled, Collection, Share, ChatDotRound, Link } from '@element-plus/icons-vue'
import MarkdownIt from 'markdown-it'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const contentStore = useContentStore()
const interactionStore = useInteractionStore()

const md = new MarkdownIt()

interface Comment {
  id: string
  content: string
  username?: string
  userAvatar?: string
  createdAt: string
}

const content = computed(() => contentStore.currentContent)
const isLoading = computed(() => contentStore.isLoading)
const isLiked = computed(() => interactionStore.likedContents.has(route.params.id as string))
const isFavorited = computed(() => interactionStore.favoriteContents.has(route.params.id as string))
const userRating = ref(0)

const commentText = ref('')
const comments = ref<Comment[]>([])
const isSubmittingComment = ref(false)

async function fetchContent() {
  const id = route.params.id as string
  await contentStore.fetchContentDetail(id)

  // 加载用户评分
  if (userStore.isAuthenticated) {
    const ratings = interactionStore.userRatings as any
    const rating = ratings.value.get(id)
    if (rating !== undefined) {
      userRating.value = rating
    }
  }

  // 加载评论
  await fetchComments()
}

async function fetchComments() {
  try {
    const response = await fetch(`/api/content/${route.params.id}/comments`)
    if (response.ok) {
      const data = await response.json()
      if (data.code === 200) {
        comments.value = data.data?.list || []
      }
    }
  } catch (e) {
    console.error('Failed to fetch comments:', e)
  }
}

async function handleLike() {
  try {
    await interactionStore.toggleLike(route.params.id as string)
  } catch (e: any) {
    console.error('Like failed:', e)
  }
}

async function handleFavorite() {
  try {
    await interactionStore.toggleFavorite(route.params.id as string)
  } catch (e: any) {
    console.error('Favorite failed:', e)
  }
}

async function handleRating(rating: number) {
  try {
    await interactionStore.rateContent(route.params.id as string, rating)
  } catch (e: any) {
    console.error('Rating failed:', e)
    userRating.value = 0
  }
}

function handleShare() {
  const url = window.location.href
  navigator.clipboard.writeText(url)
  // TODO: Show toast message
}

async function submitComment() {
  if (!commentText.value.trim()) return

  isSubmittingComment.value = true
  try {
    await interactionStore.submitComment(route.params.id as string, commentText.value)
    commentText.value = ''
    await fetchComments()
  } catch (e: any) {
    console.error('Submit comment failed:', e)
  } finally {
    isSubmittingComment.value = false
  }
}

function renderContent(markdown: string) {
  return md.render(markdown)
}

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

function formatDate(date?: string) {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

onMounted(() => {
  fetchContent()
})
</script>

<style scoped lang="scss">
@use '@/styles/variables.scss' as *;

.content-detail-view {
  min-height: 100vh;
  background: $bg-primary;
  padding: 32px;
}

.detail-container {
  max-width: 900px;
  margin: 0 auto;
}

.back-btn {
  margin-bottom: 24px;
  color: $text-secondary;
  font-size: 16px;
}

.content-header {
  margin-bottom: 32px;
}

.content-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;

  .category {
    color: $text-secondary;
    font-size: 14px;
  }
}

.content-title {
  font-size: 36px;
  font-weight: 700;
  color: $text-primary;
  line-height: 1.3;
  margin-bottom: 16px;
}

.content-description {
  font-size: 18px;
  color: $text-secondary;
  line-height: 1.6;
  margin-bottom: 24px;
}

.content-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 0;
  border-top: 1px solid $border-color;
  border-bottom: 1px solid $border-color;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;

  .author-text {
    .author-name {
      font-weight: 600;
      color: $text-primary;
      margin-bottom: 4px;
    }

    .publish-time {
      font-size: 12px;
      color: $text-tertiary;
    }
  }
}

.content-stats {
  display: flex;
  gap: 24px;

  .stat {
    display: flex;
    align-items: center;
    gap: 4px;
    color: $text-secondary;
    font-size: 14px;
  }
}

.content-actions {
  display: flex;
  gap: 12px;
  margin-bottom: 32px;
}

.rating-section {
  padding: 24px;
  background: $bg-secondary;
  border: 1px solid $border-color;
  border-radius: 16px;
  margin-bottom: 32px;

  .rating-info {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  .rating-score {
    font-size: 48px;
    font-weight: 700;
    color: $accent-primary;
  }

  .rating-count {
    font-size: 14px;
    color: $text-secondary;
  }
}

.content-body {
  margin-bottom: 32px;

  .markdown-content {
    color: $text-primary;
    line-height: 1.8;
    font-size: 16px;
  }
}

.content-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 32px;

  .tag-item {
    cursor: pointer;
  }
}

.related-links {
  margin-bottom: 48px;

  .section-title {
    font-size: 24px;
    font-weight: 700;
    color: $text-primary;
    margin-bottom: 24px;
  }

  .link-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 16px;
    background: $bg-secondary;
    border: 1px solid $border-color;
    border-radius: 12px;
    margin-bottom: 12px;
    text-decoration: none;
    transition: all 0.3s ease;

    &:hover {
      border-color: $accent-primary;
      background: rgba($accent-primary, 0.05);
    }

    .link-info {
      flex: 1;

      .link-title {
        font-weight: 600;
        color: $accent-primary;
        margin-bottom: 4px;
      }

      .link-desc {
        font-size: 14px;
        color: $text-secondary;
      }
    }
  }
}

.comments-section {
  margin-bottom: 48px;

  .section-title {
    font-size: 24px;
    font-weight: 700;
    color: $text-primary;
    margin-bottom: 24px;
  }

  .comment-input {
    margin-bottom: 32px;

    .el-textarea {
      margin-bottom: 12px;
    }
  }

  .comment-login {
    margin-bottom: 24px;
  }

  .comment-list {
    .comment-item {
      display: flex;
      gap: 12px;
      padding: 20px;
      background: $bg-secondary;
      border-radius: 12px;
      margin-bottom: 12px;

      .comment-content {
        flex: 1;

        .comment-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;

          .comment-author {
            font-weight: 600;
            color: $text-primary;
          }

          .comment-time {
            font-size: 12px;
            color: $text-tertiary;
          }
        }

        .comment-text {
          color: $text-secondary;
          line-height: 1.6;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .content-detail-view {
    padding: 16px;
  }

  .content-title {
    font-size: 24px;
  }

  .content-description {
    font-size: 16px;
  }

  .content-footer {
    flex-direction: column;
    gap: 16px;
  }

  .content-actions {
    flex-wrap: wrap;
  }

  .rating-section {
    flex-direction: column;
  }
}
</style>
