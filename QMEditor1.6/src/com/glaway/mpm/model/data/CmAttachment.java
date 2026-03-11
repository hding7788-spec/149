package com.glaway.mpm.model.data;

/**
 * 附件数据模型
 * 
 * @author 龙秀川
 * 
 */
public class CmAttachment extends CmTreeNode {

	private static final long serialVersionUID = 1L;
	/** 附件类型 */
	private String attachmentType;
	/** 附件文件字节数组 */
	private byte[] bytes;
	/** 附件名称 */
	private String fileName;
	/** 模板类型 */
	private String templateType;
	/** 顺序号 */
	private String orderNo;

	public String getAttachmentType() {
		return attachmentType;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public String getFileName() {
		return fileName;
	}

	public void setAttachmentType(String attachmentType) {
		this.attachmentType = attachmentType;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTemplateType() {
		return templateType;
	}

	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

}
