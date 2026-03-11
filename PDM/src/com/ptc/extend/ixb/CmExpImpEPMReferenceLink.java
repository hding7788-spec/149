package com.ptc.extend.ixb;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import wt.doc.DocumentMaster;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.attributes.EPMParameterMap;
import wt.epm.structure.EPMDependencyLink;
import wt.epm.structure.EPMReferenceLink;
import wt.epm.structure.EPMReferenceType;
import wt.epm.structure.EPMStandardStructureService;
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
import wt.pom.Transaction;
import wt.util.WTException;
import wt.vc.Mastered;

import com.ptc.extend.util.Debug;
import com.ptc.extend.util.ObjectProperty;

public class CmExpImpEPMReferenceLink extends CmExpImpLink {

    public CmExpImpEPMReferenceLink(Object obj, CmExporter expHdl)
            throws WTException {
        super(obj, expHdl);
    }

    public CmExpImpEPMReferenceLink(Object obj, CmImporter impHdl, String fname) throws WTException {
        super(obj, impHdl, fname);
    }

    public String getRootTag() {
        return CmExpImpConstraints.XML_EPMREFERENCELINK;
    }

    public void exportObject(Object obj) throws WTException {
        if (!(obj instanceof ArrayList))
            throw new WTException("Object not ArrayList.");
        exportAttribute((ArrayList) obj);
    }

    private void exportAttribute(ArrayList list) throws WTException {
        Iterator it = list.iterator();
        while (it.hasNext()) {
            EPMReferenceLink link = (EPMReferenceLink) it.next();
            exportAttribute(link);
        }
        this.expHdl.storeDocumentInDir(this.ixbdocument, getSavePathInJar());
    }

    private void exportAttribute(EPMReferenceLink obj) throws WTException {
        IxbElement ixbelement = this.root.addElement("DataRecord");
        EPMReferenceLink epmreferencelink = obj;
        exportLocalIdAttribute(epmreferencelink, ixbelement);
        EPMDocument epmdocument = epmreferencelink.getReferencedBy();
        ixbelement.addValue("referencedBy/number", emptyIfNull(ObjectProperty.getNumber(epmdocument)));
        ixbelement.addValue("referencedBy/version", emptyIfNull(ObjectProperty.getVersion(epmdocument)));
        ixbelement.addValue("referencedBy/iteration", emptyIfNull(ObjectProperty.getIteration(epmdocument)));
        Mastered mastered = (Mastered) epmreferencelink.getReferences();
        ixbelement.addValue("references/number", emptyIfNull(ObjectProperty.getNumber(mastered)));
        ixbelement.addValue("references/version", emptyIfNull(ObjectProperty.getVersion(mastered)));
        ixbelement.addValue("references/iteration", emptyIfNull(ObjectProperty.getIteration(mastered)));
        if ((mastered instanceof WTDocumentMaster))
            ixbelement.addValue("references/classname", "WTDocumentMaster");
        else if ((mastered instanceof EPMDocumentMaster))
            ixbelement.addValue("references/classname", "EPMDocumentMaster");
        else ixbelement.addValue("references/classname", mastered.getClass().getName());
        exportTypeDefinitionAttribute(epmreferencelink, ixbelement);
        ixbelement.addValue("referenceType", emptyIfNull(epmreferencelink.getReferenceType().toString()));
        exportEPMDependencyLinkAttribute(epmreferencelink, ixbelement);
        exportEPMParameterMapAttribute(epmreferencelink, ixbelement);
    }

