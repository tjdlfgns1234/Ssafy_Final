<template>
  <div class="travel-plans-page">
    <!-- Content for the travel plans page -->
    <div class="travel-plans-content" :style="contentStyle">
      <!-- Sidebar toggle button -->
      <button
        class="sidebar-toggle-button"
        @click="toggleMainSidebar"
        :class="{ 'sidebar-closed': !isSidebarOpen }"
      >
        <span class="toggle-icon">◀</span>
      </button>

      <!-- Map container -->
      <div class="map-container" ref="mapContainer">
        <!-- Map will be rendered here -->
      </div>

      <!-- Detail sidebar toggle button (visible when detail sidebar is hidden) -->
      <button
        v-if="selectedPlan && !isPlanDetailOpen"
        class="detail-sidebar-toggle-button"
        @click="togglePlanDetail"
      >
        <span class="toggle-icon">▶</span>
      </button>
    </div>

    <!-- Plan Detail Sidebar -->
    <ResizableSidebar
      v-if="selectedPlan"
      :isOpen="isPlanDetailOpen"
      :width="planDetailWidth"
      :title="selectedPlan.title"
      position="right"
      @toggle="togglePlanDetail"
      @resize="handlePlanDetailResize"
      class="plan-detail-sidebar"
    >
      <div class="plan-detail-content">
        <div class="detail-header">
          <p class="detail-dates">
            {{ formatDateRange(selectedPlan.startDate, selectedPlan.endDate) }}
          </p>
          <p class="detail-description">{{ selectedPlan.description }}</p>
        </div>
        <div class="view-controls">
          <button class="view-control-button" @click="showEntireRoute" title="전체 경로 보기">
            전체보기
          </button>
          <button class="view-control-button" @click="updateMapMarkers" title="현재 일차만 보기">
            현재 일차만
          </button>
        </div>

        <!-- Multi-day view (horizontal scrollable) -->
        <div class="multi-day-view">
          <div
            class="multi-day-container"
            ref="multiDayContainer"
            @wheel.prevent="handleHorizontalScroll"
            @mousedown="handleMouseDown"
            @mousemove="handleMouseMove"
            @mouseup="handleMouseUp"
            @mouseleave="handleMouseLeave"
          >
            <div
              v-for="day in totalDaysComputed"
              :key="day"
              class="day-column"
              :class="{ 'active-day': activeDay === day }"
              @click="highlightDayOnMap(day)"
            >
              <div class="day-header">
                <div class="day-header-content">
                  <h3>{{ day }}일차</h3>
                  <div class="day-date">{{ formatDayDate(selectedPlan.startDate, day - 1) }}</div>
                </div>
                <button
                  v-if="totalDaysComputed > 1"
                  class="delete-day-button"
                  @click.stop="confirmDeleteDay(day)"
                  title="일차 삭제"
                >
                  ✕
                </button>
              </div>

              <div class="day-locations">
                <div class="day-time-input">
                  <div class="time-label">시작 시간</div>
                  <input
                    type="time"
                    :value="getDayStartTime(day)"
                    @change="updateDayStartTime($event, day)"
                    class="time-input"
                  />
                </div>

                <div class="location-item start-location" @click="focusOnStartLocation(day)">
                  <div class="location-label">출발 지점</div>
                  <div class="location-content">
                    <div class="location-name">{{ getDayStartLocation(day) }}</div>
                    <button
                      class="location-edit-button"
                      @click.stop="editDayStartLocation(day)"
                      title="Edit"
                    >
                      ✏️
                    </button>
                  </div>
                  <div class="location-time">{{ getDayStartTime(day) }}</div>
                </div>

                <div class="location-item end-location" @click="focusOnEndLocation(day)">
                  <div class="location-label">도착 지점</div>
                  <div class="location-content">
                    <div class="location-name">{{ getDayEndLocation(day) }}</div>
                    <button
                      class="location-edit-button"
                      @click.stop="editDayEndLocation(day)"
                      title="Edit"
                    >
                      ✏️
                    </button>
                  </div>
                  <div class="location-time">{{ getDayEndTime(day) }}</div>
                </div>
              </div>

              <div
                class="day-destinations-column"
                :data-day="day"
                @dragover="onDragOver"
                @dragleave="onDragLeave"
                @drop="onDrop($event, day)"
              >
                <!-- Start location to first destination line (or start to end if no destinations) -->
                <div
                  v-if="dayLists[day].length === 0"
                  class="travel-line empty-day-line"
                  @click="handleTravelLineClick('start-to-end', day)"
                >
                  <div class="travel-line-content">
                    <div class="dotted-line"></div>
                    <div class="travel-time-badge">
                      {{ getTotalDayTravelTime(day) }}
                    </div>
                  </div>
                </div>

                <draggable
                  :list="dayLists[day]"
                  group="destinations"
                  item-key="id"
                  handle=".destination-drag-handle"
                  ghost-class="ghost-destination"
                  animation="200"
                  :no-transition-on-drag="true"
                  @change="(e) => handleDayListChange(day, dayLists[day])"
                >
                  <template #item="{ element, index }">
                    <div class="destination-with-lines">
                      <!-- Travel line from previous location -->
                      <div
                        v-if="index === 0"
                        class="travel-line"
                        @click="handleTravelLineClick('start-to-dest', day, element.id)"
                      >
                        <div class="travel-line-content">
                          <div class="dotted-line"></div>
                          <div class="travel-time-badge">
                            {{ getFirstDestinationTravelTime(day) }}
                          </div>
                        </div>
                      </div>

                      <!-- Destination item -->
                      <div
                        class="destination-item"
                        draggable="true"
                        @dragstart="onDragStart($event, element)"
                        @dragend="onDragEnd"
                        :data-id="element.id"
                        @click="focusOnDestination(element)"
                        :class="{ 'incomplete-place': !element.coordinates }"
                      >
                        <div class="destination-drag-handle">
                          <div class="destination-number">{{ index + 1 }}</div>
                          <div class="drag-icon">⋮⋮</div>
                        </div>
                        <div class="destination-content">
                          <div class="destination-header">
                            <h4>{{ element.displayName || element.name }}</h4>
                            <div class="destination-time">{{ formatTime(element.time) }}</div>
                          </div>
                          <div class="destination-duration">
                            <span class="duration-label">소요 시간</span>
                            <span class="duration-value">{{
                              formatDuration(element.duration)
                            }}</span>
                            <span v-if="element.realTravelTime" class="travel-time">
                              이동:
                              {{
                                element.realTravelTime === 'N/A'
                                  ? 'N/A'
                                  : element.realTravelTime + '분'
                              }}
                            </span>
                          </div>
                        </div>
                        <div class="destination-actions">
                          <button
                            class="edit-destination-button"
                            @click.stop="openEditDestinationModal(element)"
                            title="Edit"
                          >
                            ✏️
                          </button>
                          <button
                            class="delete-destination-button"
                            @click.stop="confirmDeleteDestination(element)"
                            title="Delete"
                          >
                            🗑️
                          </button>
                        </div>
                      </div>

                      <!-- Travel line to next destination or end -->
                      <div
                        class="travel-line"
                        @click="
                          handleTravelLineClick(
                            index === dayLists[day].length - 1 ? 'dest-to-end' : 'dest-to-dest',
                            day,
                            element.id,
                            index < dayLists[day].length - 1 ? dayLists[day][index + 1].id : null,
                          )
                        "
                      >
                        <div class="travel-line-content">
                          <div class="dotted-line"></div>
                          <div class="travel-time-badge">
                            {{
                              index === dayLists[day].length - 1
                                ? getLastDestinationTravelTime(day)
                                : formatTravelTime(element, dayLists[day][index + 1])
                            }}
                          </div>
                        </div>
                      </div>
                    </div>
                  </template>
                </draggable>

                <button
                  class="add-destination-button small"
                  @click="openAddDestinationModalForDay(day)"
                >
                  + 계획 추가
                </button>
              </div>
            </div>
            <div class="add-day-column">
              <button class="add-day-button" @click="addDay" title="일차 추가">+</button>
            </div>
          </div>
        </div>
        <div class="plan-actions">
          <button @click="cancelChanges" class="cancel-btn">취소</button>
          <button @click="applyChanges" class="apply-btn">적용</button>
          <button @click="getAIRecommendations" class="ai-recommend-btn">AI 추천</button>
          <button @click="getAlgorithmRecommendations" class="algorithm-recommend-btn">
            알고리즘 추천
          </button>
          <button
            @click="calculateRealTravelTimes"
            class="real-time-btn"
            :disabled="isCalculatingTravelTimes"
          >
            {{ isCalculatingTravelTimes ? '계산 중...' : '실시간 이동시간 계산' }}
          </button>
        </div>
      </div>
    </ResizableSidebar>

    <!-- Modals -->
    <div
      v-if="isAddPlanModalOpen && !isPlaceSelectionActive"
      class="modal-overlay"
      @click="closeAddPlanModal"
    >
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>{{ editingPlan ? '여행 계획 수정' : '새 여행 계획' }}</h3>
          <button class="close-modal-button" @click="closeAddPlanModal">✕</button>
        </div>
        <div class="modal-content">
          <form @submit.prevent="savePlan">
            <div class="form-group">
              <label for="plan-title">여행 제목</label>
              <input
                id="plan-title"
                v-model="planForm.title"
                type="text"
                required
                placeholder="예: 서울 3박 4일 여행"
              />
            </div>

            <div class="form-group">
              <label for="plan-description">여행 설명</label>
              <textarea
                id="plan-description"
                v-model="planForm.description"
                type="text"
                placeholder="여행에 대한 간단한 설명을 입력하세요."
              ></textarea>
            </div>

            <div class="form-group">
              <label for="plan-location">시작 지점</label>
              <div class="input-with-button">
                <input
                  id="plan-location"
                  v-model="planForm.location"
                  type="text"
                  placeholder="시작 지점을 선택하세요"
                  readonly
                />
                <button type="button" class="select-place-button" @click="selectStartLocation">
                  시작 지점 추가
                </button>
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label for="plan-start-date">시작일</label>
                <input id="plan-start-date" v-model="planForm.startDate" type="date" required />
              </div>

              <div class="form-group">
                <label for="plan-end-date">종료일</label>
                <input
                  id="plan-end-date"
                  v-model="planForm.endDate"
                  type="date"
                  required
                  :min="planForm.startDate"
                />
              </div>
            </div>

            <div class="form-actions">
              <button type="button" class="cancel-button" @click="closeAddPlanModal">취소</button>
              <button type="submit" class="save-button">저장</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <div
      v-if="isDestinationModalOpen && !isPlaceSelectionActive"
      class="modal-overlay"
      @click="closeDestinationModal"
    >
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>{{ editingDestination ? '계획 수정' : '계획 추가' }}</h3>
          <button class="close-modal-button" @click="closeDestinationModal">✕</button>
        </div>
        <div class="modal-content">
          <form @submit.prevent="saveDestination">
            <div class="form-group">
              <label for="destination-name">장소 이름</label>
              <div class="input-with-button">
                <input
                  id="destination-name"
                  v-model="destinationForm.name"
                  type="text"
                  placeholder="예: 경복궁"
                  readonly
                />
                <button type="button" class="select-place-button" @click="openPlaceSelector">
                  장소 선택
                </button>
              </div>
            </div>

            <div class="form-group">
              <label for="destination-address">주소</label>
              <input
                id="destination-address"
                v-model="destinationForm.address"
                type="text"
                placeholder="예: 서울특별시 종로구 사직로 161"
                readonly
              />
            </div>

            <div class="form-group">
              <label for="plan-name">계획 이름</label>
              <input
                id="plan-name"
                v-model="destinationForm.planName"
                type="text"
                placeholder="장소 이름과 동일하게 사용하려면 비워두세요"
              />
            </div>

            <div class="form-row">
              <div class="form-group">
                <label for="destination-day">Day</label>
                <select id="destination-day" v-model="destinationForm.day" required>
                  <option v-for="day in totalDaysComputed" :key="day" :value="day">
                    Day {{ day }}
                  </option>
                </select>
              </div>
            </div>

            <div class="form-group">
              <label for="destination-duration">소요 시간 (분)</label>
              <input
                id="destination-duration"
                v-model="destinationForm.duration"
                type="number"
                min="1"
                required
                placeholder="예: 120 (2시간)"
              />
            </div>

            <div class="form-group">
              <label for="destination-notes">메모</label>
              <textarea
                id="destination-notes"
                v-model="destinationForm.notes"
                placeholder="장소에 대한 메모를 입력하세요."
              ></textarea>
            </div>

            <div class="form-actions">
              <button type="button" class="cancel-button" @click="closeDestinationModal">
                취소
              </button>
              <button type="submit" class="save-button" :disabled="!isDestinationFormValid">
                저장
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <div v-if="isConfirmDeleteOpen" class="modal-overlay" @click="isConfirmDeleteOpen = false">
      <div class="confirm-modal" @click.stop>
        <h3>여행 계획 삭제</h3>
        <p>이 여행 계획을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.</p>
        <div class="confirm-actions">
          <button class="cancel-button" @click="isConfirmDeleteOpen = false">취소</button>
          <button class="delete-button" @click="deletePlan">삭제</button>
        </div>
      </div>
    </div>

    <div
      v-if="isConfirmDeleteDestinationOpen"
      class="modal-overlay"
      @click="isConfirmDeleteDestinationOpen = false"
    >
      <div class="confirm-modal" @click.stop>
        <h3>장소 삭제</h3>
        <p>이 장소를 여행 계획에서 삭제하시겠습니까?</p>
        <div class="confirm-actions">
          <button class="cancel-button" @click="isConfirmDeleteDestinationOpen = false">
            취소
          </button>
          <button class="delete-button" @click="deleteDestination">삭제</button>
        </div>
      </div>
    </div>

    <div
      v-if="isConfirmDeleteDayOpen"
      class="modal-overlay"
      @click="isConfirmDeleteDayOpen = false"
    >
      <div class="confirm-modal" @click.stop>
        <h3>일차 삭제</h3>
        <p>{{ dayToDelete }}일차를 삭제하시겠습니까? 이 일차의 모든 계획이 삭제됩니다.</p>
        <div class="confirm-actions">
          <button class="cancel-button" @click="isConfirmDeleteDayOpen = false">취소</button>
          <button class="delete-button" @click="deleteDay(dayToDelete)">삭제</button>
        </div>
      </div>
    </div>

    <!-- Place Selection Sidebar -->
    <PlaceSelectionSidebar
      :isActive="isPlaceSelectionActive"
      @close="handlePlaceSelectionClose"
      @select-place="handlePlaceSelected"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick, inject } from 'vue'
import { useAuthStore } from '../stores/auth'
import { useModalStore } from '../stores/modal'
import draggable from 'vuedraggable'
import ResizableSidebar from '../components/layout/ResizableSidebar.vue'
import PlaceSelectionSidebar from '../components/travel/PlaceSelectionSidebar.vue'
import { travelPlanService } from '../services/travelPlanService.js'
import travelTimeService from '../services/travelTimeService.js'
import recommendationService from '../services/recommendationService.js'

const props = defineProps({
  isSidebarOpen: Boolean,
  sidebarWidth: Number,
  isTravelPlansSidebarOpen: Boolean,
  travelPlansSidebarWidth: Number,
  selectedPlan: Object,
  travelPlans: Array,
})

const emit = defineEmits(['toggle-sidebar'])

// Stores
const authStore = useAuthStore()
const modalStore = useModalStore()

const isSidebarOpen = inject('isSidebarOpen', ref(true))
const sidebarWidth = inject('sidebarWidth', ref(250))
const isTravelPlansSidebarOpen = inject('isTravelPlansSidebarOpen', ref(true))
const travelPlansSidebarWidth = inject('travelPlansSidebarWidth', ref(300))

// Content state
const isLoading = ref(false)
const activeDay = ref(1)
const multiDayContainer = ref(null)
let showScrollIndicator = ref(false)
const tabsContainer = ref(null)
const destinationsList = ref(null)
const draggedDestinations = ref([])

// Middle mouse button panning state
// Modal state
const isAddPlanModalOpen = ref(false)
const editingPlan = ref(null)
const isDestinationModalOpen = ref(false)
const editingDestination = ref(null)
const isConfirmDeleteOpen = ref(false)
const planToDelete = ref(null)
const isConfirmDeleteDestinationOpen = ref(false)
const destinationToDelete = ref(null)
const isConfirmDeleteDayOpen = ref(false)
const dayToDelete = ref(null)

