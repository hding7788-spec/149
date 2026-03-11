package ext.sast.center.bean;

/**
 * @ Author     ：LB.
 * @ Date       ：Created in 2019/3/18
 * @ Description：跨域任务信息
 * @ Modified By：
 */
public class JaTaskBean {
    /**任务唯一标识*/
    private String task_iid;

    /**任务名称*/
    private String task_name;

    /**处理人唯一标识*/
    private String task_user_iid;

    /**处理人id*/
    private String task_user_id;

    /**处理人名称*/
    private String task_user_name;

    /**任务处理状态（需要值映射）*/
    private String task_state;

    /**父任务唯一标识，如果没有父任务该值为-1*/
    private String task_parent_iid;

    /**任务创建时间*/
    private long task_create_time;

    public String getTask_iid() {
        return task_iid;
    }

    public void setTask_iid(String task_iid) {
        this.task_iid = task_iid;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getTask_user_iid() {
        return task_user_iid;
    }

    public void setTask_user_iid(String task_user_iid) {
        this.task_user_iid = task_user_iid;
    }

    public String getTask_user_id() {
        return task_user_id;
    }

    public void setTask_user_id(String task_user_id) {
        this.task_user_id = task_user_id;
    }

    public String getTask_user_name() {
        return task_user_name;
    }

    public void setTask_user_name(String task_user_name) {
        this.task_user_name = task_user_name;
    }

    public String getTask_state() {
        return task_state;
    }

    public void setTask_state(String task_state) {
        this.task_state = task_state;
    }

    public String getTask_parent_iid() {
        return task_parent_iid;
    }

    public void setTask_parent_iid(String task_parent_iid) {
        this.task_parent_iid = task_parent_iid;
    }

    public long getTask_create_time() {
        return task_create_time;
    }

    public void setTask_create_time(long task_create_time) {
        this.task_create_time = task_create_time;
    }
}
