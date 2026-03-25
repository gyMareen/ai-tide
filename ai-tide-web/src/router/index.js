import { createRouter, createWebHistory } from 'vue-router';
import { setupRouterGuards } from './guards/auth';
const routes = [
    {
        path: '/',
        name: 'Home',
        component: () => import('@/views/home/HomeView.vue'),
        meta: {
            title: '探索',
            requiresAuth: false
        }
    },
    {
        path: '/search',
        name: 'Search',
        component: () => import('@/views/search/SearchView.vue'),
        meta: {
            title: '搜索',
            requiresAuth: false
        }
    },
    {
        path: '/category/:id',
        name: 'Category',
        component: () => import('@/views/category/CategoryView.vue'),
        meta: {
            title: '分类',
            requiresAuth: false
        }
    },
    {
        path: '/content/:id',
        name: 'ContentDetail',
        component: () => import('@/views/content/ContentView.vue'),
        meta: {
            title: '内容详情',
            requiresAuth: false
        }
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/user/LoginView.vue'),
        meta: {
            title: '登录',
            requiresAuth: false,
            hideForAuth: true
        }
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/user/RegisterView.vue'),
        meta: {
            title: '注册',
            requiresAuth: false,
            hideForAuth: true
        }
    },
    {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/user/ProfileView.vue'),
        meta: {
            title: '个人中心',
            requiresAuth: true
        }
    },
    {
        path: '/agent/teams',
        name: 'AgentTeams',
        component: () => import('@/views/agent/TeamListView.vue'),
        meta: {
            title: '智能体团队',
            requiresAuth: false
        }
    },
    {
        path: '/agent/session/:id',
        name: 'AgentSession',
        component: () => import('@/views/agent/SessionDetailView.vue'),
        meta: {
            title: '智能体会话详情',
            requiresAuth: false
        }
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: () => import('@/views/NotFoundView.vue'),
        meta: {
            title: '页面不存在',
            requiresAuth: false
        }
    }
];
const router = createRouter({
    history: createWebHistory(),
    routes
});
setupRouterGuards();
export default router;
