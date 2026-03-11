package ext.ases.changerequest.mvc.builder;

import com.ptc.jca.mvc.components.AbstractAttributesComponentBuilder;
import com.ptc.mvc.components.*;
import com.ptc.mvc.util.ClientMessageSource;
import wt.util.WTException;

@ComponentBuilder(value = "requestAttributes")
public class ChangeRequestAttributesBuilder extends AbstractAttributesComponentBuilder {
    private static final String RESOURCE = "ext.ases.changerequest.changerequestResource";
    ClientMessageSource messageSource = getMessageSource(RESOURCE);
    
    @Override
    protected CustomizableViewConfig buildAttributesComponentConfig(ComponentParams arg0) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        AttributePanelConfig panelConfig = factory.newAttributePanelConfig();
        GroupConfig groupConfig = factory.newGroupConfig("General_");
        groupConfig.setLabel(this.messageSource.getMessage("General Attributes"));
       
        groupConfig.addComponent(factory.newAttributeConfig("name", "名称", 0, 0));
        groupConfig.addComponent(factory.newAttributeConfig("number", "编号", 0, 1));
        groupConfig.addComponent(factory.newAttributeConfig("Container", "上下文", 1, 0));
        groupConfig.addComponent(factory.newAttributeConfig("LifeCycleState", "状态", 1, 1));
        groupConfig.addComponent(factory.newAttributeConfig("description", "说明", 2, 0));
        groupConfig.addComponent(factory.newAttributeConfig("remark", this.messageSource.getMessage("remark"), 2, 1));
        groupConfig.addComponent(factory.newAttributeConfig("Creator", "创建者", 3, 0));
        groupConfig.addComponent(factory.newAttributeConfig("CreateTimestamp", "创建时间", 3, 1));
        groupConfig.addComponent(factory.newAttributeConfig("ModifyTimestamp", "修改时间", 4, 0));
        groupConfig.addComponent(factory.newAttributeConfig("LifeCycleName", "生命周期", 4, 1));
        groupConfig.addComponent(factory.newAttributeConfig("avidmtype", messageSource.getMessage("avidmtype"), 5, 0));
        groupConfig.addComponent(factory.newAttributeConfig("requesttype", messageSource.getMessage("requesttype"), 5, 1));
        groupConfig.addComponent(factory.newAttributeConfig("requestproprity", messageSource.getMessage("requestproprity"), 6, 0));
        groupConfig.addComponent(factory.newAttributeConfig("template", messageSource.getMessage("template"), 6, 1));
        groupConfig.addComponent(factory.newAttributeConfig("solution", messageSource.getMessage("solution"), 7, 0));
        groupConfig.addComponent(factory.newAttributeConfig("cost", messageSource.getMessage("cost"), 7, 1));
        groupConfig.addComponent(factory.newAttributeConfig("IBA|ECRTYPE", null, 8, 0));
        groupConfig.addComponent(factory.newAttributeConfig("IBA|SECRET", null, 8, 1));
        
        panelConfig.addComponent(groupConfig);

        return panelConfig;
    }

}
