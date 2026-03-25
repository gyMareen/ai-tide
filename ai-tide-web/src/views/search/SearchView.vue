<template>
  <div class="search-view">
    <div class="search-container">
      <!-- 搜索框 -->
      <div class="search-box-container">
        <el-input
          v-model="searchQuery"
          placeholder="搜索 AI 技术、模型、产品..."
          size="large"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
          <template #append>
            <el-button :icon="Search" @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>

      <!-- 筛选器 -->
      <div class="filters">
        <div class="filter-group">
          <span class="filter-label">类型:</span>
          <el-select
            v-model="filters.type"
            placeholder="全部类型"
            clearable
            @change="handleSearch"
          >
            <el-option label="AI 模型" value="MODEL" />
            <el-option label="AI 产品" value="PRODUCT" />
            <el-option label="技术文章" value="ARTICLE" />
            <el-option label="教程" value="TUTORIAL" />
            <el-option label="资源" value="RESOURCE" />
          </el-select>
        </div>

        <div class="filter-group">
          <span class="filter-label">分类:</span>
          <el-select
            v-model="filters.categoryId"
            placeholder="全部分类"
            clearable
            filterable
            @change="handleSearch"
          >
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </div>

        <div class="filter-group">
          <span class="filter-label">排序:</span>
          <el-select
            v-model="filters.sortBy"
            placeholder="默认排序"
            @change="handleSearch"
          >
            <el-option label="相关性" value="relevance" />
            <el-option label="最新发布" value="publishTime" />
            <el-option label="浏览量" value="viewCount" />
            <el-option label="点赞数" value="likeCount" />
            <el-option label="评分" value="averageRating" />
          </el-select>
        </div>
      </div>

      <!-- 搜索结果 -->
      <div v-if="hasSearched">
        <div v-if="isLoading" class="loading-container">
          <el-skeleton :rows="10" animated />
        </div>

        <div v-else-if="results.length > 0">
          <div class="results-header">
            <h2 class="results-title">搜索结果</h2>
            <span class="results-count">找到 {{ total }} 个结果</span>
          </div>

          <div class="results-list">
            <router-link
              v-for="item in results"
              :key="item.id"
              :to="`/content/${item.id}`"
              class="result-item"
            >
              <div class="result-meta">
                <el-tag :type="getTypeColor(item.type)">
                  {{ getTypeLabel(item.type) }}
                </el-tag>
                <span v-if="item.category" class="result-category">
                  {{ item.category.name }}
                </span>
                <span class="result-date">{{ formatDate(item.publishTime) }}</span>
              </div>

              <h3 class="result-title">{{ item.title }}</h3>

              <p class="result-description">{{ item.description }}</p>

              <div class="result-stats">
                <span class="stat">
                  <el-icon><View /></el-icon>
                  {{ formatNumber(item.viewCount) }}
                </span>
                <span class="stat">
                  <el-icon><Star /></el-icon>
                  {{ formatNumber(item.likeCount) }}
                </span>
                <span class="stat">
                  <el-icon><ChatDotRound /></el-icon>
                  {{ formatNumber(item.commentCount) }}
                </span>
                <span class="stat rating">
                  <el-icon><StarFilled /></el-icon>
                  {{ item.averageRating.toFixed(1) }}
                </span>
              </div>
            </router-link>
          </div>

          <!-- 分页 -->
          <div v-if="totalPages > 1" class="pagination-container">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="total"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @current-change="handlePageChange"
              @size-change="handleSizeChange"
            />
          </div>
        </div>

        <el-empty v-else description="没有找到相关结果" />
      </div>

      <!-- 初始提示 -->
      <div v-else class="initial-hint">
        <div class="hint-icon">
          <el-icon :size="64"><Search /></el-icon>
        </div>
        <h3 class="hint-title">搜索 AI 资源</h3>
        <p class="hint-desc">输入关键词搜索 AI 模型、产品、文章等</p>

        <div class="hot-searches">
          <h4 class="hot-title">热门搜索</h4>
          <el-tag
            v-for="tag in hotSearches"
            :key="tag"
            class="hot-tag"
            @click="quickSearch(tag)"
          >
            {{ tag }}
          </el-tag>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useContentStore } from '@/stores/content'
import { Search, View, Star, StarFilled, ChatDotRound } from '@element-plus/icons-vue'

interface Category {
  id: string
  name: string
}

interface SearchResult {
  id: string
  type: string
  title: string
  description: string
  publishTime: string
  viewCount: number
  likeCount: number
  commentCount: number
  averageRating: number
  category?: {
    name: string
  }
}

const route = useRoute()
const router = useRouter()
const contentStore = useContentStore()

const searchQuery = ref('')
const hasSearched = ref(false)
const isLoading = ref(false)

const filters = ref({
  type: '',
  categoryId: '',
  sortBy: 'relevance',
})

const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const totalPages = ref(0)

const results = ref<SearchResult[]>([])
const categories = ref<Category[]>([])

const hotSearches = ref([
  'GPT',
  'Claude',
  'Stable Diffusion',
  'LLaMA',
  'LangChain',
  'RAG',
  '向量数据库',
])

onMounted(() => {
  // 从路由参数获取搜索词
  const query = route.query.q as string
  const tag = route.query.tag as string

  if (query) {
    searchQuery.value = query
    handleSearch()
  } else if (tag) {
    searchQuery.value = tag
    handleSearch()
  }

  // 获取分类列表
  fetchCategories()
})

