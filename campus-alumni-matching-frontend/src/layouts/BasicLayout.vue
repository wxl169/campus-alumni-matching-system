<template>
  <van-nav-bar :title="title" left-arrow @click-left="onClickLeft" @click-right="onClickRight" fixed="true">
    <template #right>
      <van-icon name="search" size="18" />
    </template>
  </van-nav-bar>


  <div id="content">
    <!-- 路由 -->
    <router-view />
  </div>

  <!-- v-model="active" @change="onChange" -->
  <van-tabbar route>
    <van-tabbar-item icon="records" name="index" replace to="/">计划</van-tabbar-item>
    <van-tabbar-item icon="friends-o" name="user" replace to="/userTeam">通讯录</van-tabbar-item>
    <van-tabbar-item icon="search" name="team" replace to="">发现</van-tabbar-item>
    <van-tabbar-item icon="user-o" name="user" replace to="">我</van-tabbar-item>
  </van-tabbar>
</template>

<script setup>
import { ref } from 'vue';
import { showToast } from 'vant';
import routes from "../config/route";
import { useRouter } from "vue-router";

const router = useRouter();
const DEFAULT_TITLE = '学友匹配';
const title = ref(DEFAULT_TITLE);

/**
 * 根据路由切换标题
 */
router.beforeEach((to, from) => {
  const toPath = to.path;
  const route = routes.find((route) => {
    return toPath == route.path;
  })
  title.value = route?.title ?? DEFAULT_TITLE;
})


const onClickLeft = () => {
  router.back();
};
const onClickRight = () => {
  router.push('/search')
};


</script>
<style scoped>
#content {
  padding-top: 50px;
  padding-bottom: 50px;
}
</style>