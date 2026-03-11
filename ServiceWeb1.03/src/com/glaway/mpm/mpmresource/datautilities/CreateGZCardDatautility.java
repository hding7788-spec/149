package com.glaway.mpm.mpmresource.datautilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.util.WTException;

import com.glaway.mpm.mpmresource.AttributeConstants;
import com.glaway.mpm.mpmresource.Constants;
import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.MPMResourceUtil;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.components.rendering.guicomponents.TextArea;
import com.ptc.core.components.rendering.guicomponents.TextBox;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.resource.MPMPlant;

public class CreateGZCardDatautility extends AbstractDataUtility {
	private static final String CLASSNAME = CreateGZCardDatautility.class.getName();

	public CreateGZCardDatautility() {
	}

	public Object getDataValue(String componentId, Object obj, ModelContext modelcontext) throws WTException {
//		GLLogger.debug(CLASSNAME, "--start-CreateGZCardDatautility-");
		String descriptorMode = modelcontext.getDescriptorMode().name();
		NmCommandBean nmcommandbean = modelcontext.getNmCommandBean();
		Object ret = "";
//		GLLogger.debug(CLASSNAME, "-descriptorMode-" + descriptorMode);
		if (descriptorMode.equals("EDIT")) {
			ret = edit(componentId, nmcommandbean);
		} else if (descriptorMode.equals("CREATE")) {
			ret = create(componentId, nmcommandbean);
		} else if (descriptorMode.equals("VIEW")) {
			ret = view(componentId, nmcommandbean);
		}
		return ret;
	}

	// 新建界面
	private Object create(String componentId, NmCommandBean nmcommandbean) throws WTException {

		Object obj = null;

		if (componentId.equals(AttributeConstants.number)) {
			List list = (List) nmcommandbean.getMap().get("GZNumberMap");
			Map<String, String> jsAction = new HashMap<String, String>();
			jsAction.put("onchange", "setName(this)");
			obj = getComboBox(componentId, jsAction, (ArrayList) list.get(0), (ArrayList) list.get(1), true, true);
		} else if (componentId.equals(AttributeConstants.name)) {
			String name = "";
			List list = (List) nmcommandbean.getMap().get("GZNumberMap");
			List numberNameList = (ArrayList) list.get(1);
			if (numberNameList.size() != 0) {
				name = ((String) numberNameList.get(0)).split("\\" + Constants.gzNameSplitStr)[1];
			}
			obj = getTextBox(componentId, name, true, true);
		} else if (componentId.equals(AttributeConstants.productNumber)) {
			obj = getTextBox(componentId, "", false, false);
		} else if (componentId.equals(AttributeConstants.insertPart)) {
			ArrayList valueList = getBooleanList("values",false);
			ArrayList internalValueList = getBooleanList("internalValues",false);
			obj = getComboBox(componentId, valueList, internalValueList, true, true);
		} else if (componentId.equals(AttributeConstants.productionNumber)) {
			Map<String, String> jsAction = new HashMap<String, String>();
			jsAction.put("onblur", "isNum(this)");
			obj = getTextBox(componentId, "1", jsAction, true);
		} else if (componentId.equals(AttributeConstants.isReview)) {
			ArrayList valueList = getBooleanList("values",false);
			ArrayList internalValueList = getBooleanList("internalValues",false);
			obj = getComboBox(componentId, valueList, internalValueList, true, true);
		} else if (componentId.equals(AttributeConstants.isCommonTools)) {
			ArrayList list = getIsCommonTools();
			obj = getComboBox(componentId, list, list, true, true);
		} else if (componentId.equals(AttributeConstants.isTestPart)) {
			ArrayList valueList = getBooleanList("values",true);
			ArrayList internalValueList = getBooleanList("internalValues",true);
			obj = getComboBox(componentId, valueList, internalValueList, true, true);
		} else if (componentId.equals(AttributeConstants.partNumber)) {
			obj = getTextBox(componentId, "", false, false);
		} else if (componentId.equals(AttributeConstants.wholePartNumber)) {
			obj = getTextBox(componentId, "", false, false);
		} else if (componentId.equals(AttributeConstants.workShop)) {
			ArrayList list = getWorkShop();
			obj = getComboBox(componentId, list, list, true, true);
		} else if (componentId.equals(AttributeConstants.isRegularlyTools)) {
			ArrayList valueList = getBooleanList("values",false);
			ArrayList internalValueList = getBooleanList("internalValues",false);
			obj = getComboBox(componentId, valueList, internalValueList, true, true);
		}

		return obj;
	}

