package ext.casc.sop.task;

import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.process.ProcessConstants;
import ext.casc.process.util.ProcessUtil;
import ext.casc.sop.constants.SopConstants;
import ext.casc.sop.util.SopUtil;
import ext.casc.util.IBAUtil;
import ext.casc.util.IBAUtility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import wt.doc.WTDocument;
import wt.fc.*;
import wt.folder.FolderHelper;
import wt.folder.SubFolder;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.pom.PersistenceException;
import wt.session.SessionHelper;
import wt.type.TypedUtility;
import wt.type.TypedUtilityServiceHelper;
import wt.util.IconSelector;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.vc.config.ConfigHelper;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;

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

public class GenerateSopTaskJson {
    private String partOid;
    private List<String> valueList;

    public GenerateSopTaskJson(String oid) {
        this.partOid = oid;
    }

    public JSONObject generateTable(HttpSession session) throws Exception {
        ReferenceFactory rf = new ReferenceFactory();
        String[] oids = partOid.split(",");
        List<WTPart> wtPartList = new ArrayList<WTPart>();
        for (String objOid : oids) {
            Persistable object = rf.getReference(objOid).getObject();
            if (object instanceof WTPart) {
                WTPart wtPart = (WTPart) object;
                String partType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(wtPart);
                if (partType.contains(SopConstants.SOP_TYPE_SOPPART)) {
                    wtPartList.add(wtPart);
                }
            } else if (object instanceof SubFolder) {
                SubFolder subFolder = (SubFolder) object;
                getFolderContents(subFolder, wtPartList);
            }
        }
        String taskType = (String) session.getAttribute("TaskType");

        valueList = ProcessUtil.getAllCheJian();
        JSONObject jsonObject = generateData(wtPartList, taskType);
        JSONArray array = (JSONArray) jsonObject.get("data");
        jsonObject.put("totalCount", array.length());
        return jsonObject;
    }

    private void getFolderContents(SubFolder subFolder, List<WTPart> wtPartList) throws WTException, RemoteException {
        QueryResult queryResult = FolderHelper.service.findFolderContents(subFolder);
        while (queryResult.hasMoreElements()) {
            Object o = queryResult.nextElement();
            if (o instanceof SubFolder) {
                SubFolder folder = (SubFolder) o;
                getFolderContents(folder, wtPartList);
            } else if (o instanceof WTPart) {
                WTPart wtPart = (WTPart) o;
                String partType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(wtPart);
                if (partType.contains(SopConstants.SOP_TYPE_SOPPART)) {
                    wtPartList.add(wtPart);
                }
            }
        }
    }

