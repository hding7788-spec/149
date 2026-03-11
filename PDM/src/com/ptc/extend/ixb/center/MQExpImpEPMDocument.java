package com.ptc.extend.ixb.center;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.ptc.extend.ixb.*;
import com.ptc.extend.util.ObjectProperty;

import ext.sast.center.synch.MQExpImpUtil;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.epm.AuthoringAppVersionHelper;
import wt.epm.EPMApplicationType;
import wt.epm.EPMAuthoringAppType;
import wt.epm.EPMAuthoringAppVersion;
import wt.epm.EPMBoxExtents;
import wt.epm.EPMCADReferenceControl;
import wt.epm.EPMContextHelper;
import wt.epm.EPMDocSubType;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMDocumentMasterIdentity;
import wt.epm.EPMDocumentType;
import wt.epm.attributes.EPMParameterMap;
import wt.epm.familytable.EPMFeatureValue;
import wt.epm.familytable.EPMParameterValue;
import wt.epm.util.EPMQueryHelper;
import wt.facade.ixb.IxbElement;
import wt.fc.IdentityHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTHashSet;
import wt.folder.FolderHelper;
import wt.iba.definition.AbstractAttributeDefinition;
import wt.iba.definition.AttributeDefinitionReference;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionCache;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.definition.service.IBADefinitionObjectsFactory;
import wt.iba.value.IBAHolder;
import wt.iba.value.IBAHolderReference;
import wt.iba.value.service.MultiObjIBAValueDBService;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainerRef;
import wt.ixb.epm.handlers.EPMHndHelper;
import wt.ixb.handlers.forattributes.ExpImpForIBAAttr;
import wt.ixb.publicforhandlers.IxbHndHelper;
import wt.ixb.publicforhandlers.LogHelper;
import wt.method.MethodContext;
import wt.part.QuantityUnit;
import wt.pom.Transaction;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.representation.RepresentationHelper;
import wt.series.MultilevelSeries;
import wt.series.Series;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.IterationIdentifier;
import wt.vc.Mastered;
import wt.vc.StandardVersionControlService;
import wt.vc.VersionControlHelper;
import wt.vc.VersionControlServerHelper;
import wt.vc.VersionIdentifier;
import wt.vc.wip.WorkInProgressHelper;

public class MQExpImpEPMDocument extends MQExpImpPersistable {

	public MQExpImpEPMDocument(CmExporter expHdl) throws WTException {
		super(expHdl);
	}

	public MQExpImpEPMDocument(CmImporter impHdl, String fname) throws WTException {
		super(impHdl, fname);
	}

	public String getRootTag() {
		return MQExpImpConstants.XML_MQCADDOCUMENT;
	}

	public void exportObject(Object obj) throws WTException {
		if (!(obj instanceof EPMDocument))
			throw new WTException("Object not EPMDocument.");
		EPMDocument epm = (EPMDocument) obj;
		logger("==>Export EPMDocument:" + ObjectProperty.getObjectDisplay(epm));
		exportAttribute(epm);
		expHdl.addExportedObject(epm);
		logger("==>Export Linkage of EPMDocument:" + ObjectProperty.getObjectDisplay(epm));
		try {
			ArrayList list = CmExpImpSearchHelper.searchAllEPMMemberLink(epm);
			if (list.size() > 0)
				new CmExpImpEPMMemberLink(epm, expHdl).exportObject(list);
			list = CmExpImpSearchHelper.searchAllEPMDescribeLink(epm);
			if (list.size() > 0)
				new CmExpImpEPMDescribeLink(epm, expHdl).exportObject(list);
			list = CmExpImpSearchHelper.searchAllEPMReferenceLink(epm);
			if (list.size() > 0)
				new CmExpImpEPMReferenceLink(epm, expHdl).exportObject(list);
			list = CmExpImpSearchHelper.searchAllEPMBuildRule(epm);
			if (list.size() > 0)
				new CmExpImpEPMBuildRule(epm, expHdl).exportObject(list);
			list = CmExpImpSearchHelper.searchAllEPMBuildHistory(epm);
			if (list.size() > 0)
				new CmExpImpEPMBuildHistory(epm, expHdl).exportObject(list);
		} catch (Exception e) {
			logger("==>Exception export Link for ob=<" + ObjectProperty.getObjectDisplay(epm) + ">");
		}
	}

	/**
	 * @param epm
	 * @throws WTException
	 */
	private void exportAttribute(EPMDocument epm) throws WTException {
		exportLocalIdAttribute(epm, root);
		exportContainerPathAttribute(epm, root);
		exportEPMDocumentMasterAttribute(epm, root);
		exportEPMDocumentAttribute(epm, root);
		exportEPMCADReferenceControlAttribute(epm, root);
		exportEPMExtentsAttribute(epm, root);
		exportDomainFolderAttribute(epm, root);
		exportVersionAttribute(epm, root);
		exportLifecycleAttribute(epm, root);
		exportTeamAttribute(epm, root);
		exportContentItemAttribute(epm, root);
		exportTypeDefinitionAttribute(epm, root);
		exportIBAAttribute(root, epm);
		exportEPMParameterMapAttribute(epm, root);
		root.addValue("isMissingDependents", epm.isMissingDependents());
		exportEPMFeatureValueAttribute(epm, root);
		exportEPMParameterValueAttribute(epm, root);
		exportRepresentationAttribute(epm, root);
		reallyStore();

	}

	protected void exportIBAAttribute(IxbElement ixbelement, EPMDocument epmdocument) throws WTException {
		if (!(epmdocument instanceof IBAHolder))
			return;
		try {
			IBAHolder ibaholder = (IBAHolder) epmdocument;
			Hashtable hashtable = new Hashtable();
			Hashtable defination = new Hashtable();
			boolean flag = getIBAttributes(ibaholder, hashtable, defination);

			for (Enumeration enumeration = hashtable.keys(); enumeration.hasMoreElements();) {
				String s2 = (String) enumeration.nextElement();
				AttributeDefDefaultView addv = (AttributeDefDefaultView) defination.get(s2);
				AbstractAttributeDefinition aad = (AbstractAttributeDefinition) IBADefinitionCache.getIBADefinitionCache().getAttributeDefinition(addv.getObjectID());
				String valueClassname = aad.getValueClassname();
				int ji = valueClassname.lastIndexOf('.');
				if (ji > 0)
					valueClassname = valueClassname.substring(ji + 1);
				Object obj1 = hashtable.get(s2);
				String s3 = valueClassname;
				String s4 = IxbHndHelper.getObjectIdImage(aad);
				String s5 = null;
				String s6 = null;
				if ((obj1 instanceof Vector)) {
					Vector vector3 = (Vector) obj1;
					for (int j = 0; j < vector3.size(); j++) {
						String[] as = (String[]) vector3.elementAt(j);
						String s7 = as[0];
						if (s3.equals("RatioValue")) {
							int k = s7.lastIndexOf(",");
							s5 = s7.substring(k + 1);
							s7 = s7.substring(0, k);
						} else if ((s3.equals("UnitValue")) || (s3.equals("FloatValue"))) {
							int l = s7.lastIndexOf(",");
							s6 = s7.substring(l + 1);
							s7 = s7.substring(0, l);
						}
						if (flag)
							addIBAElementToXML(ixbelement, "ext", s2, s7, s3, s6, s5, as[2]);
						else
							addIBAElementToXML(ixbelement, "ext", s2, s7, s3, s6, s5);
					}
				}
			}


		} catch (Exception exception) {
			logger("Exception in exportIBAAttribute, ob=<" + ObjectProperty.getObjectDisplay(epmdocument) + ">");
			processException(exception);
		}
	}


