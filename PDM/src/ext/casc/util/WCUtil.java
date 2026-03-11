package ext.casc.util;

import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;

import cn.hutool.core.util.StrUtil;
import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.doc.WTDocument;
import wt.doc.WTDocumentHelper;
import wt.doc.WTDocumentMaster;
import wt.enterprise.Master;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMMemberLink;
import wt.epm.structure.EPMReferenceLink;
import wt.fc.ObjectIdentifier;
import wt.fc.PersistInfo;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTReference;
import wt.iba.definition.IBADefinitionException;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.inf.container.ContainerSpec;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.library.WTLibrary;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.inf.team.StandardContainerTeamService;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartReferenceLink;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.pom.PersistenceException;
import wt.project.Role;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.representation.RepresentationHelper;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTContext;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTStandardDateFormat;
import wt.vc.Iterated;
import wt.vc.Mastered;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;
import wt.vc.struct.StructHelper;

import com.glaway.mpm.util.FileUtil;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationMaster;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink;
import com.ptc.windchill.mpml.resource.MPMProcessMaterial;
import com.ptc.windchill.mpml.resource.MPMTooling;

import ext.casc.access.AccessAdminUtil;
import ext.casc.constants.Constants;

public class WCUtil implements RemoteAccess, Serializable {

    private static final long serialVersionUID = 1L;

    private static final String ID_CONTAINER = "containerReference.key.id";
    private static final String REF_ROLEA_ID = "roleAObjectRef.key.branchId";
    private static final String REF_ROLEB_ID = "roleBObjectRef.key.branchId";