async function fetchCategories() {
  try {
    const data = await contentStore.fetchCategories()
    if (data) {
      categories.value = data
    }
  } catch (e) {
    console.error('Failed to fetch categories:', e)
  }
}

function handleSearch() {
  if (!searchQuery.value.trim()) {
    return
  }

  hasSearched.value = true
  currentPage.value = 1
  fetchResults()
}

function quickSearch(keyword: string) {
  searchQuery.value = keyword
  handleSearch()
}

async function fetchResults() {
  isLoading.value = true
  try {
    const params = new URLSearchParams()
    params.append('keyword', searchQuery.value)
    params.append('page', currentPage.value.toString())
    params.append('pageSize', pageSize.value.toString())

    if (filters.value.type) {
      params.append('type', filters.value.type)
    }

    if (filters.value.categoryId) {
      params.append('categoryId', filters.value.categoryId)
    }

    if (filters.value.sortBy) {
      params.append('sortBy', filters.value.sortBy)
    }

    const response = await fetch(`/api/search?${params.toString()}`)

    if (!response.ok) {
      throw new Error('搜索失败')
    }

    const data = await response.json()

    if (data.code === 200 && data.data) {
      results.value = data.data.list || []
      total.value = data.data.total || 0
      totalPages.value = data.data.totalPages || 0
    }
  } catch (e) {
    console.error('Search failed:', e)
  } finally {
    isLoading.value = false
  }
}

function handlePageChange(page: number) {
  currentPage.value = page
  fetchResults()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function handleSizeChange(size: number) {
  pageSize.value = size
  currentPage.value = 1
  fetchResults()
}

function getTypeLabel(type: string) {
  const labels = { MODEL: 'AI 模型', PRODUCT: 'AI 产品', ARTICLE: '技术文章', TUTORIAL: '教程', RESOURCE: '资源' }
  return labels[type as keyof typeof labels] || type
}

function getTypeColor(type: string) {
  const colors = { MODEL: 'danger', PRODUCT: 'success', ARTICLE: 'warning', TUTORIAL: 'info', RESOURCE: 'primary' }
  return colors[type as keyof typeof colors] || 'default'
}

function formatDate(date?: string) {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

function formatNumber(num: number) {
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num.toString()
}
</script>

<style scoped lang="scss">
@use '@/styles/variables.scss' as *;

.search-view {
  min-height: 100vh;
  background: $bg-primary;
  padding: 32px;
}

.search-container {
  max-width: 1200px;
  margin: 0 auto;
}

.search-box-container {
  margin-bottom: 32px;

  .el-input {
    font-size: 18px;
  }
}

.filters {
  display: flex;
  gap: 24px;
  padding: 24px;
  background: $bg-secondary;
  border: 1px solid $border-color;
  border-radius: 16px;
  margin-bottom: 32px;
  flex-wrap: wrap;

  .filter-group {
    display: flex;
    align-items: center;
    gap: 8px;

    .filter-label {
      color: $text-secondary;
      font-size: 14px;
    }
  }
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  .results-title {
    font-size: 24px;
    font-weight: 700;
    color: $text-primary;
  }

  .results-count {
    color: $text-secondary;
    font-size: 14px;
  }
}

.results-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 32px;
}

.result-item {
  padding: 24px;
  background: $bg-secondary;
  border: 1px solid $border-color;
  border-radius: 16px;
  text-decoration: none;
  transition: all 0.3s ease;

  &:hover {
    border-color: $accent-primary;
    background: rgba($accent-primary, 0.05);
    transform: translateY(-2px);
  }

  .result-meta {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 12px;

    .result-category {
      color: $text-secondary;
      font-size: 14px;
    }

    .result-date {
      color: $text-tertiary;
      font-size: 12px;
    }
  }

  .result-title {
    font-size: 20px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 8px;
  }

  .result-description {
    font-size: 14px;
    color: $text-secondary;
    line-height: 1.6;
    margin-bottom: 16px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .result-stats {
    display: flex;
    gap: 24px;

    .stat {
      display: flex;
      align-items: center;
      gap: 4px;
      color: $text-secondary;
      font-size: 14px;

      &.rating {
        color: #fbbf24;
      }
    }
  }
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 32px 0;
}

.initial-hint {
  text-align: center;
  padding: 80px 32px;

  .hint-icon {
    margin-bottom: 24px;
    color: $accent-primary;
  }

  .hint-title {
    font-size: 28px;
    font-weight: 700;
    color: $text-primary;
    margin-bottom: 12px;
  }

  .hint-desc {
    font-size: 16px;
    color: $text-secondary;
    margin-bottom: 48px;
  }

  .hot-searches {
    .hot-title {
      font-size: 16px;
      font-weight: 600;
      color: $text-primary;
      margin-bottom: 16px;
    }

    .hot-tag {
      margin: 0 8px 8px 0;
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover {
        background: $accent-primary;
        color: white;
        transform: translateY(-2px);
      }
    }
  }
}

@media (max-width: 768px) {
  .search-view {
    padding: 16px;
  }

  .filters {
    flex-direction: column;
    gap: 16px;
  }

  .results-list {
    .result-item {
      padding: 16px;
    }
  }

  .initial-hint {
    padding: 40px 16px;

    .hint-title {
      font-size: 24px;
    }
  }
}
</style>
