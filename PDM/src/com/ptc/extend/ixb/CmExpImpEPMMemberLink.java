package com.ptc.extend.ixb;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.vecmath.Matrix4d;

import com.ptc.extend.util.Debug;
import ext.casc.integrate.util.BomUtil;
import wt.epm.EPMCADReferenceControl;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.attributes.EPMParameterMap;
import wt.epm.structure.EPMDependencyLink;
import wt.epm.structure.EPMMemberLink;
import wt.epm.structure.EPMStandardStructureService;
import wt.epm.structure.Transform;
import wt.epm.util.EPMQueryHelper;
import wt.facade.ixb.IxbElement;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTArrayList;
import wt.iba.definition.AbstractAttributeDefinition;
import wt.iba.definition.AttributeDefinitionReference;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.definition.service.IBADefinitionObjectsFactory;
import wt.iba.value.IBAHolder;
import wt.iba.value.IBAHolderReference;
import wt.ixb.handlers.forattributes.ExpImpForIBAAttr;
import wt.occurrence.OccurrenceHelper;
import wt.occurrence.OccurrenceableLink;
import wt.part.*;
import wt.pom.Transaction;
import wt.util.WTException;
import wt.vc.Iterated;
import wt.vc.Mastered;

import com.ptc.extend.util.ObjectProperty;

//import com.netflux.util.ObjectProperty;

public class CmExpImpEPMMemberLink extends CmExpImpLink {
    private final String[][] transformElements = {
            { "transform/matrix4d/m00", "transform/matrix4d/m01", "transform/matrix4d/m02", "transform/matrix4d/m03" },
            { "transform/matrix4d/m10", "transform/matrix4d/m11", "transform/matrix4d/m12", "transform/matrix4d/m13" },
            { "transform/matrix4d/m20", "transform/matrix4d/m21", "transform/matrix4d/m22", "transform/matrix4d/m23" },
            { "transform/matrix4d/m30", "transform/matrix4d/m31", "transform/matrix4d/m32", "transform/matrix4d/m33" } };

    public CmExpImpEPMMemberLink(Object obj, CmExporter expHdl)
            throws WTException {
        super(obj, expHdl);
    }

    public CmExpImpEPMMemberLink(Object obj, CmImporter impHdl, String fname) throws WTException {
        super(obj, impHdl, fname);
    }

    public String getRootTag() {
        return CmExpImpConstraints.XML_EPMMEMBERLINK;
    }

    public void exportObject(Object obj) throws WTException {
        if (!(obj instanceof ArrayList))
            throw new WTException("Object not ArrayList.");
        exportAttribute((ArrayList) obj);
    }

    private void exportAttribute(ArrayList list) throws WTException {
        Iterator it = list.iterator();
        while (it.hasNext()) {
            EPMMemberLink link = (EPMMemberLink) it.next();
            exportAttribute(link);
        }
        this.expHdl.storeDocumentInDir(this.ixbdocument, getSavePathInJar());
    }

