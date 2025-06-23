import { ref } from "vue"

// Create reactive state
const isOpen = ref(true)
const width = ref(250)
const minWidth = ref(200)
const maxWidth = ref(400)

// Create the composable
export function useSidebar() {
  // Methods
  function toggleSidebar() {
    isOpen.value = !isOpen.value
  }

  function setWidth(newWidth) {
    if (newWidth >= minWidth.value && newWidth <= maxWidth.value) {
      width.value = newWidth
    }
  }

  // Return the state and methods
  return {
    // State
    isOpen,
    width,
    minWidth,
    maxWidth,

    // Methods
    toggleSidebar,
    setWidth,
  }
}
