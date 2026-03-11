package ext.casc.erp;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.iba.value.IBAHolder;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTRuntimeException;
import wt.workflow.engine.WfProcess;
import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.util.IBAUtil;

public class ERPUtil {


	/**
	 * 获取批量签审单数据
	 * @param obj
	 * @param oid
	 * @param process
	 * @return
	 * @throws Exception
	 */
	public static Map<String,String> getProcessEnvelopeMap(Object obj,String oid,WfProcess process) throws Exception{
		Map<String,String> map=new HashMap<String,String>();
		ProcessEnvelope packageEN=null;
		if(obj instanceof ProcessEnvelope){
			packageEN=(ProcessEnvelope) obj;
			QueryResult qr = PersistenceHelper.manager.navigate(packageEN, "theRevisionControlled",EnvelopeMemberLink.class, false);
			WTDocument doc=null;
			RevisionControlled obj2 = null;
			for (; qr.hasMoreElements();) {
	            obj2 = ((EnvelopeMemberLink) qr.nextElement()).getRevisionControlled();
	            if (obj2 != null) {
	            	if(obj2 instanceof WTDocument){
	            		doc=(WTDocument) obj2;
	            		break;
	            	}
	            	}
	            }

			map.put("objType", "ProcessEnvelope");
			map.put("number", packageEN.getNumber());
			map.put("name", packageEN.getName());
			map.put("productId", packageEN.getContainerName());
			String docType=TypedUtilityServiceHelper.service.getLocalizedTypeName(doc, Locale.CHINA);
			map.put("docType", docType);
			map.put("ownerName", packageEN.getCreatorFullName());
			map.put("ownerLoginName", packageEN.getCreatorName());
			SimpleDateFormat myFmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.TimeZone timeChina=java.util.TimeZone.getTimeZone("Asia/Shanghai");
			myFmt.setTimeZone(timeChina);
			String time="";
			if(packageEN.getCreateTimestamp()!=null){
			time=myFmt.format(packageEN.getCreateTimestamp());
			}
			map.put("creatTime", time);
			map.put("description", packageEN.getDescription());
			IBAUtil ibaCon=new IBAUtil((IBAHolder)packageEN.getContainer());
			map.put("productIID", ibaCon.getIBAValue("DAJCCPDH"));
			IBAUtil objIba=new IBAUtil(packageEN);
			IBAUtil iba=new IBAUtil(doc);
			map.put("department", iba.getIBAValue("BZBM"));
			String phaseName=iba.getIBAValue("DOCUMENTPHASE");

			String miji=iba.getIBAValue("DOCUMENTSECRET");
			if(miji==null||"".equals(miji)){
				miji=iba.getIBAValue("document_secret");
			}
			map.put("securityLevelName", miji);
			map.put("state", doc.getLifeCycleState().getDisplay(Locale.CHINA));

			String sd="";
			map.put("toCompany", sd);
			//String versionNumber=doc.getDisplayIdentifier().toString().split(",")[doc.getDisplayIdentifier().toString().split(",").length-1];
			//map.put("versionNo", versionNumber);
		}
		return map;
	}

	public static String getCHPhase(String phaseName){
		if(phaseName==null||"".equals(phaseName)){
			return "";
		}else if(phaseName.startsWith("M")){
			return "模样";
		}else if(phaseName.startsWith("C")){
			return "初样";
		}else if(phaseName.startsWith("Z")){
			return "正样";
		}else if(phaseName.startsWith("S")){
			return "试样";
		}
		return "";
	}
	public static void createElement(Element root,String name,String key,String value){
		Element prop=DocumentHelper.createElement("prop");
		prop.addElement("propname").addText(name);
		prop.addElement("propsign").addText(key);
		if(value == null){
			value = "";
		}
		prop.addElement("propvalue").addText(value);
		root.add(prop);
	}

