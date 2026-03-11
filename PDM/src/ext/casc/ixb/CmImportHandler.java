package ext.casc.ixb;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import wt.content.Streamed;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.facade.ixb.IxbDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTHashSet;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.ixb.clientAccess.IXBJarReader;
import wt.ixb.handlers.netmarkets.NMHandler;
import wt.ixb.publicforapps.IxbHelper;
import wt.org.WTOrganization;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.Iterated;

import com.ptc.extend.ixb.CmImporter;

public class CmImportHandler extends NMHandler implements CmImporter {
    File fileonserver;
    IXBJarReader jar;
    WTPrincipal currentUser;
    Hashtable newdata;
    Hashtable existdata;
    WTHashSet docdata;
    WTHashSet partdata;
    WTHashSet epmdata;
    WTHashSet properlyReceivedSet;
    HashSet missingObjects;
    boolean isChangeLifecycle;
    String lifecycleTemplateName;
    String lifecycleStateName;
    String importfolder;
    HashMap importedObjMap;
    HashMap<String,RevisionControlled> numberObject;
    HashMap<String,String> numberImplementAdvise;
    //处理借用件问题，先把导入的包的容器先保存在导入处理类中
    private WTContainerRef containerRef ;
    public static String PSEDO_PRE = "SAMC";
    public static boolean PSEDO = false;

