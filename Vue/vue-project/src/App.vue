<template>
  <div class="app-container">
    <TopBar />

    <!-- Main Sidebar -->
    <Sidebar
      :isOpen="isSidebarOpen"
      :width="sidebarWidth"
      @toggle="toggleSidebar"
      @resize="handleSidebarResize"
      @navigateTo="handleNavigation"
    />

    <!-- Travel Plans Sidebar -->
    <TravelPlansSidebar
      v-if="showTravelPlansSidebar"
      :isOpen="isTravelPlansSidebarOpen"
      :width="travelPlansSidebarWidth"
      :title="'한국 여행 계획'"
      :mainSidebarIsOpen="isSidebarOpen"
      :mainSidebarWidth="sidebarWidth"
      @toggle="toggleTravelPlansSidebar"
      @resize="handleTravelPlansSidebarResize"
    >
      <div class="travel-plans-container">
        <div class="actions-bar">
          <button class="add-plan-button" @click="openAddPlanModal">새 여행 계획 만들기</button>
        </div>

        <div v-if="!authStore.isAuthenticated" class="login-prompt">
          <div class="empty-icon">🔒</div>
          <h2>로그인이 필요합니다</h2>
          <p>여행 계획을 보려면 로그인해주세요.</p>
          <button class="login-button" @click="modalStore.openLoginModal">로그인하기</button>
        </div>

        <div v-else-if="isLoading" class="loading-container">
          <div class="loading-spinner"></div>
          <p>여행 계획을 불러오는 중...</p>
        </div>

        <div v-else-if="travelPlans.length === 0" class="empty-state">
          <div class="empty-icon">✈️</div>
          <h2>여행 계획이 없습니다</h2>
          <p>첫 번째 여행 계획을 만들어 한국 여행을 시작해보세요!</p>
          <button class="create-plan-button" @click="openAddPlanModal">
            첫 번째 여행 계획 만들기
          </button>
        </div>

        <div v-else class="plans-list">
          <div
            v-for="plan in travelPlans"
            :key="plan.id"
            class="plan-card"
            @click="selectPlan(plan)"
            :class="{ active: selectedPlan && selectedPlan.id === plan.id }"
          >
            <div class="card-header">
              <h3>{{ plan.title }}</h3>
              <div class="card-actions">
                <button class="edit-button" @click.stop="openEditPlanModal(plan)">✏️</button>
                <button class="delete-button" @click.stop="confirmDeletePlan(plan)">🗑️</button>
              </div>
            </div>
            <div class="card-dates">{{ formatDateRange(plan.startDate, plan.endDate) }}</div>
            <p class="card-description">{{ plan.description }}</p>
            <div class="card-stats">
              <div class="stat">
                <span class="stat-value">{{ plan.destinations.length }}</span>
                <span class="stat-label">장소</span>
              </div>
              <div class="stat">
                <span class="stat-value">{{ getDayCount(plan.startDate, plan.endDate) }}</span>
                <span class="stat-label">일</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </TravelPlansSidebar>

    <!-- Main Content -->
    <div
      class="main-content"
      :style="{
        marginLeft: isSidebarOpen ? `${sidebarWidth}px` : '60px',
        width: `calc(100% - ${isSidebarOpen ? sidebarWidth : 60}px)`,
      }"
    >
      <router-view
        :key="$route.fullPath"
        :isSidebarOpen="isSidebarOpen"
        :sidebarWidth="sidebarWidth"
        :isTravelPlansSidebarOpen="isTravelPlansSidebarOpen"
        :travelPlansSidebarWidth="travelPlansSidebarWidth"
        :selectedPlan="selectedPlan"
        :travelPlans="travelPlans"
        @select-plan="selectPlan"
      />
    </div>

    <!-- Modals -->
    <LoginModal v-if="modalStore.isLoginModalOpen" />
    <SignupModal v-if="modalStore.isSignupModalOpen" />
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, provide } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from './stores/auth'
import { useModalStore } from './stores/modal'
import TopBar from './components/layout/TopBar.vue'
import Sidebar from './components/layout/Sidebar.vue'
import TravelPlansSidebar from './components/layout/TravelPlansSidebar.vue'
import LoginModal from './components/auth/LoginModal.vue'
import SignupModal from './components/auth/SignupModal.vue'

