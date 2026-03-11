package ext.casc.ecn;

import com.glaway.mpm.processplan.helper.ProcessPlanHelper;
import com.glaway.mpm.util.*;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.factory.dataUtilities.AttributeDataUtilityHelper;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.components.rendering.guicomponents.StringInputComponent;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.type.common.TypeInstance;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.MPMProcessPlanHelper;
import ext.casc.change.CSCChange;
import ext.casc.integrate.util.ZipUtil;
import ext.casc.sop.constants.SopConstants;
import ext.casc.util.IBAHelper;
import ext.casc.util.NmTableGUIComponent;
import org.dom4j.Document;
import org.dom4j.Element;
import wt.associativity.NCServerHolder;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.part.WTPart;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.beans.PropertyVetoException;
import java.io.File;
import java.rmi.RemoteException;
import java.util.*;



public class AddAttriubutesDatautility extends AbstractDataUtility {

	public Object getDataValue(String componentId, Object object, ModelContext context) {
		try {
			if ("GONGYIWENJIANMULUNUM".equals(componentId)) {

				return gongYiMuLuNums(componentId, object, context);

			} else if ("ECNTYPE".equals(componentId)) {
				return getEcnType(componentId, object, context);
			} /*else if("CHANGETYPE".equals(componentId)){
				String cName = AttributeDataUtilityHelper.getColumnName(componentId, object, context);
		        String colName = context.getNmCommandBean().getCompContext()+"___"+cName+"_col_"+cName+"___textbox";
				String str = (String) getChangeType(componentId, object, context);
				StringBuilder sb = new StringBuilder();
	            sb.append("<input type=\"text\" hidden=\"true\" readonly name=\""+colName+"\"  id=\"" + componentId + "\" value=\""+ str + "\"/>" );
	            GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
	    		guicomponentarrayMain.setValueHidden(true);
	    		 NmTableGUIComponent gui = new NmTableGUIComponent(sb.toString());
	             guicomponentarrayMain.addGUIComponent(gui);
				return guicomponentarrayMain;
			}*/ else if("SECRET".equals(componentId)){
				String cName = AttributeDataUtilityHelper.getColumnName(componentId, object, context);
		        String colName = context.getNmCommandBean().getCompContext()+"___"+cName+"_col_"+cName+"___textbox";
				String str = getSecret(componentId, object, context);
				if("null".equals(str)){
					str = "";
				}
				StringBuilder sb = new StringBuilder();
	            sb.append("<input type=\"text\"  readonly name=\""+colName+"\"  id=\"" + componentId + "\" value=\""+ str + "\"/>" );
	            GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
	    		guicomponentarrayMain.setValueHidden(false);
	    		 NmTableGUIComponent gui = new NmTableGUIComponent(sb.toString());
	             guicomponentarrayMain.addGUIComponent(gui);
				return guicomponentarrayMain;
			} else if("CHANGEBEFORINFOR".equals(componentId)){
				String cName = AttributeDataUtilityHelper.getColumnName(componentId, object, context);
		        String colName = context.getNmCommandBean().getCompContext()+"___"+cName+"_col_"+cName+"___textbox";
				String str = getIBAInfor(componentId, object, context, "CHANGEBEFORINFOR");
				StringBuilder sb = new StringBuilder();
	           // sb.append("&nbsp<input type=\"checkbox\"   id=\"" + "cappCheckbox" + "\"  onclick=\"isCapp(this,'"+componentId+"')\">"+"CAPP更改</input>");
	            if(str!=null&&!"".equals(str)){
					sb.append("<textarea type=\"text\"   name=\""+colName+"\"  id=\"" + componentId + "\" value=\""+ str + "\" style=\"width:397px; height:60px\">"+str+"</textarea>");

		            sb.append("&nbsp<input type=\"checkbox\" checked  id=\"" + "cappCheckbox" + "\"  onclick=\"isCapp(this,'"+componentId+"')\">"+"CAPP更改</input>");
	            }else{
					sb.append("<textarea type=\"text\"  readOnly=\"true\" name=\""+colName+"\"  id=\"" + componentId + "\" value=\""+ str + "\" style=\"width:397px; height:60px\">"+str+"</textarea>");

		            sb.append("&nbsp<input type=\"checkbox\"   id=\"" + "cappCheckbox" + "\"  onclick=\"isCapp(this,'"+componentId+"')\">"+"CAPP更改</input>");
	            }
	            GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
	    		guicomponentarrayMain.setValueHidden(false);
	    		NmTableGUIComponent gui = new NmTableGUIComponent(sb.toString());
	            guicomponentarrayMain.addGUIComponent(gui);
				return guicomponentarrayMain;
			} else if("PHASE_CODE".equals(componentId)){
				String cName = AttributeDataUtilityHelper.getColumnName(componentId, object, context);
		        String colName = context.getNmCommandBean().getCompContext()+"___"+cName+"_col_"+cName+"___textbox";
				String str = getPhaseCode(componentId, object, context);
				if("null".equals(str)){
					str = "";
				}
				StringBuilder sb = new StringBuilder();
	            sb.append("<input type=\"text\"  readonly name=\""+colName+"\"  id=\"" + componentId + "\" value=\""+ str + "\"/>" );
	            GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
	    		guicomponentarrayMain.setValueHidden(false);
	    		NmTableGUIComponent gui = new NmTableGUIComponent(sb.toString());
	            guicomponentarrayMain.addGUIComponent(gui);
				return guicomponentarrayMain;

			} else if("SETCHANGEORDER2INFOR".equals(componentId)){
				TypeIdentifier identifier = (TypeIdentifier)((TypeInstance)object).getIdentifier().getDefinitionIdentifier();
				String typename = identifier.getTypename();
				NmOid pageOid = context.getNmCommandBean().getPageOid();
				Object ob = pageOid.getRefObject();
				String objOid = PersistenceHelper.getObjectIdentifier((Persistable) ob).toString();
				StringBuilder sb = new StringBuilder();


				if (typename.contains("casc.sast.149.DOCUMENT_ECN")) {
					// 拼接包含 type 参数的链接，点击触发 window.open 方法
					sb.append("<a href=\"javascript:void(0);\" onclick=\"window.open('netmarkets/jsp/ext/ases/ecn/setChangeOrderInfor.jsp?oid=").append(objOid).append("&type=doc', '_blank');\">点击此处设置更改信息</a>");
				} else {
					// 拼接不包含 type 参数的链接，点击触发 window.open 方法
					sb.append("<a href=\"javascript:void(0);\" onclick=\"window.open('netmarkets/jsp/ext/ases/ecn/setChangeOrderInfor.jsp?oid=").append(objOid).append("', '_blank');\">点击此处设置更改信息</a>");
				}

				GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
	    		guicomponentarrayMain.setValueHidden(false);
	    		NmTableGUIComponent gui = new NmTableGUIComponent(sb.toString());
	            guicomponentarrayMain.addGUIComponent(gui);
				return guicomponentarrayMain;
			} else if("CHANGECAUSE".equals(componentId)){
				String cName = AttributeDataUtilityHelper.getColumnName(componentId, object, context);
		        String colName = context.getNmCommandBean().getCompContext()+"___"+cName+"_col_"+cName+"___textbox";
		        String str = getIBAInfor(componentId, object, context, "CHANGECAUSE");
				StringBuilder sb = new StringBuilder();
				sb.append("<textarea type=\"text\"  readOnly=\"true\" name=\""+colName+"\"  id=\"" + componentId + "\" value=\""+ str + "\" style=\"width:397px; height:60px\">"+str+"</textarea>");
				GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
	    		guicomponentarrayMain.setValueHidden(false);
	    		NmTableGUIComponent gui = new NmTableGUIComponent(sb.toString());
	            guicomponentarrayMain.addGUIComponent(gui);
				return guicomponentarrayMain;
			} else if("DESIGNCHANGENUM".equals(componentId)){
				String cName = AttributeDataUtilityHelper.getColumnName(componentId, object, context);
		        String colName = context.getNmCommandBean().getCompContext()+"___"+cName+"_col_"+cName+"___textbox";
		        String str = getIBAInfor(componentId, object, context, "DESIGNCHANGENUM");
				StringBuilder sb = new StringBuilder();
				sb.append("<textarea type=\"text\"  readOnly=\"true\" name=\""+colName+"\"  id=\"" + componentId + "\" value=\""+ str + "\" style=\"width:397px; height:60px\">"+str+"</textarea>");
				GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
	    		guicomponentarrayMain.setValueHidden(false);
	    		NmTableGUIComponent gui = new NmTableGUIComponent(sb.toString());
	            guicomponentarrayMain.addGUIComponent(gui);
				return guicomponentarrayMain;
			} else if("CHANGEBEFOR".equals(componentId)){
				String cName = AttributeDataUtilityHelper.getColumnName(componentId, object, context);
		        String colName = context.getNmCommandBean().getCompContext()+"___"+cName+"_col_"+cName+"___textbox";
		        String str = getIBAInfor(componentId, object, context, "CHANGEBEFOR");
				StringBuilder sb = new StringBuilder();
				sb.append("<textarea type=\"text\"  readOnly=\"true\" name=\""+colName+"\"  id=\"" + componentId + "\" value=\""+ str + "\" style=\"width:397px; height:60px\">"+str+"</textarea>");
				GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
	    		guicomponentarrayMain.setValueHidden(false);
	    		NmTableGUIComponent gui = new NmTableGUIComponent(sb.toString());
	            guicomponentarrayMain.addGUIComponent(gui);
				return guicomponentarrayMain;
			} else if("CHANGEAFTER".equals(componentId)){
				String cName = AttributeDataUtilityHelper.getColumnName(componentId, object, context);
		        String colName = context.getNmCommandBean().getCompContext()+"___"+cName+"_col_"+cName+"___textbox";
		        String str = getIBAInfor(componentId, object, context, "CHANGEAFTER");
				StringBuilder sb = new StringBuilder();
				sb.append("<textarea type=\"text\"  readOnly=\"true\" name=\""+colName+"\"  id=\"" + componentId + "\" value=\""+ str + "\" style=\"width:397px; height:60px\">"+str+"</textarea>");
				GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
	    		guicomponentarrayMain.setValueHidden(false);
	    		NmTableGUIComponent gui = new NmTableGUIComponent(sb.toString());
	            guicomponentarrayMain.addGUIComponent(gui);
				return guicomponentarrayMain;
			}else if("ISSOP".equals(componentId)){
				NmOid pageOid = context.getNmCommandBean().getPageOid();
				Object pageObj = pageOid.getRefObject();
				String cName = AttributeDataUtilityHelper.getColumnName(componentId, object, context);
				String colName = context.getNmCommandBean().getCompContext()+"___"+cName+"_col_"+cName+"___textbox";
				boolean isSop = false;
				String type;
				if (pageObj instanceof WTDocument) {
					WTDocument document = (WTDocument) pageObj;
					type = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(document);
					if (type.contains(SopConstants.SOP_TYPE_SOPDOC)) {
						isSop = true;
					}
				} else if (pageObj instanceof WTPart) {
					WTPart part = (WTPart) pageObj;
					type = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(part);
					if (type.contains(SopConstants.SOP_TYPE_SOPPART)) {
						isSop = true;
					}
				} else if (pageObj instanceof MPMProcessPlan) {
					MPMProcessPlan processPlan = (MPMProcessPlan) pageObj;
					type = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(processPlan);
					if (type.contains(SopConstants.SOP_TYPE_SOPPROCESSPLAN)) {
						isSop = true;
					}
				}
				String str = String.valueOf(isSop);
				StringBuilder sb = new StringBuilder();
				sb.append("<input type=\"text\"  readonly name=\""+colName+"\"  id=\"" + componentId + "\" value=\""+ str + "\"/>" );
				GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
				guicomponentarrayMain.setValueHidden(false);
				NmTableGUIComponent gui = new NmTableGUIComponent(sb.toString());
				guicomponentarrayMain.addGUIComponent(gui);
				return guicomponentarrayMain;
			} else if("ECRNUMBER".equals(componentId)){
				String cName = AttributeDataUtilityHelper.getColumnName(componentId, object, context);
				String colName = context.getNmCommandBean().getCompContext()+"___"+cName+"_col_"+cName+"___textbox";
				String str = getIBAInfor(componentId, object, context, "ECRNUMBER");
				StringBuilder sb = new StringBuilder();
				sb.append("<input type=\"text\" readonly name=\"" + colName + "\" id=\"" + componentId + "\" value=\"" + str + "\"/>");
				sb.append("<input type=\"button\" value=\"搜索\" onclick=\"javascript:setEcrNumber(this);\"  id=\"ecrNumber_selected\"/>");
				GUIComponentArray componentArray = new GUIComponentArray();
				componentArray.setValueHidden(false);
				NmTableGUIComponent gui = new NmTableGUIComponent(sb.toString());
				componentArray.addGUIComponent(gui);
				return componentArray;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}


	public static List<String> getGongYiWenJianMuLuNums(ModelContext context) throws WTException, RemoteException, WTPropertyVetoException {
		List<String> GongYiNums = new ArrayList<String>();
		MPMProcessPlan pplan = null;
		NmOid pageOid = context.getNmCommandBean().getPageOid();
		Object object = pageOid.getRefObject();

		if (object != null && object instanceof MPMProcessPlan) {
			pplan = (MPMProcessPlan) object;
		}else if(object != null && object instanceof WTDocument){
			WTDocument doc = (WTDocument) object;
			pplan = MPMProcessPlanUtil.getMPMProcessPlanByNumber(doc.getNumber());
		} else if (object != null && object instanceof WTChangeOrder2) {

			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) object;
			List cas = CSCChange.getReleatedCA(changeOrder2, false);
			for (int j = 0; j < cas.size(); j++) {
				WTChangeActivity2 ca = (WTChangeActivity2) cas.get(j);
				ArrayList<WTObject> befors = CSCChange.getCAAffectedData(ca);
				for (int i = 0; i < befors.size(); i++) {
					WTObject befor = befors.get(i);
					if (befor instanceof MPMProcessPlan) {
						pplan = (MPMProcessPlan) befor;
						break;
					}
				}
			}
		}
		if (pplan != null) {
			QueryResult qr = MPMProcessPlanHelper.service.getWTParts(pplan, NCServerHolder.makeForLatestConfigSpec());
			if (qr.hasMoreElements()) {
				WTPart part = (WTPart) qr.nextElement();
				getGongYiNumLoop(part, GongYiNums);
			}
		}
		Set set = new HashSet(GongYiNums);
		GongYiNums.clear();
		GongYiNums.addAll(set);
		return GongYiNums;
	}

	private static void getGongYiNumLoop(WTPart part, List<String> GongYiNums) throws WTException, RemoteException {
		List<WTDocument> documents = ProcessPlanHelper.getAllProcessZipDoc(part);
		String num = null;
		for (WTDocument doc : documents) {
			String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
			if (docType != null && docType.endsWith("reportTechnics")) {
				num = IBAHelper.getIBAStringValue(doc, "PPNUMBER");
				if (num!=null && num.contains("-WM-")) {
					GongYiNums.add(num);
					break;
				}
			}
		}
		List<WTPart> parts = new ArrayList<WTPart>();

		WTPartUtil.getAllParentParts(part,parts);
		if (parts != null && parts.size() > 0) {
			for (WTPart child : parts) {
				getGongYiNumLoop(child, GongYiNums);
			}
		}
	}

	public ComboBox getComboBox(String id, String name, ArrayList<String> display, ArrayList<String> internal, String selected, boolean flag) {
		ComboBox combobox = new ComboBox();
		combobox.setName(name);
		combobox.setId(id);
		combobox.setValues(display);
		combobox.setInternalValues(internal);
		combobox.setSelected(selected);
		combobox.setEditable(flag);
		return combobox;
	}

	private Object gongYiMuLuNums(String s, Object obj,
			ModelContext modelcontext) throws Exception {
		NmOid pageOid = modelcontext.getNmCommandBean().getPageOid();
		Object object = pageOid.getRefObject();
		String mulu = null;
		if(object != null && object instanceof WTChangeOrder2){
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) object;
			mulu = IBAHelper.getIBAStringValue(changeOrder2, "GONGYIWENJIANMULUNUM");
		}
		List<String> GongYiNums = getGongYiWenJianMuLuNums(modelcontext);
		String cName = AttributeDataUtilityHelper.getColumnName(s, obj, modelcontext);
        String colName = modelcontext.getNmCommandBean().getCompContext()+"___"+cName+"_col_"+cName+"___textbox";
		StringBuffer sb = new StringBuffer();
		if(mulu==null || "null".equals(mulu)){
			mulu = "";
		}
		if(GongYiNums.contains(mulu)){
			sb.append("<table><tr><td><select readonly=\"false\" name=\"" + colName + "\" id=\"" + s + "\" style=\"width:300px; height:20px; display:block\" >");

			for (int i = 0; i < GongYiNums.size(); i++) {
				String tempStr = GongYiNums.get(i);
				if(tempStr.equals(mulu)){
					sb.append("<option selected=\"selected\" value=\"" + tempStr + "\">");
				}else {
					sb.append("<option value=\"" + tempStr + "\">");
				}
				sb.append(tempStr);
				sb.append("</option>");
			}
			sb.append("</select></td>");
			sb.append("<td><input type=\"text\" disabled=\"true\"  name=\""+colName+"\"  id=\"handput_id\" value=\""+mulu+"\" style=\"display:none\"/></td>");
			sb.append("<td><input type=\"checkbox\"   id=\"" + "checkbox" + "\"  onclick=\"isHandPut(this,'handput_id')\">"+"手动输入</input></td></tr></table>");
		}else{
			sb.append("<table><tr><td><select disabled=\"true\" readonly=\"false\" name=\"" + colName + "\" id=\"" + s + "\" style=\"width:300px; height:20px; display:none\" >");

			for (int i = 0; i < GongYiNums.size(); i++) {
				String tempStr = GongYiNums.get(i);
				if(tempStr.equals(mulu)){
					sb.append("<option selected=\"selected\" value=\"" + tempStr + "\">");
				}else {
					sb.append("<option value=\"" + tempStr + "\">");
				}
				sb.append(tempStr);
				sb.append("</option>");
			}
			sb.append("</select></td>");
			sb.append("<td><input type=\"text\"  name=\""+colName+"\"  id=\"handput_id\" value=\""+mulu+"\" style=\"display:block\"/></td>");
			sb.append("<td><input type=\"checkbox\" checked  id=\"" + "checkbox" + "\"  onclick=\"isHandPut(this,'handput_id')\">"+"手动输入</input></td></tr></table>");
		}
		String value = sb.toString();
        GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
		guicomponentarrayMain.setValueHidden(false);
		 NmTableGUIComponent gui = new NmTableGUIComponent(value);
         guicomponentarrayMain.addGUIComponent(gui);
		return guicomponentarrayMain;

	}


