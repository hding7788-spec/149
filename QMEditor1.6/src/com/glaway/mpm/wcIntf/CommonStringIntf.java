package com.glaway.mpm.wcIntf;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import wt.method.RemoteAccess;

import com.glaway.mpm.model.CsType;
import com.glaway.mpm.model.ShopType;
import com.glaway.mpm.model.WorkShop;
import com.glaway.mpm.resource.ResourceCache;
import com.glaway.mpm.util.IntfUtil;
import com.glaway.mpm.visual.log.VaLogger;

/**
 * @author ylshao
 * @ClassName: ProcessEditorToWCIntf
 * @Description:
 * @date 2012-11-28
 *
 */
public class CommonStringIntf implements RemoteAccess {
	private static VaLogger logger = VaLogger.getLogger(ImageIntf.class);
	private static List<CsType> csTypeList = null;
	
	/**
	 * 个人常用语入库
	 *
	 * @author ylshao
	 * @date 2012-11-1
	 * @param workType
	 * @param list
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static List addToCsLibrary(String workType, List list) throws RemoteException, InvocationTargetException {
		logger.debug("workType= " + workType);
		logger.debug("list= " + list);
		List returnList = (List) remoteMethodInvoke("justAddToCsLibraryRMI",
				new Class[] { String.class, List.class }, new Object[] {
						workType, list });
		logger.debug("returnList= " + returnList);
		return returnList;
	}

	/**
	 * 公共常用语查询
	 *
	 * @author ylshao
	 * @date 2012-11-1
	 * @return
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static CsType getPublicCommonStrings() throws RemoteException, InvocationTargetException {
		if(ResourceCache.shopTypes.isEmpty()) {
			getInitData();
		}
		List<CsType> cstypes = new ArrayList<CsType>();
		List<ShopType> shopTypes = ResourceCache.shopTypes;
		if (shopTypes != null) {
			for (ShopType shopType : shopTypes) {
				CsType type = new CsType(shopType, shopType.getCss(), null);
				cstypes.add(type);
			}
		}
		ShopType shopType = new ShopType();
		shopType.setName("常用语");
		CsType csType = new CsType(shopType, null, cstypes);
		logger.debug("csType= " + csType);
		return csType;

	}

	/**
	 * 初始化数据
	 *
	 * @author ylshao
	 * @date 2012-11-1
	 * @return
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 *
	 */
	public static void getInitData() throws RemoteException, InvocationTargetException {
		if (ResourceCache.workShops == null
				|| ResourceCache.workShops.size() == 0
				|| ResourceCache.shopTypes == null
				|| ResourceCache.shopTypes.size() == 0) {
			List<Object> list = (List<Object>) remoteMethodInvoke(
					"getAllWorkShopsRMI", null, null);
			if (list != null) {
				ResourceCache.workShops = (List<WorkShop>) list.get(0);
				ResourceCache.shopTypes = (List<ShopType>) list.get(1);
			}
		}

	}

	/**
	 * @Author caolei
	 * @Date 2015/6/3
	 * @Check caolei
	 * @Description 获取公共常用语集合
	 */

	public static List<CsType> justGetPublicCommonStrings() throws RemoteException, InvocationTargetException {
		List<CsType> cstype=new ArrayList<CsType>();
			cstype=justGetInitData();
		return cstype;

	}


	/**
	 * @Author caolei
	 * @Date 2015/6/3
	 * @Check caolei
	 * @Description 初始化常用语数据
	 */
	public static List<CsType> justGetInitData() throws RemoteException, InvocationTargetException {
		if(csTypeList == null) {
			csTypeList = (List<CsType>) remoteMethodInvoke("getAllCsType",null,null);
		}
		return csTypeList;
	}


	/**
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 *
	 *
	 * @Title: remoteMethodInvoke
	 * @Description:
	 * @return Object
	 * @throws
	 */
	public static Object remoteMethodInvoke(String mentodName,
			Class[] classArray, Object[] objectArray) throws RemoteException, InvocationTargetException {
		return IntfUtil.getRemoteMethodInvoke(mentodName, classArray,
				objectArray);
	}
}
