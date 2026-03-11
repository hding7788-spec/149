package com.glaway.mpm.mpmresource.gznumber.classification;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.glaway.mpm.util.DBConnUtil;
import com.glaway.mpm.util.Util;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;

public class GZNumberClassificationHelper implements RemoteAccess, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -7237698703712806510L;

	/**
	 * Get a Classification Object from Database by a classificationID
	 *
	 * @param classificationID
	 * @return a Classification Object
	 * @throws Exception
	 */
	public static GZNumberClassification getClassification(String classificationPath) {
		GZNumberClassification resultClass = null;
		DBConnUtil conn = null;

		try {
			conn = new DBConnUtil();

			String sql = "SELECT * FROM glaway_gznumberclassification WHERE "
					+ GZNumberClassification.CLASSIFICATION_PATH + " = '" + classificationPath + "' ORDER BY "
					+ GZNumberClassification.CLASSIFICATION_PATH;
			ResultSet rs = conn.executeQuery(sql);

			if (rs.next()) {
				resultClass = new GZNumberClassification();
				String objectClassPath = rs.getString(GZNumberClassification.CLASSIFICATION_PATH);
				String objectName = rs.getString(GZNumberClassification.CLASSIFICATION_NAME);
				String objectClassValue = Util.formateString(rs.getString(GZNumberClassification.CLASSIFICATION_VALUE));
				String objectClassDesc = Util.formateString(rs.getString(GZNumberClassification.CLASSIFICATION_DESC));
				String objectParentClassPath = Util.formateString(rs
						.getString(GZNumberClassification.CLASSIFICATION_PARENTPATH));

				resultClass.setObjectclasspath(objectClassPath);
				resultClass.setObjectname(objectName);
				resultClass.setObjectclassvalue(objectClassValue);
				resultClass.setObjectclassdesc(objectClassDesc);
				resultClass.setObjectparentclasspath(objectParentClassPath);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return resultClass;
	}

	/**
	 * Get a Classification Object by a RequestInfoBean
	 *
	 * @param requestBean
	 *            Bean from Presentation Layer
	 * @return a Classification Object
	 */
	public static GZNumberClassification getClassification(GZNumberClassificationInfoContained requestBean) {
		GZNumberClassification resultClass = new GZNumberClassification();

		resultClass.setObjectclasspath(requestBean.getObjectclasspath());
		resultClass.setObjectname(requestBean.getObjectname());
		resultClass.setObjectclassvalue(requestBean.getObjectclassvalue());
		resultClass.setObjectclassdesc(requestBean.getObjectclassdesc());

		return resultClass;
	}

	/**
	 * public static ArrayList getChildClassifications(String parentPath){ try {
	 * if (!RemoteMethodServer.ServerFlag) { return (ArrayList)
	 * RemoteMethodServer.getDefault().invoke("getChildClassificationsRemote",
	 * ClassificationHelper.class.getName(), null, new Class[] {String.class},
	 * new Object[] {parentPath}); } else { return
	 * getChildClassificationsRemote(parentPath); } } catch (Exception e) {
	 * e.printStackTrace(); } return new ArrayList(); }
	 **/

	/**
	 * Get child Classificaiton nodes by parent node path
	 *
	 * @param parentPath
	 *            String value of parent path
	 * @return ArrayList within child Classification objects
	 * @throws Exception
	 */
	public static ArrayList getChildClassifications(String parentPath) {
		ArrayList resultClassifications = new ArrayList();

		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();

			String sql = "SELECT * FROM glaway_gznumberclassification WHERE "
					+ GZNumberClassification.CLASSIFICATION_PARENTPATH + " = '" + parentPath + "' ORDER BY "
					+ GZNumberClassification.CLASSIFICATION_PATH;
			ResultSet rs = conn.executeQuery(sql);

			GZNumberClassification resultClass;
			while (rs.next()) {
				resultClass = new GZNumberClassification();
				String objectClassPath = rs.getString(GZNumberClassification.CLASSIFICATION_PATH);
				String objectName = rs.getString(GZNumberClassification.CLASSIFICATION_NAME);
				String objectClassValue = Util.formateString(rs.getString(GZNumberClassification.CLASSIFICATION_VALUE));
				String objectClassDesc = Util.formateString(rs.getString(GZNumberClassification.CLASSIFICATION_DESC));

				resultClass.setObjectclasspath(objectClassPath);
				resultClass.setObjectname(objectName);
				resultClass.setObjectclassvalue(objectClassValue);
				resultClass.setObjectclassdesc(objectClassDesc);

				resultClassifications.add(resultClass);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		}
		return resultClassifications;
	}

	/**
	 * Get child Classificaiton nodes by parent node
	 *
	 * @param parentClassification
	 *            Classification object of parent node
	 * @return ArrayList within child Classification objects
	 */
	public static ArrayList getChildClassifications(GZNumberClassificationInfoContained parentClassification) {
		String strClassificationParentPath = parentClassification.getObjectclasspath();
		return getChildClassifications(strClassificationParentPath);
	}

	public static ArrayList getUnregistedChildClassPath(String classificationPath) {
		ArrayList result = new ArrayList();
		ArrayList intitalArray = new ArrayList();

		int count = 9;

		for (int i = 0; i <= count; i++) {
			intitalArray.add(i + "");
		}
		String classpath = "";
		ArrayList aChildClasses = getChildClassifications(classificationPath);

		// if(aChildClasses.size() == 0) return result;

		for (int i = 0; i < aChildClasses.size(); i++) {
			GZNumberClassification childClass = (GZNumberClassification) aChildClasses.get(i);
			classpath = childClass.getObjectclasspath();
			String lastNumber = classpath.substring(classpath.length() - 1, classpath.length());
			for (int k = 0; k <= count; k++) {
				if (lastNumber.equals(k + "")) {
					intitalArray.remove(k + "");
					break;
				}
			}
		}

		// if classificationPath start with letters, the letters will not be
		// brought into child class path
		if (startWithLetters(classificationPath)) {
			classificationPath = "";
		}
		// else if(classificationPath.indexOf(".")<0){
		// classificationPath += ".";
		// }

		for (int i = 0; i < intitalArray.size(); i++) {
			result.add(classificationPath + intitalArray.get(i));
		}

		return result;
	}

	private static boolean startWithLetters(String classificationPath) {

		String regEx = "^[a-zA-Z]+.*$";
		Pattern pattern = Pattern.compile(regEx);
		Matcher match = pattern.matcher(classificationPath);
		return match.find();
	}

	public static ArrayList getUnregistedChildClassPath1(String classificationPath) {
		ArrayList result = new ArrayList();
		ArrayList intitalArray = new ArrayList();

		int count = 9;
		// 如果为企业标准文档，容许维护15个
		if (classificationPath.equalsIgnoreCase("QG")) {
			count = 15;
		} else if (classificationPath.equalsIgnoreCase("QS")) {
			count = 15;
		}

		for (int i = 0; i <= count; i++) {
			intitalArray.add(i + "");
		}
		String classpath = "";
		ArrayList aChildClasses = getChildClassifications(classificationPath);

		// if(aChildClasses.size() == 0) return result;
		int len = classificationPath.length();
		for (int i = 0; i < aChildClasses.size(); i++) {
			GZNumberClassification childClass = (GZNumberClassification) aChildClasses.get(i);
			classpath = childClass.getObjectclasspath();
			String lastNumber = classpath.substring(len);
			for (int k = 0; k <= count; k++) {
				if (lastNumber.equals(k + "")) {
					intitalArray.remove(k + "");
					break;
				}
			}
		}

		for (int i = 0; i < intitalArray.size(); i++) {
			result.add(classificationPath + intitalArray.get(i));
		}

		return result;
	}

	public static void regClassification(GZNumberClassificationInfoContained classInfo) throws Exception {
		String class_path = "'" + classInfo.getObjectclasspath() + "' ";
		String class_parent = "'" + classInfo.getObjectparentclasspath() + "' ";
		String class_name = "'" + classInfo.getObjectname() + "' ";
		String class_value = "'" + classInfo.getObjectclassvalue() + "' ";
		String class_desc = "'" + classInfo.getObjectclassdesc() + "' ";

		DBConnUtil conn = null;

		try {
			conn = new DBConnUtil();

			String sql;
			sql = "SELECT * FROM glaway_gznumberclassification WHERE " + GZNumberClassification.CLASSIFICATION_PATH
					+ " = " + class_path + " AND " + GZNumberClassification.CLASSIFICATION_PARENTPATH + " = "
					+ class_parent;
			ResultSet rs = conn.executeQuery(sql);

			if (!rs.next()) {
				sql = "INSERT INTO glaway_gznumberclassification " + "(" + GZNumberClassification.CLASSIFICATION_PATH
						+ "," + GZNumberClassification.CLASSIFICATION_PARENTPATH + ","
						+ GZNumberClassification.CLASSIFICATION_NAME + ","
						+ GZNumberClassification.CLASSIFICATION_VALUE + ","
						+ GZNumberClassification.CLASSIFICATION_DESC + ") " + "VALUES(" + class_path + ","
						+ class_parent + "," + class_name + "," + class_value + "," + class_desc + ")";
			} else {
				sql = "UPDATE glaway_gznumberclassification SET " + GZNumberClassification.CLASSIFICATION_PARENTPATH
						+ " = " + class_parent + ", " + GZNumberClassification.CLASSIFICATION_NAME + " = " + class_name
						+ ", " + GZNumberClassification.CLASSIFICATION_VALUE + " = " + class_value + ", "
						+ GZNumberClassification.CLASSIFICATION_DESC + " = " + class_desc + " WHERE "
						+ GZNumberClassification.CLASSIFICATION_PATH + " = " + class_path;
			}

			conn.executeUpdate(sql);
			conn.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			conn.rollback();
			throw new Exception("*** New Classification Terminated!! ***  Class path: " + class_path + " Failed!");
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				throw e;
			}
		}
	}

	public static void updateClassification(String classPath, String name, String value) throws Exception {
		String class_path = "'" + classPath + "' ";
		String class_name = "'" + name + "' ";
		String class_value = "'" + value + "' ";

		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();

			String sql;
			sql = "UPDATE glaway_gznumberclassification SET " + GZNumberClassification.CLASSIFICATION_NAME + " = "
					+ class_name + ", " + GZNumberClassification.CLASSIFICATION_VALUE + " = " + class_value + " WHERE "
					+ GZNumberClassification.CLASSIFICATION_PATH + " = " + class_path;

			conn.executeUpdate(sql);
			conn.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			conn.rollback();
			throw new Exception("*** Update Classification Terminated!! ***  Class path: " + classPath + " Failed!");
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void deleteClassification(String classPath) throws Exception {
		try {
			if (!RemoteMethodServer.ServerFlag) {
				RemoteMethodServer.getDefault().invoke("deleteClassificationRemote",
						GZNumberClassificationHelper.class.getName(), null, new Class[] { String.class },
						new Object[] { classPath });
			} else {
				deleteClassificationRemote(classPath);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public static void deleteClassificationRemote(String classPath) throws Exception {
		String class_path = "'" + classPath + "' ";
		DBConnUtil conn = null;

		try {
			conn = new DBConnUtil();

			String sql;
			sql = "Delete FROM glaway_gznumberclassification " + " WHERE " + GZNumberClassification.CLASSIFICATION_PATH
					+ " = " + class_path;

			conn.executeUpdate(sql);
			conn.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			conn.rollback();
			throw new Exception("*** Delete Classification Terminated!! ***  Class path: " + classPath + " Failed!");
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
