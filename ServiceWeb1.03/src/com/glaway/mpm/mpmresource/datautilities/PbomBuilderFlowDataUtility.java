package com.glaway.mpm.mpmresource.datautilities;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.inf.container.WTContainer;
import wt.part.WTPart;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.workflow.engine.WfActivity;
import wt.workflow.work.WfAssignmentState;
import wt.workflow.work.WorkItem;


import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.LoadConfig;
import com.glaway.mpm.util.NmTableGUIComponent;
import com.glaway.mpm.util.PbomUtil;
import com.glaway.mpm.util.WorkflowUtil;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;

public class PbomBuilderFlowDataUtility extends AbstractDataUtility {

    @SuppressWarnings("unused")
    public Object getDataValue(String columnName, Object obj, ModelContext mc) throws WTException {
    	String oid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
//        String workItemOid = mc.getNmCommandBean().getRequestData().getParameterMap().get("oid").toString();
//        String activityName = WorkflowUtil.getActivityNameByWorkItemOid(workItemOid);
        ReferenceFactory rf = new ReferenceFactory();
//        String name ="";
//        WorkItem wi= (WorkItem) rf.getReference(workItemOid).getObject();
//        WfAssignmentState state = wi.getStatus();
//        WfActivity wfAct = (WfActivity) wi.getSource().getObject();
//        Object pbo = wfAct.getContext().getValue("primaryBusinessObject");
        GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
        guicomponentarrayMain.setValueHidden(false);
        Versioned version = null;
        String veroid = null;

        QueryResult qr = VersionControlHelper.service.allVersionsFrom((Versioned) obj);
		if (qr.hasMoreElements()) {
		         version = (Versioned) qr.nextElement();
		         veroid = rf.getReference(version).toString();
		     }
		String poid  = String.valueOf(PersistenceHelper.getObjectIdentifier((Persistable) obj).getId());

        veroid = veroid.replaceAll(">", ":");
        WTPart part = (WTPart)obj;
        WTContainer container = part.getContainer();
        String zhuzhichejian = null;
        String fuzhichejian = null;
        IBAHelper helper = new IBAHelper(part);
        if ("ZZBM".equals(columnName)) { //主制车间
        		    zhuzhichejian = helper.getIBAValue(part, "ZZBM");
                    Map<String,String> users = PbomUtil.getAllCheJianAndXiangMuBuMapGYZZ(container,PbomUtil.GROUP_GYRWFG_NAME);
                    StringBuffer sb = new StringBuffer();
                    sb.append("<select  " + "name=\"zzcj_value\" value='"+zhuzhichejian+"' onChange=\"handleZzcj(this)\">");
                    Set<Entry<String, String>> useSets= users.entrySet();

                    if(zhuzhichejian==null||"null".equals(zhuzhichejian)||"".equals(zhuzhichejian)){
                    	  sb.append("<option value=\"\" selected>--请选择--</option>");
                    }else{
                    	 sb.append("<option value=\"\" >--请选择--</option>");
                    }


                    for (Iterator<Entry<String, String>> iterator = useSets.iterator(); iterator.hasNext();) {
                        Entry<String, String> entry = iterator.next();
                        if(zhuzhichejian!=null&&zhuzhichejian.contains(entry.getKey())){
                            sb.append("<option value=\""+entry.getKey()+"\" selected>");
                            sb.append(entry.getKey());
                            sb.append("</option>");
                        }else{
                            sb.append("<option value=\""+entry.getKey()+"\">");
                            sb.append(entry.getKey());
                            sb.append("</option>");
                        }

                    }
                    sb.append("</select>");
                    sb.append("<input type=\"hidden\" name=\"poid\" value=\""+poid+"\" />");
                    String value = sb.toString();
                    NmTableGUIComponent gui = new NmTableGUIComponent(value);
                    guicomponentarrayMain.addGUIComponent(gui);
                    return guicomponentarrayMain;
                } else if ("FZBM".equals(columnName)) { //辅制车间
                	fuzhichejian  = helper.getIBAValue(part,"FZBM");
                    Map<String,String> users = PbomUtil.getAllCheJianAndXiangMuBuMapGYZZ(container,PbomUtil.GROUP_GYRWFG_NAME);
                    StringBuffer sb = new StringBuffer();
                    Set<Entry<String, String>> useSets= users.entrySet();

                    for (Iterator<Entry<String, String>> iterator = useSets.iterator(); iterator.hasNext();) {

                        Entry<String, String> entry = iterator.next();
                        if (fuzhichejian!=null&&fuzhichejian.contains(entry.getKey())) {
                            sb.append("<input type=\"checkbox\" checked  name=\"fzcj_"+entry.getKey()+"_value\" value='"+entry.getKey()+"' onclick=\"handleFzcj(this,'fzcj_"+entry.getKey()+"_value')\">"+entry.getKey()+"</input>");
                        } else {
                            sb.append("<input type=\"checkbox\"  name=\"fzcj_"+entry.getKey()+"_value\" value='"+entry.getKey()+"' onclick=\"handleFzcj(this,'fzcj_"+entry.getKey()+"_value')\">"+entry.getKey()+"</input>");
                        }

                    }
                    sb.append("<input type=\"hidden\" name=\"fzcj_value\" value=\""+fuzhichejian+"\" />");
                    String value = sb.toString();

                    NmTableGUIComponent gui = new NmTableGUIComponent(value);
                    guicomponentarrayMain.addGUIComponent(gui);
                    return guicomponentarrayMain;
                }else if("CTYPE".equals(columnName)){//物料类型
        		    String ctype = helper.getIBAValue(part, "CTYPE");
                    String[] wltypes = LoadConfig.getInstance().getPartType();
                    StringBuffer sb = new StringBuffer();
                    sb.append("<select  name=\"ctype_value\" value='"+ctype+"' onChange=\"handleCtype(this)\">");
                    sb.append("<option value=\"\">--请选择--</option>");
                    for(int i=0;i<wltypes.length;i++){
                        if(wltypes[i].equals(ctype)){
                            sb.append("<option value=\""+wltypes[i]+"\" selected>");
                            sb.append(wltypes[i]);
                            sb.append("</option>");
                        }else{
                            sb.append("<option value=\""+wltypes[i]+"\" >");
                            sb.append(wltypes[i]);
                            sb.append("</option>");
                        }

                    }
                    sb.append("</select>");

                    sb.append("<input type=\"hidden\" name=\"poid\" value=\""+poid+"\" />");

                    String value = sb.toString();
                    NmTableGUIComponent gui = new NmTableGUIComponent(value);
                    guicomponentarrayMain.addGUIComponent(gui);
                }
            	return guicomponentarrayMain;
        }


}
