package ext.casc.report.command;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ptc.netmarkets.util.beans.NmCommandBean;

public class AllWorkFlowCommand  {
	public static void search(NmCommandBean commandBean){
		String processName = (String)commandBean.getText().get("processName");
		String beginTime = (String)commandBean.getText().get("beginTime_col_beginTime");
		String endTime = (String)commandBean.getText().get("endTime_col_endTime");
		//String isUsed = (String)((ArrayList)commandBean.getComboBox().get("isUsed")).get(0);
		Timestamp beginTimestamp =getTimeStamp(beginTime);
		Timestamp endTimestamp = getTimeStamp(endTime);
		commandBean.getRequest().setAttribute("paramProcessName", processName);
		commandBean.getRequest().setAttribute("paramBeginTime", beginTimestamp);
		commandBean.getRequest().setAttribute("paramEndTime", endTimestamp);
	}
	
	public static Timestamp getTimeStamp(String dateStr){
		Timestamp timestamp = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		if (dateStr != null && dateStr.trim().length() > 0){
			try {
				Date date = sdf.parse(dateStr);
				timestamp = new Timestamp(date.getTime());
			} catch (Exception parseexception) {
				parseexception.printStackTrace();
			}
		}
		return timestamp;
	}
}
