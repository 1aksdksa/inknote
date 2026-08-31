import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { http } from '@/api/http'

const TOKEN_KEY = 'inknote_token'
const PROFILE_KEY = 'inknote_profile'

export interface AuthProfile {
  userId: string
  username: string
  displayName: string
}

export interface LoginResult extends AuthProfile {
  token: string
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem(TOKEN_KEY))
  const profile = ref<AuthProfile | null>(readProfile())

  const isLoggedIn = computed(() => Boolean(token.value))

  function readProfile(): AuthProfile | null {
    const raw = localStorage.getItem(PROFILE_KEY)
    if (!raw) return null
    try {
      return JSON.parse(raw) as AuthProfile
    } catch {
      return null
    }
  }

  function persist(login: LoginResult) {
    token.value = login.token
    profile.value = {
      userId: login.userId,
      username: login.username,
      displayName: login.displayName,
    }
    localStorage.setItem(TOKEN_KEY, login.token)
    localStorage.setItem(PROFILE_KEY, JSON.stringify(profile.value))
  }

  function clear() {
    token.value = null
    profile.value = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(PROFILE_KEY)
  }

  async function register(payload: {
    username: string
    password: string
    displayName?: string
  }) {
    const data = await http.post<LoginResult>('/auth/register', payload)
    persist(data)
    return data
  }

  async function login(payload: { username: string; password: string }) {
    const data = await http.post<LoginResult>('/auth/login', payload)
    persist(data)
    return data
  }

  async function fetchProfile() {
    const data = await http.get<AuthProfile>('/auth/profile')
    profile.value = data
    localStorage.setItem(PROFILE_KEY, JSON.stringify(data))
    return data
  }

  function logout() {
    clear()
  }

  return {
    token,
    profile,
    isLoggedIn,
    register,
    login,
    fetchProfile,
    logout,
    clear,
  }
})
