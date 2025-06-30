<template>
  <div class="form-wrapper">
    <div class="logo-container">
      <img src="../assets/logo.jpg" alt="Logo" class="logo">
      <h2>创建新账户</h2>
    </div>

    <form @submit.prevent="handleSubmit" class="auth-form">
      <div class="form-group">
        <div class="input-icon">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 12a5 5 0 1 0-5-5 5 5 0 0 0 5 5zm6 2a1 1 0 0 0-1-1H7a1 1 0 0 0-1 1 7 7 0 0 0 7 7 7 7 0 0 0 7-7z"/>
          </svg>
        </div>
        <input
            type="text"
            v-model="userData.username"
            placeholder="用户名"
            required
            class="form-input"
        >
      </div>

      <div class="form-group">
        <div class="input-icon">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
            <path d="M20 4H4a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V6a2 2 0 0 0-2-2zm0 4.73l-8 4.74-8-4.74V6l8 4.73L20 6z"/>
          </svg>
        </div>
        <input
            type="email"
            v-model="userData.email"
            placeholder="电子邮箱"
            required
            class="form-input"
        >
      </div>

      <div class="form-group" style="display: flex; gap: 10px; align-items: center;">
        <input
          type="text"
          v-model="userData.code"
          placeholder="验证码"
          required
          class="form-input"
          style="flex: 1;"
        >
        <button type="button" class="send-code-btn" :disabled="codeSending || codeCountdown > 0 || !userData.email.match(/^[^\s@]+@[^\s@]+\.[^\s@]+$/)" @click="sendRegisterCode">
          {{ codeCountdown > 0 ? codeCountdown + 's后重发' : (codeSending ? '发送中...' : '发送验证码') }}
        </button>
      </div>

      <div class="form-group">
        <div class="input-icon">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
            <path d="M18 8h-1V6A6 6 0 0 0 6 6v2H5a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V10a2 2 0 0 0-2-2zM8 6a4 4 0 0 1 8 0v2H8zm4 10a2 2 0 1 1 2-2 2 2 0 0 1-2 2z"/>
          </svg>
        </div>
        <input
            type="password"
            v-model="userData.password"
            placeholder="密码"
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
            v-model="userData.confirmPassword"
            placeholder="确认密码"
            required
            class="form-input"
        >
      </div>

      <div class="terms">
        <label>
          <input type="checkbox" required v-model="agreedToTerms">
          我已阅读并同意 <a href="#">服务条款</a> 和 <a href="#">隐私政策</a>
        </label>
      </div>

      <button type="submit" class="submit-btn">
        <span>{{ isSubmitting ? '创建中...' : '创建账户' }}</span>
      </button>

      <div v-if="successMessage" class="success-message">
        <div class="success-icon">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2a10 10 0 1 0 10 10A10 10 0 0 0 12 2zm-1.71 14.29-4-4a1 1 0 0 1 1.42-1.42l3.29 3.3 6.29-6.3a1 1 0 1 1 1.42 1.42l-7 7a1 1 0 0 1-1.42 0z"/>
          </svg>
        </div>
        <div>{{ successMessage }}</div>
      </div>

      <div v-if="error" class="error-message">
        <div class="error-icon">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
            <path d="M11.29 15.29a1 1 0 0 0 1.42 0 1 1 0 0 0 0-1.42L13.41 13l1.3-1.29a1 1 0 0 0-1.42-1.42L12 11.59l-1.29-1.3a1 1 0 0 0-1.42 1.42l1.3 1.29-1.3 1.29a1 1 0 0 0 0 1.42 1 1 0 0 0 1.42 0l1.29-1.3 1.29 1.3zM12 2a10 10 0 1 0 10 10A10 10 0 0 0 12 2zm0 18a8 8 0 1 1 8-8 8 8 0 0 1-8 8z"/>
          </svg>
        </div>
        {{ error }}
      </div>
    </form>

    <div class="footer-links">
      <p>已有账号? <a href="#" @click.prevent="goToLogin">立即登录</a></p>
    </div>
  </div>
</template>

