package ext.ases.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import wt.fc.EnumeratedType;
import wt.fc.EnumeratedTypeUtil;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.inf.container.PrincipalSpec;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.method.MethodContext;
import wt.org.DirectoryContextProvider;
import wt.org.OrganizationServicesHelper;
import wt.org.OrganizationServicesMgr;
import wt.org.WTGroup;
import wt.org.WTOrganization;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.team.TeamReference;
import wt.team.TeamTemplate;
import wt.team.TeamTemplateReference;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.util.WTStandardDateFormat;
import wt.workflow.SortedEnumByPrincipal;
import wt.workflow.definer.ProcessDataInfo;
import wt.workflow.definer.WfAssignedActivityTemplate;
import wt.workflow.definer.WfVariableInfo;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfDueDate;
import wt.workflow.engine.WfVariable;
import wt.workflow.work.WfHtmlFormat;
import wt.workflow.work.WorkItem;
import wt.workflow.worklist.WfTaskProcessor;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.CheckBox;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.components.rendering.guicomponents.TextArea;
import com.ptc.core.components.rendering.guicomponents.TextBox;
import com.ptc.core.components.rendering.guicomponents.TextDisplayComponent;
import com.ptc.windchill.enterprise.workitem.ComponentId;

public class CustomVariablesDataUtility extends AbstractDataUtility {
    private Locale locale;
    private WorkItem workItem;
    private WfTaskProcessor wfTaskProcessor;
    private ModelContext modelContext;
    private static final String BLANK_SPACE = "&nbsp;";
    public static final String WIDTH_HEIGHT = "width_height";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    private static final int DEFAULT_HEIGHT = 1;
    private static final int DEFAULT_WIDTH = 50;
    private static final String WF_DISPLAY_USERS = "WF_DISPLAY_USERS";
    private static final String WF_USERS = "WF_USERS";
    private static final String WF_DISPLAY_GROUPS = "WF_DISPLAY_GROUPS";
    private static final String WF_GROUPS = "WF_GROUPS";
    private static final String ALL_ACTIVITY_VARIABLES = "all_activity_variables";
    private static final String SPECIAL_INSTRUCTIONS = "special_instructions";

    public CustomVariablesDataUtility() {
        wfTaskProcessor = new WfTaskProcessor();
    }

    public final Object getDataValue(String s, Object obj, ModelContext modelcontext) throws WTException {
        if (!WorkItem.class.isAssignableFrom(obj.getClass())) {
            return TextDisplayComponent.NBSP;
        } else {
            locale = modelcontext.getNmCommandBean() != null ? modelcontext.getNmCommandBean().getLocale() : Locale
                    .getDefault();
            workItem = (WorkItem) obj;
            wfTaskProcessor.setWorkItem(workItem);
            modelContext = modelcontext;
            return getCustomVariables(s);
        }
    }

