package ext.casc.common.mvc.builder;


import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.mpm.GyCsServerHelper;
import ext.casc.mpm.process.GLProcessParams;
import ext.casc.util.Tools;
import ext.sast.common.fc.CmQuerySpec;
import ext.sast.common.fc.CommonQueryParams;
import wt.util.WTException;

import java.util.ArrayList;
import java.util.List;


@ComponentBuilder("ext.casc.common.mvc.builder.SelectProcessParamsBuilder")
public class SelectProcessParamsBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params)
			throws Exception {
		NmCommandBean cb = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
		String gyName = (String) cb.getText().get("gyName");
		String parameterCategory = (String) cb.getText().get("parameterCategory");
		String processCategory = (String) cb.getText().get("processCategory");

		List<GLProcessParams> resultList = null;
		if(Tools.isTrimNull(gyName)&&Tools.isTrimNull(parameterCategory)&&Tools.isTrimNull(processCategory)){
			List<CommonQueryParams> queryParams = new ArrayList<CommonQueryParams>();
			CommonQueryParams param = new CommonQueryParams("parameterCategory", CmQuerySpec.EQUAL,"基础参数");
			queryParams.add(param);
			resultList   = GyCsServerHelper.queryAllProcessParams(queryParams);
		}else{
			List<CommonQueryParams> queryParams = new ArrayList<CommonQueryParams>();
			if(!Tools.isTrimNull(gyName)){
				CommonQueryParams param = new CommonQueryParams("gyName", CmQuerySpec.LIKE,"%"+gyName+"%");
				queryParams.add(param);
			}
			if(!Tools.isTrimNull(parameterCategory)){
				CommonQueryParams param = new CommonQueryParams("parameterCategory", CmQuerySpec.EQUAL,parameterCategory);
				queryParams.add(param);
			}
			if(!Tools.isTrimNull(processCategory)){
				CommonQueryParams param = new CommonQueryParams("processCategory", CmQuerySpec.EQUAL,processCategory);
				queryParams.add(param);
			}

			resultList   = GyCsServerHelper.queryAllProcessParams(queryParams);

		}
		return resultList;
	}


	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params)
			throws WTException {

		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setLabel("工艺参数列表");
		table.setId("ext.casc.common.mvc.builder.SelectProcessParamsBuilder");
		table.setSelectable(true);

		ColumnConfig gyNumber = factory.newColumnConfig("gyNumber", false);
		gyNumber.setSortable(true);
		gyNumber.setLabel("工艺参数编号");
		table.addComponent(gyNumber);

		ColumnConfig gyName = factory.newColumnConfig("gyName", false);
		gyName.setWidth(100);
		gyName.setLabel("工艺参数名称");
		gyName.setSortable(true);
		table.addComponent(gyName);


		ColumnConfig parameterCategory = factory.newColumnConfig("parameterCategory", false);
		parameterCategory.setLabel("参数类别");
		parameterCategory.setWidth(150);
		table.addComponent(parameterCategory);

		ColumnConfig unit = factory.newColumnConfig("unit", false);
		unit.setLabel("默认单位");
		unit.setWidth(50);
		table.addComponent(unit);

		ColumnConfig processCategory = factory.newColumnConfig("processCategory", false);
		processCategory.setLabel("专业");
		processCategory.setWidth(100);
		table.addComponent(processCategory);

		ColumnConfig enumValues = factory.newColumnConfig("enumValues",true);
		enumValues.setLabel("合法值列表");
		enumValues.setWidth(100);
		table.addComponent(enumValues);



		return table;
	}

}
