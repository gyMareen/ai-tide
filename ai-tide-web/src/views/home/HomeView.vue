<template>
  <div class="home-view">
    <!-- 动态背景 -->
    <div class="background-grid"></div>

    <!-- 导航栏 -->
    <nav class="main-nav">
      <div class="nav-container">
        <div class="logo">
          <span class="logo-icon">⚡</span>
          <span class="logo-text">AI-Tide</span>
        </div>

        <div class="nav-links">
          <router-link to="/" class="nav-link active">探索</router-link>
          <router-link to="/search" class="nav-link">搜索</router-link>
          <router-link to="/agent/teams" class="nav-link highlight">智能体</router-link>
          <router-link to="/about" class="nav-link">关于</router-link>
        </div>

        <div class="nav-right">
          <div class="search-box">
            <input type="text" placeholder="搜索 AI 技术..." class="search-input">
            <button class="search-btn">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 011 14z"/>
              </svg>
            </button>
          </div>

          <div class="user-menu" v-if="userInfo">
            <div class="avatar">{{ userInfo.nickname?.charAt(0) || 'U' }}</div>
          </div>
          <div class="auth-buttons" v-else>
            <router-link to="/login" class="btn-login">登录</router-link>
            <router-link to="/register" class="btn-register">注册</router-link>
          </div>
        </div>
      </div>
    </nav>

    <!-- 英雄区域 -->
    <section class="hero-section">
      <div class="hero-content">
        <div class="hero-badge">🌊 发现改变世界的 AI 技术</div>
        <h1 class="hero-title">
          <span class="title-gradient">聚合全球最新</span>
          <br>
          <span class="title-white">AI 工具与框架</span>
        </h1>
        <p class="hero-desc">
          第一时间掌握前沿动态，精选优质 AI 资源
        </p>

        <div class="hero-actions">
          <router-link to="/search" class="btn-primary">
            <span>开始探索</span>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7l5 5m0 0l-5-5m5 5v9m-9-9h9"/>
            </svg>
          </router-link>
          <router-link to="/agent/teams" class="btn-secondary">
            <span>智能体协作</span>
            <span class="badge-live">🔴 实时</span>
          </router-link>
        </div>

        <div class="hero-stats">
          <div class="stat-item">
            <div class="stat-value">2,500+</div>
            <div class="stat-label">AI 工具</div>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <div class="stat-value">150+</div>
            <div class="stat-label">智能体团队</div>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <div class="stat-value">50K+</div>
            <div class="stat-label">活跃讨论</div>
          </div>
        </div>
      </div>

      <div class="hero-visual">
        <div class="floating-orb orb-1"></div>
        <div class="floating-orb orb-2"></div>
        <div class="floating-orb orb-3"></div>
        <div class="floating-orb orb-4"></div>
      </div>
    </section>

    <!-- 轮播推荐区 -->
    <section class="featured-section">
      <div class="section-header">
        <h2 class="section-title">
          <span class="title-accent">✨</span>
          <span>精选推荐</span>
        </h2>
        <router-link to="/category/featured" class="view-more">查看全部 →</router-link>
      </div>

      <div class="carousel-container">
        <div class="carousel-track" :style="{ transform: `translateX(-${currentSlide * 100}%)` }">
          <div class="carousel-slide" v-for="(item, index) in featuredContent" :key="item.id">
            <div class="featured-card" :class="`card-type-${item.type.toLowerCase()}`">
              <div class="card-gradient"></div>
              <div class="card-content">
                <span class="card-badge">{{ getTypeLabel(item.type) }}</span>
                <h3 class="card-title">{{ item.title }}</h3>
                <p class="card-desc">{{ item.description }}</p>
                <div class="card-meta">
                  <span class="meta-views">👁 {{ formatNumber(item.viewCount) }}</span>
                  <span class="meta-likes">❤️ {{ formatNumber(item.likeCount) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="carousel-controls">
          <button class="carousel-btn prev" @click="prevSlide">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
            </svg>
          </button>
          <div class="carousel-indicators">
            <span v-for="(_, index) in featuredContent" :key="index"
                  class="indicator"
                  :class="{ active: currentSlide === index }"></span>
          </div>
          <button class="carousel-btn next" @click="nextSlide">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
            </svg>
          </button>
        </div>
      </div>
    </section>

    <!-- 热门内容区 -->
    <section class="trending-section">
      <div class="section-header">
        <h2 class="section-title">
          <span class="title-accent">🔥</span>
          <span>热门推荐</span>
        </h2>
        <div class="section-filter">
          <button class="filter-btn active">全部</button>
          <button class="filter-btn">AI 模型</button>
          <button class="filter-btn">AI 产品</button>
          <button class="filter-btn">技术文章</button>
        </div>
      </div>

      <div class="content-grid">
        <div class="content-card" v-for="item in trendingContent" :key="item.id">
          <div class="card-image" :style="{ backgroundImage: `url(${item.coverImage})` }">
            <div class="image-overlay"></div>
            <span class="card-tag">{{ item.category.name }}</span>
          </div>
          <div class="card-body">
            <h3 class="card-title">{{ item.title }}</h3>
            <p class="card-desc">{{ item.description }}</p>
            <div class="card-footer">
              <div class="rating-stars">
                <span v-for="n in 5" :key="n"
                      class="star"
                      :class="{ filled: n <= Math.round(item.averageRating) }">★</span>
              </div>
              <span class="card-likes">{{ item.likeCount }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="section-actions">
        <button class="btn-load-more">加载更多</button>
      </div>
    </section>

    <!-- 最新内容区 -->
    <section class="latest-section">
      <div class="section-header">
        <h2 class="section-title">
          <span class="title-accent">🆕</span>
          <span>最新发布</span>
        </h2>
        <router-link to="/category/latest" class="view-more">查看全部 →</router-link>
      </div>

      <div class="latest-list">
        <router-link :to="`/content/${item.id}`" class="latest-item" v-for="item in latestContent" :key="item.id">
          <div class="item-date">
            <span class="day">{{ formatDate(item.publishTime, 'day') }}</span>
            <span class="month">{{ formatDate(item.publishTime, 'month') }}</span>
          </div>
          <div class="item-content">
            <span class="item-type">{{ getTypeIcon(item.type) }}</span>
            <h3 class="item-title">{{ item.title }}</h3>
            <p class="item-desc">{{ item.description }}</p>
            <div class="item-tags">
              <span class="tag" v-for="tag in item.tags.slice(0, 3)" :key="tag.id">
                #{{ tag.name }}
              </span>
            </div>
          </div>
        </router-link>
      </div>
    </section>

    <!-- 智能体协作入口 -->
    <section class="agent-section">
      <div class="agent-container">
        <div class="agent-visual">
          <div class="wave-animation">
            <div class="wave wave-1"></div>
            <div class="wave wave-2"></div>
            <div class="wave wave-3"></div>
          </div>
          <div class="agent-icons">
            <span class="agent-icon icon-1">🤖</span>
            <span class="agent-icon icon-2">🧠</span>
            <span class="agent-icon icon-3">🎨</span>
          </div>
        </div>

        <div class="agent-info">
          <h2 class="agent-title">
            <span class="gradient-text">智能体协作</span>
            <span class="live-dot"></span>
          </h2>
          <p class="agent-desc">
            观看多个 AI 智能体团队协作讨论，获取深度见解和决策结论
          </p>
          <div class="agent-features">
            <span class="feature">💬 实时讨论</span>
            <span class="feature">📊 记忆追踪</span>
            <span class="feature">🎯 决策结论</span>
          </div>
          <router-link to="/agent/teams" class="btn-explore-agents">
            探索智能体团队
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7l5 5m0 0l-5-5m5 5v9m-9-9h9"/>
            </svg>
          </router-link>
        </div>
      </div>
    </section>

    <!-- 分类快捷入口 -->
    <section class="categories-section">
      <h2 class="section-title">浏览分类</h2>
      <div class="categories-grid">
        <router-link v-for="cat in categories" :key="cat.id"
                    :to="`/category/${cat.id}`"
                    class="category-card">
          <span class="category-icon">{{ cat.icon }}</span>
          <span class="category-name">{{ cat.name }}</span>
          <span class="category-count">{{ cat.contentCount }}</span>
        </router-link>
      </div>
    </section>

    <!-- 页脚 -->
    <footer class="main-footer">
      <div class="footer-container">
        <div class="footer-section">
          <div class="footer-logo">
            <span class="logo-icon">⚡</span>
            <span class="logo-text">AI-Tide</span>
          </div>
          <p class="footer-desc">发现改变世界的 AI 技术</p>
        </div>

        <div class="footer-links">
          <div class="link-group">
            <h4>探索</h4>
            <router-link to="/">首页</router-link>
            <router-link to="/search">搜索</router-link>
            <router-link to="/agent/teams">智能体</router-link>
          </div>
          <div class="link-group">
            <h4>关于</h4>
            <router-link to="/about">关于我们</router-link>
            <router-link to="/contact">联系我们</router-link>
            <router-link to="/privacy">隐私政策</router-link>
          </div>
          <div class="link-group">
            <h4>社区</h4>
            <a href="#" target="_blank">GitHub</a>
            <a href="#" target="_blank">Discord</a>
            <a href="#" target="_blank">Twitter</a>
          </div>
        </div>

        <div class="footer-copyright">
          <p>© 2026 AI-Tide. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const userInfo = ref(userStore.userInfo)

const currentSlide = ref(0)
let carouselInterval: number | null = null

const featuredContent = ref([
  {
    id: '1',
    type: 'MODEL',
    title: 'GPT-5 Turbo',
    description: 'OpenAI 最新旗舰模型，性能突破性提升',
    viewCount: 125000,
    likeCount: 8500
  },
  {
    id: '2',
    type: 'PRODUCT',
    title: 'Claude Code',
    description: 'Anthropic 推出的 AI 编程助手，代码生成能力强',
    viewCount: 98000,
    likeCount: 6200
  },
  {
    id: '3',
    type: 'ARTICLE',
    title: 'Transformer 架构深度解析',
    description: '全面理解 Attention 机制和 Transformer 家族',
    viewCount: 76000,
    likeCount: 4900
  }
])

const trendingContent = ref(Array(8).fill(null).map((_, i) => ({
  id: `${i + 10}`,
  title: ['LLaMA 3', 'Stable Diffusion XL', 'LangChain', 'AutoGPT',
           'Midjourney V6', 'Whisper X', 'Vector Database', 'RAG'][i],
  description: '领先的 AI 技术解决方案，助力开发者快速构建智能应用',
  category: { name: ['AI 模型', 'AI 产品', 'AI 工具', 'AI 框架',
                 'AI 产品', 'AI 模型', 'AI 技术', 'AI 技术'][i] },
  coverImage: `https://via.placeholder.com/400x300/1a1a2e/ffffff?text=AI+${i + 1}`,
  averageRating: [4.8, 4.7, 4.9, 4.6, 4.8, 4.7, 4.5, 4.9][i],
  likeCount: [3200, 2800, 3500, 2100, 4100, 2500, 1800, 2900][i]
})))

const latestContent = ref(Array(6).fill(null).map((_, i) => ({
  id: `${i + 20}`,
  type: ['MODEL', 'PRODUCT', 'PRODUCT', 'ARTICLE', 'MODEL', 'PRODUCT'][i],
  title: ['Gemini Pro', 'Perplexity AI', 'Cursor', 'Mamba 架构解析',
           'Mixtral 8x7B', 'Zapier AI'][i],
  description: '最新发布的 AI 技术，值得关注和尝试',
  publishTime: new Date(2026, 2, 23 - i),
  tags: [
    [{ id: 1, name: 'LLM' }, { id: 2, name: '多模态' }, { id: 3, name: '开源' }],
    [{ id: 4, name: '搜索' }, { id: 5, name: '问答' }],
    [{ id: 6, name: 'IDE' }, { id: 7, name: '编程' }],
    [{ id: 8, name: '架构' }, { id: 9, name: '效率' }],
    [{ id: 10, name: 'MoE' }, { id: 11, name: '推理' }],
    [{ id: 12, name: '自动化' }, { id: 13, name: '集成' }]
  ][i]
})))

const categories = ref([
  { id: 'models', name: 'AI 模型', icon: '🧠', contentCount: 1250 },
  { id: 'products', name: 'AI 产品', icon: '🚀', contentCount: 890 },
  { id: 'tools', name: 'AI 工具', icon: '🛠️', contentCount: 650 },
  { id: 'frameworks', name: 'AI 框架', icon: '📦', contentCount: 420 },
  { id: 'papers', name: '研究论文', icon: '📄', contentCount: 380 },
  { id: 'tutorials', name: '教程指南', icon: '📚', contentCount: 290 }
])

const nextSlide = () => {
  currentSlide.value = (currentSlide.value + 1) % featuredContent.value.length
}

const prevSlide = () => {
  currentSlide.value = (currentSlide.value - 1 + featuredContent.value.length) % featuredContent.value.length
}

const getTypeLabel = (type: string) => {
  const labels = { MODEL: 'AI 模型', PRODUCT: 'AI 产品', ARTICLE: '技术文章' }
  return labels[type as keyof typeof labels] || type
}

const getTypeIcon = (type: string) => {
  const icons = { MODEL: '🧠', PRODUCT: '🚀', ARTICLE: '📄' }
  return icons[type as keyof typeof icons] || '📌'
}

const formatNumber = (num: number) => {
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num.toString()
}

const formatDate = (date: Date, part: 'day' | 'month') => {
  if (part === 'day') return date.getDate().toString().padStart(2, '0')
  const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
                'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
  return months[date.getMonth()]
}

onMounted(() => {
  carouselInterval = window.setInterval(nextSlide, 5000)
})

onUnmounted(() => {
  if (carouselInterval) clearInterval(carouselInterval)
})
</script>

<style scoped lang="scss">
@use '@/styles/variables.scss' as *;

.home-view {
  min-height: 100vh;
  background: $bg-primary;
  color: $text-primary;
  font-family: 'Inter', system-ui, sans-serif;
  overflow-x: hidden;
}

// 动态背景
.background-grid {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
  background-image:
    linear-gradient(rgba(26, 54, 93, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(26, 54, 93, 0.03) 1px, transparent 1px);
  background-size: 50px 50px;
}

// 导航栏
.main-nav {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  background: rgba($bg-primary, 0.95);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid $border-color;
  z-index: 1000;
}

.nav-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 32px;
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 700;
  font-size: 24px;

  &-icon {
    font-size: 28px;
    animation: pulse 2s infinite;
  }

  @keyframes pulse {
    0%, 100% { transform: scale(1); }
    50% { transform: scale(1.1); }
  }
}

.nav-links {
  display: flex;
  gap: 32px;
}

.nav-link {
  color: $text-secondary;
  text-decoration: none;
  font-weight: 500;
  font-size: 14px;
  transition: all 0.3s ease;
  position: relative;

  &:hover {
    color: $accent-primary;
  }

  &.active {
    color: $accent-primary;

    &::after {
      content: '';
      position: absolute;
      bottom: -24px;
      left: 50%;
      transform: translateX(-50%);
      width: 0;
      height: 2px;
      background: $accent-primary;
      transition: width 0.3s ease;
    }
  }

  &.highlight {
    color: $accent-secondary;
    font-weight: 600;
  }
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.search-box {
  position: relative;

  &-input {
    width: 240px;
    padding: 10px 40px 10px 16px;
    background: $bg-secondary;
    border: 1px solid $border-color;
    border-radius: 24px;
    color: $text-primary;
    font-size: 14px;
    transition: all 0.3s ease;

    &:focus {
      outline: none;
      border-color: $accent-primary;
      background: rgba($accent-primary, 0.1);
      width: 280px;
    }

    &::placeholder {
      color: $text-tertiary;
    }
  }

  &-btn {
    position: absolute;
    right: 12px;
    top: 50%;
    transform: translateY(-50%);
    background: none;
    border: none;
    color: $text-secondary;
    cursor: pointer;
    padding: 4px;
    transition: color 0.3s ease;

    &:hover {
      color: $accent-primary;
    }
  }
}

.user-menu {
  .avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: linear-gradient(135deg, $accent-primary, $accent-secondary);
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-weight: 600;
    cursor: pointer;
    transition: transform 0.3s ease;

    &:hover {
      transform: scale(1.1);
    }
  }
}

.auth-buttons {
  display: flex;
  gap: 12px;
}

.btn-login {
  padding: 8px 24px;
  color: $text-secondary;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;

  &:hover {
    color: $text-primary;
  }
}

.btn-register {
  padding: 8px 24px;
  background: linear-gradient(135deg, $accent-primary, $accent-secondary);
  color: white;
  border-radius: 24px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-(-2px));
    box-shadow: 0 8px 20px rgba($accent-primary, 0.3);
  }
}

// 英雄区域
.hero-section {
  position: relative;
  padding: 160px 32px 80px;
  overflow: hidden;
}

.hero-content {
  max-width: 1400px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.hero-badge {
  display: inline-block;
  padding: 8px 20px;
  background: rgba($accent-secondary, 0.15);
  border: 1px solid rgba($accent-secondary, 0.3);
  border-radius: 24px;
  font-size: 14px;
  margin-bottom: 24px;
}

.hero-title {
  font-size: 64px;
  font-weight: 800;
  line-height: 1.1;
  margin-bottom: 24px;

  .title-gradient {
    background: linear-gradient(135deg, $accent-primary, $accent-secondary);
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
    color: transparent;
  }

  .title-white {
    color: white;
  }
}

.hero-desc {
  font-size: 20px;
  color: $text-secondary;
  margin-bottom: 48px;
  max-width: 600px;
}

.hero-actions {
  display: flex;
  gap: 16px;
  margin-bottom: 64px;
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 14px 36px;
  background: linear-gradient(135deg, $accent-primary, $accent-secondary);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  text-decoration: none;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-(-4px));
    box-shadow: 0 12px 32px rgba($accent-primary, 0.4);
  }
}