    private final Object getCustomVariables(String varName) throws WTRuntimeException {
        WfActivity wfactivity = wfTaskProcessor.getActivity();
        ProcessData processdata = wfactivity.getContext();
        if (workItem.getContext() == null || wfTaskProcessor.getWorkItem().getContext() == null)
            processdata = wfactivity.getContext();
        else processdata = workItem.getContext();
        WfAssignedActivityTemplate wfassignedactivitytemplate = (WfAssignedActivityTemplate) wfactivity
                .getTemplateReference().getObject();
        ProcessDataInfo processdatainfo = wfassignedactivitytemplate.getContextSignature();
        WfVariableInfo varInfos[] = processdatainfo.getVariableList();
        int width = DEFAULT_WIDTH;
        int height = DEFAULT_HEIGHT;
        String width_height = (String) modelContext.getDescriptor().getProperty("width_height");

        int ai[] = getCustomWidthHeight(width_height);
        if (ai[0] > 0)
            width = ai[0];
        if (ai[1] > 0)
            height = ai[1];
        String includes = (String) modelContext.getDescriptor().getProperty("includes");
        String excludes = (String) modelContext.getDescriptor().getProperty("excludes");

        ArrayList<WfVariableInfo> displayList = new ArrayList<WfVariableInfo>(1);
        String[] includeVars = includes == null ? null : includes.trim().split("[,;；，:：]+");
        String[] excludeVars = excludes == null ? null : excludes.trim().split("[,;；，:：]+");
        for (int i = 0; i < varInfos.length; i++) {
            if (!displayCustomVariable(varInfos[i], varName))
                continue;
            if (excludeVars != null && excludeVars.length > 0) {
                boolean excluded = false;
                for (int j = 0; j < excludeVars.length; j++) {
                    if (excludeVars[j].equals(varInfos[i].getName())) {
                        excluded = true;
                        break;
                    }
                }
                if (excluded)
                    continue;
            }
            if (includeVars != null && includeVars.length > 0) {
                for (int j = 0; j < includeVars.length; j++) {
                    if (includeVars[j].equals(varInfos[i].getName())) {
                        displayList.add(varInfos[i]);
                        break;
                    }
                }
                continue;
            }
            displayList.add(varInfos[i]);
        }

        GUIComponentArray guicomponentarray = new GUIComponentArray();
        guicomponentarray.setId(ComponentId.WORKITEM_CUSTOMVARIABLE.getId());
        for (int l = 0; l < displayList.size(); l++) {
            WfVariableInfo wfVarInfo = displayList.get(l);
            if (!displayCustomVariable(wfVarInfo, varName))
                continue;
            WfVariable wfVar = processdata.getVariable(wfVarInfo.getName());
            Class wfVarClass = wfVar.getVariableClass();
            String typeName = wfVar.getTypeName();
            String wfVarName = wfVarInfo.getName();
            String wfVarDisplayName = wfVarInfo.getDisplayName(Locale.SIMPLIFIED_CHINESE);
            guicomponentarray.setRequired(wfVarInfo.isRequired());

            if (WTObject.class.isAssignableFrom(wfVarClass))
                try {
                    displayWTObjectAssignables(wfVarName, wfVarDisplayName, wfVarInfo.isReadOnly(),
                            wfVarInfo.isRequired(), guicomponentarray);
                    continue;
                } catch (WTException wtexception) {
                    wtexception.printStackTrace();
                }
            if (EnumeratedType.class.isAssignableFrom(wfVarClass)) {
                if (wfVarInfo.isReadOnly()) {
                    String s8 = processdata.getValue(wfVarName) != null ? ((EnumeratedType) processdata
                            .getValue(wfVarName)).getDisplay(locale) : BLANK_SPACE;
                    displayFormatting(wfVarDisplayName, guicomponentarray);
                    TextDisplayComponent textdisplaycomponent = new TextDisplayComponent("");
                    textdisplaycomponent.setValue(s8);
                    guicomponentarray.addGUIComponent(textdisplaycomponent);
                    textdisplaycomponent = new TextDisplayComponent("");
                    textdisplaycomponent.setValue("</td></tr>");
                    textdisplaycomponent.setCheckXSS(false);
                    guicomponentarray.addGUIComponent(textdisplaycomponent);
                    continue;
                }
                EnumeratedType enumeratedtype = null;
                if (processdata.getValue(wfVarName) != null) {
                    Object obj = processdata.getValue(wfVarName);
                    if (obj instanceof String)
                        enumeratedtype = EnumeratedTypeUtil.toEnumeratedType((String) obj);
                    else if (obj instanceof EnumeratedType)
                        enumeratedtype = (EnumeratedType) obj;
                }
                enumeratedTypeSelector(wfVarClass, enumeratedtype, wfVarName, wfVarDisplayName, locale,
                        guicomponentarray);
                continue;
            }
            if (typeName.equals("java.lang.String")) {
                String s9 = (String) processdata.getValue(wfVarName);
                displayFormatting(wfVarDisplayName, guicomponentarray);
                displayTextAreaGui(wfVarName, s9, wfVarInfo.isReadOnly(), guicomponentarray, width, height);
                continue;
            }
            if (typeName.equals("java.lang.Boolean") || typeName.equals("boolean")) {
                boolean flag = ((Boolean) processdata.getValue(wfVarName)).booleanValue();
                displayFormatting(wfVarDisplayName, guicomponentarray);
                CheckBox checkbox = new CheckBox();
                checkbox.setName((new StringBuilder()).append("CustActVar").append(wfVarName).append("CustActVar")
                        .toString());
                checkbox.setChecked(flag);
                checkbox.setColumnName("");
                if (wfVarInfo.isReadOnly()) {
                    checkbox.setEditable(false);
                    guicomponentarray.addGUIComponent(checkbox);
                } else {
                    guicomponentarray.addGUIComponent(checkbox);
                }
                continue;
            }
            if (typeName.equals("java.net.URL")) {
                String s10 = processdata.getValue(wfVarName) != null ? processdata.getValue(wfVarName).toString() : "";
                displayFormatting(wfVarDisplayName, guicomponentarray);
                displayTextGuiWithURL(wfVarName, s10, wfVarInfo.isReadOnly(), guicomponentarray, width);
                continue;
            }
            if (typeName.equals("java.util.Date")) {
                ResourceBundle resourcebundle = ResourceBundle.getBundle("wt.util.utilResource", locale);
                String s12 = "";
                String s14 = resourcebundle.getString("22");
                if (processdata.getValue(wfVarName) != null)
                    s12 = WTStandardDateFormat.format((Date) processdata.getValue(wfVarName), s14);
                displayFormatting(wfVarDisplayName, guicomponentarray);
                if (wfVarInfo.isReadOnly()) {
                    TextDisplayComponent textdisplaycomponent1 = new TextDisplayComponent("");
                    textdisplaycomponent1.setValue(s12);
                    guicomponentarray.addGUIComponent(textdisplaycomponent1);
                    textdisplaycomponent1 = new TextDisplayComponent("");
                    textdisplaycomponent1.setValue("</td></tr>");
                    textdisplaycomponent1.setCheckXSS(false);
                    guicomponentarray.addGUIComponent(textdisplaycomponent1);
                } else {
                    TextBox textbox = new TextBox();
                    textbox.setName((new StringBuilder()).append("CustActVar").append(wfVarName).append("CustActVar")
                            .toString());
                    textbox.setLabel(wfVarName);
                    textbox.setId((new StringBuilder()).append("CustActVar").append(wfVarName).append("CustActVar")
                            .toString());
                    textbox.setValue(s12);
                    textbox.setReadOnly(wfVarInfo.isReadOnly());
                    textbox.setRenderLabel(false);
                    textbox.setWidth(width);
                    guicomponentarray.addGUIComponent(textbox);
                    TextDisplayComponent textdisplaycomponent2 = new TextDisplayComponent("");
                    textdisplaycomponent2.setValue("</td></tr>");
                    textdisplaycomponent2.setCheckXSS(false);
                    guicomponentarray.addGUIComponent(textdisplaycomponent2);
                }
                continue;
            }
            if (typeName.equals("wt.workflow.engine.WfDueDate")) {
                ResourceBundle resourcebundle1 = ResourceBundle.getBundle("wt.util.utilResource", locale);
                String s13 = resourcebundle1.getString("22");
                String s15 = "";
                if (processdata.getValue(wfVarName) != null)
                    s15 = WTStandardDateFormat.format(((WfDueDate) processdata.getValue(wfVarName)).getDeadline(), s13);
                displayFormatting(wfVarDisplayName, guicomponentarray);
                if (wfVarInfo.isReadOnly()) {
                    TextDisplayComponent textdisplaycomponent3 = new TextDisplayComponent("");
                    textdisplaycomponent3.setValue(s15);
                    guicomponentarray.addGUIComponent(textdisplaycomponent3);
                    textdisplaycomponent3 = new TextDisplayComponent("");
                    textdisplaycomponent3.setValue("</td></tr>");
                    textdisplaycomponent3.setCheckXSS(false);
                    guicomponentarray.addGUIComponent(textdisplaycomponent3);
                } else {
                    TextBox textbox1 = new TextBox();
                    textbox1.setName((new StringBuilder()).append("CustActVar").append(wfVarName).append("CustActVar")
                            .toString());
                    textbox1.setLabel(wfVarName);
                    textbox1.setId((new StringBuilder()).append("CustActVar").append(wfVarName).append("CustActVar")
                            .toString());
                    textbox1.setValue(s15);
                    textbox1.setReadOnly(wfVarInfo.isReadOnly());
                    textbox1.setRenderLabel(false);
                    textbox1.setWidth(width);
                    guicomponentarray.addGUIComponent(textbox1);
                    TextDisplayComponent textdisplaycomponent4 = new TextDisplayComponent("");
                    textdisplaycomponent4.setValue("</td></tr>");
                    textdisplaycomponent4.setCheckXSS(false);
                    guicomponentarray.addGUIComponent(textdisplaycomponent4);
                }
            } else {
                String s11 = processdata.getValue(wfVarName) != null ? processdata.getValue(wfVarName).toString()
                        : null;
                displayFormatting(wfVarDisplayName, guicomponentarray);
                displayTextBoxGui(wfVarName, s11, wfVarInfo.isReadOnly(), guicomponentarray, width);
            }
        }
        return guicomponentarray;
    }

