<template >
 <template v-if="user">
    <van-space direction="vertical" fill>
            <!-- 用户卡片 -->
        <van-row :wrap="false" align="center">
          <!-- 左侧头像 -->
          <van-col>
            <van-image :src="user.avatarUrl" fit="cover" height="60px" round width="60px" />
          </van-col>
          <!-- 右侧信息 -->
          <van-col offset="1">
            <!-- 昵称 -->
            <div class="username">{{ user.username || user.userAccount }}</div>
            <!-- 简介 -->
            <div class="profile">{{ user.profile }}</div>
            <!-- 标签 -->
            <van-space :size="5" wrap>
              <van-tag plain type="primary" v-for="tag in user.tags"  size="medium">
                {{ tag }}
              </van-tag>
            </van-space>
          </van-col>
        </van-row>
    </van-space>
    </template>
</template>


<script setup lang="ts">
import { useRoute } from "vue-router";
import { onMounted, ref } from "vue";
import { getUserDetail } from "../../services/user";


const route = useRoute()
let userId = ref(0)
const user = ref();

onMounted(async () => {
    userId = route.query.userId;
    user.value = await getUserDetail(userId);
    if (user.value.tags) {
        user.value.tags = JSON.parse(user.value.tags);
    }
})

// onMounted(async () => {
//     userId = route.query.userId;
//     console.log(userId)
//     await myAxios.get('/user/get', {
//         params: {
//             userId: userId
//         }
//     }).then(function (response) {
//         if(response.code === 0){
//         setUerDetails(response.data)
//         user.value = getUerDetails ;
//         }else{
//             showFailToast('查询失败' + response.description);
//         }
//     })
//         .catch(function (error) {
//             showFailToast('查询失败' + error);
//         })
// })
</script>