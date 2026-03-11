package com.ptc.core.ui;

import wt.util.resource.RBComment;
import wt.util.resource.RBEntry;
import wt.util.resource.RBPseudo;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("com.ptc.core.ui.navigationRB")
public final class navigationRB_zh_CN extends WTListResourceBundle {
    /**
     * bcwti
     *
     * Copyright (c) 2010 Parametric Technology Corporation (PTC). All Rights Reserved.
     *
     * This software is the confidential and proprietary information of PTC and is subject to the terms of a software
     * license agreement. You shall not disclose such confidential information and shall use it only in accordance with
     * the terms of the license agreement.
     *
     * ecwti
     *
     * <Need description here>
     *
     * Entries for the GLOBAL HEADER area
     **/
    @RBEntry("可视化集合")
    @RBComment("The text displayed for the link to the visualization collection in the global header.")
    public static final String PRIVATE_CONSTANT_0 = "netmarkets.VISUALIZATION_COLLECTION.description";

    @RBEntry("搜索")
    @RBComment("Used for the text on the Search link in the global header.")
    public static final String PRIVATE_CONSTANT_1 = "netmarkets.globalSearch.description";

    @RBEntry("转到搜索")
    @RBComment("Used for the text on the tooltip for the Search link in the global header.")
    public static final String PRIVATE_CONSTANT_2 = "netmarkets.globalSearch.tooltip";

    @RBEntry("hotlist_add.gif")
    public static final String PRIVATE_CONSTANT_3 = "user.hotList.icon";

    @RBEntry("我的笔记本")
    @RBComment("Used for the text on the My Notebook link in the global header.")
    public static final String PRIVATE_CONSTANT_4 = "user.hotList.description";

    @RBEntry("我的笔记本")
    @RBComment("Used for the text on the tooltip for the My Notebook link in the global header.")
    public static final String PRIVATE_CONSTANT_5 = "user.hotList.tooltip";

    @RBEntry("Email.gif")
    public static final String PRIVATE_CONSTANT_6 = "team.emailPage.icon";

    @RBEntry("通过电子邮件发送页面")
    @RBComment("Used for the text on the E-mail Page link in the global header.")
    public static final String PRIVATE_CONSTANT_7 = "team.emailPage.description";

    @RBEntry("通过电子邮件发送页面")
    @RBComment("Used for the tooltip for the E-mail Page link in the global header.")
    public static final String PRIVATE_CONSTANT_8 = "team.emailPage.tooltip";

    @RBEntry("height=750,width=850")
    public static final String PRIVATE_CONSTANT_9 = "team.emailPage.moreurlinfo";

    @RBEntry("通过电子邮件发送页面")
    @RBComment("Used for the E-mail Page title.")
    public static final String PRIVATE_CONSTANT_386 = "team.emailPage.title";

    @RBEntry("剪贴板")
    @RBComment("Used for the text on the Clipboard link in the global header.")
    public static final String PRIVATE_CONSTANT_10 = "netmarkets.clipboard.description";

    @RBEntry("剪贴板")
    public static final String PRIVATE_CONSTANT_11 = "netmarkets.clipboard.title";

    @RBEntry("访问剪切、复制和粘贴所收集对象的剪贴板功能。")
    @RBComment("Used for the tooltip for the Clipboard link in the global header.")
    public static final String PRIVATE_CONSTANT_12 = "netmarkets.clipboard.tooltip";

    @RBEntry("浏览")
    @RBComment("Title of the accordion navigation panel")
    public static final String NAVIGATION_TITLE = "NAVIGATION_TITLE";

    @RBEntry("{0}")
    @RBComment("Used for the welcome message which appears right under the second level nav.  The {0} will be replaced with the users display name.")
    public static final String WELCOME = "WELCOME";

    /**
     * Entries for the Help in the Quick Links
     **/

    @RBEntry("Windchill 帮助中心")
    @RBComment("Info entry for the WHC action from the quick links help action model.")
    public static final String WINDCHILL_HELP_CENTER = "help.helpCenter.description";

    @RBEntry("快速链接")
    @RBComment("Used for the text on the quick links menu button which appears in the global header")
    public static final String QUICK_LINKS = "QUICK_LINKS";

    @RBEntry("帮助")
    @RBComment("Used as text for displaying Windchill Help link in the global header. This was originally renamed in this file from \"netmarkets.globalWindchillHelp\".")
    public static final String PRIVATE_CONSTANT_13 = "object.help.description";

    @RBEntry("帮助")
    @RBComment("Used as tooltip for the Windchill Help link in the global header.")
    public static final String WIN_TITLE_HELP_TAB_HELP = "object.help.tooltip";

    @RBEntry("height=390,width=400")
    public static final String PRIVATE_CONSTANT_15 = "object.help.moreurlinfo";

    @RBEntry("关于可访问性")
    public static final String PRIVATE_CONSTANT_16 = "help.accessibility.description";

    @RBEntry("搜索所有帮助")
    public static final String PRIVATE_CONSTANT_17 = "help.searchAll.description";

    @RBEntry("联系 Windchill 发布")
    public static final String PRIVATE_CONSTANT_18 = "help.contactPubs.description";

    @RBEntry("Windchill 支持中心网站")
    public static final String PRIVATE_CONSTANT_19 = "help.support.description";

    @RBEntry("height=778,width=1044")
    public static final String WELCOME_GUIDE = "help.welcome.moreurlinfo";

    @RBEntry("欢迎使用 Windchill")
    public static final String WELCOME_GUIDE_DESC = "help.welcome.description";

    @RBEntry("height=600,width=775")
    public static final String HELP_ABOUT_MOREURLINFO = "help.about.moreurlinfo";

    @RBEntry("快速入门指南")
    public static final String QUICK_START = "help.quickstart.description";

    @RBEntry("Windchill 的新增功能")
    public static final String WHATS_NEW = "help.whatsnew.description";

    @RBEntry("Windchill 社区")
    public static final String COMMUNITIES = "help.communities.description";

    /**
     * Entries for Browser Window Titles
     **/
    @RBEntry("工作区")
    @RBComment("This is browser window title for Home/Workspaces subtab.")
    public static final String HOME_WIN_TITLE_WORKSPACES = "HOME_WIN_TITLE_WORKSPACES";

    @RBEntry("产品工作区: {0}")
    @RBComment("This is the product specific browser window title for Workspaces subtab.")
    public static final String PRODUCT_WIN_TITLE_WORKSPACES = "PRODUCT_WIN_TITLE_WORKSPACES";

    @RBEntry("项目工作区: {0}")
    @RBComment("This is the project specific browser window title for Workspaces subtab.")
    public static final String PROJECT_WIN_TITLE_WORKSPACES = "PROJECT_WIN_TITLE_WORKSPACES";

    @RBEntry("项目群工作区: {0}")
    @RBComment("This is the program specific browser window title for Workspaces subtab.")
    public static final String PROGRAM_WIN_TITLE_WORKSPACES = "PROGRAM_WIN_TITLE_WORKSPACES";

    @RBEntry("存储库工作区: {0}")
    @RBComment("This is the library specific browser window title for Workspaces subtab.")
    public static final String LIBRARY_WIN_TITLE_WORKSPACES = "LIBRARY_WIN_TITLE_WORKSPACES";

    @RBEntry("包")
    @RBComment("This is the browser window title for the Home/Packages subtab.")
    public static final String HOME_WIN_TITLE_PACKAGES = "HOME_WIN_TITLE_PACKAGES";

    @RBEntry("产品包: {0}")
    @RBComment("This is the product specific browser window title for the Packages subtab.")
    public static final String PRODUCT_WIN_TITLE_PACKAGES = "PRODUCT_WIN_TITLE_PACKAGES";

    @RBEntry("项目包: {0}")
    @RBComment("This is the project specific browser window title for the Packages subtab.")
    public static final String PROJECT_WIN_TITLE_PACKAGES = "PROJECT_WIN_TITLE_PACKAGES";

    @RBEntry("项目群包: {0}")
    @RBComment("This is the program specific browser window title for the Packages subtab.")
    public static final String PROGRAM_WIN_TITLE_PACKAGES = "PROGRAM_WIN_TITLE_PACKAGES";

    @RBEntry("存储库包: {0}")
    @RBComment("This is the library specific browser window title for the Packages subtab.")
    public static final String LIBRARY_WIN_TITLE_PACKAGES = "LIBRARY_WIN_TITLE_PACKAGES";

    @RBEntry("项目群网络: {0}")
    @RBComment("This is browser window title for Program/Network tab")
    public static final String PROGRAM_WIN_TITLE_NETWORK = "PROGRAM_WIN_TITLE_NETWORK";

    @RBEntry("项目网络: {0}")
    @RBComment("This is browser window title for Project/Network tab")
    public static final String PROJECT_WIN_TITLE_NETWORK = "PROJECT_WIN_TITLE_NETWORK";

    @RBEntry("产品网络: {0}")
    @RBComment("This is browser window title for Product/Network tab")
    public static final String PRODUCT_WIN_TITLE_NETWORK = "PRODUCT_WIN_TITLE_NETWORK";

    @RBEntry("存储库网络: {0}")
    @RBComment("This is browser window title for Library/Network tab")
    public static final String LIBRARY_WIN_TITLE_NETWORK = "LIBRARY_WIN_TITLE_NETWORK";

    @RBEntry("项目群计划: {0}")
    @RBComment("This is browser window title for Program/Plan tab")
    public static final String PROGRAM_WIN_TITLE_PLAN = "PROGRAM_WIN_TITLE_PLAN";

    @RBEntry("项目计划: {0}")
    @RBComment("This is browser window title for Project/Plan tab")
    public static final String PROJECT_WIN_TITLE_PLAN = "PROJECT_WIN_TITLE_PLAN";

    @RBEntry("工作区: {0}")
    @RBComment("This is browser window title for Workspace view.")
    public static final String WIN_TITLE_WORKSPACE_VIEW = "WIN_TITLE_WORKSPACE_VIEW";

    @RBEntry("文件夹: {0}")
    @RBComment("This is browser window title for Folder view")
    public static final String WIN_TITLE_FOLDER = "WIN_TITLE_FOLDER";

    @RBEntry("更改监视器")
    @RBComment("This is browser window title for Change Monitor subtab.")
    public static final String WIN_TITLE_CHANGE_MONITOR = "WIN_TITLE_CHANGE_MONITOR";

    @RBEntry("更改监视器")
    @RBComment("This is browser window title for Change Monitor subtab.")
    public static final String CHANGE_WIN_TITLE_CHANGE_MONITOR = "CHANGE_WIN_TITLE_CHANGE_MONITOR";

    @RBEntry("产品更改监视器: {0}")
    @RBComment("This is the product specific browser window title for Change Monitor subtab.")
    public static final String PRODUCT_WIN_TITLE_CHANGE_MONITOR = "PRODUCT_WIN_TITLE_CHANGE_MONITOR";

    @RBEntry("存储库更改监视器: {0}")
    @RBComment("This is the library specific browser window title for Change Monitor subtab.")
    public static final String LIBRARY_WIN_TITLE_CHANGE_MONITOR = "LIBRARY_WIN_TITLE_CHANGE_MONITOR";

    @RBEntry("更改监视器")
    @RBComment("This is browser window title for Change/Change Monitor subtab.")
    public static final String HOME_WIN_TITLE_CHANGE_MONITOR = "HOME_WIN_TITLE_CHANGE_MONITOR";

    @RBEntry("产品讨论: {0}")
    @RBComment("This is the product specific browser window title for Forum subtab.")
    public static final String PRODUCT_WIN_TITLE_FORUM = "PRODUCT_WIN_TITLE_FORUM";

    @RBEntry("项目讨论: {0}")
    @RBComment("This is the project specific browser window title for Forum subtab.")
    public static final String PROJECT_WIN_TITLE_FORUM = "PROJECT_WIN_TITLE_FORUM";

    @RBEntry("项目群讨论: {0}")
    @RBComment("This is the program specific browser window title for Forum subtab.")
    public static final String PROGRAM_WIN_TITLE_FORUM = "PROGRAM_WIN_TITLE_FORUM";

    @RBEntry("存储库讨论: {0}")
    @RBComment("This is the library specific browser window title for Forum subtab.")
    public static final String LIBRARY_WIN_TITLE_FORUM = "LIBRARY_WIN_TITLE_FORUM";

    @RBEntry("项目文件夹: {0}")
    @RBComment("This is browser window title for Project/Folders tab")
    public static final String PROJECT_WIN_TITLE_FOLDERS = "PROJECT_WIN_TITLE_FOLDERS";

    @RBEntry("项目群文件夹: {0}")
    @RBComment("This is browser window title for Project/Folders tab")
    public static final String PROGRAM_WIN_TITLE_FOLDERS = "PROGRAM_WIN_TITLE_FOLDERS";

    @RBEntry("产品文件夹: {0}")
    @RBComment("This is browser window title for Product/Folders tab")
    public static final String PRODUCT_WIN_TITLE_FOLDERS = "PRODUCT_WIN_TITLE_FOLDERS";

    @RBEntry("存储库文件夹: {0}")
    @RBComment("This is browser window title for Library/Folders tab")
    public static final String LIBRARY_WIN_TITLE_FOLDERS = "LIBRARY_WIN_TITLE_FOLDERS";

    @RBEntry("项目详细信息: {0}")
    @RBComment("This is browser window title for Project/Details tab")
    public static final String PROJECT_WIN_TITLE_DETAILS = "PROJECT_WIN_TITLE_DETAILS";

    @RBEntry("项目群详细信息: {0}")
    @RBComment("This is browser window title for Project/Details tab")
    public static final String PROGRAM_WIN_TITLE_DETAILS = "PROGRAM_WIN_TITLE_DETAILS";

    @RBEntry("产品详细信息: {0}")
    @RBComment("This is browser window title for Product/Details tab")
    public static final String PRODUCT_WIN_TITLE_DETAILS = "PRODUCT_WIN_TITLE_DETAILS";

    @RBEntry("项目团队: {0}")
    @RBComment("This is browser window title for Project/Team tab")
    public static final String PROJECT_WIN_TITLE_TEAM = "PROJECT_WIN_TITLE_TEAM";

    @RBEntry("项目群团队: {0}")
    @RBComment("This is browser window title for Program/Team tab")
    public static final String PROGRAM_WIN_TITLE_TEAM = "PROGRAM_WIN_TITLE_TEAM";

    @RBEntry("项目资源: {0}")
    @RBComment("This is browser window title for Project/Resources tab")
    public static final String PROJECT_WIN_TITLE_RESOURCES = "PROJECT_WIN_TITLE_RESOURCES";

    @RBEntry("项目群资源: {0}")
    @RBComment("This is browser window title for Program/Resources tab")
    public static final String PROGRAM_WIN_TITLE_RESOURCES = "PROGRAM_WIN_TITLE_RESOURCES";

    @RBEntry("项目会议: {0}")
    @RBComment("This is browser window title for Project/Meetings tab")
    public static final String PROJECT_WIN_TITLE_MEETINGS = "PROJECT_WIN_TITLE_MEETINGS";

    @RBEntry("项目群会议: {0}")
    @RBComment("This is browser window title for Program/Meetings tab")
    public static final String PROGRAM_WIN_TITLE_MEETINGS = "PROGRAM_WIN_TITLE_MEETINGS";

    @RBEntry("项目工作分配: {0}")
    @RBComment("This is browser window title for Project/Assignments tab")
    public static final String PROJECT_WIN_TITLE_ASSIGN = "PROJECT_WIN_TITLE_ASSIGN";

    @RBEntry("项目群工作分配: {0}")
    @RBComment("This is browser window title for Program/Assignments tab")
    public static final String PROGRAM_WIN_TITLE_ASSIGN = "PROGRAM_WIN_TITLE_ASSIGN";

    @RBEntry("项目报告: {0}")
    @RBComment("This is browser window title for Project/Reports tab")
    public static final String PROJECT_WIN_TITLE_REPORT = "PROJECT_WIN_TITLE_REPORT";

    @RBEntry("项目群报告: {0}")
    @RBComment("This is browser window title for Program/Reports tab")
    public static final String PROGRAM_WIN_TITLE_REPORT = "PROGRAM_WIN_TITLE_REPORT";

    @RBEntry("项目模板: {0}")
    @RBComment("This is browser window title for Project/Templates tab")
    public static final String PROJECT_WIN_TITLE_TEMPLATES = "PROJECT_WIN_TITLE_TEMPLATES";

    @RBEntry("项目群模板: {0}")
    @RBComment("This is browser window title for Program/Templates tab")
    public static final String PROGRAM_WIN_TITLE_TEMPLATES = "PROGRAM_WIN_TITLE_TEMPLATES";

    @RBEntry("项目实用程序: {0}")
    @RBComment("This is browser window title for Project/Utilities tab")
    public static final String WIN_TITLE_PROJ_TAB_UTIL = "PROJECT_WIN_TITLE_UTIL";

    @RBEntry("项目群实用程序: {0}")
    @RBComment("This is browser window title for Program/Utilities tab")
    public static final String PROGRAM_WIN_TITLE_UTIL = "PROGRAM_WIN_TITLE_UTIL";

    @RBEntry("Windchill - 主页")
    @RBComment("This is the browser window title for the Home page")
    public static final String WIN_TITLE_HOME_TAB_WINDCHILL_HOME = "WIN_TITLE_HOME_TAB_WINDCHILL_HOME";

    @RBEntry("主页")
    @RBComment("This is the tooltip for the Home icon")
    public static final String WIN_TITLE_HOME_TAB_HOME = "WIN_TITLE_HOME_TAB_HOME";

    @RBEntry("任务")
    @RBComment("This is browser window title for Home/Assignments tab")
    public static final String WIN_TITLE_HOME_TAB_ASSIGN = "WIN_TITLE_HOME_TAB_ASSIGN";

    @RBEntry("更新")
    @RBComment("This is browser window title for Home/Updates tab")
    public static final String WIN_TITLE_HOME_TAB_UPDATES = "WIN_TITLE_HOME_TAB_UPDATES";

