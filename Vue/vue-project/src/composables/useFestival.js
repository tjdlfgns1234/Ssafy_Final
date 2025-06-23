import { ref } from "vue"

// Create reactive state
const festivals = ref([])
const todaysFestivals = ref([])
const isLoading = ref(false)
const error = ref(null)

// Create the composable
export function useFestival() {
  // Methods
  function fetchFestivals() {
    isLoading.value = true
    error.value = null

    // Mock API call - replace with actual API
    return new Promise((resolve) => {
      setTimeout(() => {
        festivals.value = [
          {
            id: 1,
            name: "Seoul Lantern Festival",
            location: "Cheonggyecheon Stream, Seoul",
            startDate: "2023-11-01",
            endDate: "2023-11-17",
            description: "Annual lantern festival featuring hundreds of illuminated lanterns.",
            image: "https://via.placeholder.com/300x200?text=Seoul+Lantern+Festival",
          },
          {
            id: 2,
            name: "Busan Fireworks Festival",
            location: "Gwangalli Beach, Busan",
            startDate: "2023-10-28",
            endDate: "2023-10-28",
            description: "Spectacular fireworks display over Gwangalli Beach.",
            image: "https://via.placeholder.com/300x200?text=Busan+Fireworks+Festival",
          },
          {
            id: 3,
            name: "Jinju Lantern Festival",
            location: "Namgang River, Jinju",
            startDate: "2023-10-01",
            endDate: "2023-10-14",
            description: "Traditional lantern festival commemorating the patriotic spirit of the Korean people.",
            image: "https://via.placeholder.com/300x200?text=Jinju+Lantern+Festival",
          },
        ]
        isLoading.value = false
        resolve(festivals.value)
      }, 1000)
    })
  }

  function fetchTodaysFestivals() {
    isLoading.value = true

    // Mock API call - replace with actual API
    return new Promise((resolve) => {
      setTimeout(() => {
        // Get today's date in YYYY-MM-DD format
        const today = new Date().toISOString().split("T")[0]

        // Filter festivals happening today
        todaysFestivals.value = [
          {
            id: 1,
            name: "Seoul Lantern Festival",
            location: "Cheonggyecheon Stream, Seoul",
            startDate: "2023-11-01",
            endDate: "2023-11-17",
            description: "Annual lantern festival featuring hundreds of illuminated lanterns.",
            image: "https://via.placeholder.com/300x200?text=Seoul+Lantern+Festival",
          },
        ]

        isLoading.value = false
        resolve(todaysFestivals.value)
      }, 1000)
    })
  }

  // Return the state and methods
  return {
    // State
    festivals,
    todaysFestivals,
    isLoading,
    error,

    // Methods
    fetchFestivals,
    fetchTodaysFestivals,
  }
}
