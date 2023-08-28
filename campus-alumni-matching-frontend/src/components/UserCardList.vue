<template>
  <van-skeleton title avatar :row="3" :loading="props.loading" v-for="user in props.userList" class="skeleton">
    <van-cell :border="false" is-link center @click="toUserDetails(user.id)">
      <template #title>
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
            <div class="profile">简介：{{ user.profile }}</div>
            <!-- 标签 -->
            <van-space :size="5" wrap>
              <van-tag plain type="primary" v-for="tag in user.tags"  size="medium">
                {{ tag }}
              </van-tag>
            </van-space>
          </van-col>
        </van-row>
      </template>
      <!-- 右侧内容 -->
    </van-cell>
  </van-skeleton>
  <van-empty v-if="!userList || userList.length < 1" description="数据为空" />
</template>
<script setup lang="ts">
import { UserType } from '../models/user';
import {useRouter} from "vue-router";

const router = useRouter()


interface UserCardListProps {
  loading: boolean;
  userList: UserType[];
}

const props = withDefaults(defineProps<UserCardListProps>(), {
  loading: true,
  userList: [] as UserType[],
});

const toUserDetails = (userId : number) =>{
  router.push({
    path:'/user/details',
    query:{
      userId
    }
  });
}

</script>

<style scoped>
.username {
  line-height: 18px;
}

.profile {
  font-size: 10px;
  line-height: 30px;
  color: #969799;
}
</style>

    <!-- <van-card
    :thumb-link="user.id"
    :desc="`个人介绍：${user.profile}`"
    :title="`${user.username} (${user.userAccount})`"
    :thumb="user.avatarUrl"
  >
  
  <template #tag>
      <van-icon name="/male.svg" size="21"  v-if="user.gender === 0"/>
      <van-icon name="/female.svg" size="21"  v-if="user.gender === 1"/>
  </template>
  
    <template #tags>
      <van-tag plain type="primary" v-for="tag in user.tags" style="margin-right: 8px;margin-top: 5px;">
          {{tag}} 
      </van-tag>
    </template>
  
    <template #footer>
      <van-button size="mini">关注我</van-button>
    </template>
  </van-card> -->