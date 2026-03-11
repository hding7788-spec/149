package com.glaway.mpm.importdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import wt.configuration.TraceCode;
import wt.doc.WTDocument;
import wt.enterprise.Master;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleState;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.State;
import wt.part.PartType;
import wt.part.QuantityUnit;
import wt.part.Source;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.series.MultilevelSeries;
import wt.series.Series;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.IterationIdentifier;
import wt.vc.VersionControlHelper;
import wt.vc.VersionIdentifier;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressHelper;

import com.glaway.mpm.util.IBAHelper;
import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.windchill.enterprise.folder.FolderActionResource;

public class ImportPartUtil {
    public static String LIFECYCLE = "LIFECYCLE";
    public static String SOURCE = "SOURCE";
    public static String TYPE = "TYPE";
    public static String VERSION = "VERSION";
    public static String PART_TYPE = "PART_TYPE";
    // 导入属性

    public static String PRODUCTNO = "PRODUCTNO";
    public static String PARENTNO = "PARENTNO";
    public static String AMOUNT = "AMOUNT";
    public static String FOLDER = "FOLDER";
    public static String ENDITEM = "ENDITEMIN";
    public static String PARTTYPE = "PARTTYPE";
    public static String DEFAULTTRACECODE = "DEFAULTTRACECODE";
    public static String DEFAULTUNIT = "DEFAULTUNIT";
    public static String HIDEPARTINSTRUCTURE = "HIDEPARTINSTRUCTURE";
    public static String PHANTOM = "PHANTOM";
    public static String KEYCOMPONENT = "KEYCOMPONENT";
    public static String  PARTKIT  = "PARTKIT";
    public static String CTYPE = "CTYPE";
    public static String VIEW = "VIEW";
    public static String SCRET = "SCREET";
    public static String PHASECODE = "PHASECODE";
    public static String CSIZE = "CSIZE";
    public static String CINDEX = "CINDEX";

    public static String CMAT = "CMAT";
    public static String MATERIAL_NAME = "MATERIAL_NAME";
    public static String ENDITEM_1 = "ENDITEM_1";
    public static String COMMONNAME = "COMMONNAME";
    public static String PRODUCT_INDEX = "PRODUCT_INDEX";
    public static String COMPANY = "COMPANY";
    public static String REMARK = "REMARK";
    public static String ROUTING = "ROUTING";
    public static String MAT_UP = "MAT_UP";
    public static String MAT_DOWN = "MAT_DOWN";

    public static String MATERIAL = "MATERIAL";
    public static String PZGGBZH = "PZGGBZH";
    public static String JSTJBZH = "JSTJBZH";
    public static String JDDJ = "JDDJ";
    public static String ZLDJ = "ZLDJ";
    public static String CLZT = "CLZT";
    public static String CLDW = "CLDW";
    public static String ZQCLMC = "ZQCLMC";
    public static String JBCLMC = "JBCLMC";
    public static String ZQCLBZH = "ZQCLBZH";

    public static String PINDEX = "PINDEX";
    public static String MINDEX = "MINDEX";
    public static String DESIGNER = "DESIGNER";
    public static String size = "size";

    public static boolean VERBOSE = false;

