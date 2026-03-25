import { describe, it } from 'vitest'
import type { ApiResponse, PaginationResponse, ErrorResponse, ValidationError } from '@/types/api'

describe('API types', () => {
  describe('ApiResponse', () => {
    it('should have correct structure', () => {
      const response: ApiResponse<string> = {
        code: 200,
        message: 'Success',
        data: 'test data',
      }

      expect(response.code).toBe(200)
      expect(response.message).toBe('Success')
      expect(response.data).toBe('test data')
    })

    it('should allow any data type', () => {
      const response: ApiResponse = {
        code: 200,
        message: 'Success',
        data: { nested: { value: 'test' } },
      }

      expect(response.data).toEqual({ nested: { value: 'test' } })
    })
  })

  describe('PaginationResponse', () => {
    it('should have correct structure', () => {
      const response: PaginationResponse<string> = {
        list: ['item1', 'item2'],
        page: 1,
        pageSize: 20,
        total: 100,
        totalPages: 5,
      }

      expect(response.list).toHaveLength(2)
      expect(response.page).toBe(1)
      expect(response.pageSize).toBe(20)
      expect(response.total).toBe(100)
      expect(response.totalPages).toBe(5)
    })

    it('should handle empty list', () => {
      const response: PaginationResponse<string> = {
        list: [],
        page: 1,
        pageSize: 20,
        total: 0,
        totalPages: 0,
      }

      expect(response.list).toHaveLength(0)
      expect(response.total).toBe(0)
    })
  })

  describe('ErrorResponse', () => {
    it('should have correct structure', () => {
      const error: ErrorResponse = {
        code: 400,
        message: 'Bad Request',
        errors: [
          {
            field: 'username',
            message: 'Username is required',
          },
        ],
      }

      expect(error.code).toBe(400)
      expect(error.message).toBe('Bad Request')
      expect(error.errors).toBeDefined()
      expect(error.errors).toHaveLength(1)
      expect(error.errors[0].field).toBe('username')
    })

    it('should work without errors array', () => {
      const error: ErrorResponse = {
        code: 500,
        message: 'Server Error',
      }

      expect(error.code).toBe(500)
      expect(error.message).toBe('Server Error')
      expect(error.errors).toBeUndefined()
    })
  })

  describe('ValidationError', () => {
    it('should have correct structure', () => {
      const validationError: ValidationError = {
        field: 'email',
        message: 'Invalid email format',
      }

      expect(validationError.field).toBe('email')
      expect(validationError.message).toBe('Invalid email format')
    })
  })
})
