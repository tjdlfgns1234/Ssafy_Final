<template>
  <ResizableSidebar 
    :isOpen="isFilterPanelOpen" 
    :width="sidebarWidth" 
    title="지역 여행지"
    @toggle="toggleFilterPanel"
    @resize="handleResize"
    :minWidth="200"
    :maxWidth="600"
  >
    <div class="destinations-container">
      <div class="filter-section">
        <div class="filter-header">
          <h2>{{ currentRegion ? `${currentRegion.toUpperCase()} 여행지` : '지역 여행지' }}</h2>
          <button 
            class="toggle-filter-button"
            @click="toggleFilterVisibility"
            aria-label="Toggle filters"
          >
            {{ isFilterVisible ? '필터 숨기기' : '필터 보기' }}
          </button>
        </div>
        
        <div class="filter-content" v-if="isFilterVisible">
          <div class="filter-group">
            <h3>위치</h3>
            <div class="filter-item">
              <label for="sido">시·도</label>
              <select id="sido" v-model="selectedSido" @change="onSidoChange" :disabled="isLoadingSido">
                <option value="">전체</option>
                <option v-if="isLoadingSido" disabled>로딩 중...</option>
                <option v-for="sido in sidoOptions" :key="sido.code" :value="sido.code">{{ sido.name }}</option>
              </select>
            </div>
            
            <div class="filter-item">
              <label for="sigungu">시·군·구</label>
              <select id="sigungu" v-model="selectedSigungu" :disabled="!selectedSido || isLoadingSigungu">
                <option value="">전체</option>
                <option v-if="isLoadingSigungu" disabled>로딩 중...</option>
                <option v-for="sigungu in sigunguOptions" :key="sigungu.code" :value="sigungu.code">{{ sigungu.name }}</option>
              </select>
            </div>
          </div>
          
          <div class="filter-group">
            <h3>카테고리</h3>
            <div class="tag-filters">
              <label v-for="category in categoryOptions" :key="category.value" class="tag-checkbox">
                <input type="checkbox" v-model="selectedCategories" :value="category.value">
                <span class="tag-label">{{ category.label }}</span>
              </label>
            </div>
          </div>
          
          <div class="filter-group">
            <h3>평점</h3>
            <div class="rating-filter">
              <label class="rating-checkbox" v-for="rating in [5, 4, 3, 2, 1]" :key="rating">
                <input type="checkbox" v-model="selectedRatings" :value="rating">
                <span class="stars">{{ '★'.repeat(rating) }}{{ '☆'.repeat(5-rating) }}</span>
                <span class="rating-label">{{ rating }}점 이상</span>
              </label>
            </div>
          </div>
          
          <div class="filter-actions">
            <button class="apply-filters-button" @click="applyFilters" :disabled="isLoadingDestinations">
              {{ isLoadingDestinations ? '검색 중...' : '필터 적용' }}
            </button>
            
            <button class="reset-filters-button" @click="resetFilters">
              필터 초기화
            </button>
          </div>
        </div>
      </div>
      
      <div class="destinations-list">
        <div class="list-header">
          <h2>검색 결과 ({{ isLoadingDestinations ? '로딩 중...' : totalItems }})</h2>
          
          <!-- Pagination info for non-virtualized lists -->
          <div v-if="!isVirtualized && !isLoadingDestinations && totalItems > 0" class="pagination-info">
            {{ paginationInfo.start }}-{{ paginationInfo.end }} / {{ paginationInfo.total }}
          </div>
        </div>
        
        <div v-if="isLoadingDestinations" class="loading-container">
          <div class="loading-spinner"></div>
          <p>여행지 정보를 불러오는 중...</p>
        </div>
        
        <!-- Virtual scrolling container for large datasets -->
        
        
        <!-- Regular grid for smaller datasets -->
        <div v-else class="destinations-grid">
          <div 
            v-for="destination in filteredDestinations" 
            :key="destination.id"
            class="destination-card"
            @click="selectDestination(destination)"
          >
            <div class="card-image">
              <img :src="destination.image" :alt="destination.name" @error="handleImageError">
              <div class="card-category">{{ destination.category }}</div>
            </div>
            <div class="card-content">
              <h3>{{ destination.name }}</h3>
              <div class="card-location">{{ destination.location }}</div>
              <div class="card-rating">
                <span class="stars">★★★★★</span>
                <span class="rating-value">{{ destination.rating }}</span>
                <span class="reviews-count">({{ destination.reviews }})</span>
              </div>
              <p class="card-description">{{ destination.description }}</p>
              <button class="view-details" @click.stop="addToTravelPlan(destination)">
                여행지로 추가
              </button>
            </div>
          </div>
        </div>
        
        <!-- Pagination controls for non-virtualized lists -->
        
        <Pagination 
          v-if="!isLoadingDestinations && totalPages > 1"
          :currentPage="currentPage"
          :totalPages="totalPages"
          @page-change="handlePageChange"
        />
        
        <div v-if="!isLoadingDestinations && filteredDestinations.length === 0" class="no-results">
          <p>선택한 필터에 맞는 여행지가 없습니다. 다른 필터를 선택해 보세요.</p>
        </div>
      </div>
    </div>
  </ResizableSidebar>
  
  <!-- Toggle button that appears when sidebar is closed -->
  <button 
    v-if="!isFilterPanelOpen"
    class="open-filter-button"
    @click="toggleFilterPanel"
    aria-label="Open filters"
  >
    <span>지역 여행지</span>
  </button>
  
  <div v-if="selectedDestination" class="destination-detail-overlay">
    <div class="detail-header">
      <h2>{{ selectedDestination.name }}</h2>
      <button class="close-detail-button" @click="selectedDestination = null">✕</button>
    </div>
    <div class="detail-content">
      <div class="detail-image">
        <img :src="selectedDestination.image" :alt="selectedDestination.name" @error="handleImageError">
      </div>
      <div class="detail-info">
        <div class="detail-location">{{ selectedDestination.location }}</div>
        <div class="detail-rating">
          <span class="stars">★★★★★</span>
          <span class="rating-value">{{ selectedDestination.rating }}</span>
          <span class="reviews-count">({{ selectedDestination.reviews }} 리뷰)</span>
        </div>
        <div class="detail-category">{{ selectedDestination.category }}</div>
        <p class="detail-description">{{ selectedDestination.description }}</p>
        <div class="detail-map" ref="detailMapContainer" style="height: 200px; margin-bottom: 15px;"></div>
        <button class="view-details-button" @click="addToTravelPlan(selectedDestination)">
          여행지로 추가
        </button>
      </div>
    </div>
  </div>
  
  <div class="map-container" ref="mapContainer"></div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, watch } from 'vue';
