<template>
    <div id="teamUpdatePage">
        <van-form @submit="onSubmit">
            <van-cell-group inset>
                <van-field v-model="updateTeamData.teamName" name="teamName" label="队伍名" placeholder="队伍名"
                    :rules="[{ required: true, message: '请输入队伍名' }]" maxlength="15" />
                <van-field name="uploader" label="头像">
                    <template #input>
                        <!-- <van-uploader :after-read="afterRead" :v-model="updateTeamData.avatarUrl" multiple :max-count="2"/> -->
                        <van-uploader class="my-van-image" :after-read="afterRead">
                            <van-image height="6rem" :src="updateTeamData.avatarUrl">
                                <template v-slot:error>加载失败</template>
                            </van-image>
                        </van-uploader>
                    </template>
                </van-field>
                <van-field v-model="updateTeamData.description" rows="2" autosize label="队伍描述" type="textarea"
                    placeholder="请输入队伍描述" maxlength="200" />

                <van-field name="stepper" label="队伍人数">
                    <template #input>
                        <van-stepper v-model="updateTeamData.maxNum" max="10" />
                    </template>
                </van-field>
                <van-field name="radio" label="队伍状态">
                    <template #input>
                        <van-radio-group v-model="updateTeamData.status" direction="horizontal">
                            <van-radio name="0">公开</van-radio>
                            <van-radio name="1">私有</van-radio>
                            <van-radio name="2">加密</van-radio>
                        </van-radio-group>
                    </template>
                </van-field>

                <van-field v-model="updateTeamData.expireTime" is-link readonly name="calendar" label="日历"
                    placeholder="点击选择过期日期" @click="showCalendar = true" />
                <van-calendar v-model:show="showCalendar" @confirm="onConfirm" />

                <van-field v-if="Number(updateTeamData.status) === 2" v-model="updateTeamData.password" type="password"
                    name="password" label="队伍密码" placeholder="队伍密码" :rules="[{ required: true, message: '请输入队伍密码' }]"
                    maxlength="6" />
            </van-cell-group>
            <div style="margin: 16px;">
                <van-button round block type="primary" native-type="submit">
                    提交
                </van-button>
            </div>
        </van-form>
    </div>
</template>

<script setup lang="ts">
import moment from 'moment';
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import myAxios from "../../plugins/myAxios";
import { showFailToast, showSuccessToast } from 'vant';
import { showConfirmDialog } from 'vant';


const initFormData = {
    "description": "",
    "expireTime": "",
    "maxNum": 1,
    "password": "",
    "status": 0,
    "teamName": "",
    "avatarUrl": "",
}

// 需要用户填写的表单数据
const updateTeamData = ref({ ...initFormData })

const route = useRoute();
let teamId = ref(0)

// 获取之前的房间信息
onMounted(async () => {
    teamId = route.query.teamId;
    if (Number(teamId) <= 0) {
        showFailToast('加载房间失败');
        return;
    }
    const res = await myAxios.get("/team/get", {
        params: {
            teamId,
        }
    });
    if (res?.code === 0) {
        const { status, expireTime } = res.data;
        updateTeamData.value = res.data;
        updateTeamData.value.status = status + "";
        updateTeamData.value.expireTime = moment(expireTime).format("YYYY-MM-DD HH:MM:SS");
    } else {
        showFailToast('加载队伍失败，请刷新重试');
    }
});

//日历
const showCalendar = ref(false);
const onConfirm = (date: moment.MomentInput) => {
    updateTeamData.value.expireTime = moment(date).format("YYYY-MM-DD HH:MM:SS");
    showCalendar.value = false;
};

//点击提交按钮
const onSubmit = async () => {
    const postData = {
        ...updateTeamData.value,
        status: Number(updateTeamData.value.status)
    }
    showConfirmDialog({
        title: '确认保持修改信息？',
    })
        .then(async () => {
            const res = await myAxios.put("/team/update", postData);
            if (res?.code === 0) {
                showSuccessToast('修改成功!');
            } else {
                if (res.description == "") {
                    showFailToast("修改失败!");
                } else {
                    showFailToast(res.description);
                }

            }
        })
        .catch(() => {
            // on cancel
        });
}

const afterRead = async (file: { file: string | Blob; }) => {
    // 构建表单数据
    const formData = new FormData();
    formData.append('avatarUrl', file.file);
    // 发送请求
    const response = await myAxios.post("/upload", formData);

    if (response.code === 0) {
        updateTeamData.value.avatarUrl = response.data
    } else {
        if (res.description == "") {
            showFailToast("上传失败");
        } else {
            showFailToast(res.description);
        }
    }
};


</script>

<style scoped>
.my-van-image {
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 30px 0;
}
</style>
