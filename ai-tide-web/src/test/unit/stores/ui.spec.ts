import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useUiStore } from '@/stores/ui'

describe('uiStore', () => {
  let uiStore: ReturnType<typeof useUiStore>

  beforeEach(() => {
    setActivePinia(createPinia())
    uiStore = useUiStore()
    vi.clearAllMocks()
    vi.useFakeTimers()
    document.body.className = ''
    localStorage.clear()
  })

  afterEach(() => {
    vi.runOnlyPendingTimers()
    vi.useRealTimers()
  })

  describe('initial state', () => {
    it('should have dark theme by default', () => {
      expect(uiStore.theme).toBe('dark')
    })

    it('should have sidebarOpen as false', () => {
      expect(uiStore.sidebarOpen).toBe(false)
    })

    it('should have loading as false', () => {
      expect(uiStore.loading).toBe(false)
    })

    it('should have empty loadingText', () => {
      expect(uiStore.loadingText).toBe('')
    })

    it('should have empty toastMessage', () => {
      expect(uiStore.toastMessage).toBe('')
    })

    it('should have toastVisible as false', () => {
      expect(uiStore.toastVisible).toBe(false)
    })

    it('should have toastType as info', () => {
      expect(uiStore.toastType).toBe('info')
    })
  })

    describe('initTheme', () => {
    it('should load theme from localStorage', () => {
      localStorage.setItem('theme', 'light')
      uiStore.initTheme()
      expect(uiStore.theme).toBe('light')
      expect(document.body.classList.contains('light')).toBe(true)
    })

    it('should use dark theme as default when not in localStorage', () => {
      uiStore.initTheme()
      expect(uiStore.theme).toBe('dark')
      expect(document.body.classList.contains('dark')).toBe(true)
    })

    it('should remove previous theme class from body', () => {
      document.body.classList.add('light')
      localStorage.setItem('theme', 'dark')
      uiStore.initTheme()
      expect(document.body.classList.contains('light')).toBe(false)
      expect(document.body.classList.contains('dark')).toBe(true)
    })
  })

    describe('applyTheme', () => {
    it('should apply dark theme', () => {
      uiStore.applyTheme('dark')
      expect(uiStore.theme).toBe('dark')
      expect(localStorage.getItem('theme')).toBe('dark')
      expect(document.body.classList.contains('dark')).toBe(true)
      expect(document.body.classList.contains('light')).toBe(false)
    })

    it('should apply light theme', () => {
      uiStore.applyTheme('light')
      expect(uiStore.theme).toBe('light')
      expect(localStorage.getItem('theme')).toBe('light')
      expect(document.body.classList.contains('light')).toBe(true)
      expect(document.body.classList.contains('dark')).toBe(false)
    })

    it('should remove previous theme class', () => {
      document.body.classList.add('light')
      uiStore.applyTheme('dark')
      expect(document.body.classList.contains('light')).toBe(false)
    })
  })

    describe('toggleTheme', () => {
    it('should toggle from dark to light', () => {
      uiStore.theme = 'dark'
      uiStore.toggleTheme()
      expect(uiStore.theme).toBe('light')
    })

    it('should toggle from light to dark', () => {
      uiStore.theme = 'light'
      uiStore.toggleTheme()
      expect(uiStore.theme).toBe('dark')
    })
  })

    describe('sidebar controls', () => {
    it('should toggle sidebar', () => {
      uiStore.sidebarOpen = false
      uiStore.toggleSidebar()
      expect(uiStore.sidebarOpen).toBe(true)

      uiStore.toggleSidebar()
      expect(uiStore.sidebarOpen).toBe(false)
    })

    it('should open sidebar', () => {
      uiStore.sidebarOpen = false
      uiStore.openSidebar()
      expect(uiStore.sidebarOpen).toBe(true)
    })

    it('should close sidebar', () => {
      uiStore.sidebarOpen = true
      uiStore.closeSidebar()
      expect(uiStore.sidebarOpen).toBe(false)
    })
  })

    describe('loading controls', () => {
    it('should show loading with text', () => {
      uiStore.showLoading('正在加载...')
      expect(uiStore.loading).toBe(true)
      expect(uiStore.loadingText).toBe('正在加载...')
    })

    it('should show loading with default text', () => {
      uiStore.showLoading()
      expect(uiStore.loadingText).toBe('加载中...')
    })

    it('should hide loading', () => {
      uiStore.loading = true
      uiStore.loadingText = '测试'
      uiStore.hideLoading()
      expect(uiStore.loading).toBe(false)
      expect(uiStore.loadingText).toBe('')
    })
  })

    describe('toast controls', () => {
    it('should show toast with default type', () => {
      uiStore.showToast('测试消息')
      expect(uiStore.toastMessage).toBe('测试消息')
      expect(uiStore.toastType).toBe('info')
      expect(uiStore.toastVisible).toBe(true)
    })

    it('should show toast with custom type', () => {
      uiStore.showToast('错误消息', 'error')
      expect(uiStore.toastMessage).toBe('错误消息')
      expect(uiStore.toastType).toBe('error')
    })

    it('should hide toast', () => {
      uiStore.toastVisible = true
      uiStore.hideToast()
      expect(uiStore.toastVisible).toBe(false)
    })

    it('should auto hide toast after duration', () => {
      uiStore.showToast('自动隐藏', 'success', 3000)

      vi.advanceTimersByTime(2000)
      expect(uiStore.toastVisible).toBe(true)

      vi.advanceTimersByTime(1000)
      vi.runAllTimers()
      expect(uiStore.toastVisible).toBe(false)
    })

    it('should not auto hide when duration is 0', () => {
      uiStore.showToast('不自动隐藏', 'info', 0)

      vi.advanceTimersByTime(10000)
      vi.runAllTimers()
      expect(uiStore.toastVisible).toBe(true)
    })
  })
})
