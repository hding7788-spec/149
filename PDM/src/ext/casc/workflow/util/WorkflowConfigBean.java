package ext.casc.workflow.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class WorkflowConfigBean {
	private List<String> titleList = new ArrayList<String>();
	private Map<String,Object> contentMap = new HashMap<String,Object>();
	private boolean isConfigBean;
	
	public Map<String, Object> getContentMap() {
		return contentMap;
	}
	public void setContentMap(Map<String, Object> contentMap) {
		this.contentMap = contentMap;
	}
	public List<String> getTitleList() {
		return titleList;
	}
	public void setTitleList(List<String> titleList) {
		this.titleList = titleList;
	}
	
	public WorkflowConfigBean(List<String> titleList,Map<String,Object> contentMap){
		this.titleList = titleList;
		this.contentMap = contentMap;
	}
	
	public WorkflowConfigBean(){
		
	}
	
	public abstract void setDefaultBean(String oid);
	
	public abstract List<String> getRoleList(String oid);
	
	public abstract String getIdentifyStr();
	
	public abstract WorkflowConfigBean mergeBean(WorkflowConfigBean wcb);
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for(int i = 0 ; i < titleList.size() ; i++){
			String tempTitle = titleList.get(i);
			sb.append("title "+i+" is "+ tempTitle);
			sb.append("title "+i+" content is "+contentMap.get(tempTitle));
		}
		return sb.toString();
	}
	public boolean isConfigBean() {
		return isConfigBean;
	}
	public void setConfigBean(boolean isConfigBean) {
		this.isConfigBean = isConfigBean;
	}
}
