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

import ext.ases.technotice.TechNoticeAfterLink;

public class CmExpImpTechNoticeAfterLink extends CmExpImpLink {

    public CmExpImpTechNoticeAfterLink(Object obj, CmExporter expHdl)
            throws WTException {
        super(obj, expHdl);
    }

    public CmExpImpTechNoticeAfterLink(Object obj, CmImporter impHdl, String fname) throws WTException {
        super(obj, impHdl, fname);
    }
    public CmExpImpTechNoticeAfterLink(Object obj, CmImporter impHdl, String fname,String type) throws WTException {
        super(obj, impHdl, fname,type);
    }
    public String getRootTag() {
        return CmExpImpConstraints.XML_TECHNOTICEAFTERLINK;
    }

    public void exportObject(Object obj) throws WTException {
        if (!(obj instanceof ArrayList))
            throw new WTException("Object not ArrayList.");
        exportAttribute((ArrayList) obj);
    }

    private void exportAttribute(ArrayList list) throws WTException {
        Iterator it = list.iterator();
        while (it.hasNext()) {
            TechNoticeAfterLink link = (TechNoticeAfterLink) it.next();
            exportAttribute(link);
        }
        this.expHdl.storeDocumentInDir(this.ixbdocument, getSavePathInJar());
    }

    private void exportAttribute(TechNoticeAfterLink link) throws WTException {
        IxbElement ixbelement = this.root.addElement("DataRecord");
        exportLocalIdAttribute(link, ixbelement);
        exportTypeDefinitionAttribute(link, ixbelement);
        RevisionControlled revision = link.getAfterObject();

        ixbelement.addValue("afterObject/number", emptyIfNull(ObjectProperty.getNumber(revision)));
        ixbelement.addValue("afterObject/version", emptyIfNull(ObjectProperty.getVersion(revision)));
        ixbelement.addValue("afterObject/iteration", emptyIfNull(ObjectProperty.getIteration(revision)));
        if (revision instanceof WTPart) {
        	if("MQ".equals(type)){
                ixbelement.addValue("afterObject/type", "WTPart");
			}else{
				ixbelement.addValue("afterObject/type", MQExpImpConstants.XML_MQPART.toUpperCase());
			}
        } else if (revision instanceof WTDocument) {
        	if("MQ".equals(type)){
                ixbelement.addValue("afterObject/type", "WTDocument");
			}else{
				ixbelement.addValue("afterObject/type", MQExpImpConstants.XML_MQDOCUMENT);
			}
        } else if (revision instanceof EPMDocument) {
        	if("MQ".equals(type)){
                ixbelement.addValue("afterObject/type", "EPMDocument");
			}else{
				ixbelement.addValue("afterObject/type", MQExpImpConstants.XML_MQCADDOCUMENT);
			}
        }
        RevisionControlled techNotice = link.getAfterTechNotice();
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
                String afterObjectNumber = getNoTrimElementValue(data, "afterObject/number");
                if (this.impHdl.isLoopTest())
                    afterObjectNumber = this.impHdl.getLoopTestPrefix() + afterObjectNumber;
                String afterObjectVersion = getElementValue(data, "afterObject/version");
				afterObjectVersion = getPropertiesValue(afterObjectVersion);
                String afterObjectIteration = getElementValue(data, "afterObject/iteration");
                String afterObjectType = getElementValue(data, "afterObject/type");
                RevisionControlled revisionControlled = null;
                if ("WTPart".equalsIgnoreCase(afterObjectType)||MQExpImpConstants.XML_MQPART.equalsIgnoreCase(afterObjectType)) {
                    WTPart afterObject = (WTPart) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(
                            WTPart.class, afterObjectNumber, afterObjectVersion, afterObjectIteration);
                    revisionControlled = afterObject;
                } else if ("WTDocument".equalsIgnoreCase(afterObjectType)||MQExpImpConstants.XML_MQDOCUMENT.equalsIgnoreCase(afterObjectType)) {
                    WTDocument afterObject = (WTDocument) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(
                            WTDocument.class, afterObjectNumber, afterObjectVersion, afterObjectIteration);
                    revisionControlled = afterObject;
                } else if ("EPMDocument".equalsIgnoreCase(afterObjectType)||MQExpImpConstants.XML_MQCADDOCUMENT.equalsIgnoreCase(afterObjectType)) {
                    EPMDocument afterObject = (EPMDocument) CmExpImpSearchHelper
                            .searchIteratedByNumberVersionIteration(EPMDocument.class, afterObjectNumber,
                                    afterObjectVersion, afterObjectIteration);
                    revisionControlled = afterObject;
                }

                String techNoticeNumber = getNoTrimElementValue(data, "techNotice/number");
                if (this.impHdl.isLoopTest())
                    techNoticeNumber = this.impHdl.getLoopTestPrefix() + techNoticeNumber;
                WTDocument techNotice = (WTDocument) CmExpImpSearchHelper.searchLatestIteratedByNumber(
                        WTDocument.class, techNoticeNumber);
                logger.log("==>Import TechNoticeAfterLink=<" + techNoticeNumber +
                        "> Ver=" + afterObjectVersion + "." + afterObjectVersion + " to techNoticeNumber:number=<" +
                        techNoticeNumber + ">");
                if (revisionControlled == null) {
                    logger.log("==>Missing " + afterObjectType + ": " + afterObjectNumber + " " + afterObjectVersion
                            + "."
                            + afterObjectIteration);
                    this.impHdl.putInMissingObjectSet(afterObjectType, afterObjectNumber, afterObjectVersion,
                            afterObjectIteration);
                }
                if (techNotice == null) {
                    logger.log("==>Missing TechNotice:" + techNoticeNumber);
                    this.impHdl.putInMissingMasterObjectSet("TechNotice", techNoticeNumber);
                }
                if ((revisionControlled == null) || (techNotice == null)) {
                    missingobj = true;
                } else {
                    TechNoticeAfterLink link = CmExpImpSearchHelper.searchTechNoticeAfterLink(techNotice,
                            revisionControlled);
                    if (link == null) {
                        link = createLink(techNotice, revisionControlled, data);
                        if (link != null) {
                            list.add(link);
                            logger.log("==>TechNoticeAfterLink import OK!");

                        } else {
                            dofailed = true;
                        }
                    } else {
                        logger.log("==>TechNoticeAfterLink already imported, IGNORE!");
                    }
                }
            }
            if (missingobj)
                throw new MissingObjectException("==>Missing objects when import TechNoticeAfterLink");
            if (dofailed)
                throw new WTException("==>Not All TechNoticeAfterLink Imported, need to rearrange.");
        } catch (Exception e) {
            logger.log(e.getMessage());
            if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
        }
        return list;
    }

    private TechNoticeAfterLink createLink(WTDocument doc, RevisionControlled revision, IxbElement ixbelement) {
        Transaction tx = new Transaction();
        try {
            tx.start();

            TechNoticeAfterLink link = TechNoticeAfterLink.newTechNoticeAfterLink((RevisionControlled) doc, revision);
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

    private void deleteAfterObjLink(WTDocument wtdoc)
            throws WTException {
        QueryResult qr = PersistenceHelper.manager.navigate(wtdoc, "afterObject", TechNoticeAfterLink.class, false);
        while (qr.hasMoreElements()) {
            TechNoticeAfterLink link = (TechNoticeAfterLink) qr.nextElement();
            PersistenceHelper.manager.delete(link);
        }
    }
}