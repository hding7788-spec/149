package ext.casc.workflow.tree.mvc.builder;

import java.util.ArrayList;
import java.util.List;

import wt.change2.WTChangeOrder2;
import wt.part.WTPart;
import wt.util.WTException;
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

import ext.ases.envelope.EnvelopeHelper;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.workflow.signtrue.zp.SignatureService;

@ComponentBuilder("ext.casc.workflow.tree.mvc.builder.ShowReleaseListSignatureBuilder")
public class ShowReleaseListSignatureBuilder extends AbstractComponentBuilder {

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
        }
        return all;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TreeConfig treeConfig = factory.newTreeConfig();
        treeConfig.setLabel("数据列表");
        treeConfig.setComponentMode(ComponentMode.VIEW);
        treeConfig.setConfigurable(false);
        treeConfig.setExpansionLevel("full");
        treeConfig.setSelectable(true);
        treeConfig.setId("ext.casc.workflow.tree.mvc.builder.ShowReleaseListSignatureBuilder");
        
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

        ColumnConfig spceDepartment = factory.newColumnConfig("showSelectDepartment", false);
        spceDepartment.setLabel("分发部门");
        spceDepartment.setDataUtilityId("ReleaseDataUtility");
        spceDepartment.setAutoSize(true);
        treeConfig.addComponent(spceDepartment);
        
        treeConfig.setNodeColumn("number");

        return treeConfig;
    }

}
