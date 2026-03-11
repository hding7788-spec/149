package com.ptc.extend.ixb;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

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
import wt.iba.value.IBAHolder;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.ptc.extend.ixb.center.MQExpImpConstants;
import com.ptc.extend.util.ObjectProperty;

import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changepackaged.ChangePackagedAffectLink;
import ext.ases.changepackaged.ChangePackagedResultLink;
import ext.casc.util.IBAUtility;
import ext.sast.center.synch.MQExpImpUtil;

public class CmExpImpChangePackagedAffectLink extends CmExpImpLink {

    public CmExpImpChangePackagedAffectLink(Object obj, CmExporter expHdl)
            throws WTException {
        super(obj, expHdl);
    }
    public CmExpImpChangePackagedAffectLink(Object obj, CmExporter expHdl,String type)
            throws WTException {
        super(obj, expHdl,type);
    }

    public CmExpImpChangePackagedAffectLink(Object obj, CmImporter impHdl, String fname) throws WTException {
        super(obj, impHdl, fname);
    }

    public CmExpImpChangePackagedAffectLink(Object obj, CmImporter impHdl, String fname,String type) throws WTException {
        super(obj, impHdl, fname,type);
    }


    public String getRootTag() {
        return CmExpImpConstraints.XML_ECNAFFECTITEMLINK;
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
			IxbElement ixbelement = this.root.addElement("AffectItem");
			ixbelement.addValue("number",
					emptyIfNull(ObjectProperty.getNumber(obj)));
			ixbelement.addValue("version",
					emptyIfNull(ObjectProperty.getVersion(obj)));
			ixbelement.addValue("iteration",
					emptyIfNull(ObjectProperty.getIteration(obj)));
			if (obj instanceof WTPart) {
				if("MQ".equals(type)){
					ixbelement.addValue("type", MQExpImpConstants.XML_MQPART.toUpperCase());
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
                if (number != null) {
                    changePackaged = CmExpImpSearchHelper.getChangePackagedByNumber(number);
                } else {
                    logger.log("==>Import CmExpImpChangePackagedAffectLink Encounter Error,Can't Found ChangePackaged");
                    throw new WTException(
                            "==>Import CmExpImpChangePackagedAffectLink Encounter Error,Can't Found ChangePackaged");
                }
                if (changePackaged == null) {
                    logger.log("==>Import CmExpImpChangePackagedAffectLink Encounter Error,Can't Found The Number=< "
                            + number + " > Of ChangePackaged");
                    throw new WTException(
                            "==>Import CmExpImpChangePackagedAffectLink Encounter Error,Can't Found The Number=< "
                                    + number + " > Of ChangePackaged");
                }

            }
            if(changePackaged!= null) {
            	deleteAffectLink(changePackaged);
            }
            Enumeration affectItem = getElements("AffectItem");
            while (affectItem.hasMoreElements()) {
                IxbElement item = (IxbElement) affectItem.nextElement();
                String number = getNoTrimElementValue(item, "number");
                String version = getElementValue(item, "version");
                //version = MQExpImpUtil.attrConvertValue("versionInfo", version, "IMP");
                version = MQExpImpUtil.attrConvertValueBySendFrom("versionInfo", version, "IMP",getSendFrom());
                //version = getPropertiesValue(version);
                String iteration = getElementValue(item, "iteration");
                String type = getElementValue(item, "type");
                String changeType = getElementValue(item, "changeType");
                if(changeType == null) {
                	changeType = "";
                }
                RevisionControlled revisionControlled = null;
                if ("WTPart".equalsIgnoreCase(type)||MQExpImpConstants.XML_MQPART.equalsIgnoreCase(type)) {
                    WTPart part = (WTPart) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(
                            WTPart.class, number, version, iteration);
                    saveChangeTypeByObject(changeType,part,"changeType");
                    revisionControlled = part;
                } else if ("WTDocument".equalsIgnoreCase(type)||MQExpImpConstants.XML_MQDOCUMENT.equalsIgnoreCase(type)) {
                    WTDocument document = (WTDocument) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(
                            WTDocument.class, number, version, iteration);
                    saveChangeTypeByObject(changeType,document,"changeType");
                    revisionControlled = document;
                } else if ("EPMDocument".equalsIgnoreCase(type)||MQExpImpConstants.XML_MQCADDOCUMENT.equalsIgnoreCase(type)) {
                    EPMDocument epm = (EPMDocument) CmExpImpSearchHelper
                            .searchIteratedByNumberVersionIteration(EPMDocument.class, number, version, iteration);
                    saveChangeTypeByObject(changeType,epm,"changeType");
                    revisionControlled = epm;
                }

                /*if (this.impHdl.isLoopTest())

                    if (revisionControlled == null) {
                        logger.log("==>Missing " + type + ": " + number + " " + version
                                + "."
                                + iteration);
                        this.impHdl.putInMissingObjectSet(type, number, version,
                                iteration);
                    }*/
                if ((revisionControlled == null)) {
                    missingobj = true;
                    logger.log("==>Missing " + type + ": " + number + " " + version
                            + "."
                            + iteration);
                } else {

                    ChangePackagedAffectLink link = createLink(changePackaged, revisionControlled);
                    if (link != null) {
                        list.add(link);
                        logger.log("==>ChangePackagedAffectLink import OK!");

                    } else {
                        dofailed = true;
                    }
                }
            }

            if (missingobj){
                // throw new MissingObjectException("==>Missing objects when import ChangePackagedResultLink");
                logger.log("==>Missing objects when import ChangePackagedAffectLink");
            }

            if (dofailed){
                //throw new WTException("==>Not All ChangePackagedResultLink Imported, need to rearrange.");
                logger.log("==>Not All ChangePackagedAffectLink Imported, need to rearrange.");
            }
        } catch (Exception e) {
            logger.log(e.getLocalizedMessage());
            if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
        }
        return list;
    }

    private void saveChangeTypeByObject(String changeType, IBAHolder ibaholder, String key) throws WTException {
    	if(ibaholder!=null&&changeType!=null&&!"".equals(changeType)){
    		IBAUtility iba = new IBAUtility(ibaholder);
    		try {
    			iba.setIBAValue(key, changeType);
    			ibaholder = iba.updateAttributeContainer(ibaholder);
    			iba.updateIBAHolder(ibaholder);
    		} catch (WTPropertyVetoException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (RemoteException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (ClassNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}


	}

	private ChangePackagedAffectLink createLink(ChangePackaged changePackaged, RevisionControlled revision) {
        Transaction tx = new Transaction();
        try {
            tx.start();

            ChangePackagedAffectLink link = ChangePackagedAffectLink.newChangePackagedAffectLink(changePackaged,
                    revision);
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

    private void deleteAffectLink(ChangePackaged changePackaged)
            throws WTException {
        QueryResult qr = PersistenceHelper.manager.navigate(changePackaged, ChangePackagedResultLink.ROLE_BOBJECT_ROLE,
                ChangePackagedAffectLink.class, false);
        while (qr.hasMoreElements()) {
            ChangePackagedAffectLink link = (ChangePackagedAffectLink) qr.nextElement();
            PersistenceHelper.manager.delete(link);
        }
    }
}