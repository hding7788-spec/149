package ext.casc.doc.dataUtility;

import com.glaway.mpm.util.IBAHelper;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.factory.dataUtilities.AttributeDataUtilityHelper;
import com.ptc.core.components.rendering.guicomponents.*;
import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.doc.technology.TechnicsTechnologyTreeHander;
import ext.casc.doc.technology.TechnologyBean;
import ext.casc.process.util.ProcessUtil;
import ext.casc.util.NmTableGUIComponent;
import wt.doc.WTDocument;
import wt.fc.ReferenceFactory;
import wt.part.WTPart;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;

import java.rmi.RemoteException;
import java.util.*;

public class TechnicsTechnologyDataUtility extends AbstractDataUtility{

	@Override
	public Object getDataValue(String componentId, Object object, ModelContext context) throws WTException {
		NmCommandBean nmcommandbean = context.getNmCommandBean();
		Object ret = createTechnicsTechnologyFields(componentId, nmcommandbean,object,context);
	return ret;
	}

	private Object createTechnicsTechnologyFields(String componentId, NmCommandBean nmcommandbean, Object object,ModelContext context) throws WTException{
		Object obj = null;
		if(object instanceof TechnologyBean){
			TechnologyBean technologyBean = (TechnologyBean) object;
			if(componentId.equals("childNumber")){
//				String childNumber = technologyBean.getChildNumber();
//				obj = getTextBox(componentId, childNumber, false, true);
				StringInputComponent strInput = new StringInputComponent();
				strInput.setEditable(true);
				strInput.setName(componentId);
				strInput.setValue(technologyBean.getChildNumber());
//				strInput.setEnabled(false);
				strInput.setEditable(false);
				strInput.setInputWidth(60);

				return strInput;
			}
			if(componentId.equals("childName")){
//				String childName = technologyBean.getChildName();
//				obj = getTextBox(componentId, childName, false, true);
				StringInputComponent strInput = new StringInputComponent();
				strInput.setEditable(true);
				strInput.setName(componentId);
				strInput.setValue(technologyBean.getChildName());
				return strInput;
			}
			if(componentId.equals("status")){
				if("0".equals(technologyBean.getStatus())){
					return "启用";
				}else if("1".equals(technologyBean.getStatus())){
					return "禁用";
				}
			}
		}else if(object instanceof WTDocument){
			WTDocument doc = (WTDocument) object;
			if(componentId.equals("SUBTYPE")){
				IBAHelper ibaHelper = new IBAHelper(doc);
				String subType = ibaHelper.getIBAValue("SUBTYPE");
				String style = TypeHelper.getLocalizedTypeString(doc, Locale.CHINA);
				String childName = TechnicsTechnologyTreeHander.getChildName(style, subType);
				System.out.println(subType);
				return childName;
			}
		}else{
			if(componentId.equals("SUBTYPE")) {
				ArrayList<String> valueList = new ArrayList<String>();
				NmOid nmOid = nmcommandbean.getActionOid();
			    Object o = nmOid.getRefObject();
			    String fatherEname = "";
			    if(o instanceof WTDocument){
			    	WTDocument document = (WTDocument) o;
			    	String docType="";
					try {
						docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(document);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	fatherEname = docType;
			    }else{
			    	fatherEname =  nmcommandbean.getComboBox().get("createType").toString();
			    }
				String fatherCname = TechnicsTechnologyTreeHander.getFatherCname(fatherEname);
				List<TechnologyBean> list = TechnicsTechnologyTreeHander.search(fatherCname,"0");
				for(int i = 0; i < list.size(); i++){
					String childName = list.get(i).getChildName();
					valueList.add(childName);
				}
//				obj = getComboBox(componentId, valueList, false, true);
				StringInputComponent stringinputcomponent = new StringInputComponent(componentId,valueList, valueList, false);
				stringinputcomponent.setColumnName(AttributeDataUtilityHelper.getColumnName(componentId, object, context));
				stringinputcomponent.setId((new StringBuilder()).append(AttributeDataUtilityHelper.getHtmlId(context.getDescriptor())).append(System.currentTimeMillis()).toString());
				stringinputcomponent.setRequired(true);
				return stringinputcomponent;
			} else if (componentId.equals("BATCH")) {
				String oid = nmcommandbean.getActionOid().getOid().toString();
				WTPart part = (WTPart) new ReferenceFactory().getReference(oid).getObject();
				IBAHelper partIba = new IBAHelper(part);
				String batch = "";
				if(partIba != null){
					batch = partIba.getIBAValue("BATCH");
				}else{
					batch = "";
				}
				ArrayList<String> batchList = new ArrayList<String>();
				batchList.add(batch);
				StringInputComponent stringinputcomponent = new StringInputComponent(componentId,batchList,batchList,false);
				stringinputcomponent.setValue(batch);
				stringinputcomponent.setColumnName(AttributeDataUtilityHelper.getColumnName(componentId, obj, context));
				stringinputcomponent.setId((new StringBuilder()).append(AttributeDataUtilityHelper.getHtmlId(context.getDescriptor())).append(System.currentTimeMillis()).toString());
				stringinputcomponent.setEditable(false);
				return stringinputcomponent;
//				obj = getTextBox(componentId, batch, false, false);
			} else if ("DEPT".equals(componentId)) {
				ArrayList<String> gongXuCheJian = null;
				try {
					gongXuCheJian = ProcessUtil.getGongXuCheJian();
					gongXuCheJian.add("科技发展部（工艺部）");
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				StringInputComponent stringinputcomponent = new StringInputComponent(componentId,gongXuCheJian, gongXuCheJian, false);
				stringinputcomponent.setColumnName(AttributeDataUtilityHelper.getColumnName(componentId, object, context));
				stringinputcomponent.setId((new StringBuilder()).append(AttributeDataUtilityHelper.getHtmlId(context.getDescriptor())).append(System.currentTimeMillis()).toString());
				stringinputcomponent.setRequired(true);
				return stringinputcomponent;
			}
		}

		return obj;
	}

	private TextBox getTextBox(String componentId, String value, boolean isRequired, boolean isEditable) {
		TextBox textbox = new TextBox();
		textbox.setRequired(isRequired);
		textbox.setInputType("text");
		textbox.setName(componentId);
		textbox.setValue(value);
		textbox.setId(componentId);
		textbox.setWidth(60);
		textbox.setMaxLength(255);
		textbox.setEditable(isEditable);
		return textbox;
	}

	private TextArea getTextArea(String componentId, String value, boolean isRequired, boolean isEditable) {
	    TextArea textArea = new TextArea();
	    textArea.setId(componentId);
	    textArea.setName(componentId);
	    textArea.setEditable(isEditable);
	    textArea.setRequired(isRequired);
	    return textArea;
	}
	private DateInputComponent getDateInputComponent(String componentId, String value, boolean isRequired,
            boolean isEditable) {
        DateInputComponent date = new DateInputComponent(componentId, DateInputComponent.ValueType.DATE_ONLY);
        date.setTimeZone(TimeZone.getDefault());
        date.setId(componentId);
        date.setName(componentId);
        date.setColumnName(componentId);
        date.setReadOnly(isEditable);
        return date;
    }

	private ComboBox getComboBox(String componentId, ArrayList<String> valueList, boolean isRequired, boolean isEditable) {
		ComboBox comboBox = new ComboBox();
		comboBox.setRequired(isRequired);
		comboBox.setName(componentId);
		comboBox.setValues(valueList);
		comboBox.setInternalValues(valueList);
		comboBox.setId(componentId);
		comboBox.setEditable(isEditable);
		return comboBox;
	}

	private GUIComponentArray getGUIComponentArray(String componentId, String value, boolean isRequired, boolean isEditable) {
	    GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
        guicomponentarrayMain.setValueHidden(false);
        guicomponentarrayMain.setRequired(isRequired);
        String values = "<input type=\"text\" width=\"400\" readonly name=\"zhizaodanwei\"  id=\"zhizaodanwei\" />" +
        		"<input type=\"button\" value=\"选择\" onclick=\"javascript:setSelZhiZaoDanWei(this);\"  id=\"zhizaodanwei_selected\"/>";
        NmTableGUIComponent gui = new NmTableGUIComponent(values);
        gui.setRequired(isRequired);
        guicomponentarrayMain.addGUIComponent(gui);
        return guicomponentarrayMain;
	}
	private GUIComponentArray getGUIComponentArrayInput(String componentId, String value, boolean isRequired, boolean isEditable) {
	    GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
        guicomponentarrayMain.setValueHidden(false);
        guicomponentarrayMain.setRequired(isRequired);
        String values = value;
        NmTableGUIComponent gui = new NmTableGUIComponent(values);
        gui.setRequired(isRequired);
        guicomponentarrayMain.addGUIComponent(gui);
        return guicomponentarrayMain;
	}
	private Object getInput(String paramString, Object obj){
		NmOid oid = (NmOid)obj;
		StringInputComponent strInput = new StringInputComponent();
		strInput.setEditable(true);
		strInput.setName(paramString + "_" + oid.toString());
		HashMap map = oid.getAdditionalInfo();
		if(map != null){
			strInput.setValue((String)map.get(paramString));
		}
		return strInput;
	}

}