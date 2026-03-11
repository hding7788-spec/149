package ext.casc.process.mvc.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpSession;

import wt.enterprise.Master;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.collections.WTArrayList;
import wt.inf.container.WTContainer;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.vc.config.ConfigHelper;
import wt.vc.config.ConfigSpec;

import com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.mvc.components.TreeConfig;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.constants.Constants;
import ext.casc.process.ProAssignTreeHandler;
import ext.casc.process.ProcessConstants;
import ext.casc.util.IBAUtility;
import ext.casc.util.WCUtil;

@ComponentBuilder("ext.casc.process.mvc.builder.PBOMProAssignTaskBuilder")
public class PBOMProAssignTaskBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
        NmCommandBean commandbean = ((JcaComponentParams)params).getHelperBean().getNmCommandBean();
        HttpSession session = commandbean.getRequest().getSession();
        session.setAttribute("TaskType", ProcessConstants.TASK_TYPE_GONGYISHEJI);
        NmOid nmOid = commandbean.getPrimaryOid();
        Object object = nmOid.getRefObject();
        if (object instanceof WTPart) {
            WTPart part = (WTPart)object;
            session.setAttribute("topOid", String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId()));
//            return new ProAssignTreeHandler(part);
            List allChildrenList = new ArrayList();
            List parents =  new ArrayList() ;
            parents.add(part);
            WTContainer parentContainer = part.getContainer();
            getAllChildPartsByPart(allChildrenList, parents, parentContainer);

            allChildrenList.add(part);
            return allChildrenList;
        }
        return null;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig treeConfig = factory.newTableConfig();
        treeConfig.setLabel("对象列表");
        treeConfig.setComponentMode(ComponentMode.VIEW);
        treeConfig.setConfigurable(false);
        treeConfig.setId("PBOM_ASSIGN_TASK");
        treeConfig.setActionModel("custom_process_taskbulder_partTable_actions");
        treeConfig.setSelectable(true);
//        treeConfig.setExpansionLevel("full");

        ColumnConfig nmActionsCol = factory.newColumnConfig(ColumnIdentifiers.NM_ACTIONS, false);
        ((JcaColumnConfig) nmActionsCol).setActionModel("custom_process_task_partTable_actions");
        treeConfig.addComponent(nmActionsCol);

        ColumnConfig icon = factory.newColumnConfig("type_icon",false);
        treeConfig.addComponent(icon);

        ColumnConfig numberConfig = factory.newColumnConfig("number", true);
        numberConfig.setInfoPageLink(true);
        treeConfig.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("name", true);
        nameConfig.setInfoPageLink(true);
        treeConfig.addComponent(nameConfig);

        ColumnConfig versionConfig = factory.newColumnConfig("version", false);
        versionConfig.setWidth(50);
        treeConfig.addComponent(versionConfig);

        ColumnConfig tuhao = factory.newColumnConfig("CINDEX",false);
        tuhao.setAutoSize(true);
        tuhao.setLabel("图号");
        treeConfig.addComponent(tuhao);

        ColumnConfig mindex = factory.newColumnConfig("MINDEX",false);
        mindex.setAutoSize(true);
        mindex.setLabel("所属型号");
        treeConfig.addComponent(mindex);

        ColumnConfig cmat = factory.newColumnConfig("CMAT",false);
        cmat.setAutoSize(true);
        cmat.setLabel("材料");
        treeConfig.addComponent(cmat);

        ColumnConfig phase_code = factory.newColumnConfig("PHASE_CODE",false);
        phase_code.setAutoSize(true);
        phase_code.setLabel("当前阶段");
        treeConfig.addComponent(phase_code);

        ColumnConfig mtype = factory.newColumnConfig("MTYPE", false);
        mtype.setAutoSize(true);
        mtype.setLabel("零组件生产类型");
        mtype.setDataUtilityId("ProcessTaskItemDataUtility");
        treeConfig.addComponent(mtype);

        ColumnConfig columnConfig = factory.newColumnConfig("zhuzhichejian",true);
        columnConfig.setLabel("*主制车间");
        columnConfig.setId("zhuzhichejian");
        //columnConfig.setInputFieldType("text");
        //columnConfig.setDefaultFreeze(false);
        columnConfig.setDataUtilityId("ProAssignTaskDatautility");
        columnConfig.setWidth(50);
        treeConfig.addComponent(columnConfig);

        /*
        ColumnConfig columnConfig2 = factory.newColumnConfig("fuzhuchejian",false);
        columnConfig2.setLabel("辅制车间");
        columnConfig2.setId("fuzhichejian");
        columnConfig2.setDataUtilityId("ProAssignTaskDatautility");
        //columnConfig2.setInputFieldType("ComboBox");
        columnConfig2.setWidth(200);
        treeConfig.addComponent(columnConfig2);
		*/

        ColumnConfig columnConfig3 = factory.newColumnConfig("jihuawanchengshijian",true);
        columnConfig3.setLabel("*计划完成时间");
        columnConfig3.setId("jihuawanchengshijian");
        columnConfig3.setDataUtilityId("ProAssignTaskDatautility");
        //columnConfig3.setWidth(150);
        treeConfig.addComponent(columnConfig3);

        ColumnConfig columnConfig4 = factory.newColumnConfig("renwuyaoqiu",true);
        columnConfig4.setLabel("任务要求");
        columnConfig4.setId("renwuyaoqiu");
        columnConfig4.setDataUtilityId("ProAssignTaskDatautility");
        treeConfig.addComponent(columnConfig4);


        ColumnConfig columnConfig5 = factory.newColumnConfig("renwuyiju",true);
        columnConfig5.setLabel("任务依据");
        columnConfig5.setId("renwuyiju");
        columnConfig5.setDataUtilityId("ProAssignTaskDatautility");
        treeConfig.addComponent(columnConfig5);

