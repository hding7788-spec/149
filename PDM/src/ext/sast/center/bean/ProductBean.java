package ext.sast.center.bean;

/**
 * @ Author     ：LB.
 * @ Date       ：Created in 2019/3/18
 * @ Description：发起跨域型号信息
 * @ Modified By：
 */
public class ProductBean {
    /**发起域型号唯一标识*/
    private String iid;

    /**发起域型号编号*/
    private String id;

    /**发起域型号名称*/
    private String name;

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
