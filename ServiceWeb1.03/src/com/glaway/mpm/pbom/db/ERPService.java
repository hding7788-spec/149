package com.glaway.mpm.pbom.db;

import com.glaway.mpm.pbom.db.cache.CacheEngine;
import com.glaway.mpm.pbom.db.cache.Cacheable;
import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.LoadConfig;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ERPService implements Cacheable {
	private static final String CACHE_PREFIX = "DB";
	private static final String CACHE_WZK_CLASS_PREFIX = "DB_ClASS";
	private static final String CACHE_WZK_ID = "WZK_ID";
	private static CacheEngine cache;
	private static CacheEngine cacheAdd;

	/*
	 *  01：元器件
		02：标准紧固件
		03：金属材料
		04：非金属材料
		05：复合材料
		06：机电产品
		07：火工品
	 */

	@Override
	public void setCacheEngine(CacheEngine engine) {
		cache = engine;
	}

	public void setCacheEngineAdd(CacheEngine engine) {
		cacheAdd = engine;
	}


	public synchronized static void refreshCacheWzk(String dbtype,String wzlb) {
		Object obj = cache.get(CACHE_PREFIX+dbtype, wzlb);
		if(obj==null){

		}else{
			cache.remove(CACHE_PREFIX+dbtype, wzlb);
		}
		List<Wzk> list = ErpDao.queryWzk(dbtype,wzlb);
		if(list!=null)
		cache.add(CACHE_PREFIX+dbtype, wzlb, new LinkedList(list));

	}

	public synchronized static void loadWzk() {

		List<Wzk> list = null;
		String [] dbtypes = LoadConfig.getInstance().getDbTypes();
		String[] wzkTypes = LoadConfig.getInstance().getWzkTypes();
		if(wzkTypes==null||dbtypes==null)
			return;
		GLLogger.debug("开始查询数据");
		for(int i=0;i<dbtypes.length;i++){
			GLLogger.debug("数据库："+dbtypes[i]);
			for(int j=0;j<wzkTypes.length;j++){
				GLLogger.debug("存货类别："+wzkTypes[j]);
				list = ErpDao.queryWzk(dbtypes[i],wzkTypes[j]);
				if(list!=null){
					cache.add(CACHE_PREFIX+dbtypes[i], wzkTypes[j], new LinkedList(list));
					for(Wzk wzk:list){
						cache.add(CACHE_WZK_ID+dbtypes[i], wzk.getInvcode(),wzk);
					}
				}
			}
		}
		GLLogger.debug("完成查询数据");
	}

	public synchronized static void loadWzkAdd() {

		List<Wzk> list = null;
		String [] dbtypes = LoadConfig.getInstance().getDbTypes();
		String[] wzkTypes = LoadConfig.getInstance().getWzkTypes();
		if(wzkTypes==null||dbtypes==null)
			return;
		GLLogger.debug("开始查询新增数据");
		for(int i=0;i<dbtypes.length;i++){
			GLLogger.debug("数据库："+dbtypes[i]);
			for(int j=0;j<wzkTypes.length;j++){
				GLLogger.debug("存货类别："+wzkTypes[j]);
				list = ErpDao.queryWzkAdd(dbtypes[i],wzkTypes[j]);
				if(list!=null&&list.size()>0){
					GLLogger.debug("查询到新增数据："+list);
					cacheAdd.add(CACHE_PREFIX+dbtypes[i], wzkTypes[j], new LinkedList(list));
					for(Wzk wzk:list){
						GLLogger.debug("查询到新增物资："+wzk.getInvcode());
						cacheAdd.add(CACHE_WZK_ID+dbtypes[i], wzk.getInvcode(),wzk);
					}
				}
			}
		}
		GLLogger.debug("完成查询新增数据");
	}

	public Wzk getWzkByInvcode(String dbtype,String invcode){
		Wzk wzk = null;
		Object obj = cache.get(CACHE_WZK_ID+dbtype,invcode);
		if(obj == null){
			wzk = ErpDao.getWzkByInvcode(dbtype, invcode);
			if(wzk!=null){
				cache.add(CACHE_WZK_ID+dbtype, wzk.getInvcode(),wzk);
			}
		}else{
			wzk = (Wzk)obj;
		}

		return wzk;
	}

	public synchronized static void loadWzkClass(){
		String [] dbtypes = LoadConfig.getInstance().getDbTypes();
		for(int i = 0;i<dbtypes.length;i++){
			String dbtype = dbtypes[i];
			List<String> list = ErpDao.queryWzkClass(dbtype);
			for(int j=0;j<list.size();j++){
				cache.add(CACHE_WZK_CLASS_PREFIX+"_"+dbtype, list.get(j), ErpDao.genFolderByWzkClass(dbtype, list.get(j)) );
			}
		}

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
	public List<Wzk> queryWzk(String dbtype,String wzlb,String wzbm,String wzmc,String xhphcl,String gg,
			String jstj,String sccj, String zjldw,String fjtj, String lwgggccc,String jxxndj,String zldj,String fzxs,String jddj){
		System.out.println("dbtype="+dbtype + ",wzlb="+wzlb + ",wzbm="+wzbm +",wzmc="+wzmc
				+", xhphcl="+xhphcl+", gg="+gg + ",jstj="+jstj + ", sccj="+sccj+ ",zjldw="+zjldw+",fjtj="+fjtj
				+", lwgggccc="+lwgggccc + ",jxxndj="+jxxndj + ", zldj="+zldj+ ",fzxs="+fzxs+",jddj="+jddj);
		List<Wzk> dataList = null;
		dataList = (List<Wzk>) cache.get(CACHE_PREFIX+dbtype, wzlb);
		return filterWzk(dataList, wzbm, wzmc, xhphcl, gg, jstj, sccj, zjldw, fjtj,lwgggccc,jxxndj,zldj,fzxs,jddj);
	}
	public List<Wzk> queryWzk2(String dbtype,String wzlb,String wzbm,String wzmc,String xhphcl,String gg,
			String jstj,String sccj, String zjldw,String fjtj, String lwgggccc,String jxxndj,String zldj,String fzxs,String jddj){
		System.out.println("dbtype="+dbtype + ",wzlb="+wzlb + ",wzbm="+wzbm +",wzmc="+wzmc
				+", xhphcl="+xhphcl+", gg="+gg + ",jstj="+jstj + ", sccj="+sccj+ ",zjldw="+zjldw+",fjtj="+fjtj
				+", lwgggccc="+lwgggccc + ",jxxndj="+jxxndj + ", zldj="+zldj+ ",fzxs="+fzxs+",jddj="+jddj);
		List<Wzk> dataList = null;
		List<Wzk> dataListAdd = null;
		dataList = (List<Wzk>) cache.get(CACHE_PREFIX+dbtype, wzlb);
		if(cacheAdd!=null){
			dataListAdd = (List<Wzk>) cacheAdd.get(CACHE_PREFIX+dbtype, wzlb);
		}
		return filterWzk2(dataList, wzbm, wzmc, xhphcl, gg, jstj, sccj, zjldw, fjtj,lwgggccc,jxxndj,zldj,fzxs,jddj,dataListAdd);
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
	 * @param gyztrcl 供应状态/热处理
	 * @return List<Wzk> 物资列表
	 */
	public List<Wzk> queryWzk2(String dbtype,String wzlb,String wzbm,String wzmc,String xhphcl,String gg,
			String jstj,String sccj,String zjldw,String fjtj,String gyztrcl){
		System.out.println("dbtype="+dbtype + ",wzlb="+wzlb + ",wzbm="+wzbm +",wzmc="+wzmc
				+", xhphcl="+xhphcl+", gg="+gg + ",jstj="+jstj + ", sccj="+sccj+ ",zjldw="+zjldw+",fjtj="+fjtj
				+", gyztrcl="+gyztrcl);
		List<Wzk> dataList = null;
		List<Wzk> dataListAdd = null;
		dataList = (List<Wzk>) cache.get(CACHE_PREFIX+dbtype, wzlb);
		if(cacheAdd!=null){
			dataListAdd = (List<Wzk>) cacheAdd.get(CACHE_PREFIX+dbtype, wzlb);
		}
		return filterWzk2(dataList, wzbm, wzmc, xhphcl, gg, jstj, sccj, zjldw, fjtj,gyztrcl,dataListAdd);
	}

	/**
	 * 查询	原材料/试件原材料	物资
	 * @param dbtype 数据库类型 01：优选物资库    02：ERP物资库
	 * @param wzlb 物资类别  03：金属材料 04：非金属材料 05：复合材料 08：劳防、文版用品 0：全部
	 * @param wzbm 物资编码
	 * @param wzmc 物资名称
	 * @param xhphcl 型号/牌号/材料
	 * @param gg 规格
	 * @param jstj 技术条件
	 * @param sccj 生产厂家
	 * @param zjldw 主计量单位
	 * @param fjtj 附加条件
	 * @param gyztrcl 供应状态/热处理
	 * @return List<Wzk> 物资列表
	 */
	public List<Wzk> queryWzk3(String dbtype,String wzlb,String wzbm,String wzmc,String xhphcl,String gg,
							   String jstj,String sccj,String zjldw,String fjtj,String gyztrcl){
		System.out.println("dbtype="+dbtype + ",wzlb="+wzlb + ",wzbm="+wzbm +",wzmc="+wzmc
				+", xhphcl="+xhphcl+", gg="+gg + ",jstj="+jstj + ", sccj="+sccj+ ",zjldw="+zjldw+",fjtj="+fjtj
				+", gyztrcl="+gyztrcl);
		List<Wzk> dataList = null;
		List<Wzk> dataListAdd = null;
		if("0".equals(wzlb)){
			dataList = new ArrayList<Wzk>();
			String[] wzlbAll = {"0301","0302","0303","0401","0402","0403","0404","0405","0406","0407","0408","0409","0410","0411","0412","0413","0414","0501","0502","0503","08"};
			for(String wzlbStr : wzlbAll){
				if(cache.get(CACHE_PREFIX+dbtype, wzlbStr) != null){
					dataList.addAll((List<Wzk>) cache.get(CACHE_PREFIX+dbtype, wzlbStr));
				}
				if (cacheAdd != null && cacheAdd.get(CACHE_PREFIX+dbtype, wzlbStr) != null) {
					dataList.addAll((List<Wzk>) cacheAdd.get(CACHE_PREFIX + dbtype, wzlbStr));
				}
			}
		}else{
			dataList = (List<Wzk>) cache.get(CACHE_PREFIX+dbtype, wzlb);
			if (cacheAdd != null) {
				dataListAdd = (List<Wzk>) cacheAdd.get(CACHE_PREFIX + dbtype, wzlb);
			}
		}
		return filterWzk2(dataList, wzbm, wzmc, xhphcl, gg, jstj, sccj, zjldw, fjtj,gyztrcl,dataListAdd);
	}

	private List<Wzk> filterWzk(List<Wzk> dataList,String wzbm,String wzmc,String xhphcl,String gg,String jstj,
			String sccj, String zjldw,String fjtj, String lwgggccc,String jxxndj,String zldj,String fzxs,String jddj){
		List<Wzk> retList = new ArrayList<Wzk>();
		if(dataList==null)
			return retList;
		for(Wzk wzk:dataList){
			boolean wzbmFlag = checkValue(wzk.getInvcode(),wzbm);

			boolean wzmcFlag = checkValue(wzk.getInvname(),wzmc);

			boolean xhphclFlag = checkValue(wzk.getInvtype(),xhphcl);

			boolean ggFlag = checkValue(wzk.getInvspec(),gg);

			boolean jstjFlag = checkValue(wzk.getJsgfbz(),jstj);

			boolean sccjFlag = checkValue(wzk.getCustname(),sccj);

			boolean zjldwFlag = checkValue(wzk.getMeasname(),zjldw);

			boolean fjtjFlag = checkValue(wzk.getFjtjname(),fjtj);

			boolean lwgggcccFlag = checkValue(wzk.getDef12(),lwgggccc);

			boolean jxxndjFlag = checkValue(wzk.getDef14(),jxxndj);

			boolean zldjFlag = checkValue(wzk.getZldj(),zldj);

			boolean fzxsFlag = checkValue(wzk.getDef6(),fzxs);

			boolean jddjFlag = checkValue(wzk.getDef8(),jddj);

			if(wzbmFlag&&wzmcFlag&&xhphclFlag&&ggFlag&&jstjFlag&&sccjFlag&&zjldwFlag&&fjtjFlag
					&&lwgggcccFlag&&jxxndjFlag&&zldjFlag&&fzxsFlag&&jddjFlag) {
				retList.add(wzk);
			}
		}
		return retList;
	}

	private List<Wzk> filterWzk2(List<Wzk> dataList,String wzbm,String wzmc,String xhphcl,String gg,String jstj,
								String sccj, String zjldw,String fjtj, String lwgggccc,String jxxndj,String zldj,String fzxs,String jddj,List<Wzk> dataListAdd){
		List<Wzk> retList = new ArrayList<Wzk>();
		if(dataList==null)
			return retList;
		for(Wzk wzk:dataList){
			boolean wzbmFlag = checkValue(wzk.getInvcode(),wzbm);

			boolean wzmcFlag = checkValue(wzk.getInvname(),wzmc);

			boolean xhphclFlag = checkValue(wzk.getInvtype(),xhphcl);

			boolean ggFlag = checkValue(wzk.getInvspec(),gg);

			boolean jstjFlag = checkValue(wzk.getJsgfbz(),jstj);

			boolean sccjFlag = checkValue(wzk.getCustname(),sccj);

			boolean zjldwFlag = checkValue(wzk.getMeasname(),zjldw);

			boolean fjtjFlag = checkValue(wzk.getFjtjname(),fjtj);

			boolean lwgggcccFlag = checkValue(wzk.getDef12(),lwgggccc);

			boolean jxxndjFlag = checkValue(wzk.getDef14(),jxxndj);

			boolean zldjFlag = checkValue(wzk.getZldj(),zldj);

			boolean fzxsFlag = checkValue(wzk.getDef6(),fzxs);

			boolean jddjFlag = checkValue(wzk.getDef8(),jddj);

			if(wzbmFlag&&wzmcFlag&&xhphclFlag&&ggFlag&&jstjFlag&&sccjFlag&&zjldwFlag&&fjtjFlag
					&&lwgggcccFlag&&jxxndjFlag&&zldjFlag&&fzxsFlag&&jddjFlag) {
				retList.add(wzk);
			}
		}
		if(dataListAdd != null){
			for (Wzk wzk : dataListAdd) {
				boolean wzbmFlag = checkValue(wzk.getInvcode(), wzbm);

				boolean wzmcFlag = checkValue(wzk.getInvname(), wzmc);

				boolean xhphclFlag = checkValue(wzk.getInvtype(), xhphcl);

				boolean ggFlag = checkValue(wzk.getInvspec(), gg);

				boolean jstjFlag = checkValue(wzk.getJsgfbz(), jstj);

				boolean sccjFlag = checkValue(wzk.getCustname(), sccj);

				boolean zjldwFlag = checkValue(wzk.getMeasname(), zjldw);

				boolean fjtjFlag = checkValue(wzk.getFjtjname(), fjtj);

				boolean lwgggcccFlag = checkValue(wzk.getDef12(), lwgggccc);

				boolean jxxndjFlag = checkValue(wzk.getDef14(), jxxndj);

				boolean zldjFlag = checkValue(wzk.getZldj(), zldj);

				boolean fzxsFlag = checkValue(wzk.getDef6(), fzxs);

				boolean jddjFlag = checkValue(wzk.getDef8(), jddj);

				if (wzbmFlag && wzmcFlag && xhphclFlag && ggFlag && jstjFlag && sccjFlag && zjldwFlag && fjtjFlag
						&& lwgggcccFlag && jxxndjFlag && zldjFlag && fzxsFlag && jddjFlag) {
					retList.add(wzk);
				}
			}
		}
		return retList;
	}

	private boolean checkValue(String erpValue,String par) {
		if(par == null || "".equals(par)) {//如果条件为空，则全部显示
			return true;
		} else if (erpValue==null || "".equals(erpValue)) {//如果条件不为空，而数据库字段为空，则不匹配，继续下一条匹配
			return false;
		} else if(erpValue.indexOf(par)!=-1){//如果都不为空，且数据库字段包含查询条件，则表示匹配，返回此条数据
			return true;
		} else {//都不为空且不匹配，则继续匹配下一条数据
			return false;
		}
	}

	private List<Wzk> filterWzk2(List<Wzk> dataList,String wzbm,String wzmc,String xhphcl,String gg,String jstj,
			String sccj, String zjldw,String fjtj, String gyztrcl,List<Wzk> dataListAdd){
		List<Wzk> retList = new ArrayList<Wzk>();
		if(dataList==null)
			return retList;
		for(Wzk wzk:dataList){
			boolean wzbmFlag = checkValue(wzk.getInvcode(),wzbm);

			boolean wzmcFlag = checkValue(wzk.getInvname(),wzmc);

			boolean xhphclFlag = checkValue(wzk.getInvtype(),xhphcl);

			boolean ggFlag = checkValue(wzk.getInvspec(),gg);

			boolean jstjFlag = checkValue(wzk.getJsgfbz(),jstj);

			boolean sccjFlag = checkValue(wzk.getCustname(),sccj);

			boolean zjldwFlag = checkValue(wzk.getMeasname(),zjldw);

			boolean fjtjFlag = checkValue(wzk.getFjtjname(),fjtj);

			boolean gyztrclFlag = checkValue(wzk.getDef13(),gyztrcl);


			if(wzbmFlag&&wzmcFlag&&xhphclFlag&&ggFlag&&jstjFlag&&sccjFlag
					&&zjldwFlag&&fjtjFlag&&gyztrclFlag) {
				retList.add(wzk);
			}
		}
		if(dataListAdd!=null){
			for(Wzk wzk:dataListAdd){
				boolean wzbmFlag = checkValue(wzk.getInvcode(),wzbm);

				boolean wzmcFlag = checkValue(wzk.getInvname(),wzmc);

				boolean xhphclFlag = checkValue(wzk.getInvtype(),xhphcl);

				boolean ggFlag = checkValue(wzk.getInvspec(),gg);

				boolean jstjFlag = checkValue(wzk.getJsgfbz(),jstj);

				boolean sccjFlag = checkValue(wzk.getCustname(),sccj);

				boolean zjldwFlag = checkValue(wzk.getMeasname(),zjldw);

				boolean fjtjFlag = checkValue(wzk.getFjtjname(),fjtj);

				boolean gyztrclFlag = checkValue(wzk.getDef13(),gyztrcl);


				if(wzbmFlag&&wzmcFlag&&xhphclFlag&&ggFlag&&jstjFlag&&sccjFlag
						&&zjldwFlag&&fjtjFlag&&gyztrclFlag) {
					retList.add(wzk);
				}
			}
		}
		return retList;
	}

	public String genFolderByWzkClass(String dbtype,String wzklb){
		String floder = (String) cache.get(CACHE_WZK_CLASS_PREFIX+"_"+dbtype, wzklb);
		return floder;
	}
}
