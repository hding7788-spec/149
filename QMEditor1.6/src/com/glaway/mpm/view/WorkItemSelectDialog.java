package com.glaway.mpm.view;

import java.awt.Container;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;

public class WorkItemSelectDialog extends JPanel {

	private static final long serialVersionUID = 1L;
	protected JDialog dialog;
	private Map<String, List<String>> params;
	private List<String> routes;

	public WorkItemSelectDialog(Map<String, List<String>> params,
			List<String> routes, JFrame frame) {
		this.params = params;
		this.routes = routes;
		newDialog(frame);
	}

	public void newDialog(JFrame frame) {
		dialog = new CommonDialog(frame);
		dialog.setTitle("选择条件");
		dialog.setResizable(true);
		SwingUtil.setMiddle(dialog);
	}

	public Map<String, Object> showDialog() {
		Container container = dialog.getContentPane();
		container.add(new WorkItemSelectPanel(params, routes, dialog));
		dialog.setVisible(true);
		Map<String, Object> map = WorkItemSelectPanel.map;
		WorkItemSelectPanel.map = null;
		return map;

	}

}
