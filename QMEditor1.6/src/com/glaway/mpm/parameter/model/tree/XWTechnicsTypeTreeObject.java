package com.glaway.mpm.parameter.model.tree;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import com.glaway.mpm.parameter.model.data.CmParamTableType;
import com.glaway.mpm.parameter.model.data.CmTechnicsType;
import com.glaway.mpm.parameter.model.data.CmTreeNode;

public class XWTechnicsTypeTreeObject implements XWTreeObject {

	private CmTreeNode treeNode;

	public XWTechnicsTypeTreeObject(CmTreeNode treeNode) {
		this.treeNode = treeNode;
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public List<XWTreeObject> expand() {
		List<XWTreeObject> list = new ArrayList<XWTreeObject>();
		List<CmParamTableType> paramTableTypes = ((CmTechnicsType)treeNode).getParamTableTypes();
		XWParamTableTypeTreeObject treeObject = null;
		for (CmParamTableType paramTableType : paramTableTypes) {
			if ("启用".equals(paramTableType.getIsUsed())) {
				treeObject = new XWParamTableTypeTreeObject(paramTableType);
				list.add(treeObject);
			}
		}
		return list;
	}

	@Override
	public List<XWTreeObject> expandNode() {
		List<XWTreeObject> list = new ArrayList<XWTreeObject>();
		List<CmParamTableType> paramTableTypes = ((CmTechnicsType)treeNode).getParamTableTypes();
		XWParamTableTypeTreeObject treeObject = null;
		for (CmParamTableType paramTableType : paramTableTypes) {
			treeObject = new XWParamTableTypeTreeObject(paramTableType);
			list.add(treeObject);
		}
		return list;
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