    private void displayFormatting(String s, GUIComponentArray guicomponentarray) {
        TextDisplayComponent textdisplaycomponent = new TextDisplayComponent("");
        textdisplaycomponent.setValue("<tr><td align=\"right\" valign=\"top\" nowrap><FONT class=tabledatafont>");
        textdisplaycomponent.setCheckXSS(false);
        guicomponentarray.addGUIComponent(textdisplaycomponent);
        if (guicomponentarray.isRequired()) {
            textdisplaycomponent = new TextDisplayComponent("");
            textdisplaycomponent.setValue("*");
            textdisplaycomponent.setCheckXSS(false);
            guicomponentarray.addGUIComponent(textdisplaycomponent);
        }
        textdisplaycomponent = new TextDisplayComponent("");
        textdisplaycomponent.setValue((new StringBuilder()).append("<b>").append(s).append(":</b>").toString());
        textdisplaycomponent.setCheckXSS(false);
        guicomponentarray.addGUIComponent(textdisplaycomponent);
        textdisplaycomponent = new TextDisplayComponent("");
        textdisplaycomponent.setValue("</td><td align=\"left\" valign=\"top\" nowrap><FONT class=tabledatafont>");
        textdisplaycomponent.setCheckXSS(false);
        guicomponentarray.addGUIComponent(textdisplaycomponent);
    }

