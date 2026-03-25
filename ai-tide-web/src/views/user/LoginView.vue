<template>
  <div class="login-view">
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <h1 class="login-title">登录</h1>
          <p class="login-subtitle">欢迎回到 AI-Tide</p>
        </div>

        <el-form
          ref="formRef"
          :model="formData"
          :rules="formRules"
          size="large"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="formData.username"
              placeholder="用户名或邮箱"
              prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="formData.password"
              type="password"
              placeholder="密码"
              prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <div class="form-options">
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            <router-link to="/forgot-password" class="forgot-link">
              忘记密码?
            </router-link>
          </div>

          <el-form-item>
            <el-button
              type="primary"
              :loading="isLoading"
              class="login-btn"
              @click="handleLogin"
            >
              登录
            </el-button>
          </el-form-item>
        </el-form>

        <div class="register-link">
          还没有账号?
          <router-link to="/register">立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useUiStore } from '@/stores/ui'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const uiStore = useUiStore()

const formRef = ref<FormInstance>()
const isLoading = ref(false)
const rememberMe = ref(false)

const formData = reactive({
  username: '',
  password: '',
})

const formRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名或邮箱', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 个字符', trigger: 'blur' },
  ],
}

async function handleLogin() {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    isLoading.value = true
    await userStore.login({
      username: formData.username,
      password: formData.password,
    })

    uiStore.showToast('登录成功', 'success')

    // 跳转到 redirect 或首页
    const redirect = route.query.redirect as string
    router.push(redirect || '/')
  } catch (error: any) {
    uiStore.showToast(error.message || '登录失败', 'error')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped lang="scss">
@use '@/styles/variables.scss' as *;

.login-view {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $bg-primary;
  padding: 32px;
}

.login-container {
  width: 100%;
  max-width: 480px;
}

.login-card {
  background: $bg-secondary;
  border: 1px solid $border-color;
  border-radius: 24px;
  padding: 48px 40px;
  box-shadow: $shadow-xl;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;

  .login-title {
    font-size: 36px;
    font-weight: 700;
    color: $text-primary;
    margin-bottom: 8px;
  }

  .login-subtitle {
    font-size: 16px;
    color: $text-secondary;
  }
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  .forgot-link {
    color: $accent-primary;
    text-decoration: none;
    font-size: 14px;

    &:hover {
      text-decoration: underline;
    }
  }
}

.login-btn {
  width: 100%;
  padding: 14px;
  font-size: 16px;
  font-weight: 600;
  height: 48px;
}

.register-link {
  text-align: center;
  margin-top: 24px;
  color: $text-secondary;
  font-size: 14px;

  a {
    color: $accent-primary;
    text-decoration: none;
    margin-left: 4px;
    font-weight: 500;

    &:hover {
      text-decoration: underline;
    }
  }
}

@media (max-width: 480px) {
  .login-card {
    padding: 32px 24px;
  }

  .login-header {
    .login-title {
      font-size: 28px;
    }
  }
}
</style>
