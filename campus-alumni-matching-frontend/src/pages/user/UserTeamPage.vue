<template>
    <div id="addUserOrTeam">
        <van-cell title="新的朋友" is-link to="/search" />
        <van-cell title="新的队伍" is-link to="/team" />
    </div>
    <div id="joinUserOrTeam">
        <van-tabs v-model:active="active" @change="onTabChange">
            <van-tab title="队伍" name="team" />
            <van-tab title="好友" name="user" />
        </van-tabs>

        <AddUserList :userList="userList" />
        <JoinTeamList :teamList="teamList" />
        <van-empty v-if="teamList?.length < 1 && userList?.length < 1" description="数据为空" />
    </div>
    <div id="addTeam">
        
        <van-floating-bubble axis="xy" icon="friends-o" magnetic="x" v-model:offset="offset"   @click="onClick" />

    </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
import JoinTeamList from "../../components/JoinTeamList.vue";
import AddUserList from "../../components/AddUserList.vue";
import { onMounted, ref } from "vue";
import myAxios from "../../plugins/myAxios";
import { showFailToast } from 'vant';


const active = ref('team')
const router = useRouter();

/**
 * 切换查询状态
 * @param name
 */
const onTabChange = (name: string) => {
    // 查公开
    if (name === 'user') {
        addUserList();
    } else {
        // 查加密
        joinTeamList();
    }
}


const teamList = ref([]);
const userList = ref([]);

// 页面加载时只触发一次
onMounted(() => {
    joinTeamList();
})

/**
 * 已加入的队伍
 */
const joinTeamList = async () => {
    const res = await myAxios.get("/team/list/join");
    if (res?.code === 0) {
        teamList.value = res.data;
    } else {
        showFailToast('加载队伍失败，请刷新重试');
    }
}

/**
 * 关注的好友
 */
const addUserList = async () => {
    const res = await myAxios.get("/team/list/join", {

    });
    if (res?.code === 0) {
        teamList.value = res.data;
    } else {
        showFailToast('加载队伍失败，请刷新重试');
    }
}

const offset = ref({y: 555 });
const onClick = () => {
    router.push({
        path: "/team/add"
    })
};
</script>