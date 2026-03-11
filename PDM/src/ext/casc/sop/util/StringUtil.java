/**
 * 文件名：StringUtil.java<br/>
 * 创建时间：2016-8-2 上午09:01:31<br/>
 * 创建者：Administrator<br/>
 * 修改者：暂无<br/>
 * 修改简述：暂无<br/>
 * 修改详述：
 * <p>
 * 暂无<br/>
 * </p>
 * 修改时间：暂无<br/>
 */
package ext.casc.sop.util;

import java.util.List;

/**
 * 字符串处理类<br/>
 * <p>
 * 该类的详细描述<br/>
 * </p>
 * Time：2016-8-2 上午09:01:31<br/>
 * @author 张辉
 * @version 1.0.0
 * @since 1.0.0
 */
public class StringUtil {

	public static final String EMPTY = "";
	public static final String NULL = "null";

	/**
	 * 
	 * @param obj
	 * @return
	 */
	public static String object2String(Object obj) {
		String result;
		if (obj == null) {
			result = EMPTY;
		} else {
			result = String.valueOf(obj);
		}
		return result;
	}

	public static String listToString(List list){
		StringBuilder sb = new StringBuilder();
		for(Object obj : list){
			if(sb.toString().isEmpty()){
				sb.append(obj.toString());
			}else{
				sb.append(",").append(obj.toString());
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(Object str) {
		boolean result = Boolean.TRUE;
		if (str != null) {
			result = str.toString().trim().isEmpty();
		}
		return result;
	}

	/**
	 * 
	 * @param obj
	 * @return
	 */
	public static String getStringValue(Object obj) {
		String result;
		if (obj == null) {
			result = EMPTY;
		} else {
			result = String.valueOf(obj);
		}
		return result;
	}

	/**
	 * 
	 * @param objStr
	 * @return
	 */
	public static long getLongValue(Object objStr) {
		long result = -1;
		if (objStr != null) {
			try {
				result = Long.valueOf(objStr.toString().trim());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 
	 * @param objStr
	 * @return
	 */
	public static int getIntValue(Object objStr) {
		int result = 0;
		if (!isEmpty(objStr)) {
			try {
				result = Integer.valueOf(objStr.toString().trim());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}
