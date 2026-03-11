package ext.sast.center.productModel.bean;

import wt.pdmlink.PDMLinkProduct;

public class SAST_PDMLinkProduct extends PDMLinkProduct{
	
	String SAST_ProductOid;
	String SAST_ProductName;
	String SAST_Remarks;
	public String getSAST_ProductOid() {
		return SAST_ProductOid;
	}
	public void setSAST_ProductOid(String sAST_ProductOid) {
		SAST_ProductOid = sAST_ProductOid;
	}
	public String getSAST_ProductName() {
		return SAST_ProductName;
	}
	public void setSAST_ProductName(String sAST_ProductName) {
		SAST_ProductName = sAST_ProductName;
	}
	public String getSAST_Remarks() {
		return SAST_Remarks;
	}
	public void setSAST_Remarks(String sAST_remarks) {
		SAST_Remarks = sAST_remarks;
	}
	
	
}
