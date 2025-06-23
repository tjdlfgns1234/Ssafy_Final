<template>
  <div class="place-selection-sidebar" :class="{ active: isActive }">
    <div class="sidebar-header">
      <h2>장소 선택</h2>
      <button class="close-button" @click="close">✕</button>
    </div>

    <div class="sidebar-content">
      <!-- Tabs -->
      <div class="tabs">
        <button
          class="tab-button"
          :class="{ active: activeTab === 'search' }"
          @click="activeTab = 'search'"
        >
          장소 검색
        </button>
        <button
          class="tab-button"
          :class="{ active: activeTab === 'custom' }"
          @click="activeTab = 'custom'"
        >
          커스텀 장소
        </button>
      </div>

      <!-- Search Tab Content -->
      <div v-if="activeTab === 'search'" class="tab-content">
        <!-- Search bar -->
        <div class="search-container">
          <input
            type="text"
            v-model="searchQuery"
            placeholder="장소 검색"
            class="search-input"
            @keyup.enter="searchPlaces"
          />
          <button class="search-button" @click="searchPlaces">검색</button>
        </div>

        <!-- Filter options -->
        <div class="filter-options">
          <div class="filter-group">
            <label>카테고리</label>
            <div class="filter-buttons">
              <button
                v-for="category in categories"
                :key="category.id"
                :class="{ active: selectedCategory === category.id }"
                @click="selectCategory(category.id)"
              >
                {{ category.name }}
              </button>
            </div>
          </div>
        </div>

        <!-- Results list -->
        <div class="results-container">
          <div v-if="isLoading" class="loading-indicator">
            <div class="spinner"></div>
            <p>장소를 검색 중입니다...</p>
          </div>

          <div v-else-if="searchResults.length === 0" class="empty-results">
            <p>검색 결과가 없습니다.</p>
            <p>다른 검색어를 입력하거나 지도에서 직접 위치를 선택하세요.</p>
          </div>

          <div v-else class="results-list">
            <div
              v-for="place in searchResults"
              :key="place.id"
              class="place-item"
              @click="showPlaceOnMap(place)"
            >
              <div class="place-info">
                <h3>{{ place.name }}</h3>
                <p class="place-address">{{ place.address }}</p>
                <div class="place-category">{{ place.category }}</div>
              </div>
              <button class="select-button" @click.stop="selectPlace(place)">선택</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Custom Locations Tab Content -->
      <div v-if="activeTab === 'custom'" class="tab-content">
        <div class="custom-locations-header">
          <h3>내 커스텀 장소</h3>
          <button class="add-custom-button" @click="showAddCustomForm = true">
            + 새 장소 추가
          </button>
        </div>

        <!-- Custom locations list -->
        <div class="results-container">
          <div v-if="isLoading && activeTab === 'custom'" class="loading-indicator">
            <div class="spinner"></div>
            <p>커스텀 장소를 불러오는 중입니다...</p>
          </div>

          <div v-else-if="customLocations.length === 0" class="empty-results">
            <p>저장된 커스텀 장소가 없습니다.</p>
            <p>
              지도에서 우클릭하여 커스텀 장소를 추가하거나 위의 '새 장소 추가' 버튼을 사용하세요.
            </p>
          </div>

          <div v-else class="results-list">
            <div
              v-for="place in customLocations"
              :key="place.placeId || place.id"
              class="place-item"
              @click="showCustomPlaceOnMap(place)"
            >
              <div class="place-info">
                <h3>{{ place.name }}</h3>
                <p class="place-memo">{{ place.memo }}</p>
                <div class="place-coordinates">
                  {{ formatCoordinates(place.latitude, place.longitude) }}
                </div>
              </div>
              <div class="place-actions">
                <button class="select-button" @click.stop="selectCustomPlace(place)">선택</button>
                <button class="delete-button" @click.stop="confirmDeleteCustomPlace(place)">
                  삭제
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Map selection info -->
      <div class="map-selection-info">
        <p>또는 지도에서 직접 위치를 선택하세요.</p>
        <p class="map-right-click-info">지도에서 우클릭하여 커스텀 장소를 추가할 수 있습니다.</p>
        <div v-if="isActive" class="right-click-mode-toggle">
          <button @click="toggleRightClickMode" :class="{ active: rightClickModeActive }">
            {{ rightClickModeActive ? '우클릭 모드 해제' : '우클릭 모드 활성화' }}
          </button>
          <p class="right-click-mode-hint" v-if="rightClickModeActive">
            지도에서 클릭하여 커스텀 장소를 추가하세요.
          </p>
        </div>
      </div>
    </div>

    <!-- Add Custom Location Form -->
    <div v-if="showAddCustomForm" class="custom-location-form-overlay" @click="closeCustomForm">
      <div class="custom-location-form" @click.stop>
        <h3>커스텀 장소 추가</h3>

        <div class="form-group">
          <label for="custom-name">장소 이름</label>
          <input
            id="custom-name"
            v-model="customForm.name"
            type="text"
            placeholder="예: 우리 집"
            required
          />
        </div>

        <div class="form-group">
          <label for="custom-memo">메모</label>
          <textarea
            id="custom-memo"
            v-model="customForm.memo"
            type="text"
            placeholder="장소에 대한 메모를 입력하세요."
          ></textarea>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="custom-latitude">위도</label>
            <input
              id="custom-latitude"
              v-model="customForm.latitude"
              type="number"
              step="0.000001"
              placeholder="37.5665"
              required
            />
          </div>

          <div class="form-group">
            <label for="custom-longitude">경도</label>
            <input
              id="custom-longitude"
              v-model="customForm.longitude"
              type="number"
              step="0.000001"
              placeholder="126.9780"
              required
            />
          </div>
        </div>

        <div class="form-actions">
          <button type="button" class="cancel-button" @click="closeCustomForm">취소</button>
          <button type="button" class="save-button" @click="saveCustomLocation">저장</button>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div
      v-if="showDeleteConfirm"
      class="custom-location-form-overlay"
      @click="showDeleteConfirm = false"
    >
      <div class="confirm-modal" @click.stop>
        <h3>커스텀 장소 삭제</h3>
        <p>정말로 "{{ placeToDelete?.name }}" 장소를 삭제하시겠습니까?</p>
        <div class="form-actions">
          <button type="button" class="cancel-button" @click="showDeleteConfirm = false">
            취소
          </button>
          <button type="button" class="delete-button" @click="deleteCustomPlace">삭제</button>
        </div>
      </div>
    </div>
  </div>

  <!-- Map Right-Click Popup -->
  <div
    v-if="showRightClickPopup"
    class="map-right-click-popup"
    :style="{ top: rightClickPosition.y + 'px', left: rightClickPosition.x + 'px' }"
  >
    <button @click="openAddCustomForm">커스텀 장소 추가</button>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import axios from 'axios'
