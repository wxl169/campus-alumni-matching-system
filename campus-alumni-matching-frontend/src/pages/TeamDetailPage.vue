<template>
    <van-divider>加入成员</van-divider>
    <div>
        <div>
            <van-grid gutter="6">
                <van-space wrap>
                    <van-grid-item v-for="user in userList">
                        <van-image :src="user.avatarUrl" round width="3.5rem" height="3.5rem" />
                        {{ user.username }}
                    </van-grid-item>
                </van-space>
            </van-grid>
        </div>
        <van-divider>操作</van-divider>
        <div>
            <van-cell center title="群聊名称"   :value="teamName" size="large" />
            <van-cell center title="群公告" value=""   size="large" :label="description" />
            <van-cell title="我在群里的昵称" :value="username" size="large"/>
            <van-space direction="vertical" fill style="margin-top: 20px;">
                <van-cell title="修改队伍信息" is-link @click="showPopup" />
                <van-popup v-model:show="show" :style="{ padding: '64px' }">内容</van-popup>
                <van-button type="danger" size="large" @click="doQuitTeam(userId)">退出队伍</van-button>
            </van-space>
        </div>
    </div>
</template>
    
<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import { getCurrentUser } from "../services/user";
import { getTeamById } from "../services/team";
import { onMounted, ref } from "vue";
import myAxios from "../plugins/myAxios";
import {  showFailToast } from 'vant';


const router = useRouter()
const route = useRoute()

//队伍名
const teamName = ref("");
const description =ref("");
const username = ref("");
const userId = ref(0);
// 当前用户信息
const currentUser = ref(null);
const teamAndUser = ref(null);
const teamId = route.params.id;
const userList = ref([]);



onMounted(async () => {
    currentUser.value = await getCurrentUser();
    teamAndUser.value = await getTeamById(teamId);
    teamName.value = teamAndUser.value.teamName;
    description.value = teamAndUser.value.description
    userList.value = teamAndUser.value.userList;
    username.value = currentUser.value.username;
    userId.value = currentUser.value.id;
})

const doQuitTeam = async (id: number) => {
  const res = await myAxios.post('/team/quit', {
    teamId: id
  });
  if (res?.code === 0) {
    route.push("/");
  } else {
    showFailToast('操作失败' + (res.description ? `，${res.description}` : ''));
  }
}

</script>
    
    