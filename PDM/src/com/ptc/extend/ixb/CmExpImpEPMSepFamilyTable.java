package com.ptc.extend.ixb;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import wt.epm.EPMApplicationType;
import wt.epm.EPMAuthoringAppType;
import wt.epm.EPMContextHelper;
import wt.epm.familytable.EPMFamilyTableAttribute;
import wt.epm.familytable.EPMFamilyTableFeature;
import wt.epm.familytable.EPMFamilyTableMember;
import wt.epm.familytable.EPMFamilyTableParameter;
import wt.epm.familytable.EPMFamilyTableReference;
import wt.epm.familytable.EPMFeatureDefinition;
import wt.epm.familytable.EPMParameterDefinition;
import wt.epm.familytable.EPMSepFamilyTable;
import wt.epm.familytable.EPMSepFamilyTableMaster;
import wt.facade.ixb.IxbElement;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.folder.FolderHelper;
import wt.inf.container.ContainerSpec;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.ixb.epm.handlers.EPMHndHelper;
import wt.ixb.publicforhandlers.IxbHndHelper;
import wt.method.MethodContext;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.series.MultilevelSeries;
import wt.series.Series;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.IterationIdentifier;
import wt.vc.VersionControlHelper;
import wt.vc.VersionControlServerHelper;
import wt.vc.VersionIdentifier;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.WorkInProgressState;

public class CmExpImpEPMSepFamilyTable extends CmExpImpPersistable {

	public CmExpImpEPMSepFamilyTable(CmExporter expHdl) throws WTException {
		super(expHdl);
	}

	public CmExpImpEPMSepFamilyTable(CmImporter impHdl, String fname)
			throws WTException {
		super(impHdl, fname);
	}

	public String getRootTag() {
		return CmExpImpConstraints.XML_ESFAMILYTABLE;
	}

	public void exportObject(Object obj) throws WTException {

	}


	public Object importObject() throws WTException {
		Object obj = importAttribute();
		if (obj != null)
			this.impHdl.pubImportedObject(obj, getRemoteId());
		return obj;
	}

	public WTArrayList importObjects() throws WTException {
		return null;
	}

	public EPMSepFamilyTable importAttribute() throws WTException {

		try {
			if (!isTypeDefinitionImported()) {
				logger("==>WARNING:Import EPMSepFamilyTable number=<"
						+ this.number + "> version=" + this.version + "."
						+ this.iteration
						+ " TypeDefinition not imported, SKIP!");
				return null;
			}
			WTContainerRef wtcontainerref = getWTContainerRef(this.root);
			if (wtcontainerref!=null) {
            	this.impHdl.setWTContainerRef(wtcontainerref);
			}
			String version = getPropertiesValue(this.version);
			EPMSepFamilyTable newfamilytable=null;
			EPMSepFamilyTable table = (EPMSepFamilyTable) findAlreadyImportedVersionedObject(
					this.iname, version, this.iteration, wtcontainerref);
			if (table != null) {
				logger("==>The EPMSepFamilyTable name=<"+ this.iname +this.version +this.iteration + "> Is Already Exist");
				this.impHdl.putInExistedHashtable(getRemoteId(), table);
				return table;
			}

			table = (EPMSepFamilyTable) findAlreadyImportedVersionedObject(
					this.iname, version, null, wtcontainerref);
			if (table != null) {
				logger.log("==>Create new Iteration: EPMSepFamilyTable name=<" + this.iname + "> version=" + this.version
                        + "." + this.iteration);
				newfamilytable = createNewIteration(table);
			}else{
				table = (EPMSepFamilyTable) findAlreadyImportedVersionedObject(
						this.iname, null, null, wtcontainerref);
				if(table == null){
					newfamilytable = createNewObject();
				}else{
					newfamilytable =  createNewVersion(table);
				}
			}
			if (newfamilytable != null) {
				this.impHdl.putInNewCreatedHashtable(getRemoteId(),
						newfamilytable);
				HashMap hmap = buildOrignalInfo();
				this.impHdl.doOperationAfterStore(newfamilytable, hmap);
				logger("==>Import EPMSepFamilyTable number=<" + this.number
						+ "> version=" + this.version + "." + this.iteration
						+ " OK!");
			}
			return newfamilytable;
		} catch (Exception exception) {
			logger("***Exception in importAttribute, fname=<" + getPfilename()
					+ ">");
			processException(exception);
		}
		return null;

	}

