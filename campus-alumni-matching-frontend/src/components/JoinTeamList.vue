<template>
  <van-card v-for="team in props.teamList" :title="`${team.teamName} `" :thumb="`${team.avatarUrl}`">
    <template #bottom>
      <div>
        <!-- {{ `队伍人数: ${team.hasJoinNum}/${team.maxNum}` }} -->
      </div>
      <div v-if="team.expireTime">
        {{ '过期时间: ' + team.expireTime }}
      </div>
      <div v-if="team.expireTime == null">
        过期时间：暂未设置
      </div>
    </template>
    <template #footer>
      <van-button size="small" type="primary" plain @click="chatRoom(team)">
        聊天室
      </van-button>
    </template>
  </van-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { TeamType } from '../models/team';
import { getCurrentUser } from "../services/user";
import {useRouter} from "vue-router";

const router = useRouter();

interface JoinTeamListProps {
  teamList: TeamType[];
}

const props = withDefaults(defineProps<JoinTeamListProps>(), {
  teamList: [] as TeamType[],
});

const currentUser = ref();

onMounted(async () => {
  currentUser.value = await getCurrentUser();
})


/**
 * 进入聊天室
 */
 const chatRoom = (team: any) => { 
  router.push({
    path:'/team/chatRoom/'+team.id,
  });
};

</script>