<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const email = ref('')
const password = ref('')
const error = ref('')
const router = useRouter()

async function handleLogin() {
  error.value = ''
  try {
    const response = await axios.post('http://localhost:8089/api/profile/login', {
      email: email.value,
      password: password.value,
    })

    if (response.data.userId) {
      localStorage.setItem('userId', response.data.userId)
      router.push('/dashboard')
    } else {
      error.value = 'Invalid login response from server.'
    }
  } catch (err) {
    if (err.response) {
      if (err.response.status === 404) {
        error.value = 'User not found'
      } else if (err.response.status === 401) {
        error.value = 'Incorrect password'
      } else {
        error.value = err.response.data.message || 'Login failed'
      }
    } else {
      error.value = 'Network error. Please try again later.'
    }
  }
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50">
    <div class="bg-white shadow-md rounded-xl p-8 w-full max-w-md">
      <div class="mb-6 text-center">
        <h1 class="text-3xl font-bold text-gray-800">Login</h1>
        <div class="h-1 w-16 bg-violet-600 mx-auto mt-2 rounded"></div>
      </div>
      <form @submit.prevent="handleLogin">
        <div class="mb-4">
          <label for="email" class="block text-gray-700 mb-1">Email</label>
          <input v-model="email" type="email" id="email" required
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-violet-600" />
        </div>

        <div class="mb-4">
          <label for="password" class="block text-gray-700 mb-1">Password</label>
          <input v-model="password" type="password" id="password" required
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-violet-600" />
        </div>

        <p v-if="error" class="text-red-600 mb-4 text-center font-semibold">{{ error }}</p>

        <button type="submit"
          class="w-full bg-violet-600 hover:bg-violet-700 text-white py-2 px-4 rounded-lg focus:outline-none focus:ring-4 focus:ring-violet-400 font-semibold">
          Log In
        </button>
      </form>

      <p class="text-sm text-gray-600 mt-4 text-center">
        Don't have an account?
        <router-link to="/register" class="text-violet-600 hover:underline">Register here</router-link>
      </p>
    </div>
  </div>
</template>
