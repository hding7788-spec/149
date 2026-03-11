package com.bjsasc.avidm.mq.message;

import org.json.JSONArray;

public class MetaMessage extends Message {

	public MetaMessage() {
		super();

		// 初始状态为刚创建
		put(MSG_STATUS, MSG_STATUS_NEW);
	}

	// 消息的唯一ID
	public void setMsgId(String id) {
		put(MSG_ID, id);
	}

	// 此次业务操作的类型，如跨域会签、跨域分发等
	public void setMsgType(String type) {
		put(MSG_TYPE, type);
	}

	public void setMsgStatus(int status) {
		put(MSG_STATUS, status);
	}

	// 单据的唯一标识，可不设置
	public void setOrderIID(String iid) {
		put(ORDER_IID, iid);
	}

	// 单据的编号，可不设置
	public void setOrderID(String id) {
		put(ORDER_ID, id);
	}

	// 单据的名称，可不设置
	public void setOrderName(String name) {
		put(ORDER_NAME, name);
	}

	// 单据关联对象的概要信息（如对象的编号等），可不填
	public void setObjectSummary(String summary) {
		put(OBJECT_SUMMARY, summary);
	}

	// 发起单位的名称
	public void setSrcSiteName(String name) {
		put(SRC_SITE_NAME, name);
	}

	// 发起单位的iid
	public void setSrcSiteIID(String iid) {
		put(SRC_SITE_IID, iid);
	}

	// 接收单位的IID集合
	public void setdDstSites(JSONArray sites) {
		put(JA_DST_SITES, sites);
	}

	// 接收单位的概要信息（如有几个接收单位，可合并在一块）
	public void setdDstSiteInfo(String info) {
		put(DST_SITES_INFO, info);
	}

	// 发起人的唯一标识，可不填
	public void setUserIID(String iid) {
		put(USER_IID, iid);
	}

	// 发起人的标识，可不填
	public void setUserID(String id) {
		put(USER_ID, id);
	}

	// 发起人的名字
	public void setUserName(String name) {
		put(USER_NAME, name);
	}
}
