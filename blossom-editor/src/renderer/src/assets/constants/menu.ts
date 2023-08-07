/**
 * 菜单对象
 */
export class AsideMenu {
  // 路径
  routeName: string;
  // 图标
  icon: string;
  // 菜单名称
  menuName: string;
  // 标签
  tags: string[];
  // 自菜单
  children: AsideMenu[];

  constructor(routeName: string, icon: string, menuName: string, tags: string[], children: AsideMenu[]) {
    this.routeName = routeName;
    this.icon = icon;
    this.menuName = menuName;
    this.tags = tags;
    this.children = children;
  }
}

/**
 * 初始化静态菜单
 */
export const asideMenuConstant: AsideMenu[] =
  [
    new AsideMenu("home", "iconbl bl-home", "首页", [], []),
    new AsideMenu("cluster", "iconbl bl-server", "集群管理", [],
      [
        new AsideMenu("indexMonitorTree", "iconbl bl-node", "集群节点树", [], []),
        new AsideMenu("indexMonitorNodes", "iconbl bl-node-2", "集群节点", [], []),
        new AsideMenu("indexJvm", "iconbl bl-jvm", "节点监控", [], []),
        new AsideMenu("indexMonitorLog", "iconbl bl-elasticsearch", "日志监控", [], []),
        new AsideMenu("indexSentinel", "iconbl bl-flow", "流量监控", [], []),
        new AsideMenu("indexTracker", "iconbl bl-trace", "链路追踪", [], []),
        new AsideMenu("indexAppRelation", "iconbl bl-node-4", "应用拓扑", [], []),
        new AsideMenu("indexRedis", "iconbl bl-redis2", "缓存监控", [], []),
        new AsideMenu("indexGw", "iconbl bl-route", "路由管理", [], []),
        new AsideMenu("apiDoc", "iconbl bl-interface", "接口文档", [], []),
      ]
    ),
    // new AsideMenu("rbac", "iconbl bl-userkey", "RBAC", [], [
    //   new AsideMenu("indexUser", "iconbl bl-user3", "用户管理", [], []),
    //   new AsideMenu("indexRole", "iconbl bl-roles", "角色管理", [], []),
    //   new AsideMenu("indexPerm", "iconbl bl-key1", "权限管理", [], []),
    // ]
    // ),
    new AsideMenu("home9", "iconbl bl-bucket", "其他模块", [], [
      // new AsideMenu("home92", "iconbl bl-monitor1", "中间件监控", []),
      new AsideMenu("test", "iconbl bl-bug", "功能测试用", [], []),
      new AsideMenu("testMarkdownTuiEditor", "iconbl bl-bug", "Tui-Editor", [], []),
    ]
    ),
    // new AsideMenu("Succubus", "iconbl bl-devil", "影视剧集", [
    //   new AsideMenu("actorList", "iconbl bl-user-level", "演员列表", []),
    //   new AsideMenu("actorVideoSearch", "iconbl bl-user-search", "搜索", []),
    // ]),
  ]