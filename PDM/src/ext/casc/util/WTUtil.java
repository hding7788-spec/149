package ext.casc.util;

import com.ptc.core.command.common.bean.entity.NewEntityCommand;
import com.ptc.core.command.common.bean.entity.PrepareEntityCommand;
import com.ptc.core.foundation.type.server.impl.SoftAttributesHelper;
import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.core.meta.common.*;
import com.ptc.core.meta.common.impl.WCTypeIdentifier;
import com.ptc.core.meta.container.common.*;
import com.ptc.core.meta.container.common.impl.DefaultConstraintValidator;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.core.meta.type.common.TypeInstance;
import com.ptc.core.meta.type.mgmt.server.TypeDefinition;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinition;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinitionObjectLocator;
import com.ptc.core.meta.type.runtime.server.PopulatedAttributeContainerFactory;
import com.ptc.core.meta.type.server.TypeInstanceUtility;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.admin.AdminDomainRef;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.fc.*;
import wt.folder.FolderHelper;
import wt.iba.definition.litedefinition.AbstractAttributeDefinizerNodeView;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.litedefinition.AttributeDefNodeView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.IBAHolder;
import wt.iba.value.litevalue.AbstractValueView;
import wt.iba.value.litevalue.ReferenceValueDefaultView;
import wt.iba.value.service.IBAValueHelper;
import wt.inf.container.*;
import wt.inf.library.WTLibrary;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.inf.team.ContainerTeamReference;
import wt.inf.template.ContainerTemplateHelper;
import wt.inf.template.DefaultWTContainerTemplate;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.*;
import wt.org.electronicIdentity.SignatureLink;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.pom.UniquenessException;
import wt.project.Role;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.applicationcontext.implementation.DefaultServiceProvider;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.TeamHelper;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.util.*;
import wt.util.range.Range;
import wt.vc.*;
import wt.vc.config.LatestConfigSpec;
import wt.vc.struct.IteratedDescribeLink;
import wt.vc.struct.IteratedReferenceLink;
import wt.vc.struct.IteratedUsageLink;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * WTUtil: windchill utility methods
 */
public class WTUtil implements RemoteAccess {
    public static final String IBACONST_LEGAL_VALUE_SET = "LEGAL_VALUE_SET";
    public static final String IBACONST_STRING_LENGTH_SET = "STRING_LENGTH_SET";
    //private static final String IBACONST_REQUIRED = "Required";
    public static String WTTEMP;
    public static String WTHOME;
    public static final String IBA_IDENTIFIER = "IBA_IDENTIFIER";
    public static final String IBA_NAME = "IBA_NAME";
    public static final String IBA_VALUE = "IBA_VALUE";
    public static final String IBA_LABEL = "IBA_LABEL";
    public static final String IBA_DATATYPE = "IBA_DATATYPE";
    public static final String IBA_OPTIONS_VECTOR = "IBA_OPTIONS_VECTOR";
    public static final String IBA_REQUIRED = "IBA_REQUIRED";
    public static final String IBA_EDITABLE = "IBA_EDITABLE";
    public static final String IBA_STRING_LENGTH_MIN = "IBA_STRING_LENGTH_MIN";
    public static final String IBA_STRING_LENGTH_MAX = "IBA_STRING_LENGTH_MAX";
    public static final String IBA_FROM_DEFINITION = "IBA_FROM_DEFINITION";
    public static final String IBA_UNDEFINED = "IBA_UNDEFINED";

    static {
        try {
            WTTEMP = WTProperties.getLocalProperties().getProperty("wt.temp");
            WTHOME = WTProperties.getLocalProperties().getProperty("wt.home");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取当前代码的运行环境是JSP的VM还是MethodServer的上下文,包括当前用户名
     * 
     * @return      一个描述当前代码环境的字符串
     */
    public static String ctx() {
        String ctx = RemoteMethodServer.ServerFlag ? "MethodServer" : "JSP";
        try {
            if (!RemoteMethodServer.ServerFlag
                    || MethodContext.getContext(Thread.currentThread()).getAuthentication() != null) {
                String me = SessionHelper.getPrincipal().getName();
                ctx += "(" + me + ")";
            }
        }
        catch (WTException e) {
        }

        return ctx;
    }

    /**
     * 检查当前用户是否是系统管理员或包括在系统管理员组中(站点管理员)
     * 
     * @return  true如果是
     */
    public static boolean isAdmin() {
        try {
            WTPrincipal me = SessionHelper.manager.getPrincipal();
            WTPrincipal admin = SessionHelper.manager.getAdministrator();

            //Debug.P(me);
            //Debug.P(admin);

            return me.equals(admin);
            /*
            if (me.equals(admin))
                return true;

            WTGroup group = OrganizationServicesHelper.manager.getGroup(AdministrativeDomainHelper.ADMIN_GROUP_NAME);
            boolean result = OrganizationServicesHelper.manager.isMember(group, me);
            Debug.P("in admin group: " + result);

            return result;
            */
        }
        catch (WTException e) {
            // Debug.E(e);
            return false;
        }
    }

    /**
     * 获取当前用户所做组织的指定名称的存储库的reference
     * 
     * @param libName   存储库名称
     * @return          存储库reference
     */
    public static WTContainerRef getLibraryRef(String libName) {
        try {
            WTPrincipal me = SessionHelper.manager.getPrincipal();
            WTOrganization org = OrganizationServicesHelper.manager.getOrganization(me);
            return getLibraryRef(org.getName(), libName);
        }
        catch (WTException e) {
            // Debug.E(e);
            return null;
        }
    }

    /**
     * 获取指定组织名和存储库名的存储库Reference
     * 
     * @param orgName   组织名称
     * @param libName   存储库名称
     * @return          存储库Reference
     */
    public static WTContainerRef getLibraryRef(String orgName, String libName) {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "getLibraryRef";
            String klass = WTUtil.class.getName();
            Class[] types = { String.class, String.class };
            Object[] values = { orgName, libName };
            WTContainerRef result = null;
            try {
                result = (WTContainerRef) RemoteMethodServer.getDefault().invoke(method, klass, null,
                    types, values);
            }
            catch (Exception e) {
                if (e.getCause() != null)
                    e = (Exception) e.getCause();
                e.printStackTrace();
            }
            return result;
        }
        
        boolean accessEnforced = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            String path = "/wt.inf.container.OrgContainer=" + orgName
                    + "/wt.inf.library.WTLibrary=" + libName;
            WTContainerRef ref = WTContainerHelper.service.getByPath(path);
            WTContainer cont = ref.getReferencedContainer();
            WTPrincipal me = SessionHelper.getPrincipal();
            if (!AccessControlHelper.manager.hasAccess(me, cont, AccessPermission.READ)) {
                Role role = Role.toRole("MEMBERS");
                ContainerTeamReference teamRef = ((WTLibrary) cont).getContainerTeamReference();
                TeamHelper.service.addRolePrincipalMap(role, me, (ContainerTeam)teamRef.getObject());
            }
            return ref;
        }
        catch (WTException e) {
            // Debug.E(e);
            return null;
        }
        finally {
            SessionServerHelper.manager.setAccessEnforced(accessEnforced);
        }
    }

    /**
     * 新建一个Library容器, 使用常规存储库模板, 如没有, 则创建默认存储库模板
     * 
     * @param libName           容器名称
     * @param libDescription    容器描述
     * @return                  容器的reference
     * @throws Exception
     */
    public static WTContainerRef createLibrary(String libName, String libDescription)
            throws Exception {
        return createLibrary(libName, libDescription, "常规存储库");
    }
    
