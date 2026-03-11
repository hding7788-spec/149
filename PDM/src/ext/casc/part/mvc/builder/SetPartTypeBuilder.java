package ext.casc.part.mvc.builder;

import wt.part.WTPart;
import wt.util.WTException;

import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TreeConfig;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

@ComponentBuilder("ext.casc.part.mvc.builder.SetPartTypeBuilder")
public class SetPartTypeBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
        NmCommandBean commandbean = ((JcaComponentParams)params).getHelperBean().getNmCommandBean();
        NmOid nmOid = commandbean.getPrimaryOid();
        Object object = nmOid.getRefObject();
        if (object instanceof WTPart) {
            WTPart part = (WTPart)object;
            return new SetPartTypeTreeHandler(part);
        }
        return null;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TreeConfig treeConfig = factory.newTreeConfig();
        treeConfig.setLabel("零部件列表");
        treeConfig.setComponentMode(ComponentMode.VIEW);
        treeConfig.setConfigurable(false);
        treeConfig.setId("set_part_type");
        treeConfig.setExpansionLevel("full");
        treeConfig.setSelectable(true);
        ColumnConfig icon = factory.newColumnConfig("type_icon",false);
        treeConfig.addComponent(icon);
        
        ColumnConfig numberConfig = factory.newColumnConfig("number", true);
        numberConfig.setInfoPageLink(true);
        treeConfig.addComponent(numberConfig);
        
        ColumnConfig nameConfig = factory.newColumnConfig("name", true);
        treeConfig.addComponent(nameConfig);
        
        ColumnConfig versionConfig = factory.newColumnConfig("version", false);
        treeConfig.addComponent(versionConfig);
        
        ColumnConfig stateConfig = factory.newColumnConfig("state",false);
        treeConfig.addComponent(stateConfig);
        
        ColumnConfig partType = factory.newColumnConfig("oldPartTypeValue",false);
        partType.setLabel("零部件类型");
        partType.setDataUtilityId("PartDataUtility");
        treeConfig.addComponent(partType);
        
        ColumnConfig typeConfig = factory.newColumnConfig("partType", false);
        typeConfig.setLabel("零部件分类");
        typeConfig.setDataUtilityId("PartDataUtility");
        treeConfig.addComponent(typeConfig);
        
        treeConfig.setNodeColumn("number");
        
        return treeConfig;
    }

}