// Form state
const planForm = ref({
  title: '',
  description: '',
  startDate: '',
  endDate: '',
  location: '',
  startCoordinates: null,
  endCoordinates: null,
  start_location_title: '',
  end_location_title: '',
  start_place_id: '',
  start_content_id: '',
  end_place_id: '',
  end_content_id: '',
})

const destinationForm = ref({
  name: '',
  address: '',
  planName: '',
  day: 1,
  duration: 60, // Default 60 minutes
  notes: '',
  coordinates: null,
  place_id: null,
  content_id: null,
})

// Plan detail sidebar state
const isPlanDetailOpen = ref(true)
const planDetailWidth = ref(window.innerWidth > 1200 ? 600 : 400)
const mapContainer = ref(null)

// Add this new state
const isPlaceSelectionActive = ref(false)
const currentSelectionTarget = ref(null) // To track which form is being filled

// Add after existing refs
const originalPlanData = ref(null)

// Google Maps API 로딩 상태
const googleMapsLoaded = ref(false)

// Axios service functions
async function fetchTravelPlansFromServer() {
  try {
    const authStore = useAuthStore()
    const userId = authStore.user?.mno || 'guest'

    console.log('=== FETCH TRAVEL PLANS START ===')
    console.log('User ID:', userId)
    console.log('Calling travelPlanService.getAllPlansByUser...')

    console.log('Fetching travel plans for user:', userId)
    const res = await travelPlanService.getAllPlansByUser(userId)
    console.log('Server response:', res)

    if (res && res.length > 0) {
      // Use real server data
      applyTravelPlansToView(res)
      console.log('Applied server data to view')
    } else {
      // Fallback to dummy data if no server data
      console.log('No server data, using dummy data')
      //applyTravelPlansDummy()
    }

    return res
  } catch (error) {
    console.error('Error fetching travel plans:', error)
    // Fallback to dummy data on error
    console.log('Error occurred, using dummy data')
    //applyTravelPlansDummy()
    return []
  }
}

async function saveTravelPlanToServer(planData) {
  try {
    console.log('=== SAVE TRAVEL PLAN START ===')
    console.log('Plan data being sent:', JSON.stringify(planData, null, 2))

    console.log('Saving plan data:', planData)
    const res = await travelPlanService.savePlan(planData)
    console.log('Save response:', res)

    return { success: true, plan_id: res.plan_id || res.id || Date.now() }
  } catch (error) {
    console.error('Error saving travel plan:', error)
    throw error
  }
}

async function updateTravelPlanToServer(planData) {
  try {
    console.log('=== UPDATE TRAVEL PLAN START ===')
    console.log('Plan data being sent:', JSON.stringify(planData, null, 2))

    console.log('Updating plan data:', planData)
    const planId = planData.plan_id || planData.id
    const res = await travelPlanService.updatePlan(planId, planData)
    console.log('Update response:', res)

    return { success: true }
  } catch (error) {
    console.error('Error updating travel plan:', error)
    throw error
  }
}

async function deleteTravelPlanFromServer(planId) {
  try {
    console.log('=== DELETE TRAVEL PLAN START ===')
    console.log('Plan ID to delete:', planId)

    console.log('Deleting plan:', planId)
    const res = await travelPlanService.deletePlan(planId)
    console.log('Delete response:', res)

    return { success: true }
  } catch (error) {
    console.error('Error deleting travel plan:', error)
    throw error
  }
}

function applyTravelPlansDummy() {
  // Mock data for testing
  const mockPlans = [
    {
      id: 1,
      title: 'Seoul Weekend Getaway',
      description: 'A quick weekend trip to explore Seoul',
      startDate: '2023-11-10',
      endDate: '2023-11-12',
      destinations: [
        { id: 1, name: 'Gyeongbokgung Palace', day: 1, order: 1 },
        { id: 2, name: 'Namsan Seoul Tower', day: 1, order: 2 },
        { id: 3, name: 'Bukchon Hanok Village', day: 2, order: 1 },
      ],
      image: 'https://via.placeholder.com/400x300?text=Seoul+Trip',
      start_location_title: '서울역',
      place_id: 'ChIJzWXFYYuifDUR64Pq5LTtioU',
      latitude_start: 37.5546,
      longitude_start: 126.9706,
      end_location_title: '서울역',
      latitude_end: 37.5546,
      longitude_end: 126.9706,
      startCoordinates: { lat: 37.5546, lng: 126.9706 },
      endCoordinates: { lat: 37.5546, lng: 126.9706 },
    },
    {
      id: 2,
      title: 'Jeju Island Adventure',
      description: 'Exploring the natural wonders of Jeju Island',
      startDate: '2023-12-15',
      endDate: '2023-12-20',
      destinations: [
        { id: 6, name: 'Seongsan Ilchulbong', day: 1, order: 1 },
        { id: 7, name: 'Hallasan Mountain', day: 2, order: 1 },
      ],
      image: 'https://via.placeholder.com/400x300?text=Jeju+Trip',
      start_location_title: '제주국제공항',
      place_id: 'ChIJRUDiP8i0cDURVL8_PJYpUhA',
      latitude_start: 33.5067,
      longitude_start: 126.493,
      end_location_title: '제주국제공항',
      latitude_end: 33.5067,
      startCoordinates: { lat: 33.5067, lng: 126.493 },
      endCoordinates: { lat: 33.5067, lng: 126.493 },
    },
  ]

  props.travelPlans.splice(0, props.travelPlans.length, ...mockPlans)
  console.log('Applied dummy travel plans:', mockPlans)
}

function applyTravelPlansToView(serverData) {
  // Convert server data to view format\
  const convertedPlans = serverData.map((plan) => convertServerPlanToView(plan))
  props.travelPlans.splice(0, props.travelPlans.length, ...convertedPlans)
}

function convertServerPlanToView(serverPlan) {
  // Handle routes - it might be a string or already an object
  let routes
  try {
    if (typeof serverPlan.routes === 'string') {
      routes = JSON.parse(serverPlan.routes)
    } else {
      routes = serverPlan.routes // Already an object
    }
  } catch (error) {
    console.error('Error parsing routes:', error)
    // Fallback to empty structure
    routes = {
      title: serverPlan.title || 'Untitled Plan',
      startLocation: { title: 'Unknown', coordinates: null },
      destinations: [],
      dayLocations: {},
    }
  }

  return {
    id: serverPlan.planId,
    title: routes.title,
    description: serverPlan.description,
    startDate: new Date(serverPlan.start_day).toISOString().split('T')[0],
    endDate: new Date(serverPlan.end_day).toISOString().split('T')[0],
    userId: serverPlan.user_id,
    destinations: routes.destinations.map((dest) => ({
      id: dest.id,
      title: dest.title,
      displayName: dest.displayName,
      description: dest.description,
      day: dest.day,
      order: dest.order,
      duration: dest.duration,
      time: dest.time,
      address: dest.address,
      coordinates: { lat: dest.latitude, lng: dest.longitude },
      place_id: dest.place_id,
      content_id: dest.content_id,
    })),
    startCoordinates: routes.startLocation.coordinates,
    endCoordinates: routes.dayLocations[Object.keys(routes.dayLocations).pop()]?.endCoordinates,
    start_location_title: routes.startLocation.title,
    end_location_title:
      routes.dayLocations[Object.keys(routes.dayLocations).pop()]?.end_location_title,
    dayLocations: routes.dayLocations,
    image: 'https://via.placeholder.com/400x300?text=Travel+Plan',
  }
}

function convertViewPlanToServer(viewPlan) {
  // Ensure dayLocations has complete start/end data for each day
  const totalDays = getDayCount(viewPlan.startDate, viewPlan.endDate)
  const dayLocations = { ...viewPlan.dayLocations }

  // Fill in missing dayLocations data
  for (let day = 1; day <= totalDays; day++) {
    if (!dayLocations[day]) {
      dayLocations[day] = {}
    }

    // Ensure start location data
    if (!dayLocations[day].start_location_title) {
      if (day === 1) {
        dayLocations[day].start_location_title = viewPlan.start_location_title || '위치 미지정'
        dayLocations[day].startCoordinates = viewPlan.startCoordinates
        // Add place_id and content_id for first day start location
        dayLocations[day].start_place_id = viewPlan.start_place_id || ''
        dayLocations[day].start_content_id = viewPlan.start_content_id || ''
      } else {
        // Use previous day's end location
        dayLocations[day].start_location_title =
          dayLocations[day - 1]?.end_location_title || '위치 미지정'
        dayLocations[day].startCoordinates = dayLocations[day - 1]?.endCoordinates
        // Copy place_id and content_id from previous day's end location
        dayLocations[day].start_place_id = dayLocations[day - 1]?.end_place_id || ''
        dayLocations[day].start_content_id = dayLocations[day - 1]?.end_content_id || ''
      }
    }

    // Ensure end location data
    if (!dayLocations[day].end_location_title) {
      if (day === totalDays) {
        dayLocations[day].end_location_title =
          viewPlan.end_location_title || viewPlan.start_location_title || '위치 미지정'
        dayLocations[day].endCoordinates = viewPlan.endCoordinates || viewPlan.startCoordinates
        // Add place_id and content_id for last day end location
        dayLocations[day].end_place_id = viewPlan.end_place_id || viewPlan.start_place_id || ''
        dayLocations[day].end_content_id =
          viewPlan.end_content_id || viewPlan.start_content_id || ''
      } else {
        // Use same as start location by default
        dayLocations[day].end_location_title = dayLocations[day].start_location_title
        dayLocations[day].endCoordinates = dayLocations[day].startCoordinates
        // Copy place_id and content_id from start location
        dayLocations[day].end_place_id = dayLocations[day].start_place_id || ''
        dayLocations[day].end_content_id = dayLocations[day].start_content_id || ''
      }
    }

    // Ensure coordinates exist
    if (!dayLocations[day].startCoordinates) {
      dayLocations[day].startCoordinates = viewPlan.startCoordinates || {
        lat: 37.5665,
        lng: 126.978,
      }
    }
    if (!dayLocations[day].endCoordinates) {
      dayLocations[day].endCoordinates = viewPlan.endCoordinates ||
        viewPlan.startCoordinates || { lat: 37.5665, lng: 126.978 }
    }

    // Ensure place_id and content_id exist even if not set above
    if (!dayLocations[day].start_place_id) {
      dayLocations[day].start_place_id = ''
    }
    if (!dayLocations[day].start_content_id) {
      dayLocations[day].start_content_id = ''
    }
    if (!dayLocations[day].end_place_id) {
      dayLocations[day].end_place_id = ''
    }
    if (!dayLocations[day].end_content_id) {
      dayLocations[day].end_content_id = ''
    }
  }

  const routes = {
    title: viewPlan.title,
    startLocation: {
      title: viewPlan.start_location_title,
      coordinates: viewPlan.startCoordinates,
      place_id: dayLocations[1]?.start_place_id || '',
      content_id: dayLocations[1]?.start_content_id || '',
    },
    destinations: viewPlan.destinations.map((dest) => ({
      id: dest.id,
      title: dest.name,
      description: dest.description || '',
      latitude: dest.coordinates?.lat || 0,
      longitude: dest.coordinates?.lng || 0,
      place_id: dest.place_id || '',
      content_id: dest.content_id || '',
      day: dest.day,
      order: dest.order,
      duration: dest.duration,
      time: dest.time,
      address: dest.address,
      displayName: dest.displayName || dest.name,
    })),
    dayLocations: dayLocations,
  }

  return {
    plan_id: viewPlan.id,
    user_id: viewPlan.userId || authStore.user?.mno || 123,
    description: viewPlan.description,
    routes: JSON.stringify(routes),
    start_day: new Date(viewPlan.startDate).toISOString(),
    end_day: new Date(viewPlan.endDate).toISOString(),
  }
}

// Computed
const totalDaysComputed = computed(() => {
  if (!props.selectedPlan) return 1

  return getDayCount(props.selectedPlan.startDate, props.selectedPlan.endDate)
})

const contentStyle = computed(() => {
  return {
    marginLeft: isTravelPlansSidebarOpen.value ? `${travelPlansSidebarWidth.value}px` : '0',
    width: isTravelPlansSidebarOpen.value
      ? `calc(100% - ${travelPlansSidebarWidth.value}px)`
      : '100%',
    marginRight: props.selectedPlan && isPlanDetailOpen.value ? `${planDetailWidth.value}px` : '0',
  }
})

const dayDestinations = computed(() => {
  if (!props.selectedPlan || !props.selectedPlan.destinations) return []

  const destinations = props.selectedPlan.destinations.filter(
    (dest) => dest && dest.day === activeDay.value,
  )
  draggedDestinations.value = [...destinations]
  return destinations
})

// Create a reactive object for day lists
const dayLists = ref({})

function updateDayLists() {
  if (!props.selectedPlan) return

  const days = totalDaysComputed.value
  const newDayLists = {}

  for (let day = 1; day <= days; day++) {
    // Make sure destinations exist and have valid day property
    if (props.selectedPlan.destinations && Array.isArray(props.selectedPlan.destinations)) {
      newDayLists[day] = props.selectedPlan.destinations.filter((d) => d && d.day === day)
    } else {
      newDayLists[day] = []
    }
  }

  dayLists.value = newDayLists
}

// Watch for changes in the plan or days
watch(() => props.selectedPlan, updateDayLists, { immediate: true, deep: true })
watch(() => totalDaysComputed.value, updateDayLists)

// Handle the draggable change event
function handleDayListChange(day, newList) {
  if (!props.selectedPlan) return

  // Update day and order for each item in the new list
  newList.forEach((item, idx) => {
    item.day = day
    item.order = idx + 1
  })

  // Merge with destinations from other days
  const otherDests = props.selectedPlan.destinations.filter((d) => d.day !== day)
  props.selectedPlan.destinations = [...otherDests, ...newList]

  // Update times, save, and update map
  nextTick(() => {
    updateAllDestinationTimes()
    saveTravelPlans()
    updateMapMarkers()

    // Update real-time travel times if Google Maps is loaded
    if (googleMapsLoaded.value) {
      updateRealTimeTravelTimes(day)
    }
  })
}

// Toggle main sidebar
function toggleMainSidebar() {
  emit('toggle-sidebar')
}

// Scroll handling
function handleHorizontalScroll(event) {
  if (!multiDayContainer.value) return

  // Don't interfere with middle button scrolling
  if (event.buttons === 4 || event.button === 1) {
    return
  }

  multiDayContainer.value.scrollLeft += event.deltaY
}

// Handle mouse down for middle button panning
function handleMouseDown(event) {
  // For middle mouse button, do nothing and let browser handle it
  if (event.button === 1) {
    // Don't prevent default - this allows browser's native autoscroll
    return
  }
}

// These functions are simplified since we're using browser's native behavior
function handleMouseMove(event) {
  // No implementation - let browser handle autoscroll
}

function handleMouseUp(event) {
  // No implementation - let browser handle autoscroll
}

function handleMouseLeave() {
  // No implementation - let browser handle autoscroll
}

const handleTabsScroll = () => {
  if (tabsContainer.value) {
    showScrollIndicator.value = tabsContainer.value.scrollWidth > tabsContainer.value.clientWidth
  }
}

// Formatting functions
function formatDateRange(startDate, endDate) {
  if (!startDate || !endDate) return ''

  const start = new Date(startDate)
  const end = new Date(endDate)

  const options = { year: 'numeric', month: 'short', day: 'numeric' }

  if (startDate === endDate) {
    return start.toLocaleDateString('ko-KR', options)
  }

  return `${start.toLocaleDateString('ko-KR', options)} - ${end.toLocaleDateString('ko-KR', options)}`
}

const formatDayDateMemo = ref({})

function formatDayDate(startDate, dayOffset) {
  const key = `${startDate}-${dayOffset}`

  if (formatDayDateMemo.value[key]) {
    return formatDayDateMemo.value[key]
  }

  if (!startDate) return ''

  const date = new Date(startDate)
  date.setDate(date.getDate() + dayOffset)

  const formattedDate = date.toLocaleDateString('ko-KR', { month: 'short', day: 'numeric' })
  formatDayDateMemo.value = { ...formatDayDateMemo.value, [key]: formattedDate }
  return formattedDate
}

