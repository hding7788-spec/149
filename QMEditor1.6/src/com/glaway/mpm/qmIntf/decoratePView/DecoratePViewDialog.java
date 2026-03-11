package com.glaway.mpm.qmIntf.decoratePView;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import wt.method.RemoteMethodServer;

import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.visual.view.pview.VaPViewFactory;

public class DecoratePViewDialog {
	private static VaLogger logger = VaLogger.getLogger(DecoratePViewDialog.class);
	private static VaActionProgressBar animFrame = null;
	private JFrame dialog;
	private Map<String, String> map;

	public DecoratePViewDialog(Map<String, String> map) {
		logger.debug("map======" + map);
		this.map = map;
		Thread runThread = new Thread() {
			public void run() {
				newDialog();
				animFrame.finish();
				animFrame.dispose();
				dialog.setVisible(true);
			}
		};

		animFrame = new VaActionProgressBar(dialog, "加载组装工具", "加载组装工具",
				"正在加载组装工具，请稍候...");
		runThread.start();
		animFrame.setVisible(true);
	}

	public void newDialog() {

		dialog = new JFrame();
		dialog.setTitle("装配预览");
		dialog.setSize(1400, 750);
		dialog.setResizable(true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				VaPViewFactory.shutdown(VaPViewFactory.PV_NAME_DP);
			}
		});

		SwingUtil.setMiddle(dialog);
		// VaPViewFactory.shutdown(VaPViewFactory.PV_NAME_DP);
		Container container = dialog.getContentPane();
		container.setLayout(new BorderLayout());
//		container.add(new DecoratePViewPanel(map, dialog), BorderLayout.CENTER);
	}

	public String showDialog() {
//		Enumeration<DpStepNode> child = DpTreePanel.dpTree.getRoot().children();
//		while (child.hasMoreElements()) {
//			Enumeration<DpPaceNode> children = child.nextElement().children();
//			while (children.hasMoreElements()) {
//				DpPaceNode paceNode = children.nextElement();
//				for (TempObject tempObj : paceNode.getPace().getParts()) {
//					DpTreeMouseAdapter.setVaTreeNodeSelect(tempObj.getOid(),
//							tempObj.getOccId(), paceNode.isSelected());
//				}
//			}
//		}
//		VaMainPanel.jScrollPanel.getTree().revalidate();
//		VaMainPanel.jScrollPanel.getTree().repaint();
		return "";
	}

	public static void main(String[] args) {
		RemoteMethodServer.getDefault().setUserName("wcadmin");
		RemoteMethodServer.getDefault().setPassword("wcadmin");

		final Map<String, String> map = new HashMap<String, String>();
		map.put("oid", "886279");
		map.put("partNumber", "AL2_850_760");
		map.put("xmlPath",
				"C:\\Users\\ylshao\\Desktop\\AL2_850_760`波束选择板装配工艺`装配工艺`AL2_850_760`多基地面雷达.xml");
		// final DecoratePViewDialog d = new DecoratePViewDialog(map);
		JFrame frame = new JFrame();
		JButton btn = new JButton("启动");

		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				DecoratePViewDialog d = new DecoratePViewDialog(map);
				d.showDialog();
			}
		});
		JButton btn1 = new JButton("启动2");
		btn1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				DecoratePViewDialog d = new DecoratePViewDialog(map);
				d.showDialog();
			}
		});
		frame.setLayout(new BorderLayout());

		frame.add(btn);

		frame.add(btn1);

		frame.setSize(300, 400);
		SwingUtil.setMiddle(frame);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