    private void exportAttribute(EPMMemberLink epmmemberlink) throws WTException {
        IxbElement ixbelement = addElement("DataRecord");
        exportLocalIdAttribute(epmmemberlink, ixbelement);
        Iterated usedByEPM = epmmemberlink.getUsedBy();
        ixbelement.addValue("usedBy/name", ObjectProperty.getName(usedByEPM));
        ixbelement.addValue("usedBy/number", ObjectProperty.getNumber(usedByEPM));
        ixbelement.addValue("usedBy/version", ObjectProperty.getVersion(usedByEPM));
        ixbelement.addValue("usedBy/iteration", ObjectProperty.getIteration(usedByEPM));
        Mastered mastered = epmmemberlink.getUses();
        ixbelement.addValue("uses/name", ObjectProperty.getName(mastered));
        ixbelement.addValue("uses/number", ObjectProperty.getNumber(mastered));
        ixbelement.addValue("uses/version", ObjectProperty.getVersion(mastered));
        ixbelement.addValue("uses/iteration", ObjectProperty.getIteration(mastered));

        exportTypeDefinitionAttribute(epmmemberlink, ixbelement);
        exportEPMDependencyLinkAttribute(epmmemberlink, ixbelement);
        exportEPMParameterMapAttribute(epmmemberlink, ixbelement);
        ixbelement.addValue("isSuppressed", epmmemberlink.isSuppressed());
        ixbelement.addValue("isAnnotated", epmmemberlink.isAnnotated());
        ixbelement.addValue("name", emptyIfNull(epmmemberlink.getName()));
        Integer integer = epmmemberlink.getIdentifier();
        if (integer != null)
            ixbelement.addValue("identifier", integer.intValue());
        String s4 = String.valueOf(epmmemberlink.getQuantity().getAmount());
        String s5 = epmmemberlink.getQuantity().getUnit().toString();
        ixbelement.addValue("quantityAmount", emptyIfNull(s4));
        ixbelement.addValue("quantityUnit", emptyIfNull(s5));
        ixbelement.addValue("isPlaced", epmmemberlink.isPlaced());
        exportEPMTransformAttribute(epmmemberlink, ixbelement);
        ixbelement.addValue("compNumber", epmmemberlink.getCompNumber());
        ixbelement.addValue("compRevNumber", epmmemberlink.getCompRevNumber());
        ixbelement.addValue("compLayerIdx", epmmemberlink.getCompLayerIdx());
        exportEPMCADReferenceControlAttribute(epmmemberlink, ixbelement);
        exportEPMUsesOccurrenceAttribute(epmmemberlink, ixbelement);
        ixbelement.addValue("isSubstitute", epmmemberlink.isSubstitute());
    }

    private void exportEPMDependencyLinkAttribute(Object obj, IxbElement ixbelement) throws WTException {
        try {
            EPMDependencyLink epmdependencylink = (EPMDependencyLink) obj;
            ixbelement.addValue("depType", epmdependencylink.getDepType());
            ixbelement.addValue("asStoredChildName", emptyIfNull(epmdependencylink.getAsStoredChildName()));
            ixbelement.addValue("isRequired", epmdependencylink.isRequired());
            ixbelement.addValue("uniqueLinkID", epmdependencylink.getUniqueLinkID());
            ixbelement.addValue("uniqueNDId", emptyIfNull(epmdependencylink.getUniqueNDId()));
            exportIBAAttribute(obj, ixbelement);
        } catch (Exception e) {
            if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
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
            for (; queryresult.hasMoreElements(); ixbelement1.addValue("parameterName",
                    emptyIfNull(epmparametermap.getParameterName()))) {
                epmparametermap = (EPMParameterMap) queryresult.nextElement();
                AbstractAttributeDefinition abstractattributedefinition = ExpImpForIBAAttr
                        .getIBADefOfHierarchyID(epmparametermap.getDefinitionReference().getHierarchyID());
                String s = ExpImpForIBAAttr.getPathOfAttributeDefinition_WithoutOrganizer(abstractattributedefinition);
                ixbelement1 = ixbelement.addElement("EPMParameterMap");
                ixbelement1.addValue("ibaPath", emptyIfNull(s));
            }
        } catch (Exception e) {
            if ((e instanceof WTException)) {
                throw ((WTException) e);
            }
            throw new WTException(e);
        }
    }

    private void exportEPMTransformAttribute(Object obj, IxbElement ixbelement) throws WTException {
        try {
            EPMMemberLink epmmemberlink = (EPMMemberLink) obj;
            boolean flag = epmmemberlink.hasTransform();
            Transform transform = epmmemberlink.getTransform();
            if (transform != null) {
                ixbelement.addValue("hasTransform", flag);
                Matrix4d matrix4d = transform.toMatrix4d();
                if (matrix4d != null) {
                    int i = this.transformElements[0].length;
                    for (int j = 0; j < 4; j++)
                        for (int k = 0; k < i; k++)
                            ixbelement.addValue(this.transformElements[j][k], matrix4d.getElement(j, k));
                }
            }
        } catch (Exception e) {
            if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
        }
    }

