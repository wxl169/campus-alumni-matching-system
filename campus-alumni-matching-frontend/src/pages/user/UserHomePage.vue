<template>
    <template v-if="user">
        <!-- 用户基本信息 -->
        <van-cell is-link :label="user.profile || '暂无个人简介'" :title="user.username || user.userAccount"
            :to=" `/user/${user.id}`"  center>
            <template #icon>
                <van-image :src="user.avatarUrl" fit="cover" height="80px" round style="margin-right: 10px"
                    width="80px" />
            </template>
        </van-cell>

        <!-- 用户标签 -->
        <van-cell-group :border="false" title="我的标签">
            <van-space :size="5" style="padding: 0 16px" wrap>
                <van-tag v-if="user.gender !== 0" :color="user.gender === 1 ? '#3bb' : '#f99'" plain size="large">
                    {{ user.gender === 1 ? '男' : '女' }}
                </van-tag>
                <van-tag v-for="tag in user.tags" plain size="large" type="primary">
                    {{ tag }}
                </van-tag>
                <van-tag  size="large" type="primary" @click="$router.push('/account/tags')">
                    <van-icon name="plus" style="margin-right: 3px" />
                    添加标签
                </van-tag>
            </van-space>
        </van-cell-group>

        
    </template>
</template>


<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { getCurrentUser } from "../../services/user";

const user = ref();
onMounted(async () => {
    user.value = await getCurrentUser();
    if (user.value.tags) {
        user.value.tags = JSON.parse(user.value.tags);
    }
})


</script>
