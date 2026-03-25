<template>
  <div class="rating-stars">
    <span
      v-for="n in 5"
      :key="n"
      class="star"
      :class="{
        filled: n <= rating,
        half: allowHalf && n - 0.5 <= rating && n > rating
      }"
      @click="handleRate(n)"
    >
      ★
    </span>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  rating: number
  allowHalf?: boolean
  readonly?: boolean
}>()

const emit = defineEmits<{
  change: [rating: number]
}>()

function handleRate(star: number) {
  if (props.readonly) return

  if (props.allowHalf) {
    // 实现半星逻辑
    const currentRating = props.rating
    if (currentRating >= star - 0.5 && currentRating < star) {
      emit('change', star - 0.5)
    } else {
      emit('change', star)
    }
  } else {
    emit('change', star)
  }
}
</script>

<style scoped lang="scss">
@use '@/styles/variables.scss' as *;

.rating-stars {
  display: flex;
  gap: 2px;
  font-size: 20px;
  color: $text-tertiary;
  cursor: default;

  &:not(.readonly) {
    cursor: pointer;
  }
}

.star {
  transition: color 0.2s ease;
  position: relative;
  display: inline-block;

  &.filled {
    color: #fbbf24;
  }

  &.half::before {
    content: '★';
    position: absolute;
    left: 0;
    overflow: hidden;
    width: 50%;
    color: #fbbf24;
  }

  &:hover {
    transform: scale(1.1);
  }
}
</style>
