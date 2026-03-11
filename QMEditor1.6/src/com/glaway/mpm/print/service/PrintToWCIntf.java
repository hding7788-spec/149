package com.glaway.mpm.print.service;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.glaway.mpm.print.PrintUserCodeProcessor;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.model.data.CmAttachment;
import com.glaway.mpm.model.data.CmBaseline;
import com.glaway.mpm.print.data.CmDistributionBean;
import com.glaway.mpm.print.data.CmImportBean;
import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.print.data.CmPrintQueryBean;
import com.glaway.mpm.print.data.CmPrintRecordInfoBean;
import com.glaway.mpm.print.data.CmPrintRecordQueryBean;
import com.glaway.mpm.print.data.CmSealBean;


/**
 *打印模块与服务器端交互类
 *
 */
public class PrintToWCIntf implements RemoteAccess {
	private static VaLogger logger = VaLogger.getLogger(PrintToWCIntf.class.getName());
	private static final String PRINT_SERVERNAME = "com.glaway.mpm.intf.PrintToWCIntfRMI";

	/**
	 * 调用PDM服务器端PrintToWCIntfRMI类提供的接口。
	 *
	 * @param mentodName 方法名
	 * @param classArray 参数类数组
	 * @param objectArray 参数对象数组
	 * @return 方法调用结果
	 * @throws RemoteException
	 * @throws InvocationTargetException
	 */
	public static Object remoteMethodInvoke(String mentodName,
			Class<?>[] classArray, Object[] objectArray) throws RemoteException,
			InvocationTargetException {
		RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
		Object object = null;
		try {
			object = methodServer.invoke(mentodName, PRINT_SERVERNAME, null, classArray, objectArray);
		} catch (Exception e) {
			logger.error(e);
		}
		return object;
	}
	public static void remoteMethodInvoke1(String mentodName,
			Class<?>[] classArray, Object[] objectArray) throws RemoteException,
			InvocationTargetException {
		RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
		try {
			methodServer.invoke(mentodName, PRINT_SERVERNAME, null, classArray, objectArray);
		} catch (Exception e) {
			logger.error(e);
		}
	}
//	//将历史信息导入数据库
//	@SuppressWarnings("unchecked")
//	public static void importInfoToDB(List<CmImportBean> list)
//			throws RemoteException, InvocationTargetException {
//		Class<?>[] cls = new Class[] {List.class};
//		Object[] objs = new Object[] {list};
//		remoteMethodInvoke1("importInfoToDB", cls, objs);
//	}


