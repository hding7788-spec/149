package ext.casc.workflow.tree.mvc.builder;

import java.util.Map;

import wt.change2.WTChangeOrder2;
import wt.fc.ReferenceFactory;
import wt.util.WTException;
import wt.workflow.engine.WfActivity;
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
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.constants.Constants;
import ext.casc.workflow.signtrue.zp.SignatureService;

@ComponentBuilder("ext.casc.workflow.tree.mvc.builder.ECNChangeBeforeBuilder")
public class ECNChangeBeforeBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1)
			throws Exception {
		NmCommandBean cb = ((JcaComponentParams) arg1)
        .getHelperBean().getNmCommandBean();
		Map map = cb.getRequestData().getParameterMap();
		Object oidO = map.get("oid");				//读取任务页面的流程oid
		String oid = "";
		if(oidO instanceof String[]){
			oid = ((String[])oidO)[0];
		}else{
			oid = oidO.toString();
		}
		ReferenceFactory rf = new ReferenceFactory();
		WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
		WfActivity wfAct = (WfActivity) wi.getSource().getObject();
		WTChangeOrder2 ecn = (WTChangeOrder2) wfAct.getContext().getValue("primaryBusinessObject");
		return SignatureService.getChangeAffectItem(ecn);
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params)
			throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig tableConfig = factory.newTableConfig();
		tableConfig.setLabel("受影响前对象");
		tableConfig.setComponentMode(ComponentMode.VIEW);
		tableConfig.setConfigurable(false);
		tableConfig.setId("ext.casc.workflow.tree.mvc.builder.ECNChangeBeforeBuilder");
		tableConfig.setSelectable(true);

        ColumnConfig nmActionsCol = factory.newColumnConfig(ColumnIdentifiers.NM_ACTIONS, false);
        ((JcaColumnConfig) nmActionsCol).setActionModel("custom_folderbrowser_toolbar_open_submenu");
        tableConfig.addComponent(nmActionsCol);

        ColumnConfig icon = factory.newColumnConfig("type_icon",false);
        tableConfig.addComponent(icon);

        ColumnConfig numberConfig = factory.newColumnConfig("number", false);
        numberConfig.setInfoPageLink(true);
        numberConfig.setWidth(150);
        tableConfig.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("name", false);
        nameConfig.setWidth(150);
        tableConfig.addComponent(nameConfig);

        ColumnConfig versionConfig = factory.newColumnConfig("version", false);
        versionConfig.setAutoSize(true);
        tableConfig.addComponent(versionConfig);

        ColumnConfig cmat = factory.newColumnConfig("CMAT", false);
        cmat.setAutoSize(true);
        cmat.setLabel("材料");
        tableConfig.addComponent(cmat);

        ColumnConfig cmatup = factory.newColumnConfig("CMAT_UP", false);
        cmatup.setAutoSize(true);
        cmatup.setLabel("材料上标");
        tableConfig.addComponent(cmatup);

        ColumnConfig cmatdown = factory.newColumnConfig("CMAT_DOWN", false);
        cmatdown.setAutoSize(true);
        cmatdown.setLabel("材料下标");
        tableConfig.addComponent(cmatdown);

        ColumnConfig csize = factory.newColumnConfig("CSIZE", false);
        csize.setAutoSize(true);
        csize.setLabel("规格");
        tableConfig.addComponent(csize);

        ColumnConfig count = factory.newColumnConfig("count", false);
        count.setAutoSize(true);
        count.setLabel("数量");
        count.setDataUtilityId("SignatureResultDataUtility");
        tableConfig.addComponent(count);

        ColumnConfig designer = factory.newColumnConfig("DESIGNER", false);
        designer.setAutoSize(true);
        designer.setLabel("设计者");
        tableConfig.addComponent(designer);

        ColumnConfig ptc_material_name = factory.newColumnConfig("PTC_MATERIAL_NAME", false);
        ptc_material_name.setAutoSize(true);
        ptc_material_name.setLabel("材料名称");
        tableConfig.addComponent(ptc_material_name);


		return tableConfig;
	}

}
