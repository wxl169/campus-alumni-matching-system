<template>
        <template v-if="user">
            <van-card :thumb-link="user.id" :desc="`账号：${user.userAccount}`" :title="`${user.username}`"
                :thumb="user.avatarUrl">
                <template #tags>
                    <van-tag plain type="primary" v-for="tag in user.tags" style="margin-right: 8px;margin-top: 5px;">
                        {{ tag }}
                    </van-tag>
                </template>
            </van-card>

        <van-cell title="已加入队伍" is-link value="内容" />
        <van-cell title="单元格" is-link value="内容" />
        <van-cell title="单元格" is-link value="内容" />
        <van-cell title="单元格" is-link value="内容" />
    </template>
        

</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { getCurrentUser } from "../services/user";

const user = ref();
onMounted(async () => {
    user.value = await getCurrentUser();
    if (user.value.tags) {
        user.value.tags = JSON.parse(user.value.tags);
      }
})


</script>