    @RBEntry("已检出工作")
    @RBComment("This is browser window title for Home/Checked-out work tab")
    public static final String WIN_TITLE_HOME_TAB_CH_OUT_WORK = "WIN_TITLE_HOME_TAB_CH_OUT_WORK";

    @RBEntry("会议")
    @RBComment("This is browser window title for Home/Meetings tab")
    public static final String WIN_TITLE_HOME_TAB_MEET = "WIN_TITLE_HOME_TAB_MEET";

    @RBEntry("笔记本")
    @RBComment("This is browser window title for Home/Notebook tab")
    public static final String WIN_TITLE_HOME_TAB_NBOOK = "WIN_TITLE_HOME_TAB_NBOOK";

    @RBEntry("预订")
    @RBComment("This is browser window title for Home/Subscriptions tab")
    public static final String WIN_TITLE_HOME_TAB_SUBS = "WIN_TITLE_HOME_TAB_SUBS";

    @RBEntry("预订详细信息: {0}")
    @RBComment("This is browser window title for Subscription/Details tab")
    public static final String WIN_TITLE_HOME_TAB_SUBS_DETAILS = "WIN_TITLE_HOME_TAB_SUBS_DETAILS";

    @RBEntry("报告")
    @RBComment("This is browser window title for Home/Reports tab")
    public static final String WIN_TITLE_HOME_TAB_REPORTS = "WIN_TITLE_HOME_TAB_REPORTS";

    @RBEntry("报告: {0}")
    @RBComment("This is browser window title for Home/Reports/Report Name")
    public static final String WIN_TITLE_HOME_TAB_REP_NAME = "WIN_TITLE_HOME_TAB_REP_NAME";

    @RBEntry("实用程序")
    @RBComment("This is browser window title for Home/Utilities tab")
    public static final String WIN_TITLE_HOME_TAB_UTIL = "WIN_TITLE_HOME_TAB_UTIL";

    @RBEntry("产品列表")
    @RBComment("This is browser window title for Product tab")
    public static final String WIN_TITLE_PROD_TAB_LIST = "WIN_TITLE_PROD_TAB_LIST";

    @RBEntry("产品详细信息: {0}")
    @RBComment("This is browser window title for Product/Details tab")
    public static final String WIN_TITLE_PROD_TAB_DETAILS = "WIN_TITLE_PROD_TAB_DETAILS";

    @RBEntry("产品文件夹: {0}")
    @RBComment("This is browser window title for Product/Folders tab")
    public static final String WIN_TITLE_PROD_TAB_FOLDERS = "WIN_TITLE_PROD_TAB_FOLDERS";

    @RBEntry("产品团队: {0}")
    @RBComment("This is browser window title for Product/Team tab")
    public static final String WIN_TITLE_PROD_TAB_TEAM = "WIN_TITLE_PROD_TAB_TEAM";

    @RBEntry("产品工作分配: {0}")
    @RBComment("This is browser window title for Product/Assignments tab")
    public static final String WIN_TITLE_PROD_TAB_ASSIGN = "WIN_TITLE_PROD_TAB_ASSIGN";

    @RBEntry("工作流模板管理: {0}")
    @RBComment("This is browser window title for WorkFlow Template Administrator tab")
    public static final String WIN_TITLE_PROD_TAB_TEMPLATES = "WIN_TITLE_PROD_TAB_TEMPLATES";

    @RBEntry("产品实用程序: {0}")
    @RBComment("This is browser window title for Product/Utilities tab")
    public static final String WIN_TITLE_PROD_TAB_UTIL = "WIN_TITLE_PROD_TAB_UTIL";

    @RBEntry("产品报告: {0}")
    @RBComment("This is browser window title for Product/Reports tab")
    public static final String WIN_TITLE_PROD_TAB_REPORTS = "WIN_TITLE_PROD_TAB_REPORTS";

    @RBEntry("项目列表")
    @RBComment("This is browser window title for Project tab")
    public static final String WIN_TITLE_PROJ_TAB_LIST = "WIN_TITLE_PROJ_TAB_LIST";

    @RBEntry("项目详细信息: {0}")
    @RBComment("This is browser window title for Project/Details tab")
    public static final String WIN_TITLE_PROJ_TAB_DETAILS = "WIN_TITLE_PROJ_TAB_DETAILS";

    @RBEntry("项目文件夹: {0}")
    @RBComment("This is browser window title for Project/Folders tab")
    public static final String WIN_TITLE_PROJ_TAB_FOLDERS = "WIN_TITLE_PROJ_TAB_FOLDERS";

    @RBEntry("项目团队: {0}")
    @RBComment("This is browser window title for Project/Team tab")
    public static final String WIN_TITLE_PROJ_TAB_TEAM = "WIN_TITLE_PROJ_TAB_TEAM";

    @RBEntry("项目资源: {0}")
    @RBComment("This is browser window title for Project/Resources tab")
    public static final String WIN_TITLE_PROJ_TAB_RESOURCE = "WIN_TITLE_PROJ_TAB_RESOURCE";

    @RBEntry("项目会议: {0}")
    @RBComment("This is browser window title for Project/Meetings tab")
    public static final String WIN_TITLE_PROJ_TAB_MEET = "WIN_TITLE_PROJ_TAB_MEET";

    @RBEntry("项目工作分配: {0}")
    @RBComment("This is browser window title for Project/Assignments tab")
    public static final String WIN_TITLE_PROJ_TAB_ASSIGN = "WIN_TITLE_PROJ_TAB_ASSIGN";

    @RBEntry("更改: {0}")
    @RBComment("This is browser window title for Project/Change tab")
    public static final String WIN_TITLE_PROJ_TAB_CHANGE = "WIN_TITLE_PROJ_TAB_CHANGE";

    @RBEntry("项目报告: {0}")
    @RBComment("This is browser window title for Project/Reports tab")
    public static final String WIN_TITLE_PROJ_TAB_REPORT = "WIN_TITLE_PROJ_TAB_REPORT";

    @RBEntry("项目模板: {0}")
    @RBComment("This is browser window title for Project/Templates tab")
    public static final String WIN_TITLE_PROJ_TAB_TEMPLATES = "WIN_TITLE_PROJ_TAB_TEMPLATES";

    @RBEntry("问题")
    @RBComment("This is browser window title for Change/Issues tab")
    public static final String WIN_TITLE_CHANGE_TAB_REPORT = "WIN_TITLE_CHANGE_TAB_REPORT";

    @RBEntry("超差")
    @RBComment("This is browser window title for Change/Variances tab")
    public static final String WIN_TITLE_CHANGE_TAB_VARIANCE = "WIN_TITLE_CHANGE_TAB_VARIANCE";

    @RBEntry("更改请求")
    @RBComment("This is browser window title for Change/Change Requests tab")
    public static final String WIN_TITLE_CHANGE_TAB_CR = "WIN_TITLE_CHANGE_TAB_CR";

    @RBEntry("更改通告")
    @RBComment("This is browser window title for Change/Change Notices tab")
    public static final String WIN_TITLE_CHANGE_TAB_NOTICE = "WIN_TITLE_CHANGE_TAB_NOTICE";

    @RBEntry("更改报告")
    @RBComment("This is browser window title for Change/Reports tab")
    public static final String WIN_TITLE_CHANGE_TAB_REPORTS = "WIN_TITLE_CHANGE_TAB_REPORTS";

    @RBEntry("存储库列表")
    @RBComment("This is browser window title for Library tab")
    public static final String WIN_TITLE_LIB_TAB_LIST = "WIN_TITLE_LIB_TAB_LIST";

    @RBEntry("存储库详细信息: {0}")
    @RBComment("This is browser window title for Library/Details tab")
    public static final String WIN_TITLE_LIB_TAB_DETAILS = "WIN_TITLE_LIB_TAB_DETAILS";

    @RBEntry("存储库文件夹: {0}")
    @RBComment("This is browser window title for Library/Folders tab")
    public static final String WIN_TITLE_LIB_TAB_FOLDERS = "WIN_TITLE_LIB_TAB_FOLDERS";

    @RBEntry("存储库团队: {0}")
    @RBComment("This is browser window title for Library/Team tab")
    public static final String WIN_TITLE_LIB_TAB_TEAM = "WIN_TITLE_LIB_TAB_TEAM";

    @RBEntry("存储库工作分配: {0}")
    @RBComment("This is browser window title for Library/Assignments tab")
    public static final String WIN_TITLE_LIB_TAB_ASSIGN = "WIN_TITLE_LIB_TAB_ASSIGN";

    @RBEntry("存储库模板: {0}")
    @RBComment("This is browser window title for Library/Templates tab")
    public static final String WIN_TITLE_LIB_TAB_TEMPLATES = "WIN_TITLE_LIB_TAB_TEMPLATES";

    @RBEntry("存储库报告: {0}")
    @RBComment("This is browser window title for Library/Reports tab")
    public static final String WIN_TITLE_LIB_TAB_REPORT = "WIN_TITLE_LIB_TAB_REPORT";

    @RBEntry("存储库实用程序: {0}")
    @RBComment("This is browser window title for Library/Utilities tab")
    public static final String WIN_TITLE_LIB_TAB_UTIL = "WIN_TITLE_LIB_TAB_UTIL";

    @RBEntry("组织列表")
    @RBComment("This is browser window title for Organization tab")
    public static final String WIN_TITLE_ORG_TAB_LIST = "WIN_TITLE_ORG_TAB_LIST";

    @RBEntry("组织详细信息: {0}")
    @RBComment("This is browser window title for Organization/Details tab")
    public static final String WIN_TITLE_ORG_TAB_DETAILS = "WIN_TITLE_ORG_TAB_DETAILS";

    @RBEntry("组织文件夹: {0}")
    @RBComment("This is browser window title for Organization/Folders tab")
    public static final String WIN_TITLE_ORG_TAB_FOLDERS = "WIN_TITLE_ORG_TAB_FOLDERS";

    @RBEntry("组织成员: {0}")
    @RBComment("This is browser window title for Organization/Members tab")
    public static final String WIN_TITLE_ORG_TAB_MEMBERS = "WIN_TITLE_ORG_TAB_MEMBERS";

    @RBEntry("组织创建者: {0}")
    @RBComment("This is browser window title for Organization/Creators tab")
    public static final String WIN_TITLE_ORG_TAB_CREATOR = "WIN_TITLE_ORG_TAB_CREATOR";

    @RBEntry("组织管理员: {0}")
    @RBComment("This is browser window title for Organization/Administrators tab")
    public static final String WIN_TITLE_ORG_TAB_ADMIN = "WIN_TITLE_ORG_TAB_ADMIN";

    @RBEntry("组织分组: {0}")
    @RBComment("This is browser window title for Organization/Groups tab")
    public static final String WIN_TITLE_ORG_TAB_GROUPS = "WIN_TITLE_ORG_TAB_GROUPS";

    @RBEntry("组织角色: {0}")
    @RBComment("This is browser window title for Organization/Roles tab")
    public static final String WIN_TITLE_ORG_TAB_ROLES = "WIN_TITLE_ORG_TAB_ROLES";

    @RBEntry("组织配置文件: {0}")
    @RBComment("This is browser window title for Organization/Profiles tab")
    public static final String WIN_TITLE_ORG_TAB_PROFILES = "WIN_TITLE_ORG_TAB_PROFILES";

    @RBEntry("组织团队: {0}")
    @RBComment("This is browser window title for Organization/Teams tab")
    public static final String WIN_TITLE_ORG_TAB_TEAMS = "WIN_TITLE_ORG_TAB_TEAMS";

    @RBEntry("组织类型: {0}")
    @RBComment("This is browser window title for Organization/Types tab")
    public static final String WIN_TITLE_ORG_TAB_TYPES = "WIN_TITLE_ORG_TAB_TYPES";

    @RBEntry("组织报告: {0}")
    @RBComment("This is browser window title for Organization/Reports tab")
    public static final String WIN_TITLE_ORG_TAB_REPORTS = "WIN_TITLE_ORG_TAB_REPORTS";

    @RBEntry("组织模板: {0}")
    @RBComment("This is browser window title for Organization/Templates tab")
    public static final String WIN_TITLE_ORG_TAB_TEMPLATES = "WIN_TITLE_ORG_TAB_TEMPLATES";

    @RBEntry("组织实用程序: {0}")
    @RBComment("This is browser window title for Organization/Utilities tab")
    public static final String WIN_TITLE_ORG_TAB_UTIL = "WIN_TITLE_ORG_TAB_UTIL";

    @RBEntry("站点文件夹")
    @RBComment("This is browser window title for Site/Folders tab")
    public static final String WIN_TITLE_SITE_TAB_FOLDERS = "WIN_TITLE_SITE_TAB_FOLDERS";

    @RBEntry("站点管理员")
    @RBComment("This is browser window title for Site/Administrators tab")
    public static final String WIN_TITLE_SITE_TAB_ADMIN = "WIN_TITLE_SITE_TAB_ADMIN";

    @RBEntry("站点配置文件")
    @RBComment("This is browser window title for Site/Profiles tab")
    public static final String WIN_TITLE_SITE_TAB_PROFILES = "WIN_TITLE_SITE_TAB_PROFILES";

    @RBEntry("站点类型")
    @RBComment("This is browser window title for Site/Types tab")
    public static final String WIN_TITLE_SITE_TAB_TYPES = "WIN_TITLE_SITE_TAB_TYPES";

    @RBEntry("站点模板")
    @RBComment("This is browser window title for Site/Templates tab")
    public static final String WIN_TITLE_SITE_TAB_TEMPLATES = "WIN_TITLE_SITE_TAB_TEMPLATES";

    @RBEntry("站点报告")
    @RBComment("This is browser window title for Site/Reports tab")
    public static final String WIN_TITLE_SITE_TAB_REPORTS = "WIN_TITLE_SITE_TAB_REPORTS";

    @RBEntry("站点实用程序")
    @RBComment("This is browser window title for Site/Utilities tab")
    public static final String WIN_TITLE_SITE_TAB_UTIL = "WIN_TITLE_SITE_TAB_UTIL";

    @RBEntry("包列表")
    @RBComment("This is browser window title for Package tab")
    public static final String WIN_TITLE_PACKAGE_TAB_LIST = "WIN_TITLE_PACKAGE_TAB_LIST";

    @RBEntry("项目群列表")
    @RBComment("This is browser window title for Program list tab")
    public static final String WIN_TITLE_PROGRAM_TAB_LIST = "WIN_TITLE_PROGRAM_TAB_LIST";

    @RBEntry("采购环境")
    @RBComment("This is browser window title for Source Contexts")
    public static final String WIN_TITLE_SUPPLIER_TAB_SRC_CONTEXT = "WIN_TITLE_SUPPLIER_TAB_SRC_CONTEXT";

    @RBEntry("采购规则")
    @RBComment("This is browser window title for Source Rules")
    public static final String WIN_TITLE_SUPPLIER_TAB_SRC_RULE = "WIN_TITLE_SUPPLIER_TAB_SRC_RULE";

    @RBEntry("采购管理员")
    @RBComment("This is browser window title for Source Administrators")
    public static final String WIN_TITLE_SUPPLIER_TAB_SRC_ADMIN = "WIN_TITLE_SUPPLIER_TAB_SRC_ADMIN";

    @RBEntry("采购报告")
    @RBComment("This is browser window title for Source Reports")
    public static final String WIN_TITLE_SUPPLIER_TAB_SRC_REPORT = "WIN_TITLE_SUPPLIER_TAB_SRC_REPORT";

    @RBEntry("供应商列表")
    @RBComment("This is browser window title for Supplier list tab")
    public static final String WIN_TITLE_SUPPLIER_TAB_LIST = "WIN_TITLE_SUPPLIER_TAB_LIST";

    @RBEntry("协议: {0}")
    @RBComment("This is browser window title for the agreements view")
    public static final String WIN_TITLE_AGREEMENTS = "WIN_TITLE_AGREEMENTS";

    @RBEntry("站点协议")
    @RBComment("This is browser window title for Site/Agreements tab")
    public static final String SITE_WIN_TITLE_AGREEMENTS = "SITE_WIN_TITLE_AGREEMENTS";

    @RBEntry("组织协议: {0}")
    @RBComment("This is browser window title for Organization/Agreements tab")
    public static final String ORG_WIN_TITLE_AGREEMENTS = "ORG_WIN_TITLE_AGREEMENTS";

    @RBEntry("项目协议: {0}")
    @RBComment("This is browser window title for Project/Agreements tab")
    public static final String PROJECT_WIN_TITLE_AGREEMENTS = "PROJECT_WIN_TITLE_AGREEMENTS";

    @RBEntry("产品协议: {0}")
    @RBComment("This is browser window title for Product/Agreements tab")
    public static final String PRODUCT_WIN_TITLE_AGREEMENTS = "PRODUCT_WIN_TITLE_AGREEMENTS";

    @RBEntry("存储库协议: {0}")
    @RBComment("This is browser window title for Library/Agreements tab")
    public static final String LIBRARY_WIN_TITLE_AGREEMENTS = "LIBRARY_WIN_TITLE_AGREEMENTS";

    @RBEntry("类型管理")
    @RBComment("This is browser window title for Type Administration")
    public static final String WIN_TITLE_TYPE_ADMINISTRATION = "WIN_TITLE_TYPE_ADMINISTRATION";

    /**
     * Entries for the CONTEXT BAR
     **/
    @RBEntry("文件夹")
    @RBComment("This is the text displayed on the context bar for folders")
    public static final String CONTEXTBAR_FOLDER = "CONTEXTBAR_FOLDER";

    @RBEntry("工作区")
    @RBComment("This is the text displayed on the context bar for workspaces")
    public static final String CONTEXTBAR_WORKSPACE = "CONTEXTBAR_WORKSPACE";

    @RBEntry("产品")
    @RBComment("This is the text displayed on the context bar for Products")
    public static final String CONTEXTBAR_CONTAINER_PRODUCT = "CONTEXTBAR_CONTAINER_PRODUCT";

    @RBEntry("项目")
    @RBComment("This is the text displayed on the context bar for Projects")
    public static final String CONTEXTBAR_CONTAINER_PROJECT = "CONTEXTBAR_CONTAINER_PROJECT";

