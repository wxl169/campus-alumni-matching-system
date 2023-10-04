<template>
    <user-card-list :user-list="userList" :loading="loading_user"/>
    </template>
    
    <script setup>
    import { ref,onMounted, watchEffect} from 'vue';
    import { useRoute } from 'vue-router';
    import myAxios from "../../plugins/myAxios"
    import { showFailToast } from 'vant';
    import qs from "qs";
    import UserCardList from '../../components/UserCardList.vue';
    
    
    const route = useRoute();
    const condition = route.query.condition;
    
    const userList = ref([]);
    const loading_user = ref(true);
    

    /**
     * 加载用户数据
     */
     const loadData_user = async () => {
      loading_user.value = true;
      const userListData = await  myAxios.get('/user/search/user', {
        params: {
            condition: condition
        },
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
      loading_user.value = false;
    }
    
    watchEffect(() => {
      loadData_user();
    })
    
    </script>