	private Object importEPMFamilyTableColumnAttribute(Object obj,
			IxbElement ixbelement) throws WTException {
		try {
			EPMSepFamilyTable epmsepfamilytable = (EPMSepFamilyTable) obj;
			Enumeration enumeration = ixbelement
					.getElements("EPMFamilyTableColumn");
			if ((enumeration == null) || (!enumeration.hasMoreElements()))
				return obj;
			Map map = EPMHndHelper.getDefinitions(EPMFeatureDefinition.class,
					epmsepfamilytable);
			Map map1 = EPMHndHelper.getDefinitions(
					EPMParameterDefinition.class, epmsepfamilytable);



			Map map2 = EPMHndHelper.getFamilyTableColumns(epmsepfamilytable);
			WTArrayList obj1 = new WTArrayList();

			while (enumeration.hasMoreElements()) {
				IxbElement ixbelement1 = (IxbElement) enumeration.nextElement();
				String s = ixbelement1.getValue("ftColumnClassname");
				String s1 = ixbelement1.getValue("name");
				String s2 = ixbelement1.getValue("title");
				Integer integer = ixbelement1.getIntValue("columnType");
				Integer integer1 = ixbelement1.getIntValue("epmAttribute");
				Map map3 =(Map) map2.get(s);
				if ((s != null) && (s1 != null) && (s2 != null)
						&& (integer != null) && (integer1 != null)) {
					if (s.equals(EPMFamilyTableAttribute.class.getName())) {
						if(map3==null){
							Object obj3 = EPMFamilyTableAttribute
									.newEPMFamilyTableAttribute(s1, s2,
											integer.intValue(),
											integer1.intValue(), null,
											epmsepfamilytable);
							obj1.add((Persistable) obj3);
						}else{
							Object obj2 = map3.get(s1);
							if(obj2==null){
								Object obj3 = EPMFamilyTableAttribute
										.newEPMFamilyTableAttribute(s1, s2,
												integer.intValue(),
												integer1.intValue(), obj2,
												epmsepfamilytable);
								obj1.add((Persistable) obj3);
							}
						}

						continue;
					}
					if (s.equals(EPMFamilyTableFeature.class.getName())) {
						Object obj2 = ixbelement1.getValue("definitionName");
						if (obj2 == null)
							throw new WTException(
									"wt.epm.util.EPMResource",
									"164",
									new Object[] {
											s1,
											IxbHndHelper
													.getDisplayIdentityForIxb(epmsepfamilytable) });
						Object obj3 = (EPMFeatureDefinition) map.get(obj2);
						if (obj3 == null)
							throw new WTException("wt.epm.util.EPMResource",
									"161", new Object[] { s1, obj2 });
						Object obj4 = EPMFamilyTableFeature
								.newEPMFamilyTableFeature(s1, s2,
										integer.intValue(),
										integer1.intValue(),
										(EPMFeatureDefinition) obj3,
										epmsepfamilytable);
						obj1.add((Persistable) obj4);
						continue;
					}
					if (s.equals(EPMFamilyTableMember.class.getName())) {
						Object obj2 = EPMFamilyTableMember
								.newEPMFamilyTableMember(s1, s2,
										integer.intValue(),
										integer1.intValue(), epmsepfamilytable);
						obj1.add((Persistable) obj2);
						continue;
					}
					if (s.equals(EPMFamilyTableParameter.class.getName())) {
						Object obj2 = ixbelement1.getValue("definitionName");
						if (obj2 == null)
							throw new WTException(
									"wt.epm.util.EPMResource",
									"165",
									new Object[] {
											s1,
											IxbHndHelper
													.getDisplayIdentityForIxb(epmsepfamilytable) });
						Object obj3 = (EPMParameterDefinition) map1.get(obj2);
						if (obj3 == null)
							throw new WTException("wt.epm.util.EPMResource",
									"163", new Object[] { s1, obj2 });
						if (map3==null) {
							Object obj4 = EPMFamilyTableParameter
									.newEPMFamilyTableParameter(s1, s2,
											integer.intValue(),
											integer1.intValue(),
											(EPMParameterDefinition) obj3,
											epmsepfamilytable);
							obj1.add((Persistable) obj4);
						}else{
							Object obj5  =map3.get(s1);
							if(obj5==null){
								Object obj4 = EPMFamilyTableParameter
										.newEPMFamilyTableParameter(s1, s2,
												integer.intValue(),
												integer1.intValue(),
												(EPMParameterDefinition) obj3,
												epmsepfamilytable);
								obj1.add((Persistable) obj4);
							}

						}

						continue;
					}
					if (s.equals(EPMFamilyTableReference.class.getName())) {
						Object obj2 = EPMFamilyTableReference
								.newEPMFamilyTableReference(s1, s2,
										integer.intValue(),
										integer1.intValue(), epmsepfamilytable);
						obj1.add((Persistable) obj2);
					}
				}
			}
			PersistenceHelper.manager.store(obj1);
			return epmsepfamilytable;
		} catch (Exception e) {
			e.printStackTrace();
			if ((e instanceof WTException)) {
				throw ((WTException) e);
			} else {
				throw new WTException(e);
			}
		}
	}

