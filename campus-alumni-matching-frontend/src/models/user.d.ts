//用户类型
export type UserType = {
    id : number;
    username : string;
    userAccount : string;
    avatarUrl? : string;
    gender : number;
    phone : string;
    email : string;
    school: string;
    profile? : string;
    userStatus : number;
    userRole : number;
    tags : string[];
    createTime : Date;
}
