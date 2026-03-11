package com.ptc.extend.ixb;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentUsageLink;
import wt.facade.ixb.IxbElement;
import wt.fc.PersistenceServerHelper;
import wt.fc.collections.WTArrayList;
import wt.pom.Transaction;
import wt.util.WTException;
import wt.vc.Iterated;
import wt.vc.Mastered;

import com.ptc.extend.util.ObjectProperty;

public class CmExpImpWTDocumentUsageLink extends CmExpImpLink {

    public CmExpImpWTDocumentUsageLink(Object obj, CmExporter expHdl)
            throws WTException {
        super(obj, expHdl);
    }

    public CmExpImpWTDocumentUsageLink(Object obj, CmImporter impHdl, String fname) throws WTException {
        super(obj, impHdl, fname);
    }

    public String getRootTag() {
        return CmExpImpConstraints.XML_WTDOCUMENTUSAGELINK;
    }

    public void exportObject(Object obj) throws WTException {
        if (!(obj instanceof ArrayList))
            throw new WTException("Object not ArrayList.");
        exportAttribute((ArrayList) obj);
    }

    private void exportAttribute(ArrayList list) throws WTException {
        Iterator it = list.iterator();
        while (it.hasNext()) {
            WTDocumentUsageLink link = (WTDocumentUsageLink) it.next();
            exportAttribute(link);
        }
        this.expHdl.storeDocumentInDir(this.ixbdocument, getSavePathInJar());
    }

    private void exportAttribute(WTDocumentUsageLink link) throws WTException {
        IxbElement ixbelement = this.root.addElement("DataRecord");
        exportLocalIdAttribute(link, ixbelement);
        exportTypeDefinitionAttribute(link, ixbelement);
        Iterated iterated = link.getUsedBy();
        ixbelement.addValue("usedBy/number", emptyIfNull(ObjectProperty.getNumber(iterated)));
        ixbelement.addValue("usedBy/version", emptyIfNull(ObjectProperty.getVersion(iterated)));
        ixbelement.addValue("usedBy/iteration", emptyIfNull(ObjectProperty.getIteration(iterated)));
        Mastered master = link.getUses();
        ixbelement.addValue("uses/number", emptyIfNull(ObjectProperty.getNumber(master)));
        ixbelement.addValue("uses/version", emptyIfNull(ObjectProperty.getVersion(master)));
        ixbelement.addValue("uses/iteration", emptyIfNull(ObjectProperty.getIteration(master)));
        ixbelement.addValue("structureorder", link.getStructureOrder());
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
                String usedByNumber = getNoTrimElementValue(data, "usedBy/number");
                if (this.impHdl.isLoopTest())
                    usedByNumber = this.impHdl.getLoopTestPrefix() + usedByNumber;
                String usedByVersion = getElementValue(data, "usedBy/version");
                usedByVersion = getPropertiesValue(usedByVersion);
                String usedByIteration = getElementValue(data, "usedBy/iteration");
                WTDocument docUsedBy = (WTDocument) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(
                        WTDocument.class, usedByNumber, usedByVersion, usedByIteration);
                String usesNumber = getNoTrimElementValue(data, "uses/number");
                if (this.impHdl.isLoopTest())
                    usesNumber = this.impHdl.getLoopTestPrefix() + usesNumber;
                WTDocument docUses = (WTDocument) CmExpImpSearchHelper.searchLatestIteratedByNumber(WTDocument.class,
                        usesNumber);
                logger.log("==>Import WTDocumentDescribeLink:number=<" + usedByNumber +
                        "> Ver=" + usedByVersion + "." + usedByIteration + " to master:number=<" +
                        usesNumber + ">");
                if (docUsedBy == null) {
                    logger.log("==>Missing WTDocument:" + usedByNumber + " " + usedByVersion + "." + usedByIteration);
                    this.impHdl.putInMissingObjectSet("WTDocument", usedByNumber, usedByVersion, usedByIteration);
                }
                if (docUses == null) {
                    logger.log("==>Missing WTDocument:" + usesNumber);
                    this.impHdl.putInMissingMasterObjectSet("WTDocumentMaster", usesNumber);
                }
                if ((docUsedBy == null) || (docUses == null)) {
                    missingobj = true;
                } else {
                    WTDocumentMaster master = (WTDocumentMaster) docUses.getMaster();
                    WTDocumentUsageLink link = CmExpImpSearchHelper.searchWTDocumentUsageLink(docUsedBy, master);
                    if (link == null) {
                        link = createLink(docUsedBy, master, data);
                        if (link != null) {
                            list.add(link);
                            logger.log("==>WTDocumentDescribeLink import OK!");
                        } else {
                            dofailed = true;
                        }
                    } else {
                        logger.log("==>WTDocumentDescribeLink already imported, IGNORE!");
                    }
                }
            }
            if (missingobj)
                throw new MissingObjectException("==>Missing objects when import WTDocumentDescribeLink");
            if (dofailed)
                throw new WTException("==>Not All WTDocumentDescribeLink Imported, need to rearrange.");
        } catch (Exception e) {
            logger.log(e.getMessage());
            if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
        }
        return list;
    }

    private WTDocumentUsageLink createLink(WTDocument doc, WTDocumentMaster master, IxbElement ixbelement) {
        Transaction tx = new Transaction();
        try {
            tx.start();
            WTDocumentUsageLink link = WTDocumentUsageLink.newWTDocumentUsageLink(doc, master);
            link = (WTDocumentUsageLink) importTypeDefinitionAttribute(link, ixbelement, ixbelement);
            Integer integer = ixbelement.getIntValue("structureorder");
            link.setStructureOrder(integer.intValue());
            PersistenceServerHelper.manager.insert(link);
            tx.commit();
            tx = null;
            WTDocumentUsageLink localWTDocumentUsageLink1 = link;
            return localWTDocumentUsageLink1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tx != null)
                tx.rollback();
        }
        return null;
    }
}