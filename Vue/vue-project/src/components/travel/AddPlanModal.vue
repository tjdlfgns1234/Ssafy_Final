<template>
  <div class="modal-overlay" v-if="!isPlaceSelectionActive" @click="closeModal">
    <div class="modal-container" @click.stop>
      <div class="modal-header">
        <h2>{{ isEditing ? '여행 계획 수정' : '새 여행계획 만들기' }}</h2>
        <button class="close-button" @click="closeModal" aria-label="Close">✕</button>
      </div>
      
      <div class="modal-body">
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label for="title">여행 제목</label>
            <input 
              type="text" 
              id="title" 
              v-model="formData.title" 
              required 
              :disabled="travelPlan.isLoading.value"
            />
          </div>
          
          <div class="form-group">
            <label for="description">여행 설명</label>
            <textarea 
              id="description" 
              v-model="formData.description" 
              rows="3" 
              required 
              :disabled="travelPlan.isLoading.value"
            ></textarea>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label for="startDate">시작일</label>
              <input 
                type="date" 
                id="startDate" 
                v-model="formData.startDate" 
                required 
                :disabled="travelPlan.isLoading.value"
              />
            </div>
            
            <div class="form-group">
              <label for="endDate">종료일</label>
              <input 
                type="date" 
                id="endDate" 
                v-model="formData.endDate" 
                required 
                :min="formData.startDate"
                :disabled="travelPlan.isLoading.value"
              />
            </div>
          </div>

          <!-- Location fields -->
          <div class="form-group location-fields">
            <div class="location-field">
              <label for="placeName">장소 이름</label>
              <div class="location-input-group">
                <input 
                  type="text" 
                  id="placeName"
                  v-model="formData.start_location_title" 
                  placeholder="장소를 선택하세요" 
                  readonly 
                  :disabled="travelPlan.isLoading.value"
                />
                <button 
                  type="button" 
                  class="location-button" 
                  @click="selectLocation" 
                  :disabled="travelPlan.isLoading.value"
                >
                  시작 지점 추가
                </button>
              </div>
            </div>
          </div>
          
          <div class="form-group">
            <label for="image">커버 이미지 URL</label>
            <input 
              type="text" 
              id="image" 
              v-model="formData.image" 
              placeholder="https://example.com/image.jpg" 
              :disabled="travelPlan.isLoading.value"
            />
            <p class="form-help">비워두면 기본 이미지가 사용됩니다</p>
          </div>
          
          <div v-if="error" class="error-message">
            {{ error }}
          </div>
          
          <div class="form-actions">
            <button 
              type="button" 
              class="cancel-button" 
              @click="closeModal" 
              :disabled="travelPlan.isLoading.value"
            >
              취소
            </button>
            <button 
              type="submit" 
              class="submit-button" 
              :disabled="travelPlan.isLoading.value || !isFormValid"
            >
              {{ travelPlan.isLoading.value ? '저장 중...' : '저장' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
  
  <!-- Place Selection Sidebar -->
  <PlaceSelectionSidebar 
    :isActive="isPlaceSelectionActive"
    @close="handlePlaceSelectionClose"
    @select-place="handlePlaceSelected"
  />
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useTravelPlan } from '../../composables/useTravelPlan';
import PlaceSelectionSidebar from './PlaceSelectionSidebar.vue';

const props = defineProps({
  planId: {
    type: [Number, String],
    default: null
  }
});

const emit = defineEmits(['close', 'created', 'updated']);

const travelPlan = useTravelPlan();
const error = ref('');
const isPlaceSelectionActive = ref(false);

const formData = ref({
  title: '',
  description: '',
  startDate: new Date().toISOString().split('T')[0], // Today's date in YYYY-MM-DD format
  endDate: new Date().toISOString().split('T')[0],
  image: '',
  // Location fields
  start_location_title: '',
  place_id: null,
  latitude_start: null,
  longitude_start: null,
  end_location_title: '',
  latitude_end: null,
  longitude_end: null
});

const isEditing = computed(() => !!props.planId);

const isFormValid = computed(() => {
  return (
    formData.value.title.trim() !== '' &&
    formData.value.description.trim() !== '' &&
    formData.value.startDate &&
    formData.value.endDate &&
    new Date(formData.value.startDate) <= new Date(formData.value.endDate)
  );
});

async function loadPlanData() {
  if (props.planId) {
    try {
      const plan = await travelPlan.fetchTravelPlanById(props.planId);
      
      if (plan) {
        formData.value = {
          title: plan.title,
          description: plan.description,
          startDate: plan.startDate,
          endDate: plan.endDate,
          image: plan.image,
          // Load location data if available
          start_location_title: plan.start_location_title || '',
          place_id: plan.place_id || null,
          latitude_start: plan.latitude_start || null,
          longitude_start: plan.longitude_start || null,
          end_location_title: plan.end_location_title || '',
          latitude_end: plan.latitude_end || null,
          longitude_end: plan.longitude_end || null
        };
      }
    } catch (err) {
      error.value = '여행 계획 데이터를 불러오는데 실패했습니다.';
    }
  }
}

async function handleSubmit() {
  error.value = '';
  
  try {
    // If location is empty, try to get current location
    if (!formData.value.start_location_title) {
      await getCurrentLocation();
    }
    
    if (isEditing.value) {
      // Update existing plan
      const updatedPlan = await travelPlan.updateTravelPlan(props.planId, formData.value);
      emit('updated', updatedPlan);
    } else {
      // Create new plan
      const newPlan = await travelPlan.createTravelPlan({
        ...formData.value,
        // Set default image if none provided
        image: formData.value.image || 'https://via.placeholder.com/400x300?text=Travel+Plan'
      });
      emit('created', newPlan);
    }
    
    closeModal();
  } catch (err) {
    error.value = err.message || '여행 계획을 저장하는데 실패했습니다.';
  }
}

function closeModal() {
  emit('close');
}

// Function to select location
function selectLocation() {
  // Show the place selection sidebar
  isPlaceSelectionActive.value = true;
}

// Handle when the place selection sidebar is closed without selecting a place
function handlePlaceSelectionClose() {
  isPlaceSelectionActive.value = false;
}

// Handle the selected place from the sidebar
function handlePlaceSelected(place) {
  formData.value.start_location_title = place.name;
  formData.value.place_id = place.place_id;
  formData.value.latitude_start = place.latitude;
  formData.value.longitude_start = place.longitude;
  
  // Set end location to same as start location
  formData.value.end_location_title = place.name;
  formData.value.latitude_end = place.latitude;
  formData.value.longitude_end = place.longitude;
  
  // Hide the place selection sidebar
  isPlaceSelectionActive.value = false;
}

// Function to get user's current location
async function getCurrentLocation() {
  return new Promise((resolve, reject) => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          // If location is empty, set to current location
          if (!formData.value.start_location_title) {
            formData.value.latitude_start = position.coords.latitude;
            formData.value.longitude_start = position.coords.longitude;
            formData.value.start_location_title = '현재 위치';
            
            formData.value.latitude_end = position.coords.latitude;
            formData.value.longitude_end = position.coords.longitude;
            formData.value.end_location_title = '현재 위치';
          }
          resolve();
        },
        (error) => {
          console.error('Error getting current location:', error);
          reject(error);
        }
      );
    } else {
      reject(new Error('Geolocation is not supported by this browser.'));
    }
  });
}

onMounted(() => {
  loadPlanData();
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
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 1rem;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.form-group input:focus,
.form-group textarea:focus {
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

.form-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.cancel-button {
  padding: 12px 20px;
  background-color: #f5f5f5;
  color: var(--text-color);
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.cancel-button:hover {
  background-color: #e5e5e5;
}

.submit-button {
  padding: 12px 20px;
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

.submit-button:disabled,
.cancel-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.location-fields {
  margin-top: 10px;
}

.location-field {
  margin-bottom: 10px;
}

.location-input-group {
  display: flex;
  gap: 10px;
}

.location-input-group input {
  flex: 1;
  background-color: #f5f5f5;
}

.location-button {
  padding: 10px 15px;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: background-color 0.2s ease;
  white-space: nowrap;
}

.location-button:hover {
  background-color: #004c8e;
}

.location-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>
