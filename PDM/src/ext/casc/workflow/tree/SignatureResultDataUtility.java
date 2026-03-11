package ext.casc.workflow.tree;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.wvs.server.util.PublishUtils;
import ext.ases.part.ASESHuiqianSignature;
import ext.casc.constants.Constants;
import ext.casc.part.SignatureHelper;
import ext.casc.util.NmTableGUIComponent;
import ext.casc.util.WCUtil;
import ext.casc.workflow.CmWorkflowHelper;
import ext.sast.center.synch.MQConstants;
import wt.change2.WTChangeOrder2;
import wt.content.ContentHelper;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMMemberLink;
import wt.fc.*;
import wt.httpgw.GatewayServletHelper;
import wt.httpgw.URLFactory;
import wt.inf.container.WTContainer;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.project.Role;
import wt.representation.Representation;
import wt.util.IconSelector;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;
import wt.viewmarkup.ViewMarkUpHelper;
import wt.viewmarkup.Viewable;
import wt.viewmarkup.WTMarkUp;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfProcess;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.*;

public class SignatureResultDataUtility extends AbstractDataUtility {
	public Object getDataValue(String columnName, Object obj, ModelContext mc)
			throws WTException {
		// Map<WTObject,List<ASESHuiqianSignature>> signMap =
		// (Map)mc.getDescriptor().getProperty("signMap");
		String oid = PersistenceHelper.getObjectIdentifier((Persistable) obj)
				.toString();
		Map signMap = (Map) mc.getNmCommandBean().getRequest()
				.getAttribute("signMap");
		String webPort = "";
		String hName = "";
		try {
			WTProperties props = WTProperties.getLocalProperties();
			webPort = props.getProperty("wt.webserver.port");
			hName = props.getProperty("wt.server.hostname");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
		if (columnName.equals("icontype")) {
			WTPart part = null;
			WTDocument doc = null;
			EPMDocument epmdoc = null;
			WTChangeOrder2 co = null;
			MPMProcessPlan processPlan = null;
			if (obj instanceof WTPart) {
				part = (WTPart) obj;
			} else if (obj instanceof WTDocument) {
				doc = (WTDocument) obj;
			} else if (obj instanceof EPMDocument) {
				epmdoc = (EPMDocument) obj;
			} else if (obj instanceof WTChangeOrder2) {
				co = (WTChangeOrder2) obj;
			} else if (obj instanceof MPMProcessPlan) {
				processPlan = (MPMProcessPlan) obj;
			}
			String value = "";
			if (part != null) {
				String partNumber = part.getNumber();
				WTContainer container = part.getContainer();
				String conOid = PersistenceHelper.getObjectIdentifier(
						(Persistable) container).toString();
				oid = oid.replaceAll(":", "%3A");
				conOid = conOid.replaceAll(":", "%3A");
				String url = "http://"
						+ hName
						+ ":"
						+ webPort
						+ "/Windchill/app/#ptc1/tcomp/infoPage?ContainerOid=OR%3A"
						+ conOid + "&oid=VR%3A" + oid + "&u8=1";
				String imgUrl = getIcon(part);
				URLFactory uf = new URLFactory();
				HashMap m = new HashMap();
				m.put("oid", oid);
				m.put("action", "ObjProps");
				String urlInfo = GatewayServletHelper.buildAuthenticatedHREF(
						uf, "wt.enterprise.URLProcessor", "URLTemplateAction",
						m);
				value = "<img src=\"" + imgUrl + "\">";
			} else if (doc != null) {
				String docImgUrl = getIcon(doc);
				String docNumber = doc.getNumber();
				URLFactory uf = new URLFactory();
				HashMap m = new HashMap();
				m.put("oid", oid);
				m.put("action", "ObjProps");
				String urlInfo = GatewayServletHelper.buildAuthenticatedHREF(
						uf, "wt.enterprise.URLProcessor", "URLTemplateAction",
						m);
				value = value + "<img src=\"" + docImgUrl + "\">";
			} else if (epmdoc != null) {
				String docImgUrl = getIcon(epmdoc);
				String docNumber = epmdoc.getNumber();
				URLFactory uf = new URLFactory();
				HashMap m = new HashMap();
				m.put("oid", oid);
				m.put("action", "ObjProps");
				String urlInfo = GatewayServletHelper.buildAuthenticatedHREF(
						uf, "wt.enterprise.URLProcessor", "URLTemplateAction",
						m);
				value = value + "<img src=\"" + docImgUrl + "\">";
			} else if (co != null) {
				String docImgUrl = getIcon(co);
				String docNumber = co.getNumber();
				URLFactory uf = new URLFactory();
				HashMap m = new HashMap();
				m.put("oid", oid);
				m.put("action", "ObjProps");
				String urlInfo = GatewayServletHelper.buildAuthenticatedHREF(
						uf, "wt.enterprise.URLProcessor", "URLTemplateAction",
						m);
				value = value + "<img src=\"" + docImgUrl + "\">";
			} else if (processPlan != null) {
				String processPlanImgUrl = getIcon(processPlan);
				String processPlanNumber = processPlan.getNumber();
				URLFactory uf = new URLFactory();
				HashMap m = new HashMap();
				m.put("oid", oid);
				m.put("action", "ObjProps");
				String urlInfo = GatewayServletHelper.buildAuthenticatedHREF(
						uf, "wt.enterprise.URLProcessor", "URLTemplateAction",
						m);
				value = value + "<img src=\"" + processPlanImgUrl + "\">";
			}
			NmTableGUIComponent gui = new NmTableGUIComponent(value);
			guicomponentarrayMain.addGUIComponent(gui);
			return guicomponentarrayMain;
		}

		if (SignatureHelper.isShowSignature(obj)) {
			String value = "";
			if (columnName.equals("neibu_sign_result")) {
				try {
					value = getSignValue(obj, signMap, "内部会签");
				} catch (WTPropertyVetoException e) {
					e.printStackTrace();
				}
			} else if (columnName.equals("waibu_sign_result")) {
				try {
					value = getSignValue(obj, signMap, "外部会签");
				} catch (WTPropertyVetoException e) {
					e.printStackTrace();
				}
			} else if (columnName.equals("biaoshen_sign_result")) {
				try {
					value = getSignValue(obj, signMap, "标审");
				} catch (WTPropertyVetoException e) {
					e.printStackTrace();
				}
			}
			// else if(columnName.equals("gongyiyuan_sign_result")){
			// try {
			// value = getSignValue(obj,signMap,"工艺会签");
			// } catch (WTPropertyVetoException e) {
			// e.printStackTrace();
			// }
			// }
			else if (columnName.equals("gongyiyuan_sign_result")) {
				String value2 = "";
				try {
					value2 = getSignValue(obj, signMap,
							Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG);
					if ("".equals(value2)) {
						value2 = getSignValue(obj, signMap,
								Constants.ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN);
					}
				} catch (WTPropertyVetoException e) {
					e.printStackTrace();
				}
				String tempValue1 = "";
				try {
					tempValue1 = getSignValue(obj, signMap,
							Constants.ACTIVITYNAME_GYHQ);
				} catch (WTPropertyVetoException e) {
					e.printStackTrace();
				}
				String tempValue2 = "";
				try {
					tempValue2 = getSignValue(obj, signMap,
							Constants.TASK_GONGYYUSHEN);
				} catch (WTPropertyVetoException e) {
					e.printStackTrace();
				}
				if (!value2.equals("")) {
					value = value2;
				}
				if (!tempValue1.equals("")) {
					if ("".equals(value)) {
						value = tempValue1;
					} else {
						value = value + ";;" + tempValue1;
					}

				}
				if (!tempValue2.equals("")) {
					if ("".equals(value)) {
						value = tempValue2;
					} else {
						value = value + ";;" + tempValue2;
					}

				}
			} else if (columnName.equals("wuzi_sign_result")) {
				String tempValue1 = "";
				try {
					tempValue1 = getSignValue(obj, signMap,
							Constants.ACTIVITYNAME_WZHQ);
				} catch (WTPropertyVetoException e) {
					e.printStackTrace();
				}
				value = tempValue1;

			} else if (columnName.equals("allhuiqian_sign_result")) {
				String workItemOid = (String) mc.getNmCommandBean()
						.getRequest().getAttribute("workItemOid");
				value = "<a  href='javascript:void(0)' onclick=\"javascript:showAllReviewRecord('"
						+ workItemOid + "','OR:" + oid + "');\">查看所有记录</a>";
			}

			NmTableGUIComponent text = new NmTableGUIComponent(value);
			guicomponentarrayMain.addGUIComponent(text);
		}
		if (obj instanceof EPMDocument) {
			if (columnName.equals("count")) {
				EPMDocument epmDocument = (EPMDocument) obj;
				EPMMemberLink link = WCUtil
						.getEpmMemberLinkByChild(epmDocument);
				if (link != null) {
					return link.getQuantity().getAmount();
				}
			} else if (columnName.equals("keshihuapizhu_result")) {
				EPMDocument epm = (EPMDocument) obj;
				QueryResult qResult = VersionControlHelper.service
						.allVersionsOf(epm.getMaster());
				EPMDocument epmDocument = (EPMDocument) qResult
						.getObjectVector().lastElement();
				QueryResult qr = CmWorkflowHelper
						.getEPMBuildLinksRoles(epmDocument);
				while (qr.hasMoreElements()) {
					EPMBuildRule link = (EPMBuildRule) qr.nextElement();
					WTPart part = (WTPart) link.getRoleBObject();
					QueryResult qResult2 = VersionControlHelper.service
							.allVersionsOf(part.getMaster());
					if (qResult2.hasMoreElements()) {
						part = (WTPart) qResult2.nextElement();
						String value = getWTMarkStringLinks(part);
						NmTableGUIComponent gui = new NmTableGUIComponent(value);
						guicomponentarrayMain.addGUIComponent(gui);
						break;
					}
				}
			}
		}

		return guicomponentarrayMain;
	}

	private String getWTMarkStringLinks(WTPart wtPart) {
		StringBuffer stringbuffer = new StringBuffer(300);
		 String fileName = "";
		byte[] buf = new byte[2048];
		try {
			WTProperties props = WTProperties.getLocalProperties();
			String wthomepath = props.getProperty("wt.home");
			QueryResult qrRep = PublishUtils.getRepresentations(wtPart);

			while (qrRep.hasMoreElements()) {
				Object object = qrRep.nextElement();
				Representation representation = (Representation) object;

				representation = (Representation) ContentHelper.service
						.getContents(representation);
				QueryResult qResult = ViewMarkUpHelper.service
						.getMarkUps((Viewable) representation);
				while (qResult.hasMoreElements()) {
					WTMarkUp wtmarkup = (WTMarkUp) qResult.nextElement();
					stringbuffer.append(
							 wtmarkup.getName()).append(";");
					/*String s3 = wtmarkup.getAdditionalInfo();
					ETB etb = new ETB(s3);
					String s4 = etb.getWcFile();
					String s5 = s4;
					int j = s4.indexOf("!>");
					if (j >= 0 && j < s4.length() - 3) {
						s5 = s4.substring(j + 2);
						s3 = Util.SandR(s3, s4, s5);
					}
					String s6 = etb.getTargetWcFile();
					j = s6.indexOf("!>");
					if (j >= 0 && j < s6.length() - 3) {
						s3 = Util.SandR(s3, s6, s6.substring(j + 2));
					}
					stringbuffer.append(s3).append("\n");

					wtmarkup = (WTMarkUp) ContentHelper.service
							.getContents(wtmarkup);
					Vector<ApplicationData> vector2 = ContentHelper
							.getContentListAll(wtmarkup);
					for (ApplicationData applicationData : vector2) {
						String tempFileName;
						if (applicationData.getRole() == ContentRoleType.THUMBNAIL) {
							tempFileName = FileUtil.setExtension(s5, "gif");
						} else {
							tempFileName = s5;
						}
						String path = (new StringBuilder(
								String.valueOf(wthomepath)))
								.append(File.separator)
								.append("codebase")
								.append(File.separator)
								.append("netmarkets")
								.append(File.separator)
								.append("jsp")
								.append(File.separator)
								.append("flv")
								.append(File.separator)
								.append(new String(tempFileName.getBytes(),
										Charset.forName("GB2312"))).toString();

						InputStream is = ContentServerHelper.service
								.findContentStream(applicationData);
						FileOutputStream fos = new FileOutputStream(new File(
								path));
						int k = 0;
						while ((k = is.read(buf, 0, buf.length)) >= 0) {
							fos.write(buf, 0, k);
						}
						fos.close();*/

						// ContentServerHelper.service.writeContentStream(applicationData,
						// path);
					//}
				}
			}
			/*if (fileName.contains(".")) {
                String etbName = fileName.substring(0, fileName.indexOf(".")) + ".etb";
                String etbpath = (new StringBuilder(String.valueOf(wthomepath))).append(File.separator)
                        .append("codebase").append(File.separator).append("netmarkets").append(File.separator)
                        .append("jsp").append(File.separator).append("flv").append(File.separator)
                        .append(new String(etbName.getBytes(), Charset.forName("GB2312")))
                        .toString();
                FileOutputStream fos = new FileOutputStream(new File(etbpath));
                fos.write(stringbuffer.toString().getBytes(Charset.forName("UTF-8")));
                fos.flush();
                fos.close();
            }*/
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String oid = PersistenceHelper.getObjectIdentifier((Persistable) wtPart).toString();
		String value = "<a href=\"netmarkets/jsp/ext/workflow/showWTMarks.jsp?oid="+oid+"\" target=\"_blank\">"+stringbuffer+"</a>";
		return value;
	}

	private String getIcon(WTObject obj) throws WTException {
		String imgURL = null;
		try {
			IconDelegate delegate = IconDelegateFactory.getInstance()
					.getIconDelegate(obj);
			IconSelector selector = delegate.getStandardIconSelector();
			while (!selector.isResourceKey()) {
				delegate = delegate.resolveSelector(selector);
				selector = delegate.getStandardIconSelector();
			}
			imgURL = selector.getIconKey();
		} catch (Exception e) {
			throw new WTException(e);
		}
		return imgURL;
	}

	private String getSignValue(Object obj,
			Map<WTObject, List<ASESHuiqianSignature>> signMap,
			String activityName) throws WTException, WTPropertyVetoException {
		String value = "";
		ReferenceFactory rf = new ReferenceFactory();
		if (SignatureHelper.isShowSignature(obj)) {
			List<ASESHuiqianSignature> tempSignList = signMap.get(obj);
			String tempValue = "";
			Hashtable ht = new Hashtable();
			for (int i = 0; i < tempSignList.size(); i++) {
				ASESHuiqianSignature tempSign = tempSignList.get(i);
				String tempActOid = tempSign.getActivity();
				WfActivity wfAct = (WfActivity) rf.getReference(tempActOid)
						.getObject();
				if (wfAct.getName().equalsIgnoreCase(activityName)||(activityName.equalsIgnoreCase(Constants.ACTIVITYNAME_GYHQ)&&wfAct.getName().equalsIgnoreCase(MQConstants.ACTIVITY_NAME_KRS))) {
					String conclution = tempSign.getConclusion();
					if (conclution == null) {
						continue;
					}
					String userName = "";
					if (conclution.contains("同意")
							&& !conclution.contains("不同意")) {
						userName = conclution.substring(0,
								conclution.length() - 2);
					} else if (conclution.contains("不同意")) {
						userName = conclution.substring(0,
								conclution.length() - 3);
					} else {// 无需会签
						userName = conclution.substring(0,
								conclution.length() - 4);
					}
					ASESHuiqianSignature tempSign1 = (ASESHuiqianSignature) ht
							.get(userName);
					if (tempSign1 != null) {
						if (tempSign.getCreateTimestamp().after(
								tempSign1.getCreateTimestamp())) {
							ht.put(userName, tempSign);
						}
					} else {
						ht.put(userName, tempSign);
					}
				}
			}
			if (ht.size() > 0) {
				Enumeration enum1 = ht.keys();
				while (enum1.hasMoreElements()) {
					Object obj1 = enum1.nextElement();
					ASESHuiqianSignature tempSign = (ASESHuiqianSignature) ht
							.get(obj1);

					String tempActOid = tempSign.getActivity();
					WfActivity wfAct = (WfActivity) rf.getReference(tempActOid)
							.getObject();
					String name = wfAct.getName();
					String realConclusion = tempSign.getConclusion();

					String conclusion = "";
					if (name.equalsIgnoreCase("内部会签")
							|| name.equalsIgnoreCase("外部会签")
							|| name.equalsIgnoreCase("内部工艺会签")
							|| name.equalsIgnoreCase("用户会签")
							|| name.equalsIgnoreCase(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG)
							|| name.equalsIgnoreCase(Constants.ACTIVITYNAME_ZPGYHQ)
							|| name.equalsIgnoreCase("工艺会签")
							|| name.equalsIgnoreCase(Constants.TASK_GONGYYUSHEN)
							|| name.equalsIgnoreCase(MQConstants.ACTIVITY_NAME_KRS)
							|| name.equalsIgnoreCase("标审")
							|| name.equalsIgnoreCase(Constants.ACTIVITYNAME_WZHQ)
							|| name.equalsIgnoreCase(Constants.ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN)) {
						conclusion = realConclusion;

					} else {
						conclusion = realConclusion.substring(
								realConclusion.indexOf(" ") + 1,
								realConclusion.length());
					}
					conclusion = conclusion.replace(", ", "");
					conclusion = conclusion.replace(",", "");
					String sig = tempSign.getSignature();
					if (sig == null) {
						sig = "";
					}
					String opinion = tempSign.getOpinion();
					if (opinion == null) {
						opinion = "";
					}
					if (tempValue.equals("")) {
						if (sig == null || "".equals(sig)) {
							tempValue = conclusion + ";" + opinion;
						} else {
							if (activityName.equals("外部会签")
									|| activityName.equals("外部工艺会签")) {
								tempValue = conclusion + ";" + sig + ";"
										+ opinion;
							} else {
								tempValue = conclusion + ";" + opinion;
							}
						}
					} else {
						if (sig == null || "".equals(sig)) {
							tempValue = tempValue + "/" + conclusion + ";"
									+ opinion;
						} else {
							if (activityName.equals("外部会签")
									|| activityName.equals("外部工艺会签")) {
								tempValue = tempValue + "/" + conclusion + ";"
										+ sig + ";" + opinion;
							} else {
								tempValue = tempValue + "/" + conclusion + ";"
										+ opinion;
							}
						}
					}
					value = tempValue;
				}
			}

		}
		return value;
	}

	public static Vector getActivityUser(WfActivity wfactivity)
			throws WTException, wt.util.WTPropertyVetoException {
		Vector vector = new Vector();
		Enumeration enumerationRoles = ((wt.workflow.definer.WfAssignedActivityTemplate) wfactivity
				.getTemplateReference().getObject()).getRoles();

		String roleName = new String();
		while (enumerationRoles.hasMoreElements()) {
			roleName = ((wt.project.Role) enumerationRoles.nextElement())
					.toString();
		}
		Role ControlRole = Role.toRole(roleName);
		WfProcess parentProcess = new WfProcess();
		for (Enumeration enum1 = parentProcess.getPrincipals(ControlRole); enum1
				.hasMoreElements();) {
			WTPrincipalReference pRef1 = (WTPrincipalReference) enum1
					.nextElement();
			WTPrincipal p1 = pRef1.getPrincipal();
			if (p1 instanceof WTUser) {
				vector.add(p1);
			} else if (p1 instanceof WTGroup) {
				WTGroup group = (WTGroup) p1;
				for (Enumeration enum2 = group.members(); enum2
						.hasMoreElements();) {
					WTPrincipal p = (WTPrincipal) enum2.nextElement();
					vector.add(p);
					;
				}
				;
			}
		}
		return vector;
	}

	/*
	 * private String getSignValue(Object
	 * obj,Map<WTObject,List<ASESHuiqianSignature>> signMap , String
	 * activityName ,String dataName)throws WTException{ String value = null;
	 * ReferenceFactory rf = new ReferenceFactory(); if(!(obj instanceof
	 * WTPart)){ List<ASESHuiqianSignature> tempSignList = signMap.get(obj);
	 * String tempValue = "";
	 *
	 * //for(int i = 0 ; i < tempSignList.size() ; i++){ //只取最新的一次
	 * ASESHuiqianSignature tempSign = tempSignList.get(tempSignList.size()-1);
	 * String tempActOid = tempSign.getActivity(); WfActivity wfAct =
	 * (WfActivity)rf.getReference(tempActOid).getObject();
	 * if(wfAct.getName().equals(activityName)){ if(tempValue.equals("")){
	 * if(dataName.equals("result")){ String conclusion =
	 * tempSign.getConclusion(); if(conclusion == null){ conclusion = ""; }
	 * tempValue = tempValue+conclusion; }else if(dataName.equals("message")){
	 * String signature = tempSign.getSignature(); if(signature == null){
	 * signature = ""; } tempValue = tempValue+signature; }else
	 * if(dataName.equals("advise")){ String opinion = tempSign.getOpinion();
	 * if(opinion == null){ opinion = ""; } tempValue = tempValue+opinion; }
	 * }else{ if(dataName.equals("result")){ String conclusion =
	 * tempSign.getConclusion(); if(conclusion == null){ conclusion = ""; }
	 * tempValue = tempValue+";"+conclusion; }else
	 * if(dataName.equals("message")){ String signature =
	 * tempSign.getSignature(); if(signature == null){ signature = ""; }
	 * tempValue = tempValue+";"+signature; }else if(dataName.equals("advise")){
	 * String opinion = tempSign.getOpinion(); if(opinion == null){ opinion =
	 * ""; } tempValue = tempValue+";"+opinion; } } } //} value = tempValue;
	 * }else{ value = ""; } return value; }
	 */
}
