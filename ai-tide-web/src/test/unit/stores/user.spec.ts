import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useUserStore } from '@/stores/user'

describe('userStore', () => {
  let userStore: ReturnType<typeof useUserStore>

  beforeEach(() => {
    setActivePinia(createPinia())
    userStore = useUserStore()
    vi.clearAllMocks()
  })

  afterEach(() => {
    vi.clearAllTimers()
  })

  describe('initial state', () => {
    it('should have empty token initially', () => {
      expect(userStore.token).toBe('')
    })

    it('should have null userInfo initially', () => {
      expect(userStore.userInfo).toBeNull()
    })

    it('should have isLoading as false initially', () => {
      expect(userStore.isLoading).toBe(false)
    })

    it('should have isAuthenticated as false initially', () => {
      expect(userStore.isAuthenticated).toBe(false)
    })

    it('should have isAdmin as false initially', () => {
      expect(userStore.isAdmin).toBe(false)
    })
  })

  describe('init', () => {
    it('should load token from localStorage', () => {
      localStorage.setItem('token', 'test-token')
      userStore.init()
      expect(userStore.token).toBe('test-token')
    })

    it('should load userInfo from localStorage', () => {
      const mockUserInfo = {
        id: '1',
        username: 'testuser',
        email: 'test@example.com',
        nickname: 'Test User',
        role: 'USER' as const,
        status: 'ACTIVE' as const,
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
      }
      localStorage.setItem('userInfo', JSON.stringify(mockUserInfo))
      userStore.init()
      expect(userStore.userInfo?.username).toBe('testuser')
    })

    it('should handle invalid JSON in userInfo', () => {
      localStorage.setItem('userInfo', 'invalid-json')
      userStore.init()
      expect(userStore.userInfo).toBeNull()
    })

    it('should handle missing localStorage data', () => {
      localStorage.clear()
      userStore.init()
      expect(userStore.token).toBe('')
      expect(userStore.userInfo).toBeNull()
    })
  })

  describe('login', () => {
    const mockCredentials = {
      username: 'testuser',
      password: 'password123',
    }

    beforeEach(() => {
      global.fetch = vi.fn()
    })

    it('should set loading state during login', async () => {
      const mockResponse = {
        code: 200,
        data: {
          token: 'test-token',
          user: {
            id: '1',
            username: 'testuser',
            email: 'test@example.com',
            role: 'USER' as const,
            status: 'ACTIVE' as const,
            createdAt: '2024-01-01T00:00:00Z',
            updatedAt: '2024-01-01T00:00:00Z',
          },
        },
      }

      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response)

      const loginPromise = userStore.login(mockCredentials)
      expect(userStore.isLoading).toBe(true)
      await loginPromise
      expect(userStore.isLoading).toBe(false)
    })

    it('should successfully login and update state', async () => {
      const mockResponse = {
        code: 200,
        data: {
          token: 'test-token',
          user: {
            id: '1',
            username: 'testuser',
            email: 'test@example.com',
            nickname: 'Test User',
            role: 'ADMIN' as const,
            status: 'ACTIVE' as const,
            createdAt: '2024-01-01T00:00:00Z',
            updatedAt: '2024-01-01T00:00:00Z',
          },
        },
      }

      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response)

      const result = await userStore.login(mockCredentials)

      expect(userStore.token).toBe('test-token')
      expect(userStore.userInfo?.username).toBe('testuser')
      expect(userStore.isAuthenticated).toBe(true)
      expect(userStore.isAdmin).toBe(true)
      expect(localStorage.getItem('token')).toBe('test-token')
      expect(result).toEqual(mockResponse.data)
    })

    it('should throw error on failed response', async () => {
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: false,
      } as Response)

      await expect(userStore.login(mockCredentials)).rejects.toThrow('登录失败')
    })

    it('should throw error on non-200 code', async () => {
      const mockResponse = {
        code: 401,
        message: '无效凭证',
      }

      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response)

      await expect(userStore.login(mockCredentials)).rejects.toThrow('无效凭证')
    })

    it('should reset loading loading state on error', async () => {
      vi.mocked(global.fetch).mockRejectedValueOnce(new Error('Network error'))

      await expect(userStore.login(mockCredentials)).rejects.toThrow()
      expect(userStore.isLoading).toBe(false)
    })
  })

  describe('register', () => {
    const mockUserData = {
      username: 'newuser',
      email: 'new@example.com',
      password: 'password123',
      nickname: 'New User',
    }

    beforeEach(() => {
      global.fetch = vi.fn()
    })

    it('should successfully register and update state', async () => {
      const mockResponse = {
        code: 200,
        data: {
          token: 'new-token',
          user: {
            id: '2',
            username: 'newuser',
            email: 'new@example.com',
            nickname: 'New User',
            role: 'USER' as const,
            status: 'ACTIVE' as const,
            createdAt: '2024-01-01T00:00:00Z',
            updatedAt: '2024-01-01T00:00:00Z',
          },
        },
      }

      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response)

      const result = await userStore.register(mockUserData)

      expect(userStore.token).toBe('new-token')
      expect(userStore.userInfo?.username).toBe('newuser')
      expect(userStore.isAuthenticated).toBe(true)
      expect(result).toEqual(mockResponse.data)
    })

    it('should throw error on failed registration', async () => {
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: false,
      } as Response)

      await expect(userStore.register(mockUserData)).rejects.toThrow('注册失败')
    })
  })

  describe('logout', () => {
    beforeEach(() => {
      userStore.token = 'test-token'
      userStore.userInfo = {
        id: '1',
        username: 'testuser',
        email: 'test@example.com',
        role: 'USER' as const,
        status: 'ACTIVE' as const,
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
      }
      localStorage.setItem('token', 'test-token')
      localStorage.setItem('userInfo', JSON.stringify({}))
      global.fetch = vi.fn()
    })

    it('should clear user state and localStorage', async () => {
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
      } as Response)

      await userStore.logout()

      expect(userStore.token).toBe('')
      expect(userStore.userInfo).toBeNull()
      expect(userStore.isAuthenticated).toBe(false)
      expect(localStorage.getItem('token')).toBeNull()
      expect(localStorage.getItem('userInfo')).toBeNull()
    })

    it('should call logout API when token exists', async () => {
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
      } as Response)

      await userStore.logout()

      expect(global.fetch).toHaveBeenCalledWith(
        expect.stringContaining('/api/auth/logout'),
        expect.objectContaining({
          method: 'POST',
          headers: expect.objectContaining({
            Authorization: 'Bearer test-token',
          }),
        })
      )
    })

    it('should handle logout API error gracefully', async () => {
      vi.mocked(global.fetch).mockRejectedValueOnce(new Error('Network error'))

      await expect(userStore.logout()).resolves.toBeUndefined()

      expect(userStore.token).toBe('')
      expect(userStore.userInfo).toBeNull()
    })
  })

  describe('fetchUserInfo', () => {
    beforeEach(() => {
      userStore.token = 'test-token'
      global.fetch = vi.fn()
    })

    it('should fetch and update user info', async () => {
      const mockResponse = {
        code: 200,
        data: {
          id: '1',
          username: 'testuser',
          email: 'test@example.com',
          nickname: 'Updated User',
          role: 'USER' as const,
          status: 'ACTIVE' as const,
          createdAt: '2024-01-01T00:00:00Z',
          updatedAt: '2024-01-01T00:00:00Z',
        },
      }

      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response)

      const result = await userStore.fetchUserInfo()

      expect(userStore.userInfo?.nickname).toBe('Updated User')
      expect(result).toEqual(mockResponse.data)
    })

    it('should return early if no token', async () => {
      userStore.token = ''

      const result = await userStore.fetchUserInfo()

      expect(result).toBeUndefined()
      expect(global.fetch).not.toHaveBeenCalled()
    })
  })

  describe('updateUserInfo', () => {
    beforeEach(() => {
      userStore.userInfo = {
        id: '1',
        username: 'testuser',
        email: 'test@example.com',
        role: 'USER' as const,
        status: 'ACTIVE' as const,
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
      }
    })

    it('should update user info with partial data', () => {
      userStore.updateUserInfo({ nickname: 'New Nickname', bio: 'Test bio' })

      expect(userStore.userInfo?.nickname).toBe('New Nickname')
      expect(userStore.userInfo?.bio).toBe('Test bio')
      expect(userStore.userInfo?.username).toBe('testuser')
    })

    it('should update localStorage', () => {
      userStore.updateUserInfo({ nickname: 'Updated' })

      const saved = localStorage.getItem('userInfo')
      expect(saved).not.toBeNull()
      expect(JSON.parse(saved!)).toEqual(
        expect.objectContaining({
          nickname: 'Updated',
        })
      )
    })

    it('should do nothing if userInfo is null', () => {
      userStore.userInfo = null

      expect(() => userStore.updateUserInfo({ nickname: 'Test' })).not.toThrow()
      expect(userStore.userInfo).toBeNull()
    })
  })

  describe('isAdmin', () => {
    it('should return true when user is ADMIN', () => {
      userStore.userInfo = {
        id: '1',
        username: 'admin',
        email: 'admin@example.com',
        role: 'ADMIN' as const,
        status: 'ACTIVE' as const,
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
      }
      expect(userStore.isAdmin).toBe(true)
    })

    it('should return false when user is not ADMIN', () => {
      userStore.userInfo = {
        id: '1',
        username: 'user',
        email: 'user@example.com',
        role: 'USER' as const,
        status: 'ACTIVE' as const,
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
      }
      expect(userStore.isAdmin).toBe(false)
    })

    it('should return false when userInfo is null', () => {
      userStore.userInfo = null
      expect(userStore.isAdmin).toBe(false)
    })
  })
})
