package com.glaway.mpm.mpmresource.gznumber.number;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.OrganizationServicesHelper;
import wt.org.WTUser;

import com.glaway.mpm.constants.Constants;
import com.glaway.mpm.mpmresource.gznumber.bean.PropertiesBean;
import com.glaway.mpm.mpmresource.gznumber.bean.RequestInfoContained;
import com.glaway.mpm.util.DBConnUtil;

public class GZNumberRegisterManager implements RemoteAccess, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7051284998306004922L;

	/**
	 * public void cancelOneNumber(String number, String desc) throws Exception{
	 * try { if (!RemoteMethodServer.ServerFlag) {
	 * RemoteMethodServer.getDefault().invoke("cancelOneNumberRemote",
	 * NumberRegisterManager.class.getName(), null, new Class[] {String.class,
	 * String.class}, new Object[] {number, desc}); } else {
	 * cancelOneNumberRemote(number, desc); } } catch (Exception e) { throw e; }
	 * }
	 **/

	public void cancelOneNumber(String number, String desc) throws Exception {
		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();

			String sql;
			sql = "SELECT * FROM GLAWAY_GZNUMBER WHERE " + GZNumber.GZNUMBER_NUMBER + " = '" + number + "'";
			ResultSet rs = conn.executeQuery(sql);

			if (rs.next()) {
				sql = "UPDATE GLAWAY_GZNUMBER SET " + GZNumber.GZNUMBER_FLAG + " = " + GZNumberManager.CANCELLED_FLAG
						+ ", " + GZNumber.GZNUMBER_CANCELDESC + " = '" + desc + "'" + " WHERE "
						+ GZNumber.GZNUMBER_NUMBER + " = '" + number + "' ";
			}

			conn.executeUpdate(sql);
			conn.commit();
			/*
			 * // yanqi-2009-12-09-驳回后向申请者发送邮件通知 GZNumber numberObj =
			 * NumberManager.getNumber(number); WTUser requestorUser =
			 * OrganizationServicesHelper.manager
			 * .getUser(numberObj.getRequestor()); WTUser approveUser = null;
			 * String approveUserStr = ""; if (numberObj.getRequestdesc() !=
			 * null) { approveUserStr =
			 * numberObj.getRequestdesc().indexOf("审批者：") >= 0 ? numberObj
			 * .getRequestdesc().substring(
			 * numberObj.getRequestdesc().indexOf("审批者：") + 4,
			 * numberObj.getRequestdesc().length()) : "";
			 * 
			 * if (!approveUserStr.equals("")) { approveUser =
			 * OrganizationServicesHelper.manager .getUser(approveUserStr); }
			 * else { approveUserStr = "取号系统管理员"; } } String frommail = "";
			 * frommail = approveUser.getEMail(); frommail =
			 * "PDMsystem@careri.com"; MailUtil.sendEmail(frommail,
			 * requestorUser.getEMail(), "[PDM系统通知]申请编号被驳回！", "您申请的编号：" + number
			 * + "已经被 " + approveUserStr + " 驳回,原因是：<font color=red><b>" + desc
			 * + "</b></font><br>详情请进入系统查看。<br>[本邮件由系统自动发出，请勿回复]");
			 */
		} catch (Exception ex) {
			ex.printStackTrace();
			conn.rollback();
			throw new Exception("*** Cancel Terminated!! ***  Cancel number: " + number + " Failed!");
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("*** Cancel Terminated!! ***  Cancel number: " + number + " Failed!");
			}
		}
	}

	public void ApproveOneNumber(String number, String desc) throws Exception {
		DBConnUtil conn = null;

		// 转变编号flag为已批准
		conn = new DBConnUtil();

		String sql;
		sql = "SELECT * FROM GLAWAY_GZNUMBER WHERE " + GZNumber.GZNUMBER_NUMBER + " = '" + number + "'";
		ResultSet rs = conn.executeQuery(sql);

		if (rs.next()) {
			sql = "UPDATE GLAWAY_GZNUMBER SET " + GZNumber.GZNUMBER_FLAG + " = " + GZNumberManager.APPROVED_FLAG + ", "
					+ GZNumber.GZNUMBER_CANCELDESC + " = '" + desc + "'" + " WHERE " + GZNumber.GZNUMBER_NUMBER
					+ " = '" + number + "' ";
			conn.executeUpdate(sql);
			conn.commit();
		} else {
			throw new Exception("错误：该编号没有在取号系统注册,number=" + number + " ！");
		}

		try {
			// yanqi-2009-12-09-批准后向申请者发送邮件通知
			GZNumber numberObj = GZNumberManager.getNumber(number);
			WTUser requestorUser = OrganizationServicesHelper.manager.getUser(numberObj.getRequestor());
			WTUser approveUser = null;
			if (numberObj.getRequestdesc() != null) {

				String approveUserStr = numberObj.getRequestdesc().indexOf("审批者：") >= 0 ? numberObj.getRequestdesc()
						.substring(numberObj.getRequestdesc().indexOf("审批者：") + 4, numberObj.getRequestdesc().length())
						: "";

				if (!approveUserStr.equals("")) {
					approveUser = OrganizationServicesHelper.manager.getUser(approveUserStr);
				}
			}
			String frommail = "";
			frommail = approveUser.getEMail();
			frommail = "PDMsystem@careri.com";
			// MailUtil.sendEmail(frommail, requestorUser.getEMail(),
			// "[PDM系统通知]申请编号已批准可用！", "您申请的编号：" + number
			// + "已经被批准可以使用（或重置为被批准状态）。" + "<p>您收到这封邮件的具体原因可能是：" +
			// "<br>1.您申请的编号被取号系统管理员批准可以使用；"
			// + "<br>2.该编号的零部件被从产品库中删除，系统通知该编号的申请者可以重新使用该编号创建零部件。" +
			// "<p>[本邮件由系统自动发出，请勿回复]");
		} catch (Exception mailEx) {
			// throw new Exception("***发生错误 Mail Error! ***  Cancel number: "+
			// number + " Failed!--------end");
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("*** ***发生错误A002! ***  Cancel number: " + number + " Failed!请联系管理员。");
			}
		}
	}

	/**
	 * public void registerOneNumber(NumberInfoContained gnumber,
	 * RequestInfoContained requestInfo) throws Exception{ try { if
	 * (!RemoteMethodServer.ServerFlag) {
	 * RemoteMethodServer.getDefault().invoke("registerOneNumberRemote",
	 * NumberRegisterManager.class.getName(), null, new Class[]
	 * {NumberInfoContained.class, RequestInfoContained.class}, new Object[]
	 * {gnumber, requestInfo}); } else { registerOneNumberRemote(gnumber,
	 * requestInfo); } } catch (Exception e) { throw e; } }
	 **/

	public void registerOneNumber(GZNumberInfoContained gnumber, RequestInfoContained requestInfo) throws Exception {
		System.out.println("Begin registerOneNumberRemote=======");

		String gn_number = "'" + gnumber.getNumber() + "' ";
		String gn_class = "'" + gnumber.getClasspath() + "' ";
		String gn_requestor = "'" + requestInfo.getRequestor() + "'";
		String gn_requestdatetime = "";
		if (requestInfo.getDatetime() != null && !requestInfo.getDatetime().equals("")) {
			gn_requestdatetime = "to_date('" + requestInfo.getDatetime() + "','yyyy-mm-dd hh24:mi:ss') ";
		} else {
			Date now = new Date();
			DateFormat d1 = DateFormat.getDateTimeInstance();
			d1.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
			String str = d1.format(now);
			gn_requestdatetime = "to_date('" + str + "','yyyy-mm-dd hh24:mi:ss') ";
		}

		String gn_flag = gnumber.getFlag() + " ";
		String gn_requestdesc = "'" + requestInfo.getRequestdesc() + "' ";
		String gn_name = "'" + requestInfo.getObjectname() + "' ";
		String gn_canceldesc = "'" + requestInfo.getCanceldesc() + " ' ";
		String gn_seq = String.valueOf(gnumber.getSeq()) + " ";

		DBConnUtil conn = null;

		try {
			conn = new DBConnUtil();

			String sql;
			sql = "SELECT * FROM GLAWAY_GZNUMBER WHERE " + GZNumber.GZNUMBER_CLASS + " = " + gn_class + " AND "
					+ GZNumber.GZNUMBER_SEQ + " = " + gn_seq;

			ResultSet rs = conn.executeQuery(sql);

			if (!rs.next()) {
				sql = "INSERT INTO GLAWAY_GZNUMBER " + "(" + GZNumber.GZNUMBER_NUMBER + "," + GZNumber.GZNUMBER_CLASS
						+ "," + GZNumber.GZNUMBER_REQUESTOR + "," + GZNumber.GZNUMBER_REQUESTDATETIME + ","
						+ GZNumber.GZNUMBER_FLAG + "," + GZNumber.GZNUMBER_REQUESTDESC
						+ ","
						// + GZNumber.GZNUMBER_NAME + ","
						// + GZNumber.GZNUMBER_CANCELDESC + ","
						+ GZNumber.GZNUMBER_SEQ + ") " + "VALUES(" + gn_number + "," + gn_class + "," + gn_requestor
						+ "," + gn_requestdatetime + "," + gn_flag + "," + gn_requestdesc
						// + "," + gn_name + "," + gn_canceldesc
						+ "," + gn_seq + ")";
			} else {// 重新使用已作废的编号
				sql = "UPDATE GLAWAY_GZNUMBER SET "
						// + GZNumber.GZNUMBER_NUMBER + " = "
						// + gn_number + ", "
						+ GZNumber.GZNUMBER_REQUESTOR + " = " + gn_requestor + ", " + GZNumber.GZNUMBER_REQUESTDATETIME
						+ " = " + gn_requestdatetime + ", " + GZNumber.GZNUMBER_FLAG + " = " + gn_flag + ", "
						+ GZNumber.GZNUMBER_REQUESTDESC
						+ " = "
						+ gn_requestdesc
						+ " "
						// + GZNumber.GZNUMBER_NAME + " = "
						// + gn_name + ", "
						// + GZNumber.GZNUMBER_CANCELDESC + " = "
						// + gn_canceldesc + " "
						+ " WHERE " + GZNumber.GZNUMBER_CLASS + " = " + gn_class + " AND " + GZNumber.GZNUMBER_SEQ
						+ " = " + gn_seq;
			}
			conn.executeUpdate(sql);
			conn.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			conn.rollback();
			throw new Exception("*** Register Terminated!! ***  Register number: " + gn_number + " Failed!"
					+ ex.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void registerUsedNumber(String gn_number) throws Exception {
		gn_number = "'" + gn_number + "' ";

		DBConnUtil conn = null;

		try {
			conn = new DBConnUtil();

			String sql;
			sql = "UPDATE GLAWAY_GZNUMBER SET " + GZNumber.GZNUMBER_FLAG + " = 2 " + " WHERE "
					+ GZNumber.GZNUMBER_NUMBER + " = " + gn_number;

			conn.executeUpdate(sql);
			conn.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			conn.rollback();
			throw new Exception("*** Set Used Flag Terminated!! *** number: " + gn_number + " Failed!");
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void registerOneNumber1(GZNumberInfoContained gnumber, RequestInfoContained requestInfo) throws Exception {
		System.out.println("Begin registerOneNumberRemote=======");

		String gn_number = "'" + gnumber.getNumber() + "' ";
		String gn_class = "'" + gnumber.getClasspath() + "' ";
		String gn_requestor = "'" + requestInfo.getRequestor() + "'";
		String gn_requestdatetime = "''";
		if (requestInfo.getDatetime() != null && !requestInfo.getDatetime().equals("")) {
			gn_requestdatetime = "to_date('" + requestInfo.getDatetime() + "','yyyy-mm-dd hh24:mi:ss') ";
		}

		String gn_flag = gnumber.getFlag() + " ";
		String gn_requestdesc = "'" + requestInfo.getRequestdesc() + "' ";
		String gn_name = "'" + requestInfo.getObjectname() + "' ";
		String gn_canceldesc = "'" + requestInfo.getCanceldesc() + " ' ";
		String gn_seq = String.valueOf(gnumber.getSeq()) + " ";

		DBConnUtil conn = null;

		try {
			conn = new DBConnUtil();

			String sql;
			sql = "SELECT * FROM GLAWAY_GZNUMBER WHERE " + GZNumber.GZNUMBER_CLASS + " = " + gn_class + " AND "
					+ GZNumber.GZNUMBER_SEQ + " = " + gn_seq;

			ResultSet rs = conn.executeQuery(sql);

			if (!rs.next()) {
				sql = "INSERT INTO GLAWAY_GZNUMBER " + "(" + GZNumber.GZNUMBER_NUMBER + "," + GZNumber.GZNUMBER_CLASS
						+ "," + GZNumber.GZNUMBER_REQUESTOR + "," + GZNumber.GZNUMBER_REQUESTDATETIME + ","
						+ GZNumber.GZNUMBER_FLAG + "," + GZNumber.GZNUMBER_REQUESTDESC
						+ ","
						// + GZNumber.GZNUMBER_NAME + ","
						// + GZNumber.GZNUMBER_CANCELDESC + ","
						+ GZNumber.GZNUMBER_SEQ + ") " + "VALUES(" + gn_number + "," + gn_class + "," + gn_requestor
						+ "," + gn_requestdatetime + "," + gn_flag + "," + gn_requestdesc
						// + "," + gn_name + "," + gn_canceldesc
						+ "," + gn_seq + ")";
			} else {// 重新使用已作废的编号
				sql = "UPDATE GLAWAY_GZNUMBER SET "
						// + GZNumber.GZNUMBER_NUMBER + " = "
						// + gn_number + ", "
						+ GZNumber.GZNUMBER_REQUESTOR + " = " + gn_requestor + ", " + GZNumber.GZNUMBER_REQUESTDATETIME
						+ " = " + gn_requestdatetime + ", " + GZNumber.GZNUMBER_FLAG + " = " + gn_flag + ", "
						+ GZNumber.GZNUMBER_REQUESTDESC
						+ " = "
						+ gn_requestdesc
						+ " "
						// + GZNumber.GZNUMBER_NAME + " = "
						// + gn_name + ", "
						// + GZNumber.GZNUMBER_CANCELDESC + " = "
						// + gn_canceldesc + " "
						+ " WHERE " + GZNumber.GZNUMBER_CLASS + " = " + gn_class + " AND " + GZNumber.GZNUMBER_SEQ
						+ " = " + gn_seq;
			}
			conn.executeUpdate(sql);
			conn.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			conn.rollback();
			throw new Exception("*** Register Terminated!! ***  Register number: " + gn_number + " Failed!"
					+ ex.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void registerOneNumber2(GZNumberInfoContained gnumber, RequestInfoContained requestInfo) throws Exception {
		System.out.println("Begin registerOneNumberRemote=======");

		String gn_number = "'" + gnumber.getNumber() + "' ";
		String gn_class = "'" + gnumber.getClasspath() + "' ";
		String gn_requestor = "'" + requestInfo.getRequestor() + "'";
		String gn_requestdatetime = "''";
		if (requestInfo.getDatetime() != null && !requestInfo.getDatetime().equals("")) {
			gn_requestdatetime = "to_date('" + requestInfo.getDatetime() + "','yyyy-mm-dd hh24:mi:ss') ";
		}

		String gn_flag = gnumber.getFlag() + " ";
		String gn_requestdesc = "'" + requestInfo.getRequestdesc() + "' ";
		String gn_name = "'" + requestInfo.getObjectname() + "' ";
		String gn_canceldesc = "'" + requestInfo.getCanceldesc() + " ' ";
		String gn_seq = String.valueOf(gnumber.getSeq()) + " ";

		DBConnUtil conn = null;

		try {
			conn = new DBConnUtil();

			String sql;
			sql = "SELECT * FROM GLAWAY_GZNUMBER WHERE " + GZNumber.GZNUMBER_NUMBER + " = " + gn_number;

			ResultSet rs = conn.executeQuery(sql);

			if (!rs.next()) {
				sql = "INSERT INTO GLAWAY_GZNUMBER " + "(" + GZNumber.GZNUMBER_NUMBER + "," + GZNumber.GZNUMBER_CLASS
						+ "," + GZNumber.GZNUMBER_REQUESTOR + "," + GZNumber.GZNUMBER_REQUESTDATETIME + ","
						+ GZNumber.GZNUMBER_FLAG + "," + GZNumber.GZNUMBER_REQUESTDESC
						+ ","
						// + GZNumber.GZNUMBER_NAME + ","
						// + GZNumber.GZNUMBER_CANCELDESC + ","
						+ GZNumber.GZNUMBER_SEQ + ") " + "VALUES(" + gn_number + "," + gn_class + "," + gn_requestor
						+ "," + gn_requestdatetime + "," + gn_flag + "," + gn_requestdesc
						// + "," + gn_name + "," + gn_canceldesc
						+ "," + gn_seq + ")";
			} else {// 重新使用已作废的编号
				sql = "UPDATE GLAWAY_GZNUMBER SET "
						// + GZNumber.GZNUMBER_NUMBER + " = "
						// + gn_number + ", "
						+ GZNumber.GZNUMBER_REQUESTOR + " = " + gn_requestor + ", " + GZNumber.GZNUMBER_REQUESTDATETIME
						+ " = " + gn_requestdatetime + ", " + GZNumber.GZNUMBER_FLAG + " = " + gn_flag + ", "
						+ GZNumber.GZNUMBER_REQUESTDESC
						+ " = "
						+ gn_requestdesc
						+ " "
						// + GZNumber.GZNUMBER_NAME + " = "
						// + gn_name + ", "
						// + GZNumber.GZNUMBER_CANCELDESC + " = "
						// + gn_canceldesc + " "
						+ " WHERE " + GZNumber.GZNUMBER_CLASS + " = " + gn_class + " AND " + GZNumber.GZNUMBER_SEQ
						+ " = " + gn_seq;
			}
			conn.executeUpdate(sql);
			conn.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			conn.rollback();
			throw new Exception("*** Register Terminated!! ***  Register number: " + gn_number + " Failed!"
					+ ex.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void requestUsedNumber(String gn_number) throws Exception {
		gn_number = "'" + gn_number + "' ";
		DBConnUtil conn = null;
		// Date now = new Date();
		// DateFormat d1 = DateFormat.getDateTimeInstance() ;
		// d1.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		// String gn_requestdatetime = d1.format(now);
		// gn_requestdatetime = "to_date('" + gn_requestdatetime +
		// "','yyyy-mm-dd hh24:mi:ss') ";
		try {
			conn = new DBConnUtil();

			String sql;
			sql = "UPDATE GLAWAY_GZNUMBER SET " + GZNumber.GZNUMBER_FLAG + " =  " + GZNumberManager.GENERATED_FLAG
			// + "," + GZNumber.GZNUMBER_REQUESTDATETIME + " = "+
					// gn_requestdatetime
					+ " WHERE " + GZNumber.GZNUMBER_NUMBER + " = " + gn_number;

			conn.executeUpdate(sql);
			conn.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			conn.rollback();
			throw new Exception("*** Set Used Flag Terminated!! *** number: " + gn_number + " Failed!");
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 将号码的使用权换人
	 * 
	 * @create date: 2011-9-16下午3:28:57
	 * @methodName: resignNumberToUser
	 * @return: void
	 * @param number
	 * @param usr
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public void resignNumberToUser(String number, String usr) throws RemoteException, InvocationTargetException {
		if (!RemoteMethodServer.ServerFlag) {
			Class<?>[] clz = new Class<?>[] { String.class, String.class };
			Object[] objs = new Object[] { number, usr };
			RemoteMethodServer.getDefault().invoke("resignNumberToUser", GZNumberRegisterManager.class.getName(), this,
					clz, objs);
			return;
		}
		if (!(number == null || "".equals(number))) {
			if (!(usr == null || "".equals(usr))) {
				String[] strArr = usr.split(",");
				for (String strLdap : strArr) {
					if (strLdap.startsWith("uid=")) {
						usr = strLdap.substring("uid=".length());
						break;
					}
				}
				DBConnUtil conn = null;
				try {
					conn = new DBConnUtil();
					String sql = "UPDATE GLAWAY_GZNUMBER SET " + GZNumber.GZNUMBER_REQUESTOR + "='" + usr + "'"
							+ " WHERE " + GZNumber.GZNUMBER_NUMBER + "='" + number + "'";
					conn.executeUpdate(sql);
					conn.commit();
					conn.close();
					conn = null;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (conn != null) {
						try {
							conn.rollback();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public void registerNumbers(PropertiesBean pb, FormatedNumber fnumber, int volumn, String name) throws Exception {
		System.out.println("Begin registerNumbers=======" + volumn);

		Sequence seq;
		GZNumber gzNumber;
		DBConnUtil conn = null;
		String gn_number = "";
		try {
			conn = new DBConnUtil();
			String sql;
			String names[] = name.split("\\" + Constants.gzNameSplitStr);
			for (int i = 0; i < volumn; i++) {
				seq = new Sequence(pb, fnumber, volumn);
				gzNumber = new GZNumber();
				String formatedNumber = fnumber.getValue();
				String strNumber = formatedNumber.replaceAll("<Seq>", seq.getValue());
				String strClassPath = fnumber.getClassification().getObjectclasspath();
				String strRequestor = fnumber.getRequestor();
				String strDatetime = fnumber.getDateTime();
				int flag = GZNumberManager.GENERATED_FLAG;
				String strRequestdesc = fnumber.getRequestDesc();
				// String strObjectname =
				// fnumber.getClassification().getObjectname();
				String strObjectname = names[i];
				int iseq = seq.getIValue();

				gn_number = "'" + strNumber + "' ";
				String gn_class = "'" + strClassPath + "' ";
				String gn_requestor = "'" + fnumber.getRequestor() + "'";
				String gn_requestdatetime = "";
				if (strDatetime != null && !strDatetime.equals("")) {
					gn_requestdatetime = "to_date('" + strDatetime + "','yyyy-mm-dd hh24:mi:ss') ";
				} else {
					Date now = new Date();
					DateFormat d1 = DateFormat.getDateTimeInstance();
					d1.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
					String str = d1.format(now);
					gn_requestdatetime = "to_date('" + str + "','yyyy-mm-dd hh24:mi:ss') ";
				}

				String gn_flag = flag + " ";
				String gn_requestdesc = "'" + strRequestdesc + "' ";
				String gn_name = "'" + strObjectname + "'";
				String gn_canceldesc = "' '";
				String gn_seq = String.valueOf(iseq) + " ";

				sql = "SELECT * FROM GLAWAY_GZNUMBER WHERE " + GZNumber.GZNUMBER_CLASS + " = " + gn_class + " AND "
						+ GZNumber.GZNUMBER_SEQ + " = " + gn_seq;

				ResultSet rs = conn.executeQuery(sql);

				if (!rs.next()) {
					sql = "INSERT INTO GLAWAY_GZNUMBER " + "(" + GZNumber.GZNUMBER_NUMBER + ","
							+ GZNumber.GZNUMBER_CLASS + "," + GZNumber.GZNUMBER_REQUESTOR + ","
							+ GZNumber.GZNUMBER_REQUESTDATETIME + "," + GZNumber.GZNUMBER_FLAG + ","
							+ GZNumber.GZNUMBER_REQUESTDESC + "," + GZNumber.GZNUMBER_NAME
							+ ","
							// + GZNumber.GZNUMBER_CANCELDESC + ","
							+ GZNumber.GZNUMBER_SEQ + ") " + "VALUES(" + gn_number + "," + gn_class + ","
							+ gn_requestor + "," + gn_requestdatetime + "," + gn_flag + "," + gn_requestdesc + ","
							+ gn_name + ","
							// + gn_canceldesc+ ","
							+ gn_seq + ")";
				} else {// 重新使用已作废的编号
					sql = "UPDATE GLAWAY_GZNUMBER SET "
							// + GZNumber.GZNUMBER_NUMBER + " = "
							// + gn_number + ", "
							+ GZNumber.GZNUMBER_REQUESTOR + " = " + gn_requestor + ", "
							+ GZNumber.GZNUMBER_REQUESTDATETIME + " = " + gn_requestdatetime + ", "
							+ GZNumber.GZNUMBER_FLAG + " = " + gn_flag + ", " + GZNumber.GZNUMBER_REQUESTDESC + " = "
							+ gn_requestdesc + ", " + GZNumber.GZNUMBER_NAME + " = " + gn_name
							+ ""
							// + GZNumber.GZNUMBER_CANCELDESC + " = "
							// + gn_canceldesc + " "
							+ " WHERE " + GZNumber.GZNUMBER_CLASS + " = " + gn_class + " AND " + GZNumber.GZNUMBER_SEQ
							+ " = " + gn_seq;
				}
				System.out.println(sql);
				conn.executeUpdate(sql);
			}
			conn.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			conn.rollback();
			throw ex;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
