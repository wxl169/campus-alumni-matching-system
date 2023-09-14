<template>
    <van-space direction="vertical" fill v-for="message in messageList">
        <van-cell :border="false" is-link center
            @click="toUserChatRoom(message.sendUserId === userId ? message.receiveUserId : message.sendUserId)">
            <template #title>
                <!-- 用户卡片 -->
                <van-row :wrap="false" align="center">
                    <!-- 左侧头像 -->
                    <van-col>
                        <van-image :src="message.sendUserName == null ? message.receiveUserAvatar : message.sendUserAvatar"
                            fit="cover" height="60px" round width="60px" />
                    </van-col>
                    <!-- 右侧信息 -->
                    <van-col offset="1">
                        <!-- 昵称 -->
                        <div class="username" style="display: flex; justify-content: space-between;">
                            <div class="username" style="margin-right: 50px;">
                                {{ message.sendUserName == null ? message.receiveUserName : message.sendUserName }}
                            </div>
                            <div class="sendTime">
                                {{ formatTime(message.sendTime) }}
                            </div>
                        </div>
                        <!-- 消息 -->
                        <div class="profile"
                            style="color: rgb(145, 147, 149);text-align: left; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                            {{ message.content}}
                            <van-icon badge="99+" style="margin-left: 140px;" v-if="message.status === 0"/>
                        </div>
                    </van-col>
                </van-row>
            </template>
            <!-- 右侧内容 -->
        </van-cell>
    </van-space>
    <van-empty v-if="(!messageList || messageList.length < 1)" description="暂无消息记录" />
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { showFailToast } from 'vant';
import myAxios from "../../plugins/myAxios";
import { getCurrentUser } from "../../services/user";
import { useRouter } from "vue-router";

const messageList = ref([])
const currentUser = ref(null);
const userId = ref(0);
const router = useRouter();


onMounted(async () => {
    currentUser.value = await getCurrentUser();
    userId.value = currentUser.value.id;

    const res = await myAxios({
        url: '/message/user/get/notRead',
        method: "get",
    });
    if (res.code === 0) {
        messageList.value = res.data
    } else {
        showFailToast(res.description);
    }
})

const toUserChatRoom = (friendId: number) => {
    router.push({
        path: '/user/chatRoom/' + friendId,
    });
}



const formatTime = (time: string | number | Date) => {
    const date = new Date(time);
    const currentDate = new Date();
    const diffInDays = Math.floor((currentDate.getTime() - date.getTime()) / (1000 * 60 * 60 * 24));

    if (diffInDays === 0) {
        const hours = date.getHours();
        const period = hours >= 12 ? '下午' : '上午';
        const formattedHours = (hours % 12 || 12).toString().padStart(2, '0');
        const minutes = date.getMinutes().toString().padStart(2, '0');
        const seconds = date.getSeconds().toString().padStart(2, '0');
        return `${period}${formattedHours}:${minutes}:${seconds}`;
    } else if (diffInDays <= 7) {
        const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
        return weekdays[date.getDay()];
    } else {
        const month = (date.getMonth() + 1).toString().padStart(2, '0');
        const day = date.getDate().toString().padStart(2, '0');
        return `${month}月${day}日`;
    }
}

</script>