package ext.casc.part.bean;

import java.util.List;

import wt.part.WTPart;
import wt.part.WTPartUsageLink;

/**
 * 部件关系实体类
 * @author Liluwen
 * @date 2025年8月14日下午5:50:14
 */
public class PartUsageBean {
	/**
	 * 父部件
	 */
	private  WTPart part;
	/**
	 * 部件关系
	 */
	private WTPartUsageLink wtPartUsageLink;
	/**
	 * 子部件
	 */
	private WTPart childPart;
	
	/**
	 * 子部件和孙子部件的集合
	 */
	private List<PartUsageBean> subPartUsageList;
	
	
	public List<PartUsageBean> getSubPartUsageList() {
		return subPartUsageList;
	}
	public void setSubPartUsageList(List<PartUsageBean> subPartUsageList) {
		this.subPartUsageList = subPartUsageList;
	}
	public WTPart getPart() {
		return part;
	}
	public void setPart(WTPart part) {
		this.part = part;
	}
	public WTPartUsageLink getWtPartUsageLink() {
		return wtPartUsageLink;
	}
	public void setWtPartUsageLink(WTPartUsageLink wtPartUsageLink) {
		this.wtPartUsageLink = wtPartUsageLink;
	}
	public WTPart getChildPart() {
		return childPart;
	}
	public void setChildPart(WTPart childPart) {
		this.childPart = childPart;
	}
	
	
	
}
