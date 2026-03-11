package com.glaway.mpm.parameter.model.tree;

import java.awt.Image;
import java.util.List;

import org.dom4j.Element;

import com.glaway.mpm.parameter.model.data.CmTreeNode;

/**
 * 树节点对象的抽象接口。所有树节点的具体对象都需实现该接口并实现所有的方法。
 *
 * @author 龙秀川
 *
 */
public abstract interface XWTreeObject extends Comparable<Object> {
	/**
	 * 获取节点的所有子节点数据对象
	 *
	 * @return List<XWTreeObject> 数据对象集合
	 */
	public abstract List<XWTreeObject> expand();

	/**
	 * 获取节点的所有子节点数据对象
	 *
	 * @return List<Element> 数据对象集合
	 */
	public abstract List<XWTreeObject> expandNode();

	/**
	 * 获取节点收起时的图标
	 *
	 * @return Image
	 */
	public abstract Image getCloseImage();

	/**
	 * 获取树节点显示名称
	 *
	 * @return String 节点显示明显
	 */
	public abstract String getDisplayName();

	/**
	 * 获取树节点的数据对象
	 *
	 * @return Element 数据对象
	 */
	public abstract Element getElement();

	/**
	 * 获取节点展开时的图标
	 *
	 * @return Image
	 */
	public abstract Image getOpenImage();

	/**
	 * 获取提示信息
	 *
	 * @return String 提示信息
	 */
	public abstract String getTipNoteText();

	/**
	 * 获取树节点的数据对象
	 *
	 * @return CmTreeNode 数据对象
	 */
	public abstract CmTreeNode getTreeNode();

	/**
	 * 设置树节点的数据对象
	 *
	 * @param Element 数据对象
	 */
	public abstract void setElement(Element element);

	/**
	 * 设置树节点的数据对象
	 *
	 * @param treeNodeObject 数据对象
	 */
	public abstract void setTreeNode(CmTreeNode treeNodeObject);
}
