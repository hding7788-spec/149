package ext.sast.center.bean;

/**
 * @ Author     ：LB.
 * @ Date       ：Created in 2019/3/18
 * @ Description：域信息
 * @ Modified By：
 */
public class SiteBean {
    /**域唯一标识*/
    private String iid;

    /**域编号*/
    private String id;

    /**域名称*/
    private String name;

    /**域ip*/
    private String ip;

    /**域端口号*/
    private String port;

    /**域版本*/
    private String version;

    /**域院编号*/
    private String academy_id;

    /**域院名称*/
    private String academy_name;

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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAcademy_id() {
        return academy_id;
    }

    public void setAcademy_id(String academy_id) {
        this.academy_id = academy_id;
    }

    public String getAcademy_name() {
        return academy_name;
    }

    public void setAcademy_name(String academy_name) {
        this.academy_name = academy_name;
    }
}
