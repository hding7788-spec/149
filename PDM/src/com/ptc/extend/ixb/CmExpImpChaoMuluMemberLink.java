package com.ptc.extend.ixb;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.facade.ixb.IxbElement;
import wt.fc.collections.WTArrayList;
import wt.part.WTPart;
import wt.util.WTException;

import com.ptc.extend.ixb.center.MQExpImpConstants;
import com.ptc.extend.util.ObjectProperty;

import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.util.DBConn;
import ext.sast.center.bean.GwChaoMuluMemberLink;

public class CmExpImpChaoMuluMemberLink extends CmExpImpLink {

    public CmExpImpChaoMuluMemberLink(Object obj, CmExporter expHdl)
            throws WTException {
        super(obj, expHdl);
    }
    public CmExpImpChaoMuluMemberLink(Object obj, CmExporter expHdl,String type)
            throws WTException {
        super(obj, expHdl,type);
    }

    public CmExpImpChaoMuluMemberLink(Object obj, CmImporter impHdl, String fname) throws WTException {
        super(obj, impHdl, fname);
    }

    public CmExpImpChaoMuluMemberLink(Object obj, CmImporter impHdl, String fname,String type) throws WTException {
        super(obj, impHdl, fname,type);
    }
    public String getRootTag() {
        return CmExpImpConstraints.XML_CHAOMULUMEMBERLINK;
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
            List<GwChaoMuluMemberLink> links = new ArrayList<GwChaoMuluMemberLink>();
            String orderNumber = "";
            Enumeration dataRecords = getElements("DataRecord");
            while (dataRecords.hasMoreElements()) {
                IxbElement data = (IxbElement) dataRecords.nextElement();
                String yqjNumber = getNoTrimElementValue(data, "member/yqjNumber");
                String bpmWorkflowNumber = getElementValue(data, "member/bpmWorkflowNumber");
                String bpmWorkflowid = getElementValue(data, "member/bpmWorkflowid");
                orderNumber = getNoTrimElementValue(data, "processEnvelope/number");
                String externalTypeId = getElementValue(data, "processEnvelope/externalTypeId");
                WTPart part = (WTPart) CmExpImpSearchHelper.searchLatestIteratedByNumberAndView( WTPart.class, yqjNumber, "Design");
                GwChaoMuluMemberLink link = new GwChaoMuluMemberLink();
                link.setYqjNumber(yqjNumber);
                link.setBpmWorkflowNumber(bpmWorkflowNumber);
                link.setBpmWorkflowid(bpmWorkflowid);
                link.setOrderNumber(orderNumber);
                link.setExternalTypeId(externalTypeId);
                if(part !=null){
                    link.setYqjName(part.getName());
                    link.setYqjVersion(part.getVersionInfo().getIdentifier().getValue()+"."+part.getIterationInfo().getIdentifier().getValue());
                }
                links.add(link);
            }
            createLinks(links);
        } catch (Exception e) {
            logger.log(e.getLocalizedMessage());
            /*if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);*/
        }
        return list;
    }


    private void createLinks( List<GwChaoMuluMemberLink> links ) throws Exception {
        DBConn conn = new DBConn();
        boolean isClear = true;
        try{
            for (GwChaoMuluMemberLink link : links) {
                if (link != null && !"".equals(link.getOrderNumber()) && isClear) {
                    conn.executeUpdate("delete from GWChaoMuluMemberLink where orderNumber='" + link.getOrderNumber() + "'");
                    conn.commit();
                    isClear = false;
                }
                conn.executeUpdate("insert into GWChaoMuluMemberLink (yqjNumber,orderNumber,bpmWorkflowid,bpmWorkflowNumber,externalTypeId,yqjName,yqjVersion) values ('" + link.getYqjNumber() + "','" + link.getOrderNumber() + "','" + link.getBpmWorkflowid() + "','" + link.getBpmWorkflowNumber() + "','" + link.getExternalTypeId() + "','" + link.getYqjName() + "','" + link.getYqjVersion() + "')");
                conn.commit();
            }
         }catch (Exception e){
            e.printStackTrace();
        }finally {
            conn.close();
        }


    }



}