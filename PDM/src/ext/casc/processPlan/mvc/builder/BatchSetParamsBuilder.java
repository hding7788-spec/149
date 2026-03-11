package ext.casc.processPlan.mvc.builder;

import com.glaway.mpm.util.ReferenceFactory;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import ext.casc.mpm.GyCsServerHelper;
import ext.casc.mpm.process.GLProcessParamDefinition;
import ext.casc.mpm.process.GLProcessParams;
import ext.casc.processPlan.Constants;
import ext.casc.util.Tools;
import ext.casc.util.WCUtil;
import wt.doc.WTDocument;
import wt.util.WTException;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量创建工艺规程
 */
@ComponentBuilder("ext.casc.processPlan.mvc.builder.BatchSetParamsBuilder")
public class BatchSetParamsBuilder extends AbstractComponentBuilder {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(BatchSetParamsBuilder.class);

    @Override
    public Object buildComponentData(ComponentConfig componentConfig, ComponentParams componentParams) throws Exception {
        NmCommandBean commandBean = ((JcaComponentParams) componentParams).getHelperBean().getNmCommandBean();
//        selectTemplates = transformTemplateOid(selectTemplates);
        List<Object> selectedOidForPopup = commandBean.getSelectedOidForPopup();
        List<Object> allItems = new ArrayList<Object>();
        for (Object obj : selectedOidForPopup) {
            Object object = null;
            String nmoid = null;
            if (obj instanceof String) {
                nmoid = String.valueOf(obj);
                object = WCUtil.getPersistable(nmoid);
            } else if (obj instanceof NmOid) {
                NmOid nmOid = (NmOid) obj;
                object = nmOid.getRefObject();
            } else if (obj instanceof NmContext) {
                NmContext context = (NmContext) obj;
                object = context.getTargetOid().getRefObject();
            }
            allItems.add(object);
        }
        return allItems;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams componentParams) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setLabel("批量设置参数");
        tableConfig.setSelectable(true);
        //tableConfig.setActionModel("custom_batchSetParamsValue");

        ColumnConfig oidConfig = factory.newColumnConfig("oid", false);
        oidConfig.setHidden(true);
        tableConfig.addComponent(oidConfig);

        ColumnConfig icon = factory.newColumnConfig("type_icon",false);
        tableConfig.addComponent(icon);

        ColumnConfig numberConfig = factory.newColumnConfig("number", "编号", true);
        numberConfig.setAutoSize(true);
        tableConfig.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("name", false);
        nameConfig.setAutoSize(true);
        tableConfig.addComponent(nameConfig);

        ColumnConfig versionConfig = factory.newColumnConfig("version", false);
        versionConfig.setAutoSize(true);
        tableConfig.addComponent(versionConfig);

        try {

            NmCommandBean commandBean =  ((JcaComponentParams) componentParams).getHelperBean().getNmCommandBean();
            String templateOid = (String) commandBean.getRequest().getSession().getAttribute("templateOid");
            System.out.println("buildComponentConfig templateOid:"+templateOid);
            if(templateOid.startsWith("VR")){
                WTDocument doc = (WTDocument)ReferenceFactory.getObjectbyOid(templateOid);
                templateOid = "OR:wt.doc.WTDocument:"+doc.getPersistInfo().getObjectIdentifier().getId();
            }
            List<GLProcessParamDefinition> processParamDefinitions = GyCsServerHelper.queryGLProcessParamDefinition(templateOid);
            for (GLProcessParamDefinition definition : processParamDefinitions) {
            	if("知识参数".equals(definition.getGyParamType())) continue;

                if("枚举参数".equals(definition.getGyParamType())){
                    GLProcessParams glProcessParam =  GyCsServerHelper.getProcessParamDefinition(definition.getGyParamNumber());
                    if(glProcessParam!=null){
                        String knowledgeInferencePara = glProcessParam.getKnowledgeInferencePara();
                        if(!Tools.isTrimNull(knowledgeInferencePara)){
                            continue;
                        }
                    }

                }

                GLProcessParams params =  GyCsServerHelper.getProcessParamDefinition(definition.getGyParamNumber()) ;
                String paramName = definition.getGyParamName();
                if(!Tools.isTrimNull(params.getUnit())){
                    paramName = definition.getGyParamName()+"("+params.getUnit()+")";
                }
                ColumnConfig columnConfig = factory.newColumnConfig(Constants.PRE + definition.getTemplateId()+"_"+definition.getGyParamNumber(),paramName , true);
                columnConfig.setAutoSize(true);
                columnConfig.setHidden(false);
                columnConfig.setDataUtilityId("MPMProcessPlanDatautility");
                tableConfig.addComponent(columnConfig);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return tableConfig;
    }



}
