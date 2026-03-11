package com.ptc.extend.ixb;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;

import wt.configuration.TraceCode;
import wt.facade.ixb.IxbElement;
import wt.fc.EnumeratedType;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTHashSet;
import wt.folder.FolderHelper;
import wt.generic.GenericType;
import wt.iba.value.IBAHolder;
import wt.iba.value.service.MultiObjIBAValueDBService;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainerRef;
import wt.method.MethodContext;
import wt.part.PartType;
import wt.part.QuantityUnit;
import wt.part.Source;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pom.Transaction;
import wt.series.MultilevelSeries;
import wt.series.Series;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.IterationIdentifier;
import wt.vc.Mastered;
import wt.vc.StandardVersionControlService;
import wt.vc.VersionControlHelper;
import wt.vc.VersionControlServerHelper;
import wt.vc.VersionIdentifier;
import wt.vc.views.Variation1;
import wt.vc.views.Variation2;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.views.ViewManageable;
import wt.vc.views.ViewReference;

import com.ptc.extend.util.ObjectProperty;

import ext.casc.util.IBAUtility;

public class CmExpImpGLCatalogItem extends CmExpImpPersistable {

	private CmExpImpGLCatalogItem() {
	}

	public CmExpImpGLCatalogItem(CmExporter expHdl) throws WTException {
		super(expHdl);
	}

	public CmExpImpGLCatalogItem(CmImporter impHdl, String fname) throws WTException {
		super(impHdl, fname);
	}

	public String getRootTag() {
		return CmExpImpConstraints.XML_GLCATALOGITEM;
	}

	public void exportObject(Object obj) throws WTException {
	}

	private void exportAttribute(WTPart part) throws WTException {
		exportUfidAttribute(part, root);
		exportLocalIdAttribute(part, root);
		exportContainerPathAttribute(part, root);
		exportWTPartMasterAttribute(part, root);
		exportWTPartAttribute(part, root);
		exportDomainFolderAttribute(part, root);
		exportViewAttribute(part, root);
		exportVersionAttribute(part, root);
		exportLifecycleAttribute(part, root);
		exportTeamAttribute(part, root);
		exportTypeDefinitionAttribute(part, root);
		exportContentItemAttribute(part, root);

		exportClassificationAttribute(part, root);
		exportClassificationAttribute2(part, root);
		exportIBAAttribute(part, root);

		exportRepresentationAttribute(part, root);
		reallyStore();
	}

	private void exportClassificationAttribute2(WTPart part, IxbElement ixbelement) throws WTException {


	}

	private void exportClassificationAttribute(WTPart part, IxbElement ixbelement) throws WTException {

	}

