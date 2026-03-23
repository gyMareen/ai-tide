<template>
  <div class="session-detail-view">
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
          <router-link to="/agent/teams" class="nav-link">智能体</router-link>
        </div>
      </div>
    </nav>

    <!-- 面包屑 -->
    <div class="breadcrumb">
      <div class="breadcrumb-container">
        <router-link to="/agent/teams" class="breadcrumb-item">智能体团队</router-link>
        <span class="breadcrumb-separator">/</span>
        <router-link :to="`/agent/team/${session?.team_id}`" class="breadcrumb-item">
          {{ team?.display_name || team?.name }}
        </router-link>
        <span class="breadcrumb-separator">/</span>
        <span class="breadcrumb-current">{{ session?.title }}</span>
      </div>
    </div>

    <!-- 主内容区 -->
    <main class="session-main">
      <!-- 左侧：智能体状态 -->
      <aside class="agents-sidebar">
        <div class="sidebar-header">
          <h2 class="sidebar-title">
            <span class="gradient-text">参与智能体</span>
          </h2>
          <div class="session-status" :class="session?.status">
            <span class="status-dot"></span>
            {{ getStatusLabel(session?.status) }}
          </div>
        </div>

        <div class="agents-list">
          <div class="agent-item" v-for="agent in session?.agents" :key="agent.id"
               :class="{
                 'active': currentSpeakingAgent === agent.id,
                 'busy': agent.status === 'busy'
               }">
            <div class="agent-avatar">
              <span class="avatar-emoji">{{ agent.name.charAt(0).toUpperCase() }}</span>
              <div class="avatar-status" :class="agent.status"></div>
            </div>
            <div class="agent-info">
              <div class="agent-name">{{ agent.name }}</div>
              <div class="agent-role">{{ getRoleLabel(agent.role) }}</div>
              <div class="agent-memory" v-if="agent.memory_summary">
                <span class="memory-icon">💭</span>
                <span class="memory-text">{{ truncateText(agent.memory_summary, 50) }}</span>
              </div>
            </div>
            <div class="agent-activity">
              <div class="activity-indicator" v-if="agent.status === 'busy'">
                <div class="wave"></div>
                <div class="wave"></div>
                <div class="wave"></div>
              </div>
              <span class="activity-status">{{ agent.status === 'busy' ? '发言中...' : '空闲' }}</span>
            </div>
          </div>
        </div>

        <!-- 会话信息 -->
        <div class="session-info-card">
          <h3 class="info-title">会话信息</h3>
          <div class="info-list">
            <div class="info-item">
              <span class="info-label">开始时间</span>
              <span class="info-value">{{ formatDateTime(session?.started_at) }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">消息数量</span>
              <span class="info-value">{{ session?.message_count || 0 }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">讨论主题</span>
              <span class="info-value">{{ session?.topic }}</span>
            </div>
          </div>
        </div>
      </aside>

      <!-- 中间：消息区域 -->
      <div class="messages-area">
        <div class="messages-container">
          <div class="system-message">
            <div class="system-badge">📢</div>
            <span>协作会话已启动，智能体开始讨论...</span>
          </div>

          <!-- 消息列表 -->
          <div class="message-list" ref="messagesContainer">
            <div class="message-item" v-for="(msg, index) in messages" :key="msg.id"
                 :class="`message-type-${msg.message_type}`"
                 :style="{ animationDelay: `${index * 0.1}s` }">
              <div class="message-header">
                <div class="message-avatar" v-if="msg.agent">
                  <span class="avatar-emoji">{{ msg.agent.name.charAt(0).toUpperCase() }}</span>
                </div>
                <div class="message-meta">
                  <span class="message-sender">{{ msg.agent?.name || '系统' }}</span>
                  <span class="message-time">{{ formatTime(msg.created_at) }}</span>
                </div>
              </div>

              <div class="message-content">
                <div class="message-role-tag" v-if="msg.agent && msg.agent.role">
                  {{ getRoleLabel(msg.agent.role) }}
                </div>
                <div class="message-text" v-html="formatMessage(msg.content)"></div>
              </div>
            </div>
          </div>

          <!-- 滚动到底部 -->
          <div class="scroll-to-bottom" v-if="!isAutoScroll" @click="scrollToBottom">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 14l-7 7m0 0l-7-7m14 14l-7-7m0 0l14 14"/>
            </svg>
            <span>新消息</span>
          </div>
        </div>

        <!-- 输入区域（仅管理员可发言） -->
        <div class="input-area" v-if="canIntervene">
          <div class="input-container">
            <textarea class="message-input"
                      v-model="newMessage"
                      placeholder="输入消息参与讨论..."
                      @keydown.ctrl.enter="sendMessage"
                      rows="1"></textarea>
            <div class="input-actions">
              <button class="btn-send" @click="sendMessage" :disabled="!newMessage.trim()">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M22 2L11 13M22 2l-7 20-2-20-7 20M11 13l9 9"/>
                </svg>
                <span>发送</span>
              </button>
            </div>
          </div>
          <div class="input-hint">Ctrl + Enter 发送</div>
        </div>
      </div>

      <!-- 右侧：结论区域 -->
      <aside class="conclusion-sidebar">
        <div class="conclusion-card" v-if="session?.conclusion">
          <div class="conclusion-header">
            <h2 class="conclusion-title">
              <span class="gradient-text">讨论结论</span>
              <span class="conclusion-badge">✓</span>
            </h2>
            <div class="conclusion-time">
              生成于 {{ formatDateTime(session?.completed_at) }}
            </div>
          </div>

          <div class="conclusion-content">
            <div class="conclusion-summary">
              <h4 class="summary-title">摘要</h4>
              <p class="summary-text">{{ session.conclusion }}</p>
            </div>

            <div class="conclusion-actions">
              <button class="btn-copy" @click="copyConclusion">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h2a2 2 0 012 2v8a2 2 0 01-2 2zM16 16h-2a2 2 0 01-2-2V6a2 2 0 012-2h2a2 2 0 012 2v8a2 2 0 01-2 2zM15 9l-6-6"/>
                </svg>
                <span>复制结论</span>
              </button>
              <button class="btn-export" @click="exportConclusion">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4M7 7h10M5 21h14"/>
                </svg>
                <span>导出</span>
              </button>
            </div>
          </div>
        </div>

        <div class="pending-card" v-else>
          <div class="pending-icon">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <circle cx="12" cy="12" r="10" stroke-width="2"/>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6l4 2"/>
            </svg>
          </div>
          <h3 class="pending-title">讨论进行中</h3>
          <p class="pending-desc">智能体正在协作讨论，结论生成后将在此显示</p>
        </div>

        <!-- 相关会话 -->
        <div class="related-sessions">
          <h3 class="related-title">相关会话</h3>
          <router-link v-for="related in relatedSessions" :key="related.id"
                       :to="`/agent/session/${related.id}`"
                       class="related-item">
            <span class="related-status" :class="related.status"></span>
            <span class="related-title-text">{{ related.title }}</span>
            <span class="related-time">{{ formatDate(related.started_at) }}</span>
          </router-link>
        </div>
      </aside>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'

interface AgentMember {
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

interface DiscussionMessage {
  id: string
  session_id: string
  agent_id: string | null
  message_type: 'text' | 'system' | 'error'
  content: string
  created_at: Date
  agent?: AgentMember
}

interface CollaborationSession {
  id: string
  team_id: string
  title: string
  topic: string
  agents: AgentMember[]
  status: 'pending' | 'running' | 'paused' | 'completed' | 'failed'
  message_count: number
  conclusion: string | null
  started_at: Date | null
  completed_at: Date | null
  created_at: Date
  updated_at: Date
}

const route = useRoute()
const sessionId = route.params.id as string

const session = ref<CollaborationSession | null>(null)
const team = ref<any>(null)
const messages = ref<DiscussionMessage[]>([])
const currentSpeakingAgent = ref<string | null>(null)
const newMessage = ref('')
const isAutoScroll = ref(true)
const canIntervene = ref(false) // 根据权限设置
const messagesContainer = ref<HTMLElement | null>(null)

const relatedSessions = ref([
  { id: 's1', title: '技术方案评审', status: 'completed', started_at: new Date('2026-03-22') },
  { id: 's2', title: '性能优化讨论', status: 'running', started_at: new Date('2026-03-23') },
  { id: 's3', title: '需求分析会话', status: 'completed', started_at: new Date('2026-03-20') }
])

// 模拟数据
session.value = {
  id: sessionId,
  team_id: 'team-ai',
  title: 'GPT-5 Turbo 能力评估',
  topic: '评估 GPT-5 Turbo 在不同任务场景下的性能表现和适用性',
  agents: [
    { id: 'm1', name: 'Alice', team_id: 'team-ai', role: 'manager', personality: '', status: 'idle', memory_summary: '正在汇总各部门评估结果', avatar_url: null, created_at: new Date(), updated_at: new Date() },
    { id: 'm2', name: 'Bob', team_id: 'team-ai', role: 'worker', personality: '', status: 'idle', memory_summary: '分析编码任务性能', avatar_url: null, created_at: new Date(), updated_at: new Date() },
    { id: 'm3', name: 'Charlie', team_id: 'team-ai', role: 'worker', personality: '', status: 'idle', memory_summary: '分析推理任务性能', avatar_url: null, created_at: new Date(), updated_at: new Date() },
    { id: 'm4', name: 'Diana', team_id: 'team-ai', role: 'assistant', personality: '', status: 'idle', memory_summary: '提供性能测试数据', avatar_url: null, created_at: new Date(), updated_at: new Date() }
  ],
  status: 'completed',
  message_count: 15,
  conclusion: '经过全面评估，GPT-5 Turbo 在编码任务上表现优异，相比前代提升约 40%。在长上下文推理任务中也有显著改进，支持 128K 上下文。建议：1) 编码任务优先使用 GPT-5 Turbo；2) 长文本分析场景适用；3) 注意控制 API 调用成本。',
  started_at: new Date('2026-03-23T09:00:00'),
  completed_at: new Date('2026-03-23T09:35:00'),
  created_at: new Date(),
  updated_at: new Date()
}

team.value = { display_name: '🤖 AI 部', name: 'ai' }

messages.value = [
  {
    id: 'msg1',
    session_id: sessionId,
    agent_id: 'm1',
    message_type: 'system',
    content: '协作会话启动',
    created_at: new Date('2026-03-23T09:00:00')
  },
  {
    id: 'msg2',
    session_id: sessionId,
    agent_id: 'm2',
    message_type: 'text',
    content: '我负责分析 GPT-5 Turbo 在编码任务上的性能。从官方测试数据来看，在 HumanEval 基准上，GPT-5 Turbo 得分为 85.2%，相比 GPT-4 提升了 12.7%。特别是在代码生成和补全任务上表现突出。',
    created_at: new Date('2026-03-23T09:01:00'),
    agent: { id: 'm2', name: 'Bob', team_id: 'team-ai', role: 'worker', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date(), updated_at: new Date() }
  },
  {
    id: 'msg3',
    session_id: sessionId,
    agent_id: 'm3',
    message_type: 'text',
    content: '我关注长上下文推理性能。GPT-5 Turbo 支持 128K Token 的上下文窗口，这是 GPT-4 的 4 倍。在长文档摘要和复杂推理任务中，这个优势非常明显。但在短文本任务上，差异不大。',
    created_at: new Date('2026-03-23T09:02:00'),
    agent: { id: 'm3', name: 'Charlie', team_id: 'team-ai', role: 'worker', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date(), updated_at: new Date() }
  },
  {
    id: 'msg4',
    session_id: sessionId,
    agent_id: 'm4',
    message_type: 'text',
    content: '根据性能测试数据，我整理了以下关键指标：\n1. 编码任务：准确率 92.5%，速度提升 40%\n2. 推理任务：准确率 88.3%，长上下文优势明显\n3. 平均响应时间：3.2 秒（相比 GPT-4 慢 15%）\n4. API 调用成本：降低 50%（每百万 Token 10 美元）',
    created_at: new Date('2026-03-23T09:03:00'),
    agent: { id: 'm4', name: 'Diana', team_id: 'team-ai', role: 'assistant', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date(), updated_at: new Date() }
  },
  {
    id: 'msg5',
    session_id: sessionId,
    agent_id: 'm1',
    message_type: 'text',
    content: '感谢大家的分析。现在我需要综合这些信息，形成最终的评估结论。Bob 指出编码任务提升 40%，Charlie 强调长上下文优势，Diana 提供了具体的性能数据。总体来看，GPT-5 Turbo 是一个显著的进步。',
    created_at: new Date('2026-03-23T09:10:00'),
    agent: { id: 'm1', name: 'Alice', team_id: 'team-ai', role: 'manager', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date(), updated_at: new Date() }
  },
  {
    id: 'msg6',
    session_id: sessionId,
    agent_id: 'm2',
    message_type: 'text',
    content: '补充一点：在代码质量和可维护性方面，GPT-5 Turbo 生成的代码结构更清晰，注释更完善。这对团队协作项目非常重要。',
    created_at: new Date('2026-03-23T09:12:00'),
    agent: { id: 'm2', name: 'Bob', team_id: 'team-ai', role: 'worker', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date(), updated_at: new Date() }
  },
  {
    id: 'msg7',
    session_id: sessionId,
    agent_id: 'm1',
    message_type: 'system',
    content: '讨论结束，生成最终结论...',
    created_at: new Date('2026-03-23T09:34:00')
  },
  {
    id: 'msg8',
    session_id: sessionId,
    agent_id: null,
    message_type: 'system',
    content: '✅ 结论已生成',
    created_at: new Date('2026-03-23T09:35:00')
  }
]

const getStatusLabel = (status?: string) => {
  const labels = {
    pending: '待开始',
    running: '进行中',
    paused: '已暂停',
    completed: '已完成',
    failed: '失败'
  }
  return labels[status as keyof typeof labels] || status
}

const getRoleLabel = (role?: string) => {
  const labels = {
    manager: '管理者',
    worker: '工作者',
    assistant: '助手'
  }
  return labels[role as keyof typeof labels] || role
}

const formatDateTime = (date?: Date | string) => {
  if (!date) return '-'
  const d = typeof date === 'string' ? new Date(date) : date
  return d.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatTime = (date?: Date | string) => {
  if (!date) return '-'
  const d = typeof date === 'string' ? new Date(date) : date
  return d.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatDate = (date?: Date | string) => {
  if (!date) return '-'
  const d = typeof date === 'string' ? new Date(date) : date
  return d.toLocaleDateString('zh-CN', {
    month: 'short',
    day: 'numeric'
  })
}

const formatMessage = (content: string) => {
  // 将换行符转换为 HTML
  return content.replace(/\n/g, '<br>')
}

const truncateText = (text: string, maxLength: number) => {
  if (text.length <= maxLength) return text
  return text.slice(0, maxLength) + '...'
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
      isAutoScroll.value = true
    }
  })
}

const sendMessage = () => {
  if (!newMessage.value.trim()) return

  // 添加消息
  const userMessage: DiscussionMessage = {
    id: `msg-${Date.now()}`,
    session_id: sessionId,
    agent_id: null,
    message_type: 'text',
    content: newMessage.value,
    created_at: new Date()
  }

  messages.value.push(userMessage)
  newMessage.value = ''
  scrollToBottom()
}

const copyConclusion = () => {
  if (session.value?.conclusion) {
    navigator.clipboard.writeText(session.value.conclusion)
  }
}

const exportConclusion = () => {
  if (!session.value?.conclusion) return

  const content = `# ${session.value.title}\n\n团队: ${team.value?.display_name}\n\n## 讨论结论\n\n${session.value.conclusion}`
  const blob = new Blob([content], { type: 'text/markdown' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `${session.value.title}.md`
  a.click()
  URL.revokeObjectURL(url)
}

onMounted(() => {
  scrollToBottom()
})
</script>

<style scoped lang="scss">
@import '@/styles/variables.scss';

.session-detail-view {
  min-height: 100vh;
  background: $bg-primary;
  color: $text-primary;
  font-family: 'Inter', system-ui, sans-serif;
  display: flex;
  flex-direction: column;
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
  max-width: 1600px;
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
}

// 面包屑
.breadcrumb {
  background: $bg-secondary;
  border-bottom: 1px solid $border-color;
  padding: 16px 0;
}

.breadcrumb-container {
  max-width: 1600px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.breadcrumb-item {
  color: $text-secondary;
  text-decoration: none;
  transition: color 0.3s ease;

  &:hover {
    color: $accent-primary;
  }
}

.breadcrumb-separator {
  color: $text-tertiary;
}

.breadcrumb-current {
  color: $accent-primary;
  font-weight: 600;
}

// 主内容区
.session-main {
  display: flex;
  gap: 24px;
  flex: 1;
  padding: 24px;
  margin-top: 72px;
  max-width: 1600px;
  margin-left: auto;
  margin-right: auto;
}

// 左侧边栏
.agents-sidebar {
  width: 320px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.sidebar-header {
  padding: 24px;
  background: $bg-secondary;
  border: 1px solid $border-color;
  border-radius: 16px;
}

.sidebar-title {
  font-size: 20px;
  font-weight: 700;
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
}

.session-status {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;

  &.running {
    background: rgba(34, 197, 94, 0.15);
    color: #22c55e;

    .status-dot {
      animation: pulse 2s infinite;
    }
  }

  &.completed {
    background: rgba(251, 191, 36, 0.15);
    color: #fbbf24;
  }

  &.paused {
    background: rgba(245, 158, 11, 0.15);
    color: #f59e0b;
  }

  &.failed {
    background: rgba(239, 68, 68, 0.15);
    color: #ef4444;
  }

  .status-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: currentColor;
  }

  @keyframes pulse {
    0%, 100% { opacity: 1; transform: scale(1); }
    50% { opacity: 0.5; transform: scale(1.2); }
  }
}

.agents-list {
  background: $bg-secondary;
  border: 1px solid $border-color;
  border-radius: 16px;
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.agent-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid transparent;
  transition: all 0.3s ease;
  position: relative;

  &:hover {
    background: rgba($accent-primary, 0.05);
    border-color: rgba($accent-primary, 0.2);
  }

  &.active {
    background: rgba($accent-primary, 0.1);
    border-color: rgba($accent-primary, 0.3);

    &::after {
      content: '';
      position: absolute;
      right: 12px;
      top: 50%;
      transform: translateY(-50%);
      width: 8px;
      height: 8px;
      background: $accent-primary;
      border-radius: 50%;
      animation: pulse 1.5s infinite;
    }
  }

  &.busy {
    border-color: rgba(34, 197, 94, 0.3);
  }
}

.agent-avatar {
  position: relative;
}

.avatar-emoji {
  width:: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, $accent-primary, $accent-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 16px;
}

.avatar-status {
  position: absolute;
  bottom: -2px;
  right: -2px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2px solid $bg-secondary;

  &.idle {
    background: $text-tertiary;
  }

  &.busy {
    background: #22c55e;
    animation: pulse 2s infinite;
  }

  &.error {
    background: #ef4444;
  }
}

.agent-info {
  flex: 1;
}

.agent-name {
  font-size: 16px;
  font-weight: 600;
  color: white;
  margin-bottom: 4px;
}

.agent-role {
  font-size: 13px;
  color: $text-secondary;
  margin-bottom: 8px;
}

.agent-memory {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: $text-tertiary;

  .memory-icon {
    font-size: 14px;
  }

  .memory-text {
    flex: 1;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}

.agent-activity {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: $text-tertiary;
}

.activity-indicator {
  display: flex;
  gap: 3px;

  .wave {
    width: 4px;
    height: 4px;
    border-radius: 50%;
    background: #22c55e;
    animation: wave 1.4s infinite ease-in-out;

    &:nth-child(2) {
      animation-delay: 0.2s;
    }

    &:nth-child(3) {
      animation-delay: 0.4s;
    }
  }
}

@keyframes wave {
  0%, 100% { transform: scaleY(1); opacity: 0.5; }
  50% { transform: scaleY(2); opacity: 1; }
}

.activity-status {
  font-size: 11px;
  color: #22c55e;
}

.session-info-card {
  background: $bg-secondary;
  border: 1px solid $border-color;
  border-radius: 16px;
  padding: 20px;
}

.info-title {
  font-size: 16px;
  font-weight: 700;
  color: white;
  margin-bottom: 16px;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  font-size: 13px;

  .info-label {
    color: $text-secondary;
  }

  .info-value {
    color: white;
    font-weight: 500;
  }
}

// 消息区域
.messages-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: $bg-secondary;
  border: 1px solid $border-color;
  border-radius: 16px;
  overflow: hidden;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  scroll-behavior: smooth;

  &::-webkit-scrollbar {
    width: 8px;
  }

  &::-webkit-scrollbar-track {
    background: $bg-tertiary;
  }

  &::-webkit-scrollbar-thumb {
    background: $border-color;
    border-radius: 4px;

    &:hover {
      background: $accent-primary;
    }
  }
}

.system-message {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: rgba($accent-primary, 0.1);
  border: 1px solid rgba($accent-primary, 0.2);
  border-radius: 12px;
  margin-bottom: 24px;
  color: $accent-primary;
  font-size: 14px;
}

.system-badge {
  font-size: 18px;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-item {
  animation: fadeIn 0.5s ease forwards;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.message-avatar {
  .avatar-emoji {
    width: 36px;
    height: 36px;
    font-size: 14px;
  }
}

.message-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: $text-tertiary;
}

.message-sender {
  font-weight: 600;
  color: $text-secondary;
}

.message-time {
  color: $text-tertiary;
}

.message-content {
  background: $bg-tertiary;
  border: 1px solid $border-color;
  border-radius: 12px;
  padding: 16px;
}

.message-role-tag {
  display: inline-block;
  padding: 4px 12px;
  background: rgba($accent-primary, 0.15);
  color: $accent-primary;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 600;
  margin-bottom: 12px;
}

.message-text {
  font-size: 15px;
  line-height: 1.6;
  color: $text-primary;

  &::v-deep {
    color: $accent-secondary;
    font-weight: 500;
  }
}

.message-type-system {
  .message-content {
    background: rgba($accent-primary, 0.05);
    border-color: rgba($accent-primary, 0.15);

    .message-text {
      color: $accent-primary;
      font-style: italic;
    }
  }
}

.message-type-error {
  .message-content {
    background: rgba(239, 68, 68, 0.05);
    border-color: rgba(239, 68, 68, 0.15);

    .message-text {
      color: #ef4444;
    }
  }
}

.scroll-to-bottom {
  position: fixed;
  bottom: 120px;
  right: 400px;
  display:与其他页面保持一致;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: linear-gradient(135deg, $accent-primary, $accent-secondary);
  color: white;
  border: none;
  border-radius: 24px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 8px 24px rgba($accent-primary, 0.3);
  transition: all 0.3s ease;
  z-index: 100;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 32px rgba($accent-primary, 0.4);
  }
}

.input-area {
  padding: 16px;
  border-top: 1px solid $border-color;
}

.input-container {
  display: flex;
  gap: 12px;
}

.message-input {
  flex: 1;
  background: $bg-tertiary;
  border: 1px solid $border-color;
  border-radius: 12px;
  padding: 12px 16px;
  color: $text-primary;
  font-size: 14px;
  resize: none;
  font-family: inherit;
  transition: all 0.3s ease;

  &:focus {
    outline: none;
    border-color: $accent-primary;
    background: rgba($accent-primary, 0.05);
  }

  &::placeholder {
    color: $text-tertiary;
  }
}

.input-actions {
  display: flex;
  align-items: center;
}

.btn-send {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px 24px;
  background: linear-gradient(135deg, $accent-primary, $accent-secondary);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 8px 16px rgba($accent-primary, 0.3);
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

.input-hint {
  text-align: center;
  font-size: 12px;
  color: $text-tertiary;
  margin-top: 8px;
}

// 右侧边栏
.conclusion-sidebar {
  width: 380px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.conclusion-card {
  background: $bg-secondary;
  border: 1px solid $border-color;
  border-radius: 16px;
  overflow: hidden;
}

.conclusion-header {
  padding: 24px;
  background: linear-gradient(135deg, rgba($accent-primary, 0.1), rgba($accent-secondary, 0.05));
  border-bottom: 1px solid $border-color;
}

.conclusion-title {
  font-size: 20px;
  font-weight: 700;
  color: white;
  margin-bottom: 8px;
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

  .conclusion-badge {
    font-size: 18px;
  }
}

.conclusion-time {
  font-size: 13px;
  color: $text-secondary;
}

.conclusion-content {
  padding: 24px;
}

.conclusion-summary {
  .summary-title {
    font-size: 14px;
    font-weight: 600;
    color: $accent-secondary;
    margin-bottom: 12px;
  }

  .summary-text {
    font-size: 14px;
    line-height: 1.8;
    color: $text-primary;
    white-space: pre-line;
  }
}

.conclusion-actions {
  display: flex;
  gap: 12px;
  margin-top: 24px;
}

.btn-copy,
.btn-export {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px;
  background: rgba($accent-primary, 0.1);
  color: $accent-primary;
  border: 1px solid rgba($accent-primary, 0.3);
  border-radius: 10px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background: rgba($accent-primary, 0.2);
    transform: translateY(-2px);
  }
}

.pending-card {
  background: $bg-secondary;
  border: 1px solid $border-color;
  border-radius: 16px;
  padding: 48px 24px;
  text-align: center;
}

.pending-icon {
  color: $accent-primary;
  margin-bottom: 24px;
  animation: pulse 2s infinite;
}

.pending-title {
  font-size: 20px;
  font-weight: 700;
  color: white;
  margin-bottom: 12px;
}

.pending-desc {
  font-size: 14px;
  color: $text-secondary;
  line-height: 1.6;
}

.related-sessions {
  background: $bg-secondary;
  border: 1px solid $border-color;
  border-radius: 16px;
  padding: 20px;
}

.related-title {
  font-size: 16px;
  font-weight: 700;
  color: white;
  margin-bottom: 16px;
}

.related-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 10px;
  text-decoration: none;
  transition: all 0.3s ease;
  margin-bottom: 8px;

  &:last-child {
    margin-bottom: 0;
  }

  &:hover {
    background: rgba($accent-primary, 0.1);
    transform: translateX(4px);
  }
}

.related-status {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;

  &.completed {
    background: #22c55e;
  }

  &.running {
    background: #fbbf24;
    animation: pulse 2s infinite;
  }

  &.pending {
    background: $text-tertiary;
  }
}

.related-title-text {
  flex: 1;
  color: white;
  font-size: 14px;
  font-weight: 500;
}

.related-time {
  color: $text-tertiary;
  font-size: 12px;
}

// 响应式
@media (max-width: 1400px) {
  .conclusion-sidebar {
    width: 320px;
  }
}

@media (max-width: 1200px) {
  .session-main {
    flex-direction: column;
  }

  .agents-sidebar {
    width: 100%;
  }

  .conclusion-sidebar {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .nav-links {
    display: none;
  }

  .agents-list {
    max-height: 300px;
    overflow-y: auto;
  }

  .scroll-to-bottom {
    right: 20px;
    bottom: 80px;
  }
}
</style>
