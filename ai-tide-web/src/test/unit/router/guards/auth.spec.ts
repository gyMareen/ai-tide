import { describe, it, expect, beforeEach, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useUserStore } from '@/stores/user'

describe('router guards', () => {
  let userStore: any

  beforeEach(() => {
    setActivePinia(createPinia())
    userStore = useUserStore()
    userStore.$patch({
      isAuthenticated: false,
      userInfo: null,
      token: '',
      isLoading: false,
      isAdmin: false,
    })
    vi.clearAllMocks()
  })

  describe('authentication state', () => {
    it('should have isAuthenticated as false initially', () => {
      expect(userStore.isAuthenticated).toBe(false)
    })

    it('should update isAuthenticated when user logs in', () => {
      userStore.token = 'test-token'
      userStore.userInfo = { id: '1', username: 'test', email: 'test@example.com', role: 'USER', status: 'ACTIVE', createdAt: '2024-01-01T00:00:00Z', updatedAt: '2024-01-01T00:00:00Z' }
      expect(userStore.isAuthenticated).toBe(true)
    })
  })
})
