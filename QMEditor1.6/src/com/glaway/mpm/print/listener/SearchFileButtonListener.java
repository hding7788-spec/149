package com.glaway.mpm.print.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.print.data.CmPrintQueryBean;
import com.glaway.mpm.print.helper.MPMPrintHelper;
import com.glaway.mpm.print.helper.MPMPrintProcessor;
import com.glaway.mpm.print.ui.DestroyFileConditionPanel;
import com.glaway.mpm.print.ui.FileListTable;
import com.glaway.mpm.print.ui.MPMPrintFileFrame;
import com.glaway.mpm.print.ui.SearchFileConditionPanel;
import com.glaway.mpm.print.ui.SearchFileOnBomConditionPanel;
import com.glaway.mpm.print.ui.SearchFileOnProcessDirectoryConditionPanel;
import com.glaway.mpm.print.ui.SearchOutFilePanel;
import com.glaway.mpm.print.ui.SearchPrintApplicationPanel;
import com.glaway.mpm.print.ui.SearchPrintInfoPanel;
import com.glaway.mpm.print.ui.SearchSealPlusPanel;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;

public class SearchFileButtonListener implements ActionListener{
	private static final VaLogger logger = VaLogger.getLogger(SearchFileButtonListener.class.getClass());
	private JPanel panel;
	private JFrame frame;

