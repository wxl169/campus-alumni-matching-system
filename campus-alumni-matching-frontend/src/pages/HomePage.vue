<template>
    <div class="container">

        
        <div id="head" style="margin-bottom: 50px;">
            <van-space direction="vertical" fill>
            <van-image class="logo" width="10rem" height="10rem" fit="contain" src="../src/assets/makeFriends.svg" />
            <h2>校园学友匹配平台</h2>
        </van-space>
        </div>
        


        <van-space direction="vertical" fill>
            <van-button icon="https://fastly.jsdelivr.net/npm/@vant/assets/user-active.png" type="primary" plain block  
                size="large" to="/user/login">
                登录账号
            </van-button>
            <van-button icon="https://fastly.jsdelivr.net/npm/@vant/assets/user-active.png" type="primary" plain block  
                size="large" to="/user/register" style="margin-top: 12px;">
                注册账号
            </van-button>
        </van-space>
    </div>
</template>
  
  