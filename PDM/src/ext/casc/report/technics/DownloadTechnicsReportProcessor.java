package ext.casc.report.technics;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wt.util.WTException;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.netmarkets.model.NmObjectHelper;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class DownloadTechnicsReportProcessor extends DefaultObjectFormProcessor{

	@Override
	public FormResult doOperation(NmCommandBean nmCommandBean, List<ObjectBean> arg1) throws WTException {
		FormResult formResult = new FormResult(FormProcessingStatus.SUCCESS);
		HttpServletRequest request = nmCommandBean.getRequest();
		String actionName = request.getParameter("actionName");
		String oid = request.getParameter("oid");
		String batch = request.getParameter("batch");
		File xls = null;
		try {
			xls = DownloadTechnicsReportHelper.service.export(oid, actionName, batch);
			if(xls != null) {
				URL url = NmObjectHelper.constructOutputURL(xls,xls.getName());
				formResult.setNextAction(FormResultAction.JAVASCRIPT);
				formResult.setJavascript("window.PTC.util.downloadUrl(\"" + url.toExternalForm() + "\");");
//				formResult.setJavascript("window.PTC.util.close()");

			}
		} catch (Exception e) {
			e.printStackTrace();
			return new FormResult(FormProcessingStatus.FAILURE);
		}

//		HttpServletRequest request = nmCommandBean.getRequest();
//		HttpServletResponse response = nmCommandBean.getResponse();
//		response.reset();
//		String actionName = request.getParameter("actionName");
//		String oid = request.getParameter("oid");
//		String type = request.getParameter("type");
//		String batch = request.getParameter("batch");
//		File xls = null;
//			xls = DownloadTechnicsReportHelper.service.export(oid, actionName, batch);
//			InputStream is = null;
//			OutputStream os = null;
//		if(xls != null) {
//			try {
//				is = new BufferedInputStream(new FileInputStream(xls));
//				byte[] buffer = new byte[8192];
//				int length = 0;
//				os = response.getOutputStream();
//				Date date = new Date();
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
//				String fileName=xls.getName();
//				fileName=new String(fileName.getBytes("gbk"),"iso-8859-1");
//				response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
//				response.setContentType("application/vnd.ms-excel");
//				response.setContentLength((int) xls.length());
//				while ((length = is.read(buffer)) >= 0) {
//					os.write(buffer, 0, length);
//				}
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}finally{
//				try {
//					if(is != null){
//						is.close();
//					}
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				try {
//					if(os != null){
//						os.flush();
//					}
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				try {
//					if(os != null){
//						os.close();
//					}
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			os = null;
//		    xls.deleteOnExit();
//		} else {
//
//		}
		return formResult;
	}

}
