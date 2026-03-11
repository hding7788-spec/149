package ext.casc.tools;

import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.UserUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import ext.casc.process.ProcessConstants;
import ext.casc.process.ProcessTask;
import ext.casc.process.ProcessTaskItem;
import ext.casc.process.util.ProcessUtil;
import wt.doc.WTDocument;
import wt.fc.*;
import wt.lifecycle.LifeCycleState;
import wt.lifecycle.State;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.vc.config.LatestConfigSpec;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

public class BatchUpdateTechnicsTool implements RemoteAccess {

	public static void process(String users) {
		try {
			String[] userIds = users.split(",");
			for(String userName : userIds) {
				WTUser user = UserUtil.getUser(userName);
				if(user != null) {
					System.out.println("开始查询用户：" + userName);
					QuerySpec qs = new QuerySpec(WTDocument.class);
					qs.setAdvancedQueryEnabled(true);
					ClassAttribute caId = new ClassAttribute(WTDocument.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
					qs.appendWhere(new SearchCondition(WTDocument.class, "iterationInfo.modifier.key.id", SearchCondition.EQUAL, user.getPersistInfo().getObjectIdentifier().getId()), new int[]{0});
					qs.appendAnd();
					qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LIFE_CYCLE_STATE, SearchCondition.EQUAL, "INWORK"), new int[]{0});
					qs.appendAnd();
					qs.appendOpenParen();
					SubSelectExpression se = IBAHelper.getStringIBAQuery("CLDEZT", "已批准");
					qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, se), new int[]{0});
					qs.appendCloseParen();
					QueryResult qr = PersistenceHelper.manager.find(qs);
					LatestConfigSpec lcs = new LatestConfigSpec();
					qr = lcs.process(qr);
					System.out.println(user.getFullName() + "查询到了" + qr.size() + "条数据，开始处理......");
					while(qr.hasMoreElements()) {
						WTDocument document = (WTDocument) qr.nextElement();
						LifeCycleState state = LifeCycleState.newLifeCycleState();
						state.setState(State.toState("APPROVED"));
						document.setState(state);
						PersistenceHelper.manager.save(document);

						WTPart part = WTDocumentUtil.getLatestDescribesWTPartsByDocument(document);
						if(part != null) {
							QuerySpec querySpec = new QuerySpec(ProcessTaskItem.class);
							querySpec.appendWhere(new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.NUMBER, SearchCondition.EQUAL, part.getNumber(), false), new int[]{0});
							querySpec.appendAnd();
							querySpec.appendWhere(new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.OWNER, SearchCondition.EQUAL, user.getName(), false), new int[]{0});
							QueryResult queryResult = PersistenceHelper.manager.find(querySpec);
							while (queryResult.hasMoreElements()) {
								ProcessTaskItem taskItem = (ProcessTaskItem) queryResult.nextElement();
								if(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN.equals(taskItem.getTaskItemState())) {
									taskItem.setCompletedBy(taskItem.getOwner());
									taskItem.setRouteSelect(ProcessConstants.TASKITEM_ROUTESELECT_WANCHENGRENWU);
									taskItem.setTaskItemState(ProcessConstants.TASKITEM_STATE_YIWANCHENG);
									taskItem = (ProcessTaskItem) PersistenceHelper.manager.save(taskItem);

									QueryResult qResult = ProcessUtil.getAllProcessTaskItemByPTask(taskItem.getProcessTaskId());
									boolean flag = true;
									while (qResult.hasMoreElements()) {
										ProcessTaskItem tempTaskItem = (ProcessTaskItem) qResult.nextElement();
										if (ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN.equals(tempTaskItem.getTaskItemState())) {
											flag = false;
											break;
										}
									}
									if (flag) {
										ProcessTask processTask = ProcessUtil.getProcessTask(taskItem.getProcessTaskId());
										processTask.setTaskState(ProcessConstants.TASK_STATE_YIWANGONG);
										PersistenceHelper.manager.save(processTask);
									}
								}

							}
						}

					}
				} else {
					System.out.println("未查询到用户：" + userName);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		String username = "wcadmin";
		String passwd = "Admin@149.941";
		String users = "";
		if (args.length >= 3) {
			username = args[0];
			passwd = args[1];
			if (username == null)
				username = "wcadmin";
			if (passwd == null)
				passwd = "Admin@149.941";
			if(users != null)
				users = args[2];
		}else {
			if(users != null)
				users = args[0];
		}
		System.out.println("------user:"+username+"    password:"+passwd);
		rms.setUserName(username);
		rms.setPassword(passwd);
		if (!RemoteMethodServer.ServerFlag) {
			Class<?>[] types = null;
			Object[] vals = null;
			types = new Class<?>[] {String.class};
			vals = new Object[] { users};
			if (types != null && vals != null) {
				try {
					rms.invoke("process", BatchUpdateTechnicsTool.class.getName(), null, types, vals);
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}else{
			BatchUpdateTechnicsTool.process(users);

		}
	}



}
