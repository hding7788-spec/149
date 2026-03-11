package ext.casc.doc.technology;

import com.ptc.netmarkets.util.beans.NmCommandBean;

public class TechnologySearchCommana {
	public static void search(NmCommandBean commandBean){
		String dalei = (String)commandBean.getText().get("dalei");
		commandBean.getRequest().setAttribute("dalei", dalei);
	}
}
