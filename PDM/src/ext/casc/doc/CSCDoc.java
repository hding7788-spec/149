package ext.casc.doc;

import java.beans.PropertyVetoException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.DataFormat;
import wt.content.DataFormatReference;
import wt.doc.DepartmentList;
import wt.doc.DocumentType;
import wt.doc.WTDocument;
import wt.doc.WTDocumentDependencyLink;
import wt.doc.WTDocumentHelper;
import wt.doc.WTDocumentMaster;
import wt.fc.ObjectVector;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.collections.WTValuedHashMap;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleState;
import wt.lifecycle.State;
import wt.org.WTPrincipalReference;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartReferenceLink;
import wt.query.CompositeQuerySpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.Iterated;
import wt.vc.IterationInfo;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;
import wt.vc.struct.StructHelper;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressHelper;

import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.windchill.enterprise.copy.server.CoreMetaUtility;

import ext.casc.integrate.util.BomUtil;
import ext.casc.util.CSCUtil;
import ext.casc.util.IBAUtility;

public class CSCDoc {
    public static String FILE_PATH = "FILE_PATH";
    public static String TYPE = "TYPE";
    public static String DEPARTMENT = "DEPARTMENT";
    public static String DOC_TYPE = "DOCUMENT_TYPE";
    public static String INPUT_STREAM = "INPUT_STREAM";
    public static String FOLDER = "FOLDER";

    public static boolean VERBOSE = false;

