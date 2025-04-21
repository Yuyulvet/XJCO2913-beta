import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/api'

export const useBookingStore = defineStore('booking', () => {
  const currentBooking = ref(null)
  const bookingHistory = ref([])
  const loading = ref(false)

  const fetchCurrentBooking = async () => {
    try {
      loading.value = true
      const response = await api.get('/bookings/current')
      currentBooking.value = response.data
    } catch (error) {
      console.error('获取当前订单失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const fetchBookingHistory = async () => {
    try {
      loading.value = true
      const response = await api.get('/bookings/history')
      bookingHistory.value = response.data
    } catch (error) {
      console.error('获取订单历史失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const createBooking = async (scooterId) => {
    try {
      loading.value = true
      const response = await api.post('/bookings', { scooterId })
      currentBooking.value = response.data
      return response.data
    } catch (error) {
      console.error('创建订单失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const endBooking = async (bookingId) => {
    try {
      loading.value = true
      await api.put(`/bookings/${bookingId}/end`)
      currentBooking.value = null
    } catch (error) {
      console.error('结束订单失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const extendBooking = async (bookingId, minutes) => {
    try {
      loading.value = true
      const response = await api.put(`/bookings/${bookingId}/extend`, { minutes })
      currentBooking.value = response.data
    } catch (error) {
      console.error('延长订单失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  return {
    currentBooking,
    bookingHistory,
    loading,
    fetchCurrentBooking,
    fetchBookingHistory,
    createBooking,
    endBooking,
    extendBooking
  }
}) 