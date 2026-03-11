package com.glaway.mpm.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import wt.access.NotAuthorizedException;
import wt.doc.WTDocument;
import wt.fc.ObjectIdentifier;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.definition.IBADefinitionException;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.litedefinition.BooleanDefView;
import wt.iba.definition.litedefinition.FloatDefView;
import wt.iba.definition.litedefinition.IntegerDefView;
import wt.iba.definition.litedefinition.StringDefView;
import wt.iba.definition.litedefinition.TimestampDefView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.BooleanValue;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.FloatValue;
import wt.iba.value.IBAHolder;
import wt.iba.value.IBAValueUtility;
import wt.iba.value.IntegerValue;
import wt.iba.value.StringValue;
import wt.iba.value.TimestampValue;
import wt.iba.value.litevalue.AbstractContextualValueDefaultView;
import wt.iba.value.litevalue.AbstractValueView;
import wt.iba.value.litevalue.BooleanValueDefaultView;
import wt.iba.value.litevalue.FloatValueDefaultView;
import wt.iba.value.litevalue.IntegerValueDefaultView;
import wt.iba.value.litevalue.StringValueDefaultView;
import wt.iba.value.litevalue.TimestampValueDefaultView;
import wt.iba.value.service.IBAValueDBService;
import wt.iba.value.service.IBAValueDBServiceInterface;
import wt.iba.value.service.IBAValueHelper;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ManagerServiceFactory;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.ptc.core.foundation.type.server.impl.SoftAttributesHelper;

public class IBAHelper1 implements Serializable, RemoteAccess {

	private static final long serialVersionUID = 1L;
	private static final String CLASSNAME = IBAHelper1.class.getName();
	Hashtable ibaContainer;

	public IBAHelper1(IBAHolder ibaHolder) throws WTException {
		try {
			initializeIBAValue(ibaHolder);
		} catch (Exception e) {
			throw new WTException(e);
		}
	}

	private void initializeIBAValue(IBAHolder ibaHolder) throws WTException, RemoteException {
		ibaContainer = new Hashtable();
		if (ibaHolder.getAttributeContainer() == null) {
			ibaHolder = IBAValueHelper.service.refreshAttributeContainer(ibaHolder, null, null, null);
		}
		DefaultAttributeContainer theContainer = (DefaultAttributeContainer) ibaHolder.getAttributeContainer();
		if (theContainer == null) {
			return;
		}
		AttributeDefDefaultView[] theAtts = theContainer.getAttributeDefinitions();
		for (AttributeDefDefaultView addv : theAtts) {
			System.out.println(addv.getName());
			AbstractValueView[] theValues = theContainer.getAttributeValues(addv);
			if (theValues == null) {
				continue;
			}
			Object[] temp = new Object[theValues.length + 1];
			temp[0] = addv;
			for (int j = 1; j <= theValues.length; j++) {
				temp[j] = theValues[j - 1];
			}
			ibaContainer.put(addv.getName(), temp);
		}
	}

	/**
	 * Get IBA value (single)
	 * 
	 * @author qianlong
	 * @date 2013-7-5
	 * @param name
	 * @return
	 * 
	 */
	public String getIBAValue(String name) {
		String value = null;
		if (ibaContainer.get(name) != null) {
			AbstractContextualValueDefaultView theValue = (AbstractContextualValueDefaultView) ((Object[]) ibaContainer
					.get(name))[1];
			value = theValue.getValueAsString();
		}
		return value;
	}

