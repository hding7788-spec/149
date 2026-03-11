package com.glaway.mpm.sjzyk;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import wt.pds.oracle81.OracleDataSource;

public class SjzykDBUtil {

	/**
	 * 通过选用目录与零部件编号的关系查询标准件
	 *
	 * @param xyml 选用目录
	 * @param xzfl 选择分类
	 * @param number 部件编号
	 * @param name 部件名称
	 * @param ibaMap IBA属性
	 * @return
	 */
	public static List<SjzykBean> queryBzj(String xyml, String xzfl,String number, String name, Map<String,String> ibaMap,String containerName,long viewId) {
		List<SjzykBean> list = new ArrayList<SjzykBean>();
		Connection conn = null;
		Statement ps = null;
		ResultSet resultset = null;
		try {
			long start = System.currentTimeMillis();
            conn = OracleDataSource.getOracleDataSource().getConnection();
            StringBuffer sql = new StringBuffer();
            sql.append("select t.wtpartnumber,t.name,t.STANDARDNUMBER,t.SHORTNAME,t.CSIZE,");
            sql.append("t.CMAT,t.MECHANICALPROPERTYORHARDNESS,t.SURFACETREATMENT,t.HEATTREATMENT,");
            sql.append("t.PRODUCTFORM,t.PRODUCTLEVEL,t.PLATECSCREWFORM,t.ISIMPORT,t.SPECIALINSTRUCTION,");
            sql.append("t.MEASUREUNIT,t.ClassificationNode,t.YXJB,t.BMZT,t.BMLX ");
            sql.append(" from (select m.wtpartnumber,m.name, ");

			sql.append(getIBASql("SHORTNAME", false));//物资简称
            sql.append(getIBASql("STANDARDNUMBER", false));//标准号
            sql.append(getIBASql("CSIZE", false));//规格
            sql.append(getIBASql("CMAT", false));//材料
            sql.append(getIBASql("MECHANICALPROPERTYORHARDNESS", false));//机械性能等级或硬度
            sql.append(getIBASql("SURFACETREATMENT", false));//表面处理
            sql.append(getIBASql("HEATTREATMENT", false));//热处理
            sql.append(getIBASql("PRODUCTFORM", false));//产品型式
            sql.append(getIBASql("PRODUCTLEVEL", false));//产品等级
            sql.append(getIBASql("PLATECSCREWFORM", false));//板拧形式
			sql.append(getIBASql("ISIMPORT", false));//是否进口
  			sql.append(getIBASql("SPECIALINSTRUCTION", false));//特殊说明
  			sql.append(getIBASql("MEASUREUNIT", false));//计量单位
  			sql.append(getIBASql("YXJB", false));//编码优选级别
  			sql.append(getIBASql("BMZT", false));//编码状态
  			sql.append(getIBASql("BMLX", false));//编码类型
  			sql.append(getIBASql("ClassificationNode", true));//分类

            if(xyml != null && !"".equals(xyml) && !xyml.equals("全部")) {
            	sql.append(" from GLCATALOG g,WTPARTMASTER m,WTPART w, GLPARTLINK gl, wtlibrary wl");
            	sql.append(" where g.glcatalognumber= gl.catalognumber ");
            	sql.append(" and gl.wtpartnumber=m.wtpartnumber ");
            	sql.append(" and m.ida2a2=w.ida3masterreference ");
            	sql.append(" and w.ida3containerreference = wl.ida2a2 ");
            	sql.append(" and g.glcatalogname='"+xyml+"'");
            } else {
            	sql.append(" from WTPARTMASTER m, WTPART w, wtlibrary wl ");
            	sql.append(" where m.ida2a2=w.ida3masterreference ");
            	sql.append(" and w.ida3containerreference = wl.ida2a2 ");
            }
            sql.append(" and w.IDA3VIEW='"+viewId+"'");
            sql.append(" and wl.NAMECONTAINERINFO='"+containerName+"'");
            sql.append(") t");

            StringBuffer sql2 = new StringBuffer();
			if(!xzfl.contains("全部")) {
				sql2.append(" where t.ClassificationNode like '"+ xzfl +"%'");
			} else {
				sql2.append(" where t.ClassificationNode like '02%'");
			}
            if(number != null && !"".equals(number)) {
            	if(sql2.toString() != null && !"".equals(sql2.toString())) {
            		sql2.append(" and t.wtpartnumber like '%"+ number +"%'");
            	} else {
            		sql2.append(" where t.wtpartnumber like '%"+ number +"%'");
            	}
            }
            if(name != null && !"".equals(name)) {
            	if(sql2.toString() != null && !"".equals(sql2.toString())) {
            		sql2.append(" and t.name like '%"+ name +"%'");
            	} else {
            		sql2.append(" where t.name like '%"+ name +"%'");
            	}
            }
            if(ibaMap != null && !ibaMap.isEmpty()) {
            	for(String ibaName:ibaMap.keySet()) {
            		if(sql2.toString() != null && !"".equals(sql2.toString())) {
            			sql2.append(" and t."+ibaName +" like '%" + ibaMap.get(ibaName)+"%'");
            		} else {
            			sql2.append(" where t."+ibaName +" like '%" + ibaMap.get(ibaName)+"%'");
            		}
            	}
            }

            sql.append(sql2.toString());
            System.out.println("--------query sjzyk data by multi-table--------\r\n"+sql.toString());
            conn.setAutoCommit(false);
            ps = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            resultset = ps.executeQuery(sql.toString());
            long end = System.currentTimeMillis();
            System.out.println("Statement execute sql used times:" + ((end-start)/1000) + " s");
            SjzykBean bean = null;
            while (resultset.next()) {
            	bean = new SjzykBean();
            	//wtpartnumber,name,STANDARDNUMBER,SHORTNAME,CSIZE
				//CMAT,MECHANICALPROPERTYORHARDNESS,SURFACETREATMENT,HEATTREATMENT
				//PRODUCTFORM,PRODUCTLEVEL,PLATECSCREWFORM,ISIMPORT,SPECIALINSTRUCTION
				//MEASUREUNIT,ClassificationNode,YXJB,BMZT,BMLX
            	bean.setSjbm(SjzykUtil.object2String(resultset.getString(1)));
            	bean.setName(SjzykUtil.object2String(resultset.getString(2)));
    			bean.setBzh(SjzykUtil.object2String(resultset.getString(3)));//标准号
    			bean.setWzjc(SjzykUtil.object2String(resultset.getString(4)));//物资简称
    			bean.setGg(SjzykUtil.object2String(resultset.getString(5)));//规格
    			bean.setCl(SjzykUtil.object2String(resultset.getString(6)));//材料
    			bean.setJxxndjhyd(SjzykUtil.object2String(resultset.getString(7)));//机械性能等级或硬度
    			bean.setBmcl(SjzykUtil.object2String(resultset.getString(8)));//表面处理
    			bean.setRcl(SjzykUtil.object2String(resultset.getString(9)));//热处理
    			bean.setCpxs(SjzykUtil.object2String(resultset.getString(10)));//产品型式
    			bean.setCpdj(SjzykUtil.object2String(resultset.getString(11)));//产品等级
    			bean.setBnxs(SjzykUtil.object2String(resultset.getString(12)));//板拧形式
    			bean.setSfjk(SjzykUtil.object2String(resultset.getString(13)));//是否进口
    			bean.setTssm(SjzykUtil.object2String(resultset.getString(14)));//特殊说明
    			bean.setJldw(SjzykUtil.object2String(resultset.getString(15)));//计量单位
    			bean.setFl(SjzykUtil.object2String(resultset.getString(16)));//分类
    			bean.setBmyyjb(SjzykUtil.object2String(resultset.getString(17)));//编码优选级别
    			bean.setBmzt(SjzykUtil.object2String(resultset.getString(18)));//编码状态
    			bean.setBmlx(SjzykUtil.object2String(resultset.getString(19)));//编码类型

    			list.add(bean);
            }
            long end2 = System.currentTimeMillis();
            System.out.println("process resultSet used times:" + ((end2-end)/1000) + " s");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
        	close(ps,resultset, conn);
        }

		return list;
	}

