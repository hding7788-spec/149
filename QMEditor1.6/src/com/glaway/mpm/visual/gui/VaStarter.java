package com.glaway.mpm.visual.gui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import wt.method.RemoteMethodServer;

import com.glaway.mpm.visual.view.VaContext;
import com.glaway.mpm.visual.view.VaLauncher;
import com.glaway.mpm.visual.view.pview.VaPViewLiteDialog;
import com.glaway.mpm.visual.view.pview.VaPViewLiteUtil;
import com.glaway.mpm.visual.view.ui.VaConnectFrame;
import com.glaway.mpm.visual.view.ui.VaMainPanel;
import com.glaway.mpm.visual.view.ui.VaPartDialog;

public class VaStarter {

	private static boolean initFlag = false;

	public static void startAssembler(String partNumber, String authString) {

		VaContext.logonToServer(authString);

		startAssembler(partNumber);
	}

	public static void startAssembler(String partNumber, String username,
			String password) throws Exception {

		VaContext.logonToServer(username, password);

		startAssembler(partNumber);
	}

	public static void startAssembler(String partNumber) {

		final String tempPNum = partNumber.trim();

		if (!initFlag) {
			Thread VaRunner = new Thread(new Runnable() {

				@Override
				public void run() {
					VaContext.setCurrentPartNumber(tempPNum);
					VaPartDialog d = VaPartDialog.getInstance();
					d.setVisible(true);
				}
			});
			VaRunner.start();
		}
		else
		{
			VaPartDialog.getInstance().setVisible(true);
		}

		// if (VaContext.getConnected()) {
		// VaLauncher.main(new String[] {});
		// } else {
		// VaConnectFrame dc = new VaConnectFrame();
		//
		// dc.setVisible(true);
		// }

		// final JFrame frame = new JFrame();
		// JButton btn = new JButton();
		// btn.addActionListener(new ActionListener() {
		//
		// @Override
		// public void actionPerformed(ActionEvent e) {
		//
		// // VaPartDialog d = VaPartDialog.getInstance(frame);
		// // d.showPViewLite("sadsad", null);
		// JDialog dia = new JDialog();
		// dia.add(new VaMainPanel());
		// dia.setSize(800,600);
		// dia.setVisible(true);
		// // dia.add(comp)
		//
		// }
		// });
		// frame.add(btn);
		// // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setSize(80, 60);
		// frame.setVisible(true);

	}

	public static void main(String[] args) {
		RemoteMethodServer.getDefault().setUserName("wcadmin");
		RemoteMethodServer.getDefault().setPassword("wcadmin");
		// startAssembler("AL2_907_1460");
		startAssembler("AL2_907_1460");
	}
}
