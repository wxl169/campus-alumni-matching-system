import SearchPage from "../pages/search/SearchPage.vue"
import Index from "../pages/IndexPage.vue";
import Team from "../pages/team/TeamPage.vue";
import User from "../pages/user/UserPage.vue";
import UserEditPage from "../pages/user/UserEditPage.vue";
import UserTeamPage from "../pages/user/UserTeamPage.vue";
import SearchResultPage from "../pages/search/SearchResultPage.vue";
import UserHomePage from "../pages/user/UserHomePage.vue";
import UserLoginPage from "../pages/user/UserLoginPage.vue";
import TeamAddPage from "../pages/team/TeamAddPage.vue";
import TeamUpdatePage from "../pages/team/TeamUpdatePage.vue";
import ChatRoomPage from "../pages/ChatRoomPage.vue";
import TeamDetailPage from "../pages/team/TeamDetailPage.vue";


// 2. 定义一些路由
const routes = [
    { path: '/', title: '学友匹配', meta:{showBottom: true, showHeader: true, showBack: true}, component: Index },
    { path: '/team' , title: '队伍列表', meta:{showBottom: true, showHeader: true, showBack: true}, component: Team },
    { path: '/userTeam', title: '通讯录', meta:{showBottom: true, showHeader: true, showBack: true}, component: UserTeamPage },
    { path: '/user/:id', title: '个人信息', meta:{showBottom: true, showHeader: true, showBack: true}, component: User },
    { path: '/search', title: '搜索用户', meta:{showBottom: true, showHeader: true, showBack: true}, component: SearchPage },
    { path: '/user/edit', title: '编辑信息',meta:{showBottom: true, showHeader: true, showBack: true}, component: UserEditPage },
    { path: '/user/list', title: '用户列表', meta:{showBottom: true, showHeader: true, showBack: true},component: SearchResultPage },
    { path: '/user/home', title: '个人信息', meta:{showBottom: true, showHeader: true, showBack: true},component: UserHomePage},
    { path: '/user/login', title: '登录',meta:{showBottom: true, showHeader: true, showBack: true}, component: UserLoginPage},
    { path: '/team/add', title: '创建队伍',meta:{showBottom: true, showHeader: true, showBack: true}, component: TeamAddPage},
    { path: '/team/update/:id', title: '更新队伍', meta:{showBottom: false, showHeader: true, showBack: true},component: TeamUpdatePage},
    { path: '/team/chatRoom/:id', meta:{showBottom: false, showHeader: false, showBack: false}, component: ChatRoomPage},
    { path: '/team/detail/:id', title:'群聊设置', meta:{showBottom: false, showHeader: true, showBack: true}, component: TeamDetailPage},
  ]

export default routes;