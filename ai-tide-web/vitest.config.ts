import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  test: {
    globals: true,
    environment: 'happy-dom',
    setupFiles: ['./src/test/setup.ts'],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html', 'lcov'],
      include: ['src/**/*.{ts,tsx,vue}'],
      exclude: [
        'node_modules/',
        'dist/',
        '**/types/**',
        '**/*.d.ts',
        '**/main.ts',
        '**/*.config.*',
        '**/test/**',
      ],
      all: false,
      clean: true,
    },
    include: ['**/*.{test,spec}.{ts,tsx}'],
    exclude: ['node_modules/', 'dist/'],
    teardownTimeout: 10000,
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
      '@styles': path.resolve(__dirname, 'src/styles'),
      '@components': path.resolve(__dirname, 'src/components'),
      '@views': path.resolve(__dirname, 'src/views'),
      '@stores': path.resolve(__dirname, 'src/stores'),
      '@api': path.resolve(__dirname, 'src/api'),
      '@utils': path.resolve(__dirname, 'src/utils'),
      '@types': path.resolve(__dirname, 'src/types'),
      '@composables': path.resolve(__dirname, 'src/composables'),
      'vue': 'vue/dist/vue.esm-bundler.js',
    },
  },
})
