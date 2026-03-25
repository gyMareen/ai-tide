<template>
  <footer class="footer">
    <div class="footer-container">
      <div class="footer-content">
        <!-- Brand section -->
        <div class="footer-section footer-brand">
          <div class="logo">
            <span class="logo-icon">⚡</span>
            <span class="logo-text">AI-Tide</span>
          </div>
          <p class="footer-desc">
            发现改变世界的 AI 技术
          </p>
          <div class="social-links">
            <a href="#" class="social-link" aria-label="GitHub">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.562-.217.562-.483 0-.237-.019-.768-.027-1.312-3.358.967-4.393-.947-4.55-2.716-.056-.478-.112-.846.093-1.053 1.498-.68 2.486-1.635 2.692-2.917.074-.613.257-1.331.257-2.127 0-1.345-.589-2.523-1.505-2.92.633-.351 1.043-.793 1.461-1.315.09-.483.277-.934.48-1.353.728-.778 1.248-1.647 1.68-2.851 1.68-4.96 0-1.791-.507-3.472-1.413-4.954.143-.295.326-.592 0-.896 0-.306 0-.613 0-.92 0-1.258-2.015-2.433-5.289-2.433-3.667 0-6.642 2.975-6.642 6.642 0 1.268.373 2.444 1.01 3.525.284.31.583.602.867.87.302.721.602 1.463.867 2.226.302-.31.603-.597.867-.87z"/>
              </svg>
            </a>
            <a href="#" class="social-link" aria-label="Twitter">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                <path d="M23.953 4.57a10 10 0 01-2.825.775 4.958 4.958 0 01-2.163-2.723c-.951.555-2.005.959-3.127 1.184a4.92 4.92 0 01-4.853-6.062A10 10 0 012.087 5.8a13.958 13.958 0 01-10.03-4.009c0 .287.022.575.022.863 0 3.401-1.7 6.506-4.47 8.378a4.92 4.92 0 01-1.634 1.92A4.958 4.958 0 0116.47 13.08a10 10 0 01-3.464-.475 4.958 4.958 0 014.661 4.483 9.9 9.9 0 014.97-5.791 10 10 0 01-2.354 3.091"/>
              </svg>
            </a>
            <a href="#" class="social-link" aria-label="Discord">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                <path d="M20.317 4.37a19.791 19.791 0 00-4.885-1.515.074.07 0 00-.068.027c-1.488.739-2.822 1.64-3.989 2.55a.074.07 0 00-.032.058 19.736 19.736 0 00-2.564 4.773.072.072 0 00-.013.066c.018.156.032.313.045.469.878 5.028 2.254 9.678 2.254 9.678 0 001.398-3.886.019-2.048.043-.445.07-.604a.072.072 0 00-.012-.064 19.764 19.764 0 00-2.564-4.776.074.074 0 00-.012-.058c-1.166-.91-2.5-1.811-3.989-2.55a.074.074 0 00-.068-.027 19.786 19.786 0 00-4.885 1.515A3.48 3.48 0 002.22 6.321V12c0 .775.074 1.546.205 2.295 1.525 6.659 4.076 13.762 4.076 13.762 0 00.13-.748 205-1.521 205-2.296v-5.678a3.48 3.48 0 00-1.283-1.951zM7.794 15.034c-.326 0-.59-.265-.59-.59v-2.693c0-.325.264-.59.59-.59h-1.96c-.326 0-.59.265-.59.59v2.693c0 .325.264.59.59.59h1.96zm5.298 0c-.326 0-.59-.265-.59-.59v-2.693c0-.325.264-.59.59-.59h-1.96c-.326 0-.59.265-.59.59v2.693c0 .325.264.59.59.59h1.96zm5.298 0c-.326 0-.59-.265-.59-.59v-2.693c0-.325.264-.59.59-.59h-1.96c-.326 0-.59.265-.59.59v2.693c0 .325.264.59.59.59h1.96z"/>
              </svg>
            </a>
          </div>
        </div>

        <!-- Links sections -->
        <div class="footer-section">
          <h4 class="section-title">探索</h4>
          <router-link to="/" class="footer-link">首页</router-link>
          <router-link to="/search" class="footer-link">搜索</router-link>
          <router-link to="/category/models" class="footer-link">AI 模型</router-link>
          <router-link to="/agent/teams" class="footer-link">智能体</router-link>
        </div>

        <div class="footer-section">
          <h4 class="section-title">资源</h4>
          <router-link to="/category/products" class="footer-link">AI 产品</router-link>
          <router-link to="/category/tools" class="footer-link">AI 工具</router-link>
          <router-link to="/category/frameworks" class="footer-link">AI 框架</router-link>
          <router-link to="/category/papers" class="footer-link">研究论文</router-link>
        </div>

        <div class="footer-section">
          <h4 class="section-title">关于</h4>
          <router-link to="/about" class="footer-link">关于我们</router-link>
          <router-link to="/contact" class="footer-link">联系我们</router-link>
          <router-link to="/privacy" class="footer-link">隐私政策</router-link>
          <router-link to="/terms" class="footer-link">服务条款</router-link>
        </div>
      </div>

      <!-- Bottom bar -->
      <div class="footer-bottom">
        <p class="copyright">
          © {{ currentYear }} AI-Tide. All rights reserved.
        </p>
        <div class="bottom-links">
          <a href="https://github.com/ai-tide/ai-tide" target="_blank" class="bottom-link">
            GitHub
          </a>
          <a href="https://github.com/ai-tide/ai-tide/issues" target="_blank" class="bottom-link">
            反馈
          </a>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const currentYear = computed(() => new Date().getFullYear())
