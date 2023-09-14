<template>
<van-cell-group inset v-for="message in props.messageList">
  <van-cell title="我" :value="formatTime(message.sendTime)" :label="message.content" v-if="message.sendUserId === userId"/>
  <van-cell :title="message.sendUserName" :value="formatTime(message.sendTime)" :label="message.content" v-else/>
</van-cell-group>
    <van-empty v-if="(!messageList || messageList.length < 1)" description="暂无消息记录" />
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { MessageType } from '../models/message';
import { getCurrentUser } from "../services/user";

const currentUser = ref();
const userId = ref(0);

interface UserMessageListProps {
    loading: boolean;
    messageList: MessageType[];
}

const props = withDefaults(defineProps<UserMessageListProps>(), {
    loading: true,
    messageList: [] as MessageType[],
});

onMounted(async () => {
    currentUser.value = await getCurrentUser();
    userId.value = currentUser.value.id;
})

const formatTime=(time: string | number | Date)=> {
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