	private Object getEcnType(String s, Object obj,
			ModelContext modelcontext) throws Exception {
		ArrayList<String> ecnTypes = new ArrayList<String>();
		NmOid pageOid = modelcontext.getNmCommandBean().getPageOid();
		Object object = pageOid.getRefObject();
		String value = "";
		if (object != null && object instanceof MPMProcessPlan) {
			ecnTypes.add("正常更改");
			ecnTypes.add("作废更改");
		}if (object != null && object instanceof WTDocument) {
			ecnTypes.add("正常更改");
			ecnTypes.add("作废更改");
		}else if(object != null && object instanceof WTPart){
			ecnTypes.add("新增更改");
		}else if(object != null && object instanceof WTChangeOrder2){
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) object;
			value = IBAHelper.getIBAStringValue(changeOrder2, "ECNTYPE");
			List cas = CSCChange.getReleatedCA(changeOrder2, false);
			for (int j = 0; j < cas.size(); j++) {
				WTChangeActivity2 ca = (WTChangeActivity2) cas.get(j);
				ArrayList<WTObject> befors = CSCChange.getCAAffectedData(ca);
				for (int i = 0; i < befors.size(); i++) {
					WTObject befor = befors.get(i);
					if (befor instanceof MPMProcessPlan) {
						ecnTypes.add("正常更改");
						ecnTypes.add("作废更改");
						break;
					}if (befor instanceof WTDocument) {
						ecnTypes.add("正常更改");
						ecnTypes.add("作废更改");
						break;
					}else if(befor instanceof WTPart){
						ecnTypes.add("新增更改");
					}
				}
			}
		}

