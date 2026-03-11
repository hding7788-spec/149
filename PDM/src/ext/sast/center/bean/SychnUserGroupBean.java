package ext.sast.center.bean;

import ext.sast.center.bean.message.GroupMessageBean;
import ext.sast.center.bean.message.UserGroupLinkMessageBean;
import ext.sast.center.bean.message.UserMessageBean;

/**
 * @ Author     ：LB.
 * @ Date       ：Created in 2019/3/13
 * @ Description：
 * @ Modified By：
 */
public class SychnUserGroupBean {

    private UserMessageBean userMessageBean;

    private GroupMessageBean groupMessageBean;

    private UserGroupLinkMessageBean userGroupLinkMessageBean;

    public UserMessageBean getUserMessageBean() {
        return userMessageBean;
    }
	
    public void setUserMessageBean(UserMessageBean userMessageBean) {
        this.userMessageBean = userMessageBean;
    }

    public GroupMessageBean getGroupMessageBean() {
        return groupMessageBean;
    }

    public void setGroupMessageBean(GroupMessageBean groupMessageBean) {
        this.groupMessageBean = groupMessageBean;
    }

    public UserGroupLinkMessageBean getUserGroupLinkMessageBean() {
        return userGroupLinkMessageBean;
    }

    public void setUserGroupLinkMessageBean(UserGroupLinkMessageBean userGroupLinkMessageBean) {
        this.userGroupLinkMessageBean = userGroupLinkMessageBean;
    }
}
