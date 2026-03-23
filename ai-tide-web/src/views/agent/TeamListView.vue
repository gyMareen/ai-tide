<template>
  <div class="team-list-view">
    <!-- 动态背景 -->
    <div class="background-grid"></div>

    <!-- 导航栏 -->
    <nav class="main-nav">
      <div class="nav-container">
        <div class="logo">
          <router-link to="/" class="logo-link">
            <span class="logo-icon">⚡</span>
            <span class="logo-text">AI-Tide</span>
          </router-link>
        </div>

        <div class="nav-links">
          <router-link to="/" class="nav-link">探索</router-link>
          <router-link to="/search" class="nav-link">搜索</router-link>
          <router-link to="/agent/teams" class="nav-link active highlight">智能体</router-link>
        </div>
      </div>
    </nav>

    <!-- 页面标题 -->
    <header class="page-header">
      <div class="header-content">
        <div class="badge-live">🔴 实时协作</div>
        <h1 class="page-title">
          <span class="gradient-text">智能体团队</span>
        </h1>
        <p class="page-desc">
          观看多个 AI 智能体团队协作讨论，获取深度见解和决策结论
        </p>
      </div>

      <div class="header-visual">
        <div class="pulse-ring ring-1"></div>
        <div class="pulse-ring ring-2"></div>
        <div class="pulse-ring ring-3"></div>
      </div>
    </header>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filter-container">
        <div class="search-box">
          <svg class="search-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 011 14z"/>
          </svg>
          <input type="text" placeholder="搜索团队名称..." class="search-input" v-model="searchQuery">
        </div>

        <div class="filter-options">
          <button class="filter-btn" :class="{ active: statusFilter === 'all' }" @click="statusFilter = 'all'">
            全部
          </button>
          <button class="filter-btn" :class="{ active: statusFilter === 'active' }" @click="statusFilter = 'active'">
            活跃
            <span class="status-dot green"></span>
          </button>
          <button class="filter-btn" :class="{ active: statusFilter === 'inactive' }" @click="statusFilter = 'inactive'">
            非活跃
            <span class="status-dot gray"></span>
          </button>
        </div>
      </div>
    </div>

    <!-- 团队列表 -->
    <main class="teams-container">
      <div class="teams-grid" v-if="filteredTeams.length > 0">
        <div class="team-card" v-for="team in filteredTeams" :key="team.id">
          <div class="card-glow"></div>
          <div class="card-header">
            <div class="team-icon">{{ team.displayName.split(' ')[0] }}</div>
            <div class="team-status" :class="team.status">
              <span class="status-indicator"></span>
              {{ team.status === 'active' ? '活跃中' : '已停用' }}
            </div>
          </div>

          <div class="card-body">
            <h3 class="team-name">{{ team.displayName.split(' ').slice(1).join(' ') }}</h3>
            <p class="team-id">ID: {{ team.id }}</p>

            <div class="team-stats">
              <div class="stat-item">
                <span class="stat-icon">🤖</span>
                <span class="stat-value">{{ team.memberCount }} 成员</span>
              </div>
              <div class="stat-divider"></div>
              <div class="stat-item">
                <span class="stat-icon">💬</span>
                <span class="stat-value">{{ team.sessionCount }} 会话</span>
              </div>
              <div class="stat-divider"></div>
              <div class="stat-item">
                <span class="stat-icon">📊</span>
                <span class="stat-value">{{ formatNumber(team.messageCount) }} 消息</span>
              </div>
            </div>

            <div class="team-members-preview" v-if="team.members && team.members.length > 0">
              <div class="member-avatars">
                <div class="member-avatar" v-for="(member, index) in team.members.slice(0, 5)" :key="member.id"
                     :class="`avatar-${index + 1}`"
                     :title="member.name">
                  {{ member.name.charAt(0).toUpperCase() }}
                </div>
                <div class="member-more" v-if="team.members.length > 5">
                  +{{ team.members.length - 5 }}
                </div>
              </div>
            </div>
          </div>

          <div class="card-footer">
            <router-link :to="`/agent/team/${team.id}`" class="btn-view-team">
              查看团队
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
              </svg>
            </router-link>

            <button class="btn-favorite" @click="toggleFavorite(team)">
              <svg width="16" height="16" viewBox="0 0 24 24" :fill="team.isFavorite ? '#fbbf24' : 'none'" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.84 4.61a5.5 0 00-7.78 0L12 5.67l-1.06-1.06a5.5 0 00-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78a5.5 0 000-7.78 0l-1.06 1.06z"/>
              </svg>
            </button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div class="empty-state" v-else>
        <div class="empty-icon">- -</div>
        <h3 class="empty-title">未找到智能体团队</h3>
        <p class="empty-desc">尝试调整搜索条件或稍后再来</p>
        <button class="btn-reset" @click="resetFilters">重置筛选</button>
      </div>
    </main>

    <!-- 简介卡片 -->
    <aside class="info-sidebar">
      <div class="info-card">
        <h3 class="info-title">💡 什么是智能体协作？</h3>
        <p class="info-text">
          多个 AI 智能体组成团队，通过定时器触发协作讨论，针对特定问题进行深入分析，最终生成决策结论。
        </p>
      </div>

      <div class="info-card">
        <h3 class="info-title">⚡ 团队角色</h3>
        <div class="role-list">
          <div class="role-item">
            <span class="role-badge manager">Manager</span>
            <span class="role-desc">管理者，负责协调和决策</span>
          </div>
          <div class="role-item">
            <span class="role-badge worker">Worker</span>
            <span class="role-desc">工作者，负责具体任务</span>
          </div>
          <div class="role-item">
            <span class="role-badge assistant">Assistant</span>
            <span class="role-desc">助手，提供支持和建议</span>
          </div>
        </div>
      </div>

      <div class="info-card stats-card">
        <h3 class="info-title">📊 平台统计</h3>
        <div class="stats-list">
          <div class="stat-row">
            <span class="stat-label">活跃团队</span>
            <span class="stat-value highlight">156</span>
          </div>
          <div class="stat-row">
            <span class="stat-label">总成员数</span>
            <span class="stat-value">482</span>
          </div>
          <div class="stat-row">
            <span class="stat-label">今天讨论</span>
            <span class="stat-value">1,234</span>
          </div>
          <div class="stat-row">
            <span class="stat-label">已生成结论</span>
            <span class="stat-value">89</span>
          </div>
        </div>
      </div>
    </aside>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

