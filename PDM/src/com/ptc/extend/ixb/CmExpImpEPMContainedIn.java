package com.ptc.extend.ixb;

import java.util.Enumeration;
import java.util.Map;

import wt.epm.EPMDocument;
import wt.epm.familytable.EPMFamilyTableCell;
import wt.epm.familytable.EPMFamilyTableCellDependency;
import wt.epm.familytable.EPMFamilyTableColumn;
import wt.epm.familytable.EPMSepFamilyTable;
import wt.epm.structure.EPMContainedIn;
import wt.epm.structure.EPMContainedObjectType;
import wt.facade.ixb.IxbElement;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTSet;
import wt.ixb.epm.handlers.EPMHndHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class CmExpImpEPMContainedIn extends CmExpImpLink {

	public CmExpImpEPMContainedIn(Object obj, CmExporter expHdl)
			throws WTException {
		super(obj, expHdl);
	}

	public CmExpImpEPMContainedIn(Object obj, CmImporter impHdl, String fname)
			throws WTException {
		super(obj, impHdl, fname);
	}

	public String getRootTag() {
		return CmExpImpConstraints.XML_EPMContainedIn;
	}

	public void exportObject(Object obj) throws WTException {
		
	}

	public Object importObject() throws WTException {
		return importAttribute();
	}

	public WTArrayList importObjects() throws WTException {
		return importAttribute();
	}
	
	private WTArrayList importAttribute() throws WTException {
		WTArrayList list = new WTArrayList();
		try {
			boolean missingobj = false;
			boolean dofailed = false;
			boolean hasPurage=false;
			Enumeration containsRecords = getElements("contains");
			while (containsRecords.hasMoreElements()) {
				IxbElement contains = (IxbElement) containsRecords.nextElement();
				String containsNumber = getNoTrimElementValue(contains, "contains/number");
				String containsVersion = getElementValue(contains, "contains/version");
				containsVersion = getPropertiesValue(containsVersion);
				String containsIteration = getElementValue(contains,
						"contains/iteration");				
				EPMDocument containsEPM = (EPMDocument) CmExpImpSearchHelper
						.searchIteratedByNumberVersionIteration(
								EPMDocument.class, containsNumber, containsVersion,
								containsIteration);
				
				String containedInName = getElementValue(contains, "containedIn/name");
				String containedInVersion = getElementValue(contains, "containedIn/version");
				containedInVersion = getPropertiesValue(containedInVersion);
				String containedInIteration = getElementValue(contains, "containedIn/iteration");
				EPMSepFamilyTable familyTable = CmExpImpSearchHelper.findAlreadyImportedVersionedObject(containedInName,containedInVersion,containedInIteration,this.impHdl.getWTContainerRef());
				String s = getElementValue(contains, "identifier");
	            if(s == null)
	                s = containsEPM.getCADName();
	            String s1 = getElementValue(contains, "containedType");
	           
				if (!isTypeDefinitionImported()) {
					logger("==>WARNING:Import EPMContainedIn:TypeDefinition not imported, SKIP!");
				} else {
					if (containedInIteration == null) {
						logger("==>Missing EPMDocument:" + containsNumber + " "
								+ containsVersion + "." + containsIteration);
						this.impHdl.putInMissingObjectSet("EPMDocument",
								containsNumber, containsVersion, containsIteration);
					}
					if (familyTable == null) {
						logger("==>Missing EPMSepFamilyTable: name=<" + containedInName + "> version=<" + containedInVersion + "> iteration=<" + containedInIteration + ">");
						this.impHdl.putInMissingMasterObjectSet(
								"EPMSepFamilyTable", containedInName);
					}
					if ((containedInIteration == null) || (familyTable == null)) {
						missingobj = true;
					} else {
						if(!hasPurage){
							// 先清理点本次导入EPMSepFamilyTable 的EPMContainedIn
				        	WTSet wtset = CmExpImpSearchHelper.searchAllEPMContainedIn(familyTable);
				        	PersistenceServerHelper.manager.remove(wtset);
				        	hasPurage = true;
						}
						
						EPMContainedIn epmcontainedin = CmExpImpSearchHelper.searchEPMContainedIn(containsEPM, familyTable);
						if (epmcontainedin == null) {
							EPMContainedObjectType epmcontainedobjecttype = EPMContainedObjectType.toEPMContainedObjectType(s1);
							epmcontainedin = EPMContainedIn.newEPMContainedIn(containsEPM, familyTable, s, epmcontainedobjecttype);
							PersistenceServerHelper.manager.insert(epmcontainedin);
							epmcontainedin = importEPMFamilyTableCellAttr(epmcontainedin, contains);
							logger("==>Import EPMContainedIn:number=<" + containsNumber
								+ "> Ver=" + containsVersion + "." + containsIteration
								+ " to EPMSepFamilyTable name=<" + containedInName + "> version=<" + containedInVersion + "> iteration=<" + containedInIteration + ">");
						
							if (epmcontainedin != null) {
								list.add(epmcontainedin);
								logger("==>EPMContainedIn:number import OK!");
							} else {
								dofailed = true;
							}
						} else {
							logger("==>EPMContainedIn:number already imported, IGNORE!");
						}
					}
				}
			}
			if (missingobj)
				throw new MissingObjectException(
						"==>Missing objects when create EPMMemberLink.");
			if (dofailed)
				throw new WTException(
						"==>Not All EPMMemberLink Imported, need to rearrange.");
		} catch (Exception e) {
			logger(e.getMessage());
			if ((e instanceof WTException))
				throw ((WTException) e);
			throw new WTException(e);
		}
		return list;
	}
	
	public EPMContainedIn importEPMFamilyTableCellAttr(EPMContainedIn epmcontainedin, IxbElement ixbelement)
	        throws WTException
	    {
	        if(PersistenceHelper.getObjectIdentifier(epmcontainedin) == null){
	        	PersistenceHelper.manager.store(epmcontainedin);
	        	logger("Internal Error: the Contained In link is not persisted yet.  Cannot save family table cell before the link is persisted.");
//	        	throw new WTException("Internal Error: the Contained In link is not persisted yet.  Cannot save family table cell before the link is persisted.");
	        }
	        EPMFamilyTableCellDependency epmfamilytablecelldependency;
	        try
	        {
	        	Enumeration enumeration = ixbelement.getElements("EPMFamilyTableCell");
	            if(enumeration == null || !enumeration.hasMoreElements()){
	            	return epmcontainedin;
	            }
	            Map map = EPMHndHelper.getFamilyTableColumns((EPMSepFamilyTable)epmcontainedin.getContainedIn());
	            WTCollection collection = new WTArrayList();
	            while(enumeration.hasMoreElements()) 
		        {
	            	IxbElement ixbelement1 = (IxbElement)enumeration.nextElement();
		            String s = ixbelement1.getValue("ftCellClassname");
		            String s1 = ixbelement1.getValue("ftColumnClassname");
		            String s2 = ixbelement1.getValue("ftColumnName");
		            Boolean boolean1 = ixbelement1.getBooleanValue("isInherited");
		            Boolean boolean2 = ixbelement1.getBooleanValue("isDefinedHere");
		            Map map1 = (Map)map.get(s1);
		            EPMFamilyTableColumn epmfamilytablecolumn = (EPMFamilyTableColumn)map1.get(s2);
		            if(epmfamilytablecolumn == null){
		            	logger((new StringBuilder()).append(s2).append(" is not found in ").append(s1).toString());
		            	throw new WTException((new StringBuilder()).append(s2).append(" is not found in ").append(s1).toString());
		            }
		            if(s.equals(EPMFamilyTableCell.class.getName()))
		            {
		            	collection.add(EPMFamilyTableCell.newEPMFamilyTableCell(boolean1.booleanValue(), boolean2.booleanValue(), epmfamilytablecolumn, epmcontainedin));
		            } else
		            {
		            	String s3 = ixbelement1.getValue("uniqueId");
		            	Boolean boolean3 = ixbelement1.getBooleanValue("isSuppressed");
		            	String s4 = ixbelement1.getValue("childName");
		                epmfamilytablecelldependency = EPMFamilyTableCellDependency.newEPMFamilyTableCellDependency(boolean1.booleanValue(), boolean2.booleanValue(), epmfamilytablecolumn, epmcontainedin);
		                if(s3 != null)
		                    epmfamilytablecelldependency.setUniqueId(s3);
		                if(boolean3 != null)
		                    epmfamilytablecelldependency.setSuppressed(boolean3.booleanValue());
		                if(s4 != null)
		                    epmfamilytablecelldependency.setChildName(s4);
		                collection.add(epmfamilytablecelldependency);
		            }
		        }
	            collection = PersistenceHelper.manager.store(collection);
	        }
	        catch(WTException exception)
	        {
	        	logger(exception.getMessage());
	            throw exception;
	        } catch (WTPropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return epmcontainedin;
	    }

	
}