import { customPlaceService } from '../../services/customPlaceService'
import { useAuthStore } from '../../stores/auth'
import { attractionService } from '../../services/attractionService'

const props = defineProps({
  isActive: {
    type: Boolean,
    default: false,
  },
  initialLocation: {
    type: Object,
    default: null,
  },
  mapInstance: {
    type: Object,
    default: null,
  },
})

const emit = defineEmits(['close', 'select-place'])

// State
const activeTab = ref('search')
const searchQuery = ref('')
const selectedCategory = ref('all')
const isLoading = ref(false)
const searchResults = ref([])
const customLocations = ref([])
const showAddCustomForm = ref(false)
const showDeleteConfirm = ref(false)
const placeToDelete = ref(null)

// Right-click mode
const rightClickModeActive = ref(false)
const originalMapOptions = ref(null)
const mapClickListener = ref(null)
const mapRightClickListener = ref(null)

// Right-click popup state
const showRightClickPopup = ref(false)
const rightClickPosition = ref({ x: 0, y: 0 })
const rightClickLatLng = ref(null)

// Custom location form
const customForm = ref({
  name: '',
  memo: '',
  latitude: '',
  longitude: '',
})

// Mock categories
const categories = [
  { id: 'all', name: '전체' },
  { id: 'tourist', name: '관광지' },
  { id: 'restaurant', name: '음식점' },
  { id: 'accommodation', name: '숙소' },
  { id: 'shopping', name: '쇼핑' },
]

