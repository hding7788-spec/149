package ext.sast.center.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseUtil {

    private static final String propertiesFilePath = "/ext/sast/center/center.properties";

    /**
     * 获取数据库连接
     *
     * @return
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            PropertiesUtil propertiesUtil = new PropertiesUtil(propertiesFilePath);
            String driver = propertiesUtil.getProperty("driver_oracle");
            String url = propertiesUtil.getProperty("149_pdm_url");
            String userName = propertiesUtil.getProperty("149_pdm_username");
            String password = propertiesUtil.getProperty("149_pdm_password");
            Class.forName(driver);
            connection = DriverManager.getConnection(url, userName, password);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("加载Oracle驱动失败！");
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * 关闭数据库连接
     *
     * @param connection
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