	/**
	 * 通过选用目录与目录条目，目录条目与零部件编号的关系查询标准件
	 *
	 * @param xyml 选用目录
	 * @param xzfl 选择分类
	 * @param number 部件编号
	 * @param name 部件名称
	 * @param ibaMap IBA属性
	 * @return
	 */
	public static List<SjzykBean> queryBzj2(String xyml, String xzfl,String number, String name, Map<String,String> ibaMap, String containerName,long viewId) {
		List<SjzykBean> list = new ArrayList<SjzykBean>();
		Connection conn = null;
		Statement ps = null;
		ResultSet resultset = null;
		try {
			long start = System.currentTimeMillis();
            conn = OracleDataSource.getOracleDataSource().getConnection();
            StringBuffer sql = new StringBuffer();
            sql.append("select t.wtpartnumber,t.name,t.STANDARDNUMBER,t.SHORTNAME,t.CSIZE," );
            sql.append("t.CMAT,t.MECHANICALPROPERTYORHARDNESS,t.SURFACETREATMENT,t.HEATTREATMENT,");
            sql.append("t.PRODUCTFORM,t.PRODUCTLEVEL,t.PLATECSCREWFORM,t.ISIMPORT,t.SPECIALINSTRUCTION,");
            sql.append("t.MEASUREUNIT,t.ClassificationNode,t.YXJB,t.BMZT,t.BMLX ");
            sql.append(" from (select m.wtpartnumber,m.name, ");

            sql.append(getIBASql("SHORTNAME", false));//物资简称
            sql.append(getIBASql("STANDARDNUMBER", false));//标准号
            sql.append(getIBASql("CSIZE", false));//规格
            sql.append(getIBASql("CMAT", false));//材料
            sql.append(getIBASql("MECHANICALPROPERTYORHARDNESS", false));//机械性能等级或硬度
            sql.append(getIBASql("SURFACETREATMENT", false));//表面处理
            sql.append(getIBASql("HEATTREATMENT", false));//热处理
            sql.append(getIBASql("PRODUCTFORM", false));//产品型式
            sql.append(getIBASql("PRODUCTLEVEL", false));//产品等级
            sql.append(getIBASql("PLATECSCREWFORM", false));//板拧形式
			sql.append(getIBASql("ISIMPORT", false));//是否进口
  			sql.append(getIBASql("SPECIALINSTRUCTION", false));//特殊说明
  			sql.append(getIBASql("MEASUREUNIT", false));//计量单位
  			sql.append(getIBASql("YXJB", false));//编码优选级别
  			sql.append(getIBASql("BMZT", false));//编码状态
  			sql.append(getIBASql("BMLX", false));//编码类型
  			sql.append(getIBASql("ClassificationNode", true));//分类


            if(xyml != null && !"".equals(xyml) && !xyml.contains("全部")) {
            	sql.append(" from GLCATALOG g, WTPARTMASTER m, WTPART w, GLCILINK gc,GLCIPARTLINK gp, wtlibrary wl ");
            	sql.append(" where g.glcatalognumber=gc.glcatalognumber ");
            	sql.append(" and gc.cipartnumber=gp.cipartnumber ");
            	sql.append(" and gp.wtpartnumber=m.wtpartnumber ");
            	sql.append(" and m.ida2a2=w.ida3masterreference ");
            	sql.append(" and w.ida3containerreference = wl.ida2a2 ");
            	sql.append(" and g.glcatalogname='"+xyml+"'");
            } else {
            	sql.append(" from WTPARTMASTER m, WTPART w, wtlibrary wl ");
            	sql.append(" where m.ida2a2=w.ida3masterreference ");
            	sql.append(" and w.ida3containerreference = wl.ida2a2 ");
            }
            sql.append(" and w.IDA3VIEW='"+viewId+"'");
            sql.append(" and wl.NAMECONTAINERINFO='"+containerName+"'");
            sql.append(") t");

            StringBuffer sql2 = new StringBuffer();
			if(!xzfl.contains("全部")) {
				sql2.append(" where t.ClassificationNode like '"+ xzfl +"%' ");
			} else {
				sql2.append(" where t.ClassificationNode like '02%'");
			}
            if(number != null && !"".equals(number)) {
            	if(sql2.toString() != null && !"".equals(sql2.toString())) {
            		sql2.append(" and t.wtpartnumber like '%"+ number +"%'");
            	} else {
            		sql2.append(" where t.wtpartnumber like '%"+ number +"%'");
            	}
            }
            if(name != null && !"".equals(name)) {
            	if(sql2.toString() != null && !"".equals(sql2.toString())) {
            		sql2.append(" and t.name like '%"+ name +"%'");
            	} else {
            		sql2.append(" where t.name like '%"+ name +"%'");
            	}
            }
            if(ibaMap != null && !ibaMap.isEmpty()) {
            	for(String ibaName:ibaMap.keySet()) {
            		if(sql2.toString() != null && !"".equals(sql2.toString())) {
            			sql2.append(" and t."+ibaName +" like '%" + ibaMap.get(ibaName)+"%'");
            		} else {
            			sql2.append(" where t."+ibaName +" like '%" + ibaMap.get(ibaName)+"%'");
            		}
            	}
            }

            sql.append(sql2.toString());
            System.out.println("--------query sjzyk data by multi-table--------\r\n"+sql.toString());

            conn.setAutoCommit(false);
            ps = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            resultset = ps.executeQuery(sql.toString());
            long end = System.currentTimeMillis();
            System.out.println("execute sql used times:" + ((end-start)/1000) + " s");
            SjzykBean bean = null;
            while (resultset.next()) {
            	bean = new SjzykBean();
            	//wtpartnumber,name,STANDARDNUMBER,SHORTNAME,CSIZE
				//CMAT,MECHANICALPROPERTYORHARDNESS,SURFACETREATMENT,HEATTREATMENT
				//PRODUCTFORM,PRODUCTLEVEL,PLATECSCREWFORM,ISIMPORT,SPECIALINSTRUCTION
				//MEASUREUNIT,ClassificationNode,YXJB,BMZT,BMLX
            	bean.setSjbm(SjzykUtil.object2String(resultset.getString(1)));
            	bean.setName(SjzykUtil.object2String(resultset.getString(2)));
    			bean.setBzh(SjzykUtil.object2String(resultset.getString(3)));//标准号
    			bean.setWzjc(SjzykUtil.object2String(resultset.getString(4)));//物资简称
    			bean.setGg(SjzykUtil.object2String(resultset.getString(5)));//规格
    			bean.setCl(SjzykUtil.object2String(resultset.getString(6)));//材料
    			bean.setJxxndjhyd(SjzykUtil.object2String(resultset.getString(7)));//机械性能等级或硬度
    			bean.setBmcl(SjzykUtil.object2String(resultset.getString(8)));//表面处理
    			bean.setRcl(SjzykUtil.object2String(resultset.getString(9)));//热处理
    			bean.setCpxs(SjzykUtil.object2String(resultset.getString(10)));//产品型式
    			bean.setCpdj(SjzykUtil.object2String(resultset.getString(11)));//产品等级
    			bean.setBnxs(SjzykUtil.object2String(resultset.getString(12)));//板拧形式
    			bean.setSfjk(SjzykUtil.object2String(resultset.getString(13)));//是否进口
    			bean.setTssm(SjzykUtil.object2String(resultset.getString(14)));//特殊说明
    			bean.setJldw(SjzykUtil.object2String(resultset.getString(15)));//计量单位
    			bean.setFl(SjzykUtil.object2String(resultset.getString(16)));//分类
    			bean.setBmyyjb(SjzykUtil.object2String(resultset.getString(17)));//编码优选级别
    			bean.setBmzt(SjzykUtil.object2String(resultset.getString(18)));//编码状态
    			bean.setBmlx(SjzykUtil.object2String(resultset.getString(19)));//编码类型

    			list.add(bean);
            }
            long end2 = System.currentTimeMillis();
            System.out.println("process resultSet used times:" + ((end2-end)/1000) + " s");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
        	close(ps,resultset,conn);
        }

		return list;
	}

	/**
	 * 通过选用目录与零部件编号的关系查询元器件
	 *
	 * @param xyml 选用目录
	 * @param xzfl 选择分类
	 * @param number 部件编号
	 * @param name 部件名称
	 * @param ibaMap IBA属性
	 * @return
	 */
	public static List<SjzykBean> queryYqj(String xyml, String xzfl,String number, String name, Map<String,String> ibaMap, String containerName,long viewId) {
		List<SjzykBean> list = new ArrayList<SjzykBean>();
		Connection conn = null;
		Statement ps = null;
		ResultSet resultset = null;
		try {
			long start = System.currentTimeMillis();
            conn = OracleDataSource.getOracleDataSource().getConnection();
            StringBuffer sql = new StringBuffer();
            sql.append("select t.wtpartnumber,t.name,t.SHORTNAME,t.TYPE,t.TYPESTANDARD,t.QUALITYLEVEL,");
            sql.append("t.TOTALSTANDARD,t.DETAILSTANDARD,t.PACKAGINGFORM,t.OUTLINESIZE,");
            sql.append("t.SPECIALCONDITION,t.EXTRACONDITION,t.ISIMPORT,");
            sql.append("t.SPECIALINSTRUCTION,t.MEASUREUNIT,t.ClassificationNode,t.YXJB,t.BMZT,t.BMLX,");
            sql.append("t.BMDJ,t.KFZBTID,t.KFZBSEE,t.XNCS,t.JDMGDJ_STATE,t.JDMGDJ,t.SMDJ ");
            sql.append("from (select m.wtpartnumber,m.name, ");

			sql.append(getIBASql("SHORTNAME", false));//物资简称
            sql.append(getIBASql("TYPE", false));//型号
            sql.append(getIBASql("TYPESTANDARD", false));//型号规格
            sql.append(getIBASql("QUALITYLEVEL", false));//质量等级
            sql.append(getIBASql("TOTALSTANDARD", false));//总规范
            sql.append(getIBASql("DETAILSTANDARD", false));//详细规范
            sql.append(getIBASql("PACKAGINGFORM", false));//封装形式
            sql.append(getIBASql("OUTLINESIZE", false));//外形尺寸
            sql.append(getIBASql("SPECIALCONDITION", false));//专用条件
            sql.append(getIBASql("EXTRACONDITION", false));//附加协议
			sql.append(getIBASql("ISIMPORT", false));//是否进口
			sql.append(getIBASql("SPECIALINSTRUCTION", false));//特殊说明
			sql.append(getIBASql("MEASUREUNIT", false));//计量单位
			sql.append(getIBASql("YXJB", false));//编码优选级别
			sql.append(getIBASql("BMZT", false));//编码状态
			sql.append(getIBASql("BMLX", false));//编码类型
			sql.append(getIBASql("ClassificationNode", false));//分类
			sql.append(getIBASql("BMDJ", false));//编码等级
			sql.append(getIBASql("KFZBTID", false));//抗辐指标TID
			sql.append(getIBASql("KFZBSEE", false));//抗辐指标SEE
			sql.append(getIBASql("XNCS", false));//性能参数
			sql.append(getIBASql("JDMGDJ_STATE", false));//是否静电敏感
			sql.append(getIBASql("JDMGDJ", false));//静电敏感等级
			sql.append(getIBASql("SMDJ", true));//湿敏等级

			if(xyml != null && !"".equals(xyml) && !xyml.equals("全部")) {
            	sql.append(" from GLCATALOG g,WTPARTMASTER m,WTPART w, GLPARTLINK gl, wtlibrary wl");
            	sql.append(" where g.glcatalognumber= gl.catalognumber ");
            	sql.append(" and gl.wtpartnumber=m.wtpartnumber ");
            	sql.append(" and m.ida2a2=w.ida3masterreference ");
            	sql.append(" and w.ida3containerreference = wl.ida2a2 ");
            	sql.append(" and g.glcatalogname='"+xyml+"'");
            } else {
            	sql.append(" from WTPARTMASTER m, WTPART w, wtlibrary wl ");
            	sql.append(" where m.ida2a2=w.ida3masterreference ");
            	sql.append(" and w.ida3containerreference = wl.ida2a2 ");
            }
			sql.append(" and w.IDA3VIEW='"+viewId+"'");
            sql.append(" and wl.NAMECONTAINERINFO='"+containerName+"'");
            sql.append(") t");

            StringBuffer sql2 = new StringBuffer();
			if(!xzfl.contains("全部")) {
				sql2.append(" where t.ClassificationNode like '"+ xzfl +"%' ");
			} else {
				sql2.append(" where t.ClassificationNode like '01%'");
			}
            if(number != null && !"".equals(number)) {
            	if(sql2.toString() != null && !"".equals(sql2.toString())) {
            		sql2.append(" and t.wtpartnumber like '%"+ number +"%'");
            	} else {
            		sql2.append(" where t.wtpartnumber like '%"+ number +"%'");
            	}
            }
            if(name != null && !"".equals(name) && !"null".equals(name)) {
            	if(sql2.toString() != null && !"".equals(sql2.toString())) {
            		sql2.append(" and t.name like '%"+ name +"%'");
            	} else {
            		sql2.append(" where t.name like '%"+ name +"%'");
            	}
            }
            if(ibaMap != null && !ibaMap.isEmpty()) {
            	for(String ibaName:ibaMap.keySet()) {
            		if(sql2.toString() != null && !"".equals(sql2.toString())) {
            			sql2.append(" and t."+ibaName +" like '%" + ibaMap.get(ibaName)+"%'");
            		} else {
            			sql2.append(" where t."+ibaName +" like '%" + ibaMap.get(ibaName)+"%'");
            		}
            	}
            }

            sql.append(sql2.toString());
            System.out.println("--------query sjzyk data by multi-table--------\r\n"+sql.toString());

            conn.setAutoCommit(false);
            ps = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            resultset = ps.executeQuery(sql.toString());
            long end = System.currentTimeMillis();
            System.out.println("execute sql used times:" + ((end-start)/1000) + " s");
            SjzykBean bean = null;
            while (resultset.next()) {
            	bean = new SjzykBean();
            	//wtpartnumber,name,SHORTNAME,TYPE,TYPESTANDARD,QUALITYLEVEL
        		//TOTALSTANDARD,DETAILSTANDARD,PACKAGINGFORM,OUTLINESIZE
        		//SPECIALCONDITION,EXTRACONDITION,ISIMPORT
        		//SPECIALINSTRUCTION,MEASUREUNIT,ClassificationNode,YXJB,BMZT,BMLX
            	bean.setSjbm(SjzykUtil.object2String(resultset.getString(1)));
            	bean.setName(SjzykUtil.object2String(resultset.getString(2)));
    			bean.setWzjc(SjzykUtil.object2String(resultset.getString(3)));//物资简称
    			bean.setXh(SjzykUtil.object2String(resultset.getString(4)));//型号
    			bean.setXhgg(SjzykUtil.object2String(resultset.getString(5)));//型号规格
    			bean.setZldj(SjzykUtil.object2String(resultset.getString(6)));//质量等级
    			bean.setZgf(SjzykUtil.object2String(resultset.getString(7)));//总规范
    			bean.setXxgf(SjzykUtil.object2String(resultset.getString(8)));//详细规范
    			bean.setFzxs(SjzykUtil.object2String(resultset.getString(9)));//封装形式
    			bean.setWxcc(SjzykUtil.object2String(resultset.getString(10)));//外形尺寸
    			bean.setZytj(SjzykUtil.object2String(resultset.getString(11)));//专用条件
    			bean.setFjxy(SjzykUtil.object2String(resultset.getString(12)));//附加协议
    			bean.setSfjk(SjzykUtil.object2String(resultset.getString(13)));//是否进口
    			bean.setTssm(SjzykUtil.object2String(resultset.getString(14)));//特殊说明
    			bean.setJldw(SjzykUtil.object2String(resultset.getString(15)));//计量单位
    			bean.setFl(SjzykUtil.object2String(resultset.getString(16)));//分类
    			bean.setBmyyjb(SjzykUtil.object2String(resultset.getString(17)));//编码优选级别
    			bean.setBmzt(SjzykUtil.object2String(resultset.getString(18)));//编码状态
    			bean.setBmlx(SjzykUtil.object2String(resultset.getString(19)));//编码类型
    			bean.setBmdj(SjzykUtil.object2String(resultset.getString(20)));//编码等级
    			bean.setKfzbtid(SjzykUtil.object2String(resultset.getString(21)));//抗辐指标TID
    			bean.setKfzbsee(SjzykUtil.object2String(resultset.getString(22)));//抗辐指标SEE
    			bean.setXncs(SjzykUtil.object2String(resultset.getString(23)));//性能参数
    			bean.setJdmgdj_state(SjzykUtil.object2String(resultset.getString(24)));//是否静电敏感
    			bean.setJdmgdj(SjzykUtil.object2String(resultset.getString(25)));//静电敏感等级
    			bean.setSmdj(SjzykUtil.object2String(resultset.getString(26)));//湿敏等级
    			
    			list.add(bean);
            }
            long end2 = System.currentTimeMillis();
            System.out.println("process resultSet used times:" + ((end2-end)/1000) + " s");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
        	close(ps,resultset,conn);
        }

		return list;
	}