// Mock search results (in a real app, this would come from an API)
const mockPlaces = [
  {
    id: 1,
    name: '경복궁',
    address: '서울특별시 종로구 사직로 161',
    category: '관광지',
    place_id: 'ChIJXWlELOhiYTURIlLnQNEXJo',
    latitude: 37.5796,
    longitude: 126.977,
  },
  {
    id: 2,
    name: '명동 쇼핑거리',
    address: '서울특별시 중구 명동길',
    category: '쇼핑',
    place_id: 'ChIJC15lyP6jfDURKA2qFbLQRH',
    latitude: 37.5635,
    longitude: 126.9856,
  },
  {
    id: 3,
    name: '남산서울타워',
    address: '서울특별시 용산구 남산공원길 105',
    category: '관광지',
    place_id: 'ChIJqWqOqFeifDURpYJ5WJ9sZs',
    latitude: 37.5511,
    longitude: 126.9882,
  },
  {
    id: 4,
    name: '광화문광장',
    address: '서울특별시 종로구 세종로 172',
    category: '관광지',
    place_id: 'ChIJj8YbYOhiYTURwRoQnxZFBQ',
    latitude: 37.5725,
    longitude: 126.9766,
  },
  {
    id: 5,
    name: '인사동',
    address: '서울특별시 종로구 인사동길',
    category: '쇼핑',
    place_id: 'ChIJLZYkTOtiYTURmX9OIm6TqI',
    latitude: 37.5744,
    longitude: 126.9845,
  },
]

// Methods
// function searchPlaces() {
//   if (!searchQuery.value.trim()) return

//   isLoading.value = true

//   // Simulate API call
//   setTimeout(() => {
//     // Filter by category and search query
//     const filteredResults = mockPlaces.filter((place) => {
//       const matchesCategory =
//         selectedCategory.value === 'all' ||
//         place.category === getCategoryName(selectedCategory.value)
//       const matchesQuery =
//         place.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
//         place.address.toLowerCase().includes(searchQuery.value.toLowerCase())

//       return matchesCategory && matchesQuery
//     })

//     searchResults.value = filteredResults
//     isLoading.value = false
//   }, 500)
// }
async function searchPlaces() {
  isLoading.value = true

  try {
    const contentTypeId =
      selectedCategory.value === 'all' ? null : getCategoryContentTypeId(selectedCategory.value)
    const keyword = searchQuery.value.trim() || null

    console.log('🔵 API CALL - Keyword:', keyword, 'ContentType:', contentTypeId)
    const response = await attractionService.searchAttractions(keyword, contentTypeId)
    console.log('🔵 API RESPONSE:', response)

    // 🔥 RegionalDestinations 로직 참고 - 응답 구조 확인
    let attractions = []

    if (response && response.data && Array.isArray(response.data)) {
      attractions = response.data
    } else if (response && Array.isArray(response)) {
      attractions = response
    } else if (response && response.items && Array.isArray(response.items)) {
      attractions = response.items
    } else {
      console.log('🔴 Unexpected response format:', response)
      attractions = []
    }

    console.log('🔵 Processed attractions:', attractions)

    // 🔥 RegionalDestinations와 동일한 변환 로직 적용
    const formattedResults = attractions.map((attraction) => ({
      id: attraction.contentId || attraction.contentid || attraction.id,
      name: attraction.title || attraction.name || '이름 없음',
      address: attraction.addr1 || attraction.addr2 || attraction.address || '주소 정보 없음',
      category: getCategoryName(attraction.contentTypeId || attraction.contenttypeid) || '관광지',
      place_id: '', // 빈 문자열
      content_id: attraction.contentId || attraction.contentid || attraction.id,
      latitude: parseFloat(attraction.lati || attraction.mapy || attraction.latitude) || 0,
      longitude: parseFloat(attraction.longi || attraction.mapx || attraction.longitude) || 0,
      // 🔥 추가 필드들
      image:
        attraction.firstimg ||
        attraction.secondimg ||
        '/placeholder.svg?height=300&width=400&text=No+Image',
      description: attraction.overView || attraction.overview || '설명이 없습니다.',
    }))

    console.log('🟢 Final formatted results:', formattedResults)

    // 🔥 반응성 확보를 위해 배열 교체
    searchResults.value.splice(0, searchResults.value.length, ...formattedResults)
  } catch (error) {
    console.error('🔴 Search failed:', error)
    searchResults.value.splice(0, searchResults.value.length) // 빈 배열로 초기화
    alert('검색 중 오류가 발생했습니다.')
  } finally {
    isLoading.value = false
  }
}