    public JSONObject generateData(List<WTPart> datas, String taskType) {
        JSONObject result = new JSONObject();
        JSONArray array = new JSONArray();
        ReferenceFactory rf = new ReferenceFactory();
        try {
            for (WTPart part : datas) {
            	//校检是否下发过任务
            	if (SopUtil.isExistProcessTask(part, taskType)) {
                    continue;
                }
                JSONObject jsonObject = new JSONObject();
                IBAUtil iba = new IBAUtil(part);

                jsonObject.put("id", getDataValue(part, "id", taskType));
                jsonObject.put("type_icon", "<img src=\"wtcore/images/part.gif\">");
                jsonObject.put("number", part.getNumber() + "@" + rf.getReferenceString(part));
                jsonObject.put("name", part.getName());
                jsonObject.put("version", part.getVersionIdentifier().getValue() + "." + part.getIterationIdentifier().getValue());
                jsonObject.put("MINDEX", iba.getIBAValue("MINDEX"));
                jsonObject.put("PHASE_CODE", iba.getIBAValue("PHASE_CODE"));
                jsonObject.put("gongyiyuan", getDataValue(part, "gongyiyuan", taskType));
                jsonObject.put("jihuawanchengshijian", getDataValue(part, "jihuawanchengshijian", taskType));
                jsonObject.put("renwuyaoqiu", getDataValue(part, "renwuyaoqiu", taskType));

                array.put(jsonObject);
            }
            result.put("data", array);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public String getDataValue(WTPart currentPart, String columnName, String taskType) throws Exception {
        String value = "";
        String zzcjValue;
        String gongyiyuan;
        String state;
        IBAUtility ibaUtility;
        if (!taskType.equals(ProcessConstants.TASK_TYPE_LINSHIGONGYI) && !taskType.equals(ProcessConstants.TASK_TYPE_GONGYIGENGGAI)) {
            List<WTPart> parts = this.getAllVersionWtpart(currentPart);
            for (WTPart wtpart : parts) {
                WTDocument doc = getZProcessPlan(wtpart);
                //有正式主工艺
                if (doc != null) {
                    state = doc.getState().getState().getDisplay();
                    if ("已作废".equals(state)) {
                        if (ProcessUtil.isExistProcessTask(wtpart, taskType)) {
                            return value;
                        }
                    } else {
                        return value;
                    }
                    //无正式主工艺
                } else {
                    if (ProcessUtil.isExistProcessTask(wtpart, taskType)) {
                        return value;
                    }
                }

            }
        }
        if ("id".equals(columnName)) {
            value = "sopCustom$createProcessTask$VR:" + getPartVR(currentPart) + "!*";
        } else if (columnName.equals("icontype")) {
            String imgUrl = getIcon(currentPart);
            value = "<img src=\"" + imgUrl + "\">";
        } else if (columnName.equals("gongyiyuan")) {
            ibaUtility = new IBAUtility(currentPart);
            QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(currentPart, true);
            LatestConfigSpec lcs = new LatestConfigSpec();
            qr = lcs.process(qr);
            String oldUserName = "";
            while (qr.hasMoreElements()) {
                Object o = qr.nextElement();
                if (o instanceof WTDocument) {
                    WTDocument document = (WTDocument) o;
                    String docType = TypedUtility.getTypeIdentifier(document).getTypename();
                    if (docType.contains(SopConstants.SOP_TYPE_SOPDOC)) {
                        oldUserName = document.getModifierFullName();
                    }
                }
            }
            String department = ibaUtility.getIBAValue("Department");
            List<WTUser> userList = SopUtil.getUserListFromLibrary(currentPart,department);

            StringBuilder sb = new StringBuilder();
            sb.append("<select id=\"").append(getColumnAttr(currentPart, "sopCustom", "createProcessTask", "gongyiyuan", "id")).append("\" ").append("name=\"").append(getColumnAttr(currentPart, "sopCustom", "createProcessTask", "gongyiyuan", "name")).append("\" onChange=\"verify1(this)\">");

            for(WTUser wtUser : userList) {
                String userName = wtUser.getName();
                String userFullName = wtUser.getFullName();
                if(oldUserName != null && !oldUserName.isEmpty()){
                    if(userFullName.equals(oldUserName)){
                        sb.append("<option secelted value=\"").append(userName).append("\">");
                        sb.append(userFullName);
                        sb.append("</option>");
                    }
                }else{
                    sb.append("<option value=\"").append(userName).append("\">");
                    sb.append(userFullName);
                    sb.append("</option>");
                }
            }
            value = sb.toString();
        } else if (columnName.equals("jihuawanchengshijian")) {
            value = ("<input type=\"text\" id=\"" + getColumnAttr(currentPart, "sopCustom", "createProcessTask", "jihuawanchengshijian", "id") + "\" " +
                    "name=\"" + getColumnAttr(currentPart, "sopCustom", "createProcessTask", "jihuawanchengshijian", "name") + "\" " +
                    "onchange=\"verify3(this)\"" +
                    "onBlur=\"validateDate1(this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)\" " +
                    "onkeypress=\"validateDate1ForEnterKey(event , this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)\" " +
                    "value=\"\"  size=\"10\" maxlength=\"10\"/>\n\t\t\t<A HREF=\"javascript:void(0)\"  " +
                    "onmousedown=\"suppressCalendarBlur(event);\"" +
                    "onClick=\"initCal('\\u65e5\\u5386', '<LINK REL=stylesheet HREF=/Windchill/netmarkets/css/siteStyles.css TYPE=text/css>', " +
                    "'/Windchill/templates/cadx/common/trlUtils.js', 3, 2, 1, '', '', false, 100, 1932); " +
                    "setDateField(document.getElementsByName('" + getColumnAttr(currentPart, "sopCustom", "createProcessTask", "jihuawanchengshijian", "name") + "')[0], 'yyyy/MM/dd', '\\u4e00\\u6708#\\u4e8c\\u6708#\\u4e09\\u6708#\\u56db\\u6708#\\u4e94\\u6708#\\u516d\\u6708#\\u4e03\\u6708#\\u516b\\u6708#\\u4e5d\\u6708#\\u5341\\u6708#\\u5341\\u4e00\\u6708#\\u5341\\u4e8c\\u6708#', '#', '\\u661f\\u671f\\u65e5#\\u661f\\u671f\\u4e00#\\u661f\\u671f\\u4e8c#\\u661f\\u671f\\u4e09#\\u661f\\u671f\\u56db#\\u661f\\u671f\\u4e94#\\u661f\\u671f\\u516d#', '0'); " +
                    "newCalendarWindow(event, '/Windchill/netmarkets/jsp/util/calPopup.jsp', 'height=220,width=240')\">\n\t\t\t" +
                    "<IMG name=\"calImg\" SRC=\"/Windchill/netmarkets/images/calendar.gif\" WIDTH=18 HEIGTH=16 BORDER=0></A><font class=hlpTxt>yyyy/mm/dd</font>");
        } else if (columnName.equals("renwuyaoqiu")) {
            StringBuilder sb = new StringBuilder();
            sb.append("<input type=\"text\" name=\"").append(getColumnAttr(currentPart, "sopCustom", "createProcessTask", "renwuyaoqiu", "name")).append("\" ").append("id=\"").append(getColumnAttr(currentPart, "sopCustom", "createProcessTask", "renwuyaoqiu", "id")).append("\"/");
            value = sb.toString();
        }
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


    protected ConfigSpec getDefaultConfigSpec() throws WTException {
        return ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class);
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

    public static String getColumnAttr(WTPart currentPart, String actionType, String actionName, String columnName, String type) throws WTException {

        String result = "";
        String currentPartOR = PersistenceHelper.getObjectIdentifier(currentPart).toString();
        String currentPartVR = getPartVR(currentPart);

        if ("id".equals(type)) {
            if ("gongyiyuan".equals(columnName)) {
                result = currentPartVR + "_gongyiyuan";
            } else if ("jihuawanchengshijian".equals(columnName)) {
                result = currentPartVR + "_jihuawanchengshijian";
            } else if ("renwuyaoqiu".equals(columnName)) {
                result = currentPartOR + "_renwuyaoqiu";
            }
        }
        if ("name".equals(type)) {
            if ("gongyiyuan".equals(columnName)) {
                //customProcessTask$tempProAssignTask$VR:wt.part.WTPart:3342822$___wt.part.WTPart:3548492_zhuzhichejian___combobox
                result = actionType + "$" + actionName + "$___" + currentPartOR + "_gongyiyuan___combobox";
            } else if ("jihuawanchengshijian".equals(columnName)) {
                //customProcessTask$tempProAssignTask$VR:wt.part.WTPart:3342822$___VR:wt.part.WTPart:3361674_col_wt.part.WTPart:3548492_jihuawanchengshijian___textbox
                result = actionType + "$" + actionName + "$___VR:" + currentPartVR + "_col_" + currentPartOR + "_jihuawanchengshijian___textbox";
            } else if ("renwuyaoqiu".equals(columnName)) {
                //customProcessTask$tempProAssignTask$VR:wt.part.WTPart:3342822$___wt.part.WTPart:3548492_renwuyaoqiu___textbox
                result = actionType + "$" + actionName + "$___" + currentPartOR + "_renwuyaoqiu___textbox";
            }
        }
        return result;
    }

    public static String getPartVR(WTPart part) throws WTException {
        ReferenceFactory rf = new ReferenceFactory();
        String vr = "";
        Versioned version;
        QueryResult qr = VersionControlHelper.service.allVersionsFrom(part);
        if (qr.hasMoreElements()) {
            version = (Versioned) qr.nextElement();
            vr = rf.getReference(version).toString();
        }
        vr = vr.replaceAll(">", ":");
        return vr;
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
}
