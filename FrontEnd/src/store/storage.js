import { createStore } from 'vuex';

const store = createStore({
  state: {
    userId: JSON.parse(sessionStorage.getItem('userId')) || null, // 从 SessionStorage 恢复用户信息
    groupId: JSON.parse(sessionStorage.getItem('groupId')) || null, // 从 SessionStorage 恢复群组ID
    userRole: JSON.parse(sessionStorage.getItem('userRole')) || null,
  },
  mutations: {
    // 设置用户ID
    setUser(state, userId) {
      state.userId = userId;
      sessionStorage.setItem('userId', JSON.stringify(userId)); // 存储到 SessionStorage
    },
    // 设置群组ID
    setGroupId(state, groupId) {
      state.groupId = groupId;
      sessionStorage.setItem('groupId', JSON.stringify(groupId)); // 存储到 SessionStorage
    },
    // 设置用户角色
    setUserRole(state, userRole) {
      state.userRole = userRole;
      sessionStorage.setItem('userRole', JSON.stringify(userRole)); // 存储到 SessionStorage
    },
    
    // 清除用户ID
    clearUserId(state) {
      state.userId = null;
      sessionStorage.removeItem('userId'); // 移除 SessionStorage
    },
    // 清除群组ID
    clearGroupId(state) {
      state.groupId = null;
      sessionStorage.removeItem('groupId'); // 移除 SessionStorage
    },
    // 清除用户角色
    clearUserRole(state) {
      state.userRole = null;
      sessionStorage.removeItem('userRole'); // 移除 SessionStorage
    },
  },
  getters: {
    // 检查用户是否已登录
    isAuthenticated(state) {
      return !!state.userId; // 如果 userId 存在，则视为已登录
    },
  },
});

export default store;
