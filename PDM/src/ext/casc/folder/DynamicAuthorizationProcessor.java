package ext.casc.folder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.ReferenceFactory;
import wt.fc.WTReference;
import wt.fc.collections.WTValuedHashMap;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.type.TypedUtility;
import wt.util.WTException;

import com.glaway.mpm.util.ApacheZipUtil;
import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.SWXMLUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.TypeIdentifierHelper;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.copy.server.CoreMetaUtility;

import ext.casc.util.CSCPrincipal;
import ext.casc.util.CommonUtil;
import ext.casc.util.WCUtil;

public class DynamicAuthorizationProcessor extends DefaultObjectFormProcessor{
	private static final String ROLE_AUTHORISE_FORM_RECEIVE="AUTHORISE_RECEIVE";
	private static final String TYPE_AUTHORISE_FORM="AUTHORISE_FORM";

	public FormResult doOperation(NmCommandBean commandBean,
			List<ObjectBean> objectBeans) throws WTException {
	    WTUser receiveUser=null;
        String name ="";
        String userName = commandBean.getTextParameter("userName");
        if(userName.contains("(")){
        name = userName.substring(0, userName.indexOf("("));
        receiveUser = CSCPrincipal.getUserByName(name);
        }else{
            ReferenceFactory rf=new ReferenceFactory();
            WTReference wrf=rf.getReference(userName);
            receiveUser=(WTUser) wrf.getObject();
        }
        String authorisComment = commandBean
                .getTextParameter("authorisComment");
        WTPrincipal principal = SessionHelper.manager.getPrincipal();
        String authoriseName = ((WTUser) principal).getFullName();
        String receiveName = receiveUser.getFullName();
        String zhipaiName = receiveUser.getName();
        String receiveOid = receiveUser.getPersistInfo().getObjectIdentifier().toString();


	    ArrayList localArrayList = commandBean.getSelectedOidForPopup();

		FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
		result.setNextAction(FormResultAction.REFRESH_OPENER);
		List selectedList = commandBean.getSelectedOidForPopup();
		String currenUser = SessionHelper.manager.getPrincipal().getName();
		List<Persistable> tempobjs = AuthoriserPermissionUtil.getALLSelectedObjs(selectedList);
		List<Persistable> objs = AuthoriserPermissionUtil.processSelectedObjs(tempobjs);

		// 当前产品的信息
		WTContainer product =null;
		NmOid nmoid = commandBean.getActionOid();
		if(nmoid!=null){
			product =  nmoid.getContainerObject();
		}

		boolean isManagerFlag = CommonUtil.isSiteOrOrgAdmin();
		boolean isSameUserFlag = AuthoriserPermissionUtil.isSameModifyUserAndNotApprovedState(
				objs, authoriseName);
		if (isManagerFlag || isSameUserFlag) {
			boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
			for (int i = 0; i < objs.size(); i++) {
		        Persistable persistable = objs.get(i);
		        if (persistable instanceof WTDocument) {
		            WTDocument doc=(WTDocument) persistable;
		            TypeIdentifier identifier = TypedUtility.getTypeIdentifier(doc);
		            String typename = identifier.getTypename();
		            if (typename.contains("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN")|| typename.contains("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.SOPDoc")) {
		                if ("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN|casc.sast.149.reportTechnics".equals(typename)) {
		                    replaceModifierInXML(doc,zhipaiName, "baobiao",receiveOid);
	                    }else{
	                        replaceModifierInXML(doc,zhipaiName, "putong",receiveOid);
	                    }

	                }
//		            wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN
	            }
	        }
			try {
				//典道新增代码 begin
				/*try{
					ext.cirpoint.securitymgr.log.util.AuditRecordUtil.insertShouQuanLogs(objs,receiveName);
				}catch(Exception e){
					e.printStackTrace();
				}*/
				//典道新增代码end
				String authoriseFile = AuthoriserPermissionUtil.writeExcel(
						currenUser, receiveName, authorisComment, objs);
				WTDocument doc = WTDocument.newWTDocument();
				TypeIdentifier authoriseType = TypeIdentifierHelper
						.getTypeIdentifier("WCTYPE|wt.doc.WTDocument|casc.sast.149.AUTHORISE_FORM");
				doc = (WTDocument) CoreMetaUtility.setType(doc, authoriseType);
				ReferenceFactory rf = new ReferenceFactory();
				String authoriseFolder = "/Default/09授权单";
				WTContainerRef containerRef = (WTContainerRef) rf
						.getReference(product);
				if (product == null) {
					throw new WTException("Please create product");
				}

				doc.setName("授权单");
				String genNumber = genNumber(currenUser);
				WTDocument document = WTDocumentUtil.getLatestDocumentByNumber(genNumber);
				if(document != null) {
					Thread.sleep(100);
					genNumber = genNumber(currenUser);
				}
				doc.setNumber(genNumber);
				doc.setContainerReference(containerRef);
				Folder location = null;
				try {
					location = FolderHelper.service.getFolder(authoriseFolder,
							containerRef);
				} catch (Exception e) {
					location = null;
				}
				if (location == null)
					location = FolderHelper.service.saveFolderPath(
							authoriseFolder, containerRef);
				if (location != null) {
					WTValuedHashMap map = new WTValuedHashMap();
					map.put(doc, location);
					FolderHelper.assignLocations(map);
				}
				doc = (WTDocument) PersistenceHelper.manager.save(doc);
				doc = (WTDocument) PersistenceHelper.manager.refresh(doc);
				ApplicationData appData = ApplicationData
						.newApplicationData(doc);
				appData.setRole(ContentRoleType.PRIMARY);
				ContentServerHelper.service.updateContent(doc, appData,
						authoriseFile);
				//QueryResult procs = WfEngineHelper.service.getAssociatedProcesses(doc, null,null);
				/*while(procs.hasMoreElements()){
					WfProcess process = (WfProcess) procs.nextElement();
					Team team = (Team) process.getTeamId().getObject();
					team.addPrincipal(Role.toRole(ROLE_AUTHORISE_FORM_RECEIVE), receiveUser);
					team = (Team) PersistenceHelper.manager.refresh(team);
				}*/
				if(userName.contains("(")){
				AuthoriserPermissionUtil.assignUserToRole(name, doc,
						ROLE_AUTHORISE_FORM_RECEIVE);
				}else{
					AuthoriserPermissionUtil.assignUserToRole(receiveUser, doc,
							ROLE_AUTHORISE_FORM_RECEIVE);
				}
				AuthoriserPermissionUtil.setAllObjModifier(objs, WTPrincipalReference
								.newWTPrincipalReference(receiveUser),isManagerFlag,authoriseName);
				File file = new File(authoriseFile);
				if(file.exists()){
					file.delete();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				 wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
			}
			result.addFeedbackMessage(getSuccessFeedbackMessage());
		} else {
			result.setStatus(FormProcessingStatus.FAILURE);
			result.addFeedbackMessage(getFailFeedbackMessage());
		}
		if(!"".equals(currenUser)){
			SessionHelper.manager.setPrincipal(currenUser);
		}
		return result;

	}

	public static Boolean replaceModifierInXML(WTDocument document,String name,String flag,String receiveOid) {
		boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
	    try{

//	        WTUser   curentuser = (WTUser)SessionHelper.getPrincipal();
//	        String name = curentuser.getName();
	        String tempFilePath = PropertiesUtil.getWTHome() + File.separator + "temp" + File.separator
	                +  java.util.UUID.randomUUID().toString()+ File.separator;
	        String zipFileName = WTDocumentUtil.downloadDocumentPrimaryToTemp(document, tempFilePath);
	        String subFileName = zipFileName.substring(0, zipFileName.lastIndexOf("."));
	        ApacheZipUtil.decompress(tempFilePath + zipFileName, tempFilePath + subFileName);
	        File xmlFile = new File(tempFilePath + subFileName + File.separator + subFileName + ".xml");
	        InputStream inputStream = new FileInputStream(xmlFile);
	        SWXMLUtil xmlUtil = new SWXMLUtil(inputStream);
            Element rootElement = xmlUtil.getRootElement();
            if ("baobiao".equals(flag)) {
                Element techEle = (Element) rootElement.getChildren("XWReportTechnicsInfo").get(0);
                String value = techEle.getAttributeValue("creator");
                techEle.setAttribute("creator", name);
                techEle.setAttribute("creatorOid", receiveOid);
            }else{
                Element techEle = (Element) rootElement.getChildren("QMFawTechnicsInfo").get(0);
                String value = techEle.getAttributeValue("creator");
                techEle.setAttribute("creator", name);
                techEle.setAttribute("creatorOid", receiveOid);
            }
             // 替换工艺xml，打包工艺文件夹，上传工艺压缩包
            FileOutputStream  fileOutputStream = new FileOutputStream(new File(tempFilePath + subFileName + File.separator + subFileName
                    + ".xml"), false);
            Format format = Format.getPrettyFormat();
            format.setEncoding("GBK");
            XMLOutputter xmlOutput = new XMLOutputter(format);
            xmlOutput.output(xmlUtil.getDocument(), fileOutputStream);

            boolean compressflag = ApacheZipUtil.compress(tempFilePath + subFileName, tempFilePath + zipFileName);
            // 重新上传PBOM的XML
            if(compressflag){
            	  WTDocumentUtil.setPrimaryForDocument(document, zipFileName, new FileInputStream(new File(tempFilePath
                          + zipFileName)));
            }

            return true;

	    }catch(Exception e){
	        e.printStackTrace();
	    }finally{
	    	wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
	    }
	    return false;

    }

    public static synchronized String genNumber(){
		String maxNumber = null;
		try {
			maxNumber = AuthoriserPermissionUtil.getMaxDocNumber();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println(maxNumber);
		String nextNumber =  String.valueOf((Integer.valueOf(maxNumber)+1));
		StringBuffer prefix = new StringBuffer();
		for(int i=0;i< maxNumber.length()-nextNumber.length();i++){
			prefix.append(String.valueOf(0));
		}
		nextNumber = prefix.toString()+nextNumber;
		return nextNumber;
	}


	@SuppressWarnings("unused")
	public static synchronized String genNumber(String authoriseName){
		SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMddHHmmss");
		TimeZone t = TimeZone.getTimeZone("Asia/Shanghai");
		sdf.setTimeZone(t);
		//System.out.println(sdf.format(System.currentTimeMillis()));
		return sdf.format(System.currentTimeMillis())+"_"+authoriseName;
	}

	public FeedbackMessage getFailFeedbackMessage() throws WTException {
		return new FeedbackMessage(FeedbackType.FAILURE, (Locale) null,
				"创建", (ArrayList<String>) null, "创建失败(授权者不是修改者本人)");
	}
}
