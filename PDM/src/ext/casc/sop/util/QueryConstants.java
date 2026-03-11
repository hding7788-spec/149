package ext.casc.sop.util;

import wt.util.WTAttributeNameIfc;

/**
 *
 * Description： 查询常量<br>
 * @Author： ChenJianHong <br>
 * Create Date： 2018年5月8日 <br>
 * @Version： 0.1
 */
public class QueryConstants {

    public static final int[] INDEX = { 0 };
    /** 对象OID Key */
    public static final String OID = WTAttributeNameIfc.ID_NAME;
    /** 对象版本创建者OID Key */
    public static final String CREATOR_ID = "iterationInfo.creator.key.id";
    /** 对象容器OID Key */
    public static final String CONTAINER_ID = "containerReference.key.id";
    public static final String ROLEAOBJECT = WTAttributeNameIfc.ROLE_AOBJECT_ROLE;
    /** RoleA ID */
    public static final String ROLEAID = WTAttributeNameIfc.ROLEA_OBJECT_ID;
    /** RoleB ID */
    public static final String ROLEBID = WTAttributeNameIfc.ROLEB_OBJECT_ID;
    /** 状态 Key */
    public static final String STATE = "state.state";
    /** 对象创建者OID Key */
    public static final String USER_ID = "creator.key.id";
    /** 类型Branch ID */
    public static final String TYPE_BRANCHID = "typeDefinitionReference.key.branchId";
    /** 视图ID Key */
    public static final String VIEWID = "view.key.id";
    /** 容器名称 */
    public static final String CONTAINER_CLASSNAME = "containerReference.key.classname";
    /** definitionReference ID */
    public static final String KEY_ID_DEFINITION = "definitionReference.key.id";
    /** IBAHolderReference ID */
    public static final String KEY_ID_IBAHOLDER = "theIBAHolderReference.key.id";
}
