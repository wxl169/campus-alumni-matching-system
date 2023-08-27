<template>
<van-tabs v-model:active="active">
  <van-tab v-for="index in 2">
    <template #title v-if="index === 1"> 
       <van-icon name="contact" />用户推荐
    </template>

    <template #title v-if="index === 2"> 
       <van-icon name="friends-o" />队伍推荐
    </template>
    <UserCardList :user-list="userList" :loading="loading_user" v-if="index === 1"/>
    <TeamCardList :team-list="teamList" :loading="loading_team" v-if="index === 2"/>
    <!-- <van-empty v-if="(!userList || userList.length < 1) && (!teamList || teamList.length < 1)" description="数据为空"/> -->
  </van-tab>

</van-tabs>
  
  </template>
  
  <script setup lang="ts">
  import { ref, watchEffect } from 'vue';
  import myAxios from "../plugins/myAxios"
  import { showFailToast } from 'vant';
  import UserCardList from '../components/UserCardList.vue';
  import TeamCardList from '../components/TeamCardList.vue';
  import {UserType} from "../models/user";

/**
 * 推荐栏
 */

const active = ref(0)
const userList = ref([]);
const loading_user = ref(true);
const loading_team = ref(true);
const teamList = ref([]);


const loadData_user = async () => {
  let userListData;
  loading_user.value = true;
  // 心动模式，根据标签匹配用户
    const pageNum = 1;
    const pageSize = 10;
    userListData = await myAxios.get('/user/match', {
      params: {
        pageNum,
        pageSize,
      },
    })
        .then(function (response) {
          return response?.data.rows;
        })
        .catch(function (error) {
          showFailToast("请求失败"+error)
        })
  if (userListData) {
    userListData.forEach((user: UserType) => {
      if (user.tags) {
        user.tags = JSON.parse(user.tags);
      }
    })
    userList.value = userListData;
  }
  loading_user.value = false;
}
/**
 * 加载队伍信息
 */
const loadData_team = async () => {
  let teamListData;
  loading_team.value = true;
  // 心动模式，根据标签匹配用户
    const pageNum = 1;
    const pageSize = 10;
    teamListData = await myAxios.get('/team/match', {
      params: {
        pageNum,
        pageSize,
      },
    })
        .then(function (response) {
          return response?.data.rows;
        })
        .catch(function (error) {
          showFailToast("请求失败"+error)
        })
  if (teamListData) {
    // teamListData.forEach((user: UserType) => {
    //   if (user.tags) {
    //     user.tags = JSON.parse(user.tags);
    //   }
    // })
    teamList.value = teamListData;
  }
  loading_team.value = false;
}


watchEffect(() => {
  if(active.value == 0){
    loadData_user();
  }else{
    loadData_team();
  }
  
})

  
  
  </script>