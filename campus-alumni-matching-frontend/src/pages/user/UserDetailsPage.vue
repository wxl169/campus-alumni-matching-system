<template >
  <template v-if="user">
    <van-space direction="vertical" fill :size="[0, 10]">
      <div class="head">
        <!-- 用户卡片 -->
        <van-row :wrap="false" align="center">
          <!-- 左侧头像 -->
          <van-col>
            <van-image :src="user.avatarUrl" fit="cover" height="100px" round width="100px" />
          </van-col>
          <!-- 右侧信息 -->
          <van-col offset="1">
            <!-- 昵称 -->
            <div class="username">昵称：{{ user.username }}</div>
            <div class="account">账号：{{ user.userAccount }}</div>
            <div class="gender" v-if="user.gender === 0">性别：男</div>
            <div class="gender" v-if="user.gender === 1">性别：女</div>
            <!-- 简介 -->
            <div class="profile">个人介绍：{{ user.profile }}</div>
          </van-col>
        </van-row>
      </div>
      <!-- 标签 -->
      <div class="center">
        标签：
        <van-space :size="5" wrap>
          <van-tag plain type="primary" v-for="tag in user.tags" size="large">
            {{ tag }}
          </van-tag>
        </van-space>

        <van-cell title="电子邮箱" :value="user.email" style="margin-top: 10px;" />
      </div>

      <div class="container">
        <van-space direction="vertical" fill :size="[0, 10]">
          <van-button type="danger" size="large" v-if="user.isFriend === 1" @click="doCancel(user.id)"> 取消关注 </van-button>

          <van-button plain type="primary" size="large" v-if="user.isFriend != 1" @click="doConcern(user.id)">
            <van-icon name="like" size="20px" /> 点击关注
          </van-button>
         
          <van-button plain type="primary" size="large" style="font-size:18px;" @click="sendMessage(user.id)">
            <van-icon name="comment-circle" size="20px" /> 发消息
          </van-button>
        </van-space>
      </div>

    </van-space>
  </template>
</template>


<script setup lang="ts">
import { useRouter, useRoute } from "vue-router";
import { onMounted, ref } from "vue";
import { getUserDetail } from "../../services/user";
import { showConfirmDialog, showFailToast, showSuccessToast } from 'vant';
import myAxios from "../../plugins/myAxios";


const router = useRouter()
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
/**
 * 取消关注
 * @param friendId 
 */
const doCancel = async (friendId: number) => {
  showConfirmDialog({
    title: '确认要取消关注吗',
  })
    .then(async () => {
      const res = await myAxios({
        url: '/user/delete/friend',
        method: "delete",
        params: {
          friendId: friendId
        },
      });
      if (res?.code === 0) {
        user.value.isFriend = 0;
        showSuccessToast("取消关注成功")
      } else {
        showFailToast('操作失败' + (res.description ? `${res.description}` : ''));
      }
    })
    .catch((error) => {
      showFailToast('操作失败' + (error));
    });
}
/**
 * 发送信息
 * @param friendId 
 */
const sendMessage = async (friendId: number) => {
  router.push({
    path: '/user/chatRoom',
    query: {
      friendId
    }
  });
}
/**
 * 
 * @param friendId 点击关注
 */
const doConcern = async (friendId: number) => {
  const res = await myAxios({
    url: '/user/add/friend',
    method: "post",
    params: {
      friendId: friendId
    },
  });
  if (res.data === true) {
    user.value.isFriend = 1;
    showSuccessToast("关注成功")
  } else {
    showFailToast(res.description);
  }
}
</script>

<style setup>
.head {
  margin-left: 20px;
}

.container {
  margin-top: 50px;
}
</style>