	private Object importEPMFeatureDefinitionAttribute(Object obj,
			IxbElement ixbelement) throws WTException {
		try {
			EPMSepFamilyTable epmsepfamilytable = (EPMSepFamilyTable) obj;
			EPMSepFamilyTableMaster epmsepfamilytablemaster = (EPMSepFamilyTableMaster) epmsepfamilytable
					.getFamilyTableMaster();
			Enumeration enumeration = ixbelement
					.getElements("EPMFeatureDefinition");
			if ((enumeration == null) || (!enumeration.hasMoreElements()))
				return obj;
			Map map = EPMHndHelper.getDefinitions(EPMFeatureDefinition.class,
					epmsepfamilytable);
			Object obj1 = new WTArrayList();

			while (enumeration.hasMoreElements()) {
				IxbElement ixbelement1 = (IxbElement) enumeration.nextElement();
				String s = ixbelement1.getValue("name");
				Integer integer = ixbelement1.getIntValue("internalId");
				Integer integer1 = ixbelement1.getIntValue("valueType");
				Integer integer2 = ixbelement1.getIntValue("featureType");
				if ((s != null) && (integer != null) && (integer1 != null)
						&& (integer2 != null)) {
					EPMFeatureDefinition epmfeaturedefinition = (EPMFeatureDefinition) map
							.get(s);
					if (epmfeaturedefinition == null) {
						epmfeaturedefinition = EPMFeatureDefinition
								.newEPMFeatureDefinition(s, integer.intValue(),
										integer1.intValue(),
										integer2.intValue(),
										epmsepfamilytablemaster);
						((WTCollection) obj1).add(epmfeaturedefinition);
					}
				}
			}
			PersistenceHelper.manager.save((WTCollection) obj1);
			return epmsepfamilytable;
		} catch (Exception e) {
			e.printStackTrace();
			if ((e instanceof WTException)) {
				throw ((WTException) e);
			} else {
				throw new WTException(e);
			}
		}
	}



	private Object importEPMParameterDefinitionAttribute(Object obj,
			IxbElement ixbelement) throws WTException {
		try {
			EPMSepFamilyTable epmsepfamilytable = (EPMSepFamilyTable) obj;
			EPMSepFamilyTableMaster epmsepfamilytablemaster = (EPMSepFamilyTableMaster) epmsepfamilytable
					.getFamilyTableMaster();
			Enumeration enumeration = ixbelement
					.getElements("EPMParameterDefinition");
			if ((enumeration == null) || (!enumeration.hasMoreElements()))
				return obj;
			Map map = EPMHndHelper.getDefinitions(EPMParameterDefinition.class,
					epmsepfamilytable);
			Object obj1 = new WTArrayList();

			while (enumeration.hasMoreElements()) {
				IxbElement ixbelement1 = (IxbElement) enumeration.nextElement();
				String s = ixbelement1.getValue("name");
				Integer integer = ixbelement1.getIntValue("internalId");
				Integer integer1 = ixbelement1.getIntValue("valueType");
				Integer integer2 = ixbelement1.getIntValue("parameterType");
				if ((s != null) && (integer != null) && (integer1 != null)
						&& (integer2 != null)) {
					EPMParameterDefinition epmparameterdefinition = (EPMParameterDefinition) map
							.get(s);
					if (epmparameterdefinition == null) {
						epmparameterdefinition = EPMParameterDefinition
								.newEPMParameterDefinition(s,
										integer.intValue(),
										integer1.intValue(),
										integer2.intValue(),
										epmsepfamilytablemaster);
						((WTCollection) obj1).add(epmparameterdefinition);
					}
				}
			}
			PersistenceHelper.manager.save((WTCollection) obj1);
			return epmsepfamilytable;
		} catch (Exception e) {
			e.printStackTrace();
			if ((e instanceof WTException)) {
				throw ((WTException) e);
			} else {
				throw new WTException(e);
			}
		}
	}