.btn-secondary {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 14px 28px;
  background: rgba($accent-secondary, 0.1);
  color: $accent-secondary;
  border: 1px solid rgba($accent-secondary, 0.3);
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  text-decoration: none;
  transition: all 0.3s ease;

  &:hover {
    background: rgba($accent-secondary, 0.2);
    transform: translateY(-(-4px));
  }

  .badge-live {
    padding: 4px 12px;
    background: #ef4444;
    border-radius: 12px;
    font-size: 12px;
    animation: blink 2s infinite;
  }

  @keyframes blink {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.5; }
  }
}

.hero-stats {
  display: flex;
  align-items: center;
  gap: 32px;

  .stat-item {
    .stat-value {
      font-size: 36px;
      font-weight: 700;
      color: white;
      line-height: 1;
    }

    .stat-label {
      font-size: 14px;
      color: $text-secondary;
      margin-top: 8px;
    }
  }

  .stat-divider {
    width: 1px;
    height: 48px;
    background: $border-color;
  }
}

// 浮动光球
.hero-visual {
  position: absolute;
  top: 50%;
  right: 10%;
  width: 400px;
  height: 400px;
  z-index: 0;
}

.floating-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(40px);
  animation: float 20s infinite ease-in-out;

  &.orb-1 {
    width: 200px;
    height: 200px;
    background: rgba($accent-primary, 0.3);
    top: 10%;
    left: 10%;
    animation-delay: 0s;
  }

  &.orb-2 {
    width: 150px;
    height: 150px;
    background: rgba($accent-secondary, 0.2);
    top: 50%;
    left: 60%;
    animation-delay: -5s;
  }

  &.orb-3 {
    width: 100px;
    height: 100px;
    background: rgba($accent-primary, 0.2);
    top: 70%;
    left: 20%;
    animation-delay: -10s;
  }

  &.orb-4 {
    width: 80px;
    height: 80px;
    background: rgba($accent-secondary, 0.3);
    top: 30%;
    left: 70%;
    animation-delay: -15s;
  }
}

