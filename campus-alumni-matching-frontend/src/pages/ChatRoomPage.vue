<template>
<div class="wrap" :style="{height: scrollerHeight}">
        <div class="title">
            <div>
                <van-icon
                    name="arrow-left"
                    size="20"
                    style="margin-left: 10px"
                    @click="onClickLeft"
                />
            </div>
            <div>{{teamName}}
            </div>
            <div>
                <van-icon 
                    name="ellipsis" 
                    size="22"
                    style="margin-right: 10px"
                    @click="onClickRight(teamId)"
                />
            </div>
        </div>

        <div class="content_box" id="box" ref="scrollBox">
            <div :class="item.position === 'left' ? 'userbox2' : 'userbox'"
                 v-for="(item, index) in chatList"
                 :key="index"
            >
                <div :class="item.position === 'left' ? 'nameInfo2' : 'nameInfo'">
                    <div style="font-size: 13px">{{ item.userName }}
                    </div>
                    <div :class="item.position === 'left' ? 'contentText2' : 'contentText'">
                        {{ item.content }}
                    </div>
                </div>
                <div>
                    <van-image style="z-index: 1" width="40px" height="40px" :src="item.avatarUrl"/>
                </div>
            </div>
        </div>
        <div class="bottom">
            <!--            TODO 消息小尖角未实现-->
            <van-field
                v-model="message"
                center
                type="textarea"
                :autosize="{ maxHeight: 100, minHeight: 25 }"
                placeholder="请输入内容"
                rows="1"
            >
                <template #button>
                    <van-button size="small" type="primary" @click="sendMessage">发送</van-button>
                </template>
            </van-field>
        </div>
    </div>
</template>

<script setup>
import {useRoute, useRouter} from "vue-router";
import { getCurrentUser } from "../services/user";
import { getTeamById } from "../services/team";
import {computed, onBeforeUpdate, onMounted, onUpdated, ref} from "vue";


const router = useRouter()
const route = useRoute()

//队伍名
const teamName = ref("");
// 当前用户信息
const currentUser = ref(null);
const teamById = ref(null);
const teamId = route.params.id


const scrollerHeight = computed(() => {
    return (window.innerHeight - 50) + 'px'; //自定义高度需求
})

//返回
const onClickLeft = () => {
    router.back();
}


const onClickRight = (teamId) =>{
  router.push({
    path:'/team/detail',
    query:{
        teamId
    }
  });
};

onMounted(async () => {
  currentUser.value = await getCurrentUser();
  teamById.value = await getTeamById(teamId);
  teamName.value = teamById.value.teamName;
})




const setScrollPageSize = () => {
    // 设置滚动条位置
    setTimeout(() => {
        window.scrollTo({
            left: 0,
            top: document.scrollingElement.scrollHeight
        });
        console.log(document.scrollingElement.scrollHeight)
    }, 200); // 注意这里需要延迟20ms正好可以获取到更新后的dom节点
}

</script>







<style>
.wrap {
    width: 100%;
    background-color: #f5f5f5;
}

.title {
    position: fixed;
    top: 0;
    height: 40px;
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    background-color: #f5f5f5;
    border-bottom: 1px solid #eaeaea;
    z-index: 9999;
}

.bottom {
    min-height: 50px;
    width: 100%;
    border-top: 1px solid #eaeaea;
    position: fixed;
    bottom: 0;
    z-index: 9999;
}

.content_box {
    margin-top: 40px;
    background-color: #f5f5f5;
    /*
    中间栏计算高度，110是包含了上下固定的两个元素高度90
    这里padding：10px造成的上下够加了10，把盒子撑大了，所以一共是20要减掉
    然后不知道是边框还是组件的原因，导致多出了一些，这里再减去5px刚好。不然会出现滚动条到顶或者底部的时候再滚动的话就会报一个错，或者出现滚动条变长一下的bug
    */
    overflow-x: hidden;
    overflow-y: auto;
    padding: 10px 10px 50px 10px;
    z-index: -1;
}

.timer {
    text-align: center;
    color: #c2c2c2;
}

/* 发送的信息样式 */
/*
右边消息思路解释：首先大盒子userbox内放两个盒子，一个放头像，一个放用户名和发送的内容，我们先用flex让他横向排列。
然后把写文字的大盒子设置flex：1。这个属性的意思就是让这个元素撑满父盒子剩余位置。然后我们再把文字盒子设置flex，并把他对齐方式设置为尾部对齐就完成了基本的结构，然后微调一下就可以了
*/
.userbox {
    width: 100%;
    display: flex;
    margin-bottom: 10px;
}

.nameInfo {
    /* 用flex：1把盒子撑开 */
    flex: 1;
    margin-right: 10px;
    /* 用align-items把元素靠右对齐 */
    display: flex;
    flex-direction: column;
    align-items: flex-end;
}

.contentText {
    background-color: #9eea6a;
    /* 把内容部分改为行内块元素，因为盒子flex：1把盒子撑大了，所以用行内块元素让内容宽度不根据父盒子来 */
    display: inline-block;
    /* 这四句是圆角 */
    border-radius: 10px;
    /* 最大宽度限定内容输入到百分61换行 */
    max-width: 61%;
    padding: 10px;
    /* 忽略多余的空白，只保留一个空白 */
    white-space: normal;
    /* 换行显示全部字符 */
    word-break: break-all;
    margin-top: 3px;
    font-size: 14px;
}

/* 接收的信息样式 */
/* 左边消息思路解释：跟上面一样，就是换一下位置，首先通过把最外层大盒子的排列方式通过flex-direction: row-reverse;属性翻转，也就是头像和文字盒子换位置
   然后删除掉尾部对齐方式，因为不写这个默认是左对齐的。我们写的左边就没必要再写了。
*/
.userbox2 {
    width: 100%;
    display: flex;
    flex-direction: row-reverse;
    margin-bottom: 10px;
}

.nameInfo2 {
    /* 用flex：1把盒子撑开 */
    flex: 1;
    margin-left: 10px;
}

.contentText2 {
    background-color: #fff;
    /* 把内容部分改为行内块元素，因为盒子flex：1把盒子撑大了，所以用行内块元素让内容宽度不根据父盒子来 */
    display: inline-block;
    /* 这四句是圆角 */
    border-radius: 10px;
    /* 最大宽度限定内容输入到百分61换行 */
    max-width: 61%;
    padding: 10px;
    /* 忽略多余的空白，只保留一个空白 */
    white-space: normal;
    /* 换行显示全部字符 */
    word-break: break-all;
    margin-top: 3px;
    font-size: 14px;
}
</style>