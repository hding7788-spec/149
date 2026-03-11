package ext.casc.workflow.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wt.change2.WTChangeOrder2;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import ext.casc.workflow.CSCWorkflowException;
import ext.casc.workflow.TaskConfigrationHelper;

public class ASESWorkflowConfigBean extends WorkflowConfigBean{
	public static final String DOC_IDENTIFYSTR = "文档分类";
	
	public static final String DOC_SOFT_IDENTIFYSTR = "文档小类";
	
	public static final String PART_IDENTIFYSTR = "零部件分类";
	
	public static final String ECN_IDENTIFYSTR = "变更通告分类";
	
	public static final String EPM_IDENTIFYSTR = "图档分类";
	
	@Override
	public String getIdentifyStr() {
		String tempIdentify = (String)this.getContentMap().get(DOC_IDENTIFYSTR);
		if(tempIdentify != null){
			tempIdentify = tempIdentify + "-" + (String)this.getContentMap().get(DOC_SOFT_IDENTIFYSTR);
		}else{
			tempIdentify = (String)this.getContentMap().get(PART_IDENTIFYSTR);
		}
		if(tempIdentify != null){
			;
		}else{
			tempIdentify = (String)this.getContentMap().get(ECN_IDENTIFYSTR);
		}
		if(tempIdentify != null){
			tempIdentify = tempIdentify + "-";
		}else{
			tempIdentify = (String)this.getContentMap().get(EPM_IDENTIFYSTR);
		}
		
		if(tempIdentify != null){
			tempIdentify = tempIdentify + "-";
		}
		return tempIdentify;
	}

	@Override
	public WorkflowConfigBean mergeBean(WorkflowConfigBean wcb) {
		WTChangeOrder2 co = null;

		if(wcb == null){
			return this;
		}
		List<String> titleList = this.getTitleList();
		Map<String,Object> own_Map = this.getContentMap();
		Map<String,Object> mergedMap = wcb.getContentMap();
		for(int i = 2 ; i < titleList.size() ; i++){
			String tempTitle = titleList.get(i);
			Object temp_Own_content = own_Map.get(tempTitle);
			Object merged_content = mergedMap.get(tempTitle);
			if(merged_content == null || merged_content.equals("")){
				if(temp_Own_content == null || temp_Own_content.equals("")){
					merged_content = "";
					mergedMap.put(tempTitle, merged_content);
				}else{
					merged_content = temp_Own_content;
					mergedMap.put(tempTitle, merged_content);
				}
			}else if(merged_content.equals("2")){
				if(temp_Own_content != null && (temp_Own_content.equals("1")||temp_Own_content.equals("3"))){
					merged_content = temp_Own_content;
					mergedMap.put(tempTitle, merged_content);
				}
			}else if(merged_content.equals("3")){
				if(temp_Own_content != null && temp_Own_content.equals("1")){
					merged_content = temp_Own_content;
					mergedMap.put(tempTitle, merged_content);
				}
			}
		}
		return wcb;
	}

	@Override
	public void setDefaultBean(String oid) {
		String path = null;
		try {
			path = (String)TaskConfigrationHelper.getActivityVariableByVarName(oid, "workflowPath");
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		if(path == null){
			path = "";
		}else{
			List<String> pathList = new ArrayList<String>();
			Map<String,Object> contentMap = new HashMap<String,Object>();
			String pathArray[] = path.split(",");
			for(int i = 0 ; i < pathArray.length ;i++){
				pathList.add(pathArray[i]);
				contentMap.put(pathArray[i], "1");
			}
			this.setTitleList(pathList);
			this.setContentMap(contentMap);
		}
	}

	@Override
	public List<String> getRoleList(String oid) {
		List<String> roleList = new ArrayList<String>();
		if(this.isConfigBean()){
			List<String> titleList = this.getTitleList();
			Map<String,Object> contentMap = this.getContentMap();
			try {
				PropertiesUtil pu = new PropertiesUtil(File.separator+"codebase"+File.separator+"ext"+File.separator+"ases"+File.separator+"workflow"+File.separator+"setparticipant"+File.separator+"setparticipant_properties.properties");
				for(int i = 2 ; i < titleList.size() ; i++){
					String tempTitle = titleList.get(i);
					String tempContent = (String)contentMap.get(tempTitle);
					if(tempContent != null && !tempContent.equals("")){
						String tempRole = pu.getValue(tempTitle);
						roleList.add(tempRole);
					}
				}
			} catch (CSCWorkflowException e) {
				e.printMessage();
			}
		}else{
			try {
				String roleArray[] = ((String)TaskConfigrationHelper.getActivityVariableByVarName(oid, "workflowRole")).split(",");
				for(int i = 0 ; i < roleArray.length ; i++){
					roleList.add(roleArray[i]);
				}
			} catch (WTRuntimeException e) {
				e.printStackTrace();
			} catch (WTException e) {
				e.printStackTrace();
			}
		}
		return roleList;
	}
}
