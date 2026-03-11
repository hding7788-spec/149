package com.glaway.mpm.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import wt.method.MethodContext;
import wt.pom.WTConnection;

public class DBConnUtil {
	Connection connection;
	Statement statement;
	PreparedStatement preparedStatement;
	ResultSet resultSet;
	String errorMessage = null;
	int updateCount;

	static MethodContext methodcontext;

	public DBConnUtil() throws Exception{

		try {
			methodcontext = MethodContext.getContext();
			WTConnection wtconnection = (WTConnection)methodcontext.getConnection();
			connection = wtconnection.getConnection();
			statement = connection.createStatement();
		} catch (Exception ex) {
			errorMessage = "Cannot connect to this database.<br>" + ex.getMessage();
			throw ex;
		}
	}

	public ResultSet executeQuery(String query) throws SQLException{
		if(connection == null || statement == null){
			errorMessage = "There is no database to execute the query.";
			return null;
		}

		try{
			resultSet = statement.executeQuery(query);
			return resultSet;
		}catch(SQLException ex){
			errorMessage = ex.getMessage();
			throw ex;
		}
	}

	public void executeUpdate(String sqlcommand) throws SQLException{
		if(connection == null || statement == null){
			errorMessage = "There is no database to execute the query.";
			return;
		}
		try{
			updateCount = statement.executeUpdate(sqlcommand);
		}catch(SQLException ex){
			errorMessage = ex.getMessage();
			throw ex;
		}
	}

	public void start() throws SQLException{
		connection.setAutoCommit(false);
	}

	public void commit() throws SQLException{
		connection.commit();
	}

	public void rollback() throws SQLException{
		connection.rollback();
	}

	public void close() throws SQLException{
		if(resultSet != null){
			resultSet.close();
		}
		if(statement != null){
			statement.close();
		}
	}
}
