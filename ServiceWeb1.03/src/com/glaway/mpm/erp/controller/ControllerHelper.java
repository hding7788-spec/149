package com.glaway.mpm.erp.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wt.doc.WTDocument;
import wt.part.WTPart;
import wt.util.WTException;

import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.LoadConfig;
import com.glaway.mpm.util.ProcessPlanUtil;
import com.glaway.mpm.util.PropertiesConfigs;
import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;

public class ControllerHelper {
	private static PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesConfigs.QIAN_LONG_CONFIG_PATH);

	/**
	 * echo request message
	 *
	 * @author fly
	 * @date 2013-5-14
	 * @param request
	 *
	 */
	public static void echoRequstMessage(HttpServletRequest request) {
		if (GLLogger.isDebugEnabled()) {
			String uri = request.getRequestURI();
			Map params = request.getParameterMap();
			String message = "";
			for (Object p : params.keySet()) {
				Object value = params.get(p);
				if (value instanceof String) {
					message += p + "=" + value + "&";
				} else {
					if (value instanceof String[]) {
						String[] vs = (String[]) value;
						for (int i = 0; i < vs.length; i++) {
							message += p + "=" + vs[i] + "&";
						}
					}
				}
			}
			GLLogger.debug("Request " + request.getRemoteHost() + "" + uri + "?" + message);
		}
	}

	/**
	 * set return messages
	 *
	 * @author fly
	 * @date 2013-5-14
	 * @param returnMessages
	 * @param response
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 *
	 */
	public static void writeTransferData(String returnMessages, HttpServletResponse response)
			throws UnsupportedEncodingException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.getOutputStream().write(returnMessages.getBytes("UTF-8"));
	}

	/**
	 * 获取零件的说明文档，即工艺规程的zip包
	 * @author fly
	 * @date  2013-5-14
	 * @param partNumber
	 * @param version
	 * @return
	 * @throws WTException
	 *
	 */
	public static WTDocument getProcessDocByPart(String partNumber, String version) throws WTException {
		WTPart part=WTPartUtil.getPartByNumberAndVersion(partNumber, LoadConfig.getInstance().getPbomView(), version);
		if(part==null){
			return null;
		}
		GLLogger.debug("part number="+part.getNumber());
		String documentType = propertiesUtil.getProperty("process-zip-document-type");
		List<WTDocument> docs=	WTPartUtil.getDescribedDocumentByPart(part,documentType);
		return docs.get(0);
	}

	/**
	 * 通过零件的编号 获取该零件对应的整件的编号
	 * @author fly
	 * @date  2013-5-16
	 * @param partNumber
	 * @return
	 * @throws WTException
	 *
	 */
	public static String getWholePartNumberByPart(WTPart part) throws WTException{
		String wholePartNumber=null;
	// 通过part 找到对应的工艺规程
		List<MPMProcessPlan> pps=ProcessPlanUtil.getProcessPlanByPart(part);
		for(MPMProcessPlan pp:pps){
			IBAHelper iba=new IBAHelper(pp);
			 wholePartNumber =iba.getIBAValue("parentPartNumber");
			 GLLogger.debug("wholePartNumber="+wholePartNumber);
			return wholePartNumber;
		}
		return null;
	}



}
