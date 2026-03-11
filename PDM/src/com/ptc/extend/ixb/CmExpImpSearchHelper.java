package com.ptc.extend.ixb;

import com.ptc.extend.util.Debug;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.ases.technotice.TechNoticeAfterLink;
import ext.ases.technotice.TechNoticeBeforeLink;
import ext.casc.preview.Preview;
import ext.casc.preview.PreviewObject;
import ext.sast.catalog.GLCILink;
import ext.sast.catalog.GLCIPartLink;
import ext.sast.catalog.GLCatalog;
import ext.sast.catalog.GLPartLink;
import ext.sast.navigation.GLClassificationNode;
import ext.sast.supply.GLSupply;
import wt.content.HolderToContent;
import wt.doc.*;
import wt.enterprise.Master;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocConfigSpec;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.build.EPMBuildHistory;
import wt.epm.build.EPMBuildRule;
import wt.epm.build.EPMDerivedRepHistory;
import wt.epm.familytable.EPMSepFamilyTable;
import wt.epm.familytable.EPMSepFamilyTableMaster;
import wt.epm.navigator.CollectItem;
import wt.epm.navigator.EPMNavigateHelper;
import wt.epm.navigator.relationship.UIRelationships;
import wt.epm.structure.*;
import wt.epm.util.EPMContainerHelper;
import wt.epm.workspaces.EPMAsStoredConfig;
import wt.epm.workspaces.EPMAsStoredMember;
import wt.epm.workspaces.EPMWorkspace;
import wt.fc.ObjectVector;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTSet;
import wt.iba.definition.IBADefinitionException;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value._StringValue;
import wt.inf.container.WTContainerRef;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.org.WTPrincipal;
import wt.part.*;
import wt.pds.StatementSpec;
import wt.pom.WTConnection;
import wt.query.*;
import wt.sandbox.SandboxConfigSpec;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.Typed;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.Iterated;
import wt.vc.VersionControlHelper;
import wt.vc.baseline.Baselineable;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.wip.WorkInProgressState;

import java.rmi.RemoteException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;

public class CmExpImpSearchHelper implements RemoteAccess {
    static String CLASSNAME = CmExpImpSearchHelper.class.getName();
	private static Object link;

