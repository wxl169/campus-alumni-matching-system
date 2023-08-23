import myAxios from "../plugins/myAxios";
import {  showFailToast } from 'vant';
import { setTeamById } from "../status/team";

export const getTeamById = async (teamId:number) => {
    // 不存在则从远程获取
    const res = await myAxios.get('/team/get/detail',{
        params:{
            teamId:teamId
        }
    });
    if (res.code === 0) {
        setTeamById(res.data);
        return res.data;
    }else{
        showFailToast("请求错误");
    }
    return null;
}
