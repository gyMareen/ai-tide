import { defineStore } from 'pinia';
import { ref } from 'vue';
export const useContentStore = defineStore('content', () => {
    // 状态
    const contents = ref([]);
    const currentContent = ref(null);
    const categories = ref([]);
    const tags = ref([]);
    const isLoading = ref(false);
    const pagination = ref({
        page: 1,
        pageSize: 20,
        total: 0,
        totalPages: 0,
    });
    // 获取内容列表
    async function fetchContents(params) {
        isLoading.value = true;
        try {
            const queryParams = new URLSearchParams();
            if (params?.page)
                queryParams.append('page', params.page.toString());
            if (params?.pageSize)
                queryParams.append('pageSize', params.pageSize.toString());
            if (params?.categoryId)
                queryParams.append('categoryId', params.categoryId);
            if (params?.tagId)
                queryParams.append('tagId', params.tagId);
            if (params?.type)
                queryParams.append('type', params.type);
            if (params?.sortBy)
                queryParams.append('sortBy', params.sortBy);
            const response = await fetch(`/api/content/list?${queryParams.toString()}`);
            if (!response.ok) {
                throw new Error('获取内容列表失败');
            }
            const data = await response.json();
            if (data.code === 200 && data.data) {
                contents.value = data.data.list || [];
                pagination.value = {
                    page: data.data.page || 1,
                    pageSize: data.data.pageSize || 20,
                    total: data.data.total || 0,
                    totalPages: data.data.totalPages || 0,
                };
                return data.data;
            }
        }
        finally {
            isLoading.value = false;
        }
    }
    // 获取内容详情
    async function fetchContentDetail(id) {
        isLoading.value = true;
        try {
            const response = await fetch(`/api/content/${id}`);
            if (!response.ok) {
                throw new Error('获取内容详情失败');
            }
            const data = await response.json();
            if (data.code === 200 && data.data) {
                currentContent.value = data.data;
                return data.data;
            }
        }
        finally {
            isLoading.value = false;
        }
    }
    // 获取分类列表
    async function fetchCategories() {
        try {
            const response = await fetch('/api/category/list');
            if (!response.ok) {
                throw new Error('获取分类列表失败');
            }
            const data = await response.json();
            if (data.code === 200 && data.data) {
                categories.value = data.data;
                return data.data;
            }
        }
        catch (e) {
            console.error('Failed to fetch categories:', e);
        }
    }
    // 获取标签列表
    async function fetchTags() {
        try {
            const response = await fetch('/api/tag/list');
            if (!response.ok) {
                throw new Error('获取标签列表失败');
            }
            const data = await response.json();
            if (data.code === 200 && data.data) {
                tags.value = data.data;
                return data.data;
            }
        }
        catch (e) {
            console.error('Failed to fetch tags:', e);
        }
    }
    // 清空当前内容
    function clearCurrentContent() {
        currentContent.value = null;
    }
    return {
        contents,
        currentContent,
        categories,
        tags,
        isLoading,
        pagination,
        fetchContents,
        fetchContentDetail,
        fetchCategories,
        fetchTags,
        clearCurrentContent,
    };
});
