<template>
  <el-container direction=horizon >
      <NavBar></NavBar><!-- 引入导航栏 -->
      <router-view></router-view> <!-- 路由中定义的component决定显示什么 -->
  </el-container>
  
</template>

<script setup>
import NavBar from "@/components/NavBar.vue"

</script>

<style scoped>
.page-body {
  display: flex;
}
</style>
