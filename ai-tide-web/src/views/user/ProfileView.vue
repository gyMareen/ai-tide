<template>
  <div class="profile-view">
    <div class="profile-container">
      <div class="profile-header">
        <h1 class="profile-title">个人中心</h1>
      </div>

      <div class="profile-content">
        <!-- 用户信息卡片 -->
        <el-card class="profile-card">
          <template #header>
            <div class="card-header">
              <span>基本信息</span>
              <el-button
                v-if="!isEditing"
                type="primary"
                text
                @click="startEdit"
              >
                编辑
              </el-button>
            </div>
          </template>

          <div v-if="!isEditing" class="profile-info">
            <div class="info-item">
              <span class="label">用户名</span>
              <span class="value">{{ userInfo?.username }}</span>
            </div>
            <div class="info-item">
              <span class="label">邮箱</span>
              <span class="value">{{ userInfo?.email }}</span>
            </div>
            <div class="info-item">
              <span class="label">昵称</span>
              <span class="value">{{ userInfo?.nickname || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="label">角色</span>
              <el-tag :type="userInfo?.role === 'ADMIN' ? 'danger' : 'success'">
                {{ userInfo?.role === 'ADMIN' ? '管理员' : '普通用户' }}
              </el-tag>
            </div>
            <div class="info-item">
              <span class="label">账号状态</span>
              <el-tag :type="userInfo?.status === 'ACTIVE' ? 'success' : 'danger'">
                {{ userInfo?.status === 'ACTIVE' ? '正常' : '已停用' }}
              </el-tag>
            </div>
            <div class="info-item">
              <span class="label">注册时间</span>
              <span class="value">{{ formatDate(userInfo?.createdAt) }}</span>
            </div>
          </div>

          <el-form
            v-else
            ref="formRef"
            :model="editForm"
            :rules="formRules"
            label-width="80px"
          >
            <el-form-item label="用户名">
              <el-input v-model="editForm.username" disabled />
            </el-form-item>

            <el-form-item label="邮箱">
              <el-input v-model="editForm.email" disabled />
            </el-form-item>

            <el-form-item label="昵称" prop="nickname">
              <el-input
                v-model="editForm.nickname"
                placeholder="请输入昵称"
              />
            </el-form-item>

            <el-form-item label="个人简介" prop="bio">
              <el-input
                v-model="editForm.bio"
                type="textarea"
                :rows="4"
                placeholder="请输入个人简介"
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" :loading="isLoading" @click="saveProfile">
                保存
              </el-button>
              <el-button @click="cancelEdit">取消</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 修改密码卡片 -->
        <el-card class="profile-card">
          <template #header>
            <span>修改密码</span>
          </template>

          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="100px"
          >
            <el-form-item label="原密码" prop="oldPassword">
              <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                show-password
                placeholder="请输入原密码"
              />
            </el-form-item>

            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                show-password
                placeholder="请输入新密码"
              />
            </el-form-item>

            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                show-password
                placeholder="请再次输入新密码"
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                :loading="isChangingPassword"
                @click="changePassword"
              >
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useUiStore } from '@/stores/ui'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const uiStore = useUiStore()

const userInfo = computed(() => userStore.userInfo)

const formRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()
const isEditing = ref(false)
const isLoading = ref(false)
const isChangingPassword = ref(false)

const editForm = reactive({
  username: '',
  email: '',
  nickname: '',
  bio: '',
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const formRules: FormRules = {
  nickname: [
    { max: 50, message: '昵称最多 50 个字符', trigger: 'blur' },
  ],
  bio: [
    { max: 500, message: '个人简介最多 500 个字符', trigger: 'blur' },
  ],
}

const validateConfirmPassword = (_rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const passwordRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 个字符', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' },
  ],
}

function startEdit() {
  if (userInfo.value) {
    editForm.username = userInfo.value.username
    editForm.email = userInfo.value.email
    editForm.nickname = userInfo.value.nickname || ''
    editForm.bio = userInfo.value.bio || ''
    isEditing.value = true
  }
}

function cancelEdit() {
  isEditing.value = false
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
}

async function saveProfile() {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    isLoading.value = true

    const response = await fetch('/api/user/profile', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${userStore.token}`,
      },
      body: JSON.stringify({
        nickname: editForm.nickname || undefined,
        bio: editForm.bio || undefined,
      }),
    })

    if (!response.ok) {
      throw new Error('更新失败')
    }

    const data = await response.json()

    if (data.code === 200 && data.data) {
      userStore.updateUserInfo(data.data)
      uiStore.showToast('保存成功', 'success')
      isEditing.value = false
    } else {
      throw new Error(data.message || '更新失败')
    }
  } catch (error: any) {
    uiStore.showToast(error.message || '保存失败', 'error')
  } finally {
    isLoading.value = false
  }
}

async function changePassword() {
  if (!passwordFormRef.value) return

  try {
    await passwordFormRef.value.validate()

    isChangingPassword.value = true

    const response = await fetch('/api/user/password', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${userStore.token}`,
      },
      body: JSON.stringify({
        oldPassword: passwordForm.oldPassword,
        newPassword: passwordForm.newPassword,
      }),
    })

    if (!response.ok) {
      throw new Error('修改密码失败')
    }

    const data = await response.json()

    if (data.code === 200) {
      uiStore.showToast('密码修改成功，请重新登录', 'success')
      cancelEdit()

      // 登出并跳转到登录页
      await userStore.logout()
      router.push('/login')
    } else {
      throw new Error(data.message || '修改密码失败')
    }
  } catch (error: any) {
    uiStore.showToast(error.message || '修改密码失败', 'error')
  } finally {
    isChangingPassword.value = false
  }
}

function formatDate(date?: string) {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}
</script>

<style scoped lang="scss">
@use '@/styles/variables.scss' as *;

.profile-view {
  min-height: 100vh;
  background: $bg-primary;
  padding: 32px;
}

.profile-container {
  max-width: 800px;
  margin: 0 auto;
}

.profile-header {
  margin-bottom: 32px;

  .profile-title {
    font-size: 32px;
    font-weight: 700;
    color: $text-primary;
  }
}

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.profile-card {
  background: $bg-secondary;
  border: 1px solid $border-color;
  box-shadow: $shadow-md;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-info {
  .info-item {
    display: flex;
    padding: 16px 0;
    border-bottom: 1px solid $border-light;

    &:last-child {
      border-bottom: none;
    }

    .label {
      width: 120px;
      color: $text-secondary;
      font-size: 14px;
    }

    .value {
      flex: 1;
      color: $text-primary;
      font-size: 14px;
    }
  }
}

@media (max-width: 768px) {
  .profile-view {
    padding: 16px;
  }

  .profile-header {
    .profile-title {
      font-size: 24px;
    }
  }

  .profile-info {
    .info-item {
      flex-direction: column;
      gap: 8px;

      .label {
        width: auto;
      }

      .value {
        width: auto;
      }
    }
  }
}
</style>
