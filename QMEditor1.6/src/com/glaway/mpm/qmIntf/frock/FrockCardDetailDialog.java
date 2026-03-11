package com.glaway.mpm.qmIntf.frock;

import java.awt.Container;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.visual.log.VaLogger;

public class FrockCardDetailDialog extends JPanel {
	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private Map map;
	private VaLogger logger = VaLogger.getLogger(this.getClass());
	private boolean flag;

	/**
	 * @param map
	 * @param flag
	 *            是否显示搜索panel
	 */
	public FrockCardDetailDialog(Map map, boolean flag, NewTechnicsPart frame) {
		logger.debug("input map= " + map);
		this.map = map;
		this.flag = flag;
		newDialog(frame);
	}

	public void newDialog(NewTechnicsPart frame) {
		dialog = new CommonDialog(frame);
		dialog.setTitle("查看工装申请卡");
		if (flag) {
			dialog.setSize(780, 700);
		} else {
			dialog.setSize(780, 870);
		}
		dialog.setResizable(false);
		SwingUtil.setMiddle(dialog);
	}

	public void showDialog() {
		Container container = dialog.getContentPane();
		FrockCardDetailPanel frockDetailPanel = new FrockCardDetailPanel(map,
				flag);
		JScrollPane jScrollPane = new JScrollPane(frockDetailPanel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		container.add(jScrollPane);
		dialog.setVisible(true);
	}
}
