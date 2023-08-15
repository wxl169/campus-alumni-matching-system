<template>
 <van-nav-bar
  title="标题"  
  left-arrow
  @click-left="onClickLeft"
  @click-right="onClickRight"
  fixed="true"
 >
  <template #right>
    <van-icon name="search" size="18" />
  </template>
</van-nav-bar>

<div id="content">
  <!-- 路由 -->
  <router-view/>
</div>

 <!-- v-model="active" @change="onChange" -->
<van-tabbar route>
  <van-tabbar-item icon="home-o" name="index" replace to="/">主页</van-tabbar-item>
  <van-tabbar-item icon="search" name="team" replace to="team">队伍</van-tabbar-item>
  <van-tabbar-item icon="friends-o" name="user" replace to="user">个人</van-tabbar-item>
</van-tabbar>

</template>

<script setup>
import { ref } from 'vue';
import { showToast } from 'vant';
import { useRouter } from 'vue-router';

const router = useRouter()

 const onClickLeft = () => {
  router.back();
 };
 const onClickRight = () => {
  router.push('/search')
 };

//  const active = ref("index");
//  const onChange = (index) => showToast(`标签 ${index}`);
</script>
<style scoped>
  #content{
    padding-top: 50px;
    padding-bottom: 50px;
  }
</style>