	/**
	 * 通过选用目录与目录条目关联，目录条目与零部件编号的关系查询元器件
	 *
	 * @param xyml 选用目录
	 * @param xzfl 选择分类
	 * @param number 部件编号
	 * @param name 部件名称
	 * @param ibaMap IBA属性
	 * @return
	 */
	public static List<SjzykBean> queryYqj2(String xyml, String xzfl,String number, String name, Map<String,String> ibaMap, String containerName,long viewId) {
		List<SjzykBean> list = new ArrayList<SjzykBean>();
		Connection conn = null;
		Statement ps = null;
		ResultSet resultset = null;
		try {
			long start = System.currentTimeMillis();
            conn = OracleDataSource.getOracleDataSource().getConnection();
            StringBuffer sql = new StringBuffer();
            sql.append("select t.wtpartnumber,t.name,t.SHORTNAME,t.TYPE,t.TYPESTANDARD,t.QUALITYLEVEL,");
            sql.append("t.TOTALSTANDARD,t.DETAILSTANDARD,t.PACKAGINGFORM,t.OUTLINESIZE,");
            sql.append("t.SPECIALCONDITION,t.EXTRACONDITION,t.ISIMPORT,");
            sql.append("t.SPECIALINSTRUCTION,t.MEASUREUNIT,t.ClassificationNode,t.YXJB,t.BMZT,t.BMLX,");
            sql.append("t.BMDJ,t.KFZBTID,t.KFZBSEE,t.XNCS,t.JDMGDJ_STATE,t.JDMGDJ,t.SMDJ ");
            sql.append("from (select m.wtpartnumber,m.name, ");
            sql.append(getIBASql("SHORTNAME", false));//物资简称
            sql.append(getIBASql("TYPE", false));//型号
            sql.append(getIBASql("TYPESTANDARD", false));//型号规格
            sql.append(getIBASql("QUALITYLEVEL", false));//质量等级
            sql.append(getIBASql("TOTALSTANDARD", false));//总规范
            sql.append(getIBASql("DETAILSTANDARD", false));//详细规范
            sql.append(getIBASql("PACKAGINGFORM", false));//封装形式
            sql.append(getIBASql("OUTLINESIZE", false));//外形尺寸
            sql.append(getIBASql("SPECIALCONDITION", false));//专用条件
            sql.append(getIBASql("EXTRACONDITION", false));//附加协议
			sql.append(getIBASql("ISIMPORT", false));//是否进口
			sql.append(getIBASql("SPECIALINSTRUCTION", false));//特殊说明
			sql.append(getIBASql("MEASUREUNIT", false));//计量单位
			sql.append(getIBASql("YXJB", false));//编码优选级别
			sql.append(getIBASql("BMZT", false));//编码状态
			sql.append(getIBASql("BMLX", false));//编码类型
			sql.append(getIBASql("ClassificationNode", true));//分类
			sql.append(getIBASql("BMDJ", false));//编码等级
			sql.append(getIBASql("KFZBTID", false));//抗辐指标TID
			sql.append(getIBASql("KFZBSEE", false));//抗辐指标SEE
			sql.append(getIBASql("XNCS", false));//性能参数
			sql.append(getIBASql("JDMGDJ_STATE", false));//是否静电敏感
			sql.append(getIBASql("JDMGDJ", false));//静电敏感等级
			sql.append(getIBASql("SMDJ", true));//湿敏等级

			if(xyml != null && !"".equals(xyml) && !xyml.contains("全部")) {
            	sql.append(" from GLCATALOG g, WTPARTMASTER m, WTPART w, GLCILINK gc,GLCIPARTLINK gp, wtlibrary wl ");
            	sql.append(" where g.glcatalognumber=gc.glcatalognumber ");
            	sql.append(" and gc.cipartnumber=gp.cipartnumber ");
            	sql.append(" and gp.wtpartnumber=m.wtpartnumber ");
            	sql.append(" and m.ida2a2=w.ida3masterreference ");
            	sql.append(" and w.ida3containerreference = wl.ida2a2 ");
            	sql.append(" and g.glcatalogname='"+xyml+"'");
            } else {
            	sql.append(" from WTPARTMASTER m, WTPART w, wtlibrary wl ");
            	sql.append(" where m.ida2a2=w.ida3masterreference ");
            	sql.append(" and w.ida3containerreference = wl.ida2a2 ");
            }
			sql.append(" and w.IDA3VIEW='"+viewId+"'");
            sql.append(" and wl.NAMECONTAINERINFO='"+containerName+"'");
            sql.append(") t");

            StringBuffer sql2 = new StringBuffer();
			if(!xzfl.contains("全部")) {
				sql2.append(" where t.ClassificationNode like '"+ xzfl +"%' ");
			} else {
				sql2.append(" where t.ClassificationNode like '01%'");
			}
            if(number != null && !"".equals(number)) {
            	if(sql2.toString() != null && !"".equals(sql2.toString())) {
            		sql2.append(" and t.wtpartnumber like '%"+ number +"%'");
            	} else {
            		sql2.append(" where t.wtpartnumber like '%"+ number +"%'");
            	}
            }
            if(name != null && !"".equals(name) && !"null".equals(name)) {
            	if(sql2.toString() != null && !"".equals(sql2.toString())) {
            		sql2.append(" and t.name like '%"+ name +"%'");
            	} else {
            		sql2.append(" where t.name like '%"+ name +"%'");
            	}
            }
            if(ibaMap != null && !ibaMap.isEmpty()) {
            	for(String ibaName:ibaMap.keySet()) {
            		if(sql2.toString() != null && !"".equals(sql2.toString())) {
            			sql2.append(" and t."+ibaName +" like '%" + ibaMap.get(ibaName)+"%'");
            		} else {
            			sql2.append(" where t."+ibaName +" like '%" + ibaMap.get(ibaName)+"%'");
            		}
            	}
            }

            sql.append(sql2.toString());
            System.out.println("--------query sjzyk data by multi-table--------\r\n"+sql.toString());

            conn.setAutoCommit(false);
            ps = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            resultset = ps.executeQuery(sql.toString());
            long end = System.currentTimeMillis();
            System.out.println("execute sql used times:" + ((end-start)/1000) + " s");
            SjzykBean bean = null;
            while (resultset.next()) {
            	bean = new SjzykBean();
            	//wtpartnumber,name,SHORTNAME,TYPE,TYPESTANDARD,QUALITYLEVEL
        		//TOTALSTANDARD,DETAILSTANDARD,PACKAGINGFORM,OUTLINESIZE
        		//SPECIALCONDITION,EXTRACONDITION,ISIMPORT
        		//SPECIALINSTRUCTION,MEASUREUNIT,ClassificationNode,YXJB,BMZT,BMLX
            	bean.setSjbm(SjzykUtil.object2String(resultset.getString(1)));
            	bean.setName(SjzykUtil.object2String(resultset.getString(2)));
    			bean.setWzjc(SjzykUtil.object2String(resultset.getString(3)));//物资简称
    			bean.setXh(SjzykUtil.object2String(resultset.getString(4)));//型号
    			bean.setXhgg(SjzykUtil.object2String(resultset.getString(5)));//型号规格
    			bean.setZldj(SjzykUtil.object2String(resultset.getString(6)));//质量等级
    			bean.setZgf(SjzykUtil.object2String(resultset.getString(7)));//总规范
    			bean.setXxgf(SjzykUtil.object2String(resultset.getString(8)));//详细规范
    			bean.setFzxs(SjzykUtil.object2String(resultset.getString(9)));//封装形式
    			bean.setWxcc(SjzykUtil.object2String(resultset.getString(10)));//外形尺寸
    			bean.setZytj(SjzykUtil.object2String(resultset.getString(11)));//专用条件
    			bean.setFjxy(SjzykUtil.object2String(resultset.getString(12)));//附加协议
    			bean.setSfjk(SjzykUtil.object2String(resultset.getString(13)));//是否进口
    			bean.setTssm(SjzykUtil.object2String(resultset.getString(14)));//特殊说明
    			bean.setJldw(SjzykUtil.object2String(resultset.getString(15)));//计量单位
    			bean.setFl(SjzykUtil.object2String(resultset.getString(16)));//分类
    			bean.setBmyyjb(SjzykUtil.object2String(resultset.getString(17)));//编码优选级别
    			bean.setBmzt(SjzykUtil.object2String(resultset.getString(18)));//编码状态
    			bean.setBmlx(SjzykUtil.object2String(resultset.getString(19)));//编码类型
    			bean.setBmdj(SjzykUtil.object2String(resultset.getString(20)));//编码等级
    			bean.setKfzbtid(SjzykUtil.object2String(resultset.getString(21)));//抗辐指标TID
    			bean.setKfzbsee(SjzykUtil.object2String(resultset.getString(22)));//抗辐指标SEE
    			bean.setXncs(SjzykUtil.object2String(resultset.getString(23)));//性能参数
    			bean.setJdmgdj_state(SjzykUtil.object2String(resultset.getString(24)));//是否静电敏感
    			bean.setJdmgdj(SjzykUtil.object2String(resultset.getString(25)));//静电敏感等级
    			bean.setSmdj(SjzykUtil.object2String(resultset.getString(26)));//湿敏等级

    			list.add(bean);
            }
            long end2 = System.currentTimeMillis();
            System.out.println("process resultSet used times:" + ((end2-end)/1000) + " s");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
        	close(ps,resultset,conn);
        }

		return list;
	}

