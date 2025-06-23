import { createApp } from "vue"
import { createPinia } from "pinia"
import App from "./App.vue"
import router from "./router"
import "./assets/css/main.css"
import { setupInterceptors } from "./services/axiosConfig"

// Create the app instance
const app = createApp(App)

// Create and use Pinia
const pinia = createPinia()
app.use(pinia)

// Use router
app.use(router)

// Setup axios interceptors after Pinia is available
setupInterceptors()

// Mount the app
app.mount("#app")
