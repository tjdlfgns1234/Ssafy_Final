import { defineStore } from "pinia"

export const useSidebarStore = defineStore("sidebar", {
  state: () => ({
    isOpen: true,
    width: 250,
    minWidth: 200,
    maxWidth: 400,
  }),

  actions: {
    toggleSidebar() {
      this.isOpen = !this.isOpen
    },

    setWidth(newWidth) {
      if (newWidth >= this.minWidth && newWidth <= this.maxWidth) {
        this.width = newWidth
      }
    },
  },
})
