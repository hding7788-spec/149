package ext.casc.consCheckRecords;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import wt.util.WTException;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class TableConfigSaveProcessor extends DefaultObjectFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean nmcommandbean, List<ObjectBean> list) throws WTException {
		FormResult formresult = super.doOperation(nmcommandbean, list);
		HttpServletRequest request = nmcommandbean.getRequest();
		Map map = request.getParameterMap();
		Iterator iterator = map.keySet().iterator();
		String[] ids = null;
		String[] names = null;
		while (iterator.hasNext()) {
			String key = String.valueOf(iterator.next());
			if (key.indexOf("_gwKeyId") > -1) {
				ids = (String[]) map.get(key);
			}
			if (key.indexOf("_name") > -1 && !key.contains("old")) {
				names = (String[]) map.get(key);
			}
		}
		for (int i = 0; i < names.length; i++) {
			TableConfigTreeHander.update(ids[i], names[i]);
		}

		System.out.println("------------saveTableConfig----------");
		return formresult;
	}

}
