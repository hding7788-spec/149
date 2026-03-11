package ext.casc.workflow.tree;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import wt.change2.WTChangeOrder2;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.inf.container.WTContainer;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignmentState;
import wt.workflow.work.WorkItem;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.ases.changepackaged.ChangePackaged;
import ext.casc.constants.Constants;
import ext.casc.util.NmTableGUIComponent;
import ext.casc.workflow.WfUtil;
import ext.casc.workflow.signtrue.zp.HuiQianWorkFlowService;
import ext.casc.workflow.signtrue.zp.SignatureGYYXMLParser;
import ext.casc.workflow.signtrue.zp.SignatureGYZZXMLParser;
import ext.casc.workflow.signtrue.zp.SignatureRecord;
import ext.casc.workflow.signtrue.zp.SignatureService;

public class SignatureEpmsDataUtility extends AbstractDataUtility {

    @SuppressWarnings("unused")
    public Object getDataValue(String columnName, Object obj, ModelContext mc) throws WTException {
      //  if(flag){
    	String oid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
        SignatureGYZZXMLParser sxp = null;
        SignatureGYYXMLParser sxp2 = null;
        String workItemOid = mc.getNmCommandBean().getRequestData().getParameterMap().get("oid").toString();
        String activityName = HuiQianWorkFlowService.getActivityNameByWorkItemOid(workItemOid);
        ReferenceFactory rf = new ReferenceFactory();
        String name ="";
        WorkItem wi= (WorkItem) rf.getReference(workItemOid).getObject();
        WfAssignmentState state = wi.getStatus();
        WfActivity wfAct = (WfActivity) wi.getSource().getObject();
        Object pbo = wfAct.getContext().getValue("primaryBusinessObject");
    //  }


        /*
         * 为了开权限
         */
        // 切换系统管理员
        WTUser currentuser = (WTUser) SessionHelper.manager.getPrincipal();
        String zpr = currentuser.getPersistInfo().getObjectIdentifier().toString();
       // WTUser admin = (WTUser) SessionHelper.manager.setAdministrator();
//        try {
//            AccessUtil.setObjectAccess((Persistable) obj, currentuser);
//        } catch (WTPropertyVetoException e) {
//            e.printStackTrace();
//        } finally {
//            WTUser user = (WTUser) SessionHelper.manager.setPrincipal(currentuser.getAuthenticationName());
//        }
        GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
        guicomponentarrayMain.setValueHidden(false);
        Versioned version = null;
        String veroid = null;
        if (obj instanceof ChangePackaged) {
        	veroid = rf.getReference((ChangePackaged)obj).toString();
		}else{
			 QueryResult qr = VersionControlHelper.service.allVersionsFrom((Versioned) obj);
		        if (qr.hasMoreElements()) {
		            version = (Versioned) qr.nextElement();
		            veroid = rf.getReference(version).toString();
		        }
		}
        veroid = veroid.replaceAll(">", ":");
        if(activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG)||activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN)){
            InputStream is = SignatureService.getAttachmentsFromPBO((ContentHolder)pbo, ContentRoleType.SECONDARY, "signature_emps.xml");
            if (is !=null) {
            	sxp = new SignatureGYZZXMLParser(is);
			}

            if (obj instanceof EPMDocument||obj instanceof WTPart||obj instanceof WTDocument|| obj instanceof WTChangeOrder2 || obj instanceof ChangePackaged) {
            	SignatureRecord record =null;
            	if ( sxp!=null) {
            		record = sxp.getSignatureRecordByEPMOid(oid+"_"+zpr);
            	}
            	String number = "";
            	WTContainer container = null;
            	if (obj instanceof EPMDocument) {
           		    container =(( EPMDocument)obj).getContainer();
           		    number = (( EPMDocument)obj).getNumber();
	           	 }else if(obj instanceof WTDocument){
	           		 container =(( WTDocument)obj).getContainer();
	           		number = (( WTDocument)obj).getNumber();
	           	 }else if(obj instanceof WTChangeOrder2){
	           		 container =(( WTChangeOrder2)obj).getContainer();
	           		number = (( WTChangeOrder2)obj).getNumber();
	           	 }else if(obj instanceof ChangePackaged){
	           		 container =(( ChangePackaged)obj).getContainer();
	           		number = (( ChangePackaged)obj).getNumber();
	           	 }
            	//回显保存后的信息
            	String key = workItemOid+"_"+oid+"_sign_person";
            	String key2 = workItemOid+"_"+oid+"_sign_person_value";
            	String key3 = workItemOid+"_"+oid+"_sign_person_valueA";
            	String key4 = workItemOid+"_"+oid+"_sign_person_valueB";
                String oldValue = getOldValue(mc, key);
                String oldValue2 = getOldValue(mc, key2);
                String zzcjValue = getOldValue(mc, key3);
                String fzcjValue = getOldValue(mc, key4);

            	if (columnName.equals("sign_person")) {//工艺会签人员:工艺组长、工艺员
                    String persons = "";
                    String personsDis = "";
                    if(record!=null){
                        persons = record.getPersons();
                        personsDis = record.getPersonsDis();
                    }

                    //回显保存后的信息
                    if ((oldValue != null)&&(!"".equals(oldValue))&&!"COMPLETED".equals(state.toString())) {
                        persons = oldValue2;
                        personsDis = oldValue;
                    }

                    //String value = "<input type=\"button\" value=\"设置会签人员\" onclick=\"javascript:alert(11);\" id=\"" + oid + "_person\"/>";
                    String value = "<input type=\"text\"  readonly name=\"" + oid + "_sign_person\"  id=\"" + veroid + "_sign_person\" value=\""+personsDis+"\"/><input type=\"button\" value=\"选择\" onclick=\"javascript:showHQRY('"+veroid+"');\"  id=\"" + veroid + "_selecPer\"/><input type=\"hidden\"   name=\"" + oid + "_sign_person_value\" id=\"" + veroid + "_sign_person_value\" value=\""+persons+"\">";
                   // String value = "<input type=\"text\"  readonly  id=\"" + oid + "_sign_person\" value=\"\"/><input type=\"button\" value=\"选择\" onclick=\"javascript:showHQRY('"+oid+"');\"  id=\"" + oid + "_selecPer\"/><input type=\"button\" value=\"清空\" onclick=\"javascript:clearHQRY('"+oid+"');\"/><input type=\"hidden\"  value=''  id=\"" + oid + "_sign_person_value\" value=\"\">";
                    if("COMPLETED".equals(state.toString())){
                        value = "<input type=\"text\"  readonly  name=\"" + oid + "_sign_person\" id=\"" + veroid + "_sign_person\" value=\""+personsDis+"\"/><input type=\"hidden\"   id=\"" + veroid + "_sign_person_value\" value=\""+persons+"\">";
                    }
                    value = value + addrelated2DDRW(obj,oid,veroid);
                    NmTableGUIComponent gui = new NmTableGUIComponent(value);
                    guicomponentarrayMain.addGUIComponent(gui);
                    return guicomponentarrayMain;
                }else if ("zhuzhichejian".equals(columnName)) {
                    String zhuzhichejian = "";
                    if(record!=null){
                    	zhuzhichejian = record.getZhuzhichejian();
                    }
                    //回显保存后的信息
                    if ((zzcjValue != null)&&(!"".equals(zzcjValue))&&!"COMPLETED".equals(state.toString())) {
                        zhuzhichejian = zzcjValue;
                    }
                   // Map<String,String> users = SignatureService.getAllCheJianAndXiangMuBuMapGYZZ(container);

                    Map<String,String> users = (Map<String,String>)  mc.getNmCommandBean().getRequest().getAttribute("allZhuZhiChejian");
                    StringBuffer sb = new StringBuffer();
                    if(users!=null){
                    	sb.append("<select id=\"" +veroid +"_sign_person_value\" " + "name=\""+oid +"_sign_person_valueA\" value='"+zhuzhichejian+"' onChange=\"selectUser2(this,'"+veroid+"')\">");//getReference取到Version oid
                        Set<Entry<String, String>> useSets= users.entrySet();

                        sb.append("<option value=\"\"/>");
                        for (Iterator<Entry<String, String>> iterator = useSets.iterator(); iterator.hasNext();) {

                            Entry<String, String> entry = iterator.next();

                            if(zhuzhichejian.contains(entry.getValue())){
                                sb.append("<option value=\""+entry.getKey()+"-"+entry.getValue()+"\" selected>");
                                sb.append(entry.getKey());
                                sb.append("</option>");
                            }else{
                                sb.append("<option value=\""+entry.getKey()+"-"+entry.getValue()+"\">");
                                sb.append(entry.getKey());
                                sb.append("</option>");
                            }

                        }
                        sb.append("</select>");
                    }

                    String value = sb.toString();
                    NmTableGUIComponent gui = new NmTableGUIComponent(value);
                    guicomponentarrayMain.addGUIComponent(gui);
                    return guicomponentarrayMain;
                } else if ("fuzhichejian".equals(columnName)) {
                	String fuzhichejian = "";
                    if(record!=null){
                    	fuzhichejian = record.getFuzhichejian();
                    }
                    //回显保存后的信息
                    if ((fzcjValue != null)&&(!"".equals(fzcjValue))&&!"COMPLETED".equals(state.toString())) {
                        fuzhichejian = fzcjValue;
                    }
                   // Map<String,String> users = SignatureService.getAllCheJianAndXiangMuBuMapGYZZ(container);
                    Map<String,String> users = (Map<String,String>) mc.getNmCommandBean().getRequest().getAttribute("allFuZhiChejian");
                    StringBuffer sb = new StringBuffer();
                    if(users!=null){
                    	 Set<Entry<String, String>> useSets= users.entrySet();
                         for (Iterator<Entry<String, String>> iterator = useSets.iterator(); iterator.hasNext();) {

                             Entry<String, String> entry = iterator.next();
                             String vkey = entry.getKey();
                             if (fuzhichejian.contains(vkey+"-"+entry.getValue())) {//如果是工艺更改或临时工艺，则显示上一次选择的辅制车间记录
                                 sb.append("<input type=\"checkbox\" checked id=\""+veroid+"_sign_person_value"+vkey+"\" name=\""+oid+"_sign_person_valueB\" value='"+vkey+"-"+entry.getValue()+"' onChange=\"selectUser3(this,'"+veroid+"')\">"+vkey+"；</input>");
                             } else {
                                 sb.append("<input type=\"checkbox\" id=\""+veroid+"_sign_person_value"+vkey+"\" name=\""+oid+"_sign_person_valueB\" value='"+vkey+"-"+entry.getValue()+"' onChange=\"selectUser3(this,'"+veroid+"')\">"+vkey+"；</input>");
                             }

                         }
                    }


                    String value = sb.toString();
                    value = value + addrelated2DDRW(obj,oid,veroid);
                    value = value +"<input type=\"hidden\"   name=\"" + oid + "_number\" id=\"" + veroid + "_number\" value=\""+number+"\">";
                    NmTableGUIComponent gui = new NmTableGUIComponent(value);
                    guicomponentarrayMain.addGUIComponent(gui);
                    return guicomponentarrayMain;
                }
            }
        }else if(activityName.equals(Constants.ACTIVITYNAME_ZPGYHQ)||activityName.equals("指派工艺员")||activityName.equals("指派物资会签")){
            InputStream isOld = SignatureService.getAttachmentsFromPBO((ContentHolder) pbo, ContentRoleType.SECONDARY, "signature_emps2_old.xml");
            InputStream	is = SignatureService.getAttachmentsFromPBO((ContentHolder) pbo, ContentRoleType.SECONDARY, "signature_emps2.xml");
            sxp2 = new SignatureGYYXMLParser(is);
            SignatureGYYXMLParser sxpOld = new SignatureGYYXMLParser(isOld);
            if (obj instanceof EPMDocument||obj instanceof WTDocument|| obj instanceof WTChangeOrder2 || obj instanceof ChangePackaged) {
            	String number = "";
            	if (obj instanceof EPMDocument) {
           		    number = (( EPMDocument)obj).getNumber();
	           	 }else if(obj instanceof WTDocument){
	           		number = (( WTDocument)obj).getNumber();
	           	 }else if(obj instanceof WTChangeOrder2){
	           		number = (( WTChangeOrder2)obj).getNumber();
	           	 }else if(obj instanceof ChangePackaged){
	           		number = (( ChangePackaged)obj).getNumber();
	           	 }
                if (columnName.equals("sign_person")) {//工艺会签人员:工艺组长、工艺员
                    SignatureRecord record = sxp2.getSignatureRecordByEPMOid(oid+"_"+zpr);
                    if(record==null){
                    	record = sxpOld.getSignatureRecordByEPMOid(oid+"_"+zpr);
                    }
                    String persons = "";
                    String personsDis = "";
                    if(record!=null){
                        persons = record.getPersons();
                        personsDis = record.getPersonsDis();
                    }
                    //String value = "<input type=\"button\" value=\"设置会签人员\" onclick=\"javascript:alert(11);\" id=\"" + oid + "_person\"/>";
                   // String value = "<input type=\"text\"  readonly  id=\"" +  veroid + "_sign_person\" value=\""+personsDis+"\"/><input type=\"button\" value=\"选择\" onclick=\"javascript:showHQRY('"+veroid+"');\"  id=\"" +  veroid + "_selecPer\"/><input type=\"button\" value=\"清空\" onclick=\"javascript:clearHQRY('"+ veroid+"');\"/><input type=\"hidden\"   id=\"" +  veroid + "_sign_person_value\" value=\""+persons+"\">";
                    String value = "<input type=\"text\"  readonly name=\"" + oid + "_sign_person\"  id=\"" + veroid + "_sign_person\" value=\""+personsDis+"\"/><input type=\"button\" value=\"选择\" onclick=\"javascript:showHQRY('"+veroid+"');\"  id=\"" + veroid + "_selecPer\"/><input type=\"hidden\"   name=\"" + oid + "_sign_person_value\" id=\"" + veroid + "_sign_person_value\" value=\""+persons+"\">";
                    // String value = "<input type=\"text\"  readonly  id=\"" + oid + "_sign_person\" value=\"\"/><input type=\"button\" value=\"选择\" onclick=\"javascript:showHQRY('"+oid+"');\"  id=\"" + oid + "_selecPer\"/><input type=\"button\" value=\"清空\" onclick=\"javascript:clearHQRY('"+oid+"');\"/><input type=\"hidden\"  value=''  id=\"" + oid + "_sign_person_value\" value=\"\">";
                     if("COMPLETED".equals(state.toString())){
                         value = "<input type=\"text\"  readonly  name=\"" + oid + "_sign_person\" id=\"" + veroid + "_sign_person\" value=\""+personsDis+"\"/><input type=\"hidden\"   id=\"" + veroid + "_sign_person_value\" value=\""+persons+"\">";
                     }
                     value = value + addrelated2DDRW(obj,oid,veroid);
                     value = value +"<input type=\"hidden\"   name=\"" + oid + "_number\" id=\"" + veroid + "_number\" value=\""+number+"\">";
                    NmTableGUIComponent gui = new NmTableGUIComponent(value);
                    guicomponentarrayMain.addGUIComponent(gui);
                    return guicomponentarrayMain;
                }
            }
        }
        return guicomponentarrayMain;
    }

    private String addrelated2DDRW(Object obj,String oid,String veroid){
    	String value = "";
   	 	String drwveroid = "";
    	if(obj instanceof EPMDocument){
    		EPMDocument dRW2D = SignatureService.getLastestIter2DesignDocs((EPMDocument)obj);
    		if(dRW2D!=null){
        		String drwoid = PersistenceHelper.getObjectIdentifier( dRW2D).toString();
        		drwveroid = SignatureService.getVersionId(obj);
        		value = "<input type=\"hidden\"   name=\"" + oid + "_related2DDRW\"  id=\"" + veroid + "_related2DDRW\" value=\""+drwoid+"\"/>";
    		}else{
    			value = "<input type=\"hidden\"   name=\"" + oid + "_related2DDRW\"  id=\"" + veroid + "_related2DDRW\" value=\"\"/>";
    		}
    	}else{
    		value = "<input type=\"hidden\"   name=\"" + oid + "_related2DDRW\"  id=\"" + veroid + "_related2DDRW\" value=\"\"/>";
    	}
    	return value;
    }

    public ComboBox getComboBox(String id,String name ,ArrayList<String> display,
            ArrayList<String> internal, String selected, boolean flag) {
        ComboBox combobox = new ComboBox();
        combobox.setName(name);
        combobox.setId(id);
        combobox.setValues(display);
        combobox.setInternalValues(internal);
        combobox.setSelected(selected);
        combobox.setEditable(flag);
        return combobox;
    }

    private static String getOldValue(ModelContext mc,String key) {
        try {
            NmCommandBean commandBean = mc.getNmCommandBean();
            HttpServletRequest request = commandBean.getRequest();
            String workItemOid = request.getParameter("oid");
            ReferenceFactory rf = new ReferenceFactory();
            WorkItem wi = (WorkItem) rf.getReference(workItemOid).getObject();
            if("COMPLETED".equals(wi.getStatus().toString())){
                return "";
            }
            WfActivity wfAct = (WfActivity) wi.getSource().getObject();
            WfProcess process = wfAct.getParentProcess();
            InputStream is = WfUtil.getAttachByWfProcess(process);
            if (is != null) {
                Properties pro = new Properties();
                pro.load(is);
                is.close();
                if (pro.get(key)==null) {
                    return "";
                }else {
                    return String.valueOf(pro.get(key));
                }
            }

        } catch (WTRuntimeException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