	private void exportWTPartMasterAttribute(Object obj, IxbElement ixbelement) throws WTException {
		try {
			WTPart wtpart = (WTPart) obj;
			ixbelement.addValue("number", emptyIfNull(wtpart.getNumber()));
			WTPartMaster wtpartmaster = (WTPartMaster) wtpart.getMaster();
			ixbelement.addValue("name", emptyIfNull(wtpart.getName()));
			QuantityUnit quantityunit = wtpart.getDefaultUnit();
			if (quantityunit != null)
				ixbelement.addValue("defaultUnit", emptyIfNull(quantityunit.toString()));
			ixbelement.addValue("endItem", wtpart.isEndItem());
			ixbelement.addValue("defaultTraceCode", emptyIfNull(wtpart.getDefaultTraceCode().toString()));
			ixbelement.addValue("genericType", emptyIfNull(wtpartmaster.getGenericType().toString()));
		} catch (Exception exception) {
			logger("Exception in exportWTPartMasterAttribute, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
			processException(exception);
		}
	}

	public void exportWTPartAttribute(Object obj, IxbElement ixbelement) throws WTException {
	}

	private String getViewName(Object obj) throws WTException {
		if (!(obj instanceof ViewManageable))
			throw new WTException("Not ViewManageable Object!");
		ViewManageable viewmanageable = (ViewManageable) obj;
		String s = viewmanageable.getViewName();
		if (s != null) {
			View view = ViewHelper.service.getView(s);
			if (view != null)
				s = view.getName();
			else
				throw new WTException("Not found View for Object=<" + obj + ">");
		}
		return s;
	}

	private String getVariationName(Object obj, Class class1) throws WTException {
		ViewManageable viewmanageable = (ViewManageable) obj;
		EnumeratedType enumeratedtype = ViewHelper.getVariation(viewmanageable, class1);
		return enumeratedtype != null ? enumeratedtype.toString() : null;
	}

	private void exportViewAttribute(Object obj, IxbElement ixbelement) throws WTException {
		try {
			String s = getViewName(obj);
			ixbelement.addValue("view", emptyIfNull(s));
			String s1 = getVariationName(obj, Variation1.class);
			if (s1 != null)
				ixbelement.addValue("variation1", emptyIfNull(s1));
			String s2 = getVariationName(obj, Variation2.class);
			if (s2 != null)
				ixbelement.addValue("variation2", emptyIfNull(s2));
		} catch (Exception exception) {
			logger("Exception in exportViewAttribute, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
			processException(exception);
		}
	}

	public Object importObject() throws WTException {
		Object obj = importAttribute();
		if (obj != null)
			impHdl.pubImportedObject(obj, getRemoteId());
		return obj;
	}

	public WTArrayList importObjects() throws WTException {
		WTArrayList list = new WTArrayList();
		list.add((Persistable) importObject());
		return list;
	}

	public WTPart importAttribute() throws WTException {
		try {
			WTPart part = (WTPart) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(WTPart.class, number,
					version, iteration);
			if (part != null) {
				logger("==>Import WTPart number=<" + number + "> version=" + version + "." + iteration
						+ " already imported, IGNORE!");
				logger("==>update soft attributes and contents");
				// part = updateAttributes(part);
				// impHdl.putInExistedHashtable(getRemoteId(), part);
				// return part;
                  part = (WTPart) importIBAAttribute(part, this.root);
				return part;
			}
			WTPart newpart = null;
			part = (WTPart) CmExpImpSearchHelper.searchLatestIteratedByNumberVersion(WTPart.class, number, version);
			if (part != null) {
				logger("==>Create new Iteration: WTPart number=<" + number + "> version=" + version + "." + iteration);
				newpart = createNewIteration(part);
			} else {
				part = (WTPart) CmExpImpSearchHelper.searchLatestIteratedByNumber(WTPart.class, number);
				if (part != null) {
					logger("==>Create new Version: WTPart number=<" + number + "> version=" + version + "." + iteration);
					newpart = createNewVersion(part);
				} else {
					logger("==>Create new Object: WTPart number=<" + number + "> version=" + version + "." + iteration);
					newpart = createNewObject();
				}
			}
			if (newpart != null) {
				impHdl.putInNewCreatedHashtable(getRemoteId(), newpart);
				HashMap hmap = buildOrignalInfo();
				impHdl.doOperationAfterStore(newpart, hmap);
				logger("==>Import WTPart number=<" + number + "> version=" + version + "." + iteration + " OK!");
			}
			return newpart;
		} catch (Exception exception) {
			logger("Exception in importAttribute, fname=<" + getPfilename() + ">");
			processException(exception);
		}
		return null;
	}

	private WTPart importWTPartMasterAttribute(Object obj, IxbElement ixbelement) throws WTException {
		WTPart wtpart = (WTPart) obj;
		try {
			String s2 = getElementValue(ixbelement, "genericType");
			String s3 = getElementValue(ixbelement, "endItem");
			String s4 = getElementValue(ixbelement, "defaultTraceCode");
			wtpart.setNumber(number);
			wtpart.setName(iname);
			String s5 = getElementValue(ixbelement, "defaultUnit");
			if (s5 != null)
				wtpart.setDefaultUnit(QuantityUnit.toQuantityUnit(s5));
			if (s2 != null) {
				GenericType generictype = GenericType.toGenericType(s2);
				((WTPartMaster) wtpart.getMaster()).setGenericType(generictype);
			}
			if (s3 != null)
				wtpart.setEndItem(new Boolean(s3).booleanValue());
			if (s4 != null)
				wtpart.setDefaultTraceCode(TraceCode.toTraceCode(s4));
		} catch (Exception e) {
			logger("Exception in importWTPartMasterAttribute, fname=<" + getPfilename() + ">");
			processException(e);
		}
		return wtpart;
	}

	private WTPart importWTPartAttribute(Object obj, IxbElement ixbelement) throws WTException {
		WTPart wtpart = (WTPart) obj;
		try {
			String s = getElementValue(ixbelement, "partType");
			if (s != null)
				wtpart.setPartType(PartType.toPartType(s));
			String s1 = getElementValue(ixbelement, "partSource");
			if (s1 != null)
				wtpart.setSource(Source.toSource(s1));
			String s2 = getElementValue(root, "jobAuthorizationNumber");
			if (s2 != null)
				wtpart.setJobAuthorizationNumber(s2);
			String s3 = getElementValue(root, "contractNumber");
			if (s3 != null)
				wtpart.setContractNumber(s3);
			String s4 = getElementValue(root, "phase");
			if (s4 != null)
				wtpart.setPhase(s4);
			String s5 = getElementValue(root, "minRequired");
			if (s5 != null)
				try {
					Integer integer = Integer.valueOf(s5.trim());
					wtpart.setMinimumRequired(integer);
				} catch (NumberFormatException numberformatexception) {
					throw new WTException(numberformatexception);
				}
			String s6 = getElementValue(root, "maxAllowed");
			if (s6 != null)
				try {
					Integer integer1 = Integer.valueOf(s6.trim());
					wtpart.setMaximumAllowed(integer1);
				} catch (NumberFormatException numberformatexception1) {
					throw new WTException(numberformatexception1);
				}
		} catch (Exception e) {
			logger("Exception in importWTPartAttribute, fname=<" + getPfilename() + ">");
			processException(e);
		}
		return wtpart;
	}

	private ViewManageable importViewAttribute(Object obj, IxbElement ixbelement) throws WTException {
		ViewManageable viewmanageable = (ViewManageable) obj;
		try {
			String s = getElementValue(root, "view");
			String s1 = getElementValue(root, "variation1");
			String s2 = getElementValue(root, "variation2");
			if (s != null) {
				s = impHdl.adjustViewName(s);
				View view = ViewHelper.service.getView(s);
				viewmanageable.setView(ViewReference.newViewReference(view));
				if (s1 != null) {
					Variation1 variation1 = Variation1.toVariation1(s1);
					viewmanageable.setVariation1(variation1);
				}
				if (s2 != null) {
					Variation2 variation2 = Variation2.toVariation2(s2);
					viewmanageable.setVariation2(variation2);
				}
			}
		} catch (Exception e) {
			logger("Exception in importViewAttribute, fname=<" + getPfilename() + ">");
			processException(e);
		}
		return viewmanageable;
	}

	private WTPart updateAttributes(WTPart part) throws WTException {
		try {
			part = (WTPart) importIBAAttribute(part, root);
			part = (WTPart) importContentItemAttribute(part, root);
			part = (WTPart) importRepresentationAttribute(part, root);
		} catch (Exception e) {
			if ((e instanceof WTException))
				throw ((WTException) e);
			throw new WTException(e);
		}
		return part;
	}

	private WTPart createNewObject() throws WTException {
		WTPart part = null;
		Transaction tx = new Transaction();
		MethodContext methodcontext = MethodContext.getContext();
		try {
			long nowtime = Calendar.getInstance().getTimeInMillis();

			String createStampStr = getElementValue(root, "createtime");
			Timestamp createStamp;
			if (createStampStr != null)
				createStamp = new Timestamp(Long.parseLong(createStampStr));
			else {
				createStamp = new Timestamp(nowtime);
			}
			String modifyStampStr = getElementValue(root, "modifytime");
			Timestamp modifyStamp;
			if (modifyStampStr != null)
				modifyStamp = new Timestamp(Long.parseLong(modifyStampStr));
			else
				modifyStamp = new Timestamp(nowtime);
			WTContainerRef wtcontainerref = impHdl.getWTContainerRef();
			if(wtcontainerref==null){
				///wt.inf.container.OrgContainer=sast/wt.inf.library.WTLibrary=标准紧固件库
				 String objectContainerPath = getElementValue(root, "objectContainerPath");
				 String tempPath = objectContainerPath.substring(objectContainerPath.indexOf("=")+1);;
				 String  clazz= tempPath.substring(tempPath.indexOf("/")+1,tempPath.indexOf("="));
				 String containerRefName = tempPath.substring(tempPath.indexOf("=")+1);
				 wtcontainerref = impHdl.getWTContainerRef(Class.forName(clazz), containerRefName);
			}
			tx.start();
			part = WTPart.newWTPart();
            part = importWTPartMasterAttribute(part, this.root);
           // part = (WTPart) importViewAttribute(part, this.root);
            part = (WTPart) importLifecycleAttribute(part, this.root);
            part = (WTPart) importVersionAttribute(part, this.root);

            part = importWTPartAttribute(part, this.root);
            part = (WTPart) importDomainFolderAttribute(part, this.root);

            part = (WTPart) importTypeDefinitionAttribute(part, this.root, this.root);
            part.setContainerReference(wtcontainerref);
            ((WTContained) part.getMaster()).setContainerReference(wtcontainerref);
            Mastered mastered = part.getMaster();

			PersistenceHelper.manager.save(mastered);
			methodcontext.put("ixb_store_object_context/key", part);
			WTArrayList wtarraylist = new WTArrayList(1);
			wtarraylist.add(part);
			Transaction.getGlobalMap().put(StandardVersionControlService.SVCS_SIGNAL_PERSISTENCE_OF_IGNORE_KEY, true);
			Transaction.getGlobalMap().put(MultiObjIBAValueDBService.DO_NOT_COPY_FORWARD_IBAS_KEY, wtarraylist);
			WTHashSet wthashset = new WTHashSet();
			wthashset.add(part);
			Transaction.getGlobalMap().put("PREVENT_OWNERSHIP_CHECK", wthashset);
			part = (WTPart) PersistenceServerHelper.manager.store(part, createStamp, modifyStamp);
			methodcontext.remove("ixb_store_object_context/key");
			tx.commit();
			tx = null;
			part = (WTPart) importClassificationNode(part, this.root);
			part = (WTPart) importIBAAttribute(part, root);
		   
			part = (WTPart) importContentItemAttribute(part, root);
			part = (WTPart) importRepresentationAttribute(part, root);
		} catch (Exception e) {
			if ((e instanceof WTException))
				throw ((WTException) e);
			throw new WTException(e);
		} finally {
			if (tx != null)
				tx.rollback();
			methodcontext.remove("ixb_store_object_context/key");
		}
		return part;
	}

	private WTPart createNewVersion(WTPart part) throws WTException {
		WTPart newpart = null;
		Transaction tx = new Transaction();
		MethodContext methodcontext = MethodContext.getContext();
		try {
			long nowtime = Calendar.getInstance().getTimeInMillis();

			String createStampStr = getElementValue(root, "createtime");
			Timestamp createStamp;
			if (createStampStr != null)
				createStamp = new Timestamp(Long.parseLong(createStampStr));
			else {
				createStamp = new Timestamp(nowtime);
			}
			String modifyStampStr = getElementValue(root, "modifytime");
			Timestamp modifyStamp;
			if (modifyStampStr != null)
				modifyStamp = new Timestamp(Long.parseLong(modifyStampStr));
			else
				modifyStamp = new Timestamp(nowtime);
			tx.start();
			if (!VersionControlHelper.isLatestIteration(part))
				part = (WTPart) VersionControlHelper.getLatestIteration(part);
			String versionId = getElementValue(root, "versionInfo/versionId");
			// String versionLevel = getElementValue(root,
			// "versionInfo/versionLevel");
			String iterationId = getElementValue(root, "versionInfo/iterationId");
			Series se = VersionControlHelper.getVersionIdentifier(part).getSeries();
			se.setValueWithoutValidating(versionId);
			VersionIdentifier vi = VersionIdentifier.newVersionIdentifier((MultilevelSeries) se);
			Series series = part.getIterationInfo().getIdentifier().getSeries();
			series.setValueWithoutValidating(iterationId);
			IterationIdentifier ii = IterationIdentifier.newIterationIdentifier(series);
			newpart = (WTPart) VersionControlHelper.service.newVersion(part, true);
			VersionControlHelper.setIterationIdentifier(newpart, ii);
			VersionControlHelper.setVersionIdentifier(newpart, vi, false);
			newpart = (WTPart) importLifecycleAttribute(newpart);
			FolderHelper.assignLocation(newpart, FolderHelper.service.getFolder(part));
			newpart.setContainerReference(part.getContainerReference());

			methodcontext.put("ixb_store_object_context/key", newpart);
			WTArrayList wtarraylist = new WTArrayList(1);
			wtarraylist.add(newpart);
			Transaction.getGlobalMap().put(StandardVersionControlService.SVCS_SIGNAL_PERSISTENCE_OF_IGNORE_KEY, true);
			Transaction.getGlobalMap().put(MultiObjIBAValueDBService.DO_NOT_COPY_FORWARD_IBAS_KEY, wtarraylist);
			WTHashSet wthashset = new WTHashSet();
			wthashset.add(newpart);
			Transaction.getGlobalMap().put("PREVENT_OWNERSHIP_CHECK", wthashset);

			newpart = (WTPart) VersionControlHelper.service.insertNode(newpart, null, null);
			newpart = importWTPartAttribute(newpart, root);

			if (PersistenceHelper.isPersistent(newpart))
				newpart = (WTPart) PersistenceHelper.manager.save(newpart);
			else
				newpart = (WTPart) PersistenceServerHelper.manager.store(newpart, createStamp, modifyStamp);
			methodcontext.remove("ixb_store_object_context/key");
			tx.commit();
			tx = null;
			newpart = (WTPart) importClassificationNode(newpart, this.root);
			newpart = (WTPart) importIBAAttribute(newpart, root);
			

			newpart = (WTPart) importContentItemAttribute(newpart, root);
			newpart = (WTPart) importRepresentationAttribute(newpart, root);
		} catch (Exception e) {
			if ((e instanceof WTException))
				throw ((WTException) e);
			throw new WTException(e);
		} finally {
			if (tx != null)
				tx.rollback();
			methodcontext.remove("ixb_store_object_context/key");
		}
		return newpart;
	}

	private WTPart createNewIteration(WTPart part) throws WTException {
		WTPart newpart = null;
		Transaction tx = new Transaction();
		MethodContext methodcontext = MethodContext.getContext();
		try {
			long nowtime = Calendar.getInstance().getTimeInMillis();

			String createStampStr = getElementValue(root, "createtime");
			Timestamp createStamp;
			if (createStampStr != null)
				createStamp = new Timestamp(Long.parseLong(createStampStr));
			else {
				createStamp = new Timestamp(nowtime);
			}
			String modifyStampStr = getElementValue(root, "modifytime");
			Timestamp modifyStamp;
			if (modifyStampStr != null)
				modifyStamp = new Timestamp(Long.parseLong(modifyStampStr));
			else
				modifyStamp = new Timestamp(nowtime);
			tx.start();
			String iterationId = getElementValue(root, "versionInfo/iterationId");
			newpart = (WTPart) VersionControlHelper.service.newIteration(part, true);
			Series series = part.getIterationInfo().getIdentifier().getSeries();
			series.setValueWithoutValidating(iterationId);
			IterationIdentifier ii = IterationIdentifier.newIterationIdentifier(series);
			VersionControlHelper.setIterationIdentifier(newpart, ii);
			VersionControlServerHelper.setBranchIdentifier(newpart, VersionControlHelper.getBranchIdentifier(part));
			newpart.setControlBranch(VersionControlServerHelper.getControlBranch(part));
			newpart.setContainerReference(part.getContainerReference());
			FolderHelper.assignLocation(newpart, FolderHelper.service.getFolder(part));
			newpart = (WTPart) importLifecycleAttribute(newpart);

			methodcontext.put("ixb_store_object_context/key", newpart);
			WTArrayList wtarraylist = new WTArrayList(1);
			wtarraylist.add(newpart);
			Transaction.getGlobalMap().put(StandardVersionControlService.SVCS_SIGNAL_PERSISTENCE_OF_IGNORE_KEY, true);
			Transaction.getGlobalMap().put(MultiObjIBAValueDBService.DO_NOT_COPY_FORWARD_IBAS_KEY, wtarraylist);
			WTHashSet wthashset = new WTHashSet();
			wthashset.add(newpart);
			Transaction.getGlobalMap().put("PREVENT_OWNERSHIP_CHECK", wthashset);

			newpart = (WTPart) VersionControlHelper.service.insertIteration(newpart);

			newpart = importWTPartAttribute(newpart, root);

			if (PersistenceHelper.isPersistent(newpart))
				newpart = (WTPart) PersistenceHelper.manager.save(newpart);
			else {
				newpart = (WTPart) PersistenceServerHelper.manager.store(newpart, createStamp, modifyStamp);
			}
			methodcontext.remove("ixb_store_object_context/key");
			tx.commit();
			tx = null;
			 newpart = (WTPart) importClassificationNode(newpart, this.root);
			newpart = (WTPart) importIBAAttribute(newpart, root);
           

			newpart = (WTPart) importContentItemAttribute(newpart, root);
			newpart = (WTPart) importRepresentationAttribute(newpart, root);
		} catch (Exception e) {
			if ((e instanceof WTException))
				throw ((WTException) e);
			throw new WTException(e);
		} finally {
			if (tx != null)
				tx.rollback();
			methodcontext.remove("ixb_store_object_context/key");
		}
		return newpart;
	}
	 private WTPart importClassificationNode(WTPart part, IxbElement root) throws WTException {
	    	String s = getElementValue(this.root, "ClassificationNode");
	    	IBAUtility iba = new IBAUtility((IBAHolder)part);
			try {
				iba.setIBAValue( "ClassificationNode", s);
				part = (WTPart) iba.updateAttributeContainer(part);
				iba.updateIBAHolder(part);
			} catch (WTPropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	return part;
		}
}