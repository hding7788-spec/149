package com.glaway.mpm.parameter.model.data;

import java.util.Vector;

/**
 * 模版参数表对象
 * 
 * @author 龙秀川
 * 
 */
public class CmTemplateParamTable extends CmTreeNode {

	private static final long serialVersionUID = 1L;

	/** 参数表类型ID */
	private String paramTableTypeId;
	/** 中文名称 */
	private String chinaName;
	/** 模板参数表的参数集合 ,按照JTable的二维数据集合存放*/
	private Vector<Vector<String>> params;
	/** 特殊符号图片字节数组 */
	private byte[] imagesByte;

	public String getParamTableTypeId() {
		return paramTableTypeId;
	}

	public void setParamTableTypeId(String paramTableTypeId) {
		this.paramTableTypeId = paramTableTypeId;
	}

	public String getChinaName() {
		return chinaName;
	}

	public void setChinaName(String chinaName) {
		this.chinaName = chinaName;
	}

	public Vector<Vector<String>> getParams() {
		return params;
	}

	public void setParams(Vector<Vector<String>> params) {
		this.params = params;
	}

	public byte[] getImagesByte() {
		return imagesByte;
	}

	public void setImagesByte(byte[] imagesByte) {
		this.imagesByte = imagesByte;
	}

}