	/**
	 * 通过选用目录与零部件编号的关系查询非金属材料
	 *
	 * @param xyml 选用目录
	 * @param xzfl 选择分类
	 * @param number 部件编号
	 * @param name 部件名称
	 * @param ibaMap IBA属性
	 * @return
	 */
	public static List<SjzykBean> queryFjscl(String xyml, String xzfl,String number, String name, Map<String,String> ibaMap, String containerName,long viewId) {
		List<SjzykBean> list = new ArrayList<SjzykBean>();
		Connection conn = null;
		Statement ps = null;
		ResultSet resultset = null;
		try {
			long start = System.currentTimeMillis();
            conn = OracleDataSource.getOracleDataSource().getConnection();
            StringBuffer sql = new StringBuffer();
            sql.append("select t.wtpartnumber,t.name,t.SHORTNAME,t.MATTYPE,t.RATEOFCONVERSION,t.RATIO,t.MARKNUMBER, ");
            sql.append(" t.CSIZE,t.USESTANDARD,t.ISIMPORT,t.SPECIALINSTRUCTION,t.MEASUREUNIT,t.ClassificationNode,t.YXJB,t.BMZT,t.BMLX ");
            sql.append(" from (select m.wtpartnumber,m.name, ");

			sql.append(getIBASql("SHORTNAME", false));//物资简称
            sql.append(getIBASql("MATTYPE", false));//材料类型
            sql.append(getIBASql("RATEOFCONVERSION", false));//换算率
            sql.append(getIBASql("RATIO", false));//系数
            sql.append(getIBASql("MARKNUMBER", false));//牌号
            sql.append(getIBASql("CSIZE", false));//规格
            sql.append(getIBASql("USESTANDARD", false));//采用标准
			sql.append(getIBASql("ISIMPORT", false));//是否进口
			sql.append(getIBASql("SPECIALINSTRUCTION", false));//特殊说明
			sql.append(getIBASql("MEASUREUNIT", false));//计量单位
			sql.append(getIBASql("YXJB", false));//编码优选级别
			sql.append(getIBASql("BMZT", false));//编码状态
			sql.append(getIBASql("BMLX", false));//编码类型
			sql.append(getIBASql("ClassificationNode", true));//分类

			if(xyml != null && !"".equals(xyml) && !xyml.equals("全部")) {
            	sql.append(" from GLCATALOG g,WTPARTMASTER m,WTPART w, GLPARTLINK gl, wtlibrary wl");
            	sql.append(" where g.glcatalognumber= gl.catalognumber ");
            	sql.append(" and gl.wtpartnumber=m.wtpartnumber ");
            	sql.append(" and m.ida2a2=w.ida3masterreference ");
            	sql.append(" and w.ida3containerreference = wl.ida2a2 ");
            	sql.append(" and g.glcatalogname='"+xyml+"'");
            } else {
            	sql.append(" from GLCATALOG g, WTPARTMASTER m, WTPART w, wtlibrary wl ");
            	sql.append(" where m.ida2a2=w.ida3masterreference ");
            	sql.append(" and w.ida3containerreference = wl.ida2a2 ");
            }
			sql.append(" and w.IDA3VIEW='"+viewId+"'");
            sql.append(" and wl.NAMECONTAINERINFO='"+containerName+"'");
            sql.append(") t");

            StringBuffer sql2 = new StringBuffer();
			if(!xzfl.contains("全部")) {
				sql2.append(" where t.ClassificationNode like '"+ xzfl +"%' ");
			} else {
				sql2.append(" where t.ClassificationNode like '04%'");
			}
            if(number != null && !"".equals(number)) {
            	if(sql2.toString() != null && !"".equals(sql2.toString())) {
            		sql2.append(" and t.wtpartnumber like '%"+ number +"%'");
            	} else {
            		sql2.append(" where t.wtpartnumber like '%"+ number +"%'");
            	}
            }
            if(name != null && !"".equals(name)) {
            	if(sql2.toString() != null && !"".equals(sql2.toString())) {
            		sql2.append(" and t.name like '%"+ name +"%'");
            	} else {
            		sql2.append(" where t.name like '%"+ name +"%'");
            	}
            }
            if(ibaMap != null && !ibaMap.isEmpty()) {
            	for(String ibaName:ibaMap.keySet()) {
            		if(sql2.toString() != null && !"".equals(sql2.toString())) {
            			sql2.append(" and t."+ibaName +" like '%" + ibaMap.get(ibaName)+"%'");
            		} else {
            			sql2.append(" where t."+ibaName +" like '%" + ibaMap.get(ibaName)+"%'");
            		}
            	}
            }

            sql.append(sql2.toString());
            System.out.println("--------query sjzyk data by multi-table--------\r\n"+sql.toString());

            conn.setAutoCommit(false);
            ps = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            resultset = ps.executeQuery(sql.toString());
            long end = System.currentTimeMillis();
            System.out.println("execute sql used times:" + ((end-start)/1000) + " s");
            SjzykBean bean = null;
            while (resultset.next()) {
            	bean = new SjzykBean();
            	//wtpartnumber,name,SHORTNAME,MATTYPE,RATEOFCONVERSION,RATIO,MARKNUMBER
				//CSIZE,USESTANDARD,ISIMPORT,SPECIALINSTRUCTION,MEASUREUNIT,ClassificationNode,YXJB,BMZT,BMLX
            	bean.setSjbm(SjzykUtil.object2String(resultset.getString(1)));
            	bean.setName(SjzykUtil.object2String(resultset.getString(2)));
    			bean.setWzjc(SjzykUtil.object2String(resultset.getString(3)));//物资简称
    			bean.setCllx(SjzykUtil.object2String(resultset.getString(4)));//材料类型
    			bean.setHsl(SjzykUtil.object2String(resultset.getString(5)));//换算率
    			bean.setXs(SjzykUtil.object2String(resultset.getString(6)));//系数
    			bean.setPh(SjzykUtil.object2String(resultset.getString(7)));//牌号
    			bean.setGg(SjzykUtil.object2String(resultset.getString(8)));//规格
    			bean.setCybz(SjzykUtil.object2String(resultset.getString(9)));//采用标准
    			bean.setSfjk(SjzykUtil.object2String(resultset.getString(10)));//是否进口
    			bean.setTssm(SjzykUtil.object2String(resultset.getString(11)));//特殊说明
    			bean.setJldw(SjzykUtil.object2String(resultset.getString(12)));//计量单位
    			bean.setFl(SjzykUtil.object2String(resultset.getString(13)));//分类
    			bean.setBmyyjb(SjzykUtil.object2String(resultset.getString(14)));//编码优选级别
    			bean.setBmzt(SjzykUtil.object2String(resultset.getString(15)));//编码状态
    			bean.setBmlx(SjzykUtil.object2String(resultset.getString(16)));//编码类型

    			list.add(bean);
            }
            long end2 = System.currentTimeMillis();
            System.out.println("process resultSet used times:" + ((end2-end)/1000) + " s");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
        	close(ps,resultset,conn);
        }

		return list;
	}

	/**
	 * 通过选用目录与零部件编号的关系查询非金属材料
	 *
	 * @param xyml 选用目录
	 * @param xzfl 选择分类
	 * @param number 部件编号
	 * @param name 部件名称
	 * @param ibaMap IBA属性
	 * @return
	 */
	public static List<SjzykBean> queryFjscl2(String xyml, String xzfl,String number, String name, Map<String,String> ibaMap, String containerName,long viewId) {
		List<SjzykBean> list = new ArrayList<SjzykBean>();
		Connection conn = null;
		Statement ps = null;
		ResultSet resultset = null;
		try {
			long start = System.currentTimeMillis();
            conn = OracleDataSource.getOracleDataSource().getConnection();
            StringBuffer sql = new StringBuffer();
            sql.append("select t.wtpartnumber,t.name,t.SHORTNAME,t.MATTYPE,t.RATEOFCONVERSION,t.RATIO,t.MARKNUMBER, ");
            sql.append(" t.CSIZE,t.USESTANDARD,t.ISIMPORT,t.SPECIALINSTRUCTION,t.MEASUREUNIT,t.ClassificationNode,t.YXJB,t.BMZT,t.BMLX ");
            sql.append(" from (select m.wtpartnumber,m.name, ");

            sql.append(getIBASql("SHORTNAME", false));//物资简称
            sql.append(getIBASql("MATTYPE", false));//材料类型
            sql.append(getIBASql("RATEOFCONVERSION", false));//换算率
            sql.append(getIBASql("RATIO", false));//系数
            sql.append(getIBASql("MARKNUMBER", false));//牌号
            sql.append(getIBASql("CSIZE", false));//规格
            sql.append(getIBASql("USESTANDARD", false));//采用标准
			sql.append(getIBASql("ISIMPORT", false));//是否进口
			sql.append(getIBASql("SPECIALINSTRUCTION", false));//特殊说明
			sql.append(getIBASql("MEASUREUNIT", false));//计量单位
			sql.append(getIBASql("YXJB", false));//编码优选级别
			sql.append(getIBASql("BMZT", false));//编码状态
			sql.append(getIBASql("BMLX", false));//编码类型
			sql.append(getIBASql("ClassificationNode", true));//分类

			if(xyml != null && !"".equals(xyml) && !xyml.contains("全部")) {
            	sql.append(" from GLCATALOG g, WTPARTMASTER m, WTPART w, GLCILINK gc,GLCIPARTLINK gp, wtlibrary wl ");
            	sql.append(" where g.glcatalognumber=gc.glcatalognumber ");
            	sql.append(" and gc.cipartnumber=gp.cipartnumber ");
            	sql.append(" and gp.wtpartnumber=m.wtpartnumber ");
            	sql.append(" and m.ida2a2=w.ida3masterreference ");
            	sql.append(" and w.ida3containerreference = wl.ida2a2 ");
            	sql.append(" and g.glcatalogname='"+xyml+"'");
            } else {
            	sql.append(" from GLCATALOG g, WTPARTMASTER m, WTPART w, wtlibrary wl ");
            	sql.append(" where m.ida2a2=w.ida3masterreference ");
            	sql.append(" and w.ida3containerreference = wl.ida2a2 ");
            }
			sql.append(" and w.IDA3VIEW='"+viewId+"'");
            sql.append(" and wl.NAMECONTAINERINFO='"+containerName+"'");
            sql.append(") t");

            StringBuffer sql2 = new StringBuffer();
			if(!xzfl.contains("全部")) {
				sql2.append(" where t.ClassificationNode like '"+ xzfl +"%' ");
			} else {
				sql2.append(" where t.ClassificationNode like '04%'");
			}
            if(number != null && !"".equals(number)) {
            	if(sql2.toString() != null && !"".equals(sql2.toString())) {
            		sql2.append(" and t.wtpartnumber like '%"+ number +"%'");
            	} else {
            		sql2.append(" where t.wtpartnumber like '%"+ number +"%'");
            	}
            }
            if(name != null && !"".equals(name)) {
            	if(sql2.toString() != null && !"".equals(sql2.toString())) {
            		sql2.append(" and t.name like '%"+ name +"%'");
            	} else {
            		sql2.append(" where t.name like '%"+ name +"%'");
            	}
            }
            if(ibaMap != null && !ibaMap.isEmpty()) {
            	for(String ibaName:ibaMap.keySet()) {
            		if(sql2.toString() != null && !"".equals(sql2.toString())) {
            			sql2.append(" and t."+ibaName +" like '%" + ibaMap.get(ibaName)+"%'");
            		} else {
            			sql2.append(" where t."+ibaName +" like '%" + ibaMap.get(ibaName)+"%'");
            		}
            	}
            }

            sql.append(sql2.toString());
            System.out.println("--------query sjzyk data by multi-table--------\r\n"+sql.toString());

            conn.setAutoCommit(false);
            ps = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            resultset = ps.executeQuery(sql.toString());
            long end = System.currentTimeMillis();
            System.out.println("execute sql used times:" + ((end-start)/1000) + " s");
            SjzykBean bean = null;
            while (resultset.next()) {
            	bean = new SjzykBean();
            	//wtpartnumber,name,SHORTNAME,MATTYPE,RATEOFCONVERSION,RATIO,MARKNUMBER
				//CSIZE,USESTANDARD,ISIMPORT,SPECIALINSTRUCTION,MEASUREUNIT,ClassificationNode,YXJB,BMZT,BMLX
            	bean.setSjbm(SjzykUtil.object2String(resultset.getString(1)));
            	bean.setName(SjzykUtil.object2String(resultset.getString(2)));
    			bean.setWzjc(SjzykUtil.object2String(resultset.getString(3)));//物资简称
    			bean.setCllx(SjzykUtil.object2String(resultset.getString(4)));//材料类型
    			bean.setHsl(SjzykUtil.object2String(resultset.getString(5)));//换算率
    			bean.setXs(SjzykUtil.object2String(resultset.getString(6)));//系数
    			bean.setPh(SjzykUtil.object2String(resultset.getString(7)));//牌号
    			bean.setGg(SjzykUtil.object2String(resultset.getString(8)));//规格
    			bean.setCybz(SjzykUtil.object2String(resultset.getString(9)));//采用标准
    			bean.setSfjk(SjzykUtil.object2String(resultset.getString(10)));//是否进口
    			bean.setTssm(SjzykUtil.object2String(resultset.getString(11)));//特殊说明
    			bean.setJldw(SjzykUtil.object2String(resultset.getString(12)));//计量单位
    			bean.setFl(SjzykUtil.object2String(resultset.getString(13)));//分类
    			bean.setBmyyjb(SjzykUtil.object2String(resultset.getString(14)));//编码优选级别
    			bean.setBmzt(SjzykUtil.object2String(resultset.getString(15)));//编码状态
    			bean.setBmlx(SjzykUtil.object2String(resultset.getString(16)));//编码类型

    			list.add(bean);
            }
            long end2 = System.currentTimeMillis();
            System.out.println("process resultSet used times:" + ((end2-end)/1000) + " s");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
        	close(ps,resultset,conn);
        }

		return list;
	}

