<template>
  <div class="min-h-screen flex bg-gradient-to-br from-purple-50 to-blue-50">
    <!-- Left Column - Hero (desktop only) -->
    <div class="hidden lg:flex lg:w-1/2 relative">
      <div 
        class="absolute inset-0 bg-cover bg-center"
        :style="{ backgroundImage: `url('https://images.unsplash.com/photo-1498050108023-c5249f4df085?q=80&w=1470&auto=format&fit=crop')` }"
      >
        <div class="absolute inset-0 bg-gradient-to-r from-purple-900/80 to-blue-900/60"></div>
      </div>
      <div class="relative z-10 w-full h-full flex items-center justify-center px-4 sm:px-6 lg:px-8">
        <div class="text-center text-white">
          <a href="/" class="flex justify-center items-center gap-3 mb-8">
            <div class="h-14 w-14 rounded-full bg-gradient-to-r from-blue-600 to-purple-600 flex items-center justify-center">
              <span class="text-white font-bold text-2xl">C</span>
            </div>
            <h1 class="text-5xl font-bold">Credify</h1>
          </a>
          <h2 class="text-4xl md:text-6xl font-bold mb-6 leading-tight">
            Master Your Credit Card
            <br />
            <span class="text-transparent bg-clip-text bg-gradient-to-r from-purple-300 to-blue-300">
              Management Portal
            </span>
          </h2>
          <p class="text-xl md:text-2xl max-w-3xl mx-auto leading-relaxed">
            Take control of your financial future with our comprehensive credit card management platform. 
            Track spending, optimize rewards, and make smarter financial decisions.
          </p>
        </div>
      </div>
    </div>

    <!-- Right Column - Forms -->
    <div class="w-full lg:w-1/2 flex items-center justify-center p-6 md:p-8">
      <div class="w-full max-w-md">

        <!-- Logo (mobile & shared) -->
        <div class="text-center mb-10">
          <router-link to="/" class="inline-flex items-center gap-3">
            <div class="w-12 h-12 rounded-full bg-gradient-to-r from-purple-600 to-blue-600 flex items-center justify-center">
              <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <rect width="20" height="14" x="2" y="5" rx="2" />
                <line x1="2" x2="22" y1="10" y2="10" />
              </svg>
            </div>
            <span class="font-bold text-3xl bg-clip-text text-transparent bg-gradient-to-r from-purple-600 to-blue-600">Credify</span>
          </router-link>
        </div>

        <!-- Login Form -->
        <div v-if="!showForgotPassword && !showResetPassword && !showOtpForm && !showSuccessMessage"
             class="bg-white/85 backdrop-blur-lg shadow-2xl rounded-3xl p-8 animate-scale-in">
          <div class="text-center mb-8">
            <h2 class="text-2xl font-bold text-gray-800">Sign In</h2>
            <p class="text-gray-600 mt-2">Enter your credentials to continue</p>
          </div>

          <div class="space-y-6">
            <!-- Email -->
            <div>
              <label class="block text-gray-700 font-medium mb-2">Email</label>
              <div class="relative">
                <svg class="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                </svg>
                <input v-model="loginForm.email" type="email" placeholder="name@example.com"
                       class="w-full pl-12 pr-4 py-3 border border-gray-200 rounded-xl focus:border-purple-400 focus:ring-2 focus:ring-purple-100 outline-none transition" required />
              </div>
              <p v-if="errors.email" class="text-red-600 text-sm mt-2">{{ errors.email }}</p>
            </div>

            <!-- Password -->
            <div>
              <label class="block text-gray-700 font-medium mb-2">Password</label>
              <div class="relative">
                <svg class="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 11c0-1.1.9-2 2-2s2 .9 2 2-2 4-2 4m-6-4c0-1.1.9-2 2-2s2 .9 2 2-2 4-2 4m-2-8h4m-4 8h4" />
                </svg>
                <input v-model="loginForm.password" :type="showPassword ? 'text' : 'password'" placeholder="••••••••"
                       class="w-full pl-12 pr-12 py-3 border border-gray-200 rounded-xl focus:border-purple-400 focus:ring-2 focus:ring-purple-100 outline-none transition" required />
                <button @click="showPassword = !showPassword" type="button"
                        class="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition">
                  <svg v-if="showPassword" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                  </svg>
                  <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.542-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M3 3l18 18" />
                  </svg>
                </button>
              </div>
              <p v-if="errors.password" class="text-red-600 text-sm mt-2">{{ errors.password }}</p>
              <div class="text-right mt-2">
                <button @click="showForgotPassword = true" class="text-sm text-purple-600 hover:text-purple-800 font-medium">
                  Forgot password?
                </button>
              </div>
            </div>

            <div class="flex items-center">
              <input type="checkbox" v-model="loginForm.rememberMe" class="h-4 w-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500" />
              <label class="ml-2 text-sm text-gray-700">Remember me</label>
            </div>

            <button @click.prevent="handleLogin" :disabled="isLoading"
                    class="w-full bg-gradient-to-r from-purple-600 to-blue-600 text-white py-3.5 rounded-xl hover:from-purple-700 hover:to-blue-700 transition-all duration-300 font-medium shadow-md hover:shadow-lg">
              {{ isLoading ? 'Signing in...' : 'Sign In' }}
            </button>

            <div class="text-center mt-6 text-sm text-gray-600">
              Don't have an account? 
              <router-link to="/register" class="text-purple-600 hover:text-purple-800 font-medium">Sign up</router-link>
            </div>
          </div>
        </div>

        <!-- Forgot Password - Email -->
        <div v-if="showForgotPassword && !showOtpForm && !showResetPassword && !showSuccessMessage"
             class="bg-white/85 backdrop-blur-lg shadow-2xl rounded-3xl p-8 animate-scale-in">
          <div class="text-center mb-8">
            <h2 class="text-2xl font-bold text-gray-800">Reset Password</h2>
            <p class="text-gray-600 mt-2">We'll send a 6-digit code to your email</p>
          </div>

          <div class="space-y-6">
            <div>
              <label class="block text-gray-700 font-medium mb-2">Email</label>
              <div class="relative">
                <svg class="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                </svg>
                <input v-model="forgotPasswordForm.email" type="email" placeholder="name@example.com"
                       class="w-full pl-12 pr-4 py-3 border border-gray-200 rounded-xl focus:border-purple-400 focus:ring-2 focus:ring-purple-100 outline-none transition" required />
              </div>
              <p v-if="errors.email" class="text-red-600 text-sm mt-2">{{ errors.email }}</p>
            </div>

            <button @click.prevent="handleForgotPassword" :disabled="isLoading"
                    class="w-full bg-gradient-to-r from-purple-600 to-blue-600 text-white py-3.5 rounded-xl hover:from-purple-700 hover:to-blue-700 transition-all duration-300 font-medium shadow-md hover:shadow-lg">
              {{ isLoading ? 'Sending...' : 'Send OTP' }}
            </button>

            <div class="text-center mt-4">
              <button @click="showForgotPassword = false" class="text-sm text-purple-600 hover:text-purple-800 font-medium">
                Back to Sign In
              </button>
            </div>
          </div>
        </div>

        <!-- OTP Form -->
        <div v-if="showOtpForm && !showSuccessMessage"
             class="bg-white/85 backdrop-blur-lg shadow-2xl rounded-3xl p-8 animate-scale-in">
          <div class="text-center mb-8">
            <h2 class="text-2xl font-bold text-gray-800">Enter OTP</h2>
            <p class="text-gray-600 mt-2">Code sent to {{ forgotPasswordForm.email }}</p>
          </div>

          <div class="flex justify-center gap-4 mb-8">
            <input v-for="(digit, index) in 6" :key="index"
                   v-model="otpDigits[index]"
                   type="text" maxlength="1" inputmode="numeric" pattern="[0-9]*"
                   @input="handleOtpInput($event, index)"
                   @keydown="handleOtpKeydown($event, index)"
                   @paste="handleOtpPaste"
                   ref="otpInputs"
                   class="w-12 h-14 text-center text-2xl font-bold border-b-4 border-gray-300 focus:border-purple-600 bg-transparent outline-none transition-all" />
          </div>

          <p v-if="errors.otp" class="text-red-600 text-center text-sm mb-6">{{ errors.otp }}</p>

          <button @click.prevent="handleVerifyOtp" :disabled="isLoading || otpDigits.join('').length !== 6"
                  class="w-full bg-gradient-to-r from-purple-600 to-blue-600 text-white py-3.5 rounded-xl hover:from-purple-700 hover:to-blue-700 transition-all duration-300 font-medium shadow-md hover:shadow-lg disabled:opacity-60">
            {{ isLoading ? 'Verifying...' : 'Verify OTP' }}
          </button>

          <div class="text-center mt-6">
            <button @click="showOtpForm = false; showForgotPassword = true" class="text-sm text-purple-600 hover:text-purple-800 font-medium">
              Back to Email
            </button>
          </div>
        </div>

        <!-- Reset Password -->
        <div v-if="showResetPassword && !showSuccessMessage"
             class="bg-white/85 backdrop-blur-lg shadow-2xl rounded-3xl p-8 animate-scale-in">
          <div class="text-center mb-8">
            <h2 class="text-2xl font-bold text-gray-800">Set New Password</h2>
            <p class="text-gray-600 mt-2">Choose a strong and secure password</p>
          </div>

          <div class="space-y-6">
            <div>
              <label class="block text-gray-700 font-medium mb-2">New Password</label>
              <div class="relative">
                <input v-model="resetPasswordForm.newPassword" :type="showNewPassword ? 'text' : 'password'"
                       placeholder="••••••••"
                       class="w-full pl-4 pr-12 py-3 border border-gray-200 rounded-xl focus:border-purple-400 focus:ring-2 focus:ring-purple-100 outline-none transition" required />
                <button @click="showNewPassword = !showNewPassword" type="button"
                        class="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition">
                  <svg v-if="showNewPassword" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                  </svg>
                  <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.542-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M3 3l18 18" />
                  </svg>
                </button>
              </div>
              <p v-if="errors.newPassword" class="text-red-600 text-sm mt-2">{{ errors.newPassword }}</p>
            </div>

            <div>
              <label class="block text-gray-700 font-medium mb-2">Confirm Password</label>
              <div class="relative">
                <input v-model="resetPasswordForm.confirmPassword" :type="showConfirmPassword ? 'text' : 'password'"
                       placeholder="••••••••"
                       class="w-full pl-4 pr-12 py-3 border border-gray-200 rounded-xl focus:border-purple-400 focus:ring-2 focus:ring-purple-100 outline-none transition" required />
                <button @click="showConfirmPassword = !showConfirmPassword" type="button"
                        class="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition">
                  <svg v-if="showConfirmPassword" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                  </svg>
                  <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.542-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M3 3l18 18" />
                  </svg>
                </button>
              </div>
              <p v-if="errors.confirmPassword" class="text-red-600 text-sm mt-2">{{ errors.confirmPassword }}</p>
            </div>

            <button @click.prevent="handleResetPassword" :disabled="isLoading"
                    class="w-full bg-gradient-to-r from-purple-600 to-blue-600 text-white py-3.5 rounded-xl hover:from-purple-700 hover:to-blue-700 transition-all duration-300 font-medium shadow-md hover:shadow-lg">
              {{ isLoading ? 'Resetting...' : 'Reset Password' }}
            </button>

            <div class="text-center mt-4">
              <button @click="showResetPassword = false; showForgotPassword = true" class="text-sm text-purple-600 hover:text-purple-800 font-medium">
                Back to Email
              </button>
            </div>
          </div>
        </div>

        <!-- Success Screen -->
        <div v-if="showSuccessMessage"
             class="bg-white/90 backdrop-blur-xl shadow-2xl rounded-3xl p-10 text-center animate-fade-in">
          <div class="mb-8">
            <div class="w-20 h-20 mx-auto rounded-full bg-green-100 flex items-center justify-center">
              <svg class="w-12 h-12 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7" />
              </svg>
            </div>
          </div>

          <h2 class="text-2xl md:text-3xl font-bold text-gray-800 mb-4">
            Password Updated Successfully!
          </h2>

          <p class="text-gray-600 text-lg mb-10 leading-relaxed">
            Your password has been securely updated.<br />
            You can now sign in with your new credentials.
          </p>

          <button @click="resetToLogin"
                  class="w-full bg-gradient-to-r from-purple-600 to-blue-600 text-white py-4 rounded-xl hover:from-purple-700 hover:to-blue-700 transition-all duration-300 font-medium text-lg shadow-lg hover:shadow-xl transform hover:-translate-y-0.5">
            Return to Sign In
          </button>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
