import { createRouter, createWebHistory } from 'vue-router';
import UserRegister from '@/views/UserRegister.vue';
import UserLogin from '@/views/UserLogin.vue';
import PersonalPage from '@/views/PersonalPage.vue';
import DashBoard from '@/views/DashBoard.vue';
import TasksManage from '@/components/TasksManage.vue';
import GroupManage from '@/components/GroupManage.vue';
import MessageBoard from '@/components/MessageBoard.vue';
import MessageDetail from "@/components/MessageDetail.vue";
import DataBank from '@/components/DataBank.vue';
import store from '../store/storage'; // 引入 Vuex store

const routes = [
    {
        path: '/',
        component: UserLogin,
    },
    {
        path: '/DashBoard',
        component: DashBoard,
        redirect: '/DashBoard/Tasks',
        meta: { requiresAuth: true, requiresGroup: true }, // 需要登录且需要选择 groupId
        children: [
            { path: 'Tasks', component: TasksManage, meta: { requiresAuth: true, requiresGroup: true } },
            { path: 'Group', component: GroupManage, meta: { requiresAuth: true, requiresGroup: true } },
            { path: 'MessageBoard', component: MessageBoard, meta: { requiresAuth: true, requiresGroup: true } },
            { path: 'MessageDetail/:id', component: MessageDetail, meta: { requiresAuth: true, requiresGroup: true } },
            { path: 'DataBank', component: DataBank, meta: { requiresAuth: true, requiresGroup: true } }
        ]
    },
    {
        path: '/UserLogin',
        name: 'UserLogin',
        component: UserLogin
    },
    {
        path: '/UserRegister',
        name: 'UserRegister',
        component: UserRegister
    },
    {
        path: '/PersonalPage',
        name: 'PersonalPage',
        component: PersonalPage,
        meta: { requiresAuth: true } // 需要登录
    },
];

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
});

// 全局路由守卫
router.beforeEach((to, from, next) => {
    const isAuthenticated = store.getters.isAuthenticated; // 登录状态
    const hasGroupId = !!store.state.groupId; // 是否已选定 groupId

    if (to.matched.some(record => record.meta.requiresAuth) && !isAuthenticated) {
        // 如果未登录，跳转到登录页面
        next({ path: '/UserLogin', query: { redirect: to.fullPath } });
    } else if (to.matched.some(record => record.meta.requiresGroup) && !hasGroupId) {
        // 如果访问需要 groupId 的页面但未选择 groupId，跳转到 PersonalPage
        next({ path: '/PersonalPage', query: { redirect: to.fullPath } });
    } else {
        next(); // 继续导航
    }
});

export default router;
