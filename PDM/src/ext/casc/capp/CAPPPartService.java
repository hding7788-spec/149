package ext.casc.capp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import wt.change2.ChangeHelper2;
import wt.change2.ChangeNoticeComplexity;
import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.DocumentType;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainerHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleServerHelper;
import wt.lifecycle.State;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;
import ext.casc.change.ChangeHelper;
import ext.casc.constants.Constants;
import ext.casc.doc.CSCDoc;
import ext.casc.part.CSCPart;
import ext.casc.util.DocUtil;
import ext.casc.util.IBAHelper;


/**
 * 与天河capp集成的接口
 *
 * @author Administrator
 */
public class CAPPPartService {
    private final String PROCESS_ECN = "工艺更改单";
    private final String INNER_PROCESS_ECN = "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.CAPP_ECN";

    private final String PROCESS_PLAN = "工艺规程";
    private final String INNER_PROCESS_PLAN = "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN";

    private final String PROCESS_NOTICE = "工艺技术通知单";
    private final String INNER_PROCESS_NOTICE = "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_NOTICE";

    private final String TEMP_PROCESS_DOC = "临时工艺文件";
    private final String INNER_TEMP_PROCESS_DOC = "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.TEMP_PROCESS_DOC";

    public   String create(CAPPCraft craft)throws Exception{
        if(PROCESS_PLAN.equals(craft.getType())){//工艺规程
            return  createOrUpdateDoc(craft,INNER_PROCESS_PLAN,"03"+craft.getType());

        }else if(PROCESS_NOTICE.equals(craft.getType())){//工艺技术通知单
            return createOrUpdateDoc(craft,INNER_PROCESS_NOTICE,"04"+craft.getType());
        }else if(TEMP_PROCESS_DOC.equals(craft.getType())){//临时工艺文件
            return createOrUpdateDoc(craft,INNER_TEMP_PROCESS_DOC,"11"+craft.getType());
        }else if(PROCESS_ECN.equals(craft.getType())){//工艺更改单
        	 return createOrUpdateDoc(craft,INNER_PROCESS_ECN,"05"+craft.getType());
        }

        return "success";
    }

    public String isExistCAPPDoc(String number,String version){
    	;
		try {
			QuerySpec qs = new QuerySpec(WTDocument.class);
			SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL,
	        		number, false);
	        qs.appendSearchCondition(sc);
	        QueryResult qr = PersistenceHelper.manager.find(qs);
	        while (qr.hasMoreElements()) {
	            WTDocument document = (WTDocument) qr.nextElement();
	            String cappver = IBAHelper.getIBAStringValue(document, "CAPPVER");
	            if(cappver.equals(version)){
	            	return "true";
	            }
	        }
	        return "false";
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	return "false";
    }

    private WTDocument getDocByCappVersion(String number,String version){
    	;
		try {
			QuerySpec qs = new QuerySpec(WTDocument.class);
			SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL,
	        		number, false);
	        qs.appendSearchCondition(sc);
	        QueryResult qr = PersistenceHelper.manager.find(qs);
	        while (qr.hasMoreElements()) {
	            WTDocument document = (WTDocument) qr.nextElement();
	            String cappver = IBAHelper.getIBAStringValue(document, "CAPPVER");
	            if(cappver.equals(version)){
	            	return document;
	            }
	        }
	        return null;
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	return null;
    }

