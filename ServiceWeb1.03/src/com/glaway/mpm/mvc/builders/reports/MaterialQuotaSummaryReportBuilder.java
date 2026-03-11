package com.glaway.mpm.mvc.builders.reports;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;


import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import wt.clients.replication.unit.WTPartHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.query.ClassAttribute;
import wt.query.CompositeWhereExpression;
import wt.query.LogicalOperator;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;

import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.MPMProcessPlanUtil;
import com.ptc.core.htmlcomp.components.AbstractConfigurableTableBuilder;
import com.ptc.core.htmlcomp.tableview.ConfigurableTable;
import com.ptc.jca.mvc.components.JcaComponentConfig;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.processplan.MPMPartToProcessPlanLink;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationToConsumableLink;
import com.ptc.windchill.mpml.resource.MPMProcessMaterial;

@ComponentBuilder("com.glaway.mpm.mvc.builders.reports.MaterialQuotaSummaryReportBuilder")
public class MaterialQuotaSummaryReportBuilder extends AbstractConfigurableTableBuilder  {
	
	public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
		
		NmCommandBean bean = ((JcaComponentParams)arg1).getHelperBean().getNmCommandBean();
		HttpSession session = bean.getRequest().getSession();
		String filePath = (String)session.getAttribute("xlspath");
		System.out.println("filePath====" + filePath);
		session.removeAttribute("xlspath");
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		List<String> partNumList = new ArrayList<String>();
		String partNumber = "";
		if(filePath != null && !"".equals(filePath)){
			File file = new File(filePath);
			HSSFWorkbook book = new HSSFWorkbook(new FileInputStream(file));
			Sheet sheet = book.getSheetAt(0);
			System.out.println("sheet------" + sheet); 
			int lastRow = sheet.getLastRowNum();
			System.out.println("lastRow----" + lastRow); 
			for(int i = 0;i <= lastRow;i ++){
				Row row = sheet.getRow(i);
				partNumber = getValue(row,0);
				partNumber = partNumber.trim();
				if(!partNumList.contains(partNumber)){
					partNumList.add(partNumber);
					System.out.println("partNumber----" + partNumber); 
				}
			}
		}else{
			partNumber = (String) arg1.getParameter("partNumber");
			if(!partNumList.contains(partNumber)){
				partNumList.add(partNumber);
			}
		}

				
		partNumber = "";
		 for(int i = 0;i <partNumList.size();i ++){
			 partNumber = partNumList.get(i);
			if(partNumber == null || "".equals(partNumber)){
				return list;
			}
			WTPart[] parts = WTPartHelper.findPartByNumber(partNumber);
			System.out.println("parts.size----" + parts.length);
			WTPart part = parts.length >= 1 ? parts[0] : null;
			System.out.println("====start search parts===="); 
			if(part == null ){				
				return list;
			}
			
			System.out.println("===the part is not null==="); 
			QuerySpec qs = new QuerySpec();
			int processPlanIndex = qs.appendClassList(MPMProcessPlan.class, true);
			int partIndex = qs.appendClassList(WTPart.class, false);
			int linkIndex = qs.appendClassList(MPMPartToProcessPlanLink.class, false);
			String[] aliases = new String[3];
			aliases[0] = qs.getFromClause().getAliasAt(processPlanIndex); 
			aliases[1] = qs.getFromClause().getAliasAt(partIndex);
			aliases[2] = qs.getFromClause().getAliasAt(linkIndex);
			
			ClassAttribute mpmProcessPlanId = new ClassAttribute(MPMProcessPlan.class, "thePersistInfo.theObjectIdentifier.id",aliases[0]);
			ClassAttribute link_ProcessPlanId = new ClassAttribute(MPMPartToProcessPlanLink.class, "roleBObjectRef.key.id", aliases[2]);
			ClassAttribute link_PartId = new ClassAttribute(MPMPartToProcessPlanLink.class, "roleAObjectRef.key.id", aliases[2]);
			ClassAttribute part_PartId = new ClassAttribute(WTPart.class, "thePersistInfo.theObjectIdentifier.id", aliases[1]);

			qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, partNumber),new int[]{partIndex});
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(mpmProcessPlanId, SearchCondition.EQUAL, link_ProcessPlanId, processPlanIndex, linkIndex));
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(link_PartId, SearchCondition.EQUAL, part_PartId, linkIndex, partIndex));
			
