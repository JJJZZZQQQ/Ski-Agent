import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('ski_token') || '')
  const userInfo = ref<any>(null)

  const isLoggedIn = computed(() => !!token.value)

  async function login(username: string, password: string) {
    const res = await authApi.login(username, password)
    token.value = res.token
    userInfo.value = res.user
    localStorage.setItem('ski_token', res.token)
    return res
  }

  async function register(username: string, password: string, nickname?: string) {
    const res = await authApi.register(username, password, nickname)
    token.value = res.token
    userInfo.value = res.user
    localStorage.setItem('ski_token', res.token)
    return res
  }

  async function fetchMe() {
    if (!token.value) return
    try { userInfo.value = (await authApi.me()).data } catch { logout() }
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('ski_token')
  }

  return { token, userInfo, isLoggedIn, login, register, fetchMe, logout }
})
