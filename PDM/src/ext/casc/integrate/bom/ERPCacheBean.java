package ext.casc.integrate.bom;

import ext.casc.integrate.model.GLErpMaterialsBean;
import ext.casc.integrate.model.GLErpPbomPartBean;
import ext.casc.integrate.model.GLErpPeiTaoPartBean;
import wt.doc.WTDocument;

import java.util.ArrayList;
import java.util.List;

public class ERPCacheBean {
    private List<WTDocument> documentList = new ArrayList<WTDocument>();
    private List<GLErpPbomPartBean>  erpNewPartList = new ArrayList<GLErpPbomPartBean>();
    private List<GLErpMaterialsBean> materialBeanList = new ArrayList<GLErpMaterialsBean>();
    private List<GLErpPbomPartBean>  erpMatchPartList = new ArrayList<GLErpPbomPartBean>();
    private List<GLErpPeiTaoPartBean>  erpPeiTaoPartList = new ArrayList<GLErpPeiTaoPartBean>();


    public List<GLErpPbomPartBean> getErpMatchPartList() {
        return erpMatchPartList;
    }

    public void setErpMatchPartList(List<GLErpPbomPartBean> erpMatchPartList) {
        this.erpMatchPartList = erpMatchPartList;
    }

    public List<GLErpMaterialsBean> getMaterialBeanList() {
        return materialBeanList;
    }

    public void setMaterialBeanList(List<GLErpMaterialsBean> materialBeanList) {
        this.materialBeanList = materialBeanList;
    }

    public List<WTDocument> getDocumentList() {
        return documentList;
    }

    public void setDocumentList(List<WTDocument> documentList) {
        this.documentList = documentList;
    }

    public List<GLErpPbomPartBean> getErpNewPartList() {
        return erpNewPartList;
    }

    public void setErpNewPartList(List<GLErpPbomPartBean> erpNewPartList) {
        this.erpNewPartList = erpNewPartList;
    }

	public List<GLErpPeiTaoPartBean> getErpPeiTaoPartList() {
		return erpPeiTaoPartList;
	}

	public void setErpPeiTaoPartList(List<GLErpPeiTaoPartBean> erpPeiTaoPartList) {
		this.erpPeiTaoPartList = erpPeiTaoPartList;
	}
}
