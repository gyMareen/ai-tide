// 内容相关类型定义

export interface Category {
  id: string
  name: string
  description?: string
  icon?: string
  parentId?: string
  sort: number
  contentCount: number
  createdAt: string
  updatedAt: string
  children?: Category[]
}

export interface Tag {
  id: string
  name: string
  useCount: number
  createdAt: string
  updatedAt: string
}

export type ContentType = 'MODEL' | 'PRODUCT' | 'ARTICLE' | 'TUTORIAL' | 'RESOURCE'

export type ContentStatus = 'DRAFT' | 'PUBLISHED' | 'ARCHIVED'

export interface Content {
  id: string
  title: string
  description: string
  content?: string
  type: ContentType
  status: ContentStatus
  coverImage?: string
  categoryId?: string
  category?: Category
  tags: Tag[]
  authorId: string
  authorName?: string
  authorAvatar?: string
  viewCount: number
  likeCount: number
  favoriteCount: number
  commentCount: number
  averageRating: number
  ratingCount: number
  relatedLinks: RelatedLink[]
  publishTime?: string
  createdAt: string
  updatedAt: string
}

export interface ContentDetail extends Content {
  content: string
  author: {
    id: string
    username: string
    nickname?: string
    avatar?: string
  }
}

export interface RelatedLink {
  id: string
  title: string
  url: string
  description?: string
}

export interface CreateContentRequest {
  title: string
  description: string
  content: string
  type: ContentType
  coverImage?: string
  categoryId?: string
  tagIds?: string[]
  relatedLinks?: RelatedLink[]
}

export interface UpdateContentRequest extends Partial<CreateContentRequest> {
  id: string
}

export interface ContentListResponse {
  list: Content[]
  page: number
  pageSize: number
  total: number
  totalPages: number
}

export interface CategoryListResponse {
  list: Category[]
  total: number
}

export interface TagListResponse {
  list: Tag[]
  total: number
}