	/**
	 * 创建普通文档xml
	 * @param map
	 * @param documentOid
	 * @param processOid
	 * @return
	 * @throws Exception
	 */
	public static String createCommonDocXml(WTDocument doc,String pdmtype) throws Exception{
		String documentOid = doc.getPersistInfo().getObjectIdentifier().getId()+"";
		IBAUtil iba=new IBAUtil(doc);
		SimpleDateFormat myFmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Element root=DocumentHelper.createElement("archman");

		Element prop=DocumentHelper.createElement("DocSource");
		prop.addElement("company").addText("国睿信维");
		prop.addElement("system").addText("windchill");
		if("0".equals(pdmtype)){
			prop.addElement("type").addText("现行文档");
		}else{
			prop.addElement("type").addText("归档文档");
		}

		root.add(prop);
		String subPath = "";
		if("0".equals(pdmtype)){
			subPath = doc.getNumber();
			subPath = subPath.replaceAll("/", "_");
			createElement(root,"内部标识","IID",doc.getNumber());
			createElement(root,"文件夹ID","Folder_ID","2627");
		}else {
			subPath = doc.getPersistInfo().getObjectIdentifier().getId()+"";
			createElement(root,"内部标识","IID",doc.getPersistInfo().getObjectIdentifier().getId()+"");
			createElement(root,"文件夹ID","Folder_ID","2628");
		}
		createElement(root,"现行库还是预立卷","PDMTYPE",pdmtype);
		createElement(root,"文件来源","InOrOut","0");
		createElement(root,"编号","BookNo",doc.getNumber());
		createElement(root,"产品代号","productId",iba.getIBAValue("PINDEX"));
		createElement(root,"产品","ProductIID",doc.getContainerName());
		createElement(root,"阶段","Phase",iba.getIBAValue("PHASE_CODE"));
		createElement(root,"分系统","SubSystem","");
		createElement(root,"名称","Name",doc.getName());
		createElement(root,"密级","SecLev",iba.getIBAValue("SECRET"));
		String docType=TypedUtilityServiceHelper.service.getLocalizedTypeName(doc, Locale.CHINA);
		createElement(root,"文件类型","DocType",docType);
		createElement(root,"页数","PageCount",iba.getIBAValue("PAGE"));
		createElement(root,"份数","Count","1");
		createElement(root,"入库份数","EnrollCount","1");
		createElement(root,"文件简字","GenWord","");
		createElement(root,"编写人","Creator",doc.getCreatorFullName());
		createElement(root,"编制单位","CreateUnit","149");
		createElement(root,"编制日期","CreateDate",myFmt.format(doc.getCreateTimestamp()));
		createElement(root,"是否评审","IsCheck","0");
		createElement(root,"格式","Format","1");
		createElement(root,"备注","Notes","");
		createElement(root,"外来文原编号","FlowNo","");
		createElement(root,"版本","VerId",doc.getVersionInfo().getIdentifier().getValue()+"."+doc.getIterationInfo().getIdentifier().getValue());
		createElement(root,"编写人标识","CreatorIID",doc.getCreator().getName());
		createElement(root,"接收人标识","AcceptorIID","");
		createElement(root,"接收人","Acceptor","");
		createElement(root,"状态","Status",doc.getState().getState().getDisplay(Locale.CHINA));
		createElement(root,"阶段内部标识","PhaseIID",iba.getIBAValue("PHASE_CODE"));


		WTProperties wtp = WTProperties.getLocalProperties();
		String wtTemp = wtp.getProperty("wt.temp");
		String pathID = java.util.UUID.randomUUID().toString();
		String tempPath=wtTemp+File.separator+pathID;

		writeXML(root,tempPath,subPath);
		if("1".equals(pdmtype)){
			ContentHolder contentholder =null;
			contentholder = ContentHelper.service.getContents(doc);
			@SuppressWarnings("unchecked")
			Vector<ApplicationData> vec=ContentHelper.getApplicationData((FormatContentHolder) contentholder);
			if(vec!=null&&vec.size()>0){
				for(int i=0;i<vec.size();i++){
					ApplicationData apdata=vec.get(i);
					if (!"SECONDARY".equalsIgnoreCase(apdata.getRole().toString()))
	                    continue;// 不是附件

					if(apdata.getFileName().startsWith("Print_")){
						ContentServerHelper.service.writeContentStream(apdata, tempPath+File.separator+apdata.getFileName());
					}
				}
			}
		}
		String zipPath=createZip(tempPath,subPath);
		return zipPath;

	}

