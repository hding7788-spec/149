package com.glaway.mpm.util;

import com.glaway.mpm.mpmresource.Constants;
import com.glaway.mpm.pbom.db.ERPService;
import com.glaway.mpm.pbom.helper.PBOMHelper;
import com.ptc.core.meta.common.TypeIdentifierHelper;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;
import com.ptc.windchill.mpml.resource.MPMProcessMaterial;
import com.ptc.wvs.server.util.PublishUtils;
import com.ptc.wvs.server.util.RepUpdateUtils;
import ext.casc.util.WCUtil;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.SAXException;
import wt.access.NotAuthorizedException;
import wt.content.*;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.*;
import wt.fc.collections.WTList;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.iba.definition.IBADefinitionException;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.lifecycle.LifeCycleException;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.part.*;
import wt.pds.StatementSpec;
import wt.pom.PersistenceException;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.representation.Representation;
import wt.session.SessionServerHelper;
import wt.session.StandardSessionManager;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtility;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;
import wt.vc.config.ConfigHelper;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;
import wt.vc.struct.StructHelper;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.views.ViewReference;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import java.beans.PropertyVetoException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.*;


public class WTPartUtil implements RemoteAccess {
    private static int index[] = {0};
    private static PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesConfigs.QIAN_LONG_CONFIG_PATH);
    private static WTPart lastPart = null;

    public static void main(String[] args) {
        RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
        methodServer.setUserName("wcadmin");
        methodServer.setPassword("wcadmin");
        try {
            // WTPart part = (WTPart) Util.getObjectByOid(WTPart.class,
            // "1330349");
            // System.out.println(part);
            // QueryResult qr =
            // WTPartHelper.service.getDescribedByWTDocuments(part, true);
            // while (qr.hasMoreElements()) {
            // WTDocument document = (WTDocument) qr.nextElement();
            // System.out.println(document.getName() + "--" +
            // document.getVersionIdentifier().getValue() + "."
            // + document.getIterationIdentifier().getValue());
            // }
            methodServer.invoke("test", WTPartUtil.class.getName(), null, null, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void test() throws WTPropertyVetoException, WTException {
        WTDocument document = (WTDocument) Util.getObjectByOid(WTDocument.class, "1330222");
        document = (WTDocument) WorkInProcessUtil.checkout(document);
        document = (WTDocument) WorkInProcessUtil.checkin(document);
    }

    /**
     * @param number
     * @param viewName
     * @return
     * @throws WTException
     * @author qianlong
     * @date 2012-11-21
     */
    public static WTPart getPartByNumberAndView(String number, long viewId) throws WTException {
        WTPart part = null;

        QuerySpec qs = new QuerySpec(WTPart.class);
        qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, number), index);
        qs.appendAnd();
        qs.appendWhere(new SearchCondition(WTPart.class, "view.key.id", SearchCondition.EQUAL, viewId), index);
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        qr = new LatestConfigSpec().process(qr);
        if (qr.hasMoreElements()) {
            part = (WTPart) qr.nextElement();
        }
        return part;
    }

    /**
     * @param number
     * @param viewName
     * @return
     * @throws WTException
     * @author qianlong
     * @date 2012-11-21
     */
    public static WTPart getPartByNumberAndView(String number, String viewName) throws WTException {
        WTPart part = null;
        View view = getViewByName(viewName);
        QuerySpec qs = new QuerySpec(WTPart.class);
        qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, number), index);
        qs.appendAnd();
        qs.appendWhere(new SearchCondition(WTPart.class, "view.key.id", SearchCondition.EQUAL, view.getPersistInfo()
                .getObjectIdentifier().getId()), index);
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        qr = new LatestConfigSpec().process(qr);
        if (qr.hasMoreElements()) {
            part = (WTPart) qr.nextElement();
        }
        return part;
    }

    /**
     * @param part
     * @return
     * @throws WTException
     * @author qianlong
     * @date 2012-10-23
     */
    @SuppressWarnings("deprecation")
    public static List<WTPart> getChildPart(WTPart part) throws WTException {
        List<WTPart> nodeList = new ArrayList<WTPart>();

        if (part != null) {
            QueryResult qr = WTPartHelper.service.getUsesWTParts((WTPart) part, getConfigSpec());
            while (qr.hasMoreElements()) {
                Persistable[] per = (Persistable[]) qr.nextElement();
                Object obj = per[1];
                if (obj instanceof WTPart) {
                    nodeList.add((WTPart) obj);
                } else if (obj instanceof WTPartMaster) {
                    WTPartMaster master = (WTPartMaster) obj;
                    WTPart partTemp = getLatestPartByMaster(master);
                    nodeList.add(partTemp);
                }

            }
        }
        return nodeList;
    }

    @SuppressWarnings("deprecation")
    public static void getAllParentParts(WTPart part, List<WTPart> parts) throws WTException {
        List<WTPart> nodeList = getParentPart(part);
        parts.addAll(nodeList);
		/*for(WTPart p:nodeList){
			getAllParentParts(p,parts);
		}*/
    }

    public static List<String> getAllChildPartId(WTPart part) throws WTException {
        List<String> nodeList = new ArrayList<String>();

        if (part != null) {
            QueryResult qr = WTPartHelper.service.getUsesWTParts((WTPart) part, getConfigSpec());
            while (qr.hasMoreElements()) {
                Persistable[] per = (Persistable[]) qr.nextElement();
                Object obj = per[1];
                if (obj instanceof WTPart) {
                    String oid = ReferenceFactory.getFactory().getReferenceString((WTPart) obj);
                    nodeList.add(oid);
                } else if (obj instanceof WTPartMaster) {
                    WTPartMaster master = (WTPartMaster) obj;
                    WTPart partTemp = getLatestPartByMaster(master);
                    String oid = ReferenceFactory.getFactory().getReferenceString(partTemp);
                    nodeList.add(oid);
                }
            }
        }
        return nodeList;
    }

    public static List<WTPart> getAllChildParts(WTPart part) throws WTException {
        List<WTPart> nodeList = new ArrayList<WTPart>();
        if (part != null) {
            QueryResult qr = WTPartHelper.service.getUsesWTParts((WTPart) part, getConfigSpec());
            while (qr.hasMoreElements()) {
                Persistable[] per = (Persistable[]) qr.nextElement();
                Object obj = per[1];
                if (obj instanceof WTPart) {
                    nodeList.add((WTPart) obj);
                } else if (obj instanceof WTPartMaster) {
                    WTPartMaster master = (WTPartMaster) obj;
                    WTPart partTemp = getLatestPartByMaster(master);
                    nodeList.add(partTemp);
                }
            }
        }
        return nodeList;
    }

    /**
     * @param part
     * @return
     * @throws WTException
     * @author qianlong
     * @date 2012-10-23
     */
    public static List<WTPart> getParentPart(WTPart part) throws WTException {
        List<WTPart> nodeList = new ArrayList<WTPart>();

        if (part != null) {
            QueryResult qr = WTPartHelper.service.getUsedByWTParts((WTPartMaster) part.getMaster());
            qr = new LatestConfigSpec().process(qr);
            while (qr.hasMoreElements()) {
                nodeList.add((WTPart) qr.nextElement());
            }
        }

        return nodeList;
    }

    public static boolean isHasPbomXml(WTPart part) throws WTException, PropertyVetoException {
        WTDocument document = PBOMHelper.getBOMXmlDoc(part, Constants.pbomDocEndwith);
        if (document != null) {
            return true;
        }
        return false;
    }

    public static boolean isHasParent(WTPart part) throws WTException {
        QueryResult qr = WTPartHelper.service.getUsedByWTParts((WTPartMaster) part.getMaster());
        while (qr.hasMoreElements()) {
            WTPart p = (WTPart) qr.nextElement();
            if ("Manufacturing".equals(p.getViewName())) {
                return true;
            }
        }
        return false;
    }

    public static String getHasPbomXmlParentPartIda2a2(WTPart part, String ida2a2) throws WTException, PropertyVetoException {
        QueryResult qr = WTPartHelper.service.getUsedByWTParts((WTPartMaster) part.getMaster());
        qr = new LatestConfigSpec().process(qr);
        while (qr.hasMoreElements()) {
            WTPart parent = (WTPart) qr.nextElement();

            //如果最新版本是space.0版本且是Design视图说明肯定没有PBOM的xml，则直接跳过
            //WTPart p = Util.getLatestIterationPart(parent);
            WTPart p = parent;
            if ((p.getVersionInfo().getIdentifier().getValue() + "." + p.getIterationInfo().getIdentifier().getValue()).equals("space.0")) {
                continue;
            }
            System.out.println("---parent----number:" + parent.getNumber()
                    + "   oid:" + PersistenceHelper.getObjectIdentifier(parent).getId()
                    + "   view:" + parent.getViewName()
                    + "   version:" + parent.getVersionDisplayIdentifier().toString() + "." + parent.getIterationDisplayIdentifier().toString());
            parent = WTPartUtil.getLatestPartByNumberAndView(parent, "Manufacturing");
            if (parent != null) {
                WTDocument document = PBOMHelper.getBOMXmlDoc(parent, Constants.pbomDocEndwith);
                if (document != null) {
                    ida2a2 = String.valueOf(PersistenceHelper.getObjectIdentifier(parent).getId());
                } else {
                    ida2a2 = getHasPbomXmlParentPartIda2a2(parent, ida2a2);
                }
                if (ida2a2 != null && !"".equals(ida2a2)) {
                    break;
                }
            }
        }
        return ida2a2;
    }

    public static WTPart getLastestParent(WTPart part) throws WTException {
        QueryResult qr = WTPartHelper.service.getUsedByWTParts((WTPartMaster) part.getMaster());
        if (qr.hasMoreElements()) {
        	WTPart pp = (WTPart) qr.nextElement();
            WTPart p = pp;
            //如果最新版本是space.0版本且是Design视图说明肯定没有PBOM的xml，则直接跳过
            //WTPart p = Util.getLatestIterationPart(parent);
            p = WTPartUtil.getLatestPartByNumberAndView(p, "Manufacturing");
            lastPart = p;
            getLastestParent(p);
        }else{
        	return part;
        }
        return lastPart;
    }

	public static String getHasPbomXmlLastestParentPartIda2a2(WTPart part, String ida2a2) throws WTException, PropertyVetoException {
		WTPart lastestParent = getLastestParent(part);
		WTPart parent = lastestParent;
		System.out.println("---parent----number:" + parent.getNumber() + "   oid:" + PersistenceHelper.getObjectIdentifier(parent).getId() + "   view:" + parent.getViewName() + "   version:"
				+ parent.getVersionDisplayIdentifier().toString() + "." + parent.getIterationDisplayIdentifier().toString());
		parent = WTPartUtil.getLatestPartByNumberAndView(parent, "Manufacturing");
		if (parent != null) {
			WTDocument document = PBOMHelper.getBOMXmlDoc(parent, Constants.pbomDocEndwith);
			if (document != null) {
				ida2a2 = String.valueOf(PersistenceHelper.getObjectIdentifier(parent).getId());
			} else {
				ida2a2 = getHasPbomXmlParentPartIda2a2(parent, ida2a2);
			}
		}
		return ida2a2;
	}

    public static String getHasPbomXmlParentPartIda2a2(WTPart part, String ida2a2, boolean isLatestParent) throws WTException, PropertyVetoException {
        QueryResult qr = WTPartHelper.service.getUsedByWTParts((WTPartMaster) part.getMaster());
        if (isLatestParent) {
            qr = new LatestConfigSpec().process(qr);
        }
        while (qr.hasMoreElements()) {
            WTPart parent = (WTPart) qr.nextElement();

            //如果最新版本是space.0版本且是Design视图说明肯定没有PBOM的xml，则直接跳过
            //WTPart p = Util.getLatestIterationPart(parent);
            WTPart p = parent;
            if (p.getViewName().equals("Design")) {
                continue;
            }
            if ((p.getVersionInfo().getIdentifier().getValue() + "." + p.getIterationInfo().getIdentifier().getValue()).equals("space.0")) {
                continue;
            }
            System.out.println("---parent----number:" + parent.getNumber()
                    + "   oid:" + PersistenceHelper.getObjectIdentifier(parent).getId()
                    + "   view:" + parent.getViewName()
                    + "   version:" + parent.getVersionDisplayIdentifier().toString() + "." + parent.getIterationDisplayIdentifier().toString());
            parent = WTPartUtil.getLatestPartByNumberAndView(parent, "Manufacturing");
            if (parent != null) {
                WTDocument document = PBOMHelper.getBOMXmlDoc(parent, Constants.pbomDocEndwith);
                if (document != null) {
                    ida2a2 = String.valueOf(PersistenceHelper.getObjectIdentifier(parent).getId());
                } else {
                    ida2a2 = getHasPbomXmlParentPartIda2a2(parent, ida2a2);
                }
                if (ida2a2 != null && !"".equals(ida2a2)) {
                    break;
                }
            }
        }
        return ida2a2;
    }

    public static List<Map<String, String>> getHasPbomXmlParentPartIda2a2List(WTPart part, String ida2a2, boolean isLatestParent) throws WTException, PropertyVetoException {
        List<Map<String, String>> parentPartOidList = new ArrayList<Map<String, String>>();
        QueryResult qr = WTPartHelper.service.getUsedByWTParts((WTPartMaster) part.getMaster());
        if (isLatestParent) {
            qr = new LatestConfigSpec().process(qr);
        }
        Map<String, String> partInfo = null;
        IBAHelper ibaHelper = null;
        while (qr.hasMoreElements()) {
            WTPart parent = (WTPart) qr.nextElement();
            partInfo = new HashMap<String, String>();
            //如果最新版本是space.0版本且是Design视图说明肯定没有PBOM的xml，则直接跳过
            //WTPart p = Util.getLatestIterationPart(parent);
            WTPart p = parent;
            if (p.getViewName().equals("Design")) {
                continue;
            }
            if ((p.getVersionInfo().getIdentifier().getValue() + "." + p.getIterationInfo().getIdentifier().getValue()).equals("space.0")) {
                continue;
            }
            System.out.println("---parent----number:" + parent.getNumber()
                    + "   oid:" + PersistenceHelper.getObjectIdentifier(parent).getId()
                    + "   view:" + parent.getViewName()
                    + "   version:" + parent.getVersionDisplayIdentifier().toString() + "." + parent.getIterationDisplayIdentifier().toString());
            parent = WTPartUtil.getLatestPartByNumberAndView(parent, "Manufacturing");
            if (parent != null) {
                WTDocument document = PBOMHelper.getBOMXmlDoc(parent, Constants.pbomDocEndwith);
                if (document != null) {
                    ibaHelper = new IBAHelper(parent);
                    partInfo.put("partNumber", parent.getNumber());
                    partInfo.put("partName", parent.getName());
                    String partVersion = parent.getVersionInfo().getIdentifier().getValue() + "." + parent.getIterationInfo().getIdentifier().getValue();
                    partInfo.put("partVersion", partVersion);
                    partInfo.put("partBatch", ibaHelper.getIBAValue("BATCH"));
                    partInfo.put("partPhase_code", ibaHelper.getIBAValue("PHASE_CODE"));
                    partInfo.put("partState", parent.getState().getState().getDisplay(Locale.CHINA));
                    partInfo.put("partOid", String.valueOf(PersistenceHelper.getObjectIdentifier(parent).getId()));
                    parentPartOidList.add(partInfo);
                } else {
                    parent = getHasPbomXmlParentPartIda2a2List(parent, ida2a2);
                    if (parent != null) {
                        ibaHelper = new IBAHelper(parent);
                        partInfo.put("partNumber", parent.getNumber());
                        partInfo.put("partName", parent.getName());
                        String partVersion = parent.getVersionInfo().getIdentifier().getValue() + "." + parent.getIterationInfo().getIdentifier().getValue();
                        partInfo.put("partVersion", partVersion);
                        partInfo.put("partBatch", ibaHelper.getIBAValue("BATCH"));
                        partInfo.put("partPhase_code", ibaHelper.getIBAValue("PHASE_CODE"));
                        partInfo.put("partState", parent.getState().getState().getDisplay(Locale.CHINA));
                        partInfo.put("partOid", String.valueOf(PersistenceHelper.getObjectIdentifier(parent).getId()));
                        parentPartOidList.add(partInfo);
                    }
                }
//				if(ida2a2 != null && !"".equals(ida2a2)) {
//					break;
//				}
            }
        }
        return parentPartOidList;
    }

    public static WTPart getHasPbomXmlParentPartIda2a2List(WTPart part, String ida2a2) throws WTException, PropertyVetoException {
        QueryResult qr = WTPartHelper.service.getUsedByWTParts((WTPartMaster) part.getMaster());
        qr = new LatestConfigSpec().process(qr);
        WTPart parentPart = null;
        while (qr.hasMoreElements()) {
            WTPart parent = (WTPart) qr.nextElement();

            //如果最新版本是space.0版本且是Design视图说明肯定没有PBOM的xml，则直接跳过
            //WTPart p = Util.getLatestIterationPart(parent);
            WTPart p = parent;
            if ((p.getVersionInfo().getIdentifier().getValue() + "." + p.getIterationInfo().getIdentifier().getValue()).equals("space.0")) {
                continue;
            }
            System.out.println("---parent----number:" + parent.getNumber()
                    + "   oid:" + PersistenceHelper.getObjectIdentifier(parent).getId()
                    + "   view:" + parent.getViewName()
                    + "   version:" + parent.getVersionDisplayIdentifier().toString() + "." + parent.getIterationDisplayIdentifier().toString());
            parent = WTPartUtil.getLatestPartByNumberAndView(parent, "Manufacturing");
            if (parent != null) {
                WTDocument document = PBOMHelper.getBOMXmlDoc(parent, Constants.pbomDocEndwith);
                if (document != null) {
                    parentPart = parent;
                } else {
                    parentPart = getHasPbomXmlParentPartIda2a2List(parent, ida2a2);
                }
            }
        }
        return parentPart;
    }

    /**
     * @param part
     * @return
     * @throws WTException
     * @author qianlong
     * @date 2012-10-23
     */

    public static WTPartUsageLink getWTPartUsageLink(WTPart parentPart, WTPartMaster childPartMaster)
            throws WTException {
        QuerySpec qs = new QuerySpec(WTPartUsageLink.class);
        long roleALongID = PersistenceHelper.getObjectIdentifier(parentPart).getId();
        long roleBLongID = PersistenceHelper.getObjectIdentifier(childPartMaster).getId();
        int[] index = {0};
        SearchCondition scCondition = new SearchCondition(WTPartUsageLink.class, "roleAObjectRef.key.id",
                SearchCondition.EQUAL, roleALongID);
        qs.appendWhere(scCondition, index);
        qs.appendAnd();
        SearchCondition scCondition2 = new SearchCondition(WTPartUsageLink.class, "roleBObjectRef.key.id",
                SearchCondition.EQUAL, roleBLongID);
        qs.appendWhere(scCondition2, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qs);
        WTPartUsageLink link = null;
        while (qResult.hasMoreElements()) {
            link = (WTPartUsageLink) qResult.nextElement();
        }
        return link;
    }

    /**
     * @param partNumber
     * @return
     * @throws WTException
     * @author qianlong
     * @date 2012-10-23
     */
    public static WTPart getLatestPartByPartNumber(String number) throws WTException {
        System.out.println("number===" + number);
        WTPart part = null;
        QuerySpec qSpec = new QuerySpec(WTPart.class);
        qSpec.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, number, true), index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        System.out.println("qResult===" + qResult.hasMoreElements());
        qResult = new LatestConfigSpec().process(qResult);
        System.out.println("qResult===" + qResult.hasMoreElements());
        if (qResult.hasMoreElements()) {
            part = (WTPart) qResult.nextElement();
        }

        return part;
    }

    /**
     * @param typeName
     * @param number
     * @param name
     * @return
     * @throws WTException
     * @throws RemoteException
     * @author qianlong
     * @date 2013-4-12
     */
    public static QueryResult getPartByLikeNumberNameType(String typeName, String number, String name)
            throws WTException, RemoteException {
        QuerySpec qSpec = new QuerySpec(WTPart.class);
        TypeUtil.getTypeQuery(WTPart.class, typeName, qSpec);
        qSpec.appendAnd();
        qSpec.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.LIKE, number, false), index);
        qSpec.appendAnd();
        qSpec.appendWhere(new SearchCondition(WTPart.class, WTPart.NAME, SearchCondition.LIKE, name, false), index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        qResult = new LatestConfigSpec().process(qResult);

        return qResult;
    }

    /**
     * @param partNumber
     * @param partName
     * @return
     * @throws WTException
     * @throws RemoteException
     * @author qianlong
     * @date 2012-11-5
     */
    public static QueryResult getPartByLikeNumberNameContainer(String number, String name, WTContainer container)
            throws WTException {
        QuerySpec qSpec = new QuerySpec(WTPart.class);

        long libraryid = PersistenceHelper.getObjectIdentifier(container).getId();
        qSpec.appendWhere(new SearchCondition(WTPart.class, WTPart.CONTAINER_ID, SearchCondition.EQUAL, libraryid),
                index);
        qSpec.appendAnd();
        qSpec.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.LIKE, number, false), index);
        qSpec.appendAnd();
        qSpec.appendWhere(new SearchCondition(WTPart.class, WTPart.NAME, SearchCondition.LIKE, name, false), index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        qResult = new LatestConfigSpec().process(qResult);

        return qResult;
    }

    public static QueryResult getAllMPMProcessMaterial(WTContainer container)
            throws WTException {
        QuerySpec qSpec = new QuerySpec(MPMProcessMaterial.class);

        long libraryid = PersistenceHelper.getObjectIdentifier(container).getId();
        qSpec.appendWhere(new SearchCondition(MPMProcessMaterial.class, MPMProcessMaterial.CONTAINER_ID, SearchCondition.EQUAL, libraryid),
                index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        qResult = new LatestConfigSpec().process(qResult);

        return qResult;
    }

    /**
     * 获取整件
     *
     * @param partNumber
     * @param partName
     * @return
     * @author qianlong
     * @date 2012-11-8
     */
    public static List<WTPart> getWholePart(String number, String name) throws WTException {
        List<WTPart> list = new ArrayList<WTPart>();
        QuerySpec qSpec = new QuerySpec(WTPart.class);
        qSpec.appendOpenParen();
        qSpec
                .appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.LIKE, "AL1.%", false),
                        index);
        qSpec.appendOr();
        qSpec
                .appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.LIKE, "AL2.%", false),
                        index);
        qSpec.appendOr();
        qSpec
                .appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.LIKE, "AL3.%", false),
                        index);
        qSpec.appendOr();
        qSpec
                .appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.LIKE, "AL4.%", false),
                        index);
        qSpec.appendCloseParen();
        qSpec.appendAnd();
        qSpec.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.LIKE, number, false), index);
        qSpec.appendAnd();
        qSpec.appendWhere(new SearchCondition(WTPart.class, WTPart.NAME, SearchCondition.LIKE, name, false), index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);

        qResult = new LatestConfigSpec().process(qResult);
        while (qResult.hasMoreElements()) {
            WTPart part = (WTPart) qResult.nextElement();
            list.add(part);
        }

        return list;
    }

    /**
     * @param partMaster
     * @return
     * @throws WTException
     * @throws PersistenceException
     * @author qianlong
     * @date 2012-10-23
     */
    public static WTPart getLatestPartByMaster(WTPartMaster partMaster) throws WTException {
        WTPart part = null;

        if (partMaster != null) {
            QueryResult qr = VersionControlHelper.service.allVersionsOf(partMaster);
            if (qr.hasMoreElements()) {
                part = (WTPart) qr.nextElement();

            }
        }
        return part;
    }

    /**
     * @param partNumber
     * @return
     * @throws WTException
     * @author qianlong
     * @date 2012-10-23
     */
    public static WTPartMaster getWTPartMasterByNumber(String number) throws WTException {
        WTPartMaster partMaster = null;

        QuerySpec qs = new QuerySpec(WTPartMaster.class);
        qs.appendWhere(new SearchCondition(WTPartMaster.class, WTPartMaster.NUMBER, SearchCondition.EQUAL, number),
                index);
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        if (qr.hasMoreElements()) {
            partMaster = (WTPartMaster) qr.nextElement();
        }
        return partMaster;

    }

    /**
     * 获取二维图档
     *
     * @param part
     * @return
     * @throws WTException
     * @author qianlong
     * @date 2013-2-21
     */
    public static EPMDocument get2DEPMDocumentByPart(WTPart part) throws WTException {

        EPMDocument epmDocument = null;
        if (part != null) {
            QueryResult qr = PartDocServiceCommand.getAssociatedCADDocuments(part);
            while (qr.hasMoreElements()) {
                EPMDocument epmDoc = (EPMDocument) qr.nextElement();
                String name = epmDoc.getCADName().toLowerCase();
                if (name.endsWith(".drw") || name.endsWith(".dwg") || name.endsWith(".dxf")) {
                    epmDocument = epmDoc;
                }
            }
        }
        return epmDocument;
    }

    /**
     * 获取三维图档
     *
     * @param part
     * @return
     * @throws WTException
     * @author qianlong
     * @date 2013-2-21
     */
    public static EPMDocument get3DEPMDocumentByPart(WTPart part) throws WTException {

        EPMDocument epmDocument = null;
        if (part != null) {
            QueryResult qr = PartDocServiceCommand.getAssociatedCADDocuments(part);
            while (qr.hasMoreElements()) {
                EPMDocument epmDoc = (EPMDocument) qr.nextElement();
                String name = epmDoc.getCADName().toLowerCase();
                System.out.println("name--" + name);
                if (name.endsWith(".asm") || name.endsWith(".prt")) {
                    epmDocument = epmDoc;
                }
            }
        }
        return epmDocument;
    }

    /**
     * 获取所有的二维，三维图档
     *
     * @param part
     * @return
     * @throws WTException
     * @author qianlong
     * @date 2013-2-21
     */
    public static QueryResult getEPMDocumentByPart(WTPart part) throws WTException {
        QueryResult qr = null;
        if (part != null) {
            qr = PartDocServiceCommand.getAssociatedCADDocuments(part);
        }
        return qr;
    }

    /**
     * 通过零件类型获取所有的零件
     *
     * @param objectType
     * @return
     * @throws WTException
     * @throws RemoteException
     * @author qianlong
     * @date 2013-4-11
     */
    public static QueryResult getPartByType(String objectType) throws WTException, RemoteException {

        QuerySpec querySpec = new QuerySpec(WTPart.class);

        TypeUtil.getTypeQuery(WTPart.class, objectType, querySpec);
        querySpec.appendAnd();
        MPMResourceUtil.getExcludeLifeCycleStateQuery(WTPart.class, Constants.YZF, querySpec);
        QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
        queryResult = new LatestConfigSpec().process(queryResult);
        return queryResult;
    }

    /**
     * @param part
     * @return
     * @throws WTException
     * @author qianlong
     * @date 2012-10-23
     */
    public static List<WTDocument> getDescribedDocumentByPart(WTPart part, String docType, List<WTDocument> docList) throws WTException {
        List<WTDocument> list = new ArrayList<WTDocument>();
        if (part != null && docType != null && !docType.trim().equals("")) {

            QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(part, true);
            LatestConfigSpec lcs = new LatestConfigSpec();
            qr = lcs.process(qr);
            while (qr.hasMoreElements()) {
                WTDocument document = (WTDocument) qr.nextElement();
                if (TypedUtility.getTypeIdentifier(document).getTypename().contains(docType)) {
                    //特殊处理：因为正式机增材工艺的类型属性错误地建在了钣金工艺（英文）下面，所以查钣金的时候过滤掉增材工艺
                    if("casc.sast.149.ENGLISH_BANJIN_PROCESSPLAN".equals(docType)
                            && TypedUtility.getTypeIdentifier(document).getTypename().contains("ZENGCAI_PROCESSPLAN")) {
                        continue;
                    }
                    //document = (WTDocument)VersionControlHelper.service.getLatestIteration(document, true);
                    QueryResult qr2 = VersionControlHelper.service.allIterationsOf(document.getMaster());
                    if (qr2.hasMoreElements()) {
                        //只获取关联的最新版本
                        document = (WTDocument) qr2.nextElement();
                        if (docList.isEmpty() || !checkNumber(docList, document.getNumber())) {
                            docList.add(document);
                        }
                    }

                }
            }
        }
        return list;
    }

    public static List<WTDocument> getDescribedDocumentByPart(WTPart part, String docType) throws WTException {
        List<WTDocument> list = new ArrayList<WTDocument>();
        if (part != null && docType != null && !docType.trim().equals("")) {

            QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(part, true);
            LatestConfigSpec lcs = new LatestConfigSpec();
            qr = lcs.process(qr);
            while (qr.hasMoreElements()) {
                WTDocument document = (WTDocument) qr.nextElement();
                if (TypedUtility.getTypeIdentifier(document).getTypename().contains(docType)) {
                    //document = (WTDocument)VersionControlHelper.service.getLatestIteration(document, true);
                    //只获取关联的最新版本
                    QueryResult qr2 = VersionControlHelper.service.allIterationsOf(document.getMaster());
                    if (qr2.hasMoreElements()) {
                        //只获取关联的最新版本
                        document = (WTDocument) qr2.nextElement();
                        if (list.isEmpty() || !checkNumber(list, document.getNumber())) {
                            list.add(document);
                        }
                    }
                }
            }
        }
        return list;
    }

    public static boolean checkNumber(List<WTDocument> list, String number) {
        for (WTDocument wtDocument : list) {
            if (wtDocument.getNumber().equals(number)) {
                return true;
            }
        }
        return false;
    }

    public static List<WTDocument> getTechnicsDocumentByPart(WTPart part) throws WTException {
        List<WTDocument> list = new ArrayList<WTDocument>();
        QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(part, true);
        while (qr.hasMoreElements()) {
            WTDocument document = (WTDocument) qr.nextElement();
            if (TypedUtility.getTypeIdentifier(document).getTypename().contains("PROCESS_PLAN")) {
                document = (WTDocument) VersionControlHelper.service.getLatestIteration(document, true);
                list.add(document);
            }
        }
        return list;
    }

    public static List<WTDocument> getDescribedDocumentByPart2(WTPart part, String docType) throws WTException {
        List<WTDocument> list = new ArrayList<WTDocument>();
        QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(part, true);
        while (qr.hasMoreElements()) {
            WTDocument document = (WTDocument) qr.nextElement();
            String typeName = TypedUtility.getTypeIdentifier(document).getTypename();
            if (typeName.contains(Constants.ASSEMBLE_PROCESSPLAN)
                    || typeName.contains(Constants.MOUNT_PROCESSPLAN)
                    || typeName.contains(Constants.MACHINING_PROCESSPLAN)
                    || typeName.contains(Constants.PAINT_PROCESSPLAN)) {
                list.add(document);
            }
        }
        return list;
    }

    /**
     * 获取零件的最新的说明文档
     *
     * @param part
     * @return
     * @throws WTException
     * @author fly
     * @date 2012-10-23
     */
    public static WTDocument getDescribedDocumentByPart(WTPart part) throws WTException {
        QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(part, true);
        while (qr.hasMoreElements()) {
            WTDocument document = (WTDocument) qr.nextElement();
            return (WTDocument) VersionControlHelper.service.allIterationsOf(document.getMaster()).nextElement();
        }

        return null;
    }

    /**
     * @param part
     * @param fileType
     * @return
     * @throws WTException
     * @throws PropertyVetoException
     * @author qianlong
     * @date 2012-11-1
     */
    @SuppressWarnings({"unchecked"})
    public static InputStream getSecondaryByPart(WTPart part, String fileType) throws WTException,
            PropertyVetoException {
        InputStream inputStream = null;
        // 获得附件的主内容
        ContentHolder ch = ContentHelper.service.getContents(part);
        Vector attachmentList = ContentHelper.getApplicationData(ch);
        for (int i = 0; i < attachmentList.size(); i++) {
            ApplicationData ad = (ApplicationData) attachmentList.get(i);
            if (ad.getFileName().contains("")) {
                inputStream = ContentServerHelper.service.findContentStream(ad);
            }
        }
        return inputStream;
    }

    /**
     * @param part
     * @param document
     * @throws WTException
     * @author qianlong
     * @date 2012-11-13
     */
    public static void createWTPartDescribeLink(WTPart part, WTDocument document) throws WTException {
        WTPartDescribeLink link = WTPartDescribeLink.newWTPartDescribeLink(part, document);
        PersistenceServerHelper.manager.insert(link);
    }

    /**
     * 删除零部件关联的工艺规程文档Link
     *
     * @param part
     * @throws WTException
     * @throws RemoteException
     * @author LongXiuChuan
     * @date 2014-1-15
     */
    public static void deleteTechnicsDescribeLink(WTPart part) throws WTException, RemoteException {
        QueryResult qResult = WTPartHelper.service.getDescribedByWTDocuments(part, false);
        WTPartDescribeLink link = null;
        WTDocument document = null;
        while (qResult.hasMoreElements()) {
            Object object = qResult.nextElement();
            link = (WTPartDescribeLink) object;
            document = (WTDocument) link.getRoleBObject();
            String softType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(document);
            if (softType.indexOf("PROCESS_PLAN") != -1) {
                PersistenceServerHelper.manager.remove(link);
            }
        }
    }

    /**
     * 删除零部件关联的工艺规程文档Link
     *
     * @param part
     * @throws WTException
     * @throws RemoteException
     * @author LongXiuChuan
     * @date 2014-1-15
     */
    public static void deletePbomDescribeLink(WTPart part) throws WTException, RemoteException {
        QueryResult qResult = WTPartHelper.service.getDescribedByWTDocuments(part, false);
        WTPartDescribeLink link = null;
        WTDocument document = null;
        while (qResult.hasMoreElements()) {
            Object object = qResult.nextElement();
            link = (WTPartDescribeLink) object;
            document = (WTDocument) link.getRoleBObject();
            String softType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(document);
            if (softType.indexOf("PBOM") != -1) {
                PersistenceServerHelper.manager.remove(link);
            }
        }
    }

    /**
     * @param parentPart
     * @param childPartMaster
     * @return
     * @throws WTException
     * @author qianlong
     * @date 2012-12-25
     */
    public static WTPartUsageLink createWTPartUsageLink(WTPart parentPart, WTPartMaster childPartMaster)
            throws WTException {
        WTPartUsageLink partUsageLink = WTPartUsageLink.newWTPartUsageLink(parentPart, childPartMaster);
        PersistenceServerHelper.manager.insert(partUsageLink);
        return partUsageLink;

    }

    /**
     * @param parentPart
     * @param childPartMaster
     * @return
     * @throws WTException
     * @author qianlong
     * @date 2012-12-25
     */
    public static void deleteWTPartUsageLink(WTPartUsageLink link) throws WTException {
        PersistenceServerHelper.manager.remove(link);

    }

    /**
     * @return
     * @throws WTException
     * @author qianlong
     * @date 2012-10-23
     */
    public static ConfigSpec getConfigSpec() throws WTException {

        return ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class);
    }

    /**
     * 根据零件信息，创建零件
     *
     * @param partInfo
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws RemoteException
     * @author lbzhang
     * @date 2012-10-29下午06:02:10
     */
    @SuppressWarnings("deprecation")
    public static WTPart createPart(Element e, WTContainerRef containerRef) throws WTException,
            WTPropertyVetoException, RemoteException {
        String partType = e.getAttributeValue("partType");// 零件类型
        String partNumber = e.getAttributeValue("partNumber");
        WTPart temp = getLatestPartByPartNumber(partNumber);
        GLLogger.debug("middle or fj===>" + temp);
        if (temp != null) {
            return temp;
        }
        WTPart part = WTPart.newWTPart();
        part.setName(e.getAttributeValue("partName"));// 设置零件名称
        part.setNumber(partNumber);// 设置零件的编号

        part.setContainer(containerRef.getContainer());// 设置零件的容器

        ViewReference viewReference = getViewReferenceOfPartByName(Constants.design);// 获取零件的视图
        part.setView(viewReference);// 设置零件的视图

        Folder folder = null;

        TypeDefinitionReference typeRef = null;
        if ("middle".equals(partType)) {// 中间件
            typeRef = TypedUtilityServiceHelper.service.getTypeDefinitionReference("com.nriet.ProcessMiddlePart");// 获取物件类型为中间件的零件类型
            folder = FolderUtil.getFolder("/Default/70：PBOM/中间件", containerRef);
        } else {
            typeRef = TypedUtilityServiceHelper.service.getTypeDefinitionReference("com.nriet.ProcessAssistantPart");// 获取物件类型为工艺辅件的零件类型
            folder = FolderUtil.getFolder("/Default/70：PBOM/辅件", containerRef);
        }

        FolderHelper.assignFolder(part, folder);// 指派零件到指定容器和指定路径下
        part.setTypeDefinitionReference(typeRef);// 设置零件的类型

        part = (WTPart) PersistenceHelper.manager.save(part);
        part = (WTPart) PersistenceHelper.manager.refresh(part);
        return part;

    }

    /**
     * 修改
     *
     * @param oid
     * @param containerRef
     * @return
     * @throws WTException
     * @throws RemoteException
     * @throws WTPropertyVetoException
     * @author qianlong
     * @date 2012-12-21
     */
    @SuppressWarnings("deprecation")
    public static WTPart createPlanningPart(String oid) throws WTException, RemoteException, WTPropertyVetoException {
        //防止借用件时没有创建权限
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        //WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
        //SessionHelper.manager.setAdministrator();

        WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
        String partTypeName = TypedUtility.getTypeIdentifier(part).getTypename();
        WTPart newViewPart = null;
        newViewPart = (WTPart) ViewHelper.service.newBranchForView(part, Constants.planning);
        LifeCycleHelper.setLifeCycle(newViewPart, part.getLifeCycleTemplate());// 设置新视图版本零件的生命周期
        newViewPart.setTeamTemplateId(part.getTeamTemplateId());// 设置新视图版本的专案团队
        Folder folder = null;
        //folder = FolderUtil.getFolder(propertiesUtil.getProperty("planning-part-save-folder"), part.getContainerReference());
        // 如果是 标准间，则存放在/Default/PBOM下
        if (part.getContainer() instanceof WTLibrary) {
            folder = FolderUtil.getFolder(propertiesUtil.getProperty("planning-part-save-folder2"), part.getContainerReference());
        } else {
            folder = FolderUtil.getFolder(propertiesUtil.getProperty("planning-part-save-folder"), part.getContainerReference());
        }
        System.out.println("---folder--" + folder.getFolderPath());
        FolderHelper.assignFolder(newViewPart, folder);
        newViewPart = (WTPart) PersistenceHelper.manager.store(newViewPart);// 保存新视图版本零件

        //标准件库单独处理，PBOM默认设置为"标准件"
        try {
            if ((part.getContainer() instanceof WTLibrary) && part.getContainer().getName().contains("标准件")) {
                IBAHelper iba = new IBAHelper(newViewPart);
                iba.setIBAValue(part, "MTYPE", "标准件");
                iba.updateAttributeContainer(newViewPart);
                iba.updateIBAHolder(newViewPart);
                newViewPart = (WTPart) PersistenceHelper.manager.refresh(newViewPart);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SessionServerHelper.manager.setAccessEnforced(flag);
        //SessionHelper.manager.setPrincipal(currentUser.getName());
        return newViewPart;
    }

    public static WTPart createPlanningPart(String oid, WTContainer container, String path) throws WTException, RemoteException, WTPropertyVetoException {
        WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
        String partTypeName = TypedUtility.getTypeIdentifier(part).getTypename();
        WTPart newViewPart = null;
        newViewPart = (WTPart) ViewHelper.service.newBranchForView(part, Constants.planning);
        LifeCycleHelper.setLifeCycle(newViewPart, part.getLifeCycleTemplate());// 设置新视图版本零件的生命周期
        newViewPart.setTeamTemplateId(part.getTeamTemplateId());// 设置新视图版本的专案团队
        newViewPart.setContainer(container);
        newViewPart.setContainerReference(container.getContainerReference());
        Folder folder = null;
//		folder = FolderUtil.getFolder(propertiesUtil.getProperty("planning-part-save-folder"), cref);
        folder = FolderUtil.getFolder(path, container.getContainerReference());
        // 如果是 工装零件 文件夹结构不一样
//		if (!partTypeName.contains(TypeNameConstants.gzRootPartTypeName)
//				&& !partTypeName.contains(TypeNameConstants.gzToolingPartTypeName)) {
//			folder = FolderUtil.getFolder(propertiesUtil.getProperty("planning-part-save-folder"), part
//					.getContainerReference());
//		} else {
//			folder = FolderUtil.getFolder(((SubFolder) part.getParentFolder().getObject()).getLocation() + "/"
//					+ Constants.gzFolderName[6], part.getContainerReference());
//		}
        FolderHelper.assignFolder(newViewPart, folder);
        newViewPart = (WTPart) PersistenceHelper.manager.store(newViewPart);// 保存新视图版本零件
        return newViewPart;
    }

    /**
     * @return
     * @throws WTPropertyVetoException
     * @throws WTException
     * @throws RemoteException
     * @throws WTPropertyVetoException
     * @author qianlong
     * @date 2012-12-21
     */
    @SuppressWarnings("deprecation")
    public static WTPart createPart(String number, String name, WTContainer container, String folderPath,
                                    String viewName, String typeName) throws WTException, RemoteException, WTPropertyVetoException {
        WTPart part = WTPart.newWTPart();
        if (number != null && !"".equals(number)) {
            part.setNumber(number);

        }
        if (name != null && !"".equals(name)) {
            part.setName(name);
        }
        part.setContainer(container);
        if (folderPath != null && !"".equals(folderPath)) {
            Folder folder = FolderUtil.getFolder(folderPath, WTContainerRef.newWTContainerRef(container));
            FolderHelper.assignFolder(part, folder);
        }
        if (viewName != null && !"".equals(viewName)) {
            ViewReference viewReference = WTPartUtil.getViewReferenceOfPartByName(viewName);
            part.setView(viewReference);
        }

        TypeUtil.setType(part, typeName);
        PersistenceHelper.manager.save(part);

        return part;
    }

    /**
     * 修改
     *
     * @throws WTException
     * @author qianlong
     * @date 2012-12-21
     */
    public static void deletePart(WTPart part) throws WTException {

        if (part != null) {
            PersistenceHelper.manager.delete(part);
        }

    }

    /**
     * 根据视图的名称，获取零件的视图
     *
     * @param viewName
     * @return
     * @throws WTException
     * @author lbzhang
     * @date 2012-10-29下午06:08:28
     */
    @SuppressWarnings("deprecation")
    public static ViewReference getViewReferenceOfPartByName(String viewName) throws WTException {
        QuerySpec qs = new QuerySpec(View.class);
        qs.appendWhere(new SearchCondition(View.class, View.NAME, SearchCondition.EQUAL, viewName, false));
        QueryResult qr = PersistenceHelper.manager.find(qs);
        if (qr.hasMoreElements()) {
            View view = (View) qr.nextElement();
            ViewReference vr = ViewReference.newViewReference(view);
            return vr;
        }
        return null;
    }

    /**
     * 根据视图的名称，获取零件的视图
     *
     * @param viewName
     * @return
     * @throws WTException
     * @author qianlong
     * @date 2012-11-21
     */
    @SuppressWarnings("deprecation")
    public static View getViewByName(String viewName) throws WTException {
        View view = null;
        QuerySpec qs = new QuerySpec(View.class);
        qs.appendWhere(new SearchCondition(View.class, View.NAME, SearchCondition.EQUAL, viewName, false), index);
        QueryResult qr = PersistenceHelper.manager.find(qs);
        if (qr.hasMoreElements()) {
            view = (View) qr.nextElement();
        }
        return view;
    }

    /**
     * 通过零件和零件的视图，获取最新视图版本的零件对象
     *
     * @param part
     * @param viewName
     * @return
     * @throws WTException
     * @author qianlong
     * @date 2013-6-7
     */
    public static WTPart getLatestPartByNumberAndView(WTPart part, String viewName) throws WTException {
        View view = getViewByName(viewName);
        if (view == null) {
            return null;
        }
        long viewOid = view.getPersistInfo().getObjectIdentifier().getId();
        GLLogger.debug("viewName:" + viewName);
        GLLogger.debug("viewOid:" + viewOid);
        return getLatestPartByNumberAndView(part.getNumber(), viewOid);
    }

    /**
     * 通过零件和零件的视图，获取最新视图版本的零件对象
     *
     * @param partNumber
     * @param viewName
     * @return
     * @throws WTException
     * @author qianlong
     * @date 2013-6-7
     */
    public static WTPart getLatestPartByNumberAndView(String partNumber, String viewName) throws WTException {
        View view = getViewByName(viewName);
        if (view == null) {
            return null;
        }
        long viewOid = view.getPersistInfo().getObjectIdentifier().getId();
        return getLatestPartByNumberAndView(partNumber, viewOid);
    }

    /**
     * 通过零件和零件的视图，获取最新视图版本的上一个版本的零件对象
     *
     * @param partNumber
     * @param viewName
     * @return
     * @throws WTException
     * @author lbzhang
     * @date 2013-8-2
     */
    public static WTPart getPrevPartByNumberAndVeiw(String partNumber, String viewName) throws WTException {
        View view = getViewByName(viewName);
        if (view == null) {
            return null;
        }
        long viewOid = view.getPersistInfo().getObjectIdentifier().getId();
        GLLogger.debug("viewName:" + viewName);
        GLLogger.debug("viewOid:" + viewOid);
        return getPrevPartByNumberAndVeiw(partNumber, viewOid);
    }

    @SuppressWarnings("deprecation")
    public static WTPart getPrevPartByNumberAndVeiw(String partNumber, long viewOid) throws WTException {
        QuerySpec qs = new QuerySpec(WTPart.class);
        qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, partNumber),
                new int[]{0});

        qs.appendAnd();
        qs.appendWhere(new SearchCondition(WTPart.class, "view.key.id", SearchCondition.EQUAL, viewOid),
                new int[]{0});

        qs.setAdvancedQueryEnabled(true);

        QueryResult qr = PersistenceHelper.manager.find(qs);
        QueryResult preQr = qr;
        LatestConfigSpec lc = new LatestConfigSpec();
        qr = lc.process(qr);
        String prevVer = "";
        if (qr.hasMoreElements()) {
            WTPart temp = (WTPart) qr.nextElement();
            prevVer = temp.getVersionIdentifier().getValue();

            if ("".equals(prevVer)) {
                return temp;
            }
        }

        prevVer = getPreObject(prevVer);
        ArrayList<WTPart> partList = new ArrayList<WTPart>();
        while (preQr.hasMoreElements()) {
            WTPart temp = (WTPart) preQr.nextElement();
            String version = temp.getVersionIdentifier().getValue();
            if (version.equals(prevVer)) {
                partList.add(temp);
            }
        }
        String latestVersion = "";
        WTPart latestPart = null;
        for (int m = 0; m < partList.size(); m++) {
            WTPart temp = partList.get(m);
            String version = temp.getVersionIdentifier().getValue() + "." + temp.getIterationIdentifier().getValue();
            if (latestVersion.compareTo(version) < 0) {
                latestVersion = version;
                latestPart = temp;
            }
        }

        return latestPart;
    }

    public static String getPreObject(String str) {
        String prevVer = "";
        if (str.indexOf(".") > 0) {
            prevVer = str.substring(str.indexOf(".") + 1, str.length());
            if (prevVer.equals("A")) {
                return str;
            }
            char nowC = prevVer.charAt(0);
            int nowI = (int) nowC;
            nowI = nowI - 1;
            char prevC = (char) nowI;
            String prevS = String.valueOf(prevC);
            prevS = str.substring(0, str.indexOf(".") + 1) + prevS;
            return prevS;
        } else {
            prevVer = str;
            if (prevVer.equals("A")) {
                return str;
            }
            char nowC = prevVer.charAt(0);
            int nowI = (int) nowC;
            nowI = nowI - 1;
            char prevC = (char) nowI;
            String prevS = String.valueOf(prevC);
            return prevS;
        }
    }

    /**
     * 通过零件和零件的视图，获取最新视图版本的零件对象
     *
     * @param partNumber
     * @param viewOid
     * @return
     * @throws WTException
     * @author qianlong
     * @date 2013-6-7
     */
    @SuppressWarnings("deprecation")
    public static WTPart getLatestPartByNumberAndView(String partNumber, long viewOid) throws WTException {

        QuerySpec qs = new QuerySpec(WTPart.class);
        qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, partNumber),
                new int[]{0});

        qs.appendAnd();
        qs.appendWhere(new SearchCondition(WTPart.class, "view.key.id", SearchCondition.EQUAL, viewOid),
                new int[]{0});

        qs.setAdvancedQueryEnabled(true);

        QueryResult qr = PersistenceHelper.manager.find(qs);
        LatestConfigSpec lc = new LatestConfigSpec();
        qr = lc.process(qr);
        if (qr.hasMoreElements()) {
            WTPart temp = (WTPart) qr.nextElement();
            GLLogger.debug(temp.getName() + "  " + temp.getViewName() + "  " + temp.getVersionIdentifier().getValue()
                    + "." + temp.getIterationIdentifier().getValue());
            return temp;
        } else {
            return null;
        }
    }

    /**
     * 获取part
     *
     * @param number
     * @param viewName
     * @return
     * @throws WTException
     * @author fly
     * @date 2012-11-23上午09:21:05
     */
    @SuppressWarnings("deprecation")
    public static WTPart getPartByNumberAndVersion(String partNumber, String viewName, String version)
            throws WTException {
        View view = getViewByName(viewName);
        if (view == null) {
            return null;
        }
        String viewOid = view.getPersistInfo().getObjectIdentifier().toString();
        String idA3View = viewOid.substring(viewOid.indexOf(":") + 1, viewOid.length());
        GLLogger.debug("viewName:" + viewName);
        GLLogger.debug("idA3View:" + idA3View);

        QuerySpec qs = new QuerySpec(WTPart.class);
        qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, partNumber),
                new int[]{0});

        qs.appendAnd();
        qs.appendWhere(
                new SearchCondition(WTPart.class, "view.key.id", SearchCondition.EQUAL, Long.parseLong(idA3View)),
                new int[]{0});

        qs.setAdvancedQueryEnabled(true);
        QueryResult qr = PersistenceHelper.manager.find(qs);
        LatestConfigSpec lc = new LatestConfigSpec();
        qr = lc.process(qr);
        if (qr.hasMoreElements()) {
            WTPart temp = (WTPart) qr.nextElement();
            GLLogger.debug(temp.getName() + "  " + temp.getViewName() + "  " + temp.getVersionIdentifier().getValue()
                    + "." + temp.getIterationIdentifier().getValue());
            System.out.println("out=" + temp.getIterationDisplayIdentifier().toString());
            System.out.println("version=" + version);
            if (temp.getIterationDisplayIdentifier().toString().startsWith(version)) {
                return temp;
            }
        }
        return null;
    }

    /**
     * 根据零件编号判断是否存在此零件
     *
     * @param partNumber
     * @return
     * @throws WTException
     * @author lbzhang
     * @date 2012-10-30上午08:59:16
     */
    public static boolean isPartExist(String partNumber) throws WTException {
        WTPart part = getLatestPartByPartNumber(partNumber);
        if (part != null) {
            return true;
        }
        return false;
    }

    /**
     * 设置零件的软属性
     *
     * @param part
     * @param partInfo
     * @return
     * @throws Exception
     * @author lbzhang
     * @date 2012-10-30下午06:12:47
     */
    public static WTPart setSoftAttributeOfPlanningPart(WTPart part, Element e) throws Exception {

        if (isHasTheSoftAttri(part, "backupRate")) {
            GLLogger.debug("set backupRate");
            if (!"".equals(e.getAttributeValue("backupRate"))) {
                part = setWTPartSoftAttributeInt(part, "backupRate", e.getAttributeValue("backupRate"));
            }
        }
        if (isHasTheSoftAttri(part, "materialName")) {
            GLLogger.debug("set materialName");
            part = setWTPartSoftAttributeString(part, "materialName", e.getAttributeValue("materialName"));
        }

        if (isHasTheSoftAttri(part, "materialType")) {
            GLLogger.debug("set materialType");
            part = setWTPartSoftAttributeString(part, "materialType", e.getAttributeValue("materialType"));
        }

        if (isHasTheSoftAttri(part, "materialNumber")) {
            GLLogger.debug("set materialNumber");
            part = setWTPartSoftAttributeString(part, "materialNumber", e.getAttributeValue("materialNumber"));
        }

        if (isHasTheSoftAttri(part, "remark")) {
            GLLogger.debug("set remark");
            if (!"".equals(e.getAttributeValue("remark"))) {
                part = setWTPartSoftAttributeString(part, "remark", e.getAttributeValue("remark"));
            }
        }
        if (isHasTheSoftAttri(part, "rate")) {
            GLLogger.debug("set rate");
            if (!"".equals(e.getAttributeValue("rate"))) {
                part = setWTPartSoftAttributeString(part, "rate", e.getAttributeValue("rate"));
            }
        }
        if (isHasTheSoftAttri(part, "isKey")) {
            GLLogger.debug("set isKey");
            if (!"".equals(e.getAttributeValue("isKey"))) {
                part = setWTPartSoftAttributeBoolean(part, "isKey", e.getAttributeValue("isKey"));
            }
        }
        if (isHasTheSoftAttri(part, "isSpecial")) {
            GLLogger.debug("set isSpecial");
            if (!"".equals(e.getAttributeValue("isSpecial"))) {
                part = setWTPartSoftAttributeBoolean(part, "isSpecial", e.getAttributeValue("isSpecial"));
            }
        }
        if (isHasTheSoftAttri(part, "maxBackupCount")) {
            GLLogger.debug("set maxBackupCount");
            if (!"".equals(e.getAttributeValue("maxBackupCount"))) {
                part = setWTPartSoftAttributeInt(part, "maxBackupCount", e.getAttributeValue("maxBackupCount"));
            }
        }
        if (isHasTheSoftAttri(part, "backupReason")) {
            GLLogger.debug("set backupReason");
            if (!"".equals(e.getAttributeValue("backupReason"))) {
                part = setWTPartSoftAttributeString(part, "backupReason", e.getAttributeValue("backupReason"));
            }
        }
        if (isHasTheSoftAttri(part, "workShop")) {
            GLLogger.debug("set workShop");
            part = setWTPartSoftAttributeString(part, "workShop", e.getAttributeValue("workShop"));
        }
        return part;
    }

    /**
     * 判断零件对象是否有此软属性
     *
     * @param part
     * @param attributeName
     * @return
     * @author lbzhang
     * @date 2012-10-30下午01:39:40
     */
    public static boolean isHasTheSoftAttri(WTPart part, String attributeName) {
        AttributeDefDefaultView addv = null;
        try {
            addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(attributeName);
        } catch (IBADefinitionException e) {
            e.printStackTrace();
        } catch (NotAuthorizedException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        }
        if (addv == null) {
            return false;
        }
        return true;
    }

    /**
     * 根据零件软属性名称和值，设置软属性，软属性为字符串类型
     *
     * @param part
     * @param ibaName
     * @param ibaValue
     * @return
     * @throws Exception
     * @author lbzhang
     * @date 2012-10-30下午01:47:52
     */
    public static WTPart setWTPartSoftAttributeString(WTPart part, String ibaName, String ibaValue) throws Exception {
        try {
            if ("".equals(ibaValue)) {
                ibaValue = " ";
            }
            IBAHelper iba = new IBAHelper(part);
            iba.setIBAValue(part, ibaName, ibaValue);
            iba.updateAttributeContainer(part);
            iba.updateIBAHolder(part);
            part = (WTPart) PersistenceHelper.manager.refresh(part);
            return part;
        } catch (WTException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Set soft attribute " + ibaName + " failure!");
        }
        return null;
    }

    public static WTPart setWTPartSoftAttributeInt(WTPart part, String ibaName, String ibaValue) throws Exception {
        IBAHelper.setIBAIntegerValue(part, ibaName, Integer.parseInt(ibaValue));
        part = (WTPart) PersistenceHelper.manager.refresh(part);
        return part;
    }

    /**
     * 设置零件的软属性，软属性为boolean类型
     *
     * @param part
     * @param ibaName
     * @param booleanValue
     * @return
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws RemoteException
     * @author lbzhang
     * @date 2012-11-13上午11:41:08
     */
    public static WTPart setWTPartSoftAttributeBoolean(WTPart part, String ibaName, String booleanValue)
            throws WTException, WTPropertyVetoException, RemoteException {
        IBAHelper.setIBABooleanValue(part, ibaName, Boolean.parseBoolean(booleanValue));
        // part = (WTPart) PersistenceHelper.manager.refresh(part);
        return part;
    }

    /**
     * 创建工艺辅件
     *
     * @param partMap
     * @param isKey
     * @param isSpecial
     * @param type      创建零件的类型
     * @return
     * @author lbzhang
     * @date 2012-10-30下午06:27:16
     */
    @SuppressWarnings("deprecation")
    public static WTPart createProcessAssistantOrMiddlePart(HashMap<String, String> partMap, boolean isKey,
                                                            boolean isSpecial, String type) {
        String partName = partMap.get("partName");// 零件名称
        String partNumber = partMap.get("partNumber");// 零件编号
        String backupReason = partMap.get("backupReason");// 备份原因
        String workShop = partMap.get("workShop");// 制造单位
        String maxBackupCount = partMap.get("maxBackupCount");// 最大备份数
        String backupRate = partMap.get("backupRate");// 工艺备份比例
        String materialType = partMap.get("materialType");// 物料类型
        String remark = partMap.get("remark");// 备注

        try {
            WTPart part = WTPart.newWTPart();
            part.setName(partName);
            part.setNumber(partNumber);
            WTContainer container = WTContainerUtil.getContainerByName("测试产品1");// 获取零件的容器
            WTContainerRef containerRef = WTContainerRef.newWTContainerRef(container);// 获取零件的容器引用
            part.setContainer(container);// 设置零件的容器
            ViewReference viewReference = getViewReferenceOfPartByName("Manufacturing");// 获取零件的视图
            part.setView(viewReference);// 设置零件的视图
            GLLogger.debug("==viewReference===");
            if (type.equals("1")) {
                System.out.println("==1==");
                TypeDefinitionReference typeRef = ClientTypedUtility
                        .getTypeDefinitionReference("wt.part.WTPart|com.nriet.ProcessAssistantPart");// 获取物件类型为工艺辅件的零件类型
                part.setTypeDefinitionReference(typeRef);// 设置零件的类型
            } else {
                System.out.println("==2==");
                TypeDefinitionReference typeRef = ClientTypedUtility
                        .getTypeDefinitionReference("wt.part.WTPart|com.nriet.ProcessMiddlePart");// 获取物件类型为工艺中间件的零件类型
                part.setTypeDefinitionReference(typeRef);// 设置零件的类型
            }
            Folder folder = FolderHelper.service.getFolder("/Default", containerRef);
            FolderHelper.assignFolder(part, folder);// 指派零件到指定容器和指定路径下

            System.out.println("====save===");
            part = (WTPart) PersistenceHelper.manager.save(part);
            System.out.println("====type==?" + TypedUtilityServiceHelper.service.getExternalTypeIdentifier(part));
            // part = (WTPart)PersistenceHelper.manager.refresh(part);

            part = setWTPartSoftAttributeString(part, "backupReason", backupReason);
            part = setWTPartSoftAttributeString(part, "workShop", workShop);
            part = setWTPartSoftAttributeString(part, "maxBackupCount", maxBackupCount);
            part = setWTPartSoftAttributeString(part, "backupRate", backupRate);
            part = setWTPartSoftAttributeString(part, "materialType", materialType);
            part = setWTPartSoftAttributeString(part, "remark", remark);

            return part;
        } catch (WTException e) {
            e.printStackTrace();
            GLLogger.debug("Create ProcessAssistantPart failure!");
        } catch (WTPropertyVetoException e) {
            e.printStackTrace();
            GLLogger.debug("Set part's Container failure!");
        } catch (RemoteException e) {
            e.printStackTrace();
            GLLogger.debug("Set Part's view failure!");
        } catch (Exception e) {
            e.printStackTrace();
            GLLogger.debug("Set part's soft attribute failure!");
        }

        return null;
    }

    /**
     * 设置零件的生命周期
     *
     * @param part
     * @param state
     * @throws WTInvalidParameterException
     * @throws LifeCycleException
     * @throws WTException
     * @author lbzhang
     * @date 2012-10-31上午09:41:15
     */
    public static void setWTPartTheLifecycle(WTPart part, String state) throws WTInvalidParameterException,
            LifeCycleException, WTException {
        if (part == null) {
            return;
        }
        LifeCycleHelper.service.setLifeCycleState(part, State.toState(state));
    }

    /**
     * 判断零件是否有子阶零件
     *
     * @param part
     * @return
     * @author lbzhang
     * @date 2012-11-8上午10:17:49
     */
    @SuppressWarnings("deprecation")
    public static boolean isPartHasChild(WTPart part) {
        try {
            QueryResult qr = WTPartHelper.service.getUsesWTParts((WTPart) part, getConfigSpec());
            if (qr != null) {
                if (qr.hasMoreElements()) {
                    return true;
                }
            }
        } catch (WTException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取零件关联的描述/说明文件并返回文件名称，返回下载到本地的地址路径
     *
     * @param part
     * @return
     * @throws WTException
     * @throws PropertyVetoException
     * @throws IOException
     * @throws FileNotFoundException
     * @author lbzhang
     * @date 2012-11-12下午06:05:09
     */
    @SuppressWarnings("deprecation")
    public static String downloadAndReturnDescriptedDocName(WTPart part) throws WTException, PropertyVetoException,
            FileNotFoundException, IOException {
        String appFileName = "";
        QueryResult qr = StructHelper.service.navigateDescribedBy(part, WTPartDescribeLink.class, true);
        if (qr.hasMoreElements()) {
            WTDocument doc = (WTDocument) qr.nextElement();
            FormatContentHolder holder = (FormatContentHolder) ContentHelper.service.getContents(doc);
            ApplicationData currdata = (ApplicationData) ContentHelper.service.getPrimary(holder);
            InputStream is = ContentServerHelper.service.findContentStream(currdata);
            appFileName = currdata.getFileName();
            FileOutputStream fos = new FileOutputStream(new File(Util.getTempPath() + File.separatorChar + appFileName));
            int i = 0;
            byte abyte[] = new byte[8192];
            while ((i = is.read(abyte, 0, abyte.length)) >= 0) {
                fos.write(abyte, 0, i);
            }
            is.close();
            fos.close();
            GLLogger.debug("===>" + Util.getTempPath() + File.separatorChar + appFileName);
            return Util.getTempPath() + File.separatorChar + appFileName;
        }
        return null;
    }

    /**
     * 获取零件关联的描述/说明文件对象
     *
     * @param part
     * @return
     * @throws WTException
     * @author lbzhang
     * @date 2012-11-15下午12:37:27
     */
    @SuppressWarnings("deprecation")
    public static WTDocument getWTDocumentOfDescripedDoc(WTPart part) throws WTException {
        QueryResult qr = StructHelper.service.navigateDescribedBy(part, WTPartDescribeLink.class, true);
        if (qr.hasMoreElements()) {
            WTDocument doc = (WTDocument) qr.nextElement();
            return doc;
        }
        return null;
    }

    /**
     * 获取零件关联的描述/说明文件的文件流(说明文档的类型是装配工艺)
     *
     * @param part
     * @return
     * @throws WTException
     * @throws PropertyVetoException
     * @author lbzhang
     * @date 2012-11-15上午10:46:47
     */
    @SuppressWarnings("deprecation")
    public static InputStream getInputStreamOfDescriptedDoc(WTPart part) throws WTException, PropertyVetoException,
            RemoteException {
        part = getLatestPartByNumberAndView(part, Constants.planning);
        if (part == null) {
            return null;
        }

        WTPart pPart = WTPartUtil.getLatestPartByNumberAndView(part.getNumber(), "Planning");
        WTDocument doc = PBOMHelper.getBOMXmlDoc(pPart, Constants.pbomDocEndwith);
        if (doc != null) {
            FormatContentHolder holder = (FormatContentHolder) ContentHelper.service.getContents(doc);
            ApplicationData currdata = (ApplicationData) ContentHelper.service.getPrimary(holder);
            InputStream is = ContentServerHelper.service.findContentStream(currdata);
            return is;
        }
        return null;
    }

    /**
     * 计划员派工
     *
     * @param ebomPart
     * @return //直接派工给组员的信息
     * @throws FileNotFoundException
     * @throws TransformerConfigurationException
     * @throws WTException
     * @throws PropertyVetoException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerException
     * @throws TransformerFactoryConfigurationError
     * @author lbzhang
     * @date 2012-12-20上午11:32:59
     */
    public static HashMap<String, String> allocate_DESIGNERSYSTEM(WTPart ebomPart, Object self)
            throws FileNotFoundException, TransformerConfigurationException, WTException, PropertyVetoException,
            IOException, ParserConfigurationException, SAXException, TransformerException,
            TransformerFactoryConfigurationError {
        ebomPart = getLatestPartByNumberAndView(ebomPart, Constants.planning);
        HashMap<String, HashMap<String, String>> userMap = modifyXMLAndUpload_DESIGNERSYSTEM(ebomPart);

        HashMap<String, String> allUser = userMap.get("alluser");// 组长和组员信息
        HashMap<String, String> teamUser = userMap.get("team");// 直接派工给组员的信息(零件编号，责任人)

        String leaderUsersStr = allUser.get("leaderUsersStr");// 组长人员信息
        String teamMembersStr = allUser.get("teamMembersStr");// 组员人员信息
        GLLogger.debug("leaderUsersStr===>" + leaderUsersStr);
        GLLogger.debug("teamMembersStr===>" + teamMembersStr);

        WorkflowUtil.addPrincipalToProcessActivity(leaderUsersStr, "TECHNICALLEADER", self);// 指派组长
        WorkflowUtil.addPrincipalToProcessActivity(teamMembersStr, "TECHNICTASKTEAM", self);// 指派组员

        return teamUser;
    }

    /**
     * 修改并上传PBOM的XML, 由计划员修改，并返回给流程需要派工工艺的人员
     *
     * @param part
     * @throws FileNotFoundException
     * @throws WTException
     * @throws PropertyVetoException
     * @throws IOException
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws TransformerConfigurationException
     * @author lbzhang
     * @date 2012-11-15上午11:33:42
     */
    public static HashMap<String, HashMap<String, String>> modifyXMLAndUpload_DESIGNERSYSTEM(WTPart ebomPart)
            throws FileNotFoundException, WTException, PropertyVetoException, IOException,
            TransformerConfigurationException, ParserConfigurationException, SAXException, TransformerException,
            TransformerFactoryConfigurationError {
        // 下载零件关联的PBOM的XML
        // String filePath = downloadAndReturnDescriptedDocName(ebomPart);
        // GLLogger.debug("download part's PBOM_XML file:" + filePath);

        // 更新PBOM的XML内容
        HashMap<String, HashMap<String, String>> usersMap = updateXML_DESIGNERSYSTEM(ebomPart);
        GLLogger.debug("updatexml over===DESIGNERSYSTEM>");
        return usersMap;
    }

    /**
     * 计划员更新PBOM的XML，专更新PBOM零件的派工责任人
     * <p>
     * 传递的零件为EBOM，需找到PBOM的零件
     *
     * @param filePath
     * @param ebomPart
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     * @throws TransformerFactoryConfigurationError
     * @throws WTException
     * @throws PropertyVetoException
     * @author lbzhang
     * @date 2012-11-15下午12:17:40
     */
    @SuppressWarnings({"deprecation", "unchecked"})
    public static HashMap<String, HashMap<String, String>> updateXML_DESIGNERSYSTEM(WTPart ebomPart)
            throws ParserConfigurationException, SAXException, IOException, TransformerConfigurationException,
            TransformerException, TransformerFactoryConfigurationError, WTException, PropertyVetoException {
        String leaderUsersStr = "";// 计划员派工给组长的信息
        String teamMembersStr = "";// 计划员派工给组员的信息
        HashMap<String, String> teamMap = new HashMap<String, String>();// 计划员直接派工给组员的记录

        // 修改XML内容
        InputStream is = null;
        ApplicationData currdata = null;
        WTDocument doc = null;

        doc = PBOMHelper.getBOMXmlDoc(ebomPart, Constants.pbomDocEndwith);
        if (null == doc) {
            return null;
        }
        FormatContentHolder holder = (FormatContentHolder) ContentHelper.service.getContents(doc);
        currdata = (ApplicationData) ContentHelper.service.getPrimary(holder);
        is = ContentServerHelper.service.findContentStream(currdata);
        HashMap<String, String> map = new HashMap<String, String>();
        SWXMLUtil xmlUtil = new SWXMLUtil(is);
        Element rootElement = xmlUtil.getRootElement();
        if ("Product".equals(rootElement.getName())) {
            Element partElement = rootElement.getChild("parts");
            if (partElement != null) {
                for (Element partAttrElement : (List<Element>) partElement.getChildren("QMPartInfo")) {
                    WTPart part = null;
                    String oid = partAttrElement.getAttributeValue("oid");
                    String partNumber = partAttrElement.getAttributeValue("partNumber");
                    if ("".equals(oid)) {
                        part = WTPartUtil.getLatestPartByPartNumber(partNumber);
                    } else {
                        WTPart partTemp = (WTPart) com.glaway.mpm.util.ReferenceFactory
                                .getObjectbyOid("wt.part.WTPart:" + oid);
                        part = WTPartUtil.getLatestPartByNumberAndView(partTemp, Constants.planning);
                    }
                    String responsor = Util.getSoftAttribute(part, "responsor");
                    String group = Util.getSoftAttribute(part, "technicGroup");
                    if (responsor == null) {
                        responsor = "";
                    }
                    GLLogger.debug("responser===>" + responsor);
                    GLLogger.debug("group=======>" + group);
                    if (group != null && group.indexOf("组长") > 0) {// 如果此零件派工给组长，则记录下组长的名称
                        leaderUsersStr += responsor + ",";
                        map.put("leaderUsersStr", leaderUsersStr);
                    } else {// 否则就是没有对此零件进行派工或者此零件直接派工给组员人员
                        teamMembersStr += responsor + ",";
                        teamMap.put(partNumber, responsor);
                        map.put("teamMembersStr", teamMembersStr);
                    }
                    partAttrElement.setAttribute("responser", responsor);

                    for (Element partChildElement : (List<Element>) partAttrElement.getChildren("childs")) {
                        GLLogger.debug("---partChildElement-" + partChildElement.getName());
                        structureChildPart_DESIGNERSYSTEM(partChildElement, map, teamMap);
                    }
                }
            }
        }

        HashMap<String, HashMap<String, String>> designMap = new HashMap<String, HashMap<String, String>>();
        designMap.put("alluser", map);
        designMap.put("team", teamMap);

        ByteArrayOutputStream boutput = new ByteArrayOutputStream();
        Format format = Format.getPrettyFormat();
        format.setEncoding("GBK");

        XMLOutputter xmlOutput = new XMLOutputter(format);

        xmlOutput.output(xmlUtil.getDocument(), boutput);

        byte[] byteS = boutput.toByteArray();

        // 重新上传PBOM的XML
        doc = WTDocumentUtil.setPrimaryForDocument(doc, currdata.getFileName(), byteS);
        GLLogger.debug("the EBOM part has been uploaded new XML!");
        return designMap;
    }

    /**
     * 构建子PBOM-计划员
     *
     * @param childPartElement
     * @param users
     * @throws WTException
     * @author lbzhang
     * @date 2012-11-22下午03:59:22
     */
    @SuppressWarnings("unchecked")
    public static void structureChildPart_DESIGNERSYSTEM(Element childPartElement, HashMap<String, String> map,
                                                         HashMap<String, String> teamMap) throws WTException {
        for (Element childPartAttrElement : (List<Element>) childPartElement.getChildren("QMPartInfo")) {
            GLLogger.debug("---childPartAttrElement-" + childPartAttrElement.getName());

            WTPart part = null;
            String oid = childPartAttrElement.getAttributeValue("oid");
            String partNumber = childPartAttrElement.getAttributeValue("partNumber");
            GLLogger.debug("partNumber===>" + partNumber);
            if ("".equals(oid)) {
                part = WTPartUtil.getLatestPartByPartNumber(partNumber);
                GLLogger.debug("part.....>" + part.getName());
                part = WTPartUtil.getLatestPartByNumberAndView(part, Constants.planning);
            } else {
                WTPart partTemp = (WTPart) com.glaway.mpm.util.ReferenceFactory.getObjectbyOid("wt.part.WTPart:" + oid);
                part = WTPartUtil.getLatestPartByNumberAndView(partTemp, Constants.planning);
            }
            GLLogger.debug("part-->" + part.getName() + "   " + part.getViewName());
            String responsor = Util.getSoftAttribute(part, "responsor");
            String group = Util.getSoftAttribute(part, "technicGroup");
            if (responsor == null) {
                responsor = "";
            }

            GLLogger.debug("responser===>" + responsor);
            GLLogger.debug("group=======>" + group);

            if (group != null && group.indexOf("组长") > 0) {// 如果此零件派工给组长，则记录下组长的名称
                String mapStr = map.get("leaderUsersStr");
                if (mapStr == null) {
                    mapStr = "";
                }
                String leaderUsersStr = mapStr + responsor + ",";
                map.put("leaderUsersStr", leaderUsersStr);
            } else {// 否则就是没有对此零件进行派工或者此零件直接派工给组员人员
                String mapStr = map.get("teamMembersStr");
                if (mapStr == null) {
                    mapStr = "";
                }
                String teamMembersStr = mapStr + responsor + ",";
                teamMap.put(partNumber, responsor);
                map.put("teamMembersStr", teamMembersStr);
            }

            childPartAttrElement.setAttribute("responser", responsor);

            for (Element subChildPartElement : (List<Element>) childPartAttrElement.getChildren("childs")) {
                GLLogger.debug("---subChildPartElement-" + subChildPartElement.getName());
                structureChildPart_DESIGNERSYSTEM(subChildPartElement, map, teamMap);
            }
        }
    }

    /**
     * 修改并上传PBOM的XML, 由工艺组长修改
     *
     * @param ebomPart
     * @return
     * @throws FileNotFoundException
     * @throws WTException
     * @throws PropertyVetoException
     * @throws IOException
     * @throws TransformerConfigurationException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerException
     * @throws TransformerFactoryConfigurationError
     * @author lbzhang
     * @date 2012-11-17上午11:54:05
     */
    public static String modifyXMLAndUpload_LEADER(WTPart ebomPart, HashMap<String, String> teamUser)
            throws FileNotFoundException, WTException, PropertyVetoException, IOException,
            TransformerConfigurationException, ParserConfigurationException, SAXException, TransformerException,
            TransformerFactoryConfigurationError {
        // 下载零件关联的PBOM的XML
        // String filePath = downloadAndReturnDescriptedDocName(ebomPart);
        // GLLogger.debug("download part's PBOM_XML file:" + filePath);

        // 更新PBOM的XML内容
        String users = updateXML_LEADER(ebomPart, teamUser);
        GLLogger.debug("updatexml over===LEADER>" + users);
        return users;
    }

    /**
     * 更新PBOM的XML，专更新PBOM零件的派工责任人
     * <p>
     * 传递的零件为EBOM，需找到PBOM的零件
     *
     * @param ebomPart
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     * @throws TransformerFactoryConfigurationError
     * @throws WTException
     * @throws PropertyVetoException
     * @author lbzhang
     * @date 2012-11-17上午11:57:37
     */
    @SuppressWarnings({"deprecation", "unchecked"})
    public static String updateXML_LEADER(WTPart ebomPart, HashMap<String, String> teamUser)
            throws ParserConfigurationException, SAXException, IOException, TransformerConfigurationException,
            TransformerException, TransformerFactoryConfigurationError, WTException, PropertyVetoException {
        String users = "";

        // 修改XML内容
        InputStream is = null;
        ApplicationData currdata = null;
        WTDocument doc = null;
        WTPart pPart = getLatestPartByNumberAndView(ebomPart, Constants.planning);
        doc = PBOMHelper.getBOMXmlDoc(pPart, Constants.pbomDocEndwith);
        if (null == doc) {
            return "";
        }
        FormatContentHolder holder = (FormatContentHolder) ContentHelper.service.getContents(doc);
        currdata = (ApplicationData) ContentHelper.service.getPrimary(holder);
        is = ContentServerHelper.service.findContentStream(currdata);
        HashMap<String, String> map = new HashMap<String, String>();
        SWXMLUtil xmlUtil = new SWXMLUtil(is);
        Element rootElement = xmlUtil.getRootElement();
        if ("Product".equals(rootElement.getName())) {
            Element partElement = rootElement.getChild("parts");
            if (partElement != null) {
                for (Element partAttrElement : (List<Element>) partElement.getChildren("QMPartInfo")) {
                    WTPart part = null;
                    String oid = partAttrElement.getAttributeValue("oid");
                    String partNumber = partAttrElement.getAttributeValue("partNumber");
                    if ("".equals(oid)) {
                        part = WTPartUtil.getLatestPartByPartNumber(partNumber);
                    } else {
                        WTPart partTemp = (WTPart) com.glaway.mpm.util.ReferenceFactory
                                .getObjectbyOid("wt.part.WTPart:" + oid);
                        part = WTPartUtil.getLatestPartByNumberAndView(partTemp, Constants.planning);
                    }
                    String responsor = Util.getSoftAttribute(part, "responsor");
                    if (responsor == null) {
                        responsor = "";
                    }
                    GLLogger.debug("responser:" + responsor);

                    if (!teamUser.containsKey(partNumber)) {
                        users += responsor + ",";
                    }

                    partAttrElement.setAttribute("responser", responsor);

                    map.put("users", users);
                    for (Element partChildElement : (List<Element>) partAttrElement.getChildren("childs")) {
                        GLLogger.debug("---partChildElement-" + partChildElement.getName());
                        structureChildPart_LEADER(partChildElement, map, teamUser);
                    }
                }
            }
        }

        ByteArrayOutputStream boutput = new ByteArrayOutputStream();
        Format format = Format.getPrettyFormat();
        format.setEncoding("GBK");

        XMLOutputter xmlOutput = new XMLOutputter(format);

        xmlOutput.output(xmlUtil.getDocument(), boutput);

        byte[] byteS = boutput.toByteArray();

        // 重新上传PBOM的XML
        doc = WTDocumentUtil.setPrimaryForDocument(doc, currdata.getFileName(), byteS);
        GLLogger.debug("the EBOM part has been uploaded new XML!");
        return map.get("users");
    }

    /**
     * 构建子PBOM
     *
     * @param childPartElement
     * @param users
     * @throws WTException
     * @author lbzhang
     * @date 2012-11-22下午03:59:22
     */
    @SuppressWarnings("unchecked")
    public static void structureChildPart_LEADER(Element childPartElement, HashMap<String, String> map,
                                                 HashMap<String, String> teamUser) throws WTException {
        for (Element childPartAttrElement : (List<Element>) childPartElement.getChildren("QMPartInfo")) {
            GLLogger.debug("---childPartAttrElement-" + childPartAttrElement.getName());

            WTPart part = null;
            String oid = childPartAttrElement.getAttributeValue("oid");
            String partNumber = childPartAttrElement.getAttributeValue("partNumber");
            if ("".equals(oid)) {
                part = WTPartUtil.getLatestPartByPartNumber(partNumber);
            } else {
                WTPart partTemp = (WTPart) com.glaway.mpm.util.ReferenceFactory.getObjectbyOid("wt.part.WTPart:" + oid);
                part = WTPartUtil.getLatestPartByNumberAndView(partTemp, Constants.planning);
            }
            String responsor = Util.getSoftAttribute(part, "responsor");
            if (responsor == null) {
                responsor = "";
            }
            GLLogger.debug("responser:" + responsor);

            if (!teamUser.containsKey(partNumber)) {
                String mapStr = map.get("users");
                if (mapStr == null) {
                    mapStr = "";
                }
                String users = mapStr + responsor + ",";
                map.put("users", users);
            }

            childPartAttrElement.setAttribute("responser", responsor);

            for (Element subChildPartElement : (List<Element>) childPartAttrElement.getChildren("childs")) {
                GLLogger.debug("---subChildPartElement-" + subChildPartElement.getName());
                structureChildPart_LEADER(subChildPartElement, map, teamUser);
            }
        }
    }

    /**
     * 通过ebom零件获取最新的pbom零件
     *
     * @param ebomPart
     * @return
     * @throws WTException
     * @author lbzhang
     * @date 2012-11-15上午11:37:07
     */
    @SuppressWarnings("deprecation")
    public static WTPart getPBOMPartByEBOMPart(WTPart ebomPart) throws WTException {
        QuerySpec qs = new QuerySpec(WTPart.class);
        qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, ebomPart.getNumber(),
                false));

        QueryResult qr = PersistenceHelper.manager.find(qs);

        LatestConfigSpec lcs = new LatestConfigSpec();
        qr = lcs.process(qr);
        if (qr.hasMoreElements()) {
            return (WTPart) qr.nextElement();
        }
        return null;
    }

    // /**
    // * 获取零件对象的最新版序对象
    // *
    // * @author lbzhang
    // * @date 2012-11-15下午04:19:22
    // * @param part
    // * @return
    // * @throws PersistenceException
    // * @throws WTException
    // */
    // public static WTPart getSameVersionPlanningPart(WTPart part) throws
    // PersistenceException, WTException {
    // QueryResult qr = VersionControlHelper.service.allVersionsOf(part);
    // LatestConfigSpec lc = new LatestConfigSpec();
    // qr = lc.process(qr);
    // while (qr.hasMoreElements()) {
    // WTPart temp = (WTPart) qr.nextElement();
    // System.out.println("temp==>" + temp.getName() + "  " +
    // temp.getVersionIdentifier().getValue() + "."
    // + temp.getIterationIdentifier().getValue() + "  " + temp.getViewName());
    // return temp;
    // }
    // return part;
    // }

    /**
     * 删除零件原有的结构关联
     *
     * @param part
     * @throws WTException
     * @author lbzhang
     * @date 2012-11-22下午04:04:13
     */
    public static void deleteWTPartOriginalLinkByPart(WTPart part) throws WTException {
        QueryResult qr = PersistenceHelper.manager.navigate(part, WTPartUsageLink.USES_ROLE, WTPartUsageLink.class,
                false);
        GLLogger.debug("delet link qr.size===>" + qr.size());
        while (qr.hasMoreElements()) {
            WTPartUsageLink link = (WTPartUsageLink) qr.nextElement();
            PersistenceServerHelper.manager.remove(link);
        }
    }

    /**
     * 设置多个零件对象的状态为已归档状态
     *
     * @param wtlist
     * @throws WTInvalidParameterException
     * @throws LifeCycleException
     * @throws WTException
     * @author lbzhang
     * @date 2012-11-23上午10:35:40
     */
    public static void setPartsToRelease(WTList wtlist) throws WTInvalidParameterException, LifeCycleException,
            WTException {
        LifeCycleHelper.service.setLifeCycleState(wtlist, State.toState(Constants.RELEASED), false);
    }

    /**
     * 获取ebom的Planning视图的PBOM对象
     *
     * @param epart
     * @return
     * @throws WTException
     * @author lbzhang
     * @date 2012-11-23上午10:31:31
     */
    public static List<WTPart> getPlanningStructureByEPart(WTPart epart) throws WTException {

        List<WTPart> list = new ArrayList<WTPart>();
        Util.getAllChildParts(list, epart);
        List<WTPart> wtlist = new ArrayList<WTPart>();
        for (int i = 0; i < list.size(); i++) {
            WTPart temp = list.get(i);
            temp = getLatestPartByNumberAndView(temp, Constants.planning);

            wtlist.add(temp);
        }
        return wtlist;
    }

    /**
     * @param part
     * @param inputStream
     * @param fileName
     * @author qianlong
     * @date 2012-10-29
     */
    public static void saveFileAsPartSecondary(WTPart part, InputStream inputStream, String fileName) {

        try {
            ApplicationData appData = ApplicationData.newApplicationData(part);
            // 主物件和附件
            // ContentRoleType.SECONDARY 表示 附件
            // ContentRoleType.PRIMARY 表示主物件
            appData.setRole(ContentRoleType.SECONDARY);
            // 设置文件名称
            appData.setFileName(fileName);
            appData = ContentServerHelper.service.updateContent((ContentHolder) part, appData, inputStream); // 更新内容
            inputStream.close();
            part = (WTPart) ContentServerHelper.service.updateHolderFormat((FormatContentHolder) part); // 更新格式
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取零件的数量
     *
     * @param part
     * @param child
     * @return
     * @throws WTException
     * @author fly
     * @date 2013-5-22
     */
    public static double getPartQuantity(WTPart part, WTPart child) throws WTException {
        WTPartUsageLink link = getWTPartUsageLink(part, (WTPartMaster) child.getMaster());
        return link.getQuantity().getAmount();
    }

    public static boolean isAssemble(WTPart part) {
        boolean isAssemble = false;
        try {
            List<WTPart> parts = WTPartUtil.getChildPart(part);
            if (parts.size() > 0) {
                for (WTPart p : parts) {
                    if (!TypeIdentifierHelper.getType(p).getTypename().contains("com.nriet.ProcessAssistantPart")) {
                        return true;
                    }
                }
            }
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return isAssemble;
    }

    public static boolean updatePartNumber(WTPart part, String newNumber) {
        boolean flag = false;
        boolean bool = SessionServerHelper.manager.setAccessEnforced(false);
        Identified identified = (Identified) part.getMaster();
        WTPrincipal current = null;
        try {
            current = StandardSessionManager.newStandardSessionManager()
                    .getPrincipal();
            StandardSessionManager.newStandardSessionManager()
                    .setAdministrator();
            WTPartMasterIdentity masteridentity = (WTPartMasterIdentity) identified
                    .getIdentificationObject();
            masteridentity.setNumber(newNumber);
            identified = IdentityHelper.service.changeIdentity(identified,
                    masteridentity);
            PersistenceServerHelper.manager.update(part.getMaster());
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (current != null) {
                try {
                    StandardSessionManager.newStandardSessionManager()
                            .setPrincipal(current);
                } catch (WTException e) {
                    e.printStackTrace();
                }
                SessionServerHelper.manager.setAccessEnforced(bool);
            }
        }
        return flag;
    }


    /**
     * 创建PlanningPart 根据 number
     *
     * @param parentPart
     * @param number
     * @param name
     * @return
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws RemoteException
     */
    public static WTPart createPlanningPart(WTPart parentPart, String number, String name) throws WTException,
            WTPropertyVetoException, RemoteException {
        WTPart temp = getLatestPartByPartNumber(number);
        if (temp != null) {
            if (LoadConfig.getInstance().getPbomView().equals(temp.getViewName())) {
                //	temp = createPlanningPart(String.valueOf(PersistenceHelper.getObjectIdentifier(temp).getId()));
            } else {
                temp = createPlanningPart(String.valueOf(PersistenceHelper.getObjectIdentifier(temp).getId()));
            }
            return temp;
        }
        WTPart part = WTPart.newWTPart();
        part.setName(name);// 设置零件名称
        part.setNumber(number);// 设置零件的编号

        part.setContainer(parentPart.getContainer());// 设置零件的容器

        ViewReference viewReference = getViewReferenceOfPartByName(Constants.planning);// 获取零件的视图
        part.setView(viewReference);// 设置零件的视图

        Folder folder = null;

        String pobmpath = propertiesUtil.getProperty("planning-part-save-folder");
        TypeDefinitionReference typeRef = null;
        folder = FolderUtil.getFolder(pobmpath, parentPart.getContainerReference());

//	if ("middle".equals(partType)) {// 中间件
//		typeRef = TypedUtilityServiceHelper.service.getTypeDefinitionReference("com.nriet.ProcessMiddlePart");// 获取物件类型为中间件的零件类型
//		folder = FolderUtil.getFolder("/Default/70：PBOM/中间件", containerRef);
//	} else {
//		typeRef = TypedUtilityServiceHelper.service.getTypeDefinitionReference("com.nriet.ProcessAssistantPart");// 获取物件类型为工艺辅件的零件类型
//		folder = FolderUtil.getFolder("/Default/70：PBOM/辅件", containerRef);
//	}

        FolderHelper.assignFolder(part, folder);// 指派零件到指定容器和指定路径下
        part.setTypeDefinitionReference(parentPart.getTypeDefinitionReference());// 设置零件的类型

        part = (WTPart) PersistenceHelper.manager.save(part);
        part = (WTPart) PersistenceHelper.manager.refresh(part);
        return part;

//	return createPlanningPartByWzlb( parentPart ,  number, name, null);

    }

    public static WTPart createPlanningPartByWzlb(WTPart parentPart, String number, String name, String wzlbbm) throws WTException,
            WTPropertyVetoException, RemoteException {
        WTContainerRef cref = null;
        WTContainer container = null;
        String pobmpath = propertiesUtil.getProperty("planning-part-save-folder");
        String wzlb = null;
        if (wzlbbm != null) {
            if (wzlbbm.startsWith("01")) {
                wzlb = "01";
            } else if (wzlbbm.startsWith("02")) {
                wzlb = "02";
            } else {
                wzlb = "03";
            }
        }
        ERPService service = new ERPService();

        if ("01".equals(wzlb)) {//元器件
            container = WTContainerUtil.getLibraryByName("元器件");
            cref = container.getContainerReference();
//		pobmpath = propertiesUtil.getProperty("yqj_planning-part-save-folder");
            //pobmpath = service.genFolderByWzkClass("01", wzlbbm);
            pobmpath = propertiesUtil.getProperty("planning-part-save-folder2");
        } else if ("02".equals(wzlb)) {//标准件
            container = WTContainerUtil.getLibraryByName("标准件");
            cref = container.getContainerReference();
//		pobmpath = propertiesUtil.getProperty("bzj_planning-part-save-folder");
            //pobmpath = service.genFolderByWzkClass("01", wzlbbm);
            pobmpath = propertiesUtil.getProperty("planning-part-save-folder2");
        } else {
            container = parentPart.getContainer();
            cref = parentPart.getContainerReference();
//		pobmpath = "";
        }

        WTPart temp = getLatestPartByPartNumber(number);
        if (temp != null) {
            if (LoadConfig.getInstance().getPbomView().equals(temp.getViewName())) {
                //	temp = createPlanningPart(String.valueOf(PersistenceHelper.getObjectIdentifier(temp).getId()));
            } else {
                temp = createPlanningPart(String.valueOf(PersistenceHelper.getObjectIdentifier(temp).getId()), container, pobmpath);
            }
            return temp;
        }
        WTPart part = WTPart.newWTPart();
        part.setName(name);// 设置零件名称
        part.setNumber(number);// 设置零件的编号

        part.setContainer(container);// 设置零件的容器
//	part.setContainerReference(WTContainerRef.newWTContainerRef(cref));

        ViewReference viewReference = getViewReferenceOfPartByName(Constants.planning);// 获取零件的视图
        part.setView(viewReference);// 设置零件的视图

        Folder folder = null;

        TypeDefinitionReference typeRef = null;
        folder = FolderUtil.getFolder(pobmpath, WTContainerRef.newWTContainerRef(container));
        typeRef = TypedUtilityServiceHelper.service.getTypeDefinitionReference("wt.part.WTPart");
        //	if ("middle".equals(partType)) {// 中间件
        //		typeRef = TypedUtilityServiceHelper.service.getTypeDefinitionReference("com.nriet.ProcessMiddlePart");// 获取物件类型为中间件的零件类型
        //		folder = FolderUtil.getFolder("/Default/70：PBOM/中间件", containerRef);
        //	}
        //	else {
        //	typeRef = TypedUtilityServiceHelper.service.getTypeDefinitionReference("com.nriet.ProcessAssistantPart");// 获取物件类型为工艺辅件的零件类型
        //	folder = FolderUtil.getFolder("/Default/70：PBOM/辅件", containerRef);
        //}
        FolderHelper.assignFolder(part, folder);// 指派零件到指定容器和指定路径下
        part.setTypeDefinitionReference(typeRef);// 设置零件的类型
        part = (WTPart) PersistenceHelper.manager.save(part);
        part = (WTPart) PersistenceHelper.manager.refresh(part);

        return part;

    }

    public static QueryResult getPartByLikeNumberNameView(String view, String number, String name)
            throws WTException, RemoteException {
        QuerySpec qs = new QuerySpec(WTPart.class);
        if (!"".equals(number)) {
            qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.LIKE, "%" + number + "%"),
                    new int[]{0});
        }
        if (!"".equals(name)) {
            if (!"".equals(number)) {
                qs.appendAnd();
            }
            qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NAME, SearchCondition.LIKE, "%" + name + "%"),
                    new int[]{0});
        }
        if (view != null && !"".equals(view)) {
            qs.appendAnd();

            View v = WTPartUtil.getViewByName(view);
            long viewOid = PersistenceHelper.getObjectIdentifier(v).getId();
            qs.appendWhere(new SearchCondition(WTPart.class, "view.key.id", SearchCondition.EQUAL, viewOid), new int[]{0});
        }

        qs.setAdvancedQueryEnabled(true);

        QueryResult qr = PersistenceHelper.manager.find(qs);
        LatestConfigSpec lc = new LatestConfigSpec();
        qr = lc.process(qr);

        return qr;
    }

    public static WTPart getPartByOid(long oid) throws WTException {
        QuerySpec qs = new QuerySpec(WTPart.class);
        int[] index = {0};
        SearchCondition sc = new SearchCondition(WTPart.class,
                "thePersistInfo.theObjectIdentifier.id", SearchCondition.EQUAL,
                oid);
        qs.appendWhere(sc, index);
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        if (qr.hasMoreElements()) {
            WTPart part = (WTPart) qr.nextElement();
            return part;
        }
        return null;
    }

    public static String getPartIBAValueByPartOid(long oid, String ibaKey) throws WTException {
        WTPart part = getPartByOid(oid);
        if (part == null) {
            return "";
        }
        part = getLatestPartByNumberAndView(part, "Manufacturing");
        IBAHelper ibaHelper = new IBAHelper(part);
        String value = ibaHelper.getIBAValue(ibaKey);
        return value;
    }

    public static String getPartTypeByPartOid(long oid) throws WTException {
        WTPart part = getPartByOid(oid);
        if (part == null) {
            return "";
        }
        part = getLatestPartByNumberAndView(part, "Manufacturing");
        IBAHelper ibaHelper = new IBAHelper(part);
        String type = ibaHelper.getIBAValue("MTYPE");
        return type;
    }

    public static void getAllUp_Part(WTPart part, List<String> list, List<WTPart> tempList) {
        if (tempList.contains(part)) {
            return;
        }
        tempList.add(part);

        try {
            QueryResult qr = WTPartHelper.service.getUsedByWTParts((WTPartMaster) part.getMaster());
            while (qr.hasMoreElements()) {
                Object obj = qr.nextElement();
                if (obj instanceof WTPartUsageLink) {
                    WTPartUsageLink link = (WTPartUsageLink) obj;
                    Object obj1 = link.getOtherObject(part.getMaster());
                    if (obj1 instanceof WTPart) {
                        WTPart temp = (WTPart) obj1;
                        if (!list.contains(temp.getNumber())) {
                            list.add(temp.getNumber());
                        }
                        getAllUp_Part(temp, list, tempList);
                    }
                } else if (obj instanceof WTPart) {
                    WTPart temp = (WTPart) obj;
                    if (!list.contains(temp.getNumber())) {
                        list.add(temp.getNumber());
                    }
                    getAllUp_Part(temp, list, tempList);
                }
            }
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (tempList.size() > 0) {
            tempList.remove(tempList.size() - 1);
        }
    }

    /**
     * @param oid
     * @return HashMap<WTPart       ,   List   <   String>>   list 0:数量   1：物资编码
     */
    public static HashMap<WTPart, List<String>> getDownPartOnlyStepFromPdmSystem(String oid) {
        HashMap<WTPart, List<String>> map = new HashMap<WTPart, List<String>>();
        try {
            WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
            QueryResult qr = wt.part.WTPartHelper.service.getUsesWTPartMasters(part);
            while (qr.hasMoreElements()) {
                Object obj = qr.nextElement();
                if (obj instanceof WTPartUsageLink) {
                    WTPartUsageLink link = (WTPartUsageLink) obj;
                    double d = link.getQuantity().getAmount();
                    Persistable obj2 = link.getOtherObject(part);
                    if (obj2 instanceof WTPartMaster) {
                        WTPartMaster master = (WTPartMaster) obj2;
                        QueryResult qr2 = VersionControlHelper.service.allVersionsOf(master);
                        while (qr2.hasMoreElements()) {
                            WTPart temp = (WTPart) qr2.nextElement();
                            if (Constant.PBOM_VIEW.equals(temp.getViewName())) {
                                List<String> tlist = new ArrayList<String>();
                                IBAHelper lhelp = new IBAHelper(link);
                                String wzbm = lhelp.getIBAValue(link, "WZBM");
                                int cnt = 1;
                                if (d == 0) {
                                    cnt = 1;
                                } else {
                                    cnt = (int) d;
                                }
                                tlist.add(String.valueOf(cnt));
                                tlist.add(wzbm);
                                map.put(temp, tlist);
                                break;
                            }

                        }

                    }
                }
            }
        } catch (ObjectNotForLinkException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (PersistenceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 通过编号，名称或IBA属性查找指定存储库下的零部件
     *
     * @param number        编号
     * @param name          名称
     * @param containerName 存储库名称
     * @param ibaMap        IBA属性集合：key为IBA内部名称，value为属性值
     * @return QueryResult
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws RemoteException
     */
    public static QueryResult queryPart(String number, String name, String containerName, Map<String, String> ibaMap)
            throws WTException, WTPropertyVetoException, RemoteException {
        QuerySpec querySpec = new QuerySpec(WTPart.class);
        querySpec.setAdvancedQueryEnabled(true);
        boolean flag = false;
        SearchCondition sc = null;
        if (containerName != null && !"".equals(containerName)) {
            WTLibrary wtlib = WCUtil.getLibraryByName(containerName);
            if (wtlib != null) {
                flag = true;
                long id = PersistenceHelper.getObjectIdentifier(wtlib).getId();
                sc = new SearchCondition(WTPart.class, "containerReference.key.id", SearchCondition.EQUAL, id);
                querySpec.appendWhere(sc, index);
            }
        }

        if (number != null || !"".equals(number)) {
            if (flag) {
                querySpec.appendAnd();
            } else {
                flag = true;
            }
            sc = new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.LIKE, number);
            querySpec.appendWhere(sc, index);
        }

        if (name != null && !"".equals(name)) {
            if (flag) {
                querySpec.appendAnd();
            } else {
                flag = true;
            }
            sc = new SearchCondition(WTPart.class, WTPart.NAME, SearchCondition.LIKE, name);
            querySpec.appendWhere(sc, index);
        }

        // 通过对象的软属性查询
        if (ibaMap != null && !ibaMap.isEmpty()) {
            ClassAttribute caId = new ClassAttribute(WTPart.class, Persistable.PERSIST_INFO + "."
                    + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
            int n = 1;
            if (flag) {
                querySpec.appendAnd();
                querySpec.appendOpenParen();

                for (String key : ibaMap.keySet()) {
                    if (n != 1) {
                        querySpec.appendAnd();
                    }
                    n++;

                    String value = ibaMap.get(key);
                    SubSelectExpression subSelectExpression = getStringIBAQuery(key, value);
                    querySpec.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
                }

                querySpec.appendCloseParen();
                QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
                queryResult = new LatestConfigSpec().process(queryResult);
            } else {
                for (String key : ibaMap.keySet()) {
                    if (n != 1) {
                        querySpec.appendAnd();
                    }
                    n++;

                    String value = ibaMap.get(key);
                    SubSelectExpression subSelectExpression = getStringIBAQuery(key, value);
                    querySpec.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
                }
            }
        }

        QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
        return new LatestConfigSpec().process(queryResult);

    }

    /**
     * 构建子查询语句
     *
     * @param ibaName
     * @param ibaValue
     * @return
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws RemoteException
     * @author lbzhang
     * @date 2012-8-28
     */
    public static SubSelectExpression getStringIBAQuery(String ibaName, String ibaValue) throws WTException,
            WTPropertyVetoException, RemoteException {
        // 获取IBA属性定义
        AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
        if (addv == null)
            throw new IBADefinitionException("No IBA Definition: " + ibaName);
        long ibaDefId = addv.getObjectID().getId();
        QuerySpec qs = new QuerySpec();
        int idx = qs.appendClassList(StringValue.class, false);
        qs.appendSelect(new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), new int[]{idx},
                false);
        qs.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", SearchCondition.EQUAL,
                ibaDefId), new int[]{idx});
        qs.appendAnd();
        qs.appendWhere(
                new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.LIKE, ibaValue, true),
                new int[]{idx});
        return new SubSelectExpression(qs);
    }

    public static WTPart getLatestPartByVersionNumberAndView(WTPart part, String string) {
        boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
        String version = part.getVersionInfo().getIdentifier().getValue();
        try {
            Class klass = WTPart.class;
            QuerySpec qs = new QuerySpec(klass);
            qs.appendWhere(new SearchCondition(klass, "master>number", "=", part.getNumber()), new int[1]);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(klass, "versionInfo.identifier.versionId", "=", version), new int[1]);

            // if ((viewname != null) && (!viewname.equals(""))) {
            qs.appendAnd();
            View view = ViewHelper.service.getView("Manufacturing");
            qs.appendWhere(
                    new SearchCondition(WTPart.class, "view.key.id",
                            "=", view.getPersistInfo().getObjectIdentifier().getId()), new int[1]);
            // }

            qs = new LatestConfigSpec().appendSearchCriteria(qs);
            qs.setAdvancedQueryEnabled(true);

            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                WTPart localIterated = (WTPart) VersionControlHelper.getLatestIteration((WTPart) qr.nextElement(), true);
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

    public static WTPart getMPartByNumberAndVersion(String partNumber, String viewName, String version)
            throws WTException {
        View view = getViewByName(viewName);
        if (view == null) {
            return null;
        }
        String viewOid = view.getPersistInfo().getObjectIdentifier().toString();
        String idA3View = viewOid.substring(viewOid.indexOf(":") + 1, viewOid.length());
        GLLogger.debug("viewName:" + viewName);
        GLLogger.debug("idA3View:" + idA3View);

        QuerySpec qs = new QuerySpec(WTPart.class);
        qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, partNumber),
                new int[]{0});

        qs.appendAnd();
        qs.appendWhere(
                new SearchCondition(WTPart.class, "view.key.id", SearchCondition.EQUAL, Long.parseLong(idA3View)),
                new int[]{0});

        qs.setAdvancedQueryEnabled(true);
        QueryResult qr = PersistenceHelper.manager.find(qs);
        while (qr.hasMoreElements()) {
            WTPart temp = (WTPart) qr.nextElement();
            GLLogger.debug(temp.getName() + "  " + temp.getViewName() + "  " + temp.getVersionIdentifier().getValue()
                    + "." + temp.getIterationIdentifier().getValue());
            System.out.println("out=" + temp.getIterationDisplayIdentifier().toString());
            System.out.println("version=" + version);
            if (temp.getIterationDisplayIdentifier().toString().startsWith(version)) {
                return temp;
            }
        }
        return null;
    }

    public static String downloadPvzFile(Persistable per, String path) throws IOException {
        FileOutputStream fos = null;
        InputStream is = null;
        String tempFileName = "";
        try {
            QueryResult qrRep = PublishUtils.getRepresentations(per);
            byte[] buf = new byte[2048];
            while (qrRep.hasMoreElements()) {
                Object object = qrRep.nextElement();
                Representation representation = (Representation) object;
                representation = (Representation) ContentHelper.service.getContents(representation);
                Vector vector1 = ContentHelper.getContentList(representation);
                for (int l = 0; l < vector1.size(); l++) {
                    ContentItem contentitem = (ContentItem) vector1.elementAt(l);
                    if (!(contentitem instanceof ApplicationData)) {
                        continue;
                    }
                    ApplicationData data = (ApplicationData) contentitem;
                    if (data.getRole() != ContentRoleType.PRODUCT_VIEW_ED) {
                        continue;
                    }
                    ApplicationData data3 = RepUpdateUtils.processDeferredUpdateRepresentation(data, representation);
                    if (data3 != null) {
                        representation = (Representation) ContentHelper.service.getContents(representation);
                        vector1 = ContentHelper.getContentList(representation);
                    }
                    break;
                }

                for (int j1 = 0; j1 < vector1.size(); j1++) {
                    ContentItem contentitem1 = (ContentItem) vector1.elementAt(j1);
                    if (!(contentitem1 instanceof ApplicationData)) {
                        continue;
                    }
                    ApplicationData applicationdata1 = (ApplicationData) contentitem1;
                    String fileName = applicationdata1.getFileName();
                    is = ContentServerHelper.service.findContentStream(applicationdata1);
                    File file = new File(path + File.separator + fileName);
                    fos = new FileOutputStream(file);
                    int j = 0;
                    while ((j = is.read(buf, 0, buf.length)) >= 0) {
                        fos.write(buf, 0, j);
                    }
                    if (fileName.endsWith(".pvs") || fileName.endsWith(".pvz")) {
                        tempFileName = fileName;
                    }

                }
            }
        } catch (WTException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                fos.close();
            }
            if (is != null) {
                is.close();
            }
        }
        return tempFileName;
    }

    public static String downloadPrintPdf(Object object, String path) throws WTException, PropertyVetoException {
        FileOutputStream fos = null;
        InputStream is = null;
        ContentHolder holder = null;
        String printFileName = "";
        if(object instanceof WTDocument){
            WTDocument document = (WTDocument) object;
            holder = ContentHelper.service.getContents(document);
        }else if(object instanceof EPMDocument){
            EPMDocument epmDocument = (EPMDocument) object;
            holder = ContentHelper.service.getContents(epmDocument);
        }
        try {
            if(holder != null){
                byte[] buf = new byte[2048];
                Vector apps = ContentHelper.getApplicationData(holder);
                for (Enumeration e = apps.elements(); e.hasMoreElements(); ) {
                    ApplicationData contentItem = (ApplicationData) e.nextElement();
                    String applicationdataRole = contentItem.getRole().toString();
                    String fileName = contentItem.getFileName();
                    if (!"SECONDARY".equalsIgnoreCase(contentItem.getRole().toString()))
                        continue;// 不是附件
                    if ((fileName.startsWith("Print_") || fileName.toUpperCase().startsWith("SIGNED")) && fileName.endsWith(".pdf")) {
                        is = ContentServerHelper.service.findContentStream(contentItem);
                        File file = new File(path + File.separator + fileName);
                        printFileName = fileName;
                        fos = new FileOutputStream(file);
                        int j = 0;
                        while ((j = is.read(buf, 0, buf.length)) >= 0) {
                            fos.write(buf, 0, j);
                        }

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return printFileName;
    }
}