	// 修改界面
	private Object edit(String componentId, NmCommandBean nmcommandbean) throws WTException {
		WTDocument document = (WTDocument) nmcommandbean.getMap().get("GZCardObject");
		Object obj = null;
		IBAHelper helper = new IBAHelper(document);
		if (componentId.equals(AttributeConstants.number)) {
			obj = document.getNumber();
		} else if (componentId.equals(AttributeConstants.name)) {
			obj = document.getName();
		} else if (componentId.equals(AttributeConstants.productNumber)) {
			obj = getTextBox(componentId, helper.getIBAValue(AttributeConstants.productNumber), false, false);
		} else if (componentId.equals(AttributeConstants.insertPart)) {
			String insertPart = helper.getIBAValue(AttributeConstants.insertPart);
			ArrayList valueList = getBooleanList("values",false);
			ArrayList internalValueList = getBooleanList("internalValues",false);
			obj = getComboBox(componentId, valueList, internalValueList, insertPart, true, true);
		} else if (componentId.equals(AttributeConstants.productionNumber)) {
			Map<String, String> jsAction = new HashMap<String, String>();
			jsAction.put("onblur", "isNum(this)");
			obj = getTextBox(componentId, helper.getIBAValue(AttributeConstants.productionNumber), jsAction, true);
		} else if (componentId.equals(AttributeConstants.isReview)) {
			String isReview = helper.getIBAValue(AttributeConstants.isReview);
			ArrayList valueList = getBooleanList("values",false);
			ArrayList internalValueList = getBooleanList("internalValues",false);
			obj = getComboBox(componentId, valueList, internalValueList, isReview, true, true);
		} else if (componentId.equals(AttributeConstants.isCommonTools)) {
			String isCommonTools = helper.getIBAValue(AttributeConstants.isCommonTools);
			ArrayList list = getIsCommonTools();
			obj = getComboBox(componentId, list, list, isCommonTools, true, true);
		} else if (componentId.equals(AttributeConstants.isTestPart)) {
			String isTestPart = helper.getIBAValue(AttributeConstants.isTestPart);
			ArrayList valueList = getBooleanList("values",true);
			ArrayList internalValueList = getBooleanList("internalValues",true);  
			obj = getComboBox(componentId, valueList, internalValueList, isTestPart, true, true);
		} else if (componentId.equals(AttributeConstants.partNumber)) {
			obj = getTextBox(componentId, helper.getIBAValue(AttributeConstants.partNumber), false, false);
		} else if (componentId.equals(AttributeConstants.wholePartNumber)) {
			obj = getTextBox(componentId, helper.getIBAValue(AttributeConstants.wholePartNumber), false, false);
		} else if (componentId.equals(AttributeConstants.workShop)) {
			String workShop = helper.getIBAValue(AttributeConstants.workShop);
			ArrayList list = getWorkShop();
			obj = getComboBox(componentId, list, list, workShop, true, true);
		} else if (componentId.equals(AttributeConstants.isRegularlyTools)) {
			String isRegularlyTools = helper.getIBAValue(AttributeConstants.isRegularlyTools);
			ArrayList valueList = getBooleanList("values",false);
			ArrayList internalValueList = getBooleanList("internalValues",false);
			obj = getComboBox(componentId, valueList, internalValueList, isRegularlyTools, true, true);
		}

		return obj;
	}

	// 显示界面
	private Object view(String componentId, NmCommandBean nmcommandbean) throws WTException {
		WTDocument document = (WTDocument) nmcommandbean.getActionOid().getRef();
		Object obj = null;
		IBAHelper helper = new IBAHelper(document);
		if (componentId.equals(AttributeConstants.productionNumber)) {
			Map<String, String> jsAction = new HashMap<String, String>();
			jsAction.put("onblur", "isNum(this)");
			obj = getTextBox(componentId, helper.getIBAValue(AttributeConstants.productionNumber), jsAction, true);
		}

		return obj;
	}

	/**
	 * 
	 * @author qianlong
	 * @date 2013-3-18
	 * @param id
	 * @return
	 * 
	 */
	public TextArea getTextarea(String id, String value) {
		TextArea textArea = new TextArea();
		textArea.setId(id);
		textArea.setName(id);
		textArea.setHeight(3);
		textArea.setWidth(20);
		textArea.setEnabled(true);
		textArea.setEditable(true);
		textArea.setValue(value);
		return textArea;
	}

