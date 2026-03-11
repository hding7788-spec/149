package com.ptc.extend.ixb.center;

import java.beans.PropertyVetoException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.*;

import com.ptc.core.meta.common.DefinitionIdentifier;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.descriptor.common.DefinitionDescriptor;
import com.ptc.core.meta.descriptor.common.DefinitionDescriptorFactory;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.windchill.wp.WorkPackage;
import org.json.JSONObject;

import wt.access.NotAuthorizedException;
import wt.admin.AdminDomainRef;
import wt.admin.AdministrativeDomain;
import wt.admin.AdministrativeDomainHelper;
import wt.admin.DomainAdministered;
import wt.admin.DomainAdministeredHelper;
import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.ExternalStoredData;
import wt.content.FormatContentHolder;
import wt.content.Streamed;
import wt.content.URLData;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.facade.ixb.IxbElement;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
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
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleServerHelper;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.LifeCycleTemplateReference;
import wt.lifecycle.State;
import wt.org.WTGroup;
import wt.org.WTOrganization;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.Transaction;
import wt.project.Role;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.series.IntegerSeries;
import wt.series.MultilevelSeries;
import wt.services.applicationcontext.implementation.DefaultServiceProvider;
import wt.session.SessionHelper;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.team.TeamManaged;
import wt.ufid.Federatable;
import wt.util.FileUtil;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.Iterated;
import wt.vc.IterationIdentifier;
import wt.vc.VersionControlHelper;
import wt.vc.VersionControlServerHelper;
import wt.vc.VersionIdentifier;
import wt.vc.Versioned;
import wt.viewmarkup.DerivedImage;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.wvs.VisualizationHelperFactory;

import com.bjsasc.avidm.mq.message.Based;
import com.ptc.extend.ixb.CmExpImpChangePackaged;
import com.ptc.extend.ixb.CmExpImpChangeRequest;
import com.ptc.extend.ixb.CmExpImpClassificationNode;
import com.ptc.extend.ixb.CmExpImpEPMDocument;
import com.ptc.extend.ixb.CmExpImpEPMSepFamilyTable;
import com.ptc.extend.ixb.CmExpImpGLCatalog;
import com.ptc.extend.ixb.CmExpImpGLCatalogItem;
import com.ptc.extend.ixb.CmExpImpGLSupply;
import com.ptc.extend.ixb.CmExpImpObject;
import com.ptc.extend.ixb.CmExpImpPreview;
import com.ptc.extend.ixb.CmExpImpProcessEnvelope;
import com.ptc.extend.ixb.CmExpImpWTDocument;
import com.ptc.extend.ixb.CmExpImpWTPart;
import com.ptc.extend.ixb.CmExporter;
import com.ptc.extend.ixb.CmImporter;
import com.ptc.extend.util.Debug;
import com.ptc.extend.util.ObjectProperty;
import com.ptc.wvs.common.ui.Representer;
import com.ptc.wvs.common.ui.VisualizationHelper;

import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.ProcessEnvelope;
import ext.sast.center.synch.MQConstants;
import ext.sast.center.synch.MQExpImpUtil;

public abstract class MQExpImpPersistable extends MQExpImpObject {
    String iname;
    String number;
    String version;
    String iteration;



	protected MQExpImpPersistable() {
    }

    protected MQExpImpPersistable(CmExporter expHdl)
            throws WTException {
        this.expHdl = expHdl;
        this.ixbdocument = IxbHelper.newIxbDocument();
        this.root = this.ixbdocument.createRootElement(getRootTag());
    }
    public static CmExpImpObject newMQExpImpPersistable(CmImporter impHdl, String fname) throws WTException {
        String typename = fname.substring(fname.indexOf("TAG-") + 4, fname.lastIndexOf("."));
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

        impHdl.logger("Import WARNING:Unsupport fname=<" + fname + ">");
        return null;
    }

