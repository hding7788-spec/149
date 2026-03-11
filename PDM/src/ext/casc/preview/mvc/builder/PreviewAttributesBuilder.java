package ext.casc.preview.mvc.builder;

import wt.util.WTException;

import com.ptc.jca.mvc.components.AbstractAttributesComponentBuilder;
import com.ptc.mvc.components.AttributeConfig;
import com.ptc.mvc.components.AttributePanelConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.CustomizableViewConfig;
import com.ptc.mvc.components.GroupConfig;
import com.ptc.mvc.util.ClientMessageSource;

@ComponentBuilder(value = "previewAttributes")
public class PreviewAttributesBuilder extends AbstractAttributesComponentBuilder {
    private static final String RESOURCE = "ext.casc.preview.mvc.builder.PreviewReourceRB";
    ClientMessageSource messageSource = getMessageSource(RESOURCE);

    @Override
    protected CustomizableViewConfig buildAttributesComponentConfig(ComponentParams arg0) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        AttributePanelConfig panelConfig = factory.newAttributePanelConfig();
        GroupConfig groupConfig = factory.newGroupConfig("General_");
        groupConfig.setLabel(this.messageSource.getMessage("General Attributes"));
        groupConfig.addComponent(factory.newAttributeConfig("LifeCycleState", "状态", 0, 0));
        groupConfig.addComponent(factory.newAttributeConfig("LifeCycleName", "生命周期", 1, 0));
        groupConfig.addComponent(factory.newAttributeConfig("Container", "上下文", 1, 0));
        groupConfig.addComponent(factory.newAttributeConfig("Location", "位置", 1, 1));

        groupConfig.addComponent(factory.newAttributeConfig("Creator", "创建者", 2, 0));
        groupConfig.addComponent(factory.newAttributeConfig("CreateTimestamp", "创建时间", 2, 1));
        groupConfig.addComponent(factory.newAttributeConfig("description", "说明", 3, 0));
        groupConfig.addComponent(factory.newAttributeConfig("ModifyTimestamp", "修改时间", 3, 1));

        AttributeConfig companyConfig=factory.newAttributeConfig("DesignCompany", null, 4, 0);
        companyConfig.setLabel("设计单位");
        groupConfig.addComponent(companyConfig);

        AttributeConfig designerConfig=factory.newAttributeConfig("Designer", null, 4, 1);
        designerConfig.setLabel("设计师");
        groupConfig.addComponent(designerConfig);


        panelConfig.addComponent(groupConfig);

        return panelConfig;
    }

}
