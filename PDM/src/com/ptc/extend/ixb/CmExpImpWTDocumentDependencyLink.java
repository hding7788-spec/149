package com.ptc.extend.ixb;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import wt.doc.WTDocument;
import wt.doc.WTDocumentDependencyLink;
import wt.facade.ixb.IxbElement;
import wt.fc.PersistenceServerHelper;
import wt.fc.collections.WTArrayList;
import wt.pom.Transaction;
import wt.util.WTException;
import wt.vc.Iterated;

import com.ptc.extend.util.ObjectProperty;

public class CmExpImpWTDocumentDependencyLink extends CmExpImpLink {

    public CmExpImpWTDocumentDependencyLink(Object obj, CmExporter expHdl)
            throws WTException {
        super(obj, expHdl);
    }

    public CmExpImpWTDocumentDependencyLink(Object obj, CmImporter impHdl, String fname) throws WTException {
        super(obj, impHdl, fname);
    }

    public String getRootTag() {
        return CmExpImpConstraints.XML_WTDOCUMENTDEPENDENCYLINK;
    }

    public void exportObject(Object obj) throws WTException {
        if (!(obj instanceof ArrayList))
            throw new WTException("Object not ArrayList.");
        exportAttribute((ArrayList) obj);
    }

    private void exportAttribute(ArrayList list) throws WTException {
        Iterator it = list.iterator();
        while (it.hasNext()) {
            WTDocumentDependencyLink link = (WTDocumentDependencyLink) it.next();
            exportAttribute(link);
        }
        this.expHdl.storeDocumentInDir(this.ixbdocument, getSavePathInJar());
    }

    private void exportAttribute(WTDocumentDependencyLink link) throws WTException {
        IxbElement ixbelement = addElement("DataRecord");
        exportLocalIdAttribute(link, ixbelement);
        exportTypeDefinitionAttribute(link, ixbelement);
        Iterated describedby = link.getDescribedBy();
        ixbelement.addValue("describedBy/number", emptyIfNull(ObjectProperty.getNumber(describedby)));
        ixbelement.addValue("describedBy/version", emptyIfNull(ObjectProperty.getVersion(describedby)));
        ixbelement.addValue("describedBy/iteration", emptyIfNull(ObjectProperty.getIteration(describedby)));
        Iterated describes = link.getDescribes();
        ixbelement.addValue("describes/number", emptyIfNull(ObjectProperty.getNumber(describes)));
        ixbelement.addValue("describes/version", emptyIfNull(ObjectProperty.getVersion(describes)));
        ixbelement.addValue("describes/iteration", emptyIfNull(ObjectProperty.getIteration(describes)));
        ixbelement.addValue("linkdescription", emptyIfNull(link.getLinkDescription()));
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
                String describedByNumber = getNoTrimElementValue(data, "describedBy/number");
                if (this.impHdl.isLoopTest())
                    describedByNumber = this.impHdl.getLoopTestPrefix() + describedByNumber;
                String describedByVersion = getElementValue(data, "describedBy/version");
                describedByVersion = getPropertiesValue(describedByVersion);
                String describedByIteration = getElementValue(data, "describedBy/iteration");
                WTDocument docDescribedBy = (WTDocument) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(
                        WTDocument.class, describedByNumber, describedByVersion, describedByIteration);
                String describesNumber = getNoTrimElementValue(data, "describes/number");
                if (this.impHdl.isLoopTest())
                    describesNumber = this.impHdl.getLoopTestPrefix() + describesNumber;
                String describesVersion = getElementValue(data, "describes/version");
                describesVersion = getPropertiesValue(describesVersion);
                String describesIteration = getElementValue(data, "describes/iteration");
                WTDocument docDescribes = (WTDocument) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(
                        WTDocument.class, describesNumber, describesVersion, describesIteration);
                logger("==>Import EPMBuildDescribeLink:describedBy:number=<" + describedByNumber +
                        "> Ver=" + describedByVersion + "." + describedByIteration + " to describes:number=<" +
                        describesNumber + "> Ver=" + describesVersion + "." + describesIteration);
                if (docDescribedBy == null) {
                    logger("==>Missing WTDocument:" + describedByNumber + " " + describedByVersion + "."
                            + describedByIteration);
                    this.impHdl.putInMissingObjectSet("WTDocument", describedByNumber, describedByVersion,
                            describedByIteration);
                }
                if (docDescribes == null) {
                    logger("==>Missing WTDocument:" + describesNumber + " " + describesVersion + "."
                            + describesIteration);
                    this.impHdl.putInMissingObjectSet("WTDocument", describesNumber, describesVersion,
                            describesIteration);
                }
                if ((docDescribedBy == null) || (docDescribes == null)) {
                    missingobj = true;
                } else {
                    WTDocumentDependencyLink link = CmExpImpSearchHelper.searchWTDocumentDependencyLink(docDescribes,
                            docDescribedBy);
                    if (link == null) {
                        link = createLink(docDescribes, docDescribedBy, data);
                        if (link != null) {
                            list.add(link);
                            logger("==>WTDocumentDescribeLink import OK!");
                        } else {
                            dofailed = true;
                        }
                    } else {
                        logger("==>WTDocumentDescribeLink already imported, IGNORE!");
                    }
                }
            }
            if (missingobj)
                throw new MissingObjectException("==>Missing Objects when import WTDocumentDescribeLink.");
            if (dofailed)
                throw new WTException("==>Not All WTDocumentDescribeLink Imported, need to rearrange.");
        } catch (Exception e) {
            logger(e.getMessage());
            if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
        }
        return list;
    }

    private WTDocumentDependencyLink createLink(WTDocument docDescribes, WTDocument docDescribedBy,
            IxbElement ixbelement) {
        Transaction tx = new Transaction();
        try {
            tx.start();
            WTDocumentDependencyLink link = WTDocumentDependencyLink.newWTDocumentDependencyLink(docDescribes,
                    docDescribedBy);
            link = (WTDocumentDependencyLink) importTypeDefinitionAttribute(link, ixbelement, ixbelement);
            String s = getElementValue(ixbelement, "linkdescription");
            if (s != null)
                link.setLinkDescription(s);
            PersistenceServerHelper.manager.insert(link);
            tx.commit();
            tx = null;
            WTDocumentDependencyLink localWTDocumentDependencyLink1 = link;
            return localWTDocumentDependencyLink1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tx != null)
                tx.rollback();
        }
        return null;
    }
}