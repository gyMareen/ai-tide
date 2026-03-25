import router from '@/router'
import { useUserStore } from '@/stores/user'

// 白名单路由
const whiteList = ['/', '/search', '/category/:id', '/content/:id', '/login', '/register', '/agent/teams', '/agent/session/:id']

// 设置路由守卫
export function setupRouterGuards() {
  router.beforeEach((to, from, next) => {
    const userStore = useUserStore()
    const isAuthenticated = userStore.isAuthenticated
    const requiresAuth = to.meta.requiresAuth as boolean
    const hideForAuth = to.meta.hideForAuth as boolean

    // 如果路由需要认证但用户未登录
    if (requiresAuth && !isAuthenticated) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }

    // 如果路由隐藏给已登录用户（如登录页、注册页）
    if (hideForAuth && isAuthenticated) {
      next('/')
      return
    }

    // 如果是登录页或注册页且有 redirect 参数
    if ((to.path === '/login' || to.path === '/register') && isAuthenticated) {
      next('/')
      return
    }

    next()
  })

  router.afterEach((to) => {
    // 设置页面标题
    const title = to.meta.title as string
    if (title) {
      document.title = `${title} - AI-Tide`
    } else {
      document.title = 'AI-Tide - 发现改变世界的 AI 技术'
    }
  })
}