import ResizableSidebar from '../components/layout/ResizableSidebar.vue';
import { useRoute } from 'vue-router';
import { areaCodeService } from '../services/areaCodeService.js';
import { attractionService } from '../services/attractionService.js';
import Pagination from '../components/common/Pagination.vue';

// Add after the existing imports
const route = useRoute();

// Add after the existing imports
const currentRegion = ref('');

// Mock data - Destinations
const destinations = ref([
  {
    id: 1,
    name: "경복궁",
    description: "조선 왕조의 법궁으로, 서울 중심부에 위치한 웅장한 궁궐입니다.",
    image: "https://via.placeholder.com/400x300?text=Gyeongbokgung",
    location: "서울특별시 종로구",
    areaCode: "1", // 서울특별시
    sigunguCode: "1", // 종로구 (예시)
    contentType: "12", // 관광지
    rating: 4.7,
    reviews: 1250,
    category: "관광지",
    coordinates: { lat: 37.5796, lng: 126.9770 },
    place_id: "ChIJXWlH4Fqjfg4RbDy7wJZC-aE"
  },
  {
    id: 2,
    name: "남산서울타워",
    description: "서울 중심부 남산에 위치한 전망대로, 서울의 아름다운 전경을 감상할 수 있습니다.",
    image: "https://via.placeholder.com/400x300?text=Namsan+Tower",
    location: "서울특별시 용산구",
    areaCode: "1",
    sigunguCode: "2",
    contentType: "12",
    rating: 4.5,
    reviews: 980,
    category: "관광지",
    coordinates: { lat: 37.5512, lng: 126.9882 },
    place_id: "ChIJqWqOqFeifDURpYJ5WwGfZ2g"
  },
  {
    id: 3,
    name: "북촌한옥마을",
    description: "600년의 역사를 가진 전통 한옥 마을로, 한국의 전통 문화를 체험할 수 있습니다.",
    image: "https://via.placeholder.com/400x300?text=Bukchon+Hanok",
    location: "서울특별시 종로구",
    areaCode: "1",
    sigunguCode: "1",
    contentType: "14",
    rating: 4.6,
    reviews: 850,
    category: "문화시설",
    coordinates: { lat: 37.5826, lng: 126.9860 },
    place_id: "ChIJb1Jc9FKjfg4RNmN3d3ynJpg"
  },
  {
    id: 4,
    name: "해운대 해수욕장",
    description: "부산의 대표적인 해변으로, 아름다운 해안선과 다양한 해양 활동을 즐길 수 있습니다.",
    image: "https://via.placeholder.com/400x300?text=Haeundae+Beach",
    location: "부산광역시 해운대구",
    areaCode: "6",
    sigunguCode: "2",
    contentType: "12",
    rating: 4.8,
    reviews: 1500,
    category: "관광지",
    coordinates: { lat: 35.1588, lng: 129.1600 },
    place_id: "ChIJAQAAAKO0cDURVbI3N-bZfpQ"
  },
  {
    id: 5,
    name: "감천문화마을",
    description: "부산의 '마추픽추'라 불리는 이 마을은 계단식 거리와 알록달록한 집들로 유명합니다.",
    image: "https://via.placeholder.com/400x300?text=Gamcheon+Village",
    location: "부산광역시 사하구",
    areaCode: "6",
    sigunguCode: "5",
    contentType: "14",
    rating: 4.7,
    reviews: 1100,
    category: "문화시설",
    coordinates: { lat: 35.0979, lng: 129.0122 },
    place_id: "ChIJE8KtXCqDcDURKOZBJZwA9Sg"
  },
  {
    id: 6,
    name: "성산일출봉",
    description: "제주도의 상징적인 자연 명소로, 수중 화산 폭발로 형성된 독특한 지형을 가지고 있습니다.",
    image: "https://via.placeholder.com/400x300?text=Seongsan+Ilchulbong",
    location: "제주특별자치도 서귀포시",
    areaCode: "39",
    sigunguCode: "2",
    contentType: "12",
    rating: 4.9,
    reviews: 2000,
    category: "관광지",
    coordinates: { lat: 33.4596, lng: 126.9425 },
    place_id: "ChIJXWlH4Fqjfg4RbDy7wJZC-aE"
  },
  {
    id: 7,
    name: "한라산",
    description: "제주도의 중앙에 위치한 한국에서 가장 높은 산으로, 아름다운 자연 경관을 자랑합니다.",
    image: "https://via.placeholder.com/400x300?text=Hallasan",
    location: "제주특별자치도",
    areaCode: "39",
    sigunguCode: "0",
    contentType: "12",
    rating: 4.8,
    reviews: 1800,
    category: "관광지",
    coordinates: { lat: 33.3616, lng: 126.5292 },
    place_id: "ChIJnUEY9jDYDDURIqhkjXkwR54"
  },
  {
    id: 8,
    name: "롯데호텔 서울",
    description: "서울 중심부에 위치한 5성급 호텔로, 럭셔리한 객실과 다양한 부대시설을 제공합니다.",
    image: "https://via.placeholder.com/400x300?text=Lotte+Hotel",
    location: "서울특별시 중구",
    areaCode: "1",
    sigunguCode: "3",
    contentType: "32",
    rating: 4.6,
    reviews: 950,
    category: "숙소",
    coordinates: { lat: 37.5656, lng: 126.9807 },
    place_id: "ChIJA8CZZFKifDURyj3fSFr37s0"
  },
  {
    id: 9,
    name: "신라호텔 제주",
    description: "제주도에 위치한 럭셔리 리조트로, 아름다운 해변과 고급스러운 시설을 갖추고 있습니다.",
    image: "https://via.placeholder.com/400x300?text=Shilla+Jeju",
    location: "제주특별자치도 서귀포시",
    areaCode: "39",
    sigunguCode: "2",
    contentType: "32",
    rating: 4.7,
    reviews: 1200,
    category: "숙소",
    coordinates: { lat: 33.2478, lng: 126.4097 },
    place_id: "ChIJr_IHQlTZDDURUFsgj3tHG2s"
  },
  {
    id: 10,
    name: "광장시장",
    description: "서울에서 가장 오래된 전통시장 중 하나로, 다양한 한국 길거리 음식을 맛볼 수 있습니다.",
    image: "https://via.placeholder.com/400x300?text=Gwangjang+Market",
    location: "서울특별시 종로구",
    areaCode: "1",
    sigunguCode: "1",
    contentType: "39",
    rating: 4.5,
    reviews: 1300,
    category: "맛집",
    coordinates: { lat: 37.5700, lng: 126.9986 },
    place_id: "ChIJm-R1Ok-ifDURWLPYwq9-a9c"
  },
  {
    id: 11,
    name: "자갈치시장",
    description: "부산의 대표적인 수산시장으로, 신선한 해산물과 다양한 해물 요리를 즐길 수 있습니다.",
    image: "https://via.placeholder.com/400x300?text=Jagalchi+Market",
    location: "부산광역시 중구",
    areaCode: "6",
    sigunguCode: "3",
    contentType: "39",
    rating: 4.4,
    reviews: 980,
    category: "맛집",
    coordinates: { lat: 35.0967, lng: 129.0304 },
    place_id: "ChIJK_K_7qqFaDURhQYfstakoTA"
  },
  {
    id: 12,
    name: "국립중앙박물관",
    description: "한국의 역사와 문화를 전시하는 국립 박물관으로, 다양한 유물과 예술품을 소장하고 있습니다.",
    image: "https://via.placeholder.com/400x300?text=National+Museum",
    location: "서울특별시 용산구",
    areaCode: "1",
    sigunguCode: "2",
    contentType: "14",
    rating: 4.6,
    reviews: 870,
    category: "문화시설",
    coordinates: { lat: 37.5240, lng: 126.9803 },
    place_id: "ChIJkU0C2YKifDURhQYfstakoTA"
  },
  {
    id: 13,
    name: "부산국제영화제",
    description: "매년 10월에 개최되는 아시아 최대 규모의 영화제로, 다양한 국제 영화를 상영합니다.",
    image: "https://via.placeholder.com/400x300?text=Busan+Film+Festival",
    location: "부산광역시 해운대구",
    areaCode: "6",
    sigunguCode: "2",
    contentType: "14",
    rating: 4.7,
    reviews: 750,
    category: "행사",
    coordinates: { lat: 35.1691, lng: 129.1276 },
    place_id: "ChIJE8KtXCqDcDURKOZBJZwA9Sg"
  },
  {
    id: 14,
    name: "진주남강유등축제",
    description: "매년 10월에 개최되는 전통 등불 축제로, 아름다운 등불과 문화 행사를 즐길 수 있습니다.",
    image: "https://via.placeholder.com/400x300?text=Jinju+Lantern+Festival",
    location: "경상남도 진주시",
    areaCode: "38",
    sigunguCode: "31",
    contentType: "14",
    rating: 4.8,
    reviews: 920,
    category: "행사",
    coordinates: { lat: 35.1796, lng: 128.0756 },
    place_id: "ChIJE8KtXCqDcDURKOZBJZwA9Sg"
  },
  {
    id: 15,
    name: "설악산 국립공원",
    description: "한국에서 세 번째로 높은 산이 있는 국립공원으로, 아름다운 자연 경관과 하이킹 코스를 제공합니다.",
    image: "https://via.placeholder.com/400x300?text=Seoraksan",
    location: "강원도 속초시",
    areaCode: "32",
    sigunguCode: "31",
    contentType: "12",
    rating: 4.9,
    reviews: 1600,
    category: "관광지",
    coordinates: { lat: 38.1195, lng: 128.4656 },
    place_id: "ChIJE8KtXCqDcDURKOZBJZwA9Sg"
  }
]);

