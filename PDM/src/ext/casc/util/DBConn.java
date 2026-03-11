package ext.casc.util;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Properties;
import java.util.Vector;

import wt.method.MethodContext;
import wt.method.RemoteMethodServer;
import wt.pom.WTConnection;
import wt.util.WTProperties;

public class DBConn{
	Connection connection;
    Statement statement;
    PreparedStatement preparedStatement;
    ResultSet resultSet;
    String[] columnNames = {};
    Class[] columnClasses = {};
    Vector rows = new Vector();
    ResultSetMetaData   metaData;
    String errorMessage = null;
    int updateCount;
    
    static MethodContext methodcontext;
	
    public DBConn() throws Exception{	 
		try {			
/*			WTProperties wtproperties = WTProperties.getLocalProperties();
			String propfile = wtproperties.getProperty("wt.home")
					+ "/db/db.properties";

			Properties props = new Properties();
			FileInputStream fis = new FileInputStream(propfile);
			props.load(fis);
			fis.close();

			String driver = props.getProperty("wt.pom.jdbc.driver",
					"oracle.jdbc.driver.OracleDriver");
//			String url = props.getProperty("wt.pom.jdbc.url",
//					"jdbc:oracle:thin:@") + props.getProperty("wt.pom.serviceName");
			
			String url = props.getProperty("wt.pom.jdbc.url",
			"jdbc:oracle:thin:@") + props.getProperty("wt.pom.jdbc.host")+":"+props.getProperty("wt.pom.jdbc.port")+":"+props.getProperty("wt.pom.jdbc.service");
//			System.out.println("~~~~url:"+url); 
			
			//decrypt password
			String property = "wt.pom.dbPassword";
			String encrypted_value = props.getProperty(property);			
			String PATH = wtproperties.getProperty("wt.home")+ "/db/db.properties";
			String decrypted = com.ptc.windchill.keystore.WTKeyStoreUtil.
			decryptProperty(property,encrypted_value, PATH);
//			System.out.println("~~~~decrypted:"+decrypted); 
			String userpass = decrypted;
			String username = props.getProperty("wt.pom.dbUser");
			
			Class.forName(driver);
			connection = DriverManager.getConnection(url, username, userpass);*/
			// if (methodcontext == null)
				methodcontext = MethodContext.getContext();
			WTConnection wtconnection = (WTConnection) methodcontext.getConnection();
			connection = wtconnection.getConnection();
			statement = connection.createStatement();
		} catch (ClassNotFoundException ex) {
			errorMessage = "Cannot find the database driver classes.<br>" + ex.getMessage();
			//System.err.println(errorMessage);
			throw ex;
		} catch (SQLException ex) {
			errorMessage = "Cannot connect to this database.<br>" + ex.getMessage();
			//System.err.println(errorMessage);
			throw ex;
		}
    }
    
    public DBConn(String url, String driverName, String user, String passwd) throws Exception {
		try {			
			Class.forName(driverName);
			connection = DriverManager.getConnection(url, user, passwd);
			statement = connection.createStatement();
		} catch (ClassNotFoundException ex) {
			errorMessage = "Cannot find the database driver classes.<br>" + ex.getMessage();
			//System.err.println(errorMessage);
			throw ex;
		} catch (SQLException ex) {
			errorMessage = "Cannot connect to this database.<br>" + ex.getMessage();
			//System.err.println(errorMessage);
			throw ex;
		}
	}
    
    public DBConn(boolean a_autoInitial) throws Exception {
		if (a_autoInitial == false)
			return;
		
		if (!RemoteMethodServer.getDefault().ServerFlag) {
			//only used by method server, if used by client, throw exception
			throw new Exception("This construct can be used by windchill method server only!");
		}
		else{
			if (methodcontext == null)
				methodcontext = MethodContext.getContext();
			WTConnection wtconnection = (WTConnection) methodcontext.getConnection();
			connection = wtconnection.getConnection();
			statement = connection.createStatement();
		}         
     }

