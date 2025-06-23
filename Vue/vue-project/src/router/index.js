import { createRouter, createWebHistory } from "vue-router"
import { useAuthStore } from "../stores/auth"

// Import pages
import Home from "../pages/Home.vue"

const routes = [
  {
    path: "/",
    name: "Home",
    component: Home,
  },
  {
    path: "/destinations",
    name: "RegionalDestinations",
    component: () => import("../pages/RegionalDestinations.vue"),
  },
  {
    path: "/destination/:id",
    name: "DestinationDetail",
    component: () => import("../pages/DestinationDetail.vue"),
  },
  {
    path: "/my-travel-plans",
    name: "MyTravelPlans",
    component: () => import("../pages/MyTravelPlans.vue"),
  },
  {
    path: "/travel-plan/:id",
    name: "TravelPlanDetail",
    component: () => import("../pages/MyTravelPlans.vue"),
  },
  {
    path: "/hotspots",
    name: "HotSpots",
    component: () => import("../pages/HotSpots.vue"),
  },
  {
    path: "/travel-info",
    name: "TravelInfo",
    component: () => import("../pages/TravelInfo.vue"),
  },
  {
    path: '/travel-info/:postId',      
    name: 'TravelInfoDetail',
     component: () => import("../pages/TravelInfoDetail.vue"),
  },
  {
    path: "/admin",
    name: "Admin",
    component: () => import("../pages/Admin.vue"),
    meta: { requiresAuth: true, requiresAdmin: true },
  },
  {
    path: "/profile",
    name: "Profile",
    component: () => import("../pages/Profile.vue"),
    // meta: { requiresAuth: true },
  },

]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    // Always scroll to top
    return { top: 0 }
  },
})

// Navigation guards
router.beforeEach((to, from, next) => {
  // For demo purposes, we'll use a simple check
  const authStore = useAuthStore()
  const isAuthenticated = authStore.isAuthenticated
  const isAdmin = isAuthenticated && authStore.user?.role === "admin"

  if (to.meta.requiresAuth && !isAuthenticated) {
    // Redirect to login
    next("/")
    // Open login modal
    const modalStore = window.modalStore
    if (modalStore) {
      modalStore.openLoginModal()
    }
  } else if (to.meta.requiresAdmin && !isAdmin) {
    // Redirect non-admin users
    next("/")
  } else {
    next()
  }
})

export default router