// const BASE_URL = import.meta.env.VITE_API_URL;
const API_BASE_URL = 'http://localhost:8080';
const router = useRouter();

const showForgotPassword = ref(false);
const showResetPassword = ref(false);
const showOtpForm = ref(false);
const showSuccessMessage = ref(false);

const showPassword = ref(false);
const showNewPassword = ref(false);
const showConfirmPassword = ref(false);
const isLoading = ref(false);

const loginForm = reactive({ email: '', password: '', rememberMe: false });
const forgotPasswordForm = reactive({ email: '', otp: '' });
const resetPasswordForm = reactive({ newPassword: '', confirmPassword: '' });

const otpDigits = ref(Array(6).fill(''));
const otpInputs = ref([]);

const errors = reactive({
  email: '',
  password: '',
  otp: '',
  newPassword: '',
  confirmPassword: ''
});

const handleOtpInput = (e, index) => {
  const value = e.target.value.replace(/[^0-9]/g, '');
  otpDigits.value[index] = value;
  if (value && index < 5) nextTick(() => otpInputs.value[index + 1]?.focus());
  forgotPasswordForm.otp = otpDigits.value.join('');
};

const handleOtpKeydown = (e, index) => {
  if (e.key === 'Backspace' && !otpDigits.value[index] && index > 0) {
    nextTick(() => otpInputs.value[index - 1]?.focus());
  }
};