    private void displayTextGuiWithURL(String s, String s1, boolean flag, GUIComponentArray guicomponentarray, int i) {
        if (flag) {
            TextDisplayComponent textdisplaycomponent = new TextDisplayComponent("");
            textdisplaycomponent.setValue(s1);
            guicomponentarray.addGUIComponent(textdisplaycomponent);
            textdisplaycomponent = new TextDisplayComponent("");
            textdisplaycomponent.setValue("</td></tr>");
            textdisplaycomponent.setCheckXSS(false);
            guicomponentarray.addGUIComponent(textdisplaycomponent);
        } else {
            TextBox textbox = new TextBox();
            textbox.setName((new StringBuilder()).append("CustActVar").append(s).append("CustActVar").toString());
            textbox.setLabel(s);
            textbox.setId((new StringBuilder()).append("CustActVar").append(s).append("CustActVar").toString());
            textbox.setValue(s1);
            textbox.setReadOnly(flag);
            textbox.setWidth(i);
            textbox.setMaxLength(150);
            textbox.setRenderLabel(false);
            guicomponentarray.addGUIComponent(textbox);
            if (s1 != null) {
                TextDisplayComponent textdisplaycomponent1 = new TextDisplayComponent("");
                textdisplaycomponent1.setValue(s1);
                guicomponentarray.addGUIComponent(textdisplaycomponent1);
            }
            TextDisplayComponent textdisplaycomponent2 = new TextDisplayComponent("");
            textdisplaycomponent2.setValue("</td></tr>");
            textdisplaycomponent2.setCheckXSS(false);
            guicomponentarray.addGUIComponent(textdisplaycomponent2);
        }
    }

    private void displayTextAreaGui(String s, String s1, boolean flag, GUIComponentArray guicomponentarray, int i, int j) {

        // 针对html字符串进行特殊处理 add by guoxiaoyong 20090902
        // System.out.println("s:"+s);
        if (CmUpgradeUtil.transferHTMLFormat(s, s1, flag, guicomponentarray, i, j)) {
            return;
        }

        if(s.equals("selectUnitValue")||s.equals("selectUsersValue")||"siteAndUserIId".equals(s)){
      	  TextBox textbox = new TextBox();
            TextArea textarea = new TextArea();
            textbox.setName((new StringBuilder()).append("CustActVar").append(s).append("CustActVar").toString());
            textbox.setLabel(s);
            textbox.setId((new StringBuilder()).append("CustActVar").append(s).append("CustActVar").toString());
            textbox.setValue(s1);
            textbox.setReadOnly(true);
            textbox.setRenderLabel(false);
            if(s.equals("selectUsersValue")){
            	textbox.setWidth(30);
            }else{
            	textbox.setWidth(10);
            }

            textbox.setMaxLength(1500);
            guicomponentarray.addGUIComponent(textbox);
            TextDisplayComponent textdisplaycomponent1 = new TextDisplayComponent("");
            textdisplaycomponent1.setValue("</td></tr>");
            textdisplaycomponent1.setCheckXSS(false);
            guicomponentarray.addGUIComponent(textdisplaycomponent1);

      	  return;
        }

        if (flag) {
            TextDisplayComponent textdisplaycomponent = new TextDisplayComponent("");
            textdisplaycomponent.setValue(s1);
            guicomponentarray.addGUIComponent(textdisplaycomponent);
            textdisplaycomponent = new TextDisplayComponent("");
            textdisplaycomponent.setValue("</td></tr>");
            textdisplaycomponent.setCheckXSS(false);
            guicomponentarray.addGUIComponent(textdisplaycomponent);
        } else {
            TextArea textarea = new TextArea();
            textarea.setName((new StringBuilder()).append("CustActVar").append(s).append("CustActVar").toString());
            textarea.setLabel(s);
            textarea.setId((new StringBuilder()).append("CustActVar").append(s).append("CustActVar").toString());
            textarea.setValue(s1);
            textarea.setReadOnly(flag);
            textarea.setRenderLabel(false);
            textarea.setHeight(j);
            textarea.setWidth(i);
            textarea.setMaxLength(1500);
            guicomponentarray.addGUIComponent(textarea);
            TextDisplayComponent textdisplaycomponent1 = new TextDisplayComponent("");
            textdisplaycomponent1.setValue("</td></tr>");
            textdisplaycomponent1.setCheckXSS(false);
            guicomponentarray.addGUIComponent(textdisplaycomponent1);
        }
    }

