<template>
  <div class="min-h-screen flex flex-col items-center justify-center bg-gray-100 p-6">
    <div v-if="loading" class="text-gray-600">Loading profile...</div>

    <div v-else-if="error" class="text-red-600">{{ error }}</div>

    <div v-else class="bg-white p-6 rounded shadow-md w-full max-w-md">
      <h2 class="text-2xl font-bold mb-4">Welcome, {{ user.fullName }}</h2>
      <p><strong>Email:</strong> {{ user.email }}</p>
      <p><strong>Phone:</strong> {{ user.phone }}</p>
      <p><strong>Address:</strong> {{ user.address }}</p>
      <p><strong>Annual Income:</strong> {{ user.annualIncome }}</p>
      <!-- Add more fields as needed -->

      <button @click="logout" class="mt-6 w-full bg-red-500 text-white py-2 rounded hover:bg-red-600">
        Logout
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Dashboard',
  data() {
    return {
      user: null,
      loading: true,
      error: null,
    }
  },
  async mounted() {
    const userId = localStorage.getItem('userId')
    if (!userId) {
      this.error = 'User not logged in. Please login first.'
      this.loading = false
      return
    }
    try {
      const response = await this.$axios.get(`/api/profile/${userId}`)
      this.user = response.data
    } catch (err) {
      this.error = err.response?.data || 'Failed to load profile.'
    } finally {
      this.loading = false
    }
  },
  methods: {
    logout() {
      localStorage.clear()
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
/* optional styling if needed */
</style>