// 실제 API에서 가져온 여행지 데이터
const realDestinations = ref([]);
const isLoadingDestinations = ref(false);

// Filter options
const sidoOptions = ref([]);
const sigunguOptions = ref([]);
const isLoadingSido = ref(false);
const isLoadingSigungu = ref(false);

const categoryOptions = [
  { label: '관광지', value: '12' },
  { label: '문화시설', value: '14' },
  { label: '숙박', value: '32' },
  { label: '카페', value: '38' },
  { label: '음식점', value: '39' }
];

// State
const selectedSido = ref('');
const selectedSigungu = ref('');
const selectedCategories = ref([]);
const selectedRatings = ref([]);
const isFilterPanelOpen = ref(true);
const sidebarWidth = ref(450);
const isFilterVisible = ref(true);
const selectedDestination = ref(null);

// Add these new state variables after the existing state variables (around line 600)
const appliedSido = ref('');
const appliedSigungu = ref('');
const appliedCategories = ref([]);
const appliedRatings = ref([]);

// Pagination and performance
const currentPage = ref(1);
const itemsPerPage = ref(20);
const totalItems = ref(0);
const visibleDestinations = ref([]);
const isVirtualized = ref(false);
const virtualScrollContainer = ref(null);
const itemHeight = ref(340); // Height of each destination card
const containerHeight = ref(600);
const scrollTop = ref(0);
const visibleStartIndex = ref(0);
const visibleEndIndex = ref(20);
const bufferSize = ref(5); // Extra items to render for smooth scrolling

