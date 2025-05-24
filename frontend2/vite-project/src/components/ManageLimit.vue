<template>
    <div v-if="isOpen" class="fixed inset-0 bg-gray-500 bg-opacity-75 flex items-center justify-center p-4 z-50">
      <div class="bg-white rounded-lg max-w-md w-full p-6">
        <h3 class="text-lg font-medium text-gray-900 mb-4">Manage Card Limit</h3>
        
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700">Card:</label>
          <div class="mt-1 text-sm text-gray-900">
            {{ card?.cardType || 'CREDIT CARD' }} 
            (**** {{ card?.cardNumber ? card.cardNumber.slice(-4) : '' }})
          </div>
        </div>
        
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700">Current Limit</label>
          <div class="mt-1 text-sm text-gray-900">₹{{ card?.creditLimit?.toFixed(2) || '0.00' }}</div>
        </div>
        
        <div class="mb-6">
          <label for="new-limit" class="block text-sm font-medium text-gray-700">New Limit</label>
          <input 
            type="number" 
            id="new-limit" 
            v-model="localNewLimit" 
            min="0"
            step="0.01"
            class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500" 
          />
        </div>
        
        <div class="flex space-x-3">
          <button 
            @click="handleUpdateLimit" 
            :disabled="isUpdating"
            class="flex-1 bg-blue-600 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-blue-700 disabled:opacity-50"
          >
            {{ isUpdating ? 'UPDATING...' : 'UPDATE LIMIT' }}
          </button>
          <button 
            @click="handleClose" 
            :disabled="isUpdating"
            class="flex-1 bg-gray-300 text-gray-700 px-4 py-2 rounded-md text-sm font-medium hover:bg-gray-400 disabled:opacity-50"
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, watch, defineProps, defineEmits } from 'vue';
  
  const props = defineProps({
    isOpen: {
      type: Boolean,
      default: false
    },
    card: {
      type: Object,
      default: null
    },
    isUpdating: {
      type: Boolean,
      default: false
    }
  });
  
  const emit = defineEmits(['close', 'update-limit']);
  
  const localNewLimit = ref(0);
  
  // Watch for card changes to update the local limit
  watch(() => props.card, (newCard) => {
    if (newCard) {
      localNewLimit.value = newCard.creditLimit || 0;
    }
  }, { immediate: true });
  
  const handleUpdateLimit = () => {
    if (!props.card || !localNewLimit.value || localNewLimit.value <= 0) {
      // might want to emit an error event here instead
      return;
    }
    
    emit('update-limit', {
      cardId: props.card.cardId,
      newLimit: localNewLimit.value
    });
  };
  
  const handleClose = () => {
    emit('close');
  };
  </script>