	public SearchFileButtonListener(JPanel panel, JFrame frame){
		this.panel = panel;
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent actionevent) {
		// TODO Auto-generated method stub
		Object obj = actionevent.getSource();
		if(panel instanceof SearchFileConditionPanel){
			SearchFileConditionPanel searchFileConditionPanel = (SearchFileConditionPanel)panel;
			if(obj == searchFileConditionPanel.getSearchButton()){
				//查询
				CmPrintQueryBean cmPrintQueryBean = searchFileConditionPanel.getConditionValues();
				//对version进行校验
				boolean versionFormat = MPMPrintHelper.verifyVersionDataFormat(cmPrintQueryBean.getVersion());
				if(!versionFormat){
					CommonUIUtil.showMessageDialog(null, "版本填写格式错误，请重新填写！");
					return;
				}
//				String[] fileTypes = LoadPrintConfigurations.getInstance().getQTBGValue();
				String fileType = cmPrintQueryBean.getFileType().trim();
				//如果文件类型为其他报告，则文件名称和文件编号不能全部为空
				if(fileType!=null
						&&fileType.equals(PrintConstants.FILETYPE_QTBG)){
					if((cmPrintQueryBean.getFileName()==null
							||cmPrintQueryBean.getFileName().trim().equals(""))
							&&(cmPrintQueryBean.getFileNumber()==null
							||cmPrintQueryBean.getFileNumber().trim().equals(""))){
						CommonUIUtil.showMessageDialog(null, "请输入‘文件编号’或‘文件名称’！二者至少填写一个");
						return;
					}
				}
				long t1 = System.currentTimeMillis();
				List<CmPrintInfoBean> printInfoBeanList = MPMPrintHelper.addPrintFiles(cmPrintQueryBean);
				long t2 = System.currentTimeMillis();
				logger.debug("查询耗时：" + (t2 - t1));
				searchFileConditionPanel.getFileListTable().setUIValues(printInfoBeanList);
			}else if(obj == searchFileConditionPanel.getClearConditionButton()){
				//清空查询条件
				searchFileConditionPanel.clearCondition();
			}
		}else if(panel instanceof DestroyFileConditionPanel){
			DestroyFileConditionPanel destroyFileConditionPanel = (DestroyFileConditionPanel)panel;
			if(obj == destroyFileConditionPanel.getSearchButton()){
				//查询
				CmPrintQueryBean cmPrintQueryBean = destroyFileConditionPanel.getConditionValues();
				List<CmPrintInfoBean> printInfoBeanList = MPMPrintHelper.ylqFileAddQuery(cmPrintQueryBean);
				destroyFileConditionPanel.getFileListTable().setUIValues(printInfoBeanList);
			}else if(obj == destroyFileConditionPanel.getClearConditionButton()){
				//清空查询条件
				destroyFileConditionPanel.clearCondition();
			}
		}else if(panel instanceof SearchPrintApplicationPanel){
			final SearchPrintApplicationPanel searchPrintApplicationPanel = (SearchPrintApplicationPanel)panel;
			final String category = MPMPrintFileFrame.getCategory();
//			String category = searchPrintApplicationPanel.getCategory();
			if(obj == searchPrintApplicationPanel.getSearchButton()){
				//查询
				final CmPrintQueryBean cmPrintQueryBean = searchPrintApplicationPanel.getConditionValues();
				//对内容进行校验
				if("".equals(cmPrintQueryBean.getFileNumber())&&"".equals(cmPrintQueryBean.getFileName())
						&&"".equals(cmPrintQueryBean.getVersion())&&"".equals(cmPrintQueryBean.getPhaseCode())){
					CommonUIUtil.showMessageDialog(null, "至少输入一项搜索条件！");
					return;
				}
				//对version进行校验
				boolean versionFormat = MPMPrintHelper.verifyVersionDataFormat(cmPrintQueryBean.getVersion());
				if(!versionFormat){
					CommonUIUtil.showMessageDialog(null, "版本填写格式错误，请重新填写！");
					return;
				}
				final VaActionProgressBar progressBar = new VaActionProgressBar(
						frame, "搜索", "正在搜索,请等待...", "搜索中");
		        Thread thread = new Thread() {
		        	public void run(){
		        		long t1 = System.currentTimeMillis();
						List<CmPrintInfoBean> printInfoBeanList = null;
						if(!"".equals(category) && category != null){
							if("WL".equals(category)){//查询外来文件
								cmPrintQueryBean.setOutDept("NOTNULL");
							}else if("ZZ".equals(category)){//查询纸质文件
								cmPrintQueryBean.setOutDept("null");
							}
							printInfoBeanList = MPMPrintProcessor.searchOutFile(cmPrintQueryBean);
						}else{
							printInfoBeanList = MPMPrintProcessor.addPrintApplicationFiles(cmPrintQueryBean);
						}
						long t2 = System.currentTimeMillis();
						logger.debug("查询耗时：" + (t2 - t1));
						searchPrintApplicationPanel.getFileListTable().setUIValues(printInfoBeanList);
						progressBar.finish();
		                progressBar.setVisible(false);
		        	}
		        };
		        thread.start();
		        progressBar.setVisible(true);
			} else if(obj == searchPrintApplicationPanel.getClearConditionButton()){
				searchPrintApplicationPanel.clearCondition();
			}
		}else if(panel instanceof SearchFileOnBomConditionPanel){
			final SearchFileOnBomConditionPanel searchFileOnBomConditionPanel = (SearchFileOnBomConditionPanel)panel;
			if(obj == searchFileOnBomConditionPanel.getSearchButton()){
				final CmPrintQueryBean cmPrintQueryBean = searchFileOnBomConditionPanel.getConditionValues();
				//对内容进行校验
				if("".equals(CommonUtil.objectToString(cmPrintQueryBean.getPartNumber()))
						&& "".equals(CommonUtil.objectToString(cmPrintQueryBean.getPartName()))){
					CommonUIUtil.showMessageDialog(null, "至少输入一项搜索条件！");
					return;
				}
				final VaActionProgressBar progressBar = new VaActionProgressBar(
						frame, "搜索", "正在搜索,请等待...", "搜索中");
		        Thread thread = new Thread() {
		        	public void run(){
		        		long t1 = System.currentTimeMillis();
		        		List<CmPrintInfoBean> printInfoBeanList = MPMPrintHelper.addFilesOnBom(cmPrintQueryBean);
						long t2 = System.currentTimeMillis();
						logger.debug("查询耗时：" + (t2 - t1));
						searchFileOnBomConditionPanel.getFileListTable().setUIValues(printInfoBeanList);
						progressBar.finish();
		                progressBar.setVisible(false);
		        	}
		        };
		        thread.start();
		        progressBar.setVisible(true);
			}else if(obj == searchFileOnBomConditionPanel.getClearConditionButton()){
				searchFileOnBomConditionPanel.clearCondition();
			}
		}else if(panel instanceof SearchSealPlusPanel){
			SearchSealPlusPanel searchSealPlusPanel = (SearchSealPlusPanel) panel;
			String category = MPMPrintFileFrame.getCategory();
			if(obj == searchSealPlusPanel.getSearchButton()){
				CmPrintQueryBean cmPrintQueryBean = searchSealPlusPanel.getConditionValues();
				//对version进行校验
				boolean versionFormat = MPMPrintHelper.verifyVersionDataFormat(cmPrintQueryBean.getVersion());
				if(!versionFormat){
					CommonUIUtil.showMessageDialog(null, "版本填写格式错误，请重新填写！");
					return;
				}
				long t1 = System.currentTimeMillis();
				List<CmPrintInfoBean> printInfoBeanList = MPMPrintProcessor.searchSealPlus(cmPrintQueryBean, category);
				long t2 = System.currentTimeMillis();
				logger.debug("查询耗时：" + (t2 - t1));
				searchSealPlusPanel.getSearchFileTable().setUIValues(printInfoBeanList);
			}else if(obj == searchSealPlusPanel.getClearConditionButton()){
				searchSealPlusPanel.clearCondition();
			}
		}else if(panel instanceof SearchFileOnProcessDirectoryConditionPanel){
			final SearchFileOnProcessDirectoryConditionPanel searchFileOnPDPanel = (SearchFileOnProcessDirectoryConditionPanel) panel;
			if(obj == searchFileOnPDPanel.getSearchButton()){
				final CmPrintQueryBean cmPrintQueryBean = searchFileOnPDPanel.getConditionValues();
				//对version进行校验
				boolean versionFormat = MPMPrintHelper.verifyVersionDataFormat(cmPrintQueryBean.getVersion());
				if(!versionFormat){
					CommonUIUtil.showMessageDialog(null, "版本填写格式错误，请重新填写！");
					return;
				}
				if("".equals(cmPrintQueryBean.getFileNumber())&&"".equals(cmPrintQueryBean.getPhaseCode())
						&&"".equals(cmPrintQueryBean.getVersion())){
					CommonUIUtil.showMessageDialog(null, "至少输入一项搜索条件！");
					return;
				}
				final VaActionProgressBar progressBar = new VaActionProgressBar(
						frame, "搜索", "正在搜索,请等待...", "搜索中");
		        Thread thread = new Thread() {
		        	public void run(){
		        		long t1 = System.currentTimeMillis();
						List<CmPrintInfoBean> printInfoBeanList = MPMPrintProcessor.addFileOnProcessDirectory(cmPrintQueryBean);
						long t2 = System.currentTimeMillis();
						logger.debug("查询耗时：" + (t2 - t1));
						searchFileOnPDPanel.getFileListTable().setUIValues(printInfoBeanList);
						progressBar.finish();
		                progressBar.setVisible(false);
		        	}
		        };
		        thread.start();
		        progressBar.setVisible(true);
			}else if(obj == searchFileOnPDPanel.getClearConditionButton()){
				searchFileOnPDPanel.clearCondition();
			}
		}else if(panel instanceof SearchOutFilePanel){
			SearchOutFilePanel searchOutFilePanel = (SearchOutFilePanel) panel;
			if(obj == searchOutFilePanel.getSearchButton()){
				CmPrintQueryBean cmPrintQueryBean = searchOutFilePanel.getConditionValues();
				//对version进行校验
				boolean versionFormat = MPMPrintHelper.verifyAllVersionDataFormat(cmPrintQueryBean.getVersion());
				if(!versionFormat){
					CommonUIUtil.showMessageDialog(null, "版本填写格式错误，请重新填写！");
					return;
				}
				String category = MPMPrintFileFrame.getCategory();
				if("ZZ".equals(category)){//查询纸质文件
					cmPrintQueryBean.setOutDept("null");
				}
				List<CmPrintInfoBean> printInfoBeanList = MPMPrintProcessor.searchOutFile(cmPrintQueryBean);
				FileListTable fileListTable = searchOutFilePanel.getFileListTable();
				fileListTable.setUIValues(printInfoBeanList);
			}else if(obj == searchOutFilePanel.getClearConditionButton()){
				searchOutFilePanel.clearCondition();
			}
		}else if(panel instanceof SearchPrintInfoPanel){
			final SearchPrintInfoPanel searchPrintInfoPanel = (SearchPrintInfoPanel) panel;
			if(obj == searchPrintInfoPanel.getSearchButton()){
				final CmPrintQueryBean cmPrintQueryBean = searchPrintInfoPanel.getConditionValues();
				if("".equals(cmPrintQueryBean.getFileNumber()) && "".equals(cmPrintQueryBean.getFileName())){
					CommonUIUtil.showMessageDialog(null, "至少输入一项搜索条件！");
					return;
				}
				final VaActionProgressBar progressBar = new VaActionProgressBar(
						frame, "搜索", "正在搜索,请等待...", "搜索中");
		        Thread thread = new Thread() {
		        	public void run(){
		        		List<CmPrintInfoBean> printInfoBeanList = MPMPrintProcessor.searchPrintInfo(cmPrintQueryBean);
						FileListTable fileListTable = searchPrintInfoPanel.getFileListTable();
						fileListTable.setUIValues(printInfoBeanList);
						progressBar.finish();
		                progressBar.setVisible(false);
		        	}
		        };
		        thread.start();
		        progressBar.setVisible(true);
			}else if(obj == searchPrintInfoPanel.getClearConditionButton()){
				searchPrintInfoPanel.clearCondition();
			}
		}

	}

}
