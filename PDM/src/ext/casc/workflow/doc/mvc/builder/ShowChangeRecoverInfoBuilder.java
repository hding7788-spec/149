package ext.casc.workflow.doc.mvc.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import wt.change2.ChangeHelper2;
import wt.change2.Changeable2;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTKeyedMap;
import wt.part.WTPart;
import wt.util.WTException;
import wt.workflow.engine.WfActivity;
import wt.workflow.work.WorkItem;

import com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;
import com.ptc.windchill.mpml.MPMDocumentHelper;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;

@ComponentBuilder("ext.casc.workflow.doc.mvc.builder.showChangeRecoverInfoBuilder")

public class ShowChangeRecoverInfoBuilder extends AbstractComponentBuilder{

	@Override
	public Object buildComponentData(ComponentConfig componentconfig, ComponentParams componentparams) throws Exception {
		NmCommandBean commandBean = ((JcaComponentParams) componentparams).getHelperBean().getNmCommandBean();
		WorkItem workItem = (WorkItem)commandBean.getPageOid().getRefObject();
		Persistable per = workItem.getPrimaryBusinessObject().getObject();
		ArrayList<Persistable> objects = new ArrayList<Persistable>();
		if(per instanceof WTChangeOrder2){
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) per;
			QueryResult qResult = ChangeHelper2.service.getChangeablesBefore(changeOrder2);
            while (qResult.hasMoreElements()) {
                Object object = qResult.nextElement();
                if(object instanceof  MPMProcessPlan){
                	 WTArrayList localWTArrayList = new WTArrayList();
                     localWTArrayList.add(object);
                	 WTKeyedMap localWTKeyedMap = MPMDocumentHelper.service.getAssociatedDescribedByDocuments(localWTArrayList);
                	 if(localWTKeyedMap.get(object)!=null){
                		objects.addAll((Collection) localWTKeyedMap.get(object));
                		WTHashSet set = (WTHashSet)localWTKeyedMap.get(object);
                		Iterator<Persistable> iterator = set.iterator();
                		if(iterator.hasNext()){
                			WTDocument doc = (WTDocument) ((ObjectReference) iterator.next()).getObject();
                			WTCollection collection = RelatedChangesQueryCommands.getRelatedResultingChangeNotices((Changeable2) doc);
      	                	Iterator<Persistable> iterator1 = collection.iterator();
      	                	if(iterator1.hasNext()){
      	            			WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) iterator1.next()).getObject();
      	            			objects.add(ecn);
      	                	}
                		}
                	 }

                    	//	Object obj  = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(mpm);
//                    		Object obj  = RelatedChangesQueryCommands.getRelatedAffectingChangeNotices(mpm);

//                	for (int i = 0; i < collection.size(); i++) {
//                		collection.iterator();
//					}
//					System.out.println("lkc>>>>>>>>>>>>>>>>>" + obj);
//					if(qr.hasMoreElements()){
//						WTChangeOrder2 ecn = (WTChangeOrder2) qr.nextElement();
//						System.out.println(ecn);
//						System.out.println("lkc>>>>>>>>>>>>>>>>>" + ecn);
//						objects.add(ecn);
//					}
//					if (obj. instanceof WTChangeOrder2) {
//						//System.out.println("lkc>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//						WTChangeOrder2 ecn = (WTChangeOrder2) obj;
//						objects.add(ecn);
//
//					}
                }
            }
		}
//		else if(per instanceof WTDocument){
//			objects.add(per);
//		}
		return objects;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams componentparams) throws WTException {
		NmCommandBean commandBean = ((JcaComponentParams) componentparams).getHelperBean().getNmCommandBean();
		WorkItem workItem = (WorkItem)commandBean.getPageOid().getRefObject();
		WfActivity activity = (WfActivity) workItem.getSource().getObject();
		String activityName = activity.getName();
		ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig table = factory.newTableConfig();
        table.setLabel("更改回收文件");

        ColumnConfig nmActionsCol = factory.newColumnConfig(ColumnIdentifiers.NM_ACTIONS, false);
        ((JcaColumnConfig) nmActionsCol).setActionModel("custom_process_task_objTable_actions");
        table.addComponent(nmActionsCol);

        ColumnConfig icon = factory.newColumnConfig("type_icon", false);
        table.addComponent(icon);

        ColumnConfig numberConfig = factory.newColumnConfig("number", false);
        numberConfig.setInfoPageLink(true);
        numberConfig.setWidth(100);
        table.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("name", false);
        nameConfig.setWidth(150);
        table.addComponent(nameConfig);

        ColumnConfig versionConfig = factory.newColumnConfig("version", false);
        versionConfig.setWidth(50);
        table.addComponent(versionConfig);

        ColumnConfig phaseCodeConfig = factory.newColumnConfig("phaseCode", false);
        phaseCodeConfig.setLabel("阶段标记");
        phaseCodeConfig.setDataUtilityId("MpmplanReleaseDataUtility");
        phaseCodeConfig.setWidth(50);
        table.addComponent(phaseCodeConfig);

        ColumnConfig secretConfig = factory.newColumnConfig("secret", false);
        secretConfig.setLabel("密级");
        secretConfig.setDataUtilityId("MpmplanReleaseDataUtility");
        secretConfig.setWidth(50);
        table.addComponent(secretConfig);

        ColumnConfig distributeRecordConfig = factory.newColumnConfig("distributeRecord", false);
        distributeRecordConfig.setLabel("分发记录");
        distributeRecordConfig.setDataUtilityId("MpmplanReleaseDataUtility");
        distributeRecordConfig.setWidth(150);
        table.addComponent(distributeRecordConfig);

        ColumnConfig printStatusConfig = factory.newColumnConfig("printStatus", false);
        printStatusConfig.setLabel("打印状态");
        printStatusConfig.setDataUtilityId("MpmplanReleaseDataUtility");
        printStatusConfig.setWidth(80);
        table.addComponent(printStatusConfig);

		return table;
	}

}
