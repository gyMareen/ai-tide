<template>
  <div class="team-list-view">
    <div class="team-list-container">
      <!-- Header -->
      <div class="header-section">
        <h1 class="page-title">智能体团队</h1>
        <p class="page-desc">观看多个 AI 智能体团队协作讨论</p>
      </div>

      <!-- Filter Bar -->
      <div class="filter-bar">
        <el-input
          v-model="searchQuery"
          placeholder="搜索团队..."
          :prefix-icon="Search"
          clearable
          class="search-input"
        />
        <el-select v-model="filterStatus" placeholder="状态" clearable>
          <el-option label="全部" value="" />
          <el-option label="活跃" value="active" />
          <el-option label="空闲" value="idle" />
        </el-select>
      </div>

      <!-- Teams List -->
      <div v-if="isLoading" class="loading-container">
        <el-skeleton :rows="6" animated />
      </div>

      <div v-else class="teams-content">
        <div
          v-for="team in filteredTeams"
          :key="team.id"
          class="team-card"
          @click="selectTeamTeam(team)"
        >
          <div class="team-header">
            <div class="team-icon">🤖</div>
            <div class="team-info">
              <h3 class="team-name">{{ team.name }}</h3>
              <p class="team-description">{{ team.display_name }}</p>
              <div class="team-meta">
                <el-tag :type="team.status === 'active' ? 'success' : 'info'" size="small">
                  {{ team.status === 'active' ? '活跃' : '空闲' }}
                </el-tag>
                <span class="team-members">{{ team.memberCount }} 成员</span>
              </div>
            </div>
          </div>

          <div class="team-latest-activity">
            <div v-if="team.members && team.members.length > 0" class="members-avatars">
              <el-tooltip
                v-for="(member, index) in team.members.slice(0, 5)"
                :key="member.id"
                :content="member.name"
                placement="top"
              >
                <el-avatar :size="28" :src="member.avatar_url">
                  {{ member.name.charAt(0) }}
                </el-avatar>
                <el-badge
                  v-if="index === 4 && team.members.length > 5"
                  type="info"
                  :value="`+${team.members.length - 5}`"
                />
              </el-tooltip>
            </div>

            <div v-if="team.latestSession" class="latest-message">
              <span class="message-prefix">{{ team.members[0]?.name }}:</span>
              <span class="message-text">{{ team.latestSession }}</span>
            </div>
          </div>

          <div class="team-stats">
            <div class="stat-item">
              <span class="stat-label">会话</span>
              <span class="stat-value">{{ formatNumber(team.sessionCount) }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">消息</span>
              <span class="stat-value">{{ formatNumber(team.messageCount) }}</span>
            </div>
          </div>
        </div>

        <el-empty
          v-if="filteredTeams.length === 0"
          description="没有找到团队"
        />

        <!-- Load More -->
        <div v-if="hasMore" class="load-more">
          <el-button
            :loading="isLoadingMore"
            @click="loadMoreTeams"
          >
            加载更多
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Star, StarFilled } from '@element-plus/icons-vue'

const router = useRouter()

const searchQuery = ref('')
const filterStatus = ref('')
const teams = ref<Team[]>([])
const isLoading = ref(false)
const isLoadingMore = ref(false)
const currentPage = ref(1)
const pageSize = 12
const total = ref(0)

interface Team {
  id: string
  name: string
  display_name: string
  bot_app_id: string | null
  manager_prompt: string | null
  status: string
  created_at: string
  updated_at: string
  memberCount: number
  sessionCount: number
  messageCount: number
  isFavorite: boolean
  members: Array<{
    id: string
    name: string
    team_id: string
    role: string
    personality: string
    status: string
    memory_summary: string
    avatar_url: string | null
  }>
  latestSession?: string
}

