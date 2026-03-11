package ext.casc.workflow.processor;

import com.glaway.mpm.util.MPMProcessPlanUtil;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.ases.envelope.ProcessEnvelopeUtil;
import ext.casc.util.IBAHelper;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.util.WTException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 工作流表达式处理器，用于设置工艺计划文档的 IBA 属性值。
 * @author fny
 * @date 2025/06/18
 */
public class WorkflowExpressionProcessor {
	private static final String PROCESS_PLAN_TYPE = "casc.sast.149.PROCESS_PLAN";

	/**
	 * 主要是为了设置定额状态的
	 * 为关联的 WTDocument 设置指定的 IBA 属性值。
	 * 支持的输入对象包括 WTChangeOrder2、ProcessEnvelope 和 WTDocument。
	 * 示例用法：
	 * <pre>
	 * ext.casc.workflow.processor.WorkflowExpressionProcessor.setIBAValue(primaryBusinessObject, "GongShiDingEState", "进行中");
	 * ext.casc.workflow.processor.WorkflowExpressionProcessor.setIBAValue(primaryBusinessObject, "GongShiDingEState", "保存提交");
	 * ext.casc.workflow.processor.WorkflowExpressionProcessor.setIBAValue(primaryBusinessObject, "GongShiDingEState", "已审核");
	 * </pre>
	 *
	 * @param obj   输入对象（WTChangeOrder2、ProcessEnvelope 或 WTDocument）
	 * @param name  要设置的 IBA 属性名称
	 * @param value IBA 属性的值
	 * @throws WTException 如果处理对象或设置 IBA 值时发生错误
	 */
	public static void setIBAValue(WTObject obj, String name, String value) throws WTException {
		// 空值检查
		if(obj == null) {
			return;
		}
		if(name == null || name.trim().isEmpty()) {
			return;
		}
		if(value == null) {
			return;
		}

		Set<WTDocument> docs = findProcessPlanDocument(obj);
		try {
			for(WTDocument doc : docs) {
				IBAHelper.setIBAStringValue(doc, name, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从输入对象中查找 PROCESS_PLAN 类型的 WTDocument。
	 *
	 * @param obj 输入对象（WTChangeOrder2、ProcessEnvelope 或 WTDocument）
	 * @return 匹配的 WTDocument，若未找到则返回 null
	 * @throws WTException 如果查询对象时发生错误
	 */
	private static Set<WTDocument> findProcessPlanDocument(WTObject obj) throws WTException {
		Set<WTDocument>  docs = new HashSet<>();
		if (obj instanceof WTChangeOrder2) {
			WTChangeOrder2 ecn = (WTChangeOrder2) obj;
			QueryResult qResult = ChangeHelper2.service.getChangeablesAfter(ecn);
			while (qResult.hasMoreElements()) {
				Object element = qResult.nextElement();
				if (element instanceof WTDocument) {
					WTDocument doc = (WTDocument) element;
					docs.add(doc);
				}
			}
		} else if (obj instanceof ProcessEnvelope) {
			ProcessEnvelope pe = (ProcessEnvelope) obj;
			List<MPMOperation> ops = new ArrayList<MPMOperation>();
			List members = ProcessEnvelopeUtil.getAllMembers(pe);
			for(Object member : members) {
				if(member instanceof MPMOperation) {
					ops.add((MPMOperation) member);
				}
			}
			for(MPMOperation operation : ops) {
				MPMOperationUsageLink link = MPMProcessPlanUtil.getMPMOperationUsageLinkByMPMOperation(operation);
				if(link != null) {
					if(link.getRoleAObject() != null && link.getRoleAObject() instanceof MPMProcessPlan){
						MPMProcessPlan plan = (MPMProcessPlan) link.getRoleAObject();
						//获取关联的工艺，更新工时标识
						WTDocument document = MPMProcessPlanUtil.getWTDocumentByProcessPlan(plan);
						if(document != null) {
							docs.add(document);
						}
					}
				}
			}
		} else if (obj instanceof WTDocument) {
			WTDocument doc = (WTDocument) obj;
			docs.add(doc);
		}
		return docs;
	}

}