	public static String createCommonDocXml(WTChangeOrder2 doc,String pdmtype) throws Exception{
		String documentOid = doc.getPersistInfo().getObjectIdentifier().getId()+"";
		IBAUtil iba=new IBAUtil(doc);
		SimpleDateFormat myFmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Element root=DocumentHelper.createElement("archman");

		Element prop=DocumentHelper.createElement("DocSource");
		prop.addElement("company").addText("国睿信维");
		prop.addElement("system").addText("windchill");
		if("0".equals(pdmtype)){
			prop.addElement("type").addText("现行文档");
		}else{
			prop.addElement("type").addText("归档文档");
		}
		root.add(prop);
		String subPath = "";
		if("0".equals(pdmtype)){
			subPath = doc.getNumber();
			createElement(root,"内部标识","IID",doc.getNumber());
			createElement(root,"文件夹ID","Folder_ID","2627");
		}else {
			subPath = doc.getPersistInfo().getObjectIdentifier().getId()+"";
			createElement(root,"内部标识","IID",doc.getPersistInfo().getObjectIdentifier().getId()+"");
			createElement(root,"文件夹ID","Folder_ID","2628");
		}
		createElement(root,"现行库还是预立卷","PDMTYPE",pdmtype);
		createElement(root,"文件来源","InOrOut","0");
		createElement(root,"编号","BookNo",doc.getNumber());
		createElement(root,"产品代号","productId","");
		createElement(root,"产品","ProductIID",doc.getContainerName());
		createElement(root,"阶段","Phase",iba.getIBAValue("PHASE_CODE"));
		createElement(root,"分系统","SubSystem","");
		createElement(root,"名称","Name",doc.getName());
		createElement(root,"密级","SecLev","");
		String docType=TypedUtilityServiceHelper.service.getLocalizedTypeName(doc, Locale.CHINA);
		createElement(root,"文件类型","DocType",docType);
		createElement(root,"页数","PageCount","");
		createElement(root,"份数","Count","1");
		createElement(root,"入库份数","EnrollCount","1");
		createElement(root,"文件简字","GenWord","");
		createElement(root,"编写人","Creator",doc.getCreatorFullName());
		createElement(root,"编制单位","CreateUnit","149");
		createElement(root,"编制日期","CreateDate",myFmt.format(doc.getCreateTimestamp()));
		createElement(root,"是否评审","IsCheck","0");
		createElement(root,"格式","Format","1");
		createElement(root,"备注","Notes","");
		createElement(root,"外来文原编号","FlowNo","");
		createElement(root,"版本","VerId",doc.getVersionInfo().getIdentifier().getValue()+"."+doc.getIterationInfo().getIdentifier().getValue());
		createElement(root,"编写人标识","CreatorIID",doc.getCreator().getName());
		createElement(root,"接收人标识","AcceptorIID","");
		createElement(root,"接收人","Acceptor","");
		createElement(root,"状态","Status",doc.getState().getState().getDisplay(Locale.CHINA));
		createElement(root,"阶段内部标识","PhaseIID",iba.getIBAValue("PHASE_CODE"));


		WTProperties wtp = WTProperties.getLocalProperties();
		String wtTemp = wtp.getProperty("wt.temp");
		String pathID = java.util.UUID.randomUUID().toString();
		String tempPath=wtTemp+File.separator+pathID;

		writeXML(root,tempPath,subPath);
		if("1".equals(pdmtype)){
			ContentHolder contentholder =null;
			contentholder = ContentHelper.service.getContents(doc);
			@SuppressWarnings("unchecked")
			Vector<ApplicationData> vec=ContentHelper.getApplicationData( contentholder);
			if(vec!=null&&vec.size()>0){
				for(int i=0;i<vec.size();i++){
					ApplicationData apdata=vec.get(i);
					if (!"SECONDARY".equalsIgnoreCase(apdata.getRole().toString()))
	                    continue;// 不是附件
					if(apdata.getFileName().startsWith("Print_")){
						ContentServerHelper.service.writeContentStream(apdata, tempPath+File.separator+apdata.getFileName());
					}
				}
			}
		}
		String zipPath=createZip(tempPath,subPath);
		return zipPath;
	}



	public static void deleteFiles(File dir) {
		if(dir == null || !dir.exists() || !dir.isDirectory()) {
			return;
		}
		for(File file:dir.listFiles()) {
			if(file.isFile()) {
				file.delete();
			} else if(file.isDirectory()) {
				deleteFiles(file);
			}
		}
		dir.delete();
	}


