package ext.casc.processPlan.processor;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.doc.technology.TechnicsTechnologyTreeHander;
import wt.util.WTException;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SaveParasProcessor extends DefaultObjectFormProcessor{

	@Override
	public FormResult doOperation(NmCommandBean nmcommandbean, List<ObjectBean> list) throws WTException {
		FormResult formresult = super.doOperation(nmcommandbean, list);
		HttpServletRequest request = nmcommandbean.getRequest();
		String fatherCname = (String) nmcommandbean.getText().get("dalei");
		Map map = request.getParameterMap();
		Iterator iterator = map.keySet().iterator();
		String[] childNumbers = null;
		String[] childNames = null;
		while (iterator.hasNext()) {
			String key = String.valueOf(iterator.next());
			if (key.indexOf("_childNumber") > -1) {
				childNumbers = (String[]) map.get(key);
			}
			if(key.indexOf("_childName") > -1 && !key.contains("old")){
				childNames = (String[]) map.get(key);
			}
		}
		for(int i = 0 ; i < childNames.length; i++){
			TechnicsTechnologyTreeHander.update(fatherCname, childNumbers[i], childNames[i]);
		}
		System.out.println("-----saveItem---------");
		return formresult;
	}

}
