<template>
     <van-card
    v-for="team in props.teamList"
    :title="`${team.teamName} `"
    :thumb="`${team.avatarUrl}`"
  >
    </van-card>
</template>

<script setup lang="ts">
import {onMounted, ref} from "vue";
import { TeamType } from '../models/team';
import {getCurrentUser} from "../services/user";

interface JoinTeamListProps{
    teamList:TeamType[];
}

const props = withDefaults(defineProps<JoinTeamListProps>(),{
    teamList:[] as TeamType[],
});

const currentUser = ref();

onMounted(async () => {
  currentUser.value = await getCurrentUser();
})

</script>