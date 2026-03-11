package com.glaway.mpm.model.data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * 工艺编辑器中树节点的数据对象基类。封装了业务对象的基本属性信息。
 * 
 * @author 龙秀川
 *
 */
public class CmTreeNode implements Serializable {
	private static final long serialVersionUID = 1L;

	/** 创建时间 */
	private Timestamp createTimestamp;
	/** 创建者 */
	private CmUser createUser;
	/** 设备的软属性集合 */
	private Map<String, String> ibaAttributes = new HashMap<String, String>();
	/** 该对象是否发生了数据更改.false表示数据未修改. */
	private boolean isChanged = false;
	/** 主对象唯一标识符 */
	private long masterOid;
	/** 最近修改时间 */
	private Timestamp modifyTimestamp;
	/** 修改者 */
	private CmUser modifyUser;
	/** 名称 */
	private String name;
	/** 编号 */
	private String number;
	/** 数据对象的唯一标识符 */
	private long oid;
	/** 生命周期状态 */
	private String state;
	/** 版本 */
	private String version;
	
	/**creo view url*/
	private String url;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public Timestamp getCreateTimestamp() {
		return createTimestamp;
	}

	public CmUser getCreateUser() {
		return createUser;
	}

	public Map<String, String> getIbaAttributes() {
		return ibaAttributes;
	}

	public long getMasterOid() {
		return masterOid;
	}

	public Timestamp getModifyTimestamp() {
		return modifyTimestamp;
	}

	public CmUser getModifyUser() {
		return modifyUser;
	}

	public String getName() {
		return name;
	}

	public String getNumber() {
		return number;
	}

	public long getOid() {
		return oid;
	}

	public String getState() {
		return state;
	}

	public String getVersion() {
		return version;
	}

	public boolean isChanged() {
		return isChanged;
	}

	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}

	public void setCreateTimestamp(Timestamp createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public void setCreateUser(CmUser createUser) {
		this.createUser = createUser;
	}

	public void setIbaAttributes(Map<String, String> ibaAttributes) {
		this.ibaAttributes = ibaAttributes;
	}

	public void setMasterOid(long masterOid) {
		this.masterOid = masterOid;
	}

	public void setModifyTimestamp(Timestamp modifyTimestamp) {
		this.modifyTimestamp = modifyTimestamp;
	}

	public void setModifyUser(CmUser modifyUser) {
		this.modifyUser = modifyUser;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public void setOid(long oid) {
		this.oid = oid;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "CmTreeNode [oid=" + oid + ", name=" + name + ", number="
				+ number + ", version=" + version + ", state=" + state
				+ ", createUser=" + createUser + ", isChanged=" + isChanged + "]";
	}

}
