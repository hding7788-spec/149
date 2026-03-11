package com.ptc.extend.ixb.center;

import com.ptc.core.lwc.common.view.PropertyHolderHelper;
import com.ptc.core.lwc.common.view.TypeDefinitionReadView;
import com.ptc.core.lwc.server.TypeDefinitionServiceHelper;
import com.ptc.core.meta.common.AttributeTypeIdentifier;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.TypeIdentifierHelper;
import com.ptc.core.meta.common.impl.WCTypeIdentifier;
import com.ptc.core.meta.type.common.TypeInstance;
import com.ptc.core.meta.type.mgmt.common.TypeDefinitionDefaultView;
import com.ptc.core.meta.type.mgmt.server.impl.AdminTypeDefinitionCache;
import com.ptc.core.meta.type.server.TypeInstanceUtility;
import com.ptc.extend.ixb.CmExpImpObject;
import com.ptc.extend.ixb.CmExporter;
import com.ptc.extend.ixb.CmImporter;
import com.ptc.extend.util.ObjectProperty;
import com.ptc.extend.util.TypeUtility;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.ixb.ExpImpLogger;
import ext.sast.center.synch.MQExpImpUtil;
import ext.sast.center.util.ProductConvertUtil;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.doc.WTDocumentDependencyLink;
import wt.doc.WTDocumentUsageLink;
import wt.epm.EPMDocument;
import wt.epm.structure.EPMDescribeLink;
import wt.epm.structure.EPMReferenceLink;
import wt.epm.structure.EPMVariantLink;
import wt.facade.ixb.IxbDocument;
import wt.facade.ixb.IxbElement;
import wt.fc.*;
import wt.fc.collections.WTArrayList;
import wt.iba.definition.*;
import wt.iba.definition.litedefinition.*;
import wt.iba.definition.service.IBADefinitionCache;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.definition.service.IBADomainHelper;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.IBAHolder;
import wt.iba.value.StringValue;
import wt.iba.value.litevalue.*;
import wt.iba.value.service.IBAValueDBService;
import wt.iba.value.service.IBAValueHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.ixb.handlers.forattributes.ExpImpForIBAAttr;
import wt.ixb.handlers.forattributes.IBAValues;
import wt.ixb.handlers.forclasses.ExpImpForIBADefinition;
import wt.ixb.handlers.forclasses.ExpImpForWTTypeDefinition;
import wt.ixb.handlers.netmarkets.ProjectIXUtils;
import wt.ixb.publicforapps.IxbHelper;
import wt.ixb.publicforhandlers.IxbHndHelper;
import wt.ixb.publicforhandlers.LogHelper;
import wt.org.WTOrganization;
import wt.part.WTPart;
import wt.part.WTPartAlternateLink;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartReferenceLink;
import wt.part.WTPartSubstituteLink;
import wt.part.WTPartUsageLink;
import wt.pdmlink.PDMLinkProduct;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.type.TypeDefinitionReference;
import wt.type.Typed;
import wt.type.TypedUtility;
import wt.type.TypedUtilityServiceHelper;
import wt.units.Unit;
import wt.units.display.DefaultUnitDisplayInfo;
import wt.units.display.DefaultUnitRenderer;
import wt.units.display.QuantityOfMeasure;
import wt.units.service.QuantityOfMeasureDefaultView;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.*;

public abstract class MQExpImpObject extends CmExpImpObject{
    public static String NO_VALUE = "((null))";

    protected CmExporter expHdl = null;
    protected CmImporter impHdl = null;
    protected IxbDocument ixbdocument = null;
    protected IxbElement root = null;
    private String pfilename;
    private String remoteId;
    public ExpImpLogger logger;
    public Properties prop;
	public Properties prop2;
    String sendFrom;

    public String getSendFrom() {
		return sendFrom;
	}

	public void setSendFrom(String sendFrom) {
		this.sendFrom = sendFrom;
	}
    protected MQExpImpObject() {
        logger = ExpImpLogger.getInstance();
    }

    protected MQExpImpObject(CmExporter expHdl)
            throws WTException {
        this.expHdl = expHdl;
        this.ixbdocument = IxbHelper.newIxbDocument();
        this.root = this.ixbdocument.createRootElement(getRootTag());
    }

    protected MQExpImpObject(CmImporter impHdl, String fname) throws WTException {
        logger = ExpImpLogger.getInstance();
        this.impHdl = impHdl;
        this.pfilename = fname;
        prepareForImport(fname);
        String localId = getElementValue("ObjectID/localId");
        if(localId!=null&&localId.contains(":")){
        	localId = localId.substring(localId.indexOf(":")+1);
        }
        this.remoteId =localId;

        try {
            WTProperties wtProperties = WTProperties.getLocalProperties();
            String codebasePath = wtProperties.getProperty("wt.codebase.location");
            prop = new Properties();
			prop2 = new Properties();
            String filePath = codebasePath + File.separator + "ext"
                    + File.separator + "casc"
                    + File.separator + "ixb" + File.separator + "ixbconfig.properties";

            String	filePath2 = codebasePath + File.separator + "ext"
                        + File.separator + "casc"
                        + File.separator + "ixb" + File.separator + "zykixbconfig.properties";

            FileInputStream inputStream = new FileInputStream(new File(filePath));
			 FileInputStream inputStream2 = new FileInputStream(new File(filePath2));
            prop.load(inputStream);
			prop2.load(inputStream2);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.log(e.getLocalizedMessage());
        }

    }

    protected String getPfilename() {
        return this.pfilename;
    }

    protected String getRemoteId() {
        return this.remoteId;
    }

    protected String emptyIfNull(String s) {
        if (s == null) {
            return NO_VALUE;
        }
        return escapeString(s);
    }

    protected String escapeString(String s) {
        String rtn = s;

        rtn = rtn.replaceAll("&", "&amp;").replaceAll("\r\n", "&br;").replaceAll("\r", "&br;").replaceAll("\n", "&br;")
                .replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;").replaceAll("'", "&apos;");
        return rtn;
    }

    protected String restoreString(String s) {
        String rtn = s;

        rtn = rtn.replaceAll("&apos;", "'").replaceAll("&quot;", "\"").replaceAll("&amp;", "&").replaceAll("&gt;", ">")
                .replaceAll("&lt;", "<").replaceAll("&br;", "\n");
        return rtn;
    }

    private String replaceString(String s, String s1, String s2) {
        if ((s == null) || (s1 == null) || (s2 == null) || (s.length() < 1) || (s1.length() < 1) || (s2.length() < 1))
            return s;
        String s3 = null;
        try {
            s3 = new String(s);
            for (int i = 0; (i = s3.indexOf(s1, i)) >= 0;)
                s3 = s3.substring(0, i) + s2 + s3.substring(i + s1.length(), s3.length());
        } catch (Exception exception) {
            s3 = s;
        }
        return s3;
    }

    protected String getNoTrimElementValue(String s) throws WTException {
        return getNoTrimElementValue(this.root, s);
    }
    protected String getNoTrimElementValue(IxbElement ixbelement, String s) throws WTException {
        String value = ixbelement.getValue(s);
        if ((value == null) || (value.equals(NO_VALUE)) || (value.equals(" ((null))"))) {
            return null;
        }
        return restoreString(value);
    }

    protected String getElementValue(String s) throws WTException {
        return getElementValue(this.root, s);
    }

    protected String getElementValue(IxbElement ixbelement, String s) throws WTException {
        String value = ixbelement.getValue(s);
        if ((value == null) || (value.equals(NO_VALUE)) || (value.equals(" ((null))"))) {
            return null;
        }
        return restoreString(value.trim());
    }

    protected Boolean getBooleanValue(String s) throws WTException {
        return getBooleanValue(this.root, s);
    }

