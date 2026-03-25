import { defineStore } from 'pinia';
import { ref } from 'vue';
export const useUiStore = defineStore('ui', () => {
    // 状态
    const theme = ref('dark');
    const sidebarOpen = ref(false);
    const loading = ref(false);
    const loadingText = ref('');
    const toastMessage = ref('');
    const toastType = ref('info');
    const toastVisible = ref(false);
    // 初始化主题
    function initTheme() {
        const savedTheme = localStorage.getItem('theme');
        if (savedTheme) {
            theme.value = savedTheme;
            applyTheme(savedTheme);
        }
        else {
            // 默认暗色主题
            applyTheme('dark');
        }
    }
    // 应用主题
    function applyTheme(newTheme) {
        theme.value = newTheme;
        localStorage.setItem('theme', newTheme);
        if (newTheme === 'dark') {
            document.body.classList.add('dark');
            document.body.classList.remove('light');
        }
        else {
            document.body.classList.add('light');
            document.body.classList.remove('dark');
        }
    }
    // 切换主题
    function toggleTheme() {
        const newTheme = theme.value === 'dark' ? 'light' : 'dark';
        applyTheme(newTheme);
    }
    // 切换侧边栏
    function toggleSidebar() {
        sidebarOpen.value = !sidebarOpen.value;
    }
    // 打开侧边栏
    function openSidebar() {
        sidebarOpen.value = true;
    }
    // 关闭侧边栏
    function closeSidebar() {
        sidebarOpen.value = false;
    }
    // 显示加载中
    function showLoading(text = '加载中...') {
        loading.value = true;
        loadingText.value = text;
    }
    // 隐藏加载中
    function hideLoading() {
        loading.value = false;
        loadingText.value = '';
    }
    // 显示提示
    function showToast(message, type = 'info', duration = 3000) {
        toastMessage.value = message;
        toastType.value = type;
        toastVisible.value = true;
        if (duration > 0) {
            setTimeout(() => {
                toastVisible.value = false;
            }, duration);
        }
    }
    // 隐藏提示
    function hideToast() {
        toastVisible.value = false;
    }
    return {
        theme,
        sidebarOpen,
        loading,
        loadingText,
        toastMessage,
        toastType,
        toastVisible,
        initTheme,
        applyTheme,
        toggleTheme,
        toggleSidebar,
        openSidebar,
        closeSidebar,
        showLoading,
        hideLoading,
        showToast,
        hideToast,
    };
});
