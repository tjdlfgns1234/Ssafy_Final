<template>
  <div class="modal-overlay" @click="closeModal">
    <div class="modal-container" @click.stop>
      <div class="modal-header">
        <h2>회원가입</h2>
        <button class="close-button" @click="closeModal">✕</button>
      </div>
      <div class="modal-content">
        <form @submit.prevent="handleSignup">
          <!-- 로그인 아이디 -->
          <div class="form-group">
            <label for="signupId">아이디</label>
            <input
              type="text"
              id="signupId"
              v-model="signupId"
              required
              placeholder="원하시는 아이디를 입력하세요"
              autocomplete="off"
            />
            <div
              v-if="signupId"
              class="check-message"
              :class="{
                available: idCheckStatus === 'available',
                duplicated: idCheckStatus === 'duplicated',
              }"
            >
              <template v-if="idCheckStatus === 'checking'">확인 중...</template>
              <template v-else-if="idCheckStatus === 'available'"
                >사용 가능한 아이디입니다.</template
              >
              <template v-else-if="idCheckStatus === 'duplicated'"
                >이미 사용 중인 아이디입니다.</template
              >
            </div>
          </div>
          <!-- 사용자 이름 -->
          <div class="form-group">
            <label for="name">이름</label>
            <input type="text" id="name" v-model="name" required placeholder="이름을 입력하세요" />
          </div>
          <!-- 이메일 -->
          <div class="form-group">
            <label for="email">이메일</label>
            <input
              type="email"
              id="email"
              v-model="email"
              required
              placeholder="이메일을 입력하세요"
              autocomplete="off"
            />
            <div
              v-if="email"
              class="check-message"
              :class="{
                available: emailCheckStatus === 'available',
                duplicated: emailCheckStatus === 'duplicated',
              }"
            >
              <template v-if="emailCheckStatus === 'checking'">확인 중...</template>
              <template v-else-if="emailCheckStatus === 'available'"
                >사용 가능한 이메일입니다.</template
              >
              <template v-else-if="emailCheckStatus === 'unavailable'"
                >유효하지않은 이메일입니다.</template
              >
            </div>
          </div>
          <!-- 비밀번호 -->
          <div class="form-group">
            <label for="password">비밀번호</label>
            <input
              type="password"
              id="password"
              v-model="password"
              required
              placeholder="비밀번호를 입력하세요"
            />
          </div>
          <!-- 비밀번호 확인 -->
          <div class="form-group">
            <label for="confirmPassword">비밀번호 확인</label>
            <input
              type="password"
              id="confirmPassword"
              v-model="confirmPassword"
              required
              placeholder="비밀번호를 다시 입력하세요"
            />
          </div>
          <div class="form-actions">
            <button type="submit" class="signup-button" :disabled="isLoading">
              {{ isLoading ? '가입 중...' : '회원가입' }}
            </button>
          </div>
        </form>
        <div class="form-footer">
          <p>
            이미 계정이 있으신가요?
            <button class="text-button" @click="openLogin">로그인</button>
          </p>
        </div>
        <div v-if="error" class="error-message">
          {{ error }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import axios from 'axios'
import { useAuthStore } from '../../stores/auth'
import { useModalStore } from '../../stores/modal'

axios.defaults.baseURL = 'http://192.168.205.81:8080/api/v1/'

// 입력값 상태
const signupId = ref('')
const name = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const error = ref('')
const isLoading = ref(false)

// 중복 체크 상태
const idCheckStatus = ref('') // '', 'checking', 'available', 'duplicated'
const emailCheckStatus = ref('') // '', 'checking', 'available', 'duplicated'

// stores
const authStore = useAuthStore()
const modalStore = useModalStore()

// 아이디 중복 체크
async function checkIdDuplicate() {
  if (!signupId.value) {
    idCheckStatus.value = ''
    return
  }
  idCheckStatus.value = 'checking'
  try {
    const res = await axios.get(`members/check-id/${encodeURIComponent(signupId.value)}`)
    idCheckStatus.value = res.data.exists ? 'duplicated' : 'available'
  } catch (e) {
    idCheckStatus.value = ''
  }
}

// 이메일 중복 체크
async function checkEmailDuplicate() {
  if (!email.value) {
    emailCheckStatus.value = ''
    return
  }
  emailCheckStatus.value = 'checking'
  try {
    // 이메일 형식 검사

    // console.log(email.value)
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!emailPattern.test(email.value)) {
      emailCheckStatus.value = 'unavailable'
      console.log(email.value)
      return
    }

    const res = await axios.get(`members/check-email/${encodeURIComponent(email.value)}`)
    emailCheckStatus.value = res.data.exists ? 'unavailable' : 'available'
  } catch (e) {
    emailCheckStatus.value = ''
  }
}

// 입력값 변경 시 중복 체크
watch(signupId, () => {
  checkIdDuplicate()
})
watch(email, () => {
  checkEmailDuplicate()
})

async function handleSignup() {
  error.value = ''
  if (password.value !== confirmPassword.value) {
    error.value = '비밀번호가 일치하지 않습니다.'
    return
  }
  if (idCheckStatus.value === 'duplicated') {
    error.value = '이미 사용 중인 아이디입니다.'
    return
  }
  if (emailCheckStatus.value === 'duplicated') {
    error.value = '이미 사용 중인 이메일입니다.'
    return
  }

  isLoading.value = true
  try {
    const res = await axios.post('members/join', {
      id: signupId.value,
      pw: password.value,
      name: name.value,
      email: email.value,
      role: 'user',
    })

    const member = res.data.member
    authStore.user = {
      id: member.id,
      name: member.name,
      email: member.email,
      role: member.role,
    }
    await authStore.login(signupId.value, password.value)

    authStore.isAuthenticated = true
    localStorage.setItem('user', JSON.stringify(authStore.user))
    modalStore.closeSignupModal()
  } catch (err) {
    error.value = err.response?.data?.message || err.message
  } finally {
    isLoading.value = false
  }
}

function closeModal() {
  modalStore.closeSignupModal()
}

function openLogin() {
  modalStore.closeSignupModal()
  modalStore.openLoginModal()
}
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
  max-width: 400px;
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
  padding: 15px 20px;
  border-bottom: 1px solid var(--border-color);
}

.modal-header h2 {
  margin: 0;
  color: var(--primary-color);
  font-size: 1.5rem;
}

.close-button {
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

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  color: var(--text-color);
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 1rem;
}

.check-message {
  margin-top: 5px;
  font-size: 0.95em;
}
.check-message.available {
  color: #2e7d32; /* 초록색 */
}
.check-message.duplicated {
  color: #d32f2f; /* 빨간색 */
}

.form-actions {
  margin-top: 30px;
}

.signup-button {
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

.signup-button:hover {
  background-color: #004c8e;
}

.signup-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.form-footer {
  margin-top: 20px;
  text-align: center;
  color: var(--text-light);
}

.text-button {
  background: none;
  border: none;
  color: var(--primary-color);
  cursor: pointer;
  font-weight: bold;
}

.error-message {
  margin-top: 20px;
  padding: 10px;
  background-color: #ffebee;
  color: #d32f2f;
  border-radius: 4px;
  text-align: center;
}
</style>
