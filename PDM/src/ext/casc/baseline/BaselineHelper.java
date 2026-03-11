package ext.casc.baseline;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

import wt.fc.ReferenceFactory;
import wt.inf.container.WTContainer;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.vc.baseline.Baselineable;
import wt.vc.baseline.ManagedBaseline;
import ext.casc.util.RemoteUtility;
import ext.casc.workflow.util.WorkflowUtil;
import ext.casc.workflow.util.WorkflowValidationUtil;

public class BaselineHelper implements RemoteAccess, Serializable{
	private static final long serialVersionUID = 7845982074595365727L;
	
	public static String BASELINE_NUMBER = CSCBaseline.NUMBER;
	public static String BASELINE_NAME = CSCBaseline.NAME;
	public static String BASELINE_DESC = CSCBaseline.DESCRIPTION;
	
	/**
	 * 创建新的基线<br>
	 * <br>
	 * @param name		基线名称<br>
	 * @param number	基线编号<br>
	 * @param desc		基线详细信息<br>
	 * @param container	上下文<br>
	 * @return
	 */
	public static ManagedBaseline newBaseline(String name, String number, String desc, WTContainer container){		
		try {
			if (!RemoteMethodServer.ServerFlag) {
				return (ManagedBaseline) RemoteMethodServer.getDefault().invoke("newBaselineRemote", BaselineHelper.class.getName(), null,
						new Class[] {String.class, String.class, String.class, WTContainer.class},
						new Object[] {name, number, desc, container});
			} else {
				return newBaselineRemote(name, number, desc, container);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ManagedBaseline newBaselineRemote(String name, String number, String desc, WTContainer container){
		HashMap<String, String> hmAttribute = new HashMap<String, String>();
		hmAttribute.put(CSCBaseline.DESCRIPTION, desc);
		
		ManagedBaseline baseline = CSCBaseline.createBaseline(name, number, hmAttribute, container);
		return baseline;
	}
	
	/**
	 * 修改基线信息<br>
	 * <br>
	 * @param baseline	待修改的基线对象<br>
	 * @param name		基线名称<br>
	 * @param desc		基线描述<br>
	 * @return
	 */
	public static ManagedBaseline modifyBaseline(ManagedBaseline baseline, String desc){
		try {
			if (!RemoteMethodServer.ServerFlag) {
				return (ManagedBaseline) RemoteMethodServer.getDefault().invoke("modifyBaselineRemote", BaselineHelper.class.getName(), null,
						new Class[] {ManagedBaseline.class, String.class, String.class},
						new Object[] {baseline, desc});
			} else {
				return modifyBaselineRemote(baseline, desc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ManagedBaseline modifyBaselineRemote(ManagedBaseline baseline, String desc){
		baseline = CSCBaseline.updateBaseline(baseline, desc);
		return baseline;
	}
	
	/**
	 * 得到基线属性信息<br>
	 * <br>
	 * @param baseline
	 * @return
	 */
	public static HashMap getBasselineAttribute(ManagedBaseline baseline){
		HashMap result = new HashMap();
		if(baseline == null) return result;
		
		result.put(CSCBaseline.NUMBER, baseline.getNumber());
		result.put(CSCBaseline.NAME, baseline.getName());
		result.put(CSCBaseline.DESCRIPTION, baseline.getDescription());
		return result;
	}
	
	/**
	 * 得到基线内容<br>
	 * <br>
	 * @param baseline
	 * @return
	 */
	public static Vector getBaselinable(ManagedBaseline baseline){
		try {
			if (!RemoteMethodServer.ServerFlag) {
				return (Vector) RemoteMethodServer.getDefault().invoke("getBaselinableRemote", BaselineHelper.class.getName(), null,
						new Class[] {ManagedBaseline.class},
						new Object[] {baseline});
			} else {
				return getBaselinableRemote(baseline);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Vector getBaselinableRemote(ManagedBaseline baseline){
		Vector result = new Vector();
		if(baseline == null) return result;
		
		result = CSCBaseline.getBaselineItems(baseline);
		return result;
	}
	
	/**
	 * 添加基线内容<br>
	 * <br>
	 * @param baseline	待添加的基线对象<br>
	 * @param object	可被添加得内容<Br>
	 * @return
	 */
	public static void addBaselineable(ManagedBaseline baseline, Baselineable object){
		try {
			if (!RemoteMethodServer.ServerFlag) {
				RemoteMethodServer.getDefault().invoke("addBaselineableRemote", BaselineHelper.class.getName(), null,
						new Class[] {ManagedBaseline.class, Baselineable.class},
						new Object[] {baseline, object});
			} else {
				addBaselineableRemote(baseline, object);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void addBaselineableRemote(ManagedBaseline baseline, Baselineable object){
		CSCBaseline.addBaselineable(baseline, object);
	}
	
	/**
	 * 移除所有基线内容<br>
	 * <br>	
	 * @param baseline	待处理的基线对象<br>
	 * @return
	 */
	public static ManagedBaseline removeBaselineable(ManagedBaseline baseline){
		try {
			if (!RemoteMethodServer.ServerFlag) {
				return (ManagedBaseline) RemoteMethodServer.getDefault().invoke("removeBaselineableRemote", BaselineHelper.class.getName(), null,
						new Class[] {ManagedBaseline.class},
						new Object[] {baseline});
			} else {
				return removeBaselineableRemote(baseline);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ManagedBaseline removeBaselineableRemote(ManagedBaseline baseline){
		Vector baselineItems = CSCBaseline.getBaselineItems(baseline);
		for (int i = 0; i < baselineItems.size(); i++) {
			Baselineable baselineable = (Baselineable)baselineItems.get(i);
			CSCBaseline.removeFromBaseline(baseline, baselineable);
		}
		return baseline;
	}

	
	/**
	 * 基线是不是已批准状态
	 * @param baseline
	 * @return
	 */
	public static boolean isBaselineApproved(ManagedBaseline baseline) {
		if (baseline != null) {
			String resultString = WorkflowUtil.isWTObjectAtAssignedState(
					baseline, baseline.getDisplayIdentifier() + "状态不是已批准",
					State.toState("APPROVED"));
			return (WorkflowValidationUtil.VALIDATE_OK.equals(resultString)) ? true : false;
		} else {
			return false;
		}
	}
	

	/**
	 * 提交基线为归档，只有批准才能提交
	 * @param oid
	 * @throws WTRuntimeException
	 * @throws WTException
	 */
	public static void submitArchive(String oid) throws WTRuntimeException, WTException{
		ReferenceFactory rf = new ReferenceFactory();
		ManagedBaseline mb = (ManagedBaseline)rf.getReference(oid).getObject();
		if (!BaselineHelper.isBaselineApproved(mb)) throw new WTException("基线不是已批准状态!");
		RemoteUtility.setBaselineLifeCycleState(mb, State.toState("ASES_ARCHIVING"));
	}

}
