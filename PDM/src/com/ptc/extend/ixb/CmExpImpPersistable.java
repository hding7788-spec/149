package com.ptc.extend.ixb;

import com.bjsasc.avidm.mq.message.Based;
import com.ptc.extend.ixb.center.*;
import com.ptc.extend.util.Debug;
import com.ptc.extend.util.ObjectProperty;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;
import com.ptc.wvs.common.ui.Representer;
import com.ptc.wvs.common.ui.VisualizationHelper;
import ext.casc.synch.GLImportRecord;
import ext.casc.util.DBConn;
import ext.casc.util.Tools;
import ext.sast.center.util.ProductConvertUtil;
import ext.sast.common.fc.CmPersistenceHelper;
import org.json.JSONObject;
import wt.access.NotAuthorizedException;
import wt.admin.*;
import wt.change2.WTChangeOrder2;
import wt.content.*;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.facade.ixb.IxbElement;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTCollection;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.Foldered;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.ixb.clientAccess.StandardIXBService;
import wt.ixb.handlers.netmarkets.ProjectIXUtils;
import wt.ixb.publicforapps.IxbHelper;
import wt.ixb.publicforhandlers.IxbHndHelper;
import wt.ixb.publicforhandlers.LogHelper;
import wt.lifecycle.*;
import wt.org.*;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.project.Role;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.series.IntegerSeries;
import wt.series.MultilevelSeries;
import wt.session.SessionHelper;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.team.TeamManaged;
import wt.type.TypedUtility;
import wt.ufid.Federatable;
import wt.util.*;
import wt.vc.*;
import wt.viewmarkup.DerivedImage;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.wvs.VisualizationHelperFactory;

import java.beans.PropertyVetoException;
import java.io.*;
import java.text.MessageFormat;
import java.util.*;

public abstract class CmExpImpPersistable extends CmExpImpObject {
    String iname;
    String number;
    String version;
    String iteration;