const handleOtpPaste = (e) => {
  e.preventDefault();
  const pasted = (e.clipboardData || window.clipboardData).getData('text').replace(/\D/g, '');
  if (pasted.length > 0) {
    const digits = pasted.slice(0, 6).split('');
    otpDigits.value = digits.concat(Array(6 - digits.length).fill(''));
    forgotPasswordForm.otp = otpDigits.value.join('');
    if (digits.length === 6) nextTick(() => handleVerifyOtp());
    else nextTick(() => otpInputs.value[digits.length]?.focus());
  }
};

const resetErrors = () => Object.keys(errors).forEach(key => errors[key] = '');

const resetToLogin = () => {
  showSuccessMessage.value = false;
  showForgotPassword.value = false;
  showResetPassword.value = false;
  showOtpForm.value = false;
  loginForm.email = '';
  loginForm.password = '';
  forgotPasswordForm.email = '';
  forgotPasswordForm.otp = '';
  resetPasswordForm.newPassword = '';
  resetPasswordForm.confirmPassword = '';
  otpDigits.value = Array(6).fill('');
  errors.email = errors.password = errors.otp = errors.newPassword = errors.confirmPassword = '';
};

const handleLogin = async () => {
  resetErrors();

  if (!loginForm.email) {
    errors.email = 'Email is required';
    return;
  }
  if (!loginForm.password) {
    errors.password = 'Password is required';
    return;
  }

  isLoading.value = true;

  try {
    const response = await axios.post(`${API_BASE_URL}/api/profile/login`, {
      email: loginForm.email,
      password: loginForm.password
    });

    const { token } = response.data;

   const storage = loginForm.rememberMe ? localStorage : sessionStorage;
storage.setItem('authToken', token);
router.push('/dashboard');

  } catch (err) {
    console.error('Login error:', err);

    if (err.response) {
      // Backend error response (most common: 401 Unauthorized or 400 Bad Request)
      const status = err.response.status;
      const backendMessage = err.response.data?.message || 'Invalid email or password. Please try again';

      if (status === 401) {
        // Wrong password / invalid credentials
        errors.password = backendMessage || 'Invalid email or password';
      } else if (status === 400) {
        // Validation error (e.g. bad format)
        errors.email = backendMessage || 'Invalid input';
      } else {
        // Other server error
        errors.password = backendMessage || 'Server error. Please try again later.';
      }
    } else if (err.request) {
      // No response → network issue / CORS / backend down
      errors.password = 'Cannot connect to server. Please check your internet or try later.';
    } else {
      // Unexpected JS error
      errors.password = 'An unexpected error occurred. Please try again.';
    }
  } finally {
    isLoading.value = false;
  }
};

