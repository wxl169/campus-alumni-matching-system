<template>
    <van-form @submit="onSubmit">
        <van-cell-group inset>
            <van-field v-model="userAccount" name="账号" label="账号" placeholder="账号"
                :rules="[{ required: true, message: '请填写5~10位的账号' }]" />
            <van-field v-model="userPassword" type="password" name="密码" label="密码" placeholder="密码"
                :rules="[{ required: true, message: '请填写8~15位的密码' }]" />
            <van-field v-model="checkPassword" type="password" name="确认密码" label="确认密码" placeholder="确认密码"
                :rules="[{ required: true, message: '请再次填写密码' }]" />
        </van-cell-group>
        <div style="margin: 16px;">
            <van-button round block type="primary" native-type="submit">
                提交
            </van-button>
        </div>
    </van-form>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import myAxios from '../../plugins/myAxios';
import { useRouter } from "vue-router";
import { showFailToast,showSuccessToast } from 'vant';


const userAccount = ref('');
const userPassword = ref('');
const checkPassword = ref('');
const router = useRouter();


const onSubmit = async () => {
    const res = await myAxios.post("/user/register", {
            userAccount: userAccount.value,
            userPassword: userPassword.value,
            checkPassword: checkPassword.value
    });
    if (res?.code === 0) {
        showSuccessToast('注册成功');
        router.push('/home')
    } else {
        if (res.description == "") {
            showFailToast("注册失败!");
        } else {
            showFailToast(res.description);
        }

    }
};

</script>