import { UserType } from "./user";

//队伍类型
export type TeamType = {
    id : number;
    avatarUrl: string;
    leaderId : number;
    teamName : string;
    description? : string;
    maxNum : number;
    password : string;
    status : number;
    expireTime: Date;
    createTime : Date;
    updateTime : Date;
    userList : UserType[];
}
