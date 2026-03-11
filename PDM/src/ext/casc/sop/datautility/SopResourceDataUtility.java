package ext.casc.sop.datautility;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.process.util.ProcessUtil;
import ext.casc.sop.constants.SopConstants;
import ext.casc.sop.util.ComponentsUtil;
import ext.casc.sop.util.SopUtil;
import wt.util.WTException;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class SopResourceDataUtility extends AbstractDataUtility {

    @Override
    public Object getDataValue(String componentId, Object o, ModelContext modelContext) throws WTException {
        NmCommandBean nmcommandbean = modelContext.getNmCommandBean();
        return createMPMResourceFields(componentId, nmcommandbean);
    }

    private Object createMPMResourceFields(String componentId, NmCommandBean nmcommandbean) throws WTException {
        Object obj = null;
        if (componentId.equals(SopConstants.SOP_ATTR_NUMBER)) {
            //编号
//        	String sopZYType = nmcommandbean.getTextParameter("mpmResourceType");
//            obj = ComponentsUtil.getTextBox(componentId,sopZYType+SopUtil.getSopZYSeqNumber(1,sopZYType), true, false, 60, 255);
            obj = ComponentsUtil.getTextBox(componentId,"已生成", true, false, 60, 255);
        } else if(componentId.equals(SopConstants.SOP_ATTR_NAME)){
        	//名称
        	obj = ComponentsUtil.getTextBox(componentId, "", true, true, 60, 255);
        } else if (componentId.equals(SopConstants.SOP_IBA_SPECIALIZEDTYPE)) {
            //专业类别
            ArrayList<String> list = ProcessUtil.getSpecializedType();
            list.add(0, "");
            ComboBox comboBox = ComponentsUtil.getComboBox("SpecializedType", list, true, true);
            comboBox.addJsAction("onchange", "selectSpecializedType()");
            return comboBox;
        } else if (componentId.equals(SopConstants.SOP_IBA_PARAMETERS)) {
            //参数项目
            ArrayList<String> list = new ArrayList<String>();
            list.add(0, "");
            obj = ComponentsUtil.getComboBox(componentId, list, true, true);
        } else if (componentId.equals(SopConstants.SOP_IBA_GONGXUJIANHAO)) {
            //工序简号
            obj = ComponentsUtil.getTextBox(componentId, "", true, true, 60, 255);
        } else if (componentId.equals(SopConstants.SOP_IBA_ZZCJ)) {
            //主制车间
            ArrayList<String> list = null;
            try {
                list = ProcessUtil.getGongXuCheJian();
                list.add(0, "");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            obj = ComponentsUtil.getComboBox(componentId, list, false, true);
        } else if (componentId.equals(SopConstants.SOP_IBA_PROCEDUCENAME)) {
            //工序名称
            ArrayList<String> list = new ArrayList<String>();
            list.add(0, "");
            obj = ComponentsUtil.getComboBox(componentId, list, true, true);
        } else if (componentId.equals(SopConstants.SOP_IBA_MATERIALCATEGORY)) {
            //物资类别
            ArrayList<String> list = new ArrayList<String>();
            list.add(0, "");
            obj = ComponentsUtil.getComboBox(componentId, list, true, true);
        } else if (componentId.equals(SopConstants.SOP_ATTR_REMARK)) {
            //备注
            obj = ComponentsUtil.getTextArea(componentId, "", false, true);
        } else if (componentId.equals(SopConstants.SOP_IBA_PROFESSIONALCODE)) {
            //专业代号
            obj = ComponentsUtil.getTextBox(componentId, "", true, true, 60, 255);
        } else if (componentId.equals(SopConstants.SOP_IBA_PARAMETERSNAME)) {
        	ArrayList<String> list = new ArrayList<String>();
			list.add(0, "");
            obj = ComponentsUtil.getComboBox(componentId, list, true, true);
        } else {
            obj = ComponentsUtil.getTextBox(componentId, "", false, true, 60, 255);
        }
        return obj;
    }
}
