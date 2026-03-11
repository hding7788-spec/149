package com.glaway.mpm.parameter.model.data;

import java.util.ArrayList;
import java.util.List;

/**
 * 参数类型对象
 *
 * @author 龙秀川
 *
 */
public class CmParameterType extends CmTreeNode {

	private static final long serialVersionUID = 1L;

	/** 父类型 */
	private String parent;
	/** 工艺类别 */
	private String technicsType;
	/** 特性值类型 */
	private String valueType;
	/** 参数子类型 */
	private List<CmParameterType> childParameterTypes;
	/** 内部名称 */
	private String enName;
	/** 快捷标识,设置为拼音的首字母 */
	private String shortcut;

	public List<CmParameterType> getChildParameterTypes() {
		if (childParameterTypes == null) {
			childParameterTypes = new ArrayList<CmParameterType>();
		}
		return childParameterTypes;
	}

	public void setChildParameterTypes(List<CmParameterType> childParameterTypes) {
		this.childParameterTypes = childParameterTypes;
	}

	public String getShortcut() {
		return shortcut;
	}

	public void setShortcut(String shortcut) {
		this.shortcut = shortcut;
	}

	public String getTechnicsType() {
		return technicsType;
	}

	public void setTechnicsType(String technicsType) {
		this.technicsType = technicsType;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public String getEnName() {
		return enName;
	}

	public void setEnName(String enName) {
		this.enName = enName;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

}
