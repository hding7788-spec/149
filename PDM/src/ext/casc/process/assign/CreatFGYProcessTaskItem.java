package ext.casc.process.assign;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import wt.fc.PersistenceHelper;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.folder.FolderNotFoundException;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.glaway.mpm.mpmresource.processors.CustomerObjectFormProcessor;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;

import ext.casc.process.ProcessConstants;
import ext.casc.process.ProcessPlan;
import ext.casc.process.ProcessTask;
import ext.casc.process.ProcessTaskItem;
import ext.casc.process.util.ProcessUtil;

public class CreatFGYProcessTaskItem extends CustomerObjectFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean arg0, List<ObjectBean> arg1)
			throws WTException {

		FormResult formResult = new FormResult();
		FeedbackMessage message = new FeedbackMessage();
		StringBuffer msg = new StringBuffer();
		FormProcessingStatus formProcessingStatus = FormProcessingStatus.SUCCESS;
		Transaction tx = new Transaction();
		WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
		HttpServletRequest request = arg0.getRequest();
		WTContainer container = null;
		Map requestmap = request.getParameterMap();
		Iterator iterator = requestmap.keySet().iterator();
		List<NmOid> allProcessTask = new ArrayList<NmOid>();
		Map<String, List<WTUser>> gongYiZuZhangmap = ProcessUtil.getGroupAndUsersInOrgContainer();
		try {
			tx.start();
			ProcessTask task = null;
			long longId = 0;
			WTContainer wcontainer=null;
			List<NmContext> nmcontexts = arg0.getSelected();
			for(NmContext nmContext:nmcontexts){
				NmOid nmOid = nmContext.getTargetOid();
				allProcessTask.add(nmOid);
			}

		if(allProcessTask!=null && allProcessTask.size()>0){
			for(NmOid oid:allProcessTask){
				Object object = oid.getRefObject();
				if (object instanceof ProcessTask) {
					task = (ProcessTask) object;
					if(task.getTaskState()!=null && !"null".equals(task.getTaskState()) && !"".equals(task.getTaskState())){
						continue;
					}
					List<WTUser> gongYiZuZhangList = gongYiZuZhangmap.get(task.getZhuzhichejian());
					longId = PersistenceHelper.getObjectIdentifier(task)
							.getId();
	                for (WTUser wtUser : gongYiZuZhangList) {
					ProcessTaskItem taskitem = ProcessTaskItem.newProcessTaskItem();
					taskitem.setProcessTaskId(longId);
					taskitem.setOwner(wtUser.getName());
					taskitem.setName(task.getName());
					taskitem.setNumber(String.valueOf(new Date().getTime()+""));
					taskitem.setEndDate(task.getEndDate());
					taskitem.setRenwuyaoqiu(task.getRenwuyaoqiu());
					taskitem.setZhurengongyishi(currentUser.getName());
					taskitem.setTaskItemName(ProcessConstants.TASK_NAME_FEIGONGYISHEJIZHIPAI);
					taskitem.setTaskType(ProcessConstants.TASK_TYPE_FEIGONGYISHEJI);
					taskitem.setTaskItemState(ProcessConstants.TASK_STATE_FEIGONGZHIPAIZHONG);
					taskitem.setExecutorRole(ProcessConstants.ROLE_GONGYIZUZHANG);
					wcontainer = task.getContainer();
					taskitem.setContainer(wcontainer);
					taskitem.setChejian(task.getZhuzhichejian());
					Folder folder = getFolder("/Default", wcontainer);
					FolderHelper.assignLocation((FolderEntry) taskitem, folder);
					taskitem = (ProcessTaskItem) PersistenceHelper.manager.save(taskitem);

	              }

			   }

				task.setTaskState(ProcessConstants.TASK_STATE_FEIGONGZHIPAIZHONG);
                System.out.println("---------ProcessTask " + task.getNumber() + " completed!");
                PersistenceHelper.manager.save(task);
		    }
			msg.append(ProcessConstants.JSP_ACTIONS_ZHIPAIFGYPROCESSTASKITEM_SUCCESS);
			tx.commit();
			tx = null;
		}
		}catch (WTPropertyVetoException e) {
			formProcessingStatus = FormProcessingStatus.FAILURE;
			msg.append(ProcessConstants.JSP_ACTIONS_ZHIPAIFGYPROCESSTASKITEM_FAILED);
			e.printStackTrace();
		} finally {
			if (tx != null) {
				tx.rollback();
			}
			formResult.setStatus(formProcessingStatus);
			message.addMessage(msg.toString());
			formResult.addFeedbackMessage(message);
			formResult.setNextAction(FormResultAction.NONE);
			formResult.setNextAction(FormResultAction.REFRESH_OPENER);
		}

		return formResult;
	}

	private static Folder getFolder(String path, WTContainer con)
			throws WTException {
		Folder folder = null;
		StringTokenizer tokenizer = new StringTokenizer(path, "/");
		String subPath = "";
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			subPath = subPath + "/" + token;
			if (subPath != null && !subPath.equalsIgnoreCase("")) {
				try {
					folder = FolderHelper.service.getFolder(subPath,
							WTContainerRef.newWTContainerRef(con));
				} catch (FolderNotFoundException e) {
					boolean flag = SessionServerHelper.manager
							.setAccessEnforced(false);
					folder = FolderHelper.service.createSubFolder(subPath,
							WTContainerRef.newWTContainerRef(con));
					SessionServerHelper.manager.setAccessEnforced(flag);
				}
			}
		}
		return folder;
	}

}
