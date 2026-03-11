package ext.casc.ecn.changeinfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.hutool.core.util.StrUtil;
import ext.casc.integrate.process.ProcessService;
import ext.casc.util.IBAHelper;
import wt.change2.WTChangeOrder2;
import wt.pds.oracle81.OracleDataSource;

import com.glaway.mpm.util.DBConnUtil;
import wt.util.WTException;

public class ModifyChangeinfo {
	/*
	 *  更改单输入内容过多时与数据库交互 add by zhuhao 2017.5.24
	 */
	public static List<Map<String,Object>> searchAll(String objnum) throws SQLException{
		Connection conn = OracleDataSource.getOracleDataSource().getConnection();
        StringBuffer selectSQL = new StringBuffer();
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        Map<String, Object> data = null;
        selectSQL.append("SELECT * from CHANGEORDERCHANGEINFO WHERE  CHANGEINFO_INFOID='" + objnum + "' ORDER BY CHANGEINFO_INDEX");
        try {
            ps = conn.prepareStatement(selectSQL.toString());
            rs = ps.executeQuery();
            ResultSetMetaData rsmd = ps.getMetaData();
            // 取得结果集列数
            int columnCount = rsmd.getColumnCount();
            // 构造泛型结果集
            // 循环结果集
            while (rs.next()) {
                data = new HashMap<String, Object>();
                // 每循环一条将列名和列值存入Map
                for (int i = 1; i < columnCount; i++) {
                    data.put(rsmd.getColumnLabel(i), rs.getObject(rsmd.getColumnLabel(i)));
                }
                // 将整条数据的Map存入到List中
                datas.add(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(conn!=null){
                conn.close();
            }
            if(ps!=null){
                ps.close();
            }
            if(rs!=null){
                rs.close();
            }
        }
		return datas;
	}

	public static void conn(String cause,String designNums,String beforInfors,String afterInfors,String objnum){
		//先判断传来的值是否为空就执行删除方法
		if(!"".equals(cause)||!"".equals(designNums)||!"".equals(beforInfors)||!"".equals(afterInfors)){
			deleteinfo(objnum);
			//以"~"来拆分数组，并且处理最后一个元素为空而引起的下标越界
			String[] causes = cause.split("~");
			String[] designnumbers = designNums.split("~");
			if(designNums.endsWith("~")){
				designnumbers = new String[designnumbers.length+1];
				designnumbers = Arrays.copyOf(designNums.split("~"), designNums.split("~").length+1);
			}
		  	String[] beforinfors = beforInfors.split("~");
		  	String[] afterinfors = afterInfors.split("~");
		  	//循环读取元素
		  	for(int i=0;i<causes.length;i++){
		  		String uuid = UUID.randomUUID().toString();
		  		String cause1 = causes[i];
		  		String designnumber1 = designnumbers[i];
		  		String beforinfor1 = beforinfors[i];
		  		String afterinfor1 = afterinfors[i];
		  		int index = i+1;
		  		insert(uuid,cause1,designnumber1,beforinfor1,afterinfor1,objnum, index);
		  	}

			//更新更改单软属性信息
            try {
				WTChangeOrder2 order2 = ProcessService.getWTChangeOrder2ByNumber(objnum);
                if(order2 != null) {
					if(StrUtil.isNotEmpty(cause)) {
						IBAHelper.setIBAStringValue(order2, "CHANGECAUSE", cause);
					}
					if(StrUtil.isNotEmpty(designNums)) {
						designNums = designNums.replaceAll("null", "");
						IBAHelper.setIBAStringValue(order2, "DESIGNCHANGENUM", designNums);
					}
					if(StrUtil.isNotEmpty(beforInfors)) {
						IBAHelper.setIBAStringValue(order2, "CHANGEBEFOR", beforInfors);
					}
					if(StrUtil.isNotEmpty(afterInfors)) {
						IBAHelper.setIBAStringValue(order2, "CHANGEAFTER", afterInfors);
					}
                }
			} catch(WTException e) {
                e.printStackTrace();
            }

        }else{
			deleteinfo(objnum);
		}

	}

	public static void insert(String xuhao,String causes,String designNums,String beforInfors,String afterInfors,String objnum,int index){

		DBConnUtil conn = null;
		try {
			conn= new DBConnUtil();
			String sql = new StringBuffer()
			.append(" insert into CHANGEORDERCHANGEINFO")
			.append(" (CHANGEINFO_ID,CHANGEINFO_REASON,CHANGEINFO_PLAN,CHANGEINFO_BEFORE,CHANGEINFO_BEHIND,CHANGEINFO_INFOID,CHANGEINFO_INDEX)")
			.append(" values ('"+xuhao+"','"+causes+"','"+designNums+"','"+beforInfors+"','"+afterInfors+"','"+objnum+"','"+index+"')")
			.toString();
//			"delete from CHANGEORDERCHANGEINFO where CHANGEINFO_INFOID = '"+objnum+"' " +
//					"insert into CHANGEORDERCHANGEINFO (CHANGEINFO_ID,CHANGEINFO_REASON,CHANGEINFO_PLAN,CHANGEINFO_BEFORE,CHANGEINFO_BEHIND,CHANGEINFO_INFOID,CHANGEINFO_INDEX) values ('"+xuhao+"','"+causes+"','"+designNums+"','"+beforInfors+"','"+afterInfors+"','"+objnum+"','"+index+"')";
			conn.executeUpdate(sql);
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public static void deleteinfo(String objnum){
		DBConnUtil conn = null;
		try{
			conn= new DBConnUtil();
			String sql = new StringBuffer()
			.append(" delete from CHANGEORDERCHANGEINFO")
			.append(" where CHANGEINFO_INFOID = '"+objnum+"'")
			.toString();
			conn.executeUpdate(sql);
			conn.commit();
		} catch(Exception e){
			e.printStackTrace();
		}finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