// Stores
const authStore = useAuthStore()
const modalStore = useModalStore()
const route = useRoute()

// Sidebar state
const isSidebarOpen = ref(true)
const sidebarWidth = ref(250)

// Travel Plans Sidebar state
const isTravelPlansSidebarOpen = ref(true)
const travelPlansSidebarWidth = ref(300)

// Travel Plans state
const isLoading = ref(false)
const travelPlans = ref([])
const selectedPlan = ref(null)

// Computed
const currentRoute = computed(() => route.path)
const showTravelPlansSidebar = ref(false)

watch(
  () => route.path,
  (newPath) => {
    showTravelPlansSidebar.value = newPath === '/my-travel-plans'
  },
)

// Methods for sidebar management
function toggleSidebar() {
  isSidebarOpen.value = !isSidebarOpen.value
}

function handleSidebarResize(newWidth) {
  sidebarWidth.value = newWidth
}

function toggleTravelPlansSidebar() {
  isTravelPlansSidebarOpen.value = !isTravelPlansSidebarOpen.value
}

function handleTravelPlansSidebarResize(newWidth) {
  travelPlansSidebarWidth.value = newWidth
}

function handleNavigation(page) {
  // Handle navigation events if needed
  console.log('Navigating to:', page)
}

// Travel Plans methods
function openAddPlanModal() {
  if (!authStore.isAuthenticated) {
    modalStore.openLoginModal()
    return
  }

  // Emit event to MyTravelPlans component
  if (window.dispatchEvent) {
    window.dispatchEvent(new CustomEvent('open-add-plan-modal'))
  }
}

function openEditPlanModal(plan) {
  // Emit event to MyTravelPlans component
  if (window.dispatchEvent) {
    window.dispatchEvent(new CustomEvent('open-edit-plan-modal', { detail: plan }))
  }
}

function confirmDeletePlan(plan) {
  // Emit event to MyTravelPlans component
  if (window.dispatchEvent) {
    window.dispatchEvent(new CustomEvent('confirm-delete-plan', { detail: plan }))
  }
}

function selectPlan(plan) {
  selectedPlan.value = plan

  // Emit event to MyTravelPlans component
  if (window.dispatchEvent) {
    window.dispatchEvent(new CustomEvent('select-plan', { detail: plan }))
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

function getDayCount(startDate, endDate) {
  if (!startDate || !endDate) return 1

  const start = new Date(startDate)
  const end = new Date(endDate)

  // Calculate the difference in days
  const diffTime = Math.abs(end - start)
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))

  return diffDays + 1 // Include both start and end days
}

// Load travel plans from local storage
function loadTravelPlans() {
  isLoading.value = true

  try {
    const savedPlans = localStorage.getItem('travelPlans')
    if (savedPlans) {
      travelPlans.value = JSON.parse(savedPlans)
    } else {
      // Mock data for first-time users
      travelPlans.value = [
        {
          id: 1,
          title: '서울 3박 4일 여행',
          description: '서울의 주요 관광지를 방문하는 여행',
          startDate: '2023-12-01',
          endDate: '2023-12-04',
          userId: 1,
          destinations: [
            {
              id: 101,
              name: '경복궁',
              address: '서울특별시 종로구 사직로 161',
              day: 1,
              time: '10:00',
              duration: 120, // 2 hours
              order: 1,
              notes: '조선 왕조의 법궁',
              coordinates: { lat: 37.5796, lng: 126.977 },
            },
            {
              id: 102,
              name: '광화문 광장',
              address: '서울특별시 종로구 세종로',
              day: 1,
              time: '13:00',
              duration: 60, // 1 hour
              order: 2,
              notes: '세종대왕 동상과 이순신 장군 동상',
              coordinates: { lat: 37.5725, lng: 126.9765 },
            },
            {
              id: 103,
              name: '명동',
              address: '서울특별시 중구 명동',
              day: 2,
              time: '11:00',
              duration: 180, // 3 hours
              order: 1,
              notes: '쇼핑과 맛집',
              coordinates: { lat: 37.5633, lng: 126.983 },
            },
          ],
        },
      ]
      //localStorage.setItem('travelPlans', JSON.stringify(travelPlans.value))
    }

    // Select the first plan by default
    if (travelPlans.value.length > 0) {
      selectedPlan.value = travelPlans.value[0]
    }
  } catch (error) {
    console.error('Error loading travel plans:', error)
  } finally {
    isLoading.value = false
  }
}

