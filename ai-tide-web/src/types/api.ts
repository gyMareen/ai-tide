// API 通用响应类型

export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export interface PaginationResponse<T = any> {
  list: T[]
  page: number
  pageSize: number
  total: number
  totalPages: number
}

export interface ErrorResponse {
  code: number
  message: string
  errors?: ValidationError[]
}

export interface ValidationError {
  field: string
  message: string
}
