<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navbar -->
    <Navbar title="My Cards" />

    <div class="pt-32 md:pt-28 pb-12">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <!-- Header -->
        <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-10 gap-4">
          <h1 class="text-3xl font-bold text-gray-900">My Cards</h1>
          <button 
            @click="navigateToApplyCard"
            class="px-6 py-3 bg-gradient-to-r from-indigo-600 to-purple-600 text-white font-medium rounded-lg shadow-md hover:shadow-lg hover:scale-105 transition-all duration-300"
          >
            + Apply for New Card
          </button>
        </div>

        <!-- Loading -->
        <div v-if="loading" class="flex justify-center items-center py-32">
          <div class="animate-spin rounded-full h-16 w-16 border-t-4 border-indigo-600"></div>
          <span class="ml-4 text-gray-700 text-lg font-medium">Loading your cards...</span>
        </div>

        <!-- Error -->
        <div v-if="error" class="bg-red-50 border border-red-200 rounded-xl p-8 text-center max-w-2xl mx-auto">
          <p class="text-red-700 text-lg">{{ error }}</p>
          <button @click="fetchCards" class="mt-6 px-8 py-3 bg-red-600 hover:bg-red-700 text-white rounded-lg transition-colors">
            Try Again
          </button>
        </div>

        <!-- No Cards -->
        <div v-if="!loading && !error && cards.length === 0" class="text-center py-24 bg-white rounded-2xl shadow-sm border border-gray-200 max-w-2xl mx-auto">
          <svg class="mx-auto h-20 w-20 text-gray-400 mb-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
          </svg>
          <h3 class="text-2xl font-medium text-gray-900 mb-3">No Cards Yet</h3>
          <p class="text-gray-600 mb-8">Apply for your first credit card to get started.</p>
          <button @click="navigateToApplyCard" class="px-8 py-4 bg-gradient-to-r from-indigo-600 to-purple-600 text-white rounded-xl font-medium hover:shadow-lg transition-all">
            Apply Now
          </button>
        </div>

        <!-- Cards -->
        <div v-if="!loading && !error && cards.length" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          <div 
            v-for="card in cards" 
            :key="card.cardId"
            class="relative rounded-3xl overflow-hidden shadow-2xl transform hover:scale-[1.03] transition-all duration-300 h-72 flex flex-col justify-between"
            :style="getCardBackground(card.cardType)"
          >
            <div class="p-6 pb-4">
              <!-- Logo & Chip -->
              <div class="flex justify-between items-center mb-4">
                <span class="text-3xl font-bold text-white tracking-wider drop-shadow-lg">
                  {{ getCardLogoText(card.cardType) }}
                </span>
                <div class="w-12 h-9 bg-yellow-400 rounded-md flex items-center justify-center text-black font-bold text-xs shadow-lg">
                  CHIP
                </div>
              </div>

              <!-- Full Card Number -->
              <div class="mb-6">
                <p class="text-3xl font-mono tracking-widest text-white drop-shadow-2xl break-all">
                  {{ card.cardNumber || '•••• •••• •••• ••••' }}
                </p>
                <button 
                  @click="copyToClipboard(card.cardNumber, 'Card number')"
                  class="mt-1 text-xs text-white/90 hover:text-white flex items-center gap-1"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z" />
                  </svg>
                  Copy Number
                </button>
              </div>

              <!-- Name, Expiry, CVV - Tight Grid -->
              <div class="grid grid-cols-3 gap-x-4 gap-y-2 text-white text-sm">
                <div>
                  <p class="opacity-80 text-xs">CARDHOLDER</p>
                  <p class="font-medium uppercase truncate drop-shadow-md">
                    {{ getUserName(card) }}
                  </p>
                </div>
                <div>
                  <p class="opacity-80 text-xs">VALID THRU</p>
                  <p class="font-medium drop-shadow-md">
                    {{ formatExpiryDate(card.expiryDate) }}
                  </p>
                </div>
                <div>
                  <p class="opacity-80 text-xs">CVV</p>
                  <div class="flex items-center gap-2">
                    <p class="font-bold text-lg drop-shadow-md">
                      {{ card.cvv || '•••' }}
                    </p>
                    <button 
                      @click="copyToClipboard(card.cvv, 'CVV')"
                      class="text-white/90 hover:text-white text-xs"
                    >
                      Copy
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- Bottom Bar - Compact -->
            <div class="bg-black/40 px-6 py-3 flex justify-between items-center backdrop-blur-sm border-t border-white/10">
              <div class="flex items-center gap-3">
                <label class="relative inline-flex items-center cursor-pointer">
                  <input 
                    type="checkbox" 
                    :checked="card.status === 'ACTIVE'" 
                    @change="toggleCardStatus(card)"
                    :disabled="updatingStatus === card.cardId"
                    class="sr-only peer"
                  />
                  <div class="w-11 h-5 bg-gray-600/70 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-purple-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:bg-green-600"></div>
                </label>
                <span class="text-white text-sm font-medium">
                  {{ updatingStatus === card.cardId ? 'Updating...' : card.status }}
                </span>
              </div>

              <button 
                @click="showLimitModal(card)"
                class="px-4 py-2 bg-white/20 hover:bg-white/30 text-white text-sm rounded-lg transition-colors backdrop-blur-sm"
              >
                Manage Limit
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Card Limit Modal -->
    <CardLimitModal 
      :is-open="limitModalOpen"
      :card="selectedCard"
      :is-updating="updatingLimit"
      @close="closeLimitModal"
      @update-limit="updateCardLimit"
    />

    <!-- Toast -->
    <div v-if="showSuccessToast" 
         :class="[
           'fixed bottom-6 right-6 px-6 py-4 rounded-2xl shadow-2xl z-50 max-w-sm transition-all duration-300',
           toastType === 'success' ? 'bg-green-600 text-white' : 'bg-red-600 text-white'
         ]">
      <div class="flex items-center gap-3">
        <svg v-if="toastType === 'success'" class="w-6 h-6 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
          <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
        </svg>
        <svg v-else class="w-6 h-6 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
          <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L10 8.586l1.293-1.293a1 1 0 011.414 1.414L11.414 10l1.293 1.293a1 1 0 01-1.414 1.414L10 11.414l-1.293 1.293a1 1 0 01-1.414-1.414L8.586 10 7.293 8.707a1 1 0 011.414-1.414L10 8.586z" clip-rule="evenodd"/>
        </svg>
        <span class="text-base font-medium">{{ successMessage }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Navbar from './Navbar.vue'
import CardLimitModal from '../components/ManageLimit.vue' // adjust path if needed

const router = useRouter()
const API_BASE_URL = 'http://localhost:8080'

const cards = ref([])
const loading = ref(true)
const error = ref(null)
const limitModalOpen = ref(false)
const selectedCard = ref(null)
const updatingStatus = ref(null)
const updatingLimit = ref(false)
const showSuccessToast = ref(false)
const successMessage = ref('')
const toastType = ref('success')

const getAuthToken = () => localStorage.getItem('authToken') || sessionStorage.getItem('authToken')

const copyToClipboard = async (text, label) => {
  try {
    await navigator.clipboard.writeText(text)
    showToast(`${label} copied!`, 'success')
  } catch (err) {
    showToast('Failed to copy', 'error')
  }
}

// Card background (inline styles)
const getCardBackground = (type) => {
  if (!type) return 'background: linear-gradient(135deg, #4f46e5, #7c3aed)'
  const t = type.toUpperCase()
  if (t.includes('VISA')) return 'background: linear-gradient(135deg, #1e40af, #3b82f6)'
  if (t.includes('MASTER')) return 'background: linear-gradient(135deg, #dc2626, #f97316)'
  if (t.includes('AMEX')) return 'background: linear-gradient(135deg, #059669, #10b981)'
  return 'background: linear-gradient(135deg, #4f46e5, #7c3aed)'
}

// Simple logo text
const getCardLogoText = (type) => {
  const t = (type || '').toUpperCase()
  if (t.includes('VISA')) return 'VISA'
  if (t.includes('MASTER')) return 'Mastercard'
  if (t.includes('AMEX')) return 'AMEX'
  return 'CARD'
}

// Fetch cards
const fetchCards = async () => {
  const token = getAuthToken()
  
  if (!token) {
    error.value = 'Please sign in to view your cards.'
    loading.value = false
    setTimeout(() => router.push('/login'), 1500)
    return
  }

  loading.value = true
  error.value = null

  try {
    const response = await fetch(`${API_BASE_URL}/api/cards/me`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    })

    if (!response.ok) {
      if (response.status === 401) {
        error.value = 'Session expired. Please sign in again.'
        setTimeout(() => {
          localStorage.removeItem('authToken')
          sessionStorage.removeItem('authToken')
          router.push('/login')
        }, 2000)
        return
      }
      const errText = await response.text()
      throw new Error(errText || `Failed to load cards (HTTP ${response.status})`)
    }

    cards.value = await response.json()
  } catch (err) {
    console.error('Fetch cards error:', err)
    error.value = err.message || 'Failed to load your cards. Please try again.'
  } finally {
    loading.value = false
  }
}