<script>
import { request } from '@/utils/request'
export default {
  data() {
    return {
      userData: {
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
        code: ''
      },
      agreedToTerms: false,
      error: '',
      successMessage: '',
      isSubmitting: false,
      codeSending: false,
      codeCountdown: 0,
      codeTimer: null
    }
  },
  methods: {
    validateForm() {
      if (!this.userData.username.trim()) {
        this.error = '请输入用户名';
        return false;
      }

      if (!this.userData.email.match(/^[^\s@]+@[^\s@]+\.[^\s@]+$/)) {
        this.error = '请输入有效的电子邮箱';
        return false;
      }

      if (this.userData.password.length < 6) {
        this.error = '密码长度至少为6个字符';
        return false;
      }

      if (this.userData.password !== this.userData.confirmPassword) {
        this.error = '两次输入的密码不一致';
        return false;
      }

      if (!this.agreedToTerms) {
        this.error = '请阅读并同意服务条款和隐私政策';
        return false;
      }

      return true;
    },

    async sendRegisterCode() {
      if (!this.userData.email.match(/^[^\s@]+@[^\s@]+\.[^\s@]+$/)) {
        this.error = '请输入有效的邮箱';
        return;
      }
      this.error = '';
      this.codeSending = true;
      try {
        const res = await request.post('/api/user/register/code', { email: this.userData.email });
        if (res.code === 200) {
          this.codeCountdown = 60;
          this.codeTimer = setInterval(() => {
            this.codeCountdown--;
            if (this.codeCountdown <= 0) {
              clearInterval(this.codeTimer);
              this.codeTimer = null;
            }
          }, 1000);
        } else {
          this.error = res.msg || '验证码发送失败';
        }
      } catch (err) {
        this.error = err.message || '验证码发送失败';
      } finally {
        this.codeSending = false;
      }
    },

    async handleSubmit() {
      this.error = '';
      this.successMessage = '';

      if (!this.validateForm()) return;

      this.isSubmitting = true;

      try {
        const res = await request.post('/api/user/register', {
          username: this.userData.email,
          password: this.userData.password,
          email: this.userData.email,
          code: this.userData.code
        });

        if (res.code === 200) {
          this.isSubmitting = false;
          this.successMessage = '账户创建成功！即将跳转到登录页面...';
          setTimeout(() => {
            this.$router.push({
              path: '/login',
              query: {
                registered: 'true',
                email: this.userData.email
              }
            });
          }, 3000);
        } else {
          this.isSubmitting = false;
          this.error = res.msg || '注册失败，请重试。';
        }
      } catch (err) {
        this.isSubmitting = false;
        this.error = '注册失败，请重试。错误信息：' + (err.message || '未知错误');
      }
    },

    goToLogin() {
      this.$router.push('/login');
    }
  },
  beforeUnmount() {
    if (this.codeTimer) clearInterval(this.codeTimer);
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
  margin-bottom: 18px;
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

.terms {
  font-size: 14px;
  color: var(--text-dark);
  margin: 15px 0 25px;
  line-height: 1.5;
}

.terms a {
  color: var(--primary);
  text-decoration: none;
  font-weight: 500;
}

.terms a:hover {
  text-decoration: underline;
}

.terms label {
  display: flex;
  align-items: flex-start;
  cursor: pointer;
}

.terms input {
  margin-right: 10px;
  margin-top: 3px;
  accent-color: var(--primary);
}

.submit-btn {
  background: linear-gradient(135deg, #27ae60, #2ecc71);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 17px;
  width: 100%;
  font-size: 17px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(39, 174, 96, 0.3);
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
  background: linear-gradient(135deg, #2ecc71, #27ae60);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.submit-btn:hover::before {
  opacity: 1;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(39, 174, 96, 0.4);
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
  background: rgba(231, 76, 60, 0.1);
  border: 1px solid rgba(231, 76, 60, 0.2);
  border-radius: 10px;
  padding: 15px;
  display: flex;
  align-items: center;
  margin-top: 25px;
  color: var(--error);
  font-size: 14.5px;
}

.error-message .error-icon {
  margin-right: 10px;
  flex-shrink: 0;
}

.error-message svg {
  width: 20px;
  height: 20px;
}

.success-message {
  background: rgba(39, 174, 96, 0.1);
  border: 1px solid rgba(39, 174, 96, 0.2);
  border-radius: 10px;
  padding: 15px;
  display: flex;
  align-items: center;
  margin-top: 25px;
  color: var(--success);
  font-size: 14.5px;
  animation: fadeIn 0.3s ease;
}

.success-message .success-icon {
  margin-right: 10px;
  flex-shrink: 0;
}

.success-message svg {
  width: 20px;
  height: 20px;
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

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(5px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 480px) {
  .form-wrapper {
    padding: 30px 20px;
  }

  .form-input {
    padding: 14px 20px 14px 45px;
    height: 50px;
    font-size: 15px;
  }

  .terms {
    font-size: 13px;
  }

  .submit-btn {
    padding: 15px;
  }
}
</style>