package ext.sast.center.bean;

/**
 * @ Author     ：LB.
 * @ Date       ：Created in 2019/3/18
 * @ Description：跨域对象信息
 * @ Modified By：
 */
public class JaObjectBean {
    /**对象oid(classid:innerid)*/
    private String object_oid;

    /**主对象唯一标识，无主对象为空字符窜*/
    private String master_iid;

    /**对象编号*/
    private String object_id;

    /**对象名称*/
    private String object_name;

    /**对象状态（例如“审批中”）*/
    private String object_state;

    /**对象版本号（例如A.1）*/
    private String object_version;

    /**对象类路径（带包名）*/
    private String classname;

    /**普通文档和EPMDocument赋值为“_Doc”，部件为“_Item”，各类单据如更改单为“_Order”*/
    private String object_type;

    public String getObject_oid() {
        return object_oid;
    }

    public void setObject_oid(String object_oid) {
        this.object_oid = object_oid;
    }

    public String getMaster_iid() {
        return master_iid;
    }

    public void setMaster_iid(String master_iid) {
        this.master_iid = master_iid;
    }

    public String getObject_id() {
        return object_id;
    }

    public void setObject_id(String object_id) {
        this.object_id = object_id;
    }

    public String getObject_name() {
        return object_name;
    }

    public void setObject_name(String object_name) {
        this.object_name = object_name;
    }

    public String getObject_state() {
        return object_state;
    }

    public void setObject_state(String object_state) {
        this.object_state = object_state;
    }

    public String getObject_version() {
        return object_version;
    }

    public void setObject_version(String object_version) {
        this.object_version = object_version;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getObject_type() {
        return object_type;
    }

    public void setObject_type(String object_type) {
        this.object_type = object_type;
    }
}