    public static Object searchMasterObjectByNumber(Class klass, String number) {
        try {
            QuerySpec qs = new QuerySpec(klass);
            qs.appendWhere(new SearchCondition(klass, "number", "=", number), new int[1]);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements())
                return qr.nextElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList searchAllWTDocumentDependencyLink(WTDocument doc) {
        ArrayList list = new ArrayList();
        try {
            QuerySpec qs = new QuerySpec(WTDocumentDependencyLink.class);
            qs.appendWhere(new SearchCondition(WTDocumentDependencyLink.class, "roleAObjectRef.key.id", "=",
                    PersistenceHelper.getObjectIdentifier(doc).getId()), new int[1]);
            qs.appendOr();
            qs.appendWhere(new SearchCondition(WTDocumentDependencyLink.class, "roleBObjectRef.key.id", "=",
                    PersistenceHelper.getObjectIdentifier(doc).getId()), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                list.addAll(qr.getObjectVectorIfc().getVector());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static WTDocumentDependencyLink searchWTDocumentDependencyLink(WTDocument describes, WTDocument describedby) {
        try {
            QuerySpec qs = new QuerySpec(WTDocumentDependencyLink.class);
            qs.appendWhere(new SearchCondition(WTDocumentDependencyLink.class, "roleAObjectRef.key.id", "=",
                    PersistenceHelper.getObjectIdentifier(describes).getId()), new int[1]);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(WTDocumentDependencyLink.class, "roleBObjectRef.key.id", "=",
                    PersistenceHelper.getObjectIdentifier(describedby).getId()), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                return (WTDocumentDependencyLink) qr.nextElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList searchAllWTDocumentUsageLink(WTDocument doc) {
        ArrayList list = new ArrayList();
        try {
            QuerySpec qs = new QuerySpec(WTDocumentUsageLink.class);
            qs.appendWhere(new SearchCondition(WTDocumentUsageLink.class, "roleAObjectRef.key.id", "=",
                    PersistenceHelper.getObjectIdentifier(doc).getId()), new int[1]);
            qs.appendOr();
            qs.appendWhere(new SearchCondition(WTDocumentUsageLink.class, "roleBObjectRef.key.id", "=",
                    PersistenceHelper.getObjectIdentifier(doc.getMaster()).getId()), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                list.addAll(qr.getObjectVectorIfc().getVector());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static WTDocumentUsageLink searchWTDocumentUsageLink(WTDocument doc, WTDocumentMaster mastered) {
        try {
            QuerySpec qs = new QuerySpec(WTDocumentUsageLink.class);
            qs.appendWhere(new SearchCondition(WTDocumentUsageLink.class, "roleAObjectRef.key.id", "=",
                    PersistenceHelper.getObjectIdentifier(doc).getId()), new int[1]);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(WTDocumentUsageLink.class, "roleBObjectRef.key.id", "=",
                    PersistenceHelper.getObjectIdentifier(mastered).getId()), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                return (WTDocumentUsageLink) qr.nextElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList searchAllWTPartAlternateLink(WTPart part) {
        ArrayList list = new ArrayList();
        try {
            WTPartMaster master = (WTPartMaster) part.getMaster();
            QuerySpec qs = new QuerySpec(WTPartAlternateLink.class);
            qs.appendWhere(new SearchCondition(WTPartAlternateLink.class, "roleAObjectRef.key.id", "=",
                    PersistenceHelper.getObjectIdentifier(master).getId()), new int[1]);
            qs.appendOr();
            qs.appendWhere(new SearchCondition(WTPartAlternateLink.class, "roleBObjectRef.key.id", "=",
                    PersistenceHelper.getObjectIdentifier(master).getId()), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                list.addAll(qr.getObjectVectorIfc().getVector());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static WTPartAlternateLink searchWTPartAlternateLink(WTPartMaster alternateFor, WTPartMaster alternates) {
        try {
            QuerySpec qs = new QuerySpec(WTPartAlternateLink.class);
            qs.appendWhere(new SearchCondition(WTPartAlternateLink.class, "roleAObjectRef.key.id", "=",
                    PersistenceHelper.getObjectIdentifier(alternateFor).getId()), new int[1]);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(WTPartAlternateLink.class, "roleBObjectRef.key.id", "=",
                    PersistenceHelper.getObjectIdentifier(alternates).getId()), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                return (WTPartAlternateLink) qr.nextElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList searchAllWTPartSubstituteLink(WTPart part) {
        ArrayList list = new ArrayList();
        try {
            WTPartMaster master = (WTPartMaster) part.getMaster();
            QuerySpec qs = new QuerySpec(WTPartSubstituteLink.class);
            qs.appendWhere(new SearchCondition(WTPartSubstituteLink.class, "roleBObjectRef.key.id", "=",
                    PersistenceHelper.getObjectIdentifier(master).getId()), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                list.addAll(qr.getObjectVectorIfc().getVector());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static WTPartSubstituteLink searchWTPartSubstituteLink(WTPart part, WTPartUsageLink usagelink) {
        try {
            QuerySpec qs = new QuerySpec(WTPartSubstituteLink.class);
            qs.appendWhere(new SearchCondition(WTPartAlternateLink.class, "roleAObjectRef.key.id", "=",
                    PersistenceHelper.getObjectIdentifier(usagelink).getId()), new int[1]);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(WTPartAlternateLink.class, "roleBObjectRef.key.id", "=",
                    PersistenceHelper.getObjectIdentifier((WTPartMaster) part.getMaster()).getId()), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                return (WTPartSubstituteLink) qr.nextElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList searchAllWTPartUsageLink(WTPart part) {
        ArrayList list = new ArrayList();
        try {
            QuerySpec qs = new QuerySpec(WTPartUsageLink.class);
            qs.appendWhere(new SearchCondition(WTPartUsageLink.class, "roleAObjectRef.key.id", "=", PersistenceHelper
                    .getObjectIdentifier(part).getId()), new int[1]);
            qs.appendOr();
            qs.appendWhere(new SearchCondition(WTPartUsageLink.class, "roleBObjectRef.key.id", "=", PersistenceHelper
                    .getObjectIdentifier(part.getMaster()).getId()), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                list.addAll(qr.getObjectVectorIfc().getVector());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static WTPartUsageLink searchWTPartUsageLink(WTPart part, WTPartMaster mastered) {
        try {
            QuerySpec qs = new QuerySpec(WTPartUsageLink.class);
            qs.appendWhere(new SearchCondition(WTPartUsageLink.class, "roleAObjectRef.key.id", "=", PersistenceHelper
                    .getObjectIdentifier(part).getId()), new int[1]);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(WTPartUsageLink.class, "roleBObjectRef.key.id", "=", PersistenceHelper
                    .getObjectIdentifier(mastered).getId()), new int[1]);

            qs.setAdvancedQueryEnabled(true);

            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                return (WTPartUsageLink) qr.nextElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList searchAllWTPartDescribeLink(WTPart part) {
        ArrayList list = new ArrayList();
        try {
            QuerySpec qs = new QuerySpec(WTPartDescribeLink.class);
            qs.appendWhere(new SearchCondition(WTPartDescribeLink.class, "roleAObjectRef.key.id", "=",
                    PersistenceHelper.getObjectIdentifier(part).getId()), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                list.addAll(qr.getObjectVectorIfc().getVector());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList searchAllWTPartDescribeLink(WTDocument doc) {
        ArrayList list = new ArrayList();
        try {
            QuerySpec qs = new QuerySpec(WTPartDescribeLink.class);
            qs.appendWhere(new SearchCondition(WTPartDescribeLink.class, "roleBObjectRef.key.id", "=",
                    PersistenceHelper.getObjectIdentifier(doc).getId()), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                list.addAll(qr.getObjectVectorIfc().getVector());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static WTPartDescribeLink searchWTPartDescribeLink(WTPart part, WTDocument doc) {
        try {
            QuerySpec qs = new QuerySpec(WTPartDescribeLink.class);
            qs.appendWhere(new SearchCondition(WTPartDescribeLink.class, "roleAObjectRef.key.id", "=",
                    PersistenceHelper.getObjectIdentifier(part).getId()), new int[1]);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(WTPartDescribeLink.class, "roleBObjectRef.key.id", "=",
                    PersistenceHelper.getObjectIdentifier(doc).getId()), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                return (WTPartDescribeLink) qr.nextElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList searchAllWTPartReferenceLink(WTPart part) {
        ArrayList list = new ArrayList();
        try {
            QuerySpec qs = new QuerySpec(WTPartReferenceLink.class);
            qs.appendWhere(new SearchCondition(WTPartReferenceLink.class, "roleAObjectRef.key.id", "=",
                    PersistenceHelper.getObjectIdentifier(part).getId()), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                list.addAll(qr.getObjectVectorIfc().getVector());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList searchAllWTPartReferenceLink(WTDocument doc) {
        ArrayList list = new ArrayList();
        try {
            QuerySpec qs = new QuerySpec(WTPartReferenceLink.class);
            qs.appendWhere(new SearchCondition(WTPartReferenceLink.class, "roleBObjectRef.key.id", "=",
                    PersistenceHelper.getObjectIdentifier(doc.getMaster()).getId()), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                list.addAll(qr.getObjectVectorIfc().getVector());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static WTPartReferenceLink searchWTPartReferenceLink(WTPart part, WTDocumentMaster mastered) {
        try {
            QuerySpec qs = new QuerySpec(WTPartReferenceLink.class);
            qs.appendWhere(new SearchCondition(WTPartReferenceLink.class, "roleAObjectRef.key.id", "=",
                    PersistenceHelper.getObjectIdentifier(part).getId()), new int[1]);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(WTPartReferenceLink.class, "roleBObjectRef.key.id", "=",
                    PersistenceHelper.getObjectIdentifier(mastered).getId()), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                return (WTPartReferenceLink) qr.nextElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList searchAllEPMDescribeLink(EPMDocument epm) {
        ArrayList list = new ArrayList();
        try {
            QuerySpec qs = new QuerySpec(EPMDescribeLink.class);
            qs.appendWhere(new SearchCondition(EPMDescribeLink.class, "roleBObjectRef.key.id", "=", PersistenceHelper
                    .getObjectIdentifier(epm).getId()), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                list.addAll(qr.getObjectVectorIfc().getVector());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList searchAllEPMDescribeLink(WTPart part) {
        ArrayList list = new ArrayList();
        try {
            QuerySpec qs = new QuerySpec(EPMDescribeLink.class);
            qs.appendWhere(new SearchCondition(EPMDescribeLink.class, "roleAObjectRef.key.id", "=", PersistenceHelper
                    .getObjectIdentifier(part).getId()), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                list.addAll(qr.getObjectVectorIfc().getVector());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static EPMVariantLink searchEPMVariantLink(EPMDocument epm, EPMDocumentMaster mastered) {
	    try {
	      QuerySpec qs = new QuerySpec(EPMVariantLink.class);
	      qs.appendWhere(new SearchCondition(EPMVariantLink.class, "roleAObjectRef.key.id", "=", PersistenceHelper.getObjectIdentifier(epm).getId()), new int[1]);
	      qs.appendAnd();
	      qs.appendWhere(new SearchCondition(EPMVariantLink.class, "roleBObjectRef.key.id", "=", PersistenceHelper.getObjectIdentifier(mastered).getId()), new int[1]);
	      qs.setAdvancedQueryEnabled(true);
	      QueryResult qr = PersistenceHelper.manager.find(qs);
	      if (qr.size() > 0)
	        return (EPMVariantLink)qr.nextElement();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return null;
	  }

    public static ArrayList searchEPMVariantLink(EPMDocumentMaster mastered) {
        ArrayList list = new ArrayList();
        try {
            QuerySpec qs = new QuerySpec(EPMVariantLink.class);
            qs.appendWhere(new SearchCondition(EPMVariantLink.class,
                    "roleBObjectRef.key.id", "=", PersistenceHelper
                    .getObjectIdentifier(mastered).getId()), new int[1]);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                list.addAll(qr.getObjectVectorIfc().getVector());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public static EPMContainedIn searchEPMContainedIn(EPMDocument doc,EPMSepFamilyTable table) {
	    try {
	      QuerySpec qs = new QuerySpec(EPMContainedIn.class);
	      qs.appendWhere(new SearchCondition(EPMContainedIn.class, "roleAObjectRef.key.id", "=", PersistenceHelper.getObjectIdentifier(doc).getId()), new int[1]);
	      qs.appendAnd();
	      qs.appendWhere(new SearchCondition(EPMContainedIn.class, "roleBObjectRef.key.id", "=", PersistenceHelper.getObjectIdentifier(table).getId()), new int[1]);
	      QueryResult qr = PersistenceHelper.manager.find(qs);
	      if (qr.size() > 0)
	        return (EPMContainedIn)qr.nextElement();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return null;
	  }
    public static WTSet searchAllEPMContainedIn(EPMSepFamilyTable table) {
    	WTSet set = new WTHashSet();
	    try {
	      QuerySpec qs = new QuerySpec(EPMContainedIn.class);
	      qs.appendWhere(new SearchCondition(EPMContainedIn.class, "roleBObjectRef.key.id", "=", PersistenceHelper.getObjectIdentifier(table).getId()), new int[1]);
	      QueryResult qr = PersistenceHelper.manager.find(qs);
	      while (qr.hasMoreElements())
	    	  set.add((EPMContainedIn)qr.nextElement());
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return set;
	  }
    public static EPMSepFamilyTable findAlreadyImportedVersionedObject(
			String name, String version, String iteration,
			WTContainerRef wtcontainerref) throws WTException {
		QuerySpec queryspec = new QuerySpec();
		int i = queryspec.appendClassList(EPMSepFamilyTable.class, true);
		int j = queryspec.appendClassList(EPMSepFamilyTableMaster.class, false);
		queryspec.appendWhere(new SearchCondition(
				EPMSepFamilyTableMaster.class, "name", "=", name),
				new int[] { j });
		queryspec.appendAnd();
		queryspec.appendWhere(new SearchCondition(
				EPMSepFamilyTableMaster.class,
				"thePersistInfo.theObjectIdentifier.id",
				EPMSepFamilyTable.class, "masterReference.key.id"), new int[] {
				j, i });
		if (version != null) {
			queryspec.appendAnd();
			queryspec.appendWhere(new SearchCondition(EPMSepFamilyTable.class,
					"versionInfo.identifier.versionId", "=", version, false),
					new int[] { i });
		}
		if (iteration != null) {
			queryspec.appendAnd();
			queryspec.appendWhere(new SearchCondition(EPMSepFamilyTable.class,
					"iterationInfo.identifier.iterationId", "=", iteration),
					new int[] { i });
		}
//		ContainerSpec containerspec = new ContainerSpec();
//		try {
//			wt.inf.container.WTContainer wtcontainer = WTContainerHelper.service
//					.getNamespace(EPMSepFamilyTableMaster.class,
//							wtcontainerref.getReferencedContainer());
//			WTContainerRef wtcontainerref1 = WTContainerRef
//					.newWTContainerRef(wtcontainer);
//			containerspec.addSearchContainer(wtcontainerref1);
//			containerspec.setFilterByNamespace(true);
//			queryspec.setAdvancedQueryEnabled(true);
//			queryspec.appendAnd();
//			queryspec.appendWhere(WTContainerHelper.getWhereContainerIn(
//					containerspec, new Class[] { EPMSepFamilyTable.class }),
//					new int[] { i });
//		} catch (WTPropertyVetoException wtpropertyvetoexception) {
//			throw new WTException(wtpropertyvetoexception);
//		}
		QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
		EPMSepFamilyTable epmsepfamilytable = null;
		if (queryresult.hasMoreElements()) {
			Persistable apersistable[] = (Persistable[]) (Persistable[]) queryresult
					.nextElement();
			epmsepfamilytable = (EPMSepFamilyTable) apersistable[0];
		}
		return epmsepfamilytable;
	}
    public static EPMDescribeLink searchEPMDescribeLink(WTPart part, EPMDocument epm) {
        try {
            QuerySpec qs = new QuerySpec(EPMDescribeLink.class);
            qs.appendWhere(new SearchCondition(EPMDescribeLink.class, "roleAObjectRef.key.id", "=", PersistenceHelper
                    .getObjectIdentifier(part).getId()), new int[1]);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(EPMDescribeLink.class, "roleBObjectRef.key.id", "=", PersistenceHelper
                    .getObjectIdentifier(epm).getId()), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                return (EPMDescribeLink) qr.nextElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void ttt(WTPart wtpart) {
        try {
            Debug.P("==>Active:");
            WTSet wtset = (WTSet) EPMNavigateHelper.navigate(wtpart, UIRelationships.newAssociatedCADDocs(true),
                    new CollectItem[] { CollectItem.OTHERSIDE, CollectItem.LINK }).getResults(new WTHashSet(),
                    new String[0]);
            Iterator it = wtset.iterator();
            while (it.hasNext())
                Debug.P("==>Search:", it.next());
            Debug.P("==>Passive:");
            wtset = (WTSet) EPMNavigateHelper.navigate(wtpart, UIRelationships.newAssociatedCADDocs(false),
                    new CollectItem[] { CollectItem.OTHERSIDE, CollectItem.LINK }).getResults(new WTHashSet(),
                    new String[0]);
            it = wtset.iterator();
            while (it.hasNext())
                Debug.P("==>Search:", it.next());
        } catch (Exception localException) {
        }
    }

    public static ArrayList searchAllEPMReferenceLink(EPMDocument epm) {
        ArrayList list = new ArrayList();
        try {
            QuerySpec qs = new QuerySpec(EPMReferenceLink.class);
            qs.appendWhere(new SearchCondition(EPMReferenceLink.class, "roleAObjectRef.key.id", "=", PersistenceHelper
                    .getObjectIdentifier(epm).getId()), new int[1]);
            qs.appendOr();
            qs.appendWhere(new SearchCondition(EPMReferenceLink.class, "roleBObjectRef.key.id", "=", PersistenceHelper
                    .getObjectIdentifier(epm.getMaster()).getId()), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                list.addAll(qr.getObjectVectorIfc().getVector());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList searchAllEPMReferenceLink(DocumentMaster mastered) {
        ArrayList list = new ArrayList();
        if ((!(mastered instanceof WTDocumentMaster)) && (!(mastered instanceof EPMDocumentMaster)))
            return list;
        try {
            QuerySpec qs = new QuerySpec(EPMReferenceLink.class);
            if ((mastered instanceof WTDocumentMaster))
                qs.appendWhere(new SearchCondition(EPMReferenceLink.class, "roleBObjectRef.key.id", "=",
                        PersistenceHelper.getObjectIdentifier((WTDocumentMaster) mastered).getId()), new int[1]);
            else if ((mastered instanceof EPMDocumentMaster)) {
                qs.appendWhere(new SearchCondition(EPMReferenceLink.class, "roleBObjectRef.key.id", "=",
                        PersistenceHelper.getObjectIdentifier((EPMDocumentMaster) mastered).getId()), new int[1]);
            }
            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                list.addAll(qr.getObjectVectorIfc().getVector());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static EPMReferenceLink searchEPMReferenceLink(EPMDocument epm, DocumentMaster mastered) {
        if ((!(mastered instanceof WTDocumentMaster)) && (!(mastered instanceof EPMDocumentMaster)))
            return null;
        try {
            QuerySpec qs = new QuerySpec(EPMReferenceLink.class);
            qs.appendWhere(new SearchCondition(EPMReferenceLink.class, "roleAObjectRef.key.id", "=", PersistenceHelper
                    .getObjectIdentifier(epm).getId()), new int[1]);
            qs.appendAnd();
            if ((mastered instanceof WTDocumentMaster))
                qs.appendWhere(new SearchCondition(EPMReferenceLink.class, "roleBObjectRef.key.id", "=",
                        PersistenceHelper.getObjectIdentifier((WTDocumentMaster) mastered).getId()), new int[1]);
            else if ((mastered instanceof EPMDocumentMaster)) {
                qs.appendWhere(new SearchCondition(EPMReferenceLink.class, "roleBObjectRef.key.id", "=",
                        PersistenceHelper.getObjectIdentifier((EPMDocumentMaster) mastered).getId()), new int[1]);
            }
            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                return (EPMReferenceLink) qr.nextElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList searchAllEPMMemberLink(EPMDocument epm) {
        ArrayList list = new ArrayList();
        try {
            QuerySpec qs = new QuerySpec(EPMMemberLink.class);
            qs.appendWhere(new SearchCondition(EPMMemberLink.class, "roleAObjectRef.key.id", "=", PersistenceHelper
                    .getObjectIdentifier(epm).getId()), new int[1]);
            qs.appendOr();
            qs.appendWhere(new SearchCondition(EPMMemberLink.class, "roleBObjectRef.key.id", "=", PersistenceHelper
                    .getObjectIdentifier(epm.getMaster()).getId()), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                list.addAll(qr.getObjectVectorIfc().getVector());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static EPMMemberLink searchEPMMemberLink(EPMDocument epm, EPMDocumentMaster mastered, int identifier) {
        try {
            QuerySpec qs = new QuerySpec(EPMMemberLink.class);
            qs.appendWhere(new SearchCondition(EPMMemberLink.class, "roleAObjectRef.key.id", "=", PersistenceHelper
                    .getObjectIdentifier(epm).getId()), new int[1]);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(EPMMemberLink.class, "roleBObjectRef.key.id", "=", PersistenceHelper
                    .getObjectIdentifier(mastered).getId()), new int[1]);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(EPMMemberLink.class, "identifier", "=", identifier), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                return (EPMMemberLink) qr.nextElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList searchAllEPMBuildRule(EPMDocument epm) {
        ArrayList list = new ArrayList();
        try {
            QuerySpec qs = new QuerySpec(EPMBuildRule.class);
            qs.appendWhere(new SearchCondition(EPMBuildRule.class, "roleAObjectRef.key.branchId", "=",
                    VersionControlHelper.getBranchIdentifier(epm)), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                list.addAll(qr.getObjectVectorIfc().getVector());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList searchAllEPMBuildRule(WTPart part) {
        ArrayList list = new ArrayList();
        try {
            QuerySpec qs = new QuerySpec(EPMBuildRule.class);
            qs.appendWhere(new SearchCondition(EPMBuildRule.class, "roleBObjectRef.key.branchId", "=",
                    VersionControlHelper.getBranchIdentifier(part)), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                list.addAll(qr.getObjectVectorIfc().getVector());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static EPMBuildRule searchEPMBuildRule(EPMDocument epm, WTPart part) {
        try {
            QuerySpec qs = new QuerySpec(EPMBuildRule.class);
            qs.appendWhere(new SearchCondition(EPMBuildRule.class, "roleAObjectRef.key.branchId", "=",
                    VersionControlHelper.getBranchIdentifier(epm)), new int[1]);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(EPMBuildRule.class, "roleBObjectRef.key.branchId", "=",
                    VersionControlHelper.getBranchIdentifier(part)), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                return (EPMBuildRule) qr.nextElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList searchAllEPMBuildHistory(EPMDocument epm) {
        ArrayList list = new ArrayList();
        try {
            QuerySpec qs = new QuerySpec(EPMBuildHistory.class);
            qs.appendWhere(new SearchCondition(EPMBuildHistory.class, "roleAObjectRef.key.id", "=", PersistenceHelper
                    .getObjectIdentifier(epm).getId()), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                list.addAll(qr.getObjectVectorIfc().getVector());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList searchAllEPMBuildHistory(WTPart part) {
        ArrayList list = new ArrayList();
        try {
            QuerySpec qs = new QuerySpec(EPMBuildHistory.class);
            qs.appendWhere(new SearchCondition(EPMBuildHistory.class, "roleBObjectRef.key.id", "=", PersistenceHelper
                    .getObjectIdentifier(part).getId()), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                list.addAll(qr.getObjectVectorIfc().getVector());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static EPMBuildHistory searchEPMBuildHistory(EPMDocument epm, WTPart part) {
        try {
            QuerySpec qs = new QuerySpec(EPMBuildHistory.class);
            qs.appendWhere(new SearchCondition(EPMBuildHistory.class, "roleAObjectRef.key.id", "=", PersistenceHelper
                    .getObjectIdentifier(epm).getId()), new int[1]);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(EPMBuildHistory.class, "roleBObjectRef.key.id", "=", PersistenceHelper
                    .getObjectIdentifier(part).getId()), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                return (EPMBuildHistory) qr.nextElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList searchAllEPMDerivedRepHistory(EPMDocument epm) {
        ArrayList list = new ArrayList();
        try {
            QuerySpec qs = new QuerySpec(EPMDerivedRepHistory.class);
            qs.appendWhere(new SearchCondition(EPMDerivedRepHistory.class, "roleAObjectRef.key.id", "=",
                    PersistenceHelper.getObjectIdentifier(epm).getId()), new int[1]);
            qs.appendOr();
            qs.appendWhere(new SearchCondition(EPMDerivedRepHistory.class, "roleBObjectRef.key.id", "=",
                    PersistenceHelper.getObjectIdentifier(epm).getId()), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            while (qr.hasMoreElements()) {
                EPMDerivedRepHistory link = (EPMDerivedRepHistory) qr.nextElement();
                if (((link.getBuilt() instanceof EPMDocument)) && ((link.getBuiltBy() instanceof EPMDocument)))
                    list.add(link);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static EPMBuildHistory searchEPMDerivedRepHistory(EPMDocument epm, EPMDocument epm2) {
        try {
            QuerySpec qs = new QuerySpec(EPMBuildHistory.class);
            qs.appendWhere(new SearchCondition(EPMBuildHistory.class, "roleAObjectRef.key.id", "=", PersistenceHelper
                    .getObjectIdentifier(epm).getId()), new int[1]);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(EPMBuildHistory.class, "roleBObjectRef.key.id", "=", PersistenceHelper
                    .getObjectIdentifier(epm2).getId()), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                return (EPMBuildHistory) qr.nextElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Iterated searchIteratedByNumberVersionIteration(Class klass, String number, String version,
            String iteration) {
        boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            QuerySpec qs = new QuerySpec(klass);
            qs.appendWhere(new SearchCondition(klass, "master>number", "=", number.toUpperCase()), new int[1]);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(klass, "versionInfo.identifier.versionId", "=", version), new int[1]);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(klass, "iterationInfo.identifier.iterationId", "=", iteration),
                    new int[1]);
            qs.setAdvancedQueryEnabled(true);

            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                Iterated localIterated = (Iterated) qr.nextElement();
                return localIterated;
            }
        } catch (WTException wte) {
            wte.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessFlag);
        }
        SessionServerHelper.manager.setAccessEnforced(accessFlag);

        return null;
    }

    public static Iterated searchIteratedByNumberCADNameVersionIteration(Class klass,String number, String cadName, String version,
            String iteration) {
        boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            QuerySpec qs = new QuerySpec(klass);

            qs.appendOpenParen();
            qs.appendWhere(new SearchCondition(klass, "master>number", "=", number.toUpperCase()), new int[1]);
            qs.appendOr();
            qs.appendWhere(new SearchCondition(klass, "master>CADName", "=", cadName), new int[1]);
            qs.appendCloseParen();

            qs.appendAnd();
            qs.appendWhere(new SearchCondition(klass, "versionInfo.identifier.versionId", "=", version), new int[1]);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(klass, "iterationInfo.identifier.iterationId", "=", iteration),
                    new int[1]);
            qs.setAdvancedQueryEnabled(true);

            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                Iterated localIterated = (Iterated) qr.nextElement();
                return localIterated;
            }
        } catch (WTException wte) {
            wte.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessFlag);
        }
        SessionServerHelper.manager.setAccessEnforced(accessFlag);

        return null;
    }

    public static Iterated searchIteratedByNumberVersionIterationAndView(Class klass, String number, String version,
            String iteration,String viewname) {
        boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            QuerySpec qs = new QuerySpec(klass);
            qs.appendWhere(new SearchCondition(klass, "master>number", "=", number.toUpperCase()), new int[1]);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(klass, "versionInfo.identifier.versionId", "=", version), new int[1]);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(klass, "iterationInfo.identifier.iterationId", "=", iteration),
                    new int[1]);

            if ((viewname != null) && (!viewname.equals(""))) {
                qs.appendAnd();
                View view = ViewHelper.service.getView(viewname);
                qs.appendWhere(
                        new SearchCondition(WTPart.class, "view.key.id",
                                "=", view.getPersistInfo().getObjectIdentifier().getId()), new int[1]);
            }
            qs.setAdvancedQueryEnabled(true);

            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                Iterated localIterated = (Iterated) qr.nextElement();
                return localIterated;
            }
        } catch (WTException wte) {
            wte.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessFlag);
        }
        SessionServerHelper.manager.setAccessEnforced(accessFlag);

        return null;
    }

    public static View getViewByName(String viewName) throws WTException {
    	int index[] = { 0 };
		View view = null;
		QuerySpec qs = new QuerySpec(View.class);
		qs.appendWhere(new SearchCondition(View.class, View.NAME, SearchCondition.EQUAL, viewName, false), index);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.hasMoreElements()) {
			view = (View) qr.nextElement();
		}
		return view;
	}
    public static Iterated searchWTPartByNumberVersionIterationView(String number, String version, String iteration,
            String viewname) {
        boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            QuerySpec qs = new QuerySpec(WTPart.class);
            qs.appendWhere(new SearchCondition(WTPart.class, "master>number", "=", number.toUpperCase()), new int[1]);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(WTPart.class, "versionInfo.identifier.versionId", "=", version),
                    new int[1]);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(WTPart.class, "iterationInfo.identifier.iterationId", "=", iteration),
                    new int[1]);
            if ((viewname != null) && (!viewname.equals(""))) {
                qs.appendAnd();
                View view = ViewHelper.service.getView(viewname);
                qs.appendWhere(
                        new SearchCondition(WTPart.class, "view.key.id",
                                "=", view.getPersistInfo().getObjectIdentifier().getId()), new int[1]);
            }
            qs.setAdvancedQueryEnabled(true);

            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                Iterated localIterated = (Iterated) qr.nextElement();
                return localIterated;
            }
        } catch (WTException wte) {
            wte.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessFlag);
        }
        SessionServerHelper.manager.setAccessEnforced(accessFlag);

        return null;
    }
    public static Iterated searchWTPartByNumberVersionView(String number, String version,
            String viewname) {
        boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            QuerySpec qs = new QuerySpec(WTPart.class);
            qs.appendWhere(new SearchCondition(WTPart.class, "master>number", "=", number.toUpperCase()), new int[1]);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(WTPart.class, "versionInfo.identifier.versionId", "=", version),
                    new int[1]);

            if ((viewname != null) && (!viewname.equals(""))) {
                qs.appendAnd();
                View view = ViewHelper.service.getView(viewname);
                qs.appendWhere(
                        new SearchCondition(WTPart.class, "view.key.id",
                                "=", view.getPersistInfo().getObjectIdentifier().getId()), new int[1]);
            }
            qs.setAdvancedQueryEnabled(true);

            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                Iterated localIterated = (Iterated) qr.nextElement();
                return localIterated;
            }
        } catch (WTException wte) {
            wte.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessFlag);
        }
        SessionServerHelper.manager.setAccessEnforced(accessFlag);

        return null;
    }
    public static Iterated searchLatestIteratedByNumberVersion(Class klass, String number, String version) {
        boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            QuerySpec qs = new QuerySpec(klass);
            qs.appendWhere(new SearchCondition(klass, "master>number", "=", number.toUpperCase()), new int[1]);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(klass, "versionInfo.identifier.versionId", "=", version), new int[1]);
            qs = new LatestConfigSpec().appendSearchCriteria(qs);
            qs.setAdvancedQueryEnabled(true);

            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                Iterated localIterated = VersionControlHelper.getLatestIteration((Iterated) qr.nextElement(), true);
                return localIterated;
            }
        } catch (WTException wte) {
            wte.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessFlag);
        }
        SessionServerHelper.manager.setAccessEnforced(accessFlag);

        return null;
    }

    public static Iterated searchLatestIteratedByNumberCADNameVersion(Class klass, String number,String cadName, String version) {
        boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            QuerySpec qs = new QuerySpec(klass);

            qs.appendOpenParen();
            qs.appendWhere(new SearchCondition(klass, "master>number", "=", number.toUpperCase()), new int[1]);
            qs.appendOr();
            qs.appendWhere(new SearchCondition(klass, "master>CADName", "=", cadName), new int[1]);
            qs.appendCloseParen();

            qs.appendAnd();
            qs.appendWhere(new SearchCondition(klass, "versionInfo.identifier.versionId", "=", version), new int[1]);
            qs = new LatestConfigSpec().appendSearchCriteria(qs);
            qs.setAdvancedQueryEnabled(true);

            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                Iterated localIterated = VersionControlHelper.getLatestIteration((Iterated) qr.nextElement(), true);
                return localIterated;
            }
        } catch (WTException wte) {
            wte.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessFlag);
        }
        SessionServerHelper.manager.setAccessEnforced(accessFlag);

        return null;
    }


    public static Iterated searchLatestIteratedByNumber(Class klass, String number) {
        boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            QuerySpec qs = new QuerySpec(klass);
            qs.appendWhere(new SearchCondition(klass, "master>number", "=", number.toUpperCase()), new int[1]);

            ClassAttribute ca = new ClassAttribute(klass, "versionInfo.identifier.versionId");
            OrderBy orderby = new OrderBy(ca, true);
            qs.appendOrderBy(orderby, 0);
            qs = new LatestConfigSpec().appendSearchCriteria(qs);
            qs.setAdvancedQueryEnabled(true);

            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                Iterated localIterated = (Iterated) qr.nextElement();
                return localIterated;
            }
        } catch (WTException wte) {
            wte.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessFlag);
        }
        SessionServerHelper.manager.setAccessEnforced(accessFlag);

        return null;
    }

    public static Iterated searchLatestIteratedByNumberCADName(Class klass, String number,String cadName) {
        boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            QuerySpec qs = new QuerySpec(klass);

            qs.appendOpenParen();
            qs.appendWhere(new SearchCondition(klass, "master>number", "=", number.toUpperCase()), new int[1]);
            qs.appendOr();
            qs.appendWhere(new SearchCondition(klass, "master>CADName", "=", cadName), new int[1]);
            qs.appendCloseParen();

            ClassAttribute ca = new ClassAttribute(klass, "versionInfo.identifier.versionId");
            OrderBy orderby = new OrderBy(ca, true);
            qs.appendOrderBy(orderby, 0);
            qs = new LatestConfigSpec().appendSearchCriteria(qs);
            qs.setAdvancedQueryEnabled(true);

            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                Iterated localIterated = (Iterated) qr.nextElement();
                return localIterated;
            }
        } catch (WTException wte) {
            wte.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessFlag);
        }
        SessionServerHelper.manager.setAccessEnforced(accessFlag);

        return null;
    }
    public static Iterated searchLatestIteratedByNumberAndView(Class klass, String number,String viewname) {
        boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            QuerySpec qs = new QuerySpec(klass);
            qs.appendWhere(new SearchCondition(klass, "master>number", "=", number.toUpperCase()), new int[1]);

            if ((viewname != null) && (!viewname.equals(""))) {
                qs.appendAnd();
                View view = ViewHelper.service.getView(viewname);
                qs.appendWhere(
                        new SearchCondition(WTPart.class, "view.key.id",
                                "=", view.getPersistInfo().getObjectIdentifier().getId()), new int[1]);
            }
            ClassAttribute ca = new ClassAttribute(klass, "versionInfo.identifier.versionId");
            OrderBy orderby = new OrderBy(ca, true);
            qs.appendOrderBy(orderby, 0);
            qs = new LatestConfigSpec().appendSearchCriteria(qs);
            qs.setAdvancedQueryEnabled(true);

            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                Iterated localIterated = (Iterated) qr.nextElement();
                return localIterated;
            }
        } catch (WTException wte) {
            wte.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessFlag);
        }
        SessionServerHelper.manager.setAccessEnforced(accessFlag);

        return null;
    }

    public static Iterated searchLatestWTPartByNumberView(String number, String viewname) {
        boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            QuerySpec qs = new QuerySpec(WTPart.class);
            qs.appendWhere(new SearchCondition(WTPart.class, "master>number", "=", number.toUpperCase()), new int[1]);
            if ((viewname != null) && (!viewname.equals(""))) {
                qs.appendAnd();
                View view = ViewHelper.service.getView(viewname);
                qs.appendWhere(
                        new SearchCondition(WTPart.class, "view.key.id",
                                "=", view.getPersistInfo().getObjectIdentifier().getId()), new int[1]);
            }
            ClassAttribute ca = new ClassAttribute(WTPart.class, "versionInfo.identifier.versionId");
            OrderBy orderby = new OrderBy(ca, true);
            qs.appendOrderBy(orderby, 0);
            qs = new LatestConfigSpec().appendSearchCriteria(qs);
            qs.setAdvancedQueryEnabled(true);

            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                Iterated localIterated = (Iterated) qr.nextElement();
                return localIterated;
            }
        } catch (WTException wte) {
            wte.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessFlag);
        }
        SessionServerHelper.manager.setAccessEnforced(accessFlag);

        return null;
    }

    public static boolean docHasContents(EPMDocument epmdocument) {
        try {
            QuerySpec queryspec = new QuerySpec();
            int i = queryspec.appendClassList(HolderToContent.class, false);
            queryspec.appendSelectAttribute("roleAObjectRef.key.id", i, false);
            Long long1 = new Long(epmdocument.getPersistInfo().getObjectIdentifier().getId());
            queryspec.appendWhere(new SearchCondition(HolderToContent.class, "roleAObjectRef.key.id", "=", long1), 0,
                    -1);
            queryspec.appendAnd();
            queryspec.appendWhere(new SearchCondition(HolderToContent.class, "roleAObjectRef.key.classname", "=",
                    "wt.epm.EPMDocument"), 0, -1);
            QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
            if (queryresult.size() != 0)
                return true;
        } catch (WTException wtexception) {
            wtexception.printStackTrace();
        }
        return false;
    }

    public static EPMAsStoredConfig getAsStoredConfig(Baselineable baselineable) throws WTException {
        QuerySpec queryspec = new QuerySpec();
        // int i = queryspec.addClassList(EPMAsStoredConfig.class, true);
        queryspec.addClassList(EPMAsStoredConfig.class, true);
        int j = queryspec.addClassList(EPMAsStoredMember.class, false);
        queryspec.appendWhere(new SearchCondition(EPMAsStoredMember.class, "owner", "TRUE"), new int[] { j });
        QueryResult queryresult = PersistenceHelper.manager.navigate(baselineable, "theEPMAsStoredConfig", queryspec);
        int k = queryresult.size();
        if (k == 0)
            return null;
        if (k > 1) {
            throw new WTException("Internal Error: More than 1 owner link for a document found");
        }
        return (EPMAsStoredConfig) queryresult.nextElement();
    }

    private static QueryResult getMembers(EPMAsStoredConfig epmasstoredconfig) throws WTException {
        QuerySpec queryspec = new QuerySpec();
        // int i = queryspec.addClassList(Baselineable.class, true);
        queryspec.addClassList(Baselineable.class, true);
        int j = queryspec.addClassList(EPMAsStoredMember.class, false);
        queryspec.appendWhere(new SearchCondition(EPMAsStoredMember.class, "substitute", "FALSE"), new int[] { j });
        return PersistenceHelper.manager.navigate(epmasstoredconfig, "theBaselineable", queryspec);
    }

    public static QueryResult findCheckedOutIterations(EPMDocumentMaster epmdocumentmaster) throws WTException {
        QuerySpec queryspec = new QuerySpec();
        int i = queryspec.addClassList(EPMDocument.class, true);
        queryspec.appendWhere(
                new SearchCondition(EPMDocument.class, "masterReference.key", "=", PersistenceHelper
                        .getObjectIdentifier(epmdocumentmaster)), new int[] { i, i });
        queryspec.appendAnd();
        queryspec.appendWhere(new SearchCondition(EPMDocument.class, "checkoutInfo.state", "=",
                WorkInProgressState.WORKING), new int[] { i, i });
        QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
        ObjectVector objectvector = new ObjectVector();
        while (queryresult.hasMoreElements())
            objectvector.addElement(((wt.fc.Persistable[]) queryresult.nextElement())[0]);
        return new QueryResult(objectvector);
    }

    public static EPMWorkspace getWorkspace(String s, WTContainerRef wtcontainerref) throws WTException,
            WTPropertyVetoException {
        QuerySpec queryspec = new QuerySpec(EPMWorkspace.class);
        queryspec.appendWhere(new SearchCondition(EPMWorkspace.class, "name", "=", s), new int[1]);
        WTPrincipal wtprincipal = SessionHelper.manager.getPrincipal();
        queryspec.appendAnd();
        queryspec.appendWhere(new SearchCondition(EPMWorkspace.class, "principalReference.key.id", "=",
                PersistenceHelper.getObjectIdentifier(wtprincipal).getId()), new int[1]);
        QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
        EPMWorkspace epmworkspace = null;
        if (queryresult.hasMoreElements()) {
            epmworkspace = (EPMWorkspace) queryresult.nextElement();
            Debug.P("Workspace found: " + epmworkspace);
        } else {
            Debug.P("Creating workspace " + s);
            WTPartStandardConfigSpec wtpartstandardconfigspec = WTPartStandardConfigSpec.newWTPartStandardConfigSpec();
            WTPartConfigSpec wtpartconfigspec = WTPartConfigSpec.newWTPartConfigSpec(null, null,
                    wtpartstandardconfigspec);
            EPMDocConfigSpec epmdocconfigspec = EPMDocConfigSpec.newEPMDocConfigSpec();
            if (EPMContainerHelper.isProject(wtcontainerref)) {
                SandboxConfigSpec sandboxconfigspec = SandboxConfigSpec.newSandboxConfigSpec(wtcontainerref, true,
                        true, null);
                epmdocconfigspec.setSandboxConfig(sandboxconfigspec);
                epmdocconfigspec.setSandboxActive(true);
                wtpartconfigspec.setSandbox(sandboxconfigspec);
                wtpartconfigspec.setSandboxActive(true);
            }

        }

        return epmworkspace;
    }

    public static EPMDocConfigSpec newDocConfigSpec(WTContainerRef wtcontainerref) throws WTException {
        EPMDocConfigSpec epmdocconfigspec = null;
        try {
            epmdocconfigspec = EPMDocConfigSpec.newEPMDocConfigSpec();
            epmdocconfigspec.setWorkingIncluded(false);
        } catch (WTPropertyVetoException wtpropertyvetoexception) {
            throw new WTException(wtpropertyvetoexception);
        }
        if ((wtcontainerref != null) && (EPMContainerHelper.isProject(wtcontainerref)))
            try {
                SandboxConfigSpec sandboxconfigspec = SandboxConfigSpec.newSandboxConfigSpec(wtcontainerref, true,
                        false, null, null);
                epmdocconfigspec.setSandboxConfig(sandboxconfigspec);
                epmdocconfigspec.setSandboxActive(true);
            } catch (WTPropertyVetoException wtpropertyvetoexception1) {
                throw new WTException(wtpropertyvetoexception1);
            }
        return epmdocconfigspec;
    }

    /**
     * @param doc
     * @return
     * @since liaojun
     */
    public static ArrayList searchAllTechNoticeBeforeLink(WTDocument doc) {
        ArrayList list = new ArrayList();
        try {
            QueryResult qr = PersistenceHelper.manager.navigate(doc,
                    "beforeObject", TechNoticeBeforeLink.class, false);
            if (qr.size() > 0)
                list.addAll(qr.getObjectVectorIfc().getVector());
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return list;
    }

    /**
     * @param doc
     * @return
     * @since liaojun
     */
    public static ArrayList searchAllTechNoticeAfterLink(WTDocument doc) {
        ArrayList list = new ArrayList();
        try {
            QueryResult qr = PersistenceHelper.manager.navigate(doc,
                    "afterObject", TechNoticeAfterLink.class, false);
            if (qr.size() > 0)
                list.addAll(qr.getObjectVectorIfc().getVector());
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return list;
    }

    /**
     * @param doc
     * @param revision
     * @return
     * @since liaojun
     */
    public static TechNoticeBeforeLink searchTechNoticeBeforeLink(WTDocument doc, RevisionControlled revision) {
        try {
            QuerySpec qs = new QuerySpec(TechNoticeBeforeLink.class);
            qs.appendWhere(new SearchCondition(TechNoticeBeforeLink.class, "roleAObjectRef.key.branchId", "=",
                    PersistenceHelper.getObjectIdentifier(doc).getId()), new int[1]);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(TechNoticeBeforeLink.class, "roleBObjectRef.key.branchId", "=",
                    VersionControlHelper.getBranchIdentifier(revision)), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                return (TechNoticeBeforeLink) qr.nextElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param doc
     * @param revision
     * @return
     * @since liaojun
     */
    public static TechNoticeAfterLink searchTechNoticeAfterLink(WTDocument doc, RevisionControlled revision) {
        try {
            QuerySpec qs = new QuerySpec(TechNoticeAfterLink.class);
            qs.appendWhere(new SearchCondition(TechNoticeAfterLink.class, "roleAObjectRef.key.branchId", "=",
                    PersistenceHelper.getObjectIdentifier(doc).getId()), new int[1]);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(TechNoticeAfterLink.class, "roleBObjectRef.key.branchId", "=",
                    VersionControlHelper.getBranchIdentifier(revision)), new int[1]);

            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                return (TechNoticeAfterLink) qr.nextElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param pe
     * @param revision
     * @return
     * @since liaojun
     */
    public static EnvelopeMemberLink searchEnvelopeMemberLink(ProcessEnvelope pe, RevisionControlled revision) {
        try {
            QuerySpec qs = new QuerySpec(EnvelopeMemberLink.class);
            qs.appendWhere(new SearchCondition(EnvelopeMemberLink.class, "roleAObjectRef.key.id", "=",
                    PersistenceHelper.getObjectIdentifier(pe).getId()), new int[1]);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(EnvelopeMemberLink.class, "roleBObjectRef.key.branchId", "=",
                    VersionControlHelper.getBranchIdentifier(revision)), new int[1]);
            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.size() > 0)
                return (EnvelopeMemberLink) qr.nextElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param pe
     * @return
     * @since liaojun
     */
    public static ArrayList searchAllEnvelopeMemberLink(ProcessEnvelope pe) {

        ArrayList list = new ArrayList();
        try {
            QueryResult qr = PersistenceHelper.manager.navigate(pe,
                      "theRevisionControlled", EnvelopeMemberLink.class, false);
            if (qr.size() > 0)
                list.addAll(qr.getObjectVectorIfc().getVector());
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return list;
    }

    public static ProcessEnvelope getProcessEnvelopeByNumber(String number, String type) {
        try {
            QuerySpec qs = new QuerySpec(ProcessEnvelope.class);
            qs.appendWhere(new SearchCondition(ProcessEnvelope.class, ProcessEnvelope.NUMBER, "=", number), new int[1]);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            while (qr.hasMoreElements()) {
                ProcessEnvelope pe = (ProcessEnvelope) qr.nextElement();
                String s = TypedUtilityServiceHelper.service.getExternalTypeIdentifier((Typed) pe);
                if (s.equals(type)) {
                    return pe;
                }
            }
        } catch (WTException wte) {
            wte.printStackTrace();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
        }
        return null;
    }

    public static ProcessEnvelope getProcessEnvelopeByNumber(String number) {
        try {
            QuerySpec qs = new QuerySpec(ProcessEnvelope.class);
            qs.appendWhere(new SearchCondition(ProcessEnvelope.class, ProcessEnvelope.NUMBER, "=", number,false), new int[1]);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            while (qr.hasMoreElements()) {
                ProcessEnvelope pe = (ProcessEnvelope) qr.nextElement();
                 return pe;
            }
        } catch (WTException wte) {
            wte.printStackTrace();
        }  finally {
        }
        return null;
    }


    public static ChangePackaged getChangePackagedByNumber(String number) {
        try {
            QuerySpec qs = new QuerySpec(ChangePackaged.class);
            qs.appendWhere(new SearchCondition(ChangePackaged.class, ChangePackaged.NUMBER, "=", number,false), new int[1]);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                ChangePackaged changePackaged = (ChangePackaged) qr.nextElement();
                return changePackaged;
            }
        } catch (WTException wte) {
            wte.printStackTrace();
        } finally {
        }
        return null;
    }

    public static ChangeRequest getChangeRequestByNumber(String number) {
        // TODO Auto-generated method stub
        try {
            QuerySpec qs = new QuerySpec(ChangeRequest.class);
            qs.appendWhere(new SearchCondition(ChangeRequest.class, ChangeRequest.NUMBER, "=", number), new int[1]);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                ChangeRequest cr = (ChangeRequest) qr.nextElement();
                return cr;
            }
        } catch (WTException wte) {
            wte.printStackTrace();
        } finally {
        }
        return null;
    }

	public static Preview getPreviewByNumber(String number) {
		// TODO Auto-generated method stub
		 try {
	            QuerySpec qs = new QuerySpec(Preview.class);
	            qs.appendWhere(new SearchCondition(Preview.class, Preview.NUMBER, "=", number), new int[1]);
	            QueryResult qr = PersistenceHelper.manager.find(qs);
	            if (qr.hasMoreElements()) {
	            	Preview preview = (Preview) qr.nextElement();
	                return preview;
	            }
	        } catch (WTException wte) {
	            wte.printStackTrace();
	        } finally {
	        }
	        return null;
	}

	public static PreviewObject searchPreviewObject(
			Class<PreviewObject> class1, String objNumber, String version,
			String objType) {
		// TODO Auto-generated method stub
		try {
	        QuerySpec qs = new QuerySpec(PreviewObject.class);
	        qs.appendWhere(new SearchCondition(PreviewObject.class, PreviewObject.NUMBER, "=", objNumber), new int[1]);
	        qs.appendAnd();
	        qs.appendWhere(new SearchCondition(PreviewObject.class, PreviewObject.OBJ_VER, "=", version), new int[1]);
	        qs.appendAnd();
	        qs.appendWhere(new SearchCondition(PreviewObject.class, PreviewObject.OBJ_TYPE, "=", objType), new int[1]);
	        QueryResult qr = PersistenceHelper.manager.find(qs);
	        if (qr.hasMoreElements()) {
	        	PreviewObject previewObject = (PreviewObject) qr.nextElement();
	            return previewObject;
	        }
    } catch (WTException wte) {
        wte.printStackTrace();
    } finally {
    }
    return null;
	}

	public static GLClassificationNode getGLClassificationNodeByNumber(
			String number) {
		GLClassificationNode node = null;
		String sql = "select * from GLCLASSIFICATIONNODE where NODENAME='" + number + "'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		WTConnection wtconnection = null;
		try {
			SessionServerHelper.manager.setAccessEnforced(false);
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			pstmt = wtconnection.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				node = new GLClassificationNode();
				node.setNumber(number);
				node.setId(rs.getLong("IDA2A2"));
				node.setParentId(rs.getLong("TOPID"));
			}
			rs.close();
			pstmt.close();

		} catch (Exception ex) {
			ex.printStackTrace();
			SessionServerHelper.manager.setAccessEnforced(true);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			SessionServerHelper.manager.setAccessEnforced(true);
		}
		return node;
	}

	public static GLCatalog getGLCatalogByNumber(String name) {
		GLCatalog ci = null;
		try {
			QuerySpec qs = new QuerySpec(GLCatalog.class);
			SearchCondition sc = new SearchCondition(GLCatalog.class, GLCatalog.CATALOGNAME, SearchCondition.EQUAL,
					name, false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.hasMoreElements()) {
				ci = (GLCatalog) qr.nextElement();
			}
		} catch (Exception ex) {
			ci = null;
			// System.out.println("CSCPart.class Method=getPartMasterByNumber Exception Message = "
			// + ex.getMessage());
			// //Debug
		}
		return ci;
	}

	public static GLSupply getGLSupplyByNumber(String number) {
		GLSupply supply = null;
		try {
			QuerySpec qs = new QuerySpec(GLSupply.class);
			SearchCondition sc = new SearchCondition(GLSupply.class, GLSupply.NUMBER, SearchCondition.EQUAL,
					number, false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.hasMoreElements()) {
				supply = (GLSupply) qr.nextElement();
			}
		} catch (Exception ex) {
			supply = null;
			// System.out.println("CSCPart.class Method=getPartMasterByNumber Exception Message = "
			// + ex.getMessage());
			// //Debug
		}
		return supply;
	}

	public static GLCILink getGLCILink(String GLCATALOGNUMBER,
			String cIPARTNUMBER, String gLSUPPLIERS,String GLCATALOGNAME) {
		GLCILink link = null;
		String sql = "select * from GLCILINK where GLCATALOGNUMBER='" + GLCATALOGNUMBER + "' AND CIPARTNUMBER='"+cIPARTNUMBER+"' AND GLSUPPLIERS='"+gLSUPPLIERS+"'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		WTConnection wtconnection = null;
		try {
			SessionServerHelper.manager.setAccessEnforced(false);
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			pstmt = wtconnection.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				link = new GLCILink();
				link.setCatalogNumber(GLCATALOGNUMBER);
				link.setPartNumber(cIPARTNUMBER);
				link.setSuppliers(gLSUPPLIERS);
				link.setCatalogName(GLCATALOGNAME);
			}
			rs.close();
			pstmt.close();

		} catch (Exception ex) {
			ex.printStackTrace();
			SessionServerHelper.manager.setAccessEnforced(true);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			SessionServerHelper.manager.setAccessEnforced(true);
		}
		return link;
	}
	public static GLCIPartLink getGLCIPartLink(String CIPARTNUMBER,
			String WTPARTNUMBER) {
		GLCIPartLink link = null;
		String sql = "select * from GLCIPARTLINK where CIPARTNUMBER='" + CIPARTNUMBER + "' AND WTPARTNUMBER='"+WTPARTNUMBER+"'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		WTConnection wtconnection = null;
		try {
			SessionServerHelper.manager.setAccessEnforced(false);
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			pstmt = wtconnection.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				link = new GLCIPartLink();
				link.setCatalogItemNumber(CIPARTNUMBER);
				link.setPartNumber(WTPARTNUMBER);
			}
			rs.close();
			pstmt.close();

		} catch (Exception ex) {
			ex.printStackTrace();
			SessionServerHelper.manager.setAccessEnforced(true);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			SessionServerHelper.manager.setAccessEnforced(true);
		}
		return link;
	}

	public static GLPartLink getGLPartLink(String cATALOGNUMBER, String wTPARTNUMBER) {
		GLPartLink link = null;
		String sql = "select * from GLPartLink where CATALOGNUMBER='" + cATALOGNUMBER + "' AND WTPARTNUMBER='"+wTPARTNUMBER+"'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		WTConnection wtconnection = null;
		try {
			SessionServerHelper.manager.setAccessEnforced(false);
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			pstmt = wtconnection.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				link = new GLPartLink();
				link.setCatalogNumber(cATALOGNUMBER);
				link.setPartNumber(wTPARTNUMBER);
			}
			rs.close();
			pstmt.close();

		} catch (Exception ex) {
			ex.printStackTrace();
			SessionServerHelper.manager.setAccessEnforced(true);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			SessionServerHelper.manager.setAccessEnforced(true);
		}
		return link;
	}
	/**
     * 根据MasterId获取对应的对像
     *
     * @author jyx20180307
     * @param oid
     * @param className
     * @return
     * @throws Exception
     */
    public static Object getObjectByMasterId (String masterId,Class className) throws Exception {
		int[] index = { 0 };
		QuerySpec qSpec = new QuerySpec(className);
		qSpec.setAdvancedQueryEnabled(true);
        ClassAttribute caDocId = new ClassAttribute(className, "thePersistInfo.theObjectIdentifier.id");
        Object object = null;
		if (caDocId != null) {
			if(masterId != null && !masterId.equals("")){
				SubSelectExpression ss = getStringIBAQuery("MasterId", masterId);
				if (ss != null) {
					qSpec.appendWhere(new SearchCondition(caDocId, SearchCondition.IN, ss), index);
					QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
					qResult = new LatestConfigSpec().process(qResult);
					if (qResult.hasMoreElements()) {
						object = qResult.nextElement();
					}
				}
			}
		}
        return object;
	}
    /**
     * 根据MasterId获取Master对象
     * @author jyx20180307
     * @param oid
     * @param className
     * @return
     * @throws WTException
     */
    public static Master getMasterByMasterId (long masterId,Class className) throws WTException {
		QuerySpec qSpec = new QuerySpec(className);
        int[] index = { 0 };
        SearchCondition sCondition = new SearchCondition(className, "thePersistInfo.theObjectIdentifier.id",
                SearchCondition.EQUAL, masterId);
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        Master master = null;
        if (qResult.hasMoreElements()) {
        	master = (Master)qResult.nextElement();
		}
        return master;
	}

    /**
     * 根据MasterId获取对应的对像
     *
     * @author jyx20180307
     * @param oid
     * @param className
     * @return
     * @throws Exception
     */
    public static Object getObjectByMasterIdAndViewName (String masterId,Class className,String viewname) throws Exception {
    	boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
    	Object object = null;
		try {
			int[] index = { 0 };
			QuerySpec qSpec = new QuerySpec(className);
			qSpec.setAdvancedQueryEnabled(true);
			ClassAttribute caDocId = new ClassAttribute(className, "thePersistInfo.theObjectIdentifier.id");
			if (caDocId != null) {
				if(masterId != null && !masterId.equals("")){
					SubSelectExpression ss = getStringIBAQuery("MasterId", masterId);
					if (ss != null) {
						qSpec.appendWhere(new SearchCondition(caDocId, SearchCondition.IN, ss), index);
						if ((viewname != null) && (!viewname.equals(""))) {
							qSpec.appendAnd();
							View view = ViewHelper.service.getView(viewname);
							qSpec.appendWhere(
									new SearchCondition(WTPart.class, "view.key.id",
											"=", view.getPersistInfo().getObjectIdentifier().getId()), new int[1]);
						}
						QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
						qResult = new LatestConfigSpec().process(qResult);
						if (qResult.hasMoreElements()) {
							object = qResult.nextElement();
						}
					}
				}
			}
        }finally {
            SessionServerHelper.manager.setAccessEnforced(accessFlag);
        }
        return object;
	}
    /**
     * 根据MasterId获取对应的对像 更改到和批量数据集类型等无多版本的对象
     *
     * @author jyx20180307
     * @param oid
     * @param className
     * @return
     * @throws Exception
     */
    public static Object getObjectNoVersionByMasterId (String masterId,Class className) throws Exception {
		int[] index = { 0 };
		QuerySpec qSpec = new QuerySpec(className);
		qSpec.setAdvancedQueryEnabled(true);
        ClassAttribute caDocId = new ClassAttribute(className, "thePersistInfo.theObjectIdentifier.id");
        Object object = null;
		if (caDocId != null) {
			if(masterId != null && !masterId.equals("")){
				SubSelectExpression ss = getStringIBAQuery("MasterId", masterId);
				if (ss != null) {
					qSpec.appendWhere(new SearchCondition(caDocId, SearchCondition.IN, ss), index);
					QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
					if (qResult.hasMoreElements()) {
						object = qResult.nextElement();
					}
				}
			}
		}
        return object;
	}
	private static SubSelectExpression getStringIBAQuery(String ibaName,
			String ibaValue) throws WTException, WTPropertyVetoException,
			RemoteException {
		// 获取IBA属性定义
		AttributeDefDefaultView addv = IBADefinitionHelper.service
				.getAttributeDefDefaultViewByPath(ibaName);
		if (addv == null)
			throw new IBADefinitionException("No IBA Definition: " + ibaName);
		long ibaDefId = addv.getObjectID().getId();
		QuerySpec qs = new QuerySpec();
		int idx = qs.appendClassList(wt.iba.value.StringValue.class, false);
		qs.appendSelect(new ClassAttribute(wt.iba.value.StringValue.class,
				"theIBAHolderReference.key.id"), new int[] { idx }, false);
		qs.appendWhere(new SearchCondition(wt.iba.value.StringValue.class,
				"definitionReference.key.id", SearchCondition.EQUAL, ibaDefId),
				new int[] { idx });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(wt.iba.value.StringValue.class,
				_StringValue.VALUE2, SearchCondition.EQUAL, ibaValue ),
				new int[] { idx });
		return new SubSelectExpression(qs);
	}
}