    @RBEntry("项目群")
    @RBComment("This is the text displayed on the context bar for Programs")
    public static final String CONTEXTBAR_CONTAINER_PROGRAM = "CONTEXTBAR_CONTAINER_PROGRAM";

    @RBEntry("包")
    @RBComment("This is the text displayed on the context bar for Packages")
    public static final String CONTEXTBAR_CONTAINER_PACKAGE = "CONTEXTBAR_CONTAINER_PACKAGE";

    @RBEntry("存储库")
    @RBComment("This is the text displayed on the context bar for Libraries")
    public static final String CONTEXTBAR_CONTAINER_LIBRARY = "CONTEXTBAR_CONTAINER_LIBRARY";

    @RBEntry("组织")
    @RBComment("This is the text displayed on the context bar for Organizations")
    public static final String CONTEXTBAR_CONTAINER_ORG = "CONTEXTBAR_CONTAINER_ORG";

    @RBEntry("供应商")
    @RBComment("This is the text displayed on the context bar for Suppliers")
    public static final String CONTEXTBAR_CONTAINER_SUPPLIER = "CONTEXTBAR_CONTAINER_SUPPLIER";

    @RBEntry("采购环境")
    @RBComment("This is the text displayed on the context bar for Sourcing Contexts")
    public static final String CONTEXTBAR_CONTAINER_SRC_CONTEXT = "CONTEXTBAR_CONTAINER_SRC_CONTEXT";

    @RBEntry("状况")
    @RBComment("This is the text displayed on the context bar for the label for project status info that appears on the right side.")
    public static final String CONTEXTBAR_PROJECT_STATUS = "CONTEXTBAR_PROJECT_STATUS";

    @RBEntry("状态")
    @RBComment("This is the text displayed on the context bar for the label for project state info that appears on the right side.")
    public static final String CONTEXTBAR_PROJECT_STATE = "CONTEXTBAR_PROJECT_STATE";

    @RBEntry("质量")
    @RBComment("This is the text displayed on the context bar for Quality")
    public static final String CONTEXTBAR_CONTAINER_QMS = "CONTEXTBAR_CONTAINER_QMS";

    /**
     * Entries for new MAIN TAB navigation actions.
     **/
    @RBEntry("最近的访问")
    @RBComment("Used for the text on the home tab. The <U class=")
    public static final String PRIVATE_CONSTANT_20 = "navigation.recentContexts.description";

    @RBEntry("R")
    @RBPseudo(false)
    @RBComment("Mnemonic for the Home first level tab. This should be a character that matches the character surrounded by the <U class=")
    public static final String PRIVATE_CONSTANT_21 = "navigation.recentContexts.hotkey";

    @RBEntry("主要选项卡: 最近的上下文")
    @RBComment("Used for the tooltip on the  recent contexts tab")
    public static final String PRIVATE_CONSTANT_22 = "navigation.recentContexts.tooltip";

    @RBEntry("活动主要选项卡: 最近的上下文")
    public static final String PRIVATE_CONSTANT_23 = "navigation.recentContexts.activetooltip";

    @RBEntry("navigator_recentContexts.png")
    @RBComment("DO NOT TRANSLATE")
    public static final String PRIVATE_CONSTANT_380 = "navigation.recentContexts.icon";

    @RBEntry("主页(<U class='mnemonic'>m</U>)")
    @RBComment("Used for the text on the home tab. The <U class=")
    public static final String PRIVATE_CONSTANT_24 = "navigation.home.description";

    @RBEntry("m")
    @RBPseudo(false)
    @RBComment("Mnemonic for the Home first level tab. This should be a character that matches the character surrounded by the <U class=")
    public static final String PRIVATE_CONSTANT_25 = "navigation.home.hotkey";

    @RBEntry("主要选项卡: 主页")
    @RBComment("Used for the tooltip on the home tab")
    public static final String PRIVATE_CONSTANT_26 = "navigation.home.tooltip";

    @RBEntry("活动主要选项卡: 主页")
    public static final String PRIVATE_CONSTANT_27 = "navigation.home.activetooltip";

    @RBEntry("navigator_home.png")
    @RBComment("DO NOT TRANSLATE")
    public static final String PRIVATE_CONSTANT_28 = "navigation.home.icon";

    @RBEntry("搜索(<U class='mnemonic'>S</U>)")
    @RBComment("Used for the text on the search tab. The <U class=\"mnemonic\"> </U> tag should be put around the character that is the access key.")
    public static final String PRIVATE_CONSTANT_29 = "navigation.search.description";

    @RBEntry("s")
    @RBPseudo(false)
    @RBComment("Mnemonic for the search first level tab. This should be a character that matches the character surrounded by the <U class=\"mnemonic\"> </U> tag in the value line above.")
    public static final String PRIVATE_CONSTANT_30 = "navigation.search.hotkey";

    @RBEntry("主要选项卡: 搜索")
    @RBComment("Used for the tooltip on the search tab")
    public static final String PRIVATE_CONSTANT_31 = "navigation.search.tooltip";

    @RBEntry("活动主要选项卡: 搜索")
    public static final String PRIVATE_CONSTANT_32 = "navigation.search.activetooltip";

    @RBEntry("navigation_search.gif")
    @RBComment("DO NOT TRANSLATE")
    public static final String PRIVATE_CONSTANT_33 = "navigation.search.icon";

    @RBEntry("分类搜索")
    @RBComment("Used for the text on the classification subtab under the search main tab.")
    public static final String PRIVATE_CONSTANT_34 = "netmarkets.classificationSearch.description";

    @RBEntry("次级选项卡: 搜索")
    @RBComment("Used for the text on the tooltip for the overview sub tab under the home main tab.")
    public static final String PRIVATE_CONSTANT_35 = "netmarkets.classificationSearch.tooltip";

    @RBEntry("活动次级选项卡: 分类搜索")
    public static final String PRIVATE_CONSTANT_36 = "netmarkets.classificationSearch.activetooltip";

    @RBEntry("最近的项目群")
    @RBComment("Used for the text on the program tab.  The <U class=")
    public static final String PRIVATE_CONSTANT_37 = "navigation.program.description";

    @RBEntry("o")
    @RBPseudo(false)
    @RBComment("Mnemonic for the Program first level tab. This should be a character that matches the character surrounded by the <U class=")
    public static final String PRIVATE_CONSTANT_38 = "navigation.program.hotkey";

    @RBEntry("主要选项卡: 项目群")
    @RBComment("Used for the tooltip on the program tab")
    public static final String PRIVATE_CONSTANT_39 = "navigation.program.tooltip";

    @RBEntry("活动主要选项卡:项目群")
    public static final String PRIVATE_CONSTANT_40 = "navigation.program.activetooltip";

    @RBEntry("navigator_program.png")
    @RBComment("DO NOT TRANSLATE")
    public static final String PRIVATE_CONSTANT_41 = "navigation.program.icon";

    @RBEntry("最近的产品")
    @RBComment("Used for the text on the product tab.  The <U class=")
    public static final String PRIVATE_CONSTANT_42 = "navigation.product.description";

    @RBEntry("p")
    @RBPseudo(false)
    @RBComment("Mnemonic for the Product first level tab. This should be a character that matches the character surrounded by the <U class=")
    public static final String PRIVATE_CONSTANT_43 = "navigation.product.hotkey";

    @RBEntry("主要选项卡:产品")
    @RBComment("Used for the tooltip on the product tab")
    public static final String PRIVATE_CONSTANT_44 = "navigation.product.tooltip";

    @RBEntry("活动主要选项卡: 产品")
    public static final String PRIVATE_CONSTANT_45 = "navigation.product.activetooltip";

    @RBEntry("navigator_product.png")
    @RBComment("DO NOT TRANSLATE")
    public static final String PRIVATE_CONSTANT_46 = "navigation.product.icon";

    @RBEntry("最近的项目")
    @RBComment("Used for the text on the project tab.  The <U class=")
    public static final String PRIVATE_CONSTANT_47 = "navigation.project.description";

    @RBEntry("j")
    @RBPseudo(false)
    @RBComment("Mnemonic for the Project first level tab. This should be a character that matches the character surrounded by the <U class=")
    public static final String PRIVATE_CONSTANT_48 = "navigation.project.hotkey";

    @RBEntry("主要选项卡: 项目")
    @RBComment("Used for the tooltip on the project tab")
    public static final String PRIVATE_CONSTANT_49 = "navigation.project.tooltip";

    @RBEntry("活动主要选项卡: 项目")
    public static final String PRIVATE_CONSTANT_50 = "navigation.project.activetooltip";

    @RBEntry("navigator_project.png")
    @RBComment("DO NOT TRANSLATE")
    public static final String PRIVATE_CONSTANT_51 = "navigation.project.icon";

    @RBEntry("更改")
    @RBComment("Used for the text on the change tab.  The <U class=")
    public static final String PRIVATE_CONSTANT_52 = "navigation.change.description";

    @RBEntry("c")
    @RBPseudo(false)
    @RBComment("Mnemonic for the Change first level tab. This should be a character that matches the character surrounded by the <U class=")
    public static final String PRIVATE_CONSTANT_53 = "navigation.change.hotkey";

    @RBEntry("主要选项卡: 更改")
    @RBComment("Used for the tooltip on the change tab")
    public static final String PRIVATE_CONSTANT_54 = "navigation.change.tooltip";

    @RBEntry("活动主要选项卡: 更改")
    public static final String PRIVATE_CONSTANT_55 = "navigation.change.activetooltip";

    @RBEntry("navigator_change.png")
    @RBComment("DO NOT TRANSLATE")
    public static final String PRIVATE_CONSTANT_56 = "navigation.change.icon";

    @RBEntry("最近的存储库")
    @RBComment("Used for the text on the library tab.  The <U class=")
    public static final String PRIVATE_CONSTANT_57 = "navigation.library.description";

    @RBEntry("l")
    @RBPseudo(false)
    @RBComment("Mnemonic for the Library first level tab. This should be a character that matches the character surrounded by the <U class=")
    public static final String PRIVATE_CONSTANT_58 = "navigation.library.hotkey";

    @RBEntry("主要选项卡: 存储库")
    @RBComment("Used for the tooltip on the library tab")
    public static final String PRIVATE_CONSTANT_59 = "navigation.library.tooltip";

    @RBEntry("活动主要选项卡: 存储库")
    public static final String PRIVATE_CONSTANT_60 = "navigation.library.activetooltip";

    @RBEntry("navigator_library.png")
    @RBComment("DO NOT TRANSLATE")
    public static final String PRIVATE_CONSTANT_61 = "navigation.library.icon";

    @RBEntry("组织")
    @RBComment("Used for the text on the org tab.    The <U class=")
    public static final String PRIVATE_CONSTANT_62 = "navigation.org.description";

    @RBEntry("z")
    @RBPseudo(false)
    @RBComment("Mnemonic for the Library first level tab. This should be a character that matches the character surrounded by the <U class=")
    public static final String PRIVATE_CONSTANT_63 = "navigation.org.hotkey";

    @RBEntry("主要选项卡: 组织")
    @RBComment("Used for the tooltip on the org tab")
    public static final String PRIVATE_CONSTANT_64 = "navigation.org.tooltip";

    @RBEntry("活动主要选项卡: 组织")
    public static final String PRIVATE_CONSTANT_65 = "navigation.org.activetooltip";

    @RBEntry("navigator_org.png")
    @RBComment("DO NOT TRANSLATE")
    public static final String PRIVATE_CONSTANT_66 = "navigation.org.icon";

    @RBEntry("站点")
    @RBComment("Used for the text on the site tab")
    public static final String PRIVATE_CONSTANT_67 = "navigation.site.description";

    @RBEntry("q")
    @RBPseudo(false)
    @RBComment("Mnemonic for the Library first level tab.")
    public static final String PRIVATE_CONSTANT_68 = "navigation.site.hotkey";

    @RBEntry("主要选项卡: 站点")
    @RBComment("Used for the tooltip on the site tab")
    public static final String PRIVATE_CONSTANT_69 = "navigation.site.tooltip";

    @RBEntry("活动主要选项卡: 站点")
    public static final String PRIVATE_CONSTANT_70 = "navigation.site.activetooltip";

    @RBEntry("navigator_site.png")
    @RBComment("DO NOT TRANSLATE")
    public static final String PRIVATE_CONSTANT_71 = "navigation.site.icon";

    @RBEntry("最近的供应商")
    @RBComment("Used as the label for the Supplier navigation tab")
    public static final String PRIVATE_CONSTANT_72 = "navigation.supplier.description";

    @RBEntry("主要选项卡: 供应商")
    @RBComment("Used as the tooltip for the Supplier navigation tab")
    public static final String PRIVATE_CONSTANT_73 = "navigation.supplier.tooltip";

    @RBEntry("活动主要选项卡: 供应商")
    public static final String PRIVATE_CONSTANT_74 = "navigation.supplier.activetooltip";

    @RBEntry("navigator_supplier.png")
    @RBComment("DO NOT TRANSLATE")
    public static final String PRIVATE_CONSTANT_75 = "navigation.supplier.icon";

    /**
     * Resource Strings for the list links in the navigation pane
     **/
    @RBEntry("查看所有产品")
    public static final String PRIVATE_CONSTANT_76 = "navigation.product.list.description";

    @RBEntry("查看所有产品")
    @RBComment("Tooltip for the link to the product list page in the navigation pane.")
    public static final String PRIVATE_CONSTANT_77 = "navigation.product.list.tooltip";

    @RBEntry("查看所有项目")
    public static final String PRIVATE_CONSTANT_78 = "navigation.project.list.description";

    @RBEntry("查看所有项目")
    @RBComment("Tooltip for the link to the Project list page in the navigation pane.")
    public static final String PRIVATE_CONSTANT_79 = "navigation.project.list.tooltip";

    @RBEntry("查看所有项目群")
    public static final String PRIVATE_CONSTANT_80 = "navigation.program.list.description";

    @RBEntry("查看所有项目群")
    @RBComment("Tooltip for the link to the program list page in the navigation pane.")
    public static final String PRIVATE_CONSTANT_81 = "navigation.program.list.tooltip";

    @RBEntry("查看所有存储库")
    public static final String PRIVATE_CONSTANT_82 = "navigation.library.list.description";

    @RBEntry("查看所有存储库")
    @RBComment("Tooltip for the link to the Library list page in the navigation pane.")
    public static final String PRIVATE_CONSTANT_83 = "navigation.library.list.tooltip";

    @RBEntry("查看所有组织")
    public static final String PRIVATE_CONSTANT_84 = "navigation.org.list.description";

    @RBEntry("查看所有组织")
    @RBComment("Tooltip for the link to the org list page in the navigation pane.")
    public static final String PRIVATE_CONSTANT_85 = "navigation.org.list.tooltip";

    /**
     * Resource strings for the common subtabs (used in more than one 1st level tab)
     **/
    @RBEntry("工作区")
    @RBComment("Used for the text on the Workspaces sub tab that appears under multiple main tabs")
    public static final String PRIVATE_CONSTANT_86 = "workspaces.MyWorkspace.description";

    @RBEntry("次级选项卡: 工作区")
    @RBComment("Used for the text on tooltip for the Workspaces sub tab that appears under multiple main tabs")
    public static final String PRIVATE_CONSTANT_87 = "workspaces.MyWorkspace.tooltip";

    @RBEntry("活动次级选项卡: 工作区")
    public static final String PRIVATE_CONSTANT_88 = "workspaces.MyWorkspace.activetooltip";

    @RBEntry("更改监视器")
    @RBComment("Used for the text on the Change Monitor subtab that appears under multiple main tabs")
    public static final String PRIVATE_CONSTANT_89 = "change.changeMonitor.description";

    @RBEntry("次级选项卡: 更改监视器")
    @RBComment("Used for the text on the tooltip for the Change Monitor subtab that appears under multiple main tabs")
    public static final String PRIVATE_CONSTANT_90 = "change.changeMonitor.tooltip";

    @RBEntry("活动次级选项卡: 更改监视器")
    public static final String PRIVATE_CONSTANT_91 = "change.changeMonitor.activetooltip";

    @RBEntry("网络")
    @RBComment("Used for the text on the Network subtab that appears under multiple main tabs")
    public static final String PRIVATE_CONSTANT_92 = "network.listNetwork.description";

    @RBEntry("次级选项卡: 网络")
    @RBComment("Used for the text on the tooltip for the Network subtab that appears under multiple main tabs")
    public static final String PRIVATE_CONSTANT_93 = "network.listNetwork.tooltip";

    @RBEntry("活动次级选项卡: 网络")
    public static final String PRIVATE_CONSTANT_94 = "network.listNetwork.activetooltip";

    /**
     * HOME tab specific resources
     **/
    @RBEntry("概述")
    @RBComment("Used for the text on the overview subtab under the home main tab.")
    public static final String PRIVATE_CONSTANT_95 = "netmarkets.overview.description";

    @RBEntry("次级选项卡: 概述")
    @RBComment("Used for the text on the tooltip for the overview sub tab under the home main tab.")
    public static final String PRIVATE_CONSTANT_96 = "netmarkets.overview.tooltip";

    @RBEntry("活动次级选项卡: 概述")
    public static final String PRIVATE_CONSTANT_97 = "netmarkets.overview.activetooltip";

    @RBEntry("已检出工作")
    @RBComment("Used for the text on the checked out work subtab under the home main tab.")
    public static final String PRIVATE_CONSTANT_98 = "user.listCheckedOutWork.description";

    @RBEntry("次级选项卡: 已检出工作")
    @RBComment("Used for the text on the tooltip for the checked out work sub tab under the home main tab.")
    public static final String PRIVATE_CONSTANT_99 = "user.listCheckedOutWork.tooltip";

    @RBEntry("活动次级选项卡: 已检出工作")
    public static final String PRIVATE_CONSTANT_100 = "user.listCheckedOutWork.activetooltip";

    @RBEntry("任务")
    @RBComment("Used for the text on the Assignments subtab that appears under the home tab")
    public static final String PRIVATE_CONSTANT_101 = "work.listAssignments.description";

