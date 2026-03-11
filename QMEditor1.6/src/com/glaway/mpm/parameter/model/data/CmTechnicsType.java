package com.glaway.mpm.parameter.model.data;

import java.util.ArrayList;
import java.util.List;

/**
 * 工艺类别数据模型
 *
 */
public class CmTechnicsType extends CmTreeNode {

	private static final long serialVersionUID = 1L;

	private String enName;
	private List<CmParamTableType> paramTableTypes;

	public List<CmParamTableType> getParamTableTypes() {
		if (paramTableTypes == null) {
			paramTableTypes = new ArrayList<CmParamTableType>();
		}
		return paramTableTypes;
	}

	public void setParamTableTypes(List<CmParamTableType> paramTableTypes) {
		this.paramTableTypes = paramTableTypes;
	}

	public String getEnName() {
		return enName;
	}

	public void setEnName(String enName) {
		this.enName = enName;
	}


}
