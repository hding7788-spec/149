package ext.casc.workflow.tree;

import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.constants.Constants;
import ext.casc.process.ProcessConstants;
import ext.casc.process.util.ProcessUtil;
import ext.casc.util.IBAUtil;
import ext.casc.util.IBAUtility;
import ext.casc.util.WCUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import wt.doc.WTDocument;
import wt.enterprise.Master;
import wt.fc.*;
import wt.fc.collections.WTArrayList;
import wt.inf.container.WTContainer;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.pom.PersistenceException;
import wt.session.SessionHelper;
import wt.util.IconSelector;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.vc.config.ConfigHelper;
import wt.vc.config.ConfigSpec;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

public class GenerateProcecssTaskJson {
    private String partOid;
    private List<String> valueList;

    public GenerateProcecssTaskJson(String oid) {
        this.partOid = oid;
    }

    public JSONObject generateTable(HttpSession session) throws Exception {
        ReferenceFactory rf = new ReferenceFactory();
        WTPart wtPart = (WTPart) rf.getReference(partOid).getObject();
        session.setAttribute("topOid", String.valueOf(PersistenceHelper.getObjectIdentifier(wtPart).getId()));
        String taskType = (String) session.getAttribute("TaskType");
        List<WTPart> allChildrenList = new ArrayList();
        List<WTPart> parents = new ArrayList();
        parents.add(wtPart);
        WTContainer parentContainer = wtPart.getContainer();
        getAllChildPartsByPart(allChildrenList, parents, parentContainer);
        allChildrenList.add(wtPart);
        valueList = ProcessUtil.getAllCheJian();
        JSONObject jsonObject = generateData(wtPart, allChildrenList, taskType);
        JSONArray array = (JSONArray) jsonObject.get("data");
        jsonObject.put("totalCount", array.length());
        return jsonObject;
    }

