package ext.casc.integrate.product;

import wt.util.WTException;

public interface IProductInfo {
	/*获取最新版本的图号信息(自制件)
	 * number:产品图号
	 * versionType:版本类型
	 * version:版本值
	 * productName:型号标识
	 * ismark:是否关联成套件
	 * bomType:bom类别-Design/Manufacturing
	 * */
	public String getBomProductInfo(String number,String versionType,String version,String productName,int ismark,String bomType,String guid) throws WTException;

}
