package com.ptc.extend.ixb;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

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

import com.ptc.extend.ixb.center.MQExpImpConstants;
import com.ptc.extend.util.ObjectProperty;

import ext.ases.technotice.TechNoticeBeforeLink;

public class CmExpImpTechNoticeBeforeLink extends CmExpImpLink {

    public CmExpImpTechNoticeBeforeLink(Object obj, CmExporter expHdl)
            throws WTException {
        super(obj, expHdl);
    }

    public CmExpImpTechNoticeBeforeLink(Object obj, CmImporter impHdl, String fname) throws WTException {
        super(obj, impHdl, fname);
    }
    public CmExpImpTechNoticeBeforeLink(Object obj, CmImporter impHdl, String fname,String type) throws WTException {
        super(obj, impHdl, fname,type);
    }
    public String getRootTag() {
        return CmExpImpConstraints.XML_TECHNOTICEBEFORELINK;
    }

    public void exportObject(Object obj) throws WTException {
        if (!(obj instanceof ArrayList))
            throw new WTException("Object not ArrayList.");
        exportAttribute((ArrayList) obj);
    }

    private void exportAttribute(ArrayList list) throws WTException {
        Iterator it = list.iterator();
        while (it.hasNext()) {
            TechNoticeBeforeLink link = (TechNoticeBeforeLink) it.next();
            exportAttribute(link);
        }
        this.expHdl.storeDocumentInDir(this.ixbdocument, getSavePathInJar());
    }