    private void exportEPMDependencyLinkAttribute(Object obj, IxbElement ixbelement) throws WTException {
        try {
            EPMDependencyLink epmdependencylink = (EPMDependencyLink) obj;
            ixbelement.addValue("depType", epmdependencylink.getDepType());
            ixbelement.addValue("asStoredChildName", emptyIfNull(epmdependencylink.getAsStoredChildName()));
            ixbelement.addValue("isRequired", epmdependencylink.isRequired());
            ixbelement.addValue("uniqueLinkID", epmdependencylink.getUniqueLinkID());
            ixbelement.addValue("uniqueNDId", emptyIfNull(epmdependencylink.getUniqueNDId()));
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

    public Object importObject() throws WTException {
        return importAttribute();
    }

    public WTArrayList importObjects() throws WTException {
        return importAttribute();
    }

    public WTArrayList importAttribute() throws WTException {
        WTArrayList list = new WTArrayList();
        try {
            boolean missingobj = false;
            boolean dofailed = false;
            Enumeration dataRecords = getElements("DataRecord");
            while (dataRecords.hasMoreElements()) {
                IxbElement data = (IxbElement) dataRecords.nextElement();
                String referencedByNumber = getNoTrimElementValue(data, "referencedBy/number");
                if (this.impHdl.isLoopTest())
                    referencedByNumber = this.impHdl.getLoopTestPrefix() + referencedByNumber;
                String referencedByVersion = getElementValue(data, "referencedBy/version");
                referencedByVersion = getPropertiesValue(referencedByVersion);
                String referencedByIteration = getElementValue(data, "referencedBy/iteration");
                EPMDocument refEPM = (EPMDocument) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(
                        EPMDocument.class, referencedByNumber, referencedByVersion, referencedByIteration);

                String referencesNumber = getNoTrimElementValue(data, "references/number");
                String classname = getElementValue(data, "references/classname");
                if (this.impHdl.isLoopTest()) {
                    referencesNumber = this.impHdl.getLoopTestPrefix() + referencesNumber;
                }
                Mastered mastered = null;
                if (classname.equals("WTDocumentMaster")) {
                    WTDocument ddd = (WTDocument) CmExpImpSearchHelper.searchLatestIteratedByNumber(WTDocument.class,
                            referencesNumber);
                    if (ddd != null)
                        mastered = ddd.getMaster();
                } else if (classname.equals("EPMDocumentMaster")) {
                    EPMDocument eee = (EPMDocument) CmExpImpSearchHelper.searchLatestIteratedByNumber(
                            EPMDocument.class, referencesNumber);
                    if (eee != null)
                        mastered = eee.getMaster();
                } else {
                    logger.log("Unsupported EPMReferenceLink References class:" + classname);
                    continue;
                }
                logger.log("==>Import EPMReferenceLink:number=<" + referencedByNumber +
                        "> Ver=" + referencedByVersion + "." + referencedByIteration + " to master:number=<" +
                        referencesNumber + "> classname=" + classname);
                if (!isTypeDefinitionImported()) {
                    logger.log("==>WARNING:Import EPMReferenceLink:TypeDefinition not imported, SKIP!");
                } else {
                    if (refEPM == null) {
                        logger.log("==>Missing EPMDocument:" + referencedByNumber + " " + referencedByVersion + "."
                                + referencedByIteration);
                        this.impHdl.putInMissingObjectSet("EPMDocument", referencedByNumber, referencedByVersion,
                                referencedByIteration);
                    }
                    if (mastered == null) {
                        logger.log("==>Missing DocumentMaster:" + referencesNumber);
                        this.impHdl.putInMissingMasterObjectSet(classname, referencesNumber);
                    }
                    if ((refEPM == null) || (mastered == null)) {
                        missingobj = true;
                    } else {
                        EPMReferenceLink link = CmExpImpSearchHelper.searchEPMReferenceLink(refEPM,
                                (DocumentMaster) mastered);
                        if (link == null) {
                            link = createLink(refEPM, mastered, data);
                            if (link != null) {
                                list.add(link);
                                logger.log("==>EPMReferenceLink import OK!");
                            } else {
                                dofailed = true;
                            }
                        } else {
                            logger.log("==>EPMReferenceLink already imported, IGNORE!");
                        }
                    }
                }
            }
            if (missingobj)
                throw new MissingObjectException("==>Missing objects when import EPMDescribeLink.");
            if (dofailed)
                throw new WTException("Not All EPMDescribeLink Imported, need to rearrange for deliver.");
        } catch (Exception e) {
            logger.log(e.getMessage());
            if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
        }
        return list;
    }

    private EPMReferenceLink createLink(EPMDocument epm, Mastered mastered, IxbElement ixbelement) throws WTException {
        Transaction tx = new Transaction();
        try {
            tx.start();
            EPMReferenceLink epmreferencelink = EPMReferenceLink.newEPMReferenceLink(epm, (EPMDocumentMaster) mastered);
            epmreferencelink = (EPMReferenceLink) importTypeDefinitionAttribute(epmreferencelink, ixbelement,
                    ixbelement);
            epmreferencelink = (EPMReferenceLink) importEPMDependencyLinkAttribute(epmreferencelink, ixbelement);
            String s = getElementValue(ixbelement, "referenceType");
            if (s != null)
                epmreferencelink.setReferenceType(EPMReferenceType.toEPMReferenceType(s));
            epmreferencelink.setUniqueLinkID(EPMStandardStructureService.getNextEPMLinkSequence());
            PersistenceServerHelper.manager.insert(epmreferencelink);
            tx.commit();
            tx = null;
            epmreferencelink = (EPMReferenceLink) importEPMParameterMapAttribute(epmreferencelink, ixbelement);
            EPMReferenceLink localEPMReferenceLink1 = epmreferencelink;
            return localEPMReferenceLink1;
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
                    s = this.impHdl.getLifecycleStateName() + s;
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
                            else sb.append("/").append(sss[j]);
                        }
                        Debug.P("Changed uniqueNDID:", sb.toString());
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
            if (e instanceof WTException) {
                throw (WTException) e;
            } else {
                throw new WTException(e);
            }
        }
    }

    private Object importEPMParameterMapAttribute(Object obj, IxbElement ixbelement) throws WTException {
        Transaction tx = new Transaction();
        try {
            IBAHolder ibaholder = (IBAHolder) obj;
            Enumeration enumeration = ixbelement.getElements("EPMParameterMap");
            if ((enumeration == null) || (!enumeration.hasMoreElements())) {
                return (EPMReferenceLink) ibaholder;
            }

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
            EPMReferenceLink localEPMReferenceLink = (EPMReferenceLink) PersistenceHelper.manager
                    .refresh((Persistable) ibaholder);
            return localEPMReferenceLink;
        } catch (Exception e) {
            if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
        } finally {
            if (tx != null)
                tx.rollback();
        }
    }
}