const mapContainer = ref(null);
const detailMapContainer = ref(null);

// Computed
// 시도 목록 로드
async function loadSidoOptions() {
  isLoadingSido.value = true;
  try {
    const sidoList = await areaCodeService.getSidoList();
    sidoOptions.value = sidoList;
  } catch (error) {
    console.error('시도 목록 로드 실패:', error);
  } finally {
    isLoadingSido.value = false;
  }
}

// 시군구 목록 로드
async function loadSigunguOptions(areaCode) {
  if (!areaCode) {
    sigunguOptions.value = [];
    return;
  }
  
  isLoadingSigungu.value = true;
  try {
    const sigunguList = await areaCodeService.getSigunguList(areaCode);
    sigunguOptions.value = sigunguList;
  } catch (error) {
    console.error('시군구 목록 로드 실패:', error);
    sigunguOptions.value = [];
  } finally {
    isLoadingSigungu.value = false;
  }
}

// 시도 변경시 시군구 초기화 및 로드
async function onSidoChange() {
  selectedSigungu.value = '';
  if (selectedSido.value) {
    await loadSigunguOptions(selectedSido.value);
  } else {
    sigunguOptions.value = [];
  }
}

// Replace the filteredDestinations computed property with this updated version
const filteredDestinations = computed(() => {
  // 실제 데이터가 있으면 실제 데이터 사용, 없으면 목 데이터 사용
  const sourceData = realDestinations.value.length > 0 ? realDestinations.value : destinations.value;
  let result = [...sourceData];
  
  // Clear console logging to show data source
  if (realDestinations.value.length > 0) {
    console.log('🔴 USING REAL API DATA:', {
      totalRealData: realDestinations.value.length,
      currentPage: currentPage.value,
      itemsPerPage: itemsPerPage.value,
      totalItems: totalItems.value
    });
  } else {
    console.log('🟡 USING MOCK DATA:', {
      totalMockData: destinations.value.length,
      currentPage: currentPage.value,
      itemsPerPage: itemsPerPage.value,
      appliedFilters: {
        sido: appliedSido.value,
        sigungu: appliedSigungu.value,
        categories: appliedCategories.value
      }
    });
  }
  
  // Filter by route region parameter first (목 데이터에만 적용)
  if (currentRegion.value && realDestinations.value.length === 0) {
    const regionMap = {
      'seoul': '1',
      'busan': '6', 
      'jeju': '39',
      'gangwon': '32'
    };
    const regionCode = regionMap[currentRegion.value];
    if (regionCode) {
      result = result.filter(dest => dest.areaCode === regionCode);
      console.log('🟡 MOCK DATA - Applied region filter:', currentRegion.value, 'Filtered count:', result.length);
    }
  }
  
  // 실제 데이터의 경우 이미 API에서 필터링되어 오므로 추가 필터링 불필요
  if (realDestinations.value.length > 0) {
    totalItems.value = result.length;
    
    isVirtualized.value = false;
    // For smaller datasets, use pagination
    const startIndex = (currentPage.value - 1) * itemsPerPage.value;
    const endIndex = startIndex + itemsPerPage.value;
    const paginatedResult = result.slice(startIndex, endIndex);
    
    console.log('🔴 REAL DATA - Pagination applied:', {
      startIndex,
      endIndex,
      resultCount: paginatedResult.length
    });
    
    return paginatedResult;
  }
  
  // 목 데이터의 경우에만 클라이언트 사이드 필터링 적용
  if (appliedSido.value) {
    result = result.filter(dest => dest.areaCode === appliedSido.value);
    console.log('🟡 MOCK DATA - Applied sido filter:', appliedSido.value, 'Filtered count:', result.length);
    
    if (appliedSigungu.value) {
      result = result.filter(dest => dest.sigunguCode === appliedSigungu.value);
      console.log('🟡 MOCK DATA - Applied sigungu filter:', appliedSigungu.value, 'Filtered count:', result.length);
    }
  }
  
  if (appliedCategories.value.length > 0) {
    result = result.filter(dest => appliedCategories.value.includes(dest.contentType));
    console.log('🟡 MOCK DATA - Applied category filter:', appliedCategories.value, 'Filtered count:', result.length);
  }
  
  totalItems.value = result.length;
  
  // Use pagination for mock data
  const startIndex = (currentPage.value - 1) * itemsPerPage.value;
  const endIndex = startIndex + itemsPerPage.value;
  const paginatedResult = result.slice(startIndex, endIndex);
  
  console.log('🟡 MOCK DATA - Final pagination:', {
    totalFiltered: result.length,
    startIndex,
    endIndex,
    resultCount: paginatedResult.length
  });
  
  return paginatedResult;
});

