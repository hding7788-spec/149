package ext.sast.center.bean.message;

import java.util.List;

import ext.sast.center.bean.FileBean;
import ext.sast.center.bean.JaDispatchBean;
import ext.sast.center.bean.JaObjectBean;
import ext.sast.center.bean.JaSignBean;
import ext.sast.center.bean.JaTaskBean;
import ext.sast.center.bean.ProductBean;
import ext.sast.center.bean.SiteBean;
import ext.sast.center.bean.UserBean;

/**
 * @ Author     ：LB.
 * @ Date       ：Created in 2019/3/18
 * @ Description：业务数据信息消息对象，（预审、会签、发放）
 * @ Modified By：
 */
public class BusinessDataMessageBean {

    /**消息的唯一标识，
     * 不能为空
     * 【如果有跨域单据（会签、预审、发放），以跨域单据对象唯一标识填写该值；
     * 如果没有跨域单据，则填写uuid作为唯一标识，一次完整的业务过程，该值相同】
     */
    private String msg_id;

    /**附件信息*/
    private FileBean j_file;

    /**消息类型，不能为空且为协议约定类型*/
    private String msg_type;

    /**消息描述，可不填*/
    private String msg_description;

    /**消息创建时间*/
    private long msg_created_time;

    /**原始发起单位的系统版本*/
    private String sys_version_initial;

    /**当前发起请求的系统版本*/
    private String sys_version_request;

    private String response_site_iid;


    /**原始发起单位信息*/
    private SiteBean j_src_site;

    /**发起人信息*/
    private UserBean j_creator;

    /**发起单据型号信息*/
    private ProductBean j_product;

    /**接收单位信息*/
    private List<SiteBean> ja_dst_sites;

    /**接收人信息*/
    private List<UserBean> ja_receivers;

    /**发起请求的对象信息*/
    private List<JaObjectBean> ja_objects_request;

    private List<JaDispatchBean> ja_dispatchs_request;

    /**反馈的任务信息*/
    private List<JaTaskBean> ja_tasks_response;

    /**反馈的签署信息*/
    private List<JaSignBean> ja_signs_response;

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public FileBean getJ_file() {
        return j_file;
    }

    public void setJ_file(FileBean j_file) {
        this.j_file = j_file;
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

    public String getSys_version_initial() {
        return sys_version_initial;
    }

    public void setSys_version_initial(String sys_version_initial) {
        this.sys_version_initial = sys_version_initial;
    }

    public String getSys_version_request() {
        return sys_version_request;
    }

    public void setSys_version_request(String sys_version_request) {
        this.sys_version_request = sys_version_request;
    }

    public SiteBean getJ_src_site() {
        return j_src_site;
    }

    public void setJ_src_site(SiteBean j_src_site) {
        this.j_src_site = j_src_site;
    }

    public UserBean getJ_creator() {
        return j_creator;
    }

    public void setJ_creator(UserBean j_creator) {
        this.j_creator = j_creator;
    }

    public ProductBean getJ_product() {
        return j_product;
    }

    public void setJ_product(ProductBean j_product) {
        this.j_product = j_product;
    }

    public List<SiteBean> getJa_dst_sites() {
        return ja_dst_sites;
    }

    public void setJa_dst_sites(List<SiteBean> ja_dst_sites) {
        this.ja_dst_sites = ja_dst_sites;
    }

    public List<UserBean> getJa_receivers() {
        return ja_receivers;
    }

    public void setJa_receivers(List<UserBean> ja_receivers) {
        this.ja_receivers = ja_receivers;
    }

    public List<JaObjectBean> getJa_objects_request() {
        return ja_objects_request;
    }

    public void setJa_objects_request(List<JaObjectBean> ja_objects_request) {
        this.ja_objects_request = ja_objects_request;
    }

    public List<JaDispatchBean> getJa_dispatchs_request() {
        return ja_dispatchs_request;
    }

    public void setJa_dispatchs_request(List<JaDispatchBean> ja_dispatchs_request) {
        this.ja_dispatchs_request = ja_dispatchs_request;
    }

    public String getResponse_site_iid() {
        return response_site_iid;
    }

    public void setResponse_site_iid(String response_site_iid) {
        this.response_site_iid = response_site_iid;
    }

    public List<JaTaskBean> getJa_tasks_response() {
        return ja_tasks_response;
    }

    public void setJa_tasks_response(List<JaTaskBean> ja_tasks_response) {
        this.ja_tasks_response = ja_tasks_response;
    }

    public List<JaSignBean> getJa_signs_response() {
        return ja_signs_response;
    }

    public void setJa_signs_response(List<JaSignBean> ja_signs_response) {
        this.ja_signs_response = ja_signs_response;
    }
}
	