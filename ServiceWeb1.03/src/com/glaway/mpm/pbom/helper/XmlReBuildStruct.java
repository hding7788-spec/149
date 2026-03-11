package com.glaway.mpm.pbom.helper;

import java.beans.PropertyVetoException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Element;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.Quantity;
import wt.part.QuantityUnit;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.glaway.mpm.constants.Constants;
import com.glaway.mpm.intf.PBOMEditorToWCIntfRMI;
import com.glaway.mpm.processplan.helper.ProcessPlanHelper;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.SWXMLUtil;
import com.glaway.mpm.util.WTPartUtil;


/**
 * 根据pbom.xml重构M视图结构
 *
 */
public class XmlReBuildStruct implements RemoteAccess {

	protected static Logger logger = Logger.getLogger("XmlReBuildStruct : ");



	public static void main(String args[]) {

	}



	/**
	 * 获取部件的pbom.xml中的第一个QMPartInfo,如果没有pbom.xml，返回null
	 *
	 * @param part
	 * @return
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	private static Element getTopPartInfoElement(WTPart part) throws WTException, PropertyVetoException, IOException {
		Element partInfoElement = null;
		WTDocument doc = PBOMHelper.getBOMXmlDoc(part, "-pbom.xml");
		if (doc == null) {
			return partInfoElement;
		}
		InputStream fis = null;
		try {
			ApplicationData appData = (ApplicationData) ContentHelper.service.getPrimary(doc);
			if (appData == null) {
			}
			fis = ContentServerHelper.service.findContentStream(appData);
			SWXMLUtil xmlUtil = new SWXMLUtil(fis);
			Element rootElement = xmlUtil.getRootElement();
			partInfoElement = rootElement.getChild("parts").getChild("QMPartInfo");
			return partInfoElement;
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
	}





	private static QuantityUnit getUnit(String unitStr) {
		QuantityUnit quantityUnit = null;
		QuantityUnit[] unitSet = QuantityUnit.getQuantityUnitSet();
		for (QuantityUnit unit : unitSet) {
			if (unit.getDisplay(Locale.CHINA).equals(unitStr)) {
				quantityUnit = unit;
				break;
			}
		}
		if (quantityUnit == null) {
			quantityUnit = QuantityUnit.EA;
		}
		return quantityUnit;
	}

	public static void execute(WTPart part, byte[] bytes, Map<String, String> gysls) {
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(bytes);
			SWXMLUtil xmlUtil = new SWXMLUtil(is);
			Element rootElement = xmlUtil.getRootElement();
			String productName = rootElement.getAttributeValue("productName");
			Element element = rootElement.getChild("parts").getChild("QMPartInfo");

			WTPart newpart = (WTPart)ProcessPlanHelper.searchLatestIteratedByNumberVersionView(WTPart.class, part.getNumber(),
					part.getVersionInfo().getIdentifier().getValue(), "Manufacturing");

			synchXmlAndWTPart(newpart,element,gysls,productName);

			Element childsElement = element.getChild("childs");

			QueryResult queryResult = WTPartHelper.service.getUsesWTPartMasters(newpart);
			while (queryResult.hasMoreElements()) {
				WTPartUsageLink usageLink = (WTPartUsageLink) queryResult.nextElement();
				PersistenceServerHelper.manager.remove(usageLink);
			}

			if(childsElement!=null){
				List<Element> childPartInfoList = childsElement.getChildren("QMPartInfo");
				for (Element childPartInfo : childPartInfoList) {

					String childPartNumber = childPartInfo.getAttributeValue("partNumber");
					WTPart childPart =  WTPartUtil.getPartByNumberAndView(childPartNumber, Constants.planning);
					if (childPart == null) {
						System.out.println("找不到M视图的零件："+childPartNumber);
						continue;
					}

					//setIBAValueFromXmlDocument(childPart,childPartInfo);
					//setXmlFromWTPart(childPart,childPartInfo);
					WTPartUsageLink link = WTPartUtil.getWTPartUsageLink(newpart, (WTPartMaster) childPart.getMaster());

					double preCount = 0;
					if(link==null){
						link = WTPartUtil.createWTPartUsageLink(newpart, (WTPartMaster) childPart.getMaster());
					}else{
						preCount = link.getQuantity().getAmount();
					}


					Quantity quantity = new Quantity();
					//quantity.setAmount(link.getQuantity().getAmount() + 1);//modify by longxiuchuan 20131229

					String useCount = childPartInfo.getAttributeValue("useCount");
					//String partNumber = childPartInfo.getAttributeValue("partNumber");
					String key = newpart.getNumber()+"->"+childPartNumber;
					String gysl = gysls.get(key);
					if(gysl==null||"".equals(gysl)||"null".equals(gysl)){
						gysl = childPartInfo.getAttributeValue("gysl");
					}
					String CHBM = childPartInfo.getAttributeValue("WZMC");

					if(useCount != null && !"".equals(useCount)) {
						quantity.setAmount(Integer.valueOf(useCount)+preCount);
					} else {
						quantity.setAmount(link.getQuantity().getAmount());
					}
					link.setQuantity(quantity);
					IBAHelper linkHelper = new IBAHelper(link);
					if(CHBM!=null&&!"".equals(CHBM)){
						linkHelper.setIBAAnyValue(link, "CHBM", CHBM);
					}


					if(gysl != null && !"".equals(gysl)) {
						linkHelper.setIBAAnyValue(link, "GYSL", gysl);
					}

					PersistenceServerHelper.manager.update(link);

					processChildNode(childPart,childPartInfo, gysls);

				}
			}

			String s = SWXMLUtil.doc2ToString(rootElement.getDocument());

			PBOMEditorToWCIntfRMI.savePBOMXmlWithOutCheckOutRMI(""+newpart.getPersistInfo().getObjectIdentifier().getId(), s.getBytes());

		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private static void processChildNode(WTPart newpart ,Element partInfo,Map<String, String> gysls) throws WTException, RemoteException, WTPropertyVetoException, ParseException {

		Element childsElement = partInfo.getChild("childs");


		if(childsElement!=null){

			List<Element> childPartInfoList = childsElement.getChildren("QMPartInfo");

			if(childPartInfoList.size()>0){
				QueryResult queryResult = WTPartHelper.service.getUsesWTPartMasters(newpart);
				while (queryResult.hasMoreElements()) {
					WTPartUsageLink usageLink = (WTPartUsageLink) queryResult.nextElement();
					PersistenceServerHelper.manager.remove(usageLink);
				}
			}
			for (Element childPartInfo : childPartInfoList) {

				String childPartNumber = childPartInfo.getAttributeValue("partNumber");
				WTPart childPart =  WTPartUtil.getPartByNumberAndView(childPartNumber, Constants.planning);
				if (childPart == null) {
					System.out.println("找不到M视图的零件："+childPartNumber);
					continue;
				}

				//setIBAValueFromXmlDocument(childPart,childPartInfo);
				//setXmlFromWTPart(childPart,childPartInfo);
				WTPartUsageLink link = WTPartUtil.getWTPartUsageLink(newpart, (WTPartMaster) childPart.getMaster());

				double preCount = 0;
				if(link==null){
					link = WTPartUtil.createWTPartUsageLink(newpart, (WTPartMaster) childPart.getMaster());
				}else{
					preCount = link.getQuantity().getAmount();
				}


				Quantity quantity = new Quantity();
				//quantity.setAmount(link.getQuantity().getAmount() + 1);//modify by longxiuchuan 20131229

				String useCount = childPartInfo.getAttributeValue("useCount");
				//String partNumber = childPartInfo.getAttributeValue("partNumber");
				String key = newpart.getNumber()+"->"+childPartNumber;
				String gysl = gysls.get(key);
				if(gysl==null||"".equals(gysl)||"null".equals(gysl)){
					gysl = childPartInfo.getAttributeValue("gysl");
				}
				String CHBM = childPartInfo.getAttributeValue("WZMC");

				if(useCount != null && !"".equals(useCount)) {
					quantity.setAmount(Integer.valueOf(useCount)+preCount);
				} else {
					quantity.setAmount(link.getQuantity().getAmount());
				}
				link.setQuantity(quantity);
				IBAHelper linkHelper = new IBAHelper(link);
				if(CHBM!=null&&!"".equals(CHBM)){
					linkHelper.setIBAAnyValue(link, "CHBM", CHBM);
				}


				if(gysl != null && !"".equals(gysl)) {
					linkHelper.setIBAAnyValue(link, "GYSL", gysl);
				}

				PersistenceServerHelper.manager.update(link);

				processChildNode(childPart,childPartInfo, gysls);
			}
		}
	}

	private static void synchXmlAndWTPart(WTPart part, Element element, Map<String, String> gysls,String productName) throws WTPropertyVetoException, RemoteException, WTException {
		if("标准件".equals(part.getContainerName())){
			setBZJIBAValueFromXmlDocument(part,element);
		}
		if(part.getContainerName().equals(productName)){
			setIBAValueFromXmlDocument(part,element);
		}else{
			setXMLIBAValueFromWTPart(part,element);
		}

		setXmlFromWTPart(part,element,gysls);
		Element childsElement = element.getChild("childs");
		if(childsElement!=null){
			List<Element> childPartInfoList = childsElement.getChildren("QMPartInfo");
			for (Element childPartInfo : childPartInfoList) {
				String childPartNumber = childPartInfo.getAttributeValue("partNumber");
				WTPart childPart =  WTPartUtil.getPartByNumberAndView(childPartNumber, Constants.planning);
				if (childPart == null) {
					continue;
				}
				synchXmlAndWTPart(childPart,childPartInfo,gysls,productName);
			}
		}
	}

	private static void setBZJIBAValueFromXmlDocument(WTPart part, Element element) throws WTException, WTPropertyVetoException, RemoteException {
		Map<String, String> ibaMap = new HashMap<String,String>();
		ibaMap.put("MTYPE", element.getAttributeValue("MTYPE"));
		IBAHelper attrHelper = new IBAHelper(part);
		attrHelper.setIBAValue(part, ibaMap);

	}

	private static void setXMLIBAValueFromWTPart(WTPart part, Element element) throws WTException, WTPropertyVetoException, RemoteException {
		IBAHelper attrHelper = new IBAHelper(part);
		String PINDEX = attrHelper.getIBAValue("PINDEX");
		if(PINDEX!=null){
			element.setAttribute("PINDEX",PINDEX);
		}

		String MINDEX = attrHelper.getIBAValue("MINDEX");
		if(MINDEX!=null)
			element.setAttribute("MINDEX",MINDEX);

		String CINDEX = attrHelper.getIBAValue("CINDEX");
		if(CINDEX!=null)
			element.setAttribute("CINDEX",CINDEX);

		String PHASE_CODE = attrHelper.getIBAValue("PHASE_CODE");
		if(PHASE_CODE!=null)
			element.setAttribute("PHASE_CODE",PHASE_CODE);

	}

	private static void setXmlFromWTPart(WTPart part, Element element, Map<String, String> gysls) throws WTException {
		element.setAttribute("oid",part.getPersistInfo().getObjectIdentifier().getId()+"");
		element.setAttribute("version",part.getVersionInfo().getIdentifier().getValue()+"."+part.getIterationInfo().getIdentifier().getValue());

		IBAHelper attrHelper = new IBAHelper(part);
		String BATCH = attrHelper.getIBAValue("BATCH");
		if(BATCH!=null &&!"".equals(BATCH)){
			element.setAttribute("BATCH",BATCH);
		}

	}

	private static void setIBAValueFromXmlDocument(WTPart part, Element element) throws WTException, WTPropertyVetoException, RemoteException {
		Map<String, String> ibaMap = new HashMap<String,String>();
		//System.out.println("###"+part.getNumber());

		ibaMap.put("PINDEX", element.getAttributeValue("PINDEX"));
		ibaMap.put("MINDEX", element.getAttributeValue("MINDEX"));
		ibaMap.put("CINDEX", element.getAttributeValue("CINDEX"));
		ibaMap.put("PHASE_CODE", element.getAttributeValue("PHASE_CODE"));
		//System.out.println(ibaMap);
		IBAHelper attrHelper = new IBAHelper(part);
		attrHelper.setIBAValue(part, ibaMap);
	}

}
