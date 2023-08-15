export default [
  {
    path: '/user',
    layout: false,
    routes: [
      {
        path: '/user',
        routes: [{ name: '登录', path: '/user/login', component: './user/Login' }],
      },
      { component: './404' },
    ],
  },
  { path: '/welcome', name: '欢迎', icon: 'smile', component: './Welcome' },
  {
    path: '/admin',
    icon: 'crown',
    access: 'canAdmin',
    name: '管理页',
    routes: [
      {
        path: '/admin/user-manage',
        name: '用户管理',
        icon: 'user',
        component: './Admin/UserManage',
      },
      {
        path: '/admin/tag-manage',
        name: '标签管理',
        icon: 'smile',
        component: './Admin/TagManage',
      },
      { component: './404' },
    ],
  },

  { path: '/', redirect: '/welcome' },
  { component: './404' },
];
