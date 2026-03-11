package ext.casc.sop.util;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.HashMap;

import org.apache.log4j.Logger;

import wt.enterprise.RevisionControlled;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.httpgw.URLFactory;
import wt.iba.definition.IBADefinitionException;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.query.ArrayExpression;
import wt.query.AttributeRange;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.Iterated;
import wt.vc.Mastered;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.vc.config.LatestConfigSpec;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmActionServiceHelper;
import com.ptc.netmarkets.util.misc.NmURL;

public class QueryUtil {

    private static final Logger LOGGER = Logger.getLogger(QueryUtil.class);

    /**
     * 判断对象是否存在
     *
     * @param classs
     * @param number
     * @return
     * @throws WTException
     */
    public static boolean isExistByNumber(Class<?> classs, String number) throws WTException {
        boolean flag = false;
        if (number != null) {
            QuerySpec querySpec = new QuerySpec(classs);
            appendWhere(querySpec, classs, WTPart.NUMBER, true, true, number);
            QueryResult queryResult = find(querySpec);
            if (queryResult.hasMoreElements()) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 查询结果添加最新限制
     *
     * @param queryResult
     * @throws WTException
     */
    public static QueryResult addLatestConfigSpec(QueryResult queryResult) throws WTException {
        LatestConfigSpec latestConfigSpec = new LatestConfigSpec();
        queryResult = latestConfigSpec.process(queryResult);
        return queryResult;
    }

    /**
     *
     * @param querySpec
     * @param classs
     * @param tableName
     * @param isAnd
     * @param isEqual
     * @param value
     * @throws WTException
     */
    public static void appendWhere(QuerySpec querySpec, Class<?> classs, String tableName, boolean isAnd, boolean isEqual, String value) throws WTException {
        if (querySpec.getConditionCount() != 0) {
            if (isAnd) {
                querySpec.appendAnd();
            } else {
                querySpec.appendOr();
            }
        }
        if (isEqual) {
            querySpec.appendWhere(new SearchCondition(classs, tableName, SearchCondition.EQUAL, value), QueryConstants.INDEX);
        } else {
            querySpec.appendWhere(new SearchCondition(classs, tableName, SearchCondition.LIKE, value), QueryConstants.INDEX);
        }
    }

    /**
     *
     * @param persistable
     * @return
     */
    public static long getOid(Persistable persistable) {
        long result = -1;
        if (persistable != null) {
            result = PersistenceHelper.getObjectIdentifier(persistable).getId();
        }
        return result;
    }

    /**
     *
     * @param querySpec
     * @param classs
     * @param tableName
     * @param isAnd
     * @param searchCondition
     * @param value
     * @throws WTException
     */
    public static void appendWhere(QuerySpec querySpec, Class<?> classs, String tableName, boolean isAnd, String searchCondition, String value)
            throws WTException {
        if (querySpec.getConditionCount() != 0) {
            if (isAnd) {
                querySpec.appendAnd();
            } else {
                querySpec.appendOr();
            }
        }
        querySpec.appendWhere(new SearchCondition(classs, tableName, searchCondition, value), QueryConstants.INDEX);
    }

    /**
     *
     * @param querySpec
     * @param classs
     * @param tableName
     * @param isAnd
     * @param searchCondition
     * @param value
     * @throws WTException
     */
    public static void appendWhere(QuerySpec querySpec, Class<?> classs, String tableName, boolean isAnd, String searchCondition, long value)
            throws WTException {
        if (querySpec.getConditionCount() != 0) {
            if (isAnd) {
                querySpec.appendAnd();
            } else {
                querySpec.appendOr();
            }
        }
        querySpec.appendWhere(new SearchCondition(classs, tableName, searchCondition, value), QueryConstants.INDEX);
    }

    /**
     *
     * @param querySpec
     * @param classs
     * @param tableName
     * @param isAnd
     * @param isEqual
     * @param value
     * @throws WTException
     */
    public static void appendWhere(QuerySpec querySpec, Class<?> classs, String tableName, boolean isAnd, boolean isEqual, long value) throws WTException {
        if (querySpec.getConditionCount() != 0) {
            if (isAnd) {
                querySpec.appendAnd();
            } else {
                querySpec.appendOr();
            }
        }
        if (isEqual) {
            querySpec.appendWhere(new SearchCondition(classs, tableName, SearchCondition.EQUAL, value), QueryConstants.INDEX);
        } else {
            querySpec.appendWhere(new SearchCondition(classs, tableName, SearchCondition.LIKE, value), QueryConstants.INDEX);
        }
    }

    /**
     *
     * @param querySpec
     * @param classs
     * @param tableName
     * @param isAnd
     * @param isEqual
     * @param value
     * @throws WTException
     */
    public static void appendWhere(QuerySpec querySpec, Class<?> classs, String tableName, boolean isAnd, boolean isEqual, int value) throws WTException {
        if (querySpec.getConditionCount() != 0) {
            if (isAnd) {
                querySpec.appendAnd();
            } else {
                querySpec.appendOr();
            }
        }
        if (isEqual) {
            querySpec.appendWhere(new SearchCondition(classs, tableName, SearchCondition.EQUAL, value), QueryConstants.INDEX);
        } else {
            querySpec.appendWhere(new SearchCondition(classs, tableName, SearchCondition.LIKE, value), QueryConstants.INDEX);
        }
    }

    /**
     * 添加时间范围查询接口
     *
     * @param querySpec
     * @param classs
     * @param tableName
     * @param isAnd
     * @param time_from
     * @param time_to
     * @throws WTException
     */
    public static void appendWhere(QuerySpec querySpec, Class<?> classs, String tableName, boolean isAnd, Timestamp time_from, Timestamp time_to)
            throws WTException {
        if (querySpec.getConditionCount() != 0) {
            if (isAnd) {
                querySpec.appendAnd();
            } else {
                querySpec.appendOr();
            }
        }
        querySpec.appendWhere(new SearchCondition(classs, tableName, true, new AttributeRange(time_from, time_to)), QueryConstants.INDEX);
    }

    /**
     * IN
     *
     * @param querySpec
     * @param classs
     * @param tableName
     * @param isAnd
     * @param idList
     * @throws WTException
     */
    public static void appendWhere(QuerySpec querySpec, Class<?> classs, String tableName, boolean isAnd, long[] idList) throws WTException {
        if (querySpec.getConditionCount() != 0) {
            if (isAnd) {
                querySpec.appendAnd();
            } else {
                querySpec.appendOr();
            }
        }
        querySpec.appendWhere(new SearchCondition(new ClassAttribute(classs, tableName), SearchCondition.IN, new ArrayExpression(idList)),
                QueryConstants.INDEX);
    }

    /**
     * IN
     *
     * @param querySpec
     * @param classs
     * @param tableName
     * @param isAnd
     * @param valueList
     * @param isIgnoreCase
     *            true:不处理参数,查询区分大小写 false: 不区分大小写,查询时将数据库值和参数全部转化为大写
     * @throws WTException
     */
    public static void appendWhere(QuerySpec querySpec, Class<?> classs, String tableName, boolean isAnd, String[] valueList, boolean isIgnoreCase)
            throws WTException {
        if (querySpec.getConditionCount() != 0) {
            if (isAnd) {
                querySpec.appendAnd();
            } else {
                querySpec.appendOr();
            }
        }
        querySpec.appendWhere(new SearchCondition(classs, tableName, valueList, isIgnoreCase), QueryConstants.INDEX);
    }

    /**
     *
     * @param querySpec
     * @param classs
     * @param tableName
     * @param isEqual
     * @param value
     * @throws WTException
     */
    public static void appendWhere(QuerySpec querySpec, Class<?> classs, String tableName, boolean isEqual, String value) throws WTException {
        if (isEqual) {
            querySpec.appendWhere(new SearchCondition(classs, tableName, SearchCondition.EQUAL, value), QueryConstants.INDEX);
        } else {
            querySpec.appendWhere(new SearchCondition(classs, tableName, SearchCondition.LIKE, value), QueryConstants.INDEX);
        }
    }

    /**
     * 根据类型构建query
     *
     * @param querySpec
     * @param objectClass
     * @param isAnd
     * @param objectType
     * @throws WTException
     */
    public static void appendTypeWhere(QuerySpec querySpec, Class<?> objectClass, boolean isAnd, String objectType) throws WTException {
        TypeDefinitionReference typeRef;
        try {
            typeRef = TypedUtilityServiceHelper.service.getTypeDefinitionReference(objectType);
        } catch (RemoteException e) {
            throw new WTException(e);
        }
        if (typeRef != null) {
            if (querySpec.getConditionCount() != 0) {
                if (isAnd) {
                    querySpec.appendAnd();
                } else {
                    querySpec.appendOr();
                }
            }
            long branchId = typeRef.getKey().getBranchId();
            querySpec.appendWhere(new SearchCondition(objectClass, QueryConstants.TYPE_BRANCHID, SearchCondition.EQUAL, branchId), QueryConstants.INDEX);
        }
    }

    /**
     * 通过软属性构建子查询语句
     *
     * @param ibaName
     * @param ibaValue
     * @param isEqual
     * @return
     * @throws WTException
     */
    public static SubSelectExpression getSubSelectExpression(String ibaName, String ibaValue, boolean isEqual) throws WTException {
        SubSelectExpression result = null;
        QuerySpec querySpec = new QuerySpec();
        AttributeDefDefaultView defDefaultView = null;
        try {
            defDefaultView = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
        } catch (RemoteException e) {
            throw new WTException(e);
        }
        if (defDefaultView != null) {
            long ibaDefId = defDefaultView.getObjectID().getId();
            int index = querySpec.appendClassList(StringValue.class, false);
            int[] indexArray = new int[] { index };
            querySpec.appendSelect(new ClassAttribute(StringValue.class, QueryConstants.KEY_ID_IBAHOLDER), indexArray, false);
            querySpec.appendWhere(new SearchCondition(StringValue.class, QueryConstants.KEY_ID_DEFINITION, SearchCondition.EQUAL, ibaDefId), indexArray);
            querySpec.appendAnd();
            String searchCondition = null;
            if (isEqual) {
                searchCondition = SearchCondition.EQUAL;
            } else {
                searchCondition = SearchCondition.LIKE;
            }
            querySpec.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE2, searchCondition, ibaValue), indexArray);
            result = new SubSelectExpression(querySpec);
        } else {
            String errorMsg = "No IBA Definition: %s";
            errorMsg = String.format(errorMsg, ibaName);
            throw new IBADefinitionException(errorMsg);
        }
        return result;
    }

