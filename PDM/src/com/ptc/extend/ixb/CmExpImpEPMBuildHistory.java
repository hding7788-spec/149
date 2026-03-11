// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 2012/5/24 10:35:03
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   CmExpImpEPMBuildHistory.java

package com.ptc.extend.ixb;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import wt.build.BuildHelper;
import wt.build.BuildReference;
import wt.build.BuildableOccurrence;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.build.EPMBuildHistory;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMMemberLink;
import wt.epm.structure.EPMStructureHelper;
import wt.facade.ixb.IxbElement;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTArrayList;
import wt.ixb.publicforhandlers.IxbHndHelper;
import wt.part.PartUsesOccurrence;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.Iterated;
import wt.vc.Mastered;
import wt.vc.struct.StructHelper;

import com.ptc.extend.util.ObjectProperty;

// Referenced classes of package com.netflux.ixb:
//            CmExpImpLink, CmExpImpConstraints, CmExporter, CmImporter,
//            CmExpImpSearchHelper, MissingObjectException

public class CmExpImpEPMBuildHistory extends CmExpImpLink {

    public CmExpImpEPMBuildHistory(Object obj, CmExporter expHdl)
            throws WTException {
        super(obj, expHdl);
    }

    public CmExpImpEPMBuildHistory(Object obj, CmImporter impHdl, String fname)
            throws WTException {
        super(obj, impHdl, fname);
    }

    public String getRootTag() {
        return CmExpImpConstraints.XML_EPMBUILDHISTORY;
    }

    public void exportObject(Object obj)
            throws WTException {
        if (!(obj instanceof ArrayList)) {
            throw new WTException("Object not ArrayList.");
        } else {
            exportAttribute((ArrayList) obj);
            return;
        }
    }

    private void exportAttribute(ArrayList list)
            throws WTException {
        EPMBuildHistory link;
        for (Iterator it = list.iterator(); it.hasNext(); exportAttribute(link))
            link = (EPMBuildHistory) it.next();

        expHdl.storeDocumentInDir(ixbdocument, getSavePathInJar());
    }

