<template>
  <div class="travel-time-display">
    <div v-if="isCalculating" class="calculating">
      <div class="loading-spinner-small"></div>
      <span>계산 중...</span>
    </div>
    <div v-else-if="!travelTimeData" class="no-data">
      N/A
    </div>
    <div v-else-if="travelTimeData.error" class="error">
      <span>{{ travelTimeData.error }}</span>
    </div>
    <div v-else class="time-info">
      <div class="duration">
        <span>{{ formatDuration(travelTimeData.duration) }}</span>
      </div>
      <div v-if="showSegmentTime && travelTimeData.distance" class="distance">
        <span>{{ formatDistance(travelTimeData.distance) }}</span>
      </div>
      <div v-if="showArrivalTime && travelTimeData.arrivalTime" class="arrival">
        <span>도착: {{ formatTime(travelTimeData.arrivalTime) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  planId: {
    type: [Number, String],
    required: true
  },
  day: {
    type: [Number, String],
    required: true
  },
  fromId: {
    type: [Number, String],
    required: true
  },
  toId: {
    type: [Number, String],
    required: true
  },
  travelTimeData: {
    type: Object,
    default: null
  },
  isCalculating: {
    type: Boolean,
    default: false
  },
  showSegmentTime: {
    type: Boolean,
    default: false
  },
  showArrivalTime: {
    type: Boolean,
    default: false
  }
});

// Format duration in seconds to a human-readable string
function formatDuration(seconds) {
  if (!seconds) return 'N/A';
  
  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  
  if (hours === 0) {
    return `${minutes}분`;
  } else {
    return `${hours}시간 ${minutes}분`;
  }
}

// Format distance in meters to a human-readable string
function formatDistance(meters) {
  if (!meters) return '';
  
  if (meters < 1000) {
    return `${meters}m`;
  } else {
    const km = (meters / 1000).toFixed(1);
    return `${km}km`;
  }
}

// Format time (HH:MM format)
function formatTime(timeString) {
  if (!timeString) return '';
  
  const date = new Date(timeString);
  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');
  
  return `${hours}:${minutes}`;
}
</script>

<style scoped>
.travel-time-display {
  display: flex;
  align-items: center;
  font-size: 0.9rem;
}

.calculating {
  display: flex;
  align-items: center;
  color: var(--text-light);
}

.loading-spinner-small {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(0, 91, 172, 0.1);
  border-left-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: 8px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.no-data {
  color: var(--text-light);
}

.error {
  color: var(--secondary-color);
}

.time-info {
  display: flex;
  flex-direction: column;
}

.duration {
  font-weight: bold;
  color: var(--primary-color);
}

.distance {
  font-size: 0.8rem;
  color: var(--text-light);
}

.arrival {
  font-size: 0.8rem;
  color: var(--text-light);
  margin-top: 2px;
}
</style>
