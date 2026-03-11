package ext.casc.sop.bean;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "parts")
@XmlAccessorType(XmlAccessType.FIELD)
public class SOPPartsBean {

    public SOPPartsBean() {
    }

    public SOPPartsBean(List<SOPQMPartInfoBean> partInfoBeanList) {
        this.partInfoBeanList = partInfoBeanList;
    }

    @XmlElement(name = "QMPartInfo")
    private List<SOPQMPartInfoBean> partInfoBeanList;

    public List<SOPQMPartInfoBean> getPartInfoBeanList() {
        return partInfoBeanList;
    }

    public void setPartInfoBeanList(List<SOPQMPartInfoBean> partInfoBeanList) {
        this.partInfoBeanList = partInfoBeanList;
    }
}
