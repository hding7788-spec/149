package ext.casc.workflow.tree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.IconDelegate;
import wt.fc.IconDelegateFactory;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.httpgw.GatewayServletHelper;
import wt.httpgw.URLFactory;
import wt.inf.container.WTContainer;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.IconSelector;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.components.rendering.guicomponents.Label;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;

import ext.casc.util.AccessUtil;
import ext.casc.util.NmTableGUIComponent;


public class SignatureTreeDataUtility2 extends AbstractDataUtility{

	public Object getDataValue(String columnName, Object obj, ModelContext mc) throws WTException {
		String oid = PersistenceHelper.getObjectIdentifier((Persistable)obj).toString();
		String webPort = "";
        try {
            WTProperties props = WTProperties.getLocalProperties();
            webPort = props.getProperty("wt.webserver.port");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
	    
		/*
		 * 为了开权限
		 */
		//切换系统管理员
		WTUser currentuser = (WTUser)SessionHelper.manager.getPrincipal();
		WTUser admin = (WTUser)SessionHelper.manager.setAdministrator();
		try {
			AccessUtil.setObjectAccess((Persistable)obj, currentuser);
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}finally{
			WTUser user = (WTUser)SessionHelper.manager.setPrincipal(currentuser.getAuthenticationName());
		}
		GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
		if (columnName.equals("icontype")){
			WTPart part = null;
			WTDocument doc = null;
			EPMDocument epmdoc = null;
			WTChangeOrder2 co = null;
			MPMProcessPlan processPlan = null;
			if(obj instanceof WTPart){
				part = (WTPart)obj;
			}else if(obj instanceof WTDocument){
				doc = (WTDocument)obj;
			}else if(obj instanceof EPMDocument){
				epmdoc = (EPMDocument)obj;
			}else if(obj instanceof WTChangeOrder2){
				co = (WTChangeOrder2)obj;
			} else if (obj instanceof MPMProcessPlan) {
                processPlan = (MPMProcessPlan)obj;
            }
			
			String value = "";
			if(part != null){
				String partNumber = part.getNumber();
				String imgUrl = getIcon(part);
				URLFactory uf = new URLFactory();
				HashMap m = new HashMap();
				m.put("oid", oid);
				m.put("action", "ObjProps");
				String urlInfo = GatewayServletHelper.buildAuthenticatedHREF(uf, "wt.enterprise.URLProcessor", "URLTemplateAction",m);
				value ="<img src=\""+imgUrl+"\">";
			}else if(doc != null){
				String docImgUrl = getIcon(doc);
				String docNumber = doc.getNumber();
				URLFactory uf = new URLFactory();
				HashMap m = new HashMap();
				m.put("oid", oid);
				m.put("action", "ObjProps");
				String urlInfo = GatewayServletHelper.buildAuthenticatedHREF(uf, "wt.enterprise.URLProcessor", "URLTemplateAction",m);
				value = value + "<img src=\""+docImgUrl+"\">";
			}else if(epmdoc != null){
				String docImgUrl = getIcon(epmdoc);
				String docNumber = epmdoc.getNumber();
				URLFactory uf = new URLFactory();
				HashMap m = new HashMap();
				m.put("oid", oid);
				m.put("action", "ObjProps");
				String urlInfo = GatewayServletHelper.buildAuthenticatedHREF(uf, "wt.enterprise.URLProcessor", "URLTemplateAction",m);
				value = value + "<img src=\""+docImgUrl+"\">";
			}else if(co != null){
				String docImgUrl = getIcon(co);
				String docNumber = co.getNumber();
				URLFactory uf = new URLFactory();
				HashMap m = new HashMap();
				m.put("oid", oid);
				m.put("action", "ObjProps");
				String urlInfo = GatewayServletHelper.buildAuthenticatedHREF(uf, "wt.enterprise.URLProcessor", "URLTemplateAction",m);
				value = value + "<img src=\""+docImgUrl+"\">";
			} else if(processPlan != null){
                String processPlanImgUrl = getIcon(processPlan);
                String processPlanNumber = processPlan.getNumber();
                URLFactory uf = new URLFactory();
                HashMap m = new HashMap();
                m.put("oid", oid);
                m.put("action", "ObjProps");
                String urlInfo = GatewayServletHelper.buildAuthenticatedHREF(uf, "wt.enterprise.URLProcessor", "URLTemplateAction",m);
                value = value + "<img src=\""+processPlanImgUrl+"\">";
            }
			

//			String value = "<img src=\""+imgUrl+"\"><a href=\""+urlInfo+"\">"+partNumber+"</a>";
			NmTableGUIComponent gui = new NmTableGUIComponent(value);
			guicomponentarrayMain.addGUIComponent(gui);
			return guicomponentarrayMain;
		}
		
		if((obj instanceof WTDocument)||(obj instanceof EPMDocument)||(obj instanceof MPMProcessPlan)){
			if(columnName.equals("sign_result")){
				StringBuffer sb = new StringBuffer();
				List<String> testList = new ArrayList<String>();
				testList.add("同意");
				testList.add("无需会签");
				testList.add("不同意");
				Versioned version = null;
				QueryResult qr = VersionControlHelper.service.allVersionsFrom((Versioned)obj);
				if(qr.hasMoreElements())
					version = (Versioned)qr.nextElement();
				ReferenceFactory rf = new ReferenceFactory();
				//System.out.println("version is:" + version);//此为小版本
				sb.append("<select name=\"" +rf.getReference(version) +"_select\" " + "id=\""+oid +"_select\">");//getReference取到Version oid
				for(int i = 0 ; i < testList.size() ; i++){
					String tempStr = testList.get(i);
					sb.append("<option value=\""+tempStr+"\">");
					sb.append(tempStr);
					sb.append("</option>");
				}
				sb.append("</select>");
				String value = sb.toString();
				NmTableGUIComponent gui = new NmTableGUIComponent(value);
				guicomponentarrayMain.addGUIComponent(gui);
				
				return guicomponentarrayMain;
			}else if(columnName.equals("sign_advise")){
				String value = "<input type=\"text\" id=\""+oid+"_advise\" readonly>";  //ywu 2010.11.18 避免乱输导致解析签名错误，只能点按钮后设置，此处设为只读
				NmTableGUIComponent gui = new NmTableGUIComponent(value);
				guicomponentarrayMain.addGUIComponent(gui);
				return guicomponentarrayMain;
			}else if(columnName.equals("sign_message")){
				String value = "<input type=\"text\" id=\""+oid+"_message\" readonly>";
				NmTableGUIComponent gui = new NmTableGUIComponent(value);
				guicomponentarrayMain.addGUIComponent(gui);
				return guicomponentarrayMain;
			}else if(columnName.equals("sign_button")){
				String value = "<input type=\"button\" id=\""+oid+"_button\" value='设置签名信息' onclick='addComment(\""+oid.trim()+"\")'>";
				NmTableGUIComponent gui = new NmTableGUIComponent(value);
				guicomponentarrayMain.addGUIComponent(gui);
				return guicomponentarrayMain;
			}
		}else if(obj instanceof WTChangeOrder2){
			if(columnName.equals("sign_result")){
				StringBuffer sb = new StringBuffer();
				List<String> testList = new ArrayList<String>();
				testList.add("同意");
				testList.add("无需会签");
				testList.add("不同意");
				
				ReferenceFactory rf = new ReferenceFactory();
				//System.out.println("version is:" + version);//此为小版本
				sb.append("<select name=\"" +rf.getReference((WTObject)obj) +"_select\" " + "id=\""+oid +"_select\">");//getReference取到Version oid
				for(int i = 0 ; i < testList.size() ; i++){
					String tempStr = testList.get(i);
					sb.append("<option value=\""+tempStr+"\">");
					sb.append(tempStr);
					sb.append("</option>");
				}
				sb.append("</select>");
				String value = sb.toString();
				NmTableGUIComponent gui = new NmTableGUIComponent(value);
				guicomponentarrayMain.addGUIComponent(gui);
				
				return guicomponentarrayMain;
			}else if(columnName.equals("sign_advise")){
				String value = "<input type=\"text\" id=\""+oid+"_advise\" readonly>";
				NmTableGUIComponent gui = new NmTableGUIComponent(value);
				guicomponentarrayMain.addGUIComponent(gui);
				return guicomponentarrayMain;
			}else if(columnName.equals("sign_message")){
				String value = "<input type=\"text\" id=\""+oid+"_message\" readonly>";
				NmTableGUIComponent gui = new NmTableGUIComponent(value);
				guicomponentarrayMain.addGUIComponent(gui);
				return guicomponentarrayMain;
			}else if(columnName.equals("sign_button")){
				String value = "<input type=\"button\" id=\""+oid+"_button\" value='设置签名信息' onclick='addComment(\""+oid.trim()+"\")'>";
				NmTableGUIComponent gui = new NmTableGUIComponent(value);
				guicomponentarrayMain.addGUIComponent(gui);
				return guicomponentarrayMain;
			}
		}else{
			Label text = new Label("");
			guicomponentarrayMain.addGUIComponent(text);
		}
		
		return guicomponentarrayMain;
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



}