    private void exportAttribute(EPMBuildHistory link)
            throws WTException {
        WTPart wtpart = (WTPart) link.getBuilt();
        IxbElement ixbelement = root.addElement("DataRecord");
        exportLocalIdAttribute(link, ixbelement);
        exportTypeDefinitionAttribute(link, ixbelement);
        ixbelement.addValue("buildTarget/number", emptyIfNull(ObjectProperty.getNumber(wtpart)));
        ixbelement.addValue("buildTarget/version", emptyIfNull(ObjectProperty.getVersion(wtpart)));
        ixbelement.addValue("buildTarget/iteration", emptyIfNull(ObjectProperty.getIteration(wtpart)));
        EPMDocument epmdocument = (EPMDocument) link.getBuiltBy();
        ixbelement.addValue("buildSource/number", emptyIfNull(ObjectProperty.getNumber(epmdocument)));
        ixbelement.addValue("buildSource/version", emptyIfNull(ObjectProperty.getVersion(epmdocument)));
        ixbelement.addValue("buildSource/iteration", emptyIfNull(ObjectProperty.getIteration(epmdocument)));
        ixbelement.addValue("buildType", link.getBuildType());
        ixbelement.addValue("buildRuleID", link.getBuildRuleID());
        for (QueryResult queryresult = StructHelper.service.navigateUses(wtpart, false); queryresult.hasMoreElements();) {
            WTPartUsageLink wtpartusagelink = (WTPartUsageLink) queryresult.nextElement();
            boolean flag = false;
            if (wtpartusagelink.getSourceIdentification() != null
                    && wtpartusagelink.getSourceIdentification().getApplicationTag() != null) {
                IxbElement ixbelement1 = ixbelement.addElement("builtLink");
                Iterated iterated = wtpartusagelink.getUsedBy();
                ixbelement1.addValue("partusagelink/usedBy/number", emptyIfNull(ObjectProperty.getNumber(iterated)));
                ixbelement1.addValue("partusagelink/usedBy/version", emptyIfNull(ObjectProperty.getVersion(iterated)));
                ixbelement1.addValue("partusagelink/usedBy/iteration",
                        emptyIfNull(ObjectProperty.getIteration(iterated)));
                Mastered master = wtpartusagelink.getUses();
                ixbelement1.addValue("partusagelink/uses/number", emptyIfNull(ObjectProperty.getNumber(master)));
                ixbelement1.addValue("partusagelink/uses/version", emptyIfNull(ObjectProperty.getVersion(master)));
                ixbelement1.addValue("partusagelink/uses/iteration", emptyIfNull(ObjectProperty.getIteration(master)));
                String s5 = wtpartusagelink.getSourceIdentification().getUniqueId();
                String s6 = (new StringBuilder()).append("")
                        .append(PersistenceHelper.getObjectIdentifier(wtpartusagelink.getUses()).getId()).toString();
                String s7 = s5;
                int i = s7.indexOf("&&");
                if (i != -1)
                    s7 = s7.substring(0, i);
                if (s6.equals(s7)) {
                    flag = true;
                    ixbelement1.addValue("additionalLink/ObjectReference/number",
                            emptyIfNull(ObjectProperty.getNumber(master)));
                } else {
                    String s9 = s5;
                    int j = s9.indexOf("&&");
                    if (j != -1)
                        s9 = s9.substring(0, j);
                    long l = Long.valueOf(s9).longValue();
                    QuerySpec queryspec = new QuerySpec(wt.epm.EPMDocumentMaster.class);
                    queryspec.appendWhere(new SearchCondition(wt.epm.EPMDocumentMaster.class,
                            "thePersistInfo.theObjectIdentifier.id", "=", l), new int[1]);
                    QueryResult queryresult2 = PersistenceHelper.manager.find(queryspec);
                    // Object obj1 = null;
                    if (queryresult2.hasMoreElements()) {
                        EPMDocumentMaster epmdocumentmaster = (EPMDocumentMaster) queryresult2.nextElement();
                        ixbelement1.addValue("builtFromBuildSourceMaster/number",
                                emptyIfNull(epmdocumentmaster.getNumber()));
                    } else {
                        throw new WTException((new StringBuilder()).append("Master with id ").append(s9)
                                .append(" not found.").toString());
                    }
                }
                for (QueryResult queryresult1 = getOccurrences(wtpartusagelink); queryresult1.hasMoreElements();) {
                    PartUsesOccurrence partusesoccurrence = (PartUsesOccurrence) queryresult1.nextElement();
                    if (partusesoccurrence instanceof BuildableOccurrence) {
                        BuildReference buildreference = partusesoccurrence.getSourceIdentification();
                        if (buildreference != null && buildreference.getApplicationTag() != null)
                            if (flag) {
                                IxbElement ixbelement2 = ixbelement1.addElement("builtOccurrence");
                                ixbelement2.addValue("name", emptyIfNull(partusesoccurrence.getName()));
                            } else {
                                IxbElement ixbelement3 = ixbelement1.addElement("builtOccurrencePair");
                                IxbElement ixbelement4 = ixbelement3.addElement("builtOccurrence");
                                ixbelement4.addValue("name", emptyIfNull(partusesoccurrence.getName()));
                                QueryResult queryresult3 = getBuiltFromOccurrenceForExport(buildreference);
                                if (queryresult3.hasMoreElements()) {
                                    //windchill11升级不支持api
                                    /**EPMUsesOccurrence epmusesoccurrence = (EPMUsesOccurrence) queryresult3
                                            .nextElement();
                                    IxbElement ixbelement5 = ixbelement3.addElement("builtFromOccurrence");
                                    ixbelement5.addValue("name", emptyIfNull(epmusesoccurrence.getName()));*/
                                } else {
                                    // Object obj2 = null;
                                    Long long1 = null;
                                    try {
                                        long1 = Long.valueOf(Long.parseLong(partusesoccurrence.getName().trim()));
                                    } catch (NumberFormatException numberformatexception) {
                                        long1 = null;
                                    }
                                    if (long1 != null) {
                                        EPMMemberLink epmmemberlink = getEPMMemberLinkForExport(epmdocument,
                                                long1.longValue());
                                        Iterated usedByEPM = epmmemberlink.getUsedBy();
                                        ixbelement3.addValue("builtFromLink/usedBy/number",
                                                ObjectProperty.getNumber(usedByEPM));
                                        ixbelement3.addValue("builtFromLink/usedBy/version",
                                                ObjectProperty.getVersion(usedByEPM));
                                        ixbelement3.addValue("builtFromLink/usedBy/iteration",
                                                ObjectProperty.getIteration(usedByEPM));
                                        Mastered mastered = epmmemberlink.getUses();
                                        ixbelement3.addValue("builtFromLink/uses/number",
                                                ObjectProperty.getNumber(mastered));
                                        ixbelement3.addValue("builtFromLink/uses/version",
                                                ObjectProperty.getVersion(mastered));
                                        ixbelement3.addValue("builtFromLink/uses/iteration",
                                                ObjectProperty.getIteration(mastered));
                                        ixbelement3.addValue("builtFromLink/uniqueLinkID", (new StringBuilder())
                                                .append("").append(long1.longValue()).toString());
                                        ixbelement3.addValue("builtFromLink/identifier", epmmemberlink.getIdentifier()
                                                .intValue());
                                    } else {
                                        Object aobj[] = {
                                                partusesoccurrence.getName()
                                        };
                                        wt.ixb.publicforhandlers.LogHelper.IxbException ixbexception = new wt.ixb.publicforhandlers.LogHelper.IxbException(
                                                "wt.ixb.publicforhandlers.ixbResource", "94", aobj);
                                        ixbexception.printStackTrace();
                                        throw ixbexception;
                                    }
                                }
                            }
                    }
                }

            }
        }

    }