    private String createECN(CAPPCraft craft, String type, String fd) {
        Transaction trx = null;
        try {
            trx = new Transaction();
            trx.start();
            WTChangeOrder2 co = WTChangeOrder2.newWTChangeOrder2();
            co.setNumber(craft.getNumber());
            co.setName(craft.getName());
            if (type != null) {
                TypeDefinitionReference ref = TypedUtility.getTypeDefinitionReference(type);
                co.setTypeDefinitionReference(ref);
            }
            PDMLinkProduct product = null;
            if(craft.getProductName()!=null && !"".equals(craft.getProductName())){
                product =  getPDMLinkProductByName(craft.getProductName());
            }
            String folderStr = "Default/02工艺文件/"+fd;
            Folder folder =null;
            co.setContainer(product);
            try {
                folder = FolderHelper.service.getFolder(folderStr, co.getContainerReference());
            } catch (Exception e) {
                // e.printStackTrace();
            }
            if (folder == null) {
                folder = FolderHelper.service.createSubFolder(folderStr,co.getContainerReference());
            }
            FolderHelper.assignLocation(co, folder);

            LifeCycleHelper.setLifeCycle(co, LifeCycleHelper.service.getLifeCycleTemplate("149_PROCESS_ECN_LC",WTContainerHelper.service.getByPath("/")));
            State state = State.toState("INWORK");
            LifeCycleServerHelper.setState((LifeCycleManaged)co,state);
            co.setChangeNoticeComplexity(ChangeNoticeComplexity.BASIC);

            co = (WTChangeOrder2)ChangeHelper2.service.saveChangeOrder(co);


            WTDocument doc = DocUtil.getDoc(craft.getChangedCraftNum(),true);//更改后的工艺文件，与工艺更改单对应
            if(doc!=null){
                Vector collect = new Vector();
                collect.add(doc);
                ChangeHelper.addAffectedDataRemote(co,collect);
            }
            IBAHelper.setIBAStringValue(doc, "DEPT", craft.getDepartment());
            trx.commit();
            trx = null;
            return "success";
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return e.getMessage();
        }finally{
            if (trx != null) {
                trx.rollback();
            }
        }
    }

