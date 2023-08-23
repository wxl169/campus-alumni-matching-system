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

// 2. 定义一些路由
const routes = [
    { path: '/', title: '学友匹配',  component: Index },
    { path: '/team' , title: '队伍列表', component: Team },
    { path: '/userTeam', title: '通讯录',component: UserTeamPage },
    { path: '/user', title: '个人信息',component: User },
    { path: '/search', title: '搜索用户', component: SearchPage },
    { path: '/user/edit', title: '编辑信息', component: UserEditPage },
    { path: '/user/list', title: '用户列表', component: SearchResultPage },
    { path: '/user/home', title: '个人信息', component: UserHomePage},
    { path: '/user/login', title: '登录', component: UserLoginPage},
    { path: '/team/add', title: '创建队伍', component: TeamAddPage},
    { path: '/team/update', title: '更新队伍', component: TeamUpdatePage},

  ]

export default routes;