    private EPMMemberLink getEPMMemberLinkForExport(EPMDocument epmdocument, long l)
            throws WTException {
        QuerySpec queryspec = new QuerySpec(wt.epm.structure.EPMMemberLink.class);
        queryspec.appendWhere(new SearchCondition(wt.epm.structure.EPMMemberLink.class, "uniqueLinkID", "=", l),
                new int[1]);
        QueryResult queryresult = new QueryResult();
        queryresult.getObjectVectorIfc().getVector().addElement(epmdocument);
        QueryResult queryresult1 = EPMStructureHelper.service.navigateMasterToIteration(queryresult,
                wt.epm.structure.EPMMemberLink.class, queryspec, false, null);
        EPMMemberLink epmmemberlink = null;
        if (queryresult1 != null && queryresult1.hasMoreElements()) {
            Persistable apersistable[] = (Persistable[]) queryresult1.nextElement();
            epmmemberlink = (EPMMemberLink) apersistable[0];
        }
        return epmmemberlink;
    }

    public Object importObject()
            throws WTException {
        return importAttribute();
    }

    public WTArrayList importObjects()
            throws WTException {
        return importAttribute();
    }

    public WTArrayList importAttribute()
            throws WTException {
        WTArrayList list = new WTArrayList();
        try {
            boolean missingobj = false;
            boolean dofailed = false;
            for (Enumeration dataRecords = getElements("DataRecord"); dataRecords.hasMoreElements();) {
                IxbElement data = (IxbElement) dataRecords.nextElement();
                String targetNumber = getNoTrimElementValue(data, "buildTarget/number");
                if (impHdl.isLoopTest())
                    targetNumber = (new StringBuilder(String.valueOf(impHdl.getLoopTestPrefix()))).append(targetNumber)
                            .toString();
                String targetVersion = getElementValue(data, "buildTarget/version");
                targetVersion = getPropertiesValue(targetVersion);
                String targetIteration = getElementValue(data, "buildTarget/iteration");
                WTPart targetPart = (WTPart) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(
                        wt.part.WTPart.class, targetNumber, targetVersion, targetIteration);
                String sourceNumber = getNoTrimElementValue(data, "buildSource/number");
                if (impHdl.isLoopTest())
                    sourceNumber = (new StringBuilder(String.valueOf(impHdl.getLoopTestPrefix()))).append(sourceNumber)
                            .toString();
                String sourceVersion = getElementValue(data, "buildSource/version");
                sourceVersion = getPropertiesValue(sourceVersion);
                String sourceIteration = getElementValue(data, "buildSource/iteration");
                EPMDocument sourceEPM = (EPMDocument) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(
                        wt.epm.EPMDocument.class, sourceNumber, sourceVersion, sourceIteration);
                logger.log((new StringBuilder()).append("==>Import EPMBuildHistory:target:number=<")
                        .append(targetNumber).append("> Ver=").append(targetVersion).append(".")
                        .append(targetIteration).append(" to source:number=<").append(sourceNumber).append("> Ver=")
                        .append(sourceVersion).append(".").append(sourceIteration).toString());
                if (targetPart == null) {
                    logger.log((new StringBuilder("==>Missing WTPart:")).append(targetNumber).append(" ")
                            .append(targetVersion).append(".").append(targetIteration).toString());
                    impHdl.putInMissingObjectSet("WTPart", targetNumber, targetVersion, targetIteration);
                }
                if (sourceEPM == null) {
                    logger.log((new StringBuilder("==>Missing EPMDocument:")).append(sourceNumber).append(" ")
                            .append(sourceVersion).append(".").append(sourceIteration).toString());
                    impHdl.putInMissingObjectSet("EPMDocument", sourceNumber, sourceVersion, sourceIteration);
                }
                if (targetPart == null || sourceEPM == null) {
                    missingobj = true;
                } else {
                    EPMBuildHistory link = CmExpImpSearchHelper.searchEPMBuildHistory(sourceEPM, targetPart);
                    if (link == null) {
                    	 EPMBuildRule ruleTemp = (EPMBuildRule) IxbHndHelper.findAlreadyImportedV2VLink(wt.epm.build.EPMBuildRule.class,
                    			 sourceEPM, targetPart);
                    	 if(ruleTemp!=null){
	                        link = createLink(sourceEPM, targetPart, data);
	                        if (link != null) {
	                            list.add(link);
	                            logger.log("==>EPMBuildHistory import OK!");
	                        } else {
	                            dofailed = true;
	                        }
                    	 }
                    } else {
                        logger.log("==>EPMBuildHistory already imported, IGNORE!");
                    }
                }
            }

            if (missingobj)
                throw new MissingObjectException("==>Missing objects when create EPMBuildHistory.");
            if (dofailed)
                throw new WTException("==>Not All EPMBuildHistory Imported, need to rearrange import process.");
        } catch (Exception e) {
            logger.log(e.getLocalizedMessage());
            if (e instanceof WTException)
                throw (WTException) e;
            else throw new WTException(e);
        }
        return list;
    }

