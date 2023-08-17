import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import { Button,Toast,Icon,NavBar,Tabbar, TabbarItem , Search,Divider,Tag ,TreeSelect,Col, Row,Cell, CellGroup,Form, Field,ConfigProvider,Card} from 'vant';
import {Empty,DatePicker,Uploader} from 'vant';
import 'vant/es/toast/style';
import * as VueRouter from "vue-router";
import routes from "./config/route"

const app = createApp(App);
app.use(Button);
app.use(Toast);
app.use(Icon);
app.use(NavBar);
app.use(Tabbar);
app.use(TabbarItem);
app.use(Search);
app.use(Divider);
app.use(Tag);
app.use(TreeSelect);
app.use(Col);
app.use(Row);
app.use(Cell);
app.use(CellGroup);
app.use(Form);
app.use(Field);
app.use(ConfigProvider);
app.use(Card);
app.use(Empty);
app.use(DatePicker);
app.use(Uploader);

const router = VueRouter.createRouter({
    // 4. 内部提供了 history 模式的实现。为了简单起见，我们在这里使用 hash 模式。
    history: VueRouter.createWebHashHistory(),
    routes, // `routes: routes` 的缩写
  })
app.use(router)

app.mount('#app');

