package ext.casc.folder;

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
import com.ptc.netmarkets.user.NmUser;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.work.NmWorkItemCommands;
import com.ptc.windchill.enterprise.copy.server.CoreMetaUtility;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import ext.casc.util.CSCPrincipal;
import ext.casc.util.CommonUtil;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
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
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.vc.VersionControlHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
//import ext.cirpoint.securitymgr.log.util.AuditRecordUtil;

/**
 * 1.支持跨产品授权，若被授权人不在产品团队，则跳过授权
 * 2.不同产品的授权，需要生成不同的授权单，分别放在不同的产品库里
 */
public class QuickDynamicAuthorizationProcessor extends DefaultObjectFormProcessor {
    private static final String ROLE_AUTHORISE_FORM_RECEIVE = "AUTHORISE_RECEIVE";
    private static final String TYPE_AUTHORISE_FORM = "AUTHORISE_FORM";
    private static int totalCount;
    private static int successCount;
    private static int failCount;
    private static int dealCount;
    private static List<Persistable> persistables;

    public FormResult doOperation(NmCommandBean commandBean,
                                  List<ObjectBean> objectBeans) throws WTException {
        boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
        //1、获取数据
        totalCount = 0;
        successCount = 0;
        failCount = 0;
        dealCount = 0;
        persistables = new ArrayList<Persistable>();
        WTUser receiveUser = null;
        String name = "";
        String userName = commandBean.getTextParameter("userName");
        if (userName.contains("(")) {
            name = userName.substring(0, userName.indexOf("("));
            receiveUser = CSCPrincipal.getUserByName(name);
        } else {
            ReferenceFactory rf = new ReferenceFactory();
            WTReference wrf = rf.getReference(userName);
            receiveUser = (WTUser) wrf.getObject();
        }
        String authorisComment = commandBean
                .getTextParameter("authorisComment");
        WTPrincipal principal = SessionHelper.manager.getPrincipal();
        String authoriseName = ((WTUser) principal).getFullName();
        String receiveName = receiveUser.getFullName();
        String zhipaiName = receiveUser.getName();
        String receiveOid = receiveUser.getPersistInfo().getObjectIdentifier().toString();

        FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
        result.setNextAction(FormResultAction.REFRESH_OPENER);
        List selectedList = commandBean.getSelectedOidForPopup();
        String currenUser = SessionHelper.manager.getPrincipal().getName();
        List<Persistable> tempobjs = AuthoriserPermissionUtil.getALLSelectedObjs(selectedList);
        List<Persistable> objs = AuthoriserPermissionUtil.processSelectedObjs(tempobjs);
        totalCount = tempobjs.size();
        if (objs.isEmpty()) {
            throw new WTException("没有授权数据！");
        }
        boolean isManagerFlag = CommonUtil.isSiteOrOrgAdmin();
        //取消不同产品库的校验
//		String message = AuthoriserPermissionUtil.checkObjests(objs,authoriseName,isManagerFlag);
//		if(message!=null&&!"".equals(message)){
//			throw new WTException(message);
//		}
        // 当前产品的信息
//		WTContainer product =null;
//		NmOid nmoid = commandBean.getActionOid();
//		if(nmoid!=null){
//			product =  nmoid.getContainerObject();
//		}

        //2、组织数据
        long startTime;
        long endTime;
        Map<WTContainer, List<Persistable>> wtContainerListMap = new HashMap<WTContainer, List<Persistable>>();
        List<Persistable> persistableList;
        WTContainer wtContainer = null;
        String message = "";
        WTPrincipalReference principalRef = null;
        startTime = System.currentTimeMillis();
        for (Persistable persistable : objs) {
            if (persistable instanceof WTDocument) {
                WTDocument doc = (WTDocument) persistable;
                wtContainer = doc.getContainer();
                principalRef = VersionControlHelper.getIterationModifier(doc);
            } else if (persistable instanceof WTPart) {
                WTPart part = (WTPart) persistable;
                wtContainer = part.getContainer();
                principalRef = VersionControlHelper.getIterationModifier(part);
            } else if (persistable instanceof EPMDocument) {
                EPMDocument epmDocument = (EPMDocument) persistable;
                wtContainer = epmDocument.getContainer();
                principalRef = VersionControlHelper.getIterationModifier(epmDocument);
            } else if (persistable instanceof MPMProcessPlan) {
                MPMProcessPlan mpmProcessPlan = (MPMProcessPlan) persistable;
                wtContainer = mpmProcessPlan.getContainer();
                principalRef = VersionControlHelper.getIterationModifier(mpmProcessPlan);
            }
            //如果修改者不是自己，则跳过
            String modifyName = principalRef.getFullName();
            if (!isManagerFlag && modifyName != null && !authoriseName.equalsIgnoreCase(modifyName)) {
                continue;
            }
            if (wtContainer != null) {
                if (wtContainerListMap.containsKey(wtContainer)) {
                    persistableList = wtContainerListMap.get(wtContainer);
                    if (!persistableList.contains(persistable)) {
                        persistableList.add(persistable);
                    }
                } else {
                    persistableList = new ArrayList<Persistable>();
                    persistableList.add(persistable);
                    wtContainerListMap.put(wtContainer, persistableList);
                }
                dealCount++;

            }
        }
        endTime = System.currentTimeMillis();
        System.out.println("共" + objs.size() + "条数据，组织数据耗时：" + (endTime - startTime) + "ms");
        //3、处理数据
        for (Map.Entry<WTContainer, List<Persistable>> entry : wtContainerListMap.entrySet()) {
            if (persistables != null) {
                persistables.clear();
            }
            wtContainer = entry.getKey();
            String msg = checkIsInContainerTeam(wtContainer, zhipaiName);
            if (msg != null && !msg.isEmpty()) {
                message = message.isEmpty() ? msg : message + "," + msg;
                failCount += entry.getValue().size();
                continue;
            }
            persistableList = entry.getValue();
            //3.1 修改工艺文件xml
            successCount += persistableList.size();
            for (Persistable persistable : persistableList) {
                if (persistable instanceof WTDocument) {
                    WTDocument doc = (WTDocument) persistable;
                    TypeIdentifier identifier = TypedUtility.getTypeIdentifier(doc);
                    String typename = identifier.getTypename();
                    if (typename.contains("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN")
                    		|| typename.contains("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.SOPDoc")) {
                        if ("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN|casc.sast.149.reportTechnics".equals(typename)) {
                            replaceModifierInXML(doc, zhipaiName, "baobiao", receiveOid);
                        } else {
                            replaceModifierInXML(doc, zhipaiName, "putong", receiveOid);
                        }
                    }
                }
            }
            //3.2 创建授权单

            try {

                //典道新增代码 begin
//			try{
//				ext.cirpoint.securitymgr.log.util.AuditRecordUtil.insertShouQuanLogs(objs,receiveName);
//			}catch(Exception e){
//				e.printStackTrace();
//			}
                //典道新增代码end
                String authoriseFile = AuthoriserPermissionUtil.writeExcel(currenUser, receiveName, authorisComment, persistables);
                WTDocument doc = WTDocument.newWTDocument();
                TypeIdentifier authoriseType = TypeIdentifierHelper.getTypeIdentifier("WCTYPE|wt.doc.WTDocument|casc.sast.149.AUTHORISE_FORM");
                doc = (WTDocument) CoreMetaUtility.setType(doc, authoriseType);
                ReferenceFactory rf = new ReferenceFactory();
                String authoriseFolder = "/Default/09授权单";
                WTContainerRef containerRef = (WTContainerRef) rf.getReference(wtContainer);

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
                    location = FolderHelper.service.getFolder(authoriseFolder, containerRef);
                } catch (Exception e) {
                    location = null;
                }
                if (location == null)
                    location = FolderHelper.service.saveFolderPath(authoriseFolder, containerRef);
                if (location != null) {
                    WTValuedHashMap map = new WTValuedHashMap();
                    map.put(doc, location);
                    FolderHelper.assignLocations(map);
                }
                doc = (WTDocument) PersistenceHelper.manager.save(doc);
                doc = (WTDocument) PersistenceHelper.manager.refresh(doc);
                ApplicationData appData = ApplicationData.newApplicationData(doc);
                appData.setRole(ContentRoleType.PRIMARY);
                ContentServerHelper.service.updateContent(doc, appData, authoriseFile);

                if (userName.contains("(")) {
                    AuthoriserPermissionUtil.assignUserToRole(name, doc, ROLE_AUTHORISE_FORM_RECEIVE);
                } else {
                    AuthoriserPermissionUtil.assignUserToRole(receiveUser, doc, ROLE_AUTHORISE_FORM_RECEIVE);
                }
                AuthoriserPermissionUtil.setAllObjModifier(persistables, WTPrincipalReference.newWTPrincipalReference(receiveUser), isManagerFlag, authoriseName);
                File file = new File(authoriseFile);
                if (file.exists()) {
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
            }
        }
//		if(!message.isEmpty()){
//			message = "接收人：" + receiveName + ",不在该授权的产品团队("+message+")中";
//			result.addFeedbackMessage(getFailFeedbackMessage(message));
//		}else{
//			result.addFeedbackMessage(getSuccessFeedbackMessage());
//		}
//        message = "接收人：" + receiveName + ",不在该授权的产品团队("+message+")中";
        result.addFeedbackMessage(getFeedbackMessage(message, receiveName));

        if (!"".equals(currenUser)) {
            SessionHelper.manager.setPrincipal(currenUser);
        }
        return result;

    }

