package ext.casc.consCheckRecords;

import java.util.ArrayList;
import java.util.List;

import wt.util.WTException;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.CheckBox;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.components.rendering.guicomponents.StringInputComponent;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.util.NmTableGUIComponent;

public class ProConfigDataUtility extends AbstractDataUtility {

	@Override
	public Object getDataValue(String componentId, Object object, ModelContext context) throws WTException {
		NmCommandBean nmCommandBean = context.getNmCommandBean();
		Object ret = createProConfigFileds(componentId, nmCommandBean, object, context);
		return ret;
	}

	private Object createProConfigFileds(String componentId, NmCommandBean nmCommandBean, Object object, ModelContext context) {
		Object obj = null;
		if (object instanceof ProConfigBean) {
			ProConfigBean proConfigBean = (ProConfigBean) object;
			if (componentId.equals("selectConfig")) {//selectConfig_006
				CheckBox box = new CheckBox();
				box.setChecked(false);
				box.setName(componentId+"_"+proConfigBean.getGwKeyId());
				box.setValueHidden(true);
				return box;

			}
			if (componentId.equals("gwKeyId")) {
				StringInputComponent strInput = new StringInputComponent();
				strInput.setEditable(true);
				strInput.setName(componentId);
				strInput.setValue(proConfigBean.getGwKeyId());
				return strInput;
			}
			if (componentId.equals("value")) {
				StringInputComponent strInput = new StringInputComponent();
				strInput.setEditable(true);
				strInput.setName(componentId);
				strInput.setValue(proConfigBean.getValue());
				return strInput;
			}
			if (componentId.equals("type1")) {
				ComboBox comboBox = new ComboBox();
				ArrayList<String> s = comboBox.getSelected();
				ArrayList<String> values = new ArrayList<String>();
				values.add("检测类");
				values.add("记录类");
				comboBox.setName(componentId);
				comboBox.setEditable(true);
				comboBox.setValues(values);
				comboBox.setSelected(proConfigBean.getType1());
				return comboBox;
			}
		}
		return obj;
	}
}