@keyframes float {
  0%, 100% { transform: translate(0px, 0px); }
  25% { transform: translate(30px, -30px); }
  50% { transform: translate(-20px, 20px); }
  75% { transform: translate(20px, -10px); }
}

// 轮播区
.featured-section {
  padding: 80px 32px;
  position: relative;
  z-index: 1;
}

.section-header {
  max-width: 1400px;
  margin: 0 auto 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-title {
  font-size: 32px;
  font-weight: 700;
  color: white;
  display: flex;
  align-items: center;
  gap: 12px;

  .title-accent {
    font-size: 40px;
  }
}

.view-more {
  color: $text-secondary;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s ease;

  &:hover {
    color: $accent-primary;
  }
}

.carousel-container {
  max-width: 1400px;
  margin: 0 auto;
  position: relative;
}

.carousel-track {
  display: flex;
  transition: transform 0.5s ease;
}

.carousel-slide {
  min-width: 100%;
  padding: 0 16px;
}

.featured-card {
  position: relative;
  min-height: 320px;
  border-radius: 24px;
  padding: 48px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  overflow: hidden;
  transition: all 0.5s ease;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    border-radius: 24px;
    padding: 1px;
    background: linear-gradient(135deg, $border-color, transparent);
    -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
    mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  }

  &:hover {
    transform: translateY(-(-8px));
    box-shadow: 0 20px 40px rgba($accent-primary, 0.2);
  }

  .card-gradient {
    position: absolute;
    inset: 0;
    background: linear-gradient(135deg,
      rgba($accent-primary, 0.1),
      rgba($accent-secondary, 0.05));
  }

  .card-content {
    position: relative;
    z-index: 1;
  }

  .card-badge {
    display: inline-block;
    padding: 6px 16px;
    background: rgba($accent-primary, 0.2);
    border: 1px solid rgba($accent-primary, 0.3);
    border-radius: 12px;
    font-size: 12px;
    margin-bottom: 16px;
  }

  .card-title {
    font-size: 28px;
    font-weight: 700;
    color: white;
    margin-bottom: 12px;
  }

  .card-desc {
    font-size: 16px;
    color: $text-secondary;
    margin-bottom: 24px;
  }

  .card-meta {
    display: flex;
    gap: 24px;
    font-size: 14px;
    color: $text-tertiary;
  }
}