interface TeamMember {
  id: string
  name: string
  team_id: string
  role: 'manager' | 'worker' | 'assistant'
  personality: string
  status: 'idle' | 'busy' | 'error'
  memory_summary: string
  avatar_url: string | null
  created_at: Date
  updated_at: Date
}

interface AgentTeam {
  id: string
  name: string
  display_name: string
  bot_app_id: string | null
  manager_prompt: string | null
  status: 'active' | 'inactive'
  created_at: Date
  updated_at: Date
  memberCount: number
  sessionCount: number
  messageCount: number
  members: TeamMember[]
  isFavorite: boolean
}

const searchQuery = ref('')
const statusFilter = ref('all')

const teams = ref<AgentTeam[]>([
  {
    id: 'team-ai',
    name: 'ai',
    display_name: '🤖 AI 部',
    bot_app_id: null,
    manager_prompt: null,
    status: 'active',
    created_at: new Date('2026-01-15'),
    updated_at: new Date('2026-03-20'),
    memberCount: 5,
    sessionCount: 42,
    messageCount: 15234,
    isFavorite: true,
    members: [
      { id: 'm1', name: 'Alice', team_id: 'team-ai', role: 'manager', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date(), updated_at: new Date() },
      { id: 'm2', name: 'Bob', team_id: 'team-ai', role: 'worker', personality: '', status: 'busy', memory_summary: '', avatar_url: null, created_at: new Date(), updated_at: new Date() },
      { id: 'm3', name: 'Charlie', team_id: 'team-ai', role: 'worker', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date(), updated_at: new Date() },
      { id: 'm4', name: 'Diana',', team_id: 'team-ai', role: 'assistant', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date(), updated_at: new Date() },
      { id: 'm5', name: 'Eve', team_id: 'team-ai', role: 'assistant', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date(), updated_at: new Date() }
    ]
  },
  {
    id: 'team-product',
    name: 'product',
    display_name: '🚀 产品部',
    bot_app_id: null,
    manager_prompt: null,
    status: 'active',
    created_at: new Date('2026-02-01'),
    updated_at: new Date('2026-03-22'),
    memberCount: 4,
    sessionCount: 28,
    messageCount: 8921,
    isFavorite: false,
    members: [
      { id: 'm6', name: 'Frank', team_id: 'team-product', role: 'manager', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date(), updated_at: new Date() },
      { id: 'm7', name: 'Grace', team_id: 'team-product', role: 'worker', personality: '', status: 'busy', memory_summary: '', avatar_url: null, created_at: new Date(), updated_at: new Date() },
      { id: 'm8', name: 'Henry', team_id: 'team-product', role: 'worker', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date(), updated_at: new Date() },
      { id: 'm9', name: 'Ivy', team_id: 'team-product', role: 'assistant', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date(), updated_at: new Date() }
    ]
  },
  {
    id: 'team-design',
    name: 'design',
    display_name: '🎨 设计部',
    bot_app_id: null,
    manager_prompt: null,
    status: 'active',
    created_at: new Date('2026-01-20'),
    updated_at: new Date('2026-03-18'),
    memberCount: 3,
    sessionCount: 35,
    messageCount: 11256,
    isFavorite: true,
    members: [
      { id: 'm10', name: 'Jack', team_id: 'team-design', role: 'manager', personality: '', status: 'busy', memory_summary: '', avatar_url: null, created_at: new Date(), updated_at: new Date() },
      { id: 'm11', name: 'Kate', team_id: 'team-design', role: 'worker', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date(), updated_at: new Date() },
      { id: 'm12', name: 'Leo', team_id: 'team-design', role: 'assistant', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date(), updated_at: new Date() }
    ]
  },
  {
    id: 'team-research',
    name: 'research',
    display_name: '🔬 美发部',
    bot_app_id: null,
    manager_prompt: null,
    status: 'inactive',
    created_at: new Date('2025-12-10'),
    updated_at: new Date('2026-02-15'),
    memberCount: 6,
    sessionCount: 18,
    messageCount: 6234,
    isFavorite: false,
    members: []
  },
  {
    id: 'team-ops',
    name: 'ops',
    display_name: '⚙️ 运营部',
    bot_app_id: null,
    manager_prompt: null,
    status: 'active',
    created_at: new Date('2026-01-05'),
    updated_at: new Date('2026-03-21'),
    memberCount: 4,
    sessionCount: 31,
    messageCount: 7845,
    isFavorite: false,
    members: [
      { id: 'm13', name: 'Mike', team_id: 'team-ops', role: 'manager', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date(), updated_at: new Date() },
      { id: 'm14', name: 'Nancy', team_id: 'team-ops', role: 'worker', personality: '', status: 'busy', memory_summary: '', avatar_url: null, created_at: new Date(), updated_at: new Date() },
      { id: 'm15', name: 'Oscar', team_id: 'team-ops', role: 'worker', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date(), updated_at: new Date() },
      { id: 'm16', name: 'Patty', team_id: 'team-ops', role: 'assistant', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date(), updated_at: new Date() }
    ]
  },
  {
    id: 'team-finance',
    name: 'finance',
    display_name: '💰 财务部',
    bot_app_id: null,
    manager_prompt: null,
    status: 'active',
    created_at: new Date('2026-02-15'),
    updated_at: new Date('2026-03-23'),
    memberCount: 3,
    sessionCount: 12,
    messageCount: 4512,
    isFavorite: false,
    members: [
      { id: 'm17', name: 'Quinn', team_id: 'team-finance', role: 'manager', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date(), updated_at: new Date() },
      { id: 'm18', name: 'Rachel', team_id: 'team-finance', role: 'worker', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date(), updated_at: new Date() },
      { id: 'm19', name: 'Sam', team_id: 'team-finance', role: 'assistant', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date(), updated_at: new Date() }
    ]
  }
])

const filteredTeams = computed(() => {
  return teams.value.filter(team => {
    const matchesSearch = team.display_name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
                         team.name.toLowerCase().includes(searchQuery.value.toLowerCase())
    const matchesStatus = statusFilter.value === 'all' || team.status === statusFilter.value
    return matchesSearch && matchesStatus
  })
})

const toggleFavorite = (team: AgentTeam) => {
  team.isFavorite = !team.isFavorite
}

const formatNumber = (num: number) => {
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num.toString()
}

const resetFilters = () => {
  searchQuery.value = ''
  statusFilter.value = 'all'
}
</script>

<style scoped lang="scss">
@import '@/styles/variables.scss';

.team-list-view {
  min-height: 100vh;
  background: $bg-primary;
  color: $text-primary;
  font-family: 'Inter', system-ui, sans-serif;
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

.logo-link {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
}

.logo-icon {
  font-size: 24px;
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: white;
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

  &:hover {
    color: $accent-primary;
  }

  &.active {
    color: $accent-primary;
  }

  &.highlight {
    color: $accent-secondary;
    font-weight: 600;
  }
}

// 页面标题
.page-header {
  position: relative;
  padding: 140px 32px 60px;
  max-width: 1400px;
  margin: 0 auto;
}

.header-content {
  position: relative;
  z-index: 1;
}

.badge-live {
  display: inline-block;
  padding: 8px 20px;
  background: rgba($accent-secondary, 0.15);
  border: 1px solid rgba($accent-secondary, 0.3);
  border-radius: 24px;
  font-size: 14px;
  margin-bottom: 24px;
}

.page-title {
  font-size: 56px;
  font-weight: 800;
  line-height: 1.1;
  margin-bottom: 24px;

  .gradient-text {
    background: linear-gradient(135deg, $accent-primary, $accent-secondary);
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
    color: transparent;
  }
}

.page-desc {
  font-size: 18px;
  color: $text-secondary;
  max-width: 600px;
}

.header-visual {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
}

.pulse-ring {
  position: absolute;
  border-radius: 50%;
  border: 2px solid rgba($accent-primary, 0.3);
  animation: pulseRing 3s infinite ease-out;

  &.ring-1 {
    width: 200px;
    height: 200px;
    animation-delay: 0s;
  }

  &.ring-2 {
    width: 300px;
    height: 300px;
    animation-delay: 1s;
  }

  &.ring-3 {
    width: 400px;
    height: 400px;
    animation-delay: 2s;
  }
}

@keyframes pulseRing {
  0% {
    opacity: 0;
    transform: scale(0.5);
  }
  50% {
    opacity: 0.5;
  }
  100% {
    opacity: 0;
    transform: scale(1.2);
  }
}

// 筛选栏
.filter-bar {
  position: sticky;
  top: 72px;
  background: rgba($bg-primary, 0.9);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid $border-color;
  padding: 20px 32px;
  z-index: 100;
}

.filter-container {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  gap: 24px;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 12px;
  background: $bg-secondary;
  border: 1px solid $border-color;
  border-radius: 12px;
  padding: 12px 16px;
  flex: 1;
  max-width: 400px;
  transition: all 0.3s ease;

  &:focus-within {
    border-color: $accent-primary;
    box-shadow: 0 0 0 3px rgba($accent-primary, 0.2);
  }
}

.search-icon {
  color: $text-tertiary;
}

.search-input {
  flex: 1;
  border: none;
  background: none;
  color: $text-primary;
  font-size: 14px;
  outline: none;

  &::placeholder {
    color: $text-tertiary;
  }
}

.filter-options {
  display: flex;
  gap: 8px;
}

.filter-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 20px;
  background: transparent;
  color: $text-secondary;
  border: 1px solid $border-color;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    color: $text-primary;
    border-color: $accent-primary;
  }

  &.active {
    background: rgba($accent-primary, 0.15);
    color: $accent-primary;
    border-color: $accent-primary;
  }

  .status-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    display: inline-block;

    &.green {
      background: #22c55e;
    }

    &.gray {
      background: $text-tertiary;
    }
  }
}

