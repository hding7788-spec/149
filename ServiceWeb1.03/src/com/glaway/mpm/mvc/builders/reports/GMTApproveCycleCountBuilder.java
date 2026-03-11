package com.glaway.mpm.mvc.builders.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import wt.fc.QueryResult;
import wt.util.WTException;

import com.glaway.mpm.report.helper.GMTReportBuilderHelper;
import com.ptc.core.htmlcomp.components.AbstractConfigurableTableBuilder;
import com.ptc.core.htmlcomp.tableview.ConfigurableTable;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;



@ComponentBuilder("com.glaway.mpm.mvc.builders.reports.GMTaskApproveCycleCountBuilder")
public class GMTApproveCycleCountBuilder extends AbstractConfigurableTableBuilder {
	private static int index[] = { 0 };
	private static String departmentPath = "/codebase/com/glaway/mpm/config/groupInDeaprtment.properties";
	public Object buildComponentData(ComponentConfig config, ComponentParams param) throws Exception {
		Map map = new HashMap();
		List list = new ArrayList();
		NmCommandBean bean = ((JcaComponentParams)param).getHelperBean().getNmCommandBean();
		HttpSession session = bean.getRequest().getSession();
		
		String flag = (String) param.getParameter("count");
		flag = flag == null?"":flag.trim();
		System.out.println("+++++ApproveBuilder++++");
		if(session.getAttribute("tashcycleMap") != null){
			session.removeAttribute("tashcycleMap");
		}
		
		String group = (String) param.getParameter("group");
		String principal = (String) param.getParameter("principal");
		String taskType = (String) param.getParameter("taskType");
		String beginTime = (String) param.getParameter("beginTime");
		String endTime = (String) param.getParameter("endTime");
		String department = (String) param.getParameter("department");
		String export = (String) param.getParameter("export");
		group = group == null?"":group.trim();
		principal = principal == null?"":principal.trim();
		taskType = taskType == null?"":taskType.trim();
		beginTime = beginTime == null?"":beginTime.trim();
		endTime = endTime == null?"":endTime.trim();
		department = department == null?"":department.trim();
		export = export == null?"":export;
		System.out.println("---group---" + group);
		System.out.println("---principal---" + principal);
		System.out.println("---taskType---" + taskType);
		System.out.println("---beginTime---" + beginTime);
		System.out.println("---endTime---" + endTime);
		System.out.println("---department---" + department);
		System.out.println("---export---" + export);
		System.out.println("---flag---" + flag);
		QueryResult qr = null;
		if("".equals(flag)){
			session.setAttribute("tashcycleMap", map);
			return list;
		}
		if(!"".equals(department)){
			try{
				String groupInDepartment = GMTReportBuilderHelper.getGroupInDepartment(departmentPath,department);
				if(groupInDepartment != null){
					groupInDepartment = groupInDepartment.replace("；", ";");
					if(groupInDepartment.endsWith(";")){
						group = groupInDepartment + group;
					}else{
						group = groupInDepartment + ";" + group;
					}
				}
			}catch (IOException e) {
				session.setAttribute("tashcycleMap", map);
				e.printStackTrace();
				return list;
			}
		}
		//通过页面的多选框来判断是否进行table的查询，选中则查询
		map = GMTReportBuilderHelper.getResult(list, flag, export, group, principal, taskType, beginTime, endTime, true);
		if(qr != null){
			System.out.println("qr.size()+++++" + qr.size());
		}
//		GMTechnicTaskAccompolishCountReportBuilderUtil.setMap(qr, list, map, flag, export);
		session.setAttribute("tashcycleMap", map);
		return list;
	}
 
