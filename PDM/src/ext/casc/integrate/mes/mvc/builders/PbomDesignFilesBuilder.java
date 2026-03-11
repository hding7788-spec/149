package ext.casc.integrate.mes.mvc.builders;

import java.util.ArrayList;
import java.util.List;

import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.build.EPMBuildRule;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.util.WTException;

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

import ext.casc.integrate.mes.MesUtil;
import ext.casc.integrate.mes.PbomDesignFilesProcessor;
import ext.casc.report.AllWorkFlowService;
import ext.casc.util.WCUtil;

public class PbomDesignFilesBuilder extends AbstractComponentBuilder{

	@Override
	public Object buildComponentData(ComponentConfig arg0, ComponentParams params)
			throws Exception {
		NmCommandBean commandBean = ((JcaComponentParams)params).getHelperBean().getNmCommandBean();

		String processFileNumber =(String)commandBean.getRequest().getParameter("processFileNumber");
		String fileVersionType =(String)commandBean.getRequest().getParameter("fileVersionType");
		String fileVersion =(String)commandBean.getRequest().getParameter("fileVersion");

		List result = new ArrayList();
		PbomDesignFilesProcessor processor = new PbomDesignFilesProcessor();
		WTPart part = processor.getDesignPartByNumberAndVersion(processFileNumber,fileVersionType,fileVersion,"Design");

		if(part!=null){
			QueryResult qResult = WCUtil.getEPMBuildRoles(part);

			while (qResult.hasMoreElements()) {
	           Object object = qResult.nextElement();
	           if (object instanceof EPMBuildRule) {
	        	   EPMBuildRule rule = (EPMBuildRule) object;
	               Object ruleA = rule.getRoleAObject();
	               if (ruleA instanceof EPMDocument) {
	            	   EPMDocument epmDocument = (EPMDocument) ruleA;
	            	   result.add(epmDocument);
	               }
	           }
			}

			qResult = PartDocServiceCommand.getAssociatedCADDocuments(part);
			while (qResult.hasMoreElements()) {
				Object object = qResult.nextElement();
				if (object instanceof EPMDocument) {
					if(!result.contains(object)){
						result.add(object);
					}
				}
			}

			List<WTDocument> list = MesUtil.getRelateDocByPart(part);
			result.addAll(list);
		}

		return  result;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0)
			throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig tableConfig = factory.newTableConfig();
		tableConfig.setLabel("设计文件列表");
		tableConfig.setSelectable(false);

		ColumnConfig icon = factory.newColumnConfig("type_icon",false);
        tableConfig.addComponent(icon);

        ColumnConfig numberConfig = factory.newColumnConfig("number", false);
        numberConfig.setInfoPageLink(true);
        numberConfig.setAutoSize(true);
        tableConfig.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("name", false);
        nameConfig.setAutoSize(true);
        tableConfig.addComponent(nameConfig);


        ColumnConfig versionConfig = factory.newColumnConfig("version", false);
        versionConfig.setAutoSize(true);
        tableConfig.addComponent(versionConfig);


        ColumnConfig operations = factory.newColumnConfig("operations", false);
        operations.setLabel("操作");
        operations.setAutoSize(true);
        operations.setDataUtilityId("PartDataUtility");
        tableConfig.addComponent(operations);
		return tableConfig;
	}

}
