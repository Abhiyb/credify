<template>
  <div v-if="isOpen" class="fixed inset-0 bg-gray-500 bg-opacity-75 flex items-center justify-center p-4 z-50">
    <div class="bg-white rounded-xl max-w-md w-full p-6 shadow-2xl transform transition-all">
      <div class="flex justify-between items-center mb-6">
        <h3 class="text-xl font-bold text-gray-900">Manage Card Limit</h3>
        <button 
          @click="handleClose" 
          :disabled="isUpdating"
          class="text-gray-500 hover:text-gray-700 focus:outline-none disabled:opacity-50"
        >
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <!-- Card Info -->
      <div class="mb-6 p-4 bg-gray-50 rounded-lg">
        <div class="text-sm text-gray-700">
          <span class="font-medium">Card:</span> 
          {{ card?.cardType || 'Credit Card' }} 
          (**** {{ card?.cardNumber ? card.cardNumber.slice(-4) : 'XXXX' }})
        </div>
        <div class="mt-2 text-sm text-gray-700">
          <span class="font-medium">Current Limit:</span> 
          ₹{{ card?.creditLimit?.toLocaleString('en-IN') || '0' }}
        </div>
      </div>

      <!-- New Limit Input -->
      <div class="mb-6">
        <label for="new-limit" class="block text-sm font-medium text-gray-700 mb-2">
          New Credit Limit (₹)
        </label>
        <input 
          id="new-limit"
          type="number" 
          v-model.number="localNewLimit" 
          min="0"
          step="1000"
          placeholder="Enter new limit"
          :class="[
            'w-full px-4 py-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500',
            localNewLimit <= 0 ? 'border-red-500' : 'border-gray-300'
          ]"
          :disabled="isUpdating"
        />
        <p v-if="localNewLimit <= 0 && localNewLimit !== ''" class="mt-1 text-sm text-red-600">
          Limit must be greater than 0
        </p>
      </div>

      <!-- Buttons -->
      <div class="flex space-x-4">
        <button 
          @click="handleUpdateLimit" 
          :disabled="isUpdating || localNewLimit <= 0"
          class="flex-1 bg-blue-600 hover:bg-blue-700 text-white px-5 py-3 rounded-lg font-medium transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {{ isUpdating ? 'Updating...' : 'Update Limit' }}
        </button>
        
        <button 
          @click="handleClose" 
          :disabled="isUpdating"
          class="flex-1 bg-gray-200 hover:bg-gray-300 text-gray-800 px-5 py-3 rounded-lg font-medium transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        >
          Cancel
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, defineProps, defineEmits } from 'vue'

const props = defineProps({
  isOpen: {
    type: Boolean,
    required: true
  },
  card: {
    type: Object,
    default: null
  },
  isUpdating: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close', 'update-limit'])

const localNewLimit = ref(0)

// Sync local limit when card prop changes
watch(() => props.card, (newCard) => {
  if (newCard && newCard.creditLimit !== undefined) {
    localNewLimit.value = newCard.creditLimit
  }
}, { immediate: true })

const handleUpdateLimit = () => {
  if (localNewLimit.value <= 0) return // already handled by disabled button

  emit('update-limit', {
    cardId: props.card.cardId,
    newLimit: localNewLimit.value
  })
}

const handleClose = () => {
  emit('close')
}
</script>

<style scoped>
/* Optional: make modal look nicer */
input:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.2);
}
</style>