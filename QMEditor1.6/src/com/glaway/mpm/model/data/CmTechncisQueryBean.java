package com.glaway.mpm.model.data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * 工艺搜索查询Bean
 * @author wxl
 *
 */
public class CmTechncisQueryBean extends CmTreeNode implements Serializable {

	private static final long serialVersionUID = 1L;
	/** 工艺规程名称 */
	private String technicsName = "";
	/** 工艺规程编号 */
	private String technicsVersion = "";
	/** 是否批准 */
	private boolean isApprove = false;
	/** 创建者 */
	private String createUserName = "";
	/** 修改者 */
	private String modifyUserName = "";
	/** 日期类型（创建时间/批准时间） */
	private String dateType = "";
	/** 日期上限 */
	private Timestamp fromTime;
	/** 日期下限 */
	private Timestamp toTime;
	/** 型号代号 */
	private String mindex;
	/** 文件类型 */
	private String type;
	/** 标准属性集合 */
	private Map<String, Object> mbaAttributes = new HashMap<String, Object>();
	
	//20170322_jiangyixing
	/** 产品代号 */
	private String pindex;
	
	public String getTechnicsName() {
		return technicsName;
	}

	public void setTechnicsName(String technicsName) {
		this.technicsName = technicsName;
	}

	public String getTechnicsVersion() {
		return technicsVersion;
	}

	public void setTechnicsVersion(String technicsVersion) {
		this.technicsVersion = technicsVersion;
	}

	public boolean isApprove() {
		return isApprove;
	}

	public void setApprove(boolean isApprove) {
		this.isApprove = isApprove;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public String getModifyUserName() {
		return modifyUserName;
	}

	public void setModifyUserName(String modifyUserName) {
		this.modifyUserName = modifyUserName;
	}

	public String getDateType() {
		return dateType;
	}

	public void setDateType(String dateType) {
		this.dateType = dateType;
	}

	public Timestamp getFromTime() {
		return fromTime;
	}

	public void setFromTime(Timestamp fromTime) {
		this.fromTime = fromTime;
	}

	public Timestamp getToTime() {
		return toTime;
	}

	public void setToTime(Timestamp toTime) {
		this.toTime = toTime;
	}

	public String getMindex() {
		return mindex;
	}

	public void setMindex(String mindex) {
		this.mindex = mindex;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, Object> getMbaAttributes() {
		return mbaAttributes;
	}

	public void setMbaAttributes(Map<String, Object> mbaAttributes) {
		this.mbaAttributes = mbaAttributes;
	}

	public String getPindex() {
		return pindex;
	}

	public void setPindex(String pindex) {
		this.pindex = pindex;
	}
	
	
}
