package ext.sast.center.bean.message;

import java.util.List;

import ext.sast.center.bean.GroupBean;

/**
 * @ Author     ：LB.
 * @ Date       ：Created in 2019/3/19
 * @ Description：
 * @ Modified By：
 */
public class GroupMessageBean {
    /**uuid作为唯一标识*/
    private String msg_id;

    /**avidm_dc_syndiv_resp_datacenter*/
    private String msg_type;

    /**消息描述，可不填*/
    private String msg_description;

    /**消息创建时间*/
    private long msg_created_time;

    /**当前发起请求的系统版本*/
    private String sys_version_request;

    /**厂所域唯一标识*/
    private String response_site_iid;

    /**反馈的组织数据*/
    private List<GroupBean> ja_divs_response;

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

    public String getResponse_site_iid() {
        return response_site_iid;
    }

    public void setResponse_site_iid(String response_site_iid) {
        this.response_site_iid = response_site_iid;
    }

    public List<GroupBean> getJa_divs_response() {
        return ja_divs_response;
    }

    public void setJa_divs_response(List<GroupBean> ja_divs_response) {
        this.ja_divs_response = ja_divs_response;
    }
}
