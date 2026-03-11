package ext.casc.util;

import wt.content.*;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;

import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 该工具类封装了对于WTDocument的一系列底层操作，包括有创建，简单查询操作
 * 以及创建关联，查询关联，删除关联等操作，同时也包含了一些对于Content的操作
 * @author mliu
 *
 */
public class DocUtil implements RemoteAccess, Serializable{
    public static String QUERY_TYPE ="QUERY_TYPE";
    public static String QUERY_LIB_NAME ="QUERY_LIB_NAME";
    public static String QUERY_CONTAINER_ID ="QUERY_CONTAINER_ID";
    public static String QUERY_PRO_NAME ="QUERY_PRO_NAME";
    /**
     * 通过编号查找文档
     * @param number    查询文档编号条件
     * @param accessControlled  是否受到权限制约
     * @return  返回最新大版本的最新小版本文档
     */
    public static WTDocument getDoc(String number, boolean accessControlled) {
        try {
            number = number.toUpperCase();

            if (!RemoteMethodServer.ServerFlag) {
                return (WTDocument) RemoteMethodServer.getDefault().invoke("getDoc", DocUtil.class.getName(), null,
                        new Class[] {String.class, boolean.class},
                        new Object[] {number, accessControlled});
            } else {
                WTDocument doc = null;

                boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(accessControlled);
                try {
                    QuerySpec spec = new QuerySpec(WTDocument.class);
                    spec.appendWhere(
                            new SearchCondition(WTDocument.class,
                            WTDocument.NUMBER, SearchCondition.EQUAL, number), new int[] { 0 });

                    QueryResult qr = PersistenceHelper.manager.find(spec);
                    if (qr.hasMoreElements()){
                        WTDocument document = (WTDocument)qr.nextElement();
                        QueryResult qr2 = VersionControlHelper.service.allIterationsOf(document.getMaster());
                        if(qr2.hasMoreElements()){
                            doc = (WTDocument)qr2.nextElement();
                        }
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                } finally {
                    SessionServerHelper.manager.setAccessEnforced(enforce);
                }

                return doc;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<WTDocument> getAllGongyiWenJian(String type) throws RemoteException, WTException{
    	List<WTDocument> list = new ArrayList<WTDocument>();
    	//wt.part.WTPart|casc.sast.GLCatalogItemPart
    	TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference(type);
		long typeId = 0;
		if (tdr != null) {
			typeId = tdr.getKey().getBranchId();
		}
		QuerySpec qs = new QuerySpec(WTDocument.class);
		qs = new LatestConfigSpec().appendSearchCriteria(qs);
		qs.appendAnd();
	    qs.appendWhere(new SearchCondition(WTDocument.class,
	    "typeDefinitionReference.key.branchId", SearchCondition.EQUAL, typeId),
	     new int[]{0});
	    QueryResult qr = PersistenceHelper.manager.find(qs);
	    while(qr.hasMoreElements()){
	    	WTDocument doc = (WTDocument) qr.nextElement();
	    	list.add(doc);
	    }

	    return list;
    }

    public static WTDocument setSecondaryForDocument(WTDocument document, String fileName, InputStream inputStream)
			throws WTException, PropertyVetoException, FileNotFoundException, IOException {
		Transaction trans = new Transaction();
		trans.start();
		document = (WTDocument) PersistenceHelper.manager.refresh(document);
		 QueryResult qr2 = ContentHelper.service.getContentsByRole(document, ContentRoleType.SECONDARY);
         while (qr2.hasMoreElements()) {
             ApplicationData applicationdata = (ApplicationData) qr2.nextElement();
             String name = applicationdata.getFileName();
             if(fileName.equals(name)){
            	 PersistenceHelper.manager.delete(applicationdata);
     			 PersistenceServerHelper.manager.update(document);
             }

         }

        ApplicationData	appData = ApplicationData.newApplicationData(document);
		appData.setRole(ContentRoleType.SECONDARY);
		appData.setFileName(fileName);

		appData = ContentServerHelper.service.updateContent((ContentHolder) document, appData, inputStream); // 更新内容
		PersistenceServerHelper.manager.update(document);
		document = (WTDocument) ContentServerHelper.service.updateHolderFormat((FormatContentHolder) document); // 更新格式
		if (null != inputStream) {
			inputStream.close();
		}
		trans.commit();
		return document;
	}

    public static List<WTDocument> getLastestDocs(Map<String,String> params,Map<String,String> ibaMap) throws RemoteException, WTException{
        List<WTDocument> list = new ArrayList<WTDocument>();
        QuerySpec qs = new QuerySpec();
        qs.setAdvancedQueryEnabled(true);
        int ibaHolderIndex = qs.appendClassList(WTDocument.class, true);
        SearchCondition scLatestIteration = new SearchCondition(WTDocument.class, WTAttributeNameIfc.LATEST_ITERATION,
                SearchCondition.IS_TRUE);
        qs.appendWhere(scLatestIteration, ibaHolderIndex);

        if(!Tools.isNull(params.get(QUERY_TYPE)) ){
            TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference(String.valueOf(params.get(QUERY_TYPE)));
            long typeId = 0;
            if (tdr != null) {
                typeId = tdr.getKey().getBranchId();
            }

            if(qs.getConditionCount()>0){
                qs.appendAnd();
            }
            qs.appendWhere(new SearchCondition(WTDocument.class,"typeDefinitionReference.key.branchId", SearchCondition.EQUAL, typeId), new int[]{ibaHolderIndex});
        }
        if(!Tools.isNull(params.get(QUERY_CONTAINER_ID)) ){
            if(qs.getConditionCount()>0){
                qs.appendAnd();
            }
            qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.CONTAINER_ID, SearchCondition.EQUAL,Long.parseLong(params.get(QUERY_CONTAINER_ID))), new int[] {ibaHolderIndex});
        }


        for (String ibaname : ibaMap.keySet()) {
            if(ibaMap.get(ibaname) != null && !ibaMap.get(ibaname).equals("")){
                qs.appendAnd();
                ExtQuerySpec.appendIBACondition(qs,ibaHolderIndex,ibaname,ibaMap.get(ibaname),SearchCondition.EQUAL);
            }
        }

        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        while(qr.hasMoreElements()){
            Object[] obj = (Object[]) qr.nextElement();
            WTDocument document = (WTDocument)obj[0];
            list.add(document);
        }
        return list;
    }

    public static List<WTDocument> getLastestDocs(Map<String,String> params) throws RemoteException, WTException{
        List<WTDocument> list = new ArrayList<WTDocument>();
        QuerySpec qs = new QuerySpec();
        qs.setAdvancedQueryEnabled(true);
        int ibaHolderIndex = qs.appendClassList(WTDocument.class, true);

        SearchCondition scLatestIteration = new SearchCondition(WTDocument.class, WTAttributeNameIfc.LATEST_ITERATION,
                SearchCondition.IS_TRUE);
        qs.appendWhere(scLatestIteration, ibaHolderIndex);

        if(!Tools.isNull(params.get(QUERY_TYPE)) ){
            TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference(String.valueOf(params.get(QUERY_TYPE)));
            long typeId = 0;
            if (tdr != null) {
                typeId = tdr.getKey().getBranchId();
            }

            if(qs.getConditionCount()>0){
                qs.appendAnd();
            }
            qs.appendWhere(new SearchCondition(WTDocument.class,"typeDefinitionReference.key.branchId", SearchCondition.EQUAL, typeId), new int[]{ibaHolderIndex});
        }
        if(!Tools.isNull(params.get(QUERY_CONTAINER_ID)) ){
            if(qs.getConditionCount()>0){
                qs.appendAnd();
            }
            qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.CONTAINER_ID, SearchCondition.EQUAL,Long.parseLong(params.get(QUERY_CONTAINER_ID))), new int[] {ibaHolderIndex});
        }


        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        while(qr.hasMoreElements()){
        	Object[] obj = (Object[]) qr.nextElement();
            WTDocument document = (WTDocument)obj[0];
            list.add(document);
        }
        return list;
    }
}
