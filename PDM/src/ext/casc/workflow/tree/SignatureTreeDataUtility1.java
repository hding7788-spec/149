package ext.casc.workflow.tree;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.IconDelegate;
import wt.fc.IconDelegateFactory;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.httpgw.GatewayServletHelper;
import wt.httpgw.URLFactory;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.IconSelector;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignmentState;
import wt.workflow.work.WorkItem;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.components.rendering.guicomponents.Label;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;

import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.changepackaged.ChangePackagedResultLink;
import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.ixb.ReleaseDataAdvisBackHelper;
import ext.casc.preview.PreviewObject;
import ext.casc.util.AccessUtil;
import ext.casc.util.NmTableGUIComponent;
import ext.casc.workflow.WfUtil;

public class SignatureTreeDataUtility1 extends AbstractDataUtility {

    public Object getDataValue(String columnName, Object obj, ModelContext mc) throws WTException {
        NmCommandBean commandBean = mc.getNmCommandBean();
        HttpServletRequest request = commandBean.getRequest();
        String workItemOid = request.getParameter("oid");
        String oid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
        /*
         * 为了开权限
         */
        // 切换系统管理员
        WTUser currentuser = (WTUser) SessionHelper.manager.getPrincipal();
        SessionHelper.manager.setAdministrator();
        try {
            AccessUtil.setObjectAccess((Persistable) obj, currentuser);
        } catch (WTPropertyVetoException e) {
            e.printStackTrace();
        } finally {
            SessionHelper.manager.setPrincipal(currentuser.getAuthenticationName());
        }

        GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
        guicomponentarrayMain.setValueHidden(false);
        if (columnName.equals("icontype")) {
            WTPart part = null;
            WTDocument doc = null;
            EPMDocument epmdoc = null;
            WTChangeOrder2 co = null;
            MPMProcessPlan processPlan = null;
            ChangePackaged change = null;
            if (obj instanceof WTPart) {
                part = (WTPart) obj;
            } else if (obj instanceof WTDocument) {
                doc = (WTDocument) obj;
                // 添加对EPMDoc的处理
            } else if (obj instanceof EPMDocument) {
                epmdoc = (EPMDocument) obj;
            } else if (obj instanceof WTChangeOrder2) {
                co = (WTChangeOrder2) obj;
            } else if (obj instanceof MPMProcessPlan) {
                processPlan = (MPMProcessPlan) obj;
            } else if (obj instanceof ChangePackaged) {
            	change = (ChangePackaged) obj;
            }
            String value = "";
            if (part != null) {
                String imgUrl = getIcon(part);
                URLFactory uf = new URLFactory();
                HashMap m = new HashMap();
                m.put("oid", oid);
                m.put("action", "ObjProps");
                String urlInfo = GatewayServletHelper.buildAuthenticatedHREF(uf, "wt.enterprise.URLProcessor",
                        "URLTemplateAction", m);
                value = "<img src=\"" + imgUrl + "\">";
            } else if (doc != null) {
                String docImgUrl = getIcon(doc);
                URLFactory uf = new URLFactory();
                HashMap m = new HashMap();
                m.put("oid", oid);
                m.put("action", "ObjProps");
                String urlInfo = GatewayServletHelper.buildAuthenticatedHREF(uf, "wt.enterprise.URLProcessor",
                        "URLTemplateAction", m);
                value = value + "<img src=\"" + docImgUrl + "\">";
            } else if (epmdoc != null) {
                String docImgUrl = getIcon(epmdoc);
                URLFactory uf = new URLFactory();
                HashMap m = new HashMap();
                m.put("oid", oid);
                m.put("action", "ObjProps");
                String urlInfo = GatewayServletHelper.buildAuthenticatedHREF(uf, "wt.enterprise.URLProcessor",
                        "URLTemplateAction", m);
                value = value + "<img src=\"" + docImgUrl + "\">";
            } else if (co != null) {
                String docImgUrl = getIcon(co);
                URLFactory uf = new URLFactory();
                HashMap m = new HashMap();
                m.put("oid", oid);
                m.put("action", "ObjProps");
                String urlInfo = GatewayServletHelper.buildAuthenticatedHREF(uf, "wt.enterprise.URLProcessor",
                        "URLTemplateAction", m);
                value = value + "<img src=\"" + docImgUrl + "\">";
            } else if (processPlan != null) {
                String processPlanImgUrl = getIcon(processPlan);
                URLFactory uf = new URLFactory();
                HashMap m = new HashMap();
                m.put("oid", oid);
                m.put("action", "ObjProps");
                String urlInfo = GatewayServletHelper.buildAuthenticatedHREF(uf, "wt.enterprise.URLProcessor",
                        "URLTemplateAction", m);
                value = value + "<img src=\"" + processPlanImgUrl + "\">";
            } else if (change != null) {
                String changeImgUrl = getIcon(change);
                URLFactory uf = new URLFactory();
                HashMap m = new HashMap();
                m.put("oid", oid);
                m.put("action", "ObjProps");
                String urlInfo = GatewayServletHelper.buildAuthenticatedHREF(uf, "wt.enterprise.URLProcessor",
                        "URLTemplateAction", m);
                value = value + "<img src=\"" + changeImgUrl + "\">";
            }

            // String value = "<img src=\""+imgUrl+"\"><a href=\""+urlInfo+"\">"+partNumber+"</a>";
            NmTableGUIComponent gui = new NmTableGUIComponent(value);
            guicomponentarrayMain.addGUIComponent(gui);
            return guicomponentarrayMain;
        }

        if ((obj instanceof WTDocument) || (obj instanceof EPMDocument) || (obj instanceof MPMProcessPlan) || (obj instanceof ChangePackaged)|| (obj instanceof ChangeRequest)||obj instanceof PreviewObject) {
            if (columnName.equals("sign_result")) {
                StringBuffer sb = new StringBuffer();
                List<String> testList = new ArrayList<String>();
                testList.add("同意");
                testList.add("无需会签");
                testList.add("不同意");
                Versioned version = null;
                String versionId = null;
                ReferenceFactory rf = new ReferenceFactory();
                if (obj instanceof ChangePackaged) {
					versionId = rf.getReference((ChangePackaged)obj).toString();
				}else if(obj instanceof ChangeRequest) {
					versionId = rf.getReference((ChangeRequest)obj).toString();
				}else if(obj instanceof PreviewObject) {
					versionId = rf.getReference((PreviewObject)obj).toString();
				}else{
					QueryResult qr = VersionControlHelper.service.allVersionsFrom((Versioned) obj);
	                if (qr.hasMoreElements()) {
	                    version = (Versioned) qr.nextElement();
	                    versionId = rf.getReference(version).toString();
	                }
				}
                versionId = versionId.replace(">", ":");
                // System.out.println("version is:" + version);//此为小版本
                sb.append("<select name=\"" + versionId + "_select\" " + "id=\"" + oid + "_select\" onChange=\"selectSignResult(this,'"+versionId+"')\">");// getReference取到Version
                String key = workItemOid+"_"+versionId+"_select";
                String oldValue = getOldValue(mc, key);
                if (oldValue==null) {
                    oldValue = "";
                }
                for (int i = 0; i < testList.size(); i++) {
                    String tempStr = testList.get(i);
                    if (tempStr.equals(oldValue)) {
                        sb.append("<option selected=\"selected\" value=\"" + tempStr + "\">");
                    }else {
                        sb.append("<option value=\"" + tempStr + "\">");
                    }
                    sb.append(tempStr);
                    sb.append("</option>");
                }
                sb.append("</select>");
                String value = sb.toString();
                NmTableGUIComponent gui = new NmTableGUIComponent(value);
                guicomponentarrayMain.addGUIComponent(gui);

                return guicomponentarrayMain;
            } else if (columnName.equals("sign_advise")) {
                String key = workItemOid+"_"+oid+"_advise";
                String oldValue = getOldValue(mc, key);
                if (oldValue==null) {
                    oldValue = "";
                }
                String value = "<input value=\""+oldValue+"\" type=\"text\" name=\""+oid+"_advise\" id=\"" + oid + "_advise\"/>";
                NmTableGUIComponent gui = new NmTableGUIComponent(value);
                guicomponentarrayMain.addGUIComponent(gui);
                return guicomponentarrayMain;
            } else if (columnName.equals("updatestate")) {
                // 可能要用到
                NmCommandBean cb = mc.getNmCommandBean();
                Map map = cb.getRequestData().getParameterMap();
                String pboOid = (String) map.get("oid"); // 读取任务页面的流程oid
                ReferenceFactory rf = new ReferenceFactory();
                WTReference reference = rf.getReference(pboOid);
                WorkItem wi = (WorkItem) reference.getObject();
                WfActivity wfAct = (WfActivity) wi.getSource().getObject();
                Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
                String value = null;
                if (pbo instanceof ProcessEnvelope) {
                	QueryResult qr = PersistenceHelper.manager.navigate((Persistable) obj, "theProcessEnvelope",
                            EnvelopeMemberLink.class, false);
                    while (qr.hasMoreElements()) {
                        EnvelopeMemberLink link = (EnvelopeMemberLink) qr.nextElement();
                        Persistable roleA = link.getRoleAObject();
                        if (pbo.equals(roleA)) {
                            value = link.getDescription();
                            break;
                        }
                    }
				}else if(pbo instanceof ChangePackaged){
                    QueryResult qr = PersistenceHelper.manager.navigate((ChangePackaged)pbo, "theRevisionControlled",
                            ChangePackagedResultLink.class, false);
                    while (qr.hasMoreElements()) {
                    	ChangePackagedResultLink link = (ChangePackagedResultLink) qr.nextElement();
                        Persistable roleB = link.getRoleBObject();
                        if (obj.equals(roleB)) {
                            value = link.getDescription();
                            break;
                        }
                    }
				}

                if (value == null) {
                    value = "";
                }
                NmTableGUIComponent gui = new NmTableGUIComponent(value);
                String name = oid+"_updatestate";
                gui.addHiddenField(name, value);
                guicomponentarrayMain.addGUIComponent(gui);
                return guicomponentarrayMain;
            } else if (columnName.equals("implementadvise")) {
                NmCommandBean cb = mc.getNmCommandBean();
                Map map = cb.getRequestData().getParameterMap();
                String pboOid = (String) map.get("oid"); // 读取任务页面的流程oid
                ReferenceFactory rf = new ReferenceFactory();
                WTReference reference = rf.getReference(pboOid);
                WorkItem wi = (WorkItem) reference.getObject();
                WfActivity wfAct = (WfActivity) wi.getSource().getObject();
                Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
                String value = null;
                if (pbo instanceof ProcessEnvelope) {
                    QueryResult qr = PersistenceHelper.manager.navigate((Persistable) obj, "theProcessEnvelope",
                            EnvelopeMemberLink.class, false);
                    while (qr.hasMoreElements()) {
                        EnvelopeMemberLink link = (EnvelopeMemberLink) qr.nextElement();
                        Persistable roleA = link.getRoleAObject();
                        if (pbo.equals(roleA)) {
                            value = link.getImplementadvise();
                            break;
                        }
                    }
				}else if(pbo instanceof ChangePackaged){
					if (obj instanceof ChangePackaged) {
						ChangePackaged change = (ChangePackaged)obj;
						value = change.getImplement();
					}else{
						QueryResult qr = PersistenceHelper.manager.navigate((ChangePackaged)pbo, "theRevisionControlled",
	                            ChangePackagedResultLink.class, false);
	                    while (qr.hasMoreElements()) {
	                    	ChangePackagedResultLink link = (ChangePackagedResultLink) qr.nextElement();
	                        Persistable roleB = link.getRoleBObject();
	                        if (obj.equals(roleB)) {
	                            value = link.getImplementadvise();
	                            break;
	                        }
	                    }
					}
					}

                if (value == null) {
                    value = "";
                }
                NmTableGUIComponent gui = new NmTableGUIComponent(value);
                String name = oid+"_implementadvise";
                gui.addHiddenField(name, value);
                guicomponentarrayMain.addGUIComponent(gui);
                return guicomponentarrayMain;
            }else if ("selectDepartment".equals(columnName)) {
            	ReferenceFactory rf = new ReferenceFactory();
            	WorkItem wi= (WorkItem) rf.getReference(workItemOid).getObject();
                WfAssignmentState state = wi.getStatus();

                String veroid = null;
                Versioned version = null;
                String selected = "";
                QueryResult qr = VersionControlHelper.service.allVersionsFrom((Versioned) obj);
                if (qr.hasMoreElements()) {
                    version = (Versioned) qr.nextElement();
                    veroid = rf.getReference(version).toString();
                }
                veroid = veroid.replaceAll(">", ":");
                String values = "<input type=\"text\"  readonly name=\""
                        + oid
                        + "_selected_department\"  id=\""
                        + veroid
                        + "_selected_department\" value=\""
                        + selected
                        + "\"/><input type=\"button\" value=\"选择\" onclick=\"javascript:setSelDepartment(this,'"+veroid+"');\"  id=\""
                        + veroid + "_selecPer\"/>";
                if("COMPLETED".equals(state.toString())){
                    String number = "";
                    if(obj instanceof WTPart) {
                        number = ((WTPart)obj).getNumber();
                    } else if(obj instanceof MPMProcessPlan) {
                    	number = ((MPMProcessPlan)obj).getNumber();
                    } else if(obj instanceof WTChangeOrder2) {
                    	number = ((WTChangeOrder2)obj).getNumber();
                    }
                    String oldValue = ReleaseDataAdvisBackHelper.getSelDepartValue2(number);
                    values = "<input type=\"text\"  readonly name=\""
                        + oid
                        + "_selected_department\"  id=\""
                        + veroid
                        + "_selected_department\" value=\""
                        + oldValue
                        + "\"/>";
                }
                NmTableGUIComponent gui = new NmTableGUIComponent(values);
                guicomponentarrayMain.addGUIComponent(gui);
                return guicomponentarrayMain;
            }
        } else if (obj instanceof WTChangeOrder2) {
            if (columnName.equals("sign_result")) {
                StringBuffer sb = new StringBuffer();
                List<String> testList = new ArrayList<String>();
                testList.add("同意");
                testList.add("无需会签");
                testList.add("不同意");
                ReferenceFactory rf = new ReferenceFactory();
                // System.out.println("version is:" + version);//此为小版本
                sb.append("<select name=\"" + rf.getReference((WTObject) obj) + "_select\" " + "id=\"" + oid
                        + "_select\">");// getReference取到Version oid
                String key = workItemOid+"_"+rf.getReference((WTObject) obj)+"_select";
                String oldValue = getOldValue(mc, key);
                if (oldValue==null) {
                    oldValue = "";
                }
                for (int i = 0; i < testList.size(); i++) {
                    String tempStr = testList.get(i);
                    if (tempStr.equals(oldValue)) {
                        sb.append("<option selected=\"selected\" value=\"" + tempStr + "\">");
                    }else {
                        sb.append("<option value=\"" + tempStr + "\">");
                    }
                    sb.append(tempStr);
                    sb.append("</option>");
                }
                sb.append("</select>");
                String value = sb.toString();
                NmTableGUIComponent gui = new NmTableGUIComponent(value);
                guicomponentarrayMain.addGUIComponent(gui);

                return guicomponentarrayMain;
            } else if (columnName.equals("sign_advise")) {
                String key = workItemOid+"_"+oid+"_advise";
                String oldValue = getOldValue(mc, key);
                if (oldValue==null) {
                    oldValue = "";
                }
                String value = "<input value=\""+oldValue+"\" type=\"text\" name=\""+oid+"_advise\" id=\"" + oid + "_advise\"/>";
                NmTableGUIComponent gui = new NmTableGUIComponent(value);
                guicomponentarrayMain.addGUIComponent(gui);
                return guicomponentarrayMain;
            }
        } else {
            Label text = new Label("");
            guicomponentarrayMain.addGUIComponent(text);
        }

        return guicomponentarrayMain;
    }

    private String getIcon(WTObject obj) throws WTException {
        String imgURL = null;
        try {
            IconDelegate delegate = IconDelegateFactory.getInstance().getIconDelegate(obj);
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

    private static String getOldValue(ModelContext mc,String key) {
        try {
            NmCommandBean commandBean = mc.getNmCommandBean();
            HttpServletRequest request = commandBean.getRequest();
            String workItemOid = request.getParameter("oid");
            ReferenceFactory rf = new ReferenceFactory();
            WorkItem wi = (WorkItem) rf.getReference(workItemOid).getObject();
            if("COMPLETED".equals(wi.getStatus().toString())){
                return "";
            }
            WfActivity wfAct = (WfActivity) wi.getSource().getObject();
            WfProcess process = wfAct.getParentProcess();
            InputStream is = WfUtil.getAttachByWfProcess(process);
            if (is != null) {
                Properties pro = new Properties();
                pro.load(is);
                is.close();
                if (pro.get(key)==null) {
                    return "";
                }else {
                    return String.valueOf(pro.get(key));
                }
            }

        } catch (WTRuntimeException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
