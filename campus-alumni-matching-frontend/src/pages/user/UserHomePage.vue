<template>
    <template v-if="user">
        <!-- 用户基本信息 -->
        <van-cell is-link :label="user.profile || '暂无个人简介'" :title="user.username || user.userAccount"
              center  to="/user">
            <template #icon>
                <van-image :src="user.avatarUrl" fit="cover" height="80px" round style="margin-right: 10px"
                    width="80px" />
            </template>
        </van-cell>

        <!-- 用户标签 -->
        <van-cell-group :border="false" title="我的标签">
            <van-space :size="5" style="padding: 0 16px" wrap>
                <van-tag v-for="tag in user.tags" plain size="large" type="primary" @close="close(tag)" closeable >
                    {{ tag }}
                </van-tag>
                <van-tag  size="large" type="primary" @click="$router.push('/tag')">
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
import myAxios from "../../plugins/myAxios";
import { showFailToast, showSuccessToast } from 'vant';

const user = ref();
onMounted(async () => {
    user.value = await getCurrentUser();
    if (user.value.tags) {
        user.value.tags = JSON.parse(user.value.tags);
    }
})

const close = async (tag:string) => {
    const res = await myAxios.delete('/user/delete/tag',{
        params:{
            tagName : tag
        }
    });
  if(res.code === 0){
    user.value = await getCurrentUser();
    if (user.value.tags) {
        user.value.tags = JSON.parse(user.value.tags);
    }
      showSuccessToast("删除成功");
  }else{
    showFailToast("删除标签失败:"+res.description)
  }
};
</script>