.carousel-controls {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24px;
  margin-top: 32px;
}

.carousel-btn {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: $bg-secondary;
  border: 1px solid $border-color;
  color: $text-secondary;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background: $accent-primary;
    color: white;
    border-color: $accent-primary;
  }
}

.carousel-indicators {
  display: flex;
  gap: 8px;

  .indicator {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: $border-color;
    transition: all 0.3s ease;

    &.active {
      width: 24px;
      border-radius: 4px;
      background: $accent-primary;
    }
  }
}

// 热门内容区
.trending-section {
  padding: 80px 32px;
}

.content-grid {
  max-width: 1400px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.content-card {
  background: $bg-secondary;
  border: 1px solid $border-color;
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.4s ease;

  &:hover {
    transform: translateY(-(-8px));
    border-color: $accent-primary;
    box-shadow: 0 12px 32px rgba($accent-primary, 0.15);
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
      top: 16px;
      right: 16px;
      padding: 6px 14px;
      background: rgba($accent-primary, 0.9);
      color: white;
      border-radius: 8px;
      font-size: 12px;
      font-weight: 500;
    }
  }

  .card-body {
    padding: 20px;

    .card-title {
      font-size: 18px;
      font-weight: 600;
      color: white;
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
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .card-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .rating-stars {
        display: flex;
        gap: 2px;
        font-size: 14px;
        color: $text-tertiary;

        .star.filled {
          color: #fbbf24;
        }
      }

      .card-likes {
        color: $text-secondary;
        font-size: 14px;
      }
    }
  }
}

