package ext.casc.dataUtility;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import ext.casc.integrate.util.BomUtil;
import ext.casc.util.IBAHelper;
import ext.casc.util.IBAUtility;
import ext.casc.util.NmTableGUIComponent;
import wt.fc.*;
import wt.httpgw.URLFactory;
import wt.part.WTPart;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PartDataUtility extends AbstractDataUtility {

    @Override
    public Object getDataValue(String componentId, Object object, ModelContext modelContext) throws WTException {
        GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
        String oid = "";
        Versioned version = null;
        String veroid = "";
        if(object instanceof Persistable) {
            oid = PersistenceHelper.getObjectIdentifier((Persistable) object).toString();
            QueryResult qr = VersionControlHelper.service.allVersionsFrom((Versioned) object);
            if(qr.hasMoreElements()) {
                version = (Versioned) qr.nextElement();
            }
            ReferenceFactory rf = new ReferenceFactory();
            veroid = rf.getReference(version).toString();
            veroid = veroid.replaceAll(">", ":");
        }
        if ("operations".equals(componentId)) {
        	String url = BomUtil.getCreoViewUrlRMI(object);
        	String value = "<a href='"+url+"'  target=\"_blank\">查看可视化</a>";
	    	NmTableGUIComponent gui = new NmTableGUIComponent(value);
	        guicomponentarrayMain.addGUIComponent(gui);
	        return guicomponentarrayMain;
        }
        else if ("cbKongZhiMeasures".equals(componentId)) {
            String  objectId = IBAHelper.getIBAStringValue((WTObject)object,"cbKongZhiMeasures");
            String value ="";
            if(objectId!=null &&!"".equals(objectId)&&!"null".equals(objectId)){
                String url = "http://10.125.192.68/plm/api/v2/qdp/public/fileHandle/"+objectId+"/download";
                value =  "<a href='"+url+"'  target=\"_blank\">下载文件</a>";
            }

            NmTableGUIComponent gui = new NmTableGUIComponent(value);
            guicomponentarrayMain.addGUIComponent(gui);
            return guicomponentarrayMain;
        }
//
        if ("partType".equals(componentId)) {
            String oldValue = "";
            if (object instanceof WTPart) {
                WTPart part = (WTPart) object;
                IBAUtility ibaUtility = new IBAUtility(part);
                String partType = ibaUtility.getIBAValue("MTYPE");
                if (partType != null) {
                    oldValue = partType;
                }
            }
            StringBuffer sb = new StringBuffer();
            List<String> testList = new ArrayList<String>();
            //自制件|标准件|元器件|外购件|带料委外件|不带料委外件|外配套件
            testList.add("自制件");
            testList.add("外购件");
            testList.add("标准件");
            testList.add("带料委外件");
            testList.add("外配套件");
            testList.add("不带料委外件");
            testList.add("元器件");
            sb.append("<select name=\"" + oid + "_select\" " + "id=\"" + veroid + "_select\" onChange=\"setPartsType(this)\">");
            for (int i = 0; i < testList.size(); i++) {
                String tempStr = testList.get(i);
                if(tempStr.equals(oldValue)){
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
        } else if ("processPath".equals(componentId)) {
            String oldValue = "";
            if (object instanceof WTPart) {
                WTPart part = (WTPart) object;
                IBAUtility ibaUtility = new IBAUtility(part);
                String path = ibaUtility.getIBAValue("ROUTING");
                if (path != null) {
                    oldValue = path;
                }
            }
            String value = "<input type=\"text\" value=\""+oldValue+"\" id=\"" + oid + "_processPath\" name=\"" + oid + "_processPath\">";
            NmTableGUIComponent gui = new NmTableGUIComponent(value);
            guicomponentarrayMain.addGUIComponent(gui);
        } else if ("oldPartTypeValue".equals(componentId)) {
            if (object instanceof WTPart) {
                WTPart part = (WTPart) object;
                IBAUtility ibaUtility = new IBAUtility(part);
                String partType = ibaUtility.getIBAValue("MTYPE");
                if (partType != null) {
                    return partType;
                } else {
                    return "";
                }
            }
        } else if ("oldProcessPath".equals(componentId)) {
            if (object instanceof WTPart) {
                WTPart part = (WTPart) object;
                IBAUtility ibaUtility = new IBAUtility(part);
                String route = ibaUtility.getIBAValue("ROUTING");
                if (route != null) {
                    return route;
                } else {
                    return "";
                }
            }
        } else if("gongshiNumber".equals(componentId)) {
            if(object instanceof Map) {
                Map map = (Map) object;
                String gongshiNumber = map.get("gongshiNumber").toString();
                String[] values = gongshiNumber.split("@");
                String num = values[0];
                String docOid = values[1];
                URLFactory factory = new URLFactory();
                String base = factory.getHREF("app/#ptc1/tcomp/infoPage?oid=");
                String value="<a href='javascript:void(0)' onclick='window.open(\""+base + docOid+"\",\"_blank\")'>"+num+"</a>";
                NmTableGUIComponent gui = new NmTableGUIComponent(value);
                guicomponentarrayMain.addGUIComponent(gui);
            }
        }

        return guicomponentarrayMain;
    }

}
