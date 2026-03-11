package ext.sast.center.bean;

/**
 * @ Author     ：LB.
 * @ Date       ：Created in 2019/3/12
 * @ Description：协同站点Bean对象
 * @ Modified By：
 */
public class CenterSiteBean {

    /**站点编号*/
    private String number;

    /**站点名称*/
    private String name;

    /**站点ip*/
    private String ip;

    /**站点端口号*/
    private String port;

    /**系统版本*/
    private String sysVersion;

    /**站点类型*/
    private String siteType;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
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

    public String getSysVersion() {
        return sysVersion;
    }

    public void setSysVersion(String sysVersion) {
        this.sysVersion = sysVersion;
    }

    public String getSiteType() {
        return siteType;
    }

    public void setSiteType(String siteType) {
        this.siteType = siteType;
    }
}
