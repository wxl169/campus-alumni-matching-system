<template>
  <van-search v-model="value" show-action  placeholder="请输入搜索姓名或账号" @search="onSearch">
    <template #action>
      <div @click="onClickButton">搜索</div>
    </template>
  </van-search>

  <van-divider content-position="left">已选标签</van-divider>
  <div v-if="activeIds.length === 0">请选择标签</div>
  <van-row gutter="24">
    <van-col span="24">
      <van-tag v-for="tag in activeIds" :show="true" closeable size="large" type="primary" @close="doClose(tag)">
        {{ tag }}
      </van-tag>
    </van-col>
  </van-row>

  <van-divider content-position="left">选择标签</van-divider>
  <van-tree-select v-model:active-id="activeIds" v-model:main-active-index="activeIndex" :items="tagList" />


  <van-button type="primary" size="large" @click="doSearchResult" style="margin-top: 50px;">搜索</van-button>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { showToast } from 'vant';
import { useRouter } from 'vue-router';
import userTagList from "../../constants/userTagList";
import myAxios from "../../plugins/myAxios";

const router = useRouter();

let tagList = ref([])

onMounted(async () => {
  const res = await myAxios.get('/tag/show');
  if (res.code === 0) {
    tagList.value = res.data
  } else {
    showFailToast("获取失败:" + res.description)
  }

  await myAxios.get('/user/cache/tag');
})

const show = ref(true);
const close = () => {
  show.value = false;
};

const value = ref('');
const onSearch = (val) => {
  router.push({
    path: '/user/list2',
    query: {
      condition : val
    }
  })
};
const onClickButton = () => { 
  router.push({
    path: '/user/list2',
    query: {
      condition: value.value
     
    }
  })
};


//已选中的标签
const activeIds = ref([]);
const activeIndex = ref(0);


//关闭标签
const doClose = (tag) => {
  activeIds.value = activeIds.value.filter(item => {
    return item !== tag;
  })
}

//执行搜索
const doSearchResult = () => {
  router.push({
    path: '/user/list',
    query: {
      tags: activeIds.value
    }
  })
}

</script>

<style scoped></style>