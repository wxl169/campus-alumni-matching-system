<template>
    <van-cell-group inset v-for="message in props.messageList">

        <!-- <van-cell title="我" :value="formatTime(message.sendTime)" :label="message.content" v-if="message.sendUserId === userId"/>
      <van-cell :title="message.sendUserName" :value="formatTime(message.sendTime)" :label="message.content" v-else/> -->

        <template #title>
            <!-- 用户卡片 -->
            <van-row :wrap="false" align="center">
                <!-- 左侧头像 -->
                <van-col>
                    <van-image :src="message.sendUserAvatar" fit="cover" height="60px" round width="60px" />
                </van-col>
                <!-- 右侧信息 -->
                <van-col offset="1">
                    <div class="sendTime" style="text-align: right;">{{ formatTime(message.sendTime) }}</div>
                    <!-- 昵称 -->
                    <div class="username" style="text-align: left;">{{ message.sendUserName }}</div>
                    <!-- 简介 -->
                    <div class="profile" style="text-align: left;">{{ message.content }}</div>
                    
                </van-col>
            </van-row>
        </template>

    </van-cell-group>
    <van-empty v-if="(!messageList || messageList.length < 1)" description="暂无消息记录" />
</template>
    
<script setup lang="ts">
import { onMounted, ref } from "vue";
import { TeamMessageType } from '../models/message';
import { getCurrentUser } from "../services/user";

const currentUser = ref();
const userId = ref(0);

interface TeamMessageListProps {
    loading: boolean;
    messageList: TeamMessageType[];
}

const props = withDefaults(defineProps<TeamMessageListProps>(), {
    loading: true,
    messageList: [] as TeamMessageType[],
});

onMounted(async () => {
    currentUser.value = await getCurrentUser();
    userId.value = currentUser.value.id;
})

const formatTime = (time: string | number | Date) => {
    const date = new Date(time);
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const seconds = date.getSeconds().toString().padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}
</script>