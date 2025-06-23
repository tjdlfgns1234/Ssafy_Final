<template>
  <div class="day-total-time">
    <div v-if="isCalculating" class="calculating">
      <span class="loading-spinner">⏳</span>
      <span>계산 중...</span>
    </div>
    <div v-else-if="totalDayTime" class="total-time">
      {{ totalDayTime.formatted }}
    </div>
    <div v-else class="no-time">
      N/A
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useTravelPlan } from '../../composables/useTravelPlan.js'

const props = defineProps({
  planId: {
    type: [Number, String],
    required: true
  },
  day: {
    type: [Number, String],
    required: true
  }
})

const { getTotalDayTime, calculateTravelTimesForPlan, isCalculatingTravelTimes } = useTravelPlan()

const totalDayTime = ref(null)
const isCalculating = computed(() => isCalculatingTravelTimes.value)

const updateTotalTime = () => {
  const dayTime = getTotalDayTime(props.planId, props.day)
  totalDayTime.value = dayTime
}

// Watch for changes in plan or day
watch([() => props.planId, () => props.day], () => {
  updateTotalTime()
}, { immediate: true })

// Calculate travel times when component mounts if not already calculated
onMounted(async () => {
  if (!totalDayTime.value) {
    await calculateTravelTimesForPlan(props.planId)
    updateTotalTime()
  }
})
</script>

<style scoped>
.day-total-time {
  font-weight: 500;
  font-size: 1rem;
}

.calculating {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #666;
}

.loading-spinner {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.total-time {
  color: #28a745;
  font-weight: 600;
}

.no-time {
  color: #dc3545;
}
</style>
