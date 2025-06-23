import { ref } from "vue"

// Create reactive state
const isLoginModalOpen = ref(false)
const isSignupModalOpen = ref(false)

// Create the composable
export function useModal() {
  // Methods
  function openLoginModal() {
    isLoginModalOpen.value = true
    isSignupModalOpen.value = false
  }

  function closeLoginModal() {
    isLoginModalOpen.value = false
  }

  function openSignupModal() {
    isSignupModalOpen.value = true
    isLoginModalOpen.value = false
  }

  function closeSignupModal() {
    isSignupModalOpen.value = false
  }

  // Return the state and methods
  return {
    // State
    isLoginModalOpen,
    isSignupModalOpen,

    // Methods
    openLoginModal,
    closeLoginModal,
    openSignupModal,
    closeSignupModal,
  }
}
