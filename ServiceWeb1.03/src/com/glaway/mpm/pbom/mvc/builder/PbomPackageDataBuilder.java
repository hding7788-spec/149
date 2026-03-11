package com.glaway.mpm.pbom.mvc.builder;

import java.util.ArrayList;
import java.util.List;

import wt.enterprise.Master;
import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.part.WTPart;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

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
import com.ptc.mvc.components.TreeConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.ases.envelope.ProcessEnvelope;
import ext.ases.envelope.ProcessEnvelopeUtil;
import ext.casc.util.WCUtil;
import ext.casc.workflow.signtrue.zp.HuiQianWorkFlowService;

@ComponentBuilder("com.glaway.mpm.pbom.mvc.builder.PbomPackageDataBuilder")
public class PbomPackageDataBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
        NmCommandBean commandBean = ((JcaComponentParams) arg1).getHelperBean().getNmCommandBean();
        Object object = commandBean.getPageOid().getRefObject();
        String activityName = "";
        if(object instanceof WorkItem) {
            WorkItem workItem = (WorkItem)object;
            ReferenceFactory factory = new ReferenceFactory();
            String oid = factory.getReferenceString(workItem);
            activityName = HuiQianWorkFlowService.getActivityNameByWorkItemOid(oid);
        }
        if(object instanceof WTPart) {
            WTPart tempPart = (WTPart)object;
            if("PBOM构建".equals(activityName)) {
                return tempPart;
            }
            WTPart mpart = WCUtil.getLatestPartByView((Master)tempPart.getMaster(), "Manufacturing");
            return mpart;
        } else if (object instanceof WorkItem) {
            WorkItem wi = (WorkItem) object;
            Persistable per = wi.getPrimaryBusinessObject().getObject();
            if(per instanceof ProcessEnvelope) {
            	ProcessEnvelope processEnvelope = (ProcessEnvelope)per;
                List list = ProcessEnvelopeUtil.getAllMembers(processEnvelope);
                WTPart topPart = (WTPart)ProcessEnvelopeUtil.getTopObject(processEnvelope);
                if(topPart != null) {
                	return new PbomPackageDataTableHandler(topPart,list,activityName);
                }
                List<WTPart> resultList = new ArrayList<WTPart>();
                if((list != null) && !list.isEmpty()) {
                    for (Object tempObj : list) {
                        if(tempObj instanceof WTPart) {
                            WTPart tempPart = (WTPart)tempObj;
                            if("PBOM构建".equals(activityName)) {
                                resultList.add(tempPart);
                            } else {
                                WTPart mpart = WCUtil.getLatestPartByView((Master)tempPart.getMaster(), "Manufacturing");
                                if(mpart != null) {
                                    resultList.add((WTPart)mpart);
                                }
                            }

                        }
                    }
                }
                return resultList;
            } else if (per instanceof WTPart) {
                WTPart tempPart = (WTPart)per;
                if("PBOM构建".equals(activityName)) {
                    return tempPart;
                }
                WTPart mpart = WCUtil.getLatestPartByView((Master)tempPart.getMaster(), "Manufacturing");
                return mpart;
            }
        }
        return null;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        NmCommandBean commandBean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
        WorkItem workItem = (WorkItem)commandBean.getPageOid().getRefObject();

        ComponentConfigFactory factory = getComponentConfigFactory();
        TreeConfig treeConfig = factory.newTreeConfig();
        treeConfig.setLabel("数据列表");
        treeConfig.setComponentMode(ComponentMode.VIEW);
        treeConfig.setConfigurable(false);
        treeConfig.setSelectable(false);
        treeConfig.setExpansionLevel("full");

        if(!workItem.isComplete()){
            ColumnConfig nmActionsCol = factory.newColumnConfig(ColumnIdentifiers.NM_ACTIONS, false);
            ((JcaColumnConfig) nmActionsCol).setActionModel("pbom_table_actions");
            treeConfig.addComponent(nmActionsCol);
        }

        ColumnConfig icon = factory.newColumnConfig("type_icon", false);
        treeConfig.addComponent(icon);

        ColumnConfig numberConfig = factory.newColumnConfig("number", false);
        numberConfig.setInfoPageLink(true);
        numberConfig.setWidth(150);
        treeConfig.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("name", false);
        nameConfig.setWidth(150);
        treeConfig.addComponent(nameConfig);

        ColumnConfig versionConfig = factory.newColumnConfig("version", false);
        versionConfig.setAutoSize(true);
        treeConfig.addComponent(versionConfig);

        ColumnConfig ctype = factory.newColumnConfig("CTYPE", false);
        ctype.setAutoSize(true);
        treeConfig.addComponent(ctype);

        treeConfig.setNodeColumn("number");

        return treeConfig;
    }

}
