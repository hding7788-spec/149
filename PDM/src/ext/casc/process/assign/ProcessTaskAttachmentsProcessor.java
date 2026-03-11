package ext.casc.process.assign;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.PersistenceHelper;
import wt.inf.container.WTContainer;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;

import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;

import ext.casc.process.ProcessTask;
import ext.casc.process.ProcessTaskItem;
import ext.casc.process.util.ProcessUtil;

public class ProcessTaskAttachmentsProcessor {
	public static FormResult uploadFile(NmCommandBean cb) throws WTException, RemoteException {
        String file = cb.getTextParameter("file");
		NmOid nmOid = cb.getPageOid();
		Object obj = nmOid.getRefObject();
		ProcessTask processTask = null;
        if (obj instanceof ProcessTaskItem) {
        	ProcessTaskItem taskItem = (ProcessTaskItem)obj;
        	processTask = ProcessUtil.getProcessTask(taskItem.getProcessTaskId());
        } else if (obj instanceof ProcessTask) {
            processTask = (ProcessTask)obj;

        }

        if(processTask == null) {
        	return null;
        }

        File temp_zipfile = (File) cb.getRequest().getAttribute("file");

        FormResult form = new FormResult();
        Transaction tran = new Transaction();
        try {
        	tran.start();
        	boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        	InputStream is = new FileInputStream(temp_zipfile);
        	ContentHolder holder = (ContentHolder) ContentHelper.service.getContents((ContentHolder)processTask);
            holder = (ContentHolder) PersistenceHelper.manager.refresh(holder);
            ApplicationData data = ApplicationData.newApplicationData(holder);
            data.setRole(ContentRoleType.SECONDARY);
            data.setFileName(file);
            data.setUploadedFromPath(file);
            ContentServerHelper.service.updateContent(holder, data, is);

            is.close();
            SessionServerHelper.manager.setAccessEnforced(flag);

            tran.commit();
            tran = null;

        	form.setStatus(FormProcessingStatus.SUCCESS);
            FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null, null, null, "附件上载成功！");
            form.addFeedbackMessage(message);
            form.setNextAction(FormResultAction.REFRESH_OPENER);

        } catch (PropertyVetoException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
            if (temp_zipfile != null) {
                temp_zipfile.delete();
            }
            if (tran != null) {
                tran.rollback();
            }
        }
        return form;
    }

public static FormResult deleteFile(NmCommandBean cb) throws WTException, RemoteException {
	FormResult form = new FormResult();
	WTPrincipal currentUser = SessionHelper.manager.getPrincipal();
    boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);

	try {
		NmOid nmOid = cb.getPageOid();
		Object obj = nmOid.getRefObject();
		ProcessTask processTask = null;
        if (obj instanceof ProcessTaskItem) {
        	ProcessTaskItem taskItem = (ProcessTaskItem)obj;
        	processTask = ProcessUtil.getProcessTask(taskItem.getProcessTaskId());
        } else if (obj instanceof ProcessTask) {
            processTask = (ProcessTask)obj;

        }

        if(processTask == null) {
        	return null;
        }

		ArrayList list = cb.getSelected();

		//判断是否是删除自己上传的附件,主任工艺师可以删除所有的附件
		WTUser user = (WTUser)SessionHelper.manager.getPrincipal();
		WTContainer container = cb.getContainer();
		String roleKey = "ZHURENGONGYISHI";
//		if(!ProcessUtil.isRoleMember(user, roleKey, container)) {
			for (Object object : list) {
				NmContext nmContext = (NmContext)object;
				ApplicationData seleData = (ApplicationData)nmContext.getTargetOid().getRefObject();
				WTPrincipal creator = (WTPrincipal)seleData.getCreatedBy().getObject();
				if(!currentUser.equals(creator)) {
					form.setStatus(FormProcessingStatus.FAILURE);
					FeedbackMessage message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "只能删除自己上传的附件！");
					form.addFeedbackMessage(message);
					form.setNextAction(FormResultAction.REFRESH_OPENER);
					return form;
				}
			}
//		}

		for (Object object : list) {
			NmContext nmContext = (NmContext)object;
			ApplicationData seleData = (ApplicationData)nmContext.getTargetOid().getRefObject();
			ContentServerHelper.service.deleteContent((ContentHolder)processTask, seleData);
		}
		PersistenceHelper.manager.refresh((ContentHolder)processTask);
		form.setStatus(FormProcessingStatus.SUCCESS);
		FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null, null, null, "附件删除成功！");
		form.addFeedbackMessage(message);
		form.setNextAction(FormResultAction.REFRESH_OPENER);
	} catch (WTRuntimeException e) {
		e.printStackTrace();
	} catch (WTPropertyVetoException e) {
		e.printStackTrace();
	}finally{
		 wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
	}
    return form;
}
}