function formatTime(timeString) {
  if (!timeString) return ''

  const [hours, minutes] = timeString.split(':').map(Number)

  // Check if it's past midnight (next day)
  if (hours >= 24) {
    const nextDayHours = hours - 24
    return `다음날 오전 ${nextDayHours}:${minutes.toString().padStart(2, '0')}`
  }

  // Format with AM/PM
  const period = hours >= 12 ? '오후' : '오전'
  const hour12 = hours % 12 || 12

  return `${period} ${hour12}:${minutes.toString().padStart(2, '0')}`
}

function formatDuration(minutes) {
  if (!minutes) return ''

  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60

  if (hours > 0 && mins > 0) {
    return `${hours}시간 ${mins}분`
  } else if (hours > 0) {
    return `${hours}시간`
  } else {
    return `${mins}분`
  }
}

function getDayCount(startDate, endDate) {
  if (!startDate || !endDate) return 1

  const start = new Date(startDate)
  const end = new Date(endDate)

  // Calculate the difference in days
  const diffTime = Math.abs(end - start)
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))

  return diffDays + 1 // Include both start and end days
}

function getDayStartTime(day) {
  if (!props.selectedPlan) return '10:00'

  // Check if we have a specific start time for this day in dayLocations
  if (
    props.selectedPlan.dayLocations &&
    props.selectedPlan.dayLocations[day] &&
    props.selectedPlan.dayLocations[day].startTime
  ) {
    return props.selectedPlan.dayLocations[day].startTime
  }

  // Default to 10:00 if no start time is set
  return '10:00'
}

async function updateDayStartTime(event, day) {
  const newStartTime = event.target.value
  if (!props.selectedPlan || !newStartTime) return

  // Store the user-set start time
  if (!props.selectedPlan.dayLocations) {
    props.selectedPlan.dayLocations = {}
  }

  if (!props.selectedPlan.dayLocations[day]) {
    props.selectedPlan.dayLocations[day] = {}
  }

  props.selectedPlan.dayLocations[day].startTime = newStartTime

  // Update times based on new start time
  await updateAllDestinationTimes()

  // Save changes
  saveTravelPlans()
}

function getDestinationsForDay(day) {
  return props.selectedPlan.destinations.filter((dest) => dest.day === day)
}

// Get day's start location
function getDayStartLocation(day) {
  if (!props.selectedPlan) return '위치 미지정'

  // Check if we have a specific location for this day
  if (
    props.selectedPlan.dayLocations &&
    props.selectedPlan.dayLocations[day] &&
    props.selectedPlan.dayLocations[day].start_location_title
  ) {
    return props.selectedPlan.dayLocations[day].start_location_title
  }

  // For the first day, always use the plan's start location
  if (day === 1) {
    return props.selectedPlan.start_location_title || '위치 미지정'
  }

  // For other days, use the previous day's end location
  return getDayEndLocation(day - 1)
}

// Get day's end location
function getDayEndLocation(day) {
  if (!props.selectedPlan) return '위치 미지정'

  // Check if we have a specific location for this day
  if (
    props.selectedPlan.dayLocations &&
    props.selectedPlan.dayLocations[day] &&
    props.selectedPlan.dayLocations[day].end_location_title
  ) {
    return props.selectedPlan.dayLocations[day].end_location_title
  }

  // Find the last destination of the current day
  const dayDestinations = props.selectedPlan.destinations.filter((d) => d.day === day)

  if (dayDestinations.length > 0) {
    // Sort by order and get the last one
    dayDestinations.sort((a, b) => a.order - b.order)
    const lastDest = dayDestinations[dayDestinations.length - 1]
    return lastDest.displayName || lastDest.name || '위치 미지정'
  }

  // For the last day, use the plan's end location
  if (day === totalDaysComputed.value) {
    return (
      props.selectedPlan.end_location_title ||
      props.selectedPlan.start_location_title ||
      '위치 미지정'
    )
  }

  // For other days with no destinations, use the start location
  return getDayStartLocation(day)
}

// Get day's end time
async function getDayEndTime(day) {
  if (!props.selectedPlan) return ''

  // Find destinations for this day
  const dayDests = props.selectedPlan.destinations.filter((d) => d.day === day)

  if (dayDests.length === 0) {
    // If no destinations, calculate travel time from start to end directly
    const startCoords = getDayStartCoordinates(day)
    const endCoords = getDayEndCoordinates(day)

    if (startCoords && endCoords) {
      const dayStartTime = getDayStartTime(day)
      const [startHours, startMinutes] = dayStartTime.split(':').map(Number)
      let currentTimeMinutes = startHours * 60 + startMinutes

      // Add travel time from start to end
      const travelTime = await getLocationTravelTime(
        getDayStartLocation(day),
        getDayEndLocation(day),
        startCoords,
        endCoords,
      )

      if (travelTime !== null) {
        currentTimeMinutes += travelTime
      }

      const endHours = Math.floor(currentTimeMinutes / 60)
      const endMinutes = currentTimeMinutes % 60
      return formatTime(
        `${endHours.toString().padStart(2, '0')}:${endMinutes.toString().padStart(2, '0')}`,
      )
    }

    return getDayStartTime(day)
  }

  // Sort by order
  dayDests.sort((a, b) => a.order - b.order)

  // Get the last destination
  const lastDest = dayDests[dayDests.length - 1]

  if (!lastDest.time || !lastDest.duration) return ''

  // Calculate end time of the last destination
  const [hours, minutes] = lastDest.time.split(':').map(Number)
  const durationMinutes = Number(lastDest.duration)

  let totalMinutes = hours * 60 + minutes + durationMinutes

  // Add travel time from last destination to arrival point
  const endCoords = getDayEndCoordinates(day)
  if (lastDest.coordinates && endCoords) {
    const travelTimeToEnd = await getLocationTravelTime(
      lastDest.displayName || lastDest.name,
      getDayEndLocation(day),
      lastDest.coordinates,
      endCoords,
    )

    if (travelTimeToEnd !== null) {
      totalMinutes += travelTimeToEnd
    }
  }

  const endHours = Math.floor(totalMinutes / 60)
  const endMinutes = totalMinutes % 60

  return formatTime(
    `${endHours.toString().padStart(2, '0')}:${endMinutes.toString().padStart(2, '0')}`,
  )
}

// Edit day's start location
function editDayStartLocation(day) {
  // Set the current selection target
  currentSelectionTarget.value = { type: 'dayStart', day }
  // Show the place selection sidebar
  isPlaceSelectionActive.value = true
}

// Edit day's end location
function editDayEndLocation(day) {
  // Set the current selection target
  currentSelectionTarget.value = { type: 'dayEnd', day }
  // Show the place selection sidebar
  isPlaceSelectionActive.value = true
}

// Plan CRUD operations
function openAddPlanModal() {
  if (!authStore.isAuthenticated) {
    modalStore.openLoginModal()
    return
  }

  editingPlan.value = null
  planForm.value = {
    title: '',
    description: '',
    startDate: '',
    endDate: '',
    location: '',
    startCoordinates: null,
    endCoordinates: null,
    start_location_title: '',
    end_location_title: '',
    start_place_id: '',
    start_content_id: '',
    end_place_id: '',
    end_content_id: '',
  }
  isAddPlanModalOpen.value = true
}

function openEditPlanModal(plan) {
  editingPlan.value = { ...plan } // Create a copy to avoid direct mutation
  planForm.value = {
    title: plan.title,
    description: plan.description,
    startDate: plan.startDate,
    endDate: plan.endDate,
    location: plan.location || '',
    startCoordinates: plan.startCoordinates || null,
    endCoordinates: plan.endCoordinates || null,
    start_location_title: plan.start_location_title || '',
    end_location_title: plan.end_location_title || '',
    start_place_id: plan.start_place_id || '',
    start_content_id: plan.start_content_id || '',
    end_place_id: plan.end_place_id || '',
    end_content_id: plan.end_content_id || '',
  }
  isAddPlanModalOpen.value = true
}

function closeAddPlanModal() {
  isAddPlanModalOpen.value = false
  editingPlan.value = null
}

async function savePlan() {
  if (!authStore.isAuthenticated) return

  try {
    // If no location is selected, try to get current location
    if (!planForm.value.startCoordinates) {
      try {
        const currentLocation = await getCurrentLocation()
        planForm.value.startCoordinates = currentLocation
        planForm.value.endCoordinates = currentLocation

        planForm.value.location = '현재 위치'
        planForm.value.start_location_title = '현재 위치'
        planForm.value.end_location_title = '현재 위치'

        setTimeout(() => {
          alert('시작 지점이 지정되지 않아 현재 위치를 사용합니다.')
        }, 500)
      } catch (error) {
        console.error('Failed to get current location:', error)
        planForm.value.location = '위치 미지정'
        planForm.value.start_location_title = '위치 미지정'
        planForm.value.end_location_title = '위치 미지정'
      }
    }

    planForm.value.endCoordinates = planForm.value.endCoordinates || planForm.value.startCoordinates
    planForm.value.end_location_title =
      planForm.value.end_location_title || planForm.value.start_location_title

    if (editingPlan.value) {
      // Update existing plan
      const index = props.travelPlans.findIndex((p) => p.id === editingPlan.value.id)
      if (index !== -1) {
        const oldDayCount = getDayCount(editingPlan.value.startDate, editingPlan.value.endDate)
        const newDayCount = getDayCount(planForm.value.startDate, planForm.value.endDate)

        const updatedPlan = {
          ...props.travelPlans[index],
          title: planForm.value.title,
          description: planForm.value.description,
          startDate: planForm.value.startDate,
          endDate: planForm.value.endDate,
          location: planForm.value.location,
          startCoordinates: planForm.value.startCoordinates,
          endCoordinates: planForm.value.endCoordinates,
          start_location_title: planForm.value.start_location_title,
          end_location_title: planForm.value.end_location_title,
          start_place_id: planForm.value.start_place_id,
          start_content_id: planForm.value.start_content_id,
          end_place_id: planForm.value.end_place_id,
          end_content_id: planForm.value.end_content_id,
        }

        if (newDayCount < oldDayCount) {
          updatedPlan.destinations = updatedPlan.destinations.map((dest) => {
            if (dest.day > newDayCount) {
              return { ...dest, day: newDayCount }
            }
            return dest
          })
        }

        // Send to server
        const serverPlanData = convertViewPlanToServer(updatedPlan)
        await updateTravelPlanToServer(serverPlanData)

        // Refresh travel plans from server to get updated list
        await fetchTravelPlansFromServer()

        props.travelPlans.splice(index, 1, updatedPlan)

        if (props.selectedPlan && props.selectedPlan.id === editingPlan.value.id) {
          Object.assign(props.selectedPlan, updatedPlan)
          originalPlanData.value = JSON.parse(JSON.stringify(updatedPlan))

          if (activeDay.value > newDayCount) {
            activeDay.value = newDayCount
          }
        }
      }
    } else {
      // Create new plan
      const newPlan = {
        id: Date.now(),
        ...planForm.value,
        userId: authStore.user?.mno || 123,
        destinations: [],
        location: planForm.value.location || '위치 미지정',
        startCoordinates: planForm.value.startCoordinates,
        endCoordinates: planForm.value.endCoordinates,
        start_location_title: planForm.value.start_location_title || '위치 미지정',
        end_location_title:
          planForm.value.end_location_title || planForm.value.start_location_title || '위치 미지정',
        start_place_id: planForm.value.start_place_id || '',
        start_content_id: planForm.value.start_content_id || '',
        end_place_id: planForm.value.end_place_id || '',
        end_content_id: planForm.value.end_content_id || '',
      }

      // Send to server
      const serverPlanData = convertViewPlanToServer(newPlan)
      const result = await saveTravelPlanToServer(serverPlanData)

      if (result.success) {
        newPlan.id = result.plan_id

        // Refresh travel plans from server to get updated list
        await fetchTravelPlansFromServer()
      }

      // Remove this line to prevent duplicate addition:
      // props.travelPlans.push(newPlan);

      activeDay.value = 1
    }

    closeAddPlanModal()
    saveTravelPlans()

    nextTick(() => {
      updateDayLists()
    })
  } catch (error) {
    console.error('Error saving plan:', error)
    alert('여행 계획 저장 중 오류가 발생했습니다.')
  }
}

// Function to select location for the plan
function selectStartLocation() {
  // Set the current selection target
  currentSelectionTarget.value = 'plan'
  // Show the place selection sidebar
  isPlaceSelectionActive.value = true
}

// Handle when the place selection sidebar is closed without selecting a place
function handlePlaceSelectionClose() {
  isPlaceSelectionActive.value = false
}

async function getCurrentLocation() {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(new Error('Geolocation is not supported by your browser'))
      return
    }

    navigator.geolocation.getCurrentPosition(
      (position) => {
        const coordinates = {
          lat: position.coords.latitude,
          lng: position.coords.longitude,
        }
        resolve(coordinates)
      },
      (error) => {
        console.error('Error getting location:', error)
        reject(error)
      },
      { timeout: 10000, enableHighAccuracy: true },
    )
  })
}

async function deletePlan() {
  if (planToDelete.value) {
    try {
      // Delete from server first if it's a server plan with a valid ID
      if (planToDelete.value.id && typeof planToDelete.value.id === 'number') {
        await deleteTravelPlanFromServer(planToDelete.value.id)
      }

      // Remove from local array
      const planIndex = props.travelPlans.findIndex((p) => p.id === planToDelete.value.id)
      if (planIndex !== -1) {
        props.travelPlans.splice(planIndex, 1)
      }

      // Refresh travel plans from server
      await fetchTravelPlansFromServer()

      isConfirmDeleteOpen.value = false
      planToDelete.value = null
      saveTravelPlans()
    } catch (error) {
      console.error('Error deleting plan:', error)
      alert('여행 계획 삭제 중 오류가 발생했습니다.')
    }
  }
}

function confirmDeletePlan(plan) {
  planToDelete.value = plan
  isConfirmDeleteOpen.value = true
}

// Destination CRUD operations
function openAddDestinationModal() {
  openAddDestinationModalForDay(activeDay.value)
}

function openAddDestinationModalForDay(day) {
  if (!props.selectedPlan) return

  editingDestination.value = null

  destinationForm.value = {
    name: '',
    address: '',
    planName: '',
    day: day,
    duration: 60,
    notes: '',
    coordinates: null,
    place_id: null,
    content_id: null,
  }

  activeDay.value = day
  isDestinationModalOpen.value = true
}

function openEditDestinationModal(destination) {
  editingDestination.value = { ...destination } // Create a copy to avoid direct mutation
  destinationForm.value = {
    name: destination.name || '',
    address: destination.address || '',
    planName: destination.displayName || '',
    day: destination.day,
    duration: destination.duration || 60,
    notes: destination.notes || '',
    coordinates: destination.coordinates,
    place_id: destination.place_id,
    content_id: destination.content_id,
  }

  // Switch to the day of this destination
  activeDay.value = destination.day
  isDestinationModalOpen.value = true
}

function closeDestinationModal() {
  isDestinationModalOpen.value = false
  editingDestination.value = null
}

async function saveDestination() {
  if (!props.selectedPlan) return

  if (!isDestinationFormValid.value) return

  // Generate a default name if none is provided
  const name = destinationForm.value.name || '지역 여행지'

  // Use the planName if provided, otherwise use the place name
  const displayName = destinationForm.value.planName.trim() || name

  // Ensure we have a valid destination object with all required fields
  const destinationData = {
    ...destinationForm.value,
    name, // Ensure name is set
    displayName, // Add the display name
    time: null, // Time will be calculated later
  }

  if (editingDestination.value) {
    // Update existing destination
    const index = props.selectedPlan.destinations.findIndex(
      (d) => d.id === editingDestination.value.id,
    )
    if (index !== -1) {
      // Create a new array with the updated destination to ensure reactivity
      const updatedDestinations = [...props.selectedPlan.destinations]
      updatedDestinations[index] = {
        ...updatedDestinations[index],
        ...destinationData,
      }

      if (editingDestination.value.day !== destinationForm.value.day) {
        // Mark the previous day to recalculate times
        destinationData._previousDay = editingDestination.value.day
      }

      props.selectedPlan.destinations = updatedDestinations
    }
  } else {
    // Add new destination
    // Get the max order for the day
    const dayDestinations = props.selectedPlan.destinations.filter(
      (d) => d.day === destinationForm.value.day,
    )
    const maxOrder =
      dayDestinations.length > 0 ? Math.max(...dayDestinations.map((d) => d.order || 0)) : 0

    // Create a new array with the new destination to ensure reactivity
    props.selectedPlan.destinations = [
      ...props.selectedPlan.destinations,
      {
        id: Date.now(),
        order: maxOrder + 1,
        ...destinationData,
      },
    ]
  }

  // If the destination is for a different day, switch to that day
  if (destinationForm.value.day !== activeDay.value) {
    activeDay.value = destinationForm.value.day
  }

  // Update times based on new order
  updateAllDestinationTimes()

  // Calculate real travel times if Google Maps is loaded and destination has coordinates
  if (googleMapsLoaded.value && destinationData.coordinates) {
    await updateRealTimeTravelTimes(destinationForm.value.day)
  }

  closeDestinationModal()
  saveTravelPlans()
  updateMapMarkers()
  updateDayLists()
}