    private void displayTextBoxGui(String s, String s1, boolean flag, GUIComponentArray guicomponentarray, int i) {
        if (flag) {
            TextDisplayComponent textdisplaycomponent = new TextDisplayComponent("");
            textdisplaycomponent.setValue(s1);
            guicomponentarray.addGUIComponent(textdisplaycomponent);
            textdisplaycomponent = new TextDisplayComponent("");
            textdisplaycomponent.setValue("</td></tr>");
            textdisplaycomponent.setCheckXSS(false);
            guicomponentarray.addGUIComponent(textdisplaycomponent);
        } else {
            TextBox textbox = new TextBox();
            textbox.setName((new StringBuilder()).append("CustActVar").append(s).append("CustActVar").toString());
            textbox.setLabel(s);
            textbox.setId((new StringBuilder()).append("CustActVar").append(s).append("CustActVar").toString());
            textbox.setValue(s1);
            textbox.setReadOnly(flag);
            textbox.setWidth(i);
            textbox.setMaxLength(150);
            textbox.setRenderLabel(false);
            guicomponentarray.addGUIComponent(textbox);
            TextDisplayComponent textdisplaycomponent1 = new TextDisplayComponent("");
            textdisplaycomponent1.setValue("</td></tr>");
            textdisplaycomponent1.setCheckXSS(false);
            guicomponentarray.addGUIComponent(textdisplaycomponent1);
        }
    }

