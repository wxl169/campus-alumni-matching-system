import SearchPage from "../pages/SearchPage.vue"
import Index from "../pages/IndexPage.vue";
import Team from "../pages/TeamPage.vue";
import User from "../pages/UserPage.vue";
import UserEditPage from "../pages/UserEditPage.vue";
import SearchResultPage from "../pages/SearchResultPage.vue";
import UserHomePage from "../pages/UserHomePage.vue";
import UserLoginPage from "../pages/UserLoginPage.vue";
import TeamAddPage from "../pages/TeamAddPage.vue";
import TeamUpdatePage from "../pages/TeamUpdatePage.vue";

// 2. 定义一些路由
const routes = [
    { path: '/', component: Index },
    { path: '/team', component: Team },
    { path: '/user', component: User },
    { path: '/search', component: SearchPage },
    { path: '/user/edit', component: UserEditPage },
    { path: '/user/list', component: SearchResultPage },
    { path: '/user/home', component: UserHomePage},
    { path: '/user/login', component: UserLoginPage},
    { path: '/team/add', component: TeamAddPage},
    { path: '/team/update', component: TeamUpdatePage},
  ]

export default routes;