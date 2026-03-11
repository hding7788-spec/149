package com.ptc.extend.ixb;

import ext.sast.catalog.GLCILink;
import ext.sast.catalog.GLCIPartLink;
import ext.sast.catalog.GLPartLink;
import ext.sast.navigation.GLClassificationNode;
import wt.method.MethodContext;
import wt.pom.WTConnection;
import wt.session.SessionServerHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CmExpImpCrudHelper {

	public static boolean addClassificationNode(GLClassificationNode node){
		boolean rst = false;
		PreparedStatement pstmt = null;
		WTConnection wtconnection = null;
		Connection con = null;
		StringBuffer sqlSB = new StringBuffer();
		sqlSB.append("insert into GLClassificationNode(IDA2A2, TOPID,NODENAME,synchTime)");
		sqlSB.append("values(?, ?,?,?) ");
		try {
			SessionServerHelper.manager.setAccessEnforced(false);
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
		    con = wtconnection.getConnection();
		    con.setAutoCommit(false);
			pstmt = con.prepareStatement(sqlSB.toString());
			pstmt.setLong(1, node.getId());
			pstmt.setLong(2, node.getParentId());
			pstmt.setString(3, node.getNumber());
			pstmt.setLong(4, System.currentTimeMillis());

			// 执行批量插入操作
			int num =  pstmt.executeUpdate();
			if(num>=1){
				rst = true;
			}
			con.commit();
			con =null;
			//wtconnection.releaseAll();
			//wtconnection = null;
		} catch (Exception ex) {
			rst = false;
			try {
				con.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ex.printStackTrace();
			SessionServerHelper.manager.setAccessEnforced(true);
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			SessionServerHelper.manager.setAccessEnforced(true);
		}
		return rst;
	}

	public static boolean addGLCILink(GLCILink link) {
		boolean rst = false;
		PreparedStatement pstmt = null;
		WTConnection wtconnection = null;
		Connection con = null;
		StringBuffer sqlSB = new StringBuffer();
		sqlSB.append("insert into GLCILINK(GLCATALOGNAME, CIPARTNUMBER,GLSUPPLIERS,GLCATALOGNUMBER)");
		sqlSB.append("values(?, ?,?,?) ");
		try {
			SessionServerHelper.manager.setAccessEnforced(false);
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
		    con = wtconnection.getConnection();
		    con.setAutoCommit(false);
			pstmt = con.prepareStatement(sqlSB.toString());
			if(link.getCatalogName()==null){
					pstmt.setString(1, link.getCatalogNumber());
			}else{
					pstmt.setString(1, link.getCatalogName());
			}
			
			pstmt.setString(2, link.getPartNumber());
			pstmt.setString(3, link.getSuppliers());
			pstmt.setString(4, link.getCatalogNumber());
			// 执行批量插入操作
			int num =  pstmt.executeUpdate();
			if(num>=1){
				rst = true;
			}
			con.commit();
			con =null;
			//wtconnection.releaseAll();
			//wtconnection = null;
		} catch (Exception ex) {
			rst = false;
			try {
				con.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ex.printStackTrace();
			SessionServerHelper.manager.setAccessEnforced(true);
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			SessionServerHelper.manager.setAccessEnforced(true);
		}
		return rst;

	}
	public static boolean addGLCIPartLink(GLCIPartLink link) {
		boolean rst = false;
		PreparedStatement pstmt = null;
		WTConnection wtconnection = null;
		Connection con = null;
		StringBuffer sqlSB = new StringBuffer();
		sqlSB.append("insert into GLCIPartLink(CIPARTNUMBER,WTPARTNUMBER)");
		sqlSB.append("values(?, ?) ");
		try {
			SessionServerHelper.manager.setAccessEnforced(false);
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
		    con = wtconnection.getConnection();
		    con.setAutoCommit(false);
			pstmt = con.prepareStatement(sqlSB.toString());
			pstmt.setString(1, link.getCatalogItemNumber());
			pstmt.setString(2, link.getPartNumber());
			// 执行批量插入操作
			int num =  pstmt.executeUpdate();
			if(num>=1){
				rst = true;
			}
			con.commit();
			con =null;
			//wtconnection.releaseAll();
			//wtconnection = null;
		} catch (Exception ex) {
			rst = false;
			try {
				con.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ex.printStackTrace();
			SessionServerHelper.manager.setAccessEnforced(true);
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			SessionServerHelper.manager.setAccessEnforced(true);
		}
		return rst;

	}

	public static boolean addGLPartLink(GLPartLink link) {
		boolean rst = false;
		PreparedStatement pstmt = null;
		WTConnection wtconnection = null;
		Connection con = null;
		StringBuffer sqlSB = new StringBuffer();
		sqlSB.append("insert into GLPartLink(CATALOGNUMBER, WTPARTNUMBER)");
		sqlSB.append("values(?, ?) ");
		try {
			SessionServerHelper.manager.setAccessEnforced(false);
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
		    con = wtconnection.getConnection();
		    con.setAutoCommit(false);
			pstmt = con.prepareStatement(sqlSB.toString());
			pstmt.setString(1, link.getCatalogNumber());
			pstmt.setString(2, link.getPartNumber());
			// 执行批量插入操作
			int num =  pstmt.executeUpdate();
			if(num>=1){
				rst = true;
			}
			con.commit();
			con =null;
			//wtconnection.releaseAll();
			//wtconnection = null;
		} catch (Exception ex) {
			rst = false;
			try {
				con.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ex.printStackTrace();
			SessionServerHelper.manager.setAccessEnforced(true);
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			SessionServerHelper.manager.setAccessEnforced(true);
		}
		return rst;

	}
}
