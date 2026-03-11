package ext.casc.purge;

import java.util.Iterator;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.workspaces.EPMWorkspace;
import wt.epm.workspaces.EPMWorkspaceHelper;
import wt.fc.ObjectReference;
import wt.fc.ObjectVector;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTSet;
import wt.inf.container.WTContained;
import wt.method.RemoteAccess;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pds.StatementSpec;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.TableColumn;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;

/**
 * 用于删除指定产品容器下的所有垃圾数据.包括：部件、成品、文档和CAD文档等.
 * 程序入口在产品的详细信息页面的操作菜单下的"删除作废档案"选项.
 * 只有站点管理员才有删除的权限.
 *
 * @author LongXiuChuan
 *
 */
public class DataPurger implements RemoteAccess {

    private static final String PRODUCT_ID = "containerReference.key.id";

    public static QueryResult resultForPurgePart(WTContained contained) {
        QueryResult partResult = null;
        try {
            partResult = getParts(contained);
        } catch (WTException e) {
            e.printStackTrace();
        }
        return partResult;
    }

    public static QueryResult resultForPurgeDocument(WTContained contained) {
        QueryResult docResult = null;
        try {
            docResult = getDocuments(contained);
        } catch (WTException e) {
            e.printStackTrace();
        }

        return docResult;
    }

    public static QueryResult resultForPurgeCAD(WTContained contained) {
        QueryResult cadResult = null;
        try {
            cadResult = getEPMDocuments(contained);
        } catch (WTException e) {
            e.printStackTrace();
        }
        return cadResult;
    }

    /**
     * 从工作区移除CAD文档和部件
     *
     * @param product
     *            产品容器
     * @throws WTException
     */
    public static QueryResult resultForPurgeEpmWorkspace(WTContained contained) throws WTException {
        QueryResult qResult = getEpmWorkspace(contained);
        ObjectVector oVector = new ObjectVector();
        while (qResult.hasMoreElements()) {
            EPMWorkspace workspace = (EPMWorkspace) qResult.nextElement();
            WTSet cadSet = EPMWorkspaceHelper.manager.getObjectsInWorkspace(workspace, EPMDocument.class);
            WTSet tempCadSet = new WTHashSet();
            Iterator cadSetIterator = cadSet.iterator();
            while (cadSetIterator.hasNext()) {
                EPMDocument epmDocument = (EPMDocument) ((ObjectReference) cadSetIterator.next()).getObject();
                String name = epmDocument.getName();
                if (name.endsWith("作废")||name.endsWith("（作废）")) {
                    tempCadSet.add(epmDocument);
                    oVector.addElement(epmDocument);
                }
            }
            WTSet partSet = EPMWorkspaceHelper.manager.getObjectsInWorkspace(workspace, WTPart.class);
            WTSet tempPartSet = new WTHashSet();
            Iterator partSetIterator = partSet.iterator();
            while (partSetIterator.hasNext()) {
                WTPart part = (WTPart) ((ObjectReference) partSetIterator.next()).getObject();
                String name = part.getName();
                if (name.endsWith("作废")||name.endsWith("（作废）")) {
                    tempPartSet.add(part);
                    oVector.addElement(part);
                }
            }
        }
        qResult = new QueryResult(oVector);
        return qResult;
    }

    public static QueryResult resultWorkspace(WTContained contained) throws WTException {
        QueryResult qResult = getEpmWorkspace(contained);
        return qResult;
    }

