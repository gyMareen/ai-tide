// API 请求工具

import type { ApiResponse } from '@/types/api'

const BASE_URL = '/api'

// 获取认证令牌
function getToken(): string | null {
  return localStorage.getItem('token')
}

// 请求拦截器
async function request<T = any>(
  url: string,
  options: RequestInit = {}
): Promise<ApiResponse<T>> {
  const token = getToken()

  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string>),
  }

  // 添加认证令牌
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }

  const config: RequestInit = {
    ...options,
    headers,
  }

  try {
    const response = await fetch(`${BASE_URL}${url}`, config)

    // 处理 HTTP 错误
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({
        message: response.statusText || '请求失败',
        code: response.status,
      }))

      // 401 未授权，清除本地存储并跳转到登录页
      if (response.status === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')

        if (window.location.pathname !== '/login') {
          window.location.href = `/login?redirect=${encodeURIComponent(window.location.pathname + window.location.search)}`
        }
      }

      throw {
        code: response.status,
        message: errorData.message || '请求失败',
        data: errorData,
      }
    }

    const data = await response.json()

    // 处理业务错误
    if (data.code !== 200) {
      throw {
        code: data.code,
        message: data.message || '请求失败',
        data,
      }
    }

    return data
  } catch (error) {
    // 网络错误
    if (error instanceof TypeError && error.message.includes('Failed to fetch')) {
      throw {
        code: 0,
        message: '网络连接失败，请检查网络设置',
      }
    }

    throw error
  }
}

// GET 请求
export function get<T = any>(url: string, params?: Record<string, any>): Promise<ApiResponse<T>> {
  const query = params ? `?${new URLSearchParams(params).toString()}` : ''
  return request<T>(`${url}${query}`, { method: 'GET' })
}

// POST 请求
export function post<T = any>(url: string, data?: any): Promise<ApiResponse<T>> {
  return request<T>(url, {
    method: 'POST',
    body: data ? JSON.stringify(data) : undefined,
  })
}

// PUT 请求
export function put<T = any>(url: string, data?: any): Promise<ApiResponse<T>> {
  return request<T>(url, {
    method: 'PUT',
    body: data ? JSON.stringify(data) : undefined,
  })
}

// DELETE 请求
export function del<T = any>(url: string): Promise<ApiResponse<T>> {
  return request<T>(url, { method: 'DELETE' })
}

// 文件上传
export async function upload<T = any>(
  url: string,
  file: File,
  onProgress?: (progress: number) => void
): Promise<ApiResponse<T>> {
  const token = getToken()
  const formData = new FormData()
  formData.append('file', file)

  return new Promise((resolve, reject) => {
    const xhr = new XMLHttpRequest()

    if (token) {
      xhr.setRequestHeader('Authorization', `Bearer ${token}`)
    }

    // 上传进度
    if (onProgress) {
      xhr.upload.addEventListener('progress', (e) => {
        if (e.lengthComputable) {
          const progress = (e.loaded / e.total) * 100
          onProgress(progress)
        }
      })
    }

    xhr.addEventListener('load', () => {
      try {
        const response = JSON.parse(xhr.responseText)

        if (response.code === 200) {
          resolve(response)
        } else {
          reject({
            code: response.code,
            message: response.message || '上传失败',
          })
        }
      } catch (error) {
        reject({
          code: 0,
          message: '上传失败',
        })
      }
    })

    xhr.addEventListener('error', () => {
      reject({
        code: 0,
        message: '上传失败',
      })
    })

    xhr.open('POST', `${BASE_URL}${url}`)
    xhr.send(formData)
  })
}

export default {
  get,
  post,
  put,
  delete: del,
  upload,
}