function getCategoryContentTypeId(categoryId) {
  const categoryMap = {
    tourist: '12', // 관광지
    restaurant: '39', // 음식점
    accommodation: '32', // 숙소
    shopping: '38', // 쇼핑
  }
  return categoryMap[categoryId] || ''
}

function getContentTypeName(contentTypeId) {
  const typeMap = {
    12: '관광지',
    39: '음식점',
    32: '숙소',
    38: '쇼핑',
    14: '문화시설',
    15: '축제공연행사',
    25: '여행코스',
    28: '레포츠',
  }
  return typeMap[contentTypeId] || '기타'
}

function getCategoryName(categoryId) {
  const category = categories.find((c) => c.id === categoryId)
  return category ? category.name : ''
}

function selectCategory(categoryId) {
  selectedCategory.value = categoryId
  searchPlaces()
}

function selectPlace(place) {
  emit('select-place', {
    id: place.id,
    name: place.name,
    address: place.address,
    place_id: '', // Empty for regular places
    content_id: place.content_id || place.id, // Keep content_id for regular places
    latitude: place.latitude,
    longitude: place.longitude,
    coordinates: { lat: place.latitude, lng: place.longitude },
  })
}

function selectCustomPlace(place) {
  console.log('Selected info:', place)

  // Use placeId instead of id since that's what exists in the data
  const placeId = place.placeId || place.id || Date.now()

  emit('select-place', {
    id: placeId,
    name: place.name,
    address: `커스텀 장소: ${formatCoordinates(place.latitude, place.longitude)}`,
    place_id: `custom-${placeId}`, // Use placeId for custom places
    content_id: '', // Empty for custom places
    latitude: place.latitude,
    longitude: place.longitude,
    coordinates: { lat: place.latitude, lng: place.longitude },
    memo: place.memo,
    isCustom: true,
  })
}

function close() {
  // Deactivate right-click mode if active
  if (rightClickModeActive.value) {
    deactivateRightClickMode()
  }

  emit('close')
}

function formatCoordinates(lat, lng) {
  return `${parseFloat(lat).toFixed(4)}, ${parseFloat(lng).toFixed(4)}`
}

function toggleRightClickMode() {
  if (rightClickModeActive.value) {
    deactivateRightClickMode()
  } else {
    activateRightClickMode()
  }
}

function activateRightClickMode() {
  const mapInstance = getMapInstance()
  if (!mapInstance) return

  // Store original options
  originalMapOptions.value = {
    draggable: mapInstance.get('draggable'),
    clickableIcons: mapInstance.get('clickableIcons'),
    gestureHandling: mapInstance.get('gestureHandling'),
  }

  // Set map options to make right-clicking easier
  mapInstance.setOptions({
    draggable: false,
    clickableIcons: false,
    gestureHandling: 'none',
  })

  // Change cursor style
  const mapDiv = mapInstance.getDiv()
  if (mapDiv) {
    mapDiv.style.cursor = 'crosshair'
  }

  // Add click listener for this mode
  if (window.google && window.google.maps) {
    // Remove any existing listeners
    if (mapClickListener.value) {
      window.google.maps.event.removeListener(mapClickListener.value)
    }

    // Add new click listener
    mapClickListener.value = window.google.maps.event.addListener(
      mapInstance,
      'click',
      handleMapClick,
    )
  }

  rightClickModeActive.value = true
}

function deactivateRightClickMode() {
  const mapInstance = getMapInstance()
  if (!mapInstance) return

  // Restore original options
  if (originalMapOptions.value) {
    mapInstance.setOptions(originalMapOptions.value)
  }

  // Reset cursor
  const mapDiv = mapInstance.getDiv()
  if (mapDiv) {
    mapDiv.style.cursor = ''
  }

  // Remove click listener
  if (mapClickListener.value) {
    window.google.maps.event.removeListener(mapClickListener.value)
    mapClickListener.value = null
  }

  rightClickModeActive.value = false
}

