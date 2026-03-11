package ext.casc.sop.util;

import java.beans.PropertyVetoException;
import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.release.ProcessInfoReleaseController;
import com.glaway.mpm.util.*;
import com.ptc.cat.property.server.GWTPropertiesUtil;
import ext.casc.constants.Constants;
import ext.casc.dfmRule.util.TeamUtil;
import ext.casc.integrate.util.CldeUtil;
import ext.casc.integrate.util.ZipUtil;
import ext.casc.util.DBConn;
import ext.casc.workflow.signtrue.zp.SignatureService;
import org.apache.commons.lang.StringUtils;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.doc.WTDocument;
import wt.fc.*;
import wt.fc.ReferenceFactory;
import wt.iba.definition.IBADefinitionException;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.inf.container.OrgContainer;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.org.*;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.pds.StatementSpec;
import wt.query.ClassAttribute;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.session.SessionHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;

import com.glaway.mpm.sop.model.ParametersBean;
import com.glaway.mpm.sop.model.SopBean;
import com.glaway.mpm.visual.log.VaLogger;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.resource.MPMTooling;

import ext.casc.process.ProcessConstants;
import ext.casc.process.ProcessTask;
import ext.casc.process.ProcessTaskItem;
import ext.casc.process.ProcessTaskLink;
import ext.casc.sop.bean.SOPPartsBean;
import ext.casc.sop.bean.SOPProductBean;
import ext.casc.sop.bean.SOPQMPartInfoBean;
import ext.casc.sop.constants.SopConstants;
import ext.casc.util.IBAUtility;
import ext.casc.workflow.WorkflowHelper;
import ext.sast.center.util.OrgUtil;

public class SopUtil {

	private static VaLogger logger = VaLogger.getLogger(SopUtil.class.getName());
	public static int index[] = { 0 };

	/**
	 * 根据资源类型获取sop资源
	 *
	 * @param objType
	 *            类型
	 * @return resourceList
	 * @throws Exception
	 *             e
	 */
	public static List<WTPart> getSopResourceByType(String objType) throws Exception {
		List<WTPart> resourceList = new ArrayList<WTPart>();
		QuerySpec querySpec = new QuerySpec(WTPart.class);
		TypeUtil.getTypeQuery(WTPart.class, objType, querySpec);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		queryResult = new LatestConfigSpec().process(queryResult);
		while (queryResult.hasMoreElements()) {
			Object o = queryResult.nextElement();
			if (o instanceof WTPart) {
				resourceList.add((WTPart) o);
			}
		}
		return resourceList;
	}

	/**
	 * 根据资源类型获取sop资源
	 *
	 * @param objType
	 *            类型
	 * @return resourceList
	 * @throws Exception
	 *             e
	 */
	public static List<MPMTooling> getSopResourceByType2(String objType) throws Exception {
		List<MPMTooling> resourceList = new ArrayList<MPMTooling>();
		QuerySpec querySpec = new QuerySpec(MPMTooling.class);
		TypeUtil.getTypeQuery(MPMTooling.class, objType, querySpec);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		queryResult = new LatestConfigSpec().process(queryResult);
		while (queryResult.hasMoreElements()) {
			Object o = queryResult.nextElement();
			if (o instanceof MPMTooling) {
				resourceList.add((MPMTooling) o);
			}
		}
		return resourceList;
	}

