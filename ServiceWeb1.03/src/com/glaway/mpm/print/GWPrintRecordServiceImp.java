package com.glaway.mpm.print;

import java.io.File;
import java.util.List;
import java.util.Map;

import wt.content.ContentHolder;
import wt.doc.WTDocument;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.print.data.CmDistributionBean;
import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.print.data.CmPrintQueryBean;
import com.glaway.mpm.print.data.CmProcessFile;
import com.glaway.mpm.print.util.PrintDataQueryUtil;
import com.glaway.mpm.print.util.PrintUtil;

public class GWPrintRecordServiceImp implements GWPrintRecordService {

	private static VaLogger logger = VaLogger.getLogger(GWPrintRecordServiceImp.class.getName());

	@Override
	public String createGwPrintApplyRecord(CmPrintInfoBean cmPrintInfoBean) throws Exception {
		// TODO Auto-generated method stub
		return GWPrintApplyRecordManager.createGwPrintApplyRecord(cmPrintInfoBean);
	}

	@Override
	public WTDocument createProcessPrintDoc(List<CmPrintInfoBean> list) throws Exception {
		// TODO Auto-generated method stub
		return GWPrintApplyRecordManager.createProcessPrintDoc(list);
	}

	@Override
	public File generalPDFFile(List<CmPrintInfoBean> list, long docOid) throws Exception {
		// TODO Auto-generated method stub
		return GWPrintApplyRecordManager.generalPDFFile(list, docOid);
	}

	@Override
	public String uploadPDFFile(ContentHolder holder, File file) throws Exception {
		// TODO Auto-generated method stub
		return GWPrintApplyRecordManager.uploadPDFFile(holder, file);
	}

	@Override
	public String createGwPrintDistributeRecord(CmPrintInfoBean cmPrintInfoBean) throws Exception {
		// TODO Auto-generated method stub
		return GWPrintDistributeRecordManager.createGwPrintDistributeRecord(cmPrintInfoBean);
	}

	@Override
	public String updateGwPrintApplyRecord(CmPrintInfoBean cmPrintInfoBean)
			throws Exception {
		// TODO Auto-generated method stub
		return GWPrintApplyRecordManager.updateGwPrintApplyRecord(cmPrintInfoBean);
	}

	/**
	 * 保存以及启动打印分发流程 add by zhuhao
	 */
	@Override
	public String saveGwPrintApplyRecord(List<CmPrintInfoBean> list,String pboOid) throws Exception {
		// TODO Auto-generated method stub
		return GWPrintApplyRecordManager.saveGwPrintApplyRecord(list,pboOid);
	}

	@Override
	public String updateGwPrintDistributeRecord(CmPrintInfoBean cmPrintInfoBean)
			throws Exception {
		// TODO Auto-generated method stub
		return GWPrintDistributeRecordManager.updateGwPrintDistributeRecord(cmPrintInfoBean);
	}

	@Override
	public List<CmPrintInfoBean> queryMpmProcessPlanPrintFiles(
			CmPrintQueryBean cmPrintQueryBean) throws Exception {
		// TODO Auto-generated method stub
		return PrintDataQueryUtil.queryMpmProcessPlanPrintFiles(cmPrintQueryBean);
	}

	@Override
	public List<CmPrintInfoBean> queryWTDocumentPrintFiles(
			CmPrintQueryBean cmPrintQueryBean) throws Exception {
		// TODO Auto-generated method stub
//		return PrintDataQueryUtil.queryWTDocumentPrintFiles(cmPrintQueryBean);
		return null;
	}

	@Override
	public List<CmPrintInfoBean> queryWTChangeOrder2PrintFiles(
			CmPrintQueryBean cmPrintQueryBean) throws Exception {
		// TODO Auto-generated method stub
//		return PrintDataQueryUtil.queryWTChangeOrder2PrintFiles(cmPrintQueryBean);
		return null;
	}