    public ResultSet executeQuery(String query) throws Exception {
        if (connection == null || statement == null) {
            errorMessage = "There is no database to execute the query.";
            //System.err.println( errorMessage );
            //System.err.println("There is no database to execute the query.");
            return null;
        }
        try {
            resultSet = statement.executeQuery(query);
           return resultSet;
        }
        catch (SQLException ex) {
            errorMessage = ex.getMessage();
            //System.err.println(errorMessage);
            throw ex;
        }
    }
    

    public void executeUpdate(String sqlcommand) throws Exception {
        if (connection == null || statement == null) {
            errorMessage = "There is no database to execute the query.";
            System.err.println( errorMessage );
            //System.err.println("There is no database to execute the query.");
            return;
        }
        try {
            updateCount = statement.executeUpdate(sqlcommand);
        }
        catch (SQLException ex) {
            errorMessage = ex.getMessage();
            System.err.println(errorMessage);
            throw ex;
        }
    }
    
    public void start() throws SQLException{
    	connection.setAutoCommit( false );
    }

    public void commit() throws SQLException {
		connection.commit();
	}

    public void rollback() throws SQLException {
		connection.rollback();
	}
    

    public void getMetaData(String query) throws SQLException {
        if (connection == null || statement == null) {
            errorMessage = "There is no database to execute the query.";
            System.err.println( errorMessage );
            //System.err.println("There is no database to execute the query.");
            return;
        }
        try {
            resultSet = statement.executeQuery(query);
            metaData = resultSet.getMetaData();

            int numberOfColumns =  metaData.getColumnCount();
            columnNames = new String[numberOfColumns];
            columnClasses = new Class[numberOfColumns];
            // Get the column names and cache them.
            for(int column = 0; column < numberOfColumns; column++) {
                columnNames[column] = metaData.getColumnLabel(column+1);
                columnClasses[column] = setColumnClass( column );
            }
        }
        catch (SQLException ex) {
            errorMessage = ex.getMessage();
            System.err.println(errorMessage);
            throw ex;
        }
    }

    public Class setColumnClass(int column)throws SQLException {
        int type;
        try {
            type = metaData.getColumnType(column+1);
        }
        catch (SQLException e) {
            throw e;
        }

        switch(type) {
        case Types.CHAR:
        case Types.VARCHAR:
        case Types.LONGVARCHAR:
            return String.class;

        case Types.BIT:
            return Boolean.class;

        case Types.TINYINT:
        case Types.SMALLINT:
        case Types.INTEGER:
            return Integer.class;

        case Types.BIGINT:
            return Long.class;

        case Types.FLOAT:
        case Types.DOUBLE:
        case Types.DECIMAL: 
        case Types.NUMERIC: 
            return Double.class;

        case Types.DATE:

        case Types.TIME: 
            return java.sql.Time.class;
        case Types.TIMESTAMP: 
            return java.sql.Timestamp.class;
        default: 
            return Object.class;
        }
    }

    public ResultSet getResutlSet()
    {
        return resultSet;
    }

    public void close() throws SQLException {
        if( resultSet != null )
            resultSet.close();
        if( statement != null )
            statement.close();
        if (connection != null) {
    		if (RemoteMethodServer.getDefault().ServerFlag) { 
//    			if(methodcontext != null) methodcontext.freeConnection();
    		}
        }
    }

    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }


    public String getColumnName(int column) {
        if (columnNames[column] != null) {
            return columnNames[column];
        } else {
            return "";
        }
    }

    public Class getColumnClass(int column) {
        if (columnClasses[column] != null) {
            return columnClasses[column];
        } else {
            return null;
        }
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return rows.size();
    }

    public int getUpdateCount()
    {
        return updateCount;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public Connection getConnection()
    {
        return connection;
    }
	
	public static void main(String[] args) throws SQLException  {
		DBConn conn = null;
		ResultSet rs = null;
		try {
			conn = new DBConn();
			String sql = "SELECT * FROM nriet_numberclassification";
			rs = conn.executeQuery(sql);
			while(rs.next()){
				System.out.println("Classification_Name = " + rs.getString("Classification_Name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				conn.close();
			} catch (SQLException sqe) {
				sqe.printStackTrace();
			}
		}
	}
}