    /**
     * 通过指定产品容器查找其下的所有工作区对象
     *
     * @param product
     *            产品容器
     * @return QueryResult 工作区集合
     * @throws WTException
     */
    private static QueryResult getEpmWorkspace(WTContained contained) throws WTException {
        QuerySpec qSpec = new QuerySpec(EPMWorkspace.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(contained).getId();
        SearchCondition sCondition = new SearchCondition(EPMWorkspace.class, PRODUCT_ID, SearchCondition.EQUAL, longId);
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager
                .find((StatementSpec) qSpec);
        return qResult;
    }

    /**
     * 返回指定产品容器下的所有part
     *
     * @param product
     * @return List<WTPart> 包含所有零部件的list
     * @throws WTException
     */
    private static QueryResult getParts(WTContained contained) throws WTException {
        QuerySpec qSpec = new QuerySpec();
        long longId = PersistenceHelper.getObjectIdentifier(contained).getId();
        int index0 = qSpec.appendClassList(WTPart.class, true);
        int index1 = qSpec.addClassList(WTPartMaster.class, false);
        String[] aliases = new String[2];
        aliases[0] = qSpec.getFromClause().getAliasAt(index0);
        aliases[1] = qSpec.getFromClause().getAliasAt(index1);

        TableColumn tc0 = new TableColumn(aliases[0], "IDA3MASTERREFERENCE");
        TableColumn tc1 = new TableColumn(aliases[1], "IDA2A2");
        qSpec.appendWhere(new SearchCondition(tc0, "=", tc1), new int[] { index0, index1 });

        qSpec.appendAnd();
//        qSpec.appendOpenParen();
        qSpec.appendWhere(new SearchCondition(WTPart.class, PRODUCT_ID, SearchCondition.EQUAL, longId),
                new int[] { index0 });
//        qSpec.appendCloseParen();

        qSpec.appendAnd();
//        qSpec.appendOpenParen();

        qSpec.appendOpenParen();
        qSpec.appendWhere(new SearchCondition(WTPartMaster.class, WTPartMaster.NAME, SearchCondition.LIKE, "%作废%"),
                new int[] { index1 });
        // qSpec.appendOr();
        // qSpec.appendWhere(new SearchCondition(WTPartMaster.class, WTPartMaster.NAME, SearchCondition.LIKE, "%删除"),
        // new int[] { index1 });
        // qSpec.appendOr();
        // qSpec.appendWhere(new SearchCondition(WTPartMaster.class, WTPartMaster.NAME, SearchCondition.LIKE, "%垃圾"),
        // new int[] { index1 });
//        qSpec.appendCloseParen();

        qSpec.appendOr();

//        qSpec.appendOpenParen();
        qSpec.appendWhere(new SearchCondition(WTPartMaster.class, WTPartMaster.NUMBER, SearchCondition.LIKE, "%作废%"),
                new int[] { index1 });
        // qSpec.appendOr();
        // qSpec.appendWhere(new SearchCondition(WTPartMaster.class, WTPartMaster.NUMBER, SearchCondition.LIKE, "%删除"),
        // new int[] { index1 });
        // qSpec.appendOr();
        // qSpec.appendWhere(new SearchCondition(WTPartMaster.class, WTPartMaster.NUMBER, SearchCondition.LIKE, "%垃圾"),
        // new int[] { index1 });

        qSpec.appendCloseParen();

        //add by Li Chenglei,20121114 start,最新小版本,降序排序
        qSpec.appendAnd();
        qSpec.appendWhere(new SearchCondition(WTPart.class,WTAttributeNameIfc.LATEST_ITERATION,SearchCondition.IS_TRUE), new int[] { 0 });
        qSpec.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class,WTPart.MODIFY_TIMESTAMP), true), new int[] { 0 });
        //add by Li Chenglei,20121114 end

