package ext.casc.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContained;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.OrderByExpression;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import ext.casc.product.model.Batch;
import ext.casc.util.DBUtil;
import ext.casc.util.WTUtil;

public class CSCProduct {
	/*
	 * Answer an Vector of PDMLinkProduct by Searching Product Name
	 * @param prdName - the String object used as search criteria in the retrieval of PDMLinkProduct
	 * @return Vector
	 * @excption
	 */
	public static Vector getProductByName(String prdName){
		Vector vProduct = new Vector();
		try{
			PDMLinkProduct product = null;
			QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
			SearchCondition sc = new
					SearchCondition(PDMLinkProduct.class, PDMLinkProduct.NAME, SearchCondition.LIKE,prdName,false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()){
				product = (PDMLinkProduct)qr.nextElement();
				vProduct.add(product);
			}
		}catch(Exception ex){
			vProduct = null;
//			System.out.println("Exception Message = " + ex.getMessage()); //Debug
		}
		return vProduct;
	}

	/*
	 * Answer a PDMLinkProduct by Searching Product Name
	 * @param prdName - the String object used as search criteria in the retrieval of PDMLinkProduct
	 * @return PDMLink Product
	 * @excption
	 */
	public static PDMLinkProduct getPDMLinkProduct(String prdName){
		//Vector vProduct = new Vector();
		try{
			PDMLinkProduct product = null;
			QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
			SearchCondition sc = new
					SearchCondition(PDMLinkProduct.class, PDMLinkProduct.NAME, SearchCondition.EQUAL,prdName,false);
			qs.appendSearchCondition(sc);

	        ClassAttribute clsAttr = new ClassAttribute(PDMLinkProduct.class,PDMLinkProduct.MODIFY_TIMESTAMP);
	        OrderBy order = new OrderBy((OrderByExpression)clsAttr,true);
	        qs.appendOrderBy(order);

			QueryResult qr = PersistenceHelper.manager.find(qs);

			if (qr.hasMoreElements()){
				product = (PDMLinkProduct)qr.nextElement();
				return product;
			}
		}catch(Exception ex){
			//vProduct = null;
//			System.out.println("Exception Message = " + ex.getMessage()); //Debug
		}
		return null;
	}

	public static ArrayList getProductRolePrincipalMap(String productName,String role){
		PDMLinkProduct product = getPDMLinkProduct(productName);
		if(product == null)
			return null;
		return ext.casc.util.CSCPrincipal.getPrinciaplByRole(product, role);
	}

	public static PDMLinkProduct getProductByOid(String oid) throws WTException {
		PDMLinkProduct product = null;
		QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
		long longId = Long.valueOf(oid);
		int[] index = { 0 };
		SearchCondition sc = new SearchCondition(PDMLinkProduct.class, "thePersistInfo.theObjectIdentifier.id", SearchCondition.EQUAL,longId);
		qs.appendWhere(sc,index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec)qs);
		while (qr.hasMoreElements()){
			product = (PDMLinkProduct)qr.nextElement();
		}
		return product;
	}

	public static String getBatchesByPartNumber(String number) {
		StringBuffer result = new StringBuffer();
		try {
			WTPart part = WTUtil.findPart(number, null);
			if(part == null) {
				return result.toString();
			}
			WTContained contained = part.getContainer();
			String productOid = String.valueOf(PersistenceHelper.getObjectIdentifier(contained).getId());
			List<Batch> list = DBUtil.getBatchesByProduct(productOid, contained.getContainerName());

			for (Batch batch : list) {
				if(result.toString() == null || "".equals(result.toString())) {
					result.append("batchName=").append(batch.getName());
				} else {
					result.append("|batchName=").append(batch.getName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}
}