function handleMapClick(event) {
  // In right-click mode, treat normal clicks as if they were right-clicks
  if (rightClickModeActive.value) {
    // Get the clicked position
    rightClickPosition.value = {
      x: event.domEvent.clientX,
      y: event.domEvent.clientY,
    }

    // Save the lat/lng for the form
    rightClickLatLng.value = {
      lat: event.latLng.lat(),
      lng: event.latLng.lng(),
    }

    // Open the form directly without showing the popup
    openAddCustomForm()
  }
}

function handleMapRightClick(event) {
  // Hide any existing popup
  hideRightClickPopup()

  // Prevent the default context menu
  if (event.domEvent) {
    event.domEvent.preventDefault()
  }

  // Save the clicked position for the popup
  rightClickPosition.value = {
    x: event.domEvent.clientX,
    y: event.domEvent.clientY,
  }

  // Save the lat/lng for the form
  rightClickLatLng.value = {
    lat: event.latLng.lat(),
    lng: event.latLng.lng(),
  }

  // Show the popup
  showRightClickPopup.value = true

  // Add a click event listener to close the popup when clicking elsewhere
  setTimeout(() => {
    document.addEventListener('click', hideRightClickPopup)
  }, 0)
}

function hideRightClickPopup() {
  showRightClickPopup.value = false
  document.removeEventListener('click', hideRightClickPopup)

  // Remove any manually created popups that might be in the DOM
  const existingPopups = document.querySelectorAll(
    '.map-right-click-popup, .global-custom-place-popup',
  )
  existingPopups.forEach((popup) => {
    if (popup.parentNode) {
      popup.parentNode.removeChild(popup)
    }
  })
}

function openAddCustomForm() {
  // Hide the right-click popup
  hideRightClickPopup()

  // Set the coordinates in the form
  if (rightClickLatLng.value) {
    customForm.value.latitude = rightClickLatLng.value.lat
    customForm.value.longitude = rightClickLatLng.value.lng
  }

  // Show the form
  showAddCustomForm.value = true
}

function closeCustomForm() {
  showAddCustomForm.value = false

  // Reset form data
  customForm.value = {
    name: '',
    memo: '',
    latitude: '',
    longitude: '',
  }
}

async function fetchCustomLocations() {
  try {
    isLoading.value = true
    const authStore = useAuthStore()
    const userId = authStore.user?.mno || 'guest'

    console.log('Fetching custom places for userId:', userId)
    const data = await customPlaceService.getCustomPlaces(userId)
    console.log('Received custom places data:', data)

    // Server always returns JSON or JSON with array
    customLocations.value = Array.isArray(data) ? data : [data]

    console.log('Custom locations set to:', customLocations.value)
  } catch (error) {
    console.error('Failed to fetch custom places:', error)
    customLocations.value = []

    // Just log the error, don't show alert or redirect
    console.warn('Could not load custom places. User can still add new ones.')
  } finally {
    isLoading.value = false
  }
}

async function saveCustomLocation() {
  // Validate form
  if (!customForm.value.name.trim() || !customForm.value.latitude || !customForm.value.longitude) {
    alert('장소 이름, 위도, 경도는 필수 입력 항목입니다.')
    return
  }

  try {
    isLoading.value = true
    const authStore = useAuthStore()
    const userId = authStore.user?.mno || 'guest'

    const newLocation = {
      name: customForm.value.name.trim(),
      memo: customForm.value.memo.trim(),
      latitude: parseFloat(customForm.value.latitude),
      longitude: parseFloat(customForm.value.longitude),
      userId: userId,
    }

    const createdPlace = await customPlaceService.createCustomPlace(newLocation)

    // Refresh the custom locations list immediately
    await fetchCustomLocations()

    // Reset form and close
    closeCustomForm()

    // Switch to custom tab
    activeTab.value = 'custom'

    alert('커스텀 장소가 성공적으로 추가되었습니다.')
  } catch (error) {
    console.error('Failed to save custom place:', error)
    alert(error.message || '장소 추가 중 오류가 발생했습니다.')
  } finally {
    isLoading.value = false
  }
}

function confirmDeleteCustomPlace(place) {
  placeToDelete.value = place
  showDeleteConfirm.value = true
}

