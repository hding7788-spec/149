package com.glaway.mpm.mpmresource.gznumber.loader;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.glaway.mpm.mpmresource.gznumber.bean.RequestInfoContained;
import com.glaway.mpm.mpmresource.gznumber.number.GZNumberInfoContained;
import com.glaway.mpm.mpmresource.gznumber.number.GZNumberRegister;


/*
 1.	ALK：="RootC"& MID(A2,4,5)
 2.	ALJ: ="RootB"& MID(A2,4,5)
 3.	SP： =IF(MID(A2,3,5)="2.499","RootS",IF(MID(A2,3,5)="2.491","RootS","RootSP"))& MID(A2,3,5)
 4.	标准件：="RootSD"&MID(A2,1,9)
 5.	标准文档 ：
 =if(isnumber(find("Q/AL",A2,1)),"RootQ/AL",if(isnumber(find("QZ/AL",A2,1)),"RootQZ/AL",if(isnumber(find("QS/AL",A2,1)),"RootQS/AL","RootQG/AL")))
 6.	AL：
 a)	补classpath： update BHAL set 设计师= 'RootA'+right(left(图号,7),5);
 b)	Access删除序列号为空的记录：
 Delete from BHAL where 序列号 is NULL;
 或者
 补充序列号为空的记录：
 update BHAL set 序列号= right(left(图号,11),3) where序列号 is NULL;
 c)	更新“编号日期”为'yyyy-mm-dd'：
 update BHAL set 编号日期 = format(编号日期,'yyyy-mm-dd');
 d)	在oracle中可以用sqlldr命令导入 :　sqlldr pdm800/pdm800@wind102 control=al_load.ctl
 */
public class GZNumberLoader {
	private DataSource data;
	private GZNumberTranslated translator;
	private LoadReporter reporter;

	public GZNumberLoader(DataSource data, GZNumberTranslated translator) {
		this.data = data;
		this.translator = translator;
		reporter = new LoadReporter();
	}

	public void load() {
		for (int i = translator.getRowBegin(); i <= data.end(); i++) {
			HashMap record = data.getRecordData(i);
			if (record.get("ROW 0") == null || record.get("ROW 0").equals("")) {
				continue;
			}
			translator.translate(record);
			GZNumberInfoContained numberInfo = translator.getNumberInfo();
			RequestInfoContained requestInfo = translator.getRequestInfo();

			try {
				GZNumberRegister.manager.registerOneNumber2(numberInfo, requestInfo);
			} catch (Exception e) {
				String errorMsg = e.getMessage();
				// 去除换行符，制表符
				Pattern p = Pattern.compile("\t|\r|\n");
				Matcher m = p.matcher(errorMsg);
				errorMsg = m.replaceAll("");
				reporter.addDisplayErrors("=== Record " + numberInfo.getNumber() + " at line(" + i
						+ ") Loading Error Occures! ===");
				reporter.addBackErrors(errorMsg);
				reporter.addLogs("=== Load line(" + i + ") of Number [" + numberInfo.getNumber() + "] Failed! ===");
				// e.printStackTrace();
				return;
			}
			reporter.addLogs("=== Load line(" + i + ") of Number [" + numberInfo.getNumber() + "] successful! ===");
		}
	}

	public LoadReporter getReporter() {
		return reporter;
	}
}
