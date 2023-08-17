<template>
    <div id="teamPage">
        <van-button type="primary" @click="doJoinTeam">主要按钮</van-button>
        <team-card-list :team-list="teamList"/>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import TeamCardList from '../components/TeamCardList.vue';
import myAxios from '../plugins/myAxios';
import { showSuccessToast, showFailToast } from 'vant';

    const router = useRouter();

    //跳转到加入队伍页
    const doJoinTeam = () =>{
        router.push({
            path:"/team/add"
        })
    }
    const teamList = ref([]);
    
    onMounted(async() =>{
        const res = await myAxios.get("/team/list");
        if(res.code === 0){ 
            teamList.value = res.data.rows;
        }else{
            showFailToast("加载队伍失败，请刷新重试");
        } 
    })
</script>