.section-actions {
  max-width: 1400px;
  margin: 48px auto 0;
  text-align: center;
}

.btn-load-more {
  padding: 14px 48px;
  background: transparent;
  color: $accent-primary;
  border: 1px solid $accent-primary;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background: rgba($accent-primary, 0.1);
    transform: translateY(-(-2px));
  }
}

// 最新内容区
.latest-section {
  padding: 80px 32px;
}

.latest-list {
  max-width: 1400px;
  margin: 0 auto;
}

.latest-item {
  display: flex;
  gap: 24px;
  padding: 24px;
  background: $bg-secondary;
  border: 1px solid $border-color;
  border-radius: 16px;
  margin-bottom: 16px;
  text-decoration: none;
  transition: all 0.3s ease;

  &:hover {
    border-color: $accent-primary;
    background: rgba($accent-primary, 0.05);
    transform: translateX(8px);
  }

  .item-date {
    flex-shrink: 0;
    width: 80px;
    text-align: center;
    padding: 16px 12px;
    background: rgba($accent-primary, 0.1);
    border-radius: 12px;

    .day {
      display: block;
      font-size: 28px;
      font-weight: 700;
      color: $accent-primary;
      line-height: 1;
    }

    .month {
      display: block;
      font-size: 12px;
      color: $text-secondary;
      margin-top: 4px;
    }
  }

  .item-content {
    flex: 1;

    .item-type {
      font-size: 24px;
      margin-bottom: 8px;
    }

    .item-title {
      font-size: 20px;
      font-weight: 600;
      color: white;
      margin-bottom: 8px;
    }

    .item-desc {
      font-size: 14px;
      color: $text-secondary;
      margin-bottom: 12px;
    }

    .item-tags {
      display: flex;
      gap: 8px;

      .tag {
        padding: 4px 12px;
        background: rgba($accent-secondary, 0.1);
        color: $accent-secondary;
        border-radius: 12px;
        font-size: 12px;
      }
    }
  }
}

