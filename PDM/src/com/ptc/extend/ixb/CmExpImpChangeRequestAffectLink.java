package com.ptc.extend.ixb;

import java.util.Enumeration;

import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.facade.ixb.IxbElement;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTArrayList;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.util.WTException;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.changerequest.ChangeRequestAffectLink;

public class CmExpImpChangeRequestAffectLink extends CmExpImpLink {

    public CmExpImpChangeRequestAffectLink(Object obj, CmExporter expHdl)
            throws WTException {
        super(obj, expHdl);
    }

    public CmExpImpChangeRequestAffectLink(Object obj, CmImporter impHdl, String fname) throws WTException {
        super(obj, impHdl, fname);
    }
    public CmExpImpChangeRequestAffectLink(Object obj, CmImporter impHdl, String fname,String type) throws WTException {
        super(obj, impHdl, fname,type);
    }
    public String getRootTag() {
        return CmExpImpConstraints.XML_ECRAFFECTITEMLINK;
    }

    public void exportObject(Object obj) throws WTException {
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
            ChangeRequest cr = null;
            Enumeration request = getElements("WTChangeRequest");
            while (request.hasMoreElements()) {
                IxbElement element = (IxbElement) request.nextElement();
                String number = getNoTrimElementValue(element, "number");
                if (number != null) {
                    cr = CmExpImpSearchHelper.getChangeRequestByNumber(number);
                } else {
                    logger.log("==>Import CmExpImpChangeRequestAffectLink Encounter Error,Can't Found ChangeRequest");
                    throw new WTException(
                            "==>Import CmExpImpChangeRequestAffectLink Encounter Error,Can't Found ChangeRequest");
                }
                if (cr == null) {
                    logger.log("==>Import CmExpImpChangeRequestAffectLink Encounter Error,Can't Found The Number=< "
                            + number + " > Of ChangeRequest");
                    throw new WTException(
                            "==>Import CmExpImpChangeRequestAffectLink Encounter Error,Can't Found The Number=< "
                                    + number + " > Of ChangeRequest");
                }

            }
            deleteAffectLink(cr);

            Enumeration affectItem = getElements("AffectItem");
            while (affectItem.hasMoreElements()) {
                IxbElement item = (IxbElement) affectItem.nextElement();
                String number = getNoTrimElementValue(item, "number");
                String version = getElementValue(item, "version");
                version = getPropertiesValue(version);
                String iteration = getElementValue(item, "iteration");
                String type = getElementValue(item, "type");
                RevisionControlled revisionControlled = null;
                if (type.equalsIgnoreCase("WTPart")) {
                    WTPart part = (WTPart) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(
                            WTPart.class, number, version, iteration);
                    revisionControlled = part;
                } else if (type.equalsIgnoreCase("WTDocument")) {
                    WTDocument document = (WTDocument) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(
                            WTDocument.class, number, version, iteration);
                    revisionControlled = document;
                } else if (type.equalsIgnoreCase("EPMDocument")) {
                    EPMDocument epm = (EPMDocument) CmExpImpSearchHelper
                            .searchIteratedByNumberVersionIteration(EPMDocument.class, number, version, iteration);
                    revisionControlled = epm;
                }



                if ((revisionControlled == null)) {
                    missingobj = true;
                } else {

                    ChangeRequestAffectLink link = createLink(cr, revisionControlled);
                    if (link != null) {
                        list.add(link);
                        logger.log("==>ChangeRequestAffectLink import OK!");

                    } else {
                        dofailed = true;
                    }
                }
            }
            if (missingobj)
                throw new MissingObjectException("==>Missing objects when import ChangeRequestAffectLink");
            if (dofailed)
                throw new WTException("==>Not All ChangeRequestAffectLink Imported, need to rearrange.");
        } catch (Exception e) {
            logger.log(e.getLocalizedMessage());
            if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
        }
        return list;
    }

    private ChangeRequestAffectLink createLink(ChangeRequest cr, RevisionControlled revision) {
        Transaction tx = new Transaction();
        try {
            tx.start();
            ChangeRequestAffectLink link = ChangeRequestAffectLink.newChangeRequestAffectLink(cr,revision);
            PersistenceServerHelper.manager.insert(link);
            tx.commit();
            tx = null;
            return link;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tx != null)
                tx.rollback();
        }
        return null;
    }

    private void deleteAffectLink(ChangeRequest cr)
            throws WTException {
        QueryResult qr = PersistenceHelper.manager.navigate(cr, ChangeRequestAffectLink.ROLE_BOBJECT_ROLE,
                ChangeRequestAffectLink.class, false);
        while (qr.hasMoreElements()) {
            ChangeRequestAffectLink link = (ChangeRequestAffectLink) qr.nextElement();
            PersistenceHelper.manager.delete(link);
        }
    }
}