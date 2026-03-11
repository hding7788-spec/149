package ext.casc.workflow.tree;

import com.glaway.mpm.util.MPMProcessPlanUtil;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink;
import ext.casc.util.NmTableGUIComponent;
import org.apache.commons.lang3.StringUtils;
import wt.util.WTException;

import java.util.Map;

public class MPMOperationDataUtility extends AbstractDataUtility {

    @Override
    public Object getDataValue(String columnName, Object obj, ModelContext mc) throws WTException {
        GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
        guicomponentarrayMain.setValueHidden(false);

        if ("name".equals(columnName)) {
            MPMOperation operation = (MPMOperation) obj;
            MPMOperationUsageLink link = MPMProcessPlanUtil.getMPMOperationUsageLinkByMPMOperation(operation);
            String value = link != null ? link.getOperationLabel() + " " + operation.getName() : operation.getName();
            guicomponentarrayMain.addGUIComponent(new NmTableGUIComponent(value));
            return guicomponentarrayMain;
        }

        // 处理 zhunjie / danjian / danJianSheBeiGS
        if (obj instanceof Map) {
            Map<String, String> map = (Map<String, String>) obj;
            String oid = map.get("oid");
            String value = map.getOrDefault(columnName, "");

//            String htmlInput = generateInputHtml(oid, columnName, value);
            String htmlInput = generateInputHtml(columnName, map);
            guicomponentarrayMain.addGUIComponent(new NmTableGUIComponent(htmlInput));
        }

        return guicomponentarrayMain;
    }

    private String generateInputHtml(String field, Map<String, String> map) {

        String oid = map.get("oid");
        String value = map.getOrDefault(field, "");

        String fullId = "";
        if(StringUtils.equalsIgnoreCase(field, "danJianSheBeiGS")) {
            fullId = oid + "_" + map.get("stepName") + "_" + field;
        } else {
            fullId = oid + "_" + field;
        }

        if ("false".equals(value)) {
            return String.format("<input type=\"text\" readonly name=\"%s\" id=\"%s\" value='0' />", fullId, fullId);
        }

        return String.format(
                "<input type=\"text\" name=\"%s\" id=\"%s\" value=\"%s\" " +
                "oninput=\"value = value.match(/\\d+(\\.\\d{0,2})?/) ? value.match(/\\d+(\\.\\d{0,2})?/)[0] : ''\" />",
                fullId, fullId, value
        );
    }
}
