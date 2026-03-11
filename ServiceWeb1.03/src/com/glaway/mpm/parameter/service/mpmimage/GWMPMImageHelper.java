package com.glaway.mpm.parameter.service.mpmimage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.sql.BLOB;
import wt.fc.WTObject;
import wt.method.MethodContext;
import wt.pom.WTConnection;
import wt.util.WTException;

import com.glaway.mpm.parameter.model.GWImageFilesZip;

/**
 * 处理工艺文件特殊符号产生的图片数据的帮组类
 *
 * @author 龙秀川
 *
 */
public class GWMPMImageHelper {

	public static String COLUMN_GLKEYID = "GLKEYID";
	public static String COLUMN_OBJCLASSNAME = "OBJCLASSNAME";
	public static String COLUMN_OBJID = "OBJID";
	public static String COLUMN_IMAGE = "IMAGE";
	public static String COLUMN_FILENAME = "FILENAME";

	public static int index = 1;

	/**
	 * 生成主键编号
	 *
	 * @return
	 */
	public static String generateKeyId() {
		if (index >= 10000) {
			index = 1;
		}
		return String.valueOf(System.currentTimeMillis()) + index++;
	}

	/**
	 * 保存图片数据
	 *
	 * @param obj
	 * @param files
	 * @throws SQLException
	 * @throws WTException
	 */
	public static void saveImage(WTObject obj, GWImageFilesZip file)
			throws SQLException, WTException {
		if (file != null) {
			PreparedStatement stmt = null;
			String className = obj.getClass().getName();
			Long id = obj.getPersistInfo().getObjectIdentifier().getId();
			try {
				WTConnection wtconnection = (WTConnection) MethodContext
						.getContext().getConnection();
				String sql = "insert into GWMPMIMAGE(" + COLUMN_GLKEYID + ","
						+ COLUMN_OBJCLASSNAME + "," + COLUMN_OBJID + ","
						+ COLUMN_IMAGE + "," + COLUMN_FILENAME
						+ ") values(?,?,?,?,?,?,?)";
				stmt = wtconnection.prepareStatement(sql);
				String keyId = generateKeyId();
				stmt.setObject(1, keyId);
				stmt.setObject(2, className);
				stmt.setObject(3, id);
				BLOB blob = BLOB.getEmptyBLOB();
				stmt.setBlob(4, blob);
				stmt.setObject(5, file.getFileName());
				stmt.execute();
				stmt.close();
				stmt = null;
				updateDescriptionImage(wtconnection, obj, file);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			}
		}
	}

	public static GWImageFilesZip getImage(WTObject obj) throws Exception {
		GWImageFilesZip file = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			WTConnection wtconnection = (WTConnection) MethodContext
					.getContext().getConnection();
			String findSQL = "select a.* from GWMPMIMAGE" + " a where a."
					+ COLUMN_OBJCLASSNAME + "=?" + " and a." + COLUMN_OBJID
					+ "=?";
			stmt = wtconnection.prepareStatement(findSQL);
			stmt.setObject(1, obj.getClass().getName());
			stmt.setObject(2, obj.getPersistInfo().getObjectIdentifier()
					.getId());
			rs = stmt.executeQuery();
			if (rs.next()) {
				Blob blob = rs.getBlob(COLUMN_IMAGE);
				String fileName = rs.getString(COLUMN_FILENAME);
				InputStream is = blob.getBinaryStream();
				byte[] buffer = new byte[1024 * 4];
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				int n = 0;
				while ((n = is.read(buffer, 0, buffer.length)) >= 0) {
					out.write(buffer, 0, n);
				}
				out.close();
				byte[] allData = out.toByteArray();
				file = new GWImageFilesZip();
				file.setData(allData);
				file.setFileName(fileName);
				is.close();
				out = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}

		}
		return file;
	}

	public static void deleteImages(WTObject obj) throws WTException,
			SQLException {
		PreparedStatement stmt = null;
		String className = obj.getClass().getName();
		Long id = obj.getPersistInfo().getObjectIdentifier().getId();
		try {
			WTConnection wtconnection = (WTConnection) MethodContext
					.getContext().getConnection();
			String sql = "delete from GWMPMIMAGE" + " a where a."
					+ COLUMN_OBJCLASSNAME + "=?" + " and a." + COLUMN_OBJID
					+ "=?";
			stmt = wtconnection.prepareStatement(sql);
			stmt.setObject(1, className);
			stmt.setObject(2, id);
			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
		}
	}

	/**
	 * 更新图片数据
	 *
	 * @param wtconnection
	 * @param obj
	 * @param file
	 * @return
	 * @throws Exception
	 */
	private static boolean updateDescriptionImage(WTConnection wtconnection,
			WTObject obj, GWImageFilesZip file) throws Exception {
		boolean flag = false;
		if (file != null) {
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try {
				String findSQL = "select a." + COLUMN_IMAGE + ",a."
						+ COLUMN_GLKEYID + " from GWMPMIMAGE" + " a where a."
						+ COLUMN_OBJCLASSNAME + "=?" + " and a." + COLUMN_OBJID
						+ "=?" + " and a." + COLUMN_FILENAME + "=? for update";
				stmt = wtconnection.prepareStatement(findSQL);
				stmt.setObject(1, obj.getClass().getName());
				stmt.setObject(2, obj.getPersistInfo().getObjectIdentifier()
						.getId());
				stmt.setObject(3, file.getFileName());
				rs = stmt.executeQuery();
				Blob blob = null;
				if (rs.next()) {
					blob = rs.getBlob(1);
				}
				if (blob != null) {
					OutputStream out = blob.setBinaryStream(0);
					byte buffer[] = new byte[1024 * 4];
					ByteArrayInputStream is = new ByteArrayInputStream(
							file.getData());
					int n = 0;
					while ((n = is.read(buffer, 0, buffer.length)) >= 0) {
						out.write(buffer, 0, n);
					}
					out.close();
					out = null;
					is.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			}
		}
		return flag;
	}
}