    private void exportEPMCADReferenceControlAttribute(Object obj, IxbElement ixbelement) throws WTException {
        try {
            EPMCADReferenceControl epmcadreferencecontrol = ((EPMMemberLink) obj).getReferenceControl();
            if (epmcadreferencecontrol == null)
                return;
            ixbelement.addValue("epmCADReferenceControl/geomRestr", epmcadreferencecontrol.getGeomRestr());
            ixbelement.addValue("epmCADReferenceControl/isGeomRestrRecursive",
                    epmcadreferencecontrol.isGeomRestrRecursive());
            ixbelement.addValue("epmCADReferenceControl/scope", epmcadreferencecontrol.getScope());
            ixbelement.addValue("epmCADReferenceControl/violRestriction", epmcadreferencecontrol.getViolRestriction());
        } catch (Exception e) {
            if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
        }
    }

    private void exportEPMUsesOccurrenceAttribute(Object obj, IxbElement ixbelement) throws WTException {
        //windchill11升级不支持api
        try {
            if(obj instanceof EPMMemberLink) {
                EPMMemberLink link = (EPMMemberLink) obj;
                EPMDocument epm = (EPMDocument) link.getRoleAObject();

                WTPart part = BomUtil.getPartByEPMDocument(epm);
                if(null == part) {
                    Debug.P("part ===================== null" + epm.getIdentity());
                } else {
                    QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(part);
                    while(qr.hasMoreElements()) {
                        WTPartUsageLink usageLink = (WTPartUsageLink) qr.nextElement();
                        OccurrenceableLink occurrenceableLink = usageLink;
                        QueryResult query = getOccurrences(occurrenceableLink);
                        while(query.hasMoreElements()) {
                            PartUsesOccurrence occurrence = (PartUsesOccurrence) query.nextElement();
                            ixbelement.addValue("occurences/attribute", emptyIfNull(occurrence.getName()));
                        }
                    }
                }
            }
        } catch(Exception e) {
            if((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
        }
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
            Enumeration dataRecords = getElements("DataRecord");
            while (dataRecords.hasMoreElements()) {
                IxbElement data = (IxbElement) dataRecords.nextElement();
                String usedByNumber = getNoTrimElementValue(data, "usedBy/number");
                if (this.impHdl.isLoopTest())
                    usedByNumber = this.impHdl.getLoopTestPrefix() + usedByNumber;
                String usedByVersion = getElementValue(data, "usedBy/version");
                usedByVersion = getPropertiesValue(usedByVersion);
                String usedByIteration = getElementValue(data, "usedBy/iteration");
                String usesNumber = getNoTrimElementValue(data, "uses/number");
                if (this.impHdl.isLoopTest())
                    usesNumber = this.impHdl.getLoopTestPrefix() + usesNumber;
                Integer integer = data.getIntValue("identifier");
                EPMDocument usesEPM = (EPMDocument) CmExpImpSearchHelper.searchLatestIteratedByNumber(
                        EPMDocument.class, usesNumber);
                EPMDocument usedByEPM = (EPMDocument) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(
                        EPMDocument.class, usedByNumber, usedByVersion, usedByIteration);
                logger.log("==>Import EPMMemberLink:number=<" + usedByNumber +
                        "> Ver=" + usedByVersion + "." + usedByIteration + " to master:number=<" +
                        usesNumber + "> identifier=" + integer);
                if (!isTypeDefinitionImported()) {
                    logger.log("==>WARNING:Import EPMMemberLink:TypeDefinition not imported, SKIP!");
                } else {
                    if (usedByEPM == null) {
                        logger.log("==>Missing EPMDocument:" + usedByNumber + " " + usedByVersion + "."
                                + usedByIteration);
                        this.impHdl.putInMissingObjectSet("EPMDocument", usedByNumber, usedByVersion, usedByIteration);
                    }
                    if (usesEPM == null) {
                        logger.log("==>Missing EPMDocumentMaster:" + usesNumber);
                        this.impHdl.putInMissingMasterObjectSet("EPMDocumentMaster", usesNumber);
                    }
                    if ((usedByEPM == null) || (usesEPM == null)) {
                        missingobj = true;
                    } else {
                        EPMDocumentMaster mastered = (EPMDocumentMaster) usesEPM.getMaster();
                        EPMMemberLink link = CmExpImpSearchHelper.searchEPMMemberLink(usedByEPM, mastered,
                                integer.intValue());
                        if (link == null) {
                            link = createLink(usedByEPM, mastered, integer.intValue(), data);
                            if (link != null) {
                                list.add(link);
                                logger.log("==>EPMMemberLink import OK!");
                            } else {
                                dofailed = true;
                            }
                        } else {
                            logger.log("==>EPMMemberLink already imported, IGNORE!");
                        }
                    }
                }
            }
            if (missingobj)
                throw new MissingObjectException("==>Missing objects when create EPMMemberLink.");
            if (dofailed)
                throw new WTException("==>Not All EPMMemberLink Imported, need to rearrange.");
        } catch (Exception e) {
            logger.log(e.getMessage());
            if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
        }
        return list;
    }

    private EPMMemberLink createLink(EPMDocument usedByEPM, EPMDocumentMaster master, int identifier,
            IxbElement ixbelement) {
        Transaction tx = new Transaction();
        try {
            tx.start();
            EPMMemberLink epmmemberlink = EPMMemberLink.newEPMMemberLink(usedByEPM, master);
            epmmemberlink = (EPMMemberLink) importEPMDependencyLinkAttribute(epmmemberlink, ixbelement);
            epmmemberlink = (EPMMemberLink) importTypeDefinitionAttribute(epmmemberlink, ixbelement, ixbelement);
            String s = getElementValue(ixbelement, "name");
            Boolean boolean1 = ixbelement.getBooleanValue("isPlaced");
            String s1 = getElementValue(ixbelement, "quantityAmount");
            String s2 = getElementValue(ixbelement, "quantityUnit");

            if(s2!=null && s2.startsWith("QuantityUnit")){
                s2 = "ea";
            }
            Boolean boolean2 = ixbelement.getBooleanValue("isSuppressed");
            Boolean boolean3 = ixbelement.getBooleanValue("isAnnotated");
            Boolean boolean4 = ixbelement.getBooleanValue("isSubstitute");
            Integer integer = ixbelement.getIntValue("identifier");
            Integer integer1 = ixbelement.getIntValue("compNumber");
            Integer integer2 = ixbelement.getIntValue("compRevNumber");
            Integer integer3 = ixbelement.getIntValue("compLayerIdx");
            if (s != null)
                epmmemberlink.setName(s);
            if (boolean1 != null)
                epmmemberlink.setPlaced(boolean1.booleanValue());
            if ((s1 != null) && (s2 != null))
                epmmemberlink.setQuantity(Quantity.newQuantity(Double.valueOf(s1).doubleValue(),
                        QuantityUnit.toQuantityUnit(s2)));
            if (boolean2 != null)
                epmmemberlink.setSuppressed(boolean2.booleanValue());
            if (boolean3 != null)
                epmmemberlink.setAnnotated(boolean3.booleanValue());
            if (boolean4 != null)
                epmmemberlink.setSubstitute(boolean4.booleanValue());
            if (integer != null)
                epmmemberlink.setIdentifier(integer.intValue());
            if (integer1 != null)
                epmmemberlink.setCompNumber(integer1.intValue());
            if (integer2 != null)
                epmmemberlink.setCompRevNumber(integer2.intValue());
            if (integer3 != null)
                epmmemberlink.setCompLayerIdx(integer3.intValue());
            epmmemberlink = (EPMMemberLink) importEPMCADReferenceControlAttribute(epmmemberlink, ixbelement);
            epmmemberlink = (EPMMemberLink) importEPMTransformAttribute(epmmemberlink, ixbelement);
            epmmemberlink.setUniqueLinkID(EPMStandardStructureService.getNextEPMLinkSequence());
            PersistenceServerHelper.manager.insert(epmmemberlink);
            tx.commit();
            tx = null;
            epmmemberlink = (EPMMemberLink) PersistenceHelper.manager.refresh(epmmemberlink);
            epmmemberlink = (EPMMemberLink) importEPMUsesOccurrenceAttribute(epmmemberlink, ixbelement);
            epmmemberlink = (EPMMemberLink) importEPMParameterMapAttribute(epmmemberlink, ixbelement);
            EPMMemberLink localEPMMemberLink1 = epmmemberlink;
            return localEPMMemberLink1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tx != null)
                tx.rollback();
        }
        return null;
    }