function confirmDeleteDestination(destination) {
  destinationToDelete.value = destination
  isConfirmDeleteDestinationOpen.value = true
}

function deleteDestination() {
  if (props.selectedPlan && destinationToDelete.value) {
    const day = destinationToDelete.value.day

    // Create a new array without the deleted destination to ensure reactivity
    props.selectedPlan.destinations = props.selectedPlan.destinations.filter(
      (d) => d.id !== destinationToDelete.value.id,
    )

    // Reorder the remaining destinations for this day
    const dayDestinations = props.selectedPlan.destinations.filter((d) => d.day === day)
    dayDestinations.forEach((dest, index) => {
      dest.order = index + 1
    })

    isConfirmDeleteDestinationOpen.value = false
    destinationToDelete.value = null

    // Update times based on new list
    updateAllDestinationTimes()
    saveTravelPlans()

    updateMapMarkers()
    updateDayLists()
  }
}

async function updateRealTimeTravelTimes(day) {
  if (!props.selectedPlan?.destinations || !googleMapsLoaded.value) return

  const dayDestinations = props.selectedPlan.destinations
    .filter((d) => d.day === day)
    .sort((a, b) => a.order - b.order)

  // Get start and end coordinates for the day
  const startCoords = getDayStartCoordinates(day)
  const endCoords = getDayEndCoordinates(day)

  if (!startCoords) return

  // Calculate travel times for all segments
  for (let i = 0; i <= dayDestinations.length; i++) {
    let fromLat, fromLng, toLat, toLng, targetDest

    if (i === 0 && dayDestinations.length > 0) {
      // Start location to first destination
      fromLat = startCoords.lat
      fromLng = startCoords.lng
      toLat = dayDestinations[0].coordinates?.lat
      toLng = dayDestinations[0].coordinates?.lng
      targetDest = dayDestinations[0]

      console.log(`Calculating: Start (${fromLat}, ${fromLng}) → First Dest (${toLat}, ${toLng})`)
    } else if (i > 0 && i < dayDestinations.length) {
      // Between destinations
      const prevDest = dayDestinations[i - 1]
      const currentDest = dayDestinations[i]
      fromLat = prevDest.coordinates?.lat
      fromLng = prevDest.coordinates?.lng
      toLat = currentDest.coordinates?.lat
      toLng = currentDest.coordinates?.lng
      targetDest = currentDest

      console.log(
        `Calculating: ${prevDest.name} (${fromLat}, ${fromLng}) → ${currentDest.name} (${toLat}, ${toLng})`,
      )
    } else if (i === dayDestinations.length && dayDestinations.length > 0 && endCoords) {
      // Last destination to end location
      const lastDest = dayDestinations[dayDestinations.length - 1]
      fromLat = lastDest.coordinates?.lat
      fromLng = lastDest.coordinates?.lng
      toLat = endCoords.lat
      toLng = endCoords.lng
      targetDest = lastDest

      console.log(`Calculating: Last Dest (${fromLat}, ${fromLng}) → End (${toLat}, ${toLng})`)
    } else if (dayDestinations.length === 0 && endCoords) {
      // Direct from start to end (no destinations)
      fromLat = startCoords.lat
      fromLng = startCoords.lng
      toLat = endCoords.lat
      toLng = endCoords.lng
      targetDest = null

      console.log(`Calculating: Start (${fromLat}, ${fromLng}) → End (${toLat}, ${toLng}) [Direct]`)
    } else {
      continue
    }

    if (fromLat && fromLng && toLat && toLng) {
      // Check if start and end coordinates are the same (within a small tolerance)
      const latDiff = Math.abs(fromLat - toLat)
      const lngDiff = Math.abs(fromLng - toLng)
      const tolerance = 0.0001 // About 10 meters

      if (latDiff < tolerance && lngDiff < tolerance) {
        console.log(`⏭️ Skipping API call: Same location (${fromLat}, ${fromLng})`)

        // Set travel time to 0 for same location
        if (targetDest) {
          if (i === 0) {
            targetDest.travelTimeFromStart = 0
          } else if (i === dayDestinations.length) {
            targetDest.travelTimeToEnd = 0
          } else {
            targetDest.realTravelTime = 0
          }
        }
        continue
      }

      try {
        const travelTimeResult = await travelTimeService.calculateRealTravelTime(
          fromLat,
          fromLng,
          toLat,
          toLng,
          'TRANSIT',
        )

        if (travelTimeResult && !travelTimeResult.error && travelTimeResult.duration) {
          const travelMinutes = Math.round(travelTimeResult.duration / 60)

          if (i === 0 && targetDest) {
            // Store travel time to first destination
            targetDest.travelTimeFromStart = travelMinutes
            console.log(`✅ Start → First: ${travelMinutes} minutes`)
          } else if (i > 0 && i < dayDestinations.length && targetDest) {
            // Store travel time from previous destination
            targetDest.realTravelTime = travelMinutes
            console.log(`✅ Dest → Dest: ${travelMinutes} minutes`)
          } else if (i === dayDestinations.length && targetDest) {
            // Store travel time to end location
            targetDest.travelTimeToEnd = travelMinutes
            console.log(`✅ Last → End: ${travelMinutes} minutes`)
          }
        } else {
          console.log(`❌ API Error:`, travelTimeResult?.error || 'Unknown error')
          if (targetDest) {
            if (i === 0) {
              targetDest.travelTimeFromStart = 'N/A'
            } else if (i === dayDestinations.length) {
              targetDest.travelTimeToEnd = 'N/A'
            } else {
              targetDest.realTravelTime = 'N/A'
            }
          }
        }

        // Add delay between requests to avoid rate limiting
        await new Promise((resolve) => setTimeout(resolve, 300))
      } catch (error) {
        console.error('Failed to calculate travel time:', error)
        if (targetDest) {
          if (i === 0) {
            targetDest.travelTimeFromStart = 'N/A'
          } else if (i === dayDestinations.length) {
            targetDest.travelTimeToEnd = 'N/A'
          } else {
            targetDest.realTravelTime = 'N/A'
          }
        }
      }
    }
  }

  // Recalculate all destination times based on new travel times
  updateAllDestinationTimesWithRealData(day)
}

function updateMapMarkers() {
  // Clear existing markers and polylines
  if (window.koreaMapMarkers) {
    window.koreaMapMarkers.forEach((marker) => marker.setMap(null))
    window.koreaMapMarkers = []
  }

  if (window.koreaMapPolylines) {
    window.koreaMapPolylines.forEach((polyline) => polyline.setMap(null))
    window.koreaMapPolylines = []
  }

  if (window.koreaMapInfoWindows) {
    window.koreaMapInfoWindows.forEach((infoWindow) => infoWindow.close())
    window.koreaMapInfoWindows = []
  }

  // If no plan is selected or no Google Maps, return
  if (!props.selectedPlan || !window.google || !window.koreaMap) return

  // Initialize arrays for markers and polylines
  window.koreaMapMarkers = []
  window.koreaMapPolylines = []
  window.koreaMapInfoWindows = []

  // Get destinations for the active day
  const dayDestinations = props.selectedPlan.destinations
    .filter((d) => d.day === activeDay.value)
    .sort((a, b) => (a.order || 0) - (b.order || 0))

  // Get start and end locations for the day
  const startLocation = getDayStartLocation(activeDay.value)
  const endLocation = getDayEndLocation(activeDay.value)

  // Create all points for the day (start + destinations + end)
  const allPoints = []

  // Add start point
  const startCoordinates = getDayStartCoordinates(activeDay.value)
  if (startCoordinates) {
    allPoints.push({
      coordinates: startCoordinates,
      name: startLocation,
      type: 'start',
      index: 0,
      memo: `${activeDay.value}일차 출발 지점`,
    })
  }

  // Add destination points
  dayDestinations.forEach((dest, index) => {
    if (dest.coordinates) {
      allPoints.push({
        coordinates: dest.coordinates,
        name: dest.displayName || dest.name,
        type: 'destination',
        index: index + 1,
        memo: dest.notes || '',
        duration: dest.duration,
      })
    }
  })

  // Add end point (only if different from last destination)
  const endCoordinates = getDayEndCoordinates(activeDay.value)
  if (endCoordinates) {
    // Always add end point, even if it's the same as the last destination
    // This ensures we always have a complete route
    allPoints.push({
      coordinates: endCoordinates,
      name: endLocation,
      type: 'end',
      index: allPoints.length,
      memo: `${activeDay.value}일차 도착 지점`,
    })
  }

  // Create markers for all points
  allPoints.forEach((point, index) => {
    let markerIcon
    let labelText

    if (point.type === 'start') {
      markerIcon = {
        path: window.google.maps.SymbolPath.CIRCLE,
        fillColor: '#4CAF50', // Green for start
        fillOpacity: 1,
        strokeWeight: 2,
        strokeColor: '#ffffff',
        scale: 12,
      }
      labelText = 'S'
    } else if (point.type === 'end') {
      markerIcon = {
        path: window.google.maps.SymbolPath.CIRCLE,
        fillColor: '#f44336', // Red for end
        fillOpacity: 1,
        strokeWeight: 2,
        strokeColor: '#ffffff',
        scale: 12,
      }
      labelText = 'E'
    } else {
      markerIcon = {
        path: window.google.maps.SymbolPath.CIRCLE,
        fillColor: '#005BAC', // Blue for destinations
        fillOpacity: 1,
        strokeWeight: 2,
        strokeColor: '#ffffff',
        scale: 14,
      }
      labelText = point.index.toString()
    }

    const marker = new window.google.maps.Marker({
      position: point.coordinates,
      map: window.koreaMap,
      title: point.name,
      label: {
        text: labelText,
        color: 'white',
        fontWeight: 'bold',
      },
      icon: markerIcon,
    })

    // Create info window for this marker
    const infoContent = `
      <div style="padding: 10px; max-width: 300px;">
        <h3 style="margin-top: 0; color: #005BAC; font-size: 16px;">${point.name}</h3>
        ${point.type === 'destination' ? `<p style="margin: 5px 0;"><strong>소요 시간:</strong> ${formatDuration(point.duration)}</p>` : ''}
        ${point.memo ? `<p style="margin: 5px 0;">${point.memo}</p>` : ''}
        ${point.type === 'start' ? '<p style="color: #4CAF50; font-weight: bold;">출발 지점</p>' : ''}
        ${point.type === 'end' ? '<p style="color: #f44336; font-weight: bold;">도착 지점</p>' : ''}
      </div>
    `

    const infoWindow = new window.google.maps.InfoWindow({
      content: infoContent,
    })

    window.koreaMapInfoWindows.push(infoWindow)

    // Add click listener to marker
    marker.addListener('click', () => {
      // Close all other info windows
      window.koreaMapInfoWindows.forEach((iw) => iw.close())

      // Open this info window
      infoWindow.open(window.koreaMap, marker)

      // Center map on this marker with animation
      window.koreaMap.panTo(point.coordinates)
    })

    window.koreaMapMarkers.push(marker)
  })

  // Draw route lines between all points
  if (allPoints.length > 1) {
    for (let i = 0; i < allPoints.length - 1; i++) {
      const from = allPoints[i].coordinates
      const to = allPoints[i + 1].coordinates

      // Create the polyline with arrow symbols
      const polyline = new window.google.maps.Polyline({
        path: [from, to],
        geodesic: true,
        strokeColor: '#FF5454',
        strokeOpacity: 0.8,
        strokeWeight: 3,
        map: window.koreaMap,
        icons: [
          {
            icon: {
              path: window.google.maps.SymbolPath.FORWARD_CLOSED_ARROW,
              scale: 3,
              strokeColor: '#FF5454',
              fillColor: '#FF5454',
              fillOpacity: 1,
            },
            offset: '50%',
            repeat: '100px',
          },
        ],
      })

      window.koreaMapPolylines.push(polyline)
    }
  }

  // If there are points, fit the map to show all markers
  if (allPoints.length > 0) {
    const bounds = new window.google.maps.LatLngBounds()
    allPoints.forEach((point) => {
      bounds.extend(point.coordinates)
    })
    window.koreaMap.fitBounds(bounds)

    // Add some padding
    const padding = { top: 50, right: 50, bottom: 50, left: 50 }
    window.koreaMap.fitBounds(bounds, padding)
  }
}