    /**
     * OrderBy
     *
     * @param querySpec
     * @param classs
     * @param tableName
     * @param isDesc
     * @throws WTException
     */
    public static void appendOrderBy(QuerySpec querySpec, Class<?> classs, String tableName, boolean isDesc) throws WTException {
        OrderBy orderBy = new OrderBy(new ClassAttribute(classs, tableName), isDesc);
        querySpec.appendOrderBy(orderBy, QueryConstants.INDEX);
    }

    /**
     *
     * @param oid OR:wt.part.WTPart:138421
     * @return
     * @throws WTException
     */
    public static Object getObjectByOid(String oid) throws WTException {
        Object obj = null;
        ReferenceFactory factory = new ReferenceFactory();
        WTReference reference = factory.getReference(oid);
        if (reference != null) {
            obj = reference.getObject();
        }
        return obj;
    }

    /**
     *
     * @param classType
     * @param oid
     * @return
     * @throws WTException
     */
    public static WTObject getObjectByOid(Class<?> classType, long oid) throws WTException {
        WTObject object = null;
        QuerySpec querySpec = new QuerySpec(classType);
        appendWhere(querySpec, classType, WTAttributeNameIfc.ID_NAME, true, true, oid);
        QueryResult queryResult = find(querySpec);
        if (queryResult.hasMoreElements()) {
            object = (WTObject) queryResult.nextElement();
        }
        return object;
    }

