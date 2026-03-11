package ext.casc.workflow.tree.mvc.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import wt.doc.WTDocument;
import wt.log4j.LogR;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

import com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TreeConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;


public class PrintDataListBuilder  extends AbstractComponentBuilder {
	private static final Logger log;
    static {
        try {
            log = LogR.getLogger(PrintDataListBuilder.class.getName());
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
  }
	@Override
    public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
        NmCommandBean cb = ((JcaComponentParams) arg1).getHelperBean().getNmCommandBean();
        WorkItem wi = (WorkItem) cb.getPageOid().getRefObject();
        Object object = wi.getPrimaryBusinessObject().getObject();
        if(object instanceof WTDocument){
        	System.out.println("---------------WTDocument--------------");
        	List list = new ArrayList();
        	WTDocument doc = (WTDocument) object;
        	list.add(doc);
        	return list;
        }
        return null;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TreeConfig treeConfig = factory.newTreeConfig();
        treeConfig.setLabel("数据列表");
        treeConfig.setComponentMode(ComponentMode.VIEW);
        treeConfig.setConfigurable(false);
        treeConfig.setExpansionLevel("full");
        treeConfig.setId("ext.casc.workflow.tree.mvc.builder.PrintDataListBuilder");

        ColumnConfig nmActionsCol = factory.newColumnConfig(ColumnIdentifiers.NM_ACTIONS, false);
        ((JcaColumnConfig) nmActionsCol).setActionModel("custom_printApply");
        treeConfig.addComponent(nmActionsCol);

        ColumnConfig icon = factory.newColumnConfig("type_icon",false);
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

        ColumnConfig huiqianState = factory.newColumnConfig("setCount",false);
        huiqianState.setLabel("分发份数");
        huiqianState.setDataUtilityId("SignatureTreeDataUtility1");
        huiqianState.setAutoSize(true);
        treeConfig.addComponent(huiqianState);

        ColumnConfig spceDepartment = factory.newColumnConfig("setDept",false);
        spceDepartment.setLabel("分发部门");
        spceDepartment.setDataUtilityId("SignatureTreeDataUtility1");
        spceDepartment.setAutoSize(true);
        treeConfig.addComponent(spceDepartment);

        treeConfig.setNodeColumn("number");

        return treeConfig;
    }


}
