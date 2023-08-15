/**
 * @see https://umijs.org/zh-CN/plugins/plugin-access
 * */
export default function access(initialState: { CurrentUser?: API.CurrentUser } | undefined) {
  const { CurrentUser } = initialState ?? {};
  return {
    canAdmin: CurrentUser && CurrentUser.userRole === 1,
  };
}
