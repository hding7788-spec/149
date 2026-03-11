package com.glaway.mpm.processplan;

import com.glaway.mpm.model.ProcessEditorBean;
import com.glaway.mpm.print.PrintUserCodeProcessor;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import ext.casc.ixb.ReleaseDataAdvisBackHelper;
import ext.casc.util.NmTableGUIComponent;
import ext.casc.util.WCUtil;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.workflow.work.WfAssignmentState;
import wt.workflow.work.WorkItem;

import java.sql.SQLException;
import java.util.ArrayList;

public class MpmplanReleaseDataUtility extends AbstractDataUtility {
	private  static ArrayList<String> PESTARTPRAMS = new ArrayList<String>();
	static{
		PESTARTPRAMS.add("");
		PESTARTPRAMS.add("1024MB");
		PESTARTPRAMS.add("2048MB");
		PESTARTPRAMS.add("3072MB");
		PESTARTPRAMS.add("4096MB");
		PESTARTPRAMS.add("5120MB");
		PESTARTPRAMS.add("6144MB");
		PESTARTPRAMS.add("7168MB");
		PESTARTPRAMS.add("8192MB");
		PESTARTPRAMS.add("10240MB");
		PESTARTPRAMS.add("12288MB");
		PESTARTPRAMS.add("14336MB");
		PESTARTPRAMS.add("18000MB");
		PESTARTPRAMS.add("20000MB");
		PESTARTPRAMS.add("24000MB");
		PESTARTPRAMS.add("28000MB");
		PESTARTPRAMS.add("30000MB");
		PESTARTPRAMS.add("36000MB");
	}

