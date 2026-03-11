package com.glaway.mpm.print;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.glaway.mpm.print.data.CmDistributionBean;
import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.print.data.CmPrintQueryBean;
import com.glaway.mpm.print.data.CmProcessFile;

import wt.content.ContentHolder;
import wt.doc.WTDocument;

public interface GWPrintRecordService {

	//创建打印申请记录
	public String createGwPrintApplyRecord(CmPrintInfoBean cmPrintInfoBean) throws Exception;

	//创建打印分发记录
	public String createGwPrintDistributeRecord(CmPrintInfoBean cmPrintInfoBean) throws Exception;

	//创建文件打印单
	public WTDocument createProcessPrintDoc(List<CmPrintInfoBean> list) throws Exception;

	//重新生成PDF，并打包
	public File generalPDFFile(List<CmPrintInfoBean> list, long docOid) throws Exception;

	//上传PDF文件包至文件打印单附件
	public String uploadPDFFile(ContentHolder holder, File file) throws Exception;

	//更新打印申请记录
	public String updateGwPrintApplyRecord(CmPrintInfoBean cmPrintInfoBean) throws Exception;

	//149保存打印申请
	public String saveGwPrintApplyRecord(List<CmPrintInfoBean> list, String pboOid) throws Exception;

	//更新打印申请记录
	public String updateGwPrintDistributeRecord(CmPrintInfoBean cmPrintInfoBean) throws Exception;

	//查询打印文件总体接口
	public List<CmPrintInfoBean> queryPrintFiles(CmPrintQueryBean cmPrintQueryBean) throws Exception;

	//查询打印申请文件
	public List<CmPrintInfoBean> queryPrintApplicationFiles(CmPrintQueryBean cmPrintQueryBean) throws Exception;

	//基于BOM查询打印申请文件
	public List<CmPrintInfoBean> queryFilesOnBom(CmPrintQueryBean cmPrintQueryBean) throws Exception;

	//基于BOM查询打印申请文件
	public List<CmPrintInfoBean> queryBomFiles(CmPrintInfoBean cmPrintInfoBean) throws Exception;
	
	public List<String> getAllChildPartOid(CmPrintInfoBean cmPrintInfoBean) throws Exception;
	
	public List<CmPrintInfoBean> queryBomFilesByPart(String partOid, String mainTechnics) throws Exception;

	//查询工艺计划类型打印文件
	public List<CmPrintInfoBean> queryMpmProcessPlanPrintFiles(CmPrintQueryBean cmPrintQueryBean) throws Exception;

	//查询文档类型打印文件
	public List<CmPrintInfoBean> queryWTDocumentPrintFiles(CmPrintQueryBean cmPrintQueryBean) throws Exception;

	//查询更改单类型打印文件
	public List<CmPrintInfoBean> queryWTChangeOrder2PrintFiles(CmPrintQueryBean cmPrintQueryBean) throws Exception;

	//查询人员信息
	public Map<String, String> queryWTUserInfo(String userName) throws Exception;

	//打印文件统一/自行管理查询接口
	public List<CmPrintInfoBean> printFilesMgt(CmPrintQueryBean cmPrintQueryBean, boolean isZxdy) throws Exception;

	//已领取文件添加查询接口
	public List<CmPrintInfoBean> ylqFileAddQuery(CmPrintQueryBean cmPrintQueryBean) throws Exception;

	//创建文件退回申请单
	public WTDocument createProcessPrintRecoverDoc(List<CmPrintInfoBean> list) throws Exception;

	//创建文件退回记录
	public String createGwPrintRecoverRecord(CmPrintInfoBean cmPrintInfoBean) throws Exception;

	//查询工艺文件目录
	public List<CmPrintInfoBean> queryFileOnProcessDirectory(CmPrintQueryBean cmPrintQueryBean) throws Exception;

	//通过工艺文件目录查询工艺文件
	public List<CmPrintInfoBean> queryProcessFiles(CmPrintInfoBean cmPrintInfoBean) throws Exception;

	//根据人物信息查询对应分发信息
	public List<CmPrintInfoBean> queryReceiveData(String userName, String oid, String category) throws Exception;

	//启动补打流程存储补打信息
	public String startPrintOffSet(List<CmPrintInfoBean> list) throws Exception;
}
