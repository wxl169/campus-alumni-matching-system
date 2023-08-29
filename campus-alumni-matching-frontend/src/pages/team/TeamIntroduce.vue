<template>
    <van-divider>加入成员</van-divider>
    <div>
        <div>
            <van-grid gutter="6">
                <van-space wrap>
                    <van-grid-item v-for="user in userList">
                        <div style="width: 50px;height: 50px;" @click="toUserDetail(user.id)">
                            <van-image :src="user.avatarUrl" round width="3.5rem" height="3.5rem" />
                            {{ user.username }}
                        </div>
                    </van-grid-item>
                </van-space>
            </van-grid>
        </div>

        <div style="margin-top: 50px;">
            <van-divider>操作</van-divider>
            <van-cell center title="群聊名称" :value="teamName" size="large" />
            <van-cell center title="群公告" value="" size="large" :label="description"
                v-if="description != null && description != ''" />
            <van-cell center title="群公告" value="群主很懒，还没有群介绍哦" size="large"
                v-if="description == null || description == ''" />
            <van-cell center title="创建时间" :value="createTime" />
            <van-cell center title="过期时间" :value="expireTime" v-if="expireTime != null" />
            <van-cell center title="过期时间" value="暂未设置过期时间" v-if="expireTime == null" />
            <van-space direction="vertical" fill style="margin-top: 50px;">
                <van-button type="primary" size="large" @click="preJoinTeam(teamId, status)">加入队伍</van-button>
            </van-space>

            <van-dialog v-model:show="showPasswordDialog" title="请输入密码" show-cancel-button @confirm="doJoinTeam"
                @cancel="doJoinCancel">
                <van-field v-model="password" placeholder="请输入密码" />
            </van-dialog>

        </div>
    </div>
</template>
    
<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import { getCurrentUser } from "../../services/user";
import { getTeamById } from "../../services/team";
import { onMounted, ref } from "vue";
import myAxios from "../../plugins/myAxios";
import { showFailToast, showSuccessToast } from 'vant';

const router = useRouter()
const route = useRoute()

//队伍名
const teamName = ref("");
const description = ref("");
const username = ref("");
const leaderId = ref("");
const expireTime = ref();
const createTime = ref();
let teamId = ref(0)
const status = ref(0)
// 当前用户信息
const currentUser = ref(null);
const teamAndUser = ref(null);
const userList = ref([]);


onMounted(async () => {
    teamId = route.query.teamId;
    currentUser.value = await getCurrentUser();
    teamAndUser.value = await getTeamById(teamId);
    teamName.value = teamAndUser.value.teamName;
    description.value = teamAndUser.value.description;
    expireTime.value = teamAndUser.value.expireTime;
    userList.value = teamAndUser.value.userList;
    username.value = currentUser.value.username;
    leaderId.value = teamAndUser.value.leaderId;
    createTime.value = teamAndUser.value.createTime;
    status.value = teamAndUser.value.status;
})

const joinTeamId = ref(0);
const showPasswordDialog = ref(false);
const password = ref('');

const preJoinTeam = (teamId: number, status: number) => {
    console.log(status)
    joinTeamId.value = teamId;
    if (status === 0) {
        doJoinTeam()
    } else {
        showPasswordDialog.value = true;
    }
}

//清空数据
const doJoinCancel = () => {
    joinTeamId.value = 0;
    password.value = '';
}



/**
 * 加入队伍
 * @param teamId 
 */
const doJoinTeam = async () => {
    if (!joinTeamId.value) {
        return;
    }
    const res = await myAxios.post('/team/join', {
        teamId: joinTeamId.value,
        password: password.value
    });
    if (res?.code === 0) {
        showSuccessToast('加入成功');
        router.back();
    } else {
        showFailToast('加入失败' + (res.description ? `,${res.description}` : ''))
    }
}


/**
 * 点击头像进入用户详情
 */
const toUserDetail = (userId: number) => {
    if (userId != currentUser.value.id) {
        router.push({
            path: '/user/details',
            query: {
                userId
            }
        });
    }

}

</script>
    
    