<template>
<UserCardList :user-list="userList"/>
  
  <!-- <van-empty description="描述文字" /> -->
  <van-empty v-if="!userList || userList.length < 1" description="数据为空"/>
  
  </template>
  
  <script setup>
  import { ref,onMounted} from 'vue';
  import { useRoute } from 'vue-router';
  import myAxios from "../plugins/myAxios"
  import { showSuccessToast, showFailToast } from 'vant';
  import qs from 'qs';
  import UserCardList from '../components/UserCardList.vue';
  
  
  
  const route = useRoute();
  const {tags} = route.query;
  
  const userList = ref([]);
  
  onMounted(async () => {
    const userListData = await  myAxios.get('/user/recommend', {
      params: {
        pageNum : 1,
        pageSize : 10,
      }
  })
    .then(function (response) {
      return response?.data.rows;
    })
    .catch(function (error) {
      showFailToast("请求失败")
    })
    if(userListData){
      userListData.forEach(user =>{
        if(user.tags){
          user.tags = JSON.parse(user.tags);
        }
      })
      userList.value = userListData;
    }
  })
  
  
  
  
  
  
  </script>