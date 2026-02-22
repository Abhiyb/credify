```vue
<template>
  <div v-if="store.currentStep === 5" class="p-6 space-y-6">
    <h2 class="text-xl font-semibold text-gray-800">Transaction History</h2>
    
    <div class="flex flex-col md:flex-row md:items-end md:space-x-4 space-y-4 md:space-y-0">
      <div class="flex-1">
        <label class="block text-sm font-medium text-gray-700">Card ID</label>
        <input 
          v-model="store.searchCardId" 
          type="text" 
          class="mt-1 w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-primary focus:border-primary"
          placeholder="Enter Card ID"
          aria-label="Card ID"
        />
      </div>
      <div class="flex space-x-4">
        <button 
          @click="store.fetchTransactions" 
          class="px-6 py-3 bg-primary text-white rounded-md hover:bg-primary-dark transition-colors"
          :disabled="store.loading"
          aria-label="Search Transactions"
        >
          <span v-if="store.loading">Searching...</span>
          <span v-else>Search</span>
        </button>
      </div>
    </div>

    <div class="flex flex-col md:flex-row md:items-center md:space-x-4 space-y-4 md:space-y-0">
      <div class="flex space-x-4">
        <button 
          @click="store.filterTransactions('all')" 
          :class="`px-4 py-2 rounded-md text-sm font-medium
                  ${store.transactionFilter === 'all' ? 'bg-primary text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'}`"
          aria-label="Show All Transactions"
        >
          All
        </button>
        <button 
          @click="store.filterTransactions('bnpl')" 
          :class="`px-4 py-2 rounded-md text-sm font-medium
                  ${store.transactionFilter === 'bnpl' ? 'bg-primary text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'}`"
          aria-label="Show BNPL Transactions"
        >
          BNPL
        </button>
        <button 
          @click="store.filterTransactions('full')" 
          :class="`px-4 py-2 rounded-md text-sm font-medium
                  ${store.transactionFilter === 'full' ? 'bg-primary text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'}`"
          aria-label="Show Full Payment Transactions"
        >
          Full Payment
        </button>
      </div>
      <div class="flex flex-col sm:flex-row sm:space-x-4 space-y-4 sm:space-y-0">
        <div>
          <label class="block text-sm font-medium text-gray-700">From Date</label>
          <input 
            v-model="store.dateFilter.from" 
            type="date" 
            class="mt-1 w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-primary focus:border-primary"
            aria-label="From Date"
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700">To Date</label>
          <input 
            v-model="store.dateFilter.to" 
            type="date" 
            class="mt-1 w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-primary focus:border-primary"
            aria-label="To Date"
          />
        </div>
      </div>
      <div class="flex space-x-4 self-end">
        <button 
          @click="store.applyDateFilter" 
          class="px-4 py-2 bg-primary text-white rounded-md hover:bg-primary-dark text-sm"
          aria-label="Apply Date Filter"
        >
          Apply
        </button>
        <button 
          @click="store.clearDateFilter" 
          class="px-4 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 text-sm"
          aria-label="Clear Date Filter"
        >
          Clear
        </button>
      </div>
    </div>

    <div v-if="store.errorMessage" class="text-red-600 text-sm">{{ store.errorMessage }}</div>

    <div v-if="store.transactions.length > 0" class="mt-4">
      <div class="bg-gray-50 rounded-lg p-4">
        <div class="grid grid-cols-6 gap-4 text-sm font-medium text-gray-600 border-b border-gray-200 pb-2">
          <div>Transaction ID</div>
          <div>Amount</div>
          <div>Merchant</div>
          <div>Category</div>
          <div>Date</div>
          <div>Actions</div>
        </div>
        <div 
          v-for="transaction in store.transactions" 
          :key="transaction.id"
          class="grid grid-cols-6 gap-4 text-sm py-2 items-center"
        >
          <div>{{ transaction.id }}</div>
          <div>₹{{ transaction.amount.toFixed(2) }}</div>
          <div>{{ transaction.merchantName }}</div>
          <div>{{ transaction.category }}</div>
          <div>{{ store.formatDate(transaction.transactionDate) }}</div>
          <div>
            <button 
              v-if="transaction.isBNPL"
              @click="store.viewTransactionInstallments(transaction.id)"
              class="text-primary hover:underline text-xs"
              :aria-label="`View Installments for Transaction ${transaction.id}`"
            >
              View Installments
            </button>
          </div>
        </div>
      </div>

      <div class="mt-4 flex justify-between items-center">
        <button 
          @click="store.prevPage" 
          :disabled="store.currentPage === 1 || store.loading"
          class="px-4 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 disabled:opacity-50"
          aria-label="Previous Page"
        >
          Previous
        </button>
        <div class="flex space-x-2">
          <button 
            v-for="page in store.paginationRange" 
            :key="page"
            @click="store.goToPage(page)"
            :class="`px-3 py-1 rounded-md text-sm
                    ${store.currentPage === page ? 'bg-primary text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'}`"
            :aria-label="`Go to Page ${page}`"
            :aria-current="store.currentPage === page ? 'page' : undefined"
          >
            {{ page }}
          </button>
        </div>
        <button 
          @click="store.nextPage" 
          :disabled="store.currentPage === store.totalPages || store.loading"
          class="px-4 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 disabled:opacity-50"
          aria-label="Next Page"
        >
          Next
        </button>
      </div>
    </div>
    <div v-else-if="store.searchCardId" class="text-gray-600">
      No transactions found for this Card ID.
    </div>

    <div class="pt-4 flex justify-between">
      <button 
        @click="store.currentStep = 0" 
        class="px-6 py-3 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors"
        aria-label="Back to Transaction Details"
      >
        Back
      </button>
      <button 
        @click="store.resetForm" 
        class="px-6 py-3 bg-primary text-white rounded-md hover:bg-primary-dark transition-colors"
        aria-label="Start New Transaction"
      >
        New Transaction
      </button>
    </div>
  </div>
</template>

<script setup>
import { useBnplStore } from '../stores/bnplStore';
const store = useBnplStore();
</script>
```