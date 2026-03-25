<template>
  <div class="category-view">
    <div class="category-container">
      <!-- 返回按钮 -->
      <el-button class="back-btn" text @click="router.back()">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>

      <template v-if="isLoading">
        <el-skeleton :rows="10" animated />
      </template>

      <template v-else-if="category">
        <!-- 分类标题 -->
        <div class="category-header">
          <div class="category-icon">{{ category.icon || '📂' }}</div>
          <div>
            <h1 class="category-title">{{ category.name }}</h1>
            <p class="category-desc">{{ category.description || '' }}</p>
            <div class="category-stats">
              <span class="stat">{{ category.contentCount }} 个内容</span>
            </div>
          </div>
        </div>

        <!-- 筛选器 -->
        <div class="filters">
          <div class="filter-group">
            <span class="filter-label">类型:</span>
            <el-select
              v-model="filters.type"
              placeholder="全部类型"
              clearable
              @change="handleFilterChange"
            >
              <el-option label="AI 模型" value="MODEL" />
              <el-option label="AI 产品" value="PRODUCT" />
              <el-option label="技术文章" value="ARTICLE" />
              <el-option label="教程" value="TUTORIAL" />
              <el-option label="资源" value="RESOURCE" />
            </el-select>
          </div>

          <div class="filter-group">
            <span class="filter-label">排序:</span>
            <el-select
              v-model="filters.sortBy"
              placeholder="默认排序"
              @change="handleFilterChange"
            >
              <el-option label="最新发布" value="publishTime" />
              <el-option label="浏览量" value="viewCount" />
              <el-option label="点赞数" value="likeCount" />
              <el-option label="评分" value="averageRating" />
            </el-select>
          </div>
        </div>

        <!-- 内容列表 -->
        <div v-if="contents.length > 0">
          <div class="content-grid">
            <router-link
              v-for="item in contents"
              :key="item.id"
              :to="`/content/${item.id}`"
              class="content-card"
            >
              <div
                v-if="item.coverImage"
                class="card-image"
                :style="{ backgroundImage: `url(${item.coverImage})` }"
              >
                <span class="card-type-tag">{{ getTypeLabel(item.type) }}</span>
              </div>

              <div class="card-body">
                <h3 class="card-title">{{ item.title }}</h3>
                <p class="card-desc">{{ item.description }}</p>

                <div class="card-meta">
                  <div class="rating-stars">
                    <span
                      v-for="n in 5"
                      :key="n"
                      class="star"
                      :class="{ filled: n <= Math.round(item.averageRating) }"
                    >★</span>
                  </div>

                  <div class="stats">
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
                  </div>
                </div>
              </div>
            </router-link>
          </div>

          <!-- 分页 -->
          <div v-if="totalPages > 1" class="pagination-container">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="total"
              :page-sizes="[12, 24, 48]"
              layout="total, sizes, prev, pager, next, jumper"
              @current-change="handlePageChange"
              @size-change="handleSizeChange"
            />
          </div>
        </div>

        <el-empty v-else description="该分类下暂无内容" />
      </template>

      <el-empty v-else description="分类不存在或已删除" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { View, Star, ChatDotRound, ArrowLeft } from '@element-plus/icons-vue'

interface Category {
  id: string
  name: string
  icon?: string
  description?: string
  contentCount: number
}

interface Content {
  id: string
  type: string
  title: string
  description: string
  coverImage?: string
  averageRating: number
  viewCount: number
  likeCount: number
  commentCount: number
}

const route = useRoute()
const router = useRouter()

const category = ref<Category | null>(null)
const contents = ref<Content[]>([])
const isLoading = ref(false)

const filters = ref({
  type: '',
  sortBy: 'publishTime',
})

const currentPage = ref(1)
const pageSize = ref(24)
const total = ref(0)
const totalPages = ref(0)

onMounted(() => {
  fetchCategory()
  fetchContents()
})

