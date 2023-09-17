<template>
    <div id="teamPage">
        <van-search v-model="searchText" show-action placeholder="搜索队伍" @search="onSearch" @cancel="onCancel" />
        <!-- <van-button type="primary" @click="doJoinTeam">创建队伍</van-button> -->
        <team-card-list :team-list="teamList" :loading="loading_team"/>

    </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import TeamCardList from '../../components/TeamCardList.vue';
import myAxios from '../../plugins/myAxios';
import {  showFailToast } from 'vant';


const teamList = ref([]);
//搜索框
const searchText = ref('');
const loading_team = ref(true);


/**
 * 搜索队伍
 * @param val
 * @param status
 * @returns {Promise<void>}
 */
 const listTeam = async (val = '') => {
  loading_team.value = true;
  const res = await myAxios.get("/team/list", {
    params: {
      searchText: val,
      pageNum: 1,
    },
  });
  if (res?.code === 0) {
    teamList.value = res.data.rows;
  } else {
    showFailToast('加载队伍失败，请刷新重试');
  }
  loading_team.value = false;
}

// 页面加载时只触发一次
onMounted( () => {
  listTeam();
})

const onSearch = (val: string | undefined) => {
  listTeam(val);
};

/**
 * 取消搜索队伍
 * @param status 
 */
const onCancel = async ()=>{
  searchText.value = '';
  const res = await myAxios.get("/team/list", {
    params: {
      pageNum: 1,
    },
  });
  if (res?.code === 0) {
    teamList.value = res.data.rows;
  } else {
    showFailToast('加载队伍失败，请刷新重试');
  }
}
</script>