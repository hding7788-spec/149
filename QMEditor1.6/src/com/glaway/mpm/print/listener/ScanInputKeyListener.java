package com.glaway.mpm.print.listener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTable;

//import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.data.CmDistributionBean;
import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.print.helper.MPMPrintProcessor;
import com.glaway.mpm.print.ui.FileListTable;
import com.glaway.mpm.print.ui.FilePrintMainPanel;
import com.glaway.mpm.print.ui.MPMPrintFileFrame;
import com.glaway.mpm.print.ui.RecipientsInfoPanel;
import com.glaway.mpm.print.util.LocalPrintUtil;
import com.glaway.mpm.util.CommonUtil;

public class ScanInputKeyListener extends KeyAdapter {

//	private static final VaLogger logger = VaLogger.getLogger(ScanInputKeyListener.class.getName());

	private String scanInput = "";

	@Override
	public void keyPressed(KeyEvent e) {
		String category = MPMPrintFileFrame.getCategory();
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			FileListTable panel = MPMPrintFileFrame.getFileMainPanel().getFileTable();
			JTable table = panel.getTable();
			String type = panel.getType();
			boolean flag = LocalPrintUtil.checkStr(scanInput);//true = 扫文件 ;false = 扫工牌
			Map<String, String> map = new HashMap<String, String>();
			if(flag){
				map = LocalPrintUtil.buildFileMap(scanInput);
			}
			if(PrintConstants.TITLE_MAINPANEL_LQZZWJ.equals(type)){
				if(flag){//扫文件
					String QRCode = map.get("DABH");
					if(QRCode == null || "".equals(QRCode)){
						return;
					}
					for (int row = 0; row < table.getRowCount(); row++) {
						CmPrintInfoBean cmPrintInfoBean = (CmPrintInfoBean) table.getValueAt(row, table.getColumnCount() - 1);
						if(QRCode.equals(cmPrintInfoBean.getQrName())){
							boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 1)));
							if(isSelect){
								table.setValueAt(false, row, 1);
							}else{
								table.setValueAt(true, row, 1);
							}
						}
					}
				}else{//扫工牌
					String userName = MPMPrintProcessor.getUserNameBySign(scanInput);
					FilePrintMainPanel parentPanel = (FilePrintMainPanel) panel.getComponent();
					RecipientsInfoPanel recipientsInfoPanel = parentPanel.getRecipientsInfoPanel();
					String oid = MPMPrintFileFrame.getOid();
					CmDistributionBean newCmDistributionBean = MPMPrintProcessor.getReceiveMessage(userName);
					List<CmPrintInfoBean> list = MPMPrintProcessor.getReceiveData(userName, oid, category);
					recipientsInfoPanel.setValue(newCmDistributionBean);
					panel.setUIValues(list);
				}
			}
			scanInput = "";
		} else {
			String str =CommonUtil.objectToString(e.getKeyChar());
			String regEx="[`~!@#$%^&*()+-=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
			Pattern p=Pattern.compile(regEx);
			Matcher m=p.matcher(str);
			if(str.matches(".*[a-zA-z].*") || str.matches("^[0-9]*$") || m.find()){
				scanInput = scanInput + str;
			}
		}
	}
}
