package ext.ases.envelope.mvc.builder;

import com.ptc.jca.mvc.components.AbstractAttributesComponentBuilder;
import com.ptc.mvc.components.*;
import com.ptc.mvc.util.ClientMessageSource;
import wt.util.WTException;

@ComponentBuilder(value = "attributes")
public class ProcessEnvelopeAttributesBuilder extends AbstractAttributesComponentBuilder {
    private static final String RESOURCE = "ext.ases.envelope.mvc.builder.ProcessEnvelopeReourceRB";
    ClientMessageSource messageSource = getMessageSource(RESOURCE);
    
    @Override
    protected CustomizableViewConfig buildAttributesComponentConfig(ComponentParams arg0) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        AttributePanelConfig panelConfig = factory.newAttributePanelConfig();
        GroupConfig groupConfig = factory.newGroupConfig("General_");
        groupConfig.setLabel(this.messageSource.getMessage("General Attributes"));
        
//        AttributeConfig attributeConfig = factory.newAttributeConfig("LifeCycleState", null, 0, 0);
//        attributeConfig.setLabel("生命周期状态：");
//        attributeConfig.setDataUtilityId("ProcessEnvelopeDataUtility");
        groupConfig.addComponent(factory.newAttributeConfig("LifeCycleState", "状态", 0, 0));
        groupConfig.addComponent(factory.newAttributeConfig("Container", "上下文", 1, 0));
        groupConfig.addComponent(factory.newAttributeConfig("Location", "位置", 1, 1));
        AttributeConfig creatorNameConfig=factory.newAttributeConfig("Creator", "创建者", 2, 0);
        groupConfig.addComponent(creatorNameConfig);
        groupConfig.addComponent(factory.newAttributeConfig("CreateTimestamp", "创建时间", 2, 1));
        groupConfig.addComponent(factory.newAttributeConfig("Modifier", "修改者", 3, 0));
        groupConfig.addComponent(factory.newAttributeConfig("ModifyTimestamp", "修改时间", 3, 1));
        groupConfig.addComponent(factory.newAttributeConfig("LifeCycleName", "生命周期", 4, 0));
        groupConfig.addComponent(factory.newAttributeConfig("FAWANGDANWEI", "发往单位", 4, 1));
        groupConfig.addComponent(factory.newAttributeConfig("IBA|hasAnalysis", null, 5, 0));


        panelConfig.addComponent(groupConfig);

        return panelConfig;
    }

}
