package ext.casc.workflow.tree;

import java.sql.SQLException;

import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignmentState;
import wt.workflow.work.WorkItem;

import com.glaway.mpm.print.data.CmPrintRecordInfoBean;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;

import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.ixb.ReleaseDataAdvisBackHelper;
import ext.casc.util.NmTableGUIComponent;
import ext.casc.util.WCUtil;
import ext.casc.workflow.util.PrintDistributionHelper;

public class ReleaseDataUtility extends AbstractDataUtility {

    @Override
    public Object getDataValue(String columnName, Object obj, ModelContext mc) throws WTException {
        if ((obj instanceof WTDocument) || (obj instanceof EPMDocument) || (obj instanceof MPMProcessPlan)
                || (obj instanceof ChangePackaged)) {
            NmCommandBean cb = mc.getNmCommandBean();
            WorkItem wi = (WorkItem) cb.getPageOid().getRefObject();
            WfActivity wfAct = (WfActivity) wi.getSource().getObject();
            WfProcess wfProcess=wfAct.getParentProcess();
            String processName=wfProcess.getTemplate().getName();
            if ("updatestate".equals(columnName)) {
                Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
                String value = "";
                if (pbo instanceof ProcessEnvelope) {
                    QueryResult qr = PersistenceHelper.manager.navigate((Persistable) obj, "theProcessEnvelope",
                            EnvelopeMemberLink.class, false);
                    while (qr.hasMoreElements()) {
                        EnvelopeMemberLink link = (EnvelopeMemberLink) qr.nextElement();
                        Persistable roleA = link.getRoleAObject();
                        if (pbo.equals(roleA)) {
                            value = link.getDescription();
                            if (value == null) {
                                value = "";
                            }
                            break;
                        }
                    }
                    return value;
                }
            } else if("toGYY".equals(columnName)){
               GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
               guicomponentarrayMain.setValueHidden(false);
               WfAssignmentState state = wi.getStatus();
               String oid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
               ReferenceFactory rf = new ReferenceFactory();
               String veroid = null;
               Versioned version = null;
               String selected = "";
               if (obj instanceof ChangePackaged) {
                   veroid = rf.getReference((ChangePackaged) obj).toString();
               } else {
                   QueryResult qr = VersionControlHelper.service.allVersionsFrom((Versioned) obj);
                   if (qr.hasMoreElements()) {
                       version = (Versioned) qr.nextElement();
                       veroid = rf.getReference(version).toString();
                   }
               }
               veroid = veroid.replaceAll(">", ":");
               String values = "<input type=\"text\"  readonly name=\""
                       + oid
                       + "_selected_toggy\"  id=\""
                       + veroid
                       + "_selected_toggy\" value=\""
                       + selected
                       + "\"/><input type=\"button\" value=\"选择\" onclick=\"javascript:showToGGY('"+veroid+"');\"  id=\""
                       + veroid + "_selectGYY\"/>" +
                       "<input type=\"button\" value=\"清空\" onclick=\"javascript:clearToGGY('"+veroid+"');\"" +
                       		"id=\"" + veroid + "_selectClearGYY\"/>" +
                       		"<input type=\"hidden\"  name=\"" + oid + "_selected_toggy_value\" " +
                       "id=\"" + veroid + "_selected_toggy_value\" value=\""+selected+"\">";
               if("COMPLETED".equals(state.toString())){
                   String number = "";
                   if(obj instanceof WTDocument) {
                       number = ((WTDocument)obj).getNumber();
                   } else if(obj instanceof EPMDocument) {
                       number = ((EPMDocument)obj).getNumber();
                   }
                   String oldValue = ReleaseDataAdvisBackHelper.getSelToGYYValue(number);
                   values = "<input type=\"text\"  readonly name=\""
                       + oid
                       + "_selected_toggy\"  id=\""
                       + veroid
                       + "_selected_toggy\" value=\""
                       + oldValue
                       + "\"/>";
               }
               NmTableGUIComponent gui = new NmTableGUIComponent(values);
               guicomponentarrayMain.addGUIComponent(gui);
               return guicomponentarrayMain;
           } else if ("selectDepartment".equals(columnName)) {
                GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
                guicomponentarrayMain.setValueHidden(false);
                WfAssignmentState state = wi.getStatus();
                String oid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
                ReferenceFactory rf = new ReferenceFactory();
                String veroid = null;
                Versioned version = null;
                String selected = "";
                if (obj instanceof ChangePackaged) {
                    veroid = rf.getReference((ChangePackaged) obj).toString();
                } else {
                    QueryResult qr = VersionControlHelper.service.allVersionsFrom((Versioned) obj);
                    if (qr.hasMoreElements()) {
                        version = (Versioned) qr.nextElement();
                        veroid = rf.getReference(version).toString();
                    }
                }
                veroid = veroid.replaceAll(">", ":");
                String values = "<input type=\"text\"  readonly name=\""
                        + oid
                        + "_selected_department\"  id=\""
                        + veroid
                        + "_selected_department\" value=\""
                        + selected
                        + "\"/><input type=\"button\" value=\"选择\" onclick=\"javascript:setSelDepartment(this,'"+veroid+"_selected_department');\"  id=\""
                        + veroid + "_selecPer\"/>";
                if("COMPLETED".equals(state.toString())){
                    String number = "";
                    if(obj instanceof WTDocument) {
                        number = ((WTDocument)obj).getNumber();
                    } else if(obj instanceof EPMDocument) {
                        number = ((EPMDocument)obj).getNumber();
                    }
                    String oldValue = ReleaseDataAdvisBackHelper.getSelDepartValue(number);
                    //仅针对149正式发放包流程数据处理，查询非电子分发部门的数据。
                    if("149正式发放包流程".equals(processName)||"149变更签审包工艺会签".equals(processName)) {
                    	oldValue= ReleaseDataAdvisBackHelper.getSelDepartValueNoSdType(number,ReleaseDataAdvisBackHelper.SEND_DEPARTMENT_ELECTRONIC);
                    }
                   
                    values = "<input type=\"text\"  readonly name=\""
                        + oid
                        + "_selected_department\"  id=\""
                        + veroid
                        + "_selected_department\" value=\""
                        + oldValue
                        + "\"/>";
                }
                NmTableGUIComponent gui = new NmTableGUIComponent(values);
                guicomponentarrayMain.addGUIComponent(gui);
                return guicomponentarrayMain;
            } else if ("selectElectronicDepartment".equals(columnName)) {
                GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
                guicomponentarrayMain.setValueHidden(false);
                WfAssignmentState state = wi.getStatus();
                String oid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
                ReferenceFactory rf = new ReferenceFactory();
                String veroid = null;
                Versioned version = null;
                String selected = "";
                if (obj instanceof ChangePackaged) {
                    veroid = rf.getReference((ChangePackaged) obj).toString();
                } else {
                    QueryResult qr = VersionControlHelper.service.allVersionsFrom((Versioned) obj);
                    if (qr.hasMoreElements()) {
                        version = (Versioned) qr.nextElement();
                        veroid = rf.getReference(version).toString();
                    }
                }
                veroid = veroid.replaceAll(">", ":");
                String values = "<input type=\"text\"  readonly name=\""
                        + oid
                        + "_selected_elec_department\"  id=\""
                        + veroid
                        + "_selected_elec_department\" value=\""
                        + selected
                        + "\"/><input type=\"button\" value=\"选择\" onclick=\"javascript:setSelElectricDepartment(this,'"+veroid+"_selected_elec_department');\"  id=\""
                        + veroid + "_selecElecPer\"/>";
                if("COMPLETED".equals(state.toString())){
                    String number = "";
                    if(obj instanceof WTDocument) {
                        number = ((WTDocument)obj).getNumber();
                    } else if(obj instanceof EPMDocument) {
                        number = ((EPMDocument)obj).getNumber();
                    }
                    //获取电子发送部门的数据
                    String oldValue = ReleaseDataAdvisBackHelper.getSelDepartValue(number,ReleaseDataAdvisBackHelper.SEND_DEPARTMENT_ELECTRONIC);
                    values = "<input type=\"text\"  readonly name=\""
                        + oid
                        + "_selected_elec_department\"  id=\""
                        + veroid
                        + "_selected_elec_department\" value=\""
                        + oldValue
                        + "\"/>";
                }
                NmTableGUIComponent gui = new NmTableGUIComponent(values);
                guicomponentarrayMain.addGUIComponent(gui);
                return guicomponentarrayMain;
            } else if ("showSelectDepartment".equals(columnName)) {
                GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
                guicomponentarrayMain.setValueHidden(false);
                String oid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
                String veroid = null;
                String number = "";
                if(obj instanceof WTDocument) {
                    number = ((WTDocument)obj).getNumber();
                } else if(obj instanceof EPMDocument) {
                    number = ((EPMDocument)obj).getNumber();
                }
                String oldValue = ReleaseDataAdvisBackHelper.getSelDepartValue(number);
                String values = "<input type=\"text\"  readonly name=\""
                    + oid
                    + "_showselected_department\"  id=\""
                    + veroid
                    + "_showselected_department\" value=\""
                    + oldValue
                    + "\"/>";
                NmTableGUIComponent gui = new NmTableGUIComponent(values);
                guicomponentarrayMain.addGUIComponent(gui);
                return guicomponentarrayMain;
            }else if ("selectPrintDepartment".equals(columnName)) {
                GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
                guicomponentarrayMain.setValueHidden(false);
                WfAssignmentState state = wi.getStatus();
                String oid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
                ReferenceFactory rf = new ReferenceFactory();
                String veroid = null;
                Versioned version = null;
                String selected = "";
                if (obj instanceof ChangePackaged) {
                    veroid = rf.getReference((ChangePackaged) obj).toString();
                } else {
                    QueryResult qr = VersionControlHelper.service.allVersionsFrom((Versioned) obj);
                    if (qr.hasMoreElements()) {
                        version = (Versioned) qr.nextElement();
                        veroid = rf.getReference(version).toString();
                    }
                }
                veroid = veroid.replaceAll(">", ":");
                String values = "<input type=\"button\" value=\"选择\" onclick=\"javascript:setSelDepartment(this,'"+veroid+"', '_selected_department' );\"  id=\""
                        + veroid + "_selecPer\"/><input type=\"text\"  readonly name=\""
                        + oid
                        + "_selected_department\"  id=\""
                        + veroid
                        + "_selected_department\" value=\""
                        + selected
                        + "\"/>";
                if("COMPLETED".equals(state.toString())||!((WfActivity) wi.getSource().getObject()).getName().equals("设置打印分发信息")){
                    String number = "";
                    if(obj instanceof WTDocument) {
                        number = ((WTDocument)obj).getNumber();
                    } else if(obj instanceof EPMDocument) {
                        number = ((EPMDocument)obj).getNumber();
                    }
                    String oldValue = PrintDistributionHelper.getSelDepartValue(number);
                    values = "<input type=\"text\"  readonly name=\""
                        + oid
                        + "_selected_department\"  id=\""
                        + veroid
                        + "_selected_department\" value=\""
                        + oldValue
                        + "\"/>";
                }
                NmTableGUIComponent gui = new NmTableGUIComponent(values);
                guicomponentarrayMain.addGUIComponent(gui);
                return guicomponentarrayMain;
            }else if ("stamp".equals(columnName)) {
                GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
                guicomponentarrayMain.setValueHidden(false);
                WfAssignmentState state = wi.getStatus();
                String oid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
                ReferenceFactory rf = new ReferenceFactory();
                String veroid = null;
                Versioned version = null;
                String selected = "";
                if (obj instanceof ChangePackaged) {
                    veroid = rf.getReference((ChangePackaged) obj).toString();
                } else {
                    QueryResult qr = VersionControlHelper.service.allVersionsFrom((Versioned) obj);
                    if (qr.hasMoreElements()) {
                        version = (Versioned) qr.nextElement();
                        veroid = rf.getReference(version).toString();
                    }
                }
                veroid = veroid.replaceAll(">", ":");
                String values = "<input type=\"button\" value=\"设置\" onclick=\"javascript:setSelyinzhang(this,'"+veroid+"','_selected_yinzhang');\"  id=\""
                        + veroid + "_selecPer\"/><input type=\"text\"  readonly name=\""
                        + oid
                        + "_selected_yinzhang\"  id=\""
                        + veroid
                        + "_selected_yinzhang\" value=\""
                        + selected
                        + "\"/>";
                if("COMPLETED".equals(state.toString())||!((WfActivity) wi.getSource().getObject()).getName().equals("设置打印分发信息")){
                    String number = "";
                    if(obj instanceof WTDocument) {
                        number = ((WTDocument)obj).getNumber();
                    } else if(obj instanceof EPMDocument) {
                        number = ((EPMDocument)obj).getNumber();
                    }
                    String oldValue = PrintDistributionHelper.getSelYinZhangValue(number);
                    values = "<input type=\"text\"  readonly name=\""
                        + oid
                        + "_selected_yinzhang\"  id=\""
                        + veroid
                        + "_selected_yinzhang\" value=\""
                        + oldValue
                        + "\"/>";
                }
                NmTableGUIComponent gui = new NmTableGUIComponent(values);
                guicomponentarrayMain.addGUIComponent(gui);
                return guicomponentarrayMain;
            }
        }else if(obj instanceof WTChangeOrder2){
        	 NmCommandBean cb = mc.getNmCommandBean();
             WorkItem wi = (WorkItem) cb.getPageOid().getRefObject();
             if ("selectPrintDepartment".equals(columnName)) {
                 GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
                 guicomponentarrayMain.setValueHidden(false);
                 WfAssignmentState state = wi.getStatus();
                 String oid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
                 ReferenceFactory rf = new ReferenceFactory();
                 String veroid = null;
                 Versioned version = null;
                 String selected = "";
                 if (obj instanceof ChangePackaged) {
                     veroid = rf.getReference((ChangePackaged) obj).toString();
                 } else {
                     QueryResult qr = VersionControlHelper.service.allVersionsFrom((Versioned) obj);
                     if (qr.hasMoreElements()) {
                         version = (Versioned) qr.nextElement();
                         veroid = rf.getReference(version).toString();
                     }
                 }
                 veroid = veroid.replaceAll(">", ":");
                 String values = "<input type=\"button\" value=\"选择\" onclick=\"javascript:setSelDepartment(this,'"+veroid+"', '_selected_department' );\"  id=\""
                         + veroid + "_selecPer\"/><input type=\"text\"  readonly name=\""
                         + oid
                         + "_selected_department\"  id=\""
                         + veroid
                         + "_selected_department\" value=\""
                         + selected
                         + "\"/>";
                 if("COMPLETED".equals(state.toString())||!((WfActivity) wi.getSource().getObject()).getName().equals("设置打印分发信息")){
                     String number = "";
                     if(obj instanceof WTDocument) {
                         number = ((WTDocument)obj).getNumber();
                     } else if(obj instanceof EPMDocument) {
                         number = ((EPMDocument)obj).getNumber();
                     }
                     String oldValue = PrintDistributionHelper.getSelDepartValue(number);
                     values = "<input type=\"text\"  readonly name=\""
                         + oid
                         + "_selected_department\"  id=\""
                         + veroid
                         + "_selected_department\" value=\""
                         + oldValue
                         + "\"/>";
                 }
                 NmTableGUIComponent gui = new NmTableGUIComponent(values);
                 guicomponentarrayMain.addGUIComponent(gui);
                 return guicomponentarrayMain;
             }else if("stamp".equals(columnName)){

                 GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
                 guicomponentarrayMain.setValueHidden(false);
                 WfAssignmentState state = wi.getStatus();
                 String oid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
                 ReferenceFactory rf = new ReferenceFactory();
                 String veroid = null;
                 Versioned version = null;
                 String selected = "";
                 if (obj instanceof ChangePackaged) {
                     veroid = rf.getReference((ChangePackaged) obj).toString();
                 } else {
                     QueryResult qr = VersionControlHelper.service.allVersionsFrom((Versioned) obj);
                     if (qr.hasMoreElements()) {
                         version = (Versioned) qr.nextElement();
                         veroid = rf.getReference(version).toString();
                     }
                 }
                 veroid = veroid.replaceAll(">", ":");
                 String values = "<input type=\"button\" value=\"设置\" onclick=\"javascript:setSelyinzhang(this,'"+veroid+"','_selected_yinzhang');\"  id=\""
                         + veroid + "_selecPer\"/><input type=\"text\"  readonly name=\""
                         + oid
                         + "_selected_yinzhang\"  id=\""
                         + veroid
                         + "_selected_yinzhang\" value=\""
                         + selected
                         + "\"/>";
                 if("COMPLETED".equals(state.toString())||!((WfActivity) wi.getSource().getObject()).getName().equals("设置打印分发信息")){
                     String number = "";
                     if(obj instanceof WTDocument) {
                         number = ((WTDocument)obj).getNumber();
                     } else if(obj instanceof EPMDocument) {
                         number = ((EPMDocument)obj).getNumber();
                     }
                     String oldValue = PrintDistributionHelper.getSelYinZhangValue(number);
                     values = "<input type=\"text\"  readonly name=\""
                         + oid
                         + "_selected_yinzhang\"  id=\""
                         + veroid
                         + "_selected_yinzhang\" value=\""
                         + oldValue
                         + "\"/>";
                 }
                 NmTableGUIComponent gui = new NmTableGUIComponent(values);
                 guicomponentarrayMain.addGUIComponent(gui);
                 return guicomponentarrayMain;
             }
        }else if(obj instanceof CmPrintRecordInfoBean){
        	CmPrintRecordInfoBean cmPrintRecordInfoBean = (CmPrintRecordInfoBean) obj;
        	 if ("fileNumber".equals(columnName)) {
        		 String fileNumber = cmPrintRecordInfoBean.getFileNumber();
        		 return fileNumber;
        	 }else if("fileName".equals(columnName)){
        		 String fileName = cmPrintRecordInfoBean.getFileName();
        		 return fileName;
        	 }else if("docVersion".equals(columnName)){
        		 String docVersion = cmPrintRecordInfoBean.getDocVersion();
        		 return docVersion;
        	 }else if("phaseCode".equals(columnName)){
        		 String phaseCode = cmPrintRecordInfoBean.getPhaseCode();
        		 return phaseCode;
        	 }else if("secret".equals(columnName)){
        		 String secret = cmPrintRecordInfoBean.getSecret();
        		 return secret;
        	 }else if("disMessage".equals(columnName)){
        		 String disMessage = cmPrintRecordInfoBean.getDisMessage();
        		 return disMessage;
        	 }
        }
        return "";
    }

    public static void setSelSignValue2(String oid, String values) throws WTException, SQLException {
	    ReferenceFactory rf = new ReferenceFactory();
	    Object obj =  rf.getReference(oid).getObject();
	    WfProcess process = null;
	    if(obj instanceof WorkItem){
	    	WorkItem item = (WorkItem) obj;
	    	WfActivity activity = (WfActivity) item.getSource().getObject();
    	    process = activity.getParentProcess();
	    } else if(obj instanceof WfProcess){
	    	process = (WfProcess) obj;
	    }
	    String newValus = values;
	    String[] datas = values.split("@");
        for (String value : datas) {
            if (value != null && !"".equals(value)) {
                String[] str = value.split("~");
                String objOid = str[0];
                Persistable per = WCUtil.getPersistable(objOid);
                String number = "";
                if(per instanceof WTDocument){
                	WTDocument doc = (WTDocument) per;
                	number = doc.getNumber();
                	newValus = newValus.replaceAll(objOid+"~", number+"~");
                }
            }
        }
        System.out.println("newValus=="+newValus);
		ProcessData data = process.getContext();
		data.setValue("outSignInfos", newValus);
		PersistenceHelper.manager.save(process);
    }
}
