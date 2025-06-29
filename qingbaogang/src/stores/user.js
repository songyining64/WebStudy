import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useUserStore = defineStore('user', () => {
    // 从 localStorage 加载初始用户数据
    const savedAvatar = localStorage.getItem('userAvatar');
    const savedName = localStorage.getItem('userName');
    const savedEmail = localStorage.getItem('userEmail');
    const savedBio = localStorage.getItem('userBio');
    const savedRole = localStorage.getItem('userRole');
    const savedIsAdmin = localStorage.getItem('isAdmin');

    const avatar = ref(savedAvatar || '@/assets/default-avatar.png');
    const name = ref(savedName || '用户名');
    const email = ref(savedEmail || '');
    const bio = ref(savedBio || '');
    const role = ref(savedRole || 'user');
    const isAdmin = ref(savedIsAdmin === 'true' || false);

    // 更新头像
    function updateAvatar(newAvatar) {
        avatar.value = newAvatar;
        localStorage.setItem('userAvatar', newAvatar);
    }

    // 更新用户名
    function updateName(newName) {
        name.value = newName;
        localStorage.setItem('userName', newName);
    }

    // 更新电子邮箱
    function updateEmail(newEmail) {
        email.value = newEmail;
        localStorage.setItem('userEmail', newEmail);
    }

    // 更新个人简介
    function updateBio(newBio) {
        bio.value = newBio;
        localStorage.setItem('userBio', newBio);
    }

    // 更新用户角色
    function updateRole(newRole) {
        role.value = newRole;
        localStorage.setItem('userRole', newRole);
    }

    // 设置管理员权限
    function setAdminStatus(adminStatus) {
        isAdmin.value = adminStatus;
        localStorage.setItem('isAdmin', adminStatus.toString());
    }

    // 更新整个用户对象
    function updateUser(userData) {
        if (userData.avatar !== undefined) {
            updateAvatar(userData.avatar);
        }
        if (userData.name !== undefined) {
            updateName(userData.name);
        }
        if (userData.email !== undefined) {
            updateEmail(userData.email);
        }
        if (userData.bio !== undefined) {
            updateBio(userData.bio);
        }
        if (userData.role !== undefined) {
            updateRole(userData.role);
        }
        if (userData.isAdmin !== undefined) {
            setAdminStatus(userData.isAdmin);
        }
    }

    // 清除用户数据（退出登录时使用）
    function clearUserData() {
        localStorage.removeItem('userAvatar');
        localStorage.removeItem('userName');
        localStorage.removeItem('userEmail');
        localStorage.removeItem('userBio');
        localStorage.removeItem('userRole');
        localStorage.removeItem('isAdmin');
        avatar.value = '@/assets/default-avatar.png';
        name.value = '用户名';
        email.value = '';
        bio.value = '';
        role.value = 'user';
        isAdmin.value = false;
    }

    return {
        avatar,
        name,
        email,
        bio,
        role,
        isAdmin,
        updateAvatar,
        updateName,
        updateEmail,
        updateBio,
        updateRole,
        setAdminStatus,
        updateUser,
        clearUserData
    };
});