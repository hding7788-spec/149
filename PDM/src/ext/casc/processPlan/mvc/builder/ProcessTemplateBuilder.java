package ext.casc.processPlan.mvc.builder;

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.mpm.process.ProcessUtil;
import ext.casc.processPlan.Constants;
import wt.doc.WTDocument;
import wt.util.WTException;

import java.util.HashMap;
import java.util.Map;

/**
 * 工艺参数化模板
 */
@ComponentBuilder("ext.casc.processPlan.mvc.builder.ProcessTemplateBuilder")
public class ProcessTemplateBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig componentConfig, ComponentParams params) throws Exception {
        NmCommandBean commandBean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
        String work = String.valueOf(params.getParameter("work"));
        if ("search".equals(work)) {
            try {

                String number = String.valueOf(params.getParameter("number"));
                String name = String.valueOf(params.getParameter("name"));

                String dept = String.valueOf(params.getParameter("dept"));
                String speciality = String.valueOf( params.getParameter("speciality"));
                String productType = String.valueOf(params.getParameter("productType"));
                String startDate = String.valueOf(params.getParameter("createTime_FROM"));
                String endDate = String.valueOf(params.getParameter("createTime_To"));


                Map<String, String> map = new HashMap<String, String>();
                map.put(WTDocument.NUMBER, number);
                map.put(WTDocument.NAME, name);
                map.put(WTDocument.DOC_TYPE, Constants.DOC_TYPE_TechnicsParamTemplate_FULL);
                map.put(WTDocument.CREATE_TIMESTAMP + "_FROM", startDate);
                map.put(WTDocument.CREATE_TIMESTAMP + "_TO", endDate);

                Map<String, String> ibaMap = new HashMap<String, String>();
                ibaMap.put(Constants.IBA_DEPT, dept);
                ibaMap.put("speciality", speciality);
                ibaMap.put("productType", productType);

                return ProcessUtil.queryDocByIBA(map, ibaMap);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams componentParams) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setSelectable(true);
        tableConfig.setSingleSelect(true);
        tableConfig.setLabel("工艺参数化模板");
        tableConfig.setSelectable(true);

        ColumnConfig icon = factory.newColumnConfig("type_icon",false);
        tableConfig.addComponent(icon);

        ColumnConfig numberConfig = factory.newColumnConfig("number", "编号", false);
        numberConfig.setAutoSize(true);
        numberConfig.setSortable(true);
        tableConfig.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("name", false);
        nameConfig.setAutoSize(true);
        tableConfig.addComponent(nameConfig);

        ColumnConfig versionConfig = factory.newColumnConfig("version", false);
        versionConfig.setAutoSize(true);
        tableConfig.addComponent(versionConfig);

        return tableConfig;
    }

}
