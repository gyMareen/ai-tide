<template>
  <div class="comment-list">
    <h3 class="comment-title">评论 ({{ comments.length }})</h3>

    <!-- Comment input -->
    <div v-if="isAuthenticated" class="comment-input">
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
        :loading="isLoading"
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

    <!-- Comments -->
    <div v-if="comments.length > 0" class="comments">
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

          <p class="comment-text">{{ comment.content }}</p>

          <div class="comment-actions">
            <el-button
              v-if="isAuthenticated && comment.userId === currentUserId"
              type="danger"
              text
              size="small"
              :loading="deletingId === comment.id"
              @click="deleteComment(comment.id)"
            >
              删除
            </el-button>
          </div>

          <!-- Replies -->
          <div v-if="comment.replies && comment.replies.length > 0">
            <div
              v-for="reply in comment.replies"
              :key="reply.id"
              class="comment-reply"
            >
              <el-avatar :src="reply.userAvatar" :size="32">
                {{ reply.username?.charAt(0) }}
              </el-avatar>
              <div class="reply-content">
                <div class="reply-header">
                  <span class="reply-author">{{ reply.username }}</span>
                  <span class="reply-time">{{ formatDate(reply.createdAt) }}</span>
                </div>
                <p class="reply-text">{{ reply.content }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-empty v-else description="暂无评论" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { Comment } from '@/types/interaction'

const props = defineProps<{
  comments: Comment[]
  isAuthenticated: boolean
  currentUserId?: string
  contentId: string
}>()

const emit = defineEmits<{
  submit: [content: string]
  delete: [id: string]
}>()

const commentText = ref('')
const isLoading = ref(false)
const deletingId = ref<string | null>(null)

function submitComment() {
  if (!commentText.value.trim()) return

  isLoading.value = true
  emit('submit', commentText.value)
  commentText.value = ''
  isLoading.value = false
}

function deleteComment(id: string) {
  deletingId.value = id
  emit('delete', id)
  deletingId.value = null
}

function formatDate(date: string) {
  return new Date(date).toLocaleDateString('zh-CN')
}
</script>

<style scoped lang="scss">
@use '@/styles/variables.scss' as *;

.comment-list {
  .comment-title {
    font-size: 18px;
    font-weight: 600;
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

  .comments {
    .comment-item {
      display: flex;
      gap: 12px;
      padding: 20px;
      background: $bg-secondary;
      border-radius: 12px;
      margin-bottom: 16px;
    }
  }

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
      margin-bottom: 12px;
    }

    .comment-actions {
      display: flex;
      gap: 8px;
    }
  }

  .comment-reply {
    display: flex;
    gap: 12px;
    padding: 12px 0 0 20px;
    border-left: 2px solid $border-light;
    margin-left: 8px;
    margin-top: 8px;

    .reply-content {
      flex: 1;

      .reply-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 6px;

        .reply-author {
          font-weight: 500;
          font-size: 13px;
          color: $text-primary;
        }

        .reply-time {
          font-size: 11px;
          color: $text-tertiary;
        }
      }

      .reply-text {
        color: $text-secondary;
        font-size: 13px;
        line-height: 1.5;
      }
    }
  }
}
</style>