    /**
     * 通过部件得到其描述文档，返回描述文档列表
     *
     * @param part
     *            条件部件
     * @return 依附于条件部件的描述文档列表
     */
    public static ArrayList<WTDocument> getDescDocsByPart(WTPart part) {
        ArrayList<WTDocument> results = new ArrayList<WTDocument>();

        try {
            if (!RemoteMethodServer.ServerFlag) {
                return (ArrayList) RemoteMethodServer.getDefault().invoke("getDescDocsByPart", WCUtil.class.getName(),
                        null,
                        new Class[] { WTPart.class },
                        new Object[] { part });
            } else {
                boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
                try {
                    QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(part);
                    while (qr.hasMoreElements()) {
                        Object tempObj = qr.nextElement();
                        if (tempObj instanceof WTDocument) {
                            results.add((WTDocument) tempObj);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    SessionServerHelper.manager.setAccessEnforced(enforce);
                }
                return results;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * 按指定encoding对value进行编码，用于传递参数到下一页面，接收参数后要使用相同的enc进行解码，参见decode
     *
     * @param value
     * @param enc
     * @return
     */
    public static String encode(String value, String enc) {
        if (enc == null || enc.length() == 0)
            enc = "UTF-8";
        String ret = value;
        try {
            ret = value == null ? "" : URLEncoder.encode(value, enc);
        } catch (Throwable tt) {
            tt.printStackTrace();
        }

        return ret;
    }

    /**
     * 查询当前用户所属的所有产品容器
     *
     * @param wtprincipal
     * @return
     * @throws WTException
     * @throws WTPropertyVetoException
     */
    public static QueryResult getProductListByUser(WTPrincipal wtprincipal) throws WTException, WTPropertyVetoException {
        ContainerSpec spec = new ContainerSpec(PDMLinkProduct.class);
        // WTPrincipal wtprincipal = SessionHelper.manager.getPrincipal();
        spec.setUser(WTPrincipalReference.newWTPrincipalReference(wtprincipal));
        QueryResult qResult = WTContainerHelper.service.getContainers(spec);
        // while(qResult.hasMoreElements()){
        // PDMLinkProduct product = (PDMLinkProduct)qResult.nextElement();
        // System.out.println("-------product:"+product.getName());
        // }
        return qResult;
    }

    /**
     * 查询当前用户所属的所有存储库容器
     *
     * @param wtprincipal
     * @return
     * @throws WTException
     * @throws WTPropertyVetoException
     */
    public static QueryResult getLibraryListByUser(WTPrincipal wtprincipal) throws WTException, WTPropertyVetoException {
        ContainerSpec spec = new ContainerSpec(WTLibrary.class);
        spec.setUser(WTPrincipalReference.newWTPrincipalReference(wtprincipal));
        QueryResult qResult = WTContainerHelper.service.getContainers(spec);
        return qResult;
    }

    /**
     * 按UTF-8格式对value进行编码，用于传递参数到下一页面，接收参数后要使用相同的enc进行解码，参见decode
     *
     * @param value
     * @return
     */
    public static String encode(String value) {
        return encode(value, "UTF-8");
    }

    public static PDMLinkProduct getProductByName(String name) throws WTException {
        QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
        SearchCondition sc = new SearchCondition(PDMLinkProduct.class, PDMLinkProduct.NAME, SearchCondition.EQUAL, name);
        qs.appendWhere(sc, new int[] { 0 });
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qs);
        PDMLinkProduct product = null;
        if (qResult.hasMoreElements()) {
            product = (PDMLinkProduct) qResult.nextElement();
        }
        return product;
    }

    public static QueryResult getProductList() throws WTException {
        QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
        return PersistenceHelper.manager.find((StatementSpec) qs);
    }

    public static Set<String> getRoleListBySelectProduct(NmCommandBean commandBean) throws WTException{
        List seleted = commandBean.getSelectedContextsForPopup();
      Set<String> set = new HashSet<String>();
      if (seleted != null && !seleted.isEmpty()) {
          for (Object object : seleted) {
              if (object instanceof NmContext) {
                  NmContext nmContext = (NmContext)object;
                  NmOid nmOid = nmContext.getTargetOid();
                  PDMLinkProduct product = (PDMLinkProduct)nmOid.getRefObject();
                  ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(product);
                  Vector<Role> vector = containerTeam.getRoles();
                  Iterator<Role> iterator = vector.iterator();
                  while (iterator.hasNext()) {
                      Role role = iterator.next();
                      // 过滤角色
                      String name = role.getDisplay(Locale.CHINA);
                      if (name.equals(Constants.ROLE_DISABLE_CAI) || name.equals(Constants.ROLE_DISABLE_CAII)
                              || name.equals(Constants.ROLE_DISABLE_CAIII)
                              || name.equals(Constants.ROLE_DISABLE_CHANGEREQUESTREVIEWBOARD)
                              || name.equals(Constants.ROLE_DISABLE_COLLABORATIONMANAGER)
                              || name.equals(Constants.ROLE_DISABLE_GUEST)
                              || name.equals(Constants.ROLE_DISABLE_OPTIONADMINISTRATOR)
                              || name.equals(Constants.ROLE_DISABLE_PACKAGECREATOR)
                              || name.equals(Constants.ROLE_DISABLE_PROMOTIONAPPROVERS)
                              || name.equals(Constants.ROLE_DISABLE_PROMOTIONREVIEWERS)
                              || name.equals(Constants.ROLE_DISABLE_VARIANCEAPPROVERS)) {
                          continue;
                      }
                      set.add(name);
                  }
              }

          }
      }
      return set;
    }

    public static WTLibrary getLibraryByName(String name) throws WTException {
        QuerySpec qs = new QuerySpec(WTLibrary.class);
        SearchCondition sc = new SearchCondition(WTLibrary.class, WTLibrary.NAME, SearchCondition.EQUAL, name);
        qs.appendWhere(sc, new int[] { 0 });
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qs);
        WTLibrary library = null;
        if (qResult.hasMoreElements()) {
            library = (WTLibrary) qResult.nextElement();
        }
        return library;
    }

    /**
     * 通过递归，得到3维数模的所有子件
     *
     * @param epm3d
     * @param set
     * @return
     * @throws WTException
     * @throws RemoteException
     * @throws InvocationTargetException
     */
    public static Set getMemberEPMDoc(EPMDocument epm3d, HashSet set) throws WTException, RemoteException,
            InvocationTargetException {
        System.out.println("epmDoc:" + epm3d);
        QueryResult qr = PersistenceHelper.manager.navigate(epm3d, EPMMemberLink.USES_ROLE, EPMMemberLink.class, false);
        while (qr.hasMoreElements()) {
            EPMMemberLink link = (EPMMemberLink) qr.nextElement();
            // System.out.println("~~link.isSuppressed:"+link.isSuppressed());
            // 过滤掉隐含成员
            if (link.isSuppressed() == true) {
                continue;
            }
            EPMDocument latestChild = (EPMDocument) getIteratedByMaster((EPMDocumentMaster) link.getUses());
            set.add(latestChild);
            // getMemberEPMDoc(latestChild, set);
        }

        return set;
    }

    /**
     * 通过指定的CAD文档查找与其相关的所有EPMBuildRule对象。
     * EPMBuildRule对象是连接CAD文档和部件的link。
     *
     * @param epmDocument
     *            指定CAD文档
     * @return EPMBuildRule 对象集合
     * @throws WTException
     */
    public static QueryResult getEPMBuildRoles(EPMDocument epmDocument) throws WTException {
        QuerySpec qSpec = new QuerySpec(EPMBuildRule.class);
        int[] index = { 0 };
        long longId = epmDocument.getBranchIdentifier();
        System.out.println("--------------longId:" + longId);
        SearchCondition scCondition = new SearchCondition(EPMBuildRule.class, REF_ROLEA_ID, SearchCondition.EQUAL,
                longId);
        qSpec.appendWhere(scCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        return qResult;
    }

    public static EPMMemberLink getEpmMemberLinkByChild(EPMDocument epmDocument) throws WTException {
        QuerySpec qSpec = new QuerySpec(EPMMemberLink.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(epmDocument.getMaster()).getId();
        SearchCondition sCondition = new SearchCondition(EPMMemberLink.class, "roleBObjectRef.key.id",
                SearchCondition.EQUAL, longId);
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        if (qResult.hasMoreElements()) {
            return (EPMMemberLink) qResult.nextElement();
        }
        return null;
    }

    /**
     * 通过指定的部件查找与其相关的所有EPMBuildRule对象。
     * EPMBuildRule对象是连接CAD文档和部件的link。
     *
     * @param part
     *            部件
     * @return EPMBuildRule 对象集合
     * @throws WTException
     */
    public static QueryResult getEPMBuildRoles(WTPart part) throws WTException {
        QuerySpec qSpec = new QuerySpec(EPMBuildRule.class);
        int[] index = { 0 };
        long longId = part.getBranchIdentifier();
        SearchCondition scCondition = new SearchCondition(EPMBuildRule.class, REF_ROLEB_ID, SearchCondition.EQUAL,
                longId);
        qSpec.appendWhere(scCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        return qResult;
    }

    /**
     * 获取3维CAD模型对应的2维CAD图样文档
     *
     * @param part
     *            WTPart对象
     * @return 设计文档集合
     * @throws Exception
     */
    public static Set get2DesignDocs(EPMDocument epm3d) throws Exception {
        Set result = new HashSet();
        // 找3d模型文件的2d图样文件
        QueryResult qr2d = PersistenceHelper.manager.navigate(epm3d.getMaster(),
                EPMReferenceLink.REFERENCED_BY_ROLE, EPMReferenceLink.class, false);
        while (qr2d.hasMoreElements()) {
            EPMReferenceLink link = (EPMReferenceLink) qr2d.nextElement();
            if (link.getDepType() != 4) // 不是图纸关联关系
                continue;
            EPMDocument epm2d = link.getReferencedBy();

            // 仅加入已发布版本
            // if (LCUtil.isReleased(epm2d))
            result.add(epm2d);
        }
        return result;
    }

    /**
     * 通过master查询对象的最新版本
     *
     * @param mst
     *            Mastered
     * @return Iterated
     * @throws RemoteException
     * @throws InvocationTargetException
     */
    public static Iterated getIteratedByMaster(Mastered mst) throws RemoteException, InvocationTargetException {

        Iterated itr = null;

        if (mst != null) {
            QueryResult qr;
            try {
                qr = VersionControlHelper.service.allIterationsOf(mst);
                if (qr.hasMoreElements()) {
                    itr = (Iterated) qr.nextElement();
                }
            } catch (PersistenceException e) {
                e.printStackTrace();
            } catch (WTException e) {
                e.printStackTrace();
            }

        }
        return itr;
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

    public static WTPart getLatestPartByView(Master master, String viewName) throws WTException {
        QueryResult queryResult = VersionControlHelper.service.allVersionsOf(master);
        while (queryResult.hasMoreElements()) {
            Object object = queryResult.nextElement();
            if (object instanceof WTPart) {
                WTPart part = (WTPart) object;
                if (part.getViewName().equals(viewName)) {
                    return part;
                }
            }
        }
        return null;
    }
    public static WTPart getLatestPartByViewAndVersion(Master master, String viewName,String version) throws WTException {
        QueryResult queryResult = VersionControlHelper.service.allVersionsOf(master);
        while (queryResult.hasMoreElements()) {
            Object object = queryResult.nextElement();
            if (object instanceof WTPart) {
                WTPart part = (WTPart) object;
                if (part.getViewName().equals(viewName)) {
                    return part;
                }
            }
        }
        return null;
    }

    /**
     * 通过限定时间范围，查询变更通告。
     *
     * @param startDate
     *            开始时间
     * @param endDate
     *            最后时间
     * @return 查询结果
     * @throws ParseException
     * @throws WTException
     */
    public static QueryResult getChangeOrder2ByDate(PDMLinkProduct product, String startDate, String endDate)
            throws ParseException, WTException {
        QuerySpec qs = new QuerySpec(WTChangeOrder2.class);
        int index[] = { 0 };

        // 只限当前产品容器下
        long longId = PersistenceHelper.getObjectIdentifier(product).getId();
        if (qs.getConditionCount() > 0) {
            qs.appendOr();
        }
        qs.appendOpenParen();
        qs.appendWhere(new SearchCondition(WTChangeOrder2.class, ID_CONTAINER, SearchCondition.EQUAL, longId),
                new int[] { 0 });
        qs.appendCloseParen();

        // 加入时间范围
        TimeZone tz = WTContext.getContext().getTimeZone();
        Calendar ca = Calendar.getInstance(tz);

        // 开始时间
        if (startDate != null && startDate.length() > 0) {
            Date dateFrom = WTStandardDateFormat.parse(startDate, "yyyy/M/d");

            // 加入时区信息
            ca.setTime(dateFrom);
            dateFrom = ca.getTime();

            if (qs.getConditionCount() > 0) {
                qs.appendAnd();
            }
            qs.appendWhere(new SearchCondition(WTChangeOrder2.class,
                    WTChangeOrder2.CREATE_TIMESTAMP,
                    SearchCondition.GREATER_THAN_OR_EQUAL,
                    new Timestamp(dateFrom.getTime())), new int[] { 0 });
        }
        // 最后时间
        if (endDate != null && endDate.length() > 0) {
            Date dateTo = WTStandardDateFormat.parse(endDate, "yyyy/M/d");
            // 加一天以包含指定日期
            ca.setTime(dateTo);
            ca.add(Calendar.DAY_OF_MONTH, 1);
            dateTo = ca.getTime();

            if (qs.getConditionCount() > 0) {
                qs.appendAnd();
            }
            qs.appendWhere(new SearchCondition(WTChangeOrder2.class, WTChangeOrder2.CREATE_TIMESTAMP,
                    SearchCondition.LESS_THAN, new Timestamp(dateTo.getTime())), new int[] { 0 });
        }

        return PersistenceHelper.manager.find((StatementSpec) qs);
    }

    public static WTDocument getDocumentByNumber(String number) throws WTException {
        QuerySpec qs = new QuerySpec(WTDocument.class);
        int index[] = { 0 };
        SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number);
        qs.appendWhere(sc, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qs);
        LatestConfigSpec lcs = new LatestConfigSpec();
        qResult = lcs.process(qResult);
        WTDocument document = null;
        if (qResult.hasMoreElements()) {
            document = (WTDocument) qResult.nextElement();
        }
        return document;
    }



    /**
     * 根据文档软属性PPNUMBER的值查询文档对象
     *
     * @param number
     * @return WTDocument
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws RemoteException
     */
    public static WTDocument getDocumentByIBANumber(String number) throws WTException, WTPropertyVetoException, RemoteException {
    	QuerySpec qs = new QuerySpec(WTDocument.class);
    	qs.setAdvancedQueryEnabled(true);
    	int index[] = { 0 };

    	//查询工艺规程软属性工艺文件编号"PPNUMBER"
		ClassAttribute caId = new ClassAttribute(WTDocument.class, Persistable.PERSIST_INFO + "."
				+ PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
		SubSelectExpression subSelectExpression = getStringIBAQuery("PPNUMBER", number);
		qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);

		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		qr = new LatestConfigSpec().process(qr);
		WTDocument document = null;
		while (qr.hasMoreElements()) {
			document = (WTDocument)qr.nextElement();
		}
		return document;
    }

    /**
     * 根据文档软属性PPNUMBER的值和版本查询文档对象
     *
     * @param number
     * @param version
     * @return WTDocument
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws RemoteException
     */
    public static WTDocument getDocumentByIBANumberAndVersion(String number, String version) throws WTException, WTPropertyVetoException, RemoteException {
        QuerySpec qs = new QuerySpec(WTDocument.class);
        qs.setAdvancedQueryEnabled(true);
        int index[] = { 0 };

        //查询工艺规程软属性工艺文件编号"PPNUMBER"
        ClassAttribute caId = new ClassAttribute(WTDocument.class, Persistable.PERSIST_INFO + "."
                + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
        SubSelectExpression subSelectExpression = getStringIBAQuery("PPNUMBER", number);
        qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
        //根据版本查询
        if(StrUtil.isNotEmpty(version)) {
            qs.appendAnd();
            if(version.contains(".")){
                version = version.substring(0, version.indexOf("."));
            }
            qs.appendWhere(new SearchCondition(WTDocument.class, "versionInfo.identifier.versionId", "=", version),index);
        }
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        qr = new LatestConfigSpec().process(qr);
        WTDocument document = null;
        while (qr.hasMoreElements()) {
            document = (WTDocument)qr.nextElement();
        }
        return document;
    }

    /**
	 * 构建根据String软属性查询的子查询语句
	 *
	 * @param ibaName
	 * @param ibaValue
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 *
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
		qs.appendSelect(new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), new int[] { idx },
						false);
		qs.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", SearchCondition.EQUAL,
				ibaDefId), new int[] { idx });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.EQUAL, ibaValue, false),
				new int[] { idx });
		return new SubSelectExpression(qs);
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
            WTDocument refDoc = getDoc(refDocMaster.getNumber());
            if (!result.contains(refDoc))
                result.add(refDoc);
        }

        return result;
    }

    /**
     * Get Related WTObjects by a document number. Includes Related Parts and Related Documents
     *
     * @param docNumber
     * @return ArrayList with WTObjects
     * @throws WTException
     */
    public static ArrayList getRelatedWTObjectByDoc(WTDocument doc) throws WTException {
        ArrayList result = new ArrayList();

        QueryResult qr = WTPartHelper.service.getDescribesWTParts(doc);
        while (qr.hasMoreElements()) {
            WTPart wtpart = (WTPart) qr.nextElement();
            result.add(wtpart);
        }

        qr = WTDocumentHelper.service.getDependsOnWTDocuments(doc);
        while (qr.hasMoreElements()) {
            WTDocument wtdoc = (WTDocument) qr.nextElement();
            result.add(wtdoc);
        }

        qr = getReferencesWTParts((WTDocumentMaster) doc.getMaster());
        while (qr.hasMoreElements()) {
            WTPart wtpart = (WTPart) qr.nextElement();
            result.add(wtpart);
        }

        return result;
    }

    public static QueryResult getReferencesWTParts(WTDocumentMaster master) throws WTException {
        return StructHelper.service.navigateReferencedBy(master, WTPartReferenceLink.class, true);
    }

    /**
     * This is used to get WTDocument by its number.
     *
     * @param docNumber
     *            document number
     * @return WTDocument or null if there is no document with the number in system.
     */
    public static WTDocument getDoc(String docNumber) {
        WTDocument doc = null;
        String user = "";
        try {
            user = wt.session.SessionHelper.manager.getPrincipal().getName();
            wt.session.SessionHelper.manager.setAdministrator();
        } catch (Exception e) {
        }
        try {
            QuerySpec qs = new QuerySpec(WTDocument.class);
            SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL,
                    docNumber, false);
            qs.appendSearchCondition(sc);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                WTDocument document = (WTDocument) qr.nextElement();
                QueryResult qr2 = VersionControlHelper.service.allIterationsOf(document.getMaster());
                if (qr2.hasMoreElements()) {
                    doc = (WTDocument) qr2.nextElement();
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        if (!"".equals(user)) {
            try {
                wt.session.SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
            }
        }
        return doc;
    }

    /**
     * 通过编号查询最新小版本的WTPart
     *
     * @param number
     * @return WTPart
     * @throws WTException
     */
    public static WTPart getPartByNumber(String number) throws WTException {
        QuerySpec qSpec = new QuerySpec(WTPart.class);
        int[] index = { 0 };
        SearchCondition sCondition = new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, number);
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        LatestConfigSpec lcs = new LatestConfigSpec();
        qResult = lcs.process(qResult);
        if (qResult.hasMoreElements()) {
            return (WTPart) qResult.nextElement();
        }

        return null;
    }

    public static WTPart getTopParentPart(WTPart part, WTPart topPart, Set<WTPart> allParts) throws WTException {
        // 获取当前部件的所有父节点
        QueryResult qr = WTPartHelper.service.getUsedByWTParts((WTPartMaster) part.getMaster());
        while (qr.hasMoreElements()) {
            WTPart parentPart = (WTPart) qr.nextElement();
            if (allParts.contains(parentPart)) {
                topPart = parentPart;
                topPart = getTopParentPart(topPart, topPart, allParts);// 获取最顶层的父节点
            }
        }
        return topPart;
    }

    public static WTUser getUser(String name) throws WTException {
        QuerySpec qs = new QuerySpec(WTUser.class);
        int index[] = { 0 };
        SearchCondition scCondition = new SearchCondition(WTUser.class, WTUser.NAME, SearchCondition.EQUAL, name);
        qs.appendWhere(scCondition, index);
        QueryResult result = PersistenceHelper.manager.find((StatementSpec) qs);
        if (result.hasMoreElements()) {
            return (WTUser) result.nextElement();
        }
        return null;
    }

    public static WTGroup getGroup(String name) throws WTException {
        QuerySpec qs = new QuerySpec(WTGroup.class);
        int index[] = { 0 };
        SearchCondition scCondition = new SearchCondition(WTGroup.class, WTGroup.NAME, SearchCondition.EQUAL, name);
        qs.appendWhere(scCondition, index);
        QueryResult result = PersistenceHelper.manager.find((StatementSpec) qs);
        if (result.hasMoreElements()) {
            return (WTGroup) result.nextElement();
        }
        return null;
    }

    public static ArrayList getAllPrincipalsByRole(Role role, ContainerTeam ct) throws WTException {
        ArrayList arraylist = new ArrayList();
        StandardContainerTeamService scts = StandardContainerTeamService.newStandardContainerTeamService();
        WTGroup wtgroup = scts.findContainerTeamGroup(ct, "roleGroups", role.toString());
        if (wtgroup != null) {
            Enumeration enumeration = OrganizationServicesHelper.manager.members(wtgroup, false, true);
            while (enumeration.hasMoreElements()) {
                WTPrincipalReference wtprincipalreference = WTPrincipalReference
                        .newWTPrincipalReference((WTPrincipal) enumeration
                                .nextElement());
                arraylist.add(wtprincipalreference);
            }
        }
        return arraylist;
    }

    public static QueryResult getMPMOperationUsageLinkByMpmPr(MPMProcessPlan plan) throws WTException {
        QuerySpec qSpec = new QuerySpec(MPMOperationUsageLink.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(plan).getId();
        SearchCondition sCondition = new SearchCondition(MPMOperationUsageLink.class, "roleAObjectRef.key.id",
                SearchCondition.EQUAL, longId);
        qSpec.appendWhere(sCondition, index);
        return PersistenceHelper.manager.find((StatementSpec) qSpec);
    }

    public static QueryResult getMPMOperationUsageLinkByMpmOper(MPMOperation operation) throws WTException {
        QuerySpec qSpec = new QuerySpec(MPMOperationUsageLink.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(operation).getId();
        SearchCondition sCondition = new SearchCondition(MPMOperationUsageLink.class, "roleAObjectRef.key.id",
                SearchCondition.EQUAL, longId);
        qSpec.appendWhere(sCondition, index);
        return PersistenceHelper.manager.find((StatementSpec) qSpec);
    }

    public static List<MPMOperation> getAllMpmOperationsByMPMProPlan(MPMProcessPlan processPlan) throws WTException {
        QueryResult qResult = getMPMOperationUsageLinkByMpmPr(processPlan);
        List<MPMOperation> list = new ArrayList<MPMOperation>();
        while (qResult.hasMoreElements()) {
            MPMOperationUsageLink link = (MPMOperationUsageLink) qResult.nextElement();
            Persistable persistable = link.getRoleBObject();
            if (persistable instanceof MPMOperationMaster) {
                MPMOperationMaster master = (MPMOperationMaster) persistable;
                QueryResult qResult2 = VersionControlHelper.service.allVersionsOf(master);
                if (qResult2.hasMoreElements()) {
                    MPMOperation operation = (MPMOperation) qResult2.nextElement();
                    if (!list.contains(operation)) {
                        list.add(operation);
                    }
                    getAllChildMpmoOperations(operation, list);
                }
            }
        }
        return list;
    }

    public static List<MPMOperation> getAllChildMpmoOperations(MPMOperation operation, List<MPMOperation> list)
            throws WTException {
        QueryResult qResult = getMPMOperationUsageLinkByMpmOper(operation);
        while (qResult.hasMoreElements()) {
            MPMOperationUsageLink link = (MPMOperationUsageLink) qResult.nextElement();
            Persistable persistable = link.getRoleBObject();
            if (persistable instanceof MPMOperationMaster) {
                MPMOperationMaster master = (MPMOperationMaster) persistable;
                QueryResult qResult2 = VersionControlHelper.service.allVersionsOf(master);
                if (qResult2.hasMoreElements()) {
                    MPMOperation childOperation = (MPMOperation) qResult2.nextElement();
                    if (!list.contains(childOperation)) {
                        list.add(childOperation);
                    }
                    getAllChildMpmoOperations(childOperation, list);
                }
            }
        }
        return list;
    }

    public static MPMOperation getMpmOperation(String number) throws WTException {
        QuerySpec qSpec = new QuerySpec(MPMOperation.class);
        int[] index = { 0 };
        SearchCondition sCondition = new SearchCondition(MPMOperation.class, MPMOperation.NUMBER,
                SearchCondition.EQUAL, number);
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        qResult = new LatestConfigSpec().process(qResult);
        if (qResult.hasMoreElements()) {
            MPMOperation mpmOperation = (MPMOperation) qResult.nextElement();
            return mpmOperation;
        }
        return null;
    }

    public static MPMProcessMaterial getMpmProcessMaterialByNumber(String number) throws WTException {
        QuerySpec qSpec = new QuerySpec(MPMProcessMaterial.class);
        int[] index = { 0 };
        SearchCondition sCondition = new SearchCondition(MPMProcessMaterial.class, "master>number",
                SearchCondition.EQUAL, number);
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        LatestConfigSpec lcs = new LatestConfigSpec();
        qResult = lcs.process(qResult);
        if (qResult.hasMoreElements()) {
            return (MPMProcessMaterial) qResult.nextElement();
        }
        return null;
    }

    public static MPMTooling getMpmMPMToolingByNumber(String number) throws WTException {
        QuerySpec qSpec = new QuerySpec(MPMTooling.class);
        int[] index = { 0 };
        SearchCondition sCondition = new SearchCondition(MPMTooling.class, "master>number", SearchCondition.EQUAL,
                number);
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        LatestConfigSpec lcs = new LatestConfigSpec();
        qResult = lcs.process(qResult);
        if (qResult.hasMoreElements()) {
            return (MPMTooling) qResult.nextElement();
        }
        return null;
    }

    public static MPMProcessPlan getProcessPlanByNumber(String number) throws WTException {
        MPMProcessPlan pplan = null;
        QueryResult qr = null;
        QuerySpec qs = new QuerySpec(MPMProcessPlan.class);
        qs.appendWhere(new SearchCondition(MPMProcessPlan.class, MPMProcessPlan.NUMBER, SearchCondition.EQUAL, number,
                false));
        qr = PersistenceHelper.manager.find((StatementSpec) qs);
        LatestConfigSpec lcs = new LatestConfigSpec();
        qr = lcs.process(qr);
        if (qr.hasMoreElements()) {
            pplan = (MPMProcessPlan) qr.nextElement();
        }
        return pplan;
    }

    public static Persistable getPersistable(String oid) throws WTException {
        ReferenceFactory factory = new ReferenceFactory();
        WTReference reference = factory.getReference(oid);
        return reference.getObject();
    }

    public static String getOid(Persistable persistable) throws WTException {
        ReferenceFactory factory = new ReferenceFactory();
        return factory.getReferenceString(persistable);
    }

    public static boolean isRoleMember(WTUser user,String roleKey,WTContainer container) throws WTException {
    	boolean flag = false;
    	ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) container);
        Role role = Role.toRole(roleKey);
        if (role != null) {
        	ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
            for (WTPrincipalReference reference : arrayList) {
                Object object2 = reference.getPrincipal();
                if (object2 instanceof WTUser) {
                    WTUser userTemp = (WTUser) object2;
                    if (user.equals(userTemp)) {
                        flag = true;
                        break;
                    }
                }
            }
        }
    	return flag;
    }
    /*
     * 通过文档获取关联的部件
     */
    public static WTPart getRelatedWTPartByDoc(WTDocument doc) throws WTException {
        QueryResult qr = WTPartHelper.service.getDescribesWTParts(doc);
        while (qr.hasMoreElements()) {
            WTPart wtpart = (WTPart) qr.nextElement();
            return wtpart;
        }
        return null;
    }

    public static boolean isAdmin(){
		try {
			WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
			if (currentUser.getName().equals("Administrator")||AccessAdminUtil.isAdmin()||AccessAdminUtil.isSysAdmin()) {
	        	 return true;
	         }

		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return false;
    }
	/**
	 * 获取对象默认表示法
	 * @param obj
	 * @throws WTException
	 * @throws PropertyVetoException
	 */
	@SuppressWarnings("rawtypes")
	public static ApplicationData getRepresentation(Representable obj) throws WTException, PropertyVetoException{
		Representation representation = RepresentationHelper.service.getDefaultRepresentation(obj);
		if(representation != null){
			representation = (Representation) ContentHelper.service.getContents(representation);
			Vector vector = ContentHelper.getContentList(representation);
			for(int i = 0; i < vector.size(); i++){
				ContentItem contentItem = (ContentItem) vector.elementAt(i);
				if(contentItem instanceof ApplicationData){
					ApplicationData appData = (ApplicationData) contentItem;
					String appName = appData.getFileName();
					String extension = FileUtil.getExtension(appName);
					if("PDF".equalsIgnoreCase(extension)){
						return appData;
					}
				}
			}
		}
		return null;
	}

    public static SubSelectExpression getStringIBAQueryByLike(String ibaName, String ibaValue) throws WTException,
            WTPropertyVetoException, RemoteException {
        // 获取IBA属性定义
        AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
        if (addv == null)
            throw new IBADefinitionException("No IBA Definition: " + ibaName);
        long ibaDefId = addv.getObjectID().getId();
        QuerySpec qs = new QuerySpec();
        int idx = qs.appendClassList(StringValue.class, false);
        qs.appendSelect(new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), new int[] { idx },
                false);
        qs.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", SearchCondition.EQUAL,
                ibaDefId), new int[] { idx });
        qs.appendAnd();
        qs.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.LIKE, "%" + ibaValue + "%", false),
                new int[]{idx});
        return new SubSelectExpression(qs);
    }
}
