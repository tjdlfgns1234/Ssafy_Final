<template>
  <div class="modal-overlay" @click="closeModal">
    <div class="modal-container" @click.stop>
      <div class="modal-header">
        <h2>{{ isEditing ? 'Edit Destination' : 'Add Destination' }}</h2>
        <button class="close-button" @click="closeModal" aria-label="Close">✕</button>
      </div>
      
      <div class="modal-body">
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label for="name">Destination Name</label>
            <input 
              type="text" 
              id="name" 
              v-model="formData.name" 
              required 
              :disabled="isLoading"
            />
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label for="day">Day</label>
              <select 
                id="day" 
                v-model="formData.day" 
                required 
                :disabled="isLoading"
              >
                <option v-for="day in availableDays" :key="day" :value="day">
                  Day {{ day }}
                </option>
              </select>
            </div>
            
            <div class="form-group">
              <label for="duration">Duration (minutes)</label>
              <input 
                type="number" 
                id="duration" 
                v-model.number="formData.duration" 
                min="1" 
                :disabled="isLoading"
              />
            </div>
          </div>
          
          <div class="form-group">
            <label for="notes">Notes</label>
            <textarea 
              id="notes" 
              v-model="formData.notes" 
              rows="3" 
              :disabled="isLoading"
            ></textarea>
          </div>
          
          <div class="form-group">
            <label for="image">Image URL</label>
            <input 
              type="text" 
              id="image" 
              v-model="formData.image" 
              placeholder="https://example.com/image.jpg" 
              :disabled="isLoading"
            />
            <p class="form-help">Leave empty for a default image</p>
          </div>
          
          <div v-if="error" class="error-message">
            {{ error }}
          </div>
          
          <button 
            type="submit" 
            class="submit-button" 
            :disabled="isLoading || !isFormValid"
          >
            {{ isLoading ? 'Saving...' : (isEditing ? 'Update Destination' : 'Add Destination') }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';

const props = defineProps({
  planId: {
    type: [Number, String],
    required: true
  },
  destinationId: {
    type: [Number, String],
    default: null
  },
  totalDays: {
    type: Number,
    required: true
  },
  destinations: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['close', 'save']);

const isLoading = ref(false);
const error = ref('');

const formData = ref({
  name: '',
  day: 1,
  duration: 120, // Default 2 hours
  notes: '',
  image: ''
});

const isEditing = computed(() => !!props.destinationId);

const availableDays = computed(() => {
  const days = [];
  for (let i = 1; i <= props.totalDays; i++) {
    days.push(i);
  }
  return days;
});

const isFormValid = computed(() => {
  return (
    formData.value.name.trim() !== '' &&
    formData.value.day >= 1 &&
    formData.value.day <= props.totalDays &&
    formData.value.duration > 0
  );
});

function loadDestinationData() {
  if (props.destinationId) {
    const destination = props.destinations.find(d => d.id === props.destinationId);
    
    if (destination) {
      formData.value = {
        name: destination.name,
        day: destination.day,
        duration: destination.duration || 120,
        notes: destination.notes || '',
        image: destination.image || ''
      };
    }
  }
}

async function handleSubmit() {
  error.value = '';
  isLoading.value = true;
  
  try {
    // Prepare destination data
    const destinationData = {
      ...formData.value,
      // Set default image if none provided
      image: formData.value.image || 'https://via.placeholder.com/200x150?text=Destination'
    };
    
    if (isEditing.value) {
      // For editing, include the ID
      destinationData.id = props.destinationId;
    } else {
      // For new destinations, generate a new ID
      destinationData.id = Date.now();
    }
    
    // Emit save event with destination data
    emit('save', destinationData);
    closeModal();
  } catch (err) {
    error.value = err.message || 'Failed to save destination.';
  } finally {
    isLoading.value = false;
  }
}

function closeModal() {
  emit('close');
}

onMounted(() => {
  loadDestinationData();
});
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-container {
  background-color: white;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  animation: modal-appear 0.3s ease;
}

@keyframes modal-appear {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid var(--border-color);
  position: sticky;
  top: 0;
  background-color: white;
  z-index: 1;
}

.modal-header h2 {
  font-size: 1.5rem;
  color: var(--primary-color);
  margin: 0;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  color: var(--text-light);
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-row {
  display: flex;
  gap: 15px;
}

.form-row .form-group {
  flex: 1;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
  color: var(--text-color);
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 1rem;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(0, 91, 172, 0.2);
}

.form-help {
  font-size: 0.8rem;
  color: var(--text-light);
  margin-top: 5px;
}

.error-message {
  background-color: rgba(255, 84, 84, 0.1);
  color: var(--secondary-color);
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 20px;
  font-size: 0.9rem;
}

.submit-button {
  width: 100%;
  padding: 12px;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.submit-button:hover {
  background-color: #004c8e;
}

.submit-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>