// Update the applyFilters function
async function applyFilters() {
  console.log('🔵 APPLY FILTERS CALLED');
  
  // Apply the selected filters
  appliedSido.value = selectedSido.value;
  appliedSigungu.value = selectedSigungu.value;
  appliedCategories.value = [...selectedCategories.value];
  appliedRatings.value = [...selectedRatings.value];
  
  console.log('🔵 Applied filters:', {
    sido: appliedSido.value,
    sigungu: appliedSigungu.value,
    categories: appliedCategories.value,
    ratings: appliedRatings.value
  });
  
  // Reset to first page when applying new filters
  currentPage.value = 1;
  
  // 실제 여행지 데이터 로드
  await loadRealDestinations();
  
  // Update map markers
  nextTick(() => {
    updateMapMarkers();
  });
}

// 실제 여행지 데이터 로드
async function loadRealDestinations(page = 1) {
  console.log('🔵 STARTING API CALL:', {
    page,
    itemsPerPage: itemsPerPage.value,
    filters: {
      sido: appliedSido.value,
      sigungu: appliedSigungu.value,
      categories: appliedCategories.value
    }
  });
  
  isLoadingDestinations.value = true;
  
  try {
    const promises = [];
    
    if (appliedCategories.value.length > 0) {
      for (const contentType of appliedCategories.value) {
        const params = {
          contentType: contentType,
          page: page,
          limit: itemsPerPage.value
        };
        
        if (appliedSido.value) {
          params.areaCode = appliedSido.value;
        }
        
        if (appliedSigungu.value) {
          params.sigunguCode = appliedSigungu.value;
        }
        
        console.log('🔵 API CALL - Category:', contentType, 'Params:', params);
        promises.push(attractionService.getAttractions(params));
      }
    } else {
      const params = {
        page: page,
        limit: itemsPerPage.value
      };
      
      if (appliedSido.value) {
        params.areaCode = appliedSido.value;
      }
      
      if (appliedSigungu.value) {
        params.sigunguCode = appliedSigungu.value;
      }
      
      console.log('🔵 API CALL - No category filter, Params:', params);
      promises.push(attractionService.getAttractions(params));
    }
    
    const results = await Promise.all(promises);
    console.log('🔵 API RESPONSE:', results);
    
    // 서버에서 페이지네이션된 결과 처리
    const allAttractions = [];
    let totalCount = 0;
    
    results.forEach((result, index) => {
      console.log(`🔵 Processing result ${index}:`, result);
      
      if (result && result.data && Array.isArray(result.data)) {
        allAttractions.push(...result.data);
        totalCount = Math.max(totalCount, result.totalCount || 0);
        console.log(`🔵 Result ${index} - Data count:`, result.data.length, 'Total count:', result.totalCount);
      } else if (result && Array.isArray(result)) {
        // Handle case where API returns array directly
        allAttractions.push(...result);
        console.log(`🔵 Result ${index} - Direct array, count:`, result.length);
      } else {
        console.log(`🔵 Result ${index} - Unexpected format:`, result);
      }
    });
    
    // 중복 제거
    const uniqueAttractions = allAttractions.filter((attraction, index, self) => 
      index === self.findIndex(a => a.contentId === attraction.contentId)
    );
    
    console.log('🔵 PROCESSED DATA:', {
      totalAttractions: allAttractions.length,
      uniqueAttractions: uniqueAttractions.length,
      totalCount: totalCount
    });
    
    if (uniqueAttractions.length > 0) {
      console.log('🔵 SAMPLE ATTRACTION DATA:', uniqueAttractions[0]);
    }
    
    // 데이터 변환
    realDestinations.value = uniqueAttractions.map(attraction => ({
      id: attraction.contentId,
      name: attraction.title || '이름 없음',
      description: attraction.overView || '설명이 없습니다.',
      image: attraction.firstimg || attraction.secondimg || '/placeholder.svg?height=300&width=400&text=No+Image',
      location: attraction.addr1 || '주소 정보 없음',
      areaCode: attraction.areaCode,
      sigunguCode: attraction.giGunGuCode,
      contentType: attraction.contentTypeId,
      rating: 4.5,
      reviews: Math.floor(Math.random() * 1000) + 100,
      category: getCategoryName(attraction.contentTypeId),
      coordinates: {
        lat: parseFloat(attraction.lati) || 37.5665,
        lng: parseFloat(attraction.longi) || 126.9780
      },
      homepage: attraction.homepage || '',
      place_id: `place_${attraction.contentId}`
    }));
    
    totalItems.value = totalCount || uniqueAttractions.length;
    
    console.log('🟢 REAL DATA LOADED SUCCESSFULLY:', {
      transformedCount: realDestinations.value.length,
      totalItems: totalItems.value
    });
    
  } catch (error) {
    console.error('🔴 ERROR loading real destinations:', error);
    realDestinations.value = [];
    totalItems.value = 0;
  } finally {
    isLoadingDestinations.value = false;
  }
}