    public JSONObject generateData(WTPart topPart, List<WTPart> datas, String taskType) {
        JSONObject result = new JSONObject();
        JSONArray array = new JSONArray();
        ReferenceFactory rf = new ReferenceFactory();
        try {
            for (WTPart part : datas) {
                JSONObject jsonObject = new JSONObject();
                IBAUtil iba = new IBAUtil(part);

                jsonObject.put("id", getDataValue(part, topPart, "id", taskType));
                jsonObject.put("type_icon", "<img src=\"wtcore/images/part.gif\">");
                jsonObject.put("number", part.getNumber() + "@" + rf.getReferenceString(part));
                jsonObject.put("name", part.getName());
                jsonObject.put("version", part.getVersionIdentifier().getValue() + "." + part.getIterationIdentifier().getValue());
                jsonObject.put("CINDEX", iba.getIBAValue("CINDEX"));
                jsonObject.put("MINDEX", iba.getIBAValue("MINDEX"));
                jsonObject.put("CMAT", iba.getIBAValue("CMAT"));
                jsonObject.put("PHASE_CODE", iba.getIBAValue("PHASE_CODE"));
                jsonObject.put("MTYPE", iba.getIBAValue("MTYPE"));
                jsonObject.put("zhuzhichejian", getDataValue(part, topPart, "zhuzhichejian", taskType));
                jsonObject.put("jihuawanchengshijian", getDataValue(part, topPart, "jihuawanchengshijian", taskType));
                jsonObject.put("cldePlanTime", getDataValue(part, topPart, "cldePlanTime", taskType));
                jsonObject.put("renwuyaoqiu", getDataValue(part, topPart, "renwuyaoqiu", taskType));
                jsonObject.put("renwuyiju", getDataValue(part, topPart, "renwuyiju", taskType));

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

    public String getDataValue(WTPart currentPart, WTPart topPart, String columnName, String taskType) throws Exception {
        String value = "";
        String zzcjValue;
        String state;
        if (!taskType.equals(ProcessConstants.TASK_TYPE_LINSHIGONGYI) && !taskType.equals(ProcessConstants.TASK_TYPE_GONGYIGENGGAI)) {
            List<WTPart> parts = this.getAllVersionWtpart(currentPart);
            for (WTPart wtpart : parts) {
                WTDocument doc = getZProcessPlan(wtpart);
                //有正式主工艺
                if (doc != null) {
                    state = doc.getState().getState().getDisplay();
                    if ("已作废".equals(state) || "正在工作".equals(state)) {
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
            value = "custom$createProcessTask$VR:" + getPartVR(topPart) + "$VR:" + getPartVR(currentPart) + "!*";
        } else if (columnName.equals("icontype")) {
            String imgUrl = getIcon(topPart);
            value = "<img src=\"" + imgUrl + "\">";
        } else if (columnName.equals("zhuzhichejian")) {
            IBAUtility ibaUtility = new IBAUtility(currentPart);
            zzcjValue = ibaUtility.getIBAValue("ZZCJ");
            if (zzcjValue == null) {
                zzcjValue = "";
            }
            StringBuffer sb = new StringBuffer();
            sb.append("<select id=\"" + getColumnAttr(currentPart, topPart, "custom", "createProcessTask", "zhuzhichejian", "id") + "\" " +
                    "name=\"" + getColumnAttr(currentPart, topPart, "custom", "createProcessTask", "zhuzhichejian", "name") + "\" onChange=\"verify1(this)\">");

            for (String optionValue : valueList) {
                if (optionValue.equals(zzcjValue)) {
                    sb.append("<option value=\"" + optionValue + "\" selected=\"selected\">");
                    sb.append(optionValue);
                    sb.append("</option>");
                } else {
                    sb.append("<option value=\"" + optionValue + "\">");
                    sb.append(optionValue);
                    sb.append("</option>");
                }
            }
            value = sb.toString();
        } else if (columnName.equals("jihuawanchengshijian")) {
            StringBuffer sb = new StringBuffer();
            sb.append("<input type=\"text\" id=\"" + getColumnAttr(currentPart, topPart, "custom", "createProcessTask", "jihuawanchengshijian", "id") + "\" " +
                    "name=\"" + getColumnAttr(currentPart, topPart, "custom", "createProcessTask", "jihuawanchengshijian", "name") + "\" " +
                    "onchange=\"verify3(this)\"" +
                    "onBlur=\"validateDate1(this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)\" " +
                    "onkeypress=\"validateDate1ForEnterKey(event , this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)\" " +
                    "value=\"\"  size=\"10\" maxlength=\"10\"/>\n\t\t\t<A HREF=\"javascript:void(0)\"  " +
                    "onmousedown=\"suppressCalendarBlur(event);\"" +
                    "onClick=\"initCal('\\u65e5\\u5386', '<LINK REL=stylesheet HREF=/Windchill/netmarkets/css/siteStyles.css TYPE=text/css>', " +
                    "'/Windchill/templates/cadx/common/trlUtils.js', 3, 2, 1, '', '', false, 100, 1932); " +
                    "setDateField(document.getElementsByName('" + getColumnAttr(currentPart, topPart, "custom", "createProcessTask", "jihuawanchengshijian", "name") + "')[0], 'yyyy/MM/dd', '\\u4e00\\u6708#\\u4e8c\\u6708#\\u4e09\\u6708#\\u56db\\u6708#\\u4e94\\u6708#\\u516d\\u6708#\\u4e03\\u6708#\\u516b\\u6708#\\u4e5d\\u6708#\\u5341\\u6708#\\u5341\\u4e00\\u6708#\\u5341\\u4e8c\\u6708#', '#', '\\u661f\\u671f\\u65e5#\\u661f\\u671f\\u4e00#\\u661f\\u671f\\u4e8c#\\u661f\\u671f\\u4e09#\\u661f\\u671f\\u56db#\\u661f\\u671f\\u4e94#\\u661f\\u671f\\u516d#', '0'); " +
                    "newCalendarWindow(event, '/Windchill/netmarkets/jsp/util/calPopup.jsp', 'height=220,width=240')\">\n\t\t\t" +
                    "<IMG name=\"calImg\" SRC=\"/Windchill/netmarkets/images/calendar.gif\" WIDTH=18 HEIGTH=16 BORDER=0></A><font class=hlpTxt>yyyy/mm/dd</font>");
            value = sb.toString();
       //cldePlanTime
        }else if(columnName.equals("cldePlanTime")){
            StringBuffer sb = new StringBuffer();
            sb.append("<input type=\"text\" id=\"" + getColumnAttr(currentPart, topPart, "custom", "createProcessTask", "cldePlanTime", "id") + "\" " +
                    "name=\"" + getColumnAttr(currentPart, topPart, "custom", "createProcessTask", "cldePlanTime", "name") + "\" " +
                    "onchange=\"verify4(this)\"" +
                    "onBlur=\"validateDate1(this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)\" " +
                    "onkeypress=\"validateDate1ForEnterKey(event , this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)\" " +
                    "value=\"\"  size=\"10\" maxlength=\"10\"/>\n\t\t\t<A HREF=\"javascript:void(0)\"  " +
                    "onmousedown=\"suppressCalendarBlur(event);\"" +
                    "onClick=\"initCal('\\u65e5\\u5386', '<LINK REL=stylesheet HREF=/Windchill/netmarkets/css/siteStyles.css TYPE=text/css>', " +
                    "'/Windchill/templates/cadx/common/trlUtils.js', 3, 2, 1, '', '', false, 100, 1932); " +
                    "setDateField(document.getElementsByName('" + getColumnAttr(currentPart, topPart, "custom", "createProcessTask", "cldePlanTime", "name") + "')[0], 'yyyy/MM/dd', '\\u4e00\\u6708#\\u4e8c\\u6708#\\u4e09\\u6708#\\u56db\\u6708#\\u4e94\\u6708#\\u516d\\u6708#\\u4e03\\u6708#\\u516b\\u6708#\\u4e5d\\u6708#\\u5341\\u6708#\\u5341\\u4e00\\u6708#\\u5341\\u4e8c\\u6708#', '#', '\\u661f\\u671f\\u65e5#\\u661f\\u671f\\u4e00#\\u661f\\u671f\\u4e8c#\\u661f\\u671f\\u4e09#\\u661f\\u671f\\u56db#\\u661f\\u671f\\u4e94#\\u661f\\u671f\\u516d#', '0'); " +
                    "newCalendarWindow(event, '/Windchill/netmarkets/jsp/util/calPopup.jsp', 'height=220,width=240')\">\n\t\t\t" +
                    "<IMG name=\"calImg\" SRC=\"/Windchill/netmarkets/images/calendar.gif\" WIDTH=18 HEIGTH=16 BORDER=0></A><font class=hlpTxt>yyyy/mm/dd</font>");
            value = sb.toString();
        } else if (columnName.equals("renwuyaoqiu")) {
            StringBuffer sb = new StringBuffer();
            sb.append("<input type=\"text\" name=\"" + getColumnAttr(currentPart, topPart, "custom", "createProcessTask", "renwuyaoqiu", "name") + "\" " +
                    "id=\"" + getColumnAttr(currentPart, topPart, "custom", "createProcessTask", "renwuyaoqiu", "id") + "\"/");
            value = sb.toString();
        } else if (columnName.equals("renwuyiju")) {
            StringBuffer sb = new StringBuffer();
            sb.append("<select id=\"" + getColumnAttr(currentPart, topPart, "custom", "createProcessTask", "renwuyiju", "id") + "\" " +
                    "name=\"" + getColumnAttr(currentPart, topPart, "custom", "createProcessTask", "renwuyiju", "name") + "\" onChange=\"verify5(this)\">");
            ArrayList<String> valueList = getRenwuYijuSelectList();
            for (String optionValue : valueList) {
                sb.append("<option value=\"" + optionValue + "\">");
                sb.append(optionValue);
                sb.append("</option>");
            }
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

    public void getAllChildPartsByPart(List allChildrenList, List parents, WTContainer parentContainer) throws WTException {

        Persistable[][][] all_children = WTPartHelper.service.getUsesWTParts(new WTArrayList(parents), getDefaultConfigSpec());
        WTContainer childContainer = null;
        for (ListIterator i = parents.listIterator(); i.hasNext(); ) {
            WTPart parent = (WTPart) i.next();
            Persistable[][] branch = all_children[i.previousIndex()];
            if (branch == null) {
                continue;
            }
            List children = new ArrayList(branch.length);

            for (Persistable[] child : branch) {
                Persistable per = child[1];
                if (!(per instanceof WTPart) && !(per instanceof WTPartMaster)) {
                    continue;
                }
                WTPart childPart = null;
                if (per instanceof WTPart) {
                    childPart = (WTPart) per;
                    childPart = WCUtil.getLatestPartByView((Master) childPart.getMaster(), "Manufacturing");
                } else if (per instanceof WTPartMaster) {
                    childPart = WCUtil.getLatestPartByView((Master) per, "Manufacturing");
                }

                if (childPart == null) {
                    continue;
                }

                IBAUtility ibaUtility = new IBAUtility(childPart);
                String partType = ibaUtility.getIBAValue("MTYPE");

                if (!children.contains(childPart)) {
                    children.add(childPart);
                }

                //过滤不是自制件、外配套件、带料委外件、不带料委外件类型的零部件
                if (Constants.TYPE_ZIZHIJIAN.equals(partType)
                        || Constants.TYPE_WAIPEITAOJIAN.equals(partType)
                        || Constants.TYPE_DAILIAOWEIWAIJIAN.equals(partType)
                        || Constants.TYPE_BUDAILIAOWEIWAIJIAN.equals(partType)) {
                    childContainer = childPart.getContainer();
                    if (!parentContainer.getName().equals(childContainer.getName())) {//过滤掉借用件，即是跟父件不在同一产品库下的零部件
                        continue;
                    }
                    if (!allChildrenList.contains(childPart)) {
                        allChildrenList.add(childPart);
                    }


                }
            }
            getAllChildPartsByPart(allChildrenList, children, parentContainer);
        }
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
            } else if("cldePlanTime".equals(columnName)){
                result = currentPartVR + "_cldePlanTime";
            }else if ("renwuyaoqiu".equals(columnName)) {
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
            } else if("cldePlanTime".equals(columnName)){
                result = actionType + "$" + actionName + "$VR:" + topPartVR + "$___VR:" + currentPartVR + "_col_" + currentPartOR + "_cldePlanTime___textbox";
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
