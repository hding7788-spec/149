package com.glaway.mpm.pbombuilder.util;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.Vector;

import wt.fc.ObjectIdentifier;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.iba.constraint.IBAConstraintException;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.litedefinition.BooleanDefView;
import wt.iba.definition.litedefinition.FloatDefView;
import wt.iba.definition.litedefinition.IntegerDefView;
import wt.iba.definition.litedefinition.RatioDefView;
import wt.iba.definition.litedefinition.ReferenceDefView;
import wt.iba.definition.litedefinition.StringDefView;
import wt.iba.definition.litedefinition.TimestampDefView;
import wt.iba.definition.litedefinition.URLDefView;
import wt.iba.definition.litedefinition.UnitDefView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.definition.service.IBADefinitionObjectsFactory;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.DefaultAttributeContainerHelper;
import wt.iba.value.IBAHolder;
import wt.iba.value.StringValue;
import wt.iba.value.litevalue.AbstractContextualValueDefaultView;
import wt.iba.value.litevalue.AbstractValueView;
import wt.iba.value.litevalue.BooleanValueDefaultView;
import wt.iba.value.litevalue.FloatValueDefaultView;
import wt.iba.value.litevalue.IntegerValueDefaultView;
import wt.iba.value.litevalue.RatioValueDefaultView;
import wt.iba.value.litevalue.ReferenceValueDefaultView;
import wt.iba.value.litevalue.StringValueDefaultView;
import wt.iba.value.litevalue.TimestampValueDefaultView;
import wt.iba.value.litevalue.URLValueDefaultView;
import wt.iba.value.litevalue.UnitValueDefaultView;
import wt.iba.value.service.IBAValueHelper;
import wt.iba.value.service.StandardIBAValueService;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.applicationcontext.implementation.DefaultServiceProvider;
import wt.units.IncompatibleUnitsException;
import wt.units.Unit;
import wt.units.UnitFormatException;
import wt.units.service.MeasurementSystemCache;
import wt.units.service.QuantityOfMeasureDefaultView;
import wt.util.WTContext;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.util.WTStandardDateFormat;
import wt.util.range.Range;
import wt.vc.ControlBranch;
import wt.vc.Iterated;
import wt.vc.VersionControlException;
import wt.vc.VersionControlServerHelper;
import wt.vc.Versioned;

import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.ptc.core.command.common.bean.entity.NewEntityCommand;
import com.ptc.core.command.common.bean.entity.PrepareEntityCommand;
import com.ptc.core.meta.common.AnalogSet;
import com.ptc.core.meta.common.AttributeIdentifier;
import com.ptc.core.meta.common.AttributeTypeIdentifier;
import com.ptc.core.meta.common.ConstraintIdentifier;
import com.ptc.core.meta.common.DataSet;
import com.ptc.core.meta.common.DataTypesUtility;
import com.ptc.core.meta.common.DefinitionIdentifier;
import com.ptc.core.meta.common.DiscreteSet;
import com.ptc.core.meta.common.Identifier;
import com.ptc.core.meta.common.IdentifierFactory;
import com.ptc.core.meta.common.OperationIdentifier;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.TypeInstanceIdentifier;
import com.ptc.core.meta.common.WildcardSet;
import com.ptc.core.meta.common.impl.WCTypeIdentifier;
import com.ptc.core.meta.container.common.AttributeContainer;
import com.ptc.core.meta.container.common.AttributeContainerSpec;
import com.ptc.core.meta.container.common.AttributeTypeSummary;
import com.ptc.core.meta.container.common.ConstraintContainer;
import com.ptc.core.meta.container.common.ConstraintData;
import com.ptc.core.meta.container.common.ConstraintException;
import com.ptc.core.meta.container.common.impl.DefaultConstraintValidator;
import com.ptc.core.meta.server.IBAModel;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.core.meta.type.common.TypeInstance;
import com.ptc.core.meta.type.runtime.server.PopulatedAttributeContainerFactory;
import com.ptc.core.meta.type.server.TypeInstanceUtility;
import com.ptc.windchill.cadx.caddoc.CADDocProcessorService;

/**
 * CmIBAHelper: windchill IBA utility methods
 */
@SuppressWarnings("unchecked")
public class CmIBAHelper {
   public static final String  IBACONST_LEGAL_VALUE_SET   = "LEGAL_VALUE_SET";
   public static final String  IBACONST_STRING_LENGTH_SET = "STRING_LENGTH_SET";

   public static final String  IBA_IDENTIFIER             = "IBA_IDENTIFIER";
   public static final String  IBA_KEY                    = "IBA_KEY";
   public static final String  IBA_NAME                   = "IBA_NAME";
   public static final String  IBA_VALUE                  = "IBA_VALUE";
   public static final String  IBA_LABEL                  = "IBA_LABEL";
   public static final String  IBA_DATATYPE               = "IBA_DATATYPE";
   public static final String  IBA_OPTIONS_VECTOR         = "IBA_OPTIONS_VECTOR";
   public static final String  IBA_REQUIRED               = "IBA_REQUIRED";
   public static final String  IBA_EDITABLE               = "IBA_EDITABLE";
   public static final String  IBA_STRING_LENGTH_MIN      = "IBA_STRING_LENGTH_MIN";
   public static final String  IBA_STRING_LENGTH_MAX      = "IBA_STRING_LENGTH_MAX";
   public static final String  IBA_FROM_DEFINITION        = "IBA_FROM_DEFINITION";
   public static final String  IBA_UNDEFINED              = "IBA_UNDEFINED";

   private static final Object EDIT_IBAS                  = new wt.epm.attributes.EPMIBAConstraintFactory.EditFileBasedAttributes();

   public HashMap              ibaContainer               = null;

   public CmIBAHelper(IBAHolder holder) {
      ibaContainer = new HashMap();
      if (holder != null)
         getIBAValuesLite(holder, ibaContainer);
   }

   public String getIBAValue(String ibaName) {
      return (String) ibaContainer.get(ibaName);
   }

   public static String getIBAValue(IBAHolder holder, String ibaName) throws Exception {
      Map tmp = getStringIBAValue(holder, new String[]{ibaName});
      return (String) tmp.get(ibaName);
   }

