<template>
  <van-tabs v-model:active="active">
    <van-tab v-for="index in 2">
      <template #title v-if="index === 1">
        <van-icon name="contact" />账户登录
      </template>

      <template #title v-if="index === 2">
        <van-icon name="phone-circle-o" />手机号登录
      </template>

      <van-form @submit="userAccountSubmit" v-if="index === 1">
        <van-cell-group inset>
          <van-field v-model="userAccount" name="userAccount" label="账号" placeholder="账号"
            :rules="[{ required: true, message: '请输入账号' }]" />
          <van-field v-model="userPassword" type="password" name="userPassword" label="密码" placeholder="密码"
            :rules="[{ required: true, message: '请输入密码' }]" />
        </van-cell-group>
        <div style="margin: 16px;">
          <van-button round block type="primary" native-type="submit">
            登录
          </van-button>
        </div>
      </van-form>

      <van-form @submit="userPhoneSubmit" v-if="index === 2">
        <van-cell-group inset>
          <van-field v-model="phone" name="手机号" label="手机号" placeholder="手机号"
            :rules="[{ required: true, message: '请填写手机号' }]">
            <template #right-icon>
              <van-button type="primary" size="small" @click="sendCode" :disabled="isCounting">
                {{ isCounting ? `${countDown}s后重新发送` : '发送验证码' }}
              </van-button>
            </template>
          </van-field>
          <van-field v-model="messageCode" type="password" name="验证码" label="验证码" placeholder="验证码"
            :rules="[{ required: true, message: '请填写验证码' }]" />
        </van-cell-group>
        <div style="margin: 16px;">
          <van-button round block type="primary" native-type="submit">
            登录
          </van-button>
        </div>
      </van-form>

    </van-tab>
  </van-tabs>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router';
import { ref } from 'vue';
import myAxios from '../../plugins/myAxios';
import { showSuccessToast, showFailToast } from 'vant';


const active = ref(0)
const route = useRoute();
const userAccount = ref('');
const userPassword = ref('');
const phone = ref('');
const messageCode = ref('');
const isCounting = ref(false);
const countDown = ref(60);


/**
 * 账号密码登录
 */
const userAccountSubmit = async () => {
  const res = await myAxios.post('/user/login', {
    userAccount: userAccount.value,
    userPassword: userPassword.value
  })

  if (res.code === 0 && res.data) {
    showSuccessToast('登录成功');
    const redirectUrl = route.query?.redirect as string ?? '/';
    window.location.href = redirectUrl;
  } else {
    showFailToast('登录失败');
  }
};



/**
 * 发送验证码
 */
const sendCode = async () => {
  if (!isCounting.value) {
    // 发送验证码的逻辑
    const res = await myAxios.post('/user/send/code', {
      phone: phone.value,
    })

    if (res.code === 0) {
      showSuccessToast('验证码发送成功');
      // 开始倒计时
      startCountDown();
    } else {
      showFailToast('验证码发送失败');
    }
  }

}
/**
 * 使用验证码登录
 */
const userPhoneSubmit = async () => {
  const res = await myAxios.post('/user/login/phone', {
    phone: phone.value,
    messageCode: messageCode.value
  })

  if (res.code === 0 && res.data) {
    showSuccessToast('登录成功');
    const redirectUrl = route.query?.redirect as string ?? '/';
    window.location.href = redirectUrl;
  } else {
    showFailToast('登录失败');
  }
}

const startCountDown = () => {
  isCounting.value = true;
  let timer = setInterval(() => {
    if (countDown.value > 0) {
      countDown.value--;
    } else {
      // 倒计时结束，恢复原样
      isCounting.value = false;
      countDown.value = 60;
      clearInterval(timer);
    }
  }, 1000);
};

</script>