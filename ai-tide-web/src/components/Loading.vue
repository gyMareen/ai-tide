<template>
  <div class="loading">
    <div v-if="text" class="loading-text">{{ text }}</div>
    <div class="loading-spinner">
      <div class="spinner"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  text?: string
}>()
</script>

<style scoped lang="scss">
@use '@/styles/variables.scss' as *;

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px;
}

.loading-text {
  color: $text-secondary;
  font-size: 16px;
  margin-bottom: 24px;
}

.loading-spinner {
  width: 48px;
  height: 48px;
}

.spinner {
  width: 100%;
  height: 100%;
  border: 3px solid rgba($accent-primary, 0.2);
  border-top-color: $accent-primary;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
