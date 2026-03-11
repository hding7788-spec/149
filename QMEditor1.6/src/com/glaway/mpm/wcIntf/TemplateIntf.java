package com.glaway.mpm.wcIntf;

import com.glaway.mpm.model.TpType;
import com.glaway.mpm.qmIntf.template.StepTemplateUtil;
import com.glaway.mpm.util.IntfUtil;
import com.glaway.mpm.util.WTContainerUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.visual.log.VaLogger;
import wt.doc.WTDocument;
import wt.inf.container.WTContainer;
import wt.method.RemoteAccess;
import wt.part.WTPart;
import wt.util.WTException;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

/**
 * @author ylshao
 * @ClassName: ProcessEditorToWCIntf
 * @Description:
 * @date 2012-11-28
 *
 */
public class TemplateIntf implements RemoteAccess {
	private static VaLogger logger = VaLogger.getLogger(TemplateIntf.class);

	/**
	 * 查询工艺模板
	 *
	 * @author ylshao
	 * @date 2012-11-1
	 * @return
	 */
	public static TpType getProcessTemplates(String type) {
		TpType tpType = (TpType) remoteMethodInvoke("getProcessTemplatesRMI",
				new Class[] { String.class }, new Object[] { type });
		if (tpType == null) {
			tpType = new TpType(StepTemplateUtil.typeSwitch(type));
		}
		return tpType;
	}

	/**
	 * 查询工序模板
	 *
	 * @author ylshao
	 * @date 2012-11-1
	 * @return
	 */
	public static TpType getStepTemplates(String type) {
		TpType tpType = (TpType) remoteMethodInvoke("getStepTemplatesRMI",
				new Class[] { String.class }, new Object[] { type });
		if (tpType == null) {
			tpType = new TpType(StepTemplateUtil.typeSwitch(type));
		}
		return tpType;
	}

	/**
	 * 下载工艺模板
	 *
	 * @author ylshao
	 * @date 2012-11-1
	 * @return
	 */
	public static Vector<Object> downloadProcessTemplate(String oid) {
		return (Vector<Object>) remoteMethodInvoke(
				"downloadProcessTemplateRMI", new Class[] { String.class },
				new Object[] { oid });
	}

	/**
	 * 工艺模板入库
	 *
	 * @author ylshao
	 * @date 2012-11-1
	 * @return
	 */
	public static String uploadProcessTemplate(List<String> tempalteTypeNames,
			String tempalteName, byte[] bytes) {
		return (String) remoteMethodInvoke("uploadProcessTemplateRMI",
				new Class[] { List.class, String.class, byte[].class },
				new Object[] { tempalteTypeNames, tempalteName, bytes });
	}

	/**
	 * 保存参数化模板
	 *
	 * @author cjh
	 * @date 2023-10-25
	 * @return
	 */
	public static String uploadProcessParamTemplate(String tempalteName, byte[] bytes, String dept, String field, String productType, String templateType) {
		return (String) remoteMethodInvoke("uploadProcessParamTemplateRMI",
				new Class[] { String.class, byte[].class,String.class,String.class,String.class,String.class },
				new Object[] { tempalteName, bytes,dept,field,productType,templateType });
	}

	/**
	 * 工序模板入库
	 *
	 * @author ylshao
	 * @date 2012-11-1
	 * @return
	 */
	public static String uploadStepTemplate(List<String> tempalteTypeNames,
			String tempalteName, byte[] bytes) {
		return (String) remoteMethodInvoke("uploadStepTemplateRMI",
				new Class[] { List.class, String.class, byte[].class },
				new Object[] { tempalteTypeNames, tempalteName, bytes });
	}

	/**
	 * @Title: remoteMethodInvoke
	 * @Description:
	 * @return Object
	 * @throws
	 */
	public static Object remoteMethodInvoke(String mentodName,
			Class[] classArray, Object[] objectArray) {
		return IntfUtil.getRemoteMethodInvoke(mentodName, classArray,
				objectArray);
	}

	public static List<WTDocument> getDwg2pdfTemplate(){
		return (List<WTDocument>) remoteMethodInvoke("getDwg2pdfTemplateRMI",
				new Class[] {  },
				new Object[] {  });
	}

