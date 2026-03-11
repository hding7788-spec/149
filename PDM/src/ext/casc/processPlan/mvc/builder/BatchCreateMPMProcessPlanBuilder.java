package ext.casc.processPlan.mvc.builder;

import com.glaway.mpm.util.ReferenceFactory;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.extend.ixb.CmExpImpSearchHelper;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import ext.casc.constants.Constants;
import ext.casc.process.ProcessTask;
import ext.casc.process.ProcessTaskItem;
import ext.casc.util.IBAHelper;
import ext.casc.util.IBAUtility;
import ext.casc.util.WCUtil;
import ext.casc.util.WTUserUtil;
import wt.enterprise.Master;
import wt.fc.Persistable;
import wt.fc.collections.WTArrayList;
import wt.inf.container.WTContainer;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.config.ConfigHelper;

import java.util.*;

/**
 * 批量创建工艺规程
 */
@ComponentBuilder("ext.casc.processPlan.mvc.builder.BatchCreateMPMProcessPlanBuilder")
public class BatchCreateMPMProcessPlanBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig componentConfig, ComponentParams componentParams) throws Exception {
        NmCommandBean commandBean = ((JcaComponentParams) componentParams).getHelperBean().getNmCommandBean();
        NmOid pageObject =   commandBean.getPageOid();
        List<WTPart> allChildrenList = new ArrayList();
        if(pageObject !=null){
            Object object = pageObject.getRefObject();
            List<WTPart> parents = new ArrayList();
            WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
            String groupName = WTUserUtil.getGroupShortName(currentUser);
            if (object instanceof WTPart) {
                WTPart part = (WTPart) object;
                parents.add(part);
                WTContainer parentContainer = part.getContainer();
                getAllChildPartsByPart(allChildrenList, parents, parentContainer,groupName);
                String zzcj = IBAHelper.getIBAStringValue(part,"ZZCJ");
                if(groupName.equals(zzcj)){
                    allChildrenList.add(part);
                }

            }
        }else{
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
            Set<String> hasParts = new HashSet<>();
            for(Object tempItem :allItems){
                if (tempItem instanceof ProcessTaskItem) {
                    ProcessTaskItem processTaskItem = (ProcessTaskItem) tempItem;
                    long partId = processTaskItem.getPartId();
                    WTPart itemPart = (WTPart) ReferenceFactory.getObjectbyOid("OR:wt.part.WTPart:"+partId);
                    if(itemPart!=null){
                        if(!hasParts.contains(itemPart.getNumber())){
                            if(!itemPart.isLatestIteration()){
                                WTPart lastetPart =  WTPartUtil.getLatestPartByVersionNumberAndView(itemPart,
                                        "");
                                allChildrenList.add(lastetPart);
                            }else {
                                allChildrenList.add(itemPart);

                            }

                        }
                        hasParts.add(itemPart.getNumber());
                    }
                }
            }

        }



        return allChildrenList;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams componentParams) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setActionModel("custom_batchSetParams");
        tableConfig.setLabel("批量创建工艺");
        tableConfig.setSelectable(true);

        ColumnConfig oidConfig = factory.newColumnConfig("oid",  false);
        oidConfig.setHidden(true);
        tableConfig.addComponent(oidConfig);

        ColumnConfig icon = factory.newColumnConfig("type_icon",false);
        tableConfig.addComponent(icon);


        ColumnConfig numberConfig = factory.newColumnConfig("number", true);
        numberConfig.setWidth(100);
        tableConfig.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("name", false);
        nameConfig.setWidth(100);
        nameConfig.setInfoPageLink(true);
        tableConfig.addComponent(nameConfig);

        ColumnConfig versionConfig = factory.newColumnConfig("version", false);
        versionConfig.setAutoSize(true);
        tableConfig.addComponent(versionConfig);

        ColumnConfig cIndexConfig = factory.newColumnConfig("CINDEX", "图号", false);
        cIndexConfig.setAutoSize(true);
        tableConfig.addComponent(cIndexConfig);

        ColumnConfig mTypeConfig = factory.newColumnConfig("MTYPE", "零组件生产类型", false);
        mTypeConfig.setAutoSize(true);
        tableConfig.addComponent(mTypeConfig);

        ColumnConfig processParaStatusConfig = factory.newColumnConfig("processParaStatus", "工艺参数状态", false);
        processParaStatusConfig.setAutoSize(true);
//        processParaStatusConfig.setDataUtilityId("");
        tableConfig.addComponent(processParaStatusConfig);

        ColumnConfig processTemplateConfig = factory.newColumnConfig("processTemplate", "参数化工艺模板", false);
        processTemplateConfig.setWidth(200);
        processTemplateConfig.setDataUtilityId("MPMProcessPlanDatautility");
        tableConfig.addComponent(processTemplateConfig);

        ColumnConfig links = factory.newColumnConfig("processLink", "关联的工艺文件", false);
        links.setWidth(200);
        links.setDataUtilityId("MPMProcessPlanDatautility");
        tableConfig.addComponent(links);
       /* ColumnConfig actionConfig = factory.newColumnConfig("doAction", "操作", false);
        actionConfig.setWidth(50);
        actionConfig.setDataUtilityId("MPMProcessPlanDatautility");
        tableConfig.addComponent(actionConfig);*/

        return tableConfig;
    }


    public void getAllChildPartsByPart(List allChildrenList, List parents, WTContainer parentContainer,String groupName) throws WTException {
        Persistable[][][] all_children = WTPartHelper.service.getUsesWTParts(new WTArrayList(parents), ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class));
        WTContainer childContainer = null;
        for (ListIterator i = parents.listIterator(); i.hasNext(); ) {
           WTPart parent = (WTPart) i.next();
            Persistable[][] branch = all_children[i.previousIndex()];
            if (branch == null) {
                continue;
            }
            List children = new ArrayList(branch.length);
            for (Persistable[] child : branch) {
                Persistable per = child[1];
                if (!(per instanceof WTPart) && !(per instanceof WTPartMaster)) {
                    continue;
                }
                WTPart childPart = null;
                if (per instanceof WTPart) {
                    childPart = (WTPart) per;
                    childPart = WCUtil.getLatestPartByView((Master) childPart.getMaster(), "Manufacturing");
                } else if (per instanceof WTPartMaster) {
                    childPart = WCUtil.getLatestPartByView((Master) per, "Manufacturing");
                }

                if (childPart == null) {
                    continue;
                }
                IBAUtility ibaUtility = new IBAUtility(childPart);
                String partType = ibaUtility.getIBAValue("MTYPE");

                if (!children.contains(childPart)) {
                    children.add(childPart);
                }
                //过滤不是自制件的零部件
                if (Constants.TYPE_ZIZHIJIAN.equals(partType)) {
                    childContainer = childPart.getContainer();
                    if (!parentContainer.getName().equals(childContainer.getName())) {//过滤掉借用件，即是跟父件不在同一产品库下的零部件
                        continue;
                    }
                    if (!allChildrenList.contains(childPart)) {
                        String zzcj = IBAHelper.getIBAStringValue(childPart,"ZZCJ");
                        if(groupName.equals(zzcj)){
                            allChildrenList.add(childPart);
                        }
                    }
                }
            }
            getAllChildPartsByPart(allChildrenList, children, parentContainer,groupName);
        }
    }
}
