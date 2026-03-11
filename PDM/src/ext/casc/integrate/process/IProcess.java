package ext.casc.integrate.process;

import java.rmi.RemoteException;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public interface IProcess {
	/*获取工艺路线信息
	 * processplanType:工艺规程类型（主工艺、临时工艺、全部工艺）
	 * productNumber:产品编号（图号）
	 * productVersionType:	产品版本类型
	 * productVersion:产品版本
	 * processPlanNumber:工艺文件编号
	 * processPlanversionType:文件版本类型
	 * processPlanversion:文件版本
	 * guid:用户验证码
	 * */
	public  String getProcessPlan(String processplanType,String productNumber,String productVersionType,String productVersion,
			String processPlanNumber,String processPlanversionType,String processPlanversion,String guid) throws WTPropertyVetoException, WTException, RemoteException;


	/*获取工艺文件目录
	 * number:产品编号（图号）
	 * versionType:产品版本类型
	 * version:产品版本
	 * fileLevel:设计文件目录的展示层级
	 * fileTypeA:目录文件对象的类型
	 * fileTypeB:目录文件具体子类型
	 * guid:用户验证码
	 * */
	public String getProcessFileDirectory(String number,String versionType,String version,int fileLevel,
			String fileTypeA,String fileTypeB,String guid);
}

