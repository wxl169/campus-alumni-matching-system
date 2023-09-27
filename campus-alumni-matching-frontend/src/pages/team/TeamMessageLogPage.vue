<template>
    <form action="/" style="margin-bottom: 20px;">
        <van-search v-model="content" show-action placeholder="请输入搜索关键词" @search="onSearch" @cancel="onCancel" />
    </form>
    <TeamMessageList :message-list="messageList" :loading="loading_message" v-if="choose === true"/>
    <van-date-picker v-model="currentDate" title="选择日期" :min-date="minDate" :max-date="maxDate"  @confirm="handleConfirm" v-if="choose === false"/>
</template>

<script setup lang="ts">
import { useRoute } from "vue-router";
import { onMounted, ref } from "vue";
import { showFailToast } from 'vant';
import myAxios from "../../plugins/myAxios";
import { getCurrentUser } from "../../services/user";
import TeamMessageList from "../../components/TeamMessageList.vue";

const route = useRoute()
let teamId = ref(0)
teamId = route.query.teamId;

const loading_message = ref(true);

const messageList = ref([])
const currentUser = ref(null);

const currentDate = ref(['2023', '', '01']);
const choose = ref(false);
const year = ref(0);
const month = ref(0);
const day = ref(0);
const maxDate = ref(new Date());
const minDate = ref(new Date());

onMounted(async () => {
    currentUser.value = await getCurrentUser();
    year.value = new Date(currentUser.value.createTime).getFullYear();
    month.value = new Date(currentUser.value.createTime).getMonth() + 1;
    day.value = new Date(currentUser.value.createTime).getDate();
    maxDate.value = new Date();
    minDate.value = new Date(year.value,month.value-1,day.value);
    console.log(minDate.value)
})


const content = ref('');
//搜索消息
const onSearch = async (content: any) => {
    const res = await myAxios({
        url: '/message/team/get/oldMessage',
        method: "get",
        params: {
            teamId : teamId,
            content: content
        },
    });
    if (res.code === 0) {
        messageList.value = res.data
        choose.value = true;
    } else {
        showFailToast("搜索失败");
    }
};

const onCancel = () => {
    choose.value = false;
    content.value = '';
};

//按日期选择消息记录
const  handleConfirm =async ()=>{
    const dateString = currentDate.value.join('-');
    const res = await myAxios({
        url: '/message/team/get/oldMessage',
        method: "get",
        params: {
            teamId : teamId,
            sendTime: dateString
        },
    });
    if (res.code === 0) {
        messageList.value = res.data
        choose.value = true;
    } else {
        showFailToast("搜索失败");
    }
}
</script>