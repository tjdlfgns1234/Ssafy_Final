import { defineStore } from "pinia"

export const useModalStore = defineStore("modal", {
  state: () => ({
    isLoginModalOpen: false,
    isSignupModalOpen: false,
    isAddPlanModalOpen: false,
    isAddDestinationModalOpen: false,
    isEditDestinationModalOpen: false,
    isDeleteConfirmModalOpen: false,
    currentDestination: null,
    currentPlan: null,
  }),

  actions: {
    openLoginModal() {
      this.isLoginModalOpen = true
    },

    closeLoginModal() {
      this.isLoginModalOpen = false
    },

    openSignupModal() {
      this.isSignupModalOpen = true
    },

    closeSignupModal() {
      this.isSignupModalOpen = false
    },

    openAddPlanModal() {
      this.isAddPlanModalOpen = true
    },

    closeAddPlanModal() {
      this.isAddPlanModalOpen = false
    },

    openAddDestinationModal() {
      this.isAddDestinationModalOpen = true
    },

    closeAddDestinationModal() {
      this.isAddDestinationModalOpen = false
      this.currentDestination = null
    },

    openEditDestinationModal(destination) {
      this.currentDestination = destination
      this.isEditDestinationModalOpen = true
    },

    closeEditDestinationModal() {
      this.isEditDestinationModalOpen = false
      this.currentDestination = null
    },

    openDeleteConfirmModal(plan) {
      this.currentPlan = plan
      this.isDeleteConfirmModalOpen = true
    },

    closeDeleteConfirmModal() {
      this.isDeleteConfirmModalOpen = false
      this.currentPlan = null
    },
  },
})
