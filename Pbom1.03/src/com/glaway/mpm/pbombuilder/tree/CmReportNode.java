package com.glaway.mpm.pbombuilder.tree;

import java.io.Serializable;
/**
 * 报表中节点对象
 * @author chenyunlong
 * @date  2012-12-3
 * @param operation
 *
 */
public class CmReportNode  implements Serializable{
	private static final long serialVersionUID = 1L;
	private CmLightPart ebomPart;
	private CmLightPart pbomPart;
	private String ebomCount;
	private String pbomCount;
	public CmLightPart getEbomPart() {
		return ebomPart;
	}
	public void setEbomPart(CmLightPart ebomPart) {
		this.ebomPart = ebomPart;
	}
	public CmLightPart getPbomPart() {
		return pbomPart;
	}
	public void setPbomPart(CmLightPart pbomPart) {
		this.pbomPart = pbomPart;
	}
	public String getEbomCount() {
		return ebomCount;
	}
	public void setEbomCount(String ebomCount) {
		this.ebomCount = ebomCount;
	}
	public String getPbomCount() {
		return pbomCount;
	}
	public void setPbomCount(String pbomCount) {
		this.pbomCount = pbomCount;
	}
	
	
}
