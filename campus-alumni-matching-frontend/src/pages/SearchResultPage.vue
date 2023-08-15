<template>
 <user-card-list :user-list="userList"/>

<!-- <van-empty description="描述文字" /> -->


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
  const userListData = await  myAxios.get('/user/search/tags', {
    params: {
      tagNameList : tags
    },
    paramsSerializer: params=>{
    return qs.stringify(params,{indices:false})
  }
})
  .then(function (response) {
    return response?.data
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