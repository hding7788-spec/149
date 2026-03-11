package com.glaway.mpm.sjzyk;

import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.WTPartUtil;
import ext.ases.techMaterial.TechnicsMaterialEntries;
import ext.ases.techMaterial.util.TechnicsMaterialUtils;
import ext.casc.util.DBConn;
import ext.casc.util.Tools;
import ext.casc.util.WCUtil;
import ext.casc.workflow.WorkflowHelper;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.lifecycle.LifeCycleState;
import wt.lifecycle.State;
import wt.method.RemoteAccess;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.pds.oracle81.OracleDataSource;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;

import java.sql.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.*;

public class SjzykSchedule  implements RemoteAccess {

	private static final String[] CONTAINERS = {"八院标准紧固件库","八院元器件库","八院金属材料库","八院非金属材料库","八院复合材料库"};
	private static final String[] MTYPE = {"标准件","元器件","金属材料","非金属材料","复合材料"};
	// 标准紧固件
	public static LinkedHashMap<String, String> standardMap;
	// 电子元器件
	public static LinkedHashMap<String, String> eleComponentsMap;
	// 非金属材料
	public static LinkedHashMap<String, String> nonmetallicMap;
	// 复合材料
	public static LinkedHashMap<String, String> compoundMaterialMap;
	// 金属材料
	public static LinkedHashMap<String, String> metallicMap;
	// 机电产品
	public static LinkedHashMap<String, String> jidianMap;
	// 火工品
	public static LinkedHashMap<String, String> huogongpinMap;
	static{
		//标准件
		standardMap = new LinkedHashMap<String, String>();
		standardMap.put("WZJC", "SHORTNAME");
		standardMap.put("GG", "CSIZE");
		standardMap.put("BZH", "STANDARDNUMBER");
		standardMap.put("JXXNDJ", "MECHANICALPROPERTYORHARDNESS");
		standardMap.put("JLDW", "MEASUREUNIT");
		standardMap.put("CL", "CMAT");
		standardMap.put("BMCL", "SURFACETREATMENT");
		standardMap.put("RCL", "HEATTREATMENT");
		standardMap.put("SCCJ", "SUPPLIERS");
		standardMap.put("CPXS", "PRODUCTFORM");
		standardMap.put("CPDJ", "PRODUCTLEVEL");
		standardMap.put("NBXS", "PLATECSCREWFORM");
		standardMap.put("TSSM", "SPECIALINSTRUCTION");
		standardMap.put("SFJK", "ISIMPORT");
		standardMap.put("BMYXJB", "YXJB");
		standardMap.put("BMZT", "BMZT");
		standardMap.put("BMLX", "BMLX");
		standardMap.put("BMDJ", "BMDJ");
		standardMap.put("WZFL","ClassificationNode");
		//元器件
		eleComponentsMap = new LinkedHashMap<String, String>();
		eleComponentsMap.put("WZJC", "SHORTNAME");
		eleComponentsMap.put("XHGG", "TYPESTANDARD");
		eleComponentsMap.put("ZLDJ", "QUALITYLEVEL");
		eleComponentsMap.put("SCCJ", "SUPPLIERS");
		eleComponentsMap.put("JLDW", "MEASUREUNIT");
		eleComponentsMap.put("ZGF", "TOTALSTANDARD");
		eleComponentsMap.put("XXGF", "DETAILSTANDARD");
		eleComponentsMap.put("XH", "TYPE");
		eleComponentsMap.put("FZXS", "PACKAGINGFORM");
		eleComponentsMap.put("WXCC", "OUTLINESIZE");
		eleComponentsMap.put("ZYTJ", "SPECIALCONDITION");
		eleComponentsMap.put("FJXY", "EXTRACONDITION");
		eleComponentsMap.put("TSSM", "SPECIALINSTRUCTION");
		eleComponentsMap.put("SFJK", "ISIMPORT");
		eleComponentsMap.put("KFSZBTID", "KFZBTID");
		eleComponentsMap.put("KFSZBSEE", "KFZBSEE");
		eleComponentsMap.put("XNCS", "XNCS");
		eleComponentsMap.put("SFJDMG", "JDMGDJ_STATE");
		eleComponentsMap.put("JDMGDJ", "JDMGDJ");
		eleComponentsMap.put("SMDJ", "SMDJ");
		eleComponentsMap.put("BMYXJB", "YXJB");
		eleComponentsMap.put("BMZT", "BMZT");
		eleComponentsMap.put("BMLX", "BMLX");
		eleComponentsMap.put("BMDJ", "BMDJ");
		eleComponentsMap.put("WZFL","ClassificationNode");
		//金属材料
		metallicMap = new LinkedHashMap<String, String>();
		metallicMap.put("WZJC", "SHORTNAME");
		metallicMap.put("PH", "MARKNUMBER");
		metallicMap.put("GG", "CSIZE");
		metallicMap.put("GYZT", "SUPPLYSTATE");
		metallicMap.put("CYBZ", "USESTANDARD");
		metallicMap.put("JLDW", "MEASUREUNIT");
		metallicMap.put("PZGGBZ", "VARIETYSTANDARD");
		metallicMap.put("JD", "PRECISION");
		metallicMap.put("ZLTZ", "QUALITYCHARACTER");
		metallicMap.put("SCCJ", "SUPPLIERS");
		metallicMap.put("TSSM", "SPECIALINSTRUCTION");
		metallicMap.put("SFJK", "ISIMPORT");
		metallicMap.put("HSL", "RATEOFCONVERSION");
		metallicMap.put("XS", "RATIO");
		metallicMap.put("BMYXJB", "YXJB");
		metallicMap.put("BMZT", "BMZT");
		metallicMap.put("BMLX", "BMLX");
		metallicMap.put("BMDJ", "BMDJ");
		metallicMap.put("WZFL","ClassificationNode");
		//非金属
		nonmetallicMap = new LinkedHashMap<String, String>();
		nonmetallicMap.put("WZJC", "SHORTNAME");
		nonmetallicMap.put("PH", "MARKNUMBER");
		nonmetallicMap.put("GG", "CSIZE");
		nonmetallicMap.put("CYBZ", "USESTANDARD");
		nonmetallicMap.put("JLDW", "MEASUREUNIT");
		nonmetallicMap.put("SCCJ", "SUPPLIERS");
		nonmetallicMap.put("TSSM", "SPECIALINSTRUCTION");
		nonmetallicMap.put("SFJK", "ISIMPORT");
		nonmetallicMap.put("HSL", "RATEOFCONVERSION");
		nonmetallicMap.put("XS", "RATIO");
		nonmetallicMap.put("BMYXJB", "YXJB");
		nonmetallicMap.put("BMZT", "BMZT");
		nonmetallicMap.put("BMLX", "BMLX");
		nonmetallicMap.put("BMDJ", "BMDJ");
		nonmetallicMap.put("WZFL","ClassificationNode");
		//复合材料
		compoundMaterialMap = new LinkedHashMap<String, String>();
		compoundMaterialMap.put("WZJC", "SHORTNAME");
		compoundMaterialMap.put("PH", "MARKNUMBER");
		compoundMaterialMap.put("GG", "CSIZE");
		compoundMaterialMap.put("CYBZ", "USESTANDARD");
		compoundMaterialMap.put("JLDW", "MEASUREUNIT");
		compoundMaterialMap.put("SCCJ", "SUPPLIERS");
		compoundMaterialMap.put("TSSM", "SPECIALINSTRUCTION");
		compoundMaterialMap.put("SFJK", "ISIMPORT");
		compoundMaterialMap.put("HSL", "RATEOFCONVERSION");
		compoundMaterialMap.put("XS", "RATIO");
		compoundMaterialMap.put("BMYXJB", "YXJB");
		compoundMaterialMap.put("BMZT", "BMZT");
		compoundMaterialMap.put("BMLX", "BMLX");
		compoundMaterialMap.put("BMDJ", "BMDJ");
		compoundMaterialMap.put("WZFL","ClassificationNode");
		//机电材料
		jidianMap = new LinkedHashMap<String, String>();
		jidianMap.put("BMDJ", "BMDJ");
		jidianMap.put("XHGG", "TYPESTANDARD");
		jidianMap.put("JLDW", "MEASUREUNIT");
		jidianMap.put("SFJK", "ISIMPORT");
		jidianMap.put("BZH", "STANDARDNUMBER");
		jidianMap.put("PH", "MARKNUMBER");
		jidianMap.put("SCCJ", "SUPPLIERS");
		jidianMap.put("XNCS", "XNCS");
		jidianMap.put("TSSM", "SPECIALINSTRUCTION");
		jidianMap.put("BMZT", "BMZT");
		jidianMap.put("WZFL","ClassificationNode");
		//火工品
		huogongpinMap = new LinkedHashMap<String, String>();
		huogongpinMap.put("BMDJ", "BMDJ");
		huogongpinMap.put("CPDH", "PINDEX");
		huogongpinMap.put("JLDW", "MEASUREUNIT");
		huogongpinMap.put("BZH", "STANDARDNUMBER");
		huogongpinMap.put("SCCJ", "SUPPLIERS");
		huogongpinMap.put("ZL", "WEIGHT");
		huogongpinMap.put("ZCSM", "STORAGELIFE");
		huogongpinMap.put("TNT", "TNT");
		huogongpinMap.put("XNCS", "XNCS");
		huogongpinMap.put("TSSM", "SPECIALINSTRUCTION");
		huogongpinMap.put("BMZT", "BMZT");
		huogongpinMap.put("WZFL","ClassificationNode");
	}

