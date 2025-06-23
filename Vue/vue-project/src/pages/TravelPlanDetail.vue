<template>
  <div class="travel-plan-detail">
    <div class="detail-header">
      <BackButton />
      <h1>{{ plan ? plan.title : 'Loading...' }}</h1>
      <div class="header-actions">
        <button 
          class="optimize-button" 
          @click="optimizeRoute"
          :disabled="isOptimizing || !hasDestinations"
        >
          <span v-if="isOptimizing">최적화 중...</span>
          <span v-else>알고리즘 추천</span>
        </button>
      </div>
    </div>

    <div v-if="isLoading" class="loading-container">
      <LoadingSpinner />
      <p>여행 계획을 불러오는 중...</p>
    </div>

    <div v-else-if="!plan" class="error-container">
      <div class="error-icon">⚠️</div>
      <h2>여행 계획을 찾을 수 없습니다</h2>
      <p>요청하신 여행 계획이 존재하지 않거나 액세스 권한이 없습니다.</p>
      <router-link to="/my-travel-plans" class="back-link">
        내 여행 계획으로 돌아가기
      </router-link>
    </div>

    <div v-else class="plan-content">
      <div class="plan-info">
        <div class="info-card">
          <div class="info-row">
            <div class="info-label">여행 기간</div>
            <div class="info-value">{{ formatDateRange(plan.startDate, plan.endDate) }}</div>
          </div>
          <div class="info-row">
            <div class="info-label">목적지 수</div>
            <div class="info-value">{{ plan.destinations.length }}</div>
          </div>
          <div class="info-row">
            <div class="info-label">출발 지점</div>
            <div class="info-value">{{ plan.start_location_title || '설정되지 않음' }}</div>
          </div>
          <div class="info-row">
            <div class="info-label">도착 지점</div>
            <div class="info-value">{{ plan.end_location_title || '설정되지 않음' }}</div>
          </div>
        </div>

        <div class="description-card">
          <h3>여행 설명</h3>
          <p>{{ plan.description }}</p>
        </div>
      </div>

      <div class="plan-tabs">
        <Tabs :tabs="tabs" v-model="activeTab" />
      </div>

      <div class="tab-content">
        <!-- 일정 탭 -->
        <div v-if="activeTab === 'itinerary'" class="itinerary-content">
          <div v-if="!hasDestinations" class="empty-itinerary">
            <div class="empty-icon">🗺️</div>
            <h2>아직 목적지가 없습니다</h2>
            <p>여행 계획에 목적지를 추가하여 일정을 만들어보세요.</p>
            <button class="add-destination-button" @click="openPlaceSelectionSidebar">
              목적지 추가하기
            </button>
          </div>

          <div v-else class="itinerary-days">
            <div 
              v-for="day in dayCount" 
              :key="day"
              class="day-container"
            >
              <div class="day-header">
                <h3>Day {{ day }}</h3>
                <div class="day-actions">
                  <button class="add-button" @click="openPlaceSelectionSidebar(day)">
                    + 목적지 추가
                  </button>
                </div>
              </div>

              <div class="day-total-time" v-if="getDayDestinations(day).length > 0">
                <div class="time-label">총 이동 시간:</div>
                <div class="time-value">
                  <span v-if="isCalculatingTravelTimes">계산 중...</span>
                  <span v-else>{{ formatTotalTravelTime(plan.id, day) }}</span>
                </div>
              </div>

              <div 
                v-if="getDayDestinations(day).length === 0" 
                class="empty-day"
              >
                <p>이 날짜에 목적지가 없습니다. 목적지를 추가해보세요.</p>
              </div>

              <div 
                v-else
                class="day-destinations"
                @dragover.prevent
                @drop="onDrop($event, day)"
              >
                <RouteSegment
                  v-for="(destination, index) in getDayDestinations(day)"
                  :key="destination.id"
                  :destination="destination"
                  :index="index"
                  :plan-id="plan.id"
                  :day="day"
                  :next-destination="getNextDestination(day, index)"
                  :previous-destination="getPreviousDestination(day, index)"
                  @edit="openEditDestinationModal"
                  @delete="confirmDeleteDestination"
                  @drag-start="onDragStart"
                  @drag-end="onDragEnd"
                />
              </div>
            </div>
          </div>
        </div>

        <!-- 지도 탭 -->
        <div v-if="activeTab === 'map'" class="map-content">
          <MapView 
            :destinations="plan.destinations"
            :start-location="startLocation"
            :end-location="endLocation"
          />
        </div>
      </div>
    </div>

    <!-- 사이드바 및 모달 -->
    <PlaceSelectionSidebar
      v-if="isPlaceSelectionSidebarOpen"
      :is-open="isPlaceSelectionSidebarOpen"
      :selected-day="selectedDay"
      @close="closePlaceSelectionSidebar"
      @select-place="addDestination"
    />

    <Modal
      v-if="isEditDestinationModalOpen"
      title="목적지 정보 수정"
      @close="closeEditDestinationModal"
    >
      <div class="edit-destination-form">
        <!-- 목적지 편집 폼 내용 -->
      </div>
    </Modal>

    <Modal
      v-if="isDeleteConfirmationOpen"
      title="목적지 삭제"
      @close="closeDeleteConfirmation"
    >
      <div class="delete-confirmation">
        <p>정말로 이 목적지를 삭제하시겠습니까?</p>
        <div class="modal-actions">
          <button class="cancel-button" @click="closeDeleteConfirmation">취소</button>
          <button class="delete-button" @click="deleteDestination">삭제</button>
        </div>
      </div>
    </Modal>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useTravelPlan } from '../composables/useTravelPlan';
