package ext.casc.process.mvc.builder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wt.fc.PersistenceHelper;
import wt.part.WTPart;
import wt.util.WTException;

import com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.process.ProcessTask;
import ext.casc.process.ProcessTaskItem;
import ext.casc.process.util.ProcessUtil;

@ComponentBuilder("ext.casc.process.mvc.builder.ListPartForProcessTaskItemBuilder")
public class ListPartForProcessTaskItemBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
        NmCommandBean cb = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
        NmOid nmOid = cb.getPageOid();
        Set<Object> list = new HashSet<Object>();
        try {
            Object object = nmOid.getRefObject();
            if (object instanceof ProcessTaskItem) {
                ProcessTaskItem taskItem = (ProcessTaskItem)object;
                WTPart part = ProcessUtil.getWtPart(taskItem.getPartId());
                if (part != null) {
                	//part = ProcessUtil.getPartByNumber(part.getNumber(), "Manufacturing");

                    list.add(part);
                    list.addAll(ProcessUtil.getRelatedObjByPart(part));

                    WTPart dpart = ProcessUtil.getPartByNumber(part.getNumber(), "Design");
                    if(dpart != null) {
                    	list.addAll(ProcessUtil.getRelatedObjByPart(dpart));
                    }
                }
            } else if (object instanceof ProcessTask) {
                ProcessTask processTask = (ProcessTask)object;
                long longId = PersistenceHelper.getObjectIdentifier(processTask).getId();
                WTPart part = ProcessUtil.getWtPartByProcessTask(longId);
                if (part != null) {
                	//part = ProcessUtil.getPartByNumber(part.getNumber(), "Manufacturing");

                    list.add(part);
                    list.addAll(ProcessUtil.getRelatedObjByPart(part));

                    WTPart dpart = ProcessUtil.getPartByNumber(part.getNumber(), "Design");
                    if(dpart != null) {
                    	list.addAll(ProcessUtil.getRelatedObjByPart(dpart));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setSelectable(false);
        tableConfig.setConfigurable(false);

        tableConfig.setLabel("零部件列表");

        ColumnConfig nmActionsCol = factory.newColumnConfig(ColumnIdentifiers.NM_ACTIONS, false);
        ((JcaColumnConfig) nmActionsCol).setActionModel("custom_process_task_partTable_actions");
        tableConfig.addComponent(nmActionsCol);

        ColumnConfig icon = factory.newColumnConfig("type_icon", false);
        tableConfig.addComponent(icon);

        ColumnConfig smallThumbnail = factory.newColumnConfig("smallThumbnail",false);
        tableConfig.addComponent(smallThumbnail);

        ColumnConfig numberColumnConfig = factory.newColumnConfig("number", true);
        numberColumnConfig.setAutoSize(true);
        numberColumnConfig.setInfoPageLink(true);
        tableConfig.addComponent(numberColumnConfig);

        ColumnConfig nameColumnConfig = factory.newColumnConfig("name", true);
        nameColumnConfig.setAutoSize(true);
        tableConfig.addComponent(nameColumnConfig);

        ColumnConfig versionColumnConfig = factory.newColumnConfig("version", false);
        versionColumnConfig.setAutoSize(true);
        tableConfig.addComponent(versionColumnConfig);

        ColumnConfig mtype = factory.newColumnConfig("MTYPE", false);
        mtype.setAutoSize(true);
        mtype.setLabel("零组件生产类型");
        mtype.setDataUtilityId("ProcessTaskItemDataUtility");
        tableConfig.addComponent(mtype);

        ColumnConfig phase = factory.newColumnConfig("PHASE_CODE", false);
        phase.setAutoSize(true);
        phase.setLabel("当前阶段");
        phase.setDataUtilityId("ProcessTaskItemDataUtility");
        tableConfig.addComponent(phase);

        ColumnConfig keycomponent = factory.newColumnConfig("KEYCOMPONENT", false);
        keycomponent.setAutoSize(true);
        keycomponent.setLabel("关重键标识");
        keycomponent.setDataUtilityId("ProcessTaskItemDataUtility");
        tableConfig.addComponent(keycomponent);

        ColumnConfig enditem = factory.newColumnConfig("ENDITEMIN", false);
        enditem.setAutoSize(true);
        enditem.setLabel("所属成品");
        enditem.setDataUtilityId("ProcessTaskItemDataUtility");
        tableConfig.addComponent(enditem);

        ColumnConfig mindex = factory.newColumnConfig("MINDEX", false);
        mindex.setAutoSize(true);
        mindex.setLabel("型号代号");
        mindex.setDataUtilityId("ProcessTaskItemDataUtility");
        tableConfig.addComponent(mindex);

        ColumnConfig batch = factory.newColumnConfig("BATCH", false);
        batch.setAutoSize(true);
        batch.setLabel("批次号");
        batch.setDataUtilityId("ProcessTaskItemDataUtility");
        tableConfig.addComponent(batch);

        return tableConfig;
    }

}