async function deleteCustomPlace() {
  if (!placeToDelete.value) return

  try {
    isLoading.value = true

    // Use placeId for deletion
    const deleteId = placeToDelete.value.placeId || placeToDelete.value.id
    await customPlaceService.deleteCustomPlace(deleteId)

    // 성공적으로 삭제되면 로컬 목록에서도 제거
    customLocations.value = customLocations.value.filter(
      (p) => (p.placeId || p.id) !== (placeToDelete.value.placeId || placeToDelete.value.id),
    )

    // Reset and close
    placeToDelete.value = null
    showDeleteConfirm.value = false

    // 성공 메시지 표시
    alert('커스텀 장소가 성공적으로 삭제되었습니다.')
  } catch (error) {
    console.error('Failed to delete custom place:', error)
    alert(error.message || '장소 삭제 중 오류가 발생했습니다.')
  } finally {
    isLoading.value = false
  }
}

function getMapInstance() {
  return props.mapInstance || window.koreaMap || null
}

// Add these new functions
function showPlaceOnMap(place) {
  const coordinates = { lat: place.latitude, lng: place.longitude }

  if (window.koreaMap) {
    window.koreaMap.panTo(coordinates)
    window.koreaMap.setZoom(15)

    // Create a temporary marker to show the location
    if (window.tempMarker) {
      window.tempMarker.setMap(null)
    }

    window.tempMarker = new window.google.maps.Marker({
      position: coordinates,
      map: window.koreaMap,
      title: place.name,
      icon: {
        path: window.google.maps.SymbolPath.CIRCLE,
        fillColor: '#FF9800',
        fillOpacity: 1,
        strokeWeight: 2,
        strokeColor: '#ffffff',
        scale: 10,
      },
      animation: window.google.maps.Animation.BOUNCE,
    })

    // Remove the temporary marker after 3 seconds
    setTimeout(() => {
      if (window.tempMarker) {
        window.tempMarker.setMap(null)
        window.tempMarker = null
      }
    }, 3000)
  }
}

function showCustomPlaceOnMap(place) {
  const coordinates = { lat: place.latitude, lng: place.longitude }

  if (window.koreaMap) {
    window.koreaMap.panTo(coordinates)
    window.koreaMap.setZoom(15)

    // Create a temporary marker to show the location
    if (window.tempMarker) {
      window.tempMarker.setMap(null)
    }

    window.tempMarker = new window.google.maps.Marker({
      position: coordinates,
      map: window.koreaMap,
      title: place.name,
      icon: {
        path: window.google.maps.SymbolPath.CIRCLE,
        fillColor: '#9C27B0',
        fillOpacity: 1,
        strokeWeight: 2,
        strokeColor: '#ffffff',
        scale: 10,
      },
      animation: window.google.maps.Animation.BOUNCE,
    })

    // Remove the temporary marker after 3 seconds
    setTimeout(() => {
      if (window.tempMarker) {
        window.tempMarker.setMap(null)
        window.tempMarker = null
      }
    }, 3000)
  }
}

// Set up map handlers
function setupMapHandlers() {
  const mapInstance = getMapInstance()

  if (window.google && window.google.maps && mapInstance) {
    // Add context menu prevention globally
    const mapDiv = mapInstance.getDiv()
    if (mapDiv) {
      mapDiv.addEventListener('contextmenu', (e) => {
        e.preventDefault() // Always prevent context menu
      })
    }

    // Add right-click event listener globally
    if (mapRightClickListener.value) {
      window.google.maps.event.removeListener(mapRightClickListener.value)
    }

    mapRightClickListener.value = window.google.maps.event.addListener(
      mapInstance,
      'rightclick',
      handleMapRightClick,
    )
  }
}

// Remove map handlers
function removeMapHandlers() {
  // Deactivate right-click mode if active
  if (rightClickModeActive.value) {
    deactivateRightClickMode()
  }

  // Remove right-click listener
  if (mapRightClickListener.value) {
    window.google.maps.event.removeListener(mapRightClickListener.value)
    mapRightClickListener.value = null
  }

  // Remove click listener
  if (mapClickListener.value) {
    window.google.maps.event.removeListener(mapClickListener.value)
    mapClickListener.value = null
  }
}