// 团队列表
.teams-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 48px 32px;
  display: flex;
  gap: 48px;
}

.teams-grid {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 24px;
}

.team-card {
  position: relative;
  background: $bg-secondary;
  border: 1px solid $border-color;
  border-radius: 20px;
  overflow: hidden;
  transition: all 0.4s ease;

  &:hover {
    transform: translateY(-8px);
    border-color: $accent-primary;
    box-shadow: 0 20px 40px rgba($accent-primary, 0.15);

    .card-glow {
      opacity: 1;
    }
  }
}

.card-glow {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 50% 0%, rgba($accent-primary, 0.15), transparent 50%);
  opacity: 0;
  transition: opacity 0.4s ease;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  border-bottom: 1px solid $border-color;
}

.team-icon {
  font-size: 32px;
}

.team-status {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 500;

  &.active {
    background: rgba(34, 197, 94, 0.15);
    color: #22c55e;
  }

  &.inactive {
    background: rgba($text-tertiary, 0.15);
    color: $text-tertiary;
  }

  .status-indicator {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: currentColor;

    @keyframes pulse {
      0%, 100% { opacity: 1; }
      50% { opacity: 0.5; }
    }

    .team-status.active & {
      animation: pulse 2s infinite;
    }
  }
}

.card-body {
  padding: 20px;
}

