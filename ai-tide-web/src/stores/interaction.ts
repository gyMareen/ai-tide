import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useInteractionStore = defineStore('interaction', () => {
  // 本地状态
  const likedContents = ref<Set<string>>(new Set())
  const favoriteContents = ref<Set<string>>(new Set())
  const userRatings = ref<Map<string, number>>(new Map())
  const isLoading = ref(false)

  // 获取令牌
  function getToken() {
    return localStorage.getItem('token') || ''
  }

  // 点赞
  async function toggleLike(contentId: string) {
    const token = getToken()
    if (!token) {
      throw new Error('请先登录')
    }

    isLoading.value = true
    try {
      const isLiked = likedContents.value.has(contentId)
      const method = isLiked ? 'DELETE' : 'POST'

      const response = await fetch('/api/interaction/like', {
        method,
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ contentId }),
      })

      if (!response.ok) {
        throw new Error('操作失败')
      }

      const data = await response.json()

      if (data.code === 200) {
        if (isLiked) {
          likedContents.value.delete(contentId)
        } else {
          likedContents.value.add(contentId)
        }
        return !isLiked
      } else {
        throw new Error(data.message || '操作失败')
      }
    } finally {
      isLoading.value = false
    }
  }

  // 收藏
  async function toggleFavorite(contentId: string) {
    const token = getToken()
    if (!token) {
      throw new Error('请先登录')
    }

    isLoading.value = true
    try {
      const isFavorited = favoriteContents.value.has(contentId)
      const method = isFavorited ? 'DELETE' : 'POST'

      const response = await fetch('/api/interaction/favorite', {
        method,
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ contentId }),
      })

      if (!response.ok) {
        throw new Error('操作失败')
      }

      const data = await response.json()

      if (data.code === 200) {
        if (isFavorited) {
          favoriteContents.value.delete(contentId)
        } else {
          favoriteContents.value.add(contentId)
        }
        return !isFavorited
      } else {
        throw new Error(data.message || '操作失败')
      }
    } finally {
      isLoading.value = false
    }
  }

  // 评分
  async function rateContent(contentId: string, rating: number) {
    const token = getToken()
    if (!token) {
      throw new Error('请先登录')
    }

    isLoading.value = true
    try {
      const response = await fetch('/api/interaction/rating', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ contentId, rating }),
      })

      if (!response.ok) {
        throw new Error('评分失败')
      }

      const data = await response.json()

      if (data.code === 200) {
        userRatings.value.set(contentId, rating)
        return data.data
      } else {
        throw new Error(data.message || '评分失败')
      }
    } finally {
      isLoading.value = false
    }
  }

  // 提交评论
  async function submitComment(contentId: string, comment: string, parentId?: string) {
    const token = getToken()
    if (!token) {
      throw new Error('请先登录')
    }

    isLoading.value = true
    try {
      const response = await fetch('/api/interaction/comment', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ contentId, content: comment, parentId }),
      })

      if (!response.ok) {
        throw new Error('评论失败')
      }

      const data = await response.json()

      if (data.code === 200) {
        return data.data
      } else {
        throw new Error(data.message || '评论失败')
      }
    } finally {
      isLoading.value = false
    }
  }

  // 删除评论
  async function deleteComment(commentId: string) {
    const token = getToken()
    if (!token) {
      throw new Error('请先登录')
    }

    isLoading.value = true
    try {
      const response = await fetch(`/api/interaction/comment/${commentId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      })

      if (!response.ok) {
        throw new Error('删除评论失败')
      }

      const data = await response.json()

      if (data.code === 200) {
        return true
      } else {
        throw new Error(data.message || '删除评论失败')
      }
    } finally {
      isLoading.value = false
    }
  }

  // 获取用户的点赞列表
  async function fetchUserLikes() {
    const token = getToken()
    if (!token) return

    try {
      const response = await fetch('/api/interaction/likes', {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      })

      if (!response.ok) {
        throw new Error('获取点赞列表失败')
      }

      const data = await response.json()

      if (data.code === 200 && data.data) {
        likedContents.value = new Set(data.data.map((item: any) => item.contentId))
      }
    } catch (e) {
      console.error('Failed to fetch user likes:', e)
    }
  }

  // 获取用户的收藏列表
  async function fetchUserFavorites() {
    const token = getToken()
    if (!token) return

    try {
      const response = await fetch('/api/interaction/favorites', {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      })

      if (!response.ok) {
        throw new Error('获取收藏列表失败')
      }

      const data = await response.json()

      if (data.code === 200 && data.data) {
        favoriteContents.value = new Set(data.data.map((item: any) => item.contentId))
      }
    } catch (e) {
      console.error('Failed to fetch user favorites:', e)
    }
  }

  return {
    likedContents,
    favoriteContents,
    userRatings,
    isLoading,
    toggleLike,
    toggleFavorite,
    rateContent,
    submitComment,
    deleteComment,
    fetchUserLikes,
    fetchUserFavorites,
  }
})
