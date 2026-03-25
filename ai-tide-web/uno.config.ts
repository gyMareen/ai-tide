import {
  defineConfig,
  presetUno,
  presetAttributify,
  presetIcons,
} from 'unocss'

export default defineConfig({
  presets: [
    presetUno(),
    presetAttributify(),
    presetIcons(),
  ],
  shortcuts: {
    // 布局
    'flex-center': 'flex items-center justify-center',
    'flex-between': 'flex items-center justify-between',
    'flex-col-center': 'flex flex-col items-center justify-center',

    // 间距
    'p-content': 'px-24 py-16',
    'm-content': 'mx-24 my-16',

    // 文字
    'text-ellipsis': 'overflow-hidden text-ellipsis whitespace-nowrap',
    'text-clamp-2': 'line-clamp-2 overflow-hidden',
    'text-clamp-3': 'line-clamp-3 overflow-hidden',

    // 边框
    'border-base': 'border border-gray-200',
    'rounded-base': 'rounded-lg',
  },
  theme: {
    colors: {
      // 主色系
      primary: '#1a8cff',
      secondary: '#06b6d4',

      // 背景色
      'bg-primary': '#0a0f1f',
      'bg-secondary': '#111827',
      'bg-tertiary': '#1a2332',

      // 文字色
      'text-primary': '#e8eaed',
      'text-secondary': '#94a3b8',
      'text-tertiary': '#6b7280',
    },
    animation: {
      'fade-in': 'fade-in 0.3s ease-out',
      'slide-up': 'slide-up 0.3s ease-out',
    },
    keyframes: {
      'fade-in': {
        '0%': { opacity: '0' },
        '100%': { opacity: '1' },
      },
      'slide-up': {
        '0%': { transform: 'translateY(10px)', opacity: '0' },
        '100%': { transform: 'translateY(0)', opacity: '1' },
      },
    },
  },
})
