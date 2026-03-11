package com.glaway.mpm;

import wt.util.WTProperties;

import java.util.ArrayList;
import java.util.List;

public class EditorConfig {
	 public  static boolean isZS = true;//是否正式机
	 public  static boolean isWebInfLib = false;//是否在WEB-INF/lib下部署
	 public static String startType = null;
	 public static List<String> ACL_POSITIVE = new ArrayList<String>();

	    static {
			try {
				String hostName = WTProperties.getLocalProperties().getProperty("wt.rmi.server.hostname");
				System.out.println("hostName:" + hostName);
				if(!"pdm.149.sast.casc".equals(hostName)) {
					isZS = false;
				}

				String osName = System.getProperties().getProperty("os.name");
				System.out.println("osName:" + osName);
				if(osName != null && (osName.contains("SunOS") || osName.contains("linux") || osName.contains("Linux") || osName.contains("solaris") || osName.contains("Solaris"))) {
					isWebInfLib = true;
				}
				ACL_POSITIVE.add("niyongjun");
				ACL_POSITIVE.add("Administrator");
				//ACL_POSITIVE.add("huhuili");
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

}
