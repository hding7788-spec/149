package com.glaway.mpm.wcIntf;

import com.glaway.mpm.model.TempObject;
import com.glaway.mpm.util.IntfUtil;
import com.glaway.mpm.visual.log.VaLogger;
import wt.method.RemoteAccess;

import java.util.List;

/**
 * @author ylshao
 * @ClassName: ProcessEditorToWCIntf
 * @Description:
 * @date 2012-11-28
 *
 */
public class PBomIntf implements RemoteAccess {
	private static VaLogger logger = VaLogger.getLogger(PBomIntf.class);

	/**
	 * 获取POM XML文件
	 *
	 * @author ylshao
	 * @date 2012-11-1
	 * @param partNumber
	 * @return
	 *
	 */
	public static byte[] getPBomXML(String pbomOid) {
		byte[] bytes = (byte[]) remoteMethodInvoke("getPBOMXmlRMI",
				new Class[] { String.class }, new Object[] { pbomOid });
		return bytes;
	}

	/**
	 * 获取URL
	 *
	 * @author ylshao
	 * @date 2012-11-1
	 * @return
	 */
	public static String getPbomUrl(String oid) {
		return (String) remoteMethodInvoke("getPbomUrlRMI",
				new Class[] { String.class }, new Object[] { oid });
	}

	/**
	 * 获取整件名称和编号
	 *
	 * @author ylshao
	 * @date 2012-11-1
	 * @return
	 */
	public static List<TempObject> getPartAttributes(String nubmer, String name) {
		return (List<TempObject>) remoteMethodInvoke("getPartAttributesRMI",
				new Class[] { String.class, String.class }, new Object[] {
						nubmer, name });
	}

	/**
	 *
	 *
	 * @Title: remoteMethodInvoke
	 * @Description:
	 * @return Object
	 * @throws
	 */
	public static Object remoteMethodInvoke(String mentodName,
			Class[] classArray, Object[] objectArray) {
		return IntfUtil.getRemoteMethodInvoke(mentodName, classArray,
				objectArray);
	}

	/**
	 * 根据的getDesignOid
	 *
	 * @param map
	 * @return
	 */
	public static Long getDesignOid() {
		return (Long) pbomRemoteMethodInvoke("getViewByNameRMI",
				new Class[] { String.class }, new Object[] { "Design" });
	}

	/**
	 *
	 *
	 * @Title: remoteMethodInvoke
	 * @Description:
	 * @return Object
	 * @throws
	 */
	public static Object pbomRemoteMethodInvoke(String mentodName,
			Class[] classArray, Object[] objectArray) {
		return IntfUtil.getPbomRemoteMethodInvoke(mentodName, classArray,
				objectArray);
	}

	/**
	 * 获取二维图档url
	 *
	 * @author qianlong
	 * @date 2013-2-22
	 *
	 */
	public static String get2DDocumentUrl(String oid, long viewOid) {
		return (String)pbomRemoteMethodInvoke("get2DDocumentUrlRMI", new Class[] { String.class, Long.class },
				new Object[] { oid, viewOid });
	}

	/**
	 * 获取部件替代件编号列表
	 *
	 * @author cjh
	 * @date 2023-2-20
	 *
	 */
	public static List<String> getWTPartUsageList(String oid) {
		return (List<String>)pbomRemoteMethodInvoke("getWTPartUsageListRMI", new Class[] { String.class },
				new Object[] { oid });
	}
}
