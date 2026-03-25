<template>
  <div class="tag-cloud">
    <h3 v-if="title" class="tag-cloud-title">{{ title }}</h3>

    <div class="tag-cloud-list">
      <router-link
        v-for="tag in sortedTags"
        :key="tag.id"
        :to="to ? to(tag.id) : ''"
        class="tag-cloud-item"
        :class="getTagSizeClass(tag)"
        @click.prevent="handleClick(tag)"
      >
        #{{ tag.name }}
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { Tag } from '@/types/content'

const props = withDefaults(defineProps<{
  tags: Tag[]
  title?: string
  to?: (id: string) => string
  maxTags?: number
}>(), {
  maxTags: 50,
})

const emit = defineEmits<{
  click: [tag: Tag]
}>()

const sortedTags = computed(() => {
  // Sort by use count
  return [...props.tags]
    .sort((a, b) => b.useCount - a.useCount)
    .slice(0, props.maxTags)
})

function getTagSizeClass(tag: Tag) {
  const maxCount = sortedTags.value[0]?.useCount || 1
  const ratio = tag.useCount / maxCount

  if (ratio > 0.8) return 'size-large'
  if (ratio > 0.6) return 'size-medium-large'
  if (ratio > 0.4) return 'size-medium'
  if (ratio > 0.2) return 'size-medium-small'
  return 'size-small'
}

function handleClick(tag: Tag) {
  emit('click', tag)
}
</script>

<style scoped lang="scss">
@use '@/styles/variables.scss' as *;

.tag-cloud {
  .tag-cloud-title {
    font-size: 18px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 20px;
  }
}

.tag-cloud-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.tag-cloud-item {
  padding: 6px 16px;
  background: $bg-secondary;
  border: 1px solid $border-color;
  border-radius: 20px;
  color: $text-secondary;
  text-decoration: none;
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    border-color: $accent-primary;
    color: $accent-primary;
    transform: translateY(-2px);
  }

  &.size-small {
    font-size: 12px;
  }

  &.size-medium-small {
    font-size: 13px;
  }

  &.size-medium {
    font-size: 14px;
  }

  &.size-medium-large {
    font-size: 15px;
    font-weight: 500;
  }

  &.size-large {
    font-size: 16px;
    font-weight: 600;
  }
}
</style>
