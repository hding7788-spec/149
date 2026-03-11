package ext.casc.queue.builder;


import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.util.ClientMessageSource;

import wt.queue.ProcessingQueue;
import wt.queue.WtQueue;
import wt.queue.mvc.builder.QueueEntryTable;
import wt.util.WTException;

@ComponentBuilder({"queueCustom.entry.table"})
public class CustomQueueEntryTable extends QueueEntryTable{
	/**
	 * 流程名称
	 */
	public static final String ATTR_PROCESSNAME="processName";
	
	/**
	 * 流程主要对象编号
	 */
	public static final String ATTR_PRIMARYOBJECTNUMBER="primaryObjectNumber";
	
	/**
	 * 负责人
	 */
	public static final String ATTR_PRINCIPAL="principal";
	
	/**
	 * 流程开始时间
	 */
	public static final String ATTR_STARTDATE="startDate";
	
	/**
	 * 当前节点
	 */
	public static final String ATTR_CURRENTNODE="CurrentNode";
	
	ClientMessageSource queue_source = this.getMessageSource("ext.casc.queue.resouce.QueueResourceRB");
	
	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
		WtQueue arg1 = (WtQueue) ((JcaComponentParams) arg0).getContextObject();
		ComponentConfig componentConfig=super.buildComponentConfig(arg0);
		ComponentConfigFactory arg2 = this.getComponentConfigFactory();
		
		if (arg1 instanceof ProcessingQueue) {
			ColumnConfig arg12 = arg2.newColumnConfig(ATTR_PRIMARYOBJECTNUMBER, true);
			arg12.setDataUtilityId("CustomQueueDataUtility");
			arg12.setLabel(this.queue_source.getMessage("PROCESSNUMBER"));
			componentConfig.addComponent(arg12);
			
			ColumnConfig arg11 = arg2.newColumnConfig(ATTR_PROCESSNAME, true);
			arg11.setDataUtilityId("CustomQueueDataUtility");
			arg11.setLabel(this.queue_source.getMessage("PROCESSNAME"));
			componentConfig.addComponent(arg11);
			
			ColumnConfig arg13 = arg2.newColumnConfig(ATTR_PRINCIPAL, true);
			arg13.setDataUtilityId("CustomQueueDataUtility");
			arg13.setLabel(this.queue_source.getMessage("INITIATOR"));
			componentConfig.addComponent(arg13);
			
			ColumnConfig arg14 = arg2.newColumnConfig(ATTR_STARTDATE, true);
			arg14.setDataUtilityId("CustomQueueDataUtility");
			arg14.setLabel(this.queue_source.getMessage("STARTINGTIME"));
			componentConfig.addComponent(arg14);
			
			ColumnConfig arg15 = arg2.newColumnConfig(ATTR_CURRENTNODE, true);
			arg15.setDataUtilityId("CustomQueueDataUtility");
			arg15.setLabel(this.queue_source.getMessage("CURRENTNODE"));
			componentConfig.addComponent(arg15);
			
		}
		return componentConfig;
	}
}