	public EPMSepFamilyTable createNewObject() throws WTException {
		EPMSepFamilyTable epmsepfamilytable = null;

		Transaction tx = new Transaction();
		MethodContext methodcontext = MethodContext.getContext();
		long nowtime = Calendar.getInstance().getTimeInMillis();

		String createStampStr = getElementValue(this.root, "createtime");
		Timestamp createStamp;
		if (createStampStr != null)
			createStamp = new Timestamp(Long.parseLong(createStampStr));
		else {
			createStamp = new Timestamp(nowtime);
		}
		String modifyStampStr = getElementValue(this.root, "modifytime");
		Timestamp modifyStamp;
		if (modifyStampStr != null) {
			modifyStamp = new Timestamp(Long.parseLong(modifyStampStr));
		} else {
			modifyStamp = new Timestamp(nowtime);
		}


        String s1 = getElementValue(this.root,"name");
        String s2 = getElementValue(this.root,"authoringApplication");
        EPMAuthoringAppType epmauthoringapptype = EPMAuthoringAppType.toEPMAuthoringAppType(s2);
        try
        {	tx.start();
            String s3 = getElementValue(this.root,"ownerApplication");
            EPMApplicationType epmapplicationtype = s3 != null ? EPMApplicationType.toEPMApplicationType(s3) : EPMApplicationType.getEPMApplicationTypeDefault();
            EPMContextHelper.setApplication(epmapplicationtype);
            EPMSepFamilyTableMaster epmsepfamilytablemaster = EPMSepFamilyTableMaster.newEPMSepFamilyTableMaster(s1, epmauthoringapptype);
            WTContainerRef wtcontainerref = getWTContainerRef(this.root);
            epmsepfamilytablemaster.setContainerReference(wtcontainerref);
            epmsepfamilytablemaster = (EPMSepFamilyTableMaster)PersistenceHelper.manager.save(epmsepfamilytablemaster);
            logger((new StringBuilder()).append("createNewObject: family table master created - ").append(PersistenceHelper.getObjectIdentifier(epmsepfamilytablemaster)).append(", ").append(wtcontainerref.getName()).append(" [").append(wtcontainerref.getKey()).append("], ").toString());
            epmsepfamilytable = EPMSepFamilyTable.newEPMSepFamilyTable(epmsepfamilytablemaster, null);
    		importDomainFolderAttribute(epmsepfamilytable, this.root);
    		importVersionAttribute2(epmsepfamilytable, this.root);
    		epmsepfamilytable = (EPMSepFamilyTable)PersistenceServerHelper.manager.store(epmsepfamilytable,createStamp,modifyStamp);
    		importEPMFeatureDefinitionAttribute(epmsepfamilytable, this.root);
    		importEPMParameterDefinitionAttribute(epmsepfamilytable, this.root);
    		importEPMFamilyTableColumnAttribute(epmsepfamilytable, this.root);
    		importContentItemAttribute(epmsepfamilytable, this.root);
            tx.commit();
            tx=null;
            logger((new StringBuilder()).append("createNewObject: family table created - ").append(toString(epmsepfamilytable)).toString());
        }
        catch(WTPropertyVetoException wtpropertyvetoexception)
        {
            throw new WTException(wtpropertyvetoexception);
        } finally {
			if (tx != null)
				tx.rollback();
			methodcontext.remove("ixb_store_object_context/key");
		}
        return epmsepfamilytable;
	}


