import { describe, it, expect, beforeEach, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useInteractionStore } from '@/stores/interaction'

describe('interactionStore', () => {
  let interactionStore: ReturnType<typeof useInteractionStore>

  beforeEach(() => {
    setActivePinia(createPinia())
    interactionStore = useInteractionStore()
    localStorage.clear()
    vi.clearAllMocks()
  })

  describe('initial state', () => {
    it('should have empty likedContents set', () => {
      expect(interactionStore.likedContents).toBeInstanceOf(Set)
      expect(interactionStore.likedContents.size).toBe(0)
    })

    it('should have empty favoriteContents set', () => {
      expect(interactionStore.favoriteContents).toBeInstanceOf(Set)
      expect(interactionStore.favoriteContents.size).toBe(0)
    })

    it('should have empty userRatings map', () => {
      expect(interactionStore.userRatings).toBeInstanceOf(Map)
      expect(interactionStore.userRatings.size).toBe(0)
    })

    it('should have isLoading as false', () => {
      expect(interactionStore.isLoading).toBe(false)
    })
  })

  describe('toggleLike', () => {
    beforeEach(() => {
      localStorage.setItem('token', 'test-token')
      global.fetch = vi.fn()
    })

    it('should throw error when not logged in', async () => {
      localStorage.removeItem('token')
      await expect(interactionStore.toggleLike('content1')).rejects.toThrow('请先登录')
    })

    it('should add like when not previously liked', async () => {
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ code: 200 }),
      } as Response)

      const result = await interactionStore.toggleLike('content1')

      expect(interactionStore.likedContents.has('content1')).toBe(true)
      expect(result).toBe(true)
      expect(global.fetch).toHaveBeenCalledWith(
        expect.stringContaining('/api/interaction/like'),
        expect.objectContaining({
          method: 'POST',
        })
      )
    })

    it('should remove like when previously liked', async () => {
      interactionStore.likedContents.add('content1')
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ code: 200 }),
      } as Response)

      const result = await interactionStore.toggleLike('content1')

      expect(interactionStore.likedContents.has('content1')).toBe(false)
      expect(result).toBe(false)
      expect(global.fetch).toHaveBeenCalledWith(
        expect.stringContaining('/api/interaction/like'),
        expect.objectContaining({
          method: 'DELETE',
        })
      )
    })

    it('should handle API errors', async () => {
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: false,
      } as Response)

      await expect(interactionStore.toggleLike('content1')).rejects.toThrow('操作失败')
    })

    it('should reset loading state', async () => {
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ code: 200 }),
      } as Response)

      const promise = interactionStore.toggleLike('content1')
      expect(interactionStore.isLoading).toBe(true)
      await promise
      expect(interactionStore.isLoading).toBe(false)
    })
  })

  describe('toggleFavorite', () => {
    beforeEach(() => {
      localStorage.setItem('token', 'test-token')
      global.fetch = vi.fn()
    })

    it('should throw error when not logged in', async () => {
      localStorage.removeItem('token')
      await expect(interactionStore.toggleFavorite('content1')).rejects.toThrow('请先登录')
    })

    it('should add favorite when not previously favorited', async () => {
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ code: 200 }),
      } as Response)

      const result = await interactionStore.toggleFavorite('content1')

      expect(interactionStore.favoriteContents.has('content1')).toBe(true)
      expect(result).toBe(true)
    })

    it('should remove favorite when previously favorited', async () => {
      interactionStore.favoriteContents.add('content1')
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ code: 200 }),
      } as Response)

      const result = await interactionStore.toggleFavorite('content1')

      expect(interactionStore.favoriteContents.has('content1')).toBe(false)
      expect(result).toBe(false)
    })
  })

  describe('rateContent', () => {
    beforeEach(() => {
      localStorage.setItem('token', 'test-token')
      global.fetch = vi.fn()
    })

    it('should throw error when not logged in', async () => {
      localStorage.removeItem('token')
      await expect(interactionStore.rateContent('content1', 5)).rejects.toThrow('请先登录')
    })

    it('should rate content successfully', async () => {
      const mockData = { rating: 5, averageRating: 4.8 }
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ code: 200, data: mockData }),
      } as Response)

      const result = await interactionStore.rateContent('content1', 5)

      expect(interactionStore.userRatings.get('content1')).toBe(5)
      expect(result).toEqual(mockData)
    })

    it('should update existing rating', async () => {
      interactionStore.userRatings.set('content1', 3)
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ code: 200, data: { rating: 5 } }),
      } as Response)

      await interactionStore.rateContent('content1', 5)

      expect(interactionStore.userRatings.get('content1')).toBe(5)
    })
  })

  describe('submitComment', () => {
    beforeEach(() => {
      localStorage.setItem('token', 'test-token')
      global.fetch = vi.fn()
    })

    it('should throw error when not logged in', async () => {
      localStorage.removeItem('token')
      await expect(interactionStore.submitComment('content1', '测试评论')).rejects.toThrow('请先登录')
    })

    it('should submit comment successfully', async () => {
      const mockComment = {
        id: 'comment1',
        contentId: 'content1',
        content: '测试评论',
      }
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ code: 200, data: mockComment }),
      } as Response)

      const result = await interactionStore.submitComment('content1', '测试评论')

      expect(result).toEqual(mockComment)
    })

    it('should submit reply comment', async () => {
      const mockReply = {
        id: 'reply1',
        parentId: 'parent1',
        content: '回复内容',
      }
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ code: 200, data: mockReply }),
      } as Response)

      const result = await interactionStore.submitComment('content1', '回复内容', 'parent1')

      expect(result).toEqual(mockReply)
    })
  })

  describe('deleteComment', () => {
    beforeEach(() => {
      localStorage.setItem('token', 'test-token')
      global.fetch = vi.fn()
    })

    it('should throw error when not logged in', async () => {
      localStorage.removeItem('token')
      await expect(interactionStore.deleteComment('comment1')).rejects.toThrow('请先登录')
    })

    it('should delete comment successfully', async () => {
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ code: 200 }),
      } as Response)

      const result = await interactionStore.deleteComment('comment1')

      expect(result).toBe(true)
      expect(global.fetch).toHaveBeenCalledWith(
        expect.stringContaining('/api/interaction/comment/comment1'),
        expect.objectContaining({
          method: 'DELETE',
        })
      )
    })
  })

  describe('fetchUserLikes', () => {
    beforeEach(() => {
      global.fetch = vi.fn()
    })

    it('should return early when not logged in', async () => {
      const result = await interactionStore.fetchUserLikes()
      expect(result).toBeUndefined()
      expect(global.fetch).not.toHaveBeenCalled()
    })

    it('should fetch and populate liked contents', async () => {
      localStorage.setItem('token', 'test-token')
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => ({
          code: 200,
          data: [
            { contentId: 'content1' },
            { contentId: 'content2' },
          ],
        }),
      } as Response)

      await interactionStore.fetchUserLikes()

      expect(interactionStore.likedContents.has('content1')).toBe(true)
      expect(interactionStore.likedContents.has('content2')).toBe(true)
    })

    it('should handle errors gracefully', async () => {
      localStorage.setItem('token', 'test-token')
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: false,
      } as Response)

      const result = await interactionStore.fetchUserLikes()
      expect(result).toBeUndefined()
    })
  })

  describe('fetchUserFavorites', () => {
    beforeEach(() => {
      global.fetch = vi.fn()
    })

    it('should return early when not logged in', async () => {
      const result = await interactionStore.fetchUserFavorites()
      expect(result).toBeUndefined()
      expect(global.fetch).not.toHaveBeenCalled()
    })

    it('should fetch and populate favorite contents', async () => {
      localStorage.setItem('token', 'test-token')
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => ({
          code: 200,
          data: [
            { contentId: 'content1' },
            { contentId: 'content2' },
          ],
        }),
      } as Response)

      await interactionStore.fetchUserFavorites()

      expect(interactionStore.favoriteContents.has('content1')).toBe(true)
      expect(interactionStore.favoriteContents.has('content2')).toBe(true)
    })
  })
})
