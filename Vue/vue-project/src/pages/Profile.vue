<template>
  <div class="profile-page">
    <div class="page-header">
      <BackButton />
      <h1>프로필</h1>
    </div>

    <!-- 로그인 전 -->
    <div v-if="!authStore.isAuthenticated" class="login-prompt">
      <div class="empty-icon">🔒</div>
      <h2>로그인이 필요합니다</h2>
      <p>프로필을 보려면 로그인해주세요.</p>
      <button class="login-button" @click="modalStore.openLoginModal">로그인하기</button>
    </div>

    <!-- 로그인 후 -->
    <div v-else class="profile-container">
      <div class="profile-header">
        <div class="profile-avatar">
          {{ authStore.user.id.charAt(0).toUpperCase() }}
        </div>
        <div class="profile-info">
          <h2>{{ authStore.user.name }}</h2>
          <p>{{ authStore.user.email }}</p>
          <p class="role-badge">{{ authStore.user.role }}</p>
        </div>
        <button v-if="!isEditing" class="edit-profile-button" @click="startEdit">Edit</button>
      </div>

      <!-- 조회 모드 -->
      <div v-if="!isEditing" class="profile-details">
        <div class="detail-item">
          <span class="detail-label">ID:</span>
          <span class="detail-value">{{ authStore.user.id }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">Name:</span>
          <span class="detail-value">{{ authStore.user.name }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">Email:</span>
          <span class="detail-value">{{ authStore.user.email }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">Role:</span>
          <span class="detail-value">{{ authStore.user.role }}</span>
        </div>
        <button class="logout-button" @click="handleLogout">로그아웃</button>
        <!-- 여기서부터 회원탈퇴 버튼 추가 -->
        <button class="delete-account-button" @click="handleDeleteAccount">회원탈퇴</button>
      </div>

      <!-- 편집 모드 -->
      <div v-else class="edit-profile-form">
        <div class="detail-item">
          <span class="detail-label">ID:</span>
          <span class="detail-value">{{ authStore.user.id }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">이름:</span>
          <input class="detail-value editable" type="text" v-model="profileForm.name" />
        </div>
        <div class="detail-item">
          <span class="detail-label">Email:</span>
          <span class="detail-value">{{ authStore.user.email }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">비밀번호:</span>
          <input
            class="detail-value editable"
            type="password"
            v-model="profileForm.password"
            placeholder="새 비밀번호"
          />
        </div>
        <div class="detail-item">
          <span class="detail-label">비밀번호 확인:</span>
          <input
            class="detail-value editable"
            type="password"
            v-model="profileForm.confirmPassword"
            placeholder="새 비밀번호 확인"
          />
        </div>
        <div class="form-actions">
          <button class="cancel-button" @click="cancelEdit">Cancel</button>
          <button class="save-button" @click="saveProfile">Save Changes</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useAuthStore } from '../stores/auth'
import { useModalStore } from '../stores/modal'
import BackButton from '../components/common/BackButton.vue'

const BASE_URL = 'http://192.168.205.81:8080' // 실제 서버 주소로 변경
// axios 기본 설정
axios.defaults.baseURL = BASE_URL

const authStore = useAuthStore()
const modalStore = useModalStore()

const isEditing = ref(false)
const profileForm = ref({ name: '', email: '', password: '', confirmPassword: '' })

async function handleDeleteAccount() {
  if (!confirm('정말로 회원탈퇴 하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) return

  const token = authStore.token || authStore.user.token
  const config = { headers: {} }
  if (token) config.headers.Authorization = `Bearer ${token}`

  try {
    // console.log("http://localhost:8080/api/v1/members/"+authStore.user.id);
    const response = await axios.delete(`/api/v1/members/${authStore.user.id}`, config)
    if (response.data.status === 'SUCCESS') {
      alert('회원탈퇴가 완료되었습니다.')
      authStore.logout()
    } else {
      alert('회원탈퇴 실패: ' + response.data.error)
    }
  } catch (error) {
    console.error('회원탈퇴 에러', error)
    if (error.response?.data?.error) {
      alert('회원탈퇴 실패: ' + error.response.data.error)
    } else {
      alert('회원탈퇴 중 오류가 발생했습니다.')
    }
  }
}

function startEdit() {
  isEditing.value = true
  profileForm.value = {
    name: authStore.user.name,
    email: authStore.user.email,
    password: '',
    confirmPassword: '',
  }
}

function cancelEdit() {
  isEditing.value = false
}

async function saveProfile() {
  if (!profileForm.value.name || !profileForm.value.email) return
  if (
    profileForm.value.password &&
    profileForm.value.password !== profileForm.value.confirmPassword
  ) {
    alert('비밀번호가 일치하지 않습니다.')
    return
  }

  const payload = {
    name: profileForm.value.name,
    email: profileForm.value.email,
  }
  if (profileForm.value.password) {
    payload.pw = profileForm.value.password
  }

  const token = authStore.token || authStore.user.token
  const config = { headers: {} }
  if (token) config.headers.Authorization = `Bearer ${token}`

  try {
    const response = await axios.put(`/api/v1/members/${authStore.user.id}`, payload, config)
    if (response.data.status === 'SUCCESS') {
      const updated = response.data.member
      authStore.user.name = updated.name
      authStore.user.email = updated.email
      localStorage.setItem('user', JSON.stringify(authStore.user))
      alert('프로필이 업데이트되었습니다.')
      isEditing.value = false
    } else {
      alert('업데이트 실패')
      console.error('프로필 업데이트 실패:', response.data.error)
    }
  } catch (error) {
    console.error('Update error', error)
    if (error.response?.data?.error) {
      alert('Update failed: ' + error.response.data.error)
    } else {
      alert('An error occurred during update.')
    }
  }
}

function handleLogout() {
  authStore.logout()
}
</script>

<style scoped>
.login-prompt {
  /* 전체 뷰포트 가운데 정렬 */
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  /* 카드 스타일 */
  background-color: #fff;
  border-radius: 8px;
  padding: 40px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  max-width: 100%;
  margin: 0 auto;
}

.empty-icon {
  font-size: 4.5rem;
  margin-bottom: 16px;
  color: var(--primary-color);
}

.login-prompt h2 {
  margin: 0 0 8px;
  font-size: 1.75rem;
  font-weight: bold;
  color: var(--primary-color);
}

.login-prompt p {
  margin: 0 0 24px;
  font-size: 1rem;
  color: var(--text-light);
  line-height: 1.5;
}

.login-button {
  display: inline-block;
  background-color: var(--primary-color);
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 12px 32px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.login-button:hover {
  background-color: #004c8e;
}
.profile-page {
  padding: 20px;
}
.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
.page-header h1 {
  margin-left: 15px;
  color: var(--primary-color);
}

.login-prompt {
  text-align: center;
  padding: 30px;
  background: white;
  border-radius: 8px;
}
.login-button {
  background: var(--primary-color);
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
}

.profile-container {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: var(--shadow);
}

.profile-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
.profile-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: var(--primary-color);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  margin-right: 15px;
}
.profile-info h2 {
  margin: 0;
}
.role-badge {
  margin-top: 5px;
  font-size: 0.9rem;
  color: var(--secondary-color);
}

.edit-profile-button {
  margin-left: auto;
  background: var(--primary-color);
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
}

.profile-details .detail-item,
.edit-profile-form .detail-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}
.detail-label {
  width: 140px;
  font-weight: bold;
}
.detail-value {
  flex: 1;
}

.edit-profile-form .detail-value.editable {
  border: 1px solid var(--border-color);
  border-radius: 4px;
  padding: 6px;
  font-size: 1rem;
}

.delete-account-button {
  margin-top: 20px;
  background: var(--secondary-color);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  margin-left: 10px;
}

.logout-button {
  margin-top: 20px;
  background: var(--secondary-color);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
}

.edit-profile-form .form-actions {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}
.cancel-button {
  background: transparent;
  border: 1px solid var(--border-color);
  padding: 6px 12px;
  border-radius: 4px;
}
.save-button {
  background: var(--primary-color);
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
}
</style>
