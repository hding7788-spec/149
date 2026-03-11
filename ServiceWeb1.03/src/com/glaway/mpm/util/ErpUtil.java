package com.glaway.mpm.util;

import java.util.Map;

import com.glaway.mpm.pbom.db.Wzk;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;


public class ErpUtil {

	//ERP WZK 操作类型
	public static final int ERP_OP_TYPE_ALL = 0;
	public static final int ERP_OP_TYPE_WLBM = 1;
	public static final int ERP_OP_TYPE_CLBM = 2;
	public static final int ERP_OP_TYPE_CJLBJ = 3;
	public static final int ERP_OP_TYPE_GENBOM = 10;
	public  static final String ATTRIBUTE_LINK_812_PREFIX = "link_attr_812";

	public static String getErpOptypeDesc(int type){
		String ret = "";
		if(type==1){
			ret = "查询填写元器件/紧固件物资编码";
		}else if(type==2){
			ret = "查询填写自制零件材料编码";
		}else if(type==3){
			ret = "查询创建元器件/紧固件节点";
		}
		return ret;
	}



	public static void setPartByWzk(Map<String, String> map,Wzk wzk,int type) {
		map.put("CLFLBM",Util.formateString ( wzk.getInvclasscode())); //材料分类编码

		String wzlb = wzk.getWzlb();
		if("01".equals(wzlb)||"02".equals(wzlb)){ // 元器件| 紧固件

		}else{
			wzlb = "03";//材料
		}

		map.put("WZLB", wzlb); // 物资类别

		if(type==ErpUtil.ERP_OP_TYPE_CLBM){
			map.put("XHPHCL", Util.formateString (wzk.getInvtype()));

			map.put("WZGG", Util.formateString ( wzk.getInvspec()));

			map.put("ZGFBZHJSTJ", Util.formateString (wzk.getDef2()));

			map.put("XXGF", Util.formateString (wzk.getDef3()));

			map.put("JLDWMC",Util.formateString ( wzk.getMeasname()));//计量单位

			map.put("ZLDJ",  Util.formateString (wzk.getDef4())); //质量等级
			map.put("FZXS",  Util.formateString (wzk.getDef5()));
			map.put("SCCJ", Util.formateString (wzk.getCustname()));//共用商
			map.put("XXGF", Util.formateString (wzk.getDef3()));
			map.put("MPCC", Util.formateString (wzk.getMpcc()));
			map.put("CLBM",  Util.formateString (wzk.getClcode())); //材料编码
			map.put("CMAT",  Util.formateString (wzk.getClName())); //材料名称
		}else if(type==ErpUtil.ERP_OP_TYPE_WLBM){
//			map.put(ATTRIBUTE_LINK_812_PREFIX + "WZBM", wzk);
		}else if(type==ErpUtil.ERP_OP_TYPE_CJLBJ){
//			map.put(ATTRIBUTE_LINK_812_PREFIX + "WZBM", wzk);
		}


	}

	/**
	 * 根据关键字获取阶段
	 * @param key
	 * @return
	 */
	public static String getPhase(String key){
		String ret = "";
		if(CmCommonStringUtil.isEmpty(key))
			return ret;
		if("M".equals(key)){
			ret = "1";
		} else 	if("C".equals(key)){
			ret = "2";
		} else 	if("Z".equals(key)){
			ret = "3";
		}

		return ret;
	}
}
