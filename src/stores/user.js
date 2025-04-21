import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/api'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const loading = ref(false)

  const login = async (credentials) => {
    try {
      loading.value = true
      const response = await api.post('/auth/login', credentials)
      user.value = response.data
      return response.data
    } catch (error) {
      console.error('登录失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const register = async (userData) => {
    try {
      loading.value = true
      const response = await api.post('/auth/register', userData)
      return response.data
    } catch (error) {
      console.error('注册失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const logout = () => {
    user.value = null
  }

  return {
    user,
    loading,
    login,
    register,
    logout
  }
}) 