	/**
	 * 写入xml文档
	 * @param root
	 * @param oid
	 * @throws IOException
	 */
	public static void writeXML(Element root,String tempPath ,String oid) throws IOException{
		XMLWriter write=null;
		Document document=DocumentHelper.createDocument(root);
		File file=new File(tempPath);
		if(!file.exists()){
			file.mkdir();
		}
		FileOutputStream os=new FileOutputStream(tempPath+File.separator+oid+".xml");
		write=new XMLWriter(os,OutputFormat.createPrettyPrint());
		write.write(document);
		write.close();
	}

	/**
	 * 打压缩包
	 * @param oid
	 * @return
	 * @throws IOException
	 */
	public static String createZip(String temppath,String oid) throws IOException{
		InputStream is = null;
		String zipFile = temppath+File.separator+oid+".zip";
        ZipOutputStream zos = new ZipOutputStream(new File(zipFile));
        zos.setEncoding("GBK");
        byte[] buf = new byte[1024];
        File fileList = new File(temppath);
        File[] files = fileList.listFiles();

        if (files != null) {

            for (File file : files) {

                if (!file.isDirectory()) {

                    is = new FileInputStream(file);

                    if (is != null) {
                    	if(file.getName().endsWith(".xml")){
                    		zos.putNextEntry(new ZipEntry(file.getName()));
                    		zos.setEncoding("GBK");
                            int len = 0;
                            while ((len = is.read(buf)) >= 0) {
                                zos.write(buf, 0, len);
                            }
                            is.close();
                    	}else if(file.getName().startsWith("Print")){
                    		zos.putNextEntry(new ZipEntry(oid+File.separator+file.getName()));
                    		zos.setEncoding("GBK");
                            int len = 0;
                            while ((len = is.read(buf)) >= 0) {
                                zos.write(buf, 0, len);
                            }
                            is.close();
                    	}

                    }
                }
            }
        }
        zos.close();
        return zipFile;
	}


	/**
	 * 删除压缩包临时文件
	 * @param zipPath
	 */
	public static void deleteERPTemp(String zipPath){
		zipPath=zipPath.replaceAll(".zip", "");
		File file=new File(zipPath);
		if(file.exists()){
			if(file.isDirectory()){
				File[] files=file.listFiles();
				for(int i=0;i<files.length;i++){
					files[i].delete();
				}
				file.delete();
			}
		}
	}

	public static Object getObjectByOid(String oid) throws WTRuntimeException, WTException{
		if(oid==null||"".equals(oid)){
			return null;
		}
		ReferenceFactory rf=new ReferenceFactory();
		Object obj= rf.getReference("OR:"+oid).getObject();
		return obj;
	}


	/*public static String updownloadFile(String filePath) {
		String endpoint = "http://10.124.0.22/ArchivesFile/Services/DataInterfaceService.asmx";
		DataInterfaceServiceLocator locator = new DataInterfaceServiceLocator();
		String result = "";
		InputStream inputStream = null;
		DataInterfaceServiceSoapStub sstb;
		try {
			sstb = new DataInterfaceServiceSoapStub(new URL(endpoint), locator);
			String sb = Md5.fileMD5(filePath);
			String fileName = Tools.getFileName(filePath);
			long fileSize = 0;
			long blockOffset = 0;
			long blockSize = 65536; // 64*1024

			File file = new File(filePath);
			fileSize = file.length();

			inputStream = new FileInputStream(file);
			inputStream.skip(blockOffset);
			while (blockOffset != fileSize) {
				// 计算字节块大小
				if (blockOffset + blockSize > fileSize) {
					blockSize = fileSize - blockOffset;
				}

				// 构造字节数组
				byte[] fileBlock = new byte[(int) blockSize];
				inputStream.read(fileBlock, 0, fileBlock.length);

				// 是否是最后一个文件块
				boolean lastBlock = ((blockOffset + blockSize) == fileSize) ? true
						: false;

				result += sstb.uploadDIMonitorFile(fileName, null, fileBlock,
						blockOffset, lastBlock, sb) + ";";

				blockOffset = blockOffset + blockSize;
			}
			inputStream.close();
			String[] results = result.split(";");
			for (int i = 0; i < results.length; i++) {
				if (!"S0001".equals(results[i])) {
					if (results[i].equals("E0000")) {
						return "E0";
					} else if (results[i].equals("E0001")) {
						return "E1";
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return "ERROR";
		} finally {
			try {
				inputStream.close();
			} catch (Exception ex) {
			}
		}

		return "S0";
	}
*/
}
