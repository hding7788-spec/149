package com.ptc.extend.ixb;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import wt.facade.ixb.IxbElement;
import wt.fc.PersistenceServerHelper;
import wt.fc.collections.WTArrayList;
import wt.part.WTPart;
import wt.part.WTPartAlternateLink;
import wt.part.WTPartMaster;
import wt.pom.Transaction;
import wt.util.WTException;

public class CmExpImpWTPartAlternateLink extends CmExpImpLink {

    public CmExpImpWTPartAlternateLink(Object obj, CmExporter expHdl)
            throws WTException {
        super(obj, expHdl);
    }

    public CmExpImpWTPartAlternateLink(Object obj, CmImporter impHdl, String fname) throws WTException {
        super(obj, impHdl, fname);
    }

    public String getRootTag() {
        return CmExpImpConstraints.XML_WTPARTALTERNATELINK;
    }

    public void exportObject(Object obj) throws WTException {
        if (!(obj instanceof ArrayList))
            throw new WTException("Object not ArrayList.");
        exportAttribute((ArrayList) obj);
    }

    private void exportAttribute(ArrayList list) throws WTException {
        Iterator it = list.iterator();
        while (it.hasNext()) {
            WTPartAlternateLink link = (WTPartAlternateLink) it.next();
            exportAttribute(link);
        }
        this.expHdl.storeDocumentInDir(this.ixbdocument, getSavePathInJar());
    }

    private void exportAttribute(WTPartAlternateLink link) throws WTException {
        IxbElement ixbelement = addElement("DataRecord");
        exportLocalIdAttribute(link, ixbelement);
        exportTypeDefinitionAttribute(link, ixbelement);
        WTPartMaster alternateFor = link.getAlternateFor();
        ixbelement.addValue("alternateFor/number", emptyIfNull(alternateFor.getNumber()));
        WTPartMaster alternates = link.getAlternates();
        ixbelement.addValue("alternates/number", emptyIfNull(alternates.getNumber()));
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
                String alternateForNumber = getNoTrimElementValue(data, "alternateFor/number");
                if (this.impHdl.isLoopTest())
                    alternateForNumber = this.impHdl.getLoopTestPrefix() + alternateForNumber;
                WTPart alternateFor = (WTPart) CmExpImpSearchHelper.searchLatestIteratedByNumber(WTPart.class,
                        alternateForNumber);
                String alternatesNumber = getNoTrimElementValue(data, "alternates/number");
                if (this.impHdl.isLoopTest())
                    alternatesNumber = this.impHdl.getLoopTestPrefix() + alternatesNumber;
                WTPart alternates = (WTPart) CmExpImpSearchHelper.searchLatestIteratedByNumber(WTPart.class,
                        alternatesNumber);
                logger.log("==>Import WTPartAlternateLink:alternateFor number=<" + alternateForNumber +
                        " to alternates number=<" +
                        alternatesNumber + ">");
                if (alternateFor == null) {
                    logger.log("==>Missing WTPartMaster:" + alternateForNumber);
                    this.impHdl.putInMissingMasterObjectSet("WTPartMaster", alternateForNumber);
                }
                if (alternates == null) {
                    logger.log("==>Missing WTPartMaster:" + alternatesNumber);
                    this.impHdl.putInMissingMasterObjectSet("WTPartMaster", alternatesNumber);
                }
                if ((alternateFor == null) || (alternates == null)) {
                    missingobj = true;
                } else {
                    WTPartAlternateLink link = CmExpImpSearchHelper.searchWTPartAlternateLink(
                            (WTPartMaster) alternateFor.getMaster(), (WTPartMaster) alternates.getMaster());
                    if (link == null) {
                        link = createLink(alternateFor, alternates, data);

                        if (link != null) {
                            list.add(link);
                            logger.log("==>WTPartAlternateLink import OK!");
                        } else {
                            dofailed = true;
                        }
                    } else {
                        logger.log("==>WTPartAlternateLink already imported, IGNORE!");
                    }
                }
            }
            if (missingobj)
                throw new MissingObjectException("==>Missing objects when import WTPartAlternateLink.");
            if (dofailed)
                throw new WTException("==>Not All WTPartAlternateLink Imported, need to rearrange.");
        } catch (Exception e) {
            logger.log(e.getMessage());
            if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
        }
        return list;
    }

    private WTPartAlternateLink createLink(WTPart alternateFor, WTPart alternates, IxbElement ixbelement) {
        Transaction tx = new Transaction();
        try {
            tx.start();
            WTPartAlternateLink link = WTPartAlternateLink.newWTPartAlternateLink(
                    (WTPartMaster) alternateFor.getMaster(), (WTPartMaster) alternates.getMaster());
            link = (WTPartAlternateLink) importTypeDefinitionAttribute(link, ixbelement, ixbelement);
            PersistenceServerHelper.manager.insert(link);
            tx.commit();
            tx = null;
            WTPartAlternateLink localWTPartAlternateLink1 = link;
            return localWTPartAlternateLink1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tx != null)
                tx.rollback();
        }
        return null;
    }
}