package com.ptc.extend.ixb;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.vecmath.Matrix4d;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import wt.configuration.TraceCode;
import wt.facade.ixb.IxbElement;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.SequencePool;
import wt.fc.collections.WTArrayList;
import wt.ixb.impl.doc.IxbDomDocument;
import wt.ixb.impl.doc.IxbDomElement;
import wt.ixb.util.LogicComponentHelper;
import wt.occurrence.OccurrenceHelper;
import wt.occurrence.OccurrenceableLink;
import wt.occurrence.UsesOccurrence;
import wt.occurrence.UsesOccurrenceContext;
import wt.part.LineNumber;
import wt.part.OperationAllocationType;
import wt.part.PartUsesOccurrence;
import wt.part.Quantity;
import wt.part.QuantityUnit;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pom.PersistenceException;
import wt.pom.Transaction;
import wt.pom.UniquenessException;
import wt.util.WTContext;
import wt.util.WTException;
import wt.vc.Iterated;
import wt.vc.Mastered;
import wt.vc.struct.IteratedUsageLink;

import com.ptc.extend.util.ObjectProperty;

import ext.sast.center.synch.MQExpImpUtil;

public class CmExpImpWTPartUsageLink extends CmExpImpLink {
	public static void main(String[] args) {
		try {
			System.out.println(occurrenceDataIdentifierSequencePool.next());
			System.out.println(occurrenceIdentifierSequencePool.next());
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
    private static SequencePool occurrenceDataIdentifierSequencePool = null;
    private static SequencePool occurrenceIdentifierSequencePool = null;
    static
    {
        try
        {
            occurrenceDataIdentifierSequencePool = new SequencePool(wt.occurrence.OccurrenceDataIdentifierSeq.class);
            occurrenceIdentifierSequencePool = new SequencePool(wt.occurrence.OccurrenceIdentifierSeq.class);
        } catch (Throwable throwable)
        {
            throwable.printStackTrace(System.err);
            throw new ExceptionInInitializerError(throwable);
        }
    }

    private long getNextSequence(boolean flag)
            throws WTException
    {
        if (flag)
            return occurrenceDataIdentifierSequencePool.next();
        else return occurrenceIdentifierSequencePool.next();
    }

    public CmExpImpWTPartUsageLink(Object obj, CmExporter expHdl)
            throws WTException {
        super(obj, expHdl);
    }

    public CmExpImpWTPartUsageLink(Object obj, CmImporter impHdl, String fname) throws WTException {
        super(obj, impHdl, fname);
    }

    public String getRootTag() {
        return "WTPartUsageLink";
    }

    public void exportObject(Object obj) throws WTException {
        if (!(obj instanceof ArrayList))
            throw new WTException("Object not ArrayList.");
        exportAttribute((ArrayList) obj);
    }

    private void exportAttribute(ArrayList list) throws WTException {
        Iterator it = list.iterator();
        while (it.hasNext()) {
            WTPartUsageLink link = (WTPartUsageLink) it.next();
            exportAttribute(link);
        }
        this.expHdl.storeDocumentInDir(this.ixbdocument, getSavePathInJar());
    }

    private void exportAttribute(WTPartUsageLink link) throws WTException {
        IxbElement ixbelement = addElement("DataRecord");
        exportLocalIdAttribute(link, ixbelement);
        Iterated iterated = link.getUsedBy();
        ixbelement.addValue("usedBy/number", emptyIfNull(ObjectProperty.getNumber(iterated)));
        ixbelement.addValue("usedBy/version", emptyIfNull(ObjectProperty.getVersion(iterated)));
        ixbelement.addValue("usedBy/iteration", emptyIfNull(ObjectProperty.getIteration(iterated)));
        Mastered master = link.getUses();
        ixbelement.addValue("uses/number", emptyIfNull(ObjectProperty.getNumber(master)));
        ixbelement.addValue("uses/version", emptyIfNull(ObjectProperty.getVersion(master)));
        ixbelement.addValue("uses/iteration", emptyIfNull(ObjectProperty.getIteration(master)));
        exportTypeDefinitionAttribute(link, ixbelement);
        exportIBAAttribute(link, ixbelement);
        exportWTPartUsageLinkAttribute(link, ixbelement);
        exportPartUsesOccurenceAttribute(link, ixbelement);
        exportLogicalComponentAttribute(link, ixbelement);
    }

    private void exportWTPartUsageLinkAttribute(Object obj, IxbElement ixbelement) throws WTException {
        WTPartUsageLink link = (WTPartUsageLink) obj;
        String s = String.valueOf(link.getQuantity().getAmount());
        String s1 = link.getQuantity().getUnit().toString();
        String s2 = link.getTraceCode().toString();
        String s3 = link.getFindNumber();
        LineNumber linenumber = link.getLineNumber();
        String s4 = null;
        if (linenumber != null)
            s4 = link.getLineNumber().toString();
        OperationAllocationType operationallocationtype = link.getAllocationType();
        String s5 = null;
        if (operationallocationtype != null)
            s5 = operationallocationtype.toString();
        ixbelement.addValue("quantityAmount", emptyIfNull(s));
        ixbelement.addValue("quantityUnit", emptyIfNull(s1));
        ixbelement.addValue("traceCode", emptyIfNull(s2));
        if (s3 != null)
            ixbelement.addValue("findNumber", emptyIfNull(s3));
        if (s4 != null)
            ixbelement.addValue("lineNumber", emptyIfNull(s4));
        if (s5 != null)
            ixbelement.addValue("allocationType", emptyIfNull(s5));
    }

    private void exportPartUsesOccurenceAttribute(Object obj, IxbElement ixbelement) throws WTException {
        OccurrenceableLink occurrenceablelink = (OccurrenceableLink) obj;
        QueryResult queryresult = getOccurrences(occurrenceablelink);

        while (queryresult.hasMoreElements()) {
            PartUsesOccurrence partusesoccurrence = (PartUsesOccurrence) queryresult.nextElement();
            IxbElement ixbelement1 = ixbelement.addElement("occurence");
            ixbelement1.addValue("name", emptyIfNull(partusesoccurrence.getName()));
            if (partusesoccurrence.isHasTransform()) {
                Matrix4d matrix4d = partusesoccurrence.toMatrix4d();
                StringBuffer stringbuffer = new StringBuffer();
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        stringbuffer.append(matrix4d.getElement(i, j));
                        if ((i != 3) || (j != 3)) {
                            stringbuffer.append("#");
                        }
                    }
                }

                ixbelement1.addValue("transformString", emptyIfNull(stringbuffer.toString()));
            }
        }
    }