	/**
	 * 
	 * @author qianlong
	 * @date 2013-3-18
	 * @param componentId
	 * @param value
	 * @param isRequired
	 * @param isEditable
	 * @return
	 * 
	 */
	private TextBox getTextBox(String componentId, String value, boolean isRequired, boolean isReadOnly) {
		TextBox textbox = new TextBox();
		textbox.setRequired(isRequired);
		textbox.setName(componentId);
		textbox.setValue(value);
		textbox.setId(componentId);
		textbox.setWidth(20);
		textbox.setMaxLength(255);
		textbox.setReadOnly(isReadOnly);
		return textbox;
	}

	/**
	 * 
	 * @author qianlong
	 * @date 2013-3-18
	 * @param componentId
	 * @param value
	 * @param isRequired
	 * @param isEditable
	 * @return
	 * 
	 */
	private TextBox getTextBox(String componentId, String value, Map<String, String> jsAction, boolean isRequired) {
		TextBox textbox = new TextBox();
		textbox.setRequired(isRequired);
		textbox.addJsActions(jsAction);
		textbox.setName(componentId);
		textbox.setValue(value);
		textbox.setId(componentId);
		textbox.setWidth(20);
		textbox.setMaxLength(255);
		return textbox;
	}

	/**
	 * 
	 * @author qianlong
	 * @date 2013-3-18
	 * @param componentId
	 * @param valueList
	 * @param isRequired
	 * @param isEditable
	 * @return
	 * 
	 */
	private ComboBox getComboBox(String componentId, ArrayList valueList, ArrayList internalValueList,
			boolean isRequired, boolean isEditable) {
		ComboBox comboBox = new ComboBox();
		comboBox.setRequired(isRequired);
		comboBox.setName(componentId);
		comboBox.setValues(valueList);
		comboBox.setInternalValues(internalValueList);
		comboBox.setId(componentId);
		comboBox.setEditable(isEditable);
		return comboBox;
	}

	/**
	 * 
	 * @author qianlong
	 * @date 2013-3-18
	 * @param componentId
	 * @param valueList
	 * @param isRequired
	 * @param isEditable
	 * @return
	 * 
	 */
	private ComboBox getComboBox(String componentId, Map<String, String> jsAction, ArrayList valueList,
			ArrayList internalValueList, boolean isRequired, boolean isEditable) {
		ComboBox comboBox = new ComboBox();
		comboBox.setRequired(isRequired);
		comboBox.addJsActions(jsAction);
		comboBox.setName(componentId);
		comboBox.setValues(valueList);
		comboBox.setInternalValues(internalValueList);
		comboBox.setId(componentId);
		comboBox.setEditable(isEditable);
		return comboBox;
	}

	/**
	 * 
	 * @author qianlong
	 * @date 2013-3-27
	 * @param componentId
	 * @param valueList
	 * @param internalValueList
	 * @param isRequired
	 * @param isEditable
	 * @return
	 * 
	 */
	private ComboBox getComboBox(String componentId, ArrayList valueList, ArrayList internalValueList,
			String selectValue, boolean isRequired, boolean isEditable) {
		ComboBox comboBox = new ComboBox();
		comboBox.setRequired(isRequired);
		comboBox.setName(componentId);
		comboBox.setValues(valueList);
		comboBox.setInternalValues(internalValueList);
		comboBox.setSelected(selectValue);
		comboBox.setId(componentId);
		comboBox.setEditable(isEditable);
		return comboBox;
	}

	/**
	 * 
	 * @author qianlong
	 * @date 2013-3-26
	 * @return
	 * 
	 */
	private ArrayList getBooleanList(String tag, boolean defaultValue) {
		ArrayList list = new ArrayList();
		if ("values".equals(tag)) {
			if (defaultValue) {
				list.add("是");
				list.add("否");
			} else {
				list.add("否");
				list.add("是");
			}

		} else if ("internalValues".equals(tag)) {
			if (defaultValue) {
				list.add("true");
				list.add("false");
			} else {
				list.add("false");
				list.add("true");
			}
		}
		return list;
	}

	/**
	 * 
	 * @author qianlong
	 * @date 2013-3-27
	 * @return
	 * 
	 */
	private ArrayList getIsCommonTools() {
		ArrayList list = new ArrayList();
		list.add("N");
		list.add("A");
		list.add("B");
		list.add("C");
		return list;
	}

	private ArrayList getWorkShop() throws WTException {
		ArrayList list = new ArrayList();
		list.add("");
		QueryResult result = MPMResourceUtil.getAllPlant();
		while (result.hasMoreElements()) {
			list.add(((MPMPlant) result.nextElement()).getName());
		}
		return list;
	}
}
