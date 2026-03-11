/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command;

import com.glaway.mpm.intf.ProcessEditorToWCIntfRMI;
import com.glaway.mpm.util.*;
import ext.casc.util.IBAHelper;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import wt.content.*;
import wt.doc.WTDocument;
import wt.fc.*;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.iba.definition.IBADefinitionException;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleState;
import wt.lifecycle.State;
import wt.log4j.LogR;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.session.SessionServerHelper;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.config.LatestConfigSpec;

import java.beans.PropertyVetoException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Iterator;

/**
 * 类功能：根据拍照点编号集返回照片压缩包
 *
 * @author chenjianhui
 * @date 2021/06/07
 */

public class PDMWithMesPhotoCommand implements WebServiceCommand, InitializingBean {
	private static final Logger LOGGER = LogR.getLogger(PDMWithMesPhotoCommand.class.getName());
	// 方法标识
	public static final String METHOD_NAME = "photoTemplate";
	@Override
	public String execute(String jparams) {
		System.out.println("photoTemplate Start============");
		String errorMsg = null;
		String uuid = String.valueOf(System.currentTimeMillis());
		String hostName = "pdm.149.sast.casc";
		try {
			WTProperties prop = WTProperties.getLocalProperties();
			hostName = prop.getProperty("java.rmi.server.hostname");
		} catch (IOException e) {
			e.printStackTrace();
		}
		String url = "http://"+hostName+"/photoTemplate/" + uuid + ".zip";

		System.out.println("PDMWithMesPhotoCommand jparams========="+jparams);
		String[] params = jparams.split("\\(");

		if(params.length>1){
			String filePath = PropertiesUtil.getTempPath() + File.separator + "IXBExpImp" + File.separator +
					"photoTemplate" + File.separator + uuid;
			File fileDir = new File(filePath);
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}

			for (int i = 1; i < params.length; i++) {
				String param = params[i];
				if(param!=null && !"".equals(param) && param.lastIndexOf(")")>-1){
					param = param.substring(0,param.lastIndexOf(")"));
					String[] numberAndVersion = param.split(",");
					String number = numberAndVersion[0];
					String version = numberAndVersion[1];
					System.out.println(number+"~~~~"+version);
					String photoFilePath = filePath + File.separator + number + "_" + version;
					File photoFile = new File(photoFilePath);
					if (!photoFile.exists()) {
						photoFile.mkdirs();
					}
					WTDocument document = null;
					try {
						QuerySpec qs = new QuerySpec(WTDocument.class);
						qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number),new int[] { 0 });
						qs.setAdvancedQueryEnabled(true);
						QueryResult qr = PersistenceHelper.manager.find(qs);
						while (qr.hasMoreElements()) {
							WTDocument temp = (WTDocument) qr.nextElement();
							if (temp.getIterationDisplayIdentifier().toString().equals(version)) {
								document = temp;
							}
						}
						if(document!=null){
							ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
							if (data == null) {
								return null;
							}
							WTDocumentUtil.downloadDocumentPrimaryToTemp(document,photoFilePath + File.separator);
						}
					} catch (Exception e) {
						errorMsg = "编号" + document.getNumber() + "主内容异常 请联系管理员检查数据： " + e.getLocalizedMessage();
						e.printStackTrace();
					}
				}
			}
			try {
				boolean compress = ApacheZipUtil.compress(filePath, filePath + ".zip");
				if(compress){
					FileUtil.deleteFile(filePath);
				}
			} catch (IOException e) {
				errorMsg = "文件打包失败： " + e.getLocalizedMessage();
				LOGGER.error("", e);
			}
		}
		JSONObject rtnMsgObj = new JSONObject();
		try {
			if(errorMsg!=null&&!"".equals(errorMsg)){
				rtnMsgObj.put("status", "N");
				rtnMsgObj.put("result", errorMsg);
			}else{
				rtnMsgObj.put("status", "Y");
				rtnMsgObj.put("result", url);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println("photoTemplate End============");
		return rtnMsgObj.toString();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		WebServiceCommandFactory.register(METHOD_NAME, this);
	}

}