    private EPMBuildHistory createLink(EPMDocument epm, WTPart part, IxbElement ixbelement) {
        Transaction tx = new Transaction();
        try {
            tx.start();
            long l = getBuildRuleID(epm, part);
            EPMBuildHistory epmbuildhistory = EPMBuildHistory.newEPMBuildHistory(epm, part, l,
                    ixbelement.getIntValue("buildType").intValue());
            epmbuildhistory = (EPMBuildHistory) importTypeDefinitionAttribute(epmbuildhistory, ixbelement, ixbelement);
            PersistenceServerHelper.manager.insert(epmbuildhistory);
            tx.commit();
            tx = null;
            epmbuildhistory = (EPMBuildHistory) importObjectAttributesAfterStore(epmbuildhistory, ixbelement);
            EPMBuildHistory localEPMBuildHistory1 = epmbuildhistory;
            return localEPMBuildHistory1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tx != null)
                tx.rollback();
        }
        return null;
    }

    private long getBuildRuleID(EPMDocument epm, WTPart part)
            throws WTException {
        EPMBuildRule rule = (EPMBuildRule) IxbHndHelper.findAlreadyImportedV2VLink(wt.epm.build.EPMBuildRule.class,
                epm, part);
        return rule.getUniqueID();
    }

    private String getApplicationTag(EPMBuildHistory epmbuildhistory, EPMBuildRule epmbuildrule)
            throws WTException {
        return epmbuildrule.getApplicationTag();
    }

    private Object importObjectAttributesAfterStore(Object obj, IxbElement ixbelement)
            throws WTException, UnsupportedEncodingException {
        EPMBuildHistory epmbuildhistory = (EPMBuildHistory) obj;
        epmbuildhistory = (EPMBuildHistory) PersistenceHelper.manager.refresh(epmbuildhistory);
        EPMBuildRule epmbuildrule = (EPMBuildRule) IxbHndHelper.findAlreadyImportedV2VLink(
                wt.epm.build.EPMBuildRule.class, (EPMDocument) epmbuildhistory.getBuiltBy(),
                (WTPart) epmbuildhistory.getBuilt());
        if (epmbuildrule == null)
            return obj;
        String s = getApplicationTag(epmbuildhistory, epmbuildrule);
        for (Enumeration enumeration = ixbelement.getElements("builtLink"); enumeration.hasMoreElements();) {
            IxbElement ixbelement1 = (IxbElement) enumeration.nextElement();
            Persistable persistable = determineWTPartUsageLink(ixbelement1);
            if (persistable != null)
                if (ixbelement1.getElement("additionalLink") != null) {
                    markAdditionalLinkAsBuilt(ixbelement1, persistable, s, epmbuildrule);
                } else {
                    Persistable persistable1 = determineEPMDocumentMaster(ixbelement1);
                    if (persistable1 != null)
                        markUsageLinkAsBuilt(ixbelement1, persistable, persistable1, s, epmbuildrule);
                }
        }

        return obj;
    }

    protected Persistable determineWTPartUsageLink(IxbElement ixbelement)
            throws WTException, UnsupportedEncodingException {
        String usedByNumber = getElementValue(ixbelement, "partusagelink/usedBy/number");
        if (impHdl.isLoopTest())
            usedByNumber = (new StringBuilder(String.valueOf(impHdl.getLoopTestPrefix()))).append(usedByNumber)
                    .toString();
        String usedByVersion = getElementValue(ixbelement, "partusagelink/usedBy/version");
        usedByVersion = getPropertiesValue(usedByVersion);
        String usedByIteration = getElementValue(ixbelement, "partusagelink/usedBy/iteration");
        WTPart usedByPart = (WTPart) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(wt.part.WTPart.class,
                usedByNumber, usedByVersion, usedByIteration);
        if (usedByPart == null) {
            logger.log((new StringBuilder("==>Missing WTPart:")).append(usedByNumber).append(" ").append(usedByVersion)
                    .append(".").append(usedByIteration).toString());
            impHdl.putInMissingObjectSet("WTPart", usedByNumber, usedByVersion, usedByIteration);
            return null;
        }
        String usesNumber = getElementValue(ixbelement, "partusagelink/uses/number");
        if (impHdl.isLoopTest())
            usesNumber = (new StringBuilder(String.valueOf(impHdl.getLoopTestPrefix()))).append(usesNumber).toString();
        WTPart usesPart = (WTPart) CmExpImpSearchHelper.searchLatestIteratedByNumber(wt.part.WTPart.class, usesNumber);
        if (usesPart == null) {
            logger.log((new StringBuilder("==>Missing WTPartMaster:")).append(usesNumber).toString());
            impHdl.putInMissingMasterObjectSet("WTPartMaster", usesNumber);
            return null;
        }
        WTPartMaster master = (WTPartMaster) usesPart.getMaster();
        WTPartUsageLink link = CmExpImpSearchHelper.searchWTPartUsageLink(usedByPart, master);
        if (link == null)
            impHdl.putInMissingObjectSet("WTPart", usedByNumber, usedByVersion, usedByIteration);
        return link;
    }

    protected Persistable determineEPMDocumentMaster(IxbElement ixbelement)
            throws WTException {
        String epmmasternumber = getElementValue(ixbelement, "builtFromBuildSourceMaster/number");
        if (impHdl.isLoopTest())
            epmmasternumber = (new StringBuilder(String.valueOf(impHdl.getLoopTestPrefix()))).append(epmmasternumber)
                    .toString();
        EPMDocumentMaster epmdocumentmaster = (EPMDocumentMaster) CmExpImpSearchHelper.searchMasterObjectByNumber(
                wt.epm.EPMDocumentMaster.class, epmmasternumber);
        return epmdocumentmaster;
    }

    protected void markUsageLinkAsBuilt(IxbElement ixbelement, Persistable persistable, Persistable persistable1,
            String s, EPMBuildRule epmbuildrule)
            throws WTException {
        try {
            String s1 = String.valueOf(persistable1.getPersistInfo().getObjectIdentifier().getId());
            int i = s1.indexOf("&&");
            if (i != -1)
                s1 = s1.substring(0, i);
            BuildReference buildreference = BuildReference.newBuildReference(s, s1);
            ((WTPartUsageLink) persistable).setSourceIdentification(buildreference);
            PersistenceServerHelper.manager.update((WTPartUsageLink) persistable);
            persistable = (WTPartUsageLink) PersistenceHelper.manager.refresh((WTPartUsageLink) persistable);
            buildOccurrences(ixbelement, (WTPartUsageLink) persistable, epmbuildrule);
        } catch (WTPropertyVetoException wtpropertyvetoexception) {
            throw new WTException(wtpropertyvetoexception);
        }
    }

    protected void markAdditionalLinkAsBuilt(IxbElement ixbelement, Persistable persistable, String s,
            EPMBuildRule epmbuildrule)
            throws WTException {
        try {
            String s1 = String.valueOf(((WTPartUsageLink) persistable).getUses().getPersistInfo().getObjectIdentifier()
                    .getId());
            String s2 = (new StringBuilder()).append(s1).append("&&")
                    .append(((WTPartUsageLink) persistable).getQuantity().getUnit()).toString();
            BuildReference buildreference = BuildReference.newBuildReference(s, s2);
            ((WTPartUsageLink) persistable).setSourceIdentification(buildreference);
            PersistenceServerHelper.manager.update((WTPartUsageLink) persistable);
            persistable = (WTPartUsageLink) PersistenceHelper.manager.refresh((WTPartUsageLink) persistable);
            buildOccurrencesOnAdditionalLink(ixbelement, (WTPartUsageLink) persistable, epmbuildrule);
        } catch (WTPropertyVetoException wtpropertyvetoexception) {
            throw new WTException(wtpropertyvetoexception);
        }
    }

    private void buildOccurrences(IxbElement ixbelement,
            WTPartUsageLink wtpartusagelink, EPMBuildRule epmbuildrule)
            throws WTException, WTPropertyVetoException {
        // Object obj = null;
        PartUsesOccurrence partusesoccurrence;
        for (Enumeration enumeration = ixbelement.getElements("builtOccurrencePair"); enumeration.hasMoreElements(); ) {
            IxbElement ixbelement1 = (IxbElement) enumeration.nextElement();
            IxbElement ixbelement2 = ixbelement1.getElement("builtOccurrence");
            String s = getElementValue(ixbelement2, "name");
            IxbElement ixbelement3 = ixbelement1.getElement("builtFromOccurrence");
            if (ixbelement3 != null) {
                String s1 = getElementValue(ixbelement3, "name");
                partusesoccurrence = getNamedOccurrence(wtpartusagelink, s);
                //windchill11升级不支持api
                /**EPMUsesOccurrence epmusesoccurrence = getBuiltFromOccurrenceForImport(
                        partusesoccurrence.getSourceIdentification(), s1, epmbuildrule);
                if (epmusesoccurrence != null)
                    BuildHelper.markAsBuilt(partusesoccurrence,
                            BuildHelper.makeBuildReference(epmbuildrule.getApplicationTag(), epmusesoccurrence));*/
            } else {
                String usedByNumber = getElementValue(ixbelement1, "builtFromLink/usedBy/number");
                if (impHdl.isLoopTest())
                    usedByNumber = (new StringBuilder(String.valueOf(impHdl.getLoopTestPrefix()))).append(usedByNumber)
                            .toString();
                String usedByVersion = getElementValue(ixbelement1, "builtFromLink/usedBy/version");
                if(usedByNumber==null||usedByVersion==null) continue;
                try {
                    usedByVersion = getPropertiesValue(usedByVersion);
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                String usedByIteration = getElementValue(ixbelement1, "builtFromLink/usedBy/iteration");
                EPMDocument usedByEPM = (EPMDocument) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(
                        wt.epm.EPMDocument.class, usedByNumber, usedByVersion, usedByIteration);
                if (usedByEPM == null) {
                    logger.log((new StringBuilder("==>Missing EPMDocument:")).append(usedByNumber).append(" ")
                            .append(usedByVersion).append(".").append(usedByIteration).toString());
                    impHdl.putInMissingObjectSet("EPMDocument", usedByNumber, usedByVersion, usedByIteration);
                    return;
                }
                String usesNumber = getElementValue(ixbelement1, "builtFromLink/uses/number");
                if (impHdl.isLoopTest())
                    usesNumber = (new StringBuilder(String.valueOf(impHdl.getLoopTestPrefix()))).append(usesNumber)
                            .toString();
                EPMDocument usesEPM = (EPMDocument) CmExpImpSearchHelper.searchLatestIteratedByNumber(
                        wt.epm.EPMDocument.class, usesNumber);
                if (usesEPM == null) {
                    logger.log((new StringBuilder("Missing EPMDocumentMaster:")).append(usesNumber).toString());
                    impHdl.putInMissingMasterObjectSet("EPMDocumentMaster", usesNumber);
                    return;
                }
                Integer integer = ixbelement1.getIntValue("builtFromLink/identifier");
                EPMDocumentMaster mastered = (EPMDocumentMaster) usesEPM.getMaster();
                EPMMemberLink epmmemberlink = CmExpImpSearchHelper.searchEPMMemberLink(usedByEPM, mastered,
                        integer.intValue());
                if (epmmemberlink == null)
                    return;
                String s3 = getElementValue(ixbelement1, "builtFromLink/uniqueLinkID");
                s3 = String.valueOf(epmmemberlink.getUniqueLinkID());
                partusesoccurrence = getNamedOccurrence(wtpartusagelink, (new StringBuilder()).append("").append(s3)
                        .toString());
                BuildReference buildreference = BuildReference.newBuildReference(epmbuildrule.getApplicationTag(),
                        (new StringBuilder()).append("").append(epmmemberlink.getUniqueLinkID()).toString());
                if (partusesoccurrence != null) {
                    partusesoccurrence.setName((new StringBuilder()).append("").append(epmmemberlink.getUniqueLinkID())
                            .toString());
                    BuildHelper.markAsBuilt(partusesoccurrence, buildreference);
                }
            }

            PersistenceServerHelper.manager.update(partusesoccurrence);
        }

    }

    private void buildOccurrencesOnAdditionalLink(IxbElement ixbelement, WTPartUsageLink wtpartusagelink,
            EPMBuildRule epmbuildrule)
            throws WTException, WTPropertyVetoException {
        // Object obj = null;
        PartUsesOccurrence partusesoccurrence;
        for (Enumeration enumeration = ixbelement.getElements("builtOccurrence"); enumeration.hasMoreElements(); PersistenceServerHelper.manager
                .update(partusesoccurrence)) {
            IxbElement ixbelement1 = (IxbElement) enumeration.nextElement();
            String s = getElementValue(ixbelement1, "name");
            String s1 = String.valueOf(wtpartusagelink.getUses().getPersistInfo().getObjectIdentifier().getId());
            BuildReference buildreference = BuildReference.newBuildReference(epmbuildrule.getApplicationTag(),
                    (new StringBuilder()).append("").append(s1).append("&&").append(s).toString());
            partusesoccurrence = getNamedOccurrence(wtpartusagelink, s);
            if (partusesoccurrence != null)
                BuildHelper.markAsBuilt(partusesoccurrence, buildreference);
        }

    }

    private PartUsesOccurrence getNamedOccurrence(WTPartUsageLink wtpartusagelink, String s)
            throws WTException {
        QuerySpec queryspec = new QuerySpec(wt.occurrence.UsesOccurrence.class);
        queryspec.appendWhere(new SearchCondition(wt.occurrence.UsesOccurrence.class, "linkReference.key.id", "=",
                PersistenceHelper.getObjectIdentifier(wtpartusagelink).getId()), new int[1]);
        queryspec.appendAnd();
        queryspec.appendWhere(new SearchCondition(wt.occurrence.UsesOccurrence.class, "name", "=", s), new int[1]);
        QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
        if (!queryresult.hasMoreElements())
            return null;
        else return (PartUsesOccurrence) queryresult.nextElement();
    }

    //windchill11升级不支持api
    /**public static EPMUsesOccurrence getBuiltFromOccurrenceForImport(BuildReference buildreference, String s,
            EPMBuildRule epmbuildrule)
            throws WTException {
        QuerySpec queryspec = new QuerySpec(wt.epm.structure.occurrences.EPMUsesOccurrence.class);
        queryspec.appendWhere(
                new SearchCondition(wt.epm.structure.occurrences.EPMUsesOccurrence.class, "name", "=", s), new int[1]);
        queryspec.appendAnd();
        queryspec.appendWhere(new SearchCondition(wt.epm.structure.occurrences.EPMUsesOccurrence.class,
                "contextReference.key.id", "=", PersistenceHelper.getObjectIdentifier(epmbuildrule.getBuildSource())
                        .getId()), new int[1]);
        QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
        if (!queryresult.hasMoreElements())
            return null;
        else return (EPMUsesOccurrence) queryresult.nextElement();
    }*/
}
