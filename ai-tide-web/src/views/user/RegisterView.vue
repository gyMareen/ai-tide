<template>
  <div class="register-view">
    <div class="register-container">
      <div class="register-card">
        <div class="register-header">
          <h1 class="register-title">注册</h1>
          <p class="register-subtitle">加入 AI-Tide，探索更多可能</p>
        </div>

        <el-form
          ref="formRef"
          :model="formData"
          :rules="formRules"
          size="large"
        >
          <el-form-item prop="username">
            <el-input
              v-model="formData.username"
              placeholder="用户名"
              prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="email">
            <el-input
              v-model="formData.email"
              placeholder="邮箱"
              prefix-icon="Message"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="formData.password"
              type="password"
              placeholder="密码"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="formData.confirmPassword"
              type="password"
              placeholder="确认密码"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item prop="nickname">
            <el-input
              v-model="formData.nickname"
              placeholder="昵称（可选）"
              prefix-icon="UserFilled"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              :loading="isLoading"
              class="register-btn"
              @click="handleRegister"
            >
              注册
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-link">
          已有账号?
          <router-link to="/login">立即登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useUiStore } from '@/stores/ui'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const uiStore = useUiStore()

const formRef = ref<FormInstance>()
const isLoading = ref(false)

const formData = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  nickname: '',
})

const validateConfirmPassword = (_rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== formData.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const formRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为 3-20 个字符', trigger: 'blur' },
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 个字符', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' },
  ],
}

async function handleRegister() {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    isLoading.value = true
    await userStore.register({
      username: formData.username,
      email: formData.email,
      password: formData.password,
      nickname: formData.nickname || undefined,
    })

    uiStore.showToast('注册成功', 'success')

    // 跳转到首页
    router.push('/')
  } catch (error: any) {
    uiStore.showToast(error.message || '注册失败', 'error')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped lang="scss">
@use '@/styles/variables.scss' as *;

.register-view {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $bg-primary;
  padding: 32px;
}

.register-container {
  width: 100%;
  max-width: 480px;
}

.register-card {
  background: $bg-secondary;
  border: 1px solid $border-color;
  border-radius: 24px;
  padding: 48px 40px;
  box-shadow: $shadow-xl;
}

.register-header {
  text-align: center;
  margin-bottom: 40px;

  .register-title {
    font-size: 36px;
    font-weight: 700;
    color: $text-primary;
    margin-bottom: 8px;
  }

  .register-subtitle {
    font-size: 16px;
    color: $text-secondary;
  }
}

.register-btn {
  width: 100%;
  padding: 14px;
  font-size: 16px;
  font-weight: 600;
  height: 48px;
}

.login-link {
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
  .register-card {
    padding: 32px 24px;
  }

  .register-header {
    .register-title {
      font-size: 28px;
    }
  }
}
</style>