    private void exportLogicalComponentAttribute(Object obj, IxbElement ixbelement) throws WTException {
        WTPartUsageLink link = (WTPartUsageLink) obj;
        String s = link.getComponentId();
        if ((s != null) && (s.trim().length() > 0))
            ixbelement.addValue("componentId", emptyIfNull(s));
        String s1 = link.getReference();
        if ((s1 != null) && (s1.trim().length() > 0))
            ixbelement.addValue("compreference", emptyIfNull(s1));
        String s2 = link.getQuantityOption();
        if ((s2 != null) && (s2.trim().length() > 0))
            ixbelement.addValue("quantityOption", emptyIfNull(s2));
        String s3 = link.getInclusionOption();
        if ((s3 != null) && (s3.trim().length() > 0))
            ixbelement.addValue("inclusionOption", emptyIfNull(s3));
        Hashtable hashtable = link.getAppData();
        if ((hashtable != null) && (hashtable.size() > 0)) {
            Document document = ((IxbDomElement) ixbelement).getDomElement().getOwnerDocument();
            LogicComponentHelper.exportAppData(document, hashtable);
        }
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
                String usedByNumber = getNoTrimElementValue(data, "usedBy/number");
                if (this.impHdl.isLoopTest())
                    usedByNumber = this.impHdl.getLoopTestPrefix() + usedByNumber;
                String usedByVersion = getElementValue(data, "usedBy/version");
                if(!"zyk".equals(getSendFrom())) {
                    usedByVersion = MQExpImpUtil.attrConvertValueBySendFrom("versionInfo", usedByVersion, "IMP", getSendFrom());
                }
                //usedByVersion = getPropertiesValue(usedByVersion);
                String usedByIteration = getElementValue(data, "usedBy/iteration");
                WTPart usedByPart = (WTPart) CmExpImpSearchHelper.searchIteratedByNumberVersionIterationAndView(WTPart.class,
                        usedByNumber, usedByVersion, usedByIteration,"Design");
                String usesNumber = getNoTrimElementValue(data, "uses/number");
                if (this.impHdl.isLoopTest())
                    usesNumber = this.impHdl.getLoopTestPrefix() + usesNumber;
                WTPart usesPart = (WTPart) CmExpImpSearchHelper.searchLatestIteratedByNumberAndView(WTPart.class, usesNumber,"Design");
                logger.log("==>Import WTPartUsageLink:number=<" + usedByNumber +
                        "> Ver=" + usedByVersion + "." + usedByIteration + " to master:number=<" +
                        usesNumber + ">");
                if (!isTypeDefinitionImported()) {
                    logger.log("==>WARNING:Import WTPartUsageLink:TypeDefinition not imported, SKIP!");
                } else {
                    if (usedByPart == null) {
                        logger.log("==>Missing WTPart:" + usedByNumber + " " + usedByVersion + "." + usedByIteration);
                        this.impHdl.putInMissingObjectSet("WTPart", usedByNumber, usedByVersion, usedByIteration);
                    }
                    if (usesPart == null) {
                        logger.log("==>Missing WTPartMaster:" + usesNumber);
                        this.impHdl.putInMissingMasterObjectSet("WTPartMaster", usesNumber);
                    }
                    if ((usedByPart == null) || (usesPart == null)) {
                        missingobj = true;
                    } else {
                        WTPartMaster master = (WTPartMaster) usesPart.getMaster();
                        WTPartUsageLink link = CmExpImpSearchHelper.searchWTPartUsageLink(usedByPart, master);
                        if (link == null) {
                            link = createLink(usedByPart, master, data);
                            if (link != null) {
                                list.add(link);
                                logger.log("==>WTPartUsageLink import OK!");
                            } else {
                                dofailed = true;
                            }
                        } else {
                        	 logger.log("==>WTPartUsageLink already imported, IGNORE!");
                        }
                    }
                }
            }
           /* if (missingobj)
                throw new MissingObjectException("Missing objects");
            if (dofailed)
                throw new WTException("Not All Link Imported, need to rearrange.");*/
        } catch (Exception e) {
            logger.log(e.getMessage());
            throw new WTException(e);
        }
        return list;
    }

    private WTPartUsageLink createLink(WTPart part, WTPartMaster master, IxbElement ixbelement) {
        Transaction tx = new Transaction();
        try {
            tx.start();
            WTPartUsageLink link = WTPartUsageLink.newWTPartUsageLink(part, master);
            link = (WTPartUsageLink) importTypeDefinitionAttribute(link, ixbelement, ixbelement);
            link = (WTPartUsageLink) importIBAAttribute(link, ixbelement);
            link = importWTPartUsageLinkAttribute(link, ixbelement);
            link = importLogicComponentAttribute(link, ixbelement);
            PersistenceServerHelper.manager.insert(link);
            tx.commit();
            tx = null;
            link = importPartUsesOccurenceAttribute(link, ixbelement, part);
            WTPartUsageLink localWTPartUsageLink1 = link;
            return localWTPartUsageLink1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tx != null)
                tx.rollback();
        }
        return null;
    }

    private WTPartUsageLink importWTPartUsageLinkAttribute(Object obj, IxbElement ixbelement) throws WTException {
        try {
            WTPartUsageLink wtpartusagelink = (WTPartUsageLink) obj;

            String s = getElementValue(ixbelement, "quantityAmount");

            String s1 = getElementValue(ixbelement, "quantityUnit");

            if(s1!=null && s1.startsWith("QuantityUnit")){
                s1 = "ea";
            }
            //String s2 = getElementValue(ixbelement, "lineNumber");
            String s3 = getElementValue(ixbelement, "findNumber");
            String s4 = getElementValue(ixbelement, "traceCode");
            if("UNTRACED".equals(s4)){
            	s4="0";
            }
            String s5 = getElementValue(ixbelement, "allocationType");
            wtpartusagelink.setQuantity(Quantity.newQuantity(Double.valueOf(s).doubleValue(),
                    QuantityUnit.toQuantityUnit(s1)));
            wtpartusagelink.setTraceCode(TraceCode.toTraceCode(s4));
            //if (s2 != null)
                //wtpartusagelink.setLineNumber(LineNumber.newLineNumber(Long.valueOf(s2).longValue()));
            if (s3 != null) {
                wtpartusagelink.setFindNumber(s3);
                WTPartUsageLink[] awtpartusagelink = { wtpartusagelink };
                WTPartHelper.service.validateFindNumbers(awtpartusagelink);
            }
            if (s5 != null)
                wtpartusagelink.setAllocationType(OperationAllocationType.toOperationAllocationType(s5));
            return wtpartusagelink;
        } catch (Exception e) {
            e.printStackTrace();
            if ((e instanceof WTException)) {
                throw ((WTException) e);
            } else {
                throw new WTException(e);
            }
        }
    }

    private WTPartUsageLink importLogicComponentAttribute(Object obj, IxbElement ixbelement) throws WTException {
        try {
            WTPartUsageLink wtpartusagelink = (WTPartUsageLink) obj;
            String s = getElementValue(ixbelement, "componentId");
            if ((s != null) && (!s.equals(""))) {
                Field[] afield = wtpartusagelink.getClass().getDeclaredFields();
                Field[] afield1 = afield;
                int i = afield1.length;
                int j = 0;

                while (j < i) {
                    Field field = afield1[j];
                    if (field.getName().equals("componentId")) {
                        field.setAccessible(true);
                        field.set(wtpartusagelink, s);
                        break;
                    }
                    j++;
                }
            }
            String s1 = getElementValue(ixbelement, "compreference");
            if ((s1 != null) && (s1.trim().length() > 0))
                wtpartusagelink.setReference(s1);
            String s2 = getElementValue(ixbelement, "quantityOption");
            if ((s2 != null) && (s2.trim().length() > 0))
                wtpartusagelink.setQuantityOption(s2);
            String s3 = getElementValue(ixbelement, "inclusionOption");
            if ((s3 != null) && (s3.trim().length() > 0))
                wtpartusagelink.setInclusionOption(s3);
            Document document = null;
            if ((ixbelement instanceof IxbDomElement))
                document = ((IxbDomElement) ixbelement).getDomElement().getOwnerDocument();
            else if ((ixbelement instanceof IxbDomDocument))
                document = ((IxbDomDocument) ixbelement).getDom();
            if (document != null) {
                NodeList nodelist = document.getElementsByTagName(LogicComponentHelper.getAppDataTagName());
                if ((nodelist != null) && (nodelist.getLength() > 0) && (nodelist.item(0) != null)) {
                    Locale locale = WTContext.getContext().getLocale();
                    Hashtable hashtable = LogicComponentHelper.importAppData((Element) nodelist.item(0), locale);
                    wtpartusagelink.setAppData(hashtable);
                }
            }
            return wtpartusagelink;
        } catch (Exception e) {
            e.printStackTrace();
            if ((e instanceof WTException)) {
                throw ((WTException) e);
            } else {
                throw new WTException(e);
            }
        }
    }

    private WTPartUsageLink importPartUsesOccurenceAttribute(WTPartUsageLink obj, IxbElement ixbelement, WTPart part) {
        Transaction tx = new Transaction();
        try {
            OccurrenceableLink occurrenceablelink = obj;
            Enumeration enumeration = ixbelement.getElements("occurence");
            if(enumeration != null) {
                tx.start();
                obj = (WTPartUsageLink) PersistenceHelper.manager.lockAndRefresh(obj);
                while(enumeration.hasMoreElements()) {
                    IxbElement ixbelement1 = (IxbElement) enumeration.nextElement();
                    String s = getElementValue(ixbelement1, "name");
                    String s1 = getElementValue(ixbelement1, "transformString");
                    PartUsesOccurrence partusesoccurrence = null;
                    if(s1 == null) {
                        partusesoccurrence = PartUsesOccurrence.newPartUsesOccurrence(occurrenceablelink);
                    } else {
                        Matrix4d matrix4d = stringToMatrix4d(s1);
                        partusesoccurrence = PartUsesOccurrence.newPartUsesOccurrence(occurrenceablelink, matrix4d);
                    }
                    partusesoccurrence.setName(s);

                    /*try{
                    	Integer componentID = Integer.parseInt(s);
                    	partusesoccurrence.setComponentID(componentID);
                    }catch(Exception e){
                    	e.printStackTrace();
                    }*/
                    UsesOccurrence usesoccurrence = partusesoccurrence;
                    if(occurrenceablelink instanceof IteratedUsageLink) {
                        IteratedUsageLink iteratedusagelink = (IteratedUsageLink) occurrenceablelink;
                        Iterated iterated = iteratedusagelink.getUsedBy();
                        usesoccurrence.setContext((UsesOccurrenceContext) iterated);
                    }
                    usesoccurrence.setUsesOccurrenceDataIdentifier(getNextSequence(true));
                    usesoccurrence.setUsesOccurrenceIdentifier(getNextSequence(false));
                    PersistenceServerHelper.manager.insert(usesoccurrence);
                }
                tx.commit();
                tx = null;
                obj = (WTPartUsageLink) PersistenceHelper.manager.refresh(obj);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    private WTPartUsageLink importPartUsesOccurenceAttribute_org(WTPartUsageLink obj, IxbElement ixbelement) {
        Transaction tx = new Transaction();
        try {
            OccurrenceableLink occurrenceablelink = obj;
            Enumeration enumeration = ixbelement.getElements("occurence");
            if (enumeration != null) {
                tx.start();
                obj = (WTPartUsageLink) PersistenceHelper.manager.lockAndRefresh(obj);
                while (enumeration.hasMoreElements()) {
                    IxbElement ixbelement1 = (IxbElement) enumeration.nextElement();
                    String s = getElementValue(ixbelement1, "name");
                    String s1 = getElementValue(ixbelement1, "transformString");
                    PartUsesOccurrence partusesoccurrence = null;
                    if (s1 == null) {
                        partusesoccurrence = PartUsesOccurrence.newPartUsesOccurrence(occurrenceablelink);
                    } else {
                        Matrix4d matrix4d = stringToMatrix4d(s1);
                        partusesoccurrence = PartUsesOccurrence.newPartUsesOccurrence(occurrenceablelink, matrix4d);
                    }
                    partusesoccurrence.setName(s);
                    try {
                        OccurrenceHelper.service.saveUsesOccurrenceAndData(partusesoccurrence, null);
                    } catch (UniquenessException uniquenessexception) {
                        logger.log("Exception in importAttribute of ExpImpForPartUsesOccurenceAttr :");
                        uniquenessexception.printStackTrace();
                    } catch (PersistenceException persistenceexception) {
                        logger.log("Exception in importAttribute of ExpImpForPartUsesOccurenceAttr :");
                        persistenceexception.printStackTrace();
                    }
                }
                tx.commit();
                tx = null;
                obj = (WTPartUsageLink) PersistenceHelper.manager.refresh(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    private Matrix4d stringToMatrix4d(String s) {
        Matrix4d matrix4d = null;
        double[][] ad = new double[4][4];
        StringTokenizer stringtokenizer = new StringTokenizer(s, "#");
        int i = 0;
        int j = 0;
        while (stringtokenizer.hasMoreTokens()) {
            String s1 = stringtokenizer.nextToken();
            ad[i][j] = Double.valueOf(s1).doubleValue();
            if (j < 3) {
                j++;
            } else {
                i++;
                j = 0;
            }
        }
        matrix4d = new Matrix4d(ad[0][0], ad[0][1], ad[0][2], ad[0][3], ad[1][0], ad[1][1], ad[1][2], ad[1][3],
                ad[2][0], ad[2][1], ad[2][2], ad[2][3], ad[3][0], ad[3][1], ad[3][2], ad[3][3]);
        return matrix4d;
    }
}