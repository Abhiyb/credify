<template>
  <div class="min-h-screen bg-gray-50 p-4 md:p-8">
    <div class="max-w-4xl mx-auto bg-white rounded-xl shadow-md overflow-hidden">
      <!-- Header -->
      <div class="bg-primary text-white p-6 flex justify-between items-center">
  <div>
    <h1 class="text-2xl font-bold">Buy Now Pay Later</h1>
    <p class="text-sm opacity-80">Flexible payment options for your purchases</p>
  </div>
  <button 
    @click="$router.push('/dashboard')"
    class="px-4 py-2 bg-white text-primary rounded-md hover:bg-gray-100 transition-colors"
    aria-label="Back to Dashboard"
  >
    Back to Dashboard
  </button>
</div>

      <!-- Step indicator -->
      <div class="px-6 pt-6">
        <div class="flex items-center justify-between mb-8" role="navigation" aria-label="BNPL process steps">
          <div v-for="(step, index) in steps" :key="index" class="flex flex-col items-center flex-1">
            <div :class="`w-8 h-8 rounded-full flex items-center justify-center text-sm font-medium
                          ${currentStep >= index ? 'bg-primary text-white' : 'bg-gray-200 text-gray-500'}`"
                 :aria-current="currentStep === index ? 'step' : undefined"
                 :aria-label="`Step ${index + 1}: ${step}`">
              {{ index + 1 }}
            </div>
            <div class="text-xs mt-2 text-center" :class="currentStep >= index ? 'text-primary' : 'text-gray-500'">
              {{ step }}
            </div>
            <div v-if="index < steps.length - 1" class="hidden md:block w-full h-1 bg-gray-200 mt-4">
              <div :class="`h-full bg-primary transition-all duration-300`" 
                   :style="`width: ${currentStep > index ? '100%' : '0%'}`"></div>
            </div>
            <div v-if="index < steps.length - 1" class="block md:hidden w-8 h-1 bg-gray-200 mt-2">
              <div :class="`h-full bg-primary transition-all duration-300`" 
                   :style="`width: ${currentStep > index ? '100%' : '0%'}`"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- Main content area -->
      <div class="p-6">
        <!-- Step 1: Transaction Form -->
        <div v-if="currentStep === 0" class="space-y-6">
          <h2 class="text-xl font-semibold text-gray-800">Transaction Details</h2>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div class="space-y-2">
              <label class="block text-sm font-medium text-gray-700">Card ID</label>
              <input 
                v-model="transaction.cardId" 
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
                v-model="transaction.amount" 
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
                v-model="transaction.category" 
                class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-primary focus:border-primary"
                aria-label="Category"
                required
              >
                <option value="" disabled>Select a category</option>
                <option v-for="category in categories" :key="category" :value="category">{{ category }}</option>
              </select>
            </div>
            <div class="space-y-2">
              <label class="block text-sm font-medium text-gray-700">Merchant</label>
              <select 
                v-model="transaction.merchant" 
                class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-primary focus:border-primary"
                aria-label="Merchant"
                required
              >
                <option value="" disabled>Select a merchant</option>
                <option v-for="merchant in merchants" :key="merchant" :value="merchant">{{ merchant }}</option>
              </select>
            </div>
          </div>
          <div class="pt-4 flex justify-between">
            <button 
              @click="viewTransactionHistory" 
              class="px-6 py-3 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors"
              aria-label="View Transaction History"
            >
              View Transaction History
            </button>
            <button 
              @click="checkEligibility" 
              class="px-6 py-3 bg-primary text-white rounded-md hover:bg-primary-dark transition-colors"
              :disabled="loading"
              aria-label="Continue to Payment Options"
            >
              <span v-if="loading">Checking...</span>
              <span v-else>Continue</span>
            </button>
          </div>
        </div>

        <!-- Step 2: Eligibility Result & Payment Options -->
        <div v-if="currentStep === 1" class="space-y-6">
          <div v-if="eligibilityResult.eligible" class="space-y-6">
            <div class="bg-green-50 border border-green-200 rounded-md p-4 flex items-start">
              <div class="flex-shrink-0 text-green-500">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                  <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                </svg>
              </div>
              <div class="ml-3">
                <h3 class="text-sm font-medium text-green-800">Eligible for BNPL</h3>
                <div class="mt-1 text-sm text-green-700">
                  You're eligible to split this payment into installments.
                </div>
              </div>
            </div>

            <h2 class="text-xl font-semibold text-gray-800">Choose Payment Method</h2>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <!-- Pay in full option -->
              <div 
                @click="selectPaymentMethod('full')" 
                :class="`border rounded-lg p-4 cursor-pointer transition-all
                        ${selectedPaymentMethod === 'full' ? 'border-primary bg-primary-50' : 'border-gray-200 hover:border-primary'}`"
                role="button"
                aria-label="Select Pay in Full"
              >
                <div class="flex items-center">
                  <div :class="`w-5 h-5 rounded-full border flex items-center justify-center
                              ${selectedPaymentMethod === 'full' ? 'border-primary' : 'border-gray-300'}`">
                    <div v-if="selectedPaymentMethod === 'full'" class="w-3 h-3 rounded-full bg-primary"></div>
                  </div>
                  <div class="ml-3">
                    <h3 class="font-medium">Pay in full</h3>
                    <p class="text-sm text-gray-500">Pay the entire amount now</p>
                  </div>
                </div>
                <div class="mt-3 text-lg font-semibold">₹{{ transaction.amount }}</div>
              </div>
              
              <!-- BNPL option -->
              <div 
                @click="selectPaymentMethod('bnpl')" 
                :class="`border rounded-lg p-4 cursor-pointer transition-all
                        ${selectedPaymentMethod === 'bnpl' ? 'border-primary bg-primary-50' : 'border-gray-200 hover:border-primary'}`"
                role="button"
                aria-label="Select Pay Later"
              >
                <div class="flex items-center">
                  <div :class="`w-5 h-5 rounded-full border flex items-center justify-center
                              ${selectedPaymentMethod === 'bnpl' ? 'border-primary' : 'border-gray-300'}`">
                    <div v-if="selectedPaymentMethod === 'bnpl'" class="w-3 h-3 rounded-full bg-primary"></div>
                  </div>
                  <div class="ml-3">
                    <h3 class="font-medium">Pay Later</h3>
                    <p class="text-sm text-gray-500">Split into installments</p>
                  </div>
                </div>
                <div class="mt-3 text-sm text-primary">View installment plans</div>
              </div>
            </div>

            <div v-if="selectedPaymentMethod === 'bnpl'" class="mt-6 space-y-4">
              <h3 class="font-medium">Select Installment Plan</h3>
              <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div 
                  v-for="plan in installmentPlans" 
                  :key="plan.months"
                  @click="selectInstallmentPlan(plan)"
                  :class="`border rounded-lg p-4 cursor-pointer transition-all
                          ${selectedPlan && selectedPlan.months === plan.months ? 'border-primary bg-primary-50' : 'border-gray-200 hover:border-primary'}`"
                  role="button"
                  :aria-label="`Select ${plan.months} month installment plan`"
                >
                  <div class="text-lg font-semibold">{{ plan.months }} months</div>
                  <div class="text-sm text-gray-500 mt-1">
                    {{ plan.months }} payments of ₹{{ (transaction.amount / plan.months).toFixed(2) }}
                  </div>
                  <div v-if="plan.interestRate > 0" class="mt-2 text-xs text-gray-500">
                    {{ plan.interestRate }}% interest
                  </div>
                  <div v-else class="mt-2 text-xs text-green-600 font-medium">
                    Interest-free
                  </div>
                </div>
              </div>
            </div>

            <div class="pt-4 flex justify-between">
              <button 
                @click="currentStep = 0" 
                class="px-6 py-3 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors"
                aria-label="Back to Transaction Details"
              >
                Back
              </button>
              <button 
                @click="proceedToConfirmation" 
                class="px-6 py-3 bg-primary text-white rounded-md hover:bg-primary-dark transition-colors"
                :disabled="!canProceed || loading"
                aria-label="Continue to Confirmation"
              >
                <span v-if="loading">Processing...</span>
                <span v-else>Continue</span>
              </button>
            </div>
          </div>

          <div v-else class="space-y-6">
            <div class="bg-yellow-50 border border-yellow-200 rounded-md p-4 flex items-start">
              <div class="flex-shrink-0 text-yellow-500">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                  <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
                </svg>
              </div>
              <div class="ml-3">
                <h3 class="text-sm font-medium text-yellow-800">Not Eligible for BNPL</h3>
                <div class="mt-1 text-sm text-yellow-700">
                  {{ eligibilityResult.message || "You're not eligible for installment payments at this time." }}
                </div>
              </div>
            </div>

            <div class="pt-4 flex justify-between">
              <button 
                @click="currentStep = 0" 
                class="px-6 py-3 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors"
                aria-label="Back to Transaction Details"
              >
                Back
              </button>
              <button 
                @click="proceedToPayInFull" 
                class="px-6 py-3 bg-primary text-white rounded-md hover:bg-primary-dark transition-colors"
                aria-label="Pay in Full"
              >
                Pay in Full
              </button>
            </div>
          </div>
        </div>

        <!-- Step 3: Confirmation -->
        <div v-if="currentStep === 2" class="space-y-6">
          <h2 class="text-xl font-semibold text-gray-800">Confirm Your Payment</h2>
          
          <div class="bg-gray-50 rounded-lg p-6 space-y-4">
            <div class="flex justify-between">
              <span class="text-gray-600">Transaction Amount:</span>
              <span class="font-medium">₹{{ transaction.amount }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-600">Merchant:</span>
              <span class="font-medium">{{ transaction.merchant }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-600">Category:</span>
              <span class="font-medium">{{ transaction.category }}</span>
            </div>
            
            <div v-if="selectedPaymentMethod === 'bnpl' && selectedPlan" class="pt-2">
              <div class="border-t border-gray-200 pt-4">
                <h3 class="font-medium mb-3">Installment Plan: {{ selectedPlan.months }} months</h3>
                <div class="space-y-2">
                  <div v-for="(installment, index) in calculatedInstallments" :key="index" class="flex justify-between text-sm">
                    <span>Payment {{ index + 1 }} ({{ installment.dueDate }})</span>
                    <span>₹{{ installment.amount }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="pt-4 flex justify-between">
            <button 
              @click="currentStep = 1" 
              class="px-6 py-3 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors"
              aria-label="Back to Payment Options"
            >
              Back
            </button>
            <button 
              @click="confirmTransaction" 
              class="px-6 py-3 bg-primary text-white rounded-md hover:bg-primary-dark transition-colors"
              :disabled="loading"
              aria-label="Confirm Payment"
            >
              <span v-if="loading">Processing...</span>
              <span v-else>Confirm Payment</span>
            </button>
          </div>
        </div>

        <!-- Step 4: Success -->
        <div v-if="currentStep === 3" class="space-y-6 text-center py-8">
          <div class="mx-auto w-16 h-16 bg-green-100 rounded-full flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
            </svg>
          </div>
          <h2 class="text-2xl font-semibold text-gray-800">Payment Successful!</h2>
          <p class="text-gray-600">
            <span v-if="selectedPaymentMethod === 'bnpl'">
              Your payment has been split into {{ selectedPlan.months }} installments.
            </span>
            <span v-else>
              Your payment has been processed successfully.
            </span>
          </p>
          
          <div class="pt-6 flex flex-col md:flex-row justify-center space-y-3 md:space-y-0 md:space-x-3">
            <button 
              @click="viewInstallments" 
              v-if="selectedPaymentMethod === 'bnpl'"
              class="px-6 py-3 bg-primary text-white rounded-md hover:bg-primary-dark transition-colors"
              aria-label="View My Installments"
              :disabled="loading"
            >
              <span v-if="loading">Loading...</span>
              <span v-else>View My Installments</span>
            </button>
            <button 
              @click="viewTransactionHistory" 
              class="px-6 py-3 bg-primary text-white rounded-md hover:bg-primary-dark transition-colors"
              aria-label="View Transaction History"
            >
              View Transaction History
            </button>
            <button 
              @click="resetForm" 
              class="px-6 py-3 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors"
              aria-label="Make Another Payment"
            >
              Make Another Payment
            </button>
          </div>
        </div>

        <!-- Step 5: Installment Management View -->
        <div v-if="currentStep === 4" class="space-y-6">
          <div class="flex justify-between items-center">
            <h2 class="text-xl font-semibold text-gray-800">My Installments</h2>
            <div class="space-x-2">
              <button 
                @click="filterInstallments('all')" 
                :class="`px-3 py-1 text-sm rounded-md ${installmentFilter === 'all' ? 'bg-primary text-white' : 'bg-gray-100 text-gray-700'}`"
                aria-label="Show All Installments"
              >
                All
              </button>
              <button 
                @click="filterInstallments('pending')" 
                :class="`px-3 py-1 text-sm rounded-md ${installmentFilter === 'pending' ? 'bg-primary text-white' : 'bg-gray-100 text-gray-700'}`"
                aria-label="Show Pending Installments"
              >
                Pending
              </button>
              <button 
                @click="filterInstallments('paid')" 
                :class="`px-3 py-1 text-sm rounded-md ${installmentFilter === 'paid' ? 'bg-primary text-white' : 'bg-gray-100 text-gray-700'}`"
                aria-label="Show Paid Installments"
              >
                Paid
              </button>
              <button 
                @click="filterInstallments('overdue')" 
                :class="`px-3 py-1 text-sm rounded-md ${installmentFilter === 'overdue' ? 'bg-primary text-white' : 'bg-gray-100 text-gray-700'}`"
                aria-label="Show Overdue Installments"
              >
                Overdue
              </button>
            </div>
          </div>

          <div v-if="errorMessage" class="bg-red-50 border border-red-200 rounded-md p-4 text-sm text-red-700">
            {{ errorMessage }}
          </div>

          <div class="space-y-4">
            <div class="flex justify-between items-center">
              <div class="space-y-1">
                <h3 class="font-medium">Search Installments</h3>
                <div class="flex space-x-2">
                  <input 
                    v-model="searchTransactionId" 
                    type="text" 
                    placeholder="Transaction ID" 
                    class="px-3 py-2 border border-gray-300 rounded-md focus:ring-primary focus:border-primary"
                    aria-label="Search by Transaction ID"
                  />
                  <button 
                    @click="fetchInstallments" 
                    class="px-4 py-2 bg-primary text-white rounded-md hover:bg-primary-dark transition-colors"
                    :disabled="loading"
                    aria-label="Search Installments"
                  >
                    <span v-if="loading">Loading...</span>
                    <span v-else>Search</span>
                  </button>
                </div>
              </div>
              <button 
                @click="viewTransactionHistory" 
                class="px-4 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors"
                aria-label="View Transactions"
              >
                View Transactions
              </button>
            </div>

            <div v-if="displayedInstallments.length === 0" class="text-center py-8 bg-gray-50 rounded-lg">
              <p class="text-gray-500">No installments found for this Transaction ID</p>
            </div>

            <div v-else class="space-y-6">
              <div v-for="(group, transId) in groupedDisplayedInstallments" :key="transId" class="border rounded-lg p-4">
                <h3 class="font-medium mb-3">Transaction ID: {{ transId }}</h3>
                <div class="overflow-x-auto">
                  <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                      <tr>
                        <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Installment ID
                        </th>
                        <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Installment Number
                        </th>
                        <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Amount
                        </th>
                        <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Due Date
                        </th>
                        <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Status
                        </th>
                        <th scope="col" class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Actions
                        </th>
                      </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-gray-200">
                      <tr v-for="installment in group" :key="installment.id">
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                          {{ installment.id }}
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                          {{ installment.installmentNumber }}
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                          ₹{{ installment.amount.toFixed(2) }}
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                          {{ formatDate(installment.dueDate) }}
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                          <span 
                            :class="`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium
                                    ${installment.isPaid ? 'bg-green-100 text-green-800' : isOverdue(installment.dueDate) ? 'bg-red-100 text-red-800' : 'bg-yellow-100 text-yellow-800'}`"
                          >
                            {{ installment.isPaid ? 'Paid' : isOverdue(installment.dueDate) ? 'Overdue' : 'Pending' }}
                          </span>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                          <button 
                            v-if="!installment.isPaid"
                            @click="payInstallment(installment)"
                            class="px-3 py-1 bg-primary text-white text-sm rounded-md hover:bg-primary-dark transition-colors"
                            :aria-label="`Pay Installment ${installment.id}`"
                            :disabled="loading"
                          >
                            Pay Now
                          </button>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                <div class="mt-2 text-sm font-medium">
                  Total: ₹{{ calculateTotalAmount(group) }}
                </div>
              </div>
            </div>
          </div>

          <div class="pt-4">
            <button 
              @click="resetForm" 
              class="px-6 py-3 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors"
              aria-label="Make New Payment"
            >
              Make New Payment
            </button>
          </div>
        </div>

        <!-- Step 6: Transaction History View -->
        <div v-if="currentStep === 5" class="space-y-6">
          <div class="flex justify-between items-center">
            <h2 class="text-xl font-semibold text-gray-800">Transaction History</h2>
            <div class="space-x-2">
              <button 
                @click="filterTransactions('all')" 
                :class="`px-3 py-1 text-sm rounded-md ${transactionFilter === 'all' ? 'bg-primary text-white' : 'bg-gray-100 text-gray-700'}`"
                aria-label="Show All Transactions"
              >
                All
              </button>
              <button 
                @click="filterTransactions('full')" 
                :class="`px-3 py-1 text-sm rounded-md ${transactionFilter === 'full' ? 'bg-primary text-white' : 'bg-gray-100 text-gray-700'}`"
                aria-label="Show Paid in Full Transactions"
              >
                Paid in Full
              </button>
              <button 
                @click="filterTransactions('bnpl')" 
                :class="`px-3 py-1 text-sm rounded-md ${transactionFilter === 'bnpl' ? 'bg-primary text-white' : 'bg-gray-100 text-gray-700'}`"
                aria-label="Show BNPL Transactions"
              >
                BNPL
              </button>
            </div>
          </div>

          <div class="space-y-4">
            <div class="flex justify-between items-center">
              <div class="space-y-1">
                <h3 class="font-medium">Search Transactions</h3>
                <div class="flex space-x-2">
                  <input 
                    v-model="searchCardId" 
                    type="text" 
                    placeholder="Card ID" 
                    class="px-3 py-2 border border-gray-300 rounded-md focus:ring-primary focus:border-primary"
                    aria-label="Search by Card ID"
                  />
                  <button 
                    @click="fetchTransactions" 
                    class="px-4 py-2 bg-primary text-white rounded-md hover:bg-primary-dark transition-colors"
                    :disabled="loading"
                    aria-label="Search Transactions"
                  >
                    <span v-if="loading">Loading...</span>
                    <span v-else>Search</span>
                  </button>
                </div>
              </div>
              <div class="flex space-x-2">
                <button 
                  @click="viewInstallments" 
                  class="px-4 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors"
                  aria-label="View Installments"
                >
                  View Installments
                </button>
                <button 
                  @click="resetForm" 
                  class="px-4 py-2 bg-primary text-white rounded-md hover:bg-primary-dark transition-colors"
                  aria-label="New Payment"
                >
                  New Payment
                </button>
              </div>
            </div>

            <!-- Date range filter -->
            <div class="flex flex-wrap gap-4 items-end">
              <div class="space-y-1">
                <label class="block text-sm font-medium text-gray-700">From Date</label>
                <input 
                  v-model="dateFilter.from" 
                  type="date" 
                  class="px-3 py-2 border border-gray-300 rounded-md focus:ring-primary focus:border-primary"
                  aria-label="Filter by From Date"
                />
              </div>
              <div class="space-y-1">
                <label class="block text-sm font-medium text-gray-700">To Date</label>
                <input 
                  v-model="dateFilter.to" 
                  type="date" 
                  class="px-3 py-2 border border-gray-300 rounded-md focus:ring-primary focus:border-primary"
                  aria-label="Filter by To Date"
                />
              </div>
              <button 
                @click="applyDateFilter" 
                class="px-4 py-2 bg-gray-100 text-gray-700 rounded-md hover:bg-gray-200 transition-colors"
                aria-label="Apply Date Filter"
              >
                Apply Filter
              </button>
              <button 
                @click="clearDateFilter" 
                class="px-4 py-2 text-gray-500 rounded-md hover:text-gray-700 transition-colors"
                aria-label="Clear Date Filter"
              >
                Clear
              </button>
            </div>

            <div v-if="transactions.length === 0" class="text-center py-8 bg-gray-50 rounded-lg">
              <p class="text-gray-500">No transactions found</p>
            </div>

            <div v-else>
              <!-- Transactions table -->
              <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                  <thead class="bg-gray-50">
                    <tr>
                      <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Transaction ID
                      </th>
                      <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Date
                      </th>
                      <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Merchant
                      </th>
                      <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Category
                      </th>
                      <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Amount
                      </th>
                      <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Payment Method
                      </th>
                      <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Status
                      </th>
                      <th scope="col" class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Actions
                      </th>
                    </tr>
                  </thead>
                  <tbody class="bg-white divide-y divide-gray-200">
                    <tr v-for="transaction in transactions" :key="transaction.id">
                      <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                        #{{ transaction.id }}
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {{ formatDate(transaction.transactionDate) }}
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {{ transaction.merchantName }}
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {{ transaction.category }}
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                        ₹{{ transaction.amount.toFixed(2) }}
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        <span 
                          :class="`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium
                                  ${transaction.isBNPL ? 'bg-blue-100 text-blue-800' : 'bg-green-100 text-green-800'}`"
                        >
                          {{ transaction.isBNPL ? 'BNPL' : 'Paid in Full' }}
                        </span>
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        <span 
                          :class="`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium
                                  ${getStatusClass(transaction.status)}`"
                        >
                          {{ transaction.status }}
                        </span>
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                        <button 
                          v-if="transaction.isBNPL"
                          @click="viewTransactionInstallments(transaction.id)"
                          class="text-primary hover:text-primary-dark"
                          :aria-label="`View Installments for Transaction ${transaction.id}`"
                        >
                          View Installments
                        </button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>

              <!-- Pagination -->
              <div class="flex items-center justify-between border-t border-gray-200 px-4 py-3 sm:px-6 mt-4">
                <div class="flex-1 flex justify-between sm:hidden">
                  <button 
                    @click="prevPage" 
                    :disabled="currentPage === 1"
                    :class="`relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md
                            ${currentPage === 1 ? 'bg-gray-100 text-gray-400 cursor-not-allowed' : 'bg-white text-gray-700 hover:bg-gray-50'}`"
                    aria-label="Previous Page"
                  >
                    Previous
                  </button>
                  <button 
                    @click="nextPage" 
                    :disabled="currentPage === totalPages"
                    :class="`relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md
                            ${currentPage === totalPages ? 'bg-gray-100 text-gray-400 cursor-not-allowed' : 'bg-white text-gray-700 hover:bg-gray-50'}`"
                    aria-label="Next Page"
                  >
                    Next
                  </button>
                </div>
                <div class="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
                  <div>
                    <p class="text-sm text-gray-700">
                      Showing <span class="font-medium">{{ (currentPage - 1) * pageSize + 1 }}</span> to 
                      <span class="font-medium">{{ Math.min(currentPage * pageSize, totalTransactions) }}</span> of 
                      <span class="font-medium">{{ totalTransactions }}</span> results
                    </p>
                  </div>
                  <div>
                    <nav class="relative z-0 inline-flex rounded-md shadow-sm -space-x-px" aria-label="Pagination">
                      <button 
                        @click="prevPage" 
                        :disabled="currentPage === 1"
                        :class="`relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium
                                ${currentPage === 1 ? 'text-gray-300 cursor-not-allowed' : 'text-gray-500 hover:bg-gray-50'}`"
                        aria-label="Previous Page"
                      >
                        <span class="sr-only">Previous</span>
                        <svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                          <path fill-rule="evenodd" d="M12.707 5.293a1 1 0 010 1.414L9.414 10l3.293 3.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 0z" clip-rule="evenodd" />
                        </svg>
                      </button>
                      <button 
                        v-for="page in paginationRange" 
                        :key="page"
                        @click="goToPage(page)"
                        :class="`relative inline-flex items-center px-4 py-2 border text-sm font-medium
                                ${currentPage === page ? 'z-10 bg-primary border-primary text-white' : 'bg-white border-gray-300 text-gray-500 hover:bg-gray-50'}`"
                        :aria-label="`Go to Page ${page}`"
                      >
                        {{ page }}
                      </button>
                      <button 
                        @click="nextPage" 
                        :disabled="currentPage === totalPages"
                        :class="`relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium
                                ${currentPage === totalPages ? 'text-gray-300 cursor-not-allowed' : 'text-gray-500 hover:bg-gray-50'}`"
                        aria-label="Next Page"
                      >
                        <span class="sr-only">Next</span>
                        <svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                          <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
                        </svg>
                      </button>
                    </nav>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';

// Steps in the BNPL flow
const steps = ['Transaction Details', 'Payment Options', 'Confirmation', 'Complete', 'My Installments', 'Transaction History'];
const currentStep = ref(0);
const loading = ref(false);
const installmentsFetched = ref(false);
const errorMessage = ref('');

// Transaction form data
const transaction = ref({
  cardId: '',
  amount: '',
  category: '',
  merchant: '',
  isBNPL: false,
  installmentPlan: ''
});

// Predefined categories and merchants for dropdowns
const categories = ref([
  'Electronics',
  'Clothing',
  'Travel',
  'Home',
  'Food',
  'Entertainment',
  'Other'
]);

const merchants = ref([
  'Amazon',
  'Walmart',
  'Best Buy',
  'Target',
  'eBay',
  'Other'
]);

// Plan mapping
const planMapping = {
  3: 'THREE',
  6: 'SIX',
  9: 'NINE'
};

// Eligibility check result
const eligibilityResult = ref({
  eligible: false,
  message: ''
});

// Payment method selection
const selectedPaymentMethod = ref(null);
const selectedPlan = ref(null);

// Available installment plans
const installmentPlans = ref([
  { months: 3, interestRate: 0 },
  { months: 6, interestRate: 2.5 },
  { months: 9, interestRate: 4 }
]);

// Installment management
const installments = ref([]);
const searchCardId = ref('');
const searchTransactionId = ref('');
const installmentFilter = ref('all');
const displayedInstallments = ref([]); 

// Transaction history
const transactions = ref([]);
const transactionFilter = ref('all');
const dateFilter = ref({
  from: '',
  to: ''
});
const currentPage = ref(1);
const pageSize = 10;
const totalTransactions = ref(0);
const totalPages = computed(() => Math.ceil(totalTransactions.value / pageSize));
const paginationRange = computed(() => {
  const maxPages = 5;
  const range = [];
  const half = Math.floor(maxPages / 2);
  let start = Math.max(1, currentPage.value - half);
  let end = Math.min(totalPages.value, start + maxPages - 1);

  if (end - start + 1 < maxPages) {
    start = Math.max(1, end - maxPages + 1);
  }

  for (let i = start; i <= end; i++) {
    range.push(i);
  }
  return range;
});

const groupedDisplayedInstallments = computed(() => {
  const groups = {};
  displayedInstallments.value.forEach(i => {
    const transId = i.transaction?.id || i.transactionId || searchTransactionId.value || 'Unknown';
    if (!groups[transId]) groups[transId] = [];
    groups[transId].push(i);
  });
  return groups;
});

// Computed properties
const canProceed = computed(() => {
  if (selectedPaymentMethod.value === 'full') {
    return true;
  }
  if (selectedPaymentMethod.value === 'bnpl') {
    return selectedPlan.value !== null;
  }
  return false;
});

const calculatedInstallments = computed(() => {
  if (!selectedPlan.value) return [];
  
  const installments = [];
  const amount = parseFloat(transaction.value.amount);
  const months = selectedPlan.value.months;
  const installmentAmount = (amount / months).toFixed(2);
  
  const today = new Date();
  
  for (let i = 0; i < months; i++) {
    const dueDate = new Date(today);
    dueDate.setMonth(today.getMonth() + i + 1);
    
    installments.push({
      amount: installmentAmount,
      dueDate: dueDate.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' }),
      isPaid: false
    });
  }
  
  return installments;
});

// Methods
const checkEligibility = async () => {
  if (!transaction.value.cardId || !/^\d+$/.test(transaction.value.cardId)) {
    alert('Please enter a valid numeric Card ID');
    return;
  }
  if (!transaction.value.amount || parseFloat(transaction.value.amount) <= 0) {
    alert('Please enter a valid amount greater than 0');
    return;
  }
  if (!transaction.value.merchant) {
    alert('Please select a merchant');
    return;
  }
  if (!transaction.value.category) {
    alert('Please select a transaction category');
    return;
  }

  loading.value = true;

  try {
    const response = await fetch(`http://localhost:8089/api/profile/${transaction.value.cardId}/bnpl-eligibility`);
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || `Eligibility check failed: ${response.status}`);
    }
    const data = await response.json();
    eligibilityResult.value = data;
    currentStep.value = 1;
    errorMessage.value = '';
  } catch (error) {
    console.error('Error checking eligibility:', {
      message: error.message,
      status: error.response?.status
    });
    alert(`Error checking eligibility: ${error.message}`);
    errorMessage.value = error.message;
  } finally {
    loading.value = false;
  }
};

