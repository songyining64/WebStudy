<template>
  <div class="auth-container">
    <img class="full-bg" src="@/assets/harbor-background.png" alt="港口背景图">
    <div class="bg-overlay"></div>
    <LoginForm />
  </div>
</template>

<script>
import LoginForm from '@/components/LoginForm.vue';

export default {
  name: 'LoginView',
  components: {
    LoginForm
  }
}
</script>