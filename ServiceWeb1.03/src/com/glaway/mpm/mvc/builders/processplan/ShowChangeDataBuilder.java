package com.glaway.mpm.mvc.builders.processplan;

import java.util.ArrayList;
import java.util.List;

import wt.associativity.NCServerHolder;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.workflow.work.WorkItem;

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.MPMProcessPlanHelper;

@ComponentBuilder("com.glaway.mpm.mvc.builders.processplan.ShowChangeDataBuilder")
public class ShowChangeDataBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params)
			throws Exception {
		NmCommandBean cb = ((JcaComponentParams)params).getHelperBean().getNmCommandBean();
		WorkItem workItem = (WorkItem)cb.getPageOid().getRefObject();
		Object obj = workItem.getPrimaryBusinessObject().getObject();
		List<Object> list = new ArrayList<Object>();
		if(obj instanceof WTChangeOrder2) {
			WTChangeOrder2 ecn = (WTChangeOrder2)obj;
			QueryResult qResult = ChangeHelper2.service.getChangeablesBefore(ecn);
            while (qResult.hasMoreElements()) {
            	Object object = qResult.nextElement();
            	if (object instanceof MPMProcessPlan) {
            		MPMProcessPlan pplan = (MPMProcessPlan)object;
            		list.add(pplan);
            		String number = pplan.getNumber();
        			QueryResult qr = MPMProcessPlanHelper.service.getWTParts(pplan, NCServerHolder.makeForLatestConfigSpec());
        			if(qr.hasMoreElements()) {
        				WTPart part = (WTPart)qr.nextElement();
        				QueryResult allIter = VersionControlHelper.service.allIterationsOf(part.getMaster());
        				while(allIter.hasMoreElements()) {
        					part = (WTPart)allIter.nextElement();
        					QueryResult qr2 = WTPartHelper.service.getDescribedByWTDocuments(part, true);
        			        while (qr2.hasMoreElements()) {
        			            WTDocument document = (WTDocument) qr2.nextElement();
        			            String typeName = TypedUtility.getTypeIdentifier(document).getTypename();
        			            if (typeName.contains("ASSEMBLE_PROCESSPLAN")
        			                    ||typeName.contains("MOUNT_PROCESSPLAN")
        			                    ||typeName.contains("MACHINING_PROCESSPLAN")
        			                    ||typeName.contains("PAINT_PROCESSPLAN")) {
        			            	String docNumber = document.getNumber();
        			            	if(docNumber.equals(number)) {
        			            		list.add(part);
        			            		list.add(document);
        			            	}
        			            }
        			        }
        				}
        			}
            	}
            }
		}
		return list;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params)
			throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig table = factory.newTableConfig();
        table.setLabel("工艺变更相关数据");

        ColumnConfig icon = factory.newColumnConfig("type_icon", false);
        table.addComponent(icon);

        ColumnConfig numberConfig = factory.newColumnConfig("number", false);
        numberConfig.setInfoPageLink(true);
        numberConfig.setAutoSize(true);
        table.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("name", false);
        nameConfig.setAutoSize(true);
        table.addComponent(nameConfig);

        ColumnConfig versionConfig = factory.newColumnConfig("version", false);
        versionConfig.setAutoSize(true);
        table.addComponent(versionConfig);

		return table;
	}

}
