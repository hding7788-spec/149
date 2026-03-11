package com.glaway.mpm.util;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Pattern;

import wt.change2.ChangeIssue;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.doc.WTDocument;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.folder.Folder;
import wt.iba.definition.AttributeDefinition;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.IBAHolder;
import wt.iba.value.litevalue.AbstractValueView;
import wt.iba.value.litevalue.FloatValueDefaultView;
import wt.iba.value.litevalue.IntegerValueDefaultView;
import wt.iba.value.litevalue.StringValueDefaultView;
import wt.iba.value.service.IBAValueHelper;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.inf.library.WTLibrary;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.lifecycle.LifeCycleException;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.org.DirectoryContextProvider;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.pom.PersistenceException;
import wt.project.Role;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.series.MultilevelSeries;
import wt.series.Series;
import wt.series.SeriesException;
import wt.session.SessionHelper;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.Iterated;
import wt.vc.IterationIdentifier;
import wt.vc.Mastered;
import wt.vc.VersionControlException;
import wt.vc.VersionControlHelper;
import wt.vc.VersionControlServerHelper;
import wt.vc.VersionIdentifier;
import wt.vc.Versioned;
import wt.vc.config.ConfigHelper;
import wt.vc.config.ConfigSpec;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;
import wt.workflow.engine.WfProcess;

import com.glaway.mpm.config.ConfigPath;
import com.glaway.mpm.mpmresource.Constants;
import com.ptc.windchill.mpml.resource.MPMProcessMaterial;
import com.ptc.windchill.mpml.resource.MPMTooling;

public class Util {
	private static final String CLASSNAME = Util.class.getName();
	private static int index[] = { 0 };

	/**
	 * 格式化整数
	 *
	 * @author qianlong
	 * @date 2013-5-24
	 *
	 */
	public static String formateInteger(String str) {
		try {
			str = Integer.valueOf(str) + "";
		} catch (Exception e) {
			str = 0 + "";
		}
		return str;
	}

	/**
	 * 格式化字符窜
	 *
	 * @author qianlong
	 * @date 2013-5-28
	 * @return
	 *
	 */
	public static String formateString(String str) {
		if (null == str || "null".equals(str.toLowerCase())) {
			str = "";
		}
		return str;
	}

	/**
	 *格式化布尔值
	 *
	 * @author qianlong
	 * @date 2013-5-28
	 * @param str
	 * @return
	 *
	 */
	public static String formateBoolean(String str) {
		try {
			str = Boolean.valueOf(str) + "";
		} catch (Exception e) {
			str = false + "";
		}
		return str;
	}

