package ext.casc.dfmRule.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMasterIdentity;
import wt.enterprise.Master;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.fc.Identified;
import wt.fc.IdentityHelper;
import wt.fc.ObjectIdentifier;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.folder.CabinetBased;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.SubFolder;
import wt.httpgw.GatewayAuthenticator;
import wt.httpgw.GatewayServletHelper;
import wt.httpgw.URLFactory;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.State;
import wt.maturity.MaturityException;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.method.RemoteMethodServer;
import wt.org.OrganizationServicesHelper;
import wt.org.WTOrganization;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartStandardConfigSpec;
import wt.pom.PersistenceException;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.type.Typed;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.Iterated;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;
import wt.vc.views.ViewException;
import wt.vc.views.ViewHelper;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinition;

/**
 *
 * @author 杨方宁
 */
public class GeneralUtil {

	public static String CLASSNAME = GeneralUtil.class.getName();

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
	}

	/**
	 * 通过业务对象的oid得到对象
	 *
	 * @param oid
	 * @return
	 * @throws WTException
	 */
	public static WTObject getObjectByOid(String oid) {
		Object obj = null;
		if (oid == null || "".equals(oid)) {
			return null;
		}
		if(oid.indexOf("%3A") > -1) {
			oid = oid.replaceAll("%3A", ":");
		}
		ReferenceFactory renferenceFactory = new ReferenceFactory();
		WTReference wtReference;
		try {
			wtReference = renferenceFactory.getReference(oid);
			obj = wtReference.getObject();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return (WTObject) obj;
	}

	/**
	 * 产品是一个容器，这里通过一个产品的名称获取容器，产品的名称在windchill中具有唯一性
	 *
	 * @param containerName
	 *            产品的名称
	 * @return
	 */
	public static WTContainer getContainerByName(String containerName) {

		try {
			// 此处的逻辑部分可以参考:"windchill开发简单入门之条件查询"这篇文章
			QuerySpec qs = new QuerySpec(WTContainer.class);
			SearchCondition sc = new SearchCondition(WTContainer.class, WTContainer.NAME, "=", containerName);
			qs.appendWhere(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				WTContainer container = (WTContainer) qr.nextElement();
				return container;
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据用户名、全名查找WTUser对象
	 *
	 * @param name
	 *            可以为用户名 也可以为 全名
	 * @return
	 * @throws WTException
	 */
	public static WTUser getUserFromName(String name) throws WTException {
		WTUser user = null;
		if ("".equals(name) || null == name) {
			System.out.println("用户名不能为null");
			return null;
		}
		Enumeration enumUser = OrganizationServicesHelper.manager.findUser(WTUser.NAME, name);

		if (enumUser.hasMoreElements())
			user = (WTUser) enumUser.nextElement();
		if (user == null) {
			enumUser = OrganizationServicesHelper.manager.findUser(WTUser.FULL_NAME, name);
			if (enumUser.hasMoreElements())
				user = (WTUser) enumUser.nextElement();
		}
		if (user == null) {
			System.out.println("系统中不存在用户名为'" + name + "'的用户！");
		}
		return user;
	}

	/**
	 * 检出对象
	 *
	 * @param workable
	 * @return
	 * @throws WTPropertyVetoException
	 */
	public static boolean checkOutObject(Workable workable) throws WTPropertyVetoException {
		try {
			// if(!wt.vc.wip.WorkInProgressHelper.isCheckedOut(workable,
			// wt.session.SessionHelper.getPrincipal())))
			if (!wt.vc.wip.WorkInProgressHelper.isCheckedOut(workable, wt.session.SessionHelper.manager.getPrincipal())) {
				if (!FolderHelper.inPersonalCabinet((CabinetBased) workable) && !WorkInProgressHelper.isWorkingCopy(workable)) {
					Folder folder = WorkInProgressHelper.service.getCheckoutFolder();
					CheckoutLink checkoutLink = WorkInProgressHelper.service.checkout(workable, folder, "");
					if (checkoutLink != null) {
						checkoutLink.getWorkingCopy();
						return true;
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 *
	 * @param obj
	 * @return
	 */
	public static boolean isCheckOut(WTObject obj) {
		try {
			if(WorkInProgressHelper.isWorkingCopy((Workable) obj) || WorkInProgressHelper.isCheckedOut((Workable) obj)) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * 检入对象
	 *
	 * @param workable
	 * @return
	 */
	public static boolean checkInObject(Workable workable) {
		// if(wt.vc.wip.WorkInProgressHelper.isCheckedOut(workable))
		try {
			if (wt.vc.wip.WorkInProgressHelper.isCheckedOut(workable, wt.session.SessionHelper.getPrincipal())) {
				try {
					WorkInProgressHelper.service.checkin(workable, "AAutomatically Check In");
					return true;
				} catch (WTPropertyVetoException e) {
					e.printStackTrace();
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 找到对象的Oid
	 *
	 * @param persistable
	 * @return
	 */
	public static String getOidByPersistable(Persistable persistable) {
		try {
			ReferenceFactory referencefactory = new ReferenceFactory();
			return referencefactory.getReferenceString(persistable);
		} catch (WTException wte) {
			wte.printStackTrace();
		}
		return null;
	}

	/**
	 * 得到文件夹
	 *
	 * @param folderPath
	 * @param wtcontainer
	 * @param isCreate
	 * @return
	 * @throws Exception
	 */
	public static String getFolderRef(String folderPath, WTContainer wtcontainer, boolean isCreate) throws Exception {
		if (folderPath == null || folderPath.equals("")) {
			return "";
		}
		Folder subfolder = null;
		String folderRef = "";

		if (!folderPath.startsWith("/")) {
			folderPath = "/" + folderPath;
		}

		if (!folderPath.equalsIgnoreCase("/Default") && !folderPath.startsWith("/Default")) {
			folderPath = "/Default" + folderPath;
		}

		String nextfolder[] = folderPath.split("/");
		// System.out.println("nextfolder[]==="+nextfolder.length);
		ArrayList list = new ArrayList();
		for (int p = 0; p < nextfolder.length; p++) {
			if (nextfolder[p] != null && !nextfolder[p].trim().equals("") && !nextfolder[p].trim().equals("Default")) {
				list.add(nextfolder[p]);
			}
		}

		if (isCreate) {
			createMultiLevelDirectory(list, WTContainerRef.newWTContainerRef(wtcontainer));
		}

		subfolder = FolderHelper.service.getFolder(folderPath, WTContainerRef.newWTContainerRef(wtcontainer));
		if (subfolder == null) {
			folderPath = "/Default";
			subfolder = FolderHelper.service.getFolder(folderPath, WTContainerRef.newWTContainerRef(wtcontainer));
		} else {
			ReferenceFactory rf = new ReferenceFactory();
			folderRef = rf.getReferenceString(ObjectReference.newObjectReference(((Persistable) subfolder).getPersistInfo().getObjectIdentifier()));
		}
		return folderRef;
	}

	public static SubFolder createMultiLevelDirectory(List<String> list, WTContainerRef wtContainerRef) {
		SubFolder subFolder = null;
		String path = ((WTContainer) wtContainerRef.getObject()).getDefaultCabinet().getFolderPath();
		int size = list.size();
		for (int i = 0; i < size; i++) {
			Folder folder = null;
			try {
				folder = FolderHelper.service.getFolder(path, wtContainerRef);
				path = path + "/" + list.get(i);
				QueryResult result = FolderHelper.service.findSubFolders(folder);
				if (!checkFolderExits(result, list.get(i)))
					subFolder = FolderHelper.service.createSubFolder(path, wtContainerRef);
			} catch (WTException e) {
				e.printStackTrace();
			}
		}

		if (subFolder == null) {
			try {
				Folder folder = FolderHelper.service.getFolder(path, wtContainerRef);
				subFolder = (SubFolder) folder;
				// System.out.println(">>>>SubFolder'Name is:" +
				// subFolder.getName());
			} catch (WTException e) {
				e.printStackTrace();
			}
		}
		return subFolder;
	}

	/**
	 * 判断文件夹在系统中是否存在
	 *
	 * @param result
	 * @param str
	 * @return
	 */
	public static boolean checkFolderExits(QueryResult result, String str) {
		if (result == null)
			return false;
		while (result.hasMoreElements()) {
			Object obj = result.nextElement();
			if (obj instanceof SubFolder) {
				SubFolder subFolder = (SubFolder) obj;
				if (subFolder.getName().equals(str))
					return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * 通过传入的参数得到具体的升级对象
	 *
	 * @description
	 * @date 2010-12-14 上午11:09:49
	 * @param promotionnotice
	 * @return
	 * @throws WTException
	 */
	public static QueryResult getPromotionTargets(PromotionNotice promotionnotice) throws WTException {
		QueryResult qr = null;
		try {
			qr = MaturityHelper.service.getPromotionTargets(promotionnotice);
		} catch (MaturityException e) {
			e.printStackTrace();
			throw new MaturityException(e.getLocalizedMessage());
		} catch (WTException e) {
			e.printStackTrace();
			throw new WTException(e.getLocalizedMessage());
		}
		return qr;
	}

	/**
	 * get lastest version
	 *
	 * @param master
	 * @return
	 * @throws WTException
	 */
	public static RevisionControlled getLatestObject(Object object) {
		if (object instanceof Versioned) {
			QueryResult queryResult;
			try {
				queryResult = VersionControlHelper.service.allVersionsOf((Versioned) object);
				if (queryResult.hasMoreElements()) {
					return (RevisionControlled) queryResult.nextElement();
				}
			} catch (PersistenceException e) {
				e.printStackTrace();
			} catch (WTException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Iterated searchLatestWTPartByNumberView(String number, String viewname) {
		boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			QuerySpec qs = new QuerySpec(WTPart.class);
			qs.appendWhere(new SearchCondition(WTPart.class, "master>number", "=", number), new int[1]);
			if ((viewname != null) && (!viewname.equals(""))) {
				qs.appendAnd();
				View view = ViewHelper.service.getView(viewname);
				qs.appendWhere(new SearchCondition(WTPart.class, "view.key.id", "=", view.getPersistInfo().getObjectIdentifier().getId()), new int[1]);
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

		return null;
	}

	/**
	 * 更改Resource英文为中文
	 *
	 * @param s
	 *            需要改的字符串
	 * @param resource
	 *            resource对象，保护路径
	 * @return
	 * @throws WTException
	 */
	public static String replaceRB(String s, String resource) throws WTException {
		String str = "";
		StringTokenizer tokenizer = new StringTokenizer(s, "/");
		int i = 0;
		while (tokenizer.hasMoreElements()) {
			String token = tokenizer.nextToken();
			i++;
			// System.out.println(" "+ token);
			try {
				Class class1 = Class.forName(resource);
				Object obj = class1.getField(token).get(null);
				// ResourceBundle resourcebundle =
				// ResourceBundle.getBundle(resource,
				// SessionHelper.getLocale());
				ResourceBundle resourcebundle = ResourceBundle.getBundle(resource, Locale.SIMPLIFIED_CHINESE);
				String s1 = i > 1 ? "/" : "";
				str = str + s1 + resourcebundle.getString((String) obj);
			} catch (ClassNotFoundException classnotfoundexception) {
				classnotfoundexception.printStackTrace();
			} catch (NoSuchFieldException nosuchfieldexception) {
				nosuchfieldexception.printStackTrace();
				throw new WTException(nosuchfieldexception);
			} catch (IllegalAccessException illegalaccessexception) {
				illegalaccessexception.printStackTrace();
				throw new WTException(illegalaccessexception);
			}
		}
		return str;
	}

	/**
	 * 更改升级对象状态为正在审阅
	 *
	 * @param pn
	 * @param state
	 *            例如 UNDERREVIEW
	 * @throws WTException
	 */
	public static void setDocStateOfPN(PromotionNotice pn, String state) throws WTException {
		QueryResult qr = getPromotionTargets(pn);
		while (qr.size() > 0 && qr.hasMoreElements()) {
			Object object = qr.nextElement();
			if (object instanceof WTDocument) {
				WTDocument doc = (WTDocument) object;
				LifeCycleHelper.service.setLifeCycleState(doc, State.toState(state));
			}
			if (object instanceof WTPart) {
				WTPart part = (WTPart) object;
				LifeCycleHelper.service.setLifeCycleState(part, State.toState(state));
			}
			if (object instanceof EPMDocument) {
				EPMDocument epmdoc = (EPMDocument) object;
				LifeCycleHelper.service.setLifeCycleState(epmdoc, State.toState(state));
			}
		}
	}

	/**
	 * 更改升级对象状态为正在审阅
	 *
	 * @param pn
	 * @param state
	 *            例如 UNDERREVIEW
	 * @throws WTException
	 */
	public static void setState4PN(PromotionNotice pn, String state) throws WTException {
		QueryResult qr = getPromotionTargets(pn);
		while (qr.size() > 0 && qr.hasMoreElements()) {
			Object object = qr.nextElement();
			if (object instanceof WTDocument) {
				WTDocument doc = (WTDocument) object;
				LifeCycleHelper.service.setLifeCycleState(doc, State.toState(state));
			}
			if (object instanceof WTPart) {
				WTPart part = (WTPart) object;
				LifeCycleHelper.service.setLifeCycleState(part, State.toState(state));
			}
		}
	}

	/**
	 * 设置对象的类型
	 *
	 * @param typed
	 * @param s
	 * @throws WTException
	 */
	public static void setType(Typed typed, String s) throws WTException {
		try {
			String s1 = typed.getClass().getName();
			s1 = s;
			TypedUtility.initTypeDefinitions();
			TypeDefinitionReference typedefinitionreference = TypedUtility.getTypeDefinitionReference(s1);
			if (typedefinitionreference == null) {
				typedefinitionreference = TypeDefinitionReference.newTypeDefinitionReference();
			}
			typed.setTypeDefinitionReference(typedefinitionreference);
		} catch (WTPropertyVetoException wtpropertyvetoexception) {
			wtpropertyvetoexception.printStackTrace();
			throw new WTException(wtpropertyvetoexception);
		}
	}

	/**
	 * get 当前用户
	 *
	 * @return
	 * @throws WTException
	 */
	public static WTPrincipal getCurrentUser() throws WTException {
		WTPrincipal p = SessionHelper.getPrincipal();
		return p;
	}

	/**
	 * HTTP访问鉴权,一般使用wcadmin
	 *
	 * @param username
	 */
	public static void loginByHttp(String username) {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		GatewayAuthenticator auth = new GatewayAuthenticator();
		auth.setRemoteUser(username);
		rms.setAuthenticator(auth);
	}

	/**
	 * 远程访问鉴权
	 */
	public static void loginByRemote(String username, String password) {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		rms.setUserName(username);
		rms.setPassword(password);
	}

	/**
	 * 授予Admin权限
	 */
	public static WTPrincipal giveAdminAuthority() {
		WTPrincipal administrator = null;
		WTPrincipal previous = null;
		try {
			administrator = SessionHelper.manager.getAdministrator();
			previous = SessionContext.setEffectivePrincipal(administrator);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return previous;
	}

	/**
	 * 返回普通权限
	 */
	public static void giveAdminAuthorityBack(WTPrincipal previous) {
		SessionContext.setEffectivePrincipal(previous);
	}

	/**
	 * 根据某个Sequence得到流水码 例如"WTDOCUMENTID_SEQ"
	 *
	 * @param sequence
	 * @return
	 */
	public static String getSequenceNumber(String sequence) {
		String strNumber = null;
		try {
			strNumber = PersistenceHelper.manager.getNextSequence(sequence);
		} catch (WTException e) {
			e.printStackTrace();
		}
		NumberFormat nf = NumberFormat.getInstance();
		int temp_part_digit = 10;
		nf.setMinimumIntegerDigits(temp_part_digit);
		nf.setMaximumIntegerDigits(temp_part_digit);
		nf.setGroupingUsed(false);
		if (null != strNumber) {
			strNumber = nf.format(Integer.parseInt(strNumber.trim()));
		}
		return strNumber;
	}

	/**
	 * 根据properties路径得到Properties对象
	 *
	 * @param relativePath
	 * @return
	 */
	public static Properties getProperties(String relativePath) {
		Properties properties = new Properties();
		try {
			WTProperties wtproperties = WTProperties.getLocalProperties();
			String path = wtproperties.getProperty("wt.home") + relativePath;
			properties.load(new FileInputStream(new File(path)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}

	public static Boolean isSiteOrOrgAdmin() throws WTException {
		if (!RemoteMethodServer.ServerFlag) {
			String method = "isSiteOrOrgAdmin";
			try {
				return (Boolean) RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, null, null);
			} catch (Exception e) {
				e.printStackTrace();
				return Boolean.FALSE;
			}
		}
		WTPrincipal principal = SessionHelper.getPrincipal();
		boolean check = false;
		WTContainerRef exchangeRef = WTContainerHelper.service.getExchangeRef();
		check = WTContainerHelper.service.isAdministrator(exchangeRef, principal);
		if (check)
			return Boolean.TRUE;
		WTOrganization org = OrganizationServicesHelper.manager.getOrganization(principal);
		if (org == null)
			return Boolean.FALSE;
		WTContainerRef orgContainerRef = WTContainerHelper.service.getOrgContainerRef(org);
		check = WTContainerHelper.service.isAdministrator(orgContainerRef, principal);
		return Boolean.valueOf(check);
	}

	// 判断是不是最新版本的对象
	public static boolean isLatestObject(Versioned object) {

		if (VersionControlHelper.isLatestIteration((Iterated) object)) {
			return true;
		}
		return false;
	}

	/**
	 * 根据生命周期的名称查找相应的生命周期的Oid
	 *
	 * @param name
	 *            生命周期的名称
	 * @return
	 * @throws WTException
	 */
	public static String findLifeCyleTemplateByName(String name) throws WTException {
		String result = null;

		QuerySpec qs = new QuerySpec(LifeCycleTemplate.class);
		SearchCondition sc = new SearchCondition(LifeCycleTemplate.class, LifeCycleTemplate.NAME, SearchCondition.EQUAL, name);
		qs.appendSearchCondition(sc);

		qs.appendAnd();
		sc = new SearchCondition(LifeCycleTemplate.class, LifeCycleTemplate.LATEST_ITERATION, SearchCondition.IS_TRUE);
		qs.appendSearchCondition(sc);

		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.hasMoreElements()) {
			LifeCycleTemplate lt = (LifeCycleTemplate) qr.nextElement();

			ReferenceFactory renferenceFactory = new ReferenceFactory();
			result = renferenceFactory.getReferenceString(lt);
		}

		return result;
	}

	public static String numberOrStr(String str) {
		// 比如输入为String s，s.matches("[0-9a-zA-Z]*")返回true，就可以了～
		String result = "";
		if (null != str && !"".equals(str)) {
			if (str.matches("[0-9]*"))
				return "0";
			else if (str.matches("[a-zA-Z]*"))
				return "1";
			else
				return "-1";
		}

		return result;
	}

	/**
	 * 得到某容器下面所有此类型的特定对象
	 *
	 * @param type
	 *            类型
	 * @param container
	 *            容器
	 * @param typeClass
	 * @return
	 */
	public static <T> QueryResult GetAllTheObjInContainer(String type, WTContainer container, Class<T> typeClass) {
		QueryResult qr = new QueryResult();
		try {
			QuerySpec qs = new QuerySpec();
			int partIndex = qs.appendClassList(typeClass, true);
			int wttypeIndex = qs.appendClassList(WTTypeDefinition.class, false);
			int containerIndex = qs.appendClassList(container.getClass(), false);
			SearchCondition sc = new SearchCondition(typeClass, "typeDefinitionReference.key.id", WTTypeDefinition.class, "thePersistInfo.theObjectIdentifier.id");
			qs.appendWhere(sc, new int[] { partIndex, wttypeIndex });
			qs.appendAnd();
			sc = new SearchCondition(WTTypeDefinition.class, "logicalIdentifier", SearchCondition.EQUAL, type);
			qs.appendWhere(sc, new int[] { wttypeIndex });
			qs.appendAnd();
			String idStr = container.getIdentity();
			idStr = idStr.substring(idStr.lastIndexOf(':') + 1);
			sc = new SearchCondition(typeClass, "containerReference.key.id", container.getClass(), "thePersistInfo.theObjectIdentifier.id");
			qs.appendWhere(sc, new int[] { partIndex, containerIndex });
			qs.appendAnd();
			sc = new SearchCondition(container.getClass(), "thePersistInfo.theObjectIdentifier.id", SearchCondition.EQUAL, Long.valueOf(idStr));
			qs.appendWhere(sc, new int[] { containerIndex });
			qs.appendAnd();
			sc = new SearchCondition(typeClass, "checkoutInfo.state", SearchCondition.NOT_EQUAL, "wrk");
			qs.appendWhere(sc, new int[] { partIndex });
			qs.appendAnd();
			sc = new SearchCondition(typeClass, WTDocument.LATEST_ITERATION, SearchCondition.IS_TRUE);
			qs.appendWhere(sc, new int[] { partIndex });
			qs = new LatestConfigSpec().appendSearchCriteria(qs);
			qr = PersistenceHelper.manager.find(qs);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return qr;
	}

	/**
	 * 转为 VR:wt.doc.WTDocument:701445
	 *
	 * @param obid
	 *            浏览器上面类似于 VR%3Awt.doc.WTDocument%3A701445
	 * @return
	 */
	public static String convertObidToOid(String obid) {
		String oid = "";
		oid += obid.substring(0, obid.indexOf(":") + 1);
		obid = obid.substring(obid.indexOf(":") + 1);
		oid += obid.substring(0, obid.indexOf(":") + 1);
		obid = obid.substring(obid.indexOf(":") + 1);
		oid += obid.substring(0, obid.indexOf(":"));
		return oid;
	}

	private static void getVersion(WTPart part) {
		// 版本
		System.out.println(part.getVersionIdentifier().getValue());
		// 版序
		System.out.println(part.getIterationIdentifier().getValue());
	}

	public static synchronized <T extends Master> void setTObjectNumber(T master, String number) throws WTException {
		boolean access = SessionServerHelper.manager.setAccessEnforced(false);
		String user = SessionHelper.manager.getPrincipal().getName();
		Transaction transaction = null;
		try {
			transaction = new Transaction();
			transaction.start();

			SessionHelper.manager.setAdministrator();

			Identified identified = (Identified) master;
			WTDocumentMasterIdentity masteridentity = (WTDocumentMasterIdentity) identified.getIdentificationObject();
			// 必须对masteridentity设置number，不能直接对WTDocument对象设置number
			masteridentity.setNumber(number);
			identified = IdentityHelper.service.changeIdentity(identified, masteridentity);
			SessionHelper.manager.setPrincipal(user);
			PersistenceServerHelper.manager.update(master);

			transaction.commit();
			transaction = null;
		} catch (WTException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		} finally {
			if (transaction != null)
				transaction.rollback();
			SessionHelper.manager.setPrincipal(user);
			SessionServerHelper.manager.setAccessEnforced(access);
		}
	}

	/**
	 * 查看对象类型
	 *
	 * @param wtdocument
	 * @return
	 */
	public static String getType(Object obj) {
		try {
			return ClientTypedUtility.getExternalTypeIdentifier(obj);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 得到产品容器下面所有的某类型
	 *
	 * @param subType
	 * @param container
	 * @param typeClass
	 * @return
	 */
	public static <T> QueryResult GetAllObjectInContainer(String subType, WTContainer container, Class<T> typeClass) {
		QueryResult qr = new QueryResult();
		try {
			QuerySpec qs = new QuerySpec();
			int partIndex = qs.appendClassList(typeClass, true);
			int wttypeIndex = qs.appendClassList(WTTypeDefinition.class, false);
			int containerIndex = qs.appendClassList(container.getClass(), false);
			SearchCondition sc = new SearchCondition(typeClass, "typeDefinitionReference.key.id", WTTypeDefinition.class, "thePersistInfo.theObjectIdentifier.id");
			qs.appendWhere(sc, new int[] { partIndex, wttypeIndex });
			qs.appendAnd();
			sc = new SearchCondition(WTTypeDefinition.class, "logicalIdentifier", SearchCondition.EQUAL, subType);
			qs.appendWhere(sc, new int[] { wttypeIndex });
			qs.appendAnd();
			String idStr = container.getIdentity();
			idStr = idStr.substring(idStr.lastIndexOf(':') + 1);
			sc = new SearchCondition(typeClass, "containerReference.key.id", container.getClass(), "thePersistInfo.theObjectIdentifier.id");
			qs.appendWhere(sc, new int[] { partIndex, containerIndex });
			qs.appendAnd();
			sc = new SearchCondition(container.getClass(), "thePersistInfo.theObjectIdentifier.id", SearchCondition.EQUAL, Long.valueOf(idStr));
			qs.appendWhere(sc, new int[] { containerIndex });
			qs.appendAnd();
			sc = new SearchCondition(typeClass, "checkoutInfo.state", SearchCondition.NOT_EQUAL, "wrk");
			qs.appendWhere(sc, new int[] { partIndex });
			qs.appendAnd();
			sc = new SearchCondition(typeClass, WTDocument.LATEST_ITERATION, SearchCondition.IS_TRUE);
			qs.appendWhere(sc, new int[] { partIndex });
			qs = new LatestConfigSpec().appendSearchCriteria(qs);
			qr = PersistenceHelper.manager.find(qs);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return qr;
	}

	/**
	 * 得到产品容器下面的全部的某种类型
	 *
	 * @param containerName
	 * @param typeClass
	 * @return
	 * @throws WTException
	 */
	public static <T> QueryResult findAllObjectOfWTContainer(String containerName, Class<T> typeClass) throws WTException {
		WTContainer container = getContainerByName(containerName);
		QuerySpec qs = new QuerySpec(typeClass);
		long containerId = PersistenceHelper.getObjectIdentifier(container).getId();
		qs.appendWhere(new SearchCondition(typeClass, WTContained.CONTAINER_REFERENCE + "." + ObjectReference.KEY + "." + ObjectIdentifier.ID, SearchCondition.EQUAL, containerId), new int[] { 0 });
		return PersistenceHelper.manager.find(qs);
	}

	/**
	 * 得到对象的详细信息页面URL
	 *
	 * @param persistable
	 *            对象实例
	 * @return 详细信息页面URL
	 */
	public static String getObjectURL(Persistable persistable) {
		try {
			if (!RemoteMethodServer.ServerFlag) {
				return (String) RemoteMethodServer.getDefault().invoke("getObjectURL", GeneralUtil.class.getName(), null, new Class[] { Persistable.class }, new Object[] { persistable });
			} else {
				boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
				try {
					ReferenceFactory factory = new ReferenceFactory();
					String oid = "";
					URL url = null;
					oid = factory.getReferenceString(persistable);
					Properties properties1 = new Properties();
					properties1.put("oid", oid);
					properties1.put("action", "ObjProps");
					url = wt.httpgw.GatewayURL.getAuthenticatedGateway(null).getURL("wt.enterprise.URLProcessor", "URLTemplateAction", null, properties1);
					return url.toString();
				} finally {
					SessionServerHelper.manager.setAccessEnforced(flag);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getObjectURLOld(WTObject obj) {
		String result = "";
		try {
			if (obj instanceof WTDocument) {
				WTDocument doc = (WTDocument) obj;
				ReferenceFactory rf = new ReferenceFactory();
				String doid = rf.getReferenceString(ObjectReference.newObjectReference(doc));
				HashMap map = new HashMap();
				map.put("oid", doid);
				map.put("action", "ObjProps");
				URLFactory uf = new URLFactory();
				String urlInfo = GatewayServletHelper.buildAuthenticatedHREF(uf, "wt.enterprise.URLProcessor", "URLTemplateAction", map, true);
				urlInfo = "<a href='" + urlInfo + "'>" + doc.getIdentity() + "</a><br>\n";
				result = result + urlInfo;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 得到对象的文件夹路径 如果是检出状态，则得到对象的原始版本
	 *
	 * @param part
	 * @return
	 */
	public static String getFoldPath(WTPart part) {
		String result = "";
		try {
			if (!WorkInProgressHelper.isCheckedOut(part)) {
				result = part.getLocation();
			} else {
				WTPart part2 = (WTPart) WorkInProgressHelper.service.originalCopyOf(part);
				result = part2.getLocation();
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static <T> QueryResult getAllObject(Class<T> typeClass) {
		QuerySpec qs;
		try {
			qs = new QuerySpec(typeClass);
			qs.appendWhere(new SearchCondition(typeClass, WTContained.CONTAINER_REFERENCE + "." + ObjectReference.KEY + "." + ObjectIdentifier.ID, SearchCondition.NOT_NULL, 0L), new int[] { 0 });
			return PersistenceHelper.manager.find(qs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public static String getName(Object obj) {
		String name = null;
		if (obj != null)
			try {
				Class class1 = obj.getClass();
				Method method = class1.getMethod("getName", new Class[0]);
				name = (String) method.invoke(obj, new Object[0]);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		return name;
	}

	public static String getNumber(Object obj) {
		String number = null;
		if (obj != null)
			try {
				Class class1 = obj.getClass();
				Method method = class1.getMethod("getNumber", new Class[0]);
				number = (String) method.invoke(obj, new Object[0]);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		return number;
	}

	public static String getVersion(Object obj) {
		if ((obj instanceof Versioned)) {
			return ((Versioned) obj).getVersionIdentifier().getValue();
		}

		return null;
	}

	public static String getIteration(Object obj) {
		if ((obj instanceof Iterated)) {
			return ((Iterated) obj).getIterationIdentifier().getValue();
		}

		return null;
	}

	public static String getVersionIterationDisplay(Object obj) {
		if (obj == null)
			return "null";
		String version = getVersion(obj);
		if (version == null)
			version = "";
		String iteration = getIteration(obj);
		if (iteration == null)
			iteration = "";
		StringBuilder sb = new StringBuilder();
		sb.append(version);
		if (sb.length() != 0)
			sb.append(".");
		sb.append(iteration);
		return sb.toString();
	}


	/**
	 * 根据part得到最新的视图part
	 * @param part
	 * @return
	 * @throws ViewException
	 * @throws WTException
	 */
	public static WTPart getLatestByView(WTPart part) throws ViewException, WTException {
		WTPartStandardConfigSpec standardConfigSpec = WTPartStandardConfigSpec.newWTPartStandardConfigSpec(ViewHelper.service.getView("Design"), null);
		WTPartConfigSpec partConfiSpec = WTPartConfigSpec.newWTPartConfigSpec(standardConfigSpec);
		QueryResult qrVersions = VersionControlHelper.service.allVersionsOf(part);
		qrVersions = partConfiSpec.process(qrVersions);
		return part;
	}
}
