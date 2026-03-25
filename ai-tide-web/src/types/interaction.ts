// 交互相关类型定义

export interface Comment {
  id: string
  content: string
  contentId: string
  userId: string
  username?: string
  userAvatar?: string
  parentId?: string
  parentComment?: Comment
  replies?: Comment[]
  createdAt: string
  updatedAt: string
}

export interface Like {
  id: string
  contentId: string
  userId: string
  createdAt: string
}

export interface Favorite {
  id: string
  contentId: string
  userId: string
  createdAt: string
}

export interface Rating {
  id: string
  contentId: string
  userId: string
  rating: number
  createdAt: string
  updatedAt: string
}

export interface CommentRequest {
  contentId: string
  content: string
  parentId?: string
}

export interface RatingRequest {
  contentId: string
  rating: number
}

export interface CommentListResponse {
  list: Comment[]
  total: number
}

export interface LikeListResponse {
  list: Like[]
  total: number
}

export interface FavoriteListResponse {
  list: Favorite[]
  total: number
}
