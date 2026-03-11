package ext.casc.util;

import org.apache.commons.lang3.StringUtils;
import wt.fc.ObjectIdentifier;
import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.definition.StringDefinition;
import wt.iba.definition._AttributeHierarchyChild;
import wt.iba.value.StringValue;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pds.StatementSpec;
import wt.query.*;
import wt.tools.path.Search;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.views.ViewManageable;

public class ExtQuerySpec {
    private boolean partNoView = false;
    private QuerySpec querySpec = null;

    public ExtQuerySpec() throws WTPropertyVetoException, QueryException {
        QuerySpec querySpec = new QuerySpec();
        querySpec.setAdvancedQueryEnabled(true);
        querySpec.setDescendantQuery(false);
        this.querySpec = querySpec;
    }

    public ExtQuerySpec(QuerySpec querySpec) throws WTPropertyVetoException, QueryException {
        querySpec.setAdvancedQueryEnabled(true);
        querySpec.setDescendantQuery(false);
        this.querySpec = querySpec;
    }
    public int applendClassBack(Class clazz) throws QueryException {
        return querySpec.appendClassList(clazz,true);
    }

    public int applendClass(Class clazz) throws QueryException {
        return querySpec.appendClassList(clazz,false);
    }

    public static void appendIBACondition(QuerySpec qs,int classIndex,String ibaName,String ibaValue,String symbol) throws QueryException{
        int ibaStringValueIndex = qs.appendClassList(wt.iba.value.StringValue.class, false);
        int ibaStringDefinitionIndex = qs.appendClassList(StringDefinition.class, false);


        SearchCondition scJoinStringValueIBAHolder = new SearchCondition(wt.iba.value.StringValue.class,
                "theIBAHolderReference.key.id", qs.getClassAt(classIndex), WTAttributeNameIfc.ID_NAME);
        SearchCondition scJoinStringValueStringDefinition = new SearchCondition(wt.iba.value.StringValue.class,
                "definitionReference.key.id", StringDefinition.class, WTAttributeNameIfc.ID_NAME);

        qs.appendWhere(scJoinStringValueIBAHolder, ibaStringValueIndex, classIndex);
        qs.appendAnd();
        qs.appendWhere(scJoinStringValueStringDefinition, ibaStringValueIndex, ibaStringDefinitionIndex);

        SearchCondition scStringDefinitionName1 = new SearchCondition(StringDefinition.class, _AttributeHierarchyChild.NAME,
                SearchCondition.EQUAL, ibaName);

        SearchCondition scStringValueValue1 = new SearchCondition(wt.iba.value.StringValue.class, StringValue.VALUE,
                symbol, ibaValue);
        qs.appendAnd();
        qs.appendOpenParen();
        qs.appendWhere(scStringDefinitionName1, ibaStringDefinitionIndex);
        qs.appendAnd();
        qs.appendWhere(scStringValueValue1, ibaStringValueIndex);
        qs.appendCloseParen();

    }

    public boolean isPartNoView() {
        return partNoView;
    }

    public void setPartNoView(boolean partNoView) {
        this.partNoView = partNoView;
    }

    public QuerySpec getQuerySpec() {
        return querySpec;
    }

    public void setQuerySpec(QuerySpec querySpec) {
        this.querySpec = querySpec;
    }

    public void appendIda2a2AndIda3a5(Class clazz, int index, Class clazz2, int masterIndex) throws QueryException {
        appendConditionEqual(clazz,QuerySpaceContant.QUERY_SPEC_THEOBJECTIDENTIFIER_ID,index,clazz2,"roleAObjectRef.key.id",masterIndex);
    }

    private void appendConditionEqual(Class clazz, String column, int index, Class clazz2, String column2, int index2) throws QueryException {
        appendAnd();
        querySpec.appendWhere(new SearchCondition(clazz,column,clazz2,column2),new int[]{index,index2});
    }
    public void appendAnd() throws QueryException {
        if(null!=querySpec.getWhere()){
            querySpec.appendAnd();
        }
    }

    public void appendIda2a2AndIda3b5(Class clazz, int index, Class clazz2, int index2) throws QueryException {
        appendConditionEqual(clazz,QuerySpaceContant.QUERY_SPEC_THEOBJECTIDENTIFIER_ID,index,clazz2,"roleBObjectRef.key.id",index2);

    }

    public void appendWhereLatest(Class clazz, int index) throws QueryException {
        appendAnd();
        querySpec.appendWhere(new SearchCondition(clazz,"iterationInfo.latest",SearchCondition.IS_TRUE),new int[]{index});

    }