    @RBEntry("次级选项卡: 工作分配")
    @RBComment("Used for the text on the tooltip for the Assignments subtab that appears under the home tab")
    public static final String PRIVATE_CONSTANT_102 = "work.listAssignments.tooltip";

    @RBEntry("活动次级选项卡: 工作分配")
    public static final String PRIVATE_CONSTANT_103 = "work.listAssignments.activetooltip";

    @RBEntry("笔记本")
    @RBComment("Used for the text on the notebook subtab under the home main tab.")
    public static final String PRIVATE_CONSTANT_104 = "user.userNotebook.description";

    @RBEntry("次级选项卡: 笔记本")
    @RBComment("Used for the text on the tooltip for the notebook sub tab under the home main tab.")
    public static final String PRIVATE_CONSTANT_105 = "user.userNotebook.tooltip";

    @RBEntry("活动次级选项卡: 笔记本")
    public static final String PRIVATE_CONSTANT_106 = "user.userNotebook.activetooltip";

    @RBEntry("报告")
    @RBComment("Used for the text on the creports subtab under the home main tab.")
    public static final String PRIVATE_CONSTANT_107 = "user.reports.description";

    @RBEntry("次级选项卡: 报告")
    @RBComment("Used for the text on the tooltip for the reports sub tab under the home main tab.")
    public static final String PRIVATE_CONSTANT_108 = "user.reports.tooltip";

    @RBEntry("lmt=25")
    @RBPseudo(false)
    public static final String PRIVATE_CONSTANT_109 = "user.reports.moreurlinfo";

    @RBEntry("活动次级选项卡: 报告")
    public static final String PRIVATE_CONSTANT_110 = "user.reports.activetooltip";

    @RBEntry("已保存报告")
    @RBComment("Used for the text on the saved reports subtab under the home main tab.")
    public static final String SAVED_REPORTS_DESC = "user.savedReports.description";

    @RBEntry("次级选项卡: 已保存报告")
    @RBComment("Used for the text on the tooltip for the saved reports sub tab under the home main tab.")
    public static final String SAVED_REPORTS_TOOLTIP = "user.savedReports.tooltip";

    @RBEntry("活动次级选项卡: 已保存报告")
    public static final String SAVED_REPORTS_ACTIVETOOLTIP = "user.savedReports.activetooltip";

    @RBEntry("数据监控器")
    @RBComment("Used for the text on the data monitors subtab under the home main tab.")
    public static final String DATA_MONITORS_DESC = "user.dataMonitors.description";

    @RBEntry("次级选项卡: 数据监控器")
    @RBComment("Used for the text on the tooltip for the data monitors sub tab under the home main tab.")
    public static final String DATA_MONITORS_TOOLTIP = "user.dataMonitors.tooltip";

    @RBEntry("活动次级选项卡: 数据监控器")
    public static final String DATA_MONITORS_ACTIVETOOLTIP = "user.dataMonitors.activetooltip";

    @RBEntry("会议")
    @RBComment("Used for the text on the Meetings subtab under the home main tab.")
    public static final String PRIVATE_CONSTANT_114 = "meeting.list_mine.description";

    @RBEntry("次级选项卡: 会议")
    @RBComment("Used for the text on the tooltip for the Meetings sub tab under the home main tab.")
    public static final String PRIVATE_CONSTANT_115 = "meeting.list_mine.tooltip";

    @RBEntry("活动次级选项卡: 会议")
    public static final String PRIVATE_CONSTANT_116 = "meeting.list_mine.activetooltip";

    @RBEntry("次级选项卡: 更新")
    @RBComment("Used for the text on the tooltip for the Updates sub tab under the home main tab.")
    public static final String PRIVATE_CONSTANT_117 = "report.listUpdates.tooltip";

    @RBEntry("活动次级选项卡: 更新")
    public static final String PRIVATE_CONSTANT_118 = "report.listUpdates.activetooltip";

    @RBEntry("预订")
    @RBComment("Title for the subscriptions table")
    public static final String PRIVATE_CONSTANT_119 = "subscription.listSubscriptions.description";

    @RBEntry("次级选项卡: 预订")
    @RBComment("Tooltip value for the subscriptions second-level navigation link")
    public static final String PRIVATE_CONSTANT_120 = "subscription.listSubscriptions.tooltip";

    @RBEntry("活动次级选项卡: 预订")
    public static final String PRIVATE_CONSTANT_121 = "subscription.listSubscriptions.activetooltip";

    /**
     * PRODUCT tab specific resources are still in action.properties
     **/
    @RBEntry("详细信息")
    @RBComment("Used for the text on the Details subtab that appears under the product tab")
    public static final String PRIVATE_CONSTANT_122 = "product.info.description";

    @RBEntry("次级选项卡: 详细信息")
    @RBComment("Used for the text on the tooltip for the Details subtab that appears under the product tab")
    public static final String PRIVATE_CONSTANT_123 = "product.info.tooltip";

    @RBEntry("活动次级选项卡: 详细信息")
    public static final String PRIVATE_CONSTANT_124 = "product.info.activetooltip";

    @RBEntry("报告")
    @RBComment("Used for the text on the Reports subtab under the product main tab.")
    public static final String PRIVATE_CONSTANT_125 = "product.reports.description";

    @RBEntry("次级选项卡:报告")
    @RBComment("Used for the text on the tooltip for the Reports sub tab under the product main tab.")
    public static final String PRIVATE_CONSTANT_126 = "product.reports.tooltip";

    @RBEntry("活动次级选项卡: 报告")
    public static final String PRIVATE_CONSTANT_127 = "product.reports.activetooltip";

    @RBEntry("文件夹")
    @RBComment("Used for the text on the Folders subtab under the product main tab.")
    public static final String PRIVATE_CONSTANT_128 = "product.listFiles.description";

    @RBEntry("次级选项卡: 文件夹")
    @RBComment("Used for the text on the tooltip for the Folders sub tab under the product main tab.")
    public static final String PRIVATE_CONSTANT_129 = "product.listFiles.tooltip";

    @RBEntry("活动次级选项卡: 文件夹")
    public static final String PRIVATE_CONSTANT_130 = "product.listFiles.activetooltip";

    @RBEntry("团队")
    @RBComment("Used for the text on the Team subtab under the product main tab.")
    public static final String PRIVATE_CONSTANT_131 = "product.listTeam.description";

    @RBEntry("次级选项卡: 团队")
    @RBComment("Used for the text on the tooltip for the Team sub tab under the product main tab.")
    public static final String PRIVATE_CONSTANT_132 = "product.listTeam.tooltip";

    @RBEntry("活动次级选项卡: 团队")
    public static final String PRIVATE_CONSTANT_133 = "product.listTeam.activetooltip";

    @RBEntry("模板")
    @RBComment("Used for the text on the Templates subtab under the product main tab.")
    public static final String PRIVATE_CONSTANT_134 = "product.listTemplates.description";

    @RBEntry("次级选项卡: 模板")
    @RBComment("Used for the text on the tooltip for the Templates sub tab under the product main tab.")
    public static final String PRIVATE_CONSTANT_135 = "product.listTemplates.tooltip";

    @RBEntry("活动次级选项卡: 模板")
    public static final String PRIVATE_CONSTANT_136 = "product.listTemplates.activetooltip";

    @RBEntry("实用程序")
    @RBComment("Used for the text on the Utilities subtab under the product main tab.")
    public static final String PRIVATE_CONSTANT_137 = "product.listUtilities.description";

    @RBEntry("次级选项卡: 实用程序")
    @RBComment("Used for the text on the tooltip for the Utilities sub tab under the product main tab.")
    public static final String PRIVATE_CONSTANT_138 = "product.listUtilities.tooltip";

    @RBEntry("活动次级选项卡: 实用程序")
    public static final String PRIVATE_CONSTANT_139 = "product.listUtilities.activetooltip";

    @RBEntry("完整的产品列表")
    @RBComment("Used for the text on the Product List entry in the recent products list.")
    public static final String PRIVATE_CONSTANT_140 = "product.list.description";

    @RBEntry("次级选项卡: 完整的产品列表")
    @RBComment("Used for the text on the tooltip for the Product List entry in the recent products list.")
    public static final String PRIVATE_CONSTANT_141 = "product.list.tooltip";

    @RBEntry("活动次级选项卡: 完整的产品列表")
    public static final String PRIVATE_CONSTANT_142 = "product.list.activetooltip";

    @RBEntry("结构")
    @RBComment("Used for the text on the Product Structure entry in the product sub tab.")
    public static final String PRIVATE_CONSTANT_143 = "product.structureSubTab.description";

    @RBEntry("次级选项卡: 结构")
    @RBComment("Used for the text on the tooltip for the Product Structure Sub Tab.")
    public static final String PRIVATE_CONSTANT_144 = "product.structureSubTab.tooltip";

    @RBEntry("活动次级选项卡: 结构")
    public static final String PRIVATE_CONSTANT_145 = "product.structureSubTab.activetooltip";

    @RBEntry("任务")
    @RBComment("Used for the text on the Assignments subtab that appears under the product tab")
    public static final String PRIVATE_CONSTANT_146 = "work.listProductAssignments.description";

    @RBEntry("次级选项卡: 工作分配")
    @RBComment("Used for the text on the tooltip for the Assignments subtab that appears under the product tab")
    public static final String PRIVATE_CONSTANT_147 = "work.listProductAssignments.tooltip";

    @RBEntry("活动次级选项卡: 工作分配")
    public static final String PRIVATE_CONSTANT_148 = "work.listProductAssignments.activetooltip";

    @RBEntry("详细信息")
    @RBComment("Text for Details tab of Product Info Page")
    public static final String PRODUCT_INFOPAGE_DETAIL_TAB = "object.productInfoDetailsTab.description";

    /**
     * PROJECT tab specific resources are still in action.properties
     **/
    @RBEntry("报告")
    @RBComment("Used for the text on the reports subtab that appears under the project tab")
    public static final String PRIVATE_CONSTANT_149 = "project.reports.description";

    @RBEntry("次级选项卡: 报告")
    @RBComment("Used for the text on the tooltip for the reports subtab that appears under the project tab")
    public static final String PRIVATE_CONSTANT_150 = "project.reports.tooltip";

    @RBEntry("活动次级选项卡: 报告")
    public static final String PRIVATE_CONSTANT_151 = "project.reports.activetooltip";

    @RBEntry("实用程序")
    @RBComment("Used for the text on the Utilities subtab that appears under the project tab")
    public static final String PRIVATE_CONSTANT_152 = "project.listUtilities.description";

    @RBEntry("次级选项卡: 实用程序")
    @RBComment("Used for the text on the tooltip for the Utilities subtab that appears under the project tab")
    public static final String PRIVATE_CONSTANT_153 = "project.listUtilities.tooltip";

    @RBEntry("活动次级选项卡: 实用程序")
    public static final String PRIVATE_CONSTANT_154 = "project.listUtilities.activetooltip";

    @RBEntry("团队")
    @RBComment("Used for the text on the Team subtab that appears under the project tab")
    public static final String PRIVATE_CONSTANT_155 = "project.listTeam.description";

    @RBEntry("次级选项卡: 团队")
    @RBComment("Used for the text on the tooltip for the Team subtab that appears under the project tab")
    public static final String PRIVATE_CONSTANT_156 = "project.listTeam.tooltip";

    @RBEntry("活动次级选项卡: 团队")
    public static final String PRIVATE_CONSTANT_157 = "project.listTeam.activetooltip";

    @RBEntry("资源")
    @RBComment("Used for the text on the Resources subtab that appears under the project tab")
    public static final String PRIVATE_CONSTANT_158 = "project.listProjectResource.description";

    @RBEntry("次级选项卡: 资源")
    @RBComment("Used for the text on the tooltip for the Resources subtab that appears under the project tab")
    public static final String PRIVATE_CONSTANT_159 = "project.listProjectResource.tooltip";

    @RBEntry("活动次级选项卡: 资源")
    public static final String PRIVATE_CONSTANT_160 = "project.listProjectResource.activetooltip";

    @RBEntry("文件夹")
    @RBComment("Used for the text on the Folders subtab that appears under the project tab")
    public static final String PRIVATE_CONSTANT_161 = "project.listFiles.description";

    @RBEntry("次级选项卡: 文件夹")
    @RBComment("Used for the text on the tooltip for the Folders subtab that appears under the project tab")
    public static final String PRIVATE_CONSTANT_162 = "project.listFiles.tooltip";

    @RBEntry("活动次级选项卡: 文件夹")
    public static final String PRIVATE_CONSTANT_163 = "project.listFiles.activetooltip";

    @RBEntry("详细信息")
    @RBComment("Used for the text on the Details subtab that appears under the project tab")
    public static final String PRIVATE_CONSTANT_164 = "project.details.description";

    @RBEntry("次级选项卡: 详细信息")
    @RBComment("Used for the text on the tooltip for the Details subtab that appears under the project tab")
    public static final String PRIVATE_CONSTANT_165 = "project.details.tooltip";

    @RBEntry("活动次级选项卡: 详细信息")
    public static final String PRIVATE_CONSTANT_166 = "project.details.activetooltip";

    @RBEntry("完整的项目列表")
    @RBComment("Used for the text on the Project list link that appears under in the recent project list")
    public static final String PRIVATE_CONSTANT_167 = "project.list.description";

    @RBEntry("次级选项卡: 完整的项目列表")
    @RBComment("Used for the text on the tooltip for the Project list link that appears under in the recent project list")
    public static final String PRIVATE_CONSTANT_168 = "project.list.tooltip";

    @RBEntry("活动次级选项卡: 完整的项目列表")
    public static final String PRIVATE_CONSTANT_169 = "project.list.activetooltip";

    @RBEntry("完整的项目群列表")
    @RBComment("Used for the text on the Program list link that appears under in the recent program list")
    public static final String PRIVATE_CONSTANT_170 = "program.list.description";

    @RBEntry("次级选项卡: 完整的项目群列表")
    @RBComment("Used for the text on the tooltip for the Program list link that appears under in the recent program list")
    public static final String PRIVATE_CONSTANT_171 = "program.list.tooltip";

    @RBEntry("活动次级选项卡: 完整的项目群列表")
    public static final String PRIVATE_CONSTANT_172 = "program.list.activetooltip";

    @RBEntry("讨论")
    @RBComment("Used for the text on the Discussions subtab that appears under the project tab")
    public static final String PRIVATE_CONSTANT_173 = "project.view_forum.description";

    @RBEntry("讨论")
    @RBComment("Used for the text on the tooltip for the Forum subtab that appears under the project tab")
    public static final String PRIVATE_CONSTANT_174 = "project.view_forum.tooltip";

    @RBEntry("活动次级选项卡: 讨论")
    public static final String PRIVATE_CONSTANT_175 = "project.view_forum.activetooltip";

    @RBEntry("计划")
    @RBComment("Used for the text on the Plan subtab that appears under the project tab")
    public static final String PRIVATE_CONSTANT_176 = "project.view_plan.description";

    @RBEntry("次级选项卡: 计划")
    @RBComment("Used for the text on the tooltip for the Plan subtab that appears under the project tab")
    public static final String PRIVATE_CONSTANT_177 = "project.view_plan.tooltip";

    @RBEntry("活动次级选项卡: 计划")
    public static final String PRIVATE_CONSTANT_178 = "project.view_plan.activetooltip";

    @RBEntry("计划")
    @RBComment("Text for the Plans subtab that appears under the project tab.")
    public static final String VIEW_PLANS_DESCRIPTION = "project.view_plans.description";

    @RBEntry("次级选项卡: 计划")
    @RBComment("Tooltip for the Plans subtab that appears under the project tab.")
    public static final String VIEW_PLANS_TOOLTIP = "project.view_plans.tooltip";

    @RBEntry("活动的次级选项卡: 计划")
    public static final String VIEW_PLANS_ACTIVETOOLTIP = "project.view_plans.activetooltip";

    @RBEntry("更改")
    @RBComment("Used for the text on the Changes subtab under the project main tab")
    public static final String PRIVATE_CONSTANT_179 = "project.listChangeObjects.description";

    @RBEntry("次级选项卡: 更改")
    @RBComment("Used for the text on the tooltip for the Changes subtab under the project main tab")
    public static final String PRIVATE_CONSTANT_180 = "project.listChangeObjects.tooltip";

    @RBEntry("活动次级选项卡: 更改")
    public static final String PRIVATE_CONSTANT_181 = "project.listChangeObjects.activetooltip";

    @RBEntry("模板")
    @RBComment("Used for the text on the Templates subtab that appears under the project tab")
    public static final String PRIVATE_CONSTANT_182 = "project.listTemplates.description";

    @RBEntry("次级选项卡: 模板")
    @RBComment("Used for the text on the tooltip for the Templates subtab that appears under the project tab")
    public static final String PRIVATE_CONSTANT_183 = "project.listTemplates.tooltip";

    @RBEntry("活动次级选项卡: 模板")
    public static final String PRIVATE_CONSTANT_184 = "project.listTemplates.activetooltip";

    @RBEntry("模板")
    @RBComment("Used for the text on the Templates subtab under the program main tab.")
    public static final String PRIVATE_CONSTANT_185 = "program.listTemplates.description";

    @RBEntry("次级选项卡: 模板")
    @RBComment("Used for the text on the tooltip for the Templates sub tab under the program main tab.")
    public static final String PRIVATE_CONSTANT_186 = "program.listTemplates.tooltip";

    @RBEntry("活动次级选项卡: 模板")
    public static final String PRIVATE_CONSTANT_187 = "program.listTemplates.activetooltip";

    @RBEntry("任务")
    @RBComment("Used for the text on the Assignments subtab that appears under the project tab")
    public static final String PRIVATE_CONSTANT_188 = "work.listProjectAssignments.description";

    @RBEntry("次级选项卡: 工作分配")
    @RBComment("Used for the text on the tooltip for the Assignments subtab that appears under the project tab")
    public static final String PRIVATE_CONSTANT_189 = "work.listProjectAssignments.tooltip";

    @RBEntry("活动次级选项卡: 工作分配")
    public static final String PRIVATE_CONSTANT_190 = "work.listProjectAssignments.activetooltip";

