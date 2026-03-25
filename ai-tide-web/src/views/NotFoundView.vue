<template>
  <div class="not-found-view">
    <div class="not-found-container">
      <div class="not-found-content">
        <div class="error-code">404</div>
        <h1 class="error-title">页面不存在</h1>
        <p class="error-description">
          抱歉，您访问的页面不存在或已被删除
        </p>

        <div class="error-actions">
          <el-button type="primary" @click="router.push('/')">
            返回首页
          </el-button>
          <el-button @click="router.back()">
            返回上一页
          </el-button>
        </div>

        <div class="error-tips">
          <h3>您可能想访问：</h3>
          <div class="quick-links">
            <router-link to="/" class="quick-link">
              🏠 首页
            </router-link>
            <router-link to="/search" class="quick-link">
              🔍 搜索
            </router-link>
            <router-link to="/agent/teams" class="quick-link">
              🤖 智能体
            </router-link>
            <router-link :to="`/category/models`" class="quick-link">
              🧠 AI 模型
            </router-link>
          </div>
        </div>
      </div>

      <div class="decoration">
        <div class="orb orb-1"></div>
        <div class="orb orb-2"></div>
        <div class="orb orb-3"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<style scoped lang="scss">
@use '@/styles/variables.scss' as *;

.not-found-view {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $bg-primary;
  padding: 32px;
  position: relative;
  overflow: hidden;
}

.not-found-container {
  position: relative;
  z-index: 1;
  max-width: 600px;
  width: 100%;
}

.not-found-content {
  text-align: center;
}

.error-code {
  font-size: 120px;
  font-weight: 800;
  background: linear-gradient(135deg, $accent-primary, $accent-secondary);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 0 60px rgba($accent-primary, 0.3);
  line-height: 1;
  margin-bottom: 16px;
}

.error-title {
  font-size: 36px;
  font-weight: 700;
  color: $text-primary;
  margin-bottom: 16px;
}

.error-description {
  font-size: 18px;
  color: $text-secondary;
  margin-bottom: 48px;
  line-height: 1.6;
}

.error-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-bottom: 64px;
}

.error-tips {
  padding: 32px;
  background: $bg-secondary;
  border: 1px solid $border-color;
  border-radius: 16px;

  h3 {
    font-size: 18px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 16px;
  }
}

.quick-links {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;
}

.quick-link {
  padding: 10px 20px;
  background: rgba($accent-primary, 0.1);
  color: $accent-primary;
  border: 1px solid rgba($accent-primary, 0.3);
  border-radius: 8px;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s ease;

  &:hover {
    background: $accent-primary;
    color: white;
    transform: translateY(-2px);
  }
}

.decoration {
  position: fixed;
  inset: 0;
  pointer-events: none;
}

.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.3;

  &.orb-1 {
    width: 300px;
    height: 300px;
    background: $accent-primary;
    top: -150px;
    left: -150px;
  }

  &.orb-2 {
    width: 200px;
    height: 200px;
    background: $accent-secondary;
    bottom: -100px;
    right: -100px;
  }

  &.orb-3 {
    width: 150px;
    height: 150px;
    background: $feature-purple;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
  }
}

@media (max-width: 768px) {
  .error-code {
    font-size: 80px;
  }

  .error-title {
    font-size: 28px;
  }

  .error-description {
    font-size: 16px;
  }

  .error-actions {
    flex-direction: column;
    width: 100%;

    .el-button {
      width: 100%;
    }
  }

  .quick-links {
    flex-direction: column;
  }

  .quick-link {
    width: 100%;
    text-align: center;
  }
}
</style>