    /**
     * 新建一个Library容器, 需指定容器模板名称
     * 
     * @param libName           容器名称
     * @param libDescription    容器描述
     * @param templatename      容器模板名称
     * @return                  容器的reference
     * @throws Exception
     */
    public static WTContainerRef createLibrary(String libName, String libDescription, String templateName) throws Exception {
        if (!RemoteMethodServer.ServerFlag) {
            try {
                String method = "createLibrary";
                String klass = WTUtil.class.getName();
                Class[] types = { String.class, String.class, String.class };
                Object[] values = { libName, libDescription, templateName };
                return (WTContainerRef) RemoteMethodServer.getDefault().invoke(method, klass, null,
                        types, values);
            }
            catch (Exception e) {
                if (e.getCause() != null)
                    throw (Exception) e.getCause();
                throw e;
            }
        }

        WTUser me = (WTUser) SessionHelper.manager.getPrincipal();
        WTOrganization org = OrganizationServicesHelper.manager.getOrganization(me);
        WTContainerRef orgContainerRef = WTContainerHelper.service.getOrgContainerRef(org);

        WTLibrary lib = WTLibrary.newWTLibrary();
        WTContainerRef libRef = null;
        
        boolean accessEnforced = SessionServerHelper.manager.setAccessEnforced(false);
        Vector orgAdmins = findOrgAdmins(org);
        if (orgAdmins == null || orgAdmins.size() == 0)
            SessionHelper.manager.setAdministrator();
        else {
            WTUser admin = (WTUser) orgAdmins.get(0);
            SessionHelper.manager.setAuthenticatedPrincipal(admin.getAuthenticationName());
        }
        try {
            lib.setName(libName);
            if (libDescription != null)
                lib.setDescription(libDescription);

            WTContainerTemplateRef containerTemplateRef = ContainerTemplateHelper.service.getContainerTemplateRef(
                    orgContainerRef, templateName, WTLibrary.class);
            if (containerTemplateRef == null) {
                WTContainerTemplate containerTemplate = DefaultWTContainerTemplate.newDefaultWTContainerTemplate(
                        "默认存储库模板", WTLibrary.class.getName());
                containerTemplate = ContainerTemplateHelper.service.createContainerTemplate(
                        orgContainerRef, containerTemplate);
                containerTemplateRef = WTContainerTemplateRef.newWTContainerTemplateRef(containerTemplate);
            }
            
            lib.setContainerTemplateReference(containerTemplateRef);
            lib.setContainerReference(orgContainerRef);
            lib = (WTLibrary) WTContainerHelper.service.create(lib);
            lib = (WTLibrary) WTContainerHelper.service.makePublic(lib);
            libRef = WTContainerRef.newWTContainerRef(lib);
            
            try {
                FolderHelper.service.createSubFolder("/System/Reports", libRef);
            }
            catch (UniquenessException uniquenessexception1) {
            }

            try {
                FolderHelper.service.createSubFolder("/System/Reports/ChangeMonitor",
                        libRef);
            }
            catch (UniquenessException uniquenessexception2) {
            }

            try {
                FolderHelper.service.createSubFolder("/System/Reports/ChangeMonitor/Custom",
                        libRef);
            }
            catch (UniquenessException uniquenessexception3) {
            }
        }
        finally {
            SessionHelper.manager.setAuthenticatedPrincipal(me.getAuthenticationName());
            SessionServerHelper.manager.setAccessEnforced(accessEnforced);
        }

        return libRef;
    }

