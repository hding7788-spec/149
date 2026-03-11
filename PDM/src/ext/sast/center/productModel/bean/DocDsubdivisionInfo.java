package ext.sast.center.productModel.bean;

import java.io.Serializable;

public class DocDsubdivisionInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String innerId;
	private String comeFromSiteName;
	private String docTypeName;
	private String docTypeInnerName;
	private String localDocTypeName;
	private String localDocTypeInnerName;
	public String getInnerId() {
		return innerId;
	}
	public void setInnerId(String innerId) {
		this.innerId = innerId;
	}
	public String getComeFromSiteName() {
		return comeFromSiteName;
	}
	public void setComeFromSiteName(String comeFromSiteName) {
		this.comeFromSiteName = comeFromSiteName;
	}
	public String getDocTypeName() {
		return docTypeName;
	}
	public void setDocTypeName(String docTypeName) {
		this.docTypeName = docTypeName;
	}
	public String getDocTypeInnerName() {
		return docTypeInnerName;
	}
	public void setDocTypeInnerName(String docTypeInnerName) {
		this.docTypeInnerName = docTypeInnerName;
	}
	public String getLocalDocTypeName() {
		return localDocTypeName;
	}
	public void setLocalDocTypeName(String localDocTypeName) {
		this.localDocTypeName = localDocTypeName;
	}
	public String getLocalDocTypeInnerName() {
		return localDocTypeInnerName;
	}
	public void setLocalDocTypeInnerName(String localDocTypeInnerName) {
		this.localDocTypeInnerName = localDocTypeInnerName;
	}
	
}