	private EPMSepFamilyTable createNewVersion(EPMSepFamilyTable table) throws WTException {
		EPMSepFamilyTable newTable = null;
		Transaction tx = new Transaction();
		MethodContext methodcontext = MethodContext.getContext();
		try {
			long nowtime = Calendar.getInstance().getTimeInMillis();

			String createStampStr = getElementValue(this.root, "createtime");
			Timestamp createStamp;
			if (createStampStr != null)
				createStamp = new Timestamp(Long.parseLong(createStampStr));
			else {
				createStamp = new Timestamp(nowtime);
			}
			String modifyStampStr = getElementValue(this.root, "modifytime");
			Timestamp modifyStamp;
			if (modifyStampStr != null)
				modifyStamp = new Timestamp(Long.parseLong(modifyStampStr));
			else {
				modifyStamp = new Timestamp(nowtime);
			}
			tx.start();
			if (!VersionControlHelper.isLatestIteration(table)) {
				table = (EPMSepFamilyTable) VersionControlHelper
						.getLatestIteration(table);
			}
			String versionId = getElementValue(this.root,
					"versionInfo/versionId");
			versionId = getPropertiesValue(versionId);
			// String versionLevel = getElementValue(this.root,
			// "versionInfo/versionLevel");
			String iterationId = getElementValue(this.root,
					"versionInfo/iterationId");

			newTable = (EPMSepFamilyTable) VersionControlHelper.service
					.newVersionable(table);
			Series se = VersionControlHelper.getVersionIdentifier(table)
					.getSeries();
			se.setValueWithoutValidating(versionId);
			VersionIdentifier vi = VersionIdentifier
					.newVersionIdentifier((MultilevelSeries) se);
			Series series = table.getIterationInfo().getIdentifier().getSeries();
			series.setValueWithoutValidating(iterationId);
			IterationIdentifier ii = IterationIdentifier
					.newIterationIdentifier(series);
			VersionControlHelper.setIterationIdentifier(newTable, ii);
			VersionControlHelper.setVersionIdentifier(newTable, vi, false);
			FolderHelper.assignLocation(newTable,
					FolderHelper.service.getFolder(table));
			newTable.setContainerReference(table.getContainerReference());
			newTable = (EPMSepFamilyTable) VersionControlHelper.service.insertNode(
					newTable, null, null);
//			if (PersistenceHelper.isPersistent(newTable))
//    			newTable = (EPMSepFamilyTable) PersistenceHelper.manager.save(newTable);
//			else
//				newTable = (EPMSepFamilyTable) PersistenceServerHelper.manager.store(
//						newTable, createStamp, modifyStamp);
			tx.commit();
            tx = null;
			importEPMFeatureDefinitionAttribute(newTable, this.root);
    		importEPMParameterDefinitionAttribute(newTable, this.root);
    		importEPMFamilyTableColumnAttribute(newTable, this.root);

    		importContentItemAttribute(newTable, this.root);

            logger((new StringBuilder()).append("createNewObject: family table created - ").append(toString(newTable)).toString());

		} catch (Exception e) {
			if ((e instanceof WTException))
				throw ((WTException) e);
			throw new WTException(e);
		} finally {
			if (tx != null)
				tx.rollback();
			methodcontext.remove("ixb_store_object_context/key");
		}
		return newTable;
	}

	private EPMSepFamilyTable createNewIteration(EPMSepFamilyTable table) throws WTException {
		EPMSepFamilyTable newTable = null;
		Transaction tx = new Transaction();
		MethodContext methodcontext = MethodContext.getContext();
		try {
			long nowtime = Calendar.getInstance().getTimeInMillis();

			String createStampStr = getElementValue(this.root, "createtime");
			Timestamp createStamp;
			if (createStampStr != null)
				createStamp = new Timestamp(Long.parseLong(createStampStr));
			else {
				createStamp = new Timestamp(nowtime);
			}
			String modifyStampStr = getElementValue(this.root, "modifytime");
			Timestamp modifyStamp;
			if (modifyStampStr != null)
				modifyStamp = new Timestamp(Long.parseLong(modifyStampStr));
			else {
				modifyStamp = new Timestamp(nowtime);
			}
			tx.start();
			newTable = (EPMSepFamilyTable) VersionControlHelper.service.newIteration(
					table, true);
			String iterationId = getElementValue("versionInfo/iterationId");
			Series series = table.getIterationInfo().getIdentifier().getSeries();
			series.setValueWithoutValidating(iterationId);
			IterationIdentifier ii = IterationIdentifier
					.newIterationIdentifier(series);
			VersionControlHelper.setIterationIdentifier(newTable, ii);
			VersionControlServerHelper.setBranchIdentifier(newTable,
					VersionControlHelper.getBranchIdentifier(table));
			newTable.setControlBranch(VersionControlServerHelper
					.getControlBranch(table));
			newTable.setContainerReference(table.getContainerReference());
			FolderHelper.assignLocation(newTable,
					FolderHelper.service.getFolder(table));
			newTable = (EPMSepFamilyTable) VersionControlHelper.service.insertIteration(newTable);
			tx.commit();
			tx = null;
			importEPMFeatureDefinitionAttribute(newTable, this.root);
    		importEPMParameterDefinitionAttribute(newTable, this.root);
    		importEPMFamilyTableColumnAttribute(newTable, this.root);
    		importContentItemAttribute(newTable, this.root);
            logger((new StringBuilder()).append("createNewObject: family table created - ").append(toString(newTable)).toString());

		} catch (Exception e) {
			if ((e instanceof WTException))
				throw ((WTException) e);
			throw new WTException(e);
		} finally {
			if (tx != null)
				tx.rollback();
			methodcontext.remove("ixb_store_object_context/key");
		}
		return newTable;
	}




