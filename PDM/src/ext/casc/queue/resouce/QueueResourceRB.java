package ext.casc.queue.resouce;

import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("ext.casc.queue.resouce.QueueResourceRB")
public class QueueResourceRB extends WTListResourceBundle {
	@RBEntry("流程编号")
	public static final String PROCESSNUMBER = "PROCESSNUMBER";
	
	@RBEntry("流程名称")
	public static final String PROCESSNAME = "PROCESSNAME";
	
	@RBEntry("发起人")
	public static final String INITIATOR = "INITIATOR";
	
	@RBEntry("发起时间")
	public static final String STARTINGTIME = "STARTINGTIME";
	
	@RBEntry("当前异常节点")
	public static final String CURRENTNODE = "CURRENTNODE";

}
