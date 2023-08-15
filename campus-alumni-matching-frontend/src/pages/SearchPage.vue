<template>
  <form action="/" style="margin-top: 20px;">
    <van-search v-model="searchText" show-action placeholder="请输入要搜索的标签" @search="onSearch" @cancel="onCancel"/>
  </form>
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


  <van-button type="primary" size="large" @click="doSearchResult">搜索</van-button>
</template>

<script setup>
import { ref } from 'vue';
import { showToast } from 'vant';
import { useRouter } from 'vue-router';

const router = useRouter();


const searchText = ref('');
//列表标签
const originTagList = [
  {
    text: '性别',
    children: [
      { text: '男', id: '男' },
      { text: '女', id: '女' },
    ],
  },
  {
    text: '年级',
    children: [
      { text: '大一', id: '大一' },
      { text: '大二', id: '大二' },
      { text: '大三', id: '大三' },
      { text: '大四', id: '大四' },
      { text: '大1', id: '大1' },
      { text: '大2', id: '大2' },
      { text: '大3', id: '大3' },
      { text: '大4', id: '大4' },
    ],
  },
];

let tagList = ref(originTagList)

// 搜索过滤
const onSearch = (val) => {
  tagList.value = originTagList.map(parentTag => {
    const tempChildren = [...parentTag.children];
    const tempParentTag = {...parentTag};
    tempParentTag.children = tempChildren.filter(item => item.text.includes(searchText.value));
    return tempParentTag;
  });
};


const onCancel = () => {
  searchText.value = '';
  tagList.value = originTagList
};

const show = ref(true);
const close = () => {
  show.value = false;
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
const doSearchResult = () =>{
  router.push({
    path:'/user/list',
    query:{
      tags: activeIds.value
    }
  })
}

</script>

<style scoped>
</style>