    protected void recordErrorObject(String type,String currentVersion) {
        GLImportRecord record = new GLImportRecord();
        record.setKeyId(UUID.randomUUID().toString());
        record.setObjectType(type);
        record.setObjectNumber(this.number);
        record.setObjectVersion(this.version+"."+this.iteration);
        record.setCurrentObjectVersion(currentVersion);
        record.setImporter(this.getSendFrom());
        record.setNote("当前小版本较低，149存在更高的小版本");
        record.setMsgId(this.getMsgId());
        record.setFileName(this.getIxbFileName());
        record.setExtMsg("");
        record.setSynchTime(Tools.getCurrentFormatTime());
        try {
            CmPersistenceHelper.manager.insert(record);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	protected CmExpImpPersistable() {
    }

    protected CmExpImpPersistable(CmExporter expHdl)
            throws WTException {
        this.expHdl = expHdl;
        this.ixbdocument = IxbHelper.newIxbDocument();
        this.root = this.ixbdocument.createRootElement(getRootTag());
    }

    protected  boolean isNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
    protected CmExpImpPersistable(CmImporter impHdl, String fname) throws WTException {
        super(impHdl, fname);

        this.iname = getElementValue("name");
        this.number = getNoTrimElementValue("number");
        if (impHdl.isLoopTest())
            this.number = (impHdl.getLoopTestPrefix() + this.number);
        this.version = getElementValue(this.root, "versionInfo/versionId");
        this.iteration = getElementValue(this.root, "versionInfo/iterationId");
    }

    public String getIname() {
        return this.iname;
    }

    public String getNumber() {
        return this.number;
    }

    public String getVersion() {
        return this.version;
    }

    public String getIteration() {
        return this.iteration;
    }

    protected void exportUfidAttribute(Object obj, IxbElement ixbelement) throws WTException {
        if (!(obj instanceof Federatable))
            return;
        // Federatable federatable = (Federatable)obj;
        try {
            String s = IxbHndHelper.getBirthUfidOfObject((Federatable) obj);
            ixbelement.addValue("ObjectID/ufid", s);
        } catch (Exception exception) {
            logger.log("Exception in exportLocalIdAttribute, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
            processException(exception);
        }
    }

    protected void exportContainerPathAttribute(Object obj, IxbElement ixbelement) throws WTException {
        try {
            WTContained wtcontained = (WTContained) obj;
            WTContainerRef wtcontainerref = wtcontained.getContainerReference();
            String s = IxbHndHelper.getPathOfContainer(wtcontainerref);
            String localProductId = wtcontainerref.getObjectId().getId()+"";

            //TODO 本地型号转为中心域标准型号
            String productID = wtcontainerref.getName();
            String standardProductID =  ProductConvertUtil.getStandardProdutcName(productID);
            if(s!=null && s.indexOf("=")>0) {
            	if(productID!=null && standardProductID!=null&&!"".equals(standardProductID)){
                	s = s.replace(productID, standardProductID);
            	}
            }
            ixbelement.addValue("objectContainerPath", emptyIfNull(s));

            try {
                String sastProductID = ProductConvertUtil.getStandardProductByLocalProductId(localProductId);
                JSONObject json = ProductConvertUtil.getSastProdcutInfo(sastProductID);
                ixbelement.addValue("productiid",json.getString(Based.IID));
                ixbelement.addValue("productId",json.getString(Based.ID));
                ixbelement.addValue("productName",json.getString(Based.NAME));
            } catch (Exception e) {
                e.printStackTrace();

                //没有进行型号映射
                ixbelement.addValue("productiid",wtcontainerref.getObjectId().getId()+"");
                //ixbelement.addValue("productId",wtcontainerref.getName());
                ixbelement.addValue("productId",wtcontainerref.getName());
                ixbelement.addValue("productName",wtcontainerref.getName());

            }
        } catch (Exception exception) {
            logger.log("Exception in exportContainerPathAttribute, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
            processException(exception);
        }
    }

    protected String getDomainPath(DomainAdministered domainadministered) throws WTException {
        String s = null;
        AdminDomainRef admindomainref = DomainAdministeredHelper.getAdminDomainRef(domainadministered);
        try {
            s = AdministrativeDomainHelper.manager.getDomainPath(admindomainref);
            if (s.startsWith("[")) {
                int i = s.lastIndexOf("]");
                s = s.substring(i + 1);
            }
        } catch (WTRuntimeException wtruntimeexception) {
            Throwable throwable = wtruntimeexception.getNestedThrowable();
            if ((throwable instanceof NotAuthorizedException)) {
                NotAuthorizedException notauthorizedexception = (NotAuthorizedException) throwable;
                throw notauthorizedexception;
            }
            throw new WTException(throwable);
        }

        return s;
    }

    protected String getFolderPath(Foldered foldered) throws WTException {
        String s = null;
        try {
            s = foldered.getLocation();
        } catch (WTRuntimeException wtruntimeexception) {
            Throwable throwable = wtruntimeexception.getNestedThrowable();
            if ((throwable instanceof NotAuthorizedException)) {
                NotAuthorizedException notauthorizedexception = (NotAuthorizedException) throwable;
                throw notauthorizedexception;
            }
            throw new WTException(throwable);
        }

        return s;
    }

    protected void exportDomainFolderAttribute(Object obj) throws WTException {
        exportDomainFolderAttribute(obj, this.root);
    }

    protected void exportDomainFolderAttribute(Object obj, IxbElement ixbelement) throws WTException {
        try {
            if (!(obj instanceof Foldered))
                return;
            Foldered foldered = (Foldered) obj;
            if ((obj instanceof DomainAdministered)) {
                String s = getDomainPath((DomainAdministered) obj);
                ixbelement.addValue("domainName", emptyIfNull(s));
            }
            String s1 = getFolderPath(foldered);
            ixbelement.addValue("folderPath", emptyIfNull(s1));
        } catch (Exception exception) {
            logger.log("Exception in exportDomainFolderAttribute, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
            processException(exception);
        }
    }

    protected void exportVersionAttribute(Object obj) throws WTException {
        exportVersionAttribute(obj, this.root);
    }

    protected void exportVersionAttribute(Object obj, IxbElement ixbelement) throws WTException {
        try {
            if (!(obj instanceof Versioned))
                return;
            Versioned versioned = (Versioned) obj;
            String s = versioned.getMaster().getSeries();
            String s1 = versioned.getVersionInfo().getIdentifier().getSeries().getLevel().toString();
            String s2 = versioned.getVersionInfo().getIdentifier().getValue();
            String s3 = versioned.getIterationInfo().getIdentifier().getValue();
            ixbelement.addValue("versionInfo/versionId", emptyIfNull(s2));
            ixbelement.addValue("versionInfo/iterationId", emptyIfNull(s3));
            ixbelement.addValue("versionInfo/versionLevel", emptyIfNull(s1));
            ixbelement.addValue("versionInfo/series", emptyIfNull(s));
        } catch (Exception exception) {
            logger.log("Exception in exportVersionAttribute, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
            processException(exception);
        }
    }

    protected void exportLifecycleAttribute(Object obj) throws WTException {
        exportLifecycleAttribute(obj, this.root);
    }

    protected void exportLifecycleAttribute(Object obj, IxbElement ixbelement) throws WTException {
        try {
            if (!(obj instanceof LifeCycleManaged))
                return;
            LifeCycleManaged lifecyclemanaged = (LifeCycleManaged) obj;
            String s = lifecyclemanaged.getLifeCycleName();
            String s1 = lifecyclemanaged.getLifeCycleState().toString();
            ixbelement.addValue("lifecycleInfo/lifecycleTemplateName", emptyIfNull(s));
            ixbelement.addValue("lifecycleInfo/lifecycleState", emptyIfNull(s1));
        } catch (Exception exception) {
            logger.log("Exception in exportLifecycleAttribute, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
            processException(exception);
        }
    }

    protected void exportTeamAttribute(Object obj) throws WTException {
        exportTeamAttribute(obj, this.root);
    }

    protected void exportTeamAttribute(Object obj, IxbElement ixbelement) throws WTException {
        try {
            if (!(obj instanceof TeamManaged))
                return;
            TeamManaged teammanaged = (TeamManaged) obj;
            IxbElement teamelement = ixbelement.addElement("exportedRoleMemberMap");
            Team objTeam = TeamHelper.service.getTeam(teammanaged);
            if (objTeam == null)
                return;
            Vector objTeamRoles = TeamHelper.service.findRoles(objTeam);
            HashMap roleMap = TeamHelper.service.findAllParticipantsByRole(objTeam);
            for (int i = 0; i < objTeamRoles.size(); i++) {
                Role role = (Role) objTeamRoles.get(i);
                IxbElement ixbelement3 = teamelement.addElement("objectMember");
                ProjectIXUtils.addRoleElem(ixbelement3, role);
                ArrayList principalList = (ArrayList) roleMap.get(role);
                for (int j = 0; j < principalList.size(); j++) {
                    WTPrincipalReference wtpr = (WTPrincipalReference) principalList.get(j);
                    IxbElement ixbelement4 = ixbelement3.addElement("WTPrincipal");
                    if ((wtpr.getPrincipal() instanceof WTUser))
                        ixbelement4.addAttribute("type", "user");
                    else if ((wtpr.getPrincipal() instanceof WTGroup))
                        ixbelement4.addAttribute("type", "group");
                    else if ((wtpr.getPrincipal() instanceof WTOrganization))
                        ixbelement4.addAttribute("type", "organization");
                    else ixbelement4.addAttribute("type", "unknow");
                    ixbelement4.addValue("name", emptyIfNull(wtpr.getName()));
                }
            }
        } catch (Exception exception) {
            logger.log("Exception in exportTeamAttribute, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
            processException(exception);
        }
    }

    protected void exportContentItemAttribute(Object obj) throws WTException {
        exportContentItemAttribute(obj, this.root);
    }

    protected void exportContentItemAttribute(Object obj, IxbElement ixbelement) throws WTException {
        try {
            if (!(obj instanceof ContentHolder))
                return;
            ContentHolder contentholder = (ContentHolder) obj;
            contentholder = ContentHelper.service.getContents(contentholder);
            Vector vector = ContentHelper.getContentList(contentholder);
            if (vector != null) {
                for (int i = 0; i < vector.size(); i++) {
                    ContentItem contentitem = (ContentItem) vector.elementAt(i);
                    if ((contentitem instanceof ApplicationData))
                        storeApplicationContentItem(contentholder, ixbelement, (ApplicationData) contentitem);
                    else if ((contentitem instanceof URLData))
                        storeURLContentItem(ixbelement, (URLData) contentitem);
                    else if ((contentitem instanceof ExternalStoredData))
                        storeExternalContentItem(ixbelement, (ExternalStoredData) contentitem);
                    else {
                        Debug.info("Unknow Contentitem:", contentitem.getClass().getName());
                    }
                }
            }
            ApplicationData  ecnPdf =  getProcessEcn(contentholder);
            if(ecnPdf!=null){
                String tempFileName = ecnPdf.getFileName();
                if(ecnPdf.getFileName().equals("PDFPreview.pdf")){
                    tempFileName = "ecnPDFPreview.pdf";
                }
                storeApplicationContentItem(contentholder, ixbelement,  ecnPdf,tempFileName);
            }
            if ((contentholder instanceof FormatContentHolder)) {
                ContentItem contentitem = ContentHelper.getPrimary((FormatContentHolder) contentholder);
                if (contentitem != null)
                    if ((contentitem instanceof ApplicationData))
                        storeApplicationContentItem(contentholder, ixbelement, (ApplicationData) contentitem);
                    else if ((contentitem instanceof URLData))
                        storeURLContentItem(ixbelement, (URLData) contentitem);
                    else if ((contentitem instanceof ExternalStoredData))
                        storeExternalContentItem(ixbelement, (ExternalStoredData) contentitem);
                    else Debug.info("Unknow Contentitem:", contentitem.getClass().getName());
            }
        } catch (Exception exception) {
            logger.log("Exception in exportContentItemAttribute, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
            processException(exception);
        }
    }

    private ApplicationData getProcessEcn(ContentHolder contentholder) {
        ApplicationData returnApp = null;
        if(contentholder instanceof WTDocument){
            try {
                WTDocument doc = (WTDocument) contentholder;
                String typeName = TypedUtility.getTypeIdentifier(doc).getTypename();
                if (typeName.contains("PROCESS_PLAN")){
                    WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(doc);
                    Iterator it = coll.iterator();
                    if (it.hasNext()) {
                        String state = doc.getState().getState().getDisplay(Locale.CHINA);

                        WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it.next()).getObject();
                        QueryResult qr = ContentHelper.service.getContentsByRole(ecn, ContentRoleType.SECONDARY);
                        while (qr.hasMoreElements()) {
                            Object obj = qr.nextElement();
                            if (obj instanceof ApplicationData) {
                                ApplicationData data = (ApplicationData) obj;
                                if("已批准".equals(state)) {
                                    if(data.getFileName().startsWith("Print_")){
                                        returnApp = data;
                                        break;
                                    }
                                }else{
                                    returnApp = data;
                                }

                            }
                        }
                    }
                }
            } catch (WTException e) {
                e.printStackTrace();
            }
        }
        return returnApp;
    }

    private void storeURLContentItem(IxbElement ixbelement, URLData urldata) throws WTException {
        IxbElement ixbelement1 = ixbelement.addElement("contentItem");
        ixbelement1.addValue("contentId", NO_VALUE);
        ixbelement1.addValue("contentType", "URLData");
        ixbelement1.addValue("category", emptyIfNull(urldata.getCategory()));
        ixbelement1.addValue("fileName", NO_VALUE);
        ixbelement1.addValue("role", emptyIfNull(urldata.getRole().toString()));
        ixbelement1.addValue("descriptor", emptyIfNull(urldata.getDescription()));
        ixbelement1.addValue("urlLocation", emptyIfNull(urldata.getUrlLocation()));
        ixbelement1.addValue("comments", emptyIfNull(urldata.getComments()));
        ixbelement1.addValue("lineNumber", Integer.toString(urldata.getLineNumber()));
        ixbelement1.addValue("distributable", Boolean.toString(urldata.isDistributable()));
        ixbelement1.addValue("displayName", emptyIfNull(urldata.getDisplayName()));
    }

    private void storeExternalContentItem(IxbElement ixbelement, ExternalStoredData externalstoreddata)
            throws WTException {
        IxbElement ixbelement1 = ixbelement.addElement("contentItem");
        ixbelement1.addValue("contentId", NO_VALUE);
        ixbelement1.addValue("contentType", "ExternalStoredData");
        ixbelement1.addValue("category", emptyIfNull(externalstoreddata.getCategory()));
        ixbelement1.addValue("fileName", NO_VALUE);
        ixbelement1.addValue("role", emptyIfNull(externalstoreddata.getRole().toString()));
        ixbelement1.addValue("descriptor", emptyIfNull(externalstoreddata.getDescription()));
        ixbelement1.addValue("urlLocation", NO_VALUE);
        ixbelement1.addValue("comments", emptyIfNull(externalstoreddata.getComments()));
        ixbelement1.addValue("lineNumber", Integer.toString(externalstoreddata.getLineNumber()));
        ixbelement1.addValue("distributable", Boolean.toString(externalstoreddata.isDistributable()));
        ixbelement1.addValue("externalLocation", externalstoreddata.getExternalLocation());
        ixbelement1.addValue("displayName", emptyIfNull(externalstoreddata.getDisplayName()));
    }

    private String storeApplicationContentItem(ContentHolder obj, IxbElement ixbelement, ApplicationData applicationdata)
            throws WTException {
        //windchill11会将可视化jpg一并导出，这边做兼容，只导出主内容与附件
        String appType = applicationdata.getRole().toString();
        if(!appType.equals(MQExpImpConstants.PRIMARY)&&!appType.equals(MQExpImpConstants.SECONDARY)){
            return null;
        }
        String saveName = MessageFormat.format("{0,number,00000000}",
                new Object[] { Integer.valueOf(this.expHdl.getNextSequence()) });
        String s = getSavePathInJar(obj) + "/" + "CONTENTS" + "/" + saveName;

        IxbElement ixbelement1 = ixbelement.addElement("contentItem");

        ixbelement1.addValue("contentId", s);
        ixbelement1.addValue("contentType", "ApplicationData");
        ixbelement1.addValue("category", emptyIfNull(applicationdata.getCategory()));
        ixbelement1.addValue("fileName", emptyIfNull(applicationdata.getFileName()));
        ixbelement1.addValue("role", emptyIfNull(applicationdata.getRole().toString()));
        ixbelement1.addValue("descriptor", emptyIfNull(applicationdata.getDescription()));
        ixbelement1.addValue("urlLocation", NO_VALUE);
        ixbelement1.addValue("comments", emptyIfNull(applicationdata.getComments()));
        ixbelement1.addValue("lineNumber", emptyIfNull(Integer.toString(applicationdata.getLineNumber())));
        ixbelement1.addValue("distributable", Boolean.toString(applicationdata.isDistributable()));
        ixbelement1.addValue("toolVersion", emptyIfNull(applicationdata.getToolVersion()));
        ixbelement1.addValue("toolName", emptyIfNull(applicationdata.getToolName()));
        ixbelement1.addValue("fileVersion", emptyIfNull(applicationdata.getFileVersion()));

        Streamed streamed = (Streamed) applicationdata.getStreamData().getObject();
        InputStream is = streamed.retrieveStream();
        if (is == null)
            is = new ByteArrayInputStream(new byte[0]);
        this.expHdl.reallyStoreContent(is, s);
        return s;
    }

    private String storeApplicationContentItem(ContentHolder obj, IxbElement ixbelement, ApplicationData applicationdata,String fileName)
            throws WTException {
        //windchill11会将可视化jpg一并导出，这边做兼容，只导出主内容与附件
        String appType = applicationdata.getRole().toString();
        if(!appType.equals(MQExpImpConstants.PRIMARY)&&!appType.equals(MQExpImpConstants.SECONDARY)){
            return null;
        }
        String saveName = MessageFormat.format("{0,number,00000000}",
                new Object[] { Integer.valueOf(this.expHdl.getNextSequence()) });
        String s = getSavePathInJar(obj) + "/" + "CONTENTS" + "/" + saveName;

        IxbElement ixbelement1 = ixbelement.addElement("contentItem");

        ixbelement1.addValue("contentId", s);
        ixbelement1.addValue("contentType", "ApplicationData");
        ixbelement1.addValue("category", emptyIfNull(applicationdata.getCategory()));
        ixbelement1.addValue("fileName",fileName);
        ixbelement1.addValue("role", emptyIfNull(applicationdata.getRole().toString()));
        ixbelement1.addValue("descriptor", emptyIfNull(applicationdata.getDescription()));
        ixbelement1.addValue("urlLocation", NO_VALUE);
        ixbelement1.addValue("comments", emptyIfNull(applicationdata.getComments()));
        ixbelement1.addValue("lineNumber", emptyIfNull(Integer.toString(applicationdata.getLineNumber())));
        ixbelement1.addValue("distributable", Boolean.toString(applicationdata.isDistributable()));
        ixbelement1.addValue("toolVersion", emptyIfNull(applicationdata.getToolVersion()));
        ixbelement1.addValue("toolName", emptyIfNull(applicationdata.getToolName()));
        ixbelement1.addValue("fileVersion", emptyIfNull(applicationdata.getFileVersion()));

        Streamed streamed = (Streamed) applicationdata.getStreamData().getObject();
        InputStream is = streamed.retrieveStream();
        if (is == null)
            is = new ByteArrayInputStream(new byte[0]);
        this.expHdl.reallyStoreContent(is, s);
        return s;
    }

    protected void exportMarkupObjects(Object obj) throws WTException {
        exportMarkupObjects(obj, this.root);
    }

    protected void exportMarkupObjects(Object obj, IxbElement ixbelement) throws WTException {
        try {
            File tmpfile = StandardIXBService.getSaveFileOnServer();
            String fullfile = tmpfile.getCanonicalPath();
            if ((VisualizationHelperFactory.HELPER.saveMarkupsAsZIPFile(getRefFromObject((Persistable) obj), false,
                    fullfile, null)) &&
                    (tmpfile.exists())) {
                String s = getSavePathInJar(obj) + "/" + "CONTENTS" + "/" + "Markup.jar";
                FileInputStream fis = new FileInputStream(tmpfile);
                this.expHdl.reallyStoreContent(fis, s);
                fis.close();
                tmpfile.delete();
                // IxbElement localIxbElement = ixbelement.addValue("markUp/id", emptyIfNull(s));
                ixbelement.addValue("markUp/id", emptyIfNull(s));
            }
        } catch (Exception e) {
            logger.log("Exception in exportMarkupObjects, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
            processException(e);
        }
    }

    protected void exportRepresentationAttribute(Object obj) throws WTException {
        exportRepresentationAttribute(obj, this.root);
    }

    protected void exportRepresentationAttribute(Object obj, IxbElement ixbelement) throws WTException {
        try {
            if (!(obj instanceof Representable)) {
                return;
            }
            VisualizationHelper helper = new VisualizationHelper();
			QueryResult qr = helper.getRepresentations((Representable) obj);
			int count = 0;
            while (qr.hasMoreElements()) {
                IxbElement ixbelement1 = ixbelement.addElement("Representation");
                Representation rep = (Representation) qr.nextElement();
                ixbelement1.addValue("description", emptyIfNull(rep.getDescription()));
                // ixbelement1.addValue("default", String.valueOf(rep.isDefaultRepresentation()));
                ixbelement1.addValue("default", String.valueOf(rep.getDefaultRepresentation()));
                if (((rep instanceof DerivedImage)) && (((DerivedImage) rep).getDerivedFromReference() != null))
                    ixbelement1.addValue("republishable", "true");
                else ixbelement1.addValue("republishable", "false");
                ixbelement1.addValue("name", emptyIfNull(rep.getName()));
                exportRepresentationContent(obj, rep, ixbelement1, count);
                count++;
            }
        } catch (Exception exception) {
            logger.log("Exception in exportRepresentationAttribute, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
            processException(exception);
        }
    }

    private void exportRepresentationContent(Object obj, Representation rep, IxbElement ixbelement, int count)
            throws WTException {
        try {
            File tmpfile = StandardIXBService.getSaveFileOnServer();
            String fullfile = tmpfile.getCanonicalPath();
            Debug.P("fullfile:", fullfile);
            if (new Representer().saveAsZIPFile(getRefFromObject(rep), false, true, fullfile)) {
                String s = getSavePathInJar(obj) + "/" + "CONTENTS" + "/" + "Representation-" + String.valueOf(count)
                        + ".jar";
                FileInputStream fis = new FileInputStream(tmpfile);
                this.expHdl.reallyStoreContent(fis, s);
                fis.close();
                // IxbElement localIxbElement = ixbelement.addValue("repContent/id", emptyIfNull(s));
                ixbelement.addValue("repContent/id", emptyIfNull(s));
            }
            tmpfile.delete();
        } catch (IOException ioe) {
            throw new WTException(ioe);
        }
    }

    protected LifeCycleManaged importLifecycleAttribute(Object obj) throws Exception {
        return importLifecycleAttribute(obj, this.root);
    }

    protected LifeCycleManaged importLifecycleAttribute(Object obj, IxbElement ixbelement) throws Exception {
        LifeCycleManaged lifecyclemanaged = (LifeCycleManaged) obj;



            String lifecycleState;
            String lifecycleTemplate;

            lifecycleTemplate = getElementValue(ixbelement, "lifecycleInfo/lifecycleTemplateName");
            if (lifecycleTemplate != null) {
                lifecycleTemplate = getPropertiesValue(lifecycleTemplate);
            }
            lifecycleState = getElementValue(ixbelement, "lifecycleInfo/lifecycleState");
            if (lifecycleState != null) {
                lifecycleState = getLifeCyclePropertiesValue(lifecycleState);
            }

          if(obj!=null && getSendFrom()!=null && getSendFrom().contains("805")&&"F".equals(this.version)){
              lifecycleState = "OBSOLESCENCE";
          }
            if ((lifecycleTemplate == null) || (lifecycleState == null))
                return lifecyclemanaged;
            WTContainerRef wtcontainerref = getWTContainerRef(this.root);
            if(wtcontainerref==null){
                wtcontainerref = this.impHdl.getWTContainerRef();
            }
            if(wtcontainerref==null){
				///wt.inf.container.OrgContainer=sast/wt.inf.library.WTLibrary=标准紧固件库
				 String objectContainerPath = getElementValue(root, "objectContainerPath");
				 if("/".equals(objectContainerPath)){
					 objectContainerPath ="/wt.inf.container.OrgContainer=sast/wt.inf.library.WTLibrary=八院标准紧固件库" ;
				 }
				 String tempPath = objectContainerPath.substring(objectContainerPath.indexOf("=")+1);;
				 String  clazz= tempPath.substring(tempPath.indexOf("/")+1,tempPath.indexOf("="));
				 String containerRefName = tempPath.substring(tempPath.indexOf("=")+1);
				 wtcontainerref = impHdl.getWTContainerRef(Class.forName(clazz), containerRefName);
			}
            LifeCycleTemplateReference lifecycletemplatereference = LifeCycleHelper.service
                    .getLifeCycleTemplateReference(lifecycleTemplate, wtcontainerref);
            if (lifecycletemplatereference == null) {
                Object[] aobj = { lifecycleTemplate };
                throw new WTException("wt.ixb.publicforhandlers.imp.IXBImpConflictRB", "6", aobj);
            }
            if (PersistenceHelper.isPersistent(lifecyclemanaged)) {
                if (!lifecyclemanaged.getLifeCycleTemplate().getName().equals(lifecycletemplatereference.getName())) {
                    Debug.P("Reassign Lifecycle.");
                    Iterated latest = VersionControlHelper.getLatestIteration((Iterated) lifecyclemanaged);
                    latest = (Iterated) LifeCycleHelper.service.reassign((LifeCycleManaged) latest,
                            lifecycletemplatereference);
                }
                if ((lifecyclemanaged instanceof WTDocument)) {
                    lifecyclemanaged = (LifeCycleManaged) PersistenceHelper.manager.refresh(lifecyclemanaged);
                    lifecyclemanaged = LifeCycleHelper.service.reassign(lifecyclemanaged, lifecycletemplatereference);
                }
                lifecyclemanaged = LifeCycleHelper.service.setLifeCycleState(lifecyclemanaged,
                        State.toState(lifecycleState));
            } else {
                LifeCycleTemplate lifecycletemplate = (LifeCycleTemplate) lifecycletemplatereference.getObject();
                lifecyclemanaged = LifeCycleHelper.setLifeCycle(lifecyclemanaged, lifecycletemplate);
                try{
                    lifecyclemanaged = LifeCycleServerHelper.setState(lifecyclemanaged, State.toState(lifecycleState));
                }catch (WTInvalidParameterException e){
                	e.printStackTrace();
                	throw new WTException(e.getLocalizedMessage());
                }
            }

        return lifecyclemanaged;
    }

    protected Versioned importVersionAttribute(Object obj) throws WTException {
        return importVersionAttribute(obj, this.root);
    }

    protected Versioned importVersionAttribute(Object obj, IxbElement ixbelement) throws WTException {
        Versioned versioned = (Versioned) obj;
        try {
            String s1 = getElementValue(ixbelement, "versionInfo/versionId");
            String s2 = getElementValue(ixbelement, "versionInfo/versionLevel");
            String s4 = getElementValue(ixbelement, "versionInfo/iterationId");
            String series = getElementValue(ixbelement, "versionInfo/series");
            String s = series;
            //s = getPropertiesValue(s);
            //s1 = getPropertiesValue(s1);
            if(!"zyk".equals(this.getSendFrom())) {
                s = MQExpImpConstants.SERIES;

                if(getSendFrom()!=null && !getSendFrom().contains("805")){
                    s1 = ext.sast.center.synch.MQExpImpUtil.attrConvertValue("versionInfo", s1, "IMP");
                }else{
                    if(Character.isUpperCase(s1.charAt(0))&&!"Z".equals(s1)){//如果是A、B、C、D标准序列
                        //805的科瑞数据需版本映射
                        s1 = ext.sast.center.synch.MQExpImpUtil.attrConvertValue("versionInfo", s1, "IMP");
                    }

                }
            }

            if ((s1 == null) || (s4 == null) || (s2 == null) || (s1.trim().length() == 0) || (s4.trim().length() == 0)
                    || (s2.trim().length() == 0)) {
                logger.log("ixb - missing xml tag attributes - version: " + s1 + " iteration: " + s4
                        + "  versionLevel: " + s2);
                Object[] aobj1 = new Object[0];
                throw new LogHelper.IxbException("wt.ixb.publicforhandlers.ixbResource", "79", aobj1);
            }
            versioned.getMaster().setSeries(s);
            VersionControlServerHelper.initalizeIterationCookies(versioned);
            Integer integer = Integer.valueOf(s2);
            MultilevelSeries multilevelseries = VersionControlHelper.getVersionIdentifierSeries(versioned);
            if (multilevelseries == null) {
                throw new WTException("version series is null");
            }
            multilevelseries.setValueWithValidation(s1, integer);
            VersionIdentifier versionidentifier = VersionIdentifier.newVersionIdentifier(multilevelseries);
            Integer integer1 = Integer.valueOf(s4);
            IntegerSeries integerseries = IntegerSeries.newIntegerSeries(integer1);
            IterationIdentifier iterationidentifier = IterationIdentifier.newIterationIdentifier(integerseries);
            VersionControlHelper.setIterationIdentifier(versioned, iterationidentifier);
            VersionControlHelper.setVersionIdentifier(versioned, versionidentifier, false);
        } catch (Exception e) {
            logger.log("Exception in exportVersionAttribute, ob=<" + obj + ">");
            processException(e);
        }
        return versioned;
    }

    protected Versioned importVersionAttribute2(Object obj, IxbElement ixbelement) throws WTException {
        Versioned versioned = (Versioned) obj;
        try {
            String s1 = getElementValue(ixbelement, "versionInfo/versionId");
            String s2 = getElementValue(ixbelement, "versionInfo/versionLevel");
            String s4 = getElementValue(ixbelement, "versionInfo/iterationId");
            String s = getElementValue(ixbelement, "versionInfo/series");
            //s = getPropertiesValue(s);
            //s1 = getPropertiesValue(s1);
            if(!"zyk".equals(this.getSendFrom())) {
                //s = MQExpImpConstants.SERIES;

                if(getSendFrom()!=null && !getSendFrom().contains("805")){
                    s1 = ext.sast.center.synch.MQExpImpUtil.attrConvertValue("versionInfo", s1, "IMP");
                }
            }

            if ((s1 == null) || (s4 == null) || (s2 == null) || (s1.trim().length() == 0) || (s4.trim().length() == 0)
                    || (s2.trim().length() == 0)) {
                logger.log("ixb - missing xml tag attributes - version: " + s1 + " iteration: " + s4
                        + "  versionLevel: " + s2);
                Object[] aobj1 = new Object[0];
                throw new LogHelper.IxbException("wt.ixb.publicforhandlers.ixbResource", "79", aobj1);
            }
            versioned.getMaster().setSeries(s);
            VersionControlServerHelper.initalizeIterationCookies(versioned);
            Integer integer = Integer.valueOf(s2);
            MultilevelSeries multilevelseries = VersionControlHelper.getVersionIdentifierSeries(versioned);
            if (multilevelseries == null) {
                throw new WTException("version series is null");
            }
            multilevelseries.setValueWithValidation(s1, integer);
            VersionIdentifier versionidentifier = VersionIdentifier.newVersionIdentifier(multilevelseries);
            Integer integer1 = Integer.valueOf(s4);
            IntegerSeries integerseries = IntegerSeries.newIntegerSeries(integer1);
            IterationIdentifier iterationidentifier = IterationIdentifier.newIterationIdentifier(integerseries);
            VersionControlHelper.setIterationIdentifier(versioned, iterationidentifier);
            VersionControlHelper.setVersionIdentifier(versioned, versionidentifier, false);
        } catch (Exception e) {
            logger.log("Exception in exportVersionAttribute, ob=<" + obj + ">");
            processException(e);
        }
        return versioned;
    }

    protected Foldered importDomainFolderAttribute(Object obj) throws WTException {
        return importDomainFolderAttribute(obj, this.root);
    }

    protected Foldered importDomainFolderAttribute(Object obj, IxbElement ixbelement) throws WTException {
        Foldered foldered = (Foldered) obj;
        try {
            /*if (getElementValue(this.root, "folderPath") == null)
                return foldered;*/
            WTContainerRef wtcontainerref = getWTContainerRef(this.root);
            if (wtcontainerref == null) {
            	wtcontainerref = this.impHdl.getWTContainerRef();
			}
            if(wtcontainerref==null){
				///wt.inf.container.OrgContainer=sast/wt.inf.library.WTLibrary=标准紧固件库
				 String objectContainerPath = getElementValue(root, "objectContainerPath");
				 if("/".equals(objectContainerPath)){
					 objectContainerPath ="/wt.inf.container.OrgContainer=sast/wt.inf.library.WTLibrary=八院标准紧固件库" ;
				 }
				 String tempPath = objectContainerPath.substring(objectContainerPath.indexOf("=")+1);
				 String  clazz= tempPath.substring(tempPath.indexOf("/")+1,tempPath.indexOf("="));
				 String containerRefName = tempPath.substring(tempPath.indexOf("=")+1);
				 wtcontainerref = impHdl.getWTContainerRef(Class.forName(clazz), containerRefName);
			}
            String s = getFolderPathWithReplacedCabinet(ixbelement, wtcontainerref);
            String s2 = foldered.getFolderPath();
            if (s2 == null) {
                if (s == null) {
                    s = WTContainerHelper.DEFAULT_CABINET_NAME;
                }
                AdminDomainRef admindomainref = getDomainRef(s, wtcontainerref);
                Folder folder = IxbHndHelper.getFolder(s, admindomainref, wtcontainerref);
                FolderHelper.assignLocation(foldered, folder);
            }
        } catch (Exception e) {
            logger.log("Exception in exportDomainFolderAttribute, ob=<" + obj + ">");
            processException(e);
        }
        return foldered;
    }

    private String getFolderPathWithReplacedCabinet(IxbElement ixbelement, WTContainerRef wtcontainerref)
            throws WTException {
        String s = getElementValue(this.root, "folderPath");
        String flag =null;
        try {
			flag = getPropertiesValue("ISSTANDARDIMPORT");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (s == null) {
            s = WTContainerHelper.DEFAULT_CABINET_NAME;
        }else{
        	if (flag !=null) {
				if (flag.equals("true")) {

				}else{
				    if("外来数据产品库".equals(wtcontainerref.getName())){
				        String objectContainerPath = getElementValue(ixbelement,"objectContainerPath");
                        if (objectContainerPath.indexOf("PDMLinkProduct") > 0) {
                            int index = objectContainerPath.lastIndexOf("=");
                            String productName = objectContainerPath.substring(index + 1, objectContainerPath.length());
                            if(productName.contains("/")){
                                productName = productName.replace("/","_");
                            }
                            String sendFrom = getSendFrom();
                            if(sendFrom.contains("/")){
                                sendFrom = sendFrom.replace("/","_");
                            }
                            s = s.replaceFirst("/Default", "/Default/" + sendFrom + "/" + productName);
                        }else{
                            if (!"zyk".equals(getSendFrom())) {
                                s = s.replaceFirst("/Default", "/Default/01设计文件");
                            }
                        }
                    }else{
                        if (!"zyk".equals(getSendFrom())) {
                            s = s.replaceFirst("/Default", "/Default/01设计文件");
                        }
                    }

				}
			}else{
                if("外来数据产品库".equals(wtcontainerref.getName())){
                    String objectContainerPath = getElementValue(ixbelement,"objectContainerPath");
                    if (objectContainerPath.indexOf("PDMLinkProduct") > 0) {
                        int index = objectContainerPath.lastIndexOf("=");
                        String productName = objectContainerPath.substring(index + 1, objectContainerPath.length());
                        if(productName.contains("/")){
                            productName = productName.replace("/","_");
                        }
                        String sendFrom = getSendFrom();
                        if(sendFrom.contains("/")){
                            sendFrom = sendFrom.replace("/","_");
                        }
                        s = s.replaceFirst("/Default", "/Default/" + sendFrom + "/" + productName);
                    }else{
                        if (!"zyk".equals(getSendFrom())) {
                            s = s.replaceFirst("/Default", "/Default/01设计文件");
                        }
                    }
                }else{
                    if(!"zyk".equals(getSendFrom())){
                        s = s.replaceFirst("/Default", "/Default/01设计文件");
                    }
                }
			}
        }

        s = replaceCabinet(s);
        return s;
    }

    private String replaceCabinet(String s) throws WTException {
        String s1 = WTContainerHelper.DEFAULT_CABINET_NAME;
        String s2 = getCabinetSubstring(s);
        if (s2.equals(WTContainerHelper.SYSTEM_CABINET_NAME))
            s1 = s2;
        if (!s1.startsWith("/"))
            s1 = "/" + s1;
        String s3 = getSubFolderSubstring(s);
        if ((s3 == null) || (s3.equals(""))) {
            if (this.impHdl.isChangeFolder()) {
                return s1 + "/" + this.impHdl.getLocalImportFolder();
            }
            return s1;
        }
        if (this.impHdl.isChangeFolder()) {
            return s1 + "/" + this.impHdl.getLocalImportFolder() + "/" + s3;
        }
        return s1 + "/" + s3;
    }

    private String getSubFolderSubstring(String s) throws WTException {
        int i = s.length();
        int j = s.indexOf('/', 1);
        if ((j < 0) || (j == i)) {
            return "";
        }
        return s.substring(j + 1, i);
    }

    private String getCabinetSubstring(String s) throws WTException {
        int i = s.length();
        int j = s.indexOf('/', 1);
        if ((j < 0) || (j == i)) {
            return s.substring(1);
        }
        return s.substring(1, j);
    }

    private AdminDomainRef getDomainRef(String s, WTContainerRef wtcontainerref) throws WTException {
        AdminDomainRef admindomainref = null;
        if (s != null) {
            AdministrativeDomain administrativedomain = findDomain(s, wtcontainerref);
            if (administrativedomain == null)
                administrativedomain = wtcontainerref.getContainer().getDefaultDomain();
            if (administrativedomain != null)
                admindomainref = AdminDomainRef.newAdminDomainRef(administrativedomain);
        }
        return admindomainref;
    }

    private AdministrativeDomain getDomain(String s, WTContainerRef wtcontainerref) {
        AdministrativeDomain administrativedomain = null;
        try {
            administrativedomain = AdministrativeDomainHelper.manager.getDomain(s, wtcontainerref);
        } catch (Exception exception) {
            logger.log("Exception in getDomain(), domainPath=" + s + "container= " + wtcontainerref.getName());
        }
        return administrativedomain;
    }

    private AdministrativeDomain findDomain(String s, WTContainerRef wtcontainerref) throws WTException {
        if (s == null)
            return wtcontainerref.getContainer().getDefaultDomain();
        AdministrativeDomain administrativedomain = getDomain(s, wtcontainerref);
        if (administrativedomain == null) {
            WTContainerRef wtcontainerref1 = wtcontainerref.getParentRef();
            administrativedomain = getDomain(s, wtcontainerref1);
        }
        if (administrativedomain == null)
            administrativedomain = getDomain(s, WTContainerHelper.getExchangeRef());
        return administrativedomain;
    }

    protected ContentHolder importContentItemAttribute(Object obj) throws WTException {
        return importContentItemAttribute(obj, this.root);
    }

    protected ContentHolder importContentItemAttribute(Object obj, IxbElement ixbelement) throws WTException {
        ContentHolder contentholder = (ContentHolder) obj;
        Transaction tx = null;
        try {
            Enumeration enumeration = ixbelement.getElements("contentItem");
            if ((enumeration == null) || (!enumeration.hasMoreElements()))
                return contentholder;
            tx = new Transaction();
            tx.start();
            contentholder = (ContentHolder) PersistenceHelper.manager.lockAndRefresh(contentholder);
            contentholder = initImportContentHolder(contentholder);
            while (enumeration.hasMoreElements()) {
                IxbElement ixbelement2 = (IxbElement) enumeration.nextElement();
                String s1 = getElementValue(ixbelement2, "contentType");
                if (s1.equals("URLData"))
                    importURLDataContent(contentholder, ixbelement2);
                else if (s1.equals("ApplicationData"))
                    importAppDataContent(contentholder, ixbelement2);
                else if (s1.equals("ExternalStoredData"))
                    importExternalDataContent(contentholder, ixbelement2);
                else logger.log("importContentHolder: unknown type of content item:<" + s1 + ">");
            }
            if ((contentholder instanceof FormatContentHolder))
                contentholder = ContentServerHelper.service.updateHolderFormat((FormatContentHolder) contentholder);
            contentholder = (ContentHolder) PersistenceHelper.manager.refresh(contentholder);
            tx.commit();
            tx = null;
        } catch (Exception e) {
            logger.log("Exception in exportContentItemAttribute, ob=<" + obj + ">");
            processException(e);
        } finally {
            if (tx != null)
                tx.rollback();
        }
        return contentholder;
    }

    protected void populateContentItemData(ContentItem contentitem, IxbElement ixbelement) throws WTException {
        String s = getElementValue(ixbelement, "category");
        String s1 = getElementValue(ixbelement, "role");
        ContentRoleType contentroletype = ContentRoleType.toContentRoleType(s1);
        String s2 = getElementValue(ixbelement, "descriptor");
        String s3 = getElementValue(ixbelement, "comments");
        String s4 = getElementValue(ixbelement, "lineNumber");
        String s5 = getElementValue(ixbelement, "distributable");
        try {
            if (s != null)
                contentitem.setCategory(s);
            if (contentroletype != null)
                contentitem.setRole(contentroletype);
            if (s2 != null)
                contentitem.setDescription(s2);
            if (s3 != null)
                contentitem.setComments(s3);
            if (s4 != null&&!"".equals(s4)) {
                int i = Integer.parseInt(s4);
                contentitem.setLineNumber(i);
            }
            if (s5 != null&&!"".equals(s5))
                contentitem.setDistributable(Boolean.parseBoolean(s5));
        } catch (WTPropertyVetoException wtpropertyvetoexception) {
            throw new WTException(wtpropertyvetoexception);
        }
    }

    protected void importURLDataContent(ContentHolder contentholder, IxbElement ixbelement) throws WTException {
        URLData urldata = URLData.newURLData(contentholder);
        populateContentItemData(urldata, ixbelement);
        try {
            String s = getElementValue(ixbelement, "urlLocation");
            String s1 = getElementValue(ixbelement, "displayName");
            if (s != null)
                urldata.setUrlLocation(s);
            if (s1 != null)
                urldata.setDisplayName(s1);
            urldata = ContentServerHelper.service.updateContent(contentholder, urldata);
        } catch (WTPropertyVetoException wtpropertyvetoexception) {
            throw new WTException(wtpropertyvetoexception);
        } catch (PropertyVetoException propertyvetoexception) {
            throw new WTException(propertyvetoexception);
        }
    }

    private void importAppDataContent(ContentHolder contentholder, IxbElement ixbelement) throws WTException {
        ApplicationData applicationdata = ApplicationData.newApplicationData(contentholder);
        populateContentItemData(applicationdata, ixbelement);
        try {
            String s = getElementValue(ixbelement, "fileName");
            String s1 = getElementValue(ixbelement, "toolName");
            String s2 = getElementValue(ixbelement, "toolVersion");
            String s3 = getElementValue(ixbelement, "fileVersion");
            if (s != null)
                applicationdata.setFileName(s);
            if (s3 != null)
                applicationdata.setFileVersion(s3);
            if (s1 != null)
                applicationdata.setToolName(s1);
            if (s2 != null)
                applicationdata.setToolVersion(s2);
            String s4 = getElementValue(ixbelement, "contentId");
            InputStream inputstream = this.impHdl.getContentAsInputStream(s4);
            if (inputstream != null)
                applicationdata = ContentServerHelper.service
                        .updateContent(contentholder, applicationdata, inputstream);
        } catch (WTPropertyVetoException wtpropertyvetoexception) {
            throw new WTException(wtpropertyvetoexception);
        } catch (PropertyVetoException propertyvetoexception) {
            throw new WTException(propertyvetoexception);
        } catch (Exception exception) {
            throw new WTException(exception);
        }
    }

    protected void importExternalDataContent(ContentHolder contentholder, IxbElement ixbelement) throws WTException {
        ExternalStoredData externalstoreddata = ExternalStoredData.newExternalStoredData();
        populateContentItemData(externalstoreddata, ixbelement);
        try {
            String s = getElementValue(ixbelement, "externalLocation");
            String s1 = getElementValue(ixbelement, "displayName");
            if (s != null)
                externalstoreddata.setExternalLocation(s);
            if (s1 != null)
                externalstoreddata.setDisplayName(s1);
            externalstoreddata = ContentServerHelper.service.updateContent(contentholder, externalstoreddata);
        } catch (WTPropertyVetoException wtpropertyvetoexception) {
            throw new WTException(wtpropertyvetoexception);
        } catch (PropertyVetoException propertyvetoexception) {
            throw new WTException(propertyvetoexception);
        }
    }

    protected ContentHolder initImportContentHolder(ContentHolder contentholder) throws WTException {
        try {
            contentholder = ContentHelper.service.getContents(contentholder);
            Vector vector = ContentHelper.getContentList(contentholder);
            if (vector != null) {
                for (int i = 0; i < vector.size(); i++) {
                    ContentItem contentitem1 = (ContentItem) vector.elementAt(i);
                    if(contentitem1 instanceof ApplicationData){
                    	ApplicationData appdata = (ApplicationData)contentitem1;
                    	 if(appdata!=null&&appdata.getFileName()!=null&&appdata.getFileName().contains("signature_emps")){
                    		 continue;
                    	 }
                    }
                    ContentServerHelper.service.deleteContent(contentholder, contentitem1);
                }
            }
            if ((contentholder instanceof FormatContentHolder)) {
                ContentItem contentitem = ContentHelper.getPrimary((FormatContentHolder) contentholder);
                if (contentitem != null)
                    ContentServerHelper.service.deleteContent(contentholder, contentitem);
            }
        } catch (Exception exception) {
            logger.log("importContentHolder: could not delete content for object=<" + contentholder + ">");
            processException(exception);
        }
        return contentholder;
    }

    protected Representable importRepresentationAttribute(Object obj) throws WTException {
        return importRepresentationAttribute(obj, this.root);
    }

    protected Representable importRepresentationAttribute(Object obj, IxbElement ixbelement) throws WTException {
        Representable rep = (Representable) obj;
        Transaction tx = null;
        try {
            Enumeration enumeration = ixbelement.getElements("Representation");
            if ((enumeration == null) || (!enumeration.hasMoreElements()))
                return rep;
            tx = new Transaction();
            tx.start();
            rep = (Representable) PersistenceHelper.manager.lockAndRefresh(rep);
            while (enumeration.hasMoreElements()) {
                IxbElement ixbelement2 = (IxbElement) enumeration.nextElement();
                createRepresentation(rep, ixbelement2);
            }
            rep = (Representable) PersistenceHelper.manager.refresh(rep);
            tx.commit();
            tx = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tx != null)
                tx.rollback();
        }
        return rep;
    }

    private Representation createRepresentation(Representable obj, IxbElement ixbelement) throws WTException {
        Representation rep = null;
        try {
            File uDir = StandardIXBService.getSaveDirectoryOnServer();
            String inDir = uDir.getCanonicalPath();
            if (unzipRepresentationContent(uDir, ixbelement)) {
                String repName = getElementValue(ixbelement, "name");
                String repDesc = getElementValue(ixbelement, "description");
                boolean repDefault = Boolean.valueOf(ixbelement.getValue("default")).booleanValue();

                // String republishableValue = getElementValue(ixbelement, "republishable");
                boolean republishable = true;
                if (VisualizationHelperFactory.HELPER.loadRepresentation(inDir, getRefFromObject(obj), republishable,
                        repName, repDesc+"(可视化来自外来单位)", repDefault,
                        VisualizationHelperFactory.HELPER.isThumbnailEnabled(), false)) {
                    rep = VisualizationHelperFactory.HELPER.getRepresentation(obj, repName);
                }
                FileUtil.deleteDirectory(inDir, true);
            }
        } catch (Exception e) {
            if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
        }
        return rep;
    }

    private boolean unzipRepresentationContent(File dir, IxbElement fileXML) throws WTException, IOException {
        String c_id = getElementValue(fileXML, "repContent/id");
        if (c_id == null)
            return false;

        InputStream c_is = this.impHdl.getContentAsInputStream(c_id);

        File zipFile = StandardIXBService.getSaveFileOnServer();
        OutputStream oStream = new FileOutputStream(zipFile);

        byte[] b = new byte[1024];
        int count = -1;
        while ((count = c_is.read(b, 0, 1024)) > 0) {
            oStream.write(b, 0, count);
        }
        oStream.close();

        boolean status = FileUtil.unzipFile(zipFile, dir, true);

        zipFile.delete();
        return status;
    }

    public static CmExpImpPersistable newCmExpImpPersistable(Object obj, CmExporter expHdl) throws WTException {
        if ((obj instanceof WTDocument))
            return new CmExpImpWTDocument(expHdl);
        if ((obj instanceof WTPart))
            return new CmExpImpWTPart(expHdl);
        if ((obj instanceof EPMDocument)) {
            return new CmExpImpEPMDocument(expHdl);
        }
        expHdl.logger("Export:Unsupport Object=<" + obj.getClass().getName() + ">");
        return null;
    }

    public static CmExpImpObject newCmExpImpPersistable(CmImporter impHdl, String fname) throws WTException {
        String typename = fname.substring(fname.indexOf("TAG-") + 4, fname.lastIndexOf("."));
        if (typename.startsWith("WTDocument")){
            return new CmExpImpWTDocument(impHdl, fname);
        }
        if (typename.startsWith("EPMDocument")) {
            return new CmExpImpEPMDocument(impHdl, fname);
        }
        if (typename.startsWith("WTPart")){
            return new CmExpImpWTPart(impHdl, fname);
        }
        if (typename.startsWith("ProcessEnvelope")||typename.startsWith("ProdCmApproveOrder")) {
            return new CmExpImpProcessEnvelope(impHdl, fname);
        }
        if (typename.startsWith("WTChangeOrder")) {
            return new CmExpImpChangePackaged(impHdl, fname);
        }
        if (typename.startsWith("WTChangeRequest")) {
            return new CmExpImpChangeRequest(impHdl, fname);
        }
        if (typename.startsWith("EPMSepFamilyTable")) {
            return new CmExpImpEPMSepFamilyTable(impHdl, fname);
        }
        if (typename.startsWith("Preview")) {
            return new CmExpImpPreview(impHdl, fname);
        }
        if (typename.startsWith("ClassificationNode")) {
            return new CmExpImpClassificationNode(impHdl, fname);
        }
        if (typename.startsWith("GLCatalogItem")) {
            return new CmExpImpGLCatalogItem(impHdl, fname);
        }
        if (typename.startsWith("GLCatalog")) {
            return new CmExpImpGLCatalog(impHdl, fname);
        }

        if (typename.startsWith("GLSupply")) {
            return new CmExpImpGLSupply(impHdl, fname);
        }


        if (typename.startsWith(MQExpImpConstants.XML_MQDOCUMENT)){
            return new MQExpImpWTDocument(impHdl, fname);
        }
        if (typename.startsWith(MQExpImpConstants.XML_MQCADDOCUMENT)) {
            return new MQExpImpEPMDocument(impHdl, fname);
        }
        if (typename.startsWith(MQExpImpConstants.XML_MQPART)){
            return new MQExpImpWTPart(impHdl, fname);
        }
        if (typename.startsWith(MQExpImpConstants.XML_MQECA)) {
            return new MQExpImpProcessEnvelope(impHdl, fname);
        }
        if (typename.startsWith(MQExpImpConstants.XML_MQECO)) {
            return new MQExpImpChangePackaged(impHdl, fname);
        }
        if (typename.startsWith(MQExpImpConstants.XML_MQECR)) {
            return new MQExpImpChangeRequest(impHdl, fname);
        }
        if (typename.startsWith(MQExpImpConstants.XML_MQTNO)) {
            return new MQExpImpTNO(impHdl, fname);
        }
        impHdl.logger("Import WARNING:Unsupport fname=<" + fname + ">");
        return null;
    }

    public static CmExpImpObject newCmExpImpZykPersistable(CmImporter impHdl, String fname) throws WTException {
        String typename = fname.substring(fname.indexOf("TAG-") + 4, fname.lastIndexOf("."));
        if (typename.startsWith("WTDocument")){
            return new CmExpImpWTDocument(impHdl, fname);
        }
        if (typename.startsWith("EPMDocument")) {
            return new CmExpImpZYKEPMDocument(impHdl, fname);
        }
        if (typename.startsWith("WTPart")){
            return new CmExpImpZYKWTPart(impHdl, fname);
        }
        if (typename.startsWith("ProcessEnvelope")||typename.startsWith("ProdCmApproveOrder")) {
            return new CmExpImpProcessEnvelope(impHdl, fname);
        }
        if (typename.startsWith("WTChangeOrder")) {
            return new CmExpImpChangePackaged(impHdl, fname);
        }
        if (typename.startsWith("WTChangeRequest")) {
            return new CmExpImpChangeRequest(impHdl, fname);
        }
        if (typename.startsWith("EPMSepFamilyTable")) {
            return new CmExpImpEPMSepFamilyTable(impHdl, fname);
        }
        if (typename.startsWith("Preview")) {
            return new CmExpImpPreview(impHdl, fname);
        }
        if (typename.startsWith("ClassificationNode")) {
            return new CmExpImpClassificationNode(impHdl, fname);
        }
        if (typename.startsWith("GLCatalogItem")) {
            return new CmExpImpGLCatalogItem(impHdl, fname);
        }
        if (typename.startsWith("GLCatalog")) {
            return new CmExpImpGLCatalog(impHdl, fname);
        }

        if (typename.startsWith("GLSupply")) {
            return new CmExpImpGLSupply(impHdl, fname);
        }


        if (typename.startsWith(MQExpImpConstants.XML_MQDOCUMENT)){
            return new MQExpImpWTDocument(impHdl, fname);
        }
        if (typename.startsWith(MQExpImpConstants.XML_MQCADDOCUMENT)) {
            return new MQExpImpEPMDocument(impHdl, fname);
        }
        if (typename.startsWith(MQExpImpConstants.XML_MQPART)){
            return new MQExpImpWTPart(impHdl, fname);
        }
        if (typename.startsWith(MQExpImpConstants.XML_MQECA)) {
            return new MQExpImpProcessEnvelope(impHdl, fname);
        }
        if (typename.startsWith(MQExpImpConstants.XML_MQECO)) {
            return new MQExpImpChangePackaged(impHdl, fname);
        }
        if (typename.startsWith(MQExpImpConstants.XML_MQECR)) {
            return new MQExpImpChangeRequest(impHdl, fname);
        }
        if (typename.startsWith(MQExpImpConstants.XML_MQTNO)) {
            return new MQExpImpTNO(impHdl, fname);
        }
        impHdl.logger("Import WARNING:Unsupport fname=<" + fname + ">");
        return null;
    }

    protected HashMap buildOrignalInfo() throws WTException {
        HashMap hmap = new HashMap();
        String creator = getElementValue(this.root, "creator");
        if (creator == null)
            creator = "UNKNOW";
        hmap.put("creator", creator);
        String modifier = getElementValue(this.root, "modifier");
        if (modifier == null)
            modifier = "UNKNOW";
        hmap.put("modifier", modifier);
        String remoteurl = this.impHdl.getRemoteURL();
        hmap.put("remoteurl", remoteurl);
        return hmap;
    }

    public abstract String getRootTag();


    protected void recordImplementAdivse(IxbElement root) throws WTException {
        // TODO Auto-generated method stub
        IxbElement implement = root.getElement("ImplementAdvise");
        if (implement!=null) {
            String number = getElementValue(implement, "number");
            String advise = getElementValue(implement, "implement");
            this.impHdl.putNumberImplementAdvise(number, advise);
        }


    }


    public void noticeException(Exception exception, String number) {
    	String msg = "编号为:"+number+"的对象导入失败！报错信息："+exception.getLocalizedMessage();
    	WTPrincipal currentUser;
		try {
			currentUser = SessionHelper.manager.getPrincipal();
			WTOrganization  userOrg = currentUser.getOrganization();
			WTContainerRef  userConRef = null;
			if(userOrg==null){
				userConRef = WTContainerHelper.service.getExchangeRef();
			}else{
				userConRef = userOrg.getContainerReference();
			}
			WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
					.getProcessDefinition("149数据包导入失败提醒流程");
			WfProcess wfprocess = WfEngineHelper.service.createProcess(
					wfprocessdefinition, null,userConRef);
			ProcessData processdata = wfprocess.getContext();
			processdata.setValue("msg",msg);
			WfEngineHelper.service.startProcess(wfprocess,processdata, 1);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

    protected void updateVersionf2F(Persistable p) {
        DBConn conn = null;
        try {
            conn = new DBConn();
            long ida2a2 = p.getPersistInfo().getObjectIdentifier().getId();
            String tableName = "";
            if(p instanceof  WTPart){
                tableName = "wtpart";
            }else if(p instanceof  WTDocument){
                tableName = "WTDocument";
            }else if(p instanceof  EPMDocument){
                tableName = "EPMDocument";
            }
            String sql = "update "+tableName+" set versionida2versioninfo='F' where ida2a2 = "+ida2a2;
            conn.executeUpdate(sql);
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

    protected void updateLifeCycle2F(Persistable p) {
        DBConn conn = null;
        try {
            conn = new DBConn();
            long ida2a2 = p.getPersistInfo().getObjectIdentifier().getId();
            String tableName = "";
            if(p instanceof  WTPart){
                tableName = "wtpart";
            }else if(p instanceof  WTDocument){
                tableName = "WTDocument";
            }else if(p instanceof  EPMDocument){
                tableName = "EPMDocument";
            }
            String sql = "update "+tableName+" set statestate='OBSOLESCENCE' where ida2a2 = "+ida2a2;
            conn.executeUpdate(sql);
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
    public static void main(String[] args) {
        // WTProperties wtProperties = WTProperties.getLocalProperties();
        String objectContainerPath = "/wt.inf.container.OrgContainer=805/wt.pdmlink.PDMLinkProduct=863-707";
        int index = objectContainerPath.lastIndexOf("=");
        String productName = objectContainerPath.substring(index + 1, objectContainerPath.length() - 1);
        System.out.println(productName);
    }
}