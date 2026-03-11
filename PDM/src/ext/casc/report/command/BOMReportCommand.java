package ext.casc.report.command;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ptc.netmarkets.util.beans.NmCommandBean;

public class BOMReportCommand  {
	public static void search(NmCommandBean commandBean){
		String productName = (String)commandBean.getText().get("productName");
		commandBean.getRequest().setAttribute("paramProductName", productName);
	}
}