    @RBEntry("会议")
    @RBComment("Used for the text on the Meetings subtab under the project main tab.")
    public static final String PRIVATE_CONSTANT_191 = "meeting.list.description";

    @RBEntry("次级选项卡: 会议")
    @RBComment("Used for the text on the tooltip for the Meetings sub tab under the project main tab.")
    public static final String PRIVATE_CONSTANT_192 = "meeting.list.tooltip";

    @RBEntry("活动次级选项卡: 会议")
    public static final String PRIVATE_CONSTANT_193 = "meeting.list.activetooltip";

    /**
     * CHANGE tab specific resources are still in action.properties
     **/
    @RBEntry("问题")
    @RBComment("Used for the text on the problem report subtab under the change main tab")
    public static final String PRIVATE_CONSTANT_194 = "change.list.description";

    @RBEntry("次级选项卡: 问题")
    @RBComment("Used for the text on the tooltip for the problem report subtab under the change main tab")
    public static final String PRIVATE_CONSTANT_195 = "change.list.tooltip";

    @RBEntry("活动次级选项卡: 问题")
    public static final String PRIVATE_CONSTANT_196 = "change.list.activetooltip";

    @RBEntry("更改请求")
    @RBComment("Used for the text on the Change Requests subtab under the change main tab")
    public static final String PRIVATE_CONSTANT_197 = "change.listChangeRequests.description";

    @RBEntry("次级选项卡: 更改请求")
    @RBComment("Used for the text on the tooltip for the Change Requests subtab under the change main tab")
    public static final String PRIVATE_CONSTANT_198 = "change.listChangeRequests.tooltip";

    @RBEntry("活动次级选项卡: 更改请求")
    public static final String PRIVATE_CONSTANT_199 = "change.listChangeRequests.activetooltip";

    @RBEntry("更改通告")
    @RBComment("Used for the text on the Change Notices subtab under the change main tab")
    public static final String PRIVATE_CONSTANT_200 = "change.listChangeNotices.description";

    @RBEntry("次级选项卡: 更改通告")
    @RBComment("Used for the text on the tooltip for the Change Notices subtab under the change main tab")
    public static final String PRIVATE_CONSTANT_201 = "change.listChangeNotices.tooltip";

    @RBEntry("活动次级选项卡: 更改通告")
    public static final String PRIVATE_CONSTANT_202 = "change.listChangeNotices.activetooltip";

    @RBEntry("超差")
    @RBComment("Used for the text on the Variances subtab under the change main tab")
    public static final String PRIVATE_CONSTANT_203 = "change.listVariances.description";

    @RBEntry("次级选项卡: 多个超差")
    @RBComment("Used for the text on the tooltip for the Variances subtab under the change main tab")
    public static final String PRIVATE_CONSTANT_204 = "change.listVariances.tooltip";

    @RBEntry("活动次级选项卡: 多个超差")
    public static final String PRIVATE_CONSTANT_205 = "change.listVariances.activetooltip";

    @RBEntry("报告")
    @RBComment("Used for the text on the Reports subtab under the change main tab")
    public static final String PRIVATE_CONSTANT_206 = "change.reports.description";

    @RBEntry("次级选项卡: 报告")
    @RBComment("Used for the text on the tooltip for the Reports subtab under the change main tab")
    public static final String PRIVATE_CONSTANT_207 = "change.reports.tooltip";

    @RBEntry("活动次级选项卡: 报告")
    public static final String PRIVATE_CONSTANT_208 = "change.reports.activetooltip";

    /**
     * LIBRARY tab specific resources
     **/
    @RBEntry("详细信息")
    @RBComment("Used for the text on the Details subtab that appears under the library tab")
    public static final String PRIVATE_CONSTANT_209 = "library.info.description";

    @RBEntry("次级选项卡: 详细信息")
    @RBComment("Used for the text on the tooltip for the Details subtab that appears under the library tab")
    public static final String PRIVATE_CONSTANT_210 = "library.info.tooltip";

    @RBEntry("活动次级选项卡: 详细信息")
    public static final String PRIVATE_CONSTANT_211 = "library.info.activetooltip";

    @RBEntry("报告")
    @RBComment("Used for the text on the Reports subtab under the library main tab.")
    public static final String PRIVATE_CONSTANT_212 = "library.reports.description";

    @RBEntry("次级选项卡: 报告")
    @RBComment("Used for the text on the tooltip for the Reports sub tab under the library main tab.")
    public static final String PRIVATE_CONSTANT_213 = "library.reports.tooltip";

    @RBEntry("活动次级选项卡: 报告")
    public static final String PRIVATE_CONSTANT_214 = "library.reports.activetooltip";

    @RBEntry("文件夹")
    @RBComment("Used for the text on the Folders subtab under the library main tab.")
    public static final String PRIVATE_CONSTANT_215 = "library.listFiles.description";

    @RBEntry("次级选项卡: 文件夹")
    @RBComment("Used for the text on the tooltip for the Folders sub tab under the library main tab.")
    public static final String PRIVATE_CONSTANT_216 = "library.listFiles.tooltip";

    @RBEntry("活动次级选项卡: 文件夹")
    public static final String PRIVATE_CONSTANT_217 = "library.listFiles.activetooltip";

    @RBEntry("团队")
    @RBComment("Used for the text on the Team subtab under the library main tab.")
    public static final String PRIVATE_CONSTANT_218 = "library.listTeam.description";

    @RBEntry("次级选项卡: 团队")
    @RBComment("Used for the text on the tooltip for the Team sub tab under the library main tab.")
    public static final String PRIVATE_CONSTANT_219 = "library.listTeam.tooltip";

    @RBEntry("活动次级选项卡: 团队")
    public static final String PRIVATE_CONSTANT_220 = "library.listTeam.activetooltip";

    @RBEntry("模板")
    @RBComment("Used for the text on the Templates subtab under the library main tab.")
    public static final String PRIVATE_CONSTANT_221 = "library.listTemplates.description";

    @RBEntry("次级选项卡: 模板")
    @RBComment("Used for the text on the tooltip for the Templates sub tab under the library main tab.")
    public static final String PRIVATE_CONSTANT_222 = "library.listTemplates.tooltip";

    @RBEntry("活动次级选项卡: 模板")
    public static final String PRIVATE_CONSTANT_223 = "library.listTemplates.activetooltip";

    @RBEntry("实用程序")
    @RBComment("Used for the text on the Utilities subtab under the library main tab.")
    public static final String PRIVATE_CONSTANT_224 = "library.listUtilities.description";

    @RBEntry("次级选项卡: 实用程序")
    @RBComment("Used for the text on the tooltip for the Utilities sub tab under the library main tab.")
    public static final String PRIVATE_CONSTANT_225 = "library.listUtilities.tooltip";

    @RBEntry("活动次级选项卡: 实用程序")
    public static final String PRIVATE_CONSTANT_226 = "library.listUtilities.activetooltip";

    @RBEntry("完整的存储库列表")
    @RBComment("Used for the text on the Library List entry in the recent library list.")
    public static final String PRIVATE_CONSTANT_227 = "library.list.description";

    @RBEntry("次级选项卡: 完整的存储库列表")
    @RBComment("Used for the text on the tooltip for the Library List entry in the recent library list.")
    public static final String PRIVATE_CONSTANT_228 = "library.list.tooltip";

    @RBEntry("活动次级选项卡: 完整的存储库列表")
    public static final String PRIVATE_CONSTANT_229 = "library.list.activetooltip";

    @RBEntry("任务")
    @RBComment("Used for the text on the Assignments subtab that appears under the library tab")
    public static final String PRIVATE_CONSTANT_230 = "work.listLibraryAssignments.description";

    @RBEntry("次级选项卡: 工作分配")
    @RBComment("Used for the text on the tooltip for the Assignments subtab that appears under the library tab")
    public static final String PRIVATE_CONSTANT_231 = "work.listLibraryAssignments.tooltip";

    @RBEntry("活动次级选项卡: 工作分配")
    public static final String PRIVATE_CONSTANT_232 = "work.listLibraryAssignments.activetooltip";

    @RBEntry("详细信息")
    @RBComment("Text for Details tab of Library Info Page")
    public static final String LIBRARY_INFOPAGE_DETAIL_TAB = "object.libraryInfoDetailsTab.description";

    /**
     * AGREEMENTS tab specific resources
     **/
    @RBEntry("协议")
    @RBComment("Used for the text on the Agreements subtab that appears under the Site, Org, Projuct, Product, and Library tabs")
    public static final String PRIVATE_CONSTANT_233 = "agreements.listAgreements.description";

    @RBEntry("次级选项卡: 协议")
    @RBComment("Used for the text on the tooltip for the agreements subtab")
    public static final String PRIVATE_CONSTANT_234 = "agreements.listAgreements.tooltip";

    @RBEntry("活动次级选项卡: 协议")
    public static final String PRIVATE_CONSTANT_235 = "agreements.listAgreements.activetooltip";

    /**
     * ORGANIZATION tab specific resources, most are still in action.properties
     **/
    @RBEntry("完整的组织列表")
    @RBComment("The text displayed on the Organizations list link at the end of the recent org list")
    public static final String PRIVATE_CONSTANT_236 = "org.listOrgs.description";

    @RBEntry("次级选项卡: 完整的组织列表")
    @RBComment("The tooltip for the Organizations list link at the end of the recent org list")
    public static final String PRIVATE_CONSTANT_237 = "org.listOrgs.tooltip";

    @RBEntry("活动次级选项卡:  完整的组织列表")
    public static final String PRIVATE_CONSTANT_238 = "org.listOrgs.activetooltip";

    @RBEntry("详细信息")
    @RBComment("Used for the text on the details sub tab under the organization main tab.")
    public static final String PRIVATE_CONSTANT_239 = "org.details.description";

    @RBEntry("次级选项卡: 详细信息")
    @RBComment("Used for the text on the tooltip for the details sub tab under the organization main tab.")
    public static final String PRIVATE_CONSTANT_240 = "org.details.tooltip";

    @RBEntry("活动次级选项卡: 详细信息")
    public static final String PRIVATE_CONSTANT_241 = "org.details.activetooltip";

    @RBEntry("文件夹")
    @RBComment("Used for the text on the folders sub tab under the organization main tab.")
    public static final String PRIVATE_CONSTANT_242 = "org.listFiles.description";

    @RBEntry("次级选项卡: 组织文件夹")
    @RBComment("Used for the text on the tooltip for the folders sub tab under the organization main tab.")
    public static final String PRIVATE_CONSTANT_243 = "org.listFiles.tooltip";

    @RBEntry("活动次级选项卡: 组织文件夹")
    public static final String PRIVATE_CONSTANT_244 = "org.listFiles.activetooltip";

    @RBEntry("组")
    @RBComment("Used for the text on the Groups sub tab under the organization main tab.")
    public static final String PRIVATE_CONSTANT_245 = "org.listOrgGroups.description";

    @RBEntry("次级选项卡: 组织小组")
    @RBComment("Used for the text on the tooltip for the Groups sub tab under the organization main tab.")
    public static final String PRIVATE_CONSTANT_246 = "org.listOrgGroups.tooltip";

    @RBEntry("活动次级选项卡: 组织小组")
    public static final String PRIVATE_CONSTANT_247 = "org.listOrgGroups.activetooltip";

    @RBEntry("团队")
    @RBComment("Used for the text on the Teams sub tab under the organization main tab.")
    public static final String PRIVATE_CONSTANT_248 = "org.listOrgTeams.description";

    @RBEntry("次级选项卡: 组织团队")
    @RBComment("Used for the text on the tooltip for the Teams sub tab under the organization main tab.")
    public static final String PRIVATE_CONSTANT_249 = "org.listOrgTeams.tooltip";

    @RBEntry("活动次级选项卡: 组织团队")
    public static final String PRIVATE_CONSTANT_250 = "org.listOrgTeams.activetooltip";

    @RBEntry("角色")
    @RBComment("Used for the text on the Roles sub tab under the organization main tab.")
    public static final String PRIVATE_CONSTANT_251 = "org.listOrgRoles.description";

    @RBEntry("次级选项卡: 组织角色")
    @RBComment("Used for the text on the tooltip for the Roles sub tab under the organization main tab.")
    public static final String PRIVATE_CONSTANT_252 = "org.listOrgRoles.tooltip";

    @RBEntry("活动次级选项卡: 组织角色")
    public static final String PRIVATE_CONSTANT_253 = "org.listOrgRoles.activetooltip";

    @RBEntry("类型")
    @RBComment("Used for the text on the Types sub tab under the organization main tab.")
    public static final String PRIVATE_CONSTANT_254 = "org.listTypes.description";

    @RBEntry("次级选项卡: 组织类型")
    @RBComment("Used for the text on the tooltip for the Types sub tab under the organization main tab.")
    public static final String PRIVATE_CONSTANT_255 = "org.listTypes.tooltip";

    @RBEntry("活动次级选项卡: 组织类型")
    public static final String PRIVATE_CONSTANT_256 = "org.listTypes.activetooltip";

    @RBEntry("配置文件")
    @RBComment("Used for the text on the Profiles sub tab under the organization main tab.")
    public static final String PRIVATE_CONSTANT_257 = "org.listProfiles.description";

    @RBEntry("次级选项卡: 组织配置文件")
    @RBComment("Used for the text on the tooltip for the Profiles sub tab under the organization main tab.")
    public static final String PRIVATE_CONSTANT_258 = "org.listProfiles.tooltip";

    @RBEntry("活动次级选项卡: 组织配置文件")
    public static final String PRIVATE_CONSTANT_259 = "org.listProfiles.activetooltip";

    @RBEntry("报告")
    @RBComment("Used for the text on the reports sub tab under the organization main tab.")
    public static final String PRIVATE_CONSTANT_260 = "org.listOrgProjects.description";

    @RBEntry("次级选项卡: 组织报告")
    @RBComment("Used for the text on the tooltip for the reports sub tab under the organization main tab.")
    public static final String PRIVATE_CONSTANT_261 = "org.listOrgProjects.tooltip";

    @RBEntry("活动次级选项卡: 组织报告")
    public static final String PRIVATE_CONSTANT_262 = "org.listOrgProjects.activetooltip";

    @RBEntry("管理员")
    @RBComment("Used for the text on the Administrators sub tab under the organization main tab.")
    public static final String PRIVATE_CONSTANT_263 = "org.listOrgAdmin.description";

    @RBEntry("次级选项卡: 组织管理员")
    @RBComment("Used for the text on the tooltip for the Administrators sub tab under the organization main tab.")
    public static final String PRIVATE_CONSTANT_264 = "org.listOrgAdmin.tooltip";

    @RBEntry("活动次级选项卡: 组织管理员")
    public static final String PRIVATE_CONSTANT_265 = "org.listOrgAdmin.activetooltip";

    @RBEntry("模板")
    @RBComment("Used for the text on the Templates sub tab under the organization main tab.")
    public static final String PRIVATE_CONSTANT_266 = "org.listTemplates.description";

    @RBEntry("次级选项卡: 模板")
    @RBComment("Used for the text on the tooltip for the Templates sub tab under the organization main tab.")
    public static final String PRIVATE_CONSTANT_267 = "org.listTemplates.tooltip";

    @RBEntry("活动次级选项卡: 模板")
    public static final String PRIVATE_CONSTANT_268 = "org.listTemplates.activetooltip";

    @RBEntry("创建者")
    @RBComment("Used for the text on the Creators sub tab under the organization main tab.")
    public static final String PRIVATE_CONSTANT_269 = "org.listCreators.description";

    @RBEntry("次级选项卡: 创建者")
    @RBComment("Used for the text on the tooltip for the Creators sub tab under the organization main tab.")
    public static final String PRIVATE_CONSTANT_270 = "org.listCreators.tooltip";

    @RBEntry("lmt=100")
    @RBPseudo(false)
    public static final String PRIVATE_CONSTANT_271 = "org.listCreators.moreurlinfo";

    @RBEntry("活动次级选项卡: 创建者")
    public static final String PRIVATE_CONSTANT_272 = "org.listCreators.activetooltip";

    @RBEntry("实用程序")
    @RBComment("Used for the text on the Utilities sub tab under the organization main tab.")
    public static final String PRIVATE_CONSTANT_273 = "org.listUtilities.description";

    @RBEntry("次级选项卡: 实用程序")
    @RBComment("Used for the text on the tooltip for the Utilities sub tab under the organization main tab.")
    public static final String PRIVATE_CONSTANT_274 = "org.listUtilities.tooltip";

    @RBEntry("活动次级选项卡: 实用程序")
    public static final String PRIVATE_CONSTANT_275 = "org.listUtilities.activetooltip";

    /**
     * SITE tab specific resources
     **/
    @RBEntry("管理员")
    @RBComment("The text displayed on the Administrators sub tab under the site main tab")
    public static final String PRIVATE_CONSTANT_276 = "site.listAdmin.description";

    @RBEntry("次级选项卡: 站点管理员")
    @RBComment("The tooltip for the Administrators sub tab under the site main tab")
    public static final String PRIVATE_CONSTANT_277 = "site.listAdmin.tooltip";

    @RBEntry("活动次级选项卡: 站点管理员")
    public static final String PRIVATE_CONSTANT_278 = "site.listAdmin.activetooltip";

    @RBEntry("文件夹")
    @RBComment("The text displayed on the folders sub tab under the site main tab")
    public static final String PRIVATE_CONSTANT_279 = "site.listFiles.description";

    @RBEntry("次级选项卡: 站点文件夹")
    @RBComment("The tooltip for the folders sub tab under the site main tab")
    public static final String PRIVATE_CONSTANT_280 = "site.listFiles.tooltip";

    @RBEntry("活动次级选项卡: 站点文件夹")
    public static final String PRIVATE_CONSTANT_281 = "site.listFiles.activetooltip";

    @RBEntry("模板")
    @RBComment("The text displayed on the Templates sub tab under the site main tab")
    public static final String PRIVATE_CONSTANT_282 = "site.listTemplates.description";