async function fetchCategory() {
  const id = route.params.id as string
  try {
    const response = await fetch(`/api/category/${id}`)

    if (response.ok) {
      const data = await response.json()
      if (data.code === 200) {
        category.value = data.data
      }
    }
  } catch (e) {
    console.error('Failed to fetch category:', e)
  }
}

async function fetchContents() {
  isLoading.value = true
  const id = route.params.id as string

  try {
    const params = new URLSearchParams()
    params.append('categoryId', id)
    params.append('page', currentPage.value.toString())
    params.append('pageSize', pageSize.value.toString())

    if (filters.value.type) {
      params.append('type', filters.value.type)
    }

    if (filters.value.sortBy) {
      params.append('sortBy', filters.value.sortBy)
    }

    const response = await fetch(`/api/content/list?${params.toString()}`)

    if (response.ok) {
      const data = await response.json()
      if (data.code === 200 && data.data) {
        contents.value = data.data.list || []
        total.value = data.data.total || 0
        totalPages.value = data.data.totalPages || 0
      }
    }
  } catch (e) {
    console.error('Failed to fetch contents:', e)
  } finally {
    isLoading.value = false
  }
}

function handleFilterChange() {
  currentPage.value = 1
  fetchContents()
}

function handlePageChange(page: number) {
  currentPage.value = page
  fetchContents()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function handleSizeChange(size: number) {
  pageSize.value = size
  currentPage.value = 1
  fetchContents()
}

function getTypeLabel(type: string) {
  const labels = { MODEL: 'AI 模型', PRODUCT: 'AI 产品', ARTICLE: '技术文章', TUTORIAL: '教程', RESOURCE: '资源' }
  return labels[type as keyof typeof labels] || type
}

function formatNumber(num: number) {
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num.toString()
}
</script>

<style scoped lang="scss">
@use '@/styles/variables.scss' as *;

.category-view {
  min-height: 100vh;
  background: $bg-primary;
  padding: 32px;
}

.category-container {
  max-width: 1200px;
  margin: 0 auto;
}

.back-btn {
  margin-bottom: 24px;
  color: $text-secondary;
  font-size: 16px;
}

.category-header {
  display: flex;
  gap: 24px;
  padding: 32px;
  background: $bg-secondary;
  border: 1px solid $border-color;
  border-radius: 24px;
  margin-bottom: 32px;

  .category-icon {
    font-size: 64px;
  }

  .category-title {
    font-size: 36px;
    font-weight: 700;
    color: $text-primary;
    margin-bottom: 12px;
  }

  .category-desc {
    font-size: 16px;
    color: $text-secondary;
    margin-bottom: 16px;
  }

  .category-stats {
    .stat {
      color: $text-tertiary;
      font-size: 14px;
    }
  }
}

.filters {
  display: flex;
  gap: 24px;
  padding: 20px;
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

.content-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 32px;
}

.content-card {
  display: flex;
  flex-direction: column;
  background: $bg-secondary;
  border: 1px solid $border-color;
  border-radius: 16px;
  overflow: hidden;
  text-decoration: none;
  transition: all 0.3s ease;

  &:hover {
    border-color: $accent-primary;
    transform: translateY(-4px);
    box-shadow: $shadow-md;
  }

  .card-image {
    position: relative;
    height: 160px;
    background-size: cover;
    background-position: center;
    background-color: $bg-tertiary;

    .card-type-tag {
      position: absolute;
      top: 12px;
      right: 12px;
      padding: 4px 12px;
      background: rgba($accent-primary, 0.9);
      color: white;
      border-radius: 8px;
      font-size: 12px;
    }
  }

  .card-body {
    flex: 1;
    padding: 20px;
    display: flex;
    flex-direction: column;

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
      .rating-stars {
        display: flex;
        gap: 2px;
        font-size: 14px;
        color: $text-tertiary;
        margin-bottom: 12px;

        .star.filled {
          color: #fbbf24;
        }
      }

      .stats {
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
    }
  }
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 32px 0;
}

@media (max-width: 1200px) {
  .content-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .category-view {
    padding: 16px;
  }

  .category-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .filters {
    flex-direction: column;
  }

  .content-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }
}
</style>