    private void displayWTObjectAssignables(String name, String displayName, boolean isReadonly, boolean isRequired,
            GUIComponentArray guiComponentArray) throws WTException {
        WfActivity wfactivity = wfTaskProcessor.getActivity();
        ProcessData processdata = wfactivity.getContext();
        if (workItem.getContext() == null || wfTaskProcessor.getWorkItem().getContext() == null)
            processdata = wfactivity.getContext();
        else processdata = workItem.getContext();
        WfVariable var = processdata.getVariable(name);
        Class varClass = var.getVariableClass();
        String varTypeName = var.getTypeName();
        if (WTPrincipal.class.isAssignableFrom(varClass)) {
            if (isReadonly) {
                String s3 = processdata.getValue(name) != null ? WTUser.class.isAssignableFrom(varClass) ? ((WTUser) processdata
                        .getValue(name)).getFullName()
                        : ((WTPrincipal) processdata.getValue(name))
                                .getName()
                        : "&nbsp;";
                displayFormatting(displayName, guiComponentArray);
                TextDisplayComponent textdisplaycomponent = new TextDisplayComponent("");
                textdisplaycomponent.setValue(s3);
                guiComponentArray.addGUIComponent(textdisplaycomponent);
                textdisplaycomponent = new TextDisplayComponent("");
                textdisplaycomponent.setValue("</td></tr>");
                textdisplaycomponent.setCheckXSS(false);
                guiComponentArray.addGUIComponent(textdisplaycomponent);
            } else {
                principalSelector(varTypeName, (WTPrincipal) processdata.getValue(name), name, displayName,
                        guiComponentArray);
            }
        } else if (varTypeName.equals("wt.team.Team")) {
            if (isReadonly) {
                String s4 = processdata.getValue(name) != null ? ((Team) processdata.getValue(name)).getName() : null;
                displayFormatting(displayName, guiComponentArray);
                TextDisplayComponent textdisplaycomponent1 = new TextDisplayComponent("");
                textdisplaycomponent1.setValue(s4);
                guiComponentArray.addGUIComponent(textdisplaycomponent1);
                textdisplaycomponent1 = new TextDisplayComponent("");
                textdisplaycomponent1.setValue("</td></tr>");
                textdisplaycomponent1.setCheckXSS(false);
                guiComponentArray.addGUIComponent(textdisplaycomponent1);
            } else {
                String s5 = null;
                if (processdata.getValue(name) != null) {
                    TeamReference teamreference = TeamReference.newTeamReference((Team) processdata.getValue(name));
                    s5 = teamreference.getIdentity();
                }
                Vector vector = TeamHelper.service.findTeams();
                ReferenceFactory referencefactory = new ReferenceFactory();
                Vector<String> teamRefVec = new Vector<String>(vector.size());
                Vector<String> teamIdentityVec = new Vector<String>(vector.size());
                boolean flag4 = !isRequired;
                for (int k = 0; k < vector.size(); k++) {
                    TeamReference teamRef = (TeamReference) vector.elementAt(k);
                    String teamRefOid = referencefactory.getReferenceString(teamRef);
                    teamRefVec.addElement(teamRefOid);
                    String teamIdentity = teamRef.getIdentity();
                    teamIdentityVec.addElement(teamIdentity);
                    if (s5 == null || !s5.equals(teamIdentity))
                        continue;
                    int i = k;
                    if (flag4)
                        i++;
                }

                ArrayList<String> arraylist = new ArrayList<String>();
                arraylist.add("");
                arraylist.addAll(teamIdentityVec);
                ArrayList<String> arraylist1 = new ArrayList<String>();
                arraylist1.add("");
                arraylist1.addAll(teamRefVec);
                ComboBox combobox = new ComboBox(arraylist1, arraylist, new ArrayList());
                combobox.setName((new StringBuilder()).append("CustActVar").append(name).append("CustActVar")
                        .toString());
                String s13 = processdata.getValue(name) != null ? ((Team) processdata.getValue(name)).getName() : null;
                if (s13 != null)
                    combobox.setSelected(s13);
                else combobox.setSelected("");
                displayFormatting(displayName, guiComponentArray);
                guiComponentArray.addGUIComponent(combobox);
            }
        } else if (varTypeName.equals("wt.team.TeamTemplate")) {
            if (isReadonly) {
                String s6 = processdata.getValue(name) != null ? ((TeamTemplate) processdata.getValue(name)).getName()
                        : null;
                displayFormatting(displayName, guiComponentArray);
                TextDisplayComponent textdisplaycomponent2 = new TextDisplayComponent("");
                textdisplaycomponent2.setValue(s6);
                guiComponentArray.addGUIComponent(textdisplaycomponent2);
                textdisplaycomponent2 = new TextDisplayComponent("");
                textdisplaycomponent2.setValue("</td></tr>");
                textdisplaycomponent2.setCheckXSS(false);
                guiComponentArray.addGUIComponent(textdisplaycomponent2);
            } else {
                String s7 = null;
                if (processdata.getValue(name) != null) {
                    TeamTemplateReference teamtemplatereference = TeamTemplateReference
                            .newTeamTemplateReference((TeamTemplate) processdata.getValue(name));
                    s7 = teamtemplatereference.getIdentity();
                }
                WTContainerRef wtcontainerref = null;
                Object obj = wfTaskProcessor.getContextObj();
                if (obj instanceof WTContained)
                    wtcontainerref = ((WTContained) obj).getContainerReference();
                Vector vector1 = TeamHelper.service.findTeamTemplates(wtcontainerref);
                ReferenceFactory referencefactory1 = new ReferenceFactory();
                Vector<String> vector4 = new Vector<String>(vector1.size());
                Vector<String> vector5 = new Vector<String>(vector1.size());
                boolean flag5 = !isRequired;
                for (int l = 0; l < vector1.size(); l++) {
                    TeamTemplateReference teamtemplatereference1 = (TeamTemplateReference) vector1.elementAt(l);
                    String s12 = referencefactory1.getReferenceString(teamtemplatereference1);
                    vector4.addElement(s12);
                    String s11 = teamtemplatereference1.getIdentity();
                    vector5.addElement(s11);
                    if (s7 == null || !s7.equals(s11))
                        continue;
                    int j = l;
                    if (flag5)
                        j++;
                }

                ArrayList<String> arraylist2 = new ArrayList<String>();
                arraylist2.add("");
                arraylist2.addAll(vector5);
                ArrayList<String> arraylist3 = new ArrayList<String>();
                arraylist3.add("");
                arraylist3.addAll(vector4);
                ComboBox combobox1 = new ComboBox(arraylist3, arraylist2, new ArrayList());
                combobox1.setName((new StringBuilder()).append("CustActVar").append(name).append("CustActVar")
                        .toString());
                String s14 = processdata.getValue(name) != null ? ((TeamTemplate) processdata.getValue(name)).getName()
                        : null;
                if (s14 != null)
                    combobox1.setSelected(s14);
                else combobox1.setSelected("");
                displayFormatting(displayName, guiComponentArray);
                guiComponentArray.addGUIComponent(combobox1);
            }
        } else {
            displayFormatting(displayName, guiComponentArray);
            String s8 = WfHtmlFormat.createObjectLink((WTObject) processdata.getValue(name), null, locale);
            TextDisplayComponent textdisplaycomponent3 = new TextDisplayComponent("");
            textdisplaycomponent3.setValue(s8);
            textdisplaycomponent3.setCheckXSS(false);
            guiComponentArray.addGUIComponent(textdisplaycomponent3);
            textdisplaycomponent3 = new TextDisplayComponent("");
            textdisplaycomponent3.setValue("</td></tr>");
            textdisplaycomponent3.setCheckXSS(false);
            guiComponentArray.addGUIComponent(textdisplaycomponent3);
        }
    }