	private static EPMSepFamilyTable findAlreadyImportedVersionedObject(
			String name, String version, String iteration,
			WTContainerRef wtcontainerref) throws WTException {
		QuerySpec queryspec = new QuerySpec();
		int i = queryspec.appendClassList(EPMSepFamilyTable.class, true);
		int j = queryspec.appendClassList(EPMSepFamilyTableMaster.class, false);
		queryspec.appendWhere(new SearchCondition(
				EPMSepFamilyTableMaster.class, "name", "=", name),
				new int[] { j });
		queryspec.appendAnd();
		queryspec.appendWhere(new SearchCondition(
				EPMSepFamilyTableMaster.class,
				"thePersistInfo.theObjectIdentifier.id",
				EPMSepFamilyTable.class, "masterReference.key.id"), new int[] {
				j, i });
		if (version != null) {
			queryspec.appendAnd();
			queryspec.appendWhere(new SearchCondition(EPMSepFamilyTable.class,
					"versionInfo.identifier.versionId", "=", version, false),
					new int[] { i });
		}
		if (iteration != null) {
			queryspec.appendAnd();
			queryspec.appendWhere(new SearchCondition(EPMSepFamilyTable.class,
					"iterationInfo.identifier.iterationId", "=", iteration),
					new int[] { i });
		}
		ContainerSpec containerspec = new ContainerSpec();
		try {
			wt.inf.container.WTContainer wtcontainer = WTContainerHelper.service
					.getNamespace(EPMSepFamilyTableMaster.class,
							wtcontainerref.getReferencedContainer());
			WTContainerRef wtcontainerref1 = WTContainerRef
					.newWTContainerRef(wtcontainer);
			containerspec.addSearchContainer(wtcontainerref1);
			containerspec.setFilterByNamespace(true);
			queryspec.setAdvancedQueryEnabled(true);
			queryspec.appendAnd();
			queryspec.appendWhere(WTContainerHelper.getWhereContainerIn(
					containerspec, new Class[] { EPMSepFamilyTable.class }),
					new int[] { i });
		} catch (WTPropertyVetoException wtpropertyvetoexception) {
			throw new WTException(wtpropertyvetoexception);
		}
		QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
		EPMSepFamilyTable epmsepfamilytable = null;
		if (queryresult.hasMoreElements()) {
			Persistable apersistable[] = (Persistable[]) (Persistable[]) queryresult
					.nextElement();
			epmsepfamilytable = (EPMSepFamilyTable) apersistable[0];
		}
		return epmsepfamilytable;
	}

	public static String toString(EPMSepFamilyTable epmsepfamilytable)
	        throws WTException
	    {
	        if(epmsepfamilytable == null)
	            return "null";
	        StringBuffer stringbuffer = new StringBuffer(epmsepfamilytable.getName());
	        stringbuffer.append(" ");
	        stringbuffer.append(PersistenceHelper.getObjectIdentifier(epmsepfamilytable));
	        stringbuffer.append(" ");
	        VersionIdentifier versionidentifier = VersionControlHelper.getVersionIdentifier(epmsepfamilytable);
	        IterationIdentifier iterationidentifier = VersionControlHelper.getIterationIdentifier(epmsepfamilytable);
	        if(versionidentifier == null || iterationidentifier == null)
	        {
	            stringbuffer.append(" (new) ");
	        } else
	        {
	            stringbuffer.append(versionidentifier.getValue());
	            if(VersionControlHelper.isAOneOff(epmsepfamilytable))
	            {
	                stringbuffer.append('-');
	                stringbuffer.append(epmsepfamilytable.getOneOffVersionInfo().getIdentifier().getSeries().getValue());
	            }
	            stringbuffer.append('.');
	            stringbuffer.append(iterationidentifier.getValue());
	        }
	        if(WorkInProgressHelper.isWorkingCopy(epmsepfamilytable))
	            stringbuffer.append('W');
	        if(WorkInProgressState.TERMINAL.equals(WorkInProgressHelper.getState(epmsepfamilytable)))
	            stringbuffer.append('T');
	        return stringbuffer.toString();
	    }
}