	/**
	 * 格式化模糊查询的字符串
	 *
	 * @author qianlong
	 * @date 2012-11-12
	 * @param str
	 * @return
	 *
	 */
	public static String formatSearchString(String str) {
		if (null != str) {
			str = str.trim();
			if ("".equals(str)) {
				str = "%";
			} else {
				str = "%" + str.replace("*", "%") + "%";
			}

		} else {
			str = "%";
		}
		return str;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-11-21
	 * @param objectClass
	 * @param oid
	 * @return
	 * @throws WTException
	 *
	 */
	@SuppressWarnings("unchecked")
	public static Object getObjectByOid(Class objectClass, String oid) throws WTException {
		GLLogger.debug(CLASSNAME, "--objectClass-" + objectClass + "-oid-" + oid);
		Object object = null;
		long objectId = 0;
		if (null == objectClass || null == oid || "".equals(oid.trim())) {
			return object;
		}
		try {
			objectId = Long.valueOf(oid);
		} catch (Exception e) {
			if (oid.contains(":")) {
				String[] str = oid.split("\\:");
				objectId = Long.valueOf(str[str.length - 1]);
			}
		}
		QuerySpec qs = new QuerySpec(objectClass);
		qs.appendWhere(new SearchCondition(objectClass, WTAttributeNameIfc.ID_NAME, SearchCondition.EQUAL, objectId),
				index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		if (qr.hasMoreElements())
			object = qr.nextElement();
		return object;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2013-6-8
	 * @param lifeCycleManaged
	 * @param lifecycleStr
	 * @throws WTInvalidParameterException
	 * @throws LifeCycleException
	 * @throws WTException
	 *
	 */
	public static LifeCycleManaged setLifecycle(LifeCycleManaged lifeCycleManaged, String lifecycleStr)
			throws WTInvalidParameterException, LifeCycleException, WTException {
		return (LifeCycleManaged) LifeCycleHelper.service.setLifeCycleState(lifeCycleManaged, State
				.toState(lifecycleStr));
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-11-28
	 * @param object
	 * @return
	 *
	 */
	public static String getStringOid(Persistable persistable) {
		return getLongOid(persistable) + "";
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-11-28
	 * @param object
	 * @return
	 *
	 */
	public static long getLongOid(Persistable persistable) {
		return persistable.getPersistInfo().getObjectIdentifier().getId();
	}

	/**
	 * 排序
	 *
	 * @author qianlong
	 * @date 2013-3-21
	 * @param list
	 *
	 */
	@SuppressWarnings("unchecked")
	public static void sort(List list) {
		Collections.sort(list);
	}

	/**
	 * 判断此属性是否是软属性
	 *
	 * @author lbzhang
	 * @date 2012-7-7
	 * @modify
	 * @param attri
	 *            如DocType(小类)
	 * @return
	 */
	public static boolean isSoftwareAttri(String attri) {
		boolean flag = true;
		AttributeDefinition ad = getAttributeDefinition(attri);
		if (ad == null) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 通过软属性内部名称,获取软属性对象
	 *
	 * @author lbzhang
	 * @date 2012-7-7
	 * @modify
	 * @param softAttributeName
	 *            如DocType(小类)
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static AttributeDefinition getAttributeDefinition(String softAttributeName) {
		AttributeDefinition softAttributeDefinition = null;
		// to be continue;
		if (softAttributeName == null || softAttributeName.length() == 0)
			return null;
		try {
			QuerySpec queryStringDefinition = new QuerySpec(AttributeDefinition.class);
			SearchCondition sc = new SearchCondition(AttributeDefinition.class, "name", SearchCondition.EQUAL,
					softAttributeName.trim());
			queryStringDefinition.appendWhere(sc);
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) queryStringDefinition);

			if (qr.size() == 0) {
				return null;
			} else {
				return (AttributeDefinition) qr.nextElement();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return softAttributeDefinition;
	}

	@SuppressWarnings("deprecation")
	public static WTPart getPartByNumber(String number) {
		WTPart part = null;

		try {
			QuerySpec qs = new QuerySpec(WTPart.class);
			qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, "=", number));
			qs.appendAnd();
			qs.appendSearchCondition(VersionControlHelper.getSearchCondition(WTPart.class, true));
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			if (qr.hasMoreElements()) {
				part = (WTPart) qr.nextElement();
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return part;
	}

	public static final WTPart getLatestIterationPart(WTPart part) {
		try {
			QueryResult qr = VersionControlHelper.service.allIterationsOf(part.getMaster());
			if (qr.hasMoreElements()) {
				part = (WTPart) qr.nextElement();
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return part;
	}

	@SuppressWarnings("finally")
	public static final Workable checkOut(Workable workable, String note) {
		try {
			// if(WorkInProgressHelper.isCheckedOut(workable)){
			// return workable;
			// }
			Folder folder = WorkInProgressHelper.service.getCheckoutFolder();
			if (WorkInProgressHelper.service.isCheckoutAllowed(workable)) {
				GLLogger.debug("the object is allowed checkout!");
				WorkInProgressHelper.service.checkout(workable, folder, note);
				workable = (Workable) WorkInProgressHelper.service.workingCopyOf(workable);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return workable;
		}
	}

	@SuppressWarnings("finally")
	public static final Workable checkIn(Workable workable, String note) {
		try {
			workable = WorkInProgressHelper.service.checkin(workable, note);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return workable;
		}
	}

	private static final ConfigSpec getConfigSpec() {
		try {
			return ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 读取配置文件
	 *
	 * @author xzheng
	 * @date 2012-6-18
	 * @modify
	 * @param path
	 *            相对wt.home的路径 类似: /codebase/com/glaway/lrds/WorkFlow.properties
	 * @return
	 * @throws IOException
	 */
	public static Properties getProperties(String path) throws IOException {
		Properties properties = WTProperties.getLocalProperties();
		if (path != null) {
			String wt_home = properties.getProperty("wt.home");
			GLLogger.debug("wt_home" + wt_home);
			File file = new File(wt_home + path);
			InputStream is = new FileInputStream(file);
			properties.load(new InputStreamReader(is, "UTF-8"));
		}
		return properties;
	}

	public static boolean IsSpace(String str) {
		if (str == null || str.trim().length() == 0) {
			return true;
		}
		return false;
	}

	public static String getSystemURL() throws IOException {
		String url = "";
		Properties prop = Util.getProperties(null);
		url = prop.get("wt.webserver.protocol") + "://" + prop.get("java.rmi.server.hostname") + "/"
				+ prop.get("wt.webapp.name");
		return url;
	}

	/**
	 * 更新文档s
	 *
	 * @author lbzhang
	 * @date 2012-6-30
	 * @modify
	 * @param doc
	 * @param is
	 */
	public static void updateDoc(WTDocument doc, String filePath) {
		try {
			if (doc != null) {
				if (filePath != null) {
					FormatContentHolder currDoc = (FormatContentHolder) doc;
					currDoc = (FormatContentHolder) ContentHelper.service.getContents(currDoc);
					ApplicationData currdata = (ApplicationData) ContentHelper.getPrimary(currDoc);
					if (currdata != null) {
						PersistenceHelper.manager.delete(currdata);
						doc = (WTDocument) PersistenceHelper.manager.refresh(doc);
					}
					ApplicationData ad = ApplicationData.newApplicationData(doc);
					ad.setRole(ContentRoleType.PRIMARY);
					ad = ContentServerHelper.service.updateContent(doc, ad, filePath);
					PersistenceServerHelper.manager.update(ad);
					ad = (ApplicationData) PersistenceHelper.manager.refresh(ad);
					doc = (WTDocument) PersistenceHelper.manager.refresh(doc);
					File file = new File(filePath);
					if (file != null) {
						file.delete();
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取temp临时资料夹
	 *
	 * @author lbzhang
	 * @date 2012-7-1
	 * @modify
	 * @return
	 * @throws IOException
	 */
	public static String getTempPath() throws IOException {
		WTProperties pro = WTProperties.getLocalProperties();
		String temp = "";
		temp = pro.getProperty("wt.temp");
		return temp;
	}

	/**
	 * 获取codebase路径
	 *
	 * @author lbzhang
	 * @date 2012-10-29下午03:30:15
	 * @return
	 * @throws IOException
	 */
	public static String getCodebasePath() throws IOException {
		WTProperties pro = WTProperties.getLocalProperties();
		String codebase = "";
		codebase = pro.getProperty("wt.codebase.location");
		return codebase;
	}

	/**
	 * 根据文件对象，获取最新版本的对象
	 *
	 * @author lbzhang
	 * @date 2012-7-2
	 * @modify
	 * @param doc
	 * @return
	 * @throws WTException
	 * @throws PersistenceException
	 */
	public static WTDocument getLatestDocByDoc(WTDocument doc) throws PersistenceException, WTException {
		if (doc == null) {
			return null;
		}
		QueryResult qr = VersionControlHelper.service.allVersionsOf(doc.getMaster());
		if (qr.hasMoreElements()) {
			return (WTDocument) qr.nextElement();
		} else {
			return null;
		}
	}

	/**
	 * 获取对象的软属性
	 *
	 * @author lbzhang
	 * @date 2012-7-5
	 * @modify
	 * @param obj
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings(value = {})
	public static ArrayList<Hashtable<String, Object>> getAllIBAValues(WTObject obj) throws WTException {

		List<Hashtable<String, Object>> list = new ArrayList<Hashtable<String, Object>>();

		try {
			if (obj instanceof IBAHolder) {
				IBAHolder ibaholder = (IBAHolder) obj;
				DefaultAttributeContainer dac = getContainer(ibaholder);

				if (dac != null) {
					AbstractValueView avv[] = null;
					avv = dac.getAttributeValues();

					for (int j = 0; j < avv.length; j++) {
						Hashtable<String, Object> table = new Hashtable<String, Object>();

						String thisIBAName = avv[j].getDefinition().getName();// 属性内部名称
						String thisIBADisplayName = avv[j].getDefinition().getDisplayName();// 属性显示名称
						// String thisIBAValue =
						// IBAValueUtility.getLocalizedIBAValueDisplayString(avv[j],
						// Locale.CHINA);// 属性值
						String thisIBAClass = (avv[j].getDefinition()).getAttributeDefinitionClassName();
						table.put("1", thisIBAName);// 内显
						table.put("2", thisIBADisplayName);// 外显
						if (thisIBAClass.equals("wt.iba.definition.FloatDefinition")) {
							float value = (float) ((FloatValueDefaultView) avv[j]).getValue();
							table.put("3", new Float(value));
						} else if (thisIBAClass.equals("wt.iba.definition.IntegerDefinition")) {
							long value = ((IntegerValueDefaultView) avv[j]).getValue();
							table.put("3", String.valueOf(value));
						} else if (thisIBAClass.equals("wt.iba.definition.StringDefinition")) {
							String value = ((StringValueDefaultView) avv[j]).getValue();
							table.put("3", value);
						}
						// else if
						// (thisIBAClass.equals("wt.iba.definition.BooleanDefinition")){
						// boolean flag =
						// ((BooleanValueDefaultView)avv[j]).isValue();
						// table.put("3", flag);
						// }else if
						// (thisIBAClass.equals("wt.iba.definition.TimestampDefinition")){
						// java.sql.Timestamp tt =
						// ((TimestampValueDefaultView)avv[j]).getValue();
						// table.put("3", tt);
						// }
						list.add(table);
					}
				}
			}
		} catch (RemoteException rexp) {
			System.out.println(" ** !!!!! ** ERROR Getting IBAHelper.getAllIBAValues");
			rexp.printStackTrace();
		}

		return (ArrayList<Hashtable<String, Object>>) list;
	}

	public static DefaultAttributeContainer getContainer(IBAHolder ibaholder) throws WTException, RemoteException {

		ibaholder = IBAValueHelper.service.refreshAttributeContainerWithoutConstraints(ibaholder);
		DefaultAttributeContainer defaultattributecontainer = (DefaultAttributeContainer) ibaholder
				.getAttributeContainer();

		return defaultattributecontainer;
	}

	/**
	 * 判断用户是否存在专案团队（容器的专案团队）的角色中
	 *
	 * @author yqliu
	 * @date 2012-7-18
	 * @modify
	 * @param containerref
	 * @param user
	 * @param rolename
	 * @return
	 * @throws WTException
	 */
	public static boolean isContainerTeamRole(wt.inf.container.WTContainerRef containerref, WTUser user, String rolename)
			throws WTException {
		WTContainer container = containerref.getReferencedContainer();
		ContainerTeam containerteam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) container);
		WTGroup wtgroup = ContainerTeamHelper.service.findContainerTeamGroup(containerteam, "roleGroups", rolename);
		return wtgroup.isMember(user);
	}

	/**
	 * 判断用户是否存在专案团队（容器的专案团队）的角色中
	 *
	 * @author yqliu
	 * @date 2012-7-18
	 * @modify
	 * @param team
	 * @param principal
	 *            用户
	 * @param role
	 *            角色
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("unchecked")
	public static boolean checkUserInRole(ContainerTeam team, WTPrincipal principal, Role role) throws WTException {
		boolean flag = false;
		Enumeration enumList = team.getPrincipalTarget(role);
		Vector moldGroups = new Vector();
		while (enumList.hasMoreElements()) {
			WTPrincipalReference prinRef = (WTPrincipalReference) enumList.nextElement();
			WTPrincipal tempPrincipal = prinRef.getPrincipal();
			if (tempPrincipal instanceof WTUser) {
				if (tempPrincipal.equals(principal)) {
					flag = true;
					return flag;
				}
			} else if (tempPrincipal instanceof WTGroup) {
				WTGroup tempGroup = null;
				tempGroup = (WTGroup) tempPrincipal;
				moldGroups.add(tempGroup);
			}
		}// end while
		if ((!flag) && (moldGroups.size() > 0)) {
			for (int i = 0; i < moldGroups.size(); i++) {
				// creator
				WTGroup group = (WTGroup) moldGroups.get(i);
				flag = group.isMember(principal);
				if (flag == true) {
					break;
				}
			}// end for

		}
		return flag;
	}

	/**
	 * 获得零件的所有子阶零件
	 *
	 * @author yqliu
	 * @date 2012-8-7
	 * @modify
	 * @param part
	 */
	@SuppressWarnings("deprecation")
	public static void getAllChildParts(List<WTPart> partList, WTPart part) {
		try {
			partList.add(part);
			QueryResult qr;
			qr = WTPartHelper.service.getUsesWTParts((WTPart) part, getConfigSpec());
			if (qr != null) {
				while (qr.hasMoreElements()) {
					Persistable[] per = (Persistable[]) qr.nextElement();
					Object obj = per[1];

					if (obj instanceof WTPart) {
						WTPart childPart = (WTPart) obj;
						getAllChildParts(partList, childPart);
					} else if (obj instanceof WTPartMaster) {
						WTPartMaster master = (WTPartMaster) obj;
						WTPart partTemp = WTPartUtil.getLatestPartByMaster(master);
						getAllChildParts(partList, partTemp);
					}

				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取零件的所有子阶零件---Planning
	 *
	 * @author lbzhang
	 * @date 2012-12-14下午02:50:08
	 * @param part
	 * @param partList
	 */
	@SuppressWarnings("deprecation")
	public static void getAllPBOMChildParts(WTPart part, ArrayList<WTPart> partList) {
		try {
			if (!partList.contains(part)) {
				partList.add(part);
			}

			QueryResult qr = WTPartHelper.service.getUsesWTParts((WTPart) part, getConfigSpec());

			if (qr != null) {
				while (qr.hasMoreElements()) {
					Persistable[] per = (Persistable[]) qr.nextElement();
					WTPart childPart = (WTPart) per[1];
					WTPart tempPart = WTPartUtil.getLatestPartByNumberAndView(childPart, Constants.planning);
					getAllPBOMChildParts(tempPart, partList);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据配置获取派工属性文件属性信息
	 *
	 * @author lbzhang
	 * @date 2012-10-18 下午07:56:21
	 * @modifier
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<String> getPropertyValue(String key) throws IOException {
		Properties prop = getProperties("/codebase/com/glaway/mpm/pbom/ui/technicGroup.properties");
		ArrayList<String> list = new ArrayList<String>();
		String groups = prop.getProperty(key);
		String[] group = groups.split(",");
		for (int i = 0; i < group.length; i++) {
			list.add(group[i]);
		}
		return list;
	}

	/**
	 * 设置零件对象的软属性值
	 *
	 * @author lbzhang
	 * @date 2012-10-19 上午11:27:39
	 * @modifier
	 * @param part
	 * @param ibaName
	 * @param ibaValue
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 */
	public static void setPartSoftAttri(WTPart part, String ibaName, String ibaValue) throws WTException,
			WTPropertyVetoException, RemoteException {
		IBAHelper iba = new IBAHelper((IBAHolder) part);
		GLLogger.debug("set part soft:" + part.getName() + "  " + part.getNumber());
		GLLogger.debug("ibaName:" + ibaName);
		GLLogger.debug("ibaValue:" + ibaValue);
		iba.setIBAValue(ibaName, ibaValue);
		iba.updateAttributeContainer(part);
		iba.updateIBAHolder(part);

		IBAHelper iba2 = new IBAHelper(part);
		GLLogger.debug("has set value:" + iba2.getIBAValue(ibaName));
	}

	/**
	 * 设置对象软属性，软属性为字符串类型
	 *
	 * @author lbzhang
	 * @date 2012-11-6下午02:05:37
	 * @param obj
	 * @param ibaName
	 * @param ibaValue
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 */
	public static Object setObjectSoftAttriString(Object obj, String ibaName, String ibaValue) throws WTException,
			WTPropertyVetoException, RemoteException {
		IBAHelper iba = new IBAHelper((IBAHolder) obj);
		GLLogger.debug("ibaName:" + ibaName);
		GLLogger.debug("ibaValue:" + ibaValue);
		iba.setIBAValue(ibaName, ibaValue);
		iba.updateAttributeContainer((IBAHolder) obj);
		iba.updateIBAHolder((IBAHolder) obj);
		obj = PersistenceHelper.manager.refresh((Persistable) obj);
		return obj;
	}

	/**
	 * 设置对象软属性，软属性类型为布尔类型
	 *
	 * @author lbzhang
	 * @date 2012-11-14下午02:03:31
	 * @param obj
	 * @param ibaName
	 * @param ibaValue
	 * @return
	 * @throws WTException
	 */
	public static Object setObjectSoftAttriBoolean(Object obj, String ibaName, String ibaValue) throws WTException {
		GLLogger.debug("ibaName:" + ibaName);
		GLLogger.debug("ibaValue:" + ibaValue);
		IBAHelper.setIBABooleanValue((WTObject) obj, ibaName, Boolean.parseBoolean(ibaValue));
		obj = PersistenceHelper.manager.refresh((Persistable) obj);
		return obj;
	}

	/**
	 * 通过群组名称获取群组对象
	 *
	 * @author lbzhang
	 * @date 2012-10-22 上午10:21:37
	 * @modifier
	 * @param groupName
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("deprecation")
	public static WTGroup getGroupByName(String groupName) throws WTException {
		QuerySpec qs = new QuerySpec(WTGroup.class);
		qs.appendWhere(new SearchCondition(WTGroup.class, WTGroup.NAME, SearchCondition.EQUAL, groupName, false));
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.hasMoreElements()) {
			WTGroup group = (WTGroup) qr.nextElement();
			return group;
		}

		return null;
	}

	/**
	 * 获取零件对象软属性的值
	 *
	 * @author lbzhang
	 * @date 2012-11-15下午12:11:59
	 * @param part
	 * @param ibaName
	 * @return
	 * @throws WTException
	 */
	public static String getSoftAttribute(WTPart part, String ibaName) throws WTException {
		IBAHelper iba = new IBAHelper(part);
		String value = iba.getIBAValue(ibaName);
		return value;
	}

	/**
	 * 通过群组名称获取群组的成员
	 *
	 * @author lbzhang
	 * @date 2012-10-22 上午10:29:18
	 * @modifier
	 * @param groupName
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings(value = { "unchecked" })
	public static List<WTUser> getWTUserInGroup(String groupName) throws WTException {
		List<WTUser> userList = new ArrayList<WTUser>();
		WTGroup group = getGroupByName(groupName);
		if (group == null) {
			GLLogger.debug("Group: is null");
			return userList;
		}
		Enumeration enumeration = group.members();
		if (enumeration.hasMoreElements()) {
			WTUser user = (WTUser) enumeration.nextElement();
			userList.add(user);
		}
		return userList;
	}

	/**
	 * 获取当前的群组人员
	 *
	 * @author lbzhang
	 * @date 2012-12-18下午03:31:23
	 * @param part
	 * @param role
	 * @return
	 * @throws WTRuntimeException
	 * @throws WTException
	 */
	public static ArrayList<String> getCurrentContainerTeamGroupUser(WTPart part, String role)
			throws WTRuntimeException, WTException {
		// role = role + "MPMLEADER";
		ArrayList<ArrayList<String>> list = getCurrentContainerTeamGroupUsers(part, role);
		GLLogger.debug("list.size:" + list.size());
		if (list.size() == 0) {
			ArrayList<String> tempList = new ArrayList<String>();
			tempList.add("此组下无任何人员，请添加人员或者联系管理员");
			list.add(tempList);
		}
		return list.get(0);
	}

	/**
	 * 获取当前对象所在容器的专案团队中指定角色的人员--以字符串拼接
	 *
	 * @author lbzhang
	 * @date 2012-10-25上午11:14:43
	 * @modifier
	 * @param part
	 * @param role
	 * @return
	 * @throws WTRuntimeException
	 * @throws WTException
	 */
	public static String getCurrentContainerTeamTheRoleUserStr(WTPart part, String role) throws WTRuntimeException,
			WTException {
		// 获取对象的容器引用
		String containerRef = part.getContainerReference().toString();
		String allUserStr = "";

		// 判断是什么容器
		if (containerRef.indexOf("PDMLinkProduct") >= 0) {// 产品
			PDMLinkProduct product = (PDMLinkProduct) ReferenceFactory.getObjectbyOid(containerRef);
			ContainerTeam containerTeam = (ContainerTeam) product.getContainerTeamReference().getObject();

			Enumeration<WTPrincipalReference> prinEnum = containerTeam.getPrincipalTarget(Role.toRole(role));// 获取组员
			while (prinEnum.hasMoreElements()) {
				WTPrincipalReference principalRef = prinEnum.nextElement();
				WTPrincipal principal = (WTPrincipal) principalRef.getObject();
				if (principal instanceof WTGroup) {
					continue;
				}
				WTUser user = (WTUser) principal;
				GLLogger.debug("principal==1==>" + principal.getName() + "  "
						+ principal.getPrincipalDisplayIdentifier());
				String temp = user.getPersistInfo().getObjectIdentifier().toString() + "#"
						+ principal.getPrincipalDisplayIdentifier();
				allUserStr += temp.trim() + "|";
			}
		}
		if (containerRef.indexOf("WTLibrary") >= 0) {// 物件库
			WTLibrary lib = (WTLibrary) ReferenceFactory.getObjectbyOid(containerRef);
			ContainerTeam containerTeam = (ContainerTeam) lib.getContainerTeamReference().getObject();

			Enumeration<WTPrincipalReference> prinEnum = containerTeam.getPrincipalTarget(Role.toRole(role));// 获取组员
			while (prinEnum.hasMoreElements()) {
				WTPrincipalReference principalRef = prinEnum.nextElement();
				WTPrincipal principal = (WTPrincipal) principalRef.getObject();
				if (principal instanceof WTGroup) {
					continue;
				}
				WTUser user = (WTUser) principal;
				GLLogger.debug("principal====>" + principal.getName() + "  "
						+ principal.getPrincipalDisplayIdentifier());
				String temp = user.getPersistInfo().getObjectIdentifier().toString() + "#"
						+ principal.getPrincipalDisplayIdentifier();
				allUserStr += temp.trim() + "|";
			}
		}

		GLLogger.debug("allUserStr:" + allUserStr);
		if (!"".equals(allUserStr)) {
			allUserStr = allUserStr.substring(0, allUserStr.length() - 1);
			return allUserStr;
		} else {
			return "";
		}
	}

	/**
	 * 获取当前对象所在容器的专案团队中指定群组(角色)的人员
	 *
	 * @author lbzhang
	 * @date 2012-12-18下午03:16:42
	 * @param part
	 * @param role
	 * @return
	 * @throws WTRuntimeException
	 * @throws WTException
	 */
	public static ArrayList<ArrayList<String>> getCurrentContainerTeamGroupUsers(WTPart part, String role)
			throws WTRuntimeException, WTException {
		// 获取对象的容器引用
		String containerRef = part.getContainerReference().toString();
		ArrayList<ArrayList<String>> allList = new ArrayList<ArrayList<String>>();

		// 判断是什么容器
		if (containerRef.indexOf("PDMLinkProduct") >= 0) {// 产品
			PDMLinkProduct product = (PDMLinkProduct) ReferenceFactory.getObjectbyOid(containerRef);
			ContainerTeam containerTeam = (ContainerTeam) product.getContainerTeamReference().getObject();

			Enumeration<WTPrincipalReference> prinEnumLeader = containerTeam.getPrincipalTarget(Role.toRole(role));// 获取普通组成员
			while (prinEnumLeader.hasMoreElements()) {
				WTPrincipalReference principalRef = prinEnumLeader.nextElement();
				WTPrincipal principal = (WTPrincipal) principalRef.getObject();
				if (principal instanceof WTGroup) {
					continue;
				}
				WTUser user = (WTUser) principal;
				GLLogger.debug("principal==2==>" + principal.getName() + "  "
						+ principal.getPrincipalDisplayIdentifier());
				ArrayList<String> list = new ArrayList<String>();
				// list.add(principal.getName());
				list.add(user.getPersistInfo().getObjectIdentifier().toString());
				list.add(principal.getPrincipalDisplayIdentifier());
				allList.add(list);
			}
		}

		if (containerRef.indexOf("WTLibrary") >= 0) {// 物件库
			WTLibrary lib = (WTLibrary) ReferenceFactory.getObjectbyOid(containerRef);
			ContainerTeam containerTeam = (ContainerTeam) lib.getContainerTeamReference().getObject();

			Enumeration<WTPrincipalReference> prinEnum = containerTeam.getPrincipalTarget(Role.toRole(role));// 获取组员
			while (prinEnum.hasMoreElements()) {
				WTPrincipalReference principalRef = prinEnum.nextElement();
				WTPrincipal principal = (WTPrincipal) principalRef.getObject();
				if (principal instanceof WTGroup) {
					continue;
				}
				WTUser user = (WTUser) principal;
				GLLogger.debug("principal==1==>" + principal.getName() + "  "
						+ principal.getPrincipalDisplayIdentifier());
				ArrayList<String> list = new ArrayList<String>();
				list.add(user.getPersistInfo().getObjectIdentifier().toString());
				list.add(principal.getPrincipalDisplayIdentifier());
				allList.add(list);
			}
		}

		return allList;
	}

	/**
	 * 当前用户为组长时，获取当前普通组组名称
	 *
	 * @author lbzhang
	 * @date 2012-12-19下午02:52:00
	 * @param part
	 * @return
	 * @throws WTException
	 * @throws WTRuntimeException
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<ArrayList<String>> getCurrentUsersGroupName(WTPart part) throws WTRuntimeException,
			WTException {
		ArrayList<ArrayList<String>> groupName = new ArrayList<ArrayList<String>>();
		ArrayList<String> outNameList = new ArrayList<String>();
		ArrayList<String> inNameList = new ArrayList<String>();

		String containerRef = part.getContainerReference().toString();

		// 当前用户
		WTPrincipal principal = SessionHelper.manager.getPrincipal();
		String currentUser = principal.getName();

		// 判断是什么容器
		if (containerRef.indexOf("PDMLinkProduct") >= 0) {// 产品
			PDMLinkProduct product = (PDMLinkProduct) ReferenceFactory.getObjectbyOid(containerRef);
			ContainerTeam containerTeam = (ContainerTeam) product.getContainerTeamReference().getObject();

			Vector<Role> roles = containerTeam.getRoles();// 获取上下文下的所有角色

			for (int i = 0; i < roles.size(); i++) {
				Role allRole = roles.get(i);
				Enumeration<WTPrincipalReference> enumPrinAll = containerTeam.getPrincipalTarget(allRole);
				while (enumPrinAll.hasMoreElements()) {
					WTPrincipalReference principalRef = enumPrinAll.nextElement();
					WTPrincipal prin = principalRef.getPrincipal();
					if (currentUser.equals(prin.getName())) {
						String roleStr = allRole.getStringValue();
						roleStr = roleStr.substring(roleStr.lastIndexOf(".") + 1, roleStr.length());
						if (roleStr.endsWith("MPMLEADER")) {
							String newRoleStr = roleStr.substring(0, roleStr.lastIndexOf("MPMLEADER"));
							Role role = Role.toRole(newRoleStr);
							String outName = role.getDisplay();
							GLLogger.debug("role outName===>" + outName);
							String inName = newRoleStr;
							GLLogger.debug("role inName====>" + inName);
							outNameList.add(outName);
							inNameList.add(inName);
						}
					}
				}
			}
		}
		if (containerRef.indexOf("WTLibrary") >= 0) {// 物件库
			WTLibrary wtlib = (WTLibrary) ReferenceFactory.getObjectbyOid(containerRef);
			ContainerTeam containerTeam = (ContainerTeam) wtlib.getContainerTeamReference().getObject();

			Vector<Role> roles = containerTeam.getRoles();// 获取上下文下的所有角色

			for (int i = 0; i < roles.size(); i++) {
				Role allRole = roles.get(i);
				Enumeration<WTPrincipalReference> enumPrinAll = containerTeam.getPrincipalTarget(allRole);
				while (enumPrinAll.hasMoreElements()) {
					WTPrincipalReference principalRef = enumPrinAll.nextElement();
					WTPrincipal prin = principalRef.getPrincipal();
					if (currentUser.equals(prin.getName())) {
						String roleStr = allRole.getStringValue();
						roleStr = roleStr.substring(roleStr.lastIndexOf(".") + 1, roleStr.length());
						if (roleStr.endsWith("MPMLEADER")) {
							String newRoleStr = roleStr.substring(0, roleStr.lastIndexOf("MPMLEADER"));
							Role role = Role.toRole(newRoleStr);
							String outName = role.getDisplay();
							GLLogger.debug("role outName===>" + outName);
							String inName = newRoleStr;
							GLLogger.debug("role inName====>" + inName);
							outNameList.add(outName);
							inNameList.add(inName);
						}
					}
				}
			}
		}

		groupName.add(outNameList);
		groupName.add(inNameList);

		return groupName;
	}

	/**
	 * 获取使用者所在的组(角色)
	 *
	 * @author lbzhang
	 * @date 2012-12-19下午04:09:55
	 * @param userName
	 * @param obj
	 * @return
	 * @throws WTRuntimeException
	 * @throws WTException
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<String> getUsersGroups(String userName, Object obj) throws WTRuntimeException, WTException {
		String containerRef = "";
		ArrayList<String> groupList = new ArrayList<String>();

		if (obj instanceof WTPart) {
			WTPart part = (WTPart) obj;
			containerRef = part.getContainerReference().toString();
		}

		if ("".equals(containerRef)) {
			return null;
		}

		// 判断是什么容器
		if (containerRef.indexOf("PDMLinkProduct") >= 0) {// 产品
			PDMLinkProduct product = (PDMLinkProduct) ReferenceFactory.getObjectbyOid(containerRef);
			ContainerTeam containerTeam = (ContainerTeam) product.getContainerTeamReference().getObject();

			Vector<Role> roles = containerTeam.getRoles();// 获取上下文下的所有角色

			for (int i = 0; i < roles.size(); i++) {
				Role allRole = roles.get(i);
				Enumeration<WTPrincipalReference> enumPrinAll = containerTeam.getPrincipalTarget(allRole);
				while (enumPrinAll.hasMoreElements()) {
					WTPrincipalReference principalRef = enumPrinAll.nextElement();
					WTPrincipal prin = principalRef.getPrincipal();
					if (userName.equals(prin.getName())) {
						String roleStr = allRole.getStringValue();
						roleStr = roleStr.substring(roleStr.lastIndexOf(".") + 1, roleStr.length());
						GLLogger.debug("roleStr===>" + roleStr);
						groupList.add(roleStr);
					}
				}
			}
		}

		if (containerRef.indexOf("WTLibrary") >= 0) {// 存储库
			WTLibrary wtlib = (WTLibrary) ReferenceFactory.getObjectbyOid(containerRef);
			ContainerTeam containerTeam = (ContainerTeam) wtlib.getContainerTeamReference().getObject();

			Vector<Role> roles = containerTeam.getRoles();// 获取上下文下的所有角色

			for (int i = 0; i < roles.size(); i++) {
				Role allRole = roles.get(i);
				Enumeration<WTPrincipalReference> enumPrinAll = containerTeam.getPrincipalTarget(allRole);
				while (enumPrinAll.hasMoreElements()) {
					WTPrincipalReference principalRef = enumPrinAll.nextElement();
					WTPrincipal prin = principalRef.getPrincipal();
					if (userName.equals(prin.getName())) {
						String roleStr = allRole.getStringValue();
						roleStr = roleStr.substring(roleStr.lastIndexOf(".") + 1, roleStr.length());
						GLLogger.debug("roleStr===>" + roleStr);
						groupList.add(roleStr);
					}
				}
			}
		}
		return groupList;
	}

	/**
	 * 通过user的内部名称获取显示名称
	 *
	 * @author lbzhang
	 * @date 2012-10-26下午09:45:52
	 * @param userName
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("deprecation")
	public static String getUserDisplayName(String userName) throws WTException {
		QuerySpec qs = new QuerySpec(WTUser.class);
		qs.appendWhere(new SearchCondition(WTUser.class, WTUser.NAME, SearchCondition.EQUAL, userName, false));
		QueryResult qr = PersistenceHelper.manager.find(qs);
		while (qr.hasMoreElements()) {
			WTUser user = (WTUser) qr.nextElement();
			return user.getFullName();
		}
		return null;
	}

	/**
	 * 获取当前容器团队下的指定角色的人员
	 *
	 * @author lbzhang
	 * @date 2012-11-27下午01:22:24
	 * @param obj
	 *            //流程主对象
	 * @param role
	 *            //流程角色
	 * @param self
	 *            //流程对象
	 * @return
	 * @throws WTException
	 * @throws WTRuntimeException
	 */
	public static String getCurrentContainerTheRoleUsers(Object obj, String role, ObjectReference self)
			throws WTRuntimeException, WTException {
		String users = "";
		String containerRef = "";

		if (obj instanceof WTPart) {
			WTPart part = (WTPart) obj;
			containerRef = part.getContainerReference().toString();
		}
		if (obj instanceof WTDocument) {
			WTDocument doc = (WTDocument) obj;
			containerRef = doc.getContainerReference().toString();
		}
		if (obj instanceof ChangeIssue) {
			ChangeIssue changei = (ChangeIssue) obj;
			containerRef = changei.getContainerReference().toString();
		}
		if (obj instanceof MPMTooling) {
			MPMTooling tool = (MPMTooling) obj;
			containerRef = tool.getContainerReference().toString();
		}
		if (obj instanceof MPMProcessMaterial) {
			MPMProcessMaterial material = (MPMProcessMaterial) obj;
			containerRef = material.getContainerReference().toString();
		}
		GLLogger.debug("containerRef:" + containerRef);

		if (containerRef.indexOf("PDMLinkProduct") >= 0) {// 产品
			PDMLinkProduct product = (PDMLinkProduct) ReferenceFactory.getObjectbyOid(containerRef);
			ContainerTeam containerTeam = (ContainerTeam) product.getContainerTeamReference().getObject();

			Enumeration<WTPrincipalReference> prinEnum = containerTeam.getPrincipalTarget(Role.toRole(role));// 获取组员
			while (prinEnum.hasMoreElements()) {
				WTPrincipalReference principalRef = prinEnum.nextElement();
				WTPrincipal principal = (WTPrincipal) principalRef.getObject();
				if (principal instanceof WTGroup) {
					continue;
				}
				WTUser user = (WTUser) principal;
				users = user.getPersistInfo().getObjectIdentifier().toString();
			}
		}

		if (containerRef.indexOf("WTLibrary") >= 0) {
			WTLibrary lib = (WTLibrary) ReferenceFactory.getObjectbyOid(containerRef);
			ContainerTeam containerTeam = (ContainerTeam) lib.getContainerTeamReference().getObject();

			Enumeration<WTPrincipalReference> prinEnum = containerTeam.getPrincipalTarget(Role.toRole(role));// 获取组员
			while (prinEnum.hasMoreElements()) {
				WTPrincipalReference principalRef = prinEnum.nextElement();
				WTPrincipal principal = (WTPrincipal) principalRef.getObject();
				if (principal instanceof WTGroup) {
					continue;
				}
				WTUser user = (WTUser) principal;
				users = user.getPersistInfo().getObjectIdentifier().toString();
			}
		}

		GLLogger.debug("users:" + users);

		if ("".equals(users)) {
			WfProcess process = (WfProcess) self.getObject();
			WTPrincipal prin = (WTPrincipal) process.getCreator().getObject();
			users = prin.getPersistInfo().getObjectIdentifier().toString();
		}
		GLLogger.debug("users:" + users);
		return users;
	}

	/**
	 * 判断当前用户是否属于指定群组
	 *
	 * @author lbzhang
	 * @date 2013-3-22
	 * @return
	 * @throws WTException
	 *
	 */
	@SuppressWarnings("unchecked")
	public static boolean isTheCurrentInTheGroup(String groupName) throws WTException {
		WTPrincipal usr = SessionHelper.manager.getPrincipal();
		String as[] = OrganizationServicesHelper.manager.getDirectoryServiceNames();
		DirectoryContextProvider context = OrganizationServicesHelper.manager.newDirectoryContextProvider(as, null);
		Enumeration enumeration = OrganizationServicesHelper.manager.getGroups(groupName, context);
		if (enumeration != null) {
			while (enumeration.hasMoreElements()) {
				if (OrganizationServicesHelper.manager.isMember((WTGroup) enumeration.nextElement(), usr)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * 判断当前用户是否是室主任
	 * @author lbzhang
	 * @date  2013-7-24
	 * @return
	 * @throws WTException
	 *
	 */
	public static boolean isTheCurrentInTheShiZhuRenGroupOnly() throws WTException{
		WTPrincipal principal = SessionHelper.manager.getPrincipal();
		ArrayList<String> groupList = WTPrincipalUtil.getUserInGorupName(principal);
		String path = ConfigPath.GROUPCONFIG;
		String shizhuren = PropertiesUtil.getValue(Constants.ShiZhuRen, path);
		String[] shizhurenGroup = shizhuren.split(",");
		for(int i = 0; i < shizhurenGroup.length; i++){
			String temp = shizhurenGroup[i];
			GLLogger.debug("shizhuren=====>" + temp);
			if(groupList.contains(temp)){
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取产品中的指定角色中的一个人
	 *
	 * @author lbzhang
	 * @date 2013-3-26
	 * @param containerRefStr
	 * @param role
	 * @return
	 * @throws WTRuntimeException
	 * @throws WTException
	 *
	 */
	public static WTPrincipal getWTContainerOfRole(String containerRefStr, String role) throws WTRuntimeException,
			WTException {

		WTContainer container = (WTContainer) ReferenceFactory.getObjectbyOid(containerRefStr);
		ContainerTeam containerTeam = WorkflowUtil.getContainerTeam(container);
		Enumeration<WTPrincipalReference> prinEnumLeader = containerTeam.getPrincipalTarget(Role.toRole(role));
		while (prinEnumLeader.hasMoreElements()) {
			WTPrincipalReference principalRef = prinEnumLeader.nextElement();
			WTPrincipal principal = (WTPrincipal) principalRef.getObject();
			if (principal instanceof WTGroup) {
				continue;
			}
			return principal;
		}

		return null;
	}

	/**
	 * 获取对象oid数字值
	 *
	 * @author lbzhang
	 * @date 2013-4-1
	 * @param prinRef
	 * @return
	 *
	 */
	public static long getObjectOid(Object obj) {
		String objOidStr = obj.toString();
		objOidStr = objOidStr.substring(objOidStr.lastIndexOf(":") + 1, objOidStr.length());

		long objOid = Long.parseLong(objOidStr);
		return objOid;
	}

	/**
	 * 把list转换成string
	 *
	 * @author lbzhang
	 * @date 2013-4-25
	 * @param list
	 * @return
	 *
	 */
	public static String arrayListConvertToString(ArrayList<String> list) {
		String str = "";
		for (int i = 0; i < list.size(); i++) {
			String temp = list.get(i);
			if ("null".equals(temp) || null == temp) {
				temp = "";
			}
			str += temp + ",";
		}
		if (str.length() > 0) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	/**
	 * 把字符串类型的ArrayList转换为数组Array
	 * @author lbzhang
	 * @date  2013-7-20
	 * @param list
	 * @return
	 *
	 */
	public static String[] arrayListConvertToArray(ArrayList<String> list){
		String[] array = new String[list.size()];
		list.toArray(array);
		return array;
	}

	/**
	 * 字符串转换成ArrayList，中间以，隔开
	 * @author lbzhang
	 * @date  2013-7-24
	 * @param str
	 * @return
	 *
	 */
	public static ArrayList<String> strToArrayList(String str){
		ArrayList<String> list = new ArrayList<String>();
		String[] strs = str.split(",");
		for(int i = 0; i < strs.length; i++){
			list.add(strs[i]);
		}
		return list;
	}

	/**
	 * 判断AL零件是否是整件
	 *
	 * @author lbzhang
	 * @date 2013-4-26
	 * @param number
	 * @return
	 *
	 */
	public static boolean isALZJPart(String number) {
		return Pattern.matches("AL[1-4]\\.", number.substring(0, 4));
	}

	/**
	 * 通过用户全名获取用户
	 *
	 * @author lbzhang
	 * @date 2013-5-9
	 * @param fullNames
	 * @return
	 * @throws WTException
	 *
	 */
	@SuppressWarnings("deprecation")
	public static WTUser getUserByFullName(String fullNames) throws WTException {
		WTPrincipal prin = SessionHelper.manager.getPrincipal();
		WTUser temp = (WTUser) prin;
		if ("".equals(fullNames)) {
			return temp;
		}
		QuerySpec qs = new QuerySpec(WTUser.class);
		String[] fullName = fullNames.split(",");
		qs.appendWhere(new SearchCondition(WTUser.class, WTUser.LAST, SearchCondition.EQUAL, fullName[0]));
		if (fullName.length > 1) {
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTUser.class, WTUser.FIRST, SearchCondition.EQUAL, fullName[1]));
			qs.setAdvancedQueryEnabled(true);
		}
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.hasMoreElements()) {
			WTUser user = (WTUser) qr.nextElement();
			return user;
		} else {
			return temp;
		}
	}

	/**
	 * 获取工艺任务对象的属性
	 *
	 * @author lbzhang
	 * @date 2013-6-24
	 * @param obj
	 * @param name
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 *
	 */
//	public static Object getResultByAttrOfObj(GMTask obj, String name) throws SecurityException, NoSuchMethodException,
//			IllegalArgumentException, IllegalAccessException, InvocationTargetException {
//		name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
//		Method method = GMTask.class.getMethod("get" + name);
//		Object rtObj = method.invoke(obj);
//		if (rtObj == null) {
//			return null;
//		}
//		return rtObj;
//		return null;
//	}

	/**
	 * 设置版本
	 *
	 * @author lbzhang
	 * @date 2013-6-25
	 * @param versioned
	 * @param s
	 *
	 */
	public static void setVersion(Versioned versioned, String s) {
		MultilevelSeries multilevelseries;
		Mastered mastered;
		VersionIdentifier versionidentifier;
		try {
			if (s == null || s.trim().length() == 0) {
				s = null;
				if (versioned.getVersionInfo() != null)
					return;
			}
		} catch (Exception exception) {
		}

		multilevelseries = null;
		mastered = versioned.getMaster();
		if (mastered != null) {
			String s1 = mastered.getSeries();
			if (s1 == null) {
				if ((versioned instanceof WTContained) && ((WTContained) versioned).getContainer() != null) {
					try {
						multilevelseries = VersionControlHelper.getVersionIdentifierSeries(versioned);
						VersionControlServerHelper.changeSeries(mastered, multilevelseries.getUniqueSeriesName());
					} catch (VersionControlException e) {
					} catch (WTPropertyVetoException e) {
					} catch (WTException e) {
					}
				}
			} else {
				try {
					multilevelseries = MultilevelSeries.newMultilevelSeries(s1);
				} catch (SeriesException e) {
				}
			}
		}
		if (multilevelseries == null) {

			try {
				multilevelseries = MultilevelSeries.newMultilevelSeries("wt.vc.VersionIdentifier", s);
			} catch (Exception e) {
			}
		}
		if (s != null) {
			try {
				multilevelseries.setValueWithoutValidating(s.trim());
			} catch (Exception e) {
			}
		}
		try {
			versionidentifier = VersionIdentifier.newVersionIdentifier(multilevelseries);
			VersionControlServerHelper.setVersionIdentifier(versioned, versionidentifier, false);
		} catch (WTException e) {
		}
	}

	/**
	 * 设置版序
	 *
	 * @author lbzhang
	 * @date 2013-6-25
	 * @param iterated
	 * @param s
	 *
	 */
	public static void setIteration(Iterated iterated, String s) {
		try {
			if (s != null) {
				Series series = Series.newSeries("wt.vc.IterationIdentifier", s);
				IterationIdentifier iterationidentifier = IterationIdentifier.newIterationIdentifier(series);
				VersionControlHelper.setIterationIdentifier(iterated, iterationidentifier);
			}
		} catch (WTPropertyVetoException e) {
		} catch (SeriesException e) {
		} catch (WTException e) {
		}
	}


	public static WTContainer getContainerByName(String containerName) {
		WTContainer container = null;
		QueryResult qr = null;
		QuerySpec qs;
		try {
			qs = new QuerySpec(WTLibrary.class);
			qs.appendWhere(new SearchCondition(WTLibrary.class, WTLibrary.NAME, "=", containerName));
			qr = PersistenceHelper.manager.find((StatementSpec) qs);
			if (qr.hasMoreElements()) {
				container = (WTContainer) qr.nextElement();
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return container;
	}

	public static Persistable searchRMI(Class klass, long idA2A2) {
		Persistable ret = null;
		try {
			QuerySpec qs = new QuerySpec(klass);
			qs.appendWhere(new SearchCondition(klass, WTAttributeNameIfc.ID_NAME, SearchCondition.EQUAL, idA2A2), index);
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			if (qr.hasMoreElements())
				ret = (Persistable) qr.nextElement();
		} catch (WTException qe) {
//			log.error(qe);
			qe.printStackTrace();
		}

		return ret;
	}
}
