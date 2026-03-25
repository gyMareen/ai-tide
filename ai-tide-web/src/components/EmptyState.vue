<template>
  <div class="empty-state">
    <div v-if="icon" class="empty-icon">{{ icon }}</div>
    <div v-else class="empty-default-icon">
      <el-icon :size="60"><Box /></el-icon>
    </div>

    <h3 v-if="title" class="empty-title">{{ title }}</h3>
    <p v-else class="empty-desc">{{ description }}</p>

    <p v-if="description && title" class="empty-desc">{{ description }}</p>

    <el-button
      v-if="action"
      type="primary"
      @click="$emit('action')"
    >
      {{ action }}
    </el-button>
  </div>
</template>

<script setup lang="ts">
import { Box } from '@element-plus/icons-vue'

defineProps<{
  icon?: string
  title?: string
  description?: string
  action?: string
}>()

defineEmits<{
  action: []
}>()
</script>

<style scoped lang="scss">
@use '@/styles/variables.scss' as *;

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 64px 32px;
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 24px;
}

.empty-default-icon {
  color: $text-tertiary;
  margin-bottom: 24px;
}

.empty-title {
  font-size: 20px;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 12px;
}

.empty-desc {
  font-size: 16px;
  color: $text-secondary;
  margin-bottom: 32px;
  max-width: 400px;
  line-height: 1.6;
}
</style>
