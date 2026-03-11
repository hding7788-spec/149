/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glaway.mpm.visual.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UnsupportedLookAndFeelException;

import wt.method.RemoteMethodServer;
import wt.part.WTPart;

import com.glaway.mpm.visual.util.VaSearchHelper;
import com.glaway.mpm.visual.view.ui.VaMainframe;
import com.glaway.mpm.visual.view.ui.VaPartDialog;

/**
 * 
 * @author hywang
 */
public class VaLauncher {

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
//		try {
			// System.out.println("远程登录准备");
			 RemoteMethodServer rms = RemoteMethodServer.getDefault();
			 rms.setUserName("wcadmin");
			 rms.setPassword("wcadmin");
			// System.out.println("远程登录成功");

//			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager
//					.getCrossPlatformLookAndFeelClassName());
//			LookUtils
//					.setLookAndTheme(new Plastic3DLookAndFeel(), new VaTheme());

//		} catch (ClassNotFoundException ex) {
//			Logger.getLogger(VaLauncher.class.getName()).log(Level.SEVERE,
//					null, ex);
//		} catch (InstantiationException ex) {
//			Logger.getLogger(VaLauncher.class.getName()).log(Level.SEVERE,
//					null, ex);
//		} catch (IllegalAccessException ex) {
//			Logger.getLogger(VaLauncher.class.getName()).log(Level.SEVERE,
//					null, ex);
//		} catch (UnsupportedLookAndFeelException ex) {
//			Logger.getLogger(VaLauncher.class.getName()).log(Level.SEVERE,
//					null, ex);
//		}

		java.awt.EventQueue.invokeLater(new Runnable() {

			

			public void run() {
//				VaMainframe frame = VaMainframe.getInstance();
				
				VaPartDialog frame = VaPartDialog.getInstance();

				frame.setTitle("可视化装配工具");
				frame.setName("VisualAssembler");

				Dimension screenSize = Toolkit.getDefaultToolkit()
						.getScreenSize();
				Dimension frameSize = new Dimension();

				if (frameSize.height > screenSize.height) {
					frameSize.height = screenSize.height;
				}

				if (frameSize.width > screenSize.width) {
					frameSize.width = screenSize.width;
				}

				frame.setLocation((screenSize.width - frameSize.width) / 4,
						(screenSize.height - frameSize.height) / 4);

				frame.setVisible(true);
			}
		});
	}

}