	/**
	 * Get IBA value (single)
	 * 
	 * @author qianlong
	 * @date 2013-7-5
	 * @param name
	 * @return
	 * 
	 */
	public String getIBADisplayValue(String name) {
		String value = null;
		try {
			if (ibaContainer.get(name) != null) {
				AbstractValueView theValue = (AbstractValueView) ((Object[]) ibaContainer.get(name))[1];
				value = IBAValueUtility.getLocalizedIBAValueDisplayString(theValue, SessionHelper.manager.getLocale());

			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * Get IBA values (multi)
	 * 
	 * @author qianlong
	 * @date 2013-7-5
	 * @param name
	 * @return
	 * 
	 */
	public Vector getIBAValues(String name) {
		Vector vector = new Vector();
		if (ibaContainer.get(name) != null) {
			Object[] objs = (Object[]) ibaContainer.get(name);
			for (int i = 1; i < objs.length; i++) {
				AbstractContextualValueDefaultView theValue = (AbstractContextualValueDefaultView) objs[i];
				vector.addElement(theValue.getValueAsString());
			}
		}
		return vector;
	}

	/**
	 * 获取任意类型IBA
	 * 
	 * @author qianlong
	 * @date 2013-7-5
	 * @param ibaHolder
	 * @param ibaName
	 * @return
	 */
	public static String getAnyIBAValueOfObject(IBAHolder ibaHolder, String ibaName) {
		String method = "getAnyIBAValueOfObject";
		if (!RemoteMethodServer.ServerFlag) {
			Class[] types = { IBAHolder.class, String.class };
			Object[] vals = { ibaHolder, ibaName };
			try {
				return (String) RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, vals);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		boolean access = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
			GLLogger.debug(CLASSNAME, "method:" + method + ",ibaHolder:" + ibaHolder + ",ibaName:" + ibaName + ",addv:"
					+ addv);
			if (addv == null) {
				throw new WTException("软属性'" + ibaName + "'不存在");
			}
			if (addv instanceof StringDefView) {
				return getStringIBAValueOfObject(ibaHolder, addv, ibaName);
			} else if (addv instanceof IntegerDefView) {
				Integer ibaValue = getIntegerIBAValueOfObject(ibaHolder, addv, ibaName);
				return ibaValue == null ? null : ibaValue.toString();
			} else if (addv instanceof BooleanDefView) {
				Boolean ibaValue = getBooleanIBAValueOfObject(ibaHolder, addv, ibaName);
				return ibaValue == null ? null : ibaValue.toString();
			} else if (addv instanceof FloatDefView) {
				Float ibaValue = getFloatIBAValueOfObject(ibaHolder, addv, ibaName);
				return ibaValue == null ? null : ibaValue.toString();
			} else if (addv instanceof TimestampDefView) {
				Timestamp ibaValue = getTimestampIBAValueOfObject(ibaHolder, addv, ibaName);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				return ibaValue == null ? null : sdf.format(new Timestamp(ibaValue.getTime() + 8 * 60 * 60 * 1000));
			} else {
				throw new WTException("软属性'" + ibaName + "'的类型是'" + addv + "',代码没有处理！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(access);
		}
		return null;
	}

	// 用于得到字符串型的软属性值
	private static String getStringIBAValueOfObject(IBAHolder ibaHolder, AttributeDefDefaultView addv, String ibaName)
			throws IBADefinitionException, NotAuthorizedException, RemoteException, WTException {
		long ibaDefId = addv.getObjectID().getId();
		long prjObid = PersistenceHelper.getObjectIdentifier((Persistable) ibaHolder).getId();
		QuerySpec qs = new QuerySpec(StringValue.class);
		qs.appendWhere(new SearchCondition(StringValue.class, StringValue.DEFINITION_REFERENCE + "."
				+ ObjectReference.KEY + "." + ObjectIdentifier.ID, SearchCondition.EQUAL, ibaDefId), new int[] { 0 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(StringValue.class, StringValue.IBAHOLDER_REFERENCE + "."
				+ ObjectReference.KEY + "." + ObjectIdentifier.ID, SearchCondition.EQUAL, prjObid), new int[] { 0 });
		qs.setAdvancedQueryEnabled(true);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.hasMoreElements())
			return ((StringValue) qr.nextElement()).getValue();
		else
			return null;
	}

	// 用于得到整数型的软属性值
	private static Integer getIntegerIBAValueOfObject(IBAHolder ibaHolder, AttributeDefDefaultView addv, String ibaName)
			throws IBADefinitionException, NotAuthorizedException, RemoteException, WTException {
		long ibaDefId = addv.getObjectID().getId();
		long objObid = PersistenceHelper.getObjectIdentifier((Persistable) ibaHolder).getId();
		QuerySpec qs = new QuerySpec(IntegerValue.class);
		qs.appendWhere(new SearchCondition(IntegerValue.class, IntegerValue.DEFINITION_REFERENCE + "."
				+ ObjectReference.KEY + "." + ObjectIdentifier.ID, SearchCondition.EQUAL, ibaDefId), new int[] { 0 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(IntegerValue.class, IntegerValue.IBAHOLDER_REFERENCE + "."
				+ ObjectReference.KEY + "." + ObjectIdentifier.ID, SearchCondition.EQUAL, objObid), new int[] { 0 });
		qs.setAdvancedQueryEnabled(true);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.hasMoreElements()) {
			return Integer.valueOf((int) ((IntegerValue) qr.nextElement()).getValue());
		} else
			return null;
	}

	// 用于得到布尔型的软属性值
	private static Boolean getBooleanIBAValueOfObject(IBAHolder ibaHolder, AttributeDefDefaultView addv, String ibaName)
			throws IBADefinitionException, NotAuthorizedException, RemoteException, WTException {
		long ibaDefId = addv.getObjectID().getId();
		long objObid = PersistenceHelper.getObjectIdentifier((Persistable) ibaHolder).getId();
		QuerySpec qs = new QuerySpec(BooleanValue.class);
		qs.appendWhere(new SearchCondition(BooleanValue.class, BooleanValue.DEFINITION_REFERENCE + "."
				+ ObjectReference.KEY + "." + ObjectIdentifier.ID, SearchCondition.EQUAL, ibaDefId), new int[] { 0 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(BooleanValue.class, BooleanValue.IBAHOLDER_REFERENCE + "."
				+ ObjectReference.KEY + "." + ObjectIdentifier.ID, SearchCondition.EQUAL, objObid), new int[] { 0 });
		qs.setAdvancedQueryEnabled(true);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.hasMoreElements()) {
			return ((BooleanValue) qr.nextElement()).isValue();
		} else
			return null;
	}

	// 用于得到浮点型的软属性值
	private static Float getFloatIBAValueOfObject(IBAHolder ibaHolder, AttributeDefDefaultView addv, String ibaName)
			throws IBADefinitionException, NotAuthorizedException, RemoteException, WTException {
		long ibaDefId = addv.getObjectID().getId();
		long objObid = PersistenceHelper.getObjectIdentifier((Persistable) ibaHolder).getId();
		QuerySpec qs = new QuerySpec(FloatValue.class);
		qs.appendWhere(new SearchCondition(FloatValue.class, FloatValue.DEFINITION_REFERENCE + "."
				+ ObjectReference.KEY + "." + ObjectIdentifier.ID, SearchCondition.EQUAL, ibaDefId), new int[] { 0 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(FloatValue.class, FloatValue.IBAHOLDER_REFERENCE + "." + ObjectReference.KEY
				+ "." + ObjectIdentifier.ID, SearchCondition.EQUAL, objObid), new int[] { 0 });
		qs.setAdvancedQueryEnabled(true);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.hasMoreElements()) {
			return Float.valueOf((float) ((FloatValue) qr.nextElement()).getValue());
		} else
			return null;
	}

	// 用于得到时间型的软属性值
	private static Timestamp getTimestampIBAValueOfObject(IBAHolder ibaHolder, AttributeDefDefaultView addv,
			String ibaName) throws IBADefinitionException, NotAuthorizedException, RemoteException, WTException {
		long ibaDefId = addv.getObjectID().getId();
		long prjObid = PersistenceHelper.getObjectIdentifier((Persistable) ibaHolder).getId();
		QuerySpec qs = new QuerySpec(TimestampValue.class);
		qs.appendWhere(new SearchCondition(TimestampValue.class, TimestampValue.DEFINITION_REFERENCE + "."
				+ ObjectReference.KEY + "." + ObjectIdentifier.ID, SearchCondition.EQUAL, ibaDefId), new int[] { 0 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(TimestampValue.class, TimestampValue.IBAHOLDER_REFERENCE + "."
				+ ObjectReference.KEY + "." + ObjectIdentifier.ID, SearchCondition.EQUAL, prjObid), new int[] { 0 });
		qs.setAdvancedQueryEnabled(true);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.hasMoreElements()) {
			return ((TimestampValue) qr.nextElement()).getValue();
		} else
			return null;
	}

	/**
	 * 设置多个任意类型软属性
	 * 
	 * @author qianlong
	 * @date 2013-7-5
	 * @param ibaholder
	 * @param ibaMap
	 * @return
	 * 
	 */
	public static IBAHolder setAnyIBAValueOfObject(IBAHolder ibaholder, Map<String, String> ibaMap) {
		if (!RemoteMethodServer.ServerFlag) {
			String method = "setAnyIBAValueOfObject";
			Class[] types = { IBAHolder.class, Map.class };
			Object[] vals = { ibaholder, ibaMap };
			IBAHolder rtn = ibaholder;
			try {
				rtn = (IBAHolder) RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, vals);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return rtn;
		}
		boolean checkFlag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			for (String ibaName : ibaMap.keySet()) {
				String ibaValue = (String) ibaMap.get(ibaName);
				if (null == ibaValue || "null".equalsIgnoreCase(ibaValue))
					continue;

				AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
				if (addv == null) {
					throw new WTException("软属性'" + ibaName + "'不存在");
				} else {
					Object obj = null;
					if (addv instanceof StringDefView) {
						obj = new StringValueDefaultView((StringDefView) addv, String.valueOf(ibaValue));
					} else if (addv instanceof BooleanDefView) {
						boolean flag = Boolean.valueOf(ibaValue).booleanValue();
						obj = new BooleanValueDefaultView((BooleanDefView) addv, flag);
					} else if (addv instanceof IntegerDefView) {
						long l = Long.valueOf(ibaValue).longValue();
						obj = new IntegerValueDefaultView((IntegerDefView) addv, l);
					} else if (addv instanceof FloatDefView) {
						int i = ibaValue.lastIndexOf(",");
						String s4 = i <= 0 ? "2" : ibaValue.substring(i + 1);
						String s8 = i <= 0 ? ibaValue : ibaValue.substring(0, i);
						if (i <= 0) {
							int j = s8.lastIndexOf(".");
							if (j <= 0)
								s4 = "2";
							else
								s4 = String.valueOf(s8.substring(j).length());
						}
						System.out.println("s8"+s8);
						System.out.println("s4"+s4);
						obj = new FloatValueDefaultView((FloatDefView) addv, Double.valueOf(s8).doubleValue(), Integer
								.valueOf(s4).intValue());
					} else if (addv instanceof TimestampDefView) {
						Date dd = SoftAttributesHelper.parseDate(ibaValue, null, null);
						obj = new TimestampValueDefaultView((TimestampDefView) addv, new Timestamp(dd.getTime()));
					}
					if (obj != null) {
						ibaholder = IBAValueHelper.service.refreshAttributeContainer(ibaholder, "CSM", null, null);
						DefaultAttributeContainer defaultattributecontainer = (DefaultAttributeContainer) ibaholder
								.getAttributeContainer();
						if (defaultattributecontainer == null) {
							defaultattributecontainer = new DefaultAttributeContainer();
							defaultattributecontainer.addAttributeValue(((AbstractValueView) (obj)));
							ibaholder.setAttributeContainer(defaultattributecontainer);
						} else {
							AbstractValueView oldavv[] = defaultattributecontainer
									.getAttributeValues((AttributeDefDefaultView) addv);
							for (int k = 0; k < oldavv.length; k++)
								defaultattributecontainer.deleteAttributeValue(oldavv[k]);
							defaultattributecontainer.addAttributeValue((AbstractValueView) (obj));
							IBAValueDBServiceInterface dbService = (IBAValueDBServiceInterface) ManagerServiceFactory
									.getDefault().getManager(IBAValueDBService.class);
							defaultattributecontainer = (DefaultAttributeContainer) dbService.updateAttributeContainer(
									ibaholder, defaultattributecontainer.getConstraintParameter(), null, null);
						}
						ibaholder = (IBAHolder) IBAValueHelper.service.refreshAttributeContainer(ibaholder, "CSM",
								null, null);
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(checkFlag);
		}
		return ibaholder;
	}

	public static void main(String[] args) throws WTException, RemoteException, WTPropertyVetoException, ParseException {
		RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
		methodServer.setUserName("wcadmin");
		methodServer.setPassword("wcadmin");
//		try {
//			methodServer.invoke("set", IBAHelper1.class.getName(), null, null, null);
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		}

		// WTDocument document = (WTDocument)
		// Util.getObjectByOid(WTDocument.class, "1280073");
		// Date startDate = new Date();
		// System.out.println(getAnyIBAValueOfObject(document, "backupReason"));
		//
		// Date endDate = new Date();
		// System.out.println(endDate.getTime() - startDate.getTime());
		//		
		// Date startDate1 = new Date();
		// IBAHelper helper=new IBAHelper(document);
		// System.out.println(helper.getIBAValue("backupReason"));
		//
		// Date endDate1 = new Date();
		// System.out.println(endDate1.getTime() - startDate1.getTime());

		WTPart document = (WTPart) Util.getObjectByOid(WTPart.class, "1280307");
//		IBAHelper1 helper1 = new IBAHelper1(document);
//		System.out.println(helper1.getIBAValue("maxBackupCount"));
//		System.out.println(helper1.getIBADisplayValue("maxBackupCount"));

		 Map<String, String> ibaMap = new HashMap<String, String>();
		 ibaMap.put("maxBackupCount", "2121");
		 Date startDate = new Date();
		 setAnyIBAValueOfObject(document, ibaMap);
		 Date endDate = new Date();
		 System.out.println(endDate.getTime() - startDate.getTime());

	}
	
}
