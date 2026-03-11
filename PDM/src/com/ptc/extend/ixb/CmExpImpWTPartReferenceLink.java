package com.ptc.extend.ixb;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.facade.ixb.IxbElement;
import wt.fc.PersistenceServerHelper;
import wt.fc.collections.WTArrayList;
import wt.part.WTPart;
import wt.part.WTPartReferenceLink;
import wt.pom.Transaction;
import wt.util.WTException;
import wt.vc.Iterated;
import wt.vc.Mastered;

import com.ptc.extend.util.ObjectProperty;

public class CmExpImpWTPartReferenceLink extends CmExpImpLink {

    public CmExpImpWTPartReferenceLink(Object obj, CmExporter expHdl)
            throws WTException {
        super(obj, expHdl);
    }

    public CmExpImpWTPartReferenceLink(Object obj, CmImporter impHdl, String fname) throws WTException {
        super(obj, impHdl, fname);
    }

    public String getRootTag() {
        return "WTPartReferenceLink";
    }

    public void exportObject(Object obj) throws WTException {
        if (!(obj instanceof ArrayList))
            throw new WTException("Object not ArrayList.");
        exportAttribute((ArrayList) obj);
    }

    private void exportAttribute(ArrayList list) throws WTException {
        Iterator it = list.iterator();
        while (it.hasNext()) {
            WTPartReferenceLink link = (WTPartReferenceLink) it.next();
            exportAttribute(link);
        }
        this.expHdl.storeDocumentInDir(this.ixbdocument, getSavePathInJar());
    }

    public void exportAttribute(WTPartReferenceLink link) throws WTException {
        IxbElement ixbelement = addElement("DataRecord");
        exportLocalIdAttribute(link, ixbelement);
        exportTypeDefinitionAttribute(link, ixbelement);
        Iterated referenced = link.getReferencedBy();
        ixbelement.addValue("referencedBy/number", emptyIfNull(ObjectProperty.getNumber(referenced)));
        ixbelement.addValue("referencedBy/version", emptyIfNull(ObjectProperty.getVersion(referenced)));
        ixbelement.addValue("referencedBy/iteration", emptyIfNull(ObjectProperty.getIteration(referenced)));
        Mastered master = link.getReferences();
        ixbelement.addValue("references/number", emptyIfNull(ObjectProperty.getNumber(master)));
        ixbelement.addValue("references/version", emptyIfNull(ObjectProperty.getVersion(master)));
        ixbelement.addValue("references/iteration", emptyIfNull(ObjectProperty.getIteration(master)));
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
                String referencedByNumber = getNoTrimElementValue(data, "referencedBy/number");
                if (this.impHdl.isLoopTest())
                    referencedByNumber = this.impHdl.getLoopTestPrefix() + referencedByNumber;
                String referencedByVersion = getElementValue(data, "referencedBy/version");
                referencedByVersion = getPropertiesValue(referencedByVersion);
                String referencedByIteration = getElementValue(data, "referencedBy/iteration");
                WTPart part = (WTPart) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(WTPart.class,
                        referencedByNumber, referencedByVersion, referencedByIteration);
                String referencesNumber = getNoTrimElementValue(data, "references/number");
                if (this.impHdl.isLoopTest())
                    referencesNumber = this.impHdl.getLoopTestPrefix() + referencesNumber;
                WTDocument doc = (WTDocument) CmExpImpSearchHelper.searchLatestIteratedByNumber(WTDocument.class,
                        referencesNumber);
                logger.log("==>Import WTPartReferenceLink:number=<" + referencedByNumber +
                        "> Ver=" + referencedByVersion + "." + referencedByIteration + " to master:number=<" +
                        referencesNumber + ">");
                if (part == null) {
                    logger.log("==>Missing WTPart:" + referencedByNumber + " " + referencedByVersion + "."
                            + referencedByIteration);
                    this.impHdl.putInMissingObjectSet("WTPart", referencedByNumber, referencedByVersion,
                            referencedByIteration);
                }
                if (doc == null) {
                    logger.log("==>Missing WTDocument:" + referencesNumber);
                    this.impHdl.putInMissingMasterObjectSet("WTDocument", referencesNumber);
                }
                if ((doc == null) || (part == null)) {
                    missingobj = true;
                } else {
                    WTDocumentMaster master = (WTDocumentMaster) doc.getMaster();
                    WTPartReferenceLink link = CmExpImpSearchHelper.searchWTPartReferenceLink(part, master);
                    if (link == null) {
                        link = createLink(part, master, data);
                        if (link != null) {
                            list.add(link);
                            logger.log("==>WTPartReferenceLink import OK!");
                        } else {
                            dofailed = true;
                        }
                    } else {
                        logger.log("==>WTPartReferenceLink already imported, IGNORE!");
                    }
                }
            }
            if (missingobj)
                throw new MissingObjectException("==>Missing objects when import WTPartReferenceLink.");
            if (dofailed)
                throw new WTException("==>Not All WTPartReferenceLink Imported, need to rearrange.");
        } catch (Exception e) {
            logger.log(e.getMessage());
            if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
        }
        return list;
    }

    private WTPartReferenceLink createLink(WTPart part, WTDocumentMaster docmaster, IxbElement ixbelement) {
        Transaction tx = new Transaction();
        try {
            tx.start();
            WTPartReferenceLink link = WTPartReferenceLink.newWTPartReferenceLink(part, docmaster);
            link = (WTPartReferenceLink) importTypeDefinitionAttribute(link, ixbelement, ixbelement);
            PersistenceServerHelper.manager.insert(link);
            tx.commit();
            tx = null;
            WTPartReferenceLink localWTPartReferenceLink1 = link;
            return localWTPartReferenceLink1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tx != null)
                tx.rollback();
        }
        return null;
    }
}