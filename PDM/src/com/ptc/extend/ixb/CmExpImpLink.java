package com.ptc.extend.ixb;

import com.ptc.extend.ixb.center.MQExpImpConstants;

import wt.build.BuildReference;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.occurrence.OccurrenceableLink;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;

// Referenced classes of package com.netflux.ixb:
//            CmExpImpObject, CmExpImpWTDocumentDependencyLink, CmExpImpWTDocumentUsageLink, CmExpImpWTPartUsageLink,
//            CmExpImpWTPartReferenceLink, CmExpImpWTPartDescribeLink, CmExpImpWTPartAlternateLink, CmExpImpWTPartSubstituteLink,
//            CmExpImpEPMBuildRule, CmExpImpEPMDescribeLink, CmExpImpEPMMemberLink, CmExpImpEPMReferenceLink,
//            CmExpImpEPMBuildHistory, CmImporter, CmExporter

public abstract class CmExpImpLink extends CmExpImpObject {

    Object mainobj;
    protected String type = "CM";



    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	protected CmExpImpLink() {
    }

    protected CmExpImpLink(Object obj, CmExporter expHdl) throws WTException {
        super(expHdl);
        mainobj = obj;
    }
    protected CmExpImpLink(Object obj, CmExporter expHdl,String type) throws WTException {
        super(expHdl);
        mainobj = obj;
        this.type = type;
    }

    protected CmExpImpLink(Object obj, CmImporter impHdl, String fname)
            throws WTException {
        super(impHdl, fname);
        mainobj = obj;
    }
    protected CmExpImpLink(Object obj, CmImporter impHdl, String fname,String type)
            throws WTException {
        super(impHdl, fname);
        mainobj = obj;
        this.type = type;
    }

    protected String getSavePathInJar() {
        return getSavePathInJar(mainobj);
    }

    protected QueryResult getOccurrences(OccurrenceableLink occurrenceablelink)
            throws WTException {
        int ai[] = new int[1];
        QuerySpec queryspec = new QuerySpec(wt.occurrence.UsesOccurrence.class);
        queryspec.appendWhere(new SearchCondition(
                wt.occurrence.UsesOccurrence.class, "linkReference.key.id",
                "=", PersistenceHelper.getObjectIdentifier(occurrenceablelink)
                        .getId()), ai);
        return PersistenceHelper.manager.find(queryspec);
    }

    protected QueryResult getBuiltFromOccurrenceForExport(
            BuildReference buildreference) throws WTException {
        //windchill11升级不支持api
        /**QuerySpec queryspec = new QuerySpec(
                wt.epm.structure.occurrences.EPMUsesOccurrence.class);
        queryspec.appendWhere(
                new SearchCondition(
                        wt.epm.structure.occurrences.EPMUsesOccurrence.class,
                        "usesOccurrenceIdentifier", "=", Long
                        .parseLong(buildreference.getUniqueId())),
                new int[1]);
        return PersistenceHelper.manager.find(queryspec);*/
        return null;
    }

    public static CmExpImpLink newCmExpImpLink(Object obj, CmImporter impHdl,
            String fname) throws WTException {
        if (fname.endsWith("TAG-ChaoMuluMemberLink.xml")) {
            return new CmExpImpChaoMuluMemberLink(obj, impHdl, fname);
        }
        if (fname.endsWith("TAG-WTDocumentDependencyLink.xml")) {
            return new CmExpImpWTDocumentDependencyLink(obj, impHdl, fname);
        }
        if (fname.endsWith("TAG-WTDocumentUsageLink.xml")) {
            return new CmExpImpWTDocumentUsageLink(obj, impHdl, fname);
        }
        if (fname.endsWith("TAG-WTPartUsageLink.xml")) {
            return new CmExpImpWTPartUsageLink(obj, impHdl, fname);
        }
        if (fname.endsWith("TAG-WTPartReferenceLink.xml")) {
            return new CmExpImpWTPartReferenceLink(obj, impHdl, fname);
        }
        if (fname.endsWith("TAG-WTPartDescribeLink.xml")) {
            return new CmExpImpWTPartDescribeLink(obj, impHdl, fname);
        }
        if (fname.endsWith("TAG-WTPartAlternateLink.xml")) {
            return new CmExpImpWTPartAlternateLink(obj, impHdl, fname);
        }
        if (fname.endsWith("TAG-WTPartSubstituteLink.xml")) {
            return new CmExpImpWTPartSubstituteLink(obj, impHdl, fname);
        }
        if (fname.endsWith("TAG-EPMBuildRule.xml")) {
            return new CmExpImpEPMBuildRule(obj, impHdl, fname);
        }
        if (fname.endsWith("TAG-EPMDescribeLink.xml")) {
            return new CmExpImpEPMDescribeLink(obj, impHdl, fname);
        }
        if (fname.endsWith("TAG-EPMMemberLink.xml")) {
            return new CmExpImpEPMMemberLink(obj, impHdl, fname);
        }
        if (fname.endsWith("TAG-EPMReferenceLink.xml")) {
            return new CmExpImpEPMReferenceLink(obj, impHdl, fname);
        }
        if (fname.endsWith("TAG-EPMBuildHistory.xml")) {
            return new CmExpImpEPMBuildHistory(obj, impHdl, fname);
        }
        if (fname.endsWith("TAG-TechNoticeBeforeLink.xml")) {
            return new CmExpImpTechNoticeBeforeLink(obj, impHdl, fname);
        }
        if (fname.endsWith("TAG-TechNoticeAfterLink.xml")) {
            return new CmExpImpTechNoticeAfterLink(obj, impHdl, fname);
        }
        if (fname.endsWith("TAG-EnvelopeMemberLink.xml")) {
            return new CmExpImpEnvelopeMemberLink(obj, impHdl, fname);
        }
        if (fname.endsWith("TAG-ECNAffectItemLink.xml")) {
            return new CmExpImpChangePackagedAffectLink(obj, impHdl, fname);
        }
        if (fname.endsWith("TAG-ECRAffectItemLink.xml")) {
            return new CmExpImpChangeRequestAffectLink(obj, impHdl, fname);
        }
        if (fname.endsWith("TAG-ECNResultItemLink.xml")) {
            return new CmExpImpChangePackagedResultLink(obj, impHdl, fname);
        }
        if (fname.endsWith("TAG-EPMContainedIn.xml")) {
            return new CmExpImpEPMContainedIn(obj, impHdl, fname);
        }
        if (fname.endsWith("TAG-PreMemberLink.xml")) {
            return new CmExpImpPreMemberLink(obj, impHdl, fname);
        }
        if (fname.endsWith("TAG-GLCILink.xml")) {
            return new CmExpImpGLCILink(obj, impHdl, fname);
        }
        if (fname.endsWith("TAG-GLCIPartLink.xml")) {
            return new CmExpImpGLCIPartLink(obj, impHdl, fname);
        }
        if (fname.endsWith("TAG-EPMVariantLink.xml")) {
            return new CmExpImpEPMVariantLink(obj, impHdl, fname);
        } else {
            impHdl.logger((new StringBuilder("==>Import:Unsupport fname=<"))
                    .append(fname).append(">").toString());
            return null;
        }
    }

    public abstract String getRootTag();

    public static String getBuildTypeValue(String val,String tag){
    	if("buildType".equals(tag)){
    		String tmp =  MQExpImpConstants.BUILD_TYPE.get(val);
        	if(tmp!=null&&!"".equals(tmp)){
        		return tmp;
        	}
    	}
    	return val;
    }
}
