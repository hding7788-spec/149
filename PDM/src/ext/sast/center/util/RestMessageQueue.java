package ext.sast.center.util;

import com.bjsasc.avidm.mq.config.Config;
import com.bjsasc.avidm.mq.manager.QueueManager;
import org.apache.log4j.Logger;
import wt.log4j.LogR;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.pom.WTConnection;

import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RestMessageQueue implements RemoteAccess{
	private static final Logger logger = LogR.getLogger(RestMessageQueue.class.getName());

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
//		RemoteMethodServer rms = RemoteMethodServer.getDefault();
//		rms.setUserName("wcadmin");
//		rms.setPassword("wcadmin");
//		try {
//			Class[] types = { };
//			Object[] vls  = { };
//			rms.invoke("startRestQueue", RestMessageQueue.class.getName(), null, types, vls);
//			System.out.println();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

			String phaseInfo = ""/* attrConvertValue("phaseInfo","M","1") */;
			System.out.println("phaseInfo == "+phaseInfo);

	}

	public static void startRestQueue() {
		logger.info("开始初始化队列服务器...");
		try {
			boolean isBg = ManagementFactory.getRuntimeMXBean().getInputArguments().contains("-Dwt.manager.serviceName=BackgroundMethodServer");
			//需提前配置好配置文件
			Config config = Config.getInstance();
			QueueManager manager = QueueManager.getInstance();
//			manager.setBg(isBg);
			manager.config(config);
			manager.init();
			logger.info("成功初始化队列服务器...");
		} catch (Exception e) {
			logger.error("初始化队列服务器失败...",e);
			//throw new RuntimeException(e);
		}
	}

	public static boolean checkIsBg(){
		boolean isBg = false;




		String jvmId = ManagementFactory.getRuntimeMXBean().getName();
		MethodContext methodcontext = MethodContext.getContext();
		WTConnection wtconnection;
		Connection conn;
		PreparedStatement pstmt = null;
		ResultSet set = null;
		String lastestBgId = "";
		try {
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			String sql = "select jvm_id from methodserverinfo where SERVICENAME='BackgroundMethodServer' order by JVM_STARTTIME desc";
			pstmt = conn.prepareStatement(sql);
			set = pstmt.executeQuery();
			if(set.next()){
				lastestBgId = set.getString(1);
			}
			System.out.println("currend_jvmId:" + jvmId + ",lastestBg_jvmId:" + lastestBgId);
			if(jvmId != null && jvmId.equals(lastestBgId)){
				isBg = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(set != null){
					set.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return isBg;
	}
}