	//更改文件回收查询
	@SuppressWarnings("unchecked")
	public static List<CmPrintRecordInfoBean> queryFileInfoByChange(List<String> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (List<CmPrintRecordInfoBean>)remoteMethodInvoke("queryFileInfoByChange", cls, objs);
	}
	@SuppressWarnings("unchecked")
	public static Boolean insertChangeRecoverToDB(List<CmPrintRecordInfoBean> listBean)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {listBean};
		return (Boolean) remoteMethodInvoke("insertChangeRecoverToDB", cls, objs);
	}


	//查询重新打印文件
	@SuppressWarnings("unchecked")
	public static List<CmPrintRecordInfoBean> queryReprintInfo(CmPrintRecordQueryBean cmPrintRecordQueryBean)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {CmPrintRecordQueryBean.class};
		Object[] objs = new Object[] {cmPrintRecordQueryBean};
		return (List<CmPrintRecordInfoBean>)remoteMethodInvoke("queryReprintInfo", cls, objs);
	}
	//重新打印之后更新系统中的重新打印的信息
	@SuppressWarnings("unchecked")
	public static void updateReprintInfo(List<CmPrintRecordInfoBean> listBean, String time)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class, String.class};
		Object[] objs = new Object[] {listBean, time};
		remoteMethodInvoke1("updateReprintInfo", cls, objs);
	}
	//根据条码查看部门
	@SuppressWarnings("unchecked")
	public static CmPrintRecordInfoBean getInfoByBarCode(String barCode)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class};
		Object[] objs = new Object[] {barCode};
		return (CmPrintRecordInfoBean)remoteMethodInvoke("getInfoByBarCode", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static Boolean checkUserByStore(String userName, List<String> strList)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class, List.class};
		Object[] objs = new Object[] {userName, strList};
		return (Boolean)remoteMethodInvoke("checkUserByStore", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static Boolean checkUserByRecover(String userName, List<String> strList)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class, List.class};
		Object[] objs = new Object[] {userName, strList};
		return (Boolean)remoteMethodInvoke("checkUserByRecover", cls, objs);
	}

	//根据回收人查看回收信息
	@SuppressWarnings("unchecked")
	public static CmPrintRecordInfoBean getInfoByUserName(String userName)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class};
		Object[] objs = new Object[] {userName};
		return (CmPrintRecordInfoBean)remoteMethodInvoke("getInfoByUserName", cls, objs);
	}
	//查询遗失文件信息
	@SuppressWarnings("unchecked")
	public static List<CmPrintRecordInfoBean> queryLoseInfo(List<String> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (List<CmPrintRecordInfoBean>)remoteMethodInvoke("queryLoseInfo", cls, objs);
	}
	//修改遗失文件信息
	@SuppressWarnings("unchecked")
	public static Boolean saveUpdateLoseInfo(List<CmPrintRecordInfoBean> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (Boolean)remoteMethodInvoke("saveUpdateLoseInfo", cls, objs);
	}

	@SuppressWarnings("unchecked") //add by lkc 2017.12.28
	public static List<String> queryRecoverBarcode(String delayStatus)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class};
		Object[] objs = new Object[] {delayStatus};
		return (List<String>)remoteMethodInvoke("queryRecoverBarcode", cls, objs);
	}

	@SuppressWarnings("unchecked") //add by lkc 2017.12.28
	public static List<CmPrintRecordInfoBean> queryDelayInfo(CmPrintRecordInfoBean cmPrintRecordQueryBean, List<String> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {CmPrintRecordInfoBean.class, List.class};
		Object[] objs = new Object[] {cmPrintRecordQueryBean, list};
		return (List<CmPrintRecordInfoBean>)remoteMethodInvoke("queryDelayInfo", cls, objs);
	}
	//文件入库管理中查询已分发的文件信息
	@SuppressWarnings("unchecked") //add by lkc 2018.1.11
	public static List<CmPrintRecordInfoBean> queryDistributeInfo(CmPrintRecordQueryBean cmPrintRecordQueryBean, String category)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {CmPrintRecordQueryBean.class, String.class};
		Object[] objs = new Object[] {cmPrintRecordQueryBean, category};
		return (List<CmPrintRecordInfoBean>)remoteMethodInvoke("queryDistributeInfo", cls, objs);
	}

	//文件入库同步至现行库
	public static Boolean synchDangan(List<CmPrintRecordInfoBean> listBean, String category)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class, String.class};
		Object[] objs = new Object[] {listBean, category};
		return (Boolean)remoteMethodInvoke("synchDangan", cls, objs);
	}

	//厂内文件封存查看分发信息
	@SuppressWarnings("unchecked") //add by lkc 2018.1.11
	public static List<CmPrintRecordInfoBean> inFactoryQueryDistributeInfoOfStore(CmPrintRecordInfoBean cmPrintRecordQueryBean, String category)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {CmPrintRecordInfoBean.class, String.class};
		Object[] objs = new Object[] {cmPrintRecordQueryBean, category};
		return (List<CmPrintRecordInfoBean>)remoteMethodInvoke("inFactoryQueryDistributeInfoOfStore", cls, objs);
	}
	//外来文件封存查看分发信息
	@SuppressWarnings("unchecked") //add by lkc 2018.1.11
	public static List<CmPrintRecordInfoBean> outsideQueryDistributeInfoOfStore(CmPrintRecordInfoBean cmPrintRecordQueryBean)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {CmPrintRecordInfoBean.class};
		Object[] objs = new Object[] {cmPrintRecordQueryBean};
		return (List<CmPrintRecordInfoBean>)remoteMethodInvoke("outsideQueryDistributeInfoOfStore", cls, objs);
	}
	//厂内纸质文件封存查看分发信息
	@SuppressWarnings("unchecked") //add by lkc 2018.1.11
	public static List<CmPrintRecordInfoBean> queryPaperDistributeInfoOfStore(CmPrintRecordInfoBean cmPrintRecordQueryBean)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {CmPrintRecordInfoBean.class};
		Object[] objs = new Object[] {cmPrintRecordQueryBean};
		return (List<CmPrintRecordInfoBean>)remoteMethodInvoke("queryPaperDistributeInfoOfStore", cls, objs);
	}
	@SuppressWarnings("unchecked") //add by lkc 2018.1.11
	public static List<CmPrintRecordInfoBean> storeInfoByDept(List<String> list, String dept)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class, String.class};
		Object[] objs = new Object[] {list, dept};
		return (List<CmPrintRecordInfoBean>)remoteMethodInvoke("storeInfoByDept", cls, objs);
	}
	@SuppressWarnings("unchecked") //add by lkc 2018.1.11
	public static List<CmPrintRecordInfoBean> storeInfo(List<String> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (List<CmPrintRecordInfoBean>)remoteMethodInvoke("storeInfo", cls, objs);
	}
	//暂时保存发起封存的封存时间到数据库
	@SuppressWarnings("unchecked") //add by lkc 2018.1.11
	public static String saveInfoOfStore(List<CmPrintRecordInfoBean> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (String)remoteMethodInvoke("saveInfoOfStore", cls, objs);
	}
	//启封流程校验，避免重复发起
	@SuppressWarnings("unchecked") //add by lkc 2018.1.11
	public static String checkInfoOfOpenStore(List<CmPrintRecordInfoBean> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (String)remoteMethodInvoke("checkInfoOfOpenStore", cls, objs);
	}
	//直接提交延迟流程校验，避免重复发起
	@SuppressWarnings("unchecked") //add by lkc 2018.1.11
	public static String checkInfoOfDelayByRecover(List<CmPrintRecordInfoBean> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (String)remoteMethodInvoke("checkInfoOfDelayByRecover", cls, objs);
	}
	@SuppressWarnings("unchecked") //add by lkc 2018.1.11
	public static String checkInfoOfDelayByStore(List<CmPrintRecordInfoBean> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (String)remoteMethodInvoke("checkInfoOfDelayByStore", cls, objs);
	}
	@SuppressWarnings("unchecked") //add by lkc 2018.1.11
	public static void updateOpenStoreStatus(List<String> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		remoteMethodInvoke1("updateOpenStoreStatus", cls, objs);
	}
	@SuppressWarnings("unchecked") //add by lkc 2018.1.11
	public static void updateDelayStatusByRecover(List<String> list, String userName, String userDept)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class, String.class, String.class};
		Object[] objs = new Object[] {list, userName, userDept};
		remoteMethodInvoke1("updateDelayStatusByRecover", cls, objs);
	}
	@SuppressWarnings("unchecked") //add by lkc 2018.1.11
	public static void updateDelayStatusByStore(List<String> list, String userName, String userDept)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class, String.class, String.class};
		Object[] objs = new Object[] {list, userName, userDept};
		remoteMethodInvoke1("updateDelayStatusByStore", cls, objs);
	}

	@SuppressWarnings("unchecked") //add by lkc 2017.12.28
	public static List<CmPrintRecordInfoBean> queryRecoverTableIsDelay(List<String> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (List<CmPrintRecordInfoBean>)remoteMethodInvoke("queryRecoverTableIsDelay", cls, objs);
	}

	@SuppressWarnings("unchecked") //add by lkc 2017.12.28
	public static List<CmPrintRecordInfoBean> queryStoreTableIsDelay(List<String> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (List<CmPrintRecordInfoBean>)remoteMethodInvoke("queryStoreTableIsDelay", cls, objs);
	}

	@SuppressWarnings("unchecked") //add by lkc 2017.12.28
	public static String getNumberByOid(String oid)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class};
		Object[] objs = new Object[] {oid};
		return (String)remoteMethodInvoke("getNumberByOid", cls, objs);
	}

	@SuppressWarnings("unchecked") //add by lkc 2017.12.28
	public static List<String> queryRecoverTableID(String pboOid)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class};
		Object[] objs = new Object[] {pboOid};
		return (List<String>)remoteMethodInvoke("queryRecoverTableID", cls, objs);
	}

	@SuppressWarnings("unchecked") //add by lkc 2017.12.28
	public static void updateRecoverStatusBySelf(List<String> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		remoteMethodInvoke1("updateRecoverStatusBySelf", cls, objs);
	}

	@SuppressWarnings("unchecked") //add by lkc 2017.12.28
	public static void updateStoreStatusBySelf(List<String> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		remoteMethodInvoke1("updateStoreStatusBySelf", cls, objs);
	}
	/**
	 * 点击启封确认时修改文件状态
	* @author jyx
	* @date 2018-5-21
	* @param list
	* @throws RemoteException
	* @throws InvocationTargetException
	 */
	@SuppressWarnings("unchecked") //add by jyc 2018.05.21
	public static void updateFileStatusOfOpenStore(List<String> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		remoteMethodInvoke1("updateFileStatusOfOpenStore", cls, objs);
	}

