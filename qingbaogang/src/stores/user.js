import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import defaultAvatarUrl from '@/assets/default-avatar.png';

export const useUserStore = defineStore('user', () => {
    // 从 localStorage 加载初始用户数据
    const savedAvatar = localStorage.getItem('userAvatar');
    const savedName = localStorage.getItem('userName');
    const savedEmail = localStorage.getItem('userEmail');
    const savedBio = localStorage.getItem('userBio');
    const savedRole = localStorage.getItem('userRole');
    const savedIsAdmin = localStorage.getItem('isAdmin');
    const savedBirthDate = localStorage.getItem('userBirthDate');
    const savedUserId = localStorage.getItem('userId');

    // 处理默认头像路径
    const getDefaultAvatar = () => {
        return defaultAvatarUrl;
    }

    const avatar = ref(savedAvatar || getDefaultAvatar());
    const name = ref(savedName || '用户名');
    const email = ref(savedEmail || '');
    const bio = ref(savedBio || '');
    const role = ref(savedRole || 'user');
    const isAdmin = ref(savedIsAdmin === 'true' || false);
    const birthDate = ref(savedBirthDate || '');
    const userId = ref(savedUserId || '');

    // 计算属性：用户是否已登录
    const isLoggedIn = computed(() => {
        return !!userId.value; // 如果 userId 存在且不为空，则认为用户已登录
    });

    // 批量更新 localStorage
    function updateLocalStorage(updates) {
        Object.entries(updates).forEach(([key, value]) => {
            if (value !== undefined) {
                try {
                    localStorage.setItem(key, value.toString());
                    console.log(`Updated ${key} in localStorage`);
                } catch (error) {
                    console.error(`Failed to update ${key} in localStorage:`, error);
                    // 如果是 QuotaExceededError，尝试移除旧的头像数据
                    if (error.name === 'QuotaExceededError' && key === 'userAvatar') {
                        try {
                            localStorage.removeItem('userAvatar');
                            localStorage.setItem(key, value.toString());
                            console.log('Successfully updated avatar after clearing old data');
                        } catch (retryError) {
                            console.error('Failed to update avatar even after retry:', retryError);
                        }
                    }
                }
            }
        });
    }

    // 更新整个用户对象
    async function updateUser(userData) {
        console.log('Updating user data:', { ...userData, avatar: userData.avatar ? 'data present' : 'no data' });
        const updates = {};

        if (userData.avatar !== undefined) {
            if (
                typeof userData.avatar === 'string' &&
                userData.avatar.trim() !== '' &&
                (userData.avatar.startsWith('data:image/') ||
                    userData.avatar.startsWith('http') ||
                    userData.avatar.startsWith('blob:') ||
                    userData.avatar === defaultAvatarUrl)
            ) {
                avatar.value = userData.avatar;
                updates.userAvatar = userData.avatar;
                console.log('Avatar data validated and updated');
            } else {
                avatar.value = getDefaultAvatar();
                updates.userAvatar = getDefaultAvatar();
                console.warn('Avatar data invalid, fallback to default avatar');
            }
        }
        if (userData.name !== undefined) {
            name.value = userData.name;
            updates.userName = userData.name;
        }
        if (userData.email !== undefined) {
            email.value = userData.email;
            updates.userEmail = userData.email;
        }
        if (userData.bio !== undefined) {
            bio.value = userData.bio;
            updates.userBio = userData.bio;
        }
        if (userData.role !== undefined) {
            role.value = userData.role;
            updates.userRole = userData.role;
        }
        if (userData.isAdmin !== undefined) {
            isAdmin.value = userData.isAdmin;
            updates.isAdmin = userData.isAdmin;
        }
        if (userData.birthDate !== undefined) {
            birthDate.value = userData.birthDate;
            updates.userBirthDate = userData.birthDate;
        }
        if (userData.userId !== undefined) {
            userId.value = userData.userId;
            updates.userId = userData.userId;
        }

        // 批量更新 localStorage
        updateLocalStorage(updates);
    }

    // 清除用户数据（退出登录时使用）
    function clearUserData() {
        const keys = ['userAvatar', 'userName', 'userEmail', 'userBio', 'userRole', 'isAdmin', 'userBirthDate', 'userId'];
        keys.forEach(key => {
            try {
                localStorage.removeItem(key);
            } catch (error) {
                console.error(`Failed to remove ${key} from localStorage:`, error);
            }
        });

        avatar.value = getDefaultAvatar();
        name.value = '用户名';
        email.value = '';
        bio.value = '';
        role.value = 'user';
        isAdmin.value = false;
        birthDate.value = '';
        userId.value = '';
    }

    return {
        avatar,
        name,
        email,
        bio,
        role,
        isAdmin,
        birthDate,
        userId,
        isLoggedIn,
        updateUser,
        clearUserData
    };
});