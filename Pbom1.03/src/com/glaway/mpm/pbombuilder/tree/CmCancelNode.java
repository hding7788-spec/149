package com.glaway.mpm.pbombuilder.tree;

import java.io.Serializable;
import java.util.List;
/**
 * 存放节点操作的对象---------主要作用于节点撤销
 * objNode 操作的节点
 * parentNode操作的节点的父节点
 * childNode操作的节点的子节点集
 * operation操作类型
 * Created on 2012-11-27
 * 
 * @author chenyunlong
 */
public class CmCancelNode implements Serializable  {
	private static final long serialVersionUID = 1L;
	private CmTreeNode objNode;
	private CmTreeNode parentNode;//现在的父节点
	private CmTreeNode newParentNode;//原来的父节点
	private List<CmTreeNode> childNode;
	private List<CmCancelNode> cancelList;
	private String operation;  //create|delete|update|virtual|package|unpackages(新增（添加中间件、添加辅件、粘贴）、删除（移除,剪切）、更新（）、设置虚拟件、打包、拆包)
	private boolean isAllPackage;
	private String objindex;
	private String paretnindex;
	public CmTreeNode getObjNode() {
		return objNode;
	}
	public void setObjNode(CmTreeNode objNode) {
		this.objNode = objNode;
	}
	public CmTreeNode getParentNode() {
		return parentNode;
	}
	public void setParentNode(CmTreeNode parentNode) {
		this.parentNode = parentNode;
	}
	public List<CmTreeNode> getChildNode() {
		return childNode;
	}
	public void setChildNode(List<CmTreeNode> childNode) {
		this.childNode = childNode;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public List<CmCancelNode> getCancelList() {
		return cancelList;
	}
	public void setCancelList(List<CmCancelNode> cancelList) {
		this.cancelList = cancelList;
	}
	public boolean isAllPackage() {
		return isAllPackage;
	}
	public void setAllPackage(boolean isAllPackage) {
		this.isAllPackage = isAllPackage;
	}
	public CmTreeNode getNewParentNode() {
		return newParentNode;
	}
	public void setNewParentNode(CmTreeNode newParentNode) {
		this.newParentNode = newParentNode;
	}
	public String getObjindex() {
		return objindex;
	}
	public void setObjindex(String objindex) {
		this.objindex = objindex;
	}
	public String getParetnindex() {
		return paretnindex;
	}
	public void setParetnindex(String paretnindex) {
		this.paretnindex = paretnindex;
	}
}