    /**
     * <li>获取对象最新版本
     *
     * @param iterated
     * @return
     * @throws WTException
     */
    public static Iterated getLatestIteration(Iterated iterated) throws WTException {
        Iterated result = null;
        if (iterated != null) {
            Object object = getLatestIteration(iterated.getMaster());
            if (object instanceof Iterated) {
                result = (Iterated) object;
            }
        }
        return result;
    }

    /**
     * <li>获取对象最新版本
     *
     * @param mastered
     * @return
     * @throws WTException
     */
    public static Object getLatestIteration(Mastered mastered) throws WTException {
        Object result = null;
        QueryResult queryResult = VersionControlHelper.service.allVersionsOf(mastered);
        if (queryResult.hasMoreElements()) {
            result = queryResult.nextElement();
        }
        return result;
    }

    /**
     * @Description 获取对象版本
     * @author ChenJianHong
     * @param revisionControlled
     * @return
     */
    public static String getVersion(RevisionControlled revisionControlled) {
        StringBuilder versionStr = new StringBuilder();
        String point = ".";
        versionStr.append(revisionControlled.getVersionIdentifier().getValue());
        versionStr.append(point);
        versionStr.append(revisionControlled.getIterationIdentifier().getValue());
        return versionStr.toString();
    }

    /**
     * @Description 检出对象
     * @author ChenJianHong
     * @param workable
     * @param description
     * @return
     * @throws WTException
     */
    public static Workable checkout(Workable workable, String description) throws WTException {
        Workable workingCopy = null;
        if (!WorkInProgressHelper.isCheckedOut(workable)) {
            try {
                WorkInProgressHelper.service.checkout(workable, WorkInProgressHelper.service.getCheckoutFolder(), description);
            } catch (WTPropertyVetoException e) {
                throw new WTException("检出对象异常: " + e);
            }
            workingCopy = WorkInProgressHelper.service.workingCopyOf(workable);
        } else {
            if (!WorkInProgressHelper.isWorkingCopy(workable)) {
                workingCopy = WorkInProgressHelper.service.workingCopyOf(workable);
            } else {
                workingCopy = workable;
            }
        }
        return workingCopy;
    }

