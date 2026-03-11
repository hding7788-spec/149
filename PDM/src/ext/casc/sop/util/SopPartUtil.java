package ext.casc.sop.util;

import com.glaway.mpm.util.TypeUtil;
import com.glaway.mpm.util.WTContainerUtil;
import com.ptc.windchill.mpml.resource.MPMTooling;
import ext.casc.sop.constants.SopConstants;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContainer;
import wt.part.WTPart;
import wt.query.*;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SopPartUtil {


    /** 工艺视图 */
    public final static String VIEW_M = "Manufacturing";
    /** 设计视图 */
    public final static String VIEW_D = "Design";
    /** 布尔值true */
    private static final boolean TRUE = true;
    /** 布尔值false */
    private static final boolean FALSE = false;

    /**
     * 根据软属性查询部件
     *
     * @param containerName 精确查询
     * @param number
     * @param name
     * @param viewName 精确查询
     * @param ibaMap
     * @param isEqual true : 精确查询；false : 模糊查询
     * @return
     * @throws WTException
     */
    public static List<WTPart> searchLatestPartList(String containerName, String number, String name, String viewName, Map<String, String> ibaMap,
                                                    boolean isEqual) throws WTException {
        List<WTPart> partList = new ArrayList<WTPart>();
        try {
            QuerySpec querySpec = new QuerySpec();
            querySpec.setAdvancedQueryEnabled(TRUE);
            querySpec.appendClassList(WTPart.class, TRUE);
            if (!StringUtil.isEmpty(number)) {
                QueryUtil.appendWhere(querySpec, WTPart.class, WTPart.NUMBER, TRUE, isEqual, number);
            }
            if (!StringUtil.isEmpty(name)) {
                QueryUtil.appendWhere(querySpec, WTPart.class, WTPart.NAME, TRUE, isEqual, name);
            }
            if (!StringUtil.isEmpty(viewName)) {
                long viewId = -1;
                View view = ViewHelper.service.getView(viewName);
                if (view != null) {
                    viewId = view.getPersistInfo().getObjectIdentifier().getId();
                    QueryUtil.appendWhere(querySpec, WTPart.class, QueryConstants.VIEWID, TRUE, TRUE, viewId);
                }
            }
            if (!StringUtil.isEmpty(containerName)) {
                WTContainer container = WTContainerUtil.getContainerByName(containerName);
                if (container != null) {
                    long containerId = PersistenceHelper.getObjectIdentifier(container).getId();
                    QueryUtil.appendWhere(querySpec, WTPart.class, QueryConstants.CONTAINER_ID, TRUE, TRUE, containerId);
                }
            }
            // 根据软属性查询
            if (ibaMap != null && ibaMap.size() != 0) {
                ClassAttribute classAttribute = new ClassAttribute(WTPart.class, QueryConstants.OID);
                for (String ibaName : ibaMap.keySet()) {
                    if (!StringUtil.isEmpty(ibaMap.get(ibaName))) {
                        SubSelectExpression subSelectExpression = QueryUtil.getSubSelectExpression(ibaName, ibaMap.get(ibaName), isEqual);
                        if (querySpec.getConditionCount() != 0) {
                            querySpec.appendAnd();
                        }
                        querySpec.appendWhere(new SearchCondition(classAttribute, SearchCondition.IN, subSelectExpression), new int[] { 0 });
                    }
                }
            }
            querySpec = new LatestConfigSpec().appendSearchCriteria(querySpec);
            QueryResult queryResult = QueryUtil.find(querySpec);
            WTPart part = null;
            while (queryResult.hasMoreElements()) {
                Object[] obj = (Object[]) queryResult.nextElement();
                part = (WTPart) obj[0];
                partList.add(part);
            }
        } catch (QueryException e) {
            throw new WTException(e);
        }
        return partList;
    }

    /**
     * 根据软属性查询部件
     *
     * @param containerName 精确查询
     * @param number
     * @param name
     * @param viewName 精确查询
     * @param ibaMap
     * @param isEqual true : 精确查询；false : 模糊查询
     * @return
     * @throws WTException
     */
    public static List<WTPart> searchLatestPartList(String containerName, String number, String name, String viewName, Map<String, String> ibaMap,
                                                    boolean isEqual,String type) throws WTException {
        List<WTPart> partList = new ArrayList<WTPart>();
        try {
            QuerySpec querySpec = new QuerySpec();
            querySpec.setAdvancedQueryEnabled(TRUE);
            querySpec.appendClassList(WTPart.class, TRUE);
            if(!StringUtil.isEmpty(type)){
                TypeUtil.getTypeQuery(MPMTooling.class, type, querySpec);
            }
            if (!StringUtil.isEmpty(number)) {
                QueryUtil.appendWhere(querySpec, WTPart.class, WTPart.NUMBER, TRUE, isEqual, number);
            }
            if (!StringUtil.isEmpty(name)) {
                QueryUtil.appendWhere(querySpec, WTPart.class, WTPart.NAME, TRUE, isEqual, name);
            }
            if (!StringUtil.isEmpty(viewName)) {
                long viewId = -1;
                View view = ViewHelper.service.getView(viewName);
                if (view != null) {
                    viewId = view.getPersistInfo().getObjectIdentifier().getId();
                    QueryUtil.appendWhere(querySpec, WTPart.class, QueryConstants.VIEWID, TRUE, TRUE, viewId);
                }
            }
            if (!StringUtil.isEmpty(containerName)) {
                WTContainer container = WTContainerUtil.getContainerByName(containerName);
                if (container != null) {
                    long containerId = PersistenceHelper.getObjectIdentifier(container).getId();
                    QueryUtil.appendWhere(querySpec, WTPart.class, QueryConstants.CONTAINER_ID, TRUE, TRUE, containerId);
                }
            }
            // 根据软属性查询
            if (ibaMap != null && ibaMap.size() != 0) {
                ClassAttribute classAttribute = new ClassAttribute(WTPart.class, QueryConstants.OID);
                for (String ibaName : ibaMap.keySet()) {
                    if (!StringUtil.isEmpty(ibaMap.get(ibaName))) {
                        SubSelectExpression subSelectExpression = QueryUtil.getSubSelectExpression(ibaName, ibaMap.get(ibaName), isEqual);
                        if (querySpec.getConditionCount() != 0) {
                            querySpec.appendAnd();
                        }
                        querySpec.appendWhere(new SearchCondition(classAttribute, SearchCondition.IN, subSelectExpression), new int[] { 0 });
                    }
                }
            }
            querySpec = new LatestConfigSpec().appendSearchCriteria(querySpec);
            QueryResult queryResult = QueryUtil.find(querySpec);
            WTPart part = null;
            while (queryResult.hasMoreElements()) {
                Object[] obj = (Object[]) queryResult.nextElement();
                part = (WTPart) obj[0];
                partList.add(part);
            }
        } catch (QueryException e) {
            throw new WTException(e);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return partList;
    }
}
