package ext.sast.center.bean.message;

import ext.sast.center.bean.SiteBean;

/**
 * @ Author     ：LB.
 * @ Date       ：Created in 2019/3/19
 * @ Description：
 * @ Modified By：
 */
public class SiteMessageBean {
    /**uuid作为唯一标识*/
    private String msg_id;
    /**消息类型*/
    private String msg_type;
    /**消息描述，可不填*/
    private String msg_description;
    /**消息创建时间*/
    private long msg_created_time;
    /**当前发起请求系统版本*/
    private String sys_version_request;
    /**中心域信息*/
    private SiteBean j_siteinfo_response;

    /**操作类型（新增add，修改modify，删除delete）*/
    private String operate_type;

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public String getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(String msg_type) {
        this.msg_type = msg_type;
    }

    public String getMsg_description() {
        return msg_description;
    }

    public void setMsg_description(String msg_description) {
        this.msg_description = msg_description;
    }

    public long getMsg_created_time() {
        return msg_created_time;
    }

    public void setMsg_created_time(long msg_created_time) {
        this.msg_created_time = msg_created_time;
    }

    public String getSys_version_request() {
        return sys_version_request;
    }

    public void setSys_version_request(String sys_version_request) {
        this.sys_version_request = sys_version_request;
    }

    public SiteBean getJ_siteinfo_response() {
        return j_siteinfo_response;
    }

    public void setJ_siteinfo_response(SiteBean j_siteinfo_response) {
        this.j_siteinfo_response = j_siteinfo_response;
    }

    public String getOperate_type() {
        return operate_type;
    }

    public void setOperate_type(String operate_type) {
        this.operate_type = operate_type;
    }
}