    static {
        try {
            VERBOSE = WTProperties.getLocalProperties().getProperty("ext.talib.TADoc.verbose", true);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /**
     * This is used to get WTDocument by its number.
     *
     * @param docNumber
     *            document number
     * @return WTDocument or null if there is no document with the number in system.
     */
    public static WTDocument getDoc(String docNumber) {
        WTDocument doc = null;
        String user = "";
        try {
            user = wt.session.SessionHelper.manager.getPrincipal().getName();
            wt.session.SessionHelper.manager.setAdministrator();
        } catch (Exception e) {
        }
        try {
            QuerySpec qs = new QuerySpec(WTDocument.class);
            SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL,
                    docNumber, false);
            qs.appendSearchCondition(sc);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                WTDocument document = (WTDocument) qr.nextElement();
                QueryResult qr2 = VersionControlHelper.service.allIterationsOf(document.getMaster());
                if (qr2.hasMoreElements()) {
                    doc = (WTDocument) qr2.nextElement();
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        if (!"".equals(user)) {
            try {
                wt.session.SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
            }
        }
        return doc;
    }

    public static WTDocument getLatestDocByNumberAndVersion(String docNumber,String version) {
        WTDocument doc = null;
        String user = "";
        try {
            user = wt.session.SessionHelper.manager.getPrincipal().getName();
            wt.session.SessionHelper.manager.setAdministrator();
        } catch (Exception e) {
        }
        try {
        	 int index[] = {0};
            QuerySpec qs = new QuerySpec(WTDocument.class);
            SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL,
                    docNumber);
            qs.appendWhere(sc,index);

            if (version != null && !"".equals(version)) {
            	qs.appendAnd();
            	SearchCondition sCondition = new SearchCondition(WTDocument.class,"versionInfo.identifier.versionId",SearchCondition.EQUAL, version);
            	qs.appendWhere(sCondition,index);
            }
            qs.appendAnd();
        	 SearchCondition sCondition = new SearchCondition(WTDocument.class, "iterationInfo.latest","TRUE");
        	 qs.appendWhere(sCondition,index);

            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                WTDocument document = (WTDocument) qr.nextElement();
                doc = document;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        if (!"".equals(user)) {
            try {
                wt.session.SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
            }
        }
        return doc;
    }


    public static WTDocument getDocumentByOid(String oid) throws WTException {
        ReferenceFactory rf = new ReferenceFactory();
        int pos = oid.indexOf("~");
        if (pos >= 0)
            oid = oid.substring(pos + 1);
        WTDocument doc = (WTDocument) rf.getReference(oid).getObject();
        return doc;
    }

    public static ArrayList getDocVersions(String docNumber) {
        ArrayList aDoc = new ArrayList();
        String user = "";
        try {
            user = wt.session.SessionHelper.manager.getPrincipal().getName();
            wt.session.SessionHelper.manager.setAdministrator();
        } catch (Exception e) {
        }
        try {
            QuerySpec qs = new QuerySpec(WTDocument.class);
            SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL,
                    docNumber, false);
            qs.appendSearchCondition(sc);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                WTDocument document = (WTDocument) qr.nextElement();
                QueryResult qr2 = VersionControlHelper.service.allIterationsOf(document.getMaster());
                while (qr2.hasMoreElements()) {
                    WTDocument doc = (WTDocument) qr2.nextElement();
                    aDoc.add(doc);
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        if (!"".equals(user)) {
            try {
                wt.session.SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
            }
        }

        return aDoc;
    }

    public static WTDocument createDoc(String number, String name, HashMap attributes, HashMap IBAattributes,
            String daleitype, WTContainer containerRef) {

        WTDocument document = null;
        String dalei_temp = (String) attributes.get("DALEI");

        // 若编号为空，自动生成编号
        try {
            if (dalei_temp.equals("工艺附图") || dalei_temp.equals("数控程序") || dalei_temp.equals("设计方案预审单")) {
                number = getDefaultDocSeqNumber();
            }
        } catch (Exception ex) {
            // CSCDebug.outDebugInfo("Get doc numbering Sequence error!");
        }

        // 文件夹

        Folder folder = null;
        String strFolder = (String) attributes.get("FILEFOLDER");
        if (strFolder == null)
            strFolder = "";
        strFolder.trim();
        if ("".equals(strFolder))
            strFolder = "/Default";
        try {
            folder = FolderHelper.service.getFolder(strFolder, WTContainerRef.newWTContainerRef(containerRef));
        } catch (Exception e) {
            folder = null;
        }

        if (folder == null) {
            try {
                folder = FolderHelper.service.saveFolderPath(strFolder, WTContainerRef.newWTContainerRef(containerRef));
            } catch (Exception e) {
                // TODO: handle exception
                folder = null;
            }
        }

        try {
            document = WTDocument.newWTDocument();
            // 设置生命周期为已发布
            LifeCycleState documentState = LifeCycleState.newLifeCycleState();
            documentState.setState(State.toState("APPROVED"));
            document.setState(documentState);
            // 设置基本属性
            document.setName(name);
            document.setNumber(number);
            // 设置PDM系统中存储路径
            if (folder != null)
                FolderHelper.assignLocation((FolderEntry) document, folder);
            // 设置文档大类
            TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference(daleitype);
            document.setTypeDefinitionReference(tdr);

            // 保存为持久对象
            document = (WTDocument) PersistenceHelper.manager.save(document);
            IBAUtility ibaUtility = new IBAUtility(document);
            ibaUtility.setIBAValue("COMPANY", (String) IBAattributes.get("COMPANY"));

            if (dalei_temp.equals("工艺规程") || dalei_temp.equals("工艺技术通知单") || dalei_temp.equals("技术通知单")
                    || dalei_temp.equals("临时工艺文件") || dalei_temp.equals("图样") || dalei_temp.equals("文字或表格类设计文件")) {
                ibaUtility.setIBAValue("SECRET", (String) IBAattributes.get("SECRET"));
                ibaUtility.setIBAValue("PHASE_CODE", (String) IBAattributes.get("PHASE_CODE"));
                if (dalei_temp.equals("工艺规程")) {
                    ibaUtility.setIBAValue("DEPT", (String) IBAattributes.get("DEPT"));
                    ibaUtility.setIBAValue("KEYCOMPONENT", (String) IBAattributes.get("KEYCOMPONENT"));
                    ibaUtility.setIBAValue("PROCESSSTATUS", (String) IBAattributes.get("PROCESSSTATUS"));
                }
            }
            if (dalei_temp.equals("软件文档") || dalei_temp.equals("图样") || dalei_temp.equals("文字或表格类设计文件")
                    || dalei_temp.equals("研试文件") || dalei_temp.equals("质量报告")) {
                ibaUtility.setIBAValue("SUBTYPE", (String) IBAattributes.get("SUBTYPE"));
                ibaUtility.setIBAValue("SECRET", (String) IBAattributes.get("SECRET"));
                ibaUtility.setIBAValue("PHASE_CODE", (String) IBAattributes.get("PHASE_CODE"));
            }

            if(dalei_temp.equals("典型工艺") || dalei_temp.equals("通用工艺")|| dalei_temp.equals("国家标准")) {
            	ibaUtility.setIBAValue("SECRET", (String) IBAattributes.get("SECRET"));
            	ibaUtility.setIBAValue("PPNUMBER", (String) IBAattributes.get("PPNUMBER"));
            }

            document = (WTDocument) ibaUtility.updateAttributeContainer(document);
            ibaUtility.updateIBAHolder(document);
            document = (WTDocument) PersistenceHelper.manager.refresh(document);

        } catch (Exception e) {
            outDebugInfo("create doc error, message:\n");
            e.printStackTrace();
            return null;
        }

        return document;
    }

    public static WTDocument createDoc(String number, String name, String desc, HashMap attributes,
            WTContainerRef containerRef) {
        // CSCDebug.outDebugInfo("Begin to create doc " + number);

        try {
            if (number == null) {
                number = getDefaultDocSeqNumber();
            }
        } catch (Exception ex) {
            // CSCDebug.outDebugInfo("Get doc numbering Sequence error!");
        }

        WTDocument doc = getDoc(number);

        if (doc != null) {
            // CSCDebug.outDebugInfo("Doc is Existed! Doc Number = " + number);
            return doc;
        }

        if (containerRef == null) {
            // CSCDebug.outDebugInfo("Container reference is null, can't create doc");
            return null;
        }
        // CSCDebug.outDebugInfo("Doc doesn't exist");
        // CSCDebug.outDebugInfo("Container reference is " + containerRef + " " + containerRef.getName());

        String folder = getStringValue((String) attributes.get(FOLDER));
        if ("".equals(folder))
            folder = "/Default";
        else if (!folder.startsWith("/Default"))
            folder = "/Default/" + folder;
        folder = folder.replaceAll("//", "/");
        outDebugInfo("\tFolder=" + folder);
        Folder location = null;
        try {
            location = FolderHelper.service.getFolder(folder, containerRef);
        } catch (Exception e) {
            location = null;
        }

        try {
            // set DocumentType
            DocumentType docType = null;
            String doc_type = getStringValue((String) attributes.get(DOC_TYPE));
            if (!doc_type.equals(""))
                docType = DocumentType.toDocumentType(doc_type);

            if (docType == null)
                docType = DocumentType.getDocumentTypeDefault();

            doc = WTDocument.newWTDocument(number, name, docType);

            // set doucment type identifier
            String type = getStringValue((String) attributes.get(TYPE));
            if (!"".equals(type)) {
                outDebugInfo("\ttype=" + type);
                if (!type.startsWith("WCTYPE|"))
                    type = "WCTYPE|" + type;
                TypeIdentifier id = TypeHelper.getTypeIdentifier(type);
                doc = (WTDocument) CoreMetaUtility.setType(doc, id);
            }

            // set doucment department
            DepartmentList department = null;
            String strDepartment = getStringValue((String) attributes.get(DEPARTMENT));
            if (!"".equals(strDepartment)) {
                department = DepartmentList.toDepartmentList(strDepartment);
            } else {
                department = DepartmentList.getDepartmentListDefault();
            }

            if (department != null)
                doc.setDepartment(department);

            if (desc != null)
                doc.setDescription(desc);

            // set folder's container info and location.
            doc.setContainerReference(containerRef);

            if (location == null)
                location = FolderHelper.service.saveFolderPath(folder, containerRef);

            if (location != null) {
                WTValuedHashMap map = new WTValuedHashMap();
                map.put(doc, location);
                FolderHelper.assignLocations(map);
            }
            doc = (WTDocument)PersistenceHelper.manager.save(doc);

            String filepath = getStringValue((String) attributes.get(FILE_PATH));
            if (!"".equals(filepath)) {
            	doc = updateDocContent(doc, filepath);
            }

        } catch (Exception e) {
            outDebugInfo("create doc error, message:\n");
            e.printStackTrace();
            return null;
        }

        return doc;
    }

    public static WTDocument updateDocAttributes(String number, String name, String desc, String filePath) {
        outDebugInfo("====begin to update doc");

        if (number == null)
            return null;
        try {
            WTDocument doc = getDoc(number);

            if (doc == null) {
                outDebugInfo("doc doesn't be found");
                return null;
            }

            if (name != null)
                doc.setName(name);
            if (desc != null)
                doc.setDescription(desc);

            PersistenceHelper.manager.save(doc);

            String filepath = getStringValue(filePath);
            if (!"".equals(filepath)) {
                updateDocContent(doc, filepath);
            }
            return doc;
        } catch (Exception ex) {
            outDebugInfo("update doc terminated!");
            ex.printStackTrace();
            return null;
        }
    }

    public static WTDocument updateDocContent(WTDocument doc, String filepath) {
        try {
            if (doc == null)
                return null;
            doc = getWorkingCopyOfDoc(doc);

            String fileformat = getFileFormat(filepath);
            ApplicationData applicationdata = ApplicationData.newApplicationData(doc);
            applicationdata.setRole(ContentRoleType.PRIMARY);
            applicationdata = ContentServerHelper.service.updateContent(doc, applicationdata, filepath);
            doc = (WTDocument) PersistenceServerHelper.manager.restore(doc);

            // set file format
            if (fileformat != null && !"".equals(fileformat)) {
                DataFormat df = ContentHelper.service.getFormatByName(fileformat);
                DataFormatReference dfr = DataFormatReference.newDataFormatReference(df);
                doc.setFormat(dfr);
            }
            if (doc != null) {
                if (wt.vc.wip.WorkInProgressHelper.isCheckedOut(doc, wt.session.SessionHelper.manager.getPrincipal()))
                    doc = (WTDocument) WorkInProgressHelper.service.checkin(doc, "add primary file");
            }
        } catch (Exception e) {
            // TODO: handle exception
            outDebugInfo("update doc content error, doc=" + doc.getNumber());
            e.printStackTrace();
            return null;

        }

        return doc;
    }

    public static WTDocument getWorkingCopyOfDoc(WTDocument doc) throws Exception {
        WTDocument workingdoc = null;

        if (!WorkInProgressHelper.isCheckedOut(doc)) {
            wt.folder.Folder folder = WorkInProgressHelper.service.getCheckoutFolder();
            CheckoutLink checkoutlink = WorkInProgressHelper.service.checkout(doc, folder, "");
            workingdoc = (WTDocument) checkoutlink.getWorkingCopy();
        } else {
            if (!WorkInProgressHelper.isWorkingCopy(doc))
                workingdoc = (WTDocument) WorkInProgressHelper.service.workingCopyOf(doc);
            else workingdoc = doc;
        }
        return workingdoc;
    }

    public static QueryResult getDocPartByNumberName(String classType, String number, String name, String context)
            throws WTException {
        String user = "";
        SearchCondition condition = null;
        CompositeQuerySpec querySpec = new CompositeQuerySpec();
        WTContainer orgContainer = null;
        long orgA2A2 = 0;
        int indexDoc = 0;
        int indexPart = 0;
        QueryResult result = new QueryResult();
        QueryResult rs = null;
        Persistable[] oneResult = null;
        Persistable object = null;
        LatestConfigSpec latestSpec = null;
        Vector vct = new Vector();

        try {
            user = wt.session.SessionHelper.manager.getPrincipal().getName();
            wt.session.SessionHelper.manager.setAdministrator();
            // CSCDebug.outDebugInfo(wt.session.SessionHelper.manager.getPrincipal().getName());
        } catch (Exception e) {
        }

        if (context != null) {
            orgContainer = (WTContainer) CSCUtil.getObjectByOid(context);
            orgA2A2 = PersistenceHelper.getObjectIdentifier(orgContainer).getId();
        }

        QuerySpec documentQuery = new QuerySpec();
        QuerySpec partQuery = new QuerySpec();

        Class class1 = WTDocument.class;
        Class class2 = WTPart.class;

        indexDoc = documentQuery.addClassList(class1, true);
        indexPart = partQuery.addClassList(class2, true);

        // FOR PART
        if (classType == null || classType.equals("All") || classType.contains("WTPart.class")) {
            boolean firstCondition = true;
            if (context != null && context.length() > 0) {
                if (!firstCondition)
                    partQuery.appendAnd();
                condition = new SearchCondition(WTPart.class, "containerReference.key.id", SearchCondition.EQUAL,
                        orgA2A2);
                partQuery.appendWhere(condition, indexPart);
                firstCondition = false;
            }

            if (number != null && number.length() > 0) {
                if (!firstCondition)
                    partQuery.appendAnd();
                condition = new SearchCondition(WTPart.class, "master>number", SearchCondition.LIKE, "%" + number + "%");
                partQuery.appendWhere(condition, indexPart);
                firstCondition = false;
            }

            if (name != null && name.length() > 0) {
                if (!firstCondition)
                    partQuery.appendAnd();
                condition = new SearchCondition(WTPart.class, "master>name", SearchCondition.LIKE, "%" + name + "%");
                partQuery.appendWhere(condition, indexPart);
                firstCondition = false;
            }

            latestSpec = new LatestConfigSpec();
            partQuery = latestSpec.appendSearchCriteria(partQuery);
        }

        // FOR DOCUMENT
        if (classType == null || classType.equals("All") || classType.contains("WTDocument.class")) {
            boolean firstCondition = true;
            if (context != null && context.length() > 0) {
                if (!firstCondition)
                    documentQuery.appendAnd();
                condition = new SearchCondition(WTDocument.class, "containerReference.key.id", SearchCondition.EQUAL,
                        orgA2A2);
                documentQuery.appendWhere(condition, indexDoc);
                firstCondition = false;
            }

            if (number != null && number.length() > 0) {
                if (!firstCondition)
                    documentQuery.appendAnd();
                condition = new SearchCondition(WTDocument.class, "master>number", SearchCondition.LIKE, "%" + number
                        + "%");
                documentQuery.appendWhere(condition, indexDoc);
                firstCondition = false;
            }

            if (name != null && name.length() > 0) {
                if (!firstCondition)
                    documentQuery.appendAnd();
                condition = new SearchCondition(WTDocument.class, "master>name", SearchCondition.LIKE, "%" + name + "%");
                documentQuery.appendWhere(condition, indexDoc);
                firstCondition = false;
            }

            latestSpec = new LatestConfigSpec();
            documentQuery = latestSpec.appendSearchCriteria(documentQuery);
        }

        querySpec.addComponent(documentQuery);
        querySpec.addComponent(partQuery);

        if (classType.contains("WTDocument.class"))
            rs = PersistenceHelper.manager.find(documentQuery);
        else if (classType.contains("WTPart.class"))
            rs = PersistenceHelper.manager.find(partQuery);
        else rs = PersistenceHelper.manager.find(querySpec);
        while (rs.hasMoreElements()) {
            if (classType.contains("WTDocument.class") || classType.contains("WTPart.class")) {
                oneResult = (Persistable[]) rs.nextElement();
                object = oneResult[0];
            } else {
                oneResult = (Persistable[]) rs.nextElement();
                object = oneResult[0];
            }

            if (object instanceof WTPart) {
                if (!vct.contains((WTPart) object)) {
                    WTPart part = (WTPart) object;
                    part = (WTPart) VersionControlHelper.service.getLatestIteration(part, false);
                    vct.add(part);

                }
            } else if (object instanceof WTDocument) {
                if (!vct.contains((WTDocument) object)) {
                    WTDocument doc = (WTDocument) object;
                    doc = (WTDocument) VersionControlHelper.service.getLatestIteration(doc, false);
                    vct.add(doc);
                }
            } else {
                // System.out.println("Not found the Object type" + object.getClass());
            }
        }
        result.append(new ObjectVector(vct));

        if (!"".equals(user)) {
            try {
                // wt.session.SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
            }
        }
        // CSCDebug.outDebugInfo("SERCH END...");
        return result;
    }

    public static WTDocumentDependencyLink getDocDependencyLink(WTDocument doc1, WTDocument doc2) throws WTException {
        QueryResult queryresult = PersistenceHelper.manager.find(wt.doc.WTDocumentDependencyLink.class, doc1,
                WTDocumentDependencyLink.DESCRIBES_ROLE, doc2);
        if (queryresult == null || queryresult.size() == 0)
            return null;
        else return (WTDocumentDependencyLink) queryresult.nextElement();
    }

    public static boolean createDocAssociateDoc(String number, WTDocument wtdocument) throws WTException {
        WTDocument doc1 = CSCDoc.getDoc(number);
        WTDocument doc2 = CSCDoc.getDoc(wtdocument.getNumber());
        WTDocumentDependencyLink wtdocdependencylinkOld = CSCDoc.getDocDependencyLink(doc1, doc2);
        if (wtdocdependencylinkOld == null) {
            // System.out.println("no link between the wtdocument and wtdocument,will create");
            WTDocumentDependencyLink wtdocdependencylink1 = WTDocumentDependencyLink.newWTDocumentDependencyLink(doc1,
                    doc2);
            PersistenceServerHelper.manager.insert(wtdocdependencylink1);

            wtdocdependencylink1 = (WTDocumentDependencyLink) PersistenceHelper.manager.refresh(wtdocdependencylink1);
            WTDocumentDependencyLink wtdocdependencylink = CSCDoc.getDocDependencyLink(doc1, doc2);

            if (wtdocdependencylink == null) {
                // System.out.println("creat wtpart associate doc fail");
                return false;
            } else return true;
        }
        // System.out.println("the link has alseardy exist,do not need to create it again");
        return false;
    }

    public static boolean createDocAssociateDoc(WTDocument doc1, WTDocument doc2) throws WTException {
        WTDocumentDependencyLink wtdocdependencylinkOld = CSCDoc.getDocDependencyLink(doc1, doc2);
        if (wtdocdependencylinkOld == null) {
            // System.out.println("no link between the wtdocument and wtdocument,will create");
            WTDocumentDependencyLink wtdocdependencylink1 = WTDocumentDependencyLink.newWTDocumentDependencyLink(doc1,
                    doc2);
            PersistenceServerHelper.manager.insert(wtdocdependencylink1);

            wtdocdependencylink1 = (WTDocumentDependencyLink) PersistenceHelper.manager.refresh(wtdocdependencylink1);
            WTDocumentDependencyLink wtdocdependencylink = CSCDoc.getDocDependencyLink(doc1, doc2);

            if (wtdocdependencylink == null) {
                // System.out.println("creat wtpart associate doc fail");
                return false;
            } else return true;
        }
        // System.out.println("the link has alseardy exist,do not need to create it again");
        return false;
    }

    public static boolean removeDocAssociateDoc(String number, WTDocument wtdocument) throws WTException {
        WTDocument doc1 = CSCDoc.getDoc(number);
        WTDocument doc2 = CSCDoc.getDoc(wtdocument.getNumber());
        WTDocumentDependencyLink wtdocdependencylinkOld = CSCDoc.getDocDependencyLink(doc1, doc2);

        if (wtdocdependencylinkOld != null) {
            PersistenceServerHelper.manager.remove(wtdocdependencylinkOld);
            // System.out.println("Deleting the link...");
            return true;
        }
        // System.out.println("the link isn't exist");
        return false;
    }

    public static boolean removeDocAssociateDoc(WTDocument doc1, WTDocument doc2) throws WTException {
        WTDocumentDependencyLink wtdocdependencylinkOld = CSCDoc.getDocDependencyLink(doc1, doc2);

        if (wtdocdependencylinkOld != null) {
            PersistenceServerHelper.manager.remove(wtdocdependencylinkOld);
            // System.out.println("Deleting the link between " + doc1 + " AND " + doc2 + "...");
            return true;
        }
        // System.out.println("the link isn't exist");
        return false;
    }

    public static String getFileFormat(String filename) {
        String format = "";
        if (filename != null && !"".equals(filename)) {
            if (filename.endsWith(".xls"))
                format = "Microsoft Excel";
            else if (filename.endsWith(".doc"))
                format = "Microsoft Word";
            else if (filename.endsWith(".txt"))
                format = "Text File";
            else if (filename.endsWith(".pdf"))
                format = "PDF";
            else format = "NONE";
        }
        return format;
    }

    public static String getStringValue(String value) {
        if (value == null)
            value = "";
        value = value.trim();
        return value;
    }

    public static ArrayList getDocumentsLikeNumber(String number) {
        // System.out.println("==get Document number=" + number);
        String user = "";
        ArrayList list = new ArrayList();
        try {
            user = wt.session.SessionHelper.manager.getPrincipal().getName();
            wt.session.SessionHelper.manager.setAdministrator();
        } catch (Exception e) {
        }
        try {
            QuerySpec qs = new QuerySpec(WTDocument.class);
            SearchCondition sc = new
                    SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.LIKE, "%" + number + "%",
                            false);
            qs.appendSearchCondition(sc);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            while (qr.hasMoreElements()) {
                WTDocument doc = (WTDocument) qr.nextElement();
                doc = (WTDocument) VersionControlHelper.service.getLatestIteration(doc, false);
                if (!list.contains(doc))
                    list.add(doc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!"".equals(user)) {
            try {
                wt.session.SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
            }
        }

        return list;
    }

    public static ArrayList getDocumentsLikeName(String name) {
        // System.out.println("==get Document name=" + name);
        String user = "";
        ArrayList list = new ArrayList();
        try {
            user = wt.session.SessionHelper.manager.getPrincipal().getName();
            wt.session.SessionHelper.manager.setAdministrator();
        } catch (Exception e) {
        }
        try {
            QuerySpec qs = new QuerySpec(WTDocument.class);
            SearchCondition sc = new
                    SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.LIKE, "%" + name + "%", false);
            qs.appendSearchCondition(sc);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            while (qr.hasMoreElements()) {
                WTDocument doc = (WTDocument) qr.nextElement();
                doc = (WTDocument) VersionControlHelper.service.getLatestIteration(doc, false);
                if (!list.contains(doc))
                    list.add(doc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!"".equals(user)) {
            try {
                wt.session.SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
            }
        }

        return list;
    }

    public static WTDocument getDocByNumberEdition(String docNumber, String edition) {
        String user = "";
        ArrayList list = new ArrayList();
        WTDocument doc = null;
        try {
            user = wt.session.SessionHelper.manager.getPrincipal().getName();
            wt.session.SessionHelper.manager.setAdministrator();
        } catch (Exception e) {
        }

        StringTokenizer st = new StringTokenizer(edition, ".");
        String strVersion = "";
        String strIteration = "";

        // System.out.println("SEARCH PARTNUMBER:" + docNumber + " | VERSION: " + strVersion + " | Iteration " +
        // strIteration);

        try {
            QuerySpec qs = new QuerySpec(WTDocument.class);
            SearchCondition sc = new
                    SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, docNumber, false);
            qs.appendSearchCondition(sc);
            qs.appendAnd();

            if (st.hasMoreTokens())
                strVersion = st.nextToken();
            SearchCondition sc2 = new
                    SearchCondition(WTDocument.class, "versionInfo.identifier.versionId", SearchCondition.EQUAL,
                            strVersion, false);
            qs.appendSearchCondition(sc2);

            if (edition.indexOf('.') != -1) {
                if (st.hasMoreTokens())
                    strIteration = st.nextToken();
                qs.appendAnd();
                SearchCondition sc3 = new
                        SearchCondition(WTDocument.class, "iterationInfo.identifier.iterationId",
                                SearchCondition.EQUAL, strIteration, false);
                qs.appendSearchCondition(sc3);
            }

            QueryResult qr = PersistenceHelper.manager.find(qs);
            while (qr.hasMoreElements()) {
                doc = (WTDocument) qr.nextElement();
            }
        } catch (Exception e) {
            // e.printStackTrace();
            // System.out.println("Cannot find doc " + docNumber + "v" + edition);
        }
        if (!"".equals(user)) {
            try {
                wt.session.SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
            }
        }

        return doc;
    }

    /**
     * Get Related WTObjects by a document number. Includes Related Parts and Related Documents
     *
     * @param docNumber
     * @return ArrayList with WTObjects
     * @throws WTException
     */
    public static ArrayList getRelatedWTObjectByDoc(WTDocument doc) throws WTException {
        ArrayList result = new ArrayList();

        QueryResult qr = WTPartHelper.service.getDescribesWTParts(doc);
        while (qr.hasMoreElements()) {
            WTPart wtpart = (WTPart) qr.nextElement();
            result.add(wtpart);
        }

        qr = WTDocumentHelper.service.getDependsOnWTDocuments(doc);
        while (qr.hasMoreElements()) {
            WTDocument wtdoc = (WTDocument) qr.nextElement();
            result.add(wtdoc);
        }

        qr = getReferencesWTParts((WTDocumentMaster) doc.getMaster());
        while (qr.hasMoreElements()) {
            WTPart wtpart = (WTPart) qr.nextElement();
            result.add(wtpart);
        }

        return result;
    }

    public static void outDebugInfo(String message) {
        // if(VERBOSE)
        // System.out.println("TADoc.class Debug Info:\t" + message);
    }

    public static void setModifier(Iterated obj, WTPrincipalReference uref) throws Exception {
        try {
            obj.getCreator();
            Class[] pp = new Class[] { WTPrincipalReference.class };
            Method setCreator = IterationInfo.class.getDeclaredMethod("setModifier", pp);
            setCreator.setAccessible(true);
            setCreator.invoke(obj.getIterationInfo(), new Object[] { uref });
        } catch (Exception e) {
            // System.out.println(">>>Error:" + e.toString());
            e.printStackTrace();
            throw e;
        }

    }

    public static String getDocSeqNumber(String seqName) {
        return getDocSeqNumber(seqName, 10);
    }

    public static String getDocSeqNumber(String seqName, int bits) {
        String bitFormat = "";

        try {
            if (bits == 0)
                return PersistenceHelper.manager.getNextSequence(seqName);

            for (int i = 0; i < bits; i++) {
                bitFormat = bitFormat + "0";
            }

            int seq = Integer.parseInt(PersistenceHelper.manager.getNextSequence(seqName));
            DecimalFormat format = new DecimalFormat(bitFormat);
            return format.format(seq);
        } catch (Exception ex) {
            outDebugInfo("get doc seqence error!");
        }
        return null;
    }

    public static String getDefaultDocSeqNumber() {
        return getDocSeqNumber("WTDOCUMENTID_SEQ");
    }

    public static QueryResult getReferencesWTParts(WTDocumentMaster master) throws WTException {
        return StructHelper.service.navigateReferencedBy(master, WTPartReferenceLink.class, true);
    }

    public static String getDeptByProcessDoc(WTDocument doc){
		try {
			Element techEle = BomUtil.getTechincisElement(doc,null,null);
			if(techEle!=null){
				return  techEle.attributeValue("DEPT");
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	return "";
    }

    public static void main(String[] args) throws WTException {
        /**
         * Get Document related Objects!!
         */
        /**
         * WTDocument doc = CSCDoc.getDoc("0000000021");
         * QueryResult qr = WTPartHelper.service.getDescribesWTParts(doc);
         * while(qr.hasMoreElements()){
         * WTPart wtpart = (WTPart)qr.nextElement();
         * System.out.println(wtpart.getNumber());
         * }
         *
         * qr = WTDocumentHelper.service.getDependsOnWTDocuments(doc);
         * while(qr.hasMoreElements()){
         * WTDocument wtdoc = (WTDocument)qr.nextElement();
         * System.out.println(wtdoc.getNumber());
         * }
         **/
        WTDocument doc = CSCDoc.getDoc(args[0]);

        QueryResult qr = CSCDoc.getReferencesWTParts((WTDocumentMaster) doc.getMaster());
        while (qr.hasMoreElements()) {
            System.out.println(qr.nextElement());
        }
    }
}
