<template>
  <div class="auth-container">
    <img class="full-bg" src="@/assets/harbor-background.png" alt="港口背景图">
    <div class="bg-overlay"></div>
    <RegisterForm />
  </div>
</template>

<script>
import RegisterForm from '@/components/RegisterForm.vue';

export default {
  name: 'RegisterView',
  components: {
    RegisterForm
  }
}
</script>