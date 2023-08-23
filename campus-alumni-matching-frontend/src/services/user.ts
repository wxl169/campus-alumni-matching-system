import myAxios from "../plugins/myAxios";
import { setCurrentUserState } from "../status/user";
import {  showFailToast } from 'vant';

export const getCurrentUser = async () => {
    // 不存在则从远程获取
    const res = await myAxios.get('/user/currentUser');
    if (res.code === 0) {
        setCurrentUserState(res.data);
        return res.data;
    }else{
        showFailToast("请先登录");
    }
    return null;
}


