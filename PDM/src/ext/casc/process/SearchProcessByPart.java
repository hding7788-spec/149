package ext.casc.process;

import java.util.ArrayList;

import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;

import ext.casc.util.IBAUtility;

import wt.change2.WTChangeOrder2;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;
import wt.workflow.engine.WfEngineHelper;

public class SearchProcessByPart {

	public static ArrayList getProcess(WTPart  part) throws WTException {

		ArrayList list= new ArrayList();
		QuerySpec qSpec;

			qSpec = new QuerySpec(ProcessTaskItem.class);
			int[] index = { 0 };
	         SearchCondition sCondition = new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.NUMBER,SearchCondition.EQUAL, part.getNumber());
	         qSpec.appendWhere(sCondition, index);
	         QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
	         while(qResult.hasMoreElements()) {
	        	 ProcessTaskItem processTaskItem=(ProcessTaskItem) qResult.nextElement();
	        	 if (processTaskItem.getTaskType().equals("报表类工艺任务")&&processTaskItem.getTaskItemName().equals("报表类工艺编制")) {
	 				IBAUtility ibaUtility1 = new IBAUtility(processTaskItem);
	 				String value = ibaUtility1.getIBAValue("TECHNICSREPORTSTYLE");
	 				list.add(value);
	 			}
	         }

	return	getTechnicsStyle(list);

	}
     public static ArrayList getTechnicsStyle(ArrayList list) {
    	 ArrayList oldList= new ArrayList();
    	 oldList.add("工艺路线表");
    	 oldList.add("工艺装备明细表");
    	 oldList.add("仪器仪表明细表");
    	 oldList.add("非标仪器仪表、设备明细表");
    	 oldList.add("标准刀量具明细表");
    	 oldList.add("外协件明细表");
    	 oldList.add("关键工序明细表");
    	 oldList.add("材料消耗工艺定额明细表");
    	 oldList.add("辅助材料定额表");
    	 oldList.add("辅助材料定额汇总表");
    	 oldList.add("外购件（元器件、标准件）消耗工艺定额汇总表");
    	 oldList.add("工艺文件目录");
    	 ArrayList list1= new ArrayList();
    	 for (int i = 0; i < oldList.size(); i++) {
    		 for (int j = 0; j < list.size(); j++) {
    			 if (oldList.get(i).equals(list.get(j))) {
    				 list1.add(oldList.get(i));
				}
			}
		}
    	 oldList.removeAll(list1);
   return oldList;

	}


}
