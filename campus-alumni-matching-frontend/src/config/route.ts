import SearchPage from "../pages/search/SearchPage.vue"
import Index from "../pages/IndexPage.vue";
import Home from "../pages/HomePage.vue";
import Team from "../pages/team/TeamPage.vue";
import User from "../pages/user/UserPage.vue";
import UserEditPage from "../pages/user/UserEditPage.vue";
import UserTeamPage from "../pages/user/UserTeamPage.vue";
import SearchResultPage from "../pages/search/SearchResultPage.vue";
import SearchUserPage from "../pages/search/SearchUserPage.vue";
import UserHomePage from "../pages/user/UserHomePage.vue";
import UserLoginPage from "../pages/user/UserLoginPage.vue";
import UserRegisterPage from "../pages/user/UserRegisterPage.vue";
import TeamAddPage from "../pages/team/TeamAddPage.vue";
import TeamUpdatePage from "../pages/team/TeamUpdatePage.vue";
import ChatRoomPage from "../pages/team/ChatRoomPage.vue";
import TeamDetailPage from "../pages/team/TeamDetailPage.vue";
import UserDetailsPage from "../pages/user/UserDetailsPage.vue"
import Tag from "../pages/tag/TagPage.vue";
import TeamIntroduce from "../pages/team/TeamIntroduce.vue";
import MessageHome from "../pages/message/MessageHome.vue";
import UserChatRoomPage from "../pages/user/UserChatRoomPage.vue";
import UserSetup from "../pages/user/UserSetup.vue";
import UserMessageLog from "../pages/user/UserMessageLog.vue";
import TeamMessageLog from "../pages/team/TeamMessageLogPage.vue";
// 2. 定义一些路由
const routes = [
    { path: '/', title: '学友匹配', meta:{showBottom: true, showHeader: true, showBack: false,showRight:true}, component: Index },
    { path: '/home', meta:{showBottom: false, showHeader: false, showBack: false,showRight:false}, component: Home },
    { path: '/team' , title: '队伍列表', meta:{showBottom: true, showHeader: true, showBack: true,showRight:false}, component: Team },
    { path: '/userTeam', title: '通讯录', meta:{showBottom: true, showHeader: true, showBack: false,showRight:true}, component: UserTeamPage },
    { path: '/user', title: '个人详情', meta:{showBottom: false, showHeader: true, showBack: true,showRight:false}, component: User },
    { path: '/tag', title: '标签页', meta:{showBottom: false, showHeader: true, showBack: true,showRight:false}, component: Tag },
    { path: '/search', title: '搜索用户', meta:{showBottom: false, showHeader: true, showBack: true,showRight:false}, component: SearchPage },
    { path: '/user/edit', title: '编辑信息',meta:{showBottom: false, showHeader: true, showBack: true,showRight:false}, component: UserEditPage },
    { path: '/user/list', title: '用户列表', meta:{showBottom: false, showHeader: true, showBack: true,showRight:false},component: SearchResultPage },
    { path: '/user/list2', title: '用户列表', meta:{showBottom: false, showHeader: true, showBack: true,showRight:false},component: SearchUserPage },
    { path: '/user/home', title: '个人信息', meta:{showBottom: true, showHeader: true, showBack: false,showRight:false},component: UserHomePage},
    { path: '/user/login', title: '登录账号',meta:{showBottom: false, showHeader: true, showBack: true,showRight:false}, component: UserLoginPage},
    { path: '/user/register', title: '注册账号',meta:{showBottom: false, showHeader: true, showBack: true,showRight:false}, component: UserRegisterPage},
    { path: '/user/details', title: '个人资料',meta:{showBottom: false, showHeader: true, showBack: true,showRight:false}, component: UserDetailsPage},
    { path: '/user/setUp', title:'用户设置', meta:{showBottom: false, showHeader: true, showBack: true,showRight:false}, component: UserSetup},
    { path: '/user/message/log', title:'聊天记录', meta:{showBottom: false, showHeader: true, showBack: true,showRight:false}, component: UserMessageLog},
    { path: '/team/message/log', title:'聊天记录', meta:{showBottom: false, showHeader: true, showBack: true,showRight:false}, component: TeamMessageLog},
    { path: '/user/chatRoom/:id',meta:{showBottom: false, showHeader: false, showBack: false,showRight:false}, component: UserChatRoomPage},
    { path: '/team/add', title: '创建队伍',meta:{showBottom: false, showHeader: true, showBack: true,showRight:false}, component: TeamAddPage},
    { path: '/team/update', title: '更新队伍', meta:{showBottom: false, showHeader: true, showBack: true,showRight:false},component: TeamUpdatePage},
    { path: '/team/chatRoom/:id', meta:{showBottom: false, showHeader: false, showBack: false,showRight:false}, component: ChatRoomPage},
    { path: '/team/detail', title:'群聊设置', meta:{showBottom: false, showHeader: true, showBack: true,showRight:false}, component: TeamDetailPage},
    { path: '/team/introduce', title:'群聊介绍', meta:{showBottom: false, showHeader: true, showBack: true,showRight:false}, component: TeamIntroduce},
    { path: '/message', title:'消息列表', meta:{showBottom: true, showHeader: true, showBack: true,showRight:true}, component: MessageHome},
  ]

export default routes;