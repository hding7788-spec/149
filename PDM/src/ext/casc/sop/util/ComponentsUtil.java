package ext.casc.sop.util;

import com.ptc.core.components.rendering.guicomponents.*;
import ext.casc.util.NmTableGUIComponent;

import java.util.ArrayList;
import java.util.TimeZone;

public class ComponentsUtil {

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

    public static TextBox getTextBox(String componentId, String value, boolean isRequired, boolean isEditable,int width, int maxLength) {
        TextBox textbox = new TextBox();
        textbox.setRequired(isRequired);
        textbox.setInputType("text");
        textbox.setName(componentId);
        textbox.setValue(value);
        textbox.setId(componentId);
        textbox.setWidth(width);
        textbox.setMaxLength(maxLength);
        textbox.setEditable(isEditable);
        return textbox;
    }

    public static TextArea getTextArea(String componentId, String value, boolean isRequired, boolean isEditable) {
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

    public static ComboBox getComboBox(String componentId, ArrayList<String> valueList, boolean isRequired, boolean isEditable) {
        ComboBox comboBox = new ComboBox();
        comboBox.setRequired(isRequired);
        comboBox.setName(componentId);
        comboBox.setValues(valueList);
        comboBox.setInternalValues(valueList);
        comboBox.setId(componentId);
        comboBox.setEditable(isEditable);
        return comboBox;
    }
}
