<template>
    <van-card
    v-for="team in props.teamList"

    :desc="`队伍详情：${team.description}`"
    :title="`${team.teamName} `"

    :price="`最大人数:`+team.maxNum"
  >
    <template #footer>
      <van-button size="mini">联系我</van-button>
    </template>
    
  </van-card>
</template>




<script setup lang="ts">
import { TeamType } from '../models/team';


interface TeamCardListProps{
    teamList:TeamType[];
}

const props = withDefaults(defineProps<TeamCardListProps>(),{
    teamList:[] as TeamType[],
});

</script>