// Virtual scrolling functions


// Pagination functions
function goToPage(page) {
  currentPage.value = page;
}

function nextPage() {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
  }
}

function prevPage() {
  if (currentPage.value > 1) {
    currentPage.value--;
  }
}

const totalPages = computed(() => {
  return Math.ceil(totalItems.value / itemsPerPage.value);
});

const paginationInfo = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage.value + 1;
  const end = Math.min(currentPage.value * itemsPerPage.value, totalItems.value);
  return { start, end, total: totalItems.value };
});

// 카테고리 코드를 이름으로 변환
function getCategoryName(contentTypeId) {
  const categoryMap = {
    '12': '관광지',
    '14': '문화시설',
    '32': '숙박',
    '38': '카페',
    '39': '음식점'
  };
  return categoryMap[String(contentTypeId)] || '기타';
}

// 이미지 로드 에러 처리
function handleImageError(event) {
  event.target.src = '/placeholder.svg?height=300&width=400&text=No+Image';
}

function resetFilters() {
  // Reset selected filters
  selectedSido.value = '';
  selectedSigungu.value = '';
  selectedCategories.value = [];
  selectedRatings.value = [];
  
  // Reset applied filters
  appliedSido.value = '';
  appliedSigungu.value = '';
  appliedCategories.value = [];
  appliedRatings.value = [];
  
  // 실제 여행지 데이터 초기화
  realDestinations.value = [];
  
  // Update map markers
  nextTick(() => {
    updateMapMarkers();
  });
}

function toggleFilterPanel() {
  isFilterPanelOpen.value = !isFilterPanelOpen.value;
  
  // If closing the panel, update the map to show all of Korea
  if (!isFilterPanelOpen.value && window.koreaMap) {
    window.koreaMap.setCenter({ lat: 36.2, lng: 127.9 });
    window.koreaMap.setZoom(7);
  }
}

function handleResize(newWidth) {
  sidebarWidth.value = newWidth;
}

function selectDestination(destination) {
  selectedDestination.value = destination;
  
  // Center map on destination
  if (window.koreaMap && destination.coordinates) {
    window.koreaMap.setCenter(destination.coordinates);
    window.koreaMap.setZoom(15);
    
    // Highlight the marker
    if (window.koreaMapMarkers) {
      window.koreaMapMarkers.forEach(marker => {
        if (marker.getPosition().lat() === destination.coordinates.lat && 
            marker.getPosition().lng() === destination.coordinates.lng) {
          // You could add animation or change the marker icon here
          marker.setAnimation(window.google.maps.Animation.BOUNCE);
          setTimeout(() => {
            marker.setAnimation(null);
          }, 1500);
        }
      });
    }
  }
}

function addToTravelPlan(destination) {
  // This function would add the destination to a travel plan
  console.log('Adding to travel plan:', destination);
  
  // For now, just show an alert
  alert(`${destination.name}이(가) 여행 계획에 추가되었습니다.`);
}

function toggleFilterVisibility() {
  isFilterVisible.value = !isFilterVisible.value;
}