// Add this function after updateMapMarkers()
function showAllRoutes() {
  // Clear existing markers and polylines
  if (window.koreaMapMarkers) {
    window.koreaMapMarkers.forEach((marker) => marker.setMap(null))
    window.koreaMapMarkers = []
  }

  if (window.koreaMapPolylines) {
    window.koreaMapPolylines.forEach((polyline) => polyline.setMap(null))
    window.koreaMapPolylines = []
  }

  if (window.koreaMapInfoWindows) {
    window.koreaMapInfoWindows.forEach((infoWindow) => infoWindow.close())
    window.koreaMapInfoWindows = []
  }

  // If no plan is selected or no Google Maps, return
  if (!props.selectedPlan || !window.google || !window.koreaMap) return

  // Initialize arrays for markers and polylines
  window.koreaMapMarkers = []
  window.koreaMapPolylines = []
  window.koreaMapInfoWindows = []

  // Define colors for different days
  const dayColors = [
    '#4CAF50', // Green
    '#2196F3', // Blue
    '#9C27B0', // Purple
    '#FF9800', // Orange
    '#F44336', // Red
    '#009688', // Teal
    '#795548', // Brown
    '#607D8B', // Blue Grey
    '#E91E63', // Pink
    '#FFEB3B', // Yellow
  ]

  // Get all days
  const totalDays = totalDaysComputed.value
  const allPoints = []

  // Process each day
  for (let day = 1; day <= totalDays; day++) {
    // Get color for this day (cycle through colors if more days than colors)
    const dayColor = dayColors[(day - 1) % dayColors.length]

    // Get destinations for this day
    const dayDestinations = props.selectedPlan.destinations
      .filter((d) => d.day === day)
      .sort((a, b) => (a.order || 0) - (b.order || 0))

    // Get start and end locations for the day
    const startLocation = getDayStartLocation(day)
    const endLocation = getDayEndLocation(day)

    // Create points for this day
    const dayPoints = []

    // Add start point
    const startCoordinates = getDayStartCoordinates(day)
    if (startCoordinates) {
      dayPoints.push({
        coordinates: startCoordinates,
        name: startLocation,
        type: 'start',
        day: day,
        index: 0,
        memo: `${day}일차 출발 지점`,
        color: dayColor,
      })
    }

    // Add destination points
    dayDestinations.forEach((dest, index) => {
      if (dest.coordinates) {
        dayPoints.push({
          coordinates: dest.coordinates,
          name: dest.displayName || dest.name,
          type: 'destination',
          day: day,
          index: index + 1,
          memo: dest.notes || '',
          duration: dest.duration,
          color: dayColor,
        })
      }
    })

    // Add end point
    const endCoordinates = getDayEndCoordinates(day)
    if (endCoordinates) {
      dayPoints.push({
        coordinates: endCoordinates,
        name: endLocation,
        type: 'end',
        day: day,
        index: dayPoints.length,
        memo: `${day}일차 도착 지점`,
        color: dayColor,
      })
    }

    // Add all points from this day to the overall points array
    allPoints.push(...dayPoints)

    // Draw route lines for this day with the day's color
    if (dayPoints.length > 1) {
      for (let i = 0; i < dayPoints.length - 1; i++) {
        const from = dayPoints[i].coordinates
        const to = dayPoints[i + 1].coordinates

        // Create the polyline with arrow symbols
        const polyline = new window.google.maps.Polyline({
          path: [from, to],
          geodesic: true,
          strokeColor: dayColor,
          strokeOpacity: 0.8,
          strokeWeight: 3,
          map: window.koreaMap,
          icons: [
            {
              icon: {
                path: window.google.maps.SymbolPath.FORWARD_CLOSED_ARROW,
                scale: 3,
                strokeColor: dayColor,
                fillColor: dayColor,
                fillOpacity: 1,
              },
              offset: '50%',
              repeat: '100px',
            },
          ],
        })

        window.koreaMapPolylines.push(polyline)
      }
    }
  }

  // Create markers for all points
  allPoints.forEach((point) => {
    let markerIcon
    let labelText

    if (point.type === 'start') {
      markerIcon = {
        path: window.google.maps.SymbolPath.CIRCLE,
        fillColor: point.color, // Use day color
        fillOpacity: 1,
        strokeWeight: 2,
        strokeColor: '#ffffff',
        scale: 12,
      }
      labelText = `${point.day}S`
    } else if (point.type === 'end') {
      markerIcon = {
        path: window.google.maps.SymbolPath.CIRCLE,
        fillColor: point.color, // Use day color
        fillOpacity: 1,
        strokeWeight: 2,
        strokeColor: '#ffffff',
        scale: 12,
      }
      labelText = `${point.day}E`
    } else {
      markerIcon = {
        path: window.google.maps.SymbolPath.CIRCLE,
        fillColor: point.color, // Use day color
        fillOpacity: 1,
        strokeWeight: 2,
        strokeColor: '#ffffff',
        scale: 14,
      }
      labelText = `${point.day}-${point.index}`
    }

    const marker = new window.google.maps.Marker({
      position: point.coordinates,
      map: window.koreaMap,
      title: `${point.day}일차: ${point.name}`,
      label: {
        text: labelText,
        color: 'white',
        fontWeight: 'bold',
      },
      icon: markerIcon,
    })

    // Create info window for this marker
    const infoContent = `
      <div style="padding: 10px; max-width: 300px;">
        <h3 style="margin-top: 0; color: ${point.color}; font-size: 16px;">${point.day}일차: ${point.name}</h3>
        ${point.type === 'destination' ? `<p style="margin: 5px 0;"><strong>소요 시간:</strong> ${formatDuration(point.duration)}</p>` : ''}
        ${point.memo ? `<p style="margin: 5px 0;">${point.memo}</p>` : ''}
        ${point.type === 'start' ? '<p style="font-weight: bold;">출발 지점</p>' : ''}
        ${point.type === 'end' ? '<p style="font-weight: bold;">도착 지점</p>' : ''}
      </div>
    `

    const infoWindow = new window.google.maps.InfoWindow({
      content: infoContent,
    })

    window.koreaMapInfoWindows.push(infoWindow)

    // Add click listener to marker
    marker.addListener('click', () => {
      // Close all other info windows
      window.koreaMapInfoWindows.forEach((iw) => iw.close())

      // Open this info window
      infoWindow.open(window.koreaMap, marker)

      // Center map on this marker with animation
      window.koreaMap.panTo(point.coordinates)
    })

    window.koreaMapMarkers.push(marker)
  })

  // If there are points, fit the map to show all markers
  if (allPoints.length > 0) {
    const bounds = new window.google.maps.LatLngBounds()
    allPoints.forEach((point) => {
      bounds.extend(point.coordinates)
    })
    window.koreaMap.fitBounds(bounds)

    // Add some padding
    const padding = { top: 50, right: 50, bottom: 50, left: 50 }
    window.koreaMap.fitBounds(bounds, padding)
  }
}

// Add a new function to handle the "전체보기" button click
function showEntireRoute() {
  showAllRoutes()
}

// Add these new functions for consistent map focusing behavior
function focusOnStartLocation(day) {
  const coordinates = getDayStartCoordinates(day)
  if (coordinates && window.koreaMap) {
    // Center map on the start location with smooth animation
    window.koreaMap.panTo(coordinates)
    window.koreaMap.setZoom(16)

    // Find and trigger the marker click event
    if (window.koreaMapMarkers) {
      const marker = window.koreaMapMarkers.find(
        (m) =>
          Math.abs(m.getPosition().lat() - coordinates.lat) < 0.0001 &&
          Math.abs(m.getPosition().lng() - coordinates.lng) < 0.0001,
      )

      if (marker) {
        // Small delay to ensure map has moved before opening info window
        setTimeout(() => {
          window.google.maps.event.trigger(marker, 'click')
        }, 300)
      }
    }
  }
}

function focusOnEndLocation(day) {
  const coordinates = getDayEndCoordinates(day)
  if (coordinates && window.koreaMap) {
    // Center map on the end location with smooth animation
    window.koreaMap.panTo(coordinates)
    window.koreaMap.setZoom(16)

    // Find and trigger the marker click event
    if (window.koreaMapMarkers) {
      const marker = window.koreaMapMarkers.find(
        (m) =>
          Math.abs(m.getPosition().lat() - coordinates.lat) < 0.0001 &&
          Math.abs(m.getPosition().lng() - coordinates.lng) < 0.0001,
      )

      if (marker) {
        // Small delay to ensure map has moved before opening info window
        setTimeout(() => {
          window.google.maps.event.trigger(marker, 'click')
        }, 300)
      }
    }
  }
}

function focusOnDestination(destination) {
  if (destination.coordinates && window.koreaMap) {
    // Center map on the destination with smooth animation
    window.koreaMap.panTo(destination.coordinates)
    window.koreaMap.setZoom(16)

    // Find the marker for this destination and trigger its click event
    if (window.koreaMapMarkers) {
      const marker = window.koreaMapMarkers.find(
        (m) =>
          Math.abs(m.getPosition().lat() - destination.coordinates.lat) < 0.0001 &&
          Math.abs(m.getPosition().lng() - destination.coordinates.lng) < 0.0001,
      )

      if (marker) {
        // Small delay to ensure map has moved before opening info window
        setTimeout(() => {
          window.google.maps.event.trigger(marker, 'click')
        }, 300)
      }
    }
  } else {
    // If no coordinates, show a message
    alert('이 장소의 위치 정보가 없습니다. 장소를 다시 선택해주세요.')
  }
}

// Helper function to get day start coordinates
function getDayStartCoordinates(day) {
  if (!props.selectedPlan) return null

  // Check if we have specific coordinates for this day
  if (
    props.selectedPlan.dayLocations &&
    props.selectedPlan.dayLocations[day] &&
    props.selectedPlan.dayLocations[day].startCoordinates
  ) {
    return props.selectedPlan.dayLocations[day].startCoordinates
  }

  // For the first day, use the plan's start coordinates
  if (day === 1) {
    return props.selectedPlan.startCoordinates
  }

  // For other days, use the previous day's end coordinates
  return getDayEndCoordinates(day - 1)
}

// Helper function to get day end coordinates
function getDayEndCoordinates(day) {
  if (!props.selectedPlan) return null

  // Check if we have specific coordinates for this day
  if (
    props.selectedPlan.dayLocations &&
    props.selectedPlan.dayLocations[day] &&
    props.selectedPlan.dayLocations[day].endCoordinates
  ) {
    return props.selectedPlan.dayLocations[day].endCoordinates
  }

  // Find the last destination of the current day
  const dayDestinations = props.selectedPlan.destinations.filter((d) => d.day === day)

  if (dayDestinations.length > 0) {
    // Sort by order and get the last one
    dayDestinations.sort((a, b) => a.order - b.order)
    const lastDest = dayDestinations[dayDestinations.length - 1]
    if (lastDest.coordinates) {
      return lastDest.coordinates
    }
  }

  // For the last day, use the plan's end coordinates
  if (day === totalDaysComputed.value) {
    return props.selectedPlan.endCoordinates || props.selectedPlan.startCoordinates
  }

  // For other days with no destinations, use the start coordinates
  return getDayStartCoordinates(day)
}

// Function to highlight a specific day on the map
function highlightDayOnMap(day) {
  // Switch to the selected day
  activeDay.value = day

  // Update map markers for the selected day
  updateMapMarkers()

  // Add a subtle highlight effect to the day column
  nextTick(() => {
    // Remove existing highlights
    document.querySelectorAll('.day-column').forEach((col) => {
      col.classList.remove('map-highlighted')
    })

    // Add highlight to the selected day
    const dayColumn = document.querySelector(`.day-column:nth-child(${day})`)
    if (dayColumn) {
      dayColumn.classList.add('map-highlighted')

      // Remove highlight after 2 seconds
      setTimeout(() => {
        dayColumn.classList.remove('map-highlighted')
      }, 2000)
    }
  })
}

// Local storage functions
function saveTravelPlans() {
  //localStorage.setItem('travelPlans', JSON.stringify(props.travelPlans))
}

function togglePlanDetail() {
  isPlanDetailOpen.value = !isPlanDetailOpen.value

  // Update map when sidebar is toggled
  nextTick(() => {
    updateMapMarkers()
  })
}

function handlePlanDetailResize(newWidth) {
  planDetailWidth.value = newWidth

  // Update map when sidebar is resized
  nextTick(() => {
    updateMapMarkers()
  })
}

// Watch for changes in the active day
watch(
  () => activeDay.value,
  () => {
    updateMapMarkers()
  },
)

// Update the watch for changes in the selected plan
watch(
  () => props.selectedPlan,
  (newPlan) => {
    if (newPlan) {
      // Store original data for cancel functionality
      originalPlanData.value = JSON.parse(JSON.stringify(newPlan))

      activeDay.value = 1
      isPlanDetailOpen.value = true
      nextTick(() => {
        showAllRoutes() // Show all routes instead of just the active day
      })
    }
  },
)

// Watch for changes in the selected plan's date range
watch(
  () => [props.selectedPlan?.startDate, props.selectedPlan?.endDate],
  () => {
    if (props.selectedPlan) {
      nextTick(() => {
        updateDayLists()
      })
    }
  },
  { immediate: true, deep: true },
)

// Remove the old getLocationTravelTime function and replace with:
async function getLocationTravelTime(fromLocation, toLocation, fromCoords, toCoords) {
  // If we have coordinates and Google Maps is loaded, use real API
  if (googleMapsLoaded.value && fromCoords && toCoords && window.google) {
    try {
      const travelTimeResult = await travelTimeService.calculateRealTravelTime(
        fromCoords.lat,
        fromCoords.lng,
        toCoords.lat,
        toCoords.lng,
        'TRANSIT',
      )

      if (travelTimeResult && !travelTimeResult.error && travelTimeResult.duration) {
        return Math.round(travelTimeResult.duration / 60) // Convert seconds to minutes
      }
    } catch (error) {
      console.error('Failed to calculate travel time:', error)
    }
  }

  // Return null for N/A display
  return null
}

// Remove the old getTravelTimeBetween function and replace with:
async function getTravelTimeBetween(fromDest, toDest) {
  // If either destination doesn't have coordinates, return null for N/A
  if (!fromDest.coordinates || !toDest.coordinates) {
    return null
  }

  // If we have real travel time data, use it
  if (toDest.realTravelTime && toDest.realTravelTime !== 'N/A') {
    return toDest.realTravelTime
  }

  // Try to get real travel time
  if (googleMapsLoaded.value && window.google) {
    try {
      const travelTimeResult = await travelTimeService.calculateRealTravelTime(
        fromDest.coordinates.lat,
        fromDest.coordinates.lng,
        toDest.coordinates.lat,
        toDest.coordinates.lng,
        'TRANSIT',
      )

      if (travelTimeResult && !travelTimeResult.error && travelTimeResult.duration) {
        return Math.round(travelTimeResult.duration / 60)
      }
    } catch (error) {
      console.error('Failed to calculate travel time:', error)
    }
  }

  // Return null for N/A display
  return null
}

// updateAllDestinationTimes 함수를 추가합니다 (getTravelTimeBetween 함수 다음에)
async function updateAllDestinationTimes() {
  if (!props.selectedPlan) return

  // Group destinations by day
  const dayGroups = {}
  props.selectedPlan.destinations.forEach((dest) => {
    if (!dayGroups[dest.day]) {
      dayGroups[dest.day] = []
    }
    dayGroups[dest.day].push(dest)
  })

  // Sort each day's destinations by order
  Object.keys(dayGroups).forEach((day) => {
    dayGroups[day].sort((a, b) => (a.order || 0) - (b.order || 0))
  })

  // Update times for each day
  for (const day of Object.keys(dayGroups)) {
    const dayNum = Number(day)

    // Get the day's start time
    const dayStartTime = getDayStartTime(dayNum)
    const [startHours, startMinutes] = dayStartTime.split(':').map(Number)

    // Get coordinates for start and end locations
    const startCoords = getDayStartCoordinates(dayNum)
    const endCoords = getDayEndCoordinates(dayNum)

    // If there are destinations for this day
    if (dayGroups[day].length > 0) {
      let currentTimeMinutes = startHours * 60 + startMinutes

      for (let index = 0; index < dayGroups[day].length; index++) {
        const dest = dayGroups[day][index]

        // Calculate travel time to this destination
        let travelTime = null
        if (index === 0) {
          // Travel time from start location to first destination
          if (startCoords && dest.coordinates) {
            travelTime = await getLocationTravelTime(
              getDayStartLocation(dayNum),
              dest.displayName || dest.name,
              startCoords,
              dest.coordinates,
            )
          }
        } else {
          // Travel time from previous destination
          const prevDest = dayGroups[day][index - 1]
          if (prevDest.coordinates && dest.coordinates) {
            travelTime = await getTravelTimeBetween(prevDest, dest)
          }
        }

        // Add travel time if available
        if (travelTime !== null) {
          currentTimeMinutes += travelTime
        }

        // Set the destination arrival time
        const destHours = Math.floor(currentTimeMinutes / 60)
        const destMinutes = currentTimeMinutes % 60
        dest.time = `${destHours.toString().padStart(2, '0')}:${destMinutes.toString().padStart(2, '0')}`

        // Add duration for this destination
        const duration = Number(dest.duration || 60)
        currentTimeMinutes += duration
      }
    }
  }
}

function updateAllDestinationTimesWithRealData(day) {
  if (!props.selectedPlan) return

  const dayDestinations = props.selectedPlan.destinations
    .filter((d) => d.day === day)
    .sort((a, b) => a.order - b.order)

  if (dayDestinations.length === 0) return

  // Get the day's start time
  const dayStartTime = getDayStartTime(day)
  const [startHours, startMinutes] = dayStartTime.split(':').map(Number)
  let currentTimeMinutes = startHours * 60 + startMinutes

  dayDestinations.forEach((dest, index) => {
    // Add travel time to reach this destination
    if (index === 0) {
      // Travel time from start location
      const travelTime =
        dest.travelTimeFromStart !== 'N/A' && dest.travelTimeFromStart
          ? dest.travelTimeFromStart
          : getLocationTravelTime(getDayStartLocation(day), dest.displayName || dest.name)
      currentTimeMinutes += travelTime
    } else {
      // Travel time from previous destination
      const prevDest = dayDestinations[index - 1]
      const travelTime =
        dest.realTravelTime !== 'N/A' && dest.realTravelTime
          ? dest.realTravelTime
          : getTravelTimeBetween(prevDest, dest)
      currentTimeMinutes += travelTime
    }

    // Set the destination arrival time
    const destHours = Math.floor(currentTimeMinutes / 60)
    const destMinutes = currentTimeMinutes % 60
    dest.time = `${destHours.toString().padStart(2, '0')}:${destMinutes.toString().padStart(2, '0')}`

    // Add duration for this destination
    const duration = Number(dest.duration || 60)
    currentTimeMinutes += duration
  })
}

// 드래그 앤 드롭 관련 함수들을 수정합니다
// onDragStart 함수를 다음과 같이 수정:
function onDragStart(event, destination) {
  console.log('Drag started for:', destination.displayName || destination.name)
  event.dataTransfer.effectAllowed = 'move'

  // JSON 객체를 문자열로 변환하여 설정
  const dragData = {
    id: destination.id,
    sourceDay: destination.day,
    name: destination.displayName || destination.name,
    address: destination.address,
    coordinates: destination.coordinates,
    duration: destination.duration,
    place_id: destination.place_id,
    content_id: destination.content_id,
  }

  event.dataTransfer.setData('text/plain', JSON.stringify(dragData))

  // Add visual feedback
  event.target.classList.add('dragging')

  // Store the dragged element reference
  window.draggedElement = destination
}

