package ext.casc.doc.technology;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import wt.util.WTException;

import java.util.List;
import java.util.UUID;

public class TechnilogyAddRowProcessor extends DefaultObjectFormProcessor{

	@Override
	public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> objectBeans) throws WTException {
		FormResult formresult = super.doOperation(commandBean, objectBeans);
		String keyId = UUID.randomUUID().toString();
		String fatherCname = (String) commandBean.getText().get("dalei");
		List<TechnologyBean> result = TechnicsTechnologyTreeHander.search(fatherCname,null);
		String fatherEname = TechnicsTechnologyTreeHander.getFatherEname(fatherCname);
		String childNumber = "";
		int count = 0;
		if(result != null){
			count = result.size() + 1;
		}else{
			count = 1;
		}
		if(count < 10){
			childNumber = "0" + count;
		}else{
			childNumber = String.valueOf(count);
		}
		TechnicsTechnologyTreeHander.insert(keyId, fatherEname, fatherCname, childNumber);
		System.out.println("--------addRow---------");

		return formresult;
	}

}
