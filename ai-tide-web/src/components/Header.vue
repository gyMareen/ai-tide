<template>
  <header class="header">
    <div class="header-container">
      <!-- Logo -->
      <router-link to="/" class="logo">
        <span class="logo-icon">⚡</span>
        <span class="logo-text">AI-Tide</span>
      </router-link>

      <!-- Navigation -->
      <nav class="nav">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="nav-link"
          active-class="nav-link--active"
        >
          <span v-if="item.icon" class="nav-icon">{{ item.icon }}</span>
          <span>{{ item.label }}</span>
          <span v-if="item.badge" class="nav-badge">{{ item.badge }}</span>
        </router-link>
      </nav>

      <!-- Right section -->
      <div class="header-right">
        <!-- Search -->
        <SearchBox class="header-search" />

        <!-- User menu -->
        <div v-if="isAuthenticated" class="user-menu">
          <el-dropdown trigger="click">
            <div class="user-avatar">
              <el-avatar :size="36">
                {{ userInfo?.nickname?.charAt(0) || 'U' }}
              </el-avatar>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>
                  <router-link to="/profile">
                    <el-icon><User /></el-icon>
                    个人中心
                  </router-link>
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>

        <!-- Auth buttons -->
        <div v-else class="auth-buttons">
          <router-link to="/login" class="btn btn-login">登录</router-link>
          <router-link to="/register" class="btn btn-register">注册</router-link>
        </div>

        <!-- Theme toggle -->
        <el-button
          :icon="isDark ? Sunny : Moon"
          circle
          class="theme-toggle"
          @click="toggleTheme"
        />

        <!-- Mobile menu -->
        <el-button
          :icon="isMenuOpen ? Close : Menu"
          circle
          class="mobile-menu-toggle"
          @click="toggleMenu"
        />
      </div>
    </div>

    <!-- Mobile nav -->
    <div v-if="isMenuOpen" class="mobile-nav">
      <router-link
        v-for="item in navItems"
        :key="item.path"
        :to="item.path"
        class="mobile-nav-link"
        @click="closeMenu"
      >
        <span v-if="item.icon" class="nav-icon">{{ item.icon }}</span>
        <span>{{ item.label }}</span>
      </router-link>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useUiStore } from '@/stores/ui'
import { User, SwitchButton, Sunny, Moon, Menu, Close } from '@element-plus/icons-vue'
import SearchBox from './SearchBox.vue'

const router = useRouter()
const userStore = useUserStore()
const uiStore = useUiStore()

const isAuthenticated = computed(() => userStore.isAuthenticated)
const userInfo = computed(() => userStore.userInfo)
const isDark = computed(() => uiStore.theme === 'dark')

const isMenuOpen = ref(false)

const navItems = [
  { path: '/', label: '探索', icon: '🔍' },
  { path: '/search', label: '搜索' },
  { path: '/agent/teams', label: '智能体', icon: '🤖', badge: 'LIVE' },
]

function toggleTheme() {
  uiStore.toggleTheme()
}

function toggleMenu() {
  isMenuOpen.value = !isMenuOpen.value
}

function closeMenu() {
  isMenuOpen.value = false
}

async function handleLogout() {
  await userStore.logout()
  router.push('/')
}
</script>

<style scoped lang="scss">
@use '@/styles/variables.scss' as *;

.header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  background: rgba($bg-primary, 0.95);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid $border-color;
  z-index: 1000;
}

.header-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 32px;
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 700;
  font-size: 24px;
  color: $text-primary;
  text-decoration: none;

  &-icon {
    font-size: 28px;
  }
}

.nav {
  display: flex;
  gap: 32px;

  @media (max-width: 768px) {
    display: none;
  }
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 6px;
  color: $text-secondary;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: color 0.3s ease;
  position: relative;

  &:hover {
    color: $accent-primary;
  }

  &--active {
    color: $accent-primary;
  }
}

.nav-icon {
  font-size: 18px;
}

.nav-badge {
  margin-left: 4px;
  padding: 2px 6px;
  background: #ef4444;
  color: white;
  border-radius: 4px;
  font-size: 10px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-search {
 {
  @media (max-width: 768px) {
    display: none;
  }
}

.user-menu {
  .user-avatar {
    cursor: pointer;
    transition: transform 0.3s ease;

    &:hover {
      transform: scale(1.1);
    }
  }
}

.auth-buttons {
  display: flex;
  gap: 12px;

  @media (max-width: 768px) {
    display: none;
  }
}

.btn {
  padding: 8px 24px;
  border-radius: 24px;
  text-decoration: none;
  font-weight: 500;
  font-size: 14px;
  transition: all 0.3s ease;

  &-login {
    color: $text-secondary;

    &:hover {
      color: $text-primary;
    }
  }

  &-register {
    background: linear-gradient(135deg, $accent-primary, $accent-secondary);
    color: white;

    &:hover {
      transform: translateY(-2px);
      box-shadow: $shadow-accent;
    }
  }
}

.theme-toggle {
  width: 40px;
  height: 40px;
}

.mobile-menu-toggle {
  display: none;

  @media (max-width: 768px) {
    display: flex;
  }
}

.mobile-nav {
  position: absolute;
  top: 72px;
  left: 0;
  right: 0;
  background: $bg-secondary;
  border-bottom: 1px solid $border-color;
  padding: 24px 32px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  animation: slideDown 0.3s ease;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.mobile-nav-link {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  color: $text-primary;
  text-decoration: none;
  border-radius: 8px;

  &:hover {
    background: rgba($accent-primary, 0.1);
  }

  .nav-icon {
    font-size: 20px;
  }
}
</style>