</script>

<style scoped lang="scss">
@use '@/styles/variables.scss' as *;

.footer {
  background: $bg-secondary;
  border-top: 1px solid $border-color;
  padding: 64px 32px 32px;
  margin-top: auto;
}

.footer-container {
  max-width: 1400px;
  margin: 0 auto;
}

.footer-content {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 48px;
  margin-bottom: 48px;
}

.footer-section {
  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 16px;
  }

  .footer-link {
    display: block;
    color: $text-secondary;
    text-decoration: none;
    font-size: 14px;
    margin-bottom: 12px;
    transition: color 0.3s ease;

    &:hover {
      color: $accent-primary;
    }
  }
}

.footer-brand {
  .logo {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 16px;

    .logo-icon {
      font-size: 24px;
    }

    .logo-text {
      font-size: 20px;
      font-weight: 700;
      color: $text-primary;
    }
  }

  .footer-desc {
    font-size: 14px;
    color: $text-secondary;
    margin-bottom: 24px;
  }

  .social-links {
    display: flex;
    gap: 12px;
  }

  .social-link {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
    background: $bg-tertiary;
    border: 1px solid $border-color;
    border-radius: 50%;
    color: $text-secondary;
    transition: all 0.3s ease;

    &:hover {
      background: $accent-primary;
      color: white;
      border-color: $accent-primary;
      transform: translateY(-2px);
    }
  }
}

.footer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 32px;
  border-top: 1px solid $border-color;

  .copyright {
    color: $text-tertiary;
    font-size: 14px;
  }

  .bottom-links {
    display: flex;
    gap: 24px;

    .bottom-link {
      color: $text-secondary;
      text-decoration: none;
      font-size: 14px;
      transition: color 0.3s ease;

      &:hover {
        color: $accent-primary;
      }
    }
  }
}

@media (max-width: 1024px) {
  .footer-content {
    grid-template-columns: repeat(2, 1fr);
    gap: 32px;
  }
}

@media (max-width: 640px) {
  .footer {
    padding: 48px 24px 24px;
  }

  .footer-content {
    grid-template-columns: 1fr;
    gap: 32px;
  }

  .footer-bottom {
    flex-direction: column;
    gap: 16px;
    text-align: center;

    .bottom-links {
      gap: 16px;
    }
  }
}
</style>