const selectPaymentMethod = (method) => {
  selectedPaymentMethod.value = method;
  transaction.value.isBNPL = method === 'bnpl';
  if (method === 'full') {
    selectedPlan.value = null;
    transaction.value.installmentPlan = '';
  }
};

const selectInstallmentPlan = (plan) => {
  selectedPlan.value = plan;
  transaction.value.installmentPlan = planMapping[plan.months];
};

const proceedToConfirmation = () => {
  if (!canProceed.value) return;
  currentStep.value = 2;
};

const proceedToPayInFull = () => {
  selectedPaymentMethod.value = 'full';
  transaction.value.isBNPL = false;
  selectedPlan.value = null;
  transaction.value.installmentPlan = '';
  currentStep.value = 2;
};

const confirmTransaction = async () => {
  loading.value = true;
  const payload = {
    cardId: parseInt(transaction.value.cardId),
    amount: parseFloat(transaction.value.amount),
    category: transaction.value.category,
    merchantName: transaction.value.merchant,
    isBNPL: transaction.value.isBNPL,
    installmentPlan: transaction.value.installmentPlan || 'THREE'
  };
  try {
    const response = await fetch(`http://localhost:8089/transactions?plan=${payload.installmentPlan}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || `Transaction failed: ${response.status}`);
    }
    const savedTransaction = await response.json();
    console.log(savedTransaction);
    transactions.value.unshift(savedTransaction);
    totalTransactions.value++;
    currentStep.value = 3;
    errorMessage.value = '';
  } catch (error) {
    console.error('Error confirming transaction:', {
      message: error.message,
      payload,
      status: error.response?.status
    });
    alert(`Error processing transaction: ${error.message}`);
    errorMessage.value = error.message;
  } finally {
    loading.value = false;
  }
};

const viewInstallments = async () => {
  const transId = prompt('Enter Transaction ID to view installments:');
  if (!transId || !/^\d+$/.test(transId.trim())) {
    alert('Please enter a valid numeric Transaction ID');
    return;
  }
  const transactionId = parseInt(transId.trim());
  loading.value = true;
  errorMessage.value = '';

  try {
    const response = await fetch(`http://localhost:8089/bnpl/installments/transaction/${transactionId}`);
    if (!response.ok) {
      let errorMsg = 'Failed to fetch installments';
      if (response.status === 404) {
        errorMsg = 'No installments found for this Transaction ID';
      } else {
        try {
          const errorData = await response.json();
          errorMsg = errorData.message || errorMsg;
        } catch {
          errorMsg = `Failed to fetch installments: ${response.status} ${response.statusText}`;
        }
      }
      throw new Error(errorMsg);
    }
    const data = await response.json();
    installments.value = data;
    displayedInstallments.value = data;
    installmentsFetched.value = data.length > 0;
    searchTransactionId.value = transactionId.toString();
    installmentFilter.value = 'all';
    currentStep.value = 4;
    errorMessage.value = data.length === 0 ? 'No installments found for this Transaction ID' : '';
  } catch (error) {
    console.error('Error fetching installments:', {
      message: error.message,
      transactionId,
      status: error.response?.status
    });
    alert(error.message);
    displayedInstallments.value = [];
    installments.value = [];
    installmentsFetched.value = false;
    errorMessage.value = error.message;
  } finally {
    loading.value = false;
  }
};

const viewTransactionHistory = () => {
  searchCardId.value = transaction.value.cardId || searchCardId.value;
  fetchTransactions();
  currentStep.value = 5;
};

const viewTransactionInstallments = async (id) => {
  if (!id) {
    alert('Invalid Transaction ID');
    return;
  }
  loading.value = true;
  errorMessage.value = '';

  try {
    const response = await fetch(`http://localhost:8080/bnpl/installments/transaction/${id}`);
    if (!response.ok) {
      let errorMsg = 'Failed to fetch installments';
      if (response.status === 404) {
        errorMsg = 'No installments found for this Transaction ID';
      } else {
        try {
          const errorData = await response.json();
          errorMsg = errorData.message || errorMsg;
        } catch {
          errorMsg = `Failed to fetch installments: ${response.status} ${response.statusText}`;
        }
      }
      throw new Error(errorMsg);
    }
    const data = await response.json();
    console.log('Raw installments response:', data);
    const enrichedData = data.map(installment => ({
      ...installment,
      transactionId: installment.transaction?.id || id
    }));
    installments.value = enrichedData;
    displayedInstallments.value = enrichedData;
    installmentsFetched.value = enrichedData.length > 0;
    searchTransactionId.value = id.toString();
    installmentFilter.value = 'all';
    currentStep.value = 4;
    errorMessage.value = enrichedData.length === 0 ? 'No installments found for this Transaction ID' : '';
  } catch (error) {
    console.error('Error fetching installments:', {
      message: error.message,
      transactionId: id,
      status: error.response?.status
    });
    alert(error.message);
    displayedInstallments.value = [];
    installments.value = [];
    installmentsFetched.value = false;
    errorMessage.value = error.message;
  } finally {
    loading.value = false;
  }
};

const resetForm = () => {
  currentStep.value = 0;
  transaction.value = {
    cardId: '',
    amount: '',
    category: '',
    merchant: '',
    isBNPL: false,
    installmentPlan: ''
  };
  eligibilityResult.value = {
    eligible: false,
    message: ''
  };
  selectedPaymentMethod.value = null;
  selectedPlan.value = null;
  searchTransactionId.value = '';
  displayedInstallments.value = [];
  installments.value = [];
  installmentsFetched.value = false;
  errorMessage.value = '';
};

const fetchInstallments = async () => {
  if (!searchTransactionId.value || !/^\d+$/.test(searchTransactionId.value.trim())) {
    alert('Please enter a valid numeric Transaction ID');
    return;
  }
  const transactionId = parseInt(searchTransactionId.value.trim());
  loading.value = true;
  errorMessage.value = '';

  try {
    const response = await fetch(`http://localhost:8080/bnpl/installments/transaction/${transactionId}`);
    if (!response.ok) {
      let errorMsg = 'Failed to fetch installments';
      if (response.status === 404) {
        errorMsg = 'No installments found for this Transaction ID';
      } else {
        try {
          const errorData = await response.json();
          errorMsg = errorData.message || errorMsg;
        } catch {
          errorMsg = `Failed to fetch installments: ${response.status} ${response.statusText}`;
        }
      }
      throw new Error(errorMsg);
    }
    const data = await response.json();
    console.log('Raw installments response:', data);
    const enrichedData = data.map(installment => ({
      ...installment,
      transactionId: installment.transaction?.id || transactionId
    }));
    installments.value = enrichedData;
    displayedInstallments.value = enrichedData;
    installmentsFetched.value = enrichedData.length > 0;
    installmentFilter.value = 'all';
    currentStep.value = 4;
    errorMessage.value = enrichedData.length === 0 ? 'No installments found for this Transaction ID' : '';
  } catch (error) {
    console.error('Error fetching installments:', {
      message: error.message,
      transactionId,
      status: error.response?.status
    });
    alert(error.message);
    displayedInstallments.value = [];
    installments.value = [];
    installmentsFetched.value = false;
    errorMessage.value = error.message;
  } finally {
    loading.value = false;
  }
};

const fetchTransactions = async () => {
  if (!searchCardId.value || !/^\d+$/.test(searchCardId.value.trim())) {
    alert('Please enter a valid numeric Card ID');
    return;
  }
  loading.value = true;
  errorMessage.value = '';

  try {
    const response = await fetch(`http://localhost:8089/transactions/card/${searchCardId.value.trim()}`, {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' }
    });
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || `Failed to fetch transactions: ${response.status}`);
    }
    let filteredTransactions = await response.json();
    if (transactionFilter.value !== 'all') {
      filteredTransactions = filteredTransactions.filter(t => t.isBNPL === (transactionFilter.value === 'bnpl'));
    }
    if (dateFilter.value.from) {
      const fromDate = new Date(dateFilter.value.from);
      filteredTransactions = filteredTransactions.filter(t => new Date(t.transactionDate) >= fromDate);
    }
    if (dateFilter.value.to) {
      const toDate = new Date(dateFilter.value.to);
      toDate.setHours(23, 59, 59, 999);
      filteredTransactions = filteredTransactions.filter(t => new Date(t.transactionDate) <= toDate);
    }
    totalTransactions.value = filteredTransactions.length;
    const start = (currentPage.value - 1) * pageSize;
    const end = start + pageSize;
    transactions.value = filteredTransactions.slice(start, end);
    errorMessage.value = filteredTransactions.length === 0 ? 'No transactions found for this Card ID' : '';
  } catch (error) {
    console.error('Error fetching transactions:', {
      message: error.message,
      cardId: searchCardId.value,
      status: error.response?.status
    });
    alert(`Error fetching transactions: ${error.message}`);
    transactions.value = [];
    totalTransactions.value = 0;
    errorMessage.value = error.message;
  } finally {
    loading.value = false;
  }
};

