package com.ptc.extend.ixb.center;

import com.ptc.extend.ixb.*;
import com.ptc.extend.util.ObjectProperty;
import ext.casc.util.IBAHelper;
import ext.sast.center.synch.MQExpImpUtil;
import wt.doc.DepartmentList;
import wt.doc.DocumentType;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.facade.ixb.IxbElement;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTHashSet;
import wt.folder.FolderHelper;
import wt.iba.value.service.MultiObjIBAValueDBService;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainerRef;
import wt.method.MethodContext;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.series.MultilevelSeries;
import wt.series.Series;
import wt.util.WTException;
import wt.vc.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * 导出文档对象类
 *
 */
public class MQExpImpTNO extends MQExpImpPersistable {

	public MQExpImpTNO(CmExporter expHdl) throws WTException {
		super(expHdl);
	}

	public MQExpImpTNO(CmImporter impHdl, String fname) throws WTException {
		super(impHdl, fname);
	}

	public String getRootTag() {
		return MQExpImpConstants.XML_MQTNO;
	}

	public void exportObject(Object obj) throws WTException {
		if (!(obj instanceof WTDocument))
			throw new WTException("Object not WTDocument.");
		WTDocument doc = (WTDocument) obj;
		logger("==>Export WTDocument:" + ObjectProperty.getObjectDisplay(doc));
		exportAttribute(doc);
		this.expHdl.addExportedObject(doc);
		logger("==>Export Linkage of WTDocument:" + ObjectProperty.getObjectDisplay(doc));
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
//			list = DocUtil.getPLDBeforDataLink(doc);
//			if (list.size() > 0)
//				new CmExpImpTechNoticeBeforeLink(doc, this.expHdl, "MQ").exportObject(list);
//			list = DocUtil.getPLDAfterDataLink(doc);
//			if (list.size() > 0)
//				new CmExpImpTechNoticeAfterLink(doc, this.expHdl ,"MQ").exportObject(list);
		} catch (Exception e) {
			logger("==>Exception export Link for ob=<" + ObjectProperty.getObjectDisplay(doc) + ">");
		}
	}

	private void exportAttribute(WTDocument doc) throws WTException {
		exportModelType(doc,this.root);
		exportOriginType(doc,this.root);
		exportUfidAttribute(doc, this.root);
		exportLocalIdAttribute(doc, this.root);
		exportContainerPathAttribute(doc, this.root);
		exportWTDocumentMasterAttribute(doc, this.root);
		exportWTDocumentAttribute(doc, this.root);
		exportDomainFolderAttribute(doc, this.root);
		exportVersionAttribute(doc, this.root);
		exportLifecycleAttribute(doc, this.root);
		// exportTeamAttribute(doc, this.root);
		//exportTypeDefinitionAttribute(doc, this.root);
		exportContentItemAttribute(doc, this.root);
		WTUser user = (WTUser) doc.getCreator().getObject();
		String designer = user.getFullName() + "/" + user.getName();
		exportIBAAttribute(doc, this.root);
		exportRepresentationAttribute(doc, this.root);
		// 导出会签意见信息
		/* exportImplementAdvise(doc, this.root); */
		reallyStore();
	}

	private void exportWTDocumentMasterAttribute(Object obj, IxbElement ixbelement) throws WTException {
		try {
			WTDocument wtdocument = (WTDocument) obj;
			ixbelement.addValue("number", emptyIfNull(wtdocument.getNumber()));
			// WTDocumentMaster wtdocumentmaster =
			// (WTDocumentMaster)wtdocument.getMaster();
			ixbelement.addValue("name", emptyIfNull(wtdocument.getName()));
			ixbelement.addValue("docType", emptyIfNull(wtdocument.getDocType().toString()));
		} catch (Exception exception) {
			logger("Exception in ExpImpForWTDocumentMasterAttr, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
			processException(exception);
		}
	}

	private void exportWTDocumentAttribute(Object obj, IxbElement ixbelement) throws WTException {
		try {
			WTDocument wtdocument = (WTDocument) obj;
			//ixbelement.addValue("docTitle", emptyIfNull(wtdocument.getTitle()));
			//ixbelement.addValue("description", emptyIfNull(wtdocument.getDescription()));
			//ixbelement.addValue("department", emptyIfNull(wtdocument.getDepartment().toString()));
			ixbelement.addValue("createStamp", String.valueOf(wtdocument.getCreateTimestamp().getTime()));
			ixbelement.addValue("creator", emptyIfNull(wtdocument.getCreatorName()));
			ixbelement.addValue("modifyStamp", String.valueOf(wtdocument.getModifyTimestamp().getTime()));
			ixbelement.addValue("modifier", emptyIfNull(wtdocument.getModifierName()));
			ixbelement.addValue("owner", emptyIfNull(wtdocument.getOwnership().getOwner().getFullName()));
			//ixbelement.addValue("originDomainName",ext.sast.center.synch.MQConstants.SITENAME_805);
			String securityLevel = IBAHelper.getIBAStringValue(wtdocument, "SECRET");
			securityLevel = MQExpImpUtil.attrConvertValue("securityLevel", securityLevel, "EXP");
			if(securityLevel == null) {
				securityLevel = "10";
			}
			String phaseInfo = IBAHelper.getIBAStringValue(wtdocument, "PHASE_CODE");
			ixbelement.addValue("securityLevel",securityLevel);
			ixbelement.addValue("phaseInfo",phaseInfo);
			ixbelement.addValue("department",NO_VALUE);
			ixbelement.addValue("productCode",NO_VALUE);
		} catch (Exception exception) {
			logger("Exception in ExpImpForWTDocumentAttr, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
			processException(exception);
		}
	}

	public Object importObject() throws WTException {
		Object obj = importAttribute();
		if (obj != null)
			this.impHdl.pubImportedObject(obj, getRemoteId());
		return obj;
	}

	public WTArrayList importObjects() throws WTException {
		WTArrayList list = new WTArrayList();
		list.add((Persistable) importObject());
		return list;
	}

	private Object importAttribute() throws WTException {
		try {
			if (!isTypeDefinitionImported()) {
				logger("==>WARNING:Import WTDocument number=<" + this.number + "> version=" + this.version + "."
						+ this.iteration + " TypeDefinition not imported, SKIP!");
				return null;
			}
			WTDocument doc = (WTDocument) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(WTDocument.class,
					this.number.toUpperCase(), this.version, this.iteration);
			if (doc != null) {
				logger("==>Import WTDocument number=<" + this.number + "> version=" + this.version + "."
						+ this.iteration + " already imported, IGNORE!");
				this.impHdl.putInExistedHashtable(getRemoteId(), doc);
				doc = (WTDocument) importContentItemAttribute(doc, this.root);
				doc = (WTDocument) importLifecycleAttribute(doc, this.root);
				return doc;
			}
			WTDocument newdoc = null;
			doc = (WTDocument) CmExpImpSearchHelper.searchLatestIteratedByNumberVersion(WTDocument.class, this.number.toUpperCase(),
					this.version);
			if (doc != null) {
				logger("==>Create new Iteration: WTDocument number=<" + this.number + "> version=" + this.version + "."
						+ this.iteration);
				newdoc = createNewIteration(doc);
			} else {
				doc = (WTDocument) CmExpImpSearchHelper.searchLatestIteratedByNumber(WTDocument.class, this.number.toUpperCase());
				if (doc != null) {
					logger("==>Create new Version: WTDocument number=<" + this.number + "> version=" + this.version
							+ "." + this.iteration);
					newdoc = createNewVersion(doc);
				} else {
					logger("==>Create new Object: WTDocument number=<" + this.number + "> version=" + this.version + "."
							+ this.iteration);
					newdoc = createNewObject();
				}
			}
			if (newdoc != null) {
				this.impHdl.putInNewCreatedHashtable(getRemoteId(), newdoc);
				HashMap hmap = buildOrignalInfo();
				this.impHdl.doOperationAfterStore(newdoc, hmap);
				logger("==>Import WTDocument number=<" + this.number + "> version=" + this.version + "."
						+ this.iteration + " OK!");
			}
			return newdoc;
		} catch (Exception exception) {
			logger("Exception in importWTDocument, fname=<" + getPfilename() + ">");
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
			logger("Exception in importWTDocumentMasterAttribute, fname=<" + getPfilename() + ">");
			processException(e);
		}
		return doc;
	}

	private WTDocument importWTDocumentAttribute(Object obj, IxbElement ixbelement) throws WTException {
		WTDocument doc = (WTDocument) obj;
		try {
			String s = getElementValue(ixbelement, "docTitle");
			if (s != null)
				doc.setTitle(s);
			String s1 = getElementValue(ixbelement, "description");
			if (s1 != null)
				doc.setDescription(s1);
			String s2 = getElementValue(ixbelement, "department");
			s2 = null;
			if (s2 != null) {
				DepartmentList.toDepartmentList(s2);
				doc.setDepartment(DepartmentList.toDepartmentList(s2));
			}
			return doc;
		} catch (Exception e) {
			logger("Exception in importWTDocumentAttribute, fname=<" + getPfilename() + ">");
			processException(e);
		}
		return doc;
	}

	private WTDocument createNewObject() throws WTException {
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
			if (modifyStampStr != null)
				modifyStamp = new Timestamp(Long.parseLong(modifyStampStr));
			else
				modifyStamp = new Timestamp(nowtime);
			WTContainerRef wtcontainerref = getWTContainerRef(this.root);
			if (wtcontainerref == null) {
				wtcontainerref = this.impHdl.getWTContainerRef();
			}

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
			else
				modifyStamp = new Timestamp(nowtime);
			tx.start();
			if (!VersionControlHelper.isLatestIteration(doc))
				doc = (WTDocument) VersionControlHelper.getLatestIteration(doc);
			//TODO  版序需要转换
			String versionId = getElementValue("versionInfo");
			versionId = MQExpImpUtil.attrConvertValue("versionInfo", versionId, "IMP");
			// String versionLevel = getElementValue("versionInfo/versionLevel");
			String iterationId = getElementValue("iterationInfo");
			Series se = VersionControlHelper.getVersionIdentifier(doc).getSeries();
			se.setValueWithoutValidating(versionId);
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
			else
				newdoc = (WTDocument) PersistenceServerHelper.manager.store(newdoc, createStamp, modifyStamp);
			methodcontext.remove("ixb_store_object_context/key");
			tx.commit();
			tx = null;
			newdoc = (WTDocument) importIBAAttribute(newdoc, this.root);
			newdoc = (WTDocument) importContentItemAttribute(newdoc, this.root);
			newdoc = (WTDocument) importRepresentationAttribute(newdoc, this.root);
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
			else
				modifyStamp = new Timestamp(nowtime);
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