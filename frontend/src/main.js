// src/main.js or src/main.ts
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import axios from 'axios'
import './style.css'
const app = createApp(App)

// ✅ Set Axios baseURL
axios.defaults.baseURL = 'http://localhost:8080'  // Or your actual backend URL

// ✅ Register Axios globally
app.config.globalProperties.$axios = axios


app.use(router)
app.mount('#app')



