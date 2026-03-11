package ext.casc.doc.technology;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import wt.util.WTException;

import java.util.List;

public class TechnologyDeleteRowProcessor extends DefaultObjectFormProcessor{

	@Override
	public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> objectBeans) throws WTException {
		FormResult formresult = super.doOperation(commandBean, objectBeans);
		//小类编号
		String childNumber = commandBean.getRequest().getParameter("hiddenValue");
		String fatherCname = (String) commandBean.getText().get("dalei");
		//大类内部名称
		String fatherEname = TechnicsTechnologyTreeHander.getFatherEname(fatherCname);
		String status = TechnicsTechnologyTreeHander.getStatus(fatherEname, childNumber);
		if("0".equals(status)){
			status = "1";
		}else if("1".equals(status)){
			status = "0";
		}
		TechnicsTechnologyTreeHander.updateStatus(fatherEname, childNumber, status);
		System.out.println("-----deleteRow---------");
		return formresult;
	}

}
