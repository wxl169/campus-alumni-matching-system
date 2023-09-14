<template>
    <van-loading vertical v-if="loading">
        <template #icon>
            <van-icon name="star-o" size="30" />
        </template>
        加载中...
    </van-loading>

    <van-skeleton title avatar :row="3" :loading="props.loading" v-for="team in props.teamList" class="skeleton">
        <van-cell :border="false" is-link center @click="toTeamDetails(team.id)">
            <template #title>
                <!-- 用户卡片 -->
                <van-row :wrap="false" align="center">
                    <!-- 左侧头像 -->
                    <van-col>
                        <van-image :src="team.avatarUrl" fit="cover" height="60px" round width="60px" />
                    </van-col>
                    <!-- 右侧信息 -->
                    <van-col offset="1">
                        <!-- 昵称 -->

                        <div class="teamName" style="text-align: left;">{{ team.teamName }}</div>
                        <!-- 简介 -->
                        <div class="description" v-if="team.description !== null && team.description != ''">描述：{{
                            team.description }}</div>
                        <div class="description" v-if="team.description == null || team.description == ''">描述：群主很懒，还没有群介绍哦</div>
                        
                        <!-- 标签 -->
                        <van-space :size="5" wrap>
                            <van-tag plain size="large" type="danger" style="margin-right: 8px; margin-top: 8px">
                                {{
                                    teamStatusEnum[team.status]
                                }}
                            </van-tag>
                            <van-tag size="large" type="primary" style="margin-right: 8px; margin-top: 8px">
                                {{ team.maxNum + `人组` }}
                            </van-tag>
                        </van-space>
                    </van-col>
                </van-row>
            </template>
            <!-- 右侧内容 -->
        </van-cell>
        <van-divider />
    </van-skeleton>
    <van-empty v-if="!teamList || teamList.length < 1" description="数据为空" />
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { TeamType } from '../models/team';
import { useRouter } from "vue-router";
import { getCurrentUser } from "../services/user";
import { teamStatusEnum } from "../constants/team";

const router = useRouter()
const currentUser = ref();


interface TeamCardListProps {
    loading: boolean;
    teamList: TeamType[];
}

const props = withDefaults(defineProps<TeamCardListProps>(), {
    loading: true,
    teamList: [] as TeamType[],
});

onMounted(async () => {
    currentUser.value = await getCurrentUser();
})

const toTeamDetails = (teamId: number) => {
    router.push({
        path: '/team/introduce',
        query: {
            teamId
        }
    });
}

</script>