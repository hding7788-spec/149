package com.ptc.extend.ixb;

import com.ptc.extend.ixb.center.MQExpImpConstants;
import com.ptc.extend.util.ObjectProperty;
import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.workflow.tree.GenerateJson;
import ext.sast.center.synch.MQExpImpUtil;
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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

public class CmExpImpEnvelopeMemberLink extends CmExpImpLink {

    public CmExpImpEnvelopeMemberLink(Object obj, CmExporter expHdl)
            throws WTException {
        super(obj, expHdl);
    }
    public CmExpImpEnvelopeMemberLink(Object obj, CmExporter expHdl,String type)
            throws WTException {
        super(obj, expHdl,type);
    }

    public CmExpImpEnvelopeMemberLink(Object obj, CmImporter impHdl, String fname) throws WTException {
        super(obj, impHdl, fname);
    }

    public CmExpImpEnvelopeMemberLink(Object obj, CmImporter impHdl, String fname,String type) throws WTException {
        super(obj, impHdl, fname,type);
    }
    public String getRootTag() {
        return CmExpImpConstraints.XML_ENVELOPEMEMBERLINK;
    }

    public void exportObject(Object obj) throws WTException {
        if (!(obj instanceof ArrayList))
            throw new WTException("Object not ArrayList.");
        exportAttribute((ArrayList) obj);
    }

    private void exportAttribute(ArrayList list) throws WTException {
        Iterator it = list.iterator();
        while (it.hasNext()) {
            EnvelopeMemberLink link = (EnvelopeMemberLink) it.next();
            exportAttribute(link);
        }
        this.expHdl.storeDocumentInDir(this.ixbdocument, getSavePathInJar());
    }

