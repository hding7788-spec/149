/**
 *
 */
package ext.sast.center.synch;

import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.preview.Preview;
import ext.casc.synch.DataSynchHelper;
import wt.doc.WTDocument;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.util.WTException;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

/**
 * @author cfire
 *
 */
public class MQDataExportHelper implements RemoteAccess {
	private MQDataExportHelper() {
	}

	/**
	 * 工艺通知单外发数据打包入口
	 *
	 * @param WTDocument
	 *            流程PBO对象
	 * @return 打包成功后的全文件路径
	 * @throws WTException
	 */
	public static String exportProcessNotice(WTDocument doc,String selectUnitValue) throws WTException {
		if (!RemoteMethodServer.ServerFlag) {
			String method = "exportProcessNotice";
			Class[] types = { WTDocument.class,String.class};
			Object[] vals = { doc,selectUnitValue };
			try {
				return (String) RemoteMethodServer.getDefault().invoke(method, MQDataExportHelper.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			if(selectUnitValue!=null&&MQExpImpUtil.isCmPackage(selectUnitValue)){
				//返回给八部、805
				return DataSynchHelper.exportProcessNotice(doc);
			}else{
				return new MQDataExportService().exportProcessNotice(doc);

			}

		}
		return null;
	}

	public static String exportCommonProcess(WTDocument doc) throws WTException {
		if (!RemoteMethodServer.ServerFlag) {
			String method = "exportCommonProcess";
			Class[] types = { WTDocument.class};
			Object[] vals = { doc };
			try {
				return (String) RemoteMethodServer.getDefault().invoke(method, MQDataExportHelper.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			return DataSynchHelper.exportCommonProcess(doc);
		}
		return null;
	}

	public static String exportProcessEnvelopeTargets(ProcessEnvelope pe,String sendFrom) throws WTException {
		if (!RemoteMethodServer.ServerFlag) {
			String method = "exportProcessEnvelopeTargets";
			Class[] types = { ProcessEnvelope.class ,String.class};
			Object[] vals = { pe,sendFrom };
			try {
				return (String) RemoteMethodServer.getDefault().invoke(method, MQDataExportHelper.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			if((sendFrom!=null&&(sendFrom.contains("805")||sendFrom.contains("no8")||sendFrom.contains("八部")))||MQConstants.units.contains(sendFrom)){
				//返回给八部、805
				return DataSynchHelper.exportProcessEnvelopeTargets(pe);
			}else{
				return new MQDataExportService().exportProcessEnvelopeTargets(pe);
			}

		}
		return null;
	}




	public static String exportChangeRequestTargets(ChangeRequest cr,String sendFrom) throws WTException {
		if (!RemoteMethodServer.ServerFlag) {
			String method = "exportChangeRequestTargets";
			Class[] types = { ChangeRequest.class ,String.class};
			Object[] vals = { cr,sendFrom };
			try {
				return (String) RemoteMethodServer.getDefault().invoke(method, MQDataExportHelper.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		} else {
			if((sendFrom!=null&&(sendFrom.contains("805")||sendFrom.contains("no8")||sendFrom.contains("八部")))||MQConstants.units.contains(sendFrom)){
				//返回给八部、805
				return DataSynchHelper.exportChangeRequestTargets(cr);
			}else{

			}
		}
		return null;
	}

	public static String exportChangePackagedTargets(ChangePackaged packaged,String sendFrom) throws WTException {
		if (!RemoteMethodServer.ServerFlag) {
			String method = "exportChangePackagedTargets";
			Class[] types = { ChangePackaged.class,String.class };
			Object[] vals = { packaged ,sendFrom};
			try {
				return (String) RemoteMethodServer.getDefault().invoke(method, MQDataExportHelper.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		} else {
			if((sendFrom!=null&&(sendFrom.contains("805")||sendFrom.contains("no8")||sendFrom.contains("八部")))||MQConstants.units.contains(sendFrom)){
				//返回给八部、805
				return DataSynchHelper.exportChangePackagedTargets(packaged);
			}else{
				return DataSynchHelper.exportChangePackagedTargets(packaged);
			}
		}
		return null;
	}

	public static String exportPreviewTargets(Preview preview,String sendFrom) throws WTException {
		if (!RemoteMethodServer.ServerFlag) {
			String method = "exportPreviewTargets";
			Class[] types = { Preview.class ,String.class};
			Object[] vals = { preview,sendFrom};
			try {
				return (String) RemoteMethodServer.getDefault().invoke(method, MQDataExportHelper.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		} else {
			if((sendFrom!=null&&(sendFrom.contains("805")||sendFrom.contains("no8")||sendFrom.contains("八部")))||MQConstants.units.contains(sendFrom)){
				//返回给八部、805
				return DataSynchHelper.exportPreviewTargets(preview);
			}else{

			}
		}
		return null;
	}
}
