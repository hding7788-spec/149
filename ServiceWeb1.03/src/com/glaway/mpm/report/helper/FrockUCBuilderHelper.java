package com.glaway.mpm.report.helper;

import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wt.associativity.NCServerHolder;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartUsageLink;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.query.ClassAttribute;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.config.LatestConfigSpec;

import com.glaway.mpm.mpmresource.Constants;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.MPMProcessPlanUtil;
import com.glaway.mpm.util.TypeUtil;
import com.glaway.mpm.util.Util;
import com.glaway.mpm.util.WTContainerUtil;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.MPMProcessPlanHelper;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationHolder;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationMaster;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationToConsumableLink;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink;
import com.ptc.windchill.mpml.processplan.sequence.MPMSequence;
import com.ptc.windchill.mpml.resource.MPMTooling;



public class FrockUCBuilderHelper {

	public static List startSearch(String productNumber){
		List list = new ArrayList();
		List<MPMTooling> frockList = new ArrayList();
		try {
			PDMLinkProduct product = WTContainerUtil.getProductByName(productNumber);
			QueryResult qr = searchMPMProcessPlan(product);
			System.out.println("plan--qr---" + qr.size());
			if(qr != null){
				QueryResult opeResult = null;
				while(qr.hasMoreElements()){
					Object obj = qr.nextElement();
					if(obj instanceof MPMProcessPlan){
						MPMProcessPlan plan = (MPMProcessPlan)obj;
						//查找工艺下的操作
						opeResult = getOpetToPlan(plan);
						System.out.println("operation--qr---" + opeResult.size());
						while(opeResult.hasMoreElements()){
							Object perObj = opeResult.nextElement();
							if(perObj instanceof MPMOperation){
								//查找操作下的所有操作
								MPMOperation operation = (MPMOperation)perObj;
								List<MPMOperation> operList = getOperation(operation);
								System.out.println("operation--list---" + operList.size());
								//查找操作下的工装
								QueryResult frockQr =  getFrock(operation);
								MPMTooling frock = null;
								while(frockQr.hasMoreElements()){
									frock = (MPMTooling)frockQr.nextElement();
									frockList.add(frock);
								}
								for(MPMOperation oper:operList){
									QueryResult subopeforkqr = getFrock(oper);
									while(subopeforkqr.hasMoreElements()){
										MPMTooling subfrock = (MPMTooling)subopeforkqr.nextElement();
										if( frock != null && frock.equals(subfrock)){
											continue;
										}
										frockList.add(subfrock);
									}
								}
							}
						}

					}//end if
				}//end while
			}// end if
			System.out.println("frockList---" + frockList.size());
			for(MPMTooling forck:frockList){
				Map map = new HashMap();
				List<WTPart> partList = getPartFFBySql(forck);
				IBAHelper iba = new IBAHelper();
				String forckName = forck.getName();
				String forckNumber = forck.getNumber();
				String stock = iba.getIBAValue(forck, "stock");
				String partName = "";
				String partNumber = "";
				for(WTPart part:partList){
					partName = part.getName() + ";" + partName;
					partNumber = part.getNumber()  + ";" + partNumber;
				}
				partName = partName.endsWith(";")?partName.substring(0, partName.length()-1):partName;
				partNumber = partNumber.endsWith(";")?partNumber.substring(0, partNumber.length()-1):partNumber;
				map.put("proNumber", productNumber);
				map.put("frockNum", forckNumber);
				map.put("frockName", forckName);
				map.put("stockAmount", stock);
				map.put("hardwareNumber", partNumber);
				map.put("hardwarePartName", partName);
				map.put("workTime", "");
				map.put("materiRation", "");
				map.put("signalPrice", "");
				map.put("totalPrice", "");
				list.add(map);
			}


		} catch (WTException e) {
			e.printStackTrace();
			return list;
		}
		System.out.println("----" + list.size());
		return list;
	}


	/**
	 * 查找产品下所有工艺
	 * @author liu
	 * @date 2013-7-31
	 * @param productNumber
	 * @return
	 */
	public static QueryResult searchMPMProcessPlan(PDMLinkProduct product){
		QueryResult qr = new QueryResult();
		if(product == null){
			return qr;
		}
		long id = PersistenceHelper.getObjectIdentifier(product).getId();
		try {
			QuerySpec qs = new QuerySpec(MPMProcessPlan.class);
			qs.appendWhere(new SearchCondition(MPMProcessPlan.class,MPMProcessPlan.CONTAINER_ID,SearchCondition.EQUAL,id),new int[]{0});
			qr = PersistenceHelper.manager.find(qs);
			LatestConfigSpec lcs = new LatestConfigSpec();
			qr = lcs.process(qr);
		} catch (QueryException e) {
			e.printStackTrace();
			return qr;
		} catch (WTException e) {
			e.printStackTrace();
			return qr;
		}
		return qr;
	}

