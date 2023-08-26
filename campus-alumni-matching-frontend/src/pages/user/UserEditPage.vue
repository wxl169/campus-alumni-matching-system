<template>
  <van-form @submit="onSubmit">
    <van-field name="uploader" label="头像" v-if="editUser.editName === '头像'">
      <template #input>
        <!-- <van-uploader :after-read="afterRead" :v-model="updateTeamData.avatarUrl" multiple :max-count="2"/> -->
        <van-uploader class="my-van-image" :after-read="afterRead">
          <van-image height="6rem" :src="editUser.currentValue">
            <template v-slot:error>加载失败</template>
          </van-image>
        </van-uploader>
      </template>
    </van-field>

    <van-radio-group v-model="checked" direction="horizontal" v-if="editUser.editName === '性别'">
      <van-radio name="0">男</van-radio>
      <van-radio name="1">女</van-radio>
    </van-radio-group>


    <van-cell-group inset v-if="editUser.editName === '个人简介'">
      <van-field v-model="editUser.currentValue" rows="2" autosize label="个人简介" type="textarea" maxlength="50" placeholder="请输入个人简介"
        show-word-limit />
    </van-cell-group>

    <van-cell-group inset v-if="editUser.editName != '头像' && editUser.editName != '性别' && editUser.editName != '个人简介'">
      <van-field v-model="editUser.currentValue" :name="editUser.editKey" :label="editUser.editName"
        :placeholder="`请输入${editUser.editName}`" />
    </van-cell-group>


    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        提交
      </van-button>
    </div>
  </van-form>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router';
import { ref } from 'vue';
import myAxios from '../../plugins/myAxios';
import { showSuccessToast, showFailToast } from 'vant';
import { getCurrentUser } from "../../services/user";

const route = useRoute();
const router = useRouter();
const editUser = ref({
  editKey: route.query.editKey,
  currentValue: route.query.currentValue,
  editName: route.query.editName,
})

let checked = ref(editUser.value.currentValue);



const onSubmit = async () => {
  //把editkey.currentValue,editName提交到后台
  const currentUser = await getCurrentUser();
  if (!currentUser) {
    showFailToast('用户未登录');
    return;
  }
  if (editUser.value.editName === '性别') {
    editUser.value.currentValue = checked;
  }
  const res = await myAxios.put('/user/updateUser', {
    'id': currentUser.id,
    [editUser.value.editKey as string]: editUser.value.currentValue,
  })
  if (res.code === 0 && res.data > 0) {
    showSuccessToast('修改成功');
    router.back();
  } else {
    showFailToast('修改错误');
  }
};
/**
 * 加载头像
 * @param file 
 */
const afterRead = async (file: { file: string | Blob; }) => {
  // 构建表单数据
  const formData = new FormData();
  formData.append('avatarUrl', file.file);
  // 发送请求
  const response = await myAxios.post("/upload", formData);

  if (response.code === 0) {
    editUser.value.currentValue = response.data
  } else {
    if (res.description == "") {
      showFailToast("上传失败");
    } else {
      showFailToast(res.description);
    }
  }
};
</script>