	/**
	 * 通过选用目录与零部件编号的关系查询复合材料
	 *
	 * @param xyml 选用目录
	 * @param xzfl 选择分类
	 * @param number 部件编号
	 * @param name 部件名称
	 * @param ibaMap IBA属性
	 * @return
	 */
	public static List<SjzykBean> queryFhcl(String xyml, String xzfl,String number, String name, Map<String,String> ibaMap, String containerName,long viewId) {
		List<SjzykBean> list = new ArrayList<SjzykBean>();
		Connection conn = null;
		Statement ps = null;
		ResultSet resultset = null;
		try {
			long start = System.currentTimeMillis();
            conn = OracleDataSource.getOracleDataSource().getConnection();
            StringBuffer sql = new StringBuffer();
            sql.append("select t.wtpartnumber,t.name,t.SHORTNAME,t.MATTYPE,t.RATEOFCONVERSION,t.RATIO,t.MARKNUMBER, ");
            sql.append(" t.CSIZE,t.USESTANDARD,t.ISIMPORT,t.SPECIALINSTRUCTION,t.MEASUREUNIT,t.ClassificationNode,t.YXJB,t.BMZT,t.BMLX ");
			sql.append(" from (select m.wtpartnumber,m.name, ");

			sql.append(getIBASql("SHORTNAME", false));//物资简称
            sql.append(getIBASql("MATTYPE", false));//材料类型
            sql.append(getIBASql("RATEOFCONVERSION", false));//换算率
            sql.append(getIBASql("RATIO", false));//系数
            sql.append(getIBASql("MARKNUMBER", false));//牌号
            sql.append(getIBASql("CSIZE", false));//规格
            sql.append(getIBASql("USESTANDARD", false));//采用标准
			sql.append(getIBASql("ISIMPORT", false));//是否进口
			sql.append(getIBASql("SPECIALINSTRUCTION", false));//特殊说明
			sql.append(getIBASql("MEASUREUNIT", false));//计量单位
			sql.append(getIBASql("YXJB", false));//编码优选级别
			sql.append(getIBASql("BMZT", false));//编码状态
			sql.append(getIBASql("BMLX", false));//编码类型
			sql.append(getIBASql("ClassificationNode", true));//分类

			if(xyml != null && !"".equals(xyml) && !xyml.equals("全部")) {
            	sql.append(" from GLCATALOG g,WTPARTMASTER m,WTPART w, GLPARTLINK gl, wtlibrary wl");
            	sql.append(" where g.glcatalognumber= gl.catalognumber ");
            	sql.append(" and gl.wtpartnumber=m.wtpartnumber ");
            	sql.append(" and m.ida2a2=w.ida3masterreference ");
            	sql.append(" and w.ida3containerreference = wl.ida2a2 ");
            	sql.append(" and g.glcatalogname='"+xyml+"'");
            } else {
            	sql.append(" from GLCATALOG g, WTPARTMASTER m, WTPART w, wtlibrary wl ");
            	sql.append(" where m.ida2a2=w.ida3masterreference ");
            	sql.append(" and w.ida3containerreference = wl.ida2a2 ");
            }
			sql.append(" and w.IDA3VIEW='"+viewId+"'");
            sql.append(" and wl.NAMECONTAINERINFO='"+containerName+"'");
            sql.append(") t");

            StringBuffer sql2 = new StringBuffer();
			if(!xzfl.contains("全部")) {
				sql2.append(" where t.ClassificationNode like '"+ xzfl +"%' ");
			} else {
				sql2.append(" where t.ClassificationNode like '05%'");
			}
            if(number != null && !"".equals(number)) {
            	if(sql2.toString() != null && !"".equals(sql2.toString())) {
            		sql2.append(" and t.wtpartnumber like '%"+ number +"%'");
            	} else {
            		sql2.append(" where t.wtpartnumber like '%"+ number +"%'");
            	}
            }
            if(name != null && !"".equals(name)) {
            	if(sql2.toString() != null && !"".equals(sql2.toString())) {
            		sql2.append(" and t.name like '%"+ name +"%'");
            	} else {
            		sql2.append(" where t.name like '%"+ name +"%'");
            	}
            }
            if(ibaMap != null && !ibaMap.isEmpty()) {
            	for(String ibaName:ibaMap.keySet()) {
            		if(sql2.toString() != null && !"".equals(sql2.toString())) {
            			sql2.append(" and t."+ibaName +" like '%" + ibaMap.get(ibaName)+"%'");
            		} else {
            			sql2.append(" where t."+ibaName +" like '%" + ibaMap.get(ibaName)+"%'");
            		}
            	}
            }

            sql.append(sql2.toString());
            System.out.println("--------query sjzyk data by multi-table--------\r\n"+sql.toString());

            conn.setAutoCommit(false);
            ps = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            resultset = ps.executeQuery(sql.toString());
            long end = System.currentTimeMillis();
            System.out.println("execute sql used times:" + ((end-start)/1000) + " s");
            SjzykBean bean = null;
            while (resultset.next()) {
            	bean = new SjzykBean();
            	//wtpartnumber,name,SHORTNAME,MATTYPE,RATEOFCONVERSION,RATIO,MARKNUMBER
				//CSIZE,USESTANDARD,ISIMPORT,SPECIALINSTRUCTION,MEASUREUNIT,ClassificationNode,YXJB,BMZT,BMLX
            	bean.setSjbm(SjzykUtil.object2String(resultset.getString(1)));
            	bean.setName(SjzykUtil.object2String(resultset.getString(2)));
    			bean.setWzjc(SjzykUtil.object2String(resultset.getString(3)));//物资简称
    			bean.setCllx(SjzykUtil.object2String(resultset.getString(4)));//材料类型
    			bean.setHsl(SjzykUtil.object2String(resultset.getString(5)));//换算率
    			bean.setXs(SjzykUtil.object2String(resultset.getString(6)));//系数
    			bean.setPh(SjzykUtil.object2String(resultset.getString(7)));//牌号
    			bean.setGg(SjzykUtil.object2String(resultset.getString(8)));//规格
    			bean.setCybz(SjzykUtil.object2String(resultset.getString(9)));//采用标准
    			bean.setSfjk(SjzykUtil.object2String(resultset.getString(10)));//是否进口
    			bean.setTssm(SjzykUtil.object2String(resultset.getString(11)));//特殊说明
    			bean.setJldw(SjzykUtil.object2String(resultset.getString(12)));//计量单位
    			bean.setFl(SjzykUtil.object2String(resultset.getString(13)));//分类
    			bean.setBmyyjb(SjzykUtil.object2String(resultset.getString(14)));//编码优选级别
    			bean.setBmzt(SjzykUtil.object2String(resultset.getString(15)));//编码状态
    			bean.setBmlx(SjzykUtil.object2String(resultset.getString(16)));//编码类型

    			list.add(bean);
            }
            long end2 = System.currentTimeMillis();
            System.out.println("process resultSet used times:" + ((end2-end)/1000) + " s");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
        	close(ps,resultset,conn);
        }

		return list;
	}

