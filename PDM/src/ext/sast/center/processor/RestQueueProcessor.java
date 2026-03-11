package ext.sast.center.processor;

import org.apache.log4j.Logger;

import wt.log4j.LogR;
import wt.util.WTException;

import com.bjsasc.avidm.mq.message.Based;
import com.bjsasc.avidm.mq.message.Message;
import com.bjsasc.avidm.mq.message.win10.Win10SynDivRespDcMessage;
import com.bjsasc.avidm.mq.message.win10.Win10SynPrincipalRespDcMessage;
import com.bjsasc.avidm.mq.message.win10.Win10SynUserRespDcMessage;
import com.bjsasc.avidm.mq.sender.Sender;

import ext.sast.center.bean.SychnUserGroupBean;
import ext.sast.center.bean.message.GroupMessageBean;
import ext.sast.center.bean.message.UserGroupLinkMessageBean;
import ext.sast.center.bean.message.UserMessageBean;
import ext.sast.center.util.JsonConvertUtil;
import ext.sast.center.util.QueryUtil;

/**
 * @ Author     ：LB.
 * @ Date       ：Created in 2019/3/7
 * @ Description：
 * @ Modified By：
 */
public class RestQueueProcessor {

    private static final Logger logger = LogR.getLogger(RestQueueProcessor.class.getName());

	    /**
     * 同步用户、组信息
     */
    public static void synchUserAndGroupInfo() {
        try {
            //获取系统用户、组信息
            SychnUserGroupBean sychnUserGroupBean = QueryUtil.getAllUserAndGroup();
            //同步用户
            synchUser(sychnUserGroupBean.getUserMessageBean());
            //同步组
            synchGroup(sychnUserGroupBean.getGroupMessageBean());
            //同步用户与组关系
            synchUserGroupLink(sychnUserGroupBean.getUserGroupLinkMessageBean());
        } catch (WTException e) {
            e.printStackTrace();
        }
    }
    /**
     * 同步用户信息
     *
     * @param userBeanList
     */
    public static void synchUser(UserMessageBean userMessageBean) {
        logger.info("==========>>>>>开始同步人员<<<<<==========");
        Sender sender;
        Message msg;
        try {
            msg = new Win10SynUserRespDcMessage();
            msg.put(Based.MSG_ID, userMessageBean.getMsg_id());
            msg.put(Based.MSG_TYPE, userMessageBean.getMsg_type());
            msg.put(Based.MSG_DESCRIPTION, userMessageBean.getMsg_description());
            msg.put(Based.MSG_CREATED_TIME, userMessageBean.getSys_version_request());
            msg.put(Based.SYS_VERSION_REQUEST, userMessageBean.getSys_version_request());
            msg.put(Based.RESPONSE_SITE_IID, userMessageBean.getResponse_site_iid());
            msg.put(Based.JA_USERS_RESPONSE, JsonConvertUtil.convertUserListToJson(userMessageBean.getJa_users_response()));
            sender = Sender.getInstance();
            sender.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("==========>>>>>结束同步人员<<<<<==========");
    }

    /**
     * 同步组
     *
     * @param groupMessageBean
     */
    public static void synchGroup(GroupMessageBean groupMessageBean) {
        logger.info("==========>>>>>开始同步组<<<<<==========");
        Sender sender;
        Message msg;
        try {
            msg = new Win10SynDivRespDcMessage();
            msg.put(Based.MSG_ID, groupMessageBean.getMsg_id());
            msg.put(Based.MSG_TYPE, groupMessageBean.getMsg_type());
            msg.put(Based.MSG_DESCRIPTION, groupMessageBean.getMsg_description());
            msg.put(Based.MSG_CREATED_TIME, groupMessageBean.getSys_version_request());
            msg.put(Based.SYS_VERSION_REQUEST, groupMessageBean.getSys_version_request());
            msg.put(Based.RESPONSE_SITE_IID, groupMessageBean.getResponse_site_iid());
            msg.put(Based.JA_DIVS_RESPONSE, JsonConvertUtil.convertGroupToJson(groupMessageBean.getJa_divs_response()));

            sender = Sender.getInstance();
            sender.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("==========>>>>>结束同步组<<<<<==========");
    }

    /**
     * 同步用户组关系
     *
     * @param userGroupLinkMessageBean
     */
    public static void synchUserGroupLink(UserGroupLinkMessageBean userGroupLinkMessageBean) {
        logger.info("==========>>>>>开始同步用户组关系<<<<<==========");
        Sender sender;
        Message msg;
        try {
            msg = new Win10SynPrincipalRespDcMessage();
            msg.put(Based.MSG_ID, userGroupLinkMessageBean.getMsg_id());
            msg.put(Based.MSG_TYPE, userGroupLinkMessageBean.getMsg_type());
            msg.put(Based.MSG_DESCRIPTION, userGroupLinkMessageBean.getMsg_description());
            msg.put(Based.MSG_CREATED_TIME, userGroupLinkMessageBean.getSys_version_request());
            msg.put(Based.SYS_VERSION_REQUEST, userGroupLinkMessageBean.getSys_version_request());
            msg.put(Based.RESPONSE_SITE_IID, userGroupLinkMessageBean.getResponse_site_iid());
            msg.put(Based.JA_PRINCIPALS_RESPONSE, JsonConvertUtil.convertUserGroupLinkToJson(userGroupLinkMessageBean.getJa_principals_response()));
            sender = Sender.getInstance();
            sender.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("==========>>>>>结束同步用户组关系<<<<<==========");
    }


}
