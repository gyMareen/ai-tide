import { describe, it } from 'vitest'
import type { UserInfo, LoginRequest, RegisterRequest, UpdateProfileRequest, ChangePasswordRequest } from '@/types/user'

describe('user types', () => {
  describe('UserInfo interface', () => {
    it('should have required fields', () => {
      const userInfo: UserInfo = {
        id: '1',
        username: 'testuser',
        email: 'test@example.com',
        role: 'USER',
        status: 'ACTIVE',
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
      }

      expect(userInfo.id).toBe('1')
      expect(userInfo.username).toBe('testuser')
      expect(userInfo.email).toBe('test@example.com')
      expect(userInfo.role).toBe('USER')
      expect(userInfo.status).toBe('ACTIVE')
      expect(typeof userInfo.createdAt).toBe('string')
      expect(typeof userInfo.updatedAt).toBe('string')
    })

    it('should have optional fields', () => {
      const userInfo: UserInfo = {
        id: '1',
        username: 'testuser',
        email: 'test@example.com',
        nickname: 'Test User',
        avatar: 'avatar.jpg',
        bio: 'User bio',
        role: 'USER',
        status: 'ACTIVE',
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
      }

      expect(userInfo.nickname).toBe('Test User')
      expect(userInfo.avatar).toBe('avatar.jpg')
      expect(userInfo.bio).toBe('User bio')
    })

    it('should support ADMIN role', () => {
      const userInfo: UserInfo = {
        id: '1',
        username: 'admin',
        email: 'admin@example.com',
        role: 'ADMIN',
        status: 'ACTIVE',
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
      }

      expect(userInfo.role).toBe('ADMIN')
    })

    it('should support different statuses', () => {
      const statuses: Array<'ACTIVE' | 'INACTIVE' | 'LOCKED'> = ['ACTIVE', 'INACTIVE', 'LOCKED']
      statuses.forEach(status => {
        const userInfo: UserInfo = {
          id: '1',
          username: 'testuser',
          email: 'test@example.com',
          role: 'USER',
          status,
          createdAt: '2024-01-01T00:00:00Z',
          updatedAt: '2024-01-01T00:00:00Z',
        }

        expect(userInfo.status).toBe(status)
      })
    })
  })

  describe('LoginRequest interface', () => {
    it('should have required fields', () => {
      const loginRequest: LoginRequest = {
        username: 'testuser',
        password: 'password123',
      }

      expect(loginRequest.username).toBe('testuser')
      expect(loginRequest.password).toBe('password123')
    })
  })

  describe('RegisterRequest interface', () => {
    it('should have required fields', () => {
      const registerRequest: RegisterRequest = {
        username: 'newuser',
        email: 'new@example.com',
        password: 'password123',
      }

      expect(registerRequest.username).toBe('newuser')
      expect(registerRequest.email).toBe('new@example.com')
      expect(registerRequest.password).toBe('password123')
    })

    it('should have optional fields', () => {
      const registerRequest: RegisterRequest = {
        username: 'newuser',
        email: 'new@example.com',
        password: 'password123',
        nickname: 'New User',
      }

      expect(registerRequest.nickname).toBe('New User')
    })
  })

  describe('UpdateProfileRequest interface', () => {
    it('should have all optional fields', () => {
      const updateRequest: UpdateProfileRequest = {}

      expect(updateRequest.nickname).toBeUndefined()
      expect(updateRequest.bio).toBeUndefined()
      expect(updateRequest.avatar).toBeUndefined()
    })

    it('should allow partial updates', () => {
      const updateRequest1: UpdateProfileRequest = {
        nickname: 'Updated Nickname',
      }

      const updateRequest2: UpdateProfileRequest = {
        bio: 'Updated bio',
      }

      const updateRequest3: UpdateProfileRequest = {
        avatar: 'new-avatar.jpg',
      }

      expect(updateRequest1.nickname).toBe('Updated Nickname')
      expect(updateRequest2.bio).toBe('Updated bio')
      expect(updateRequest3.avatar).toBe('new-avatar.jpg')
    })
  })

  describe('ChangePasswordRequest interface', () => {
    it('should have required fields', () => {
      const changePasswordRequest: ChangePasswordRequest = {
        oldPassword: 'oldpassword123',
        newPassword: 'newpassword456',
      }

      expect(changePasswordRequest.oldPassword).toBe('oldpassword123')
      expect(changePasswordRequest.newPassword).toBe('newpassword456')
    })
  })
})