	/**
	 * 查询工艺下的工序、工步
	 * 操作-MPMOperation 工步-SubOperation 工序-Operation
	 * @author qianlong
	 * @date 2013-4-8
	 * @param parentPart
	 * @param childPartMaster
	 * @return
	 * @throws WTException
	 *
	 */
	public static QueryResult getOpetToPlan(MPMProcessPlan plan){

		QueryResult qr = new QueryResult();
		try {
			QuerySpec qs = new QuerySpec(MPMOperation.class);
			qs.setAdvancedQueryEnabled(true);
			SubSelectExpression	subSelectExpression = getChildOperMasterQuery(plan);
			ClassAttribute masterId = new ClassAttribute(MPMOperation.class, "masterReference.key.id");
			qs.appendWhere(new SearchCondition(masterId, SearchCondition.IN, subSelectExpression), new int[]{0});
//			System.out.println("plan-----" + qs);
			qr = PersistenceHelper.manager.find((StatementSpec)qs);
			qr = new LatestConfigSpec().process(qr);
		} catch (QueryException e) {
			e.printStackTrace();
			return qr;
		} catch (WTException e) {
			e.printStackTrace();
			return qr;
		}
		return qr;
	}


	/**
	 * 获取工艺计划关联的零件对象
	 *
	 * @author qianlong
	 * @date 2013-7-25
	 * @param processPlan
	 * @return
	 * @throws WTException
	 */
	public static WTPart getMPMProcessplanRelatedPart(MPMProcessPlan processPlan) throws WTException {
		WTPart part = null;
		QueryResult qr = MPMProcessPlanHelper.service.getWTParts(processPlan, NCServerHolder.makeForLatestConfigSpec());
		if (qr.hasMoreElements()) {
			part = (WTPart) qr.nextElement();
		}
		return part;
	}

	/**
	 * 获取子操作
	 *
	 * @author qianlong
	 * @date 2012-12-28
	 * @param operation
	 * @return
	 * @throws WTException
	 *
	 */
	public static QueryResult getChildMPMOperation(MPMOperationHolder holder) throws WTException {

		return MPMProcessPlanHelper.service.getOperationsFromOperationHolder(holder, NCServerHolder
				.makeForLatestConfigSpec(), false);
	}


	/**
	 * 获得操作的子阶操作
	 * @author qianlong
	 * @date 2013-7-31
	 * @param operation
	 * @return
	 */
	public static QueryResult getChildMPMOperation(MPMOperation operation){
		QueryResult qr = new QueryResult();
		try {
			QuerySpec qs = new QuerySpec(MPMOperation.class);
			qs.setAdvancedQueryEnabled(true);
			SubSelectExpression subSelectExpression = getChildOperMasterQuery(operation);
			ClassAttribute masteId = new ClassAttribute(MPMOperation.class,"masterReference.key.id");
			qs.appendWhere(new SearchCondition(masteId, SearchCondition.IN, subSelectExpression),new int[]{0});
//			System.out.println("child++++++qs=------" + qs);
			qr = PersistenceHelper.manager.find((StatementSpec)qs);
			qr = new LatestConfigSpec().process(qr);
		} catch (QueryException e) {
			e.printStackTrace();
			return qr;
		} catch (WTException e) {
			e.printStackTrace();
			return qr;
		}
		return qr;
	}


	/**
	 * 获得操作下的工装
	 * @author qianlong
	 * @date 2013-7-31
	 * @param operation
	 * @return
	 */
	public static QueryResult getFrock(MPMOperation operation){
		QueryResult qr = new QueryResult();
		try {
			QuerySpec qs = new QuerySpec(MPMTooling.class);
			qs.setAdvancedQueryEnabled(true);
			SubSelectExpression subSelectExpression = getfrockMasterQuery(operation);
			ClassAttribute masteId = new ClassAttribute(MPMTooling.class,"masterReference.key.id");
			qs.appendWhere(new SearchCondition(masteId, SearchCondition.IN, subSelectExpression),new int[]{0});
			qs.appendAnd();
			TypeUtil.getTypeQuery(MPMTooling.class, "com.nriet.Frock", qs);
//			System.out.println("frock-----" + qs);
			qr = PersistenceHelper.manager.find((StatementSpec)qs);
			qr = new LatestConfigSpec().process(qr);
		} catch (QueryException e) {
			e.printStackTrace();
			return qr;
		} catch (WTException e) {
			e.printStackTrace();
			return qr;
		} catch (RemoteException e) {
			e.printStackTrace();
			return qr;
		}
		return qr;
	}


	/**
	 * 递归查找所有操作子阶
	 * @author qianlong
	 * @date 2013-7-31
	 * @return
	 */
	public static List getOperation(MPMOperation operation){
		List list = new ArrayList();
		QueryResult qr = getChildMPMOperation(operation);
		while(qr.hasMoreElements()){
			Object obj = qr.nextElement();
			if(obj instanceof MPMOperation){
				MPMOperation childOper = (MPMOperation) obj;
				getOperation(childOper);
				list.add(childOper);
			}
		}
		return list;
	}


