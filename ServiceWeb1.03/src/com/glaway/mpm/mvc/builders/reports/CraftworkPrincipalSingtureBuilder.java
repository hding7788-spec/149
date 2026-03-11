package com.glaway.mpm.mvc.builders.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.util.WTException;

import com.glaway.mpm.report.helper.CraftworkPSBuilderHelper;
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

@ComponentBuilder("com.glaway.mpm.mvc.builders.reports.CraftworkPrincipalSingtureBuilder")
public class CraftworkPrincipalSingtureBuilder extends AbstractConfigurableTableBuilder{

	private static int index[] = { 0 };
	public Object buildComponentData(ComponentConfig config, ComponentParams param) throws Exception {
//		NmCommandBean bean = ((JcaComponentParams)param).getHelperBean().getNmCommandBean();
//		HttpSession session = bean.getRequest().getSession();
//		Map map = new HashMap();
		List list = new ArrayList();
//		if(session.getAttribute("signatureMap") != null){
//			session.removeAttribute("signatureMap");
//		}
//		String group = (String) param.getParameter("group");
//		String principal = (String) param.getParameter("principal");
//		String beginTime = (String) param.getParameter("beginTime");
//		String endTime = (String) param.getParameter("endTime");
		String export = (String) param.getParameter("export");
		if(export == null){
			return list;
		}
//		group = group == null?"":group;
//		principal = principal == null?"":principal;
//		beginTime = beginTime == null?"":beginTime;
//		endTime = endTime == null?"":endTime;
//		export = export == null?"":export;
//		System.out.println("---group---" + group);
//		System.out.println("---principal---" + principal);
//		System.out.println("---beginTime---" + beginTime);
//		System.out.println("---endTime---" + endTime);
		System.out.println("---export---" + export);
//		String[] nameList = {"工艺审核流程"};
//		WTUser user = (WTUser) CraftworkPSBuilderHelper.getPrincipal(principal);
		List workItemList = CraftworkPSBuilderHelper.getWorkItemList();
		System.out.println("workItemListsize======" + workItemList.size()); 
		list = CraftworkPSBuilderHelper.getDisPlayMessage(workItemList);
		workItemList.clear();
		System.out.println("messagelist.size======" + list.size());
//		session.setAttribute("signatureMap", map);
		return list;
	}
 
	public ComponentConfig buildComponentConfig(ComponentParams param) throws WTException {
		
//		flag = flag.trim();
		ComponentConfigFactory componentconfigfactory = getComponentConfigFactory();
		TableConfig table = componentconfigfactory.newTableConfig();
		table.setActionModel("export to xls toolbar");
		
		table.setLabel("工艺主师会签任务统计");
		table.setId("CraftworkPrincipalSingtureBuilder");
		table.setSelectable(false);
		
		ColumnConfig productSymbol = componentconfigfactory.newColumnConfig();
		productSymbol.setId("proNumber");
		productSymbol.setLabel("产品代号");
		table.addComponent(productSymbol);
		
		ColumnConfig wholeNumber = componentconfigfactory.newColumnConfig();
		wholeNumber.setId("wholeNumber");
		wholeNumber.setLabel("整件图号");
		table.addComponent(wholeNumber);
		
		ColumnConfig srPartNumber = componentconfigfactory.newColumnConfig();
		srPartNumber.setId("hardwareNumber");
		srPartNumber.setLabel("零部件图号");
		table.addComponent(srPartNumber);
		
		ColumnConfig srPartName = componentconfigfactory.newColumnConfig();
		srPartName.setId("hardwarePartName");
		srPartName.setLabel("零部件名称");
		table.addComponent(srPartName);
		
		ColumnConfig signaturer = componentconfigfactory.newColumnConfig();
		signaturer.setId("signaturer");
		signaturer.setLabel("会签者");
		table.addComponent(signaturer);
		
		ColumnConfig specialityGroup = componentconfigfactory.newColumnConfig();
		specialityGroup.setId("group");
		specialityGroup.setLabel("专业组");
		table.addComponent(specialityGroup);
		
		ColumnConfig signatureTime = componentconfigfactory.newColumnConfig();
		signatureTime.setId("signatureTime");
		signatureTime.setLabel("审签时间");
		table.addComponent(signatureTime);
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