	/**
	 * 通过选用目录与零部件编号的关系查询复合材料
	 *
	 * @param xyml 选用目录
	 * @param xzfl 选择分类
	 * @param number 部件编号
	 * @param name 部件名称
	 * @param ibaMap IBA属性
	 * @return
	 */
	public static List<SjzykBean> queryFhcl2(String xyml, String xzfl,String number, String name, Map<String,String> ibaMap, String containerName,long viewId) {
		List<SjzykBean> list = new ArrayList<SjzykBean>();
		Connection conn = null;
		Statement ps = null;
		ResultSet resultset = null;
		try {
			long start = System.currentTimeMillis();
            conn = OracleDataSource.getOracleDataSource().getConnection();
            StringBuffer sql = new StringBuffer();
            sql.append("select t.wtpartnumber,t.name,t.SHORTNAME,t.MATTYPE,t.RATEOFCONVERSION,t.RATIO,t.MARKNUMBER, ");
            sql.append(" t.CSIZE,t.USESTANDARD,t.ISIMPORT,t.SPECIALINSTRUCTION,t.MEASUREUNIT,t.ClassificationNode,t.YXJB,t.BMZT,t.BMLX ");
            sql.append(" from (select m.wtpartnumber,m.name, ");

            sql.append(getIBASql("SHORTNAME", false));//物资简称
            sql.append(getIBASql("MATTYPE", false));//材料类型
            sql.append(getIBASql("RATEOFCONVERSION", false));//换算率
            sql.append(getIBASql("RATIO", false));//系数
            sql.append(getIBASql("MARKNUMBER", false));//牌号
            sql.append(getIBASql("CSIZE", false));//规格
            sql.append(getIBASql("USESTANDARD", false));//采用标准
			sql.append(getIBASql("ISIMPORT", false));//是否进口
			sql.append(getIBASql("SPECIALINSTRUCTION", false));//特殊说明
			sql.append(getIBASql("MEASUREUNIT", false));//计量单位
			sql.append(getIBASql("YXJB", false));//编码优选级别
			sql.append(getIBASql("BMZT", false));//编码状态
			sql.append(getIBASql("BMLX", false));//编码类型
			sql.append(getIBASql("ClassificationNode", true));//分类

			if(xyml != null && !"".equals(xyml) && !xyml.contains("全部")) {
            	sql.append(" from GLCATALOG g, WTPARTMASTER m, WTPART w, GLCILINK gc,GLCIPARTLINK gp, wtlibrary wl ");
            	sql.append(" where g.glcatalognumber=gc.glcatalognumber ");
            	sql.append(" and gc.cipartnumber=gp.cipartnumber ");
            	sql.append(" and gp.wtpartnumber=m.wtpartnumber ");
            	sql.append(" and m.ida2a2=w.ida3masterreference ");
            	sql.append(" and w.ida3containerreference = wl.ida2a2 ");
            	sql.append(" and g.glcatalogname='"+xyml+"'");
            } else {
            	sql.append(" from GLCATALOG g, WTPARTMASTER m, WTPART w, wtlibrary wl ");
            	sql.append(" where m.ida2a2=w.ida3masterreference ");
            	sql.append(" and w.ida3containerreference = wl.ida2a2 ");
            }
			sql.append(" and w.IDA3VIEW='"+viewId+"'");
            sql.append(" and wl.NAMECONTAINERINFO='"+containerName+"'");
            sql.append(") t");

            StringBuffer sql2 = new StringBuffer();
			if(!xzfl.contains("全部")) {
				sql2.append(" where t.ClassificationNode like '"+ xzfl +"%' ");
			} else {
				sql2.append(" where t.ClassificationNode like '05%'");
			}
            if(number != null && !"".equals(number)) {
            	if(sql2.toString() != null && !"".equals(sql2.toString())) {
            		sql2.append(" and t.wtpartnumber like '%"+ number +"%'");
            	} else {
            		sql2.append(" where t.wtpartnumber like '%"+ number +"%'");
            	}
            }
            if(name != null && !"".equals(name)) {
            	if(sql2.toString() != null && !"".equals(sql2.toString())) {
            		sql2.append(" and t.name like '%"+ name +"%'");
            	} else {
            		sql2.append(" where t.name like '%"+ name +"%'");
            	}
            }
            if(ibaMap != null && !ibaMap.isEmpty()) {
            	for(String ibaName:ibaMap.keySet()) {
            		if(sql2.toString() != null && !"".equals(sql2.toString())) {
            			sql2.append(" and t."+ibaName +" like '%" + ibaMap.get(ibaName)+"%'");
            		} else {
            			sql2.append(" where t."+ibaName +" like '%" + ibaMap.get(ibaName)+"%'");
            		}
            	}
            }

            sql.append(sql2.toString());
            System.out.println("--------query sjzyk data by multi-table--------\r\n"+sql.toString());

            conn.setAutoCommit(false);
            ps = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            resultset = ps.executeQuery(sql.toString());
            long end = System.currentTimeMillis();
            System.out.println("execute sql used times:" + ((end-start)/1000) + " s");
            SjzykBean bean = null;
            while (resultset.next()) {
            	bean = new SjzykBean();
            	//wtpartnumber,name,SHORTNAME,MATTYPE,RATEOFCONVERSION,RATIO,MARKNUMBER
				//CSIZE,USESTANDARD,ISIMPORT,SPECIALINSTRUCTION,MEASUREUNIT,ClassificationNode,YXJB,BMZT,BMLX
            	bean.setSjbm(SjzykUtil.object2String(resultset.getString(1)));
            	bean.setName(SjzykUtil.object2String(resultset.getString(2)));
    			bean.setWzjc(SjzykUtil.object2String(resultset.getString(3)));//物资简称
    			bean.setCllx(SjzykUtil.object2String(resultset.getString(4)));//材料类型
    			bean.setHsl(SjzykUtil.object2String(resultset.getString(5)));//换算率
    			bean.setXs(SjzykUtil.object2String(resultset.getString(6)));//系数
    			bean.setPh(SjzykUtil.object2String(resultset.getString(7)));//牌号
    			bean.setGg(SjzykUtil.object2String(resultset.getString(8)));//规格
    			bean.setCybz(SjzykUtil.object2String(resultset.getString(9)));//采用标准
    			bean.setSfjk(SjzykUtil.object2String(resultset.getString(10)));//是否进口
    			bean.setTssm(SjzykUtil.object2String(resultset.getString(11)));//特殊说明
    			bean.setJldw(SjzykUtil.object2String(resultset.getString(12)));//计量单位
    			bean.setFl(SjzykUtil.object2String(resultset.getString(13)));//分类
    			bean.setBmyyjb(SjzykUtil.object2String(resultset.getString(14)));//编码优选级别
    			bean.setBmzt(SjzykUtil.object2String(resultset.getString(15)));//编码状态
    			bean.setBmlx(SjzykUtil.object2String(resultset.getString(16)));//编码类型

    			list.add(bean);
            }
            long end2 = System.currentTimeMillis();
            System.out.println("process resultSet used times:" + ((end2-end)/1000) + " s");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
        	close(ps,resultset,conn);
        }

		return list;
	}

	/**
	 * 通过选用目录与零部件编号的关系查询金属材料
	 *
	 * @param xyml 选用目录
	 * @param xzfl 选择分类
	 * @param number 部件编号
	 * @param name 部件名称
	 * @param ibaMap IBA属性
	 * @return
	 */
	public static List<SjzykBean> queryJscl(String xyml, String xzfl,String number, String name, Map<String,String> ibaMap, String containerName,long viewId) {
		List<SjzykBean> list = new ArrayList<SjzykBean>();
		Connection conn = null;
		Statement ps = null;
		ResultSet resultset = null;
		try {
			long start = System.currentTimeMillis();
            conn = OracleDataSource.getOracleDataSource().getConnection();
            StringBuffer sql = new StringBuffer();

            sql.append("select t.wtpartnumber,t.name,t.SHORTNAME,t.MATTYPE,t.RATEOFCONVERSION,t.RATIO,t.MARKNUMBER, ");
            sql.append(" t.CSIZE,t.USESTANDARD,t.PRECISION,t.QUALITYCHARACTER,t.VARIETYSTANDARD,t.SUPPLYSTATE,");
            sql.append("t.ISIMPORT,t.SPECIALINSTRUCTION,t.MEASUREUNIT,t.ClassificationNode,t.YXJB,t.BMZT,t.BMLX ");
            sql.append(" from (select m.wtpartnumber,m.name, ");
            sql.append(getIBASql("SHORTNAME", false));//物资简称
            sql.append(getIBASql("MATTYPE", false));//材料类型
            sql.append(getIBASql("RATEOFCONVERSION", false));//换算率
            sql.append(getIBASql("RATIO", false));//系数
            sql.append(getIBASql("MARKNUMBER", false));//牌号
            sql.append(getIBASql("CSIZE", false));//规格
            sql.append(getIBASql("USESTANDARD", false));//采用标准
            sql.append(getIBASql("PRECISION", false));//精度
            sql.append(getIBASql("QUALITYCHARACTER", false));//质量特征
            sql.append(getIBASql("VARIETYSTANDARD", false));//品种规格标准
            sql.append(getIBASql("SUPPLYSTATE", false));//供应状态
			sql.append(getIBASql("ISIMPORT", false));//是否进口
			sql.append(getIBASql("SPECIALINSTRUCTION", false));//特殊说明
			sql.append(getIBASql("MEASUREUNIT", false));//计量单位
			sql.append(getIBASql("YXJB", false));//编码优选级别
			sql.append(getIBASql("BMZT", false));//编码状态
			sql.append(getIBASql("BMLX", false));//编码类型
			sql.append(getIBASql("ClassificationNode", true));//分类

			if(xyml != null && !"".equals(xyml) && !xyml.equals("全部")) {
            	sql.append(" from GLCATALOG g,WTPARTMASTER m,WTPART w, GLPARTLINK gl, wtlibrary wl");
            	sql.append(" where g.glcatalognumber= gl.catalognumber ");
            	sql.append(" and gl.wtpartnumber=m.wtpartnumber ");
            	sql.append(" and m.ida2a2=w.ida3masterreference ");
            	sql.append(" and w.ida3containerreference = wl.ida2a2 ");
            	sql.append(" and g.glcatalogname='"+xyml+"'");
            } else {
            	sql.append(" from GLCATALOG g, WTPARTMASTER m, WTPART w, wtlibrary wl ");
            	sql.append(" where m.ida2a2=w.ida3masterreference ");
            	sql.append(" and w.ida3containerreference = wl.ida2a2 ");
            }
			sql.append(" and w.IDA3VIEW='"+viewId+"'");
            sql.append(" and wl.NAMECONTAINERINFO='"+containerName+"'");
            sql.append(") t");

            StringBuffer sql2 = new StringBuffer();
			if(!xzfl.contains("全部")) {
				sql2.append(" where t.ClassificationNode like '"+ xzfl +"%' ");
			} else {
				sql2.append(" where t.ClassificationNode like '03%'");
			}
            if(number != null && !"".equals(number)) {
            	if(sql2.toString() != null && !"".equals(sql2.toString())) {
            		sql2.append(" and t.wtpartnumber like '%"+ number +"%'");
            	} else {
            		sql2.append(" where t.wtpartnumber like '%"+ number +"%'");
            	}
            }
            if(name != null && !"".equals(name)) {
            	if(sql2.toString() != null && !"".equals(sql2.toString())) {
            		sql2.append(" and t.name like '%"+ name +"%'");
            	} else {
            		sql2.append(" where t.name like '%"+ name +"%'");
            	}
            }
            if(ibaMap != null && !ibaMap.isEmpty()) {
            	for(String ibaName:ibaMap.keySet()) {
            		if(sql2.toString() != null && !"".equals(sql2.toString())) {
            			sql2.append(" and t."+ibaName +" like '%" + ibaMap.get(ibaName)+"%'");
            		} else {
            			sql2.append(" where t."+ibaName +" like '%" + ibaMap.get(ibaName)+"%'");
            		}
            	}
            }

            sql.append(sql2.toString());
            System.out.println("--------query sjzyk data by multi-table--------\r\n"+sql.toString());

            conn.setAutoCommit(false);
            ps = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            resultset = ps.executeQuery(sql.toString());
            long end = System.currentTimeMillis();
            System.out.println("execute sql used times:" + ((end-start)/1000) + " s");
            SjzykBean bean = null;
            while (resultset.next()) {
            	bean = new SjzykBean();
            	//wtpartnumber,name,SHORTNAME,MATTYPE,RATEOFCONVERSION,RATIO,MARKNUMBER
				//CSIZE,USESTANDARD,PRECISION,QUALITYCHARACTER,t.VARIETYSTANDARD,t.SUPPLYSTATE,
            	//ISIMPORT,SPECIALINSTRUCTION,MEASUREUNIT,ClassificationNode,YXJB,BMZT,BMLX
            	bean.setSjbm(SjzykUtil.object2String(resultset.getString(1)));
            	bean.setName(SjzykUtil.object2String(resultset.getString(2)));
    			bean.setWzjc(SjzykUtil.object2String(resultset.getString(3)));//物资简称
    			bean.setCllx(SjzykUtil.object2String(resultset.getString(4)));//材料类型
    			bean.setHsl(SjzykUtil.object2String(resultset.getString(5)));//换算率
    			bean.setXs(SjzykUtil.object2String(resultset.getString(6)));//系数
    			bean.setPh(SjzykUtil.object2String(resultset.getString(7)));//牌号
    			bean.setGg(SjzykUtil.object2String(resultset.getString(8)));//规格
    			bean.setCybz(SjzykUtil.object2String(resultset.getString(9)));//采用标准
    			bean.setJd(SjzykUtil.object2String(resultset.getString(10)));//精度
    			bean.setZltz(SjzykUtil.object2String(resultset.getString(11)));//质量特征
    			bean.setPzggbz(SjzykUtil.object2String(resultset.getString(12)));//品种规格标准
    			bean.setGyzt(SjzykUtil.object2String(resultset.getString(13)));//供应状态
    			bean.setSfjk(SjzykUtil.object2String(resultset.getString(14)));//是否进口
    			bean.setTssm(SjzykUtil.object2String(resultset.getString(15)));//特殊说明
    			bean.setJldw(SjzykUtil.object2String(resultset.getString(16)));//计量单位
    			bean.setFl(SjzykUtil.object2String(resultset.getString(17)));//分类
    			bean.setBmyyjb(SjzykUtil.object2String(resultset.getString(18)));//编码优选级别
    			bean.setBmzt(SjzykUtil.object2String(resultset.getString(19)));//编码状态
    			bean.setBmlx(SjzykUtil.object2String(resultset.getString(20)));//编码类型

    			list.add(bean);
            }
            long end2 = System.currentTimeMillis();
            System.out.println("process resultSet used times:" + ((end2-end)/1000) + " s");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
        	close(ps,resultset,conn);
        }

		return list;
	}