// onDragEnd 함수를 다음과 같이 수정:
function onDragEnd(event) {
  event.target.classList.remove('dragging')
  window.draggedElement = null
}

// 드롭 존 이벤트 핸들러들을 추가합니다
function onDragOver(event) {
  event.preventDefault()
  event.dataTransfer.dropEffect = 'move'

  // Add visual feedback to drop zone
  const dropZone = event.currentTarget
  dropZone.classList.add('drag-over')
}

function onDragLeave(event) {
  // Remove visual feedback when leaving drop zone
  const dropZone = event.currentTarget
  dropZone.classList.remove('drag-over')
}

async function onDrop(event, targetDay) {
  event.preventDefault()

  try {
    const dataString = event.dataTransfer.getData('text/plain')
    console.log('Raw drag data:', dataString)

    let dragData
    try {
      dragData = JSON.parse(dataString)
    } catch (parseError) {
      console.error('Failed to parse drag data as JSON:', parseError)
      console.log('Data string:', dataString)
      return
    }

    const sourceDay = dragData.sourceDay
    const destinationId = dragData.id

    console.log('Parsed drag data:', dragData)

    if (sourceDay === targetDay) {
      return // Same day, no need to move
    }

    // Find the destination to move
    const destinationIndex = props.selectedPlan.destinations.findIndex(
      (d) => d.id === destinationId,
    )
    if (destinationIndex === -1) return

    const destination = props.selectedPlan.destinations[destinationIndex]

    // Store original day destinations before moving (for travel time recalculation)
    const originalSourceDayDests = props.selectedPlan.destinations
      .filter((d) => d.day === sourceDay && d.id !== destinationId)
      .sort((a, b) => a.order - b.order)

    const originalTargetDayDests = props.selectedPlan.destinations
      .filter((d) => d.day === targetDay)
      .sort((a, b) => a.order - b.order)

    // Remove from source day
    props.selectedPlan.destinations.splice(destinationIndex, 1)

    // Update destination day and order
    const targetDayDestinations = props.selectedPlan.destinations.filter((d) => d.day === targetDay)
    const newOrder = targetDayDestinations.length + 1

    destination.day = targetDay
    destination.order = newOrder

    // Add to target day
    props.selectedPlan.destinations.push(destination)

    // Reorder destinations in source day
    const sourceDayDestinations = props.selectedPlan.destinations.filter((d) => d.day === sourceDay)
    sourceDayDestinations.forEach((dest, index) => {
      dest.order = index + 1
    })

    // Update times and save
    await updateAllDestinationTimes()
    saveTravelPlans()
    updateMapMarkers()
    updateDayLists()

    // Update real-time travel times if Google Maps is loaded
    if (googleMapsLoaded.value) {
      console.log('Recalculating travel times for affected days...')

      // Calculate travel times for source day (where destination was removed)
      if (originalSourceDayDests.length > 0) {
        console.log(`Recalculating Day ${sourceDay} travel times (destination removed)`)
        await updateRealTimeTravelTimes(sourceDay)
      }

      // Calculate travel times for target day (where destination was added)
      console.log(`Recalculating Day ${targetDay} travel times (destination added)`)
      await updateRealTimeTravelTimes(targetDay)

      console.log('Travel time recalculation completed')
    }

    // Remove drag over styling
    const dropZone = event.currentTarget
    dropZone.classList.remove('drag-over')
  } catch (error) {
    console.error('Error handling drop:', error)
  }
}

// Add this function to display travel time from departure point to first destination
function getFirstDestinationTravelTime(day) {
  if (!props.selectedPlan) return 'N/A'

  const dayDests = props.selectedPlan.destinations.filter((d) => d.day === day)
  if (dayDests.length === 0) return 'N/A'

  // Sort by order
  dayDests.sort((a, b) => a.order - b.order)
  const firstDest = dayDests[0]

  // Check if we have real travel time data from start
  if (firstDest.travelTimeFromStart && firstDest.travelTimeFromStart !== 'N/A') {
    return formatDuration(firstDest.travelTimeFromStart)
  }

  // Return N/A if no real data
  return 'N/A'
}

function getLastDestinationTravelTime(day) {
  if (!props.selectedPlan) return 'N/A'

  const dayDests = props.selectedPlan.destinations.filter((d) => d.day === day)
  if (dayDests.length === 0) return 'N/A'

  // Sort by order
  dayDests.sort((a, b) => a.order - b.order)
  const lastDest = dayDests[dayDests.length - 1]

  // Check if we have real travel time data to end
  if (lastDest.travelTimeToEnd && lastDest.travelTimeToEnd !== 'N/A') {
    return formatDuration(lastDest.travelTimeToEnd)
  }

  // Return N/A if no real data
  return 'N/A'
}

function getTotalDayTravelTime(day) {
  // For days with no destinations, return N/A until we calculate real time
  return 'N/A'
}

// Function to select location for a destination
function openPlaceSelector() {
  // Set the current selection target
  currentSelectionTarget.value = 'destination'
  // Show the place selection sidebar
  isPlaceSelectionActive.value = true
}

// Handle the selected place from the sidebar
function handlePlaceSelected(place) {
  if (currentSelectionTarget.value === 'plan') {
    // Update plan form
    planForm.value.location = place.address
    planForm.value.startCoordinates = { lat: place.latitude, lng: place.longitude }
    planForm.value.endCoordinates = { lat: place.latitude, lng: place.longitude } // Same as start for now
    planForm.value.start_location_title = place.name
    planForm.value.end_location_title = place.name
    planForm.value.start_place_id = place.place_id || ''
    planForm.value.start_content_id = place.content_id || ''
    planForm.value.end_place_id = place.place_id || ''
    planForm.value.end_content_id = place.content_id || ''
  } else if (currentSelectionTarget.value === 'destination') {
    // Update destination form
    destinationForm.value.name = place.name
    destinationForm.value.address = place.address
    destinationForm.value.coordinates = { lat: place.latitude, lng: place.longitude }
    destinationForm.value.place_id = place.place_id
    destinationForm.value.content_id = place.content_id
  } else if (currentSelectionTarget.value && currentSelectionTarget.value.type === 'dayStart') {
    const day = currentSelectionTarget.value.day
    const newLocation = place.name

    if (newLocation && newLocation.trim()) {
      // Update the start location for this day
      if (!props.selectedPlan.dayLocations) {
        props.selectedPlan.dayLocations = {}
      }

      if (!props.selectedPlan.dayLocations[day]) {
        props.selectedPlan.dayLocations[day] = {}
      }

      props.selectedPlan.dayLocations[day].start_location_title = newLocation
      props.selectedPlan.dayLocations[day].startCoordinates = {
        lat: place.latitude,
        lng: place.longitude,
      }
      props.selectedPlan.dayLocations[day].start_place_id = place.place_id || ''
      props.selectedPlan.dayLocations[day].start_content_id = place.content_id || ''

      // If this is not the first day, update the end location of the previous day
      if (day > 1) {
        if (!props.selectedPlan.dayLocations[day - 1]) {
          props.selectedPlan.dayLocations[day - 1] = {}
        }
        props.selectedPlan.dayLocations[day - 1].end_location_title = newLocation
        props.selectedPlan.dayLocations[day - 1].endCoordinates = {
          lat: place.latitude,
          lng: place.longitude,
        }
        props.selectedPlan.dayLocations[day - 1].end_place_id = place.place_id || ''
        props.selectedPlan.dayLocations[day - 1].end_content_id = place.content_id || ''
      }

      // Update all consecutive days' start and end locations
      for (let i = day; i <= totalDaysComputed.value; i++) {
        if (!props.selectedPlan.dayLocations[i]) {
          props.selectedPlan.dayLocations[i] = {}
        }
        props.selectedPlan.dayLocations[i].start_location_title = newLocation
        props.selectedPlan.dayLocations[i].startCoordinates = {
          lat: place.latitude,
          lng: place.longitude,
        }
        props.selectedPlan.dayLocations[i].start_place_id = place.place_id || ''
        props.selectedPlan.dayLocations[i].start_content_id = place.content_id || ''
        props.selectedPlan.dayLocations[i].end_location_title = newLocation
        props.selectedPlan.dayLocations[i].endCoordinates = {
          lat: place.latitude,
          lng: place.longitude,
        }
        props.selectedPlan.dayLocations[i].end_place_id = place.place_id || ''
        props.selectedPlan.dayLocations[i].end_content_id = place.content_id || ''
      }

      // If this is the last day, update the plan's end location
      if (day === totalDaysComputed.value) {
        props.selectedPlan.end_location_title = newLocation
        props.selectedPlan.endCoordinates = { lat: place.latitude, lng: place.longitude }
        props.selectedPlan.end_place_id = place.place_id || ''
        props.selectedPlan.end_content_id = place.content_id || ''
      }

      // Save changes
      saveTravelPlans()

      // Inform the user
      setTimeout(() => {
        alert(
          `${day}일차의 출발 지점이 변경되었습니다.\n\n변경된 사항:\n- ${day - 1 > 0 ? day - 1 + '일차 도착 지점\n- ' : ''}${day}일차 도착 지점\n- 이후 모든 일차의 출발/도착 지점`,
        )
      }, 300)
    }
  } else if (currentSelectionTarget.value && currentSelectionTarget.value.type === 'dayEnd') {
    const day = currentSelectionTarget.value.day
    const newLocation = place.name

    if (newLocation && newLocation.trim()) {
      // Update the end location for this day
      if (!props.selectedPlan.dayLocations) {
        props.selectedPlan.dayLocations = {}
      }

      if (!props.selectedPlan.dayLocations[day]) {
        props.selectedPlan.dayLocations[day] = {}
      }

      props.selectedPlan.dayLocations[day].end_location_title = newLocation
      props.selectedPlan.dayLocations[day].endCoordinates = {
        lat: place.latitude,
        lng: place.longitude,
      }
      props.selectedPlan.dayLocations[day].end_place_id = place.place_id || ''
      props.selectedPlan.dayLocations[day].end_content_id = place.content_id || ''

      // Update all consecutive days' start and end locations (from next day)
      for (let i = day + 1; i <= totalDaysComputed.value; i++) {
        if (!props.selectedPlan.dayLocations[i]) {
          props.selectedPlan.dayLocations[i] = {}
        }
        props.selectedPlan.dayLocations[i].start_location_title = newLocation
        props.selectedPlan.dayLocations[i].startCoordinates = {
          lat: place.latitude,
          lng: place.longitude,
        }
        props.selectedPlan.dayLocations[i].start_place_id = place.place_id || ''
        props.selectedPlan.dayLocations[i].start_content_id = place.content_id || ''
        props.selectedPlan.dayLocations[i].end_location_title = newLocation
        props.selectedPlan.dayLocations[i].endCoordinates = {
          lat: place.latitude,
          lng: place.longitude,
        }
        props.selectedPlan.dayLocations[i].end_place_id = place.place_id || ''
        props.selectedPlan.dayLocations[i].end_content_id = place.content_id || ''
      }

      // If this is the last day, update the plan's end location
      if (day === totalDaysComputed.value) {
        props.selectedPlan.end_location_title = newLocation
        props.selectedPlan.endCoordinates = { lat: place.latitude, lng: place.longitude }
        props.selectedPlan.end_place_id = place.place_id || ''
        props.selectedPlan.end_content_id = place.content_id || ''
      }

      // Save changes
      saveTravelPlans()

      // Inform the user
      setTimeout(() => {
        alert(
          `${day}일차의 도착 지점이 변경되었습니다.\n\n변경된 사항:\n- 이후 모든 일차의 출발/도착 지점`,
        )
      }, 300)
    }
  } else if (currentSelectionTarget.value === 'global-custom') {
    // This is a global custom place addition - just add it to the custom locations
    // without selecting it for any specific purpose
    // The place selection sidebar will handle adding it to custom locations
    // We don't need to do anything special here
  }

  // Hide the place selection sidebar
  isPlaceSelectionActive.value = false
  currentSelectionTarget.value = null
}

const isDestinationFormValid = computed(() => {
  // Only duration is required now
  return destinationForm.value.duration > 0
})

function confirmDeleteDay(day) {
  if (totalDaysComputed.value <= 1) {
    alert('여행 계획에는 최소 1일이 필요합니다.')
    return
  }

  dayToDelete.value = day
  isConfirmDeleteDayOpen.value = true
}

function deleteDay(day) {
  if (!props.selectedPlan || totalDaysComputed.value <= 1) return

  // Remove all destinations for this day
  props.selectedPlan.destinations = props.selectedPlan.destinations.filter((d) => d.day !== day)

  // Shift all destinations from later days back by one
  props.selectedPlan.destinations.forEach((dest) => {
    if (dest.day > day) {
      dest.day -= 1
    }
  })

  // Shift day locations
  if (props.selectedPlan.dayLocations) {
    for (let i = day; i < totalDaysComputed.value; i++) {
      props.selectedPlan.dayLocations[i] = props.selectedPlan.dayLocations[i + 1] || {}
    }
    // Remove the last day's location
    delete props.selectedPlan.dayLocations[totalDaysComputed.value]
  }

  // Calculate the new end date
  const endDate = new Date(props.selectedPlan.endDate)
  endDate.setDate(endDate.getDate() - 1)

  // Update the plan's end date
  props.selectedPlan.endDate = endDate.toISOString().split('T')[0]

  // If the active day was the deleted day or later, adjust it
  if (activeDay.value >= day) {
    activeDay.value = Math.max(1, activeDay.value - 1)
  }

  // Save changes
  saveTravelPlans()

  // Update day lists
  nextTick(() => {
    updateDayLists()
  })

  // Close the modal if open
  isConfirmDeleteDayOpen.value = false
  dayToDelete.value = null
}

function addDay() {
  if (!props.selectedPlan) return

  // Calculate the new end date
  const endDate = new Date(props.selectedPlan.endDate)
  endDate.setDate(endDate.getDate() + 1)

  // Update the plan's end date
  props.selectedPlan.endDate = endDate.toISOString().split('T')[0]

  // Get the new day number
  const newDay = totalDaysComputed.value

  // Initialize locations for the new day
  if (!props.selectedPlan.dayLocations) {
    props.selectedPlan.dayLocations = {}
  }

  // Set the new day's start location to the previous day's end location
  const prevDayEndLocation = getDayEndLocation(newDay - 1)

  if (!props.selectedPlan.dayLocations[newDay]) {
    props.selectedPlan.dayLocations[newDay] = {}
  }

  props.selectedPlan.dayLocations[newDay].start_location_title = prevDayEndLocation
  props.selectedPlan.dayLocations[newDay].end_location_title =
    props.selectedPlan.end_location_title || prevDayEndLocation

  // Update the plan's end location
  props.selectedPlan.end_location_title = props.selectedPlan.dayLocations[newDay].end_location_title

  // Save changes
  saveTravelPlans()

  // Switch to the new day
  activeDay.value = newDay

  // Update day lists
  nextTick(() => {
    updateDayLists()
  })
}

// Add this new function for global map handlers
function setupGlobalMapHandlers() {
  if (window.google && window.google.maps && window.koreaMap) {
    // Add global right-click event listener
    window.koreaMap.addListener('rightclick', (event) => {
      // Prevent default context menu
      if (event.domEvent) {
        event.domEvent.preventDefault()
      }

      // Show custom place addition popup
      showAddCustomPlacePopup(event)
    })
  }
}

