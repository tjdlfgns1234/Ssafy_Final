"use client"

import { ref } from "vue"

// Create reactive state
const user = ref(null)
const isAuthenticated = ref(false)
const isLoading = ref(false)
const error = ref(null)

// Create the composable
export function useAuth() {
  // Methods
  function login(credentials) {
    isLoading.value = true
    error.value = null

    // Check for admin login
    if (credentials.username === "admin" && credentials.password === "admin123") {
      user.value = {
        id: 999,
        username: "admin",
        name: "Admin User",
        email: "admin@example.com",
        isAdmin: true,
        createdAt: new Date("2023-01-01"),
      }
      isAuthenticated.value = true
      isLoading.value = false

      // Save to localStorage if rememberMe is true
      if (credentials.rememberMe) {
        localStorage.setItem("user", JSON.stringify(user.value))
      }

      return Promise.resolve(user.value)
    }

    // Mock login - replace with actual API call
    return new Promise((resolve) => {
      setTimeout(() => {
        // Simulate successful login
        user.value = {
          id: 1,
          username: credentials.username,
          name: "Test User",
          email: "test@example.com",
          isAdmin: false,
          createdAt: new Date("2023-05-15"),
        }
        isAuthenticated.value = true
        isLoading.value = false

        // Save to localStorage if rememberMe is true
        if (credentials.rememberMe) {
          localStorage.setItem("user", JSON.stringify(user.value))
        }

        resolve(user.value)
      }, 1000)
    })
  }

  function logout() {
    user.value = null
    isAuthenticated.value = false
    localStorage.removeItem("user")
  }

  function signup(userData) {
    isLoading.value = true
    error.value = null

    // Mock signup - replace with actual API call
    return new Promise((resolve) => {
      setTimeout(() => {
        // Simulate successful signup
        user.value = {
          id: 1,
          username: userData.username,
          name: userData.name,
          email: userData.email,
          isAdmin: false,
          createdAt: new Date(),
        }
        isAuthenticated.value = true
        isLoading.value = false

        // Save to localStorage
        localStorage.setItem("user", JSON.stringify(user.value))

        resolve(user.value)
      }, 1000)
    })
  }

  function checkAuth() {
    // Check local storage or cookies for existing auth
    const savedUser = localStorage.getItem("user")
    if (savedUser) {
      try {
        user.value = JSON.parse(savedUser)
        isAuthenticated.value = true
      } catch (e) {
        localStorage.removeItem("user")
      }
    }
  }

  // Return the state and methods
  return {
    // State
    user,
    isAuthenticated,
    isLoading,
    error,

    // Methods
    login,
    logout,
    signup,
    checkAuth,
  }
}