//        qSpec.appendCloseParen();

        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        ObjectVector ov = new ObjectVector();
        WTPart part = null;
        while (qResult.hasMoreElements()) {
            Persistable[] persistables = (Persistable[]) qResult.nextElement();
            part = (WTPart) persistables[0];
            ov.addElement(part);
        }
        qResult = new QueryResult(ov);
        return qResult;
    }

    /**
     * 返回指定产品容器下的所有document
     *
     * @modify by Li Chenglei,20121114
     * @param product
     * @return List<WTDocument> 包含所有文档的list
     * @throws WTException
     */
    private static QueryResult getDocuments(WTContained contained) throws WTException {
        QuerySpec qSpec = new QuerySpec();
        long longId = PersistenceHelper.getObjectIdentifier(contained).getId();
        int index0 = qSpec.appendClassList(WTDocument.class, true);
        int index1 = qSpec.addClassList(WTDocumentMaster.class, false);
        String[] aliases = new String[2];
        aliases[0] = qSpec.getFromClause().getAliasAt(index0);
        aliases[1] = qSpec.getFromClause().getAliasAt(index1);

        TableColumn tc0 = new TableColumn(aliases[0], "IDA3MASTERREFERENCE");
        TableColumn tc1 = new TableColumn(aliases[1], "IDA2A2");
        qSpec.appendWhere(new SearchCondition(tc0, "=", tc1), new int[] { index0, index1 });

        qSpec.appendAnd();
//        qSpec.appendOpenParen();
        qSpec.appendWhere(new SearchCondition(WTDocument.class, PRODUCT_ID, SearchCondition.EQUAL, longId),
                new int[] { index0 });
//        qSpec.appendCloseParen();

        qSpec.appendAnd();
//        qSpec.appendOpenParen();

        qSpec.appendOpenParen();
        qSpec.appendWhere(new SearchCondition(WTDocumentMaster.class, WTDocumentMaster.NAME, SearchCondition.LIKE,
                "%作废%"), new int[] { index1 });
        // qSpec.appendOr();
        // qSpec.appendWhere(new SearchCondition(WTDocumentMaster.class, WTDocumentMaster.NAME, SearchCondition.LIKE,
        // "%删除"), new int[] { index1 });
        // qSpec.appendOr();
        // qSpec.appendWhere(new SearchCondition(WTDocumentMaster.class, WTDocumentMaster.NAME, SearchCondition.LIKE,
        // "%垃圾"), new int[] { index1 });
//        qSpec.appendCloseParen();

        qSpec.appendOr();
//        qSpec.appendOpenParen();
        qSpec.appendWhere(new SearchCondition(WTDocumentMaster.class, WTDocumentMaster.NUMBER, SearchCondition.LIKE,
                "%作废%"), new int[] { index1 });
        // qSpec.appendOr();
        // qSpec.appendWhere(new SearchCondition(WTDocumentMaster.class, WTDocumentMaster.NUMBER, SearchCondition.LIKE,
        // "%删除"), new int[] { index1 });
        // qSpec.appendOr();
        // qSpec.appendWhere(new SearchCondition(WTDocumentMaster.class, WTDocumentMaster.NUMBER, SearchCondition.LIKE,
        // "%垃圾"), new int[] { index1 });

        qSpec.appendCloseParen();

        //add by Li Chenglei,20121114 start,最新小版本,降序排序
        qSpec.appendAnd();
        qSpec.appendWhere(new SearchCondition(WTDocument.class,WTAttributeNameIfc.LATEST_ITERATION,SearchCondition.IS_TRUE), new int[] { 0 });
        qSpec.appendOrderBy(new OrderBy(new ClassAttribute(WTDocument.class,WTDocument.MODIFY_TIMESTAMP), true), new int[] { 0 });
        //add by Li Chenglei,20121114 end


//        qSpec.appendCloseParen();

        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        ObjectVector ov = new ObjectVector();
        WTDocument doc = null;
        while (qResult.hasMoreElements()) {
            Persistable[] persistables = (Persistable[]) qResult.nextElement();
            doc = (WTDocument) persistables[0];
            ov.addElement(doc);
        }
        qResult = new QueryResult(ov);
        return qResult;
    }

    /**
     * 返回指定容器下的所有epmdocument
     *
     * @param contained
     * @return List<EPMDocument> 包含所有图档的list
     * @throws WTException
     */
    private static QueryResult getEPMDocuments(WTContained contained) throws WTException {
        QuerySpec qSpec = new QuerySpec();
        long longId = PersistenceHelper.getObjectIdentifier(contained).getId();
        int index0 = qSpec.appendClassList(EPMDocument.class, true);
        int index1 = qSpec.addClassList(EPMDocumentMaster.class, false);
        String[] aliases = new String[2];
        aliases[0] = qSpec.getFromClause().getAliasAt(index0);
        aliases[1] = qSpec.getFromClause().getAliasAt(index1);

        TableColumn tc0 = new TableColumn(aliases[0], "IDA3MASTERREFERENCE");
        TableColumn tc1 = new TableColumn(aliases[1], "IDA2A2");
        qSpec.appendWhere(new SearchCondition(tc0, "=", tc1), new int[] { index0, index1 });

        qSpec.appendAnd();
//        qSpec.appendOpenParen();
        qSpec.appendWhere(new SearchCondition(EPMDocument.class, PRODUCT_ID, SearchCondition.EQUAL, longId),
                new int[] { index0 });
//        qSpec.appendCloseParen();

        qSpec.appendAnd();
//        qSpec.appendOpenParen();

        qSpec.appendOpenParen();
        qSpec.appendWhere(new SearchCondition(EPMDocumentMaster.class, EPMDocumentMaster.NAME, SearchCondition.LIKE,
                "%作废%"), new int[] { index1 });
        // qSpec.appendOr();
        // qSpec.appendWhere(new SearchCondition(EPMDocumentMaster.class, EPMDocumentMaster.NAME, SearchCondition.LIKE,
        // "%删除"), new int[] { index1 });
        // qSpec.appendOr();
        // qSpec.appendWhere(new SearchCondition(EPMDocumentMaster.class, EPMDocumentMaster.NAME, SearchCondition.LIKE,
        // "%垃圾"), new int[] { index1 });

//        qSpec.appendCloseParen();

        qSpec.appendOr();

//        qSpec.appendOpenParen();
        qSpec.appendWhere(new SearchCondition(EPMDocumentMaster.class, EPMDocumentMaster.NUMBER, SearchCondition.LIKE,
                "%作废%"), new int[] { index1 });
        // qSpec.appendOr();
        // qSpec.appendWhere(new SearchCondition(EPMDocumentMaster.class, EPMDocumentMaster.NUMBER,
        // SearchCondition.LIKE,
        // "%删除"), new int[] { index1 });
        // qSpec.appendOr();
        // qSpec.appendWhere(new SearchCondition(EPMDocumentMaster.class, EPMDocumentMaster.NUMBER,
        // SearchCondition.LIKE,
        // "%垃圾"), new int[] { index1 });
        qSpec.appendCloseParen();

        //add by Li Chenglei,20121114 start,最新小版本,降序排序
        qSpec.appendAnd();
        qSpec.appendWhere(new SearchCondition(EPMDocument.class,WTAttributeNameIfc.LATEST_ITERATION,SearchCondition.IS_TRUE), new int[] { 0 });
        qSpec.appendOrderBy(new OrderBy(new ClassAttribute(EPMDocument.class,EPMDocument.MODIFY_TIMESTAMP), true), new int[] { 0 });
        //add by Li Chenglei,20121114 end

//        qSpec.appendCloseParen();

        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        ObjectVector ov = new ObjectVector();
        EPMDocument cadDoc = null;
        while (qResult.hasMoreElements()) {
            Persistable[] persistables = (Persistable[]) qResult.nextElement();
            cadDoc = (EPMDocument) persistables[0];
            ov.addElement(cadDoc);
        }
        qResult = new QueryResult(ov);
        return qResult;
    }

}