    public void enumeratedTypeSelector(Class class1, EnumeratedType enumeratedtype, String s, String s1,
            Locale locale1, GUIComponentArray guicomponentarray) {
        Vector<String> vector = new Vector<String>();
        Vector<String> vector1 = new Vector<String>();
        String s2 = getSimpleName(class1);
        try {
            Method method = null;
            method = class1.getMethod((new StringBuilder()).append("get").append(s2).append("Set").toString(),
                    (Class[]) null);
            Object obj = method.invoke((Class[]) null, (Object[]) null);
            EnumeratedType aenumeratedtype[] = (EnumeratedType[]) (EnumeratedType[]) obj;
            for (int j = 0; j < aenumeratedtype.length; j++)
                if (aenumeratedtype[j].isSelectable()) {
                    vector.addElement(aenumeratedtype[j].toString());
                    vector1.addElement(aenumeratedtype[j].getDisplay(locale1));
                }

        } catch (InvocationTargetException invocationtargetexception) {
            invocationtargetexception.printStackTrace();
        } catch (NoSuchMethodException nosuchmethodexception) {
            nosuchmethodexception.printStackTrace();
        } catch (IllegalAccessException illegalaccessexception) {
            illegalaccessexception.printStackTrace();
        }
        ArrayList<String> arraylist = new ArrayList<String>();
        arraylist.add("");
        arraylist.addAll(vector1);
        ArrayList<String> arraylist1 = new ArrayList<String>();
        arraylist1.add("");
        arraylist1.addAll(vector);
        ComboBox combobox = new ComboBox(arraylist1, arraylist, new ArrayList());
        combobox.setName((new StringBuilder()).append("CustActVar").append(s).append("CustActVar").toString());
        if (enumeratedtype != null)
            combobox.setSelected(enumeratedtype.toString());
        else combobox.setSelected("");
        displayFormatting(s1, guicomponentarray);
        guicomponentarray.addGUIComponent(combobox);
    }

    public void principalSelector(String s, WTPrincipal wtprincipal, String s1, String s2,
            GUIComponentArray guicomponentarray) throws WTException {
        try {
            Vector<String> displayUsersVec = new Vector<String>();
            Vector<String> usersVec = new Vector<String>();
            if (s.equals("wt.org.WTPrincipal") || s.equals("wt.org.WTUser")) {
                Vector savedUsersVec = (Vector) MethodContext.getContext().get(WF_USERS);
                Vector savedDisplayUsersVec = (Vector) MethodContext.getContext().get(WF_DISPLAY_USERS);
                if (savedUsersVec == null || savedDisplayUsersVec == null) {
                    WTUser wtuser;
                    for (SortedEnumByPrincipal sortedenumbyprincipal = new SortedEnumByPrincipal(
                            OrganizationServicesMgr.allUsers(), false, 1); sortedenumbyprincipal.hasMoreElements(); usersVec
                            .addElement(SortedEnumByPrincipal.getLastNameFirstName(wtuser))) {
                        wtuser = (WTUser) sortedenumbyprincipal.nextElement();
                        displayUsersVec.addElement(wtuser.getName());
                    }

                    MethodContext.getContext().put(WF_USERS, usersVec);
                    MethodContext.getContext().put(WF_DISPLAY_USERS, displayUsersVec);
                } else {
                    usersVec = savedUsersVec;
                    displayUsersVec = savedDisplayUsersVec;
                }
            }
            if (s.equals("wt.org.WTPrincipal") || s.equals("wt.org.WTGroup")) {
                Vector vector3 = (Vector) MethodContext.getContext().get(WF_GROUPS);
                Vector vector5 = (Vector) MethodContext.getContext().get(WF_DISPLAY_GROUPS);
                if (vector3 == null || vector5 == null) {
                    ArrayList arraylist2 = null;
                    WorkItem workitem = wfTaskProcessor.getWorkItem();
                    WfActivity wfactivity = (WfActivity) workitem.getSource().getObject();
                    arraylist2 = getContextProviders(wfactivity.getContainerReference());
                    PrincipalSpec principalspec = new PrincipalSpec(wfactivity.getContainerReference(), WTGroup.class);
                    principalspec.setInternalGroupSet("orgs");
                    DirectoryContextProvider adirectorycontextprovider[] = (DirectoryContextProvider[]) (DirectoryContextProvider[]) arraylist2
                            .toArray(new DirectoryContextProvider[arraylist2.size()]);
                    for (int i = 0; i < adirectorycontextprovider.length; i++)
                        adirectorycontextprovider[i].setInternalGroupsSearchCriteria(principalspec);

                    Enumeration enumeration = OrganizationServicesHelper.manager.queryPrincipals(WTGroup.class,
                            "name=*", adirectorycontextprovider);
                    do {
                        if (!enumeration.hasMoreElements())
                            break;
                        Object obj = enumeration.nextElement();
                        if (!obj.getClass().equals(WTOrganization.class)) {
                            WTGroup wtgroup = (WTGroup) obj;
                            displayUsersVec.addElement(wtgroup.getName());
                            usersVec.addElement(wtgroup.getName());
                        }
                    } while (true);
                    MethodContext.getContext().put(WF_GROUPS, usersVec);
                    MethodContext.getContext().put(WF_DISPLAY_GROUPS, displayUsersVec);
                } else {
                    usersVec = vector3;
                    displayUsersVec = vector5;
                }
            }
            ArrayList<String> arraylist = new ArrayList<String>();
            arraylist.add("");
            arraylist.addAll(usersVec);
            ArrayList<String> arraylist1 = new ArrayList<String>();
            arraylist1.add("");
            arraylist1.addAll(displayUsersVec);
            ComboBox combobox = new ComboBox(arraylist1, arraylist, new ArrayList());
            combobox.setName((new StringBuilder()).append("CustActVar").append(s1).append("CustActVar").toString());
            if (wtprincipal != null)
                combobox.setSelected(wtprincipal.getName());
            else combobox.setSelected("");
            displayFormatting(s2, guicomponentarray);
            guicomponentarray.addGUIComponent(combobox);
        } catch (WTException wtexception) {
            wtexception.printStackTrace();
        } catch (WTPropertyVetoException wtpropertyvetoexception) {
            wtpropertyvetoexception.printStackTrace();
        }
    }