    protected void exportModelType(Object obj, IxbElement ixbelement) throws WTException {
        try {
            if (obj instanceof WTPart) {
                ixbelement.addValue("modelType", "PART");
            } else if (obj instanceof WTDocument) {
                String typeName = TypeIdentifierUtility.getTypeIdentifier(obj).getTypename();
                if(typeName.endsWith("wt.doc.WTDocument|com.ptc.ReferenceDocument|casc.sast.805.PLD")){
                    ixbelement.addValue("modelType", "TNO");
                }else {
                    ixbelement.addValue("modelType", "Document");
                }

            } else if (obj instanceof EPMDocument) {
                ixbelement.addValue("modelType", "CADDocument");
            } else if (obj instanceof WTChangeOrder2) {
                ixbelement.addValue("modelType", "ECO");
            } else if (obj instanceof WorkPackage) {
                ixbelement.addValue("modelType", "ApproveOrder");
            }
        } catch (WTException exception) {
            logger("Exception in exportOriginType, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
            processException(exception);
        }
    }

    protected void exportOriginType(Object obj, IxbElement ixbelement) throws WTException {
        try {
            TypeIdentifier type = TypeIdentifierUtility.getTypeIdentifier(obj);
            DefinitionIdentifier[] types = { type };
            DefinitionDescriptorFactory ddf = (DefinitionDescriptorFactory) DefaultServiceProvider
                    .getService(DefinitionDescriptorFactory.class, "default", null);
            DefinitionDescriptor[] defs = ddf.get(types, null, Locale.CHINA);
            if (defs != null && defs.length > 0) {
                DefinitionDescriptor def = defs[0];
                ixbelement.addValue("originClassId", type.getTypename());
                ixbelement.addValue("originName", def.getDisplay());
            }
        } catch (Exception exception) {
            logger("Exception in exportOriginType, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
            processException(exception);
        }
    }
    protected MQExpImpPersistable(CmImporter impHdl, String fname) throws WTException {
        super(impHdl, fname);

        this.iname = getElementValue("name");
        this.number = getNoTrimElementValue("number");
        if (impHdl.isLoopTest()) {
            this.number = (impHdl.getLoopTestPrefix() + this.number);
        }
        String version = getElementValue(this.root, "versionInfo");
        version = ext.sast.center.synch.MQExpImpUtil.attrConvertValue("versionInfo", version, "IMP");
        this.version = version;
        this.iteration = getElementValue(this.root, "iterationInfo");
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
            //TODO 需转为中心域标准型号
            String productIID = wtcontainerref.getObjectId().getId()+"";
            String productID = wtcontainerref.getName();
           JSONObject json = ext.sast.center.util.ProductConvertUtil.getSastProdcutInfo(productID);
           try {
            	productIID = json.getString(Based.PRODUCT_IID);
            	productID = json.getString(Based.PRODUCT_ID);
			} catch (Exception e) {
				e.printStackTrace();
			}
            if(obj instanceof PDMLinkProduct){
                ixbelement.addValue("isLibrary", "0");
            }else{
                ixbelement.addValue("isLibrary", "1");
            }
            ixbelement.addValue("productiid", emptyIfNull(productIID));
            ixbelement.addValue("productId", emptyIfNull(productID));
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
            String s1 = getFolderPath(foldered);
            ixbelement.addValue("folder", emptyIfNull(s1));
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
            String s2 = versioned.getVersionInfo().getIdentifier().getValue();
            String s3 = versioned.getIterationInfo().getIdentifier().getValue();
            s2 = ext.sast.center.synch.MQExpImpUtil.attrConvertValue("versionInfo", s2, MQConstants.EXPIMP_FLAG_EXP);
            ixbelement.addValue("versionInfo", emptyIfNull(s2));
            ixbelement.addValue("iterationInfo", emptyIfNull(s3));
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
            String s1 = lifecyclemanaged.getLifeCycleState().toString();
            if(MQExpImpConstants.STATE_APPROVED.equals(s1)){
                ixbelement.addValue("state", "受控中");
            }else{
                ixbelement.addValue("state", "审批中");
            }
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

        IxbElement ixbelement1 = ixbelement.addElement("File");
        ixbelement1.addValue("exportPath", s);
        ixbelement1.addValue("fileExtendName", emptyIfNull(applicationdata.getCategory()));
        ixbelement1.addValue("fileName", emptyIfNull(applicationdata.getFileName()));
        if(MQExpImpConstants.PRIMARY.equals(applicationdata.getRole().toString())){
            ixbelement1.addValue("fileObjType", "main");
        }else{
            ixbelement1.addValue("fileObjType", "normal");
        }
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

            String lifecycleState = "";
            String lifecycleTemplate="149_Doc_LC";

            if(obj instanceof WTDocument){
            	lifecycleTemplate ="149_Doc_LC";
            }else if(obj instanceof WTPart){
            	lifecycleTemplate ="149_Part_LC";
            }else if(obj instanceof EPMDocument){
            	lifecycleTemplate ="149_EPM_LC";
            }else if(obj instanceof ProcessEnvelope){
            	lifecycleTemplate ="149_Envelope_LC";
            }else if(obj instanceof ChangePackaged){
            	lifecycleTemplate ="149_ChangePackaged_LC";
            }

            lifecycleState = getElementValue(ixbelement, "state");
            if ("受控中".equals(lifecycleState)||"分发中".equals(lifecycleState)||"调度中".equals(lifecycleState)||"已分发".equals(lifecycleState)) {
            	lifecycleState ="APPROVED";
            }else{
            	lifecycleState ="PROCESSCOUNTERSIGN";
            }

            WTContainerRef wtcontainerref = getWTContainerRef(this.root);
            if(wtcontainerref==null){
                wtcontainerref = this.impHdl.getWTContainerRef();
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
            String s1 = getElementValue(ixbelement, "versionInfo");
            s1 = MQExpImpUtil.attrConvertValue("versionInfo", s1, "IMP");
            String s2 = "1";
            String s4 = getElementValue(ixbelement, "iterationInfo");
            String s = MQExpImpConstants.SERIES;
            //s1 = getPropertiesValue(s1);
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
            String productName = getElementValue(ixbelement, "productId");

            if (wtcontainerref == null) {
            	//报错或者抛异常
            	throw new WTException("找不到对应的"+productName+"产品库，请检查是否存在该型号或型号映射！");
			}

            String s = getFolderPathWithReplacedCabinet(ixbelement, wtcontainerref);
            String s2 = foldered.getFolderPath();
            if (s2 == null) {
                if (s == null) {
                    s = WTContainerHelper.DEFAULT_CABINET_NAME;
                }
                if("外来文件产品库".equals(wtcontainerref.getName())){
                    if(productName != null){
                        if(productName.contains("/")){
                            productName = productName.replace("/","_");
                        }
                        s += "/" + productName;
                    }

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
        String s = getElementValue(this.root, "originDomainName");
        String productName = getElementValue(ixbelement, "productId");
        if(MQExpImpConstants.WAILAI_PRODUCT.equals(wtcontainerref.getName())){
            s= "/Default/"+s+"/"+productName;
        }else{
            s= "/Default/外来文件/"+s;

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
            Enumeration enumeration = ixbelement.getElements("File");
            if ((enumeration == null) || (!enumeration.hasMoreElements()))
                return contentholder;
            tx = new Transaction();
            tx.start();
            contentholder = (ContentHolder) PersistenceHelper.manager.lockAndRefresh(contentholder);
            contentholder = initImportContentHolder(contentholder);
            while (enumeration.hasMoreElements()) {
                IxbElement ixbelement2 = (IxbElement) enumeration.nextElement();
                importAppDataContent(contentholder, ixbelement2);

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
        String s = getElementValue(ixbelement, "fileExtendName");
        String fileObjType = getElementValue(ixbelement, "fileObjType");
        String role = MQExpImpConstants.PRIMARY;
        if("main".equals(fileObjType)){
        	role =  MQExpImpConstants.PRIMARY;
        }else{
        	role = MQExpImpConstants.SECONDARY;
        }
        ContentRoleType contentroletype = ContentRoleType.toContentRoleType(role);
        try {
            if (s != null)
                contentitem.setCategory(s);
            if (contentroletype != null)
                contentitem.setRole(contentroletype);
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
            if (s != null)
                applicationdata.setFileName(s);
            String s4 = getElementValue(ixbelement, "exportPath");
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

            	File indirF = new File(inDir);
            	File[] files = indirF.listFiles();
            	for(File f:files){
            		if(f.getName().endsWith(".pvz")||f.getName().endsWith(".PVZ")){
            			FileUtil.unzipFile(f, indirF, true);
            		}
            	}

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

    public static MQExpImpPersistable newCmExpImpPersistable(Object obj, CmExporter expHdl) throws WTException {
        if ((obj instanceof WTDocument))
            return new MQExpImpWTDocument(expHdl);
        if ((obj instanceof WTPart))
            return new MQExpImpWTPart(expHdl);
        if ((obj instanceof EPMDocument)) {
            return new MQExpImpEPMDocument(expHdl);
        }
        expHdl.logger("Export:Unsupport Object=<" + obj.getClass().getName() + ">");
        return null;
    }

    public static MQExpImpPersistable newCmExpImpPersistable(CmImporter impHdl, String fname) throws WTException {
        String typename = fname.substring(fname.indexOf("TAG-") + 4, fname.lastIndexOf("."));
        if (typename.startsWith("WTDocument")){
        	return new MQExpImpWTDocument(impHdl, fname);
        }
        if (typename.startsWith("EPMDocument")) {
        	return new MQExpImpEPMDocument(impHdl, fname);
        }
        if (typename.startsWith("WTPart")){
        	return new MQExpImpWTPart(impHdl, fname);
        }
        if (typename.startsWith("ProcessEnvelope")||typename.startsWith("ProdCmApproveOrder")) {
           // return new CmExpImpProcessEnvelope(impHdl, fname);
        }
        if (typename.startsWith("WTChangeOrder")) {
            //return new CmExpImpChangePackaged(impHdl, fname);
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

    /** 当导入的pbo没有相应的容器时，在处理异常时方便取容器名称
     * @return
     * @throws WTException
     */
    public String getContainerName(){
        String str = null;
        try {
            String objectContainerPath = getElementValue(this.root, "objectContainerPath");
            if (objectContainerPath.indexOf("PDMLinkProduct") > 0) {
                int index = objectContainerPath.lastIndexOf("=");
                String productName = objectContainerPath.substring(index + 1, objectContainerPath.length());
                str = "系统不存在名称为："+productName+" 的产品库";

            } else if (objectContainerPath.indexOf("WTLibrary") > 0) {
                int index = objectContainerPath.lastIndexOf("=");
                String libraryName = objectContainerPath.substring(index + 1, objectContainerPath.length());
                str = "系统不存在名称为："+libraryName+" 的存储库";
                }
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return str;
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
    public static void main(String[] args) {
        // WTProperties wtProperties = WTProperties.getLocalProperties();
        String objectContainerPath = "/wt.inf.container.OrgContainer=805/wt.pdmlink.PDMLinkProduct=863-707";
        int index = objectContainerPath.lastIndexOf("=");
        String productName = objectContainerPath.substring(index + 1, objectContainerPath.length() - 1);
        System.out.println(productName);
    }
}