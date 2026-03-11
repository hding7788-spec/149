package com.glaway.mpm.mpmresource.datautilities;

import wt.util.WTException;

import com.glaway.mpm.mpmresource.AttributeConstants;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.TextBox;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.resource.MPMPlant;
import com.ptc.windchill.mpml.resource.MPMProcessMaterial;
import com.ptc.windchill.mpml.resource.MPMResourceGroup;
import com.ptc.windchill.mpml.resource.MPMSkill;
import com.ptc.windchill.mpml.resource.MPMTooling;
import com.ptc.windchill.mpml.resource.MPMWorkCenter;

public class MPMResourceRelationshipDatautility extends AbstractDataUtility {

	public MPMResourceRelationshipDatautility() {
	}

	public Object getDataValue(String componentId, Object obj, ModelContext modelcontext) throws WTException {

		String descriptorMode = modelcontext.getDescriptorMode().name();
		NmCommandBean nmcommandbean = modelcontext.getNmCommandBean();
		Object ret = "";

		if (descriptorMode.equals("CREATE")) {
			ret = createMPMResourceFields(componentId, nmcommandbean);
		} else if (descriptorMode.equals("EDIT")) {
			ret = editMPMResourceFields(componentId, nmcommandbean);
		}
		return ret;
	}

	private Object createMPMResourceFields(String componentId, NmCommandBean nmcommandbean) throws WTException {
		Object obj = null;
		if (componentId.equals(AttributeConstants.number)) {
			obj = getTextBox(componentId, "", true, true);
		} else if (componentId.equals(AttributeConstants.name)) {
			obj = getTextBox(componentId, "", true, true);
		}
		return obj;
	}

	private Object editMPMResourceFields(String componentId, NmCommandBean nmcommandbean) throws WTException {
		Object obj = nmcommandbean.getActionOid().getRefObject();
		String numberValue = "";
		String nameValue = "";
		if (obj instanceof MPMPlant) {
			MPMPlant mpmPlant = (MPMPlant) obj;
			numberValue = mpmPlant.getNumber();
			nameValue = mpmPlant.getName();
		} else if (obj instanceof MPMProcessMaterial) {
			MPMProcessMaterial mpmProcessMaterial = (MPMProcessMaterial) obj;
			numberValue = mpmProcessMaterial.getNumber();
			nameValue = mpmProcessMaterial.getName();
		} else if (obj instanceof MPMWorkCenter) {
			MPMWorkCenter mpmWorkCenter = (MPMWorkCenter) obj;
			numberValue = mpmWorkCenter.getNumber();
			nameValue = mpmWorkCenter.getName();
		} else if (obj instanceof MPMTooling) {
			MPMTooling mpmTooling = (MPMTooling) obj;
			numberValue = mpmTooling.getNumber();
			nameValue = mpmTooling.getName();
		} else if (obj instanceof MPMResourceGroup) {
			MPMResourceGroup mpmResourceGroup = (MPMResourceGroup) obj;
			numberValue = mpmResourceGroup.getNumber();
			nameValue = mpmResourceGroup.getName();
		} else if (obj instanceof MPMSkill) {
			MPMSkill mpmSkill = (MPMSkill) obj;
			numberValue = mpmSkill.getNumber();
			nameValue = mpmSkill.getName();
		}
		Object obj1 = null;
		if (componentId.equals(AttributeConstants.number)) {
			obj1 = getTextBox(componentId, numberValue, true, true);
		} else if (componentId.equals(AttributeConstants.name)) {
			obj1 = getTextBox(componentId, nameValue, true, true);
		}
		return obj1;
	}

	public TextBox getTextBox(String componentId, String value, boolean isRequired, boolean isEditable) {
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

}
