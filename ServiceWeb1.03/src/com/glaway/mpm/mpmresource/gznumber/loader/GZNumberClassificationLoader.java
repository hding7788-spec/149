package com.glaway.mpm.mpmresource.gznumber.loader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import wt.log4j.LogR;

import com.glaway.mpm.mpmresource.gznumber.classification.GZNumberClassification;
import com.glaway.mpm.mpmresource.gznumber.classification.GZNumberClassificationInfoContained;
import com.glaway.mpm.util.DBConnUtil;

public class GZNumberClassificationLoader {
	private DataSource data;
	private GZNumberClassificationTranslated translator;
	private LoadReporter reporter;
	private static Logger logger = LogR.getLogger(GZNumberClassificationLoader.class.getName());

	public GZNumberClassificationLoader(DataSource data, GZNumberClassificationTranslated translator) {
		this.data = data;
		this.translator = translator;
		reporter = new LoadReporter();
	}

	public void load() {
		loadClassification();
	}

	private void loadClassification() {
		String class_path = "";
		String class_parent = "";
		String class_name = "";
		String class_value = "";
		String class_desc = "";

		DBConnUtil conn = null;
		int index = 0;
		try {
			conn = new DBConnUtil();

			for (int i = translator.getRowBegin(); i <= data.end(); i++) {
				index = i;
				HashMap record = data.getRecordData(i);
				translator.translate(record);
				GZNumberClassificationInfoContained classInfo = translator.getClassInfo();

				class_path = "'" + classInfo.getObjectclasspath() + "' ";
				class_parent = "'" + classInfo.getObjectparentclasspath() + "' ";
				class_name = "'" + classInfo.getObjectname() + "' ";
				class_value = "'" + classInfo.getObjectclassvalue() + "' ";
				class_desc = "'" + classInfo.getObjectclassdesc() + "' ";

				if (!class_path.equals("''") && !class_parent.equals("''")) {
					String sql;
					sql = "SELECT * FROM glaway_gznumberclassification WHERE "
							+ GZNumberClassification.CLASSIFICATION_PATH + " = " + class_path + " AND "
							+ GZNumberClassification.CLASSIFICATION_PARENTPATH + " = " + class_parent;

					ResultSet rs = conn.executeQuery(sql);
					// logger.debug("~~ sql~1:"+sql);

					if (!rs.next()) {
						sql = "INSERT INTO glaway_gznumberclassification " + "("
								+ GZNumberClassification.CLASSIFICATION_PATH + ","
								+ GZNumberClassification.CLASSIFICATION_PARENTPATH + ","
								+ GZNumberClassification.CLASSIFICATION_NAME + ","
								+ GZNumberClassification.CLASSIFICATION_VALUE + ","
								+ GZNumberClassification.CLASSIFICATION_DESC + ") " + "VALUES(" + class_path + ","
								+ class_parent + "," + class_name + "," + class_value + "," + class_desc + ")";
					} else {
						sql = "UPDATE glaway_gznumberclassification SET "
								+ GZNumberClassification.CLASSIFICATION_PARENTPATH + " = " + class_parent + ", "
								+ GZNumberClassification.CLASSIFICATION_NAME + " = " + class_name + ", "
								+ GZNumberClassification.CLASSIFICATION_VALUE + " = " + class_value + ", "
								+ GZNumberClassification.CLASSIFICATION_DESC + " = " + class_desc + " WHERE "
								+ GZNumberClassification.CLASSIFICATION_PATH + " = " + class_path + " AND "
								+ GZNumberClassification.CLASSIFICATION_PARENTPATH + " = " + class_parent;
					}
					// logger.debug("~~ sql~2:"+sql);
					conn.executeUpdate(sql);
					logger.debug("=== Load line(" + i + ") of Class Path [" + class_path + "], Parent [" + class_parent
							+ "], Name [" + class_name + "], Value [" + class_value + "], Desc [" + class_desc
							+ "] successful! ===");
					reporter.addLogs("=== Load line(" + i + ") of Class Path [" + class_path + "], Parent ["
							+ class_parent + "], Name [" + class_name + "], Value [" + class_value + "],Desc ["
							+ class_desc + "] successful! ===");
				}
			}

			conn.commit();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			String errorMsg = e.getMessage();
			// 去除换行符，制表符
			Pattern p = Pattern.compile("\t|\r|\n");
			Matcher m = p.matcher(errorMsg);
			errorMsg = m.replaceAll("");
			reporter.addDisplayErrors("=== Record: Class Path [" + class_path + "] at line(" + index
					+ ") Loading Error Occures! ===");
			reporter.addBackErrors(errorMsg);
			reporter.addLogs("=== Load line(" + index + ") of Class Path [" + class_path + "], Parent [" + class_parent
					+ "], Name [" + class_name + "], Value [" + class_value + "] Failed! ===");
			e.printStackTrace();
			logger.debug("=== Load line(" + index + ") of Class Path [" + class_path + "], Parent [" + class_parent
					+ "], Name [" + class_name + "], Value [" + class_value + "] Failed! ===");
			logger.debug(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public LoadReporter getReporter() {
		return reporter;
	}
}