const filterInstallments = (filter) => {
  installmentFilter.value = filter;
  if (filter === 'all') {
    displayedInstallments.value = installments.value;
  } else if (filter === 'pending') {
    displayedInstallments.value = installments.value.filter(i => !i.isPaid && !isOverdue(i.dueDate));
  } else if (filter === 'paid') {
    displayedInstallments.value = installments.value.filter(i => i.isPaid);
  } else if (filter === 'overdue') {
    displayedInstallments.value = installments.value.filter(i => !i.isPaid && isOverdue(i.dueDate));
  }
};

const filterTransactions = (filter) => {
  transactionFilter.value = filter;
  currentPage.value = 1;
  fetchTransactions();
};

const applyDateFilter = () => {
  currentPage.value = 1;
  fetchTransactions();
};

const clearDateFilter = () => {
  dateFilter.value = {
    from: '',
    to: ''
  };
  fetchTransactions();
};

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
    fetchTransactions();
  }
};

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
    fetchTransactions();
  }
};

const goToPage = (page) => {
  currentPage.value = page;
  fetchTransactions();
};

const payInstallment = async (installment) => {
  if (!installment?.id || !installment?.amount) {
    alert('Invalid installment data');
    return;
  }
  if (!confirm(`Pay installment #${installment.id} of ₹${installment.amount.toFixed(2)}?`)) return;

  loading.value = true;
  errorMessage.value = '';

  try {
    const amount = parseFloat(installment.amount.toFixed(2));
    const response = await fetch(
      `http://localhost:8080/bnpl/installments/${installment.id}/pay?amount=${amount}`,
      {
        method: 'POST'
      }
    );

    if (!response.ok) {
      let errorMsg = 'Payment failed';
      try {
        const errorData = await response.json();
        errorMsg = errorData.message || errorMsg;
      } catch {
        errorMsg = `Payment failed: ${response.status} ${response.statusText}`;
      }
      throw new Error(errorMsg);
    }

    const transId = installment.transaction?.id || installment.transactionId || searchTransactionId.value;
    if (!transId) {
      throw new Error('Transaction ID not found in installment data');
    }
    const res = await fetch(`http://localhost:8080/bnpl/installments/transaction/${transId}`);
    if (!res.ok) {
      const errorData = await res.json().catch(() => ({}));
      throw new Error(errorData.message || 'Failed to refetch installments');
    }
    const data = await res.json();
    console.log('Raw installments response after payment:', data);
    const enrichedData = data.map(i => ({
      ...i,
      transactionId: i.transaction?.id || transId
    }));
    installments.value = enrichedData;
    displayedInstallments.value = enrichedData;
    installmentsFetched.value = enrichedData.length > 0;
    filterInstallments(installmentFilter.value);
    alert('Payment successful!');
    errorMessage.value = enrichedData.length === 0 ? 'No installments found after payment' : '';
  } catch (error) {
    console.error('Error paying installment:', {
      message: error.message,
      installmentId: installment.id,
      amount: installment.amount,
      status: error.response?.status
    });
    alert(`Payment failed: ${error.message}`);
    errorMessage.value = error.message;
  } finally {
    loading.value = false;
  }
};

