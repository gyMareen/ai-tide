// 用户相关类型定义

export interface UserInfo {
  id: string
  username: string
  email: string
  nickname?: string
  avatar?: string
  bio?: string
  role: 'ADMIN' | 'USER'
  status: 'ACTIVE' | 'INACTIVE' | 'LOCKED'
  createdAt: string
  updatedAt: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  email: string
  password: string
  nickname?: string
}

export interface UpdateProfileRequest {
  nickname?: string
  bio?: string
  avatar?: string
}

export interface ChangePasswordRequest {
  oldPassword: string
  newPassword: string
}

export interface LoginResponse {
  token: string
  user: UserInfo
}

export interface RegisterResponse {
  token: string
  user: UserInfo
}