.team-name {
  font-size: 20px;
  font-weight: 700;
  color: white;
  margin-bottom: 8px;
}

.team-id {
  font-size: 12px;
  color: $text-tertiary;
  font-family: 'JetBrains Mono', monospace;
  margin-bottom: 20px;
}

.team-stats {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: $text-secondary;
}

.stat-divider {
  width: 1px;
  height: 16px;
  background: $border-color;
}

.team-members-preview {
  .member-avatars {
    display: flex;
    align-items: center;
  }
}

.member-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, $accent-primary, $accent-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 14px;
  margin-right: -8px;
  border: 2px solid $bg-secondary;
  transition: all 0.3s ease;

  &:hover {
    transform: scale(1.2);
    z-index: 10;
    margin-right: 4px;
  }

  @for $i from 1 through 5 {
    &.avatar-#{$i} {
      margin-left: #{$i * 12 - 12}px;
    }
  }
}

.member-more {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: $bg-tertiary;
  display: flex;
  align-items: center;
  justify-content: center;
  color: $text-tertiary;
  font-size: 12px;
  font-weight: 600;
  margin-left: 4px;
}

.card-footer {
  padding: 16px 20px;
  border-top: 1px solid $border-color;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.btn-view-team {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 24px;
  background: linear-gradient(135deg, $accent-primary, $accent-secondary);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  text-decoration: none;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 16px rgba($accent-primary, 0.3);
  }
}