// Initialize
onMounted(() => {
  // Show all places initially
  searchResults.value = []

  // Set up map event handlers
  setupMapHandlers()

  // Listen for the custom event to open the form
  window.addEventListener('open-custom-form', handleOpenCustomForm)
})

// Clean up
onUnmounted(() => {
  // Clean up event listeners
  hideRightClickPopup()
  removeMapHandlers()

  // Remove the custom event listener
  window.removeEventListener('open-custom-form', handleOpenCustomForm)
})

// Watch for changes in isActive
watch(
  () => props.isActive,
  (newValue) => {
    if (newValue) {
      // Reset search when sidebar becomes active
      searchQuery.value = ''
      selectedCategory.value = 'all'
      searchResults.value = mockPlaces
      activeTab.value = 'search'

      // Fetch custom places when sidebar becomes active
      fetchCustomLocations()

      // Set up map handlers
      setupMapHandlers()
    } else {
      // Remove map handlers when sidebar is inactive
      removeMapHandlers()
    }
  },
)

// Add this new function
function handleOpenCustomForm(event) {
  if (event.detail && event.detail.coordinates) {
    // Set the coordinates in the form
    customForm.value.latitude = event.detail.coordinates.lat
    customForm.value.longitude = event.detail.coordinates.lng

    // Switch to the custom tab
    activeTab.value = 'custom'

    // Show the form
    showAddCustomForm.value = true
  }
}

// 커스텀 탭이 활성화될 때 데이터 새로고침
watch(
  () => activeTab.value,
  (newTab) => {
    if (newTab === 'custom') {
      fetchCustomLocations()
    }
  },
)
</script>

<style scoped>
.place-selection-sidebar {
  position: fixed;
  top: var(--topbar-height, 60px);
  right: 0;
  bottom: 0;
  width: 350px;
  background-color: white;
  box-shadow: -2px 0 10px rgba(0, 0, 0, 0.1);
  z-index: 1500;
  display: flex;
  flex-direction: column;
  transform: translateX(100%);
  transition: transform 0.3s ease;
}