function updateMapMarkers() {
  // Clear existing markers
  if (window.koreaMapMarkers) {
    window.koreaMapMarkers.forEach(marker => marker.setMap(null));
    window.koreaMapMarkers = [];
  }
  
  // Create new markers for filtered destinations
  if (window.google && window.koreaMap) {
    window.koreaMapMarkers = filteredDestinations.value.map(dest => {
      const marker = new window.google.maps.Marker({
        position: dest.coordinates,
        map: window.koreaMap,
        title: dest.name
      });
      
      // Add click event to marker
      marker.addListener('click', () => {
        selectDestination(dest);
      });
      
      return marker;
    });
    
    // If there are markers, fit the map to show all of them
    if (window.koreaMapMarkers.length > 0) {
      const bounds = new window.google.maps.LatLngBounds();
      window.koreaMapMarkers.forEach(marker => {
        bounds.extend(marker.getPosition());
      });
      window.koreaMap.fitBounds(bounds);
      
      // Don't zoom in too far
      const listener = window.google.maps.event.addListenerOnce(window.koreaMap, 'idle', () => {
        if (window.koreaMap.getZoom() > 15) {
          window.koreaMap.setZoom(15);
        }
      });
    } else {
      // If no markers, show all of Korea
      window.koreaMap.setCenter({ lat: 36.2, lng: 127.9 });
      window.koreaMap.setZoom(7);
    }
  }
}

const initializeMap = ref(null);

onMounted(() => {
  // Function to initialize the map
  initializeMap.value = () => {
    if (mapContainer.value && window.google && window.google.maps) {
      window.koreaMap = new window.google.maps.Map(mapContainer.value, {
        center: { lat: 36.2, lng: 127.9 }, // Center of South Korea
        zoom: 7,
        mapTypeId: window.google.maps.MapTypeId.ROADMAP
      });
      
      // Initialize markers
      updateMapMarkers();
    } else {
      console.error('Google Maps API not loaded or map container not found');
    }
  };

  // Check if Google Maps is already loaded
  if (window.googleMapsLoaded && window.google && window.google.maps) {
    nextTick(() => {
      initializeMap.value();
    });
  } else {
    // Wait for Google Maps to load
    window.addEventListener('google-maps-loaded', () => {
      nextTick(() => {
        initializeMap.value();
      });
    });
    
    // Fallback in case the event doesn't fire
    setTimeout(() => {
      if (window.google && window.google.maps && mapContainer.value) {
        initializeMap.value();
      }
    }, 2000);
  }

  loadSidoOptions();
});

watch(() => selectedDestination.value, (newDestination) => {
  if (newDestination && detailMapContainer.value) {
    nextTick(() => {
      if (window.google && window.google.maps) {
        const detailMap = new window.google.maps.Map(detailMapContainer.value, {
          center: newDestination.coordinates,
          zoom: 15,
          mapTypeId: window.google.maps.MapTypeId.ROADMAP,
          disableDefaultUI: true,
          zoomControl: true
        });
        
        new window.google.maps.Marker({
          position: newDestination.coordinates,
          map: detailMap,
          title: newDestination.name
        });
      }
    });
  }
});

watch(filteredDestinations, () => {
  nextTick(() => {
    updateMapMarkers();
  });
}, { deep: true });

// Add this watch after the existing watches
watch(() => route.params.region, (newRegion) => {
  currentRegion.value = newRegion || '';
  // Reset filters when region changes
  resetFilters();
}, { immediate: true });

// 페이지 변경 핸들러
function handlePageChange(page) {
  currentPage.value = page;
  if (realDestinations.value.length > 0) {
    loadRealDestinations(page);
  }
}
</script>

<style scoped>
.destinations-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.filter-section {
  border-bottom: 1px solid var(--border-color);
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: var(--background-light);
}

.filter-header h2 {
  margin: 0;
  color: var(--primary-color);
  font-size: 1.2rem;
}

.toggle-filter-button {
  background: none;
  border: 1px solid var(--primary-color);
  color: var(--primary-color);
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.filter-content {
  padding: 15px 20px;
}

.filter-group {
  margin-bottom: 20px;
}

.filter-group h3 {
  margin: 0 0 10px 0;
  color: var(--text-color);
  font-size: 1rem;
  border-bottom: 1px solid var(--border-color);
  padding-bottom: 5px;
}

.filter-item {
  margin-bottom: 10px;
}

.filter-item label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  font-size: 0.9rem;
}

.filter-item select {
  width: 100%;
  padding: 8px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
}

