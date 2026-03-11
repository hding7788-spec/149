package ext.sast.center.bean.message;

import org.json.JSONArray;

public class ProductModelMappingMessageBean {
	String msg_id;
	String msg_type;
	JSONArray ja_prodmaps_request;
	String operate_type;
	
	public String getMsg_id() {
		return msg_id;
	}
	public void setMsg_id(String msg_id) {
		this.msg_id = msg_id;
	}
	public String getMsg_type() {
		return msg_type;
	}
	public void setMsg_type(String msg_type) {
		this.msg_type = msg_type;
	}
	public JSONArray getJa_prodmaps_request() {
		return ja_prodmaps_request;
	}
	public void setJa_prodmaps_request(JSONArray ja_prodmaps_request) {
		this.ja_prodmaps_request = ja_prodmaps_request;
	}
	public String getOperate_type() {
		return operate_type;
	}
	public void setOperate_type(String operate_type) {
		this.operate_type = operate_type;
	}

}