// 智能体协作入口
.agent-section {
  padding: 80px 32px;
}

.agent-container {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  gap: 48px;
  align-items: center;
}

.agent-visual {
  flex: 1;
  height: 400px;
  position: relative;
  overflow: hidden;
  border-radius: 24px;
  background: linear-gradient(135deg,
    rgba($accent-primary, 0.1),
    rgba($accent-secondary, 0.05));
}

.wave-animation {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.wave {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 200%;
  height: 60%;
  background: linear-gradient(90deg,
    rgba($accent-primary, 0.3),
    rgba($accent-secondary, 0.2),
    rgba($accent-primary, 0.3));
  border-radius: 50% 50% 0 0;
  animation: wave 3s infinite linear;

  &.wave-1 {
    animation-delay: 0s;
    opacity: 1;
  }

  &.wave-2 {
    animation-delay: -1s;
    opacity: 0.5;
    animation-duration: 4s;
  }

  &.wave-3 {
    animation-delay: -2s;
    opacity: 0.3;
    animation-duration: 5s;
  }
}

@keyframes wave {
  0% { transform: translateX(0) scaleY(1); }
  50% { transform: translateX(-50%) scaleY(1.1); }
  100% { transform: translateX(-100%) scaleY(1); }
}

.agent-icons {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  gap: 32px;

  .agent-icon {
    font-size: 48px;
    animation: bounce 2s infinite ease;

    &.icon-2 { animation-delay: -0.5s; }
    &.icon-3 { animation-delay: -1s; }
  }
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

.agent-info {
  flex: 1;

  .agent-title {
    font-size: 40px;
    font-weight: 800;
    color: white;
    margin-bottom: 16px;
    display: flex;
    align-items: center;
    gap: 12px;

    .gradient-text {
      background: linear-gradient(135deg, $accent-primary, $accent-secondary);
      -webkit-background-clip: text;
      background-clip: text;
      -webkit-text-fill-color: transparent;
      color: transparent;
    }

    .live-dot {
      width: 12px;
      height: 12px;
      border-radius: 50%;
      background: #22c55e;
      animation: livePulse 2s infinite;
    }
  }

  @keyframes livePulse {
    0%, 100% { opacity: 1; transform: scale(1); }
    50% { opacity: 0.5; transform: scale(1.2); }
  }

  .agent-desc {
    font-size: 18px;
    color: $text-secondary;
    margin-bottom: 32px;
    line-height: 1.6;
  }

  .agent-features {
    display: flex;
    gap: 16px;
    margin-bottom: 32px;

    .feature {
      padding: 12px 20px;
      background: rgba($accent-primary, 0.1);
      border: 1px solid rgba($accent-primary, 0.2);
      border-radius: 12px;
      font-size: 14px;
      color: $accent-primary;
    }
  }
}

.btn-explore-agents {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 16px 40px;
  background: linear-gradient(135deg, $accent-primary, $accent-secondary);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  text-decoration: none;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-(-4px));
    box-shadow: 0 12px 32px rgba($accent-primary, 0.4);
  }
}

