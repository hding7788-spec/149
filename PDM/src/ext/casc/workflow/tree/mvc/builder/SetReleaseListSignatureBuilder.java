package ext.casc.workflow.tree.mvc.builder;

import java.util.ArrayList;
import java.util.List;

import wt.change2.WTChangeOrder2;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.part.WTPart;
import wt.util.WTException;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

import com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TreeConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changepackaged.ChangePackagedResultLink;
import ext.ases.envelope.EnvelopeHelper;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.constants.Constants;
import ext.casc.workflow.signtrue.zp.SignatureService;

@ComponentBuilder("ext.casc.workflow.tree.mvc.builder.SetReleaseListSignatureBuilder")
public class SetReleaseListSignatureBuilder extends AbstractComponentBuilder {
    private String activityName = "";

    @Override
    public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
        NmCommandBean cb = ((JcaComponentParams) arg1).getHelperBean().getNmCommandBean();
        WorkItem wi = (WorkItem) cb.getPageOid().getRefObject();
        Object object =wi.getPrimaryBusinessObject().getObject();
        List<Object> all = new ArrayList<Object>();
        if (object instanceof WTChangeOrder2) {
            List tempList = SignatureService.getChangeAffectItem((WTChangeOrder2) object);
            if (tempList != null && !tempList.isEmpty()) {
                for (Object object2 : tempList) {
                    if (!(object2 instanceof WTPart)) {
                        all.add(object2);
                    }
                }
            }
        } else if (object instanceof ProcessEnvelope) {
            List tempList = EnvelopeHelper.service.getAllMembers((ProcessEnvelope) object);
            if (tempList != null && !tempList.isEmpty()) {
                for (Object object2 : tempList) {
                    if (!(object2 instanceof WTPart)) {
                        all.add(object2);
                    }
                }
            }
        }else if (object instanceof ChangePackaged) {
        	ChangePackaged change = (ChangePackaged) object;
        	QueryResult qr = PersistenceHelper.manager.navigate(change,
					ChangePackagedResultLink.ROLE_BOBJECT_ROLE,
					ChangePackagedResultLink.class, true);
			while (qr.hasMoreElements()) {
				WTObject wtobject = (WTObject) qr.nextElement();
				if (!(wtobject instanceof WTPart)) {
					all.add(wtobject);
				}
			}
        }
        return all;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        NmCommandBean commandBean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
        WorkItem wi = (WorkItem) commandBean.getPageOid().getRefObject();
        WfActivity activity = (WfActivity) wi.getSource().getObject();
        WfProcess process = activity.getParentProcess();
        String pName = process.getName();
        activityName = activity.getName();
        TreeConfig treeConfig = factory.newTreeConfig();
        treeConfig.setLabel("数据列表");
        treeConfig.setComponentMode(ComponentMode.VIEW);
        treeConfig.setConfigurable(false);
        treeConfig.setExpansionLevel("full");
        treeConfig.setSelectable(true);
        treeConfig.setId("ext.casc.workflow.tree.mvc.builder.SetReleaseListSignatureBuilder");

        ColumnConfig nmActionsCol = factory.newColumnConfig(ColumnIdentifiers.NM_ACTIONS, false);
        ((JcaColumnConfig) nmActionsCol).setActionModel("custom_folderbrowser_toolbar_open_submenu");
        treeConfig.addComponent(nmActionsCol);

        ColumnConfig icon = factory.newColumnConfig("type_icon", false);
        treeConfig.addComponent(icon);

        ColumnConfig numberConfig = factory.newColumnConfig("number", false);
        numberConfig.setInfoPageLink(true);
        numberConfig.setAutoSize(true);
        treeConfig.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("name", false);
        nameConfig.setAutoSize(true);
        treeConfig.addComponent(nameConfig);

        ColumnConfig versionConfig = factory.newColumnConfig("version", false);
        versionConfig.setAutoSize(true);
        treeConfig.addComponent(versionConfig);

        ColumnConfig huiqianState = factory.newColumnConfig("updatestate", false);
        huiqianState.setLabel("更新状态");
        huiqianState.setDataUtilityId("ReleaseDataUtility");
        huiqianState.setWidth(25);
        treeConfig.addComponent(huiqianState);

        if(pName!=null && !"".equals(pName)){
        	if(pName.contains("149正式发放包流程") || pName.contains("149变更签审包工艺会签")){
        		ColumnConfig toGYY = factory.newColumnConfig("toGYY", false);
        	    toGYY.setLabel("通知工艺员");
        	    toGYY.setDataUtilityId("ReleaseDataUtility");
        	    toGYY.setAutoSize(true);
        	    treeConfig.addComponent(toGYY);
        	}
        }

        if(activityName.equals(Constants.TASK_SHEZHIFENFAFANWEIHEFENSHU)) {
        	String processName=process.getTemplate().getName();
        	 if("149正式发放包流程".equals(processName)||"149变更签审包工艺会签".equals(processName)) {
        		 ColumnConfig spceDepartment = factory.newColumnConfig("selectDepartment", false);
                 spceDepartment.setLabel("纸质分发部门");
                 spceDepartment.setDataUtilityId("ReleaseDataUtility");
                 spceDepartment.setAutoSize(true);
                 treeConfig.addComponent(spceDepartment);
                 
                 ColumnConfig spceElectronicDepartment = factory.newColumnConfig("selectElectronicDepartment", false);
                 spceElectronicDepartment.setLabel("电子分发部门");
                 spceElectronicDepartment.setDataUtilityId("ReleaseDataUtility");
                 spceElectronicDepartment.setWidth(50);
                 treeConfig.addComponent(spceElectronicDepartment);
        	} else {
        		ColumnConfig spceDepartment = factory.newColumnConfig("selectDepartment", false);
                spceDepartment.setLabel("分发部门");
                spceDepartment.setDataUtilityId("ReleaseDataUtility");
                spceDepartment.setAutoSize(true);
                treeConfig.addComponent(spceDepartment);
        	}
        }

        treeConfig.setNodeColumn("number");

        return treeConfig;
    }

}