		StringInputComponent stringinputcomponent = new StringInputComponent(s,
				ecnTypes, ecnTypes, false);
		stringinputcomponent.setValue(value);
		stringinputcomponent.setColumnName(AttributeDataUtilityHelper
				.getColumnName(s, obj, modelcontext));
		stringinputcomponent.setId((new StringBuilder()).append(
				AttributeDataUtilityHelper.getHtmlId(modelcontext
						.getDescriptor())).append(System.currentTimeMillis())
				.toString());
		stringinputcomponent.setRequired(true);
		return stringinputcomponent;
	}

	//获取更改标记
	private Object getChangeType(String s, Object obj,
			ModelContext modelcontext) throws WTException{
		NmOid pageOid = modelcontext.getNmCommandBean().getPageOid();
		Object object = pageOid.getRefObject();
		if (object != null && object instanceof MPMProcessPlan) {
			MPMProcessPlan pplan = (MPMProcessPlan) object;
			String TechNo = pplan.getNumber();
			String ecnBiaoJi =(String) IntfUtil.getPeRemoteMethodInvoke("getChangeBiaoJiByTechnics",
					new Class[] { String.class }, new Object[] { TechNo });
			if("Z".equals(ecnBiaoJi)){
				return "I";
			}else{
                return processChangeType(ecnBiaoJi);
			}
		}else if(object != null && object instanceof WTDocument){
			WTDocument pplan = (WTDocument) object;
			String TechNo = pplan.getNumber();
			String ecnBiaoJi =(String) IntfUtil.getPeRemoteMethodInvoke("getChangeBiaoJiByTechnics",
					new Class[] { String.class }, new Object[] { TechNo });
			if("Z".equals(ecnBiaoJi)){
				return "I";
			}else{
                return processChangeType(ecnBiaoJi);
			}
		}else if(object != null && object instanceof WTPart){
			return "Z";
		}else if(object != null && object instanceof WTChangeOrder2){
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) object;
            return IBAHelper.getIBAStringValue(changeOrder2, "CHANGETYPE");
		}

		return "";
	}

	private String processChangeType(String str){
		if("".equals(str)){
			return "I";
		}else if("I".equals(str)){
			return "II";
		}else if("II".equals(str)){
			return "III";
		}else if("III".equals(str)){
			return "IV";
		}else if("IV".equals(str)){
			return "V";
		}else if("V".equals(str)){
			return "VI";
		}else if("VI".equals(str)){
			return "VII";
		}else if("VII".equals(str)){
			return "VIII";
		}else if("VIII".equals(str)){
			return "IX";
		}else if("IX".equals(str)){
			return "X";
		}else if("X".equals(str)){
			return "XI";
		}else if("XI".equals(str)){
			return "XII";
		}else if("XII".equals(str)){
			return "XIII";
		}else if("XIII".equals(str)){
			return "XIV";
		}else if("XIV".equals(str)){
			return "XV";
		}else if("XV".equals(str)){
			return "XVI";
		}else if("XVI".equals(str)){
			return "XVII";
		}else{
			int n = Integer.valueOf(str)+1;
			return String.valueOf(n);
		}
	}


	private String getSecret(String s, Object obj, ModelContext modelcontext) throws WTException {
		NmOid pageOid = modelcontext.getNmCommandBean().getPageOid();
		Object object = pageOid.getRefObject();
		String secret = "";
		if (object != null && object instanceof MPMProcessPlan) {
			MPMProcessPlan pplan = (MPMProcessPlan) object;
			secret = IBAHelper.getIBAStringValue(pplan, "SECRET");
		} else if (object != null && object instanceof WTPart) {
			WTPart part = (WTPart) object;
			secret =  IBAHelper.getIBAStringValue(part, "SECRET");
		} else if (object != null && object instanceof WTChangeOrder2) {
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) object;
			secret = IBAHelper.getIBAStringValue(changeOrder2, "SECRET");
		} else if(object != null && object instanceof WTDocument){
			//TODO-SOP:SOP修改需部署
			WTDocument wtDocument = (WTDocument) object;
			secret = IBAHelper.getIBAStringValue(wtDocument, "SECRET");
		}
        if("".equals(secret) || "null".equals(secret) ||null==secret){
        	secret = "无";
        }
		return secret;
	}


	public String getIBAInfor(String s, Object obj, ModelContext modelcontext, String ibaName) throws WTException{
		NmOid pageOid = modelcontext.getNmCommandBean().getPageOid();
		Object object = pageOid.getRefObject();
		String beforInfor = null;
		if (object != null && object instanceof WTChangeOrder2) {
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) object;
			beforInfor = IBAHelper.getIBAStringValue(changeOrder2, ibaName);
		}
		if(beforInfor==null || "null".equals(beforInfor)){
		   beforInfor="";
		}
		return beforInfor;
	}

	public String getPhaseCode(String s, Object obj, ModelContext modelcontext) throws WTException{
		NmOid pageOid = modelcontext.getNmCommandBean().getPageOid();
		Object object = pageOid.getRefObject();
		String phaseCode = "";
		if (object != null && object instanceof MPMProcessPlan) {
			MPMProcessPlan beforPPlan = (MPMProcessPlan) object;
			WTDocument beforDoc = MPMProcessPlanUtil.getWTDocumentByProcessPlan(beforPPlan);
			phaseCode = IBAHelper.getIBAStringValue(beforDoc, "PHASE_CODE");
			if(phaseCode == null || "".equals(phaseCode)){
				byte[] bytes = null;
				if (beforDoc != null) {
					ApplicationData data;
					try {
						data = WTDocumentUtil.getPrimaryByDocument(beforDoc);
						bytes = WTDocumentUtil.applicationDataToByte(data);
						String fileName = data.getFileName();
						if (fileName.toLowerCase().endsWith(".zip")) {
							fileName = fileName.substring(0, fileName.length() - 4);
						}
						String filepath = PropertiesUtil.getTempPath() + File.separator + UUID.randomUUID() + File.separator + fileName; //主内容解压路径
						File dir = new File(filepath);
						if(!dir.exists()){
							dir.mkdirs();
						}
						ZipUtil.unZip(bytes, filepath);
						File xmlFile = new File(filepath + File.separator + fileName + ".xml");
						Document doc = XmlUtility.getDocument(xmlFile);
						Element rootElement = doc.getRootElement();
						Element technicElement = rootElement.element("QMFawTechnicsInfo");
						if(technicElement == null){
							technicElement = rootElement.element("XWReportTechnicsInfo");
						}
						phaseCode = technicElement.attributeValue("PHASE_CODE");
						//删除临时文件
						String tempDir = filepath.substring(0, filepath.lastIndexOf(fileName) - 1);
						FilesUtil.delAllFile(tempDir);
					} catch (WTException e) {
						e.printStackTrace();
					} catch (PropertyVetoException e) {
						e.printStackTrace();
					}
				}
			}
		} else if (object != null && object instanceof WTPart) {
			WTPart part = (WTPart) object;
			phaseCode =  IBAHelper.getIBAStringValue(part, "PHASE_CODE");
		} else if (object != null && object instanceof WTChangeOrder2) {
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) object;
			phaseCode =  IBAHelper.getIBAStringValue(changeOrder2, "PHASE_CODE");

		} else if (object != null && object instanceof WTDocument) {
			WTDocument beforDoc = (WTDocument) object;
			phaseCode = IBAHelper.getIBAStringValue(beforDoc, "PHASE_CODE");
			if (phaseCode == null || "".equals(phaseCode)) {
				byte[] bytes = null;
				if (beforDoc != null) {
					ApplicationData data;
					try {
						data = WTDocumentUtil.getPrimaryByDocument(beforDoc);
						bytes = WTDocumentUtil.applicationDataToByte(data);
						String fileName = data.getFileName();
						if (fileName.toLowerCase().endsWith(".zip")) {
							fileName = fileName.substring(0, fileName.length() - 4);
						}
						String filepath = PropertiesUtil.getTempPath() + File.separator + UUID.randomUUID() + File.separator + fileName; // 主内容解压路径
						File dir = new File(filepath);
						if (!dir.exists()) {
							dir.mkdirs();
						}
						ZipUtil.unZip(bytes, filepath);
						File xmlFile = new File(filepath + File.separator + fileName + ".xml");
						Document doc = XmlUtility.getDocument(xmlFile);
						Element rootElement = doc.getRootElement();
						Element technicElement = rootElement.element("QMFawTechnicsInfo");
						if (technicElement == null) {
							technicElement = rootElement.element("XWReportTechnicsInfo");
						}
						phaseCode = technicElement.attributeValue("PHASE_CODE");
						// 删除临时文件
						String tempDir = filepath.substring(0, filepath.lastIndexOf(fileName) - 1);
						FilesUtil.delAllFile(tempDir);
					} catch (WTException e) {
						e.printStackTrace();
					} catch (PropertyVetoException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if ("null".equals(phaseCode) || null == phaseCode) {
			phaseCode = "";
		}
		return phaseCode;
	}
}
