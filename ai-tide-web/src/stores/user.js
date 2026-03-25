import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
export const useUserStore = defineStore('user', () => {
    // 状态
    const token = ref('');
    const userInfo = ref(null);
    const isLoading = ref(false);
    // 计算属性
    const isAuthenticated = computed(() => !!token.value && !!userInfo.value);
    const isAdmin = computed(() => userInfo.value?.role === 'ADMIN');
    // 初始化从 localStorage 读取
    function init() {
        const savedToken = localStorage.getItem('token');
        const savedUserInfo = localStorage.getItem('userInfo');
        if (savedToken) {
            token.value = savedToken;
        }
        if (savedUserInfo) {
            try {
                userInfo.value = JSON.parse(savedUserInfo);
            }
            catch (e) {
                console.error('Failed to parse user info from localStorage', e);
            }
        }
    }
    // 登录
    async function login(credentials) {
        isLoading.value = true;
        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(credentials),
            });
            if (!response.ok) {
                throw new Error('登录失败');
            }
            const data = await response.json();
            if (data.code === 200 && data.data) {
                token.value = data.data.token;
                userInfo.value = data.data.user;
                // 保存到 localStorage
                localStorage.setItem('token', data.data.token);
                localStorage.setItem('userInfo', JSON.stringify(data.data.user));
                return data.data;
            }
            else {
                throw new Error(data.message || '登录失败');
            }
        }
        finally {
            isLoading.value = false;
        }
    }
    // 注册
    async function register(userData) {
        isLoading.value = true;
        try {
            const response = await fetch('/api/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(userData),
            });
            if (!response.ok) {
                throw new Error('注册失败');
            }
            const data = await response.json();
            if (data.code === 200 && data.data) {
                token.value = data.data.token;
                userInfo.value = data.data.user;
                // 保存到 localStorage
                localStorage.setItem('token', data.data.token);
                localStorage.setItem('userInfo', JSON.stringify(data.data.user));
                return data.data;
            }
            else {
                throw new Error(data.message || '注册失败');
            }
        }
        finally {
            isLoading.value = false;
        }
    }
    // 登出
    async function logout() {
        isLoading.value = true;
        try {
            if (token.value) {
                await fetch('/api/auth/logout', {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token.value}`,
                    },
                });
            }
        }
        catch (e) {
            console.error('Logout API error:', e);
        }
        finally {
            token.value = '';
            userInfo.value = null;
            localStorage.removeItem('token');
            localStorage.removeItem('userInfo');
            isLoading.value = false;
        }
    }
    // 获取用户信息
    async function fetchUserInfo() {
        if (!token.value)
            return;
        isLoading.value = true;
        try {
            const response = await fetch('/api/user/profile', {
                headers: {
                    'Authorization': `Bearer ${token.value}`,
                },
            });
            if (!response.ok) {
                throw new Error('获取用户信息失败');
            }
            const data = await response.json();
            if (data.code === 200 && data.data) {
                userInfo.value = data.data;
                localStorage.setItem('userInfo', JSON.stringify(data.data));
                return data.data;
            }
        }
        finally {
            isLoading.value = false;
        }
    }
    // 更新用户信息
    function updateUserInfo(info) {
        if (userInfo.value) {
            userInfo.value = { ...userInfo.value, ...info };
            localStorage.setItem('userInfo', JSON.stringify(userInfo.value));
        }
    }
    return {
        token,
        userInfo,
        isLoading,
        isAuthenticated,
        isAdmin,
        init,
        login,
        register,
        logout,
        fetchUserInfo,
        updateUserInfo,
    };
});
