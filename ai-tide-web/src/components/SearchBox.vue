<template>
  <div class="search-box" :class="{ 'search-box--expanded': isExpanded }">
    <el-input
      v-model="searchQuery"
      :placeholder="placeholder"
      :prefix-icon="Search"
      clearable
      @focus="handleFocus"
      @blur="handleBlur"
      @keyup.enter="handleSearch"
    />

    <!-- Search history -->
    <div v-if="showHistory && history.length > 0" class="search-history">
      <div class="history-header">
        <span class="history-title">搜索历史</span>
        <el-button text size="small" @click="clearHistory">
          清除
        </el-button>
      </div>
      <div class="history-list">
        <div
          v-for="(item, index) in history"
          :key="index"
          class="history-item"
          @click="searchItem(item)"
        >
          <el-icon><Clock /></el-icon>
          <span>{{ item }}</span>
          <el-button
            text
            circle
            size="small"
            @click.stop="removeHistoryItem(index)"
          >
            <el-icon><Close /></el-icon>
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Clock, Close } from '@element-plus/icons-vue'

const router = useRouter()
const emit = defineEmits<{
  search: [query: string]
}>()

const props = withDefaults(defineProps<{
  placeholder?: string
  showHistoryOnFocus?: boolean
}>(), {
  placeholder: '搜索 AI 技术...',
  showHistoryOnFocus: true,
})

const searchQuery = ref('')
const isExpanded = ref(false)
const history = ref<string[]>([])

const showHistory = computed(() =>
  props.showHistoryOnFocus && isExpanded.value && !searchQuery.value
)

onMounted(() => {
  loadHistory()
})

function loadHistory() {
  try {
    const saved = localStorage.getItem('searchHistory')
    if (saved) {
      history.value = JSON.parse(saved)
    }
  } catch (e) {
    console.error('Failed to load search history:', e)
  }
}

function saveHistory() {
  try {
    localStorage.setItem('searchHistory', JSON.stringify(history.value))
  } catch (e) {
    console.error('Failed to save search history:', e)
  }
}

function handleFocus() {
  isExpanded.value = true
}

function handleBlur() {
  setTimeout(() => {
    isExpanded.value = false
  }, 200)
}

function handleSearch() {
  if (!searchQuery.value.trim()) return

  // Add to history
  addToHistory(searchQuery.value)

  // Emit search event
  emit('search', searchQuery.value)

  // Navigate to search page
  router.push(`/search?q=${encodeURIComponent(searchQuery.value)}`)
}

function searchItem(query: string) {
  searchQuery.value = query
  handleSearch()
}

function addToHistory(query: string) {
  // Remove if already exists
  const index = history.value.indexOf(query)
  if (index > -1) {
    history.value.splice(index, 1)
  }

  // Add to beginning
  history.value.unshift(query)

  // Keep only last 10
  if (history.value.length > 10) {
    history.value = history.value.slice(0, 10)
  }

  saveHistory()
}

function removeHistoryItem(index: number) {
  history.value.splice(index, 1)
  saveHistory()
}

function clearHistory() {
  history.value = []
  saveHistory()
}
</script>

<style scoped lang="scss">
@use '@/styles/variables.scss' as *;

.search-box {
  position: relative;
}

.search-history {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  right: 0;
  background: $bg-secondary;
  border: 1px solid $border-color;
  border-radius: 12px;
  padding: 16px;
  box-shadow: $shadow-lg;
  z-index: 100;
  animation: slideDown 0.2s ease;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;

  .history-title {
    font-size: 14px;
    font-weight: 600;
    color: $text-primary;
  }
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.history-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: $bg-tertiary;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    background: rgba($accent-primary, 0.1);
  }

  span {
    flex: 1;
    color: $text-secondary;
    font-size: 14px;
  }
}
</style>
