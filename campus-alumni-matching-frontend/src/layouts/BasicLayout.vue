<template>
  <van-nav-bar :title="title"  fixed="true"  v-if="route.meta.showHeader">
    <template #left v-if="route.meta.showBack">
      <van-icon name="arrow-left" size="18"  @click="onClickLeft" />
    </template>
    <template #right v-if="route.meta.showRight">
      <van-icon name="search" size="18"  @click="onClickRight"/>
    </template>
  </van-nav-bar>


  <div id="content">
    <!-- 路由 -->
    <router-view />
  </div>

  <!-- v-model="active" @change="onChange" -->
  <van-tabbar v-if="route.meta.showBottom" route>
    <van-tabbar-item icon="wap-home-o" name="index" replace to="/">主页</van-tabbar-item>
    <van-tabbar-item icon="smile-comment-o" name="team" replace to="" badge="99+">消息</van-tabbar-item>
    <van-tabbar-item icon="friends-o" name="user" replace to="/userTeam">通讯录</van-tabbar-item>
    <van-tabbar-item icon="user-o" name="user" replace to="/user/home">我</van-tabbar-item>
  </van-tabbar>
</template>

<script setup>
import { ref } from 'vue';
import { showToast } from 'vant';
import routes from "../config/route";
import { useRouter,useRoute} from "vue-router";

const route = useRoute();
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