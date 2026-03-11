package ext.casc.tools;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ext.casc.workflow.PrintHelper;


import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTStandardDateFormat;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;
import wt.workflow.engine.WfTransition;
import wt.workflow.status.WfWorkflowStatusHelper;
import wt.workflow.work.WfAssignedActivity;

public class TerminateProcess {
	public static void stopProcessByNameAndTime(String beginTime,String endTime,String pocessName) throws WTException, ParseException {
		if(endTime == null || "".equals(endTime)){
			return;
		}
		Date dateFrom1 = null;
		if(beginTime != null && !"".equals(beginTime)){
			dateFrom1 = WTStandardDateFormat.parse(beginTime, "yyyy/MM/dd");
		}
		Date dateFrom2 = WTStandardDateFormat.parse(endTime, "yyyy/MM/dd");
		QuerySpec qSpec = new QuerySpec(WfProcess.class);
		int[] index = { 0 };
		SearchCondition sCondition = new SearchCondition(WfProcess.class, "name", SearchCondition.LIKE, "%"+pocessName+"%");
		qSpec.appendWhere(sCondition, index);
		if(beginTime != null && !"".equals(beginTime)){
			qSpec.appendAnd();
			SearchCondition condition = new SearchCondition(WfProcess.class, "thePersistInfo.createStamp", SearchCondition.GREATER_THAN_OR_EQUAL,
					new Timestamp(dateFrom1.getTime()));
			qSpec.appendWhere(condition, index);
		}
		qSpec.appendAnd();
		SearchCondition condition = new SearchCondition(WfProcess.class, "thePersistInfo.createStamp", SearchCondition.LESS_THAN_OR_EQUAL,
				new Timestamp(dateFrom2.getTime()));
		qSpec.appendWhere(condition, index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		while(qResult.hasMoreElements()) {
			WfProcess process = (WfProcess) qResult.nextElement();
			System.out.println("process name:"+process.getName());
			System.out.println("process before state:" + process.getState());
			if (WfState.CLOSED.includes(process.getState())){
				continue;
			}
			List<WfAssignedActivity> list = new ArrayList<WfAssignedActivity>();
			list = PrintHelper.getActivities(process, list);
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				WfAssignedActivity ac = (WfAssignedActivity) iterator.next();
				String name = ac.getName();
				System.out.println("WfAssignedActivity name:"+name);
				if(name.equals("设置分发部门及份数")){
					String state = ac.getState().toString();
					System.out.println("WfAssignedActivity state:"+state);
					if(state.equals("OPEN_RUNNING")){
						WfEngineHelper.service.changeState(process, WfTransition.TERMINATE);
						WfWorkflowStatusHelper.service.cleanOrphanedWorkItems(process);
						PersistenceHelper.manager.refresh(process);
					}

				}

			}



		}

	}
}
