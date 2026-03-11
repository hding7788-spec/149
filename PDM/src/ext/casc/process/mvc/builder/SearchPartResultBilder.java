package ext.casc.process.mvc.builder;

import com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.process.util.ProcessUtil;
import wt.fc.ReferenceFactory;
import wt.inf.container.WTContainer;
import wt.part.WTPart;
import wt.util.WTException;

@ComponentBuilder("ext.casc.process.mvc.builder.SearchPartResultBilder")
public class SearchPartResultBilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
        String work = String.valueOf(params.getParameter("work"));
        if ("search".equals(work)) {
            //String productName = ProcessUtil.getNotNullParam(params.getParameter("productName"));
            String number = ProcessUtil.getNotNullParam(params.getParameter("number"));
            String name = ProcessUtil.getNotNullParam(params.getParameter("name"));
            String version = ProcessUtil.getNotNullParam(params.getParameter("version"));
            return ProcessUtil.queryPart("", number, name, "Manufacturing",true,version);
        }
        return null;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setSelectable(true);
        tableConfig.setId("ext.casc.process.mvc.builder.SearchPartResultBilder");
        tableConfig.setLabel("零部件列表");
        ColumnConfig nmActionsCol = factory.newColumnConfig(ColumnIdentifiers.NM_ACTIONS, false);
        ((JcaColumnConfig) nmActionsCol).setActionModel("custom_process_task_partTable_actions");
        tableConfig.addComponent(nmActionsCol);
        ColumnConfig icon = factory.newColumnConfig("type_icon",false);
        tableConfig.addComponent(icon);

        ColumnConfig numberColumnConfig = factory.newColumnConfig("number", true);
        numberColumnConfig.setAutoSize(true);
        numberColumnConfig.setInfoPageLink(true);
        tableConfig.addComponent(numberColumnConfig);

        ColumnConfig nameColumnConfig = factory.newColumnConfig("name", true);
        nameColumnConfig.setAutoSize(true);
        nameColumnConfig.setInfoPageLink(true);
        tableConfig.addComponent(nameColumnConfig);

        ColumnConfig versionColumnConfig = factory.newColumnConfig("version", false);
        versionColumnConfig.setAutoSize(true);
        tableConfig.addComponent(versionColumnConfig);

        ColumnConfig tuhao = factory.newColumnConfig("CINDEX",false);
        tuhao.setAutoSize(true);
        tuhao.setLabel("图号");
        tableConfig.addComponent(tuhao);

        ColumnConfig mindex = factory.newColumnConfig("MINDEX",false);
        mindex.setAutoSize(true);
        mindex.setLabel("所属型号");
        tableConfig.addComponent(mindex);

        ColumnConfig cmat = factory.newColumnConfig("CMAT",false);
        cmat.setAutoSize(true);
        cmat.setLabel("材料");
        tableConfig.addComponent(cmat);

        ColumnConfig phase_code = factory.newColumnConfig("PHASE_CODE",false);
        phase_code.setAutoSize(true);
        phase_code.setLabel("当前阶段");
        tableConfig.addComponent(phase_code);

        NmCommandBean commandBean = ((JcaComponentParams) params)
                .getHelperBean().getNmCommandBean();
        WTContainer wtContainer = commandBean.getContainer();
       if(wtContainer==null){

    	   String[] containerOids =  (String[])commandBean.getParameterMap().get("partOid");
    	   ReferenceFactory rf = new ReferenceFactory();
    	   WTPart wtPart = (WTPart)rf.getReference(containerOids[0]).getObject();
    	   wtContainer = wtPart.getContainer();
       }
        if(ProcessUtil.isZhuRenGongyiShi(wtContainer)){

            ColumnConfig columnConfig = factory.newColumnConfig("zhuzhichejian",false);
            columnConfig.setLabel("*主制车间");
            columnConfig.setHidden(true);
            columnConfig.setId("zhuzhichejian");
            //columnConfig.setInputFieldType("text");
            //columnConfig.setDefaultFreeze(false);
            columnConfig.setDataUtilityId("NewProAssignTaskDatautility");
            columnConfig.setWidth(50);
            tableConfig.addComponent(columnConfig);


      }else{
      	 ColumnConfig columnConfig = factory.newColumnConfig("sign_person",false);
           columnConfig.setLabel("*车间工艺员");
           columnConfig.setId("sign_person");
           //columnConfig.setInputFieldType("text");
           //columnConfig.setDefaultFreeze(false);
           columnConfig.setDataUtilityId("NewProAssignTaskDatautility");
           columnConfig.setWidth(200);
           tableConfig.addComponent(columnConfig);
      }

        ColumnConfig columnConfig3 = factory.newColumnConfig("jihuawanchengshijian",false);
        columnConfig3.setLabel("*计划完成时间");
        columnConfig3.setHidden(true);
        columnConfig3.setId("jihuawanchengshijian");
        columnConfig3.setDataUtilityId("NewProAssignTaskDatautility");
        //columnConfig3.setWidth(150);
        tableConfig.addComponent(columnConfig3);

        ColumnConfig columnConfig4 = factory.newColumnConfig("renwuyaoqiu",false);
        columnConfig4.setLabel("任务要求");
        columnConfig4.setHidden(true);
        columnConfig4.setId("renwuyaoqiu");
        columnConfig4.setDataUtilityId("NewProAssignTaskDatautility");
        tableConfig.addComponent(columnConfig4);

        ColumnConfig columnConfig5 = factory.newColumnConfig("renwuyiju",false);
        columnConfig5.setLabel("任务依据");
        columnConfig5.setId("renwuyiju");
        columnConfig5.setDataUtilityId("NewProAssignTaskDatautility");
        tableConfig.addComponent(columnConfig5);

        ColumnConfig mtype = factory.newColumnConfig("MTYPE", false);
        mtype.setAutoSize(true);
        mtype.setLabel("零组件生产类型");
        mtype.setDataUtilityId("ProcessTaskItemDataUtility");
        tableConfig.addComponent(mtype);
        return tableConfig;
    }

}
