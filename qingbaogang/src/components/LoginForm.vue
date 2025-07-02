<template>
  <div class="form-wrapper">
    <div class="logo-container">
      <img src="../assets/logo.jpg" alt="Logo" class="logo">
      <h2>情波港</h2>
    </div>

    <div v-if="showWelcomeMessage" class="welcome-message">
      <i class="check-icon"></i>
      <span>注册成功！请使用您的账号登录</span>
    </div>

    <form @submit.prevent="handleSubmit" class="auth-form">
      <div class="form-group">
        <div class="input-icon">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
            <path d="M20 4H4a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V6a2 2 0 0 0-2-2zm0 4.73l-8 4.74-8-4.74V6l8 4.73L20 6z"/>
          </svg>
        </div>
        <input
            type="email"
            v-model="credentials.email"
            placeholder="邮箱"
            required
            class="form-input"
        >
      </div>

      <div class="form-group">
        <div class="input-icon">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
            <path d="M18 8h-1V6A6 6 0 0 0 6 6v2H5a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V10a2 2 0 0 0-2-2zM8 6a4 4 0 0 1 8 0v2H8zm4 10a2 2 0 1 1 2-2 2 2 0 0 1-2 2z"/>
          </svg>
        </div>
        <input
            type="password"
            v-model="credentials.password"
            placeholder="密码"
            required
            class="form-input"
        >
      </div>

      <div class="action-row">
        <label class="remember">
          <input type="checkbox" v-model="rememberMe"> 记住我
        </label>
        <a href="#" class="forgot-link" @click.prevent="handleForgotPassword">忘记密码?</a>
      </div>

      <button type="submit" class="submit-btn" :disabled="isLoading">
        <span>{{ isLoading ? '登录中...' : '登 录' }}</span>
      </button>
    </form>

    <div class="footer-links">
      <p>还没有账号? <a href="#" @click.prevent="goToRegister">立即注册</a></p>
    </div>

    <div v-if="error" class="error-message">
      <div class="error-icon">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
          <path d="M11.29 15.29a1 1 0 0 0 1.42 0 1 1 0 0 0 0-1.42L13.41 13l1.3-1.29a1 1 0 0 0-1.42-1.42L12 11.59l-1.29-1.3a1 1 0 0 0-1.42 1.42l1.3 1.29-1.3 1.29a1 1 0 0 0 0 1.42 1 1 0 0 0 1.42 0l1.29-1.3 1.29 1.3zM12 2a10 10 0 1 0 10 10A10 10 0 0 0 12 2zm0 18a8 8 0 1 1 8-8 8 8 0 0 1-8 8z"/>
        </svg>
      </div>
      {{ error }}
    </div>

    <div v-if="showForgotDialog" class="forgot-dialog-bg">
      <div class="forgot-dialog">
        <h3>重置密码</h3>
        <form @submit.prevent="handleResetPassword">
          <div class="form-group">
            <input type="email" v-model="forgotEmail" placeholder="邮箱" required class="form-input">
          </div>
          <div class="form-group forgot-code-group">
            <input type="text" v-model="forgotCode" placeholder="验证码" required class="form-input">
            <button type="button" class="send-code-btn" :disabled="forgotCodeSending || forgotCodeCountdown > 0 || !forgotEmail.match(/^\S+@\S+\.\S+$/)" @click="sendForgotCode">
              {{ forgotCodeCountdown > 0 ? forgotCodeCountdown + 's后重发' : (forgotCodeSending ? '发送中...' : '发送验证码') }}
            </button>
          </div>
          <div class="form-group">
            <input type="password" v-model="forgotNewPwd" placeholder="新密码" required class="form-input">
          </div>
          <div class="form-group">
            <input type="password" v-model="forgotConfirmPwd" placeholder="确认新密码" required class="form-input">
          </div>
          <div v-if="forgotError" class="error-message">{{ forgotError }}</div>
          <div v-if="forgotSuccess" class="success-message">{{ forgotSuccess }}</div>
          <div class="dialog-actions">
            <button type="submit" class="submit-btn">重置密码</button>
            <button type="button" class="cancel-btn" @click="showForgotDialog=false">取消</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { useUserStore } from '@/stores/user'
import { ref } from 'vue'
import { request } from '@/utils/request'

