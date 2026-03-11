package ext.casc.workflow.tree.mvc.builder;

import wt.fc.ReferenceFactory;
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

import ext.casc.constants.Constants;
import ext.casc.workflow.TaskConfigrationHelper;
import ext.casc.workflow.tree.SignatureResultTreeHandler;

@ComponentBuilder("ext.casc.workflow.tree.mvc.builder.ShowTreeSignatureBuilder")
public class ShowTreeSignatureBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
        return new SignatureResultTreeHandler();
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        NmCommandBean commandbean = ((JcaComponentParams)params).getHelperBean().getNmCommandBean();
        String signOid = commandbean.getRequest().getParameter("oid");
        ReferenceFactory rf = new ReferenceFactory();
        WorkItem wi = (WorkItem) rf.getReference(signOid).getObject();
        WfActivity activity = (WfActivity) wi.getSource().getObject();
        WfProcess process = activity.getParentProcess();
        String processName = process.getName();
        String isShowSignResult = (String)TaskConfigrationHelper.getActivityVariableByVarName(signOid,"isShowSignResult");
        if(isShowSignResult == null){
            isShowSignResult = "";
        }
        TreeConfig treeConfig = factory.newTreeConfig();
        treeConfig.setLabel("签审列表");
        treeConfig.setComponentMode(ComponentMode.VIEW);
        treeConfig.setConfigurable(false);
        treeConfig.setId("show_tree_signature");
       // treeConfig.setExpansionLevel("full");
        treeConfig.setShowTreeLines(true);
        treeConfig.setActionModel("showListSignatureToolBar");
        ColumnConfig nmActionsCol = factory.newColumnConfig(ColumnIdentifiers.NM_ACTIONS, false);
        ((JcaColumnConfig) nmActionsCol).setActionModel("custom_folderbrowser_toolbar_open_submenu");
        treeConfig.addComponent(nmActionsCol);

        ColumnConfig icon = factory.newColumnConfig("type_icon",false);
        treeConfig.addComponent(icon);

        ColumnConfig numberConfig = factory.newColumnConfig("number", false);
        numberConfig.setInfoPageLink(true);
        numberConfig.setWidth(150);
        treeConfig.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("name", false);
        nameConfig.setWidth(150);
        treeConfig.addComponent(nameConfig);

        ColumnConfig versionConfig = factory.newColumnConfig("version", false);
        versionConfig.setAutoSize(true);
        treeConfig.addComponent(versionConfig);
//        if (isShowSignResult.contains("内部会签")) {
            ColumnConfig neibuhuiqian = factory.newColumnConfig("neibu_sign_result",false);
            neibuhuiqian.setLabel("内部会签");
            neibuhuiqian.setDataUtilityId("SignatureResultDataUtility");
            neibuhuiqian.setWidth(200);
            treeConfig.addComponent(neibuhuiqian);
//        }
//        if (isShowSignResult.contains("外部会签")) {
            ColumnConfig waibuhuiqian = factory.newColumnConfig("waibu_sign_result", false);
            waibuhuiqian.setLabel("外部会签");
            waibuhuiqian.setDataUtilityId("SignatureResultDataUtility");
            waibuhuiqian.setWidth(200);
            treeConfig.addComponent(waibuhuiqian);
//        }
//        if (isShowSignResult.contains("工艺会签")) {
            ColumnConfig gongyihuiqian = factory.newColumnConfig("gongyiyuan_sign_result", false);
            gongyihuiqian.setLabel("工艺会签");
            gongyihuiqian.setDataUtilityId("SignatureResultDataUtility");
            gongyihuiqian.setWidth(200);
            treeConfig.addComponent(gongyihuiqian);
//        }
         ColumnConfig biaoshen = factory.newColumnConfig("biaoshen_sign_result", false);
        biaoshen.setLabel("标审");
        biaoshen.setDataUtilityId("SignatureResultDataUtility");
        biaoshen.setWidth(200);
        treeConfig.addComponent(biaoshen);
            if (processName.indexOf(Constants.WF_PART_APPROVAL) > -1 || processName.indexOf(Constants.WF_SJ_ECN) > -1
                    || processName.indexOf(Constants.WF_149ECNPAKAGE) > -1 || processName.indexOf(Constants.WF_149APPROVAL_HUIQIAN) > -1
                    || processName.indexOf(Constants.WF_149APPROVAL_ZHENGSHI_HUIQIAN) > -1) {
                ColumnConfig cmat = factory.newColumnConfig("CMAT", false);
                cmat.setAutoSize(true);
                cmat.setLabel("材料");
                treeConfig.addComponent(cmat);

                ColumnConfig cmatup = factory.newColumnConfig("CMAT_UP", false);
                cmatup.setAutoSize(true);
                cmatup.setLabel("材料上标");
                treeConfig.addComponent(cmatup);

                ColumnConfig cmatdown = factory.newColumnConfig("CMAT_DOWN", false);
                cmatdown.setAutoSize(true);
                cmatdown.setLabel("材料下标");
                treeConfig.addComponent(cmatdown);

                ColumnConfig csize = factory.newColumnConfig("CSIZE", false);
                csize.setAutoSize(true);
                csize.setLabel("规格");
                treeConfig.addComponent(csize);

                ColumnConfig count = factory.newColumnConfig("count", false);
                count.setAutoSize(true);
                count.setLabel("数量");
                count.setDataUtilityId("SignatureResultDataUtility");
                treeConfig.addComponent(count);

                ColumnConfig designer = factory.newColumnConfig("DESIGNER", false);
                designer.setAutoSize(true);
                designer.setLabel("设计者");
                treeConfig.addComponent(designer);

                ColumnConfig ptc_material_name = factory.newColumnConfig("PTC_MATERIAL_NAME", false);
                ptc_material_name.setAutoSize(true);
                ptc_material_name.setLabel("材料名称");
                treeConfig.addComponent(ptc_material_name);
            }

            treeConfig.setNodeColumn("number");

        return treeConfig;
    }

}