.tag-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag-checkbox {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.tag-checkbox input {
  display: none;
}

.tag-label {
  background-color: var(--background-light);
  padding: 5px 10px;
  border-radius: 20px;
  font-size: 0.9rem;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.tag-checkbox input:checked + .tag-label {
  background-color: var(--primary-color);
  color: white;
}

.rating-filter {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.rating-checkbox {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.rating-checkbox input {
  margin-right: 10px;
}

.stars {
  color: #FFD700;
}

.rating-label {
  margin-left: 5px;
  font-size: 0.9rem;
}

.filter-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 20px;
}

.apply-filters-button,
.reset-filters-button {
  width: 100%;
  padding: 10px;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.apply-filters-button {
  background-color: var(--primary-color);
  color: white;
  border: none;
}

.apply-filters-button:hover:not(:disabled) {
  background-color: #004c8e;
}

.apply-filters-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.reset-filters-button {
  background-color: transparent;
  border: 1px solid var(--border-color);
  color: var(--text-color);
}

.reset-filters-button:hover {
  background-color: var(--background-light);
}

.destinations-list {
  flex: 1;
  overflow-y: auto;
  padding: 15px 20px;
}

.destinations-list h2 {
  margin: 0 0 15px 0;
  color: var(--primary-color);
  font-size: 1.2rem;
}

.destinations-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 15px;
}

.destination-card {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: var(--shadow);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 320px;
}

.destination-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.card-image {
  position: relative;
  height: 120px;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-category {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 5px 10px;
  border-radius: 20px;
  font-size: 0.8rem;
}

.card-content {
  padding: 15px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-content h3 {
  margin: 0 0 5px 0;
  color: var(--primary-color);
  font-size: 1rem;
}

.card-location {
  color: var(--text-light);
  font-size: 0.8rem;
  margin-bottom: 10px;
}

.card-rating {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  font-size: 0.8rem;
}

.rating-value {
  font-weight: bold;
  margin-right: 5px;
}

.reviews-count {
  color: var(--text-light);
  font-size: 0.7rem;
}

.card-description {
  color: var(--text-color);
  font-size: 0.8rem;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
}

.view-details {
  display: inline-block;
  background-color: var(--primary-color);
  color: white;
  padding: 8px 15px;
  border-radius: 4px;
  text-decoration: none;
  font-weight: bold;
  transition: background-color 0.2s ease;
  text-align: center;
  border: none;
  cursor: pointer;
  width: 100%;
}

.view-details:hover {
  background-color: #004c8e;
}

.no-results {
  text-align: center;
  padding: 50px 20px;
  color: var(--text-light);
}

.open-filter-button {
  position: fixed;
  top: calc(var(--topbar-height) + 20px);
  right: 20px;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 4px;
  padding: 10px 15px;
  font-weight: bold;
  cursor: pointer;
  z-index: 5;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  transition: background-color 0.2s ease;
}

.open-filter-button:hover {
  background-color: #004c8e;
}

.destination-detail-overlay {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80%;
  max-width: 800px;
  max-height: 80vh;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  z-index: 1000;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid var(--border-color);
}

.detail-header h2 {
  margin: 0;
  color: var(--primary-color);
}

.close-detail-button {
  background: none;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  color: var(--text-light);
}

.detail-content {
  display: flex;
  overflow-y: auto;
}

.detail-image {
  width: 40%;
  flex-shrink: 0;
}

.detail-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detail-info {
  flex: 1;
  padding: 20px;
}

.detail-location {
  color: var(--text-light);
  margin-bottom: 10px;
}

.detail-rating {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.detail-category {
  display: inline-block;
  background-color: var(--primary-color);
  color: white;
  padding: 5px 10px;
  border-radius: 20px;
  font-size: 0.9rem;
  margin-bottom: 15px;
}

.detail-description {
  margin-bottom: 20px;
  line-height: 1.6;
}

.view-details-button {
  display: inline-block;
  background-color: var(--primary-color);
  color: white;
  padding: 10px 20px;
  border-radius: 4px;
  text-decoration: none;
  font-weight: bold;
  transition: background-color 0.2s ease;
  border: none;
  cursor: pointer;
}

.view-details-button:hover {
  background-color: #004c8e;
}

@media (max-width: 768px) {
  .detail-content {
    flex-direction: column;
  }
  
  .detail-image {
    width: 100%;
    height: 200px;
  }
  
  .destinations-grid {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  }
  
  .destination-card {
    height: 300px;
  }
}

.map-container {
  width: 100%;
  height: 100%;
  background-color: #f5f5f5;
  position: relative;
  min-height: 400px;
}

@media (max-width: 768px) {
  .map-container {
    height: 300px;
  }
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 50px 20px;
  text-align: center;
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

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.pagination-info {
  font-size: 0.9rem;
  color: var(--text-light);
}

.virtual-scroll-container {
  overflow-y: auto;
  border: 1px solid var(--border-color);
  border-radius: 8px;
}

.virtual-scroll-content {
  position: relative;
}

.virtual-item {
  position: absolute !important;
  width: calc(100% - 20px);
  margin: 10px;
  box-sizing: border-box;
}

.pagination-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-top: 20px;
  padding: 20px 0;
}

.pagination-button {
  background-color: var(--primary-color);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.pagination-button:hover:not(:disabled) {
  background-color: #004c8e;
}

.pagination-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.pagination-numbers {
  display: flex;
  gap: 5px;
  align-items: center;
}

.pagination-number {
  background-color: transparent;
  border: 1px solid var(--border-color);
  color: var(--text-color);
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.pagination-number:hover {
  background-color: var(--background-light);
}

.pagination-number.active {
  background-color: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.pagination-ellipsis {
  color: var(--text-light);
  padding: 0 5px;
}

@media (max-width: 768px) {
  .virtual-scroll-container {
    height: 400px !important;
  }
  
  .pagination-controls {
    flex-wrap: wrap;
    gap: 5px;
  }
  
  .pagination-numbers {
    order: -1;
    width: 100%;
    justify-content: center;
    margin-bottom: 10px;
  }
}
</style>