    private String createOrUpdateDoc(CAPPCraft craft,String docType,String fd)throws Exception{
        WTDocument doc =DocUtil.getDoc(craft.getNumber(),true);
        if(doc==null){
           return  createDoc(craft,docType,fd);
        }else{
           return   updateDoc(craft,docType,fd,doc);
        }

    }
    private String updateDoc(CAPPCraft craft,String docType,String fd,WTDocument doc){

        Transaction trx = null;
        try {
            trx = new Transaction();
            trx.start();
            if (WorkInProgressHelper.isCheckedOut((Workable) doc)) {
                return "checked out "+doc;
            }

            String craftor = craft.getCraftor();
            if(!"".equals(craftor)){
            	craftor = craftor.substring(0,craftor.indexOf("("));
            	SessionHelper.manager.setPrincipal(craftor);
            }
            //doc = CSCDoc.getWorkingCopyOfDoc(doc);
            if(doc == null)
				return null;
            WTDocument oldDoc = doc;
            doc = (WTDocument) VersionControlHelper.service.newVersion(doc);
            State state = State.toState(Constants.STATE_APPROVED);
            LifeCycleServerHelper.setState((LifeCycleManaged)doc,state);
            doc =  (WTDocument)PersistenceHelper.manager.save(doc);

            updateCraftPrimaryAttachment(doc,craft.getNumber()+".pdf");

            WTPart part =  getLatestWTPartByNum(craft.getPartNumber());
            Persistable refLink = null;
            if(doc!=null){
                if(part!=null){
                	WTPartDescribeLink link = CSCPart.getPartDescribeLink(part, oldDoc);
                	if(link!=null){
                		  PersistenceServerHelper.manager.remove(link);
                	}
                    refLink = WTPartDescribeLink.newWTPartDescribeLink(part,doc );
                    PersistenceServerHelper.manager.insert(refLink);
                }

                if(craft!=null&&!"".equals(craft.getChangedCraftNum())){
                	WTDocument cappDoc = getDocByCappVersion(craft.getChangedCraftNum(),craft.getChangedCraftVersion());
                	if(cappDoc!=null){
                		CSCDoc.createDocAssociateDoc(doc, cappDoc);
                	}
                }
                IBAHelper.setIBAStringValue(doc, "DEPT", craft.getDepartment());
                IBAHelper.setIBAStringValue(doc, "CAPPVER", craft.getVersion());
            }else{
                return "";
            }

            if(doc != null){
    			if ( wt.vc.wip.WorkInProgressHelper.isCheckedOut(doc, wt.session.SessionHelper.manager.getPrincipal()) )
	            	doc = (WTDocument) WorkInProgressHelper.service.checkin(doc, "capp update doc");
    		}
            trx.commit();
            trx = null;
            return "success";
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return e.getMessage();
        }finally{
            if (trx != null) {
                trx.rollback();
            }
        }
    }
    private String createDoc(CAPPCraft craft,String docType,String fd){
        Transaction trx = null;
        try {
            trx = new Transaction();
            trx.start();
            String craftor = craft.getCraftor();
            if(!"".equals(craftor)){
            	craftor = craftor.substring(0,craftor.indexOf("("));
            	SessionHelper.manager.setPrincipal(craftor);
            }


            WTDocument doc = WTDocument.newWTDocument();
            doc.setDocType(DocumentType.getDocumentTypeDefault());
            doc.setNumber(craft.getNumber());
            doc.setName(craft.getName());

            WTPart part =  getLatestWTPartByNum(craft.getPartNumber());
            WTDocument cappDoc = null;
            if(craft!=null&&!"".equals(craft.getChangedCraftNum())){
            	 cappDoc = getDocByCappVersion(craft.getChangedCraftNum(),craft.getChangedCraftVersion());
            }
            if(part!=null){
            	craft.setProductName(part.getContainerName());
            }
            if(cappDoc!=null){
            	craft.setProductName(cappDoc.getContainerName());
            }
            if("".equals(craft.getProductName())){
            	return "absence of Container";
            }
            if (docType != null) {
                TypeDefinitionReference ref = TypedUtility.getTypeDefinitionReference(docType);
                doc.setTypeDefinitionReference(ref);
            }
            PDMLinkProduct product = null;
            if(craft.getProductName()!=null && !"".equals(craft.getProductName())){
                product =  getPDMLinkProductByName(craft.getProductName());
            }
            String folderStr = "Default/02工艺文件/"+fd;
            Folder folder =null;
            doc.setContainer(product);
            try {
                folder = FolderHelper.service.getFolder(folderStr, doc.getContainerReference());
            } catch (Exception e) {
                 e.printStackTrace();
            }
            if (folder == null) {
                folder = FolderHelper.service.createSubFolder(folderStr,doc.getContainerReference());
            }
            FolderHelper.assignLocation(doc, folder);

            LifeCycleHelper.setLifeCycle(doc, LifeCycleHelper.service.getLifeCycleTemplate("149_Doc_LC",WTContainerHelper.service.getByPath("/")));
            State state = State.toState(Constants.STATE_APPROVED);
            LifeCycleServerHelper.setState((LifeCycleManaged)doc,state);
            //CSCVersion.setVersionIteration(doc, "A", "1");

            doc = (WTDocument) PersistenceHelper.manager.save(doc);

            updateCraftPrimaryAttachment(doc,craft.getNumber()+".pdf");


            Persistable refLink = null;
            if(part!=null){
                refLink = WTPartDescribeLink.newWTPartDescribeLink(part, doc);
                PersistenceServerHelper.manager.insert(refLink);
            }

        	if(cappDoc!=null){
        		CSCDoc.createDocAssociateDoc(doc, cappDoc);
        	}

            IBAHelper.setIBAStringValue( doc, "DEPT", craft.getDepartment());
            IBAHelper.setIBAStringValue(doc, "CAPPVER", craft.getVersion());
            trx.commit();

            trx = null;
            return "success";
        } catch (Exception e) {
            // TODO: handle exception
           e.printStackTrace();
           return e.getMessage();
        } finally{
            if (trx != null) {
                trx.rollback();
            }
        }
    }
    public  static PDMLinkProduct getPDMLinkProductByName(String name) throws Exception{
        PDMLinkProduct ret = null;
        QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
        qs.appendWhere(new SearchCondition(PDMLinkProduct.class,
                "containerInfo.name", SearchCondition.EQUAL, name));
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        while (qr.hasMoreElements()) {
            Persistable product = (Persistable) qr.nextElement();
            ret = (PDMLinkProduct) product;
        }
        return ret;
    }