// Add this function to show the custom place popup globally
function showAddCustomPlacePopup(event) {
  // Remove any existing popups first
  const existingPopups = document.querySelectorAll('.global-custom-place-popup')
  existingPopups.forEach((popup) => {
    if (popup.parentNode) {
      popup.parentNode.removeChild(popup)
    }
  })

  const clickPosition = {
    x: event.domEvent.clientX,
    y: event.domEvent.clientY,
  }

  const clickLatLng = {
    lat: event.latLng.lat(),
    lng: event.latLng.lng(),
  }

  // Create a simple popup for adding custom places
  const popup = document.createElement('div')
  popup.className = 'global-custom-place-popup'
  popup.style.cssText = `
    position: fixed;
    top: ${clickPosition.y}px;
    left: ${clickPosition.x}px;
    z-index: 1600;
    background-color: white;
    border-radius: 4px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
    overflow: hidden;
    animation: popup-appear 0.2s ease;
  `

  popup.innerHTML = `
    <button style="
      display: block;
      width: 100%;
      padding: 10px 15px;
      background: none;
      border: none;
      text-align: left;
      font-size: 0.9rem;
      cursor: pointer;
      transition: background-color 0.2s ease;
    " onmouseover="this.style.backgroundColor='#f5f5f5'" onmouseout="this.style.backgroundColor='white'">
      커스텀 장소 추가
    </button>
  `

  document.body.appendChild(popup)

  // Add click handler to the button
  popup.querySelector('button').addEventListener('click', () => {
    // Store the coordinates for the place selection sidebar
    if (!window.globalCustomCoordinates) {
      window.globalCustomCoordinates = {}
    }
    window.globalCustomCoordinates = clickLatLng

    // Open the custom form directly
    openCustomPlaceForm(clickLatLng)

    // Remove the popup
    document.body.removeChild(popup)
  })

  // Remove popup when clicking elsewhere
  setTimeout(() => {
    const removePopup = (e) => {
      if (!popup.contains(e.target)) {
        if (document.body.contains(popup)) {
          document.body.removeChild(popup)
        }
        document.removeEventListener('click', removePopup)
      }
    }
    document.addEventListener('click', removePopup)
  }, 0)
}

// Add this new function to open the custom place form directly
function openCustomPlaceForm(coordinates) {
  // Set the current selection target
  currentSelectionTarget.value = 'global-custom'

  // Show the place selection sidebar
  isPlaceSelectionActive.value = true

  // Wait for the sidebar to be active, then trigger the custom form
  nextTick(() => {
    // Dispatch a custom event that the PlaceSelectionSidebar can listen for
    const event = new CustomEvent('open-custom-form', {
      detail: {
        coordinates: coordinates,
      },
    })
    window.dispatchEvent(event)
  })
}

function cancelChanges() {
  if (originalPlanData.value && props.selectedPlan) {
    // Create a deep copy of the original plan
    const restoredPlan = JSON.parse(JSON.stringify(originalPlanData.value))

    // Update all properties individually to maintain reactivity
    Object.assign(props.selectedPlan, restoredPlan)

    // Update day lists and map
    updateDayLists()
    updateMapMarkers()

    alert('모든 변경사항이 취소되었습니다.')
  }
}

async function applyChanges() {
  if (!props.selectedPlan) return

  try {
    const serverPlanData = convertViewPlanToServer(props.selectedPlan)

    if (
      props.selectedPlan.id &&
      typeof props.selectedPlan.id === 'number' &&
      props.selectedPlan.id > 1000000000000
    ) {
      // This is a new plan (created with Date.now())
      const result = await saveTravelPlanToServer(serverPlanData)
      if (result.success) {
        // Update the plan ID with the server-provided ID
        props.selectedPlan.id = result.plan_id
        // Update original data
        originalPlanData.value = JSON.parse(JSON.stringify(props.selectedPlan))

        // Refresh travel plans from server
        await fetchTravelPlansFromServer()

        alert('여행 계획이 저장되었습니다.')
      }
    } else {
      // This is an existing plan
      await updateTravelPlanToServer(serverPlanData)
      // Update original data
      originalPlanData.value = JSON.parse(JSON.stringify(props.selectedPlan))

      // Refresh travel plans from server
      await fetchTravelPlansFromServer()

      alert('여행 계획이 업데이트되었습니다.')
    }

    saveTravelPlans() // Save to localStorage as backup
  } catch (error) {
    console.error('Error applying changes:', error)
    alert('변경사항 적용 중 오류가 발생했습니다.')
  }
}

async function getAlgorithmRecommendations() {
  if (!props.selectedPlan) {
    alert('여행 계획을 선택해주세요.')
    return
  }

  try {
    console.log('Starting route optimization for plan:', props.selectedPlan.id)

    // 현재 plan에서 routes 데이터 추출
    const routesData = extractRoutesData(props.selectedPlan)
    console.log('Sending routes data:', routesData)

    // API 호출
    const optimizationResult = await recommendationService.optimizeRoute(routesData)
    console.log('Optimization result:', optimizationResult)

    // 결과를 현재 plan에 적용
    applyOptimizationResult(optimizationResult)

    alert('경로 최적화가 완료되었습니다!')
  } catch (error) {
    console.error('Error optimizing route:', error)
    alert('경로 최적화 중 오류가 발생했습니다: ' + error.message)
  }
}
//TODO AI recommend
async function getAIRecommendations() {
  if (!props.selectedPlan) {
    alert('여행 계획을 선택해주세요.')
    return
  }

  try {
    console.log('Starting route optimization for plan:', props.selectedPlan.id)

    // 현재 plan에서 routes 데이터 추출
    const routesData = extractRoutesData(props.selectedPlan)
    console.log('Sending routes data:', routesData)

    // API 호출
    const optimizationResult = await recommendationService.recommmendRouteAI(routesData)
    console.log('Optimization result:', optimizationResult)

    // 결과를 현재 plan에 적용
    applyOptimizationResult(optimizationResult)

    alert('경로 추천이 완료되었습니다!')
  } catch (error) {
    console.error('Error optimizing route:', error)
    alert('경로 추천 중 오류가 발생했습니다: ' + error.message)
  }
}

// routes 데이터 추출 함수 추가
function extractRoutesData(plan) {
  const totalDays = getDayCount(plan.startDate, plan.endDate)
  const dayLocations = { ...plan.dayLocations }

  // Fill in missing dayLocations data (기존 convertViewPlanToServer 로직 재사용)
  for (let day = 1; day <= totalDays; day++) {
    if (!dayLocations[day]) {
      dayLocations[day] = {}
    }

    // Ensure start location data
    if (!dayLocations[day].start_location_title) {
      if (day === 1) {
        dayLocations[day].start_location_title = plan.start_location_title || '위치 미지정'
        dayLocations[day].startCoordinates = plan.startCoordinates
        dayLocations[day].start_place_id = plan.start_place_id || ''
        dayLocations[day].start_content_id = plan.start_content_id || ''
      } else {
        dayLocations[day].start_location_title =
          dayLocations[day - 1]?.end_location_title || '위치 미지정'
        dayLocations[day].startCoordinates = dayLocations[day - 1]?.endCoordinates
        dayLocations[day].start_place_id = dayLocations[day - 1]?.end_place_id || ''
        dayLocations[day].start_content_id = dayLocations[day - 1]?.end_content_id || ''
      }
    }

    // Ensure end location data
    if (!dayLocations[day].end_location_title) {
      if (day === totalDays) {
        dayLocations[day].end_location_title =
          plan.end_location_title || plan.start_location_title || '위치 미지정'
        dayLocations[day].endCoordinates = plan.endCoordinates || plan.startCoordinates
        dayLocations[day].end_place_id = plan.end_place_id || plan.start_place_id || ''
        dayLocations[day].end_content_id = plan.end_content_id || plan.start_content_id || ''
      } else {
        dayLocations[day].end_location_title = dayLocations[day].start_location_title
        dayLocations[day].endCoordinates = dayLocations[day].startCoordinates
        dayLocations[day].end_place_id = dayLocations[day].start_place_id || ''
        dayLocations[day].end_content_id = dayLocations[day].start_content_id || ''
      }
    }

    // Ensure coordinates exist
    if (!dayLocations[day].startCoordinates) {
      dayLocations[day].startCoordinates = plan.startCoordinates || { lat: 37.5665, lng: 126.978 }
    }
    if (!dayLocations[day].endCoordinates) {
      dayLocations[day].endCoordinates = plan.endCoordinates ||
        plan.startCoordinates || { lat: 37.5665, lng: 126.978 }
    }

    // Ensure place_id and content_id exist
    if (!dayLocations[day].start_place_id) {
      dayLocations[day].start_place_id = ''
    }
    if (!dayLocations[day].start_content_id) {
      dayLocations[day].start_content_id = ''
    }
    if (!dayLocations[day].end_place_id) {
      dayLocations[day].end_place_id = ''
    }
    if (!dayLocations[day].end_content_id) {
      dayLocations[day].end_content_id = ''
    }
  }

  const routes = {
    title: plan.title,
    startLocation: {
      title: plan.start_location_title,
      coordinates: plan.startCoordinates,
      place_id: dayLocations[1]?.start_place_id || '',
      content_id: dayLocations[1]?.start_content_id || '',
    },
    destinations: plan.destinations.map((dest) => ({
      id: dest.id,
      title: dest.name,
      description: dest.description || '',
      latitude: dest.coordinates?.lat || 0,
      longitude: dest.coordinates?.lng || 0,
      place_id: dest.place_id || '',
      content_id: dest.content_id || '',
      day: dest.day,
      order: dest.order,
      duration: dest.duration,
      time: dest.time,
      address: dest.address,
      displayName: dest.displayName || dest.name,
    })),
    dayLocations: dayLocations,
  }

  return routes
}

// 최적화 결과 적용 함수 추가
function applyOptimizationResult(optimizationResult) {
  if (!optimizationResult.dayRoutes || !props.selectedPlan) {
    console.error('Invalid optimization result or no selected plan')
    return
  }

  console.log('Applying optimization result:', optimizationResult)

  // 새로운 destinations 배열 생성
  const newDestinations = []

  // dayRoutes 순서대로 처리
  optimizationResult.dayRoutes.forEach((dayRoute, dayIndex) => {
    const newDay = dayIndex + 1 // 또는 dayRoute.day 사용 가능
    const { visits } = dayRoute

    console.log(
      `Processing dayRoute[${dayIndex}] -> Day ${newDay} with ${visits.length} visits:`,
      visits,
    )

    // visits 배열 순서대로 목적지 배치
    visits.forEach((visit, visitIndex) => {
      const { day: originalDay, order: originalOrder, key } = visit
      const newOrder = visitIndex + 1

      let originalDestination

      // 🔥 원래 day, order로 목적지 찾기
      originalDestination = props.selectedPlan.destinations.find(
        (dest) => dest.day === originalDay && dest.order === originalOrder,
      )

      // 만약 day, order로 못 찾으면 key로 찾기 (fallback)
      if (!originalDestination && key) {
        if (!isNaN(key)) {
          originalDestination = props.selectedPlan.destinations.find((dest) => dest.id == key)
        } else if (key.startsWith('custom-')) {
          const customId = key.replace('custom-', '')
          originalDestination = props.selectedPlan.destinations.find((dest) => dest.id == customId)
        }
      }

      if (originalDestination) {
        const newDestination = {
          ...originalDestination,
          day: newDay, // 🔥 새로운 day 위치
          order: newOrder, // 🔥 새로운 order 위치
        }

        newDestinations.push(newDestination)
        console.log(
          `Moved "${originalDestination.name}" from day ${originalDay} order ${originalOrder} to day ${newDay} order ${newOrder}`,
        )
      } else {
        console.warn(
          `Could not find destination with day: ${originalDay}, order: ${originalOrder}, key: ${key}`,
        )
      }
    })
  })

  console.log('Final newDestinations:', newDestinations)

  // 반응성 유지하면서 배열 교체
  props.selectedPlan.destinations.splice(
    0,
    props.selectedPlan.destinations.length,
    ...newDestinations,
  )

  // UI 업데이트
  nextTick(() => {
    updateDayLists()
    updateAllDestinationTimes()
    updateMapMarkers()
    saveTravelPlans()
  })

  // 성공 메시지
  const summary = optimizationResult.dayRoutes
    .map((dayRoute, index) => `${index + 1}일차: ${dayRoute.visits.length}개 목적지`)
    .join('\n')

  setTimeout(() => {
    alert(`경로 최적화 완료!\n\n${summary}`)
  }, 500)
}

function confirmDeleteCustomPlace(place) {
  // This function should be implemented if needed for custom places
  console.log('Confirm delete custom place:', place)
}

function formatTravelTime(fromDest, toDest) {
  // If either destination doesn't have coordinates, return N/A
  if (!fromDest || !toDest || !fromDest.coordinates || !toDest.coordinates) {
    return 'N/A'
  }

  // Check if we have real travel time data
  if (toDest.realTravelTime && toDest.realTravelTime !== 'N/A') {
    return formatDuration(toDest.realTravelTime)
  }

  // Return N/A if no real data
  return 'N/A'
}

function handleTravelLineClick(type, day, fromId = null, toId = null) {
  console.log('Travel line clicked:', { type, day, fromId, toId })

  let fromLocation = null
  let toLocation = null
  let fromCoords = null
  let toCoords = null
  let fromPlaceId = null
  let fromContentId = null
  let toPlaceId = null
  let toContentId = null

  // Get start and end coordinates and IDs for the day
  const startCoords = getDayStartCoordinates(day)
  const endCoords = getDayEndCoordinates(day)
  const startLocation = getDayStartLocation(day)
  const endLocation = getDayEndLocation(day)

  // Get place_id and content_id for start/end locations from dayLocations
  const startPlaceId =
    props.selectedPlan?.dayLocations?.[day]?.start_place_id ||
    props.selectedPlan?.start_place_id ||
    ''
  const startContentId =
    props.selectedPlan?.dayLocations?.[day]?.start_content_id ||
    props.selectedPlan?.start_content_id ||
    ''
  const endPlaceId =
    props.selectedPlan?.dayLocations?.[day]?.end_place_id || props.selectedPlan?.end_place_id || ''
  const endContentId =
    props.selectedPlan?.dayLocations?.[day]?.end_content_id ||
    props.selectedPlan?.end_content_id ||
    ''

  switch (type) {
    case 'start-to-end':
      // Direct from start to end (when no destinations)
      fromLocation = startLocation
      toLocation = endLocation
      fromCoords = startCoords
      toCoords = endCoords
      fromPlaceId = startPlaceId
      fromContentId = startContentId
      toPlaceId = endPlaceId
      toContentId = endContentId
      break

    case 'start-to-dest':
      // From start to first destination
      const firstDest = props.selectedPlan.destinations.find((d) => d.id === fromId)
      fromLocation = startLocation
      toLocation = firstDest?.displayName || firstDest?.name || '목적지'
      fromCoords = startCoords
      toCoords = firstDest?.coordinates
      fromPlaceId = startPlaceId
      fromContentId = startContentId
      toPlaceId = firstDest?.place_id || ''
      toContentId = firstDest?.content_id || ''
      break

    case 'dest-to-dest':
      // Between destinations
      const fromDest = props.selectedPlan.destinations.find((d) => d.id === fromId)
      const toDest = props.selectedPlan.destinations.find((d) => d.id === toId)
      fromLocation = fromDest?.displayName || fromDest?.name || '출발지'
      toLocation = toDest?.displayName || toDest?.name || '목적지'
      fromCoords = fromDest?.coordinates
      toCoords = toDest?.coordinates
      fromPlaceId = fromDest?.place_id || ''
      fromContentId = fromDest?.content_id || ''
      toPlaceId = toDest?.place_id || ''
      toContentId = toDest?.content_id || ''
      break

    case 'dest-to-end':
      // From last destination to end
      const lastDest = props.selectedPlan.destinations.find((d) => d.id === fromId)
      fromLocation = lastDest?.displayName || lastDest?.name || '출발지'
      toLocation = endLocation
      fromCoords = lastDest?.coordinates
      toCoords = endCoords
      fromPlaceId = lastDest?.place_id || ''
      fromContentId = lastDest?.content_id || ''
      toPlaceId = endPlaceId
      toContentId = endContentId
      break
  }

  // Create detailed travel info
  const travelInfo = {
    type,
    day,
    from: {
      name: fromLocation,
      coordinates: fromCoords,
      place_id: fromPlaceId,
      content_id: fromContentId,
    },
    to: {
      name: toLocation,
      coordinates: toCoords,
      place_id: toPlaceId,
      content_id: toContentId,
    },
  }

  console.log('Travel Info:', travelInfo)

  // Show detailed information
  const message = `
이동 정보:
출발: ${fromLocation}
도착: ${toLocation}
일차: ${day}일차
타입: ${getTypeDescription(type)}

출발지 정보:
- Place ID: ${fromPlaceId || 'N/A'}
- Content ID: ${fromContentId || 'N/A'}
- 좌표: ${fromCoords ? `${fromCoords.lat.toFixed(6)}, ${fromCoords.lng.toFixed(6)}` : 'N/A'}

도착지 정보:
- Place ID: ${toPlaceId || 'N/A'}
- Content ID: ${toContentId || 'N/A'}
- 좌표: ${toCoords ? `${toCoords.lat.toFixed(6)}, ${toCoords.lng.toFixed(6)}` : 'N/A'}
  `

  alert(message)
}