//        treeConfig.setNodeColumn("number");

        return treeConfig;
    }

    public  void getAllChildPartsByPart(List allChildrenList ,List parents,WTContainer parentContainer) throws WTException{

    	Persistable[][][] all_children = WTPartHelper.service.getUsesWTParts(new WTArrayList(parents),getDefaultConfigSpec());
        WTContainer childContainer = null;
        for (ListIterator i = parents.listIterator(); i.hasNext();) {
        	WTPart parent = (WTPart) i.next();
        	Persistable[][] branch = all_children[i.previousIndex()];
            if (branch == null) {
                continue;
            }
            List children = new ArrayList(branch.length);

            for (Persistable[] child : branch) {
                Persistable per = child[1];
                if(!(per instanceof WTPart) && !(per instanceof WTPartMaster)) {
                	continue;
                }
                WTPart childPart = null;
                if (per instanceof WTPart) {
                    childPart = (WTPart)per;
                    childPart = WCUtil.getLatestPartByView((Master)childPart.getMaster(), "Manufacturing");
                } else if(per instanceof WTPartMaster) {
                	childPart = WCUtil.getLatestPartByView((Master)per, "Manufacturing");
                }

                if (childPart == null){
                    continue;
                }

                IBAUtility ibaUtility = new IBAUtility(childPart);
                String partType = ibaUtility.getIBAValue("MTYPE");

                if(!children.contains(childPart)){
                	children.add(childPart);
                }

                //过滤不是自制件、外配套件、带料委外件、不带料委外件类型的零部件
                if (Constants.TYPE_ZIZHIJIAN.equals(partType)
                		||Constants.TYPE_WAIPEITAOJIAN.equals(partType)
                		||Constants.TYPE_DAILIAOWEIWAIJIAN.equals(partType)
                		||Constants.TYPE_BUDAILIAOWEIWAIJIAN.equals(partType)) {
                    childContainer = childPart.getContainer();
                    if (!parentContainer.getName().equals(childContainer.getName())) {//过滤掉借用件，即是跟父件不在同一产品库下的零部件
                        continue;
                    }
                    if(!allChildrenList.contains(childPart)){
                    	allChildrenList.add(childPart);
                    }


                }
            }
            getAllChildPartsByPart(allChildrenList,children,parentContainer);
        }
    }

    protected ConfigSpec getDefaultConfigSpec() throws WTException {
        return ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class);
    }

}