const filteredTeams = computed(() => {
  let result = teams.value as Team[]

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter((team: Team) =>
      team.name.toLowerCase().includes(query) ||
      team.display_name.toLowerCase().includes(query)
    )
  }

  if (filterStatus.value) {
    result = result.filter((team: Team) => team.status === filterStatus.value)
  }

  return result
})

const hasMore = computed(() => teams.value.length < total.value)

function selectTeamTeam(team: Team) {
  router.push(`/agent/session/${team.id}`)
}

function toggleFavorite(team: Team) {
  team.isFavorite = !team.isFavorite
  // TODO: Call API to toggle favorite
}

async function loadTeams(page: number) {
  isLoading.value = page === 1
  isLoadingMore.value = page > 1

  try {
    // TODO: Replace with actual API call
    // const response = await fetch(`/api/agent/teams?page=${page}&pageSize=${pageSize}`)
    // const data = await response.json()

    // Mock data for now
    const mockData = {
      teams: [
        {
          id: 'team-ai',
          name: 'ai-tide',
          display_name: '🚀 产品部',
          bot_app_id: null,
          manager_prompt: null,
          status: 'active',
          created_at: new Date('2026-02-01').toISOString(),
          updated_at: new Date('2026-03-22').toISOString(),
          memberCount: 4,
          sessionCount: 128,
          messageCount: 8521,
          isFavorite: false,
          members: [
            { id: 'm1', name: 'Alice', team_id: 'team-ai', role: 'manager', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
            { id: 'm2', name: 'Bob', team_id: 'team-ai', role: 'worker', personality: '', status: 'busy', memory_summary: '', avatar_url: null, created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
            { id: 'm3', name: 'Charlie', team_id: 'team-ai', role: 'worker', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
            { id: 'm4', name: 'Diana', team_id: 'team-ai', role: 'assistant', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date().toISOString(), updated_at: new Date().toISOString() }
          ],
          latestSession: '讨论了产品功能优化的方案，建议从用户体验角度出发...'
        },
        {
          id: 'team-product',
          name: 'product',
          display_name: '🚀 产品部',
          bot_app_id: null,
          manager_prompt: null,
          status: 'active',
          created_at: new Date('2026-02-01').toISOString(),
          updated_at: new Date('2026-03-22').toISOString(),
          memberCount: 4,
          sessionCount: 28,
          messageCount: 8921,
          isFavorite: true,
          members: [
            { id: 'm6', name: 'Frank', team_id: 'team-product', role: 'manager', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
            { id: 'm7', name: 'Grace', team_id: 'team-product', role: 'worker', personality: '', status: 'busy', memory_summary: '', avatar_url: null, created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
            { id: 'm8', name: 'Henry', team_id: 'team-product', role: 'worker', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
            { id: 'm9', name: 'Ivy', team_id: 'team-product', role: 'assistant', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date().toISOString(), updated_at: new Date().toISOString() }
          ],
          latestSession: '分析了竞品的用户需求，提出了三个核心改进方向'
        },
        {
          id: 'team-design',
          name: 'design',
          display_name: '🎨 设计部',
          bot_app_id: null,
          manager_prompt: null,
          status: 'active',
          created_at: new Date('2026-01-20').toISOString(),
          updated_at: new Date('2026-03-18').toISOString(),
          memberCount: 3,
          sessionCount: 35,
          messageCount: 11256,
          isFavorite: false,
          members: [
            { id: 'm10', name: 'Jack', team_id: 'team-design', role: 'manager', personality: '', status: 'busy', memory_summary: '', avatar_url: null, created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
            { id: 'm11', name: 'Kate', team_id: 'team-design', role: 'worker', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date().toISOString(), updated_at: new Date().toISOString() },
            { id: 'm12', name: 'Leo', team_id: 'team-design', role: 'assistant', personality: '', status: 'idle', memory_summary: '', avatar_url: null, created_at: new Date().toISOString(), updated_at: new Date().toISOString() }
          ],
          latestSession: '完成了初步设计方案，准备进入开发阶段'
        },
        {
          id: 'team-research',
          name: 'research',
          display_name: '🔬 研发部',
          bot_app_id: null,
          manager_prompt: null,
          status: 'active',
          created_at: new Date('2026-01-15').toISOString(),
          updated_at: new Date('2026-03-15').toISOString(),
          memberCount: 3,
          sessionCount: 15,
          messageCount: 450,
          isFavorite: false,
          members: []
        }
      ],
      total: 4,
      page: 1,
      pageSize: 12
    }

    if (page === 1) {
      teams.value = mockData.teams
      total.value = mockData.total
    } else {
      teams.value.push(...mockData.teams)
    }

    currentPage.value = page
  } catch (error) {
    console.error('Failed to load teams:', error)
  } finally {
    isLoading.value = false
    isLoadingMore.value = false
  }
}

function loadMoreTeams() {
  loadTeams(currentPage.value + 1)
}

function formatNumber(num: number) {
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num.toString()
}

onMounted(() => {
  loadTeams(1)
})
</script>

<style scoped lang="scss">
@use '@/styles/variables.scss' as *;

.team-list-view {
  min-height: 100vh;
  background: $bg-primary;
  padding: 32px;
}

.team-list-container {
  max-width: 1200px;
  margin: 0 auto;
}

.header-section {
  text-align: center;
  margin-bottom: 40px;

  .page-title {
    font-size: 36px;
    font-weight: 700;
    color: $text-primary;
    margin-bottom: 12px;
  }

  .page-desc {
    font-size: 18px;
    color: $text-secondary;
  }
}

.filter-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 32px;
  padding: 20px;
  background: $bg-secondary;
  border: 1px solid $border-color;
  border-radius: 12px;

  .search-input {
    flex: 1;
  }
}

.loading-container {
  padding: 40px 0;
}

.teams-content {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.team-card {
  background: $bg-secondary;
  border: 1px solid $border-color;
  border-radius: 16px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    border-color: $accent-primary;
    background: rgba($accent-primary, 0.05);
    transform: translateY(-4px);
  }
}

.team-header {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;

  .team-icon {
    font-size: 48px;
    line-height: 1;
  }

  .team-info {
    flex: 1;

    .team-name {
      font-size: 18px;
      font-weight: 600;
      color: $text-primary;
      margin-bottom: 4px;
    }

    .team-description {
      font-size: 14px;
      color: $text-secondary;
      margin-bottom: 12px;
    }

    .team-meta {
      display: flex;
      align-items: center;
      gap: 12px;

      .team-members {
        font-size: 13px;
        color: $text-tertiary;
      }
    }
  }
}

.team-latest-activity {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 16px;

  .members-avatars {
    display: flex;
    gap: -8px;
  }

  .latest-message {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px;
    background: rgba($accent-secondary, 0.1);
    border-radius: 8px;

    .message-prefix {
      font-size: 12px;
      font-weight: 500;
      color: $accent-secondary;
    }

    .message-text {
      font-size: 13px;
      color: $text-secondary;
      flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}

.team-stats {
  display: flex;
  gap: 24px;
  padding-top: 12px;
  border-top: 1px solid $border-light;

  .stat-item {
    display: flex;
    flex-direction: column;
    gap: 4px;

    .stat-label {
      font-size: 12px;
      color: $text-tertiary;
    }

    .stat-value {
      font-size: 16px;
      font-weight: 600;
      color: $text-primary;
    }
  }
}

.load-more {
  text-align: center;
  margin-top: 40px;
  padding-bottom: 40px;
}

@media (max-width: 768px) {
  .team-list-view {
    padding: 16px;
  }

  .team-list-container {
    max-width: 100%;
  }

  .teams-content {
    grid-template-columns: 1fr;
  }

  .team-stats {
    flex-direction: column;
    gap: 12px;
  }
}
</style>