	public static byte[] getDwgTemplateByte(String number){
		return (byte[]) remoteMethodInvoke("getDwgTemplateByteRMI",
				new Class[] { String.class },
				new Object[] { number });
	}

	public static List<WTDocument> getDwg2pdfTemplateRMI() throws WTException, RemoteException{
		List<WTDocument> docList = null;
		String type = "casc.sast.149.DWG2PDFTemplate";
		WTContainer container = WTContainerUtil.getLibraryByName("工艺资源库");
		docList = WTDocumentUtil.getDocumentByTypeAndConatiner(container, type);
		return docList;
	}

	public static byte[] getDwgPdfTemplateByteRMI(String number){
		return (byte[]) remoteMethodInvoke("getDwgPdfTemplateByteRMI",
				new Class[] { String.class },
				new Object[] { number });
	}

	public static byte[] getFuJianDocByteRMI(String number){
		return (byte[]) remoteMethodInvoke("getFuJianDocByteRMI",
				new Class[] { String.class },
				new Object[] { number });
	}


	public static void removeFujianDoc(String number){
		remoteMethodInvoke("removeFujianDoc",
				new Class[] { String.class },
				new Object[] { number });
	}

	public static WTDocument uploadDwgDoc(WTContainer container,String dwgNumber,String dwgName ,byte[] bytes, String fileName){
		return (WTDocument) remoteMethodInvoke("uploadDwgDocRMI",
				new Class[] { WTContainer.class, String.class, String.class, byte[].class, String.class},
				new Object[] { container, dwgNumber, dwgName, bytes, fileName});
	}
	/**add by liangbo */
	public static WTDocument uploadLargeFileDoc(WTContainer container,String largeFileNumber,String largeFileName ,byte[] bytes){
		return (WTDocument) remoteMethodInvoke("uploadLargeFileDoc",
				new Class[] { WTContainer.class, String.class, String.class, byte[].class},
				new Object[] { container, largeFileNumber, largeFileName, bytes});
	}
	public static WTDocument deleteLargeFileDoc(String largeFileNumber){
		return (WTDocument) remoteMethodInvoke("deleteLargeFileDoc",
				new Class[] { String.class },
				new Object[] { largeFileNumber });
	}
	@SuppressWarnings("unchecked")
	public static List<WTDocument> getAllLargeFileDoc(String likeName){
		return (List<WTDocument>) remoteMethodInvoke("getAllLargeFileDoc",
				new Class[] { String.class },
				new Object[] { likeName});
	}
	public static byte[] downloadLargeFile(String docNumber){
		return (byte[]) remoteMethodInvoke("downloadLargeFile",
				new Class[] { String.class },
				new Object[] { docNumber});
	}
	public static WTDocument uploadFuJianDoc(WTContainer container,String dwgNumber,String dwgName ,byte[] bytes){
		return (WTDocument) remoteMethodInvoke("uploadFuJianDocRMI",
				new Class[] { WTContainer.class, String.class, String.class, byte[].class},
				new Object[] { container, dwgNumber, dwgName, bytes});
	}

	public static WTPart getLatestParttByNumber(String partNumber){
		return (WTPart) remoteMethodInvoke("getLatestParttByNumberRMI",
				new Class[] { String.class },
				new Object[] { partNumber });
	}

	/**
	 * 查询工序模板
	 *
	 * @author chenjianhui
	 * @date 2022年1月5日16:00:23
	 * @return
	 */
	public static List<TpType> getAllStepTemplates(String type) {
		List<TpType> tpTypes = (List<TpType>) remoteMethodInvoke("getAllStepTemplatesRMI",
				new Class[] {}, new Object[] {});
		if (tpTypes.size() == 0) {
			TpType tpType = new TpType(StepTemplateUtil.typeSwitch(type));
			tpTypes.add(tpType);
		}
		return tpTypes;
	}

	public static List<TpType> getAllStepTemplates() {
		List<TpType> tpTypes = (List<TpType>) remoteMethodInvoke("getAllStepTemplatesRMI",
				new Class[] {}, new Object[] {});
		return tpTypes;
	}

}