//			CompositeWhereExpression andExpression = new CompositeWhereExpression(LogicalOperator.AND);
//	        andExpression.append(new SearchCondition(mpmProcessPlanId, SearchCondition.EQUAL, link_ProcessPlanId, processPlanIndex, linkIndex));
//	        andExpression.append(new SearchCondition(link_PartId, SearchCondition.EQUAL, part_PartId, linkIndex, partIndex));
//	        
//	        qs.appendWhere(andExpression, null);
	        System.out.println(qs);
	        QueryResult processPlanResult = PersistenceHelper.manager.find(qs);
	      System.out.println("processPlanResult.size()-----" + processPlanResult.size()); 
	        while(processPlanResult.hasMoreElements()){
	        	  System.out.println("====find the result =====" + processPlanResult.size()); 
	        	MPMProcessPlan processPlan = (MPMProcessPlan)((Persistable[])processPlanResult.nextElement())[0];
	        	QueryResult queryResult = MPMProcessPlanUtil.getChildMPMOperation(processPlan);
				while (queryResult.hasMoreElements()) {
					MPMOperation operation = (MPMOperation) ((Persistable[]) queryResult.nextElement())[1];
					System.out.println(operation.getName());
					for (Persistable[] p : getMPMOperationToConsumable(operation)) {
						System.out.println(p[0] + "--1-" + ((MPMProcessMaterial) p[1]).getName());
						list.add(setMap(processPlan, operation, null, p, partNumber));
					}
					QueryResult qr = MPMProcessPlanUtil.getChildMPMOperation(operation);
					while (qr.hasMoreElements()) {
						MPMOperation subOperation = (MPMOperation) ((Persistable[]) qr.nextElement())[1];
						System.out.println(subOperation.getName());
						for (Persistable[] p : getMPMOperationToConsumable(subOperation)) {
							System.out.println(p[0] + "-2--" + ((MPMProcessMaterial) p[1]).getName());
							list.add(setMap(processPlan, operation, subOperation, p, partNumber));
						}
					}
				}
	        }
	       
		 }
		return list;
	}

	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
		ComponentConfigFactory componentconfigfactory = getComponentConfigFactory();

		TableConfig table = componentconfigfactory.newTableConfig();
		
		table.setLabel("材料定额报表");
		table.setActionModel("export to xls toolbar");
		
		table.setSelectable(false);
		table.setId("materialQuotaSummaryReport");
		
		ColumnConfig partNumber = componentconfigfactory.newColumnConfig();
		partNumber.setId("PartNumber");
		partNumber.setLabel("零件编号");
		//partNumber.setDataUtilityId("partNumber");
		table.addComponent(partNumber);

		ColumnConfig processPlanName = componentconfigfactory.newColumnConfig();
		processPlanName.setId("ProcessPlanName");
		processPlanName.setLabel("工艺名称");
		//processPlanName.setDataUtilityId("CreateMPMResourceDatautility");
		table.addComponent(processPlanName);

		ColumnConfig operationName = componentconfigfactory.newColumnConfig();
		operationName.setId("OperationName");
		operationName.setLabel("工序名称");
		table.addComponent(operationName);

		ColumnConfig subOperationName = componentconfigfactory.newColumnConfig();
		subOperationName.setId("SubOperationName");
		subOperationName.setLabel("工步号");
		table.addComponent(subOperationName);

		ColumnConfig materialName = componentconfigfactory.newColumnConfig();
		materialName.setId("MaterialName");
		materialName.setLabel("材料名称");
		table.addComponent(materialName);

		ColumnConfig materialQuota = componentconfigfactory.newColumnConfig();
		materialQuota.setId("MaterialQuota");
		materialQuota.setLabel("材料定额");
		table.addComponent(materialQuota);
		

		return table;
	}

	private static Map<String, String> setMap(MPMProcessPlan processPlan, MPMOperation operation,
			MPMOperation subOperation, Persistable[] p, String partNumber) throws WTException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("partNumber", partNumber);
		map.put("ProcessPlanName", processPlan.getName());
		map.put("OperationName", operation.getName());
		if (subOperation != null) {
			map.put("SubOperationName", subOperation.getName());
		}
		map.put("MaterialName", ((MPMProcessMaterial) p[1]).getName());
		IBAHelper ibaHelper = new IBAHelper((MPMOperationToConsumableLink) p[0]);
		map.put("MaterialQuota", ibaHelper.getIBAValue("materialQuota"));

		return map;
	}

	private static List<Persistable[]> getMPMOperationToConsumable(MPMOperation operation) throws WTException {
		List<Persistable[]> list = new ArrayList<Persistable[]>();
		QueryResult queryResult = MPMProcessPlanUtil.getMPMOperationToConsumableLink(operation);

		while (queryResult.hasMoreElements()) {

			Persistable[] p = (Persistable[]) queryResult.nextElement();
			if (p[1] instanceof MPMProcessMaterial) {
				list.add(p);
			}
		}
		return list;
	}

	public ConfigurableTable buildConfigurableTable(String arg0)
			throws WTException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 获得cell中的值
	 * @author liuzhaogang
	 * @date 2013-7-6
	 * @param row 准备取之row
	 * @param index  用于指定cell
	 * @return cell 值
	 */
	public static String getValue(Row row, int index){
		Cell cell = row.getCell(index);
		if(cell == null){
			return "";
		}
		String value = "";
		int flag = cell.getCellType();
		
		switch (flag) {
			case Cell.CELL_TYPE_STRING:
			    value = cell.getStringCellValue();			    
				break;
			case Cell.CELL_TYPE_NUMERIC:	
				if(HSSFDateUtil.isCellDateFormatted(cell)){
					value  = cell.getDateCellValue().toString();  
					
				}else{
					value = String.valueOf((long)(cell.getNumericCellValue())).toString();
				}
				break;
			
			case Cell.CELL_TYPE_BOOLEAN:
				value = String.valueOf(cell.getBooleanCellValue()).toString();				
				break;
	
			default:				
				break;				
		}
		return value;
	}
}
