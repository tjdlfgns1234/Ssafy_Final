<template>
  <div class="rating" :class="{ 'interactive': interactive }">
    <div 
      v-for="star in 5" 
      :key="star"
      class="star"
      :class="{ 
        'filled': star <= roundedValue,
        'half-filled': star === Math.ceil(value) && !Number.isInteger(value)
      }"
      @click="interactive ? updateRating(star) : null"
      @mouseover="interactive ? hoverRating = star : null"
      @mouseleave="interactive ? hoverRating = 0 : null"
    >
      ★
    </div>
    <span v-if="showValue" class="rating-value">{{ value.toFixed(1) }}</span>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

const props = defineProps({
  value: {
    type: Number,
    default: 0
  },
  showValue: {
    type: Boolean,
    default: false
  },
  interactive: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['update']);

const hoverRating = ref(0);

const roundedValue = computed(() => {
  if (hoverRating.value > 0 && props.interactive) {
    return hoverRating.value;
  }
  return Math.round(props.value * 2) / 2;
});

function updateRating(rating) {
  if (props.interactive) {
    emit('update', rating);
  }
}
</script>

<style scoped>
.rating {
  display: flex;
  align-items: center;
}

.star {
  font-size: 1.2rem;
  color: #ddd;
  margin-right: 2px;
}

.star.filled {
  color: #FFD700;
}

.star.half-filled {
  position: relative;
  color: #ddd;
}

.star.half-filled::before {
  content: '★';
  position: absolute;
  color: #FFD700;
  width: 50%;
  overflow: hidden;
}

.rating-value {
  margin-left: 5px;
  font-size: 0.9rem;
  color: var(--text-light);
}

.interactive .star {
  cursor: pointer;
}

.interactive .star:hover {
  transform: scale(1.2);
}
</style>
