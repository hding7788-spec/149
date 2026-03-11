package ext.casc.consCheckRecords;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import wt.util.WTException;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class TableConfigAddRowProcessor extends DefaultObjectFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> objectBeans) throws WTException {
		FormResult formresult = super.doOperation(commandBean, objectBeans);

		Date timeOfNow = new Date();
		Long strTimeStamp = timeOfNow.getTime();
		String keyId = String.valueOf(strTimeStamp);
		TableConfigTreeHander.insert(keyId);

		return formresult;
	}

}
