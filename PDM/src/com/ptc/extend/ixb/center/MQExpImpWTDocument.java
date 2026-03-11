package com.ptc.extend.ixb.center;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;

import com.ptc.extend.ixb.CmExpImpEPMReferenceLink;
import com.ptc.extend.ixb.CmExpImpSearchHelper;
import com.ptc.extend.ixb.CmExpImpTechNoticeAfterLink;
import com.ptc.extend.ixb.CmExpImpTechNoticeBeforeLink;
import com.ptc.extend.ixb.CmExpImpWTDocumentDependencyLink;
import com.ptc.extend.ixb.CmExpImpWTDocumentUsageLink;
import com.ptc.extend.ixb.CmExpImpWTPartDescribeLink;
import com.ptc.extend.ixb.CmExpImpWTPartReferenceLink;
import com.ptc.extend.ixb.CmExporter;
import com.ptc.extend.ixb.CmImporter;
import com.ptc.extend.ixb.ErrorImportObject;
import com.ptc.extend.util.ObjectProperty;

import ext.casc.constants.Constants;
import ext.casc.util.IBAHelper;
import ext.sast.center.synch.MQConstants;
import ext.sast.center.synch.MQExpImpUtil;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.doc.DocumentType;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentMasterIdentity;
import wt.facade.ixb.IxbElement;
import wt.fc.IdentityHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTHashSet;
import wt.folder.FolderHelper;
import wt.folder.Foldered;
import wt.iba.value.service.MultiObjIBAValueDBService;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainerRef;
import wt.method.MethodContext;
import wt.pom.Transaction;
import wt.series.MultilevelSeries;
import wt.series.Series;
import wt.session.SessionServerHelper;
import wt.type.Typed;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.IterationIdentifier;
import wt.vc.Mastered;
import wt.vc.StandardVersionControlService;
import wt.vc.VersionControlHelper;
import wt.vc.VersionControlServerHelper;
import wt.vc.VersionIdentifier;

public class MQExpImpWTDocument extends MQExpImpPersistable {

    public MQExpImpWTDocument(CmExporter expHdl) throws WTException {
        super(expHdl);
    }

    public MQExpImpWTDocument(CmImporter impHdl, String fname)
            throws WTException {
        super(impHdl, fname);
    }

    public String getRootTag() {
        return MQExpImpConstants.XML_MQDOCUMENT;
    }

    public void exportObject(Object obj) throws WTException {
        if (!(obj instanceof WTDocument))
            throw new WTException("Object not WTDocument.");
        WTDocument doc = (WTDocument) obj;
        logger.log("==>Export WTDocument:" + ObjectProperty.getObjectDisplay(doc));
        exportAttribute(doc);
        this.expHdl.addExportedObject(doc);
        logger.log("==>Export Linkage of WTDocument:" + ObjectProperty.getObjectDisplay(doc));
        try {
            ArrayList list = CmExpImpSearchHelper.searchAllWTDocumentUsageLink(doc);
            if (list.size() > 0)
                new CmExpImpWTDocumentUsageLink(doc, this.expHdl).exportObject(list);
            list = CmExpImpSearchHelper.searchAllWTDocumentDependencyLink(doc);
            if (list.size() > 0)
                new CmExpImpWTDocumentDependencyLink(doc, this.expHdl).exportObject(list);
            list = CmExpImpSearchHelper.searchAllWTPartDescribeLink(doc);
            if (list.size() > 0)
                new CmExpImpWTPartDescribeLink(doc, this.expHdl).exportObject(list);
            list = CmExpImpSearchHelper.searchAllWTPartReferenceLink(doc);
            if (list.size() > 0)
                new CmExpImpWTPartReferenceLink(doc, this.expHdl).exportObject(list);
            list = CmExpImpSearchHelper.searchAllEPMReferenceLink((WTDocumentMaster) doc.getMaster());
            if (list.size() > 0)
                new CmExpImpEPMReferenceLink(doc, this.expHdl).exportObject(list);
            list = CmExpImpSearchHelper.searchAllTechNoticeBeforeLink(doc);
            if (list.size() > 0)
                new CmExpImpTechNoticeBeforeLink(doc, this.expHdl).exportObject(list);
            list = CmExpImpSearchHelper.searchAllTechNoticeAfterLink(doc);
            if (list.size() > 0)
                new CmExpImpTechNoticeAfterLink(doc, this.expHdl).exportObject(list);
        } catch (Exception e) {
            logger.log(e);
            logger.log("==>Exception export Link for ob=<" + ObjectProperty.getObjectDisplay(doc) + ">");
        }
    }

