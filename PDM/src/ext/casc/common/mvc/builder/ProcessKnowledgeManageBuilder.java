package ext.casc.common.mvc.builder;


import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.mpm.GyCsServerHelper;
import ext.casc.mpm.process.GLProcessKnowledge;
import ext.casc.mpm.process.GLProcessParams;
import ext.sast.common.fc.CmPersistable;
import wt.util.WTException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ComponentBuilder("ext.casc.common.mvc.builder.ProcessKnowledgeManageBuilder")
public class ProcessKnowledgeManageBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params)
			throws Exception {
		NmCommandBean cb = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
		String[] paramNumber = (String[])cb.getParameterMap().get("oid");
		GLProcessParams  glProcessParams = GyCsServerHelper.getProcessParamDefinition(paramNumber[0]);
		Map<String,String> queryParams = new HashMap<String, String>();
		queryParams.put("SHEETNAME", glProcessParams.getGyName());
		List<CmPersistable> list = GyCsServerHelper.queryGLObjects(GLProcessKnowledge.class, queryParams);
		return list;
	}


	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params)
			throws WTException {
		String paramNumber = (String)params.getParameter("oid");
		GLProcessParams zhiShiCanShu = GyCsServerHelper.getProcessParamDefinition(paramNumber,false);
		String in = zhiShiCanShu.getKnowledgeInferencePara();
		String  out = zhiShiCanShu.getKnowledgeOutputPara();
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setActionModel("custom_processKnowledge_actions");
		table.setLabel("工艺知识列表("+zhiShiCanShu.getGyName()+")");

		table.setId("ext.casc.common.mvc.builder.ProcessKnowledgeManageBuilder");
		table.setSelectable(true);
		String[] ins = in.split("\\|");
		String[] outs  = out.split("\\|");

	/*	ColumnConfig nmActionsCol = factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.NM_ACTIONS, false);
		nmActionsCol.setActionModel("custom_processKnowledge_actions");
		table.addComponent(nmActionsCol);*/

		ColumnConfig keyId = factory.newColumnConfig("keyId", false);
		keyId.setLabel("ID");
		//keyId.setHidden(true);
		table.addComponent(keyId);

		int inNumber = 0;
		for(int i=0;i< ins.length;i++){
			ColumnConfig column = factory.newColumnConfig("column"+(i+1), false);
			column.setLabel(ins[i]+"(输入)");
			table.addComponent(column);
			inNumber++;
		}

		for(int i=0;i< outs.length;i++){
			ColumnConfig column = factory.newColumnConfig("column"+(i+1+inNumber), false);
			column.setLabel(outs[i]+"(输出)");
			table.addComponent(column);
		}



		return table;
	}

}