.place-selection-sidebar.active {
  transform: translateX(0);
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid var(--border-color, #e5e5e5);
  background-color: var(--primary-color, #005bac);
  color: white;
}

.sidebar-header h2 {
  margin: 0;
  font-size: 1.2rem;
}

.close-button {
  background: none;
  border: none;
  color: white;
  font-size: 1.2rem;
  cursor: pointer;
  padding: 5px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: background-color 0.2s ease;
}

.close-button:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.sidebar-content {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

/* Tabs */
.tabs {
  display: flex;
  border-bottom: 1px solid var(--border-color, #e5e5e5);
  margin-bottom: 15px;
}

.tab-button {
  flex: 1;
  padding: 10px;
  background: none;
  border: none;
  border-bottom: 2px solid transparent;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s ease;
}

.tab-button.active {
  border-bottom-color: var(--primary-color, #005bac);
  color: var(--primary-color, #005bac);
}

.tab-content {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.search-container {
  display: flex;
  gap: 10px;
}

.search-input {
  flex: 1;
  padding: 10px;
  border: 1px solid var(--border-color, #e5e5e5);
  border-radius: 4px;
  font-size: 1rem;
}

.search-button {
  padding: 10px 15px;
  background-color: var(--primary-color, #005bac);
  color: white;
  border: none;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.search-button:hover {
  background-color: #004c8e;
}

.filter-options {
  background-color: #f5f5f5;
  border-radius: 8px;
  padding: 15px;
}

.filter-group {
  margin-bottom: 15px;
}

.filter-group:last-child {
  margin-bottom: 0;
}

.filter-group label {
  display: block;
  font-weight: bold;
  margin-bottom: 8px;
  color: var(--text-color, #333);
}

.filter-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.filter-buttons button {
  padding: 6px 12px;
  background-color: white;
  border: 1px solid var(--border-color, #e5e5e5);
  border-radius: 20px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.filter-buttons button.active {
  background-color: var(--primary-color, #005bac);
  color: white;
  border-color: var(--primary-color, #005bac);
}

.results-container {
  flex: 1;
  overflow-y: auto;
  background-color: white;
  border-radius: 8px;
  border: 1px solid var(--border-color, #e5e5e5);
}

.loading-indicator {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 30px;
  color: var(--text-light, #666);
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(0, 91, 172, 0.1);
  border-left-color: var(--primary-color, #005bac);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 15px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.empty-results {
  padding: 30px;
  text-align: center;
  color: var(--text-light, #666);
}

.results-list {
  display: flex;
  flex-direction: column;
}

.place-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid var(--border-color, #e5e5e5);
  transition: background-color 0.2s ease;
  cursor: pointer;
}

.place-item:last-child {
  border-bottom: none;
}

.place-item:hover {
  background-color: #f9f9f9;
}

.place-info {
  flex: 1;
}

.place-info h3 {
  margin: 0 0 5px 0;
  font-size: 1rem;
  color: var(--text-color, #333);
}

.place-address,
.place-memo {
  margin: 0 0 5px 0;
  font-size: 0.9rem;
  color: var(--text-light, #666);
}

.place-category,
.place-coordinates {
  display: inline-block;
  padding: 2px 8px;
  background-color: #f0f0f0;
  border-radius: 12px;
  font-size: 0.8rem;
  color: var(--text-light, #666);
}

.place-actions {
  display: flex;
  gap: 5px;
}

.select-button,
.delete-button {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.select-button {
  background-color: var(--primary-color, #005bac);
  color: white;
}

.select-button:hover {
  background-color: #004c8e;
}

.delete-button {
  background-color: #f44336;
  color: white;
}

.delete-button:hover {
  background-color: #d32f2f;
}

.map-selection-info {
  text-align: center;
  padding: 15px;
  background-color: #f5f5f5;
  border-radius: 8px;
  font-size: 0.9rem;
  color: var(--text-light, #666);
}

.map-right-click-info {
  margin-top: 5px;
  font-style: italic;
  color: var(--primary-color, #005bac);
}

/* Right-click mode toggle */
.right-click-mode-toggle {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.right-click-mode-toggle button {
  padding: 8px 16px;
  background-color: var(--primary-color, #005bac);
  color: white;
  border: none;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.2s ease;
}

.right-click-mode-toggle button.active {
  background-color: #f44336;
}

.right-click-mode-hint {
  margin-top: 8px;
  font-weight: bold;
  color: #f44336;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    opacity: 0.7;
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0.7;
  }
}

/* Custom Locations Header */
.custom-locations-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.custom-locations-header h3 {
  margin: 0;
  font-size: 1.1rem;
  color: var(--text-color, #333);
}

.add-custom-button {
  padding: 6px 12px;
  background-color: var(--primary-color, #005bac);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.add-custom-button:hover {
  background-color: #004c8e;
}

/* Custom Location Form */
.custom-location-form-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.custom-location-form,
.confirm-modal {
  background-color: white;
  border-radius: 8px;
  width: 90%;
  max-width: 400px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.custom-location-form h3,
.confirm-modal h3 {
  margin-top: 0;
  color: var(--primary-color, #005bac);
  margin-bottom: 15px;
}

.form-group {
  margin-bottom: 15px;
}

.form-row {
  display: flex;
  gap: 10px;
}

.form-row .form-group {
  flex: 1;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  color: var(--text-color, #333);
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid var(--border-color, #e5e5e5);
  border-radius: 4px;
  font-size: 0.9rem;
}

.form-group textarea {
  min-height: 80px;
  resize: vertical;
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
  padding: 8px 16px;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.cancel-button {
  background-color: #f5f5f5;
  border: 1px solid var(--border-color, #e5e5e5);
  color: var(--text-color, #333);
}

.save-button {
  background-color: var(--primary-color, #005bac);
  border: none;
  color: white;
}

.delete-button {
  background-color: #f44336;
  border: none;
  color: white;
}

/* Map Right-Click Popup */
.map-right-click-popup {
  position: fixed;
  z-index: 1600;
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  overflow: hidden;
  animation: popup-appear 0.2s ease;
}

@keyframes popup-appear {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.map-right-click-popup button {
  display: block;
  width: 100%;
  padding: 10px 15px;
  background: none;
  border: none;
  text-align: left;
  font-size: 0.9rem;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.map-right-click-popup button:hover {
  background-color: #f5f5f5;
}
</style>