    private String checkIsInContainerTeam(WTContainer wtContainer, String receiverName) throws WTException {
        String msg = "";
        List<String> userList = getALlUsers(wtContainer);
        if (!userList.contains(receiverName)) {
            msg = wtContainer.getName();
        }
        return msg;
    }

    public static Boolean replaceModifierInXML(WTDocument document, String name, String flag, String receiveOid) {
        boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
        try {
            persistables = new ArrayList<>();
//	        WTUser   curentuser = (WTUser)SessionHelper.getPrincipal();
//	        String name = curentuser.getName();
            String tempFilePath = PropertiesUtil.getWTHome() + File.separator + "temp" + File.separator
                    + java.util.UUID.randomUUID().toString() + File.separator;
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
            } else {
                Element techEle = (Element) rootElement.getChildren("QMFawTechnicsInfo").get(0);
                String value = techEle.getAttributeValue("creator");
                techEle.setAttribute("creator", name);
                techEle.setAttribute("creatorOid", receiveOid);
            }
            // 替换工艺xml，打包工艺文件夹，上传工艺压缩包
            FileOutputStream fileOutputStream = new FileOutputStream(new File(tempFilePath + subFileName + File.separator + subFileName
                    + ".xml"), false);
            Format format = Format.getPrettyFormat();
            format.setEncoding("GBK");
            XMLOutputter xmlOutput = new XMLOutputter(format);
            xmlOutput.output(xmlUtil.getDocument(), fileOutputStream);

            boolean compressflag = ApacheZipUtil.compress(tempFilePath + subFileName, tempFilePath + zipFileName);
            // 重新上传PBOM的XML
            if (compressflag) {
                WTDocumentUtil.setPrimaryForDocument(document, zipFileName, new FileInputStream(new File(tempFilePath
                        + zipFileName)));
            }
            successCount += 1;
            persistables.add(document);
            return true;

        } catch (Exception e) {
            failCount += 1;
            e.printStackTrace();
        } finally {
            wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
        }
        return false;

    }

    public static synchronized String genNumber() {
        String maxNumber = null;
        try {
            maxNumber = AuthoriserPermissionUtil.getMaxDocNumber();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(maxNumber);
        String nextNumber = String.valueOf((Integer.valueOf(maxNumber) + 1));
        StringBuffer prefix = new StringBuffer();
        for (int i = 0; i < maxNumber.length() - nextNumber.length(); i++) {
            prefix.append(String.valueOf(0));
        }
        nextNumber = prefix.toString() + nextNumber;
        return nextNumber;
    }


    @SuppressWarnings("unused")
    public static synchronized String genNumber(String authoriseName) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        TimeZone t = TimeZone.getTimeZone("Asia/Shanghai");
        sdf.setTimeZone(t);
        //System.out.println(sdf.format(System.currentTimeMillis()));
        return sdf.format(System.currentTimeMillis()) + "_" + authoriseName;
    }

    private FeedbackMessage getFailFeedbackMessage(String msg) throws WTException {
        return new FeedbackMessage(FeedbackType.FAILURE, (Locale) null, "动态授权", (ArrayList<String>) null, "授权失败:" + msg);
    }

    public static FeedbackMessage getFeedbackMessage(String msg, String receiveName) throws WTException {
        String feedBackMsg = "本次共选择" + totalCount + "条数据，实际需操作" + dealCount + "条，成功" + successCount + "条，失败" + failCount + "条";
        if (!msg.isEmpty()) {
            msg = "接收人：" + receiveName + ",不在该授权的产品团队(" + msg + ")中";
            feedBackMsg += ";\r\n存在以下异常：" + msg;
        }
        return new FeedbackMessage(FeedbackType.SUCCESS, (Locale) null, "操作成功", (ArrayList) null, feedBackMsg);
    }

    /**
     * 获取某产品下团队中所有的人员
     *
     * @param wtContainer
     */
    public static List<String> getALlUsers(WTContainer wtContainer) throws WTException {
        ArrayList nameList = new ArrayList();
        NmOid nmOid = new NmOid(wtContainer);
        Enumeration teamMembers = NmWorkItemCommands.service.getProjectMembers(nmOid);
        NmUser member;
        while (teamMembers.hasMoreElements()) {
            member = (NmUser) teamMembers.nextElement();
            if (!nameList.contains(member.getName())) {
                nameList.add(member.getName());
            }
        }
        return nameList;
    }
}