export default {
  data() {
    return {
      credentials: {
        email: '',
        password: ''
      },
      error: '',
      rememberMe: false,
      showWelcomeMessage: false,
      showForgotDialog: false,
      forgotEmail: '',
      forgotCode: '',
      forgotNewPwd: '',
      forgotConfirmPwd: '',
      forgotError: '',
      forgotSuccess: '',
      forgotCodeSending: false,
      forgotCodeCountdown: 0,
      forgotCodeTimer: null,
      isLoading: false
    }
  },
  created() {
    // 从路由参数检查是否有注册成功消息
    if (this.$route.query.registered === 'true') {
      this.showWelcomeMessage = true;
      this.credentials.email = this.$route.query.email || '';

      // 5秒后隐藏欢迎消息
      setTimeout(() => {
        this.showWelcomeMessage = false;
      }, 5000);
    }
  },
  methods: {
    async handleSubmit() {
      if (this.isLoading) return;
      // 简单的表单验证
      if (!this.credentials.email.trim()) {
        this.error = '请输入邮箱';
        return;
      }
      // 邮箱格式校验
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.credentials.email)) {
        this.error = '请输入有效的邮箱';
        return;
      }
      if (!this.credentials.password) {
        this.error = '请输入密码';
        return;
      }
      this.error = '';
      this.isLoading = true;
      try {
        // 调用后端登录接口，参数为 username 和 password
        const res = await request.post('/api/user/login', {
          username: this.credentials.email, // 用邮箱作为用户名
          password: this.credentials.password
        });
        if (res.code === 200) {
          // 保存token
          localStorage.setItem('authToken', res.data.token);
          // 新增：保存userId
          localStorage.setItem('userId', res.data.userId);
          // 保存角色
          localStorage.setItem('userRole', res.data.role);
          // 设置isAdmin状态
          localStorage.setItem('isAdmin', (res.data.role === 'admin').toString());
          // 记住我逻辑
          if (this.rememberMe) {
            localStorage.setItem('rememberedEmail', this.credentials.email);
          } else {
            localStorage.removeItem('rememberedEmail');
          }
          // 更新用户store
          const userStore = useUserStore();
          console.log('后端返回 userId:', res.data.userId)
          await userStore.updateUser({
            name: res.data.username,
            email: this.credentials.email,
            avatar: res.data.avatar,
            role: res.data.role,
            userId: res.data.userId
          });
          console.log('Pinia userId:', userStore.userId)
          await this.$nextTick();
          // 根据角色跳转
          if (res.data.role === 'admin') {
            await this.$router.push('/admin');
          } else {
            await this.$router.push('/home');
          }
        } else {
          this.error = res.msg || '用户名或密码错误';
        }
      } catch (err) {
        this.error = err.message || '用户名或密码错误';
      } finally {
        this.isLoading = false;
      }
    },
    handleForgotPassword() {
      this.showForgotDialog = true;
      this.forgotEmail = '';
      this.forgotCode = '';
      this.forgotNewPwd = '';
      this.forgotConfirmPwd = '';
      this.forgotError = '';
      this.forgotSuccess = '';
      this.forgotCodeCountdown = 0;
      if (this.forgotCodeTimer) clearInterval(this.forgotCodeTimer);
    },
    async sendForgotCode() {
      if (!this.forgotEmail.match(/^\S+@\S+\.\S+$/)) {
        this.forgotError = '请输入有效的邮箱';
        return;
      }
      this.forgotError = '';
      this.forgotCodeSending = true;
      try {
        await request.post('/api/user/forgot-password/code', { email: this.forgotEmail });
        this.forgotCodeCountdown = 60;
        this.forgotCodeTimer = setInterval(() => {
          this.forgotCodeCountdown--;
          if (this.forgotCodeCountdown <= 0) {
            clearInterval(this.forgotCodeTimer);
            this.forgotCodeTimer = null;
          }
        }, 1000);
      } catch (err) {
        this.forgotError = err.message || '验证码发送失败';
      } finally {
        this.forgotCodeSending = false;
      }
    },
    async handleResetPassword() {
      this.forgotError = '';
      this.forgotSuccess = '';
      if (!this.forgotEmail.match(/^\S+@\S+\.\S+$/)) {
        this.forgotError = '请输入有效的邮箱';
        return;
      }
      if (!this.forgotCode.trim()) {
        this.forgotError = '请输入验证码';
        return;
      }
      if (this.forgotNewPwd.length < 6) {
        this.forgotError = '新密码长度至少为6位';
        return;
      }
      if (this.forgotNewPwd !== this.forgotConfirmPwd) {
        this.forgotError = '两次输入的新密码不一致';
        return;
      }
      try {
        await request.post('/api/user/forgot-password/reset', {
          email: this.forgotEmail,
          code: this.forgotCode,
          newPassword: this.forgotNewPwd
        });
        this.forgotSuccess = '密码重置成功，请使用新密码登录！';
        setTimeout(() => {
          this.showForgotDialog = false;
        }, 2000);
      } catch (err) {
        this.forgotError = err.message || '重置失败';
      }
    },
    goToRegister() {
      this.$router.push('/register');
    }
  },
  mounted() {
    // 检查是否有记住的登录信息
    const rememberedEmail = localStorage.getItem('rememberedEmail');
    
    if (rememberedEmail) {
      this.credentials.email = rememberedEmail;
      this.rememberMe = true;
    }
  },
  beforeUnmount() {
    if (this.forgotCodeTimer) clearInterval(this.forgotCodeTimer);
  }
}
</script>

