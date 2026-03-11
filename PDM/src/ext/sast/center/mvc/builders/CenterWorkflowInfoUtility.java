package ext.sast.center.mvc.builders;

import com.ptc.core.components.rendering.guicomponents.TextDisplayComponent;
import ext.sast.center.record.bean.GWMQRecord;
import ext.sast.center.synch.MQConstants;
import wt.httpgw.URLFactory;
import wt.util.WTException;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import ext.casc.util.NmTableGUIComponent;
import ext.sast.center.bean.message.WorkflowMessage;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class CenterWorkflowInfoUtility extends AbstractDataUtility {
	public Object getDataValue(String columnName, Object obj, ModelContext mc) throws WTException {
		GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
		URLFactory factory = new URLFactory();
		if("taskInfo".equals(columnName)){
			String base = factory.getHREF("/netmarkets/jsp/ext/sast/center/workflow/centerWorkflowTable.jsp?msg_id=");
			WorkflowMessage message = (WorkflowMessage) obj;
			String value = "<a href='javascript:void(0)' onclick='window.open(\"" + base + message.getUnit() + "\",\"_blank\")'>"+message.getTaskInfo()+"</a>";
			NmTableGUIComponent gui = new NmTableGUIComponent(value);
			guicomponentarrayMain.addGUIComponent(gui);
			// NmAction action = new NmAction();
			// action.setIcon("details.gif");
			// action.setPageURL(base + awf.getPartOid());
			// action.setToolTip("查看详细信息");
			return guicomponentarrayMain;
		}else if("operation".equals(columnName)){
			String base = factory.getHREF("/netmarkets/jsp/ext/sast/center/workflow/retry.jsp?msg_id=");
			WorkflowMessage message = (WorkflowMessage) obj;
			String value = "<a href='javascript:void(0)' onclick='window.open(\"" + base + message.getUnit() + "\",\"_blank\")'>重试</a>";
			NmTableGUIComponent gui = new NmTableGUIComponent(value);
			guicomponentarrayMain.addGUIComponent(gui);
			// NmAction action = new NmAction();
			// action.setIcon("details.gif");
			// action.setPageURL(base + awf.getPartOid());
			// action.setToolTip("查看详细信息");
			return guicomponentarrayMain;
		}else if("operation2".equals(columnName)){
			String base = factory.getHREF("/netmarkets/jsp/ext/sast/center/workflow/retry2.jsp?msg_id=");
			WorkflowMessage message = (WorkflowMessage) obj;
			String value = "<a href='javascript:void(0)' onclick='window.open(\"" + base + message.getUnit() + "\",\"_blank\")'>重新导入</a>";

			if(message.getTaskRemark().equals(MQConstants.SITENAME_149)){
				base = factory.getHREF("/netmarkets/jsp/ext/sast/center/workflow/reSend.jsp?msg_id=");
				value = "<a href='javascript:void(0)' onclick='window.open(\"" + base + message.getUnit() + "\",\"_blank\")'>重新发送</a>";
			}
			NmTableGUIComponent gui = new NmTableGUIComponent(value);
			guicomponentarrayMain.addGUIComponent(gui);
			// NmAction action = new NmAction();
			// action.setIcon("details.gif");
			// action.setPageURL(base + awf.getPartOid());
			// action.setToolTip("查看详细信息");
			return guicomponentarrayMain;
		}else if("feedback".equals(columnName)){
			WorkflowMessage message = (WorkflowMessage) obj;
			if(message.getTaskRemark().equals(MQConstants.SITENAME_149)) {//发送方是149
				return "";
			}
			String base = factory.getHREF("/netmarkets/jsp/ext/sast/center/workflow/feedback.jsp?msg_id=");

			String value = "<a href='javascript:void(0)' onclick='window.open(\"" + base + message.getUnit() + "\",\"_blank\")'>重新发送意见</a>";
			NmTableGUIComponent gui = new NmTableGUIComponent(value);
			guicomponentarrayMain.addGUIComponent(gui);
			// NmAction action = new NmAction();
			// action.setIcon("details.gif");
			// action.setPageURL(base + awf.getPartOid());
			// action.setToolTip("查看详细信息");
			return guicomponentarrayMain;
		}else if("receiveFeedback".equals(columnName)){
			String base = factory.getHREF("/netmarkets/jsp/ext/sast/center/workflow/receiveFeedback.jsp?msg_id=");
			WorkflowMessage message = (WorkflowMessage) obj;
			if(!message.getTaskRemark().equals(MQConstants.SITENAME_149)) {//发送方不是149
				return "";
			}
			String value = "<a href='javascript:void(0)' onclick='window.open(\"" + base + message.getUnit() + "\",\"_blank\")'>重新接收意见</a>";
			NmTableGUIComponent gui = new NmTableGUIComponent(value);
			guicomponentarrayMain.addGUIComponent(gui);
			// NmAction action = new NmAction();
			// action.setIcon("details.gif");
			// action.setPageURL(base + awf.getPartOid());
			// action.setToolTip("查看详细信息");
			return guicomponentarrayMain;
		}
		else if(columnName.equals(GWMQRecord.ORDERNUMBER)){
			GWMQRecord gwmqRecord = (GWMQRecord) obj;
			String sendType_ZH = gwmqRecord.getSendType();
			String sendType_EN  ="";
			if("送审单".equals(sendType_ZH)){
				sendType_EN = "songshendan";
			}else if("更改单".equals(sendType_ZH)){
				sendType_EN = "genggaidan";
			}else if("更改申请".equals(sendType_ZH)){
				sendType_EN = "genggaishenqing";
			}else if("预审单".equals(sendType_ZH)){
				sendType_EN = "yushendan";
			}
			String processName = gwmqRecord.getProcessName();
			if(processName != null && processName.contains("技术通知单")){
				sendType_EN = "processNotice";
			}

			String url = factory.getHREF("/netmarkets/jsp/ext/sast/synergy/synergyOrderNumberNavigation.jsp?number="+gwmqRecord.getOrderNumber()+"&type="+sendType_EN);
			String value = "<a href='javascript:void(0)' onclick='window.open(\"" + url + "\",\"_blank\")'>"+ gwmqRecord.getOrderNumber()+"</a>";
			NmTableGUIComponent gui = new NmTableGUIComponent(value);
			guicomponentarrayMain.addGUIComponent(gui);
			return guicomponentarrayMain;
		}else if(columnName.equals(GWMQRecord.MSGSTATE)){
			GWMQRecord gwmqRecord = (GWMQRecord) obj;
			String base = factory.getHREF("/netmarkets/jsp/ext/sast/center/workflow/centerWorkflowTable.jsp?msg_id=");
			String value = "<a href='javascript:void(0)' onclick='window.open(\"" + base + gwmqRecord.getMsgId() + "\",\"_blank\")'>"+gwmqRecord.getMsgState()+"</a>";
			NmTableGUIComponent gui = new NmTableGUIComponent(value);
			guicomponentarrayMain.addGUIComponent(gui);
			return guicomponentarrayMain;
		}else if("cz".equals(columnName)){
			String base = factory.getHREF("/netmarkets/jsp/ext/sast/synergy/retryUnion.jsp?msg_id=");
			GWMQRecord gwmqRecord = (GWMQRecord) obj;
			String value = "<a href='javascript:void(0)' onclick='window.open(\"" + base + gwmqRecord.getMsgId() + "\",\"_blank\")'>重试</a>";
			NmTableGUIComponent gui = new NmTableGUIComponent(value);
			guicomponentarrayMain.addGUIComponent(gui);
			return guicomponentarrayMain;
		} else if (columnName.equals("cxfk")) {
			if(!isSelfSend(obj)) {
				GWMQRecord gwmqRecord = (GWMQRecord) obj;
				String base = factory.getHREF("/netmarkets/jsp/ext/sast/synergy/sendFeedback.jsp?msg_id=");
				String value = "<a href='javascript:void(0)' onclick='window.open(\"" + base + gwmqRecord.getMsgId() + "\",\"_blank\")'>发送反馈意见</a>";
				ext.sast.center.productModel.util.NmTableGUIComponent gui = new ext.sast.center.productModel.util.NmTableGUIComponent(value);
				guicomponentarrayMain.addGUIComponent(gui);
			}
			return guicomponentarrayMain;
		}
		return guicomponentarrayMain;
	}

	private boolean isSelfSend(Object obj) {
		if(obj instanceof GWMQRecord){
			GWMQRecord wm = (GWMQRecord)obj;
			String sender = wm.getSender();
			if(sender!=null&&sender.contains("805")){
				return  true;
			}
		}
		return false;
	}

}