	private void exportEPMDocumentMasterAttribute(Object obj, IxbElement ixbelement) throws WTException {
		try {
			EPMDocument epmdocument = (EPMDocument) obj;
			ixbelement.addValue("ownerApplication", emptyIfNull(epmdocument.getOwnerApplication().toString()));
			ixbelement.addValue("authoringApplication", emptyIfNull(epmdocument.getAuthoringApplication().toString()));
			ixbelement.addValue("number", emptyIfNull(emptyIfNull(epmdocument.getNumber())));
			EPMDocumentMaster epmdocumentmaster = (EPMDocumentMaster) epmdocument.getMaster();
			exportEPMTypeDefinitionAttr(epmdocumentmaster, ixbelement);
			IxbElement ixbelement1 = ixbelement.addElement("masterIba");
			exportIBAAttribute(epmdocumentmaster, ixbelement1);
			ixbelement.addValue("name", emptyIfNull(epmdocument.getName()));
			ixbelement.addValue("CADName", emptyIfNull(epmdocument.getCADName()));
			ixbelement.addValue("epmDocType", emptyIfNull(epmdocument.getDocType().toString()));
			EPMDocSubType epmdocsubtype = epmdocument.getDocSubType();
			if (epmdocsubtype != null)
				ixbelement.addValue("epmDocSubType", emptyIfNull(epmdocsubtype.toString()));
			QuantityUnit quantityunit = epmdocument.getDefaultUnit();
			if (quantityunit != null)
				ixbelement.addValue("defaultUnit", emptyIfNull(quantityunit.toString()));
		} catch (Exception e) {
			logger("Exception in exportEPMDocumentMasterAttribute, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
			processException(e);
		}
	}

	private void exportEPMDocumentAttribute(Object obj, IxbElement ixbelement) throws WTException {
		try {
			EPMDocument epmdocument = (EPMDocument) obj;
			if (epmdocument.getDescription() != null)
				ixbelement.addValue("description", emptyIfNull(epmdocument.getDescription()));
			if (epmdocument.getAuthoringAppVersion() != null) {
				ixbelement.addValue("authoringApplicationVersion/versionNumber", epmdocument.getAuthoringAppVersion().getVersionNumber());
				ixbelement.addValue("authoringApplicationVersion/versionName", emptyIfNull(epmdocument.getAuthoringAppVersion().getVersionName()));
			}
			ixbelement.addValue("dbKeySize", epmdocument.getDbKeySize());
			ixbelement.addValue("isVerified", epmdocument.isVerified());
			ixbelement.addValue("revisionNumber", epmdocument.getRevisionNumber());
			ixbelement.addValue("familyTableStatus", epmdocument.getFamilyTableStatus());
			ixbelement.addValue("derived", epmdocument.isDerived());
			ixbelement.addValue("creator", emptyIfNull(epmdocument.getCreatorName()));
            ixbelement.addValue("createStamp", String.valueOf(epmdocument.getCreateTimestamp().getTime()));
            ixbelement.addValue("modifier", emptyIfNull(epmdocument.getModifierName()));
            ixbelement.addValue("modifyStamp", String.valueOf(epmdocument.getModifyTimestamp().getTime()));
            ixbelement.addValue("originDomainName", MQExpImpConstants.VALUE_DOMAINNAME);
            ixbelement.addValue("securityLevel", "10");
            ixbelement.addValue("owner", epmdocument.getOwnership().getOwner().getName());
		} catch (Exception e) {
			logger("Exception in exportEPMDocumentAttribute, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
			processException(e);
		}
	}

	private void exportEPMTypeDefinitionAttr(Object obj, IxbElement ixbelement) throws WTException {
		exportTypeDefinitionAttribute(obj, ixbelement);
	}

	private void exportEPMCADReferenceControlAttribute(Object obj, IxbElement ixbelement) throws WTException {
		try {
			EPMCADReferenceControl epmcadreferencecontrol = ((EPMDocument) obj).getReferenceControl();
			if (epmcadreferencecontrol == null)
				return;
			ixbelement.addValue("epmCADReferenceControl/geomRestr", epmcadreferencecontrol.getGeomRestr());
			ixbelement.addValue("epmCADReferenceControl/isGeomRestrRecursive", epmcadreferencecontrol.isGeomRestrRecursive());
			ixbelement.addValue("epmCADReferenceControl/scope", epmcadreferencecontrol.getScope());
			ixbelement.addValue("epmCADReferenceControl/violRestriction", epmcadreferencecontrol.getViolRestriction());
		} catch (Exception e) {
			logger("Exception in exportEPMCADReferenceControlAttribute, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
			processException(e);
		}
	}

	private void exportEPMExtentsAttribute(Object obj, IxbElement ixbelement) throws WTException {
		try {
			EPMDocument epmdocument = (EPMDocument) obj;
			EPMBoxExtents epmboxextents = epmdocument.getBoxExtents();
			if (epmboxextents != null) {
				ixbelement.addValue("extentsValid", epmdocument.isExtentsValid());
				Vector vector = epmboxextents.getAxyz();
				Double double1 = (Double) vector.elementAt(0);
				ixbelement.addValue("epmBoxExtents/Ax", double1.doubleValue());
				Double double2 = (Double) vector.elementAt(1);
				ixbelement.addValue("epmBoxExtents/Ay", double2.doubleValue());
				Double double3 = (Double) vector.elementAt(2);
				ixbelement.addValue("epmBoxExtents/Az", double3.doubleValue());
				Vector vector1 = epmboxextents.getBxyz();
				Double double4 = (Double) vector1.elementAt(0);
				ixbelement.addValue("epmBoxExtents/Bx", double4.doubleValue());
				Double double5 = (Double) vector1.elementAt(1);
				ixbelement.addValue("epmBoxExtents/By", double5.doubleValue());
				Double double6 = (Double) vector1.elementAt(2);
				ixbelement.addValue("epmBoxExtents/Bz", double6.doubleValue());
			}
		} catch (Exception e) {
			logger("Exception in exportEPMExtentsAttribute, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
			processException(e);
		}
	}

	private void exportEPMParameterMapAttribute(Object obj, IxbElement ixbelement) throws WTException {
		try {
			IBAHolder ibaholder = (IBAHolder) obj;
			WTArrayList wtarraylist = new WTArrayList(1);
			wtarraylist.add(ibaholder);
			QueryResult queryresult = EPMQueryHelper.lookupParamMaps(wtarraylist);
			EPMParameterMap epmparametermap;
			IxbElement ixbelement1;
			for (; queryresult.hasMoreElements(); ixbelement1.addValue("parameterName", emptyIfNull(epmparametermap.getParameterName()))) {
				epmparametermap = (EPMParameterMap) queryresult.nextElement();
				AbstractAttributeDefinition abstractattributedefinition = ExpImpForIBAAttr.getIBADefOfHierarchyID(epmparametermap.getDefinitionReference().getHierarchyID());
				String s = ExpImpForIBAAttr.getPathOfAttributeDefinition_WithoutOrganizer(abstractattributedefinition);
				ixbelement1 = ixbelement.addElement("EPMParameterMap");
				ixbelement1.addValue("extPath", emptyIfNull(s));
			}
		} catch (Exception e) {
			logger("Exception in exportEPMParameterMapAttribute, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
			processException(e);
		}
	}

	private void exportEPMFeatureValueAttribute(Object obj, IxbElement ixbelement) throws WTException {
		try {
			EPMDocument epmdocument = (EPMDocument) obj;
			Map map = epmdocument.getFeatureValues();
			if (map == null)
				return;
			Iterator iterator = map.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry entry = (Map.Entry) iterator.next();
				IxbElement ixbelement1 = ixbelement.addElement("EPMFeatureValue");
				String s = (String) entry.getKey();
				EPMFeatureValue epmfeaturevalue = (EPMFeatureValue) entry.getValue();
				ixbelement1.addValue("definitionName", emptyIfNull(s));
				if (epmfeaturevalue != null) {
					Object obj1 = epmfeaturevalue.getValue();
					String s1 = null;
					String s2 = null;
					if (obj1 != null) {
						s1 = obj1.getClass().getName();
						if ((obj1 instanceof Date))
							s2 = String.valueOf(((Date) obj1).getTime());
						else
							s2 = obj1.toString();
					}
					ixbelement1.addValue("valueClassname", emptyIfNull(s1));
					ixbelement1.addValue("valueString", emptyIfNull(s2));
				}
			}
		} catch (Exception e) {
			logger("Exception in exportEPMFeatureValueAttribute, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
			processException(e);
		}
	}

	private void exportEPMParameterValueAttribute(Object obj, IxbElement ixbelement) throws WTException {
		try {
			EPMDocument epmdocument = (EPMDocument) obj;
			Map map = epmdocument.getParameterValues();
			if (map == null)
				return;
			Iterator iterator = map.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry entry = (Map.Entry) iterator.next();
				IxbElement ixbelement1 = ixbelement.addElement("EPMParameterValue");
				String s = (String) entry.getKey();
				EPMParameterValue epmparametervalue = (EPMParameterValue) entry.getValue();
				ixbelement1.addValue("definitionName", emptyIfNull(s));
				if (epmparametervalue != null) {
					Object obj1 = epmparametervalue.getValue();
					String s1 = null;
					String s2 = null;
					if (obj1 != null) {
						s1 = obj1.getClass().getName();
						if ((obj1 instanceof Date))
							s2 = String.valueOf(((Date) obj1).getTime());
						else
							s2 = obj1.toString();
					}
					ixbelement1.addValue("valueClassname", emptyIfNull(s1));
					ixbelement1.addValue("valueString", emptyIfNull(s2));
				}
			}
		} catch (Exception e) {
			logger("Exception in exportEPMParameterValueAttribute, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
			processException(e);
		}
	}

	public synchronized Object importObject() throws WTException {
		Object obj = importAttribute();
		if (obj != null && obj instanceof EPMDocument) {
			EPMDocument epm = (EPMDocument) obj;
			changeNumberAndName(epm);
		}
		if (obj != null) {
			if (obj instanceof ErrorImportObject) {
			} else {
				this.impHdl.pubImportedObject(obj, getRemoteId());
			}
		}
		return obj;
	}

	private void changeNumber(EPMDocument epm, String number, String name) throws WTException, WTPropertyVetoException {
		EPMDocumentMaster master = (EPMDocumentMaster) epm.getMaster();
		EPMDocumentMasterIdentity idy = (EPMDocumentMasterIdentity) master.getIdentificationObject();
		idy.setNumber(number);
		if (name != null && !"".equals(name) && !"null".equals(name)) {
			idy.setName(name);
		}
		master = (EPMDocumentMaster) IdentityHelper.service.changeIdentity(master, idy);
	}

	public WTArrayList importObjects() throws WTException {
		WTArrayList list = new WTArrayList();
		list.add((Persistable) importObject());
		return list;
	}

	private Object importAttribute() throws WTException {
		EPMDocument epm = null;
		String CADName = "";
		try {
			if (!isTypeDefinitionImported()) {
				logger.log("==>WARNING:Import EPMDocument number=<" + this.number + "> version=" + this.version + "." + this.iteration + " TypeDefinition not imported, SKIP!");
				return null;
			}
			String versionInfo = this.version;
			CADName = getElementValue("cadName");
			if (CADName == null) {
				CADName = "";
			}
			epm = (EPMDocument) CmExpImpSearchHelper.searchIteratedByNumberCADNameVersionIteration(EPMDocument.class, this.number, CADName, versionInfo, this.iteration);

			if(epm != null) {
				//查出来之后先判断模型是不是工作副本，如果是则删掉，防止修改状态失败
				if(WorkInProgressHelper.isPrivateWorkingCopy(epm)) {
					CmExpImpEPMDocument.purgeFromWs(epm);
					epm = (EPMDocument) CmExpImpSearchHelper.searchIteratedByNumberCADNameVersionIteration(EPMDocument.class, this.number, CADName, versionInfo, this.iteration);
				}
			}

			if (epm != null) {
				logger.log("==>Import EPMDocument number=<" + this.number + "> version=" + this.version + "." + this.iteration + " already imported, Go On Import Sign File!New");
				epm = (EPMDocument) importContentItemAttribute(epm, this.root);
				this.impHdl.putInExistedHashtable(getRemoteId(), epm);
				epm = (EPMDocument) importIBAAttribute(epm, this.root);
				recordImplementAdivse(this.root);
				epm = (EPMDocument) importLifecycleAttribute(epm, this.root);

				changeNumberAndName(epm);
				// add by hding 20171010 begin
				// 删掉默认可视化
				try {
					Representation representation = RepresentationHelper.service.getDefaultRepresentation((Representable) epm);
					if (representation != null) {
						RepresentationHelper.service.deleteRepresentation(representation);
					}
					// 重新导入可视化文件
					epm = (EPMDocument) importRepresentationAttribute(epm, this.root);

				} catch (Exception e) {
					e.printStackTrace();
				}
				PersistenceServerHelper.manager.update(epm);
				// and by hding 20171010 end
				return epm;
			}
			EPMDocument newepm = null;
			epm = (EPMDocument) CmExpImpSearchHelper.searchLatestIteratedByNumberCADNameVersion(EPMDocument.class, this.number, CADName, versionInfo);

			if(epm != null) {
				//查出来之后先判断模型是不是工作副本，如果是则删掉，防止修改状态失败
				if(WorkInProgressHelper.isPrivateWorkingCopy(epm)) {
					CmExpImpEPMDocument.purgeFromWs(epm);
					epm = (EPMDocument) CmExpImpSearchHelper.searchLatestIteratedByNumberCADNameVersion(EPMDocument.class, this.number, CADName, versionInfo);
				}
			}

			if (epm != null) {
				logger.log("==>Create new Iteration: EPMDocument number=<" + this.number + "> version=" + this.version + "." + this.iteration);

				changeNumberAndName(epm);
				newepm = createNewIteration(epm);
			} else {
				epm = (EPMDocument) CmExpImpSearchHelper.searchLatestIteratedByNumberCADName(EPMDocument.class, this.number, CADName);

				if(epm != null) {
					//查出来之后先判断模型是不是工作副本，如果是则删掉，防止修改状态失败
					if(WorkInProgressHelper.isPrivateWorkingCopy(epm)) {
						CmExpImpEPMDocument.purgeFromWs(epm);
						epm = (EPMDocument) CmExpImpSearchHelper.searchLatestIteratedByNumberCADName(EPMDocument.class, this.number, CADName);
					}
				}

				if (epm != null) {
					if (epm.getContainerName().startsWith("八院")) {
						logger.log("==>八院数据跳过n: WTPart number=<" + this.number + "> version=" + this.version + "." + this.iteration);
						return epm;
					}

					changeNumberAndName(epm);
					logger.log("==>Create new Version: EPMDocument number=<" + this.number + "> version=" + this.version + "." + this.iteration);
					newepm = createNewVersion(epm);
				} else {
					logger.log("==>Create new Object: EPMDocument number=<" + this.number + "> version=" + this.version + "." + this.iteration);
					newepm = createNewObject();
				}
			}
			if (newepm != null) {
				this.impHdl.putInNewCreatedHashtable(getRemoteId(), newepm);
				HashMap hmap = buildOrignalInfo();
				this.impHdl.doOperationAfterStore(newepm, hmap);
				logger.log("==>Import EPMDocument number=<" + this.number + "> version=" + this.version + "." + this.iteration + " OK!");
			}
			return newepm;
		} catch (Exception exception) {
			logger.log("***Exception in importAttribute, fname=<" + getPfilename() + ">");

			String errorMsg = exception.getLocalizedMessage();
			if (errorMsg != null && (errorMsg.contains("因为它已经被同步更新") || errorMsg.contains("密钥的对象"))) {
				logger.log(errorMsg);
				return epm;
			} else {
				ErrorImportObject eo = new ErrorImportObject();
				eo.setNumber(this.number);
				eo.setCadName(CADName);
				eo.setXmlName(getPfilename());
				eo.setType("EPMDocument");
				if(exception.getLocalizedMessage()!=null&&exception.getLocalizedMessage().contains("未找到上下文")){
					String productId = getElementValue(this.root, "productId");
					eo.setMessage(this.number+"149PDM系统找不到相应的型号:"+productId+",请联系系统管理员创建并映射对应型号");
				} else{
					eo.setMessage(exception.getLocalizedMessage());
				}
				logger.log(exception.getLocalizedMessage());
				exception.printStackTrace();
				return eo;
			}

		}
	}

	/**
	 * @param epm
	 * @throws WTException
	 */
	private void changeNumberAndName(EPMDocument epm) throws WTException {
		String name = getElementValue("name");
		if (!epm.getNumber().equals(this.number) || !epm.getName().equals(name)) {
			try {
				changeNumber(epm, this.number, name);
			} catch (WTPropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void setEPMApplicationContext(IxbElement ixbelement) throws WTException {
		String s = getElementValue(ixbelement, "ownerApplication");
		if(s == null) {
			s = "EPM";
		}
		EPMApplicationType epmapplicationtype = s != null ? EPMApplicationType.toEPMApplicationType(s) : EPMApplicationType.getEPMApplicationTypeDefault();
		EPMApplicationType epmapplicationtype1 = EPMContextHelper.getApplication();
		if (!epmapplicationtype.equals(epmapplicationtype1))
			try {
				EPMContextHelper.setApplication(epmapplicationtype);
			} catch (WTPropertyVetoException wtpropertyvetoexception) {
				throw new WTException(wtpropertyvetoexception);
			}
	}

	private EPMDocument importEPMExtentsAttribute(Object obj, IxbElement ixbelement) throws WTException {
		EPMDocument epmdocument = (EPMDocument) obj;
		try {
			Boolean boolean1 = Boolean.valueOf("false");
			if (boolean1 == null)
				return epmdocument;
			boolean flag = boolean1.booleanValue();
			EPMBoxExtents epmboxextents = EPMBoxExtents.newEPMBoxExtents();
			Double double1 = ixbelement.getDoubleValue("epmBoxExtents/Ax");
			if(double1 == null) {
				double1 = Double.valueOf("-0.05252911836241437");
			}
			Double double2 = ixbelement.getDoubleValue("epmBoxExtents/Ay");
			if(double2 == null) {
				double2 = Double.valueOf("-0.0");
			}
			Double double3 = ixbelement.getDoubleValue("epmBoxExtents/Az");
			if(double3 == null) {
				double3 = Double.valueOf("-6.204508726446682E-4");
			}
			Vector vector = new Vector(3);
			vector.addElement(double1);
			vector.addElement(double2);
			vector.addElement(double3);
			epmboxextents.setAxyz(vector);
			Double double4 = ixbelement.getDoubleValue("epmBoxExtents/Bx");
			if(double4 == null) {
				double4 = Double.valueOf("0.05252911836241437");
			}
			Double double5 = ixbelement.getDoubleValue("epmBoxExtents/By");
			if(double5 == null) {
				double5 = Double.valueOf("0.12353312906818652");
			}
			Double double6 = ixbelement.getDoubleValue("epmBoxExtents/Bz");
			if(double6 == null) {
				double6 = Double.valueOf("0.029620450872644668");
			}
			Vector vector1 = new Vector(3);
			vector1.addElement(double4);
			vector1.addElement(double5);
			vector1.addElement(double6);
			epmboxextents.setBxyz(vector1);
			epmdocument.setExtentsValid(flag);
			epmdocument.setBoxExtents(epmboxextents);
		} catch (Exception e) {
			logger.log("Exception in importEPMExtentsAttribute, fname=<" + getPfilename() + ">");
			processException(e);
		}
		return epmdocument;
	}

	private Object importEPMTypeDefinitionAttribute(Object obj, IxbElement ixbelement) throws WTException {
		IxbElement ixbelement1 = ixbelement;
		if ((obj instanceof EPMDocumentMaster))
			ixbelement1 = ixbelement.getElement("masterIba");
		return importTypeDefinitionAttribute(obj, ixbelement, ixbelement1);
	}

	private void importMasterIBAs(IxbElement ixbelement, EPMDocumentMaster epmdocumentmaster, IxbElement ixbelement1) throws WTException {
		if (ixbelement != null)
			epmdocumentmaster = (EPMDocumentMaster) importIBAAttribute(epmdocumentmaster, ixbelement);
		epmdocumentmaster = (EPMDocumentMaster) importEPMTypeDefinitionAttribute(epmdocumentmaster, ixbelement1);
	}

	private EPMDocument importEPMDocumentMasterAttributes(EPMDocument epmdocument, IxbElement ixbelement) throws WTException {
		try {
			EPMDocumentMaster epmdocumentmaster = (EPMDocumentMaster) epmdocument.getMaster();
			IxbElement ixbelement1 = ixbelement.getElement("masterIba");
			importMasterIBAs(ixbelement1, epmdocumentmaster, ixbelement);
			String s = getElementValue(ixbelement, "epmDocType");
			if(s == null) {
				s = "CADCOMPONENT";
			}
			String s1 = getElementValue(ixbelement, "epmDocSubType");
			if ((s != null) && (!s.equals(epmdocument.getDocType().toString()))) {
				Object[] aobj = new Object[0];
				throw new LogHelper.IxbException("wt.ixb.publicforhandlers.ixbResource", "111", aobj);
			}
			if ((s1 != null) && (!s1.equals(epmdocument.getDocSubType().toString())))
				epmdocument.setDocSubType(EPMDocSubType.toEPMDocSubType(s1));
			String s2 = getElementValue(ixbelement, "authoringApplication");
			if(s2 == null) {
				s2 = "PROE";
			}
			String s3 = getElementValue(ixbelement, "ownerApplication");
			if(s3 == null) {
				s3 = "EPM";
			}
			if (s2 != null) {
				if (epmdocumentmaster.getAuthoringApplication() != null) {
					if (!s2.equals(epmdocumentmaster.getAuthoringApplication().toString())) {
						Object[] aobj1 = new Object[0];
						throw new LogHelper.IxbException("wt.ixb.publicforhandlers.ixbResource", "111", aobj1);
					}
				} else
					epmdocumentmaster.setAuthoringApplication(EPMAuthoringAppType.toEPMAuthoringAppType(s2));
			}
			if (s3 != null) {
				if (epmdocumentmaster.getOwnerApplication() != null) {
					if (!s3.equals(epmdocumentmaster.getOwnerApplication().toString())) {
						Object[] aobj2 = new Object[0];
						throw new LogHelper.IxbException("wt.ixb.publicforhandlers.ixbResource", "111", aobj2);
					}
				} else
					epmdocumentmaster.setOwnerApplication(EPMApplicationType.toEPMApplicationType(s3));
			}
			String s4 = getElementValue(ixbelement, "defaultUnit");
			if(s4 == null) {
				s4 = "ea";
			}
			if (s4 != null)
				epmdocumentmaster.setDefaultUnit(QuantityUnit.toQuantityUnit(s4));
		} catch (Exception e) {
			logger.log("Exception in importEPMDocumentMasterAttributes, fname=<" + getPfilename() + ">");
			processException(e);
		}
		return epmdocument;
	}

	private EPMDocument importEPMDocumentAttributes(Object obj, IxbElement ixbelement) throws WTException {
		EPMDocument epmdocument = (EPMDocument) obj;
		try {
			String s = getElementValue(ixbelement, "description");
			if (s != null)
				epmdocument.setDescription(s);
			String s3 = getElementValue(ixbelement, "authoringApplication");
			if(s3 == null) {
				s3 = "PROE";
			}
			String s1 = getElementValue(ixbelement, "authoringApplicationVersion/versionNumber");
			String s5 = getElementValue(ixbelement, "authoringApplicationVersion/versionName");
			Object obj1 = null;
			boolean flag = true;
			if (s1 != null && s5 != null) {
				int i = Integer.parseInt(s1);
				obj1 = AuthoringAppVersionHelper.getAuthoringAppVersion(EPMAuthoringAppType.toEPMAuthoringAppType(s3), i, s5);
			} else {
				flag = false;
			}
			if (flag)
				epmdocument.setAuthoringAppVersion((EPMAuthoringAppVersion) obj1);
			String s2 = getElementValue(ixbelement, "dbKeySize");
			if (s2 != null)
				epmdocument.setDbKeySize(Integer.parseInt(s2));
			String s4 = getElementValue(ixbelement, "isVerified");
			if (s4 != null)
				epmdocument.setVerified(Boolean.valueOf(s4).booleanValue());
			String s6 = getElementValue(ixbelement, "revisionNumber");
			if (s6 != null)
				epmdocument.setRevisionNumber(Integer.parseInt(s6));
			obj1 = getElementValue(ixbelement, "familyTableStatus");
			if (obj1 != null)
				epmdocument.setFamilyTableStatus(Integer.parseInt((String) obj1));
			String s7 = getElementValue(ixbelement, "derived");
			if (s7 != null)
				epmdocument.setDerived(Boolean.valueOf(s7).booleanValue());
		} catch (Exception e) {
			logger.log("Exception in importEPMDocumentAttributes, fname=<" + getPfilename() + ">");
			processException(e);
		}
		return epmdocument;
	}

	private EPMDocument importEPMCADReferenceControlAttribute(Object obj, IxbElement ixbelement) throws WTException {
		try {
			if (getElementValue(ixbelement, "epmCADReferenceControl") == null)
				return (EPMDocument) obj;
			EPMCADReferenceControl epmcadreferencecontrol = ((EPMDocument) obj).getReferenceControl();
			if (epmcadreferencecontrol == null) {
				String s = getElementValue(ixbelement, "epmCADReferenceControl/geomRestr");
				if(s == null) {
					s = "5";
				}
				int i = Integer.parseInt(s);
				s = getElementValue(ixbelement, "epmCADReferenceControl/isGeomRestrRecursive");
				if(s == null) {
					s = "false";
				}
				boolean flag1 = Boolean.valueOf(s).booleanValue();
				s = getElementValue(ixbelement, "epmCADReferenceControl/scope");
				if(s == null) {
					s = "0";
				}
				int i1 = Integer.parseInt(s);
				s = getElementValue(ixbelement, "epmCADReferenceControl/violRestriction");
				if(s == null) {
					s = "0";
				}
				int j1 = Integer.parseInt(s);
				epmcadreferencecontrol = EPMCADReferenceControl.newEPMCADReferenceControl(i, flag1, i1, j1);
				((EPMDocument) obj).setReferenceControl(epmcadreferencecontrol);
			} else {
				String s1 = getElementValue(ixbelement, "epmCADReferenceControl/geomRestr");
				if(s1 == null) {
					s1 = "5";
				}
				if (s1 != null) {
					int j = Integer.parseInt(s1);
					epmcadreferencecontrol.setGeomRestr(j);
				}
				s1 = getElementValue(ixbelement, "epmCADReferenceControl/isGeomRestrRecursive");
				if(s1 == null) {
					s1 = "false";
				}
				if (s1 != null) {
					boolean flag = Boolean.valueOf(s1).booleanValue();
					epmcadreferencecontrol.setGeomRestrRecursive(flag);
				}
				s1 = getElementValue(ixbelement, "epmCADReferenceControl/scope");
				if(s1 == null) {
					s1 = "0";
				}
				if (s1 != null) {
					int k = Integer.parseInt(s1);
					epmcadreferencecontrol.setScope(k);
				}
				s1 = getElementValue(ixbelement, "epmCADReferenceControl/violRestriction");
				if(s1 == null) {
					s1 = "0";
				}
				if (s1 != null) {
					int l = Integer.parseInt(s1);
					epmcadreferencecontrol.setViolRestriction(l);
				}
			}
		} catch (Exception e) {
			logger.log("Exception in importEPMCADReferenceControlAttribute, fname=<" + getPfilename() + ">");
			processException(e);
		}
		return (EPMDocument) obj;
	}

	private EPMDocument importEPMParameterMapAttribute(Object obj, IxbElement ixbelement) throws WTException {
		Transaction tx = null;
		MethodContext methodcontext = MethodContext.getContext();
		try {
			IBAHolder ibaholder = (IBAHolder) obj;
			Enumeration enumeration = ixbelement.getElements("EPMParameterMap");
			if ((enumeration == null) || (!enumeration.hasMoreElements()))
				return (EPMDocument) ibaholder;
			tx = new Transaction();
			tx.start();
			ibaholder = (IBAHolder) PersistenceHelper.manager.lockAndRefresh((Persistable) ibaholder);
			IBAHolderReference ibaholderreference = IBAHolderReference.newIBAHolderReference(ibaholder);
			WTArrayList wtarraylist = new WTArrayList();
			WTArrayList wtarraylist1 = new WTArrayList();
			wtarraylist1.add(ibaholder);

			WTArrayList wtarraylist2 = new WTArrayList();
			QueryResult queryResult = EPMQueryHelper.lookupEPMParameterUnitInfoAndParameterMap(wtarraylist1);
			while (queryResult.hasMoreElements()) {
				Object object = queryResult.nextElement();
				if (object instanceof EPMParameterMap) {
					EPMParameterMap epmp = (EPMParameterMap) object;
					AttributeDefinitionReference reference = epmp.getDefinitionReference();
					wtarraylist2.add(reference);
				}
			}

			while (enumeration.hasMoreElements()) {
				IxbElement ixbelement1 = (IxbElement) enumeration.nextElement();
				String s = getElementValue(ixbelement1, "extPath");
				s = getPropertiesValue(s);
				String s1 = getElementValue(ixbelement1, "parameterName");
				if ((s != null) && (s1 != null)) {
					AttributeDefDefaultView attributedefdefaultview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(s);
					AttributeDefinitionReference attributedefinitionreference = IBADefinitionObjectsFactory.newAttributeDefinitionReference(attributedefdefaultview);
					if (wtarraylist2 != null && attributedefinitionreference != null && wtarraylist2.contains(attributedefinitionreference)) {
						continue;
					}
					if (attributedefinitionreference != null)
						wtarraylist.add(EPMParameterMap.newEPMParameterMap(ibaholderreference, attributedefinitionreference, s1));
				}
			}
			if (!wtarraylist.isEmpty()) {
				methodcontext.put("ixb_store_object_context/key", ibaholder);
				// WTArrayList wtarraylist2 = new WTArrayList(1);
				// wtarraylist2.add(ibaholder);
				Transaction.getGlobalMap().put(StandardVersionControlService.SVCS_SIGNAL_PERSISTENCE_OF_IGNORE_KEY, true);

				WTHashSet wthashset = new WTHashSet();
				wthashset.add(ibaholder);
				Transaction.getGlobalMap().put("PREVENT_OWNERSHIP_CHECK", wthashset);
				PersistenceHelper.manager.store(wtarraylist);
				methodcontext.remove("ixb_store_object_context/key");
			}
			tx.commit();
			tx = null;
			obj = PersistenceHelper.manager.refresh((Persistable) ibaholder);
		} catch (Exception e) {
			logger.log("Exception in importEPMParameterMapAttribute, fname=<" + getPfilename() + ">");
			processException(e);
		} finally {
			if (tx != null)
				tx.rollback();
			methodcontext.remove("ixb_store_object_context/key");
		}
		return (EPMDocument) obj;
	}

	private EPMDocument importEPMFeatureValueAttribute(Object obj, IxbElement ixbelement) throws WTException {
		Transaction tx = new Transaction();
		try {
			tx.start();
			EPMDocument epmdocument = (EPMDocument) obj;
			epmdocument = (EPMDocument) PersistenceHelper.manager.lockAndRefresh(epmdocument);
			Enumeration enumeration = ixbelement.getElements("EPMFeatureValue");
			Map map = epmdocument.getFeatureValues();
			if ((enumeration == null) || (!enumeration.hasMoreElements())) {
				if ((map != null) && (map.size() > 0)) {
					WTHashSet wthashset = new WTHashSet(map.values());
					PersistenceHelper.manager.delete(wthashset);
				}
			}
			WTArrayList wtarraylist = new WTArrayList();

			while (enumeration.hasMoreElements()) {
				IxbElement ixbelement1 = (IxbElement) enumeration.nextElement();
				String s = getElementValue(ixbelement1, "valueClassname");
				if (s != null) {
					String s1 = getElementValue(ixbelement1, "definitionName");
					s1 = getPropertiesValue(s1);
					String s2 = getElementValue(ixbelement1, "valueString");
					Object obj1 = EPMHndHelper.constructObject(s, s2);
					EPMFeatureValue epmfeaturevalue = EPMFeatureValue.newEPMFeatureValue(s1, obj1);
					epmfeaturevalue.setContainer(epmdocument);
					wtarraylist.add(epmfeaturevalue);
				}
			}
			if (!wtarraylist.isEmpty())
				PersistenceServerHelper.manager.insert(wtarraylist);
			tx.commit();
			tx = null;
			obj = PersistenceHelper.manager.refresh(epmdocument);
		} catch (Exception e) {
			logger.log("Exception in importEPMFeatureValueAttribute, fname=<" + getPfilename() + ">");
			processException(e);
		} finally {
			if (tx != null)
				tx.rollback();
		}
		return (EPMDocument) obj;
	}

	private EPMDocument importEPMParameterValueAttribute(Object obj, IxbElement ixbelement) throws WTException {
		Transaction tx = new Transaction();
		try {
			tx.start();
			EPMDocument epmdocument = (EPMDocument) obj;
			epmdocument = (EPMDocument) PersistenceHelper.manager.lockAndRefresh(epmdocument);
			Enumeration enumeration = ixbelement.getElements("EPMParameterValue");
			Map map = epmdocument.getParameterValues();
			if ((enumeration != null) && enumeration.hasMoreElements()) {
				if ((map != null) && (map.size() > 0)) {
					WTHashSet wthashset = new WTHashSet(map.values());
					PersistenceServerHelper.manager.remove(wthashset);
				}
			}
			WTArrayList wtarraylist = new WTArrayList();

			while (enumeration.hasMoreElements()) {
				IxbElement ixbelement1 = (IxbElement) enumeration.nextElement();
				String s = getElementValue(ixbelement1, "definitionName");
				String s1 = getElementValue(ixbelement1, "valueClassname");
				String s2 = getElementValue(ixbelement1, "valueString");
				if (s1 != null) {
					Object obj1 = EPMHndHelper.constructObject(s1, s2);
					EPMParameterValue epmparametervalue = EPMParameterValue.newEPMParameterValue(s, obj1);
					epmparametervalue.setContainer(epmdocument);
					wtarraylist.add(epmparametervalue);
				}
			}
			if (!wtarraylist.isEmpty())
				// PersistenceHelper.manager.save(wtarraylist);
				PersistenceServerHelper.manager.insert(wtarraylist);
			// PersistenceHelper.manager.store(wtarraylist);
			tx.commit();
			tx = null;
			obj = PersistenceHelper.manager.refresh(epmdocument);
		} catch (Exception e) {
			logger.log("Exception in importEPMParameterValueAttribute, fname=<" + getPfilename() + ">");
			processException(e);
		} finally {
			if (tx != null)
				tx.rollback();
		}
		return (EPMDocument) obj;
	}

	private EPMDocument createNewObject() throws WTException {
		EPMDocument epm = null;
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
			String containerRefName = "";
			if (wtcontainerref == null) {
				// /wt.inf.container.OrgContainer=sast/wt.inf.library.WTLibrary=标准紧固件库
				String objectContainerPath = getElementValue(root, "objectContainerPath");
				if ("/".equals(objectContainerPath)) {
					objectContainerPath = "/wt.inf.container.OrgContainer=sast/wt.inf.library.WTLibrary=八院标准紧固件库";
				}
				String tempPath = objectContainerPath.substring(objectContainerPath.indexOf("=") + 1);
				String clazz = tempPath.substring(tempPath.indexOf("/") + 1, tempPath.indexOf("="));
				containerRefName = tempPath.substring(tempPath.indexOf("=") + 1);
				wtcontainerref = impHdl.getWTContainerRef(Class.forName(clazz), containerRefName);
			}
			if (wtcontainerref == null) {
				throw new WTException(containerRefName + "产品库不存在；");
			}
			String s2 = getElementValue(this.root, "docType");
			if("DRAWING".equals(s2)) {
				s2 = "CADDRAWING";
			}else if("ASM".equals(s2)) {
				s2 = "CADASSEMBLY";
			}else{
				s2 = "CADCOMPONENT";
			}
			String s3 = getElementValue(this.root, "epmDocSubType");
			String s4 = getElementValue(this.root, "authoringApplication");
			if(s4 == null) {
				s4 = "PROE";
			}
			String s5 = getElementValue(this.root, "cadName");
			if (this.impHdl.isLoopTest())
				s5 = this.impHdl.getLoopTestPrefix() + s5;
			EPMDocumentType epmdocumenttype = EPMDocumentType.toEPMDocumentType(s2);
			EPMDocSubType epmdocsubtype = EPMDocSubType.toEPMDocSubType(s3);
			EPMAuthoringAppType epmauthoringapptype = EPMAuthoringAppType.toEPMAuthoringAppType(s4);
			if (s5 == null)
				s5 = new String(this.number);
			setEPMApplicationContext(this.root);

			String epmname = this.iname;
			if (this.impHdl.isLoopTest())
				epmname = this.impHdl.getLoopTestPrefix() + epmname;
			tx.start();
			epm = EPMDocument.newEPMDocument(this.number, epmname, epmauthoringapptype, epmdocumenttype, s5);
			epm.setDocSubType(epmdocsubtype);
			epm = (EPMDocument) importLifecycleAttribute(epm, this.root);
			epm = (EPMDocument) importVersionAttribute(epm, this.root);
			epm = (EPMDocument) importDomainFolderAttribute(epm, this.root);

			epm = importEPMDocumentMasterAttributes(epm, this.root);
			epm = importEPMDocumentAttributes(epm, this.root);
			epm = importEPMCADReferenceControlAttribute(epm, this.root);
			epm = importEPMExtentsAttribute(epm, this.root);

			epm = (EPMDocument) importTypeDefinitionAttribute(epm, this.root, this.root);

			Boolean boolean1 = this.root.getBooleanValue("isMissingDependents");
			if (boolean1 != null) {
				epm.setMissingDependents(boolean1.booleanValue());
			}
			epm.setContainerReference(wtcontainerref);
			((WTContained) epm.getMaster()).setContainerReference(wtcontainerref);
			Mastered mastered = epm.getMaster();
			PersistenceHelper.manager.save(mastered);
			methodcontext.put("ixb_store_object_context/key", epm);
			WTArrayList wtarraylist = new WTArrayList(1);
			wtarraylist.add(epm);
			Transaction.getGlobalMap().put(StandardVersionControlService.SVCS_SIGNAL_PERSISTENCE_OF_IGNORE_KEY, true);
			Transaction.getGlobalMap().put(MultiObjIBAValueDBService.DO_NOT_COPY_FORWARD_IBAS_KEY, wtarraylist);
			WTHashSet wthashset = new WTHashSet();
			wthashset.add(epm);
			Transaction.getGlobalMap().put("PREVENT_OWNERSHIP_CHECK", wthashset);
			epm = (EPMDocument) PersistenceServerHelper.manager.store(epm, createStamp, modifyStamp);
			methodcontext.remove("ixb_store_object_context/key");
			tx.commit();
			tx = null;
			epm = (EPMDocument) importIBAAttribute(epm, this.root);
			epm = (EPMDocument) importContentItemAttribute(epm, this.root);
			epm = importEPMParameterMapAttribute(epm, this.root);
			epm = importEPMFeatureValueAttribute(epm, this.root);
			epm = importEPMParameterValueAttribute(epm, this.root);
			epm = (EPMDocument) importRepresentationAttribute(epm, this.root);
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
		return epm;
	}

	private EPMDocument createNewVersion(EPMDocument epm) throws WTException {
		EPMDocument newepm = null;
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
			else {
				modifyStamp = new Timestamp(nowtime);
			}
			tx.start();
			if (!VersionControlHelper.isLatestIteration(epm)) {
				epm = (EPMDocument) VersionControlHelper.getLatestIteration(epm);
			}
			String versionInfo = getElementValue(this.root, "versionInfo");
			versionInfo = MQExpImpUtil.attrConvertValue("versionInfo", versionInfo, "IMP");
			//versionId = getPropertiesValue(versionId);
			String iterationId = getElementValue(this.root, "iterationInfo");

			newepm = (EPMDocument) VersionControlHelper.service.newVersionable(epm);
			Series se = VersionControlHelper.getVersionIdentifier(epm).getSeries();
			se.setValueWithoutValidating(versionInfo);
			VersionIdentifier vi = VersionIdentifier.newVersionIdentifier((MultilevelSeries) se);
			Series series = epm.getIterationInfo().getIdentifier().getSeries();
			series.setValueWithoutValidating(iterationId);
			IterationIdentifier ii = IterationIdentifier.newIterationIdentifier(series);
			newepm = (EPMDocument) VersionControlHelper.service.newVersion(epm, true);
			VersionControlHelper.setIterationIdentifier(newepm, ii);
			VersionControlHelper.setVersionIdentifier(newepm, vi, false);
			FolderHelper.assignLocation(newepm, FolderHelper.service.getFolder(epm));
			newepm.setContainerReference(epm.getContainerReference());
			newepm = (EPMDocument) importLifecycleAttribute(newepm);
			newepm = importEPMDocumentAttributes(newepm, this.root);
			newepm = importEPMCADReferenceControlAttribute(newepm, this.root);
			newepm = importEPMExtentsAttribute(newepm, this.root);
			newepm = (EPMDocument) importTypeDefinitionAttribute(newepm, this.root, this.root);
			Boolean boolean1 = this.root.getBooleanValue("isMissingDependents");
			if (boolean1 != null)
				newepm.setMissingDependents(boolean1.booleanValue());
			methodcontext.put("ixb_store_object_context/key", newepm);
			WTArrayList wtarraylist = new WTArrayList(1);
			wtarraylist.add(newepm);
			Transaction.getGlobalMap().put(StandardVersionControlService.SVCS_SIGNAL_PERSISTENCE_OF_IGNORE_KEY, true);
			Transaction.getGlobalMap().put(MultiObjIBAValueDBService.DO_NOT_COPY_FORWARD_IBAS_KEY, wtarraylist);
			WTHashSet wthashset = new WTHashSet();
			wthashset.add(newepm);
			Transaction.getGlobalMap().put("PREVENT_OWNERSHIP_CHECK", wthashset);
			newepm = (EPMDocument) VersionControlHelper.service.insertNode(newepm, null, null);
			if (PersistenceHelper.isPersistent(newepm))
				newepm = (EPMDocument) PersistenceHelper.manager.save(newepm);
			else
				newepm = (EPMDocument) PersistenceServerHelper.manager.store(newepm, createStamp, modifyStamp);
			methodcontext.remove("ixb_store_object_context/key");
			tx.commit();
			tx = null;
			if (newepm.isGeneric() || newepm.isInstance()) {
				/*
				 * EPMFamily family= EPMFamily.getEPMFamily(newepm);
				 * EPMSepFamilyTable table =
				 * (EPMSepFamilyTable)family.getFamilyTable(); WTSet wtset =
				 * CmExpImpSearchHelper.searchAllEPMContainedIn(table);
				 * PersistenceServerHelper.manager.remove(wtset);
				 * PersistenceHelper.manager.delete(table);
				 */
			}
			newepm = (EPMDocument) importIBAAttribute(newepm, this.root);
			newepm = (EPMDocument) importContentItemAttribute(newepm, this.root);
			newepm = importEPMParameterMapAttribute(newepm, this.root);
			newepm = importEPMFeatureValueAttribute(newepm, this.root);
			newepm = importEPMParameterValueAttribute(newepm, this.root);
			newepm = (EPMDocument) importRepresentationAttribute(newepm, this.root);
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
		return newepm;
	}

	private EPMDocument createNewIteration_bak(EPMDocument epm) throws WTException {
		EPMDocument newepm = null;
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

			String s2 = getElementValue(this.root, "epmDocType");
			if(s2 == null) {
				s2 = "CADCOMPONENT";
			}
			String s3 = getElementValue(this.root, "epmDocSubType");
			String s4 = getElementValue(this.root, "authoringApplication");
			if(s4 == null) {
				s4 = "PROE";
			}
			String s5 = getElementValue(this.root, "cadName");
			EPMDocumentType epmdocumenttype = EPMDocumentType.toEPMDocumentType(s2);
			EPMDocSubType epmdocsubtype = EPMDocSubType.toEPMDocSubType(s3);
			EPMAuthoringAppType epmauthoringapptype = EPMAuthoringAppType.toEPMAuthoringAppType(s4);
			if (s5 == null)
				s5 = new String(this.number);
			setEPMApplicationContext(this.root);

			String epmname = this.iname;
			if (this.impHdl.isLoopTest())
				epmname = this.impHdl.getLoopTestPrefix() + epmname;
			tx.start();
			// newepm = EPMDocument.newEPMDocument(this.number, epmname,
			// epmauthoringapptype, epmdocumenttype, s5);
			newepm.setNumber(this.number);
			newepm.setName(epmname);
			// newepm.set
			newepm.setCADName(s5);
			newepm.setDocType(epmdocumenttype);
			newepm.setMaster(epm.getMaster());
			newepm.setDocSubType(epmdocsubtype);
			newepm = (EPMDocument) importLifecycleAttribute(newepm, this.root);
			newepm = (EPMDocument) importVersionAttribute(newepm, this.root);
			newepm = (EPMDocument) importDomainFolderAttribute(newepm, this.root);

			newepm = importEPMDocumentMasterAttributes(newepm, this.root);
			newepm = importEPMDocumentAttributes(newepm, this.root);
			newepm = importEPMCADReferenceControlAttribute(newepm, this.root);
			newepm = importEPMExtentsAttribute(newepm, this.root);

			newepm = (EPMDocument) importTypeDefinitionAttribute(newepm, this.root, this.root);

			Boolean boolean1 = this.root.getBooleanValue("isMissingDependents");
			if (boolean1 != null) {
				newepm.setMissingDependents(boolean1.booleanValue());
			}
			String iterationId = getElementValue("versionInfo/iterationId");
			Series series = epm.getIterationInfo().getIdentifier().getSeries();
			series.setValueWithoutValidating(iterationId);
			IterationIdentifier ii = IterationIdentifier.newIterationIdentifier(series);
			VersionControlHelper.setIterationIdentifier(newepm, ii);
			VersionControlServerHelper.setBranchIdentifier(newepm, VersionControlHelper.getBranchIdentifier(epm));
			newepm.setControlBranch(VersionControlServerHelper.getControlBranch(epm));
			newepm.setContainerReference(wtcontainerref);
			newepm = (EPMDocument) VersionControlHelper.service.insertIteration(newepm);
			methodcontext.put("ixb_store_object_context/key", newepm.getMaster());
			WTArrayList wtarraylist = new WTArrayList(1);
			wtarraylist.add(newepm);
			Transaction.getGlobalMap().put(StandardVersionControlService.SVCS_SIGNAL_PERSISTENCE_OF_IGNORE_KEY, true);
			Transaction.getGlobalMap().put(MultiObjIBAValueDBService.DO_NOT_COPY_FORWARD_IBAS_KEY, wtarraylist);
			WTHashSet wthashset = new WTHashSet();
			wthashset.add(newepm);
			Transaction.getGlobalMap().put("PREVENT_OWNERSHIP_CHECK", wthashset);
			methodcontext.remove("ixb_store_object_context/key");
			tx.commit();
			tx = null;
			newepm = (EPMDocument) importIBAAttribute(newepm, this.root);
			newepm = (EPMDocument) importContentItemAttribute(newepm, this.root);
			newepm = importEPMParameterMapAttribute(newepm, this.root);
			newepm = importEPMFeatureValueAttribute(newepm, this.root);
			newepm = importEPMParameterValueAttribute(newepm, this.root);
			newepm = (EPMDocument) importRepresentationAttribute(newepm, this.root);
		} catch (Exception e) {
			if ((e instanceof WTException))
				throw ((WTException) e);
			throw new WTException(e);
		} finally {
			if (tx != null)
				tx.rollback();
			methodcontext.remove("ixb_store_object_context/key");
		}
		return newepm;
	}

	private EPMDocument createNewIteration(EPMDocument epm) throws WTException {
		EPMDocument newepm = null;
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
			else {
				modifyStamp = new Timestamp(nowtime);
			}
			tx.start();
			newepm = (EPMDocument) VersionControlHelper.service.newIteration(epm, true);
			String iterationId = getElementValue("iterationInfo");
			Series series = epm.getIterationInfo().getIdentifier().getSeries();
			series.setValueWithoutValidating(iterationId);
			IterationIdentifier ii = IterationIdentifier.newIterationIdentifier(series);
			VersionControlHelper.setIterationIdentifier(newepm, ii);
			VersionControlServerHelper.setBranchIdentifier(newepm, VersionControlHelper.getBranchIdentifier(epm));
			newepm.setControlBranch(VersionControlServerHelper.getControlBranch(epm));
			newepm.setContainerReference(epm.getContainerReference());
			FolderHelper.assignLocation(newepm, FolderHelper.service.getFolder(epm));
			newepm = (EPMDocument) importLifecycleAttribute(newepm);
			newepm = (EPMDocument) VersionControlHelper.service.insertIteration(newepm);
			newepm = importEPMDocumentAttributes(newepm, this.root);
			newepm = importEPMCADReferenceControlAttribute(newepm, this.root);
			newepm = importEPMExtentsAttribute(newepm, this.root);

			newepm = (EPMDocument) importTypeDefinitionAttribute(newepm, this.root, this.root);
			newepm.setContainerReference(epm.getContainerReference());

			Boolean boolean1 = getBooleanValue("isMissingDependents");
			if (boolean1 != null) {
				newepm.setMissingDependents(boolean1.booleanValue());
			}
			methodcontext.put("ixb_store_object_context/key", newepm);
			WTArrayList wtarraylist = new WTArrayList(1);
			wtarraylist.add(newepm);
			Transaction.getGlobalMap().put(StandardVersionControlService.SVCS_SIGNAL_PERSISTENCE_OF_IGNORE_KEY, true);
			Transaction.getGlobalMap().put(MultiObjIBAValueDBService.DO_NOT_COPY_FORWARD_IBAS_KEY, wtarraylist);
			WTHashSet wthashset = new WTHashSet();
			wthashset.add(newepm);
			Transaction.getGlobalMap().put("PREVENT_OWNERSHIP_CHECK", wthashset);
			if (PersistenceHelper.isPersistent(newepm))
				newepm = (EPMDocument) PersistenceHelper.manager.save(newepm);
			else {
				newepm = (EPMDocument) PersistenceServerHelper.manager.store(newepm, createStamp, modifyStamp);
			}
			tx.commit();
			tx = null;
			if (newepm.isGeneric() || newepm.isInstance()) {
				/*
				 * EPMFamily family= EPMFamily.getEPMFamily(newepm);
				 * EPMSepFamilyTable table =
				 * (EPMSepFamilyTable)family.getFamilyTable(); WTSet wtset =
				 * CmExpImpSearchHelper.searchAllEPMContainedIn(table);
				 * PersistenceServerHelper.manager.remove(wtset);
				 * VersionControlHelper.service.deleteIterations(table, table);
				 */
			}

			newepm = (EPMDocument) importIBAAttribute(newepm, this.root);
			newepm = (EPMDocument) importContentItemAttribute(newepm, this.root);
			newepm = importEPMParameterMapAttribute(newepm, this.root);
			newepm = importEPMFeatureValueAttribute(newepm, this.root);
			newepm = importEPMParameterValueAttribute(newepm, this.root);
			newepm = (EPMDocument) importRepresentationAttribute(newepm, this.root);
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
		return newepm;
	}

	private ContentHolder importPDFContentItem(EPMDocument obj, IxbElement ixbelement) throws WTException {
		ContentHolder contentholder = (ContentHolder) obj;
		Transaction tx = null;
		try {
			Enumeration enumeration = ixbelement.getElements("contentItem");
			if ((enumeration == null) || (!enumeration.hasMoreElements()))
				return contentholder;
			tx = new Transaction();
			tx.start();
			contentholder = (ContentHolder) PersistenceHelper.manager.lockAndRefresh(contentholder);
			boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
			ContentHolder holder = ContentHelper.service.getContents(contentholder);
			Vector vector = ContentHelper.getContentList(holder);
			List<String> existFiles = new ArrayList<String>();
			if (vector != null) {
				for (int i = 0; i < vector.size(); i++) {
					ContentItem contentitem1 = (ContentItem) vector.elementAt(i);
					if (contentitem1 instanceof ApplicationData) {
						ApplicationData data = (ApplicationData) contentitem1;
						if (data.getFileName().startsWith("Print_")) {
							existFiles.add(data.getFileName());
						}
					}

				}
			}
			while (enumeration.hasMoreElements()) {
				IxbElement ixbelement2 = (IxbElement) enumeration.nextElement();
				String s1 = getElementValue(ixbelement2, "contentType");
				String fileName = getElementValue(ixbelement2, "fileName");
				if (s1.equals("ApplicationData") && fileName.startsWith("Print_") && !existFiles.contains(fileName))
					importPDFAppDataContent(contentholder, ixbelement2);
				else
					logger.log("importContentHolder: unknown type of content item:<" + s1 + ">");
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
				applicationdata = ContentServerHelper.service.updateContent(contentholder, applicationdata, inputstream);
		} catch (WTPropertyVetoException wtpropertyvetoexception) {
			throw new WTException(wtpropertyvetoexception);
		} catch (PropertyVetoException propertyvetoexception) {
			throw new WTException(propertyvetoexception);
		} catch (Exception exception) {
			throw new WTException(exception);
		}
	}
}