    protected Boolean getBooleanValue(IxbElement ixbelement, String s) throws WTException {
        return ixbelement.getBooleanValue(s);
    }

    protected Enumeration getElements(String s) throws WTException {
        return getElements(this.root, s);
    }

    protected Enumeration getElements(IxbElement ixbelement, String s) throws WTException {
        return ixbelement.getElements(s);
    }

    protected IxbElement addElement(String s) throws WTException {
        return addElement(this.root, s);
    }

    protected IxbElement addElement(IxbElement ixbelement, String s) throws WTException {
        return ixbelement.addElement(s);
    }

    protected String getRefFromObject(Persistable persistable) {
        try {
            ReferenceFactory referencefactory = new ReferenceFactory();
            return referencefactory.getReferenceString(ObjectReference.newObjectReference(persistable.getPersistInfo()
                    .getObjectIdentifier()));
        } catch (Exception exception) {
        }
        return null;
    }

    protected void reallyStore() throws WTException {
        if (this.expHdl != null)
            this.expHdl.storeDocument(this.ixbdocument);
    }

    protected void logger(String s) {
        if (this.expHdl != null)
            this.expHdl.logger(s);
        else if (this.impHdl != null)
            this.impHdl.logger(s);
        else System.out.println("ExpImp:" + String.valueOf(s));
    }

    protected void processException(Exception e) throws WTException {
        if (this.expHdl != null) {
            this.expHdl.processException(e);
        } else if (this.impHdl != null) {
            this.impHdl.processException(e);
        } else {
            e.printStackTrace();
            if ((e instanceof WTException)) {
                throw ((WTException) e);
            }
            throw new WTException(e);
        }
    }