    /**
     * 检入对象
     *
     * @param workable
     * @return
     * @throws WTException
     */
    public static Workable checkin(Workable workable, String description) throws WTException {
        try {
            workable = (Workable) PersistenceHelper.manager.refresh(workable);
            if (WorkInProgressHelper.isCheckedOut(workable)) {
                WorkInProgressHelper.service.checkin(workable, description);
            }
        } catch (WTPropertyVetoException e) {
            LOGGER.error(e.getMessage());
        }
        return workable;
    }

    /**
     * 升版本
     *
     * @param versioned
     * @return
     * @throws WTException
     */
    public static Versioned newVersion(Versioned versioned) throws WTException {
        Versioned newVersion = null;
        try {
            newVersion = VersionControlHelper.service.newVersion(versioned);
            newVersion = (Versioned) PersistenceHelper.manager.store(newVersion);
        } catch (WTPropertyVetoException e) {
            LOGGER.error(e.getMessage());
        }
        return newVersion;
    }

    /**
     * 执行查询
     *
     * @param querySpec
     * @return
     * @throws WTException
     */
    public static QueryResult find(QuerySpec querySpec) throws WTException {
        QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
        return queryResult;
    }

    /**
     * 获取对象URL
     *
     * @return: String
     * @param obj
     * @return
     * @throws WTException
     */
    public static String searchURL(Object obj) throws WTException {
        String result = null;
        if (obj instanceof Persistable) {
            ReferenceFactory referenceFactory = new ReferenceFactory();
            String oid = referenceFactory.getReferenceString((Persistable) obj);
            URLFactory urlFactory = new URLFactory();
            HashMap<String, String> hashmap = new HashMap<String, String>();
            hashmap.put("oid", oid);
            String url = NmActionServiceHelper.service.getAction("object", "view").getUrl();
            result = urlFactory.getHREF(url, hashmap, true);
        }
        if (result == null) {
            result = StringUtil.EMPTY;
        }
        return result;
    }

    /**
     * 获取首页url
     *
     * @param commandBean
     * @return
     * @throws WTException
     */
    public static String getHomePageUrl(NmCommandBean commandBean) throws WTException {
        String result = null;
        NmURL url = new NmURL();
        url.setType("homepage");
        result = url.toString2(commandBean.getUrlFactoryBean());
        return result;
    }
}