    /**
     * 获取一个Organization的所有管理员
     * 
     * @param org   组织对象
     * @return      Vector of organization administrators
     * @throws Exception
     */
    public static Vector findOrgAdmins(WTOrganization org) throws Exception {
        if (!RemoteMethodServer.ServerFlag) {
            try {
                String method = "findOrgAdmins";
                String klass = WTUtil.class.getName();
                Class[] types = { WTOrganization.class };
                Object[] vals = { org };
                return (Vector) RemoteMethodServer.getDefault().invoke(method, klass, null, types, vals);
            }
            catch (Exception e) {
                if (e.getCause() != null)
                    throw (Exception) e.getCause();
                throw e;
            }
        }

        Vector admins = new Vector();
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            if (org == null)
                org = SessionHelper.getPrincipal().getOrganization();
            OrgContainer orgCont = WTContainerHelper.service.getOrgContainer(org);
            if (orgCont != null) {
                WTGroup grp = orgCont.getAdministrators();
                for (Enumeration en = grp.members(); en.hasMoreElements(); ) {
                    WTPrincipal principal = (WTPrincipal) en.nextElement();
                    if (principal instanceof WTUser)
                        admins.add(principal);
                }
            }
        }
        finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        
        return admins;
    }

    /**
     * 读取指定softtype类型的IBA属性
     * 
     * 返回的ibaList和ibaMap的每一些为一个HashMap, 其key意义如下:
     *
     * String IBA_NAME = "IBA_NAME";                            属性名称 <br>
     * String IBA_VALUE = "IBA_VALUE";                          属性值   <br>
     * String IBA_LABEL = "IBA_LABEL";                          属性标签 <br>
     * String IBA_DATATYPE = "IBA_DATATYPE";                    数据类型 <br>
     * String IBA_OPTIONS_VECTOR = "IBA_OPTIONS_VECTOR";        选项Vector <br>
     * String IBA_REQUIRED = "IBA_REQUIRED";                    是否必需 <br>
     * String IBA_EDITABLE = "IBA_EDITABLE";                    是否可编辑 <br>
     * String IBA_STRING_LENGTH_MIN = "IBA_STRING_LENGTH_MIN";  字串最短(null不限) <br>
     * String IBA_STRING_LENGTH_MAX = "IBA_STRING_LENGTH_MAX";  字串最长(null不限) <br>
     * String IBA_FROM_DEFINITION = "IBA_FROM_DEFINITION";      类型定义(对象不含) <br>
     * String IBA_UNDEFINED = "IBA_UNDEFINED"; 未定义(属性已从定义中删除, 对象中包含) <br>
     * 
     * @param ibaHolder     SoftType类型的对象实例
     * @param getDefinition 是否获取IBA属性的定义信息
     * @param ibaList       IBA属性列表
     * @param ibaMap        IBA属性Map, 以IBA属性名称为key
     * @throws WTException
     */
    public static void getIBAValues(IBAHolder ibaHolder, ArrayList ibaList, 
            HashMap ibaMap) throws WTException {
        getIBAValuesInternal(ibaHolder, ibaList, ibaMap, true);
    }

    /**
     * 读取指定softtype类型的IBA属性.
     * 
     * 返回的ibaList和ibaMap的每一些为一个HashMap, 其key意义如下:
     *
     * String IBA_NAME = "IBA_NAME";                            属性名称 <br>
     * String IBA_VALUE = "IBA_VALUE";                          属性值   <br>
     * String IBA_LABEL = "IBA_LABEL";                          属性标签 <br>
     * String IBA_DATATYPE = "IBA_DATATYPE";                    数据类型 <br>
     * String IBA_OPTIONS_VECTOR = "IBA_OPTIONS_VECTOR";        选项Vector <br>
     * String IBA_REQUIRED = "IBA_REQUIRED";                    是否必需 <br>
     * String IBA_EDITABLE = "IBA_EDITABLE";                    是否可编辑 <br>
     * String IBA_STRING_LENGTH_MIN = "IBA_STRING_LENGTH_MIN";  字串最短(null不限) <br>
     * String IBA_STRING_LENGTH_MAX = "IBA_STRING_LENGTH_MAX";  字串最长(null不限) <br>
     * String IBA_FROM_DEFINITION = "IBA_FROM_DEFINITION";      类型定义(对象不含) <br>
     * String IBA_UNDEFINED = "IBA_UNDEFINED"; 未定义(属性已从定义中删除, 对象中包含) <br>
     * 
     * @param typeIdentifier  softtype类型字符串, WCTYPE|协议头可省略 
     *                      如: WCTYPE|wt.doc.WTDocument|com.haitian.DocTypeA
     * @param ibaList       IBA属性列表
     * @param ibaMap        IBA属性Map, 以IBA属性名称为key
     * @throws WTException
     */
    public static void getIBAValues(String typeIdentifier, ArrayList ibaList, 
            HashMap ibaMap) throws WTException {
        String protoHeader = 
                WCTypeIdentifier.PROTOCOL + WCTypeIdentifier.PROTOCOL_SEPARATOR;
        if (!typeIdentifier.startsWith(protoHeader))
            typeIdentifier = protoHeader + typeIdentifier;
        
        //Debug.P("Get type ibas: ", typeIdentifier);
        
        getIBAValuesInternal(typeIdentifier, ibaList, ibaMap, true);
    }
    
    /**
     * 获取IBA信息的内部实现
     * 
     * @param obj       IBAHolder对象或typeIdentifer字串
     * @param ibaList   *
     * @param ibaMap    *
     * @throws WTException
     */
    static TypeInstance getIBAValuesInternal(Object obj, ArrayList ibaList, HashMap ibaMap,
            boolean returnOpts) throws WTException {
        TypeInstanceIdentifier tii = null;
        Locale locale = WTContext.getContext().getLocale();
        boolean forTypedObj = false;
        
        // 取TypeInstanceIdentifier
        if (obj instanceof IBAHolder) { // obj是一个IBAHolder(Typed)对象
            tii = TypeIdentifierUtility.getTypeInstanceIdentifier(obj);
            forTypedObj = true;
        }
        else { // obj是一个TypeIdentifier字符串, e.g. WTTYPE|wt.doc.WTDocument|...
            IdentifierFactory idFactory = (IdentifierFactory) DefaultServiceProvider
                .getService(com.ptc.core.meta.common.IdentifierFactory.class, 
                "default");
            TypeIdentifier ti = (TypeIdentifier) idFactory.get((String) obj);
            tii = ti.newTypeInstanceIdentifier();
        }

        // 获取TypeInstance
        TypeInstance typeInstance = null;
        try {
            if (false) {
                PopulatedAttributeContainerFactory pacFactory = 
                    (PopulatedAttributeContainerFactory)DefaultServiceProvider
                    .getService(PopulatedAttributeContainerFactory.class, "virtual");
                AttributeContainer ac = pacFactory.getAttributeContainer(null, 
                        (TypeIdentifier) tii.getDefinitionIdentifier());
                
                if (ac == null) {
                    if (obj instanceof String)
                        throw new WTException("未定义的SoftType类型: " + obj);
                    else
                        throw new WTException("未定义的SoftType类型: " + tii);
                }
            
                AttributeContainerSpec acSpec = new AttributeContainerSpec();
                IdentifierFactory idFact = (IdentifierFactory)DefaultServiceProvider.getService(
                        com.ptc.core.meta.common.IdentifierFactory.class, "logical");
                AttributeTypeIdentifier ati1 = (AttributeTypeIdentifier)idFact.get(
                        "ALL_SOFT_SCHEMA_ATTRIBUTES", tii.getDefinitionIdentifier());
                acSpec.putEntry(ati1, true, true);
                AttributeTypeIdentifier ati2 = (AttributeTypeIdentifier)idFact.get(
                        "ALL_SOFT_ATTRIBUTES", tii.getDefinitionIdentifier());
                acSpec.putEntry(ati2, true, true);
                AttributeTypeIdentifier ati3 = (AttributeTypeIdentifier)idFact.get(
                        "ALL_SOFT_CLASSIFICATION_ATTRIBUTES", tii.getDefinitionIdentifier());
                acSpec.putEntry(ati3, true, true);
                if(tii.isInitialized())
                    acSpec.setNextOperation(OperationIdentifier.newOperationIdentifier(
                            "STDOP|com.ptc.windchill.update"));
                else
                    acSpec.setNextOperation(OperationIdentifier.newOperationIdentifier(
                            "STDOP|com.ptc.windchill.create"));
                PrepareEntityCommand peCmd = new PrepareEntityCommand();
                peCmd.setLocale(locale);
                peCmd.setFilter(acSpec);
                peCmd.setSource(tii);
                peCmd = (PrepareEntityCommand)peCmd.execute();
                typeInstance = peCmd.getResult();
                Set set = (Set)typeInstance.getSingle(ati3);
                if(set != null) {
                    for(Iterator iterator = set.iterator(); iterator.hasNext();
                            typeInstance.purge((AttributeTypeIdentifier)iterator.next()));
                }
                typeInstance.purge(ati1);
                typeInstance.purge(ati2);
                typeInstance.purge(ati3);
                AttributeTypeIdentifier ati[] = typeInstance.getAttributeTypeIdentifiers();
                for(int j = 0; j < ati.length; j++)
                if(ati[j].getContext() instanceof AttributeTypeIdentifier)
                    typeInstance.purge(ati[j]);
            }
            else {
            	String baseType = tii.getDefinitionIdentifier().toExternalForm();
            	try {typeInstance = TypeHelper.getCustomAttributesTypeInstanceForWizard(obj, baseType, (obj instanceof String), locale);} catch (Exception e) {}
            	if (typeInstance == null) {
            		try {typeInstance = SoftAttributesHelper.getSoftSchemaTypeInstance(tii, null, locale);} catch (Exception e) {}
            	}
            }
        }
        catch(WTPropertyVetoException wtpropertyvetoexception) {
            throw new WTException(wtpropertyvetoexception,
                    "SoftAttributesHelper.getSoftSchemaTypeInstance(): " +
                    "Exception encountered when trying to create a type instance");
        }
        catch(UnsupportedOperationException unsupportedoperationexception) {
            throw new WTException(unsupportedoperationexception,
                    "SoftAttributesHelper.getSoftSchemaTypeInstance(): " +
                    "Exception encountered when trying to create a type instance");
        }
        
        // 对IBAHolder对象,填充未设定的属性
        if (forTypedObj) {
            //TypeInstanceUtility.populateMissingTypeContent(typeInstance, null);
        }
        
        // 逐个获取IBA属性
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

            HashMap ibaInfo = new HashMap();
            ibaInfo.put(IBA_IDENTIFIER, ibaIdentifier);
            ibaInfo.put(IBA_NAME, name);
            ibaInfo.put(IBA_VALUE, value);
            ibaInfo.put(IBA_LABEL, label);
            ibaInfo.put(IBA_DATATYPE, dataType);
            ibaInfo.put(IBA_REQUIRED, required);
            ibaInfo.put(IBA_EDITABLE, editable);
            ibaInfo.put(IBA_STRING_LENGTH_MIN, minStringLength);
            ibaInfo.put(IBA_STRING_LENGTH_MAX, maxStringLength);

            if (returnOpts) {
                Vector options = null;
                DataSet dsVal = ats.getLegalValueSet();
                if (dsVal != null && dsVal instanceof DiscreteSet) {
                    Object[] eles = ((DiscreteSet) dsVal).getElements();
                    options = new Vector();
                    for (int j = 0; eles != null && j < eles.length; j++) {
                        options.add(String.valueOf(eles[j]));
                    }
                }
                ibaInfo.put(IBA_OPTIONS_VECTOR, options);
            }
            
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
     * 取IBAHolder的所有IBA属性值: 
     * 
     * String result[i][0]: 第i个属性的名称<br>
     * String result[i][1]: 第i个属性的值
     * 
     * @param ibaHolder
     * @return
     */
    public static String[][] getIBAValuesLite(IBAHolder ibaHolder, HashMap ibaMap) {
        Locale locale = WTContext.getContext().getLocale();
        
        DefaultAttributeContainer dac = (DefaultAttributeContainer) ibaHolder
                .getAttributeContainer();
        if (dac == null) {
            try {
                ibaHolder = IBAValueHelper.service.refreshAttributeContainer(
                        ibaHolder, null, null, null);
                dac = (DefaultAttributeContainer) ibaHolder.getAttributeContainer();
            }
            catch (Exception e) {
                e.printStackTrace();
                return new String[0][0];
            }
        }
        
        AbstractValueView[] avv = null;
        if (dac == null || (avv = dac.getAttributeValues()) == null)
            return new String[0][0];
        
        String[][] result = new String[avv.length][2];
        for (int i = 0; i < avv.length; i++) {
            result[i][0] = avv[i].getDefinition().getName();
            if (avv[i] instanceof ReferenceValueDefaultView) {
                result[i][1] = ((ReferenceValueDefaultView)avv[i])
                        .getLiteIBAReferenceable()
                        .getIBAReferenceableDisplayString();
            }
            else 
                result[i][1] = avv[i].getLocalizedDisplayString(locale);
            
            if (ibaMap != null)
                ibaMap.put(result[i][0], result[i][1]);
        }
        
        return result;
    }
    
    /**
     * 设定IBA属性, 如果返回值为true, 则需要更新对象:
     * persistable = IBAValueHelper.service.updateIBAHolder(ibaHolder, null, 
     *          null, null)
     *          
     * @param ibaHolder     设定IBA属性目标对象
     * @param ibaValues     要设定的属性名和值集合
     * @return              对象是否被更改需要保存
     * @throws WTException
     */
    public static boolean setIBAValues(IBAHolder ibaHolder, Properties ibaValues) 
    throws WTException {
        // 去对象的原有IBA属性
        HashMap ibaMap = new HashMap();
        Locale locale = WTContext.getContext().getLocale();
        TimeZone tzone = WTContext.getContext().getTimeZone();
        TypeInstance ti = getIBAValuesInternal(ibaHolder, null, ibaMap, false);
        IdentifierFactory idFactory = (IdentifierFactory) DefaultServiceProvider
                .getService(com.ptc.core.meta.common.IdentifierFactory.class, 
                "default");

        // 整理要赋值的IBA属性
        ArrayList listIBAId = new ArrayList();
        ArrayList listIBATypeId = new ArrayList();
        ArrayList listIBAValue = new ArrayList();
        for (Enumeration en = ibaValues.keys(); en.hasMoreElements();) {
            String iName = (String) en.nextElement();
            String iVal = (String) ibaValues.get(iName);
            if (iVal == null)       // null ==> 使用默认值
                continue;
            
            HashMap ibaInfo = (HashMap) ibaMap.get(iName);
            if (ibaInfo == null) {  // 未定义的属性名称
                Persistable p = (Persistable) ibaHolder;
                String oid = PersistenceHelper.isPersistent(p) ?
                        new ReferenceFactory().getReferenceString(p) :
                        ibaHolder.getClass().getName() + ":NEW";
                System.out.println("未定义的IBA属性名: [" + iName + "], " + oid);
                continue;
            }
            Boolean required = (Boolean) ibaInfo.get(IBA_REQUIRED);
            if (required != null && required.booleanValue() && iVal.equals(""))
                throw new WTException("属性<" + iName + ">的值不能为空!");
            
            AttributeIdentifier ai = (AttributeIdentifier) 
                    idFactory.get((String) ibaInfo.get(IBA_IDENTIFIER));
            DefinitionIdentifier ati = ai.getDefinitionIdentifier();

            String dataType = (String) ibaInfo.get(IBA_DATATYPE);
            Object iv = convertStringToIBAValue(iVal, dataType, locale, tzone);
            
            listIBAId.add(ai);
            listIBAValue.add(iv);
            listIBATypeId.add(ati);
        }
        
        // 逐个赋值
        HashMap vmap = new HashMap();
        TypeInstanceIdentifier tii = (TypeInstanceIdentifier) ti.getIdentifier();
        for (int i = 0; i < listIBAId.size(); i++) {
            AttributeTypeIdentifier ati = (AttributeTypeIdentifier) 
                    listIBATypeId.get(i);
            AttributeIdentifier[] ais = ti.getAttributeIdentifiers(ati);
            if (ais.length > 0) {
                vmap.put(ais[0], ti.get(ais[0]));
                ti.put(ais[0], listIBAValue.get(i));
            }
            else {
                AttributeIdentifier ai = ati.newAttributeIdentifier(tii);
                vmap.put(ai, null);
                ti.put(ai, listIBAValue.get(i));
            }
        }

        ti.acceptDefaultContent();
        ti.purgeDefaultContent();
        
        // 检查约束
        if(tii.isInitialized())
            TypeInstanceUtility.populateConstraints(ti, 
                    OperationIdentifier.newOperationIdentifier(
                    "STDOP|com.ptc.windchill.update"));
        else
            TypeInstanceUtility.populateConstraints(ti, 
                    OperationIdentifier.newOperationIdentifier(
                    "STDOP|com.ptc.windchill.create"));
        DefaultConstraintValidator dac = DefaultConstraintValidator.getInstance();
        ConstraintContainer cc = ti.getConstraintContainer();
        if(cc != null) {
            AttributeIdentifier ais[] = ti.getAttributeIdentifiers();
            for (int i = 0; i < ais.length; i++) {
                Object ibaVal = ti.get(ais[i]);
                try {
                    dac.isValid(ti, cc, ais[i], ibaVal);
                }
                catch (ConstraintException ce) {
                    if ((!ce.getConstraintIdentifier().getEnforcementRuleClassname().equals(
                            "com.ptc.core.meta.container.common.impl.DiscreteSetConstraint")
                            || vmap == null || vmap.get(ais[i]) == null 
                            || (!(vmap.get(ais[i]) instanceof Comparable) 
                            || ((Comparable) ti.get(ais[i])).compareTo(
                                    (Comparable) vmap.get(ais[i])) != 0)
                            && !vmap.get(ais[i]).equals(
                                    ti.get(ais[i])))
                            && !ce.getConstraintIdentifier().getEnforcementRuleClassname().equals(
                                    "com.ptc.core.meta.container.common.impl.ImmutableConstraint")) {
                        WTException wtexception = interpretConstraintViolationException(
                                ce, locale);
                        if (wtexception != null)
                            throw wtexception;
                    }
                }
            }
        }
        
        // 保存到目标对象
        TypeInstanceUtility.updateIBAValues((IBAHolder)ibaHolder, ti);
        return ti.isDirty();
    }

    /**
     * 将字符串值按指定类型转换为IBA属性值对应的对象
     * 
     * @param strVal        字符串值
     * @param dataType      数据类型(java类型)
     * @param locale        *
     * @param timezone      *
     * @return              Object值对象
     * @throws WTException
     */
    static Object convertStringToIBAValue(String strVal, String dataType, 
            Locale locale, TimeZone timezone) throws WTException {
        Object obj = null;
        if (dataType.equals("java.lang.Long"))
            try {
                obj = Long.valueOf(strVal);
            }
            catch (Exception exception) {
                Object aobj1[] = { strVal };
                throw new WTException(
                        "com.ptc.core.HTMLtemplateutil.server.processors.processorsResource",
                        "58", aobj1);
            }
        else if (dataType.equals("com.ptc.core.meta.common.FloatingPoint"))
            try {
                obj = DataTypesUtility.toFloatingPoint(strVal, locale);
            }
            catch (Exception exception1) {
                Object aobj3[] = { strVal };
                throw new WTException(
                        "com.ptc.core.HTMLtemplateutil.server.processors.processorsResource",
                        "59", aobj3);
            }
        else if (dataType.equals("java.lang.Boolean"))
            obj = Boolean.valueOf(strVal);
        else if (dataType.equals("java.sql.Timestamp"))
            try {
                Date date = null;
                try {
                    date = WTStandardDateFormat.parse(strVal, 3, locale,
                            timezone);
                }
                catch (ParseException parseexception) {
                    try {
                        date = WTStandardDateFormat.parse(strVal, 25, locale,
                                timezone);
                    }
                    catch (ParseException parseexception1) {
                        date = WTStandardDateFormat.parse(strVal, 26, locale,
                                timezone);
                    }
                }
                obj = new Timestamp(date.getTime());
            }
            catch (ParseException parseexception) {
                Object aobj5[] = { strVal };
                throw new WTException(
                        "com.ptc.core.HTMLtemplateutil.server.processors.processorsResource",
                        "60", aobj5);
            }
        else
            obj = new String(strVal);
        return obj;
    }

    /**
     * 解释IBA约束错误, 来自: EntityTaskDelegate
     * @param constraintexception   *
     * @param locale                *
     * @return                      *
     * @throws WTException
     */
    public static WTException interpretConstraintViolationException(
            ConstraintException constraintexception, Locale locale)
    throws WTException {
        AttributeIdentifier attributeidentifier = 
            constraintexception.getAttributeIdentifier();
        AttributeTypeIdentifier attributetypeidentifier = 
            (AttributeTypeIdentifier) attributeidentifier.getDefinitionIdentifier();
        AttributeContainerSpec attributecontainerspec = new AttributeContainerSpec();
        attributecontainerspec.putEntry(attributetypeidentifier, true, true);
        NewEntityCommand newentitycommand = new NewEntityCommand();
        try {
            ((NewEntityCommand) newentitycommand).setIdentifier(
                    attributetypeidentifier.getContext());
            newentitycommand.setFilter(attributecontainerspec);
            newentitycommand.setLocale(locale);
        }
        catch (WTPropertyVetoException wtpropertyvetoexception) {
            throw new WTException(wtpropertyvetoexception);
        }
        newentitycommand.execute();
        TypeInstance typeinstance = newentitycommand.getResult();
        AttributeTypeSummary attributetypesummary = 
            typeinstance.getAttributeTypeSummary((AttributeTypeIdentifier) 
                    attributeidentifier.getDefinitionIdentifier());
        String s = attributetypesummary.getLabel();
        //Object obj = constraintexception.getAttributeContent();
        ConstraintIdentifier constraintidentifier = 
            constraintexception.getConstraintIdentifier();
        String s1 = constraintidentifier.getEnforcementRuleClassname();
        ConstraintData constraintdata = constraintexception.getConstraintData();
        //String s2 = " ";
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
                }
                else if (range.hasLowerBound()) {
                    arraylist.add(range.getLowerBoundValue());
                    s4 = "73";
                }
                else if (range.hasUpperBound()) {
                    arraylist.add(range.getUpperBoundValue());
                    s4 = "74";
                }
            }
            else {
                s4 = "75";
            }
        }
        else if (s1.equals(
                "com.ptc.core.meta.container.common.impl.ImmutableConstraint"))
            s4 = "78";
        else if (s1.equals(
                "com.ptc.core.meta.container.common.impl.DiscreteSetConstraint")) {
            if (serializable instanceof DiscreteSet) {
                Object aobj[] = ((DiscreteSet) serializable).getElements();
                String s5 = "";
                for (int j = 0; j < aobj.length; j++)
                    s5 = s5 + aobj[j].toString() + ",";

                String s7 = s5.substring(0, s5.length() - 1);
                arraylist.add(s7);
                s4 = "83";
            }
            else {
                s4 = "84";
            }
        }
        else if (s1.equals("com.ptc.core.meta.container.common.impl.StringLengthConstraint")) {
            if (serializable instanceof AnalogSet) {
                Range range1 = ((AnalogSet) serializable).getBoundingRange();
                if (range1.hasLowerBound() && range1.hasUpperBound()) {
                    arraylist.add(range1.getLowerBoundValue());
                    arraylist.add(range1.getUpperBoundValue());
                    s4 = "79";
                }
                else if (range1.hasLowerBound()) {
                    arraylist.add(range1.getLowerBoundValue());
                    s4 = "80";
                }
                else if (range1.hasUpperBound()) {
                    arraylist.add(range1.getUpperBoundValue());
                    s4 = "81";
                }
            }
            else {
                s4 = "82";
            }
        }
        else if (s1.equals(
                "com.ptc.core.meta.container.common.impl.StringFormatConstraint")) {
            if (serializable instanceof DiscreteSet) {
                Object aobj1[] = ((DiscreteSet) serializable).getElements();
                String s6 = "";
                for (int k = 0; k < aobj1.length; k++)
                    s6 = s6 + "\"" + aobj1[k].toString() + "\" or ";

                String s8 = s6.substring(0, s6.length() - 4);
                arraylist.add(s8);
                s4 = "85";
            }
            else {
                s4 = "84";
            }
        }
        else if (s1.equals(
                "com.ptc.core.meta.container.common.impl.UpperCaseConstraint"))
            s4 = "86";
        else if (s1.equals(
                "com.ptc.core.meta.container.common.impl.ValueRequiredConstraint"))
            s4 = "77";
        else if (s1.equals(
                "com.ptc.core.meta.container.common.impl.WildcardConstraint")) {
            if (serializable instanceof WildcardSet) {
                arraylist.add(((WildcardSet) serializable).getValue());
                int i = ((WildcardSet) serializable).getMode();
                if (i == 1) {
                    s4 = "87";
                    arraylist.add(((WildcardSet) serializable).getValue());
                }
                else if (i == 2) {
                    if (((WildcardSet) serializable).isNegated())
                        s4 = "89";
                    else
                        s4 = "88";
                }
                else if (i == 3) {
                    if (((WildcardSet) serializable).isNegated())
                        s4 = "91";
                    else
                        s4 = "90";
                }
                else if (i == 4)
                    if (((WildcardSet) serializable).isNegated())
                        s4 = "93";
                    else
                        s4 = "92";
            }
            else {
                s4 = "84";
            }
        }
        else {
            s4 = "84";
        }
        if (s4 != null)
            return new WTException(s3, s4, arraylist.toArray());
        else
            return null;
    }
    
    /**
     * 获取指定softtype类型的下级softtype清单
     * 
     * @param parentType    指定的softtype名, 如wt.doc.WTDocument|com.ptc.cacgg.gywj
     * @param typeList      下级softtype类型的显示名称列表, 如: 工艺文件
     * @param typeMap       下级softtype类型的类型名称Map, key为显示名称, 
     *                      如: 工艺文件=>wt.doc.WTDocument|com.ptc.cacgg.gywj
     */
    public static void getSoftTypes(String parentType, ArrayList typeList, HashMap typeMap) {
        Vector v = (Vector) new WTTypeDefinitionObjectLocator().locate(WTTypeDefinitionObjectLocator.LATEST);
        //WTTypeDefinition td = null;
        
        int targetLevel = 0;
        if (parentType != null) {
            // 去除不需要的类型名称头
            String typeHead = WCTypeIdentifier.PROTOCOL + WCTypeIdentifier.HIERARCHY_SEPARATOR;
            if (parentType.startsWith(typeHead))
                parentType = parentType.substring(typeHead.length());
                
            StringTokenizer st = new StringTokenizer(parentType, WCTypeIdentifier.HIERARCHY_SEPARATOR);
            targetLevel = st.countTokens();
        }
        for (int i = 0; v != null && i < v.size(); i++) {
            TypeDefinition ttd = (TypeDefinition) v.get(i);
            if (ttd.isDeleted()) {
                //Debug.P("type definition node marked deleted: ", ttd.getName());
                continue;
            }
            //TypeDefinition parent = null;
            
            String typeLabel = ttd.getDisplayNameKey();
            String typeName = ttd.getName();
            int levels = 0;
            while (true) {
                try {
                    ttd = ttd.getParent();
                    if (ttd == null)
                        break;
                    
                    typeName = ttd.getName() + WCTypeIdentifier.HIERARCHY_SEPARATOR + typeName;
                    levels++;
                }
                catch (Exception e) {
                    //Debug.P("parent deleted: ", ttd.getName());
                    ttd = null;
                    typeName = null;
                    break;
                }
            }
            
            if (typeName == null) {
                // illegal orphan type definition node, should be ignored
                continue;
            }
            
            if (levels == targetLevel && (parentType == null || typeName.startsWith(parentType))) {
                if (typeList != null)
                    typeList.add(typeLabel);
                if (typeMap != null) 
                    typeMap.put(typeLabel, typeName);
                
                //Debug.P("found type: ", typeLabel, "=", typeName);
            }
        }
    }
    
    /**
     * 获取一个softtype下所有子类的type branch id (sorted)
     * @param fromTypeId    格式如：wt.doc.WTDocument|com.nriet.设计文档
     * @return
     */
    public static long[] getTypeIds(String fromTypeId) {
        if (fromTypeId == null)
            return new long[0];
        
        Vector v = (Vector) new WTTypeDefinitionObjectLocator().locate(
                WTTypeDefinitionObjectLocator.LATEST);
        //WTTypeDefinition td = null;
        
        // 去除不需要的类型名称头
        String typeHead = WCTypeIdentifier.PROTOCOL + WCTypeIdentifier.HIERARCHY_SEPARATOR;
        if (fromTypeId.startsWith(typeHead))
            fromTypeId = fromTypeId.substring(typeHead.length());
        TypeDefinitionReference tdr = null;
        try {
            tdr = ClientTypedUtility.getTypeDefinitionReference(fromTypeId);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (tdr == null)
            return new long[0];

        HashSet idSet = new HashSet();
        idSet.add(new Long(tdr.getKey().getBranchId()));
        boolean moreFound = true;
        while (moreFound) {
            moreFound = false;
            for (int i = 0; v != null && i < v.size(); i++) {
                TypeDefinition ttd = (TypeDefinition) v.get(i);
                if (ttd.isDeleted() || ttd.getParent() == null || ttd.getParent().isDeleted())
                    continue;
                
                Long parentId = new Long(ttd.getParent().getBranchIdentifier());
                Long typeId = new Long(ttd.getBranchIdentifier());
                if (idSet.contains(parentId) && !idSet.contains(typeId)) {
                    idSet.add(typeId);
                    moreFound = true;
                }
            }
        }
        
        int i = 0;
        long[] ida = new long[idSet.size()];
        for (Iterator it = idSet.iterator(); it.hasNext();) 
            ida[i++] = ((Long) it.next()).longValue();
        Arrays.sort(ida);
        
        return ida;
    }
    
    /**
     * 找到系统的站点internet domain，修正Soft Type的类型ID为fully qualified，如 - 
     * from: wt.doc.WTDocument | cacCAPPDocument
     * to:   wt.doc.WTDocument | com.cac.cacCAPPDocument
     * 
     * @param simpleId      类型ID简写形式
     * @return              类型ID完整形式
     * @throws WTException 
     */
    public static String qualifyTypeId(String simpleId) throws WTException {
        // 取站点internet domain
        String siteDomain = WTContainerHelper.service.getExchangeContainer()
            .getInternetDomain();
        siteDomain = siteDomain.replace('-', '_');
        
        // 计算逆序
        String reversedDomain = "";
        String[] segs = siteDomain.split("\\.");
        for (int i = segs.length - 1; i >= 0; i--) {
            if (i < segs.length - 1)
                reversedDomain += ".";
            reversedDomain += segs[i].trim();
        }
        
        // 分裂type id，逐段修正
        segs = simpleId.split("\\|");
        String result = "";
        for (int i = 0; i < segs.length; i++) {
            if (i > 0)
                result += WCTypeIdentifier.HIERARCHY_SEPARATOR;
            if (segs[i].indexOf(".") < 0 && (i != 0 || 
                    !segs[i].equals(WCTypeIdentifier.PROTOCOL)))
                result += reversedDomain + ".";
            result += segs[i];
        }
        
        return result;
    }
    
    /**
     * 根据Typed对象的getTypeDefinitionReference()值，获得类型定义对象WTTypeDefinition
     * @param tdr   *
     * @return      *
     */
    public static WTTypeDefinition getTypeDefinition(TypeDefinitionReference tdr) 
    throws Exception {
        long id = tdr.getKey().getId();
        QuerySpec qs = new QuerySpec(WTTypeDefinition.class);
        qs.appendWhere(new SearchCondition(WTTypeDefinition.class,
                WTTypeDefinition.PERSIST_INFO + "." 
                + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID,
                SearchCondition.EQUAL, id
                ), new int[]{0});
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        if (qr.size() <= 0)
            return null;
        return (WTTypeDefinition) qr.nextElement();
    }
    
    /**
     * 枚举列出站点的所有IBA属性定义，按中文显示名－属性名的方式存入HashMap
     * @param mDispName
     * @throws Exception
     */
    public static void enumSiteIBAs(HashMap mDispName, HashMap mNameDisp, 
            HashMap mNameDef) throws Exception {
        if (mDispName == null && mNameDisp == null && mNameDef == null)
            return;
        
        new Object() {
            public void enumIBAs(AbstractAttributeDefinizerNodeView from, 
                    HashMap mDispName, HashMap mNameDisp, HashMap mNameDef) 
            throws Exception {
                AbstractAttributeDefinizerNodeView[] children = null;
                if (from == null)
                    children = IBADefinitionHelper.service.getAttributeOrganizerRoots();
                else 
                    children = IBADefinitionHelper.service.getAttributeChildren(from);

                if (from != null) {
                    // 属性定义节点
                    if (from instanceof AttributeDefNodeView) {
                        AttributeDefDefaultView def = 
                                IBADefinitionHelper.service.getAttributeDefDefaultView(
                                (AttributeDefNodeView) from);
                        String disp = def.getDisplayName();
                        String name = def.getName();
                        
                        if (mDispName != null)
                            mDispName.put(disp, name);
                        if (mNameDisp != null)
                            mNameDisp.put(name, disp);
                        if (mNameDef != null)
                            mNameDef.put(name, def);
                    }
                    /* 属性组织器节点
                    else {
                        Debug.P(">>>>>> Org Node: ", from.getName(), " <<<<<<");
                    }
                    */
                }
                
                for (int i = 0; children != null && i < children.length; i++) {
                    enumIBAs(children[i], mDispName, mNameDisp, mNameDef);
                }
            }
        }.enumIBAs(null, mDispName, mNameDisp, mNameDef);
    }
    
    public static String getObjectIcon(WTObject o) throws Exception {
        IconDelegateFactory factory = new IconDelegateFactory();
        IconDelegate delegate = factory.getIconDelegate(o);
        IconSelector selector = delegate.getStandardIconSelector();
        while (!selector.isResourceKey()) {
            delegate = delegate.resolveSelector(selector);
            IconSelector selector1 = delegate.getStandardIconSelector();
            selector1.setAdornments(selector.getAdornments());
            selector = selector1;
        }
        return selector.getIconKey();
    }

    /**
     * Find parts of specified numbers and versions. numbers must be specified
     * and must be different each other. Versions can be specified or not. When
     * specified version not found or version not specified, a latest version
     * will be returned if any.
     * @param numbers       part numbers array
     * @param versions      part versions array
     * @param parts         result parts map with key of number, value of part
     * @throws Exception
     */
    public static void findParts(String[] numbers, String[] versions, Map parts)
    throws Exception {
        findBizObjs(WTPart.class, WTPart.NUMBER, numbers, versions, parts);
    }
    
    /**
     * Find epm docs of specified numbers and versions. numbers must be specified
     * and must be different each other. Versions can be specified or not. When
     * specified version not found or version not specified, a latest version
     * will be returned if any.
     * @param numbers       numbers array
     * @param versions      versions array
     * @param epms          result objects map with key of number, value of epm doc
     * @throws Exception
     */
    public static void findEPMs(String[] numbers, String[] versions, Map epms)
    throws Exception {
        findBizObjs(EPMDocument.class, EPMDocument.NUMBER, numbers, versions, epms);
    }
    
    /**
     * Find wt docs of specified numbers and versions. numbers must be specified
     * and must be different each other. Versions can be specified or not. When
     * specified version not found or version not specified, a latest version
     * will be returned if any.
     * @param numbers       numbers array
     * @param versions      versions array
     * @param docs          result objects map with key of number, value of wt doc
     * @throws Exception
     */
    public static void findDocs(String[] numbers, String[] versions, Map docs)
    throws Exception {
        findBizObjs(WTDocument.class, WTDocument.NUMBER, numbers, versions, docs);
    }
    
    /**
     * Find a part of specified number and version. The number must be specified. while
     * version can be specified or not. If the version is not specified, latest version
     * will be found if exists.
     * @param number        target number
     * @param version       target version
     * @return              target wtpart object or null if not found
     * @throws Exception
     */
    public static WTPart findPart(String number, String version) throws Exception {
        return (WTPart) findBizObj(WTPart.class, WTPartMaster.class,
                WTPart.NUMBER, WTPartMaster.NUMBER, number, version);
    }
    
    /**
     * Find an epmdoc of specified number and version. The number must be specified. while
     * version can be specified or not. If the version is not specified, latest version
     * will be found if exists.
     * @param number        target number
     * @param version       target version
     * @return              target wtpart object or null if not found
     * @throws Exception
     */
    public static EPMDocument findEPM(String number, String version) throws Exception {
        return (EPMDocument) findBizObj(EPMDocument.class, EPMDocumentMaster.class,
                EPMDocument.NUMBER, EPMDocumentMaster.NUMBER, number, version);
    }
    
    /**
     * Find a wtdoc of specified number and version. The number must be specified. while
     * version can be specified or not. If the version is not specified, latest version
     * will be found if exists.
     * @param number        target number
     * @param version       target version
     * @return              target wtpart object or null if not found
     * @throws Exception
     */
    public static WTDocument findDoc(String number, String version) throws Exception {
        return (WTDocument) findBizObj(WTDocument.class, WTDocumentMaster.class,
                WTDocument.NUMBER, WTDocumentMaster.NUMBER, number, version);
    }
    
    private static void findBizObjs(Class objClass, String numberField,
            String[] numbers, String[] versions, Map objs) throws Exception {
        if (numbers.length != versions.length)
            throw new Exception("INTERNAL ERROR: number of obj numbers and " +
                    "number of obj versions should be same!");
        
        Map vers = new HashMap();
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = numbers[i] == null ? null : numbers[i].toUpperCase();
            versions[i] = versions[i] == null ? null : versions[i].toUpperCase();
            vers.put(numbers[i], versions[i]);
        }
        QuerySpec qs = new QuerySpec(objClass);
        qs.appendWhere(new SearchCondition(objClass, 
                numberField, numbers, true), new int[]{0});
        qs = new LatestConfigSpec().appendSearchCriteria(qs);
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        Vector pv = qr.getObjectVectorIfc().getVector();
        
        // Latest version first
        Collections.sort(pv, new Comparator() {
            public int compare(Object o1, Object o2) {
                String v1 = ((Versioned) o1).getVersionIdentifier().getValue();
                String v2 = ((Versioned) o2).getVersionIdentifier().getValue();
                return v2.compareTo(v1);
            }
        });
        
        for (Iterator it = pv.iterator(); it.hasNext();) {
            Versioned p = (Versioned) it.next();
            String number;
                if (p instanceof WTPart)
                    number = ((WTPart) p).getNumber();
                else if (p instanceof EPMDocument)
                    number = ((EPMDocument) p).getNumber();
                else
                    number = ((WTDocument) p).getNumber();
            String version = p.getVersionIdentifier().getValue();
            String targetVersion = (String) vers.get(number);
            
            // get the latest version or the correct version
            if (objs.get(number) == null || version.equals(targetVersion))
                objs.put(number, p);
        }
    }
    
    private static RevisionControlled findBizObj(Class objClass, Class masterClass, 
            String objNumber, String masterNumber, String number, String version) throws Exception {
        if (version != null && !version.equals("")) {
            QuerySpec qs = new QuerySpec(objClass);
            qs.appendWhere(new SearchCondition(objClass,
                    objNumber, 
                    SearchCondition.EQUAL,
                    number), new int[]{0});
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(objClass,
                    Versioned.VERSION_IDENTIFIER + "." + VersionIdentifier.VERSIONID,
                    SearchCondition.EQUAL,
                    version), new int[]{0});
            qs = new LatestConfigSpec().appendSearchCriteria(qs);
            QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
            return qr.hasMoreElements() ? (RevisionControlled) qr.nextElement() : null;
        } else {
            QuerySpec qs = new QuerySpec(masterClass); 
            qs.appendWhere(new SearchCondition(masterClass, masterNumber, SearchCondition.EQUAL, number), new int[]{0});
            QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
            if (!qr.hasMoreElements())
                return null;
            Mastered m = (Mastered) qr.nextElement();
            return (RevisionControlled) VersionControlHelper.service.allVersionsOf(m).nextElement();
        }
    }
    
    /**
     * 删除指定编号/版本的文档,如要删除所有版本,指定version为null
     * @param number    目标对象编号
     * @param version   目标对象版本
     */
    public static void deleteDoc(String number, String version) throws Exception {
        WTDocument doc = findDoc(number, version);
        if (doc == null)
            return;
        deleteBizObj(doc, version == null ? true : false);
    }
    
    /**
     * 删除指定编号/版本的PART,如要删除所有版本,指定version为null
     * @param number    目标对象编号
     * @param version   目标对象版本
     */
    public static void deletePart(String number, String version) throws Exception {
        WTPart part = findPart(number, version);
        if (part == null)
            return;
        deleteBizObj(part, version == null ? true : false);
    }
    
    /**
     * 删除指定编号/版本的CAD文档,如要删除所有版本,指定version为null
     * @param number    目标对象编号
     * @param version   目标对象版本
     */
    public static void deleteEPM(String number, String version) throws Exception {
        EPMDocument epm = findEPM(number, version);
        if (epm == null)
            return;
        deleteBizObj(epm, version == null ? true : false);
    }
    
    private static void deleteBizObj(RevisionControlled obj, boolean deleteAllVersion) throws Exception {
        Transaction tx = new Transaction();
        tx.start();
        try {
            // 先删除与指定对象每个Iteration的关联关系: IteratedDescribeLink
            HashSet linkSet = new HashSet();
            QueryResult qrIter;
            if (deleteAllVersion)
                qrIter = VersionControlHelper.service.allIterationsOf(obj.getMaster());
            else
                qrIter = VersionControlHelper.service.iterationsOf(obj);
            while (qrIter.hasMoreElements()) {
                Iterated ddd = (Iterated) qrIter.nextElement();
                linkSet.addAll((PersistenceServerHelper.manager.expand(ddd,
                        IteratedDescribeLink.DESCRIBES_ROLE, 
                        IteratedDescribeLink.class, false)).getObjectVectorIfc().getVector());
            }
            
            // 如果只有一个版本，则Master也将被自动删除，需要准备将Master的关联删除
            // IteratedUsageLink, IteratedReferenceLink
            int verCount = VersionControlHelper.service.allVersionsOf(obj).size();
            //Debug.P("version count: " + verCount);
            if (verCount == 1) {
                linkSet.addAll((PersistenceServerHelper.manager.expand(
                        obj.getMaster(), IteratedUsageLink.USED_BY_ROLE,
                        IteratedUsageLink.class, false)).getObjectVectorIfc().getVector());
                linkSet.addAll((PersistenceServerHelper.manager.expand(
                        obj.getMaster(), IteratedReferenceLink.REFERENCED_BY_ROLE,
                        IteratedReferenceLink.class, false)).getObjectVectorIfc().getVector());
            }
            
            // 删除关联Link
            //Debug.P("deleting relations: " + linkSet.size());
            for (Iterator it = linkSet.iterator(); it.hasNext();) {
                BinaryLink link = (BinaryLink) it.next();
                //Debug.P("removing: ", link.getRoleAObjectRef(), ", "
                //        , link.getRoleBObjectRef());
                PersistenceServerHelper.manager.remove(link);
            }
            
            // 删除对象本身
            PersistenceHelper.manager.delete(obj);
            tx.commit();
        }
        catch (Exception e) {
            tx.rollback();
            throw e;
        }
    }

    /**
     * 创建访问控制策略规则
     * 
     * @param adminDomainRef    域
     * @param persistedType     对象类型
     * @param lcStateKey        生命周期状态, 全部适用时用null
     * @param ppRef             principal参考
     * @param grantPermissions  赋予权限, Vector of AccessPermission
     * @param denyPermissions   阻止权限, Vector of AccessPermission
     * @throws WTException
     */
    public static void createAccessControlRule(AdminDomainRef adminDomainRef, String persistedType, String lcStateKey, 
            WTPrincipalReference ppRef, Vector grantPermissions, Vector denyPermissions) throws WTException {
//        AccessControlHelper.manager.createAccessControlRule(adminDomainRef, persistedType, lcStateKey, 
//                ppRef, grantPermissions, denyPermissions);
    }
    
    /**
     * 更新访问控制策略规则
     * 
     * @param adminDomainRef    域
     * @param persistedType     对象类型
     * @param lcStateKey        生命周期状态, 全部适用时用null
     * @param ppRef             principal参考
     * @param grantPermissions  赋予权限, Vector of AccessPermission
     * @param denyPermissions   阻止权限, Vector of AccessPermission
     * @throws WTException
     */
    public static void updateAccessControlRule(AdminDomainRef adminDomainRef, String persistedType, String lcStateKey, 
            WTPrincipalReference ppRef, Vector grantPermissions, Vector denyPermissions) throws WTException {
//        AccessControlHelper.manager.updateAccessControlRule(adminDomainRef, persistedType, lcStateKey, 
//                ppRef, grantPermissions, denyPermissions);
    }
    
    /**
     * 删除访问控制策略规则
     * 
     * @param adminDomainRef    域
     * @param persistedType     对象类型
     * @param lcStateKey        生命周期状态, 全部适用时用null
     * @param ppRef             principal参考
     * @throws WTException
     */
    public static void deleteAccessControlRule(AdminDomainRef adminDomainRef, String persistedType, String lcStateKey, 
            WTPrincipalReference ppRef) throws WTException {
//        AccessControlHelper.manager.deleteAccessControlRule(adminDomainRef, persistedType, lcStateKey, ppRef);
    }

    /**
     * 删除访问控制策略规则
     * 
     * @param adminDomainRef    域
     * @throws WTException
     */
    public static void deleteAccessControlRule(AdminDomainRef adminDomainRef) throws WTException {
        AccessControlHelper.manager.deleteAccessControlRules(adminDomainRef);
    }

    /**
     * 获取访问控制策略规则
     * 
     * @param adminDomainRef    域
     * @param persistedType     对象类型
     * @param lcStateKey        生命周期状态, 全部适用时用null
     * @param ppRef             principal参考
     * @return                  数组0项 - 赋予权限, 数组1项 - 阻止权限
     * @throws WTException
     */
