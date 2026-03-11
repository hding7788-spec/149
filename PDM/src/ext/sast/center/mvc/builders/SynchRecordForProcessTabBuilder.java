package ext.sast.center.mvc.builders;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.mvc.util.ClientMessageSource;

import ext.sast.center.bean.SynchRecord;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.util.WTException;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.Ballots;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WfAssignment;
import wt.workflow.work.WfAssignmentState;
import wt.workflow.work.WfBallot;
import wt.workflow.work.WorkItem;
import wt.workflow.work.WorkItemLink;

@ComponentBuilder("ext.sast.center.mvc.builders.SynchRecordForProcessTabBuilder")
public class SynchRecordForProcessTabBuilder extends AbstractComponentBuilder {
	private final ClientMessageSource messageSource = getMessageSource("ext.sast.center.resource.CustomResource");
	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams configParams) throws Exception {
		List<SynchRecord> list = new ArrayList<SynchRecord>();
//		NmCommandBean cb = ((JcaComponentParams)configParams).getHelperBean().getNmCommandBean();
//		ProcessEnvelope pe = (ProcessEnvelope) cb.getActionOid().getRefObject();
//		WfProcess process = getProcess(pe);
//		List<WorkItem> workItemList = getWorkItems(process);
//		for(WorkItem item : workItemList) {
//			WfAssignedActivity activity = (WfAssignedActivity) item.getSource().getObject();
//			String activityName = activity.getName();
//			String status = activity.getState().getDisplay(Locale.CHINA);
//			String principal = item.getOwnership().getOwner().getFullName();
//			String userRole = item.getRole().getDisplay(Locale.CHINA);
//			Timestamp startDate = activity.getStartTime();
//			String startTime = setTimestampToString(startDate,"yyyy-MM-dd HH:mm:ss");
//			Timestamp completeDate = activity.getModifyTimestamp();
//			String completeTime = setTimestampToString(completeDate,"yyyy-MM-dd HH:mm:ss");
//			String remarks = getAssignMent(activity);
//			
//			SynchPartRecord record = new SynchPartRecord();
//			record.setUnit("149");
//			record.setActivity_Name(activityName);
//			record.setPrincipal(principal);
//			record.setUserRole(userRole);
//			record.setStartTime(startTime);
//			record.setCompleteTime(completeTime);
//			record.setStatus(status);
//			record.setRemarks(remarks);
//			list.add(record);
//		}
		return list;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
		
		
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setLabel(messageSource.getMessage("SYNCHRECORD_LABEL"));
		table.setSelectable(false);
		table.setShowCount(true);

		ColumnConfig col = factory.newColumnConfig("unit", true);
		col.setLabel(messageSource.getMessage("SYNCHRECORD_01"));
		table.addComponent(col);

		col = factory.newColumnConfig("activity_Name", true);
		col.setLabel(messageSource.getMessage("SYNCHRECORD_02"));
		table.addComponent(col);
		
		col = factory.newColumnConfig("principal", true);
		col.setLabel(messageSource.getMessage("SYNCHRECORD_03"));
		table.addComponent(col);
		
		col = factory.newColumnConfig("userRole", true);
		col.setLabel(messageSource.getMessage("SYNCHRECORD_04"));
		table.addComponent(col);
		
		col = factory.newColumnConfig("status", true);
		col.setLabel(messageSource.getMessage("SYNCHRECORD_05"));
		table.addComponent(col);
		
		col = factory.newColumnConfig("startTime", true);
		col.setLabel(messageSource.getMessage("SYNCHRECORD_06"));
		table.addComponent(col);
		
		col = factory.newColumnConfig("completeTime", true);
		col.setLabel(messageSource.getMessage("SYNCHRECORD_07"));
		table.addComponent(col);
		
		col = factory.newColumnConfig("remarks", true);
		col.setLabel(messageSource.getMessage("SYNCHRECORD_08"));
		table.addComponent(col);
		return table;
	}

	
	private static WfProcess getProcess(Persistable pbo) {
		WfProcess process = null;
		try {
			QueryResult queryResult = WfEngineHelper.service.getAssociatedProcesses(pbo, null, null);
			if (queryResult.hasMoreElements()) {
				process = (WfProcess) queryResult.nextElement();
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return process;
	}
	
	private static List<WorkItem> getWorkItems(WfProcess process) throws WTException {
		List<WorkItem> workItemList = new ArrayList<WorkItem>();
		Enumeration<?> processSteps = WfEngineHelper.service.getProcessSteps(process, null);
		WfActivity activity = null;
		WfAssignedActivity assignedActivity = null;
		Enumeration<?> assignments = null;
		WfAssignment assignment = null;
		QueryResult queryResult = null;
		WorkItem workItem = null;
		while (processSteps.hasMoreElements()) {
			activity = (WfActivity) processSteps.nextElement();
			if (!(activity instanceof WfAssignedActivity)) {
				continue;
			}
			assignedActivity = (WfAssignedActivity) activity;
			assignments = assignedActivity.getAssignments();
			if (assignments.hasMoreElements()) {
				assignment = (WfAssignment) assignments.nextElement();
				queryResult = PersistenceHelper.manager.navigate(assignment, WorkItemLink.WORK_ITEM_ROLE, WorkItemLink.class, true);
				while (queryResult.hasMoreElements()) {
					workItem = (WorkItem) queryResult.nextElement();
					workItemList.add(workItem);
				}
			}
		}
		return workItemList;
	}
	
	private static String setTimestampToString(Timestamp time, String pattern) {
		TimeZone timeZone = TimeZone.getTimeZone("GMT+8:00");
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		sdf.setTimeZone(timeZone);
		return sdf.format(time);
	}
	
	private static String getAssignMent(WfAssignedActivity activity) {
		StringBuffer buf = new StringBuffer();
		try {
			Enumeration<?> enumeration = activity.getAssignments();
			while(enumeration.hasMoreElements()) {
				WfAssignment wa = (WfAssignment) enumeration.nextElement();
				if (wa.getStatus().equals(WfAssignmentState.COMPLETED)) {
					QueryResult qrBallots = PersistenceHelper.manager.navigate(wa, Ballots.BALLOT_ROLE, Ballots.class, true);
					while (qrBallots.hasMoreElements()) {
						WfBallot ballot = (WfBallot) qrBallots.nextElement();
						Vector<?> events = ballot.getEventList();
						for (int i = 0; events != null && i < events.size(); i++) {
							if (buf.length() > 0) {
								buf.append(",");
							}
							buf.append(events.get(i));
						}
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return buf.toString();
	}
}
