<template>
  <div v-if="totalPages > 1" class="pagination">
    <el-button
      :disabled="currentPage <= 1"
      circle
      @click="handlePageChange(currentPage - 1)"
    >
      <el-icon><ArrowLeft /></el-icon>
    </el-button>

    <div class="page-numbers">
      <el-button
        v-for="page in visiblePages"
        :key="page"
        :type="page === currentPage ? 'primary' : 'default'"
        circle
        :class="{
          'page-btn': true,
          'page-btn--ellipsis': page === '...'
        }"
        :disabled="page === '...'"
        @click="page !== '...' && handlePage(page as number)"
      >
        {{ page }}
      </el-button>
    </div>

    <el-button
      :disabled="currentPage >= totalPages"
      circle
      @click="handlePageChange(currentPage + 1)"
    >
      <el-icon><ArrowRight /></el-icon>
    </el-button>

    <div class="page-info">
      <span class="current">{{ currentPage }}</span>
      <span class="divider">/</span>
      <span class="total">{{ totalPages }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'

const props = defineProps<{
  currentPage: number
  pageSize: number
  total: number
}>()

const emit = defineEmits<{
  change: [page: number]
  sizeChange: [size: number]
}>()

const totalPages = computed(() => Math.ceil(props.total / props.pageSize))

const visiblePages = computed(() => {
  const pages: (number | string)[] = []
  const maxVisible = 5
  const current = props.currentPage
  const total = totalPages.value

  if (total <= maxVisible) {
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    if (current <= 3) {
      for (let i = 1; i <= 4; i++) {
        pages.push(i)
      }
      pages.push('...')
      pages.push(total)
    } else if (current >= total - 2) {
      pages.push(1)
      pages.push('...')
      for (let i = total - 3; i <= total; i++) {
        pages.push(i)
      }
    } else {
      pages.push(1)
      pages.push('...')
      for (let i = current - 1; i <= current + 1; i++) {
        pages.push(i)
      }
      pages.push('...')
      pages.push(total)
    }
  }

  return pages
})

function handlePageChange(page: number) {
  emit('change', page)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function handlePage(page: number) {
  handlePageChange(page)
}
</script>

<style scoped lang="scss">
@use '@/styles/variables.scss' as *;

.pagination {
  display: flex;
  align-items: center;
  gap: 12px;
  justify-content: center;
  padding: 24px 0;
}

.page-numbers {
  display: flex;
  gap: 8px;
}

.page-btn {
  min-width: 36px;
  height: 36px;
  font-size: 14px;

  &--ellipsis {
    cursor: default;
  }
}

.page-info {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: $text-secondary;
  margin-left: 12px;

  .current {
    font-weight: 600;
    color: $text-primary;
  }

  .divider {
    color: $text-tertiary;
  }
}
</style>
