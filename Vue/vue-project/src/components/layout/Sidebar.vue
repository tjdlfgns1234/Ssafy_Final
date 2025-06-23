<template>
  <div
    class="sidebar"
    :class="{ 'sidebar-collapsed': !isOpen }"
    :style="{ width: sidebarWidth, zIndex: 'var(--z-index-sidebar)' }"
  >
    <div class="sidebar-header">
      <div class="logo-container">
        <img
          v-if="isOpen"
          src="https://via.placeholder.com/150x50?text=한국+여행"
          alt="한국 여행 Logo"
          class="logo"
        />
        <img
          v-else
          src="https://via.placeholder.com/40x40?text=KT"
          alt="한국 여행 Logo"
          class="logo-small"
        />
      </div>
      <button class="toggle-button" @click="$emit('toggle')" aria-label="Toggle sidebar">
        <span class="toggle-icon" :class="{ 'toggle-icon-collapsed': !isOpen }"> ◀ </span>
      </button>
    </div>

    <nav class="sidebar-nav">
      <router-link
        to="/"
        class="nav-item"
        active-class="active"
        @click="$emit('navigateTo', 'home')"
      >
        <span class="nav-icon">🏠</span>
        <span v-if="isOpen" class="nav-text">홈</span>
      </router-link>

      <router-link
        to="/destinations"
        class="nav-item"
        active-class="active"
        @click="$emit('navigateTo', 'destinations')"
      >
        <span class="nav-icon">🗺️</span>
        <span v-if="isOpen" class="nav-text">지역 여행지</span>
      </router-link>

      <router-link
        to="/my-travel-plans"
        class="nav-item"
        active-class="active"
        @click="$emit('navigateTo', 'my-travel-plans')"
      >
        <span class="nav-icon">✈️</span>
        <span v-if="isOpen" class="nav-text">나의 여행 계획</span>
      </router-link>

      <router-link
        to="/hotspots"
        class="nav-item"
        active-class="active"
        @click="$emit('navigateTo', 'hotspots')"
      >
        <span class="nav-icon">🔥</span>
        <span v-if="isOpen" class="nav-text">축제 & 이벤트</span>
      </router-link>

      <router-link
        to="/travel-info"
        class="nav-item"
        active-class="active"
        @click="$emit('navigateTo', 'travel-info')"
      >
        <span class="nav-icon">📝</span>
        <span v-if="isOpen" class="nav-text">여행 정보 공유</span>
      </router-link>

      <!-- 관리자 대시보드 로그인 필요  -->
      <router-link
        v-if="authStore.isAuthenticated && authStore.user?.role === 'admin'"
        to="/admin"
        class="nav-item"
        active-class="active"
        @click="$emit('navigateTo', 'admin')"
      >
        <span class="nav-icon">⚙️</span>
        <span v-if="isOpen" class="nav-text">관리자 대시보드</span>
      </router-link>
      <!-- 프로필 로그인 필요  -->
      <router-link
        to="/profile"
        class="nav-item"
        active-class="active"
        @click="$emit('navigateTo', 'profile')"
      >
        <span class="nav-icon">👤</span>
        <span v-if="isOpen" class="nav-text">프로필</span>
      </router-link>
    </nav>

    <div class="sidebar-footer">
      <div v-if="isOpen" class="auth-buttons">
        <button
          v-if="!authStore.isAuthenticated"
          class="login-button"
          @click="modalStore.openLoginModal"
        >
          로그인
        </button>
        <button
          v-if="!authStore.isAuthenticated"
          class="signup-button"
          @click="modalStore.openSignupModal"
        >
          회원가입
        </button>
        <div v-else class="user-info">
          <div class="user-avatar">
            {{ authStore.user?.name?.charAt(0).toUpperCase() || 'U' }}
          </div>
          <div class="user-name">{{ authStore.user?.name || 'User' }}</div>
        </div>
      </div>
      <div v-else class="auth-buttons-small">
        <button
          v-if="!authStore.isAuthenticated"
          class="login-button-small"
          @click="modalStore.openLoginModal"
          title="로그인"
        >
          👤
        </button>
      </div>
    </div>

    <div class="resize-handle" @mousedown="startResize" @wheel="handleMouseWheel"></div>

    <!-- Keep the small triangle button -->
    <button class="sidebar-collapse-button" @click="$emit('toggle')" aria-label="Collapse sidebar">
      <span class="collapse-icon" :class="{ 'collapse-icon-collapsed': !isOpen }"> ◀ </span>
    </button>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { useModalStore } from '../../stores/modal'
import SearchBar from '../common/SearchBar.vue'