<style scoped>
.form-wrapper {
  background: rgba(255, 255, 255, 0.92);
  padding: 40px;
  border-radius: 16px;
  box-shadow: var(--shadow);
  width: 100%;
  max-width: 440px;
  animation: slideUp 0.6s cubic-bezier(0.18, 0.89, 0.32, 1.28);
}

.logo-container {
  text-align: center;
  margin-bottom: 30px;
}

.logo {
  height: 75px;
  margin-bottom: 15px;
}

.logo-container h2 {
  color: var(--text-dark);
  font-size: 26px;
  margin-top: 10px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.auth-form {
  width: 100%;
}

.form-group {
  position: relative;
  margin-bottom: 22px;
}

.input-icon {
  position: absolute;
  left: 18px;
  top: 50%;
  transform: translateY(-50%);
  height: 20px;
  width: 20px;
  color: #8c9aaf;
}

.form-input {
  width: 100%;
  padding: 16px 20px 16px 50px;
  border: 2px solid #e1e5eb;
  border-radius: 12px;
  font-size: 16px;
  transition: all 0.3s ease;
  height: 56px;
  background-color: #f8f9fc;
}

.form-input:focus {
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.15);
  outline: none;
  background-color: white;
}

.action-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 18px 0 24px;
  font-size: 14.5px;
}

.remember {
  display: flex;
  align-items: center;
  color: var(--text-dark);
  cursor: pointer;
}

.remember input {
  margin-right: 8px;
  accent-color: var(--primary);
}

.forgot-link {
  color: var(--primary);
  text-decoration: none;
  font-weight: 500;
  transition: all 0.2s;
}

.forgot-link:hover {
  color: var(--primary-dark);
  text-decoration: underline;
}

.submit-btn {
  background: linear-gradient(135deg, var(--primary), var(--primary-dark));
  color: white;
  border: none;
  border-radius: 12px;
  padding: 17px;
  width: 100%;
  font-size: 17px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(52, 152, 219, 0.3);
  position: relative;
  overflow: hidden;
}

.submit-btn span {
  position: relative;
  z-index: 1;
}

.submit-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, var(--primary-dark), var(--primary));
  opacity: 0;
  transition: opacity 0.3s ease;
}

.submit-btn:hover::before {
  opacity: 1;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(52, 152, 219, 0.4);
}

.submit-btn:active {
  transform: translateY(0);
}

.footer-links {
  margin-top: 30px;
  text-align: center;
  color: var(--text-dark);
  font-size: 15px;
}

.footer-links a {
  color: var(--primary);
  font-weight: 600;
  margin-left: 5px;
  text-decoration: none;
  transition: all 0.2s;
}

.footer-links a:hover {
  color: var(--primary-dark);
  text-decoration: underline;
}

.error-message {
  display: flex;
  align-items: center;
  background: #fee;
  color: #c33;
  padding: 12px 16px;
  border-radius: 8px;
  margin-top: 20px;
  font-size: 14px;
  border: 1px solid #fcc;
}

.error-icon {
  margin-right: 8px;
  width: 20px;
  height: 20px;
}

.success-message {
  background: #efe;
  color: #363;
  padding: 12px 16px;
  border-radius: 8px;
  margin-top: 20px;
  font-size: 14px;
  border: 1px solid #cfc;
}

.welcome-message {
  display: flex;
  align-items: center;
  background: #efe;
  color: #363;
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 20px;
  font-size: 14px;
  border: 1px solid #cfc;
}

.check-icon {
  width: 20px;
  height: 20px;
  background: #4caf50;
  border-radius: 50%;
  margin-right: 8px;
  position: relative;
}

.check-icon::after {
  content: '✓';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 12px;
  font-weight: bold;
}

.forgot-dialog-bg {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.forgot-dialog {
  background: white;
  padding: 30px;
  border-radius: 16px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.forgot-dialog h3 {
  margin: 0 0 20px 0;
  color: var(--text-dark);
  text-align: center;
}

.forgot-code-group {
  display: flex;
  gap: 10px;
}

.forgot-code-group .form-input {
  flex: 1;
}

.send-code-btn {
  background: var(--primary);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 0 15px;
  font-size: 14px;
  cursor: pointer;
  white-space: nowrap;
  transition: background 0.3s ease;
}

.send-code-btn:hover:not(:disabled) {
  background: var(--primary-dark);
}

.send-code-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.dialog-actions {
  display: flex;
  gap: 15px;
  margin-top: 20px;
}

.dialog-actions .submit-btn {
  flex: 1;
}

.cancel-btn {
  background: #6c757d;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 12px 20px;
  cursor: pointer;
  transition: background 0.3s ease;
}

.cancel-btn:hover {
  background: #5a6268;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 480px) {
  .form-wrapper {
    padding: 30px 20px;
    margin: 20px;
  }
  
  .action-row {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .forgot-code-group {
    flex-direction: column;
  }
  
  .send-code-btn {
    padding: 12px;
  }
  
  .dialog-actions {
    flex-direction: column;
  }
}
</style>