package com.ptc.extend.ixb;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import com.ptc.extend.ixb.center.MQExpImpConstants;
import com.ptc.extend.util.ObjectProperty;

import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.facade.ixb.IxbElement;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.fc.collections.WTArrayList;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.util.WTException;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changepackaged.ChangePackagedAffectLink;
import ext.ases.changepackaged.ChangePackagedResultLink;
import ext.sast.center.synch.MQExpImpUtil;

public class CmExpImpChangePackagedResultLink extends CmExpImpLink {

    public CmExpImpChangePackagedResultLink(Object obj, CmExporter expHdl)
            throws WTException {
        super(obj, expHdl);
    }

    public CmExpImpChangePackagedResultLink(Object obj, CmExporter expHdl,String type)
            throws WTException {
        super(obj, expHdl,type);
    }
    public CmExpImpChangePackagedResultLink(Object obj, CmImporter impHdl, String fname) throws WTException {
        super(obj, impHdl, fname);
    }

    public CmExpImpChangePackagedResultLink(Object obj, CmImporter impHdl, String fname,String type) throws WTException {
        super(obj, impHdl, fname,type);
    }

    public String getRootTag() {
        return CmExpImpConstraints.XML_ECNRESULTITEMLINK;
    }

    public void exportObject(Object obj) throws WTException {
		if (!(obj instanceof ArrayList))
			throw new WTException("Object not ArrayList.");
		exportAttribute((ArrayList) obj);
	}

	private void exportAttribute(ArrayList list) throws WTException {
		Iterator it = list.iterator();
		while (it.hasNext()) {
			WTObject obj = (WTObject) it.next();
			exportAttribute(obj);
		}
		this.expHdl.storeDocumentInDir(this.ixbdocument, getSavePathInJar());
	}

