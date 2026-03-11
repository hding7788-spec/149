package ext.casc.tools;

import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.WTPartUtil;
import ext.casc.util.IBAUtility;
import ext.casc.workflow.PrintHelper;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTStandardDateFormat;
import wt.vc.VersionControlHelper;
import wt.vc.views.View;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class ExportDemo {

	public static void exportMutiProcessPlan() {
		try {
			int index[] = { 0 };
			View view = WTPartUtil.getViewByName("Manufacturing");
			QuerySpec qs = new QuerySpec(WTPart.class);
			SearchCondition latest = VersionControlHelper.getSearchCondition(WTPart.class, true);
			qs.appendSearchCondition(latest);
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTPart.class, "view.key.id", SearchCondition.EQUAL, view.getPersistInfo().getObjectIdentifier().getId()), index);
			// qr里存储的是每个大版本的最新小版本,按顺序存放如：A.2,B.1,C.4
			// 如果要取得C.4，则需要while循环到最后
			QueryResult qr = PersistenceHelper.manager.find(qs);
			IBAUtility ibaUtility;
			int cellCount = 1;
			String tempPath = PropertiesUtil.getTempPath();
			FileOutputStream stream = null;
			try {
				stream = new FileOutputStream(tempPath+File.separator+"双份主工艺导出TEST.xls");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("部件双份主工艺汇总");
			HSSFRow row = sheet.createRow(0);
			HSSFCellStyle style = workbook.createCellStyle();
			style.setVerticalAlignment(VerticalAlignment.CENTER); // 使用枚举值
			style.setAlignment(HorizontalAlignment.CENTER);

			HSSFCell cell = null;
			String[] title = { "部件编号", "工艺文件编号", "工艺文件修改者", "部门", "工艺文件版本","部件版本", "批准时间" };
			for (int i = 0; i < title.length; i++) {
				cell = row.createCell(i);
				cell.setCellValue(title[i]);
				cell.setCellStyle(style);
			}
			WTDocument beforeDoc = null;
			while (qr.hasMoreElements()) {
				WTPart wtPart = (WTPart) qr.nextElement();
				// out.print(wtPart.getNumber() + "|" +
				// wtPart.getIterationDisplayIdentifier().toString() + "</br>");
				List<WTDocument> documentList = WTPartUtil.getDescribedDocumentByPart(wtPart, "casc.sast.149.PROCESS_PLAN");
				int count = 0;

				for (WTDocument wtDocument : documentList) {
					ibaUtility = new IBAUtility(wtDocument);
					String type = ibaUtility.getIBAValue("PPLANTYPE");
					String zfflag = ibaUtility.getIBAValue("ZFFLAG");
					String state = wtDocument.getState().getState().getDisplay(Locale.CHINA);
					if ("正式工艺文件".equals(type) && "Z".equals(zfflag) && !"已作废".equals(state)) {
						count += 1;
						if(count == 1){
							beforeDoc = wtDocument;
						}
						if (count > 1) {
							IBAUtility utility = new IBAUtility(wtPart);
							String number = wtPart.getNumber();
							String department = utility.getIBAValue("ZZCJ");
							String wtPartidentifier = wtPart.getIterationDisplayIdentifier().toString();
							if(count == 2 && beforeDoc != null){
								String modifier = beforeDoc.getModifierFullName();
								String identifier = beforeDoc.getIterationDisplayIdentifier().toString();
								String approvalTime = getPiZhunTime(beforeDoc);
								String docNumber = beforeDoc.getNumber();
								row = sheet.createRow(cellCount);
								row.createCell(0).setCellValue(number);
								row.createCell(1).setCellValue(docNumber);
								row.createCell(2).setCellValue(modifier);
								row.createCell(3).setCellValue(department);
								row.createCell(4).setCellValue(identifier);
								row.createCell(5).setCellValue(wtPartidentifier);
								row.createCell(6).setCellValue(approvalTime);
								cellCount++;
							}
							String modifier = wtDocument.getModifierFullName();
							String identifier = wtDocument.getIterationDisplayIdentifier().toString();
							String approvalTime = getPiZhunTime(wtDocument);
							String docNumber = wtDocument.getNumber();
							row = sheet.createRow(cellCount);
							row.createCell(0).setCellValue(number);
							row.createCell(1).setCellValue(docNumber);
							row.createCell(2).setCellValue(modifier);
							row.createCell(3).setCellValue(department);
							row.createCell(4).setCellValue(identifier);
							row.createCell(5).setCellValue(wtPartidentifier);
							row.createCell(6).setCellValue(approvalTime);
							cellCount++;

						}
					}
				}
			}
			try {
				workbook.write(stream);
				stream.flush();
				stream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	private static String getPiZhunTime(WTDocument wtDocument) {
		String approvalTime = "";
		QueryResult qrProcs;
		try {
			qrProcs = WfEngineHelper.service.getAssociatedProcesses(wtDocument, null, null);
			WfProcess proc = null;
			while (qrProcs.hasMoreElements()) {
				WfProcess process = (WfProcess) qrProcs.nextElement();
				if (proc != null) {
					if (process.getStartTime().after(proc.getStartTime())) {
						proc = process;
					}
				} else {
					proc = process;
				}
			}
			if (proc != null) {
				List<WfAssignedActivity> activityList = new ArrayList<WfAssignedActivity>();
				activityList = PrintHelper.getActivities(proc, activityList);
				Iterator iterator = activityList.iterator();
				while (iterator.hasNext()) {
					WfAssignedActivity wfactivity = (WfAssignedActivity) iterator.next();
					String activityName = wfactivity.getName();
					if (activityName.equals("批准")) {
						Timestamp endTime = wfactivity.getEndTime();
						if (endTime != null) {
							approvalTime = WTStandardDateFormat.format(wfactivity.getEndTime(), "yyyy/MM/dd");
						}
					}
				}
			}

		} catch (WTException e1) {
			e1.printStackTrace();
		}
		return approvalTime;
	}


}