    private void exportAttribute(WTDocument doc) throws WTException {
        //exportUfidAttribute(doc, this.root);
        exportLocalIdAttribute(doc, this.root);
        exportContainerPathAttribute(doc, this.root);
        exportWTDocumentMasterAttribute(doc, this.root);
        exportWTDocumentAttribute(doc, this.root);
        exportDomainFolderAttribute(doc, this.root);
        exportVersionAttribute(doc, this.root);
        exportLifecycleAttribute(doc, this.root);
        //exportTeamAttribute(doc, this.root);
        exportTypeDefinitionAttribute(doc, this.root);
        exportContentItemAttribute(doc, this.root);
        exportIBAAttribute(doc, this.root);
        exportRepresentationAttribute(doc, this.root);
        reallyStore();
    }

    protected void exportDomainFolderAttribute(Object obj, IxbElement ixbelement) throws WTException {
        try {
            if (!(obj instanceof Foldered))
                return;
            Foldered foldered = (Foldered) obj;
            String s1 = getFolderPath(foldered);
            String type = TypedUtilityServiceHelper.service.getExternalTypeIdentifier((Typed) obj);
            if(type.contains(Constants.PROCESS_NOTICE_DOCUMENT)){
            	s1 = Constants.FOLDER_PATH1;
            }
            ixbelement.addValue("folder", emptyIfNull(s1));
        } catch (Exception exception) {
            logger.log("Exception in exportDomainFolderAttribute, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
            processException(exception);
        }
    }




    private void exportWTDocumentMasterAttribute(Object obj, IxbElement ixbelement) throws WTException {
        try {
            WTDocument wtdocument = (WTDocument) obj;
            ixbelement.addValue("number", emptyIfNull(wtdocument.getNumber()));
            // WTDocumentMaster wtdocumentmaster = (WTDocumentMaster)wtdocument.getMaster();
            ixbelement.addValue("name", emptyIfNull(wtdocument.getName()));
            //ixbelement.addValue("modelType", "Document");
        } catch (Exception exception) {
            logger.log("Exception in ExpImpForWTDocumentMasterAttr, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
            processException(exception);
        }
    }

    private void exportWTDocumentAttribute(Object obj, IxbElement ixbelement) throws WTException {
        try {
            WTDocument wtdocument = (WTDocument) obj;
            ixbelement.addValue("docTitle", emptyIfNull(wtdocument.getTitle()));
            ixbelement.addValue("description", emptyIfNull(wtdocument.getDescription()));
            ixbelement.addValue("department", emptyIfNull(wtdocument.getDepartment().toString()));
            ixbelement.addValue("creator", emptyIfNull(wtdocument.getCreatorName()));
            ixbelement.addValue("createStamp", String.valueOf(wtdocument.getCreateTimestamp().getTime()));
            ixbelement.addValue("modifier", emptyIfNull(wtdocument.getModifierName()));
            ixbelement.addValue("modifyStamp", String.valueOf(wtdocument.getModifyTimestamp().getTime()));
            ixbelement.addValue("originDomainName", MQExpImpConstants.VALUE_DOMAINNAME);

            String securityLevel = IBAHelper.getIBAStringValue(wtdocument, "SECRET");
            securityLevel  = ext.sast.center.synch.MQExpImpUtil.attrConvertValue("securityLevel", securityLevel, MQConstants.EXPIMP_FLAG_EXP);
            ixbelement.addValue("securityLevel", securityLevel);
            ixbelement.addValue("owner", wtdocument.getOwnership().getOwner().getName());
        } catch (Exception exception) {
            logger.log("Exception in ExpImpForWTDocumentAttr, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
            processException(exception);
        }
    }

    public Object importObject() throws WTException {
        Object obj = importAttribute();
        if(obj!=null && obj instanceof WTDocument){
        	WTDocument doc = (WTDocument)obj;
        	String  name=  getElementValue("name");
        	if(name!=null&&!"".equals(name)&&!"null".equals(name)&&!doc.getName().equals(name)){
        		try {
					changeName(doc,name);
				} catch (WTPropertyVetoException e) {
					e.printStackTrace();
				}
        	}
        }
        if (obj != null){
        	if(obj instanceof ErrorImportObject){
        	}else{
                this.impHdl.pubImportedObject(obj, getRemoteId());
        	}
        }
        return obj;
    }
    private void changeName(WTDocument doc, String name) throws WTException, WTPropertyVetoException {
    	WTDocumentMaster master = (WTDocumentMaster) doc.getMaster();
    	WTDocumentMasterIdentity idy = (WTDocumentMasterIdentity) master.getIdentificationObject();
		idy.setName(name);
		master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, idy);
	}

    public WTArrayList importObjects() throws WTException {
        WTArrayList list = new WTArrayList();
        list.add((Persistable) importObject());
        return list;
    }

    private Object importAttribute() throws WTException {
        try {
            logger.log("\n==>Start To Import Attributes For WTDocument: " + this.number + "." + this.iteration);
            if (!isTypeDefinitionImported()) {
                logger.log("==>WARNING:Import WTDocument number=<" + this.number + "> version=" + this.version + "."
                        + this.iteration + " TypeDefinition not imported, SKIP!");
                return null;
            }
            String versionStr = this.version;
            WTDocument doc = (WTDocument) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(WTDocument.class,
                    this.number, versionStr, this.iteration);
            if (doc != null) {
                logger.log("==>Import WTDocument number=<" + this.number + "> version=" + this.version + "."
                        + this.iteration + " already imported, Go On Import Sign File!");
                this.impHdl.putInExistedHashtable(getRemoteId(), doc);
                recordImplementAdivse(this.root);
                doc = (WTDocument) importContentItemAttribute(doc, this.root);
                doc = (WTDocument) importLifecycleAttribute(doc, this.root);
                return doc;
            }
            WTDocument newdoc = null;
            doc = (WTDocument) CmExpImpSearchHelper.searchLatestIteratedByNumberVersion(WTDocument.class, this.number,
                    versionStr);
            if (doc != null) {
                logger.log("==>Create new Iteration: WTDocument number=<" + this.number + "> version=" + this.version
                        + "." + this.iteration);
                newdoc = createNewIteration(doc);
            } else {
                doc = (WTDocument) CmExpImpSearchHelper.searchLatestIteratedByNumber(WTDocument.class, this.number);
                if (doc != null) {
                    logger.log("==>Create new Version: WTDocument number=<" + this.number + "> version=" + this.version
                            + "." + this.iteration);
                    newdoc = createNewVersion(doc);
                } else {
                    logger.log("==>Create new Object: WTDocument number=<" + this.number + "> version=" + this.version
                            + "." + this.iteration);
                    newdoc = createNewObject();
                }
            }
            if (newdoc != null) {
                this.impHdl.putInNewCreatedHashtable(getRemoteId(), newdoc);
                HashMap hmap = buildOrignalInfo();
                this.impHdl.doOperationAfterStore(newdoc, hmap);
                logger.log("==>Import WTDocument number=<" + this.number + "> version=" + this.version + "."
                        + this.iteration + " OK!");
            }
            return newdoc;
        } catch (Exception exception) {
            logger.log("Exception in importWTDocument, fname=<" + getPfilename() + ">");
            logger.log(exception.getLocalizedMessage());
            exception.printStackTrace();
            ErrorImportObject eo = new ErrorImportObject();
            eo.setNumber(this.number);
            eo.setXmlName(getPfilename());
            eo.setType("WTDocument");
            if(exception.getLocalizedMessage()!=null&&exception.getLocalizedMessage().contains("未找到上下文")){
                String productId = getElementValue(this.root, "productId");
                eo.setMessage(this.number+"149PDM系统找不到相应的型号:"+productId+",请联系系统管理员创建并映射对应型号");
            } else{
                eo.setMessage(exception.getLocalizedMessage());
            }
            exception.printStackTrace();
            return eo;
        }
    }

    private ContentHolder importPDFContentItem(WTDocument obj, IxbElement ixbelement) throws WTException {
    	 ContentHolder contentholder = (ContentHolder) obj;
         Transaction tx = null;
         try {
             Enumeration enumeration = ixbelement.getElements("contentItem");
             if ((enumeration == null) || (!enumeration.hasMoreElements()))
                 return contentholder;
             tx = new Transaction();
             tx.start();
             contentholder = (ContentHolder) PersistenceHelper.manager.lockAndRefresh(contentholder);
             boolean enforce = SessionServerHelper.manager
                     .setAccessEnforced(false);
             ContentHolder holder = ContentHelper.service.getContents(contentholder);
             while (enumeration.hasMoreElements()) {
                 IxbElement ixbelement2 = (IxbElement) enumeration.nextElement();
                 String s1 = getElementValue(ixbelement2, "contentType");
                 String fileName = getElementValue(ixbelement2, "fileName");
                 if (s1.equals("ApplicationData")&&fileName.startsWith("Print_"))
                     importPDFAppDataContent(contentholder, ixbelement2);
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

	 private void importPDFAppDataContent(ContentHolder contentholder, IxbElement ixbelement) throws WTException {
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

	private WTDocument importWTDocumentMasterAttribute(Object obj, IxbElement ixbelement) throws WTException {
        WTDocument doc = (WTDocument) obj;
        try {
            String s2 = "$$Document";
            doc.setNumber(this.number);
            doc.setName(this.iname);
            if (s2 != null) {
                DocumentType documenttype = DocumentType.toDocumentType(s2);
                doc.setDocType(documenttype);
            }
        } catch (Exception e) {
            logger.log("Exception in importWTDocumentMasterAttribute, fname=<" + getPfilename() + ">");
            processException(e);
        }
        return doc;
    }

    private WTDocument importWTDocumentAttribute(Object obj, IxbElement ixbelement) throws WTException {
        WTDocument doc = (WTDocument) obj;
        try {
            String s = getElementValue(ixbelement, "name");
            if (s != null)
                doc.setTitle(s);
            String s1 = getElementValue(ixbelement, "originName");//exp:技术说明书
            if (s1 != null)
                doc.setDescription(s1);

            return doc;
        } catch (Exception e) {
            logger.log("Exception in importWTDocumentAttribute, fname=<" + getPfilename() + ">");
            processException(e);
        }
        return doc;
    }

	@SuppressWarnings("unchecked")
	private WTDocument createNewObject() throws Exception {
		WTDocument doc = null;
		Transaction tx = new Transaction();
		MethodContext methodcontext = MethodContext.getContext();
		try {
			long nowtime = Calendar.getInstance().getTimeInMillis();

			String createStampStr = getElementValue(this.root, "createStamp");
			Timestamp createStamp;
			if (createStampStr != null)
				createStamp = new Timestamp(Long.parseLong(createStampStr));
			else {
				createStamp = new Timestamp(nowtime);
			}
			String modifyStampStr = getElementValue(this.root, "modifyStamp");
			Timestamp modifyStamp;
			if (modifyStampStr != null) {
				modifyStamp = new Timestamp(Long.parseLong(modifyStampStr));
			}else {
				modifyStamp = new Timestamp(nowtime);
			}

			WTContainerRef wtcontainerref = getWTContainerRef(this.root);

			tx.start();
			doc = WTDocument.newWTDocument();
			doc = importWTDocumentMasterAttribute(doc, this.root);
			doc = (WTDocument) importLifecycleAttribute(doc, this.root);
			doc = (WTDocument) importVersionAttribute(doc, this.root);

			doc = importWTDocumentAttribute(doc, this.root);
			doc = (WTDocument) importDomainFolderAttribute(doc, this.root);

			doc = (WTDocument) importTypeDefinitionAttribute(doc, this.root, this.root);
			doc.setContainerReference(wtcontainerref);
			((WTContained) doc.getMaster()).setContainerReference(wtcontainerref);
			Mastered mastered = doc.getMaster();
			PersistenceHelper.manager.save(mastered);
			methodcontext.put("ixb_store_object_context/key", doc);
			WTArrayList wtarraylist = new WTArrayList(1);
			wtarraylist.add(doc);
			Transaction.getGlobalMap().put(StandardVersionControlService.SVCS_SIGNAL_PERSISTENCE_OF_IGNORE_KEY, true);
			Transaction.getGlobalMap().put(MultiObjIBAValueDBService.DO_NOT_COPY_FORWARD_IBAS_KEY, wtarraylist);
			WTHashSet wthashset = new WTHashSet();
			wthashset.add(doc);
			Transaction.getGlobalMap().put("PREVENT_OWNERSHIP_CHECK", wthashset);
			doc = (WTDocument) PersistenceServerHelper.manager.store(doc, createStamp, modifyStamp);
			methodcontext.remove("ixb_store_object_context/key");
			tx.commit();
			tx = null;
			doc = (WTDocument) importIBAAttribute(doc, this.root);
			doc = (WTDocument) importContentItemAttribute(doc, this.root);
			doc = (WTDocument) importRepresentationAttribute(doc, this.root);
		} catch (Exception e) {
			if ((e instanceof WTException))
				throw ((WTException) e);
			throw new WTException(e);
		} finally {
			if (tx != null)
				tx.rollback();
			methodcontext.remove("ixb_store_object_context/key");
		}
		return doc;
	}


    private WTDocument createNewVersion(WTDocument doc) throws WTException {
        WTDocument newdoc = null;
        Transaction tx = new Transaction();
        MethodContext methodcontext = MethodContext.getContext();
        try {
            long nowtime = Calendar.getInstance().getTimeInMillis();

            String createStampStr = getElementValue(this.root, "createStamp");
            Timestamp createStamp;
            if (createStampStr != null)
                createStamp = new Timestamp(Long.parseLong(createStampStr));
            else {
                createStamp = new Timestamp(nowtime);
            }
            String modifyStampStr = getElementValue(this.root, "modifyStamp");
            Timestamp modifyStamp;
            if (modifyStampStr != null)
                modifyStamp = new Timestamp(Long.parseLong(modifyStampStr));
            else modifyStamp = new Timestamp(nowtime);
            tx.start();
            if (!VersionControlHelper.isLatestIteration(doc))
                doc = (WTDocument) VersionControlHelper.getLatestIteration(doc);
            String versionInfo = getElementValue("versionInfo");
            versionInfo = MQExpImpUtil.attrConvertValue("versionInfo", versionInfo, "IMP");
            //versionId = getPropertiesValue(versionId);
            String iterationId = getElementValue("iterationInfo");
            Series se = VersionControlHelper.getVersionIdentifier(doc).getSeries();
            se.setValueWithoutValidating(versionInfo);
            VersionIdentifier vi = VersionIdentifier.newVersionIdentifier((MultilevelSeries) se);
            Series series = doc.getIterationInfo().getIdentifier().getSeries();
            series.setValueWithoutValidating(iterationId);
            IterationIdentifier ii = IterationIdentifier.newIterationIdentifier(series);
            newdoc = (WTDocument) VersionControlHelper.service.newVersion(doc, true);
            VersionControlHelper.setIterationIdentifier(newdoc, ii);
            VersionControlHelper.setVersionIdentifier(newdoc, vi, false);
            FolderHelper.assignLocation(newdoc, FolderHelper.service.getFolder(doc));
            newdoc.setContainerReference(doc.getContainerReference());

            methodcontext.put("ixb_store_object_context/key", newdoc);
            WTArrayList wtarraylist = new WTArrayList(1);
            wtarraylist.add(newdoc);
            Transaction.getGlobalMap().put(StandardVersionControlService.SVCS_SIGNAL_PERSISTENCE_OF_IGNORE_KEY, true);
            Transaction.getGlobalMap().put(MultiObjIBAValueDBService.DO_NOT_COPY_FORWARD_IBAS_KEY, wtarraylist);
            WTHashSet wthashset = new WTHashSet();
            wthashset.add(newdoc);
            Transaction.getGlobalMap().put("PREVENT_OWNERSHIP_CHECK", wthashset);

            newdoc = (WTDocument) VersionControlHelper.service.insertNode(newdoc, null, null);

            newdoc = (WTDocument) importLifecycleAttribute(newdoc);
            newdoc = importWTDocumentAttribute(newdoc, this.root);

            if (PersistenceHelper.isPersistent(newdoc))
                newdoc = (WTDocument) PersistenceHelper.manager.save(newdoc);
            else newdoc = (WTDocument) PersistenceServerHelper.manager.store(newdoc, createStamp, modifyStamp);
            methodcontext.remove("ixb_store_object_context/key");
            tx.commit();
            tx = null;
            newdoc = (WTDocument) importIBAAttribute(newdoc, this.root);
            newdoc = (WTDocument) importContentItemAttribute(newdoc, this.root);
            newdoc = (WTDocument) importRepresentationAttribute(newdoc, this.root);
            recordImplementAdivse(this.root);
        } catch (Exception e) {
            if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
        } finally {
            if (tx != null)
                tx.rollback();
            methodcontext.remove("ixb_store_object_context/key");
        }
        return newdoc;
    }

    private WTDocument createNewIteration(WTDocument doc) throws WTException {
        WTDocument newdoc = null;
        Transaction tx = new Transaction();
        MethodContext methodcontext = MethodContext.getContext();
        try {
            long nowtime = Calendar.getInstance().getTimeInMillis();

            String createStampStr = getElementValue(this.root, "createStamp");
            Timestamp createStamp;
            if (createStampStr != null)
                createStamp = new Timestamp(Long.parseLong(createStampStr));
            else {
                createStamp = new Timestamp(nowtime);
            }
            String modifyStampStr = getElementValue(this.root, "modifyStamp");
            Timestamp modifyStamp;
            if (modifyStampStr != null)
                modifyStamp = new Timestamp(Long.parseLong(modifyStampStr));
            else modifyStamp = new Timestamp(nowtime);
            tx.start();
            String iterationId = getElementValue(this.root, "iterationInfo");
            newdoc = (WTDocument) VersionControlHelper.service.newIteration(doc, true);
            Series series = doc.getIterationInfo().getIdentifier().getSeries();
            series.setValueWithoutValidating(iterationId);
            IterationIdentifier ii = IterationIdentifier.newIterationIdentifier(series);
            VersionControlHelper.setIterationIdentifier(newdoc, ii);
            VersionControlServerHelper.setBranchIdentifier(newdoc, VersionControlHelper.getBranchIdentifier(doc));
            newdoc.setControlBranch(VersionControlServerHelper.getControlBranch(doc));
            newdoc.setContainerReference(doc.getContainerReference());
            FolderHelper.assignLocation(newdoc, FolderHelper.service.getFolder(doc));
            newdoc = (WTDocument) importLifecycleAttribute(newdoc);

            methodcontext.put("ixb_store_object_context/key", newdoc);
            WTArrayList wtarraylist = new WTArrayList(1);
            wtarraylist.add(newdoc);
            Transaction.getGlobalMap().put(StandardVersionControlService.SVCS_SIGNAL_PERSISTENCE_OF_IGNORE_KEY, true);
            Transaction.getGlobalMap().put(MultiObjIBAValueDBService.DO_NOT_COPY_FORWARD_IBAS_KEY, wtarraylist);
            WTHashSet wthashset = new WTHashSet();
            wthashset.add(newdoc);
            Transaction.getGlobalMap().put("PREVENT_OWNERSHIP_CHECK", wthashset);

            newdoc = (WTDocument) VersionControlHelper.service.insertIteration(newdoc);

            newdoc = importWTDocumentAttribute(newdoc, this.root);

            if (PersistenceHelper.isPersistent(newdoc))
                newdoc = (WTDocument) PersistenceHelper.manager.save(newdoc);
            else {
                newdoc = (WTDocument) PersistenceServerHelper.manager.store(newdoc, createStamp, modifyStamp);
            }
            methodcontext.remove("ixb_store_object_context/key");
            tx.commit();
            tx = null;
            newdoc = (WTDocument) importIBAAttribute(newdoc, this.root);
            newdoc = (WTDocument) importContentItemAttribute(newdoc, this.root);
            newdoc = (WTDocument) importRepresentationAttribute(newdoc, this.root);
            recordImplementAdivse(this.root);
        } catch (Exception e) {
            if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
        } finally {
            if (tx != null)
                tx.rollback();
            methodcontext.remove("ixb_store_object_context/key");
        }
        return newdoc;
    }

}