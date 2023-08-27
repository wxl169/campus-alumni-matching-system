<template>
      <van-skeleton title avatar :row="3" :loading="props.loading" v-for="user in props.userList">
      <van-card
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
      <van-button size="mini">联系我</van-button>
    </template>
  </van-card>
  </van-skeleton>
  <van-empty v-if="!userList || userList.length < 1" description="数据为空" />
</template>

<script setup lang="ts">
import { UserType } from '../models/user';


interface AddUserListProps{
  loading : boolean;
  userList : UserType[];
}

const props = withDefaults(defineProps<AddUserListProps>(),{
    loading : true,
    userList:[] as UserType[],
});
</script>