    @RBEntry("次级选项卡: 模板")
    @RBComment("The tooltip for the Templates sub tab under the site main tab")
    public static final String PRIVATE_CONSTANT_283 = "site.listTemplates.tooltip";

    @RBEntry("活动次级选项卡: 模板")
    public static final String PRIVATE_CONSTANT_284 = "site.listTemplates.activetooltip";

    @RBEntry("类型")
    @RBComment("The text displayed on the types sub tab under the site main tab")
    public static final String PRIVATE_CONSTANT_285 = "site.listTypes.description";

    @RBEntry("次级选项卡: 站点类型")
    @RBComment("The tooltip for the types sub tab under the site main tab")
    public static final String PRIVATE_CONSTANT_286 = "site.listTypes.tooltip";

    @RBEntry("活动次级选项卡: 站点类型")
    public static final String PRIVATE_CONSTANT_287 = "site.listTypes.activetooltip";

    @RBEntry("实用程序")
    @RBComment("The text displayed on the Utilities sub tab under the site main tab")
    public static final String PRIVATE_CONSTANT_288 = "site.listUtilities.description";

    @RBEntry("次级选项卡: 实用程序")
    @RBComment("The tooltip for the Utilities sub tab under the site main tab")
    public static final String PRIVATE_CONSTANT_289 = "site.listUtilities.tooltip";

    @RBEntry("活动次级选项卡: 实用程序")
    public static final String PRIVATE_CONSTANT_290 = "site.listUtilities.activetooltip";

    @RBEntry("报告")
    @RBComment("The text displayed on the Reports sub tab under the site main tab")
    public static final String PRIVATE_CONSTANT_291 = "site.reports.description";

    @RBEntry("次级选项卡: 报告")
    @RBComment("The tooltip for the Reports sub tab under the site main tab")
    public static final String PRIVATE_CONSTANT_292 = "site.reports.tooltip";

    @RBEntry("活动次级选项卡: 报告")
    public static final String PRIVATE_CONSTANT_293 = "site.reports.activetooltip";

    @RBEntry("站点")
    public static final String PRIVATE_CONSTANT_294 = "site.view.description";

    @RBEntry("站点")
    public static final String PRIVATE_CONSTANT_295 = "site.view.tooltip";

    @RBEntry("配置文件")
    @RBComment("The text displayed on the Profiles sub tab under the site main tab")
    public static final String PRIVATE_CONSTANT_296 = "site.listProfiles.description";

    @RBEntry("次级选项卡: 站点配置文件")
    @RBComment("The tooltip for the Profiles sub tab under the site main tab")
    public static final String PRIVATE_CONSTANT_297 = "site.listProfiles.tooltip";

    @RBEntry("活动次级选项卡: 站点配置文件")
    public static final String PRIVATE_CONSTANT_298 = "site.listProfiles.activetooltip";

    /**
     * RECENT LISTS
     **/
    @RBEntry("最近访问的")
    @RBComment("Used for the text on the recently accessed menu button which appears in the global header")
    public static final String RECENT_LIST = "RECENT_LIST";

    @RBEntry("无法连接至服务器。")
    @RBComment("Error message displayed if there is a problem retrieving the recently accessed list from the server.")
    public static final String UNABLE_TO_CONNECT = "UNABLE_TO_CONNECT";

    @RBEntry("产品")
    @RBComment("Used for the text on the Products list which appears with the sub tabs under the Product main tab.")
    public static final String PRIVATE_CONSTANT_299 = "object.recentProductsList.description";

    @RBEntry("产品")
    @RBComment("Used for the text on the tooltip for the Products list which appears with the sub tabs under the Product main tab.")
    public static final String PRIVATE_CONSTANT_300 = "object.recentProductsList.tooltip";

    @RBEntry("项目")
    @RBComment("Used for the text on the Projects list which appears with the sub tabs under the Project main tab.")
    public static final String PRIVATE_CONSTANT_301 = "object.recentProjectsList.description";

    @RBEntry("项目")
    @RBComment("Used for the text on the tooltip for the Projects list which appears with the sub tabs under the Project main tab.")
    public static final String PRIVATE_CONSTANT_302 = "object.recentProjectsList.tooltip";

    @RBEntry("项目群")
    @RBComment("Used for the text on the Programs list which appears with the sub tabs under the Program main tab.")
    public static final String PRIVATE_CONSTANT_303 = "object.recentProgramsList.description";

    @RBEntry("项目群")
    @RBComment("Used for the text on the tooltip for the Programs list which appears with the sub tabs under the Program main tab.")
    public static final String PRIVATE_CONSTANT_304 = "object.recentProgramsList.tooltip";

    @RBEntry("更改")
    @RBComment("Used for the text on the Changes list which appears with the sub tabs under the Change main tab.")
    public static final String PRIVATE_CONSTANT_305 = "object.recentChangesList.description";

    @RBEntry("更改")
    @RBComment("Used for the text on the tooltip for the Changes list which appears with the sub tabs under the Change main tab.")
    public static final String PRIVATE_CONSTANT_306 = "object.recentChangesList.tooltip";

    @RBEntry("存储库")
    @RBComment("Used for the text on the Libraries list which appears with the sub tabs under the library main tab.")
    public static final String PRIVATE_CONSTANT_307 = "object.recentLibrariesList.description";

    @RBEntry("存储库")
    @RBComment("Used for the text on the tooltip for the Libraries list which appears with the sub tabs under the library main tab.")
    public static final String PRIVATE_CONSTANT_308 = "object.recentLibrariesList.tooltip";

    @RBEntry("组织")
    @RBComment("Used for the text on the organizations list which appears with the sub tabs under the organization main tab.")
    public static final String PRIVATE_CONSTANT_309 = "org.recentOrganizationsList.description";

    @RBEntry("组织")
    @RBComment("Used for the text on the tooltip for the organizations list which appears with the sub tabs under the organization main tab.")
    public static final String PRIVATE_CONSTANT_310 = "org.recentOrganizationsList.tooltip";

    @RBEntry("没有可显示的对象")
    public static final String NO_OBJECTS_TO_DISPLAY = "NO_OBJECTS_TO_DISPLAY";

    @RBEntry("没有可显示的操作")
    public static final String NO_ACTIONS_TO_DISPLAY = "NO_ACTIONS_TO_DISPLAY";

    @RBEntry("关于可访问性")
    public static final String PRIVATE_CONSTANT_311 = "skipNav.aboutAccessibility.altText";

    /**
     * third level nav
     **/
    @RBEntry("常规(<U class='mnemonic'>G</U>)")
    @RBComment("Used for the text on the General third level nav menu.  The <U class=")
    public static final String PRIVATE_CONSTANT_312 = "object.general.description";

    @RBEntry("g")
    @RBPseudo(false)
    @RBComment("Mnemonic for the General third level nav menu. This should be a character that matches the character surrounded by the <U class=")
    public static final String PRIVATE_CONSTANT_313 = "object.general.hotkey";

    @RBEntry("常规(<U class='mnemonic'>G</U>)")
    @RBComment("Used for the text on the General third level nav menu.  The <U class=")
    public static final String PRIVATE_CONSTANT_314 = "object.generalCADDoc.description";

    @RBEntry("g")
    @RBPseudo(false)
    @RBComment("Mnemonic for the General third level nav menu. This should be a character that matches the character surrounded by the <U class=")
    public static final String PRIVATE_CONSTANT_315 = "object.generalCADDoc.hotkey";

    @RBEntry("常规(<U class='mnemonic'>G</U>)")
    @RBComment("Used for the text on the General third level nav menu.  The <U class=")
    public static final String PRIVATE_CONSTANT_316 = "object.generalCADDoc_LocalCache.description";

    @RBEntry("g")
    @RBPseudo(false)
    @RBComment("Mnemonic for the General third level nav menu. This should be a character that matches the character surrounded by the <U class=")
    public static final String PRIVATE_CONSTANT_317 = "object.generalCADDoc_LocalCache.hotkey";

    @RBEntry("主要内容")
    @RBComment("Used under the General third level nav menu for allowing a user to see the primary content associate with this object.")
    public static final String PRIVATE_CONSTANT_318 = "object.primaryContent.description";

    @RBEntry("附件")
    @RBComment("Used under the General third level nav menu for allowing a user to see the attachments associated with this object.")
    public static final String PRIVATE_CONSTANT_319 = "object.attachments.description";

    @RBEntry("可视化和属性")
    @RBComment("Used under the General third level nav menu for allowing a user to see the visualization and attributes associated with this object.")
    public static final String PRIVATE_CONSTANT_320 = "object.visualizationAndAttributes.description";

    @RBEntry("属性")
    @RBComment("Used under the General third level nav menu for allowing a user to see the primary attributes associated with this object.")
    public static final String PRIVATE_CONSTANT_321 = "object.primaryAttributes.description";

    @RBEntry("结构(<U class='mnemonic'>S</U>)")
    @RBComment("Used for the text on the Structure third level nav menu.  The <U class=")
    public static final String PRIVATE_CONSTANT_322 = "object.productStructure.description";

    @RBEntry("s")
    @RBPseudo(false)
    @RBComment("Mnemonic for the Structure third level nav menu. This should be a character that matches the character surrounded by the <U class=")
    public static final String PRIVATE_CONSTANT_323 = "object.productStructure.hotkey";

    @RBEntry("结构(<U class='mnemonic'>S</U>)")
    @RBComment("Used for the text on the Structure third level nav menu.  The <U class=")
    public static final String PRIVATE_CONSTANT_324 = "object.documentStructure.description";

    @RBEntry("s")
    @RBPseudo(false)
    @RBComment("Mnemonic for the Structure third level nav menu. This should be a character that matches the character surrounded by the <U class=")
    public static final String PRIVATE_CONSTANT_325 = "object.documentStructure.hotkey";

    @RBEntry("更改(<U class='mnemonic'>C</U>)")
    @RBComment("Used for the text on the Changes third level nav menu.  The <U class=")
    public static final String CHANGE_INFO_NAME = "object.changes.description";

    @RBEntry("c")
    @RBComment("Mnemonic for the Changes third level nav menu. This should be a char that matches the char surrounded by the <U class=")
    public static final String CHANGE_INFO_MNEMONIC = "object.changes.hotkey";

    @RBEntry("协作(<U class='mnemonic'>n</U>)")
    @RBComment("Used for the text on the Collaboration third level nav menu.  The <U class=")
    public static final String PRIVATE_CONSTANT_326 = "object.collaboration.description";

    @RBEntry("n")
    @RBPseudo(false)
    @RBComment("Mnemonic for the Collaboration third level nav menu. This should be a character that matches the character surrounded by the <U class=")
    public static final String PRIVATE_CONSTANT_327 = "object.collaboration.hotkey";

    @RBEntry("相关对象(<U class='mnemonic'>R</U>)")
    @RBComment("Used for the text on the Related Objects third level nav menu.  The <U class=")
    public static final String PRIVATE_CONSTANT_328 = "object.relatedItems.description";

    @RBEntry("r")
    @RBPseudo(false)
    @RBComment("Mnemonic for the Related Objects third level nav menu. This should be a character that matches the character surrounded by the <U class=")
    public static final String PRIVATE_CONSTANT_329 = "object.relatedItems.hotkey";

    @RBEntry("相关对象(<U class='mnemonic'>R</U>)")
    @RBComment("Used for the text on the Related Objects third level nav menu.  The <U class=")
    public static final String PRIVATE_CONSTANT_330 = "object.relatedItemsCADDoc.description";

    @RBEntry("r")
    @RBPseudo(false)
    @RBComment("Mnemonic for the Related Objects third level nav menu. This should be a character that matches the character surrounded by the <U class=")
    public static final String PRIVATE_CONSTANT_331 = "object.relatedItemsCADDoc.hotkey";

    @RBEntry("相关对象(<U class='mnemonic'>R</U>)")
    @RBComment("Used for the text on the Related Objects third level nav menu.  The <U class=")
    public static final String PRIVATE_CONSTANT_332 = "object.relatedItemsCADDoc_LocalCache.description";

    @RBEntry("r")
    @RBPseudo(false)
    @RBComment("Mnemonic for the Related Objects third level nav menu. This should be a character that matches the character surrounded by the <U class=")
    public static final String PRIVATE_CONSTANT_333 = "object.relatedItemsCADDoc_LocalCache.hotkey";

    @RBEntry("质量(<U class='mnemonic'>U</U>)")
    @RBComment("Used for the text on the Quality third level nav menu. The <U class=")
    public static final String PRIVATE_CONSTANT_395 = "object.quality.description";

    @RBEntry("u")
    @RBPseudo(false)
    @RBComment("Mnemonic for the Quality third level nav menu. This should be a character that matches the character surrounded by the <U class=")
    public static final String PRIVATE_CONSTANT_396 = "object.quality.hotkey";

    @RBEntry("历史记录(<U class='mnemonic'>I</U>)")
    @RBComment("Used for the text on the History third level nav menu.  The <U class=")
    public static final String PRIVATE_CONSTANT_334 = "object.history.description";

    @RBEntry("i")
    @RBPseudo(false)
    @RBComment("Mnemonic for the History third level nav menu. This should be a character that matches the character surrounded by the <U class=")
    public static final String PRIVATE_CONSTANT_335 = "object.history.hotkey";

    @RBEntry("产品分析(<U class='mnemonic'>Y</U>)")
    @RBComment("Used for the text on the Product Analytics third level nav menu.  The <U class=")
    public static final String PROD_ANALYTICS_CONSTANT = "object.prodAnalytics.description";

    @RBEntry("y")
    @RBPseudo(false)
    @RBComment("Mnemonic for the Product Analytics third level nav menu. This should be a character that matches the character surrounded by the <U class=")
    public static final String PROD_ANALYTICS_HOTKEY_CONSTANT = "object.prodAnalytics.hotkey";

    /**
     * Default wiz buttons
     **/
    @RBEntry("完成(<u class='mnemonic'>F</u>)")
    @RBComment("Used for the text on the FINISH wizard button.  The <U class=")
    public static final String finishButton = "object.finishButton.description";

    @RBEntry("f")
    @RBPseudo(false)
    @RBComment("Mnemonic for the FINISH wizard button. This should be a character that matches the character surrounded by the <U class=")
    public static final String finishButtonHotKey = "object.finishButton.hotkey";

    @RBEntry("确定(<u class='mnemonic'>O</u>)")
    @RBComment("Used for the text on the OK wizard button.  The <U class=")
    public static final String okButton = "object.okButton.description";

    @RBEntry("o")
    @RBPseudo(false)
    @RBComment("Mnemonic for the OK wizard button. This should be a character that matches the character surrounded by the <U class=")
    public static final String okButtonHotKey = "object.okButton.hotkey";

    @RBEntry("确定")
    public static final String okDialogButton = "object.okDialogButton.description";

    @RBEntry("下一页(<u class='mnemonic'>N</u>)")
    @RBComment("Used for the text on the Next wizard button.  The <U class=")
    public static final String nextButton = "object.nextButton.description";

    @RBEntry("n")
    @RBPseudo(false)
    @RBComment("Mnemonic for the Next wizard button. This should be a character that matches the character surrounded by the <U class=")
    public static final String nextButtonHotKey = "object.nextButton.hotkey";

    @RBEntry("上一页(<u class='mnemonic'>B</u>)")
    @RBComment("Used for the text on the Back wizard button.  The <U class=")
    public static final String backButton = "object.prevButton.description";

    @RBEntry("b")
    @RBPseudo(false)
    @RBComment("Mnemonic for the Back wizard button. This should be a character that matches the character surrounded by the <U class=")
    public static final String backButtonHotKey = "object.prevButton.hotkey";

    @RBEntry("取消(<u class='mnemonic'>C</u>)")
    @RBComment("Used for the text on the Cancel wizard button.  The <U class=")
    public static final String editCancelButton = "object.editCancelButton.description";

    @RBEntry("c")
    @RBPseudo(false)
    @RBComment("Mnemonic for the Cancel wizard button. This should be a character that matches the character surrounded by the <U class=")
    public static final String editCancelButtonHotKey = "object.editCancelButton.hotkey";


    @RBEntry("取消(<u class='mnemonic'>C</u>)")
    @RBComment("Used for the text on the Cancel wizard button.  The <U class=")
    public static final String cancelButton = "object.cancelButton.description";

    @RBEntry("c")
    @RBPseudo(false)
    @RBComment("Mnemonic for the Cancel wizard button. This should be a character that matches the character surrounded by the <U class=")
    public static final String cancelButtonHotKey = "object.cancelButton.hotkey";

    @RBEntry("取消")
    public static final String cancelDialogButton = "object.cancelDialogButton.description";


    @RBEntry("应用(<u class='mnemonic'>A</u>)")
    @RBComment("Used for the text on the Apply wizard button.  The <U class=")
    public static final String PRIVATE_CONSTANT_336 = "object.applyButton.description";

    @RBEntry("a")
    @RBPseudo(false)
    @RBComment("Mnemonic for the Apply wizard button. This should be a character that matches the character surrounded by the <U class=")
    public static final String PRIVATE_CONSTANT_337 = "object.applyButton.hotkey";

    @RBEntry("检入(<u class='mnemonic'>I</u>)")
    @RBComment("Used for the text on the Check In wizard button.  The <U class=")
    public static final String PRIVATE_CONSTANT_338 = "object.checkinButton.description";

    @RBEntry("i")
    @RBPseudo(false)
    @RBComment("Mnemonic for the Check In wizard button. This should be a character that matches the character surrounded by the <U class=")
    public static final String PRIVATE_CONSTANT_339 = "object.checkinButton.hotkey";

    @RBEntry("保存(<U class='mnemonic'>S</U>)")
    @RBComment("Used for the text on the Save wizard button.  The <U class=")
    public static final String PRIVATE_CONSTANT_341 = "object.saveButton.description";

    @RBEntry("s")
    @RBPseudo(false)
    @RBComment("Mnemonic for the Save wizard button. This should be a character that matches the character surrounded by the <U class=")
    public static final String PRIVATE_CONSTANT_342 = "object.saveButton.hotkey";

    /**
     * Entries for the FOOTER area
     **/
    @RBEntry("PTC 徽标")
    @RBComment("Used for the text on the tooltip for the PTC logo image.")
    public static final String PTC_LOGO_ALT = "PTC_LOGO_ALT";

