

//消息类型
export type MessageType = {
    sendUserName: any;
    id : number;
    content: string;
    status : number;
    sendTime : Date;
    sendUserId : number;
    receiveUserId : number;
    messageShow : number;
    isSystem : number;
}


export type TeamMessageType = {
    sendUserName : string;
    sendTime : Date;
    sendUserId : number;
    content : any;
    sendUserAvatar : string;
}