    private void exportAttribute(EnvelopeMemberLink link) throws WTException {
        IxbElement ixbelement = this.root.addElement("DataRecord");
        exportLocalIdAttribute(link, ixbelement);
        exportTypeDefinitionAttribute(link, ixbelement);
        RevisionControlled revision = link.getRevisionControlled();
        ixbelement.addValue("member/number", emptyIfNull(ObjectProperty.getNumber(revision)));
        ixbelement.addValue("member/version", emptyIfNull(ObjectProperty.getVersion(revision)));
        ixbelement.addValue("member/iteration", emptyIfNull(ObjectProperty.getIteration(revision)));

        if (revision instanceof WTPart) {
			if("MQ".equals(type)){
				ixbelement.addValue("type", MQExpImpConstants.XML_MQPART.toUpperCase());
			}else{
				ixbelement.addValue("member/type", "WTPart");
			}

		} else if (revision instanceof WTDocument) {
			if("MQ".equals(type)){
				ixbelement.addValue("type",  MQExpImpConstants.XML_MQDOCUMENT);
			}else{
				ixbelement.addValue("member/type", "WTDocument");
			}
		} else if (revision instanceof EPMDocument) {
			if("MQ".equals(type)){
				ixbelement.addValue("type", MQExpImpConstants.XML_MQCADDOCUMENT);
			}else{
				ixbelement.addValue("member/type", "EPMDocument");
			}
		}
        ProcessEnvelope pe = link.getProcessEnvelope();
        ixbelement.addValue("processEnvelope/number", emptyIfNull(pe.getNumber()));
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
                String memberNumber = getNoTrimElementValue(data, "member/number");
                String memberVersion = getElementValue(data, "member/version");
                memberVersion = MQExpImpUtil.attrConvertValueBySendFrom("versionInfo", memberVersion, "IMP",getSendFrom());
                if(memberNumber == null || memberVersion == null){
                	continue;
                }
                //memberVersion = getPropertiesValue(memberVersion);
                String memberIteration = getElementValue(data, "member/iteration");
                String memberObjectType = getElementValue(data, "member/type");

                //批量预审单相关
                String maturity = getElementValue(data, "member/maturity");
                String maturityChangeReason = getElementValue(data, "member/maturityChangeReason");

                RevisionControlled revisionControlled = null;
                if (memberObjectType.equalsIgnoreCase("WTPart")||memberObjectType.equalsIgnoreCase(MQExpImpConstants.XML_MQPART)) {
                    WTPart memberObject = (WTPart) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(
                            WTPart.class, memberNumber, memberVersion, memberIteration);
                    revisionControlled = memberObject;
                } else if (memberObjectType.equalsIgnoreCase("WTDocument")||memberObjectType.equalsIgnoreCase(MQExpImpConstants.XML_MQDOCUMENT)) {
                    WTDocument memberObject = (WTDocument) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(
                            WTDocument.class, memberNumber, memberVersion, memberIteration);
                    revisionControlled = memberObject;
                } else if (memberObjectType.equalsIgnoreCase("EPMDocument")||memberObjectType.equalsIgnoreCase(MQExpImpConstants.XML_MQCADDOCUMENT)) {
                    EPMDocument memberObject = (EPMDocument) CmExpImpSearchHelper
                            .searchIteratedByNumberVersionIteration(EPMDocument.class, memberNumber, memberVersion,
                                    memberIteration);
                    revisionControlled = memberObject;
                }

                String processEnvelopeNumber = getNoTrimElementValue(data, "processEnvelope/number");
                String s = getElementValue(data, "processEnvelope/externalTypeId");
                if(s == null) {
                	//s = "WCTYPE|ext.ases.envelope.ProcessEnvelope|casc.sast.149.APPROVEFORM";
                	s ="WCTYPE|ext.ases.envelope.ProcessEnvelope|casc.sast.149.RELEASEFORM";//发放单
                }else {
                	s = getPropertiesValue(s);
                }
                ProcessEnvelope pe = (ProcessEnvelope) CmExpImpSearchHelper.getProcessEnvelopeByNumber(
                        processEnvelopeNumber, s);

                if(pe==null){
                	 pe = (ProcessEnvelope) CmExpImpSearchHelper.getProcessEnvelopeByNumber(
                             processEnvelopeNumber);
                }
                logger.log("==>Import EnvelopeMemberLink:number=<" + memberNumber +
                        "> MemberType=" + memberObjectType + " Ver=" + memberVersion + "." + memberIteration
                        + " to ProcessEnvelope:number=<" +
                        processEnvelopeNumber + ">");
                if (revisionControlled == null) {
                    logger.log("==>Missing " + memberObjectType + ":" + memberNumber + " " + memberVersion + "."
                            + memberIteration);
                    this.impHdl.putInMissingObjectSet(memberObjectType, memberNumber, memberVersion, memberIteration);
                }
                if (pe == null) {
                    logger.log("==>Missing ProcessEnvelope:" + processEnvelopeNumber);
                    this.impHdl.putInMissingMasterObjectSet("ProcessEnvelope", processEnvelopeNumber);
                }
                if ((revisionControlled == null) || (pe == null)) {
                    missingobj = true;
                } else {
                    EnvelopeMemberLink link = CmExpImpSearchHelper.searchEnvelopeMemberLink(pe, revisionControlled);
                    if (link == null) {
                        link = createLink(pe, revisionControlled, data);
                        if(GenerateJson.isPreviewPkg(pe)){
                            link.setDescription(maturity);
                            link.setImplementadvise(maturityChangeReason);
                            PersistenceServerHelper.manager.update(link);
                        }
                        if (link != null) {
                            list.add(link);
                            logger.log("==>EnvelopeMemberLink import OK!");

                        } else {
                            dofailed = true;
                        }
                    } else {
                        logger.log("==>EnvelopeMemberLink already imported, IGNORE!");
                    }
                }
            }
            if (missingobj)
                throw new MissingObjectException("==>Missing objects when import EnvelopeMemberLink");
            if (dofailed)
                throw new WTException("==>Not All EnvelopeMemberLink Imported, need to rearrange.");
        } catch (Exception e) {
            logger.log(e.getLocalizedMessage());
            if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
        }
        return list;
    }

    private EnvelopeMemberLink createLink(ProcessEnvelope pe,
            RevisionControlled revision, IxbElement ixbelement) {
        Transaction tx = new Transaction();
        try {
            tx.start();
            EnvelopeMemberLink link = EnvelopeMemberLink.newEnvelopeMemberLink(
                    pe, revision);
            if (!(revision instanceof WTPart)) {
            	String memberNumber = ObjectProperty.getNumber(revision);
                HashMap<String, RevisionControlled> map = impHdl.getExistedNumberObject();
                //设置会签状态
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
				// 设置实施意见
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

    private void deleteRelatedObjLink(ProcessEnvelope processEnvelope)
            throws WTException {
        QueryResult qr = PersistenceHelper.manager.navigate(processEnvelope,
                "theRevisionControlled",
                ext.ases.envelope.EnvelopeMemberLink.class, false);
        while (qr.hasMoreElements()) {
            EnvelopeMemberLink link = (EnvelopeMemberLink) qr.nextElement();
            PersistenceHelper.manager.delete(link);
        }
    }

}