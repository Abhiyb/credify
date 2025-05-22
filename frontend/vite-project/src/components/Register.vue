<template>
  <div class="min-h-screen bg-gray-50 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8 bg-white shadow-md rounded-xl p-8">
      <div class="text-center">
        <h2 class="text-3xl font-bold text-gray-900">Create an Account</h2>
        <p class="mt-2 text-sm text-gray-600">Fill in the details to register</p>
        <div class="h-1 w-16 bg-violet-600 mx-auto mt-2"></div>
      </div>

      <form class="mt-8 space-y-6" @submit.prevent="register">
        <div class="rounded-md shadow-sm -space-y-px">
          <div class="mb-4">
            <label for="fullName" class="block text-sm font-medium text-gray-700">Full Name</label>
            <input
              id="fullName"
              name="fullName"
              type="text"
              required
              v-model="form.fullName"
              class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-lg shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-violet-600 focus:border-transparent"
            />

          </div>
          <div class="mb-4">
            <label for="email" class="block text-sm font-medium text-gray-700">Email</label>
            <input
              id="email"
              name="email"
              type="email"
              required
              v-model="form.email"
              class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-lg shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-violet-600 focus:border-transparent"
            />
          </div>
          <div class="mb-4">
            <label for="phone" class="block text-sm font-medium text-gray-700">Phone</label>
            <input
              id="phone"
              name="phone"
              type="text"
              required
              v-model="form.phone"
              class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-lg shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-violet-600 focus:border-transparent"
            />
          </div>
          <div class="mb-4">
            <label for="address" class="block text-sm font-medium text-gray-700">Address</label>
            <input
              id="address"
              name="address"
              type="text"
              required
              v-model="form.address"
              class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-lg shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-violet-600 focus:border-transparent"
            />
          </div>
          <div class="mb-4">
            <label for="annualIncome" class="block text-sm font-medium text-gray-700">Annual Income</label>
            <input
              id="annualIncome"
              name="annualIncome"
              type="number"
              required
              v-model="form.annualIncome"
              class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-lg shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-violet-600 focus:border-transparent"
            />
          </div>
          <div class="mb-6">
            <label for="password" class="block text-sm font-medium text-gray-700">Password</label>
            <input
              id="password"
              name="password"
              type="password"
              required
              v-model="form.password"
              class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-lg shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-violet-600 focus:border-transparent"
            />
          </div>
        </div>

        <p v-if="error" class="text-red-600 mb-4 text-center font-semibold">{{ error }}</p>
        <p v-if="success" class="text-green-600 mb-4 text-center font-semibold">{{ success }}</p>

        <div>
          <button
            type="submit"
            class="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-lg text-white bg-violet-600 hover:bg-violet-700 focus:outline-none focus:ring-4 focus:ring-violet-500"
            :disabled="loading"
          >
            <span v-if="loading">Registering...</span>
            <span v-else>Register</span>
          </button>
        </div>
      </form>
      <p class="mt-4 text-center text-sm text-gray-600">
        Already have an account?
        <router-link to="/login" class="text-violet-600 hover:underline font-medium">Login</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const form = ref({
  fullName: '',
  email: '',
  phone: '',
  address: '',
  annualIncome: '',
  password: '',
})

const error = ref('')
const success = ref('')
const loading = ref(false)

async function register() {
  error.value = ''
  success.value = ''
  loading.value = true

  // Cast annualIncome to Number
  form.value.annualIncome = Number(form.value.annualIncome)

  console.log('Sending:', JSON.stringify(form.value))

  try {
    const res = await axios.post('http://localhost:8089/api/profile', form.value, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
    console.log(res.data)
    success.value = 'Registration successful!'
    router.push('/login') // or redirect if needed
  } catch (err) {
    if (err.response && err.response.data) {
      error.value = err.response.data
    } else {
      error.value = 'Something went wrong. Please try again.'
    }
  } finally {
    loading.value = false
  }
}

</script>

<style scoped>
/* No custom styles needed; Tailwind covers everything */
</style>
