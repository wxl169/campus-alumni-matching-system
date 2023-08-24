<template>
   <template v-if="user">
   <van-cell title="昵称" is-link  :value="user.username" @click="toEdit('username','昵称',user.username)"/>
      <van-cell title="账号" :value="user.userAccount" />
      <van-cell title="头像" is-link @click="toEdit('avatarUrl','头像',user.avatarUrl)">
         <img style="height: 50ph;width: 40px;" :src="user.avatarUrl" />
      </van-cell>

      <template v-if="user.gender == 0">
         <van-cell title="性别" is-link  value="男"  @click="toEdit('gender','性别',user.gender)"/>
      </template>
      <template v-if="user.gender == 1">
      <van-cell title="性别" is-link  value="女"  @click="toEdit('gender','性别',user.gender)"/>
      </template>
      <van-cell title="电话" is-link  :value="user.phone" @click="toEdit('phone','电话',user.phone)"/>
      <van-cell title="邮箱" is-link  :value="user.email" @click="toEdit('email','邮箱',user.email)"/>
      <van-cell title="注册时间" :value="user.createTime" />
   </template>

   <van-button  type="danger" size="large"  style="margin-top: 50px;" @click="doLogout">退出登录</van-button>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import {  useRouter } from "vue-router";
import {getCurrentUser} from "../../services/user";
import { showFailToast } from 'vant';
import { showConfirmDialog } from 'vant';
import myAxios from "../../plugins/myAxios";


    const user = ref();
   onMounted(async () => {
      user.value = await getCurrentUser();
   })

   const router = useRouter();
   const toEdit = (editKey: string , editName: string, currentValue: string)=>{
      router.push({
         path:'/user/edit',
         query:{
             editKey,
             editName,
             currentValue,  
         }
      })
}

const doLogout = async () => {
    showConfirmDialog({
        title: '是否退出登录！',
    })
        .then(async () => {
            const res = await myAxios.post("/user/logout");
            if (res?.code === 0) {
               router.push('/user/login')
            } else {
                if (res.description == "") {
                    showFailToast("退出失败!");
                } else {
                    showFailToast(res.description);
                }

            }
        })
        .catch(() => {
            // on cancel
        });
}
</script >

<style scoped>

</style>