<template>
  <van-cell center title="心动模式" >
    <template #right-icon>
      <van-switch v-model="isMatchMode" size="24" />
    </template>
  </van-cell>

<UserCardList :user-list="userList" :loading="loading"/>
  <!-- <van-empty description="描述文字" /> -->
  <van-empty v-if="!userList || userList.length < 1" description="数据为空"/>
  </template>
  
  <script setup lang="ts">
  import { ref, watchEffect } from 'vue';
  import myAxios from "../plugins/myAxios"
  import { showFailToast } from 'vant';
  import UserCardList from '../components/UserCardList.vue';
  import {UserType} from "../models/user";
  


const isMatchMode = ref<boolean>(false);
const userList = ref([]);
const loading = ref(true);

  
/**
 * 加载数据
 */

const loadData = async () => {
  let userListData;
  loading.value = true;
  // 心动模式，根据标签匹配用户
  if (isMatchMode.value) {
    const pageNum = 1;
    const pageSize = 10;
    userListData = await myAxios.get('/user/match', {
      params: {
        pageNum,
        pageSize,
      },
    })
        .then(function (response) {
          console.log('/user/match succeed', response);
          return response?.data.rows;
        })
        .catch(function (error) {
          console.error('/user/match error', error);
          showFailToast("请求失败")
        })
  } else {
    // 普通模式，直接分页查询用户
    userListData = await myAxios.get('/user/recommend', {
      params: {
        pageNum: 1,
        pageSize: 10,
      },
    })
        .then(function (response) {
          return response?.data?.rows;
        })
        .catch(function (error) {
          console.error('/user/recommend error', error);
          showFailToast("请求失败")
        })
  }
  if (userListData) {
    userListData.forEach((user: UserType) => {
      if (user.tags) {
        user.tags = JSON.parse(user.tags);
      }
    })
    userList.value = userListData;
  }
  loading.value = false;
}

watchEffect(() => {
  loadData();
})

  
  
  
  
  </script>