const calculateTotalAmount = (installments) => {
  return installments.reduce((total, installment) => total + parseFloat(installment.amount || 0), 0).toFixed(2);
};

const isOverdue = (dueDate) => {
  if (!dueDate) return false;
  const today = new Date();
  const due = new Date(dueDate);
  return due < today && !isNaN(due.getTime());
};

const formatDate = (dateString) => {
  if (!dateString) {
    // Return current date if dateString is missing
    return new Date().toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }
  
  let date;
  try {
    date = new Date(dateString);
    if (isNaN(date.getTime())) {
      // Try parsing YYYY-MM-DD format explicitly
      const parts = dateString.match(/^(\d{4})-(\d{2})-(\d{2})$/);
      if (parts) {
        date = new Date(parts[1], parts[2] - 1, parts[3]);
      } else {
        // Fallback to current date if parsing fails
        console.warn(`Invalid date format: ${dateString}`);
        return new Date().toLocaleDateString('en-US', {
          year: 'numeric',
          month: 'short',
          day: 'numeric'
        });
      }
    }
  } catch (error) {
    console.warn(`Error parsing date: ${dateString}`, error);
    // Fallback to current date
    return new Date().toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  });
};

const getStatusClass = (status) => {
  switch (status?.toLowerCase()) {
    case 'completed':
      return 'bg-green-100 text-green-800';
    case 'active':
      return 'bg-blue-100 text-blue-800';
    case 'overdue':
      return 'bg-red-100 text-red-800';
    case 'pending':
      return 'bg-yellow-100 text-yellow-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
};

onMounted(() => {
  // Any initialization logic can go here
});
</script>

<style>
:root {
  --color-primary: #007BFF;       /* violet-600 */
  --color-primary-dark: ##007BFF;
  --color-primary-50: #eff6ff;
}

.bg-primary {
  background-color: var(--color-primary);
}

.bg-primary-dark {
  background-color: var(--color-primary-dark);
}

.bg-primary-50 {
  background-color: var(--color-primary-50);
}

.text-primary {
  color: var(--color-primary);
}

.border-primary {
  border-color: var(--color-primary);
}

.focus\:ring-primary:focus {
  --tw-ring-color: var(--color-primary);
}

.focus\:border-primary:focus {
  border-color: var(--color-primary);
}

.hover\:bg-primary-dark:hover {
  background-color: var(--color-primary-dark);
}
</style>