    public  static WTPart getLatestWTPartByNum(String num) throws Exception{
        String number = num.toUpperCase();

        if (!RemoteMethodServer.ServerFlag) {
            return (WTPart) RemoteMethodServer.getDefault().invoke("getLatestWTPartByNum", CAPPPartService.class.getName(), null,
                    new Class[] {String.class},
                    new Object[] {number});
        } else {
            WTPart part = null;

            boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(true);
            try {
                QuerySpec spec = new QuerySpec(WTPart.class);
                spec.appendWhere(
                        new SearchCondition(WTPart.class,
                                WTPart.NUMBER, SearchCondition.EQUAL, number), new int[] { 0 });

                QueryResult qr = PersistenceHelper.manager.find(spec);
                if (qr.hasMoreElements()){
                    WTPart document = (WTPart)qr.nextElement();
                    QueryResult qr2 = VersionControlHelper.service.allIterationsOf(document.getMaster());
                    if(qr2.hasMoreElements()){
                        part = (WTPart)qr2.nextElement();
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            } finally {
                SessionServerHelper.manager.setAccessEnforced(enforce);
            }

            return part;
        }
    }
    public  String uploadCAPPPDF(HttpServletRequest request){
    	String retS = "success";
    	try {
	    	InputStream is = request.getInputStream();
	    	SAXReader reader = new SAXReader();
            Document document =  reader.read(is);
            Element root = document.getRootElement();
            for (Iterator<Element> i = root.elementIterator( "File" ); i.hasNext(); ) {
            	 Element fileElement = (Element) i.next();
            	 CAPPCraft craft = new CAPPCraft();
            	 String fileName = "";
            	 String number = "";
            	 String base64 ="";
            	 StringBuilder sb = new StringBuilder("cappLogs=");
            	 for(Iterator<Element> j = fileElement.elementIterator(); j.hasNext();){
            		 Element element = j.next();
            		 String name = element.getName();
            		 if("number".equalsIgnoreCase(name)){
            			 number = element.getStringValue().toString();
            			 sb.append("number:").append(number).append(",");
            			 craft.setNumber(element.getStringValue().toString());
            		 }else  if("name".equalsIgnoreCase(name)){
            			 craft.setName(element.getStringValue().toString());
            			 sb.append("name:").append(name).append(",");
            		 }else  if("partNumber".equalsIgnoreCase(name)){
            			 craft.setPartNumber(element.getStringValue().toString());
            			 sb.append("partNumber:").append(craft.getPartNumber()).append(",");
            		 }else  if("fileSate".equalsIgnoreCase(name)){
            			 craft.setFileSate(element.getStringValue().toString());
            			 sb.append("fileSate:").append(craft.getFileSate()).append(",");
            		 }else  if("type".equalsIgnoreCase(name)){
            			 craft.setType(element.getStringValue().toString());
            			 sb.append("type:").append(craft.getType()).append(",");
            		 }else  if("version".equalsIgnoreCase(name)){
            			 craft.setVersion(element.getStringValue().toString());
            			 sb.append("version:").append(craft.getVersion()).append(",");
            		 }else  if("changedCraftNum".equalsIgnoreCase(name)){
            			 craft.setChangedCraftNum(element.getStringValue().toString());
            			 sb.append("changedCraftNum:").append(craft.getChangedCraftNum()).append(",");
            		 }else  if("changedCraftVersion".equalsIgnoreCase(name)){
            			 craft.setChangedCraftVersion(element.getStringValue().toString());
            			 sb.append("changedCraftVersion:").append(craft.getChangedCraftVersion()).append(",");
            		 }else  if("craftor".equalsIgnoreCase(name)){
            			 craft.setCraftor(element.getStringValue().toString());
            			 sb.append("craftor:").append(craft.getCraftor()).append(",");
            		 }else  if("department".equalsIgnoreCase(name)){
            			 craft.setDepartment(element.getStringValue().toString());
            			 sb.append("department:").append(craft.getDepartment()).append(",");
            		 }else  if("ProductName".equalsIgnoreCase(name)){
            			 craft.setProductName(element.getStringValue().toString());
            			 sb.append("ProductName:").append(craft.getProductName()).append(",");
            		 }else  if("FileName".equalsIgnoreCase(name)){
            			 fileName = element.getStringValue().toString();
            			 sb.append("number:").append(fileName).append(",");
            		 }else  if("FileBase64".equalsIgnoreCase(name)){
            			 base64 = element.getStringValue().toString();
            		 }
            	 }
            	 System.out.println(sb);
            	 WTProperties props = WTProperties.getLocalProperties();
		         String tempFolder = props.getProperty("wt.temp");
		         String filePath = tempFolder + File.separator + number+".pdf";
		         byte[] b =  Base64.decodeBase64(base64.getBytes());
		         FileOutputStream fis = new FileOutputStream(new File(filePath));

		         fis.write(b, 0, b.length);
		         fis.flush();
		         fis.close();
		         retS = create(craft);
            }


    	} catch (Exception e) {
	        e.printStackTrace();
	        return e.getMessage();
	    }
    	return retS;
    	/*if (isMultipart) {
    	    try {
    	        FileItemFactory factory = new DiskFileItemFactory();
    	        ServletFileUpload upload = new ServletFileUpload(factory);

    	        // 得到所有的表单域，它们目前都被当作FileItem
    	        List<FileItem> fileItems = upload.parseRequest(request);
    	        Iterator<FileItem> iter = fileItems.iterator();
    	        Map<String,String> fieldMap = new HashMap<String,String>();
    	        // 依次处理每个表单域
    	        while (iter.hasNext()) {
    	            FileItem item = (FileItem) iter.next();

    	            if(item.isFormField()){
    	                // 如果item是正常的表单域
    	                String name = item.getFieldName();
    	                String value = item.getString();
    	                fieldMap.put(name, value);
    	            }
    	            else{
    	                // 如果item是文件上传表单域
    	                // 获得文件名及路径
    	                String fileName = item.getName();
    	                if (fileName != null) {
    						InputStream is = item.getInputStream();
    						WTProperties props = WTProperties.getLocalProperties();
    			            String tempFolder = props.getProperty("wt.temp");
    			            String filePath = tempFolder + File.separator + fileName;
    			            FileOutputStream fis = new FileOutputStream(new File(filePath));
    			            byte[] b = new byte[1024];
    			            int count = -1;
    			            while ((count = is.read(b, 0, 1024)) > 0) {
    			            	fis.write(b, 0, count);
    			            }
    			            fis.flush();
    			            fis.close();
    			            is.close();
    	                }
    	            }
    	        }
    	    } catch (Exception e) {
    	        e.printStackTrace();
    	    }*/

    }

    public static boolean isPdfExist(String fileName) throws IOException{
    	WTProperties props = WTProperties.getLocalProperties();
		String tempFolder = props.getProperty("wt.temp");
		String filePath = tempFolder + File.separator + fileName;
		File file = new File(filePath);
		return file.exists();
    }
    @SuppressWarnings("rawtypes")
	public static void updateCraftPrimaryAttachment(ContentHolder contentholder, String fileName) throws Exception{
			boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
			contentholder = ContentHelper.service.getContents(contentholder);
			WTProperties props = WTProperties.getLocalProperties();
			String tempFolder = props.getProperty("wt.temp");
			String filePath = tempFolder + File.separator + fileName;
			File file = new File(filePath);
			if(file.exists()){
				Transaction tx = new Transaction();
				tx.start();

				QueryResult qr = ContentHelper.service.getContentsByRole(contentholder, ContentRoleType.PRIMARY);
	            while (qr.hasMoreElements()) {
	                ApplicationData ap = (ApplicationData) qr.nextElement();
	                ContentServerHelper.service.deleteContent(contentholder,
	                		ap);
	            }

				PersistenceHelper.manager.lockAndRefresh(contentholder);
				ApplicationData appData = ApplicationData.newApplicationData(contentholder);
				appData.setFileName(fileName);
				appData.setRole(ContentRoleType.PRIMARY);
				appData.setDescription(String.valueOf(Calendar.getInstance()
						.getTimeInMillis()));
				appData.setComments("自动生成");
				appData = ContentServerHelper.service.updateContent(contentholder, appData,
						new FileInputStream(file), true);
				tx.commit();
				tx = null;
				file.deleteOnExit();
				PersistenceHelper.manager.refresh(contentholder);
			}
			SessionServerHelper.manager.setAccessEnforced(enforce);
	}
}
