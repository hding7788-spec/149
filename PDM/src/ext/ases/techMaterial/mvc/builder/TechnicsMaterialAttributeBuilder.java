package ext.ases.techMaterial.mvc.builder;

import wt.util.WTException;

import com.ptc.jca.mvc.components.AbstractAttributesComponentBuilder;
import com.ptc.mvc.components.AttributeConfig;
import com.ptc.mvc.components.AttributePanelConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.CustomizableViewConfig;
import com.ptc.mvc.components.GroupConfig;

@ComponentBuilder(value = "techMaterialattributes")
public class TechnicsMaterialAttributeBuilder extends AbstractAttributesComponentBuilder {
    
    @Override
    protected CustomizableViewConfig buildAttributesComponentConfig(ComponentParams arg0) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        AttributePanelConfig panelConfig = factory.newAttributePanelConfig();
        GroupConfig groupConfig = factory.newGroupConfig("General_");
        groupConfig.setLabel("详细信息");
        
        AttributeConfig numberConfig=factory.newAttributeConfig("number", null, 0, 0);
        numberConfig.setLabel("编号");
        groupConfig.addComponent(numberConfig);
        
        AttributeConfig nameConfig=factory.newAttributeConfig("name", null, 0, 1);
        nameConfig.setLabel("名称");
        groupConfig.addComponent(nameConfig);
        
        groupConfig.addComponent(factory.newAttributeConfig("LifeCycleState", "状态", 0, 2));
        groupConfig.addComponent(factory.newAttributeConfig("Container", "上下文", 1, 0));
        groupConfig.addComponent(factory.newAttributeConfig("Location", "位置", 1, 1));
        AttributeConfig creatorNameConfig=factory.newAttributeConfig("Creator", "创建者", 2, 0);
        groupConfig.addComponent(creatorNameConfig);
        groupConfig.addComponent(factory.newAttributeConfig("CreateTimestamp", "创建时间", 2, 1));
        groupConfig.addComponent(factory.newAttributeConfig("Modifier", "修改者", 3, 0));
        groupConfig.addComponent(factory.newAttributeConfig("ModifyTimestamp", "修改时间", 3, 1));
        groupConfig.addComponent(factory.newAttributeConfig("LifeCycleName", "生命周期", 4, 0));
        
        
        panelConfig.addComponent(groupConfig);

        return panelConfig;
    }

}