import { useModal } from '../composables/useModal';
import BackButton from '../components/common/BackButton.vue';
import LoadingSpinner from '../components/common/LoadingSpinner.vue';
import Tabs from '../components/common/Tabs.vue';
import RouteSegment from '../components/travel/RouteSegment.vue';
import MapView from '../components/travel/MapView.vue';
import PlaceSelectionSidebar from '../components/travel/PlaceSelectionSidebar.vue';
import Modal from '../components/common/Modal.vue';
import recommendationService from '../services/recommendationService';

export default {
  components: {
    BackButton,
    LoadingSpinner,
    Tabs,
    RouteSegment,
    MapView,
    PlaceSelectionSidebar,
    Modal
  },
  setup() {
    // Router and route
    const route = useRoute();
    const router = useRouter();

    // Composables
    const { 
      fetchTravelPlanById, 
      currentPlan, 
      isLoading, 
      error,
      removeDestinationFromPlan,
      reorderDestinations,
      calculateTravelTimesForPlan,
      getTotalTravelTimeForDay,
      formatTravelTime,
      isCalculatingTravelTimes
    } = useTravelPlan();

    const { openModal, closeModal } = useModal();

    // Local state
    const plan = computed(() => currentPlan.value);
    const activeTab = ref('itinerary');
    const tabs = [
      { id: 'itinerary', label: '일정' },
      { id: 'map', label: '지도' }
    ];

    // Sidebar and modal state
    const isPlaceSelectionSidebarOpen = ref(false);
    const selectedDay = ref(1);
    const isEditDestinationModalOpen = ref(false);
    const selectedDestination = ref(null);
    const isDeleteConfirmationOpen = ref(false);
    const destinationToDelete = ref(null);

    // Drag and drop state
    const draggedDestination = ref(null);
    const draggedElement = ref(null);
    const originalPosition = ref(null);

    // Optimization state
    const isOptimizing = ref(false);

    // Computed properties
    const dayCount = computed(() => {
      if (!plan.value) return 0;
      
      const startDate = new Date(plan.value.startDate);
      const endDate = new Date(plan.value.endDate);
      
      const diffTime = Math.abs(endDate - startDate);
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      
      return diffDays + 1; // Include both start and end days
    });

    const hasDestinations = computed(() => {
      return plan.value && plan.value.destinations && plan.value.destinations.length > 0;
    });

    const startLocation = computed(() => {
      if (!plan.value || !plan.value.latitude_start || !plan.value.longitude_start) return null;
      
      return {
        lat: plan.value.latitude_start,
        lng: plan.value.longitude_start,
        name: plan.value.start_location_title || '출발 지점'
      };
    });

    const endLocation = computed(() => {
      if (!plan.value || !plan.value.latitude_end || !plan.value.longitude_end) return null;
      
      return {
        lat: plan.value.latitude_end,
        lng: plan.value.longitude_end,
        name: plan.value.end_location_title || '도착 지점'
      };
    });

    // Methods
    function getDayDestinations(day) {
      if (!plan.value || !plan.value.destinations) return [];
      
      return plan.value.destinations
        .filter(dest => dest.day === day)
        .sort((a, b) => a.order - b.order);
    }

    function getNextDestination(day, index) {
      const destinations = getDayDestinations(day);
      return index < destinations.length - 1 ? destinations[index + 1] : null;
    }

    function getPreviousDestination(day, index) {
      const destinations = getDayDestinations(day);
      return index > 0 ? destinations[index - 1] : null;
    }

    function formatDateRange(startDate, endDate) {
      if (!startDate || !endDate) return '';
      
      const start = new Date(startDate);
      const end = new Date(endDate);
      
      const options = { year: 'numeric', month: 'long', day: 'numeric' };
      
      if (startDate === endDate) {
        return start.toLocaleDateString('ko-KR', options);
      }
      
      return `${start.toLocaleDateString('ko-KR', options)} - ${end.toLocaleDateString('ko-KR', options)}`;
    }

    function formatTotalTravelTime(planId, day) {
      const totalTime = getTotalTravelTimeForDay(planId, day);
      if (!totalTime) return 'N/A';
      
      return formatTravelTime(totalTime);
    }

    // Sidebar and modal methods
    function openPlaceSelectionSidebar(day = 1) {
      selectedDay.value = day;
      isPlaceSelectionSidebarOpen.value = true;
    }

    function closePlaceSelectionSidebar() {
      isPlaceSelectionSidebarOpen.value = false;
    }

    function addDestination(place) {
      // Implementation for adding a destination
      console.log('Adding destination:', place, 'to day:', selectedDay.value);
      closePlaceSelectionSidebar();
    }

    function openEditDestinationModal(destinationId) {
      if (!plan.value) return;
      
      const destination = plan.value.destinations.find(d => d.id === destinationId);
      if (destination) {
        selectedDestination.value = destination;
        isEditDestinationModalOpen.value = true;
      }
    }

    function closeEditDestinationModal() {
      isEditDestinationModalOpen.value = false;
      selectedDestination.value = null;
    }

    function confirmDeleteDestination(destinationId) {
      destinationToDelete.value = destinationId;
      isDeleteConfirmationOpen.value = true;
    }

    function closeDeleteConfirmation() {
      isDeleteConfirmationOpen.value = false;
      destinationToDelete.value = null;
    }

    function deleteDestination() {
      if (destinationToDelete.value && plan.value) {
        removeDestinationFromPlan(plan.value.id, destinationToDelete.value)
          .then(() => {
            // Recalculate travel times after deletion
            calculateTravelTimesForPlan(plan.value.id);
          });
      }
      
      closeDeleteConfirmation();
    }

    // Drag and drop methods
    function onDragStart({ destinationId, initialY, element }) {
      if (!plan.value) return;
      
      const destination = plan.value.destinations.find(d => d.id === destinationId);
      if (destination) {
        draggedDestination.value = destination;
        draggedElement.value = element;
        originalPosition.value = { day: destination.day, order: destination.order };
        
        // Add styling for dragged element
        element.classList.add('is-dragging');
      }
    }

    function onDragEnd() {
      if (draggedElement.value) {
        draggedElement.value.classList.remove('is-dragging');
      }
      
      draggedDestination.value = null;
      draggedElement.value = null;
      originalPosition.value = null;
    }

    function onDrop(event, day) {
      event.preventDefault();
      
      if (!draggedDestination.value || !plan.value) return;
      
      // If dropping in a different day, update the day
      if (draggedDestination.value.day !== day) {
        draggedDestination.value.day = day;
        
        // Get the destinations for the new day
        const dayDestinations = getDayDestinations(day);
        
        // Set the order to be at the end of the list
        draggedDestination.value.order = dayDestinations.length > 0 
          ? Math.max(...dayDestinations.map(d => d.order)) + 1 
          : 1;
      }
      
      // Get the updated order of destinations for the day
      const newOrder = getDayDestinations(day).map(d => d.id);
      
      // Update the order in the store
      reorderDestinations(plan.value.id, day, newOrder)
        .then(() => {
          // Recalculate travel times after reordering
          calculateTravelTimesForPlan(plan.value.id);
        });
    }

    // Route optimization methods
    async function optimizeRoute() {
      if (!plan.value || !hasDestinations.value) return;
      
      isOptimizing.value = true;
      
      try {
        // Extract routes data from the current plan
        const routesData = extractRoutesData(plan.value);
        
        // Call the recommendation service
        const result = await recommendationService.optimizeRoute(routesData);
        
        // Apply the optimization result
        await applyOptimizationResult(result);
        
        // Show success message
        alert('경로가 성공적으로 최적화되었습니다!');
      } catch (error) {
        console.error('Route optimization error:', error);
        alert(`경로 최적화 중 오류가 발생했습니다: ${error.message}`);
      } finally {
        isOptimizing.value = false;
      }
    }

    function extractRoutesData(plan) {
      // Group destinations by day
      const dayDestinations = {};
      
      plan.destinations.forEach(dest => {
        if (!dayDestinations[dest.day]) {
          dayDestinations[dest.day] = [];
        }
        dayDestinations[dest.day].push(dest);
      });
      
      // Create dayLocations array
      const dayLocations = Object.entries(dayDestinations).map(([day, destinations]) => {
        // Sort destinations by order
        const sortedDestinations = [...destinations].sort((a, b) => a.order - b.order);
        
        // Map to the format expected by the API
        return {
          day: parseInt(day),
          locations: sortedDestinations.map(dest => ({
            id: dest.id,
            name: dest.name,
            latitude: dest.coordinates?.lat || 0,
            longitude: dest.coordinates?.lng || 0,
            duration: dest.duration || 0
          }))
        };
      });
      
      // Create the final request object
      return {
        planId: plan.id,
        startLocation: plan.latitude_start && plan.longitude_start ? {
          name: plan.start_location_title || "Start",
          latitude: plan.latitude_start,
          longitude: plan.longitude_start
        } : null,
        endLocation: plan.latitude_end && plan.longitude_end ? {
          name: plan.end_location_title || "End",
          latitude: plan.latitude_end,
          longitude: plan.longitude_end
        } : null,
        dayLocations
      };
    }

    async function applyOptimizationResult(result) {
      if (!plan.value || !result.dayRoutes) return;
      
      // Process each day's route
      for (const dayRoute of result.dayRoutes) {
        const day = dayRoute.day;
        const visits = dayRoute.visits;
        
        if (!visits || visits.length === 0) continue;
        
        // Create a new order array based on the visits
        const newOrder = visits.map(visit => visit.locationId);
        
        // Update the order in the store
        await reorderDestinations(plan.value.id, day, newOrder);
      }
      
      // Recalculate travel times after optimization
      await calculateTravelTimesForPlan(plan.value.id);
    }

    // Lifecycle hooks
    onMounted(async () => {
      const planId = route.params.id;
      
      if (planId) {
        await fetchTravelPlanById(planId);
        
        if (plan.value) {
          // Calculate travel times for the plan
          calculateTravelTimesForPlan(plan.value.id);
        }
      }
    });

    // Watch for changes to the plan
    watch(() => plan.value, (newPlan) => {
      if (newPlan) {
        // Recalculate travel times when the plan changes
        calculateTravelTimesForPlan(newPlan.id);
      }
    }, { deep: true });

    return {
      plan,
      activeTab,
      tabs,
      isPlaceSelectionSidebarOpen,
      selectedDay,
      isEditDestinationModalOpen,
      selectedDestination,
      isDeleteConfirmationOpen,
      destinationToDelete,
      isOptimizing,
      dayCount,
      hasDestinations,
      startLocation,
      endLocation,
      getDayDestinations,
      getNextDestination,
      getPreviousDestination,
      formatDateRange,
      formatTotalTravelTime,
      openPlaceSelectionSidebar,
      closePlaceSelectionSidebar,
      addDestination,
      openEditDestinationModal,
      closeEditDestinationModal,
      confirmDeleteDestination,
      closeDeleteConfirmation,
      deleteDestination,
      onDragStart,
      onDragEnd,
      onDrop,
      optimizeRoute,
      isLoading,
      error,
      isCalculatingTravelTimes
    };
  }
}
</script>

