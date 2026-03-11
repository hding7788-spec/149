package com.glaway.mpm.erp.controller;

import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import wt.content.ApplicationData;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.util.WTException;
import wt.vc.VersionControlHelper;

import com.glaway.mpm.mpmresource.Constants;
import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.ReferenceFactory;
import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.windchill.mpml.command.common.ProcessPlanHelper;

/**
 * @author fly
 *
 */
public class MPMInterfaceController extends MultiActionController {
	public static final String returnErrorMsg = "ERROR";

	/**
	 * 通过零件的编号和版本号 获取工艺规程的zip包的信息（xml的格式） 如果版本号为空，返回最新版本的工艺规程的zip包的信息
	 *
	 * @author fly
	 * @date 2013-5-14
	 * @param request
	 *            number 零件的编号 verison 零件的版本号
	 * @param response
	 *            工艺规程压缩包的名称，oid，
	 *
	 */
	public void getProcessByPartNumber(HttpServletRequest request, HttpServletResponse response) {
//		try {
//			String errorMsg = "error";
//			String xmlString = "";
//			try {
//				String partNumber = request.getParameter("number");
//				String planVersion = request.getParameter("version");
//				ControllerHelper.echoRequstMessage(request);
//				WTPart part = WTPartUtil.getLatestPartByNumberAndView(partNumber, "Planning");
//				if (part == null) {
//					errorMsg = "partNumber don't exist";
//					throw new Exception(errorMsg);
//				}
//				List<WTDocument> list = ProcessPlanHelper.getProcessZipDoc(part, null, Constants.normalProcess);
//				if (list.size() == 0) {
//					errorMsg = "ProcessPlan don't exist";
//					throw new Exception(errorMsg);
//				}
//				WTDocument planDoc = null;
//				WTDocument doc = list.get(0);
//				QueryResult qr = VersionControlHelper.service.allVersionsOf(doc);
//				if (planVersion == null || "".equals(planVersion)) {
//					GLLogger.debug("planVersion==null");
//					planDoc = (WTDocument) qr.nextElement();
//				} else {
//					while (qr.hasMoreElements()) {
//						WTDocument vdoc = (WTDocument) qr.nextElement();
//						GLLogger.debug("vdoc=" + vdoc.getIterationDisplayIdentifier().toString());
//						if (vdoc.getIterationDisplayIdentifier().toString().equals(planVersion)) {
//							planDoc = vdoc;
//							break;
//						}
//					}
//				if(planDoc==null){
//					errorMsg = "ProcessPlan("+planVersion+") don't exist";
//					throw new Exception(errorMsg);
//				}
//				GLLogger.debug("plandoc version=" + planDoc.getIterationDisplayIdentifier().toString());
//
//				}
//				Document xmldoc = XMLHelper.getDefaultDocument();
//				// 根节点
//				Element document = DocumentHelper.createElement("document");
//				document.addAttribute("name", planDoc.getName());
//				document.addAttribute("oid", planDoc.getPersistInfo().getObjectIdentifier().toString());
//				xmldoc.getRootElement().add(document);
//				xmlString = XMLHelper.doucmnetToXMLStringUTF8(xmldoc);
//			} catch (Exception e) {
//				System.out.println("test........");
//				Document doc = XMLHelper.getErrorInfoDocument(errorMsg);
//				xmlString = XMLHelper.doucmnetToXMLStringUTF8(doc);
//				e.printStackTrace();
//			} finally {
//				ControllerHelper.writeTransferData(xmlString, response);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * @author fly
	 * @date 2013-7-11
	 * @param request
	 *            oid:工艺规程文件的oid for example：wt.doc.WTDocumet:124512
	 * @param response
	 *            工艺发布的zip
	 */
	public static void getProcessByProcessDocOid(HttpServletRequest request, HttpServletResponse response) {
//		try {
//			String returnErrorMsg="error";
//			try {
//				ControllerHelper.echoRequstMessage(request);
//				String oid = request.getParameter("oid");
//				if (oid == null) {
//					throw new WTException("oid==null");
//				}
//				TechnicPreview technicPreview = new TechnicPreview();
//				String url = technicPreview.preview(oid);
//				if(url==null){
//					GLLogger.debug("url==null");
//					returnErrorMsg = "get processPlan error";
//					throw new Exception(returnErrorMsg);
//				}
//				File  htmlfile =new File(url);
//				File parentfile=htmlfile.getParentFile();
//				String proceeName=parentfile.getName();
//				WTDocument doc=(WTDocument)ReferenceFactory.getObjectbyOid(oid);
//				String docName=doc.getName();
//				GLLogger.debug("docName="+docName);
//				GLLogger.debug("proceeName="+proceeName);
//				ApacheZipUtil.compress(parentfile,parentfile.getParentFile().getAbsolutePath()+File.separator+docName + ".zip");
//				response.setContentType("application/zip");
//				response.getOutputStream().write(FileUtil.fileToBytes(parentfile.getParentFile().getAbsolutePath()+File.separator+docName + ".zip"));
//				response.setHeader("Content-Disposition", "attachment;filename="+docName+".zip");
//			} catch (Exception e) {
//				e.printStackTrace();
//				ControllerHelper.writeTransferData(returnErrorMsg, response);
//			} finally {
//
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

	}

	/**
	 * 通过文档的oid获取文档主题档案
	 *
	 * @author fly
	 * @date 2013-5-14
	 * @param request
	 *            oid 文档的oid for example：wt.doc.WTDocumet:124512
	 * @param response
	 *
	 */
	public void getDocPrimaryContent(HttpServletRequest request, HttpServletResponse response) {
		try {
			try {
				ControllerHelper.echoRequstMessage(request);
				String oid = request.getParameter("oid");
				if (oid == null) {
					throw new WTException("oid==null");
				}
				WTDocument doc = (WTDocument) ReferenceFactory.getObjectbyOid(oid);
				ApplicationData ap = WTDocumentUtil.getPrimaryByDocument(doc);
				InputStream inputStream = ContentServerHelper.service.findContentStream(ap);
				response.getOutputStream().write(FileUtil.fileToBytes(inputStream));
			} catch (Exception e) {
				e.printStackTrace();
				ControllerHelper.writeTransferData(returnErrorMsg, response);
			} finally {

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 接口描述：用户输入零件的编号和版本号，返回该零件版本所关联的所有变更单，
	 *
	 * @author fly
	 * @date 2013-5-15
	 * @param request
	 *            number String version String
	 * @param response
	 *            xml（主要内容包括，变更的属性，类型和变更单的url）
	 *
	 */
	public void getChangeOrdersByPartNumber(HttpServletRequest request, HttpServletResponse response) {
		try {
			String errorMsg = "error";
			String xmlString = "";
			try {
				String partNumber = request.getParameter("number");
				String partVersion = request.getParameter("version");
				// 1. 工艺变更通知单

				// TODO tfwang
				Document doc = XMLHelper.getDefaultDocument();
				// 根节点

				Element document = DocumentHelper.createElement("document");
				document.addAttribute("name", " 通知单01");
				document.addAttribute("oid", "wt.doc.WTDocument:12145");
				document.addAttribute("type", " changeType01");
				doc.getRootElement().add(document);

				Element document1 = DocumentHelper.createElement("document");
				document1.addAttribute("name", " 通知单02");
				document1.addAttribute("oid", "wt.doc.WTDocument:12146");
				document1.addAttribute("type", " changeType02");
				doc.getRootElement().add(document1);
				xmlString = XMLHelper.doucmnetToXMLString(doc);

			} catch (Exception e) {
				e.printStackTrace();
				Document doc = XMLHelper.getErrorInfoDocument(errorMsg);
				xmlString = XMLHelper.doucmnetToXMLString(doc);
			} finally {
				ControllerHelper.writeTransferData(xmlString, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过单据号获取单据号对应的返工工艺任务的完成情况
	 *
	 * @author fly
	 * @date 2013-5-15
	 * @param orderNumber
	 *            xml（如果返工工艺没有完成，返回不成功标示 如果返工工艺完成，返回成功标示，并返回，完工时间，工艺师的工号和主制单位）
	 */
	public void getReworkProcessTaskByOrderNumber(HttpServletRequest request, HttpServletResponse response) {
//		try {
//			String errorMsg = "error";
//			String xmlString = "";
//			try {
//				ControllerHelper.echoRequstMessage(request);
//				String orderNumber = request.getParameter("orderNumber");
//				GMReworkProcessTask task = TaskUtil.getReworkProcessTaskByOrderNumber(orderNumber);
//				if (task == null) {
//					errorMsg = "task do not exist";
//					throw new Exception("task do not exist");
//				}
//				Document doc = XMLHelper.getDefaultDocument();
//				Element reworkProcessTask = DocumentHelper.createElement("reworkProcessTask");
//				reworkProcessTask.addAttribute("orderNumber", orderNumber);
//				reworkProcessTask.addAttribute("worker", task.getResponsor() == null ? null : task.getResponsor()
//						.getName());
//				if (task.getLifeCycleState().getDisplay().equals("已完成")) {
//					reworkProcessTask.addAttribute("completeTime", task.getCompleteTime().toLocaleString());
//					reworkProcessTask.addAttribute("worker", task.getResponsor() == null ? null : task.getResponsor()
//							.getName());
//				} else {
//					reworkProcessTask.addAttribute("isComplete", "false");
//				}
//				// 根节点
//				doc.getRootElement().add(reworkProcessTask);
//				xmlString = XMLHelper.doucmnetToXMLString(doc);
//			} catch (Exception e) {
//				e.printStackTrace();
//				Document doc = XMLHelper.getErrorInfoDocument(errorMsg);
//				xmlString = XMLHelper.doucmnetToXMLString(doc);
//			} finally {
//				ControllerHelper.writeTransferData(xmlString, response);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	/**
	 *通过零件的图号创建返工工艺任务
	 *
	 * @author fly
	 * @date 2013-5-15
	 * @param request
	 *            partNumber 零件图号 orderNumber 单据号 onBuildNumber 生产令号
	 *
	 * @param response
	 *            是否成功
	 *
	 */
	@SuppressWarnings("deprecation")
	public void createReworkTask(HttpServletRequest request, HttpServletResponse response) {
//		try {
//			String errorMsg = "error";
//			String xmlString = "";
//			try {
//				ControllerHelper.echoRequstMessage(request);
//				String partNumber = request.getParameter("partNumber");
//				String orderNumber = request.getParameter("orderNumber");
//				String onBuildNumber = request.getParameter("onBuildNumber");
//				WTPart part = WTPartUtil.getLatestPartByNumberAndView(partNumber, "Planning");
//				String wholePartNumber = ControllerHelper.getWholePartNumberByPart(part);
//				if (wholePartNumber == null || "".equals(wholePartNumber)) {
//					errorMsg = "whole part number is null";
//					throw new Exception(errorMsg);
//				}
//				Timestamp time = new Timestamp(new Date().getTime() + 4 * 60 * 60 *1000);
//				GMReworkProcessTask reworkTask = GMReworkProcessTask.newGMReworkProcessTask();
//				reworkTask.setName("reworkTask:" + partNumber);
//				// set partNumber
//				reworkTask.setPartNumber(partNumber);
//				// set orderNumber
//				reworkTask.setOrderNumber(orderNumber);
//				// set onBuildNumber
//				reworkTask.setOnBuildNumber(onBuildNumber);
//				// set IsUrgency
//				reworkTask.setIsUrgency(true);
//				// set 整件编号
//				//设置预计提交时间
//				reworkTask.setEstimatedSubmitTime(time);
//				//设置预计批准时间
//				reworkTask.setEstimatedApproveTime(time);
//				reworkTask.setWholePartNumber(wholePartNumber);
//				// set 产品代号
//				reworkTask.setProductNumber(part.getContainerName());
//				// set 计划员
//				WTContainerRef containerRef = part.getContainerReference();
//				// reworkTask.setContainerReference(containerRef);
//				Folder folder = FolderHelper.service.getFolder("Default/06：工艺文件/0611：工艺任务", containerRef);
//				FolderHelper.assignFolder(reworkTask, folder);// 路径
//
//				WTPrincipal planner = Util.getWTContainerOfRole(containerRef.toString(), Constants.GONGYIBUJIHUAYUAN);
//				GLLogger.debug("planner name=" + planner);
//				reworkTask.setPlanner(WTPrincipalReference.newWTPrincipalReference(planner));
//
//				WTPrincipal maindivisher = Util.getWTContainerOfRole(containerRef.toString(),
//						Constants.DESIGNERSYSTEM15);
//				GLLogger.debug("rework mainDivish==>" + maindivisher);
//				reworkTask.setMainDivish(WTPrincipalReference.newWTPrincipalReference(maindivisher));// 工艺主师
//				reworkTask.setTemp01(part.getPersistInfo().getObjectIdentifier().toString());// temp01存放此零件oid
//				PersistenceHelper.manager.save(reworkTask);
//				Document doc = XMLHelper.getDefaultDocument();
//				xmlString = XMLHelper.doucmnetToXMLString(doc);
//			} catch (Exception e) {
//				e.printStackTrace();
//				Document doc = XMLHelper.getErrorInfoDocument(errorMsg);
//				xmlString = XMLHelper.doucmnetToXMLString(doc);
//			} finally {
//				ControllerHelper.writeTransferData(xmlString, response);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

}
