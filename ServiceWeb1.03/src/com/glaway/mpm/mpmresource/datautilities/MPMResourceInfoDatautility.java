package com.glaway.mpm.mpmresource.datautilities;

import com.glaway.mpm.mpmresource.AttributeConstants;
import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.IBAHelper;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.components.rendering.guicomponents.DateInputComponent;
import com.ptc.core.components.rendering.guicomponents.TextArea;
import com.ptc.core.components.rendering.guicomponents.TextBox;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.resource.MPMPlant;
import com.ptc.windchill.mpml.resource.MPMSkill;
import com.ptc.windchill.mpml.resource.MPMTooling;
import ext.casc.process.util.ProcessUtil;
import ext.casc.sop.constants.SopConstants;
import ext.casc.sop.util.SopUtil;
import wt.part.WTPart;
import wt.util.WTException;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class MPMResourceInfoDatautility extends AbstractDataUtility {
	private static final String CLASSNAME = MPMResourceInfoDatautility.class.getName();

	public MPMResourceInfoDatautility() {
	}

	public Object getDataValue(String componentId, Object obj, ModelContext modelcontext) throws WTException {
		GLLogger.debug(CLASSNAME, "--start--");
		String descriptorMode = modelcontext.getDescriptorMode().name();
		NmCommandBean nmcommandbean = modelcontext.getNmCommandBean();
		Object actionObj = nmcommandbean.getActionOid().getRefObject();
		Object ret = "";
		GLLogger.debug(CLASSNAME, "-descriptorMode-" + descriptorMode);
		if (descriptorMode.equals("EDIT")) {
			try {
				ret = editMPMResourceFields(componentId, nmcommandbean);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			if (componentId.equals("workcenter")) {
				Object object = modelcontext.getModelObject();
				if (actionObj instanceof MPMPlant && object instanceof MPMSkill) {
					ret = ((MPMPlant) actionObj).getNumber() + ((MPMSkill) object).getNumber();
				}
			}
		}
		return ret;
	}

	private Object editMPMResourceFields(String componentId, NmCommandBean nmcommandbean) throws Exception {
		Object obj = nmcommandbean.getActionOid().getRefObject();
		WTPart part = (WTPart) obj;
		IBAHelper helper = new IBAHelper(part);
		String sop = "sop_";
		componentId = componentId.trim();
		if (componentId.equals(AttributeConstants.number)) {
			obj = getTextBox(componentId, part.getNumber(), true, true);
		} else if (componentId.equals(sop+AttributeConstants.number)) {
			obj = getTextBox(AttributeConstants.number, part.getNumber(), true, false);
		} else if (componentId.equals(AttributeConstants.name)) {
			obj = getTextBox(componentId, part.getName(), true, true);
		} else if (componentId.equals(sop+AttributeConstants.name)) {
			obj = getTextBox(AttributeConstants.name, part.getName(), true, false);
		} else if (componentId.equals(AttributeConstants.equipmentNumber)) {
			obj = getTextBox(componentId, helper.getIBAValue(componentId), false, true);
		} else if (componentId.equals(AttributeConstants.enableddate)) {
            obj = getDateInputComponent(componentId, helper.getIBAValue(AttributeConstants.enableddate), false, false);
        } else if (componentId.equals(AttributeConstants.applydate)) {
            obj = getDateInputComponent(componentId, helper.getIBAValue(AttributeConstants.applydate), false, false);
        } else if (componentId.equals(AttributeConstants.remarkKey)) {
            obj = getTextArea(componentId, helper.getIBAValue(AttributeConstants.remarkKey), false, true);
        } else if (componentId.equals(AttributeConstants.managestatus)) {
            ArrayList<String> list = new ArrayList<String>();
            list.add("在用");
            list.add("封存");
            list.add("报废");
            obj = getComboBox(componentId, list,helper.getIBAValue(componentId), false, true);
        } else if (componentId.equals(AttributeConstants.equipmentType)) {
            ArrayList<String> list = new ArrayList<String>();
            list.add("车床");
            list.add("铣床");
            list.add("转运车");
            obj = getComboBox(componentId, list,helper.getIBAValue(componentId), false, true);
        } else if (componentId.equals(AttributeConstants.level)) {
            ArrayList<String> list = new ArrayList<String>();
            list.add("A");
            list.add("B");
            list.add("C");
            obj = getComboBox(componentId,list,helper.getIBAValue(componentId), false, true);
        }else if (componentId.equals(AttributeConstants.ZZCJ)) {
            ArrayList<String> list = null;
            try {
                list = ProcessUtil.getGongXuCheJian();
                list.add(0,"");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            obj = getComboBox(componentId, list,helper.getIBAValue(componentId), false, true);
        }else if (componentId.equals(AttributeConstants.SFSBGS)) {
			ArrayList<String> list = new ArrayList<String>();
			try {
				list.add(0,"");
				list.add(1,"否");
				list.add(2,"是");
			} catch (Exception e) {
				e.printStackTrace();
			}
			obj = getComboBox(componentId, list,helper.getIBAValue(componentId), false, true);
		}else if (componentId.equals(sop+AttributeConstants.ZZCJ)) {
            ArrayList<String> list = null;
            try {
                list = ProcessUtil.getGongXuCheJian();
                list.add(0,"");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            obj = getComboBox(AttributeConstants.ZZCJ, list,helper.getIBAValue(AttributeConstants.ZZCJ), false, true);
        }else if (componentId.equals(sop+AttributeConstants.SpecializedType)) {
        	ArrayList<String> list = new ArrayList<String>();
        	list = ProcessUtil.getSpecializedType();
			list.add(0, "");
			ComboBox comboBox = getComboBox("SpecializedType",list,helper.getIBAValue(SopConstants.SOP_IBA_SPECIALIZEDTYPE), true, false);
            comboBox.addJsAction("onchange", "selectSpecializedType()");
            return comboBox;
        }else if (componentId.equals(sop+AttributeConstants.ProceduceName)) {
        	ArrayList<String> list = new ArrayList<String>();
            String specializedType = helper.getIBAValue("SpecializedType");
            List<MPMTooling> allGxmc = SopUtil.getAllGxmc(specializedType);
            for (MPMTooling wtPart : allGxmc) {
            	String name = wtPart.getName().trim();
				list.add(name);
			}
			list.add(0, "");
			String ibaValue = helper.getIBAValue(SopConstants.SOP_IBA_PROCEDUCENAME).trim();
            obj = getComboBox(AttributeConstants.ProceduceName, list,ibaValue, true, false);
        }else if (componentId.equals(sop+AttributeConstants.GONGXUJIANHAO)) {
        	obj = getTextBox(AttributeConstants.GONGXUJIANHAO,helper.getIBAValue(SopConstants.SOP_IBA_GONGXUJIANHAO), true, false);
        }else if (componentId.equals(sop+AttributeConstants.ParametersName)) {
        	ArrayList<String> list = new ArrayList<String>();
        	 String specializedType = helper.getIBAValue("SpecializedType");
             List<WTPart> allCsxmmc = SopUtil.getAllCsxmmc(specializedType);
             for (WTPart wtPart : allCsxmmc) {
 				list.add(wtPart.getName());
 			}
			list.add(0, "");
            obj = getComboBox(AttributeConstants.ParametersName, list,helper.getIBAValue(SopConstants.SOP_IBA_PARAMETERSNAME), true, false);
        }else if (componentId.equals(sop+AttributeConstants.MaterialCategory)) {
        	ArrayList<String> list = new ArrayList<String>();
        	 String specializedType = helper.getIBAValue("SpecializedType");
             List<MPMTooling> allWzlb = SopUtil.getAllMaterialCategory(specializedType);
             for (MPMTooling wtPart : allWzlb) {
 				list.add(wtPart.getName());
 			}
			list.add(0, "");
            obj = getComboBox(AttributeConstants.MaterialCategory, list,helper.getIBAValue(SopConstants.SOP_IBA_MATERIALCATEGORY), true, false);
        }else if(componentId.equals(sop+AttributeConstants.ProfessionalCode)){
        	String oldValue = helper.getIBAValue(AttributeConstants.ProfessionalCode);
			obj = getTextBox(AttributeConstants.ProfessionalCode, oldValue, true, false);
		}else {
        	if(componentId.contains("sop")){
        		componentId = componentId.replace("sop_", "");
        	}
        	String oldValue = helper.getIBAValue(componentId);
			obj = getTextBox(componentId, oldValue, false, true);
		}
		return obj;
	}

	   private TextArea getTextArea(String componentId, String value, boolean isRequired, boolean isEditable) {
	        TextArea textArea = new TextArea();
	        textArea.setId(componentId);
	        textArea.setName(componentId);
	        textArea.setEditable(isEditable);
	        textArea.setRequired(isRequired);
	        textArea.setValue(value);
	        return textArea;
	    }

	    private DateInputComponent getDateInputComponent(String componentId, String value, boolean isRequired,
	            boolean isEditable) {
	        DateInputComponent date = new DateInputComponent(componentId, DateInputComponent.ValueType.DATE_ONLY);
	        date.setTimeZone(TimeZone.getDefault());
	        date.setId(componentId);
	        date.setName(componentId);
	        if(value != null && !"".equals(value)) {
	            date.setValue(Timestamp.valueOf(value));
	        }
	        date.setColumnName(componentId);
	        date.setReadOnly(isEditable);
	        return date;
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

	private ComboBox getComboBox(String componentId, ArrayList<String> valueList, String selectedValue,
			boolean isRequired, boolean isEditable) {
		ComboBox comboBox = new ComboBox();
		comboBox.setRequired(isRequired);
		comboBox.setName(componentId);
		comboBox.setValues(valueList);
		comboBox.setInternalValues(valueList);
		comboBox.setSelected(selectedValue);
		comboBox.setId(componentId);
		comboBox.setEditable(isEditable);
		return comboBox;
	}
}