    protected String getSimpleName(Class class1) {
        char ac[] = class1.getName().toCharArray();
        String s = null;
        int i = ac.length - 1;
        do {
            if (i <= 0)
                break;
            if (ac[i] == '.') {
                s = class1.getName().substring(i + 1);
                break;
            }
            i--;
        } while (true);
        return s;
    }

    private final boolean displayCustomVariable(WfVariableInfo wfvariableinfo, String s) {
        return s != null
                && wfvariableinfo != null
                && wfvariableinfo.isVisible()
                && (s.equals(ALL_ACTIVITY_VARIABLES) && !wfvariableinfo.getName().equals("primaryBusinessObject")
                        && !wfvariableinfo.getName().equals("instructions")
                        && !wfvariableinfo.getName().equals(SPECIAL_INSTRUCTIONS)
                        && !wfvariableinfo.getName().equals("comments") || s.equals(wfvariableinfo.getName()));
    }

    private ArrayList getContextProviders(WTContainerRef wtcontainerref) throws WTException, WTPropertyVetoException {
        try {
            ArrayList<DirectoryContextProvider> arraylist = null;
            PrincipalSpec principalspec = new PrincipalSpec(wtcontainerref, WTGroup.class);
            principalspec.setIncludeAllServices(true);
            DirectoryContextProvider adirectorycontextprovider[] = WTContainerHelper.service
                    .getPublicContextProviders(principalspec);
            arraylist = new ArrayList<DirectoryContextProvider>(adirectorycontextprovider.length);
            for (int i = 0; i < adirectorycontextprovider.length; i++)
                arraylist.add(adirectorycontextprovider[i]);

            return arraylist;
        } catch (Exception exception) {
            if (exception instanceof WTException)
                throw (WTException) exception;
            else throw new WTException(exception);
        }
    }

    private int[] getCustomWidthHeight(String s) {
        int ai[] = { -1, -1 };
        try {
            if (s != null) {
                s = s.replace('}', ' ').trim();
                String as[] = { s };
                if (s.contains(";"))
                    as = s.split(";");
                String as1[] = as;
                int i = as1.length;
                for (int j = 0; j < i; j++) {
                    String s1 = as1[j];
                    if (s1.contains(":")) {
                        String as2[] = s1.split(":");
                        if (as2.length == 2 && as2[0] != null && as2[1] != null) {
                            if (as2[0].toLowerCase().trim().equals("height"))
                                ai[1] = Integer.valueOf(as2[1].trim()).intValue();
                            if (as2[0].toLowerCase().trim().equals("width"))
                                ai[0] = Integer.valueOf(as2[1].trim()).intValue();
                        }
                    }
                }

            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return ai;
    }
}