package ext.casc.process.assign;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.ResultSet;
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
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.method.RemoteMethodServer;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.Transaction;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.glaway.mpm.mpmresource.processors.CustomerObjectFormProcessor;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.validation.UIValidationStatus;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.number.NumberMgt;
import ext.casc.process.ProcessConstants;
import ext.casc.process.ProcessPlan;
import ext.casc.util.DBConn;

public class CreatProcessPlan extends CustomerObjectFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean arg0, List<ObjectBean> arg1) throws WTException {
		FormResult formResult = new FormResult();
		FeedbackMessage message = new FeedbackMessage();
		StringBuffer msg = new StringBuffer();
		FormProcessingStatus formProcessingStatus = FormProcessingStatus.SUCCESS;
		Transaction tx = new Transaction();

		HttpServletRequest request = arg0.getRequest();
		WTContainer container = null;
		Map requestmap = request.getParameterMap();
		Iterator iterator = requestmap.keySet().iterator();
		WTUser currentUser = (WTUser) SessionHelper.getPrincipal();

		try {
			String name = "";
			String zhuti = "";
			String xiangmubu = "";
			boolean isZRGYS= false;
			while (iterator.hasNext()) {
				String key = String.valueOf(iterator.next());
				if ("name".equals(key)) {
					name = request.getParameter(key);
				}/*
				 * else if("number".equals(key)){
				 * newProcessPlan.setNumber(request.getParameter(key)); }
				 */else if ("zhuti".equals(key)) {
					 zhuti = request.getParameter(key);
				} else if ("xiangmubu".equals(key)) {
					xiangmubu = request.getParameter(key);
				} else if ("containerTypeList".equals(key)) {
					ReferenceFactory rf = new ReferenceFactory();
					WTReference wtreference = rf.getReference(request.getParameter(key));
					container = (PDMLinkProduct) wtreference.getObject();

					if(container!=null){
						ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) container);
						Role role = Role.toRole("ZHURENGONGYISHI");
						ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
						for (WTPrincipalReference reference : arrayList) {
							Object object2 = reference.getPrincipal();
							if (object2 instanceof WTUser) {
								WTUser user = (WTUser) object2;
								if (user.getName().equals(currentUser.getName())) {
									isZRGYS = true;
								}
							}else if (object2 instanceof WTGroup) {
								WTGroup group = (WTGroup) object2;
								if (group.isMember(currentUser)) {
									isZRGYS = true;
								}
							}
						}
					}
				}
			}

			if(isZRGYS){
				tx.start();
				ProcessPlan newProcessPlan = ProcessPlan.newProcessTask();
				newProcessPlan.setName(name);
				newProcessPlan.setZhuti(zhuti);
				newProcessPlan.setXiangmubu(xiangmubu);
				newProcessPlan.setContainer(container);
				// String number2 =Long.toString(getNumber(1,"newProcessPlan")) ;
				String number2 = Long.toString(new Date().getTime());
				newProcessPlan.setNumber(number2);
				Folder folder = getFolder("/Default", container);
				FolderHelper.assignLocation((FolderEntry) newProcessPlan, folder);
				newProcessPlan = (ProcessPlan) PersistenceHelper.manager.save(newProcessPlan);

				msg.append(ProcessConstants.JSP_ACTIONS_NEWPROCESSPLAN_SUCCESS);
				tx.commit();
				tx = null;
			}else{
				msg.append("无权限，您不是该型号的主任工艺师。请联系管理员把对应账户添加到型号的主任工艺师角色中");
				throw new WTException("无权限，您不是该型号的主任工艺师。请联系管理员把对应账户添加到型号的主任工艺师角色中！");
			}
		} catch (WTPropertyVetoException e) {
			formProcessingStatus = FormProcessingStatus.FAILURE;
			msg.append(ProcessConstants.JSP_ACTIONS_NEWPROCESSPLAN_FAILED);
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

	private static Folder getFolder(String path, WTContainer con) throws WTException {
		Folder folder = null;
		StringTokenizer tokenizer = new StringTokenizer(path, "/");
		String subPath = "";
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			subPath = subPath + "/" + token;
			if (subPath != null && !subPath.equalsIgnoreCase("")) {
				try {
					folder = FolderHelper.service.getFolder(subPath, WTContainerRef.newWTContainerRef(con));
				} catch (FolderNotFoundException e) {
					boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
					folder = FolderHelper.service.createSubFolder(subPath, WTContainerRef.newWTContainerRef(con));
					SessionServerHelper.manager.setAccessEnforced(flag);
				}
			}
		}
		return folder;
	}

	public static synchronized long getNumber(Integer type, String pre) {
		if (!RemoteMethodServer.ServerFlag) {
			String method = "getNumber";
			Class[] types = { Integer.class, String.class };
			Object[] vals = { type, pre };
			RemoteMethodServer rms = RemoteMethodServer.getDefault();
			try {
				return (Long) rms.invoke(method, NumberMgt.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		StringBuilder sql = new StringBuilder("select NUM FROM ");
		StringBuilder isql = new StringBuilder("insert into ");
		StringBuilder usql = new StringBuilder("update ");

		sql.append("GL_GYFA_SEQ ").append("WHERE PRE='").append(pre).append("'");
		isql.append(" GL_GYFA_SEQ (PRE,NUM) values ('").append(pre).append("',");
		usql.append(" GL_GYFA_SEQ set NUM=");

		DBConn conn = null;
		long num = 0;
		try {
			conn = new DBConn();
			ResultSet rs = conn.executeQuery(sql.toString());
			if (rs.next()) {
				num = rs.getLong("NUM");
			}
			if (num == 0) {
				num = 1;
				isql.append(num).append(")");
				conn.executeUpdate(isql.toString());
			} else {
				num = num + 1;
				usql.append(num).append(" WHERE PRE='").append(pre).append("'");
				conn.executeUpdate(usql.toString());
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return num;
	}

}