// 分类快捷入口
.categories-section {
  padding: 80px 32px;
}

.categories-grid {
  max-width: 1400px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
}

.category-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 32px 24px;
  background: $bg-secondary;
  border: 1px solid $border-color;
  border-radius: 16px;
  text-decoration: none;
  transition: all 0.3s ease;

  &:hover {
    background: rgba($accent-primary, 0.1);
    border-color: $accent-primary;
    transform: translateY(-(-8px));
  }

  .category-icon {
    font-size: 32px;
  }

  .category-name {
    font-size: 16px;
    font-weight: 600;
    color: white;
  }

  .category-count {
    font-size: 12px;
    color: $text-tertiary;
  }
}

// 页脚
.main-footer {
  background: $bg-secondary;
  border-top: 1px solid $border-color;
  padding: 64px 32px 32px;
  margin-top: 80px;
}

.footer-container {
  max-width: 1400px;
  margin: 0 auto;
}

.footer-section {
  .footer-logo {
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
      color: white;
    }
  }

  .footer-desc {
    color: $text-secondary;
    font-size: 14px;
  }
}

.footer-links {
  display: flex;
  gap: 64px;
  margin-top: 32px;

  .link-group {
    h4 {
      font-size: 14px;
      font-weight: 600;
      color: white;
      margin-bottom: 16px;
    }

    a {
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
}

.footer-copyright {
  margin-top: 64px;
  padding-top: 32px;
  border-top: 1px solid $border-color;
  text-align: center;

  p {
    color: $text-tertiary;
    font-size: 14px;
  }
}

// 响应式
@media (max-width: 1200px) {
  .content-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .categories-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .agent-container {
    flex-direction: column;
    text-align: center;
  }
}

@media (max-width: 768px) {
  .nav-links {
    display: none;
  }

  .search-box input {
    width: 160px;

    &:focus {
      width: 180px;
    }
  }

  .hero-title {
    font-size: 40px;
  }

  .content-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .categories-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .latest-item {
    flex-direction: column;
  }

  .footer-links {
    flex-direction: column;
    gap: 32px;
  }
}
</style>
