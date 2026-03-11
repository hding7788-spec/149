package ext.casc.common.mvc.builder;

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.mpm.GyCsServerHelper;
import ext.casc.mpm.process.GLProcessParamDefinition;
import ext.casc.persistence.PersistenceCommonHelper;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.util.WTException;

import java.util.List;

/**
 * 批量创建工艺规程
 */
@ComponentBuilder("ext.casc.common.mvc.builder.SetTemplateEnumBuilder")
public class SetTemplateEnumBuilder extends AbstractComponentBuilder {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SetTemplateEnumBuilder.class);

    @Override
    public Object buildComponentData(ComponentConfig componentConfig, ComponentParams componentParams) throws Exception {

        NmCommandBean commandBean = ((JcaComponentParams) componentParams).getHelperBean().getNmCommandBean();
        Object object = commandBean.getPageOid().getRefObject();
        if (object instanceof WTDocument) {
           String oid =  PersistenceCommonHelper.getOid((Persistable) object);
            List<GLProcessParamDefinition> list = GyCsServerHelper.queryGLProcessParamDefinition(oid);
            return list;
        }
        return null;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams componentParams) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setLabel("批量设置参数");
        tableConfig.setSelectable(true);

        ColumnConfig templateId = factory.newColumnConfig("templateId", false);
        templateId.setHidden(true);
        tableConfig.addComponent(templateId);


        ColumnConfig gyParamNumber = factory.newColumnConfig("gyParamNumber", "工艺参数编号", true);
        gyParamNumber.setAutoSize(true);
        tableConfig.addComponent(gyParamNumber);

        ColumnConfig gyParamName = factory.newColumnConfig("gyParamName","工艺参数名称", true);
        gyParamName.setAutoSize(true);
        tableConfig.addComponent(gyParamName);

        ColumnConfig enumValues = factory.newColumnConfig("enumValues","合法值列表", false);
        enumValues.setWidth(400);
        enumValues.setDataUtilityId("MPMProcessPlanDatautility");
        tableConfig.addComponent(enumValues);

        return tableConfig;
    }



}
