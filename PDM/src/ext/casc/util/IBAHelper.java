package ext.casc.util;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import wt.fc.ObjectIdentifier;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.iba.definition.litedefinition.AbstractAttributeDefinizerNodeView;
import wt.iba.definition.litedefinition.AbstractAttributeDefinizerView;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.litedefinition.AttributeDefNodeView;
import wt.iba.definition.litedefinition.AttributeOrgNodeView;
import wt.iba.definition.litedefinition.BooleanDefView;
import wt.iba.definition.litedefinition.FloatDefView;
import wt.iba.definition.litedefinition.IntegerDefView;
import wt.iba.definition.litedefinition.StringDefView;
import wt.iba.definition.litedefinition.TimestampDefView;
import wt.iba.definition.litedefinition.UnitDefView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.BooleanValue;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.IBAHolder;
import wt.iba.value.IBAValueUtility;
import wt.iba.value.IntegerValue;
import wt.iba.value.StringValue;
import wt.iba.value.TimestampValue;
import wt.iba.value.litevalue.AbstractValueView;
import wt.iba.value.litevalue.BooleanValueDefaultView;
import wt.iba.value.litevalue.FloatValueDefaultView;
import wt.iba.value.litevalue.IntegerValueDefaultView;
import wt.iba.value.litevalue.StringValueDefaultView;
import wt.iba.value.litevalue.TimestampValueDefaultView;
import wt.iba.value.litevalue.UnitValueDefaultView;
import wt.iba.value.service.IBAValueDBService;
import wt.iba.value.service.IBAValueDBServiceInterface;
import wt.iba.value.service.IBAValueHelper;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ManagerServiceFactory;
import wt.session.SessionServerHelper;
import wt.type.TypedUtility;
import wt.util.WTException;

import com.ptc.core.foundation.type.server.impl.SoftAttributesHelper;
import com.ptc.core.lwc.common.view.AttributeDefinitionReadView;
import com.ptc.core.lwc.common.view.ConstraintDefinitionReadView;
import com.ptc.core.lwc.common.view.ConstraintDefinitionReadView.RuleDataObject;
import com.ptc.core.lwc.common.view.ConstraintRuleDefinitionReadView;
import com.ptc.core.lwc.common.view.EnumerationDefinitionReadView;
import com.ptc.core.lwc.common.view.EnumerationEntryReadView;
import com.ptc.core.lwc.common.view.EnumerationMembershipReadView;
import com.ptc.core.lwc.common.view.PropertyValueReadView;
import com.ptc.core.lwc.common.view.TypeDefinitionReadView;
import com.ptc.core.lwc.server.TypeDefinitionServiceHelper;
import com.ptc.core.meta.common.DiscreteSet;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;

public class IBAHelper implements Serializable, RemoteAccess {

    // --- Attribute Section ---

    private static final String CLASSNAME = IBAHelper.class.getName();
    private static Locale LOCALE = Locale.CHINA;

    /**
     *
     * @param object
     *            Object to be validated
     * @return String
     * @exception wt.util.WTException
     **/

    public static Hashtable getAllIBAValues(WTObject obj)
            throws WTException {

        Hashtable hashtable = new Hashtable();

        try {
            if (obj instanceof IBAHolder) {
                IBAHolder ibaholder = (IBAHolder) obj;
                DefaultAttributeContainer dac = getContainer(ibaholder);

                if (dac != null) {
                    AbstractValueView avv[] = null;
                    avv = dac.getAttributeValues();

                    for (int j = 0; j < avv.length; j++) {
                        String thisIBAName = avv[j].getDefinition().getName();
                        String thisIBAValue = IBAValueUtility.getLocalizedIBAValueDisplayString
                                (avv[j], LOCALE);
                        String thisIBAClass = (avv[j].getDefinition()).
                                getAttributeDefinitionClassName();
                        if (thisIBAClass.equals("wt.iba.definition.FloatDefinition")) {
                            float value = (float) ((FloatValueDefaultView) avv[j]).getValue();
                            hashtable.put(thisIBAName, new Float(value));
                        } else if (thisIBAClass.equals("wt.iba.definition.IntegerDefinition")) {
                            long value = ((IntegerValueDefaultView) avv[j]).getValue();
                            hashtable.put(thisIBAName, String.valueOf(value));
                        } else if (thisIBAClass.equals("wt.iba.definition.StringDefinition")) {
                            String value = ((StringValueDefaultView) avv[j]).getValue();
                            hashtable.put(thisIBAName, value);
                        }
                    }
                }
            }
        } catch (RemoteException rexp) {
            System.out.println(" ** !!!!! ** ERROR Getting IBAHelper.getAllIBAValues");
            rexp.printStackTrace();
        }

        return hashtable;
    }