	/**
	 * 设计资源库同步时调用该接口以更新中间表数据信息
	 *
	 * @param part
	 */
	public static void updateSjzykMiddleTable(WTPart part) {
		try {
			IBAHelper ibaHelper = new IBAHelper(part);
			String fl = object2String(ibaHelper.getIBAValue("ClassificationNode"));
			//更新的零部件必须是标准件、元器件、金属材料、非金属材料或复合材料
			if(!fl.startsWith("01")
					&& !fl.startsWith("02")
					&& !fl.startsWith("03")
					&& !fl.startsWith("04")
					&& !fl.startsWith("05")
					&& !fl.startsWith("06")
					&& !fl.startsWith("07")) {
				System.out.println("updateSjzykMiddleTable part " + part.getNumber() + " is not start with 01|02|03|04|05|06|07");
				return ;
			}

			//List<List<String>> oldList = getOldGlCatalogName(part.getNumber());
			List<String> newList = getNewGlCatalogName(part.getNumber());
			List beans = new ArrayList();
			delete(part.getNumber());
			String synchTime = String.valueOf(System.currentTimeMillis());
			if(newList != null && !newList.isEmpty()) {
				SjzykBean bean = null;
				for(String xyml:newList) {
					//插入新的数据
					bean = getSjzykBean(xyml, part,synchTime);
					beans.add(bean);
				}
			} else {
				//插入新的数据
				SjzykBean bean = getSjzykBean("", part,synchTime);
				beans.add(bean);
			}
			//向中间表插入数据
			insertData(beans);
		} catch (WTException e) {
			e.printStackTrace();
		}
	}