	/**
	 * 获取所有SOP工序名称
	 *
	 * @param zylb
	 *            专业类别
	 * @return gxmcList
	 */
	public static List<MPMTooling> getSOPAllGxmc() throws Exception {
		List<MPMTooling> gxmcList = new ArrayList<MPMTooling>();

		QuerySpec qs = new QuerySpec(MPMTooling.class);
		qs.setAdvancedQueryEnabled(true);
		TypeUtil.getTypeQuery(MPMTooling.class, SopConstants.SOP_TYPE_PROCEDUCENAME, qs);

		qs.appendAnd();
		qs.appendOpenParen();
		ClassAttribute caId = new ClassAttribute(MPMTooling.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
		String ibaName = "SpecializedType";
		AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
		if (addv == null)
			throw new IBADefinitionException("No IBA Definition: " + ibaName);
		long ibaDefId = addv.getObjectID().getId();
		QuerySpec qs1 = new QuerySpec();
		int idx = qs1.appendClassList(StringValue.class, false);
		qs1.appendSelect(new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), new int[] { idx }, false);
		qs1.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", SearchCondition.EQUAL, ibaDefId), new int[] { idx });
		qs1.appendAnd();
		qs1.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.NOT_NULL, "", false), new int[] { idx });
		SubSelectExpression subSelectExpression = new SubSelectExpression(qs1);
		qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
		qs.appendCloseParen();

		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr = lcs.process(qr);
		MPMTooling wtPart = null;
		while (qr.hasMoreElements()) {
			wtPart = (MPMTooling) qr.nextElement();
			gxmcList.add(wtPart);
		}
		return gxmcList;
	}

	/**
	 * 获取所有工序名称
	 *
	 * @param zylb
	 *            专业类别
	 * @return gxmcList
	 */
	public static List<MPMTooling> getAllGxmc(String zylb) throws Exception {
		List<MPMTooling> gxmcList = new ArrayList<MPMTooling>();

		QuerySpec qs = new QuerySpec(MPMTooling.class);
		qs.setAdvancedQueryEnabled(true);
		TypeUtil.getTypeQuery(MPMTooling.class, SopConstants.SOP_TYPE_PROCEDUCENAME, qs);
		qs.appendAnd();
		qs.appendOpenParen();
		ClassAttribute caId = new ClassAttribute(MPMTooling.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
		SubSelectExpression subSelectExpression = getStringIBAQuery("SpecializedType", zylb);
		qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
		qs.appendCloseParen();
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr = lcs.process(qr);
		MPMTooling wtPart = null;
		while (qr.hasMoreElements()) {
			wtPart = (MPMTooling) qr.nextElement();
			gxmcList.add(wtPart);
		}
		return gxmcList;
	}

	public static SubSelectExpression getStringIBAQuery(String ibaName, String ibaValue) throws WTException, RemoteException {
		// 获取IBA属性定义
		AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
		if (addv == null)
			throw new IBADefinitionException("No IBA Definition: " + ibaName);
		long ibaDefId = addv.getObjectID().getId();
		QuerySpec qs = new QuerySpec();
		int idx = qs.appendClassList(StringValue.class, false);
		qs.appendSelect(new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), new int[] { idx }, false);
		qs.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", SearchCondition.EQUAL, ibaDefId), new int[] { idx });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.EQUAL, ibaValue, false), new int[] { idx });
		return new SubSelectExpression(qs);
	}

	public static List<WTPart> getAllCsxmmc(String zylb) throws Exception {
		List<WTPart> csxmmcList = new ArrayList<WTPart>();

		QuerySpec qs = new QuerySpec(WTPart.class);
		qs.setAdvancedQueryEnabled(true);
		TypeUtil.getTypeQuery(WTPart.class, SopConstants.SOP_TYPE_PARAMETERSNAME, qs);
		qs.appendAnd();
		qs.appendOpenParen();
		ClassAttribute caId = new ClassAttribute(WTPart.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
		SubSelectExpression subSelectExpression = getStringIBAQuery("SpecializedType", zylb);
		qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
		qs.appendCloseParen();
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr = lcs.process(qr);
		WTPart wtpart = null;
		while (qr.hasMoreElements()) {
			wtpart = (WTPart) qr.nextElement();
			csxmmcList.add(wtpart);
		}
		return csxmmcList;

	}

	public static List<MPMTooling> getAllMaterialCategory(String zylb) throws Exception {
		List<MPMTooling> csxmmcList = new ArrayList<MPMTooling>();

		QuerySpec qs = new QuerySpec(MPMTooling.class);
		qs.setAdvancedQueryEnabled(true);
		TypeUtil.getTypeQuery(MPMTooling.class, SopConstants.SOP_TYPE_MATERIALCATEGORY, qs);
		qs.appendAnd();
		qs.appendOpenParen();
		ClassAttribute caId = new ClassAttribute(MPMTooling.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
		SubSelectExpression subSelectExpression = getStringIBAQuery("SpecializedType", zylb);
		qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
		qs.appendCloseParen();
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr = lcs.process(qr);
		MPMTooling wtpart = null;
		while (qr.hasMoreElements()) {
			wtpart = (MPMTooling) qr.nextElement();
			csxmmcList.add(wtpart);
		}
		return csxmmcList;

	}

	public static void getUserFromWTGroup(WTGroup group, List<WTUser> list) throws WTException {
		if (group == null || list == null) {
			return;
		}
		Enumeration member = group.members();
		while (member.hasMoreElements()) {
			WTPrincipal principal = (WTPrincipal) member.nextElement();
			if (principal instanceof WTUser) {
				list.add((WTUser) principal);
			} else if (principal instanceof WTGroup) {
				getUserFromWTGroup((WTGroup) principal, list);
			}
		}
	}

	public static String getGyyJsonInfo(String taskOid) {
		ReferenceFactory rf = new ReferenceFactory();
		try {
			ProcessTaskItem taskItem = (ProcessTaskItem) rf.getReference(taskOid).getObject();
			Long partId = taskItem.getPartId();
			WTPart part = WTPartUtil.getPartByOid(partId);
			String zhuzhichejian = taskItem.getChejian();
			List<WTUser> userList = getUserListFromLibrary(part,zhuzhichejian);
			StringBuffer result = new StringBuffer("[");
			result.append("{");
			result.append("text:'工艺员',");
			result.append("expand:true,");
			result.append("children:[");
			for (WTUser user : userList) {
				String name = user.getName() + "(" + user.getFullName() + ")";
				result.append("{");
				result.append("id:'" + rf.getReferenceString(user) + "',");
				result.append("text:'" + name + "',");
				result.append("checked:false,");
				result.append("leaf:true");
				result.append("},");
			}
			if (!userList.isEmpty()) {
				result = result.deleteCharAt(result.length() - 1);
			}
			result.append("]");
			result.append("}");
			result.append("]");
			return result.toString();
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static List<WTUser> getUserList(String department) throws WTException {
		List<WTUser> userList = null;
		department = transDepartment(department);
		if (department != null) {
			userList = new ArrayList<WTUser>();
			WTPrincipal admin = SessionHelper.manager.getAdministrator();
			WTOrganization wtOrganization = OrganizationServicesHelper.manager.getOrganization(admin);
			OrgContainer orgContainer = WTContainerHelper.service.getOrgContainer(wtOrganization);
			List<?> list = OrgUtil.getNodes(orgContainer);
			for (Object object : list) {
				if (object instanceof WTGroup) {
					WTGroup group = (WTGroup) object;
					if (group.getName().equals("部门_" + department + "_工艺员")) {
						SopUtil.getUserFromWTGroup(group, userList);
					}
				}
			}
		}

		return userList;
	}

	public static List<WTUser> getUserListFromLibrary(WTPart wtPart,String department) throws WTException {
		List<WTUser> userList = null;
		//正式机 测试机为 *分厂
		//本地虚拟机为  *车间
		department = transDepartment(department);
		if (department != null) {
			userList = new ArrayList<WTUser>();
			userList = WorkflowHelper.getUserFromLibrary(wtPart, department+"工艺员");
		}
		if(userList.isEmpty()){
			String roleGyy = Constants.allChejianToWorkFlowGYYRoleMap.get(department);
			if(StrUtil.isNotEmpty(roleGyy)){
				List<WTPrincipal> list = TeamUtil.getRoleMember((ContainerTeamManaged) wtPart.getContainer(), roleGyy);
				for(WTPrincipal principal : list) {
					if(principal instanceof WTUser){
						userList.add((WTUser) principal);
					} else  if(principal instanceof WTGroup){
						WTGroup group = (WTGroup) principal;
						Set<WTUser> userSet = new HashSet<>();
						SignatureService.getUserFromWTGroup(group, userSet);
						userList.addAll(userSet);
					}
				}
			}
		}
		return userList;
	}

	private static String transSOPDepartment(String department) {
		if ("1".equals(department)) {
			department = "一车间";
		} else if ("2".equals(department)) {
			department = "二车间";
		} else if ("3".equals(department)) {
			department = "三车间";
		} else if ("4".equals(department)) {
			department = "四车间";
		} else if ("5".equals(department)) {
			department = "五车间";
		} else if ("6".equals(department)) {
			department = "六车间";
		} else if ("7".equals(department)) {
			department = "七车间";
		} else if ("8".equals(department)) {
			department = "八车间";
		} else if ("9".equals(department)) {
			department = "九车间";
		} else if ("10".equals(department)) {
			department = "十车间";
		}
		return department;
	}

	private static String transDepartment(String department) {
		if ("1".equals(department)) {
			department = "一分厂";
		} else if ("2".equals(department)) {
			department = "二分厂";
		} else if ("3".equals(department)) {
			department = "三分厂";
		} else if ("4".equals(department)) {
			department = "四分厂";
		} else if ("5".equals(department)) {
			department = "五分厂";
		} else if ("6".equals(department)) {
			department = "六分厂";
		} else if ("7".equals(department)) {
			department = "七分厂";
		} else if ("8".equals(department)) {
			department = "八分厂";
		} else if ("9".equals(department)) {
			department = "九分厂";
		} else if ("10".equals(department)) {
			department = "十分厂";
		}
		return department;
	}

	/**
	 * 创建PBOM的xml
	 *
	 * @param part
	 * @return
	 */
	public static String createPbomXml(WTPart part) throws WTException {
		long startTime = System.currentTimeMillis();
		IBAUtility ibaUtility = new IBAUtility(part);
		// sop部件编号信息
		String partOid = String.valueOf(part.getPersistInfo().getObjectIdentifier().getId());
		String partState = part.getState().getState().getDisplay(Locale.CHINA);
		String partVersion = part.getIterationDisplayIdentifier().toString();
		;
		String secret = ibaUtility.getIBAValue(SopConstants.SOP_IBA_SECRET);
		String term = ibaUtility.getIBAValue(SopConstants.SOP_IBA_TERM);
		String specializedType = ibaUtility.getIBAValue(SopConstants.SOP_IBA_SPECIALIZEDTYPE);
		String procedureName = ibaUtility.getIBAValue(SopConstants.SOP_IBA_PROCEDUCENAME);
		String professionalCode = ibaUtility.getIBAValue(SopConstants.SOP_IBA_PROFESSIONALCODE);
		String gxjh = ibaUtility.getIBAValue(SopConstants.SOP_IBA_GONGXUJIANHAO);
		String zzcj = ibaUtility.getIBAValue(SopConstants.SOP_IBA_DEPARTMENT);

		// part节点
		SOPQMPartInfoBean partInfoBean = new SOPQMPartInfoBean();
		partInfoBean.setPartNumber(part.getNumber());
		partInfoBean.setPartName(part.getName());
		partInfoBean.setOid(partOid);
		partInfoBean.setLifecycle(partState);
		partInfoBean.setVersion(partVersion);
		partInfoBean.setSecret(secret);
		partInfoBean.setTerm(term);
		partInfoBean.setSpecializedType(specializedType);
		partInfoBean.setProcedureName(procedureName);
		partInfoBean.setProfessionalCode(professionalCode);
		partInfoBean.setGxjh(gxjh);
		partInfoBean.setDept(zzcj);
		// parts 节点
		List<SOPQMPartInfoBean> partInfoBeanList = new ArrayList<SOPQMPartInfoBean>();
		partInfoBeanList.add(partInfoBean);
		SOPPartsBean partsBean = new SOPPartsBean(partInfoBeanList);
		// product节点
		WTContainer container = part.getContainer();
		String containerOid = String.valueOf(container.getPersistInfo().getObjectIdentifier().getId());
		String productNumber = container.getName();
		SOPProductBean productBean = new SOPProductBean(containerOid, productNumber, productNumber, partsBean);
		long end = System.currentTimeMillis();
		System.out.println("组建xml耗时：" + (end - startTime));
		return XMLUtil.convertToXml(productBean);
	}

	public static List<WTPart> getAllCSXM(String zylb) throws Exception {
		List<WTPart> csxm = new ArrayList<WTPart>();

		QuerySpec qs = new QuerySpec(WTPart.class);
		qs.setAdvancedQueryEnabled(true);
		TypeUtil.getTypeQuery(WTPart.class, SopConstants.SOP_TYPE_PARAMETERS, qs);
		qs.appendAnd();
		qs.appendOpenParen();
		ClassAttribute caId = new ClassAttribute(WTPart.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
		SubSelectExpression subSelectExpression = getStringIBAQuery("SpecializedType", zylb);
		qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
		qs.appendCloseParen();
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr = lcs.process(qr);
		WTPart wtpart = null;
		while (qr.hasMoreElements()) {
			wtpart = (WTPart) qr.nextElement();
			csxm.add(wtpart);
		}
		return csxm;

	}

	/**
	 * 根据资源类型获取sop资源名称集合
	 *
	 * @param objType
	 *            类型
	 * @return resourceList
	 * @throws Exception
	 *             e
	 */
	public static List<String> getSopResourceName(String objType) throws Exception {
		List<String> resourceList = new ArrayList<String>();
		QuerySpec querySpec = new QuerySpec(WTPart.class);
		TypeUtil.getTypeQuery(WTPart.class, objType, querySpec);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		queryResult = new LatestConfigSpec().process(queryResult);
		MPMTooling tooling = null;
		while (queryResult.hasMoreElements()) {
			Object o = queryResult.nextElement();
			if (o instanceof MPMTooling) {
				tooling = (MPMTooling) o;
				resourceList.add(tooling.getName());
			}
		}
		return resourceList;
	}

	/**
	 * 方法功能: 查询SOP
	 *
	 * @param sopBeanInfo
	 * @return java.util.List<com.glaway.mpm.sop.model.SopBean>
	 * @author LB
	 * @date 2019/12/24
	 */
	public static List<SopBean> querySop(SopBean sopBeanInfo) throws Exception {
		QuerySpec qs = new QuerySpec(WTDocument.class);
		qs.setAdvancedQueryEnabled(true);
		TypeUtil.getTypeQuery(WTDocument.class, SopConstants.SOP_TYPE_SOPDOC, qs);
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LIFE_CYCLE_STATE, SearchCondition.EQUAL, "APPROVED"), new int[] { 0 });
		String number = sopBeanInfo.getNumber();
		String specializedName = sopBeanInfo.getSpecializedType();
		String proceduceName = sopBeanInfo.getProceduceName();
		String cheJian = sopBeanInfo.getDepartment();
		if (StringUtils.isNotEmpty(specializedName) || StringUtils.isNotEmpty(proceduceName) || StringUtils.isNotEmpty(cheJian)) {
			qs.appendAnd();
			qs.appendOpenParen();
			ClassAttribute caId = new ClassAttribute(WTDocument.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
			if (StringUtils.isNotEmpty(specializedName)) {
				SubSelectExpression subSelectExpression = getStringIBAQuery(SopConstants.SOP_IBA_SPECIALIZEDTYPE, specializedName);
				qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
			}
			if (StringUtils.isNotEmpty(cheJian)) {
				if (StringUtils.isNotEmpty(specializedName)) {
					qs.appendAnd();
				}
				SubSelectExpression subSelectExpression = getStringIBAQuery("SopDepartment", cheJian);
				qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
			}
			if (StringUtils.isNotEmpty(proceduceName)) {
				if (StringUtils.isNotEmpty(specializedName) || StringUtils.isNotEmpty(cheJian)) {
					qs.appendAnd();
				}
				SubSelectExpression subSelectExpression = getStringIBAQuery(SopConstants.SOP_IBA_PROCEDUCENAME, proceduceName);
				qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
			}
			qs.appendCloseParen();
		}

		if(StringUtils.isNotEmpty(number)){
			ClassAttribute caId = new ClassAttribute(WTDocument.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
			qs.appendAnd();
			qs.appendOpenParen();
			qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.LIKE, "%" + number + "%"), index);
			qs.appendOr();
			SubSelectExpression subSelectExpression = getStringIBAQuery("PPNUMBER", number);
			qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
			qs.appendCloseParen();
		}

		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr = lcs.process(qr);
		List<SopBean> list = new ArrayList<SopBean>();
		WTDocument processPlan = null;
		while (qr.hasMoreElements()) {
			processPlan = (WTDocument) qr.nextElement();
			list.add(buildSopBean(processPlan));
		}
		return list;
	}

	public static SopBean buildSopBean(WTDocument wtDocument) throws Exception {
		SopBean sopBean = new SopBean();
		sopBean.setOid(String.valueOf(wtDocument.getPersistInfo().getObjectIdentifier().getId()));
		sopBean.setNumber(wtDocument.getNumber());
		sopBean.setName(wtDocument.getName());
		sopBean.setPpnumber(IBAHelper.getIBAValue(wtDocument, SopConstants.SOP_IBA_SOPNUMBER));
		sopBean.setSpecializedType(IBAHelper.getIBAValue(wtDocument, SopConstants.SOP_IBA_SPECIALIZEDTYPE));
		sopBean.setProceduceName(IBAHelper.getIBAValue(wtDocument, SopConstants.SOP_IBA_PROCEDUCENAME));
		sopBean.setOperationJob(IBAHelper.getIBAValue(wtDocument, SopConstants.SOP_IBA_OPERATIONJOB));
		sopBean.setCustomArea(IBAHelper.getIBAValue(wtDocument, SopConstants.SOP_IBA_CUSTOMAREA));
		sopBean.setParameters(IBAHelper.getIBAValue(wtDocument, SopConstants.SOP_IBA_PARAMETERS));
		sopBean.setSecret(IBAHelper.getIBAValue(wtDocument, SopConstants.SOP_IBA_SECRET));
		String version = wtDocument.getVersionIdentifier().getValue() + "." + wtDocument.getIterationIdentifier().getValue();
		sopBean.setVersion(version);
		sopBean.setProfessionalCod(IBAHelper.getIBAValue(wtDocument, SopConstants.SOP_IBA_PROFESSIONALCODE));
		sopBean.setGongXuJianHao(IBAHelper.getIBAValue(wtDocument, SopConstants.SOP_IBA_GONGXUJIANHAO));
		sopBean.setDescription(wtDocument.getDescription());
		sopBean.setDepartment(IBAHelper.getIBAValue(wtDocument, SopConstants.SOP_IBA_DEPARTMENT));
		List<ParametersBean> parametersBeanList = getRelatedParameters(wtDocument.getNumber(), wtDocument.getVersionIdentifier().getValue());
		sopBean.setArameters(parametersBeanList);
		return sopBean;
	}

	private static List<ParametersBean> getRelatedParameters(String technicsNumber, String technicsVersion) {
		DBConnUtil dbConnUtil = null;
		List<ParametersBean> parametersBeanList = new ArrayList<ParametersBean>();
		ParametersBean parametersBean;
		try {
			dbConnUtil = new DBConnUtil();
			String sql = "select * from GL_DOCPARAMETERSLINK where TECHNICSNUMBER='" + technicsNumber + "' and TECHNICSVERSION='" + technicsVersion + "'";
			ResultSet resultSet = dbConnUtil.executeQuery(sql);
			while (resultSet.next()) {
				long parameteroid = resultSet.getLong("PARAMETEROID");
				MPMTooling parameter = (MPMTooling) QueryUtil.getObjectByOid(MPMTooling.class, parameteroid);
				if (parameter != null) {
					parametersBean = buildParametersBean(parameter);
					parametersBeanList.add(parametersBean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return parametersBeanList;
	}

	public static ParametersBean buildParametersBean(MPMTooling mpmTooling) throws Exception {
		ParametersBean parametersBean = new ParametersBean();
		parametersBean.setOid(mpmTooling.getPersistInfo().getObjectIdentifier().getId());
		parametersBean.setNumber(mpmTooling.getNumber());
		parametersBean.setName(mpmTooling.getName());
		parametersBean.setSpecializedType(IBAHelper.getIBAValue(mpmTooling, SopConstants.SOP_IBA_SPECIALIZEDTYPE));
		parametersBean.setProceduceName(IBAHelper.getIBAValue(mpmTooling, SopConstants.SOP_IBA_PROCEDUCENAME));
		parametersBean.setMaterialCategory(IBAHelper.getIBAValue(mpmTooling, SopConstants.SOP_IBA_MATERIALCATEGORY));
		parametersBean.setCanShuZhi(IBAHelper.getIBAValue(mpmTooling, SopConstants.SOP_IBA_CANSHUZHI));
		parametersBean.setDescription(mpmTooling.getDescription());
		return parametersBean;
	}

	public static List<ParametersBean> queryParameters(ParametersBean parametersBean) throws Exception {
		QuerySpec qs = new QuerySpec(MPMTooling.class);
		qs.setAdvancedQueryEnabled(true);
		TypeUtil.getTypeQuery(MPMTooling.class, SopConstants.SOP_TYPE_PARAMETERS, qs);
		qs.appendAnd();
		String name = parametersBean.getName();
		SearchCondition sc = new SearchCondition(MPMTooling.class, "master>name", SearchCondition.EQUAL, name);
		qs.appendWhere(sc, index);

		String specializedName = parametersBean.getSpecializedType();
		String proceduceName = parametersBean.getProceduceName();
		if (StringUtils.isNotEmpty(specializedName) || StringUtils.isNotEmpty(proceduceName)) {
			qs.appendAnd();
			qs.appendOpenParen();
			ClassAttribute caId = new ClassAttribute(MPMTooling.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
			if (StringUtils.isNotEmpty(specializedName)) {
				SubSelectExpression subSelectExpression = getStringIBAQuery(SopConstants.SOP_IBA_SPECIALIZEDTYPE, specializedName);
				qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
			}
			if (StringUtils.isNotEmpty(proceduceName)) {
				SubSelectExpression subSelectExpression = getStringIBAQuery(SopConstants.SOP_IBA_PROCEDUCENAME, proceduceName);
				qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
			}
			qs.appendCloseParen();
		}

		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr = lcs.process(qr);
		List<ParametersBean> list = new ArrayList<ParametersBean>();
		MPMTooling mpmTooling = null;
		while (qr.hasMoreElements()) {
			mpmTooling = (MPMTooling) qr.nextElement();
			list.add(buildParametersBean(mpmTooling));
		}
		return list;
	}

	public static List<Vector<Object>> downloadTechnics(String sopOid) throws Exception {
//		logger.debug("sopOid=" + sopOid);
		List<Vector<Object>> list = new ArrayList<Vector<Object>>();
		// MPMProcessPlan processPlan =
		// MPMProcessPlanUtil.getMPMProcessPlanByOid(Long.valueOf(sopOid));
		WTDocument document = (WTDocument) QueryUtil.getObjectByOid(WTDocument.class, Long.valueOf(sopOid));
		// logger.debug("processPlan="+processPlan);
		// WTDocument document =
		// MPMProcessPlanUtil.getWTDocumentByProcessPlan(processPlan);
//		logger.debug("document=" + document);
		byte[] bytes = null;
		Vector<Object> vector = null;
		if (document != null) {
			document = WTDocumentUtil.getLatestDocumentByNumber(document.getNumber());
			ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
			bytes = WTDocumentUtil.applicationDataToByte(data);
			vector = new Vector<Object>();
			vector.add(data.getFileName());
			vector.add(bytes);
			vector.add(document.getLifeCycleState().getLocalizedMessage(Locale.CHINA));
			vector.add(document.getIterationDisplayIdentifier().toString());
			vector.add(document.getNumber());
			vector.add(document.getName());
			list.add(vector);
		} else {
			throw new WTException("找不到该SOP标准规程关联的ZIP数据包对象：" + sopOid);
		}
//		logger.debug("list=" + list);
		return list;
	}

	/**
	 * SOP工艺获取流水号
	 *
	 * @param type
	 *            1:查询；2:更新
	 * @param pre
	 * @return
	 * @throws WTException
	 */
	public static String getSopSeqNumber(Integer type, String pre) throws WTException {
		StringBuilder sql = new StringBuilder("select NUM FROM ");
		sql.append("GL_BBLGY_SEQ ").append("WHERE PRE='").append(pre).append("'");
		DBConn conn = null;
		long num = 0;
		try {
			conn = new DBConn();
			conn.start();
			ResultSet rs = conn.executeQuery(sql.toString());
			if (rs.next()) {
				num = rs.getLong("NUM");
			}
			if (num == 0) {
				num = 001;
				if (2 == type) {
					// 更新
					StringBuilder insertSql = new StringBuilder();
					insertSql.append("INSERT INTO GL_BBLGY_SEQ(PRE,NUM) ")//
							.append("VALUES('").append(pre).append("',")//
							.append("'").append(num).append("')");
					conn.executeUpdate(insertSql.toString());
					conn.commit();
				}
			} else {
				num = num + 1;
				if (2 == type) {
					// 更新
					StringBuilder updateSql = new StringBuilder();
					updateSql.append("UPDATE GL_BBLGY_SEQ SET NUM='").append(num).append("'")//
							.append(" WHERE PRE='").append(pre).append("'");
					conn.executeUpdate(updateSql.toString());
					conn.commit();
				}
			}
		} catch (Exception e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			throw new WTException("更新报表类工艺流水号失败！");
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return String.format("%0" + 3 + "d", num);
	}

	/**
	 * SOP资源获取流水号
	 *
	 * @param type
	 *            1:查询；2:更新
	 * @param pre
	 * @return
	 * @throws WTException
	 */
	public static String getSopZYSeqNumber(Integer type, String pre) throws WTException {
		StringBuilder sql = new StringBuilder("select NUM FROM ");
		sql.append("GL_BBLGY_SEQ ").append("WHERE PRE='").append(pre).append("'");
		DBConn conn = null;
		long num = 0;
		try {
			conn = new DBConn();
			conn.start();
			ResultSet rs = conn.executeQuery(sql.toString());
			if (rs.next()) {
				num = rs.getLong("NUM");
			}
			if (num == 0) {
				num = 000001;
				if (2 == type) {
					// 更新
					StringBuilder insertSql = new StringBuilder();
					insertSql.append("INSERT INTO GL_BBLGY_SEQ(PRE,NUM) ")//
							.append("VALUES('").append(pre).append("',")//
							.append("'").append(num).append("')");
					conn.executeUpdate(insertSql.toString());
					conn.commit();
				}
			} else {
				num = num + 1;
				if (2 == type) {
					// 更新
					StringBuilder updateSql = new StringBuilder();
					updateSql.append("UPDATE GL_BBLGY_SEQ SET NUM='").append(num).append("'")//
							.append(" WHERE PRE='").append(pre).append("'");
					conn.executeUpdate(updateSql.toString());
					conn.commit();
				}
			}
		} catch (Exception e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			throw new WTException("更新报表类工艺流水号失败！");
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return String.format("%0" + 6 + "d", num);
	}

	/**
	 * 获取工艺的xml
	 *
	 * @param document
	 * @return
	 * @throws Exception
	 */
	public static Element getTechnicsElement(WTDocument document) throws Exception {
		ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
		if (data == null) {
			return null;
		}
		String zipFilePath = PropertiesUtil.getTempPath() + File.separator + "sopTemp" + File.separator + document.getNumber();
		File techDir = new File(zipFilePath);
		if (!techDir.exists()) {
			techDir.mkdirs();
		}
		String xmlFile = zipFilePath + File.separator + document.getNumber() + ".xml";
		byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
		ZipUtil.unZip(bytes, zipFilePath);

		File file = new File(xmlFile);
		if (!file.exists()) {
			System.out.println(xmlFile + " is not exist!");
			return null;
		}
		SAXReader reader = new SAXReader();
		Document doc = reader.read(file);
		Element rootElement = doc.getRootElement();
		Element technicsElement = rootElement.element("QMFawTechnicsInfo");
		deleteFiles(new File(PropertiesUtil.getTempPath() + File.separator + "sopTemp" + File.separator + document.getNumber()));
		return technicsElement;
	}

	/**
	 * 删除文件
	 *
	 * @param dir
	 */
	public static void deleteFiles(File dir) {
		if (dir == null || !dir.exists() || !dir.isDirectory()) {
			return;
		}
		for (File file : dir.listFiles()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				deleteFiles(file);
			}
		}
		dir.delete();
	}

	/**
	 * 通过零部件查询ProcessTaskLink
	 *
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static boolean isExistProcessTask(WTPart part, String taskType) throws WTException {
		QuerySpec qSpec = new QuerySpec(ProcessTaskLink.class);
		int[] index = { 0 };
		long longId = PersistenceHelper.getObjectIdentifier(part).getId();
		SearchCondition sCondition = new SearchCondition(ProcessTaskLink.class, "roleAObjectRef.key.id", SearchCondition.EQUAL, longId);
		qSpec.appendWhere(sCondition, index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		while (qResult.hasMoreElements()) {
			ProcessTaskLink link = (ProcessTaskLink) qResult.nextElement();
			ProcessTask processTask = (ProcessTask) link.getRoleBObject();
			if (!ProcessConstants.TASK_STATE_YIZUOFEI.equals(processTask.getTaskState())) {
				if (processTask.getTaskType().equals(taskType)) {
					return true;
				}

			}
		}
		return false;
	}

	/**
	 * 方法功能: 获取部件下所有已批准的SOP工艺
	 *
	 * @param wtPart
	 * @return java.util.List<wt.doc.WTDocument>
	 * @author LB
	 * @date 2019/12/25
	 */
	public static List<WTDocument> getAllApprovedSopTechnics(WTPart wtPart) throws WTException {
		List<WTDocument> documentList = new ArrayList<WTDocument>();
		QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(wtPart, true);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr = lcs.process(qr);
		while (qr.hasMoreElements()) {
			Object o = qr.nextElement();
			if (o instanceof WTDocument) {
				WTDocument document = (WTDocument) o;
				String type = TypedUtilityServiceHelper.service.getTypeIdentifier(document).toString();
				String state = document.getState().toString();
				if ("APPROVED".equals(state) && type.contains(SopConstants.SOP_TYPE_SOPDOC)) {
					documentList.add(document);
				}
			}
		}
		return documentList;
	}

	public static String getEcnDeptNo() throws WTException {
		String number = "";
		Map<String, String> map = getBmdhMap();
		WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
		Enumeration groups = currentUser.parentGroups(false);
		while (groups.hasMoreElements()) {
			WTPrincipalReference principalRef = (WTPrincipalReference) groups.nextElement();
			WTGroup group = (WTGroup) principalRef.getPrincipal();
			String description = group.getDescription();
			if (!"".equals(description) && !"null".equals(description) && description != null) {
				if (map.containsKey(description)) {
					number = map.get(description);
					break;
				}
			}
		}
		return number;
	}

	private static Map<String, String> getBmdhMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("部门_一车间", "301");
		map.put("部门_二车间", "302");
		map.put("部门_三车间", "303");
		map.put("部门_四车间", "304");
		map.put("部门_五车间", "305");
		map.put("部门_六车间", "306");
		map.put("部门_七车间", "307");
		map.put("部门_八车间", "308");
		map.put("部门_九车间", "309");
		map.put("部门_十车间", "310");
		return map;
	}

	public static Element getYinYongSopsElement(WTDocument document) {
		byte[] bytes = null;
		if (document != null) {
			ApplicationData data;
			try {
				data = WTDocumentUtil.getPrimaryByDocument(document);
				bytes = WTDocumentUtil.applicationDataToByte(data);
				String fileName = data.getFileName();
				if (fileName.toLowerCase().endsWith(".zip")) {
					fileName = fileName.substring(0, fileName.length() - 4);
				}
				String filepath = PropertiesUtil.getTempPath() + File.separator + fileName;
				File dir = new File(filepath);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				ZipUtil.unZip(bytes, filepath);
				File xmlFile = new File(filepath + File.separator + fileName + ".xml");
				Document doc = XmlUtility.getDocument(xmlFile);
				Element rootElement = doc.getRootElement();
				Element technics = rootElement.element("QMFawTechnicsInfo");
				Element steps = technics.element("steps");
				Element procedure = steps.element("QMProcedureInfo");
				Element sops = procedure.element("sops");
				return sops;
			} catch (WTException e) {
				e.printStackTrace();
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static WTDocument getSopBySopNumber(String sopNumber) throws Exception{
		int index1[] = { 0 };
		QuerySpec qs = new QuerySpec(WTDocument.class);
		qs.setAdvancedQueryEnabled(true);
		ClassAttribute caId = new ClassAttribute(WTDocument.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
		TypeUtil.getTypeQuery(WTDocument.class,SopConstants.SOP_TYPE_SOPDOC, qs);
		qs.appendAnd();
		qs.appendOpenParen();
		AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("SopNumber");
		if (addv == null)
			throw new IBADefinitionException("No IBA Definition: " + "SopNumber");
		long ibaDefId = addv.getObjectID().getId();
		QuerySpec qs1 = new QuerySpec();
		int idx = qs1.appendClassList(StringValue.class, false);
		qs1.appendSelect(new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), new int[] { idx }, false);
		qs1.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", SearchCondition.EQUAL, ibaDefId), new int[] { idx });
		qs1.appendAnd();
		qs1.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.EQUAL, sopNumber, true), new int[] { idx });
		SubSelectExpression stringIBAQuery = new SubSelectExpression(qs1);
		qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, stringIBAQuery), index1);
		qs.appendCloseParen();
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.hasMoreElements()) {
			WTDocument pt = (WTDocument) qr.nextElement();
			return pt;
		}
		return null;
	}

	/**
	 * 从jar中copy文件
	 *
	 * @param sourceFolder
	 */
	public static void copyFromJar(File sourceFolder) {
		try {
			String sourceFolderPath = sourceFolder.getAbsolutePath()
					+ File.separator;
			//String path = ProcessInfoReleaseController.class.getResource("/com/glaway/mpm/release/templetes").getPath();
			String path = ProcessInfoReleaseController.class.getResource(ProcessInfoReleaseController.class.getSimpleName() + ".class").getFile();
			System.out.println(">>>>>>>>>>>>>path:" + path);
			path = "jar:" + path.substring(0, path.indexOf("!") + 2);
			URL url = new URL(path);
			JarURLConnection con = (JarURLConnection) url.openConnection();
			JarFile jarFile = con.getJarFile();

			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String name = entry.getName();
				if (name.startsWith("com/glaway/mpm/release/templetes")) {
					name = name
							.replace("com/glaway/mpm/release/templetes/", "");
					if (!name.equals("")) {
						if (name.indexOf("/") == -1) {
							writeInputStreamToFile(
									jarFile.getInputStream(entry),
									sourceFolderPath + name);
						} else {
							int i = name.lastIndexOf("/");
							String tempPath = name.substring(0, i);
							String fileName = name.substring(i + 1);
							if (!fileName.equals("")) {
								File f = createDir(sourceFolderPath + tempPath);
								writeInputStreamToFile(
										jarFile.getInputStream(entry),
										f.getAbsolutePath() + File.separator
												+ fileName);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 创建目录
	 *
	 * @param path
	 * @return
	 */
	private static File createDir(String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir;
	}
	/**
	 * 写文件
	 *
	 * @param is
	 * @param destPath
	 */
	public static void writeInputStreamToFile(InputStream is, String destPath) {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(new FileOutputStream(destPath));
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = bis.read(b)) != -1) {
				bos.write(b, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
				if (bos != null) {
					bos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 深层创建目录
	 *
	 * @param path
	 * @return
	 */
	public static File createDirs(String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	/**
	 * 导入SOP体系BOM处理流水号
	 *
	 * @param num 导入的编号流水号
	 * @param pre
	 * @return
	 * @throws WTException
	 */
	public static void dealImpSopSeqNumber(Long impNum, String pre) throws WTException {
		StringBuilder sql = new StringBuilder("select NUM FROM ");
		sql.append("GL_BBLGY_SEQ ").append("WHERE PRE='").append(pre).append("'");
		DBConn conn = null;
		long num = 0;
		try {
			conn = new DBConn();
			conn.start();
			ResultSet rs = conn.executeQuery(sql.toString());
			if (rs.next()) {
				num = rs.getLong("NUM");
			}
			if (num == 0) {
				StringBuilder insertSql = new StringBuilder();
				insertSql.append("INSERT INTO GL_BBLGY_SEQ(PRE,NUM) ")
						.append("VALUES('").append(pre).append("',")
						.append("'").append(impNum).append("')");
				conn.executeUpdate(insertSql.toString());
				conn.commit();
			} else {
				if (impNum >= num) {
					StringBuilder updateSql = new StringBuilder();
					updateSql.append("UPDATE GL_BBLGY_SEQ SET NUM='").append(impNum).append("'")
							.append(" WHERE PRE='").append(pre).append("'");
					conn.executeUpdate(updateSql.toString());
					conn.commit();
				}
			}
		} catch (Exception e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			throw new WTException("处理导入SOP体系BOM流水号失败！");
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static SopBean buildSopBeanByNumber(String number) throws Exception {
		WTDocument wtDocument = null;
		QuerySpec qSpec = new QuerySpec(WTDocument.class);
		qSpec.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL,
				number), index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		qResult = new LatestConfigSpec().process(qResult);
		while (qResult.hasMoreElements()) {
			wtDocument = (WTDocument) qResult.nextElement();
		}
		SopBean sopBean = new SopBean();
		if(wtDocument!=null){
			sopBean.setOid(String.valueOf(wtDocument.getPersistInfo().getObjectIdentifier().getId()));
			sopBean.setNumber(wtDocument.getNumber());
			sopBean.setName(wtDocument.getName());
			sopBean.setPpnumber(IBAHelper.getIBAValue(wtDocument, SopConstants.SOP_IBA_SOPNUMBER));
			sopBean.setSpecializedType(IBAHelper.getIBAValue(wtDocument, SopConstants.SOP_IBA_SPECIALIZEDTYPE));
			sopBean.setProceduceName(IBAHelper.getIBAValue(wtDocument, SopConstants.SOP_IBA_PROCEDUCENAME));
			sopBean.setOperationJob(IBAHelper.getIBAValue(wtDocument, SopConstants.SOP_IBA_OPERATIONJOB));
			sopBean.setCustomArea(IBAHelper.getIBAValue(wtDocument, SopConstants.SOP_IBA_CUSTOMAREA));
			sopBean.setParameters(IBAHelper.getIBAValue(wtDocument, SopConstants.SOP_IBA_PARAMETERS));
			sopBean.setSecret(IBAHelper.getIBAValue(wtDocument, SopConstants.SOP_IBA_SECRET));
			String version = wtDocument.getVersionIdentifier().getValue() + "." + wtDocument.getIterationIdentifier().getValue();
			sopBean.setVersion(version);
			sopBean.setProfessionalCod(IBAHelper.getIBAValue(wtDocument, SopConstants.SOP_IBA_PROFESSIONALCODE));
			sopBean.setGongXuJianHao(IBAHelper.getIBAValue(wtDocument, SopConstants.SOP_IBA_GONGXUJIANHAO));
			sopBean.setDescription(wtDocument.getDescription());
			sopBean.setDepartment(IBAHelper.getIBAValue(wtDocument, SopConstants.SOP_IBA_DEPARTMENT));
			List<ParametersBean> parametersBeanList = getRelatedParameters(wtDocument.getNumber(), wtDocument.getVersionIdentifier().getValue());
			sopBean.setArameters(parametersBeanList);
		}
		return sopBean;
	}
}
