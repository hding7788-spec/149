package com.glaway.mpm.model.data;

import java.io.Serializable;

/**
 * 封装WTUser的数据模型
 * 
 * @author 龙秀川
 *
 */
public class CmUser implements Serializable {

	private static final long serialVersionUID = 1L;
	/** 所属部门 */
	private String department;
	/** 用户的电子邮件 */
	private String email;
	/** 用户全名 */
	private String fullName;
	/** 用于登录系统的名称 */
	private String name;
	/** 数据对象的唯一标识符 */
	private long oid;
	/** 是否定版工艺修改组 */
	private boolean approveModifyGroup;

	public String getDepartment() {
		return department;
	}

	public String getEmail() {
		return email;
	}

	public String getFullName() {
		return fullName;
	}

	public String getName() {
		return name;
	}

	public long getOid() {
		return oid;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOid(long oid) {
		this.oid = oid;
	}

	public boolean isApproveModifyGroup() {
		return approveModifyGroup;
	}

	public void setApproveModifyGroup(boolean approveModifyGroup) {
		this.approveModifyGroup = approveModifyGroup;
	}

	@Override
	public String toString() {
		return "CmUser [fullName=" + fullName + ", department=" + department
				+ "]";
	}

}