   public static Map getStringIBAValue(IBAHolder holder, String[] ibaNames) throws Exception {
      Map ret = new HashMap();
      if (ibaNames != null) {
         Map ibaNameMap = new HashMap(ibaNames.length);
         for (int i = 0; i < ibaNames.length; i++) {
            AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaNames[i]);
            if (addv == null)
               throw new Exception("ϵͳδ�����IBA���ԣ�" + ibaNames[i]);
            ibaNameMap.put(addv.getObjectID(), ibaNames[i]);
         }

         long ibaHolderId = PersistenceHelper.getObjectIdentifier((Persistable) holder).getId();
         QuerySpec qs = new QuerySpec(StringValue.class);
         qs.appendWhere(new SearchCondition(StringValue.class, StringValue.IBAHOLDER_REFERENCE + "." + ObjectReference.KEY + "." + ObjectIdentifier.ID, SearchCondition.EQUAL, ibaHolderId),
               new int[]{0});

         QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
         StringValue sv = null;
         while (qr.hasMoreElements()) {
            sv = (StringValue) qr.nextElement();
            Object oid = sv.getDefinitionReference().getKey();
            if (ibaNameMap.containsKey(oid))
               ret.put(ibaNameMap.get(oid), sv.getValue());
         }
      }
      return ret;
   }

   /**
    * ���ַ�ֵ��ָ������ת��ΪIBA����ֵ��Ӧ�Ķ���
    * 
    * @param strVal
    *        �ַ�ֵ
    * @param dataType
    *        �������(java����)
    * @param locale *
    * @param timezone *
    * @return Objectֵ����
    * @throws WTException
    */
   public static Object convertStringToIBAValue(String strVal, String dataType, Locale locale, TimeZone timezone) throws WTException {
      Object obj = null;
      if (dataType.equals("java.lang.Long"))
         try {
            obj = Long.valueOf(strVal);
         } catch (Exception exception) {
            Object aobj1[] = {strVal};
            throw new WTException("com.ptc.core.HTMLtemplateutil.server.processors.processorsResource", "58", aobj1);
         }
      else if (dataType.equals("com.ptc.core.meta.common.FloatingPoint"))
         try {
            obj = DataTypesUtility.toFloatingPoint(strVal, locale);
         } catch (Exception exception1) {
            Object aobj3[] = {strVal};
            throw new WTException("com.ptc.core.HTMLtemplateutil.server.processors.processorsResource", "59", aobj3);
         }
      else if (dataType.equals("java.lang.Boolean"))
         obj = Boolean.valueOf(strVal);
      else if (dataType.equals("java.sql.Timestamp"))
         try {
            Date date = null;
            try {
               date = WTStandardDateFormat.parse(strVal, 3, locale, timezone);
            } catch (ParseException parseexception) {
               try {
                  date = WTStandardDateFormat.parse(strVal, 25, locale, timezone);
               } catch (ParseException parseexception1) {
                  date = WTStandardDateFormat.parse(strVal, 26, locale, timezone);
               }
            }
            obj = new Timestamp(date.getTime());
         } catch (ParseException parseexception) {
            Object aobj5[] = {strVal};
            throw new WTException("com.ptc.core.HTMLtemplateutil.server.processors.processorsResource", "60", aobj5);
         }
      else
         obj = new String(strVal);
      return obj;
   }

   public static AbstractValueView createAttribute(String wcAttrName, String value) throws WTException, Exception {
      AttributeDefDefaultView theAttrDef = IBADefinitionObjectsFactory.newAttributeDefDefaultView(IBAModel.getIBADefinitionByHid(wcAttrName));
      if (theAttrDef == null) {
         return null;
      }

      AbstractValueView theAttr;
      Class attrValueClass = getAttrValueClass(theAttrDef);
      theAttr = null;
      if (attrValueClass == (wt.iba.value.litevalue.RatioValueDefaultView.class)) {
         Class numeratorValueType = attrValueClass.getDeclaredField("value").getType();
         Class denominatorValueType = attrValueClass.getDeclaredField("denominator").getType();
         int indexOfColon = value.indexOf(":");
         String numeratorInputString = value.substring(0, indexOfColon);
         String denominatorInputString = value.substring(indexOfColon + 1);
         Object convertedNumeratorValue = getConvertedValue(numeratorInputString, numeratorValueType);
         Object convertedDenominatorValue = getConvertedValue(denominatorInputString, denominatorValueType);
         Class parameterTypes[] = {theAttrDef.getClass(), numeratorValueType, denominatorValueType};
         Constructor constructor = attrValueClass.getConstructor(parameterTypes);
         Object parameters[] = {theAttrDef, convertedNumeratorValue, convertedDenominatorValue};
         theAttr = (AbstractValueView) constructor.newInstance(parameters);
         setRatioValues(theAttr, convertedNumeratorValue, convertedDenominatorValue);
      } else if (attrValueClass == (wt.iba.value.litevalue.FloatValueDefaultView.class) || attrValueClass == (wt.iba.value.litevalue.UnitValueDefaultView.class)) {
         Class valueType = attrValueClass.getDeclaredField("value").getType();
         Object convertedValue = getConvertedValue(value, valueType);
         Class parameterTypes[] = {theAttrDef.getClass(), valueType, Integer.TYPE};
         Constructor constructor = attrValueClass.getConstructor(parameterTypes);
         String valueStr = value.toString();
         int precision = valueStr.length();
         if (valueStr.indexOf(".") != -1) {
            precision--;
         } else {
            precision = 0;
         }
         Object parameters[] = {theAttrDef, convertedValue, new Integer(precision)};
         theAttr = (AbstractValueView) constructor.newInstance(parameters);
         if (attrValueClass == (wt.iba.value.litevalue.UnitValueDefaultView.class)) {
            setValue(theAttr, getDefaultUnitValue(theAttr, (Double) convertedValue));
         }
      } else if (attrValueClass == (wt.iba.value.litevalue.TimestampValueDefaultView.class)) {
         Class valueType = attrValueClass.getDeclaredField("value").getType();
         Object convertedValue = getConvertedValue(value, valueType);
         Class parameterTypes[] = {theAttrDef.getClass(), valueType};
         Constructor constructor = attrValueClass.getConstructor(parameterTypes);
         Object parameters[] = {theAttrDef, convertedValue};
         theAttr = (AbstractValueView) constructor.newInstance(parameters);
         setTimestampValues(theAttr, convertedValue);
      } else if (attrValueClass == (wt.iba.value.litevalue.URLValueDefaultView.class)) {
         Class linkValueType = attrValueClass.getDeclaredField("value").getType();
         Class labelValueType = attrValueClass.getDeclaredField("description").getType();
         String linkInputString = value.substring(value.indexOf("href=\"") + 6, value.indexOf("\">"));
         String labelInputString = value.substring(value.indexOf("\">") + 2, value.indexOf("</a>"));
         Object convertedLinkValue = getConvertedValue(linkInputString, linkValueType);
         Object convertedLabelValue = getConvertedValue(labelInputString, labelValueType);
         Class parameterTypes[] = {theAttrDef.getClass(), linkValueType, labelValueType};
         Constructor constructor = attrValueClass.getConstructor(parameterTypes);
         Object parameters[] = {theAttrDef, convertedLinkValue, convertedLabelValue};
         theAttr = (AbstractValueView) constructor.newInstance(parameters);
      } else if (attrValueClass == (wt.iba.value.litevalue.ReferenceValueDefaultView.class)) {
         Class parameterTypes[] = {theAttrDef.getClass()};
         Constructor constructor = attrValueClass.getConstructor(parameterTypes);
         Object parameters[] = {theAttrDef};
         theAttr = (AbstractValueView) constructor.newInstance(parameters);
      } else {
         Class valueType = attrValueClass.getDeclaredField("value").getType();
         Object convertedValue = getConvertedValue(value, valueType);
         Class parameterTypes[] = {theAttrDef.getClass(), valueType};
         Constructor constructor = attrValueClass.getConstructor(parameterTypes);
         Object parameters[] = {theAttrDef, convertedValue};
         theAttr = (AbstractValueView) constructor.newInstance(parameters);
      }

      return theAttr;
   }

   private static Class getAttrValueClass(AttributeDefDefaultView attrDef) {
      if (attrDef instanceof StringDefView) {
         return wt.iba.value.litevalue.StringValueDefaultView.class;
      }
      if (attrDef instanceof FloatDefView) {
         return wt.iba.value.litevalue.FloatValueDefaultView.class;
      }
      if (attrDef instanceof IntegerDefView) {
         return wt.iba.value.litevalue.IntegerValueDefaultView.class;
      }
      if (attrDef instanceof TimestampDefView) {
         return wt.iba.value.litevalue.TimestampValueDefaultView.class;
      }
      if (attrDef instanceof BooleanDefView) {
         return wt.iba.value.litevalue.BooleanValueDefaultView.class;
      }
      if (attrDef instanceof URLDefView) {
         return wt.iba.value.litevalue.URLValueDefaultView.class;
      }
      if (attrDef instanceof UnitDefView) {
         return wt.iba.value.litevalue.UnitValueDefaultView.class;
      }
      if (attrDef instanceof RatioDefView) {
         return wt.iba.value.litevalue.RatioValueDefaultView.class;
      }
      if (attrDef instanceof ReferenceDefView) {
         return wt.iba.value.litevalue.ReferenceValueDefaultView.class;
      } else {
         return null;
      }
   }

   private static Object getConvertedValue(String value, Class valueType) {
      Object objValue = null;
      if (valueType.getName() == "java.sql.Timestamp") {
         try {
            Date date = WTStandardDateFormat.parse(value, "yyyy-MM-dd HH:mm:ss z", null, null);
            objValue = new Timestamp(date.getTime());
         } catch (ParseException e) {
            e.printStackTrace();
         }
      } else if (valueType.getName() == "long") {
         objValue = new Integer(value);
      } else if (valueType.getName() == "double") {
         objValue = new Double(value);
      } else if (valueType.getName() == "boolean") {
         if (value.equals("1")) {
            objValue = new Boolean("true");
         } else {
            objValue = new Boolean("false");
         }
      } else {
         objValue = value;
      }
      return objValue;
   }

   private static Double getDefaultUnitValue(AbstractValueView attributeValueView, Double displayValue) throws IOException, UnitFormatException, IncompatibleUnitsException {
      UnitValueDefaultView unitValueDefaultView = (UnitValueDefaultView) attributeValueView;
      UnitDefView unitDefinition = unitValueDefaultView.getUnitDefinition();
      QuantityOfMeasureDefaultView qom = unitDefinition.getQuantityOfMeasureDefaultView();
      String displayUnits = qom.getBaseUnit();
      String defaultUnits = displayUnits;
      double convertedValue = displayValue.doubleValue();
      String measurementSystem = MeasurementSystemCache.getCurrentMeasurementSystem();
      if (measurementSystem == null) {
         measurementSystem = WTProperties.getLocalProperties().getProperty("wt.units.defaultMeasurementSystem");
      }
      String wcMeasurementSystem = WTProperties.getLocalProperties().getProperty("wt.units.defaultMeasurementSystem");
      if (!measurementSystem.equals(wcMeasurementSystem)) {
         if (wcMeasurementSystem != null) {
            String du = unitDefinition.getDisplayUnitString(wcMeasurementSystem);
            if (du == null) {
               du = qom.getDisplayUnitString(wcMeasurementSystem);
            }
            if (du == null) {
               du = qom.getDefaultDisplayUnitString(wcMeasurementSystem);
            }
            if (du != null) {
               defaultUnits = du;
            }
         }
         if (measurementSystem != null) {
            String du = unitDefinition.getDisplayUnitString(measurementSystem);
            if (du == null) {
               du = qom.getDisplayUnitString(measurementSystem);
            }
            if (du == null) {
               du = qom.getDefaultDisplayUnitString(measurementSystem);
            }
            if (du != null) {
               displayUnits = du;
            }
         }
         Unit u = new Unit(displayValue.doubleValue(), displayUnits);
         convertedValue = u.convert(defaultUnits);
      }
      return new Double(convertedValue);
   }

   /**
    * ��ȡָ��softtype���͵�IBA���� ���ص�ibaList��ibaMap��ÿһЩΪһ��HashMap, ��key��������: <br>
    * String IBA_NAME = "IBA_NAME"; ������� <br>
    * String IBA_VALUE = "IBA_VALUE"; ����ֵ <br>
    * String IBA_LABEL = "IBA_LABEL"; ���Ա�ǩ <br>
    * String IBA_DATATYPE = "IBA_DATATYPE"; ������� <br>
    * String IBA_OPTIONS_VECTOR = "IBA_OPTIONS_VECTOR"; ѡ��Vector <br>
    * String IBA_REQUIRED = "IBA_REQUIRED"; �Ƿ���� <br>
    * String IBA_EDITABLE = "IBA_EDITABLE"; �Ƿ�ɱ༭ <br>
    * String IBA_STRING_LENGTH_MIN = "IBA_STRING_LENGTH_MIN"; �ִ����(null����) <br>
    * String IBA_STRING_LENGTH_MAX = "IBA_STRING_LENGTH_MAX"; �ִ��(null����) <br>
    * 
    * @param ibaHolder
    *        SoftType���͵Ķ���ʵ��
    * @param ibaList
    *        IBA�����б�
    * @param ibaMap
    *        IBA����Map, ��IBA�������Ϊkey
    * @throws WTException
    */
   public static void getIBAValues(IBAHolder ibaHolder, ArrayList ibaList, HashMap ibaMap) throws WTException {
      getIBAValuesInternal(ibaHolder, ibaList, ibaMap);
   }

   /**
    * ��ȡָ��softtype���͵�IBA����. ���ص�ibaList��ibaMap��ÿһЩΪһ��HashMap, ��key��������: <br>
    * String IBA_NAME = "IBA_NAME"; ������� <br>
    * String IBA_VALUE = "IBA_VALUE"; ����ֵ <br>
    * String IBA_LABEL = "IBA_LABEL"; ���Ա�ǩ <br>
    * String IBA_DATATYPE = "IBA_DATATYPE"; ������� <br>
    * String IBA_OPTIONS_VECTOR = "IBA_OPTIONS_VECTOR"; ѡ��Vector <br>
    * String IBA_REQUIRED = "IBA_REQUIRED"; �Ƿ���� <br>
    * String IBA_EDITABLE = "IBA_EDITABLE"; �Ƿ�ɱ༭ <br>
    * String IBA_STRING_LENGTH_MIN = "IBA_STRING_LENGTH_MIN"; �ִ����(null����) <br>
    * String IBA_STRING_LENGTH_MAX = "IBA_STRING_LENGTH_MAX"; �ִ��(null����) <br>
    * 
    * @param typeIdentifier
    *        softtype�����ַ�, WCTYPE|Э��ͷ��ʡ�� ��:
    *        WCTYPE|wt.doc.WTDocument|com.haitian.DocTypeA
    * @param ibaList
    *        IBA�����б�
    * @param ibaMap
    *        IBA����Map, ��IBA�������Ϊkey
    * @throws WTException
    */
   public static void getIBAValues(String typeIdentifier, ArrayList ibaList, HashMap ibaMap) throws WTException {
      String protoHeader = WCTypeIdentifier.PROTOCOL + Identifier.PROTOCOL_SEPARATOR;
      if (!typeIdentifier.startsWith(protoHeader))
         typeIdentifier = protoHeader + typeIdentifier;

      getIBAValuesInternal(typeIdentifier, ibaList, ibaMap);
   }

   /**
    * ��ȡIBA��Ϣ���ڲ�ʵ��
    * 
    * @param obj
    *        IBAHolder�����typeIdentifer�ִ�
    * @param ibaList *
    * @param ibaMap *
    * @throws WTException
    */
   static TypeInstance getIBAValuesInternal(Object obj, ArrayList ibaList, HashMap ibaMap) throws WTException {
      TypeInstanceIdentifier tii = null;
      Locale locale = WTContext.getContext().getLocale();

      // ȡTypeInstanceIdentifier
      if (obj instanceof IBAHolder) { // obj��һ��IBAHolder(Typed)����
         tii = TypeIdentifierUtility.getTypeInstanceIdentifier(obj);
      } else { // obj��һ��TypeIdentifier�ַ�, e.g. WTTYPE|wt.doc.WTDocument|...
         IdentifierFactory idFactory = (IdentifierFactory) DefaultServiceProvider.getService(com.ptc.core.meta.common.IdentifierFactory.class, "default");
         TypeIdentifier ti = (TypeIdentifier) idFactory.get((String) obj);
         tii = ti.newTypeInstanceIdentifier();
      }

      // ��ȡTypeInstance
      TypeInstance typeInstance = null;
      try {
         PopulatedAttributeContainerFactory pacFactory = (PopulatedAttributeContainerFactory) DefaultServiceProvider.getService(PopulatedAttributeContainerFactory.class, "virtual");
         AttributeContainer ac = pacFactory.getAttributeContainer(null, (TypeIdentifier) tii.getDefinitionIdentifier());

         if (ac == null) {
            if (obj instanceof String)
               throw new WTException("δ�����SoftType����: " + obj);
            else
               throw new WTException("δ�����SoftType����: " + tii);
         }

         AttributeTypeIdentifier[] atis = ac.getAttributeTypeIdentifiers();
         AttributeContainerSpec acSpec = new AttributeContainerSpec();

         if (tii.isInitialized())
            acSpec.setNextOperation(OperationIdentifier.newOperationIdentifier("STDOP|com.ptc.windchill.update"));
         else
            acSpec.setNextOperation(OperationIdentifier.newOperationIdentifier("STDOP|com.ptc.windchill.create"));
         acSpec.putEntries(atis, true, true);
         PrepareEntityCommand peCmd = new PrepareEntityCommand();
         peCmd.setSource(tii);
         peCmd.setLocale(locale);
         peCmd.setFilter(acSpec);
         peCmd = (PrepareEntityCommand) peCmd.execute();
         typeInstance = peCmd.getResult();
      } catch (WTPropertyVetoException e) {
         throw new WTException(e);
      }

      // �����ȡIBA����
      AttributeIdentifier[] ais = typeInstance.getAttributeIdentifiers();
      for (int i = 0; ais != null && i < ais.length; i++) {
         DefinitionIdentifier di = ais[i].getDefinitionIdentifier();
         AttributeTypeIdentifier ati = (AttributeTypeIdentifier) di;
         AttributeTypeSummary ats = typeInstance.getAttributeTypeSummary(ati);

         String ibaIdentifier = ais[i].toExternalForm();
         String name = ati.getAttributeName();
         ati.getWithTailContext();

         String value = String.valueOf(typeInstance.get(ais[i]));
         String dataType = ats.getDataType();
         String label = ats.getLabel();
         Boolean required = ats.isRequired() ? new Boolean(true) : null;
         Boolean editable = ats.isEditable() ? new Boolean(true) : null;

         int min = ats.getMinStringLength();
         int max = ats.getMaxStringLength();
         Integer minStringLength = min == 0 ? null : new Integer(min);
         Integer maxStringLength = max == 0 ? null : new Integer(max);

         Vector options = null;
         DataSet dsVal = ats.getLegalValueSet();
         if (dsVal != null && dsVal instanceof DiscreteSet) {
            Object[] eles = ((DiscreteSet) dsVal).getElements();
            options = new Vector();
            for (int j = 0; eles != null && j < eles.length; j++) {
               options.add(String.valueOf(eles[j]));
            }
         }

         HashMap ibaInfo = new HashMap();
         ibaInfo.put(IBA_IDENTIFIER, ibaIdentifier);
         ibaInfo.put(IBA_NAME, name);
         ibaInfo.put(IBA_VALUE, value);
         ibaInfo.put(IBA_LABEL, label);
         ibaInfo.put(IBA_DATATYPE, dataType);
         ibaInfo.put(IBA_REQUIRED, required);
         ibaInfo.put(IBA_EDITABLE, editable);
         ibaInfo.put(IBA_OPTIONS_VECTOR, options);
         ibaInfo.put(IBA_STRING_LENGTH_MIN, minStringLength);
         ibaInfo.put(IBA_STRING_LENGTH_MAX, maxStringLength);

         if (ibaList != null) {
            ibaList.add(ibaInfo);
         }
         if (ibaMap != null) {
            ibaMap.put(name, ibaInfo);
         }
      }

      return typeInstance;
   }

   /**
    * ȡIBAHolder������IBA����ֵ: <br>
    * 
    * @param ibaHolder
    * @param ibaMap *
    * @return
    */
   public static HashMap getIBAValuesLite(IBAHolder ibaHolder, HashMap ibaMap) {
      if (ibaMap == null)
         ibaMap = new HashMap();
      else
         ibaMap.clear();

      Locale locale = WTContext.getContext().getLocale();
      DefaultAttributeContainer dac = (DefaultAttributeContainer) ibaHolder.getAttributeContainer();
      if (dac == null) {
         try {
            ibaHolder = IBAValueHelper.service.refreshAttributeContainer(ibaHolder, null, null, null);
            dac = (DefaultAttributeContainer) ibaHolder.getAttributeContainer();
         } catch (Exception e) {
            e.printStackTrace();
            return ibaMap;
         }
      }

      AbstractValueView[] avv = null;
      if (dac == null || (avv = dac.getAttributeValues()) == null)
         return ibaMap;

      String name = null;
      String value = null;
      for (int i = 0; i < avv.length; i++) {
         name = avv[i].getDefinition().getName();
         if (avv[i] instanceof ReferenceValueDefaultView) {
            value = ((ReferenceValueDefaultView) avv[i]).getLiteIBAReferenceable().getIBAReferenceableDisplayString();
         } else
            value = avv[i].getLocalizedDisplayString(locale);
         ibaMap.put(name, value);
      }

      return ibaMap;
   }

   /**
    * ����IBAԼ�����, ����: EntityTaskDelegate
    * 
    * @param constraintexception *
    * @param locale *
    * @return *
    * @throws WTException
    */
   private static WTException interpretConstraintViolationException(ConstraintException constraintexception, Locale locale) throws WTException {
      AttributeIdentifier attributeidentifier = constraintexception.getAttributeIdentifier();
      AttributeTypeIdentifier attributetypeidentifier = (AttributeTypeIdentifier) attributeidentifier.getDefinitionIdentifier();
      AttributeContainerSpec attributecontainerspec = new AttributeContainerSpec();
      attributecontainerspec.putEntry(attributetypeidentifier, true, true);
      NewEntityCommand newentitycommand = new NewEntityCommand();
      try {
         (newentitycommand).setIdentifier(attributetypeidentifier.getContext());
         newentitycommand.setFilter(attributecontainerspec);
         newentitycommand.setLocale(locale);
      } catch (WTPropertyVetoException wtpropertyvetoexception) {
         throw new WTException(wtpropertyvetoexception);
      }
      newentitycommand.execute();
      TypeInstance typeinstance = newentitycommand.getResult();
      AttributeTypeSummary attributetypesummary = typeinstance.getAttributeTypeSummary((AttributeTypeIdentifier) attributeidentifier.getDefinitionIdentifier());
      String s = attributetypesummary.getLabel();
      // Object obj = constraintexception.getAttributeContent();
      ConstraintIdentifier constraintidentifier = constraintexception.getConstraintIdentifier();
      String s1 = constraintidentifier.getEnforcementRuleClassname();
      ConstraintData constraintdata = constraintexception.getConstraintData();
      // String s2 = " ";
      String s3 = "com.ptc.core.HTMLtemplateutil.server.processors.processorsResource";
      String s4 = null;
      java.io.Serializable serializable = constraintdata.getEnforcementRuleData();
      ArrayList arraylist = new ArrayList();
      arraylist.add(s);
      if (s1.equals("com.ptc.core.meta.container.common.impl.RangeConstraint")) {
         if (serializable instanceof AnalogSet) {
            Range range = ((AnalogSet) serializable).getBoundingRange();
            if (range.hasLowerBound() && range.hasUpperBound()) {
               arraylist.add(range.getLowerBoundValue());
               arraylist.add(range.getUpperBoundValue());
               s4 = "72";
            } else if (range.hasLowerBound()) {
               arraylist.add(range.getLowerBoundValue());
               s4 = "73";
            } else if (range.hasUpperBound()) {
               arraylist.add(range.getUpperBoundValue());
               s4 = "74";
            }
         } else {
            s4 = "75";
         }
      } else if (s1.equals("com.ptc.core.meta.container.common.impl.ImmutableConstraint"))
         s4 = "78";
      else if (s1.equals("com.ptc.core.meta.container.common.impl.DiscreteSetConstraint")) {
         if (serializable instanceof DiscreteSet) {
            Object aobj[] = ((DiscreteSet) serializable).getElements();
            String s5 = "";
            for (int j = 0; j < aobj.length; j++)
               s5 = s5 + aobj[j].toString() + ",";

            String s7 = s5.substring(0, s5.length() - 1);
            arraylist.add(s7);
            s4 = "83";
         } else {
            s4 = "84";
         }
      } else if (s1.equals("com.ptc.core.meta.container.common.impl.StringLengthConstraint")) {
         if (serializable instanceof AnalogSet) {
            Range range1 = ((AnalogSet) serializable).getBoundingRange();
            if (range1.hasLowerBound() && range1.hasUpperBound()) {
               arraylist.add(range1.getLowerBoundValue());
               arraylist.add(range1.getUpperBoundValue());
               s4 = "79";
            } else if (range1.hasLowerBound()) {
               arraylist.add(range1.getLowerBoundValue());
               s4 = "80";
            } else if (range1.hasUpperBound()) {
               arraylist.add(range1.getUpperBoundValue());
               s4 = "81";
            }
         } else {
            s4 = "82";
         }
      } else if (s1.equals("com.ptc.core.meta.container.common.impl.StringFormatConstraint")) {
         if (serializable instanceof DiscreteSet) {
            Object aobj1[] = ((DiscreteSet) serializable).getElements();
            String s6 = "";
            for (int k = 0; k < aobj1.length; k++)
               s6 = s6 + "\"" + aobj1[k].toString() + "\" or ";

            String s8 = s6.substring(0, s6.length() - 4);
            arraylist.add(s8);
            s4 = "85";
         } else {
            s4 = "84";
         }
      } else if (s1.equals("com.ptc.core.meta.container.common.impl.UpperCaseConstraint"))
         s4 = "86";
      else if (s1.equals("com.ptc.core.meta.container.common.impl.ValueRequiredConstraint"))
         s4 = "77";
      else if (s1.equals("com.ptc.core.meta.container.common.impl.WildcardConstraint")) {
         if (serializable instanceof WildcardSet) {
            arraylist.add(((WildcardSet) serializable).getValue());
            int i = ((WildcardSet) serializable).getMode();
            if (i == 1) {
               s4 = "87";
               arraylist.add(((WildcardSet) serializable).getValue());
            } else if (i == 2) {
               if (((WildcardSet) serializable).isNegated())
                  s4 = "89";
               else
                  s4 = "88";
            } else if (i == 3) {
               if (((WildcardSet) serializable).isNegated())
                  s4 = "91";
               else
                  s4 = "90";
            } else if (i == 4)
               if (((WildcardSet) serializable).isNegated())
                  s4 = "93";
               else
                  s4 = "92";
         } else {
            s4 = "84";
         }
      } else {
         s4 = "84";
      }
      if (s4 != null)
         return new WTException(s3, s4, arraylist.toArray());
      else
         return null;
   }

   /**
    * @param ibaHolder
    *        �趨IBA����Ŀ�����
    * @param ibaValues
    *        Ҫ�趨���������ֵ����
    * @return 
    * @throws Exception
    */
   public static void updateOrCreateIBAValues(IBAHolder holder, Properties ibaValues) throws Exception {
      if (ibaValues == null || ibaValues.size() == 0)
         return;

      DefaultAttributeContainer container = (DefaultAttributeContainer) holder.getAttributeContainer();
      if (container == null) {
         container = new DefaultAttributeContainer();
         holder.setAttributeContainer(container);
      }
      container.setConstraintParameter(EDIT_IBAS);

      Locale locale = WTContext.getContext().getLocale();

      holder = IBAValueHelper.service.refreshAttributeContainer(holder, EDIT_IBAS, locale, null);
      DefaultAttributeContainer dac = (DefaultAttributeContainer) holder.getAttributeContainer();
      AbstractValueView[] avv = dac.getAttributeValues();

      for (Iterator it = ibaValues.keySet().iterator(); it.hasNext();) {
         String attrName = (String) it.next();
         String attrValue = (String) ibaValues.get(attrName);

         String wcAttrName = CADDocProcessorService.getWCAttrName(attrName);
         if (wcAttrName == null || wcAttrName == "") {
            wcAttrName = attrName;
         }

         if (updateAttributes(dac, wcAttrName, attrValue, locale, avv))
            continue;

         AbstractValueView valueView = createAttribute(wcAttrName, attrValue);
         if (valueView != null) {
            dac.addAttributeValue(valueView);
         } else {
            Persistable p = (Persistable) holder;
            String oid = PersistenceHelper.isPersistent(p) ? new ReferenceFactory().getReferenceString(p) : holder.getClass().getName() + ":NEW";
            //            CmLogger.getLogger(CmIBAHelper.class).error("δ�ɹ�����IBA����: [", attrName, "], ", oid);
            throw new WTException("δ�ɹ�����IBA����: [" + attrName + "] ");
         }
      }

      try {
         StandardIBAValueService.theIBAValueDBService.updateAttributeContainer(holder, null, null, null);
      } catch (Exception e) {
         e.printStackTrace();
         throw new WTException("����IBA���Թ���г��ִ���:" + e.toString());
      }
   }

   /**
    * <pre>
    *      �趨IBA����, ����ֵΪtrue, ����Ҫ���¶���: 
    *          persistable = IBAValueHelper.service.updateIBAHolder(ibaHolder, null, null, null)
    * </pre>
    * 
    * @param ibaHolder
    *        �趨IBA����Ŀ�����
    * @param ibaValues
    *        Ҫ�趨���������ֵ����
    * @return �����Ƿ񱻸����Ҫ����
    * @throws WTException
    */
   public static boolean setIBAValues(IBAHolder ibaHolder, Properties ibaValues) throws WTException {
      if (!PersistenceHelper.isPersistent(ibaHolder) && (ibaHolder instanceof Iterated)) {
         if (getBranchPointOf((Iterated) ibaHolder) != null)
            ibaHolder = (IBAHolder) PersistenceHelper.manager.save((Persistable) ibaHolder);
      }

      // ȡ�����ԭ��IBA����
      HashMap ibaMap = new HashMap();
      Locale locale = WTContext.getContext().getLocale();
      TimeZone tzone = WTContext.getContext().getTimeZone();
      TypeInstance ti = getIBAValuesInternal(ibaHolder, null, ibaMap);
      IdentifierFactory idFactory = (IdentifierFactory) DefaultServiceProvider.getService(com.ptc.core.meta.common.IdentifierFactory.class, "default");

      // ����Ҫ��ֵ��IBA����
      ArrayList listIBAId = new ArrayList();
      ArrayList listIBATypeId = new ArrayList();
      ArrayList listIBAValue = new ArrayList();
      for (Enumeration en = ibaValues.keys(); en.hasMoreElements();) {
         String iName = (String) en.nextElement();
         String iVal = (String) ibaValues.get(iName);
         if (iVal == null) // null ==> ʹ��Ĭ��ֵ
            continue;

         HashMap ibaInfo = (HashMap) ibaMap.get(iName);
         if (ibaInfo == null) { // δ������������
            Persistable p = (Persistable) ibaHolder;
            String oid = PersistenceHelper.isPersistent(p) ? new ReferenceFactory().getReferenceString(p) : ibaHolder.getClass().getName() + ":NEW";
            CmLogger.getLogger(CmIBAHelper.class).info("δ�����IBA������: [" + iName + "], " + oid);
            continue;
         }
         Boolean required = (Boolean) ibaInfo.get(IBA_REQUIRED);
         if (required != null && required.booleanValue() && iVal.equals(""))
            throw new WTException("����<" + iName + ">��ֵ����Ϊ��!");

         AttributeIdentifier ai = (AttributeIdentifier) idFactory.get((String) ibaInfo.get(IBA_IDENTIFIER));
         DefinitionIdentifier ati = ai.getDefinitionIdentifier();

         String dataType = (String) ibaInfo.get(IBA_DATATYPE);
         Object iv = convertStringToIBAValue(iVal, dataType, locale, tzone);

         listIBAId.add(ai);
         listIBAValue.add(iv);
         listIBATypeId.add(ati);
      }

      // �����ֵ
      HashMap vmap = new HashMap();
      TypeInstanceIdentifier tii = (TypeInstanceIdentifier) ti.getIdentifier();
      for (int i = 0; i < listIBAId.size(); i++) {
         AttributeTypeIdentifier ati = (AttributeTypeIdentifier) listIBATypeId.get(i);
         AttributeIdentifier[] ais = ti.getAttributeIdentifiers(ati);
         if (ais.length > 0) {
            vmap.put(ais[0], ti.get(ais[0]));
            ti.put(ais[0], listIBAValue.get(i));
         } else {
            AttributeIdentifier ai = ati.newAttributeIdentifier(tii);
            vmap.put(ai, null);
            ti.put(ai, listIBAValue.get(i));
         }
      }

      ti.acceptDefaultContent();
      ti.purgeDefaultContent();

      // ���Լ��
      if (tii.isInitialized())
         TypeInstanceUtility.populateConstraints(ti, OperationIdentifier.newOperationIdentifier("STDOP|com.ptc.windchill.update"));
      else
         TypeInstanceUtility.populateConstraints(ti, OperationIdentifier.newOperationIdentifier("STDOP|com.ptc.windchill.create"));
      DefaultConstraintValidator dac = DefaultConstraintValidator.getInstance();
      ConstraintContainer cc = ti.getConstraintContainer();
      if (cc != null) {
         AttributeIdentifier ais[] = ti.getAttributeIdentifiers();
         for (int i = 0; i < ais.length; i++) {
            Object ibaVal = ti.get(ais[i]);
            try {
               dac.isValid(ti, cc, ais[i], ibaVal);
            } catch (ConstraintException ce) {
               if ((!ce.getConstraintIdentifier().getEnforcementRuleClassname().equals("com.ptc.core.meta.container.common.impl.DiscreteSetConstraint") || vmap == null || vmap.get(ais[i]) == null || (!(vmap
                     .get(ais[i]) instanceof Comparable) || ((Comparable) ti.get(ais[i])).compareTo(vmap.get(ais[i])) != 0)
                     && !vmap.get(ais[i]).equals(ti.get(ais[i])))
                     && !ce.getConstraintIdentifier().getEnforcementRuleClassname().equals("com.ptc.core.meta.container.common.impl.ImmutableConstraint")) {
                  WTException wtexception = interpretConstraintViolationException(ce, locale);
                  if (wtexception != null)
                     throw wtexception;
               }
            }
         }
      }

      // ���浽Ŀ�����
      TypeInstanceUtility.updateIBAValues(ibaHolder, ti);
      return ti.isDirty();
   }

   private static void setRatioValues(AbstractValueView attributeValueView, Object numeratorValue, Object denominatorValue) throws WTException {
      try {
         Class methodParameterTypes[] = {attributeValueView.getClass().getDeclaredField("value").getType()};
         Method setNumeratorValueMethod = attributeValueView.getClass().getDeclaredMethod("setValue", methodParameterTypes);
         Object numeratorMethodParameters[] = {new Double(((Double) numeratorValue).doubleValue() / ((Double) denominatorValue).doubleValue())};
         setNumeratorValueMethod.invoke(attributeValueView, numeratorMethodParameters);
         Method setDenominatorValueMethod = attributeValueView.getClass().getDeclaredMethod("setDenominator", methodParameterTypes);
         Object denominatorMethodParameters[] = {denominatorValue};
         setDenominatorValueMethod.invoke(attributeValueView, denominatorMethodParameters);
      } catch (InvocationTargetException e) {
         throw new WTException(e.getTargetException());
      } catch (Exception e) {
         throw new WTException(e);
      }
   }

   private static void setTimestampValues(AbstractValueView attributeValueView, Object timestampValue) throws WTException {
      try {
         Class methodParameterTypes[] = {attributeValueView.getClass().getDeclaredField("value").getType()};
         Method setTimestampValueMethod = attributeValueView.getClass().getDeclaredMethod("setValue", methodParameterTypes);
         Object timestampMethodParameters[] = {timestampValue};
         setTimestampValueMethod.invoke(attributeValueView, timestampMethodParameters);
      } catch (InvocationTargetException e) {
         throw new WTException(e.getTargetException());
      } catch (Exception e) {
         throw new WTException(e);
      }
   }

   private static void setURLValues(AbstractValueView attributeValueView, Object linkValue, Object labelValue) throws WTException {
      try {
         Class linkMethodParameterTypes[] = {attributeValueView.getClass().getDeclaredField("value").getType()};
         Method setLinkValueMethod = attributeValueView.getClass().getDeclaredMethod("setValue", linkMethodParameterTypes);
         Object linkMethodParameters[] = {linkValue};
         setLinkValueMethod.invoke(attributeValueView, linkMethodParameters);
         Class labelMethodParameterTypes[] = {attributeValueView.getClass().getDeclaredField("description").getType()};
         Method setLabelValueMethod = attributeValueView.getClass().getDeclaredMethod("setDescription", labelMethodParameterTypes);
         Object labelMethodParameters[] = {labelValue};
         setLabelValueMethod.invoke(attributeValueView, labelMethodParameters);
      } catch (InvocationTargetException e) {
         throw new WTException(e.getTargetException());
      } catch (Exception e) {
         throw new WTException(e);
      }
   }

   private static void setValue(AbstractValueView attributeValueView, Object newValue) throws WTException {
      try {
         Class parameterTypes[] = {attributeValueView.getClass().getDeclaredField("value").getType()};
         Method setValueMethod = attributeValueView.getClass().getDeclaredMethod("setValue", parameterTypes);
         Object parameters[] = {newValue};
         setValueMethod.invoke(attributeValueView, parameters);
      } catch (InvocationTargetException e) {
         throw new WTException(e.getTargetException());
      } catch (Exception e) {
         throw new WTException(e);
      }
   }

   private static boolean updateAttributes(DefaultAttributeContainer dac, String wcAttrName, String attrValue, Locale locale, AbstractValueView avv[]) throws Exception {
      boolean matchFound = false;
      AttributeDefDefaultView theAttrDef = IBADefinitionObjectsFactory.newAttributeDefDefaultView(IBAModel.getIBADefinitionByHid(wcAttrName));
      for (int i = 0; i < avv.length; i++) {
         if (avv[i] instanceof ReferenceValueDefaultView) {
            continue;
         }

         AttributeDefDefaultView attrDef = avv[i].getDefinition();
         String attrName = attrDef.getName();
         if (!attrName.equals(theAttrDef.getName())) {
            continue;
         }

         if (attrValue == null || attrValue.trim().equals("")) {
            dac.deleteAttributeValue(avv[i]);
         } else {
            Class attrValueClass = getAttrValueClass(attrDef);
            if (attrValueClass == RatioValueDefaultView.class) {
               Class numeratorValueType = attrValueClass.getDeclaredField("value").getType();
               Class denominatorValueType = attrValueClass.getDeclaredField("denominator").getType();
               int indexOfColon = attrValue.indexOf(":");
               String numeratorInputString = attrValue.substring(0, indexOfColon);
               String denominatorInputString = attrValue.substring(indexOfColon + 1);
               Object convertedNumeratorValue = getConvertedValue(numeratorInputString, numeratorValueType);
               Object convertedDenominatorValue = getConvertedValue(denominatorInputString, denominatorValueType);
               setRatioValues(avv[i], convertedNumeratorValue, convertedDenominatorValue);
            } else if (attrValueClass == UnitValueDefaultView.class) {
               Class valueType = attrValueClass.getDeclaredField("value").getType();
               Object convertedValue = getConvertedValue(attrValue, valueType);
               setValue(avv[i], getDefaultUnitValue(avv[i], (Double) convertedValue));
            } else if (attrValueClass == URLValueDefaultView.class) {
               Class linkValueType = attrValueClass.getDeclaredField("value").getType();
               Class labelValueType = attrValueClass.getDeclaredField("description").getType();
               String linkInputString = attrValue.substring(attrValue.indexOf("href=\"") + 6, attrValue.indexOf("\">"));
               String labelInputString = attrValue.substring(attrValue.indexOf("\">") + 2, attrValue.indexOf("</a>"));
               Object convertedLinkValue = getConvertedValue(linkInputString, linkValueType);
               Object convertedLabelValue = getConvertedValue(labelInputString, labelValueType);
               setURLValues(avv[i], convertedLinkValue, convertedLabelValue);
            } else if (attrValueClass != ReferenceValueDefaultView.class) {
               if (attrValueClass == FloatValueDefaultView.class) {
                  Class valueType = attrValueClass.getDeclaredField("value").getType();
                  Object convertedValue = getConvertedValue(attrValue, valueType);
                  String valueStr = attrValue;
                  int precision = valueStr.length();
                  if (valueStr.indexOf(".") != -1) {
                     precision--;
                  } else {
                     precision = 0;
                  }
                  ((FloatValueDefaultView) avv[i]).setPrecision(precision);
                  setValue(avv[i], convertedValue);
               } else {
                  Class valueType = attrValueClass.getDeclaredField("value").getType();
                  Object convertedValue = getConvertedValue(attrValue, valueType);
                  setValue(avv[i], convertedValue);
               }
            }
            dac.updateAttributeValue(avv[i]);
         }
         matchFound = true;
         break;
      }
      return matchFound;
   }

   @SuppressWarnings("deprecation")
   public static Iterated getBranchPointOf(Iterated iterated) throws VersionControlException, WTException {
      if (iterated instanceof Versioned) {
         ControlBranch ctrlBranch = VersionControlServerHelper.getControlBranch(iterated);
         if (ctrlBranch != null) {
            ObjectReference objectRef = ctrlBranch.getBranchPoint();
            if (!objectRef.equals(ObjectReference.newObjectReference())) {
               Iterated ret = (Iterated) objectRef.getObject();
               return ret;
            }
         }
      }
      return null;
   }

   /**
    * ��ibaholder1��IBA����copy��ibaholder2
    * @param ibaHolder1
    * @param ibaHolder2
    * @return
    * @throws WTException
    */
   public static IBAHolder copyIBAValues(IBAHolder ibaHolder1, IBAHolder ibaHolder2) throws WTException {
      try {
         ibaHolder1 = refreshIBAValues(ibaHolder1);
         DefaultAttributeContainer dac1 = (DefaultAttributeContainer) ibaHolder1.getAttributeContainer();

         DefaultAttributeContainer dac2 = new DefaultAttributeContainer();
         dac2.setConstraintGroups(dac1.getConstraintGroups());
         dac2.setConstraintParameter(dac1.getConstraintParameter());
         AbstractValueView avv[] = null;
         avv = ibaHolder2.getAttributeContainer() == null ? DefaultAttributeContainerHelper.getUnclonedAttributeValues(dac1) : DefaultAttributeContainerHelper
               .getUnclonedAttributeValues((DefaultAttributeContainer) ibaHolder2.getAttributeContainer());
         HashMap hashmap = new HashMap(57);
         for (int i = 0; avv != null && i < avv.length; i++) {
            if (!(avv[i] instanceof ReferenceValueDefaultView))
               continue;
            ReferenceValueDefaultView refValueDefaultView = new ReferenceValueDefaultView((ReferenceDefView) avv[i].getDefinition());
            refValueDefaultView.setLiteIBAReferenceable(((ReferenceValueDefaultView) avv[i]).getLiteIBAReferenceable());
            refValueDefaultView.setState(3);
            try {
               dac2.addAttributeValue(refValueDefaultView);
            } catch (IBAConstraintException ibaconstraintexception) {
            }
            hashmap.put(String.valueOf(avv[i].getKey()), refValueDefaultView);
         }

         for (int j = 0; avv != null && j < avv.length; j++) {
            Object obj = null;
            if (avv[j] instanceof BooleanValueDefaultView)
               obj = new BooleanValueDefaultView((BooleanDefView) avv[j].getDefinition(), ((BooleanValueDefaultView) avv[j]).isValue());
            else if (avv[j] instanceof FloatValueDefaultView)
               obj = new FloatValueDefaultView((FloatDefView) avv[j].getDefinition(), ((FloatValueDefaultView) avv[j]).getValue(), ((FloatValueDefaultView) avv[j]).getPrecision());
            else if (avv[j] instanceof IntegerValueDefaultView)
               obj = new IntegerValueDefaultView((IntegerDefView) avv[j].getDefinition(), ((IntegerValueDefaultView) avv[j]).getValue());
            else if (avv[j] instanceof RatioValueDefaultView)
               obj = new RatioValueDefaultView((RatioDefView) avv[j].getDefinition(), ((RatioValueDefaultView) avv[j]).getValue(), ((RatioValueDefaultView) avv[j]).getDenominator());
            else if (avv[j] instanceof StringValueDefaultView)
               obj = new StringValueDefaultView((StringDefView) avv[j].getDefinition(), ((StringValueDefaultView) avv[j]).getValue());
            else if (avv[j] instanceof TimestampValueDefaultView)
               obj = new TimestampValueDefaultView((TimestampDefView) avv[j].getDefinition(), ((TimestampValueDefaultView) avv[j]).getValue());
            else if (avv[j] instanceof URLValueDefaultView)
               obj = new URLValueDefaultView((URLDefView) avv[j].getDefinition(), ((URLValueDefaultView) avv[j]).getValue(), ((URLValueDefaultView) avv[j]).getDescription());
            else if (avv[j] instanceof UnitValueDefaultView)
               obj = new UnitValueDefaultView((UnitDefView) avv[j].getDefinition(), ((UnitValueDefaultView) avv[j]).getValue(), ((UnitValueDefaultView) avv[j]).getPrecision());
            if (avv[j] instanceof AbstractContextualValueDefaultView) {
               ReferenceValueDefaultView referencevaluedefaultview1 = ((AbstractContextualValueDefaultView) avv[j]).getReferenceValueDefaultView();
               if (referencevaluedefaultview1 != null) {
                  referencevaluedefaultview1 = (ReferenceValueDefaultView) hashmap.get(String.valueOf(referencevaluedefaultview1.getKey()));
                  ((AbstractContextualValueDefaultView) (obj)).setReferenceValueDefaultView(referencevaluedefaultview1);
               }
            }
            if (obj == null)
               continue;
            ((AbstractContextualValueDefaultView) (obj)).setState(3);
            try {
               dac2.addAttributeValue(((AbstractValueView) (obj)));
            } catch (IBAConstraintException ibaconstraintexception1) {
            }
         }

         ibaHolder2.setAttributeContainer(dac2);

      } catch (WTPropertyVetoException wtpropertyvetoexception) {
         throw new WTException(wtpropertyvetoexception);
      }

      return ibaHolder2;
   }

   public static IBAHolder refreshIBAValues(IBAHolder ibaholder) throws WTException {
      try {
         return IBAValueHelper.service.refreshAttributeContainer(ibaholder, "CSM", null, null);
      } catch (RemoteException re) {
         throw new WTException(re);
      }
   }
}
