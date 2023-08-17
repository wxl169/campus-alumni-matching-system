<template>
  <div id="teamCardList">
    <van-card
    v-for="team in props.teamList"
    :desc="`队伍详情：${team.description}`"
    :tag="team.maxNum+`人组`"
    :title="`${team.teamName} `"
    :thumb="`${team.avatarUrl}`"
  >

    <template #tags>
        <van-tag plain type="danger" style="margin-right: 8px; margin-top: 8px">
          {{
            teamStatusEnum[team.status]
          }}
        </van-tag>
      </template>
      <template #bottom>
        <div>
          <!-- {{ `队伍人数: ${team.hasJoinNum}/${team.maxNum}` }} -->
        </div>
        <div v-if="team.expireTime">
          {{ '过期时间: ' + team.expireTime }}
        </div>
        <div>
          {{ '创建时间: ' + team.createTime }}
        </div>
      </template>
      <template #footer>
        <!-- 直接不显示已加入的队伍 -->‘
        <van-button size="small" type="primary" v-if="team.leaderId !== currentUser?.id" plain
                    @click="preJoinTeam(team)">
          加入队伍
        </van-button>
        <!-- <van-button v-if="team.userId === currentUser?.id" size="small" plain
                    @click="doUpdateTeam(team.id)">更新队伍
        </van-button>
    
        <van-button v-if="team.userId !== currentUser?.id && team.hasJoin" size="small" plain
                    @click="doQuitTeam(team.id)">退出队伍
        </van-button>
        <van-button v-if="team.userId === currentUser?.id" size="small" type="danger" plain
                    @click="doDeleteTeam(team.id)">解散队伍
        </van-button> -->
      </template>
    </van-card>
     <van-dialog v-model:show="showPasswordDialog" title="请输入密码" show-cancel-button @confirm="doJoinTeam" @cancel="doJoinCancel">
      <van-field v-model="password" placeholder="请输入密码"/>
    </van-dialog>
  </div>
</template>




<script setup lang="ts">
import {onMounted, ref} from "vue";
import { TeamType } from '../models/team';
import {teamStatusEnum} from "../constants/team";
import { showSuccessToast, showFailToast } from 'vant';
import {getCurrentUser} from "../services/user";
import myAxios from "../plugins/myAxios";

interface TeamCardListProps{
    teamList:TeamType[];
}

const props = withDefaults(defineProps<TeamCardListProps>(),{
    teamList:[] as TeamType[],
});

const currentUser = ref();
const joinTeamId = ref(0);
const showPasswordDialog = ref(false);
const password = ref('');


onMounted(async () => {
  currentUser.value = await getCurrentUser();
})

const preJoinTeam = (team: TeamType) => {
  joinTeamId.value = team.id;
  if (team.status === 0) {
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
    doJoinCancel();
  } else {
    showFailToast('加入失败' + (res.description ? `,${res.description}` : ''))
  }
}

</script>