const formatExpiryDate = (expiryDate) => {
  if (!expiryDate) return 'MM/YY'
  if (expiryDate.includes('-')) {
    const [year, month] = expiryDate.split('-')
    return `${month.padStart(2, '0')}/${year.slice(-2)}`
  }
  return expiryDate
}

const getUserName = (card) => {
  return card.cardHolderName?.toUpperCase() || 'CARDHOLDER'
}

const toggleCardStatus = async (card) => {
  const newStatus = card.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
  updatingStatus.value = card.cardId

  try {
    const token = getAuthToken()
    const response = await fetch(`${API_BASE_URL}/api/cards/${card.cardId}/status?status=${newStatus}`, {
      method: 'PUT',
      headers: { 'Authorization': `Bearer ${token}` }
    })

    if (!response.ok) {
      if (response.status === 401) throw new Error('unauthorized')
      throw new Error('Failed to update status')
    }

    const updated = await response.json()
    const index = cards.value.findIndex(c => c.cardId === card.cardId)
    if (index !== -1) cards.value[index] = { ...cards.value[index], ...updated }

    showToast(`Card status changed to ${newStatus}`, 'success')
  } catch (err) {
    if (err.message.includes('unauthorized')) {
      showToast('Session expired. Please sign in.', 'error')
      setTimeout(() => {
        localStorage.removeItem('authToken')
        sessionStorage.removeItem('authToken')
        router.push('/login')
      }, 2000)
    } else {
      showToast('Failed to update card status', 'error')
    }
  } finally {
    updatingStatus.value = null
  }
}

