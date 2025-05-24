<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navbar -->
    <Navbar title="My Cards" />

    <!-- Main Content with padding to avoid overlap -->
    <div class="pt-32 md:pt-28">
      <div class="max-w-7xl mx-auto px-6">
        <!-- Loading State -->
        <div v-if="loading" class="flex justify-center items-center py-20">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <span class="ml-3 text-gray-600">Loading cards...</span>
        </div>

        <!-- Error State -->
        <div v-if="error" class="px-4 py-10">
          <div class="bg-red-50 border border-red-200 rounded-md p-4">
            <div class="flex">
              <div class="ml-3">
                <h3 class="text-sm font-medium text-red-800">Error loading cards</h3>
                <p class="mt-2 text-sm text-red-700">{{ error }}</p>
                <button @click="fetchCards" class="mt-4 bg-red-100 px-3 py-2 rounded-md text-sm font-medium text-red-800 hover:bg-red-200">
                  Try Again
                </button>
              </div>
            </div>
          </div>
        </div>
        
        <!-- Cards Content -->
        <div v-if="!loading && !error">
          <div class="flex justify-between items-center mb-8">
            <h1 class="text-2xl font-semibold text-gray-900">My Cards</h1>
            <button @click="navigateToApplyCard" class="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded hover:bg-blue-700 transition-colors duration-200">
              + APPLY FOR NEW CARD
            </button>
          </div>
          
          <div v-if="cards.length === 0" class="text-center py-12">
            <h3 class="text-lg font-medium text-gray-900">No cards found</h3>
            <p class="mt-1 text-gray-500">You don't have any credit cards yet.</p>
          </div>
          
          <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <div v-for="card in cards" :key="card.cardId" class="bg-gradient-to-r from-blue-500 to-purple-600 rounded-lg shadow-lg overflow-hidden w-full max-w-sm">
              <div class="p-4">
                <div class="flex justify-between items-start">
                  <div>
                    <p class="text-white text-xs font-medium">{{ card.cardType || 'CREDIT CARD' }}</p>
                    <p class="text-white text-base font-bold mt-3">{{ formatCardNumber(card.cardNumber) }}</p>
                  </div>
                </div>
                
                <div class="mt-4 flex justify-between items-end">
                  <div>
                    <p class="text-white text-xs">EXPIRY</p>
                    <p class="text-white text-sm font-medium">{{ formatExpiryDate(card.expiryDate) }}</p>
                  </div>
                  <div>
                    <p class="text-white text-xs">NAME</p>
                    <p class="text-white text-sm font-medium">{{ getUserName(card) }}</p>
                  </div>
                </div>
                
                <div class="mt-3">
                  <div class="flex justify-between">
                    <p class="text-white text-xs">LIMIT</p>
                    <p class="text-white text-xs">AVAILABLE</p>
                  </div>
                  <div class="flex justify-between">
                    <p class="text-white text-sm font-medium">₹{{ card.creditLimit?.toFixed(2) || '0.00' }}</p>
                    <p class="text-white text-sm font-medium">₹{{ card.availableLimit?.toFixed(2) || '0.00' }}</p>
                  </div>
                </div>
              </div>
              
              <div class="bg-blue-700 px-4 py-3 flex justify-between items-center">
                <div class="flex items-center">
                  <div class="relative inline-block w-8 mr-2">
                    <input 
                      :id="`toggle-${card.cardId}`" 
                      type="checkbox" 
                      :checked="card.status === 'ACTIVE'" 
                      @change="toggleCardStatus(card)"
                      :disabled="updatingStatus === card.cardId"
                      class="toggle-checkbox absolute block w-5 h-5 rounded-full bg-white border-2 appearance-none cursor-pointer disabled:opacity-50" 
                    />
                    <label :for="`toggle-${card.cardId}`" class="toggle-label block overflow-hidden h-5 rounded-full bg-gray-300 cursor-pointer"></label>
                  </div>
                  <span class="text-white text-xs">
                    {{ updatingStatus === card.cardId ? 'UPDATING...' : card.status }}
                  </span>
                </div>
                
                <div class="space-x-1">
                  <button @click="showLimitModal(card)" class="px-2 py-1 bg-white text-blue-700 text-xs font-medium rounded hover:bg-gray-100">
                    MANAGE LIMIT
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Card Limit Modal -->
      <div v-if="limitModalOpen" class="fixed inset-0 bg-gray-500 bg-opacity-75 flex items-center justify-center p-4 z-50">
        <div class="bg-white rounded-lg max-w-md w-full p-6">
          <h3 class="text-lg font-medium text-gray-900 mb-4">Manage Card Limit</h3>
          
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700">Card:</label>
            <div class="mt-1 text-sm text-gray-900">
              {{ selectedCard?.cardType || 'CREDIT CARD' }} 
              (**** {{ selectedCard?.cardNumber ? selectedCard.cardNumber.slice(-4) : '' }})
            </div>
          </div>
          
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700">Current Limit</label>
            <div class="mt-1 text-sm text-gray-900">₹{{ selectedCard?.creditLimit?.toFixed(2) || '0.00' }}</div>
          </div>
          
          <div class="mb-6">
            <label for="new-limit" class="block text-sm font-medium text-gray-700">New Limit</label>
            <input 
              type="number" 
              id="new-limit" 
              v-model="newLimit" 
              min="0"
              step="0.01"
              class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500" 
            />
          </div>
          
          <div class="flex space-x-3">
            <button 
              @click="updateCardLimit" 
              :disabled="updatingLimit"
              class="flex-1 bg-blue-600 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-blue-700 disabled:opacity-50"
            >
              {{ updatingLimit ? 'UPDATING...' : 'UPDATE LIMIT' }}
            </button>
            <button 
              @click="closeLimitModal" 
              :disabled="updatingLimit"
              class="flex-1 bg-gray-300 text-gray-700 px-4 py-2 rounded-md text-sm font-medium hover:bg-gray-400 disabled:opacity-50"
            >
              Cancel
            </button>
          </div>
        </div>
      </div>

      <!-- Success Toast -->
      <div v-if="showSuccessToast" class="fixed bottom-4 right-4 bg-green-500 text-white px-6 py-3 rounded-lg shadow-lg z-50">
        <div class="flex items-center">
          <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
          </svg>
          {{ successMessage }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import Navbar from './Navbar.vue';

const router = useRouter();
const API_BASE_URL = 'http://localhost:8089';

const cards = ref([]);
const loading = ref(false);
const error = ref(null);
const limitModalOpen = ref(false);
const selectedCard = ref(null);
const newLimit = ref(0);
const updatingStatus = ref(null);
const updatingLimit = ref(false);
const showSuccessToast = ref(false);
const successMessage = ref('');
const userId = ref(null);

const getUserIdFromStorage = () => {
  try {
    const storedUserId = localStorage.getItem('userId');
    if (storedUserId) {
      userId.value = parseInt(storedUserId, 10);
      return true;
    }
    return false;
  } catch (err) {
    console.error('Error reading userId from localStorage:', err);
    return false;
  }
};

const fetchCards = async () => {
  if (!getUserIdFromStorage()) {
    error.value = 'User not found. Please login first.';
    loading.value = false;
    return;
  }

  loading.value = true;
  error.value = null;
  
  try {
    const response = await fetch(`${API_BASE_URL}/api/cards/${userId.value}`);
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    
    const data = await response.json();
    cards.value = data;
  } catch (err) {
    console.error('Error fetching cards:', err);
    error.value = 'Failed to fetch cards. Please check if the backend server is running.';
  } finally {
    loading.value = false;
  }
};

const toggleCardStatus = async (card) => {
  updatingStatus.value = card.cardId;
  
  try {
    const newStatus = card.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    const response = await fetch(`${API_BASE_URL}/api/cards/${card.cardId}/status?status=${newStatus}`, {
      method: 'PUT',
    });
    
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    
    const updatedCard = await response.json();
    const cardIndex = cards.value.findIndex(c => c.cardId === card.cardId);
    if (cardIndex !== -1) {
      cards.value[cardIndex] = updatedCard;
    }
    
    showToast(`Card has been ${newStatus.toLowerCase()} successfully!`);
  } catch (err) {
    console.error('Error updating card status:', err);
    showToast('Failed to update card status. Please try again.');
  } finally {
    updatingStatus.value = null;
  }
};

const showLimitModal = (card) => {
  selectedCard.value = card;
  newLimit.value = card.creditLimit || 0;
  limitModalOpen.value = true;
};

const closeLimitModal = () => {
  limitModalOpen.value = false;
  selectedCard.value = null;
  newLimit.value = 0;
};

const updateCardLimit = async () => {
  if (!selectedCard.value || !newLimit.value || newLimit.value <= 0) {
    showToast('Please enter a valid limit amount.');
    return;
  }
  
  updatingLimit.value = true;
  
  try {
    const response = await fetch(`${API_BASE_URL}/cards/${selectedCard.value.cardId}/limit?newLimit=${newLimit.value}`, {
      method: 'PUT',
    });
    
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    
    const cardIndex = cards.value.findIndex(c => c.cardId === selectedCard.value.cardId);
    if (cardIndex !== -1) {
      cards.value[cardIndex].creditLimit = parseFloat(newLimit.value);
      cards.value[cardIndex].availableLimit = parseFloat(newLimit.value);
    }
    
    closeLimitModal();
    showToast(`Card limit updated to ₹${parseFloat(newLimit.value).toFixed(2)} successfully!`);
    
  } catch (err) {
    console.error('Error updating card limit:', err);
    showToast('Failed to update card limit. Please try again.');
  } finally {
    updatingLimit.value = false;
  }
};

const getUserName = (card) => {
  if (card.user?.fullName) return card.user.fullName.toUpperCase();
  if (card.application?.user?.fullName) return card.application.user.fullName.toUpperCase();
  return 'CARDHOLDER';
};

const formatCardNumber = (cardNumber) => {
  if (!cardNumber) return '**** **** **** ****';
  if (cardNumber.includes('X')) return cardNumber.replace(/X/g, '*');
  if (cardNumber.includes('*')) return cardNumber;
  return `**** **** **** ${cardNumber.slice(-4)}`;
};

const formatExpiryDate = (expiryDate) => {
  if (!expiryDate) return 'MM/YY';
  if (expiryDate.includes('-')) {
    const [year, month] = expiryDate.split('-');
    return `${month.padStart(2, '0')}/${year.slice(-2)}`;
  }
  return expiryDate;
};

const showToast = (message) => {
  successMessage.value = message;
  showSuccessToast.value = true;
  setTimeout(() => {
    showSuccessToast.value = false;
  }, 3000);
};

const navigateToApplyCard = () => {
  router.push('/apply-card');
};

onMounted(() => {
  if (getUserIdFromStorage()) {
    fetchCards();
  } else {
    router.push('/login');
  }
});
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