import { describe, it, expect, beforeEach, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useContentStore } from '@/stores/content'

describe('contentStore', () => {
  let contentStore: ReturnType<typeof useContentStore>

  beforeEach(() => {
    setActivePinia(createPinia())
    contentStore = useContentStore()
    vi.clearAllMocks()
  })

  describe('initial state', () => {
    it('should have empty contents array', () => {
      expect(contentStore.contents).toEqual([])
    })

    it('should have null currentContent', () => {
      expect(contentStore.currentContent).toBeNull()
    })

    it('should have empty categories array', () => {
      expect(contentStore.categories).toEqual([])
    })

    it('should have empty tags array', () => {
      expect(contentStore.tags).toEqual([])
    })

    it('should have isLoading as false', () => {
      expect(contentStore.isLoading).toBe(false)
    })

    it('should have default pagination values', () => {
      expect(contentStore.pagination).toEqual({
        page: 1,
        pageSize: 20,
        total: 0,
        totalPages: 0,
      })
    })
  })

  describe('fetchContents', () => {
    beforeEach(() => {
      global.fetch = vi.fn()
    })

    it('should set loading state', async () => {
      const mockResponse = {
        code: 200,
        data: {
          list: [],
          page: 1,
          pageSize: 20,
          total: 0,
          totalPages: 0,
        },
      }

      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response)

      const fetchPromise = contentStore.fetchContents()
      expect(contentStore.isLoading).toBe(true)
      await fetchPromise
      expect(contentStore.isLoading).toBe(false)
    })

    it('should successfully fetch and update contents', async () => {
      const mockResponse = {
        code: 200,
        data: {
          list: [
            {
              id: '1',
              title: 'Test Content',
              description: 'Test description',
              type: 'MODEL' as const,
              categoryId: 'cat1',
              tags: [],
              viewCount: 100,
              likeCount: 10,
              averageRating: 4.5,
              coverImage: 'image.jpg',
              publishTime: '2024-01-01T00:00:00Z',
            },
          ],
          page: 1,
          pageSize: 20,
          total: 1,
          totalPages: 1,
        },
      }

      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response)

      const result = await contentStore.fetchContents()

      expect(contentStore.contents).toHaveLength(1)
      expect(contentStore.contents[0].title).toBe('Test Content')
      expect(contentStore.pagination.total).toBe(1)
      expect(result).toEqual(mockResponse.data)
    })

    it('should build correct query params', async () => {
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => ({
          code: 200,
          data: {
            list: [],
            page: 1,
            pageSize: 20,
            total: 0,
            totalPages: 0,
          },
        }),
      } as Response)

      await contentStore.fetchContents({
        page: 2,
        pageSize: 10,
        categoryId: 'cat1',
        tagId: 'tag1',
        type: 'MODEL',
        sortBy: 'latest',
      })

      expect(global.fetch).toHaveBeenCalled()
      const fetchCall = vi.mocked(global.fetch).mock.calls[0]
      expect(fetchCall[0]).toContain('page=2')
      expect(fetchCall[0]).toContain('pageSize=10')
      expect(fetchCall[0]).toContain('categoryId=cat1')
      expect(fetchCall[0]).toContain('tagId=tag1')
      expect(fetchCall[0]).toContain('type=MODEL')
      expect(fetchCall[0]).toContain('sortBy=latest')
    })

    it('should handle fetch errors', async () => {
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: false,
      } as Response)

      await expect(contentStore.fetchContents()).rejects.toThrow('获取内容列表失败')
    })
  })

  describe('fetchContentDetail', () => {
    beforeEach(() => {
      global.fetch = vi.fn()
    })

    it('should successfully fetch content detail', async () => {
      const mockResponse = {
        code: 200,
        data: {
          id: '1',
          title: 'Test Content',
          description: 'Test description',
          type: 'MODEL' as const,
          categoryId: 'cat1',
          tags: [],
          viewCount: 100,
          likeCount: 10,
          averageRating: 4.5,
          coverImage: 'image.jpg',
          publishTime: '2024-01-01T00:00:00Z',
        },
      }

      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response)

      const result = await contentStore.fetchContentDetail('1')

      expect(contentStore.currentContent).toEqual(mockResponse.data)
      expect(result).toEqual(mockResponse.data)
    })

    it('should handle fetch errors', async () => {
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: false,
      } as Response)

      await expect(contentStore.fetchContentDetail('1')).rejects.toThrow('获取内容详情失败')
    })
  })

  describe('fetchCategories', () => {
    beforeEach(() => {
      global.fetch = vi.fn()
    })

    it('should successfully fetch categories', async () => {
      const mockResponse = {
        code: 200,
        data: [
          {
            id: 'cat1',
            name: 'Category 1',
            icon: '🔥',
            description: 'Test category',
            contentCount: 100,
          },
        ],
      }

      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response)

      const result = await contentStore.fetchCategories()

      expect(contentStore.categories).toEqual(mockResponse.data)
      expect(result).toEqual(mockResponse.data)
    })

    it('should handle fetch errors gracefully', async () => {
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: false,
      } as Response)

      const result = await contentStore.fetchCategories()
      expect(result).toBeUndefined()
    })
  })

  describe('fetchTags', () => {
    beforeEach(() => {
      global.fetch = vi.fn()
    })

    it('should successfully successfully fetch tags', async () => {
      const mockResponse = {
        code: 200,
        data: [
          {
            id: 'tag1',
            name: 'Tag 1',
            contentCount: 50,
          },
        ],
      }

      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response)

      const result = await contentStore.fetchTags()

      expect(contentStore.tags).toEqual(mockResponse.data)
      expect(result).toEqual(mockResponse.data)
    })

    it('should handle fetch errors gracefully', async () => {
      vi.mocked(global.fetch).mockResolvedValueOnce({
        ok: false,
      } as Response)

      const result = await contentStore.fetchTags()
      expect(result).toBeUndefined()
    })
  })

  describe('clearCurrentContent', () => {
    it('should clear current content', () => {
      contentStore.currentContent = {
        id: '1',
        title: 'Test',
        description: 'Test',
        type: 'MODEL' as const,
        categoryId: 'cat1',
        tags: [],
        viewCount: 0,
        likeCount: 0,
        averageRating: 0,
        coverImage: '',
        publishTime: '2024-01-01T00:00:00Z',
      }

      contentStore.clearCurrentContent()

      expect(contentStore.currentContent).toBeNull()
    })
  })
})
