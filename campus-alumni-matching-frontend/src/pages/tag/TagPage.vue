<template>
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
  <van-button type="primary" size="large" @click="userAddTags" style="margin-top: 50px;">添加</van-button>
</template>

<script setup langt="ts">
import { ref,onMounted } from 'vue';
import userTagList from "../../constants/userTagList";
import myAxios from "../../plugins/myAxios";
import { showSuccessToast, showFailToast } from 'vant';

//列表标签
let tagList = ref([])

onMounted(async () => {
  const res = await myAxios.get('/tag/show');
  if(res.code === 0){
      tagList.value = res.data
  }else{
    showFailToast("获取失败:"+res.description)
  }
})



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

//添加标签
const userAddTags = async () => {
  const res = await myAxios.post('/user/add/tags', {
      tagNameList : activeIds.value
  });
  if(res.data === true){
    showSuccessToast("添加成功");
  }else{
    showFailToast("添加失败:"+res.description)
  }

  console.log(tagNameList);
}

</script>