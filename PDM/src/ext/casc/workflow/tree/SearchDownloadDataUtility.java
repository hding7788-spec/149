package ext.casc.workflow.tree;

import com.glaway.mpm.processplan.helper.ProcessPlanHelper;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.UserUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import ext.casc.doc.CSCDoc;
import ext.casc.process.ProcessConstants;
import ext.casc.process.ProcessPlan;
import ext.casc.process.ProcessTask;
import ext.casc.process.ProcessTaskItem;
import ext.casc.process.util.ProcessUtil;
import ext.casc.util.IBAUtility;
import ext.casc.util.NmTableGUIComponent;
import ext.csc.utilities.principal.CSCPrincipal;
import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.doc.WTDocument;
import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.collections.WTCollection;
import wt.httpgw.URLFactory;
import wt.inf.container.WTContainer;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;
import wt.workflow.engine.WfProcess;

import java.beans.PropertyVetoException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SearchDownloadDataUtility extends AbstractDataUtility {

	@Override
	public Object getDataValue(String columnName, Object obj, ModelContext mc) throws WTException {
		String number;
		WfProcess wf = null;
		ProcessTask pTask = null;
		WTPart gywdhzPart = null;
		WTDocument wi1 = null;
		if (obj instanceof WfProcess) {
			wf = (WfProcess) obj;
		}
		if (obj instanceof WTDocument) {
			wi1 = (WTDocument) obj;
			QueryResult wtParts = WTPartHelper.service.getDescribesWTParts(wi1);
			if (wtParts.hasMoreElements()) {
				gywdhzPart = (WTPart) wtParts.nextElement();
			}
		} else if (obj instanceof ProcessTaskItem) {
			ProcessTaskItem taskItem = (ProcessTaskItem) obj;
			if ("technicsState".equals(columnName)) {
				String state = getTechnicsState(taskItem);
				System.out.println("cmcmcmcmcm:::" + state + "cm" + state.length());
				if (!"".equals(state.trim()) && !"null".equals(state.trim()) && state.trim() != null) {
					return state;
				} else {
					return "";
				}
			}else if ("owner".equals(columnName)) {
				String owner = taskItem.getOwner();
				if (owner.equals("") || owner.equals("0")) {
					return "";
				}

				WTUser user = UserUtil.getWTUserByName(owner);
				if (user!=null) {
					return user.getFullName();
				}else {
					return taskItem.getOwner();
				}
			}

		}
		if ("dept".equals(columnName)) {
			WTDocument doc = (WTDocument) obj;
			return CSCDoc.getDeptByProcessDoc(doc);
		} else if ("PDFDoc".equals(columnName)) {
			WTDocument doc = (WTDocument) obj;
			String docOid = Long.toString(doc.getPersistInfo().getObjectIdentifier().getId());
			try {
				doc = (WTDocument) ContentHelper.service.getContents(doc);
			} catch (PropertyVetoException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			URLFactory urlfactory = new URLFactory();
			String baseHREF = urlfactory.getBaseHREF();
			Vector apps = ContentHelper.getApplicationData(doc);
			for (Enumeration e = apps.elements(); e.hasMoreElements();) {
				ApplicationData contentItem = (ApplicationData) e.nextElement();
				if (contentItem.getFileName().startsWith("Print_") && contentItem.getFileName().endsWith(".pdf")) {
					ReferenceFactory refefence1 = new ReferenceFactory();
					String appOid = refefence1.getReferenceString(contentItem);
					appOid = appOid.split("OR:wt.content.ApplicationData:")[1];
					GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
					guicomponentarrayMain.setValueHidden(false);
					String values = "<a herf=\"javascript:void(0)\" " + "onclick=\"window.open('" + baseHREF + "servlet/AttachmentsDownloadDirectionServlet?oid=OR:wt.doc.WTDocument:" + docOid
							+ "&cioids=wt.content.ApplicationData:" + appOid + "&role=SECONDARY')\">" + "<font " + "color=\"blue\">" + contentItem.getFileName() + "</font></a>";
					NmTableGUIComponent gui = new NmTableGUIComponent(values);
					guicomponentarrayMain.addGUIComponent(gui);
					return guicomponentarrayMain;
				}
			}

		} else if ("PartNumber".equals(columnName)) {
			if (gywdhzPart != null && !"".equals(gywdhzPart)) {
				return gywdhzPart.getNumber();
			} else {
				return "";
			}
		} else if ("PINDEX".equals(columnName)) {
			// cpdh=ext.casc.util.IBAHelper.getIBAStringValue(part1,"PINDEX");
			// xhdh=ext.casc.util.IBAHelper.getIBAStringValue(part1,"MINDEX");
			// jdbj=ext.casc.util.IBAHelper.getIBAStringValue(part1,"PHASE_CODE");
			if (gywdhzPart != null && !"".equals(gywdhzPart)) {
				return ext.casc.util.IBAHelper.getIBAStringValue(gywdhzPart, "PINDEX");
			} else {
				return "";
			}
		} else if ("MINDEX".equals(columnName)) {
			if (gywdhzPart != null && !"".equals(gywdhzPart)) {
				return ext.casc.util.IBAHelper.getIBAStringValue(gywdhzPart, "MINDEX");
			} else {
				return "";
			}
		} else if ("PHASE_CODE".equals(columnName)) {
			if (gywdhzPart != null && !"".equals(gywdhzPart)) {
				return ext.casc.util.IBAHelper.getIBAStringValue(gywdhzPart, "PHASE_CODE");
			} else {
				return "";
			}
		} else if ("changeDoc".equals(columnName)) {
			WTDocument wi = (WTDocument) obj;
			number = wi.getNumber();
			QuerySpec qSpec;
			Long changeOid;
			String appOid;
			URLFactory urlfactory = new URLFactory();
			String baseHREF = urlfactory.getBaseHREF();
			try {
				qSpec = new QuerySpec(MPMProcessPlan.class);
				int[] index = { 0 };
				SearchCondition sCondition = new SearchCondition(MPMProcessPlan.class, MPMProcessPlan.NUMBER, SearchCondition.EQUAL, number);
				qSpec.appendWhere(sCondition, index);
				QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
				LatestConfigSpec lcs = new LatestConfigSpec();
				qResult = lcs.process(qResult);
				if (qResult.hasMoreElements()) {
					MPMProcessPlan plan = (MPMProcessPlan) qResult.nextElement();
					QueryResult qr = VersionControlHelper.service.allIterationsOf(plan.getMaster());
					while (qr.hasMoreElements()) {
						MPMProcessPlan p = (MPMProcessPlan) qr.nextElement();
						WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(p);
						Iterator it = coll.iterator();
						if (it.hasNext()) {
							WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it.next()).getObject();
							changeOid = ecn.getPersistInfo().getObjectIdentifier().getId();
							try {
								ecn = (WTChangeOrder2) ContentHelper.service.getContents(ecn);
							} catch (PropertyVetoException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							Vector apps = ContentHelper.getApplicationData(ecn);
							if (apps.size() == 0) {
								return "";
							}
							for (Enumeration e = apps.elements(); e.hasMoreElements();) {
								ApplicationData contentItem = (ApplicationData) e.nextElement();
								while (contentItem.getFileName().startsWith("Print_") && !contentItem.getFileName().contains(plan.getNumber())) {
									appOid = Long.toString(contentItem.getPersistInfo().getObjectIdentifier().getId());

									GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
									guicomponentarrayMain.setValueHidden(false);
									String values = "<a herf=\"javascript:void(0)\" " + "onclick=\"window.open('" + baseHREF
											+ "servlet/AttachmentsDownloadDirectionServlet?oid=OR:wt.change2.WTChangeOrder2:" + changeOid + "&cioids=wt.content.ApplicationData:" + appOid
											+ "&role=SECONDARY')\">" + "<font " + "color=\"blue\">" + contentItem.getFileName() + "</font></a>";
									NmTableGUIComponent gui = new NmTableGUIComponent(values);
									guicomponentarrayMain.addGUIComponent(gui);
									return guicomponentarrayMain;
								}
							}
						} else {
							return "";
						}

					}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if ("plannumber".equals(columnName)) {
			if (obj instanceof ProcessPlan) {
				ProcessPlan plan = (ProcessPlan) obj;
				long id = PersistenceHelper.getObjectIdentifier(plan).getId();
				GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
				guicomponentarrayMain.setValueHidden(false);
				// String values = "<a herf=\"javascript:void(0)\" "
				// +
				// "onclick=\"window.open('http://pdm.149.sast.casc/Windchill/app/#netmarkets/jsp/ext/casc/process/otherChildDesingTaskManage.jsp?ProcessPlanId="
				// + id + "')\">" + "<font "
				// + "color=\"blue\">" + plan.getNumber() + "</font></a>";
				URLFactory urlfactory = new URLFactory();
				String baseHREF = urlfactory.getBaseHREF();

				String values = "<a herf=\"javascript:void(0)\" " + "onclick=\"window.open('" + baseHREF
						+ "app/#netmarkets/jsp/ext/casc/process/otherChildDesingTaskManage.jsp?oid=OR%3Aext.casc.process.ProcessPlan%3A" + id + "')\">" + "<font " + "color=\"blue\">"
						+ plan.getNumber() + "</font></a>";

				NmTableGUIComponent gui = new NmTableGUIComponent(values);
				guicomponentarrayMain.addGUIComponent(gui);
				return guicomponentarrayMain;
			}
		} else if ("renwujindu".equals(columnName)) {
			if (obj instanceof ProcessTask) {
				ProcessTask task = (ProcessTask) obj;
				String state = task.getTaskState();
				if (null == state || "null".equals(state)) {
					state = "";
				}
				return state;
			}

		} else if ("wanchengshijian".equals(columnName)) {
			if (obj instanceof ProcessTask) {
				ProcessTask task = (ProcessTask) obj;
				if (null != task.getTaskState() && ProcessConstants.TASK_STATE_FEIGONGYIWANCHENG.equals(task.getTaskState())) {
					return task.getWanchengDate();
				}
			}
		} else if ("xinghao".equals(columnName)) {
			if (obj instanceof ProcessPlan) {
				ProcessPlan plan = (ProcessPlan) obj;
				WTContainer contain = plan.getContainer();
				if (contain != null) {
					return contain.getName();
				}
				return "";
			}

		} else if ("taskname".equals(columnName)) {
			ProcessTaskItem item = null;
			if (obj instanceof ProcessTask) {
				ProcessTask task = (ProcessTask) obj;

				long id = PersistenceHelper.getObjectIdentifier(task).getId();
				QueryResult qr = ProcessUtil.getAllProcessTaskItemByPTask(id);
				if (qr != null && qr.size() > 0) {
					while (qr.hasMoreElements()) {
						item = (ProcessTaskItem) qr.nextElement();
						long taskitemid = PersistenceHelper.getObjectIdentifier(item).getId();
						GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
						guicomponentarrayMain.setValueHidden(false);
						URLFactory urlfactory = new URLFactory();
						String baseHREF = urlfactory.getBaseHREF();

						String values = "<a herf=\"javascript:void(0)\" " + "onclick=\"window.open('" + baseHREF + "app/#ptc1/tcomp/infoPage?oid=OR%3Aext.casc.process.ProcessTaskItem%3A" + taskitemid
								+ "')\">" + "<font " + "color=\"blue\">" + task.getName() + "</font></a>";
						NmTableGUIComponent gui = new NmTableGUIComponent(values);
						guicomponentarrayMain.addGUIComponent(gui);
						return guicomponentarrayMain;
					}
				} else {
					return task.getName();
				}
			}
		} else if ("index".equals(columnName)) {

			System.out.println("----->>>" + mc.getCurrentRow() + 1);
			return mc.getCurrentRow() + 1;
		} else if ("gyrwName".equals(columnName)) {
			if (obj instanceof ProcessTaskItem) {
				ProcessTaskItem taskItem = (ProcessTaskItem) obj;
				String taskNumber = taskItem.getNumber();
				if (taskNumber != null && !"".equals(taskNumber)) {
					QuerySpec qs = new QuerySpec(WTPart.class);
					SearchCondition sc = new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, taskNumber, false);
					qs.appendSearchCondition(sc);
					QueryResult qr = PersistenceHelper.manager.find(qs);
					if (qr.hasMoreElements()) {
						WTPart part = (WTPart) qr.nextElement();
						return part.getName();
					}
				}
			}

		} else if ("thePersistInfo.modifyStamp".equals(columnName)) {
			if (obj instanceof ProcessTaskItem) {
				ProcessTaskItem taskItem = (ProcessTaskItem) obj;
				String taskItemState = taskItem.getTaskItemState();
				if ("已完成".equals(taskItemState)) {
					if (!"".equals(taskItem.getModifyTimestamp()) && !"null".equals(taskItem.getModifyTimestamp()) && taskItem.getModifyTimestamp() != null) {
						return ProcessUtil.formatTime(taskItem.getModifyTimestamp().getTime());
					} else {
						return "";
					}
				} else {
					return "";
				}
			}
		} else if ("fgyzhixingchejian".equals(columnName)) {
			if (obj instanceof ProcessPlan) {
				ProcessPlan plan = (ProcessPlan) obj;
				long id = plan.getPersistInfo().getObjectIdentifier().getId();
				// String idString = Long.toString(id);
				try {
					QuerySpec qs = new QuerySpec(ProcessTask.class);
					SearchCondition sc = new SearchCondition(ProcessTask.class, ProcessTask.PROCESS_PLAN_ID, SearchCondition.EQUAL, id);
					qs.appendSearchCondition(sc);
					QueryResult qr = PersistenceHelper.manager.find(qs);
					if (qr.hasMoreElements()) {
						pTask = (ProcessTask) qr.nextElement();
						if (pTask != null && !"".equals(pTask)) {
							return pTask.getZhuzhichejian();
						} else {
							return "";
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} else if ("fgyrwzxr".equals(columnName)) {
			if (obj instanceof ProcessPlan) {
				ProcessPlan plan = (ProcessPlan) obj;
				long id = plan.getPersistInfo().getObjectIdentifier().getId();
				// String idString = Long.toString(id);
				try {
					QuerySpec qs = new QuerySpec(ProcessTask.class);
					SearchCondition sc = new SearchCondition(ProcessTask.class, ProcessTask.PROCESS_PLAN_ID, SearchCondition.EQUAL, id);
					qs.appendSearchCondition(sc);
					QueryResult qr = PersistenceHelper.manager.find(qs);
					if (qr.hasMoreElements()) {
						pTask = (ProcessTask) qr.nextElement();
						if (pTask != null && !"".equals(pTask)) {
							Long ids = pTask.getPersistInfo().getObjectIdentifier().getId();
							QuerySpec qs1 = new QuerySpec(ProcessTaskItem.class);
							SearchCondition sc1 = new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.PROCESS_TASK_ID, SearchCondition.EQUAL, ids);
							qs1.appendSearchCondition(sc1);
							QueryResult qr1 = PersistenceHelper.manager.find(qs1);
							if (qr1.hasMoreElements()) {
								ProcessTaskItem taskItem = (ProcessTaskItem) qr1.nextElement();
								return taskItem.getOwner();
							} else {
								return "";
							}
						} else {
							return "";
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} else if ("zrgyssjsj".equals(columnName)) {
			if (obj instanceof ProcessPlan) {
				ProcessPlan plan = (ProcessPlan) obj;
				long id = plan.getPersistInfo().getObjectIdentifier().getId();
				// String idString = Long.toString(id);
				try {
					QuerySpec qs = new QuerySpec(ProcessTask.class);
					SearchCondition sc = new SearchCondition(ProcessTask.class, ProcessTask.PROCESS_PLAN_ID, SearchCondition.EQUAL, id);
					qs.appendSearchCondition(sc);
					QueryResult qr = PersistenceHelper.manager.find(qs);
					if (qr.hasMoreElements()) {
						pTask = (ProcessTask) qr.nextElement();
						if (pTask != null && !"".equals(pTask)) {
							SimpleDateFormat dFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
							String time = dFormat.format(pTask.getEndDate());
							return time;
						} else {
							return "";
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} else if ("cjzzshjhwcsj".equals(columnName)) {
			if (obj instanceof ProcessPlan) {
				ProcessPlan plan = (ProcessPlan) obj;
				long id = plan.getPersistInfo().getObjectIdentifier().getId();
				// String idString = Long.toString(id);
				try {
					QuerySpec qs = new QuerySpec(ProcessTask.class);
					SearchCondition sc = new SearchCondition(ProcessTask.class, ProcessTask.PROCESS_PLAN_ID, SearchCondition.EQUAL, id);
					qs.appendSearchCondition(sc);
					QueryResult qr = PersistenceHelper.manager.find(qs);
					if (qr.hasMoreElements()) {
						pTask = (ProcessTask) qr.nextElement();
						if (pTask != null && !"".equals(pTask)) {
							Long ids = pTask.getPersistInfo().getObjectIdentifier().getId();
							// String ids =
							// Long.toString(pTask.getPersistInfo().getObjectIdentifier().getId());
							QuerySpec qs1 = new QuerySpec(ProcessTaskItem.class);
							SearchCondition sc1 = new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.PROCESS_TASK_ID, SearchCondition.EQUAL, ids);
							qs1.appendSearchCondition(sc1);
							QueryResult qr1 = PersistenceHelper.manager.find(qs1);
							if (qr1.hasMoreElements()) {
								ProcessTaskItem taskItem = (ProcessTaskItem) qr1.nextElement();
								SimpleDateFormat dFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
								String time = dFormat.format(taskItem.getEndDate());
								return time;
							} else {
								return "";

							}
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				return "";
			}
		} else if ("sjwcsh".equals(columnName)) {
			if (obj instanceof ProcessPlan) {
				ProcessPlan plan = (ProcessPlan) obj;
				long id = plan.getPersistInfo().getObjectIdentifier().getId();
				// String idString = Long.toString(id);
				try {
					QuerySpec qs = new QuerySpec(ProcessTask.class);
					SearchCondition sc = new SearchCondition(ProcessTask.class, ProcessTask.PROCESS_PLAN_ID, SearchCondition.EQUAL, id);
					qs.appendSearchCondition(sc);
					QueryResult qr = PersistenceHelper.manager.find(qs);
					if (qr.hasMoreElements()) {
						pTask = (ProcessTask) qr.nextElement();
						if (pTask != null && !"".equals(pTask)) {
							Long ids = pTask.getPersistInfo().getObjectIdentifier().getId();
							// String ids =
							// Long.toString(pTask.getPersistInfo().getObjectIdentifier().getId());
							QuerySpec qs1 = new QuerySpec(ProcessTaskItem.class);
							SearchCondition sc1 = new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.PROCESS_TASK_ID, SearchCondition.EQUAL, ids);
							qs1.appendSearchCondition(sc1);
							QueryResult qr1 = PersistenceHelper.manager.find(qs1);
							if (qr1.hasMoreElements()) {
								ProcessTaskItem taskItem = (ProcessTaskItem) qr1.nextElement();
								SimpleDateFormat dFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
								String time = dFormat.format(taskItem.getModifyTimestamp());
								return time;
							} else {
								return "";

							}
						} else {
							return "";
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} else if ("GYWDHZPartNumber".equals(columnName)) {
			if (gywdhzPart != null) {
				return gywdhzPart.getNumber();
			} else {
				return "";
			}
		} else if ("GYWDHZChangeReason".equals(columnName)) {
			IBAHelper ibaHelper = new IBAHelper(wi1);
			String changeReason = ibaHelper.getIBAValue("CHGREASON");
			if (changeReason != null) {
				return changeReason;
			} else {
				return "";
			}
		} else if ("GYWDHZLimitedTime".equals(columnName)) {
			IBAHelper ibaHelper = new IBAHelper(wi1);
			String changeLimitedTime = ibaHelper.getIBAValue("EFFDATE");
			if (changeLimitedTime != null && !"".equals(changeLimitedTime)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date d = null;
				try {
					d = sdf.parse(changeLimitedTime);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
				changeLimitedTime = sdf2.format(d);
			} else {
				changeLimitedTime = ibaHelper.getIBAValue("EFFDATE2");
				if (changeLimitedTime == null) {
					changeLimitedTime = "";
				}
			}
			return changeLimitedTime;
		} else if ("GYWDHZPartName".equals(columnName)) {
			if (gywdhzPart != null) {
				return gywdhzPart.getName();
			} else {
				return "";
			}
		} else if ("GYWDHZPartVersion".equals(columnName)) {
			if (gywdhzPart != null) {
				return gywdhzPart.getVersionInfo().getIdentifier().getValue() + "." + gywdhzPart.getIterationInfo().getIdentifier().getValue();
			} else {
				return "";
			}
		} else if ("GYWDHZJieduanbiaoji".equals(columnName)) {
			if (gywdhzPart != null) {
				IBAHelper ibaHelper = new IBAHelper(gywdhzPart);
				String phaseCode = ibaHelper.getIBAValue("PHASE_CODE");
				if ("".equals(phaseCode) || "null".equals(phaseCode) || phaseCode == null) {
					return "";
				} else {
					return ibaHelper.getIBAValue("PHASE_CODE");
				}
			} else {
				return "";
			}
		} else if ("GYWDHZPici".equals(columnName)) {
			if (gywdhzPart != null) {
				IBAHelper ibaHelper = new IBAHelper(gywdhzPart);
				String batch = ibaHelper.getIBAValue("BATCH");
				if ("".equals(batch) || "null".equals(batch) || batch == null) {
					return "";
				} else {
					return ibaHelper.getIBAValue("BATCH");
				}
			} else {
				return "";
			}
		} else if ("GYWDHZDayingzhuangtai".equals(columnName)) {
			IBAHelper ibaHelper = new IBAHelper(wi1);
			String flag = ibaHelper.getIBAValue("PrintFlag");
			if ("".equals(flag) || "null".equals(flag) || flag == null) {
				return "";
			} else {
				return ibaHelper.getIBAValue("PrintFlag");
			}
		} else if ("GYWDHZChejian".equals(columnName)) {
			if (gywdhzPart != null) {
				IBAHelper ibaHelper = new IBAHelper(gywdhzPart);
				String zzcj = ibaHelper.getIBAValue("ZZCJ");
				if ("".equals(zzcj) || "null".equals(zzcj) || zzcj == null) {
					return "";
				} else {
					return zzcj;
				}
			} else {
				return "";
			}

		} else if ("gywdhzName".equals(columnName)) {
			if (wi1 != null) {
				String typeName = TypedUtility.getTypeIdentifier(wi1).getTypename();
				if (typeName.contains("PROCESS_PLAN")) {
					String name = wi1.getName();
					if (!"".equals(name) && !"null".equals(name) && name != null) {
						name = name.split("\\(")[0];
						return name;
					}
				} else {
					String name = wi1.getName();
					if (!"".equals(name) && !"null".equals(name) && name != null) {
						return name;
					} else {
						return "";
					}
				}
			} else {
				return "";
			}
		} else if ("gywdhzNumber".equals(columnName)) {
			if (wi1 != null) {
				String typeName = TypedUtility.getTypeIdentifier(wi1).getTypename();
				if (typeName.contains("PROCESS_PLAN")) {
					String name = wi1.getName();
					if (!"".equals(name) && !"null".equals(name) && name != null) {
						String[] nameString = name.split("\\(");
						if (nameString.length > 1) {
							String head = nameString[0];
							String hou = name.substring(head.length() + 1, name.length() - 1);
							return hou;
						}
					}
				} else {
					String number2 = wi1.getNumber();
					if (!"".equals(number2) && !"null".equals(number2) && number2 != null) {
						return number2;
					} else {
						return "";
					}
				}
			} else {
				return "";
			}
		} else if ("GYWDHZYeshu".equals(columnName)) {
			if (wi1 != null) {
				IBAHelper ibaHelper = new IBAHelper(wi1);
				String yeshu = ibaHelper.getIBAValue("PAGE");
				if (!"".equals(yeshu) && !"null".equals(yeshu) && yeshu != null) {
					return yeshu;
				} else {
					return "";
				}
			} else {
				return "";
			}

		} else if ("yuqiFlag".equals(columnName)) {
			if (obj instanceof ProcessTaskItem) {
				ProcessTaskItem pti = (ProcessTaskItem) obj;
				if ("已完成".equals(pti.getTaskItemState())) {
					SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
					Timestamp endDate1 = pti.getEndDate();
					if (!"".equals(endDate1) && !"null".equals(endDate1) && endDate1 != null) {
//						String endTime = format.format(endDate1).trim();
						Timestamp shijianshijian = pti.getModifyTimestamp();
						if (!"".equals(shijianshijian) && !"null".equals(shijianshijian) && shijianshijian != null) {
							String shijianTime = format.format(shijianshijian).trim();
//							Long endTimeLong = Long.valueOf(endTime);
							Long shijianTimeLong = Long.valueOf(shijianTime);
							long endTimeLong =endDate1.getTime()+(long)1000*3600*24;
							if (shijianTimeLong > endTimeLong) {
								return "是";
							} else {
								return "否";
							}
						} else {
							Date date = new Date();
							SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
							String currentDate = format1.format(date);
							Timestamp endDate = pti.getEndDate();
							if (!"".equals(endDate) && !"null".equals(endDate) && endDate != null) {
//								String endTime1 = format.format(endDate).trim();
								Long currentDateLong = Long.valueOf(currentDate);
//								Long endTimeLong = Long.valueOf(endTime1);
								long endTimeLong =endDate.getTime()+(long)1000*3600*24;
								if (currentDateLong > endTimeLong) {
									return "是";
								} else {
									return "";
								}
							} else {
								return "";
							}

						}
					} else {
						return "";
					}
				} else {
					Date date = new Date();
					SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
					String currentDate = format.format(date);
					Timestamp endDate1 = pti.getEndDate();
					if (!"".equals(endDate1) && !"null".equals(endDate1) && endDate1 != null) {
//						String endTime = format.format(endDate1).trim();
						Long currentDateLong = Long.valueOf(currentDate);
//						Long endTimeLong = Long.valueOf(endTime);
						long endTimeLong =endDate1.getTime()+(long)1000*3600*24;
						if (currentDateLong > endTimeLong) {
							return "是";
						} else {
							return "";
						}
					} else {
						return "";
					}
				}

			} else if (obj instanceof ProcessTask) {
				ProcessTask pt = (ProcessTask) obj;
				IBAUtility ibaUtility = new IBAUtility(pt);
				String cldePlanTime = ibaUtility.getIBAValue("cldePlanTime");
				String cldeEndTime = ibaUtility.getIBAValue("cldeEndTime");
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date cldePlanDate = null;
				Date cldeEndDate = null;
				long time = 0;
				long thistime = new Date().getTime();
				if (cldeEndTime == null || "".equals(cldeEndTime)) {
						long id = PersistenceHelper.getObjectIdentifier(pt).getId();
						QueryResult qr = ProcessUtil.getAllProcessTaskItemByPTask(id);
						while (qr.hasMoreElements()) {
							ProcessTaskItem taskItem = (ProcessTaskItem) qr.nextElement();
							IBAUtility utility1 = new IBAUtility(taskItem);
							String tecNumber = utility1.getIBAValue("PROCESSDOCNUM");
							if(tecNumber!=null && !"null".equals(tecNumber)){
								WTDocument document = WTDocumentUtil.getLatestDocumentByNumber(tecNumber);
								String state = document.getState().getState().getDisplay(Locale.CHINA);
								if(state.contains("已批准")){
									time = document.getModifyTimestamp().getTime();
								}
							}
						}
					try {
						cldePlanDate = format.parse(cldePlanTime);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					long plantime = cldePlanDate.getTime()+(long)1000*3600*24;
					if(time!=0){
						if(time > plantime){
							return "";
						}else{
							return "否";
						}
					}else{
						if (thistime > plantime) {
							return "是";
						} else {
							return "";
						}
					}
				}
				if (cldeEndTime != null && !"".equals(cldeEndTime)) {
					try {
						cldePlanDate = format.parse(cldePlanTime);
						if (cldeEndTime.contains("/")) {
							cldeEndDate = format1.parse(cldeEndTime);
						} else if (cldeEndTime.contains("-")) {
							cldeEndDate = format.parse(cldeEndTime);
						}

					} catch (ParseException e) {
						e.printStackTrace();
					}
					long plantime = cldePlanDate.getTime()+(long)1000*3600*24;
					long endtime = cldeEndDate.getTime();
					if (plantime > endtime) {
						return "否";
					} else {
						return "是";
					}

				}

			}

		} else if ("cldexinghao".equals(columnName)) {
			if (obj instanceof ProcessTask) {
				ProcessTask task = (ProcessTask) obj;
				String containerName = task.getContainerName();
				return containerName;
			}
		} else if ("cldeState".equals(columnName)) {
			if (obj instanceof ProcessTask) {
				ProcessTask task = (ProcessTask) obj;
				IBAUtility ibaUtility = new IBAUtility(task);
				String technicsNumber = ibaUtility.getIBAValue("relatedTech");// 关联工艺
				String cldeState = "";
				try {
					if (technicsNumber != null && !"".equals(technicsNumber)) {
						WTDocument doc = WTDocumentUtil.getLatestDocumentByNumber(technicsNumber);
						if (doc != null) {
							IBAHelper ibaHelper = new IBAHelper(doc);
							cldeState = ibaHelper.getIBAValue("CLDEZT");
						}
					}
				} catch (WTException e) {
					e.printStackTrace();
				}
				if (cldeState == null || "null".equals(cldeState) || "".equals(cldeState))
					cldeState = "";
				return cldeState;
			}
		} else if ("cldeJHEndTime".equals(columnName)) {
			if (obj instanceof ProcessTask) {
				ProcessTask task = (ProcessTask) obj;
				IBAUtility utility = new IBAUtility(task);
				String cldePlanTime = utility.getIBAValue("cldePlanTime");
				return cldePlanTime;
			}
		} else if ("cldeSJEndTime".equals(columnName)) {
			if (obj instanceof ProcessTask) {
				ProcessTask task = (ProcessTask) obj;
				IBAUtility utility = new IBAUtility(task);
				String cldeEndTime = utility.getIBAValue("cldeEndTime");// 材料定额实际完成时间
				if (cldeEndTime != null && !"".equals(cldeEndTime)) {
					cldeEndTime = cldeEndTime.replaceAll("/", "-");
				}else {
					long id = PersistenceHelper.getObjectIdentifier(task).getId();
					QueryResult qr = ProcessUtil.getAllProcessTaskItemByPTask(id);
					while (qr.hasMoreElements()) {
						ProcessTaskItem taskItem = (ProcessTaskItem) qr.nextElement();
						IBAUtility utility1 = new IBAUtility(taskItem);
						String tecNumber = utility1.getIBAValue("PROCESSDOCNUM");
						if(tecNumber!=null && !"null".equals(tecNumber)){
							WTDocument document = WTDocumentUtil.getLatestDocumentByNumber(tecNumber);
							String state = document.getState().getState().getDisplay(Locale.CHINA);
							if(state.contains("已批准")){
								SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								ApplicationData applicationData = null;
								try {
									applicationData = WTDocumentUtil.getPrimaryByDocument(document);
									String time = format.format(applicationData.getModifyTimestamp());
									return time;
								} catch (PropertyVetoException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
				return cldeEndTime;
			}
		} else if ("clderenwuName".equals(columnName)) {
			if (obj instanceof ProcessTask) {
				ProcessTask task = (ProcessTask) obj;
				String taskNumber = task.getNumber();
				if (taskNumber != null && !"".equals(taskNumber)) {
					QuerySpec qs = new QuerySpec(WTPart.class);
					SearchCondition sc = new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, taskNumber, false);
					qs.appendSearchCondition(sc);
					QueryResult qr = PersistenceHelper.manager.find(qs);
					if (qr.hasMoreElements()) {
						WTPart part = (WTPart) qr.nextElement();
						return part.getName();
					}
				}
			}

		} else if ("cldeleixing".equals(columnName)) {
			if (obj instanceof ProcessTask) {
				ProcessTask task = (ProcessTask) obj;
				String type = task.getTaskType();
				return type;
			}

		} else if ("faqizhe".equals(columnName)) {
			if (obj instanceof ProcessTask) {
				ProcessTask task = (ProcessTask) obj;
				Long pid = PersistenceHelper.getObjectIdentifier(task).getId();
				QueryResult result = ProcessUtil.getAllProcessTaskItemByPTask(pid);
				if (result.hasMoreElements()) {
					ProcessTaskItem item = (ProcessTaskItem) result.nextElement();
					String zhurengongyishi = item.getZhurengongyishi();
					return zhurengongyishi;
				}
			}

		} else if ("faqizhebumen".equals(columnName)) {
			if (obj instanceof ProcessTask) {
				ProcessTask task = (ProcessTask) obj;
				Long pid = PersistenceHelper.getObjectIdentifier(task).getId();
				QueryResult result = ProcessUtil.getAllProcessTaskItemByPTask(pid);
				if (result.hasMoreElements()) {
					ProcessTaskItem item = (ProcessTaskItem) result.nextElement();
					String zhurengongyishi = item.getZhurengongyishi();
					WTUser user = CSCPrincipal.getUserByName(zhurengongyishi);
					if (user == null || "Administrator".equals(user)) {
						return "";
					}
					Enumeration groups = user.parentGroupNames();
					while (groups.hasMoreElements()) {
						String nextElement = (String) groups.nextElement();
						if (nextElement.startsWith("部门")) {
							nextElement = nextElement.substring(3);
							return nextElement;
						}
					}

				}
			}

		} else if ("bianzhizhe".equals(columnName)) {
			if (obj instanceof ProcessTask) {
				ProcessTask task = (ProcessTask) obj;
				Long pid = PersistenceHelper.getObjectIdentifier(task).getId();
				QueryResult result = ProcessUtil.getAllProcessTaskItemByPTask(pid);
				while (result.hasMoreElements()) {
					ProcessTaskItem item = (ProcessTaskItem) result.nextElement();
					String gongyiyuan = item.getGongyiyuan();
					if (gongyiyuan != null && !"".equals(gongyiyuan)) {
						return gongyiyuan;
					}
				}

			}
		} else if ("ZHUZHICHEJIAN".equals(columnName)) {
			if (obj instanceof ProcessTask) {
				ProcessTask task = (ProcessTask) obj;
				String zhuzhichejian = task.getZhuzhichejian();
				return zhuzhichejian;
			}
		}
		return "";
	}

	private static String getTechnicsState(ProcessTaskItem item) {
		String cycleState = "";
		if (item.getTaskItemName().contains("指派")) {
			return "";
		}
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		Long processTaskId = item.getProcessTaskId();
		ReferenceFactory refefence = new ReferenceFactory();
		try {
			WTPart part = ProcessUtil.getWtPartByProcessTask(processTaskId);
			if ("".equals(part) || "null".equals(part) || part == null) {
				return "";
			} else {

				QueryResult partqr = ProcessPlanHelper.searchAllIteratedByNumberVersionView(WTPart.class, part.getNumber(), part.getVersionInfo().getIdentifier().getValue(), "Manufacturing");
				while (partqr.hasMoreElements()) {
					WTPart p = (WTPart) partqr.nextElement();
					QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(p);
					while (qr.hasMoreElements()) {
						WTDocument doc = (WTDocument) qr.nextElement();
						doc = (WTDocument) VersionControlHelper.service.getLatestIteration(doc, true);
						if (!doc.getCreatorName().equals(item.getOwner())) {
							continue;
						}
						ext.casc.util.IBAHelper iba = new ext.casc.util.IBAHelper();
						String PPLANTYPE = iba.getIBAStringValue(doc, "PPLANTYPE");
						String ZFFLAG = iba.getIBAStringValue(doc, "ZFFLAG");
						cycleState = String.valueOf(doc.getState().getState().getDisplay(Locale.CHINA));

						if (("工艺设计任务".equals(item.getTaskType()) || "工艺更改任务".equals(item.getTaskType())) && "正式工艺文件".equals(PPLANTYPE)) {
							if (item.getIszhuzhi() != null && item.getIszhuzhi()) {
								if ("Z".equals(ZFFLAG)) {
									return cycleState;
								}
							} else if (item.getIszhuzhi() != null && !item.getIszhuzhi()) {
								if ("F".equals(ZFFLAG)) {
									return cycleState;
								}
							}
						} else if ("临时工艺任务".equals(item.getTaskType()) && "临时工艺文件".equals(PPLANTYPE)) {
							return cycleState;
						}
					}
				}

			}

		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return "";
	}

}