    private String adjustLocalExternalTypeId(String remoteExternalTypeId) throws WTException {
        String typeIdStr=null;
        try {
            typeIdStr = getPropertiesValue(remoteExternalTypeId);
            if(typeIdStr==null||"".equals(typeIdStr)){
            	if(remoteExternalTypeId!=null&&remoteExternalTypeId.contains("wt.doc.WTDocument")){
            		typeIdStr = "WCTYPE|wt.doc.WTDocument|casc.sast.149.RESEARCH_DOC";
            	}
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (typeIdStr!=null) {
            remoteExternalTypeId = typeIdStr;
        }
        if (!remoteExternalTypeId.startsWith("WCTYPE"))
            throw new WTException("Error Format ExternalTypeId:[" + remoteExternalTypeId + "]");
        String typeId = remoteExternalTypeId.substring(WCTypeIdentifier.PROTOCOL_LENGTH);
        int i = typeId.indexOf("|");
        String cname;
        String typeName;
        if (i == -1) {
            typeName = typeId;
            cname = "";
        } else {
            typeName = typeId.substring(0, i);
            cname = typeId.substring(i + 1);
        }
        try {
            Class.forName(typeName);
        } catch (ClassNotFoundException cnfe) {
            throw new WTException((new StringBuilder()).append("Class Not Found:[").append(typeName).append("]")
                    .toString());
        }
        String externalTypename = (new StringBuilder()).append("WCTYPE").append("|").append(typeName).toString();
        if (cname.equals(""))
            return externalTypename;
        String logicalId = cname.substring(cname.lastIndexOf(".") + 1);
        TypeIdentifier ti = TypeUtility.getChildTypeIdentifier(externalTypename, logicalId);
        if (ti == null) {
            logger.log("Can not adjust typeId:[" + remoteExternalTypeId + "]");
            if(remoteExternalTypeId!=null && remoteExternalTypeId.contains("wt.part.WTPart")){
                return  "WCTYPE|wt.part.WTPart";
            }
            return null;
        }
        return ti.toExternalForm();
    }

    protected void exportLocalIdAttribute(Object obj) throws WTException {
        exportLocalIdAttribute(obj, this.root);
    }

    protected void exportLocalIdAttribute(Object obj, IxbElement ixbelement) throws WTException {
        try {
            String s = IxbHndHelper.getObjectIdImage((Persistable) obj);

            if(obj instanceof WTDocument){
    			ixbelement.addValue("modelType", MQExpImpConstants.XML_MQDOCUMENT);
            }else if(obj instanceof WTPart){
    			ixbelement.addValue("modelType", MQExpImpConstants.XML_MQPART.toUpperCase());
            }else if(obj instanceof EPMDocument){
    			ixbelement.addValue("modelType", MQExpImpConstants.XML_MQCADDOCUMENT);
            }else if(obj instanceof ProcessEnvelope){
    			ixbelement.addValue("modelType", MQExpImpConstants.XML_MQECA);
            } else if(obj instanceof WTChangeOrder2){
    			ixbelement.addValue("modelType", MQExpImpConstants.XML_MQECO);
            }

            ixbelement.addValue("ObjectID/localId", emptyIfNull(s));
        } catch (Exception exception) {
            logger.log("Exception in exportLocalIdAttribute, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
            processException(exception);
        }
    }

    protected void exportTypeDefinitionAttribute(Object obj) throws WTException {
        exportTypeDefinitionAttribute(obj, this.root);
    }

    protected void exportTypeDefinitionAttribute(Object obj, IxbElement ixbelement) throws WTException {
        try {
            if (!(obj instanceof Typed))
                return;
            String s = TypedUtilityServiceHelper.service.getExternalTypeIdentifier((Typed) obj);
            ixbelement.addValue("originClassId", emptyIfNull(s));
            TypeDefinitionReadView tv = TypeDefinitionServiceHelper.service.getTypeDefView(TypeIdentifierHelper.getType(obj));
            String zhType = PropertyHolderHelper.getDisplayName(tv, Locale.CHINA);
            ixbelement.addValue("originName", emptyIfNull(zhType));
            expHdl.storeTypeDefinition(s);
        } catch (Exception exception) {
            logger.log("Exception in exportTypeDefinitionAttribute, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
            processException(exception);
        }
    }

    protected String getSavePathInJar(Object obj) {
        if (this.expHdl != null) {
            return this.expHdl.getSavePathInJar(obj);
        }
        return "";
    }

    protected void exportIBAAttribute(Object obj) throws WTException {
        exportIBAAttribute(obj);
    }

    protected void exportIBAAttribute(Object obj, IxbElement ixbelement) throws WTException {
        if (!(obj instanceof IBAHolder))
            return;
        try {
            IBAHolder ibaholder = (IBAHolder) obj;
            Hashtable hashtable = new Hashtable();
            Hashtable defination = new Hashtable();
            boolean flag = getIBAttributes(ibaholder, hashtable, defination);

            for (Enumeration enumeration = hashtable.keys(); enumeration.hasMoreElements();) {
                String s2 = (String) enumeration.nextElement();
                AttributeDefDefaultView addv = (AttributeDefDefaultView) defination.get(s2);
                AbstractAttributeDefinition aad = (AbstractAttributeDefinition) IBADefinitionCache
                        .getIBADefinitionCache().getAttributeDefinition(addv.getObjectID());
                String valueClassname = aad.getValueClassname();
                int ji = valueClassname.lastIndexOf('.');
                if (ji > 0)
                    valueClassname = valueClassname.substring(ji + 1);
                Object obj1 = hashtable.get(s2);
                String s3 = valueClassname;
                String s4 = IxbHndHelper.getObjectIdImage(aad);
                String s5 = null;
                String s6 = null;
                if ((obj1 instanceof Vector)) {
                    Vector vector3 = (Vector) obj1;
                    for (int j = 0; j < vector3.size(); j++) {
                        String[] as = (String[]) vector3.elementAt(j);
                        String s7 = as[0];
                        if (s3.equals("RatioValue")) {
                            int k = s7.lastIndexOf(",");
                            s5 = s7.substring(k + 1);
                            s7 = s7.substring(0, k);
                        } else if ((s3.equals("UnitValue")) || (s3.equals("FloatValue"))) {
                            int l = s7.lastIndexOf(",");
                            s6 = s7.substring(l + 1);
                            s7 = s7.substring(0, l);
                        }
                        if (flag)
                            addIBAElementToXML(ixbelement, "ext", s2, s7, s3, s6, s5, as[2]);
                        else addIBAElementToXML(ixbelement, "ext", s2, s7, s3, s6, s5);
                    }
                }
            }
        } catch (Exception exception) {
            logger.log("Exception in exportIBAAttribute, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
            processException(exception);
        }
    }

    protected void addIBAElementToXML(IxbElement ixbelement, String s, String s1, String s2, String s3, String s4,
            String s5, String s6) throws WTException {
        IxbElement ixbelement1 = ixbelement.addElement(s);
        if (s4 != null)
            ixbelement1.addValue("precision", emptyIfNull(s4));
        if (s5 != null)
            ixbelement1.addValue("denominator", emptyIfNull(s5));
        String extPath = emptyIfNull(s1);
        //TODO 本地属性转为中心与属性
        //extPath = ext.sast.center.util.ProductConvertUtil.getStandardAttr(extPath);
        ixbelement1.addValue("extPath", extPath);
        ixbelement1.addValue("extValue", emptyIfNull(s2));
        ixbelement1.addValue("extType", emptyIfNull(s3));
        expHdl.storeIBADefinition(s1);
    }

    protected void addIBAElementToXML(IxbElement ixbelement, String s, String s1, String s2, String s3, String s4,
            String s5) throws WTException {
        addIBAElementToXML(ixbelement, s, s1, s2, s3, s4, s5, null);
    }

    protected boolean getIBAttributes(IBAHolder ibaholder, Hashtable hashtable, Hashtable defination) throws WTException {
        boolean flag = false;
        try {
            ibaholder = IBAValueHelper.service.refreshAttributeContainer(ibaholder,
                    IxbHndHelper.getContainerConstraintParameter(), null, null);
            DefaultAttributeContainer defaultattributecontainer = (DefaultAttributeContainer) ibaholder
                    .getAttributeContainer();
            if (defaultattributecontainer != null) {
                AbstractValueView[] aabstractvalueview = defaultattributecontainer.getAttributeValues();
                for (int i = 0; i < aabstractvalueview.length; i++) {
                    AbstractValueView abstractvalueview1 = aabstractvalueview[i];
                    AttributeDefDefaultView attributedefdefaultview = abstractvalueview1.getDefinition();
                    AbstractAttributeDefinition abstractattributedefinition = (AbstractAttributeDefinition) IBADefinitionCache
                            .getIBADefinitionCache().getAttributeDefinition(attributedefdefaultview.getObjectID());
                    String s = IxbHndHelper.getObjectIdImage(abstractattributedefinition);
                    String s1 = ExpImpForIBAAttr
                            .getPathOfAttributeDefinition_WithoutOrganizer(abstractattributedefinition);
                    String s2 = null;
                    String s3 = null;
                    if ((abstractvalueview1 instanceof AbstractContextualValueDefaultView)) {
                        s2 = ((AbstractContextualValueDefaultView) abstractvalueview1).getValueAsString();
                        ReferenceValueDefaultView referencevaluedefaultview = ((AbstractContextualValueDefaultView) abstractvalueview1)
                                .getReferenceValueDefaultView();
                        if (referencevaluedefaultview != null) {
                            flag = true;
                            s3 = referencevaluedefaultview.getObjectID().getStringValue();
                        }
                    } else if ((abstractvalueview1 instanceof ReferenceValueDefaultView)) {
                        ReferenceValueDefaultView referencevaluedefaultview1 = (ReferenceValueDefaultView) abstractvalueview1;
                        if (referencevaluedefaultview1.getLiteIBAReferenceable() != null)
                            s2 = ""
                                    + referencevaluedefaultview1.getLiteIBAReferenceable().getReferencedLiteObject()
                                            .getObjectID();
                        s3 = referencevaluedefaultview1.getObjectID().getStringValue();
                    }
                    if (s2 == null)
                        s2 = NO_VALUE;
                    if ((abstractvalueview1 instanceof RatioValueDefaultView)) {
                        RatioValueDefaultView ratiovaluedefaultview = (RatioValueDefaultView) abstractvalueview1;
                        double d = ratiovaluedefaultview.getDenominator();
                        s2 = s2 + "," + d;
                    } else if ((abstractvalueview1 instanceof UnitValueDefaultView)) {
                        UnitValueDefaultView unitvaluedefaultview = (UnitValueDefaultView) abstractvalueview1;
                        UnitDefView unitdefview = (UnitDefView) unitvaluedefaultview.getDefinition();
                        QuantityOfMeasureDefaultView quantityofmeasuredefaultview = unitdefview
                                .getQuantityOfMeasureDefaultView();
                        String s5 = quantityofmeasuredefaultview.getBaseUnit();
                        if (s2.indexOf(" " + s5) < 0)
                            s2 = s2 + " " + s5;
                        int k = unitvaluedefaultview.getPrecision();
                        s2 = s2 + "," + k;
                    } else if ((abstractvalueview1 instanceof FloatValueDefaultView)) {
                        FloatValueDefaultView floatvaluedefaultview = (FloatValueDefaultView) abstractvalueview1;
                        int j = floatvaluedefaultview.getPrecision();
                        s2 = s2 + "," + j;
                    } else if ((abstractvalueview1 instanceof URLValueDefaultView)) {
                        URLValueDefaultView urlvaluedefaultview = (URLValueDefaultView) abstractvalueview1;
                        String s4 = urlvaluedefaultview.getDescription();
                        s2 = s2 + "," + s4;
                    }
                    if (hashtable.get(s1) == null)
                        hashtable.put(s1, new Vector());
                    String[] as = new String[3];
                    as[0] = s2;
                    as[1] = s;
                    as[2] = s3;
                    Vector vector = (Vector) hashtable.get(s1);
                    vector.addElement(as);
                    defination.put(s1, attributedefdefaultview);
                }
            }
        } catch (Exception exception) {
            logger.log("Exception getIBAAttribute: IBAholder=<" + ibaholder + ">");
            if ((exception instanceof WTException))
                throw ((WTException) exception);
            throw new WTException(exception);
        }
        return flag;
    }

    private Vector getEncodedIbaAttributes(TypeDefinitionDefaultView defV) throws Exception {
        Vector v = new Vector();
        Vector tempv = new Vector();
        AttributeTypeIdentifier[] attrTypeIds = defV.getAttributeTypeIdentifiers();
        if (attrTypeIds != null) {
            for (int ii = 0; ii < attrTypeIds.length; ii++) {
                AttributeTypeIdentifier attrTypeId = attrTypeIds[ii];
                String attributePath = attrTypeId.getAttributeName().replace('|', '/');
                Object attributevalues = defV.get(attrTypeId);

                AttributeDefDefaultView addv = IBADefinitionHelper.service
                        .getAttributeDefDefaultViewByPath(attributePath);
                AbstractAttributeDefinition aad = (AbstractAttributeDefinition) IBADefinitionCache
                        .getIBADefinitionCache().getAttributeDefinition(addv.getObjectID());
                String valueClassname = aad.getValueClassname();
                int ji = valueClassname.lastIndexOf('.');
                if (ji > 0)
                    valueClassname = valueClassname.substring(ji + 1);
                String type = valueClassname;
                String ibaDefOid = IxbHndHelper.getObjectIdImage(aad);
                String valueKey = null;
                String[] strArr = new String[2];
                String strAttrValues = null;
                if ((attributevalues instanceof Object[])) {
                    Object[] attrValues = (Object[]) attributevalues;
                    for (int jj = 0; jj < attrValues.length; jj++) {
                        strAttrValues = IxbHndHelper.getAttributeValueAsString(attrValues[jj]);
                        valueKey = ExpImpForWTTypeDefinition.encodeIBAValue(type, attributePath, strAttrValues);
                        if (!tempv.contains(valueKey)) {
                            strArr[0] = valueKey;
                            strArr[1] = ibaDefOid;
                            v.addElement(strArr);
                        }
                    }
                } else {
                    strAttrValues = IxbHndHelper.getAttributeValueAsString(attributevalues);
                    valueKey = ExpImpForWTTypeDefinition.encodeIBAValue(type, attributePath, strAttrValues);
                    if (!tempv.contains(valueKey)) {
                        strArr[0] = valueKey;
                        strArr[1] = ibaDefOid;
                        v.addElement(strArr);
                    }
                }
            }
        }
        return v;
    }

    protected void prepareForImport(String fname) throws WTException {
        this.ixbdocument = this.impHdl.getIxbDocumentFromJar(fname);
        this.root = this.ixbdocument.getRootElement();
        ProjectIXUtils.checkTag(this.root, getRootTag());
    }

    protected boolean isTypeDefinitionImported() {
        return isTypeDefinitionImported(this.root);
    }

    protected boolean isTypeDefinitionImported(IxbElement ixbelement) {
        try {
            String s1 = getElementValue(ixbelement, "externalTypeId");
            if (s1 == null)
                s1 = getElementValue(ixbelement, "masterExternalTypeId");
            if (s1 == null)
                return true;
            String s2 = adjustLocalExternalTypeId(s1);
            if (s2 == null)
                return false;
            s2 = s2.substring(s2.indexOf("|") + 1);
            TypeDefinitionReference typedefinitionreference = TypedUtility.getTypeDefinitionReference(s2);
            if (typedefinitionreference != null)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    protected Object importTypeDefinitionAttribute(Object obj) throws WTException {
        return importTypeDefinitionAttribute(obj, this.root, this.root);
    }
    protected Object importTypeDefinitionAttribute(Object obj, IxbElement ixbelement, IxbElement ixbelement1)

            throws WTException {
        if (!(obj instanceof Typed))
            return obj;
        Typed typed = (Typed) obj;
        try {
            String s = null;
            String modelType = getElementValue(ixbelement, "modelType");
            if (obj instanceof WTDocument){
                if("Document".equalsIgnoreCase(modelType)) {
                    s = "WCTYPE|wt.doc.WTDocument|casc.sast.149.WAILAI_DOC";//需新增外来文件 软类型
                }else if("TNO".equalsIgnoreCase(modelType)) {
                    s = "WCTYPE|wt.doc.WTDocument|casc.sast.149.DESIGN_DOC|casc.sast.149.TECHNOTICE_DOC";//技术通知单
                }
            }else if (obj instanceof WTPart){
            	s ="WCTYPE|wt.part.WTPart";
            }else if (obj instanceof EPMDocument){
            	s ="WCTYPE|wt.epm.EPMDocument|casc.sast.149.DefaultEPMDocument";
            }else if (obj instanceof ProcessEnvelope){
                if("DisOrder".equalsIgnoreCase(modelType)) {
                    s = "WCTYPE|ext.ases.envelope.ProcessEnvelope|casc.sast.149.RELEASEFORM";
                }else {
                    s = "WCTYPE|ext.ases.envelope.ProcessEnvelope|casc.sast.149.APPROVEFORM";
                }
            }else if (obj instanceof ChangePackaged){
            	s ="WCTYPE|ext.ases.changepackaged.ChangePackaged";
            }
            if(s==null){
            	s = getExternalTypeId(obj);
            }
            if (s == null)
                return typed;
            DefaultAttributeContainer defaultattributecontainer = (DefaultAttributeContainer) typed
                    .getAttributeContainer();
            if (defaultattributecontainer != null) {
                IxbHndHelper.setContainerConstraintParameter(defaultattributecontainer);
                IBAValueHelper.service.refreshAttributeConstraint(typed,
                        IxbHndHelper.getContainerConstraintParameter(), null);
            }
            importTypeIbaValues(typed, ixbelement,s);
            updateIBAValues(typed, ixbelement1);
        } catch (Exception e) {
            logger.log("Exception in exportTypeDefinitionAttribute, ob=<" + obj + ">");
            processException(e);
        }
        return typed;
    }

    private String getExternalTypeId(Object obj) {

		if( obj instanceof WTPartReferenceLink){
        	return  "WCTYPE|wt.part.WTPartReferenceLink";
        }
        if( obj instanceof WTPartDescribeLink){
        	return  "WCTYPE|wt.part.WTPartDescribeLink";
        }
        if( obj instanceof  WTDocumentDependencyLink){
        	return "WCTYPE|wt.doc.WTDocumentDependencyLink";
        }
        if(obj instanceof  WTDocumentUsageLink){
        	return "WCTYPE|wt.doc.WTDocumentUsageLink";
        }
        if(obj instanceof  EPMReferenceLink){
        	return "WCTYPE|wt.epm.structure.EPMReferenceLink";
        }
        if(obj instanceof  EPMDescribeLink){
        	return "WCTYPE|wt.epm.structure.EPMDescribeLink";
        }
        if(obj instanceof  EPMVariantLink){
        	return "WCTYPE|wt.epm.structure.EPMVariantLink";
        }
        if( obj instanceof  WTPartAlternateLink){
        	return "WCTYPE|wt.part.WTPartAlternateLink";
        }
        if(obj instanceof  WTPartSubstituteLink){
        	return "WCTYPE|wt.part.WTPartSubstituteLink";
        }

        if(obj instanceof  WTPartUsageLink){
        	return "WCTYPE|wt.part.WTPartUsageLink";
        }
        return null;
	}

    private void importTypeIbaValues(Typed typed, IxbElement ixbelement, String s)
            throws WTException, WTPropertyVetoException {
    	String s2 = s;
        s2 = s2.substring(s2.indexOf("|") + 1);
        TypeDefinitionReference typedefinitionreference = TypedUtility.getTypeDefinitionReference(s2);
        if (typedefinitionreference != null)
            typed.setTypeDefinitionReference(typedefinitionreference);
    }

    private void updateIBAValues(Typed typed, IxbElement ixbelement) throws WTException, WTPropertyVetoException,
            RemoteException {
        TypeInstance typeinstance = TypeInstanceUtility.getIBAValues(typed);
        TypeInstanceUtility.populateMissingTypeContent(typeinstance, null);
        typed.setAttributeContainer(null);
        TypeInstanceUtility.updateIBAValues(typed, typeinstance);
        DefaultAttributeContainer defaultattributecontainer = (DefaultAttributeContainer) typed.getAttributeContainer();
        if (defaultattributecontainer != null) {
            IxbHndHelper.setContainerConstraintParameter(defaultattributecontainer);
            IBAValueHelper.service.refreshAttributeConstraint(typed, IxbHndHelper.getContainerConstraintParameter(),
                    null);
        }
    }

    private String getInternetDomainFromExternalTypeId(String s) throws WTException {
        String s1 = null;
        int i = s.indexOf("|");
        String s3 = i < 0 ? s : s.substring(0, i);
        TypeDefinitionDefaultView typedefinitiondefaultview = AdminTypeDefinitionCache.getInstance().getTypeDefinition(
                s3, true);
        String s4 = ExpImpForWTTypeDefinition.getFullPath(typedefinitiondefaultview);
        s1 = s4 + (i < 0 ? "" : new StringBuilder().append("/").append(s.substring(i + 1)).toString());
        s1 = s1.replace('|', '/');
        return s1;
    }

    protected IBAHolder importIBAAttribute(Object obj) throws WTException {
        return importIBAAttribute(obj, this.root);
    }

    protected IBAHolder importIBAAttribute(Object obj, IxbElement ixbelement) throws WTException {
        IBAHolder ob = (IBAHolder) obj;
        try {
            ob = prepareForImportOfIBA(ob);
            ob = IBAValueHelper.service.refreshAttributeContainer(ob, IxbHndHelper.getContainerConstraintParameter(),
                    null, null);
            DefaultAttributeContainer container = (DefaultAttributeContainer) ob.getAttributeContainer();
            if (container == null) {
                logger.log("IBAHolder: <" + ob
                        + "> has not been properly initialized by the method 'prepareForImportOfIBA'");
            }

            AbstractValueView[] valueVs = container.getAttributeValues();
            if (valueVs != null) {
                for (int ii = 0; ii < valueVs.length; ii++) {
                    container.deleteAttributeValue(valueVs[ii]);
                }
            }
            if (PersistenceHelper.isPersistent(ob)) {
                IBAValueDBService serv = IBAValueDBService.newIBAValueDBService();
                serv.updateAttributeContainer(ob, IxbHndHelper.getContainerConstraintParameter(), null, null);
            }
            container = new DefaultAttributeContainer();
            ob.setAttributeContainer(container);

            IxbHndHelper.setContainerConstraintParameter(container);
            IBAValueHelper.service.refreshAttributeConstraint(ob, IxbHndHelper.getContainerConstraintParameter(), null);

            ob = importIBA(ob, ixbelement);

            if (PersistenceHelper.isPersistent(ob)) {
                IBAValueDBService serv = IBAValueDBService.newIBAValueDBService();
                serv.updateAttributeContainer(ob, IxbHndHelper.getContainerConstraintParameter(), null, null);

                IxbHndHelper.setContainerConstraintParameter(container);
                IBAValueHelper.service.refreshAttributeConstraint(ob, IxbHndHelper.getContainerConstraintParameter(),
                        null);
            }
        } catch (Exception e) {
            logger.log("Exception importIBAAttribute: IBAholder=<" + ob + ">");
            logger.log(e.getLocalizedMessage());
            processException(e);
        }
        return ob;
    }
//&apos;  '
    private IBAHolder importIBA(IBAHolder holder, IxbElement fileXML) throws WTException {
        Enumeration ibas = fileXML.getElements("ext");
        HashMap dependencyMap = new HashMap();
        HashMap referenceIbaMap = new HashMap();
        while (ibas.hasMoreElements()) {
            IxbElement el = (IxbElement) ibas.nextElement();
            String objIdXML = el.getValue("extOid");
            //IBAValues i1 = new IBAValues(el);
            String extPath = el.getValue("extId");
        	if(extPath==null||"".equals(extPath)){
        		extPath = el.getValue("extPath");
        	}
        	String extType = el.getValue("extType");
        	if("String".equals(extType)){
        		extType = "StringValue";
        	}

        	String ibaValue = el.getValue("extValue");
            try {

            	//add by hding 20150209 begin
            	//String ibaValue = i1.getattrValue();
            	ibaValue= restoreString(ibaValue);

            	//add by hding 20150209 end

                AbstractValueView attrValue = addIBAttribute(holder, objIdXML,extPath, extType,
                		ibaValue);

            } catch (WTException e) {
                // TODO: handle exception
                logger.log("WARNING 未找到名称为：" +extPath + "的属性");
                logger.log(e.getLocalizedMessage());
            }
        }
        if (dependencyMap.size() > 0 && referenceIbaMap.size() > 0) {
            DefaultAttributeContainer container = (DefaultAttributeContainer) holder.getAttributeContainer();
            if (container != null) {
                Set keys = referenceIbaMap.keySet();
                for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
                    String key = (String) iterator.next();
                    ReferenceValueDefaultView refValue = (ReferenceValueDefaultView) referenceIbaMap.get(key);
                    ArrayList values = (ArrayList) dependencyMap.get(key);
                    if (values != null) {
                        for (Iterator iterator1 = values.iterator(); iterator1.hasNext();) {
                            AbstractValueView value = (AbstractValueView) iterator1.next();
                            if (value instanceof AbstractContextualValueDefaultView) {
                                try {
                                    ((AbstractContextualValueDefaultView) value).setReferenceValueDefaultView(refValue);
                                    container.updateAttributeValue(value);
                                } catch (WTPropertyVetoException e) {
                                    throw new WTException(e);
                                }
                            }
                        }

                    }
                }

            }
        }
        return holder;
    }

    private AbstractValueView addIBAttribute(IBAHolder holder, String objId, String attrPath, String attrType,
            String attrValueImage) throws WTException {
        try {
//        	if (attrPath.equals("ENDITEM")&&attrType.equals("StringValue")) {
//        		attrPath="ENDITEMIN";
//			}
        	//attrPath = getPropertiesValue(attrPath);
            attrPath = MQExpImpUtil.attrConvertValue("extPath", attrPath, "IMP");

        	if("SECRET".equals(attrPath)){
        		if("".equals(attrValueImage)||"非密".equals(attrValueImage)||"无".equals(attrValueImage)||NO_VALUE.equals(attrValueImage)){
        			attrValueImage = "公开";
        		}else if("内部★5年".equals(attrValueImage)){
        			attrValueImage = "内部";
        		}
        	}

            AttrDefInfo attrDefInfo = null;
            objId = objId == null ? "" : objId;
            //TODO 中心域属性转为本地属性
//            attrPath = ProductConvertUtil.getLocalAttr(attrPath);
            attrDefInfo = getAttrDefInfo(attrPath);
            AttributeDefDefaultView attrDefView = attrDefInfo.attrDefView;
            if (!attrDefInfo.exists) {
                attrDefInfo = getAttrDefInfoByPathAndType(attrPath, attrType);
                attrDefView = attrDefInfo.attrDefView;
            }

            if ((attrType != null) && (!attrType.equals(attrDefInfo.typeName))) {
                return null;
            }

            AbstractValueView attrView = null;

            Class defClass = attrDefInfo.attrDefView == null ? null : attrDefInfo.attrDefView.getClass();
            if (defClass == BooleanDefView.class) {
                boolean val = Boolean.valueOf(attrValueImage).booleanValue();
                attrView = new BooleanValueDefaultView((BooleanDefView) attrDefView, val);
            } else if (defClass == IntegerDefView.class) {
                long val = Long.valueOf(attrValueImage).longValue();
                attrView = new IntegerValueDefaultView((IntegerDefView) attrDefView, val);
            } else if (defClass == FloatDefView.class) {
                int delim = attrValueImage.lastIndexOf(",");
                String thePrecision = delim > 0 ? attrValueImage.substring(delim + 1) : "6";
                String theValue = delim > 0 ? attrValueImage.substring(0, delim) : attrValueImage;
                attrView = new FloatValueDefaultView((FloatDefView) attrDefView,
                        Double.valueOf(theValue).doubleValue(), Integer.valueOf(thePrecision).intValue());
            } else if (defClass == StringDefView.class) {
                attrView = new StringValueDefaultView((StringDefView) attrDefView, attrValueImage);
            } else if (defClass == UnitDefView.class) {
                int delim = attrValueImage.lastIndexOf(",");
                String thePrecision = delim > 0 ? attrValueImage.substring(delim + 1) : "6";
                String theValue = delim > 0 ? attrValueImage.substring(0, delim) : attrValueImage;
                delim = theValue.lastIndexOf(" ");
                String doubleValue = theValue.substring(0, delim);
                String srcBaseUnit = theValue.substring(delim + 1);
                UnitDefView udf = (UnitDefView) attrDefView;
                QuantityOfMeasureDefaultView qmv = udf.getQuantityOfMeasureDefaultView();
                String baseUnit = qmv.getBaseUnit();
                if (!srcBaseUnit.equals(baseUnit)) {
                    Unit typeUnit = new Unit(doubleValue, srcBaseUnit);
                    DefaultUnitDisplayInfo displayInfo = new DefaultUnitDisplayInfo();
                    displayInfo.setDisplayUnitString(baseUnit);
                    DefaultUnitRenderer render = new DefaultUnitRenderer();
                    String rendedValue = render.renderValue(typeUnit, displayInfo);
                    delim = rendedValue.indexOf(' ');
                    doubleValue = delim >= 0 ? rendedValue.substring(0, delim) : rendedValue;
                }

                attrView = new UnitValueDefaultView((UnitDefView) attrDefView, Double.valueOf(doubleValue)
                        .doubleValue(), Integer.valueOf(thePrecision).intValue());
            } else if (defClass == TimestampDefView.class) {
                Timestamp val = Timestamp.valueOf(attrValueImage);
                attrView = new TimestampValueDefaultView((TimestampDefView) attrDefView, val);
            } else if (defClass == URLDefView.class) {
                int delim = attrValueImage.indexOf(",");
                String url = delim > 0 ? attrValueImage.substring(0, delim) : attrValueImage;
                String urlDesc = delim > 0 ? attrValueImage.substring(delim + 1) : attrValueImage;
                attrView = new URLValueDefaultView((URLDefView) attrDefView, url, urlDesc);
            } else if (defClass == RatioDefView.class) {
                int delim = attrValueImage.lastIndexOf(",");
                String theBottom = attrValueImage.substring(delim + 1);
                String theValue = attrValueImage.substring(0, delim);
                attrView = new RatioValueDefaultView((RatioDefView) attrDefView,
                        Double.valueOf(theValue).doubleValue(), Double.valueOf(theBottom).doubleValue());
            } else {
                return null;
            }

            DefaultAttributeContainer container = (DefaultAttributeContainer) holder.getAttributeContainer();
            if (container == null) {
                LogHelper.devError("IBAHolder: <" + holder
                        + "> has not been properly initialized by the method 'prepareForImport'");
            }

            container.addAttributeValue(attrView);
            IxbHndHelper.setContainerConstraintParameter(container);

            return attrView;
        } catch (WTPropertyVetoException e) {
            logger.log("addIBAAttribute: IBAholder=<" + holder + ">, attrPath=<" + attrPath + ">, attrValue=<"
                    + attrValueImage + ">");
            processException(e);
        }
        return null;
    }

    private AttrDefInfo getAttrDefInfo(String path) throws WTException {
        WTContainerRef containerRef = this.impHdl.getWTContainerRef();
        if (containerRef==null) {
        	containerRef = getWTContainerRef(this.root);
		}
        AttrDefInfo attrDefInfo = new AttrDefInfo();
        try {
            AttributeDefDefaultView attrView = ExpImpForIBADefinition.getAttributeDefDefaultView(path, containerRef);

            if (attrView == null) {
                attrDefInfo.exists = false;
                attrDefInfo.attrDef = null;
                attrDefInfo.attrDefView = null;
                return attrDefInfo;
            }
            AbstractAttributeDefinition attrDef = getIBADefFromDefaultView(attrView);

            attrDefInfo.exists = true;
            attrDefInfo.attrDef = attrDef;
            attrDefInfo.attrDefView = attrView;
            String typeName = attrDef.getValueClassname();
            int i = typeName.lastIndexOf('.');
            if (i > 0) {
                typeName = typeName.substring(i + 1);
            }
            attrDefInfo.typeName = typeName;
        } catch (Exception e) {
            processException(e);
        }
        return attrDefInfo;
    }

    private AbstractAttributeDefinition getIBADefFromDefaultView(AttributeDefDefaultView attrDefView)
            throws WTException {
        AbstractAttributeDefinition attributeDefinition = null;
        if (attrDefView != null) {
            attributeDefinition = (AbstractAttributeDefinition) IBADefinitionCache.getIBADefinitionCache()
                    .getAttributeDefinition(attrDefView.getObjectID());
        }
        return attributeDefinition;
    }

    private AttrDefInfo getAttrDefInfoByPathAndType(String name, String type) throws WTException {
        AttrDefInfo attrDefInfo = new AttrDefInfo();
        Class defClass = null;
        AttributeDefDefaultView av = null;

        if (type.equals("StringValue")) {
            defClass = StringDefinition.class;
            av = new StringDefView();
        } else if (type.equals("BooleanValue")) {
            defClass = BooleanDefinition.class;
            av = new BooleanDefView();
        } else if (type.equals("IntegerValue")) {
            defClass = IntegerDefinition.class;
            av = new IntegerDefView();
        } else if (type.equals("FloatValue")) {
            defClass = FloatDefinition.class;
            av = new FloatDefView();
        } else if (type.equals("RatioValue")) {
            defClass = RatioDefinition.class;
            av = new RatioDefView();
        } else if (type.equals("URLValue")) {
            defClass = URLDefinition.class;
            av = new URLDefView();
        } else if (type.equals("TimestampValue")) {
            defClass = TimestampDefinition.class;
            av = new TimestampDefView();
        } else if (type.equals("UnitValue")) {
            defClass = UnitDefinition.class;
            av = new UnitDefView();
        } else if (type.equals("AttributeOrganizer")) {
            defClass = AttributeOrganizer.class;
        } else if (type.equals("ReferenceValue")) {
            defClass = ReferenceDefinition.class;
            av = new ReferenceDefView();
        } else {
            logger.log("Unknown attribute definition class: " + type);
        }
        WTContainerRef containerRef = this.impHdl.getWTContainerRef();
        if (containerRef==null) {
        	containerRef = getWTContainerRef(this.root);
		}
        AbstractAttributeDefinition attrDef = queryIBADefinitionFromFullPath(defClass, name, containerRef);

        if (attrDef != null) {
            try {
                av.setObjectID(PersistenceHelper.getObjectIdentifier(attrDef));
                av.setDisplayName(attrDef.getDisplayName());
                av.setHierarchyDisplayName(attrDef.getHierarchyDisplayName());
                av.setHierarchyID(attrDef.getHierarchyID());

                if ((attrDef instanceof UnitDefinition)) {
                    UnitDefinition unitDef = (UnitDefinition) attrDef;
                    UnitDefView udv = (UnitDefView) av;
                    QuantityOfMeasure qom = unitDef.getQuantityOfMeasure();
                    QuantityOfMeasureDefaultView qomDefView = new QuantityOfMeasureDefaultView(qom.getName(),
                            qom.getBaseUnitSymbol());
                    udv.setQuantityOfMeasureDefaultView(qomDefView);
                }

                attrDefInfo.exists = true;
                attrDefInfo.attrDef = attrDef;
                attrDefInfo.attrDefView = av;

                String typeName = attrDef.getValueClassname();
                int i = typeName.lastIndexOf('.');
                if (i > 0) {
                    typeName = typeName.substring(i + 1);
                }
                attrDefInfo.typeName = typeName;
            } catch (Exception e) {
                logger.log("Exception getAttrDefInfoByPathAndType: name=<" + name + ">, type=<" + ">");
                processException(e);
            }
        }
        return attrDefInfo;
    }

    private AbstractAttributeDefinition queryIBADefinitionFromFullPath(Class class1, String s,
            WTContainerRef wtcontainerref)
            throws WTException {
        try {
            String s1 = getPathWithoutOrg(s, wtcontainerref);
            if (s1 == null)
                s1 = getAttributeNameFromFullPath(s);
            AttributeDefDefaultView attributedefdefaultview = getAttributeDefDefaultView(s1, wtcontainerref);
            if (attributedefdefaultview == null) {
                return null;
            }
            ObjectIdentifier objectidentifier = attributedefdefaultview.getObjectID();
            ObjectReference objectreference = ObjectReference.newObjectReference(objectidentifier);
            return (AbstractAttributeDefinition) objectreference.getObject();
        } catch (Exception exception) {
            logger.log("Can not find AbstractAttributeDefinition for fullPath=<" + s + ">");
            processException(exception);
        }
        return null;
    }

    private AttributeOrgNodeView[] getEffectiveOrganizers(
            WTContainerRef wtcontainerref) throws WTException {
        try {
            String s = IBADomainHelper.getDomain(wtcontainerref);
            AttributeOrgNodeView[] aattributeorgnodeview = IBADefinitionHelper.service
                    .getAttributeOrganizerRoots(s);
            return aattributeorgnodeview;
        } catch (RemoteException remoteexception) {
            throw new WTException(remoteexception);
        }
    }

    private AttributeDefDefaultView getAttributeDefDefaultView(String s, WTContainerRef wtcontainerref)
            throws WTException {
        AttributeDefDefaultView attributedefdefaultview = null;
        try {
            attributedefdefaultview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(s);
            if ((attributedefdefaultview != null)
                    && (!IBADomainHelper.isAssociatedWith(s, IBADomainHelper.getDomain(wtcontainerref), true, true)))
                attributedefdefaultview = null;
            if ((attributedefdefaultview == null) && (s != null) && (wtcontainerref != null)) {
                s = IBADomainHelper.getIBANameForCreate(s, IBADomainHelper.getDomain(wtcontainerref));
                wtcontainerref = getParentContainerRef(wtcontainerref);
                return getAttributeDefDefaultView(s, wtcontainerref);
            }
        } catch (RemoteException remoteexception) {
            processException(remoteexception);
        }
        return attributedefdefaultview;
    }

    private WTContainerRef getParentContainerRef(WTContainerRef wtcontainerref) throws WTException {
        WTContainerRef wtcontainerref1 = null;
        if (wtcontainerref != null) {
            wtcontainerref1 = wtcontainerref.getParentRef();
            if (wtcontainerref.equals(wtcontainerref1))
                wtcontainerref1 = null;
        }
        return wtcontainerref1;
    }

    private String getAttributeNameFromFullPath(String s) {
        String s1 = s;
        if (s.indexOf("/") > -1) {
            for (StringTokenizer stringtokenizer = new StringTokenizer(s, "/"); stringtokenizer.hasMoreTokens();)
                s1 = stringtokenizer.nextToken();
        }
        return s1;
    }

    private String getPathWithoutOrg(String s, WTContainerRef wtcontainerref) throws WTException {
        if ((s == null) || (s.trim().length() < 1))
            return null;
        String s1 = null;
        try {
            AttributeOrgNodeView attributeorgnodeview = null;
            StringTokenizer stringtokenizer = new StringTokenizer(s, "/");
            if (stringtokenizer.countTokens() > 0) {
                String s2 = (String) stringtokenizer.nextElement();
                attributeorgnodeview = (AttributeOrgNodeView) findOrganizerOrIBADefinition(s2, wtcontainerref, true);
                if (attributeorgnodeview != null) {
                    s1 = s2;
                }
            }
            while ((stringtokenizer.hasMoreElements()) && (attributeorgnodeview != null)) {
                String s3 = (String) stringtokenizer.nextElement();
                attributeorgnodeview = findChildOrganizer(s3, attributeorgnodeview, wtcontainerref);
                if (attributeorgnodeview == null)
                    break;
                s1 = s1 + "/" + s3;
            }
            if (s1 == null)
                return s;
        } catch (Exception exception) {
            processException(exception);

            if (s1.equals(s))
                return null;
        }
        return s.substring(s.indexOf(s1) + s1.length() + 1);
    }

    private AbstractAttributeDefinizerNodeView findOrganizerOrIBADefinition(String s, WTContainerRef wtcontainerref,
            boolean flag) throws WTException {
        AttributeOrgNodeView[] aattributeorgnodeview = getEffectiveOrganizers(wtcontainerref);
        AbstractAttributeDefinizerNodeView abstractattributedefinizernodeview = null;
        if (aattributeorgnodeview != null)
            abstractattributedefinizernodeview = findOrganizerOrIBADefinition(s, wtcontainerref, aattributeorgnodeview,
                    flag, 1);
        return abstractattributedefinizernodeview;
    }

    private AbstractAttributeDefinizerNodeView findOrganizerOrIBADefinition(String s, WTContainerRef wtcontainerref,
            AbstractAttributeDefinizerNodeView[] aabstractattributedefinizernodeview, boolean flag, int i)
            throws WTException {
        AbstractAttributeDefinizerNodeView abstractattributedefinizernodeview = null;
        i++;
        if (i >= 20)
            return null;
        if (aabstractattributedefinizernodeview != null) {
            for (int j = 0; j < aabstractattributedefinizernodeview.length; j++) {
                AbstractAttributeDefinizerNodeView abstractattributedefinizernodeview1 = aabstractattributedefinizernodeview[j];
                String s1 = abstractattributedefinizernodeview1.getName();
                if ((!s.equals(s1))
                        || ((flag) && (!(abstractattributedefinizernodeview1 instanceof AttributeOrgNodeView)))
                        || ((!flag) && ((abstractattributedefinizernodeview1 instanceof AttributeOrgNodeView))))
                    continue;
                abstractattributedefinizernodeview = abstractattributedefinizernodeview1;
                return abstractattributedefinizernodeview;
            }

            if (wtcontainerref != null) {
                s = IBADomainHelper.getNameFromFullyQualifiedAttributeName(s);
                s = IBADomainHelper.getIBANameForCreate(s, IBADomainHelper.getDomain(wtcontainerref));
                wtcontainerref = getParentContainerRef(wtcontainerref);
                return findOrganizerOrIBADefinition(s, wtcontainerref, aabstractattributedefinizernodeview, flag, i);
            }
        }
        return abstractattributedefinizernodeview;
    }

    private AttributeOrgNodeView findChildOrganizer(String s,
            AttributeOrgNodeView attributeorgnodeview,
            WTContainerRef wtcontainerref) throws WTException {
        try {
            AbstractAttributeDefinizerNodeView[] aabstractattributedefinizernodeview = IBADefinitionHelper.service
                    .getAttributeChildren(
                            IBADomainHelper.getDomain(wtcontainerref),
                            attributeorgnodeview);
            attributeorgnodeview = findOrganizer(s, wtcontainerref,
                    aabstractattributedefinizernodeview, 1);
            return attributeorgnodeview;
        } catch (RemoteException remoteexception) {
            throw new WTException(remoteexception);
        }
    }

    private AttributeOrgNodeView findOrganizer(String s, WTContainerRef wtcontainerref,
            AbstractAttributeDefinizerNodeView[] aabstractattributedefinizernodeview, int i) throws WTException {
        AttributeOrgNodeView attributeorgnodeview = null;
        i++;
        if (i >= 20)
            return null;
        if (aabstractattributedefinizernodeview != null) {
            for (int j = 0; j < aabstractattributedefinizernodeview.length; j++) {
                AbstractAttributeDefinizerNodeView abstractattributedefinizernodeview = aabstractattributedefinizernodeview[j];
                String s1 = abstractattributedefinizernodeview.getName();
                if (!s.equals(s1))
                    continue;
                if ((abstractattributedefinizernodeview instanceof AttributeOrgNodeView))
                    attributeorgnodeview = (AttributeOrgNodeView) abstractattributedefinizernodeview;
                return attributeorgnodeview;
            }

            if (wtcontainerref != null) {
                s = IBADomainHelper.getNameFromFullyQualifiedAttributeName(s);
                s = IBADomainHelper.getIBANameForCreate(s, IBADomainHelper.getDomain(wtcontainerref));
                wtcontainerref = getParentContainerRef(wtcontainerref);
                return findOrganizer(s, wtcontainerref, aabstractattributedefinizernodeview, i);
            }
        }
        return attributeorgnodeview;
    }

    private IBAHolder prepareForImportOfIBA(IBAHolder holder) throws WTException {
        try {
            DefaultAttributeContainer container = (DefaultAttributeContainer) holder.getAttributeContainer();
            if (container == null) {
                container = new DefaultAttributeContainer();
                holder.setAttributeContainer(container);
            }
            IxbHndHelper.setContainerConstraintParameter(container);
        } catch (Exception e) {
            logger.log("Exception prepareForImport: IBAholder=<" + holder + ">");
            processException(e);
        }
        return holder;
    }

    protected abstract String getRootTag();

    public abstract void exportObject(Object paramObject)
            throws WTException;

    public abstract Object importObject()
            throws WTException;

    public abstract WTArrayList importObjects()
            throws WTException;

    private class AttrDefInfo {
        AbstractAttributeDefinition attrDef;
        AttributeDefDefaultView attrDefView;
        boolean exists;
        String typeName;

        private AttrDefInfo() {
        }
    }

    private class AttrValueInfo {
        private String path = null;
        private String type = null;
        private String value = null;
        private String objId = null;
        private String precision = "10";

        public AttrValueInfo(String _path, String _objId, String _type, String _value, String _precision) {
            this.path = _path;
            this.type = _type;
            this.value = _value;
            this.objId = _objId;
            if (_precision != null)
                this.precision = _precision;
        }

        public String getPath() {
            return this.path;
        }

        public String getType() {
            return this.type;
        }

        public String getValue() {
            return this.value;
        }

        public String getObjID() {
            return this.objId;
        }

        public String getPrecision() {
            return this.precision;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof AttrValueInfo)) {
                return false;
            }
            AttrValueInfo valueInfo = (AttrValueInfo) obj;
            boolean res = (this.type.equals(valueInfo.getType()))
                    &&
                    (this.path.equals(valueInfo.getPath()))
                    && (
                    ((this.value == null) && (valueInfo.getValue() == null)) || ((this.value != null) && (this.value
                            .equals(valueInfo.getValue()))));
            return res;
        }

        public String toString() {
            return "AttrValueInfo: type " + this.type + "; path " + this.path + "; value " + this.value
                    + "; precision " + this.precision;
        }
    }
    protected WTContainerRef getWTContainerRef(IxbElement ixbelement) throws WTException {
        // TODO Auto-generated method stub
        String productName = getElementValue(ixbelement, "productId");
        //TODO 中心域标准型号转为本地型号
        String tempName = ProductConvertUtil.getLocalProductName(productName);
        if(tempName!=null&&!"".equals(tempName)){
        	productName = tempName;
        }
        String isLibrary = getElementValue(ixbelement, "isLibrary");
        if (productName != null) {
        	if ("0".equals(isLibrary)) {
                WTContainerRef ref = getWTContainerRef(PDMLinkProduct.class, productName);
                if(ref==null){
                	try {
						ref = getWTContainerRef(PDMLinkProduct.class, getPropertiesValue(productName));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
                }
                if(ref==null){
                    ref = getWTContainerRef(PDMLinkProduct.class, MQExpImpConstants.WAILAI_PRODUCT);
                }


                return ref;

            } else  {
                WTContainerRef ref = getWTContainerRef(WTLibrary.class, productName);
                if(ref==null){
                    ref = getWTContainerRef(WTLibrary.class, MQExpImpConstants.WAILAI_LIB);
                }
                return ref;
            }
        }
        return null;
    }


    protected  WTContainerRef getWTContainerRef(Class kass, String name) throws WTException {
        try {
            QuerySpec qs = new QuerySpec(kass);
            SearchCondition sc = new SearchCondition(kass,
                    WTContainer.NAME, SearchCondition.EQUAL, name, false);
            qs.appendSearchCondition(sc);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            WTContainer container;
            if (qr.hasMoreElements()) {
                container = (WTContainer) qr.nextElement();
                return WTContainerRef.newWTContainerRef(container);
            }else{
                container = getContainerByPIndex(name);
                if(container!=null){
                    return WTContainerRef.newWTContainerRef(container);
                }
            }
        } catch (WTException ex) {
            ex.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (WTPropertyVetoException e) {
            e.printStackTrace();
        }
        WTOrganization org = getOrganizationByName("casc");
        return WTContainerHelper.service.getOrgContainerRef(org);
    }
    public static WTContainer getContainerByPIndex(String pindex) throws WTException, RemoteException, WTPropertyVetoException {
        WTContainer wtContainer = null;
        QuerySpec querySpec = new QuerySpec(WTContainer.class);
        querySpec.setAdvancedQueryEnabled(true);
        querySpec.appendOpenParen();
        ClassAttribute caId = new ClassAttribute(WTContainer.class, "thePersistInfo.theObjectIdentifier.id");
        SubSelectExpression subSelectExpression = getStringIBAQuery2("PINDEX", pindex);
        querySpec.appendWhere(new SearchCondition(caId, "IN", subSelectExpression), new int[]{0});
        querySpec.appendCloseParen();
        QueryResult queryResult = PersistenceHelper.manager.find(querySpec);
        if (queryResult.hasMoreElements()) {
            wtContainer = (WTContainer) queryResult.nextElement();
        }
        return wtContainer;
    }
    public static SubSelectExpression getStringIBAQuery2(String ibaName, String ibaValue) throws WTException,
            WTPropertyVetoException, RemoteException {
        // 获取IBA属性定义
        AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
        if (addv == null) {
            throw new IBADefinitionException("No IBA Definition: " + ibaName);
        }
        long ibaDefId = addv.getObjectID().getId();
        QuerySpec qs = new QuerySpec();
        int idx = qs.appendClassList(StringValue.class, false);
        qs.appendSelect(new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), new int[]{idx}, false);
        qs.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", SearchCondition.EQUAL, ibaDefId), new int[]{idx});
        qs.appendAnd();
        qs.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.EQUAL, ibaValue, true), new int[]{idx});
        return new SubSelectExpression(qs);
    }

    public static WTOrganization getOrganizationByName(String name) {
        WTOrganization org = null;
        try {
            QuerySpec qs = new QuerySpec(WTOrganization.class);
            SearchCondition sc = new SearchCondition(WTOrganization.class,
                    WTOrganization.NAME, SearchCondition.EQUAL, name, false);
            qs.appendSearchCondition(sc);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements())
                org = (WTOrganization) qr.nextElement();
        } catch (WTException ex) {
            ex.printStackTrace();
        }
        return org;
    }

    protected String getPropertiesValue(String key) throws UnsupportedEncodingException {

        // TODO Auto-generated method stub
        String value = null;
        key = new String(key.getBytes("GBK"), "ISO8859-1");
		 if("zyk".equals(sendFrom)){
             value = prop2.getProperty(key,key);
		 }else{
			 if(key!=null && key.contains("WTDocument")){
				 value = prop.getProperty(key);
			 }else{
				 value = prop.getProperty(key,key);
			 }

		 }

        if (value != null) {
            value = new String(value.getBytes("ISO8859-1"), "GBK");
        }
        return value;
    }

    protected String getLifeCyclePropertiesValue(String key) throws UnsupportedEncodingException {

        // TODO Auto-generated method stub
        String value = null;
        key = new String(key.getBytes("GBK"), "ISO8859-1");
		 if("zyk".equals(sendFrom)){
             value = prop2.getProperty(key,"PROCESSCOUNTERSIGN");
		 }else{
			 value = prop.getProperty(key,"PROCESSCOUNTERSIGN");

		 }

        if (value != null) {
            value = new String(value.getBytes("ISO8859-1"), "GBK");
        }
        return value;
    }
}