	@Override
	public List<CmPrintInfoBean> queryPrintFiles(
			CmPrintQueryBean cmPrintQueryBean) throws Exception {
		// TODO Auto-generated method stub
		return PrintDataQueryUtil.queryPrintFiles(cmPrintQueryBean);
	}

	@Override
	public List<CmPrintInfoBean> queryPrintApplicationFiles(
			CmPrintQueryBean cmPrintQueryBean) throws Exception {
		// TODO Auto-generated method stub
		return PrintDataQueryUtil.queryPrintApplicationFiles(cmPrintQueryBean);
	}

	@Override
	public List<CmPrintInfoBean> queryFilesOnBom(
			CmPrintQueryBean cmPrintQueryBean) throws Exception {
		// TODO Auto-generated method stub
		return PrintDataQueryUtil.queryFilesOnBom(cmPrintQueryBean);
	}

	@Override
	public List<CmPrintInfoBean> queryBomFiles(
			CmPrintInfoBean cmPrintInfoBean) throws Exception {
		// TODO Auto-generated method stub
		return PrintDataQueryUtil.queryBomFiles(cmPrintInfoBean);
	}

	@Override
	public Map<String, String> queryWTUserInfo(String userName)
			throws Exception {
		// TODO Auto-generated method stub
		return PrintUtil.queryWTUserInfo(userName);
	}

	@Override
	public List<CmPrintInfoBean> printFilesMgt(CmPrintQueryBean cmPrintQueryBean, boolean isZxdy)
			throws Exception {
		// TODO Auto-generated method stub
		return PrintDataQueryUtil.printFilesMgt(cmPrintQueryBean, isZxdy);
	}

	@Override
	public List<CmPrintInfoBean> ylqFileAddQuery(
			CmPrintQueryBean cmPrintQueryBean) throws Exception {
		// TODO Auto-generated method stub
		return PrintDataQueryUtil.ylqFileAddQuery(cmPrintQueryBean);
	}

	@Override
	public WTDocument createProcessPrintRecoverDoc(List<CmPrintInfoBean> list)
			throws Exception {
		return GWPrintRecoverRecordManager.createProcessPrintRecoverDoc(list);
	}

	@Override
	public String createGwPrintRecoverRecord(CmPrintInfoBean cmPrintInfoBean)
			throws Exception {
		return GWPrintRecoverRecordManager.createGWPrintRecoverRecord(cmPrintInfoBean);
	}

	@Override
	public List<CmPrintInfoBean> queryFileOnProcessDirectory(
			CmPrintQueryBean cmPrintQueryBean) throws Exception {
		// TODO Auto-generated method stub
		return PrintDataQueryUtil.queryFileOnProcessDirectory(cmPrintQueryBean);
	}

	@Override
	public List<CmPrintInfoBean> queryProcessFiles(CmPrintInfoBean cmPrintInfoBean) throws Exception {
		// TODO Auto-generated method stub
		return PrintDataQueryUtil.queryProcessFiles(cmPrintInfoBean);
	}

	@Override
	public List<CmPrintInfoBean> queryReceiveData(String userName, String oid, String category) throws Exception {
		// TODO Auto-generated method stub
		return PrintDataQueryUtil.queryReceiveData(userName, oid, category);
	}

	@Override
	public String startPrintOffSet(List<CmPrintInfoBean> list) throws Exception {
		return GWPrintApplyRecordManager.startPrintOffSet(list);
	}

	@Override
	public List<String> getAllChildPartOid(CmPrintInfoBean cmPrintInfoBean)
			throws Exception {
		// TODO Auto-generated method stub
		return PrintDataQueryUtil.getAllChildPartOid(cmPrintInfoBean);
	}

	@Override
	public List<CmPrintInfoBean> queryBomFilesByPart(String partOid,
			String mainTechnics) throws Exception {
		// TODO Auto-generated method stub
		return PrintDataQueryUtil.queryBomFilesByPart(partOid, mainTechnics);
	}

}