	/**
	 *创建工艺和操作之间，操作和操作之间的link
	 *
	 * @author qianlong
	 * @date 2012-10-31
	 * @param operationHolder
	 * @param operationMaster
	 * @param lable
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws Exception
	 *
	 */
	public static void createMPMOperationUsageLink(

	MPMOperationHolder operationHolder, MPMOperationMaster operationMaster, String lable) throws WTException,
			WTPropertyVetoException {
		MPMOperationUsageLink mpmOperationUsageLink = MPMOperationUsageLink.newMPMOperationUsageLink(operationHolder,
				operationMaster, lable);
		PersistenceServerHelper.manager.insert(mpmOperationUsageLink);

	}

	/**
	 * 查询操作的master的子查询语句
	 *
	 * @author qianlong
	 * @throws QueryException
	 * @date 2013-4-8
	 *
	 */
	private static SubSelectExpression getChildOperMasterQuery(MPMOperation operation) throws QueryException {
		QuerySpec qs = new QuerySpec();
		qs.appendClassList(MPMOperationUsageLink.class, false);
		qs.appendSelect(new ClassAttribute(MPMOperationUsageLink.class, "roleBObjectRef.key.id"), new int[]{0}, false);
		qs.appendWhere(new SearchCondition(MPMOperationUsageLink.class, "roleAObjectRef.key.id", SearchCondition.EQUAL, Util
				.getLongOid(operation)), new int[]{0});
//		System.out.println("ChildOperMasterQueryqs-----" + qs);
		return new SubSelectExpression(qs);
	}

	/**
	 * 查询工艺的master的子查询语句
	 *
	 * @author qianlong
	 * @throws QueryException
	 * @date 2013-4-8
	 *
	 */
	private static SubSelectExpression getChildOperMasterQuery(MPMProcessPlan plan) throws QueryException {
		QuerySpec qs = new QuerySpec();
		qs.appendClassList(MPMOperationUsageLink.class, false);
		qs.appendSelect(new ClassAttribute(MPMOperationUsageLink.class, "roleBObjectRef.key.id"), new int[]{0}, false);
		qs.appendWhere(new SearchCondition(MPMOperationUsageLink.class, "roleAObjectRef.key.id", SearchCondition.EQUAL, Util
				.getLongOid(plan)), new int[]{0});
//		System.out.println("qs-----" + qs);
		return new SubSelectExpression(qs);
	}

	private static SubSelectExpression getfrockMasterQuery(MPMOperation operation) throws QueryException {
		QuerySpec qs = new QuerySpec();
		qs.appendClassList(MPMOperationToConsumableLink.class, false);
		qs.appendSelect(new ClassAttribute(MPMOperationToConsumableLink.class, "roleBObjectRef.key.id"), new int[]{0}, false);
		qs.appendWhere(new SearchCondition(MPMOperationToConsumableLink.class, "roleAObjectRef.key.id", SearchCondition.EQUAL, Util
				.getLongOid(operation)), new int[]{0});
//		System.out.println("qs-----" + qs);
		return new SubSelectExpression(qs);
	}


	private static List getPartFFBySql(MPMTooling frock){
		List partList = new ArrayList();
//		String frockName = frock.getName();
//		System.out.println("frockName---" + frockName);
//		DBConn conn = null;
//		ResultSet rs = null;
//		try {
//			conn = new DBConn();
//
//			String sql = "SELECT A.WTPARTNUMBER as num FROM MPMPROCESSPLANMASTER A WHERE A.IDA2A2 IN" +
//			"(SELECT A0.IDA3MASTERREFERENCE FROM MPMPROCESSPLAN A0 WHERE A0.IDA2A2 IN" +
//			"(SELECT B0.IDA3A5 FROM MPMOPERATIONUSAGELINK B0 WHERE B0.IDA3B5 IN" +
//					"(SELECT A1.IDA3MASTERREFERENCE FROM MPMOPERATION A1 WHERE A1.IDA2A2 IN" +
//							"(SELECT A2.IDA3A5 FROM MPMOPERATIONTOCONSUMABLELINK A2 WHERE A2.IDA3B5 IN" +
//								"(SELECT A3.IDA2A2 FROM MPMToolingMASTER A3 WHERE A3.NAME='" +
//								frockName + "')))))";
//			String str = "SELECT A2.IDA3A5 as name FROM MPMOPERATIONTOCONSUMABLELINK A2 WHERE A2.IDA3B5 IN" +
//			"(SELECT A3.IDA2A2 FROM MPMToolingMASTER A3 WHERE A3.NAME='" + frockName + "')";
//			rs = conn.executeQuery(sql);
//			while(rs.next()){
//				String num = rs.getString("num");
//				System.out.println("num---" + num);
//				MPMProcessPlan plan = MPMProcessPlanUtil.getMPMProcessPlanByNumber(num);
//				WTPart part = getMPMProcessplanRelatedPart(plan);
//				partList.add(part);
//			}
//			rs.getStatement().close();
//			rs.close();
//			conn.close();
//			conn = null;
//		} catch (Exception e) {
//			System.out.println("----connect oracle wrong---");
//			e.printStackTrace();
//			return partList;
//		}
		return partList;
	}

}
