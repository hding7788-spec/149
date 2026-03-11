package com.glaway.mpm.visual.view.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UnsupportedLookAndFeelException;

import wt.part.WTPart;
import wt.util.WTContext;
import wt.vc.views.View;

import com.glaway.mpm.util.LoadConfig;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.util.VaSearchHelper;
import com.glaway.mpm.visual.view.VaContext;
import com.glaway.mpm.visual.view.VaLauncher;
import com.glaway.mpm.visual.view.VaTheme;

public class VaConnectFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = -8390305415689370432L;

	private static final String CLASSNAME = VaConnectFrame.class.getName();
	private static final VaLogger log = VaLogger.getLogger(CLASSNAME);

	JTextField textUser = null;
	JPasswordField textPwd = null;
	JButton btnClose = null;
	JButton btnConfirm = null;

	public VaConnectFrame() {
		super("登录");
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int width = 300;
		int height = 160;
		int left = (screen.width - width) / 2;
		int top = (screen.height - height) / 2;
		setBounds(left, top, width, height);
		getContentPane().setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Color myColor = new Color(220, 220, 220);
		getContentPane().setBackground(myColor);
		JLabel lblUser = new JLabel("用户名称", JLabel.RIGHT);
		lblUser.setBounds(0, 15, 100, 20);
		getContentPane().add(lblUser);

		textUser = new JTextField("");
		textUser.setBounds(120, 15, 100, 30);
		getContentPane().add(textUser);

		JLabel lblPwd = new JLabel("用户密码", JLabel.RIGHT);
		lblPwd.setBounds(0, 50, 100, 20);
		getContentPane().add(lblPwd);

		textPwd = new JPasswordField("");
		textPwd.setBounds(120, 50, 100, 30);
		textPwd.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
			}

			public void keyTyped(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					btnConfirm.requestFocusInWindow();
					btnConfirm.doClick();
				}
			}
		});
		getContentPane().add(textPwd);

		// ---- Buttons OK and CLose
		btnConfirm = new JButton("确定");
		btnConfirm.setBounds(60, 90, 80, 30);
		btnConfirm.setEnabled(true);
		btnConfirm.setPreferredSize(new Dimension(100, 26));
		getContentPane().add(btnConfirm);
		btnConfirm.addActionListener(this);

		btnClose = new JButton("取消");
		btnClose.setBounds(160, 90, 80, 30);
		btnClose.setPreferredSize(new Dimension(100, 26));
		getContentPane().add(btnClose);
		btnClose.addActionListener(this);

		setResizable(false);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnClose) {
			System.exit(0);
		} else if (e.getSource() == btnConfirm) {
			String userName = textUser.getText();
			String password = new String(textPwd.getPassword());

			try {
				VaContext.logonToServer(userName, password);
			} catch (Exception exc) {
				log.error(exc);
			}
			if (!VaContext.getConnected())
				JOptionPane.showMessageDialog(this, "用户名称或密码错误", "登录异常", JOptionPane.WARNING_MESSAGE);
			else {
				this.setVisible(false);

				String ret = checkInput(this);
				if (ret == "cancel") {
					return;
				}
				VaContext.setCurrentPartNumber(ret);
				this.dispose();
				// if (VaContext.isJWS()) {
				log.debug("kicking off main frame....");
				startApplication(VaContext.getApplication());
				return;
				// }
			}
		}
	}

	private String checkInput(JFrame fra) {

		try {
			String partNumber = VaContext.getCurrentPartNumber();
			if (partNumber == "0" || partNumber == "") {
				partNumber = JOptionPane.showInputDialog(fra, "请输入整件编号");
			}
			if (partNumber == null) {
				return "cancel";
			} else if (partNumber.trim() == "") {
				JOptionPane.showMessageDialog(fra, "输入不能为空", "输入异常", JOptionPane.WARNING_MESSAGE);
				checkInput(fra);
			} else {
				View view = VaSearchHelper.getViewByName(LoadConfig.getInstance().getPbomView());
				WTPart part = VaSearchHelper.getPartByNumberAndView(partNumber, view.getPersistInfo().getObjectIdentifier()
						.getId());
				if (part == null) {
					JOptionPane.showMessageDialog(fra, "找不到该整件", "输入异常", JOptionPane.WARNING_MESSAGE);
					checkInput(fra);
				}
				return partNumber;
			}

		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	private static void startApplication(String application) {
		WTContext context = WTContext.getContext();

		VaContext.isEdit(!"false".equalsIgnoreCase(context.getParameter("isEdit")));
		String containerOidKey = context.getParameter("containerOidKey");
		if (containerOidKey != null) {
			VaContext.setContainerOid(Long.parseLong(containerOidKey));
		} else {
			String containerName = context.getParameter("containerName");
			if (containerName != null)
				VaContext.setContainerName(containerName);
		}

		VaLauncher.main(new String[] {});

	}

	public static void main(String[] args) {
		// try {
		// // LookUtils
		// // .setLookAndTheme(new Plastic3DLookAndFeel(), new VaTheme());
		// } catch (UnsupportedLookAndFeelException e) {
		// e.printStackTrace();
		// }

		WTContext wtcontext = WTContext.getContext();

		VaContext.setApplication(wtcontext.getParameter("application"));
		log.debug("wtcontext.getApplication(): " + VaContext.getApplication());
		for (String arg : args) {
			log.debug("wtcontext.getArgs(): " + arg);
		}

		String auth = wtcontext.getParameter("authorization");
		log.debug("wtcontext.auth(): " + auth);

		if (auth != null && auth.startsWith("Basic ")) {
			VaContext.logonToServer(auth.substring(6));
			if (VaContext.getConnected()) {
				startApplication(wtcontext.getParameter("application"));
			} else {
				VaConnectFrame dc = new VaConnectFrame();
				log.debug("in wrong init....");
				dc.setVisible(true);
			}
		} else {
			VaConnectFrame dc = new VaConnectFrame();
			log.debug("in right init....");
			dc.setVisible(true);
		}

	}
}