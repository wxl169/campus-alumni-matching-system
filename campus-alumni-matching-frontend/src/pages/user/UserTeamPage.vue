<template>
    <div id="addUserOrTeam">
        <van-cell title="新的朋友" is-link to="/search" />
        <van-cell title="新的队伍" is-link to="/team" />
    </div>
    <div id="joinUserOrTeam">
        <van-tabs v-model:active="active">
            <van-tab v-for="index in 2">
                <template #title v-if="index === 1">
                    <van-icon name="contact" />关注列表
                </template>

                <template #title v-if="index === 2">
                    <van-icon name="friends-o" />群聊列表
                </template>
                <UserCardList :user-list="userList" :loading="loading_user" v-if="index === 1" />
                <JoinTeamList :team-list="teamList" :loading="loading_team" v-if="index === 2" />
                <!-- <van-empty v-if="(!userList || userList.length < 1) && (!teamList || teamList.length < 1)" description="数据为空" /> -->
            </van-tab>
        </van-tabs>
    </div>
    <div id="addTeam">

        <van-floating-bubble axis="xy" icon="friends-o" magnetic="x" v-model:offset="offset" @click="onClick" />
    </div>
</template>


<script setup lang="ts">
import { ref, watchEffect } from 'vue';
import myAxios from "../../plugins/myAxios"
import { showFailToast } from 'vant';
import JoinTeamList from "../../components/JoinTeamList.vue";
import UserCardList from "../../components/UserCardList.vue";
import {UserType} from "../../models/user";
import { useRouter } from "vue-router";

/**
 * 推荐栏
 */
 const router = useRouter();
const active = ref(0)
const userList = ref([]);
const loading_user = ref(true);
const loading_team = ref(true);
const teamList = ref([]);


const loadData_user = async () => {
  let userListData;
  loading_user.value = true;
  // 心动模式，根据标签匹配用户

    userListData = await myAxios.get('/user/list/friend')
        .then(function (response) {
          return response?.data;
        })
        .catch(function (error) {
          showFailToast("请求失败"+error)
        })
  if (userListData) {
    userListData.forEach((user: UserType) => {
      if (user.tags) {
        user.tags = JSON.parse(user.tags);
      }
    })
    userList.value = userListData;
  }
  loading_user.value = false;
}
/**
 * 加载队伍信息
 */
const loadData_team = async () => {
  let teamListData;
  loading_team.value = true;
  // 心动模式，根据标签匹配用户
    teamListData = await myAxios.get('/team/list/join')
        .then(function (response) {
          return response?.data;
        })
        .catch(function (error) {
          showFailToast("请求失败"+error)
        })
  teamList.value = teamListData;
  loading_team.value = false;
}


watchEffect(() => {
  if(active.value == 0){
    loadData_user();
  }else{
    loadData_team();
  }
  
})

const offset = ref({ y: 555 });
const onClick = () => {
    router.push({
        path: "/team/add"
    })
};
  
  </script>