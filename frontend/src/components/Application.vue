<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navbar -->
    <Navbar title="My Applications" />

    <!-- Main Content with padding to avoid overlap -->
    <div class="pt-32 md:pt-28">
      <div class="max-w-7xl mx-auto px-6">
        <!-- Header with Refresh -->
        <div class="flex justify-between items-center mb-8">
          <h1 class="text-2xl md:text-3xl font-bold text-gray-900">Your Credit Card Applications</h1>
          <button 
            @click="fetchApplications"
            :disabled="loading"
            class="flex items-center gap-2 px-5 py-2.5 bg-indigo-600 text-white text-sm font-medium rounded-lg hover:bg-indigo-700 disabled:opacity-60 transition-colors duration-200 shadow-sm hover:shadow"
          >
            <svg v-if="loading" class="animate-spin h-5 w-5" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
            </svg>
            {{ loading ? 'Refreshing...' : 'Refresh' }}
          </button>
        </div>

        <!-- Loading State - Show whenever loading is true -->
        <div v-if="loading" class="flex justify-center items-center py-32">
          <div class="animate-spin rounded-full h-16 w-16 border-b-4 border-indigo-600"></div>
          <span class="ml-4 text-gray-700 text-lg font-medium">Loading your applications...</span>
        </div>

        <!-- Error State (only for real errors) -->
        <div v-else-if="errorMsg" class="bg-red-50 border border-red-200 rounded-xl p-6 text-center">
          <svg class="w-12 h-12 text-red-500 mx-auto mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
          <p class="text-red-700 font-medium text-lg">{{ errorMsg }}</p>
          <button 
            @click="fetchApplications"
            class="mt-6 px-6 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg transition-colors"
          >
            Try Again
          </button>
        </div>

        <!-- Applications List -->
        <div v-else-if="applications.length" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div
            v-for="app in applications"
            :key="app.id"
            class="bg-white rounded-xl shadow-sm border border-gray-200 p-6 hover:shadow-md transition-all duration-200"
          >
            <div class="flex justify-between items-start mb-4">
              <h3 class="text-xl font-bold text-gray-800">{{ app.cardType || 'Unknown Card' }}</h3>
              <span
                :class="[
                  'px-4 py-1.5 rounded-full text-sm font-semibold inline-flex items-center',
                  getStatusClass(app.status)
                ]"
              >
                {{ getStatusIcon(app.status) }}
                {{ app.status || 'Unknown' }}
              </span>
            </div>

            <div class="space-y-3 text-gray-700">
              <div class="flex justify-between">
                <span class="font-medium">Requested Limit:</span>
                <span class="font-semibold">{{ formatCurrency(app.requestedLimit) }}</span>
              </div>
              
              <div class="flex justify-between">
                <span class="font-medium">Application Date:</span>
                <span>{{ formatDate(app.applicationDate) }}</span>
              </div>

              <div v-if="app.processedDate" class="flex justify-between">
                <span class="font-medium">Processed Date:</span>
                <span>{{ formatDate(app.processedDate) }}</span>
              </div>

              <!-- Status-specific messages -->
              <div v-if="app.status?.toUpperCase() === 'APPROVED' && app.approvedLimit" class="mt-4 p-4 bg-green-50 rounded-lg border border-green-200">
                <p class="text-green-800 font-medium flex items-center gap-2">
                  <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
                  </svg>
                  Approved with limit: {{ formatCurrency(app.approvedLimit) }}
                </p>
              </div>

              <div v-if="app.status?.toUpperCase() === 'REJECTED' && app.rejectionReason" class="mt-4 p-4 bg-red-50 rounded-lg border border-red-200">
                <p class="text-red-800 font-medium">
                  <span class="font-bold">Reason:</span> {{ app.rejectionReason }}
                </p>
              </div>

              <div v-if="app.remarks" class="mt-4 p-4 bg-blue-50 rounded-lg border border-blue-200">
                <p class="text-blue-800">
                  <span class="font-bold">Remarks:</span> {{ app.remarks }}
                </p>
              </div>
            </div>
          </div>
        </div>

        <!-- Empty State - Friendly message when no applications -->
        <div v-else class="text-center py-24 bg-white rounded-xl shadow-sm border border-gray-200">
          <svg class="mx-auto h-20 w-20 text-indigo-400 mb-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
          <h3 class="text-2xl font-bold text-gray-900 mb-4">No Applications Yet</h3>
          <p class="text-lg text-gray-600 mb-8 max-w-lg mx-auto">
            You haven't applied for any credit cards yet. Apply now to get started with your credit journey!
          </p>
          <router-link 
            to="/apply-card" 
            class="inline-block px-10 py-4 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-full font-medium text-lg hover:shadow-xl hover:scale-105 transition-all duration-300"
          >
            Apply for a Card Now
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Navbar from './Navbar.vue' // adjust path if needed

const router = useRouter()
const API_BASE_URL = 'http://localhost:8080'

const applications = ref([])
const loading = ref(true)
const errorMsg = ref(null)

// Get token (supports remember me)
const getAuthToken = () => localStorage.getItem('authToken') || sessionStorage.getItem('authToken')

const fetchApplications = async () => {
  const token = getAuthToken()
  if (!token) {
    errorMsg.value = 'Please sign in to view your applications.'
    setTimeout(() => router.push('/login'), 1500)
    return
  }

  loading.value = true
  errorMsg.value = null

  try {
    const response = await fetch(`${API_BASE_URL}/api/cards/applications/me`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })

    if (!response.ok) {
      // Read the body ONLY ONCE as text
      const errorText = await response.text()

      let parsedError = 'Failed to load applications'
      try {
        const errData = JSON.parse(errorText)
        parsedError = errData.message || parsedError
      } catch {
        parsedError = errorText.trim() || parsedError
      }

      if (response.status === 401) {
        errorMsg.value = 'Session expired. Please sign in again.'
        setTimeout(() => {
          localStorage.removeItem('authToken')
          sessionStorage.removeItem('authToken')
          router.push('/login')
        }, 2000)
        return
      }

      throw new Error(parsedError)
    }

    applications.value = await response.json()
  } catch (err) {
    console.error('Fetch applications error:', err)
    errorMsg.value = err.message || 'Unable to load your applications. Please try again.'
  } finally {
    loading.value = false
  }
}

// Formatters
const formatDate = (dateStr) => {
  if (!dateStr) return 'N/A'
  try {
    return new Date(dateStr).toLocaleDateString('en-IN', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    })
  } catch {
    return dateStr
  }
}

const formatCurrency = (amount) => {
  if (!amount && amount !== 0) return 'N/A'
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  }).format(amount)
}

const getStatusClass = (status) => {
  if (!status) return 'bg-gray-100 text-gray-700'
  const s = status.toUpperCase()
  if (s === 'APPROVED') return 'bg-green-100 text-green-800'
  if (s === 'PENDING') return 'bg-yellow-100 text-yellow-800'
  if (s === 'REJECTED') return 'bg-red-100 text-red-800'
  if (s === 'UNDER_REVIEW') return 'bg-blue-100 text-blue-800'
  return 'bg-gray-100 text-gray-700'
}

const getStatusIcon = (status) => {
  if (!status) return '•'
  const s = status.toUpperCase()
  if (s === 'APPROVED') return '✓'
  if (s === 'PENDING') return '⏳'
  if (s === 'REJECTED') return '✗'
  if (s === 'UNDER_REVIEW') return '🔍'
  return '•'
}

onMounted(() => {
  fetchApplications()
})
</script>

<style scoped>
/* Your original spin animation */
.animate-spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>