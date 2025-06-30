<template>
  <div id="app">
    <NavBar v-if="showNavBar" />
    <router-view />
  </div>
</template>

<script>
import NavBar from '@/components/NavBar.vue';
export default {
  components: { NavBar },
  computed: {
    showNavBar() {
      // 在登录和注册页面不显示导航栏
      return !['Login', 'Register'].includes(this.$route.name);
    }
  }
}
</script>

<style>
@import '@/assets/global.css';
</style>