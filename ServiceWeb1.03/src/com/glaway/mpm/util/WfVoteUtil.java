package com.glaway.mpm.util;

import wt.util.WTException;

public class WfVoteUtil {

	public static String getTechnicGroup(Object obj, Object self) throws WTException{
		String roleStr = "";
		roleStr = WorkflowUtil.getTheTeam(obj, self);
		GLLogger.debug("roleStr===>" + roleStr);

		if("MACHINE".equals(roleStr)){
			return "MACHINE";//机械组
		}else if("HEATWORKER".equals(roleStr)){
			return "HEATWORKER";//热加工组
		}else if("FACEDISPOSE".equals(roleStr)){
			return "FACEDISPOSE";//表面处理组
		}else if("MATERIAL".equals(roleStr)){
			return "MATERIAL";//材料组
		}else if("PRINTBOARD".equals(roleStr)){
			return "PRINTBOARD";//印制板组
		}else if("LOADJOIN".equals(roleStr)){
			return "LOADJOIN";//装联组
		}else if("MICROELECTRONIC".equals(roleStr)){
			return "MICROELECTRONIC";//微电子组
		}else{
			return "";
		}
	}

}
