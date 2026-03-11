package com.glaway.mpm.mvc.builders.reports;

import java.util.ArrayList;
import java.util.List;



import wt.fc.QueryResult;
import wt.pdmlink.PDMLinkProduct;
import wt.util.WTException;

import com.glaway.mpm.report.helper.FrockUCBuilderHelper;
import com.glaway.mpm.util.WTContainerUtil;
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

@ComponentBuilder("com.glaway.mpm.mvc.builders.reports.FrockUsedCollectBuilder")
public class FrockUsedCollectBuilder extends AbstractConfigurableTableBuilder{

	public Object buildComponentData(ComponentConfig config, ComponentParams param) throws Exception {
		List list = new ArrayList();
		String productNumber = (String) param.getParameter("productNumber");
		System.out.println("productNumber===" + productNumber);
		productNumber = productNumber == null?"":productNumber;
		if("".equals(productNumber)){
			return list;
		}
		list = FrockUCBuilderHelper.startSearch(productNumber);
		return list;
	} 
  
	public ComponentConfig buildComponentConfig(ComponentParams param) throws WTException {
		
//		flag = flag.trim();
		ComponentConfigFactory componentconfigfactory = getComponentConfigFactory();
		TableConfig table = componentconfigfactory.newTableConfig();
		table.setActionModel("export to xls toolbar");
		
		table.setLabel("工装使用情况统计");
		table.setId("FrockUsedCollectBuilder");
		table.setSelectable(false);
		
		ColumnConfig productSymbol = componentconfigfactory.newColumnConfig();
		productSymbol.setId("proNumber");
		productSymbol.setLabel("产品代号");
		table.addComponent(productSymbol);
		
		ColumnConfig frockNum = componentconfigfactory.newColumnConfig();
		frockNum.setId("frockNum");
		frockNum.setLabel("工装图号"); 
		table.addComponent(frockNum);
		
		ColumnConfig frockName = componentconfigfactory.newColumnConfig();
		frockName.setId("frockName");
		frockName.setLabel("工装名称");
		table.addComponent(frockName);
		
		ColumnConfig  stockAmount= componentconfigfactory.newColumnConfig();
		stockAmount.setId("stockAmount");
		stockAmount.setLabel("库存数量");
		table.addComponent(stockAmount);
		
//		ColumnConfig wholeNumber = componentconfigfactory.newColumnConfig();
//		wholeNumber.setId("wholeNumber");
//		wholeNumber.setLabel("整件图号");
//		table.addComponent(wholeNumber);
		
		ColumnConfig srPartNumber = componentconfigfactory.newColumnConfig();
		srPartNumber.setId("hardwareNumber");
		srPartNumber.setLabel("零部件图号");
		table.addComponent(srPartNumber);
		
		ColumnConfig srPartName = componentconfigfactory.newColumnConfig();
		srPartName.setId("hardwarePartName");
		srPartName.setLabel("零部件名称");
		table.addComponent(srPartName);
		
		ColumnConfig workTime = componentconfigfactory.newColumnConfig();
		workTime.setId("workTime");
		workTime.setLabel("工时");
		table.addComponent(workTime);
		
		ColumnConfig materiRation = componentconfigfactory.newColumnConfig();
		materiRation.setId("materiRation");
		materiRation.setLabel("材料定额");
		table.addComponent(materiRation);
		
		ColumnConfig signalPrice = componentconfigfactory.newColumnConfig();
		signalPrice.setId("signalPrice");
		signalPrice.setLabel("单价");
		table.addComponent(signalPrice);
		
		ColumnConfig totalPrice = componentconfigfactory.newColumnConfig();
		totalPrice.setId("totalPrice");
		totalPrice.setLabel("总价");
		table.addComponent(totalPrice);
		
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
