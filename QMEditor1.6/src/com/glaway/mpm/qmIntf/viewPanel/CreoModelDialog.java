package com.glaway.mpm.qmIntf.viewPanel;

import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;



import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.qmIntf.participatePart.VaClipboard;
import com.glaway.mpm.resource.Constants;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.wcIntf.ImageIntf;

public class CreoModelDialog {
	private static VaLogger logger = VaLogger.getLogger();
		private Map<String, String> map;
	private JDialog dialog;
	private JFrame frame;

	public CreoModelDialog(Map<String, String> map, JFrame frame) {
		this.map = map;
		this.frame = frame;
		logger.debug("map==========" + map);
		newDialog();
	}

	public void newDialog() {
		dialog = new CommonDialog(frame);
		dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int selection = SwingUtil.showConfirmDialog(
						"关闭后数据将不会被保存，如想保存请点击确定按钮，是否关闭？", Constants.TIP, 2);
				logger.debug(selection);
				if (selection == 0) {
					VaClipboard.clipboardCmpTreeNodes.clear();
					dialog.dispose();
				}
			}
		});

		dialog.setTitle("添加PDS中间模型");
		dialog.setSize(800, 700);
		dialog.setResizable(true);
		SwingUtil.setMiddle(dialog);
	}

	public Map<String, Vector<List<String>>> showDialog() {
		Container container = dialog.getContentPane();
		List<List<Object>> mapData = ImageIntf.getCADName(map);
		logger.debug("mapData==========" + mapData);
		if (mapData == null || mapData.size() == 0) {
			SwingUtil.showMessageDialog("零件的简图不存在", "提示", 2);
			return null;
		}
		container.add(new CreoModelPanel(map, dialog, mapData));
		dialog.setVisible(true);
		Map<String, Vector<List<String>>> map = CreoModelPanel.stepsInforMap;
		CreoModelPanel.stepsInforMap = null;
		return map;
	}
}