    /**
     *
     * @param object
     *            Object to be validated
     * @return String
     * @exception wt.util.WTException
     **/

    public static String getIBAStringValue(WTObject obj, String ibaName)
            throws WTException {

        String value = null;
        String ibaClass = "wt.iba.definition.StringDefinition";

        try {
            if (obj instanceof IBAHolder) {
                IBAHolder ibaholder = (IBAHolder) obj;
                DefaultAttributeContainer defaultattributecontainer = getContainer(ibaholder);
                if (defaultattributecontainer != null) {
                    AbstractValueView avv = getIBAValueView(defaultattributecontainer, ibaName, ibaClass);
                    if (avv != null) {
                        value = ((StringValueDefaultView) avv).getLocalizedDisplayString();
                    }
                }
            }
        } catch (RemoteException rexp) {
            rexp.printStackTrace();
        }

        return value;

    }

    public static String getStringIBAValueOfObject(IBAHolder p, String ibaName) {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "getStringIBAValueOfObject";
            Class[] types = { IBAHolder.class, String.class };
            Object[] vals = { p, ibaName };
            try {
                return (String) RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, vals);
            } catch (Exception e) {
                // Debug.P("getStringIBAValueOfObject() Exception:", e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
        boolean access = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
            if (addv == null) {
                // Debug.P("Not found Attribute ["+ibaName+"] defined.");
                return null;
            }
            long ibaDefId = addv.getObjectID().getId();
            long prjObid = PersistenceHelper.getObjectIdentifier((Persistable) p).getId();
            QuerySpec qs = new QuerySpec(StringValue.class);
            qs.appendWhere(new SearchCondition(StringValue.class, StringValue.DEFINITION_REFERENCE + "."
                    + ObjectReference.KEY + "." + ObjectIdentifier.ID,
                    SearchCondition.EQUAL, ibaDefId), new int[] { 0 });
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(StringValue.class, StringValue.IBAHOLDER_REFERENCE + "."
                    + ObjectReference.KEY + "." + ObjectIdentifier.ID,
                    SearchCondition.EQUAL, prjObid), new int[] { 0 });
            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements())
                return ((StringValue) qr.nextElement()).getValue();
            else return null;
        } catch (Exception e) {
            // Debug.P("Get IBA Value Exception.", e);
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(access);
        }
        return null;
    }

    public static Integer getIntegerIBAValueOfObject(IBAHolder p, String ibaName) {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "getIntegerIBAValueOfObject";
            Class[] types = { IBAHolder.class, String.class };
            Object[] vals = { p, ibaName };
            try {
                return (Integer) RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, vals);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        boolean access = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
            if (addv == null) {
                // Debug.P("Not found Attribute ["+ibaName+"] defined.");
                return null;
            }
            long ibaDefId = addv.getObjectID().getId();
            long objObid = PersistenceHelper.getObjectIdentifier((Persistable) p).getId();
            QuerySpec qs = new QuerySpec(IntegerValue.class);
            qs.appendWhere(new SearchCondition(IntegerValue.class, IntegerValue.DEFINITION_REFERENCE + "."
                    + ObjectReference.KEY + "." + ObjectIdentifier.ID,
                    SearchCondition.EQUAL, ibaDefId), new int[] { 0 });
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(IntegerValue.class, IntegerValue.IBAHOLDER_REFERENCE + "."
                    + ObjectReference.KEY + "." + ObjectIdentifier.ID,
                    SearchCondition.EQUAL, objObid), new int[] { 0 });
            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                return Integer.valueOf((int) ((IntegerValue) qr.nextElement()).getValue());
            } else return null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(access);
        }
        return null;
    }

    public static Boolean getBooleanIBAValueOfObject(IBAHolder p, String ibaName) {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "getBooleanIBAValueOfObject";
            Class[] types = { IBAHolder.class, String.class };
            Object[] vals = { p, ibaName };
            try {
                return (Boolean) RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, vals);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        boolean access = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
            if (addv == null) {
                return null;
            }
            long ibaDefId = addv.getObjectID().getId();
            long objObid = PersistenceHelper.getObjectIdentifier((Persistable) p).getId();
            QuerySpec qs = new QuerySpec(BooleanValue.class);
            qs.appendWhere(new SearchCondition(BooleanValue.class, BooleanValue.DEFINITION_REFERENCE + "."
                    + ObjectReference.KEY + "." + ObjectIdentifier.ID,
                    SearchCondition.EQUAL, ibaDefId), new int[] { 0 });
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(BooleanValue.class, BooleanValue.IBAHOLDER_REFERENCE + "."
                    + ObjectReference.KEY + "." + ObjectIdentifier.ID,
                    SearchCondition.EQUAL, objObid), new int[] { 0 });
            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                Object obj = qr.nextElement();
                return Boolean.valueOf(((BooleanValue) obj).isValue());
            } else return null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(access);
        }
        return null;
    }

    /**
     * get string or date IBA
     *
     * @param p
     * @param ibaName
     * @return
     */
    public static String getIBAValueOfObject(IBAHolder p, String ibaName) {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "getIBAValueOfObject";
            Class[] types = { IBAHolder.class, String.class };
            Object[] vals = { p, ibaName };
            try {
                return (String) RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, vals);
            } catch (Exception e) {
                // Debug.P("getStringIBAValueOfObject() Exception:", e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
        boolean access = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            AbstractAttributeDefinizerView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
            if (addv == null) {
                return null;
            }
            if (addv instanceof StringDefView) {
                return getStringIBAValueOfObject(p, ibaName);
            } else if (addv instanceof TimestampDefView) {
                long ibaDefId = addv.getObjectID().getId();
                long prjObid = PersistenceHelper.getObjectIdentifier((Persistable) p).getId();
                QuerySpec qs = new QuerySpec(TimestampValue.class);
                qs.appendWhere(new SearchCondition(TimestampValue.class, TimestampValue.DEFINITION_REFERENCE + "."
                        + ObjectReference.KEY + "." + ObjectIdentifier.ID,
                            SearchCondition.EQUAL, ibaDefId), new int[] { 0 });
                qs.appendAnd();
                qs.appendWhere(new SearchCondition(TimestampValue.class, TimestampValue.IBAHOLDER_REFERENCE + "."
                        + ObjectReference.KEY + "." + ObjectIdentifier.ID,
                            SearchCondition.EQUAL, prjObid), new int[] { 0 });
                qs.setAdvancedQueryEnabled(true);
                QueryResult qr = PersistenceHelper.manager.find(qs);
                if (qr.hasMoreElements()) {
                    TimestampValue timeValue = (TimestampValue) qr.nextElement();
                    Timestamp time = timeValue.getValue();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    // SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd",locale.CHINA);
                    String value = sdf.format(new Timestamp(time.getTime() + 8 * 60 * 60 * 1000));
                    return value;
                } else return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(access);
        }
        return null;
    }

    /**
     * Add by Helay for set IBA values
     *
     * @param ibaholder
     * @param properties
     * @return
     */
    public static IBAHolder setIBAValues(IBAHolder ibaholder, Properties ibaValues) {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "setIBAValues";
            Class[] types = { IBAHolder.class, Properties.class };
            Object[] vals = { ibaholder, ibaValues };
            IBAHolder rtn = ibaholder;
            try {
                rtn = (IBAHolder) RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, vals);
            } catch (Exception e) {
                // Debug.I("setObjectIBAValue() Exception:", e);
                e.printStackTrace();
            }
            return rtn;
        }
        boolean checkFlag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            for (Enumeration en = ibaValues.keys(); en.hasMoreElements();) {
                String attrName = (String) en.nextElement();
                String attrValue = (String) ibaValues.get(attrName);
                if (attrValue == null) // null ==> 使锟斤拷默锟斤拷值
                    continue;

                AbstractAttributeDefinizerView aadv = IBADefinitionHelper.service
                        .getAttributeDefDefaultViewByPath(attrName);
                if (aadv == null) {
                    System.out.println("Not found IBA=" + attrName + "] Definition.");
                } else {
                    Object obj = null;
                    if (aadv instanceof StringDefView) {
                        obj = new StringValueDefaultView((StringDefView) aadv, String.valueOf(attrValue));
                    } else if (aadv instanceof BooleanDefView) {
                        boolean flag = Boolean.valueOf(attrValue).booleanValue();
                        obj = new BooleanValueDefaultView((BooleanDefView) aadv, flag);
                    } else if (aadv instanceof IntegerDefView) {
                        long l = Long.valueOf(attrValue).longValue();
                        obj = new IntegerValueDefaultView((IntegerDefView) aadv, l);
                    } else if (aadv instanceof FloatDefView) {
                        int i = attrValue.lastIndexOf(",");
                        String s4 = i <= 0 ? "2" : attrValue.substring(i + 1);
                        String s8 = i <= 0 ? attrValue : attrValue.substring(0, i);
                        if (i <= 0) {
                            int j = s8.lastIndexOf(".");
                            if (j <= 0)
                                s4 = "2";
                            else s4 = String.valueOf(s8.substring(j).length());
                        }
                        obj = new FloatValueDefaultView((FloatDefView) aadv, Double.valueOf(s8).doubleValue(), Integer
                                .valueOf(s4).intValue());
                    } else if (aadv instanceof TimestampDefView) {
                        try {
                            // java.util.Date dd=SoftAttributesHelper.parseDate(attrValue, Locale.SIMPLIFIED_CHINESE,
                            // TimeZone.getTimeZone("GMT+8:00"));
                            java.util.Date dd = SoftAttributesHelper.parseDate(attrValue, null, null);
                            obj = new TimestampValueDefaultView((TimestampDefView) aadv, new Timestamp(dd.getTime()));
                        } catch (ParseException parseexception) {
                            parseexception.printStackTrace();
                        }
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
                                    .getAttributeValues((AttributeDefDefaultView) aadv);
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

    public static IBAHolder setObjectIBAValueNoCheckout(IBAHolder ibaholder, String attrName, String attrValue) {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "setObjectIBAValueNoCheckout";
            Class[] types = { IBAHolder.class, String.class, String.class };
            Object[] vals = { ibaholder, attrName, attrValue };
            IBAHolder rtn = ibaholder;
            try {
                rtn = (IBAHolder) RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, vals);
            } catch (Exception e) {
                // Debug.I("setObjectIBAValue() Exception:", e);
                e.printStackTrace();
            }
            return rtn;
        }
        boolean checkFlag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            AbstractAttributeDefinizerView aadv = IBADefinitionHelper.service
                    .getAttributeDefDefaultViewByPath(attrName);
            if (aadv == null) {
                // Debug.I("Not found IBA="+attrName+"] Definition.");
            } else {
                Object obj = null;
                if (aadv instanceof StringDefView) {
                    obj = new StringValueDefaultView((StringDefView) aadv, String.valueOf(attrValue));
                } else if (aadv instanceof BooleanDefView) {
                    boolean flag = Boolean.valueOf(attrValue).booleanValue();
                    obj = new BooleanValueDefaultView((BooleanDefView) aadv, flag);
                } else if (aadv instanceof IntegerDefView) {
                    long l = Long.valueOf(attrValue).longValue();
                    obj = new IntegerValueDefaultView((IntegerDefView) aadv, l);
                } else if (aadv instanceof FloatDefView) {
                    int i = attrValue.lastIndexOf(",");
                    String s4 = i <= 0 ? "2" : attrValue.substring(i + 1);
                    String s8 = i <= 0 ? attrValue : attrValue.substring(0, i);
                    if (i <= 0) {
                        int j = s8.lastIndexOf(".");
                        if (j <= 0)
                            s4 = "2";
                        else s4 = String.valueOf(s8.substring(j).length());
                    }
                    obj = new FloatValueDefaultView((FloatDefView) aadv, Double.valueOf(s8).doubleValue(), Integer
                            .valueOf(s4).intValue());
                } else if (aadv instanceof TimestampDefView) {
                    try {
                        // java.util.Date dd=SoftAttributesHelper.parseDate(attrValue, Locale.SIMPLIFIED_CHINESE,
                        // TimeZone.getTimeZone("GMT+8:00"));
                        java.util.Date dd = SoftAttributesHelper.parseDate(attrValue, null, null);
                        obj = new TimestampValueDefaultView((TimestampDefView) aadv, new Timestamp(dd.getTime()));
                    } catch (ParseException parseexception) {
                        parseexception.printStackTrace();
                    }
                } else if (aadv instanceof UnitDefView) {
                    StringBuilder sb = new StringBuilder();
                    for (char c : attrValue.toCharArray()) {
                        if (Character.isDigit(c) || c == '.') {
                            sb.append(c);
                        } else {
                            break;
                        }
                    }
                    obj = new UnitValueDefaultView((UnitDefView) aadv, Double.parseDouble(sb.toString()), -1);
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
                                .getAttributeValues((AttributeDefDefaultView) aadv);
                        for (int k = 0; k < oldavv.length; k++)
                            defaultattributecontainer.deleteAttributeValue(oldavv[k]);
                        defaultattributecontainer.addAttributeValue((AbstractValueView) (obj));
                        IBAValueDBServiceInterface dbService = (IBAValueDBServiceInterface) ManagerServiceFactory
                                .getDefault().getManager(IBAValueDBService.class);
                        defaultattributecontainer = (DefaultAttributeContainer) dbService.updateAttributeContainer(
                                ibaholder, defaultattributecontainer.getConstraintParameter(), null, null);
                    }
                    ibaholder = (IBAHolder) IBAValueHelper.service.refreshAttributeContainer(ibaholder, "CSM", null,
                            null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Debug.I("setObjectIBAValue() Exception:", e);
        } finally {
            SessionServerHelper.manager.setAccessEnforced(checkFlag);
        }
        return ibaholder;
    }

    public static String setIBAStringValue(WTObject obj, String ibaName, String newvalue)
            throws WTException {

        String value = null;
        String ibaClass = "wt.iba.definition.StringDefinition";
        try {
            if (obj instanceof IBAHolder) {
                IBAHolder ibaholder = (IBAHolder) obj;
                DefaultAttributeContainer defaultattributecontainer = getContainer(ibaholder);
                if (defaultattributecontainer != null) {
                    StringValueDefaultView avv = (StringValueDefaultView) getIBAValueView(defaultattributecontainer,
                            ibaName, ibaClass);
                    if (avv != null) {
                        avv.setValue(newvalue);
                        defaultattributecontainer.updateAttributeValue(avv);
                        ibaholder.setAttributeContainer(defaultattributecontainer);
                        wt.iba.value.service.LoadValue.applySoftAttributes(ibaholder);
                    } else {
                        AbstractAttributeDefinizerView aadv = IBADefinitionHelper.service
                                .getAttributeDefDefaultViewByPath(ibaName);
                        avv = new StringValueDefaultView((StringDefView) aadv, String.valueOf(newvalue));
                        defaultattributecontainer.addAttributeValue(avv);
                        ibaholder.setAttributeContainer(defaultattributecontainer);
                        wt.iba.value.service.LoadValue.applySoftAttributes(ibaholder);
                    }
                }
            }
        } catch (Exception rexp) {

            rexp.printStackTrace();
        }

        return value;

    }

    public static String setIBAStringValue(WTObject obj, String ibaName, String path, String newvalue)
            throws WTException {

        String value = null;
        String ibaClass = "wt.iba.definition.StringDefinition";
        try {
            if (obj instanceof IBAHolder) {
                IBAHolder ibaholder = (IBAHolder) obj;
                DefaultAttributeContainer defaultattributecontainer = getContainer(ibaholder);
                if (defaultattributecontainer != null) {
                    StringValueDefaultView avv = (StringValueDefaultView) getIBAValueView(defaultattributecontainer,
                            ibaName, ibaClass);
                    if (avv != null) {
                        avv.setValue(newvalue);
                        defaultattributecontainer.updateAttributeValue(avv);

                    } else {

                        StringDefView adv = getStringDefViewByPath(path);
                        avv = new StringValueDefaultView(adv);
                        avv.setValue(newvalue);
                        defaultattributecontainer.addAttributeValue(avv);

                    }
                    ibaholder.setAttributeContainer(defaultattributecontainer);
                    wt.iba.value.service.LoadValue.applySoftAttributes(ibaholder);
                }
            }
        } catch (Exception rexp) {
            rexp.printStackTrace();
        }

        return value;

    }

    public static void copySingleValueAttrs(IBAHolder src, IBAHolder dest) throws RemoteException, WTException {
        DefaultAttributeContainer srccont = getContainer(src);
        DefaultAttributeContainer destcont = getContainer(dest);
        AbstractValueView[] srcvals = srccont.getAttributeValues();
        for (AbstractValueView srcval : srcvals) {
            AttributeDefDefaultView srcdef = srcval.getDefinition();
            destcont.deleteAttributeValues(srcdef);
            destcont.addAttributeValue(srcval);
        }
        dest.setAttributeContainer(destcont);
        IBAValueDBServiceInterface dbService =
                (IBAValueDBServiceInterface) ManagerServiceFactory.getDefault().getManager(IBAValueDBService.class);
        destcont = (DefaultAttributeContainer) dbService.updateAttributeContainer(
                dest, destcont.getConstraintParameter(), null, null);

    }

    public static DefaultAttributeContainer getContainer(IBAHolder ibaholder)
            throws WTException, RemoteException {

        ibaholder = IBAValueHelper.service.refreshAttributeContainerWithoutConstraints(ibaholder);
        DefaultAttributeContainer defaultattributecontainer = (DefaultAttributeContainer) ibaholder
                .getAttributeContainer();

        return defaultattributecontainer;
    }

    public static AbstractValueView getIBAValueView(DefaultAttributeContainer dac, String ibaName, String ibaClass)
            throws WTException {

        AbstractValueView aabstractvalueview[] = null;
        AbstractValueView avv = null;

        aabstractvalueview = dac.getAttributeValues();
        for (int j = 0; j < aabstractvalueview.length; j++) {
            String thisIBAName = aabstractvalueview[j].getDefinition().getName();
            String thisIBAValue = IBAValueUtility.getLocalizedIBAValueDisplayString(aabstractvalueview[j], LOCALE);
            String thisIBAClass = (aabstractvalueview[j].getDefinition()).getAttributeDefinitionClassName();
            if (thisIBAName.equals(ibaName) && thisIBAClass.equals(ibaClass)) {
                avv = aabstractvalueview[j];
                break;
            }
        }
        return avv;
    }

    public static StringDefView getStringDefViewByPath(String path)
            throws Exception {

        StringTokenizer stringtokenizer = new StringTokenizer(path, "/");

        String node1, node2, ibaName;
        node1 = stringtokenizer.nextToken();
        node2 = stringtokenizer.nextToken();
        ibaName = stringtokenizer.nextToken();

        AttributeOrgNodeView nodeview1 = getAttributeOrganizer(node1);
        AbstractAttributeDefinizerNodeView nodeview2 = getAttributeChildren(nodeview1, node2);
        AbstractAttributeDefinizerNodeView ibaDefNode = getAttributeChildren(nodeview2, ibaName);
        AttributeDefDefaultView adv = IBADefinitionHelper.service
                .getAttributeDefDefaultView((AttributeDefNodeView) ibaDefNode);

        if (adv instanceof StringDefView)
            return (StringDefView) adv;
        else return null;
    }

    private static AttributeOrgNodeView getAttributeOrganizer(String s) {
        int i;
        AttributeOrgNodeView aattributeorgnodeview[] = null;
        try {
            aattributeorgnodeview = IBADefinitionHelper.service.getAttributeOrganizerRoots();
            for (i = 0; i < aattributeorgnodeview.length; i++) {
                if (aattributeorgnodeview[i] == null)
                    continue;
                if (aattributeorgnodeview[i].getName().equalsIgnoreCase(s))
                    return aattributeorgnodeview[i];
            }
        } catch (java.rmi.RemoteException remoteexception) {
            remoteexception.printStackTrace();
        } catch (WTException wte) {
            wte.printStackTrace();
        }

        return null;
    }

    private static AbstractAttributeDefinizerNodeView getAttributeChildren(
            AbstractAttributeDefinizerNodeView ibaDefNode, String s) {
        int i;
        AbstractAttributeDefinizerNodeView aattributeorgnodeview[] = null;
        try {
            aattributeorgnodeview = IBADefinitionHelper.service.getAttributeChildren(ibaDefNode);
            for (i = 0; i < aattributeorgnodeview.length; i++) {
                if (aattributeorgnodeview[i] == null)
                    continue;
                if (aattributeorgnodeview[i].getName().equalsIgnoreCase(s))
                    return aattributeorgnodeview[i];
            }
        } catch (java.rmi.RemoteException remoteexception) {
            remoteexception.printStackTrace();
        } catch (WTException wte) {
            wte.printStackTrace();
        }

        return null;
    }

    private static AttributeDefDefaultView getDefaultViewObject(Object obj) {
        AttributeDefDefaultView attributedefdefaultview = null;
        try {
            if (obj instanceof AttributeDefNodeView)
                attributedefdefaultview = IBADefinitionHelper.service
                        .getAttributeDefDefaultView((AttributeDefNodeView) obj);
        } catch (java.rmi.RemoteException remoteexception) {
            remoteexception.printStackTrace();
        } catch (WTException wte) {
            wte.printStackTrace();
        }

        return attributedefdefaultview;
    }

    public static ArrayList<String> getSoftTypeIBAValues(String softtype, String attrname) {
        ArrayList<String> result = new ArrayList<String>();
        String pSoftType = softtype.substring(softtype.lastIndexOf("|") + 1);
        TypeIdentifier ti = TypedUtility.getTypeIdentifier(pSoftType);
        try {
            TypeDefinitionReadView tv = TypeDefinitionServiceHelper.service.getTypeDefView(ti);
            AttributeDefinitionReadView av = tv.getAttributeByName(attrname);
            if (av != null) {
                Collection<ConstraintDefinitionReadView> constraints = av.getAllConstraints();
                for (ConstraintDefinitionReadView constraint : constraints) {
                    ConstraintRuleDefinitionReadView constraintRuleDefReadView = constraint.getRule();
                    if (constraintRuleDefReadView != null) {
                        String constraintRuleKey = constraintRuleDefReadView.getKey();
                        if (constraintRuleKey != null) {
                            RuleDataObject rdo = constraint.getRuleDataObj();
                            if (rdo != null) {
                                Serializable edvSeries = rdo.getRuleData();
                                EnumerationDefinitionReadView edv = rdo.getEnumDef();
                                if (edvSeries != null) {
                                    if (!(edvSeries instanceof DiscreteSet)) {
                                        continue;
                                    } else {
                                        DiscreteSet disc = (DiscreteSet) edvSeries;
                                        Object[] members = disc.getElements();
                                        for (Object member : members) {
                                            result.add(member.toString());
                                        }
                                    }
                                } else if (edv != null) {
                                    Map<String, EnumerationEntryReadView> entryViewMap = edv.getAllEnumerationEntries();
                                    for (String key : entryViewMap.keySet()) {
                                        EnumerationMembershipReadView emv = edv.getMembershipByName(key);
                                        EnumerationEntryReadView member = emv.getMember();
                                        PropertyValueReadView readView = member.getPropertyValueByName("displayName");
                                        if (readView != null) {
                                            result.add(readView.getValue().toString());
                                        }
                                    }

                                }
                            }
                        }
                    }

                }
            }
        } catch (WTException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getSoftType(WTObject obj)
            throws WTException {
        String typename = "";
        TypeIdentifier type = TypeIdentifierUtility.getTypeIdentifier(obj);
        typename = type.getTypename();
        int nIndex1 = typename.lastIndexOf("|");
        int nIndex2 = typename.lastIndexOf(".");
        int nIndex = nIndex1 > nIndex2 ? nIndex1 : nIndex2;

        if (nIndex >= 0) {
            typename = typename.substring(nIndex + 1);
        }
        return typename;
    }

}
