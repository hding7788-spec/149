package ext.sast.center.bean;

/**
 * @ Author     ：LB.
 * @ Date       ：Created in 2019/3/18
 * @ Description：签署意见信息
 * @ Modified By：
 */
public class JaSignBean {

    /**任务唯一标识*/
    private String task_iid;

    /**处理人隶属组织名*/
    private String div_name;

    /**是否同意，是1，否0*/
    private String is_agree;

    /**签署意见*/
    private String sign_content;

    /**签署人名称*/
    private String sign_user_name;

    /**意见类型，单位意见：division，个人意见：person*/
    private String mind_type;

    /**签署时间long*/
    private long sign_time;

    public String getTask_iid() {
        return task_iid;
    }

    public void setTask_iid(String task_iid) {
        this.task_iid = task_iid;
    }

    public String getDiv_name() {
        return div_name;
    }

    public void setDiv_name(String div_name) {
        this.div_name = div_name;
    }

    public String getIs_agree() {
        return is_agree;
    }

    public void setIs_agree(String is_agree) {
        this.is_agree = is_agree;
    }

    public String getSign_content() {
        return sign_content;
    }

    public void setSign_content(String sign_content) {
        this.sign_content = sign_content;
    }

    public String getSign_user_name() {
        return sign_user_name;
    }

    public void setSign_user_name(String sign_user_name) {
        this.sign_user_name = sign_user_name;
    }

    public String getMind_type() {
        return mind_type;
    }

    public void setMind_type(String mind_type) {
        this.mind_type = mind_type;
    }

    public long getSign_time() {
        return sign_time;
    }

    public void setSign_time(long sign_time) {
        this.sign_time = sign_time;
    }
}