const props = defineProps({
  isOpen: {
    type: Boolean,
    default: true,
  },
  width: {
    type: Number,
    default: 250,
  },
})

const emit = defineEmits(['toggle', 'resize', 'navigateTo'])

const authStore = useAuthStore()
const modalStore = useModalStore()

const sidebarWidth = computed(() => {
  return props.isOpen ? `${props.width}px` : '60px'
})

function startResize(event) {
  event.preventDefault()

  const startX = event.clientX
  const startWidth = props.width

  function onMouseMove(mouseMoveEvent) {
    const newWidth = startWidth + (mouseMoveEvent.clientX - startX)
    const minWidth = 200
    const maxWidth = 400

    if (newWidth >= minWidth && newWidth <= maxWidth) {
      emit('resize', newWidth)
    }
  }

  function onMouseUp() {
    document.removeEventListener('mousemove', onMouseMove)
    document.removeEventListener('mouseup', onMouseUp)
  }

  document.addEventListener('mousemove', onMouseMove)
  document.addEventListener('mouseup', onMouseUp)
}

function handleMouseWheel(event) {
  event.preventDefault()

  // Determine direction and amount to resize
  const delta = event.deltaY > 0 ? -10 : 10
  const newWidth = props.width + delta
  const minWidth = 200
  const maxWidth = 400

  if (newWidth >= minWidth && newWidth <= maxWidth) {
    emit('resize', newWidth)
  }
}
</script>

<style scoped>
.sidebar {
  height: 100vh;
  background-color: var(--white);
  border-right: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  position: fixed;
  top: 0;
  left: 0;
  box-shadow: var(--shadow);
}

.sidebar-collapsed {
  width: 60px !important;
}

.sidebar-header {
  height: var(--topbar-height);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 10px;
  border-bottom: 1px solid var(--border-color);
}

.logo-container {
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.logo {
  max-width: 100%;
  max-height: 40px;
}

.logo-small {
  width: 40px;
  height: 40px;
}

.search-container {
  padding: 10px;
}

.toggle-button {
  width: 24px;
  height: 24px;
  background: none;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.toggle-icon {
  transition: transform 0.3s ease;
}

.toggle-icon-collapsed {
  transform: rotate(180deg);
}

.sidebar-nav {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px 0;
  overflow-y: auto;
  justify-content: flex-start; /* Align items from the top */
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  color: var(--text-color);
  text-decoration: none;
  transition: background-color 0.2s ease;
  white-space: nowrap;
  overflow: hidden;
}

.nav-item:hover {
  background-color: var(--background-light);
}

.nav-item.active {
  background-color: rgba(0, 91, 172, 0.1);
  color: var(--primary-color);
  font-weight: bold;
  border-left: 3px solid var(--primary-color);
}

.nav-icon {
  font-size: 1.2rem;
  margin-right: 15px;
  width: 20px;
  text-align: center;
}

.sidebar-footer {
  padding: 15px;
  border-top: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: auto; /* Push to the bottom */
}

.auth-buttons {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
}

.login-button,
.signup-button {
  width: 100%;
  padding: 8px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.2s ease;
}

.login-button {
  background-color: transparent;
  border: 1px solid var(--primary-color);
  color: var(--primary-color);
}

.login-button:hover {
  background-color: rgba(0, 91, 172, 0.1);
}

.signup-button {
  background-color: var(--primary-color);
  color: white;
  border: 1px solid var(--primary-color);
}

.signup-button:hover {
  background-color: #004c8e;
}

.auth-buttons-small {
  display: flex;
  justify-content: center;
}

.login-button-small {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: var(--primary-color);
  color: white;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
}

.user-info {
  display: flex;
  align-items: center;
  width: 100%;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: var(--primary-color);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-right: 10px;
}

.user-name {
  font-size: 0.9rem;
  font-weight: bold;
}

.resize-handle {
  position: absolute;
  top: 0;
  right: 0;
  width: 5px;
  height: 100%;
  cursor: ew-resize;
  background-color: transparent;
}

.resize-handle:hover {
  background-color: rgba(0, 0, 0, 0.1);
}

/* Sidebar collapse button */
.sidebar-collapse-button {
  position: absolute;
  top: 50%;
  right: -16px;
  transform: translateY(-50%);
  width: 16px;
  height: 40px;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: var(--z-index-sidebar);
}

.collapse-icon {
  font-size: 10px;
  transition: transform 0.3s ease;
}

.collapse-icon-collapsed {
  transform: rotate(180deg);
}

@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
  }

  .sidebar.sidebar-open {
    transform: translateX(0);
  }
}
</style>