//    public static EnumeratorVector[] getAccessControlRule(AdminDomainRef adminDomainRef, String persistedType, String lcStateKey, 
//            WTPrincipalReference ppRef) throws WTException {
//        return AccessControlHelper.manager.getAccessControlRule(adminDomainRef, persistedType, lcStateKey, ppRef);
//    }

    /**
     * 查找obj对象的电子签名历史记录
     * 
     * @param obj           ElectronicallySignable对象
     * @return              SignatureLink的QueryResult
     * @throws WTException
     */
    public static QueryResult getElectronicSignatures(Persistable obj) throws WTException {
        QuerySpec qs = new QuerySpec(SignatureLink.class);
        qs.appendWhere(new SearchCondition(SignatureLink.class, 
                SignatureLink.ROLE_AOBJECT_REF + "." + ObjectReference.KEY + "." + QueryKey.CLASSNAME,
                SearchCondition.EQUAL,
                obj.getClass().getName()
                ), new int[]{0});
        qs.appendAnd();
        qs.appendWhere(new SearchCondition(SignatureLink.class,
                SignatureLink.ROLE_AOBJECT_REF + "." + ObjectReference.KEY + "." + ObjectIdentifier.ID,
                SearchCondition.EQUAL,
                obj.getPersistInfo().getObjectIdentifier().getId()
                ), new int[]{0});
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec)qs);
        return qr;
    }
    
	/**
	 * 按照给定格式获取当前时间
	 * 
	 * @author 
	 * @param DATE_FORMAT
	 * @return
	 */
	public static String getCurrentTime(String DATE_FORMAT) {
		Locale locale = WTContext.getContext().getLocale();
		Calendar calendar = Calendar.getInstance(WTContext.getContext()
				.getTimeZone(), locale);
		java.util.Date datDate = calendar.getTime();
		return WTStandardDateFormat.format(datDate, DATE_FORMAT, locale,
				calendar.getTimeZone());

	}
	
	/**
	 * Following is TEST code
	 * @throws InvocationTargetException 
	 * @throws RemoteException 
	 * @throws WTException 
	 * 
	 */
	
	public static void testRetrieveAllIbas(String oid) throws RemoteException, InvocationTargetException, WTException {
		if (!RemoteMethodServer.ServerFlag) {
            String method = "testRetrieveAllIbas";
            String klass = WTUtil.class.getName();
            Class[] types = {String.class};
            Object[] values = {oid};
            RemoteMethodServer.getDefault().setUserName("wcadmin");
            RemoteMethodServer.getDefault().setPassword("wcadmin");
            RemoteMethodServer.getDefault().invoke(method, klass, null, types, values);
		} else {
			ReferenceFactory rf = new ReferenceFactory();
			Object obj = rf.getReference(oid).getObject();
			if (obj instanceof IBAHolder) {
    			System.out.println("========== " + obj.toString() + " ==========");
				ArrayList ibaList = new ArrayList();
		        getIBAValues((IBAHolder) obj, ibaList, null);
		        for (Object entry : ibaList) {
	    			System.out.println("---------- IBA Entry ----------");
		        	if (entry instanceof Map) {
		        		Map ibaInfo = (Map) entry;
		        		for (Object key : ibaInfo.keySet()) {
		        			System.out.println(key.toString() + " : " + ibaInfo.get(key));
		        		}
		        	}
		        }
			}
		}
	}
	
	public static Vector getConfirmedGroupMembers(WTContained cobj)
	{
		Vector vec = new Vector();
		try
		{		
			WTContainer container=WTContainerHelper.getContainer((WTContained)cobj);
			ContainerTeam containerTeam=ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged)container);
			Vector vecRole = containerTeam.getRoles();
			
			for (int i = 0; i < vecRole.size(); i++) {
				Role tempRole = (Role)vecRole.get(i);
				List tempUserList = containerTeam.getAllPrincipalsForTarget(tempRole);
				for (int k = 0; tempUserList != null && k < tempUserList.size(); k++) {
					WTPrincipalReference wtprincipalreference = (WTPrincipalReference) tempUserList.get(k);
					Persistable persistable = wtprincipalreference.getObject();
					if(persistable != null && persistable instanceof WTUser){
						WTUser tempUser = (WTUser)persistable;
						if(!vec.contains(tempUser))
							vec.add(tempUser); 
					}else if (persistable instanceof WTGroup){
						WTGroup group = (WTGroup)persistable;
						List list = getGroupMembersOfUser(group);
						for (Iterator iterator = list.iterator(); iterator.hasNext();) {
							WTUser user = (WTUser) iterator.next();
							if(!vec.contains(user))
								vec.add(user);
						}
					}
				}
			}
			
			
		}
		catch (Exception e)
		{
			e.printStackTrace();	
		}
		
		return vec;
	}
	
	/**
	 * 本方法用于查找系统组中的用户,内部方法,不可调用.
	 * @param 参数是一个WTGroup对象
	 * @return 返回结果是一个list列表,里面存放一组WTUser对象
	 * */
	public static List<WTUser> getGroupMembersOfUser(WTGroup group)
			throws WTException {
		List<WTUser> users = new ArrayList<WTUser>();
		Enumeration member = group.members();

		while (member.hasMoreElements()) {
			WTPrincipal principal = (WTPrincipal) member.nextElement();
			if (principal instanceof WTUser) {
				users.add((WTUser) principal);
			} else if (principal instanceof WTGroup) {
				//System.out.println("contains group in group");
				List<WTUser> ausers = getGroupMembersOfUser((WTGroup) principal);
				for (int i = 0; i < ausers.size(); i++) {
					users.add(ausers.get(i));
				}
			}
		}

		return users;
	}

	public static void main(String[] args) {
		try {
			if (args.length == 1) {
				testRetrieveAllIbas(args[0]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public static String getDateString() {
        String returnValue = "*";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        TimeZone tz = WTContext.getContext().getTimeZone();
        SimpleTimeZone stz = new SimpleTimeZone(tz.getRawOffset(), tz.getID());
        sdf.setTimeZone(stz);
        returnValue = sdf.format(Calendar.getInstance(Locale.getDefault()).getTime());
        return returnValue;
    }
    public static String getVersion(Versioned version) {
      return   version.getVersionIdentifier().getValue()+"."+version.getIterationIdentifier().getValue();
    }

    public static String getCellStringValue(Cell cell) {
        String value = null;
        if (cell != null) {
            int type = cell.getCellType();
            if (type == HSSFCell.CELL_TYPE_STRING) {
                value = cell.getStringCellValue();
            } else if (HSSFDateUtil.isCellDateFormatted(cell)) {
                Date date = cell.getDateCellValue();
                value = date.toString();
            } else if (type == HSSFCell.CELL_TYPE_NUMERIC) {
                double dvalue = cell.getNumericCellValue();
                if (isIntegerNum(String.valueOf(dvalue))) {
                    value = String.valueOf((int) dvalue);
                } else {
                    value = String.valueOf(dvalue);
                }
            } else if (type == HSSFCell.CELL_TYPE_BOOLEAN) {
                value = cell.getBooleanCellValue() + "";
            } else if (type == HSSFCell.CELL_TYPE_BLANK) {
                value = "";
            } else if (type == HSSFCell.CELL_TYPE_FORMULA) {
                value = cell.getCellFormula();
            } else {
                value = cell.getStringCellValue();
            }

            if (value != null) {
                value = value.trim();
            }
        }
        return value;
    }


    private static boolean isIntegerNum(String s) {
        String[] strNum = s.split("\\.");
        try {
            if (Integer.parseInt(strNum[1]) == 0) {
                return true;
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            return true;
        }
        return false;
    }
}