const handleForgotPassword = async () => {
  resetErrors();
  if (!forgotPasswordForm.email) return errors.email = 'Email is required';

  isLoading.value = true;
  try {
    await axios.post(`${API_BASE_URL}/api/auth/forgot-password`, {
      email: forgotPasswordForm.email
    });
    showOtpForm.value = true;
    nextTick(() => otpInputs.value[0]?.focus());
  } catch (err) {
    errors.email = err.response?.data || 'Failed to send OTP';
  } finally {
    isLoading.value = false;
  }
};

const handleVerifyOtp = async () => {
  resetErrors();
  if (forgotPasswordForm.otp.length !== 6) return errors.otp = 'Enter complete 6-digit OTP';

  isLoading.value = true;
  try {
    await axios.post(`${API_BASE_URL}/api/auth/verify-otp`, {
      email: forgotPasswordForm.email,
      otp: forgotPasswordForm.otp
    });
    showOtpForm.value = false;
    showResetPassword.value = true;
  } catch (err) {
    errors.otp = err.response?.data || 'Invalid or expired OTP';
  } finally {
    isLoading.value = false;
  }
};

const handleResetPassword = async () => {
  resetErrors();
  if (!resetPasswordForm.newPassword) errors.newPassword = 'Required';
  if (!resetPasswordForm.confirmPassword) errors.confirmPassword = 'Required';
  if (resetPasswordForm.newPassword !== resetPasswordForm.confirmPassword) {
    errors.confirmPassword = 'Passwords do not match';
  }
  if (Object.values(errors).some(e => e)) return;

  isLoading.value = true;
  try {
    await axios.post(`${API_BASE_URL}/api/auth/reset-password`, {
      email: forgotPasswordForm.email,
      newPassword: resetPasswordForm.newPassword
    });
    showResetPassword.value = false;
    showSuccessMessage.value = true;
  } catch (err) {
    alert(err.response?.data || 'Failed to reset password');
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.bg-purple-gradient {
  background: linear-gradient(to right, #6B46C1, #4299E1);
}

.animate-scale-in {
  animation: scaleIn 0.4s ease-out;
}

.animate-fade-in {
  animation: fadeIn 0.6s ease-out;
}

@keyframes scaleIn {
  from { opacity: 0; transform: scale(0.96); }
  to   { opacity: 1; transform: scale(1); }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(16px); }
  to   { opacity: 1; transform: translateY(0); }
}
</style>