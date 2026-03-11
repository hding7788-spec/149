package ext.casc.process.datautility;

import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.process.ProcessConstants;
import ext.casc.process.ProcessTask;
import ext.casc.process.util.ProcessUtil;
import ext.casc.util.IBAUtility;
import ext.casc.util.NmTableGUIComponent;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pom.PersistenceException;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class NewProAssignTaskDatautility extends AbstractDataUtility {

    @Override
    public Object getDataValue(String componentId, Object object, ModelContext context) throws WTException {
        String oid = PersistenceHelper.getObjectIdentifier((Persistable) object).toString();
        NmCommandBean commandBean = context.getNmCommandBean();
        HttpSession session = commandBean.getRequest().getSession();
        String taskType = (String) session.getAttribute("TaskType");
        ProcessTask processTask = null;
        if (taskType.equals(ProcessConstants.TASK_TYPE_GONGYIGENGGAI) || taskType.equals(ProcessConstants.TASK_TYPE_LINSHIGONGYI)) {
            processTask = ProcessUtil.getProcessTaskByPart((WTPart) object, ProcessConstants.TASK_TYPE_ZZGYRW);
        }
        String zzcjValue = "";
        String fzcjValue = "";
        String state = "";
        WTPart part = null;
        if (object instanceof WTPart) {
            part = (WTPart) object;
            if (!taskType.equals(ProcessConstants.TASK_TYPE_LINSHIGONGYI) && !taskType.equals(ProcessConstants.TASK_TYPE_GONGYIGENGGAI)) {
                List<WTPart> parts = this.getAllVersionWtpart(part);
                for (WTPart wtpart : parts) {
                    WTDocument doc = getZProcessPlan(wtpart);
                    //有正式主工艺
                    if (doc != null) {
                        state = doc.getState().getState().getDisplay();
                        if ("已作废".equals(state)) {
                            if (ProcessUtil.isExistProcessTask(wtpart, taskType)) {
                                NmTableGUIComponent gui = new NmTableGUIComponent("");
                                GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
                                guicomponentarrayMain.addGUIComponent(gui);
                                return guicomponentarrayMain;
                            }
                        } else {
                            NmTableGUIComponent gui = new NmTableGUIComponent("");
                            GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
                            guicomponentarrayMain.addGUIComponent(gui);
                            return guicomponentarrayMain;
                        }
                        //无正式主工艺
                    } else {
                        if (ProcessUtil.isExistProcessTask(wtpart, taskType)) {
                            NmTableGUIComponent gui = new NmTableGUIComponent("");
                            GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
                            guicomponentarrayMain.addGUIComponent(gui);
                            return guicomponentarrayMain;
                        }
                    }

                }
            }
            IBAUtility ibaUtility = new IBAUtility(part);
            zzcjValue = ibaUtility.getIBAValue("ZZCJ");
            if (zzcjValue == null) {
                zzcjValue = "";
            }
            fzcjValue = ibaUtility.getIBAValue("FZCJ");
            if (fzcjValue == null) {
                fzcjValue = "";
            }
        }
        Object reObj = null;
        Versioned version = null;
        String veroid = null;
        ReferenceFactory rf = new ReferenceFactory();
        QueryResult qr = VersionControlHelper.service.allVersionsFrom((Versioned) object);
        if (qr.hasMoreElements()) {
            version = (Versioned) qr.nextElement();
            veroid = rf.getReference(version).toString();
        }
        veroid = veroid.replaceAll(">", ":");

        WTUser user = (WTUser) SessionHelper.getPrincipal();
        String userOid = PersistenceHelper.getObjectIdentifier((Persistable) user).toString();

        String id;
        String name;
        if ("zhuzhichejian".equals(componentId)) {
                StringBuffer sb = new StringBuffer();
                sb.append("<select id=\"" + getColumnAttr(part, null, "custom", "createProcessTask", "zhuzhichejian", "id") + "\" " +
                        "name=\"" + getColumnAttr(part, null, "custom", "createProcessTask", "zhuzhichejian", "name") + "\" onChange=\"verify1(this)\">");
            ArrayList<String> valueList = null;
            try {
                valueList = getValueList();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            for (String optionValue : valueList) {
                    sb.append("<option value=\"" + optionValue + "\">");
                    sb.append(optionValue);
                    sb.append("</option>");
                }
                NmTableGUIComponent gui = new NmTableGUIComponent(sb.toString());
                GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
                guicomponentarrayMain.setValueHidden(false);
                guicomponentarrayMain.addGUIComponent(gui);
                return guicomponentarrayMain;
        } else if ("jihuawanchengshijian".equals(componentId)) {
                StringBuffer sb = new StringBuffer();
                sb.append("<input type=\"text\" id=\"" + getColumnAttr(part,null,"custom","createProcessTask","jihuawanchengshijian","id")+"\" " +
                        "name=\""+getColumnAttr(part,null,"custom","createProcessTask","jihuawanchengshijian","name")+"\" " +
                        "onchange=\"verify3(this)\"" +
                        "onBlur=\"validateDate1(this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)\" " +
                        "onkeypress=\"validateDate1ForEnterKey(event , this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)\" " +
                        "value=\"\"  size=\"10\" maxlength=\"10\"/>\n\t\t\t<A HREF=\"javascript:void(0)\"  " +
                        "onmousedown=\"suppressCalendarBlur(event);\"" +
                        "onClick=\"initCal('\\u65e5\\u5386', '<LINK REL=stylesheet HREF=/Windchill/netmarkets/css/siteStyles.css TYPE=text/css>', " +
                        "'/Windchill/templates/cadx/common/trlUtils.js', 3, 2, 1, '', '', false, 100, 1932); " +
                        "setDateField(document.getElementsByName('" + getColumnAttr(part,null,"custom","createProcessTask","jihuawanchengshijian","name") + "')[0], 'yyyy/MM/dd', '\\u4e00\\u6708#\\u4e8c\\u6708#\\u4e09\\u6708#\\u56db\\u6708#\\u4e94\\u6708#\\u516d\\u6708#\\u4e03\\u6708#\\u516b\\u6708#\\u4e5d\\u6708#\\u5341\\u6708#\\u5341\\u4e00\\u6708#\\u5341\\u4e8c\\u6708#', '#', '\\u661f\\u671f\\u65e5#\\u661f\\u671f\\u4e00#\\u661f\\u671f\\u4e8c#\\u661f\\u671f\\u4e09#\\u661f\\u671f\\u56db#\\u661f\\u671f\\u4e94#\\u661f\\u671f\\u516d#', '0'); " +
                        "newCalendarWindow(event, '/Windchill/netmarkets/jsp/util/calPopup.jsp', 'height=220,width=240')\">\n\t\t\t" +
                        "<IMG name=\"calImg\" SRC=\"/Windchill/netmarkets/images/calendar.gif\" WIDTH=18 HEIGTH=16 BORDER=0></A><font class=hlpTxt>yyyy/mm/dd</font>");
                NmTableGUIComponent gui = new NmTableGUIComponent(sb.toString());
                GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
                guicomponentarrayMain.setValueHidden(false);
                guicomponentarrayMain.addGUIComponent(gui);
                return guicomponentarrayMain;
        } else if ("renwuyaoqiu".equals(componentId)) {
            id = getColumnAttr(part,null,"custom","createProcessTask","renwuyaoqiu","id");
            name = getColumnAttr(part,null,"custom","createProcessTask","renwuyaoqiu","name");

            String value= "<input type=\"text\" name=\""+name+"\" id=\""+id+"\"/>";
            NmTableGUIComponent gui = new NmTableGUIComponent(value);
            GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
            guicomponentarrayMain.setValueHidden(false);
            guicomponentarrayMain.addGUIComponent(gui);
            return guicomponentarrayMain;
        } else if ("renwuyiju".equals(componentId)) {
                StringBuffer sb = new StringBuffer();
                sb.append("<select id=\""+getColumnAttr(part,null,"custom","createProcessTask","renwuyiju","id")+"\" " +
                        "name=\""+getColumnAttr(part,null,"custom","createProcessTask","renwuyiju","name")+"\" onChange=\"verify5(this)\">");
                ArrayList<String> valueList = getRenwuYijuSelectList();
                for (String optionValue : valueList) {
                    sb.append("<option value=\"" + optionValue + "\">");
                    sb.append(optionValue);
                    sb.append("</option>");
                }
                NmTableGUIComponent gui = new NmTableGUIComponent(sb.toString());
                GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
                guicomponentarrayMain.setValueHidden(false);
                guicomponentarrayMain.addGUIComponent(gui);
                return guicomponentarrayMain;
        }

        return reObj;
    }

    private ArrayList<String> getRenwuYijuSelectList() {
        ArrayList<String> resultList = new ArrayList<String>();
        resultList.add("蓝图");
        resultList.add("并行生产");
        resultList.add("返工返修");
        resultList.add("设计更改");
        resultList.add("工艺更改");
        resultList.add("设计通知");
        resultList.add("工艺通知");
        resultList.add("临时生产");
        return resultList;
    }

    private ArrayList<String> getRenwuYijuDisplayList() {
        ArrayList<String> resultList = new ArrayList<String>();
        resultList.add("蓝图");
        resultList.add("并行生产");
        resultList.add("返工返修");
        resultList.add("设计更改");
        resultList.add("工艺更改");
        resultList.add("设计通知");
        resultList.add("工艺通知");
        resultList.add("临时生产");
        return resultList;
    }

    private ArrayList<String> getRenwuYijuValueList() {
        ArrayList<String> resultList = new ArrayList<String>();
        resultList.add("蓝图");
        resultList.add("并行生产");
        resultList.add("返工返修");
        resultList.add("设计更改");
        resultList.add("工艺更改");
        resultList.add("设计通知");
        resultList.add("工艺通知");
        resultList.add("临时生产");
        return resultList;
    }

    private static ArrayList<String> getDisplayList() throws RemoteException {
        return ProcessUtil.getAllCheJian();
    }

    private static ArrayList<String> getValueList() throws RemoteException {
        return ProcessUtil.getAllCheJian();
    }

    private static ArrayList<String> getSelectList() throws RemoteException {
        return ProcessUtil.getAllCheJian();
    }

    private static String getOldValue(ModelContext mc, String key) {
        try {
            NmCommandBean commandBean = mc.getNmCommandBean();
            HttpServletRequest request = commandBean.getRequest();
            WTProperties prop = WTProperties.getLocalProperties();
            String wt_temp = prop.getProperty("wt.temp");
            WTUser user = (WTUser) SessionHelper.getPrincipal();
            File file = new File(wt_temp + File.separator + user.getName() + File.separator + "record.properties");
            if (!file.exists()) {
                return "";
            }
            InputStream is = new FileInputStream(file);
            if (is != null) {
                Properties pro = new Properties();
                pro.load(is);
                is.close();
                if (pro.get(key) == null) {
                    return "";
                } else {
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

    public static List<WTPart> getAllVersionWtpart(WTPart part) {
        List<WTPart> list = new ArrayList<WTPart>();
        try {
            String s = part.getVersionIdentifier().getValue();
            WTPart[] parts = wt.clients.prodmgmt.WTPartHelper.findPartByNumber(part.getNumber());
            for (WTPart wtpart : parts) {
                if (s.equals(wtpart.getVersionIdentifier().getValue())) {
                    list.add(wtpart);
                }
            }

        } catch (PersistenceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return list;

    }

    public static WTDocument getZProcessPlan(WTPart part) throws WTException {
        WTDocument document = null;
        List<WTDocument> docList = WTPartUtil.getDescribedDocumentByPart(part, "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN");
        for (WTDocument doc : docList) {
            String planType = IBAHelper.getIBAValue(doc, "PPLANTYPE");
            String zfFlag = IBAHelper.getIBAValue(doc, "ZFFLAG");
            if ("正式工艺文件".equals(planType) && "Z".equals(zfFlag)) {
                document = doc;
            }
        }
        return document;
    }

    public static String getColumnAttr(WTPart currentPart, WTPart topPart, String actionType, String actionName, String columnName, String type) throws WTException {

        String result = "";
        String currentPartOR = PersistenceHelper.getObjectIdentifier(currentPart).toString();
        String currentPartVR = getPartVR(currentPart);
        String topPartVR = getPartVR(topPart);

        if ("id".equals(type)) {
            if ("zhuzhichejian".equals(columnName)) {
                result = currentPartVR + "_zhuzhichejian";
            } else if ("jihuawanchengshijian".equals(columnName)) {
                result = currentPartVR + "_jihuawanchengshijian";
            } else if ("renwuyaoqiu".equals(columnName)) {
                result = currentPartOR + "_renwuyaoqiu";
            } else if ("renwuyiju".equals(columnName)) {
                result = currentPartVR + "_renwuyiju";
            }
        }
        if ("name".equals(type)) {
            if ("zhuzhichejian".equals(columnName)) {
                //customProcessTask$tempProAssignTask$VR:wt.part.WTPart:3342822$___wt.part.WTPart:3548492_zhuzhichejian___combobox
                result = actionType + "$" + actionName + "$VR:" + topPartVR + "$___" + currentPartOR + "_zhuzhichejian___combobox";
            } else if ("jihuawanchengshijian".equals(columnName)) {
                //customProcessTask$tempProAssignTask$VR:wt.part.WTPart:3342822$___VR:wt.part.WTPart:3361674_col_wt.part.WTPart:3548492_jihuawanchengshijian___textbox
                result = actionType + "$" + actionName + "$VR:" + topPartVR + "$___VR:" + currentPartVR + "_col_" + currentPartOR + "_jihuawanchengshijian___textbox";
            } else if ("renwuyaoqiu".equals(columnName)) {
                //customProcessTask$tempProAssignTask$VR:wt.part.WTPart:3342822$___wt.part.WTPart:3548492_renwuyaoqiu___textbox
                result = actionType + "$" + actionName + "$VR:" + topPartVR + "$___" + currentPartOR + "_renwuyaoqiu___textbox";
            } else if ("renwuyiju".equals(columnName)) {
                //customProcessTask$tempProAssignTask$VR:wt.part.WTPart:3342822$___wt.part.WTPart:3548492_renwuyiju___combobox
                result = actionType + "$" + actionName + "$VR:" + topPartVR + "$___" + currentPartOR + "_renwuyiju___combobox";
            }
        }
        return result;
    }

    public static String getPartVR(WTPart part) throws WTException {
        ReferenceFactory rf = new ReferenceFactory();
        String vr = "";
        Versioned version;
        if(part != null){
            QueryResult qr = VersionControlHelper.service.allVersionsFrom(part);
            if (qr.hasMoreElements()) {
                version = (Versioned) qr.nextElement();
                vr = rf.getReference(version).toString();
            }
        }
        vr = vr.replaceAll(">", ":");
        return vr;
    }
}
