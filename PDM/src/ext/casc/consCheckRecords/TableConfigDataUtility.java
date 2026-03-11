package ext.casc.consCheckRecords;

import wt.util.WTException;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.CheckBox;
import com.ptc.core.components.rendering.guicomponents.StringInputComponent;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class TableConfigDataUtility extends AbstractDataUtility {

	@Override
	public Object getDataValue(String componentId, Object object, ModelContext context) throws WTException {
		NmCommandBean nmCommandBean = context.getNmCommandBean();
		Object ret = createTableConfigFileds(componentId, nmCommandBean, object, context);
		return ret;
	}

	private Object createTableConfigFileds(String componentId, NmCommandBean nmCommandBean, Object object, ModelContext context) {
		Object obj = null;
		if (object instanceof TableConfigBean) {
			TableConfigBean tableConfigBean = (TableConfigBean) object;
			if(componentId.equals("selectTable")){
				CheckBox box = new CheckBox();
				box.setChecked(false);
				box.setName(componentId+"_"+tableConfigBean.getGwKeyId());
				box.setValueHidden(true);
				return box;
			}
			if (componentId.equals("gwKeyId")) {
				StringInputComponent strInput = new StringInputComponent();
				strInput.setEditable(true);
				strInput.setName(componentId);
				strInput.setValue(tableConfigBean.getGwKeyId());
				return strInput;
			}
			if (componentId.equals("name")) {
				StringInputComponent strInput = new StringInputComponent();
				strInput.setEditable(true);
				strInput.setName(componentId);
				strInput.setValue(tableConfigBean.getName());
				return strInput;
			}
		}
		return obj;
	}
}
