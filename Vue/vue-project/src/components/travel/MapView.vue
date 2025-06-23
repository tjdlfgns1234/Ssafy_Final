<template>
  <div class="map-container">
    <div class="map-placeholder">
      <div class="map-overlay">
        <div class="map-message">
          <h3>Interactive Map</h3>
          <p>This is a placeholder for an interactive map that would show the travel route.</p>
          <p>In a real implementation, this would integrate with a mapping API like Google Maps, Kakao Maps, or Naver Maps.</p>
        </div>
      </div>
      <div class="map-markers">
        <div 
          v-for="(destination, index) in destinations" 
          :key="destination.id"
          class="map-marker"
          :style="getMarkerPosition(index)"
        >
          <div class="marker-number">{{ index + 1 }}</div>
          <div class="marker-tooltip">
            {{ destination.name }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  destinations: {
    type: Array,
    default: () => []
  }
});

// Generate pseudo-random positions for markers
function getMarkerPosition(index) {
  // Create a deterministic but seemingly random distribution
  const baseX = 20 + (index * 37) % 60;
  const baseY = 30 + (index * 23) % 40;
  
  return {
    left: `${baseX}%`,
    top: `${baseY}%`
  };
}
</script>

<style scoped>
.map-container {
  width: 100%;
  height: 300px;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 20px;
}

.map-placeholder {
  width: 100%;
  height: 100%;
  background-color: #e9eef2;
  background-image: url('https://via.placeholder.com/1200x600?text=Map+Background');
  background-size: cover;
  background-position: center;
  position: relative;
}

.map-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  pointer-events: none;
}

.map-message {
  background-color: rgba(255, 255, 255, 0.9);
  padding: 15px;
  border-radius: 8px;
  text-align: center;
  max-width: 80%;
}

.map-message h3 {
  margin-top: 0;
  color: var(--primary-color);
}

.map-message p {
  margin: 5px 0;
  font-size: 0.9rem;
  color: var(--text-light);
}

.map-markers {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.map-marker {
  position: absolute;
  transform: translate(-50%, -50%);
}

.marker-number {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background-color: var(--primary-color);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  position: relative;
  z-index: 1;
  cursor: pointer;
}

.marker-tooltip {
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%);
  background-color: white;
  padding: 5px 10px;
  border-radius: 4px;
  font-size: 0.8rem;
  white-space: nowrap;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  opacity: 0;
  transition: opacity 0.2s ease;
  pointer-events: none;
  margin-bottom: 5px;
}

.map-marker:hover .marker-tooltip {
  opacity: 1;
}
</style>