	/**
	 * 通过选用目录与零部件编号的关系查询金属材料
	 *
	 * @param xyml 选用目录
	 * @param xzfl 选择分类
	 * @param number 部件编号
	 * @param name 部件名称
	 * @param ibaMap IBA属性
	 * @return
	 */
	public static List<SjzykBean> queryJscl2(String xyml, String xzfl,String number, String name, Map<String,String> ibaMap, String containerName,long viewId) {
		List<SjzykBean> list = new ArrayList<SjzykBean>();
		Connection conn = null;
		Statement ps = null;
		ResultSet resultset = null;
		try {
			long start = System.currentTimeMillis();
            conn = OracleDataSource.getOracleDataSource().getConnection();
            StringBuffer sql = new StringBuffer();
            sql.append("select t.wtpartnumber,t.name,t.SHORTNAME,t.MATTYPE,t.RATEOFCONVERSION,t.RATIO,t.MARKNUMBER, ");
            sql.append(" t.CSIZE,t.USESTANDARD,t.PRECISION,t.QUALITYCHARACTER,t.VARIETYSTANDARD,t.SUPPLYSTATE,");
            sql.append("t.ISIMPORT,t.SPECIALINSTRUCTION,t.MEASUREUNIT,t.ClassificationNode,t.YXJB,t.BMZT,t.BMLX ");
			sql.append(" from (select m.wtpartnumber,m.name, ");

			sql.append(getIBASql("SHORTNAME", false));//物资简称
			sql.append(getIBASql("MATTYPE", false));//材料类型
			sql.append(getIBASql("RATEOFCONVERSION", false));//换算率
			sql.append(getIBASql("RATIO", false));//系数
			sql.append(getIBASql("MARKNUMBER", false));//牌号
			sql.append(getIBASql("CSIZE", false));//规格
			sql.append(getIBASql("USESTANDARD", false));//采用标准
			sql.append(getIBASql("PRECISION", false));//精度
			sql.append(getIBASql("QUALITYCHARACTER", false));//质量特征
			sql.append(getIBASql("VARIETYSTANDARD", false));//品种规格标准
			sql.append(getIBASql("SUPPLYSTATE", false));//供应状态
			sql.append(getIBASql("ISIMPORT", false));//是否进口
			sql.append(getIBASql("SPECIALINSTRUCTION", false));//特殊说明
			sql.append(getIBASql("MEASUREUNIT", false));//计量单位
			sql.append(getIBASql("YXJB", false));//编码优选级别
			sql.append(getIBASql("BMZT", false));//编码状态
			sql.append(getIBASql("BMLX", false));//编码类型
			sql.append(getIBASql("ClassificationNode", true));//分类

			if(xyml != null && !"".equals(xyml) && !xyml.contains("全部")) {
            	sql.append(" from GLCATALOG g, WTPARTMASTER m, WTPART w, GLCILINK gc,GLCIPARTLINK gp, wtlibrary wl ");
            	sql.append(" where g.glcatalognumber=gc.glcatalognumber ");
            	sql.append(" and gc.cipartnumber=gp.cipartnumber ");
            	sql.append(" and gp.wtpartnumber=m.wtpartnumber ");
            	sql.append(" and m.ida2a2=w.ida3masterreference ");
            	sql.append(" and w.ida3containerreference = wl.ida2a2 ");
            	sql.append(" and g.glcatalogname='"+xyml+"'");
            } else {
            	sql.append(" from GLCATALOG g, WTPARTMASTER m, WTPART w, wtlibrary wl ");
            	sql.append(" where m.ida2a2=w.ida3masterreference ");
            	sql.append(" and w.ida3containerreference = wl.ida2a2 ");
            }
			sql.append(" and w.IDA3VIEW='"+viewId+"'");
            sql.append(" and wl.NAMECONTAINERINFO='"+containerName+"'");
            sql.append(") t");

            StringBuffer sql2 = new StringBuffer();
			if(!xzfl.contains("全部")) {
				sql2.append(" where t.ClassificationNode like '"+ xzfl +"%'");
			} else {
				sql2.append(" where t.ClassificationNode like '03%'");
			}
            if(number != null && !"".equals(number)) {
            	if(sql2.toString() != null && !"".equals(sql2.toString())) {
            		sql2.append(" and t.wtpartnumber like '%"+ number +"%'");
            	} else {
            		sql2.append(" where t.wtpartnumber like '%"+ number +"%'");
            	}
            }
            if(name != null && !"".equals(name)) {
            	if(sql2.toString() != null && !"".equals(sql2.toString())) {
            		sql2.append(" and t.name like '%"+ name +"%'");
            	} else {
            		sql2.append(" where t.name like '%"+ name +"%'");
            	}
            }
            if(ibaMap != null && !ibaMap.isEmpty()) {
            	for(String ibaName:ibaMap.keySet()) {
            		if(sql2.toString() != null && !"".equals(sql2.toString())) {
            			sql2.append(" and t."+ibaName +" like '%" + ibaMap.get(ibaName)+"%'");
            		} else {
            			sql2.append(" where t."+ibaName +" like '%" + ibaMap.get(ibaName)+"%'");
            		}
            	}
            }

            sql.append(sql2.toString());
            System.out.println("--------query sjzyk data by multi-table--------\r\n"+sql.toString());

            conn.setAutoCommit(false);
            ps = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            resultset = ps.executeQuery(sql.toString());
            long end = System.currentTimeMillis();
            System.out.println("execute sql used times:" + ((end-start)/1000) + " s");
            SjzykBean bean = null;
            while (resultset.next()) {
            	bean = new SjzykBean();
            	//wtpartnumber,name,SHORTNAME,MATTYPE,RATEOFCONVERSION,RATIO,MARKNUMBER
				//CSIZE,USESTANDARD,PRECISION,QUALITYCHARACTER,t.VARIETYSTANDARD,t.SUPPLYSTATE,
            	//ISIMPORT,SPECIALINSTRUCTION,MEASUREUNIT,ClassificationNode,YXJB,BMZT,BMLX
            	bean.setSjbm(SjzykUtil.object2String(resultset.getString(1)));
            	bean.setName(SjzykUtil.object2String(resultset.getString(2)));
    			bean.setWzjc(SjzykUtil.object2String(resultset.getString(3)));//物资简称
    			bean.setCllx(SjzykUtil.object2String(resultset.getString(4)));//材料类型
    			bean.setHsl(SjzykUtil.object2String(resultset.getString(5)));//换算率
    			bean.setXs(SjzykUtil.object2String(resultset.getString(6)));//系数
    			bean.setPh(SjzykUtil.object2String(resultset.getString(7)));//牌号
    			bean.setGg(SjzykUtil.object2String(resultset.getString(8)));//规格
    			bean.setCybz(SjzykUtil.object2String(resultset.getString(9)));//采用标准
    			bean.setJd(SjzykUtil.object2String(resultset.getString(10)));//精度
    			bean.setZltz(SjzykUtil.object2String(resultset.getString(11)));//质量特征
    			bean.setPzggbz(SjzykUtil.object2String(resultset.getString(12)));//品种规格标准
    			bean.setGyzt(SjzykUtil.object2String(resultset.getString(13)));//供应状态
    			bean.setSfjk(SjzykUtil.object2String(resultset.getString(14)));//是否进口
    			bean.setTssm(SjzykUtil.object2String(resultset.getString(15)));//特殊说明
    			bean.setJldw(SjzykUtil.object2String(resultset.getString(16)));//计量单位
    			bean.setFl(SjzykUtil.object2String(resultset.getString(17)));//分类
    			bean.setBmyyjb(SjzykUtil.object2String(resultset.getString(18)));//编码优选级别
    			bean.setBmzt(SjzykUtil.object2String(resultset.getString(19)));//编码状态
    			bean.setBmlx(SjzykUtil.object2String(resultset.getString(20)));//编码类型

    			list.add(bean);
            }
            long end2 = System.currentTimeMillis();
            System.out.println("process resultSet used times:" + ((end2-end)/1000) + " s");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
        	close(ps,resultset,conn);
        }

		return list;
	}

	private static String getIBASql(String ibaKey, boolean isEnd) {
		String sql = " (select sv.value2 "+
                " from stringdefinition sd, stringvalue sv "+
                " where sd.ida2a2 = sv.ida3a6 "+
                " and sv.classnamekeya4 = 'wt.part.WTPart' "+
                " and sv.ida3a4 = w.ida2a2 "+
                " and sd.name = '"+ibaKey+"') as "+ibaKey;//编码优选级别

		if(!isEnd) {
			sql = sql + ", ";
		}

		return sql;
	}

