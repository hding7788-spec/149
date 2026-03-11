package ext.casc.integrate.version;

import wt.util.WTException;

public interface IProductVersion {
	//获取产品相关的批次信息
	public String getAllBatches(String number,String bomType,String guid) throws WTException;
}
