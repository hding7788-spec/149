package ext.casc.workflow.doc.mvc.builder;

import com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers;
import com.ptc.core.meta.common.impl.TypeIdentifierUtilityHelper;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.MPMDocumentHelper;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTKeyedMap;
import wt.part.WTPart;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

import java.util.ArrayList;
import java.util.Collection;

@ComponentBuilder("ext.casc.workflow.doc.mvc.builder.ShowReadObjBuilder")
public class ShowReadObjBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params)
			throws Exception {
		NmCommandBean commandBean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
		WorkItem workItem = (WorkItem)commandBean.getPageOid().getRefObject();
//		WfActivity activity = (WfActivity) workItem.getSource().getObject();
//        WfProcess process = activity.getParentProcess();
//        ProcessData processData = process.getContext();
		Persistable per = workItem.getPrimaryBusinessObject().getObject();
		ArrayList<Persistable> objects = new ArrayList<Persistable>();
		if(per instanceof WTChangeOrder2){
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) per;
			QueryResult qResult = ChangeHelper2.service.getChangeablesBefore(changeOrder2);
            while (qResult.hasMoreElements()) {
                Object object = qResult.nextElement();
                if(object instanceof  MPMProcessPlan){
                	//objects.add((Persistable)object);
                	 WTArrayList localWTArrayList = new WTArrayList();
                     localWTArrayList.add(object);
                	 WTKeyedMap localWTKeyedMap = MPMDocumentHelper.service.getAssociatedDescribedByDocuments(localWTArrayList);
                	 if(localWTKeyedMap.get(object)!=null){
                		objects.addAll((Collection) localWTKeyedMap.get(object));
                	 }
                }else if(object instanceof WTPart){
                	WTPart part = (WTPart) object;
                	objects.add(part);
                } else if(object instanceof WTDocument) {
					String objectType = TypeIdentifierUtilityHelper.service.getTypeIdentifier(changeOrder2).toString();
					//文档更改单
					if(objectType.indexOf("casc.sast.149.DOCUMENT_ECN") > -1){
						WTDocument document = (WTDocument) object;
						objects.add(document);
					}
				}
			}
		}else if(per instanceof WTDocument){
			objects.add(per);
		}
		return objects;

	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params)
			throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig table = factory.newTableConfig();
        table.setLabel("更改前数据");

        ColumnConfig nmActionsCol = factory.newColumnConfig(ColumnIdentifiers.NM_ACTIONS, false);
        ((JcaColumnConfig) nmActionsCol).setActionModel("custom_process_task_objTable_actions");
        table.addComponent(nmActionsCol);

        ColumnConfig icon = factory.newColumnConfig("type_icon", false);
        table.addComponent(icon);

        ColumnConfig numberConfig = factory.newColumnConfig("number", false);
        numberConfig.setInfoPageLink(true);
        numberConfig.setWidth(100);
//        numberConfig.setAutoSize(true);
        table.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("name", false);
//        nameConfig.setAutoSize(true);
        nameConfig.setWidth(150);
        table.addComponent(nameConfig);

        ColumnConfig versionConfig = factory.newColumnConfig("version", false);
//        versionConfig.setAutoSize(true);
        versionConfig.setWidth(50);
        table.addComponent(versionConfig);

		return table;
	}

}