    /**
     * Answer a part by for a given part number
     *
     * @param partNumber
     *            - the String object used as search criteria in the retrieval of WTPart
     * @return WTPart
     *
     */
    public static WTPartMaster getPartMasterByNumber(String partNumber) {
        WTPartMaster partMaster = null;
        try {
            QuerySpec qs = new QuerySpec(WTPartMaster.class);
            SearchCondition sc = new
                    SearchCondition(WTPartMaster.class, WTPartMaster.NUMBER, SearchCondition.EQUAL, partNumber, false);
            qs.appendSearchCondition(sc);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                partMaster = (WTPartMaster) qr.nextElement();
            }
        } catch (Exception ex) {
            partMaster = null;
            // System.out.println("CSCPart.class Method=getPartMasterByNumber Exception Message = " + ex.getMessage());
            // //Debug
        }
        return partMaster;
    }

    /**
     * This method is used to create a WTPart.
     *
     * @param number
     *            part number
     * @param name
     *            part name
     * @param attributes
     *            a HashMap that conains part attributes
     * @param containerRef
     *            container reference
     * @param isCheckout
     *            after creating it, if check out it.
     * @return WTPart
     */
    public static WTPart createPart(String number, String name, HashMap attributes, WTContainerRef containerRef,
            boolean isCheckout) {
        WTPart part = null;
        part = getPart(number);
        if (part != null)
            return part;
        String type = (String) attributes.get(TYPE);
        if (type == null || "".equals(type)) {
            type = "wt.part.WTPart";
        }

        String source = (String) attributes.get(SOURCE);
        if (source == null)
            source = "";
        source = source.trim();
        Source part_source;
        if (source.equals(""))
            source = "make";
        try {
            part_source = Source.toSource(source);
        } catch (Exception e) {
            // TODO: handle exception
            part_source = Source.MAKE;
        }

        String part_type = (String) attributes.get(PART_TYPE);
        if (part_type == null)
            part_type = "";
        part_type = part_type.trim();
        PartType partType;
        if (part_type.equals(""))
            part_type = "separable";
        try {
            partType = PartType.toPartType(part_type);
        } catch (Exception e) {
            // TODO: handle exception
            partType = PartType.SEPARABLE;
        }

        String view = (String) attributes.get(VIEW);
        if (view == null)
            view = "";
        view = view.trim();
        View partView = null;
        if (view.equals(""))
            view = "Design";
        try {
            partView = ViewHelper.service.getView(view);
        } catch (Exception e) {
            // TODO: handle exception
        }

        if (partView == null) {
            try {
                partView = ViewHelper.service.getView("Design");
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        String lifecycle = (String) attributes.get(LIFECYCLE);
        LifeCycleTemplate lifecycleTemplate = null;
        try {
            lifecycleTemplate = LifeCycleHelper.service.getLifeCycleTemplate(lifecycle,
                    ((WTContainer) (containerRef.getObject())).getContainerReference());
        } catch (Exception e) {
            // TODO: handle exception
            // System.out.print("get lifecycle template erorr: message= ");
            e.printStackTrace();
        }

        Folder folder = null;
        String strFolder = (String) attributes.get(FOLDER);
        if (strFolder == null)
            strFolder = "";
        strFolder.trim();
        if ("".equals(strFolder))
            strFolder = "/Default";
        try {
            folder = FolderHelper.service.getFolder(strFolder, containerRef);
        } catch (Exception e) {
            folder = null;
        }

        if (folder == null) {
            try {
                folder = FolderHelper.service.saveFolderPath(strFolder, containerRef);
            } catch (Exception e) {
                // TODO: handle exception
                folder = null;
            }
        }

        try {
            part = WTPart.newWTPart();
            part.setName(name);
            part.setNumber(number);

            if (!type.startsWith("WCTYPE|"))
                type = "WCTYPE|" + type;
            TypeIdentifier id = TypeHelper.getTypeIdentifier(type);
            // part = (WTPart) CoreMetaUtility.setType(part, id);
            part.setDefaultUnit(QuantityUnit.EA);

            part.setContainerReference(containerRef);
            part.setPartType(partType);
            part.setSource(part_source);
            if (partView != null)
                ViewHelper.assignToView(part, partView);
            String version = (String) attributes.get(VERSION);
            if (version != null && !"".equals(version)) {
                try {
                    MultilevelSeries multilevelseries = MultilevelSeries.newMultilevelSeries("wt.vc.VersionIdentifier",
                            version);
                    VersionIdentifier versionidentifier = VersionIdentifier.newVersionIdentifier(multilevelseries);
                    VersionControlHelper.setVersionIdentifier(part, versionidentifier);
                    Series series = Series.newSeries("wt.vc.IterationIdentifier");
                    IterationIdentifier iterationidentifier = IterationIdentifier.newIterationIdentifier(series);
                    VersionControlHelper.setIterationIdentifier(part, iterationidentifier);
                    // part = (WTPart) PersistenceHelper.manager.save(part);
                } catch (Exception e) {
                    // System.out.println("CSCPart.class Method=createPart message=set version error, version=" +
                    // version );
                    e.printStackTrace();
                }
            }
            // System.out.println("DEBUG INFO ==== MOVE TO FOLDER " + folder.toString());
            if (folder != null)
                FolderHelper.assignLocation(part, folder);

            if (lifecycleTemplate != null)
                part = (WTPart) LifeCycleHelper.setLifeCycle((LifeCycleManaged) part, lifecycleTemplate);

            PersistenceHelper.manager.save(part);

        } catch (Exception e) {
            // TODO: handle exception
            // System.out.println("CSCPart.class CreatePart Error:" + e.getMessage());
            e.printStackTrace();
            part = null;
        }

        return part;
    }

    public static WTPart createPart(String number, String name, HashMap attributes, HashMap ibaattributes,
            WTContainer containerRef, boolean isCheckout) {
        WTPart part = null;
        part = getPart(number);
        if (part != null)
            return part;
        // 成品
        String enditem_temp = (String) attributes.get(ENDITEM);
        String enditem = enditem_temp.trim();
        boolean enditem_flag = false;
        if (enditem.equals("是"))
            enditem_flag = true;

        // 装配模式
        String part_type = (String) attributes.get(PARTTYPE);
        if (part_type == null)
            part_type = "";
        part_type = part_type.trim();
        PartType partType;
        try {
            partType = PartType.toPartType(part_type);
            System.out.println("---装配模式：" + partType.toString());
        } catch (Exception e) {
            // TODO: handle exception
            partType = PartType.SEPARABLE;
        }
        // 默认追踪代码
        String defaulttracecode = (String) attributes.get(DEFAULTTRACECODE);
        if (defaulttracecode == null)
            defaulttracecode = "";
        defaulttracecode = defaulttracecode.trim();
        TraceCode traceCode;
        try {
            traceCode = TraceCode.toTraceCode(defaulttracecode);

        } catch (Exception e) {
            traceCode = TraceCode.UNTRACED;
        }
        System.out.println("---装配模式：" + traceCode.toString());
        // 默认单位
        String defaultunit = (String) attributes.get(DEFAULTUNIT);
        if (defaultunit == null)
            defaultunit = "";
        defaultunit = defaultunit.trim();
        QuantityUnit unit;
        unit = QuantityUnit.toQuantityUnit(defaultunit);
        // unit = QuantityUnit.EA;
        // 收集部件
        String hidepartinstructure = (String) attributes.get(HIDEPARTINSTRUCTURE);
        hidepartinstructure = hidepartinstructure.trim();
        boolean hidepart_flag = false;
        if (hidepartinstructure.equals("是"))
            hidepart_flag = true;
        // 虚拟制造部件
        String phantom = (String) attributes.get(PHANTOM);
        phantom = phantom.trim();
        boolean phantom_flag = false;
        if (phantom.equals("是"))
            phantom_flag = true;
        // 视图

        String view = (String) attributes.get(VIEW);
        if (view == null)
            view = "";
        view = view.trim();
        View partView = null;
        if (view.equals(""))
            view = "Design";
        try {
            partView = ViewHelper.service.getView(view);
        } catch (Exception e) {
            // TODO: handle exception
        }

        if (partView == null) {
            try {
                partView = ViewHelper.service.getView("Design");
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        // 文件夹

        Folder folder = null;
        String strFolder = (String) attributes.get(FOLDER);
        if (strFolder == null)
            strFolder = "";
        strFolder.trim();
        if ("".equals(strFolder))
            strFolder = "/Default";
        try {
            folder = FolderHelper.service.getFolder(strFolder, WTContainerRef.newWTContainerRef(containerRef));
        } catch (Exception e) {
            folder = null;
        }

        if (folder == null) {
            try {
                folder = FolderHelper.service.saveFolderPath(strFolder, WTContainerRef.newWTContainerRef(containerRef));
            } catch (Exception e) {
                // TODO: handle exception
                folder = null;
            }
        }

        try {
            part = WTPart.newWTPart();
            // 设置生命周期为已发布
            LifeCycleState partState = LifeCycleState.newLifeCycleState();
            partState.setState(State.toState("INWORK"));
            part.setState(partState);

            part.setName(name);
            part.setNumber(number);
            part.setEndItem(enditem_flag);
            part.setPartType(partType);
            part.setDefaultTraceCode(traceCode);
            part.setDefaultUnit(unit);
            part.setHidePartInStructure(hidepart_flag);
            part.setPhantom(phantom_flag);

            if (partView != null)
                ViewHelper.assignToView(part, partView);
            if (folder != null)
                FolderHelper.assignLocation((FolderEntry) part, folder);

            part.setContainer(containerRef);
            System.out.println("-------容器类：" + containerRef.toString());

            // 保存为持久对象
            part = (WTPart) PersistenceHelper.manager.save(part);
            IBAHelper ibaUtility = new IBAHelper(part);

            ibaUtility.setIBAValue("KEYCOMPONENT", (String) ibaattributes.get(KEYCOMPONENT));
//            ibaUtility.setIBAValue("PARTKIT", (String) ibaattributes.get(PARTKIT));
            String ctjbs = (String) ibaattributes.get(PARTKIT);
            boolean bs = false;
            if("是".equals(ctjbs)){
            	bs = true;
            }
            ibaUtility.setIBABooleanValue(part, "partkit", bs);


            ibaUtility.setIBAValue("CTYPE", (String) ibaattributes.get(CTYPE));
            String mj =  (String) ibaattributes.get(SCRET);
            ibaUtility.setIBAValue("document_secret",mj);
            ibaUtility.setIBAValue("document_phase", (String) ibaattributes.get(PHASECODE));
//            ibaUtility.setIBAValue("CSIZE", (String) ibaattributes.get(CSIZE));
//            ibaUtility.setIBAValue("CINDEX", (String) ibaattributes.get(CINDEX));
//            ibaUtility.setIBAValue("CMAT", (String) ibaattributes.get(CMAT));
//            ibaUtility.setIBAValue("SETMARK", (String) ibaattributes.get(SETMARK));
//            ibaUtility.setIBAValue("PTC_MATERIAL_NAME", (String) ibaattributes.get(MATERIAL_NAME));
//            ibaUtility.setIBAValue("ENDITEMIN", (String) ibaattributes.get(ENDITEM_1));
//            ibaUtility.setIBAValue("PTC_COMMON_NAME", (String) ibaattributes.get(COMMONNAME));
//            ibaUtility.setIBAValue("PRODUCT_INDEX", (String) ibaattributes.get(PRODUCT_INDEX));
//            ibaUtility.setIBAValue("COMPANY", (String) ibaattributes.get(COMPANY));
//            ibaUtility.setIBAValue("REMARK", (String) ibaattributes.get(REMARK));
//            ibaUtility.setIBAValue("ROUTING", (String) ibaattributes.get(ROUTING));
//
//            ibaUtility.setIBAValue("CMAT_UP", (String) ibaattributes.get(MAT_UP));
//            ibaUtility.setIBAValue("CMAT_DOWN", (String) ibaattributes.get(MAT_DOWN));
//
//            ibaUtility.setIBAValue("MATERIAL", (String) ibaattributes.get(MATERIAL));
//            ibaUtility.setIBAValue("PZGGBZH", (String) ibaattributes.get(PZGGBZH));
//            ibaUtility.setIBAValue("JSTJBZH", (String) ibaattributes.get(JSTJBZH));
//            ibaUtility.setIBAValue("JDDJ", (String) ibaattributes.get(JDDJ));
//            ibaUtility.setIBAValue("ZLDJ", (String) ibaattributes.get(ZLDJ));
//            ibaUtility.setIBAValue("CLZT", (String) ibaattributes.get(CLZT));
//            ibaUtility.setIBAValue("CLDW", (String) ibaattributes.get(CLDW));
//            ibaUtility.setIBAValue("ZQCLMC", (String) ibaattributes.get(ZQCLMC));
//            ibaUtility.setIBAValue("JBCLMC", (String) ibaattributes.get(JBCLMC));
//            ibaUtility.setIBAValue("ZQCLBZH", (String) ibaattributes.get(ZQCLBZH));
//
//            ibaUtility.setIBAValue("PINDEX", (String) ibaattributes.get(PINDEX));
//            ibaUtility.setIBAValue("MINDEX", (String) ibaattributes.get(MINDEX));
//            ibaUtility.setIBAValue("DESIGNER", (String) ibaattributes.get(DESIGNER));
//            ibaUtility.setIBAValue("SIZE", (String) ibaattributes.get(size));

            part = (WTPart) ibaUtility.updateAttributeContainer(part);
            ibaUtility.updateIBAHolder(part);
            part = (WTPart) PersistenceHelper.manager.refresh(part);

        } catch (Exception e) {
            // TODO: handle exception
            // System.out.println("CSCPart.class CreatePart Error:" + e.getMessage());
            e.printStackTrace();
            part = null;
        }

        return part;
    }

    /**
     * Answer a part by for a given part number
     *
     * @param partNumber
     *            - the String object used as search criteria in the retrieval of WTPart
     * @return WTPart
     */
    public static WTPart getPartByNumber(String partNumber) {
        WTPart part = null;
        try {
            WTPartMaster partMaster = ImportPartUtil.getPartMasterByNumber(partNumber);
            QueryResult qr = VersionControlHelper.service.allIterationsOf((Master) partMaster);
            if (qr.hasMoreElements()) {
                part = (WTPart) qr.nextElement();
            }
        } catch (Exception ex) {
            part = null;
            System.out.println("TAPart.class Method=getPartByNumber Exception Message = " + ex.getMessage()); // Debug
        }
        return part;
    }

    /**
     * Answer a part by for a given part number
     *
     * @param partNumber
     *            - the String object used as search criteria in the retrieval of WTPart
     * @return WTPart
     */
    public static WTPart getPart(String partNumber) {
        WTPart part = null;
        String user = "";
        try {
            user = wt.session.SessionHelper.manager.getPrincipal().getName();
            wt.session.SessionHelper.manager.setAdministrator();
            part = getPartByNumber(partNumber);
            if (part != null) {
                return (WTPart) getLatestObject((Master) part.getMaster());
            }
        } catch (Exception ex) {
            // System.out.println("Exception Message = " + ex.getMessage()); //Debug
        } finally {
            try {
                if (user != null && !user.equals(""))
                    wt.session.SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return part;
    }

    /**
     * This method is used to get working version of part
     *
     * @param part
     *            current part
     * @return working version
     */
    public static WTPart getWorkingCopyOfPart(WTPart part) throws WTException, WTPropertyVetoException {
        WTPart workingPart = null;

        if (!WorkInProgressHelper.isCheckedOut(part)) {
            wt.folder.Folder folder = WorkInProgressHelper.service.getCheckoutFolder();
            CheckoutLink checkoutlink = WorkInProgressHelper.service.checkout(part, folder,
                    wt.session.SessionHelper.manager.getPrincipal().getName());
            workingPart = (WTPart) checkoutlink.getWorkingCopy();
        } else {
            if (!WorkInProgressHelper.isWorkingCopy(part))
                workingPart = (WTPart) WorkInProgressHelper.service.workingCopyOf(part);
            else workingPart = part;
        }

        return workingPart;
    }

    /**
     * This method is used to get the lastest version from a master.
     *
     * @param master
     * @return lastest iteration of a version-controlled object.
     */
    public static RevisionControlled getLatestObject(Master master) throws WTException {
        QueryResult queryResult = VersionControlHelper.service.allVersionsOf(master);
        if (queryResult != null && queryResult.size() > 0)
            return (RevisionControlled) queryResult.nextElement();
        return null;
    }

    public static ArrayList getPartsLikeNumber(String number) {
        // System.out.println("==get parts number=" + number);
        String user = "";
        ArrayList list = new ArrayList();
        try {
            user = wt.session.SessionHelper.manager.getPrincipal().getName();
            wt.session.SessionHelper.manager.setAdministrator();
        } catch (Exception e) {
        }
        try {
            QuerySpec qs = new QuerySpec(WTPart.class);
            SearchCondition sc = new
                    SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.LIKE, "%" + number + "%", false);
            qs.appendSearchCondition(sc);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            while (qr.hasMoreElements()) {
                WTPart part = (WTPart) qr.nextElement();
                part = (WTPart) VersionControlHelper.service.getLatestIteration(part, false);
                if (!list.contains(part))
                    list.add(part);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!"".equals(user)) {
            try {
                wt.session.SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
            }
        }

        return list;
    }

    public static ArrayList getPartsLikeName(String name) {
        String user = "";
        ArrayList list = new ArrayList();
        try {
            user = wt.session.SessionHelper.manager.getPrincipal().getName();
            wt.session.SessionHelper.manager.setAdministrator();
        } catch (Exception e) {
        }
        try {
            QuerySpec qs = new QuerySpec(WTPart.class);
            SearchCondition sc = new
                    SearchCondition(WTPart.class, WTPart.NAME, SearchCondition.LIKE, "%" + name + "%", false);
            qs.appendSearchCondition(sc);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            while (qr.hasMoreElements()) {
                WTPart part = (WTPart) qr.nextElement();
                part = (WTPart) VersionControlHelper.service.getLatestIteration(part, false);
                if (!list.contains(part))
                    list.add(part);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        if (!"".equals(user)) {
            try {
                wt.session.SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
            }
        }

        return list;
    }

    public static WTPart getPartByNumberEdition(String partNumber, String edition) {
        String user = "";
        ArrayList list = new ArrayList();
        WTPart part = null;
        try {
            user = wt.session.SessionHelper.manager.getPrincipal().getName();
            wt.session.SessionHelper.manager.setAdministrator();
        } catch (Exception e) {
        }

        StringTokenizer st = new StringTokenizer(edition, ".");
        String strVersion = "";
        String strIteration = "";
        // System.out.println("SEARCH PARTNUMBER:" + partNumber + " | VERSION: " + strVersion + " | Iteration " +
        // strIteration);
        try {
            QuerySpec qs = new QuerySpec(WTPart.class);
            SearchCondition sc = new
                    SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, partNumber, false);
            qs.appendSearchCondition(sc);
            qs.appendAnd();

            if (st.hasMoreTokens())
                strVersion = st.nextToken();
            SearchCondition sc2 = new
                    SearchCondition(WTPart.class, "versionInfo.identifier.versionId", SearchCondition.EQUAL,
                            strVersion, false);
            qs.appendSearchCondition(sc2);

            if (edition.indexOf('.') != -1) {
                if (st.hasMoreTokens())
                    strIteration = st.nextToken();
                qs.appendAnd();
                SearchCondition sc3 = new
                        SearchCondition(WTPart.class, "iterationInfo.identifier.iterationId", SearchCondition.EQUAL,
                                strIteration, false);
                qs.appendSearchCondition(sc3);
            }

            QueryResult qr = PersistenceHelper.manager.find(qs);
            while (qr.hasMoreElements()) {
                part = (WTPart) qr.nextElement();
            }
        } catch (Exception e) {
            // e.printStackTrace();
            // System.out.println("Cannot find part " + partNumber + "v" + edition);
        }
        if (!"".equals(user)) {
            try {
                wt.session.SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
            }
        }

        return part;
    }
}