const showLimitModal = (card) => {
  selectedCard.value = card
  limitModalOpen.value = true
}

const closeLimitModal = () => {
  limitModalOpen.value = false
  selectedCard.value = null
}

const updateCardLimit = async ({ cardId, newLimit }) => {
  if (!newLimit || newLimit <= 0) {
    showToast('Please enter a valid limit amount', 'error')
    return
  }

  updatingLimit.value = true

  try {
    const token = getAuthToken()
    const response = await fetch(`${API_BASE_URL}/api/cards/${cardId}/limit?newLimit=${newLimit}`, {
      method: 'PUT',
      headers: { 'Authorization': `Bearer ${token}` }
    })

    if (!response.ok) {
      if (response.status === 401) throw new Error('unauthorized')
      const errText = await response.text()
      throw new Error(errText || 'Failed to update limit')
    }

    const updated = await response.json()
    const index = cards.value.findIndex(c => c.cardId === cardId)
    if (index !== -1) cards.value[index] = { ...cards.value[index], ...updated }

    closeLimitModal()
    showToast(
      `Limit updated to ₹${newLimit.toLocaleString('en-IN')}\n` +
      `New available credit: ₹${updated.availableLimit?.toLocaleString('en-IN') || '0'}`,
      'success'
    )
  } catch (err) {
    if (err.message.includes('unauthorized')) {
      showToast('Session expired. Please sign in.', 'error')
      setTimeout(() => {
        localStorage.removeItem('authToken')
        sessionStorage.removeItem('authToken')
        router.push('/login')
      }, 2000)
    } else {
      showToast(err.message || 'Failed to update limit', 'error')
    }
  } finally {
    updatingLimit.value = false
  }
}

const showToast = (msg, type = 'success') => {
  successMessage.value = msg
  toastType.value = type
  showSuccessToast.value = true
  setTimeout(() => showSuccessToast.value = false, 4000)
}

const navigateToApplyCard = () => router.push('/apply-card')

onMounted(() => {
  if (!getAuthToken()) {
    router.push('/login')
  } else {
    fetchCards()
  }
})
</script>

<style scoped>
.toggle-checkbox:checked {
  right: 0;
  border-color: #2563eb;
}
.toggle-checkbox:checked + .toggle-label {
  background-color: #2563eb;
}

.animate-spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>