// Watch for authentication changes
watch(
  () => authStore.isAuthenticated,
  (isAuthenticated) => {
    if (!isAuthenticated) {
      selectedPlan.value = null
      travelPlans.value = []
    } else {
      //loadTravelPlans()
    }
  },
)

// Watch for route changes
watch(
  () => route.path,
  (path) => {
    // If navigating away from travel plans, reset some state if needed
    if (path !== '/my-travel-plans') {
      // Reset state if needed
    }
  },
)

// Provide sidebar state to child components
provide('isSidebarOpen', isSidebarOpen)
provide('sidebarWidth', sidebarWidth)
provide('isTravelPlansSidebarOpen', isTravelPlansSidebarOpen)
provide('travelPlansSidebarWidth', travelPlansSidebarWidth)
provide('selectedPlan', selectedPlan)
provide('travelPlans', travelPlans)

onMounted(() => {
  // Check if user is already logged in
  authStore.checkAuth()

  // Load travel plans if authenticated
  if (authStore.isAuthenticated) {
    //loadTravelPlans()
  }

  // Load Google Maps API if not already loaded
  if (!window.google) {
    const script = document.createElement('script')
    script.src =
      'https://maps.googleapis.com/maps/api/js?key=&loading=async&callback=initMap'
    script.async = true
    script.defer = true
    script.onload = () => {
      console.log('Google Maps API loaded')
    }
    document.head.appendChild(script)
  }
})
</script>

<style scoped>
.app-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
  position: relative;
}

.main-content {
  flex: 1;
  margin-top: var(--topbar-height);
  transition:
    margin-left 0.3s ease,
    width 0.3s ease,
    margin-right 0.3s ease;
  overflow-y: auto;
  position: relative;
  z-index: var(--z-index-content);
}

/* Travel Plans Sidebar Styles */
.travel-plans-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.actions-bar {
  padding: 15px 20px;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  justify-content: flex-end;
}

.add-plan-button {
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 4px;
  padding: 8px 15px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.add-plan-button:hover {
  background-color: #004c8e;
}

.plans-list {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.plan-card {
  background-color: white;
  border-radius: 8px;
  padding: 15px;
  box-shadow: var(--shadow);
  cursor: pointer;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
  border-left: 3px solid transparent;
}

.plan-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
}

.plan-card.active {
  border-left-color: var(--primary-color);
  background-color: rgba(0, 91, 172, 0.05);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.card-header h3 {
  margin: 0;
  color: var(--primary-color);
}

.card-actions {
  display: flex;
  gap: 5px;
}

.edit-button,
.delete-button {
  background: none;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  padding: 5px;
  border-radius: 4px;
  transition: background-color 0.2s ease;
}

.edit-button:hover,
.delete-button:hover {
  background-color: var(--background-light);
}

.card-dates {
  color: var(--text-light);
  font-size: 0.9rem;
  margin-bottom: 10px;
}

.card-description {
  color: var(--text-color);
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-stats {
  display: flex;
  gap: 20px;
}

.stat {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: bold;
  color: var(--primary-color);
}

.stat-label {
  font-size: 0.8rem;
  color: var(--text-light);
}

.login-prompt,
.empty-state,
.loading-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 20px;
}

.login-prompt h2,
.empty-state h2 {
  color: var(--primary-color);
  margin-bottom: 10px;
}

.login-prompt p,
.empty-state p {
  color: var(--text-light);
  margin-bottom: 30px;
  max-width: 500px;
}

.login-button,
.create-plan-button {
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 4px;
  padding: 12px 20px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.login-button:hover,
.create-plan-button:hover {
  background-color: #004c8e;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(0, 91, 172, 0.1);
  border-left-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 15px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Mobile responsiveness */
@media (max-width: 768px) {
  .main-content {
    margin-left: 60px !important;
    width: calc(100% - 60px) !important;
  }

  .travel-plans-sidebar {
    width: 100% !important;
    left: 60px !important;
    z-index: var(--z-index-travel-sidebar);
  }

  .travel-plans-sidebar.collapsed {
    transform: translateX(-100%);
  }
}
</style>