//	@SuppressWarnings("unchecked") //add by lkc 2017.12.28
//	public static List<String> queryBarCodeIsDelay(List<String> list)
//			throws RemoteException, InvocationTargetException {
//		Class<?>[] cls = new Class[] {List.class};
//		Object[] objs = new Object[] {list};
//		return (List<String>)remoteMethodInvoke("queryBarCodeIsDelay", cls, objs);
//	}

	@SuppressWarnings("unchecked") //add by lkc 2017.12.28
	public static List<String> queryBarTableIDByDept(List<String> list, String dept)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class, String.class};
		Object[] objs = new Object[] {list, dept};
		return (List<String>)remoteMethodInvoke("queryBarTableIDByDept", cls, objs);
	}
	//封存流程中线下回收纸质文件中根据部门获得显示文件在条码表中id
	@SuppressWarnings("unchecked") //add by lkc 2017.12.28
	public static List<String> queryBarTableIDByDeptOfStore(List<String> list, String dept)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class, String.class};
		Object[] objs = new Object[] {list, dept};
		return (List<String>)remoteMethodInvoke("queryBarTableIDByDeptOfStore", cls, objs);
	}

	@SuppressWarnings("unchecked") //add by lkc 2017.12.28
	public static List<String> queryBarTableIDByDeptOfChange(String pboOid, String dept)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class, String.class};
		Object[] objs = new Object[] {pboOid, dept};
		return (List<String>)remoteMethodInvoke("queryBarTableIDByDeptOfChange", cls, objs);
	}

	@SuppressWarnings("unchecked") //add by lkc 2017.12.28
	public static List<String> queryBarTableID(List<String> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (List<String>)remoteMethodInvoke("queryBarTableID", cls, objs);
	}

	@SuppressWarnings("unchecked") //add by lkc 2017.12.28
	public static List<String> queryBarTableIDByOver(List<String> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (List<String>)remoteMethodInvoke("queryBarTableIDByOver", cls, objs);
	}

	@SuppressWarnings("unchecked") //add by lkc 2017.12.28
	public static List<String> queryBarTableIDByDelay(List<String> list, Boolean isFromRecover)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class, Boolean.class};
		Object[] objs = new Object[] {list, isFromRecover};
		return (List<String>)remoteMethodInvoke("queryBarTableIDByDelay", cls, objs);
	}

	@SuppressWarnings("unchecked") //add by lkc 2017.12.28
	public static List<String> queryBarTableIDByChange(String pboOid)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class};
		Object[] objs = new Object[] {pboOid};
		return (List<String>)remoteMethodInvoke("queryBarTableIDByChange", cls, objs);
	}
	@SuppressWarnings("unchecked") //add by lkc 2017.12.28
	public static List<CmPrintRecordInfoBean> queryRecoverTable(List<String> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (List<CmPrintRecordInfoBean>)remoteMethodInvoke("queryRecoverTable", cls, objs);
	}
	//封存流程中在纸质文件回收阶段获得待展示文件信息
	@SuppressWarnings("unchecked") //add by lkc 2017.12.28
	public static List<CmPrintRecordInfoBean> queryStoreTable(List<String> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (List<CmPrintRecordInfoBean>)remoteMethodInvoke("queryStoreTable", cls, objs);
	}

	@SuppressWarnings("unchecked") //add by lkc 2017.12.28
	public static List<CmPrintRecordInfoBean> queryImmediateSubmitDelayInfo(List<String> list, Boolean isFromRecover)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class, Boolean.class};
		Object[] objs = new Object[] {list, isFromRecover};
		return (List<CmPrintRecordInfoBean>)remoteMethodInvoke("queryImmediateSubmitDelayInfo", cls, objs);
	}

	@SuppressWarnings("unchecked") //add by lkc 2017.12.28
	public static List<CmPrintRecordInfoBean> queryDelayFileInfo(List<String> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (List<CmPrintRecordInfoBean>)remoteMethodInvoke("queryDelayFileInfo", cls, objs);
	}
	@SuppressWarnings("unchecked") //add by lkc 2017.12.28
	public static List<CmPrintRecordInfoBean> queryDelayFileInfoByOver(List<String> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (List<CmPrintRecordInfoBean>)remoteMethodInvoke("queryDelayFileInfoByOver", cls, objs);
	}
	@SuppressWarnings("unchecked") //add by lkc 2017.12.28
	public static List<CmPrintRecordInfoBean> queryRecoverTableOfDelay(List<String> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (List<CmPrintRecordInfoBean>)remoteMethodInvoke("queryRecoverTableOfDelay", cls, objs);
	}

	@SuppressWarnings("unchecked") //add by lkc 2017.12.28
	public static List<CmPrintRecordInfoBean> queryStoreTableByDelay(List<String> list, Boolean isFromRecover)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class, Boolean.class};
		Object[] objs = new Object[] {list, isFromRecover};
		return (List<CmPrintRecordInfoBean>)remoteMethodInvoke("queryStoreTableByDelay", cls, objs);
	}
	@SuppressWarnings("unchecked") //add by lkc 2018.1.2
	public static Boolean isSubmitDelayByWfProcessOid(String oid)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class};
		Object[] objs = new Object[] {oid};
		return (Boolean)remoteMethodInvoke("isSubmitDelayByWfProcessOid", cls, objs);
	}
	@SuppressWarnings("unchecked") //add by lkc 2018.1.2
	public static Boolean isSubmitDelayByWfProcessOidAndActivityNames(String oid,String activityNames)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class,String.class};
		Object[] objs = new Object[] {oid,activityNames};
		return (Boolean)remoteMethodInvoke("isSubmitDelayByWfProcessOidAndActivityNames", cls, objs);
	}
	//延迟申请流程中保存收延迟原因和延迟时间
	@SuppressWarnings("unchecked") //add by jyx 2018.5.15
	public static String saveToRecoverTableDelayDate(List<CmPrintRecordInfoBean> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (String)remoteMethodInvoke("saveToRecoverTableDelayDate", cls, objs);
	}
	//回收流程中延迟请求新增数据保存到数据库
	@SuppressWarnings("unchecked") //add by lkc 2018.1.2
	public static String saveToRecoverTableDelayInfo(List<CmPrintRecordInfoBean> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (String)remoteMethodInvoke("saveToRecoverTableDelayInfo", cls, objs);
	}
	//封存流程中延迟请求新增数据保存到数据库
	@SuppressWarnings("unchecked") //add by lkc 2018.1.2
	public static String saveToStoreTableDelayInfo(List<CmPrintRecordInfoBean> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (String)remoteMethodInvoke("saveToStoreTableDelayInfo", cls, objs);
	}

	@SuppressWarnings("unchecked") //add by lkc 2018.1.2
	public static void updatePBONumberToDB(List<String> list, String pboNumber)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class, String.class};
		Object[] objs = new Object[] {list, pboNumber};
		remoteMethodInvoke1("updatePBONumberToDB", cls, objs);
	}

	@SuppressWarnings("unchecked") //add by lkc 2018.1.2
	public static void updatePBONumberToDBByStore(List<String> list, String pboNumber)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class, String.class};
		Object[] objs = new Object[] {list, pboNumber};
		remoteMethodInvoke1("updatePBONumberToDBByStore", cls, objs);
	}

	//更新回收人和部门
	@SuppressWarnings("unchecked") //add by lkc 2018.1.2
	public static void updateCurrentUserAndDept(List<String> list, String user, String dept)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class, String.class, String.class};
		Object[] objs = new Object[] {list, user, dept};
		remoteMethodInvoke1("updateCurrentUserAndDept", cls, objs);
	}

	@SuppressWarnings("unchecked") //add by lkc 2018.1.2
	public static Boolean saveToRecoverTableDelayInfor(List<CmPrintRecordInfoBean> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (Boolean)remoteMethodInvoke("saveToRecoverTableDelayInfor", cls, objs);
	}
	@SuppressWarnings("unchecked") //add by lkc 2018.1.2
	public static Boolean saveToStoreTableDelayInfor(List<CmPrintRecordInfoBean> list, Boolean isFromRecover)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class, Boolean.class};
		Object[] objs = new Object[] {list, isFromRecover};
		return (Boolean)remoteMethodInvoke("saveToStoreTableDelayInfor", cls, objs);
	}
	//自行发起回收流程启动
	@SuppressWarnings("unchecked") //add by lkc 2017.12.25
	public static Boolean startProcessOfRecover(String name, List<String> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class, List.class};
		Object[] objs = new Object[] {name, list};
		return (Boolean)remoteMethodInvoke("startProcessOfRecover", cls, objs);
	}
	//自行发起回收流程启动
	@SuppressWarnings("unchecked")
	public static Boolean startProcessOfRecoverInFactory(String name, List<String> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class, List.class};
		Object[] objs = new Object[] {name, list};
		return (Boolean)remoteMethodInvoke("startProcessOfRecoverInFactory", cls, objs);
	}
	//回收中发起的延迟流程
	@SuppressWarnings("unchecked") //add by lkc 2017.12.25
	public static String startProcessOfDelay(String name, List<String> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class, List.class};
		Object[] objs = new Object[] {name, list};
		return (String)remoteMethodInvoke("startProcessOfDelay", cls, objs);
	}
	//启动遗失流程
	@SuppressWarnings("unchecked") //add by lkc 2017.12.25
	public static String startProcessOfLose(String name, List<String> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class, List.class};
		Object[] objs = new Object[] {name, list};
		return (String)remoteMethodInvoke("startProcessOfLose", cls, objs);
	}
	//启动封存与启封流程
	@SuppressWarnings("unchecked") //add by lkc 2017.12.25
	public static Boolean startProcessOfStore(String name, List<String> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class, List.class};
		Object[] objs = new Object[] {name, list};
		return (Boolean)remoteMethodInvoke("startProcessOfStore", cls, objs);
	}
	//自行发起回收更新表中部分
	@SuppressWarnings("unchecked") //add by lkc 2017.12.25
	public static Boolean updateToDB(String barCode)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class};
		Object[] objs = new Object[] {barCode};
		return (Boolean)remoteMethodInvoke("updateToDB", cls, objs);
		}
	//自行发起回收保存到回收表中部分
	@SuppressWarnings("unchecked") //add by lkc 2017.12.20
	public static String saveToDBOfRecover(List<CmPrintRecordInfoBean> listBean)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {listBean};
		return (String)remoteMethodInvoke("saveToDBOfRecover", cls, objs);
	}
	//厂内文件自行发起回收查询
	@SuppressWarnings("unchecked") //add by lkc 2017.12.20
	public static List<CmPrintRecordInfoBean> queryFileInfo(String fileNumber, String fileName, String category)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class, String.class, String.class};
		Object[] objs = new Object[] {fileNumber, fileName, category};
		return (List<CmPrintRecordInfoBean>)remoteMethodInvoke("queryFileInfo", cls, objs);
	}
	//外来文件自行发起回收查询
	@SuppressWarnings("unchecked") //add by lkc 2017.12.20
	public static List<CmPrintRecordInfoBean> queryOutsideFileInfo(String fileNumber, String fileName)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class, String.class};
		Object[] objs = new Object[] {fileNumber, fileName};
		return (List<CmPrintRecordInfoBean>)remoteMethodInvoke("queryOutsideFileInfo", cls, objs);
	}
	//厂内纸质文件自行发起回收查询
	@SuppressWarnings("unchecked") //add by lkc 2017.12.20
	public static List<CmPrintRecordInfoBean> queryPaperFileInfo(String fileNumber, String fileName)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class, String.class};
		Object[] objs = new Object[] {fileNumber, fileName};
		return (List<CmPrintRecordInfoBean>)remoteMethodInvoke("queryPaperFileInfo", cls, objs);
	}
	//多条件查询
	@SuppressWarnings("unchecked") //add by lkc 2017.12.18
	public static List<CmPrintRecordInfoBean> queryInfo(CmPrintRecordQueryBean cmPrintRecordQueryBean)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {CmPrintRecordQueryBean.class};
		Object[] objs = new Object[] {cmPrintRecordQueryBean};
		return (List<CmPrintRecordInfoBean>)remoteMethodInvoke("queryInfo", cls, objs);
	}
	//获得当前用户名所在部门
	@SuppressWarnings("unchecked") //add by lkc 2017.12.18
	public static String getUserDepartment()
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {};
		Object[] objs = new Object[] {};
		return (String)remoteMethodInvoke("getUserDepartment", cls, objs);
	}
	@SuppressWarnings("unchecked") //add by lkc 2017.12.18
	public static String getUserDepartment(String userName)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class};
		Object[] objs = new Object[] {userName};
		return (String)remoteMethodInvoke("getUserDepartment", cls, objs);
	}
	@SuppressWarnings("unchecked") //add by lkc 2017.12.18
	public static List<String> saveInfoToDB(List<String> strList, String userName, String dept, String date)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class, String.class, String.class, String.class};
		Object[] objs = new Object[] {strList, userName, dept, date};
		return (List<String>)remoteMethodInvoke("saveInfoToDB", cls, objs);
	}

	public static List<String> saveInfoToDB2(List<String> strList, String userName, String dept, String date)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class, String.class, String.class, String.class};
		Object[] objs = new Object[] {strList, userName, dept, date};
		return (List<String>)remoteMethodInvoke("saveInfoToDB2", cls, objs);
	}

	@SuppressWarnings("unchecked") //add by lkc 2017.12.18
	public static List<String> saveInfoToDBOfStore(List<String> strList, String userName, String dept, String date)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class, String.class, String.class, String.class};
		Object[] objs = new Object[] {strList, userName, dept, date};
		return (List<String>)remoteMethodInvoke("saveInfoToDBOfStore", cls, objs);
	}

	//获得当前用户所在组
	@SuppressWarnings("unchecked") //add by lkc 2017.12.18
	public static String getUserGroup()
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {};
		Object[] objs = new Object[] {};
		return (String)remoteMethodInvoke("getUserGroup", cls, objs);
	}
	//获得当前用户名
		@SuppressWarnings("unchecked") //add by lkc 2017.12.18
		public static String getUserName()
				throws RemoteException, InvocationTargetException {
			Class<?>[] cls = new Class[] {};
			Object[] objs = new Object[] {};
			return (String)remoteMethodInvoke("getUserName", cls, objs);
		}
	//保存遗失申请信息
	@SuppressWarnings("unchecked") //add by lkc 2017.12.18
	public static String saveLoseInfo(List<CmPrintRecordInfoBean> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (String)remoteMethodInvoke("saveLoseInfo", cls, objs);
	}
	//回收过程中保存遗失申请信息
	@SuppressWarnings("unchecked") //add by lkc 2017.12.18
	public static String saveLoseInfoOfRecover(List<CmPrintRecordInfoBean> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (String)remoteMethodInvoke("saveLoseInfoOfRecover", cls, objs);
	}
	//回收过程中保存遗失申请信息
	@SuppressWarnings("unchecked") //add by lkc 2017.12.18
	public static String saveWfprocessLoseInfoOfRecover(List<CmPrintRecordInfoBean> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (String)remoteMethodInvoke("saveWfprocessLoseInfoOfRecover", cls, objs);
	}


	@SuppressWarnings("unchecked") //add by lkc 2017.12.18
	public static String saveLoseInfoOfStore(List<CmPrintRecordInfoBean> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (String)remoteMethodInvoke("saveLoseInfoOfStore", cls, objs);
	}

	@SuppressWarnings("unchecked") //add by lkc 2017.12.18
	public static List<CmPrintRecordInfoBean> updateBean(List<CmPrintRecordInfoBean> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (List<CmPrintRecordInfoBean>)remoteMethodInvoke("updateBean", cls, objs);
	}

	@SuppressWarnings("unchecked") //add by lkc 2017.12.12
	public static String queryMaxNumber()
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {};
		Object[] objs = new Object[] {};
		return (String)remoteMethodInvoke("queryMaxNumber", cls, objs);
	}

	//印章管理查询印章
	@SuppressWarnings("unchecked") //add by lkc 2017.12.12
	public static List<CmSealBean> selectSeal(String sealName)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class};
		Object[] objs = new Object[] {sealName};
		return (List<CmSealBean>)remoteMethodInvoke("selectSeal", cls, objs);
	}

	//印章管理添加印章
	@SuppressWarnings("unchecked")//add by lkc 2017.12.12
	public static Boolean addSeal(String uuid, String name, String number)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class, String.class, String.class};
		Object[] objs = new Object[] {uuid, name, number};
		return (Boolean)remoteMethodInvoke("addSeal", cls, objs);
	}
	//印章管理修改印章
	@SuppressWarnings("unchecked")//add by lkc 2017.12.12
	public static Boolean alterSeal(String uuid, String name)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class, String.class};
		Object[] objs = new Object[] {uuid, name};
		return (Boolean)remoteMethodInvoke("alterSeal", cls, objs);
	}
	//印章管理删除印章
	@SuppressWarnings("unchecked")//add by lkc 2017.12.12
	public static Boolean deleteSeal(List<String> list)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (Boolean)remoteMethodInvoke("deleteSeal", cls, objs);
	}
	@SuppressWarnings("unchecked")//add by lkc 2017.12.12
	public static void updateNumberToDB(List<CmSealBean> listBean)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {listBean};
		remoteMethodInvoke1("updateNumberToDB", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<CmBaseline> getBaseline(String oid, String fileType) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class, String.class };
		Object[] objs = new Object[] { oid, fileType };
		return (List<CmBaseline>)remoteMethodInvoke("getBaseline", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static Vector<String> getDistributeDept() throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {  };
		Object[] objs = new Object[] {  };
		return (Vector<String>)remoteMethodInvoke("getDistributeDept", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static Vector<String> getOutsideDept() throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {  };
		Object[] objs = new Object[] {  };
		return (Vector<String>)remoteMethodInvoke("getOutsideDept", cls, objs);
	}

	public static String createGwPrintApplyRecords(List<CmPrintInfoBean> list) throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { List.class };
		Object[] objs = new Object[] { list };
		return (String)remoteMethodInvoke("createGwPrintApplyRecords", cls, objs);
	}

	public static String getPrintObjNumber(String objType, String preFix) throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { String.class, String.class };
		Object[] objs = new Object[] { objType, preFix };
		return (String)remoteMethodInvoke("getPrintObjNumber", cls, objs);
	}

	public static String saveGwPrintApplyRecords(List<CmPrintInfoBean> list, String pboOid) throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { List.class,String.class };
		Object[] objs = new Object[] { list,pboOid };
		return (String)remoteMethodInvoke("saveGwPrintApplyRecords", cls, objs);
	}

	public static String updatePrintState(String qrCodeNumber, long userOid) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class, long.class };
		Object[] objs = new Object[] { qrCodeNumber, userOid };
		return (String)remoteMethodInvoke("updatePrintState", cls, objs);
	}

	public static String updateRejectState(List<String[]> list) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { List.class };
		Object[] objs = new Object[] { list };
		return (String)remoteMethodInvoke("updateRejectState", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<CmAttachment> getPdfFiles(List<String> list, String oid) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { List.class, String.class };
		Object[] objs = new Object[] { list, oid };
		return (List<CmAttachment>)remoteMethodInvoke("getPdfFiles", cls, objs);
	}
	@SuppressWarnings("unchecked") // add by lkc
	public static List<CmAttachment> getPdfByOid(List<String> list) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class };
		Object[] objs = new Object[] {list};
		return (List<CmAttachment>)remoteMethodInvoke("getPdfByOid", cls, objs);
	}
	//获得文档最新版本
	@SuppressWarnings("unchecked") // add by lkc
	public static String getLatestDocumentVersionByNumber(String number) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class };
		Object[] objs = new Object[] {number};
		return (String)remoteMethodInvoke("getLatestDocumentVersionByNumber", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<CmAttachment> printPDFAndReturn(List<CmPrintInfoBean> list) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { List.class };
		Object[] objs = new Object[] { list };
		return (List<CmAttachment>)remoteMethodInvoke("printPDFAndReturn", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<CmPrintInfoBean> addPrintFiles(CmPrintQueryBean cmPrintQueryBean) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { CmPrintQueryBean.class };
		Object[] objs = new Object[] { cmPrintQueryBean };
		return (List<CmPrintInfoBean>)remoteMethodInvoke("addPrintFiles", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<CmPrintInfoBean> addPrintApplicationFiles(CmPrintQueryBean cmPrintQueryBean) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { CmPrintQueryBean.class };
		Object[] objs = new Object[] { cmPrintQueryBean };
		return (List<CmPrintInfoBean>)remoteMethodInvoke("addPrintApplicationFiles", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<CmPrintInfoBean> addFilesOnBom(CmPrintQueryBean cmPrintQueryBean) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { CmPrintQueryBean.class };
		Object[] objs = new Object[] { cmPrintQueryBean };
		return (List<CmPrintInfoBean>)remoteMethodInvoke("addFilesOnBom", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<CmPrintInfoBean> addBomFiles(CmPrintInfoBean cmPrintInfoBean) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { CmPrintInfoBean.class };
		Object[] objs = new Object[] { cmPrintInfoBean };
		return (List<CmPrintInfoBean>)remoteMethodInvoke("addBomFiles", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<String> getAllChildPartOid(CmPrintInfoBean cmPrintInfoBean) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { CmPrintInfoBean.class };
		Object[] objs = new Object[] { cmPrintInfoBean };
		return (List<String>)remoteMethodInvoke("getAllChildPartOid", cls, objs);
	}
	
	@SuppressWarnings("unchecked")
	public static List<CmPrintInfoBean> queryBomFilesByPart(String partOid, String mainTechnics) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class, String.class };
		Object[] objs = new Object[] { partOid, mainTechnics };
		return (List<CmPrintInfoBean>)remoteMethodInvoke("queryBomFilesByPart", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> getReceiptPerson(String userName) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class };
		Object[] objs = new Object[] { userName };
		return (Map<String, String>)remoteMethodInvoke("getReceiptPerson", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<CmPrintInfoBean> printFilesMgt(CmPrintQueryBean cmPrintQueryBean, boolean isZxdy) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { CmPrintQueryBean.class, boolean.class };
		Object[] objs = new Object[] { cmPrintQueryBean, isZxdy };
		return (List<CmPrintInfoBean>)remoteMethodInvoke("printFilesMgt", cls, objs);
	}

	public static String isCanGet(String barCode, String receiptDept) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class, String.class };
		Object[] objs = new Object[] { barCode, receiptDept };
		return (String)remoteMethodInvoke("isCanGet", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<CmPrintInfoBean> getReceiptFile(String barCode, String receiptDept) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class, String.class };
		Object[] objs = new Object[] { barCode, receiptDept };
		return (List<CmPrintInfoBean>)remoteMethodInvoke("getReceiptFile", cls, objs);
	}

	public static String updateReceiptInfo(List<CmPrintInfoBean> list) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { List.class };
		Object[] objs = new Object[] { list };
		return (String)remoteMethodInvoke("updateReceiptInfo", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<CmPrintInfoBean> ylqFileAddQuery(CmPrintQueryBean cmPrintQueryBean) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { CmPrintQueryBean.class };
		Object[] objs = new Object[] { cmPrintQueryBean };
		return (List<CmPrintInfoBean>)remoteMethodInvoke("ylqFileAddQuery", cls, objs);
	}

	public static String isCanRecover(String barCode, String recoverDept) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class, String.class };
		Object[] objs = new Object[] { barCode, recoverDept };
		return (String)remoteMethodInvoke("isCanRecover", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<CmPrintInfoBean> getRecoverFile(String barCode, String recoverDept) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class, String.class };
		Object[] objs = new Object[] { barCode, recoverDept };
		return (List<CmPrintInfoBean>)remoteMethodInvoke("getRecoverFile", cls, objs);
	}

	public static String createGwPrintRecoverRecords(List<CmPrintInfoBean> list, String oid) throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { List.class, String.class };
		Object[] objs = new Object[] { list, oid };
		return (String)remoteMethodInvoke("createGwPrintRecoverRecords", cls, objs);
	}

	public static String updateRecoverInfo(List<CmPrintInfoBean> list) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { List.class };
		Object[] objs = new Object[] { list };
		return (String)remoteMethodInvoke("updateRecoverInfo", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> queryUserInfoByCode(String userCode) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class };
		Object[] objs = new Object[] { userCode };
		return (Map<String, String>)remoteMethodInvoke("queryUserInfoByCode", cls, objs);
	}

	public static String cancelPrintState(String qrCodeNumber) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class };
		Object[] objs = new Object[] { qrCodeNumber };
		return (String)remoteMethodInvoke("cancelPrintState", cls, objs);
	}

	public static List<CmPrintInfoBean> getDistributeAndRecoverInfo(String qrCodeNumber) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class };
		Object[] objs = new Object[] { qrCodeNumber };
		return (List<CmPrintInfoBean>)remoteMethodInvoke("getDistributeAndRecoverInfo", cls, objs);
	}

	public static Map<String, byte[]> getPdfFileForLookUp(List<CmPrintInfoBean> list) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { List.class };
		Object[] objs = new Object[] { list };
		return (Map<String, byte[]>)remoteMethodInvoke("getPdfFileForLookUp", cls, objs);
	}
	public static Map<String, byte[]> checkIsHasPrint(List<CmPrintInfoBean> list) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { List.class };
		Object[] objs = new Object[] { list };
		return (Map<String, byte[]>)remoteMethodInvoke("checkIsHasPrint", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<CmPrintInfoBean> printFilesTYDYGL() throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { };
		Object[] objs = new Object[] { };
		return (List<CmPrintInfoBean>)remoteMethodInvoke("printFilesTYDYGL", cls, objs);
	}

	public static List<CmPrintInfoBean> queryBaselines(CmPrintQueryBean printQueryBean) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { CmPrintQueryBean.class };
		Object[] objs = new Object[] { printQueryBean };
		return (List<CmPrintInfoBean>)remoteMethodInvoke("queryBaselines", cls, objs);
	}

	//20170401_jiangyixing_判断是否已批准
	@SuppressWarnings("unchecked")
	public static String checkComplete(String qrCodeNumber)throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { String.class};
		Object[] objs = new Object[] { qrCodeNumber};
		return (String)remoteMethodInvoke("checkComplete", cls, objs);
	}
	/**
	 * 获取所有的工艺类型
	 * @return
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	@SuppressWarnings("unchecked")
	public static Vector<String> getAllMPMSkill() throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { };
		Object[] objs = new Object[] { };
		return (Vector<String>) remoteMethodInvoke("getAllMPMSkill", cls, objs);
    }
	/**
	 * 获得所有产品的型号代号值
	 * @return
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	@SuppressWarnings("unchecked")
	public static Vector<String> getProductMindex() throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { };
		Object[] objs = new Object[] { };
		return (Vector<String>) remoteMethodInvoke("getProductMindex", cls, objs);
    }

	@SuppressWarnings("unchecked")
	public static List<CmPrintInfoBean> loadPrintApplication(String oid)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class };
		Object[] objs = new Object[] {oid };
		return (List<CmPrintInfoBean>)remoteMethodInvoke("loadPrintApplication", cls, objs);
	}

	public static void setPrintStatus(List<String> list, String status, String printer, String printDate) {
		Class<?>[] cls = new Class[] {List.class,String.class,String.class,String.class};
		Object[] objs = new Object[] {list, status, printer, printDate};
		try {
			remoteMethodInvoke("setPrintStatus", cls, objs);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static List<CmPrintInfoBean> loadPaperFile(String oid, String category)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class, String.class};
		Object[] objs = new Object[] {oid, category};
		return (List<CmPrintInfoBean>)remoteMethodInvoke("loadPaperFile", cls, objs);
	}


	@SuppressWarnings("unchecked")
	public static List<CmPrintInfoBean> searchSealPlus(CmPrintQueryBean cmPrintQueryBean, String category) {
		Class<?>[] cls = new Class[] {CmPrintQueryBean.class, String.class};
		Object[] objs = new Object[] {cmPrintQueryBean, category};
		try {
			return (List<CmPrintInfoBean>) remoteMethodInvoke("searchSealPlus", cls, objs);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	//加盖印章查询数据 add by zhuhao 20180124
	@SuppressWarnings("unchecked")
	public static ArrayList<CmSealBean> selectSeal() throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { };
		Object[] objs = new Object[] { };
		return (ArrayList<CmSealBean>)remoteMethodInvoke("selectAllSeal", cls, objs);
	}

	public static String updatePrintAddSeal(ArrayList<CmSealBean> list, String oid) {
		Class<?>[] cls = new Class[] {List.class, String.class };
		Object[] objs = new Object[] {list, oid };
		try {
			return (String)remoteMethodInvoke("updatePrintAddSeal", cls, objs);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<CmPrintInfoBean> loadSealPlus(String oid, String category) {
		Class<?>[] cls = new Class[] {String.class,String.class };
		Object[] objs = new Object[] {oid, category };
		try {
			return (List<CmPrintInfoBean>)remoteMethodInvoke("loadSealPlus", cls, objs);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<CmPrintInfoBean> getQRbarcode(String oid) {
		Class<?>[] cls = new Class[] {String.class };
		Object[] objs = new Object[] {oid };
		try {
			return (List<CmPrintInfoBean>)remoteMethodInvoke("getQRbarcode", cls, objs);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<CmPrintInfoBean> addFileOnProcessDirectory(CmPrintQueryBean cmPrintQueryBean) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { CmPrintQueryBean.class };
		Object[] objs = new Object[] { cmPrintQueryBean };
		return (List<CmPrintInfoBean>)remoteMethodInvoke("addFileOnProcessDirectory", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<CmPrintInfoBean> getProcessFiles(CmPrintInfoBean cmPrintInfoBean) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { CmPrintInfoBean.class };
		Object[] objs = new Object[] { cmPrintInfoBean };
		return (List<CmPrintInfoBean>)remoteMethodInvoke("getProcessFiles", cls, objs);
	}
	@SuppressWarnings("unchecked")
	public static List<String> setFileStatus(List<String> list, CmDistributionBean cmDistributionBean) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { List.class, CmDistributionBean.class};
		Object[] objs = new Object[] { list, cmDistributionBean };
		return (List<String>)remoteMethodInvoke("setFileStatus", cls, objs);
	}

	public static CmPrintInfoBean addOutFile(CmPrintQueryBean cmPrintQueryBean, boolean isModify, String id) throws RemoteException, InvocationTargetException  {
		Class<?>[] cls = new Class[] { CmPrintQueryBean.class ,boolean.class,String.class};
		Object[] objs = new Object[] { cmPrintQueryBean ,isModify,id};
		return (CmPrintInfoBean)remoteMethodInvoke("addOutFile", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<CmPrintInfoBean> searchOutFile(CmPrintQueryBean cmPrintQueryBean) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { CmPrintQueryBean.class};
		Object[] objs = new Object[] { cmPrintQueryBean};
		return (List<CmPrintInfoBean>)remoteMethodInvoke("searchOutFile", cls, objs);
	}
	public static void deleteOutFileByID(List<CmPrintInfoBean> beanList) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { List.class};
		Object[] objs = new Object[] { beanList};
		remoteMethodInvoke("deleteOutFileByID", cls, objs);
	}
	public static void saveChangeNoticeInfo(CmPrintQueryBean cmPrintQueryBean1, String id, boolean isModify) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { CmPrintQueryBean.class ,String.class,boolean.class};
		Object[] objs = new Object[] { cmPrintQueryBean1 ,id,isModify};
		remoteMethodInvoke("saveChangeNoticeInfo", cls, objs);
	}
	@SuppressWarnings("unchecked")
	public static List<String> getAddBatch(String oid) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class};
		Object[] objs = new Object[] { oid};
		return (List<String>)remoteMethodInvoke("getAddBatch", cls, objs);
	}
	@SuppressWarnings("unchecked")
	public static List<CmPrintInfoBean> getReceiveData(String userName, String oid, String category) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class, String.class, String.class};
		Object[] objs = new Object[] { userName, oid, category};
		return (List<CmPrintInfoBean>)remoteMethodInvoke("getReceiveData", cls, objs);
	}
	public static String[] getAllDept() throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {};
		Object[] objs = new Object[] {};
		return (String[])remoteMethodInvoke("getAllDept", cls, objs);
	}
	public static CmDistributionBean getReceiveMessage(String name) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class };
		Object[] objs = new Object[] { name };
		return (CmDistributionBean)remoteMethodInvoke("getReceiveMessage", cls, objs);
	}
	public static String getCategory(String oid) throws RemoteException, InvocationTargetException  {
		Class<?>[] cls = new Class[] { String.class };
		Object[] objs = new Object[] { oid };
		return (String)remoteMethodInvoke("getCategory", cls, objs);
	}

	public static String getUserDept(String userName)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class};
		Object[] objs = new Object[] {userName};
		return (String)remoteMethodInvoke("getUserDept", cls, objs);
	}
	public static byte[] getImageByte(String qrName) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class};
		Object[] objs = new Object[] {qrName};
		return (byte[])remoteMethodInvoke("getImageByte", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<CmPrintInfoBean> loadPrintBarCode(String oid, String category,String dept) throws RemoteException, InvocationTargetException  {
		Class<?>[] cls = new Class[] {String.class, String.class, String.class};
		Object[] objs = new Object[] {oid, category, dept};
		return (List<CmPrintInfoBean>)remoteMethodInvoke("loadPrintBarCode", cls, objs);
	}

	public static void setPrintStatus(List<String> idList) throws RemoteException, InvocationTargetException  {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {idList};
		remoteMethodInvoke("setPrintStatus", cls, objs);
	}
	@SuppressWarnings("unchecked")
	public static List<CmPrintInfoBean> saveImportInfo(List<CmImportBean> beanList) throws RemoteException, InvocationTargetException  {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {beanList};
		return (List<CmPrintInfoBean>) remoteMethodInvoke("saveImportInfo", cls, objs);
	}
	@SuppressWarnings("unchecked")
	public static List<String> getTechnicsNumberByBOM(String value) throws RemoteException, InvocationTargetException  {
		Class<?>[] cls = new Class[] {String.class};
		Object[] objs = new Object[] {value};
		return (List<String>) remoteMethodInvoke("getTechnicsNumberByBOM", cls, objs);
	}
	@SuppressWarnings("unchecked")
	public static List<CmPrintRecordInfoBean> queryPrintInfo(List<String> technicsList) throws RemoteException, InvocationTargetException  {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {technicsList};
		return (List<CmPrintRecordInfoBean>) remoteMethodInvoke("queryPrintInfo", cls, objs);
	}
	@SuppressWarnings("unchecked")
	public static List<String> getTechnicsNumberByProcessDirectory(String value) throws RemoteException, InvocationTargetException  {
		Class<?>[] cls = new Class[] {String.class};
		Object[] objs = new Object[] {value};
		return (List<String>) remoteMethodInvoke("getTechnicsNumberByProcessDirectory", cls, objs);
	}
	@SuppressWarnings("unchecked")
	public static List<CmPrintRecordInfoBean> queryRecoverValueByUser(List<String> strList, String userName) throws RemoteException, InvocationTargetException  {
		Class<?>[] cls = new Class[] {List.class, String.class};
		Object[] objs = new Object[] {strList, userName};
		return (List<CmPrintRecordInfoBean>) remoteMethodInvoke("queryRecoverValueByUser", cls, objs);
	}
	public static void setProcessState(String pboOid, String state) throws RemoteException, InvocationTargetException  {
		Class<?>[] cls = new Class[] {String.class, String.class};
		Object[] objs = new Object[] {pboOid, state};
		remoteMethodInvoke("setProcessState", cls, objs);
	}
	public static void setChangeFileState(List<String> strList) throws RemoteException, InvocationTargetException  {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {strList};
		remoteMethodInvoke("setChangeFileState", cls, objs);
	}
	public static void saveLosePboNumber(String pboNumber, List<CmPrintRecordInfoBean> listBean) throws RemoteException, InvocationTargetException  {
		Class<?>[] cls = new Class[] {String.class, List.class};
		Object[] objs = new Object[] {pboNumber, listBean};
		remoteMethodInvoke("saveLosePboNumber", cls, objs);
	}
	public static String queryLosePboNumber(String barTableID) throws RemoteException, InvocationTargetException  {
		Class<?>[] cls = new Class[] {String.class};
		Object[] objs = new Object[] {barTableID};
		return (String)remoteMethodInvoke("queryLosePboNumber", cls, objs);
	}
	public static String querydelayPboNumber(String barTableID) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class};
		Object[] objs = new Object[] {barTableID};
		return (String)remoteMethodInvoke("querydelayPboNumber", cls, objs);
	}
	public static void saveAddSealPlus(String id, String allBatch) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class, String.class};
		Object[] objs = new Object[] {id, allBatch};
		remoteMethodInvoke("saveAddSealPlus", cls, objs);
	}
	public static String getUserNameBySign(String scanInput) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class};
		Object[] objs = new Object[] {scanInput};
		return (String)remoteMethodInvoke("getUserNameBySign", cls, objs);
	}
	@SuppressWarnings("unchecked")
	public static List<CmPrintInfoBean> searchPrintInfo(CmPrintQueryBean cmPrintQueryBean) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {CmPrintQueryBean.class};
		Object[] objs = new Object[] {cmPrintQueryBean};
		return (List<CmPrintInfoBean>)remoteMethodInvoke("searchPrintInfo", cls, objs);
	}
	public static String startPrintOffSet(List<CmPrintInfoBean> list) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (String)remoteMethodInvoke("startPrintOffSet", cls, objs);
	}
	public static boolean checkContainerRole(String containerName) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class};
		Object[] objs = new Object[] {containerName};
		return (Boolean)remoteMethodInvoke("checkContainerRole", cls, objs);
	}
	public static boolean checkRepeatOutFile(CmPrintQueryBean cmPrintQueryBean, String category) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {CmPrintQueryBean.class, String.class};
		Object[] objs = new Object[] {cmPrintQueryBean, category};
		return (Boolean)remoteMethodInvoke("checkRepeatOutFile", cls, objs);
	}
	public static boolean checkRepeatInputFile(String fileNumber, String version, String category) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class, String.class, String.class};
		Object[] objs = new Object[] {fileNumber, version, category};
		return (Boolean)remoteMethodInvoke("checkRepeatInputFile", cls, objs);
	}

	public static boolean isWfaComplete(String wfaOid) throws InvocationTargetException, RemoteException {
		Class<?>[] cls = new Class[] {String.class};
		Object[] objs = new Object[] {wfaOid};
		return (Boolean)remoteMethodInvoke("isWfaComplete", cls, objs);
	}
	
	public static String getPartType(String partNumber) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class };
		Object[] objs = new Object[] { partNumber };
		return (String) remoteMethodInvoke("getPartType", cls, objs);
	}

	public static Map<String, List<String>> getPrinterByDepts(String[] deptSeal) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[]{String[].class};
		Object[] objs = new Object[]{deptSeal};
		return (Map<String, List<String>>) remoteMethodInvoke("getPrinterByDepts", cls, objs);
	}

	public static String createPrintTransferProcess(List<CmPrintRecordInfoBean> list) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {List.class};
		Object[] objs = new Object[] {list};
		return (String) remoteMethodInvoke("createPrintTransferProcess", cls, objs);
	}

	public static List<CmPrintRecordInfoBean> queryTransferInfo(CmPrintRecordQueryBean cmPrintRecordQueryBean) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {CmPrintRecordQueryBean.class};
		Object[] objs = new Object[] {cmPrintRecordQueryBean};
		return (List<CmPrintRecordInfoBean>) remoteMethodInvoke("queryTransferInfo", cls, objs);
	}

	public static List<CmPrintRecordInfoBean> loadTransferData(String oid) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class};
		Object[] objs = new Object[] {oid};
		return (List<CmPrintRecordInfoBean>) remoteMethodInvoke("loadTransferData", cls, objs);
	}
}