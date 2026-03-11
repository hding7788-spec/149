package com.glaway.mpm.print.listener;

import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;

import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.print.helper.MPMPrintProcessor;
import com.glaway.mpm.print.service.PrintToWCIntf;
import com.glaway.mpm.print.ui.AddFileOnBomDialog;
import com.glaway.mpm.print.ui.FileListTable;
import com.glaway.mpm.print.ui.FilePrintMainPanel;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;

public class FileListTableMouseListener extends MouseAdapter {

	private JPanel panel;
	private String type;
	private JTable table;
	private Container component;
	private String sopPartType = "casc.sast.149.SOPPart";
//	private FileListTablePopupMenu popup;

	public FileListTableMouseListener (JTable table, String type, FileListTable fileListTable, Container component) {
		this.table = table;
		this.type = type;
		this.panel = fileListTable;
//		popup = new FileListTablePopupMenu(table, type);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			int row = table.rowAtPoint(e.getPoint());
			table.getSelectionModel().setSelectionInterval(row, row);
//			int selectRow = table.getSelectedRow();
//				popup.setStatus();
//				popup.show(table, e.getX(), e.getY());
		} else if (e.getClickCount() == 2 && type.equals(PrintConstants.TITLE_ADDONBOM_PART)) {
			JFrame frame = (JFrame) component;
			final FileListTable panel = (FileListTable)this.panel;
			final int row = table.rowAtPoint(e.getPoint());
			String hasFound = (String) table.getValueAt(row, 6);
			String partType = "";
			try {
				partType = PrintToWCIntf.getPartType((String) table.getValueAt(row, 2));
			} catch (RemoteException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			}
			if ((hasFound == null || "".equals(hasFound) || "无".equals(hasFound)) & !partType.contains(sopPartType)) {
				CommonUIUtil.showMessageDialog(null, "该零件下没有工艺文件目录");
				return ;
			}
			final VaActionProgressBar progressBar = new VaActionProgressBar(
					frame, "搜索", "正在搜索,请等待...", "搜索中");
	        Thread thread = new Thread() {
	        	public void run(){
	        		CmPrintInfoBean cmPrintInfoBean = (CmPrintInfoBean) table.getValueAt(row, table.getColumnCount() - 1);
	        		String mainTechnics = cmPrintInfoBean.getMainTechnics();
	        		List<String> allPartOidList = MPMPrintProcessor.getAllChildPartOid(cmPrintInfoBean);
	        		System.out.println("allPartOidList-size="+allPartOidList.size());
	        		List<CmPrintInfoBean> printInfoBeanList = new ArrayList<CmPrintInfoBean>();
	        		if(allPartOidList != null && allPartOidList.size() > 0) {
	        			for (String partOid : allPartOidList) {
	        				System.out.println("partOid="+partOid);
	        				List<CmPrintInfoBean> list = MPMPrintProcessor.queryBomFilesByPart(partOid, mainTechnics);
	        				System.out.println("list-size="+list.size());
	        				printInfoBeanList.addAll(list);
						}
	        		}
	        		
//	    			List<CmPrintInfoBean> printInfoBeanList = MPMPrintProcessor.addBomFiles(cmPrintInfoBean);
	    			AddFileOnBomDialog addFileOnBomDialog = (AddFileOnBomDialog)panel.getComponent();
	    			addFileOnBomDialog.getFileListTable2().setUIValues(printInfoBeanList);
	    			progressBar.finish();
	                progressBar.setVisible(false);
	        	}
	        };
	        thread.start();
	        progressBar.setVisible(true);
		} else if (e.getClickCount() == 2 && type.equals(PrintConstants.TITLE_MAINPANEL_JGYZGL_RELATE)){
			FileListTable panel = (FileListTable)this.panel;
			int row = table.rowAtPoint(e.getPoint());
			ArrayList<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
			CmPrintInfoBean cmPrintInfoBean = (CmPrintInfoBean) table.getValueAt(row, table.getColumnCount() - 1);
			if(cmPrintInfoBean.getFileState().equals("未打印") || cmPrintInfoBean.getFileState().equals("已遗失")){
				CommonUIUtil.showMessageDialog(null, "不可以添加状态为“未打印”或者“已遗失”的文档");
				return ;
			}
			list.add(cmPrintInfoBean);
			FilePrintMainPanel filePrintMainPanel = (FilePrintMainPanel)panel.getComponent();
			filePrintMainPanel.getFileTable().setAddValues(list);
		}
	}


}
