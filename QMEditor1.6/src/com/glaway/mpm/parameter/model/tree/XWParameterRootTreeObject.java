package com.glaway.mpm.parameter.model.tree;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import com.glaway.mpm.parameter.model.data.CmTreeNode;

public class XWParameterRootTreeObject implements XWTreeObject {

	private CmTreeNode treeNode;

	public XWParameterRootTreeObject(CmTreeNode treeNode) {
		this.treeNode = treeNode;
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public List<XWTreeObject> expand() {
		List<XWTreeObject> list = new ArrayList<XWTreeObject>();

//		//检验特性管理
//		CmParameterType parameterType = new CmParameterType();
//		parameterType.setName("检验特性管理");
//		XWParameterTypeTreeObject parameterTypeObject = new XWParameterTypeTreeObject(parameterType);
//		list.add(parameterTypeObject);
//
//		//检验记录表管理
//		CmParamTableType paramTableType = new CmParamTableType();
//		paramTableType.setName("检验记录表管理");
//		XWParamTableTypeTreeObject paramTableTypeObject = new XWParamTableTypeTreeObject(paramTableType);
//		list.add(paramTableTypeObject);

		return list;
	}

	@Override
	public List<XWTreeObject> expandNode() {
		return null;
	}

	@Override
	public Image getCloseImage() {
		return null;
	}

	@Override
	public String getDisplayName() {
		return treeNode.getName();
	}

	@Override
	public Element getElement() {
		return null;
	}

	@Override
	public Image getOpenImage() {
		return null;
	}

	@Override
	public String getTipNoteText() {
		return treeNode.getName();
	}

	@Override
	public CmTreeNode getTreeNode() {
		return treeNode;
	}

	@Override
	public void setElement(Element element) {

	}

	@Override
	public void setTreeNode(CmTreeNode treeNode) {
		this.treeNode = treeNode;
	}

}
