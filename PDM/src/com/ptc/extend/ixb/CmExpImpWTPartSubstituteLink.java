package com.ptc.extend.ixb;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import wt.facade.ixb.IxbElement;
import wt.fc.PersistenceServerHelper;
import wt.fc.collections.WTArrayList;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartSubstituteLink;
import wt.part.WTPartUsageLink;
import wt.pom.Transaction;
import wt.util.WTException;
import wt.vc.Iterated;
import wt.vc.Mastered;

import com.ptc.extend.util.ObjectProperty;

public class CmExpImpWTPartSubstituteLink extends CmExpImpLink {

    public CmExpImpWTPartSubstituteLink(Object obj, CmExporter expHdl)
            throws WTException {
        super(obj, expHdl);
    }

    public CmExpImpWTPartSubstituteLink(Object obj, CmImporter impHdl, String fname) throws WTException {
        super(obj, impHdl, fname);
    }

    public String getRootTag() {
        return CmExpImpConstraints.XML_WTPARTSUBSTITUTELINK;
    }

    public void exportObject(Object obj) throws WTException {
        if (!(obj instanceof ArrayList))
            throw new WTException("Object not ArrayList.");
        exportAttribute((ArrayList) obj);
    }

    private void exportAttribute(ArrayList list) throws WTException {
        Iterator it = list.iterator();
        while (it.hasNext()) {
            WTPartSubstituteLink link = (WTPartSubstituteLink) it.next();
            exportAttribute(link);
        }
        this.expHdl.storeDocumentInDir(this.ixbdocument, getSavePathInJar());
    }

    private void exportAttribute(WTPartSubstituteLink link) throws WTException {
        IxbElement ixbelement = addElement("DataRecord");
        exportLocalIdAttribute(link, ixbelement);
        exportTypeDefinitionAttribute(link, ixbelement);
        WTPartUsageLink substituteForLink = link.getSubstituteFor();
        Iterated iterated = substituteForLink.getUsedBy();
        ixbelement.addValue("substituteFor/usedBy/number", emptyIfNull(ObjectProperty.getNumber(iterated)));
        ixbelement.addValue("substituteFor/usedBy/version", emptyIfNull(ObjectProperty.getVersion(iterated)));
        ixbelement.addValue("substituteFor/usedBy/iteration", emptyIfNull(ObjectProperty.getIteration(iterated)));
        Mastered master = substituteForLink.getUses();
        ixbelement.addValue("substituteFor/uses/number", emptyIfNull(ObjectProperty.getNumber(master)));
        ixbelement.addValue("substituteFor/uses/version", emptyIfNull(ObjectProperty.getVersion(master)));
        ixbelement.addValue("substituteFor/uses/iteration", emptyIfNull(ObjectProperty.getIteration(master)));
        WTPartMaster substitutes = link.getSubstitutes();
        ixbelement.addValue("substitutes/number", emptyIfNull(substitutes.getNumber()));
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

                String usedByNumber = getNoTrimElementValue(data, "substituteFor/usedBy/number");
                if (this.impHdl.isLoopTest())
                    usedByNumber = this.impHdl.getLoopTestPrefix() + usedByNumber;
                String usedByVersion = getElementValue(data, "substituteFor/usedBy/version");
                usedByVersion = getPropertiesValue(usedByVersion);
                String usedByIteration = getElementValue(data, "substituteFor/usedBy/iteration");
                WTPart usedByPart = (WTPart) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(WTPart.class,
                        usedByNumber, usedByVersion, usedByIteration);
                String usesNumber = getNoTrimElementValue(data, "substituteFor/uses/number");
                if (this.impHdl.isLoopTest())
                    usesNumber = this.impHdl.getLoopTestPrefix() + usesNumber;
                WTPart usesPart = (WTPart) CmExpImpSearchHelper.searchLatestIteratedByNumber(WTPart.class, usesNumber);
                if (usedByPart == null) {
                    logger.log("==>Missing WTPart:" + usedByNumber + " " + usedByVersion + "." + usedByIteration);
                    this.impHdl.putInMissingObjectSet("WTPart", usedByNumber, usedByVersion, usedByIteration);
                }
                if (usesPart == null) {
                    logger.log("==>Missing WTPartMaster:" + usesNumber);
                    this.impHdl.putInMissingMasterObjectSet("WTPartMaster", usesNumber);
                }
                WTPartUsageLink usagelink = null;
                if ((usedByPart != null) && (usesPart != null)) {
                    WTPartMaster master = (WTPartMaster) usesPart.getMaster();
                    usagelink = CmExpImpSearchHelper.searchWTPartUsageLink(usedByPart, master);
                }
                if (usagelink == null) {
                    logger.log("==>Missing WTPartUsageLink: " + usedByNumber + "->" + usesNumber);
                }

                String substitutesNumber = getElementValue(data, "substitutes/number");
                if (this.impHdl.isLoopTest())
                    substitutesNumber = this.impHdl.getLoopTestPrefix() + substitutesNumber;
                WTPart substitutes = (WTPart) CmExpImpSearchHelper.searchLatestIteratedByNumber(WTPart.class,
                        substitutesNumber);
                if (substitutes == null) {
                    logger.log("==>Missing WTPartMaster:" + substitutesNumber);
                    this.impHdl.putInMissingMasterObjectSet("WTPartMaster", substitutesNumber);
                }
                if ((usagelink == null) || (substitutesNumber == null)) {
                    missingobj = true;
                } else {
                    WTPartSubstituteLink link = CmExpImpSearchHelper.searchWTPartSubstituteLink(substitutes, usagelink);
                    if (link == null) {
                        link = createLink(substitutes, usagelink, data);

                        if (link != null) {
                            list.add(link);
                            logger.log("==>WTPartSubstituteLink import OK!");
                        } else {
                            dofailed = true;
                        }
                    } else {
                        logger.log("==>WTPartSubstituteLink already imported, IGNORE!");
                    }
                }
            }
            if (missingobj)
                throw new MissingObjectException("==>Missing objects when import WTPartSubstituteLink.");
            if (dofailed)
                throw new WTException("==>Not All WTPartSubstituteLink Imported, need to rearrange.");
        } catch (Exception e) {
            logger.log(e.getMessage());
            if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
        }
        return list;
    }

    private WTPartSubstituteLink createLink(WTPart substitutes, WTPartUsageLink usagelink, IxbElement ixbelement) {
        Transaction tx = new Transaction();
        try {
            tx.start();
            WTPartSubstituteLink link = WTPartSubstituteLink.newWTPartSubstituteLink(usagelink,
                    (WTPartMaster) substitutes.getMaster());
            link = (WTPartSubstituteLink) importTypeDefinitionAttribute(link, ixbelement, ixbelement);
            PersistenceServerHelper.manager.insert(link);
            tx.commit();
            tx = null;
            WTPartSubstituteLink localWTPartSubstituteLink1 = link;
            return localWTPartSubstituteLink1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tx != null)
                tx.rollback();
        }
        return null;
    }
}