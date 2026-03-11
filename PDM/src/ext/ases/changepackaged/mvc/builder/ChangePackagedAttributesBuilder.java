package ext.ases.changepackaged.mvc.builder;

import com.ptc.jca.mvc.components.AbstractAttributesComponentBuilder;
import com.ptc.mvc.components.*;
import com.ptc.mvc.util.ClientMessageSource;
import wt.util.WTException;

@ComponentBuilder(value = "changeAttributes")
public class ChangePackagedAttributesBuilder extends AbstractAttributesComponentBuilder {
    private static final String RESOURCE = "ext.ases.changepackaged.changepackagedResource";
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
        groupConfig.addComponent(factory.newAttributeConfig("complex", this.messageSource.getMessage("complex"), 2, 1));
        groupConfig.addComponent(factory.newAttributeConfig("Creator", "创建者", 3, 0));
        groupConfig.addComponent(factory.newAttributeConfig("CreateTimestamp", "创建时间", 3, 1));
        groupConfig.addComponent(factory.newAttributeConfig("ModifyTimestamp", "修改时间", 4, 0));
        groupConfig.addComponent(factory.newAttributeConfig("LifeCycleName", "生命周期", 4, 1));
        groupConfig.addComponent(factory.newAttributeConfig("profession", messageSource.getMessage("profession"), 5, 0));
        groupConfig.addComponent(factory.newAttributeConfig("changetype", messageSource.getMessage("changetype"), 5, 1));
        groupConfig.addComponent(factory.newAttributeConfig("phasecode", messageSource.getMessage("phasecode"), 6, 0));
        groupConfig.addComponent(factory.newAttributeConfig("affectdpage", messageSource.getMessage("affectdpage"), 6, 1));
        groupConfig.addComponent(factory.newAttributeConfig("department", messageSource.getMessage("department"), 7, 0));
        groupConfig.addComponent(factory.newAttributeConfig("requestpriority", messageSource.getMessage("requestpriority"), 7, 1));
        groupConfig.addComponent(factory.newAttributeConfig("changeleixing", messageSource.getMessage("changeleixing"), 8, 0));
        groupConfig.addComponent(factory.newAttributeConfig("changereason", messageSource.getMessage("changereason"), 8, 1));
        groupConfig.addComponent(factory.newAttributeConfig("edittime", messageSource.getMessage("edittime"), 9, 0));
        groupConfig.addComponent(factory.newAttributeConfig("requesttime", messageSource.getMessage("requesttime"), 9, 1));
        groupConfig.addComponent(factory.newAttributeConfig("pindex", messageSource.getMessage("pindex"), 10, 0));
        groupConfig.addComponent(factory.newAttributeConfig("filenumber", messageSource.getMessage("filenumber"), 10, 1));
        groupConfig.addComponent(factory.newAttributeConfig("startphasename", messageSource.getMessage("startphasename"), 11, 0));
        groupConfig.addComponent(factory.newAttributeConfig("targetphasename", messageSource.getMessage("targetphasename"), 11, 1));
        groupConfig.addComponent(factory.newAttributeConfig("avidmtype", messageSource.getMessage("avidmtype"), 12, 0));
        groupConfig.addComponent(factory.newAttributeConfig("template", messageSource.getMessage("template"), 12, 1));
        groupConfig.addComponent(factory.newAttributeConfig("cost", messageSource.getMessage("cost"), 13, 0));
        groupConfig.addComponent(factory.newAttributeConfig("secret", messageSource.getMessage("secret"), 13, 1));
        groupConfig.addComponent(factory.newAttributeConfig("guancanghao", messageSource.getMessage("guancanghao"), 14, 0));
        groupConfig.addComponent(factory.newAttributeConfig("responsor", messageSource.getMessage("responsor"), 14, 1));
        groupConfig.addComponent(factory.newAttributeConfig("remark", messageSource.getMessage("remark"), 15, 0));
        groupConfig.addComponent(factory.newAttributeConfig("IBA|hasAnalysis", null, 15, 1));
        panelConfig.addComponent(groupConfig);
        return panelConfig;
    }

}
