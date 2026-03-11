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
import wt.fc.ReferenceFactory;
import wt.fc.WTReference;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.folder.FolderNotFoundException;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.Transaction;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.glaway.mpm.mpmresource.processors.CustomerObjectFormProcessor;
import com.ptc.core.components.beans.ObjectBean;
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

public class CreatChildProcessPlan extends CustomerObjectFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean arg0, List<ObjectBean> arg1)
			throws WTException {
		FormResult formResult = new FormResult();
		FeedbackMessage message = new FeedbackMessage();
		StringBuffer msg = new StringBuffer();
		FormProcessingStatus formProcessingStatus = FormProcessingStatus.SUCCESS;
		Transaction tx = new Transaction();

		HttpServletRequest request = arg0.getRequest();

		WTContainer container = null;
		Map requestmap = request.getParameterMap();
		Iterator iterator = requestmap.keySet().iterator();
		ProcessTask task = ProcessTask.newProcessTask();
		try {
			tx.start();
			ProcessPlan plan = null;
			long longId = 0;
			WTContainer wcontainer=null;
			List<NmOid> allTask = new ArrayList<NmOid>();

			NmOid nmOid = arg0.getPageOid();
			if (nmOid != null) {
				allTask.add(nmOid);
			} else {
				List<NmContext> list = arg0.getSelected();
				for (NmContext nmContext : list) {
					nmOid = nmContext.getTargetOid();
					allTask.add(nmOid);
				}
			}

			for (NmOid Oid : allTask) {
				Object object = Oid.getRefObject();
				if (object instanceof ProcessPlan) {
					plan = (ProcessPlan) object;
					longId = PersistenceHelper.getObjectIdentifier(plan)
							.getId();
					wcontainer = plan.getContainer();
					if (longId != 0) {
						break;
					}
				}
			}
			String name="";
			String zhixingbumen="";
			String date="";

			while (iterator.hasNext()) {
				String key = String.valueOf(iterator.next());
				if ("name".equals(key)) {
					name = request.getParameter(key);
					task.setName(name);
				} else if ("zhixingbumen".equals(key)) {
					zhixingbumen = request.getParameter(key);
					task.setZhuzhichejian(zhixingbumen);
				} else if ("endDate".equals(key)) {
					date = request.getParameter(key);
					DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
					try {
						Date d = df.parse(date);
						task.setEndDate(new Timestamp(d.getTime()));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if ("renwuyaoqiu".equals(key)) {
					task.setRenwuyaoqiu(request.getParameter(key));
				}

			}

			if("".equals(name) || "".equals(zhixingbumen) || "".equals(date)){
				throw new Exception();
			}
			task.setProcessPlanId(longId);
			task.setNumber(new Date().getTime()+"");
			task.setContainer(wcontainer);
			Folder folder = getFolder("/Default", wcontainer);
			FolderHelper.assignLocation((FolderEntry) task, folder);
			task = (ProcessTask) PersistenceHelper.manager.save(task);

			msg.append(ProcessConstants.JSP_ACTIONS_NEWCHILDPROCESSPLAN_SUCCESS);
			tx.commit();
			tx = null;
		} catch (WTPropertyVetoException e) {
			formProcessingStatus = FormProcessingStatus.FAILURE;
			msg.append(ProcessConstants.JSP_ACTIONS_NEWCHILDPROCESSPLAN_FAILED);
			e.printStackTrace();
		} catch (Exception e) {
			formProcessingStatus = FormProcessingStatus.FAILURE;
			msg.append(ProcessConstants.JSP_JS_VALIDATE);
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
