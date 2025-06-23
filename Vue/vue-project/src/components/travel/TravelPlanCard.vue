<template>
  <div class="travel-plan-card" @click="navigateToDetails">
    <div class="card-image">
      <img :src="plan.image" :alt="plan.title">
      <div class="card-dates">
        {{ formatDateRange(plan.startDate, plan.endDate) }}
      </div>
    </div>
    <div class="card-content">
      <h3 class="card-title">{{ plan.title }}</h3>
      <p class="card-description">{{ plan.description }}</p>
      <div class="card-destinations">
        <div class="destinations-count">
          {{ plan.destinations.length }} destinations
        </div>
        <div class="destinations-preview">
          <span 
            v-for="(destination, index) in previewDestinations" 
            :key="destination.id"
            class="destination-dot"
            :title="destination.name"
          >
            {{ index + 1 }}
          </span>
          <span v-if="plan.destinations.length > 3" class="more-destinations">
            +{{ plan.destinations.length - 3 }}
          </span>
        </div>
      </div>
    </div>
    <div class="card-actions">
      <button class="edit-button" @click.stop="$emit('edit', plan.id)">
        Edit
      </button>
      <button class="delete-button" @click.stop="$emit('delete', plan.id)">
        Delete
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';

const props = defineProps({
  plan: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['edit', 'delete']);

const router = useRouter();

const previewDestinations = computed(() => {
  return props.plan.destinations.slice(0, 3);
});

function formatDateRange(startDate, endDate) {
  const start = new Date(startDate);
  const end = new Date(endDate);
  
  const options = { month: 'short', day: 'numeric' };
  
  if (startDate === endDate) {
    return start.toLocaleDateString('en-US', options);
  }
  
  return `${start.toLocaleDateString('en-US', options)} - ${end.toLocaleDateString('en-US', options)}`;
}

function navigateToDetails() {
  router.push({ name: 'TravelPlanDetail', params: { id: props.plan.id } });
}
</script>

<style scoped>
.travel-plan-card {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: var(--shadow);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
  position: relative;
}

.travel-plan-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.card-image {
  position: relative;
  height: 160px;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-dates {
  position: absolute;
  bottom: 0;
  left: 0;
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 5px 10px;
  font-size: 0.8rem;
  border-top-right-radius: 4px;
}

.card-content {
  padding: 15px;
}

.card-title {
  font-size: 1.2rem;
  margin-bottom: 8px;
  color: var(--primary-color);
}

.card-description {
  font-size: 0.9rem;
  color: var(--text-light);
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-destinations {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.destinations-count {
  font-size: 0.8rem;
  color: var(--text-light);
}

.destinations-preview {
  display: flex;
  align-items: center;
}

.destination-dot {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: var(--primary-color);
  color: white;
  font-size: 0.8rem;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 5px;
}

.more-destinations {
  font-size: 0.8rem;
  color: var(--text-light);
  margin-left: 5px;
}

.card-actions {
  display: flex;
  border-top: 1px solid var(--border-color);
}

.edit-button,
.delete-button {
  flex: 1;
  padding: 10px;
  border: none;
  background: none;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.2s ease;
}

.edit-button {
  color: var(--primary-color);
  border-right: 1px solid var(--border-color);
}

.edit-button:hover {
  background-color: rgba(0, 91, 172, 0.1);
}

.delete-button {
  color: var(--secondary-color);
}

.delete-button:hover {
  background-color: rgba(255, 84, 84, 0.1);
}
</style>
