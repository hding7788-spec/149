package ext.casc.processPlan.datautility;

import com.glaway.mpm.util.WTPartUtil;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.components.rendering.guicomponents.TextDisplayComponent;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.common.PartCommonHelper;
import ext.casc.dfmRule.util.GeneralUtil;
import ext.casc.mpm.GyCsServerHelper;
import ext.casc.mpm.process.GLProcessParamDefinition;
import ext.casc.mpm.process.GLProcessParamValues;
import ext.casc.processPlan.Constants;
import ext.casc.util.NmTableGUIComponent;
import ext.casc.util.Tools;
import ext.casc.version.VersionCommonHelper;
import org.apache.commons.lang3.StringUtils;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.WTObject;
import wt.httpgw.GatewayServletHelper;
import wt.httpgw.URLFactory;
import wt.part.WTPart;
import wt.util.WTException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MPMProcessPlanDatautility extends AbstractDataUtility {

    public MPMProcessPlanDatautility() {
    }

    public Object getDataValue(String componentId, Object obj, ModelContext modelcontext) throws WTException {
        URLFactory factory = new URLFactory();
        NmCommandBean nmcommandbean = modelcontext.getNmCommandBean();
        Object ret = "";
        if ("enumValues".equals(componentId)) {
            return processEnumValuesUI(componentId, nmcommandbean, factory, (GLProcessParamDefinition) obj);
        }
        if ("processTemplate".equals(componentId)) {
            return processTemplateUI(componentId, nmcommandbean, factory, (WTPart) obj);
        }
        if (componentId.startsWith(Constants.PRE)) {
            return setParameterValues(componentId, nmcommandbean, factory, (WTPart) obj);
        }
        if (componentId.endsWith("processLink")) {
    		GUIComponentArray guicomponentarrayMain = new GUIComponentArray();

        	WTPart part = (WTPart)obj;
            List<WTDocument> list = PartCommonHelper.getLatestTechnicsDocumentByPart(part,"PROCESS_PLAN");
        	if(list.isEmpty()){
        		return "";
        	}else{
        		URLFactory uf = new URLFactory();
        		StringBuilder sb = new StringBuilder("");
        		for(WTDocument doc:list){
        			HashMap m = new HashMap();
    				m.put("oid", "OR:wt.doc.WTDocument:"+doc.getPersistInfo().getObjectIdentifier().getId());
    				m.put("action", "ObjProps");
    				String urlInfo = GatewayServletHelper.buildAuthenticatedHREF(uf, "wt.enterprise.URLProcessor", "URLTemplateAction",m, true);
    				sb.append("<a href='javascript:void(0)' onclick='window.open(\""+urlInfo+"\")','_blank'>" + doc.getName() + "</a>");

    				sb.append(";");
        		}

                NmTableGUIComponent text = new NmTableGUIComponent(sb.toString());
				guicomponentarrayMain.addGUIComponent(text);
        		return guicomponentarrayMain;
        	}
        }


        return ret;
    }

    private GUIComponentArray processEnumValuesUI(String componentId, NmCommandBean nmcommandbean, URLFactory factory, GLProcessParamDefinition obj) {

        GUIComponentArray guicomponentarray = new GUIComponentArray();
        StringBuffer sbstr = new StringBuffer();
        String value = obj.getEnumValues();
        if(Tools.isNull(value)) value = "";
        String id = Constants.PRE + "_" +obj.getTemplateId()+"_"+ obj.getGyParamNumber();
        sbstr.append("<input type='text' id='").append(id).append("' name='").append(id).append("'").append(" value='").append(value).append("' size='300'/>");
        TextDisplayComponent text = new TextDisplayComponent(componentId);
        text.setValue(sbstr.toString());
        text.setCheckXSS(false);
        text.setRequired(false);
        guicomponentarray.addGUIComponent(text);

        return guicomponentarray;
    }

    public static GUIComponentArray setParameterValues(String componentId, NmCommandBean nmcommandbean, URLFactory factory, WTPart part) {
        StringBuilder  paramValue = new StringBuilder("");

        GUIComponentArray guicomponentarray = new GUIComponentArray();
        StringBuffer sbstr = new StringBuffer();
        String templateIdAndParamNumber = componentId.substring(4);
        String templateId = templateIdAndParamNumber.substring(0,templateIdAndParamNumber.indexOf("_"));
        String paramNumber =  templateIdAndParamNumber.substring(templateIdAndParamNumber.indexOf("_")+1);
        GLProcessParamDefinition glProcessParamDefinition =  GyCsServerHelper.getGLProcessParamDefinition(templateId,paramNumber);

        if(!Tools.isTrimNull(glProcessParamDefinition.getEnumValues())){
            List<GLProcessParamValues> glProcessParamValues = ext.casc.mpm.process.ProcessUtil.queryGLProcessParamValues(part.getNumber(),  paramNumber);
            String realValue = "";
            if (!glProcessParamValues.isEmpty()) {
                GLProcessParamValues paramValues = glProcessParamValues.get(glProcessParamValues.size()-1);
                realValue =paramValues.getParamValue();
            }
            String id = componentId + "_" + part.getNumber();
            sbstr.append("<select readonly=\"false\" name=\"" + id + "\" id=\"" + id + "\" style=\"width:50px; height:20px; display:block\" >");

            String enumValues = glProcessParamDefinition.getEnumValues();
            if(!Tools.isNull(enumValues)){
               String[] ss =  enumValues.split("\\|");
               for(String s:ss){
            	   if(s.equals(realValue)){
            		   sbstr.append("<option selected=\"selected\" value=\"" + realValue + "\">");
            	   }else{
                       sbstr.append("<option value=\"" + s + "\">");

            	   }
                   sbstr.append(s);
                   sbstr.append("</option>");
               }
            }
            sbstr.append("</select></td>");
            NmTableGUIComponent gui = new NmTableGUIComponent(sbstr.toString());

            guicomponentarray.addGUIComponent(gui);
        }else{
            List<GLProcessParamValues> glProcessParamValues = ext.casc.mpm.process.ProcessUtil.queryGLProcessParamValues(part.getNumber(), paramNumber);
            if (!glProcessParamValues.isEmpty()) {
                GLProcessParamValues paramValues = glProcessParamValues.get(glProcessParamValues.size()-1);
                if (paramValues != null) {
                    if(!StringUtils.isEmpty(paramValues.getParamValue())){
                        paramValue.append(paramValues.getParamValue());
                    }
                   /* if(!StringUtils.isEmpty(paramValues.getParamUnit())){
                        paramValue.append(paramValues.getParamUnit());
                    }*/
                    if(!StringUtils.isEmpty(paramValues.getGongChengZhi())){
                        paramValue.append(";");
                        paramValue.append("公称值:");
                        paramValue.append(paramValues.getGongChengZhi());
                    }
                    if(!StringUtils.isEmpty(paramValues.getShangPianCha())){
                        paramValue.append(";");
                        paramValue.append("上偏差:");
                        paramValue.append(paramValues.getShangPianCha());
                    }
                    if(!StringUtils.isEmpty(paramValues.getXiaPianCha())){
                        paramValue.append(";");
                        paramValue.append("下偏差:");
                        paramValue.append(paramValues.getXiaPianCha());
                    }
                    if(!StringUtils.isEmpty(paramValues.getFuHao())){
                        paramValue.append(";");
                        paramValue.append("符号:");
                        paramValue.append(paramValues.getFuHao());
                    }
                    if(!StringUtils.isEmpty(paramValues.getJiZhun1())){
                        paramValue.append(";");
                        paramValue.append("基准1:");
                        paramValue.append(paramValues.getJiZhun1());
                    }
                    if(!StringUtils.isEmpty(paramValues.getJiZhun2())){
                        paramValue.append(";");
                        paramValue.append("基准2:");
                        paramValue.append(paramValues.getJiZhun2());
                    }
                    if(!StringUtils.isEmpty(paramValues.getJiZhun3())){
                        paramValue.append(";");
                        paramValue.append("基准3:");
                        paramValue.append(paramValues.getJiZhun3());
                    }

                }
            }

            String id = componentId + "_" +  part.getNumber();
            sbstr.append("<input type='text' id='").append(id).append("' name='").append(id).append("'").append(" value='").append(paramValue).append("' size='30' ondblclick=\"javascript:setParamValues(this,'"+templateId+"','"+paramNumber+"','"+ part.getNumber()+"');\"/>");
            TextDisplayComponent text = new TextDisplayComponent(componentId);
            text.setValue(sbstr.toString());
            text.setCheckXSS(false);
            text.setRequired(false);
            guicomponentarray.addGUIComponent(text);
        }


        return guicomponentarray;
    }



    /**
     * 根据部件属性自动找到其对应的 参数化工艺模板
     *
     * @param nmcommandbean
     * @param factory
     * @param part
     * @return
     */
    private static GUIComponentArray processTemplateUI(String componentId, NmCommandBean nmcommandbean, URLFactory factory, WTPart part) {
        GUIComponentArray guicomponentarray = new GUIComponentArray();
        String templateName = "";
        String templateOid = "";
        List<GLProcessParamValues> glProcessParamValues = ext.casc.mpm.process.ProcessUtil.queryGLProcessParamValues(part.getNumber(),part.getVersionIdentifier().getValue()+"."+part.getIterationIdentifier().getValue(), "", "");
        if(glProcessParamValues.isEmpty()){

            try {
                WTPart dpart = WTPartUtil.getLatestPartByNumberAndView(part, com.glaway.mpm.constants.Constants.design);
                EPMDocument epm = PartCommonHelper.getEPMDocumentByPart(dpart);
                if(epm!=null){
                	Map<String, String> params = new HashMap<String, String>();
                    params.put("CADNAME", epm.getNumber());
                    params.put("CADVERSION", VersionCommonHelper.getVersion(epm));
                    glProcessParamValues = GyCsServerHelper.queryProcessParamInstances(params);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!glProcessParamValues.isEmpty()) {
            GLProcessParamValues glProcessParamValuesi = glProcessParamValues.get(0);
            WTObject obj  =GeneralUtil.getObjectByOid(glProcessParamValuesi.getTemplateId());
            if(obj!=null){
                WTDocument doc = (WTDocument)obj;
                templateName = doc.getName();
                templateOid = "OR:wt.doc.WTDocument:"+doc.getPersistInfo().getObjectIdentifier().getId();
            }

        }

        String base = factory.getHREF("/app/#ptc1/netmarkets/jsp/ext/casc/processPlan/searchProcessTemplate.jsp?partNumber=" + part.getNumber());

        StringBuffer sbstr = new StringBuffer();
        sbstr.append("<input type = 'hidden' id='processTemplate_" + part.getNumber() + "_oid' name='processTemplateOid' value='" + templateOid +"'/>");
        sbstr.append("<input type = 'text' id='processTemplate_" + part.getNumber() + "' name='processTemplate' value='" + templateName + "' disabled='true' size='30''/>");
        sbstr.append("<a href='javascript:void(0)' onclick='window.open(\"" + base + "\",\"_blank\",\"location=no,resizable=yes,top=0,toolbar=no,menubar=no,height=500\")'>");
        sbstr.append("   <img id='DesignDocIMG' hspace='0' vspace='0' border='0' align='top' title='查找...' alt='查找...' src='netmarkets/images/search.gif'>");
        sbstr.append(" </a>");

        TextDisplayComponent text = new TextDisplayComponent(componentId);
        text.setValue(sbstr.toString());
        text.setCheckXSS(false);
        text.setRequired(false);

        guicomponentarray.addGUIComponent(text);
        return guicomponentarray;
    }


}
