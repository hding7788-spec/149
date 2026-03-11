package ext.casc.report;

import com.glaway.mpm.util.DBConnUtil;
import ext.casc.util.ExcelFileGenerator;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.query.*;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WfAssignment;
import wt.workflow.work.WorkItem;
import wt.workflow.work.WorkItemLink;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class AllWorkFlowService {

	public static Object getWorkFlowReport(String processName, String beginTime,
			String endTime) {

		List<AllWorkFlow> result = new ArrayList<AllWorkFlow>();
		ReferenceFactory ref = new ReferenceFactory();
		List<WfProcess> processList  = getWfProcessByNameAndTime(processName,beginTime,endTime);
		for(WfProcess process :processList){
			AllWorkFlow awf = new AllWorkFlow();
			awf.setProcessName(process.getName());
			try {
				awf.setProcessOid( ref.getReferenceString((Persistable)process));
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			awf.setTemplate(process.getTemplate().getName());
			awf.setContainerName(process.getContainerName());
			awf.setStarter(process.getCreator().getFullName());
			awf.setStartTime(process.getStartTime()+"");
			getStep(process,awf);

			awf.setState(process.getState().getDisplay(Locale.CHINA));
			result.add(awf);
		}
		return result;
	}
	private static void getStep(WfProcess process, AllWorkFlow awf) {
		String name = "";
		String userName = "";
		long ida2a2 = process.getPersistInfo().getObjectIdentifier().getId();
		DBConnUtil dbUtil = null;
		ResultSet resultset = null;
		try {
			dbUtil = new DBConnUtil();
			String sql = "select wfb.ida2a2 from WfRequesterActivity wfr,wfblock wfb where wfb.ida3a6=wfr.ida2a2 and wfr.ida3parentprocessref="+ida2a2;
			resultset = dbUtil.executeQuery(sql);
			long blockId= 1;
			while (resultset.next()) {
				blockId= resultset.getLong(1);
			}
			QuerySpec qs = new QuerySpec(WfAssignedActivity.class);
			qs.appendWhere(new SearchCondition(WfAssignedActivity.class, WfAssignedActivity.STATE,
					SearchCondition.EQUAL, WfState.OPEN_RUNNING));
			qs.appendAnd();
			if (blockId!=1) {
				qs.appendOpenParen();
				qs.appendWhere(new SearchCondition(WfAssignedActivity.class, "parentProcessRef.key.id",
						SearchCondition.EQUAL,ida2a2));
				qs.appendOr();
				qs.appendWhere(new SearchCondition(WfAssignedActivity.class, "parentProcessRef.key.id",
						SearchCondition.EQUAL,blockId));
				qs.appendCloseParen();
			}else{
				qs.appendWhere(new SearchCondition(WfAssignedActivity.class, "parentProcessRef.key.id",
						SearchCondition.EQUAL,ida2a2));
			}
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				WfAssignedActivity wfa = (WfAssignedActivity) qr.nextElement();
				name=name+"  "+wfa.getName();
				userName = "";
				Enumeration enu = wfa.getAssignments();
				while (enu.hasMoreElements()) {
					WfAssignment wa = (WfAssignment) enu.nextElement();
					QueryResult qrItems = PersistenceHelper.manager.navigate(
							wa, WorkItemLink.WORK_ITEM_ROLE,
							WorkItemLink.class, true);

					while (qrItems.hasMoreElements()) {
						WorkItem wi = (WorkItem) qrItems.nextElement();
						if(!wi.isComplete()){

							WTPrincipalReference user = wi.getOwnership()
							.getOwner();
							WTUser u = (WTUser)user.getObject();
							userName= userName  +u.getFullName()+ "、";


						}
					}
				}
				if(userName.endsWith("、")){
					userName = userName.substring(0,userName.length()-1);
				}
				name+="("+userName+")；";
			}

			awf.setRunningActivity(name);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(resultset!=null)
					resultset.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (dbUtil != null) {
					dbUtil.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
	public static File genReport(String processName, String beginTime,
			String endTime) {
		if (!RemoteMethodServer.ServerFlag) {
			String method = "genReport";
			Class[] types = {String.class,String.class,String.class};
			Object[] vals = {processName,beginTime,endTime};
			try {
				RemoteMethodServer rms = RemoteMethodServer.getDefault();
				return (File)rms.invoke(method, AllWorkFlowService.class.getName(), null,
						types, vals);
			} catch (Exception e) {
				// System.out.println("Exception:", e.getMessage());
				e.printStackTrace();
			}
			return null;
		}
		ArrayList<String> titles = new ArrayList<String>();
		titles.add("流程名称");
		titles.add("流程模板");
		titles.add("产品库名称");
		titles.add("启动者");
		titles.add("启动时间");
		titles.add("运行的活动和负责人");
		titles.add("状态");
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
			WTProperties wtp = WTProperties.getLocalProperties();
			String temp = wtp.getProperty("wt.temp");
			List<WfProcess> processList = getWfProcessByNameAndTime(processName,beginTime,endTime);
			ArrayList<ArrayList<String>> values =buildValues(processList);
			ExcelFileGenerator gen = new ExcelFileGenerator(titles,values);
			File file = new File(temp + File.separator +"流程报表"+sdf.format(new Date())+".xls");
			gen.expordExcel(new FileOutputStream(file));
			return file;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return null;
	}

	public static ArrayList<ArrayList<String>> buildValues(
			List<WfProcess> processList) {
		ArrayList<ArrayList<String>> allList = new ArrayList<ArrayList<String>>();
		for(WfProcess process :processList){
			ArrayList<String> list = new ArrayList<String>();
			list.add(process.getName());
			list.add(process.getTemplate().getName());
			list.add(process.getContainerName());
			list.add(process.getCreator().getFullName());
			list.add(process.getStartTime()+"");

			getStep(process,list);
			list.add(process.getState().getDisplay(Locale.CHINA));
			allList.add(list);
		}
		return allList;
	}

	public static List<WfProcess> getWfProcessByTemplateName(String name) {
		List<WfProcess> list = new ArrayList<WfProcess>();
		try {
			WfProcess process = null;
			QuerySpec qs = new QuerySpec(WfProcess.class);
			qs.appendWhere(new SearchCondition(WfProcess.class, WfProcess.NAME,
					SearchCondition.LIKE, name+"%"));
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WfProcess.class, WfProcess.STATE,
					SearchCondition.EQUAL, "OPEN_RUNNING"));
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				process = (WfProcess) qr.nextElement();
				list.add(process);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	public static List<WfProcess> getWfProcessByNameAndTime(String name,String beginTime,String endTime) {
		List<WfProcess> list = new ArrayList<WfProcess>();
		try {
			WfProcess process = null;
			QuerySpec qs = new QuerySpec(WfProcess.class);
			qs.appendWhere(new SearchCondition(WfProcess.class, WfProcess.STATE,
					SearchCondition.EQUAL, "OPEN_RUNNING"));
			if(name!=null&&!"".equals(name)){
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WfProcess.class, WfProcess.NAME,
						SearchCondition.LIKE, "%"+name+"%"));
			}
			if(beginTime!=null&&!"".equals(beginTime)&&endTime!=null&&!"".equals(endTime)){
				qs.appendAnd();
				SimpleDateFormat dFormat = new  SimpleDateFormat("yyyy/MM/dd");
				Date fbegin=dFormat.parse(beginTime);
				Date fend=dFormat.parse(endTime);
				Timestamp tstartDate=new Timestamp(fbegin.getTime()); //当天凌晨
				Timestamp tendDate=new Timestamp(fend.getTime());  //当前时间
				AttributeRange arange=new AttributeRange(tstartDate,tendDate);
				qs.appendWhere(new SearchCondition(WfProcess.class,WfProcess.CREATE_TIMESTAMP, true,arange));
			}
			qs.appendOrderBy(new OrderBy(new ClassAttribute(WfProcess.class,WfProcess.CREATE_TIMESTAMP), true), new int[] { 0 });
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				process = (WfProcess) qr.nextElement();
				list.add(process);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public static String getStep(WfProcess wfprocess,ArrayList<String> list){
		String name = "";
		String userName = "";
		long ida2a2 = wfprocess.getPersistInfo().getObjectIdentifier().getId();
		DBConnUtil dbUtil = null;
		ResultSet resultset = null;
		try {
			dbUtil = new DBConnUtil();
			String sql = "select wfb.ida2a2 from WfRequesterActivity wfr,wfblock wfb where wfb.ida3a6=wfr.ida2a2 and wfr.ida3parentprocessref=" + ida2a2;
			resultset = dbUtil.executeQuery(sql);
			long blockId= 1;
			while (resultset.next()) {
				blockId= resultset.getLong(1);
			}
			QuerySpec qs = new QuerySpec(WfAssignedActivity.class);
			qs.appendWhere(new SearchCondition(WfAssignedActivity.class, WfAssignedActivity.STATE,
					SearchCondition.EQUAL, WfState.OPEN_RUNNING));
			qs.appendAnd();
			if (blockId!=1) {
				qs.appendOpenParen();
				qs.appendWhere(new SearchCondition(WfAssignedActivity.class, "parentProcessRef.key.id",
						SearchCondition.EQUAL,ida2a2));
				qs.appendOr();
				qs.appendWhere(new SearchCondition(WfAssignedActivity.class, "parentProcessRef.key.id",
						SearchCondition.EQUAL,blockId));
				qs.appendCloseParen();
			}else{
				qs.appendWhere(new SearchCondition(WfAssignedActivity.class, "parentProcessRef.key.id",
						SearchCondition.EQUAL,ida2a2));
			}
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				WfAssignedActivity wfa = (WfAssignedActivity) qr.nextElement();
				name=name+"  "+wfa.getName();
				userName = "";
				Enumeration enu = wfa.getAssignments();
				while (enu.hasMoreElements()) {
					WfAssignment wa = (WfAssignment) enu.nextElement();
					QueryResult qrItems = PersistenceHelper.manager.navigate(
							wa, WorkItemLink.WORK_ITEM_ROLE,
							WorkItemLink.class, true);

					while (qrItems.hasMoreElements()) {
						WorkItem wi = (WorkItem) qrItems.nextElement();
						if(!wi.isComplete()){

							WTPrincipalReference user = wi.getOwnership()
							.getOwner();
							WTUser u = (WTUser)user.getObject();
							userName= userName  +u.getFullName()+ "、";


						}
					}
				}
				if(userName.endsWith("、")){
					userName = userName.substring(0,userName.length()-1);
				}
				name+="("+userName+")；";
			}

			list.add(name);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				if(resultset!=null)
					resultset.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (dbUtil != null) {
					dbUtil.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return name;

	}


}
