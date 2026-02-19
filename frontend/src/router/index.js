// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import Login from '../components/Login.vue'
import Register from '../components/Register.vue'
import Dashboard from '../components/Dashboard.vue'
import CreditCard from '../components/CardList.vue'
import ApplyCard from '../components/ApplyCard.vue'
import Profile from '../components/Profile.vue'
import Application from '../components/Application.vue'
import LandingPage from '../pages/LandingPage.vue'
import TransactionWithBNPLFeatureModule from '../components/TransactionWithBNPLFeatureModule.vue'
import Privacy from '../pages/TermsAndConditions.vue'
const routes = [
  { path: '/', component: LandingPage },
  { path: '/login', component: Login },
  { path :'/terms', component:Privacy},

  { path: '/register', component: Register },
  { path: '/dashboard', component: Dashboard, meta: { requiresAuth: true } },
  { path: '/card', component: CreditCard, meta: { requiresAuth: true } },
  { path: '/apply-card', component: ApplyCard, meta: { requiresAuth: true } },
  { path: '/profile', component: Profile, meta: { requiresAuth: true } },
  { path: '/applications', component: Application, meta: { requiresAuth: true } },
  { path: '/transactions', component: TransactionWithBNPLFeatureModule, meta: { requiresAuth: true } },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// Navigation guard to protect routes requiring auth
router.beforeEach((to, from, next) => {
 const loggedIn = !! (localStorage.getItem('authToken') || sessionStorage.getItem('authToken'));
  // Optional: add console log during development to see what's happening
  // console.log(`Navigating to: ${to.path} | Has authToken? ${!!localStorage.getItem('authToken')}`)

  if (to.meta.requiresAuth && !loggedIn) {
    next('/login')
  } else {
    next()
  }
})

export default router