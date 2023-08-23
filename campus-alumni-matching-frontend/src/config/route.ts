import SearchPage from "../pages/SearchPage.vue"
import Index from "../pages/IndexPage.vue";
import Team from "../pages/TeamPage.vue";
import User from "../pages/UserPage.vue";
import UserEditPage from "../pages/UserEditPage.vue";
import UserTeamPage from "../pages/UserTeamPage.vue";
import SearchResultPage from "../pages/SearchResultPage.vue";
import UserHomePage from "../pages/UserHomePage.vue";
import UserLoginPage from "../pages/UserLoginPage.vue";
import TeamAddPage from "../pages/TeamAddPage.vue";
import TeamUpdatePage from "../pages/TeamUpdatePage.vue";
import ChatRoomPage from "../pages/ChatRoomPage.vue";
import TeamDetailPage from "../pages/TeamDetailPage.vue";


// 2. 定义一些路由
const routes = [
    { path: '/', title: '学友匹配', meta:{showBottom: true, showHeader: true, showBack: true}, component: Index },
    { path: '/team' , title: '队伍列表', meta:{showBottom: true, showHeader: true, showBack: true}, component: Team },
    { path: '/userTeam', title: '通讯录', meta:{showBottom: true, showHeader: true, showBack: true}, component: UserTeamPage },
    { path: '/user', title: '个人信息', meta:{showBottom: true, showHeader: true, showBack: true}, component: User },
    { path: '/search', title: '搜索用户', meta:{showBottom: true, showHeader: true, showBack: true}, component: SearchPage },
    { path: '/user/edit', title: '编辑信息',meta:{showBottom: true, showHeader: true, showBack: true}, component: UserEditPage },
    { path: '/user/list', title: '用户列表', meta:{showBottom: true, showHeader: true, showBack: true},component: SearchResultPage },
    { path: '/user/home', title: '个人信息', meta:{showBottom: true, showHeader: true, showBack: true},component: UserHomePage},
    { path: '/user/login', title: '登录',meta:{showBottom: true, showHeader: true, showBack: true}, component: UserLoginPage},
    { path: '/team/add', title: '创建队伍',meta:{showBottom: true, showHeader: true, showBack: true}, component: TeamAddPage},
    { path: '/team/update', title: '更新队伍', meta:{showBottom: true, showHeader: true, showBack: true},component: TeamUpdatePage},
    { path: '/team/chatRoom/:id', meta:{showBottom: false, showHeader: false, showBack: false}, component: ChatRoomPage},
    { path: '/team/detail/:id', title:'群聊设置', meta:{showBottom: false, showHeader: true, showBack: true}, component: TeamDetailPage},
  ]

export default routes;