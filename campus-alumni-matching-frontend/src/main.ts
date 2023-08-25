import { createApp } from "vue";
import "./style.css";
import App from "./App.vue";
import {
  DropdownMenu, 
  DropdownItem ,
  Button,
  Toast,
  Icon,
  NavBar,
  Tabbar,
  TabbarItem,
  Search,
  Divider,
  Tag,
  TreeSelect,
  Col,
  Row,
  Cell,
  CellGroup,
  Form,
  Field,
  ConfigProvider,
  Card,
  Empty,
  DatePicker,
  Uploader,
  Switch,
  Skeleton,
  SkeletonTitle,
  SkeletonImage,
  SkeletonAvatar,
  SkeletonParagraph,
  Grid, 
  GridItem,
  IndexBar, 
  IndexAnchor, 
  Popup,
  Space,
  Dialog, 
  RadioGroup, 
  Radio,
  FloatingBubble

} from "vant";
import "vant/es/toast/style";
import * as VueRouter from "vue-router";
import routes from "./config/route";

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
app.use(Switch);
app.use(Skeleton);
app.use(SkeletonTitle);
app.use(SkeletonImage);
app.use(SkeletonAvatar);
app.use(SkeletonParagraph);
app.use(DropdownMenu);
app.use(DropdownItem);
app.use(Grid);
app.use(GridItem);
app.use(IndexBar);
app.use(IndexAnchor);
app.use(Popup);
app.use(Space);
app.use(Dialog);
app.use(Radio);
app.use(RadioGroup);
app.use(FloatingBubble);


const router = VueRouter.createRouter({
  // 4. 内部提供了 history 模式的实现。为了简单起见，我们在这里使用 hash 模式。
  history: VueRouter.createWebHistory(),
  routes, // `routes: routes` 的缩写
});
app.use(router);

app.mount("#app");
