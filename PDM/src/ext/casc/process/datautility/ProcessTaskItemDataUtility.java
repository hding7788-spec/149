package ext.casc.process.datautility;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.glaway.mpm.util.UserUtil;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.inf.container.WTContainer;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;

import com.glaway.mpm.processplan.helper.ProcessPlanHelper;
import com.glaway.mpm.util.WTDocumentUtil;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.AbstractGuiComponent;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.components.rendering.guicomponents.IconComponent;
import com.ptc.core.components.rendering.guicomponents.TextBox;

import ext.casc.constants.Constants;
import ext.casc.process.ProcessConstants;
import ext.casc.process.ProcessTask;
import ext.casc.process.ProcessTaskItem;
import ext.casc.process.util.ProcessUtil;
import ext.casc.util.IBAHelper;
import ext.casc.util.IBAUtility;
import ext.casc.util.NmTableGUIComponent;

public class ProcessTaskItemDataUtility extends AbstractDataUtility {

	@Override
	public Object getDataValue(String componentId, Object object,
			ModelContext context) throws WTException {
		String oid = PersistenceHelper
				.getObjectIdentifier((Persistable) object).toString();
		Object reObj = null;
		WTContainer wtContainer = null;
		if (object instanceof ProcessTaskItem) {
			ProcessTaskItem taskItem = (ProcessTaskItem) object;
			wtContainer = taskItem.getContainer();
			if ("isComplete".equals(componentId)) {
				IconComponent localIconComponent = new IconComponent("");
				if (taskItem.getTaskItemState().equals(
						ProcessConstants.TASKITEM_STATE_YIWANCHENG)) {
					localIconComponent.setSrc("netmarkets/images/checked.gif");
				}
				return localIconComponent;
			} else if ("endDate".endsWith(componentId)) {
				Timestamp endDate = taskItem.getEndDate();
				if(endDate==null) return "";
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(endDate.getTime());
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH) + 1;
				int days = calendar.get(Calendar.DAY_OF_MONTH);
				String data = year + "/" + month + "/" + days;
				return data;
			} else if ("iszhuzhi".equals(componentId)) {
				Object oflag = taskItem.getIszhuzhi();
				if (oflag == null) {
					return "";
				} else {
					boolean flag = (Boolean) oflag;
					if (flag) {
						return ProcessConstants.TASK_ISZHUZHI_SHI;
					} else {
						return ProcessConstants.TASK_ISZHUZHI_FOU;
					}
				}

			} else if ("gongyiyuan".equals(componentId)) {
				// String chejian = taskItem.getChejian();
				// ArrayList<String> valueList =
				// getAllGongYiYuanForCheJian(wtContainer,chejian);
				// ArrayList<String> displayList =
				// getAllGongYiYuanForCheJian(wtContainer,chejian);
				// ArrayList<String> selectList =
				// getAllGongYiYuanForCheJian(wtContainer,chejian);
				// AbstractGuiComponent gui = new ComboBox(valueList,
				// displayList, selectList);
				// ((ComboBox) gui).setId(oid + "_gongyiyuan");
				// ((ComboBox) gui).setName(oid + "_gongyiyuan");
				// ((ComboBox) gui).setMultiValued(false);
				// ((ComboBox) gui).setEditable(true);
				// reObj = gui;
				// return reObj;
				ReferenceFactory rf = new ReferenceFactory();
				String veroid = rf.getReference(taskItem).toString();

				String value = "<input type=\"text\"  readonly name=\""
						+ oid
						+ "_sign_person\"  id=\""
						+ veroid
						+ "_sign_person\" /><input type=\"button\" value=\"选择\" onclick=\"javascript:showHQRY('"
						+ veroid + "');\"  id=\"" + veroid
						+ "_selecPer\"/><input type=\"hidden\"   name=\"" + oid
						+ "_sign_person_value\" id=\"" + veroid
						+ "_sign_person_value\" >";
				NmTableGUIComponent gui = new NmTableGUIComponent(value);
				GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
				guicomponentarrayMain.setValueHidden(false);
				guicomponentarrayMain.addGUIComponent(gui);
				return guicomponentarrayMain;
			} else if ("description".equals(componentId)) {
				AbstractGuiComponent gui = new TextBox();
				((TextBox) gui).setId(oid + "_description");
				((TextBox) gui).setName(oid + "_description");
				((TextBox) gui).setWidth(30);
				reObj = gui;
				return reObj;
			} else if ("owner".equals(componentId)) {
				WTUser user = ProcessUtil.getUserByName(taskItem.getOwner());
				if (user != null) {
					return user.getFullName();
				} else {
					return taskItem.getOwner();
				}
			} else if ("taskType".equals(componentId)) {
				return taskItem.getTaskType();
			} else if ("thePersistInfo.createStamp".equals(componentId)) {
				return ProcessUtil.formatTime(taskItem.getCreateTimestamp()
						.getTime());
			} else if ("thePersistInfo.modifyStamp".equals(componentId)) {
				return ProcessUtil.formatTime(taskItem.getModifyTimestamp()
						.getTime());
			} else if ("chejian".equals(componentId)) {
				return taskItem.getChejian();
			} else if ("taskState".equals(componentId)) {
				return taskItem.getTaskItemState();
			} else if ("technicsState".equals(componentId)) {
				return getTechnicsState(taskItem);
			}
		} else if (object instanceof ProcessTask) {
			ProcessTask processTask = (ProcessTask) object;
			if ("endDate".endsWith(componentId)) {
				Timestamp endDate = processTask.getEndDate();
				if(endDate==null) return "";
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(endDate.getTime());
				return calendar.get(Calendar.YEAR) + "/"
						+ (calendar.get(Calendar.MONTH) + 1) + "/"
						+ calendar.get(Calendar.DAY_OF_MONTH);
			} else if ("taskType".equals(componentId)) {
				return processTask.getTaskType();
			} else if ("fuzhichejian".equals(componentId)) {
				String fuzhichejian = processTask.getFuzhichejian();
				if (fuzhichejian == null) {
					fuzhichejian = "";
				}
				fuzhichejian = fuzhichejian.replaceAll("&", "-");
				return fuzhichejian;
			} else if ("thePersistInfo.createStamp".equals(componentId)) {
				return ProcessUtil.formatTime(processTask.getCreateTimestamp()
						.getTime());
			} else if ("thePersistInfo.modifyStamp".equals(componentId)) {
				return ProcessUtil.formatTime(processTask.getModifyTimestamp()
						.getTime());
			} else if ("userName".equals(componentId)) {
				QueryResult qr = ProcessUtil.getAllProcessTaskItemByPTask(PersistenceHelper.getObjectIdentifier(processTask).getId());
				while (qr.hasMoreElements()) {
					ProcessTaskItem taskItem = (ProcessTaskItem) qr.nextElement();
					String role = taskItem.getExecutorRole();
					if ("工艺员".equals(role)) {
						String user = taskItem.getOwner();
						WTUser name = UserUtil.getWTUserByName(user);
						if(name==null){
							return user;
						}else{
							return name.getFullName();
						}
					}
				}
			}
		} else if (object instanceof WTPart) {
			IBAUtility ibaUtility = new IBAUtility((WTPart) object);
			String value = "";
			if ("PHASE_CODE".endsWith(componentId)) {
				value = ibaUtility.getIBAValue("PHASE_CODE");
			} else if ("KEYCOMPONENT".endsWith(componentId)) {
				value = ibaUtility.getIBAValue("KEYCOMPONENT");
			} else if ("MTYPE".endsWith(componentId)) {
				value = ibaUtility.getIBAValue("MTYPE");
			} else if ("ENDITEMIN".endsWith(componentId)) {
				value = ibaUtility.getIBAValue("ENDITEMIN");
			} else if ("MINDEX".endsWith(componentId)) {
				value = ibaUtility.getIBAValue("MINDEX");
			} else if ("BATCH".endsWith(componentId)) {
				value = ibaUtility.getIBAValue("BATCH");
			}
			if (value != null) {
				return value;
			} else {
				return "";
			}
		} else if (object instanceof WTDocument) {
			IBAUtility ibaUtility = new IBAUtility((WTDocument) object);
			String value = "";
			if ("PHASE_CODE".endsWith(componentId)) {
				value = ibaUtility.getIBAValue("PHASE_CODE");
			} else if ("KEYCOMPONENT".endsWith(componentId)) {
				value = ibaUtility.getIBAValue("KEYCOMPONENT");
			} else if ("MTYPE".endsWith(componentId)) {
				value = ibaUtility.getIBAValue("MTYPE");
			} else if ("ENDITEMIN".endsWith(componentId)) {
				value = ibaUtility.getIBAValue("ENDITEMIN");
			} else if ("MINDEX".endsWith(componentId)) {
				value = ibaUtility.getIBAValue("MINDEX");
			}
			if (value != null) {
				return value;
			} else {
				return "";
			}
		} else if (object instanceof EPMDocument) {
			IBAUtility ibaUtility = new IBAUtility((EPMDocument) object);
			String value = "";
			if ("PHASE_CODE".endsWith(componentId)) {
				value = ibaUtility.getIBAValue("PHASE_CODE");
			} else if ("KEYCOMPONENT".endsWith(componentId)) {
				value = ibaUtility.getIBAValue("KEYCOMPONENT");
			} else if ("MTYPE".endsWith(componentId)) {
				value = ibaUtility.getIBAValue("MTYPE");
			} else if ("ENDITEMIN".endsWith(componentId)) {
				value = ibaUtility.getIBAValue("ENDITEMIN");
			} else if ("MINDEX".endsWith(componentId)) {
				value = ibaUtility.getIBAValue("MINDEX");
			}
			if (value != null) {
				return value;
			} else {
				return "";
			}
		}
		return "";
	}

	/**
	 * 获取指定车间下的所有工艺员
	 *
	 * @param chejian
	 *            车间
	 * @return List<String> 工艺员集合
	 * @throws WTException
	 */
	private static ArrayList<String> getAllGongYiYuanForCheJian(
			WTContainer wtContainer, String chejian) throws WTException {
		ArrayList<String> list = new ArrayList<String>();
		List<WTUser> allUsers = null;
		if (chejian.equals(Constants.ROLE_XIANGMUBUXINGHAOZHUGUAN)) {
			allUsers = ProcessUtil.getRoleUsersByWTContainer(
					"XIANGMUBUGONGYIYUAN", wtContainer);
		} else {
			allUsers = ProcessUtil.getUsersByChanJianNumber(wtContainer,
					chejian);
		}
		if (allUsers != null && !allUsers.isEmpty()) {
			for (WTUser wtUser : allUsers) {
				String fullName = wtUser.getFullName();
				String name = wtUser.getName();
				name = name + "(" + fullName + ")";
				list.add(name);
			}
		}
		return list;
	}
	private static String getTechnicsState(ProcessTaskItem item) {
		if (item.getTaskItemName().contains("指派")) {
			return "";
		}
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			IBAUtility ibaUtility = new IBAUtility(item);
			String docNum = ibaUtility.getIBAValue("PROCESSDOCNUM");
			if(docNum!=null &&!"".equals(docNum)){
				WTDocument doc = WTDocumentUtil.getLatestDocumentByNumber(docNum);
				if(doc==null) return "";
				String state = String.valueOf(doc.getState()
						.getState().getDisplay(Locale.CHINA));
				return state;
            }else{
            	return "";
            }
			/*Long processTaskId = item.getProcessTaskId();
			ReferenceFactory refefence = new ReferenceFactory();
			String oid;
			oid = refefence.getReferenceString(item);
			//String ItemOid = oid.split("ProcessTaskItem:")[1];
			WTPart part = ProcessUtil.getWtPartByProcessTask(processTaskId);

			QueryResult partqr = ProcessPlanHelper.searchAllIteratedByNumberVersionView(WTPart.class,part.getNumber(),part.getVersionInfo().getIdentifier().getValue(),"Manufacturing");
			while(partqr.hasMoreElements()){
				WTPart p = (WTPart)partqr.nextElement();
				QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(p);
				while (qr.hasMoreElements()) {
					WTDocument doc = (WTDocument) qr.nextElement();
					doc = (WTDocument)VersionControlHelper.service.getLatestIteration(doc, true);
					if(!doc.getCreatorName().equals(item.getOwner())){
						continue;
					}
					IBAHelper iba = new IBAHelper();
					String PPLANTYPE= iba.getIBAStringValue(doc, "PPLANTYPE");
					String ZFFLAG = iba.getIBAStringValue(doc, "ZFFLAG");
					String cycleState = String.valueOf(doc.getState()
							.getState().getDisplay(Locale.CHINA));

					if(("工艺设计任务".equals(item.getTaskType())||"工艺更改任务".equals(item.getTaskType()))
							&&"正式工艺文件".equals(PPLANTYPE)){
						if(item.getIszhuzhi()!=null && item.getIszhuzhi()){
							if("Z".equals(ZFFLAG)){
								return cycleState;
							}
						}else if(item.getIszhuzhi()!=null && !item.getIszhuzhi()){
							if("F".equals(ZFFLAG)){
								return cycleState;
							}
						}
					}else if("临时工艺任务".equals(item.getTaskType())&&"临时工艺文件".equals(PPLANTYPE)){
						return cycleState;
					}
				}
			}*/




		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return "";
	}



}
