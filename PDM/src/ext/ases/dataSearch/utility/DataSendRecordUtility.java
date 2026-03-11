package ext.ases.dataSearch.utility;

import java.io.IOException;
import java.util.Map;

import ext.ases.techMaterial.TechnicsMaterialEntries;
import ext.ases.techMaterial.model.TechnicaQuotaNumber;
import ext.ases.techMaterial.util.TechnicsMaterialUtils;
import ext.casc.util.NmTableGUIComponent;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTProperties;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.components.rendering.guicomponents.UrlDisplayComponent;

import ext.ases.dataSearch.ObjectDetailDataBean;
import ext.casc.util.WCUtil;
import ext.casc.util.WNCUtil;
import ext.casc.util.WTUtil;

public class DataSendRecordUtility extends AbstractDataUtility {

	private static String getURLAddress(String containerOid, String oid)
			throws IOException {
		String urlBase = WTProperties.getLocalProperties().getProperty(
				"java.rmi.server.hostname");
		String webAPP = WTProperties.getLocalProperties().getProperty(
				"wt.webapp.name");
		String url = "http://" + urlBase + "/" + webAPP
				+ "/app/#ptc1/tcomp/infoPage?ContainerOid="+containerOid+"&oid=" + oid + "&u8=1";
		return url;
	}

	public Object getDataValue(String columnName, Object obj, ModelContext mc)
			throws WTException {
		// TODO Auto-generated method stub
		String number = "";
		String oid = "";
		String containerOid = "";
		GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
		if (columnName.equals("number")) {

			if (obj instanceof ObjectDetailDataBean) {
				ObjectDetailDataBean oddb = (ObjectDetailDataBean) obj;
				number = oddb.getNumber();
				oid = oddb.getObjOid();
				ReferenceFactory rf = new ReferenceFactory();
				Persistable p = null;
				try{
					 p = rf.getReference(oid).getObject();
				}catch(Exception e){
					e.printStackTrace();
				}

				if(p==null){
					return "数据已被删除";
				}

				if (p instanceof WTContained) {
					WTContained contained = (WTContained) p;
					WTContainer contaner = WTContainerHelper
							.getContainer(contained);
					containerOid = rf.getReferenceString(contaner);
				}
			}
			UrlDisplayComponent udc = new UrlDisplayComponent(number);
			udc.setLabelForTheLink(number);
			try {
				oid = "OR:"+oid;
				udc.setLink(getURLAddress(containerOid, oid));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			udc.setTarget("_blank");
			guicomponentarrayMain.addGUIComponent(udc);
		} else if (columnName.equals("packNum")) {
			if (obj instanceof ObjectDetailDataBean) {
				ObjectDetailDataBean oddb = (ObjectDetailDataBean) obj;
				number = oddb.getPackNum();
				oid = oddb.getPackOid();
				ReferenceFactory rf = new ReferenceFactory();
				Persistable p = null;
				try{
					 p = rf.getReference(oid).getObject();
				}catch(Exception e){
					e.printStackTrace();
				}

				if(p==null){
					return "";
				}
				if (p instanceof WTContained) {
					WTContained contained = (WTContained) p;
					WTContainer contaner = WTContainerHelper
							.getContainer(contained);
					containerOid = rf.getReferenceString(contaner);
				}

			}
			UrlDisplayComponent udc = new UrlDisplayComponent(number);
			udc.setLabelForTheLink(number);
			try {
				udc.setLink(getURLAddress(containerOid, oid));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			udc.setTarget("_blank");
			guicomponentarrayMain.addGUIComponent(udc);
		}else if (columnName.equals("container")) {
			if (obj instanceof ObjectDetailDataBean) {
				ObjectDetailDataBean oddb = (ObjectDetailDataBean) obj;
				String objoid = oddb.getObjOid();
				Persistable p =null;
				try{
					 p = WCUtil.getPersistable("OR:"+objoid);

				}catch(Exception e){
					e.printStackTrace();
				}
				if(p!=null && p instanceof WTContained){
					return ((WTContained)p).getContainerName();
				}else{
					return "";
				}
			}
		}else if(columnName.equals("quotanumber")){
			if (obj instanceof TechnicaQuotaNumber) {
				TechnicaQuotaNumber oddb = (TechnicaQuotaNumber) obj;
				String quotanumber = oddb.getQuotanumber();

				try {
					Map map = TechnicsMaterialUtils.getTMEidByNumber(quotanumber);
					String containOid ="OR%3Awt.inf.library.WTLibrary%3A"+(String) map.get("containOid");
					String docOid ="OR%3Aext.ases.techMaterial.TechnicsMaterialEntries%3A"+ (String)map.get("docOid");
					String urlBase = WTProperties.getLocalProperties().getProperty(
							"java.rmi.server.hostname");
					String webAPP = WTProperties.getLocalProperties().getProperty(
							"wt.webapp.name");
					String url = "http://" + urlBase + "/" + webAPP
							+ "/app/#ptc1/tcomp/infoPage?ContainerOid=" + containOid + "&oid=" + docOid + "&u8=1";
					//String values = "<a herf=\"javascript:void(0) onclick='window.open(\""+url+"\",\"_blank\")'>" + "<font " + "color=\"blue\">" + quotanumber + "</font></a>";
					String value = "<a href='"+url+"'  target=\"_blank\">"+quotanumber+"</a>";
					NmTableGUIComponent gui = new NmTableGUIComponent(value);
					guicomponentarrayMain.addGUIComponent(gui);
					return guicomponentarrayMain;
				}catch (Exception e){
					e.printStackTrace();
				}

			}
		}

		return guicomponentarrayMain;
	}

}
