package com.ptc.extend.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import wt.folder.Cabinet;
import wt.iba.definition.litedefinition.AbstractAttributeDefinizerView;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.litedefinition.BooleanDefView;
import wt.iba.definition.litedefinition.FloatDefView;
import wt.iba.definition.litedefinition.IntegerDefView;
import wt.iba.definition.litedefinition.StringDefView;
import wt.iba.definition.litedefinition.TimestampDefView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.IBAHolder;
import wt.iba.value.litevalue.AbstractValueView;
import wt.iba.value.litevalue.BooleanValueDefaultView;
import wt.iba.value.litevalue.FloatValueDefaultView;
import wt.iba.value.litevalue.IntegerValueDefaultView;
import wt.iba.value.litevalue.StringValueDefaultView;
import wt.iba.value.service.IBAValueDBService;
import wt.iba.value.service.IBAValueDBServiceInterface;
import wt.iba.value.service.IBAValueHelper;
import wt.introspection.ReflectionHelper;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.services.ManagerServiceFactory;
import wt.session.SessionServerHelper;
import wt.vc.Iterated;
import wt.vc.Versioned;

import com.ptc.core.foundation.type.server.impl.SoftAttributesHelper;

public class ObjectProperty implements RemoteAccess {
	static String CLASSNAME = ObjectProperty.class.getName();

	public static Cabinet getDefaultCabinet(Object obj) {
		Cabinet cabinet = null;
		if (obj != null)
			try {
				Class class1 = obj.getClass();
				Method method = class1.getMethod("getDefaultCabinet",
						new Class[0]);
				cabinet = (Cabinet) method.invoke(obj, new Object[0]);
			} catch (Exception exception) {
				Debug.info(obj.toString() + " is not a cabinet holder");
				exception.printStackTrace();
			}
		return cabinet;
	}

	public static String getName(Object obj) {
		String name = null;
		if (obj != null)
			try {
				Class class1 = obj.getClass();
				Method method = class1.getMethod("getName", new Class[0]);
				name = (String) method.invoke(obj, new Object[0]);
			} catch (Exception exception) {
				Debug.info(obj.toString() + " has no name");
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
				Debug.info(obj.toString() + " has no number");
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

	public static String getObjectDisplay(Object obj) {
		if (obj == null)
			return "null";
		StringBuilder sb = new StringBuilder();
		sb.append("number=<").append(getNumber(obj)).append("> ");
		sb.append("name=<").append(getName(obj)).append("> ");
		sb.append("ver=<").append(getVersionIterationDisplay(obj)).append("> ");
		return sb.toString();
	}

	public static Object getProperty(Object obj, String s) {
		try {
			Method method = ReflectionHelper.getReadMethod(obj.getClass(), s);
			if (method != null) {
				return method.invoke(obj, new Object[0]);
			}
			return null;
		} catch (NoSuchMethodException nosuchmethodexception) {
			nosuchmethodexception.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static IBAHolder setObjectIBAValueNoCheckout(IBAHolder ibaholder,
			String attrName, String attrValue) {
		if (!RemoteMethodServer.ServerFlag) {
			String method = "setObjectIBAValueNoCheckout";
			Class[] types = { IBAHolder.class, String.class, String.class };
			Object[] vals = { ibaholder, attrName, attrValue };
			IBAHolder rtn = ibaholder;
			try {
				rtn = (IBAHolder) RemoteMethodServer.getDefault().invoke(
						method, CLASSNAME, null, types, vals);
			} catch (Exception e) {
				Debug.info("setObjectIBAValue() Exception:", e);
				e.printStackTrace();
			}
			return rtn;
		}
		boolean checkFlag = SessionServerHelper.manager
				.setAccessEnforced(false);
		try {
			AbstractAttributeDefinizerView aadv = IBADefinitionHelper.service
					.getAttributeDefDefaultViewByPath(attrName);
			if (aadv == null) {
				Debug.info("Not found IBA=" + attrName + "] Definition.");
			} else {
				Object obj = null;
				if ((aadv instanceof StringDefView)) {
					obj = new StringValueDefaultView((StringDefView) aadv,
							String.valueOf(attrValue));
				} else if ((aadv instanceof BooleanDefView)) {
					boolean flag = Boolean.valueOf(attrValue).booleanValue();
					obj = new BooleanValueDefaultView((BooleanDefView) aadv,
							flag);
				} else if ((aadv instanceof IntegerDefView)) {
					long l = Long.valueOf(attrValue).longValue();
					obj = new IntegerValueDefaultView((IntegerDefView) aadv, l);
				} else if ((aadv instanceof FloatDefView)) {
					int i = attrValue.lastIndexOf(",");
					String s4 = i <= 0 ? "2" : attrValue.substring(i + 1);
					String s8 = i <= 0 ? attrValue : attrValue.substring(0, i);
					if (i <= 0) {
						int j = s8.lastIndexOf(".");
						if (j <= 0)
							s4 = "2";
						else
							s4 = String.valueOf(s8.substring(j).length());
					}
					obj = new FloatValueDefaultView((FloatDefView) aadv, Double
							.valueOf(s8).doubleValue(), Integer.valueOf(s4)
							.intValue());
				} else if ((aadv instanceof TimestampDefView)) {
					try {
						Date dd = SoftAttributesHelper.parseDate(attrValue,
								Locale.SIMPLIFIED_CHINESE,
								TimeZone.getTimeZone("GMT+8:00"));
						obj = new Timestamp(dd.getTime());
					} catch (ParseException parseexception) {
						parseexception.printStackTrace();
					}
				}
				if (obj != null) {
					ibaholder = IBAValueHelper.service
							.refreshAttributeContainer(ibaholder, "CSM", null,
									null);
					DefaultAttributeContainer defaultattributecontainer = (DefaultAttributeContainer) ibaholder
							.getAttributeContainer();
					if (defaultattributecontainer == null) {
						defaultattributecontainer = new DefaultAttributeContainer();
						defaultattributecontainer
								.addAttributeValue((AbstractValueView) obj);
						ibaholder
								.setAttributeContainer(defaultattributecontainer);
					} else {
						AbstractValueView[] oldavv = defaultattributecontainer
								.getAttributeValues((AttributeDefDefaultView) aadv);
						for (int k = 0; k < oldavv.length; k++)
							defaultattributecontainer
									.deleteAttributeValue(oldavv[k]);
						defaultattributecontainer
								.addAttributeValue((AbstractValueView) obj);
						IBAValueDBServiceInterface dbService = (IBAValueDBServiceInterface) ManagerServiceFactory
								.getDefault().getManager(
										IBAValueDBService.class);
						defaultattributecontainer = (DefaultAttributeContainer) dbService
								.updateAttributeContainer(ibaholder,
										defaultattributecontainer
												.getConstraintParameter(),
										null, null);
					}
					ibaholder = IBAValueHelper.service
							.refreshAttributeContainer(ibaholder, "CSM", null,
									null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.info("setObjectIBAValue() Exception:", e);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(checkFlag);
		}
		return ibaholder;
	}
}