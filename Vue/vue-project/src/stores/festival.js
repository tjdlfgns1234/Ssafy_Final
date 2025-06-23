import { defineStore } from "pinia"

export const useFestivalStore = defineStore("festival", {
  state: () => ({
    festivals: [],
    todaysFestivals: [],
    isLoading: false,
    error: null,
  }),

  actions: {
    fetchFestivals() {
      this.isLoading = true
      this.error = null

      // Mock API call - replace with actual API
      return new Promise((resolve) => {
        setTimeout(() => {
          this.festivals = [
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
          this.isLoading = false
          resolve(this.festivals)
        }, 1000)
      })
    },

    fetchTodaysFestivals() {
      this.isLoading = true

      // Mock API call - replace with actual API
      return new Promise((resolve) => {
        setTimeout(() => {
          // Get today's date in YYYY-MM-DD format
          const today = new Date().toISOString().split("T")[0]

          // Filter festivals happening today
          this.todaysFestivals = [
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

          this.isLoading = false
          resolve(this.todaysFestivals)
        }, 1000)
      })
    },
  },
})