	public  static void delete( String wtpartnumber) {
		DBConn conn = null;
		try {
			conn = new DBConn();
			StringBuffer sql = new StringBuffer();
			sql.append("delete from QUERYMIDDLE_TABLE t where ");
			sql.append("  t.wtpartnumber='"+wtpartnumber+"'");
			conn.executeUpdate(sql.toString());
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(conn!=null){
					conn.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}


	}

	public  static SjzykBean getSjzykBean(String xyml, WTPart part,String synchTime) throws WTException {
		IBAHelper ibaHelper = new IBAHelper(part);
		SjzykBean bean = new SjzykBean();
		bean.setXyml(xyml);
    	bean.setSjbm(part.getNumber());
    	bean.setName(part.getName());
    	bean.setBzh(object2String(ibaHelper.getIBAValue("STANDARDNUMBER")));
    	bean.setWzjc(object2String(ibaHelper.getIBAValue("SHORTNAME")));
    	bean.setGg(object2String(ibaHelper.getIBAValue("CSIZE")));
    	bean.setCl(object2String(ibaHelper.getIBAValue("CMAT")));
    	bean.setJxxndjhyd(object2String(ibaHelper.getIBAValue("MECHANICALPROPERTYORHARDNESS")));
    	bean.setBmcl(object2String(ibaHelper.getIBAValue("SURFACETREATMENT")));
    	bean.setRcl(object2String(ibaHelper.getIBAValue("HEATTREATMENT")));
    	bean.setCpxs(object2String(ibaHelper.getIBAValue("PRODUCTFORM")));
    	bean.setCpdj(object2String(ibaHelper.getIBAValue("PRODUCTLEVEL")));
    	bean.setBnxs(object2String(ibaHelper.getIBAValue("PLATECSCREWFORM")));
    	bean.setSfjk(object2String(ibaHelper.getIBAValue("ISIMPORT")));
    	bean.setTssm(object2String(ibaHelper.getIBAValue("SPECIALINSTRUCTION")));
    	bean.setJldw(object2String(ibaHelper.getIBAValue("MEASUREUNIT")));
    	String fl = object2String(ibaHelper.getIBAValue("ClassificationNode"));
    	bean.setFl(fl);
    	bean.setBmyyjb(object2String(ibaHelper.getIBAValue("YXJB")));
    	bean.setBmzt(object2String(ibaHelper.getIBAValue("BMZT")));
    	bean.setBmlx(object2String(ibaHelper.getIBAValue("BMLX")));
    	bean.setXh(object2String(ibaHelper.getIBAValue("TYPE")));
    	bean.setXhgg(object2String(ibaHelper.getIBAValue("TYPESTANDARD")));
    	bean.setZldj(object2String(ibaHelper.getIBAValue("QUALITYLEVEL")));
    	bean.setZgf(object2String(ibaHelper.getIBAValue("TOTALSTANDARD")));
    	bean.setXxgf(object2String(ibaHelper.getIBAValue("DETAILSTANDARD")));
    	bean.setFzxs(object2String(ibaHelper.getIBAValue("PACKAGINGFORM")));
    	bean.setWxcc(object2String(ibaHelper.getIBAValue("OUTLINESIZE")));
    	bean.setZytj(object2String(ibaHelper.getIBAValue("SPECIALCONDITION")));
    	bean.setFjxy(object2String(ibaHelper.getIBAValue("EXTRACONDITION")));
    	bean.setCllx(object2String(ibaHelper.getIBAValue("MATTYPE")));
    	bean.setHsl(object2String(ibaHelper.getIBAValue("RATEOFCONVERSION")));
    	bean.setXs(object2String(ibaHelper.getIBAValue("RATIO")));
    	bean.setPh(object2String(ibaHelper.getIBAValue("MARKNUMBER")));
    	bean.setCybz(object2String(ibaHelper.getIBAValue("USESTANDARD")));
    	bean.setPzggbz(object2String(ibaHelper.getIBAValue("VARIETYSTANDARD")));
    	bean.setZltz(object2String(ibaHelper.getIBAValue("QUALITYCHARACTER")));
    	bean.setJd(object2String(ibaHelper.getIBAValue("PRECISION")));
    	bean.setGyzt(object2String(ibaHelper.getIBAValue("SUPPLYSTATE")));
    	bean.setGys(object2String(ibaHelper.getIBAValue("SUPPLIERS")));
    	// 同步中间表数据时增加新增属性值 -- add by hz - 20191216
    	bean.setBmdj(object2String(ibaHelper.getIBAValue("BMDJ")));
    	bean.setSynchTime(synchTime);

		bean.setKfzbtid(object2String(ibaHelper.getIBAValue("KFZBTID")));
		bean.setKfzbsee(object2String(ibaHelper.getIBAValue("KFZBSEE")));
		bean.setXncs(object2String(ibaHelper.getIBAValue("XNCS")));
		bean.setJdmgdj_state(object2String(ibaHelper.getIBAValue("JDMGDJ_STATE")));
		bean.setJdmgdj(object2String(ibaHelper.getIBAValue("JDMGDJ")));
		bean.setSmdj(object2String(ibaHelper.getIBAValue("SMDJ")));
		bean.setCpdh(object2String(ibaHelper.getIBAValue("PINDEX")));
		bean.setZl(object2String(ibaHelper.getIBAValue("WEIGHT")));
		bean.setTnt(object2String(ibaHelper.getIBAValue("TNT")));
		bean.setZcsm(object2String(ibaHelper.getIBAValue("STORAGELIFE")));

    	if(fl.startsWith("01")) {
    		bean.setMtype("元器件");
    	} else if (fl.startsWith("02")) {
    		bean.setMtype("标准件");
    	} else if (fl.startsWith("03")) {
    		bean.setMtype("金属材料");
    	} else if (fl.startsWith("04")) {
    		bean.setMtype("非金属材料");
    	} else if (fl.startsWith("05")) {
			bean.setMtype("复合材料");
		}else if (fl.startsWith("06")) {
			bean.setMtype("机电材料");
		}else if (fl.startsWith("07")) {
			bean.setMtype("火工品");
		}
    	return bean;
	}

	public static void insertData(SjzykBean bean) {
		DBConn conn = null;
		try {
			conn = new DBConn();
			StringBuffer  sql = new StringBuffer();
			sql.append("INSERT INTO QUERYMIDDLE_TABLE values('");
			sql.append(bean.getXyml()).append("','");
			sql.append(bean.getSjbm()).append("','");
			sql.append(bean.getName()).append("','");
			sql.append(bean.getBzh()).append("','");
			sql.append(bean.getWzjc()).append("','");
			sql.append(bean.getGg()).append("','");
			sql.append(bean.getCl()).append("','");
			sql.append(bean.getJxxndjhyd()).append("','");
			sql.append(bean.getBmcl()).append("','");
			sql.append(bean.getRcl()).append("','");
			sql.append(bean.getCpxs()).append("','");
			sql.append(bean.getCpdj()).append("','");
			sql.append(bean.getBnxs()).append("','");
			sql.append(bean.getSfjk()).append("','");
			sql.append(bean.getTssm()).append("','");
			sql.append(bean.getJldw()).append("','");
			sql.append(bean.getFl()).append("','");
			sql.append(bean.getBmyyjb()).append("','");
			sql.append(bean.getBmzt()).append("','");
			sql.append(bean.getBmlx()).append("','");
			sql.append(bean.getXh()).append("','");
			sql.append(bean.getXhgg()).append("','");
			sql.append(bean.getZldj()).append("','");
			sql.append(bean.getZgf()).append("','");
			sql.append(bean.getXxgf()).append("','");
			sql.append(bean.getFzxs()).append("','");
			sql.append(bean.getWxcc()).append("','");
			sql.append(bean.getZytj()).append("','");
			sql.append(bean.getFjxy()).append("','");
			sql.append(bean.getCllx()).append("','");
			sql.append(bean.getHsl()).append("','");
			sql.append(bean.getXs()).append("','");
			sql.append(bean.getPh()).append("','");
			sql.append(bean.getCybz()).append("','");
			sql.append(bean.getMtype()).append("','");
			sql.append(bean.getPzggbz()).append("','");
			sql.append(bean.getZltz()).append("','");
			sql.append(bean.getJd()).append("','");
			sql.append(bean.getGyzt()).append("','");
			sql.append(bean.getGys()).append("','");
			// 资源库中间表添加同步部件时新增属性值 -- update by hz - 20191216
			sql.append(bean.getBmdj()).append("','");
			sql.append(bean.getKfzbtid()).append("','");
			sql.append(bean.getKfzbsee()).append("','");
			sql.append(bean.getXncs()).append("','");
			sql.append(bean.getJdmgdj_state()).append("','");
			sql.append(bean.getJdmgdj()).append("','");
			sql.append(bean.getSmdj()).append("',");
			if(Tools.isNull(bean.getSynchTime())){
				sql.append(System.currentTimeMillis()).append(",'");
			}else{
				sql.append(bean.getSynchTime()).append(",'");

			}
			sql.append(bean.getCpdh()).append("','");
			sql.append(bean.getZl()).append("','");
			sql.append(bean.getTnt()).append("','");
			sql.append(bean.getZcsm()).append("')");
			conn.executeUpdate(sql.toString());
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn!=null){
					conn.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}


	}
	private static List<List<String>> getOldGlCatalogName(String wtpartnumber) {
		List<List<String>> list = new ArrayList<List<String>>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet resultset = null;
		try {
			conn = OracleDataSource.getOracleDataSource().getConnection();
			String sql = "select t.xyml,t.wtpartnumber from QUERYMIDDLE_TABLE t where t.wtpartnumber='"+wtpartnumber+"'";
			conn.setAutoCommit(false);
		    ps = conn.prepareStatement(sql,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			resultset = ps.executeQuery();
			List<String> tempList = null;
			while (resultset.next()) {
				tempList = new ArrayList<String>();
				tempList.add(object2String(resultset.getObject(1)));
				tempList.add(object2String(resultset.getObject(2)));
				list.add(tempList);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			close(ps,resultset,conn);
		}
		return list;
	}

	private static List<String> getNewGlCatalogName(String wtpartnumber) {
		DBConn conn = null;
		List<String> list = new ArrayList<String>();
		try {
			conn = new DBConn();
			StringBuffer sql = new StringBuffer();
			//选用目录直接与零部件编号关联
			sql.append("select g.glcatalogname ");
			sql.append(" from GLCATALOG g, GLPARTLINK gl ");
			sql.append(" where g.glcatalognumber= gl.catalognumber ");
			sql.append(" and gl.wtpartnumber='"+wtpartnumber+"'");
			ResultSet resultset =  conn.executeQuery(sql.toString());
			while (resultset.next()) {
				list.add(object2String(resultset.getObject(1)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
		return list;
	}

	/**
	 * 将设计资源库所有存储库中的零部件部分属性查询出来并统一写入到中间表
	 *
	 */
	public static void process() {
		System.out.println("starting query sjyzk all data ......"+DateFormat.getDateTimeInstance().format(new Date()));
		long start = System.currentTimeMillis();
		List<SjzykBean> all = new ArrayList<SjzykBean>();
		try {
			WTPart part = null;
			for (int i = 0; i < CONTAINERS.length ; i++) {
				System.out.println(CONTAINERS[i]);
				QueryResult qr = queryPartByContainer(CONTAINERS[i]);
				if(qr != null) {
					System.out.println(qr.size());
					while(qr.hasMoreElements()) {
						part = (WTPart)qr.nextElement();
						updateSjzykMiddleTable(part);
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取每个零部件编号关联的所有的选用目录<br>
	 * <li>选用目录直接与零部件编号关联<br>
	 * <li>选用目录与目录条目关联，目录条目与零部件编号关联<br>
	 *
	 * @return
	 */
	private static Map<String,Set<String>> getGlcatanameMap() {
		Map<String,Set<String>> map = new HashMap<String,Set<String>>();
		Connection conn = null;
		PreparedStatement ps= null;
		ResultSet resultset = null;
		try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);

            StringBuffer sql = new StringBuffer();
            sql.append("SELECT G.GLCATALOGNAME, GL.WTPARTNUMBER ");
            sql.append(" FROM GLCATALOG G, GLPARTLINK GL ");
            sql.append(" WHERE G.GLCATALOGNUMBER= GL.CATALOGNUMBER");
            System.out.println(sql.toString());
            ps = conn.prepareStatement(sql.toString());
            resultset = ps.executeQuery();
            String partNumber = "";
            String catalogName = "";
            Set<String> list = null;
            while (resultset.next()) {
            	catalogName = resultset.getString(1);
            	partNumber = resultset.getString(2);
            	if(map.containsKey(partNumber)) {
            		map.get(partNumber).add(catalogName);
            	} else {
            		list = new HashSet<String>();
            		list.add(catalogName);
            		map.put(partNumber, list);
            	}
            }

            StringBuffer sql2 = new StringBuffer();
            sql2.append("SELECT G.GLCATALOGNAME, GP.WTPARTNUMBER ");
            sql2.append(" FROM GLCATALOG G, GLCILINK GC,GLCIPARTLINK GP ");
            sql2.append(" WHERE G.GLCATALOGNUMBER=GC.GLCATALOGNUMBER AND GC.CIPARTNUMBER=GP.CIPARTNUMBER");
            System.out.println(sql2.toString());
            ps = conn.prepareStatement(sql2.toString());
            resultset = ps.executeQuery();
            while (resultset.next()) {
            	catalogName = resultset.getString(1);
            	partNumber = resultset.getString(2);
            	if(map.containsKey(partNumber)) {
            		map.get(partNumber).add(catalogName);
            	} else {
            		list = new HashSet<String>();
            		list.add(catalogName);
            		map.put(partNumber, list);
            	}
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
        	close(ps,resultset, conn);
		}
		return map;
	}

	private static void close(PreparedStatement ps,ResultSet resultset,Connection conn){
		if(ps!=null){
			try {
				ps.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(resultset!=null){
			try {
				resultset.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if(conn!=null){
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void close(Statement state,Connection conn){
		if(state!=null){
			try {
				state.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if(conn!=null){
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * 通过存储库名称，查询该存储库下所有的零部件(Design)
	 *
	 * @param containerName
	 * @return QueryResult
	 * @throws WTException
	 */
	public static QueryResult queryPartByContainer(String containerName) throws WTException {
		WTLibrary wtlib = WCUtil.getLibraryByName(containerName);
		View view = WTPartUtil.getViewByName("Design");
		if(wtlib != null) {
			long libId = PersistenceHelper.getObjectIdentifier(wtlib).getId();
			long viewId = PersistenceHelper.getObjectIdentifier(view).getId();
			QuerySpec qs = new QuerySpec(WTPart.class);
			int[] index = { 0 };
			SearchCondition sc = new SearchCondition(WTPart.class,"containerReference.key.id",SearchCondition.EQUAL,libId);
			qs.appendWhere(sc, index);
			qs.appendAnd();
			sc = new SearchCondition(WTPart.class,"view.key.id",SearchCondition.EQUAL,viewId);
			qs.appendWhere(sc, index);
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec)qs);
			LatestConfigSpec lcs = new LatestConfigSpec();
			qr = lcs.process(qr);
			return qr;
		} else {
			System.out.println(containerName+" is not exsit");
		}
		return null;
	}

	/**
	 * 向中间表（QUERYMIDDLE_TABLE）中插入数据
	 *
	 * @param queryList
	 */
	public static void insertData(List<SjzykBean> queryList) {
		for (SjzykBean bean : queryList) {
			insertData(bean);
		}

	}

	/**
	 * 清空中间表数据
	 *
	 */
	public static void clearData() {
		DBConn	conn = null;
		try {
			conn = new DBConn();

            StringBuffer sql = new StringBuffer();
            sql.append("DELETE FROM QUERYMIDDLE_TABLE");

			conn.executeUpdate(sql.toString());
			conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}

	public static String object2String(Object obj) {
		if(obj == null) {
			return "";
		}
		String value = obj.toString();
		if(!"".equals(value) && value.contains("'")) {
			value = value.replace("'", "''");
		}
		return value;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SjzykSchedule.process();
	}

	/**
	 * 同步工艺物资条目信息
	 *
	 * @param part
	 */
	public static void updateTechnicMaterialInfo(WTPart part) {
		try {
			IBAHelper ibaHelper = new IBAHelper(part);
			String fl = object2String(ibaHelper.getIBAValue("ClassificationNode"));
			//更新的零部件必须是标准件、元器件、金属材料、非金属材料或复合材料
			if (!fl.startsWith("01")&& !fl.startsWith("02")&& !fl.startsWith("03")&& !fl.startsWith("04")&& !fl.startsWith("05")&& !fl.startsWith("06")&& !fl.startsWith("07")) {
				System.out.println("updateSjzykMiddleTable part " + part.getNumber() + " is not start with 01|02|03|04|05|06|07");
				return;
			}
			String type="";
			String tableName="";
			Map<String, String> attrMap = new HashMap<String, String>();

			if(fl.startsWith("02")){
				type="标准紧固件";
				tableName="TMESTANDPARTLINK";
				for (String key:standardMap.keySet()) {
					String value = object2String(ibaHelper.getIBAValue(standardMap.get(key)));
					attrMap.put(key,value);
				}
			}else if(fl.startsWith("01")){
				
				type="电子元器件";
				tableName="TMEELECOMPONENTSPARTLINK";
				for (String key:eleComponentsMap.keySet()) {
					String value = object2String(ibaHelper.getIBAValue(eleComponentsMap.get(key)));
					attrMap.put(key,value);
				}
			}else if(fl.startsWith("03")){
				type="金属材料";
				tableName="TMEMETALLICPARTLINK";
				for (String key:metallicMap.keySet()) {
					String value = object2String(ibaHelper.getIBAValue(metallicMap.get(key)));
					attrMap.put(key,value);
				}
				attrMap.put("GYDW","毫米");
			}else if(fl.startsWith("04")){
				type="非金属材料";
				tableName="TMENONMETALLICPARTLINK";
				for (String key:nonmetallicMap.keySet()) {
					String value = object2String(ibaHelper.getIBAValue(nonmetallicMap.get(key)));
					attrMap.put(key,value);
				}
			}else if(fl.startsWith("05")){
				type="复合材料";
				tableName="TMECOMPOUNDMATERIALPARTLINK";
				for (String key:compoundMaterialMap.keySet()) {
					String value = object2String(ibaHelper.getIBAValue(compoundMaterialMap.get(key)));
					attrMap.put(key,value);
				}
			}else if(fl.startsWith("06")){
				type="机电材料";
				tableName="TMEELEMACHINEPARTLINK";
				for (String key:jidianMap.keySet()) {
					String value = object2String(ibaHelper.getIBAValue(jidianMap.get(key)));
					attrMap.put(key,value);
				}
			}else if(fl.startsWith("07")){
				type="火工品";
				tableName="TMEEXPDEVICEPARTLINK";
				for (String key:huogongpinMap.keySet()) {
					String value = object2String(ibaHelper.getIBAValue(huogongpinMap.get(key)));
					attrMap.put(key,value);
				}
			}
			attrMap.put("WZMC",part.getName());
			attrMap.put("SJBM",part.getNumber());
			attrMap.put("WZBM",part.getNumber());

			String bmdj = object2String(ibaHelper.getIBAValue("BMDJ"));
//			if(!"C".equals(bmdj)){
				String number = part.getNumber();
				QuerySpec qs = new QuerySpec(TechnicsMaterialEntries.class);
				SearchCondition sc = new SearchCondition(TechnicsMaterialEntries.class, TechnicsMaterialEntries.NUMBER,
						SearchCondition.EQUAL, number);
				qs.appendWhere(sc, new int[] { 0 });
				QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qs);
				if(!qResult.hasMoreElements()){
					//创建新的物资条目
					TechnicsMaterialEntries material = TechnicsMaterialEntries.newTechnicsMaterial();
					// 设置生命周期为已发布
					LifeCycleState materialState = LifeCycleState.newLifeCycleState();
					String state = "APPROVED";
					String bmzt = ibaHelper.getIBAValue("BMZT");
					if("禁用".equals(bmzt)){
						state="OBSOLESCENCE";
					}else if("启用".equals(bmzt)){
						state="APPROVED";
					}
					materialState.setState(State.toState(state));
					material.setState(materialState);
					// String uuid = "GYWZTM" + TechnicsMaterialUtils.getUqipNumber();
					material.setName(part.getName());
					material.setNumber(part.getNumber());
					material.setDescription(type);
					WTContainerRef wtContainerRef = TechnicsMaterialUtils.getWTContainerRef(WTContainer.class, "工艺物资信息库");
					long nowtime = Calendar.getInstance().getTimeInMillis();
					Timestamp createStamp = new Timestamp(nowtime);
					material.setContainerReference(wtContainerRef);
					Folder folder = FolderHelper.service.getFolder("/Default/工艺物资条目/"+type, wtContainerRef);
					FolderHelper.assignLocation(material, folder);
					material = (TechnicsMaterialEntries) PersistenceServerHelper.manager.store(material, createStamp, createStamp);
					String id = material.getPersistInfo().getObjectIdentifier().getStringValue();
					// 建立关联关系
					TechnicsMaterialUtils.saveTechnicsMaterialEntriesLink(id, attrMap, tableName);
				}else{

					TechnicsMaterialEntries entries = (TechnicsMaterialEntries) qResult.nextElement();
					String id = entries.getPersistInfo().getObjectIdentifier().getStringValue();
					TechnicsMaterialUtils.saveTechnicsMaterialEntriesLink(id, attrMap, tableName);
					String bmzt = ibaHelper.getIBAValue("BMZT");
					if("禁用".equals(bmzt)){
						WorkflowHelper.setObjectLifeCycle(entries, "OBSOLESCENCE");
					}else if("启用".equals(bmzt)){
						WorkflowHelper.setObjectLifeCycle(entries, "APPROVED");
					}
				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 更新数据
	 *
	 * @param docOid
	 * @param technicsDocNumber
	 */
	public static void updateTechnicaQuotaInfo(String tableName,String csnumber, String sjbm) throws Exception {
		Connection conn = OracleDataSource.getOracleDataSource().getConnection();
		StringBuffer selectSQL = new StringBuffer();
		PreparedStatement ps = null;
		HashSet<String> hashSet = new HashSet<String>();
		try {
			selectSQL.append("UPDATE "+tableName+" SET SJBM='" + sjbm + "' WHERE WZBM='" + csnumber + "' ");
			ps = conn.prepareStatement(selectSQL.toString());
			ps.executeUpdate();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.close();
			}
			if (ps != null) {
				ps.close();
			}
		}
	}


}
