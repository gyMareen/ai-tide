import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import { get, post, put, del, upload } from '@/utils/request'

describe('request utilities', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
    vi.stubGlobal('window', 'location', {
      value: {
        pathname: '/test',
        search: '?param=value',
        href: '',
      },
      writable: true,
    })
  })

  afterEach(() => {
    vi.unstubAllGlobals()
  })

  describe('get', () => {
    beforeEach(() => {
      global.fetch = vi.fn()
    })

    it('should make GET request with url', async () => {
      const mockResponse = {
        code: 200,
        message: 'Success',
        data: { id: 1 },
      }

      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response)

      const result = await get('/test')

      expect(global.fetch).toHaveBeenCalledWith(
        '/api/test',
        expect.objectContaining({
          method: 'GET',
          headers: expect.objectContaining({
            'Content-Type': 'application/json',
          }),
        }),
      )
      expect(result).toEqual(mockResponse)
    })

    it('should append query params to url', async () => {
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ code: 200 }),
      } as Response)

      await get('/test', { page: '1', limit: '10' })

      expect(global.fetch).toHaveBeenCalledWith(
        expect.stringContaining('page=1'),
        expect.anything()
      )
    })

    it('should throw error on failed response', async () => {
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: false,
        status: 404,
        statusText: 'Not Found',
        json: async () => ({ message: 'Not Found', code: 404 }),
      } as Response)

      await expect(get('/test')).rejects.toEqual({
        code: 404,
        message: 'Not Found',
        data: expect.anything(),
      })
    })

    it('should handle 401 and redirect to login', async () => {
      vi.stubGlobal('window', 'location', {
        value: {
          pathname: '/test',
          search: '',
          href: '',
        },
        writable: true,
      })

      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: false,
        status: 401,
        json: async () => ({ code: 401 }),
      } as Response)

      await expect(get('/test')).rejects.toThrow()

      expect(localStorage.getItem('token')).toBeNull()
      expect(localStorage.getItem('userInfo')).toBeNull()
    })

    it('should add authorization header when token exists', async () => {
      localStorage.setItem('token', 'test-token')
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ code: 200 }),
      } as Response)

      await get('/test')

      expect(global.fetch).toHaveBeenCalledWith(
        expect.anything(),
        expect.objectContaining({
          headers: expect.objectContaining({
            Authorization: 'Bearer test-token',
          }),
        })
      )
    })

    it('should throw error on non-200 code', async () => {
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ code: 400, message: 'Bad Request' }),
      } as Response)

      await expect(get('/test')).rejects.toEqual({
        code: 400,
        message: 'Bad Request',
        data: { code: 400, message: 'Bad Request' },
      })
    })

    it('should throw error on network failure', async () => {
      vi.mocked(global.fetch).mockRejectedValueOnce(
        new TypeError('Failed to fetch')
      )

      await expect(get('/test')).rejects.toEqual({
        code: 0,
        message: '网络连接失败，请检查网络设置',
      })
    })
  })

  describe('post', () => {
    beforeEach(() => {
      global.fetch = vi.fn()
    })

    it('should make POST request with data', async () => {
      const mockResponse = {
        code: 200,
        message: 'Success',
        data: { id: 1 },
      }

      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response)

      const result = await post('/test', { name: 'Test' })

      expect(global.fetch).toHaveBeenCalledWith(
        '/api/test',
        expect.objectContaining({
          method: 'POST',
          body: JSON.stringify({ name: 'Test' }),
          headers: expect.objectContaining({
            'Content-Type': 'application/json',
          }),
        })
      )
      expect(result).toEqual(mockResponse)
    })

    it('should make POST request without data', async () => {
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ code: 200 }),
      } as Response)

      await post('/test')

      expect(global.fetch).toHaveBeenCalledWith(
        '/api/test',
        expect.objectContaining({
          body: undefined,
        }),
      )
    })
  })

  describe('put', () => {
    beforeEach(() => {
      global.fetch = vi.fn()
    })

    it('should make PUT request with data', async () => {
      const mockResponse = { code: 200, data: { id: 1 } }
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response)

      const result = await put('/test/1', { name: 'Updated' })

      expect(global.fetch).toHaveBeenCalledWith(
        '/api/test/1',
        expect.objectContaining({
          method: 'PUT',
          body: JSON.stringify({ name: 'Updated' }),
        }),
      )
      expect(result).toEqual(mockResponse)
    })
  })

  describe('delete', () => {
    beforeEach(() => {
      global.fetch = vi.fn()
    })

    it('should make DELETE request', async () => {
      const mockResponse = { code: 200, data: { success: true } }
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response)

      const result = await del('/test/1')

      expect(global.fetch).toHaveBeenCalledWith(
        '/api/test/1',
        expect.objectContaining({
          method: 'DELETE',
        }),
      )
      expect(result).toEqual(mockResponse)
    })
  })

  describe('upload', () => {
    // Note: XMLHttpRequest mocking in happy-dom requires more complex setup
    // These tests are skipped for now
    it.skip('should upload file with progress callback', async () => {
      // Test implementation would go here
    })

    it.skip('should add authorization header to upload', async () => {
      // Test implementation would go here
    })

    it.skip('should handle upload error', async () => {
      // Test implementation would go here
    })
  })
})