    public static class CmJarFileNameComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            int level1 = getPriority(o1);
            int level2 = getPriority(o2);
            if (level1 > level2)
                return 1;
            else if (level1 < level2)
                return -1;
            else return String.valueOf(o1).compareTo(String.valueOf(o2));
        }

        public int getPriority(Object o) {
            String fullname;
            String typename;
            if (o instanceof String) {
                fullname = (String) o;
                typename = fullname.substring(fullname.indexOf("TAG-") + 4);
                if (typename.indexOf("-") >= 0)
                    typename = typename.substring(0, typename.indexOf("-"));
                else if (typename.indexOf(".xml") > 0)
                    typename = typename.substring(0, typename.indexOf(".xml"));
            } else {
                fullname = o.getClass().getName();
                typename = fullname.substring(fullname.lastIndexOf(".") + 1);
            }
            //由于要把实施意见记录到ProcessEnvelope，WTChangeOrder的link中，所以先建立包及ChangePackaged
            if (typename.equals("ProcessEnvelope")|| typename.equals("WTChangeRequest")||typename.equals("WTChangeOrder"))
                return 40;
            if (typename.equals("WTDocument"))
                return 50;
            if (typename.equals("EPMSepFamilyTable"))
            	return 51;
            if (typename.equals("EPMDocument"))
                return 52;
            if (typename.equals("WTPart") || typename.equals("ManufacturerPart") || typename.equals("VendorPart"))
            	return 53;
            if (typename.equals("WTVariance") || typename.equals("WTChangeRequest2")
                    || typename.equals("WTChangeIssue")
                    || typename.equals("WTChangeProposal") || typename.equals("WTChangeActivity2")
                    || typename.equals("WTAnalysisActivity"))
                return 54;
            if (typename.equals("Representation"))
                return 60;
            if (typename.equals("TechNoticeBeforeLink") || typename.equals("TechNoticeAfterLink")
                    || typename.equals("EnvelopeMemberLink") || typename.equals("WTDocumentUsageLink")
                    || typename.equals("WTDocumentDependencyLink") || typename.equals("WTPartUsageLink")
                    || typename.equals("EPMMemberLink") || typename.equals("EPMVariantLink")
                    || typename.equals("EPMReferenceLink") || typename.equals("ECNAffectItemLink")
                    || typename.equals("ECNResultItemLink")|| typename.equals("ECRAffectItemLink"))
                return 80;
            if (typename.equals("WTPartDescribeLink") || typename.equals("WTPartReferenceLink")
                    || typename.equals("EPMDescribeLink") || typename.equals("EPMContainedIn"))
                return 81;
            if (typename.equals("WTPartAlternateLink") || typename.equals("WTPartSubstituteLink"))
                return 82;
            if (typename.equals("EPMBuildRule") || typename.equals("PublishedContentLink") ||
                    typename.equals("FormalizedBy") || typename.equals("ResearchedBy")
                    || typename.equals("AddressedBy2") || typename.equals("IncludedIn2")
                    || typename.equals("DetailedBy") || typename.equals("AcceptedStrategy")
                    || typename.equals("ReportedAgainst") || typename.equals("ProblemProduct")
                    || typename.equals("SubjectProduct") || typename.equals("AffectedActivityData")
                    || typename.equals("ChangeRecord2") || typename.equals("RelevantRequestData2")
                    || typename.equals("NoteHolderNoteLink") || typename.equals("RelevantAnalysisData")
                    || typename.equals("ConfigurableDescribeLink") || typename.equals("ConfigurableReferenceLink"))
                return 85;
            if (typename.equals("EPMBuildHistory"))
                return 90;
            if (typename.equals("WTTypeDefinitions"))
                return 30;
            if (typename.equals("ibaDefinitions"))
                return 25;
            if (typename.equals("DisplayUnits"))
                return 10;
            if (typename.equals("RelationshipMap") || typename.equals("BaseCriterionDef")
                    || typename.equals("CollectionCriteria") || typename.equals("CriterionDefValidWith")
                    || typename.equals("RelationshipMapDef"))
                return 95;
            if (typename.equals("ClassificationNode") || typename.equals("BusinessEntity"))
                return 20;

            return 100;
        }
    }

    private CmImportHandler() {
    }

    public CmImportHandler(File fn) {
        try {
            fileonserver = fn;
            jar = new IXBJarReader(fileonserver);
            currentUser = SessionHelper.getPrincipal();
            newdata = new Hashtable();
            existdata = new Hashtable();
            docdata = new WTHashSet();
            partdata = new WTHashSet();
            epmdata = new WTHashSet();
            properlyReceivedSet = new WTHashSet();
            missingObjects = new HashSet();
            importedObjMap = new HashMap();
            numberObject= new HashMap<String, RevisionControlled>();
            numberImplementAdvise = new HashMap<String, String>();
            // loadImportConfig();
        } catch (Throwable t) {
        	t.printStackTrace();
            throw new ExceptionInInitializerError(t);
        }
    }

    // public CmImportHandler(ImportConfig icc, File fn) {
    // try {
    // fileonserver=fn;
    // jar = new IXBJarReader(fileonserver);
    // currentUser=SessionHelper.getPrincipal();
    // newdata=new Hashtable();
    // existdata=new Hashtable();
    // docdata=new WTHashSet();
    // partdata=new WTHashSet();
    // epmdata=new WTHashSet();
    // properlyReceivedSet=new WTHashSet();
    // missingObjects=new HashSet();
    // importedObjMap=new HashMap();
    // ic=icc;
    // } catch (Throwable t) {
    // throw new ExceptionInInitializerError(t);
    // }
    // }

    // private void loadImportConfig() {
    // try {
    // if (checkFileExistInJar(ApplicationHandlerFactory.MANIFEST_FILE_NAME)) {
    // InputStream is=getContentAsInputStream(ApplicationHandlerFactory.MANIFEST_FILE_NAME);
    // Properties props=new Properties();
    // props.load(is);
    // String s=(String)props.getProperty("exportsite");
    // is.close();
    // if (s!=null&&!s.equals("")) {
    // ic=CmDeliveryHelper.getImportConfigFromRemoteURL(s);
    // }
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    // public boolean isValid() {
    // return ic!=null;
    // }

    public void putInExistedHashtable(String id, Object obj) {
        existdata.put(id, obj);
        classifyObject((Persistable) obj);
    }

    public void putInNewCreatedHashtable(String id, Object obj) {
        newdata.put(id, obj);
        classifyObject((Persistable) obj);
    }


    private void classifyObject(Persistable obj) {
        if (obj instanceof WTDocument)
            docdata.add(obj);
        else if (obj instanceof WTPart)
            partdata.add(obj);
        else if (obj instanceof EPMDocument)
            epmdata.add(obj);
    }

    private void putInMissingObjectSet(Object obj) {
        if (missingObjects.contains(obj))
            return;
        missingObjects.add(obj);
    }

    public void putInMissingObjectSet(String objType, String number, String version, String iteration) {
        String idstr = (new StringBuilder()).append(objType).append("@")
                    .append(number).append("@")
                    .append(version).append("@")
                    .append(iteration).toString();
        putInMissingObjectSet(idstr);
    }

    public void putInMissingMasterObjectSet(String objMasterType, String number) {
        String idstr = (new StringBuilder()).append(objMasterType).append("@").append(number).toString();
        putInMissingObjectSet(idstr);
    }

    public void putInProperlyReceivedObjectSet(Persistable obj) {
        if (properlyReceivedSet.contains(obj))
            return;
        properlyReceivedSet.add(obj);
    }

    public void removeFromProperlyReceivedObjectSet(Persistable obj) {
        if (properlyReceivedSet.contains(obj))
            properlyReceivedSet.remove(obj);
    }

    public boolean isProperlyReceived(Persistable obj) {
        return properlyReceivedSet.contains(obj);
    }

    public WTHashSet getDocHashSet() {
        return docdata;
    }

    public WTHashSet getPartHashSet() {
        return partdata;
    }

    public WTHashSet getEPMDocHashSet() {
        return epmdata;
    }

    public HashSet getMissingObjectSet() {
        return missingObjects;
    }

    public Hashtable getExistObjects() {
        return existdata;
    }

    public Hashtable getNewObjects() {
        return newdata;
    }

    public ArrayList getAllObjects() {
        ArrayList list = new ArrayList();
        Iterator it = existdata.keySet().iterator();
        while (it.hasNext()) {
            String remoteId = (String) it.next();
            list.add(existdata.get(remoteId));
        }
        it = newdata.keySet().iterator();
        while (it.hasNext()) {
            String remoteId = (String) it.next();
            list.add(newdata.get(remoteId));
        }
        Collections.sort(list, new CmJarFileNameComparator());
        return list;
    }

    public WTContainerRef getWTContainerRef() throws WTException {
        return containerRef;
    }

    public void setWTContainerRef(WTContainerRef containerRef) throws WTException {
        this.containerRef = containerRef;
    }

    public WTUser getOperator() {
        return (WTUser) currentUser;
    }

    public ArrayList getAllFilesInJar() throws WTException {
        ArrayList list = new ArrayList();
        String filenames[] = jar.getFileNames();
        for (int i = 0; i < filenames.length; i++) {
            String fname = filenames[i];
            // Debug.P("File:", fname);
            list.add(fname);
        }
        return list;
    }

    public ArrayList getAllXmlFilesInJar() throws WTException {
        ArrayList list = new ArrayList();
        String filenames[] = jar.getFileNames();
        for (int i = 0; i < filenames.length; i++) {
            String fname = filenames[i];
            if (fname.toUpperCase().endsWith(".XML"))
                list.add(fname);
        }
        Collections.sort(list, new CmJarFileNameComparator());
        return list;
    }

    public ArrayList getAllWTDocumentInJar() throws WTException {
        ArrayList list = new ArrayList();
        String filenames[] = jar.getFileNames();
        for (int i = 0; i < filenames.length; i++) {
            String fname = filenames[i];
            if (fname.startsWith("TAG-WTDocument-") && fname.endsWith(".xml"))
                list.add(fname);
        }
        return list;
    }

    public ArrayList getAllWTPartInJar() throws WTException {
        ArrayList list = new ArrayList();
        String filenames[] = jar.getFileNames();
        for (int i = 0; i < filenames.length; i++) {
            String fname = filenames[i];
            if (fname.startsWith("TAG-WTPart-") && fname.endsWith(".xml"))
                list.add(fname);
        }
        return list;
    }

    public ArrayList getAllEPMDocumentInJar() throws WTException {
        ArrayList list = new ArrayList();
        String filenames[] = jar.getFileNames();
        for (int i = 0; i < filenames.length; i++) {
            String fname = filenames[i];
            if (fname.startsWith("TAG-EPMDocument-") && fname.endsWith(".xml"))
                list.add(fname);
        }
        return list;
    }

    public ArrayList getAllTopObjectXmlFileInJar() throws WTException {
        ArrayList list = new ArrayList();
        String filenames[] = jar.getFileNames();
        for (int i = 0; i < filenames.length; i++) {
            String fname = filenames[i];
            if (fname.startsWith("TAG-") && fname.endsWith(".xml"))
                list.add(fname);
        }
        Collections.sort(list, new CmJarFileNameComparator());
        return list;
    }

    public ArrayList getXmlDocumentsUnderDirInJar(String dir) throws WTException {
        ArrayList list = new ArrayList();
        String filenames[] = jar.getFileNames();
        for (int i = 0; i < filenames.length; i++) {
            String fname = filenames[i];
            if (fname.startsWith(dir + "/") && fname.endsWith(".xml")) {
                list.add(fname);
            }
        }
        Collections.sort(list, new CmJarFileNameComparator());
        return list;
    }

    public ArrayList getXmlDocumentsUnderLikelyDirInJar(String dir) throws WTException {
        ArrayList list = new ArrayList();
        String filenames[] = jar.getFileNames();
        for (int i = 0; i < filenames.length; i++) {
            String fname = filenames[i];
            if (fname.startsWith(dir + "/") && fname.endsWith(".xml")) {
                list.add(fname);
            } else if (fname.startsWith(dir + "@") && fname.endsWith(".xml")) {
                list.add(fname);
            }
        }
        Collections.sort(list, new CmJarFileNameComparator());
        return list;
    }

    public boolean checkFileExistInJar(String cname) throws WTException {
        String filenames[] = jar.getFileNames();
        for (int i = 0; i < filenames.length; i++) {
            String fname = filenames[i];
            if (fname.equalsIgnoreCase(cname))
                return true;
        }

        return false;
    }

    public IxbDocument getIxbDocumentFromJar(String s) throws WTException {
        InputStream is = getContentAsInputStream(s);
        return IxbHelper.newIxbDocument(is, false);
    }

    public InputStream getContentAsInputStream(String s) throws WTException {
        try {
            return jar.getStreamByName(s);
        } catch (Exception exception) {
        	exception.printStackTrace();
            logger((new StringBuilder()).append("getContentAsInputStream contentId=<").append(s).append('>').toString());
            throw new WTException(exception);
        }
    }

    public Streamed getContentAsStreamed(String s) throws WTException {
        return super.getContentAsStreamed(s);
    }

    public InputStream getXMLEntryAsInputStream(String s) throws WTException {
        try {
            return jar.getStreamByName(s);
        } catch (IOException ioexception) {
            // logger((new
            // StringBuilder()).append("getXMLEntryAsInputStream zipEntry=<").append(s).append('>').toString());
            throw new WTException(ioexception);
        }
    }

    public void processException(Exception e) throws WTException {
        CharArrayWriter caw = new CharArrayWriter();
        PrintWriter pw = new PrintWriter(caw);
        e.printStackTrace(pw);
        pw.flush();
        // logger((new
        // StringBuilder()).append("****Import Exception as below****\n").append(caw.toString()).toString());
        if (e instanceof WTException)
            throw (WTException) e;
        else throw new WTException(e);
    }

    public boolean isLoopTest() {
        return PSEDO;
    }

    public String getLoopTestPrefix() {
        return PSEDO_PRE;
    }

    public String getLifecycleTemplateName() {
        // if (ic!=null&&ic.getLocalLifecycleTemplate()!=null)
        // return ic.getLocalLifecycleTemplate();
        // else
        // return null;

        return "图档生命周期";
    }

    //
    public boolean isChangeLifecycle() {
        // return ic!=null&&ic.getLocalLifecycleTemplate()!=null;

        return false;
    }

    public void cloaseHandler() {
        try {
            jar.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void pubImportedObject(Object obj, Object obj1) {
        importedObjMap.put(obj, obj1);
    }

    public Object getImportedObjectRemoteId(Object obj) {
        return importedObjMap.get(obj);
    }

    @Override
    public String adjustViewName(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void doOperationAfterStore(Iterated arg0, HashMap arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getLifecycleStateName() {
        // TODO Auto-generated method stub
        return "INWORK";
    }

    @Override
    public String getLocalImportFolder() {
        // TODO Auto-generated method stub
        return "/Default/test";
    }

    @Override
    public String getRemoteURL() {
        // TODO Auto-generated method stub
        return "dplm.geely.com";
    }

    @Override
    public boolean isChangeFolder() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isValid() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void logger(Object arg0) {
        // TODO Auto-generated method stub

    }

    public WTContainerRef getWTContainerRef(Class kass, String name) throws WTException {
        try {
            QuerySpec qs = new QuerySpec(kass);
            SearchCondition sc = new SearchCondition(kass,
                    WTContainer.NAME, SearchCondition.EQUAL, name, false);
            qs.appendSearchCondition(sc);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                WTContainer container = (WTContainer) qr.nextElement();
                return WTContainerRef.newWTContainerRef(container);
            }
        } catch (WTException ex) {
            ex.printStackTrace();
        }
        //WTOrganization org = getOrganizationByName("casc");
        //return WTContainerHelper.service.getOrgContainerRef(org);
        return null;
    }

    public static WTOrganization getOrganizationByName(String name) {
        WTOrganization org = null;
        try {
            QuerySpec qs = new QuerySpec(WTOrganization.class);
            SearchCondition sc = new SearchCondition(WTOrganization.class,
                    WTOrganization.NAME, SearchCondition.EQUAL, name, false);
            qs.appendSearchCondition(sc);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements())
                org = (WTOrganization) qr.nextElement();
        } catch (WTException ex) {
            ex.printStackTrace();
        }
        return org;
    }

    @Override
    public void putIntExistedNumberObject(String s, RevisionControlled revisoin) {
        // TODO Auto-generated method stub
        numberObject.put(s, revisoin);

    }

    @Override
    public HashMap<String, RevisionControlled> getExistedNumberObject() {
        // TODO Auto-generated method stub
        return numberObject;
    }

    @Override
    public void putNumberImplementAdvise(String number, String implement) {
        // TODO Auto-generated method stub
        numberImplementAdvise.put(number, implement);

    }

    @Override
    public HashMap<String, String> getNumberImplementAdvise() {
        // TODO Auto-generated method stub
        return numberImplementAdvise;
    }

}