    @Override
    public Object getDataValue(String columnName, Object obj, ModelContext mc) throws WTException {

        if(obj instanceof ProcessEditorBean){
        	GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
            guicomponentarrayMain.setValueHidden(false);
			ProcessEditorBean processEditorBean = (ProcessEditorBean) obj;
			if(columnName.equals("userName")){
//				StringInputComponent strInput = new StringInputComponent();
//				strInput.setEditable(true);
//				strInput.setName(columnName);
//				strInput.setValue(processEditorBean.getUserName());
				String userName = processEditorBean.getUserName();
				return userName;
			}else if(columnName.equals("swt")){
				String swt = processEditorBean.getSwt() + "位";

//				StringBuffer sb = new StringBuffer();
//				List<String> testList = new ArrayList<String>();
//				testList.add("");
//				testList.add("32");
//				testList.add("64");
//				sb.append("<select name=\"" + swt +"_select\" " + "id=\""+ swt +"_select\">");
//				for(int i = 0 ; i < testList.size() ; i++){
//					String tempStr = testList.get(i);
//					if(swt.equals(tempStr)){
//						sb.append("<option value=\""+tempStr+"\" selected=\"selected\">");
//						sb.append(tempStr);
//						sb.append("</option>");
//					}else{
//						sb.append("<option value=\""+tempStr+"\">");
//						sb.append(tempStr);
//						sb.append("</option>");
//					}
//				}
//				sb.append("</select>");
//				String value = sb.toString();
//				//StringInputComponent strInput = new StringInputComponent(value);
//				NmTableGUIComponent gui = new NmTableGUIComponent(value);
//				guicomponentarrayMain.addGUIComponent(gui);
//
//				return guicomponentarrayMain;
				ComboBox comboBox = new ComboBox();
				ArrayList<String> list = new ArrayList<String>();
				list.add("");
				list.add("32位");
				list.add("64位");
				comboBox.setEditable(true);
				comboBox.setName(columnName);
				comboBox.setValues(list);
				comboBox.setSelected(swt);
				return comboBox;
			}else if(columnName.equals("jwsRuntimeParameters")){
				String jwsRuntimeParameters = processEditorBean.getJwsRuntimeParameters() + "MB";
//				StringBuffer sb = new StringBuffer();
//				List<String> testList = new ArrayList<String>();
//				testList.add("");
//				testList.add("1024");
//				testList.add("2048");
//				testList.add("3072");
//				testList.add("4096");
//				sb.append("<select name=\"" + jwsRuntimeParameters +"_select\" " + "id=\""+ jwsRuntimeParameters +"_select\">");
//				for(int i = 0 ; i < testList.size() ; i++){
//					String tempStr = testList.get(i);
//					if(jwsRuntimeParameters.equals(tempStr)){
//						sb.append("<option value=\""+tempStr+"\" selected=\"selected\">");
//						sb.append(tempStr);
//						sb.append("</option>");
//					}else{
//						sb.append("<option value=\""+tempStr+"\">");
//						sb.append(tempStr);
//						sb.append("</option>");
//					}
//				}
//				sb.append("</select>");
//				String value = sb.toString();
//				//StringInputComponent strInput = new StringInputComponent(value);
//				NmTableGUIComponent gui = new NmTableGUIComponent(value);
//				guicomponentarrayMain.addGUIComponent(gui);
//
//				return guicomponentarrayMain;
				ComboBox comboBox = new ComboBox();

				comboBox.setEditable(true);
				comboBox.setName(columnName);
				comboBox.setValues(PESTARTPRAMS);
				comboBox.setSelected(jwsRuntimeParameters);
				return comboBox;
			}
		}else if(obj instanceof WTUser){
			WTUser user = (WTUser) obj;
			GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
            guicomponentarrayMain.setValueHidden(false);
			if(columnName.equals("userName")){
//				StringInputComponent strInput = new StringInputComponent();
//				strInput.setEditable(true);
//				strInput.setName(columnName);
//				strInput.setValue(user.getName());
				return user.getName();
			}else if(columnName.equals("swt")){
//				StringBuffer sb = new StringBuffer();
//				List<String> testList = new ArrayList<String>();
//				testList.add("");
//				testList.add("32");
//				testList.add("64");
//				sb.append("<select name=\"swt_select\" " + "id=\"_select\">");
//				for(int i = 0 ; i < testList.size() ; i++){
//					String tempStr = testList.get(i);
//					if("".equals(tempStr)){
//						sb.append("<option value=\""+tempStr+"\" selected=\"selected\">");
//						sb.append(tempStr);
//						sb.append("</option>");
//					}else{
//						sb.append("<option value=\""+tempStr+"\">");
//						sb.append(tempStr);
//						sb.append("</option>");
//					}
//				}
//				sb.append("</select>");
//				String value = sb.toString();
//				//StringInputComponent strInput = new StringInputComponent(value);
//				NmTableGUIComponent gui = new NmTableGUIComponent(value);
//				guicomponentarrayMain.addGUIComponent(gui);
//
//				return guicomponentarrayMain;
				ComboBox comboBox = new ComboBox();
				ArrayList<String> list = new ArrayList<String>();
				list.add("");
				list.add("32位");
				list.add("64位");
				comboBox.setEditable(true);
				comboBox.setName(columnName);
				comboBox.setValues(list);
				comboBox.setSelected(list.get(0));
				return comboBox;
			}else if(columnName.equals("jwsRuntimeParameters")){
//				StringBuffer sb = new StringBuffer();
//				List<String> testList = new ArrayList<String>();
//				testList.add("");
//				testList.add("1024");
//				testList.add("2048");
//				testList.add("3072");
//				testList.add("4096");
//				sb.append("<select name=\"jwsRuntimeParameters_select\" " + "id=\"jwsRuntimeParameters_select\">");
//				for(int i = 0 ; i < testList.size() ; i++){
//					String tempStr = testList.get(i);
//					if("".equals(tempStr)){
//						sb.append("<option value=\""+tempStr+"\" selected=\"selected\">");
//						sb.append(tempStr);
//						sb.append("</option>");
//					}else{
//						sb.append("<option value=\""+tempStr+"\">");
//						sb.append(tempStr);
//						sb.append("</option>");
//					}
//				}
//				sb.append("</select>");
//				String value = sb.toString();
//				//StringInputComponent strInput = new StringInputComponent(value);
//				NmTableGUIComponent gui = new NmTableGUIComponent(value);
//				guicomponentarrayMain.addGUIComponent(gui);
//
//				return guicomponentarrayMain;
				ComboBox comboBox = new ComboBox();

				comboBox.setEditable(true);
				comboBox.setName(columnName);
				comboBox.setValues(PESTARTPRAMS);
				comboBox.setSelected(PESTARTPRAMS.get(0));
				return comboBox;
			}
		}else{
			NmCommandBean cb = mc.getNmCommandBean();
	        WorkItem wi = (WorkItem) cb.getPageOid().getRefObject();
	        String wiOid = cb.getPageOid().getOid().getStringValue();
	        if ("selectDepartment".equals(columnName)) {
	            GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
	            guicomponentarrayMain.setValueHidden(false);
	            WfAssignmentState state = wi.getStatus();
	            String oid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
	            ReferenceFactory rf = new ReferenceFactory();
	            String veroid = null;
	            Versioned version = null;
	            String selected = "";
	            QueryResult qr = VersionControlHelper.service.allVersionsFrom((Versioned) obj);
	            if (qr.hasMoreElements()) {
	                version = (Versioned) qr.nextElement();
	                veroid = rf.getReference(version).toString();
	            }
	            veroid = veroid.replaceAll(">", ":");
	            String values = "<input type=\"text\"  readonly name=\""
	                    + oid
	                    + "_selected_department\"  id=\""
	                    + veroid
	                    + "_selected_department\" value=\""
	                    + selected
	                    + "\"/><input type=\"button\" value=\"选择\" onclick=\"javascript:setSelDepartment(this,'"+veroid+"');\"  id=\""
	                    + veroid + "_selecPer\"/>";
	            if("COMPLETED".equals(state.toString())){
	                String number = "";
	                if(obj instanceof WTPart) {
	                    number = ((WTPart)obj).getNumber();
	                } else if(obj instanceof MPMProcessPlan) {
	                	number = ((MPMProcessPlan)obj).getNumber();
	                } else if(obj instanceof WTChangeOrder2) {
	                	number = ((WTChangeOrder2)obj).getNumber();
	                }
	                String oldValue = ReleaseDataAdvisBackHelper.getSelDepartValue2(number);
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
	        } else if ("showSelectDepartment".equals(columnName)) {
	            GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
	            guicomponentarrayMain.setValueHidden(false);
	            String oid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
	            String veroid = null;
	            String number = "";
	            if(obj instanceof WTPart) {
	                number = ((WTPart)obj).getNumber();
	            } else if(obj instanceof MPMProcessPlan) {
	            	number = ((MPMProcessPlan)obj).getNumber();
	            } else if(obj instanceof WTChangeOrder2) {
	            	number = ((WTChangeOrder2)obj).getNumber();
	            }
	            String oldValue = ReleaseDataAdvisBackHelper.getSelDepartValue2(number);
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
	        }else if("showSignInfo".equals(columnName)){
	       	 GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
	       	 if(obj instanceof WTChangeOrder2){
	       	   return "";
	         }
	         guicomponentarrayMain.setValueHidden(false);
	         String oid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
	         String veroid = null;
	         String number = "";
	         if(obj instanceof WTPart) {
	             number = ((WTPart)obj).getNumber();
	         } else if(obj instanceof MPMProcessPlan) {
	         	number = ((MPMProcessPlan)obj).getNumber();
	         } else if(obj instanceof WTChangeOrder2) {
	         	number = ((WTChangeOrder2)obj).getNumber();
	         }else if(obj instanceof WTDocument) {
	         	number = ((WTDocument)obj).getNumber();
	         }
	         String oldValue = "";
			try {
				oldValue = ReleaseDataAdvisBackHelper.getSelSignValue2(wiOid);
			} catch (SQLException e) {

				e.printStackTrace();
			}
			if(oldValue == null){
				oldValue = "";
			}
	        WfAssignmentState state = wi.getStatus();
	        ReferenceFactory rf = new ReferenceFactory();
	        Versioned version = null;
	        String selected = getSignVlue(oldValue,oid);
	        if(selected == null){
	        	selected = "";
	        }
	        QueryResult qr = VersionControlHelper.service.allVersionsFrom((Versioned) obj);
	        if (qr.hasMoreElements()) {
	            version = (Versioned) qr.nextElement();
	            veroid = rf.getReference(version).toString();
	        }
	        veroid = veroid.replaceAll(">", ":");
	        String values = "<input type=\"text\"  readonly name=\""
	                + oid
	                + "_selected_sign_info\"  id=\""
	                + veroid
	                + "_selected_sign_info\" value=\""
	                + selected
	                + "\"/><input type=\"button\" value=\"查看\" onclick=\"javascript:setSignInfo(this,'false','"+veroid+"');\"  id=\""
	                + veroid + "_signInfo\"/>";
	        NmTableGUIComponent gui = new NmTableGUIComponent(values);
	        guicomponentarrayMain.addGUIComponent(gui);
	        return guicomponentarrayMain;
	    }else if("addSignInfo".equals(columnName)){
	        GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
	        guicomponentarrayMain.setValueHidden(false);
	        if(obj instanceof WTChangeOrder2){
	        	 return "";
	        }
	        WfAssignmentState state = wi.getStatus();
	        String oid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
	        ReferenceFactory rf = new ReferenceFactory();
	        String veroid = null;
	        Versioned version = null;
	//        String selected = "";
	        String oldValue = "";
			try {
				oldValue = ReleaseDataAdvisBackHelper.getSelSignValue2(wiOid);
			} catch (SQLException e) {

				e.printStackTrace();
			}
			if(oldValue == null){
				oldValue = "";
			}
	        String selected = getSignVlue(oldValue,oid);
	        QueryResult qr = VersionControlHelper.service.allVersionsFrom((Versioned) obj);
	        if (qr.hasMoreElements()) {
	            version = (Versioned) qr.nextElement();
	            veroid = rf.getReference(version).toString();
	        }
	        veroid = veroid.replaceAll(">", ":");
	        String values = "<input type=\"text\"  readonly name=\""
	                + oid
	                + "_selected_sign_info\"  id=\""
	                + veroid
	                + "_selected_sign_info\" value=\""
	                + selected
	                + "\"/><input type=\"button\" value=\"选择\" onclick=\"javascript:setSignInfo(this,'true','"+veroid+"');\"  id=\""
	                + veroid + "_signInfo\"/>";
	        if("COMPLETED".equals(state.toString())){
	            String number = "";
	            if(obj instanceof WTPart) {
	                number = ((WTPart)obj).getNumber();
	            } else if(obj instanceof MPMProcessPlan) {
	            	number = ((MPMProcessPlan)obj).getNumber();
	            } else if(obj instanceof WTChangeOrder2) {
	            	number = ((WTChangeOrder2)obj).getNumber();
	            }
	            values = "<input type=\"text\"  readonly name=\""
	                + oid
	                + "_selected_department\"  id=\""
	                + veroid
	                + "_selected_department\" value=\""
	                + oldValue
	                + "\"/>";
	        }
	       // wt.change2.WTChangeOrder2:3593247
	        //com.ptc.windchill.mpml.processplan.MPMProcessPlan:3361523
	       // com.ptc.windchill.mpml.processplan.MPMProcessPlan:3361518
		        NmTableGUIComponent gui = new NmTableGUIComponent(values);
		        guicomponentarrayMain.addGUIComponent(gui);
		        return guicomponentarrayMain;
	    	} else if("phaseCode".equals(columnName)){
	    		if(obj instanceof MPMProcessPlan) {
	    			ReferenceFactory rf = new ReferenceFactory();
	            	String oid = rf.getReferenceString((Persistable) obj);
		       		String phaseCode = PrintUserCodeProcessor.queryPhaseCodeByOid(oid);
		       		return phaseCode;
	            } else if(obj instanceof WTChangeOrder2) {
	            	//YGRZ273-10051
	            	//String number = ((WTChangeOrder2) obj).getNumber();
	            	ReferenceFactory rf = new ReferenceFactory();
	            	String oid = rf.getReferenceString((Persistable) obj);
		       		String phaseCode = PrintUserCodeProcessor.queryPhaseCodeByOid(oid);
		       		return phaseCode;
	            }else if(obj instanceof WTDocument) {
	            	//String oid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
	            	ReferenceFactory rf = new ReferenceFactory();
	            	String oid = rf.getReferenceString((Persistable) obj);
		       		String phaseCode = PrintUserCodeProcessor.queryPhaseCodeByOid(oid);
		       		return phaseCode;
	            }

	    	} else if("secret".equals(columnName)){
	    		if(obj instanceof MPMProcessPlan) {
	    			ReferenceFactory rf = new ReferenceFactory();
	            	String oid = rf.getReferenceString((Persistable) obj);
	    			String secret = PrintUserCodeProcessor.querySecretByOid(oid);
	       		 	return secret;
	            } else if(obj instanceof WTChangeOrder2) {
	            	ReferenceFactory rf = new ReferenceFactory();
	            	String oid = rf.getReferenceString((Persistable) obj);
	    			String secret = PrintUserCodeProcessor.querySecretByOid(oid);
	       		 	return secret;
	            }else if(obj instanceof WTDocument) {
	            	ReferenceFactory rf = new ReferenceFactory();
	            	String oid = rf.getReferenceString((Persistable) obj);
	    			String secret = PrintUserCodeProcessor.querySecretByOid(oid);
	       		 	return secret;
	            }
	    	} else if("distributeRecord".equals(columnName)){
	    		if(obj instanceof MPMProcessPlan) {
	    			ReferenceFactory rf = new ReferenceFactory();
	            	String oid = rf.getReferenceString((Persistable) obj);
	    			String distributeRecord = PrintUserCodeProcessor.queryDistributeRecordByOid(oid);
	       		 	return distributeRecord;
	            } else if(obj instanceof WTChangeOrder2) {
	            	ReferenceFactory rf = new ReferenceFactory();
	            	String oid = rf.getReferenceString((Persistable) obj);
	    			String distributeRecord = PrintUserCodeProcessor.queryDistributeRecordByOid(oid);
	       		 	return distributeRecord;
	            }else if(obj instanceof WTDocument) {
	            	ReferenceFactory rf = new ReferenceFactory();
	            	String oid = rf.getReferenceString((Persistable) obj);
	            	//String oid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
	    			String distributeRecord = PrintUserCodeProcessor.queryDistributeRecordByOid(oid);
	       		 	return distributeRecord;
	            }
	    	} else if("printStatus".equals(columnName)){
	    		if(obj instanceof MPMProcessPlan) {
	    			ReferenceFactory rf = new ReferenceFactory();
	            	String oid = rf.getReferenceString((Persistable) obj);
	    			String printStatus = PrintUserCodeProcessor.queryPrintStatusByOid(oid);
	       		 	return printStatus;
	            } else if(obj instanceof WTChangeOrder2) {
	            	ReferenceFactory rf = new ReferenceFactory();
	            	String oid = rf.getReferenceString((Persistable) obj);
	    			String printStatus = PrintUserCodeProcessor.queryPrintStatusByOid(oid);
	       		 	return printStatus;
	            }else if(obj instanceof WTDocument) {
	            	//String oid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
	            	ReferenceFactory rf = new ReferenceFactory();
	            	String oid = rf.getReferenceString((Persistable) obj);
	    			String printStatus = PrintUserCodeProcessor.queryPrintStatusByOid(oid);
	       		 	return printStatus;
	            }
	    	}
		}
	        return "";

    }

    public static String getSignVlue(String values,String oid) throws WTException{
		String value = "";
		 String[] data = values.split("@");
	        for (String oneValue : data) {
	            if (oneValue != null && !"".equals(oneValue)) {
	                String[] str = oneValue.split("~");
	                String objNumber = str[0];
	                Persistable per = WCUtil.getPersistable(oid);
                    String number = "";
                    if(per instanceof WTDocument){
                    	WTDocument doc = (WTDocument) per;
                    	number = doc.getNumber();
                    }else if(per instanceof WTChangeOrder2){
                    	WTChangeOrder2 ecn = (WTChangeOrder2) per;
                    	number = ecn.getNumber();
                    }
	                if(objNumber.equals(number)){
	                	value = str[1];
	                	break;
	                }
	            }
	        }
		return value;

	}

}