function getTypeDescription(type) {
  switch (type) {
    case 'start-to-end':
      return '출발지 → 도착지 (직접)'
    case 'start-to-dest':
      return '출발지 → 첫 번째 목적지'
    case 'dest-to-dest':
      return '목적지 간 이동'
    case 'dest-to-end':
      return '마지막 목적지 → 도착지'
    default:
      return '알 수 없음'
  }
}

const isCalculatingTravelTimes = ref(false)

async function calculateRealTravelTimes() {
  if (!props.selectedPlan) return

  isCalculatingTravelTimes.value = true

  try {
    // Calculate for all days
    const totalDays = totalDaysComputed.value

    for (let day = 1; day <= totalDays; day++) {
      await updateRealTimeTravelTimes(day)
      // Add delay between days to avoid rate limiting
      if (day < totalDays) {
        await new Promise((resolve) => setTimeout(resolve, 500))
      }
    }

    // Update map markers after calculation
    updateMapMarkers()

    alert('실시간 이동시간 계산이 완료되었습니다.')
  } catch (error) {
    console.error('Error calculating real travel times:', error)
    alert('이동시간 계산 중 오류가 발생했습니다.')
  } finally {
    isCalculatingTravelTimes.value = false
  }
}

onMounted(() => {
  // Check if Google Maps is already loaded
  if (window.google && window.google.maps) {
    googleMapsLoaded.value = true
  }

  // Fetch travel plans from server
  fetchTravelPlansFromServer()

  // Set up event listeners for communication with parent component
  window.addEventListener('open-add-plan-modal', () => {
    openAddPlanModal()
  })

  window.addEventListener('open-edit-plan-modal', (event) => {
    openEditPlanModal(event.detail)
  })

  window.addEventListener('confirm-delete-plan', (event) => {
    confirmDeletePlan(event.detail)
  })

  window.addEventListener('select-plan', (event) => {
    // The plan is already selected in the parent component
    activeDay.value = 1
    isPlanDetailOpen.value = true
    nextTick(() => {
      showAllRoutes() // Show all routes instead of just the active day
    })
  })

  // Initialize map if container exists
  nextTick(() => {
    if (mapContainer.value && window.google) {
      window.koreaMap = new window.google.maps.Map(mapContainer.value, {
        center: { lat: 37.5665, lng: 126.978 }, // Seoul
        zoom: 10,
        mapTypeId: window.google.maps.MapTypeId.ROADMAP,
      })

      // Initialize arrays for map elements
      window.koreaMapMarkers = []
      window.koreaMapPolylines = []
      window.koreaMapInfoWindows = []
      window.tempMarker = null

      // Set up global right-click handlers for custom place addition
      setupGlobalMapHandlers()

      // Update markers if a plan is selected
      if (props.selectedPlan) {
        updateMapMarkers()
      }
    }

    handleTabsScroll()
  })

  // Move the hook call inside the `onMounted` lifecycle hook
  planDetailWidth.value = window.innerWidth > 1200 ? 600 : 400
})
</script>

<style scoped>
.travel-plans-page {
  position: relative;
  width: 100%;
  height: 100%;
}

.travel-plans-content {
  position: relative;
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
  transition:
    margin-left 0.3s ease,
    width 0.3s ease,
    margin-right 0.3s ease;
}

.sidebar-toggle-button {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 24px;
  height: 60px;
  background-color: var(--primary-color);
  border: none;
  border-radius: 0 4px 4px 0;
  color: white;
  cursor: pointer;
  z-index: 5;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.sidebar-toggle-button.sidebar-closed .toggle-icon {
  transform: rotate(180deg);
}

.toggle-icon {
  transition: transform 0.3s ease;
}

/* Detail sidebar toggle button */
.detail-sidebar-toggle-button {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 24px;
  height: 60px;
  background-color: var(--primary-color);
  border: none;
  border-radius: 4px 0 0 4px;
  color: white;
  cursor: pointer;
  z-index: 5;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.detail-sidebar-toggle-button .toggle-icon {
  transform: rotate(180deg);
}

.map-container {
  width: 100%;
  height: 100%;
  background-color: #f5f5f5;
  position: relative;
  z-index: 1; /* Lower z-index to ensure sidebars appear above */
}

.plan-detail-sidebar {
  top: var(--topbar-height);
  z-index: var(--z-index-travel-sidebar);
}

.plan-detail-content {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.plan-detail-container {
  display: flex;
  flex-direction: column;
  height: 300px;
  overflow: hidden;
  border-bottom: 1px solid var(--border-color);
  background-color: white;
  z-index: 1;
}

.detail-header {
  padding: 15px 20px;
  background-color: var(--primary-color);
  color: white;
}

.detail-dates {
  font-size: 0.9rem;
  margin-bottom: 5px;
}

.detail-description {
  font-size: 1rem;
}

.detail-tabs-container {
  position: relative;
  border-bottom: 1px solid var(--border-color);
  background-color: white;
}

.detail-tabs {
  display: flex;
  overflow-x: auto;
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE and Edge */
}

/* Hide scrollbar for Chrome, Safari and Opera */
.detail-tabs::-webkit-scrollbar {
  display: none;
}

.detail-tab {
  padding: 10px 15px;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition:
    background-color 0.2s ease,
    border-color 0.2s ease;
  white-space: nowrap;
}

.detail-tab.active {
  border-bottom-color: var(--primary-color);
  background-color: var(--background-light);
}

.detail-tab:hover {
  background-color: var(--background-light);
}

.scroll-indicator {
  position: absolute;
  top: 0;
  right: 0;
  height: 100%;
  width: 30px;
  background: linear-gradient(to right, rgba(255, 255, 255, 0), white);
  display: flex;
  align-items: center;
  justify-content: center;
  pointer-events: none;
}

.scroll-indicator::after {
  content: '▶';
  font-size: 1.2rem;
  color: var(--text-light);
}

/* Multi-day view styles */
.multi-day-view {
  flex: 1;
  overflow-y: auto; /* Changed from hidden to auto to enable vertical scrolling */
  display: flex; /* Added to make the container take full height */
  flex-direction: column; /* Added to stack children vertically */
}

.multi-day-container {
  display: flex;
  overflow-x: auto;
  min-height: 100%; /* Changed from auto to 100% to fill the parent */
  padding: 10px;
  flex: 1; /* Added to make it grow and fill available space */
}

.day-column {
  flex: 0 0 300px;
  margin-right: 15px;
  background-color: #f5f5f5;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  height: fit-content; /* This ensures the height is exactly what's needed for content */
  min-height: 200px; /* Minimum height for visual consistency */
  overflow-y: visible; /* No scrollbar for individual columns */
}

.day-column.active-day {
  border: 2px solid var(--primary-color);
}

.day-header {
  padding: 15px;
  background-color: var(--primary-color);
  color: white;
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
  position: sticky;
  top: 0;
  z-index: 5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.day-header h3 {
  margin: 0;
  font-size: 1.2rem;
}

.day-date {
  font-size: 0.9rem;
  opacity: 0.9;
}

.day-locations {
  background-color: white;
  border-bottom: 1px solid var(--border-color);
}

.location-item {
  padding: 10px 15px;
  border-bottom: 1px solid var(--border-color);
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.location-item:hover {
  background-color: rgba(0, 91, 172, 0.1);
}

.start-location:hover {
  background-color: rgba(76, 175, 80, 0.15);
}

.end-location:hover {
  background-color: rgba(244, 67, 54, 0.15);
}

.location-item:last-child {
  border-bottom: none;
}

.location-label {
  font-weight: bold;
  font-size: 0.9rem;
  color: var(--text-light);
  margin-bottom: 5px;
}

.location-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.location-name {
  font-size: 1rem;
  color: var(--primary-color);
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.location-edit-button {
  background: none;
  border: none;
  font-size: 1rem;
  cursor: pointer;
  padding: 5px;
  border-radius: 4px;
  transition: background-color 0.2s ease;
}

.location-edit-button:hover {
  background-color: var(--background-light);
}

.location-time {
  font-size: 0.9rem;
  color: var(--text-light);
  margin-top: 5px;
}

.location-travel-time {
  font-size: 0.8rem;
  color: var(--text-light);
}

.location-travel-time .travel-time-label {
  font-weight: bold;
  margin-right: 5px;
}

.start-location {
  background-color: rgba(0, 91, 172, 0.05);
}

.end-location {
  background-color: rgba(255, 84, 84, 0.05);
}

.day-time-input {
  display: flex;
  align-items: center;
  padding: 10px 15px;
  background-color: white;
  border-bottom: 1px solid var(--border-color);
}

.time-label {
  margin-right: 10px;
  font-weight: bold;
  font-size: 0.9rem;
}

.time-input {
  padding: 5px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 1rem;
}

.destination-with-lines {
  display: flex;
  flex-direction: column;
}

.travel-line {
  position: relative;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.travel-line:hover {
  background-color: rgba(0, 91, 172, 0.05);
}

.travel-line-content {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.dotted-line {
  position: absolute;
  left: 50%;
  top: 0;
  bottom: 0;
  width: 2px;
  background-image: linear-gradient(to bottom, #ccc 50%, transparent 50%);
  background-size: 2px 8px;
  background-repeat: repeat-y;
  transform: translateX(-50%);
}

.travel-time-badge {
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 12px;
  padding: 4px 8px;
  font-size: 0.8rem;
  color: var(--text-color);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  z-index: 1;
}

.empty-day-line {
  height: 60px;
  margin: 20px 0;
}

.empty-day-line .dotted-line {
  background-image: linear-gradient(to bottom, var(--primary-color) 50%, transparent 50%);
}

.empty-day-line .travel-time-badge {
  background-color: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.destination-item {
  display: flex;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
  cursor: pointer;
  margin: 0;
}

.destination-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.destination-item.incomplete-place {
  background-color: #fff3e0;
  border-left: 4px solid #ff9800;
}

.destination-drag-handle {
  width: 50px;
  background-color: var(--background-light);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 15px 0;
  cursor: grab;
}

.destination-drag-handle:active {
  cursor: grabbing;
}

.destination-number {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background-color: var(--primary-color);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.9rem;
  font-weight: bold;
  margin-bottom: 8px;
}

.incomplete-place .destination-number {
  background-color: #ff9800;
}

.drag-icon {
  color: var(--text-light);
  font-size: 1rem;
}

.destination-content {
  flex: 1;
  padding: 15px;
}

.destination-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.destination-header h4 {
  margin: 0;
  font-size: 1.1rem;
  color: var(--primary-color);
  font-weight: 600;
}

.incomplete-place .destination-header h4 {
  color: #e65100;
}

.destination-time {
  font-size: 0.85rem;
  color: var(--text-light);
  background-color: var(--background-light);
  padding: 4px 8px;
  border-radius: 4px;
}

.destination-duration {
  display: flex;
  align-items: center;
  gap: 8px;
}

.duration-label {
  font-size: 0.85rem;
  color: var(--text-light);
  font-weight: 500;
}

.duration-value {
  font-size: 0.9rem;
  color: var(--text-color);
  font-weight: 600;
}

.destination-actions {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 15px;
  gap: 8px;
}

.edit-destination-button,
.delete-destination-button {
  background: none;
  border: none;
  font-size: 1.1rem;
  cursor: pointer;
  padding: 6px;
  border-radius: 4px;
  transition: background-color 0.2s ease;
}

.edit-destination-button:hover,
.delete-destination-button:hover {
  background-color: var(--background-light);
}

.delete-destination-button:hover {
  color: var(--secondary-color);
}

.ghost-destination {
  opacity: 0.5;
  background-color: #e3f2fd !important;
  border: 2px dashed var(--primary-color) !important;
  transform: scale(0.98);
}

.ghost-destination .travel-line {
  opacity: 0.3;
}

/* Modal styles */
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
  z-index: var(--z-index-modal);
}

.modal {
  background-color: white;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid var(--border-color);
  position: sticky;
  top: 0;
  background-color: white;
  z-index: 1;
}

.modal-header h3 {
  margin-top: 0;
  color: var(--primary-color);
}

.close-modal-button {
  background: none;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  color: var(--text-light);
}

.modal-content {
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
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.cancel-button,
.save-button,
.delete-button {
  padding: 10px 20px;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.cancel-button {
  background-color: var(--background-light);
  border: 1px solid var(--border-color);
  color: var(--text-color);
}

.save-button {
  background-color: var(--primary-color);
  border: none;
  color: white;
}

.delete-button {
  background-color: var(--secondary-color);
  border: none;
  color: white;
}

.confirm-modal {
  background-color: white;
  border-radius: 8px;
  width: 90%;
  max-width: 400px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.confirm-modal h3 {
  margin-top: 0;
  color: var(--primary-color);
}

.confirm-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.input-with-button {
  display: flex;
  gap: 10px;
}

.input-with-button input {
  flex: 1;
}

.select-place-button {
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 4px;
  padding: 0 15px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.select-place-button:hover {
  background-color: #004c8e;
}

.day-header-content {
  flex: 1;
}

.delete-day-button {
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.7);
  font-size: 1rem;
  cursor: pointer;
  padding: 5px;
  border-radius: 4px;
  transition: color 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.delete-day-button:hover {
  color: white;
  background-color: rgba(255, 255, 255, 0.1);
}

.add-day-column {
  flex: 0 0 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.add-day-button {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: var(--primary-color);
  color: white;
  border: none;
  font-size: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.add-day-button:hover {
  background-color: #004c8e;
}

.day-column.map-highlighted {
  border: 3px solid #4caf50 !important;
  box-shadow: 0 0 15px rgba(76, 175, 80, 0.5);
  transform: scale(1.02);
  transition: all 0.3s ease;
}

.day-column {
  cursor: pointer;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
}

.day-column:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* Add this to the <style> section */
.view-controls {
  display: flex;
  gap: 10px;
  padding: 10px 15px;
  background-color: #f5f5f5;
  border-bottom: 1px solid var(--border-color);
}

.view-control-button {
  padding: 8px 15px;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s ease;
  flex: 1;
}

.view-control-button:hover {
  background-color: #004c8e;
}

.plan-actions {
  margin-top: 20px;
  display: flex;
  gap: 10px;
  justify-content: center;
  flex-wrap: wrap;
}

.cancel-btn,
.apply-btn,
.ai-recommend-btn,
.algorithm-recommend-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.2s ease;
}

.cancel-btn {
  background-color: #f44336;
  color: white;
}

.cancel-btn:hover {
  background-color: #d32f2f;
}

.apply-btn {
  background-color: #4caf50;
  color: white;
}

.apply-btn:hover {
  background-color: #388e3c;
}

.ai-recommend-btn {
  background-color: #9c27b0;
  color: white;
}

.ai-recommend-btn:hover {
  background-color: #7b1fa2;
}

.algorithm-recommend-btn {
  background-color: #ff9800;
  color: white;
}

.algorithm-recommend-btn:hover {
  background-color: #f57c00;
}

.destination-item {
  cursor: grab;
  transition: all 0.2s ease;
}

.destination-item:active {
  cursor: grabbing;
}

.destination-item.dragging {
  opacity: 0.5;
  transform: rotate(2deg);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

.add-destination-button {
  width: 60%;
  padding: 12px;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s ease;
  margin-top: 10px;
  font-size: 0.9rem;
}

.add-destination-button:hover {
  background-color: #004c8e;
}

.add-destination-button.small {
  font-size: 0.85rem;
  margin: 0px 20%;
}

.day-destinations-column {
  min-height: 100px;
  transition: background-color 0.2s ease;
}

.day-destinations-column.drag-over {
  background-color: rgba(0, 91, 172, 0.1);
  border: 2px dashed var(--primary-color);
  border-radius: 8px;
}

.destination-item.dragging {
  opacity: 0.5;
  transform: rotate(2deg) scale(0.95);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
  z-index: 1000;
}

.real-time-btn {
  background-color: #2196f3;
  color: white;
}

.real-time-btn:hover {
  background-color: #1976d2;
}

.real-time-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>
