<template>
  <div class="route-segment" :class="{ 'is-dragging': isDragging }">
    <div class="segment-handle" @mousedown="startDrag">
      ⋮⋮
    </div>
    <div class="segment-content">
      <div class="segment-header">
        <div class="segment-title">
          <span class="segment-number">{{ index + 1 }}</span>
          <h3>{{ destination.name }}</h3>
        </div>
        <div class="segment-actions">
          <button 
            class="edit-button" 
            @click="$emit('edit', destination.id)"
            title="Edit"
          >
            ✏️
          </button>
          <button 
            class="delete-button" 
            @click="$emit('delete', destination.id)"
            title="Delete"
          >
            🗑️
          </button>
        </div>
      </div>
      
      <div class="segment-details">
        <div class="segment-image">
          <img :src="destination.image" :alt="destination.name">
        </div>
        <div class="segment-info">
          <div class="segment-duration">
            <span class="info-label">Duration:</span>
            <span class="info-value">{{ formatDuration(destination.duration) }}</span>
          </div>
          <div class="segment-notes" v-if="destination.notes">
            <span class="info-label">Notes:</span>
            <span class="info-value">{{ destination.notes }}</span>
          </div>
        </div>
      </div>

      <!-- Travel time to next destination -->
      <div v-if="nextDestination" class="travel-time-section">
        <div class="travel-time-label">다음 목적지까지</div>
        <TravelTimeDisplay
          :plan-id="planId"
          :day="day"
          :from-id="destination.id"
          :to-id="nextDestination.id"
          :travel-time-data="getTravelTimeForSegment(planId, day, destination.id, nextDestination.id)"
          :is-calculating="isCalculatingTravelTimes"
          :show-segment-time="true"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import TravelTimeDisplay from './TravelTimeDisplay.vue';
import { useTravelPlan } from '../../composables/useTravelPlan.js';

const { getTravelTimeForSegment, formatTravelTime, isCalculatingTravelTimes } = useTravelPlan();

const props = defineProps({
  destination: {
    type: Object,
    required: true
  },
  index: {
    type: Number,
    required: true
  },
  planId: {
    type: [Number, String],
    required: true
  },
  day: {
    type: [Number, String],
    required: true
  },
  nextDestination: {
    type: Object,
    default: null
  },
  previousDestination: {
    type: Object,
    default: null
  }
});

const emit = defineEmits(['edit', 'delete', 'drag-start', 'drag-end']);

const isDragging = ref(false);

function formatDuration(minutes) {
  if (!minutes) return 'N/A';
  
  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;
  
  if (hours === 0) {
    return `${mins} min`;
  } else if (mins === 0) {
    return `${hours} hr`;
  } else {
    return `${hours} hr ${mins} min`;
  }
}

let handleDrag = null;
let endDrag = null;

function startDrag(event) {
  isDragging.value = true;
  emit('drag-start', {
    destinationId: props.destination.id,
    initialY: event.clientY,
    element: event.target.closest('.route-segment')
  });

  handleDrag = (event) => {
    // The actual drag logic will be handled by the parent component
  };

  endDrag = () => {
    isDragging.value = false;
    emit('drag-end');

    document.removeEventListener('mousemove', handleDrag);
    document.removeEventListener('mouseup', endDrag);
    handleDrag = null;
    endDrag = null;
  };
  
  document.addEventListener('mousemove', handleDrag);
  document.addEventListener('mouseup', endDrag);
}
</script>

<style scoped>
.route-segment {
  display: flex;
  background-color: white;
  border-radius: 8px;
  box-shadow: var(--shadow);
  margin-bottom: 15px;
  overflow: hidden;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.route-segment:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.route-segment.is-dragging {
  opacity: 0.8;
  transform: scale(1.02);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
  z-index: 10;
}

.segment-handle {
  width: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--background-light);
  cursor: grab;
  font-size: 1.2rem;
  color: var(--text-light);
}

.segment-handle:active {
  cursor: grabbing;
}

.segment-content {
  flex: 1;
  padding: 15px;
}

.segment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.segment-title {
  display: flex;
  align-items: center;
}

.segment-number {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: var(--primary-color);
  color: white;
  font-size: 0.8rem;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
}

.segment-title h3 {
  font-size: 1.1rem;
  margin: 0;
  color: var(--primary-color);
}

.segment-actions {
  display: flex;
  gap: 5px;
}

.edit-button,
.delete-button {
  background: none;
  border: none;
  font-size: 1rem;
  cursor: pointer;
  padding: 5px;
  border-radius: 4px;
  transition: background-color 0.2s ease;
}

.edit-button:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.delete-button:hover {
  background-color: rgba(255, 84, 84, 0.1);
}

.segment-details {
  display: flex;
  gap: 15px;
}

.segment-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  overflow: hidden;
  flex-shrink: 0;
}

.segment-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.segment-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.info-label {
  font-weight: bold;
  color: var(--text-light);
  font-size: 0.9rem;
}

.info-value {
  font-size: 0.9rem;
}

.segment-notes {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

@media (max-width: 768px) {
  .segment-details {
    flex-direction: column;
  }
  
  .segment-image {
    width: 100%;
    height: 120px;
  }
}

.travel-time-section {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

.travel-time-label {
  font-size: 0.8rem;
  color: var(--text-light);
  margin-bottom: 5px;
}
</style>
