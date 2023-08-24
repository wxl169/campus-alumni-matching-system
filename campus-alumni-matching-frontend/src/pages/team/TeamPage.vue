<template>
    <div id="teamPage">
        <van-search v-model="searchText" show-action placeholder="搜索队伍" @search="onSearch" @cancel="onCancel" />
        <van-button type="primary" @click="doJoinTeam">创建队伍</van-button>
        <team-card-list :team-list="teamList" />

        <van-empty v-if="teamList?.length < 1" description="数据为空"/>
    </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import TeamCardList from '../../components/TeamCardList.vue';
import myAxios from '../../plugins/myAxios';


const router = useRouter();

const teamList = ref([]);
//搜索框
const searchText = ref('');

//跳转到加入队伍页
const doJoinTeam = () => {
    router.push({
        path: "/team/add"
    })
}




/**
 * 搜索队伍
 * @param val
 * @param status
 * @returns {Promise<void>}
 */
 const listTeam = async (val = '', status = 0) => {
  const res = await myAxios.get("/team/list/page", {
    params: {
      searchText: val,
      pageNum: 1,
      status,
    },
  });
  if (res?.code === 0) {
    teamList.value = res.data.rows;
  } else {
    Toast.fail('加载队伍失败，请刷新重试');
  }
}

// 页面加载时只触发一次
onMounted( () => {
  listTeam();
})

const onSearch = (val) => {
  listTeam(val);
};


</script>