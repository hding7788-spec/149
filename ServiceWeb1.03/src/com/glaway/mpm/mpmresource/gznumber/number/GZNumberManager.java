package com.glaway.mpm.mpmresource.gznumber.number;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.log4j.LogR;
import wt.method.RemoteAccess;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.glaway.mpm.constants.Constants;
import com.glaway.mpm.util.DBConnUtil;

public class GZNumberManager implements RemoteAccess, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1056608402346291948L;
	public static int GENERATED_FLAG = 0;// 已申请未使用
	public static int USED_FLAG = 1; // 已使用
	public static int CANCELLED_FLAG = 2;// 已作废
	public static int GENANDUSED_FLAG = 3;// lq add, 已申请未使用和已使用
	public static int ALL_FLAG = 4;
	public static int APPROVED_FLAG = 5;// quxg add,用于标识已批准但未在系统中创建part的编号

	public static String DESC = "desc";
	public static String ASC = "";

	public static String EQUAL = "=";
	public static String LIKE = "LIKE";

	private static Logger logger = LogR.getLogger(GZNumberManager.class.getName());

	/**
	 * Search Number Object by number
	 * 
	 * @param number
	 * @return
	 */
	public static GZNumber getNumber(String number) {
		ArrayList result = getNumbersBy(GZNumber.GZNUMBER_NUMBER, GZNumberManager.EQUAL, number,
				GZNumberManager.ALL_FLAG, "");
		if (result.size() > 0) {
			return (GZNumber) result.get(0);
		} else {
			return null;
		}
	}

	public static ArrayList getNumbersLikeNumStr(String numberString, int usedFlag, String seqOrder) {
		ArrayList result = getNumbersBy(GZNumber.GZNUMBER_NUMBER, GZNumberManager.LIKE, "%" + numberString + "%",
				usedFlag, seqOrder);
		return result;
	}

	/**
	 * Search Numbers by its class path
	 * 
	 * @param classPath
	 * @param usedFlag
	 * @param orderby
	 * @return
	 */
	public static ArrayList getNumbersByClassPath(String classPath, int usedFlag, String seqOrder) {
		ArrayList result = getNumbersBy(GZNumber.GZNUMBER_CLASS, GZNumberManager.EQUAL, classPath, usedFlag, seqOrder);
		return result;
	}

	/**
	 * Search Numbers like its class path
	 * 
	 * @param classPath
	 * @param usedFlag
	 * @param seqOrder
	 * @return
	 */
	public static ArrayList getNumberLikeClassPath(String classPath, int usedFlag, String seqOrder) {
		ArrayList result = getNumbersBy(GZNumber.GZNUMBER_CLASS, GZNumberManager.LIKE, classPath + "%", usedFlag,
				seqOrder);
		return result;
	}

	/**
	 * Search Numbers by the requestor
	 * 
	 * @param registerUser
	 * @param usedFlag
	 * @param orderby
	 * @return
	 */
	public static ArrayList getNumbersByRequestor(String requestor, int usedFlag, String seqOrder) {
		ArrayList result = new ArrayList();
		if (requestor.equals(AdministrationHelper.ADMINISTRATOR)) {
			result = getNumbersBy(GZNumber.GZNUMBER_REQUESTOR, GZNumberManager.LIKE, "%", usedFlag, seqOrder);
		} else {
			result = getNumbersBy(GZNumber.GZNUMBER_REQUESTOR, GZNumberManager.EQUAL, requestor, usedFlag, seqOrder);
		}
		return result;
	}

	/**
	 * private static ArrayList getNumbersBy(String searchBy, String operation,
	 * String searchValue, Integer usedFlag, String seqOrder){ try { if
	 * (!RemoteMethodServer.ServerFlag) { return (ArrayList)
	 * RemoteMethodServer.getDefault().invoke("getNumbersByRemote",
	 * NumberManager.class.getName(), null, new Class[] {String.class,
	 * String.class, String.class, Integer.class, String.class}, new Object[]
	 * {searchBy, operation, searchValue, usedFlag, seqOrder}); } else { return
	 * getNumbersByRemote(searchBy, operation, searchValue, usedFlag, seqOrder);
	 * } } catch (Exception e) { e.printStackTrace(); } return new ArrayList();
	 * }
	 **/

	public static ArrayList getNumbersBy(String searchBy, String operation, String searchValue, Integer usedFlag,
			String seqOrder) {
		ArrayList result = new ArrayList();

		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();

			String sql = "SELECT * FROM GLAWAY_GZNUMBER WHERE " + searchBy + " " + operation + " '" + searchValue + "'";

			if (usedFlag != ALL_FLAG) {
				sql = sql + " AND " + GZNumber.GZNUMBER_FLAG + " = " + usedFlag;
			}
			sql = sql + " ORDER BY " + GZNumber.GZNUMBER_SEQ + " " + seqOrder;

			ResultSet rs = conn.executeQuery(sql);
			GZNumber gNumber;
			while (rs.next()) {
				gNumber = new GZNumber();
				gNumber.setNumber(rs.getString(GZNumber.GZNUMBER_NUMBER));
				gNumber.setClasspath(rs.getString(GZNumber.GZNUMBER_CLASS));
				gNumber.setObjectname(rs.getString(GZNumber.GZNUMBER_NAME));
				gNumber.setFlag(rs.getInt(GZNumber.GZNUMBER_FLAG));
				gNumber.setRequestor(rs.getString(GZNumber.GZNUMBER_REQUESTOR));
				gNumber.setDatetime(rs.getString(GZNumber.GZNUMBER_REQUESTDATETIME));
				gNumber.setRequestdesc(rs.getString(GZNumber.GZNUMBER_REQUESTDESC));
				gNumber.setCanceldesc(rs.getString(GZNumber.GZNUMBER_CANCELDESC));
				gNumber.setSeq(rs.getInt(GZNumber.GZNUMBER_SEQ));
				result.add(gNumber);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	public static ArrayList getNumbersBySQL(String sql) {
		ArrayList result = new ArrayList();

		if (sql.equals("") || sql == null)
			return result;

		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();

			logger.debug(sql);

			ResultSet rs = conn.executeQuery(sql);
			GZNumber gNumber;
			while (rs.next()) {
				gNumber = new GZNumber();
				gNumber.setNumber(rs.getString(GZNumber.GZNUMBER_NUMBER));
				gNumber.setClasspath(rs.getString(GZNumber.GZNUMBER_CLASS));
				gNumber.setObjectname(rs.getString(GZNumber.GZNUMBER_NAME));
				gNumber.setFlag(rs.getInt(GZNumber.GZNUMBER_FLAG));
				gNumber.setRequestor(rs.getString(GZNumber.GZNUMBER_REQUESTOR));
				gNumber.setDatetime(rs.getString(GZNumber.GZNUMBER_REQUESTDATETIME));
				gNumber.setRequestdesc(rs.getString(GZNumber.GZNUMBER_REQUESTDESC));
				gNumber.setCanceldesc(rs.getString(GZNumber.GZNUMBER_CANCELDESC));
				gNumber.setSeq(rs.getInt(GZNumber.GZNUMBER_SEQ));
				result.add(gNumber);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	public static ArrayList getNumbersBySQL2(String sql) {
		ArrayList result = new ArrayList();
		ArrayList numberList = new ArrayList();
		ArrayList numberNameList = new ArrayList();
		result.add(numberList);
		result.add(numberNameList);
		if (sql.equals("") || sql == null)
			return result;

		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();

			logger.debug(sql);

			ResultSet rs = conn.executeQuery(sql);
			while (rs.next()) {
				numberList.add(rs.getString(GZNumber.GZNUMBER_NUMBER));
				numberNameList.add(rs.getString(GZNumber.GZNUMBER_NUMBER) + Constants.gzNameSplitStr
						+ rs.getString(GZNumber.GZNUMBER_NAME));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	public static Map getNumbersBySQL3(String sql) {
		Map result = new LinkedHashMap();
		if (sql.equals("") || sql == null)
			return result;

		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();

			logger.debug(sql);

			ResultSet rs = conn.executeQuery(sql);
			while (rs.next()) {
				result.put(rs.getString(GZNumber.GZNUMBER_NUMBER), rs.getString(GZNumber.GZNUMBER_NAME));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	public static int getMaxSequence(String classPath, Integer usedFlag) {
		int result = -1;

		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();

			String sql = "SELECT Max(" + GZNumber.GZNUMBER_SEQ + ") AS MaxSeq FROM GLAWAY_GZNUMBER WHERE "
					+ GZNumber.GZNUMBER_CLASS + " " + GZNumberManager.EQUAL + " '" + classPath + "' ";

			if (usedFlag != ALL_FLAG) {
				sql = sql + " AND " + GZNumber.GZNUMBER_FLAG + " = " + usedFlag;
			}
			System.out.println(sql);
			ResultSet rs = conn.executeQuery(sql);

			if (rs.next()) {
				if (rs.getString("MaxSeq") != null) {
					result = Integer.parseInt(rs.getString("MaxSeq"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	// 得到最新版本的S编号
	public static String getLatestSNumber(String figureNumber, String gnclass) {
		String result = "";
		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();

			// select max(gn_number) as maxnum from GLAWAY_GZNUMBER a where
			// gn_number like '%2.4918%' and gn_class = 'RootS2.491'

			String sql = "SELECT max(" + GZNumber.GZNUMBER_SEQ + ") as maxnum  FROM GLAWAY_GZNUMBER WHERE "
					+ GZNumber.GZNUMBER_CLASS + " " + GZNumberManager.EQUAL + " '" + gnclass + "' AND "
					+ GZNumber.GZNUMBER_NUMBER + " " + GZNumberManager.LIKE + " '%" + figureNumber + "%'";

			ResultSet rs = conn.executeQuery(sql);

			if (rs.next()) {
				if (rs.getString("maxnum") != null) {
					result = rs.getString("maxnum");
					result = figureNumber + result;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/*
	 * 得到编号的状态flag
	 */
	public static String getNumberState(String figureNumber) {
		String result = "";
		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();

			// select GN_Flag as flag from GLAWAY_GZNUMBER where gn_number =
			// 'figureNumber'

			String sql = "SELECT " + GZNumber.GZNUMBER_FLAG + " as flag  FROM GLAWAY_GZNUMBER WHERE "
					+ GZNumber.GZNUMBER_NUMBER + " " + GZNumberManager.EQUAL + " '" + figureNumber + "'";

			ResultSet rs = conn.executeQuery(sql);

			if (rs.next()) {
				if (rs.getString("flag") != null) {
					result = rs.getString("flag");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public void createSSPNumber(String gn_number, String gn_class, String gn_requestor, String gn_requestdesc,
			String gn_seq) throws Exception {

		DBConnUtil conn = null;

		try {
			conn = new DBConnUtil();

			String sql;

			sql = "SELECT * FROM GLAWAY_GZNUMBER WHERE " + GZNumber.GZNUMBER_NUMBER + " = '" + gn_number + "'";

			ResultSet rs = conn.executeQuery(sql);
			if (!rs.next()) {
				sql = "INSERT INTO GLAWAY_GZNUMBER " + "(" + GZNumber.GZNUMBER_NUMBER + "," + GZNumber.GZNUMBER_CLASS
						+ "," + GZNumber.GZNUMBER_REQUESTOR + "," + GZNumber.GZNUMBER_REQUESTDATETIME + ","
						+ GZNumber.GZNUMBER_FLAG + "," + GZNumber.GZNUMBER_REQUESTDESC
						+ ","
						// + GZNumber.GZNUMBER_NAME + ","
						// + GZNumber.GZNUMBER_CANCELDESC + ","
						+ GZNumber.GZNUMBER_SEQ + ") " + "VALUES('" + gn_number + "','" + gn_class + "','"
						+ gn_requestor + "',sysdate,0,'" + gn_requestdesc
						// + "," + gn_name + "," + gn_canceldesc
						+ "'," + gn_seq + ")";
			} else {// 重新使用已作废的编号
				sql = "UPDATE GLAWAY_GZNUMBER SET " + GZNumber.GZNUMBER_REQUESTOR + " = '" + gn_requestor + "', "
						+ GZNumber.GZNUMBER_REQUESTDATETIME + " = sysdate" + ", " + GZNumber.GZNUMBER_FLAG + " = 0"
						+ ", " + GZNumber.GZNUMBER_REQUESTDESC + " = '" + gn_requestdesc + "' "
						// + GZNumber.GZNUMBER_NAME + " = "
						// + gn_name + " "
						+ " WHERE " + GZNumber.GZNUMBER_NUMBER + " = '" + gn_number + "'";
			}

			conn.executeUpdate(sql);
			conn.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			conn.rollback();
			throw new Exception("*** Register Terminated!! ***  Register number: " + gn_number + " Failed!");
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 判断P整件编号是否存在
	public static boolean checkPNumber(String figureNumber) {
		// String Requestor = AdministrationHelper.getUser();
		String Requestor = "";// 也可以基于其他人的p整件生成sp整件号
		int flag = ALL_FLAG;
		return checkNumber(figureNumber, Requestor, flag);
	}

	public static boolean isNumberExists(String number) {
		return checkNumber(number, "", ALL_FLAG);
	}

	// 判断编号是否存在
	public static boolean checkNumber(String figureNumber, String Requestor, int flag) {
		boolean bReturn = false;

		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();

			String sql = "SELECT " + GZNumber.GZNUMBER_NUMBER + "  as num FROM GLAWAY_GZNUMBER WHERE "
					+ GZNumber.GZNUMBER_NUMBER + " = '" + figureNumber + "'";

			if (Requestor != null && !Requestor.equals("")) {
				sql = sql + " AND " + GZNumber.GZNUMBER_REQUESTOR + " = '" + Requestor + "'";
			}
			if (flag != ALL_FLAG) {
				sql = sql + " AND " + GZNumber.GZNUMBER_FLAG + " = " + flag;
			}

			ResultSet rs = conn.executeQuery(sql);

			if (rs.next()) {
				if (rs.getString("num") != null) {
					bReturn = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return bReturn;
	}

	// ywu 2011/11/8
	// QG/AL的顺序号计算需基于分类号
	public static String getLatestNumSeq(String Number, String classNumber, String gnclass) {
		String result = "";

		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();

			if ((Number.startsWith("QG") || Number.startsWith("QS")) && classNumber != null
					&& classNumber.trim().length() > 0)
				Number = Number + "/AL" + classNumber;

			String sql = "SELECT max(" + GZNumber.GZNUMBER_SEQ + ") as maxseq  FROM GLAWAY_GZNUMBER WHERE "
					+ GZNumber.GZNUMBER_CLASS + " " + GZNumberManager.LIKE + " '" + gnclass + "%' AND "
					+ GZNumber.GZNUMBER_NUMBER + " " + GZNumberManager.LIKE + " '" + Number + "%'";

			ResultSet rs = conn.executeQuery(sql);

			if (rs.next()) {
				if (rs.getString("maxseq") != null) {
					result = rs.getString("maxseq");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	// 得到最新版本的企业标准编号的序列号
	// 序列号的递增只与企业标准4大文档有关，与分册号和分类号
	public static String getLatestNumSeq(String Number, String gnclass) {
		String result = "";

		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();

			String sql = "SELECT max(" + GZNumber.GZNUMBER_SEQ + ") as maxseq  FROM GLAWAY_GZNUMBER WHERE "
					+ GZNumber.GZNUMBER_CLASS + " " + GZNumberManager.LIKE + " '" + gnclass + "%' AND "
					+ GZNumber.GZNUMBER_NUMBER + " " + GZNumberManager.LIKE + " '" + Number + "%'";

			ResultSet rs = conn.executeQuery(sql);

			if (rs.next()) {
				if (rs.getString("maxseq") != null) {
					result = rs.getString("maxseq");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public void createQYNumber(String gn_number, String gn_class, String gn_requestor, String gn_requestdesc,
			String gn_seq, String flag) throws Exception {

		DBConnUtil conn = null;

		try {
			conn = new DBConnUtil();

			String sql;

			sql = "INSERT INTO GLAWAY_GZNUMBER " + "(" + GZNumber.GZNUMBER_NUMBER + "," + GZNumber.GZNUMBER_CLASS + ","
					+ GZNumber.GZNUMBER_REQUESTOR + "," + GZNumber.GZNUMBER_REQUESTDATETIME + ","
					+ GZNumber.GZNUMBER_FLAG + "," + GZNumber.GZNUMBER_REQUESTDESC
					+ ","
					// + GZNumber.GZNUMBER_NAME + ","
					// + GZNumber.GZNUMBER_CANCELDESC + ","
					+ GZNumber.GZNUMBER_SEQ + ") " + "VALUES('" + gn_number + "','" + gn_class + "','" + gn_requestor
					+ "',sysdate," + flag + ",'" + gn_requestdesc
					// + "," + gn_name + "," + gn_canceldesc
					+ "'," + gn_seq + ")";

			conn.executeUpdate(sql);
			conn.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			conn.rollback();
			// throw new
			// Exception("*** Register Terminated!! ***  Register number: " +
			// gn_number + " Failed!");
			throw ex;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Search Numbers by the type
	 * 
	 * @param requestor
	 * @param type
	 * @return number list
	 */
	public static ArrayList getNumbersByType(String requestor, String type) throws Exception {
		ArrayList resultList = new ArrayList();
		ArrayList numList = new ArrayList();
		String seqOrder = "desc";
		String gnclass = "";
		String num = "";

		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();

			String sql = "SELECT " + GZNumber.GZNUMBER_NUMBER + " as number1  FROM GLAWAY_GZNUMBER WHERE "
					+ GZNumber.GZNUMBER_FLAG + " " + GZNumberManager.EQUAL + "0";

			if (requestor != null && !"".equals(requestor)) {
				sql += " AND " + GZNumber.GZNUMBER_REQUESTOR + " " + GZNumberManager.EQUAL + " '" + requestor + "'";
			}

			// 按照类型过滤，得到对应的编号集
			// 在成品页面下，只显示AL1-4级号
			// 在自制件页面下，只显示AL2-AL8级所有号（包括ALJ）, 去掉AL4.999%的软件part和ALK(1~8)
			// 在科研件页面下，只显示ALK号
			// 在软件Part页面下，只显示AL4.999号
			// 企业标准文件取号，分系类：Q/AL企业标准文件，QZ/AL指导性技术文件，QG/AL管理标准文件，QS/AL质量体系文件
			if (type.equalsIgnoreCase("CP")) {
				sql += " AND " + GZNumber.GZNUMBER_CLASS + " " + GZNumberManager.LIKE + " 'RootA%'" + " AND ("
						+ GZNumber.GZNUMBER_NUMBER + " " + GZNumberManager.LIKE + " 'AL1.%'" + " OR "
						+ GZNumber.GZNUMBER_NUMBER + " " + GZNumberManager.LIKE + " 'AL2.%'" + " OR "
						+ GZNumber.GZNUMBER_NUMBER + " " + GZNumberManager.LIKE + " 'AL3.%'" + " OR "
						+ GZNumber.GZNUMBER_NUMBER + " " + GZNumberManager.LIKE + " 'AL4.%')" + " AND NOT ("
						+ GZNumber.GZNUMBER_NUMBER + " " + GZNumberManager.LIKE + " 'AL4.999%')";

			} else if (type.equalsIgnoreCase("AL")) {
				sql +=
				// " AND "+ GZNumber.GZNUMBER_CLASS + " " +
				// NumberManager.LIKE + " 'RootA%'" +
				" AND NOT (" + GZNumber.GZNUMBER_NUMBER
						+ " "
						+ GZNumberManager.LIKE
						+ " 'AL0.%')"
						+
						// " AND NOT ("+ GZNumber.GZNUMBER_NUMBER + " " +
						// NumberManager.LIKE + " 'AL1.%')" +
						" AND NOT (" + GZNumber.GZNUMBER_NUMBER + " " + GZNumberManager.LIKE + " 'ALK%')"
						+ " AND NOT (" + GZNumber.GZNUMBER_NUMBER + " " + GZNumberManager.LIKE + " 'Q%')"
						+ " AND NOT (" + GZNumber.GZNUMBER_NUMBER + " " + GZNumberManager.LIKE + " 'AL4.999%')";
			} else if (type.equalsIgnoreCase("ALK")) {
				sql += " AND " + GZNumber.GZNUMBER_CLASS + " " + GZNumberManager.LIKE + " 'RootC%'";
			} else if (type.equalsIgnoreCase("S")) {
				sql += " AND " + GZNumber.GZNUMBER_CLASS + " " + GZNumberManager.LIKE + " 'RootA%'" + " AND "
						+ GZNumber.GZNUMBER_NUMBER + " " + GZNumberManager.LIKE + " 'AL4.999%'";
			} else if (type.equalsIgnoreCase("Q")) {
				sql += " AND " + GZNumber.GZNUMBER_CLASS + " " + GZNumberManager.LIKE + " 'RootQ/AL%'";
			} else if (type.equalsIgnoreCase("QZ")) {
				sql += " AND " + GZNumber.GZNUMBER_CLASS + " " + GZNumberManager.LIKE + " 'RootQZ/AL%'";
			} else if (type.equalsIgnoreCase("QG")) {
				sql += " AND " + GZNumber.GZNUMBER_CLASS + " " + GZNumberManager.LIKE + " 'RootQG/AL%'";
			} else if (type.equalsIgnoreCase("QS")) {
				sql += " AND " + GZNumber.GZNUMBER_CLASS + " " + GZNumberManager.LIKE + " 'RootQS/AL%'";
			}

			sql += " order by " + GZNumber.GZNUMBER_NUMBER;

			ResultSet rs = conn.executeQuery(sql);

			while (rs.next()) {
				String strNum = rs.getString("number1");
				if (strNum != null && !strNum.equals("")) {
					resultList.add(strNum);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("数据库操作失败。");
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return resultList;
	}

	/**
	 * Search Numbers by the type
	 * 
	 * @param registerUser
	 * @param usedFlag
	 * @param orderby
	 * @return
	 * @throws Exception
	 */
	public static void setNumberUsed(String number) throws Exception {
		String sql = "";

		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();

			sql = "UPDATE GLAWAY_GZNUMBER SET " + GZNumber.GZNUMBER_REQUESTDATETIME + " = sysdate, "
					+ GZNumber.GZNUMBER_FLAG + " = " + 1 + " " + " WHERE " + GZNumber.GZNUMBER_NUMBER + " = '" + number
					+ "'";

			conn.executeUpdate(sql);
			conn.commit();

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("数据库更新失败。");
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Set number unUsed
	 * 
	 * @param registerUser
	 * @param usedFlag
	 * @param orderby
	 * @return
	 * @throws Exception
	 */
	public static void setNumberUnUsed(String number) {
		String sql = "";

		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();

			sql = "UPDATE GLAWAY_GZNUMBER SET " + GZNumber.GZNUMBER_REQUESTDATETIME + " = sysdate, "
					+ GZNumber.GZNUMBER_FLAG + " = " + 0 + " " + " WHERE " + GZNumber.GZNUMBER_NUMBER + " = '" + number
					+ "'" + " AND " + GZNumber.GZNUMBER_FLAG + " = " + 1;

			conn.executeUpdate(sql);
			conn.commit();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 获取该用户申请的未使用的工装编号
	 * 
	 * @author qianlong
	 * @date 2013-3-26
	 * @return
	 * @throws WTException
	 * 
	 */
	public static ArrayList getSearchGZNumber() throws WTException {

		String sql = "SELECT * FROM GLAWAY_GZNUMBER WHERE 1=1 ";

		sql = sql + " AND " + GZNumber.GZNUMBER_FLAG + " = " + GZNumberManager.GENERATED_FLAG;

		if (!AdministrationHelper.getUser().equals(AdministrationHelper.ADMINISTRATOR)) {
			sql = sql + " AND  UPPER(" + GZNumber.GZNUMBER_REQUESTOR + " ) " + GZNumberManager.LIKE + " '"
					+ SessionHelper.getPrincipal().getName().toUpperCase() + "' ";
		}
		sql = sql + "ORDER BY " + GZNumber.GZNUMBER_NUMBER + " ASC";
		return GZNumberManager.getNumbersBySQL2(sql);
	}

	/**
	 * 获取该用户申请的未使用的工装编号
	 * 
	 * @author qianlong
	 * @date 2013-3-26
	 * @return
	 * @throws WTException
	 * 
	 */
	public static Map getSearchGZNumber2() throws WTException {

		String sql = "SELECT * FROM GLAWAY_GZNUMBER WHERE 1=1 ";

		sql = sql + " AND " + GZNumber.GZNUMBER_FLAG + " = " + GZNumberManager.GENERATED_FLAG;

		if (!AdministrationHelper.getUser().equals(AdministrationHelper.ADMINISTRATOR)) {
			sql = sql + " AND  UPPER(" + GZNumber.GZNUMBER_REQUESTOR + " ) " + GZNumberManager.LIKE + " '"
					+ SessionHelper.getPrincipal().getName().toUpperCase() + "' ";
		}
		sql = sql + "ORDER BY " + GZNumber.GZNUMBER_NUMBER + " ASC";
		return GZNumberManager.getNumbersBySQL3(sql);
	}

}
