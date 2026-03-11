package ext.casc.sop.bean;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "Product")
@XmlAccessorType(XmlAccessType.FIELD)
public class SOPProductBean {

    public SOPProductBean() {
    }

    public SOPProductBean(String oid, String productName, String productNumber, SOPPartsBean partsBean) {
        this.oid = oid;
        this.productName = productName;
        this.productNumber = productNumber;
        this.partsBean = partsBean;
    }

    @XmlAttribute(name = "oid")
    private String oid;

    @XmlAttribute(name = "productName")
    private String productName;

    @XmlAttribute(name = "productNumber")
    private String productNumber;

    @XmlElement(name = "parts")
    private SOPPartsBean partsBean;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public SOPPartsBean getPartsBean() {
        return partsBean;
    }

    public void setPartsBean(SOPPartsBean partsBean) {
        this.partsBean = partsBean;
    }
}
