/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command;


import com.glaway.mpm.util.*;

import ext.casc.folder.AuthoriserPermissionUtil;
import ext.casc.sop.constants.SopConstants;
import ext.casc.webservice.TestConfig;
import ext.casc.webservice.WSConstants;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 类功能：PBOM状态查询接口
 *
 * @author hding
 * @date 2020/6/24
 */
public class ExchangeProcessDocCommand implements WebServiceCommand, InitializingBean {
	static Logger LOGGER = Logger.getLogger(ExchangeProcessDocCommand.class.getName());
   public static String[] ibaAttrs =new String[] {"DEPT","MINDEX","PINDEX","SECRET","KEYCOMPONENT","PHASE_CODE","PPNUMBER"
			,"PAGE","PPLANTYPE","PPNAME","ZFFLAG","CINDEX","BATCH","isCLDE","BIAOSHI"};
	// 方法标识
	public static final String METHOD_NAME = "exchangeProcessDoc";
	@Override
	public String execute(String params) {
		String rtnCode = "E";
		String rtnMsg="";
        JSONObject jrtnObj = new JSONObject();

		JSONObject jparams;
		try {
			jparams = new JSONObject(params);
			String partNumber =jparams.optString("partNumber");


			WTPart part = WTPartUtil.getPartByNumberAndView(partNumber,"Manufacturing");
			if(part!=null){
				Set<String> set = new HashSet<String>();
				QueryResult qr2 = WTPartHelper.service.getDescribedByWTDocuments(part, true);
				while (qr2.hasMoreElements()) {
					WTDocument document = (WTDocument) qr2.nextElement();
					set.add(document.getNumber());
				}

				JSONArray processPlans =jparams.optJSONArray("processPlans");
				StringBuilder sb = new StringBuilder("");
				for(int i=0;i<processPlans.length();i++) {
					String docNumber =  "";
					try{
						JSONObject	processPlan  = processPlans.getJSONObject(i);
						docNumber = processPlan.optString("docNumber");
						if(set.contains(docNumber+TestConfig.END)){
							continue;
						}
						System.out.println("正在迁移工艺："+docNumber);
						String docName = processPlan.optString("docName");
						String technicsType = processPlan.optString("technicsType");
						String userName = processPlan.optString("userName");
						WTUser user = UserUtil.getUser(userName);
						String fileUrl = "http://pdm.149.sast.casc/"+docNumber+".zip";
						URL url = new URL(fileUrl);

						/*String tempFilePath = PropertiesUtil.getWTHome() + File.separator + "temp" +File.separator
								+ java.util.UUID.randomUUID().toString() + File.separator;
						FileUtils.copyURLToFile(url,new File(tempFilePath  + docNumber+".zip"));
						ApacheZipUtil.decompress(tempFilePath + docNumber+".zip", tempFilePath + docNumber);
						File xmlFile = new File(tempFilePath + docNumber + File.separator + docNumber + ".xml");
						InputStream inputStream = new FileInputStream(xmlFile);
						SWXMLUtil xmlUtil = new SWXMLUtil(inputStream);
						Element rootElement = xmlUtil.getRootElement();
						Element techEle = (Element) rootElement.getChildren("QMFawTechnicsInfo").get(0);
						*//*String value = techEle.getAttributeValue("creator");
						techEle.setAttribute("creator", name);
						techEle.setAttribute("creatorOid", receiveOid);*//*
						techEle.setAttribute("lifecycle", "正在工作");*/



						String folderPath = LoadConfig.getInstance().getTechnicsDocPrefixPath()+technicsType;
						String[][] technicsTypes = LoadConfig.getInstance().getTechnicsType();
						String doctype = null;

						for(int j=0;j<technicsTypes[1].length;j++){
							if(technicsTypes[1][j].equals(technicsType)){
								doctype = technicsTypes[5][j];
							}
						}
						if("SOPDoc".equals(doctype)){
							folderPath = part.getFolderPath().trim();
							String[] str = folderPath.split("/");
							folderPath = SopConstants.SOP_FOLDOR_PROCESS+str[3]+"/"+str[4];
						}

						//如果doctype为空，则为报表类工艺文件
						if(doctype == null) {
							doctype = LoadConfig.getInstance().getReportTechnicsType();
							folderPath = LoadConfig.getInstance().getReportTechnicsFolderPath();
						}
						WTDocument document = WTDocumentUtil.createDocument(docNumber+TestConfig.END,docName, part.getContainer(), folderPath, LoadConfig.getInstance().getLocalDomainName()+"."+doctype);

						InputStream input = url.openStream();

						document = WTDocumentUtil.setPrimaryForDocument(document, docNumber + ".zip", input);
						WTPartUtil.createWTPartDescribeLink(part, document);

						Map<String,String > ibaMap = new HashMap<String, String>();

						for(String iba:ibaAttrs){
							if(!"".equals(processPlan.optString(iba))){
								ibaMap.put(iba,processPlan.optString(iba));
							}
						}
						IBAHelper attrHelper = new IBAHelper(document);
						attrHelper.setIBAValue(document, ibaMap);

						document = (WTDocument) PersistenceHelper.manager.refresh(document);

						if(user!=null){
							//AuthoriserPermissionUtil.assignUserToRole(user, document, "AUTHORISE_RECEIVE");
							WTPrincipalReference ref = WTPrincipalReference.newWTPrincipalReference(user);
							try {
								AuthoriserPermissionUtil.setModifier(document,ref);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}catch(Exception e){
						sb.append(docNumber+"工艺迁移失败！");
						LOGGER.error("工艺迁移失败： " + e.getLocalizedMessage(), e);
						e.printStackTrace();
					}

				}
				JSONObject returnData = new JSONObject() ;
				jrtnObj.put(WSConstants.RTN_DATA, returnData.toString());
				rtnCode = "S";
				if("".equals(sb.toString())){
					rtnMsg = "工艺迁移成功";
				}else{
					rtnMsg = sb.toString();
				}
			}else{
				rtnCode = "S";
				rtnMsg = "没找到相应零件";
			}


		} catch (JSONException e) {
			rtnMsg = "参数JSON格式不正确： " + e.getLocalizedMessage();
			LOGGER.error(rtnMsg, e);
		} catch (WTException e) {
			rtnMsg = "工艺迁移失败： " + e.getLocalizedMessage();
			LOGGER.error(rtnMsg, e);
		}

		try {
	           jrtnObj.put(WSConstants.RTN_MSG, rtnMsg);
	           jrtnObj.put(WSConstants.RTN_CODE, rtnCode);
	     } catch (JSONException ex) {
	            LOGGER.error("Error building JSON: ", ex);
	     }

		return jrtnObj.toString();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		WebServiceCommandFactory.register(METHOD_NAME, this);
	}

}
