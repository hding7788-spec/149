package ext.casc.analysisActivity.dataUtility;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.*;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.ases.envelope.ProcessEnvelope;
import ext.ases.envelope.ProcessEnvelopeUtil;
import ext.casc.util.NmTableGUIComponent;
import wt.doc.WTDocument;
import wt.fc.ReferenceFactory;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.util.WTProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimeZone;

public class AnalysisDataUtility extends AbstractDataUtility {

    @Override
    public Object getDataValue(String componentId, Object object, ModelContext context) throws WTException {
        NmCommandBean nmcommandbean = context.getNmCommandBean();
        ReferenceFactory rf = new ReferenceFactory();
        if("plNumber".endsWith(componentId)) {
            if(object instanceof ProcessEnvelope) {
                ProcessEnvelope pe = (ProcessEnvelope) object;
                ArrayList members = ProcessEnvelopeUtil.getAllMembers(pe);
                for(Object obj : members) {
                    if(obj instanceof WTDocument) {
                        WTDocument doc = (WTDocument) obj;
                        TypeIdentifier identifier = TypedUtility.getTypeIdentifier(doc);
                        String typeName = identifier.getTypename();
                        if(typeName.contains("casc.sast.149.TECHNOTICE_DOC")) {
                            GUIComponentArray main = new GUIComponentArray();
                            UrlDisplayComponent udc = new UrlDisplayComponent(doc.getNumber());
                            udc.setLabelForTheLink(doc.getNumber());
                            try {
                                String oid = rf.getReferenceString(doc);
                                String urlBase = WTProperties.getLocalProperties().getProperty("java.rmi.server.hostname");
                                String webAPP = WTProperties.getLocalProperties().getProperty("wt.webapp.name");
                                String url = "http://" + urlBase + "/" + webAPP + "/app/#ptc1/tcomp/infoPage?oid=" + oid + "&u8=1";
                                udc.setLink(url);
                            } catch(IOException e) {
                                e.printStackTrace();
                            }
                            udc.setTarget("_blank");
                            main.addGUIComponent(udc);
                            return main;
                        }
                    }
                }
            }
        }
        Object ret = getDataValues(componentId, nmcommandbean, object, context);
        return ret;
    }

    private Object getDataValues(String componentId, NmCommandBean nmcommandbean, Object object, ModelContext context) throws WTException {
        GUIComponentArray main = new GUIComponentArray();
        if(object instanceof HashMap) {
            HashMap map = (HashMap) object;
            NmTableGUIComponent gui = new NmTableGUIComponent((String) map.get(componentId));
            main.addGUIComponent(gui);
        }
        return main;
    }

    private TextBox getTextBox(String componentId, String value, boolean isRequired, boolean isEditable) {
        TextBox textbox = new TextBox();
        textbox.setRequired(isRequired);
        textbox.setInputType("text");
        textbox.setName(componentId);
        textbox.setValue(value);
        textbox.setId(componentId);
        textbox.setWidth(60);
        textbox.setMaxLength(255);
        textbox.setEditable(isEditable);
        return textbox;
    }

    private TextArea getTextArea(String componentId, String value, boolean isRequired, boolean isEditable) {
        TextArea textArea = new TextArea();
        textArea.setId(componentId);
        textArea.setName(componentId);
        textArea.setEditable(isEditable);
        textArea.setRequired(isRequired);
        return textArea;
    }

    private DateInputComponent getDateInputComponent(String componentId, String value, boolean isRequired,
                                                     boolean isEditable) {
        DateInputComponent date = new DateInputComponent(componentId, DateInputComponent.ValueType.DATE_ONLY);
        date.setTimeZone(TimeZone.getDefault());
        date.setId(componentId);
        date.setName(componentId);
        date.setColumnName(componentId);
        date.setReadOnly(isEditable);
        return date;
    }

    private ComboBox getComboBox(String componentId, ArrayList<String> valueList, boolean isRequired, boolean isEditable) {
        ComboBox comboBox = new ComboBox();
        comboBox.setRequired(isRequired);
        comboBox.setName(componentId);
        comboBox.setValues(valueList);
        comboBox.setInternalValues(valueList);
        comboBox.setId(componentId);
        comboBox.setEditable(isEditable);
        return comboBox;
    }

    private GUIComponentArray getGUIComponentArrayInput(String componentId, String value, boolean isRequired, boolean isEditable) {
        GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
        guicomponentarrayMain.setValueHidden(false);
        guicomponentarrayMain.setRequired(isRequired);
        String values = value;
        NmTableGUIComponent gui = new NmTableGUIComponent(values);
        gui.setRequired(isRequired);
        guicomponentarrayMain.addGUIComponent(gui);
        return guicomponentarrayMain;
    }

    private Object getInput(String paramString, Object obj) {
        NmOid oid = (NmOid) obj;
        StringInputComponent strInput = new StringInputComponent();
        strInput.setEditable(true);
        strInput.setName(paramString + "_" + oid.toString());
        HashMap map = oid.getAdditionalInfo();
        if(map != null) {
            strInput.setValue((String) map.get(paramString));
        }
        return strInput;
    }

}