    public void appendView(int mIndex, String viewName) {
        if(StringUtils.isNotEmpty(viewName)){
            appendPartView(mIndex,viewName);
        }
    }
    public  void appendPartView(int partIndex,String viewName){
        try {
            View view = ViewHelper.service.getView(viewName);
            if(view!=null){
                if(this.querySpec.getConditionCount()>0){
                    this.querySpec.appendAnd();
                }
                ObjectIdentifier objId = PersistenceHelper.getObjectIdentifier(view);
                SearchCondition where = new SearchCondition(WTPart.class, ViewManageable.VIEW+"."+ ObjectReference.KEY,SearchCondition.EQUAL,objId);
                this.querySpec.appendWhere(where,new int[partIndex]);
            }
        } catch (WTException e) {
            e.printStackTrace();
        }
    }
    public  void appendPartView(QuerySpec spec,int partIndex,String viewName){
        try {
            View view = ViewHelper.service.getView(viewName);
            if(view!=null){
                if(spec.getConditionCount()>0){
                    spec.appendAnd();
                }
                ObjectIdentifier objId = PersistenceHelper.getObjectIdentifier(view);
                SearchCondition where = new SearchCondition(WTPart.class, ViewManageable.VIEW+"."+ ObjectReference.KEY,SearchCondition.EQUAL,objId);
                spec.appendWhere(where,new int[partIndex]);
            }
        } catch (WTException e) {
            e.printStackTrace();
        }
    }

    public void appendIda2a2AndMaster(Class clazz, int index, Class clazz2, int masterIndex) throws QueryException {
        appendConditionEqual(clazz,QuerySpaceContant.QUERY_SPEC_MASTERREFERENCE_KEY_ID,index,clazz2,QuerySpaceContant.QUERY_SPEC_THEOBJECTIDENTIFIER_ID,masterIndex);
    }

    public void maxVersion(Class clazz, int index, Class masterClazz, int masterIndex) throws QueryException, WTPropertyVetoException {
        maxVersion(clazz,index,masterClazz,masterIndex,"Design");
    }
    public void maxVersion(Class clazz, int index, Class masterClazz, int masterIndex,String viewName) throws QueryException, WTPropertyVetoException {
       QuerySpec maxVerSpec = maxVersionSortIda2VersionInfo(clazz,index,masterClazz,masterIndex,viewName);
       appendAnd();;
       querySpec.appendWhere(new SearchCondition(new ClassAttribute(clazz,QuerySpaceContant.QUERY_SPEC_VERSIONSORTID),SearchCondition.EQUAL,new SubSelectExpression(maxVerSpec)),new int[]{index});
    }

    private QuerySpec maxVersionSortIda2VersionInfo(Class clazz, int index, Class masterClazz, int masterIndex, String viewName) throws QueryException, WTPropertyVetoException {
        QuerySpec maxVerSpec = new QuerySpec();
        maxVerSpec.setAdvancedQueryEnabled(true);
        maxVerSpec.setDescendantQuery(false);
        maxVerSpec.getFromClause().setAliasPrefix("Max");
        int maxIndex = maxVerSpec.appendClassList(clazz,false);
        String alias1 = maxVerSpec.getFromClause().getAliasAt( maxIndex);
        TableExpression expression1 = maxVerSpec.getFromClause().getTableExpressionAt(maxIndex);
        SQLFunction sqlFunction = SQLFunction.newSQLFunction(SQLFunction.MAXIMUM,new ClassAttribute(clazz,QuerySpaceContant.QUERY_SPEC_VERSIONSORTID));
        maxVerSpec.appendSelect(sqlFunction,false);
        SearchCondition mCondition = new SearchCondition(clazz,QuerySpaceContant.QUERY_SPEC_MASTERREFERENCE_KEY_ID,masterClazz,QuerySpaceContant.QUERY_SPEC_THEOBJECTIDENTIFIER_ID);
        ClassTableExpression[] exps = new ClassTableExpression[2];
        String alias2 = querySpec.getFromClause().getAliasAt(masterIndex);
        TableExpression expression2 = querySpec.getFromClause().getTableExpressionAt(masterIndex);
        exps[0]= (ClassTableExpression) expression1;
        exps[1]= (ClassTableExpression) expression2;
        String[] alias = new String[2];
        alias[0] = alias1;
        alias[1] = alias2;
        maxVerSpec.appendWhere(mCondition,exps,alias);
        if(WTPart.class.getName().equals(clazz.getName())&&!partNoView){
            if(StringUtils.isEmpty(viewName)){
                viewName = "Design";
            }
            appendPartView(maxVerSpec,maxIndex,viewName);

        }
        return maxVerSpec;
    }

    public void appendConditionNotEqual(Class clazz, String column, int index, String value) throws QueryException {
        appendWhere(clazz,column,SearchCondition.NOT_EQUAL,index,value);
    }

    private void appendWhere(Class clazz, String column, String condition, int index, String value) throws QueryException {
        appendAnd();
        querySpec.appendWhere(new SearchCondition(clazz,column,condition,value),new int[index]);
    }

    public void appendWhereIn(Class clazz, String column, int index, Object[] array) throws QueryException {
        appendAnd();
        querySpec.appendWhere(new SearchCondition(new ClassAttribute(clazz,column),SearchCondition.IN,new ArrayExpression(array)),new int[]{index});

    }

    public QueryResult find() throws WTException {
        QueryResult qr = null;
        QuerySpec spec = getQuerySpec();
        if(spec !=null){
            qr = PersistenceHelper.manager.find((StatementSpec) spec);
        }else{
            qr = new QueryResult();
        }
        return qr;
    }
}
