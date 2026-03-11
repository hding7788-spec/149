package com.glaway.mpm.parameter.model.tree;

import java.awt.Image;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import com.glaway.mpm.parameter.model.data.CmTreeNode;

public class XWParamTableTypeTreeObject implements XWTreeObject, Serializable {

	private static final long serialVersionUID = 1L;

	private CmTreeNode treeNode;

	public XWParamTableTypeTreeObject(CmTreeNode treeNode) {
		this.treeNode = treeNode;
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public List<XWTreeObject> expand() {
		List<XWTreeObject> list = new ArrayList<XWTreeObject>();
//		List<CmParamTableType> parameterTypes = ((CmParamTableType)treeNode).getChildParameterTypes();
//		XWParamTableTypeTreeObject treeObject = null;
//		for (CmParameterType cmParameterType : parameterTypes) {
//			treeObject = new XWParamTableTypeTreeObject(cmParameterType);
//			list.add(treeObject);
//		}
		return list;
	}

	@Override
	public List<XWTreeObject> expandNode() {
		List<XWTreeObject> list = new ArrayList<XWTreeObject>();
//		List<CmParameterType> parameterTypes = ((CmParameterType)treeNode).getChildParameterTypes();
//		XWParamTableTypeTreeObject treeObject = null;
//		for (CmParameterType cmParameterType : parameterTypes) {
//			treeObject = new XWParamTableTypeTreeObject(cmParameterType);
//			list.add(treeObject);
//		}
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
	public void setTreeNode(CmTreeNode treeNodeObject) {
		this.treeNode = treeNodeObject;
	}
}