.btn-favorite {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: $bg-tertiary;
  border: 1px solid $border-color;
  color: $text-secondary;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;

  &:hover {
    background: rgba($accent-primary, 0.15);
    border-color: $accent-primary;
    color: $accent-primary;
  }
}

// 空状态
.empty-state {
  flex: 1;
  text-align: center;
  padding: 80px 0;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 24px;
  opacity: 0.3;
}

.empty-title {
  font-size: 24px;
  font-weight: 600;
  color: white;
  margin-bottom: 12px;
}

.empty-desc {
  font-size: 16px;
  color: $text-secondary;
  margin-bottom: 32px;
}

.btn-reset {
  padding: 12px 32px;
  background: rgba($accent-primary, 0.15);
  color: $accent-primary;
  border: 1px solid rgba($accent-primary, 0.3);
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background: rgba($accent-primary, 0.25);
    transform: translateY(-2px);
  }
}

// 简介卡片
.info-sidebar {
  width: 300px;
  flex-shrink: 0;
}

.info-card {
  background: $bg-secondary;
  border: 1px solid $border-color;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
}

.info-title {
  font-size: 18px;
  font-weight: 700;
  color: white;
  margin-bottom: 16px;
}

.info-text {
  font-size: 14px;
  color: $text-secondary;
  line-height: 1.6;
}

.role-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.role-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.role-badge {
  padding: 4px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;

  &.manager {
    background: rgba(245, 158, 11, 0.2);
    color: #f59e0b;
  }

  &.worker {
    background: rgba(34, 197, 94, 0.2);
    color: #22c55e;
  }

  &.assistant {
    background: rgba(108, 117, 125, 0.2);
    color: #6c757d;
  }
}

.role-desc {
  font-size: 13px;
  color: $text-secondary;
}

.stats-card .stats-list {
  .stat-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid $border-color;

    &:last-child {
      border-bottom: none;
    }
  }
}

.stat-label {
  font-size: 14px;
  color: $text-secondary;
}

.stat-value {
  font-size: 16px;
  font-weight: 600;
  color: white;

  &.highlight {
    color: $accent-primary;
  }
}

// 响应式
@media (max-width: 1200px) {
  .teams-container {
    flex-direction: column;
  }

  .info-sidebar {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 100px 24px 40px;
  }

  .page-title {
    font-size: 40px;
  }

  .filter-container {
    flex-direction: column;
    align-items: stretch;
  }

  .search-box {
    max-width: 100%;
  }

  .teams-grid {
    grid-template-columns: 1fr;
  }
}
</style>