<style scoped>
.travel-plan-detail {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.detail-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.detail-header h1 {
  margin: 0 0 0 20px;
  font-size: 1.8rem;
  color: var(--primary-color);
  flex: 1;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.optimize-button {
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 4px;
  padding: 8px 15px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.optimize-button:hover:not(:disabled) {
  background-color: #004c8e;
}

.optimize-button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.loading-container,
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 50px 20px;
  text-align: center;
}

.error-icon {
  font-size: 3rem;
  margin-bottom: 20px;
}

.back-link {
  margin-top: 20px;
  color: var(--primary-color);
  text-decoration: none;
  font-weight: bold;
}

.plan-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.plan-info {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.info-card,
.description-card {
  background-color: white;
  border-radius: 8px;
  padding: 15px;
  box-shadow: var(--shadow);
  flex: 1;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.info-label {
  font-weight: bold;
  color: var(--text-light);
}

.description-card h3 {
  margin-top: 0;
  color: var(--primary-color);
  margin-bottom: 10px;
}

.plan-tabs {
  margin-bottom: 20px;
}

.tab-content {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: var(--shadow);
}

.empty-itinerary {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 50px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 20px;
}

.add-destination-button {
  margin-top: 20px;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 4px;
  padding: 10px 20px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.add-destination-button:hover {
  background-color: #004c8e;
}

.itinerary-days {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.day-container {
  border: 1px solid var(--border-color);
  border-radius: 8px;
  overflow: hidden;
}

.day-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background-color: var(--background-light);
  border-bottom: 1px solid var(--border-color);
}

.day-header h3 {
  margin: 0;
  color: var(--primary-color);
}

.day-actions {
  display: flex;
  gap: 10px;
}

.add-button {
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 4px;
  padding: 5px 10px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.add-button:hover {
  background-color: #004c8e;
}

.day-total-time {
  display: flex;
  align-items: center;
  padding: 10px 15px;
  background-color: #f8f9fa;
  border-bottom: 1px solid var(--border-color);
}

.time-label {
  font-weight: bold;
  margin-right: 10px;
}

.time-value {
  color: var(--primary-color);
  font-weight: bold;
}

.empty-day {
  padding: 20px;
  text-align: center;
  color: var(--text-light);
}

.day-destinations {
  padding: 15px;
}

.map-content {
  height: 600px;
}

.edit-destination-form,
.delete-confirmation {
  padding: 20px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.cancel-button {
  background-color: var(--background-light);
  border: 1px solid var(--border-color);
  border-radius: 4px;
  padding: 8px 15px;
  cursor: pointer;
}

.delete-button {
  background-color: var(--secondary-color);
  color: white;
  border: none;
  border-radius: 4px;
  padding: 8px 15px;
  cursor: pointer;
}

@media (max-width: 768px) {
  .plan-info {
    flex-direction: column;
  }
  
  .map-content {
    height: 400px;
  }
}
</style>