	public ComponentConfig buildComponentConfig(ComponentParams param) throws WTException {
		
//		flag = flag.trim();
		ComponentConfigFactory componentconfigfactory = getComponentConfigFactory();
		TableConfig table = componentconfigfactory.newTableConfig();
		
		table.setActionModel("export to xls toolbar");
		
		table.setLabel("工艺设计周期统计");
		table.setId("GMTaskApproveCycleCountBuilder");
		table.setSelectable(false);
		
		ColumnConfig isUrgency = componentconfigfactory.newColumnConfig();
		isUrgency.setId("isUrgency");
		isUrgency.setLabel("是否急件");
		table.addComponent(isUrgency);
		
		ColumnConfig partNumber = componentconfigfactory.newColumnConfig();
		partNumber.setId("partpictureNumber");
		partNumber.setLabel("零件图号");
		table.addComponent(partNumber);
		
		ColumnConfig taskType = componentconfigfactory.newColumnConfig();
		taskType.setId("MytaskType");
		taskType.setLabel("任务类型");
		table.addComponent(taskType);
		
		ColumnConfig state = componentconfigfactory.newColumnConfig();
		state.setId("state");
		state.setLabel("任务状态");
		table.addComponent(state);
		
		ColumnConfig planner = componentconfigfactory.newColumnConfig();
		planner.setId("planner");
		planner.setLabel("计划员");
		table.addComponent(planner);
		
		ColumnConfig plannerDispatchTime = componentconfigfactory.newColumnConfig();
		plannerDispatchTime.setId("plannerDispatchTime");
		plannerDispatchTime.setLabel("计划员派工时间");
		table.addComponent(plannerDispatchTime);
		
		ColumnConfig accomplishedPercent = componentconfigfactory.newColumnConfig();
		accomplishedPercent.setId("cycle");
		accomplishedPercent.setLabel("工艺批准周期T");
		table.addComponent(accomplishedPercent);
		
		ColumnConfig workshop = componentconfigfactory.newColumnConfig();
		workshop.setId("workshop");
		workshop.setLabel("主制单位");
//		workshop.setDataUtilityId("");
		table.addComponent(workshop);
		
		ColumnConfig disgner = componentconfigfactory.newColumnConfig();
		disgner.setId("disgner");
		disgner.setLabel("设计者");
		table.addComponent(disgner);
		
		ColumnConfig reviewer = componentconfigfactory.newColumnConfig();
		reviewer.setId("reviewer");
		reviewer.setLabel("审核者");
		table.addComponent(reviewer);
		
		ColumnConfig professionalGroup = componentconfigfactory.newColumnConfig();
		professionalGroup.setId("professionalGroup");
		professionalGroup.setLabel("专业组");
		table.addComponent(professionalGroup);
		
		ColumnConfig groupLeader = componentconfigfactory.newColumnConfig();
		groupLeader.setId("groupLeader");
		groupLeader.setLabel("组长");
		table.addComponent(groupLeader);
		
		ColumnConfig mainDivish = componentconfigfactory.newColumnConfig();
		mainDivish.setId("mainDivish");
		mainDivish.setLabel("工艺主师");
		table.addComponent(mainDivish);
		
		ColumnConfig createStamp = componentconfigfactory.newColumnConfig();
		createStamp.setId("createStamp");
		createStamp.setLabel("创建时间");
		table.addComponent(createStamp);
		
		ColumnConfig estimatedSubmitTime = componentconfigfactory.newColumnConfig();
		estimatedSubmitTime.setId("estimatedSubmitTime");
		estimatedSubmitTime.setLabel("预计提交时间");
		table.addComponent(estimatedSubmitTime);
		
		ColumnConfig estimatedApproveTime = componentconfigfactory.newColumnConfig();
		estimatedApproveTime.setId("myestimatedApproveTime");
		estimatedApproveTime.setLabel("预计批准时间");
		table.addComponent(estimatedApproveTime);
		
		ColumnConfig actualSubmitTime = componentconfigfactory.newColumnConfig();
		actualSubmitTime.setId("myactualSubmitTime");
		actualSubmitTime.setLabel("实际提交时间");
		table.addComponent(actualSubmitTime);
			
		ColumnConfig actualApproveTime = componentconfigfactory.newColumnConfig();
		actualApproveTime.setId("myactualApproveTime");
		actualApproveTime.setLabel("实际批准时间");
		table.addComponent(actualApproveTime);
		
		return table;
		
	}


	
	public static String changeIntToString(int num){
		String	numStr = String.valueOf(num);
			return numStr;
		
	}
	public ConfigurableTable buildConfigurableTable(String arg0) throws WTException {
		// TODO Auto-generated method stub
		return null;
	}
}

