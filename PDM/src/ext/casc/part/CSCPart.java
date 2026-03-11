package ext.casc.part;

import com.glaway.mpm.util.GLLogger;
import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import ext.casc.doc.CSCDoc;
import ext.casc.util.CSCUtil;
import ext.casc.util.IBAUtility;
import wt.configuration.TraceCode;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.enterprise.Master;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.*;
import wt.part.*;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.series.MultilevelSeries;
import wt.series.Series;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.IterationIdentifier;
import wt.vc.Mastered;
import wt.vc.VersionControlHelper;
import wt.vc.VersionIdentifier;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

public class CSCPart {
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
    public static String VIEW = "VIEW";
    public static String STATE = "STATE";

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
            partState.setState(State.toState("APPROVED"));
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
            IBAUtility ibaUtility = new IBAUtility(part);
            Set<Entry<String, String>> set = ibaattributes.entrySet();
            for(Entry<String, String> entry :set){
            	ibaUtility.setIBAValue(entry.getKey(),entry.getValue());
            }
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


    public static WTPart createPart(String number, String name, HashMap attributes, HashMap ibaattributes,
                                    WTContainer containerRef) {
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

            String state = (String) attributes.get(STATE);
            if(state!=null &&!"".equals(state)){
                partState.setState(State.toState(state));
            }else{
                partState.setState(State.toState("INWORK"));

            }
            part.setState(partState);
            part.setName(name);
            part.setNumber(number);
            part.setEndItem(enditem_flag);
            part.setPartType(partType);
            part.setDefaultTraceCode(traceCode);
            part.setDefaultUnit(unit);

            if (partView != null)
                ViewHelper.assignToView(part, partView);
            if (folder != null)
                FolderHelper.assignLocation((FolderEntry) part, folder);

            part.setContainer(containerRef);
            System.out.println("-------容器类：" + containerRef.toString());

            // 保存为持久对象
            part = (WTPart) PersistenceHelper.manager.save(part);
            IBAUtility ibaUtility = new IBAUtility(part);
            Set<Entry<String, String>> set = ibaattributes.entrySet();
            for(Entry<String, String> entry :set){
                ibaUtility.setIBAValue(entry.getKey(),entry.getValue());
            }
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
            WTPartMaster partMaster = CSCPart.getPartMasterByNumber(partNumber);
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
     * 通过编号和视图查询零部件
     *
     * @param number
     * @param viewName
     * @return
     */
    public static WTPart getPartByNumberAndViewName(String number,String viewName) {
    	try {
			View view = getViewByName(viewName);
			if (view == null) {
				return null;
			}
			long viewOid = view.getPersistInfo().getObjectIdentifier().getId();
			return getLatestPartByNumberAndView(number, viewOid);
		} catch (WTException e) {
			e.printStackTrace();
		}
    	return null;
    }

	/**
	 * 根据视图的名称，获取零件的视图
	 *
	 */
	@SuppressWarnings("deprecation")
	public static View getViewByName(String viewName) throws WTException {
		View view = null;
		QuerySpec qs = new QuerySpec(View.class);
		int[] index = { 0 };
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
	 */
	@SuppressWarnings("deprecation")
	public static WTPart getLatestPartByNumberAndView(String partNumber, long viewOid) throws WTException {

		QuerySpec qs = new QuerySpec(WTPart.class);
		qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, partNumber),
				new int[] { 0 });

		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTPart.class, "view.key.id", SearchCondition.EQUAL, viewOid),
				new int[] { 0 });

		qs.setAdvancedQueryEnabled(true);

		QueryResult qr = PersistenceHelper.manager.find((StatementSpec)qs);
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
    public static RevisionControlled getLatestObject(Mastered master) throws WTException {
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

    public static ArrayList getEndItemPartByContext(String context) {
        WTContainer orgContainer = null;
        ArrayList result = new ArrayList<WTPart>();
        long orgA2A2 = 0;

        try {
            if (context != null) {
                orgContainer = (WTContainer) CSCUtil.getObjectByOid(context);
                orgA2A2 = PersistenceHelper.getObjectIdentifier(orgContainer).getId();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        String user = "";
        ArrayList list = new ArrayList();
        WTPart part = null;
        try {
            user = wt.session.SessionHelper.manager.getPrincipal().getName();
            wt.session.SessionHelper.manager.setAdministrator();
        } catch (Exception e) {
        }

        try {
            QuerySpec qs = new QuerySpec(WTPartMaster.class);
            SearchCondition sc = new
                    SearchCondition(WTPartMaster.class, "containerReference.key.id", SearchCondition.EQUAL, orgA2A2);
            qs.appendSearchCondition(sc);
            qs.appendAnd();

            SearchCondition sc2 = new
                    SearchCondition(WTPartMaster.class, WTPartMaster.END_ITEM, SearchCondition.IS_TRUE);
            qs.appendSearchCondition(sc2);

            QueryResult qr = PersistenceHelper.manager.find(qs);
            while (qr.hasMoreElements()) {
                part = (WTPart) getLatestObject((WTPartMaster) qr.nextElement());
                // System.out.println(part.getNumber());
                if (part != null)
                    result.add(part);
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

        return result;
    }

    public static boolean createPartAssociateDoc(String number, WTDocument wtdocument) throws WTException {
        WTPart wtpart = CSCPart.getPart(number);
        wtdocument = CSCDoc.getDoc(wtdocument.getNumber());
        WTPartDescribeLink wtpartdescribelinkOld = getPartDescribeLink(wtpart,
                wtdocument);
        if (wtpartdescribelinkOld == null) {
            // System.out
            // .println("no link between the wtpart and wtdocument,will create");
            WTPartDescribeLink wtpartdescribelink1 = WTPartDescribeLink
                    .newWTPartDescribeLink(wtpart, wtdocument);
            PersistenceServerHelper.manager.insert(wtpartdescribelink1);

            wtpartdescribelink1 = (WTPartDescribeLink) PersistenceHelper.manager
                    .refresh(wtpartdescribelink1);
            WTPartDescribeLink wtpartdescribelink = getPartDescribeLink(wtpart,
                    wtdocument);
            if (wtpartdescribelink == null) {
                // System.out.println("creat wtpart associate doc fail");
                return false;
            } else return true;
        }
        // System.out.println("the link has alseardy exist,do not need to create it again");
        return false;

    }

    public static boolean createPartAssociateDoc(WTPart wtpart, WTDocument wtdocument) throws WTException {
        WTPartDescribeLink wtpartdescribelinkOld = getPartDescribeLink(wtpart,
                wtdocument);
        if (wtpartdescribelinkOld == null) {
            // System.out
            // .println("no link between the wtpart and wtdocument,will create");
            WTPartDescribeLink wtpartdescribelink1 = WTPartDescribeLink
                    .newWTPartDescribeLink(wtpart, wtdocument);
            PersistenceServerHelper.manager.insert(wtpartdescribelink1);

            wtpartdescribelink1 = (WTPartDescribeLink) PersistenceHelper.manager
                    .refresh(wtpartdescribelink1);
            WTPartDescribeLink wtpartdescribelink = getPartDescribeLink(wtpart,
                    wtdocument);
            if (wtpartdescribelink == null) {
                // System.out.println("creat wtpart associate doc fail");
                return false;
            } else return true;
        }
        // System.out.println("the link has alseardy exist,do not need to create it again");
        return false;
    }

    public static boolean removePartAssociateDoc(String number, WTDocument wtdocument) throws WTException {
        WTPart wtpart = CSCPart.getPart(number);
        wtdocument = CSCDoc.getDoc(wtdocument.getNumber());
        WTPartDescribeLink wtpartdescribelinkOld = getPartDescribeLink(wtpart,
                wtdocument);
        if (wtpartdescribelinkOld != null) {
            PersistenceServerHelper.manager.remove(wtpartdescribelinkOld);
            return true;
        }
        // System.out.println("the link isn't exist");
        return false;
    }

    public static boolean removePartAssociateDoc(WTPart wtpart, WTDocument wtdocument) throws WTException {
        WTPartDescribeLink wtpartdescribelinkOld = getPartDescribeLink(wtpart,
                wtdocument);
        if (wtpartdescribelinkOld != null) {
            PersistenceServerHelper.manager.remove(wtpartdescribelinkOld);
            return true;
        }
        // System.out.println("the link isn't exist");
        return false;
    }

    public static WTPartDescribeLink getPartDescribeLink(WTPart wtpart, WTDocument wtdocument) throws WTException {
        QueryResult queryresult = PersistenceHelper.manager.find(wt.part.WTPartDescribeLink.class, wtpart,
                WTPartDescribeLink.DESCRIBES_ROLE, wtdocument);
        if (queryresult == null || queryresult.size() == 0)
            return null;
        else return (WTPartDescribeLink) queryresult.nextElement();
    }

    public static boolean createPartReferenceDoc(String number, WTDocument wtdocument) throws WTException {
        WTPart wtpart = CSCPart.getPart(number);
        wtdocument = CSCDoc.getDoc(wtdocument.getNumber());
        WTPartReferenceLink wtpartdescribelinkOld = getPartReferenceLink(wtpart,
                wtdocument);
        if (wtpartdescribelinkOld == null) {
            // System.out
            // .println("no link between the wtpart and wtdocument,will create");
            WTPartReferenceLink wtpartreferencelink1 = WTPartReferenceLink
                    .newWTPartReferenceLink(wtpart, (WTDocumentMaster) wtdocument.getMaster());
            PersistenceServerHelper.manager.insert(wtpartreferencelink1);

            wtpartreferencelink1 = (WTPartReferenceLink) PersistenceHelper.manager
                    .refresh(wtpartreferencelink1);
            WTPartReferenceLink wtpartreferencelink = getPartReferenceLink(wtpart,
                    wtdocument);
            if (wtpartreferencelink == null) {
                // System.out.println("creat wtpart associate doc fail");
                return false;
            } else return true;
        }
        // System.out.println("the link has alseardy exist,do not need to create it again");
        return false;

    }

    public static boolean removePartReferenceDoc(String number, WTDocument wtdocument) throws WTException {
        WTPart wtpart = CSCPart.getPart(number);
        wtdocument = CSCDoc.getDoc(wtdocument.getNumber());
        WTPartReferenceLink wtpartreferencelinkOld = getPartReferenceLink(wtpart,
                wtdocument);
        if (wtpartreferencelinkOld != null) {
            PersistenceServerHelper.manager.remove(wtpartreferencelinkOld);
            return true;
        }
        // System.out.println("the link isn't exist");
        return false;
    }

    public static WTPartReferenceLink getPartReferenceLink(WTPart wtpart, WTDocument wtdocument) throws WTException {
        QueryResult queryresult = PersistenceHelper.manager.find(wt.part.WTPartReferenceLink.class, wtpart,
                WTPartReferenceLink.REFERENCES_ROLE, (WTDocumentMaster) wtdocument.getMaster());
        if (queryresult == null || queryresult.size() == 0)
            return null;
        else return (WTPartReferenceLink) queryresult.nextElement();
    }

    /**
     * Get Related WTObjects by a document number. Includes Related Parts and Related Documents
     *
     * @param docNumber
     * @return ArrayList with WTObjects
     * @throws WTException
     */
    public static ArrayList getRelatedWTObjectByPart(WTPart part) throws WTException {
        ArrayList result = new ArrayList();
        QueryResult qr = WTPartHelper.service.getDescribedByDocuments(part);

        while (qr.hasMoreElements()) {
            Persistable descPersist = (Persistable) qr.nextElement();
            if (descPersist instanceof WTDocument) {
                WTDocument descDoc = (WTDocument) descPersist;
                result.add(descDoc);
            } else if (descPersist instanceof EPMDocument) {
                EPMDocument descEPMDoc = (EPMDocument) descPersist;
                result.add(descEPMDoc);
            }
        }

        qr = WTPartHelper.service.getReferencesWTDocumentMasters(part);
        while (qr.hasMoreElements()) {
            WTDocumentMaster refDocMaster = (WTDocumentMaster) qr.nextElement();
            WTDocument refDoc = CSCDoc.getDoc(refDocMaster.getNumber());
            if (!result.contains(refDoc))
                result.add(refDoc);
        }

        return result;
    }

    /**
     * ��ȡdecribed
     *
     * @param part
     * @return
     * @throws WTException
     */
    public static ArrayList getDescribedDocumentsByPart(WTPart part) throws WTException {
        ArrayList result = new ArrayList();
        QueryResult qr = WTPartHelper.service.getDescribedByDocuments(part);

        while (qr.hasMoreElements()) {
            Persistable descPersist = (Persistable) qr.nextElement();
            if (descPersist instanceof WTDocument) {
                WTDocument descDoc = (WTDocument) descPersist;
                result.add(descDoc);
            } else if (descPersist instanceof EPMDocument) {
                EPMDocument descEPMDoc = (EPMDocument) descPersist;
                result.add(descEPMDoc);
            }
        }
        return result;
    }
    /**
     * ��ȡdecribed
     *
     * @param part
     * @return
     * @throws WTException
     */
    public static ArrayList<WTDocument> getDescribedDocumentsWithoutEpmByPart(WTPart part) throws WTException {
        ArrayList<WTDocument> result = new ArrayList<WTDocument>();
        QueryResult qr = WTPartHelper.service.getDescribedByDocuments(part);

        while (qr.hasMoreElements()) {
            Persistable descPersist = (Persistable) qr.nextElement();
            if (descPersist instanceof WTDocument) {
                WTDocument descDoc = (WTDocument) descPersist;
                QueryResult qResult = VersionControlHelper.service.allVersionsOf(descDoc);
                if(qResult.hasMoreElements()){
                	WTDocument doc = (WTDocument)qResult.nextElement();
                	if(!result.contains(doc)){
                		result.add(doc);
                	}
                }

            }
        }
        return result;
    }

    /**
     * ��ȡreferenced
     *
     * @param part
     * @return
     * @throws WTException
     */
    public static ArrayList getReferenceWTObjectByPart(WTPart part) throws WTException {
        ArrayList result = new ArrayList();
        QueryResult qr = WTPartHelper.service.getReferencesWTDocumentMasters(part);
        while (qr.hasMoreElements()) {
            WTDocumentMaster refDocMaster = (WTDocumentMaster) qr.nextElement();
            WTDocument refDoc = CSCDoc.getDoc(refDocMaster.getNumber());
            if (!result.contains(refDoc))
                result.add(refDoc);
        }
        return result;
    }

    public static void main(String[] args) {
        ArrayList list = getEndItemPartByContext("wt.pdmlink.PDMLinkProduct:10086");
        for (int i = 0; i < list.size(); i++) {
            WTPart part = (WTPart) list.get(i);
            System.out.println("EndItem = " + part.getNumber() + " -- " + part.getName() + " -- "
                    + part.getVersionDisplayIdentifier().toString());
        }
    }
}
