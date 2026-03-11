package ext.casc.sop.datautility;

import com.glaway.mpm.util.WTDocumentUtil;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.factory.dataUtilities.AttributeDataUtilityHelper;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.components.rendering.guicomponents.TextBox;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;

import ext.casc.constants.Constants;
import ext.casc.process.util.ProcessUtil;
import ext.casc.sop.bean.DocParametersLinkBean;
import ext.casc.sop.bean.DocSopLinkBean;
import ext.casc.sop.constants.SopConstants;
import ext.casc.sop.util.SopUtil;
import ext.casc.sop.util.SopWorkflowUtil;
import ext.casc.util.IBAUtility;
import ext.casc.util.NmTableGUIComponent;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.ObjectReference;
import wt.fc.collections.WTCollection;
import wt.part.WTPart;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTStandardDateFormat;
import wt.workflow.work.WfAssignedActivity;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * SOP客制化UI类
 */
public class SopObjectDataUtility extends AbstractDataUtility {
    @Override
    public Object getDataValue(String componentId, Object object, ModelContext context) throws WTException {
        NmCommandBean nmcommandbean = context.getNmCommandBean();
        try {
            if ("number".equals(componentId)) {
                //编号:VSOP代号+专业代号+"-"+工位简号+"-"+顺序号
                StringBuilder value = new StringBuilder();
                value.append("<input type=\"text\" id=\"vsop\" value=\"S\" style=\"width:20px;text-align:center\"/>")
                        .append("<input type=\"text\" id=\"zhuanyedaihao\" value=\"\" style=\"width:80px;text-align:center\" readonly/>")
                        .append("-")
                        .append("<input type=\"text\" id=\"gongweijianhao\" value=\"\" style=\"width:80px;text-align:center\" readonly/>")
                        .append("-")
                        .append("<input type=\"text\" id=\"shunxuhao\" value=\"\" style=\"width:25px;text-align:center\" readonly/>");
                NmTableGUIComponent gui = new NmTableGUIComponent(value.toString());
                gui.setRequired(true);
                GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
                guicomponentarrayMain.setValueHidden(false);
                guicomponentarrayMain.addGUIComponent(gui);
                return guicomponentarrayMain;
            } else if ("SpecializedType".equals(componentId)) {
                //专业类别:下拉
                List<WTPart> zylbList = SopUtil.getSopResourceByType(SopConstants.SOP_TYPE_SPECIALIZEDTYPE);
                ArrayList<String> zylbStrList = new ArrayList<String>();
                ArrayList<String> zylbInnerStrList = new ArrayList<String>();
                zylbStrList.add("");
                zylbInnerStrList.add("");
                IBAUtility ibaUtility;
                for (WTPart wtPart : zylbList) {
                    zylbStrList.add(wtPart.getName());
                    ibaUtility = new IBAUtility(wtPart);
                    String zydaihao = ibaUtility.getIBAValue("ProfessionalCode");
                    zylbInnerStrList.add(zydaihao);
                }
                ComboBox comboBox = getComboBox("specializedType",zylbInnerStrList, zylbStrList, true, true);
                comboBox.addJsAction("onchange", "selectSpecializedType()");
                return comboBox;
            } else if ("ProceduceName".equals(componentId)) {
                //工序名称:根据专业类别过滤，下拉
                ArrayList<String> zylbStrList = new ArrayList<String>();
                zylbStrList.add("");
                ComboBox comboBox = getComboBox("procedureName",zylbStrList, zylbStrList, true, true);
                comboBox.addJsAction("onchange", "selectProcedureName()");
                return comboBox;
            } else if ("ProfessionalCode".equals(componentId)) {
                //专业代号:根据专业类别自动生成
                TextBox textBox = getTextBox("professionalCode", "", true, true);
                return textBox;
            } else if ("GONGXUJIANHAO".equals(componentId)) {
                //工序简号：根据工序名称自动生成
                TextBox textBox = getTextBox("stationNo", "", true, true);
                return textBox;
            } else if ("Department".equals(componentId)) {
                //部门:下拉
//            	ArrayList<String> gongXuCheJian = ProcessUtil.getGongXuCheJian();
                ArrayList<String> gongXuCheJian = new ArrayList<>();
                gongXuCheJian.addAll(Constants.allChejian);
            	gongXuCheJian.add(0,"");
                ComboBox comboBox = getComboBox("department", gongXuCheJian,gongXuCheJian, true, true);
                return comboBox;
            } else if ("SECRET".equals(componentId)) {
                //公开|商密|内部|秘密|机密
                ArrayList<String> secretList = new ArrayList<String>();
                secretList.add("");
                secretList.add("公开");
                secretList.add("商密");
                secretList.add("内部");
                secretList.add("秘密");
                secretList.add("机密");
                ComboBox comboBox = getComboBox("SECRET", secretList, secretList, true,true);
                comboBox.addJsAction("onchange", "selectSecret()");
                return comboBox;
            } else if("Term".equals(componentId)){
                StringBuilder value = new StringBuilder();
                String textColName = context.getNmCommandBean().getCompContext() + "___termtext___textbox";
                String selectColName = context.getNmCommandBean().getCompContext() + "___term___combobox";
                value.append("<input type=\"text\" id=\"termtext\" name=\""+textColName+"\" value=\"\" style=\"width:80px;display:none;\">");
                value.append("<select id=\"term\" name=\""+selectColName+"\" class=\"required\" style=\"\">");
                value.append("<option></option>");
                value.append("</select>");
                NmTableGUIComponent gui = new NmTableGUIComponent(value.toString());
                gui.setRequired(true);
                GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
                guicomponentarrayMain.setValueHidden(false);
                guicomponentarrayMain.addGUIComponent(gui);
                return guicomponentarrayMain;
            } else if (componentId.contains("yinyongSop")) {
    			if (object instanceof DocSopLinkBean) {
    				DocSopLinkBean bean = (DocSopLinkBean) object;
    				String docNumber = bean.getDocNum();
    				String docVersion = bean.getDocVersion();
    				WTDocument document = WTDocumentUtil.getDocumentByNumberAndVersion(docNumber, docVersion);
    				if ("yinyongSopVersion".equals(componentId)) {
    					return document.getIterationDisplayIdentifier().toString();
    				} else if ("yinyongSopBianZhiZhe".equals(componentId)) {
    					return document.getModifierFullName();
    				}  else if ("yinyongSopPiZhunTime".equals(componentId)) {
    					WfAssignedActivity activity;
    					WTCollection coll2 = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(document);
    					Iterator it2 = coll2.iterator();
    					if (it2.hasNext()) {
    						WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it2.next()).getObject();
    						activity = SopWorkflowUtil.getWfActivity(ecn, "批准");
    					} else {
    						activity = SopWorkflowUtil.getWfActivity(document, "批准");
    					}
    					if(activity != null){
    						Timestamp endTime = activity.getEndTime();
    						if (endTime != null) {
    							return WTStandardDateFormat.format(activity.getEndTime(), "yyyy/MM/dd");
    						}
    					}
    					return "";
    				} else if ("yinyongSopState".equals(componentId)) {
    					return document.getState().getState().getDisplay(Locale.CHINA);
    				}
    				return "";
    			}

    		} else if (componentId.contains("yiJvSop")) {
    			if (object instanceof DocParametersLinkBean) {
    				DocParametersLinkBean bean = (DocParametersLinkBean) object;
    				String number = bean.getTechnicsNumber();
    				String version = bean.getTechnicsVersion();
    				WTDocument document = WTDocumentUtil.getDocumentByNumberAndVersion(number, version);
    				if ("yiJvSopVersion".equals(componentId)) {
    					return document.getIterationDisplayIdentifier().toString();
    				} else if ("yiJvSopPiZhun".equals(componentId)) {
    					WfAssignedActivity activity;
    					WTCollection coll2 = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(document);
    					Iterator it2 = coll2.iterator();
    					if (it2.hasNext()) {
    						WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it2.next()).getObject();
    						activity = SopWorkflowUtil.getWfActivity(ecn, "批准");
    					} else {
    						activity = SopWorkflowUtil.getWfActivity(document, "批准");
    					}
    					if(activity != null){
    						Timestamp endTime = activity.getEndTime();
    						if (endTime != null) {
    							return WTStandardDateFormat.format(activity.getEndTime(), "yyyy/MM/dd");
    						}
    					}
    					return "";
    				} else if ("yiJvSopState".equals(componentId)) {
    					return document.getState().getState().getDisplay(Locale.CHINA);
    				}
    				return "";
    			}

    		}
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private ComboBox getComboBox(String id, ArrayList<String> innerValueList, ArrayList<String> valueList, boolean isRequired, boolean isEditable) {
        ComboBox comboBox = new ComboBox();
        comboBox.setId(id);
        comboBox.setName(id);
        comboBox.setRequired(isRequired);
        comboBox.setValues(valueList);
        comboBox.setInternalValues(innerValueList);
        comboBox.setEditable(isEditable);
        return comboBox;
    }

    private TextBox getTextBox(String componentId, String value, boolean isRequired, boolean isReadOnly) {
        TextBox textbox = new TextBox();
        textbox.setRequired(isRequired);
        textbox.setName(componentId);
        textbox.setValue(value);
        textbox.setId(componentId);
        textbox.setWidth(20);
        textbox.setMaxLength(255);
        textbox.setReadOnly(isReadOnly);
        return textbox;
    }
}
