package ext.casc.consCheckRecords;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import wt.fc.WTObject;
import wt.util.WTException;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class ProConfigDeleteRowProcessor extends DefaultObjectFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean nmcommandbean, List<ObjectBean> objectBeans) throws WTException {
		FormResult formresult = super.doOperation(nmcommandbean, objectBeans);

		HashMap map = nmcommandbean.getChecked();

		Iterator iterator = map.keySet().iterator();
		List<String> list = new ArrayList<String>();

		while (iterator.hasNext()) {
			String key = String.valueOf(iterator.next());
			String valueName =  key.substring(key.indexOf("_")+1);
			list.add(valueName);
		}

		ProConfigTreeHander proConfigTreeHander = new ProConfigTreeHander();
			for (int i = 0; i < list.size(); i++) {
				 proConfigTreeHander.delete(list.get(i));
			}

		System.out.println("------------deleteProConfig----------");
		return formresult;
	}
}
