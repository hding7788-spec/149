package ext.casc.distribute.util;

import wt.method.MethodContext;
import wt.pom.WTConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DWSqlUtil {

    public static List<Map<String, Object>> getSqlResultList(String sql, List<String> propertyList) {

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        WTConnection wtconnection = null;
        try {
            MethodContext methodcontext = MethodContext.getContext();
            wtconnection = (WTConnection) methodcontext.getConnection();
            pstmt = wtconnection.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                for(String key: propertyList)
                {
                    map.put(key, rs.getString(key));
                }
                result.add(map);
            }
            rs.close();
            pstmt.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String getSqlResultString(String sql) {

        String result = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        WTConnection wtconnection = null;
        try {
            MethodContext methodcontext = MethodContext.getContext();
            wtconnection = (WTConnection) methodcontext.getConnection();
            pstmt = wtconnection.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                result = rs.getString(1);
            }
            rs.close();
            pstmt.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static int getSqlResultInt(String sql) {

        int result = 0;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        WTConnection wtconnection = null;
        try {
            MethodContext methodcontext = MethodContext.getContext();
            wtconnection = (WTConnection) methodcontext.getConnection();
            pstmt = wtconnection.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                result = rs.getInt(1);
            }
            rs.close();
            pstmt.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static Timestamp getSqlResultTimestamp(String sql) {

        Timestamp result = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        WTConnection wtconnection = null;
        try {
            MethodContext methodcontext = MethodContext.getContext();
            wtconnection = (WTConnection) methodcontext.getConnection();
            pstmt = wtconnection.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                result = rs.getTimestamp(1);
            }
            rs.close();
            pstmt.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    public static List<Map<String, Object>> getSqlResultListMap(String sql) {

        List<Map<String, Object>> result = new ArrayList<>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        WTConnection wtconnection = null;
        try {
            MethodContext methodcontext = MethodContext.getContext();
            wtconnection = (WTConnection) methodcontext.getConnection();
            pstmt = wtconnection.prepareStatement(sql);
            rs = pstmt.executeQuery();

            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            int columnCount =  resultSetMetaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                for(int i=1;i<columnCount;i++)
                {
                    String columnName = resultSetMetaData.getColumnName(i);
                    Object columnValue = rs.getObject(i);
                    map.put(columnName, columnValue);
                }
                result.add(map);
            }
            rs.close();
            pstmt.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
