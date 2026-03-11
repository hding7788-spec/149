package ext.casc.workflow.tree.mvc.builder;

import java.util.Map;

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
import ext.casc.workflow.signtrue.zp.HuiQianWorkFlowService;
import ext.casc.workflow.tree.SignatureTreeHandler1;
import ext.casc.workflow.tree.SignatureTreeHandler2;

@ComponentBuilder("ext.casc.workflow.tree.mvc.builder.SetTreeSignatrueBuilder")
public class SetTreeSignatrueBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
    	NmCommandBean commandBean = ((JcaComponentParams) arg1).getHelperBean().getNmCommandBean();
		Map map = commandBean.getRequestData().getParameterMap();
		String oid = (String)map.get("oid");//读取任务页面的流程oid
		String activityName = HuiQianWorkFlowService.getActivityNameByWorkItemOid(oid);
		return new SignatureTreeHandler1(activityName);
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        NmCommandBean commandBean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
		Map map = commandBean.getRequestData().getParameterMap();
		String oid = (String)map.get("oid");//读取任务页面的流程oid
		ReferenceFactory rf = new ReferenceFactory();
        WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
        WfActivity activity = (WfActivity) wi.getSource().getObject();
        WfProcess process = activity.getParentProcess();
        String processName = process.getName();
		String activityName = HuiQianWorkFlowService.getActivityNameByWorkItemOid(oid);

        TreeConfig treeConfig = factory.newTreeConfig();
        treeConfig.setLabel("签审列表");
        treeConfig.setComponentMode(ComponentMode.VIEW);
        treeConfig.setConfigurable(false);
        treeConfig.setId("set_tree_signature");
        treeConfig.setSelectable(true);
        treeConfig.setExpansionLevel("full");
        treeConfig.setShowTreeLines(true);
        treeConfig.setActionModel("workItem_table_action");

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

        ColumnConfig huiqianresult = factory.newColumnConfig("sign_result",false);
        huiqianresult.setLabel("会签结论");
        huiqianresult.setDataUtilityId("SignatureTreeDataUtility1");
        huiqianresult.setWidth(50);
        treeConfig.addComponent(huiqianresult);

        ColumnConfig huiqianadvise = factory.newColumnConfig("sign_advise", false);
        huiqianadvise.setLabel("会签意见");
        huiqianadvise.setDataUtilityId("SignatureTreeDataUtility1");
        huiqianadvise.setWidth(200);
        treeConfig.addComponent(huiqianadvise);

        if (activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG)||activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN)) {
	        ColumnConfig columnConfig = factory.newColumnConfig("zhuzhichejian",false);
	        columnConfig.setLabel("*主制车间");
	        columnConfig.setId("zhuzhichejian");
	        //columnConfig.setInputFieldType("text");
	        //columnConfig.setDefaultFreeze(false);
	        columnConfig.setDataUtilityId("SignatureEpmsDataUtility");
	        columnConfig.setWidth(50);
	        treeConfig.addComponent(columnConfig);

	        ColumnConfig columnConfig2 = factory.newColumnConfig("fuzhuchejian",false);
	        columnConfig2.setLabel("辅制车间");
	        columnConfig2.setId("fuzhichejian");
	        columnConfig2.setDataUtilityId("SignatureEpmsDataUtility");
	        //columnConfig2.setInputFieldType("ComboBox");
	        columnConfig2.setWidth(200);
	        treeConfig.addComponent(columnConfig2);
        }
        treeConfig.setNodeColumn("number");

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

        return treeConfig;
    }

}
