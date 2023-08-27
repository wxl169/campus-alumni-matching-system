import {UserType,UserDetails} from "../models/user";

let currentUser: UserType;
let userDetails : UserDetails;


const setCurrentUserState = (user: UserType) => {
    currentUser = user;
}

const getCurrentUserState = () : UserType => {
    return currentUser;
}

const setUerDetails = (user : UserDetails) =>{
    userDetails = user;
}

const getUerDetails = () : UserDetails =>{
    return userDetails;
}

export {
    setCurrentUserState,
    getCurrentUserState,
    setUerDetails,
    getUerDetails,
}
