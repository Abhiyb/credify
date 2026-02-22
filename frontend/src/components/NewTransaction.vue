
<template>
  <div v-if="store.currentStep === 0" class="p-6">
    <h2 class="text-xl font-semibold text-gray-800">Transaction Details</h2>
    <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mt-6">
      <div class="space-y-2">
        <label class="block text-sm font-medium text-gray-700">Card ID</label>
        <input 
          v-model="store.transaction.cardId" 
          type="text" 
          class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-primary focus:border-primary"
          placeholder="Enter your card ID"
          aria-label="Card ID"
          required
        />
      </div>
      <div class="space-y-2">
        <label class="block text-sm font-medium text-gray-700">Amount</label>
        <input 
          v-model="store.transaction.amount" 
          type="number" 
          class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-primary focus:border-primary"
          placeholder="Enter amount"
          aria-label="Amount"
          required
        />
      </div>
      <div class="space-y-2">
        <label class="block text-sm font-medium text-gray-700">Category</label>
        <select 
          v-model="store.transaction.category" 
          class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-primary focus:border-primary"
          aria-label="Category"
          required
        >
          <option value="" disabled>Select a category</option>
          <option v-for="category in store.categories" :key="category" :value="category">{{ category }}</option>
        </select>
      </div>
      <div class="space-y-2">
        <label class="block text-sm font-medium text-gray-700">Merchant</label>
        <select 
          v-model="store.transaction.merchant" 
          class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-primary focus:border-primary"
          aria-label="Merchant"
          required
        >
          <option value="" disabled>Select a merchant</option>
          <option v-for="merchant in store.merchants" :key="merchant" :value="merchant">{{ merchant }}</option>
        </select>
      </div>
    </div>
    <div class="pt-4 flex justify-between">
      <button 
        @click="store.viewTransactionHistory" 
        class="px-6 py-3 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors"
        aria-label="View Transaction History"
      >
        View Transaction History
      </button>
      <button 
        @click="store.checkEligibility" 
        class="px-6 py-3 bg-primary text-white rounded-md hover:bg-primary-dark transition-colors"
        :disabled="store.loading"
        aria-label="Continue to Payment Options"
      >
        <span v-if="store.loading">Checking...</span>
        <span v-else>Continue</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { useBnplStore } from '../stores/bnplStore';
const store = useBnplStore();
</script>