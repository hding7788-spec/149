package ext.casc.workflow.tree.mvc.builder;

import com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.util.IBAUtility;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.util.WTException;
import wt.workflow.engine.WfActivity;
import wt.workflow.work.WorkItem;

import java.util.ArrayList;

@ComponentBuilder("ext.casc.workflow.tree.mvc.builder.SetPrintDistributiontureBuilder")
public class SetPrintDistributiontureBuilder extends AbstractComponentBuilder {
    private String activityName = "";

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
		NmCommandBean commandBean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
		WorkItem workItem = (WorkItem)commandBean.getPageOid().getRefObject();
//		WfActivity activity = (WfActivity) workItem.getSource().getObject();
//        WfProcess process = activity.getParentProcess();
//        ProcessData processData = process.getContext();
		Persistable per = workItem.getPrimaryBusinessObject().getObject();
		ArrayList<Persistable> objects = new ArrayList<Persistable>();
		if(per instanceof WTDocument){
			objects.add(per);
		}else if(per instanceof WTChangeOrder2){
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) per;
			objects.add(changeOrder2);
			IBAUtility ibaUtility = new IBAUtility(changeOrder2);
			String type = ibaUtility.getIBAValue("ECNTYPE");
			if(!"作废更改".equals(type)){
				QueryResult qResult = ChangeHelper2.service.getChangeablesAfter(changeOrder2);
	            while (qResult.hasMoreElements()) {
	                Object object = qResult.nextElement();
	                if (object instanceof WTDocument) {
	                    WTDocument doc = (WTDocument) object;
	                    objects.add(doc);
	                }
	            }
			}

//			WTObject targetObj = PrintHelper.getReleatedDocByECN(changeOrder2);
//            if(targetObj != null){
//            	if(targetObj instanceof WTDocument){
//            		WTDocument document = (WTDocument)targetObj;
//            		QueryResult allIterDocs = VersionControlHelper.service.allIterationsOf(document.getMaster());
//            		if(allIterDocs.hasMoreElements()) {
//            			document = (WTDocument)allIterDocs.nextElement();
//            			objects.add(document);
//            		}
//            	}else{
//            		 objects.add(targetObj);
//            	}
//                return objects;
//            }
//			QueryResult qResult = ChangeHelper2.service.getChangeablesAfter(changeOrder2);
//            while (qResult.hasMoreElements()) {
//                Object object = qResult.nextElement();
//                if(object instanceof  Persistable){
//                	objects.add((Persistable)object);
//                }
//            }
		}
		return objects;
	}

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        NmCommandBean commandBean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
        WorkItem wi = (WorkItem) commandBean.getPageOid().getRefObject();
        WfActivity activity = (WfActivity) wi.getSource().getObject();
        activityName = activity.getName();
        TreeConfig treeConfig = factory.newTreeConfig();
        treeConfig.setLabel("打印分发信息");
        treeConfig.setComponentMode(ComponentMode.VIEW);
        treeConfig.setConfigurable(false);
        treeConfig.setExpansionLevel("full");
        treeConfig.setSelectable(true);
        treeConfig.setId("ext.casc.workflow.tree.mvc.builder.SetPrintDistributiontureBuilder");

        ColumnConfig nmActionsCol = factory.newColumnConfig(ColumnIdentifiers.NM_ACTIONS, false);
        ((JcaColumnConfig) nmActionsCol).setActionModel("custom_folderbrowser_toolbar_open_submenu");
        treeConfig.addComponent(nmActionsCol);

        ColumnConfig icon = factory.newColumnConfig("type_icon", false);
        treeConfig.addComponent(icon);

        ColumnConfig numberConfig = factory.newColumnConfig("number", false);
        numberConfig.setInfoPageLink(true);
        numberConfig.setWidth(100);
//        numberConfig.setAutoSize(true);
        treeConfig.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("name", false);
        numberConfig.setWidth(100);
//        nameConfig.setAutoSize(true);
        treeConfig.addComponent(nameConfig);

        ColumnConfig versionConfig = factory.newColumnConfig("version", false);
        numberConfig.setWidth(20);
//        versionConfig.setAutoSize(true);
        treeConfig.addComponent(versionConfig);

        ColumnConfig PHASE_CODEConfig = factory.newColumnConfig("PHASE_CODE", false);
        numberConfig.setWidth(20);
//        PHASE_CODEConfig.setAutoSize(true);
        treeConfig.addComponent(PHASE_CODEConfig);

        ColumnConfig SECRETConfig = factory.newColumnConfig("SECRET", false);
        numberConfig.setWidth(50);
//        SECRETConfig.setAutoSize(true);
        treeConfig.addComponent(SECRETConfig);

        if(activityName.equals("设置打印分发信息")) {
        	 ColumnConfig stampConfig = factory.newColumnConfig("stamp", false);
             stampConfig.setLabel("印章");
             stampConfig.setDataUtilityId("ReleaseDataUtility");
             numberConfig.setWidth(200);
//             stampConfig.setAutoSize(true);
             treeConfig.addComponent(stampConfig);

            ColumnConfig spceDepartment = factory.newColumnConfig("selectPrintDepartment", false);
            spceDepartment.setLabel("分发部门");
            spceDepartment.setDataUtilityId("ReleaseDataUtility");
            numberConfig.setWidth(200);
//            spceDepartment.setAutoSize(true);
            treeConfig.addComponent(spceDepartment);
        }
        treeConfig.setNodeColumn("number");

        return treeConfig;
    }

}
