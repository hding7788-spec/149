package com.glaway.mpm.pbom.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ErpDao {

	/**
	 * 查询物资库
	 * @param dbtype 数据库类型
	 * @param wztype 物资类别
	 * @return
	 */
	public static List<Wzk> queryWzk(String dbtype,String wztype){
		StringBuffer sql = new StringBuffer();
	    sql.append("         select a.invclasscode, a.invcode, a.invname, a.invtype,                           ");
	    sql.append("                a.invspec,e.fjtjname,f.jstjname,c.custname,d.measname, g.zldj, a.def5, a.def6, a.def7, ");
	    sql.append("                a.def8, a.def9, a.def10,a.invclassname, f.jsgfbz,                          ");
	    sql.append("                 a.def12, a.def14, a.def13,                                     ");
	    sql.append("                 a.wzlb, a.erprq, a.pk_measdoc                                      ");
	    sql.append("         from (                                                                            ");
	    sql.append("                select a.invcode, a.invname, a.invtype, a.invspec,                         ");
	    sql.append("                	   a.def2, a.def3, a.def1, a.def4, a.def5, a.def6,                     ");
	    sql.append("                	   a.def7, a.def8, a.def9,a.def10, d.invclasscode, d.invclassname,             ");
	    sql.append("                       a.def12, a.def14, a.def13, substr(d.invclasscode, 0, 4) wzlb,       ");
	    sql.append("                       a.ts erprq, a.pk_measdoc                                            ");
	    sql.append("                from BD_INVBASDOC a,BD_INVCL d                                             ");
	    sql.append("                where a.pk_invcl = d.pk_invcl and d.invclasscode like '"+wztype+"%'        ");
	    sql.append("            		and nvl(a.sealflag,'N') = 'N'                                           ");
	    //sql.append("                      and a.invcode not like 'X%'                                          ");
	    sql.append("         ) a                                                                               ");
	    sql.append("         left join BD_cumandoc b on a.def3 = b.pk_cumandoc                                 ");
	    sql.append("         left join BD_cubasdoc c on b.pk_cubasdoc = c.pk_cubasdoc                             ");
	    sql.append("         left join BD_MEASDOC d on a.pk_measdoc = d.pk_measdoc                             ");
	    sql.append("         left join SHHT_FJTJ e on a.def2 = e.pk_fjtj                                       ");
	    sql.append("         left join SHHT_JSTJ f on a.def1 = f.pk_jstj                                       ");
	    sql.append("         left join SHHT_ZLDJ_B g on a.def4 = g.pk_zldj_b                                   ");

		List<Wzk> list = DBUtil.getDBUtil(dbtype).queryForList(sql.toString(), Wzk.class);
		System.out.println("queryWzk sql="+sql);
		return list;
	}

	public static List<Wzk> queryWzkAdd(String dbtype,String wztype){
		StringBuffer sql = new StringBuffer();
	    sql.append("         select a.invclasscode, a.invcode, a.invname, a.invtype,                           ");
	    sql.append("                a.invspec,e.fjtjname,f.jstjname,c.custname,d.measname, g.zldj, a.def5, a.def6, a.def7, ");
	    sql.append("                a.def8, a.def9, a.def10,a.invclassname, f.jsgfbz,                          ");
	    sql.append("                 a.def12, a.def14, a.def13,                                     ");
	    sql.append("                 a.wzlb, a.erprq, a.pk_measdoc                                      ");
	    sql.append("         from (                                                                            ");
	    sql.append("                select a.invcode, a.invname, a.invtype, a.invspec,                         ");
	    sql.append("                	   a.def2, a.def3, a.def1, a.def4, a.def5, a.def6,                     ");
	    sql.append("                	   a.def7, a.def8, a.def9,a.def10, d.invclasscode, d.invclassname,             ");
	    sql.append("                       a.def12, a.def14, a.def13, substr(d.invclasscode, 0, 4) wzlb,       ");
	    sql.append("                       a.ts erprq, a.pk_measdoc                                            ");
	    sql.append("                from BD_INVBASDOC a,BD_INVCL d                                             ");
	    sql.append("                where a.pk_invcl = d.pk_invcl and d.invclasscode like '"+wztype+"%'        ");
	    sql.append("            		 and a.ts > to_char(sysdate,'yyyy-mm-dd')   and nvl(a.sealflag,'N') = 'N'                                           ");
	    //sql.append("                      and a.invcode not like 'X%'                                          ");
	    sql.append("         ) a                                                                               ");
	    sql.append("         left join BD_cumandoc b on a.def3 = b.pk_cumandoc                                 ");
	    sql.append("         left join BD_cubasdoc c on b.pk_cubasdoc = c.pk_cubasdoc                             ");
	    sql.append("         left join BD_MEASDOC d on a.pk_measdoc = d.pk_measdoc                             ");
	    sql.append("         left join SHHT_FJTJ e on a.def2 = e.pk_fjtj                                       ");
	    sql.append("         left join SHHT_JSTJ f on a.def1 = f.pk_jstj                                       ");
	    sql.append("         left join SHHT_ZLDJ_B g on a.def4 = g.pk_zldj_b                                   ");

		List<Wzk> list = DBUtil.getDBUtil(dbtype).queryForList(sql.toString(), Wzk.class);
		System.out.println("queryWzkAdd sql="+sql);
		return list;
	}


	public static Wzk getWzkByInvcode(String dbtype,String invcode){
		StringBuffer sql = new StringBuffer();
	    sql.append("         select a.invcode,                                                             ");
	    sql.append("                a.invname,                                                             ");
	    sql.append("                a.invtype,                                                             ");
	    sql.append("                a.invspec,                                                             ");
	    sql.append("                a.def2,                                                                ");
	    sql.append("                a.def3,                                                                ");
	    sql.append("                b.def1,                                                                ");
	    sql.append("                b.custname,                                                            ");
	    sql.append("                c.measname,                                                            ");
	    sql.append("                a.def4,                                                                ");
	    sql.append("                a.def5,                                                                ");
	    sql.append("                a.def6,                                                                ");
	    sql.append("                a.def7,                                                                ");
	    sql.append("                a.def8,                                                                ");
	    sql.append("                a.def9,                                                                ");
	    sql.append("                d.invclasscode,                                                        ");
	    sql.append("                d.invclassname,                                                        ");
	    sql.append("                substr(d.invclasscode, 0, 4) wzlb   ,  a.ts erprq                      ");
	    sql.append("           from bd_invbasdoc a, bd_cubasdoc b, bd_measdoc c, bd_invcl d                ");
	    sql.append("          where a.def1 = b.pk_cubasdoc                                                 ");
	    sql.append("            and nvl(a.sealflag,'N') = 'N'                                              ");
	    sql.append("            and a.pk_measdoc = c.pk_measdoc                                            ");
	    sql.append("            and a.pk_invcl = d.pk_invcl                                                ");
	    sql.append("            and a.invcode = '"+invcode+"'                                             ");
		Wzk wzk = (Wzk)DBUtil.getDBUtil(dbtype).queryForObject(sql.toString(), Wzk.class);
		System.out.println("getWzkByInvcode sql="+sql);
		return wzk;
	}

	public static Wzk getWzk(String dbtype,String invcode){
		StringBuffer sql = new StringBuffer();
	    sql.append("         select a.invclasscode, a.invcode, a.invname, a.invtype,                           ");
	    sql.append("                a.invspec,e.fjtjname,f.jstjname,c.custname,d.measname, g.zldj, a.def5, a.def6, a.def7, ");
	    sql.append("                a.def8, a.def9, a.def10,a.invclassname, f.jsgfbz,                          ");
	    sql.append("                 a.def12, a.def14, a.def13,                                     ");
	    sql.append("                 a.wzlb, a.erprq, a.pk_measdoc                                      ");
	    sql.append("         from (                                                                            ");
	    sql.append("                select a.invcode, a.invname, a.invtype, a.invspec,                         ");
	    sql.append("                	   a.def2, a.def3, a.def1, a.def4, a.def5, a.def6,                     ");
	    sql.append("                	   a.def7, a.def8, a.def9,a.def10, d.invclasscode, d.invclassname,             ");
	    sql.append("                       a.def12, a.def14, a.def13, substr(d.invclasscode, 0, 4) wzlb,       ");
	    sql.append("                       a.ts erprq, a.pk_measdoc                                            ");
	    sql.append("                from BD_INVBASDOC a,BD_INVCL d                                             ");
	   // sql.append("                where a.pk_invcl = d.pk_invcl and d.invclasscode like '"+wztype+"%'        ");
	   // sql.append("            		and nvl(a.sealflag,'N') = 'N'                                           ");
	    sql.append("                 where    a.pk_invcl = d.pk_invcl  and   a.invcode = '"+invcode+"'                                          ");
	    sql.append("         ) a                                                                               ");
	    sql.append("         left join BD_cumandoc b on a.def3 = b.pk_cumandoc                                 ");
	    sql.append("         left join BD_cubasdoc c on b.pk_cubasdoc = c.pk_cubasdoc                             ");
	    sql.append("         left join BD_MEASDOC d on a.pk_measdoc = d.pk_measdoc                             ");
	    sql.append("         left join SHHT_FJTJ e on a.def2 = e.pk_fjtj                                       ");
	    sql.append("         left join SHHT_JSTJ f on a.def1 = f.pk_jstj                                       ");
	    sql.append("         left join SHHT_ZLDJ_B g on a.def4 = g.pk_zldj_b                                   ");

	    Wzk wzk =(Wzk) DBUtil.getDBUtil(dbtype).queryForObject(sql.toString(), Wzk.class);
		System.out.println("queryWzk sql="+sql);
		return wzk;
	}

	/**
	 * 根据物资库分类，生成文件夹字符串
	 * @param dbtype
	 * @param wzklb
	 * @return
	 */
	public static String genFolderByWzkClass(String dbtype,String wzklb){
		String floder = "/Default";
		StringBuffer sb = new StringBuffer();
		sb.append("  select d.invclasscode, d.invclassname											 ");
		sb.append("    from bd_invcl d                                                               ");
		sb.append("   start with d.invclasscode = '"+wzklb+"'                                        ");
		sb.append("  connect by prior SUBSTR(d.invclasscode, 0, length(d.invclasscode) - 2) =        ");
		sb.append("              d.invclasscode                                                      ");
		sb.append("   order by d.invclasscode asc                                                    ");
		List<Map<String,Object>> mapList = DBUtil.getDBUtil(dbtype).getJdbcTemplate().queryForList(sb.toString());

		if(mapList==null) return floder;
		String invclasscode = null;
		String invclassname = null;
		for(int i=0;i<mapList.size();i++){
			invclasscode = (String)mapList.get(i).get("invclasscode");
			invclassname = (String)mapList.get(i).get("invclassname");

			floder = floder +"/"+ invclasscode + invclassname;

		}
		return floder;
	}

	/**
	 * 查询物资分类
	 * @param dbtype
	 * @return
	 */
	public static List<String> queryWzkClass(String dbtype){
		String sql = "select d.invclasscode, d.invclassname from bd_invcl d order by d.invclasscode";
		List<String> list = new ArrayList<String>();
		List<Map<String,Object>> mapList  = DBUtil.getDBUtil(dbtype).getJdbcTemplate().queryForList(sql);
		for(int i=0;i<mapList.size();i++){
			list.add((String)mapList.get(i).get("invclasscode"));
		}
		return list;
	}

}
