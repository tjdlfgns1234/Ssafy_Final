<template>
  <div class="topbar" :style="{ zIndex: 'var(--z-index-topbar)' }">
    <div class="topbar-left">
      <div class="logo"><img src="./logo.png" alt="한국 여행 Logo" /> 한국 여행</div>
    </div>

    <div class="topbar-right">
      <div class="user-actions">
        <button
          v-if="!authStore.isAuthenticated"
          class="login-button"
          @click="modalStore.openLoginModal"
        >
          로그인
        </button>
        <div v-else class="user-info" @click="toggleUserMenu">
          <div class="user-avatar">
            {{ authStore.user?.name?.charAt(0).toUpperCase() || 'U' }}
          </div>
          <span class="user-name">{{ authStore.user?.name || 'User' }}</span>
          <span class="dropdown-icon">▼</span>

          <!-- User dropdown menu -->
          <div v-if="isUserMenuOpen" class="user-menu" @click.stop>
            <router-link to="/profile" class="menu-item">프로필</router-link>
            <router-link v-if="authStore.user?.role === 'admin'" to="/admin" class="menu-item">
              관리자 대시보드
            </router-link>
            <button class="menu-item logout-button" @click="handleLogout">로그아웃</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { useModalStore } from '../../stores/modal'
import SearchBar from '../common/SearchBar.vue'

const authStore = useAuthStore()
const modalStore = useModalStore()

const isUserMenuOpen = ref(false)

function toggleUserMenu() {
  isUserMenuOpen.value = !isUserMenuOpen.value
}

function handleLogout() {
  authStore.logout()
  isUserMenuOpen.value = false
}

// Close menu when clicking outside
function handleClickOutside(event) {
  if (isUserMenuOpen.value && !event.target.closest('.user-info')) {
    isUserMenuOpen.value = false
  }
}

// Add and remove event listeners
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.topbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: var(--topbar-height);
  background-color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  border-bottom: 1px solid var(--border-color);
  box-shadow: var(--shadow);
}

.topbar-left,
.topbar-right {
  flex: 1;
}

.topbar-center {
  flex: 2;
  max-width: 500px;
}

.logo {
  display: inline-flex; /* inline 요소처럼 취급하면서 flex 기능 사용 */
  align-items: center; /* 세로축 가운데 정렬 */
}

.logo img {
  height: 40px;
  margin-right: 8px; /* 이미지와 텍스트 사이 여유 */
}

.user-actions {
  display: flex;
  justify-content: flex-end;
  position: relative;
}

.login-button {
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 4px;
  padding: 8px 15px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.login-button:hover {
  background-color: #004c8e;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  transition: background-color 0.2s ease;
  position: relative;
}

.user-info:hover {
  background-color: var(--background-light);
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
  font-weight: bold;
  margin-right: 5px;
}

.dropdown-icon {
  font-size: 0.8rem;
  color: var(--text-light);
}

.user-menu {
  position: absolute;
  top: 100%;
  right: 0;
  width: 200px;
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  margin-top: 5px;
  z-index: var(--z-index-dropdown);
  overflow: hidden;
  animation: fadeIn 0.2s ease;
}

.menu-item {
  display: block;
  padding: 12px 15px;
  color: var(--text-color);
  text-decoration: none;
  transition: background-color 0.2s ease;
  text-align: left;
  width: 100%;
  border: none;
  background: none;
  font-size: 1rem;
  cursor: pointer;
}

.menu-item:hover {
  background-color: var(--background-light);
}

.logout-button {
  color: var(--secondary-color);
  border-top: 1px solid var(--border-color);
}

@media (max-width: 768px) {
  .topbar-center {
    display: none;
  }

  .user-name {
    display: none;
  }
}
</style>
