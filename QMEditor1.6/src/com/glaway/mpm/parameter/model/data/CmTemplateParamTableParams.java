package com.glaway.mpm.parameter.model.data;

/**
 * 模版参数表的参数对象
 * 
 * @author 龙秀川
 * 
 */
public class CmTemplateParamTableParams extends CmTreeNode {

	private static final long serialVersionUID = 1L;

	/** 顺序号 */
	private String orderNo;
	/** 模板参数表ID */
	private String templateParamTableId;
	/** 参数类型ID */
	private String parameterTypeId;
	/** 要求值 */
	private String requireValue;
	/** 备注 */
	private String remark;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getTemplateParamTableId() {
		return templateParamTableId;
	}

	public void setTemplateParamTableId(String templateParamTableId) {
		this.templateParamTableId = templateParamTableId;
	}

	public String getParameterTypeId() {
		return parameterTypeId;
	}

	public void setParameterTypeId(String parameterTypeId) {
		this.parameterTypeId = parameterTypeId;
	}

	public String getRequireValue() {
		return requireValue;
	}

	public void setRequireValue(String requireValue) {
		this.requireValue = requireValue;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