	public static List<SjzykBean> querySjzykData(String type, String xyml, String fl, Map<String,String> map) {
		List<SjzykBean> list = new ArrayList<SjzykBean>();
		Connection conn = null;
		Statement ps = null;
		ResultSet resultset = null;
		try {
			long start = System.currentTimeMillis();
            conn = OracleDataSource.getOracleDataSource().getConnection();
            StringBuffer sql = new StringBuffer();

            sql.append("select t.xyml,t.wtpartnumber,t.name,t.STANDARDNUMBER,t.SHORTNAME,t.CSIZE,t.CMAT,t.MECHANICALPROPERTYORHARDNESS,t.SURFACETREATMENT,t.HEATTREATMENT,");
            sql.append("t.PRODUCTFORM,t.PRODUCTLEVEL,t.PLATECSCREWFORM,t.ISIMPORT,t.SPECIALINSTRUCTION,t.MEASUREUNIT,t.ClassificationNode,t.YXJB,t.BMZT,t.BMLX,");
            sql.append("t.TYPE,t.TYPESTANDARD,t.QUALITYLEVEL,t.TOTALSTANDARD,t.DETAILSTANDARD,t.PACKAGINGFORM,t.OUTLINESIZE,t.SPECIALCONDITION,t.EXTRACONDITION,");
            sql.append("t.MATTYPE,t.RATEOFCONVERSION,t.RATIO,t.MARKNUMBER,t.USESTANDARD,t.MTYPE,t.VARIETYSTANDARD ,t.QUALITYCHARACTER ,t.PRECISION ,t.SUPPLYSTATE,t.GYS,");
            sql.append("t.BMDJ,t.KFZBTID,t.KFZBSEE,t.XNCS,t.JDMGDJ_STATE,t.JDMGDJ,t.SMDJ ");
            sql.append("from QUERYMIDDLE_TABLE t ");

            StringBuffer sql2 = new StringBuffer();
            //选用目录不是选择"全部"
            if(!"全部".equals(xyml)) {
            	//分类不是选择"全部"
            	if(!"全部".equals(fl)) {
            		sql2.append(" where t.xyml='"+xyml+"' ");
        			sql2.append(" and t.ClassificationNode like '"+ fl +"%' ");
        			if(map != null && !map.isEmpty()) {
        				for(String ibaName:map.keySet()) {
                    		if(sql2.toString() != null && !"".equals(sql2.toString())) {
                    			sql2.append(" and t."+ibaName +" like '%" + map.get(ibaName)+"%'");
                    		}
                    	}
        			}
            	} else {//分类选择"全部"
            		if("标准件".equals(type)) {
            			sql2.append(" where t.xyml='"+xyml+"' ");
            			sql2.append(" and t.ClassificationNode like '02%' ");
            			if(map != null && !map.isEmpty()) {
            				for(String ibaName:map.keySet()) {
                        		if(sql2.toString() != null && !"".equals(sql2.toString())) {
                        			sql2.append(" and t."+ibaName +" like '%" + map.get(ibaName)+"%'");
                        		}
                        	}
            			}
                    } else if("元器件".equals(type)) {
                    	sql2.append(" where t.xyml='"+xyml+"' ");
            			sql2.append(" and t.ClassificationNode like '01%' ");
            			if(map != null && !map.isEmpty()) {
            				for(String ibaName:map.keySet()) {
                        		if(sql2.toString() != null && !"".equals(sql2.toString())) {
                        			sql2.append(" and t."+ibaName +" like '%" + map.get(ibaName)+"%'");
                        		}
                        	}
            			}
                    } else {
                    	sql2.append(" where t.xyml='"+xyml+"' ");
            			sql2.append(" and ((t.ClassificationNode like '03%') or (t.ClassificationNode like '04%') or (t.ClassificationNode like '05%')) ");
            			if(map != null && !map.isEmpty()) {
            				for(String ibaName:map.keySet()) {
                        		if(sql2.toString() != null && !"".equals(sql2.toString())) {
                        			sql2.append(" and t."+ibaName +" like '%" + map.get(ibaName)+"%'");
                        		}
                        	}
            			}
                    }
            	}
            } else {//选用目录选择"全部"
            	//分类不是选择"全部"
            	if(!"全部".equals(fl)) {
            		sql2.append(" where t.ClassificationNode like '"+ fl +"%' ");
        			if(map != null && !map.isEmpty()) {
        				for(String ibaName:map.keySet()) {
                    		if(sql2.toString() != null && !"".equals(sql2.toString())) {
                    			sql2.append(" and t."+ibaName +" like '%" + map.get(ibaName)+"%'");
                    		}
                    	}
        			}
            	} else {//分类选择"全部"
            		if("标准件".equals(type)) {
            			sql2.append(" where t.ClassificationNode like '02%' ");
            			if(map != null && !map.isEmpty()) {
            				for(String ibaName:map.keySet()) {
                        		if(sql2.toString() != null && !"".equals(sql2.toString())) {
                        			sql2.append(" and t."+ibaName +" like '%" + map.get(ibaName)+"%'");
                        		}
                        	}
            			}
                    } else if("元器件".equals(type)) {
                    	sql2.append(" where t.ClassificationNode like '01%' ");
            			if(map != null && !map.isEmpty()) {
            				for(String ibaName:map.keySet()) {
                        		if(sql2.toString() != null && !"".equals(sql2.toString())) {
                        			sql2.append(" and t."+ibaName +" like '%" + map.get(ibaName)+"%'");
                        		}
                        	}
            			}
                    } else {
                    	sql2.append(" where ((t.ClassificationNode like '03%') or (t.ClassificationNode like '04%') or (t.ClassificationNode like '05%')) ");
            			if(map != null && !map.isEmpty()) {
            				for(String ibaName:map.keySet()) {
                        		if(sql2.toString() != null && !"".equals(sql2.toString())) {
                        			sql2.append(" and t."+ibaName +" like '%" + map.get(ibaName)+"%' ");
                        		}
                        	}
            			}
                    }
            	}
            }

            sql.append(sql2.toString());
            System.out.println("--------query sjzyk data by middle table--------\r\n"+sql.toString());
            conn.setAutoCommit(false);
            ps = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            resultset = ps.executeQuery(sql.toString());
            long end = System.currentTimeMillis();
            System.out.println("exexute sql used times:"+((end-start)/1000)+" s");
            SjzykBean bean = null;
            while (resultset.next()) {
            	bean = new SjzykBean();
            	//xyml,wtpartnumber,name,STANDARDNUMBER,SHORTNAME,CSIZE,CMAT,MECHANICALPROPERTYORHARDNESS,SURFACETREATMENT,HEATTREATMENT,
    			//PRODUCTFORM,PRODUCTLEVEL,PLATECSCREWFORM,ISIMPORT,SPECIALINSTRUCTION,MEASUREUNIT,ClassificationNode,YXJB,BMZT,BMLX,
    			//TYPE,TYPESTANDARD,QUALITYLEVEL,TOTALSTANDARD,DETAILSTANDARD,PACKAGINGFORM,OUTLINESIZE,SPECIALCONDITION,EXTRACONDITION,
    			//MATTYPE,RATEOFCONVERSION,RATIO,MARKNUMBER,USESTANDARD,MTYPE
            	bean.setXyml(SjzykUtil.object2String(resultset.getString(1)));
            	bean.setSjbm(SjzykUtil.object2String(resultset.getString(2)));
            	bean.setName(SjzykUtil.object2String(resultset.getString(3)));
            	bean.setBzh(SjzykUtil.object2String(resultset.getString(4)));
            	bean.setWzjc(SjzykUtil.object2String(resultset.getString(5)));
            	bean.setGg(SjzykUtil.object2String(resultset.getString(6)));
            	bean.setCl(SjzykUtil.object2String(resultset.getString(7)));
            	bean.setJxxndjhyd(SjzykUtil.object2String(resultset.getString(8)));
            	bean.setBmcl(SjzykUtil.object2String(resultset.getString(9)));
            	bean.setRcl(SjzykUtil.object2String(resultset.getString(10)));
            	bean.setCpxs(SjzykUtil.object2String(resultset.getString(11)));
            	bean.setCpdj(SjzykUtil.object2String(resultset.getString(12)));
            	bean.setBnxs(SjzykUtil.object2String(resultset.getString(13)));
            	bean.setSfjk(SjzykUtil.object2String(resultset.getString(14)));
            	bean.setTssm(SjzykUtil.object2String(resultset.getString(15)));
            	bean.setJldw(SjzykUtil.object2String(resultset.getString(16)));
            	bean.setFl(SjzykUtil.object2String(resultset.getString(17)));
            	bean.setBmyyjb(SjzykUtil.object2String(resultset.getString(18)));
            	bean.setBmzt(SjzykUtil.object2String(resultset.getString(19)));
            	bean.setBmlx(SjzykUtil.object2String(resultset.getString(20)));
            	bean.setXh(SjzykUtil.object2String(resultset.getString(21)));
            	bean.setXhgg(SjzykUtil.object2String(resultset.getString(22)));
            	bean.setZldj(SjzykUtil.object2String(resultset.getString(23)));
            	bean.setZgf(SjzykUtil.object2String(resultset.getString(24)));
            	bean.setXxgf(SjzykUtil.object2String(resultset.getString(25)));
            	bean.setFzxs(SjzykUtil.object2String(resultset.getString(26)));
            	bean.setWxcc(SjzykUtil.object2String(resultset.getString(27)));
            	bean.setZytj(SjzykUtil.object2String(resultset.getString(28)));
            	bean.setFjxy(SjzykUtil.object2String(resultset.getString(29)));
            	bean.setCllx(SjzykUtil.object2String(resultset.getString(30)));
            	bean.setHsl(SjzykUtil.object2String(resultset.getString(31)));
            	bean.setXs(SjzykUtil.object2String(resultset.getString(32)));
            	bean.setPh(SjzykUtil.object2String(resultset.getString(33)));
            	bean.setCybz(SjzykUtil.object2String(resultset.getString(34)));
            	bean.setMtype(SjzykUtil.object2String(resultset.getString(35)));
            	bean.setPzggbz(SjzykUtil.object2String(resultset.getString(36)));
            	bean.setZltz(SjzykUtil.object2String(resultset.getString(37)));
            	bean.setJd(SjzykUtil.object2String(resultset.getString(38)));
            	bean.setGyzt(SjzykUtil.object2String(resultset.getString(39)));
            	bean.setGys(SjzykUtil.object2String(resultset.getString(40)));
            	bean.setBmdj("null".equals(SjzykUtil.object2String(resultset.getString(41)))? "":SjzykUtil.object2String(resultset.getString(41)));
				bean.setKfzbtid("null".equals(SjzykUtil.object2String(resultset.getString(42))) ? "" : SjzykUtil.object2String(resultset.getString(42)));
				bean.setKfzbsee("null".equals(SjzykUtil.object2String(resultset.getString(43))) ? "" : SjzykUtil.object2String(resultset.getString(43)));
				bean.setXncs("null".equals(SjzykUtil.object2String(resultset.getString(44))) ? "" : SjzykUtil.object2String(resultset.getString(44)));
				bean.setJdmgdj_state("null".equals(SjzykUtil.object2String(resultset.getString(45))) ? "" : SjzykUtil.object2String(resultset.getString(45)));
				bean.setJdmgdj("null".equals(SjzykUtil.object2String(resultset.getString(46))) ? "" : SjzykUtil.object2String(resultset.getString(46)));
				bean.setSmdj("null".equals(SjzykUtil.object2String(resultset.getString(47))) ? "" : SjzykUtil.object2String(resultset.getString(47)));
            	list.add(bean);
            }
            long end2 = System.currentTimeMillis();
            System.out.println("process result used times:"+((end2-end)/1000)+" s");
		} catch (SQLException e) {
            e.printStackTrace();
        }finally{
        	close(ps,resultset,conn);
        }
		return list;
	}

	public static void close(PreparedStatement ps,ResultSet resultset,Connection conn){
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

	public static void close(Statement state,Connection conn){
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

	public static void close(Statement state,ResultSet resultset,Connection conn){
		if(state!=null){
			try {
				state.close();
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
}