    /**
     * Entries for Second Level Nav Details action
     **/
    @RBEntry("详细信息")
    @RBComment("The text displayed for the Details sub tab under the main tabs:  Product, Library, Program, and Project")
    public static final String PRIVATE_CONSTANT_344 = "object.view.description";

    @RBEntry("详细信息")
    @RBComment("title used for the Details action sub tab")
    public static final String PRIVATE_CONSTANT_345 = "object.view.title";

    @RBEntry("详细信息")
    @RBComment("tooltip used for the i icon action that leads to the information page")
    public static final String PRIVATE_CONSTANT_346 = "object.view.tooltip";

    @RBEntry("折叠属性面板")
    @RBComment("Tool tip for collapse attributes panel link")
    public static final String COLLAPSE_ATTRIBUTES_PANEL = "COLLAPSE_ATTRIBUTES_PANEL";

    @RBEntry("展开属性面板")
    @RBComment("Tool tip for expand attributes panel link")
    public static final String EXPAND_ATTRIBUTES_PANEL = "EXPAND_ATTRIBUTES_PANEL";

    @RBEntry("折叠页眉和页脚")
    @RBComment("Tool tip for collapse header link")
    public static final String COLLAPSE_HEADER_FOOTER = "COLLAPSE_HEADER_FOOTER";

    @RBEntry("展开标题和脚注")
    @RBComment("Tool tip for expand header link")
    public static final String EXPAND_HEADER_FOOTER = "EXPAND_HEADER_FOOTER";

    @RBEntry("搜索")
    @RBComment("This is browser window title for Search Page")
    public static final String WIN_TITLE_HOME_TAB_SEARCH = "WIN_TITLE_HOME_TAB_SEARCH";

    @RBEntry("高级搜索")
    @RBComment("This is browser window title for Advacend Search Page")
    public static final String WIN_TITLE_HOME_TAB_SEARCH_ADVANCED = "WIN_TITLE_HOME_TAB_SEARCH_ADVANCED";

    /**
     * ----------------------- Entries for page headers. These headers are not displayed on the page, but they are
     * available to users with screen readers.
     **/
    @RBEntry("主要选项卡。已选择 {0}。")
    @RBComment("The header text for the first level of navigation available on every page. Contains links like \"Home\", \"Product\", \"Project\", \"Library\" etc. {0} is populated with the label from one of those links. This text is not displayed on the page, but is available to users with screen readers. ")
    public static final String FIRST_NAV_HEADER = "FIRST_NAV_HEADER";

    @RBEntry("主要选项卡。未作任何选择。 ")
    @RBComment("The header text for the first level of navigation when nothing is selected. ")
    public static final String FIRST_NAV_HEADER_NO_SELECTION = "FIRST_NAV_HEADER_NO_SELECTION";

    @RBEntry("次级选项卡。已选择 {0}。")
    @RBComment("The header text for the second level of navigation available on every page. Contains links like \"Meetings\", \"Team\", \"Discussion\", \"Templates\" etc. {0} is populated with the label from one of those links. This text is not displayed on the page, but is available to users with screen readers.")
    public static final String SECOND_NAV_HEADER = "SECOND_NAV_HEADER";

    @RBEntry("次级选项卡。未作任何选择。")
    @RBComment("The header text for the second level of navigation when nothing is selected.")
    public static final String SECOND_NAV_HEADER_NO_SELECTION = "SECOND_NAV_HEADER_NO_SELECTION";

    @RBEntry("当前上下文 ")
    @RBComment("The trailing space is intentional \"Current context \". This is the header text for the context bar shown below 1st and 2nd level tabs once a container context is selected. The context bar contains information like \"Product: GOLF_CART -> Folder: Design\" or \"Welcome, userName\". The CONTEXT_BAR_HEADER text is hidden but is put into the page just before the context info and will be read by the screen reader in that order. For example \"Current context Product: GOLF_CART -> Folder: Design\" or \"Current context Welcome, userName\"")
    public static final String CONTEXT_BAR_HEADER = "CONTEXT_BAR_HEADER";

    @RBEntry("信息表菜单栏。已选择 {0}。")
    @RBComment("The header text for the third level of navigation that is only available on the info page. Contains menus like \"Product Structure\", \"General\", \"Related Objects\", \"History\", \"Collaboration\" etc. The {0} is populated with one of these menu names. This text is not displayed on the page, but is available to users with screen readers.")
    public static final String THIRD_NAV_HEADER = "THIRD_NAV_HEADER";

    @RBEntry("内容区域。{0}")
    @RBComment("The header text for the content area of wizards. {0} is populated with the currently shown step like \"Define Item\", \"Set Attributes\", \"Set Attachments\" etc. ")
    public static final String WIZARD_CONTENT_HEADER = "WIZARD_CONTENT_HEADER";

    @RBEntry("详细信息")
    @RBComment("The Details tab on the info page")
    public static final String PRIVATE_CONSTANT_349 = "object.infoDetails.description";

    @RBEntry("详细信息")
    @RBComment("The Details tab on the info page for WTPart")
    public static final String PRIVATE_CONSTANT_350 = "object.partInfoDetailsTab.description";

    @RBEntry("信息")
    @RBComment("The Info page tab for WTDocument info page.")
    public static final String PRIVATE_CONSTANT_351 = "object.docInfoPageTabSet.description";

    @RBEntry("详细信息")
    @RBComment("The Details tab on the info page for WTDocument")
    public static final String PRIVATE_CONSTANT_352 = "object.docInfoDefaultDetails.description";

    @RBEntry("详细信息")
    @RBComment("The Related tab on the info page for WTPartMaster")
    public static final String PRIVATE_CONSTANT_356 = "object.partMasterInfoDetailsTab.description";

    @RBEntry("使用情况")
    @RBComment("The Where Used tab on the info page for WTPartMaster")
    public static final String PRIVATE_CONSTANT_357 = "object.partMasterInfoWhereUsedTab.description";

    @RBEntry("详细信息")
    @RBComment("The Related tab on the info page for ManagedBaseline")
    public static final String PRIVATE_CONSTANT_358 = "object.baselineInfoDetailsTab.description";

    @RBEntry("相关对象")
    @RBComment("The Related tab on the info page for ManagedBaseline")
    public static final String PRIVATE_CONSTANT_359 = "object.baselineInfoRelatedItemsTab.description";

    @RBEntry("使用情况")
    @RBComment("The Where Used tab on the info page for WTPart")
    public static final String PRIVATE_CONSTANT_360 = "object.partInfoWhereUsedTab.description";

    @RBEntry("更改")
    @RBComment("The Change tab on the info page for WTPart")
    public static final String PRIVATE_CONSTANT_361 = "object.partInfoChangeTab.description";

    @RBEntry("相关对象")
    @RBComment("The Related tab on the info page for WTPart")
    public static final String PRIVATE_CONSTANT_362 = "object.partInfoRelatedItemsTab.description";

    @RBEntry("历史记录")
    @RBComment("The History tab on the info page for WTPart")
    public static final String PRIVATE_CONSTANT_370 = "object.partInfoHistoryTab.description";

    @RBEntry("协作")
    @RBComment("The Collaboration tab on the info page for WTPart")
    public static final String PRIVATE_CONSTANT_371 = "object.partInfoCollaborationTab.description";

    @RBEntry("内容")
    @RBComment("The Content tab on the info page for WTPart")
    public static final String CONTENT_TAB_FOR_WTPART = "object.partInfoContentTab.description";

    @RBEntry("AML/AVL")
    @RBComment("The AML/AVL tab on the info page for WTPart")
    public static final String AML_AVL_TAB_FOR_WTPART = "object.amlAvlTab.description";

    @RBEntry("产品分析")
    @RBComment("The Analytics tab on the info page for WTPart")
    public static final String PROD_ANALYTICS_TAB_FOR_WTPART = "object.prodAnalyticsTab.description";

    @RBEntry("详细信息")
    @RBComment("The Details tab on the info page for WTProductConfiguration and WTProductInstance2")
    public static final String PRIVATE_CONSTANT_365 = "object.configInfoDefaultDetails.description";

    @RBEntry("新建选项卡 {0}")
    @RBComment("This will be used when a tab is added on the info page via the '+'. (e.g. New Tab 1, New Tab 2, New Tab 3)")
    public static final String INFO_PAGE_NEW_TAB = "INFO_PAGE_NEW_TAB";

    @RBEntry("+")
    @RBComment("The text for the action to add a new tab on the info page.")
    public static final String INFO_PAGE_ADD_NEW_TAB = "INFO_PAGE_ADD_NEW_TAB";

    @RBEntry("自定义")
    @RBComment("The menu button located on the \"My Tab\" info page.")
    public static final String MY_TAB_MENU = "MY_TAB_MENU";

    @RBEntry("自定义此选项卡")
    @RBComment("The menu button located on the \"My Tab\" info page.")
    public static final String MY_TAB_MENU_TOOLTIP = "MY_TAB_MENU_TOOLTIP";

    @RBEntry("重命名选项卡")
    @RBComment("The title of the prompt dialogue to rename a tab on the info page")
    public static final String RENAME_TAB_PROMPT_TITLE = "RENAME_TAB_PROMPT_TITLE";

    @RBEntry("公共选项卡 - 您站点或组织中的用户可以看见内容。")
    @RBComment("The text of the tooltip over the image showing that the tab is public")
    public static final String TAB_STATUS_PUBLIC = "TAB_STATUS_PUBLIC";

    @RBEntry("名称")
    @RBComment("The text of the prompt on the Rename Tab prompt.")
    public static final String RENAME_TAB_PROMPT_TEXT = "RENAME_TAB_PROMPT_TEXT";

    @RBEntry("输入选项卡的名称")
    @RBComment("The error message to display when a user has entered an empty name for the new tab")
    public static final String RENAME_TAB_NAME_EMPTY = "RENAME_TAB_NAME_EMPTY";

    @RBEntry("具有此名称的选项卡已存在")
    @RBComment("The error message to display when a user has entered a name that already is the name of another tab")
    public static final String RENAME_TAB_DUPLICATE_NAME = "RENAME_TAB_DUPLICATE_NAME";

    @RBEntry("字段最大长度为 60 个字符")
    @RBComment("The error message to display when a user has entered a tab name that exceeds 60 characters")
    public static final String RENAME_TAB_CHARS_EXCEEDED = "RENAME_TAB_CHARS_EXCEEDED";

    @RBEntry("60")
    @RBComment("Max length for view name entered in save as view function.")
    public static final String RENAME_TAB_MAX_LENGTH_FIELD = "RENAME_TAB_MAX_LENGTH_FIELD";

    @RBEntry("名称")
    @RBComment("The text of the prompt on the Save As View prompt.")
    public static final String SAVE_AS_VIEW_PROMPT_TEXT = "SAVE_AS_VIEW_PROMPT_TEXT";

    @RBEntry("另存为视图")
    public static final String SAVE_AS_VIEW_PROMPT_TITLE = "SAVE_AS_VIEW_PROMPT_TITLE";

    @RBEntry("最大字段长度为 32 个字符")
    @RBComment("The error message to display when a user has entered a view name that exceeds 32 characters")
    public static final String SAVE_AS_VIEW_CHARS_EXCEEDED = "SAVE_AS_VIEW_CHARS_EXCEEDED";

    @RBEntry("输入视图名称")
    public static final String SAVE_AS_VIEW_NAME_EMPTY = "SAVE_AS_VIEW_NAME_EMPTY";

    @RBEntry("具有此名称的视图已存在")
    @RBComment("The error message to display when a user has entere a view name that already exists.")
    public static final String SAVE_AS_VIEW_DUPLICATE_NAME = "SAVE_AS_VIEW_DUPLICATE_NAME";

    @RBEntry("32")
    @RBComment("Max length for view name entered in save as view function.")
    public static final String SAVE_AS_VIEW_MAX_LENGTH_FIELD = "SAVE_AS_VIEW_MAX_LENGTH_FIELD";

    @RBEntry("正在加载...")
    @RBComment("The text to display by the blank screen while the wizard steps are loading in the background")
    public static final String LOADING = "LOADING";

    @RBEntry("项目状况")
    @RBComment("Table name, for story B-25650. Table for all PDM checked out, Sent to PDM, Superseded, Abandoned, and shares.")
    public static final String PRIVATE_CONSTANT_353 = "sandbox.projectRevision.description";

    @RBEntry("项目状况")
    @RBComment("Table name, for story B-25650. Table for all PDM checked out, Sent to PDM, Superseded, Abandoned, and shares.")
    public static final String PRIVATE_CONSTANT_354 = "sandbox.projectRevision.title";

    @RBEntry("项目状况")
    @RBComment("Table name, for story B-25650. Table for all PDM checked out, Sent to PDM, Superseded, Abandoned, and shares.")
    public static final String PRIVATE_CONSTANT_355 = "sandbox.projectRevision.tooltip";

    @RBEntry("项目状况")
    @RBComment("The name of the ProjectRevision table.")
    public static final String PROJECT_REVISION_TABLE_NAME = "PROJECT_REVISION_TABLE_NAME";

    @RBEntry("浏览(<u class='mnemonic'>W</u>)")
    @RBComment("Title for browse tab on Navigator.")
    public static final String PRIVATE_CONSTANT_363 = "object.main navigation.description";

    @RBEntry("W")
    @RBPseudo(false)
    @RBComment("Hotkey for browse tab on Navigator.")
    public static final String BROWSE_HOTKEY = "object.main navigation.hotkey";

    @RBEntry("浏览 ")
    @RBComment("Tooltip for browse tab on Navigator.")
    public static final String BROWSE_TOOLTIP = "object.main navigation.tooltip";

    @RBEntry("搜索(<u class='mnemonic'>S</u>)")
    @RBComment("Title for search tab on Navigator.")
    public static final String PRIVATE_CONSTANT_364 = "object.search navigation.description";

    @RBEntry("搜索 ")
    @RBComment("Tooltip for search tab on Navigator.")
    public static final String SEARCH_TOOLTIP = "object.search navigation.tooltip";

    @RBEntry("S")
    @RBPseudo(false)
    @RBComment("Hotkey for search tab on Navigator.")
    public static final String SEARCH_HOTKEY = "object.search navigation.hotkey";

    @RBEntry("导航器")
    @RBComment("Title for the navigator panel")
    public static final String NAVIGATOR_TITLE = "NAVIGATOR_TITLE";

    @RBEntry("详细信息")
    @RBComment("The Details tab on the cadx info page")
    public static final String PRIVATE_CONSTANT_366 = "object.infoDetails_cadx.description";

    @RBEntry("项目监视器")
    @RBComment("Used for the text on the project monitor subtab that appears under the project tab")
    public static final String PROJECT_MONITOR_DESCRIPTION = "project.projectMonitor.description";

    @RBEntry("次级选项卡: 项目监视器")
    @RBComment("Used for the text on the tooltip for the project monitor subtab that appears under the project tab")
    public static final String PROJECT_MONITOR_TOOLTIP = "project.projectMonitor.tooltip";

    @RBEntry("活动次级选项卡: 项目监视器")
    public static final String PROJECT_MONITOR_ACTIVETOOLTIP = "project.projectMonitor.activetooltip";

    @RBEntry("lmt=25")
    @RBPseudo(false)
    @RBComment("DO NOT TRANSLATE")
    public static final String PROJECT_MONITOR_MOREURLINFO = "project.projectMonitor.moreurlinfo";

    @RBEntry("历史记录")
    @RBComment("The History tab on the info page for ManagedBaseline")
    public static final String PRIVATE_CONSTANT_367 = "object.baselineHistoryTab.description";

    @RBEntry("协作")
    @RBComment("The Collaboration tab on the info page for ManagedBaseline")
    public static final String PRIVATE_CONSTANT_368 = "object.baselineCollaborationTab.description";

    @RBEntry("发布结构")
    @RBComment("The name of the Publication Structure table.")
    public static final String PUBLICATION_STRUCTURE_TABLE_NAME = "PUBLICATION_STRUCTURE_TABLE_NAME";

    @RBEntry("发布结构")
    @RBComment("Table name for table showing all Publication Structure objects.")
    public static final String PRIVATE_CONSTANT_PUBST1 = "arbortext.listPubStructures.description";

    @RBEntry("发布结构")
    @RBComment("Table name for table showing all Publication Structure objects.")
    public static final String PRIVATE_CONSTANT_PUBST2 = "arbortext.listPubStructures.title";

    @RBEntry("发布结构")
    @RBComment("Table name for table showing all Publication Structure objects.")
    public static final String PRIVATE_CONSTANT_PUBST3 = "arbortext.listPubStructures.tooltip";

    @RBEntry("操作")
    @RBComment("The Actions context menu for the Publication Structure table.")
    public static final String PUBLICATION_STRUCTURE_ACTIONS = "PUBLICATION_STRUCTURE_ACTIONS";

    @RBEntry("历史记录")
    @RBComment("The History tab on the info page for WTPartMaster")
    public static final String PRIVATE_CONSTANT_369 = "object.partMasterInfoHistoryTab.description";

    @RBEntry("更改")
    @RBComment("The text for Change in the third level nav on the info page for WTPart")
    public static final String PRIVATE_CONSTANT_372 = "object.partInfoChange.description";

    @RBEntry("常规")
    @RBComment("The text for General in the third level nav on the info page for WTPart")
    public static final String PRIVATE_CONSTANT_374 = "object.partInfoGeneral.description";

    @RBEntry("相关对象")
    @RBComment("The text for Related in the third level nav on the info page for WTPart")
    public static final String PRIVATE_CONSTANT_375 = "object.partInfoRelatedItems.description";

    @RBEntry("常规")
    @RBComment("The text for General in the third level nav on the info page for ManagedBaseline")
    public static final String PRIVATE_CONSTANT_377 = "object.generalBaseline.description";

    @RBEntry("常规")
    @RBComment("The text for General in the third level nav on the info page for WTPartMaster")
    public static final String PRIVATE_CONSTANT_378 = "object.generalPartMaster.description";