    private Object importEPMDependencyLinkAttribute(Object obj, IxbElement ixbelement) throws WTException {
        EPMDependencyLink epmdependencylink = (EPMDependencyLink) obj;
        try {
            Boolean boolean1 = ixbelement.getBooleanValue("isRequired");
            if (boolean1 != null)
                epmdependencylink.setRequired(boolean1.booleanValue());
            String s = getElementValue(ixbelement, "asStoredChildName");
            if (s != null) {
                if (this.impHdl.isLoopTest())
                    s = this.impHdl.getLoopTestPrefix() + s;
                epmdependencylink.setAsStoredChildName(s);
            }
            Integer integer = ixbelement.getIntValue("depType");
            if (integer != null)
                epmdependencylink.setDepType(integer.intValue());
            String s1 = getElementValue(ixbelement, "uniqueNDId");
            if (s1 != null) {
                if (this.impHdl.isLoopTest()) {
                    String[] sss = s1.split("/");
                    if (sss.length > 0) {
                        String oldchildname = getElementValue(ixbelement, "asStoredChildName");
                        StringBuilder sb = new StringBuilder();
                        sb.append(sss[0]);
                        for (int j = 1; j < sss.length; j++) {
                            if (sss[j].equals(oldchildname))
                                sb.append("/").append(this.impHdl.getLoopTestPrefix()).append(sss[j]);
                            else {
                                sb.append("/").append(sss[j]);
                            }
                        }
                        epmdependencylink.setUniqueNDId(sb.toString());
                    } else {
                        epmdependencylink.setUniqueNDId(s1);
                    }
                } else {
                    epmdependencylink.setUniqueNDId(s1);
                }

            }

            epmdependencylink = (EPMDependencyLink) importIBAAttribute(obj, ixbelement);
            return epmdependencylink;
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof WTException) {
                throw (WTException) e;
            } else {
                throw new WTException(e);
            }
        }
    }

    private Object importEPMCADReferenceControlAttribute(Object obj, IxbElement ixbelement) throws WTException {
        try {
            if (getElementValue(ixbelement, "epmCADReferenceControl") == null)
                return obj;
            EPMCADReferenceControl epmcadreferencecontrol = ((EPMMemberLink) obj).getReferenceControl();
            if (epmcadreferencecontrol == null) {
                String s = getElementValue(ixbelement, "epmCADReferenceControl/geomRestr");
                int i = Integer.parseInt(s);
                s = getElementValue(ixbelement, "epmCADReferenceControl/isGeomRestrRecursive");
                boolean flag1 = Boolean.valueOf(s).booleanValue();
                s = getElementValue(ixbelement, "epmCADReferenceControl/scope");
                int i1 = Integer.parseInt(s);
                s = getElementValue(ixbelement, "epmCADReferenceControl/violRestriction");
                int j1 = Integer.parseInt(s);
                epmcadreferencecontrol = EPMCADReferenceControl.newEPMCADReferenceControl(i, flag1, i1, j1);
                ((EPMMemberLink) obj).setReferenceControl(epmcadreferencecontrol);
            } else {
                String s1 = getElementValue(ixbelement, "epmCADReferenceControl/geomRestr");
                if (s1 != null) {
                    int j = Integer.parseInt(s1);
                    epmcadreferencecontrol.setGeomRestr(j);
                }
                s1 = getElementValue(ixbelement, "epmCADReferenceControl/isGeomRestrRecursive");
                if (s1 != null) {
                    boolean flag = Boolean.valueOf(s1).booleanValue();
                    epmcadreferencecontrol.setGeomRestrRecursive(flag);
                }
                s1 = getElementValue(ixbelement, "epmCADReferenceControl/scope");
                if (s1 != null) {
                    int k = Integer.parseInt(s1);
                    epmcadreferencecontrol.setScope(k);
                }
                s1 = getElementValue(ixbelement, "epmCADReferenceControl/violRestriction");
                if (s1 != null) {
                    int l = Integer.parseInt(s1);
                    epmcadreferencecontrol.setViolRestriction(l);
                }
            }
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof WTException) {
                throw (WTException) e;
            } else {
                throw new WTException(e);
            }
        }
    }

    private Object importEPMTransformAttribute(Object obj, IxbElement ixbelement) throws WTException {
        try {
            EPMMemberLink epmmemberlink = (EPMMemberLink) obj;
            Boolean boolean1 = ixbelement.getBooleanValue("hasTransform");
            if ((boolean1 == null) || (!boolean1.booleanValue())) {
                epmmemberlink.setTransform(null);
                return epmmemberlink;
            }
            Vector vector = new Vector(16);
            int i = this.transformElements[0].length;
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < i; k++) {
                    Double double1 = ixbelement.getDoubleValue(this.transformElements[j][k]);
                    if (double1 != null) {
                        vector.addElement(double1);
                    }
                }
            }
            Matrix4d matrix4d = new Matrix4d();
            if (vector.size() > 0) {
                double[] ad = new double[16];
                int l = 0;
                for (Enumeration enumeration = vector.elements(); enumeration.hasMoreElements();) {
                    ad[l] = ((Double) enumeration.nextElement()).doubleValue();
                    l++;
                }

                matrix4d.set(ad);
            }
            Transform transform = Transform.newTransform(matrix4d);
            epmmemberlink.setTransform(transform);
            return epmmemberlink;
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof WTException) {
                throw (WTException) e;
            } else {
                throw new WTException(e);
            }
        }
    }

    private Object importEPMUsesOccurrenceAttribute(Object obj, IxbElement ixbelement) {
        //windchill11升级不支持api
        /**OccurrenceableLink occurrenceablelink = (OccurrenceableLink) obj;
         Transaction tx = new Transaction();
         try {
         tx.start();
         Enumeration enumeration = ixbelement.getValues("occurences/attribute");
         if (enumeration == null)
         return occurrenceablelink;

         EPMUsesOccurrence epmusesoccurrence;
         for (; enumeration.hasMoreElements(); ) {
         epmusesoccurrence = EPMUsesOccurrence.newEPMUsesOccurrence(occurrenceablelink);
         epmusesoccurrence.setName((String) enumeration.nextElement());
         OccurrenceHelper.service.saveUsesOccurrenceAndData(epmusesoccurrence,
         null);
         }
         tx.commit();
         tx = null;
         return occurrenceablelink;
         } catch (Exception e) {
         e.printStackTrace();
         }finally{
         if (tx != null)
         tx.rollback();
         tx = null;
         }*/
        return obj;
    }

    private Object importEPMParameterMapAttribute(Object obj, IxbElement ixbelement) throws WTException {
        IBAHolder ibaholder = (IBAHolder) obj;
        Enumeration enumeration = ixbelement.getElements("EPMParameterMap");
        if ((enumeration == null) || (!enumeration.hasMoreElements()))
            return ibaholder;
        Transaction tx = new Transaction();
        try {
            tx.start();
            ibaholder = (IBAHolder) PersistenceHelper.manager.lockAndRefresh((Persistable) ibaholder);
            IBAHolderReference ibaholderreference = IBAHolderReference.newIBAHolderReference(ibaholder);
            WTArrayList wtarraylist = new WTArrayList();

            while (enumeration.hasMoreElements()) {
                IxbElement ixbelement1 = (IxbElement) enumeration.nextElement();
                String s = getElementValue(ixbelement1, "ibaPath");
                String s1 = getElementValue(ixbelement1, "parameterName");
                if ((s != null) && (s1 != null)) {
                    AttributeDefDefaultView attributedefdefaultview = IBADefinitionHelper.service
                            .getAttributeDefDefaultViewByPath(s);
                    AttributeDefinitionReference attributedefinitionreference = IBADefinitionObjectsFactory
                            .newAttributeDefinitionReference(attributedefdefaultview);
                    wtarraylist.add(EPMParameterMap.newEPMParameterMap(ibaholderreference,
                            attributedefinitionreference, s1));
                }
            }
            if (!wtarraylist.isEmpty())
                PersistenceHelper.manager.store(wtarraylist);
            tx.commit();
            tx = null;
            EPMDocument localEPMDocument = (EPMDocument) PersistenceHelper.manager.refresh((Persistable) ibaholder);
            return localEPMDocument;
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof WTException) {
                throw (WTException) e;
            } else {
                throw new WTException(e);
            }
        } finally {
            if (tx != null)
                tx.rollback();
        }
    }
}