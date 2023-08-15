<template>
    <van-card
    v-for="user in userList"
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
</template>
<script setup lang="ts">
import { UserType } from '../models/user';


interface UserCardListProps{
    userList:UserType[];
}

const props = withDefaults(defineProps<UserCardListProps>(),{
    userList:[] as UserType[],
});

</script>