    @RBEntry("历史记录")
    @RBComment("The text for History in the third level nav on the info page for WTPartMaster")
    public static final String PRIVATE_CONSTANT_379 = "object.historyPartMaster.description";

    @RBEntry("自定义")
    @RBComment("The text that is on the drop down list for customizing the home page.")
    public static final String HOME_PAGE_CUSTOMIZE = "HOME_PAGE_CUSTOMIZE";

    /*
     * Entries for My Settings in the Quick Links action model
     */
    @RBEntry("我的设置")
    @RBComment("Title for the submodel mysettings")
    public static final String MY_SETTINGS = "object.mysettings.description";

    @RBEntry("首选项")
    @RBComment("Settings and preferences specific to the user")
    public static final String USER_PREFERENCES = "user.preferences.description";

    @RBEntry("配置文件")
    @RBComment("View your personal information that the company has on record.")
    public static final String USER_PROFILE = "user.profile.description";

    @RBEntry("日历")
    @RBComment("Provide the ability to schedule non-working days and delegate work.")
    public static final String CALENDAR_MANAGEMENT = "user.calendarmanagement.description";

    /*
     * Entries for the breadcrumbs.
     */
    @RBEntry("产品")
    @RBComment("Container context list crumb for products")
    public static final String PRODUCTS = "PRODUCTS";

    @RBEntry("项目")
    @RBComment("Container context list crumb for projects")
    public static final String PROJECTS = "PROJECTS";

    @RBEntry("项目群")
    @RBComment("Container context list crumb for programs")
    public static final String PROGRAMS = "PROGRAMS";

    @RBEntry("存储库")
    @RBComment("Container context list crumb for libraries")
    public static final String LIBRARIES = "LIBRARIES";

    @RBEntry("质量")
    @RBComment("Container context list crumb for quality")
    public static final String QMS = "QMS";

    @RBEntry("组织")
    @RBComment("Container context list crumb for organizations")
    public static final String ORGANIZATIONS = "ORGANIZATIONS";

    @RBEntry("文件夹")
    @RBComment("Crumb for the root folder page of a container")
    public static final String FOLDERS = "FOLDERS";

    @RBEntry("工作区")
    @RBComment("Crumb that leads back to the workspace list for a given container")
    public static final String WORKSPACES = "WORKSPACES";

    @RBEntry("任务")
    @RBComment("Crumb that leads to the assignments table to which the current object belongs")
    public static final String ASSIGNMENTS = "ASSIGNMENTS";

    @RBEntry("协议")
    @RBComment("Crumb that leads to the agreements cabinet")
    public static final String AGREEMENTS = "AGREEMENTS";

    /*
     * Cadx entries.
     */

    @RBEntry("相关对象")
    @RBComment("The Related tab on the cadx info page")
    public static final String PRIVATE_CONSTANT_382 = "object.related_cadx.description";

    @RBEntry("内容")
    @RBComment("The Content tab on the cadx info page")
    public static final String PRIVATE_CONSTANT_383 = "object.content_cadx.description";

    @RBEntry("历史记录")
    @RBComment("The History tab on the cadx info page")
    public static final String PRIVATE_CONSTANT_384 = "object.history_cadx.description";

    /**
     * Entries for Open Link
     */
    @RBEntry("打开")
    @RBComment("Open Links")
    public static final String OPEN = "object.open.description";

    /**
     * Entries for WTDocumentMaster info page
     */
    @RBEntry("详细信息")
    @RBComment("The Related tab on the info page for WTDocumentMaster")
    public static final String PRIVATE_CONSTANT_388 = "object.docMasterInfoDetailsTab.description";

    @RBEntry("历史记录")
    @RBComment("The History tab on the info page for WTDocumentMaster")
    public static final String PRIVATE_CONSTANT_389 = "object.docMasterInfoHistoryTab.description";

    @RBEntry("信息")
    @RBComment("The Info page tab for WTDocumentMaster info page.")
    public static final String PRIVATE_CONSTANT_390 = "object.docMasterInfoPageTabSet.description";

    /**
     * Entries for Navigator BAR
     */
    @RBEntry("展开")
    @RBComment("Tool tip for expand navigator bar")
    public static final String EXPAND_NAVIGATOR_BAR = "EXPAND_NAVIGATOR_BAR";

    @RBEntry("折叠")
    @RBComment("Tool tip for collapse navigator bar")
    public static final String COLLAPSE_NAVIGATOR_BAR = "COLLAPSE_NAVIGATOR_BAR";

    @RBEntry("全部查看")
    @RBComment("Navigator view all link.")
    public static final String VIEW_ALL = "VIEW_ALL";

    @RBEntry("向后滚动选项卡列表")
    @RBComment("tooltip on the details page tabs area if the tabs do not fit and scroll arrows are present to scroll to the other tabs.")
    public static final String SCROLL_TABS_LEFT = "SCROLL_TABS_LEFT";

    @RBEntry("向前滚动选项卡列表")
    @RBComment("tooltip on the details page tabs area if the tabs do not fit and scroll arrows are present to scroll to the other tabs.")
    public static final String SCROLL_TABS_RIGHT = "SCROLL_TABS_RIGHT";

    //QMS Related entries
    @RBEntry("CAPA")
    @RBComment("Crumb that leads to the CAPA table to which the current object belongs")
    public static final String CAPA = "CAPA";

    @RBEntry("不合规")
    @RBComment("Crumb that leads to the Non Compliance table to which the current object belongs")
    public static final String NON_COMPLIANCE = "NON_COMPLIANCE";

    @RBEntry("投诉")
    @RBComment("Crumb that leads to the Complaints table to which the current object belongs")
    public static final String COMPLAINTS = "COMPLAINTS";

    @RBEntry("人员和网上邻居")
    @RBComment("Crumb that leads to the People and Places table to which the current object belongs")
    public static final String PEOPLE_PLACES = "PEOPLE_PLACES";

    @RBEntry("部件")
    @RBComment("Crumb that leads to the Quality Parts table to which the current object belongs")
    public static final String QUALITY_PARTS = "QUALITY_PARTS";

    @RBEntry("文档")
    @RBComment("Crumb that leads to the Quality Document table to which the current object belongs")
    public static final String QUALITY_DOCUMENTS = "QUALITY_DOCUMENTS";

    //@RBEntry("My Stuff")
    //@RBComment("Used for the text on the My stuff tab on navigator.")
    //public static final String MY_STUFF_TAB_DESC = "navigation.myStuffTab.description";

    @RBEntry("相关信息")
    @RBComment("The text displayed for the menu item in the mini navigator to switch to the related info pane.")
    public static final String MININAVWIDGETS = "navigation.miniNavWidgets.description";

    @RBEntry("浏览缩略图")
    @RBComment("The text displayed for the menu item in the mini navigator to switch to the default navigate by thumbnails pane.")
    public static final String MININAVTHUMBPANE = "navigation.miniNavThumbPane.description";

    @RBEntry("可视化")
    @RBComment("The title of the thumbnail image panel in the mini navigator window.")
    public static final String VISUALIZATION_TITLE = "VISUALIZATION_TITLE";

    @RBEntry("固定导航器")
    @RBComment("Tooltip for pinning tool on navigator bar.")
    public static final String PIN_NAVIGATOR = "PIN_NAVIGATOR";

    @RBEntry("取消固定导航器")
    @RBComment("Tooltip for unpinning tool on navigator bar.")
    public static final String UNPIN_NAVIGATOR = "UNPIN_NAVIGATOR";

    @RBEntry("您的 Windchill 会话已经重新连接。可以重试该操作或刷新页面。")
    @RBComment("Text displayed after a successful re-login in a special login Lightbox.  This only happens when the Form Based Authentication configuration is used and the user tried to do something in a session that had timed out.")
    public static final String FBA_LOGIN_SUCCESS = "FBA_LOGIN_SUCCESS";

    @RBEntry("主要选项卡: 分类管理")
    @RBComment("Used for the tooltip on the Classification Administration tab")
    public static final String PRIVATE_CONSTANT_391 = "navigation.clfAdmin.tooltip";

    @RBEntry("活动主要选项卡: 分类管理")
    public static final String PRIVATE_CONSTANT_392 = "navigation.clfAdmin.activetooltip";

    @RBEntry("navigator_clfAdmin.png")
    @RBComment("DO NOT TRANSLATE")
    public static final String PRIVATE_CONSTANT_393 = "navigation.clfAdmin.icon";

    @RBEntry("分类管理")
    @RBComment("Used for the text on the Classification Administration tab.")
    public static final String PRIVATE_CONSTANT_394 = "navigation.clfAdmin.description";

    @RBEntry("分类管理员组")
    @RBComment("Description for the Classification Administrator Groups action")
    public static final String CSM_ADMIN_GROUP_DESC = "csm.clfAdminGroup.description";

    @RBEntry("分类管理员组")
    @RBComment("Title for the Classification Administrator Groups action")
    public static final String CSM_ADMIN_GROUP_TITLE = "csm.clfAdminGroup.title";

    @RBEntry("分类管理员组")
    @RBComment("Tooltip for the Classification Administrator Groups action")
    public static final String CSM_ADMIN_GROUP_TOOLTIP = "csm.clfAdminGroup.tooltip";

    @RBEntry("管理分类")
    @RBComment("Description for the Manage Classification action")
    public static final String MANAGE_CLASSIFICATION_DESC = "csm.manageClassification.description";

    @RBEntry("管理分类")
    @RBComment("Title for the Manage Classification action")
    public static final String MANAGE_CLASSIFICATION_TITLE = "csm.manageClassification.title";

    @RBEntry("管理分类")
    @RBComment("Tooltip for the Manage Classification action")
    public static final String MANAGE_CLASSIFICATION_TOOLTIP = "csm.manageClassification.tooltip";


    @RBEntry("公用")
    @RBComment("The public tab name to be displayed for Site admins.")
    public static final String PUBLIC_TAB = "PUBLIC_TAB";

    @RBEntry("专用")
    @RBComment("The private tab name to be displayed for Site admins.")
    public static final String PRIVATE_TAB = "PRIVATE_TAB";

    // Quality Tab in the Part Info Page.

    @RBEntry("质量")
    @RBComment("The Quality tab on the info page for WTPart")
    public static final String QUALITY_TAB = "object.qualityTab.description";

    @RBEntry("操作")
    @RBComment("The Label for Action Group in Homepage Customize Menu")
    public static final String ACTION_GROUPS = "user.actionGroup.description";

    @RBEntry("ThingWorx")
    @RBComment("The label for a thingworx submodel that will be empty out of the box, but is a placeholder where custom thingworx actions may be added for demos or by customizers.")
    public static final String THINGWORX_ACTIONS_MENU = "object.thingworxActionsMenu.description";

    @RBEntry("ThingWorx")
    @RBComment("The label for a thingworx submodel that will be empty out of the box, but is a placeholder where custom thingworx actions may be added for demos or by customizers.")
    public static final String THINGWORX_CUSTOMIZE_MENU = "object.thingworxCustomizeMenu.description";

    @RBEntry("转至 ThingWorx")
    @RBComment("Label for a hyperlink that would appear above ThingWorx mashup embedded in Windchill page. Hyperlink launches the mashup in new window.")
    public static final String THINGWORX_GO_TO = "THINGWORX_GO_TO";

    // Learning Connector action:
    @RBEntry("PTC Learning Connector")
    @RBComment("Name for the Learning Connector action from the Quick Links menu.")
    public static final String OPEN_LC_ACTION_DESCRIPTION = "help.openLearningConnector.description";

    @RBEntry("上下文相关的培训、支持和帮助")
    @RBComment("Tooltip for the Learning Connector action from the Quick Links menu.")
    public static final String OPEN_LC_ACTION_TOOLTIP = "help.openLearningConnector.tooltip";

    @RBEntry("open_learning_connector.png")
    @RBComment("DO NOT TRANSLATE Icon for the Learning Connector action from the Quick Links menu.")
    public static final String OPEN_LC_ACTION_ICON = "help.openLearningConnector.icon";
	
	/*
     * Entries for consCheckRecords in the Quick Links action model
     */
    @RBEntry("结构检验记录")
    @RBComment("Title for the submodel consCheckRecords")
    public static final String Construct_Checking_Records = "object.consCheckRecords.description";

    @RBEntry("项目配置")
    @RBComment("consCheckRecords and proConfig specific to the ccr")
    public static final String Project_Configurations = "ccr.proConfig.description";

    @RBEntry("套表配置")
    @RBComment("consCheckRecords and tableConfig specific to the ccr")
    public static final String Table_Configurations = "ccr.tableConfig.description";

    /*
     * Entries for filePrintManagement in the Quick Links action model	_add by hz
     */
    @RBEntry("文件打印分发回收管理")
    @RBComment("Title for the submodel filePrintManagement")
    public static final String File_Print_Management = "object.filePrintManagement.description";

    @RBEntry("厂所工艺文件")
    @RBComment("Title for the submodel insideTechFile")
    public static final String Inside_TechFile = "object.insideTechFile.description";

    @RBEntry("打印申请")
    @RBComment("filePrintManagement and printApply specific to the itf")
    public static final String Print_Apply = "itf.printApply.description";

    @RBEntry("加盖印章")
    @RBComment("filePrintManagement and sealPlus specific to the itf")
    public static final String Seal_Plus = "itf.sealPlus.description";

    @RBEntry("文件回收")
    @RBComment("filePrintManagement and fileRecycling specific to the itf")
    public static final String File_Recycling = "itf.fileRecycling.description";

    @RBEntry("文件入库")
    @RBComment("filePrintManagement and fileInstore specific to the itf")
    public static final String File_Instore = "itf.fileInstore.description";

    @RBEntry("文件封存")
    @RBComment("filePrintManagement and fileSafekeeping specific to the itf")
    public static final String File_Safekeeping = "itf.fileSafekeeping.description";

    @RBEntry("文件转移")
    @RBComment("filePrintManagement and fileTransfer specific to the itf")
    public static final String File_Transfer = "itf.fileTransfer.description";

    @RBEntry("外来文件录入")
    @RBComment("filePrintManagement and outsideFileEntry specific to the itf")
    public static final String OutsideFile_Entry = "itf.outsideFileEntry.description";

    @RBEntry("外来文件")
    @RBComment("Title for the submodel outsideFile")
    public static final String Outside_File = "object.outsideFile.description";

    @RBEntry("印章管理") //add by lkc 2017.12.05
    @RBComment("Title for the submodel sealManager")
    public static final String Seal_Manager = "object.sealManager.description";

    @RBEntry("文件打印分发记录查询") //add by lkc 2017.12.11
    @RBComment("Title for the submodel filePrintRecordQuery")
    public static final String File_Print_Record_Query = "object.filePrintRecordQuery.description";

    @RBEntry("文件打印异常处理") //add by lkc 2018.1.11
    @RBComment("Title for the submodel filePrintExceptionHand")
    public static final String File_Print_Exception_Hand = "object.filePrintExceptionHand.description";

    @RBEntry("文件重新打印") //add by lkc 2018.1.11
    @RBComment("Title for the submodel fileReprint")
    public static final String File_Reprint = "object.fileReprint.description";

    @RBEntry("文件条码信息查询") //add by lkc 2018.1.11
    @RBComment("Title for the submodel fileBarCodeQuery")
    public static final String File_BarCode_Query = "object.fileBarCodeQuery.description";

    @RBEntry("文件补打申请")
    @RBComment("Title for the submodel fileOffSetApply")
    public static final String file_OffSet_Apply = "object.fileOffSetApply.description";

    @RBEntry("厂内纸质文件")
    @RBComment("Title for the submodel insidePaperFile")
    public static final String Inside_PaperFile = "object.insidePaperFile.description";

    @RBEntry("历史数据导入")
    public static final String File_Import_Inside = "itf.historyInsideTechFileImport.description";

    @RBEntry("历史数据导入")
    public static final String File_Import_Outside = "itf.historyOutsideFileImport.description";

    @RBEntry("*本机文件路径:")
    public static final String File_Import_Inside_step = "itf.historyInsideTechFileImport_step.description";

    @RBEntry("*本机文件路径:")
    public static final String File_Import_Outside_step = "itf.historyOutsideFileImport_step.description";

    @RBEntry("外来文件回收")
    public static final String Out_File_Recycling = "itf.outFileRecycling.description";

    @RBEntry("纸质文件回收")
    public static final String Paper_File_Recycling = "itf.paperFileRecycling.description";

    @RBEntry("外来文件入库")
    public static final String Out_File_Instore = "itf.outFileInstore.description";

    @RBEntry("纸质文件入库")
    public static final String Paper_File_Instore = "itf.paperFileInstore.description";

    @RBEntry("外来文件封存")
    public static final String Out_File_Safekeeping = "itf.outFileSafekeeping.description";

    @RBEntry("纸质文件封存")
    public static final String Paper_File_Safekeeping = "itf.paperFileSafekeeping.description";

    @RBEntry("外来文件分发")
    public static final String Out_File_Print_Apply = "itf.outFilePrintApply.description";

    @RBEntry("外来文件加盖印章")
    public static final String Out_File_Seal_Plus = "itf.outFileSealPlus.description";

    @RBEntry("纸质文件录入")
    public static final String Inside_Paper_File_Entry = "itf.insidePaperFileEntry.description";

    @RBEntry("纸质文件分发")
    public static final String Inside_PaperFile_printApply = "itf.insidePaperFileprintApply.description";

    @RBEntry("纸质文件加盖印章")
    public static final String Paper_File_Seal_Plus = "itf.paperFileSealPlus.description";

    @RBEntry("编码快捷填报")
    public static final String Custom_Apply_Code = "custom.applyCode.description";

    @RBEntry("工艺引用SOP文件汇总表")
    public static final String CUSTOM_YINYONGSOP = "navigation.yinyongsophuizong.description";
    @RBEntry("依据文件引用情况汇总表")
    public static final String CUSTOM_YIJVWENJIAN = "navigation.sopyijvwenjianhuizong.description";

    @RBEntry("工时定额状态源查询表")
    public static final String CUSTOM_QUERYGONGSHISTATE = "navigation.queryGongShiState.description";

}

