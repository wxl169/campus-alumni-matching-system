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
            <van-cell title="我在群里的昵称" :value="username" size="large" />
            <van-space direction="vertical" fill style="margin-top: 20px;">
                <van-cell title="修改队伍信息" is-link  v-if="leaderId === currentUser?.id" @click="toUpdateTeam(teamId)"/>
                <van-cell title="解散队伍"  is-link  @click="doDeleteTeam(teamId)" v-if="leaderId === currentUser?.id"/>
                <van-button type="danger" size="large" @click="doQuitTeam(teamId)">退出队伍</van-button>
            </van-space>
        </div>
    </div>
</template>
    
<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import { getCurrentUser } from "../../services/user";
import { getTeamById } from "../../services/team";
import { onMounted, ref } from "vue";
import myAxios from "../../plugins/myAxios";
import { showFailToast } from 'vant';
import { showConfirmDialog } from 'vant';

const router = useRouter()
const route = useRoute()

//队伍名
const teamName = ref("");
const description = ref("");
const username = ref("");
const leaderId = ref("")
const expireTime = ref();
const createTime = ref();
let teamId = ref(0)
// 当前用户信息
const currentUser = ref(null);
const teamAndUser = ref(null);
const userList = ref([]);


onMounted(async () => {
    teamId = route.query.teamId;
    currentUser.value = await getCurrentUser();
    teamAndUser.value = await getTeamById(teamId);
    teamName.value = teamAndUser.value.teamName;
    description.value = teamAndUser.value.description
    userList.value = teamAndUser.value.userList;
    username.value = currentUser.value.username;
    leaderId.value = teamAndUser.value.leaderId;
    expireTime.value = teamAndUser.value.expireTime;
    createTime.value = teamAndUser.value.createTime;
})


/**
 * 退出队伍
 * @param teamId 
 */
const doQuitTeam = async (teamId: number) => {
    showConfirmDialog({
        title: '确认要退出队伍？',
    })
        .then(async () => {
            const res = await myAxios({
                url: '/team/quit',
                method: "post",
                params: {
                    teamId: teamId
                },
            });
            if (res?.code === 0) {
                router.push('/')
            } else {
                showFailToast('操作失败' + (res.description ? `${res.description}` : ''));
            }
        })
        .catch(() => {
            // on cancel
        });
}
/**
 * 解散队伍
 * @param teamId 
 */
const doDeleteTeam = async (teamId: number) => {
    showConfirmDialog({
        title: '确认要解散队伍？',
        message:'解散队伍后，所有成员将退出队伍，且操作不可逆'
    })
        .then(async () => {
            const res = await myAxios({
                url: '/team/delete',
                method: "delete",
                data: {
                    id: teamId
                },
            });
            if (res?.code === 0) {
                router.push('/')
            } else {
                showFailToast('操作失败' + (res.description ? `${res.description}` : ''));
            }
        })
        .catch(() => {
            // on cancel
        });
}

/**
 * 前往修改队伍信息界面
 */
 const toUpdateTeam = (teamId:number) =>{
  router.push({
    path:'/team/update',
    query:{
        teamId
    }
  });
};

/**
 * 点击头像进入用户详情
 */
 const toUserDetail = (userId:number) =>{
    if(userId != currentUser.value.id){
         router.push({
        path: '/user/details',
        query: {
            userId
        }
    });
    }

 }

</script>
    
    