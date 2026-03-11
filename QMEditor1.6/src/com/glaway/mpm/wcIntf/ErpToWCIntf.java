package com.glaway.mpm.wcIntf;

import com.glaway.mpm.pbom.db.Wzk;
import wt.method.RemoteMethodServer;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;

public class ErpToWCIntf {

	public static void main(String[] args){

	}
	/**
	 * 查询物资
	 * @param dbtype 数据库类型 01：优选物资库    02：ERP物资库
	 * @param wzlb 物资类别  01：元器件  02：标准紧固件 03：金属材料 04：非金属材料 05：复合材料 06：机电产品 07：火工品
	 * @param wzbm 物资编码
	 * @param wzmc 物资名称
	 * @param xhphcl 型号/牌号/材料
	 * @param gg 规格
	 * @param jstj 技术条件
	 * @param xxgf 详细规范
	 * @param zldj 质量等级
	 * @param gys 公用商
	 * @return List<Wzk> 物资列表
	 */
	public static List<Wzk> queryWzk(String dbtype,String wzlb,String wzbm,String wzmc,String xhphcl,String gg,String jstj,String xxgf,String zldj,String gys){
		return (List<Wzk>) remoteMetnodInvoke("queryWzk", new Class[] {String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class},new Object[] {   dbtype, wzlb, wzbm, wzmc, xhphcl, gg, jstj, xxgf, zldj, gys });

	}

	/**
	 * 查询物资
	 * @param dbtype 数据库类型 01：优选物资库    02：ERP物资库
	 * @param wzlb 物资类别  01：元器件  02：标准紧固件 03：金属材料 04：非金属材料 05：复合材料 06：机电产品 07：火工品
	 * @param wzbm 物资编码
	 * @param wzmc 物资名称
	 * @param xhphcl 型号/牌号/材料
	 * @param gg 规格
	 * @param jstj 技术条件
	 * @param sccj 生产厂家
	 * @param zjldw 主计量单位
	 * @param fjtj 附加条件
	 * @param lwgggccc 螺纹规格/公称尺寸
	 * @param jxxndj 机械性能等级
	 * @param zldj 质量等级
	 * @param fzxs 封装形式
	 * @param jddj 精度等级
	 * @return List<Wzk> 物资列表
	 */
	public static List<Wzk> queryWzk(String dbtype,String wzlb,String wzbm,String wzmc,
			String xhphcl,String gg,String jstj,String sccj, String zjldw,String fjtj,
			String lwgggccc,String jxxndj,String zldj,String fzxs,String jddj){
		Class[] cls = new Class[] {String.class,String.class,String.class,String.class,String.class,
				String.class,String.class,String.class,String.class,String.class,
				String.class,String.class,String.class,String.class,String.class};
		Object[] obj = new Object[] { dbtype, wzlb, wzbm, wzmc, xhphcl, gg, jstj, sccj, zjldw,fjtj,lwgggccc,jxxndj, zldj,fzxs,jddj };
		return (List<Wzk>) remoteMetnodInvoke("queryWzk2", cls, obj);

	}

	/**
	 * 更加物资编码查询物资
	 * @param dbtype
	 * @param invcode
	 * @return
	 */
	public static Wzk getWzkByInvcode(String dbtype,String invcode){
		return (Wzk) remoteMetnodInvoke("getWzkByInvcode" , new Class[] {String.class,String.class}  , new  Object[] {dbtype,invcode} );
	}

	private static Object remoteMetnodInvoke(String methodName, Class[] classArray, Object[] objectArray) {
		Object object = null;
		RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
		try {
			object = methodServer.invoke(methodName, "com.glaway.mpm.intf.ERPToWCInfRMI", null, classArray,
					objectArray);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return object;
	}

}