    private void exportAttribute(TechNoticeBeforeLink link) throws WTException {
        IxbElement ixbelement = this.root.addElement("DataRecord");
        exportLocalIdAttribute(link, ixbelement);
        exportTypeDefinitionAttribute(link, ixbelement);
        RevisionControlled revision = link.getBeforeObject();

        ixbelement.addValue("beforeObject/number", emptyIfNull(ObjectProperty.getNumber(revision)));
        ixbelement.addValue("beforeObject/version", emptyIfNull(ObjectProperty.getVersion(revision)));
        ixbelement.addValue("beforeObject/iteration", emptyIfNull(ObjectProperty.getIteration(revision)));
        if (revision instanceof WTPart) {
        	if("MQ".equals(type)){
                ixbelement.addValue("beforeObject/type", "WTPart");
			}else{
				ixbelement.addValue("beforeObject/type", MQExpImpConstants.XML_MQPART.toUpperCase());
			}
        } else if (revision instanceof WTDocument) {
        	if("MQ".equals(type)){
                ixbelement.addValue("beforeObject/type", "WTDocument");
			}else{
				ixbelement.addValue("beforeObject/type", MQExpImpConstants.XML_MQDOCUMENT);
			}
        } else if (revision instanceof EPMDocument) {
        	if("MQ".equals(type)){
                ixbelement.addValue("beforeObject/type", "EPMDocument");
			}else{
				ixbelement.addValue("beforeObject/type", MQExpImpConstants.XML_MQCADDOCUMENT);
			}
        }

        RevisionControlled techNotice = link.getBeforeTechNotice();
        ixbelement.addValue("techNotice/number", emptyIfNull(ObjectProperty.getNumber(techNotice)));
        ixbelement.addValue("techNotice/version", emptyIfNull(ObjectProperty.getVersion(techNotice)));
        ixbelement.addValue("techNotice/iteration", emptyIfNull(ObjectProperty.getIteration(techNotice)));
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
                String beforeObjectNumber = getNoTrimElementValue(data, "beforeObject/number");
                if (this.impHdl.isLoopTest())
                    beforeObjectNumber = this.impHdl.getLoopTestPrefix() + beforeObjectNumber;
                String beforeObjectVersion = getElementValue(data, "beforeObject/version");
				beforeObjectVersion = getPropertiesValue(beforeObjectVersion);
                String beforeObjectIteration = getElementValue(data, "beforeObject/iteration");
                String beforeObjectType = getElementValue(data, "beforeObject/type");
                RevisionControlled revisionControlled = null;
                if ("WTPart".equalsIgnoreCase(beforeObjectType)||MQExpImpConstants.XML_MQPART.equalsIgnoreCase(beforeObjectType)) {
                    WTPart beforeObject = (WTPart) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(
                            WTPart.class, beforeObjectNumber, beforeObjectVersion, beforeObjectIteration);
                    revisionControlled = beforeObject;
                } else if ("WTDocument".equalsIgnoreCase(beforeObjectType)||MQExpImpConstants.XML_MQDOCUMENT.equalsIgnoreCase(beforeObjectType)) {
                    WTDocument beforeObject = (WTDocument) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(
                            WTDocument.class, beforeObjectNumber, beforeObjectVersion, beforeObjectIteration);
                    revisionControlled = beforeObject;
                } else if ("EPMDocument".equalsIgnoreCase(beforeObjectType)||MQExpImpConstants.XML_MQCADDOCUMENT.equalsIgnoreCase(beforeObjectType)) {
                    EPMDocument beforeObject = (EPMDocument) CmExpImpSearchHelper
                            .searchIteratedByNumberVersionIteration(EPMDocument.class, beforeObjectNumber,
                                    beforeObjectVersion, beforeObjectIteration);
                    revisionControlled = beforeObject;
                }

                String techNoticeNumber = getNoTrimElementValue(data, "techNotice/number");
                if (this.impHdl.isLoopTest())
                    techNoticeNumber = this.impHdl.getLoopTestPrefix() + techNoticeNumber;
                WTDocument techNotice = (WTDocument) CmExpImpSearchHelper.searchLatestIteratedByNumber(
                        WTDocument.class, techNoticeNumber);
                logger.log("==>Import TechNoticeBeforeLink=<" + techNoticeNumber +
                        "> Ver=" + beforeObjectVersion + "." + beforeObjectIteration + " to techNoticeNumber:number=<" +
                        techNoticeNumber + ">");
                if (revisionControlled == null) {
                    logger.log("==>Missing " + beforeObjectType + ": " + beforeObjectNumber + " " + beforeObjectVersion
                            + "." + beforeObjectIteration);
                    this.impHdl.putInMissingObjectSet(beforeObjectType, beforeObjectNumber, beforeObjectVersion,
                            beforeObjectIteration);
                }
                if (techNotice == null) {
                    logger.log("==>Missing TechNotice:" + techNoticeNumber);
                    this.impHdl.putInMissingMasterObjectSet("TechNotice", techNoticeNumber);
                }
                if ((revisionControlled == null) || (techNotice == null)) {
                    missingobj = true;
                } else {
                    TechNoticeBeforeLink link = CmExpImpSearchHelper.searchTechNoticeBeforeLink(techNotice,
                            revisionControlled);
                    if (link == null) {
                        link = createLink(techNotice, revisionControlled, data);
                        if (link != null) {
                            list.add(link);
                            logger.log("==>TechNoticeBeforeLink import OK!");

                        } else {
                            dofailed = true;
                        }
                    } else {
                        logger.log("==>TechNoticeBeforeLink already imported, IGNORE!");
                    }
                }
            }
            if (missingobj)
                throw new MissingObjectException("==>Missing objects when import TechNoticeBeforeLink");
            if (dofailed)
                throw new WTException("==>Not All TechNoticeBeforeLink Imported, need to rearrange.");
        } catch (Exception e) {
            logger.log(e.getMessage());
            if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
        }
        return list;
    }

    private TechNoticeBeforeLink createLink(WTDocument doc, RevisionControlled revision, IxbElement ixbelement) {
        Transaction tx = new Transaction();
        try {
            tx.start();

            TechNoticeBeforeLink link = TechNoticeBeforeLink
                    .newTechNoticeBeforeLink((RevisionControlled) doc, revision);
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

    private void deleteBeforeObjLink(WTDocument wtdoc)
            throws WTException {
        QueryResult qr = PersistenceHelper.manager.navigate(wtdoc, "beforeObject", TechNoticeBeforeLink.class, false);
        while (qr.hasMoreElements()) {
            TechNoticeBeforeLink link = (TechNoticeBeforeLink) qr.nextElement();
            PersistenceHelper.manager.delete(link);
        }
    }
}