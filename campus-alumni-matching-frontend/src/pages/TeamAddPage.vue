<template>
    <div id="teamAddPage">
        <van-form @submit="onSubmit">
            <van-cell-group inset>
                <van-field v-model="addTeamData.teamName" name="teamName" label="队伍名" placeholder="队伍名"
                    :rules="[{ required: true, message: '请输入队伍名' }]" maxlength="15" />

                <van-field v-model="addTeamData.description" rows="2" autosize label="队伍描述" type="textarea"
                    placeholder="请输入队伍描述" maxlength="200" />

                <van-field name="stepper" label="队伍人数">
                    <template #input>
                        <van-stepper v-model="addTeamData.maxNum" max="10" />
                    </template>
                </van-field>
                <van-field name="radio" label="队伍状态">
                    <template #input>
                        <van-radio-group v-model="addTeamData.status" direction="horizontal">
                            <van-radio name="0">公开</van-radio>
                            <van-radio name="1">私有</van-radio>
                            <van-radio name="2">加密</van-radio>
                        </van-radio-group>
                    </template>
                </van-field>

                <van-field v-model="addTeamData.expireTime" is-link readonly name="calendar" label="日历"
                    placeholder="点击选择过期日期" @click="showCalendar = true" />
                <van-calendar v-model:show="showCalendar" @confirm="onConfirm" />

                <van-field v-if="Number(addTeamData.status) === 2" v-model="addTeamData.password" type="password"
                    name="password" label="队伍密码" placeholder="队伍密码" :rules="[{ required: true, message: '请输入队伍密码' }]"
                    maxlength="6" />
            </van-cell-group>
            <div style="margin: 16px;">
                <van-button round block type="primary" native-type="submit">
                    提交
                </van-button>
            </div>
        </van-form>
        {{ addTeamData }}
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import myAxios from '../plugins/myAxios';
import { Toast } from 'vant';
import moment from 'moment';
import { showSuccessToast, showFailToast } from 'vant';

const router = useRouter();



const initFormData = {
    "description": "",
    "expireTime": "",
    "maxNum": 1,
    "password": "",
    "status": 0,
    "teamName": ""
}
//需要用户填写的表单数据
const addTeamData = ref({ ...initFormData });

//日历
const showCalendar = ref(false);
const onConfirm = (date) => {
console.log(moment(date).format("YYYY-MM-DD HH:MM:SS"))
addTeamData.value.expireTime = moment(date).format("YYYY-MM-DD HH:MM:SS");
    showCalendar.value = false;
};
//点击提交按钮
const onSubmit = async () => {
    const postData = {
        ...addTeamData.value,
        status: Number(addTeamData.value.status)
    }
    const res = await myAxios.post("team/add", postData);
    if (res?.code === 0) {
        showSuccessToast('添加成功');
        router.push({
            path: '/team',
            replace: true,
        });
    } else {
        if(res.description == ""){
            showFailToast("添加失败");
        }else{
            showFailToast(res.description);
        }
        
    }
}
</script>