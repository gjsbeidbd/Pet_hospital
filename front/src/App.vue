<script setup>
// App.vue 中不应定义路由守卫，路由守卫应在路由配置中定义
</script>

<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<style>
#app {
  height: 100vh;
  width: 100vw;
  overflow: hidden;
  margin: 0;
  padding: 0;
}
</style>