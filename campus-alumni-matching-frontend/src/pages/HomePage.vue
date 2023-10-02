<template>
    <div class="container">


        <div id="head" style="margin-bottom: 50px;">
            <van-space direction="vertical" fill>
                <van-image class="logo" width="10rem" height="10rem" fit="contain" src="https://image.cqiewxl.cn/%E5%B0%8F%E4%BC%99%E4%BC%B4.png" />
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


        <div class="container">
            <!-- 其他内容 -->
            <a href="https://beian.miit.gov.cn">渝ICP备2023010626号-1</a>
        </div>
    </div>
</template>
<style>
.container {
    position: relative;
    min-height: 100px; /* 设置容器高度为视口的高度 */
}

.container a {
    position: absolute;
    bottom: 0; /* 将a标签固定在底部 */
    left: 0;
    right: 0;
    padding: 10px;
    text-align: center;
}</style>
  
  