	private void exportAttribute(WTObject obj) throws WTException {

		if (obj instanceof ChangePackaged) {
			IxbElement ixbelement = this.root.addElement("WTChangeOrder");
			ixbelement.addValue("number",
					emptyIfNull(ObjectProperty.getNumber(obj)));
			ixbelement.addValue("name",
					emptyIfNull(ObjectProperty.getName(obj)));
		} else {
			IxbElement ixbelement = this.root.addElement("ResultItem");
			ixbelement.addValue("number",
					emptyIfNull(ObjectProperty.getNumber(obj)));
			ixbelement.addValue("version",
					emptyIfNull(ObjectProperty.getVersion(obj)));
			ixbelement.addValue("iteration",
					emptyIfNull(ObjectProperty.getIteration(obj)));
			if (obj instanceof WTPart) {
				if("MQ".equals(type)){
					ixbelement.addValue("type", MQExpImpConstants.XML_MQPART.toUpperCase() );
				}else{
					ixbelement.addValue("type", "WTPart");
				}

			} else if (obj instanceof WTDocument) {
				if("MQ".equals(type)){
					ixbelement.addValue("type",  MQExpImpConstants.XML_MQDOCUMENT);
				}else{
					ixbelement.addValue("type", "WTDocument");
				}
			} else if (obj instanceof EPMDocument) {
				if("MQ".equals(type)){
					ixbelement.addValue("type", MQExpImpConstants.XML_MQCADDOCUMENT);
				}else{
					ixbelement.addValue("type", "EPMDocument");
				}
			}
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
            ChangePackaged changePackaged = null;
            Enumeration changeOrder = getElements("WTChangeOrder");
            while (changeOrder.hasMoreElements()) {
                IxbElement packaged = (IxbElement) changeOrder.nextElement();
                String number = getNoTrimElementValue(packaged, "number");
//                String name = getElementValue(packaged, "name");
                if (number != null) {
                    changePackaged = CmExpImpSearchHelper.getChangePackagedByNumber(number);
                } else {
                    logger.log("==>Import CmExpImpChangePackagedResultLink Encounter Error,Can't Found ChangePackaged");
                    throw new WTException(
                            "==>Import CmExpImpChangePackagedResultLink Encounter Error,Can't Found ChangePackaged");
                }
                if (changePackaged == null) {
                    logger.log("==>Import CmExpImpChangePackagedResultLink Encounter Error,Can't Found The Number=< "
                            + number + " > Of ChangePackaged");
                    throw new WTException(
                            "==>Import CmExpImpChangePackagedResultLink Encounter Error,Can't Found The Number=< "
                                    + number + " > Of ChangePackaged");
                }

            }
            if(changePackaged != null) {
            	deleteResultLink(changePackaged);
            }

            Enumeration affectItem = getElements("ResultItem");
            while (affectItem.hasMoreElements()) {
                IxbElement item = (IxbElement) affectItem.nextElement();
                String number = getNoTrimElementValue(item, "number");
                String version = getElementValue(item, "version");
                version = MQExpImpUtil.attrConvertValueBySendFrom("versionInfo", version, "IMP",getSendFrom());
                String iteration = getElementValue(item, "iteration");
                String type = getElementValue(item, "type");
                RevisionControlled revisionControlled = null;
                if (type.equalsIgnoreCase("WTPart")||type.equalsIgnoreCase(MQExpImpConstants.XML_MQPART)) {
                    WTPart part = (WTPart) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(
                            WTPart.class, number, version, iteration);
                    revisionControlled = part;
                } else if (type.equalsIgnoreCase("WTDocument")||type.equalsIgnoreCase(MQExpImpConstants.XML_MQDOCUMENT)) {
                    WTDocument document = (WTDocument) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(
                            WTDocument.class, number, version, iteration);
                    revisionControlled = document;
                } else if (type.equalsIgnoreCase("EPMDocument")||type.equalsIgnoreCase(MQExpImpConstants.XML_MQCADDOCUMENT)) {
                    EPMDocument epm = (EPMDocument) CmExpImpSearchHelper
                            .searchIteratedByNumberVersionIteration(EPMDocument.class, number, version, iteration);
                    revisionControlled = epm;
                }

               /* if (this.impHdl.isLoopTest()) {

                    if (revisionControlled == null) {
                        logger.log("==>Missing " + type + ": " + number + " " + version
                                + "."
                                + iteration);
                        this.impHdl.putInMissingObjectSet(type, number, version,
                                iteration);
                    }
                }*/
                if ((revisionControlled == null)) {
                    logger.log("==>Missing " + type + ": " + number + " " + version
                            + "."
                            + iteration);
                    missingobj = true;
                } else {

                    ChangePackagedResultLink link = createLink(changePackaged, revisionControlled);
                    if (link != null) {
                        list.add(link);
                        logger.log("==>ChangePackagedResultLink import OK!");

                    } else {
                        dofailed = true;
                    }
                }
            }
            if (missingobj){
               // throw new MissingObjectException("==>Missing objects when import ChangePackagedResultLink");
                logger.log("==>Missing objects when import ChangePackagedResultLink");
            }

            if (dofailed){
                //throw new WTException("==>Not All ChangePackagedResultLink Imported, need to rearrange.");
                logger.log("==>Not All ChangePackagedResultLink Imported, need to rearrange.");
            }
        } catch (Exception e) {
            logger.log(e.getLocalizedMessage());
            if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
        }
        return list;
    }

    private ChangePackagedResultLink createLink(ChangePackaged changePackaged, RevisionControlled revision) {
        Transaction tx = new Transaction();
        try {
            tx.start();

            ChangePackagedResultLink link = ChangePackagedResultLink.newChangePackagedResultLink(changePackaged,
                    revision);
			if (!(revision instanceof WTPart)) {
				String memberNumber = ObjectProperty.getNumber(revision);
				HashMap<String, RevisionControlled> map = impHdl
						.getExistedNumberObject();
				if (map.size() > 0) {
					if (map.get(memberNumber) != null) {
						RevisionControlled temp = map.get(memberNumber);
						if (temp.equals(revision)) {
							link.setDescription("");
						} else {
							link.setDescription("更新");
						}
					} else {
						link.setDescription("新增");
					}
				} else {
					link.setDescription("");
				}
				HashMap<String, String> implement = impHdl
						.getNumberImplementAdvise();
				String advise = implement.get(memberNumber);
				if (advise != null) {
					link.setImplementadvise(advise);
				}
			}
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

    private void deleteResultLink(ChangePackaged changePackaged)
            throws WTException {
        QueryResult qr = PersistenceHelper.manager.navigate(changePackaged, ChangePackagedResultLink.ROLE_BOBJECT_ROLE,
                ChangePackagedResultLink.class, false);
        while (qr.hasMoreElements()) {
            ChangePackagedResultLink link = (ChangePackagedResultLink) qr.nextElement();
            PersistenceHelper.manager.delete(link);
        }
    }
}