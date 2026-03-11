/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command;


import com.glaway.mpm.util.*;
import ext.casc.fileprint.FilePrintUtil2;
import ext.casc.folder.AuthoriserPermissionUtil;
import ext.casc.sop.constants.SopConstants;
import ext.casc.util.DBConn;
import ext.casc.util.DocUtil;
import ext.casc.webservice.WSConstants;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import wt.content.*;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pom.PersistenceException;
import wt.pom.Transaction;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.VersionControlHelper;

import java.beans.PropertyVetoException;
import java.io.*;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 类功能：PBOM状态查询接口
 *
 * @author hding
 * @date 2020/6/24
 */
public class ExchangeProcessDocCommandZS implements WebServiceCommand, InitializingBean {
	static Logger LOGGER = Logger.getLogger(ExchangeProcessDocCommandZS.class.getName());
   public static String[] ibaAttrs =new String[] {"DEPT","MINDEX","PINDEX","SECRET","KEYCOMPONENT","PHASE_CODE","PPNUMBER"
			,"PAGE","PPLANTYPE","PPNAME","ZFFLAG","CINDEX","BATCH","isCLDE","BIAOSHI"};
    public static final String METHOD_NAME = "exchangeProcessDocZS";
	public static final String DOC_END = "";
	public static final String PART_END = "";
	public static final String BACKUP_PATH = "/c/gybackup/";
	//public static final String BACKUP_PATH = "C:\\ptc\\gybackup\\";
	public static final String URL_CS = "http://10.125.192.32/";//正式部署需调整
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	@Override
	public String execute(String params) {
		String rtnCode = "E";
		String rtnMsg="";
        JSONObject jrtnObj = new JSONObject();

		JSONObject jparams;
		try {
			jparams = new JSONObject(params);
			System.out.println("ExchangeProcessDocCommandZS:"+jparams);
			String partNumber =jparams.optString("partNumber")+PART_END;
			WTPart part = WTPartUtil.getPartByNumberAndView(partNumber,"Manufacturing");
			if(part!=null){

				JSONArray processPlans =jparams.optJSONArray("processPlans");
				StringBuilder sb = new StringBuilder("");
				for(int i=0;i<processPlans.length();i++) {
					String docNumber =  "";
					try{
						JSONObject	processPlan  = processPlans.getJSONObject(i);
						docNumber = processPlan.optString("docNumber");
						String newDocNuber = docNumber+DOC_END;

						if(isReplace(newDocNuber)){
							continue;
						}
						long createTime = processPlan.optLong("createTime");

						//获取要替换的文档
						WTDocument preVersion = getPreVersion(newDocNuber,createTime);

						if(preVersion!=null){

								String screateTime = simpleDateFormat.format(preVersion.getCreateTimestamp());
								String smodifyTime = simpleDateFormat.format(preVersion.getModifyTimestamp());
								//备份该文档
								//合并工艺
							    if(mergeAndReplaceDoc(preVersion,processPlan)){

					                FilePrintUtil2.decompressZip(preVersion);

					                updateDocTime(preVersion, screateTime,smodifyTime);

					                updateApplicationDatas(preVersion, screateTime,smodifyTime);

							    }



						}else{
							//新建工艺
							newProcess(processPlan, part);
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

	private void newProcess(JSONObject processPlan,WTPart part ) throws Exception {
		String docNumber = processPlan.optString("docNumber");
		String fileUrl = URL_CS+docNumber+".zip";
		URL url = new URL(fileUrl);

		InputStream input = url.openStream();

		System.out.println("正在迁移工艺："+docNumber);
		String docName = processPlan.optString("docName");
		String technicsType = processPlan.optString("technicsType");
		String userName = processPlan.optString("userName");
		WTUser user = UserUtil.getUser(userName);

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
		WTDocument document = WTDocumentUtil.createDocument(docNumber+DOC_END,docName, part.getContainer(), folderPath, LoadConfig.getInstance().getLocalDomainName()+"."+doctype);

		String fileUrl2 = "";
		String fileName = "";
		document = WTDocumentUtil.setPrimaryForDocument(document, docNumber + ".zip", input);

		fileName = processPlan.optString("fileName");

		if(fileName!=null &&!"".equals(fileName)){
			String urlFileName = fileName.replace(".pdf", "");
			fileUrl2 = URL_CS+urlFileName;
			URL url2 = new URL(fileUrl2);
			InputStream input2 = url2.openStream();
			document = DocUtil.setSecondaryForDocument(document, fileName, input2);
		}


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

		recordDoc(document,"ADD");

		if(user!=null){
			//AuthoriserPermissionUtil.assignUserToRole(user, document, "AUTHORISE_RECEIVE");
			WTPrincipalReference ref = WTPrincipalReference.newWTPrincipalReference(user);
			try {
				AuthoriserPermissionUtil.setModifier(document,ref);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		setAttris(document,processPlan);
	}





	private boolean  mergeAndReplaceDoc(WTDocument preVersion, JSONObject processPlan)  {
		FileInputStream fileInputStream =null;
		Transaction trans = new Transaction();
   	 	FileOutputStream fileOutputStream = null;
   	 	InputStream inputStreamZS = null;
   	 	InputStream inputStreamCS = null;


		try{
			trans.start();

			String docNumber = processPlan.optString("docNumber");
			String filePathZs = BACKUP_PATH+docNumber+File.separatorChar+preVersion.getPersistInfo().getObjectIdentifier().getId();
			String filePathCs = BACKUP_PATH+docNumber+File.separatorChar+"cs";
			String filePathHB = BACKUP_PATH+docNumber+File.separatorChar+"hb";
			String bkfilePathZs = BACKUP_PATH+docNumber+File.separatorChar+preVersion.getPersistInfo().getObjectIdentifier().getId()+File.separatorChar+"pdf";


			File bkfilePathZsPath = new File(bkfilePathZs);
			if(!bkfilePathZsPath.exists()){
				bkfilePathZsPath.mkdirs();
			}
			File filePathCsPath = new File(filePathCs);
			if(!filePathCsPath.exists()){
				filePathCsPath.mkdirs();
			}

			File filePathHBPath = new File(filePathHB);
			if(!filePathHBPath.exists()){
				filePathHBPath.mkdirs();
			}

			//下载工艺
			String fileName = WTDocumentUtil.downloadDocumentPrimaryToTemp(preVersion, filePathZs);

			downloadDocumentSecondToPath(preVersion, bkfilePathZs);


			String csFileName = filePathCs+File.separatorChar+docNumber+".zip";
			String fileUrl = URL_CS+docNumber+".zip";
			URL url = new URL(fileUrl);
			dowloadFile(csFileName,url);

			ApacheZipUtil.decompress(filePathZs+File.separatorChar+fileName, filePathZs);
			ApacheZipUtil.decompress(csFileName, filePathCs);

			FileUtil.deleteFile(new File(csFileName));

			String zsXmlFile = filePathZs+File.separatorChar + docNumber+".xml";
			String csXmlFile = filePathCs+File.separatorChar + docNumber+".xml";

			inputStreamZS = new FileInputStream(zsXmlFile);
			inputStreamCS = new FileInputStream(csXmlFile);
			SWXMLUtil xmlUtilZs = new SWXMLUtil(inputStreamZS);
			SWXMLUtil xmlUtilCs = new SWXMLUtil(inputStreamCS);
			Element rootElementZS = xmlUtilZs.getRootElement();
			String versionZS = "";
			GLLogger.debug(this, "--rootElementZS--" + rootElementZS.getName());
			Element cldeZS = null;Element gydeZS = null;Element peitaoTableZS = null;
			if ("technics".equals(rootElementZS.getName())) {
				Element qmFawTechnicsInfo = rootElementZS.getChild("QMFawTechnicsInfo");
				versionZS = qmFawTechnicsInfo.getAttributeValue("version");
				cldeZS = qmFawTechnicsInfo.getChild("CLDE");
				gydeZS = qmFawTechnicsInfo.getChild("GYDE");
				peitaoTableZS = qmFawTechnicsInfo.getChild("PEITAOTABLE");

			}


			Element rootElementCS = xmlUtilCs.getRootElement();
			GLLogger.debug(this, "--rootElementCS--" + rootElementCS.getName());
			if ("technics".equals(rootElementCS.getName())) {
				Element qmFawTechnicsInfo = rootElementCS.getChild("QMFawTechnicsInfo");
				qmFawTechnicsInfo.removeChildren("CLDE");
				qmFawTechnicsInfo.removeChildren("GYDE");
				qmFawTechnicsInfo.removeChildren("PEITAOTABLE");

				if(cldeZS!=null){
					Element newCLDE =  (Element)cldeZS.clone();
					if(newCLDE!=null )  qmFawTechnicsInfo.addContent(newCLDE);
				}
				if(gydeZS!=null){
					Element newGYDE = (Element)gydeZS.clone();
					if(newGYDE!=null )  qmFawTechnicsInfo.addContent(newGYDE);

				}
				if(peitaoTableZS!=null){
					Element newPEITAOTABLE = (Element)peitaoTableZS.clone();
					if(newPEITAOTABLE!=null )  qmFawTechnicsInfo.addContent(newPEITAOTABLE);
				}
				qmFawTechnicsInfo.setAttribute("version",versionZS);

				fileOutputStream = new FileOutputStream(new File(csXmlFile), false);
				Format format = Format.getPrettyFormat();
				format.setEncoding("GBK");
				XMLOutputter xmlOutput = new XMLOutputter(format);
				xmlOutput.output(xmlUtilCs.getDocument(), fileOutputStream);
				boolean flag = ApacheZipUtil.compress(filePathCs, filePathHB+File.separatorChar + docNumber+".zip");
				if(flag){
					//updatePrimaryContent(preVersion,filePathHB+File.pathSeparatorChar + docNumber+".zip");
				    //记录，用于恢复
	                recordDoc(preVersion,"UPDATE");

	                fileInputStream = new FileInputStream(filePathHB+File.separatorChar + docNumber+".zip");
	                ApplicationData data = WTDocumentUtil.getPrimaryByDocument(preVersion);
	                data = ContentServerHelper.service.updateContent((ContentHolder) preVersion, data, fileInputStream); // 更新内容
				}

			}
			trans.commit();
			trans = null;
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}finally{
			if (trans != null) {
				trans.rollback();
			}
    		try {
				ApacheZipUtil.closeOutputStream(fileOutputStream);
	    		ApacheZipUtil.closeInputStream(inputStreamZS);
	    		ApacheZipUtil.closeInputStream(inputStreamCS);
	    		ApacheZipUtil.closeInputStream(fileInputStream);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}



		return true;


	}
	private void downloadDocumentSecondToPath(WTDocument doc, String fileDir) throws WTException, IOException {
		QueryResult qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.SECONDARY);
		while (qr.hasMoreElements()) {
			ApplicationData applicationdata = (ApplicationData) qr.nextElement();
			String name = applicationdata.getFileName();
			InputStream is = ContentServerHelper.service.findContentStream(applicationdata);
			FileOutputStream fos = new FileOutputStream(new File(fileDir+File.separatorChar+"Print.pdf"));
			int i = 0;
			byte abyte[] = new byte[8192];
			while ((i = is.read(abyte, 0, abyte.length)) >= 0) {
				fos.write(abyte, 0, i);
			}
			is.close();
			fos.close();

		}

	}

	public static String downloadDocumentSencordToTemp(WTDocument doc, String tempPath) throws WTException,
			PropertyVetoException {
		ApplicationData data = WTDocumentUtil.getPrimaryByDocument(doc);
		if (data == null) {
			return null;
		}
		InputStream is = ContentServerHelper.service.findContentStream(data);
		String appFileName = data.getFileName();
		File file = new File(tempPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(tempPath + File.separatorChar + appFileName));
			int i = 0;
			byte abyte[] = new byte[8192];
			while ((i = is.read(abyte, 0, abyte.length)) >= 0) {
				fos.write(abyte, 0, i);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != is) {
					is.close();
				}
				if (null != fos) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return appFileName;
	}

	private void recordDoc(WTDocument preVersion,String state) {
		DBConn conn = null;
		try {
			conn = new DBConn();
			StringBuilder sql = new StringBuilder("insert into GLBACKWTDOCUMENT (DOCNUMBER,DOCID,VERSION,CREATESTAMPA2,MODIFYSTAMPA2,STATE) values ('");

			sql.append(preVersion.getNumber()).append("','");
			sql.append(preVersion.getPersistInfo().getObjectIdentifier().getId()).append("','");
			sql.append(preVersion.getVersionIdentifier().getValue()+"."+preVersion.getIterationIdentifier().getValue()).append("','");
			sql.append(simpleDateFormat.format(preVersion.getCreateTimestamp())).append("','");
			sql.append(simpleDateFormat.format(preVersion.getModifyTimestamp())).append("','");
			sql.append(state).append("')");
			conn.executeUpdate(sql.toString());
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private boolean isReplace(String docNumber) {
		DBConn conn = null;
		try {
			conn = new DBConn();
			StringBuilder sql = new StringBuilder("select * from GLBACKWTDOCUMENT where DOCNUMBER='");
			sql.append(docNumber).append("'");
			ResultSet rs = conn.executeQuery(sql.toString());
			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;

	}

	private void updateDoc(long ida2a2) {
		DBConn conn = null;
		try {
			conn = new DBConn();
			StringBuilder sql = new StringBuilder("update GLBACKWTDOCUMENT set state='1' where DOCID='");
			sql.append(ida2a2).append("'");
			conn.executeUpdate(sql.toString());
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void updatePrimaryContent(WTDocument preVersion, String file) throws FileNotFoundException, WTException, PropertyVetoException, IOException {
		Transaction trans = new Transaction();
		trans.start();
		preVersion = (WTDocument) PersistenceHelper.manager.refresh(preVersion);
		ApplicationData appData = (ApplicationData) ContentHelper.service.getPrimary(preVersion);
		if (appData != null) {
			appData = ContentServerHelper.service.updateContent((ContentHolder) preVersion, appData, new FileInputStream(file)); // 更新内容

		}
		trans.commit();

	}

	private void dowloadFile(String filePath,URL url ) throws IOException{
        OutputStream os = null;
        InputStream is = null;
		try {
            os = new FileOutputStream(filePath);
            is = url.openStream();
            byte[] buffer = new byte[8192];
            int len = 0;
            while ((len = is.read(buffer)) >= 0){
                os.write(buffer, 0, len);
            }
        }finally {
            if (os != null){
                try {
                    os.close();
                }catch (Exception e) {}
            }
            if (is != null){
                try {
                    is.close();
                }catch (Exception e) {

                }
            }
        }
	}


	private WTDocument getPreVersion(String docNumber, long createTime) {
		try {
			 QuerySpec spec = new QuerySpec(WTDocument.class);
	         spec.appendWhere(
	                 new SearchCondition(WTDocument.class,
	                 WTDocument.NUMBER, SearchCondition.EQUAL, docNumber), new int[] { 0 });

	         QueryResult qr = PersistenceHelper.manager.find(spec);
	         if (qr.hasMoreElements()){
	             WTDocument document = (WTDocument)qr.nextElement();
	             QueryResult qr2 = VersionControlHelper.service.allVersionsOf(document.getMaster());
	             WTDocument preDoc  =  null;
	             //WTDocument nowDoc  =  null;

	             while(qr2.hasMoreElements()){
	                 preDoc  = (WTDocument)qr2.nextElement();
	                 if(preDoc.getCreateTimestamp().getTime() < createTime){
	                	 return preDoc;
	                 }
	             }
	             return preDoc;
	         }
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private WTDocument getPreVersion2(String docNumber, long createTime) {
		try {
			QuerySpec qs= new QuerySpec(WTDocument.class);
			qs.appendWhere(new SearchCondition(WTDocument.class, "master>number", "=", docNumber.toUpperCase()), new int[1]);
			qs.appendAnd();
	        qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LATEST_ITERATION, SearchCondition.IS_TRUE), new int[]{1});
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTDocument.class,WTDocument.CREATE_TIMESTAMP, SearchCondition.LESS_THAN, new Timestamp(createTime)), new int[1]);

			QueryResult qr = PersistenceHelper.manager.find(qs);
			if(qr.hasMoreElements()){

			}

		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO Auto-generated method stub
		return null;
	}


	private void setAttris(WTDocument document, JSONObject json) throws PersistenceException, WTException {
		updateDocTimeAndState(document,json);
		long createTime = json.optLong("createTime");
		long modifyTime = json.optLong("modifyTime");
		String screateTime = simpleDateFormat.format(new Date(createTime));
		String smodifyTime = simpleDateFormat.format(new Date(modifyTime));
		updateApplicationDatas(document,screateTime,smodifyTime);

	}
	private void updateApplicationDatas(WTDocument document, String screateTime,String smodifyTime) throws WTException{
		QueryResult qr = ContentHelper.service.getContentsByRole(document, ContentRoleType.SECONDARY);
		while(qr.hasMoreElements()){
			ApplicationData appData = (ApplicationData)qr.nextElement();
			updateApplicationData(appData,screateTime,smodifyTime);
		}

		qr = ContentHelper.service.getContentsByRole(document, ContentRoleType.PRIMARY);
		while(qr.hasMoreElements()){
			ApplicationData appData = (ApplicationData)qr.nextElement();
			updateApplicationData(appData,screateTime,smodifyTime);
		}
	}

	private void updateApplicationData(ApplicationData appData, String screateTime,String smodifyTime) {
		Map<String,String> attris = new HashMap<String, String>();
		attris.put("MODIFYSTAMPA2","to_date('"+smodifyTime+"','yyyy-MM-dd HH24:mi:ss') " );

		attris.put("CREATESTAMPA2","to_date('"+screateTime+"','yyyy-MM-dd HH24:mi:ss') " );

		updateTableAttri("ApplicationData",attris,appData.getPersistInfo().getObjectIdentifier().getId());

	}

	private void updateDocTime(WTDocument document, String screateTime,String smodifyTime) {
		Map<String,String> attris = new HashMap<String, String>();
		attris.put("MODIFYSTAMPA2","to_date('"+smodifyTime+"','yyyy-MM-dd HH24:mi:ss') " );

		attris.put("CREATESTAMPA2","to_date('"+screateTime+"','yyyy-MM-dd HH24:mi:ss') " );

		updateTableAttri("WTDocument",attris,document.getPersistInfo().getObjectIdentifier().getId());

	}


	private void  	updateDocTimeAndState (WTDocument document,JSONObject json){
		Map<String,String> attris = new HashMap<String, String>();
		long  modifyTimestamp = json.optLong("modifyTime");
		String smodifyTime = simpleDateFormat.format(new Date(modifyTimestamp));
		attris.put("MODIFYSTAMPA2","to_date('"+smodifyTime+"','yyyy-MM-dd HH24:mi:ss') " );

		long createTimestamp = json.optLong("createTime");
		String screateTime = simpleDateFormat.format(new Date(createTimestamp));
		attris.put("CREATESTAMPA2","to_date('"+screateTime+"','yyyy-MM-dd HH24:mi:ss') " );

		attris.put("STATESTATE","'APPROVED'" );

		updateTableAttri("WTDocument",attris,document.getPersistInfo().getObjectIdentifier().getId());
	}
	private void updateTableAttri(String tableName ,Map<String,String> attris,long ida2a2){
		DBConn conn = null;
		try {
			conn = new DBConn();
			StringBuilder sql = new StringBuilder("update ");
			sql.append(tableName);
			sql.append(" set ");
			Set<Entry<String, String >> lattriSet =  attris.entrySet();
			for(Entry<String, String > lattri :lattriSet){
				sql.append(lattri.getKey());
				sql.append("=");
				sql.append(lattri.getValue());
				sql.append(",");
			}
			 if (!lattriSet.isEmpty()) {
				sql.deleteCharAt(sql.length() - 1);
			 }
			sql.append("  where ida2a2=").append(ida2a2);
			conn.executeUpdate(sql.toString());
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


	}

	@Override
	public void afterPropertiesSet() throws Exception {
		WebServiceCommandFactory.register(METHOD_NAME, this);
	}

}
